package com.f1.ami.relay;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiRelayCommandDefMessage;
import com.f1.ami.amicommon.msg.AmiRelayConnectionMessage;
import com.f1.ami.amicommon.msg.AmiRelayErrorMessage;
import com.f1.ami.amicommon.msg.AmiRelayLoginMessage;
import com.f1.ami.amicommon.msg.AmiRelayLogoutMessage;
import com.f1.ami.amicommon.msg.AmiRelayMessage;
import com.f1.ami.amicommon.msg.AmiRelayObjectDeleteMessage;
import com.f1.ami.amicommon.msg.AmiRelayObjectMessage;
import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandRequest;
import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandResponse;
import com.f1.ami.amicommon.msg.AmiRelayStatusMessage;
import com.f1.ami.amicommon.msg.SingleParamMessage;
import com.f1.ami.relay.fh.AmiFH;
import com.f1.ami.relay.plugins.AmiRelayInvokablePlugin;
import com.f1.base.IdeableGenerator;
import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.ContainerTools;
import com.f1.container.OutputPort;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.VolatilePointer;

public class AmiRelayServer {

	private static final Logger log = LH.get();

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	public static final String PCE_FH_ADDED = "FH_ADDED";
	public static final String PCE_FH_REMOVED = "FH_REMOVED";
	public static final String PCE_FH_LOGGED_IN = "FH_LOGGED_IN";
	public static final String PCE_PROPS_ADDED = "PROPS_ADDED";

	final private int serverPort;
	final private ThreadFactory threadFactory;
	final private IdeableGenerator generator;

	private OutputPort<AmiRelayMessage> outputPort;

	private ConcurrentHashMap<Integer, AmiFHPeer> sessions = new ConcurrentHashMap<Integer, AmiFHPeer>();

	private ObjectGeneratorForClass<AmiRelayStatusMessage> genStatusMsg;
	private ObjectGeneratorForClass<AmiRelayRunAmiCommandResponse> genResponseMsg;
	private ObjectGeneratorForClass<AmiRelayLogoutMessage> genLogoutMsg;
	private ObjectGeneratorForClass<AmiRelayLoginMessage> genLoginMsg;
	private ObjectGeneratorForClass<AmiRelayErrorMessage> genErrorMsg;
	private ObjectGeneratorForClass<AmiRelayObjectMessage> genObjectMsg;
	private ObjectGeneratorForClass<AmiRelayCommandDefMessage> genCommandDefMsg;
	private ObjectGeneratorForClass<AmiRelayObjectDeleteMessage> genObjectDeleteMsg;
	private ObjectGeneratorForClass<AmiRelayConnectionMessage> genConnectionMsg;

	private AtomicInteger nextConnectionId = new AtomicInteger(1);

	private String id;

	private AmiRelayState relayState;

	private AmiRelayCenterDefinition[] centers;

	private OutputPort<AmiRelayMessage> transformOutputPort;

	public AmiRelayServer(int port, ThreadFactory threadFactory, IdeableGenerator generator, OutputPort<AmiRelayMessage> transformOutputPort,
			OutputPort<AmiRelayMessage> outputPort, AmiRelayState amiRelayState, AmiRelayCenterDefinition[] centers) {
		this.centers = centers;
		this.serverPort = port;
		this.threadFactory = threadFactory;
		this.generator = generator;
		this.outputPort = outputPort;
		this.transformOutputPort = transformOutputPort;
		this.relayState = amiRelayState;
		genStatusMsg = this.generator.getGeneratorForClass(AmiRelayStatusMessage.class);
		genResponseMsg = this.generator.getGeneratorForClass(AmiRelayRunAmiCommandResponse.class);
		genLogoutMsg = this.generator.getGeneratorForClass(AmiRelayLogoutMessage.class);
		genLoginMsg = this.generator.getGeneratorForClass(AmiRelayLoginMessage.class);
		genErrorMsg = this.generator.getGeneratorForClass(AmiRelayErrorMessage.class);
		genObjectMsg = this.generator.getGeneratorForClass(AmiRelayObjectMessage.class);
		genCommandDefMsg = this.generator.getGeneratorForClass(AmiRelayCommandDefMessage.class);
		genObjectDeleteMsg = this.generator.getGeneratorForClass(AmiRelayObjectDeleteMessage.class);
		genConnectionMsg = this.generator.getGeneratorForClass(AmiRelayConnectionMessage.class);
	}

