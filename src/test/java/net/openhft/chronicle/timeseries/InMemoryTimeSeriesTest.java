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

import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

/**
 * Created by peter on 19/02/16.
 */
public class InMemoryTimeSeriesTest {
    static float sqr(float f) {
        return f * f;
    }

    @Test
    public void testBidAsk() throws ExecutionException, InterruptedException {
        TimeSeries ts = new InMemoryTimeSeries(null);
        long size = 10_000_000_000L;
        ts.ensureCapacity(size);
        LongColumn time = ts.acquireLongColumn("ts", BytesLongLookups.INT64);
        DoubleColumn bid = ts.acquireDoubleColumn("bid", BytesDoubleLookups.INT16_4);
        DoubleColumn ask = ts.acquireDoubleColumn("ask", BytesDoubleLookups.INT16_4);

        int threads = Runtime.getRuntime().availableProcessors() * 2;
        long start = System.currentTimeMillis();
        long block = (((((size + threads - 1) / threads) - 1) | 63) + 1);

        List<ForkJoinTask<?>> tasks = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            final int finalI = i;
            tasks.add(ForkJoinPool.commonPool().submit(() -> {
                Random rand1 = new Random();
                Random rand2 = new Random();
                long first = finalI * block;
                for (int j = 0, max = (int) Math.min(size - first, block); j < max; j++) {
                    long v = first + j;
                    time.set(v, v);
                    int r1 = rand1.nextInt(1000);
                    int r2 = rand2.nextInt(1000);
                    bid.set(v, Math.min(r1, r2) / 1e3);
                    ask.set(v, Math.max(r1, r2) / 1e3);
                }
            }));
        }
        for (ForkJoinTask<?> task : tasks) {
            task.get();
        }
        long took = System.currentTimeMillis() - start;
        System.out.printf("%d threads took %.3f secs%n", threads, took / 1e3);
    }

    @Test
    public void testGenerateBrownian() {
        TimeSeries ts = new InMemoryTimeSeries(null);
        long size = 4L << 30;
        ts.setLength(size);

        long start = System.currentTimeMillis();
        DoubleColumn mid = ts.acquireDoubleColumn("mid", BytesDoubleLookups.INT16_4);
        mid.generateBrownian(1, 2, 0.001);

        DoubleColumn spread = ts.acquireDoubleColumn("spread", BytesDoubleLookups.INT16_4);
        spread.generateBrownian(0.001, 0.001, 0.0001);
        long took = System.currentTimeMillis() - start;

        System.out.printf("generateBrownian took %.3f secs%n", took / 1e3);
    }

    @Test
    @Ignore("TODO FIX")
    public void testGenerateRandomSequence() {
        long size = 600_000_000L;

        // generate series 1
        TimeSeries ts = new InMemoryTimeSeries(null);
        ts.setLength(size);

        LongColumn time = ts.getTimestamp();
        time.setAll(Random::new, (c, i, r) -> c.set(i, 9 + (int) Math.pow(1e6, sqr(r.nextFloat()))));
        long sum = time.integrate(); // sum all the intervals

        System.out.printf("%.1f days%n", sum / 86400e6);

        DoubleColumn mid = ts.acquireDoubleColumn("mid", BytesDoubleLookups.INT16_4);
        mid.generateBrownian(1, 2, 0.0005);

        // generate series 2
        TimeSeries ts2 = new InMemoryTimeSeries(null);
        ts2.setLength(size);

        LongColumn time2 = ts.getTimestamp();
        time2.setAll(Random::new, (c, i, r) -> c.set(i, 9 + (int) Math.pow(1e6, sqr(r.nextFloat()))));
        long sum2 = time2.integrate(); // sum all the intervals

        System.out.printf("%.1f days%n", sum2 / 86400e6);

        DoubleColumn mid2 = ts2.acquireDoubleColumn("mid", BytesDoubleLookups.INT16_4);
        mid2.generateBrownian(1, 2, 0.0005);

        // compare the correlation
//    CorrelationStatistic stats = PearsonsCorrelation.calcCorrelation(mid, mid2, Mode.AFTER_BOTH_CHANGE);
    }
}
