package com.f1.suite.web.portal.impl;

import java.util.Map;

import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletContainer;
import com.f1.suite.web.portal.impl.DesktopPortlet.Window;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;

public class TabPlaceholderPortlet extends HtmlPortlet implements HtmlPortletListener {

	final private Window tearoutwindow;
	private boolean poppedIn;
	private boolean fullyDockOnClose;
	final private Tab tab;
	final private Portlet portlet;

	public TabPlaceholderPortlet(Tab tab, DesktopPortlet desktop, boolean isPopoutEnabled, int x, int y) {
		super(desktop.getManager().generateConfig());
		setHtml("<div onclick='" + super.generateCallback("popin")
				+ "' style='background:#EFC675;color:#000000;width:100%;height:100%;text-align:center;cursor:pointer'><P>This tab is currently undocked. <BR>Click here to redock</div>");
		this.addListener(this);
		this.portlet = tab.getPortlet();
		this.tab = tab;
		this.tab.getTabPortlet().replaceChild(tab.getPortlet().getPortletId(), (TabPlaceholderPortlet) this);
		this.tearoutwindow = desktop.addChild(tab.getTitle() + " [undocked]", this.portlet, DesktopPortlet.Window.DEFAULT_FLAGS);
		Window w = this.getTearoutWindow();
		desktop.addAmiWebTabPlaceholderPortlet(this);
		w.setAllowTitleEdit(false, true);
		w.setCloseable(true, true);
		w.addOption(DesktopPortlet.OPTION_STYLE_BUTTON_CLOSE, "_bgi=url('rsc/window_redock_btn2.gif')");
		w.setAllowPop(isPopoutEnabled, true);
		this.setPosition(x, y);
		getManager().onPortletAdded(this);
	}
	public void setPosition(int x, int y) {
		this.getTearoutWindow().setPosition(Math.max(x - 40, 4), Math.max(y - 40, 20), portlet.getWidth(), portlet.getHeight());
	}
	public void undock() {
		this.onPopoutWindow();
	}
	public void popout() {
		this.setFullyDockOnClose(true);
		this.getTearoutWindow().popoutWindow();
		this.onPopoutWindow();
	}
	public void popout(int x, int y, int w, int h) {
		this.setFullyDockOnClose(true);
		this.getTearoutWindow().popoutWindow(x, y, w, h);
		this.onPopoutWindow();
	}
	public void popoutDirectly() {
		this.tab.getTabPortlet().replaceChild(tab.getPortlet().getPortletId(), (TabPlaceholderPortlet) this);
		this.setFullyDockOnClose(true);
		this.getTearoutWindow().popoutWindow();
		this.onPopoutWindow();
	}
	public void popoutDirectly(int x, int y, int w, int h) {
		this.tab.getTabPortlet().replaceChild(tab.getPortlet().getPortletId(), (TabPlaceholderPortlet) this);
		this.setFullyDockOnClose(true);
		this.getTearoutWindow().popoutWindow(x, y, w, h);
		this.onPopoutWindow();
	}

	@Override
	public void onUserClick(HtmlPortlet portlet) {
	}

	@Override
	public void onUserCallback(HtmlPortlet htmlPortlet, String id, int mouseX, int mouseY, HtmlPortlet.Callback attributes) {
		if ("popin".equals(id)) {
			popin();
		}

	}

	public void popin() {
		this.poppedIn = true;
		if (getTearoutWindow().isPoppedOut()) {
			Portlet p = getTearoutWindow().getPortletForPopout();
			p.getParent().removeChild(p.getPortletId());
			getParent().replaceChild(this.getPortletId(), p);
			getTearoutWindow().closePopup();
			this.tab.getTabPortlet().setActiveTab(p);
		} else {
			Portlet tearoutPortlet = getTearoutPortlet();
			PortletContainer p = tearoutPortlet.getParent();
			if (p != null)
				p.removeChild(tearoutwindow.getPortletId());
			TabPortlet tp = (TabPortlet) getParent();
			tp.replaceChild(this.getPortletId(), tearoutPortlet);
			this.tab.getTabPortlet().setActiveTab(tearoutPortlet);
		}
		close();
		this.tearoutwindow.getDesktop().onPlaceHolderClosed(this);
		this.onPopinWindow();
	}

	@Override
	public void onClosed() {
		super.onClosed();
		if (!this.poppedIn) {
			if (getTearoutPortlet().getParent() instanceof RootPortlet) {
				getTearoutPortlet().getParent().getParent().removeChild(getTearoutPortlet().getParent().getPortletId());
			}
			this.tearoutwindow.getPortlet().close();
			this.tearoutwindow.getDesktop().onPlaceHolderClosed(this);
		}
	}

	public Portlet getTearoutPortlet() {
		return tearoutwindow.getPortlet();
	}

	public boolean isPoppedIn() {
		return this.poppedIn;
	}

	@Override
	public void onHtmlChanged(String old, String nuw) {
	}

	public Window getTearoutWindow() {
		return tearoutwindow;
	}

	public void setFullyDockOnClose(boolean b) {
		this.fullyDockOnClose = b;
	}
	public boolean getFullyDockOnClose() {
		return this.fullyDockOnClose;
	}

	public void onPopoutWindow() {
		if (this.tab.getTabPortlet().getTabPortletStyle().getHideTabWhenPoppedOut() == true) {
			this.tab.setHidden(true);
			if (this.tab == this.tab.getTabPortlet().getSelectedTab())
				this.setNextVisibleTabActive();
		}
	}
	public void onPopinWindow() {
		if (this.tab.getTabPortlet().getTabPortletStyle().getHideTabWhenPoppedOut() == true)
			this.tab.setHidden(false);
	}

	@Override
	public Map<String, Object> getConfiguration() {
		return super.getConfiguration();
	}
	private void setNextVisibleTabActive() {
		TabPortlet tp = this.tab.getTabPortlet();
		if (tp.getTabsCount() == 1)
			return;
		Tab nextTab = null;
		int loc = this.tab.getLocation();
		for (Tab t : tp.getTabs()) {
			if (nextTab == null && t.isHidden() == false) {
				nextTab = t;
				if (t.getLocation() > loc)
					break;
				continue;
			}
			if (t.getLocation() <= loc || t.isHidden() == true) {
				continue;
			} else {
				nextTab = t;
				break;
			}
		}
		if (nextTab != null)
			tp.setActiveTab(nextTab.getPortlet());

	}
	public Tab getTab() {
		return this.tab;
	}

}
