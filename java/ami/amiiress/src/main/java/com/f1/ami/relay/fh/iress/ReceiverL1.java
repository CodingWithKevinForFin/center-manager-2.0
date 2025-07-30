package com.f1.ami.relay.fh.iress;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.relay.fh.iress.AmiIressFH.Handler;
import com.f1.utils.LH;
import com.feedos.api.core.Any;
import com.feedos.api.core.PolymorphicInstrumentCode;
import com.feedos.api.requests.Constants;
import com.feedos.api.requests.InstrumentQuotationData;
import com.feedos.api.requests.ListOfTagValue;
import com.feedos.api.requests.QuotationTradeCancelCorrection;
import com.feedos.api.requests.QuotationTradeEventExt;
import com.feedos.api.requests.Receiver_Quotation_SubscribeInstrumentsL1;

public class ReceiverL1 implements Receiver_Quotation_SubscribeInstrumentsL1 {
	PolymorphicInstrumentCode[] instrumentCodes;
	private static final Logger Log = LH.get();
	private Handler messagehandler;

	HashSet<Integer> trackedTags = new HashSet<Integer>();
	HashMap<Integer, InstrumentQuotationData> trackedData = new HashMap<>();
	HashMap<Integer, ListOfTagValue> referentialData = new HashMap<>();

	public ReceiverL1(PolymorphicInstrumentCode[] instrumentCodes, Handler messageHandler, HashSet<Integer> trackedTags, HashMap<Integer, ListOfTagValue> referentialData) {
		this.instrumentCodes = instrumentCodes;
		this.messagehandler = messageHandler;
		this.trackedTags = trackedTags;
		this.referentialData = referentialData;
	}

	public void quotSubscribeInstrumentsL1Response(int subscription_num, Object user_context, int rc, InstrumentQuotationData[] result) {
		LH.info(Log, "L1 subscription started. Sub ID: " + subscription_num + " Status: " + Constants.getErrorCodeName(rc));
		for (int i = 0; i < result.length; i++) {
			result[i].setTrackedTags(trackedTags);
			result[i].enableTrackUpdates();
			Map<String, Object> v = parseMessage(result[i]);
			messagehandler.sendMessage("iress", null, v, System.currentTimeMillis());
		}
	}

	public void quotSubscribeInstrumentsL1UnsubNotif(int subscription_num, Object user_context, int rc) {
		LH.info(Log, "Subscription Stopped. Return Code: " + Constants.getErrorCodeName(rc));
	}

	public void quotNotifTradeEventExt(int subscription_num, Object user_context, int instrument_code, long server_timestamp, long market_timestamp,
			QuotationTradeEventExt trade_event_ext) {
		InstrumentQuotationData qd = trackedData.get(instrument_code);
		if (qd != null) {
			qd.resetTracking();
			qd.update_with_TradeEventExt(server_timestamp, market_timestamp, trade_event_ext);
			if (qd.getUpdatedTags() != null) {
				Map<String, Object> v = parseMessage(qd);
				messagehandler.sendMessage("iress", null, v, System.currentTimeMillis());
			}
		}
	}

	public void quotNotifTradeCancelCorrection(int subscription_num, Object user_context, int instrument_code, long server_timestamp, QuotationTradeCancelCorrection data) {
		InstrumentQuotationData qd = trackedData.get(instrument_code);
		if (qd != null)
			qd.update_with_TradeCancelCorrection(server_timestamp, data);
	}

	public Map<String, Object> parseMessage(InstrumentQuotationData data) {
		Map<String, Object> m = new HashMap<>();
		String symbol = referentialData.get(data.getInstrumentCode().get_internal_code()).getTagByNumber(Constants.TAG_Symbol).get_string();
		String description = referentialData.get(data.getInstrumentCode().get_internal_code()).getTagByNumber(Constants.TAG_Description).get_string();

		m.put("Symbol", symbol);
		m.put("Description", description);

		//Ask and Bid are done in this manner instead of tracking tags per Iress docs
		for (int i = 0; i < data.getOrderBook().getAskSide().getDepth(); i++) {
			m.put("AskPrice" + (i + 1), data.getOrderBook().getAskSide().getPrice(i));
			m.put("AskQty" + (i + 1), data.getOrderBook().getAskSide().getQty(i));

		}

		for (int i = 0; i < data.getOrderBook().getBidSide().getDepth(); i++) {
			m.put("BidPrice" + (i + 1), data.getOrderBook().getBidSide().getPrice(i));
			m.put("BidQty" + (i + 1), data.getOrderBook().getBidSide().getQty(i));

		}

		trackedData.put(data.getInstrumentCode().get_internal_code(), data);
		for (Integer tag : this.trackedTags) {
			if (data.getTagByNumber(tag) != null) {
				m.put(Constants.getTagName(tag), parseType(data.getTagByNumber(tag)));
			}
		}
		return m;
	}

	public Object parseType(Any val) {
		final int STRING_VAL = Any.SYNTAX_String;
		final int FLOAT_VAL = Any.SYNTAX_float64;
		final int INT_VAL = Any.SYNTAX_int;
		final int BOOLEAN_VAL = Any.SYNTAX_bool;
		final int CHAR_VAL = Any.SYNTAX_char;
		final int ENUM_VAL = Any.SYNTAX_Enum;
		final int TIMESTAMP_VAL = Any.SYNTAX_Timestamp;
		final int UINT64_VAL = Any.SYNTAX_uint64;
		final int UINT32_VAL = Any.SYNTAX_uint32;
		final int UINT16_VAL = Any.SYNTAX_uint16;
		final int UINT8_VAL = Any.SYNTAX_uint8;
		final int INT32_VAL = Any.SYNTAX_int32;
		final int INT16_VAL = Any.SYNTAX_int16;
		final int INT8_VAL = Any.SYNTAX_int8;
		final int UNKNOWN_VAL = Any.SYNTAX_UNKNOWN;

		switch (val.getSyntax()) {
			case STRING_VAL:
				return val.get_string();
			case INT_VAL:
				return val.get_int();
			case INT32_VAL:
				return val.get_int32();
			case INT16_VAL:
				return val.get_int16();
			case INT8_VAL:
				return val.get_int8();
			case UINT64_VAL:
				return val.get_uint64();
			case UINT32_VAL:
				return val.get_uint32();
			case UINT16_VAL:
				return val.get_uint16();
			case UINT8_VAL:
				return val.get_uint8();
			case FLOAT_VAL:
				return val.get_float64();
			case BOOLEAN_VAL:
				return val.get_bool();
			case CHAR_VAL:
				return val.get_char();
			case ENUM_VAL:
				return val.get_enum();
			case TIMESTAMP_VAL:
				return val.get_timestamp();
			case UNKNOWN_VAL:
				return val.get_string();
			default:
				return val.get_string();
		}
	}

}