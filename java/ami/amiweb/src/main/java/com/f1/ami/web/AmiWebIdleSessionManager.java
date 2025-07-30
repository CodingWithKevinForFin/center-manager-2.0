package com.f1.ami.web;

import java.util.Map;
import java.util.Set;

import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.RootPortletDialog;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Long;

public class AmiWebIdleSessionManager {
	public static byte DEV_IGNORE = 1;
	public static byte DEV_PROMPT = 2;
	public static byte DEV_LOGUT = 3;

	private long lastUserEventMillis;
	private long idleUserWarnAfterMillis = 5000;
	private long idleUserWarnForMillis = 10000;
	private byte idleUserWarnEvenIfNotLocked;
	private RootPortletDialog idleUserDialog;
	private final AmiWebService service;

	public AmiWebIdleSessionManager(AmiWebService service) {
		this.service = service;
	}

	public long getIdleUserWarnAfterMillis() {
		return this.idleUserWarnAfterMillis;
	}
	public void setIdleUserWarnAfterMillis(long t) {
		this.idleUserWarnAfterMillis = t;
	}
	public long getIdleUserWarnForMillis() {
		return this.idleUserWarnForMillis;
	}

	public void setIdleUserWarnForMillis(long t) {
		if (t > 0)
			lastUserEventMillis = service.getPortletManager().getNow();
		this.idleUserWarnForMillis = t;
	}

	public void clear() {
		this.idleUserWarnAfterMillis = 0;
		this.idleUserWarnForMillis = 0;
	}
	public void onFrontendCalled(Map<String, String> attributes) {
		if (this.idleUserWarnAfterMillis <= 0)
			return;
		if (!this.service.getDesktop().getIsLocked()) {
			String s = service.getVarsManager().getSetting(AmiWebConsts.USER_SETTING_LOGOUT);
			if ("Ignore".equals(s))
				return;
		}
		String type = attributes.get("type");
		PortletManager portletManager = this.service.getPortletManager();
		long now = portletManager.getNow();
		if (!NON_USER_ACTIONS.contains(type) || lastUserEventMillis == 0) {
			lastUserEventMillis = now;
			if (this.idleUserDialog != null) {
				this.idleUserDialog.close();
				this.idleUserDialog = null;
			}
		}
		long over = now - lastUserEventMillis - idleUserWarnAfterMillis;
		if (over > 0) {
			long warningRemaining = idleUserWarnForMillis - over;
			if (warningRemaining <= 0) {
				if (this.service.getDesktop().getIsLocked())
					portletManager.close();
				else {
					String s = service.getVarsManager().getSetting(AmiWebConsts.USER_SETTING_LOGOUT);
					if ("Debug".equals(s) || SH.isnt(s)) {
						if (this.idleUserDialog != null) {
							this.idleUserDialog.close();
							this.idleUserDialog = null;
						}

						this.idleUserDialog = portletManager.showDialog("Idle Session",
								new ConfirmDialogPortlet(portletManager.generateConfig(),
										"Would normally log you out, but you are a developer!<BR>(For options, See Account -> My Developer Settings -> On Automated Logout)",
										ConfirmDialog.TYPE_ALERT));
						this.lastUserEventMillis = now;
					} else
						portletManager.close();
				}

			} else {
				if (this.idleUserDialog == null) {
					this.idleUserDialog = portletManager.showDialog("Idle Session", new ConfirmDialogPortlet(portletManager.generateConfig(), "", ConfirmDialogPortlet.TYPE_ALERT));
					this.idleUserDialog.setBorderSize(20);
					this.idleUserDialog.setHeaderSize(0);
				}
				Portlet p = this.idleUserDialog.getPortlet();
				long seconds = (warningRemaining + 999) / 1000;
				((ConfirmDialogPortlet) p).setText(
						"<P><B>CLICK ANYWHERE TO AVOID BEING LOGGED OUT</B><P>(You will be automatically logged out in " + seconds + (seconds != 1 ? " seconds" : " second") + ")");
			}
		}
	}

	private static final Set<String> NON_USER_ACTIONS = CH.s("polling", "tableScroll", "clipzone", "userScroll");

	public Map<String, Object> getConfiguration() {
		if (this.idleUserWarnAfterMillis <= 0)
			return null;
		return CH.m("wait", this.idleUserWarnAfterMillis, "warn", this.idleUserWarnForMillis);
	}

	public void init(Map<String, Object> map) {
		if (CH.isEmpty(map)) {
			this.idleUserWarnAfterMillis = 0;
			this.idleUserWarnForMillis = 0;
		}
		this.idleUserWarnAfterMillis = CH.getOr(Caster_Long.INSTANCE, map, "wait", 0L);
		this.idleUserWarnForMillis = CH.getOr(Caster_Long.INSTANCE, map, "warn", 0L);
	}
}
