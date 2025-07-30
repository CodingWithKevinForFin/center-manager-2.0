/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.portal.impl;

import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.f1.base.Action;
import com.f1.base.IterableAndSize;
import com.f1.base.Message;
import com.f1.base.ObjectGenerator;
import com.f1.container.ContainerTools;
import com.f1.container.Partition;
import com.f1.container.RequestOutputPort;
import com.f1.container.ResultMessage;
import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.http.HttpUtils;
import com.f1.suite.web.HttpRequestAction;
import com.f1.suite.web.HttpWebSuite;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.PortalHttpStateCreator;
import com.f1.suite.web.WebState;
import com.f1.suite.web.WebStatesManager;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.peripheral.MouseEvent;
import com.f1.suite.web.portal.BackendResponseListener;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletBackend;
import com.f1.suite.web.portal.PortletBuilder;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletContainer;
import com.f1.suite.web.portal.PortletDownload;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletListener;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.PortletManagerListener;
import com.f1.suite.web.portal.PortletManagerRestCallResponseListener;
import com.f1.suite.web.portal.PortletManagerSecurityModel;
import com.f1.suite.web.portal.PortletMenuManager;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.PortletNotificationListener;
import com.f1.suite.web.portal.PortletService;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.PortletUserConfigStore;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.style.PortletStyleManager;
import com.f1.utils.AH;
import com.f1.utils.BundledTextFormatter;
import com.f1.utils.CH;
import com.f1.utils.ColorHelper;
import com.f1.utils.ConvertedException;
import com.f1.utils.CopyOnWriteHashMap;
import com.f1.utils.FastPrintStream;
import com.f1.utils.GuidHelper;
import com.f1.utils.LH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.assist.RootAssister;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Byte;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.IndexedList;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.LongKeyMap.Node;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.Tuple3;

public class BasicPortletManager implements PortletManager {
	private static final int CONCURRENT_SESSIONS_MS = 500;
	private static final String CONCURRENT_CLIENT_LOGGINS = "CONCURRENT_CLIENT_LOGGINS";
	public static final String WINDOWID = "F1WID";
	public static final String PAGEID = "F1PGID";
	public static final String KEEP_EXISTING_OPEN = "KEEP_EXISTING_SESSION";
	public static final String PRELOGINID = "PRELOGINID";

	public static final String URL_PORTAL = "/3forge";
	public static final String URL_START = "/start";
	public static final String URL_END = "/end";
	public static final String URL_AJAXL = "/portal.ajax";
	public static final String URL_CUSTOM_CSS = "/custom.css";

	private static final PortletListener[] EMPTY_PORTLET_LISTENERS = new PortletListener[0];
	private static final String PORTAL_PRELOGIN_PARAMS = "PORTAL_PRELOGIN_PARAMS";
	private static final String GENERAL_ERROR_MESSAGE = "general.error.message";
	private static final String GENERAL_ERROR_EMAIL_TO = "general.error.emailTo";
	private static final String GENERAL_ERROR_EMAIL_SUBJECT = "general.error.emailSubject";
	private static final String GENERAL_ERROR_EMAIL_BODY = "general.error.emailBody";
	private static final String PROPERTY_LOADING_BAR_STYLES = "web.loadingbar.styles";

	private static final String PORTLET_DEBUG_LAYOUT_ENABLED = "portlet.debug.layout.enabled";

	private static final int DEFAULT_POLLING_MS = 150;

	private PortletListener portletListeners[] = EMPTY_PORTLET_LISTENERS;
	private PortletManagerListener[] portletManagerListeners = new PortletManagerListener[0];
	final private List<PortletNotificationListener> portletNotificationListeners = new ArrayList<PortletNotificationListener>();
	private static final Logger log = Logger.getLogger(BasicPortletManager.class.getName());
	private static final int PORTLET_ID_LENGTH = 10;
	private Map<String, RequestOutputPort<?, ?>> serviceRequestPorts = new HashMap<String, RequestOutputPort<?, ?>>();
	private RootPortlet root;
	private LongKeyMap<RootPortlet> popupRoots = new LongKeyMap<RootPortlet>();
	private Map<String, RootPortlet> popupRootsByPortletId = new HashMap<String, RootPortlet>();

	final private Map<String, Portlet> managedPortlets = new HashMap<String, Portlet>();

	final private Set<String> portletsWithQueuedJs = new LinkedHashSet<String>();
	final private StringBuilder pendingJs = new StringBuilder();
	final private WebState state;
	private HttpRequestResponse action;
	private Map<String, PortletBuilder> portletBuilders = new HashMap<String, PortletBuilder>();
	private ObjectGenerator generator;
	private Map<String, PortletService> services = new CopyOnWriteHashMap<String, PortletService>();
	private PortletBackend backend;
	private LocaleFormatter formatter;
	private long pageUid;
	private int pollingMs = DEFAULT_POLLING_MS;
	private ObjectToJsonConverter jsonConverter;
	private Map<String, Object> metadata = new LinkedHashMap<String, Object>();
	private IndexedList<String, Portlet> focusedPortlets = new BasicIndexedList<String, Portlet>();//last (highest index) has focus
	private final PortletMenuManager menuManager;

	private JsFunction tmpFunction = new JsFunction();

	private List<PortletNotification> pendingNotifications = new ArrayList<PortletNotification>();
	private int nextNotificationId = 0;
	private Map<String, PortletNotification> openNotifications = new HashMap<String, PortletNotification>();
	private PortletManagerSecurityModel securityModel;

	final private String defaultBrowserTitle;
	final private String buildVersion;

	public static final long DEFAULT_AJAX_LOADING_TIMEOUT_MS = 2000;
	public static final String DEFAULT_PORTAL_DIALOG_HEADER_TITLE = "";
	private long ajaxLoadingTimeoutMs = DEFAULT_AJAX_LOADING_TIMEOUT_MS;
	private long ajaxLoadingCheckPeriodMs = DEFAULT_AJAX_LOADING_TIMEOUT_MS / 10;
	private String portalDialogHeaderTitle = DEFAULT_PORTAL_DIALOG_HEADER_TITLE;
	private HashMap<String, String> loadingBarStyle = new HashMap<String, String>();
	final private LinkedHashMap<String, String> urlParams = new LinkedHashMap<String, String>();

	public BasicPortletManager(HttpRequestResponse request, WebState state, ObjectGenerator generator, PortletBackend backend, String buildVersion, LocaleFormatter f) {
		resetNow();
		state.setPortletManager(this);
		this.buildVersion = buildVersion;
		this.menuManager = new BasicPortletMenuManager(this);
		this.securityModel = new BasicPortletManagerSecurityModel(this);
		this.state = state;
		this.generator = generator;
		this.tools = state.getPartition().getContainer().getTools();
		this.defaultBrowserTitle = this.tools.getOptional(HttpWebSuite.PROPERTY_WEB_TITLE, HttpWebSuite.DEFAULT_TITLE);
		this.root = new RootPortlet(new BasicPortletConfig(this, generateId(), null, false), 0);
		focusPortlet(root);
		onPortletAdded(this.root);
		this.backend = backend;
		this.backend.subscribe((String) state.getPartitionId());
		this.formatter = f;
		this.textFormatter = formatter.getBundledTextFormatter();
		this.jsonConverter = new ObjectToJsonConverter();
		this.jsonConverter.setStrictValidation(true);
		String rawStyles = getTools().getOptional(PROPERTY_LOADING_BAR_STYLES);
		Map<String, String> parsedStyles = SH.splitToMap(",", "=", rawStyles);
		for (Entry<String, String> e : parsedStyles.entrySet()) {
			String color = e.getValue();
			// validate color
			if (ColorHelper.checkColor(color))
				this.loadingBarStyle.put(e.getKey(), color);
		}
		LH.info(log, "Created Portlet Manager for: ", describeUser());
	}
	@Override
	public void focusPortlet(Portlet p) {
		Portlet old = getFocusedPortlet();
		if (old == p)
			return;
		if (this.focusedPortlets.getSize() > 0 && CH.last(this.focusedPortlets) == p)
			return;
		this.focusedPortlets.removeNoThrow(p.getPortletId());
		this.focusedPortlets.add(p.getPortletId(), p);
		if (this.focusedField != null && this.focusedField.getA() == p)
			this.focusedField = null;
	}

	public void setPollingMs(int pollingMs) {
		this.pollingMs = MH.clip(pollingMs, 10, 15000);
		if (this.root.getVisible())
			tmpFunction.reset(this.pendingJs, "portletManager", "setPolling").addParam(this.pollingMs).end();

	}

	private void resetNow() {
		this.now = -1L;
	}

	private GuidHelper gh = new GuidHelper();

	@Override
	public String generateId() {
		for (;;) {
			gh.getRandomGUID(62, SH.clear(tmpSb));
			SH.shuffle(tmpSb, gh.getRandom());
			String r = SH.substring(tmpSb, 0, PORTLET_ID_LENGTH);//with 10 digits, You'd need 130,000,000 panels to have a 1% chance of collision
			if (!SH.areBetween(r, '0', '9'))
				return "GG" + r;
		}
	}
	static public String generateIdStatic() {
		GuidHelper gh = new GuidHelper();
		StringBuilder tmpSb = new StringBuilder();
		for (;;) {
			gh.getRandomGUID(62, SH.clear(tmpSb));
			SH.shuffle(tmpSb, gh.getRandom());
			String r = SH.substring(tmpSb, 0, PORTLET_ID_LENGTH);//with 10 digits, You'd need 130,000,000 panels to have a 1% chance of collision
			if (!SH.areBetween(r, '0', '9'))
				return "GG" + r;
		}
	}

