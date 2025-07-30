package com.f1.ami.amicommon;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.f1.ami.amicommon.msg.AmiDataEntity;
import com.f1.base.Bytes;
import com.f1.base.Complex;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.base.UUID;
import com.f1.utils.FastByteArrayDataInputStream;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.IntKeyMap;

public class AmiEntityByteUtils {

	public static void writeInt(FastByteArrayDataOutputStream buf, int i) {
		switch (MH.getByteDepth(i)) {
			case 1:
				buf.writeByte(AmiDataEntity.PARAM_TYPE_INT1);
				buf.writeByte(i);
				break;
			case 2:
				buf.writeByte(AmiDataEntity.PARAM_TYPE_INT2);
				buf.writeShort(i);
				break;
			case 3:
				buf.writeByte(AmiDataEntity.PARAM_TYPE_INT3);
				buf.writeInt3(i);
				break;
			case 4:
				buf.writeByte(AmiDataEntity.PARAM_TYPE_INT4);
				buf.writeInt(i);
				break;
		}

	}
	public static void writeLong(FastByteArrayDataOutputStream buf, long i) {
		switch (MH.getByteDepth(i)) {
			case 1:
				buf.writeByte(AmiDataEntity.PARAM_TYPE_LONG1);
				buf.writeByte((byte) i);
				break;
			case 2:
				buf.writeByte(AmiDataEntity.PARAM_TYPE_LONG2);
				buf.writeShort((short) i);
				break;
			case 3:
				buf.writeByte(AmiDataEntity.PARAM_TYPE_LONG3);
				buf.writeInt3((int) i);
				break;
			case 4:
				buf.writeByte(AmiDataEntity.PARAM_TYPE_LONG4);
				buf.writeInt((int) i);
				break;
			case 5:
				buf.writeByte(AmiDataEntity.PARAM_TYPE_LONG5);
				buf.writeLong5(i);
				break;
			case 6:
				buf.writeByte(AmiDataEntity.PARAM_TYPE_LONG6);
				buf.writeLong6(i);
				break;
			case 7:
				buf.writeByte(AmiDataEntity.PARAM_TYPE_LONG7);
				buf.writeLong7(i);
				break;
			case 8:
				buf.writeByte(AmiDataEntity.PARAM_TYPE_LONG8);
				buf.writeLong(i);
				break;
		}

	}
	public static void writeEnum(FastByteArrayDataOutputStream buf, int i) {
		switch (MH.getByteDepth(i)) {
			case 1:
				buf.writeByte(AmiDataEntity.PARAM_TYPE_ENUM1);
				buf.writeByte((byte) i);
				break;
			case 2:
				buf.writeByte(AmiDataEntity.PARAM_TYPE_ENUM2);
				buf.writeShort((short) i);
				break;
			case 3:
				buf.writeByte(AmiDataEntity.PARAM_TYPE_ENUM3);
				buf.writeInt3((int) i);
				break;
			default:
				throw new RuntimeException("Bad enum: " + i);
		}
	}
	public static void writeDouble(FastByteArrayDataOutputStream buf, double val) {
		buf.writeByte(AmiDataEntity.PARAM_TYPE_DOUBLE);
		buf.writeDouble(val);

	}
	public static void writeFloat(FastByteArrayDataOutputStream buf, float val) {
		buf.writeByte(AmiDataEntity.PARAM_TYPE_FLOAT);
		buf.writeFloat((Float) val);
	}
	public static void writeBoolean(FastByteArrayDataOutputStream buf, boolean val) {
		buf.writeByte(AmiDataEntity.PARAM_TYPE_BOOLEAN);
		buf.writeByte((Boolean) val ? 1 : 0);

	}
	public static void writeUtc(FastByteArrayDataOutputStream buf, long time) {
		buf.writeByte(AmiDataEntity.PARAM_TYPE_UTC6);
		buf.writeLong6(time);
	}
	public static void writeUtcn(FastByteArrayDataOutputStream buf, long time) {
		buf.writeByte(AmiDataEntity.PARAM_TYPE_UTCN);
		buf.writeLong(time);
	}
	public static void writeBinary(FastByteArrayDataOutputStream buf, byte[] data) {
		buf.writeByte(AmiDataEntity.PARAM_TYPE_BINARY);
		buf.writeInt(data.length);
		buf.write(data);
	}
	public static void writeCharacter(FastByteArrayDataOutputStream buf, char c) {
		buf.writeByte(AmiDataEntity.PARAM_TYPE_CHAR);
		buf.writeChar(c);
	}
	public static void writeComplex(FastByteArrayDataOutputStream buf, Complex c) {
		buf.writeByte(AmiDataEntity.PARAM_TYPE_COMPLEX);
		buf.writeDouble(c.real());
		buf.writeDouble(c.imaginary());
	}
	public static void writeUUID(FastByteArrayDataOutputStream buf, UUID c) {
		buf.writeByte(AmiDataEntity.PARAM_TYPE_UUID);
		buf.writeLong(c.getMostSignificantBits());
		buf.writeLong(c.getLeastSignificantBits());
	}
	public static void writeBigInt(FastByteArrayDataOutputStream buf, BigInteger c) {
		buf.writeByte(AmiDataEntity.PARAM_TYPE_BIGINT);
		byte[] bytes = c.toByteArray();
		buf.writeInt(bytes.length);
		buf.write(bytes);
	}
	public static void writeBigDec(FastByteArrayDataOutputStream buf, BigDecimal c) {
		buf.writeByte(AmiDataEntity.PARAM_TYPE_BIGDEC);
		byte[] bytes = c.unscaledValue().toByteArray();
		buf.writeInt(c.scale());
		buf.writeInt(bytes.length);
		buf.write(bytes);
	}

