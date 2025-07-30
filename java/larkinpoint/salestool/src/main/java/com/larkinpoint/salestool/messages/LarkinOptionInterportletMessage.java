package com.larkinpoint.salestool.messages;

import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.utils.structs.LongSet;

public class LarkinOptionInterportletMessage implements InterPortletMessage {
	private LongSet optionIds;
	private String symbol;

	public LarkinOptionInterportletMessage(LongSet ids, String symbol) {
		// TODO Auto-generated constructor stub
		this.optionIds = ids;
		this.symbol = symbol;
	}
	public LongSet getOptionIds() {
		return optionIds;
	}
	public String getSymbol() {
		return symbol;
	}

}
