package com.vortex.web.portlet.forms;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.SH;
import com.f1.utils.impl.TextMatcherFactory;
import com.f1.vortexcommon.msg.agent.VortexAgentCron;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentFileSystem;
import com.f1.vortexcommon.msg.agent.VortexAgentMachine;
import com.f1.vortexcommon.msg.agent.VortexAgentNetConnection;
import com.f1.vortexcommon.msg.agent.VortexAgentProcess;
import com.f1.vortexcommon.msg.eye.VortexExpectation;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageExpectationRequest;
import com.vortex.client.VortexClientCron;
import com.vortex.client.VortexClientEntity;
import com.vortex.client.VortexClientFileSystem;
import com.vortex.client.VortexClientMachine;
import com.vortex.client.VortexClientNetConnection;
import com.vortex.client.VortexClientProcess;
import com.vortex.web.VortexWebEyeService;

public class VortexWebExpectationFormPortlet extends VortexWebMetadataFormPortlet {
	private VortexWebEyeService service;

	private FormPortletTextField nameField;
	private FormPortletSelectField<Byte> typeField;
	private FormPortletSelectField<String> muidField;
	private FormPortletTextField fileSystemTypeField;
	private FormPortletTextField fileSystemNameField;
	private FormPortletTextField processCommandField;
	private FormPortletTextField processUserField;
	private FormPortletTextField ncLocalPortField;
	private FormPortletTextField ncForeignPortField;
	private FormPortletTextField ncLocalIpField;
	private FormPortletTextField ncForeignIpField;
	private FormPortletSelectField<Byte> ncStateField;
	private FormPortletButton submitButton;
	private VortexWebExpectationFormPortlet next;
	private FormPortletTextField cronCommandField;
	private FormPortletTextField cronScheduleField;

