package com.vortex.web.portlet.tables;

import static com.f1.vortexcommon.msg.eye.VortexDeployment.MASK_ACTIONS;
import static com.f1.vortexcommon.msg.eye.VortexDeployment.MASK_DEPLOY;
import static com.f1.vortexcommon.msg.eye.VortexDeployment.MASK_PROCESS;
import static com.f1.vortexcommon.msg.eye.VortexDeployment.STATUS_ACTION_INSTALLING____;
import static com.f1.vortexcommon.msg.eye.VortexDeployment.STATUS_ACTION_RETRIEVING____;
import static com.f1.vortexcommon.msg.eye.VortexDeployment.STATUS_ACTION_RUNNING_SCRIPT;
import static com.f1.vortexcommon.msg.eye.VortexDeployment.STATUS_ACTION_STARTING______;
import static com.f1.vortexcommon.msg.eye.VortexDeployment.STATUS_ACTION_STOPPING______;
import static com.f1.vortexcommon.msg.eye.VortexDeployment.STATUS_ACTION_UNINSTALLING__;
import static com.f1.vortexcommon.msg.eye.VortexDeployment.STATUS_ACTION_VERIFYING_____;
import static com.f1.vortexcommon.msg.eye.VortexDeployment.STATUS_DEPLOY_BAD_ROOT_PATH_;
import static com.f1.vortexcommon.msg.eye.VortexDeployment.STATUS_DEPLOY_FILE_MISMATCH_;
import static com.f1.vortexcommon.msg.eye.VortexDeployment.STATUS_DEPLOY_GENERAL_ERROR_;
import static com.f1.vortexcommon.msg.eye.VortexDeployment.STATUS_DEPLOY_MANIFEST_BAD__;
import static com.f1.vortexcommon.msg.eye.VortexDeployment.STATUS_DEPLOY_NOT_FOUND_____;
import static com.f1.vortexcommon.msg.eye.VortexDeployment.STATUS_DEPLOY_NO_MANIFEST___;
import static com.f1.vortexcommon.msg.eye.VortexDeployment.STATUS_DEPLOY_SCRIPTS_BAD___;
import static com.f1.vortexcommon.msg.eye.VortexDeployment.STATUS_PROCESS_AGENT_DOWN___;
import static com.f1.vortexcommon.msg.eye.VortexDeployment.STATUS_PROCESS_BAD_DIID_____;
import static com.f1.vortexcommon.msg.eye.VortexDeployment.STATUS_PROCESS_PID_FOUND____;
import static com.f1.vortexcommon.msg.eye.VortexDeployment.STATUS_PROCESS_PUID_MATCH___;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.Action;
import com.f1.base.Row;
import com.f1.container.ResultMessage;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.TableFieldsTreePortlet;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.suite.web.table.impl.NumberWebCellFormatter;
import com.f1.suite.web.table.impl.WebTableFilteredSetFilter;
import com.f1.utils.BitMaskDescription;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.LongSet;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.Tuple3;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.vortexcommon.msg.VortexEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentFile;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunDeploymentRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunDeploymentResponse;
import com.f1.vortexcommon.msg.eye.VortexDeployment;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageDeploymentRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageDeploymentResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyePassToAgentRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyePassToAgentResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeQueryDataRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeQueryDataResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunBuildProcedureResponse;
import com.vortex.client.VortexClientBuildProcedure;
import com.vortex.client.VortexClientBuildResult;
import com.vortex.client.VortexClientDeployment;
import com.vortex.client.VortexClientDeploymentSet;
import com.vortex.client.VortexClientEntity;
import com.vortex.client.VortexClientMachine;
import com.vortex.client.VortexClientMachineListener;
import com.vortex.web.VortexWebEyeService;
import com.vortex.web.messages.VortexDeploymentIdInterPortletMessage;
import com.vortex.web.messages.VortexDeploymentSetIdInterPortletMessage;
import com.vortex.web.messages.VortexMachineIdInterPortletMessage;
import com.vortex.web.messages.VortexPidInterPortletMessage;
import com.vortex.web.messages.VortexProcedureIdInterPortletMessage;
import com.vortex.web.portlet.forms.VortexWebBackupFormPortlet;
import com.vortex.web.portlet.forms.VortexWebCommentFormPortlet;
import com.vortex.web.portlet.forms.VortexWebCopyDeploymentsFormPortlet;
import com.vortex.web.portlet.forms.VortexWebDeploymentFormPortlet;
import com.vortex.web.portlet.forms.VortexWebRunBackupFormPortlet;
import com.vortex.web.portlet.forms.VortexWebRunDeploymentFormPortlet;
import com.vortex.web.portlet.forms.VortexWebRunProcedureFormPortlet;
import com.vortex.web.portlet.grids.VortexWebMachineFilesDialog;
import com.vortex.web.portlet.visuals.VortexTerminalPortlet;

public class VortexWebDeploymentTablePortlet extends VortexWebTablePortlet implements VortexClientMachineListener, WebContextMenuFactory, WebContextMenuListener {

	private static final String PREFIX = "&nbsp;&nbsp;&nbsp;&nbsp;";
	private final BitMaskDescription statusBitsDescription = new BitMaskDescription("Status", ',', 64);

	//private static final byte RUNNING_RUNNING = 2;
	//private static final byte RUNNING_PID_FOUND = 1;
	//private static final byte RUNNING_NOT_RUNNING = 0;
	private VortexWebEyeService service;
	private LongKeyMap<Row> rows = new LongKeyMap<Row>();
	final private BasicPortletSocket sendPidSocket;
	final private BasicPortletSocket sendMiidSocket;
	final private BasicPortletSocket sendDeploymentIdSocket;
	final private BasicPortletSocket procedureIdSocket;
	private BasicPortletSocket sendProcedureIdSocket;
	private BasicPortletSocket deploymentSetIdSocket;
	private boolean isHistory;
	private BasicPortletSocket miidSocket;
	private VortexWebMetadataColumnsManager metadataColumnManager;

	public VortexWebDeploymentTablePortlet(PortletConfig config) {
		this(config, false);
	}
	public VortexWebDeploymentTablePortlet(PortletConfig config, boolean isHistory) {
		super(config, null);
		String[] ids = { "dpid", HOST, "bpname", BPID, "brname", "brversion", "brid", "dsname", DSID, "tdir", "tuser", "invokedby", "startf", "stopf", "propf", "status",
				"statusMsg", PID, "puid", "data", "props", "diid", "bcount", "desc", "now", MIID, "mdata" };
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("Deployments");
		SmartTable st = new BasicSmartTable(inner);
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);

