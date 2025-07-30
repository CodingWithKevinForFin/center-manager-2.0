package com.f1.utils.cacher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Test;

import com.f1.utils.CH;
import com.f1.utils.MockClock;
import com.f1.utils.OH;
import com.f1.utils.mapping.MapBackedMapping;

public class CachedGetterTests {
	final int MAX = 10000;
	final int TIME = 10;
	@Test
	public void test1() {
		Map<Integer, String> m = CH.m(1, "10", 2, "20", 3, "30", 4, "40", 5, "50");
		MapBackedMapping<Integer, String> mg = new MapBackedMapping<Integer, String>(m);
		MockClock clock = new MockClock(1000, TimeZone.getDefault(), Locale.getDefault());
		CachedGetter<Integer, String> cg = new CachedGetter<Integer, String>(mg, true, clock);
		cg.setTimeToLiveMs(3);
		assertEquals("10", cg.get(1));
		assertEquals("20", cg.get(2));
		assertEquals(2, cg.getCacheSize());
		assertEquals("10", cg.get(1));
		assertEquals("20", cg.get(2));
		assertEquals("30", cg.get(3));
		m.put(3, "300");
		assertEquals(3, cg.getCacheSize());
		clock.incNow(1);
		assertEquals("30", cg.get(3));
		assertEquals(3, cg.getCacheSize());
		clock.incNow(1);
		assertEquals("30", cg.get(3));
		assertEquals(3, cg.getCacheSize());
		clock.incNow(1);
		assertEquals("30", cg.get(3));
		assertEquals(3, cg.getCacheSize());
		clock.incNow(1);
		assertEquals("300", cg.get(3));
		assertEquals(1, cg.getCacheSize());
		assertEquals("10", cg.get(1));
		assertEquals("20", cg.get(2));
		assertEquals("300", cg.get(3));
		assertEquals("40", cg.get(4));
		assertEquals("50", cg.get(5));
		assertEquals(5, cg.getCacheSize());
		System.out.println(cg);
		cg.setMaxSize(3);
		System.out.println(cg);
		assertEquals(3, cg.getCacheSize());
		assertFalse(cg.isInCache(3));
		assertFalse(cg.isInCache(1));
		assertTrue(cg.isInCache(2));
		assertTrue(cg.isInCache(4));
		assertTrue(cg.isInCache(5));

		Map<Integer, String> map = new HashMap<Integer, String>();
		for (int i = 0; i < MAX; i++) {
			CH.putOrThrow(map, i, "" + i);
		}
		MapBackedMapping<Integer, String> mapGetter = new MapBackedMapping<Integer, String>(map);
		MockClock clock2 = new MockClock(10, TimeZone.getDefault(), Locale.getDefault());
		CachedGetter<Integer, String> cacheGetter = new CachedGetter<Integer, String>(mapGetter, true, clock2);
		int hit = 0, update = 0, miss = 0;
		cacheGetter.setTimeToLiveMs(TIME);
		for (int i = 0; i < MAX; i++) {
			OH.assertFalse(cacheGetter.isInCache(i));
			OH.assertEq("" + i, cacheGetter.get(i));
			OH.assertEq("" + i, cacheGetter.get(i));
			miss++;
			hit++;
			OH.assertTrue(cacheGetter.isInCache(i));
			if (i <= TIME)
				OH.assertEq(i + 1, cacheGetter.getCacheSize());
			else
				OH.assertEq(TIME + 1, cacheGetter.getCacheSize());
			clock2.incNow(1);
		}
		for (int i = 0; i < MAX - TIME; i += TIME) {
			cacheGetter.setMaxSize(TIME);
			for (int j = i; j < i + TIME; j++) {
				OH.assertEq("" + j, cacheGetter.get(j));
				miss++;
			}
			cacheGetter.setMaxSize(TIME / 2);
			for (int j = 0; j < TIME; j++) {
				if ((TIME % 2 == 0 && j < TIME / 2) || (TIME % 2 == 1 && j < TIME / 2 + 1))
					OH.assertFalse(cacheGetter.isInCache(i + j));
				else
					OH.assertTrue(cacheGetter.isInCache(i + j));
			}
		}

		for (int i = 0; i < MAX; i++) {
			OH.assertEq("" + i, cacheGetter.get(i));
			miss++;
			clock2.setNow(clock2.getNow() + TIME + 1);
			OH.assertEq("" + i, cacheGetter.get(i));
			update++;
		}
		OH.assertEq(miss, cacheGetter.getMiss());
		OH.assertEq(update, cacheGetter.getUpdate());
		OH.assertEq(hit, cacheGetter.getHit());
		assertTrue(cg.isInCache(5));
		cg.evict(5);
		assertFalse(cg.isInCache(5));
		cg.put(5, "test");
		assertTrue(cg.isInCache(5));
		assertEquals("test", cg.get(5));
		clock.incNow(TIME * 2);
		assertEquals("50", cg.get(5));
	}

}
