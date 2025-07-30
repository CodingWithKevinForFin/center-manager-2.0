package com.f1.ami.web.guiplugin;

import java.util.List;
import java.util.logging.Logger;

import com.f1.ami.web.AmiWebGuiServiceAdapter;
import com.f1.ami.web.AmiWebGuiServiceAdapterPeer;
import com.f1.suite.web.portal.impl.DesktopPortlet;
import com.f1.suite.web.portal.impl.DesktopPortlet.Window;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebGuiServiceAdapter_Finsemble implements AmiWebGuiServiceAdapter {

	private static final String GUI_CALLBACK_ON_CONTEXT = "onContext";
	private static final String GUI_CALLBACK_ON_RAISE_INTENT_FOR_CONTEXT = "onRaiseIntentForContext";
	private static final String GUI_CALLBACK_ON_RAISE_INTENT = "onRaiseIntent";
	private static final String GUI_CALLBACK_ON_GET_CHANNEL = "onGetChannel";
	private static final String GUI_CALLBACK_ON_MESSAGE = "onMessage";
	private static final String GUI_CALLBACK_ON_ERROR = "onError";
	private static final String GUI_CALLBACK_ON_INIT = "onInit";
	private static final String GUI_CALLBACK_ON_INIT_FDC3 = "onInitFdc3";
	private static final String AMISCRIPT_METHOD_BRING_TO_FRONT = "bringToFront";
	private static final String AMISCRIPT_METHOD_BRING_WINDOW_TO_FRONT = "bringWindowToFront";
	private static final String AMISCRIPT_METHOD_UNMAXIMIZE_WINDOW = "unmaximizeWindow";
	private static final String AMISCRIPT_METHOD_ADD_LISTENER = "addListener";
	private static final String AMISCRIPT_METHOD_SEND_MESSAGE = "sendMessage";
	private static final String AMISCRIPT_METHOD_MOVE_WINDOW_TO = "moveWindowTo";
	private static final String AMISCRIPT_METHOD_MAXIMIZE_WINDOW = "maximizeWindow";
	private static final String AMISCRIPT_METHOD_GET_OR_CREATE_CHANNEL = "getOrCreateChannel";
	private static final String AMISCRIPT_METHOD_ADD_CONTEXT_LISTENER = "addContextListener";
	private static final String AMISCRIPT_METHOD_BROADCAST_CHANNEL = "broadcastChannel";
	private static final String AMISCRIPT_METHOD_BROADCAST = "broadcast";
	private static final String AMISCRIPT_METHOD_ADD_CONTEXT_LISTENER_CHANNEL = "addContextListenerChannel";
	private static final String AMISCRIPT_METHOD_RAISE_INTENT = "raiseIntent";
	private static final String AMISCRIPT_METHOD_RAISE_INTENT_FOR_CONTEXT = "raiseIntentForContext";
	private AmiWebGuiServiceAdapterPeer peer;
	private boolean isInit;
	private static final Logger log = LH.get();

	@Override
	public String getGuiServiceId() {
		return "FINSEMBLE";
	}

	@Override
	public String getDescription() {
		return "Finsemble";
	}

	@Override
	public String getAmiscriptClassname() {
		return "Finsemble";
	}

	@Override
	public List<ParamsDefinition> getAmiscriptMethods() {
		return CH.l(//
				new ParamsDefinition(AMISCRIPT_METHOD_SEND_MESSAGE, Object.class, "String channel,Object jsonPayload") //
				, new ParamsDefinition(AMISCRIPT_METHOD_ADD_LISTENER, Object.class, "String channel") //
				, new ParamsDefinition(AMISCRIPT_METHOD_MAXIMIZE_WINDOW, Object.class, "com.f1.suite.web.portal.impl.DesktopPortlet$Window window") //
				, new ParamsDefinition(AMISCRIPT_METHOD_UNMAXIMIZE_WINDOW, Object.class, "com.f1.suite.web.portal.impl.DesktopPortlet$Window window") //
				, new ParamsDefinition(AMISCRIPT_METHOD_MOVE_WINDOW_TO, Object.class, "com.f1.suite.web.portal.impl.DesktopPortlet$Window window,Integer x,Integer y") //
				, new ParamsDefinition(AMISCRIPT_METHOD_BRING_TO_FRONT, Object.class, "") //
				, new ParamsDefinition(AMISCRIPT_METHOD_BRING_WINDOW_TO_FRONT, Object.class, "com.f1.suite.web.portal.impl.DesktopPortlet$Window window") //
				, new ParamsDefinition(AMISCRIPT_METHOD_GET_OR_CREATE_CHANNEL, Object.class, "String channel") //
				, new ParamsDefinition(AMISCRIPT_METHOD_ADD_CONTEXT_LISTENER, Object.class, "String contextType") //
				, new ParamsDefinition(AMISCRIPT_METHOD_ADD_CONTEXT_LISTENER_CHANNEL, Object.class, "String channelId,String contextType") //
				, new ParamsDefinition(AMISCRIPT_METHOD_BROADCAST, Object.class, "java.util.Map context") //
				, new ParamsDefinition(AMISCRIPT_METHOD_BROADCAST_CHANNEL, Object.class, "String channelId,java.util.Map context") //
				, new ParamsDefinition(AMISCRIPT_METHOD_RAISE_INTENT, Object.class, "String intent,java.util.Map context") //
				, new ParamsDefinition(AMISCRIPT_METHOD_RAISE_INTENT_FOR_CONTEXT, Object.class, "java.util.Map context") //
		); //
	}

	@Override
	public Object onAmiScriptMethod(String name, Object[] arg) {
		if (AMISCRIPT_METHOD_MAXIMIZE_WINDOW.equals(name)) {
			DesktopPortlet.Window window = (Window) arg[0];
			if (window != null) {
				if (window.isPoppedOut()) {
					RootPortlet pfp = (RootPortlet) window.getPortletForPopout().getParent();
					peer.executeJavascriptCallback(name, new Object[] { pfp.getWindowId() });
				}
				else
					window.maximizeWindow();
			}
		} else if (AMISCRIPT_METHOD_UNMAXIMIZE_WINDOW.equals(name)) {
			DesktopPortlet.Window window = (Window) arg[0];
			if (window != null) {
				if (window.isPoppedOut()) {
					RootPortlet pfp = (RootPortlet) window.getPortletForPopout().getParent();
					peer.executeJavascriptCallback(name, new Object[] { pfp.getWindowId() });
				}
				else
					window.floatWindow();
			}
		} else if (AMISCRIPT_METHOD_BRING_WINDOW_TO_FRONT.equals(name)) {
			DesktopPortlet.Window window = (Window) arg[0];
			if (window != null) {
				if (window.isPoppedOut()) {
					RootPortlet pfp = (RootPortlet) window.getPortletForPopout().getParent();
					peer.executeJavascriptCallback(name, new Object[] { pfp.getWindowId() });
				}
				else
					window.bringToFront();
			}
		} else if (AMISCRIPT_METHOD_MOVE_WINDOW_TO.equals(name)) {
			DesktopPortlet.Window window = (Window) arg[0];
			Integer l = Caster_Integer.INSTANCE.cast(arg[1], true);
			Integer t = Caster_Integer.INSTANCE.cast(arg[2], true);
			Integer w = window.getWidth();
			Integer h = window.getHeight();
			if (window != null) {
				if (window.isPoppedOut()) {
					RootPortlet rp = (RootPortlet) window.getPortletForPopout().getParent();
					rp.resizeTo(l, t, w, h);
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
		} else if (AMISCRIPT_METHOD_GET_OR_CREATE_CHANNEL.equals(name)) {
			String channelName = Caster_String.INSTANCE.cast(arg[0], true);
			peer.executeJavascriptCallback(name, arg);
			return channelName;
		} else
			peer.executeJavascriptCallback(name, arg);
		return null;
	}
	@Override
	public List<ParamsDefinition> getAmiScriptCallbacks() {
		return CH.l(//
				new ParamsDefinition(GUI_CALLBACK_ON_INIT, Object.class, "") //
				, new ParamsDefinition(GUI_CALLBACK_ON_INIT_FDC3, Object.class, "java.util.Map initOptions") //
				, new ParamsDefinition(GUI_CALLBACK_ON_ERROR, Object.class, "String type,String message") //
				, new ParamsDefinition(GUI_CALLBACK_ON_MESSAGE, Object.class, "String channel,Object jsonPayload")//
				, new ParamsDefinition(GUI_CALLBACK_ON_GET_CHANNEL, Object.class, "java.util.Map channel")//
				, new ParamsDefinition(GUI_CALLBACK_ON_CONTEXT, Object.class, "Object context,Object metadata")//
				, new ParamsDefinition(GUI_CALLBACK_ON_RAISE_INTENT_FOR_CONTEXT, Object.class, "Object intentResolution")//
				, new ParamsDefinition(GUI_CALLBACK_ON_RAISE_INTENT, Object.class, "Object intentResolution")//
		);
	}

	@Override
	public void onCallFromJavascript(String name, Object args[]) {
		//TODO: The onInit Callback potentially may get called twice, if confirmed add an else, because onLayoutStartup should call it
		if (GUI_CALLBACK_ON_INIT.equals(name))
			this.isInit = true;
		peer.executeAmiScriptCallback(name, args);
	}

	@Override
	public String getJavascriptInitialization() {
		return "";
	}

	@Override
	public String getJavascriptNewInstance() {
		return "new FinsembleServer()";
	}

	@Override
	public List<String> getJavascriptLibraries() {
		return CH.l("ami_finsemble.js");
	}
	@Override
	public void init(AmiWebGuiServiceAdapterPeer peer) {
		this.peer = peer;
	}

	@Override
	public void onPageLoading() {
		this.isInit = false;
	}

	@Override
	public void onLayoutStartup() {
		if (isInit)
			peer.executeAmiScriptCallback(GUI_CALLBACK_ON_INIT, OH.EMPTY_OBJECT_ARRAY);
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
