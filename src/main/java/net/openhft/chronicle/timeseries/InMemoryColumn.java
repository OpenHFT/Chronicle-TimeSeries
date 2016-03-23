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

/**
 * Created by peter on 19/02/16.
 */
public class InMemoryColumn<T> extends AbstractColumn implements Column<T> {
    private final ArrayList<T> list = new ArrayList<>();

    public InMemoryColumn(TimeSeries timeSeries, String name, long capacity) {
        super(timeSeries, name);

        ensureCapacity(capacity);
    }

    @Override
    public void set(long index, T t) {
        int index0 = Math.toIntExact(index);
        while (list.size() <= index0)
            list.add(null);
        list.set(index0, t);
    }

    @Override
    public T get(long index) {
        return list.get(Math.toIntExact(index));
    }

    @Override
    public void ensureCapacity(long capacity) {
        list.ensureCapacity(Math.toIntExact(capacity));
        while (list.size() <= capacity)
            list.add(null);
    }

    @Override
    public boolean supportsNaN() {
        return false;
    }
}
