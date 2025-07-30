package com.f1.ami.relay.plugins;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.ami.amicommon.msg.AmiDataEntity;
import com.f1.ami.relay.AmiRelayPlugin;
import com.f1.ami.relay.fh.AmiFH;
import com.f1.utils.AH;
import com.f1.utils.ByteArray;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.impl.CharSequenceHasher;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.Tuple2;

public class FixAmiPlugin implements AmiRelayPlugin {

	private static final String AMI_FIXPLUGIN_PREFIX = "ami.fixplugin.prefix";
	private static final String AMI_FIXPLUGIN_EQUAL_ASCII = "ami.fixplugin.equal.ascii";
	private static final String AMI_FIXPLUGIN_DELIM_ASCII = "ami.fixplugin.delim.ascii";
	private static final String AMI_FIXPLUGIN_IGNORE_KEYS = "ami.fixplugin.ignore.keys";
	private static final byte[] HEADER = "O".getBytes();
	private static final Map<String, Byte> TYPES = new HashMap<String, Byte>();

	static {
		TYPES.put("string", AmiDataEntity.PARAM_TYPE_ASCII);
		TYPES.put("enum", AmiDataEntity.PARAM_TYPE_ASCII_ENUM);
		TYPES.put("int", AmiDataEntity.PARAM_TYPE_INT4);
		TYPES.put("long", AmiDataEntity.PARAM_TYPE_LONG8);
		TYPES.put("double", AmiDataEntity.PARAM_TYPE_DOUBLE);
		TYPES.put("float", AmiDataEntity.PARAM_TYPE_FLOAT);
		TYPES.put("utc", AmiDataEntity.PARAM_TYPE_UTC6);
	}
	private final StringBuilder valBuf = new StringBuilder("");
	private final FastByteArrayDataOutputStream buf = new FastByteArrayDataOutputStream();
	private IntKeyMap<Tuple2<String, Byte>> keys = new IntKeyMap<Tuple2<String, Byte>>();
	private HasherMap<String, Integer> toKeys = new HasherMap<String, Integer>(CharSequenceHasher.INSTANCE);
	private byte delim;
	private byte equal;
	private int typeTag;
	private SimpleDateFormat UTC_PARSER_MS = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS");
	private SimpleDateFormat UTC_PARSER = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
	private byte[] prefix = "FIX|".getBytes();
	final private IntKeyMap<Map<CharSequence, String>> enumsMap = new IntKeyMap<Map<CharSequence, String>>();
	private String typeDefaultValue;
	private StringBuilder keybuf = new StringBuilder();
	private HasherSet<CharSequence> ignoreKeys = null;

