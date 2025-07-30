/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.agg;

import org.junit.Ignore;
import org.junit.Test;

import junit.framework.Assert;

public class LongAggregatorTest {

	@Test
	public void TestLongAggregatorCtor() {
		LongAggregator aggregator = new LongAggregator();
		Assert.assertNotNull(aggregator);
	}

	@Test
	public void TestLongAggregatorCtor2() {
		LongAggregator aggregator = new LongAggregator(4L, 100L);
		Assert.assertNotNull(aggregator);
	}

	@Test
	public void TestLongAggregatorReset() {
		LongAggregator aggregator = new LongAggregator(4L, 100L);
		aggregator.clear();
		Assert.assertEquals(0, aggregator.getMax());
		Assert.assertEquals(0, aggregator.getMin());
		Assert.assertEquals(0, aggregator.getCount());
		Assert.assertEquals(0, aggregator.getTotal());
		Assert.assertEquals(Double.NaN, aggregator.getRunningAverage());
		Assert.assertNotNull(aggregator);
	}

	@Test
	public void TestLongAggregatorAdd() {
		LongAggregator aggregator = new LongAggregator();
		long val = 2L;
		Assert.assertEquals(val, aggregator.add(val));
	}

	@Test
	public void TestLongAggregatorAdd2() {
		LongAggregator aggregator = new LongAggregator();
		long val = 2L;
		long val2 = 5L;
		aggregator.add(val);
		Assert.assertEquals(5L, aggregator.add(val2));
	}

	@Test
	public void TestLongAggregatorGetRunningAverage() {
		LongAggregator aggregator = new LongAggregator(1L, 2L, 3L);
		Assert.assertEquals(3.0, aggregator.getRunningAverage());
	}

	@Test
	public void TestLongAggregatorGetRunningAverage2() {
		LongAggregator aggregator = new LongAggregator();
		aggregator.setRunningAverageMaxSamples(3);
		aggregator.add(1L, 2L, 3L);
		Assert.assertEquals(String.format("%.3f", 1.750),
				String.format("%.3f", aggregator.getRunningAverage()));
	}

	@Test
	public void TestLongAggregatorGetAverage() {
		LongAggregator aggregator = new LongAggregator(1L, 2L, 3L);
		Assert.assertEquals(String.format("%.2f", 2.0),
				String.format("%.2f", aggregator.getAverage()));
	}

	@Test
	public void TestLongAggregatorGetAverage2() {
		LongAggregator aggregator = new LongAggregator();
		Assert.assertEquals(Double.NaN, aggregator.getAverage());
	}

	@Test
	public void TestLongAggregatorGetMax() {
		LongAggregator aggregator = new LongAggregator(1L, 2L, 3L);
		Assert.assertEquals(3L, aggregator.getMax());
	}

	@Test
	public void TestLongAggregatorGetMax2() {
		LongAggregator aggregator = new LongAggregator();
		Assert.assertEquals(Long.MAX_VALUE, aggregator.getMax());
	}

	@Test
	public void TestLongAggregatorGetMin() {
		LongAggregator aggregator = new LongAggregator(1L, 2L, 3L);
		Assert.assertEquals(1L, aggregator.getMin());
	}

	@Test
	public void TestLongAggregatorGetMin2() {
		LongAggregator aggregator = new LongAggregator();
		Assert.assertEquals(Long.MIN_VALUE, aggregator.getMin());
	}

	@Test
	public void TestLongAggregatorGetCount() {
		LongAggregator aggregator = new LongAggregator(1L, 2L, 3L);
		Assert.assertEquals(3, aggregator.getCount());
	}

	@Test
	public void TestLongAggregatorGetCount2() {
		LongAggregator aggregator = new LongAggregator();
		Assert.assertEquals(0, aggregator.getCount());
	}

	@Test
	public void TestLongAggregatorGetTotal() {
		LongAggregator aggregator = new LongAggregator(1L, 2L, 3L);
		Assert.assertEquals(1L + 2L + 3L, aggregator.getTotal());
	}

	@Test
	public void TestLongAggregatorGetTotal2() {
		LongAggregator aggregator = new LongAggregator();
		Assert.assertEquals(0L, aggregator.getTotal());
	}

	@Test
	public void TestLongAggregatorToString() {
		LongAggregator aggregator = new LongAggregator(1L, 2L, 3L);
		Assert.assertNotNull(aggregator.toString());
	}

	@Test
	public void TestLongAggregatorToString2() {
		LongAggregator aggregator = new LongAggregator();
		Assert.assertEquals("(count=0)", aggregator.toString());
	}

	@Test
	public void TestLongAggregatorGetSetRunningAverageMaxSamples() {
		LongAggregator aggregator = new LongAggregator(1L, 2L, 3L);
		aggregator.setRunningAverageMaxSamples(10);
		Assert.assertEquals(10, aggregator.getRunningAverageMaxSamples());
	}

	@Ignore("TODO: Fix")
	@Test
	public void TestLongAggregatorGetMiddle() {
		LongAggregator aggregator = new LongAggregator(5L, 2L, 3L);
		Assert.assertEquals(String.format("%.4f", 3.5),
				String.format("%.4f", aggregator.getMiddle()));
	}

	@Ignore("TODO: Fix")
	@Test
	public void TestLongAggregatorGetMiddle2() {
		LongAggregator aggregator = new LongAggregator();
		Assert.assertEquals(Double.NaN, aggregator.getMiddle());
	}

}
