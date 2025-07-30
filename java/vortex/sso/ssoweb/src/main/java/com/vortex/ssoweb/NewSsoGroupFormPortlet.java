package com.vortex.ssoweb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.AH;
import com.f1.utils.EH;
import com.f1.utils.OH;
import com.sso.messages.CreateSsoGroupRequest;
import com.sso.messages.SsoGroup;
import com.sso.messages.SsoGroupAttribute;

public class NewSsoGroupFormPortlet extends FormPortlet {
	private SsoService service;
	private static final String sName = "ssog.Name";

	private FormPortletTextField groupname;
	private FormPortletButton submitButton;
	private SsoWebGroup parentGroup;
	private FormPortletSelectField<Short> grouptype;
	private FormPortletTextField patternHostField;
	private FormPortletTextField patternAppNameField;
	private FormPortletTextField patternAccountField;
	private FormPortletTextField patternProcessField;

	public NewSsoGroupFormPortlet(PortletConfig config) {
		super(config);
		addField(this.grouptype = new FormPortletSelectField<Short>(Short.class, "ssog.type"));
		this.patternHostField = new FormPortletTextField("ssog.patternhost");
		this.patternAccountField = new FormPortletTextField("ssog.patternaccount");
		this.patternHostField.setValue("*");

		this.patternAccountField.setValue("*");
		this.patternProcessField = new FormPortletTextField("ssog.patternprocess");
		this.patternAppNameField = new FormPortletTextField("ssog.patternappName");
		this.patternAppNameField.setValue("*");
		this.patternProcessField.setValue("*");
		grouptype.addOption((SsoGroup.GROUP_TYPE_GENERIC), "generic");
		grouptype.addOption((SsoGroup.GROUP_TYPE_HOST), "host");
		grouptype.addOption((SsoGroup.GROUP_TYPE_REGION), "region");
		grouptype.addOption((SsoGroup.GROUP_TYPE_PROCESS), "process");
		grouptype.addOption((SsoGroup.GROUP_TYPE_ENVIRONMENT), "environment");
		grouptype.addOption((SsoGroup.GROUP_TYPE_DEPLOYMENT), "deployment");
		grouptype.addOption((SsoGroup.GROUP_TYPE_ACCOUNT), "account");
		addField(this.groupname = new FormPortletTextField(sName));

		addButton(this.submitButton = new FormPortletButton("Create Group"));
		service = (SsoService) getManager().getService(SsoService.ID);
		service.addPortlet(this);
	}
	@Override
	protected void onUserPressedButton(FormPortletButton button) {
		if (button == submitButton) {
			CreateSsoGroupRequest request = nw(CreateSsoGroupRequest.class);
			SsoGroup group = nw(SsoGroup.class);
			group.setNow(EH.currentTimeMillis());
			group.setName(groupname.getValue());
			group.setType(grouptype.getValue());
			request.setGroup(group);
			List<SsoGroupAttribute> groupAttributes = new ArrayList<SsoGroupAttribute>();
			switch (group.getType()) {
				case SsoGroup.GROUP_TYPE_PROCESS:
					if (!"*".equals(patternProcessField.getValue()))
						groupAttributes.add(newAttribute("process_mask", patternProcessField.getValue(), SsoGroupAttribute.TYPE_TEXT));
					if (!"*".equals(patternAppNameField.getValue()))
						groupAttributes.add(newAttribute("appName_mask", patternAppNameField.getValue(), SsoGroupAttribute.TYPE_TEXT));
				case SsoGroup.GROUP_TYPE_HOST:
				case SsoGroup.GROUP_TYPE_REGION:
				case SsoGroup.GROUP_TYPE_ENVIRONMENT:
					if (!"*".equals(patternHostField.getValue()))
						groupAttributes.add(newAttribute("host_mask", patternHostField.getValue(), SsoGroupAttribute.TYPE_TEXT));
					break;
				case SsoGroup.GROUP_TYPE_ACCOUNT:
					if (!"*".equals(patternAccountField.getValue()))
						groupAttributes.add(newAttribute("account_mask", patternAccountField.getValue(), SsoGroupAttribute.TYPE_TEXT));
					break;
			}
			if (parentGroup == null)
				request.setParentGroups(OH.EMPTY_LONG_ARRAY);
			else
				request.setParentGroups(AH.longs(parentGroup.getGroupId()));
			request.setGroupAttributes(groupAttributes);
			service.sendRequestToBackend(getPortletId(), request);
			close();
		}

		super.onUserPressedButton(button);
	}

	private SsoGroupAttribute newAttribute(String key, String value, byte type) {
		SsoGroupAttribute r = nw(SsoGroupAttribute.class);
		r.setKey(key);
		r.setType(type);
		r.setValue(value);
		return r;
	}
	@Override
	protected boolean onUserChangedValue(FormPortletField<?> field, Map<String, String> attributes) {
		super.onUserChangedValue(field, attributes);
		if (field == grouptype) {
			removeFieldNoThrow(patternHostField);
			removeFieldNoThrow(patternAccountField);
			removeFieldNoThrow(patternProcessField);
			removeFieldNoThrow(patternAppNameField);
			switch ((grouptype.getValue())) {
				case SsoGroup.GROUP_TYPE_PROCESS:
					addField(patternProcessField);
					addField(patternAppNameField);
					addField(patternHostField);
					break;
				case SsoGroup.GROUP_TYPE_HOST:
				case SsoGroup.GROUP_TYPE_REGION:
				case SsoGroup.GROUP_TYPE_ENVIRONMENT:
					addField(patternHostField);
					break;
				case SsoGroup.GROUP_TYPE_ACCOUNT:
					addField(patternAccountField);
					break;
				default:
					break;
			}
		}
		return true;
	}
	public void setGroup(SsoWebGroup node) {
		this.parentGroup = node;
	}

	public static class Builder extends AbstractPortletBuilder<NewSsoGroupFormPortlet> {

		public static final String ID = "newSsoGroupTablePortlet";

		public Builder() {
			super(NewSsoGroupFormPortlet.class);
			setUserCreatable(false);
		}

		@Override
		public NewSsoGroupFormPortlet buildPortlet(PortletConfig portletConfig) {
			return new NewSsoGroupFormPortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "New group";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

}