	@Override
	public void onPortletAdded(Portlet newPortlet) {
		if (!CH.putOrThrow(managedPortlets, newPortlet.getPortletId(), newPortlet))
			return;
		newPortlet.addPortletListener(this);
		for (PortletListener l : portletListeners)
			l.onPortletAdded(newPortlet);
		if (newPortlet instanceof PortletContainer) {
			PortletContainer pc = (PortletContainer) newPortlet;
			for (Portlet p : pc.getChildren().values())
				onPortletAdded(p);
		}
	}

	@Override
	public void onPortletRenamed(Portlet portlet, String oldName, String newName) {
		for (PortletListener l : portletListeners)
			l.onPortletRenamed(portlet, oldName, newName);
	}

	@Override
	public void onPortletClosed(Portlet removedPortlet) {
		CH.removeOrThrow(managedPortlets, removedPortlet.getPortletId());
		removedPortlet.removePortletListener(this);
		portletsWithQueuedJs.remove(removedPortlet.getPortletId());
		focusedPortlets.removeNoThrow(removedPortlet.getPortletId());
		if (this.focusedField != null && this.focusedField.getA() == removedPortlet)
			this.focusedField = null;
		for (PortletListener l : portletListeners)
			l.onPortletClosed(removedPortlet);
	}
	@Override
	public void onSocketConnected(PortletSocket localHost, PortletSocket remoteHost) {
		for (PortletListener l : portletListeners)
			l.onSocketConnected(localHost, remoteHost);
	}
	@Override
	public void onSocketDisconnected(PortletSocket localHost, PortletSocket remoteHost) {
		for (PortletListener l : portletListeners)
			l.onSocketDisconnected(localHost, remoteHost);
	}

	@Override
	public void onPortletParentChanged(Portlet newPortlet, PortletContainer oldParent) {
		for (PortletListener l : portletListeners)
			l.onPortletParentChanged(newPortlet, oldParent);
	}

	@Override
	public void onJavascriptQueued(Portlet portlet) {
		portletsWithQueuedJs.add(portlet.getPortletId());
		for (PortletListener l : portletListeners)
			l.onJavascriptQueued(portlet);
	}
	private void fireInit() {
		for (PortletManagerListener listener : this.portletManagerListeners)
			try {
				listener.onPageRefreshed(this);
			} catch (Exception e) {
				LH.warning(log, this.getUserName(), " received listener error", e);
			}
		for (PortletManagerRestCallResponseListener listener : this.restCallListeners.values())
			try {
				listener.onRestCallResponse(PortletManagerRestCallResponseListener.CODE_NO_RESPONSE, null);
			} catch (Exception e) {
				LH.warning(log, this.getUserName(), " received listener error", e);
			}
		this.restCallListeners.clear();

	}
	private void fireMetadataChanged() {
		for (PortletManagerListener listener : this.portletManagerListeners) {
			try {
				listener.onMetadataChanged(this);
			} catch (Exception e) {
				LH.warning(log, this.getUserName(), " received listener error", e);
			}
		}
	}

	@Override
	public long getPageUid() {
		return this.pageUid;
	}

	@Override
	public RootPortlet getRoot() {
		return root;
	}

	private final ArrayList<String> tmp = new ArrayList<String>();
	private final StringBuilder tmpSb = new StringBuilder();
	private HttpRequestResponse lastAction;

	@Override
	public void drainPendingJs(StringBuilder sb) {
		resetNow();
		if (pendingJs.length() > 0) {
			sb.append(pendingJs);
			clearPendingJs();
		}
		tmp.clear();
		tmp.addAll(portletsWithQueuedJs);
		portletsWithQueuedJs.clear();
		for (int i = 0, l = tmp.size(); i < l; i++) {
			String portletId = tmp.get(i);
			try {
				Portlet p = managedPortlets.get(portletId);
				if (p == null) {
					LH.warning(log, this.getUserName(), " skipping removed portlet with pending js: ", portletId);
					continue;
				}
				p.drainJavascript();
				sb.append(pendingJs);
				clearPendingJs();
			} catch (RuntimeException e) {
				LH.severe(log, this.getUserName(), " received Error draining Pending js for portlet ", portletId, e);
				portletsWithQueuedJs.add(portletId);
			}
		}
		if (pendingDownloadsCount > 0) {
			tmpFunction.reset(sb, "portletManager", "downloadFile").end();
			pendingDownloadsCount--;
		}
		while (!pendingAudio.isEmpty())
			tmpFunction.reset(sb, "portletManager", "playAudio").addParamQuoted(pendingAudio.remove()).end();
		tmpFunction.reset(sb, "portletManager", "onJsProcessed").addParam(this.portletsWithQueuedJs.isEmpty() ? null : 1).end();
		if (urlParamsChanged) {
			tmpFunction.reset(sb, null, "setBrowserURL").addParamQuoted(this.buildUrlParams(true)).end();
			this.urlParamsChanged = false;
		}
		if (requestFocuseOnField != null) {
			FormPortlet form = (FormPortlet) managedPortlets.get(requestFocuseOnField.getA().getPortletId());
			if (form != null && form.getVisible() && !portletsWithQueuedJs.contains(form.getPortletId())) {
				FormPortletField<?> field = form.getField(requestFocuseOnField.getB());
				if (field != null && field.isVisible())
					tmpFunction.reset(pendingJs, "portletManager", "focusField").addParamQuoted(requestFocuseOnField.getA().getPortletId())
							.addParamQuoted(requestFocuseOnField.getB()).end();
				this.requestFocuseOnField = null;
			}
		}
	}

	@Override
	public Portlet getPortlet(String portletId) {
		return CH.getOrThrow(managedPortlets, portletId, "PortletId not found");
	}

	private void init(int width, int height, long windowId) {
		this.menuManager.resetIds();
		resetNow();
		if (windowId == 0) {
			this.pendingDownloadsCount = this.pendingDownloads.size();
			if (!this.popupRoots.isEmpty()) {
				for (Node<RootPortlet> i : this.popupRoots) {
					RootPortlet removedRoot = i.getValue();
					onPortletClosed(removedRoot);
					removedRoot.setVisible(false);
					removedRoot.fireOnPopupWindowClosed();
					removedRoot.close();
				}
				this.popupRoots.clear();
				this.popupRootsByPortletId.clear();
			}
			pageUid = MH.abs(new SecureRandom().nextInt());
			root.resetVisibility();
			root.setSize(width, height);
			clearPendingJs();
			drainPendingJs(pendingJs);
			clearPendingJs();
			pendingJs.append("portletManager=");
			String ajaxSafeMode = this.getTools().getOptional("ajax.safe.mode");
			tmpFunction.reset(pendingJs, "", "new PortletManager").addParamQuoted(URL_AJAXL).addParam(pageUid).addParam(pollingMs).addParamQuoted(ajaxSafeMode)
					.addParam(ajaxLoadingTimeoutMs).addParam(ajaxLoadingCheckPeriodMs).addParamQuoted(portalDialogHeaderTitle).addParamQuoted(buildVersion).end();
			fireInit();
		} else {
			RootPortlet popupRoot = popupRoots.get(windowId);
			if (popupRoot == null) {
				SH.clear(getPendingJs()).append("window.close();");
				LH.fine(log, getUserName(), ": popoutRoot is null so redirectToLogin");
				SH.clear(getPendingJs()).append("redirectToLogin();");
				return;
			}
			popupRoot.resetVisibility();
			popupRoot.setSize(width, height);
			//			clearPendingJs();
			//			drainPendingJs(pendingJs);
			//			clearPendingJs();
		}
		tmpFunction.reset(pendingJs, null, "postInit").addParam(windowId).end();
		if (this.loadingBarStyle.size() > 0) {
			tmpFunction.reset(pendingJs, "portletManager", "storeLoadingDialogStyle").addParamJson(this.loadingBarStyle).end();
		}
	}
	private void clearPendingJs() {
		if (pendingJs.length() > 100000) {
			pendingJs.setLength(1000);
			pendingJs.trimToSize();
		}
		pendingJs.setLength(0);
	}

	@Override
	public StringBuilder getPendingJs() {
		return pendingJs;
	}

	@Override
	public void setBreakJs() {
		pendingJs.append("debugger;\n");
	}

	@Override
	public WebState getState() {
		return state;
	}

	@Override
	public void setCurrentAction(HttpRequestResponse action) {
		if (action != null)
			this.lastAction = action;
		this.action = action;
	}

	@Override
	public HttpRequestResponse getLastRequestAction() {
		return this.lastAction;
	}

	@Override
	public HttpRequestResponse getCurrentRequestAction() {
		return this.action;
	}

	@Override
	public ObjectGenerator getGenerator() {
		return generator;
	}

	private Map<Class, PortletService[]> messageToServiceCache = new HashMap<Class, PortletService[]>();
	final private BundledTextFormatter textFormatter;
	private PortletMetrics portletMetrics = new BasicPortletMetrics();
	private PortletUserConfigStore userConfigStore;
	private boolean isLoadingConfig;

	private Portlet callbackTarget;
	private List<Tuple3<String, Throwable, ConfirmDialogPortlet>> deferedAlerts = new ArrayList<Tuple3<String, Throwable, ConfirmDialogPortlet>>();

	@Override
	public boolean getIsLoadingConfig() {
		return isLoadingConfig;
	}
	@Override
	public void registerService(PortletService service) {
		CH.putOrThrow(services, service.getServiceId(), service);
		messageToServiceCache.clear();
	}

	@Override
	public PortletService getService(String id) {
		return CH.getOrThrow(services, id);
	}
	@Override
	public PortletService getServiceNoThrow(String id) {
		return services.get(id);
	}

