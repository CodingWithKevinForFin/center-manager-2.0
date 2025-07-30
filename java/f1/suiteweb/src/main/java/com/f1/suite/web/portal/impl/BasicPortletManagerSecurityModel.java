package com.f1.suite.web.portal.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.PortletManagerSecurityModel;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;

public class BasicPortletManagerSecurityModel implements PortletManagerSecurityModel {

	private static final Logger log = LH.get();

	public static final Set<String> NON_PORTLET_PERMITTED_ACTIONS = CH.s("audit", "polling", "userKey", "userClick", "userScroll", "userActivatePortlet", "popupClosed", "init",
			"popupFailed", "notificationClosed", "notificationClicked", "notificationDenied", "logout", "postInit", "restResponse", "service", "unload");
	public static final Set<String> NON_PORTLET_PERMITTED_ADMIN_ACTIONS = CH.s("exportConfig", "manageAccount", "saveConfig", "loadConfig");
	private PortletManager portletManager;

	public BasicPortletManagerSecurityModel(PortletManager portletManager) {
		this.portletManager = portletManager;
	}
	/*
	 * From AmiWebService
	 */

	private Map<String, Tuple2<Set<String>, List<String>>> assertNotCachedActionsCache = new HashMap<String, Tuple2<Set<String>, List<String>>>();

	@Override
	public void assertPermitted(Portlet source, String action, String permissableActions) {
		//		if (!this.getDesktop().getIsLocked())
		//			return;
		if (SH.is(permissableActions)) {
			if (OH.eq(action, permissableActions))
				return;
			Tuple2<Set<String>, List<String>> t = assertNotCachedActionsCache.get(permissableActions);
			if (t == null) {
				Set<String> a = new HashSet<String>();
				List<String> b = new ArrayList<String>();
				for (String s : SH.split(',', permissableActions))
					if (s.endsWith("*"))
						b.add(SH.stripSuffix(s, "*", true));
					else
						a.add(s);
				t = new Tuple2<Set<String>, List<String>>(a, b);
				assertNotCachedActionsCache.put(permissableActions, t);
			}
			if (t.getA().contains(action))
				return;
			for (String s : t.getB())
				if (action.startsWith(s))
					return;
		}
		this.throwSecurityException(source, action);
	}
	public void assertPermitted(Portlet source, String action) { // description renamed to action
		assertPermitted(source, action, null);
	}

	@Override
	public boolean hasPermissions(PortletManager manager, Portlet portlet, String type, Map<String, String> attributes) {
		if (portlet == null)
			return NON_PORTLET_PERMITTED_ACTIONS.contains(type) || NON_PORTLET_PERMITTED_ADMIN_ACTIONS.contains(type);
		return true;
	}

	@Deprecated
	@Override
	public boolean hasPermissions(PortletManager manager, String type, Map<String, String> attributes) {
		return NON_PORTLET_PERMITTED_ACTIONS.contains(type) || NON_PORTLET_PERMITTED_ADMIN_ACTIONS.contains(type);
	}

	@Override
	public void raiseSecurityViolation(String reason) {
		HttpRequestResponse action = this.portletManager.getCurrentRequestAction();
		if (action != null)
			LH.severe(log, this.portletManager.describeUser(), " violated security permissions, attributes are: ", action.getAttributes(), new SecurityException(reason));
		else
			LH.severe(log, this.portletManager.describeUser(), " violated security permissions: ", new SecurityException(reason));
		portletManager.getState().getWebStatesManager().getSession().kill();
		portletManager.close();
	}
	@Override
	public void raiseSecurityViolation(Portlet portlet, String type, Map<String, String> attributes) {
		if (portlet == null)
			LH.severe(log, this.portletManager.describeUser(), " violated security permissions. Action=", type, "   attributes=", attributes);
		else
			LH.severe(log, this.portletManager.describeUser(), " violated security permissions. Action=", OH.getSimpleClassName(portlet), "::", type, "   attributes=", attributes);
		portletManager.getState().getWebStatesManager().getSession().kill();
		portletManager.close();
	}

	@Override
	public void throwSecurityException(Portlet portlet, String reason) { // Renamed action to reason
		LH.warning(log, this.portletManager.describeUser(), " is locked and attempted to access unlocked-only feature. Request=",
				this.portletManager.getCurrentRequestAction().getRequestUri(), " Portlet Structure: " + describe(portlet));
		//CHANGING: 
		//		this.raiseSecurityViolation(action);
		//TO:
		HttpRequestResponse action = this.portletManager.getCurrentRequestAction();
		if (action != null)
			LH.severe(log, this.portletManager.describeUser(), " violated security permissions, attributes are: ", action.getAttributes(), new SecurityException(reason));
		else
			LH.severe(log, this.portletManager.describeUser(), " violated security permissions: ", new SecurityException(reason));
		portletManager.close();
		throw new SecurityException("Attempt to access privildeged action '" + reason + "' on '" + OH.getSimpleClassName(portlet) + "' from locked account");
		// TODO Auto-generated method stub

	}
	private static StringBuilder describe(Portlet portlet) {
		StringBuilder sb = new StringBuilder();
		for (;;) {
			SH.getSimpleName(portlet.getClass(), sb);
			portlet = portlet.getParent();
			if (portlet == null)
				break;
			sb.append(" ==> ");
		}
		return sb;
	}
}
