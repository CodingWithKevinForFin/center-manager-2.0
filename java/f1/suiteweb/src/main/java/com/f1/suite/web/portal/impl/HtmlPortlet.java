/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.portal.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.base.Lockable;
import com.f1.base.LockedException;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.WebMenuLink;
import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.form.Form;
import com.f1.suite.web.portal.impl.form.HtmlPortletCustomCallbackListener;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.IntKeyMap.Node;

public class HtmlPortlet extends AbstractPortlet {

	public static final PortletSchema<HtmlPortlet> SCHEMA = new BasicPortletSchema<HtmlPortlet>("Html", "HtmlPortlet", HtmlPortlet.class, false, true);
	private boolean needsInit;
	private String html;
	public final BasicPortletSocket setHtmlSocket;
	private String cssClass = "html_portlet";
	private String cssStyle = "";
	private boolean needsCss = true;
	private int MAX_HTML_LENGTH = 1024 * 1024 * 2;

	private List<HtmlPortletListener> listeners = new ArrayList<HtmlPortletListener>();
	private String js;
	private List<WebHtmlContextMenuListener> menuListeners = new ArrayList<WebHtmlContextMenuListener>();
	private WebHtmlContextMenuFactory contextMenuFactory;
	private boolean configChanged;
	private IntKeyMap<Callback> callbacks = new IntKeyMap<Callback>();
	private IntKeyMap<Callback> callbacksBuf = new IntKeyMap<Callback>();
	private List<HtmlPortletCustomCallbackListener> customCallbackListeners = new ArrayList<HtmlPortletCustomCallbackListener>();

	public HtmlPortlet(PortletConfig manager) {
		this(manager, "");
	}
	public HtmlPortlet(PortletConfig manager, String html) {
		super(manager);
		setHtml(html);
		this.setHtmlSocket = addSocket(false, "setHtml", "Set Html", true, null, CH.s(SetHtmlInterPortletMessage.class));
	}
	public HtmlPortlet(PortletConfig manager, String html, String cssClass) {
		this(manager, html);
		setCssClass(cssClass);
	}

	@Override
	public void initJs() {
		super.initJs();
		flagPendingAjax();
		needsInit = true;
		configChanged = true;
		needsCss = true;
	}

