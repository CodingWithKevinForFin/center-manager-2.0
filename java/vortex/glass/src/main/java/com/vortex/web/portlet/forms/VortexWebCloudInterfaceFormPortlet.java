package com.vortex.web.portlet.forms;

import java.util.HashMap;
import java.util.Map;

import com.f1.base.Action;
import com.f1.container.ResultMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.vortexcommon.msg.eye.VortexEyeCloudInterface;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageCloudInterfaceRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageCloudInterfaceResponse;
import com.vortex.client.VortexClientCloudInterface;
import com.vortex.client.VortexClientUtils;
import com.vortex.web.VortexWebEyeService;

public class VortexWebCloudInterfaceFormPortlet extends VortexWebMetadataFormPortlet {
	final private VortexWebEyeService service;

	final private FormPortletSelectField<Short> vendorTypeField;
	final private FormPortletTextField descriptionField;
	final private FormPortletTextField usernameField;
	final private FormPortletTextField passwordField;
	final private FormPortletTextAreaField keyDataField;
	final private FormPortletButton submitButton;

	private long ciid = 0;

	private FormPortletButton testButton;

	final private FormPortletTextField amazonAccessIdField;
	final private FormPortletTextField amazonAccessKeyField;
	final private FormPortletTextField amazonEndPointField;

	private FormPortletTextField rackspaceUsernameField;

	private FormPortletTextField rackspaceApiKeyField;

