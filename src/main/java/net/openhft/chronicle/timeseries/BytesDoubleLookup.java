package net.openhft.chronicle.timeseries;

import net.openhft.chronicle.bytes.BytesStore;

/**
 * Created by peter on 19/02/16.
 */
public interface BytesDoubleLookup {
    double get(BytesStore bytes, long index);

    void set(BytesStore bytes, long index, double value);

    long sizeFor(long capacity);

    default double add(BytesStore bytes, long index, double value) {
        double x = get(bytes, index);
        x += value;
        set(bytes, index, x);
        return x;
    }

    default boolean supportNaN() {
        return false;
    }
}
