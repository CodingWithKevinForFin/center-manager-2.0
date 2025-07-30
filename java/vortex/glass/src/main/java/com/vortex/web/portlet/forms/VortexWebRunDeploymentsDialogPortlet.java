package com.vortex.web.portlet.forms;

import java.util.List;
import java.util.Map;

import com.f1.base.Action;
import com.f1.base.Row;
import com.f1.container.ResultMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.MapWebCellFormatter;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexDeployment;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunDeploymentRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunDeploymentResponse;
import com.vortex.client.VortexClientBuildResult;
import com.vortex.client.VortexClientDeployment;
import com.vortex.client.VortexClientEntity;
import com.vortex.client.VortexClientMachine;
import com.vortex.client.VortexClientMachineListener;
import com.vortex.web.VortexWebEyeService;

public class VortexWebRunDeploymentsDialogPortlet extends GridPortlet implements WebContextMenuListener, FormPortletListener, VortexClientMachineListener {

	private static final byte STATE_VERIFY_FAILURE = 1;
	private static final byte STATE_VERIFY_ERROR = 2;
	private static final byte STATE_VERIFY_RUNNING = 3;
	private static final byte STATE_VERIFY_SUCCCESS = 4;
	private static final byte STATE_VERIFY_SKIPPED = 9;
	private static final byte STATE_DEPLOYING = 5;
	private static final byte STATE_DEPLOYED = 6;
	private static final byte STATE_DEPLOY_FAILURE = 7;
	private static final byte STATE_DEPLOY_ERROR = 8;

	private final VortexWebEyeService service;
	private final FastTablePortlet table;
	private final LongKeyMap<Row> rows = new LongKeyMap<Row>();
	private HtmlPortlet header;
	private byte status;
	private int successes = 0;
	private DividerPortlet div;
	private HtmlPortlet stdoutArea;
	private HtmlPortlet stderrArea;
	private HtmlPortlet installStdoutArea;
	private HtmlPortlet installStderrArea;
	private TabPortlet tabs;
	private boolean onlyVerify;
	private String comment;
	private boolean isDeploying = false;
	private FormPortlet form;
	private FormPortletButton closeButton;

	public VortexWebRunDeploymentsDialogPortlet(PortletConfig config, boolean onlyVerify, String comment) {
		super(config);
		this.onlyVerify = onlyVerify;
		this.comment = comment;
		MapWebCellFormatter<Byte> stateFormatter = new MapWebCellFormatter<Byte>(getManager().getTextFormatter());
		stateFormatter.addEntry(STATE_VERIFY_FAILURE, "Failure on Verify", "className=portlet_icon_warning", "&nbsp;&nbsp;&nbsp;&nbsp;Failure on Verify");
		stateFormatter.addEntry(STATE_VERIFY_ERROR, "Error on Verify", "className=portlet_icon_error", "&nbsp;&nbsp;&nbsp;&nbsp;Error on Verify");
		stateFormatter.addEntry(STATE_VERIFY_RUNNING, "Verifying", "className=portlet_icon_process", "&nbsp;&nbsp;&nbsp;&nbsp;Verifying");
		stateFormatter.addEntry(STATE_VERIFY_SUCCCESS, "Verified", "className=portlet_icon_okay", "&nbsp;&nbsp;&nbsp;&nbsp;Verified");
		stateFormatter.addEntry(STATE_VERIFY_SKIPPED, "Skipped Verify", "className=portlet_icon_info", "&nbsp;&nbsp;&nbsp;&nbsp;Skipped Verify");
		stateFormatter.addEntry(STATE_DEPLOYING, "Deploying", "className=portlet_icon_connection", "&nbsp;&nbsp;&nbsp;&nbsp;Deploying");
		stateFormatter.addEntry(STATE_DEPLOYED, "Deployed", "className=portlet_icon_okay", "&nbsp;&nbsp;&nbsp;&nbsp;Deployed");
		stateFormatter.addEntry(STATE_DEPLOY_FAILURE, "Failure on Deploying", "className=portlet_icon_warning", "&nbsp;&nbsp;&nbsp;&nbsp;Failure on Deploying");
		stateFormatter.addEntry(STATE_DEPLOY_ERROR, "Error on Deploying", "className=portlet_icon_error", "&nbsp;&nbsp;&nbsp;&nbsp;Error on Deploying");
		stateFormatter.setDefaultWidth(80).lockFormatter();
		service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		BasicTable inner = new BasicTable(new Object[] { "depid", "brid", "version", "status", "message", "host", "desc", "bp", "res", "vars", "origmsg" });
		inner.setTitle("Database Tables");
		SmartTable st = new BasicSmartTable(inner);
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());

