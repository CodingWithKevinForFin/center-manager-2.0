package com.f1.utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.nio.charset.Charset;
import java.util.Random;

import org.junit.Test;

import com.f1.utils.encrypt.EncoderUtils;

public class EncoderUtilsTest {

	@Test
	public void test() {
		test("blah 123");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 1000; i++) {
			sb.append((char) i);
			test(sb.toString());
		}
	}

	private void test(String string) {
		string = new String(string.getBytes(Charset.forName("UTF-16")), Charset.forName("UTF-16"));
		byte[] bytes = string.getBytes(Charset.forName("UTF-16"));
		assertEquals(string, new String(EncoderUtils.decode64(EncoderUtils.encode64(bytes)), Charset.forName("UTF-16")));
		assertEquals(string, new String(EncoderUtils.decode64(EncoderUtils.encode64(bytes).replaceAll("=", "")), Charset.forName("UTF-16")));
		assertEquals(string, new String(EncoderUtils.decode16(EncoderUtils.encode16(bytes)), Charset.forName("UTF-16")));
		assertEquals(string, new String(EncoderUtils.decode64IgnoreWhitespace("   " + addSpaces(EncoderUtils.encode64(bytes)) + "   "), Charset.forName("UTF-16")));
	}

	private String addSpaces(String s) {
		StringBuilder sb = new StringBuilder();
		for (char c : s.toCharArray())
			sb.append(c).append("\n");
		return sb.toString();
	}

	@Test()
	public void test3() {
		String text = "AIT_72237_ETTETC_API:XXXXYYYYYYYYZZZZZZZZ";
		test(text);
	}

	@Test()
	public void test2() {
		//		byte[] data = EncoderUtils.decode64("abcda===");
		//		byte[] data2 = EncoderUtils.decode64("abcda===");
		byte[] data;
		byte[] data2;

		data = EncoderUtils.decode64("aaaa");
		data2 = EncoderUtils.decode64("aaaa");
		assertArrayEquals(data, data2);

		data = EncoderUtils.decode64("aaa=");
		data2 = EncoderUtils.decode64("aaa");
		assertArrayEquals(data, data2);

		data = EncoderUtils.decode64("aa==");
		data2 = EncoderUtils.decode64("aa");
		assertArrayEquals(data, data2);

		data = EncoderUtils.decode64("a===");
		data2 = EncoderUtils.decode64("a");
		assertArrayEquals(data, data2);

		data = EncoderUtils.decode64("");
		data2 = EncoderUtils.decode64("");
		assertArrayEquals(data, data2);
	}

	@Test()
	public void testLong() {
		long n1 = 1234;
		testLong(n1);
		testLong(Long.MAX_VALUE);
		testLong(Long.MIN_VALUE);
		Random r = new Random();
		for (int i = 0; i < 100; i++)
			testLong(r.nextLong());
		for (int i = 0; i < 100; i++)
			testLong(r.nextLong() / 10000);
	}

	private void testLong(long n1) {
		StringBuilder sink = new StringBuilder();
		EncoderUtils.encodeLong64(n1, sink);
		long n2 = EncoderUtils.decodeLong64(sink, 0);
		assertEquals(n1, n2);
	}

	@Test()
	public void testInt() {
		testInt(1234);
		testInt(Integer.MAX_VALUE);
		testInt(Integer.MIN_VALUE);
		Random r = new Random();
		for (int i = 0; i < 100; i++)
			testInt(r.nextInt());
		for (int i = 0; i < 100; i++)
			testInt(r.nextInt() / 10000);
	}

	private void testInt(int n1) {
		StringBuilder sink = new StringBuilder();
		EncoderUtils.encodeInt64(n1, sink);
		long n2 = EncoderUtils.decodeInt64(sink, 0);
		assertEquals(n1, n2);
	}
}
