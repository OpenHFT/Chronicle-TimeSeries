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

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.bytes.NativeBytesStore;
import net.openhft.chronicle.core.Jvm;

import java.nio.ByteBuffer;
import java.util.function.Supplier;

/**
 * Created by peter on 19/02/16.
 */
public class InMemoryLongColumn extends AbstractColumn implements LongColumn {
    private final BytesLongLookup lookup;
    private BytesStore bytes;

    public InMemoryLongColumn(TimeSeries timeSeries, String name, BytesLongLookup lookup, long capacity) {
        super(timeSeries, name);
        this.lookup = lookup;
        long value = lookup.sizeFor(capacity);
        this.bytes = Jvm.isDebug()
                ? Bytes.wrapForRead(ByteBuffer.allocateDirect(Math.toIntExact(value)))
                : NativeBytesStore.lazyNativeBytesStoreWithFixedCapacity(value);
    }

    @Override
    public void ensureCapacity(long capacity) {
        long cap = lookup.sizeFor(capacity);
        if (cap > bytes.realCapacity()) {
            long value = lookup.sizeFor(capacity);
            BytesStore bytes2 = Jvm.isDebug()
                    ? Bytes.wrapForRead(ByteBuffer.allocateDirect(Math.toIntExact(value)))
                    : NativeBytesStore.lazyNativeBytesStoreWithFixedCapacity(value);
            bytes2.write(0, bytes);
            bytes.release();
            bytes = bytes2;
        }
    }

    @Override
    public void set(long index, long value) {
        lookup.set(bytes, index, value);
    }

    @Override
    public long get(long index) {
        return lookup.get(bytes, index);
    }

    @Override
    public boolean supportsNaN() {
        return lookup.supportsNaN();
    }

    @Override
    public <T> void setAll(Supplier<T> perThread, LongColumnIndexObjectConsumer<T> consumer) {
        Columns.setAll(this, perThread, consumer);
    }

    @Override
    public long integrate() {
        long sum = 0;
        for (long i = 0; i < length(); i++) {
            long v = get(i);
            sum += v;
            set(i, v);
        }
        return sum;
    }

    @Override
    public BytesLongLookup lookup() {
        return lookup;
    }
}