	@Override
	public void addPortletListener(PortletListener listener) {
		this.portletListeners = AH.append(portletListeners, listener);
		LH.fine(log, "PortletManager Add Listener: ", SH.toObjectStringSimple(listener), " Count: ", AH.length(this.portletListeners));
	}

	@Override
	public void removePortletlistener(PortletListener listener) {
		this.portletListeners = AH.remove(portletListeners, listener);
		LH.fine(log, "PortletManager Remove Listener: ", SH.toObjectStringSimple(listener), " Count: ", AH.length(this.portletListeners));
	}

	@Override
	public boolean getIsOpen() {
		return this.closePortletManagerAction == null && !closed;
	}

	@Override
	public void onBackendAction(Action action) {
		resetNow();
		if (this.closePortletManagerAction == action) {
			this.closePortletManagerAction = null;
			this.close();
		}

		if (this.portletManagerListeners.length > 0)
			for (PortletManagerListener i : this.portletManagerListeners)
				i.onBackendCalled(this, action);
		PortletService[] l = messageToServiceCache.get(action.getClass());
		if (l == null) {
			ArrayList<PortletService> l2 = new ArrayList<PortletService>();
			for (PortletService ps : services.values()) {
				INNER: for (Class<? extends Action> a : ps.getInterestedBackendMessages()) {
					if (a.isInstance(action)) {
						l2.add(ps);
						break INNER;
					}
				}
			}
			l = AH.toArray(l2, PortletService.class);
			messageToServiceCache.put(action.getClass(), l);
		}
		if (l.length > 0)
			for (PortletService ps : l)
				ps.onBackendAction(action);
		else
			LH.warning(log, "No services interested in message of type: ", action.getClass().getName(), " for user: ", this.getUserName());
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result, Object correlationId) {
		resetNow();
		boolean processedByPortlet = false;
		if (correlationId != null) {
			if (correlationId instanceof BackendResponseListener) {
				((BackendResponseListener) correlationId).onBackendResponse(result);
				processedByPortlet = true;
			} else {
				String portletId = (String) correlationId;
				Portlet target = managedPortlets.get(portletId);
				if (target == null) {
					LH.warning(log, "Portlet no longer alive for response to request for user ", this.getUserName(), ": ", result);
				} else {
					target.onBackendResponse(result);
					processedByPortlet = true;
				}
			}
		} else {
			if (result.getError() != null) {
				String ticket = getTools().generateErrorTicket();
				LH.info(log, this.describeUser(), " received Error from backend. Generated ticket ", ticket, result.getError());
				if (SH.is(result.getError().getMessage()))
					showAlert("Backend generated error processing request (" + result.getError().getMessage() + ") .  Please refer to ticket " + ticket, result.getError());
				else
					showAlert("Backend generated error processing request.  Please refer to ticket " + ticket, result.getError());
				return;
			}
		}
		Action action = result.getAction();
		if (action == null) {
			if (!processedByPortlet)
				LH.warning(log, "ignoring empty message for user ", this.getUserName(), ": ", result);
			return;
		}
		PortletService[] l = messageToServiceCache.get(action.getClass());
		if (l == null) {
			ArrayList<PortletService> l2 = new ArrayList<PortletService>();
			for (PortletService ps : services.values()) {
				INNER: for (Class<? extends Action> a : ps.getInterestedBackendMessages()) {
					if (a.isInstance(action)) {
						l2.add(ps);
						break INNER;
					}
				}
			}
			l = AH.toArray(l2, PortletService.class);
			messageToServiceCache.put(action.getClass(), l);
		}
		if (l.length > 0)
			for (PortletService ps : l)
				ps.onBackendResponse(result);
		else if (!processedByPortlet)
			LH.warning(log, "No services interested in response messages of type for user ", this.getUserName(), ": ", action.getClass().getName());
	}

	@Override
	public PortletBackend getBackend() {
		return backend;
	}

	@Override
	public void sendRequestToBackend(String backendServiceId, Message m) {
		backend.sendRequestToBackend(backendServiceId, (String) state.getPartitionId(), null, m);
	}
	@Override
	public void sendRequestToBackend(String backendServiceId, String portletId, Message m) {
		Portlet requestingPortlet = managedPortlets.get(portletId);
		if (requestingPortlet == null)
			throw new NoSuchElementException("portlet not found: " + portletId);

		backend.sendRequestToBackend(backendServiceId, (String) state.getPartitionId(), portletId, m);
	}
	@Override
	public void sendRequestToBackend(String backendServiceId, BackendResponseListener responseListener, Message m) {
		backend.sendRequestToBackend(backendServiceId, (String) state.getPartitionId(), responseListener, m);
	}

	@Override
	public void sendMessageToBackend(String backendServiceId, Message m) {
		backend.sendMessageToBackend(backendServiceId, (String) state.getPartitionId(), m);

	}

	@Override
	public PortletBuilder getPortletBuilder(String builderId) {
		return CH.getOrThrow(portletBuilders, builderId);
	}

	@Override
	public Map<String, Object> getConfiguration(String rootPortletId) {
		Map<String, Object> r = new LinkedHashMap<String, Object>();
		r.put("topId", SH.toString(rootPortletId));
		r.put("metadata", RootAssister.INSTANCE.clone(this.metadata));
		final List<Map<String, Object>> portletConfigs = new ArrayList<Map<String, Object>>();
		final List<Map<String, Object>> connectionConfigs = new ArrayList<Map<String, Object>>();
		List<Portlet> sink = new ArrayList<Portlet>();
		getAllPortletsUnder(getPortlet(rootPortletId), sink);
		Collections.sort(sink, new Comparator<Portlet>() {

			@Override
			public int compare(Portlet o1, Portlet o2) {
				return OH.compare(o1.getPortletId(), o2.getPortletId());
			}
		});
		for (Portlet p : sink) {
			final PortletConfig pc = p.getPortletConfig();
			if (pc.getBuilderId() == null)
				continue;
			final String portletId = pc.getPortletId();
			final Map<String, Object> m = new LinkedHashMap<String, Object>();
			m.put("portletId", portletId);
			m.put("portletBuilderId", pc.getBuilderId());
			m.put("portletConfig", p.getConfiguration());
			portletConfigs.add(m);
			for (PortletSocket socket : p.getSockets().values()) {
				if (socket.getIsInitiator() && !socket.getRemoteConnections().isEmpty()) {
					final String socketName = socket.getName();
					for (PortletSocket remoteSocket : socket.getRemoteConnections()) {
						final String remotePortletId = remoteSocket.getPortlet().getPortletId();
						final String remoteSocketName = remoteSocket.getName();
						Map<String, Object> connectionConfig = new HashMap<String, Object>();
						connectionConfig.put("InitatorPortletId", portletId);
						connectionConfig.put("InitatorSocketName", socketName);
						connectionConfig.put("remotePortletId", remotePortletId);
						connectionConfig.put("remoteSocketName", remoteSocketName);
						connectionConfigs.add(connectionConfig);
					}
				}
			}
		}
		if (CH.isntEmpty(connectionConfigs))
			r.put("connectionConfigs", connectionConfigs);
		r.put("portletConfigs", portletConfigs);
		return r;
	}
	private void getAllPortletsUnder(Portlet portlet, List<Portlet> sink) {
		sink.add(portlet);
		if (portlet instanceof PortletContainer) {
			final PortletContainer pc = (PortletContainer) portlet;
			for (Portlet p : pc.getChildren().values())
				getAllPortletsUnder(p, sink);
		}
	}

	@Override
	public Portlet init(Map<String, Object> configuration, String rootId, StringBuilder warningsSink) {
		return init(configuration, rootId, warningsSink, new HashMap<String, String>(), true);
	}

