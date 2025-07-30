package com.f1.utils.validation;

import com.f1.base.ToStringable;
import com.f1.utils.SH;

public class Issue implements ToStringable {
	final public static byte WARNING = 1, ERROR = 2;
	final private byte type;
	final private Object field;
	final private String message;
	final private Throwable throwable;

	public Issue(byte type, Object field, String message, Throwable throwable) {
		super();
		this.type = type;
		this.field = field;
		this.message = message;
		this.throwable = throwable;
	}

	public byte getType() {
		return type;
	}

	public String getTypeString() {
		switch (type) {
			case WARNING :
				return "warning";
			case ERROR :
				return "error";
			default :
				return "unknown: " + type;
		}
	}

	public Object getField() {
		return field;
	}

	public String getMessage() {
		return message;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toString(StringBuilder sb) {
		return toString(sb, false);
	}

	public StringBuilder toString(StringBuilder sb, boolean expandExceptions) {
		sb.append(getTypeString());
		sb.append(':');
		if (field != null)
			sb.append(field).append(':');
		sb.append(message);
		if (throwable != null) {
			sb.append(' ');
			if (expandExceptions) {
				SH.printStackTrace("> ", "", throwable, sb);
			} else
				sb.append(throwable);
		}
		return sb;
	}
}
