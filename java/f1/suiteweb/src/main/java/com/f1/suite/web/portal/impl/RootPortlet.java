/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.portal.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.suite.web.JsFunction;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuLink;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.peripheral.MouseEvent;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.suite.web.portal.style.PortletStyleManager_Dialog;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.WebRectangle;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.json.JsonBuilder;

public class RootPortlet extends AbstractPortletContainer {

	private static final Logger log = LH.get();
	public static final int DIALOG_AUTO_WIDTH_HEIGHT = -1;
	public static final PortletSchema<RootPortlet> SCHEMA = new BasicPortletSchema<RootPortlet>("Root", "RootPortlet", RootPortlet.class, true, true);
	final private Map<String, RootPortletDialog> dialogs = new HashMap<String, RootPortletDialog>();
	private Portlet content;
	private boolean isFullscreen;
	final private long windowId;
	private boolean needsInit;
	private List<RootPortletListener> listeners = new ArrayList<RootPortletListener>();

	//This indicates if the windows is physically visible to the user... While the Portlet::isVisible really indicates if the contents have been pushed to the browser.
	private boolean isVisible = true;
	private int screenX;
	private int screenY;
	private boolean locationSet = false;
	private int forceFocusMeAfterMs;
	private double heightScaleRatio = 0.8;
	private double widthScaleRatio = 0.8;
	private int widthScalePx = 100;
	private int HeightScalePx = 100;
	private double browserZoom = 1d;

	public RootPortlet(PortletConfig manager, long windowId) {
		super(manager);
		this.forceFocusMeAfterMs = this.getManager().getTools().getOptional("f1.webkit.force.popup.to.front.by.page.refresh.in.millis", -1);
		this.windowId = windowId;
		this.setTitle(getManager().getDefaultBrowserTitle());
		this.setSize(640, 480);
	}

	public Collection<RootPortletDialog> getDialogs() {
		return dialogs.values();
	}

	@Override
	public void addChild(Portlet child) {
		if (content != null)
			throw new RuntimeException("Root portlet can only have one child. To add a dialog use addDialog(...)");
		setContent(child);
		super.addChild(child);
		makeChildrenVisible(getVisible());
		if (getVisible()) {
			layoutChildren();
		}
	}

	@Override
	public Portlet removeChild(String portletId) {
		Portlet r = super.removeChild(portletId);
		r.setVisible(false);
		if (r == content) {
			setContent(null);
			if (this.isPopupWindow() && this.isVisible) {
				closeThisPopup();
			}
		} else {
			RootPortletDialog dialog = dialogs.remove(portletId);
			if (dialog != null)
				dialog.fireOnClosed();

		}
		if (r.getVisible())
			callJsRemoveChild(r);
		return r;
	}

	private void setContent(Portlet content) {
		if (this.content == content)
			return;
		Portlet old = this.content;
		this.content = content;
		fireOnContentChanged(old, this.content);

	}

	@Override
	public void replaceChild(String removed, Portlet replacement) {
		Portlet r = super.removeChild(removed);
		r.setVisible(false);
		if (r == content) {
			setContent(null);
		} else {
			RootPortletDialog dialog = dialogs.remove(removed);
			if (dialog != null)
				dialog.fireOnClosed();
		}
		if (r.getVisible())
			callJsRemoveChild(r);
		addChild(replacement);
	}

	@Override
	protected void layoutChildren() {
		if (content != null) {
			content.setSize(getWidth(), getHeight());
			makeChildVisible(content, true);
		}
	}

