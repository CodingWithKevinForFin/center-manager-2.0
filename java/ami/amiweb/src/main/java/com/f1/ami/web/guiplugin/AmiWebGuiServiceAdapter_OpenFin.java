package com.f1.ami.web.guiplugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.web.AmiWebGuiServiceAdapter;
import com.f1.ami.web.AmiWebGuiServiceAdapterPeer;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.impl.DesktopPortlet;
import com.f1.suite.web.portal.impl.DesktopPortlet.Window;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebGuiServiceAdapter_OpenFin implements AmiWebGuiServiceAdapter {
	private static final Logger log = LH.get();

	private static final String JAVASCRIPT_LIBRARY_OPENFIN_NOTIFICATION = "openfin_notification.js";
	private static final String JAVASCRIPT_LIBRARY_OPENFIN = "ami_openfin.js";
	private static final String JAVASCRIPT_NEW_OPENFIN_SERVER = "typeof(OpenFinServer) == 'undefined'?null:new OpenFinServer()";

	//#####################
	//#####  METHODS  #####
	//#####################

	private static final String AMISCRIPT_METHOD_GET_IDENTITY = "getIdentity";
	private static final String AMISCRIPT_METHOD_BRING_TO_FRONT = "bringToFront";
	private static final String AMISCRIPT_METHOD_SET_AS_FOREGROUND = "setAsForeground";
	private static final String AMISCRIPT_METHOD_MAXIMIZE_WINDOW = "maximizeWindow";
	private static final String AMISCRIPT_METHOD_MINIMIZE_WINDOW = "minimizeWindow";
	private static final String AMISCRIPT_METHOD_RESTORE_WINDOW = "restoreWindow";
	private static final String AMISCRIPT_METHOD_SET_LOCATION = "setLocation";
	private static final String AMISCRIPT_METHOD_ADD_LISTENER = "addListener";
	private static final String AMISCRIPT_METHOD_SEND_MESSAGE = "sendMessage";

	private static final String AMISCRIPT_METHOD_CREATE_PROVIDER_CHANNEL = "createProviderChannel";
	private static final String AMISCRIPT_METHOD_DESTROY_PROVIDER_CHANNEL = "destroyProviderChannel";
	private static final String AMISCRIPT_METHOD_CONNECT_CLIENT_CHANNEL = "connectClientChannel";
	private static final String AMISCRIPT_METHOD_DISCONNECT_CLIENT_CHANNEL = "disconnectClientChannel";

	private static final String AMISCRIPT_METHOD_REGISTER_PROVIDER_CHANNEL_LISTENER = "registerProviderChannelListener";
	private static final String AMISCRIPT_METHOD_REGISTER_CLIENT_CHANNEL_LISTENER = "registerClientChannelListener";
	private static final String AMISCRIPT_METHOD_REMOVE_PROVIDER_CHANNEL_LISTENER = "removeProviderChannelListener";
	private static final String AMISCRIPT_METHOD_REMOVE_CLIENT_CHANNEL_LISTENER = "removeClientChannelListener";

	private static final String AMISCRIPT_METHOD_SEND_PROVIDER_CHANNEL_REQUEST = "sendProviderChannelRequest";
	private static final String AMISCRIPT_METHOD_SEND_CLIENT_CHANNEL_REQUEST = "sendClientChannelRequest";
	private static final String AMISCRIPT_METHOD_SEND_CHANNEL_RESPONSE = "sendChannelResponse";
	private static final String AMISCRIPT_METHOD_SEND_NOTIFICATION = "sendNotification";
	private static final String AMISCRIPT_METHOD_ADD_CONTEXT_LISTENER = "addContextListener";
	private static final String AMISCRIPT_METHOD_ADD_INTENT_LISTENER = "addIntentListener";
	private static final String AMISCRIPT_METHOD_BROADCAST = "broadcast";
	private static final String AMISCRIPT_METHOD_RAISE_INTENT = "raiseIntent";
	private static final String AMISCRIPT_METHOD_RAISE_INTENT_BY_APP = "raiseIntentByApp";
	private static final String AMISCRIPT_METHOD_OPEN = "open";

	//#####################
	//##### CALLBACKS #####
	//#####################

	private static final String AMI_SCRIPT_CALLBACK_ON_INIT = "onInit";
	private static final String AMI_SCRIPT_CALLBACK_ON_ERROR = "onError";
	private static final String AMI_SCRIPT_CALLBACK_ON_MESSAGE = "onMessage";
	private static final String AMI_SCRIPT_CALLBACK_ON_RAISE_INTENT = "onRaiseIntent";
	private static final String AMI_SCRIPT_CALLBACK_ON_CONTEXT = "onContext";
	private static final String AMI_SCRIPT_CALLBACK_ON_RECEIVE_INTENT = "onReceiveIntent";
	private static final String AMI_SCRIPT_CALLBACK_ON_NOTIFICATION_ACTION = "onNotificationAction";
	private static final String AMI_SCRIPT_CALLBACK_ON_CHANNEL_REQUEST = "onChannelRequest";
	private static final String AMI_SCRIPT_CALLBACK_ON_CHANNEL_RESPONSE = "onChannelResponse";

	private AmiWebGuiServiceAdapterPeer peer;
	private Map<String, OpenFinCallback> requestPayloads;

	private Object identity;
	private long nextCorrelationId = 0;
	private boolean isInitialized = false;

	class OpenFinCallback {
		final private String correlationId;
		final private String channelId;
		final private String action;
		final private Object payload;

		public OpenFinCallback(String correlationId, String channelId, String action, Object payload) {
			this.correlationId = correlationId;
			this.channelId = channelId;
			this.action = action;
			this.payload = payload;
		}

		public String getChannelId() {
			return channelId;
		}

		public String getAction() {
			return action;
		}

		public Object getPayload() {
			return payload;
		}
	}

	private String getNextCorrelationId() {
		return SH.toString(nextCorrelationId++);
	}

	@Override
	public void init(AmiWebGuiServiceAdapterPeer peer) {
		this.peer = peer;
		this.requestPayloads = new HashMap<String, AmiWebGuiServiceAdapter_OpenFin.OpenFinCallback>();
	}

	@Override
	public String getGuiServiceId() {
		return "OPENFIN";
	}

	@Override
	public String getDescription() {
		return "OpenFin";
	}

	@Override
	public String getAmiscriptClassname() {
		return "OpenFin";
	}

	@Override
	public List<ParamsDefinition> getAmiscriptMethods() {
		//Method Name Return Type String arguments
		List<ParamsDefinition> r = new ArrayList<ParamsDefinition>();
		r.add(new ParamsDefinition(AMISCRIPT_METHOD_GET_IDENTITY, Object.class, "").addDesc("Return Identifier of this adapter"));
		r.add(new ParamsDefinition(AMISCRIPT_METHOD_BRING_TO_FRONT, Object.class, "")
				.addDesc("Bring the current AMI window to the front of the OpenFin stack. This is different from AMI's own bringToFront method."));
		r.add(new ParamsDefinition(AMISCRIPT_METHOD_SET_AS_FOREGROUND, Object.class, "").addDesc("Bring the AMI window to the front."));
		r.add(new ParamsDefinition(AMISCRIPT_METHOD_MAXIMIZE_WINDOW, Object.class, "com.f1.suite.web.portal.impl.DesktopPortlet$Window window")
				.addDesc("Maximize a specific window"));
		r.add(new ParamsDefinition(AMISCRIPT_METHOD_MINIMIZE_WINDOW, Object.class, "com.f1.suite.web.portal.impl.DesktopPortlet$Window window")
				.addDesc("Minimize a specific window"));
		r.add(new ParamsDefinition(AMISCRIPT_METHOD_RESTORE_WINDOW, Object.class, "com.f1.suite.web.portal.impl.DesktopPortlet$Window window")
				.addDesc("Restore the size of a specific window"));
		r.add(new ParamsDefinition(AMISCRIPT_METHOD_SET_LOCATION, Object.class,
				"com.f1.suite.web.portal.impl.DesktopPortlet$Window window,Integer left,Integer top,Integer width,Integer height")
						.addDesc("Sets the location of a specific window"));
		r.add(new ParamsDefinition(AMISCRIPT_METHOD_SEND_MESSAGE, Object.class, "String topic,Object jsonPayload").addDesc("Send a message on a topic").addParamDesc(0,
				"Topic to send message on", "the message to send"));
		r.add(new ParamsDefinition(AMISCRIPT_METHOD_ADD_LISTENER, Object.class, "String topic").addDesc("Listen for events on  a specific topic (see onMessage callback)"));

		r.add(new ParamsDefinition(AMISCRIPT_METHOD_CREATE_PROVIDER_CHANNEL, Object.class, "String channelId").addDesc("Create a provider on a specific channel"));
		r.add(new ParamsDefinition(AMISCRIPT_METHOD_DESTROY_PROVIDER_CHANNEL, Object.class, "String channelId").addDesc("Destroy an existing provider on a specific channel"));
		r.add(new ParamsDefinition(AMISCRIPT_METHOD_CONNECT_CLIENT_CHANNEL, Object.class, "String channelId").addDesc("Connect to a client channel"));
		r.add(new ParamsDefinition(AMISCRIPT_METHOD_DISCONNECT_CLIENT_CHANNEL, Object.class, "String channelId").addDesc("Disconnect from an existing client channel"));

		r.add(new ParamsDefinition(AMISCRIPT_METHOD_REGISTER_PROVIDER_CHANNEL_LISTENER, Object.class, "String channelId,String action")
				.addDesc("Listen for an action on a registered  provider channelId (see onChannelRequest,onChannelResponse callbacks)"));
		r.add(new ParamsDefinition(AMISCRIPT_METHOD_REGISTER_CLIENT_CHANNEL_LISTENER, Object.class, "String channelId,String action")
				.addDesc("Listen for an action on a registered client channelId (see onChannelRequest,onChannelResponse callbacks)"));
		r.add(new ParamsDefinition(AMISCRIPT_METHOD_REMOVE_PROVIDER_CHANNEL_LISTENER, Object.class, "String channelId,String action")
				.addDesc("Remove listener on provider channel"));
		r.add(new ParamsDefinition(AMISCRIPT_METHOD_REMOVE_CLIENT_CHANNEL_LISTENER, Object.class, "String channelId,String action").addDesc("Remove listener on client channel"));

		r.add(new ParamsDefinition(AMISCRIPT_METHOD_SEND_PROVIDER_CHANNEL_REQUEST, Object.class, "String channelId,String action,Object requestPayload,Object identity")
				.addDesc("Send a request as a provider. Returns a correlation id for linking to response (see onChannelResponse callback)"));
		r.add(new ParamsDefinition(AMISCRIPT_METHOD_SEND_CLIENT_CHANNEL_REQUEST, Object.class, "String channelId,String action,Object requestPayload")
				.addDesc("Send a request as a client. Returns a correlation id for linking to response (see onChannelResponse callback)"));
		r.add(new ParamsDefinition(AMISCRIPT_METHOD_SEND_CHANNEL_RESPONSE, Object.class, "String correlationId,Object responsePayload")
				.addDesc("Respond to a request (see onChannelRequest callback)"));
		r.add(new ParamsDefinition(AMISCRIPT_METHOD_RAISE_INTENT, Object.class, "String intent,java.util.Map context"));
		r.add(new ParamsDefinition(AMISCRIPT_METHOD_RAISE_INTENT_BY_APP, Object.class, "String intent,java.util.Map context,java.util.Map appId"));
		r.add(new ParamsDefinition(AMISCRIPT_METHOD_OPEN, Object.class, "java.util.Map targetApp,java.util.Map context"));
		r.add(new ParamsDefinition(AMISCRIPT_METHOD_BROADCAST, Object.class, "java.util.Map context"));
		r.add(new ParamsDefinition(AMISCRIPT_METHOD_SEND_NOTIFICATION, Object.class, "java.util.Map options"));
		r.add(new ParamsDefinition(AMISCRIPT_METHOD_ADD_CONTEXT_LISTENER, Object.class, "String contextType"));
		r.add(new ParamsDefinition(AMISCRIPT_METHOD_ADD_INTENT_LISTENER, Object.class, "String intentType"));
		return r;
	}
	@Override
	public Object onAmiScriptMethod(String name, Object[] args) {
		if (AMISCRIPT_METHOD_GET_IDENTITY.equals(name)) {
			return this.identity;
		} else if (AMISCRIPT_METHOD_MAXIMIZE_WINDOW.equals(name)) {
			if (args.length == 1) {
				DesktopPortlet.Window window = (Window) args[0];
				if (window != null) {
					if (window.isPoppedOut()) {
						RootPortlet root = PortletHelper.findParentByType(window.getPortlet(), RootPortlet.class);
						if (root != null)
							peer.executeJavascriptCallback(name, new Object[] { root.getWindowId() });
					} else
						window.maximizeWindow();
				}
			} else
				peer.executeJavascriptCallback(name, args);
		} else if (AMISCRIPT_METHOD_MINIMIZE_WINDOW.equals(name)) {
			if (args.length == 1) {
				DesktopPortlet.Window window = (Window) args[0];
				if (window != null) {
					if (window.isPoppedOut()) {
						RootPortlet root = PortletHelper.findParentByType(window.getPortlet(), RootPortlet.class);
						if (root != null)
							peer.executeJavascriptCallback(name, new Object[] { root.getWindowId() });
					} else
						window.minimizeWindow();
				}
			} else
				peer.executeJavascriptCallback(name, args);
		} else if (AMISCRIPT_METHOD_RESTORE_WINDOW.equals(name)) {
			if (args.length == 1) {
				DesktopPortlet.Window window = (Window) args[0];
				if (window != null) {
					if (window.isPoppedOut()) {
						RootPortlet root = PortletHelper.findParentByType(window.getPortlet(), RootPortlet.class);
						if (root != null)
							peer.executeJavascriptCallback(name, new Object[] { root.getWindowId() });
					} else
						window.restore();
				}
			} else
				peer.executeJavascriptCallback(name, args);
		} else if (AMISCRIPT_METHOD_SET_LOCATION.equals(name)) {
			DesktopPortlet.Window window = (Window) args[0];
			Integer l = Caster_Integer.INSTANCE.cast(args[1], true);
			Integer t = Caster_Integer.INSTANCE.cast(args[2], true);
			Integer w = Caster_Integer.INSTANCE.cast(args[3], true);
			Integer h = Caster_Integer.INSTANCE.cast(args[4], true);
			if (window != null) {
				if (window.isPoppedOut()) {
					RootPortlet root = PortletHelper.findParentByType(window.getPortlet(), RootPortlet.class);
					if (root != null)
						peer.executeJavascriptCallback(name, new Object[] { root.getWindowId(), l, t, w, h });
				} else {
					DesktopPortlet desktop = this.peer.getService().getDesktop().getDesktop();
					// TODO: make generic function copied from AmiWebScriptMemberMethods_Window setLocation
					if (window.isHidden(true) || window.isMinimized())
						window.floatWindow();
					window.setLeft(Math.max(0, l));
					window.setTop(Math.max(0, t));
					window.setWidth(Math.min(w, desktop.getWidth()));
					window.setHeight(Math.min(h, desktop.getHeight()));
				}
			}

		} else if (AMISCRIPT_METHOD_SEND_PROVIDER_CHANNEL_REQUEST.equals(name)) {
			String channelId = (String) args[0];
			String action = (String) args[1];
			Object payload = (Object) args[2];
			Object identity = (Object) args[3];
			String requestCorrelationId = getNextCorrelationId();

			OpenFinCallback openfinCallback = new OpenFinCallback(requestCorrelationId, channelId, action, payload);
			this.requestPayloads.put(requestCorrelationId, openfinCallback);

			peer.executeJavascriptCallback(name, new Object[] { requestCorrelationId, channelId, action, payload, identity });
		} else if (AMISCRIPT_METHOD_SEND_CLIENT_CHANNEL_REQUEST.equals(name)) {
			String channelId = (String) args[0];
			String action = (String) args[1];
			Object payload = (Object) args[2];
			String requestCorrelationId = getNextCorrelationId();

			OpenFinCallback openfinCallback = new OpenFinCallback(requestCorrelationId, channelId, action, payload);
			this.requestPayloads.put(requestCorrelationId, openfinCallback);

			peer.executeJavascriptCallback(name, new Object[] { requestCorrelationId, channelId, action, payload });
		} else if (AMISCRIPT_METHOD_SEND_CHANNEL_RESPONSE.equals(name)) {
			peer.executeJavascriptCallback(name, args);
		} else
			peer.executeJavascriptCallback(name, args);
		return null;
	}

	@Override
	public List<ParamsDefinition> getAmiScriptCallbacks() {
		return CH.l(//
				new ParamsDefinition(AMI_SCRIPT_CALLBACK_ON_INIT, Object.class, "")//
				, new ParamsDefinition(AMI_SCRIPT_CALLBACK_ON_ERROR, Object.class, "String type,String message")//
				, new ParamsDefinition(AMI_SCRIPT_CALLBACK_ON_MESSAGE, Object.class, "String topic,Object jsonPayload")//
				, new ParamsDefinition(AMI_SCRIPT_CALLBACK_ON_CHANNEL_REQUEST, Object.class,
						"String correlationId,String channelId,String action,Object requestPayload,Object identity")//
				, new ParamsDefinition(AMI_SCRIPT_CALLBACK_ON_CHANNEL_RESPONSE, Object.class,
						"String correlationId,String channelId,String action,String status,Object originalRequestPayload,Object responsePayload")//
				, new ParamsDefinition(AMI_SCRIPT_CALLBACK_ON_CONTEXT, Object.class, "java.util.Map context,Object metadata")//
				, new ParamsDefinition(AMI_SCRIPT_CALLBACK_ON_RAISE_INTENT, Object.class, "java.util.Map intentResolution")//
				, new ParamsDefinition(AMI_SCRIPT_CALLBACK_ON_RECEIVE_INTENT, Object.class, "java.util.Map context")//
				, new ParamsDefinition(AMI_SCRIPT_CALLBACK_ON_NOTIFICATION_ACTION, Object.class, "Object event")//
		);
	}
	@Override
	public void onCallFromJavascript(String name, Object[] args) {
		if (AMI_SCRIPT_CALLBACK_ON_INIT.equals(name)) {
			this.isInitialized = true;
			this.identity = (Object) args[0];
			peer.executeAmiScriptCallback(AMI_SCRIPT_CALLBACK_ON_INIT, new Object[] {});
		} else if (AMI_SCRIPT_CALLBACK_ON_CHANNEL_REQUEST.equals(name)) {
			String channelId = (String) args[0];
			String action = (String) args[1];
			Object requestPayload = (Object) args[2];
			Object identity = (Object) args[3];
			String responseCorrelationId = (String) args[4];

			peer.executeAmiScriptCallback(name, new Object[] { responseCorrelationId, channelId, action, requestPayload, identity });
		} else if (AMI_SCRIPT_CALLBACK_ON_CHANNEL_RESPONSE.equals(name)) {
			String requestCorrelationId = (String) args[0];
			String status = (String) args[1];
			Object responsePayload = (Object) args[2];

			String channelId = null;
			String action = null;
			Object originalRequestPayload = null;
			if (this.requestPayloads.containsKey(requestCorrelationId)) {
				OpenFinCallback callback = this.requestPayloads.get(requestCorrelationId);
				this.requestPayloads.remove(requestCorrelationId);
				channelId = callback.getChannelId();
				action = callback.getAction();
				originalRequestPayload = callback.getPayload();
			}

			peer.executeAmiScriptCallback(name, new Object[] { requestCorrelationId, channelId, action, status, originalRequestPayload, responsePayload });
		} else
			peer.executeAmiScriptCallback(name, args);
	}
	@Override
	public List<String> getJavascriptLibraries() {
		return CH.l(JAVASCRIPT_LIBRARY_OPENFIN, JAVASCRIPT_LIBRARY_OPENFIN_NOTIFICATION);
	}

	@Override
	public String getJavascriptInitialization() {
		return "";
	}

	@Override
	public String getJavascriptNewInstance() {
		return JAVASCRIPT_NEW_OPENFIN_SERVER;
	}

	@Override
	public void onPageLoading() {
		this.isInitialized = false;

	}

	@Override
	public void onLayoutStartup() {
		if (this.isInitialized)
			peer.executeAmiScriptCallback(AMI_SCRIPT_CALLBACK_ON_INIT, new Object[] { this.identity });

	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	@Override
	public String getJavascriptCloseInstance() {
		return "closePlugin";
	}

}
