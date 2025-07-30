/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.portal.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.base.Action;
import com.f1.container.ResultMessage;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.peripheral.MouseEvent;
import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletContainer;
import com.f1.suite.web.portal.PortletDownload;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletListener;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.PortletSocketListener;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;

public abstract class AbstractPortlet implements Portlet, PortletSocketListener {
	private static final Logger log = LH.get();

	public static final String CALLBACK_REPLACECHILD = "replaceChild";

	final private String portletId;
	private PortletContainer parent;
	final private List<PortletListener> portletListeners = new ArrayList<PortletListener>();
	final private String jsObjectName;
	final private JsFunction jsFunction;
	private boolean visible = false;
	final private PortletManager manager;
	private boolean pendingAjax = false;
	private Map<String, PortletSocket> sockets = null;
	private int width;
	private int height;
	private int initWidth = -1;
	private int initHeight = -1;
	final private PortletConfig portletConfig;
	private String title = getClass().getSimpleName();
	private String htmlIdSelector;//this would be used for testing
	private boolean htmlIdSelectorChanged;
	private boolean htmlCssClassChanged;
	private String htmlCssClass;

	public AbstractPortlet(PortletConfig portletConfig) {
		this.portletConfig = portletConfig;
		this.manager = portletConfig.getPortletManager();
		this.portletId = portletConfig.getPortletId();
		this.jsObjectName = "g(\'" + portletId + "\')";
		this.jsFunction = new JsFunction(getJsObjectName());
	}

	@Override
	public PortletConfig getPortletConfig() {
		return portletConfig;
	}

	@Override
	public String getPortletId() {
		return portletId;
	}

	@Override
	public PortletContainer getParent() {
		return parent;
	}

	@Override
	public void setParent(PortletContainer parent) {
		if (this.parent == parent)
			return;
		PortletContainer oldParent = this.parent;
		this.parent = parent;
		for (int i = 0, l = portletListeners.size(); i < l; i++)
			portletListeners.get(i).onPortletParentChanged(this, oldParent);
	}

	public String getJsObjectName() {
		return jsObjectName;
	}

	@Override
	public void addPortletListener(PortletListener portletListener) {
		if (portletListeners.contains(portletListener))
			return;
		portletListeners.add(portletListener);
		if (pendingAjax)
			portletListener.onJavascriptQueued(this);
	}

	@Override
	public void removePortletListener(PortletListener portletListener) {
		portletListeners.remove(portletListener);
	}

	protected List<PortletListener> getPortletListeners() {
		return portletListeners;
	}

	protected void flagPendingAjax() {
		if (pendingAjax)
			return;
		pendingAjax = true;
		for (int i = 0, l = portletListeners.size(); i < l; i++)
			portletListeners.get(i).onJavascriptQueued(this);
	}

	protected JsFunction callJsFunction(String functionName) {
		return callJsFunction(manager.getPendingJs(), functionName);
	}
	protected JsFunction callJsFunction(StringBuilder sink, String functionName) {
		return jsFunction.reset(sink, functionName);
	}

	@Override
	public void drainJavascript() {
		pendingAjax = false;
		if (this.htmlIdSelectorChanged) {
			this.htmlIdSelectorChanged = false;
			if (visible)
				this.callJsFunction("setHIDS").addParamQuoted(this.htmlIdSelector).end();
		}
		if (this.htmlCssClassChanged) {
			this.htmlCssClassChanged = false;
			if (visible)
				this.callJsFunction("setHCSC").addParamQuoted(this.htmlCssClass).end();
		}
	}

	@Override
	public void setVisible(boolean isVisible) {
		if (this.visible == isVisible)
			return;
		this.visible = isVisible;
		if (visible) {
			initJs();
			onVisibilityChanged(true);
		} else {
			closeJs();
			onVisibilityChanged(false);
			pendingAjax = false;
		}
		fireLocationChanged();
	}

	protected void onVisibilityChanged(boolean isVisible) {
	}

	protected void closeJs() {
		StringBuilder js = manager.getPendingJs();
		js.append("rmp('").append(getPortletId()).append("');").append(SH.NEWLINE);
	}

