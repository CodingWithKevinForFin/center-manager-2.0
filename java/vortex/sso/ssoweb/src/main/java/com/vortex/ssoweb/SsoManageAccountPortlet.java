package com.vortex.ssoweb;

import java.io.IOException;

import com.f1.base.Action;
import com.f1.container.ResultMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.FastByteArrayInputStream;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.sso.messages.SsoUser;
import com.sso.messages.UpdateSsoUserRequest;
import com.sso.messages.UpdateSsoUserResponse;

public class SsoManageAccountPortlet extends FormPortlet {

	final private SsoUser user;
	final private FormPortletTextField currentPasswordField;
	final private FormPortletTextField newPasswordField;
	final private FormPortletTextField newPasswordConfirmField;
	final private FormPortletTextField resetQuestionField;
	private FormPortletButton updateButton;
	private FormPortletButton cancelButton;

	public SsoManageAccountPortlet(PortletConfig config, SsoUser user) {
		super(config);
		this.user = user;
		currentPasswordField = addField(new FormPortletTextField("Current Password")).setPassword(true);
		if (SH.is(user.getResetQuestion())) {
			resetQuestionField = addField(new FormPortletTextField(user.getResetQuestion())).setPassword(true);
		} else
			resetQuestionField = null;
		newPasswordField = addField(new FormPortletTextField("New Password")).setPassword(true);
		newPasswordConfirmField = addField(new FormPortletTextField("New Password Confirm")).setPassword(true);
		updateButton = addButton(new FormPortletButton("Update Password"));
		cancelButton = addButton(new FormPortletButton("Cancel"));
	}

	@Override
	public void onUserPressedButton(FormPortletButton button) {
		if (button == updateButton) {
			if (SH.isnt(newPasswordField.getValue())) {
				getManager().showAlert("New password required");
				return;
			} else if (OH.ne(newPasswordConfirmField.getValue(), newPasswordField.getValue())) {
				getManager().showAlert("New password and Confirm Password do not match");
				return;
			}
			UpdateSsoUserRequest update = nw(UpdateSsoUserRequest.class);
			if (resetQuestionField != null)
				update.setUserSuppliedAnswer(encode(resetQuestionField.getValue(), SsoUser.ENCODING_CHECKSUM64));
			update.setUserSuppliedPassword(encode(currentPasswordField.getValue(), SsoUser.ENCODING_CHECKSUM64));
			update.setEncodingAlgorithm(SsoUser.ENCODING_CHECKSUM64);
			//if (OH.ne(update.getUserSuppliedPassword(), user.getPassword())) {
			//getManager().showAlert("password incorrect");
			//return;
			//} else if (OH.ne(update.getUserSuppliedAnswer(), user.getResetAnswer())) {
			//getManager().showAlert("reset answer incorrect");
			//return;
			//} else 
			{
				String namespace = getManager().getTools().getRequired("sso.namespace");
				update.setNamespace(namespace);
				update.setClientLocation(getManager().getState().getWebStatesManager().getRemoteAddress());
				update.setSession(OH.toString(getManager().getState().getPartitionId()));
				update.setSsoUserId(user.getId());
				SsoUser user2 = user.clone();
				user2.setPassword(encode(newPasswordField.getValue(), SsoUser.ENCODING_CHECKSUM64));
				update.setSsoUser(user2);
				getManager().sendRequestToBackend("SSO", getPortletId(), update);
			}
		} else if (button == cancelButton) {
			close();
		} else
			super.onUserPressedButton(button);
	}
	public void onBackendResponse(ResultMessage<Action> result) {
		UpdateSsoUserResponse response = (UpdateSsoUserResponse) result.getAction();
		if (response.getOk()) {
			if (SH.isnt(response.getMessage()))
				getManager().showAlert("Password changed");
			getManager().getState().getWebStatesManager().setUser(new SsoWebUser(response.getSsoUser()));
			close();
		} else {
			if (SH.isnt(response.getMessage()))
				getManager().showAlert("Password change failed");
		}
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

}
