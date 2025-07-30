package com.vortex.sso;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.RequestOutputPort;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.Iterator2Iterable;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.VH;
import com.f1.utils.structs.Tuple3;
import com.sso.messages.LoginSsoUserRequest;
import com.sso.messages.LoginSsoUserResponse;
import com.sso.messages.SsoGroupAttribute;
import com.sso.messages.SsoUpdateEvent;
import com.sso.messages.SsoUser;
import com.sso.messages.UpdateSsoUserRequest;
import com.sso.messages.UpdateSsoUserResponse;

public class LoginSsoUserProcessor extends BasicRequestProcessor<LoginSsoUserRequest, SsoState, LoginSsoUserResponse> {

	public final OutputPort<SsoUpdateEvent> broadcastPort = newOutputPort(SsoUpdateEvent.class);
	public final RequestOutputPort<UpdateSsoUserRequest, UpdateSsoUserResponse> updatePort = newRequestOutputPort(UpdateSsoUserRequest.class, UpdateSsoUserResponse.class);
	private boolean useLdap = false;
	private String dn;
	private String domain;
	private String ldapAdmin;
	private String ldapAdminPwd;
	private PropertyController ldapProps;

	private final static Tuple3<String, Byte, String>[] LDAP_ERRORS = new Tuple3[] { //
	new Tuple3(".*code 49.*data 52e.*", LoginSsoUserResponse.STATUS_PASSWORD_INVALID, "Password Invalid"), //
			new Tuple3(".*code 49.*data 52f.*", LoginSsoUserResponse.STATUS_ACCOUNT_RESTRICTION, "Account Restrictions"), //
			new Tuple3(".*code 49.*data 530.*", LoginSsoUserResponse.STATUS_INVALID_LOGON_HOURS, "Invalid Logon Hours"), //
			new Tuple3(".*code 49.*data 532.*", LoginSsoUserResponse.STATUS_PASSWORD_EXPIRED, "Password Expired"), //
			new Tuple3(".*code 49.*data 533.*", LoginSsoUserResponse.STATUS_ACCOUNT_DISABLED, "Account Disabled"), //
			new Tuple3(".*code 49.*data 701.*", LoginSsoUserResponse.STATUS_ACCOUNT_EXPIRED, "Account Expired"), //
			new Tuple3(".*code 49.*data 773.*", LoginSsoUserResponse.STATUS_PASSWORD_MUST_CHANGE, "Password Must Change"), //
			new Tuple3(".*code 49.*data 775.*", LoginSsoUserResponse.STATUS_ACCOUNT_LOCKED, "Account Locked"), //
	};

	public LoginSsoUserProcessor() {
		super(LoginSsoUserRequest.class, SsoState.class, LoginSsoUserResponse.class);
	}

	@Override
	public void init() {
		super.init();

		PropertyController props = getTools().getSubPropertyController("sso.ldap.");
		useLdap = props.getOptional("enabled", false);
		if (useLdap) {
			dn = props.getRequired("dn");
			domain = props.getRequired("domain");
			ldapAdmin = props.getRequired("admin.user");
			ldapAdminPwd = props.getRequired("admin.pwd");

			ldapProps = props.getSubPropertyController("props.");
		}
	}

