package com.vortex.ssoweb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.f1.base.Day;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletDayChooserField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.BasicDay;
import com.f1.utils.FastByteArrayInputStream;
import com.f1.utils.IOH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.LocalizedLocale;
import com.f1.utils.LocalizedTimeZone;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;
import com.sso.messages.CreateSsoUserRequest;
import com.sso.messages.SsoGroupAttribute;
import com.sso.messages.SsoUser;
import com.sso.messages.UpdateSsoUserRequest;

public class NewSsoUserFormPortlet extends FormPortlet {
	private SsoService service;

	final private FormPortletTextField username;
	final private FormPortletTextField first;
	final private FormPortletTextField last;
	final private FormPortletTextField phone;
	final private FormPortletTextField psw;
	final private FormPortletTextField email;
	final private FormPortletTextField company;
	final private FormPortletTextField resetq;
	final private FormPortletTextField reseta;
	final private FormPortletSelectField<Byte> status;
	final private FormPortletSelectField<String> locale;
	final private FormPortletSelectField<String> timezone;
	final private FormPortletButton submitButton;
	final private FormPortletDayChooserField expiresOn;
	private SsoWebGroup parentGroup;

	private SsoUser userToEdit;

	public NewSsoUserFormPortlet(PortletConfig config) {
		super(config);
		addField(this.username = new FormPortletTextField("User"));
		addField(this.expiresOn = new FormPortletDayChooserField("Expires on", getManager().getLocaleFormatter().getTimeZone(), false));
		addField(this.first = new FormPortletTextField("First Name"));
		addField(this.last = new FormPortletTextField("Last Name"));
		addField(this.phone = new FormPortletTextField("Phone"));
		addField(this.psw = new FormPortletTextField("Password").setPassword(true));
		addField(this.email = new FormPortletTextField("Email"));
		addField(this.company = new FormPortletTextField("Company"));
		addField(this.resetq = new FormPortletTextField("Request Question"));
		addField(this.reseta = new FormPortletTextField("Request Answer"));
		addField(this.status = new FormPortletSelectField<Byte>(Byte.class, "Status").addOption(SsoUser.STATUS_ENABLED, "ENABLED").addOption(SsoUser.STATUS_DISABLED, "DISABLED")
				.addOption(SsoUser.STATUS_LOCKED, "LOCKED"));
		this.status.setValue(SsoUser.STATUS_ENABLED);
		this.expiresOn.setDefaultValue(new Tuple2<Day, Day>(new BasicDay(getManager().getLocaleFormatter().getTimeZone(), getManager().getTools().getNowDate()).add(100), null));

		this.locale = new FormPortletSelectField<String>(String.class, "Locale");
		final LocaleFormatter formatter = getManager().getState().getWebStatesManager().getSession().getFormatter();

		for (final LocalizedLocale l : formatter.getAvailableLocales()) {
			locale.addOptionNoThrow(l.getId(), l.toDisplayString());
			if (l.isLocalLocale()) {
				locale.setDefaultValue(l.getId());
				locale.setValue(l.getId());
			}
		}

		this.timezone = new FormPortletSelectField<String>(String.class, "Timezone");
		for (LocalizedTimeZone tz : formatter.getAvailableTimeZones()) {
			timezone.addOptionNoThrow(tz.getId(), tz.toDisplayString());
			if (tz.isLocalTimeZone()) {
				timezone.setDefaultValue(tz.getId());
				timezone.setValue(tz.getId());
			}
		}

		addField(this.timezone);
		addField(this.locale);
		addButton(this.submitButton = new FormPortletButton("Create User"));

		service = (SsoService) getManager().getService(SsoService.ID);
		service.addPortlet(this);

	}
	public static String encode(String text, byte encodingType) {
		switch (encodingType) {
			case SsoUser.ENCODING_CHECKSUM64:
				try {
					return text == null ? null : SH.toString(IOH.checkSumBsdLong(new FastByteArrayInputStream(text.getBytes())));
				} catch (IOException e) {
					throw OH.toRuntime(e);
				}
			case SsoUser.ENCODING_PLAIN:
				return text;
			default:
				throw new RuntimeException("unknown encoding type: " + encodingType);
		}
	}
	@Override
	protected void onUserPressedButton(FormPortletButton button) {
		if (button == submitButton) {
			SsoUser user = nw(SsoUser.class);
			List<SsoGroupAttribute> atts = new ArrayList<SsoGroupAttribute>();
			user.setExpires(this.expiresOn.getValue().getA().getStartMillis());
			user.setNow(getManager().getTools().getNow());
			user.setUserName(username.getValue());
			user.setFirstName(first.getValue());
			user.setLastName(last.getValue());
			user.setPhoneNumber(phone.getValue());
			user.setEmail(email.getValue());
			user.setCompany(company.getValue());
			user.setResetQuestion(resetq.getValue());
			user.setStatus((status.getValue()));
			user.setEncodingAlgorithm(SsoUser.ENCODING_CHECKSUM64);
			user.setPassword(encode(psw.getValue(), user.getEncodingAlgorithm()));
			user.setResetAnswer(encode(reseta.getValue(), user.getEncodingAlgorithm()));
			user.setMaxBadAttempts(5);
			atts.add(newAttribute("Locale", SsoGroupAttribute.TYPE_TEXT, locale.getValue()));
			atts.add(newAttribute("TimeZone", SsoGroupAttribute.TYPE_TEXT, timezone.getValue()));
			if (userToEdit != null) {
				if ("********".equals(psw.getValue()))
					user.setPassword(userToEdit.getPassword());
				if ("********".equals(reseta.getValue()))
					user.setResetAnswer(userToEdit.getResetAnswer());
				user.setEncodingAlgorithm(userToEdit.getEncodingAlgorithm());
				UpdateSsoUserRequest request = nw(UpdateSsoUserRequest.class);
				user.setId(userToEdit.getId());
				request.setSsoUser(user);
				request.setSsoUserId(userToEdit.getId());
				//request.setGroupAttributes(atts);
				//request.setParentGroups(parentGroup != null ? new long[] { parentGroup.getGroup().getId() } : OH.EMPTY_LONG_ARRAY);
				service.sendRequestToBackend(getPortletId(), request);
				close();
			} else {
				CreateSsoUserRequest request = nw(CreateSsoUserRequest.class);
				request.setUser(user);
				request.setGroupAttributes(atts);
				request.setParentGroups(parentGroup != null ? new long[] { parentGroup.getGroup().getId() } : OH.EMPTY_LONG_ARRAY);
				service.sendRequestToBackend(getPortletId(), request);
			}
		}

		super.onUserPressedButton(button);
	}

