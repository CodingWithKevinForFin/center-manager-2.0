package com.f1.ami.center.hdb.col;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.f1.ami.center.table.AmiTable;
import com.f1.base.Bytes;
import com.f1.base.Complex;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.base.UUID;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;
import com.f1.utils.IOH;
import com.f1.utils.casters.Caster_BigDecimal;
import com.f1.utils.casters.Caster_BigInteger;
import com.f1.utils.casters.Caster_Byte;
import com.f1.utils.casters.Caster_Bytes;
import com.f1.utils.casters.Caster_Character;
import com.f1.utils.casters.Caster_Complex;
import com.f1.utils.casters.Caster_DateMillis;
import com.f1.utils.casters.Caster_DateNanos;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Float;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.casters.Caster_Short;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.casters.Caster_UUID;

public class AmiHdbMarshallers {
	public static final AmiHdbMarshallerPrimitive<Byte> BYTE_INSTANCE = new BYTE();
	public static final AmiHdbMarshallerPrimitive<Short> SHORT_INSTANCE = new SHORT();
	public static final AmiHdbMarshallerPrimitive<Integer> INT_INSTANCE = new INT();
	public static final AmiHdbMarshallerPrimitive<Long> LONG_INSTANCE = new LONG();
	public static final AmiHdbMarshallerPrimitive<Float> FLOAT_INSTANCE = new FLOAT();
	public static final AmiHdbMarshallerPrimitive<Double> DOUBLE_INSTANCE = new DOUBLE();
	public static final AmiHdbMarshallerPrimitive<Character> CHAR_INSTANCE = new CHAR();
	public static final AmiHdbMarshallerPrimitive<DateMillis> UTC_INSTANCE = new UTC();
	public static final AmiHdbMarshallerPrimitive<DateNanos> UTCN_INSTANCE = new UTCN();
	public static final AmiHdbMarshallerFixedSize<UUID> UUID_INSTANCE = new UUID_();
	public static final AmiHdbMarshallerFixedSize<Complex> COMPLEX_INSTANCE = new COMPLEX();
	public static final AmiHdbMarshallerVarSize<String> STRING_INSTANCE = new STRING();
	public static final AmiHdbMarshallerVarSize<Bytes> BINARY_INSTANCE = new BINARY();
	public static final AmiHdbMarshallerVarSize<BigInteger> BIGINT_INSTANCE = new BIGINT();
	public static final AmiHdbMarshallerVarSize<BigDecimal> BIGDEC_INSTANCE = new BIGDEC();

	public static AmiHdbMarshaller<?> getMarshaller(byte type) {
		switch (type) {
			case AmiTable.TYPE_DOUBLE:
				return AmiHdbMarshallers.DOUBLE_INSTANCE;
			case AmiTable.TYPE_FLOAT:
				return AmiHdbMarshallers.FLOAT_INSTANCE;
			case AmiTable.TYPE_BYTE:
				return AmiHdbMarshallers.BYTE_INSTANCE;
			case AmiTable.TYPE_SHORT:
				return AmiHdbMarshallers.SHORT_INSTANCE;
			case AmiTable.TYPE_INT:
				return AmiHdbMarshallers.INT_INSTANCE;
			case AmiTable.TYPE_LONG:
				return AmiHdbMarshallers.LONG_INSTANCE;
			case AmiTable.TYPE_CHAR:
				return AmiHdbMarshallers.CHAR_INSTANCE;
			//			case AmiTable.TYPE_BOOLEAN:
			//				return AmiHdbMarshallers.BOOLEAN_INSTANCE;
			case AmiTable.TYPE_UUID:
				return AmiHdbMarshallers.UUID_INSTANCE;
			case AmiTable.TYPE_UTC:
				return AmiHdbMarshallers.UTC_INSTANCE;
			case AmiTable.TYPE_UTCN:
				return AmiHdbMarshallers.UTCN_INSTANCE;
			case AmiTable.TYPE_COMPLEX:
				return AmiHdbMarshallers.COMPLEX_INSTANCE;
			case AmiTable.TYPE_BINARY:
				return AmiHdbMarshallers.BINARY_INSTANCE;
			case AmiTable.TYPE_BIGINT:
				return AmiHdbMarshallers.BIGINT_INSTANCE;
			case AmiTable.TYPE_BIGDEC:
				return AmiHdbMarshallers.BIGDEC_INSTANCE;
			case AmiTable.TYPE_STRING:
				return AmiHdbMarshallers.STRING_INSTANCE;
		}
		return null;
	}

