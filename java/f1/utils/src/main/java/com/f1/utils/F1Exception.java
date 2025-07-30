package com.f1.utils;

import com.f1.base.ToStringable;

public class F1Exception extends Exception implements ToStringable {

	private String messageCache = null;;
	private Object[] parts;

	public F1Exception() {
		parts = OH.EMPTY_OBJECT_ARRAY;
		messageCache = "";
	}

	public Object getPart(int index) {
		return parts[index];
	}
	public int getCount() {
		return parts.length;
	}

	//This constructor will modify the contents of this array, so do not be fancy and pass in an array, let varargs create for you
	public F1Exception(Object... message) {
		StringBuilder buf = null;
		int innerCauseLoc = -1;
		for (int i = message.length - 1; i >= 0; i--) {
			Object obj = message[i];
			if (innerCauseLoc == -1 && obj instanceof Throwable) {
				initCause((Throwable) obj);
				innerCauseLoc = i;
			} else if (!OH.isImmutable(obj)) {
				try {
					if (buf == null)
						buf = new StringBuilder();
					else
						SH.clear(buf);
					message[i] = formatObjectSafe(obj, buf);
				} catch (Throwable ex) {
					message[i] = obj.getClass().getName() + ".toString() failed: " + ex;
				}
			}
		}

		if (innerCauseLoc != -1) {
			this.parts = AH.remove(message, innerCauseLoc);
		} else {
			this.parts = message;
		}
		if (parts.length == 1 && parts[0] instanceof String)
			messageCache = (String) parts[0];
	}
	private StringBuilder formatObjectSafe(Object obj, StringBuilder sink) {
		int len = sink.length();
		try {
			formatObject(obj, sink);
		} catch (Throwable ex) {
			sink.setLength(len);
			sink.append(obj.getClass().getName()).append(".toString() failed: ").append(ex);
		}
		return sink;
	}

	protected StringBuilder formatObject(Object object, StringBuilder sink) {
		return SH.s(object, sink);
	}

	@Override
	public String getMessage() {
		if (messageCache == null) {
			StringBuilder buf = new StringBuilder(parts.length * 8);
			for (Object obj : parts)
				formatObjectSafe(obj, buf);
			messageCache = buf.toString();
		}
		return messageCache;
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append(getClass().getName()).append(':');
		if (messageCache == null) {
			for (Object obj : parts)
				formatObjectSafe(obj, sink);
		} else
			sink.append(messageCache);
		return sink;
	}

	public static void main(String a[]) throws F1Exception {
		try {
			throw new F1Exception("there is a problem with value: ", 45, " and the correct value would be: ", 32);
		} catch (Exception e) {
			throw new F1Exception("there is a problem with value: ", 20, " - ", 32, " exception is: ", e);
		}
	}

}
