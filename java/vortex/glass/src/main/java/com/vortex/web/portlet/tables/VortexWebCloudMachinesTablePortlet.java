package com.vortex.web.portlet.tables;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.base.Action;
import com.f1.base.Row;
import com.f1.base.ValuedParam;
import com.f1.container.ResultMessage;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletErrorField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.FormPortletValidator;
import com.f1.suite.web.portal.impl.form.FormPortletValidator.FormPortletValidatorListener;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.MapWebCellFormatter;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.vortexcommon.msg.VortexEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortextEyeCloudMachineInfo;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeCIMachineOPRequest;
import com.vortex.client.VortexClientCloudMachineInfo;
import com.vortex.client.VortexClientEntity;
import com.vortex.client.VortexClientMachine;
import com.vortex.client.VortexClientMachineListener;
import com.vortex.web.VortexUtils;
import com.vortex.web.messages.VortexInterPortletMessage;

public class VortexWebCloudMachinesTablePortlet extends VortexWebTablePortlet implements WebContextMenuFactory, VortexClientMachineListener {

	private LongKeyMap<Row> rowsById = new LongKeyMap<Row>();
	private Map<String, Row> rowsByHost = new HashMap<String, Row>();

	private BasicPortletSocket ciIdSocket;

	public VortexWebCloudMachinesTablePortlet(PortletConfig config) {
		super(config, null);
		VortextEyeCloudMachineInfo sample = nw(VortextEyeCloudMachineInfo.class);
		BasicTable inner = (BasicTable) TableHelper.toTable(CH.l(sample));
		inner.clear();
		inner.addColumn(Boolean.class, "agent");
		inner.addColumn(Boolean.class, "agent_state");
		inner.setTitle("Cloud Machines");
		SmartTable st = new BasicSmartTable(inner);

		MapWebCellFormatter<Boolean> booleanFormatter = new MapWebCellFormatter<Boolean>(getManager().getTextFormatter());
		booleanFormatter.addEntry(false, false, "_cna=portlet_icon_error", "&nbsp;&nbsp;&nbsp;&nbsp");
		booleanFormatter.addEntry(null, null, "_cna=portlet_icon_error", "&nbsp;&nbsp;&nbsp;&nbsp");
		booleanFormatter.addEntry(true, true, "_cna=portlet_icon_okay", "&nbsp;&nbsp;&nbsp;&nbsp");
		booleanFormatter.setDefaultWidth(80).lockFormatter();

		MapWebCellFormatter<Short> opFormatter = new MapWebCellFormatter<Short>(getManager().getTextFormatter());
		opFormatter.addEntry(VortexEyeCIMachineOPRequest.OP_START, VortexEyeCIMachineOPRequest.OP_START, "", "Start");
		opFormatter.addEntry(VortexEyeCIMachineOPRequest.OP_STOP, VortexEyeCIMachineOPRequest.OP_STOP, "", "Stop");
		opFormatter.addEntry(VortexEyeCIMachineOPRequest.OP_DEPLOY, VortexEyeCIMachineOPRequest.OP_DEPLOY, "", "Deploy");
		opFormatter.addEntry(VortexEyeCIMachineOPRequest.OP_DUP, VortexEyeCIMachineOPRequest.OP_DUP, "", "Request more instances");
		opFormatter.addEntry(VortexEyeCIMachineOPRequest.OP_TERMINATE, VortexEyeCIMachineOPRequest.OP_TERMINATE, "", "Terminate");
		opFormatter.setDefaultWidth(60).lockFormatter();

		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());

		table.addColumn(false, "Id", "id", service.getBasicFormatter());
		table.addColumn(false, "CIId", "cIId", service.getBasicFormatter());
		table.addColumn(true, "Last Command", "lastOP", opFormatter);
		table.addColumn(true, "Last Command Status", "lastOPStatus", service.getBasicFormatter());
		table.addColumn(true, "Agent Present", "agent", booleanFormatter);
		table.addColumn(true, "Agent State", "agent_state", booleanFormatter);
		table.addColumn(true, "Vendor", "cIName", service.getBasicFormatter());
		table.addColumn(true, "Name", "name", service.getBasicFormatter());
		table.addColumn(true, "InstanceId", "instanceId", service.getBasicFormatter());
		table.addColumn(true, "Status", "status", service.getBasicFormatter());
		table.addColumn(true, "Private IP", "privateIP", service.getBasicFormatter());
		table.addColumn(true, "Public IP", "publicIP", service.getBasicFormatter());
		table.addColumn(true, "OS", "oS", service.getBasicFormatter());
		table.addColumn(true, "Data As Of", "asOf", service.getDateTimeWebCellFormatter());
		table.addColumn(true, "Created", "createTime", service.getDateTimeWebCellFormatter());
		table.addColumn(true, "Type", "instanceType", service.getBasicFormatter());
		table.addColumn(true, "Key Name", "keyName", service.getBasicFormatter());

