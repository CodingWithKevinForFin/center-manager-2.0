package com.vortex.web.portlet.tables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.base.Action;
import com.f1.base.Row;
import com.f1.container.ResultMessage;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.MapWebCellFormatter;
import com.f1.suite.web.table.impl.WebTableFilteredSetFilter;
import com.f1.utils.ArchiveFileReader;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.LongSet;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.vortexcommon.msg.VortexEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexBuildResult;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageBuildResultRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeQueryDataRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeQueryDataResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunBuildProcedureResponse;
import com.vortex.client.VortexClientBuildProcedure;
import com.vortex.client.VortexClientBuildResult;
import com.vortex.client.VortexClientEntity;
import com.vortex.client.VortexClientMachine;
import com.vortex.client.VortexClientMachineListener;
import com.vortex.web.VortexWebEyeService;
import com.vortex.web.diff.DiffableArchiveNode;
import com.vortex.web.messages.VortexProcedureIdInterPortletMessage;
import com.vortex.web.portlet.forms.VortexWebCommentFormPortlet;
import com.vortex.web.portlet.forms.VortexWebRunProcedureFormPortlet;
import com.vortex.web.portlet.trees.VortexWebDiffTreePortlet;

public class VortexWebBuildResultTablePortlet extends VortexWebTablePortlet implements VortexClientMachineListener, WebContextMenuFactory, WebContextMenuListener {

	private static final Logger log = LH.get(VortexWebBuildResultTablePortlet.class);

	private VortexWebEyeService service;
	private LongKeyMap<Row> rows = new LongKeyMap<Row>();
	private BasicPortletSocket procedureIdSocket;
	private VortexWebMetadataColumnsManager metadataColumnManager;

	public VortexWebBuildResultTablePortlet(PortletConfig config) {
		super(config, null);
		String[] ids = { "id", "name", "now", "invokedby", "dlength", "start", "state", "host", BPID, "cmd", "err", "out", "in", "user", "file", "procname", "ecode", "checksum",
				"ver", "data", "variables", "dcount", "mdata", "vdlength", "vfile", "vchecksum" };
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("Build Results");
		SmartTable st = new BasicSmartTable(inner);
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);

