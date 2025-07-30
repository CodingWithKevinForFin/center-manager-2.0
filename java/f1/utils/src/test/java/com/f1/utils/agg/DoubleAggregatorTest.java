/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.agg;

import org.junit.Test;

import junit.framework.Assert;

public class DoubleAggregatorTest {

	@Test
	public void TestDoubleAggregatorCtor() {
		DoubleAggregator bd = new DoubleAggregator();
		Assert.assertNotNull(bd);
	}

	@Test
	public void TestDoubleAggregatorCtor2() {
		DoubleAggregator bd = new DoubleAggregator(0.4, 100);
		Assert.assertNotNull(bd);
	}

	@Test
	public void TestDoubleAggregatorReset() {
		DoubleAggregator bd = new DoubleAggregator(0.4, 100);
		bd.reset();
		Assert.assertEquals(Double.NaN, bd.getMax());
		Assert.assertEquals(Double.NaN, bd.getMin());
		Assert.assertEquals(0, bd.getCount());
		Assert.assertEquals(0d, bd.getTotal());
		Assert.assertEquals(Double.NaN, bd.getRunningAverage());
		Assert.assertNotNull(bd);
	}

	@Test
	public void TestDoubleAggregatorAdd() {
		DoubleAggregator bd = new DoubleAggregator();
		double val = 2.34567;
		Assert.assertEquals(val, bd.add(val));
	}

	@Test
	public void TestDoubleAggregatorAdd2() {
		DoubleAggregator bd = new DoubleAggregator();
		double val = 2.34;
		double val2 = 0.5;
		bd.add(val);
		Assert.assertEquals(0.5, bd.add(val2));
	}

	@Test
	public void TestDoubleAggregatorGetRunningAverage() {
		DoubleAggregator bd = new DoubleAggregator(0.1, 0.2, 0.3);
		Assert.assertEquals(0.3, bd.getRunningAverage());
	}

	@Test
	public void TestDoubleAggregatorGetRunningAverage2() {
		DoubleAggregator bd = new DoubleAggregator();
		bd.setRunningAverageMaxSamples(3);
		bd.add(0.1, 0.2, 0.3);
		Assert.assertEquals(String.format("%.3f", 0.175),
				String.format("%.3f", bd.getRunningAverage()));
	}

	@Test
	public void TestDoubleAggregatorGetAverage() {
		DoubleAggregator bd = new DoubleAggregator(0.1, 0.2, 0.3);
		Assert.assertEquals(String.format("%.2f", 0.2),
				String.format("%.2f", bd.getAverage()));
	}

	@Test
	public void TestDoubleAggregatorGetAverage2() {
		DoubleAggregator bd = new DoubleAggregator();
		Assert.assertEquals(Double.NaN, bd.getAverage());
	}

	@Test
	public void TestDoubleAggregatorGetMax() {
		DoubleAggregator bd = new DoubleAggregator(0.1, 0.2, 0.3);
		Assert.assertEquals(0.3, bd.getMax());
	}

	@Test
	public void TestDoubleAggregatorGetMax2() {
		DoubleAggregator bd = new DoubleAggregator();
		Assert.assertEquals(Double.NaN, bd.getMax());
	}

	@Test
	public void TestDoubleAggregatorGetMin() {
		DoubleAggregator bd = new DoubleAggregator(0.1, 0.2, 0.3);
		Assert.assertEquals(0.1, bd.getMin());
	}

	@Test
	public void TestDoubleAggregatorGetMin2() {
		DoubleAggregator bd = new DoubleAggregator();
		Assert.assertEquals(Double.NaN, bd.getMin());
	}

	@Test
	public void TestDoubleAggregatorGetCount() {
		DoubleAggregator bd = new DoubleAggregator(0.1, 0.2, 0.3);
		Assert.assertEquals(3, bd.getCount());
	}

	@Test
	public void TestDoubleAggregatorGetCount2() {
		DoubleAggregator bd = new DoubleAggregator();
		Assert.assertEquals(0, bd.getCount());
	}

	@Test
	public void TestDoubleAggregatorGetTotal() {
		DoubleAggregator bd = new DoubleAggregator(0.1, 0.2, 0.3);
		Assert.assertEquals(String.format("%.4f", 0.1 + 0.2 + 0.3),
				String.format("%.4f", bd.getTotal()));
	}

	@Test
	public void TestDoubleAggregatorGetTotal2() {
		DoubleAggregator bd = new DoubleAggregator();
		Assert.assertEquals(0.0, bd.getTotal());
	}

	@Test
	public void TestDoubleAggregatorToString() {
		DoubleAggregator bd = new DoubleAggregator(0.1, 0.2, 0.3);
		Assert.assertNotNull(bd.toString());
	}

	@Test
	public void TestDoubleAggregatorToString2() {
		DoubleAggregator bd = new DoubleAggregator();
		Assert.assertEquals("(count=0)", bd.toString());
	}

	@Test
	public void TestDoubleAggregatorGetSetRunningAverageMaxSamples() {
		DoubleAggregator bd = new DoubleAggregator(0.1, 0.2, 0.3);
		bd.setRunningAverageMaxSamples(10);
		Assert.assertEquals(10, bd.getRunningAverageMaxSamples());
	}

	@Test
	public void TestDoubleAggregatorGetRange() {
		DoubleAggregator bd = new DoubleAggregator(0.5, 0.2, 0.3);
		Assert.assertEquals(String.format("%.4f", 0.3),
				String.format("%.4f", bd.getRange()));
	}

	@Test
	public void TestDoubleAggregatorGetRange2() {
		DoubleAggregator bd = new DoubleAggregator();
		Assert.assertEquals(String.format("%.4f", 0.0),
				String.format("%.4f", bd.getRange()));
	}

	@Test
	public void TestDoubleAggregatorGetMiddle() {
		DoubleAggregator bd = new DoubleAggregator(0.5, 0.2, 0.3);
		Assert.assertEquals(String.format("%.4f", 0.35),
				String.format("%.4f", bd.getMiddle()));
	}

	@Test
	public void TestDoubleAggregatorGetMiddle2() {
		DoubleAggregator bd = new DoubleAggregator();
		Assert.assertEquals(Double.NaN, bd.getMiddle());
	}

}
