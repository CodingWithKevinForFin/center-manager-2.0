package com.f1.coinbase;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArrayBuilder;

import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;

public class AmiCoinbaseUtils {
	public static Logger log = LH.get(AmiCoinbaseUtils.class);
	// coinbase socket
	public static String CB_SOCKET_ENDPOINT = "wss://ws-feed.exchange.coinbase.com";
	public static String PRODUCT_IDS = "ETH-USD,ETH-EUR";
	public static String CHANNELS = "full,ticker"; // can be comma delimited

	// ami rt
	public static final byte OPTION_AUTO_PROCESS_INCOMING = 2;
	public static final int TOTAL_ROWS = 100000;

	public static Map<String, Byte> MSG_TYPE_2_ID = new HasherMap<String, Byte>();

	public static final String MSG_TYPE_OPEN = "open";
	public static final String MSG_TYPE_RECEIVED = "received";
	public static final String MSG_TYPE_DONE = "done";
	public static final String MSG_TYPE_MATCH = "match";
	public static final String MSG_TYPE_TICKER = "ticker";

	public static final byte MSG_TYPE_ID_OPEN = 1;
	public static final byte MSG_TYPE_ID_RECEIVED = 2;
	public static final byte MSG_TYPE_ID_DONE = 3;
	public static final byte MSG_TYPE_ID_MATCH = 4;
	public static final byte MSG_TYPE_ID_TICKER = 5;

	static {
		MSG_TYPE_2_ID.put(MSG_TYPE_OPEN, MSG_TYPE_ID_OPEN);
		MSG_TYPE_2_ID.put(MSG_TYPE_RECEIVED, MSG_TYPE_ID_RECEIVED);
		MSG_TYPE_2_ID.put(MSG_TYPE_DONE, MSG_TYPE_ID_DONE);
		MSG_TYPE_2_ID.put(MSG_TYPE_MATCH, MSG_TYPE_ID_MATCH);
		MSG_TYPE_2_ID.put(MSG_TYPE_TICKER, MSG_TYPE_ID_TICKER);
	}

	public static byte getMsgTypeId(String msgType) {
		return MSG_TYPE_2_ID.get(msgType);
	}
	public static JsonArrayBuilder getChannels() {
		List<String> channels = SH.splitToList(",", AmiCoinbaseUtils.CHANNELS);
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		for (String channel : channels)
			arrayBuilder.add(channel);
		return arrayBuilder;
	}
	public static String getSubscribeMessage() {
		return Json.createObjectBuilder().add("type", "subscribe").add("product_ids", getProductIds()).add("channels", getChannels()).build().toString();
	}
	public static JsonArrayBuilder getProductIds() {
		List<String> productIds = SH.splitToList(",", AmiCoinbaseUtils.PRODUCT_IDS);
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		for (String productId : productIds)
			arrayBuilder.add(productId);
		return arrayBuilder;

	}
	public static Long parseDateTimeString(String dateTimeString) {
		try {
			Instant ins = Instant.parse(dateTimeString);
			return ins.getEpochSecond() * 1000;
		} catch (Exception e) {
			LH.warning(log, "could not parse datetime string ", dateTimeString);
			return null;
		}
	}
	public static void main(String[] args) {
		Long t = parseDateTimeString("2021-10-14T18:58:19.635200Z");
		System.out.println(t);
	}
}
