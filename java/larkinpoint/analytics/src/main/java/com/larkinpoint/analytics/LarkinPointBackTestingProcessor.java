package com.larkinpoint.analytics;

import java.util.ArrayList;
import java.util.List;

import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.utils.BasicDay;
import com.larkinpoint.messages.GetLarkinBackTestingDataRequest;
import com.larkinpoint.messages.GetOptionDataResponse;
import com.larkinpoint.messages.SpreadMessage;

public class LarkinPointBackTestingProcessor extends BasicRequestProcessor<GetLarkinBackTestingDataRequest, LarkinPointState, GetOptionDataResponse> {

	public LarkinPointBackTestingProcessor() {
		super(GetLarkinBackTestingDataRequest.class, LarkinPointState.class, GetOptionDataResponse.class);
	}

	@Override
	protected GetOptionDataResponse processRequest(RequestMessage<GetLarkinBackTestingDataRequest> request, LarkinPointState state, ThreadScope threadScope) throws Exception {
		GetOptionDataResponse response = nw(GetOptionDataResponse.class);
		GetLarkinBackTestingDataRequest action = request.getAction();
		final String underlyingSymbol = action.getUnderlyingSymbol();
		final BasicDay qdate1 = action.getQuoteDate1();
		final BasicDay qdate2 = action.getQuoteDate2();
		//	TimeZone tz = getServices().getLocaleFormatter().getTimeZone();
		//	SimpleDateFormat sdfSource = new SimpleDateFormat("yyyyMMdd");

		//	BasicDay date1 = new BasicDay(tz, sdfSource.parse(qdate1));
		//	BasicDay date2 = new BasicDay(tz, sdfSource.parse(qdate2));

		List<SpreadMessage> list = new ArrayList<SpreadMessage>();
		//	public void buildTradeList(, float cpRatio, BasicDay firstTradeDate, BasicDay lastTradeDate, int step, int daysLB,
		//			int daysUB, float ratioLB, float ratioUB) 
		state.getOptionDataRoot()
				.getUnderlying(underlyingSymbol)
				.buildStraddleTradeList(getServices().getGenerator(), list, action.getRatio(), qdate1, qdate2, action.getStrikeStep(), action.getMinDaysToExpiry(),
						action.getMaxDaysToExpiry(), action.getRatioLowerBound(), action.getRatioUpperBound(), action.getTradeAmount());
		response.setOptionData(list);

		return response;
	}
	@Override
	public void init() {
		super.init();

	}

}
