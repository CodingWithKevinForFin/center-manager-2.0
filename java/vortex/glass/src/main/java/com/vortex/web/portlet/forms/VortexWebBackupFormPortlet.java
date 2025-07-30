package com.vortex.web.portlet.forms;

import java.util.Map;

import com.f1.base.Action;
import com.f1.container.ResultMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.AH;
import com.f1.utils.SH;
import com.f1.utils.impl.TextMatcherFactory;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexEyeBackup;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageBackupRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageBackupResponse;
import com.vortex.client.VortexClientBackup;
import com.vortex.client.VortexClientBackupDestination;
import com.vortex.client.VortexClientDeployment;
import com.vortex.client.VortexClientMachine;
import com.vortex.web.VortexWebEyeService;

public class VortexWebBackupFormPortlet extends VortexWebMetadataFormPortlet {
	final private VortexWebEyeService service;

	final private FormPortletSelectField<String> muidField;
	final private FormPortletSelectField<Long> destinationField;
	final private FormPortletSelectField<Long> deploymentField;
	final private FormPortletTextField pathField;
	final private FormPortletTextAreaField ignoreFilesField;
	final private FormPortletButton submitButton;
	final private FormPortletTextField descriptionField;
	private Long backupId;

	public VortexWebBackupFormPortlet(PortletConfig config) {
		super(config, "backupdest.jpg");
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		addButton(this.submitButton = new FormPortletButton("Create Backup"));
		addField(this.descriptionField = new FormPortletTextField("Description"));
		addField(this.deploymentField = new FormPortletSelectField<Long>(Long.class, "Associated Deployment"));
		addField(this.muidField = new FormPortletSelectField<String>(String.class, "Source Machine"));
		addField(this.pathField = new FormPortletTextField("Source Path")).setWidth(FormPortletTextField.WIDTH_STRETCH);
		addField(this.destinationField = new FormPortletSelectField<Long>(Long.class, "Destination"));
		addField(this.ignoreFilesField = new FormPortletTextAreaField("Ignore Files"));
		for (VortexClientMachine i : service.getAgentManager().getAgentMachinesSortedByHostname())
			this.muidField.addOption(i.getData().getMachineUid(), i.getHostName());
		for (VortexClientBackupDestination dest : service.getAgentManager().getBackupDestinations()) {
			VortexClientMachine machine = service.getAgentManager().getAgentMachineByUid(dest.getData().getDestinationMachineUid());
			if (machine != null)
				this.destinationField.addOption(dest.getId(), dest.getData().getName() + " [" + machine.getHostName() + ":" + dest.getData().getDestinationPath() + "]");
		}
		this.deploymentField.addOption(VortexEyeBackup.NO_DEPLOYMENT, "<No Deployment>");
		for (VortexClientDeployment dep : service.getAgentManager().getDeployments()) {
			final VortexClientMachine machine = service.getAgentManager().getAgentMachineByUid(dep.getData().getTargetMachineUid());
			final String name = dep.getBuildProcedure().getData().getName();
			this.deploymentField.addOption(dep.getId(), name + " [" + (machine == null ? "" : machine.getHostName()) + ":" + dep.getData().getTargetDirectory() + "]");
		}
		initMetadataFormFields(VortexAgentEntity.TYPE_BACKUP);
		updatePathFieldTitle();
	}
	@Override
	protected void onUserPressedButton(FormPortletButton button) {
		if (button == submitButton) {
			VortexEyeManageBackupRequest request = nw(VortexEyeManageBackupRequest.class);
			request.setBackup(toBackup());
			if (request.getBackup() == null)
				return;
			service.sendRequestToBackend(getPortletId(), request);
			//close();
		}

		super.onUserPressedButton(button);
	}
	public VortexEyeBackup toBackup() {
		VortexEyeBackup backup = nw(VortexEyeBackup.class);
		backup.setSourcePath(pathField.getValue());
		backup.setDescription(descriptionField.getValue());
		backup.setDeploymentId(deploymentField.getValue());
		backup.setOptions(0);
		if (deploymentField.getValue() == VortexEyeBackup.NO_DEPLOYMENT) {
			backup.setSourceMachineUid(muidField.getValue());
		} else {
			backup.setSourceMachineUid(null);
		}
		backup.setBackupDestinationId(destinationField.getValue());
		String[] ifs = SH.trimArray(SH.splitLines(ignoreFilesField.getValue()));
		if (AH.isEmpty(ifs))
			backup.setIgnoreExpression("");
		else
			backup.setIgnoreExpression("(" + SH.join(")|(", ifs) + ")");
		if (this.backupId != null) {
			backup.setId(this.backupId);
		}
		if (!populateMetadata(backup))
			return null;
		return backup;
	}

	@Override
	protected void onUserChangedValue(FormPortletField<?> field, Map<String, String> attributes) {
		super.onUserChangedValue(field, attributes);
		if (field == deploymentField) {
			updatePathFieldTitle();
		}
	}

	private void updatePathFieldTitle() {
		Long val = deploymentField.getValue();
		if (hasField(this.muidField) && (val == null || val == VortexEyeBackup.NO_DEPLOYMENT)) {
			addFieldNoThrow(this.muidField);
			this.pathField.setTitle("Absolute Source Path");
		} else {
			removeFieldNoThrow(this.muidField);
			this.pathField.setTitle("Source Path (under " + service.getAgentManager().getDeployment(val).getData().getTargetDirectory() + ")");
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

	private static String wrap(String text) {
		return TextMatcherFactory.DEFAULT.stringToExpression(text);
	}
	public void setBackupToEdit(VortexClientBackup bp) {
		setBackupToCopy(bp);
		VortexEyeBackup data = bp.getData();
		this.backupId = data.getId();
		this.submitButton.setName("Update");
		setIconToEdit();
	}
	public void setBackupToCopy(VortexClientBackup bp) {
		VortexEyeBackup data = bp.getData();
		this.descriptionField.setValue(data.getDescription());
		this.destinationField.setValue(data.getBackupDestinationId());
		String ie = data.getIgnoreExpression();
		if (ie != null) {
			if (ie.startsWith("(") || ie.endsWith(")")) {
				ie = SH.strip(ie, "(", ")", true);
				this.ignoreFilesField.setValue(SH.replaceAll(SH.noNull(ie), ")|(", "\n"));
			} else
				this.ignoreFilesField.setValue(ie);
		}
		if (data.getDeploymentId() != VortexEyeBackup.NO_DEPLOYMENT) {
			this.deploymentField.setValueNoThrow(data.getDeploymentId());
			VortexClientDeployment dep = service.getAgentManager().getDeployment(data.getDeploymentId());
			if (dep != null)
				this.muidField.setValueNoThrow(dep.getData().getTargetMachineUid());
		} else {
			this.muidField.setValueNoThrow(data.getSourceMachineUid());
		}
		this.pathField.setValue(data.getSourcePath());
		this.descriptionField.setValue(data.getDescription());
		populateMetadataFormFields(bp.getData());
		setIconToCopy();
		updatePathFieldTitle();
	}
	public void disableDeploymentField() {
		removeField(deploymentField);
	}

	public void setDeploymentId(long deploymentId) {
		this.deploymentField.setValue(deploymentId);
	}
	public void setMachineUid(String muid) {
		this.muidField.setValueNoThrow(muid);
	}
	public void setDirectory(String path) {
		this.pathField.setValue(path);
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		VortexEyeManageBackupResponse response = (VortexEyeManageBackupResponse) result.getAction();
		if (response.getOk())
			close();
		else {
			getManager().showAlert(response.getMessage());
		}
	}
}
