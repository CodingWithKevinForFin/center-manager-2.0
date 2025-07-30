package com.larkinpoint.salestool;

import java.util.Set;

import com.f1.base.Action;
import com.f1.container.ResultMessage;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.PortletService;
import com.f1.utils.CH;
import com.larkinpoint.messages.ActionMessage;
import com.larkinpoint.messages.GetOptionDataResponse;
import com.larkinpoint.messages.GetUnderlyingDataBySymbolDateResponse;
import com.larkinpoint.messages.LoadFileMessage;
import com.larkinpoint.messages.SpreadMessage;

public class LarkinPointWebService implements PortletService {

	private PortletManager manager;
	private Set<Class<? extends Action>> interestedMessages = (Set) CH.s(GetOptionDataResponse.class, GetUnderlyingDataBySymbolDateResponse.class, LoadFileMessage.class,
			ActionMessage.class, SpreadMessage.class);

	public LarkinPointWebService(PortletManager manager) {
		this.manager = manager;
	}

	@Override
	public String getServiceId() {
		return "LARKIN";
	}

	@Override
	public void onBackendAction(Action action) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBackendResponse(ResultMessage<Action> action) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<Class<? extends Action>> getInterestedBackendMessages() {
		return interestedMessages;
	}

	@Override
	public void close() {

	}

}