	@Override
	public Portlet init(Map<String, Object> configuration, String rootId, StringBuilder warningsSink, Map<String, String> origToNewPortletIdSink, boolean forceNewPortletIds) {
		return init(configuration, rootId, warningsSink, origToNewPortletIdSink, false, forceNewPortletIds);
	}
	private Portlet init(Map<String, Object> configuration, String rootId, StringBuilder warningsSink, Map<String, String> origToNewPortletIdSink, boolean flagLoadingConfig,
			boolean forceNewPortletIds) {
		this.menuManager.resetIds();
		this.isLoadingConfig = flagLoadingConfig;
		try {
			resetNow();
			if (this.portletManagerListeners.length > 0 && rootId != null)
				for (PortletManagerListener i : this.portletManagerListeners)
					i.onInit(this, configuration, rootId);
			if (rootId != null) {
				this.metadata.clear();
				Map<String, Object> metadata = (Map<String, Object>) configuration.get("metadata");
				if (metadata != null)
					this.metadata.putAll(metadata);
				fireMetadataChanged();
			} else {

			}
			String topId = CH.getOrThrow(Caster_String.INSTANCE, configuration, "topId");
			final List<Map<String, Object>> portletConfigs = (List<Map<String, Object>>) CH.getOrThrow(Caster_Simple.OBJECT, configuration, "portletConfigs");
			final List<Map<String, Object>> connectionConfigs = (List<Map<String, Object>>) CH.getOr(Caster_Simple.OBJECT, configuration, "connectionConfigs",
					Collections.EMPTY_LIST);
			final Map<String, Map<String, Object>> tmpConfigs = new TreeMap<String, Map<String, Object>>();
			for (Map<String, Object> e : portletConfigs) {
				final String builderId = (String) CH.getOrThrow(e, "portletBuilderId");
				final String origPortletId = (String) CH.getOrThrow(e, "portletId");
				final String portletId;
				if (forceNewPortletIds || SH.areBetween(origPortletId, '0', '9') || getPortletNoThrow(origPortletId) != null)
					portletId = generateId();
				else
					portletId = origPortletId;
				origToNewPortletIdSink.put(origPortletId, portletId);

				final Map<String, Object> portletConfig = (Map<String, Object>) Caster_Simple.OBJECT.cast(CH.getOrThrow(e, "portletConfig"));
				tmpConfigs.put(portletId, portletConfig);
				Portlet portlet = null;
				final PortletBuilder pb = portletBuilders.get(builderId);
				if (pb == null) {
					warningsSink.append("Porlet builder not available: " + builderId);
					continue;
				}
				try {
					portlet = pb.buildPortlet(new BasicPortletConfig(this, portletId, builderId, true));
				} catch (Exception ex) {
					LH.severe(log, "Error for user: ", describeUser(), ". Loading portlet failed so replacing with blank portlet. builderId=: ", builderId,
							", portletId: " + portletId, ex);
					portlet = getPortletBuilder(BlankPortlet.Builder.ID).buildPortlet(new BasicPortletConfig(this, portletId, BlankPortlet.Builder.ID, true));
				}
				onPortletAdded(portlet);
				OH.assertTrue(managedPortlets.get(portletId) == portlet);
			}
			for (Map.Entry<String, Map<String, Object>> e : tmpConfigs.entrySet()) {
				String portletId = e.getKey();
				Map<String, Object> val = e.getValue();
				Portlet portlet = managedPortlets.get(portletId);
				if (portlet == null)
					LH.warning(log, "Portlet not found for portletId=", portletId, " for user", this.getUserName());
				else {
					try {
						portlet.init(val, origToNewPortletIdSink, warningsSink);
					} catch (Exception ex) {
						LH.warning(log, this.getUserName(), " received Error building config, init failed for portlet: ", portlet.getClass().getName(), ", portletId=",
								portlet.getPortletId(), ex);
					}
				}
			}
			for (Map<String, Object> e : connectionConfigs) {
				try {
					String portletId = origToNewPortletIdSink.get(CH.getOrThrow(Caster_String.INSTANCE, e, "InitatorPortletId"));
					String socketName = CH.getOrThrow(Caster_String.INSTANCE, e, "InitatorSocketName");
					String remotePortletId = origToNewPortletIdSink.get(CH.getOrThrow(Caster_String.INSTANCE, e, "remotePortletId"));
					String remoteSocketName = CH.getOrThrow(Caster_String.INSTANCE, e, "remoteSocketName");
					Portlet portlet = managedPortlets.get(portletId);
					if (portlet == null) {
						warningsSink.append("source portlet not found for socket connection: ").append(portletId).append(SH.NEWLINE);
						continue;
					}
					Portlet remotePortlet = managedPortlets.get(remotePortletId);
					if (remotePortlet == null) {
						warningsSink.append("remote portlet not found for socket connection: ").append(portletId).append(SH.NEWLINE);
						continue;
					}
					PortletSocket socket = portlet.getSockets().get(socketName);
					if (socket == null) {
						warningsSink.append("source socket portlet not found for socket connection: ").append(portletId).append(':').append(socketName).append(SH.NEWLINE);
						continue;
					}
					PortletSocket remoteSocket = remotePortlet.getSockets().get(remoteSocketName);
					if (remoteSocket == null) {
						warningsSink.append("remote socket portlet not found for socket connection: ").append(portletId).append(':').append(socketName).append(SH.NEWLINE);
						continue;
					}
					if (!socket.canConnectTo(remoteSocket)) {
						warningsSink.append("sockets are no longer compatible: ").append(socket).append(", ").append(remoteSocket).append(SH.NEWLINE);
						continue;
					}
					try {
						socket.connectTo(remoteSocket);
					} catch (Exception ex) {
						LH.warning(log, this.getUserName(), " received Error building config, dropping connection: ", socket, " --> ", remoteSocket, ex);
					}
				} catch (Exception ex) {
					LH.warning(log, this.getUserName(), " received General error for connection config: ", e, ex);
				}
			}
			Portlet r = getPortlet(CH.getOrThrow(origToNewPortletIdSink, topId));
			if (rootId != null) {
				PortletContainer target = (PortletContainer) getPortlet(rootId);
				target.addChild(r);
			}
			return r;
		} finally {
			isLoadingConfig = false;
		}
	}

	private String currentSeqNum;
	private ClosePortletManagerAction closePortletManagerAction;

	@Override
	public String getCurrentSeqNum() {
		return this.currentSeqNum;
	}