	public static void main(String s[]) {
		BigDecimal in = new BigDecimal(123.4214);
		byte[] bytes = in.unscaledValue().toByteArray();
		BigDecimal out = new BigDecimal(new BigInteger(bytes), 0);
		System.out.println(in);
		System.out.println(out);
	}
	public static void writeCharSequence(FastByteArrayDataOutputStream buf, CharSequence s) {
		if (s == null) {
			buf.writeByte(AmiDataEntity.PARAM_TYPE_NULL);
			return;
		}

		int len = s.length();
		boolean isAscii = true;
		for (int i = 0; i < len; i++)
			if (s.charAt(i) > 127) {
				isAscii = false;
				break;
			}
		if (!isAscii) {
			buf.writeByte(AmiDataEntity.PARAM_TYPE_STRING);
			buf.writeInt(len);
			for (int i = 0; i < len; i++)
				buf.writeChar(s.charAt(i));
		} else {
			if (len > Byte.MAX_VALUE) {
				buf.writeByte(AmiDataEntity.PARAM_TYPE_ASCII);
				buf.writeInt(len);
			} else {
				buf.writeByte(AmiDataEntity.PARAM_TYPE_ASCII_SMALL);
				buf.writeByte(len);
			}
			for (int i = 0; i < len; i++)
				buf.writeByte(s.charAt(i));
		}

	}
	public static void writeNull(FastByteArrayDataOutputStream buf) {
		buf.writeByte(AmiDataEntity.PARAM_TYPE_NULL);
	}
	public static Object read(FastByteArrayDataInputStream in, IntKeyMap<String> enumValues) throws IOException {
		byte type = in.readByte();
		switch (type) {
			case AmiDataEntity.PARAM_TYPE_NULL:
				return null;
			case AmiDataEntity.PARAM_TYPE_BOOLEAN:
				return in.readByte() == 1 ? Boolean.TRUE : Boolean.FALSE;
			case AmiDataEntity.PARAM_TYPE_INT1:
				return OH.valueOf((int) in.readByte());
			case AmiDataEntity.PARAM_TYPE_INT2:
				return OH.valueOf((int) in.readShort());
			case AmiDataEntity.PARAM_TYPE_INT3:
				return OH.valueOf((int) in.readInt3());
			case AmiDataEntity.PARAM_TYPE_INT4:
				return OH.valueOf((int) in.readInt());
			case AmiDataEntity.PARAM_TYPE_FLOAT:
				return OH.valueOf((float) in.readFloat());
			case AmiDataEntity.PARAM_TYPE_DOUBLE:
				return OH.valueOf((double) in.readDouble());
			case AmiDataEntity.PARAM_TYPE_LONG1:
				return OH.valueOf((long) in.readByte());
			case AmiDataEntity.PARAM_TYPE_LONG2:
				return OH.valueOf((long) in.readShort());
			case AmiDataEntity.PARAM_TYPE_LONG3:
				return OH.valueOf((long) in.readInt3());
			case AmiDataEntity.PARAM_TYPE_LONG4:
				return OH.valueOf((long) in.readInt());
			case AmiDataEntity.PARAM_TYPE_LONG5:
				return OH.valueOf((long) in.readLong5());
			case AmiDataEntity.PARAM_TYPE_LONG6:
				return OH.valueOf((long) in.readLong6());
			case AmiDataEntity.PARAM_TYPE_UTC6:
				return new DateMillis(in.readLong6());
			case AmiDataEntity.PARAM_TYPE_UTCN:
				return new DateNanos(in.readLong());
			case AmiDataEntity.PARAM_TYPE_LONG7:
				return OH.valueOf((long) in.readLong7());
			case AmiDataEntity.PARAM_TYPE_LONG8:
				return OH.valueOf((long) in.readLong());
			case AmiDataEntity.PARAM_TYPE_STRING: {
				int length = in.readInt();
				char[] t = new char[length];
				for (int j = 0; j < length; j++)
					t[j] = in.readChar();
				return new String(t);
			}
			case AmiDataEntity.PARAM_TYPE_ASCII: {
				int length = in.readInt();
				byte[] bytes = new byte[length];
				in.readFully(bytes);
				return new String(bytes, SH.CHARSET_UTF);
			}
			case AmiDataEntity.PARAM_TYPE_ASCII_SMALL:
			case AmiDataEntity.PARAM_TYPE_ASCII_ENUM: {
				int length = in.readByte();
				byte[] bytes = new byte[length];
				in.readFully(bytes);
				return new String(bytes, SH.CHARSET_UTF);
			}
			case AmiDataEntity.PARAM_TYPE_BINARY: {
				int length = in.readInt();
				byte[] bytes = new byte[length];
				in.readFully(bytes);
				return new Bytes(bytes);
			}
			case AmiDataEntity.PARAM_TYPE_COMPLEX:
				double real = in.readDouble();
				double imaginary = in.readDouble();
				return new Complex(real, imaginary);
			case AmiDataEntity.PARAM_TYPE_UUID:
				long most = in.readLong();
				long least = in.readLong();
				return new UUID(most, least);
			case AmiDataEntity.PARAM_TYPE_BIGINT: {
				int length = in.readInt();
				byte[] bytes = new byte[length];
				in.readFully(bytes);
				return new BigInteger(bytes);
			}
			case AmiDataEntity.PARAM_TYPE_BIGDEC: {
				int scale = in.readInt();
				int length = in.readInt();
				byte[] bytes = new byte[length];
				in.readFully(bytes);
				return new BigDecimal(new BigInteger(bytes), scale);
			}
			case AmiDataEntity.PARAM_TYPE_ENUM1: {
				return enumValues.get((int) in.readByte());
			}
			case AmiDataEntity.PARAM_TYPE_ENUM2: {
				return enumValues.get((int) in.readShort());
			}
			case AmiDataEntity.PARAM_TYPE_ENUM3: {
				return enumValues.get((int) in.readInt3());
			}
			case AmiDataEntity.PARAM_TYPE_CHAR:
				return OH.valueOf((char) in.readChar());
			default:
				throw new RuntimeException("bad type: " + type);
		}
	}
}
