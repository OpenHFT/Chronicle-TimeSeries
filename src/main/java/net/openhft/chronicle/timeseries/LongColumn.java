package net.openhft.chronicle.timeseries;

import java.util.function.Supplier;

/**
 * Created by peter on 19/02/16.
 */
public interface LongColumn extends ColumnCommon {
    void set(long index, long value);

    long get(long index);

    BytesLongLookup lookup();

    <T> void setAll(Supplier<T> perThread, LongColumnIndexObjectConsumer<T> consumer);

    long integrate();
}
