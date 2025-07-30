package com.larkinpoint.analytics;

import java.util.ArrayList;
import java.util.List;

import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.utils.BasicDay;
import com.f1.utils.CH;
import com.larkinpoint.messages.GetOptionDataByOptionIDRequest;
import com.larkinpoint.messages.GetOptionDataResponse;
import com.larkinpoint.messages.SpreadMessage;

public class LarkinPointOptionDataByIDProcessor extends BasicRequestProcessor<GetOptionDataByOptionIDRequest, LarkinPointState, GetOptionDataResponse> {

	public LarkinPointOptionDataByIDProcessor() {
		super(GetOptionDataByOptionIDRequest.class, LarkinPointState.class, GetOptionDataResponse.class);
	}

	@Override
	protected GetOptionDataResponse processRequest(RequestMessage<GetOptionDataByOptionIDRequest> request, LarkinPointState state, ThreadScope threadScope) throws Exception {
		GetOptionDataResponse response = nw(GetOptionDataResponse.class);
		GetOptionDataByOptionIDRequest action = request.getAction();
		final String underlyingSymbol = action.getUnderlyingSymbol();
		long leg1 = action.getLeg1OptionId();
		long leg2 = action.getLeg2OptionId();
		BasicDay day1 = (BasicDay) action.getQuoteDate1();
		BasicDay day2 = (BasicDay) action.getQuoteDate2();

		ArrayList<SpreadMessage> spreads = new ArrayList<SpreadMessage>();
		state.getOptionDataRoot().buildOptionList(getServices().getGenerator(), spreads, underlyingSymbol, leg1, leg2, day1, day2);
		List<List<SpreadMessage>> batch = CH.batchSublists(spreads, 10000, true);
		for (int i = 0; i < batch.size() - 1; i++) {
			GetOptionDataResponse response1 = nw(GetOptionDataResponse.class);
			response1.setOptionData(batch.get(i));
			ResultMessage result1 = nw(ResultMessage.class);
			result1.setAction(response1);
			result1.setIsIntermediateResult(true);
			this.reply(request, result1, threadScope);

		}
		response.setOptionData(CH.last(batch));

		return response;
	}
	@Override
	public void init() {
		super.init();

	}
}
