package net.openhft.chronicle.timeseries;

import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.core.Maths;

/**
 * Created by peter on 19/02/16.
 */
public enum BytesDoubleLookups implements BytesDoubleLookup {
    FLOAT64 {
        @Override
        public double get(BytesStore bytes, long index) {
            return bytes.readDouble(index << 3);
        }

        @Override
        public void set(BytesStore bytes, long index, double value) {
            bytes.writeDouble(index << 3, value);
        }

        @Override
        public long sizeFor(long capacity) {
            return capacity << 3;
        }

        @Override
        public boolean supportNaN() {
            return true;
        }
    },
    FLOAT32 {
        @Override
        public double get(BytesStore bytes, long index) {
            return bytes.readFloat(index << 2);
        }

        @Override
        public void set(BytesStore bytes, long index, double value) {
            bytes.writeFloat(index << 2, (float) value);
        }

        @Override
        public long sizeFor(long capacity) {
            return capacity << 2;
        }

        @Override
        public boolean supportNaN() {
            return true;
        }
    },
    INT16_1 {
        final double factor = 1e1;

        @Override
        public double get(BytesStore bytes, long index) {
            short i = bytes.readShort(index << 1);
            return i == Short.MIN_VALUE ? Double.NaN : i / factor;
        }

        @Override
        public void set(BytesStore bytes, long index, double value) {
            short i = Double.isNaN(value) ? Short.MIN_VALUE : Maths.toInt16(Math.round(value * factor));
            bytes.writeShort(index << 1, i);
        }

        @Override
        public long sizeFor(long capacity) {
            return capacity << 1;
        }


        @Override
        public boolean supportNaN() {
            return true;
        }
    },
    INT16_2 {
        final double factor = 1e2;

        @Override
        public double get(BytesStore bytes, long index) {
            short i = bytes.readShort(index << 1);
            return i == Short.MIN_VALUE ? Double.NaN : i / factor;
        }

        @Override
        public void set(BytesStore bytes, long index, double value) {
            short i = Double.isNaN(value) ? Short.MIN_VALUE : Maths.toInt16(Math.round(value * factor));
            bytes.writeShort(index << 1, i);
        }

        @Override
        public long sizeFor(long capacity) {
            return capacity << 1;
        }


        @Override
        public boolean supportNaN() {
            return true;
        }
    },
    INT16_3 {
        final double factor = 1e3;

        @Override
        public double get(BytesStore bytes, long index) {
            short i = bytes.readShort(index << 1);
            return i == Short.MIN_VALUE ? Double.NaN : i / factor;
        }

        @Override
        public void set(BytesStore bytes, long index, double value) {
            short i = Double.isNaN(value) ? Short.MIN_VALUE : Maths.toInt16(Math.round(value * factor));
            bytes.writeShort(index << 1, i);
        }

        @Override
        public long sizeFor(long capacity) {
            return capacity << 1;
        }


        @Override
        public boolean supportNaN() {
            return true;
        }
    },
    INT16_4 {
        final double factor = 1e4;

        @Override
        public double get(BytesStore bytes, long index) {
            short i = bytes.readShort(index << 1);
            return i == Short.MIN_VALUE ? Double.NaN : i / factor;
        }

        @Override
        public void set(BytesStore bytes, long index, double value) {
            short i = Double.isNaN(value) ? Short.MIN_VALUE : Maths.toInt16(Math.round(value * factor));
            bytes.writeShort(index << 1, i);
        }

        @Override
        public long sizeFor(long capacity) {
            return capacity << 1;
        }

        @Override
        public boolean supportNaN() {
            return true;
        }
    },
    INT32_1 {
        final double factor = 1e1;

        @Override
        public double get(BytesStore bytes, long index) {
            int i = bytes.readInt(index << 2);
            return i == Integer.MIN_VALUE ? Double.NaN : i / factor;
        }

        @Override
        public void set(BytesStore bytes, long index, double value) {
            int i = Double.isNaN(value) ? Integer.MIN_VALUE : Math.toIntExact(Math.round(value * factor));
            bytes.writeInt(index << 2, i);
        }

        @Override
        public long sizeFor(long capacity) {
            return capacity << 2;
        }

        @Override
        public boolean supportNaN() {
            return true;
        }
    },
    INT32_2 {
        final double factor = 1e2;

        @Override
        public double get(BytesStore bytes, long index) {
            int i = bytes.readInt(index << 2);
            return i == Integer.MIN_VALUE ? Double.NaN : i / factor;
        }

        @Override
        public void set(BytesStore bytes, long index, double value) {
            int i = Double.isNaN(value) ? Integer.MIN_VALUE : Math.toIntExact(Math.round(value * factor));
            bytes.writeInt(index << 2, i);
        }

        @Override
        public long sizeFor(long capacity) {
            return capacity << 2;
        }

        @Override
        public boolean supportNaN() {
            return true;
        }
    },
    INT32_3 {
        final double factor = 1e3;

        @Override
        public double get(BytesStore bytes, long index) {
            int i = bytes.readInt(index << 2);
            return i == Integer.MIN_VALUE ? Double.NaN : i / factor;
        }

        @Override
        public void set(BytesStore bytes, long index, double value) {
            int i = Double.isNaN(value) ? Integer.MIN_VALUE : Math.toIntExact(Math.round(value * factor));
            bytes.writeInt(index << 2, i);
        }

        @Override
        public long sizeFor(long capacity) {
            return capacity << 2;
        }

        @Override
        public boolean supportNaN() {
            return true;
        }
    },
    INT32_4 {
        final double factor = 1e4;

        @Override
        public double get(BytesStore bytes, long index) {
            int i = bytes.readInt(index << 2);
            return i == Integer.MIN_VALUE ? Double.NaN : i / factor;
        }

        @Override
        public void set(BytesStore bytes, long index, double value) {
            int i = Double.isNaN(value) ? Integer.MIN_VALUE : Math.toIntExact(Math.round(value * factor));
            bytes.writeInt(index << 2, i);
        }

        @Override
        public long sizeFor(long capacity) {
            return capacity << 2;
        }

        @Override
        public boolean supportNaN() {
            return true;
        }
    },
    INT32_6 {
        final double factor = 1e6;

        @Override
        public double get(BytesStore bytes, long index) {
            int i = bytes.readInt(index << 2);
            return i == Integer.MIN_VALUE ? Double.NaN : i / factor;
        }

        @Override
        public void set(BytesStore bytes, long index, double value) {
            int i = Double.isNaN(value) ? Integer.MIN_VALUE : Math.toIntExact(Math.round(value * factor));
            bytes.writeInt(index << 2, i);
        }

        @Override
        public long sizeFor(long capacity) {
            return capacity << 2;
        }

        @Override
        public boolean supportNaN() {
            return true;
        }
    }
}