	@Override
	public void drainJavascript() {
		if (needsInit) {
			needsInit = false;
			callJsFunction("init").addParam(windowId).end();
			if (!isPopupWindow())
				callJsFunction("setBrowserTitle").addParamQuoted(getTitle()).end();
		}
		if (super.getVisible())
			for (RootPortletDialog window : dialogs.values()) {
				if (window.visible) {
					makeChildVisible(window.getPortlet(), true);
					JsFunction js = callJsFunction("setDialogLocation").addParamQuoted(window.portlet.getPortletId()).addParam(window.getLeft()).addParam(window.getTop())
							.addParam(window.portlet.getWidth()).addParam(window.portlet.getHeight()).addParam(false).addParam(window.getZindex())
							.addParam(window.getHasCloseButton()).addParam(window.getShadeOutside()).addParamQuoted(window.getStylePrefix()).addParam(window.getHeaderSize())
							.addParam(window.getBorderSize()).addParam(!window.isCloseOnClickOutside()).addParam(window.getIsModal());
					js.startJson().addAutotype(window.getOptions(), false);
					js.end();
				}
			}
		if (moveTo != null && locationSet) {
			callJsFunction("resizeTo").addParam(moveTo.getLeft()).addParam(moveTo.getTop()).addParam(moveTo.getWidth()).addParam(moveTo.getHeight()).end();
			moveTo = null;
		}
		super.drainJavascript();
	}
	@Override
	protected void initJs() {
		closeMenuListener();
		super.initJs();
		needsInit = true;
		flagPendingAjax();
	}