	protected void initJs() {
		StringBuilder js = manager.getPendingJs();
		js.append("portletManager.putPortlet(new ").append(this.getPortletSchema().getJsPrototype()).append("(");
		WebHelper.quote(getPortletId(), js);
		js.append("));").append(SH.NEWLINE);
		if (getPortletConfig().getBuilderId() != null)
			js.append(getJsObjectName()).append(".portlet.setUserSelectable(true);").append(SH.NEWLINE);
		if (width != 0 && height != 0)
			callJsSetSize();

		Map<String, PortletSocket> s = getSockets();
		if (CH.isntEmpty(s)) {
			Map<String, List<String>> socketsMap = new HashMap<String, List<String>>();
			for (Map.Entry<String, PortletSocket> e : s.entrySet()) {
				PortletSocket socket = e.getValue();
				if (socket.getIsInitiator() == false)
					continue;
				String name = e.getKey();
				socketsMap.put(name, CH.l(formatText(socket.getTitle())));
			}
			if (!socketsMap.isEmpty())
				callJsFunction("setSockets").addParamJson(socketsMap).end();
		}
		RootPortlet root = PortletHelper.findParentByType(this, RootPortlet.class);
		if (root != null) {
			this.callJsFunction("setOwningWindowId").addParam(root.getWindowId()).end();
		}
		this.htmlIdSelectorChanged = false;
		if (SH.is(this.htmlIdSelector)) {
			this.callJsFunction("setHIDS").addParamQuoted(this.htmlIdSelector).end();
		}
		this.htmlCssClassChanged = false;
		if (SH.is(this.htmlCssClass)) {
			this.callJsFunction("setHCSC").addParamQuoted(this.htmlCssClass).end();
		}
	}

	@Override
	public void setHtmlIdSelector(String his) {
		if (OH.eq(this.htmlIdSelector, his))
			return;
		this.htmlIdSelector = his;
		if (getVisible()) {
			this.htmlIdSelectorChanged = true;
			flagPendingAjax();
		}
	}

	@Override
	public String getHtmlIdSelector() {
		return this.htmlIdSelector;
	}

	protected void callJsSetSize() {
		callJsFunction("setSize").addParam(getWidth()).addParam(getHeight()).end();
	}

	@Override
	final public boolean getVisible() {
		return visible;
	}

