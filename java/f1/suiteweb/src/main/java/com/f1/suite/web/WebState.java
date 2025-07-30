/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web;

import java.util.Map;
import java.util.logging.Logger;

import com.f1.container.impl.BasicState;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.BasicPortletManager;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class WebState extends BasicState {

	private static final Logger log = LH.get();
	private String pgId;
	private WebStatesManager manager;
	private long lastAccessTime;
	private PortletManager portletManager;

	public WebState(WebStatesManager manager, String pgId) {
		this.pgId = pgId;
		this.manager = manager;
	}

	public WebStatesManager getWebStatesManager() {
		return this.manager;
	}

	public String getPgId() {
		return this.pgId;
	}

	public void setPortletManager(PortletManager pm) {
		OH.assertNull(this.portletManager);
		this.portletManager = pm;
	}
	public PortletManager getPortletManager() {
		return this.portletManager;
	}
	public String describeUser() {
		final WebUser user = manager == null ? null : manager.getUser();
		final String sessionId = getPartitionId().toString();
		final String userId = user == null ? "<no-user>" : user.getUserName();
		return userId + "::" + sessionId;
	}

	public void touch(long now) {
		WebStatesManager mgr = this.manager;
		if (mgr != null)
			mgr.getSession().touch(now);
		if (now < this.lastAccessTime)
			LH.info(log, "Ignoring old touch before last access time: ", now, " < ", lastAccessTime);
		else
			this.lastAccessTime = now;
	}

	public long getLastAccess() {
		return lastAccessTime;
	}

	public String getUserName() {
		if (this.manager == null)
			return null;
		if (this.manager.getUser() == null)
			return null;
		return this.manager.getUser().getUserName();
	}

	public Map<String, Object> getUserAttributes() {
		return this.manager.getUserAttributes();
	}
	public String getSessionId() {
		return this.manager == null ? null : (String) this.manager.getSession().getSessionId();
	}
	public void setWebStatesManager(WebStatesManager manager) {
		OH.assertNull(this.manager);
		if (this.manager != manager) {
			this.manager = manager;
			onManagerChanged();
		}
	}

	public void removeMeFromManager() {
		if (this.manager == null)
			return;
		this.manager.removeState(this.pgId);
		this.manager = null;
		onManagerChanged();
	};
	protected void onManagerChanged() {
	}

	public void reset() {
		if (this.portletManager != null) {
			((BasicPortletManager) this.portletManager).closeInner();
			this.portletManager = null;
		}
	}

	public void killWebState() {
		reset();
		if (this.getPartition() != null)
			this.getPartition().getContainer().getPartitionController().removePartition(this.getPartitionId());
		removeMeFromManager();
		invalidate();
	}

	@Override
	final public Class<? extends WebState> getType() {
		return WebState.class;
	}
}
