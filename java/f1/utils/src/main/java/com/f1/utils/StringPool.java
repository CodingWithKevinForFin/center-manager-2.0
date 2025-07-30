package com.f1.utils;

import java.util.HashMap;

public class StringPool {

	private Object[] root = new Object[256];

	public String pool(CharSequence cs) {
		return pool(root, 0, cs);

	}

	public String pool(Object[] data, int start, CharSequence cs) {
		for (;;) {
			int b = cs.charAt(start);
			if (b > 255)
				b = 255;
			Object node = data[b];
			if (node == null) {
				String r = cs.toString();
				data[b] = r;
				return r;
			} else if (node.getClass() == String.class) {
				String s = (String) node;
				if (SH.equals(cs, s))
					return (String) node;
				String r = cs.toString();
				Object[] node3 = new Object[256];
				node3[cs.charAt(start + 1)] = r;
				node3[s.charAt(start + 1)] = s;
				data[b] = node3;
				return r;
			}
			Object[] node2 = (Object[]) node;
			start += 1;
			data = node2;
		}
	}

	public static void toString(Object[] data, int tab) {
		for (int i = 0; i < data.length; i++)
			if (data[i] != null) {
				if (data[i] instanceof String) {
					System.out.println(SH.repeat(' ', tab * 2) + ((char) i) + ':' + data[i]);
				} else {
					System.out.println(SH.repeat(' ', tab * 2) + ((char) i) + ':');
					toString((Object[]) data[i], tab + 1);
				}

			}
	}

	public static void main(String a[]) {
		StringPool sp = new StringPool();
		for (int j = 0; j < 10; j++) {
			Duration d = new Duration();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 100000; i++) {
				sp.pool(SH.clear(sb).append("test"));
				sp.pool(SH.clear(sb).append("what"));
				sp.pool(SH.clear(sb).append("wet"));
				sp.pool(SH.clear(sb).append("where"));
				sp.pool(SH.clear(sb).append("blah2"));
				sp.pool(SH.clear(sb).append("blah3"));
				sp.pool(SH.clear(sb).append("blah4"));
				sp.pool(SH.clear(sb).append("blank"));
				sp.pool(SH.clear(sb).append("greate"));
				sp.pool(SH.clear(sb).append("good"));
			}
			d.stampStdout();
		}
		System.out.println();
		System.out.println();
		HashMap hm = new HashMap();
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < 10; j++) {
			Duration d = new Duration();
			for (int i = 0; i < 100000; i++) {
				pool(hm, SH.clear(sb).append("test"));
				pool(hm, SH.clear(sb).append("what"));
				pool(hm, SH.clear(sb).append("wet"));
				pool(hm, SH.clear(sb).append("where"));
				pool(hm, SH.clear(sb).append("blah2"));
				pool(hm, SH.clear(sb).append("blah3"));
				pool(hm, SH.clear(sb).append("blah4"));
				pool(hm, SH.clear(sb).append("blank"));
				pool(hm, SH.clear(sb).append("greate"));
				pool(hm, SH.clear(sb).append("good"));
			}
			d.stampStdout();
		}

		System.out.println();
		toString(sp.root, 0);
	}

	private static String pool(HashMap hm, StringBuilder sb) {
		String string = sb.toString();
		String s = (String) hm.get(string);
		if (s == null)
			hm.put(string, s = string);
		return s;
	}
}
