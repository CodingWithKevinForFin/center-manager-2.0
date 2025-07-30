package com.f1.sample;

import com.f1.base.Action;
import com.f1.container.Partition;
import com.f1.container.Processor;
import com.f1.container.impl.BasicStateGenerator;

public class SampleStateGenerator extends BasicStateGenerator<SampleState> {

	public SampleStateGenerator() {
		super(SampleState.class);
	}

	@Override
	public SampleState createState(Partition partition, Action a, Processor<? extends Action, ?> processor) {
		return new SampleState(1000);
	}
}
