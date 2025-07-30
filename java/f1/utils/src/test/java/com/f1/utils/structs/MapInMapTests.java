package com.f1.utils.structs;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

public class MapInMapTests {

	@Test
	public void test() {
		MapInMap<Integer, Integer, String> mim = new MapInMap<Integer, Integer, String>();
		mim.putMulti(1, 2, "12");
		mim.putMulti(2, 2, "22");
		mim.putMulti(3, 4, "22");
		mim.putMulti(3, 6, "25");
		mim.putMulti(3, 7, "26");
		Random r = new Random(321);
		for (int i = 0; i < 100; i++) {
			mim.putMulti(r.nextInt() % 10, r.nextInt() % 20, Integer.toString(r.nextInt()));
		}
		mim.removeMulti(2, 2);
		System.out.println(mim);
		MapInMap<Integer, Integer, String> mim2 = new MapInMap<Integer, Integer, String>();
		for (Tuple3<Integer, Integer, String> i : mim.entrySetMulti()) {
			mim2.putMulti(i.getA(), i.getB(), i.getC());
			System.out.println(i);
		}
		assertEquals(mim, mim2);
	}
	@Test
	public void test2() {
		MapInMapInMap<Integer, Integer, Integer, String> mim = new MapInMapInMap<Integer, Integer, Integer, String>();
		mim.putMulti(1, 2, 4, "12");
		mim.putMulti(2, 2, 4, "22");
		mim.putMulti(3, 4, 2, "22");
		mim.putMulti(3, 6, 4, "25");
		mim.putMulti(3, 7, 3, "26");
		mim.putMulti(3, 7, 5, "27");
		Random r = new Random(321);
		mim.removeMulti(2, 2, 4);
		System.out.println(mim);
		MapInMapInMap<Integer, Integer, Integer, String> mim2 = new MapInMapInMap<Integer, Integer, Integer, String>();
		for (Tuple4<Integer, Integer, Integer, String> i : mim.entrySetMulti()) {
			mim2.putMulti(i.getA(), i.getB(), i.getC(), i.getD());
			System.out.println(i);
		}
		assertEquals(mim, mim2);
	}

}
