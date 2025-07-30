package com.vortex.sso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.f1.base.Console;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.container.Container;
import com.f1.container.ResultMessage;
import com.f1.container.impl.ContainerHelper;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.sso.messages.CreateSsoUserRequest;
import com.sso.messages.CreateSsoUserResponse;
import com.sso.messages.LoginSsoUserRequest;
import com.sso.messages.LoginSsoUserResponse;
import com.sso.messages.ResetPasswordRequest;
import com.sso.messages.ResetPasswordResponse;
import com.sso.messages.SsoGroupAttribute;
import com.sso.messages.SsoUser;
import com.sso.messages.UpdateSsoUserRequest;
import com.sso.messages.UpdateSsoUserResponse;

@Console(name="SsoConsole",help = "manage users, entitlements and products")
public class SsoConsole {

	private Container container;

	public SsoConsole(Container container) {
		this.container = container;
	}

	@Console(help = "add user", params = { "userName", "email", "password", "status", "requestQuestion", "requestAnswer", "firstName", "lastName", "phoneNumber", "company",
			"maxAttempts", "attributes" })
	public String addUser(String userName, String email, String password, int status, String requestQuestion, String resetAnswer, String firstName, String lastName,
			String phoneNumber, String company, int maxAttempts, String attributes) throws IOException {
		SsoUser user = container.nw(SsoUser.class);
		user.setUserName(userName);
		user.setEmail(email);
		user.setStatus((byte) status);
		user.setResetQuestion(requestQuestion);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setPhoneNumber(phoneNumber);
		user.setCompany(company);
		user.setExpires(container.getTools().getNow() + TimeUnit.DAYS.toMillis(365));
		user.setPassword(password);
		user.setResetAnswer(resetAnswer);
		user.setEncodingAlgorithm(SsoUser.ENCODING_PLAIN);
		user.setMaxBadAttempts(maxAttempts);
		List<SsoGroupAttribute> attribs = new ArrayList<SsoGroupAttribute>();
		for (String a : SH.split(',', attributes)) {
			SsoGroupAttribute att = container.nw(SsoGroupAttribute.class);
			att.setKey(SH.beforeFirst(SH.afterFirst(a, '.'), '='));
			att.setValue(SH.afterFirst(a, '='));
			attribs.add(att);
		}
		SsoHelper.encode(user, SsoUser.ENCODING_CHECKSUM64);
		CreateSsoUserRequest req = container.nw(CreateSsoUserRequest.class);
		req.setUser(user);
		req.setGroupAttributes(attribs);
		ResultMessage<CreateSsoUserResponse> response = (ResultMessage<CreateSsoUserResponse>) ContainerHelper.call(container, CreateSsoUserProcessor.class.getSimpleName(), req,
				3000);
		CreateSsoUserResponse result = response.getAction();
		if (result.getOk())
			return "Okay, id: " + result.getUser().getId();
		else
			return "Not Okay, message: " + result.getMessage();
	}