		table.addColumn(true, "Host", "host", service.getHostnameFormatter());
		table.addColumn(true, "Desciption", "desc", service.getBasicFormatter());
		table.addColumn(true, "Build Procedure", "bp", service.getBasicFormatter());
		table.addColumn(true, "Version", "version", service.getBasicFormatter());
		table.addColumn(true, "Status", "status", stateFormatter);
		table.addColumn(true, "Message", "message", service.getBasicFormatter()).setWidth(250);
		table.addColumn(true, "Deployment ID", "depid", service.getIdFormatter("DI-"));
		table.addColumn(true, "Build Result ID", "brid", service.getIdFormatter("BR-"));

		this.table = new FastTablePortlet(generateConfig(), table);
		setSuggestedSize(900, 600);

		addChild(header = new HtmlPortlet(generateConfig(), "", "comment_header"), 0, 0, 1, 1);
		setStatus(STATE_VERIFY_RUNNING);
		this.div = new DividerPortlet(generateConfig(), false);
		this.tabs = new TabPortlet(generateConfig());
		this.stdoutArea = new HtmlPortlet(generateConfig());
		this.stderrArea = new HtmlPortlet(generateConfig());
		this.installStdoutArea = new HtmlPortlet(generateConfig());
		this.installStderrArea = new HtmlPortlet(generateConfig());
		this.tabs.addChild("Verify Stdout", stdoutArea);
		this.tabs.addChild("Verify Stderr", stderrArea);
		this.tabs.addChild("Install Stdout", installStdoutArea);
		this.tabs.addChild("Install Stderr", installStderrArea);
		this.tabs.setIsCustomizable(false);
		this.div.addChild(this.table);
		this.table.getTable().addMenuListener(this);
		this.div.addChild(this.tabs);
		this.form = new FormPortlet(generateConfig());
		addChild(this.div, 0, 1, 1, 1);
		addChild(this.form, 0, 2, 1, 1);
		setRowSize(0, 120);
		setRowSize(2, 35);
		this.form.addFormPortletListener(this);
		this.service.getAgentManager().addMachineListener(this);
	}
	private void setStatus(byte status) {
		if (this.status == status)
			return;
		this.status = status;
		String image;
		if (status == STATE_VERIFY_RUNNING)
			image = "verify_env.jpg";
		else if (status == STATE_VERIFY_ERROR || status == STATE_VERIFY_FAILURE || status == STATE_DEPLOY_FAILURE || status == STATE_DEPLOY_ERROR)
			image = "failed.jpg";
		else if (status == STATE_VERIFY_SUCCCESS || status == STATE_DEPLOYED) {
			image = "okay.jpg";
		} else if (status == STATE_DEPLOYING) {
			image = "deploying.jpg";
		} else
			image = "";
		header.setHtml("<div style=\"width:100%;height:100%;background-image:url('rsc/headers/" + image
				+ "');background-repeat:no-repeat;background-position:center;text-align:center;padding:5px 5px\">");
	}
	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		VortexEyeRunDeploymentRequest req = (VortexEyeRunDeploymentRequest) result.getRequestMessage().getAction();
		VortexEyeRunDeploymentResponse res = (VortexEyeRunDeploymentResponse) result.getAction();
		Row row = rows.get(req.getDeploymentId());
		if (isDeploying) {
			byte status;
			String message = null;
			if (res.getOk()) {
				if (res.getInstallExitCode() == 0) {
					status = STATE_DEPLOYED;
					incrementSuccesses();
				} else {
					status = STATE_DEPLOY_FAILURE;
					setStatus(STATE_DEPLOY_FAILURE);
					message = "Install Script returned: " + res.getInstallExitCode();
					addCloseButton();
				}
			} else {
				status = STATE_DEPLOY_ERROR;
				setStatus(STATE_DEPLOY_FAILURE);
				message = res.getMessage();
				addCloseButton();
			}
			row.put("status", status);
			row.put("message", message);
			VortexEyeRunDeploymentResponse existing = row.get("res", VortexEyeRunDeploymentResponse.class);
			if (existing != null) {
				existing.setInstallExitCode(res.getInstallExitCode());
				existing.setInstallStderr(res.getInstallStderr());
				existing.setInstallStdout(res.getInstallStdout());
			}
			//row.put("res", res);
		} else {
			byte status;
			String message = null;
			if (res.getOk()) {
				if (res.getVerifyExitCode() == 0) {
					status = STATE_VERIFY_SUCCCESS;
					incrementSuccesses();
					if (!onlyVerify)
						message = "Waiting for all verifications before deploying";
				} else {
					status = STATE_VERIFY_FAILURE;
					setStatus(STATE_VERIFY_FAILURE);
					message = "Verify Script returned: " + res.getVerifyExitCode();
					addCloseButton();
				}
			} else {
				status = STATE_VERIFY_ERROR;
				setStatus(STATE_VERIFY_FAILURE);
				message = res.getMessage();
				addCloseButton();
			}
			//if (!isDeploying) {
			row.put("status", status);
			row.put("message", message);
			row.put("res", res);
			//}
		}
	}
	private void incrementSuccesses() {
		if (++successes == rows.size()) {
			if (isDeploying) {
				setStatus(STATE_DEPLOYED);
				addCloseButton();
			} else {
				if (onlyVerify) {
					setStatus(STATE_VERIFY_SUCCCESS);
					addCloseButton();
				} else {
					sendDeployRequests();
				}
			}
		}
	}
	private void addCloseButton() {
		if (this.closeButton == null)
			this.closeButton = this.form.addButton(new FormPortletButton("Close"));
	}
	private void sendDeployRequests() {

		isDeploying = true;
		this.successes = 0;
		setStatus(STATE_DEPLOYING);
		for (Row row : this.table.getTable().getTable().getRows()) {
			long depid = row.get("depid", long.class);
			long brid = row.get("brid", long.class);
			Map<String, String> vars = row.get("vars", Map.class);
			VortexEyeRunDeploymentRequest request = nw(VortexEyeRunDeploymentRequest.class);
			request.setDeploymentId(depid);
			request.setDeploymentVariables(vars);
			request.setBuildResultId(brid);
			request.setType(VortexEyeRunDeploymentRequest.TYPE_DEPLOY);
			request.setInvokedBy(getManager().getState().getWebState().getUser().getUserName());
			request.setComment(this.comment);
			service.sendRequestToBackend(getPortletId(), request);
			row.put("status", STATE_DEPLOYING);
			row.put("message", "");
		}
	}
	public void addRow(VortexClientDeployment deployment, VortexClientBuildResult buildResult, Map<String, String> vars) {
		Row row = table
				.getTable()
				.getTable()
				.getRows()
				.addRow(deployment.getId(), buildResult.getId(), buildResult.getData().getVersion(), STATE_VERIFY_RUNNING, "", deployment.getHostName(),
						deployment.getData().getDescription(), deployment.getBuildProcedure().getData().getName(), null, vars, deployment.getData().getMessage());
		this.rows.put(deployment.getId(), row);
	}
	@Override
	public void onContextMenu(WebTable table, String action) {

	}
	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
		List<Row> sel = fastWebTable.getSelectedRows();
		VortexEyeRunDeploymentResponse res = sel.isEmpty() ? null : sel.get(0).get("res", VortexEyeRunDeploymentResponse.class);
		if (res != null) {
			stderrArea.setHtml("<pre>" + WebHelper.formatForPre(res.getVerifyStderr()) + "</pre>");
			stdoutArea.setHtml("<pre>" + WebHelper.formatForPre(res.getVerifyStdout()) + "</pre>");
			installStderrArea.setHtml("<pre>" + WebHelper.formatForPre(res.getInstallStderr()) + "</pre>");
			installStdoutArea.setHtml("<pre>" + WebHelper.formatForPre(res.getInstallStdout()) + "</pre>");
		} else {
			stderrArea.setHtml("");
			stdoutArea.setHtml("");
			installStderrArea.setHtml("");
			installStdoutArea.setHtml("");
		}
	}
	@Override
	public void onVisibleRowsChanged(FastWebTable fastWebTable) {

	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		close();
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {

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
	public void onMachineRemoved(VortexClientMachine machine) {

	}
	@Override
	public void onMachineEntityAdded(VortexClientEntity<?> node) {

	}
	@Override
	public void onMachineEntityUpdated(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_DEPLOYMENT) {
			VortexDeployment dep = (VortexDeployment) node.getData();
			Row row = rows.get(dep.getId());
			if (row != null && SH.is(dep.getMessage()) && OH.ne(dep.getMessage(), row.get("origmsg"))) {
				row.put("message", "From Agent: " + dep.getMessage());
				row.put("origmsg", dep.getMessage());
			}
		}
	}
	@Override
	public void onMachineEntityRemoved(VortexClientEntity<?> node) {

	}
	@Override
	public void close() {
		service.getAgentManager().removeMachineListener(this);
		super.close();
	}
	public void sendVerify() {
		setStatus(STATE_VERIFY_RUNNING);
		for (Row row : this.table.getTable().getTable().getRows()) {
			long depid = row.get("depid", long.class);
			long brid = row.get("brid", long.class);
			if (SH.isnt(service.getAgentManager().getDeployment(depid).getData().getVerifyScriptFile())) {
				row.put("status", STATE_VERIFY_SKIPPED);
				row.put("message", "No verification script defined");
				incrementSuccesses();
			} else {
				Map<String, String> vars = row.get("vars", Map.class);
				VortexEyeRunDeploymentRequest request = nw(VortexEyeRunDeploymentRequest.class);
				request.setDeploymentId(depid);
				request.setDeploymentVariables(vars);
				request.setBuildResultId(brid);
				request.setType(VortexEyeRunDeploymentRequest.TYPE_VERIFY);
				request.setInvokedBy(getManager().getState().getWebState().getUser().getUserName());
				request.setComment(comment);
				service.sendRequestToBackend(getPortletId(), request);
			}
		}

	}
	@Override
	public void onMachineActive(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onCellMousedown(WebTable table, Row row, WebColumn col) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onNoSelectedChanged(FastWebTable fastWebTable) {
		// TODO Auto-generated method stub

	}
}
