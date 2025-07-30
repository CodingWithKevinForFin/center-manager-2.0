package com.f1.utils;

import java.util.Random;

import org.junit.Test;

import junit.framework.Assert;

public class IOHTests {

	@Test
	public void testGetCanonical() {
		testCanonical("");
		testCanonical("/");
		testCanonical("///");
		testCanonical("/a/b");
		testCanonical("/this/that/then/../../that.text");
		testCanonical("/a/b/../c/../d");
		testCanonical("/this/that/../then/../that.text");
		testCanonical("../a/c/../d/../b");
		testCanonical("../this/that/../then/../that.text");
		testCanonical("../that/../then/../that.text");
		testCanonical("../that/..//then/..//that.text");
		testCanonical("../that/../then/../that.text");
		testCanonical("///../../..///b");
		testCanonical("/../../then/../that.text");
		testCanonical("../then/../that.text");
		testCanonical("../../then/../that.text");
		testCanonical("../.././then/.././that.text");
		testCanonical(".././.././then/.././that.text");
		testCanonical("./.././.././then/.././that.text");
		testCanonical("../../then/../that.text");
		testCanonical("/this/that/then/../where/../that.text");
		testCanonical("/this/that.text");
		testCanonical("/this/./that.text");
		testCanonical("/this/./.that.text");
		testCanonical("/this/././.that.text");
		testCanonical("/this/./../.that.text");
		testCanonical("/this/./../.that.text");
		testCanonical("/this/../that.text");
		testCanonical("../that.text");
		testCanonical("../../that.text");
		testCanonical("blah/../a");
		testCanonical("blah/../a/../b");
		testCanonical("blah/../../a");
		testCanonical("../../..");
		testCanonical("../../blah/../../a");
		testCanonical("../..");
		testCanonical("../");
		testCanonical("/../");
		testCanonical("./../");
		testCanonical("//test");
		testCanonical("/test//test");
		testCanonical("//test//test");
		testCanonical("//test//test//");
	}

	public static void testCanonical(String t) {
		System.out.println(t);
		System.out.println(IOH.getCanonical(t));
		System.out.println();
		Assert.assertEquals(t, getCanonical(t), IOH.getCanonical(t));
		Assert.assertEquals("/" + t, getCanonical("/" + t), IOH.getCanonical("/" + t));
		Assert.assertEquals(t, getCanonical(t), IOH.getCanonical(t));
		Assert.assertEquals("/" + t + "/", getCanonical("/" + t + "/"), IOH.getCanonical("/" + t + "/"));
	}

	public static String getCanonical(String st) {
		st = st.replaceAll("//+", "/");
		st = replaceAll(st, "/\\./", "/");
		st = st.replaceAll("^\\./", "");
		st = replaceAll(st, "/[^/]*[^/\\.][^/]*/\\.\\./", "/");
		st = replaceAll(st, "^[^/]*[^/\\.][^/]*/\\.\\./", "");
		if (st.length() == 0)
			return "/";
		return st;
	}

	private static String replaceAll(String st, String s1, String s2) {
		for (;;) {
			String st2 = st.replaceAll(s1, s2);
			if (st2.equals(st))
				return st2;
			st = st2;
		}
	}

	public static void main2(String a[]) {
		System.out.println(getCanonical("/asdf/asdfsdfdfdfd/../../asdfasdfasdfasdf/..asdf/aasdf/asdf/../////./////...//asdfasdf"));
		System.out.println(IOH.getCanonical("/asdf/asdfsdfdfdfd/../../asdfasdfasdfasdf/..asdf/aasdf/asdf/../////./////...//asdfasdf"));
		for (Duration d = new Duration(); d.count() < 10; d.stampStdout(1)) {
			for (int i = 0; i < 10000; i++)
				IOH.getCanonical("/asdf/asdfsdfdfdfd/../../asdfasdfasdfasdf/..asdf/aasdf/asdf/../////./////...//asdfasdf");
		}
	}

	@Test
	public void testIp24() {
		testIp4ToInt(0);
		testIp4ToInt(100000);
		testIp4ToInt(Integer.MAX_VALUE);
		testIp4ToInt(Integer.MIN_VALUE);
		Random r = new Random(323);
		for (int i = 0; i < 100000; i++)
			testIp4ToInt(r.nextInt());
	}

	private void testIp4ToInt(int i) {
		byte[] parts = IOH.intToIp4(i);
		Assert.assertEquals(Integer.toString(i) + " ==> " + SH.join(".", parts), i, IOH.ip4ToInt(parts));
	}
}
