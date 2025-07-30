package com.f1.utils;

import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import org.junit.Test;
import com.f1.utils.CachedFile.Cache;

public class FileCachedTests {

	@Test
	public void test1() throws IOException {
		if (true)
			return;// TODO:FIX THIS
		int DELAY = 100;
		File f = new File("FileCached.text");
		IOH.delete(f);
		CachedFile file = new CachedFile(f, 10);
		Cache cache1 = file.getData();
		assertFalse(cache1.exists());
		IOH.writeText(f, "what");
		System.out.println("modified time(1):" + file.getModifiedMs());
		OH.sleep(DELAY);
		assertFalse(cache1.exists());
		assertTrue(cache1.isOld());
		Cache cache2 = cache1.getUpdated();
		assertTrue(cache2.exists());
		assertEquals(null, cache1.getText());
		assertEquals("what", cache2.getText());
		IOH.writeText(f, "where");
		System.out.println("modified time(2):" + file.getModifiedMs());
		OH.sleep(DELAY);
		assertEquals("what", cache2.getText());
		assertTrue(cache2.isOld());
		Cache cache3 = cache2.getUpdated();
		assertEquals("where", cache3.getText());
		OH.sleep(DELAY);
		assertFalse(cache3.isOld());
		OH.sleep(DELAY);
		assertTrue(cache3 == cache3.getUpdated());
		IOH.delete(f);
		System.out.println("modified time(3):" + file.getModifiedMs());
		OH.sleep(DELAY);
		assertTrue(cache3.exists());
		assertFalse(cache3.getUpdated().exists());
	}
}