	@Override
	public int processData(ByteArray mutableRawData, StringBuilder errorSink) {
		byte[] data = mutableRawData.getData();
		if (!AH.startsWith(data, prefix, mutableRawData.getStart()))
			return NA;
		buf.reset(2048);
		int pos = mutableRawData.getStart() + prefix.length - 1;
		int length = mutableRawData.getEnd();
		buf.write(HEADER);
		CharSequence typeVal = typeDefaultValue;
		outer: while (pos < length) {
			final Tuple2<String, Byte> nameAndType;
			int key = 0;
			for (;;) {
				if (++pos == length) {
					if (key == 0)
						break outer;
					errorSink.append("unexpected end of line, missing value at byte ").append(pos);
					return ERROR;
				}
				byte val = data[pos];
				if (val == equal) {
					nameAndType = keys.get(key);
					break;
				} else if (val < '0' || val > '9') {
					if (key == 0) {
						keybuf.setLength(0);
						while (val != equal) {
							keybuf.append((char) val);
							if (++pos == length) {
								errorSink.append("unexpected end of line, missing value at byte ").append(pos);
								return ERROR;
							}
							val = data[pos];
						}
						Integer key2 = toKeys.get(keybuf);
						if (key2 != null) {
							key = key2.intValue();
							nameAndType = keys.get(key);
						} else
							nameAndType = null;
						break;
					} else {
						errorSink.append("key must be a number or variable at byte ").append(pos);
						return ERROR;
					}
				} else
					key = key * 10 + val - '0';
			}

			byte type;
			if (nameAndType == null) {
				if (keybuf.length() > 0) {
					if (shouldIgnore(keybuf))
						type = AmiDataEntity.PARAM_TYPE_NULL;
					else {
						buf.writeByte('|');
						type = AmiDataEntity.PARAM_TYPE_ASCII;
						buf.writeBytes(keybuf);
					}
				} else {
					String name = SH.toString(key);
					if (shouldIgnore(name)) {
						type = AmiDataEntity.PARAM_TYPE_NULL;
					} else {
						buf.writeByte('|');
						buf.writeByte('_');
						buf.writeBytes(name);
						type = AmiDataEntity.PARAM_TYPE_ASCII;
					}
				}
			} else {
				if (shouldIgnore(nameAndType.getA())) {
					type = AmiDataEntity.PARAM_TYPE_NULL;
				} else {
					buf.writeByte('|');
					buf.writeBytes(nameAndType.getA());
					type = nameAndType.getB().byteValue();
				}
			}
			pos++;

			SH.clear(valBuf);
			SH.clear(keybuf);
			boolean hasDecimal = false;
			while (pos < length && data[pos] != delim) {
				byte d = data[pos++];
				if (d == '.')
					hasDecimal = true;
				valBuf.append((char) d);
			}
			if (type == AmiDataEntity.PARAM_TYPE_NULL)
				continue;

			buf.writeByte('=');
			CharSequence val = valBuf;

			switch (type) {
				case AmiDataEntity.PARAM_TYPE_UTC6:
					try {
						String str = valBuf.toString();
						long utc = (str.length() == 17 ? UTC_PARSER : UTC_PARSER_MS).parse(str).getTime();
						SH.toString(utc, 10, SH.clear(valBuf));
						buf.writeBytes(valBuf);
						buf.writeByte('L');
					} catch (ParseException e) {
						errorSink.append("bad utc at byte ").append(pos);
						return ERROR;
					}
					break;
				case AmiDataEntity.PARAM_TYPE_ASCII:
					buf.writeByte('"');
					buf.writeBytes(valBuf);
					buf.writeByte('"');
					break;
				case AmiDataEntity.PARAM_TYPE_ASCII_ENUM:
					buf.writeByte('\'');
					Map<CharSequence, String> enms = this.enumsMap.get(key);
					if (enms != null) {
						String val2 = enms.get(valBuf);
						if (val2 != null)
							val = val2;
					}
					buf.writeBytes(val);
					buf.writeByte('\'');
					break;
				case AmiDataEntity.PARAM_TYPE_DOUBLE:
					buf.writeBytes(valBuf);
					buf.writeByte('D');
					break;
				case AmiDataEntity.PARAM_TYPE_INT4:
					buf.writeBytes(valBuf);
					break;
				case AmiDataEntity.PARAM_TYPE_LONG8:
					buf.writeBytes(valBuf);
					buf.writeByte('L');
					break;
				case AmiDataEntity.PARAM_TYPE_FLOAT:
					buf.writeBytes(valBuf);
					if (!hasDecimal)
						buf.writeBytes(".0");
					break;
			}
			if (key == this.typeTag) {
				typeVal = val;
			}

		}
		buf.writeBytes("|T='");
		buf.writeBytes(typeVal);
		buf.writeByte('\'');

		mutableRawData.reset(buf.getBuffer(), 0, buf.getCount());
		return OKAY;
	}
	private boolean shouldIgnore(CharSequence a) {
		return ignoreKeys != null && ignoreKeys.contains(a);
	}
	@Override
	public boolean init(PropertyController properties, AmiFH fh, String switches, StringBuilder errorSink) {
		delim = (byte) (char) properties.getOptional(AMI_FIXPLUGIN_DELIM_ASCII, '|');
		equal = (byte) (char) properties.getOptional(AMI_FIXPLUGIN_EQUAL_ASCII, '=');
		String ignoreKeys = properties.getOptional(AMI_FIXPLUGIN_IGNORE_KEYS);
		if (SH.is(switches)) {
			Map<String, String> switchMap = SH.splitToMap(',', '=', '\\', switches);
			for (Entry<String, String> e : switchMap.entrySet()) {
				final String key = e.getKey();
				final String value = e.getValue();
				try {
					if (AMI_FIXPLUGIN_DELIM_ASCII.equals(key))
						delim = (byte) SH.parseChar(value);
					else if (AMI_FIXPLUGIN_EQUAL_ASCII.equals(key))
						equal = (byte) SH.parseChar(value);
					else if (AMI_FIXPLUGIN_PREFIX.equals(key))
						prefix = value.getBytes();
					else if (AMI_FIXPLUGIN_IGNORE_KEYS.equals(key))
						ignoreKeys = value;
					else {
						errorSink.append("unknown switch: ").append(key);
						return false;
					}
				} catch (Exception e2) {
					errorSink.append("Error processing switch: ").append(key).append("=").append(value).append(" ==> ").append(e2.getMessage());
					return false;
				}
			}
		}
		if (ignoreKeys != null) {
			this.ignoreKeys = new HasherSet<CharSequence>(CharSequenceHasher.INSTANCE);
			for (String s : SH.splitWithEscape(',', '\\', ignoreKeys))
				this.ignoreKeys.add(s);
		}
		PropertyController sub = properties.getSubPropertyController("ami.fixplugin.tag.");
		for (String key : sub.getKeys()) {
			String value = sub.getRequired(key);
			int tag;
			try {
				tag = Integer.parseInt(key);
			} catch (Exception e) {
				errorSink.append("invalid tag definition, key must be number: ").append(key).append('=').append(value);
				return false;
			}
			String type = SH.beforeFirst(value, ' ');
			String name = SH.afterFirst(value, ' ');
			if (value.indexOf(' ') == -1 || SH.isnt(name) || SH.isnt(type)) {
				errorSink.append("invalid tag definition, must follow syntax 'tag=type name': ").append(key).append('=').append(value);
				return false;
			}
			Byte typeCode = TYPES.get(type);
			if (typeCode == null) {
				errorSink.append("invalid tag definition, type must be either ").append(TYPES.keySet()).append(": ").append(key).append('=').append(value);
				return false;
			}
			this.keys.put(tag, new Tuple2<String, Byte>(name, typeCode));
			this.toKeys.put(name, tag);
		}
		typeTag = properties.getOptional("ami.fixplugin.type.tag", 35);
		typeDefaultValue = properties.getOptional("ami.fixplugin.type.default.value", "FIX");
		sub = properties.getSubPropertyController("ami.fixplugin.enum.");
		for (String key : sub.getKeys()) {
			try {
				int tag = SH.parseInt(SH.beforeFirst(key, '.'));
				String value = SH.afterFirst(key, '.');
				Map<CharSequence, String> enums = this.enumsMap.get(tag);
				if (enums == null)
					this.enumsMap.put(tag, enums = new HasherMap<CharSequence, String>(new CharSequenceHasher()));
				enums.put(value, sub.getRequired(key));
			} catch (Exception e) {
				throw new RuntimeException("Error processing key: " + key);
			}
		}
		return true;
	}
}