	public VortexWebExpectationFormPortlet(PortletConfig config) {
		super(config, "expectation.jpg");
		setIconToAdd();
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		addButton(this.submitButton = new FormPortletButton("Create Expectation"));
		addField(this.nameField = new FormPortletTextField("Expectation Name")).setWidth(FormPortletTextField.WIDTH_STRETCH);
		addField(this.muidField = new FormPortletSelectField<String>(String.class, "Machine"));
		addField(this.typeField = new FormPortletSelectField<Byte>(Byte.class, "Target Type"));
		addField(this.fileSystemTypeField = new FormPortletTextField("File System Type").setValue("*"));
		addField(this.fileSystemNameField = new FormPortletTextField("File System Name").setValue("*"));
		addField(this.processCommandField = new FormPortletTextField("Process Command").setValue("*"));
		addField(this.processUserField = new FormPortletTextField("Process User").setValue("*"));
		addField(this.ncLocalPortField = new FormPortletTextField("Local Port").setValue("*"));
		addField(this.ncForeignPortField = new FormPortletTextField("Foreign Port").setValue("*"));
		addField(this.ncLocalIpField = new FormPortletTextField("Local IP").setValue("*"));
		addField(this.ncForeignIpField = new FormPortletTextField("Foreign IP").setValue("*"));
		addField(this.ncStateField = new FormPortletSelectField<Byte>(Byte.class, "Connection State"));
		addField(this.cronCommandField = new FormPortletTextField("Crontab Command").setValue("*"));
		addField(this.cronScheduleField = new FormPortletTextField("Crontab Schedule").setValue("*"));

		for (Entry<Byte, String> i : service.getConnectionStateFormatter().getEnumValuesAsText().entrySet())
			this.ncStateField.addOption(i.getKey(), i.getValue());

		for (Entry<Byte, String> i : service.getAgentTypeFormatter().getEnumValuesAsText().entrySet())
			this.typeField.addOption(i.getKey(), i.getValue());
		for (VortexClientMachine i : service.getAgentManager().getAgentMachinesSortedByHostname())
			this.muidField.addOption(i.getData().getMachineUid(), i.getHostName());
		initMetadataFormFields(VortexAgentEntity.TYPE_EXPECTATION);
		updateExpectationType();
	}
	@Override
	protected void onUserPressedButton(FormPortletButton button) {
		if (button == submitButton) {
			VortexEyeManageExpectationRequest request = nw(VortexEyeManageExpectationRequest.class);
			VortexExpectation expectation = nw(VortexExpectation.class);
			request.setExpectation(expectation);
			expectation.setName(nameField.getValue());
			expectation.setMachineUid(muidField.getValue());
			expectation.setTargetType((typeField.getValue()));
			HashMap<Byte, String> masks = new HashMap<Byte, String>();
			expectation.setFieldMasks(masks);
			switch ((typeField.getValue())) {
				case VortexAgentEntity.TYPE_FILE_SYSTEM:
					populateMap(masks, VortexExpectation.MASK_TYPE_TYPE, fileSystemTypeField);
					populateMap(masks, VortexExpectation.MASK_TYPE_NAME, fileSystemNameField);
					break;
				case VortexAgentEntity.TYPE_PROCESS:
					populateMap(masks, VortexExpectation.MASK_TYPE_COMMAND, processCommandField);
					populateMap(masks, VortexExpectation.MASK_TYPE_USER, processUserField);
					break;
				case VortexAgentEntity.TYPE_NET_CONNECTION:
					populateMap(masks, VortexExpectation.MASK_TYPE_STATE, ncStateField);
					populateMap(masks, VortexExpectation.MASK_TYPE_FOREIGN_HOST, ncForeignIpField);
					populateMap(masks, VortexExpectation.MASK_TYPE_FOREIGN_PORT, ncForeignPortField);
					populateMap(masks, VortexExpectation.MASK_TYPE_LOCAL_HOST, ncLocalIpField);
					populateMap(masks, VortexExpectation.MASK_TYPE_LOCAL_PORT, ncLocalPortField);
					break;
				case VortexAgentEntity.TYPE_CRON:
					populateMap(masks, VortexExpectation.MASK_TYPE_COMMAND, cronCommandField);
					populateMap(masks, VortexExpectation.MASK_TYPE_SCHEDULE, cronScheduleField);
					break;
				default:
					break;
			}
			if (!populateMetadata(expectation))
				return;
			service.sendRequestToBackend(getPortletId(), request);
			close();
			if (next != null) {
				getManager().showDialog("Add Expectation", next);
				next = null;
			}
		}

		super.onUserPressedButton(button);
	}

	private void populateMap(Map<Byte, String> sink, byte key, FormPortletField<?> field) {
		Object value = field.getValue();
		if (SH.isnt(value) || "*".equals(value))
			return;
		sink.put(key, SH.toString(value));
	}

	@Override
	protected void onUserChangedValue(FormPortletField<?> field, Map<String, String> attributes) {
		super.onUserChangedValue(field, attributes);
		if (field == typeField) {
			updateExpectationType();
		}
	}

	private void updateExpectationType() {
		removeFieldNoThrow(fileSystemTypeField);
		removeFieldNoThrow(fileSystemNameField);
		removeFieldNoThrow(processCommandField);
		removeFieldNoThrow(processUserField);
		removeFieldNoThrow(ncForeignIpField);
		removeFieldNoThrow(ncForeignPortField);
		removeFieldNoThrow(ncLocalIpField);
		removeFieldNoThrow(ncLocalPortField);
		removeFieldNoThrow(ncStateField);
		removeFieldNoThrow(cronCommandField);
		removeFieldNoThrow(cronScheduleField);
		switch ((typeField.getValue())) {
			case VortexAgentEntity.TYPE_FILE_SYSTEM:
				addField(fileSystemTypeField);
				addField(fileSystemNameField);
				break;
			case VortexAgentEntity.TYPE_PROCESS:
				addField(processCommandField);
				addField(processUserField);
			case VortexAgentEntity.TYPE_MACHINE:
				break;
			case VortexAgentEntity.TYPE_NET_CONNECTION:
				addField(ncStateField);
				addField(ncLocalIpField);
				addField(ncLocalPortField);
				addField(ncForeignIpField);
				addField(ncForeignPortField);
				break;
			case VortexAgentEntity.TYPE_CRON:
				addField(cronCommandField);
				addField(cronScheduleField);
				break;
			default:
				break;
		}

	}