	@Override
	public void handleCallback(Map<String, String> attributes, HttpRequestAction action) {
		this.closePortletManagerAction = null;

		String num = attributes.get("seqnum");
		if (num != null) {
			OH.assertTrue(SH.areBetween(num, '0', '9'));
			tmpFunction.reset(pendingJs, "portletManager", "onSeqnum").addParamQuoted(num).end();
		}
		this.currentSeqNum = num;
		resetNow();
		String portletId = CH.getOr(Caster_String.INSTANCE, attributes, "portletId", null);
		Long pageUid = CH.getOr(Caster_Long.INSTANCE, attributes, "pageUid", null);
		String type = CH.getOrThrow(Caster_String.INSTANCE, attributes, "type");
		if (root == null) {
			LH.info(log, "Dropping callback for user ", this.getUserName(), ", because root portlet no longer exists: ", attributes);
			return;
		}
		boolean isInit = "init".equals(type);
		if (portletManagerListeners.length > 0 && root.getVisible() && !isInit)
			for (PortletManagerListener i : this.portletManagerListeners)
				i.onFrontendCalled(this, attributes, action);
		if (!isInit && (pageUid == null || pageUid != this.pageUid)) {
			long webWindowId = CH.getOr(Caster_Long.INSTANCE, attributes, "webWindowId", 0L);
			if (webWindowId == 0) {
				LH.fine(log, getUserName(), ": webWindowId is null so redirectToLogin");
				SH.clear(getPendingJs()).append("redirectToLogin();");
			} else
				SH.clear(getPendingJs()).append("window.close();");
			return;
		}
		if (portletId == null) {
			if (!this.securityModel.hasPermissions(this, null, type, attributes))
				this.securityModel.raiseSecurityViolation(null, type, attributes);
			else if ("polling".equals(type)) {
			} else if ("unload".equals(type)) {
				int timeout = tools.getOptional("close.session.after.unload.millis", 3000);
				this.getBackend().sendMessageToPortletManager((String) this.state.getPartitionId(), this.closePortletManagerAction = getTools().nw(ClosePortletManagerAction.class),
						timeout);

			} else if ("restResponse".equals(type)) {
				long id = CH.getOrThrow(Caster_Long.INSTANCE, attributes, "id");
				int status = CH.getOrThrow(Caster_Integer.INSTANCE, attributes, "status");
				String response = CH.getOrThrow(Caster_String.INSTANCE, attributes, "response");
				PortletManagerRestCallResponseListener listener = this.restCallListeners.remove(id);
				if (listener == null)
					showAlert("Duplicate response for rest call code " + id);
				else {
					try {
						listener.onRestCallResponse(status, response);
					} catch (Exception e) {
						LH.warning(log, this.getUserName(), " received Error processing rest response ", attributes, e);
						showAlert("Unknown error for rest call code " + id, e);
					}
				}
			} else if ("postInit".equals(type)) {
				long webWindowId = CH.getOrThrow(Caster_Long.INSTANCE, attributes, "webWindowId", null);
				if (webWindowId == 0L)
					root.setVisible(true);
				else
					popupRoots.get(webWindowId).setVisible(true);
			} else if ("userScroll".equals(type)) {
				final String id = CH.getOr(Caster_String.INSTANCE, attributes, "id", null);
				if (id != null) {
					Portlet portlet = getPortletNoThrow(id);
					if (portlet instanceof FormPortlet) {
						final int clipTop = CH.getOr(Caster_Integer.INSTANCE, attributes, "t", Integer.MIN_VALUE);
						final int clipLeft = CH.getOr(Caster_Integer.INSTANCE, attributes, "l", Integer.MIN_VALUE);
						FormPortlet fp = (FormPortlet) portlet;
						fp.setClipTopNoFire(clipTop);
						fp.setClipLeftNoFire(clipLeft);
					}
				}
			} else if ("userKey".equals(type)) {
				Portlet fp = getFocusedPortlet();
				if (fp != null) {
					final String key = CH.getOr(Caster_String.INSTANCE, attributes, "k", null);
					if (key != null) {
						final boolean ctrlKey = CH.getOr(Caster_Boolean.INSTANCE, attributes, "c", Boolean.FALSE);
						final boolean shiftKey = CH.getOr(Caster_Boolean.INSTANCE, attributes, "s", Boolean.FALSE);
						final boolean altKey = CH.getOr(Caster_Boolean.INSTANCE, attributes, "a", Boolean.FALSE);
						final KeyEvent keyEvent;
						if (this.focusedField != null)
							keyEvent = new KeyEvent(key, ctrlKey, shiftKey, altKey, KeyEvent.KEYDOWN, this.focusedField.getA(), this.focusedField.getB());
						else
							keyEvent = new KeyEvent(key, ctrlKey, shiftKey, altKey, KeyEvent.KEYDOWN, fp, null);
						try {
							callbackTarget = fp;
							fp.onUserKeyEvent(keyEvent);
						} catch (Exception e) {
							throw new RuntimeException("Portlet '" + OH.getSimpleClassName(fp) + "' threw exception on callback '" + type + "':" + attributes, e);
						} finally {
							callbackTarget = null;
						}
					}
				}
			} else if ("userClick".equals(type)) {
				Portlet fp = getFocusedPortlet();
				if (fp != null) {
					String pid = CH.getOr(Caster_String.INSTANCE, attributes, "pid", null);
					Portlet portlet = getPortletNoThrow(pid);
					final MouseEvent mouseEvent = toMouseEvent(attributes);
					try {
						callbackTarget = portlet;
						if (callbackTarget == null)
							callbackTarget = fp;
						fp.onUserMouseEvent(mouseEvent);
					} catch (Exception e) {
						throw new RuntimeException("Portlet '" + OH.getSimpleClassName(fp) + "' threw exception on callback '" + type + "':" + attributes, e);
					} finally {
						callbackTarget = null;
					}
				}

			} else if ("userActivatePortlet".equals(type)) {
				String pid = CH.getOr(attributes, "pid", null);
				Portlet p = getPortletNoThrow(pid);
				if (p != null) {
					final MouseEvent mouseEvent = toMouseEvent(attributes);
					p.onUserRequestFocus(mouseEvent);
				}
			} else if ("popupClosed".equals(type)) {
				long childWindowId = CH.getOrThrow(Caster_Long.INSTANCE, attributes, "childWindowId");
				RootPortlet removedRoot = this.popupRoots.remove(childWindowId);
				if (!removedRoot.getDialogs().isEmpty())
					for (RootPortletDialog rootPortletDialog : CH.l(removedRoot.getDialogs())) {
						rootPortletDialog.getPortlet().getParent().removeChild(rootPortletDialog.getPortlet().getPortletId());
						this.getRoot().addDialog(rootPortletDialog);
					}
				this.popupRootsByPortletId.remove(removedRoot.getPortletId());
				onPortletClosed(removedRoot);
				removedRoot.setVisible(false);
				removedRoot.fireOnPopupWindowClosed();
				removedRoot.close();
			} else if ("audit".equals(type)) {
				HttpRequestResponse httpReq = action.getRequest();
				StringBuilder sb = new StringBuilder();
				sb.append(attributes.get("text"));
				sb.append(SH.NEWLINE);
				sb.append("### BACKEND PORTLET STRUCTURE ###");
				debugPortletTree(getRoot(), 0, 4, sb);
				sb = SH.prefixLines(sb, "==> ", true, new StringBuilder());
				LH.warning(log, "Audit from client browser (", httpReq.getRemoteHost(), ":", httpReq.getPort(), ") for user: ", describeUser(), SH.NEWLINE, sb);
			} else if (isInit) {
				long webWindowId = CH.getOrThrow(Caster_Long.INSTANCE, attributes, "webWindowId");
				int width = CH.getOrThrow(Caster_Integer.INSTANCE, attributes, "width");
				int height = CH.getOrThrow(Caster_Integer.INSTANCE, attributes, "height");
				init(width, height, webWindowId);
			} else if ("popupFailed".equals(type)) {
				long webWindowId = CH.getOr(Caster_Long.INSTANCE, attributes, "popupWindowId", 0L);
				RootPortlet popup = this.popupRoots.get(webWindowId);
				showAlert("Popups are disabled. Enable them by clicking on the popup icon in the upper right corner. Then try again.");
				popup.fireOnPopupWindowFailed();
			} else if ("notificationClosed".equals(type)) {
				final String nid = CH.getOrThrow(Caster_String.INSTANCE, attributes, "nid");
				PortletNotification notification = this.openNotifications.remove(nid);
				for (int i = 0, l = this.portletNotificationListeners.size(); i < l; i++)
					this.portletNotificationListeners.get(i).onNotificationClosed(this, notification);
			} else if ("notificationClicked".equals(type)) {
				final String nid = CH.getOrThrow(Caster_String.INSTANCE, attributes, "nid");
				PortletNotification notification = this.openNotifications.remove(nid);
				for (int i = 0, l = this.portletNotificationListeners.size(); i < l; i++)
					this.portletNotificationListeners.get(i).onNotificationUserClicked(this, notification);
			} else if ("notificationDenied".equals(type)) {
				final String nid = CH.getOrThrow(Caster_String.INSTANCE, attributes, "nid");
				PortletNotification notification = this.openNotifications.remove(nid);
				for (int i = 0, l = this.portletNotificationListeners.size(); i < l; i++)
					this.portletNotificationListeners.get(i).onNotificationDenied(this, notification);
			} else if ("exportConfig".equals(type)) {
				Map<String, Object> config = getConfiguration(getRoot().getContent().getPortletId());
				tmpFunction.reset(pendingJs, "portletManager", "showSaveConfigDialog").addParamQuoted(ObjectToJsonConverter.INSTANCE_COMPACT.objectToString(config)).end();
				//			} else if ("logout".equals(type)) {
				//				close();
			} else if ("saveConfig".equals(type)) {
				saveConfig();
			} else if ("loadConfig".equals(type)) {
				StringBuilder warningsSink = new StringBuilder();
				String text = (String) CH.getOrThrow(attributes, "text");
				Map<String, Object> configuration = (Map<String, Object>) new ObjectToJsonConverter().stringToObject(text);
				CH.first(root.getChildren().values()).close();
				init(configuration, getRoot().getPortletId(), warningsSink);
				if (warningsSink.length() > 0)
					LH.warning(log, this.getUserName(), " received Warnings loading config:", warningsSink);
			} else if ("service".equals(type)) {
				String serviceId = CH.getOrThrow(Caster_String.INSTANCE, attributes, "serviceId");
				PortletService service = getService(serviceId);
				service.handleCallback(attributes, action);
			}
		} else {
			if (SH.is(portletId)) {
				Portlet portlet = getPortletNoThrow(portletId);
				if (portlet == null) {
					LH.info(log, this.getUserName(), " received callback for dead portlet: '", portletId, "' type: '", type, "' callback: ", attributes);
				} else if (!portlet.getVisible()) {
					if (LH.isFine(log))
						LH.fine(log, this.getUserName(), " received callback for hidden portlet: '", portletId, "' type: '", type, "' callback: ", attributes);
				} else {
					if (!this.securityModel.hasPermissions(this, portlet, type, attributes))
						this.securityModel.raiseSecurityViolation(portlet, type, attributes);
					try {
						callbackTarget = portlet;
						portlet.handleCallback(type, attributes);
					} catch (Exception e) {
						throw new RuntimeException(
								"Portlet '" + OH.getSimpleClassName(portlet) + "' threw exception for user " + this.getUserName() + " on callback '" + type + "':" + attributes, e);
					} finally {
						callbackTarget = null;
					}
				}
			}
		}
		if (!this.pendingNotifications.isEmpty()) {
			for (int i = 0; i < this.pendingNotifications.size(); i++) {
				PortletNotification pn = this.pendingNotifications.get(i);
				tmpFunction.reset(pendingJs, "portletManager", "showNotification").addParamQuoted(pn.getId()).addParamQuoted(pn.getTitle()).addParamQuoted(pn.getBody())
						.addParamQuoted(pn.getImageUrl()).end();
			}
			this.pendingNotifications.clear();
		}

	}
	private MouseEvent toMouseEvent(Map<String, String> attributes) {
		final byte mouseEventType = CH.getOr(Caster_Byte.INSTANCE, attributes, "t", MouseEvent.CLICK);
		final boolean ctrlKey = CH.getOr(Caster_Boolean.INSTANCE, attributes, "c", Boolean.FALSE);
		final boolean shiftKey = CH.getOr(Caster_Boolean.INSTANCE, attributes, "s", Boolean.FALSE);
		final boolean altKey = CH.getOr(Caster_Boolean.INSTANCE, attributes, "a", Boolean.FALSE);
		final int x = CH.getOr(Caster_Integer.INSTANCE, attributes, "x", -1);
		final int y = CH.getOr(Caster_Integer.INSTANCE, attributes, "y", -1);
		final int b = CH.getOr(Caster_Integer.INSTANCE, attributes, "b", 0);
		final MouseEvent mouseEvent = new MouseEvent(mouseEventType, b, x, y, ctrlKey, shiftKey, altKey);
		return mouseEvent;
	}
	@Override
	public Portlet getFocusedPortlet() {
		return this.focusedPortlets.getSize() == 0 ? null : this.focusedPortlets.getAt(this.focusedPortlets.getSize() - 1);
	}
	@Override
	public PortletDownload handleContentRequest(Map<String, String> attributes, HttpRequestAction action) {
		String portletId = CH.getOr(Caster_String.INSTANCE, attributes, "portletId", null);
		String type = CH.getOrThrow(Caster_String.INSTANCE, attributes, "type");
		if (SH.is(portletId)) {
			Portlet portlet = getPortletNoThrow(portletId);
			if (portlet == null) {
				LH.info(log, this.getUserName(), " received callback for dead portlet: ", portletId);
			} else if (!portlet.getVisible()) {
				LH.info(log, this.getUserName(), " received callback for hidden portlet: ", portletId);
			} else
				try {
					callbackTarget = portlet;
					return portlet.handleContentRequest(type, attributes);
				} catch (Exception e) {
					throw new RuntimeException("Portlet '" + OH.getSimpleClassName(portlet) + "' threw exception on callback '" + type + "':" + attributes, e);
				} finally {
					callbackTarget = null;
				}
		}
		return null;
	}

	@Override
	public Portlet getCallbackTarget() {
		return callbackTarget;
	}

	static public void debugPortletTree(Portlet p, int tabs, int tabSize, StringBuilder sb) {
		SH.repeatSpaces(tabSize * tabs, sb);
		if (p.getVisible()) {
			int x, y;
			PortletContainer parent = p.getParent();
			if (parent != null) {
				x = parent.getChildOffsetX(p.getPortletId());
				y = parent.getChildOffsetY(p.getPortletId());
			} else
				x = y = 0;
			sb.append(OH.getSimpleClassName(p)).append('[').append(p.getPortletId()).append("] ").append("(" + x + " " + y + ": " + p.getWidth() + " x " + p.getHeight() + ")")
					.append(SH.NEWLINE);
		} else {
			sb.append(OH.getSimpleClassName(p)).append('[').append(p.getPortletId()).append("] ").append("(hidden)").append(SH.NEWLINE);
		}
		tabs++;
		if (p instanceof PortletContainer)
			for (Portlet i : ((PortletContainer) p).getChildren().values())
				debugPortletTree(i, tabs, tabSize, sb);
	}