	@Override
	public PortletManager getManager() {
		return manager;
	}

	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		if ("replaceChild".equals(callback)) {
			final PortletContainer parent = getParent();
			final String childType = CH.getOrThrow(Caster_String.INSTANCE, attributes, "childType");
			final Portlet newChild = getManager().buildPortlet(childType);
			parent.replaceChild(getPortletId(), newChild);
			onClosed();
		} else if ("insertParent".equals(callback)) {
			final String childType = CH.getOrThrow(Caster_String.INSTANCE, attributes, "childType");
			final Portlet newChild = getManager().buildPortlet(childType);
			final PortletContainer parent = getParent();
			PortletContainer pc = (PortletContainer) newChild;
			parent.replaceChild(getPortletId(), newChild);
			Portlet firstChild = pc.getChildrenCount() == 0 ? null : pc.getChildren().values().iterator().next();
			if (firstChild instanceof BlankPortlet) {
				pc.replaceChild(firstChild.getPortletId(), this);
			} else {
				pc.addChild(this);
			}
		} else if ("showWrapPortletDialog".equals(callback)) {
			PortletBuilderPortlet pbp = new PortletBuilderPortlet(generateConfig(), true);
			pbp.setPortletIdOfParentToAddPortletTo(getParent().getPortletId());
			pbp.setPortletIdOfPortletToWrap(getPortletId());
			getManager().showDialog("Wrap Portlet", pbp);
		} else if ("connectSocket".equals(callback)) {
			final String targetPortletId = CH.getOrThrow(Caster_String.INSTANCE, attributes, "targetPortletId");
			final String sourceSocket = CH.getOrThrow(Caster_String.INSTANCE, attributes, "sourceSocket");
			PortletSocket socket = CH.getOrThrow(getSockets(), SH.afterLast(sourceSocket, '/'));
			Collection<PortletSocket> remoteSockets = manager.getPortlet(targetPortletId).getSockets().values();
			boolean connected = false;
			for (PortletSocket remoteSocket : remoteSockets) {
				if (socket.canConnectTo(remoteSocket)) {
					if (!socket.canAcceptMoreConnections()) {
						getManager().showAlert(formatText("This Link does not support multiple connections"));
					} else if (!remoteSocket.canAcceptMoreConnections()) {
						getManager().showAlert(formatText("The destination portlet does not support multiple connections"));
					} else if (socket.getRemoteConnections().contains(remoteSocket)) {
						getManager().showAlert(formatText("These portlets are already connected to each other"));
					} else if (socket.getIsInitiator()) {
						socket.connectTo(remoteSocket);
					} else {
						remoteSocket.connectTo(socket);
					}
					connected = true;
					break;
				}
			}
			if (!connected)
				getManager().showAlert(formatText("The selected link is invalid"));
		} else {
			throw new RuntimeException("unknown callback:" + callback);
		}
	}

	@Override
	public void resetVisibility() {
		this.visible = false;
	}

	public <T> T nw(Class<T> clazz) {
		return getManager().getGenerator().nw(clazz);
	}

	@Override
	public void onClosed() {
		for (PortletListener listener : new ArrayList<PortletListener>(portletListeners))
			listener.onPortletClosed(this);
		Map<String, PortletSocket> s = getSockets();
		if (CH.isntEmpty(s))
			for (PortletSocket socket : s.values()) {
				if (!socket.getRemoteConnections().isEmpty()) {
					for (PortletSocket remoteSocket : new ArrayList<PortletSocket>(socket.getRemoteConnections())) {
						if (socket.getIsInitiator()) {
							socket.disconnectFrom(remoteSocket);
						} else {
							remoteSocket.disconnectFrom(socket);
						}
					}
				}
			}
	}

	public Map<String, PortletSocket> getSockets() {
		return sockets;
	}

	public BasicPortletSocket addSocket(boolean isInitator, String name, String title, boolean supportsMultipleConnections, Set outboundMessageTypes, Set inboundMessageTypes) {
		if (sockets == null)
			sockets = new HashMap<String, PortletSocket>();
		if (outboundMessageTypes == null)
			outboundMessageTypes = Collections.emptySet();
		if (inboundMessageTypes == null)
			inboundMessageTypes = Collections.emptySet();
		BasicPortletSocket socket = new BasicPortletSocket(this, isInitator, name, title, supportsMultipleConnections, outboundMessageTypes, inboundMessageTypes);
		CH.putOrThrow(sockets, socket.getName(), socket);
		socket.addListener(this);
		return socket;
	}

	public void onInterPortletMessage(PortletSocket localSocket, PortletSocket remoteSocket, InterPortletMessage message) {
		LH.info(log, "Unhandled inter-portlet message from " + remoteSocket + " --> " + localSocket + ": " + message);
	}

	@Override
	public void setSize(int width, int height) {
		if (this.width == width && this.height == height)
			return;
		this.width = width;
		this.height = height;

		if (visible) {
			callJsSetSize();
			fireLocationChanged();
		}
		onSizeChanged(width, height);
	}

	protected void onSizeChanged(int width, int height) {
	}

	@Override
	final public int getWidth() {
		return width;
	}

	@Override
	final public int getHeight() {
		return height;
	}

	@Override
	public void onDisconnect(PortletSocket localSocket, PortletSocket remoteSocket) {
	}

	@Override
	public void onConnect(PortletSocket localSocket, PortletSocket remoteSocket) {
	}

	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket origin, InterPortletMessage message) {
	}

	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = new LinkedHashMap<String, Object>();
		return r;
	}

	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToPortletIdMapping, StringBuilder sb) {
		Integer configWidth = CH.getOr(Caster_Integer.INSTANCE, configuration, "width", null);
		Integer configHeight = CH.getOr(Caster_Integer.INSTANCE, configuration, "height", null);
		if (configWidth != null && configHeight != null) {
			this.initWidth = configWidth;
			this.initHeight = configHeight;
			setSize(configWidth, configHeight);
		}
	}

	public String formatText(String text) {
		return getManager().getTextFormatter().format(text);
	}

	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return -1;
	}

	@Override
	public int getSuggestedHeight(PortletMetrics pm) {
		return -1;
	}
	@Override
	public void close() {
		if (getParent() != null)
			getParent().removeChild(getPortletId());
		onClosed();
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		if (OH.eq(this.title, title))
			return;
		this.title = title;
		final String oldTitle = this.title;
		for (int i = 0, l = portletListeners.size(); i < l; i++)
			portletListeners.get(i).onPortletRenamed(this, oldTitle, title);
	}

	public PortletConfig generateConfig() {
		return manager.generateConfig();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [" + getPortletId() + "]";
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		LH.w(log, "Unhandled backend message for portlet: ", this, " message: ", result);
	}
	private void fireLocationChanged() {
		for (PortletListener listener : this.portletListeners)
			listener.onLocationChanged(this);
	}

	protected String generateErrorTicket() {
		return getManager().getTools().generateErrorTicket();
	}

	@Override
	public PortletDownload handleContentRequest(String callback, Map<String, String> attributes) {
		return null;
	}

	@Override
	public void onUserRequestFocus(MouseEvent me) {
		getManager().focusPortlet(this);
	}

	@Override
	public boolean onUserKeyEvent(KeyEvent keyEvent) {
		if (parent != null)
			return parent.onUserKeyEvent(keyEvent);
		else
			return false;
	}

	@Override
	public boolean onUserMouseEvent(MouseEvent mouseEvent) {
		if (parent != null)
			return parent.onUserMouseEvent(mouseEvent);
		else
			return false;
	}

	public int getInitWidth() {
		return this.initWidth;
	}
	public int getInitHeight() {
		return this.initHeight;
	}

	public String getUserName() {
		return this.manager.getUserName();
	}

	@Override
	public String getHtmlCssClass() {
		return this.htmlCssClass;
	}

	@Override
	public void setHtmlCssClass(String t) {
		if (OH.eq(this.htmlCssClass, t))
			return;
		this.htmlCssClass = t;
		if (getVisible()) {
			this.htmlCssClassChanged = true;
			flagPendingAjax();
		}
	}
}
