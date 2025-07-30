package com.f1.utils.structs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Test;

import com.f1.base.ObjectGeneratorForClass;
import com.f1.utils.BasicObjectGeneratorForClass;

public class BasicMultiMapTests {

	@Test
	public void testBasicMultiMapClassMapOfKEYCOL() {
		Map<Integer, Collection> map1 = new HashMap<Integer, Collection>();
		BasicMultiMap.List map2 = new BasicMultiMap.List(map1);
	}

	@Test
	public void testBasicMultiMapClass() {
		BasicMultiMap.List map = new BasicMultiMap.List();
	}

	@Test
	public void testPutMultiMapOfKEYVAL() {
		Map map1 = new HashMap<Integer, Collection>();
		BasicMultiMap.List map2 = new BasicMultiMap.List();
		Collection list = new ArrayList<Integer>();
		for (int i = 0; i < 100000; i++) {
			list.add(i % 5);
			map1.put(i, list);
			if (i % 5 == 0)
				list.clear();
		}
		map2.putMulti(map1);
	}

	@Test
	public void testValuesMulti() {
		HashSet<Integer> list = new HashSet<Integer>();
		BasicMultiMap.List<Integer, Integer> map = new BasicMultiMap.List<Integer, Integer>();
		for (int i = 0; i < 100000; i++) {
			list.add(i);
			map.putMulti(i % 73, i);
		}
		for (Object n : map.valuesMulti())
			assertTrue(list.remove(n));
		assertTrue(list.isEmpty());
	}

	@Test
	public void testSetCollectionGenerator() {
		BasicMultiMap map = new BasicMultiMap.List();
	}

	@Test
	public void testGetCollectionGenerator() {
		ObjectGeneratorForClass gen = new BasicObjectGeneratorForClass<ArrayList>(null, null, ArrayList.class);
		BasicMultiMap map = new BasicMultiMap.List.List();
	}

	@Test
	public void testRemoveMulti() {
		BasicMultiMap.List<Integer, Integer> map = new BasicMultiMap.List<Integer, Integer>();
		for (int i = 0; i < 100000; i++)
			map.putMulti(i % 73, i);
		for (int i = 0; i < 100000; i++)
			assertTrue(map.removeMultiAndKeyIfEmpty(i % 73, i));
		assertTrue(map.isEmpty());

	}

	@Test
	public void testGetMulti() {
		Map map1 = new HashMap<Integer, Collection>();
		ArrayList list = new ArrayList<Integer>();
		BasicMultiMap map = new BasicMultiMap.List(map1);
		for (int i = 0; i < 1000; i++) {
			list.add(i % 5);
			if (i % 5 == 0)
				list.clear();
			map.putMulti(i, list);
			assertEquals(list, map.getMulti(i));
		}
	}

}
