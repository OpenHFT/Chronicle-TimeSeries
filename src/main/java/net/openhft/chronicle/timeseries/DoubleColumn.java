package net.openhft.chronicle.timeseries;

import java.util.DoubleSummaryStatistics;

/**
 * Created by peter on 19/02/16.
 */
public interface DoubleColumn extends ColumnCommon {
    void set(long index, double value);

    double get(long index);

    double add(long index, double v);

    void generateBrownian(double start, double end, double sd);

    DoubleSummaryStatistics summaryStatistics();

    BytesDoubleLookup lookup();
}
