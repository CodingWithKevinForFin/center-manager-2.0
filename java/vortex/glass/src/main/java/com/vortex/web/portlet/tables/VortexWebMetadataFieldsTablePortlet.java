package com.vortex.web.portlet.tables;

import java.util.ArrayList;
import java.util.List;

import com.f1.base.Row;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.MapWebCellFormatter;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexEyeMetadataField;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageMetadataFieldRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRequest;
import com.vortex.client.VortexClientEntity;
import com.vortex.client.VortexClientMachine;
import com.vortex.client.VortexClientMachineListener;
import com.vortex.client.VortexClientMetadataField;
import com.vortex.web.VortexWebEyeService;
import com.vortex.web.portlet.forms.VortexWebCommentFormPortlet;
import com.vortex.web.portlet.forms.VortexWebMetadataFieldFormPortlet;

public class VortexWebMetadataFieldsTablePortlet extends VortexWebTablePortlet implements WebContextMenuFactory, VortexClientMachineListener {

	private VortexWebEyeService service;
	final private LongKeyMap<Row> rows = new LongKeyMap<Row>();

	public VortexWebMetadataFieldsTablePortlet(PortletConfig config) {
		super(config, null);

		String[] ids = { MFID, NOW, "ttypes", "vtype", "req", "key", "title", "maxlen", "enums", "desc", "maxval", "minval", "validations", "data" };
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("Metadata Fields");
		SmartTable st = new BasicSmartTable(inner);
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);

		MapWebCellFormatter<Byte> typeFormatter = new MapWebCellFormatter<Byte>(getManager().getTextFormatter());
		typeFormatter.addEntry(VortexEyeMetadataField.VALUE_TYPE_STRING, "String");
		typeFormatter.addEntry(VortexEyeMetadataField.VALUE_TYPE_BOOLEAN, "Boolean");
		typeFormatter.addEntry(VortexEyeMetadataField.VALUE_TYPE_INT, "Integer");
		typeFormatter.addEntry(VortexEyeMetadataField.VALUE_TYPE_DOUBLE, "Double");
		typeFormatter.addEntry(VortexEyeMetadataField.VALUE_TYPE_ENUM, "Enum");
		typeFormatter.setDefaultWidth(80).lockFormatter();