	final public static class BYTE implements AmiHdbMarshallerPrimitive<Byte> {
		private static final Byte MIN = Byte.MIN_VALUE;

		@Override
		public void write(FastDataOutput out, Byte v) throws IOException {
			out.writeByte(v);
		}

		@Override
		public Byte cast(Object o) {
			return Caster_Byte.INSTANCE.castNoThrow(o);
		}

		@Override
		public Byte minValue() {
			return MIN;
		}

		@Override
		public int getFixedSize() {
			return 1;
		}

		@Override
		public Byte read(FastDataInput input) throws IOException {
			return input.readByte();
		}

		@Override
		public byte getType() {
			return AmiTable.TYPE_BYTE;
		}

		@Override
		public int getSize(Byte value) {
			return getFixedSize();
		}

		@Override
		public void writePrimitiveLong(FastDataOutput out, long v) throws IOException {
			out.writeByte((byte) v);
		}

		@Override
		public void writePrimitiveDouble(FastDataOutput out, double v) throws IOException {
			out.writeByte((byte) v);
		}

		@Override
		public void writeMinValue(FastDataOutput out) throws IOException {
			out.writeByte(MIN);
		}

		@Override
		public boolean isMin(long v) {
			return (byte) v == MIN;
		}

		@Override
		public boolean isMin(double v) {
			return (byte) v == MIN;
		}
	}

	final public static class SHORT implements AmiHdbMarshallerPrimitive<Short> {
		private static final Short MIN = Short.MIN_VALUE;

		@Override
		public void write(FastDataOutput out, Short v) throws IOException {
			out.writeShort(v);
		}

		@Override
		public Short cast(Object o) {
			return Caster_Short.INSTANCE.castNoThrow(o);
		}

		@Override
		public Short minValue() {
			return MIN;
		}

		@Override
		public int getFixedSize() {
			return 2;
		}

		@Override
		public Short read(FastDataInput input) throws IOException {
			return input.readShort();
		}
		@Override
		public byte getType() {
			return AmiTable.TYPE_SHORT;
		}
		@Override
		public int getSize(Short value) {
			return getFixedSize();
		}
		@Override
		public void writePrimitiveLong(FastDataOutput out, long v) throws IOException {
			out.writeShort((short) v);
		}

		@Override
		public void writePrimitiveDouble(FastDataOutput out, double v) throws IOException {
			out.writeShort((short) v);
		}

		@Override
		public void writeMinValue(FastDataOutput out) throws IOException {
			out.writeShort(MIN);
		}

		@Override
		public boolean isMin(long v) {
			return (short) v == MIN;
		}

		@Override
		public boolean isMin(double v) {
			return (short) v == MIN;
		}
	}

	final public static class INT implements AmiHdbMarshallerPrimitive<Integer> {
		private static final Integer MIN = Integer.MIN_VALUE;

		@Override
		public void write(FastDataOutput out, Integer v) throws IOException {
			out.writeInt(v);
		}

		@Override
		public Integer cast(Object o) {
			return Caster_Integer.INSTANCE.castNoThrow(o);
		}

		@Override
		public Integer minValue() {
			return MIN;
		}

		@Override
		public int getFixedSize() {
			return 4;
		}

		@Override
		public Integer read(FastDataInput input) throws IOException {
			return input.readInt();
		}
		@Override
		public byte getType() {
			return AmiTable.TYPE_INT;
		}
		@Override
		public int getSize(Integer value) {
			return getFixedSize();
		}
		@Override
		public void writePrimitiveLong(FastDataOutput out, long v) throws IOException {
			out.writeInt((int) v);
		}

		@Override
		public void writePrimitiveDouble(FastDataOutput out, double v) throws IOException {
			out.writeInt((int) v);
		}

		@Override
		public void writeMinValue(FastDataOutput out) throws IOException {
			out.writeInt(MIN);
		}

		@Override
		public boolean isMin(long v) {
			return (int) v == MIN;
		}

		@Override
		public boolean isMin(double v) {
			return (int) v == MIN;
		}
	}

	final public static class LONG implements AmiHdbMarshallerPrimitive<Long> {
		private static final Long MIN = Long.MIN_VALUE;

		@Override
		public void write(FastDataOutput out, Long v) throws IOException {
			out.writeLong(v);
		}

		@Override
		public Long cast(Object o) {
			return Caster_Long.INSTANCE.castNoThrow(o);
		}

		@Override
		public Long minValue() {
			return MIN;
		}

		@Override
		public int getFixedSize() {
			return 8;
		}

