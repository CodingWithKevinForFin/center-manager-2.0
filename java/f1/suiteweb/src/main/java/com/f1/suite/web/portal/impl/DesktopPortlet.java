/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.portal.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.f1.suite.web.JsFunction;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletBuilder;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletContainer;
import com.f1.suite.web.portal.PortletContainerBuilder;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.suite.web.portal.impl.form.WebAbsoluteLocation;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.json.JsonBuilder;
import com.f1.utils.structs.table.derived.ToDerivedString;

public class DesktopPortlet extends AbstractPortletContainer implements ConfirmDialogListener {

	private static final Logger log = LH.get();
	public static final String WINDOW_STATE_FLT = "flt";
	public static final String WINDOW_STATE_MIN = "min";
	public static final String WINDOW_STATE_MAX = "max";
	public static final String WINDOW_STATE_MINMAX = "minmax";

	public static final PortletSchema<DesktopPortlet> SCHEMA = new BasicPortletSchema<DesktopPortlet>("Desktop", "DesktopPortlet", DesktopPortlet.class, true, true);

	private DesktopManager desktopManager;
	public static final String OPTION_DOCLET_LOCATION = "docletPosition";
	public static final String OPTION_STYLE_CLASS_PREFIX = "styleClassPrefix";
	public static final String VALUE_TOP = "top";
	public static final String VALUE_BOTTOM = "bottom";
	public static final String VALUE_NONE = "none";

	public static final String OPTION_COLOR_WINDOW = "windowColor";
	public static final String OPTION_COLOR_WINDOW_UP = "windowColorUp";
	public static final String OPTION_COLOR_WINDOW_DOWN = "windowColorDown";
	public static final String OPTION_COLOR_WINDOW_TEXT = "windowColorText";
	public static final String OPTION_COLOR_WINDOW_BUTTON = "windowColorButton";
	public static final String OPTION_COLOR_WINDOW_BUTTON_UP = "windowColorButtonUp";
	public static final String OPTION_COLOR_WINDOW_BUTTON_DOWN = "windowColorButtonDown";
	public static final String OPTION_COLOR_WINDOW_BUTTON_ICON = "windowColorButtonIcon";
	public static final String OPTION_WINDOW_BUTTON_HEIGHT = "windowButtonHeight";
	public static final String OPTION_WINDOW_BUTTON_WIDTH = "windowButtonWidth";
	public static final String OPTION_STYLE_BUTTON_MIN = "windowStyleButtonMin";
	public static final String OPTION_STYLE_BUTTON_MAX = "windowStyleButtonMax";
	public static final String OPTION_STYLE_BUTTON_POP = "windowStyleButtonPop";
	public static final String OPTION_STYLE_BUTTON_CLOSE = "windowStyleButtonClose";
	public static final String OPTION_BACKGROUND_INNNER_HTML = "backgroundInnerHTML";
	public static final String OPTION_WINDOW_FONTSTYLE = "windowFontStyle";
	public static final String OPTION_WINDOW_BORDER_INNER_SIZE = "windowBorderInnerSize";
	public static final String OPTION_WINDOW_BORDER_OUTER_SIZE = "windowBorderOuterSize";

	public static final String OPTION_DESKTOP_STYLE = "desktopStyle";

	final private Map<String, Window> windows = new LinkedHashMap<String, Window>();
	final private List<DesktopPortletListener> listeners = new ArrayList<DesktopPortletListener>();
	private Window activeWindow = null;
	private Window defaultActiveWindow;
	private int numWindowsCreated = 0;
	private boolean optionsChanged;

	private boolean needsEnsureInWindowCheck = false;

	private boolean needsLayout = true;
	private boolean isCustomizable = true;
	private boolean saveWindowButtonConfig = false;

	public DesktopPortlet(PortletConfig manager) {
		super(manager);
	}

	public void layoutChildren() {
		layoutWindows();
	}

	protected void layoutWindows() {
		flagPendingAjax();
		needsLayout = true;
		if (needsEnsureInWindowCheck) {
			for (Window window : windows.values()) {
				if (window.isWindowFloating())
					ensureInWindow(window);
			}
			needsEnsureInWindowCheck = false;
		}

		if (getVisible()) {
			Window closestMaximized = null;
			for (Window window : windows.values()) {
				if (window.isWindowMaximized())
					if (closestMaximized == null || closestMaximized.getZindex() < window.getZindex())
						closestMaximized = window;
			}
			if (closestMaximized != null) {
				if (VALUE_NONE.equals(this.options.get(OPTION_DOCLET_LOCATION))) {
					int top = closestMaximized.getHasHeader(true) ? closestMaximized.getHeaderSize() : 0;
					closestMaximized.setPosition(0, top);
					closestMaximized.setSize(getWidth(), getHeight() - top);
				} else {
					closestMaximized.setPosition(0, 20);
					closestMaximized.setSize(getWidth(), getHeight() - 50);
				}
				for (Window window : windows.values())
					if (window.isWindowMaximized())
						makeChildVisible(window.getPortlet(), closestMaximized == window);
			}

			for (Window window : windows.values()) {
				if (window.isPoppedOut())
					continue;
				if (!window.isMinimized() && !window.isPoppedOut() && (closestMaximized == null || window.getZindex() > closestMaximized.getZindex())) {
					if (window.getTop() == -1) {
						if (window.restore())
							ensureInWindow(window);
						else
							layoutNewWindow(window);
					}
					makeChildVisible(window.getPortlet(), true);
				} else if (!window.isWindowMaximized())
					makeChildVisible(window.getPortlet(), false);
			}
			//			}
		}
	}
	public void layoutWindows2() {
		if (getVisible()) {
			for (Window window : this.windows.values())
				if (window.getPortlet().getVisible())
					if (!window.isPoppedOut())
						callJsSetWindowLocation(window);
		}
	}
	private void ensureInWindow(Window window) {
		if (window.getLeft() < 4)
			window.setLeft(4);
		if (window.getTop() < 19)
			window.setTop(19);
		if (getWidth() > 100 && window.getLeft() > getWidth() - 100)
			window.setLeft(getWidth() - 100);
		if (getHeight() > 50 && window.getTop() > getHeight() - 50)
			window.setTop(getHeight() - 50);
		if (getWidth() > 100 && window.getWidth() > this.getWidth())
			window.setWidth(getWidth());
		if (getHeight() > 100 && window.getHeight() > this.getHeight() - 23)
			window.setHeight(getHeight() - 23);
	}
	private void layoutNewWindow(Window window) {
		final int sh = window.getPortlet().getSuggestedHeight(getManager().getPortletMetrics());
		final int sw = window.getPortlet().getSuggestedWidth(getManager().getPortletMetrics());
		final int SPACING = 40;
		numWindowsCreated++;
		final int width = getWidth();
		final int height = getHeight();
		final int windowWidth;
		final int windowHeight;
		if (sw != -1)
			windowWidth = MH.between(sw, 10, (int) (width * .8) - 10);
		else
			windowWidth = width / 2;
		if (sh != -1)
			windowHeight = MH.between(sh, 10, (int) (height * .8) - 55);
		else
			windowHeight = height / 2;
		int paddingW = width - windowWidth;
		int paddingH = height - windowHeight;
		int modW = Math.max(1, paddingW / SPACING);
		int modH = Math.max(1, paddingH / SPACING);
		int windowLeft = (numWindowsCreated % modW) * SPACING;
		int windowTop = (numWindowsCreated % modH) * SPACING;
		window.setPosition(windowLeft, windowTop + 20);
		if (window.getWidth() == 0 || window.getHeight() == 0) {
			window.setSize(windowWidth, windowHeight);
		}
		ensureInWindow(window);

	}
	@Override
	public void addChild(Portlet child) {
		addChild(null, child);
	}

