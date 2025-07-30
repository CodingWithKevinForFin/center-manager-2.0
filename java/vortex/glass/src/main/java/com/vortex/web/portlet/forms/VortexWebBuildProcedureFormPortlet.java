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
import com.f1.vortexcommon.msg.eye.VortexBuildProcedure;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageBuildProcedureRequest;
import com.vortex.client.VortexClientBuildProcedure;
import com.vortex.client.VortexClientMachine;
import com.vortex.web.VortexWebEyeService;

public class VortexWebBuildProcedureFormPortlet extends VortexWebMetadataFormPortlet {
	private VortexWebEyeService service;

	private FormPortletTextField nameField;
	private FormPortletSelectField<String> muidField;
	private FormPortletTextField buildProcessField;
	private FormPortletTextField buildStdinField;
	private FormPortletTextField buildOutputField;
	private FormPortletTextField buildOutputVerifyField;
	private FormPortletTextField buildUserField;
	private FormPortletTextField buildResultNameField;
	private FormPortletTextField buildResultVersionField;
	private FormPortletButton submitButton;
	private Long buildId;

	public VortexWebBuildProcedureFormPortlet(PortletConfig config) {
		super(config, "build.jpg");
		setIconToAdd();
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		addButton(this.submitButton = new FormPortletButton("Create Build Procedure"));
		addField(this.nameField = new FormPortletTextField("Build Name")).setWidth(200);
		addField(this.muidField = new FormPortletSelectField<String>(String.class, "Machine"));
		addField(this.buildUserField = new FormPortletTextField("Build user Template")).setWidth(200).setValue("root");
		addField(this.buildProcessField = new FormPortletTextField("Command Template")).setWidth(FormPortletTextField.WIDTH_STRETCH);
		addField(this.buildStdinField = new FormPortletTextField("Stdin Template")).setWidth(FormPortletTextField.WIDTH_STRETCH);
		addField(this.buildOutputField = new FormPortletTextField("Output File Template")).setWidth(FormPortletTextField.WIDTH_STRETCH);
		addField(this.buildOutputVerifyField = new FormPortletTextField("Output Verify File Template")).setWidth(FormPortletTextField.WIDTH_STRETCH);
		addField(this.buildResultNameField = new FormPortletTextField("Result Name Template")).setWidth(400);
		addField(this.buildResultVersionField = new FormPortletTextField("Result Version Template")).setWidth(400);

		formPortlet.setLabelsWidth(160);
		for (VortexClientMachine i : service.getAgentManager().getAgentMachinesSortedByHostname())
			this.muidField.addOption(i.getData().getMachineUid(), i.getHostName());
		initMetadataFormFields(VortexAgentEntity.TYPE_BUILD_PROCEDURE);
	}
	@Override
	protected void onUserPressedButton(FormPortletButton button) {
		if (button == submitButton) {
			VortexEyeManageBuildProcedureRequest request = nw(VortexEyeManageBuildProcedureRequest.class);
			VortexBuildProcedure buildProcedure = nw(VortexBuildProcedure.class);
			request.setBuildProcedure(buildProcedure);
			buildProcedure.setName(nameField.getValue());
			buildProcedure.setBuildMachineUid(muidField.getValue());
			buildProcedure.setTemplateCommand(buildProcessField.getValue());
			buildProcedure.setTemplateStdin(buildStdinField.getValue());
			buildProcedure.setTemplateResultFile(buildOutputField.getValue());
			buildProcedure.setTemplateResultVerifyFile(buildOutputVerifyField.getValue());
			buildProcedure.setTemplateUser(buildUserField.getValue());
			buildProcedure.setTemplateResultName(buildResultNameField.getValue());
			buildProcedure.setTemplateResultVersion(buildResultVersionField.getValue());
			if (this.buildId != null) {
				buildProcedure.setId(this.buildId);
			}
			if (!populateMetadata(buildProcedure))
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
	public void setBuildProcedureToEdit(VortexClientBuildProcedure bp) {
		VortexBuildProcedure data = bp.getData();
		setBuildProcedureToCopy(bp);
		this.nameField.setValue(data.getName());
		this.buildId = data.getId();
		this.submitButton.setName("Update");
		setIconToEdit();
	}
	public void setBuildProcedureToCopy(VortexClientBuildProcedure bp) {
		VortexBuildProcedure data = bp.getData();
		this.nameField.setValue("Copy of " + data.getName());
		this.muidField.setValue(data.getBuildMachineUid());
		this.buildUserField.setValue(data.getTemplateUser());
		this.buildProcessField.setValue(data.getTemplateCommand());
		this.buildStdinField.setValue(data.getTemplateStdin());
		this.buildOutputField.setValue(data.getTemplateResultFile());
		this.buildOutputVerifyField.setValue(data.getTemplateResultVerifyFile());
		this.buildResultNameField.setValue(data.getTemplateResultName());
		this.buildResultVersionField.setValue(data.getTemplateResultVersion());
		populateMetadataFormFields(bp.getData());
		setIconToCopy();

	}

}
