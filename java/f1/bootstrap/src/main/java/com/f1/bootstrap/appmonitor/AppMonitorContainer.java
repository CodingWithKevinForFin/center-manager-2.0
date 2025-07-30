package com.f1.bootstrap.appmonitor;

import com.f1.base.Factory;
import com.f1.base.Message;
import com.f1.bootstrap.Bootstrap;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.bootstrap.F1Constants;
import com.f1.bootstrap.appmonitor.marshalling.GenericObjectToByteArrayConverter;
import com.f1.container.Container;
import com.f1.container.Suite;
import com.f1.container.impl.BasicContainer;
import com.f1.container.impl.ContainerHelper;
import com.f1.msgdirect.MsgDirectConnection;
import com.f1.msgdirect.MsgDirectConnectionConfiguration;
import com.f1.msgdirect.MsgDirectTopicConfiguration;
import com.f1.povo.f1app.audit.F1AppAuditTrailRuleSet;
import com.f1.povo.f1app.reqres.F1AppChangeLogLevelRequest;
import com.f1.povo.f1app.reqres.F1AppChangesRequest;
import com.f1.povo.f1app.reqres.F1AppInspectPartitionRequest;
import com.f1.povo.f1app.reqres.F1AppInterruptThreadRequest;
import com.f1.povo.f1app.reqres.F1AppResponse;
import com.f1.povo.f1app.reqres.F1AppSnapshotRequest;
import com.f1.povo.f1app.reqres.F1AppSnapshotResponse;
import com.f1.speedlogger.impl.SpeedLoggerInstance;
import com.f1.suite.utils.ClassRoutingProcessor;
import com.f1.suite.utils.msg.MsgSuite;
import com.f1.utils.EH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

public class AppMonitorContainer extends BasicContainer implements Factory<String, String> {

	public static final String APPMONITOR = "APPMONITOR.";
	public static final String PARTITIONID = "F1APPMONITOR";
	private static AppMonitorContainer instance;
	private String mainClassName;
	private String processUid;
	private ContainerBootstrap bs;
	private AppMonitorManagersListener managersListener;
	final private String appName;
	private ObjectToByteArrayConverter genericConverter;

	public AppMonitorContainer(ContainerBootstrap bs) {
		super();
		this.genericConverter = new GenericObjectToByteArrayConverter();
		this.bs = bs;
		this.mainClassName = bs.getMainClass().getName();
		this.appName = SH.is(bs.getAppNameProperty()) ? bs.getAppNameProperty() : EH.getStartupSimpleClassName();
		this.processUid = bs.getProcessUid();
		getThreadPoolController().setDefaultThreadPoolKey("APPMONITOR");
		getThreadPoolController().setDefaultThreadPoolSize(2);
		getThreadPoolController().setUseAggressiveThreadPool(false);
	}

