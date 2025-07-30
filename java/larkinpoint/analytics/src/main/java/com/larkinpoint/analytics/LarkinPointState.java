package com.larkinpoint.analytics;

import com.f1.container.impl.BasicState;
import com.larkinpoint.analytics.state.OptionDataRoot;

public class LarkinPointState extends BasicState {

	private OptionDataRoot underlyings = new OptionDataRoot();

	public OptionDataRoot getOptionDataRoot() {
		return underlyings;
	}

	public void setUnderlyings(OptionDataRoot underlyings) {
		this.underlyings = underlyings;
	}

}