		statusBitsDescription.define(VortexDeployment.STATUS_ACTION_INSTALLING____, "Installing");
		statusBitsDescription.define(VortexDeployment.STATUS_ACTION_UNINSTALLING__, "Uninstalling");
		statusBitsDescription.define(VortexDeployment.STATUS_ACTION_STOPPING______, "Stopping");
		statusBitsDescription.define(VortexDeployment.STATUS_ACTION_STARTING______, "Starting");
		statusBitsDescription.define(VortexDeployment.STATUS_ACTION_RUNNING_SCRIPT, "Running Script");
		statusBitsDescription.define(VortexDeployment.STATUS_ACTION_RETRIEVING____, "Retrieving");
		statusBitsDescription.define(VortexDeployment.STATUS_ACTION_VERIFYING_____, "Verifying");

		statusBitsDescription.define(VortexDeployment.STATUS_PROCESS_PID_FOUND____, "Pid Found");
		statusBitsDescription.define(VortexDeployment.STATUS_PROCESS_PUID_MATCH___, "Puid Match");
		statusBitsDescription.define(VortexDeployment.STATUS_PROCESS_AGENT_DOWN___, "Agent Down");
		statusBitsDescription.define(VortexDeployment.STATUS_PROCESS_BAD_DIID_____, "Bad Diid");

