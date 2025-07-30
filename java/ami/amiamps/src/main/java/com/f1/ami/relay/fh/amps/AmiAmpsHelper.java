package com.f1.ami.relay.fh.amps;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.ami.relay.fh.AmiRelayMapToBytesConverter;
import com.f1.utils.CharReader;
import com.f1.utils.OH;
import com.f1.utils.RH;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.BasicFromJsonConverterSession;
import com.f1.utils.converter.json2.FromJsonConverterSession;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.impl.BasicCharMatcher;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;

public class AmiAmpsHelper {
	public static final byte MTYPE_JSON = 1;
	public static final byte MTYPE_FIX = 2;
	public static final byte MTYPE_NVFIX = 3;
	public static final byte MTYPE_PROTOBUF = 4;
	public static final char FIX_DELIMITER = '\u0001'; // ASCII SOH
	public static final String NVFIX_DELIMTER = "\u0001";

	public static final BasicCharMatcher COMMA_OR_CLOSE_WHITE = new BasicCharMatcher(",} \n\t\r", false);

	public static byte parseType(String origUrl) {
		String url = SH.trim(SH.afterLast(origUrl, '/', origUrl));
		if ("json".equalsIgnoreCase(url))
			return MTYPE_JSON;
		else if ("fix".equalsIgnoreCase(url))
			return MTYPE_FIX;
		else if ("nvfix".equalsIgnoreCase(url))
			return MTYPE_NVFIX;
		else
			throw new RuntimeException("Unsupported AMPS protocol: '" + url + "' (supported: json,fix,nvfix and for protobufs please specify the protobuf class to map to)");
	}

	public static String formatType(byte type) {
		switch (type) {
			case MTYPE_FIX:
				return "fix";
			case MTYPE_NVFIX:
				return "nvfix";
			case MTYPE_JSON:
				return "json";
			default:
				throw new RuntimeException("bad AMPS protocol: " + type);
		}
	}

	public AmiAmpsHelper() {
	}

	static public boolean parseMessage(byte mtype, String data, BasicFromJsonConverterSession jsonConverter, AmiRelayMapToBytesConverter converter, StringBuilder errorSink,
			Parser<?> protobufParser, StringBuilder keyBuf) {
		switch (mtype) {
			case MTYPE_JSON:
				try {
					jsonConverter.getStream().reset(data);
					jsonStringToMap(jsonConverter, converter, keyBuf);
					return true;
				} catch (Exception e) {
					if (errorSink != null)
						errorSink.append("json parser error: " + e.getMessage());
					return false;
				}
				//				if (o instanceof Map) {
				//					for (Map.Entry<?, ?> e : ((Map<?, ?>) o).entrySet()) {
				//						if (e.getKey() != null) {
				//							Object val = e.getValue();
				//							if (val instanceof Map || val instanceof Collection)
				//								val = val.toString();
				//							converter.append(e.getKey().toString(), val);
				//						}
				//					}
				//					return true;
				//				} else {
				//					if (errorSink != null)
				//						errorSink.append("json top level must be a map");
				//					return false;
				//				}
			case MTYPE_FIX:
				try {
					data = SH.trim(SH.CHAR_SOH, data);
					splitToMap(converter, SH.CHAR_SOH, '=', data);
					return true;
				} catch (Exception e) {
					if (errorSink != null)
						errorSink.append("fix parser error: " + e.getMessage());
					return false;
				}
			case MTYPE_NVFIX:
				try {
					data = SH.trim(SH.CHAR_SOH, data);
					splitToMap(converter, SH.CHAR_SOH, '=', data);
					return true;
				} catch (Exception e) {
					if (errorSink != null)
						errorSink.append("nvfix parser error: " + e.getMessage());
					return false;
				}
			case MTYPE_PROTOBUF:
				AbstractMessage p;
				try {
					p = (AbstractMessage) protobufParser.parseFrom(data.getBytes());
					for (Entry<FieldDescriptor, Object> e : p.getAllFields().entrySet()) {
						Object v = e.getValue();
						if (v instanceof Number) {
							if (OH.isFloat(v.getClass()))
								v = ((Number) v).doubleValue();
							else if (!(v instanceof Integer || v instanceof Long || v instanceof Short || v instanceof Byte))
								v = ((Number) v).longValue();
						} else if (v != null && !(v instanceof Boolean)) {
							v = v.toString();
						}
						converter.append(e.getKey().getName(), v);
					}
				} catch (InvalidProtocolBufferException e1) {
					if (errorSink != null)
						errorSink.append("protobufs error: " + e1.getMessage());
					return false;
				}
				return true;
			default:
				throw new RuntimeException("bad AMPS protocol: " + mtype);
		}

	}

