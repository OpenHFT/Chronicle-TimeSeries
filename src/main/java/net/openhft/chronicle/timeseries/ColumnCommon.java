package net.openhft.chronicle.timeseries;

/**
 * Created by peter on 19/02/16.
 */
public interface ColumnCommon {
    void ensureCapacity(long capacity);

    TimeSeries timeSeries();

    String name();

    default long length() {
        return timeSeries().length();
    }

    boolean supportsNaN();
}