	public void saveConfig() {
		Map<String, Object> config = getConfiguration(getRoot().getContent().getPortletId());
		String configText = ObjectToJsonConverter.INSTANCE_COMPACT.objectToString(config);
		String key = tools.getOptional("sso.key.portlet.layout", "portletlayout_default");
		userConfigStore.saveFile(key, configText);
	}
	private void logout() {
		String username = getUserName();
		this.backend.unsubscribe((String) state.getPartitionId());
		for (Entry<String, PortletService> service : services.entrySet()) {
			try {
				service.getValue().close();
			} catch (Exception e) {
				LH.warning(log, "Error closing service ", service, " for user ", describeUser(), e);
			}
		}
		try {
			if (this.root != null && root.getChildren() != null && !root.getChildren().isEmpty())
				CH.first(root.getChildren().values()).close();
		} catch (Exception e) {
			LH.warning(log, "Error closing children for user ", describeUser(), e);
		}
		services.clear();

		LH.fine(log, username, ": User Logout so redirectToLogin");
		SH.clear(getPendingJs()).append("redirectToLogin();");
	}
	@Override
	public Portlet buildPortlet(String portletBuilderId) {
		Portlet r = getPortletBuilder(portletBuilderId).buildPortlet(new BasicPortletConfig(this, generateId(), portletBuilderId, false));
		onPortletAdded(r);
		return r;
	}

	@Override
	public BundledTextFormatter getTextFormatter() {
		return textFormatter;
	}

	@Override
	public void showAlert(String text) {
		this.showAlert(text, null);
	}
	@Override
	public void showAlert(String text, Throwable exception) {
		this.showAlert(text, exception, null);
	}

	public void showAlert(String text, Throwable exception, ConfirmDialogPortlet cdp) {
		if (this.getCurrentRootPortlet() == null || this.root.getChildrenCount() == 0) {
			this.deferedAlerts.add(new Tuple3<String, Throwable, ConfirmDialogPortlet>(text, exception, cdp));
			return;
		}
		showAlertInner(text, exception, cdp);
	}

	private void showAlertInner(String text, Throwable exception, ConfirmDialogPortlet cdp) {
		try {
			if (cdp == null)
				cdp = new ConfirmDialogPortlet(generateConfig(), text, ConfirmDialogPortlet.TYPE_ALERT);
			else
				cdp.setText(text);

			if (exception != null)
				cdp.setDetails(SH.printStackTrace(exception));
			if (getCurrentRootPortlet() == null) {
				LH.warning(log, describeUser() + " Cannot display alert because there isn't a root portlet: " + text, exception);
			} else
				showDialog("Message", cdp);
		} catch (Exception e) {
			LH.warning(log, this.getUserName(), " received General error for warning: " + text, e, exception);
			tmpFunction.reset(getPendingJs(), null, "alert").addParamQuoted(text).end();
		}

	}
	public ConfirmDialogPortlet createAlertDialog() {
		return createAlertDialog(ConfirmDialogPortlet.TYPE_ALERT);
	}
	public ConfirmDialogPortlet createAlertDialog(byte confirmDialogType) {
		return new ConfirmDialogPortlet(generateConfig(), "", confirmDialogType);
	}
	@Override
	public LocaleFormatter getLocaleFormatter() {
		return this.formatter;
	}

	@Override
	public RootPortletDialog showDialog(String title, Portlet p) {
		onPortletAdded(p);
		return getCurrentRootPortlet().addDialog(title, p);
	}
	@Override
	public RootPortletDialog showDialog(String title, Portlet p, int width, int height) {
		return showDialog(title, p, width, height, true);
	}

	@Override
	public RootPortletDialog showDialog(String title, Portlet p, int width, int height, boolean isModal) {
		onPortletAdded(p);
		return getCurrentRootPortlet().addDialog(title, p, width, height, isModal);
	}

	@Override
	public RootPortlet getCurrentRootPortlet() {
		RootPortlet r = null;
		if (this.callbackTarget != null)
			r = PortletHelper.findParentByType(this.callbackTarget, RootPortlet.class);
		return r == null ? root : r;
	}

	@Override
	public Set<String> getBuilders() {
		return portletBuilders.keySet();
	}

	@Override
	public void addPortletBuilder(PortletBuilder portletAppBuilder) {
		this.addPortletBuilder(portletAppBuilder.getPortletBuilderId(), portletAppBuilder);
	}
	@Override
	public void addPortletBuilder(String portletid, PortletBuilder portletAppBuilder) {
		portletBuilders.put(portletid, portletAppBuilder);
	}

	@Override
	public PortletMetrics getPortletMetrics() {
		return this.portletMetrics;
	}

	@Override
	public PortletUserConfigStore getUserConfigStore() {
		return userConfigStore;
	}

	@Override
	public void setUserConfigStore(PortletUserConfigStore userConfigStore) {
		this.userConfigStore = userConfigStore;
	}

	@Override
	public String describeUser() {
		return getState().describeUser();
	}

	@Override
	public PortletConfig generateConfig() {
		return new BasicPortletConfig(this);
	}

	@Override
	public void loadConfig() {
		String key = tools.getOptional("sso.key.portlet.layout", "portletlayout_default");
		String portletEntitlements = getUserConfigStore().getSettingString("portlets_entitled");
		ObjectToJsonConverter converter = new ObjectToJsonConverter();
		if (portletEntitlements != null) {
			Map<String, Boolean> enabled = (Map<String, Boolean>) converter.stringToObject(portletEntitlements);
			boolean deflt = CH.getOr(enabled, "DEFAULT", true);
			for (String s : CH.l(this.portletBuilders.keySet())) {
				if (!CH.getOr(enabled, s, deflt)) {
					this.portletBuilders.remove(s);
				}
			}
		}
		String defaultLayout = getUserConfigStore().loadFile(key);
		if (SH.is(defaultLayout)) {
			StringBuilder warningsSink = new StringBuilder();
			Map<String, Object> configuration = (Map<String, Object>) converter.stringToObject(defaultLayout);
			init(configuration, getRoot().getPortletId(), warningsSink);
		}

	}

	private int pendingDownloadsCount = 0;
	private Queue<PortletDownload> pendingDownloads = new LinkedList<PortletDownload>();
	private Queue<String> pendingAudio = new LinkedList<String>();
	private long now;

	private ContainerTools tools;

	private int nextWebWindowId = 1;

	private PortletStyleManager portletStyleManager = new PortletStyleManager();

	private boolean debugLayout = false;
	private boolean isSecureConnection;
	private boolean closed = false;

	@Override
	public boolean hasPendingDownloads() {
		return !pendingDownloads.isEmpty();
	}

	@Override
	public PortletDownload popPendingDownload() {
		return pendingDownloads.poll();
	}

	@Override
	public void pushPendingDownload(PortletDownload download) {
		pendingDownloads.offer(download);
		pendingDownloadsCount++;
	}

	@Override
	public Portlet getPortletNoThrow(String portletId) {
		return managedPortlets.get(portletId);
	}

	@Override
	public void addPortletManagerListener(PortletManagerListener listener) {
		this.portletManagerListeners = AH.append(this.portletManagerListeners, listener);
	}

	@Override
	public void removePortletManagerListener(PortletManagerListener listener) {
		this.portletManagerListeners = AH.remove(this.portletManagerListeners, listener);
	}
	@Override
	public void addPortletNotificationListener(PortletNotificationListener listener) {
		this.portletNotificationListeners.add(listener);
	}

	@Override
	public void removePortletNotificationListener(PortletNotificationListener listener) {
		this.portletNotificationListeners.remove(listener);
	}

	@Override
	public long getNow() {
		return System.currentTimeMillis();
	}
	@Override
	public void showContextMenu(WebMenu menu, WebMenuListener listener, int x, int y, Map<String, Object> options) {
		if (menu.getChildren().isEmpty())
			return;
		this.getCurrentRootPortlet().showContextMenu(menu, listener, x, y, options);
	}
	@Override
	public void showContextMenu(WebMenu menu, WebMenuListener listener, int x, int y) {
		if (menu.getChildren().isEmpty())
			return;
		this.getCurrentRootPortlet().showContextMenu(menu, listener, x, y);
	}

	@Override
	public void showContextMenu(WebMenu menu, WebMenuListener listener) {
		if (menu.getChildren().isEmpty())
			return;
		this.getCurrentRootPortlet().showContextMenu(menu, listener, -1, -1);
	}

	@Override
	public void closeContextMenu() {
		this.getCurrentRootPortlet().closeContextMenu();
	}
	@Override
	public void onLocationChanged(Portlet portlet) {
		for (PortletListener l : portletListeners)
			l.onLocationChanged(portlet);
		if (!portlet.getVisible()) {
			this.focusedPortlets.removeNoThrow(portlet.getPortletId());
			if (this.focusedField != null && this.focusedField.getA() == portlet)
				this.focusedField = null;
		}
	}

	public int getPollingMs() {
		return pollingMs;
	}

	@Override
	public void close() {
		getState().killWebState();
	}

