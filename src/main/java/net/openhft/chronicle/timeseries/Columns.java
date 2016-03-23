/*
 *
 *  *     Copyright (C) 2016  higherfrequencytrading.com
 *  *
 *  *     This program is free software: you can redistribute it and/or modify
 *  *     it under the terms of the GNU Lesser General Public License as published by
 *  *     the Free Software Foundation, either version 3 of the License.
 *  *
 *  *     This program is distributed in the hope that it will be useful,
 *  *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  *     GNU Lesser General Public License for more details.
 *  *
 *  *     You should have received a copy of the GNU Lesser General Public License
 *  *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package net.openhft.chronicle.timeseries;

import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.bytes.NativeBytesStore;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.function.Supplier;

/**
 * Created by peter on 20/02/16.
 */
public enum Columns {
    ;

    static int CHUNK_SIZE_SQRT = 64;
    static int CHUNK_SIZE = CHUNK_SIZE_SQRT * CHUNK_SIZE_SQRT;

    public static void generateBrownian(DoubleColumn col, double start, double end, double sd) {
        long length = col.length();
        double sd2 = sd / Math.sqrt(length);
        NormalDistribution nd = new NormalDistribution(0, sd2 * CHUNK_SIZE);
        int trendLength = Math.toIntExact((length - 1) / CHUNK_SIZE + 2);
        BytesStore trend = NativeBytesStore.lazyNativeBytesStoreWithFixedCapacity(trendLength * 8L);
        double x = start;
        RandomGenerator rand = new MersenneTwister();
        for (int i = 0; i < trendLength - 1; i++) {
            float f = rand.nextFloat();
            trend.writeDouble((long) i << 3, x);
            x += nd.inverseCumulativeProbability(f);
        }
        trend.writeDouble((long) (trendLength - 1) << 3, x);
        double diff = end - x;
        double gradient = diff / (trendLength - 1);
        for (int i = 0; i < trendLength; i++) {
            double y = trend.addAndGetDoubleNotAtomic((long) i << 3, i * gradient);
//            System.out.println(i + ": "+y);
        }
        int procs = Runtime.getRuntime().availableProcessors();
        int chunksPerTask = (trendLength - 1) / procs + 1;
        ForkJoinPool fjp = ForkJoinPool.commonPool();
        List<ForkJoinTask> tasks = new ArrayList<>(procs);
        for (int i = 0; i < procs; i++) {
            int si = i * chunksPerTask;
            int ei = Math.min(trendLength, si + chunksPerTask);
            tasks.add(fjp.submit(() -> {
                NormalDistribution nd2 = new NormalDistribution(0, sd2);
                RandomGenerator rand2 = new MersenneTwister();
                for (int j = si; j < ei; j++) {
                    generateBrownian(col,
                            (long) j * CHUNK_SIZE,
                            trend.readDouble((long) j << 3),
                            trend.readDouble((long) (j + 1) << 3), nd2, rand2);
                }
            }));
        }
        for (ForkJoinTask task : tasks) {
            task.join();
        }
        trend.release();
    }

    private static void generateBrownian(DoubleColumn col, long first, double start, double end, NormalDistribution nd, RandomGenerator rand) {
        double x = start;
        int chunkSize = (int) Math.min(col.length() - first, CHUNK_SIZE);
        for (int i = 0; i < chunkSize; i++) {
            col.set(first + i, x);
            double p = rand.nextFloat() + 0.5 / (1 << 24);
            double v = nd.inverseCumulativeProbability(p);
            x += v;
            assert !Double.isInfinite(x);
        }
        double diff = end - x;
        double gradient = diff / chunkSize;
        for (int i = 0; i < chunkSize; i++) {
            col.add(first + i, i * gradient);
        }
    }

    public static <T> void setAll(LongColumn col, Supplier<T> perThread, LongColumnIndexObjectConsumer<T> consumer) {
        long length = col.length();
        int chunks = Math.toIntExact((length - 1) / CHUNK_SIZE + 1);
        ForkJoinPool fjp = ForkJoinPool.commonPool();
        int procs = Runtime.getRuntime().availableProcessors();
        List<ForkJoinTask> tasks = new ArrayList<>(procs);
        int chunksPerTask = (chunks - 1) / procs + 1;
        for (int i = 0; i < procs; i++) {
            int si = i * chunksPerTask;
            int ei = Math.min(chunks, si + chunksPerTask);
            tasks.add(fjp.submit(() -> {
                T t = perThread.get();
                long first = (long) si * CHUNK_SIZE;
                int max = (int) Math.min((ei - si) * CHUNK_SIZE, length - first);
                for (int j = 0; j < max; j++) {
                    consumer.apply(col, first + j, t);
                }
            }));
        }
        for (ForkJoinTask task : tasks) {
            task.join();
        }
    }
}
