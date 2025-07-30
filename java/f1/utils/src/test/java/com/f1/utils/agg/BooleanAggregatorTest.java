/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.agg;

import org.junit.Test;

import junit.framework.Assert;

public class BooleanAggregatorTest {

	@Test
	public void BooleanAggregatorTestCtor() {
		BooleanAggregator agg = new BooleanAggregator();
		Assert.assertNotNull(agg);
	}

	@Test
	public void BooleanAggregatorTestAdd() {
		BooleanAggregator agg = new BooleanAggregator();
		agg.add(true);
		agg.add(false);
		Assert.assertEquals(1, agg.getCountTrue());
		Assert.assertEquals(1, agg.getCountFalse());
		Assert.assertEquals(0, agg.getCountNull());
		Assert.assertEquals(2, agg.getCount());
		Assert.assertEquals(true, agg.hasTrue());
		Assert.assertEquals(true, agg.hasFalse());
		Assert.assertEquals(true, agg.hasBoth());
		Assert.assertEquals(false, agg.hasNull());
	}

	@Test
	public void BooleanAggregatorTestAdd2() {
		BooleanAggregator agg = new BooleanAggregator();
		agg.add(new Boolean(true));
		agg.add(new Boolean(false));
		agg.add((Boolean) null);
		Assert.assertEquals(1, agg.getCountTrue());
		Assert.assertEquals(1, agg.getCountFalse());
		Assert.assertEquals(1, agg.getCountNull());
		Assert.assertEquals(3, agg.getCount());
		Assert.assertEquals(true, agg.hasTrue());
		Assert.assertEquals(true, agg.hasFalse());
		Assert.assertEquals(true, agg.hasBoth());
		Assert.assertEquals(true, agg.hasNull());
	}

}