	public void closeInner() {
		LH.info(log, "Closing Portlet Manager for: ", describeUser());
		this.closed = true;
		logout();
		for (PortletManagerListener i : this.portletManagerListeners) {
			try {
				i.onPortletManagerClosed();
			} catch (Exception e) {
				LH.warning(log, this.getUserName(), " received error closing for porlet manager listener: ", i, e);
			}
		}
		this.portletBuilders.clear();
		this.managedPortlets.clear();
		this.focusedPortlets.clear();
		this.messageToServiceCache.clear();
		this.pendingDownloads.clear();
		this.restCallListeners.clear();
		this.pendingDownloadsCount = 0;
		this.pendingJs.setLength(0);
		this.portletListeners = EMPTY_PORTLET_LISTENERS;
		this.portletsWithQueuedJs.clear();
		this.menuManager.resetIds();
		this.requestFocuseOnField = null;
		this.focusedField = null;
	}

	@Override
	public Set<String> getPortletIds() {
		return this.managedPortlets.keySet();
	}

	@Override
	public ObjectToJsonConverter getJsonConverter() {
		return this.jsonConverter;
	}

	@Override
	public ContainerTools getTools() {
		return tools;
	}

	@Override
	public Map<String, Object> getMetadataConfig() {
		return metadata;
	}

	@Override
	public Object getMetadata(String name) {
		return this.metadata.get(name);
	}

	@Override
	public Object putMetadata(String name, Object value) {
		return this.metadata.put(name, value);
	}

	@Override
	public Object removeMetadata(String name) {
		return this.metadata.remove(name);
	}

	@Override
	public Set<String> getMetadataNames() {
		return this.metadata.keySet();
	}

	@Override
	public PortletNotification showNotification(String title, String body, String imageUrl, Map<String, String> options) {
		SH.clear(this.tmpSb);
		GuidHelper.getGuid(62, this.tmpSb);
		this.tmpSb.setLength(4);
		this.tmpSb.append(++nextNotificationId);
		PortletNotification r = new PortletNotification(SH.toStringAndClear(this.tmpSb), title, body, imageUrl, options);
		showNotification(r);
		return r;
	}
	private void showNotification(PortletNotification portletNotification) {
		this.pendingNotifications.add(portletNotification);
		this.openNotifications.put(portletNotification.getId(), portletNotification);
	}

	@Override
	public RootPortlet showPopupWindow(Portlet portlet, int left, int top, int width, int height, String title) {
		long id = nextWebWindowId++;
		RootPortlet r = new RootPortlet(generateConfig(), id);
		r.setTitle(title);
		r.addChild(portlet);
		this.onPortletAdded(r);
		this.popupRoots.put(id, r);
		this.popupRootsByPortletId.put(r.getPortletId(), r);
		tmpFunction.reset(getPendingJs(), "portletManager", "showPopupWindow").addParam(id).addParam(left).addParam(top).addParam(width).addParam(height)
				.addParamQuoted(r.getTitle()).end();
		return r;

	}

	public void fireOnPageLoading(HttpRequestResponse action) {
		this.isSecureConnection = action.getIsSecure();
		Map<String, String> paramsCombined = getUrlParams(action);
		for (PortletManagerListener i : this.portletManagerListeners)
			i.onPageLoading(this, paramsCombined, action);
		this.urlParams.clear();
		this.urlParams.putAll(paramsCombined);
		if (!this.deferedAlerts.isEmpty()) {
			for (Tuple3<String, Throwable, ConfirmDialogPortlet> i : this.deferedAlerts)
				showAlertInner(i.getA(), i.getB(), i.getC());
			this.deferedAlerts.clear();
		}
		FastPrintStream out = action.getOutputStream();
		out.append("<script>setBrowserURL(\"" + URL_PORTAL);
		out.append(buildUrlParams(true));
		out.append("\");</script>");
		this.requestFocuseOnField = focusedField;
		this.focusedField = null;

	}
	public String buildUrlParams(boolean isFirst) {
		if (this.urlParams.isEmpty())
			return "";
		boolean first = isFirst;
		StringBuilder out = new StringBuilder();
		for (Entry<String, String> entry : this.urlParams.entrySet()) {
			if (first) {
				out.append('?');
				first = false;
			} else
				out.append('&');
			out.append(SH.encodeUrl(entry.getKey()));
			out.append('=');
			out.append(SH.encodeUrl(entry.getValue()));
		}
		return out.toString();
	}

	public static LinkedHashMap<String, String> getUrlParams(HttpRequestResponse action) {
		LinkedHashMap<String, String> paramsCombined = new LinkedHashMap<String, String>();
		if (action != null) {
			HttpSession session = action.getSession(false);
			String preloginGuid = action.getParams().get(PRELOGINID);
			Map<String, Map> map = (Map<String, Map>) session.getAttributes().get(PORTAL_PRELOGIN_PARAMS);
			LinkedHashMap<String, String> params = map == null || preloginGuid == null ? null : (LinkedHashMap<String, String>) map.remove(preloginGuid);
			Map<String, String> params2 = action.getParams();
			if (CH.isntEmpty(params))
				paramsCombined.putAll(params);
			if (CH.isntEmpty(params2))
				paramsCombined.putAll(params2);
			paramsCombined.remove(PAGEID);
			paramsCombined.remove(KEEP_EXISTING_OPEN);
			paramsCombined.remove(WINDOWID);
			paramsCombined.remove(PRELOGINID);
		}
		return paramsCombined;
	}

	//Strategy handle case where two browsers come from the same IP within CONCURRENT_SESSION_MS(500) millis and neither has a session:
	//   (A) assume they are current requests from the same browser
	//   (B) Strategy is to create a session for one and tell the other(s) to retry (see window.location.href script below)
	private static HttpSession getSessionHandleConcurrency(HttpRequestResponse req) {
		final HttpSession session = req.getSession(false);
		if (session != null)
			return session;
		ConcurrentMap<String, Object> serverAttributes = req.getHttpServer().getAttributes();
		ConcurrentHashMap<String, Long> existingClients = (ConcurrentHashMap<String, Long>) serverAttributes.get(CONCURRENT_CLIENT_LOGGINS);
		if (existingClients == null) {
			serverAttributes.put(CONCURRENT_CLIENT_LOGGINS, new ConcurrentHashMap<String, Long>());
			existingClients = (ConcurrentHashMap<String, Long>) serverAttributes.get(CONCURRENT_CLIENT_LOGGINS);
		}
		final long now = System.currentTimeMillis();
		String remoteHost = req.getHeader().get("X-Forwarded-For");
		if (remoteHost == null)
			remoteHost = req.getRemoteHost();
		final Long createdOn = existingClients.get(remoteHost);
		if (createdOn == null) {
			if (existingClients.putIfAbsent(remoteHost, now) == null)
				return req.getSession(true);
		} else if (now > createdOn.longValue() + CONCURRENT_SESSIONS_MS) {
			existingClients.remove(remoteHost, createdOn);
		}
		FastPrintStream out = req.getOutputStream();
		out.println("<script>window.location.href='" + req.getContextPath() + "'; </script>");
		req.setResponseType(HttpRequestResponse.HTTP_200_OK);
		return null;
	}

	//called by portal.jsp
	public static boolean processPgid(HttpRequestResponse req) {
		final HttpSession session = getSessionHandleConcurrency(req);
		if (session == null)
			return false;

		WebStatesManager manager = WebStatesManager.get(req.getSession(false));
		FastPrintStream out = req.getOutputStream();
		if (manager == null || !manager.isLoggedIn()) {
			Map<String, String> params = req.getParams();
			String preloginParamGuid = null;
			if (!params.isEmpty()) {
				preloginParamGuid = "PLID" + GuidHelper.getGuid();
				params = new LinkedHashMap<String, String>(params);
				params.remove(PAGEID);
				Map<String, Map> map = (Map<String, Map>) session.getAttributes().get(PORTAL_PRELOGIN_PARAMS);
				if (map == null) {
					session.getAttributes().putIfAbsent(PORTAL_PRELOGIN_PARAMS, new ConcurrentHashMap<String, Map>());
					map = (Map<String, Map>) session.getAttributes().get(PORTAL_PRELOGIN_PARAMS);
				}
				map.put(preloginParamGuid, params);
			}
			out.println("<script>");
			if (preloginParamGuid != null)
				out.println("  sessionStorage.setItem('" + PRELOGINID + "','" + preloginParamGuid + "');");
			out.println("  window.location.href='/';");
			out.println("</script>");
			req.setResponseType(HttpRequestResponse.HTTP_200_OK);
			return false;
		}
		String pgid = req.getParams().get(PAGEID);
		if (pgid == null) {//this is a reload.  Redo the page with a pgid
			String passthroughParams = HttpUtils.getParamsAsString(req.getParams());
			out.println("<script>");
			out.println("var pgid=sessionStorage.getItem('" + PAGEID + "');");
			out.println("if(!pgid)");
			if (SH.is(passthroughParams)) {
				out.println("  window.location.href='" + BasicPortletManager.URL_START + "?" + passthroughParams + "'");
				out.println("else");
				out.println("  window.location.href='" + BasicPortletManager.URL_PORTAL + "?" + BasicPortletManager.PAGEID + "='+ pgid+'&" + passthroughParams + "';");
			} else {
				out.println("  window.location.href='" + BasicPortletManager.URL_START + "'");
				out.println("else");
				out.println("  window.location.href='" + BasicPortletManager.URL_PORTAL + "?" + BasicPortletManager.PAGEID + "='+ pgid;");
			}
			out.println("</script>");
			return false;
		} else {
			out.println("<script>");
			out.println("var __PGID='" + pgid + "';");
			out.println("sessionStorage.setItem('" + PAGEID + "',__PGID);");
			out.println("</script>");
			req.getAttributes().put("pageReload", false);
			return true;
		}

	}

