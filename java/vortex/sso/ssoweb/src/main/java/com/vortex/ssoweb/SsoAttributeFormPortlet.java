package com.vortex.ssoweb;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.sso.messages.SsoGroupAttribute;
import com.sso.messages.UpdateSsoGroupRequest;

public class SsoAttributeFormPortlet extends FormPortlet {

	private FormPortletSelectField<Long> groupField;
	private FormPortletTextField keyField;
	private FormPortletTextField valueField;
	private Long attributeId;
	private FormPortletButton submitButton;
	private SsoService service;

	public SsoAttributeFormPortlet(PortletConfig config) {
		super(config);
		groupField = addField(new FormPortletSelectField<Long>(Long.class, "group"));
		keyField = addField(new FormPortletTextField("key"));
		valueField = addField(new FormPortletTextField("value"));

		service = (SsoService) getManager().getService(SsoService.ID);
		for (SsoWebGroup group : service.getSsoTree().getGroups()) {
			groupField.addOption(group.getGroupId(), group.getName());
		}
		this.submitButton = addButton(new FormPortletButton("Submit"));
	}

	public void init(long groupId, String key, String value) {
		if (groupId > 0)
			groupField.setValue(groupId);
		if (SH.is(key))
			keyField.setValue(key);
		if (SH.is(value))
			valueField.setValue(value);
	}

	public static class Builder extends AbstractPortletBuilder<SsoAttributeFormPortlet> {

		public static final String ID = "SsoAttributePortlet";

		public Builder() {
			super(SsoAttributeFormPortlet.class);
			setUserCreatable(false);
		}

		@Override
		public SsoAttributeFormPortlet buildPortlet(PortletConfig portletConfig) {
			return new SsoAttributeFormPortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "New Member Attribute";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onUserPressedButton(FormPortletButton button) {
		if (button == submitButton) {
			UpdateSsoGroupRequest updateRequest = getManager().getGenerator().nw(UpdateSsoGroupRequest.class);
			updateRequest.setGroupId(this.groupField.getValue());
			SsoGroupAttribute attr = getManager().getGenerator().nw(SsoGroupAttribute.class);
			attr.setGroupId(updateRequest.getGroupId());
			SsoWebGroup group = service.getSsoTree().getGroup(updateRequest.getGroupId());
			if (group == null) {
				getManager().showAlert("Group not found: " + updateRequest.getGroupId());
				return;
			}
			attr.setKey(this.keyField.getValue());
			attr.setValue(this.valueField.getValue());
			updateRequest.setGroupAttributes(CH.l(attr));
			if (attributeId != null)
				attr.setId(this.attributeId);
			else {
				if (group.getGroupAttributes().containsKey(attr.getKey())) {
					getManager().showAlert("Key for Group '" + group.getName() + "' already exists: " + attr.getKey());
					return;
				}
			}
			service.sendRequestToBackend(getPortletId(), updateRequest);
		} else
			super.onUserPressedButton(button);
		close();
	}

	public void setModifyAttributeId(Long attributeId) {
		this.attributeId = attributeId;
	}
}