	@Override
	public void drainJavascript() {
		super.drainJavascript();
		if (this.getVisible()) {
			if (configChanged) {
				configChanged = false;
				callJsFunction("setSupportsContextMenu").addParam(this.contextMenuFactory != null).end();
			}
			if (needsInit) {
				needsInit = false;
				String text = getHtml();
				callJsFunction("setInnerHTML").addParamQuoted(text).end();
			}
			if (needsCss) {
				needsCss = false;
				callJsFunction("setCssClass").addParamQuoted(cssClass).end();
				callJsFunction("setCssStyle").addParamQuoted(cssStyle).end();
			}
			if (SH.is(js))
				getManager().getPendingJs().append(getJsObjectName()).append(".").append(js).append(";");
			//							getManager().getPendingJs().append(js);
		}
	}
	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		if ("click".equals(callback)) {
			onUserClick();
		} else if ("showmenu".equals(callback)) {
			if (contextMenuFactory != null) {
				WebMenuItem menu = contextMenuFactory.createMenu(this);
				if (menu != null) {
					JsFunction jsf = callJsFunction("showMenu");
					jsf.addParamJson(PortletHelper.menuToJson(getManager(), menu));
					jsf.end();
				}
			}
		} else if ("callback".equals(callback)) {
			String id = CH.getOr(Caster_String.INSTANCE, attributes, "id", null);
			int x = CH.getOr(Caster_Double.INSTANCE, attributes, "mouseX", -1d).intValue();
			int y = CH.getOr(Caster_Double.INSTANCE, attributes, "mouseY", -1d).intValue();
			if (SH.isInt(id)) {
				int n = SH.parseInt(id);

				Callback cb = this.callbacks.get(n);
				if (cb.listener != null)
					cb.listener.onUserCallback(this, cb.id, x, y, cb);
				else
					onUserCallback(cb.id, x, y, cb);
			}

		} else if ("menuitem".equals(callback)) {
			final WebMenuLink id = getManager().getMenuManager().fireLinkForId(CH.getOrThrow(attributes, "id"));
			if (id != null)
				fireOnMenu(id.getAction());
		} else if (Form.CALLBACK_CUSTOMCALLBACK.contentEquals(callback)) {
			String customType = attributes.get("customType");
			Object params = ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject(attributes.get("customParams"));
			for (HtmlPortletCustomCallbackListener listener : this.customCallbackListeners) {
				listener.onCustomCallback(this, customType, params, attributes);
			}
		} else
			super.handleCallback(callback, attributes);
	}
	public String getHtml() {
		return this.html;
	}
	public String getCssClass() {
		return this.cssClass;
	}
	public String getCssStyle() {
		return this.cssStyle;
	}

	public HtmlPortlet setHtml(String html) {
		this.callbacks.clear();
		if (this.callbacksBuf.size() > 0) {
			for (Node<Callback> i : this.callbacksBuf)
				i.getValue().lock();
			this.callbacks.addAll(this.callbacksBuf);
			this.callbacksBuf.clear();
		}
		return setHtml(html, true);
	}
	public HtmlPortlet appendHtml(String html) {
		if (this.callbacksBuf.size() > 0) {
			for (Node<Callback> i : this.callbacksBuf)
				i.getValue().lock();
			this.callbacks.addAll(this.callbacksBuf);
			this.callbacksBuf.clear();
		}
		return setHtml(getHtml() + html, true);
	}
	public HtmlPortlet setHtml(String html, boolean fireEvent) {
		if (html == null)
			html = "";
		if (OH.eq(this.html, html))
			return this;
		String orig = this.html;
		if (html.length() > MAX_HTML_LENGTH)
			throw new RuntimeException("Html exceeds max length: " + html.length() + " > " + MAX_HTML_LENGTH);
		this.html = html;
		needsInit = true;
		flagPendingAjax();
		fireOnHtmlChanged(orig, html);
		return this;
	}

	private void fireOnHtmlChanged(String orig, String html) {
		for (HtmlPortletListener i : this.listeners)
			i.onHtmlChanged(orig, html);
	}
	public void setJavascript(String js) {
		if (OH.eq(this.js, js))
			return;
		this.js = js;
		this.needsInit = true;
	}
	public HtmlPortlet setCssClass(String cssClass) {
		if (OH.eq(this.cssClass, cssClass))
			return this;
		this.cssClass = cssClass;
		needsCss = true;
		flagPendingAjax();
		return this;
	}
	public HtmlPortlet setCssStyle(String cssStyle) {
		if (OH.eq(this.cssStyle, cssStyle))
			return this;
		this.cssStyle = cssStyle;
		needsCss = true;
		flagPendingAjax();
		return this;
	}

	protected void onUserClick() {
		for (HtmlPortletListener i : this.listeners)
			i.onUserClick(this);
	}
	protected void onUserCallback(String id, int mouseX, int mouseY, Callback cb) {
		for (HtmlPortletListener i : this.listeners)
			i.onUserCallback(this, id, mouseX, mouseY, cb);
	}

	@Override
	public PortletSchema<? extends HtmlPortlet> getPortletSchema() {
		return SCHEMA;
	}

	public static class Builder extends AbstractPortletBuilder<HtmlPortlet> {

		private static final String ID = "Html";

		public Builder() {
			super(HtmlPortlet.class);
		}

		@Override
		public HtmlPortlet buildPortlet(PortletConfig portletManager) {
			return new HtmlPortlet(portletManager);
		}

		@Override
		public String getPortletBuilderName() {
			return "Html";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	public void addListener(HtmlPortletListener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(HtmlPortletListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket origin, InterPortletMessage message) {
		SetHtmlInterPortletMessage showMessage = (SetHtmlInterPortletMessage) message;
		setHtml(showMessage.getHtml());
	}

	public WebHtmlContextMenuFactory getContextMenuFactory() {
		return contextMenuFactory;
	}

	public void setContextMenuFactory(WebHtmlContextMenuFactory contextMenuFactory) {
		this.contextMenuFactory = contextMenuFactory;
		if ((this.contextMenuFactory == null) != (contextMenuFactory == null)) {
			flagPendingAjax();
			configChanged = true;
		}
	}

	public void addMenuContextListener(WebHtmlContextMenuListener listener) {
		menuListeners.add(listener);
	}

	public boolean removeMenuContextListener(WebHtmlContextMenuListener listener) {
		return menuListeners.remove(listener);
	}
	private void fireOnMenu(String action) {
		for (WebHtmlContextMenuListener ml : menuListeners)
			ml.onContextMenu(this, action);
	}

	public String generateCallback(String string) {
		int n = this.callbacksBuf.size() + this.callbacks.size();
		Callback value = new Callback(string);
		this.callbacksBuf.put(n, value);
		return "callbackHtmlPortlet(event,this," + n + ")";
	}
	public String generateCallback(Callback cb) {
		int n = this.callbacksBuf.size() + this.callbacks.size();
		this.callbacksBuf.put(n, cb);
		return "callbackHtmlPortlet(event,this," + n + ")";
	}

	public static class Callback implements Lockable {
		final private String id;
		private Map<String, Object> attributes = null;
		private HtmlPortletListener listener;
		private boolean locked;

		public Callback(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}

		public Map<String, Object> getAttributes() {
			return attributes == null ? Collections.EMPTY_MAP : attributes;
		}

		public HtmlPortletListener getListener() {
			return listener;
		}
		public Callback setListener(HtmlPortletListener listener) {
			LockedException.assertNotLocked(this);
			this.listener = listener;
			return this;
		}

		public Object getAttribute(String key) {
			return this.attributes == null ? null : this.attributes.get(key);
		}
		public Callback addAttribute(String key, Object value) {
			LockedException.assertNotLocked(this);
			if (this.attributes == null)
				this.attributes = new HashMap<String, Object>();
			this.attributes.put(key, value);
			return this;

		}

		@Override
		public void lock() {
			this.locked = true;
		}

		@Override
		public boolean isLocked() {
			return locked;
		}

	}

	public void addCustomCallbackListener(HtmlPortletCustomCallbackListener formPortletListener) {
		customCallbackListeners.add(formPortletListener);
	}

	public void removeCustomCallbackListener(HtmlPortletCustomCallbackListener formPortletListener) {
		customCallbackListeners.remove(formPortletListener);
	}
}
