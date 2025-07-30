package com.vortex.web.portlet.tables;

import com.f1.base.Action;
import com.f1.container.ResultMessage;
import com.f1.povo.f1app.reqres.F1AppResponse;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.utils.SH;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeResponse;
import com.vortex.client.VortexClientMachine;
import com.vortex.client.VortexClientManager;
import com.vortex.client.VortexClientManagerListener;
import com.vortex.web.VortexWebEyeService;

public abstract class VortexWebTablePortlet extends FastTablePortlet implements VortexClientManagerListener {

	public static final String HOST = "host"; //hostname
	public static final String USER = "user";//user name
	public static final String MAIN = "main"; //main
	public static final String REV = "rev"; //revision number
	public static final String MIID = "miid"; //machine instance id"
	public static final String MUID = "muid"; //machine uid
	public static final String FSID = "fsid"; //file system id
	public static final String ADID = "adid"; //network address id
	public static final String NLID = "nlid"; //network link id
	public static final String DBID = "dbid"; //database id
	public static final String DSID = "dsid"; //database server id
	public static final String TBID = "tbid"; //database table id
	public static final String OBID = "obid"; //database object id
	public static final String CLID = "clid"; //database column id
	public static final String CNID = "cnid"; //Connection ID
	public static final String PTID = "ptid"; //Process Table Id
	public static final String STID = "stid"; //Scheduled task Id
	public static final String MFID = "mfid"; //database server id
	public static final String PID = "pid"; // Process ID
	public static final String BPID = "bpid";
	public static final String PUID = "puid";
	public static final String START = "start"; // "Start Time"
	public static final String END = "end"; // "End Time"
	public static final String NOW = "now"; //"Update Time"
	final protected VortexWebEyeService service;
	final protected VortexClientManager agentManager;
	private boolean isEyeConnected;

	public VortexWebTablePortlet(PortletConfig config, FastWebTable nodeType) {
		super(config, nodeType);
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		this.agentManager = this.service.getAgentManager();
		agentManager.addClientConnectedListener(this);
		this.isEyeConnected = agentManager.getIsEyeConnected();
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		Action action = result.getActionNoThrowable();
		if (action instanceof F1AppResponse) {
			F1AppResponse cmdResponse = (F1AppResponse) action;
			if (SH.is(cmdResponse.getMessage()) || !cmdResponse.getOk())
				getManager().showAlert(SH.is(cmdResponse.getMessage()) ? cmdResponse.getMessage() : "Request failed");
		} else if (action instanceof VortexEyeResponse) {
			VortexEyeResponse cmdResponse = (VortexEyeResponse) action;
			if (SH.is(cmdResponse.getMessage()) || !cmdResponse.getOk())
				getManager().showAlert(SH.is(cmdResponse.getMessage()) ? cmdResponse.getMessage() : "Request failed");
		} else {
			super.onBackendResponse(result);
		}
	}

	@Override
	public void close() {
		agentManager.removeClientConnectedListener(this);
		super.close();
	}

	@Override
	final public void onVortexEyeDisconnected() {
		this.onEyeDisconnected();
		this.isEyeConnected = false;
	}

	@Override
	final public void onVortexEyeSnapshotProcessed() {
		this.onEyeSnapshotProcessed();
		this.isEyeConnected = true;
	}

	protected void onEyeSnapshotProcessed() {
	}
	protected void onEyeDisconnected() {
	}

	@Override
	public void onVortexClientListenerAdded(Object listener) {

	}

	public boolean getIsEyeConnected() {
		return isEyeConnected;
	}

	public void onMachineActive(VortexClientMachine machine) {
	}
	public VortexWebEyeService getService() {
		return service;
	}

	@Override
	public void onVortexConnectionStateChanged(VortexClientManager vortexClientManager, VortexWebEyeService vortexWebEyeService) {
	}
}
