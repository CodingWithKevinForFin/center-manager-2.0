package com.larkinpoint.analytics;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.utils.BasicDay;
import com.larkinpoint.messages.ActionMessage;

public class LarkinPointCalculateReturnsProcessor extends BasicProcessor<ActionMessage, LarkinPointState> {

	public LarkinPointCalculateReturnsProcessor() {
		super(ActionMessage.class, LarkinPointState.class);
	}

	@Override
	public void init() {
		super.init();

	}

	@Override
	public void processAction(ActionMessage action, LarkinPointState state, ThreadScope threadScope) throws Exception {
		// TODO Auto-generated method stub
		final String qdate1 = action.getTradeDate();
		TimeZone tz = getServices().getLocaleFormatter().getTimeZone();
		SimpleDateFormat sdfSource = new SimpleDateFormat("MM/dd/yyyy");

		BasicDay date1 = new BasicDay(tz, sdfSource.parse(qdate1));

	}
}