	public static boolean parseMessage(byte mtype, String data, Map<String, Object> parts, StringBuilder errorSink, Parser<?> protobufParser) {
		switch (mtype) {
			case MTYPE_JSON:
				Object o;
				try {
					o = ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject(data);
				} catch (Exception e) {
					if (errorSink != null)
						errorSink.append("json parser error: " + e.getMessage());
					return false;
				}
				if (o instanceof Map) {
					for (Map.Entry<?, ?> e : ((Map<?, ?>) o).entrySet()) {
						if (e.getKey() != null) {
							Object val = e.getValue();
							if (val instanceof Map || val instanceof Collection)
								val = val.toString();
							parts.put(e.getKey().toString(), val);
						}
					}
					return true;
				} else {
					if (errorSink != null)
						errorSink.append("json top level must be a map");
					return false;
				}
			case MTYPE_FIX:
				try {
					data = SH.trim(SH.CHAR_SOH, data);
					SH.splitToMap((Map) parts, SH.CHAR_SOH, '=', data);
					return true;
				} catch (Exception e) {
					if (errorSink != null)
						errorSink.append("fix parser error: " + e.getMessage());
					return false;
				}
			case MTYPE_NVFIX:
				try {
					data = SH.trim(SH.CHAR_SOH, data);
					SH.splitToMap((Map) parts, SH.CHAR_SOH, '=', data);
					return true;
				} catch (Exception e) {
					if (errorSink != null)
						errorSink.append("nvfix parser error: " + e.getMessage());
					return false;
				}
			case MTYPE_PROTOBUF:
				AbstractMessage p;
				try {
					p = (AbstractMessage) protobufParser.parseFrom(data.getBytes());
					for (Entry<FieldDescriptor, Object> e : p.getAllFields().entrySet()) {
						Object v = e.getValue();
						if (v instanceof Number) {
							if (OH.isFloat(v.getClass()))
								v = ((Number) v).doubleValue();
							else if (!(v instanceof Integer || v instanceof Long || v instanceof Short || v instanceof Byte))
								v = ((Number) v).longValue();
						} else if (v != null && !(v instanceof Boolean)) {
							v = v.toString();
						}
						parts.put(e.getKey().getName(), v);
					}
				} catch (InvalidProtocolBufferException e1) {
					if (errorSink != null)
						errorSink.append("protobufs error: " + e1.getMessage());
					return false;
				}
				return true;
			default:
				throw new RuntimeException("bad AMPS protocol: " + mtype);
		}

	}
	static public Parser<?> getProtobufParser(String className) {
		try {
			Class<?> c = Class.forName(className);
			return (Parser) RH.getStaticField(c, "PARSER");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not get Google Protobuf PARSER for " + className, e);
		}
	}

	public static AmiRelayMapToBytesConverter splitToMap(AmiRelayMapToBytesConverter converter, char delim, char associator, String text) {
		if (text == null || text.length() == 0)
			return converter;
		for (int last = 0;; last++) {
			int i = text.indexOf(associator, last);
			if (i == -1)
				throw new RuntimeException("trailing text after char " + last + ": " + text);
			final String key = text.substring(last, i);
			last = text.indexOf(delim, ++i);
			if (last == -1) {
				converter.appendString(key, text.substring(i));
				return converter;
			} else
				converter.appendString(key, text.substring(i, last));
		}
	}

	public static final int[] COMMA_OR_CLOSE = new int[] { ',', '}' };
	public static final int[] SPACE_OR_COLON = new int[] { ':', ' ' };

