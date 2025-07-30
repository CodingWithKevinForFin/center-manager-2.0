package com.f1.utils;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

public class ByteHelperTests {

	@Test
	public void testUnsigned() {
		byte[] buf = new byte[2];
		for (int i = 0; i < 65536; i++) {
			ByteHelper.writeUnsignedShort(i, buf, 0);
			assertEquals(i, ByteHelper.readUnsignedShort(buf, 0));
		}
	}
	@Test
	public void testBytes() {
		byte[] bytes = new byte[8];
		Random r = new Random(123);
		for (int i = 0; i < 1000; i++) {
			long val = r.nextLong();
			ByteHelper.writeLong(val, bytes, 0);
			long val2 = ByteHelper.readLong(bytes, 0);
			assertEquals(val, val2);
		}
	}

	@Test
	public void testGetGetByteAt() {
		for (long i = -127; i < 127; i++) {
			long j = i & 0xff;
			assertEquals((byte) i, ByteHelper.getByteAt(i, 7));
			assertEquals((byte) i, ByteHelper.getByteAt(i << 8, 6));
			assertEquals((byte) i, ByteHelper.getByteAt(i << 16, 5));
			assertEquals((byte) i, ByteHelper.getByteAt(i << 24, 4));
			assertEquals((byte) i, ByteHelper.getByteAt(i << 32, 3));
			assertEquals((byte) i, ByteHelper.getByteAt(i << 40, 2));
			assertEquals((byte) i, ByteHelper.getByteAt(i << 48, 1));
			assertEquals((byte) i, ByteHelper.getByteAt(i << 56, 0));
			assertEquals((long) i << 56, ByteHelper.setByteAt(0L, 0, (byte) i));
			assertEquals((long) (j << 48), ByteHelper.setByteAt(0L, 1, (byte) i));
			assertEquals((long) (j << 40), ByteHelper.setByteAt(0L, 2, (byte) i));
			assertEquals((long) (j << 32), ByteHelper.setByteAt(0L, 3, (byte) i));
			assertEquals((long) (j << 24), ByteHelper.setByteAt(0L, 4, (byte) i));
			assertEquals((long) (j << 16), ByteHelper.setByteAt(0L, 5, (byte) i));
			assertEquals((long) (j << 8), ByteHelper.setByteAt(0L, 6, (byte) i));
			assertEquals((long) (j << 0), ByteHelper.setByteAt(0L, 7, (byte) i));
		}
	}
	@Test
	public void testGetByteAt() {
		Random r = new Random(123);
		for (int i = 0; i < 100000; i++) {
			long val = r.nextLong();
			byte[] bytes = ByteHelper.asBytes(val);
			for (int j = 0; j < 8; j++)
				assertEquals(bytes[j], ByteHelper.getByteAt(val, j));
			byte b = (byte) r.nextInt();
			int pos = r.nextInt(8);

			bytes[pos] = b;
			val = ByteHelper.setByteAt(val, pos, b);
			assertEquals(ByteHelper.readLong(bytes, 0), val);

		}
	}
}
