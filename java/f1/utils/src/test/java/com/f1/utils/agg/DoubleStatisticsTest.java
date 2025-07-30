package com.f1.utils.agg;

import org.junit.Test;

import com.f1.utils.DoubleArrayList;
import com.f1.utils.MH;

import junit.framework.Assert;

public final class DoubleStatisticsTest {

	@Test
	public void TestDoubleStatisticsCtor() {
		DoubleStatistics stats = new DoubleStatistics();
		Assert.assertNotNull(stats);
	}

	@Test
	public void TestDoubleStatisticsCtor2() {
		DoubleStatistics stats = new DoubleStatistics(0.0, 1.0, -1.0, 2.0);
		Assert.assertNotNull(stats);
	}

	@Test
	public void TestDoubleStatisticsReset() {
		DoubleStatistics bd = new DoubleStatistics(0.4, 100);
		bd.reset();
		Assert.assertEquals(Double.NaN, bd.getMax());
		Assert.assertEquals(Double.NaN, bd.getMin());
		Assert.assertEquals(0, bd.getCount());
		Assert.assertEquals(0d, bd.getTotal());
		Assert.assertEquals(Double.NaN, bd.getRunningAverage());
		Assert.assertNotNull(bd);
	}

	@Test
	public void TestDoubleStatisticsAdd() {
		DoubleStatistics bd = new DoubleStatistics();
		double val = 2.34567;
		Assert.assertEquals(val, bd.add(val));
	}

	@Test
	public void TestDoubleStatisticsAdd2() {
		DoubleStatistics bd = new DoubleStatistics();
		double val = 2.34;
		double val2 = 0.5;
		bd.add(val);
		Assert.assertEquals(0.5, bd.add(val2));
	}

	@Test
	public void TestDoubleStatisticsGetRunningAverage() {
		DoubleStatistics bd = new DoubleStatistics(0.1, 0.2, 0.3);
		Assert.assertEquals(0.3, bd.getRunningAverage());
	}

	@Test
	public void TestDoubleStatisticsGetRunningAverage2() {
		DoubleStatistics bd = new DoubleStatistics();
		bd.setRunningAverageMaxSamples(3);
		bd.add(0.1, 0.2, 0.3);
		Assert.assertEquals(String.format("%.3f", 0.175),
				String.format("%.3f", bd.getRunningAverage()));
	}

	@Test
	public void TestDoubleStatisticsGetAverage() {
		DoubleStatistics bd = new DoubleStatistics(0.1, 0.2, 0.3);
		Assert.assertEquals(String.format("%.2f", 0.2),
				String.format("%.2f", bd.getAverage()));
	}

	@Test
	public void TestDoubleStatisticsGetAverage2() {
		DoubleStatistics bd = new DoubleStatistics();
		Assert.assertEquals(Double.NaN, bd.getAverage());
	}

	@Test
	public void TestDoubleStatisticsGetMax() {
		DoubleStatistics bd = new DoubleStatistics(0.1, 0.2, 0.3);
		Assert.assertEquals(0.3, bd.getMax());
	}

	@Test
	public void TestDoubleStatisticsGetMax2() {
		DoubleStatistics bd = new DoubleStatistics();
		Assert.assertEquals(Double.NaN, bd.getMax());
	}

	@Test
	public void TestDoubleStatisticsGetMin() {
		DoubleStatistics bd = new DoubleStatistics(0.1, 0.2, 0.3);
		Assert.assertEquals(0.1, bd.getMin());
	}

	@Test
	public void TestDoubleStatisticsGetMin2() {
		DoubleStatistics bd = new DoubleStatistics();
		Assert.assertEquals(Double.NaN, bd.getMin());
	}

	@Test
	public void TestDoubleStatisticsGetCount() {
		DoubleStatistics bd = new DoubleStatistics(0.1, 0.2, 0.3);
		Assert.assertEquals(3, bd.getCount());
	}

	@Test
	public void TestDoubleStatisticsGetCount2() {
		DoubleStatistics bd = new DoubleStatistics();
		Assert.assertEquals(0, bd.getCount());
	}

	@Test
	public void TestDoubleStatisticsGetTotal() {
		DoubleStatistics bd = new DoubleStatistics(0.1, 0.2, 0.3);
		Assert.assertEquals(String.format("%.4f", 0.1 + 0.2 + 0.3),
				String.format("%.4f", bd.getTotal()));
	}

	@Test
	public void TestDoubleStatisticsGetTotal2() {
		DoubleStatistics bd = new DoubleStatistics();
		Assert.assertEquals(0.0, bd.getTotal());
	}

	@Test
	public void TestDoubleStatisticsGetSetRunningAverageMaxSamples() {
		DoubleStatistics bd = new DoubleStatistics(0.1, 0.2, 0.3);
		bd.setRunningAverageMaxSamples(10);
		Assert.assertEquals(10, bd.getRunningAverageMaxSamples());
	}

	@Test
	public void TestDoubleStatisticsGetRange() {
		DoubleStatistics bd = new DoubleStatistics(0.5, 0.2, 0.3);
		Assert.assertEquals(String.format("%.4f", 0.3),
				String.format("%.4f", bd.getRange()));
	}

	@Test
	public void TestDoubleStatisticsGetRange2() {
		DoubleStatistics bd = new DoubleStatistics();
		Assert.assertEquals(String.format("%.4f", 0.0),
				String.format("%.4f", bd.getRange()));
	}

	@Test
	public void TestDoubleStatisticsGetMiddle() {
		DoubleStatistics bd = new DoubleStatistics(0.5, 0.2, 0.3);
		Assert.assertEquals(String.format("%.4f", 0.35),
				String.format("%.4f", bd.getMiddle()));
	}

	@Test
	public void TestDoubleStatisticsGetMiddle2() {
		DoubleStatistics bd = new DoubleStatistics();
		Assert.assertEquals(Double.NaN, bd.getMiddle());
	}

	@Test
	public void TestDoubleStatisticsGetVariance() {
		DoubleStatistics bd = new DoubleStatistics();
		bd.setRunningAverageMaxSamples(3);
		bd.add(0.1, 0.2, 0.3);
		Assert.assertEquals(String.format("%.3f", 0.007),
				String.format("%.3f", bd.getVariance()));
	}

	@Test
	public void TestDoubleStatisticsGetVariance2() {
		DoubleStatistics bd = new DoubleStatistics();
		Assert.assertEquals(Double.NaN, bd.getVariance());
	}

	@Test
	public void TestDoubleStatisticsGetStdev() {
		DoubleStatistics bd = new DoubleStatistics();
		bd.setRunningAverageMaxSamples(3);
		bd.add(0.1, 0.2, 0.3);
		Assert.assertEquals(String.format("%.4f", Math.sqrt(bd.getVariance())),
				String.format("%.4f", bd.getStdev()));
	}

	@Test
	public void TestDoubleStatisticsGetStdev2() {
		DoubleStatistics bd = new DoubleStatistics();
		Assert.assertEquals(Double.NaN, bd.getStdev());
	}

}
