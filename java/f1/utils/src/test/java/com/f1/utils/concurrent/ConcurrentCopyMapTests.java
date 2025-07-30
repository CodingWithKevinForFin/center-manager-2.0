package com.f1.utils.concurrent;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.f1.utils.SH;

public class ConcurrentCopyMapTests {

	@Test
	public void test1() {
		testMap(new ConcurrentCopyMap());
	}

	private void testMap(ConcurrentCopyMap m) {
		m.clear();
		for (int i = 0; i < 100; i++) {
			assertEquals(i, m.size());
			m.put(i, SH.toString(i));
			assertEquals(SH.toString(i), m.get(i));
		}
		for (int i = 0; i < 100; i++) {
			assertEquals(100 - i, m.size());
			assertEquals(SH.toString(i), m.remove(i));
		}
	}
}
