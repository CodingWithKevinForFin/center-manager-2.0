package com.f1.suite.web;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.container.ContainerTools;
import com.f1.http.HttpRequestResponse;
import com.f1.suite.web.portal.PortletBackend;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.BasicPortletManager;
import com.f1.suite.web.portal.impl.BlankPortlet;
import com.f1.suite.web.portal.impl.DesktopPortlet;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.HtmlCustomPortlet;
import com.f1.suite.web.portal.impl.PortletBuilderPortlet;
import com.f1.suite.web.portal.impl.PortletLayoutPortlet;
import com.f1.suite.web.portal.impl.ShortcutPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.TableFieldsTreePortlet;
import com.f1.suite.web.portal.impl.json.JsonTreePortlet;
import com.f1.utils.LH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.SH;

public class PortletManagerFactory implements UserLoginListener {

	private static final Logger log = LH.get();
	final private ContainerTools tools;
	private String buildVersion;

	public PortletManagerFactory(ContainerTools tools, LocaleFormatter f) {
		this(tools, null, f);
	}
	public PortletManagerFactory(ContainerTools tools, String version, LocaleFormatter formatter) {
		this.tools = tools;
		if (SH.is(version))
			this.buildVersion = version;
		else if (tools.getOptional("build.version") != null && tools.getOptional("build.branch") != null)
			this.buildVersion = (tools.getOptional("build.version", "") + "." + tools.getOptional("build.branch"));
		else
			this.buildVersion = null;
		this.formatter = formatter;
		init();
	}

	private PortletBackend portletBackend;
	final private LocaleFormatter formatter;

	public BasicPortletManager createPortletManager(HttpRequestResponse request, WebState state) {
		BasicPortletManager portletManager = new BasicPortletManager(request, state, this.tools.getServices().getGenerator(), portletBackend, buildVersion, this.formatter);
		LinkedHashMap<String, String> urlParams = BasicPortletManager.getUrlParams(request);
		try {
			portletManager.setCurrentAction(request);
			portletManager.setUrlParams(urlParams);
			applyBuilders(portletManager);
			applyServices(portletManager);
			applyDefaultLayout(portletManager, urlParams);
			return portletManager;
		} finally {
			portletManager.setCurrentAction(null);
		}
	}
	public void applyDefaultLayout(PortletManager portletManager, Map<String, String> urlParams) {
		String id = tools.getOptional("default.portletid", DesktopPortlet.Builder.ID);
		portletManager.getRoot().addChild(portletManager.buildPortlet(id));
	}

	protected void applyServices(PortletManager portletManager) {
	}

	protected void applyBuilders(PortletManager portletManager) {
		String path = "Layout";
		String epath = "Extras";
		String apath = epath + ".Advanced";
		portletManager.addPortletBuilder(new DividerPortlet.Builder().setPath(path));
		portletManager.addPortletBuilder("hdiv", new DividerPortlet.HBuilder().setPath(path));
		portletManager.addPortletBuilder("vdiv", new DividerPortlet.VBuilder().setPath(path));
		portletManager.addPortletBuilder(new DesktopPortlet.Builder().setPath(path));
		portletManager.addPortletBuilder(new TabPortlet.Builder().setPath(path));
		portletManager.addPortletBuilder(new BlankPortlet.Builder().setPath(apath));
		portletManager.addPortletBuilder(new PortletBuilderPortlet.Builder().setPath(apath));
		portletManager.addPortletBuilder(new PortletLayoutPortlet.Builder().setPath(apath));
		portletManager.addPortletBuilder(new JsonTreePortlet.Builder().setPath(apath));
		portletManager.addPortletBuilder(new TableFieldsTreePortlet.Builder().setPath(apath));
		portletManager.addPortletBuilder(new ShortcutPortlet.Builder().setPath(epath));
		portletManager.addPortletBuilder(new HtmlCustomPortlet.Builder().setPath(epath));
	}
	public PortletBackend getPortletBackend() {
		return portletBackend;
	}
	@Override
	public String onLoginSuccess(HttpRequestAction request, WebState state, boolean isRelogin) {
		PortletManager portletManager = (PortletManager) state.getPortletManager();
		if (portletManager == null) {
			try {
				portletManager = createPortletManager(request.getRequest(), state);
			} catch (Exception e) {
				String ticket = tools.generateErrorTicket();
				LH.warning(log, ticket, ": An Error occurred building portlet manager from session: ", state.describeUser(), e);
				state.killWebState();
				return null;
			}
		}
		return "/" + BasicPortletManager.URL_PORTAL;
	}
	public void setPortletBackend(PortletBackend portletBackend) {
		this.portletBackend = portletBackend;
	}
	public void init() {

	}
	public ContainerTools getTools() {
		return tools;
	}
}
