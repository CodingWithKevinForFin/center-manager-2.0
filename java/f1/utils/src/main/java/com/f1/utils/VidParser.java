/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import com.f1.base.Ideable;
import com.f1.base.VID;
import com.f1.base.VIN;

public class VidParser {

	static public long toLong(CharSequence vid) {
		if (vid == null)
			return Ideable.NO_IDEABLEID;
		long r = 0;
		int len = vid.length();
		if (!OH.isBetween(len, 2, 12))
			throw new RuntimeException("invalid VID pattern, must be string of 2-12 characters where A-Z and 0-9 and period (.) are valid characters: " + vid);
		int i = 0;
		for (; i < len; i++) {
			char c = vid.charAt(i);
			if (OH.isBetween(c, '0', '9')) {
				r = r * 38 + (c - '0');//numbers represented as 0-9
			} else if (OH.isBetween(c, 'A', 'Z')) {
				r = r * 38 + (10 + c - 'A');//letters represented as 10-35
			} else if (c == '.') {
				r = r * 38 + 36;//period represented as 36
			} else {
				throw new RuntimeException("invalid character '" + c + "' in VID pattern, valid characters are A-Z, 0-9 or period (.): " + vid);
			}
		}
		for (; i < 12; i++)
			r = r * 38 + 37;//37 
		return r + 2;//adding one just ensures that we can assume zero and one as an invalid VID
	}
	static public String fromLong(long vid) {
		if (vid == -1)
			return null;
		if (vid < 2)
			throw new RuntimeException("invalid vid: " + vid);
		long id = vid - 2;
		StringBuilder sb = new StringBuilder(12);
		for (int i = 0; i < 12; i++) {
			int val = (int) (id % 38);
			if (val < 10)
				sb.append((char) ('0' + val));//numbers represented as 0-9
			else if (val < 36)
				sb.append((char) ('A' + (val - 10)));//letters represented as 10-35
			else if (val == 36)
				sb.append('.');//period represented as 36
			//if value is 37, do nothing. padding represented as 37`
			id /= 38;
		}
		if (id != 0)
			throw new RuntimeException("invalid vid: " + vid);
		if (sb.length() < 2 || sb.length() > 12)
			throw new RuntimeException("invalid vid: " + vid);
		return sb.reverse().toString();
	}

	public static long getVid(Class<?> o) {
		VID vid = o.getAnnotation(VID.class);
		return vid == null ? Ideable.NO_IDEABLEID : toLong(vid.value());
	}

	public static String getVin(Class<?> o) {
		VIN vin = o.getAnnotation(VIN.class);
		if (vin != null)
			return vin.value();
		String s = fromLong(getVid(o));
		if (s != null)
			return s;
		if (!Ideable.class.isAssignableFrom(o))
			return null;
		return o.getCanonicalName();
	}

}
