package com.f1.ami.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.ami.amicommon.AmiAuthenticatorApplyEntitlements;
import com.f1.ami.web.auth.AmiAuthUser;
import com.f1.ami.web.auth.AmiWebLoginHttpHandler;
import com.f1.ami.web.pages.AmiWebPages;
import com.f1.container.Container;
import com.f1.container.impl.BasicContainer;
import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.suite.web.HttpRequestAction;
import com.f1.suite.web.HttpStateCreator;
import com.f1.suite.web.WebState;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;

public class AmiWebSSOResponseHandler extends AbstractHttpHandler {
	private static final java.util.logging.Logger log = LH.get();
	private WebState state;
	private Container container;
	private AmiWebPortletManagerFactory portletManagerFactory;
	private AmiWebSSOPlugin plugin;
	private AmiWebEntitlementsPlugin entitlementsPlugin;
	private String userNameField;
	private String pluginDesc;
	private AmiAuthenticatorApplyEntitlements entitlements;

	public AmiWebSSOResponseHandler(HttpStateCreator sc, BasicContainer c, AmiWebPortletManagerFactory portletManagerFactory, AmiWebSSOPlugin plugin,
			AmiWebEntitlementsPlugin entitlementsPlugin, PropertyController props) {
		this.container = c;
		this.portletManagerFactory = portletManagerFactory;
		this.plugin = plugin;
		this.entitlementsPlugin = entitlementsPlugin;
		this.pluginDesc = plugin.getPluginId();
		this.entitlements = new AmiAuthenticatorApplyEntitlements(c.getTools(), c.getTools(), null);
	}
	@Override
	public void handle(HttpRequestResponse req) throws IOException {
		try {
			super.handle(req);
			final HttpSession session = req.getSession(true);
			final HttpRequestAction reqWrapper = container.nw(HttpRequestAction.class);
			reqWrapper.setRequest(req);
			AmiAuthUser response;
			boolean hadSemephore;
			try {
				response = plugin.processResponse(reqWrapper);
			} finally {
				hadSemephore = session.getAttributes().remove("AUTHSEMAPHORE") != null;
			}
			if (response == null) {
				if (hadSemephore) {
					req.getOutputStream().println(this.pluginDesc + " Authentication Failed.");
					req.getOutputStream().println("<a href='" + AmiWebPages.URL_HELLO + "'>Try again</a>");
				} else {
					req.sendRedirect(AmiWebPages.URL_HELLO);//This means was probably logged in on another window
				}
				return;
			}
			response = entitlements.process(response);
			if (response == null) {
				req.getOutputStream().print(this.pluginDesc + " Entitlements Failed.");
				return;
			}
			String userName = response.getUserName();
			if (userName == null) {
				req.getOutputStream().print("Critical Error Processing " + this.pluginDesc + " Response. Response does not have Username");
				LH.warning(log, "Error Processing Response, missing key '", userNameField, "', existing response Values are ", response);
				return;
			}
			if (this.entitlementsPlugin != null) {
				AmiAuthUser entitlementsResponse;
				try {
					entitlementsResponse = this.entitlementsPlugin.processEntitlements(response, reqWrapper);
				} catch (Exception e) {
					LH.warning(log, "Error obtaining entitlements for user ", response.getUserName(), " ", e);
					req.getOutputStream().print(this.entitlementsPlugin.getPluginId() + " Entitlements Failed.");
					return;
				}
				if (entitlementsResponse == null) {
					req.getOutputStream().print(this.entitlementsPlugin.getPluginId() + " Entitlements Failed.");
					return;
				}
				response = entitlementsResponse;
			}
			Map<String, Object> attributes = new HashMap<String, Object>();

			for (Entry<String, Object> i : response.getAuthAttributes().entrySet()) {
				attributes.put(i.getKey(), i.getValue());
			}
			AmiWebLoginHttpHandler.success(log, container.getServices(), req, userName, attributes, null);
		} catch (Exception e) {
			req.getOutputStream().print("Critical Error Processing " + this.pluginDesc + " Response.");
			LH.warning(log, "Error Processing Response ", e);
		}
		return;
	}

}