		MapWebCellFormatter<Byte> stateFormatter = new MapWebCellFormatter<Byte>(getManager().getTextFormatter());
		stateFormatter.addEntry(VortexBuildResult.STATE_FAILURE, "Failure", "_cna=portlet_icon_warning", "&nbsp;&nbsp;&nbsp;&nbsp;Failure");
		stateFormatter.addEntry(VortexBuildResult.STATE_ERROR, "Error", "_cna=portlet_icon_error", "&nbsp;&nbsp;&nbsp;&nbsp;Error");
		stateFormatter.addEntry(VortexBuildResult.STATE_RUNNING, "Running", "_cna=portlet_icon_process", "&nbsp;&nbsp;&nbsp;&nbsp;Running");
		stateFormatter.addEntry(VortexBuildResult.STATE_INIT, "Init", "_cna=portlet_icon_clock", "&nbsp;&nbsp;&nbsp;&nbsp;Init");
		stateFormatter.addEntry(VortexBuildResult.STATE_SUCCCESS, "Success", "_cna=portlet_icon_okay", "&nbsp;&nbsp;&nbsp;&nbsp;Success");
		stateFormatter.addEntry(VortexBuildResult.STATE_TRANSFERRING, "Transferring", "_cna=portlet_icon_connection", "&nbsp;&nbsp;&nbsp;&nbsp;Transferring");
		stateFormatter.addEntry(VortexBuildResult.STATE_FILE_NOT_FOUND, "Output file not found", "_cna=portlet_icon_error", "&nbsp;&nbsp;&nbsp;&nbsp;Output File not found");
		stateFormatter.addEntry((byte) 100, "Deployed", "_cna=portlet_icon_okay_lock", "&nbsp;&nbsp;&nbsp;&nbsp;Deployed");
		stateFormatter.setDefaultWidth(70).lockFormatter();

		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());
		table.addColumn(true, "Id", "id", service.getIdFormatter("BR-"));
		table.addColumn(true, "Name", "name", service.getBasicFormatter()).setWidth(150).addCssClass("bold");
		table.addColumn(true, "Version", "ver", service.getBasicFormatter()).setWidth(150);
		table.addColumn(true, "File", "file", service.getFilenameFormatter());
		table.addColumn(true, "Checksum", "checksum", service.getChecksumFormatter());
		table.addColumn(true, "Invoked by", "invokedby", service.getBasicFormatter());
		table.addColumn(true, "File Size", "dlength", service.getMemoryFormatter());
		table.addColumn(true, "Started", "start", service.getDateTimeWebCellFormatter());
		table.addColumn(true, "Updated", "now", service.getDateTimeWebCellFormatter());
		table.addColumn(true, "State", "state", stateFormatter);
		table.addColumn(true, "Exit Code", "ecode", service.getWarningNumberFormatter());
		table.addColumn(true, "Stdout", "out", service.getBasicFormatter());
		table.addColumn(true, "Stderr", "err", service.getBasicFormatter());
		table.addColumn(true, "Stdin", "in", service.getBasicFormatter());
		table.addColumn(true, "Procedure Host", "host", service.getHostnameFormatter());
		table.addColumn(true, "Deployments", "dcount", service.getNumberFormatter());
		table.addColumn(false, "Command", "cmd", service.getBasicFormatter());
		table.addColumn(false, "Procedure Name", "procname", service.getBasicFormatter());
		table.addColumn(false, "Variables", "variables", service.getBasicFormatter());
		table.addColumn(false, "Procedure Id", BPID, service.getIdFormatter("BP-"));

		table.addColumn(false, "Verify File Size", "vdlength", service.getMemoryFormatter());
		table.addColumn(false, "Verify File", "vfile", service.getFilenameFormatter());
		table.addColumn(false, "Verify Checksum", "vchecksum", service.getChecksumFormatter());
		table.setMenuFactory(this);
		this.procedureIdSocket = addSocket(false, "procedureId", "build Procedure ID", true, null, CH.s(VortexProcedureIdInterPortletMessage.class));
		agentManager.addMachineListener(this);
		setTable(table);
		metadataColumnManager = new VortexWebMetadataColumnsManager(getTable(), VortexAgentEntity.TYPE_BUILD_RESULT, "mdata");

		for (VortexClientBuildResult br : service.getAgentManager().getBuildResults())
			onMachineEntityAdded(br);
	}

	@Override
	public void close() {
		agentManager.removeMachineListener(this);
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
		if (node.getType() == VortexAgentEntity.TYPE_BUILD_RESULT) {
			VortexClientBuildResult t = (VortexClientBuildResult) node;
			addBuildResultRow(t);
		}
		metadataColumnManager.onMachineEntityAdded(node);
	}

	private void addBuildResultRow(VortexClientBuildResult node) {
		VortexBuildResult br = node.getData();
		Row existing = rows.get(node.getId());
		String name = br.getName();
		long now = br.getNow();
		String invokedby = br.getInvokedBy();
		Long dlength = br.getDataLength() == -1 ? null : br.getDataLength();
		Long vdlength = br.getVerifyDataLength() == -1 ? null : br.getVerifyDataLength();
		long start = br.getStartTime();
		byte state = br.getState();
		int dcount = node.getDeployments().size();
		if (state == VortexBuildResult.STATE_SUCCCESS && dcount > 0)
			state = 100;//TODO: HACK, this should be determined by the eye
		String cmd = br.getBuildCommand();
		String err = state == VortexBuildResult.STATE_RUNNING ? null : formatData(br.getBuildStderr(), br.getBuildStderrLength());
		String out = state == VortexBuildResult.STATE_RUNNING ? null : formatData(br.getBuildStdout(), br.getBuildStdoutLength());
		String in = br.getBuildStdin();
		String user = br.getBuildUser();
		String file = br.getFile();
		String vfile = br.getVerifyFile();
		String procname = br.getProcedureName();
		String ver = br.getVersion();
		Map<String, String> variables = br.getBuildVariables();
		Integer ecode = br.getBuildExitcode();
		long checksum = br.getDataChecksum();
		long vchecksum = br.getVerifyDataChecksum();
		String host = null;
		VortexClientMachine machine = service.getAgentManager().getAgentMachineByUid(br.getBuildMachineUid());
		if (machine != null)
			host = machine.getHostName();
		if (existing == null) {
			rows.put(
					node.getId(),
					addRow(node.getId(), name, now, invokedby, dlength, start, state, host, br.getProcedureId(), cmd, err, out, in, user, file, procname, ecode, checksum, ver,
							node, variables, dcount, br.getMetadata(), vdlength, vfile, vchecksum));
		} else {
			existing.put("name", name);
			existing.put("now", now);
			existing.put("state", state);
			existing.put("cmd", cmd);
			existing.put("err", err);
			existing.put("out", out);
			existing.put("in", in);
			existing.put("user", user);
			existing.put("file", file);
			existing.put("procname", procname);
			existing.put("ecode", ecode);
			existing.put("dlength", dlength);
			existing.put("checksum", checksum);
			existing.put("ver", ver);
			existing.put("dcount", dcount);
			existing.put("variables", variables);
			existing.put("mdata", br.getMetadata());
			existing.put("vdlength", vdlength);
			existing.put("vfile", vfile);
			existing.put("vchecksum", vchecksum);
		}
	}
	private String formatData(byte[] data, long len) {
		if (data != null) {
			if (data.length == len)
				return new String(data);
			else
				return new String(data) + "... [" + (len - data.length) + " byte(s) supressed]";
		} else if (len < 0)
			return null;
		return SH.formatMemory(len);
	}

	private void removeBuildResult(VortexClientBuildResult node) {
		Row existing = rows.remove(node.getId());
		if (existing != null)
			removeRow(existing);
	}

	@Override
	public void onMachineEntityUpdated(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_BUILD_RESULT) {
			addBuildResultRow((VortexClientBuildResult) node);
		}
		metadataColumnManager.onMachineEntityUpdated(node);
	}

	@Override
	public void onMachineEntityRemoved(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_BUILD_RESULT) {
			VortexClientBuildResult rule = (VortexClientBuildResult) node;
			removeBuildResult(rule);
		}
		metadataColumnManager.onMachineEntityRemoved(node);
	}

	@Override
	public void onEyeDisconnected() {
		clearRows();
		rows.clear();
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebBuildResultTablePortlet> {

		public static final String ID = "BuildResultsTablePortlet";

		public Builder() {
			super(VortexWebBuildResultTablePortlet.class);
		}

		@Override
		public VortexWebBuildResultTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebBuildResultTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Build Results Table";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
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
		List<WebMenuItem> children = new ArrayList<WebMenuItem>();
		List<Row> sel = table.getSelectedRows();
		int cnt = sel.size();
		if (cnt > 0) {
			if (cnt == 2) {
				children.add(new BasicWebMenuLink("Show Difference Tree", true, "diff"));
			}
			if (cnt == 1) {
				children.add(new BasicWebMenuLink("Show Details", true, "details"));
				children.add(new BasicWebMenuLink("Rerun", true, "rerun"));
			}
			for (Row row : sel) {
				if (row.get("dcount", Integer.class) == 0) {
					children.add(new BasicWebMenuLink("Delete Build Result(s)", true, "delete"));
					break;
				}
			}
		}
		BasicWebMenu r = new BasicWebMenu("", true, children);

		return r;
	}

	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket remoteSocket, InterPortletMessage message) {
		//if (localSocket == recvMachineSocket) {
		//VortexUtils.applyIDFilter((VortexInterPortletMessage) message, getTable());
		//onSelectedChanged(getTable());
		//} else 
		if (localSocket == procedureIdSocket) {
			VortexProcedureIdInterPortletMessage msg = (VortexProcedureIdInterPortletMessage) message;
			getTable().setExternalFilter(new WebTableFilteredSetFilter(getTable().getColumn(BPID), msg.getProcedureIds()));
			onVortexRowsChanged();
		} else {
			super.onMessage(localSocket, remoteSocket, message);
		}
	}
	@Override
	public void onContextMenu(WebTable table, String action) {
		if ("delete".equals(action)) {
			List<VortexEyeRequest> reqs = new ArrayList<VortexEyeRequest>();
			for (Row row : table.getSelectedRows()) {
				Long id = row.get("id", Long.class);
				VortexClientBuildResult br = service.getAgentManager().getBuildResult(id);
				if (br != null && br.getDeployments().size() == 0) {
					VortexEyeManageBuildResultRequest request = nw(VortexEyeManageBuildResultRequest.class);
					VortexBuildResult t = nw(VortexBuildResult.class);
					t.setId(id);
					t.setRevision(VortexAgentEntity.REVISION_DONE);
					request.setBuildResult(t);
					reqs.add(request);
				}

			}
			getManager().showDialog("Delete Build Result",
					new VortexWebCommentFormPortlet(generateConfig(), getPortletId(), reqs, "Delete Build Result", "backupdest.jpg").setIconToDelete());
		} else if ("diff".equals(action)) {
			LongSet ids = new LongSet();
			for (Row row : table.getSelectedRows())
				ids.add(row.get("id", Long.class));
			if (ids.size() == 2) {
				VortexEyeQueryDataRequest request = nw(VortexEyeQueryDataRequest.class);
				request.setIds(ids.toLongArray());
				request.setType(VortexAgentEntity.TYPE_BUILD_RESULT);
				service.sendRequestToBackend(getPortletId(), request);
			} else
				getManager().showAlert("Diff process requires two disctict elements. Please select exactly two rows and try again.");
		} else if ("details".equals(action)) {
			LongSet ids = new LongSet();
			for (Row row : table.getSelectedRows())
				ids.add(row.get("id", Long.class));
			if (ids.size() == 1) {
				VortexEyeQueryDataRequest request = nw(VortexEyeQueryDataRequest.class);
				request.setIds(ids.toLongArray());
				request.setType(VortexAgentEntity.TYPE_BUILD_RESULT);
				service.sendRequestToBackend(getPortletId(), request);
			} else
				getManager().showAlert("Show details only available for one element at a time. Please select exactly one row and try again.");
		} else if ("rerun".equals(action)) {
			if (table.getSelectedRows().size() == 1) {
				Row row = table.getSelectedRows().get(0);
				VortexClientBuildResult result = row.get("data", VortexClientBuildResult.class);
				VortexClientBuildProcedure bp = service.getAgentManager().getBuildProcedure(result.getData().getProcedureId());
				VortexBuildResult br = result.getData();
				if (bp == null)
					getManager().showAlert("Build Procedure no longer available for this build result.");
				else {
					VortexWebRunProcedureFormPortlet p = new VortexWebRunProcedureFormPortlet(generateConfig(), getPortletId());
					p.setBuildProcedures(CH.l(bp));
					if (br.getBuildVariables() != null)
						p.setBuildResult(br);
					getManager().showDialog("Rerun Build Result - " + p.describe(), p);
				}
			} else
				getManager().showAlert("Rerun only available for one element at a time. Please select exactly one row and try again.");
		}
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		Action action = result.getAction();
		if (action instanceof VortexEyeQueryDataResponse) {
			final List<VortexBuildResult> results = new ArrayList<VortexBuildResult>(2);
			for (VortexEntity row : ((VortexEyeQueryDataResponse) action).getData()) {
				VortexBuildResult vbr = (VortexBuildResult) row;
				results.add(vbr);
			}
			if (results.size() == 2) {
				ArchiveFileReader afr = new ArchiveFileReader();
				TabPortlet tabs = new TabPortlet(generateConfig());
				tabs.setIsCustomizable(false);
				try {
					VortexBuildResult leftNode = results.get(0);
					VortexBuildResult rightNode = results.get(1);
					if (leftNode.getData() != null && rightNode.getData() != null) {
						DiffableArchiveNode left = new DiffableArchiveNode(afr.read("Build Result", leftNode.getData()));
						DiffableArchiveNode right = new DiffableArchiveNode(afr.read("Build Result", rightNode.getData()));
						VortexWebDiffTreePortlet diffTree = new VortexWebDiffTreePortlet(generateConfig(), describe(leftNode), left, describe(rightNode), right);
						tabs.addChild("Output", diffTree);
					}
				} catch (IOException e) {
					LH.log(log, Level.WARNING, "error on build result", e);
				}
				if (false)
					try {
						VortexBuildResult leftNode = results.get(0);
						VortexBuildResult rightNode = results.get(0);
						DiffableArchiveNode left = new DiffableArchiveNode(afr.read("Build Verify Result", leftNode.getVerifyData()));
						DiffableArchiveNode right = new DiffableArchiveNode(afr.read("Build Verify Result", leftNode.getVerifyData()));
						DividerPortlet div = new DividerPortlet(generateConfig(), false);
						VortexWebDiffTreePortlet diffTree = new VortexWebDiffTreePortlet(generateConfig(), describe(leftNode), left, describe(rightNode), right);
						tabs.addChild("Verify Output", diffTree);
					} catch (IOException e) {
						LH.log(log, Level.WARNING, "error on build result", e);
					}
				getManager().showDialog("Diff", tabs);
			} else if (results.size() == 1) {
				ArchiveFileReader afr = new ArchiveFileReader();
				try {
					VortexBuildResult br = results.get(0);
					TabPortlet tabs = new TabPortlet(generateConfig());
					tabs.setIsCustomizable(false);
					Portlet stdout = new HtmlPortlet(generateConfig(), SH.dddMiddle(toString(br.getBuildStdout()), 1024 * 1024));
					Portlet stderr = new HtmlPortlet(generateConfig(), SH.dddMiddle(toString(br.getBuildStderr()), 1024 * 1024));
					tabs.addChild("stdout - " + SH.formatMemory(br.getBuildStdoutLength()), stdout);
					tabs.addChild("stderr - " + SH.formatMemory(br.getBuildStderrLength()), stderr);
					if (br.getData() != null) {
						DiffableArchiveNode left = new DiffableArchiveNode(afr.read(br.getFile(), br.getData()));
						VortexWebDiffTreePortlet diffTree = new VortexWebDiffTreePortlet(generateConfig(), describe(br), left, null, null);
						tabs.addChild("Output - " + br.getFile(), diffTree);
					}
					if (br.getVerifyData() != null) {
						DiffableArchiveNode left = new DiffableArchiveNode(afr.read(br.getVerifyFile(), br.getVerifyData()));
						VortexWebDiffTreePortlet diffTree = new VortexWebDiffTreePortlet(generateConfig(), describe(br), left, null, null);
						tabs.addChild("Verify Output - " + br.getVerifyFile(), diffTree);
					}
					tabs.setTitle("BR-" + br.getId());
					getManager().showDialog("BR-" + br.getId(), tabs);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if (action instanceof VortexEyeRunBuildProcedureResponse) {
			VortexEyeRunBuildProcedureResponse response = (VortexEyeRunBuildProcedureResponse) result.getAction();
			if (!response.getOk())
				getManager().showAlert(response.getMessage());
		} else
			super.onBackendResponse(result);
	}

	private String describe(VortexBuildResult node) {
		return "BR-" + node.getId() + " - " + node.getVersion();
	}
	private String toString(byte[] std) {
		return std == null ? "" : "<pre>" + new String(std) + "</pre>";
	}

	@Override
	public void onMachineRemoved(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}

	protected void onVortexRowsChanged() {
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
