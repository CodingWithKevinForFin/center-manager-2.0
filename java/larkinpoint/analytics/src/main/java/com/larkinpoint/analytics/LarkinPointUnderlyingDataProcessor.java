package com.larkinpoint.analytics;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.utils.BasicDay;
import com.f1.utils.IOH;
import com.f1.utils.db.DbService;
import com.larkinpoint.analytics.state.OptionUnderlying;
import com.larkinpoint.messages.GetUnderlyingDataBySymbolDateRequest;
import com.larkinpoint.messages.GetUnderlyingDataBySymbolDateResponse;
import com.larkinpoint.messages.UnderlyingMessage;

public class LarkinPointUnderlyingDataProcessor extends BasicRequestProcessor<GetUnderlyingDataBySymbolDateRequest, LarkinPointState, GetUnderlyingDataBySymbolDateResponse> {

	public LarkinPointUnderlyingDataProcessor() {
		super(GetUnderlyingDataBySymbolDateRequest.class, LarkinPointState.class, GetUnderlyingDataBySymbolDateResponse.class);
	}
	private void GetSecurityInfo(LarkinPointState state) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Getting Security information");

		final DbService dbservice = (DbService) getServices().getService("OPTIONSDB");
		try {
			final Connection connection = dbservice.getConnection();
			final Map<Object, Object> params = new HashMap<Object, Object>();
			ResultSet result = dbservice.executeQuery("query_ivy_security_data", params, connection);
			while (result.next()) {
				state.getOptionDataRoot().addSecurity(result.getLong("security_id"), result.getString("ticker"), result.getInt("valor"), result.getString("issuer"),
						result.getString("isin"), result.getString("cusip"), result.getInt("expiry_dayofweek"), result.getInt("expiry_weekofmonth"));
			}
			state.getOptionDataRoot().setSecurityMapLoaded(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	protected GetUnderlyingDataBySymbolDateResponse processRequest(RequestMessage<GetUnderlyingDataBySymbolDateRequest> request, LarkinPointState state, ThreadScope threadScope)
			throws Exception {
		GetUnderlyingDataBySymbolDateResponse response = nw(GetUnderlyingDataBySymbolDateResponse.class);
		GetUnderlyingDataBySymbolDateRequest action = request.getAction();
		final BasicDay qdate1 = (BasicDay) action.getQuoteDate1();
		final BasicDay qdate2 = (BasicDay) action.getQuoteDate2();
		final String underlyingSymbol = action.getUnderlyingSymbol();
		if (state.getOptionDataRoot().isSecurityMapLoaded() == false) {
			GetSecurityInfo(state);	
			queryDatabase(underlyingSymbol, state);
		}
		else if (state.getOptionDataRoot().getUnderlying(underlyingSymbol).isNeedsRefresh()){
			queryDatabase(underlyingSymbol, state);
		}
		
		List<UnderlyingMessage> list = new ArrayList<UnderlyingMessage>();
		state.getOptionDataRoot().buildUnderlyingList(getServices().getGenerator(), list, underlyingSymbol, qdate1, qdate2);

		response.setUnderlyingData(list);

		return response;
	}
	@Override
	public void init() {
		super.init();

	}
	public void queryDatabase(String underlyingSymbol, LarkinPointState state) throws Exception {
		System.out.println("Getting Underlying Data for :" + underlyingSymbol);

		final DbService dbservice = (DbService) getServices().getService("OPTIONSDB");
		final Connection connection = dbservice.getConnection();

		try {
			final Map<Object, Object> params = new HashMap<Object, Object>();

			long securityId = state.getOptionDataRoot().getSecurityInfo(underlyingSymbol).getSecurityID();
			params.put("security_id", securityId);
			ResultSet result = dbservice.executeQuery("query_ivy_security_price_data", params, connection);
			processRecords(underlyingSymbol, result, state);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			IOH.close(connection);
		}

		// TODO Auto-generated method stub
		System.out.println("Finished querying Underlying Data for " + underlyingSymbol);

	}
	public void processRecords(String underlyingSymbol, ResultSet quotes, LarkinPointState state) throws SQLException {

		OptionUnderlying u = state.getOptionDataRoot().getUnderlying(underlyingSymbol);

		TimeZone tz = getServices().getLocaleFormatter().getTimeZone();

		int count = 0;
		while (quotes.next()) {
			UnderlyingMessage underlying = nw(UnderlyingMessage.class);

			underlying.setQuoteDate(new BasicDay(tz, (Date) quotes.getDate("quote_date")));
			underlying.setVolume(quotes.getLong("volume"));
			underlying.setOpen(quotes.getDouble("open"));
			underlying.setClose(quotes.getDouble("close"));
			underlying.setBid(quotes.getDouble("bid"));
			underlying.setAsk(quotes.getDouble("ask"));
			underlying.setSecurityId(quotes.getLong("security_id"));
			underlying.setSymbol(state.getOptionDataRoot().getSecurityInfo(underlying.getSecurityId()).getTicker());
			underlying.setTotalReturn(quotes.getDouble("total_return"));

			u.addRecord(underlying);
			underlying.lock();
			count++;
		}
		u.setNeedsRefresh(false);
		System.out.println("Processed " + count + " records");

	}

}
