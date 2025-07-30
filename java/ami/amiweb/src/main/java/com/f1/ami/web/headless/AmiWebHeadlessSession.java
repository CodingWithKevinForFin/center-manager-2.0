package com.f1.ami.web.headless;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.f1.ami.web.AmiWebPortletManagerFactory;
import com.f1.ami.web.auth.AmiWebStatesManager;
import com.f1.container.Partition;
import com.f1.http.HttpSession;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.BasicPortletManager;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class AmiWebHeadlessSession implements Runnable {
	final private static Logger log = LH.get();

	final private String sessionName;
	final private String username;
	final private long startTime;
	final private int defaultWidth;
	final private int defaultHeight;
	final private Map<String, Object> attributes;
	final private AmiWebHeadlessManager manager;
	private String partitionId;
	private String pgId;
	private AmiWebHeadlessWebState webstate;

	public AmiWebHeadlessSession(AmiWebHeadlessManager manager, String sessionName, String username, int w, int h, Map<String, Object> map) {
		this.manager = manager;
		this.startTime = EH.currentTimeMillis();
		this.sessionName = sessionName;
		this.username = username;
		this.attributes = map;
		this.defaultWidth = w;
		this.defaultHeight = h;
	}

	public String getPartitionId() {
		return this.partitionId;
	}
	public String getSessionName() {
		return this.sessionName;
	}
	public String getUsername() {
		return this.username;
	}
	public PortletManager getPortletManager() {
		return this.webstate == null ? null : this.webstate.getPortletManager();
	}

	public HttpSession getHttpSession() {
		return this.webstate == null || this.webstate.getWebStatesManager() == null ? null : this.webstate.getWebStatesManager().getSession();
	}

	public String getPgid() {
		return this.pgId;
	}

	public AmiWebHeadlessWebState getWebState() {
		return this.webstate;
	}

	public Object getSessionId() {
		return this.webstate == null ? null : this.webstate.getSessionId();
	}

	public long getSessionStarttime() {
		return this.startTime;
	}

	@Override
	public void run() {
		StringBuilder buf = new StringBuilder();
		BasicPortletManager pm = (BasicPortletManager) webstate.getPortletManager();
		boolean needsInit = true;
		for (;;) {
			try {
				AmiWebHeadlessWebState ws = this.webstate;
				if (ws == null || !ws.isAlive() || ws.getPortletManager() == null)
					break;
				ws.touch(EH.currentTimeMillis());
				if (ws.getWebStatesManager() != null) {//Someone is logged in
					needsInit = true;
				} else {
					Partition partition = ws.getPartition();
					if (!partition.lockForWrite(60, TimeUnit.SECONDS)) {
						LH.warning(log, "Could not get headless lock for session: " + this.sessionName);
					} else {
						try {
							if (needsInit) {
								LH.info(log, "Starting dummy browser of resolution ", this.defaultWidth, "x", this.defaultHeight, " for: " + this.sessionName);
								dummyHttpRequest(pm, (Map) CH.m("type", "init", "webWindowId", 0, "width", this.defaultWidth, "height", this.defaultHeight), buf);
								dummyHttpRequest(pm, (Map) CH.m("type", "postInit", "webWindowId", 0, BasicPortletManager.PAGEID, this.getPgid(), "pageUid", pm.getPageUid()), buf);
								needsInit = false;
							}
							dummyHttpRequest(pm, (Map) CH.m("seqnum", pm.getCurrentSeqNum(), "type", "polling", "pageUid", pm.getPageUid()), buf);
						} finally {
							partition.unlockForWrite();
						}
					}
				}
			} catch (Throwable t) {
				LH.warning(log, "Error with headless session " + this.sessionName, t);
			}
			OH.sleep(1000);
		}
	}

	private void dummyHttpRequest(BasicPortletManager pm, Map values, StringBuilder buf) {
		try {
			buf.setLength(0);
			pm.handleCallback(values, null);
			pm.drainPendingJs(buf);
		} catch (Exception e) {
			LH.warning(log, "Error with handle callback for ", sessionName, ": ", e);
		}
	}

	public void stop() {
		if (isAlive()) {
			getWebState().forceKillHeadlessState();
			this.partitionId = null;
			this.webstate = null;
			this.pgId = null;
		}
	}

	public boolean isAlive() {
		AmiWebHeadlessWebState ws = this.webstate;
		return ws != null && ws.isAlive();
	}

	public void start() {
		OH.assertNull(this.webstate);
		AmiWebPortletManagerFactory pmf = manager.getPortletManagerFactory();
		boolean isAdmin = AmiWebStatesManager.isAdmin(attributes, pmf.getTools());
		boolean isDev = AmiWebStatesManager.isDev(attributes, pmf.getTools());

		String pgid = pmf.getCreator().nextPgId();
		AmiWebHeadlessWebState ws = new AmiWebHeadlessWebState(this, pgid, sessionName, username, attributes, isDev, isAdmin, 1);
		Partition partition = pmf.getTools().getContainer().getPartitionController().getOrCreatePartition(pgid);
		partition.putState(ws);
		if (!partition.lockForWrite(60, TimeUnit.SECONDS))
			throw new RuntimeException("Could not get lock");
		try {
			pmf.createPortletManager(null, ws);
		} finally {
			partition.unlockForWrite();
		}
		this.webstate = ws;
		this.partitionId = (String) this.webstate.getPartitionId();
		this.pgId = this.webstate.getPgId();
		Thread thread = new Thread(this, "HEADLESS-" + sessionName);
		thread.setDaemon(true);
		thread.start();
	}

	public int getDefaultWidth() {
		return this.defaultWidth;
	}
	public int getDefaultHeight() {
		return this.defaultHeight;
	}

	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	public AmiWebHeadlessManager getManager() {
		return this.manager;
	}

}
