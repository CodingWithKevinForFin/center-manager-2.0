package com.f1.utils.impl;

import java.util.Iterator;

import com.f1.utils.OH;
import com.f1.utils.SH;

public class PerfTestInstance implements Iterable<Object>, Iterator<Object>, Comparable<PerfTestInstance> {
	static final private byte STAGE_PRE = 0;
	static final private byte STAGE_GUAGE_SPEED = 1;
	static final private byte STAGE_HOTSPOT = 2;
	static final private byte STAGE_RUNNING = 3;
	static final private byte STAGE_DONE = 4;
	private static final long SECOND = 1000 * 1000 * 1000;
	private static final int HOTSPOT_BATCHSIZE = 3;
	private static final int BATCHES_TO_RUN = 10;

	private byte stage = STAGE_PRE;

	final private String name;

	private long stageStart;
	private long stageDur;
	private long stageCount = 0;
	private int batchSize;
	private double persecond;
	private long batchLength;
	private long runDuration;
	private long runTotal;

	@Override
	public Iterator<Object> iterator() {
		return this;
	}

	public PerfTestInstance(String name, int runtimeInSeconds) {
		this.name = name;
		this.batchLength = SECOND * runtimeInSeconds / (BATCHES_TO_RUN + HOTSPOT_BATCHSIZE + 1);
	}

	@Override
	public boolean hasNext() {
		return stage != STAGE_DONE;
	}

	@Override
	public Object next() {
		switch (stage) {
			case STAGE_PRE: {
				moveToStage(STAGE_GUAGE_SPEED);
				System.out.println(name + ": Calculating Batch size and hotspotting for " + HOTSPOT_BATCHSIZE + " batches...");
				this.stageStart = now();
				break;
			}
			case STAGE_GUAGE_SPEED: {
				long dur = now() - this.stageStart;
				stageCount++;
				if (stageCount > 10 && dur > batchLength) {
					double perRun = Math.max(1, dur / stageCount);
					this.batchSize = Math.max(10, (int) (batchLength / perRun));
					moveToStage(STAGE_HOTSPOT);
				}
				break;
			}
			case STAGE_HOTSPOT: {
				stageCount++;
				if (stageCount > this.batchSize * HOTSPOT_BATCHSIZE) {
					moveToStage(STAGE_RUNNING);
				}
				break;
			}
			case STAGE_RUNNING: {
				stageCount++;
				if (stageCount % batchSize == 0) {
					long dur = now() - this.stageStart;
					this.stageDur += dur;
					double persecond = batchSize * SECOND / (dur);
					int cnt = 1;
					while (persecond * cnt < 10)
						cnt *= 10;
					System.out.println(name + ": Run was " + SH.comma((long) persecond * cnt) + " per " + cnt + " second");

					if (stageCount == batchSize * BATCHES_TO_RUN) {
						this.persecond = stageCount * SECOND / (this.stageDur);
						cnt = 1;
						while (persecond * cnt < 10)
							cnt *= 10;
						System.out.println(name + ": Avg was " + SH.comma((long) persecond * cnt) + " per " + cnt + " second  (" + SH.comma(stageCount) + " in "
								+ SH.comma(this.stageDur / 1000000L) + " milliseconds) ");
						this.runDuration = this.stageDur;
						this.runTotal = this.stageCount;
						moveToStage(STAGE_DONE);
					}
					this.stageStart = now();
					break;
				}
			}
		}
		return null;
	}
	private void moveToStage(byte stage) {
		this.stageStart = now();
		this.stageDur = 0;
		this.stage = stage;
		this.stageCount = 0;
	}

	final static private long now() {
		return System.nanoTime();
	}

	@Override
	public int compareTo(PerfTestInstance o) {
		return OH.compare(this.getRunsPerSecond(), o.getRunsPerSecond());
	}

	double getRunsPerSecond() {
		if (stage != PerfTestInstance.STAGE_DONE)
			throw new IllegalStateException();
		return this.persecond;
	}

	public String getName() {
		return this.name;
	}

	public long getRunTotal() {
		if (stage != PerfTestInstance.STAGE_DONE)
			throw new IllegalStateException();
		return this.runTotal;
	}

	public long getRunDuration() {
		if (stage != PerfTestInstance.STAGE_DONE)
			throw new IllegalStateException();
		return this.runDuration;
	}

	@Override
	public void remove() {
	}
}
