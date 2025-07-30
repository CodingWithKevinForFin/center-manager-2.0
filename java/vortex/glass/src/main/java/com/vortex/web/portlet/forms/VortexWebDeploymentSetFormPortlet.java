package com.vortex.web.portlet.forms;

import java.util.Map;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.impl.TextMatcherFactory;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexDeploymentSet;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageDeploymentSetRequest;
import com.vortex.client.VortexClientDeploymentSet;
import com.vortex.web.VortexWebEyeService;

public class VortexWebDeploymentSetFormPortlet extends VortexWebMetadataFormPortlet {
	final static private byte EDIT = 1;
	final static private byte COPY = 2;
	final static private byte NEW = 3;
	private VortexWebEyeService service;

	private FormPortletTextField nameField;
	private FormPortletTextAreaField propsField;
	private FormPortletButton submitButton;
	private Long deploymentSetId;

	private boolean isCopy = false;

	private byte mode = NEW;

	public VortexWebDeploymentSetFormPortlet(PortletConfig config) {
		super(config, "deployment.jpg");
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		addButton(this.submitButton = new FormPortletButton("Create Deployment Set"));
		addField(this.nameField = new FormPortletTextField("Deployment Set Name")).setWidth(200);
		addField(this.propsField = new FormPortletTextAreaField("Properties")).setValue("").setHeight(600);
		initMetadataFormFields(VortexAgentEntity.TYPE_DEPLOYMENT_SET);
	}
	@Override
	protected void onUserPressedButton(FormPortletButton button) {
		if (button == submitButton) {
			VortexEyeManageDeploymentSetRequest request = nw(VortexEyeManageDeploymentSetRequest.class);
			request.setDeploymentSet(toDeploymentSet());
			if (request.getDeploymentSet() == null)
				return;
			service.sendRequestToBackend(getPortletId(), request);
			close();
		}

		super.onUserPressedButton(button);
	}

	public VortexDeploymentSet toDeploymentSet() {
		VortexDeploymentSet deploymentSet = nw(VortexDeploymentSet.class);
		deploymentSet.setName(nameField.getValue());
		deploymentSet.setProperties(propsField.getValue());
		if (mode == EDIT)
			deploymentSet.setId(this.deploymentSetId);
		if (!populateMetadata(deploymentSet))
			return null;
		return deploymentSet;
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
	public void setDeploymentSetToEdit(VortexClientDeploymentSet bp) {
		VortexDeploymentSet data = bp.getData();
		this.nameField.setValue(data.getName());
		this.propsField.setValue(data.getProperties());
		this.deploymentSetId = data.getId();
		this.submitButton.setName("Update");
		populateMetadataFormFields(bp.getData());
		mode = EDIT;
	}
	public void setDeploymentSetToCopy(VortexClientDeploymentSet bp) {
		VortexDeploymentSet data = bp.getData();
		this.nameField.setValue(data.getName());
		this.deploymentSetId = data.getId();
		this.propsField.setValue(data.getProperties());
		mode = COPY;
		populateMetadataFormFields(bp.getData());
		removeButton(submitButton);
	}

}