	private Map<String, AmiRelayInvokablePlugin> invokerPlugins = new HashMap<String, AmiRelayInvokablePlugin>();

	public ThreadFactory getThreadFactory() {
		return this.threadFactory;
	}

	public void start() throws IOException {
		String invokerPlugins = getTools().getOptional(AmiRelayProperties.OPTION_AMI_RELAY_INVOKABLES);
		if (SH.is(invokerPlugins)) {
			try {
				ContainerTools tools = getTools();
				for (String invokerClass : SH.split(',', invokerPlugins)) {
					StringBuilder errorSink = new StringBuilder();
					AmiRelayInvokablePlugin plugin = AmiUtils.loadPlugin(invokerClass, "Ami Relay Command Plugin", tools, tools.getSubPropertyController(invokerClass + '.'),
							AmiRelayInvokablePlugin.class, errorSink);
					if (plugin == null)
						throw new RuntimeException("Error loading " + invokerClass + ": " + errorSink.toString());
					CH.putOrThrow(this.invokerPlugins, plugin.getPluginId(), plugin, "Ami Relay Command Plugin");
				}
			} catch (Exception e) {
				throw new RuntimeException("Error Processing property: ami.relay.invokables=" + invokerPlugins, e);
			}
		}
		id = getTools().getRequired(AmiRelayProperties.OPTION_AMI_RELAY_ID);
		if (!AmiUtils.isValidVariableName(id, false, false))
			throw new RuntimeException("Property not a valid variable name: " + AmiRelayProperties.OPTION_AMI_RELAY_ID + "=" + id);
		System.out.println("This AMI relay id:  " + id);

		//load the fhs
		PropertyController pfh = getTools().getSubPropertyController("ami.relay.fh.");
		String[] fhs = SH.trimArray(SH.split(',', pfh.getRequired("active")));

		for (String fh : fhs) {
			PropertyController fhp = pfh.getSubPropertyController(fh + ".");
			boolean start = fhp.getOptional("start", true);
			String clazz = fhp.getRequired("class");

			PropertyController props = fhp.getSubPropertyController("props.");

			//create class
			try {
				AmiFH f = (AmiFH) OH.forName(clazz).newInstance();
				if (start)
					initAndStartFH(f, fh, props);
				else
					initFH(f, fh, props);
			} catch (Exception e) {
				throw new RuntimeException("Failed to instantiate fh with class - " + clazz, e);
			}
		}
	}

	public void stop() {
		//stop all FHs
		for (AmiFHPeer fhp : sessions.values()) {
			AmiFH fh = fhp.getFh();
			if (AmiFH.STATUS_STARTED == fh.getStatus()) {
				log.info("Attempting to stop fh " + fh);
				fh.stop();

				log.info("fh " + fh + " stopped " + (AmiFH.STATUS_STOPPED == fh.getStatus() ? "" : "un") + "succesfullyl");
			}
		}
	}

	public void initFH(AmiFH fh, String name, PropertyController fhProps) {
		LH.info(log, "Initializing fh - " + fh);
		int id = nextConnectionId.getAndIncrement();
		AmiFHPeer peer = new AmiFHPeer(this, fh, id, name);
		fh.init(id, name, this.getTools(), fhProps, peer);
		this.sessions.put(peer.getFhId(), peer);

		//notify
		this.pcs.firePropertyChange(PCE_FH_ADDED, null, fh);
	}

	public void initAndStartFH(AmiFH fh, String name) {
		initAndStartFH(fh, name, this.getTools());
	}

	public void initAndStartFH(AmiFH fh, String name, PropertyController fhProps) {
		initFH(fh, name, fhProps);
		fh.start();

		switch (fh.getStatus()) {
			case AmiFH.STATUS_STARTED:
				log.info("Started fh " + fh + " successfully");
				break;

			case AmiFH.STATUS_START_FAILED:
				log.severe("Failed to start fh " + fh + " " + fh.getStatusReason());
				break;
		}

	}

