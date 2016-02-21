package net.openhft.chronicle.timeseries;

import net.openhft.chronicle.bytes.BytesStore;

/**
 * Created by peter on 19/02/16.
 */
public interface BytesLongLookup {
    long get(BytesStore bytes, long index);

    void set(BytesStore bytes, long index, long value);

    long sizeFor(long capacity);

    default boolean supportsNaN() {
        return false;
    }
}
