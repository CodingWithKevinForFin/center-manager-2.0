package com.f1.ami.relay.fh;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiEntityByteUtils;
import com.f1.ami.amicommon.msg.AmiDataEntity;
import com.f1.base.Complex;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.utils.ByteHelper;
import com.f1.utils.FastByteArrayDataInputStream;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class AmiRelayMapToBytesConverter {

	private static final Logger log = LH.get();
	public static final byte[] EMPTY = new byte[] { 0, 0 };
	private FastByteArrayDataOutputStream converterKeysOut = new FastByteArrayDataOutputStream();
	private FastByteArrayDataOutputStream converterValsOut = new FastByteArrayDataOutputStream();

	private int fieldsCount;

	public byte[] toBytes() {
		if (fieldsCount == 0)
			return EMPTY.clone();
		int keyBufSize = converterKeysOut.getCount();
		int valBufSize = converterValsOut.getCount();
		byte[] r = new byte[keyBufSize + valBufSize + 2];
		ByteHelper.writeShort(fieldsCount, r, 0);
		System.arraycopy(converterKeysOut.getBuffer(), 0, r, 2, keyBufSize);
		System.arraycopy(converterValsOut.getBuffer(), 0, r, 2 + keyBufSize, valBufSize);
		converterKeysOut.reset();
		converterValsOut.reset();
		fieldsCount = 0;
		return r;
	}
	public void clear() {
		if (fieldsCount != 0) {
			converterKeysOut.reset();
			converterValsOut.reset();
			fieldsCount = 0;
		}
	}

	private void appendKey(CharSequence name) {
		if (name.length() > 127)
			throw new RuntimeException("key length exceeds 127 char limit: '" + name + "'");
		converterKeysOut.writeByte(name.length());
		converterKeysOut.writeBytes(name);
		this.fieldsCount++;
	}
	public void appendNull(CharSequence key) {
		appendKey(key);
		converterValsOut.writeByte(AmiDataEntity.PARAM_TYPE_NULL);
	}

	public void appendRaw(CharSequence key, byte[] val, int start, int len) {
		appendKey(key);
		converterValsOut.write(val, start, len);
	}
	public void append(CharSequence key, Object val) {
		if (val == null)
			appendNull(key);
		else {
			Class<? extends Object> clazz = val.getClass();
			if (val instanceof Number) {
				if (clazz == Integer.class) {
					appendInt(key, (Integer) val);
				} else if (clazz == Long.class) {
					appendLong(key, (Long) val);
				} else if (clazz == Double.class) {
					appendDouble(key, (Double) val);
				} else if (clazz == Float.class) {
					appendFloat(key, (Float) val);
				} else if (clazz == DateMillis.class) {
					appendDateMillis(key, (DateMillis) val);
				} else if (clazz == DateNanos.class) {
					appendDateNanos(key, (DateNanos) val);
				} else if (clazz == Byte.class) {
					appendByte(key, (Byte) val);
				} else if (clazz == Short.class) {
					appendShort(key, (Short) val);
				} else if (clazz == BigDecimal.class) {
					appendBigDecimal(key, (BigDecimal) val);
				} else if (clazz == BigInteger.class) {
					appendBigInt(key, (BigInteger) val);
				} else if (clazz == Complex.class) {
					appendComplex(key, (Complex) val);
				} else {
					appendNull(key);
					LH.warning(log, "Don't know how to serialize numeric data of type ", clazz, " for '", key, "'");
				}
			} else if (clazz == char[].class) {
				appendChars(key, (char[]) val);
			} else if (clazz == String.class) {
				appendString(key, (String) val);
			} else if (clazz == Boolean.class) {
				appendBoolean(key, (Boolean) val);
			} else if (clazz == Date.class) {
				appendDate(key, (Date) val);
			} else if (clazz == byte[].class) {
				appendBytes(key, (byte[]) val);
			} else if (clazz == Character.class) {
				appendChar(key, (Character) val);
			} else {
				appendNull(key);
				LH.warning(log, "Don't know how to serialize data of type ", clazz, " for '", key, "'");
			}

		}

	}
	public void appendString(CharSequence key, CharSequence val) {
		appendKey(key);
		int len = val.length();
		boolean isAscii = true;
		for (int i = 0; i < len; i++)
			if (val.charAt(i) > 127) {
				isAscii = false;
				break;
			}
		if (!isAscii) {
			converterValsOut.writeByte(AmiDataEntity.PARAM_TYPE_STRING);
			converterValsOut.writeInt(len);
			converterValsOut.writeChars(val);
		} else {
			if (len > Byte.MAX_VALUE) {
				converterValsOut.writeByte(AmiDataEntity.PARAM_TYPE_ASCII);
				converterValsOut.writeInt(len);
			} else {
				converterValsOut.writeByte(AmiDataEntity.PARAM_TYPE_ASCII_SMALL);
				converterValsOut.writeByte(len);
			}
			converterValsOut.writeBytes(val);
		}

	}

	public void appendChars(CharSequence key, char[] val) {
		appendKey(key);
		int len = val.length;
		boolean isAscii = true;
		for (int i = 0; i < len; i++)
			if (val[i] > 127) {
				isAscii = false;
				break;
			}
		if (!isAscii) {
			converterValsOut.writeByte(AmiDataEntity.PARAM_TYPE_STRING);
			converterValsOut.writeInt(len);
			converterValsOut.writeChars(val);
		} else {
			if (len > Byte.MAX_VALUE) {
				converterValsOut.writeByte(AmiDataEntity.PARAM_TYPE_ASCII);
				converterValsOut.writeInt(len);
			} else {
				converterValsOut.writeByte(AmiDataEntity.PARAM_TYPE_ASCII_SMALL);
				converterValsOut.writeByte(len);
			}
			converterValsOut.writeBytes(val);
		}

	}

	public void appendBoolean(CharSequence key, boolean val) {
		appendKey(key);
		AmiEntityByteUtils.writeBoolean(converterValsOut, val);
	}

	public void appendChar(CharSequence key, char val) {
		appendKey(key);
		AmiEntityByteUtils.writeCharacter(converterValsOut, val);

	}

	public void appendBytes(CharSequence key, byte[] val) {
		appendKey(key);
		AmiEntityByteUtils.writeBinary(converterValsOut, val);

	}

	public void appendDate(CharSequence key, Date val) {
		appendKey(key);
		AmiEntityByteUtils.writeUtc(converterValsOut, val.getTime());
	}
	public void appendDate(CharSequence key, long val) {
		appendKey(key);
		AmiEntityByteUtils.writeUtc(converterValsOut, val);
	}

	public void appendDateNanos(CharSequence key, DateNanos val) {
		appendKey(key);
		AmiEntityByteUtils.writeUtcn(converterValsOut, val.getTimeNanos());
	}

	public void appendDateMillis(CharSequence key, DateMillis val) {
		appendKey(key);
		AmiEntityByteUtils.writeUtc(converterValsOut, val.getDate());
	}

	public void appendComplex(CharSequence key, Complex val) {
		appendKey(key);
		AmiEntityByteUtils.writeComplex(converterValsOut, val);
	}

	public void appendBigInt(CharSequence key, BigInteger val) {
		appendKey(key);
		AmiEntityByteUtils.writeBigInt(converterValsOut, val);
	}

	public void appendShort(CharSequence key, short val) {
		appendKey(key);
		AmiEntityByteUtils.writeInt(converterValsOut, val);
	}

	public void appendBigDecimal(CharSequence key, BigDecimal val) {
		appendKey(key);
		AmiEntityByteUtils.writeBigDec(converterValsOut, (BigDecimal) val);
	}

	public void appendByte(CharSequence key, byte val) {
		appendKey(key);
		AmiEntityByteUtils.writeInt(converterValsOut, val);
	}

	public void appendFloat(CharSequence key, float val) {
		appendKey(key);
		AmiEntityByteUtils.writeFloat(converterValsOut, val);
	}

	public void appendDouble(CharSequence key, double val) {
		appendKey(key);
		AmiEntityByteUtils.writeDouble(converterValsOut, val);
	}

	public void appendLong(CharSequence key, long val) {
		appendKey(key);
		AmiEntityByteUtils.writeLong(converterValsOut, val);
	}

	public void appendInt(CharSequence key, int i) {
		appendKey(key);
		AmiEntityByteUtils.writeInt(this.converterValsOut, i);
	}

	public byte[] toBytes(Map<String, Object> object) {
		if (object == null)
			return null;
		for (Map.Entry<String, Object> entry : object.entrySet())
			append(entry.getKey(), entry.getValue());
		return toBytes();
	}
	public static Map<String, Object> toMap(byte[] data) {
		try {
			final FastByteArrayDataInputStream in = new FastByteArrayDataInputStream(data);
			final int keysLength = in.readShort();
			final String[] names = new String[keysLength];
			for (int i = 0; i < keysLength; i++) {
				final int len = in.readByte();
				final byte tmp[] = new byte[len];
				in.read(tmp);
				names[i] = new String(tmp);
			}

			final Map<String, Object> r = new HashMap<String, Object>(keysLength);
			for (int i = 0; i < keysLength; i++)
				r.put(names[i], AmiEntityByteUtils.read(in, null));
			return r;
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
	}
}