package com.f1.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.f1.base.ValuedEnum;
import com.f1.base.ValuedEnumCache;

public class ValuedEnumHelperTests {

	@Test
	public void test() {
		assertEquals(Tst.ONE, ValuedEnumCache.getEnumValue(Tst.class, "1"));
		assertEquals(Tst.TWO, ValuedEnumCache.getEnumValue(Tst.class, "2"));
		assertEquals(Tst.THREE, ValuedEnumCache.getEnumValue(Tst.class, "3"));
	}

	@Test
	public void test2() {
		assertEquals(Tst2.ONE, ValuedEnumCache.getEnumValue(Tst2.class, '1'));
		assertEquals(Tst2.TWO, ValuedEnumCache.getEnumValue(Tst2.class, '2'));
		assertEquals(Tst2.THREE, ValuedEnumCache.getEnumValue(Tst2.class, '3'));
	}

	@Test
	public void test3() {
		assertEquals(Tst3.ONE, ValuedEnumCache.getEnumValue(Tst3.class, 1));
		assertEquals(Tst3.TWO, ValuedEnumCache.getEnumValue(Tst3.class, 2));
		assertEquals(Tst3.THREE, ValuedEnumCache.getEnumValue(Tst3.class, 3));
	}

	@Test
	public void testDefault() {
		assertEquals(Tst2.ONE, ValuedEnumCache.getEnumValue(Tst2.class, 4, Tst2.ONE));
		assertEquals(null, ValuedEnumCache.getEnumValue(Tst2.class, 4, null));
	}

	@Test
	public void test4() {
		assertEquals(Tst4.ONE, Tst4.get(1));
		assertEquals(Tst4.ONE, Tst4.get(1, Tst4.TWO));

		assertEquals(Tst4.THREE, Tst4.get(3));
		assertEquals(Tst4.THREE, Tst4.get(3, Tst4.TWO));

		assertEquals(Tst4.NEG_THREE, Tst4.get(-3));
		assertEquals(Tst4.NEG_THREE, Tst4.get(-3, Tst4.TWO));

		assertEquals(Tst4.TWO_K, Tst4.get(2000));
		assertEquals(Tst4.TWO_K, Tst4.get(2000, Tst4.TWO));

		assertEquals(Tst4.TWO, Tst4.get(2001, Tst4.TWO));
		assertEquals(Tst4.TWO, Tst4.get(4, Tst4.TWO));
		assertEquals(Tst4.TWO, Tst4.get(-200, Tst4.TWO));
	}

	@Test(expected = Exception.class)
	public void test4Throw() {
		assertEquals(Tst4.TWO, Tst4.get(-200));
	}

	@Test(expected = Exception.class)
	public void test4Throw2() {
		assertEquals(Tst4.TWO, Tst4.get(2001));
	}
	@Test(expected = Exception.class)
	public void testMissing() {
		assertEquals(Tst2.ONE, ValuedEnumCache.getEnumValue(Tst2.class, 4));
	}

	@Test
	public void test5() {
		assertEquals(Tst5.ONE, Tst5.get('1'));
		assertEquals(Tst5.ONE, Tst5.get('1', Tst5.TWO));

		assertEquals(Tst5.THREE, Tst5.get('3'));
		assertEquals(Tst5.THREE, Tst5.get('3', Tst5.TWO));

	}

	public static enum Tst implements ValuedEnum<String> {
		ONE("1"),
		TWO("2"),
		THREE("3");

		public final String value;

		private Tst(String value) {
			this.value = value;
		}

		@Override
		public String getEnumValue() {
			return value;
		}

	}

	public static enum Tst2 implements ValuedEnum<Character> {
		ONE('1'),
		TWO('2'),
		THREE('3');

		public final Character value;

		private Tst2(Character value) {
			this.value = value;
		}

		@Override
		public Character getEnumValue() {
			return value;
		}

	}

	public static enum Tst3 implements ValuedEnum<Integer> {
		ONE(1),
		TWO(2),
		THREE(3);

		public final Integer value;

		private Tst3(Integer value) {
			this.value = value;
		}

		@Override
		public Integer getEnumValue() {
			return value;
		}

	}

	public static enum Tst4 implements ValuedEnum<Integer> {
		ONE(1),
		TWO(2),
		THREE(3),
		TWO_K(2000),
		NEG_THREE(-3);

		private static ValuedEnumCache<Integer, Tst4> cache;
		public final Integer value;

		private Tst4(Integer value) {
			this.value = value;
		}

		@Override
		public Integer getEnumValue() {
			return value;
		}

		static {
			cache = ValuedEnumCache.getCache(Tst4.class);
		}

		public static Tst4 get(int value) {
			return cache.getValueByPrimitive(value);
		}
		public static Tst4 get(int value, Tst4 dflt) {
			return cache.getValueByPrimitive(value, dflt);
		}

	}

	public static enum Tst5 implements ValuedEnum<Character> {
		ONE('1'),
		TWO('2'),
		THREE('3');

		private static ValuedEnumCache<Character, Tst5> cache;
		public final char value;

		private Tst5(char value) {
			this.value = value;
		}

		@Override
		public Character getEnumValue() {
			return value;
		}

		static {
			cache = ValuedEnumCache.getCache(Tst5.class);
		}

		public static Tst5 get(char value) {
			return cache.getValueByPrimitive(value);
		}
		public static Tst5 get(char value, Tst5 dflt) {
			return cache.getValueByPrimitive(value, dflt);
		}

	}
}