	public void appendNewProperties(PropertyController props) {
		getTools().getProperties().putAll(props.getProperties());

		pcs.firePropertyChange(PCE_PROPS_ADDED, null, props);
	}

	public void onStatus(AmiFHPeer session, byte[] params) {
		AmiRelayStatusMessage m = genStatusMsg.nw();
		createMessage(m, params);
		m.setConnectionId(session.getFhId());
		m.setAppIdStringKey(session.getFhAppIdKey());
		sendMessage(m);
	}

	public void onResponse(AmiFHPeer session, String id, int status, String msg, String script, Map<String, Object> params) {
		AmiRelayRunAmiCommandResponse m = genResponseMsg.nw();
		m.setParams(CH.isEmpty(params) ? null : params);
		m.setConnectionId(session.getFhId());
		m.setCommandUid(id);
		m.setStatusCode(status);
		m.setMessage(msg);
		m.setAmiScript(script);
		m.setOk(true);
		VolatilePointer<AmiRelayRunAmiCommandResponse> p = this.responses.get(id);
		if (p != null) {
			p.put(m);
			OH.notify(p);
		}
	}

	public void onError(AmiFHPeer session, byte[] params, CharSequence bufout) {
		AmiRelayErrorMessage m = genErrorMsg.nw();
		createMessage(m, params);
		m.setConnectionId(session.getFhId());
		m.setAppIdStringKey(session.getFhAppIdKey());
		sendMessage(m);
	}

	public void onLogout(AmiFHPeer session, byte[] params, boolean clean) {
		AmiRelayLogoutMessage m = genLogoutMsg.nw();
		createMessage(m, params);
		m.setConnectionId(session.getFhId());
		m.setAppIdStringKey(session.getFhAppIdKey());
		sendMessage(m);
		sessions.remove(session.getFhId());

		//notify
		session.getFh().stop();
		pcs.firePropertyChange(PCE_FH_REMOVED, session, null);
	}

	public void onLogin(AmiFHPeer session, String options, String plugin, byte[] params) {
		AmiRelayLoginMessage m = genLoginMsg.nw();
		m.setOptions(options);
		m.setAppId(session.getAppId());
		m.setPlugin(plugin);
		createMessage(m, params);
		m.setConnectionId(session.getFhId());
		m.setAppIdStringKey(session.getFhAppIdKey());
		sendMessage(m);

		pcs.firePropertyChange(PCE_FH_LOGGED_IN, null, session);
	}
	public void onConnection(AmiFHPeer session, byte[] params) {
		AmiRelayConnectionMessage m = genConnectionMsg.nw();
		m.setConnectionTime(session.getFh().getConnectionTime());
		m.setRemoteIp(session.getFh().getRemoteIp());
		m.setRemotePort(session.getFh().getRemotePort());
		createMessage(m, params);
		m.setConnectionId(session.getFhId());
		m.setAppIdStringKey(session.getFhAppIdKey());
		sendMessage(m);
	}

	public void onCommandDef(AmiFHPeer session, String id, String title, int lvl, String whereClause, String filterClause, String help, String arguments, String script,
			int priority, String enableClause, String style, String selectMode, String fields, byte[] params, int callbackMask) {
		AmiRelayCommandDefMessage m = genCommandDefMsg.nw();
		createMessage(m, params);
		m.setConnectionId(session.getFhId());
		m.setAppIdStringKey(session.getFhAppIdKey());
		m.setCommandId(id);
		m.setLevel(lvl);
		m.setWhereClause(whereClause);
		m.setHelp(help);
		m.setTitle(title);
		m.setArgumentsJson(arguments);
		m.setAmiScript(script);
		m.setPriority(priority);
		m.setEnabledExpression(enableClause);
		m.setStyle(style);
		m.setSelectMode(selectMode);
		m.setFields(fields);
		m.setFilterClause(filterClause);
		m.setCallbacksMask(callbackMask);
		sendMessage(m);
	}

