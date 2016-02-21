package net.openhft.chronicle.timeseries;

import java.util.List;

/**
 * Created by peter on 19/02/16.
 */
public interface TimeSeries {
    long LONG_NAN = Long.MIN_VALUE;
    String TIMESTAMP = "time";

    void setLength(long size);

    void ensureCapacity(long capacity);

    long addIndex(long timeStampMicros);

    List<String> getColumns();

    default LongColumn acquireLongColumn(String name) {
        return acquireLongColumn(name, BytesLongLookups.INT64);
    }

    LongColumn acquireLongColumn(String name, BytesLongLookup lookup);

    LongColumn getLongColumn(String name);

    default DoubleColumn acquireDoubleColumn(String name) {
        return acquireDoubleColumn(name, BytesDoubleLookups.FLOAT64);
    }

    DoubleColumn acquireDoubleColumn(String name, BytesDoubleLookup lookup);

    DoubleColumn getDoubleColumn(String name);

    <T> Column<T> acquireColumn(String name, Class<T> tClass);

    <T> Column<T> getColumn(String name, Class<T> tClass);

    long length();

    DoubleColumn projectAs(String name, DoubleColumn source);

    LongColumn projectAs(String name, LongColumn source);

    LongColumn getTimestamp();
}
