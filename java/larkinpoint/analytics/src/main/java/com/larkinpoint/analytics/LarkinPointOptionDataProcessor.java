package com.larkinpoint.analytics;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import com.f1.base.Day;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.utils.BasicDay;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.SH;
import com.f1.utils.db.DbService;
import com.larkinpoint.analytics.state.OptionDataRoot;
import com.larkinpoint.analytics.state.OptionDataRoot.SecurityInfo;
import com.larkinpoint.messages.GetOptionDataBySymbolDateRequest;
import com.larkinpoint.messages.GetOptionDataResponse;
import com.larkinpoint.messages.OptionMessage;
import com.larkinpoint.messages.SpreadMessage;
import com.larkinpoint.messages.UnderlyingMessage;

public class LarkinPointOptionDataProcessor extends BasicRequestProcessor<GetOptionDataBySymbolDateRequest, LarkinPointState, GetOptionDataResponse> {

	public LarkinPointOptionDataProcessor() {
		super(GetOptionDataBySymbolDateRequest.class, LarkinPointState.class, GetOptionDataResponse.class);

	}

	@Override
	protected GetOptionDataResponse processRequest(RequestMessage<GetOptionDataBySymbolDateRequest> request, LarkinPointState state, ThreadScope threadScope) throws Exception {
		GetOptionDataResponse response = nw(GetOptionDataResponse.class);
		GetOptionDataBySymbolDateRequest action = request.getAction();
		final String underlyingSymbol = action.getUnderlyingSymbol();
		final BasicDay qdate1 = action.getQuoteDate1();
		final BasicDay qdate2 = action.getQuoteDate2();
		//	TimeZone tz = getServices().getLocaleFormatter().getTimeZone();
		//	SimpleDateFormat sdfSource = new SimpleDateFormat("yyyyMMdd");

		//	BasicDay date1 = new BasicDay(tz, sdfSource.parse(qdate1));
		//	BasicDay date2 = new BasicDay(tz, sdfSource.parse(qdate2));

		if (action.getQueryDatabase() == true)
			queryDatabase(underlyingSymbol, qdate1, qdate2, state);

		List<SpreadMessage> list = new ArrayList<SpreadMessage>();
		state.getOptionDataRoot().buildOptionList(getServices().getGenerator(), list, underlyingSymbol, qdate1, qdate2, action.getStrikeCount(), action.getMaxDaysToExpiry(),
				action.getMinDaysToExpiry());

		List<List<SpreadMessage>> batch = CH.batchSublists(list, 10000, true);
		for (int i = 0; i < batch.size() - 1; i++) {
			GetOptionDataResponse response1 = nw(GetOptionDataResponse.class);
			response1.setOptionData(batch.get(i));
			ResultMessage result1 = nw(ResultMessage.class);
			result1.setAction(response1);
			result1.setIsIntermediateResult(true);
			this.reply(request, result1, threadScope);

		}
		if (batch.size() > 0)
			response.setOptionData(CH.last(batch));

		return response;
	}
	@Override
	public void init() {
		super.init();

	}
	public void queryDatabase(String underlyingSymbol, Day qdate1, Day qdate2, LarkinPointState state) throws Exception {
		System.out.println("Getting Option Data for :" + underlyingSymbol + " between Dates " + qdate1 + " and " + qdate2);

		final DbService dbservice = (DbService) getServices().getService("OPTIONSDB");
		final Connection connection = dbservice.getConnection();
		SimpleDateFormat sdfSource = new SimpleDateFormat("MM/dd/yyyy");

		//	Date date1 = sdfSource.parse(qdate1);
		//	Date date2 = sdfSource.parse(qdate2);

		try {
			final Map<Object, Object> params = new HashMap<Object, Object>();
			long securityId = state.getOptionDataRoot().getSecurityInfo(underlyingSymbol).getSecurityID();
			params.put("security_id", securityId);
			params.put("qdate1", qdate1.toSqlDate());
			params.put("qdate2", qdate2.toSqlDate());

			ResultSet result = dbservice.executeQuery("query_ivy_daily_option_data", params, connection);

			buildOptionMatrix(underlyingSymbol, result, state);
			//	optionData = DBH.toTable(result);		

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			IOH.close(connection);
		}

		// TODO Auto-generated method stub
		System.out.println("Finished querying Option Data");

	}
	public void buildOptionMatrix(String underlyingSymbol, ResultSet options, LarkinPointState state) throws SQLException {
		OptionDataRoot u = state.getOptionDataRoot();
		TimeZone tz = getServices().getLocaleFormatter().getTimeZone();
		SecurityInfo sec = u.getSecurityInfo(underlyingSymbol);
		Map<String, Integer> dropped = new TreeMap<String, Integer>();

		int count = 0;
		while (options.next()) {
			OptionMessage ov = nw(OptionMessage.class);
			ov.setUnderlying(underlyingSymbol);
			//	String oSymbol = (String) options.getString("option_symbol");
			ov.setExpiry(u.pool(new BasicDay(tz, (Date) options.getDate("expiry"))));
			ov.setTradeDate(u.pool(new BasicDay(tz, (Date) options.getDate("quote_date"))));
			ov.setStrike(options.getFloat("strike_price"));
			ov.setVolume(options.getInt("volume"));
			ov.setLast(options.getFloat("last"));
			ov.setBid(options.getFloat("bid"));
			ov.setAsk(options.getFloat("ask"));
			ov.setVega(options.getFloat("vega"));
			ov.setGamma(options.getFloat("gamma"));
			ov.setDelta(options.getFloat("delta"));
			ov.setTheta(options.getFloat("theta"));
			ov.setImpliedVol(options.getFloat("implied_vol"));
			ov.setCP(options.getInt("cp") == 0 ? false : true);
			ov.setOpenInterest(options.getInt("open_interest"));
			ov.setOptionId(options.getInt("option_id"));
			ov.setDaysToExpiry((int) ((ov.getExpiry().getStartMillis() - ov.getTradeDate().getStartMillis()) / (1000 * 86400)));

			UnderlyingMessage um = u.getUnderlying(underlyingSymbol).getUnderlyingData((BasicDay) ov.getTradeDate());
			if (um == null) {
				dropped.put(SH.join(':', underlyingSymbol, ov.getTradeDate()), ov.getOptionId());

			} else {
				ov.setUnderlyingClose((float) um.getClose());
				u.addRecord(ov);
				count++;
			}

		}
		System.out.println("Processed " + count + " records");
		for (String entry : dropped.keySet()) {
			String[] values;
			values = SH.split(':', entry);
			System.out.println("No underlying data for " + values[0] + " on TradeDate " + values[1] + " " + dropped.get(entry));
		}

	}
}
