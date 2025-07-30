package com.f1.utils.impl;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.f1.utils.structs.table.BasicTable;

public class PerfTest {

	private Map<String, PerfTestInstance> perfTests = new LinkedHashMap<String, PerfTestInstance>();
	private int runtimeInSeconds;

	public PerfTest(int runtimeSeconds) {
		this.runtimeInSeconds = runtimeSeconds;
	}

	public PerfTestInstance test(String name) {
		PerfTestInstance r = new PerfTestInstance(name, runtimeInSeconds);
		CH.putOrThrow(perfTests, r.getName(), r);
		return r;
	}

	public void printStats() {
		BasicTable t = new BasicTable(new String[] { "Rank", "Name", "Diff To Prior", "Per Interval", "Interval", "Executed", "Duration(millis)" });
		double min = Double.MAX_VALUE;
		for (PerfTestInstance i : this.perfTests.values()) {
			min = Math.min(i.getRunsPerSecond(), min);
		}
		int interval = 1;
		while (min * interval < 10)
			interval *= 10;
		int rank = this.perfTests.size();
		DecimalFormat nf = new DecimalFormat("#,###.##");
		double last = Double.NaN;
		for (PerfTestInstance i : CH.sort(this.perfTests.values())) {

			String diffOfPrior = "n/a";
			if (last == last) {
				diffOfPrior = nf.format(i.getRunsPerSecond() / last) + "x";
			}
			t.getRows().addRow("#" + rank, i.getName(), diffOfPrior, SH.comma((long) i.getRunsPerSecond() * interval), interval + " seconds", SH.comma(i.getRunTotal()),
					SH.comma(i.getRunDuration() / 1000000));
			last = i.getRunsPerSecond();
			rank--;
		}
		System.out.println(TableHelper.toString(t, "", TableHelper.SHOW_ALL_BUT_TYPES));
	}

}