	public static class Builder extends AbstractPortletBuilder<VortexWebExpectationFormPortlet> {

		public static final String ID = "ExpectationFormPortlet";

		public Builder() {
			super(VortexWebExpectationFormPortlet.class);
			setUserCreatable(false);
		}

		@Override
		public VortexWebExpectationFormPortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebExpectationFormPortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Expectation Form Portlet";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	//@Override
	//public int getSuggestedHeight(PortletMetrics pm) {
	//return 250;
	//}

	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return 450;
	}

	public void setTemplate(VortexClientEntity<?> template) {
		this.muidField.setValue(template.getMachine().getMachineUid());
		if (template instanceof VortexClientFileSystem) {
			VortexClientFileSystem node = (VortexClientFileSystem) template;
			VortexAgentFileSystem data = node.getData();
			typeField.setValue((VortexAgentEntity.TYPE_FILE_SYSTEM));
			nameField.setValue(data.getName());
			fileSystemNameField.setValue(wrap(data.getName()));
			fileSystemTypeField.setValue(wrap(data.getType()));
		} else if (template instanceof VortexClientProcess) {
			VortexClientProcess node = (VortexClientProcess) template;
			VortexAgentProcess data = node.getData();
			typeField.setValue((VortexAgentEntity.TYPE_PROCESS));
			nameField.setValue(node.getName());
			processCommandField.setValue(wrap(data.getCommand()));
			processUserField.setValue(wrap(data.getUser()));
		} else if (template instanceof VortexClientMachine) {
			VortexClientMachine node = (VortexClientMachine) template;
			VortexAgentMachine data = node.getData();
			typeField.setValue((VortexAgentEntity.TYPE_MACHINE));
			nameField.setValue(data.getHostName());
		} else if (template instanceof VortexClientCron) {
			VortexClientCron node = (VortexClientCron) template;
			VortexAgentCron data = node.getData();
			typeField.setValue((VortexAgentEntity.TYPE_CRON));
			nameField.setValue(data.getUser() + ": " + data.getCommand());
			cronCommandField.setValue(wrap(data.getCommand()));
			cronScheduleField.setValue(wrap(node.getScheduleText()));
		} else if (template instanceof VortexClientNetConnection) {
			VortexClientNetConnection node = (VortexClientNetConnection) template;
			VortexAgentNetConnection data = node.getData();
			typeField.setValue((VortexAgentEntity.TYPE_NET_CONNECTION));
			final byte connectionState = data.getState();
			ncStateField.setValue((connectionState));
			ncLocalIpField.setValue(wrap(SH.toString(data.getLocalHost())));
			switch (connectionState) {
				case VortexAgentNetConnection.STATE_LISTEN:
					ncLocalPortField.setValue(wrap(SH.toString(data.getLocalPort())));
					nameField.setValue("Server socket " + data.getLocalPort());
					break;
				case VortexAgentNetConnection.STATE_ESTABLISHED:
					ncForeignIpField.setValue(wrap(SH.toString(data.getForeignHost())));
					nameField.setValue("Connection to " + data.getForeignHost());
				case VortexAgentNetConnection.STATE_CLOSE_WAIT:
				case VortexAgentNetConnection.STATE_TIME_WAIT:
					break;
			}
		}
		updateExpectationType();
	}
	private static String wrap(String text) {
		return TextMatcherFactory.DEFAULT.stringToExpression(text);
	}

	public void setNextForm(VortexWebExpectationFormPortlet next) {
		if (this.next != null)
			throw new IllegalStateException();
		this.next = next;
	}

}
