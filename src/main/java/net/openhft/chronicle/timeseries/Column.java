package net.openhft.chronicle.timeseries;

/**
 * Created by peter on 19/02/16.
 */
public interface Column<T> extends ColumnCommon {
    void set(long index, T t);

    T get(long index);
}
