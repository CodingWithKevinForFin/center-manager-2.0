package com.f1.console.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import com.f1.utils.FastByteArrayInputStream;
import com.f1.utils.FastByteArrayOutputStream;

public class TelnetInputStreamTests {

	//@Test
	public void testIn() throws IOException {
		byte b[] = new byte[] { 1, 2, 3, 10, 13, 5, 6, 10, 13, 7, 10, 13, 10, 13, 8 };
		FastByteArrayInputStream in = new FastByteArrayInputStream(b);
		TelnetInputStream tin = new TelnetInputStream(in);
		byte[] b2 = new byte[50];
		assertEquals(11, tin.read(b2));
		assertEquals(1, b2[0]);
		assertEquals(2, b2[1]);
		assertEquals(3, b2[2]);
		assertEquals(10, b2[3]);
		assertEquals(5, b2[4]);
		assertEquals(6, b2[5]);
		assertEquals(10, b2[6]);
		assertEquals(7, b2[7]);
		assertEquals(10, b2[8]);
		assertEquals(10, b2[9]);
		assertEquals(8, b2[10]);

		in.reset(new byte[] { 10, 13, 8 });
		assertEquals(2, tin.read(b2));
		assertEquals(10, b2[0]);
		assertEquals(8, b2[1]);

		in.reset(new byte[] { 10, 13, 8, 10 });
		assertEquals(3, tin.read(b2));
		assertEquals(10, b2[0]);
		assertEquals(8, b2[1]);
		assertEquals(10, b2[2]);

		in.reset(new byte[] { 13, 8 });
		assertEquals(1, tin.read(b2));
		assertEquals(8, b2[0]);
		in.reset(new byte[] { 1, 2, 3 });
		assertEquals(3, tin.read(b2));
		assertEquals(1, b2[0]);
		assertEquals(2, b2[1]);
		assertEquals(3, b2[2]);

		in.reset(new byte[] { 1, 2, 3, 10, 13 });
		assertEquals(4, tin.read(b2));
		assertEquals(1, b2[0]);
		assertEquals(2, b2[1]);
		assertEquals(3, b2[2]);
		assertEquals(10, b2[3]);

		in.reset(new byte[] { 1, 2, 3 });
		assertEquals(3, tin.read(b2));
		assertEquals(1, b2[0]);
		assertEquals(2, b2[1]);
		assertEquals(3, b2[2]);
	}

	//@Test
	public void testOut() throws IOException {
		FastByteArrayOutputStream buf = new FastByteArrayOutputStream();
		TelnetOutputStream out = new TelnetOutputStream(buf);

		buf.reset();
		out.write(new byte[] { 1, 2, 3, 4 });
		assertArrayEquals(new byte[] { 1, 2, 3, 4 }, buf.toByteArray());

		buf.reset();
		out.write(new byte[] { 1, 2, 3, 4, 10, 5 });
		assertArrayEquals(new byte[] { 1, 2, 3, 4, 10, 13, 5 }, buf.toByteArray());

		buf.reset();
		out.write(new byte[] { 1, 2, 3, 4, 10, 13, 5 });
		assertArrayEquals(new byte[] { 1, 2, 3, 4, 10, 13, 5 }, buf.toByteArray());
	}

}