	public void setUserToEdit(SsoUser user) {
		userToEdit = user;
		username.setValue(user.getUserName());
		expiresOn.setValue(new Tuple2<Day, Day>(new BasicDay(getManager().getLocaleFormatter().getTimeZone(), new Date(user.getExpires())), null));
		first.setValue(user.getFirstName());
		last.setValue(user.getLastName());
		phone.setValue(user.getPhoneNumber());
		psw.setValue("********");
		email.setValue(user.getEmail());
		company.setValue(user.getCompany());
		resetq.setValue(user.getResetQuestion());
		reseta.setValue("********");
		status.setValue(user.getStatus());
		this.submitButton.setName("Update User");
	}
	public void setUserToCopy(SsoUser user) {
		userToEdit = user;
		username.setValue(user.getUserName());
		expiresOn.setValue(new Tuple2<Day, Day>(new BasicDay(getManager().getLocaleFormatter().getTimeZone(), new Date(user.getExpires())), null));
		first.setValue(user.getFirstName());
		last.setValue(user.getLastName());
		phone.setValue(user.getPhoneNumber());
		email.setValue(user.getEmail());
		company.setValue(user.getCompany());
		resetq.setValue(user.getResetQuestion());
		status.setValue(user.getStatus());
	}
	private SsoGroupAttribute newAttribute(String key, byte valueType, String value) {
		final SsoGroupAttribute r = nw(SsoGroupAttribute.class);
		r.setKey(key);
		r.setValue(value);
		r.setType(valueType);
		return r;
	}
	@Override
	protected boolean onUserChangedValue(FormPortletField<?> field, Map<String, String> attributes) {
		return super.onUserChangedValue(field, attributes);
	}

	public static class Builder extends AbstractPortletBuilder<NewSsoUserFormPortlet> {

		public Builder() {
			super(NewSsoUserFormPortlet.class);
			setUserCreatable(false);
		}

		public static final String ID = "newSsoUserTablePortlet";

		@Override
		public NewSsoUserFormPortlet buildPortlet(PortletConfig portletConfig) {
			return new NewSsoUserFormPortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "New User";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	public void setGroup(SsoWebGroup node) {
		this.parentGroup = node;
	}
}
