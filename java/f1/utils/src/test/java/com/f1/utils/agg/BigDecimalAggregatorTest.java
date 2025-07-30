/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.agg;

import java.math.BigDecimal;

import org.junit.Test;

import junit.framework.Assert;

public class BigDecimalAggregatorTest {

	@Test
	public void TestBigDecimalAggregatorCtor() {
		BigDecimalAggregator bd = new BigDecimalAggregator();
		Assert.assertNotNull(bd);
	}

	@Test
	public void TestBigDecimalAggregatorCtor2() {
		BigDecimalAggregator bd = new BigDecimalAggregator(new BigDecimal(0.4), new BigDecimal(100));
		Assert.assertNotNull(bd);
	}

	@Test
	public void TestBigDecimalAggregatorAdd() {
		BigDecimalAggregator bd = new BigDecimalAggregator();
		Assert.assertNull(bd.add((BigDecimal) null));
	}

	@Test
	public void TestBigDecimalAggregatorAdd2() {
		BigDecimalAggregator bd = new BigDecimalAggregator();
		BigDecimal val = new BigDecimal(2.34567);
		Assert.assertEquals(val, bd.add(val));
	}

	@Test
	public void TestBigDecimalAggregatorAdd3() {
		BigDecimalAggregator bd = new BigDecimalAggregator();
		BigDecimal val = new BigDecimal(2.34);
		BigDecimal val2 = new BigDecimal(0.5);
		bd.add(val);
		Assert.assertEquals(new BigDecimal(0.5), bd.add(val2));
	}

	@Test
	public void TestBigDecimalAggregatorGetRunningAverage() {
		BigDecimalAggregator bd = new BigDecimalAggregator(new BigDecimal(0.1), new BigDecimal(0.2),
				new BigDecimal(0.3));
		Assert.assertEquals(new BigDecimal(0.1), bd.getRunningAverage());
	}

	@Test
	public void TestBigDecimalAggregatorGetRunningAverage2() {
		BigDecimalAggregator bd = new BigDecimalAggregator();
		bd.setRunningAverageMaxSamples(3);
		bd.add(new BigDecimal(0.1), new BigDecimal(0.2), new BigDecimal(0.3));
		Assert.assertEquals(String.format("%.3f", new BigDecimal(0.175).floatValue()),
				String.format("%.3f", bd.getRunningAverage().floatValue()));
	}

	@Test
	public void TestBigDecimalAggregatorGetAverage() {
		BigDecimalAggregator bd = new BigDecimalAggregator(new BigDecimal(0.1), new BigDecimal(0.2),
				new BigDecimal(0.3));
		Assert.assertEquals(String.format("%.2f", new BigDecimal(0.2).floatValue()),
				String.format("%.2f", bd.getAverage().floatValue()));
	}

	@Test(expected = ArithmeticException.class)
	public void TestBigDecimalAggregatorGetAverage2() {
		BigDecimalAggregator bd = new BigDecimalAggregator((BigDecimal) null);
		Assert.assertEquals(new BigDecimal(0.2), bd.getAverage());
	}

	@Test
	public void TestBigDecimalAggregatorGetMax() {
		BigDecimalAggregator bd = new BigDecimalAggregator(new BigDecimal(0.1), new BigDecimal(0.2),
				new BigDecimal(0.3));
		Assert.assertEquals(new BigDecimal(0.3), bd.getMax());
	}

	@Test
	public void TestBigDecimalAggregatorGetMax2() {
		BigDecimalAggregator bd = new BigDecimalAggregator((BigDecimal) null);
		Assert.assertEquals(null, bd.getMax());
	}

	@Test
	public void TestBigDecimalAggregatorGetMin() {
		BigDecimalAggregator bd = new BigDecimalAggregator(new BigDecimal(0.1), new BigDecimal(0.2),
				new BigDecimal(0.3));
		Assert.assertEquals(new BigDecimal(0.1), bd.getMin());
	}

	@Test
	public void TestBigDecimalAggregatorGetMin2() {
		BigDecimalAggregator bd = new BigDecimalAggregator((BigDecimal) null);
		Assert.assertEquals(null, bd.getMin());
	}

	@Test
	public void TestBigDecimalAggregatorGetCount() {
		BigDecimalAggregator bd = new BigDecimalAggregator(new BigDecimal(0.1), new BigDecimal(0.2),
				new BigDecimal(0.3));
		Assert.assertEquals(3, bd.getCount());
	}

	@Test
	public void TestBigDecimalAggregatorGetCount2() {
		BigDecimalAggregator bd = new BigDecimalAggregator((BigDecimal) null);
		Assert.assertEquals(0, bd.getCount());
	}

	@Test
	public void TestBigDecimalAggregatorGetTotal() {
		BigDecimalAggregator bd = new BigDecimalAggregator(new BigDecimal(0.1), new BigDecimal(0.2),
				new BigDecimal(0.3));
		Assert.assertEquals(String.format("%.4f", new BigDecimal(0.1 + 0.2 + 0.3)),
				String.format("%.4f", bd.getTotal()));
	}

	@Test
	public void TestBigDecimalAggregatorGetTotal2() {
		BigDecimalAggregator bd = new BigDecimalAggregator((BigDecimal) null);
		Assert.assertEquals(BigDecimal.ZERO, bd.getTotal());
	}

	@Test
	public void TestBigDecimalAggregatorToString() {
		BigDecimalAggregator bd = new BigDecimalAggregator(new BigDecimal(0.1), new BigDecimal(0.2),
				new BigDecimal(0.3));
		Assert.assertNotNull(bd.toString());
	}

	@Test
	public void TestBigDecimalAggregatorToString2() {
		BigDecimalAggregator bd = new BigDecimalAggregator((BigDecimal) null);
		Assert.assertEquals("(count=0)", bd.toString());
	}

	@Test
	public void TestBigDecimalAggregatorGetSetRunningAverageMaxSamples() {
		BigDecimalAggregator bd = new BigDecimalAggregator(new BigDecimal(0.1), new BigDecimal(0.2),
				new BigDecimal(0.3));
		bd.setRunningAverageMaxSamples(10);
		Assert.assertEquals(10, bd.getRunningAverageMaxSamples());
	}

}
