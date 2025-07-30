package com.larkinpoint.analytics;

import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.utils.CH;
import com.larkinpoint.analytics.state.OptionUnderlying;
import com.larkinpoint.messages.GetAllTradeDatesRequest;
import com.larkinpoint.messages.GetAllTradeDatesResponse;

public class LarkinPointTradeDatesBySymbolProcessor extends BasicRequestProcessor<GetAllTradeDatesRequest, LarkinPointState, GetAllTradeDatesResponse> {

	public LarkinPointTradeDatesBySymbolProcessor() {
		super(GetAllTradeDatesRequest.class, LarkinPointState.class, GetAllTradeDatesResponse.class);
	}

	@Override
	protected GetAllTradeDatesResponse processRequest(RequestMessage<GetAllTradeDatesRequest> request, LarkinPointState state, ThreadScope threadScope) throws Exception {
		GetAllTradeDatesResponse response = nw(GetAllTradeDatesResponse.class);
		GetAllTradeDatesRequest action = request.getAction();
		final String underlyingSymbol = action.getUnderlyingSymbol();

		OptionUnderlying underlying = state.getOptionDataRoot().getUnderlying(underlyingSymbol);
		if (underlying != null)
			response.setDates(CH.l(underlying.getAllTradeDateKeys()));

		return response;
	}
	@Override
	public void init() {
		super.init();
	}

}
