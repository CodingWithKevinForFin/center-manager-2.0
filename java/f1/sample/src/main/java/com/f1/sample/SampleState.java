package com.f1.sample;

import com.f1.container.impl.BasicState;

public class SampleState extends BasicState {

	private int count;

	public SampleState(int start) {
		this.count = start;
	}

	public int incrementCount(int delta) {
		return count += delta;
	}

}