	@Console(help = "add user", params = { "id", "userName", "email", "password", "status", "requestQuestion", "requestAnswer", "firstName", "lastName", "phoneNumber", "company",
			"maxAttempts", "attributes" })
	public String updateUser(long id, String userName, String email, String password, int status, String requestQuestion, String resetAnswer, String firstName, String lastName,
			String phoneNumber, String company, int maxAttempts, String attributes) throws IOException {
		SsoUser user = container.nw(SsoUser.class);
		if (userName != null)
			user.setUserName(userName);
		if (email != null)
			user.setEmail(email);
		if (status != 0)
			user.setStatus((byte) status);
		if (requestQuestion != null)
			user.setResetQuestion(requestQuestion);
		if (firstName != null)
			user.setFirstName(firstName);
		if (lastName != null)
			user.setLastName(lastName);
		if (phoneNumber != null)
			user.setPhoneNumber(phoneNumber);
		if (company != null)
			user.setCompany(company);
		// user.setExpires(container.getTools().getNow() +
		// TimeUnit.DAYS.toMillis(365));
		if (password != null)
			user.setPassword(password);
		if (resetAnswer != null)
			user.setResetAnswer(resetAnswer);
		user.setEncodingAlgorithm(SsoUser.ENCODING_PLAIN);
		if (maxAttempts != 0)
			user.setMaxBadAttempts(maxAttempts);
		List<SsoGroupAttribute> attribs = new ArrayList<SsoGroupAttribute>();
		if (attributes != null)
			for (String a : SH.split(',', attributes)) {
				SsoGroupAttribute att = container.nw(SsoGroupAttribute.class);
				att.setKey(SH.beforeFirst(SH.afterFirst(a, '.'), '='));
				att.setValue(SH.afterFirst(a, '='));
				attribs.add(att);
			}
		SsoHelper.encode(user, SsoUser.ENCODING_CHECKSUM64);
		UpdateSsoUserRequest req = container.nw(UpdateSsoUserRequest.class);
		req.setSsoUser(user);
		req.setSsoUserId(id);
		ResultMessage<UpdateSsoUserResponse> response = (ResultMessage<UpdateSsoUserResponse>) ContainerHelper.call(container, UpdateSsoUserProcessor.class.getSimpleName(), req,
				3000);
		UpdateSsoUserResponse result = response.getAction();
		if (result.getOk())
			return "Okay, id: " + result.getSsoUser().getId();
		else
			return "Not Okay, message: " + result.getMessage();
	}

	@Console(help = "list all users")
	public String showUsers() {
		SsoState state = (SsoState) container.getPartitionController().getState("SSOSTATE", SsoState.class);
		try {
			state.getPartition().lockForRead(2, TimeUnit.SECONDS);
			Iterable<SsoUser> users = state.getUsers();
			Table table = TableHelper.toTable(users);
			table.addColumn(Integer.class, "bad_attempts", 0);
			for (Row row : table.getRows()) {
				row.put("bad_attempts", state.getLoginAttempts(row.get("id", Long.class)));
			}
			return TableHelper.toString(table, "", TableHelper.SHOW_ALL_BUT_TYPES);
		} finally {
			state.getPartition().unlockForRead();
		}
	}

	@Console(help = "simulate login, you must supply at least a username or email", params = { "username", "email", "password" })
	public String login(String namespace, String username, String email, String password) {
		LoginSsoUserRequest req = container.nw(LoginSsoUserRequest.class);
		req.setNamespace(namespace);
		req.setUserName(username);
		req.setEmail(email);
		req.setPassword(password);
		req.setEncodingAlgorithm(SsoUser.ENCODING_PLAIN);
		ResultMessage<LoginSsoUserResponse> response = (ResultMessage<LoginSsoUserResponse>) ContainerHelper
				.call(container, LoginSsoUserProcessor.class.getSimpleName(), req, 3000);
		return response.getAction().getStatus() + ": " + response.getAction().getMessage() + "  (" + response.getAction().getFailedLoginAttempts() + " bad attempts)"
				+ response.getAction().getGroupAttributes();
	}

	@Console(help = "simulate password reset, you must supply at least a username or email", params = { "username", "email", "password" })
	public String reset(String username, String email, String answer) {

		ResetPasswordRequest req = container.nw(ResetPasswordRequest.class);
		req.setUserName(username);
		req.setEmail(email);
		req.setResetAnswer(answer);
		req.setEncodingAlgorithm(SsoUser.ENCODING_PLAIN);
		ResultMessage<ResetPasswordResponse> response = (ResultMessage<ResetPasswordResponse>) ContainerHelper.call(container, ResetPasswordProcessor.class.getSimpleName(), req,
				3000);
		return response.getAction().getStatus() + " - " + response.getAction().getMessage() + ": " + response.getAction().getResetQuestion();
	}
}