	//called by portal.jsp
	public static void onPageLoad(HttpRequestResponse req) {
		HttpSession session = req.getSession(true);
		String pgid = req.getParams().get(PAGEID);
		if (pgid == null)
			return;
		WebState windowState = WebStatesManager.get(session, pgid);
		if (windowState == null)
			return;
		final Partition partition = windowState.getPartition();
		if (partition == null) {
			return;
		}
		int timeout = PortalHttpStateCreator.getSessionAcquireLockTimeoutSeconds(session);
		if (!partition.lockForWrite(timeout, TimeUnit.SECONDS)) {
			LH.warning(log, "for '", windowState.getUserName(), "': Could not acquire partition after " + timeout + " seconds.");
			return;
		}
		try {
			BasicPortletManager portletManager = (BasicPortletManager) windowState.getPortletManager();
			if (portletManager != null) {
				portletManager.fireOnPageLoading(req);
			}
		} finally {
			partition.unlockForWrite();
		}

	}

	@Override
	public PortletStyleManager getStyleManager() {
		return this.portletStyleManager;
	}

	@Override
	public boolean getDebugLayout() {
		return this.debugLayout;
	}

	@Override
	public void setDebugLayout(boolean b) {
		if (this.getTools().getOptional(PORTLET_DEBUG_LAYOUT_ENABLED, Boolean.FALSE)) {
			this.debugLayout = b;
			if (b) {
				debugPortletTree(getRoot(), 0, 4, SH.clear(this.tmpSb));
				LH.info(log, "Debug Mode Enabled for user ", this.getUserName(), " : ", this.tmpSb);
			}
		}
	}

	public IterableAndSize<RootPortlet> getPopouts() {
		return this.popupRoots.values();
	}

	@Override
	public RootPortlet getPopoutForPortletId(String portletId) {
		Portlet p = this.managedPortlets.get(portletId);
		if (p == null)
			return null;
		return PortletHelper.findParentByType(p, RootPortlet.class);
	}

	private HtmlPortlet tmpPortlet;

	public Portlet getTmpPortlet() {
		if (this.tmpPortlet == null) {
			this.tmpPortlet = new HtmlPortlet(generateConfig());
			onPortletAdded(tmpPortlet);
		}
		return this.tmpPortlet;
	}

	public PortletManagerSecurityModel getSecurityModel() {
		return securityModel;
	}

	public void setSecurityModel(PortletManagerSecurityModel securityModel) {
		this.securityModel = securityModel;
	}

	@Override
	public PortletMenuManager getMenuManager() {
		return this.menuManager;
	}

	//	@Override
	//	public WebUser getUser() {
	//		return this.getState().getWebStatesManager().getUser();
	//	}

	public String getUserName() {
		return this.getState().getUserName();
	}

	@Override
	public String getDefaultBrowserTitle() {
		return defaultBrowserTitle;
	}
	@Override
	public void handlGeneralError(String ticket, Exception e) {
		String msg = tools.getOptional(GENERAL_ERROR_MESSAGE, "Frontend encountered unhandled condition.");
		msg = SH.replaceAll(msg, "${ticket}", ticket);
		String emailTo = tools.getOptional(GENERAL_ERROR_EMAIL_TO, "support@3forge.com");
		if (emailTo != null) {
			final String emailSubject = tools.getOptional(GENERAL_ERROR_EMAIL_SUBJECT, "Support Issue");
			final String emailBody = tools.getOptional(GENERAL_ERROR_EMAIL_BODY, "Please find details below:");
			final String message = getMessage(e);
			final String stack = getStack(e);

			StringBuilder emailUrl = new StringBuilder();
			emailUrl.append("mailto:").append(emailTo);
			emailUrl.append("?subject=").append(escapeUrl(emailSubject));
			emailUrl.append("&body=").append(escapeUrl(emailBody));
			if (SH.is(ticket))
				emailUrl.append(escapeUrl("\n\nTicket:\n" + ticket));
			if (SH.is(buildVersion))
				emailUrl.append(escapeUrl("\n\nVersion:\n" + buildVersion));
			if (SH.is(stack))
				emailUrl.append(escapeUrl("\n\nStack:\n" + stack));
			if (SH.is(message))
				emailUrl.append(escapeUrl("\n\nMessage:\n" + message));
			String email = "<a href=\'" + SH.ddd(emailUrl.toString(), 2048) + "'>Please send details to support</a>";
			msg += "<P>" + email;
		}
		showAlert(msg, e);
	}
	private String getMessage(Throwable e) {
		StringBuilder msg = new StringBuilder();
		while (e != null) {
			if (SH.is(e.getMessage())) {
				if (msg.length() > 0)
					msg.append(" ==> ");
				SH.ddd(e.getMessage(), 200, msg);
			}
			e = e.getCause();
		}
		return SH.isnt(msg) ? null : SH.ddd(msg.toString(), 400);
	}
	private String escapeUrl(String url) {
		return SH.encodeUrl(url, false);
	}
	public static String getStack(Throwable exception) {
		StackTraceElement[] toIgnore = Thread.currentThread().getStackTrace();
		AH.reverse(toIgnore, 0, toIgnore.length);
		StringBuilder sb = new StringBuilder();
		while (exception != null) {
			String exceptionClassName;
			if (exception instanceof ConvertedException) {
				ConvertedException ce = (ConvertedException) exception;
				exceptionClassName = ce.getExceptionClassName();
			} else
				exceptionClassName = exception.getClass().getName();
			String ecn = SH.afterLast(exceptionClassName, '.', exceptionClassName);
			shortenClassname(ecn, sb);
			sb.append(':');
			StackTraceElement elements[] = exception.getStackTrace();
			if (elements != null) {
				String lastName = null;
				for (int i = 0; i < elements.length; i++) {
					StackTraceElement element = elements[i];
					int posFromEnd = elements.length - i - 1;
					if (posFromEnd < toIgnore.length && OH.eq(toIgnore[posFromEnd], element))
						continue;
					if (element.getLineNumber() == 1)
						continue;//skip printing casts.
					String name = element.getFileName();
					if (name == null)
						name = SH.afterLast(element.getClassName(), '.', element.getClassName());
					if (OH.eq(name, lastName))
						sb.append(',');
					else
						shortenClassname(lastName = name, sb);
					sb.append(SH.toString(element.getLineNumber()));
				}
			}
			exception = exception.getCause();
		}
		return sb.toString();
	}
	public static void shortenClassname(String string, StringBuilder sb) {
		boolean lastPrinted = false;
		for (int i = 0, l = string.length(); i < l; i++) {
			char c = string.charAt(i);
			if (i == 0 || (OH.isBetween(c, 'A', 'Z') && !lastPrinted)) {
				sb.append(c);
				if (i + 1 < l) {
					c = string.charAt(i + 1);
					if (OH.isntBetween(c, 0, 9))
						sb.append(c);
				}
				lastPrinted = true;
			} else
				lastPrinted = false;
		}
	}

	@Override
	public boolean getIsSecureConnection() {
		return this.isSecureConnection;
	}

	private long nextRestCallId = 0;
	private LongKeyMap<PortletManagerRestCallResponseListener> restCallListeners = new LongKeyMap<PortletManagerRestCallResponseListener>();
	private boolean urlParamsChanged = false;
	private Tuple2<Portlet, String> requestFocuseOnField;
	private Tuple2<Portlet, String> focusedField;

	@Override
	public void sendRestRequest(boolean isPost, URL target, String data, int timeoutMs, PortletManagerRestCallResponseListener listener) {
		long id = ++nextRestCallId;
		tmpFunction.reset(getPendingJs(), "portletManager", "callRest").addParam(id).addParam(isPost).addParamQuoted(target.toString()).addParamQuoted(data).addParam(timeoutMs)
				.end();
		restCallListeners.put(id, listener);
	}
	@Override
	public void playAudio(String audioUrl) {
		this.pendingAudio.add(audioUrl);

	}
	public void setAjaxLoadingTimeoutMs(long ajaxLoadingTimeoutMS) {
		this.ajaxLoadingTimeoutMs = ajaxLoadingTimeoutMS;
	}
	public void setAjaxLoadingCheckPeriodMs(long ajaxLoadingCheckPeriodMs) {
		this.ajaxLoadingCheckPeriodMs = ajaxLoadingCheckPeriodMs;
	}
	public String getPortalDialogHeaderTitle() {
		return portalDialogHeaderTitle;
	}
	public void setPortalDialogHeaderTitle(String portalDialogHeaderTitle) {
		this.portalDialogHeaderTitle = portalDialogHeaderTitle;
	}

	@Override
	public boolean getIsClosed() {
		return this.closed;
	}
	@Override
	public void setUrlParams(LinkedHashMap<String, String> url) {
		if (url.equals(urlParams))
			return;
		this.urlParams.clear();
		this.urlParams.putAll(url);
		this.urlParamsChanged = true;
	}
	@Override
	public LinkedHashMap<String, String> getUrlParams() {
		return this.urlParams;
	}
	@Override
	public String putUrlParam(String key, String value) {
		String r = value == null ? this.urlParams.remove(key) : this.urlParams.put(key, value);
		if (OH.ne(r, value))
			this.urlParamsChanged = true;
		return r;
	}
	@Override
	public void requestFocusOnField(Portlet portlet, String attachmentId) {
		if (portlet == null)
			this.requestFocuseOnField = null;
		else
			this.requestFocuseOnField = new Tuple2<Portlet, String>(portlet, attachmentId);
	}
	@Override
	public void onFieldFocused(Portlet portlet, String attachmentId) {
		focusPortlet(portlet);
		this.focusedField = new Tuple2<Portlet, String>(portlet, attachmentId);
	}
	@Override
	public void onFieldBlured(Portlet portlet, String attachmentId) {
		if (this.focusedField != null && this.focusedField.getA() == portlet && OH.eq(this.focusedField.getB(), attachmentId))
			this.focusedField = null;
	}
	@Override
	public Tuple2<Portlet, String> getFocusedField() {
		return this.focusedField;
	}
}