	public void onObject(AmiFHPeer session, long origSeqnum, String id, String type, long expires, byte[] params) {
		AmiRelayObjectMessage m = genObjectMsg.nw();
		createMessage(m, params);
		m.setConnectionId(session.getFhId());
		m.setAppIdStringKey(session.getFhAppIdKey());
		m.setOrigSeqNum(origSeqnum);
		m.setId(id);
		m.setType(type);
		m.setExpires(expires);
		sendMessage(m);
	}

	public void onObjectDelete(AmiFHPeer session, long origSeqnum, String ids, String type, byte[] params) {
		AmiRelayObjectDeleteMessage m = genObjectDeleteMsg.nw();
		createMessage(m, params);
		m.setConnectionId(session.getFhId());
		m.setAppIdStringKey(session.getFhAppIdKey());
		m.setOrigSeqNum(origSeqnum);
		m.setId(ids);
		m.setType(type);
		sendMessage(m);
	}

	private SingleParamMessage createMessage(SingleParamMessage spm, byte[] bytes) {
		spm.setParams(AH.isEmpty(bytes) ? null : bytes);
		return spm;
	}
	private void sendMessage(AmiRelayMessage m) {
		AmiRelayTransforms tst = this.relayState.getTransformManager().getThreadSafeTransforms();
		if (tst.hasTransforms())
			transformOutputPort.send(m, "RELAY_TRANSFORM", null);
		else {
			if (tst.getDebugMode())
				AmiRelayTransforms.logSkippingTransform(m);

			outputPort.send(m, "AMI_RELAY", null);
		}
	}

	private ConcurrentHashMap<String, VolatilePointer<AmiRelayRunAmiCommandResponse>> responses = new ConcurrentHashMap<String, VolatilePointer<AmiRelayRunAmiCommandResponse>>();

	public AmiRelayRunAmiCommandResponse callCommand(AmiRelayRunAmiCommandRequest action, StringBuilder errorSink) {
		AmiFH fh = getSession(action.getRelayConnectionId());
		if (fh == null) {
			errorSink.append("Application not found for supplied connectionId: " + action.getRelayConnectionId());
			return null;
		}
		VolatilePointer<AmiRelayRunAmiCommandResponse> pointer = new VolatilePointer<AmiRelayRunAmiCommandResponse>();
		responses.put(action.getCommandUid(), pointer);
		fh.call(this, action, errorSink);
		AmiRelayRunAmiCommandResponse r = pointer.get();
		long start = EH.currentTimeMillis();
		synchronized (pointer) {
			while (r == null && OH.waitFromStart(pointer, start, action.getTimeoutMs()))
				r = pointer.get();
		}
		responses.remove(action.getCommandUid());
		return r;

	}

	public AmiFH getSession(int connectionId) {
		AmiFHPeer r = this.sessions.get(connectionId);
		return r == null ? null : r.getFh();
	}

	public Collection<AmiFHPeer> getSessions() {
		return this.sessions.values();
	}

	public ContainerTools getTools() {
		return this.outputPort.getContainer().getTools();
	}

	public int getServerPort() {
		return this.serverPort;
	}

	public String getId() {
		return this.id;
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		this.pcs.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		this.pcs.removePropertyChangeListener(l);
	}

	public AmiRelayInvokablePlugin getInvokable(String typ) {
		return this.invokerPlugins.get(typ);
	}

	public Set<String> getInvokableTypes() {
		return this.invokerPlugins.keySet();
	}

	public short getStringkey(String appId) {
		if (!this.relayState.getPartition().lockForWrite(60, TimeUnit.SECONDS))
			throw new RuntimeException("Could not aquire lock on session after 60 seconds");
		try {
			return this.relayState.getAmiKeyId(appId);
		} finally {
			this.relayState.getPartition().unlockForWrite();
		}
	}

	public void onCenterConnected(byte centerId) {
		for (AmiFHPeer i : this.sessions.values()) {
			try {
				i.getFh().onCenterConnected(centers[centerId].getHostPort());
			} catch (Exception e) {
				LH.warning(log, "Critical error for ", i, e);
			}
		}
	}

}
