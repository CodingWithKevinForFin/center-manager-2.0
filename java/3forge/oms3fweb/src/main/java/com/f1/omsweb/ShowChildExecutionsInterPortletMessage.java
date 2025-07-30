package com.f1.omsweb;

import java.util.List;

import com.f1.suite.web.portal.InterPortletMessage;

public class ShowChildExecutionsInterPortletMessage implements InterPortletMessage {

	final private List<WebOmsOrder> showExecutions, hideExecutions;

	public ShowChildExecutionsInterPortletMessage(List<WebOmsOrder> showExecutions, List<WebOmsOrder> hideExecutions) {
		this.showExecutions = showExecutions;
		this.hideExecutions = hideExecutions;
	}

	public List<WebOmsOrder> getShowExecutions() {
		return showExecutions;
	}

	public List<WebOmsOrder> getHideExecutions() {
		return hideExecutions;
	}

}
