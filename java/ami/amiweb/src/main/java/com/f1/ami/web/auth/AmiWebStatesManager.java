package com.f1.ami.web.auth;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.f1.ami.web.AmiWebProperties;
import com.f1.ami.web.headless.AmiWebHeadlessWebState;
import com.f1.container.ContainerServices;
import com.f1.http.HttpSession;
import com.f1.suite.web.BasicWebUser;
import com.f1.suite.web.WebState;
import com.f1.suite.web.WebStatesManager;
import com.f1.utils.CH;
import com.f1.utils.PropertyController;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Integer;

public class AmiWebStatesManager extends WebStatesManager {

	private boolean isDev;
	private int maxSessions;
	private Boolean isAdmin;
	private ContainerServices services;
	private AtomicInteger nextId = new AtomicInteger(1);

	public AmiWebStatesManager(HttpSession session, String username, Map<String, Object> attributes, ContainerServices services) {
		super(session);
		setUser(new BasicWebUser(username));
		setUserAttributes(attributes);
		this.isDev = isDev(attributes, services.getPropertyController());
		this.isAdmin = isAdmin(attributes, services.getPropertyController());
		this.maxSessions = getMaxSessions(attributes, services.getPropertyController());
		this.services = services;
	}

	public boolean isDev() {
		return this.isDev;
	}
	public boolean isAdmin() {
		return this.isAdmin;
	}
	public int getMaxSessions() {
		return this.maxSessions;
	}

	@Override
	public boolean canAddSession() {
		return this.getNonHeadlessSessionsCount() < this.getMaxSessions();
	}

	public ContainerServices getServices() {
		return services;
	}

	public int getNextId() {
		return nextId.getAndIncrement();
	}

	static public boolean isDev(Map<String, Object> properties, PropertyController pc) {
		Boolean b = CH.getOr(Caster_Boolean.INSTANCE, properties, AmiAuthUser.PROPERTY_ISDEV, null);
		if (b == null)
			b = pc.getOptional(AmiWebProperties.PROPERTY_AMI_DEFAULT_ISDEV, Caster_Boolean.INSTANCE);
		return Boolean.TRUE.equals(b);
	}
	static public boolean isAdmin(Map<String, Object> properties, PropertyController pc) {
		Boolean b = CH.getOr(Caster_Boolean.INSTANCE, properties, AmiAuthUser.PROPERTY_ISADMIN, null);
		if (b == null)
			b = pc.getOptional(AmiWebProperties.PROPERTY_AMI_DEFAULT_ISADMIN, Caster_Boolean.INSTANCE);
		return Boolean.TRUE.equals(b);
	}
	static public int getMaxSessions(Map<String, Object> properties, PropertyController pc) {
		Integer b = CH.getOr(Caster_Integer.INSTANCE, properties, AmiAuthUser.PROPERTY_MAXSESSIONS, null);
		if (b == null)
			b = pc.getOptional(AmiWebProperties.PROPERTY_AMI_DEFAULT_MAXSESSIONS, 1);
		return b;
	}

	public int getNonHeadlessSessionsCount() {
		int n = 0;
		for (String i : super.getPgIds()) {
			WebState s = super.getState(i);
			if (s != null && !(s instanceof AmiWebHeadlessWebState))
				n++;
		}
		return n;
	}

	@Override
	public void onHttpSessionClosed() {
		super.onHttpSessionClosed();
		AmiSsoSession ssoSession = (AmiSsoSession) getUserAttributes().get(AmiAuthUser.PROPERTY_SSO_SESSION);
		if (ssoSession != null && ssoSession.isAlive())
			ssoSession.killSession();
	}

}