		@Override
		public Long read(FastDataInput input) throws IOException {
			return input.readLong();
		}
		@Override
		public byte getType() {
			return AmiTable.TYPE_LONG;
		}
		@Override
		public int getSize(Long value) {
			return getFixedSize();
		}
		@Override
		public void writePrimitiveLong(FastDataOutput out, long v) throws IOException {
			out.writeLong(v);
		}

		@Override
		public void writePrimitiveDouble(FastDataOutput out, double v) throws IOException {
			out.writeLong((long) v);
		}

		@Override
		public void writeMinValue(FastDataOutput out) throws IOException {
			out.writeLong(MIN);
		}

		@Override
		public boolean isMin(long v) {
			return v == MIN;
		}

		@Override
		public boolean isMin(double v) {
			return (long) v == MIN;
		}
	}

	final public static class FLOAT implements AmiHdbMarshallerPrimitive<Float> {
		private static final Float MIN = Float.MIN_VALUE;

		@Override
		public void write(FastDataOutput out, Float v) throws IOException {
			out.writeFloat(v);
		}

		@Override
		public Float cast(Object o) {
			return Caster_Float.INSTANCE.castNoThrow(o);
		}

		@Override
		public Float minValue() {
			return MIN;
		}

		@Override
		public int getFixedSize() {
			return 4;
		}

		@Override
		public Float read(FastDataInput input) throws IOException {
			return input.readFloat();
		}
		@Override
		public byte getType() {
			return AmiTable.TYPE_FLOAT;
		}
		@Override
		public int getSize(Float value) {
			return getFixedSize();
		}
		@Override
		public void writePrimitiveLong(FastDataOutput out, long v) throws IOException {
			out.writeFloat((float) v);
		}

		@Override
		public void writePrimitiveDouble(FastDataOutput out, double v) throws IOException {
			out.writeFloat((float) v);
		}

		@Override
		public void writeMinValue(FastDataOutput out) throws IOException {
			out.writeFloat(MIN);
		}

		@Override
		public boolean isMin(long v) {
			return (float) v == MIN;
		}

		@Override
		public boolean isMin(double v) {
			return (float) v == MIN;
		}
	}

	final public static class DOUBLE implements AmiHdbMarshallerPrimitive<Double> {
		private static final Double MIN = Double.NEGATIVE_INFINITY;

		@Override
		public void write(FastDataOutput out, Double v) throws IOException {
			out.writeDouble(v);
		}

		@Override
		public Double cast(Object o) {
			return Caster_Double.INSTANCE.castNoThrow(o);
		}

		@Override
		public Double minValue() {
			return MIN;
		}

		@Override
		public int getFixedSize() {
			return 8;
		}

		@Override
		public Double read(FastDataInput input) throws IOException {
			return input.readDouble();
		}
		@Override
		public byte getType() {
			return AmiTable.TYPE_DOUBLE;
		}
		@Override
		public int getSize(Double value) {
			return getFixedSize();
		}
		@Override
		public void writePrimitiveLong(FastDataOutput out, long v) throws IOException {
			out.writeDouble((double) v);
		}

		@Override
		public void writePrimitiveDouble(FastDataOutput out, double v) throws IOException {
			out.writeDouble((double) v);
		}

		@Override
		public void writeMinValue(FastDataOutput out) throws IOException {
			out.writeDouble(MIN);
		}

		@Override
		public boolean isMin(long v) {
			return (double) v == MIN;
		}

		@Override
		public boolean isMin(double v) {
			return (double) v == MIN;
		}
	}

	final public static class CHAR implements AmiHdbMarshallerPrimitive<Character> {
		private static final Character MIN = Character.MIN_VALUE;

		@Override
		public void write(FastDataOutput out, Character v) throws IOException {
			out.writeChar(v);
		}

		@Override
		public Character cast(Object o) {
			return Caster_Character.INSTANCE.castNoThrow(o);
		}

		@Override
		public Character minValue() {
			return MIN;
		}

		@Override
		public int getFixedSize() {
			return 2;
		}

		@Override
		public Character read(FastDataInput input) throws IOException {
			return input.readChar();
		}
		@Override
		public byte getType() {
			return AmiTable.TYPE_CHAR;
		}
		@Override
		public int getSize(Character value) {
			return getFixedSize();
		}
		@Override
		public void writePrimitiveLong(FastDataOutput out, long v) throws IOException {
			out.writeChar((char) v);
		}

		@Override
		public void writePrimitiveDouble(FastDataOutput out, double v) throws IOException {
			out.writeChar((char) v);
		}

		@Override
		public void writeMinValue(FastDataOutput out) throws IOException {
			out.writeChar(MIN);
		}

