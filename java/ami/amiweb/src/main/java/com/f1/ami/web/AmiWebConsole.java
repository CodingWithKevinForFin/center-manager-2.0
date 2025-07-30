package com.f1.ami.web;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amiscript.AmiDebugManager;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.web.auth.AmiWebState;
import com.f1.ami.web.auth.AmiWebStatesManager;
import com.f1.ami.web.headless.AmiWebHeadlessManager;
import com.f1.ami.web.headless.AmiWebHeadlessSession;
import com.f1.ami.web.headless.AmiWebHeadlessWebState;
import com.f1.base.Console;
import com.f1.base.Table;
import com.f1.container.Partition;
import com.f1.container.PartitionController;
import com.f1.container.impl.dispatching.RootPartitionActionRunner;
import com.f1.container.impl.dispatching.RootPartitionStats;
import com.f1.http.HttpServer;
import com.f1.http.HttpSession;
import com.f1.suite.web.WebState;
import com.f1.suite.web.WebStatesManager;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletContainer;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.DesktopPortlet.Window;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;

@Console(name = "AmiWebServer", help = "Used to diagnose/control user sessions")
public class AmiWebConsole {

	final private HttpServer server;
	final private PartitionController pc;

	public AmiWebConsole(HttpServer server, PartitionController pc) {
		this.server = server;
		this.pc = pc;
	}
	@Console(help = "show all logins")
	public String showLogins() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
		String timeZone = "GMT";
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		Collection<HttpSession> sessions = server.getHttpSessionManager().getSessions();
		Table t = new BasicTable(String.class, "__USERNAME", String.class, "__LOGINID", String.class, "__LOGINTIME(" + timeZone + ")", Integer.class, "Sessions");
		for (HttpSession session : sessions) {
			WebStatesManager wsm = WebStatesManager.get(session);
			t.getRows().addRow(wsm == null ? null : wsm.getUserName(), session.getSessionId(), wsm == null || wsm.getLoginTime() == 0 ? null : format(df, wsm.getLoginTime()),
					wsm == null ? 0 : wsm.getPgIds().size());
		}
		return TableHelper.toString(t, "", TableHelper.SHOW_ALL_BUT_TYPES);
	}

	@Console(help = "show all sessions")
	public String showSessions() {
		return showSessions(null);
	}
	@Console(help = "show all sessions", params = { "__LOGINID" })
	public String showSessions(String loginId) {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
		String timeZone = "GMT";
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		Table t = new BasicTable(String.class, "Type", String.class, "__USERNAME", String.class, "__LOGINID", String.class, "__SESSIONID", String.class, "__ADDRESS", String.class,
				"__NAME", String.class, "Layout", String.class, "Label", String.class, "__LOADTIME(" + timeZone + ")", String.class, "LastAccessTime(" + timeZone + ")",
				Boolean.class, "Status");
		Collection<HttpSession> sessions = server.getHttpSessionManager().getSessions();
		RootPartitionStats stats = new RootPartitionStats();
		AmiWebHeadlessManager hm = pc.getServices().getService(AmiWebHeadlessManager.SERVICE_ID, AmiWebHeadlessManager.class);
		Set<String> headless = new HashSet<String>();
		for (String s : hm.getSessionNames()) {
			AmiWebHeadlessSession hs = hm.getSessionByName(s);
			if (loginId != null && OH.ne(hs.getSessionId(), loginId))
				continue;
			headless.add(hs.getPgid());
			AmiWebHeadlessWebState webState = hs.getWebState();
			if (webState == null) {
				t.getRows().addRow("HEADLESS", hs.getUsername(), hs.getSessionId(), null, null, hs.getSessionName(), null, null, format(df, hs.getSessionStarttime()), null,
						"DISABLED");
			} else {
				WebStatesManager wm = webState.getWebStatesManager();
				String host = wm == null ? null : wm.getRemoteAddress();
				String layout = webState.getLayout();
				String label = webState.getLabel();
				t.getRows().addRow("HEADLESS", hs.getUsername(), hs.getSessionId(), hs.getPgid(), host, hs.getSessionName(), layout, label, format(df, hs.getSessionStarttime()),
						format(df, webState.getLastAccess()), getStatus(webState));
			}

		}
		for (HttpSession session : sessions) {
			if (loginId != null && OH.ne(session.getSessionId(), loginId))
				continue;
			AmiWebStatesManager wsm = (AmiWebStatesManager) WebStatesManager.get(session);
			if (wsm != null) {
				for (String pgid : wsm.getPgIds()) {
					if (headless.contains(pgid))
						continue;
					AmiWebState w = (AmiWebState) wsm.getState(pgid);
					if (w != null) {
						RootPartitionActionRunner rpr = pc.getContainer().getDispatchController().getRootPartitionRunner((String) session.getSessionId());
						if (rpr != null)
							rpr.getStats(stats);
						else {
							stats.setQueueRuns(0);
							stats.setActionsProcessed(0);
							stats.setTimeSpentMs(-1);
							stats.setInThreadPool(false);
							stats.setActionsAdded(0);
						}
						PortletManager pm = w.getPortletManager();
						if (pm != null) {
							AmiWebService service = (AmiWebService) pm.getServiceNoThrow(AmiWebService.ID);
							if (service != null) {
								String layout = w.getLayout();
								String label = w.getLabel();
								String host = w.getWebStatesManager().getRemoteAddress();
								AmiWebVarsManager vm = service.getVarsManager();
								layout = SH.afterLast(layout, "/");
								t.getRows().addRow("USER", session.getDescription(), session.getSessionId(), pgid, host, w.getName(), layout, label,
										format(df, vm.getSessionStarttime()), format(df, w.getLastAccess()), getStatus(w));
							}
						}
					}
				}
			}
		}
		return TableHelper.toString(t, "", TableHelper.SHOW_ALL_BUT_TYPES);
	}
	private String getStatus(WebState w) {

		return w == null || !w.isAlive() ? "DISABLED" : (w.getPartition().isWriteLocked() ? "WORKING" : "ENABLED");
	}

	@Console(help = "kill user session", params = { "__SESSIONID" })
	public String killSession(String sessionId) {
		Partition partition = pc.getPartition(sessionId);
		if (partition == null)
			return "Session not found";
		WebState state = (WebState) partition.getState(WebState.class);
		if (state == null)
			return "Session State not found";
		if (state instanceof AmiWebHeadlessWebState)
			return "Use disableHeadlessSession(...) to kill a headless session";
		state.killWebState();
		return "killed";

	}
	@Console(help = "disablestop headless session", params = { "headlessSessionName" })
	public String disableHeadlessSession(String name) {
		AmiWebHeadlessManager hm = pc.getServices().getService(AmiWebHeadlessManager.SERVICE_ID, AmiWebHeadlessManager.class);
		AmiWebHeadlessSession session = hm.getSessionByName(name);
		if (session == null)
			return "Session not found";
		if (session.getWebState() == null)
			return "Session already disabled";
		if (!session.getWebState().isAlive())
			return "Session already killed";

		synchronized (hm) {
			session.stop();
			hm.writeSessionsToDisk();
		}
		return "headless session disabled";

	}
	@Console(help = "Create headless session, resolution is in format widhtXheight, ex 1000x2000, attributes ia comma delimited list ", params = { "headlessSessionName",
			"__USERANME", "RESOLUTION", "ATTRIBUTES" })
	public String createHeadlessSession(String sessionName, String username, String resolution, String attributes) {
		if (SH.isnt(sessionName))
			return "name required";
		if (SH.isnt(username))
			return "user required";
		AmiWebHeadlessManager hm = pc.getServices().getService(AmiWebHeadlessManager.SERVICE_ID, AmiWebHeadlessManager.class);
		synchronized (hm) {
			if (hm.getSessionByName(sessionName) != null)
				return "Session already exists";
			Tuple2<Integer, Integer> res = AmiWebHeadlessManager.parseResolution(resolution);
			Map<String, Object> atr = AmiWebHeadlessManager.parseAttributes(attributes);
			AmiWebHeadlessSession hs = new AmiWebHeadlessSession(hm, sessionName, username, (int) res.getA(), (int) res.getB(), atr);
			hs.start();
			hm.addSession(hs);
			hm.writeSessionsToDisk();
		}
		return "headless session created and enabled";
	}

	@Console(help = "Delete a headless session", params = { "headlessSessionName" })
	public String deleteHeadlessSession(String sessionName) {
		AmiWebHeadlessManager hm = pc.getServices().getService(AmiWebHeadlessManager.SERVICE_ID, AmiWebHeadlessManager.class);
		AmiWebHeadlessSession session = hm.getSessionByName(sessionName);
		synchronized (hm) {
			if (session == null)
				return "Session not found";
			if (session.isAlive())
				return "session still ALIVE, run disableHeadlessSession(...) first";
			hm.removeSession(session);
			hm.writeSessionsToDisk();
		}
		return "headless session deleted";
	}

	@Console(help = "Enable a headless session", params = { "headlessSessionName" })
	public String enableHeadlessSession(String sessionName) {
		AmiWebHeadlessManager hm = pc.getServices().getService(AmiWebHeadlessManager.SERVICE_ID, AmiWebHeadlessManager.class);
		AmiWebHeadlessSession session = hm.getSessionByName(sessionName);
		synchronized (hm) {
			if (session == null)
				return "Headless Session not found";
			if (session.isAlive())
				return "Headless session already enabled";
			session.start();
			hm.writeSessionsToDisk();
		}
		return "headless session enabled";
	}
	@Console(help = "print the headless session details as it is saved in headless.txt", params = { "headlessSessionName" })
	public String describeHeadlessSession(String sessionName) {
		AmiWebHeadlessManager hm = pc.getServices().getService(AmiWebHeadlessManager.SERVICE_ID, AmiWebHeadlessManager.class);
		AmiWebHeadlessSession session = hm.getSessionByName(sessionName);
		if (session == null)
			return "Session not found";
		StringBuilder sink = new StringBuilder();
		AmiWebHeadlessManager.toLine(session, sink);
		return sink.toString();
	}

	@Console(help = "kill user login", params = { "__LOGINID" })
	public String killLogin(String uid) {
		HttpSession session = server.getHttpSessionManager().getHttpSession(uid);
		if (session == null)
			return "Login not found";
		else
			session.kill();
		return "killed user login";
	}

	@Console(help = "show all panels", params = { "__SESSIONID" })
	public String showPanels(String sessionId) {
		Partition partition = pc.getPartition(sessionId);
		if (partition == null)
			return "Session not found";
		WebState state = (WebState) partition.getState(WebState.class);
		if (state == null)
			return "Session State not found";
		PortletManager pm = state.getPortletManager();
		if (pm == null)
			return "Session portlet manager not found";
		if (!partition.lockForWrite(10, TimeUnit.SECONDS)) {
			return "Timeout, could not aquire lock";
		}
		try {
			Table t = new BasicTable(String.class, "Structure", String.class, "Type", String.class, "PanelId", Boolean.class, "Visible", Integer.class, "Width", Integer.class,
					"Height", Boolean.class, "Transient");
			RootPortlet rootPortlet = (RootPortlet) pm.getRoot();
			t.getRows().addRow("Desktop - " + rootPortlet.getTitle(), "Main Window", null, rootPortlet.isWindowVisible(), rootPortlet.getWidth(), rootPortlet.getHeight(), null);
			AmiWebDesktopPortlet desktop = (AmiWebDesktopPortlet) rootPortlet.getContent();
			AmiWebInnerDesktopPortlet desktop2 = desktop.getInnerDesktop();
			t.getRows().addRow(" Inner Desktop", "Inner Desktop", null, desktop2.getVisible(), desktop2.getWidth(), desktop2.getHeight(), null);
			for (Window window : desktop2.getWindows())
				if (!window.isPoppedOut())
					addPanels(t, "Window - " + window.getName(), 2, window.getPortlet());
			for (Window window : desktop2.getWindows())
				if (window.isPoppedOut()) {
					RootPortlet rp = window.getRootPortlet();
					t.getRows().addRow("BrowserWindow - " + rp.getTitle(), "Popout Window", null, rp.isWindowVisible(), rp.getWidth(), rp.getHeight(), null);
					addPanels(t, "Inner", 1, window.getPortlet());
				}
			return TableHelper.toString(t, "", TableHelper.SHOW_ALL_BUT_TYPES);
		} finally {
			partition.unlockForWrite();
		}
	}

	private void addPanels(Table t, String structure, int indent, Portlet p) {
		if (p instanceof AmiWebAliasPortlet) {
			AmiWebAliasPortlet awp = (AmiWebAliasPortlet) p;
			String amiPanelId = awp.getAmiPanelId();
			t.getRows().addRow(SH.repeat(' ', indent) + structure, awp.getConfigMenuTitle(), amiPanelId, awp.getVisible(), awp.getWidth(), awp.getHeight(), awp.isTransient());
			if (p instanceof AmiWebTabPortlet) {
				AmiWebTabPortlet p2 = (AmiWebTabPortlet) p;
				for (Tab tab : p2.getInnerContainer().getTabs())
					addPanels(t, "Tab - " + tab.getTitle(), indent + 1, tab.getPortlet());
			} else if (p instanceof AmiWebScrollPortlet) {
				AmiWebScrollPortlet p2 = (AmiWebScrollPortlet) p;
				addPanels(t, "Inner", indent + 1, p2.getInnerPortlet());
			} else if (p instanceof AmiWebDividerPortlet) {
				AmiWebDividerPortlet p2 = (AmiWebDividerPortlet) p;
				addPanels(t, p2.isVertical() ? "Left" : "Top", indent + 1, p2.getInnerContainer().getFirstChild());
				addPanels(t, p2.isVertical() ? "Right" : "Bottom", indent + 1, p2.getInnerContainer().getSecondChild());
			} else if (p instanceof AmiWebAbstractContainerPortlet) {
				AmiWebAbstractContainerPortlet p2 = (AmiWebAbstractContainerPortlet) p;
				for (Portlet i : p2.getChildren().values())
					addPanels(t, i.getPortletId(), indent + 1, i);
			}
		} else if (p instanceof PortletContainer) {
			PortletContainer p2 = (PortletContainer) p;
			for (Portlet i : p2.getChildren().values())
				addPanels(t, structure, indent, i);
		}
	}

	private String format(SimpleDateFormat df, long startTime) {
		if (startTime == 0)
			return null;
		return df.format(new Date(startTime));
	}
	@Console(help = "execute amiscript ", params = { "__SESSIONID_or_HEADLESSNAME" })
	public String exec(String sessionId, String script) {
		Partition partition = pc.getPartition(sessionId);
		WebState state;
		if (partition == null) {
			AmiWebHeadlessManager hm = pc.getServices().getService(AmiWebHeadlessManager.SERVICE_ID, AmiWebHeadlessManager.class);
			AmiWebHeadlessSession session = hm.getSessionByName(sessionId);
			if (session == null)
				return "Session not found";
			state = session.getWebState();
			partition = state.getPartition();
		} else
			state = (WebState) partition.getState(WebState.class);
		if (state == null)
			return "State not found";
		PortletManager pm = state.getPortletManager();
		if (pm == null)
			return "Portlet manager not found";
		if (!partition.lockForWrite(10, TimeUnit.SECONDS)) {
			return "Timeout, could not aquire lock";
		}
		try {
			com.f1.utils.structs.table.stack.BasicCalcTypes types = new com.f1.utils.structs.table.stack.BasicCalcTypes();
			types.putType("this", AmiWebService.class);
			AmiWebService service = (AmiWebService) pm.getService(AmiWebService.ID);
			AmiWebScriptManagerForLayout layout = service.getScriptManager().getLayout("");
			DerivedCellCalculator dcc = layout.toCalc(script, types, service, null);
			StringBuilder errorSink = new StringBuilder();
			AmiDebugManager dm = service.getDebugManager();
			Object o = layout.executeAmiScript(script, errorSink, dcc, EmptyCalcFrame.INSTANCE, dm, AmiDebugMessage.TYPE_ADMIN_CONSOLE, service, "onAdminConsole");
			return AmiUtils.s(o);
		} finally {
			partition.unlockForWrite();
		}
	}

	@Console(help = "change the web activity log level ", params = { "__SESSIONID_or_HEADLESSNAME", "off/on/verbose" })
	public String changeActivityLogLevel(String sessionId, String newLevel) {
		byte level;
		switch (newLevel) {
			case "off":
				level = 0;
				break;
			case "on":
				level = 1;
				break;
			case "verbose":
				level = 2;
				break;
			default:
				return "invalid level, must be off/on/verbose";
		}
		Partition partition = pc.getPartition(sessionId);
		WebState state;
		if (partition == null) {
			AmiWebHeadlessManager hm = pc.getServices().getService(AmiWebHeadlessManager.SERVICE_ID, AmiWebHeadlessManager.class);
			AmiWebHeadlessSession session = hm.getSessionByName(sessionId);
			if (session == null)
				return "Session not found";
			state = session.getWebState();
			partition = state.getPartition();
		} else
			state = (WebState) partition.getState(WebState.class);
		if (state == null)
			return "State not found";
		PortletManager pm = state.getPortletManager();
		if (pm == null)
			return "Portlet manager not found";
		if (!partition.lockForWrite(10, TimeUnit.SECONDS)) {
			return "Timeout, could not aquire lock";
		}
		try {
			AmiWebService service = (AmiWebService) pm.getService(AmiWebService.ID);
			if (service != null)
				service.setActivityLogLevel(level);
		} finally {
			partition.unlockForWrite();
		}
		return "New Log level: " + newLevel;
	}
}
