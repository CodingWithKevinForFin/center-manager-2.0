import java.util.Map;

import com.f1.ami.client.AmiClient;
import com.f1.ami.client.AmiClientListener;
import com.f1.utils.OH;

public class AmiSimMain implements AmiClientListener {

	private AmiClient client;

	public AmiSimMain(String host, int port, String appname) {
		this.client = new AmiClient();
		client.addListener(this);
		client.start(host, port, appname, AmiClient.LOG_CONNECTION_RETRY_ERRORS);
		while (true) {
			OH.sleep(1000);
		}
	}
	public static void main(String a[]) {
		new AmiSimMain(a[0], Integer.parseInt(a[1]), a[2]);
	}
	@Override
	public void onMessageReceived(AmiClient source, long now, long seqnum, int status, CharSequence message) {
	}
	@Override
	public void onMessageSent(AmiClient source, CharSequence message) {
	}
	@Override
	public void onConnect(AmiClient source) {
	}
	@Override
	public void onDisconnect(AmiClient source) {
	}
	@Override
	public void onCommand(AmiClient source, String requestId, String cmd, String userName, String objectType, String objectId, Map<String, Object> params) {
		client.startResponseMessage(requestId, AmiClient.RESPONSE_STATUS_UPDATE_RECORD, "");
		client.addMessageParamString("Status", "closed");
		client.sendMessage();
		client.startObjectMessage("Admin Message", null, 0).addMessageParamString("msg", "Order " + objectId + " was closed by: " + userName)
				.addMessageParamString("comment", (String) params.get("comment")).sendMessageAndFlush();
	}
	@Override
	public void onLoggedIn(AmiClient rawAmiClient) {
		//		client.startCommandDefinition("CMD", 2, "Close Order", "Are you sure you want to cancel this order?", "{timeout:3000,form:{inputs:[{label:\"comment\",var:\"comment\"}]}}",
		//				"T==\"Order-Single\"");
		client.sendMessageAndFlush();
	}
}
