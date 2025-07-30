package com.f1.utils;

public class Property {

	public static final byte TYPE_FILE = 1;
	public static final byte TYPE_SYSTEM_PROPERTY = 2;
	public static final byte TYPE_SYSTEM_ENV = 3;
	public static final byte TYPE_CODE = 4;
	public static final byte TYPE_PREFERENCE = 5;
	public static final byte TYPE_COLLECTION = 6;
	public static final byte TYPE_RESOURCE = 7;
	public static final int NO_LINE_NUMBER = -1;

	private final String key;
	private final String value;
	private final String source;
	private final int lineNumber;
	private final byte type;
	private String toString;
	private boolean isSecure;

	public Property(String key, String value, byte type, String source, int lineNumber, boolean isSecure) {
		this.key = key;
		this.value = value;
		this.type = type;
		this.source = source;
		this.lineNumber = lineNumber;
		this.isSecure = isSecure;
		this.toString = key + " = " + value + " (source: " + formatType(type) + ":" + source + (lineNumber == NO_LINE_NUMBER ? "" : ":" + lineNumber) + ")";
	}

	public static String formatType(byte type) {
		switch (type) {
			case TYPE_FILE:
				return "file";
			case TYPE_SYSTEM_PROPERTY:
				return "system";
			case TYPE_SYSTEM_ENV:
				return "env";
			case TYPE_CODE:
				return "code";
			case TYPE_COLLECTION:
				return "collection";
			case TYPE_PREFERENCE:
				return "preferences";
			case TYPE_RESOURCE:
				return "resource";
			default:
				return SH.toString(type);
		}

	}

	public Property(String key, String value, byte type) {
		this.key = key;
		this.value = value;
		this.type = type;
		this.source = "";
		this.lineNumber = NO_LINE_NUMBER;
		this.toString = key + " = " + value + " (source: " + formatType(type) + ":" + source + ")";
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public String getSource() {
		return source;
	}

	public int getSourceLineNumber() {
		return lineNumber;
	}

	public byte getSourceType() {
		return type;
	}
	public boolean getIsSecure() {
		return isSecure;
	}

	@Override
	public String toString() {
		return toString;
	}

}
