package net.openhft.chronicle.timeseries;

/**
 * Created by peter on 20/02/16.
 */
public abstract class AbstractColumn implements ColumnCommon {
    private final TimeSeries timeSeries;
    private final String name;

    protected AbstractColumn(TimeSeries timeSeries, String name) {
        this.timeSeries = timeSeries;
        this.name = name;
    }

    @Override
    public TimeSeries timeSeries() {
        return timeSeries;
    }

    @Override
    public String name() {
        return name;
    }
}
