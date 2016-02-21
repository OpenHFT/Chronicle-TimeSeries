package net.openhft.chronicle.timeseries;

/**
 * Created by peter on 21/02/16.
 */
@FunctionalInterface
public interface LongColumnIndexObjectConsumer<T> {
    void apply(LongColumn col, long index, T t);
}
