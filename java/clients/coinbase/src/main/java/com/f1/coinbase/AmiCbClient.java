package com.f1.coinbase;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import com.f1.ami.client.AmiClient;
import com.f1.ami.client.AmiClientListener;
import com.f1.bootstrap.Bootstrap;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

@WebSocket
public class AmiCbClient implements AmiClientListener {
	public static final Logger log = LH.get(AmiCbClient.class);
	public static String PROPERTY_AMI_RT_HOST;
	public static int PROPERTY_AMI_RT_PORT;
	public static String PROPERTY_AMI_RT_USER;
	public static String PROPERTY_AMI_CB_ENDPOINT;
	public static Long PROPERTY_AMI_RT_ROW_EXPIRATION;

	private AmiClient client;

	public AmiCbClient(AmiClient client) {
		this.client = client;
	}
	public void connectToSocket() {
		WebSocketClient socketClient = new WebSocketClient(new SslContextFactory());
		try {
			socketClient.start();
			URI echoUri = new URI(AmiCoinbaseUtils.CB_SOCKET_ENDPOINT);
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			socketClient.connect(this, echoUri, request);
			LH.info(log, "connected to coinbase socket");
		} catch (Throwable t) {
			LH.warning(log, "failed to connect to coinbase socket");
			t.printStackTrace();
		}
	}
	@OnWebSocketConnect
	public void onConnect(Session session) throws IOException {
		session.getRemote().sendString(AmiCoinbaseUtils.getSubscribeMessage());
	}
	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		LH.warning(log, "websocket has been closed, status code: " + statusCode + " reason: " + reason);
	}
	@OnWebSocketMessage
	public void OnWebSocketMessage(String msg) {
		Map<String, Object> msgObj = (Map<String, Object>) ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject(msg);
		String msgType = (String) msgObj.get("type");
		Byte id = AmiCoinbaseUtils.MSG_TYPE_2_ID.getOrDefault(msgType, (byte) 0);
		switch (id) {
			case AmiCoinbaseUtils.MSG_TYPE_ID_OPEN:
				streamMsgTypeOpen(msgObj);
				break;
			case AmiCoinbaseUtils.MSG_TYPE_ID_RECEIVED:
				streamMsgTypeReceived(msgObj);
				break;
			case AmiCoinbaseUtils.MSG_TYPE_ID_DONE:
				streamMsgTypeDone(msgObj);
				break;
			case AmiCoinbaseUtils.MSG_TYPE_ID_MATCH:
				streamMsgTypeMatch(msgObj);
				break;
			case AmiCoinbaseUtils.MSG_TYPE_ID_TICKER:
				streamMsgTypeTicker(msgObj);
				break;
			case 0:
			default:
				LH.warning(log, "unknown message type: " + msgType);
				break;
		}
	}
	public void streamCommonMsgParams(Map<String, Object> msgObj) {
		this.client.addMessageParamString("side", (String) msgObj.get("side"));
		this.client.addMessageParamString("product_id", (String) msgObj.get("product_id"));
		Long time = Caster_Long.INSTANCE.cast(AmiCoinbaseUtils.parseDateTimeString((String) msgObj.get("time")));
		this.client.addMessageParamLong("time", time);
		this.client.addMessageParamLong("sequence", Caster_Long.INSTANCE.cast(msgObj.get("sequence")));
		this.client.addMessageParamDouble("price", Caster_Double.INSTANCE.cast(msgObj.get("price")));
		this.client.addMessageParamLong("E", PROPERTY_AMI_RT_ROW_EXPIRATION); // add expire for all message types for now
	}
	public void streamMsgTypeOpen(Map<String, Object> msgObj) {
		this.client.startObjectMessage(AmiCoinbaseUtils.MSG_TYPE_OPEN, null);
		streamCommonMsgParams(msgObj);
		this.client.addMessageParamString("order_id", (String) msgObj.get("order_id"));
		this.client.addMessageParamDouble("remaining_size", Caster_Double.INSTANCE.cast(msgObj.get("remaining_size")));
		this.client.sendMessageAndFlush();

	}
	public void streamMsgTypeReceived(Map<String, Object> msgObj) {
		this.client.startObjectMessage(AmiCoinbaseUtils.MSG_TYPE_RECEIVED, null);
		streamCommonMsgParams(msgObj);
		this.client.addMessageParamString("order_id", (String) msgObj.get("order_id"));
		this.client.addMessageParamString("order_type", (String) msgObj.get("order_type"));
		this.client.addMessageParamDouble("size", Caster_Double.INSTANCE.cast(msgObj.get("size")));
		this.client.addMessageParamString("client_oid", (String) msgObj.get("client_oid"));
		this.client.sendMessageAndFlush();
	}
	public void streamMsgTypeDone(Map<String, Object> msgObj) {
		this.client.startObjectMessage(AmiCoinbaseUtils.MSG_TYPE_DONE, null);
		streamCommonMsgParams(msgObj);
		this.client.addMessageParamString("order_id", (String) msgObj.get("order_id"));
		this.client.addMessageParamString("reason", (String) msgObj.get("reason"));
		this.client.addMessageParamDouble("remaining_size", Caster_Double.INSTANCE.cast(msgObj.get("remaining_size")));
		this.client.sendMessageAndFlush();
	}
	public void streamMsgTypeTicker(Map<String, Object> msgObj) {
		this.client.startObjectMessage(AmiCoinbaseUtils.MSG_TYPE_TICKER, null);
		streamCommonMsgParams(msgObj);
		this.client.addMessageParamDouble("open_24h", Caster_Double.INSTANCE.cast(msgObj.get("open_24h")));
		this.client.addMessageParamDouble("volume_24h", Caster_Double.INSTANCE.cast(msgObj.get("volume_24h")));
		this.client.addMessageParamDouble("low_24h", Caster_Double.INSTANCE.cast(msgObj.get("low_24h")));
		this.client.addMessageParamDouble("high_24h", Caster_Double.INSTANCE.cast(msgObj.get("high_24h")));
		this.client.addMessageParamDouble("volume_30d", Caster_Double.INSTANCE.cast(msgObj.get("volume_30d")));
		this.client.addMessageParamDouble("best_bid", Caster_Double.INSTANCE.cast(msgObj.get("best_bid")));
		this.client.addMessageParamDouble("best_ask", Caster_Double.INSTANCE.cast(msgObj.get("best_ask")));
		this.client.addMessageParamLong("trade_id", Caster_Long.INSTANCE.cast(msgObj.get("trade_id")));
		this.client.addMessageParamDouble("last_size", Caster_Double.INSTANCE.cast(msgObj.get("last_size")));
		this.client.sendMessageAndFlush();
	}
	public void streamMsgTypeMatch(Map<String, Object> msgObj) {
		this.client.startObjectMessage(AmiCoinbaseUtils.MSG_TYPE_MATCH, null);
		streamCommonMsgParams(msgObj);
		this.client.addMessageParamLong("trade_id", Caster_Long.INSTANCE.cast(msgObj.get("trade_id")));
		this.client.addMessageParamString("maker_order_id", (String) msgObj.get("maker_order_id"));
		this.client.addMessageParamString("taker_order_id", (String) msgObj.get("taker_order_id"));
		this.client.addMessageParamDouble("size", Caster_Double.INSTANCE.cast(msgObj.get("size")));
		this.client.sendMessageAndFlush();
	}
	public void onMessageReceived(AmiClient rawClient, long now, int seqnum, int status, CharSequence message) {
	}
	public void onMessageSent(AmiClient rawClient, CharSequence message) {
	}
	public void onConnect(AmiClient rawClient) {
	}
	public void onDisconnect(AmiClient rawClient) {
	}
	public void onCommand(AmiClient rawClient, String requestId, String cmd, String userName, String objectType, String objectId, Map<String, Object> params) {
	}
	public void onLoggedIn(AmiClient rawClient) {
	}
	public static void main(String[] args) {
		Bootstrap bootstrap = new Bootstrap(AmiCbClient.class, args);
		bootstrap.startup();
		PropertyController properties = bootstrap.getProperties();
		PROPERTY_AMI_RT_HOST = properties.getRequired("ami.rt.host");
		PROPERTY_AMI_RT_PORT = Caster_Integer.INSTANCE.cast(properties.getRequired("ami.rt.port"));
		PROPERTY_AMI_RT_USER = properties.getRequired("ami.rt.user");
		PROPERTY_AMI_CB_ENDPOINT = properties.getRequired("ami.cb.endpoint");
		PROPERTY_AMI_RT_ROW_EXPIRATION = Caster_Long.INSTANCE.cast(properties.getOptional("ami.rt.row.expiration", 0L));

		AmiClient client = new AmiClient();
		AmiCbClient cbClient = new AmiCbClient(client);
		client.addListener(cbClient);
		client.start(PROPERTY_AMI_RT_HOST, PROPERTY_AMI_RT_PORT, PROPERTY_AMI_RT_USER, AmiCoinbaseUtils.OPTION_AUTO_PROCESS_INCOMING);
		LH.info(log, "connected to AMI Realtime Streaming API");
		cbClient.connectToSocket();
		while (true) {
			OH.sleep(1000);
		}
	}
}