		@Override
		public boolean isMin(long v) {
			return (char) v == MIN;
		}

		@Override
		public boolean isMin(double v) {
			return (char) v == MIN;
		}
	}

	final public static class UTCN implements AmiHdbMarshallerPrimitive<DateNanos> {
		private static DateNanos MIN = new DateNanos(Long.MIN_VALUE);

		@Override
		public void write(FastDataOutput out, DateNanos v) throws IOException {
			out.writeLong(v.getTimeNanos());
		}

		@Override
		public DateNanos cast(Object o) {
			return Caster_DateNanos.INSTANCE.castNoThrow(o);
		}

		@Override
		public DateNanos minValue() {
			return MIN;
		}

		@Override
		public int getFixedSize() {
			return 8;
		}

		@Override
		public DateNanos read(FastDataInput input) throws IOException {
			long r = input.readLong();
			if (r == Long.MIN_VALUE)
				return MIN;
			return new DateNanos(r);
		}
		@Override
		public byte getType() {
			return AmiTable.TYPE_UTCN;
		}
		@Override
		public int getSize(DateNanos value) {
			return getFixedSize();
		}
		@Override
		public void writePrimitiveLong(FastDataOutput out, long v) throws IOException {
			out.writeLong(v);
		}

		@Override
		public void writePrimitiveDouble(FastDataOutput out, double v) throws IOException {
			out.writeLong((long) v);
		}

		@Override
		public void writeMinValue(FastDataOutput out) throws IOException {
			out.writeLong(MIN.getTimeNanos());
		}

		@Override
		public boolean isMin(long v) {
			return v == MIN.getTimeNanos();
		}

		@Override
		public boolean isMin(double v) {
			return (long) v == MIN.getTimeNanos();
		}
	}

	final public static class UTC implements AmiHdbMarshallerPrimitive<DateMillis> {
		private static DateMillis MIN = new DateMillis(Long.MIN_VALUE);

		@Override
		public void write(FastDataOutput out, DateMillis v) throws IOException {
			out.writeLong(v.getDate());
		}

		@Override
		public DateMillis cast(Object o) {
			return Caster_DateMillis.INSTANCE.castNoThrow(o);
		}

		@Override
		public DateMillis minValue() {
			return MIN;
		}

		@Override
		public int getFixedSize() {
			return 8;
		}

		@Override
		public DateMillis read(FastDataInput input) throws IOException {
			long r = input.readLong();
			if (r == Long.MIN_VALUE)
				return MIN;
			return new DateMillis(r);
		}
		@Override
		public byte getType() {
			return AmiTable.TYPE_UTC;
		}
		@Override
		public int getSize(DateMillis value) {
			return getFixedSize();
		}
		@Override
		public void writePrimitiveLong(FastDataOutput out, long v) throws IOException {
			out.writeLong(v);
		}

		@Override
		public void writePrimitiveDouble(FastDataOutput out, double v) throws IOException {
			out.writeLong((long) v);
		}

		@Override
		public void writeMinValue(FastDataOutput out) throws IOException {
			out.writeLong(MIN.getDate());
		}

		@Override
		public boolean isMin(long v) {
			return (long) v == MIN.getDate();
		}

		@Override
		public boolean isMin(double v) {
			return (long) v == MIN.getDate();
		}
	}

	final public static class UUID_ implements AmiHdbMarshallerFixedSize<UUID> {
		private static UUID MIN = new UUID(Long.MIN_VALUE, Long.MIN_VALUE);

		@Override
		public void write(FastDataOutput out, UUID v) throws IOException {
			out.writeLong(v.getMostSignificantBits());
			out.writeLong(v.getLeastSignificantBits());
		}

		@Override
		public UUID cast(Object o) {
			return Caster_UUID.INSTANCE.castNoThrow(o);
		}

		@Override
		public UUID minValue() {
			return MIN;
		}

		@Override
		public int getFixedSize() {
			return 16;
		}

		@Override
		public UUID read(FastDataInput input) throws IOException {
			long r1 = input.readLong();
			long r2 = input.readLong();
			if (r1 == Long.MIN_VALUE && r2 == Long.MIN_VALUE)
				return MIN;
			return new UUID(r1, r2);
		}
		@Override
		public byte getType() {
			return AmiTable.TYPE_UUID;
		}

		@Override
		public int getSize(UUID value) {
			return getFixedSize();
		}
	}

	final public static class COMPLEX implements AmiHdbMarshallerFixedSize<Complex> {
		private static Complex MIN = new Complex(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);

		@Override
		public void write(FastDataOutput out, Complex v) throws IOException {
			out.writeDouble(v.real());
			out.writeDouble(v.imaginary());
		}