	public VortexWebCloudInterfaceFormPortlet(PortletConfig config) {
		super(config, "cloud.jpg");
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		addField(this.descriptionField = new FormPortletTextField("Description"));
		addField(this.vendorTypeField = new FormPortletSelectField<Short>(Short.class, "Vendor Type"));
		this.vendorTypeField.addOption(VortexEyeCloudInterface.VENDOR_AMAZON_AWS, "amazon AWS (TM)");
		this.vendorTypeField.addOption(VortexEyeCloudInterface.VENDOR_RACKSPACE, "Rackspace (TM)");
		addField(this.usernameField = new FormPortletTextField("Default UserName")).setWidth(300);
		addField(this.passwordField = new FormPortletTextField("Default Password")).setWidth(300);
		addField(this.keyDataField = new FormPortletTextAreaField("Default Key Data")).setHeight(200);

		this.amazonAccessIdField = new FormPortletTextField("Amazon Access Id").setWidth(300);
		this.amazonAccessKeyField = new FormPortletTextField("Amazon Access Key").setWidth(300);
		this.amazonEndPointField = new FormPortletTextField("Amazon End Point").setWidth(300);

		this.rackspaceUsernameField = new FormPortletTextField("Rackspace Username").setWidth(300);
		this.rackspaceApiKeyField = new FormPortletTextField("Rackspace Api Key").setWidth(300);

		addButton(this.testButton = new FormPortletButton("Test connection to cloud"));
		addButton(this.submitButton = new FormPortletButton("Create Cloud Interface (after test)"));
		setIconToAdd();
		updateVendorFields();
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.vendorTypeField) {
			updateVendorFields();
		}
	}
	private void updateVendorFields() {

		removeFieldNoThrow(this.amazonAccessIdField);
		removeFieldNoThrow(this.amazonAccessKeyField);
		removeFieldNoThrow(this.amazonEndPointField);
		removeFieldNoThrow(this.rackspaceApiKeyField);
		removeFieldNoThrow(this.rackspaceUsernameField);
		switch (this.vendorTypeField.getValue()) {
			case VortexEyeCloudInterface.VENDOR_AMAZON_AWS:
				addField(this.amazonAccessIdField);
				addField(this.amazonAccessKeyField);
				addField(this.amazonEndPointField);
				break;
			case VortexEyeCloudInterface.VENDOR_RACKSPACE:
				addField(this.rackspaceUsernameField);
				addField(this.rackspaceApiKeyField);
				break;
		}

	}
	@Override
	protected void onUserPressedButton(FormPortletButton button) {
		if (button == submitButton) {
			VortexEyeManageCloudInterfaceRequest request = nw(VortexEyeManageCloudInterfaceRequest.class);
			request.setCloudInterface(toCloudInterface());
			if (request.getCloudInterface() == null)
				return;
			service.sendRequestToBackend(getPortletId(), request);
		} else if (button == testButton) {
			VortexEyeManageCloudInterfaceRequest request = nw(VortexEyeManageCloudInterfaceRequest.class);
			request.setCloudInterface(toCloudInterface());
			request.setOnlyTest(true);
			service.sendRequestToBackend(getPortletId(), request);
		} else
			super.onUserPressedButton(button);
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		VortexEyeManageCloudInterfaceResponse response = (VortexEyeManageCloudInterfaceResponse) result.getAction();
		if (response.getOk())
			close();
		else {
			getManager().showAlert(response.getMessage());
		}
	}
	public VortexEyeCloudInterface toCloudInterface() {
		VortexEyeCloudInterface ci = nw(VortexEyeCloudInterface.class);
		ci.setCloudVendorType(this.vendorTypeField.getValue());
		ci.setDescription(descriptionField.getValue());
		//ci.setKeyType(keyTypeField.getValue());
		HashMap<String, String> params = new HashMap<String, String>();
		switch (this.vendorTypeField.getValue()) {
			case VortexEyeCloudInterface.VENDOR_AMAZON_AWS:
				params.put("endpoint", this.amazonEndPointField.getValue());
				params.put("accessid", this.amazonAccessIdField.getValue());
				params.put("accesskey", this.amazonAccessKeyField.getValue());
				break;
			case VortexEyeCloudInterface.VENDOR_RACKSPACE:
				params.put("username", this.rackspaceUsernameField.getValue());
				params.put("apikey", this.rackspaceApiKeyField.getValue());
				break;
		}
		ci.setParameters(params);
		ci.setUserName(usernameField.getValue());
		ci.setKeyContents(VortexClientUtils.encryptString(keyDataField.getValue()));
		ci.setPassword(VortexClientUtils.encryptString(passwordField.getValue()));
		if (ciid != 0)
			ci.setId(ciid);

		return ci;
	}

	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return 600;
	}

	public void setCloudInterfaceToEdit(VortexClientCloudInterface ci) {
		VortexEyeCloudInterface data = ci.getData();
		setCloudInterfaceToCopy(ci);
		this.ciid = data.getId();
		this.submitButton.setName("Update Cloud Interface (after test)");
		setIconToEdit();
		//populateMetadataFormFields(bp.getData());
		//updatePathFieldTitle();
	}
	public void setCloudInterfaceToCopy(VortexClientCloudInterface ci) {
		VortexEyeCloudInterface data = ci.getData();
		this.vendorTypeField.setValue(data.getCloudVendorType());
		descriptionField.setValue(data.getDescription());
		usernameField.setValue(data.getUserName());
		keyDataField.setValue(VortexClientUtils.decryptToString(data.getKeyContents()));
		passwordField.setValue(VortexClientUtils.decryptToString(data.getPassword()));
		if (data.getParameters() != null) {
			switch (data.getCloudVendorType()) {
				case VortexEyeCloudInterface.VENDOR_AMAZON_AWS:
					this.amazonEndPointField.setValue(data.getParameters().get("endpoint"));
					this.amazonAccessIdField.setValue(data.getParameters().get("accessid"));
					this.amazonAccessKeyField.setValue(data.getParameters().get("accesskey"));
					break;
				case VortexEyeCloudInterface.VENDOR_RACKSPACE:
					this.rackspaceUsernameField.setValue(data.getParameters().get("username"));
					this.rackspaceApiKeyField.setValue(data.getParameters().get("apikey"));
					break;
			}
		}
		updateVendorFields();
		setIconToCopy();
	}

}