	private static void jsonStringToMap(FromJsonConverterSession session, AmiRelayMapToBytesConverter sink, StringBuilder keyBuf) {
		CharReader stream = session.getStream();
		stream.expect('{');
		boolean first = true;
		for (;;) {
			session.skipWhite();
			if (first) {
				first = false;
				if (stream.peak() == '}') {
					stream.readChar();
					break;
				}
			} else if (stream.expectAny(COMMA_OR_CLOSE) == '}')
				break;
			keyBuf.setLength(0);
			skipComments(stream);
			session.skipWhite();
			switch (stream.peak()) {
				case '\'':
					stream.expect('\'');
					stream.readUntil('\'', '\\', keyBuf);
					stream.expect('\'');
					break;
				case '\"':
					stream.expect('\"');
					stream.readUntil('\"', '\\', keyBuf);
					stream.expect('\"');
					break;
				default:
					stream.readUntilAny(SPACE_OR_COLON, '\\', keyBuf);
					break;
			}
			//			String key = keyBuf.toString();
			session.skipWhite();
			stream.expect(':');
			session.skipWhite();
			try {
				char peak = session.getStream().peak();
				switch (peak) {
					case '"':
					case '\'': {
						sink.appendString(keyBuf, stringToObject(session));
						break;
					}
					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
					case '.': {
						StringBuilder buf = session.getTempStringBuilder();
						stream.readUntilAny(AmiAmpsHelper.COMMA_OR_CLOSE_WHITE, false, buf);
						if (buf.indexOf(".") != -1 || SH.indexOfIgnoreCase(buf, "E", 0) != 1 || SH.endsWithIgnoreCase(buf, "D") || SH.endsWithIgnoreCase(buf, "F")) {
							double d = SH.parseDouble(buf);
							sink.appendDouble(keyBuf, d);
						} else {
							long n = SH.parseLong(buf, 10);
							if (SH.endsWithIgnoreCase(buf, "L") || n > Integer.MAX_VALUE || n < Integer.MIN_VALUE)
								sink.appendLong(keyBuf, n);
							else
								sink.appendInt(keyBuf, (int) n);
						}
						break;
					}

					default: {
						Object val = session.getConverter().stringToObject(session);
						if (val instanceof Collection || val instanceof Map)
							val = val.toString();
						sink.append(keyBuf, val);
					}
				}
			} catch (Exception e) {
				throw new RuntimeException("Error building map for field: ", e);
			}
		}
	}
	static public StringBuilder stringToObject(FromJsonConverterSession session) {
		CharReader stream = session.getStream();
		StringBuilder temp = session.getTempStringBuilder();
		char c = stream.readChar();
		if (c != '"' && c != '\'')
			throw new RuntimeException("expecting single quote(') or double quote(\") at char " + (stream.getCountRead() - 1));
		for (;;) {
			char c2 = stream.readChar();
			if (c == c2)
				return temp;
			if (c2 == '\\') {
				char c3 = stream.readChar();
				if (c3 == 'u') {
					int unicode = (readDigit(stream) << 12) + (readDigit(stream) << 8) + (readDigit(stream) << 4) + readDigit(stream);
					temp.append((char) unicode);
				} else
					temp.append(SH.toSpecialIfSpecial(c3));
			} else
				temp.append(c2);
		}
	}

	static private int readDigit(CharReader stream) {
		switch (stream.readChar()) {
			case '0':
				return 0x0;
			case '1':
				return 0x1;
			case '2':
				return 0x2;
			case '3':
				return 0x3;
			case '4':
				return 0x4;
			case '5':
				return 0x5;
			case '6':
				return 0x6;
			case '7':
				return 0x7;
			case '8':
				return 0x8;
			case '9':
				return 0x9;
			case 'a':
			case 'A':
				return 0xA;
			case 'b':
			case 'B':
				return 0xB;
			case 'c':
			case 'C':
				return 0xC;
			case 'd':
			case 'D':
				return 0xD;
			case 'e':
			case 'E':
				return 0xE;
			case 'f':
			case 'F':
				return 0xF;
			default:
				throw new RuntimeException("Invalid unicode encoding at char " + (stream.getCountRead() - 1));
		}
	}
	//From ObjectToJsonConverter
	protected static String skipComments(CharReader stream) {
		if (stream.peakOrEof() == '/') {
			char c[] = new char[2];
			if (stream.peak(c) == 2) {
				if (c[1] == '*') {

					stream.skip('/');
					stream.skip('*');
					StringBuilder sink = new StringBuilder();
					for (;;) {
						stream.readUntil('*', '\\', sink);
						char c1 = stream.expect('*');
						char c2 = stream.readChar();
						if (c2 == '/')
							break;
						sink.append(c1).append(c2);
						if (c2 == '\\')
							sink.append(stream.readChar());
					}
					return sink.toString();
				} else if (c[1] == '/') {
					stream.skip('/');
					stream.skip('/');
					StringBuilder sink = new StringBuilder();
					stream.readUntilAny(new int[] { SH.CHAR_CR, SH.CHAR_LF }, sink);
					return sink.toString();
				}
			}
		}
		return null;
	}
}
