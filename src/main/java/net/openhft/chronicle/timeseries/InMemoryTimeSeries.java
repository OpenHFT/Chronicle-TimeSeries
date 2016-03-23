/*
 *
 *  *     Copyright (C) 2016  higherfrequencytrading.com
 *  *
 *  *     This program is free software: you can redistribute it and/or modify
 *  *     it under the terms of the GNU Lesser General Public License as published by
 *  *     the Free Software Foundation, either version 3 of the License.
 *  *
 *  *     This program is distributed in the hope that it will be useful,
 *  *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  *     GNU Lesser General Public License for more details.
 *  *
 *  *     You should have received a copy of the GNU Lesser General Public License
 *  *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package net.openhft.chronicle.timeseries;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by peter on 19/02/16.
 */
public class InMemoryTimeSeries implements TimeSeries {
    private final Map<String, ColumnCommon> columnMap = new LinkedHashMap<>();
    private final TimeSeries parent;
    private long length = 0;
    private long capacity = 1 << 20;

    public InMemoryTimeSeries(TimeSeries parent) {
        this.parent = parent;
    }

    @Override
    public void setLength(long size) {
        ensureCapacity(size);
        this.length = size;
    }

    @Override
    public void ensureCapacity(long capacity) {
        if (capacity > this.capacity) {
            for (ColumnCommon c : columnMap.values()) {
                c.ensureCapacity(capacity);
            }
        }
        this.capacity = capacity;
    }

    @Override
    public long addIndex(long timeStampMicros) {
        LongColumn ts = getTimestamp();
        ts.set(length, timeStampMicros);
        if (length + 1 >= capacity)
            ensureCapacity(length + (1 << 20));
        return length++;
    }

    @Override
    public List<String> getColumns() {
        List<String> columns = new ArrayList<>();
        if (parent != null)
            columns.addAll(parent.getColumns());
        columns.addAll(columnMap.keySet());
        return columns;
    }

    @Override
    public LongColumn getTimestamp() {
        return acquireLongColumn(TIMESTAMP);
    }

    @Override
    public LongColumn acquireLongColumn(String name, BytesLongLookup lookup) {
        return (LongColumn) columnMap.computeIfAbsent(name, (n) -> new InMemoryLongColumn(this, n, lookup, capacity));
    }

    @Override
    public LongColumn getLongColumn(String name) {
        return (LongColumn) columnMap.get(name);
    }

    @Override
    public DoubleColumn acquireDoubleColumn(String name, BytesDoubleLookup lookup) {
        return (DoubleColumn) columnMap.computeIfAbsent(name, (n) -> new InMemoryDoubleColumn(this, n, lookup, capacity));
    }

    @Override
    public DoubleColumn getDoubleColumn(String name) {
        return (DoubleColumn) columnMap.get(name);
    }

    @Override
    public <T> Column<T> acquireColumn(String name, Class<T> tClass) {
        return (Column<T>) columnMap.computeIfAbsent(name, (n) -> new InMemoryColumn<>(this, n, capacity));
    }

    @Override
    public <T> Column<T> getColumn(String name, Class<T> tClass) {
        return (Column<T>) columnMap.get(name);
    }

    @Override
    public long length() {
        return length;
    }

    @Override
    public DoubleColumn projectAs(String name, DoubleColumn source) {
        LongColumn ts = getTimestamp();
        LongColumn ts2 = source.timeSeries().getTimestamp();
        DoubleColumn result = acquireDoubleColumn(name, source.lookup());
        long i2 = 0, time2 = ts2.get(0);
        double v2 = source.get(0);
        for (long i = 0; i < length(); i++) {
            long time = ts.get(i);
            OUTER:
            if (time > time2) {
                do {
                    if (i2 + 1 >= ts2.length()) {
                        v2 = Double.NaN;
                        time2 = Long.MAX_VALUE;
                        break OUTER;
                    }
                    time2 = ts2.get(++i2);
                } while (time > time2);
                v2 = source.get(i2);
            }
            result.set(i, v2);
        }
        return result;
    }

    @Override
    public LongColumn projectAs(String name, LongColumn source) {
        LongColumn ts = getTimestamp();
        LongColumn ts2 = source.timeSeries().getTimestamp();
        LongColumn result = acquireLongColumn(name, source.lookup());
        long i2 = 0, time2 = ts2.get(0);
        long v2 = source.get(0);
        for (long i = 0; i < length(); i++) {
            long time = ts.get(i);
            OUTER:
            if (time > time2) {
                do {
                    if (i2 + 1 >= ts2.length()) {
                        v2 = Long.MIN_VALUE;
                        time2 = Long.MAX_VALUE;
                        break OUTER;
                    }
                    time2 = ts2.get(++i2);
                } while (time > time2);
                v2 = source.get(i2);
            }
            result.set(i, v2);
        }
        return result;
    }
}
