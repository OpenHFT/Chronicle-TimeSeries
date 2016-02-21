package net.openhft.chronicle.timeseries;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesStore;

import java.nio.ByteBuffer;
import java.util.DoubleSummaryStatistics;

/**
 * Created by peter on 19/02/16.
 */
public class InMemoryDoubleColumn extends AbstractColumn implements DoubleColumn {
    private final BytesDoubleLookup lookup;
    private BytesStore bytes;

    public InMemoryDoubleColumn(TimeSeries timeSeries, String name, BytesDoubleLookup lookup, long capacity) {
        super(timeSeries, name);
        this.lookup = lookup;
//        this.bytes = NativeBytesStore.lazyNativeBytesStoreWithFixedCapacity(lookup.sizeFor(capacity));
        this.bytes = Bytes.wrapForRead(ByteBuffer.allocateDirect(Math.toIntExact(lookup.sizeFor(capacity))));
    }

    @Override
    public void ensureCapacity(long capacity) {
        long cap = lookup.sizeFor(capacity);
        if (cap > bytes.realCapacity()) {
//            BytesStore bytes2 = NativeBytesStore.lazyNativeBytesStoreWithFixedCapacity(Maths.divideRoundUp(cap, OS.pageSize()));
            BytesStore bytes2 = Bytes.wrapForRead(ByteBuffer.allocateDirect(Math.toIntExact(lookup.sizeFor(capacity))));
            bytes2.write(0, bytes);
            bytes.release();
            bytes = bytes2;
        }
    }

    @Override
    public void set(long index, double value) {
        if (index < 0 || index > bytes.realCapacity())
            throw new AssertionError("index: " + index);
        lookup.set(bytes, index, value);
    }

    @Override
    public double get(long index) {
        return lookup.get(bytes, index);
    }

    public double add(long index, double value) {
        return lookup.add(bytes, index, value);
    }

    @Override
    public void generateBrownian(double start, double end, double sd) {
        Columns.generateBrownian(this, start, end, sd);
    }

    @Override
    public DoubleSummaryStatistics summaryStatistics() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean supportsNaN() {
        return lookup.supportNaN();
    }

    @Override
    public BytesDoubleLookup lookup() {
        return lookup;
    }
}