	private void closeMenuListener() {
		this.getManager().getMenuManager().resetIds();
		if (this.menuListener != null)
			this.menuListener.onMenuDismissed();
		this.menuListener = null;

	}

	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		boolean isVisible = true;
		if ("location".equals(callback)) {
			this.locationSet = true;
			int width = CH.getOrThrow(Caster_Integer.INSTANCE, attributes, "width");
			int height = CH.getOrThrow(Caster_Integer.INSTANCE, attributes, "height");
			this.screenX = CH.getOrThrow(Caster_Integer.INSTANCE, attributes, "screenX");
			this.screenY = CH.getOrThrow(Caster_Integer.INSTANCE, attributes, "screenY");
			this.browserZoom = CH.getOrThrow(Caster_Double.INSTANCE, attributes, "zoom");
			this.isFullscreen = CH.getOr(Caster_Boolean.INSTANCE, attributes, "fullscreen", Boolean.FALSE);
			setSize(width, height);
			flagPendingAjax();
		} else if ("visibility".equals(callback)) {
			isVisible = CH.getOrThrow(Caster_Boolean.INSTANCE, attributes, "visible");
		} else if ("dialogOutsideClicked".equals(callback)) {
			String childId = CH.getOrThrow(Caster_String.INSTANCE, attributes, "childId");
			RootPortletDialog dialog = dialogs.get(childId);
			if (dialog != null) {
				dialog.fireOnClickedOutside();
				if (dialog.isCloseOnClickOutside())
					dialog.close();
			} else
				LH.info(log, "Dialog not found for closing: ", childId);
		} else if ("dialogSized".equals(callback)) {
			int left = CH.getOrThrow(Caster_Integer.INSTANCE, attributes, "left");
			int top = CH.getOrThrow(Caster_Integer.INSTANCE, attributes, "top");
			int width = CH.getOrThrow(Caster_Integer.INSTANCE, attributes, "width");
			int height = CH.getOrThrow(Caster_Integer.INSTANCE, attributes, "height");
			String childId = CH.getOrThrow(Caster_String.INSTANCE, attributes, "childId");
			RootPortletDialog dialog = dialogs.get(childId);
			if (dialog != null) {
				// ensure visible when overflows.
				if (left < 0)
					left = 1;
				if (top < 65)
					top = 65;
				if (getWidth() > 100 && left > getWidth() - 100)
					left = getWidth() - 100;
				if (getHeight() > 50 && top > getHeight() - 50)
					top = getHeight() - 50;
				dialog.setPosition(left, top);
				dialog.portlet.setSize(width, height);
				callJsFunction("onUpdated").end();
				layoutChildren();
				flagPendingAjax();
			}
		} else if ("dialogClosed".equals(callback)) {
			String childId = CH.getOrThrow(Caster_String.INSTANCE, attributes, "childId");
			RootPortletDialog dialog = this.dialogs.get(childId);
			if (dialog != null)
				dialog.fireOnUserClosed();
			closeDialog(childId);
			closeMenuListener();
		} else if ("menudismissed".equals(callback)) {
			closeMenuListener();
		} else if ("menuitem".equals(callback)) {
			final WebMenuLink id = getManager().getMenuManager().fireLinkForId(CH.getOrThrow(attributes, "id"));
			if (id != null && this.menuListener != null)
				this.menuListener.onMenuItem(id.getAction());
			this.menuListener = null;
		} else {
			super.handleCallback(callback, attributes);
			return;
		}
		this.isVisible = isVisible;
	}

	public Portlet closeDialog(String childId) {
		Portlet r = getChild(childId);
		if (r != null)
			r.close();
		return r;
	}

	@Override
	public PortletSchema<RootPortlet> getPortletSchema() {
		return SCHEMA;
	}

	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		r.put("child", CH.firstOr(getChildren().keySet(), null));
		return r;
	}

	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		super.init(configuration, origToNewIdMapping, sb);
		String child = (String) configuration.get("child");
		Portlet childPortlet = getManager().getPortlet(origToNewIdMapping.get(child));
		if (childPortlet == null)
			sb.append("Root should have a child").append(SH.NEWLINE);
		else
			addChild(childPortlet);
	}

	public RootPortletDialog addDialog(String title, Portlet p, int width, int height, boolean isModal) {
		if (title != null)
			p.setTitle(title);
		if (getChildrenCount() == 0)
			throw new IllegalStateException("Root doesn't have any children... Can't contain a dialog");
		RootPortletDialog dialog = newRootPortletDialog(p, isModal);

		int sw = width;
		int sh = height;
		int widthLimit = (int) (getWidth() * getWidthScaleRatio());
		int heightLimit = (int) (getHeight() * getHeightScaleRatio());
		if (sw == DIALOG_AUTO_WIDTH_HEIGHT)
			sw = p.getSuggestedWidth(getManager().getPortletMetrics());
		if (sh == DIALOG_AUTO_WIDTH_HEIGHT)
			sh = p.getSuggestedHeight(getManager().getPortletMetrics());
		if (sw == DIALOG_AUTO_WIDTH_HEIGHT)
			sw = widthLimit;
		if (sh == DIALOG_AUTO_WIDTH_HEIGHT)
			sh = heightLimit;
		// ensure dialog size is always within screen size
		p.setSize(Math.min(sw, widthLimit), Math.min(sh, heightLimit));
		dialog.setPosition((getWidth() - p.getWidth()) / 2, (getHeight() - p.getHeight()) / 2);
		dialogs.put(p.getPortletId(), dialog);
		super.addChild(p);
		makeChildrenVisible(getVisible());
		if (getVisible())
			flagPendingAjax();
		if (!PortletHelper.isParentOfChild(p, getManager().getFocusedPortlet()))
			p.onUserRequestFocus(null);
		return dialog;
	}
	public RootPortletDialog addDialog(String title, Portlet p) {
		if (title != null)
			p.setTitle(title);
		if (getChildrenCount() == 0)
			throw new IllegalStateException("Root doesn't have any children... Can't contain a dialog");
		RootPortletDialog dialog = newRootPortletDialog(p, true);

		int sw = p.getSuggestedWidth(getManager().getPortletMetrics());
		int sh = p.getSuggestedHeight(getManager().getPortletMetrics());
		if (sw == DIALOG_AUTO_WIDTH_HEIGHT)
			sw = (int) (getWidth() * getWidthScaleRatio());
		if (sh == DIALOG_AUTO_WIDTH_HEIGHT)
			sh = (int) (getHeight() * getHeightScaleRatio());
		p.setSize(Math.min(sw, getWidth() - getWidthScalePx()), Math.min(sh, getHeight() - getHeightScalePx()));
		dialog.setPosition((getWidth() - p.getWidth()) / 2, (getHeight() - p.getHeight()) / 2);
		dialogs.put(p.getPortletId(), dialog);
		super.addChild(p);
		makeChildrenVisible(getVisible());
		if (getVisible())
			flagPendingAjax();
		if (!PortletHelper.isParentOfChild(p, getManager().getFocusedPortlet()))
			p.onUserRequestFocus(null);
		getManager().focusPortlet(p);
		return dialog;
	}

	public RootPortletDialog addDialog(RootPortletDialog dialog) {
		Portlet portlet = dialog.getPortlet();
		dialog.setRoot(this);
		dialogs.put(portlet.getPortletId(), dialog);
		super.addChild(portlet);
		makeChildrenVisible(getVisible());
		if (getVisible())
			flagPendingAjax();
		if (!PortletHelper.isParentOfChild(portlet, getManager().getFocusedPortlet()))
			portlet.onUserRequestFocus(null);
		return dialog;
	}

	private RootPortletDialog newRootPortletDialog(Portlet p, boolean isModal) {
		final RootPortletDialog r = new RootPortletDialog(this, p, this.dialogs.size() + 1, isModal);
		final PortletStyleManager_Dialog styleManager = this.getManager().getStyleManager().getDialogStyle();
		r.setStyle(styleManager);
		return r;
	}

	@Override
	protected void callJsAddChild(Portlet p) {
		if (p == content)
			callJsFunction("addChild").addParam(0).addParamQuoted(p.getPortletId()).addParamQuoted(null).addParam(false).end();
		else {
			RootPortletDialog dialog = dialogs.get(p.getPortletId());
			callJsFunction("addChild").addParam(dialog.getZindex()).addParamQuoted(p.getPortletId()).addParamQuoted(dialog.getPortlet().getTitle()).addParam(dialog.getIsModal())
					.end();
		}
	}

	@Override
	public void bringToFront(String portletId) {
		//nothing to do... all children are already in front
	}
	@Override
	public boolean hasVacancy() {
		return this.content == null || this.content instanceof BlankPortlet;
	}

	public Portlet getContent() {
		return content;
	}

	@Override
	public boolean isCustomizable() {
		return true;
	}

	public void hideDialog(RootPortletDialog dialog) {
		dialog.visible = false;
		if (dialog.portlet.getVisible()) {
			dialog.portlet.setVisible(false);
			callJsRemoveChild(dialog.portlet);
			dialog.fireOnVisible(false);
		}
	}

	public void showDialog(RootPortletDialog dialog) {
		dialog.visible = true;
		if (!dialog.portlet.getVisible()) {
			makeChildrenVisible(getVisible());
			if (getVisible())
				layoutChildren();
			dialog.fireOnVisible(true);
		}
	}

	private WebMenuListener menuListener;
	private WebRectangle moveTo;

	public void showContextMenu(WebMenu menu, WebMenuListener listener, int x, int y, Map<String, Object> options) {
		closeMenuListener();
		JsFunction jsf = callJsFunction("showMenu").addParam(x).addParam(y).addParamJson(PortletHelper.menuToJson(getManager(), menu));
		JsonBuilder jsonBuilder = jsf.startJson();
		jsonBuilder.addAutotype(options, false);
		jsonBuilder.end();
		jsf.end();
		this.menuListener = listener;
	}
	public void showContextMenu(WebMenu menu, WebMenuListener listener, int x, int y) {
		closeMenuListener();
		callJsFunction("showMenu").addParam(x).addParam(y).addParamJson(PortletHelper.menuToJson(getManager(), menu)).end();
		this.menuListener = listener;
	}
	public void closeContextMenu() {
		closeMenuListener();
		callJsFunction("closeMenu").end();
	}

	@Override
	public int getChildOffsetX(String id) {
		if (content != null && OH.eq(id, content.getPortletId()))
			return 0;
		final RootPortletDialog dialog = this.dialogs.get(id);
		if (dialog != null)
			return dialog.getLeft();
		return -1;
	}

	@Override
	public int getChildOffsetY(String id) {
		if (OH.eq(id, content.getPortletId()))
			return 0;
		final RootPortletDialog dialog = this.dialogs.get(id);
		if (dialog != null)
			return dialog.getTop();
		return -1;
	}

	public boolean isFullScreen() {
		return this.isFullscreen;
	}

	public long getWindowId() {
		return windowId;
	}

	public boolean isPopupWindow() {
		return windowId != 0;
	}

	public void addRootPortletListener(RootPortletListener listener) {
		this.listeners.add(listener);
	}
	public void removeRootPortletListener(RootPortletListener listener) {
		this.listeners.remove(listener);
	}

	public void fireOnPopupWindowFailed() {
		this.isVisible = false;
		this.fireOnPopupWindowClosed();
	}
	public void fireOnPopupWindowClosed() {
		for (RootPortletListener i : this.listeners)
			i.onPopupWindowclosed(this);
		this.content = null;
		//		if (this.content != null)
		//			removeChild(this.content.getPortletId());
	}
	public void fireOnContentChanged(Portlet old, Portlet nuw) {
		for (RootPortletListener i : this.listeners)
			i.onContentChanged(this, old, nuw);
	}

	public void closeThisPopup() {
		OH.assertTrue(this.isPopupWindow(), "Must be a popup window");
		new JsFunction(getManager().getPendingJs(), null, "closeMe").addParamQuoted(getPortletId()).end();
		this.isVisible = false;
	}

	@Override
	public void close() {
	}

	public boolean isWindowVisible() {
		return this.isVisible;
	}

	@Override
	final public void onUserRequestFocus(MouseEvent e) {
		if (this.getChildrenCount() == 1)
			CH.first(this.getChildren().values()).onUserRequestFocus(e);
	}

	@Override
	public boolean onUserKeyEvent(KeyEvent keyEvent) {
		if ("D".equals(keyEvent.getKey()) && keyEvent.isAltKey() && keyEvent.isShiftKey() && !keyEvent.isCtrlKey()) {
			boolean mode = getManager().getDebugLayout();
			getManager().setDebugLayout(!mode);
			if (getManager().getDebugLayout() != mode) {
				setVisible(false);
				setVisible(true);
				getManager().focusPortlet(this);
				return true;
			}
		}
		if (KeyEvent.ESCAPE.equals(keyEvent.getKey())) {
			int maxZ = -1;
			RootPortletDialog maxDialog = null;
			for (RootPortletDialog i : this.getDialogs()) {
				if (i.getZindex() > maxZ)
					maxZ = (maxDialog = i).getZindex();
			}
			if (maxDialog != null) {
				if (maxDialog.getEscapeKeyCloses()) {
					closeDialog(maxDialog.getPortlet().getPortletId());
					return true;
				}
			}
		}
		return super.onUserKeyEvent(keyEvent);
	}

	@Override
	public void setTitle(String title) {
		if (OH.eq(title, this.getTitle()))
			return;
		super.setTitle(title);
		if (getVisible())
			callJsFunction("setBrowserTitle").addParamQuoted(getTitle()).end();
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
	}

	public void resizeTo(int x, int y, int width, int height) {
		this.moveTo = new WebRectangle(x, y, width, height);
		this.flagPendingAjax();
	}

	public int getScreenX() {
		return this.screenX;
	}
	public int getScreenY() {
		return this.screenY;
	}

	public void bringThisPopupToFront() {
		if (this.locationSet) {
			this.callJsFunction("focusMe").addParam(forceFocusMeAfterMs).end();
		}
	}

	public double getHeightScaleRatio() {
		return heightScaleRatio;
	}

	public void setHeightScaleRatio(double heightScaleRatio) {
		this.heightScaleRatio = heightScaleRatio;
	}

	public double getWidthScaleRatio() {
		return widthScaleRatio;
	}

	public void setWidthScaleRatio(double widthScaleRatio) {
		this.widthScaleRatio = widthScaleRatio;
	}

	public int getWidthScalePx() {
		return widthScalePx;
	}

	public void setWidthScalePx(int widthScalePx) {
		this.widthScalePx = widthScalePx;
	}

	public int getHeightScalePx() {
		return HeightScalePx;
	}

	public void setHeightScalePx(int heightScalePx) {
		HeightScalePx = heightScalePx;
	}

	public double getBrowserZoom() {
		return browserZoom;
	}
}