		statusBitsDescription.define(VortexDeployment.STATUS_DEPLOY_NO_MANIFEST___, "No manifest");
		statusBitsDescription.define(VortexDeployment.STATUS_DEPLOY_BAD_ROOT_PATH_, "Bad target path");
		statusBitsDescription.define(VortexDeployment.STATUS_DEPLOY_GENERAL_ERROR_, "General error");
		statusBitsDescription.define(VortexDeployment.STATUS_DEPLOY_SCRIPTS_BAD___, "Scripts config error");
		statusBitsDescription.define(VortexDeployment.STATUS_DEPLOY_FILE_MISMATCH_, "Files changed");
		statusBitsDescription.define(VortexDeployment.STATUS_DEPLOY_MANIFEST_BAD__, "Manifest Bad");
		statusBitsDescription.define(VortexDeployment.STATUS_DEPLOY_NOT_FOUND_____, "Not Found");

		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());
		table.addColumn(isHistory, "Update Time", "now", service.getDateTimeWebCellFormatter());
		table.addColumn(true, "Deployment Set", "dsname", service.getBasicFormatter());
		table.addColumn(true, "Host", HOST, service.getHostnameFormatter());
		table.addColumn(true, "Description", "desc", service.getBasicFormatter()).setWidth(150).addCssClass("bold");
		table.addColumn(true, "Directory", "tdir", service.getBasicFormatter()).setWidth(300);
		table.addColumn(true, "Build Procedure", "bpname", service.getBasicFormatter());
		table.addColumn(true, "Build Result Version", "brversion", service.getBasicFormatter());
		table.addColumn(true, "Run Status", "status", new ProcessStatusFormatter());// service.getBasicFormatter());
		table.addColumn(true, "User Status", "status", new UserStatusFormatter());
		table.addColumn(true, "Install Status", "status", new InstallStatusFormatter());
		//table.addColumn(true, "Running", "running", runningStatusFormatter);
		table.addColumn(true, "Message", "statusMsg", service.getBasicFormatter()).setWidth(250);
		table.addColumn(false, "Properties", "props", service.getBasicFormatter());
		table.addColumn(true, "Deployment Instance Id", "diid", service.getIdFormatter("DI-"));

		table.addColumn(true, "Pid", PID, service.getBasicFormatter()).setWidth(60).setCssColumn("blue");
		table.addColumn(!isHistory, "Backups", "bcount", service.getNumberFormatter()).setWidth(60);
		table.addColumn(false, "Process Uid", "puid", service.getBasicFormatter());
		table.addColumn(false, "Target User", "tuser", service.getBasicFormatter());
		table.addColumn(false, "Generated Properties", "propf", service.getFilenameFormatter());
		table.addColumn(false, "Build Result", "brname", service.getBasicFormatter());
		table.addColumn(false, "Start Script", "startf", service.getFilenameFormatter());
		table.addColumn(false, "Stop Script", "stopf", service.getFilenameFormatter());
		table.addColumn(false, "Build Procedure ID", "bpid", service.getIdFormatter("BP-"));
		table.addColumn(false, "Last Deployment Invoked By", "invokedby", service.getBasicFormatter());
		table.addColumn(false, "Id", "dpid", service.getIdFormatter("DP-"));
		table.addColumn(false, "Build Result ID", "brid", service.getIdFormatter("BR-"));
		table.addColumn(false, "Raw Status", "status", new NumberWebCellFormatter(statusBitsDescription));
		table.addColumn(false, "Machine ID", MIID, service.getIdFormatter("MA-"));
		table.addColumn(true, "Deployment Set ID", DSID, service.getIdFormatter("DS-"));
		table.setMenuFactory(this);
		this.sendPidSocket = addSocket(true, "sendPid", "Pid", true, CH.s(VortexPidInterPortletMessage.class), null);
		this.sendMiidSocket = addSocket(true, "sendMiid", "Send Machine ID", true, CH.s(VortexMachineIdInterPortletMessage.class), null);
		this.sendDeploymentIdSocket = addSocket(true, "sendDeploymentId", "Send Deployment ID", true, CH.s(VortexDeploymentIdInterPortletMessage.class), null);
		this.sendProcedureIdSocket = addSocket(true, "sendProcedureId", "Send Build Procedure ID", true, CH.s(VortexProcedureIdInterPortletMessage.class), null);
		this.procedureIdSocket = addSocket(false, "procedureId", "build Procedure ID", true, null, CH.s(VortexProcedureIdInterPortletMessage.class));
		this.deploymentSetIdSocket = addSocket(false, "deploymentSetId", "Deployment Set ID", true, null, CH.s(VortexDeploymentSetIdInterPortletMessage.class));
		this.miidSocket = addSocket(false, "miid", "Machine ID", true, null, CH.s(VortexMachineIdInterPortletMessage.class));
		this.isHistory = isHistory;
		setTable(table);
		metadataColumnManager = new VortexWebMetadataColumnsManager(getTable(), VortexAgentEntity.TYPE_DEPLOYMENT, "mdata");
		if (!isHistory) {
			agentManager.addMachineListener(this);
			for (VortexClientDeployment bp : service.getAgentManager().getDeployments())
				onMachineEntityAdded(bp);
		} else {
			getTable().sortRows("now", false, false, false);
		}
	}

	@Override
	public void close() {
		if (!isHistory) {
			agentManager.removeMachineListener(this);
		}
		super.close();
	}
	@Override
	public void onMachineAdded(VortexClientMachine machine) {
	}

	@Override
	public void onMachineUpdated(VortexClientMachine machine) {
	}

	@Override
	public void onMachineStale(VortexClientMachine machine) {
	}

	@Override
	public void onMachineEntityAdded(VortexClientEntity<?> node) {
		switch (node.getType()) {
			case VortexAgentEntity.TYPE_DEPLOYMENT: {
				VortexClientDeployment t = (VortexClientDeployment) node;
				addDeploymentRow(t);
			}
		}
		metadataColumnManager.onMachineEntityAdded(node);
	}

	private void addDeploymentRow(VortexClientDeployment node) {
		final VortexDeployment dep = node.getData();
		final Row existing = isHistory ? null : rows.get(node.getId());

		final VortexClientMachine targetMachine = node.getMachine();
		final String targetHostname = targetMachine == null ? ("Decommisioned: " + node.getData().getTargetMachineUid()) : targetMachine.getHostName();
		final long now = dep.getNow();
		final String targetDir = dep.getTargetDirectory();
		final String targetUser = dep.getTargetUser();

		final VortexClientBuildProcedure bp = node.getBuildProcedure();
		final String bpName = bp == null ? null : bp.getData().getName();
		final long bpId = dep.getProcedureId();
		final String invokedBy = dep.getCurrentBuildInvokedBy();

		final VortexClientBuildResult br = node.getBuildResult();
		final String brName = br == null ? null : br.getData().getName();
		final String brVersion = br == null ? null : br.getData().getVersion();
		final Long brId = dep.getCurrentBuildResultId();

		final VortexClientDeploymentSet ds = node.getDeploymentSet();
		final String dsName = ds == null ? null : ds.getData().getName();
		final long dsId = dep.getDeploymentSetId();

		final String startf = dep.getStartScriptFile();
		final String stopf = dep.getStopScriptFile();
		final String propf = dep.getGeneratedPropertiesFile();
		final String props = dep.getProperties();
		final long miid = targetMachine == null ? -1 : targetMachine.getId();
		final int status = dep.getStatus();
		//final String pstatus = statusBitsDescription.format(VortexDeployment.MASK_PROCESS & dep.getStatus());
		//final String astatus = statusBitsDescription.format(VortexDeployment.MASK_ACTIONS & dep.getStatus());
		//final String istatus = statusBitsDescription.format(VortexDeployment.MASK_DEPLOY & dep.getStatus());
		final String statusMsg = dep.getMessage();
		final Integer pid = dep.getRunningPid();
		final String runningProcessUid = dep.getRunningProcessUid();
		final Long diid = dep.getDeployedInstanceId();
		final int bcount = node.getBackups().size();
		String desc = node.getData().getDescription();

		boolean fireChanged = false;
		if (existing == null) {
			fireChanged = true;
			rows.put(node.getId(), addRow(node.getId(), targetHostname, bpName, bpId, brName, brVersion, brId, dsName, dsId, targetDir, targetUser, invokedBy, startf, stopf, propf,
					status, statusMsg, pid, runningProcessUid, node, props, diid, bcount, desc, now, miid, dep.getMetadata()));
		} else {
			if (OH.ne(pid, existing.get("pid")))
				fireChanged = true;
			existing.put(HOST, targetHostname);
			existing.put("tdir", targetDir);
			existing.put("diid", diid);
			existing.put("tuser", targetUser);
			existing.put("bpname", bpName);
			existing.put(BPID, bpId);
			existing.put("brname", brName);
			existing.put("brversion", brVersion);
			existing.put("brid", brId);
			existing.put("dsname", dsName);
			existing.put("dsid", dsId);
			existing.put("invokedby", invokedBy);
			existing.put("startf", startf);
			existing.put("stopf", stopf);
			existing.put("propf", propf);
			existing.put("props", props);
			existing.put("status", status);
			existing.put("statusMsg", statusMsg);
			existing.put("pid", pid);
			existing.put("puid", runningProcessUid);
			existing.put("bcount", bcount);
			existing.put("desc", desc);
			existing.put("now", now);
			existing.put(MIID, miid);
			existing.put("mdata", dep.getMetadata());
		}
		if (fireChanged || !getTable().hasSelectedRows())
			onVortexRowsChanged();
	}
	private void removeDeployment(VortexClientDeployment node) {
		Row existing = rows.remove(node.getId());
		if (existing != null)
			removeRow(existing);
		if (!getTable().hasSelectedRows())
			onVortexRowsChanged();
	}

	@Override
	public void onMachineEntityUpdated(VortexClientEntity<?> node) {
		switch (node.getType()) {
			case VortexAgentEntity.TYPE_DEPLOYMENT: {
				VortexClientDeployment t = (VortexClientDeployment) node;
				addDeploymentRow(t);
				break;
			}
			case VortexAgentEntity.TYPE_DEPLOYMENT_SET: {
				VortexClientDeploymentSet t = (VortexClientDeploymentSet) node;
				for (VortexClientDeployment d : t.getDeployments())
					addDeploymentRow(d);
				break;
			}
		}
		metadataColumnManager.onMachineEntityUpdated(node);
	}

	@Override
	public void onMachineEntityRemoved(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_DEPLOYMENT) {
			VortexClientDeployment rule = (VortexClientDeployment) node;
			removeDeployment(rule);
		}
		metadataColumnManager.onMachineEntityRemoved(node);
	}

	@Override
	public void onEyeDisconnected() {
		clearRows();
		rows.clear();
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebDeploymentTablePortlet> {

		public static final String ID = "DeploymentsTablePortlet";

		public Builder() {
			super(VortexWebDeploymentTablePortlet.class);
		}

		@Override
		public VortexWebDeploymentTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebDeploymentTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Deployments Table";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		if ("create".equals(action)) {
			if (CH.isEmpty(service.getAgentManager().getDeploymentSets())) {
				getManager().showAlert("Please create a deployment set first.");
				return;
			}
			getManager().showDialog("Add Deployment", new VortexWebDeploymentFormPortlet(generateConfig()));
		} else if ("copy".equals(action)) {
			VortexWebCopyDeploymentsFormPortlet form = new VortexWebCopyDeploymentsFormPortlet(generateConfig());
			int cnt = 0;
			for (Row row : table.getSelectedRows()) {
				VortexClientDeployment dep = row.get("data", VortexClientDeployment.class);
				if (dep != null) {
					cnt++;
					form.addDeploymentToCopy(null, dep);
				}
			}
			if (cnt > 0)
				getManager().showDialog("Copy Deployment", form);//new VortexWebCopyDeploymentSetFormPortlet(generateConfig(), bp));
		} else if ("edit".equals(action)) {
			for (Row row : table.getSelectedRows()) {
				long dpid = row.get("dpid", Long.class);
				VortexClientDeployment bp = service.getAgentManager().getDeployment(dpid);
				if (bp != null) {
					VortexWebDeploymentFormPortlet p = new VortexWebDeploymentFormPortlet(generateConfig());
					p.setDeploymentToEdit(bp);
					getManager().showDialog("Edit Deployment", p);
				}
			}
		} else if ("delete".equals(action)) {
			List<VortexEyeRequest> eyeReqs = new ArrayList<VortexEyeRequest>();
			for (Row row : table.getSelectedRows()) {
				VortexEyeManageDeploymentRequest request = nw(VortexEyeManageDeploymentRequest.class);
				VortexDeployment exp = nw(VortexDeployment.class);
				exp.setId(row.get("dpid", Long.class));
				exp.setRevision(VortexAgentEntity.REVISION_DONE);
				request.setDeployment(exp);
				eyeReqs.add(request);
			}
			getManager().showDialog("Delete Deployment",
					new VortexWebCommentFormPortlet(generateConfig(), getPortletId(), eyeReqs, "Delete Deployment", "deployment.jpg").setIconToDelete());
		} else if ("run".equals(action)) {
			List<VortexClientDeployment> bps = new ArrayList<VortexClientDeployment>();
			for (Row row : table.getSelectedRows()) {
				VortexClientDeployment data = row.get("data", VortexClientDeployment.class);
				bps.add(data);
			}
			if (bps.size() > 0) {
				VortexWebRunDeploymentFormPortlet p = new VortexWebRunDeploymentFormPortlet(generateConfig(), false);
				if (p.setDeployments(bps))
					getManager().showDialog("Run Deployments - " + p.describe(), p, 800, 500);
			}
		} else if ("stop".equals(action)) {
			List<VortexEyeRequest> eyeReqs = new ArrayList<VortexEyeRequest>();
			for (Row row : table.getSelectedRows()) {
				VortexClientDeployment data = row.get("data", VortexClientDeployment.class);
				VortexAgentRunDeploymentRequest request = nw(VortexAgentRunDeploymentRequest.class);
				request.setCommandType(VortexAgentRunDeploymentRequest.TYPE_STOP_SCRIPT);
				request.setInvokedBy(getManager().getState().getWebState().getUser().getUserName());
				request.setDeploymentId(data.getId());
				VortexEyePassToAgentRequest eyeReq = nw(VortexEyePassToAgentRequest.class);
				eyeReq.setAgentMachineUid(data.getData().getTargetMachineUid());
				eyeReq.setAgentRequest(request);
				eyeReqs.add(eyeReq);
			}
			getManager().showDialog("Shutdown application", new VortexWebCommentFormPortlet(generateConfig(), getPortletId(), eyeReqs, "Shutdown Deployed App", "stop.jpg"));
		} else if ("remove_files".equals(action)) {
			List<VortexEyeRequest> eyeReqs = new ArrayList<VortexEyeRequest>();
			boolean extraWarn = false;
			for (Row row : table.getSelectedRows()) {
				VortexClientDeployment data = row.get("data", VortexClientDeployment.class);
				if (!extraWarn && MH.anyBits(data.getData().getStatus(), VortexDeployment.STATUS_PROCESS_PID_FOUND____ | VortexDeployment.STATUS_PROCESS_PUID_MATCH___))
					extraWarn = true;
				VortexAgentRunDeploymentRequest request = nw(VortexAgentRunDeploymentRequest.class);
				request.setCommandType(VortexAgentRunDeploymentRequest.TYPE_DELETE_ALL_FILES);
				request.setInvokedBy(getManager().getState().getWebState().getUser().getUserName());
				request.setDeploymentId(data.getId());
				VortexEyePassToAgentRequest eyeReq = nw(VortexEyePassToAgentRequest.class);
				eyeReq.setAgentMachineUid(data.getData().getTargetMachineUid());
				eyeReq.setAgentRequest(request);
				eyeReqs.add(eyeReq);
			}
			VortexWebCommentFormPortlet t = new VortexWebCommentFormPortlet(generateConfig(), getPortletId(), eyeReqs, "Undeploy Deployed App", "undeploy.jpg");
			if (extraWarn) {
				ConfirmDialogPortlet dialog = new ConfirmDialogPortlet(generateConfig(), "Are you sure you want to undeploy applications that are still running?",
						ConfirmDialogPortlet.TYPE_OK_CANCEL);
				dialog.updateButton(ConfirmDialogPortlet.ID_YES, "Yes, undeploy");
				dialog.setFollowupDialog(ConfirmDialogPortlet.ID_YES, "Undeploy application", t);
				getManager().showDialog("Undeploy", dialog);
			} else
				getManager().showDialog("Undeploy application", t);
		} else if ("files".equals(action)) {
			List<Tuple3<String, VortexAgentFile, String>> files = new ArrayList<Tuple3<String, VortexAgentFile, String>>();
			for (Row row : getTable().getSelectedRows()) {
				VortexClientDeployment data = row.get("data", VortexClientDeployment.class);
				VortexAgentFile f = nw(VortexAgentFile.class);
				f.setPath(data.getData().getTargetDirectory());
				f.setMask(VortexAgentFile.DIRECTORY);
				files.add(new Tuple3<String, VortexAgentFile, String>(data.getMachine().getMachineUid(), f, data.getDescription()));
			}
			getManager().showDialog("Files", new VortexWebMachineFilesDialog(generateConfig(), files));
		} else if ("logs".equals(action)) {
			List<Tuple3<String, VortexAgentFile, String>> files = new ArrayList<Tuple3<String, VortexAgentFile, String>>();
			for (Row row : getTable().getSelectedRows()) {
				VortexClientDeployment data = row.get("data", VortexClientDeployment.class);
				String logDir = data.getData().getLogDirectories();
				if (SH.is(logDir)) {
					for (String file : SH.split(',', logDir)) {
						VortexAgentFile f = nw(VortexAgentFile.class);
						if (IOH.isAbsolutePath(file))
							f.setPath(file);
						else
							f.setPath(data.getData().getTargetDirectory() + "/" + file);
						f.setMask(VortexAgentFile.DIRECTORY);
						files.add(new Tuple3<String, VortexAgentFile, String>(data.getMachine().getMachineUid(), f, file + " for " + data.getDescription()));
					}
					//IOH.isAbsolutePath(logDir)) {
					//f.setPath(logDir);
				}
				//				else {
				//					VortexAgentFile f = nw(VortexAgentFile.class);
				//					f.setPath(data.getData().getTargetDirectory());
				//					f.setMask(VortexAgentFile.DIRECTORY);
				//					files.add(new Tuple3<String, VortexAgentFile, String>(data.getMachine().getMachineUid(), f, data.getDescription()));
				//				}
			}
			if (files.size() == 0)
				getManager().showAlert("No log directories specified for selected deployments");
			else
				getManager().showDialog("Files", new VortexWebMachineFilesDialog(generateConfig(), files));
		} else if (action.startsWith("terminal")) {
			for (Row row : getTable().getSelectedRows()) {
				VortexClientDeployment data = row.get("data", VortexClientDeployment.class);
				String muid = data.getMachine().getMachineUid();
				String pwd = data.getData().getTargetDirectory();
				String user = action.equals("terminal_local") ? getManager().getState().getWebState().getUser().getUserName() : data.getData().getTargetUser();
				VortexTerminalPortlet vtp = new VortexTerminalPortlet(generateConfig(), user, muid, pwd);
				vtp.setAllowExit(true);

				vtp.logLocation("Opened Terminal from Deployment DP-" + data.getId() + " '" + data.getDescriptionWithoutTarget() + "' at ");
				getManager().showDialog("Terminal", vtp);
			}
		} else if ("history".equals(action)) {
			LongSet ids = new LongSet();
			for (Row row : table.getSelectedRows())
				ids.add(row.get("data", VortexClientDeployment.class).getId());
			final VortexEyeQueryDataRequest request = nw(VortexEyeQueryDataRequest.class);
			request.setIds(ids.toLongArray());
			request.setType(VortexAgentEntity.TYPE_DEPLOYMENT);
			service.sendRequestToBackend(getPortletId(), request);

		} else if ("addbackup".equals(action)) {
			for (Row row : table.getSelectedRows()) {
				VortexClientDeployment data = row.get("data", VortexClientDeployment.class);
				VortexWebBackupFormPortlet dialog = new VortexWebBackupFormPortlet(generateConfig());
				dialog.setDeploymentId(data.getId());
				getManager().showDialog("Add Backup", dialog);
			}
		} else if ("verify".equals(action)) {
			List<VortexClientDeployment> bps = new ArrayList<VortexClientDeployment>();
			for (Row row : table.getSelectedRows()) {
				VortexClientDeployment data = row.get("data", VortexClientDeployment.class);
				bps.add(data);
			}
			if (bps.size() > 0) {
				VortexWebRunDeploymentFormPortlet p = new VortexWebRunDeploymentFormPortlet(generateConfig(), true);
				if (p.setDeployments(bps))
					getManager().showDialog("Verify Deployments - " + p.describe(), p, 800, 500);
			}
		} else if ("start".equals(action)) {
			List<VortexEyeRequest> eyeReqs = new ArrayList<VortexEyeRequest>();
			for (Row row : table.getSelectedRows()) {
				VortexClientDeployment data = row.get("data", VortexClientDeployment.class);
				VortexAgentRunDeploymentRequest request = nw(VortexAgentRunDeploymentRequest.class);
				request.setCommandType(VortexAgentRunDeploymentRequest.TYPE_START_SCRIPT);
				request.setDeploymentId(data.getId());
				request.setInvokedBy(getManager().getState().getWebState().getUser().getUserName());
				VortexEyePassToAgentRequest eyeReq = nw(VortexEyePassToAgentRequest.class);
				eyeReq.setAgentMachineUid(data.getData().getTargetMachineUid());
				eyeReq.setAgentRequest(request);
				eyeReqs.add(eyeReq);
				//service.sendRequestToBackend(getPortletId(), eyeReq);
			}
			getManager().showDialog("Start application", new VortexWebCommentFormPortlet(generateConfig(), getPortletId(), eyeReqs, "Start Deployed App", "start.jpg"));
		} else if ("build".equals(action)) {
			List<VortexEyeRequest> eyeReqs = new ArrayList<VortexEyeRequest>();
			List<VortexClientBuildProcedure> bps = new ArrayList<VortexClientBuildProcedure>();
			LongSet existing = new LongSet();
			Map<String, String> variables = new HashMap<String, String>();
			for (Row row : table.getSelectedRows()) {
				VortexClientDeployment data = row.get("data", VortexClientDeployment.class);
				VortexClientBuildResult br = data.getBuildResult();
				if (br != null)
					variables.putAll(br.getData().getBuildVariables());
				VortexClientBuildProcedure bp = data.getBuildProcedure();
				if (existing.add(bp.getId()))
					bps.add(bp);
			}
			if (bps.size() > 0) {
				VortexWebRunProcedureFormPortlet p = new VortexWebRunProcedureFormPortlet(generateConfig(), getPortletId());
				p.setBuildProcedures(bps);
				p.setVariables(variables);
				getManager().showDialog("Run Command - " + p.describe(), p);
			}
		} else if ("backup".equals(action)) {
			List<VortexClientDeployment> deployments = new ArrayList<VortexClientDeployment>();
			for (Row row : table.getSelectedRows()) {
				VortexClientDeployment bp = row.get("data", VortexClientDeployment.class);
				if (bp.getBackups().size() > 0)
					deployments.add(bp);
			}
			if (deployments.isEmpty()) {
				getManager().showAlert("Selected Deployments do not have backups nor archives configured");
			} else {
				final VortexWebRunBackupFormPortlet p = new VortexWebRunBackupFormPortlet(generateConfig());
				p.setDeployments(deployments);
				getManager().showDialog("Run Backup / Archive", p);
			}
		} else if (SH.startsWith(action, "run:")) {
			String script = SH.stripPrefix(action, "run:", true);
			List<VortexEyeRequest> eyeReqs = new ArrayList<VortexEyeRequest>();
			for (Row row : table.getSelectedRows()) {
				VortexClientDeployment data = row.get("data", VortexClientDeployment.class);
				VortexAgentRunDeploymentRequest request = nw(VortexAgentRunDeploymentRequest.class);
				request.setCommandType(VortexAgentRunDeploymentRequest.TYPE_RUN_SCRIPT);
				request.setTargetFile(script);
				request.setDeploymentId(data.getId());
				request.setInvokedBy(getManager().getState().getWebState().getUser().getUserName());
				VortexEyePassToAgentRequest eyeReq = nw(VortexEyePassToAgentRequest.class);
				eyeReq.setAgentMachineUid(data.getData().getTargetMachineUid());
				eyeReq.setAgentRequest(request);
				eyeReqs.add(eyeReq);
			}
			getManager().showDialog("Run custom script", new VortexWebCommentFormPortlet(generateConfig(), getPortletId(), eyeReqs, "Run " + script, "script.jpg"));
		}

	}
	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {

	}

	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
		super.onSelectedChanged(fastWebTable);
		onVortexRowsChanged();
	}

	@Override
	public WebMenu createMenu(WebTable table) {
		if (isHistory)
			return null;
		List<WebMenuItem> items = new ArrayList<WebMenuItem>();
		List<Row> sel = table.getSelectedRows();
		if (sel.size() > 0) {
			items.add(new BasicWebMenuLink("Show log files", true, "logs"));
			items.add(new BasicWebMenuLink("Show files", true, "files"));
			items.add(new BasicWebMenuLink("Show History", true, "history"));
			if (sel.size() == 1) {
				VortexClientDeployment data = sel.get(0).get("data", VortexClientDeployment.class);
				String userName = getManager().getState().getWebState().getUser().getUserName();
				String deployUser = data.getData().getTargetUser();
				if (OH.eq(userName, deployUser))
					items.add(new BasicWebMenuLink("Open terminal as " + userName, true, "terminal"));
				else {
					BasicWebMenu menu;
					items.add(menu = new BasicWebMenu("Open Terminal", true, new ArrayList<WebMenuItem>()));
					menu.addChild(new BasicWebMenuLink("As local user: " + userName, true, "terminal_local"));
					menu.addChild(new BasicWebMenuLink("As deployment user: " + deployUser, true, "terminal"));
				}
			}
			items.add(new BasicWebMenuDivider());
			items.add(new BasicWebMenuLink("Start", true, "start"));
			items.add(new BasicWebMenuLink("Stop", true, "stop"));
			Set<String> scripts = null;
			boolean hasBackups = false;
			for (int i = 0; i < sel.size(); i++) {
				final Row row = sel.get(i);
				final VortexClientDeployment data = row.get("data", VortexClientDeployment.class);
				final String found = data.getData().getScriptsFound();
				final Set<String> set = CH.s(SH.split(',', found));
				//filter in only scripts common to all selections
				scripts = i == 0 ? set : CH.comm(scripts, CH.s(SH.split(',', found)), false, false, true);
				hasBackups = hasBackups || data.getBackups().size() > 0;
			}
			if (scripts.size() > 0) {
				BasicWebMenu scriptItems = new BasicWebMenu("Custom Script", true, new ArrayList<WebMenuItem>());
				for (String sh : CH.sort(scripts)) {
					scriptItems.addChild(new BasicWebMenuLink("Run: " + sh, true, "run:" + sh));
				}
				items.add(scriptItems);
			}
			items.add(new BasicWebMenuDivider());
			items.add(new BasicWebMenuLink("Deploy", true, "run"));
			items.add(new BasicWebMenuLink("Undeploy (Remove files)", true, "remove_files"));
			items.add(new BasicWebMenuLink("Run Build Procedure", true, "build"));
			items.add(new BasicWebMenuLink("Verify Environments", true, "verify"));
			items.add(new BasicWebMenuDivider());
			items.add(new BasicWebMenuLink("Copy Deployment", true, "copy"));
			items.add(new BasicWebMenuLink("Edit Deployment", true, "edit"));
			items.add(new BasicWebMenuLink("Delete Deployment", true, "delete"));
			items.add(new BasicWebMenuLink("Create Deployment", true, "create"));
			items.add(new BasicWebMenuDivider());
			items.add(new BasicWebMenuLink("Add Backup", true, "addbackup"));
			if (hasBackups)
				items.add(new BasicWebMenuLink("Run backup(s)", true, "backup"));

		} else
			items.add(new BasicWebMenuLink("Create Deployment", true, "create"));
		return new BasicWebMenu("", true, items);
	}
	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		Action action = result.getAction();
		if (action instanceof VortexEyeQueryDataResponse) {
			VortexWebDeploymentTablePortlet tbl = new VortexWebDeploymentTablePortlet(generateConfig(), true);
			for (VortexEntity row : ((VortexEyeQueryDataResponse) action).getData()) {
				VortexDeployment dep = (VortexDeployment) row;
				VortexClientDeployment cdep = new VortexClientDeployment(dep);
				cdep.setDeploymentSet(service.getAgentManager().getDeploymentSet(dep.getDeploymentSetId()));
				cdep.setBuildProcedure(service.getAgentManager().getBuildProcedure(dep.getProcedureId()));
				if (dep.getCurrentBuildResultId() != null)
					cdep.setBuildResult(service.getAgentManager().getBuildResult(dep.getCurrentBuildResultId()));
				cdep.setMachine(service.getAgentManager().getAgentMachineByUid(dep.getTargetMachineUid()));
				tbl.onMachineEntityAdded(cdep);
			}
			DividerPortlet div = new DividerPortlet(generateConfig(), true);
			TableFieldsTreePortlet dtls = new TableFieldsTreePortlet(generateConfig());
			div.setOffset(.8);
			div.addChild(tbl);
			div.addChild(dtls);
			tbl.hiddenFields.connectTo(dtls.hiddenFields);
			getManager().showDialog("Show History", div);
		} else if (action instanceof VortexEyeManageDeploymentResponse) {
			VortexEyeManageDeploymentResponse res = (VortexEyeManageDeploymentResponse) action;
			if (!res.getOk()) {
				getManager().showAlert(res.getMessage());
			}
			return;
		} else if (action instanceof VortexEyeRunBuildProcedureResponse) {
			VortexEyeRunBuildProcedureResponse rbp = (VortexEyeRunBuildProcedureResponse) action;
			if (!rbp.getOk() && SH.is(rbp.getMessage())) {
				getManager().showAlert(rbp.getMessage());
			}
		} else {
			VortexEyePassToAgentResponse p2a = (VortexEyePassToAgentResponse) action;
			if (!p2a.getOk()) {
				getManager().showAlert(p2a.getMessage());
				return;
			}
			VortexAgentRunDeploymentResponse res = (VortexAgentRunDeploymentResponse) p2a.getAgentResponse();
			if (res.getOk()) {
				//VortexEyePassToAgentRequest req = (VortexEyePassToAgentRequest) result.getRequestMessage().getAction();
				//VortexAgentRunDeploymentRequest depReq = (VortexAgentRunDeploymentRequest) req.getAgentRequest();
				//if (depReq.getCommandType() == VortexAgentRunDeploymentRequest.TYPE_GET_FILE_STRUCTURE) {
				//List<VortexAgentFile> files = res.getFiles();
				//if (files.size() == 1) {
				//VortexAgentFile file = files.get(0);
				//VortexWebDeploymentFilesDialog dialog = new VortexWebDeploymentFilesDialog(generateConfig());
				//dialog.addFiles(file);
				//dialog.setDeploymentId(depReq.getDeploymentId());
				//getManager().showDialog("log files", dialog);
				//}
				//} else if (depReq.getCommandType() == VortexAgentRunDeploymentRequest.TYPE_GET_FILE) {
				//if (res.getFiles() != null) {
				//TabPortlet dialog = new TabPortlet(generateConfig());
				//for (VortexAgentFile file : res.getFiles()) {
				//VortexClientUtils.decompressFile(file);
				//dialog.addChild(SH.afterLast(file.getPath(), '/'), new HtmlPortlet(generateConfig(), "<pre>" + new String(file.getData()) + "</pre>"));
				//}
				//getManager().showDialog("log files", dialog);
				//} else
				//getManager().showAlert("no files");
				//
				//}
			} else if (res.getMessage() != null) {
				getManager().showAlert(res.getMessage());
			} else {
				getManager().showAlert("Backend error: " + res.getClass().getName());
			}
		}
	}
	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket remoteSocket, InterPortletMessage message) {
		if (localSocket == miidSocket) {
			VortexMachineIdInterPortletMessage msg = (VortexMachineIdInterPortletMessage) message;
			getTable().setExternalFilter(new WebTableFilteredSetFilter(getTable().getColumn(MIID), msg.getMiids()));
			onVortexRowsChanged();
			//} else if (localSocket == recvMachineSocket) {
			//VortexUtils.applyIDFilter((VortexInterPortletMessage) message, getTable());
			//onSelectedChanged(getTable());
		} else if (localSocket == deploymentSetIdSocket) {
			VortexDeploymentSetIdInterPortletMessage msg = (VortexDeploymentSetIdInterPortletMessage) message;
			getTable().setExternalFilter(new WebTableFilteredSetFilter(getTable().getColumn(DSID), msg.getDeploymentSetIds()));
			onVortexRowsChanged();
		} else if (localSocket == procedureIdSocket) {
			VortexProcedureIdInterPortletMessage msg = (VortexProcedureIdInterPortletMessage) message;
			getTable().setExternalFilter(new WebTableFilteredSetFilter(getTable().getColumn(BPID), msg.getProcedureIds()));
			onVortexRowsChanged();
		} else {
			super.onMessage(localSocket, remoteSocket, message);
		}
	}

	public Class getClassType(String name) {
		if (PUID.equals(name))
			return String.class;
		return Long.class;
	}

	@Override
	public void onMachineRemoved(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}

	protected void onVortexRowsChanged() {
		if (!getIsEyeConnected())
			return;
		if (!sendPidSocket.hasConnections() && !sendMiidSocket.hasConnections() && !sendDeploymentIdSocket.hasConnections() && !sendProcedureIdSocket.hasConnections())
			return;

		FastWebTable t = getTable();
		List<? extends Row> sel = t.hasSelectedRows() ? t.getSelectedRows() : t.getTable().getRows();
		if (sendPidSocket.hasConnections()) {
			Set<Tuple2<String, String>> selections = new HashSet<Tuple2<String, String>>();
			for (Row addRow : sel) {
				Object pid = addRow.get(PID);
				if (pid != null)
					selections.add(new Tuple2<String, String>((String) addRow.get(HOST), SH.toString((int) (Integer) pid)));
			}
			sendPidSocket.sendMessage(new VortexPidInterPortletMessage(selections));
		}
		if (sendMiidSocket.hasConnections()) {
			LongSet selections = new LongSet();
			for (Row addRow : sel)
				selections.add(addRow.get("data", VortexClientDeployment.class).getMachine().getMachineId());
			sendMiidSocket.sendMessage(new VortexMachineIdInterPortletMessage(selections));
		}
		if (sendDeploymentIdSocket.hasConnections()) {
			LongSet selections = new LongSet();
			for (Row addRow : sel)
				selections.add(addRow.get("dpid", Long.class));
			sendDeploymentIdSocket.sendMessage(new VortexDeploymentIdInterPortletMessage(selections));
		}
		if (sendProcedureIdSocket.hasConnections()) {
			LongSet selections = new LongSet();
			for (Row addRow : sel)
				selections.add((Long) addRow.get(BPID));
			sendProcedureIdSocket.sendMessage(new VortexProcedureIdInterPortletMessage(selections));
		}
	}

	class ProcessStatusFormatter extends BasicWebCellFormatter {

		@Override
		public void formatCellToHtml(Object value, StringBuilder sb, StringBuilder cellStyle) {
			if (value == null)
				return;
			int rawstatus = (Integer) value;
			int status = MASK_PROCESS & (Integer) rawstatus;
			if (MH.anyBits(rawstatus, STATUS_DEPLOY_GENERAL_ERROR_)) {
				sb.append(PREFIX).append("Unknown Error");
				cellStyle.append("_cna=portlet_icon_error");
			} else {
				switch (status) {
					case 0:
						sb.append(PREFIX).append("Not running");
						cellStyle.append("_cna=portlet_icon_deployment_stopped");
						break;
					case STATUS_PROCESS_AGENT_DOWN___:
						sb.append(PREFIX).append("Agent Not Running");
						cellStyle.append("_cna=portlet_icon_info");
						break;
					case STATUS_PROCESS_PID_FOUND____:
						sb.append(PREFIX).append("Pid Found, awaiting agent connection");
						cellStyle.append("_cna=portlet_icon_deployment_found");
						break;
					case STATUS_PROCESS_PID_FOUND____ | STATUS_PROCESS_PUID_MATCH___:
						sb.append(PREFIX).append("Monitored");
						cellStyle.append("_cna=portlet_icon_deployment_started");
						break;
					case STATUS_PROCESS_PID_FOUND____ | STATUS_PROCESS_PUID_MATCH___ | STATUS_PROCESS_BAD_DIID_____:
						sb.append(PREFIX).append("Monitoring old version");
						cellStyle.append("_cna=portlet_icon_warning");
						break;
					default:
						sb.append(PREFIX).append("Unknown status: ");
						statusBitsDescription.toString(status, sb);
						cellStyle.append("_cna=portlet_icon_warning");
						break;
				}
			}
		}
	}

	class UserStatusFormatter extends BasicWebCellFormatter {

		@Override
		public void formatCellToHtml(Object value, StringBuilder sb, StringBuilder cellStyle) {
			if (value == null)
				return;
			int rawstatus = (Integer) value;
			int status = MASK_ACTIONS & (Integer) rawstatus;
			if (MH.anyBits(rawstatus, STATUS_DEPLOY_GENERAL_ERROR_)) {
				sb.append(PREFIX).append("Unknown Error");
				cellStyle.append("_cna=portlet_icon_error");
			} else if (rawstatus == STATUS_PROCESS_AGENT_DOWN___) {
				sb.append(PREFIX).append("Agent Not Running");
				cellStyle.append("_cna=portlet_icon_info");
			} else {
				switch (status) {
					case 0:
						cellStyle.append("_cna=");
						break;
					case STATUS_ACTION_STARTING______:
						sb.append(PREFIX).append("Starting");
						cellStyle.append("_cna=portlet_icon_connection");
						break;
					case STATUS_ACTION_STOPPING______:
						sb.append(PREFIX).append("Stopping");
						cellStyle.append("_cna=portlet_icon_connection");
						break;
					case STATUS_ACTION_UNINSTALLING__:
						sb.append(PREFIX).append("Uninstalling");
						cellStyle.append("_cna=portlet_icon_connection");
						break;
					case STATUS_ACTION_INSTALLING____:
						sb.append(PREFIX).append("Installing");
						cellStyle.append("_cna=portlet_icon_connection");
						break;
					case STATUS_ACTION_RUNNING_SCRIPT:
						sb.append(PREFIX).append("Running Custom Script");
						cellStyle.append("_cna=portlet_icon_play");
						break;
					case STATUS_ACTION_RETRIEVING____:
						sb.append(PREFIX).append("Retrieving");
						cellStyle.append("_cna=portlet_icon_connection");
						break;
					case STATUS_ACTION_VERIFYING_____:
						sb.append(PREFIX).append("Verifying");
						cellStyle.append("_cna=portlet_icon_environment");
						break;
					default:
						sb.append(PREFIX).append("Unknown status: ");
						statusBitsDescription.toString(status, sb);
						cellStyle.append("_cna=portlet_icon_warning");
						break;
				}
			}
		}
	}

	class InstallStatusFormatter extends BasicWebCellFormatter {

		@Override
		public void formatCellToHtml(Object value, StringBuilder sb, StringBuilder cellStyle) {
			if (value == null)
				return;

			int rawstatus = (Integer) value;
			int status = MASK_DEPLOY & (Integer) rawstatus;

			if (MH.anyBits(rawstatus, STATUS_DEPLOY_GENERAL_ERROR_)) {
				sb.append(PREFIX).append("Unknown Error");
				cellStyle.append("_cna=portlet_icon_error");
			} else if (rawstatus == STATUS_PROCESS_AGENT_DOWN___) {
				sb.append(PREFIX).append("Agent Not Running");
				cellStyle.append("_cna=portlet_icon_info");
			} else if (MH.anyBits(status, STATUS_DEPLOY_BAD_ROOT_PATH_)) {
				sb.append(PREFIX);
				statusBitsDescription.toString(status, sb);
				cellStyle.append("_cna=portlet_icon_error");
			} else if (MH.anyBits(status, STATUS_DEPLOY_SCRIPTS_BAD___ | STATUS_DEPLOY_FILE_MISMATCH_ | STATUS_DEPLOY_MANIFEST_BAD__)) {
				sb.append(PREFIX);
				statusBitsDescription.toString(status, sb);
				cellStyle.append("_cna=portlet_icon_warning");
			} else if (MH.anyBits(status, STATUS_DEPLOY_NO_MANIFEST___ | STATUS_DEPLOY_NOT_FOUND_____)) {
				sb.append(PREFIX);
				statusBitsDescription.toString(status, sb);
				cellStyle.append("_cna=portlet_icon_info");
			} else if (status == 0) {
				cellStyle.append("_cna=");
			} else {
				sb.append(PREFIX).append("Unknown status: ");
				statusBitsDescription.toString(status, sb);
				cellStyle.append("_cna=portlet_icon_warning");
			}
		}
	}

	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		metadataColumnManager.init(configuration, origToNewIdMapping, sb);
		super.init(configuration, origToNewIdMapping, sb);
	}
	@Override
	public Map<String, Object> getConfiguration() {
		return metadataColumnManager.getConfiguration(super.getConfiguration());
	}
	@Override
	public void onEyeSnapshotProcessed() {
		metadataColumnManager.onEyeSnapshotProcessed(service.getAgentManager());
	}

}
