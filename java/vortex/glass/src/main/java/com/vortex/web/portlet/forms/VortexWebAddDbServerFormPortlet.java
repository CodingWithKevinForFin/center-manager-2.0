package com.vortex.web.portlet.forms;

import java.util.HashMap;
import java.util.Map;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.SH;
import com.f1.vortexcommon.msg.agent.VortexAgentDbServer;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageDbServerRequest;
import com.vortex.client.VortexClientDbServer;
import com.vortex.client.VortexClientMachine;
import com.vortex.web.VortexWebEyeService;

public class VortexWebAddDbServerFormPortlet extends VortexWebMetadataFormPortlet {
	private static final String DEFAULT_PORT_MYSQL = "3306";
	private static final String DEFAULT_PORT_ORACLE = "1701";
	private static final String DEFAULT_PORT_SQLSERVER = "1433";
	private static final String DEFAULT_PORT_SYBASE = "7100";

	private VortexWebEyeService service;

	final private FormPortletButton submitButton;
	final private FormPortletSelectField<String> targetMuidField;
	final private FormPortletTextField portField;
	final private FormPortletTextField databaseField;
	final private FormPortletTextField usernameField;
	final private FormPortletTextField passwordField;
	final private FormPortletSelectField<Byte> dbTypeField;
	final private FormPortletTextField urlField;
	final private FormPortletTextField descriptionField;

	private Long idToEdit;

	public VortexWebAddDbServerFormPortlet(PortletConfig config) {
		super(config, "db.jpg");

		setIconToAdd();
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);

		addField(this.descriptionField = new FormPortletTextField("Description")).setWidth(400);
		addField(this.targetMuidField = new FormPortletSelectField<String>(String.class, "Inspect From"));
		for (VortexClientMachine i : service.getAgentManager().getAgentMachinesSortedByHostname())
			this.targetMuidField.addOption(i.getData().getMachineUid(), i.getHostName());

		addField(this.dbTypeField = new FormPortletSelectField<Byte>(Byte.class, "Database Type"));
		this.dbTypeField.addOption(VortexAgentDbServer.TYPE_MYSQL, "Mysql");
		this.dbTypeField.addOption(VortexAgentDbServer.TYPE_ORACLE, "Oracle");
		this.dbTypeField.addOption(VortexAgentDbServer.TYPE_SQLSERVER, "Microsoft Sql Server");
		this.dbTypeField.addOption(VortexAgentDbServer.TYPE_SYBASE, "Sybase");

		addField(this.portField = new FormPortletTextField("Server Port")).setWidth(50);
		addField(this.databaseField = new FormPortletTextField("Database Name")).setWidth(200);
		addField(this.usernameField = new FormPortletTextField("User Name")).setWidth(200);
		addField(this.passwordField = new FormPortletTextField("User Password")).setWidth(200);
		addField(this.urlField = new FormPortletTextField("database URL")).setWidth(FormPortletTextField.WIDTH_STRETCH);

		initMetadataFormFields(VortexAgentEntity.TYPE_DB_SERVER);

		addButton(this.submitButton = new FormPortletButton("Create Deployment"));
		onDbTypeChanged();
	}
	private void onDbTypeChanged() {
		switch (dbTypeField.getValue()) {
			case VortexAgentDbServer.TYPE_MYSQL:
				portField.setValue(DEFAULT_PORT_MYSQL);
				urlField.setValue("com.mysql.jdbc.Driver:{}:jdbc:mysql://${host}:${port}/${database}?user=${user}&password=****");
				break;
			case VortexAgentDbServer.TYPE_ORACLE:
				portField.setValue(DEFAULT_PORT_ORACLE);
				urlField.setValue("com.mysql.jdbc.Driver:{}:jdbc:todo");
				break;
			case VortexAgentDbServer.TYPE_SQLSERVER:
				portField.setValue(DEFAULT_PORT_SQLSERVER);
				urlField.setValue("com.microsoft.sqlserver.jdbc.SQLServerDriver{}:jdbc:sqlserver://host:port;databaseName=database;user=UserName;password=****");
				break;
			case VortexAgentDbServer.TYPE_SYBASE:
				portField.setValue(DEFAULT_PORT_SYBASE);
				urlField.setValue("com.mysql.jdbc.Driver:{}:jdbc:todo");
				break;
		}
	}

	@Override
	protected void onUserPressedButton(FormPortletButton button) {
		if (button == submitButton) {
			VortexEyeManageDbServerRequest request = nw(VortexEyeManageDbServerRequest.class);
			VortexAgentDbServer dbserver = nw(VortexAgentDbServer.class);
			dbserver.setPassword(this.passwordField.getValue());
			dbserver.setUrl(this.urlField.getValue());
			dbserver.setServerPort(Integer.parseInt(this.portField.getValue()));
			dbserver.setDbType((this.dbTypeField.getValue()));
			dbserver.setMachineUid(this.targetMuidField.getValue());
			dbserver.setDescription(this.descriptionField.getValue());
			dbserver.setHints(getHints());

			if (idToEdit != null)
				dbserver.setId(idToEdit);
			request.setDbServer(dbserver);
			if (!populateMetadata(dbserver))
				return;
			service.sendRequestToBackend(getPortletId(), request);
			close();
		}

		super.onUserPressedButton(button);
	}

	@Override
	protected void onUserChangedValue(FormPortletField<?> field, Map<String, String> attributes) {
		super.onUserChangedValue(field, attributes);
		if (field == dbTypeField || field == this.targetMuidField) {
			onDbTypeChanged();
		}
	}
	//@Override
	//public int getSuggestedHeight(PortletMetrics pm) {
	//return 450;
	//}

	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return 600;
	}

	public void setDbServerToEdit(VortexClientDbServer bp) {
		VortexAgentDbServer data = bp.getData();
		this.targetMuidField.setValue(data.getMachineUid());
		this.descriptionField.setValue(data.getDescription());
		this.urlField.setValue(data.getUrl());
		this.passwordField.setValue(data.getPassword());
		this.portField.setValue(SH.toString(data.getServerPort()));
		this.dbTypeField.setValue((data.getDbType()));
		this.submitButton.setName("Update Database Server");
		processVariables(data.getHints());
		this.idToEdit = bp.getId();
		setIconToEdit();
		populateMetadataFormFields(bp.getData());
	}
	public void setDbServerToCopy(VortexClientDbServer bp) {
		VortexAgentDbServer data = bp.getData();
		this.targetMuidField.setValue(data.getMachineUid());
		this.descriptionField.setValue(data.getDescription());
		this.urlField.setValue(data.getUrl());
		this.passwordField.setValue(data.getPassword());
		this.portField.setValue(SH.toString(data.getServerPort()));
		this.dbTypeField.setValue((data.getDbType()));
		processVariables(data.getHints());
		this.submitButton.setName("Create Database Server");
		setIconToCopy();
		populateMetadataFormFields(bp.getData());
	}
	private void processVariables(String variables) {
		if (SH.is(variables)) {
			Map<String, String> map = SH.splitToMap(",", "=", variables);
			this.usernameField.setValue(SH.noNull(map.get("username")));
			this.databaseField.setValue(SH.noNull(map.get("databaseName")));
		}
	}
	private String getHints() {
		final Map<String, String> map = new HashMap<String, String>();
		map.put("username", this.usernameField.getValue());
		map.put("databaseName", this.databaseField.getValue());
		return SH.joinMap(",", "=", map);
	}
}
