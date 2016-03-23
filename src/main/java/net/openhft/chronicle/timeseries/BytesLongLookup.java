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
