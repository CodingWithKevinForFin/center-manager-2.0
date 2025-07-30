package com.vortex.web.portlet.forms;

import java.util.Map;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.impl.TextMatcherFactory;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexEyeBackupDestination;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageBackupDestinationRequest;
import com.vortex.client.VortexClientBackupDestination;
import com.vortex.client.VortexClientMachine;
import com.vortex.web.VortexWebEyeService;

public class VortexWebBackupDestinationFormPortlet extends VortexWebMetadataFormPortlet {
	private VortexWebEyeService service;

	private FormPortletTextField nameField;
	private FormPortletSelectField<String> muidField;
	private FormPortletTextField pathField;
	private FormPortletButton submitButton;
	private Long buildId;

	public VortexWebBackupDestinationFormPortlet(PortletConfig config) {
		super(config, "backupdest.jpg");
		setIconToAdd();
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		addButton(this.submitButton = new FormPortletButton("Create Backup Destination"));
		addField(this.nameField = new FormPortletTextField("Destination Name")).setWidth(200);
		addField(this.muidField = new FormPortletSelectField<String>(String.class, "Machine"));
		addField(this.pathField = new FormPortletTextField("Path")).setWidth(FormPortletTextField.WIDTH_STRETCH).setValue("/var/backup");
		for (VortexClientMachine i : service.getAgentManager().getAgentMachinesSortedByHostname())
			this.muidField.addOption(i.getData().getMachineUid(), i.getHostName());
		initMetadataFormFields(VortexAgentEntity.TYPE_BACKUP_DESTINATION);
	}
	@Override
	protected void onUserPressedButton(FormPortletButton button) {
		if (button == submitButton) {
			VortexEyeManageBackupDestinationRequest request = nw(VortexEyeManageBackupDestinationRequest.class);
			VortexEyeBackupDestination backupDestination = nw(VortexEyeBackupDestination.class);
			request.setBackupDestination(backupDestination);
			backupDestination.setName(nameField.getValue());
			backupDestination.setDestinationMachineUid(muidField.getValue());
			backupDestination.setDestinationPath(pathField.getValue());
			if (this.buildId != null) {
				backupDestination.setId(this.buildId);
			}
			if (!populateMetadata(backupDestination))
				return;
			request.setComment(getUserComment());
			service.sendRequestToBackend(getPortletId(), request);
			close();
		}

		super.onUserPressedButton(button);
	}

	@Override
	protected void onUserChangedValue(FormPortletField<?> field, Map<String, String> attributes) {
		super.onUserChangedValue(field, attributes);
	}

	//@Override
	//public int getSuggestedHeight(PortletMetrics pm) {
	//return 450;
	//}

	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return 600;
	}

	private static String wrap(String text) {
		return TextMatcherFactory.DEFAULT.stringToExpression(text);
	}
	public void setBackupDestinationToEdit(VortexClientBackupDestination bp) {
		VortexEyeBackupDestination data = bp.getData();
		this.nameField.setValue(data.getName());
		this.muidField.setValue(data.getDestinationMachineUid());
		this.pathField.setValue(data.getDestinationPath());
		this.buildId = data.getId();
		this.submitButton.setName("Update");
		setIconToEdit();
		populateMetadataFormFields(bp.getData());

	}
	public void setBackupDestinationToCopy(VortexClientBackupDestination bp) {
		VortexEyeBackupDestination data = bp.getData();
		this.nameField.setValue("Copy of " + data.getName());
		this.muidField.setValue(data.getDestinationMachineUid());
		this.pathField.setValue(data.getDestinationPath());
		setIconToCopy();
		populateMetadataFormFields(bp.getData());
	}

}
