package com.f1.ami.web;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.ami.amicommon.centerclient.AmiCenterClientGetSnapshotRequest;
import com.f1.ami.amicommon.centerclient.AmiCenterClientGetSnapshotResponse;
import com.f1.ami.amicommon.centerclient.AmiCenterClientObjectMessage;
import com.f1.ami.amicommon.centerclient.AmiCenterClientObjectMessages;
import com.f1.ami.amicommon.centerclient.AmiCenterClientSnapshot;
import com.f1.ami.amicommon.msg.AmiCenterRequest;
import com.f1.ami.amicommon.msg.AmiCenterResponse;
import com.f1.ami.amicommon.msg.AmiCenterStatusRequest;
import com.f1.ami.amicommon.msg.AmiCenterStatusResponse;
import com.f1.base.Action;
import com.f1.container.ContainerTools;
import com.f1.container.ResultMessage;
import com.f1.povo.msg.MsgStatusMessage;
import com.f1.suite.web.portal.BackendResponseListener;
import com.f1.suite.web.portal.PortletManager;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiWebSnapshotManager implements BackendResponseListener {
	private static final Logger log = LH.get();

	public static final byte STATE_REQUEST_NOT_SENT = 1;
	public static final byte STATE_REQUEST_SENT = 2;
	public static final byte STATE_CONNECTED = 3;
	public static final byte STATE_DISCONNECTED = 5;
	public static final byte STATE_INIT = 6;
	public static final byte STATE_PREINIT = 7;
	final private PortletManager manager;
	final private AmiWebManager agentManager;
	private byte state = STATE_PREINIT;

	private long snapshotSeqnum;

	private AmiWebService service;

	private String backendName;

	private long stateChangedTime;

	private String processUid;

	private String sessionUid;

	private AmiCenterClientGetSnapshotRequest initialSnapshotRequest;

	public AmiWebSnapshotManager(AmiWebService service, AmiWebManager manager) {
		this.service = service;
		this.manager = service.getPortletManager();
		this.tools = this.manager.getTools();
		this.agentManager = manager;
		this.backendName = "AmiCenter" + this.agentManager.getCenterId();
		this.sessionUid = (String) this.manager.getState().getPartitionId();
		init();
	}

	public void init() {
		OH.assertEq(this.state, STATE_PREINIT);
		this.state = STATE_INIT;
		this.stateChangedTime = System.currentTimeMillis();
		sendRequestToBackend(this, tools.nw(AmiCenterStatusRequest.class), 0);

	}

	// #### PORTLET INTERACTIONS ####
	public void sendRequestToBackend(String portletId, AmiCenterRequest request, int priority) {
		request.setPriority(priority);
		request.setInvokedBy(service.getUserName());
		request.setRequestTime(System.currentTimeMillis());
		LH.fine(log, logMe(), " Portlet, ", portletId, " sending Request to center: ", request.getClass().getName());
		manager.sendRequestToBackend(backendName, portletId, request);
	}
	public void sendRequestToBackend(BackendResponseListener listener, AmiCenterRequest request, int priority) {
		request.setPriority(priority);
		request.setInvokedBy(service.getUserName());
		request.setRequestTime(System.currentTimeMillis());
		LH.fine(log, logMe(), " Sending Request to center: ", request.getClass().getName());
		manager.sendRequestToBackend(this.backendName, listener, request);
	}
	public void sendRequestToBackend(BackendResponseListener listener, AmiCenterClientGetSnapshotRequest request) {
		request.setInvokedBy(service.getUserName());
		request.setSessionUid(this.sessionUid);
		request.setRequestTime(System.currentTimeMillis());
		manager.sendRequestToBackend(this.backendName, listener, request);
	}

	//########### CONNECTION STATE MANAGEMENT ############
	private void onAmiCenterDisconnect(boolean showAlert) {
		this.processUid = null;
		switch (state) {
			case STATE_DISCONNECTED:
				break;
			case STATE_REQUEST_NOT_SENT:
				break;
			case STATE_REQUEST_SENT:
				setState(state);
				break;
			case STATE_CONNECTED:
				setState(STATE_DISCONNECTED);
				if (showAlert && manager != null)
					manager.showAlert("Center disconnected:<P><B> " + this.agentManager.getCenterDef().getDescription());
				break;
		}
	}

	private void onAmiCenterConnect() {
		switch (state) {
			case STATE_DISCONNECTED:
			case STATE_INIT:
			case STATE_REQUEST_NOT_SENT:
				sendAmiCenterSnapshotRequest();
				setState(STATE_REQUEST_SENT);
				break;
			case STATE_REQUEST_SENT://re-send, test track main came up after request was sent.
				sendAmiCenterSnapshotRequest();
				break;
			case STATE_CONNECTED:
				LH.warning(log, logMe(), " Connect w/o disconnect(response received)!");
				break;
		}
	}

	private void onAmiSnapshotResponse(AmiCenterClientGetSnapshotResponse action, AmiCenterClientGetSnapshotRequest request, ResultMessage<Action> result) {
		LH.info(log, logMe(), " Received snapshot ack w/ seqnum ", action.getSeqNum(), " for types: ", request.getAmiObjectTypesToSend(), ", curseqnum=",
				agentManager.getCurrentSeqNum());
	}

	private void sendAmiCenterSnapshotRequest() {
		Set<String> types = agentManager.getAmiObjectTypesBeingViewed();
		AmiCenterClientGetSnapshotRequest req = tools.nw(AmiCenterClientGetSnapshotRequest.class);
		LH.info(log, logMe(), " Sending initial snapshot Request with types:  ", types, " curseqnum=", this.agentManager.getCurrentSeqNum());
		req.setAmiObjectTypesToSend(new HashSet<String>(types));
		this.initialSnapshotRequest = req;
		sendRequestToBackend(this, req);
	}
	public void requestPartialSnapshot(Set<String> types) {
		LH.info(log, logMe(), " Sending partial snapshot Request with types:  ", types, " curseqnum=", this.agentManager.getCurrentSeqNum());
		AmiCenterClientGetSnapshotRequest req = tools.nw(AmiCenterClientGetSnapshotRequest.class);
		req.setAmiObjectTypesToSend(types);
		sendRequestToBackend(this, req);
	}
	private void sendSnapshotRequestIfNecessary() {
		switch (state) {
			case STATE_REQUEST_NOT_SENT:
				sendAmiCenterSnapshotRequest();
				setState(STATE_REQUEST_SENT);
				break;
			case STATE_CONNECTED:
			case STATE_DISCONNECTED:
			case STATE_REQUEST_SENT:
				break;
		}
	}

	public void onInit(PortletManager manager, Map<String, Object> configuration, String rootId) {
		if (state != STATE_DISCONNECTED && state != STATE_INIT && state != STATE_PREINIT) {
			onAmiCenterDisconnect(false);
			setState(STATE_REQUEST_NOT_SENT);
		}
	}
	private void setState(byte state) {
		if (this.state == state)
			return;
		this.state = state;
		this.stateChangedTime = System.currentTimeMillis();
		if (state == STATE_DISCONNECTED)
			snapshotSeqnum = -1;
		this.agentManager.onConnectionStateChanged(state);
	}
	public void onBackendResponse(ResultMessage<Action> result) {
		Action action = result.getAction();
		if (log.isLoggable(Level.FINE))
			LH.fine(log, logMe(), " Received response: ", OH.getSimpleName(action.askSchema().askOriginalType()));
		if (action instanceof AmiCenterStatusResponse) {
			if (((AmiCenterResponse) action).getOk()) {
				if (state == STATE_INIT || state == STATE_DISCONNECTED) {
					onAmiCenterDisconnect(false);
					setState(STATE_REQUEST_NOT_SENT);
					sendSnapshotRequestIfNecessary();
				}
			} else if (state == STATE_INIT)
				setState(STATE_DISCONNECTED);
		} else if (action instanceof AmiCenterClientGetSnapshotResponse) {
			onAmiSnapshotResponse((AmiCenterClientGetSnapshotResponse) action, (AmiCenterClientGetSnapshotRequest) result.getRequestMessage().getAction(), result);
		}
	}
	public void onBackendAction(MsgStatusMessage status) {
		if (!status.getIsConnected()) {
			onAmiCenterDisconnect(true);
		} else {
			onAmiCenterConnect();
		}
	}
	public void onBackendAction(AmiCenterClientObjectMessages action) {
		if (state == STATE_CONNECTED)
			processAmiCenterAction((AmiCenterClientObjectMessages) action);
	}
	public void onBackendAction(AmiCenterClientSnapshot sn) {
		if (sn.getSessionUid() != this.sessionUid)
			return;
		if (state == STATE_REQUEST_SENT) {
			if (this.initialSnapshotRequest != sn.getOrigRequest()) {
				LH.warning(log, logMe(), " Ignoring snapshot that does not correlate to initial request");
				return;
			}
			this.initialSnapshotRequest = null;
			this.processUid = sn.getProcessUid();
			this.snapshotSeqnum = sn.getSeqNum();
			LH.info(log, logMe(), " Set snapshot and current seqnum to: " + this.snapshotSeqnum);
			agentManager.setCurrentSeqNum(sn.getSeqNum());
			processSnapshot(sn);
			setState(STATE_CONNECTED);
		} else if (state == STATE_CONNECTED)
			processSnapshot(sn);
		else
			LH.warning(log, logMe(), " Ignoring snapshot because not in CONNECTED nor REQUEST_SENT state");
	}
	public byte getConnectionState() {
		return state;
	}
	public void onInitDone() {
		if (this.state != STATE_PREINIT)
			sendSnapshotRequestIfNecessary();
	}

	final private ContainerTools tools;
	private long messages;

	private void processAmiCenterAction(AmiCenterClientObjectMessages action) {
		if (action.getSeqNum() <= this.snapshotSeqnum || this.snapshotSeqnum == -1)
			LH.info(log, logMe(), " Skipping pre-snapshot action: " + action.getSeqNum(), "<=", this.snapshotSeqnum);
		else {
			this.messages++;
			List<AmiCenterClientObjectMessage> m = action.getMessages();
			agentManager.setCurrentSeqNum(action.getSeqNum());
			for (int i = 0; i < m.size(); i++)
				agentManager.onAmiWebObjectMessage(m.get(i));
			this.agentManager.getSystemObjectsManager().onActionsProcessed();
		}
	}
	private String logMe() {
		return manager.describeUser() + "->" + agentManager.getCenterName();
	}

	public void processSnapshot(AmiCenterClientSnapshot action) {
		if (action.getSeqNum() != agentManager.getCurrentSeqNum())
			LH.warning(log, logMe(), " Bad snapshot Seqnum: " + action.getSeqNum(), " vs ", agentManager.getCurrentSeqNum());
		this.messages++;
		List<AmiWebObject_Feed> cached = (List<AmiWebObject_Feed>) action.getCached();
		for (String type : action.getTypes())
			agentManager.onSnapshotsProcessed(type);
		for (int i = 0; i < cached.size(); i++)
			agentManager.onAmiAdd(cached.get(i));
		agentManager.getSystemObjectsManager().onActionsProcessed();
		LH.info(log, logMe(), " Processed snapshot with ", cached.size(), " rows for types: ", action.getTypes(), " seqnum=", action.getSeqNum());
	}

	public AmiWebManager getWebManager() {
		return this.agentManager;
	}

	public long getConnectionStateChangedTime() {
		return this.stateChangedTime;
	}
	public long getMessagesStatistics() {
		return this.messages;
	}

	public static String formatConnectionState(byte state) {
		switch (state) {
			case STATE_REQUEST_NOT_SENT:
				return "Request Not Sent";
			case STATE_REQUEST_SENT:
				return "Snapshot Requested";
			case STATE_CONNECTED:
				return "Connected";
			case STATE_DISCONNECTED:
				return "Disconnected";
			case STATE_INIT:
				return "Initialized";
			case STATE_PREINIT:
				return "Preinitialized";
			default:
				return SH.toString(state);
		}
	}

	public String getProcessUid() {
		return this.processUid;
	}

}