	@Override
	protected LoginSsoUserResponse processRequest(RequestMessage<LoginSsoUserRequest> action, SsoState state, ThreadScope threadScope) throws Exception {
		LoginSsoUserRequest request = action.getAction();
		String session = request.getSession();
		int attemptsLeft = 0;
		SsoUser user = null, user2 = null;

		final LoginSsoUserResponse response = processRequest2(action, state, threadScope);
		user = response.getUser();

		if (SH.isnt(request.getNamespace()))
			throw new RuntimeException("namespace required");
		if (response.getStatus() != LoginSsoUserResponse.STATUS_OK) {
			if (user != null) {
				final int badAttempts = state.onLoginAttempt(user.getId(), false);
				if (user.getMaxBadAttempts() > 0 && user.getMaxBadAttempts() <= badAttempts && user.getStatus() == SsoUser.STATUS_ENABLED) {
					user2 = nw(SsoUser.class);
					user2.setStatus(SsoUser.STATUS_LOCKED);
					final UpdateSsoUserRequest update = nw(UpdateSsoUserRequest.class);
					update.setSsoUserId(user.getId());
					update.setSsoUser(user2);
					updatePort.requestWithFuture(update, threadScope).getResult();
				}
				attemptsLeft = user.getMaxBadAttempts() - badAttempts;
			}
		} else {
			state.onLoginAttempt(response.getUser().getId(), true);
			Map<String, SsoGroupAttribute> attributes = new HashMap<String, SsoGroupAttribute>();
			for (SsoGroupAttribute ga : state.getGroupAttributes(user.getGroupId()))
				attributes.put(ga.getKey(), ga);
			response.setGroupAttributes(attributes);
			response.setUser(user);
		}
		response.setFailedLoginAttempts(attemptsLeft);
		broadcastSsoEvent(request, response, session, request.getNamespace(), request.getUserName(), threadScope);
		final SsoDbService dbservice = (SsoDbService) getServices().getService("DB");
		Connection con = dbservice.getConnection();
		try {
			dbservice.insertUserEvent(request.getUserName(), request.getEmail(), user == null ? -1 : user.getId(), SsoUpdateEvent.USER_LOGIN, request.getNamespace(), con);
		} finally {
			IOH.close(con);
		}

		return response;
	}