		@Override
		public Complex cast(Object o) {
			return Caster_Complex.INSTANCE.castNoThrow(o);
		}

		@Override
		public Complex minValue() {
			return MIN;
		}

		@Override
		public int getFixedSize() {
			return 16;
		}

		@Override
		public Complex read(FastDataInput input) throws IOException {
			double r = input.readDouble();
			double i = input.readDouble();
			if (r == Double.NEGATIVE_INFINITY && i == Double.NEGATIVE_INFINITY)
				return MIN;
			return new Complex(r, i);
		}
		@Override
		public byte getType() {
			return AmiTable.TYPE_COMPLEX;
		}

		@Override
		public int getSize(Complex value) {
			return getFixedSize();
		}
	}

	final public static class STRING implements AmiHdbMarshallerVarSize<String> {
		@Override
		public String read(FastDataInput in) throws IOException {
			return in.readUTF();
		}

		@Override
		public void write(FastDataOutput out, String val) throws IOException {
			out.writeUTFSupportLarge(val);

		}

		@Override
		public String cast(Object o) {
			return Caster_String.INSTANCE.castNoThrow(o);
		}
		@Override
		public byte getType() {
			return AmiTable.TYPE_STRING;
		}

		@Override
		public int getSize(String value) {
			return FastByteArrayDataOutputStream.getUTFLength(value, true);
		}

		@Override
		public void skip(FastDataInput in) throws IOException {
			in.skipUTF();
		}
	}

	final public static class BINARY implements AmiHdbMarshallerVarSize<Bytes> {
		@Override
		public Bytes read(FastDataInput in) throws IOException {
			final byte[] t = new byte[in.readInt()];
			in.readFully(t);
			return new Bytes(t);
		}

		@Override
		public void write(FastDataOutput out, Bytes val) throws IOException {
			out.writeInt(val.length());
			out.write(val.getBytes());

		}

		@Override
		public Bytes cast(Object o) {
			return Caster_Bytes.INSTANCE.castNoThrow(o);
		}
		@Override
		public byte getType() {
			return AmiTable.TYPE_BINARY;
		}

		@Override
		public int getSize(Bytes value) {
			return 4 + value.getBytes().length;
		}

		@Override
		public void skip(FastDataInput in) throws IOException {
			IOH.skipBytes(in, in.readInt());
			return;
		}
	}

	final public static class BIGINT implements AmiHdbMarshallerVarSize<BigInteger> {
		@Override
		public BigInteger read(FastDataInput in) throws IOException {
			int length = in.readInt();
			byte[] bytes = new byte[length];
			in.readFully(bytes);
			return new BigInteger(bytes);
		}

		@Override
		public void write(FastDataOutput out, BigInteger val) throws IOException {
			byte[] bytes = val.toByteArray();
			out.writeInt(bytes.length);
			out.write(bytes);
		}

		@Override
		public BigInteger cast(Object o) {
			return Caster_BigInteger.INSTANCE.castNoThrow(o);
		}
		@Override
		public byte getType() {
			return AmiTable.TYPE_BIGINT;
		}

		@Override
		public int getSize(BigInteger value) {
			byte[] bytes = value.toByteArray();
			return 4 + bytes.length;
		}
		@Override
		public void skip(FastDataInput in) throws IOException {
			int length = in.readInt();
			IOH.skipBytes(in, length);
		}
	}

	final public static class BIGDEC implements AmiHdbMarshallerVarSize<BigDecimal> {
		@Override
		public BigDecimal read(FastDataInput in) throws IOException {
			int scale = in.readInt();
			int length = in.readInt();
			byte[] bytes = new byte[length];
			in.readFully(bytes);
			return new BigDecimal(new BigInteger(bytes), scale);
		}

		@Override
		public void write(FastDataOutput out, BigDecimal val) throws IOException {
			byte[] bytes = val.unscaledValue().toByteArray();
			out.writeInt(val.scale());
			out.writeInt(bytes.length);
			out.write(bytes);
		}

		@Override
		public BigDecimal cast(Object o) {
			return Caster_BigDecimal.INSTANCE.castNoThrow(o);
		}
		@Override
		public byte getType() {
			return AmiTable.TYPE_BIGDEC;
		}

		@Override
		public int getSize(BigDecimal value) {
			byte[] bytes = value.unscaledValue().toByteArray();
			return 8 + bytes.length;
		}

		@Override
		public void skip(FastDataInput in) throws IOException {
			in.readInt();
			int length = in.readInt();
			IOH.skipBytes(in, length);
		}
	}
}
