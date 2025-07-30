/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.concurrent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class UnsafeHelper {
	public static final Unsafe unsafe;
	static {
		Unsafe r;
		try {
			Field field = Unsafe.class.getDeclaredField("theUnsafe");
			field.setAccessible(true);
			r = (sun.misc.Unsafe) field.get(null);
		} catch (Exception e) {
			r = null;
			e.printStackTrace();
		}
		unsafe = r;
	}

	public static void cleaner(MappedByteBuffer buffer) throws Exception {
		try {
			Method cleanerMethod = MappedByteBuffer.class.getMethod("cleaner", new Class[0]);
			cleanerMethod.setAccessible(true);
			cleanerMethod.invoke(buffer, new Object[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void clean(ByteBuffer buf) {
		((sun.nio.ch.DirectBuffer) buf).cleaner().clean();
	}

}