		table.setMenuFactory(this);
		agentManager.addMachineListener(this);
		setTable(table);

		this.ciIdSocket = addSocket(false, "cIID", "Send Clound Interface Id", true, null, CH.s(VortexInterPortletMessage.class));

		for (VortexClientCloudMachineInfo mi : service.getAgentManager().getCloudMachinesInfo())
			onMachineEntityAdded(mi);
	}

	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket origin, InterPortletMessage message) {
		if (localSocket == ciIdSocket) {
			VortexInterPortletMessage m = (VortexInterPortletMessage) message;
			VortexUtils.applyIDFilter(m, getTable(), "cIId");
		}
	}

	@Override
	public void onMachineAdded(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMachineUpdated(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMachineStale(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMachineEntityAdded(VortexClientEntity<?> node) {
		switch (node.getType()) {
			case VortexAgentEntity.TYPE_CLOUD_MACHINE_INFO:
				addVortexRow((VortexClientCloudMachineInfo) node);
				break;
			case VortexAgentEntity.TYPE_MACHINE:
				updateAgent((VortexClientMachine) node);
				break;
		}
	}
	private void addVortexRow(VortexClientCloudMachineInfo node) {
		VortextEyeCloudMachineInfo data = node.getData();
		Row row = from(node, null);

		rowsById.put(data.getId(), row);
		rowsByHost.put(data.getName(), row);
	}
	private void removeVortexRow(VortexClientCloudMachineInfo node) {
		VortextEyeCloudMachineInfo data = node.getData();
		Row row = rowsById.remove(data.getId());
		rowsByHost.remove(data.getName());
		removeRow(row);
	}
	private void updateVortexRow(VortexClientCloudMachineInfo node) {
		Row row = rowsById.get(node.getId());
		rowsByHost.put(node.getData().getName(), row);
		from(node, row);
	}

	private Row from(VortexClientEntity<?> node, Row r) {
		VortexEntity e = node.getData();
		if (r == null)
			r = getTable().getTable().newEmptyRow();

		for (ValuedParam p : e.askSchema().askValuedParams())
			r.put(p.getName(), p.getValue(e));

		updateAgent(r);

		return r;
	}

	@Override
	public void onMachineEntityUpdated(VortexClientEntity<?> node) {
		switch (node.getType()) {
			case VortexAgentEntity.TYPE_CLOUD_MACHINE_INFO:
				updateVortexRow((VortexClientCloudMachineInfo) node);
				break;
			case VortexAgentEntity.TYPE_MACHINE:
				updateAgent((VortexClientMachine) node);
				break;
		}
	}

	@Override
	public void onMachineEntityRemoved(VortexClientEntity<?> node) {
		switch (node.getType()) {
			case VortexAgentEntity.TYPE_CLOUD_MACHINE_INFO:
				removeVortexRow((VortexClientCloudMachineInfo) node);
				break;
			case VortexAgentEntity.TYPE_MACHINE:
				updateAgent((VortexClientMachine) node);
				break;
		}
	}

	private void updateAgent(VortexClientMachine m) {
		Row r = rowsByHost.get(stripDomain(m.getHostName()));
		if (r != null) {
			r.put("agent", true);
			r.put("agent_state", m.getIsRunning());
		}
	}

	private String stripDomain(String fqdn) {
		return SH.split('.', fqdn)[0];
	}

	private void updateAgent(Row r) {
		String name = (String) r.get("name");
		if (SH.isnt(name))
			return;

		boolean present = false;
		boolean running = false;
		for (VortexClientMachine m : agentManager.getAgentMachines()) {
			if (SH.equals(name, stripDomain(m.getHostName()))) {
				present = true;
				running = m.getIsRunning();
				break;
			}
		}

		r.put("agent", present);
		r.put("agent_state", running);
	}

	@Override
	public WebMenu createMenu(WebTable table) {
		BasicWebMenu r = null;

		if (table.hasSelectedRows()) {
			r = new BasicWebMenu();

			if (table.getSelectedRows().size() == 1)
				r.addChild(new BasicWebMenuLink("Launch More Like This", true, "launch_more"));

			if (table.getSelectedRows().size() >= 1) {
				r.addChild(new BasicWebMenuLink("Stop", true, "stop"));
				r.addChild(new BasicWebMenuLink("Start", true, "start"));
				r.addChild(new BasicWebMenuLink("Start Agent", true, "start_agent"));
				r.addChild(new BasicWebMenuLink("Terminate", true, "terminate"));
				r.addChild(new BasicWebMenuLink("Prepare and Deploy Agent", true, "deploy"));
			}

		}
		return r;
	}
	@Override
	public void onContextMenu(final WebTable table, String action) {
		if (CH.isEmpty(table.getSelectedRows()))
			return;

		if ("terminate".equals(action)) {
			final FormPortlet f = new FormPortlet(generateConfig());
			f.addField(new FormPortletTitleField("Are you sure you want to terminate highlighted instance(s)?"));
			final FormPortletButton cancelBtn = new FormPortletButton("No");
			final FormPortletButton submitBtn = new FormPortletButton("Yes");
			f.addButton(cancelBtn);
			f.addButton(submitBtn);
			f.addFormPortletListener(new FormPortletListener() {

				@Override
				public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
				}

				@Override
				public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
				}

				@Override
				public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
					if (button == submitBtn) {
						for (Row r : table.getSelectedRows()) {
							Long id = (Long) r.get("id");
							String name = (String) r.get("name");
							if (id == null)
								return;

							final VortexEyeCIMachineOPRequest req = nw(VortexEyeCIMachineOPRequest.class);
							req.setId(id);
							req.setOp(VortexEyeCIMachineOPRequest.OP_TERMINATE);
							service.sendRequestToBackend(getPortletId(), req);
						}
					}

					f.close();
				}
			});
			getManager().showDialog("Terminating Instances", f, 500, 100);
		}

		for (Row r : table.getSelectedRows()) {
			Long id = (Long) r.get("id");
			String name = (String) r.get("name");
			if (id == null)
				return;

			final VortexEyeCIMachineOPRequest req = nw(VortexEyeCIMachineOPRequest.class);
			req.setId(id);

			if ("stop".equals(action)) {
				req.setOp(VortexEyeCIMachineOPRequest.OP_STOP);
				service.sendRequestToBackend(getPortletId(), req);
			} else if ("start".equals(action)) {
				req.setOp(VortexEyeCIMachineOPRequest.OP_START);
				service.sendRequestToBackend(getPortletId(), req);
			} else if ("deploy".equals(action)) {
				req.setOp(VortexEyeCIMachineOPRequest.OP_DEPLOY);
				service.sendRequestToBackend(getPortletId(), req);
			} else if ("start_agent".equals(action)) {
				req.setOp(VortexEyeCIMachineOPRequest.OP_START_AGENT);
				service.sendRequestToBackend(getPortletId(), req);
			} else if ("launch_more".equals(action)) {
				final FormPortlet f = new FormPortlet(generateConfig());
				f.addField(new FormPortletTitleField("Launching new instances like " + name));
				final FormPortletField<String> fpName = new FormPortletErrorField<String>(new FormPortletTextField("Name"));
				fpName.setName("name");
				f.addField(fpName);
				final FormPortletNumericRangeField fpNum = new FormPortletNumericRangeField("Number of instances").setValue(1).setRange(1, 250);
				fpNum.setName("num");
				f.addField(fpNum);
				final FormPortletButton cancelBtn = new FormPortletButton("Cancel");
				final FormPortletButton submitBtn = new FormPortletButton("Submit");
				submitBtn.setEnabled(false);
				f.addButton(cancelBtn);
				f.addButton(submitBtn);

				FormPortletValidator fpv = new FormPortletValidator(f);
				fpv.add(fpName, "name != null && name != \"\"", "Name is required");
				for (int i = 0; i < table.getRowsCount(); i++) {
					String n = SH.toString(table.getRow(i).get("name"));
					fpv.add(fpName, SH.join("", "name!=\"", n, "\""), n + " is already taken");
				}
				fpv.setListener(new FormPortletValidatorListener() {
					@Override
					public void setValid(boolean valid, List<String> reasons) {
						submitBtn.setEnabled(valid);
					}

					@Override
					public void setValid(FormPortletField<?> field, boolean valid, List<String> reasons) {
					}
				});

				fpv.compile();

				f.addFormPortletListener(new FormPortletListener() {

					@Override
					public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
					}

					@Override
					public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
					}

					@Override
					public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
						if (button == submitBtn) {
							req.setOp(VortexEyeCIMachineOPRequest.OP_DUP);
							req.setName(fpName.getValue());
							req.setNumberOfInstances(fpNum.getValue().intValue());

							service.sendRequestToBackend(getPortletId(), req);
						}

						f.close();
					}
				});
				getManager().showDialog("Launch New Instances", f, 500, 200);

			}
		}
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		super.onBackendResponse(result);
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebCloudMachinesTablePortlet> {

		public static final String ID = "CloudMachinesTablePortlet";

		public Builder() {
			super(VortexWebCloudMachinesTablePortlet.class);
		}

		@Override
		public VortexWebCloudMachinesTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebCloudMachinesTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Cloud Machines Table";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onEyeDisconnected() {
		clearRows();
	}

	@Override
	public void onMachineRemoved(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}
}
