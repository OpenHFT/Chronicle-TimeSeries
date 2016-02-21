package net.openhft.chronicle.timeseries;

import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.core.Maths;

/**
 * Created by peter on 19/02/16.
 */
public enum BytesLongLookups implements BytesLongLookup {
    INT64 {
        @Override
        public long get(BytesStore bytes, long index) {
            return bytes.readLong(index << 3);
        }

        @Override
        public void set(BytesStore bytes, long index, long value) {
            bytes.writeLong(index << 3, value);
        }

        @Override
        public long sizeFor(long capacity) {
            return capacity << 3;
        }

        @Override
        public boolean supportsNaN() {
            return true;
        }
    },
    INT32 {
        @Override
        public long get(BytesStore bytes, long index) {
            int i = bytes.readInt(index << 2);
            return i == Integer.MIN_VALUE ? TimeSeries.LONG_NAN : i;
        }

        @Override
        public void set(BytesStore bytes, long index, long value) {
            int i = value == TimeSeries.LONG_NAN ? Integer.MIN_VALUE : Math.toIntExact(value);
            bytes.writeInt(index << 2, i);
        }

        @Override
        public long sizeFor(long capacity) {
            return capacity << 2;
        }

        @Override
        public boolean supportsNaN() {
            return true;
        }
    },
    INT16 {
        @Override
        public long get(BytesStore bytes, long index) {
            short i = bytes.readShort(index << 1);
            return i == Short.MIN_VALUE ? Long.MIN_VALUE : i;
        }

        @Override
        public void set(BytesStore bytes, long index, long value) {
            short i = value == Long.MIN_VALUE ? Short.MIN_VALUE : Maths.toInt16(value);
            bytes.writeShort(index << 1, i);
        }

        @Override
        public long sizeFor(long capacity) {
            return capacity << 1;
        }

        @Override
        public boolean supportsNaN() {
            return true;
        }
    },
    INT8 {
        @Override
        public long get(BytesStore bytes, long index) {
            return bytes.readByte(index);
        }

        @Override
        public void set(BytesStore bytes, long index, long value) {
            bytes.writeByte(index, Maths.toInt8(value));
        }

        @Override
        public long sizeFor(long capacity) {
            return capacity;
        }
    },
    UINT8 {
        @Override
        public long get(BytesStore bytes, long index) {
            return bytes.readUnsignedByte(index);
        }

        @Override
        public void set(BytesStore bytes, long index, long value) {
            bytes.writeByte(index, Maths.toUInt8(value));
        }

        @Override
        public long sizeFor(long capacity) {
            return capacity;
        }
    },
    UINT4 {
        @Override
        public long get(BytesStore bytes, long index) {
            int i = bytes.readUnsignedByte(index >> 1);
            return (index & 1) != 0 ? (i >> 4) : (i & 0xF);
        }

        @Override
        public void set(BytesStore bytes, long index, long value) {
            int i = bytes.readUnsignedByte(index >> 1);
            int i2 = (int) ((index & 1) != 0 ? (i & 0xF0) | (value & 0xF) : (i & 0xF) | ((value & 0xf) << 4));
            bytes.writeByte(index, Maths.toUInt8(value));
        }

        @Override
        public long sizeFor(long capacity) {
            return (capacity + 1) >> 1;
        }
    }
}
