/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.agg;

import org.junit.Test;

import junit.framework.Assert;

public class IntegerAggregatorTest {

	@Test
	public void TestIntegerAggregatorCtor() {
		IntegerAggregator bd = new IntegerAggregator();
		Assert.assertNotNull(bd);
	}

	@Test
	public void TestIntegerAggregatorCtor2() {
		IntegerAggregator bd = new IntegerAggregator(4, 100);
		Assert.assertNotNull(bd);
	}

	@Test
	public void TestIntegerAggregatorClear() {
		IntegerAggregator bd = new IntegerAggregator(4, 100);
		bd.clear();
		Assert.assertEquals(0, bd.getMax());
		Assert.assertEquals(0, bd.getMin());
		Assert.assertEquals(0, bd.getCount());
		Assert.assertEquals(0, bd.getTotal());
		Assert.assertEquals(Double.NaN, bd.getAverage());
		Assert.assertNotNull(bd);
	}

	@Test
	public void TestIntegerAggregatorAdd() {
		IntegerAggregator bd = new IntegerAggregator();
		int val = 2;
		Assert.assertEquals(val, bd.add(val));
	}

	@Test
	public void TestIntegerAggregatorAdd2() {
		IntegerAggregator bd = new IntegerAggregator();
		int val = 2;
		int val2 = 5;
		bd.add(val);
		Assert.assertEquals(5, bd.add(val2));
	}

	@Test
	public void TestIntegerAggregatorGetAverage() {
		IntegerAggregator bd = new IntegerAggregator(1, 2, 3);
		Assert.assertEquals(2.0, bd.getAverage());
	}

	@Test
	public void TestIntegerAggregatorGetAverage2() {
		IntegerAggregator bd = new IntegerAggregator();
		Assert.assertEquals(Double.NaN, bd.getAverage());
	}

	@Test
	public void TestIntegerAggregatorGetMax() {
		IntegerAggregator bd = new IntegerAggregator(1, 2, 3);
		Assert.assertEquals(3, bd.getMax());
	}

	@Test
	public void TestIntegerAggregatorGetMax2() {
		IntegerAggregator bd = new IntegerAggregator();
		Assert.assertEquals(Integer.MAX_VALUE, bd.getMax());
	}

	@Test
	public void TestIntegerAggregatorGetMin() {
		IntegerAggregator bd = new IntegerAggregator(1, 2, 3);
		Assert.assertEquals(1, bd.getMin());
	}

	@Test
	public void TestIntegerAggregatorGetMin2() {
		IntegerAggregator bd = new IntegerAggregator();
		Assert.assertEquals(Integer.MIN_VALUE, bd.getMin());
	}

	@Test
	public void TestIntegerAggregatorGetCount() {
		IntegerAggregator bd = new IntegerAggregator(1, 2, 3);
		Assert.assertEquals(3, bd.getCount());
	}

	@Test
	public void TestIntegerAggregatorGetCount2() {
		IntegerAggregator bd = new IntegerAggregator();
		Assert.assertEquals(0, bd.getCount());
	}

	@Test
	public void TestIntegerAggregatorGetTotal() {
		IntegerAggregator bd = new IntegerAggregator(1, 2, 3);
		Assert.assertEquals(1 + 2 + 3, bd.getTotal());
	}

	@Test
	public void TestIntegerAggregatorGetTotal2() {
		IntegerAggregator bd = new IntegerAggregator();
		Assert.assertEquals(0, bd.getTotal());
	}

	@Test
	public void TestIntegerAggregatorToString() {
		IntegerAggregator bd = new IntegerAggregator(1, 2, 3);
		Assert.assertNotNull(bd.toString());
	}

	@Test
	public void TestIntegerAggregatorToString2() {
		IntegerAggregator bd = new IntegerAggregator();
		Assert.assertEquals("(count=0)", bd.toString());
	}

}
