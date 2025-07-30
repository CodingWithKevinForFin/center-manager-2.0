package com.f1.base;

import java.util.Arrays;

import com.f1.utils.converter.json2.Jsonable;

public class Bytes implements ToStringable, Comparable<Bytes>, Jsonable {
	public static final Bytes EMPTY = new Bytes(new byte[0]);
	private int hash = -1;
	private final byte[] data;

	public Bytes(byte[] data) {
		if (data == null)
			throw new NullPointerException("data");
		this.data = data;
	}

	public byte[] getBytes() {
		return data;
	}

	@Override
	public int compareTo(Bytes o) {
		int n1 = data.length;
		int n2 = o.data.length;
		int min = Math.min(n1, n2);
		for (int i = 0; i < min; i++) {
			int c1 = getUByteAt(i);
			int c2 = o.getUByteAt(i);
			if (c1 != c2)
				return c1 - c2;
		}
		return n1 - n2;
	}

	private int getUByteAt(int i) {
		return data[i] & 0xff;
	}
	public int length() {
		return data.length;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		return sink.append(data.length).append(" bytes");
	}

	public String toString() {
		return data.length + " bytes";
	}

	public static Bytes valueOf(byte[] data) {
		return data == null ? null : new Bytes(data);
	}

	public static byte[] getBytes(Bytes data) {
		return data == null ? null : data.getBytes();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof Bytes))
			return false;
		Bytes obj2 = (Bytes) obj;
		if (hash != -1 && obj2.hash != -1 && obj2.hash != hash)
			return false;
		return Arrays.equals(data, obj2.data);
	};

	private static final int HASH_MAX_SAMPLE_SIZE = 100;

	@Override
	public int hashCode() {
		int h = hash;
		if (h == -1) {
			int len = data.length;
			h = len;
			if (len <= HASH_MAX_SAMPLE_SIZE)
				for (int i = 0; i < len; i++)
					h = 31 * h + data[i];
			else {
				int skip = ((--len) + HASH_MAX_SAMPLE_SIZE) / HASH_MAX_SAMPLE_SIZE;
				for (int i = 0; i < len; i += skip)
					h = 31 * h + data[i];
				h = 31 * h + data[len];
			}
			if (h == -1)
				h = -2;
			hash = h;
		}
		return h;
	}
	public static void main(String a[]) {
		for (int i = 0; i < 100000; i++)
			new Bytes(new byte[i]).hashCode();
	}

	@Override
	public String objectToJson() {
		return this.toString();
	}
}