	public void init() {
		super.init();

		// JSON converting
		ObjectToJsonConverter jsonConverter = new ObjectToJsonConverter();
		jsonConverter.registerConverterLowPriority(new ObjToJsonConverter());
		getServices().setJsonConverter(jsonConverter);

		// Options
		String agentHost = getTools().getOptional(F1Constants.PROPERTY_AGENT_HOST, F1Constants.DEFAULT_AGENT_HOST);
		int agentPort = getTools().getOptional(F1Constants.PROPERTY_AGENT_PORT, F1Constants.DEFAULT_AGENT_PORT);

		// Connectivity
		MsgDirectConnectionConfiguration config = new MsgDirectConnectionConfiguration("app_monitor", this);
		MsgDirectConnection server = new MsgDirectConnection(config);
		server.addTopic(new MsgDirectTopicConfiguration("in", agentHost, agentPort, "f1.agent.to.app"));
		server.addTopic(new MsgDirectTopicConfiguration("out", agentHost, agentPort, "f1.app.to.agent"));

		// Message routing and 
		final MsgSuite msgSuite = new MsgSuite(PARTITIONID, server, "in", "out", processUid);

		final AppMonitorClientConnectedProcessor clientConnectedProcessor = new AppMonitorClientConnectedProcessor();
		clientConnectedProcessor.bindToPartition(PARTITIONID);

		final ClassRoutingProcessor<Message> router = new ClassRoutingProcessor<Message>(Message.class);
		router.bindToPartition(PARTITIONID);

		Suite rs = getRootSuite();

		rs.addChild(msgSuite);
		rs.addChild(clientConnectedProcessor);
		rs.addChild(router);

		ContainerHelper.wireCast(rs, msgSuite.inboundOutputPort, router, false);
		rs.wire(msgSuite.statusPort, clientConnectedProcessor, true);

		AppMonitorAuditRulesProcessor auditRuleProcessor = new AppMonitorAuditRulesProcessor();
		AppMonitorSnapshotRequestProcessor snapshotRequestProcessor = new AppMonitorSnapshotRequestProcessor(appName);
		AppMonitorChangesProcessor changesProcessor = new AppMonitorChangesProcessor();
		AppMonitorInterruptCommandProcessor interruptCommandProcessor = new AppMonitorInterruptCommandProcessor();
		AppMonitorChangeLogLevelProcessor changeLogLevelProcessor = new AppMonitorChangeLogLevelProcessor();
		AppMonitorInspectPartitionProcessor inspectPartitionProcessor = new AppMonitorInspectPartitionProcessor();

		auditRuleProcessor.bindToPartition(PARTITIONID);
		snapshotRequestProcessor.bindToPartition(PARTITIONID);
		changesProcessor.bindToPartition(PARTITIONID);
		interruptCommandProcessor.bindToPartition(PARTITIONID);
		changeLogLevelProcessor.bindToPartition(PARTITIONID);
		inspectPartitionProcessor.bindToPartition(PARTITIONID);

		rs.addChild(auditRuleProcessor);
		rs.addChild(snapshotRequestProcessor);
		rs.addChild(changesProcessor);
		rs.addChild(interruptCommandProcessor);
		rs.addChild(changeLogLevelProcessor);
		rs.addChild(inspectPartitionProcessor);

		rs.wire(router.newOutputPort(F1AppAuditTrailRuleSet.class), auditRuleProcessor, true);
		rs.wire(router.newRequestOutputPort(F1AppSnapshotRequest.class, F1AppSnapshotResponse.class), snapshotRequestProcessor, true);
		rs.wire(router.newRequestOutputPort(F1AppInterruptThreadRequest.class, F1AppResponse.class), interruptCommandProcessor, true);
		rs.wire(router.newRequestOutputPort(F1AppChangeLogLevelRequest.class, F1AppResponse.class), changeLogLevelProcessor, true);
		rs.wire(router.newRequestOutputPort(F1AppInspectPartitionRequest.class, F1AppResponse.class), inspectPartitionProcessor, true);
		rs.wire(snapshotRequestProcessor.toChangesProcessorPort, changesProcessor, true);
		rs.wire(changesProcessor.loopback, changesProcessor, true);
		rs.wire(changesProcessor.changesOutputPort, msgSuite.outboundInputPort, true);

		getServices().getGenerator().register(F1AppAuditTrailRuleSet.class, F1AppSnapshotRequest.class, F1AppChangesRequest.class, F1AppInterruptThreadRequest.class,
				F1AppInspectPartitionRequest.class, F1AppChangeLogLevelRequest.class);

		AppMonitorState state = new AppMonitorState();
		state.setGenericConverter(this.genericConverter);
		getPartitionController().putState(PARTITIONID, state);

		this.managersListener = new AppMonitorManagersListener(state);
		this.managersListener.addLogManager(SpeedLoggerInstance.getInstance());
	}

	@Override
	public void start() {
		super.start();
	}
	public static synchronized AppMonitorContainer init(ContainerBootstrap bootStrap) {
		if (instance == null) {
			instance = new AppMonitorContainer(bootStrap);
			instance.setLogNamer(instance);
			instance.getServices().setConverter(bootStrap.getConverter());
			instance.getServices().setGenerator(bootStrap.getGenerator());
			instance.getServices().setPropertyController(bootStrap.getProperties());
			instance.init();
		} else {
			OH.assertEq(bootStrap.getMainClass().getName(), instance.mainClassName);
			OH.assertEq(EH.getProcessUid(), instance.processUid);
		}
		return instance;
	}

	@Override
	public String get(String key) {
		return APPMONITOR + SH.afterLast(key, ".", key);
	}

	public void onNewContainer(Container container) {
		this.managersListener.addContainer(container);
	}

	public Bootstrap getBootstrap() {
		return bs;
	}
	public AppMonitorManagersListener getManagersListener() {
		return this.managersListener;
	}
}