	protected LoginSsoUserResponse processRequest2(RequestMessage<LoginSsoUserRequest> action, SsoState state, ThreadScope threadScope) throws Exception {
		LoginSsoUserResponse r = nw(LoginSsoUserResponse.class);
		String message = null;
		byte status;
		SsoUser user = null, user2 = null;
		try {
			final LoginSsoUserRequest req = action.getAction();

			// Find user
			if (SH.is(req.getUserName())) {
				user = state.getUserByUserName(req.getUserName());
				if (user == null)
					message = "username not found";
				else if (req.getEmail() != null && OH.ne(req.getEmail(), user.getEmail())) {
					message = "email incorrect";
					user = null;
				}

			} else if (SH.is(req.getEmail())) {
				user = state.getUserByEmail(req.getEmail());
				if (user == null)
					message = "email not found";
			} else {
				message = "Username or email required";
				user = null;
			}
			if (user == null) {
				status = LoginSsoUserResponse.STATUS_USER_NOT_FOUND;
			} else {
				user2 = VH.clone(user);
				//user2.setPassword(null);
				//user2.setEncodingAlgorithm((byte) 0);
				r.setUser(user2);
				if (SH.isnt(req.getPassword())) {
					message = "Password required";
					status = LoginSsoUserResponse.STATUS_PASSWORD_INVALID;
				} else {
					if (useLdap) {

						//ensure that the encoding is correct...we wasted a good day troubleshooting this :(
						if (req.getEncodingAlgorithm() != SsoUser.ENCODING_PLAIN) {
							LH.log(log, Level.SEVERE, "SSO in ldap mode only supports requests with PLAIN pwd encoding. Passed encoding: ", req.getEncodingAlgorithm());
							status = LoginSsoUserResponse.STATUS_INTERNAL_ERROR;
							message = "Incorrect encoding [" + req.getEncodingAlgorithm() + "] ... expecting PLAIN[" + SsoUser.ENCODING_PLAIN + "]";
						} else {

							String name = user.getUserName() + "@" + domain;

							Properties properties = new Properties();
							properties.putAll(ldapProps.getProperties());
							properties.put(Context.SECURITY_PRINCIPAL, name);
							properties.put(Context.SECURITY_CREDENTIALS, req.getPassword());
							InitialDirContext context = null;
							try {
								context = new InitialDirContext(properties);
								status = LoginSsoUserResponse.STATUS_OK;
							} catch (CommunicationException ce) {
								LH.log(log, Level.SEVERE, "Failed to connect to ldap", ce);
								status = LoginSsoUserResponse.STATUS_INTERNAL_ERROR;
								message = "Failed to connect to LDAP";
							} catch (NamingException e) {
								LH.log(log, Level.INFO, "Failed to bind user to ldap ", name, " via ldap", e);
								status = LoginSsoUserResponse.STATUS_PASSWORD_INVALID;
								message = "password invalid";

								for (Tuple3<String, Byte, String> t : LDAP_ERRORS) {
									if (e.getMessage().matches(t.getA())) {
										status = t.getB();
										message = t.getC();
										break;
									}
								}

								switch (status) {
									case LoginSsoUserResponse.STATUS_PASSWORD_EXPIRED:
									case LoginSsoUserResponse.STATUS_PASSWORD_MUST_CHANGE:
										//if user is required to change pwd and new pwd has been provided ... attempt to change the pwd and on success log user in
										if (SH.is(req.getNewPassword())) {
											//do this as admin
											properties.put(Context.SECURITY_PRINCIPAL, ldapAdmin);
											properties.put(Context.SECURITY_CREDENTIALS, ldapAdminPwd);

											InitialDirContext context2 = null;
											try {
												context2 = new InitialDirContext(properties);

												//need to find the actual name
												SearchControls searchCtls = new SearchControls();
												searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
												String foundName = null;
												for (SearchResult o : new Iterator2Iterable<SearchResult>(context2.search(dn, "(&(objectClass=user)(userPrincipalName=" + name
														+ "))", searchCtls))) {
													foundName = o.getName();
													break;
												}

												String quotedPassword = "\"" + req.getNewPassword() + "\"";
												ModificationItem[] mods = new ModificationItem[1];
												mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("unicodePwd", quotedPassword.getBytes("UTF-16LE")));

												context2.modifyAttributes(foundName + "," + dn, mods);

												status = LoginSsoUserResponse.STATUS_OK;

											} catch (NamingException ex) {
												LH.log(log, Level.INFO, "Failed to change pwd for ", name, ex);
												status = LoginSsoUserResponse.STATUS_PASSWORD_CHANGE_FAILED;
												message = ex.getMessage();
												if (message.contains("problem 5003 (WILL_NOT_PERFORM)"))
													message = "Password doesn't satisfy policy requirement";
											} finally {
												if (context2 != null)
													context2.close();
											}

										}
										break;
								}
							} finally {
								if (context != null)
									context.close();
							}
						}

					} else {

						if (user.getStatus() != SsoUser.STATUS_ENABLED) {
							status = LoginSsoUserResponse.STATUS_ACCOUNT_DISABLED;
							message = "account locked or disabled";
						} else {

							// Encode password
							String password = SsoHelper.encode(req.getPassword(), req.getEncodingAlgorithm(), user.getEncodingAlgorithm());
							long now = getTools().getNow();
							long daysRemaining = TimeUnit.DAYS.convert(user.getExpires() - now, TimeUnit.MILLISECONDS);

							// Compare passwords
							if (OH.ne(password, user.getPassword())) {
								message = "password invalid";
								status = LoginSsoUserResponse.STATUS_PASSWORD_INVALID;
							} else if (user.getExpires() >= 0 && now >= user.getExpires()) {
								status = LoginSsoUserResponse.STATUS_ACCOUNT_EXPIRED;
								message = "account has expired";
							} else {
								if (daysRemaining < 10)
									message = "account will expire soon";
								status = LoginSsoUserResponse.STATUS_OK;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			status = LoginSsoUserResponse.STATUS_INTERNAL_ERROR;
			message = e.getMessage();
		}
		if (user2 == null)
			user2 = user;
		r.setStatus(status);
		r.setMessage(message);
		r.setUser(user2);
		return r;
	}
	private void broadcastSsoEvent(LoginSsoUserRequest request, LoginSsoUserResponse response, String session, String namespace, String username, ThreadScope threadScope) {
		SsoUpdateEvent event = nw(SsoUpdateEvent.class);
		SsoUser user = response.getUser();
		event.setUsers(CH.l(user));
		event.setOk(response.getStatus() == LoginSsoUserResponse.STATUS_OK);
		event.setMessage(response.getMessage());
		event.setType(SsoUpdateEvent.USER_LOGIN);
		event.setSession(session);
		event.setName(username);
		event.setNow(this.getTools().getNow());
		event.setNamespace(namespace);
		event.setClientLocation(request.getClientLocation());
		event.setMemberId(response.getUser() == null ? -1 : response.getUser().getId());
		broadcastPort.send(event, threadScope);
	}

}
