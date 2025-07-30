package com.f1.ami.relay;

import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiDataEntity;
import com.f1.base.Bytes;
import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.base.Caster;
import com.f1.base.Complex;
import com.f1.base.UUID;
import com.f1.utils.ByteHelper;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.Tuple3;
import com.f1.utils.structs.table.stack.BasicCalcFrame;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;

public class AmiRelayHelper {

	private static final Logger log = LH.get();

	public static void toValuesObject(IntKeyMap<Tuple3<String, Caster<?>, Integer>> keys, byte[] data, String type, String id, char msg, Object[] sink) {
		sink[0] = msg;
		for (int i = 3; i < sink.length; i++)
			sink[i] = null;
		final int keysLength = ByteHelper.readShort(data, 0);
		for (int i = 0, valPos = (keysLength << 1) + 2, len; i < keysLength; i++, valPos += len - 1) {
			len = AmiUtils.getDataLength(data, valPos);
			final short key = ByteHelper.readShort(data, (i << 1) + 2);
			Object value;
			Tuple3<String, Caster<?>, Integer> tuple = keys.get(key);
			if (tuple != null) {
				try {
					value = getValueAt(data, valPos);
				} catch (Exception e) {
					LH.warning(log, "Error for ", key, "=", keys.get(key), ", at pos, ", valPos, " data=", SH.join(",", data), e);
					continue;
				}
				if (tuple.getB() != null)
					value = tuple.getB().cast(value, false, false);
				sink[tuple.getC() + 1] = value;
			}
			valPos++;
		}
		sink[1] = type;
		sink[2] = id;
	}
	public static CalcFrame toValues(CalcTypes types, IntKeyMap<Tuple3<String, Caster<?>, Integer>> keys, byte[] data, String type, String id) {
		if (keys.isEmpty())
			return EmptyCalcFrame.INSTANCE;
		final int keysLength = ByteHelper.readShort(data, 0);
		CalcFrame r = new BasicCalcFrame(types);
		for (int i = 0, valPos = (keysLength << 1) + 2, len; i < keysLength; i++, valPos += len - 1) {
			if (r.getVarsCount() == keys.size())
				break;
			len = AmiUtils.getDataLength(data, valPos);
			final short key = ByteHelper.readShort(data, (i << 1) + 2);
			Object value;
			Tuple3<String, Caster<?>, Integer> tuple = keys.get(key);
			if (tuple != null) {
				try {
					value = getValueAt(data, valPos);
				} catch (Exception e) {
					LH.warning(log, "Error for ", key, "=", keys.get(key), ", at pos, ", valPos, " data=", SH.join(",", data), e);
					continue;
				}
				if (tuple.getB() != null)
					value = tuple.getB().cast(value, false, false);
				r.putValue(tuple.getA(), value);
			}
			valPos++;
		}
		r.putValue("T", type);
		r.putValue("I", id);
		return r;
	}
	public static Object getValueAt(byte[] data, int pos) {
		byte type = ByteHelper.readByte(data, pos++);
		switch (type) {
			case AmiDataEntity.PARAM_TYPE_NULL:
				return null;
			case AmiDataEntity.PARAM_TYPE_BOOLEAN:
				return data[pos++] == 1;
			case AmiDataEntity.PARAM_TYPE_INT1:
				return (int) ByteHelper.readByte(data, pos);
			case AmiDataEntity.PARAM_TYPE_INT2:
				return (int) ByteHelper.readShort(data, pos);
			case AmiDataEntity.PARAM_TYPE_INT3:
				return (int) ByteHelper.readInt3(data, pos);
			case AmiDataEntity.PARAM_TYPE_INT4:
				return (int) ByteHelper.readInt(data, pos);
			case AmiDataEntity.PARAM_TYPE_CHAR:
				return (char) ByteHelper.readChar(data, pos);
			case AmiDataEntity.PARAM_TYPE_FLOAT:
				return ByteHelper.readFloat(data, pos);
			case AmiDataEntity.PARAM_TYPE_DOUBLE:
				return ByteHelper.readDouble(data, pos);
			case AmiDataEntity.PARAM_TYPE_LONG1:
				return (long) ByteHelper.readByte(data, pos);
			case AmiDataEntity.PARAM_TYPE_LONG2:
				return (long) ByteHelper.readShort(data, pos);
			case AmiDataEntity.PARAM_TYPE_LONG3:
				return (long) ByteHelper.readInt3(data, pos);
			case AmiDataEntity.PARAM_TYPE_LONG4:
				return (long) ByteHelper.readInt(data, pos);
			case AmiDataEntity.PARAM_TYPE_LONG5:
				return (long) ByteHelper.readLong5(data, pos);
			case AmiDataEntity.PARAM_TYPE_LONG6:
			case AmiDataEntity.PARAM_TYPE_UTC6:
				return (long) ByteHelper.readLong6(data, pos);
			case AmiDataEntity.PARAM_TYPE_LONG7:
				return (long) ByteHelper.readLong7(data, pos);
			case AmiDataEntity.PARAM_TYPE_LONG8:
			case AmiDataEntity.PARAM_TYPE_UTCN:
				return (long) ByteHelper.readLong(data, pos);
			case AmiDataEntity.PARAM_TYPE_STRING: {
				int length = ByteHelper.readInt(data, pos) * 2;
				return new String(data, pos + 4, length);
			}
			case AmiDataEntity.PARAM_TYPE_BINARY: {
				final int length = ByteHelper.readInt(data, pos);
				final byte[] r = new byte[length];
				System.arraycopy(data, pos + 2, r, 0, length);
				return new Bytes(r);
			}
			case AmiDataEntity.PARAM_TYPE_COMPLEX: {
				double r = ByteHelper.readDouble(data, pos);
				double i = ByteHelper.readDouble(data, pos + 8);
				return new Complex(r, i);
			}
			case AmiDataEntity.PARAM_TYPE_UUID: {
				long m = ByteHelper.readLong(data, pos);
				long l = ByteHelper.readLong(data, pos + 8);
				return new UUID(m, l);
			}
			case AmiDataEntity.PARAM_TYPE_ASCII: {
				int length = ByteHelper.readInt(data, pos);
				return new String(data, pos + 4, length, SH.CHARSET_UTF);
			}
			case AmiDataEntity.PARAM_TYPE_ASCII_ENUM:
			case AmiDataEntity.PARAM_TYPE_ASCII_SMALL: {
				int length = ByteHelper.readByte(data, pos);
				return new String(data, pos + 1, length, SH.CHARSET_UTF);
			}
			default:
				throw new RuntimeException("bad type: " + type);
		}

	}
}