		//MapWebCellFormatter<Boolean> requiredFormatter = new MapWebCellFormatter<Boolean>(getManager().getTextFormatter());
		//requiredFormatter.addEntry(true, "Required");
		//requiredFormatter.addEntry(false, "Optional");
		//requiredFormatter.setDefaultWidth(60).lockFormatter();

		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());
		table.addColumn(true, "Id", MFID, service.getIdFormatter("MF-"));
		table.addColumn(true, "Key", "key", service.getBasicFormatter());
		table.addColumn(true, "Title", "title", service.getBasicFormatter());
		table.addColumn(true, "Applicable To", "ttypes", service.getAgentTypeMaskFormatter());
		table.addColumn(true, "Type", "vtype", typeFormatter);
		//table.addColumn(true, "Required", "req", requiredFormatter);
		table.addColumn(true, "Desciption", "desc", service.getBasicFormatter());
		table.addColumn(true, "Validations", "validations", service.getBasicFormatter());
		table.addColumn(false, "maxlen", "maxlen", service.getNumberFormatter());
		table.addColumn(false, "Enum Values", "enums", service.getBasicFormatter());
		table.addColumn(false, "Min Value", "maxval", service.getNumberFormatter());
		table.addColumn(false, "Max Value", "minval", service.getNumberFormatter());
		table.addColumn(false, "Updated", NOW, service.getDateTimeWebCellFormatter());
		table.setMenuFactory(this);
		agentManager.addMachineListener(this);
		setTable(table);
		for (VortexClientMetadataField bp : service.getAgentManager().getMetadataFields())
			onMachineEntityAdded(bp);
	}

	@Override
	public void close() {
		agentManager.removeMachineListener(this);
		super.close();
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
	public void onMachineRemoved(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMachineEntityAdded(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_METADATA_FIELD) {
			VortexClientMetadataField metadataField = (VortexClientMetadataField) node;
			addMetadata(metadataField);
		}
	}

	@Override
	public void onMachineEntityUpdated(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_METADATA_FIELD) {
			VortexClientMetadataField metadataField = (VortexClientMetadataField) node;
			updateMetadata(metadataField);
		}
	}

	@Override
	public void onMachineEntityRemoved(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_METADATA_FIELD) {
			VortexClientMetadataField metadataField = (VortexClientMetadataField) node;
			removeMetadata(metadataField);
		}
	}

	private void addMetadata(VortexClientMetadataField metadataField) {
		String[] ids = { MFID, NOW, "ttypes", "vtype", "req", "key", "title", "maxlen", "enums", "desc", "maxval", "minval" };
		final VortexEyeMetadataField data = metadataField.getData();
		final Row row = addRow(data.getId(), data.getNow(), data.getTargetTypes(), data.getValueType(), data.getRequired(), data.getKeyCode(), data.getTitle(),
				data.getMaxLength(), data.getEnums(), data.getDescription(), data.getMaxValue(), data.getMinValue(), metadataField.getValidationDescription(), metadataField);
		rows.put(data.getId(), row);
	}
	private void updateMetadata(VortexClientMetadataField metadataField) {
		final VortexEyeMetadataField data = metadataField.getData();
		final Row row = rows.get(data.getId());
		row.put("ttypes", data.getTargetTypes());
		row.put("vtype", data.getValueType());
		row.put("req", data.getRequired());
		row.put("key", data.getKeyCode());
		row.put("title", data.getTitle());
		row.put("maxlen", data.getMaxLength());
		row.put("enums", data.getEnums());
		row.put("desc", data.getDescription());
		row.put("maxval", data.getMaxValue());
		row.put("minval", data.getMinValue());
		row.put("validations", metadataField.getValidationDescription());

	}
	private void removeMetadata(VortexClientMetadataField metadataField) {
		final VortexEyeMetadataField data = metadataField.getData();
		final Row row = rows.remove(data.getId());
		getTable().getTable().removeRow(row);
	}

	@Override
	public void onEyeDisconnected() {
		getTable().clear();
	}

	@Override
	public WebMenu createMenu(WebTable table) {
		BasicWebMenu menu = new BasicWebMenu();
		menu.addChild(new BasicWebMenuLink("Add Metadata Field", true, "add"));
		if (table.hasSelectedRows()) {
			menu.addChild(new BasicWebMenuLink("Copy Metadata Field", true, "copy"));
			menu.addChild(new BasicWebMenuLink("Edit Metadata Field", true, "edit"));
			menu.addChild(new BasicWebMenuLink("Delete Metadata Field", true, "delete"));
		}
		return menu;
	}

	public void onContextMenu(WebTable table, String action) {
		if ("add".equals(action)) {
			getManager().showDialog("Add Metadata Field", new VortexWebMetadataFieldFormPortlet(generateConfig()), 800, 600);
		} else if ("copy".equals(action)) {
			for (Row row : table.getSelectedRows()) {
				VortexWebMetadataFieldFormPortlet form = new VortexWebMetadataFieldFormPortlet(generateConfig());
				VortexClientMetadataField field = row.get("data", VortexClientMetadataField.class);
				form.setMetadataFieldToCopy(field.getData());
				getManager().showDialog("Add Metadata Field", form, 800, 600);
			}
		} else if ("edit".equals(action)) {
			for (Row row : table.getSelectedRows()) {
				VortexWebMetadataFieldFormPortlet form = new VortexWebMetadataFieldFormPortlet(generateConfig());
				VortexClientMetadataField field = row.get("data", VortexClientMetadataField.class);
				form.setMetadataFieldToEdit(field.getData());
				getManager().showDialog("Edit Metadata Field", form, 800, 600);
			}
		} else if ("delete".equals(action)) {
			List<VortexEyeRequest> requests = new ArrayList<VortexEyeRequest>();
			for (Row row : table.getSelectedRows()) {
				VortexClientMetadataField field = row.get("data", VortexClientMetadataField.class);
				VortexEyeManageMetadataFieldRequest req = nw(VortexEyeManageMetadataFieldRequest.class);
				VortexEyeMetadataField metadataField = nw(VortexEyeMetadataField.class);
				metadataField.setId(field.getId());
				metadataField.setRevision(VortexAgentEntity.REVISION_DONE);
				req.setMetadataField(metadataField);
				requests.add(req);
			}
			getManager().showDialog("Delete Metadata",
					new VortexWebCommentFormPortlet(generateConfig(), getPortletId(), requests, "Delete Metadata Field", "metadata.jpg").setIconToDelete());
		}
	};

	public static class Builder extends AbstractPortletBuilder<VortexWebMetadataFieldsTablePortlet> {
		public Builder() {
			super(VortexWebMetadataFieldsTablePortlet.class);
		}

		public static final String ID = "metadataTablePortlet";

		@Override
		public VortexWebMetadataFieldsTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebMetadataFieldsTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Metadata Fields";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onEyeSnapshotProcessed() {
		// TODO Auto-generated method stub

	}

}