	@Override
	public Portlet removeChild(String childId) {
		Window window = windows.remove(childId);
		if (window != null && window.pwl != null) {
			window.pwl.close();
		}
		if (window == null) {
			return null;
		}
		Portlet r = super.removeChild(childId);

		if (window != null) {
			if (getActiveWindow() == window)
				setActiveWindow(null);
			int maxZIndex = -1;
			Window maxWindow = null;
			int zindex = window.getZindex();
			Integer defaultZIndex = window.getDefaultZIndex();
			boolean hasDefaultZIndex = window.hasDefaultZIndex();
			for (Window w : windows.values()) {
				int z = w.getZindex();
				if (!w.isMinimized()) {
					if (z > maxZIndex) {
						maxZIndex = z;
						maxWindow = w;
					}
				}
				if (z > zindex && zindex != -1)
					w.setZindex(z - 1);
				Integer dz = w.getDefaultZIndex();
				if (hasDefaultZIndex && dz != null && dz > defaultZIndex)
					w.setDefaultZIndex(dz - 1);
			}
			if (maxWindow != null)
				setActiveWindow(maxWindow);
			layoutWindows();
		}
		return r;
	}

	public Window addChild(String childName, Portlet child) {
		return addChild(childName, child, Window.DEFAULT_FLAGS);
	}
	public Window addChild(String childName, Portlet child, int flags) {
		if (null == childName) {
			PortletBuilder p = getManager().getPortletBuilder(child.getPortletConfig().getBuilderId());
			childName = formatText(p == null ? child.getClass().getSimpleName() : p.getPortletBuilderName());
		}
		Window window = newWindow(childName, child, getMaxZIndex() + 1, flags);
		Integer maxDefaultZIndex = getMaxDefaultZIndex();
		if (maxDefaultZIndex != null)
			window.setDefaultZIndex(maxDefaultZIndex + 1);
		windows.put(child.getPortletId(), window);
		window.bringToFront();
		super.addChild(child);
		return window;
	}
	@Override
	public void initJs() {
		super.initJs();
		buildOptionsJs();
	}

	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		if ("bgDoubleClicked".equals(callback)) {
			if (isCustomizable) {
				PortletBuilderPortlet pbp = new PortletBuilderPortlet(generateConfig(), false);
				pbp.setPortletIdOfParentToAddPortletTo(getPortletId());
				getManager().showDialog("Add Portlet", pbp);
			}
		} else if ("windowSized".equals(callback)) {
			String childId = CH.getOrThrow(Caster_String.INSTANCE, attributes, "childId");
			Window window = windows.get(childId);
			if (window == null) {
				PortletManager pm = this.getManager();
				LH.warning(log, pm.describeUser(), " attempted to resize a window that doesn't exist, windowId: ", childId);
			} else if (!window.isMaximized) {
				int left = CH.getOrThrow(Caster_Integer.INSTANCE, attributes, "left");
				int top = CH.getOrThrow(Caster_Integer.INSTANCE, attributes, "top");
				int width = CH.getOrThrow(Caster_Integer.INSTANCE, attributes, "width");
				int height = CH.getOrThrow(Caster_Integer.INSTANCE, attributes, "height");
				if (window.getLeft() == left && window.getTop() == top && window.getWidth() == width && window.getHeight() == height) {
					//					fireWindowMoved(window);//TODO:this should go away
					return;
				}
				width = Math.max(width, 90);
				height = Math.max(height, 1);
				window.setPosition(left, top, width, height);
				ensureInWindow(window);
				window.bringToFront();
				callJsFunction("onUpdated").end();
				//				layoutWindows();
				fireWindowMoved(window);
			}
		} else if ("windowFocus".equals(callback)) {
			String childId = CH.getOrThrow(Caster_String.INSTANCE, attributes, "childId");
			Window window = windows.get(childId);
			if (window != null) {
				if (this.getActiveWindow() == window || window.isMinimized()) {
					return;
				}
				window.bringToFront();
			}
		} else if ("windowButton".equals(callback)) {
			onWindowButton(CH.getOrThrow(Caster_String.INSTANCE, attributes, "buttonId"), CH.getOrThrow(Caster_String.INSTANCE, attributes, "childId"));
		} else if ("renameWindow".equals(callback)) {
			String childId = CH.getOrThrow(Caster_String.INSTANCE, attributes, "childId");
			String text = CH.getOrThrow(Caster_String.INSTANCE, attributes, "text");
			Window window = windows.get(childId);
			window.setNameNoFire(text);
			layoutWindows();
		} else
			super.handleCallback(callback, attributes);
	}

	private void onWindowButton(String buttonId, String childId) {
		Window window = windows.get(childId);
		if (window == null)
			return;
		if (WINDOW_STATE_MIN.equals(buttonId)) {
			window.minimizeWindow();
		} else if (WINDOW_STATE_MAX.equals(buttonId)) {
			if (window.isWindowMaximized()) {
				if (window.getAllowFloat(true))
					window.floatWindow();
			} else {
				if (window.getAllowMax(true))
					window.maximizeWindow();
			}
			window.bringToFront();
		} else if ("pop".equals(buttonId)) {
			if (desktopManager == null) {
				window.popoutWindow();
			} else
				this.desktopManager.onUserPopoutWindow(this, window);
			onUserPopoutWindow(window);
			//Portlet portlet = window.portlet;
			//this.removeChild(portlet.getPortletId());

		} else if ("close".equals(buttonId)) {
			TabPlaceholderPortlet placeholder = placeholderPortletsByOtherId.get(window);
			if (placeholder != null) {
				placeholder.popin();
				return;
			} else {
				if (desktopManager == null) {
					ConfirmDialogPortlet dialog = new ConfirmDialogPortlet(generateConfig(), "Are you sure you want to delete '<B>" + window.getName() + "</B>' window?",
							ConfirmDialogPortlet.TYPE_OK_CANCEL);
					dialog.setCallback("close");
					dialog.setCorrelationData(childId);
					dialog.addDialogListener(this);
					getManager().showDialog("Close Window Confirm", dialog);
				} else {
					desktopManager.onUserDeleteWindow(this, window);
				}
			}
		}
	}

	private void onUserPopoutWindow(Window window) {
		Portlet portlet = window.getPortlet();
		if (portlet instanceof PortletContainer) {
			if (!PortletHelper.findPortletsByType((PortletContainer) window.getPortlet(), TabPlaceholderPortlet.class).isEmpty()) {
				getManager().showAlert("Please redock tabs before popping out this window");
				return;
			}
		}
		TabPlaceholderPortlet t = this.placeholderPortletsByOtherId.get(window);
		if (t != null) {
			t.onPopoutWindow();
		}
		window.popoutWindow();

	}

	public static class Window implements ToDerivedString {

		public static final int ALLOW_FLOAT = 1;
		public static final int ALLOW_MIN = 2;
		public static final int ALLOW_MAX = 4;
		public static final int HAS_HEADER = 8;
		public static final int ALLOW_CLOSE = 16;
		public static final int ALLOW_EDIT_TITLE = 32;
		public static final int ALLOW_POP = 64;
		public static final int IS_HIDDEN = 128;
		public static final int DEFAULT_FLAGS = ALLOW_FLOAT | ALLOW_MIN | ALLOW_MAX | HAS_HEADER | ALLOW_CLOSE | ALLOW_EDIT_TITLE | ALLOW_POP;
		private Portlet portlet;
		private Map<String, Object> options = new HashMap<String, Object>();

		final private Map<String, String> attributes = new HashMap<String, String>();

		//if both are true, then it should become maximized when no-longer minimized
		private boolean isMinimized = false;
		private boolean isMaximized = false;
		private boolean isPoppedOut = false;
		private String defaultState;

		private int restoreWidth = -1;
		private int restoreHeight = -1;
		private int restoreTop;
		private int restoreLeft;
		private int zindex;
		private Integer defaultZIndex;
		private String name;
		private int borderSize = 5;
		private int headerSize = 16;
		private int flags;
		private int defaultFlags;
		private PopupWindowListener pwl;

		private WebAbsoluteLocation locationH = new WebAbsoluteLocation();
		private WebAbsoluteLocation locationV = new WebAbsoluteLocation();
		private WebAbsoluteLocation defaultLocationH = new WebAbsoluteLocation();
		private WebAbsoluteLocation defaultLocationV = new WebAbsoluteLocation();
		private DesktopPortlet owner;

		public boolean isMinimized() {
			return isMinimized;
		}

		public boolean restore() {
			if (getRestoreWidth() == -1)
				return false;

			setSize(restoreWidth, restoreHeight);
			setTop(restoreTop);
			setLeft(restoreLeft);
			return true;
		}

		public boolean isPoppedOut() {
			return this.isPoppedOut;
		}

		public void makeActiveWindow() {
			if (isWindowMinimized())
				return;
			owner.setActiveWindow(this);
		}

		public boolean isWindowMaximized() {
			return isMaximized && !isMinimized && !isPoppedOut;
		}
		public boolean isWindowMinimized() {
			return isMinimized;
		}
		public boolean isWindowFloating() {
			return !isMaximized && !isMinimized && !isPoppedOut;
		}

		public void maximizeWindow() {
			if (isPoppedOut)
				throw new IllegalStateException("not allowed while popped out");
			if (!getAllowMax(true))
				throw new IllegalStateException("Maximize not enabled");
			if (isWindowMaximized())
				return;
			storeRestoreLocation();
			isMaximized = true;
			isMinimized = false;
			owner.fireWindowMoved(this);
			owner.layoutChildren();
		}
		public void minimizeWindowForce() {
			minimizeWindow(true);
		}
		public void minimizeWindow() {
			minimizeWindow(false);
		}
		private void minimizeWindow(boolean force) {
			if (isWindowMinimized())
				return;
			if (isPoppedOut)
				throw new IllegalStateException("not allowed while popped out");
			if (!force && !getAllowMin(true))
				throw new IllegalStateException("Minimize not enabled");
			storeRestoreLocation();
			isMinimized = true;
			if (owner.getActiveWindow() == this)
				owner.setActiveWindow(null);
			owner.fireWindowMoved(this);
			owner.layoutChildren();
		}
		public void floatWindow() {
			if (isPoppedOut)
				throw new IllegalStateException("not allowed while popped out");
			if (!getAllowFloat(true))
				throw new IllegalStateException("Minimize float");
			if (!getAllowFloat(true))
				return;
			if (isWindowFloating())
				return;
			isMaximized = false;
			isMinimized = false;
			if (!restore()) {
				setSize(200, 200);
				setTop(200);
				setLeft(200);
			}
			this.restoreWidth = -1;
			this.restoreHeight = -1;
			owner.fireWindowMoved(this);
			owner.layoutChildren();
		}

		public void popoutWindow() {
			this.popoutWindow(getLeft() - 5, getTop() + 34, getWidth(), getHeight());
		}
		public void popoutWindow(int x, int y, int width, int height) {
			//			x -= 5;
			//			y += 34;
			if (isPoppedOut)
				return;
			this.isPoppedOut = true;
			if (owner.getActiveWindow() == this)
				owner.setActiveWindow(null);
			for (Window w : owner.windows.values()) {
				int z = w.getZindex();
				if (z > this.zindex) {
					w.setZindex(w.getZindex() - 1);
				}
			}
			owner.removeChild(this.getPortlet().getPortletId());
			RootPortlet root = owner.getManager().showPopupWindow(portlet, x, y, width, height, name);
			this.pwl = new PopupWindowListener(this, root);
			owner.addChildSuper(root);
			owner.windows.put(root.getPortletId(), this);
			root.addRootPortletListener(pwl);
			setZindex(-1);
			//popouts.put(pwl.getOrigWindow().getPortlet().getPortletId(), pwl);
		}

		public void setHeight(int height) {
			portlet.setSize(getWidth(), height);
			this.locationV.setSizePx(height);
			owner.layoutChildren();
		}

		public void setWidth(int width) {
			portlet.setSize(width, getHeight());
			this.locationH.setSizePx(width);
			owner.layoutChildren();
		}

		public int getWidth() {
			//			return portlet.getWidth();
			return locationH.getSizePx();
		}
		public int getHeight() {
			//			return portlet.getHeight();
			return locationV.getSizePx();
		}

		protected boolean setNameNoFire(String title) {
			if (SH.isnt(title))
				title = "_";
			if (OH.eq(title, this.name))
				return false;
			this.name = title;
			return true;
		}
		public void setName(String title) {
			if (setNameNoFire(title))
				owner.layoutChildren();
		}

		public void setZindex(int i) {
			zindex = i;
		}

		public int getZindex() {
			return zindex;
		}

		public void storeRestoreLocation() {
			if (isMaximized || isMinimized)
				return;
			this.restoreWidth = getWidth();
			this.restoreHeight = getHeight();
			this.restoreTop = getTop();
			this.restoreLeft = getLeft();
		}

		public Window(DesktopPortlet owner, String name, Portlet portlet, int zindex, int flags) {
			this.owner = owner;
			this.portlet = portlet;
			this.zindex = zindex;
			this.flags = flags;
			this.defaultFlags = flags;
			this.locationV.setStartPx(-1);
			this.locationH.setSizePx(0);
			this.locationV.setSizePx(0);
			setNameNoFire(name);
		}

		public Portlet getPortlet() {
			return portlet;
		}

		public String getName() {
			return name;
		}

		public void setPosition(int left, int top) {
			setLeft(left);
			setTop(top);
		}
		public void setPosition(int left, int top, int w, int h) {
			setPosition(left, top);
			setSize(w, h);
			owner.layoutChildren();
		}
		public void setSize(int w, int h) {
			locationH.setSizePx(w);
			locationV.setSizePx(h);
			portlet.setSize(w, h);
		}
		public void setLeft(int left) {
			locationH.setStartPx(left);
		}
		public void setTop(int top) {
			locationV.setStartPx(top);
		}

		public int getLeft() {
			return locationH.getStartPx();
		}

		public int getTop() {
			return locationV.getStartPx();
		}
		public int getOuterLeft() {
			return isWindowFloating() ? getLeft() - getBorderSize() : getLeft();
		}

		public int getOuterTop() {
			return isWindowFloating() ? getTop() - getBorderSize() - getHeaderSize() : getTop();
		}
		public int getOuterRight() {
			return isWindowFloating() ? getLeft() + getWidth() + getBorderSize() * 2 : getLeft() + getWidth();
		}
		public int getOuterBottom() {
			return isWindowFloating() ? getTop() + getHeight() + getBorderSize() * 2 : getTop() + getHeight();
		}
		public int getRight() {
			return getLeft() + getWidth();
		}
		public int getBottom() {
			return getTop() + getHeight();
		}

		public int getRestoreLeft() {
			return restoreLeft;
		}
		public int getRestoreTop() {
			return restoreTop;
		}
		public int getRestoreWidth() {
			return restoreWidth;
		}
		public int getRestoreHeight() {
			return restoreHeight;
		}

		public void setRestorePosition(int left, int top, int w, int h) {
			this.restoreLeft = left;
			this.restoreTop = top;
			this.restoreWidth = w;
			this.restoreHeight = h;
		}

		public boolean isClipped() {
			for (Window window : owner.windows.values())
				if (!isMinimized && !window.isMinimized() && window.getZindex() > zindex //
						&& getOuterRight() >= window.getOuterLeft() && getOuterLeft() <= window.getOuterRight() //
						&& getOuterTop() <= window.getOuterBottom() && getOuterBottom() >= window.getOuterTop())
					return true;
			return false;
		}

		public boolean getCloseable(boolean currentValue) {
			return MH.allBits(currentValue ? flags : defaultFlags, ALLOW_CLOSE);
		}

		public void setCloseable(boolean allowClose, boolean applyDefaultToo) {
			setFlags(ALLOW_CLOSE, allowClose, applyDefaultToo);
		}
		public boolean getAllowTitleEdit(boolean currentValue) {
			return MH.allBits(currentValue ? flags : defaultFlags, ALLOW_EDIT_TITLE);
		}
		public void setAllowTitleEdit(boolean allowEditTitle, boolean applyDefaultToo) {
			setFlags(ALLOW_EDIT_TITLE, allowEditTitle, applyDefaultToo);
		}
		public boolean isMinimizedAndMaximized() {
			return isMinimized && isMaximized;
		}

		public void bringToFront() {
			if (pwl != null) {
				pwl.bringToFront();
				return;
			}
			if (this.getZindex() == -1) {
				setZindex(owner.getMaxZIndex() + 1);
				owner.setActiveWindow(this);
			}
			if (this == owner.getActiveWindow())
				return;
			int current = getZindex();
			int max = current;
			for (Window w : owner.windows.values()) {
				int z = w.getZindex();
				if (z > current) {
					w.setZindex(w.getZindex() - 1);
					if (z > max)
						max = z;
				}
			}
			isMinimized = false;
			setZindex(max);
			if (!getAllowFloat(true) && getAllowMax(true))
				this.isMaximized = true;
			this.portlet.onUserRequestFocus(null);
			owner.setActiveWindow(this);
			owner.fireWindowMoved(this);
			owner.layoutChildren();
		}
		public boolean getAllowFloat(boolean currentValue) {
			return MH.allBits(currentValue ? flags : defaultFlags, ALLOW_FLOAT);
		}

		public void setAllowFloat(boolean allowFloat, boolean applyDefaultToo) {
			setFlags(ALLOW_FLOAT, allowFloat, applyDefaultToo);
			//			layoutWindows();
		}

		public boolean getAllowMin(boolean currentValue) {
			return MH.allBits(currentValue ? flags : defaultFlags, ALLOW_MIN);
		}

		public void setAllowMin(boolean allowMin, boolean applyDefaultToo) {
			setFlags(ALLOW_MIN, allowMin, applyDefaultToo);
			//			layoutWindows();
		}

		public boolean getAllowPop(boolean currentValue) {
			return MH.allBits(currentValue ? flags : defaultFlags, ALLOW_POP);
		}

		public void setAllowPop(boolean allowPop, boolean applyDefaultToo) {
			setFlags(ALLOW_POP, allowPop, applyDefaultToo);
			//			layoutWindows();
		}

		public boolean getAllowMax(boolean currentValue) {
			return MH.allBits(currentValue ? flags : defaultFlags, ALLOW_MAX);
		}

		public void setAllowMax(boolean allowMax, boolean applyDefaultToo) {
			setFlags(ALLOW_MAX, allowMax, applyDefaultToo);
			//			layoutWindows();
		}

		public boolean getHasHeader(boolean currentValue) {
			return MH.allBits(currentValue ? flags : defaultFlags, HAS_HEADER);
		}

		public void setHasHeader(boolean hasHeader, boolean applyDefaultToo) {
			setFlags(HAS_HEADER, hasHeader, applyDefaultToo);
			owner.layoutChildren();
		}

		public void setIsHidden(boolean b, boolean applyDefaultToo) {
			setFlags(IS_HIDDEN, b, applyDefaultToo);
		}

		public boolean isHidden(boolean currentValue) {
			return MH.allBits(currentValue ? flags : defaultFlags, IS_HIDDEN);
		}

		public String getPortletId() {
			return this.portlet.getPortletId();
		}
		public Object addOption(String key, Object value) {
			//			layoutWindows();
			return options.put(key, value);
		}
		public Object removeOption(String key) {
			//			layoutWindows();
			return options.remove(key);
		}
		public void clearOptions() {
			if (this.options.isEmpty())
				return;
			//			layoutWindows();
			this.options.clear();
		}
		public Object getOption(String option) {
			return options.get(option);
		}
		public Set<String> getOptions() {
			return options.keySet();
		}

		public void closePopup() {
			if (!this.isPoppedOut)
				throw new IllegalStateException("not popped out");
			this.pwl.close();
		}
		public Portlet getPortletForPopout() {
			if (!this.isPoppedOut)
				throw new IllegalStateException("not popped out");
			return this.pwl.getRootPortlet().getContent();
		}

		public Set<String> getAttributes() {
			return attributes.keySet();
		}

		public void putAttribute(String key, String value) {
			this.attributes.put(key, value);
		}
		public String removeAttribute(String key) {
			return this.attributes.remove(key);
		}
		public String getAttribute(String key) {
			return this.attributes.get(key);
		}
		public void clearAttributes() {
			this.attributes.clear();
		}

		//do not modify return value
		public Map<String, String> getAttributesMap() {
			return this.attributes;
		}

		public DesktopPortlet getDesktop() {
			return owner;
		}
		public void clearDefaultLocation() {
			defaultLocationH.clearAllPositioning();
			defaultLocationV.clearAllPositioning();
		}
		public void setDefaultLocationToCurrent() {
			if ((!isMaximized && !isMinimized) || !defaultLocationH.isDefined() || !defaultLocationV.isDefined()) {
				defaultLocationH.setStartPx(locationH.getStartPx());
				defaultLocationH.setSizePx(locationH.getSizePx());
				defaultLocationV.setStartPx(locationV.getStartPx());
				defaultLocationV.setSizePx(locationV.getSizePx());
			}
		}
		public void setDefaultLocation(int left, int top, int width, int height) {
			defaultLocationH.setStartPx(left);
			defaultLocationH.setSizePx(width);
			defaultLocationV.setStartPx(top);
			defaultLocationV.setSizePx(height);
		}
		public int getDefaultLeft() {
			return defaultLocationH.getStartPx();
		}
		public int getDefaultTop() {
			return defaultLocationV.getStartPx();
		}
		public int getDefaultWidth() {
			return defaultLocationH.getSizePx();
		}
		public int getDefaultHeight() {
			return defaultLocationV.getSizePx();
		}
		public boolean hasDefaultLocation() {
			return defaultState != null && defaultLocationH.isDefined() && defaultLocationV.isDefined();
		}
		public boolean isTopDefined() {
			return defaultLocationV.isStartDefined();
		}
		public void setDefaultZIndexToCurrent() {
			this.defaultZIndex = this.zindex;
		}
		public void clearDefaultZIndex() {
			this.defaultZIndex = null;
		}
		public void setDefaultZIndex(Integer idx) {
			if (this.defaultZIndex == null)
				this.defaultZIndex = idx;
			else
				getDesktop().onWindowDefaultZIndexChanged(this, this.defaultZIndex, idx);
		}
		public Integer getDefaultZIndex() {
			return defaultZIndex;
		}
		public boolean hasDefaultZIndex() {
			return defaultZIndex != null;
		}
		public void setDefaultState(String state) {
			this.defaultState = state;
		}
		public String getDefaultState() {
			return defaultState;
		}
		public void setDefaultStateToCurrent() {
			this.defaultState = getCurrentState();
		}
		public String getCurrentState() {
			if (isMinimized || isMaximized)
				return isMinimizedAndMaximized() ? WINDOW_STATE_MINMAX : (isMinimized ? WINDOW_STATE_MIN : WINDOW_STATE_MAX);
			else
				return WINDOW_STATE_FLT;
		}
		public void clearDefaultState() {
			this.defaultState = null;
		}

		public int getBorderSize() {
			return borderSize;
		}

		public void setBorderSize(int borderSize) {
			if (this.borderSize == borderSize)
				return;
			this.borderSize = borderSize;
			owner.layoutWindows();
		}

		public int getHeaderSize() {
			return headerSize;
		}

		public void setHeaderSize(int headerSize) {
			if (this.headerSize == headerSize)
				return;
			this.headerSize = headerSize;
			owner.layoutWindows();
		}

		@Override
		public String toDerivedString() {
			return toDerivedString(new StringBuilder()).toString();
		}

		@Override
		public StringBuilder toDerivedString(StringBuilder sb) {
			return sb.append("WINDOW[").append(this.getName()).append(']');
		}
		private void setFlags(int flag, boolean on, boolean applyDefaultToo) {
			this.flags = MH.setBits(flags, flag, on);
			if (applyDefaultToo)
				this.defaultFlags = MH.setBits(defaultFlags, flag, on);
		}

		public boolean currentIsDefaultIncludeIndex() {
			if (!currentIsDefault())
				return false;
			return getCurrentState() != WINDOW_STATE_FLT || OH.eq(getDefaultZIndex(), (Integer) getZindex());
		}
		public boolean currentIsDefault() {
			boolean statesEq = OH.eq(getCurrentState(), getDefaultState());
			if (!statesEq)
				return false;
			if (getCurrentState() != WINDOW_STATE_FLT)
				return true;
			return getDefaultWidth() == getWidth() && getDefaultHeight() == getHeight() && getDefaultLeft() == getLeft() && getDefaultTop() == getTop();
		}

		public RootPortlet getRootPortlet() {
			return this.pwl == null ? null : this.pwl.getRootPortlet();
		}
	}

	@Override
	public PortletSchema<DesktopPortlet> getPortletSchema() {
		return SCHEMA;
	}
	public void addChildSuper(Portlet root) {
		super.addChild(root);
	}

	public int getMaxZIndex() {
		int r = 0;
		for (Window i : this.windows.values())
			r = Math.max(i.getZindex(), r);
		return r;
	}
	public Integer getMaxDefaultZIndex() {
		Integer r = 0, cur;
		for (Window w : this.windows.values()) {
			cur = w.getDefaultZIndex();
			if (cur == null)
				return null;
			r = Math.max(cur, r);
		}
		return r == 0 ? null : r;
	}
	@Override
	public void replaceChild(String removed, Portlet replacement) {
		Portlet removedPortlet = super.removeChild(removed);
		Window window = windows.remove(removed);
		replacement.setSize(window.getPortlet().getWidth(), window.getPortlet().getHeight());
		window.portlet = replacement;
		windows.put(window.portlet.getPortletId(), window);
		if (getVisible()) {
			makeChildVisible(removedPortlet, false);
			makeChildVisible(window.portlet, true);
			layoutWindows();
		}
		super.addChild(replacement);
	}

	@Override
	protected void onUserDeleteChild(String childPortletId) {
		Portlet removed = removeChild(childPortletId);
		removed.onClosed();
		layoutWindows();
	}

	@Override
	protected void callJsAddChild(Portlet p) {
		callJsFunction("addChild").addParamQuoted(p.getPortletId()).addParamQuotedHtml(CH.getOrThrow(windows, p.getPortletId()).getName()).end();
	}
	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		if (getDefaultActiveWindow() != null)
			r.put("activeDflt", portletToConfigSaveId(this.getDefaultActiveWindow().getPortlet()));
		else if (getActiveWindow() != null)
			CH.putNoNull(r, "active", portletToConfigSaveId(this.getActiveWindow().getPortlet()));
		List<Map<String, Object>> windowConfigs = new ArrayList<Map<String, Object>>();
		int pos = 0;
		Set<String> tearouts = new HashSet<String>();
		for (TabPlaceholderPortlet i : PortletHelper.findPortletsByType(getManager().getRoot(), TabPlaceholderPortlet.class)) {
			tearouts.add(i.getTearoutWindow().getPortletId());
		}
		for (Window window : windows.values()) {
			if (!shouldSaveWindowConfiguration(window))
				continue;
			if (window.getPortlet().getPortletConfig().getBuilderId() == null)
				continue;
			if (tearouts.contains(window.getPortlet().getPortletId()))
				continue;
			Map<String, Object> map = new HashMap<String, Object>();
			saveWindowConfig(pos++, window, map);
			windowConfigs.add(map);
		}
		r.put("windows", windowConfigs);
		return r;
	}

	protected boolean shouldSaveWindowConfiguration(Window window) {
		return true;
	}

	private void saveWindowConfig(int pos, Window window, Map<String, Object> map) {
		String portletId = portletToConfigSaveId(window.getPortlet());
		if (portletId == null)
			return;
		if (window.hasDefaultLocation()) {
			map.put("stateDflt", window.getDefaultState());
			map.put("leftDflt", window.getDefaultLeft());
			map.put("topDflt", window.getDefaultTop());
			map.put("widthDflt", window.getDefaultWidth());
			map.put("heightDflt", window.getDefaultHeight());
		} else {
			map.put("state", window.getCurrentState());
			if (window.isMinimized || window.isMaximized) {
				map.put("left", window.getRestoreLeft());
				map.put("top", window.getRestoreTop());
				map.put("width", window.getRestoreWidth());
				map.put("height", window.getRestoreHeight());
			} else {
				map.put("left", window.getLeft());
				map.put("top", window.getTop());
				map.put("width", window.getWidth());
				map.put("height", window.getHeight());
			}
		}
		map.put("pos", pos++);
		map.put("title", window.getName());
		Integer defaultZIndex = window.getDefaultZIndex();
		if (defaultZIndex == null) {
			map.put("zindex", window.getZindex());
		} else {
			map.put("zindexDflt", defaultZIndex);
		}
		if (this.saveWindowButtonConfig) {
			map.put("aflt", window.getAllowFloat(false));
			map.put("amax", window.getAllowMax(false));
			map.put("amin", window.getAllowMin(false));
			map.put("acls", window.getCloseable(false));
		}
		map.put("header", window.getHasHeader(false));
		map.put("portlet", portletId);
		map.put("hidden", window.isHidden(false));
		Set<String> attr = window.getAttributes();
		if (!attr.isEmpty()) {
			map.put("attr", new HashMap<String, String>(window.getAttributesMap()));
		}
	}
	protected String portletToConfigSaveId(Portlet portlet) {
		return portlet.getPortletId();
	}
	protected Portlet configSaveIdToPortlet(Map<String, String> origToNewIdMapping, String portletId) {
		return getManager().getPortlet(CH.getOrThrow(origToNewIdMapping, portletId));
	}

	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		super.init(configuration, origToNewIdMapping, sb);
		List<Map<String, Object>> windowConfigs = (List<Map<String, Object>>) CH.getOrThrow(Caster_Simple.OBJECT, configuration, "windows");
		windows.clear();
		Map<Integer, Window> windowByZindex = new TreeMap<Integer, DesktopPortlet.Window>();
		Map<Integer, Window> windowByPos = new TreeMap<Integer, DesktopPortlet.Window>();
		for (Map<String, Object> windowConfig : windowConfigs) {
			Integer defaultZIndex = CH.getOr(Caster_Integer.INSTANCE, windowConfig, "zindexDflt", null);
			Integer zindex;
			if (defaultZIndex == null)
				zindex = CH.getOrThrow(Caster_Integer.INSTANCE, windowConfig, "zindex");
			else
				zindex = defaultZIndex;
			String title = CH.getOrThrow(Caster_String.INSTANCE, windowConfig, "title");
			String portletId = CH.getOrThrow(Caster_String.INSTANCE, windowConfig, "portlet");
			Portlet portlet = configSaveIdToPortlet(origToNewIdMapping, portletId);
			OH.assertNotNull(portlet, portletId);
			Window window = newWindow(title, portlet, zindex, Window.DEFAULT_FLAGS);
			window.setDefaultZIndex(defaultZIndex);
			while (windowByZindex.containsKey(zindex))
				zindex++;
			String defaultState = CH.getOr(Caster_String.INSTANCE, windowConfig, "stateDflt", null);
			window.setDefaultState(defaultState);
			if (defaultState != null) {
				Integer defaultLeft = CH.getOr(Caster_Integer.INSTANCE, windowConfig, "leftDflt", null);
				Integer defaultTop = CH.getOr(Caster_Integer.INSTANCE, windowConfig, "topDflt", null);
				Integer defaultWidth = CH.getOr(Caster_Integer.INSTANCE, windowConfig, "widthDflt", null);
				Integer defaultHeight = CH.getOr(Caster_Integer.INSTANCE, windowConfig, "heightDflt", null);
				window.setPosition(defaultLeft, defaultTop, defaultWidth, defaultHeight);
				window.setDefaultLocation(defaultLeft, defaultTop, defaultWidth, defaultHeight);
				if (OH.eq(defaultState, WINDOW_STATE_MIN)) {
					window.setRestorePosition(defaultLeft, defaultTop, defaultWidth, defaultHeight);
					window.isMinimized = true;
					// check if the portlet id on the key "active" matches with id of this window.
					// if so, then set it to null. Otherwise, the program would think the minimized window is active
					// and won't show up when clicking it from the "Windows" tab.
					if (OH.eq(configuration.get("active"), windowConfig.get("portlet"))) {
						configuration.put("active", null);
					}
				} else if (OH.eq(defaultState, WINDOW_STATE_FLT)) {
					// nothing to do here
					// the block before the if statement should take care of this
				} else if (OH.eq(defaultState, WINDOW_STATE_MAX)) {
					window.setRestorePosition(defaultLeft, defaultTop, defaultWidth, defaultHeight);
					window.isMaximized = true;
					if (OH.eq(configuration.get("active"), windowConfig.get("portlet"))) {
						configuration.put("active", null);
					}
				} else if (OH.eq(defaultState, WINDOW_STATE_MINMAX)) {
					window.isMaximized = true;
					window.isMinimized = true;
				}
			} else {
				String state = CH.getOr(Caster_String.INSTANCE, windowConfig, "state", WINDOW_STATE_FLT);
				if (WINDOW_STATE_FLT.equals(state)) {

					int left = CH.getOrThrow(Caster_Double.INSTANCE, windowConfig, "left").intValue();
					int top = CH.getOrThrow(Caster_Double.INSTANCE, windowConfig, "top").intValue();
					Integer width = CH.getOr(Caster_Integer.INSTANCE, windowConfig, "width", 0);
					Integer height = CH.getOr(Caster_Integer.INSTANCE, windowConfig, "height", 0);
					window.setPosition(left, top, width, height);
				} else {
					int left, top, w, h;
					if (windowConfig.containsKey("rleft")) {//backwards compatibility
						left = CH.getOrThrow(Caster_Double.INSTANCE, windowConfig, "rleft").intValue();
						top = CH.getOrThrow(Caster_Double.INSTANCE, windowConfig, "rtop").intValue();
						w = CH.getOrThrow(Caster_Double.INSTANCE, windowConfig, "rwidth").intValue();
						h = CH.getOrThrow(Caster_Double.INSTANCE, windowConfig, "rheight").intValue();
					} else {
						left = CH.getOrThrow(Caster_Double.INSTANCE, windowConfig, "left").intValue();
						top = CH.getOrThrow(Caster_Double.INSTANCE, windowConfig, "top").intValue();
						w = CH.getOrThrow(Caster_Double.INSTANCE, windowConfig, "width").intValue();
						h = CH.getOrThrow(Caster_Double.INSTANCE, windowConfig, "height").intValue();
					}
					window.setRestorePosition(left, top, w, h);
					if (WINDOW_STATE_MIN.equals(state)) {
						window.isMinimized = true;
					} else if (WINDOW_STATE_MAX.equals(state)) {
						window.isMaximized = true;
					} else if (WINDOW_STATE_MINMAX.equals(state)) {
						window.isMinimized = true;
						window.isMaximized = true;
					} else
						sb.append(" invalid window state: ").append(state);
				}
			}

			Boolean aflt = CH.getOr(Caster_Boolean.INSTANCE, windowConfig, "aflt", null);
			if (aflt != null)
				window.setAllowFloat(aflt, true);
			Boolean amax = CH.getOr(Caster_Boolean.INSTANCE, windowConfig, "amax", null);
			if (amax != null)
				window.setAllowMax(amax, true);
			Boolean amin = CH.getOr(Caster_Boolean.INSTANCE, windowConfig, "amin", null);
			if (amin != null)
				window.setAllowMin(amin, true);
			Boolean acls = CH.getOr(Caster_Boolean.INSTANCE, windowConfig, "acls", null);
			if (acls != null)
				window.setCloseable(acls, true);
			//			}
			window.setHasHeader(CH.getOr(Caster_Boolean.INSTANCE, windowConfig, "header", Boolean.TRUE), true);
			window.setIsHidden(CH.getOr(Caster_Boolean.INSTANCE, windowConfig, "hidden", Boolean.FALSE), true);
			Map<String, String> attr = (Map) windowConfig.get("attr");
			if (attr != null)
				for (Entry<String, String> e : attr.entrySet())
					window.putAttribute(e.getKey(), e.getValue());

			windowByZindex.put(zindex, window);
			int pos = CH.getOr(Caster_Integer.INSTANCE, windowConfig, "pos", windowByPos.size());

			while (windowByPos.containsKey(pos))
				pos++;
			windowByPos.put(pos, window);
		}
		for (Window window : windowByPos.values()) {
			windows.put(window.getPortlet().getPortletId(), window);
			super.addChild(window.getPortlet());
		}
		int index = 0;
		for (Window w : windowByZindex.values())
			w.setZindex(++index);
		String defaultActiveWindowId = CH.getOr(Caster_String.INSTANCE, configuration, "activeDflt", null);
		String activeWindow = CH.getOr(Caster_String.INSTANCE, configuration, "active", null);
		if (defaultActiveWindowId != null) {

			Portlet p = configSaveIdToPortlet(origToNewIdMapping, defaultActiveWindowId);
			if (p != null)
				this.defaultActiveWindow = windows.get(p.getPortletId());
			this.setActiveWindow(this.defaultActiveWindow);
		} else if (activeWindow != null) {
			Portlet p = configSaveIdToPortlet(origToNewIdMapping, activeWindow);
			if (p != null)
				this.setActiveWindow(windows.get(p.getPortletId()));
		}
	}

	protected Window newWindow(String title, Portlet portlet, int zindex, int flags) {
		return new Window(this, title, portlet, zindex, flags);
	}

	public static class Builder extends AbstractPortletBuilder<DesktopPortlet> implements PortletContainerBuilder<DesktopPortlet> {

		public static final String ID = "desktop";

		public Builder() {
			super(DesktopPortlet.class);
			setIcon("portlet_icon_desktop");
		}

		@Override
		public DesktopPortlet buildPortlet(PortletConfig portletConfig) {
			DesktopPortlet r = new DesktopPortlet(portletConfig);
			return r;
		}

		@Override
		public String getPortletBuilderName() {
			return "Desktop";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

		@Override
		public void extractChildPorletIds(Map<String, Object> config, Map<String, Map> sink) {
			List<Map> windows = CH.getOrThrow(List.class, config, "windows");
			for (Map window : windows)
				sink.put(CH.getOrThrow(String.class, window, "portlet"), window);
		}

	}

	@Override
	public void bringToFront(String portletId) {
		Window window = windows.get(portletId);
		if (window == null) {
			window = windows.get(getManager().getPopoutForPortletId(portletId).getPortletId());
			window.closePopup();
		} else if (window.isPoppedOut()) {
			window.bringToFront();
		} else
			window.bringToFront();
	}

	@Override
	public boolean hasVacancy() {
		return true;
	}

	@Override
	public void setSize(int width, int height) {
		if (width != getWidth() || height != getHeight()) {
			needsEnsureInWindowCheck = true;
			super.setSize(width, height);
		}
	}

	private void callJsSetWindowLocation(Window window) {
		JsFunction func = callJsFunction("setWindowLocation");
		func.addParamQuoted(window.portlet.getPortletId());
		func.addParam(window.getLeft());
		func.addParam(window.getTop());
		func.addParam(window.getWidth());
		func.addParam(window.getHeight());
		func.addParam(window.isWindowMaximized());
		func.addParam(window.getZindex());
		func.addParam(window == getActiveWindow() || !window.isClipped());
		func.addParam(window.getAllowPop(true));//show min?
		func.addParam(window.getAllowMin(true));//show min?
		func.addParam(window.isWindowMaximized() ? window.getAllowFloat(true) : window.getAllowMax(true));//show max?
		func.addParam(window.getCloseable(true));//show close?
		func.addParam((window.getCloseable(true) || window.getAllowMin(true) || window.getAllowFloat(true)) && window.getHasHeader(true));//show header?
		func.addParam(window.getAllowTitleEdit(true));//title editable?
		func.addParam(window.getHasHeader(true) ? window.getHeaderSize() : 0);
		func.addParam(window.getBorderSize());
		func.addParamQuoted(WebHelper.escapeHtml(window.getName()));
		func.startJson().addQuoted(window.options).close();
		func.end();
	}
	@Override
	public boolean isCustomizable() {
		return isCustomizable;
	}

	public void setIsCustomizable(boolean isCustomizable) {
		this.isCustomizable = isCustomizable;
	}
	@Override
	public void drainJavascript() {
		//		addOption(OPTION_WINDOW_FONTSTYLE, "_fs=20|style.marginLeft=20px");
		super.drainJavascript();
		if (getVisible()) {
			if (optionsChanged) {
				optionsChanged = false;
				buildOptionsJs();
			}
			if (needsLayout) {
				this.needsLayout = false;
				layoutWindows2();
			}
		}
	}
	private void buildOptionsJs() {
		JsFunction func = callJsFunction("setOptions");
		JsonBuilder optionsJson = func.startJson();
		optionsJson.addAutotype(options, false);
		optionsJson.close();
		func.end();
	}

	private Map<String, Object> options = new HashMap<String, Object>();

	public Object addOption(String key, Object value) {
		Object existing = options.put(key, value);
		if (OH.ne(existing, value)) {
			flagPendingAjax();
			this.optionsChanged = true;
		}
		return existing;
	}
	public Object removeOption(String key) {
		Object existing = options.remove(key);
		if (existing != null) {
			flagPendingAjax();
			this.optionsChanged = true;
		}
		return existing;
	}
	public void clearOptions() {
		this.options.clear();
		if (options.size() == 0)
			return;
		flagPendingAjax();
		this.optionsChanged = true;
	}
	public Object getOption(String option) {
		return options.get(option);
	}
	public Set<String> getOptions() {
		return options.keySet();
	}

	public Window getWindow(String id) {
		Window r = CH.getOrThrow(this.windows, id);
		return r;
	}
	public Window getWindowNoThrow(String id) {
		Window r = CH.getOr(this.windows, id, null);
		return r;
	}

	public void addDesktopListener(DesktopPortletListener listener) {
		this.listeners.add(listener);
	}
	public void removeDesktopListener(DesktopPortletListener listener) {
		this.listeners.remove(listener);
	}

	private void fireWindowMoved(Window window) {
		if (getVisible())
			for (DesktopPortletListener listener : this.listeners)
				listener.onWindowMoved(this, window);
	}

	public Window getActiveWindow() {
		return activeWindow;
	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if (ConfirmDialogPortlet.ID_YES.equals(id)) {
			if ("close".equals(source.getCallback())) {
				getChild((String) source.getCorrelationData()).close();
			}
		}
		return true;
	}

	public void docLeft(String portletId) {
		//demaximize();
		Window window = getWindow(portletId);
		window.setPosition(0, 0);
		Portlet portlet = window.getPortlet();
		if (portlet.getWidth() == 0)
			portlet.setSize(420, portlet.getHeight());
		portlet.setSize(portlet.getWidth(), getHeight());
		if (portlet.getWidth() < getWidth() - 200) {
			int minLeft = portlet.getWidth() + 15;
			int maxWidth = getWidth() - minLeft - 10;
			for (Window other : this.windows.values()) {
				if (other == window)
					continue;
				if (other.getLeft() < minLeft)
					other.setLeft(minLeft);
				if (other.getWidth() > maxWidth)
					other.setWidth(maxWidth);
				ensureInWindow(other);
			}
		}
		window.setRestorePosition(window.getLeft(), window.getTop(), window.getWidth(), window.getHeight());
		ensureInWindow(window);
		layoutWindows();
	}

	@Override
	public int getChildOffsetX(String id) {
		final Window window = this.windows.get(id);
		return window == null ? -1 : window.getLeft();
	}

	@Override
	public int getChildOffsetY(String id) {
		final Window window = this.windows.get(id);
		return window == null ? -1 : window.getTop();
	}

	public Collection<Window> getWindows() {
		return this.windows.values();
	}

	public DesktopManager getDeskopManager() {
		return desktopManager;
	}

	public void setDeskopManager(DesktopManager desktopManager) {
		this.desktopManager = desktopManager;
	}

	public static class PopupWindowListener implements RootPortletListener {

		private Window origWindow;
		private RootPortlet root;
		private boolean isClosed;
		private DesktopPortlet desktop;

		public PopupWindowListener(Window window, RootPortlet root) {
			origWindow = window;
			this.desktop = window.getDesktop();
			this.root = root;
		}
		@Override
		public void onPopupWindowclosed(RootPortlet rootPortlet) {
			isClosed = true;
			if (desktop.desktopManager != null)
				desktop.desktopManager.onUserPopoutWindowClosed(this, origWindow);
			TabPlaceholderPortlet t = desktop.placeholderPortletsByOtherId.get(origWindow);
			if (t != null && t.getFullyDockOnClose())
				t.popin();
			if (desktop.getChildren().containsKey(root.getPortletId())) {
				origWindow.portlet = root.getContent();
				desktop.removeChild(root.getPortletId());
				if (origWindow.getPortlet() != null) {
					origWindow.portlet.setSize(origWindow.getWidth(), origWindow.getHeight());
					desktop.windows.put(origWindow.getPortletId(), origWindow);
					origWindow.isPoppedOut = false;
					origWindow.pwl = null;
					desktop.addChildSuper(origWindow.getPortlet());
					origWindow.bringToFront();
				}
			} else
				desktop.windows.remove(root.getPortletId());
		}
		public Window getOrigWindow() {
			return this.origWindow;
		}
		public RootPortlet getRootPortlet() {
			return this.root;
		}

		public void close() {
			if (this.isClosed)
				return;
			this.isClosed = true;
			this.root.closeThisPopup();
		}
		public void bringToFront() {
			this.root.bringThisPopupToFront();
		}
		@Override
		public void onContentChanged(RootPortlet rootPortlet, Portlet old, Portlet nuw) {
			if (nuw != null)
				origWindow.portlet = nuw;
		}
	}

	public void setActiveWindow(Window activeWindow) {
		this.activeWindow = activeWindow;
		if (this.activeWindow != null)
			this.activeWindow.getPortlet().onUserRequestFocus(null);
	}
	public Window getDefaultActiveWindow() {
		return defaultActiveWindow;
	}
	public void setDefaultActiveWindow(Window w) {
		this.defaultActiveWindow = w;
	}
	public void setDefaultActiveWindowToCurrent() {
		this.defaultActiveWindow = this.activeWindow;
	}
	public void clearDefaultActiveWindow() {
		this.defaultActiveWindow = null;
	}
	public void setSaveWindowButtonConfig(boolean save) {
		this.saveWindowButtonConfig = save;
	}
	public boolean getSaveWindowButtonConfig() {
		return saveWindowButtonConfig;
	}
	private void onWindowDefaultZIndexChanged(Window target, int prevIdx, int newIdx) {
		if (prevIdx > newIdx) {
			for (Window w : windows.values()) {
				if (w.hasDefaultZIndex())
					if (newIdx <= w.getDefaultZIndex() && w.getDefaultZIndex() < prevIdx) {
						w.defaultZIndex++;
					}
			}
		} else {
			for (Window w : windows.values()) {
				if (w.hasDefaultZIndex())
					if (prevIdx < w.getDefaultZIndex() && w.getDefaultZIndex() <= newIdx) {
						w.defaultZIndex--;
					}
			}
		}
		target.defaultZIndex = newIdx;
	}

	private Map<String, TabPlaceholderPortlet> placeholderPortlets = new HashMap<String, TabPlaceholderPortlet>();
	private Map<Window, TabPlaceholderPortlet> placeholderPortletsByOtherId = new IdentityHashMap<Window, TabPlaceholderPortlet>();

	public void addAmiWebTabPlaceholderPortlet(TabPlaceholderPortlet p2) {
		this.placeholderPortlets.put(p2.getPortletId(), p2);
		this.placeholderPortletsByOtherId.put(p2.getTearoutWindow(), p2);
	}
	public void onPlaceHolderClosed(TabPlaceholderPortlet p) {
		placeholderPortlets.remove(p.getPortletId());
		placeholderPortletsByOtherId.remove(p.getTearoutWindow());
	}
	public boolean hasUndockedWindows() {
		return !this.placeholderPortlets.isEmpty();
	}
	public boolean isInTearout(Portlet p) {
		Portlet windowPortlet = PortletHelper.findPortletWithParentByType(p, DesktopPortlet.class);
		if (windowPortlet == null)
			return false;
		DesktopPortlet desktop = (DesktopPortlet) windowPortlet.getParent();
		Window window = desktop.getWindow(windowPortlet.getPortletId());
		return window != null && placeholderPortletsByOtherId.containsKey(window);
	}

	public boolean isPlaceholder(Window win) {
		return this.placeholderPortletsByOtherId.containsKey(win);
	}
	public void redockAllPortlets() {
		if (!placeholderPortlets.isEmpty())
			for (TabPlaceholderPortlet i : CH.l(placeholderPortlets.values()))
				i.popin();
	}
	public TabPlaceholderPortlet getTabPlaceholderPortlet(Portlet p) {
		Portlet windowPortlet = PortletHelper.findPortletWithParentByType(p, DesktopPortlet.class);
		if (windowPortlet == null)
			return null;
		DesktopPortlet desktop = (DesktopPortlet) windowPortlet.getParent();
		Window window = desktop.getWindow(windowPortlet.getPortletId());
		if (window == null)
			return null;
		return placeholderPortletsByOtherId.get(window);
	}

	private Comparator<Window> ZINDEX_COMPARATOR = new Comparator<DesktopPortlet.Window>() {

		@Override
		public int compare(Window o1, Window o2) {
			return OH.compare(o1.getZindex(), o2.getZindex());
		}
	};

	public void organizeZIndexes() {
		List<Window> t = CH.sort(windows.values(), ZINDEX_COMPARATOR);
		int z = 1;
		for (Window i : t) {
			if (i.getZindex() == -1)
				continue;
			i.setZindex(z++);
		}
	}
}
