package com.vortex.client;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.povo.f1app.F1AppClass;
import com.f1.povo.f1app.F1AppContainerScope;
import com.f1.povo.f1app.F1AppDatabase;
import com.f1.povo.f1app.F1AppDispatcher;
import com.f1.povo.f1app.F1AppEntity;
import com.f1.povo.f1app.F1AppInstance;
import com.f1.povo.f1app.F1AppLogger;
import com.f1.povo.f1app.F1AppLoggerSink;
import com.f1.povo.f1app.F1AppMsgTopic;
import com.f1.povo.f1app.F1AppPartition;
import com.f1.povo.f1app.F1AppProcessor;
import com.f1.povo.f1app.F1AppProperty;
import com.f1.povo.f1app.F1AppThreadScope;
import com.f1.utils.BitMaskDescription;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.ToDoException;
import com.f1.utils.structs.LongKeyMap;

public class VortexClientF1AppState {
	private static final Logger log = Logger.getLogger(VortexClientF1AppState.class.getName());
	final private F1AppInstance snapshot;

	private static final BitMaskDescription TYPES_DESCRIPTIONS = new BitMaskDescription("TYPE");
	static {
		TYPES_DESCRIPTIONS.define(F1AppContainerScope.TYPE_CONTAINER, "Container");
		TYPES_DESCRIPTIONS.define(F1AppContainerScope.TYPE_DISPATCHER, "Dispatcher");
		TYPES_DESCRIPTIONS.define(F1AppContainerScope.TYPE_SUITE, "Suite");
		TYPES_DESCRIPTIONS.define(F1AppContainerScope.TYPE_SERVICES, "Services");
		TYPES_DESCRIPTIONS.define(F1AppContainerScope.TYPE_PROCESSOR, "Processor");
		TYPES_DESCRIPTIONS.define(F1AppContainerScope.TYPE_TOOLS, "Tools");
		TYPES_DESCRIPTIONS.define(F1AppContainerScope.TYPE_PARTITION_CONTROLLER, "Partition Controller");
		TYPES_DESCRIPTIONS.define(F1AppContainerScope.TYPE_PARTITION_GENERATOR, "Partition Generator");
		TYPES_DESCRIPTIONS.define(F1AppContainerScope.TYPE_PARTITION_RESOLVER, "Partition Resolver");
		TYPES_DESCRIPTIONS.define(F1AppContainerScope.TYPE_PERSISTENCE_CONTROLLER, "Persistence Controller");
		TYPES_DESCRIPTIONS.define(F1AppContainerScope.TYPE_STATE_GENERATOR, "State Generator");
		TYPES_DESCRIPTIONS.define(F1AppContainerScope.TYPE_SUITE_CONTROLLER, "Suite Controller");
		TYPES_DESCRIPTIONS.define(F1AppContainerScope.TYPE_THREADPOOL_CONTROLLER, "Threadpool Controller");
		TYPES_DESCRIPTIONS.define(F1AppContainerScope.TYPE_THREADSCOPE_CONTROLLER, "Threadscope Controller");
		TYPES_DESCRIPTIONS.define(F1AppContainerScope.TYPE_THROWABLE_HANDLER, "Throwable Handler");
		TYPES_DESCRIPTIONS.define(F1AppContainerScope.TYPE_PORT, "Port");
		TYPES_DESCRIPTIONS.define(F1AppContainerScope.TYPE_PORT_INPUT, "(Input)");
		TYPES_DESCRIPTIONS.define(F1AppContainerScope.TYPE_PORT_OUTPUT, "(Output)");
		TYPES_DESCRIPTIONS.define(F1AppContainerScope.TYPE_PORT_REQUEST, "(Request)");
	}

	final private LongKeyMap<AgentWebObject> objects = new LongKeyMap<AgentWebObject>();
	final private LongKeyMap<F1AppClass> classes = new LongKeyMap<F1AppClass>();
	final private Map<String, AgentWebLogger> loggersById = new HashMap<String, AgentWebLogger>();
	final private Map<String, AgentWebLoggerSink> loggerSinksById = new HashMap<String, AgentWebLoggerSink>();
	final private LongKeyMap<AgentWebThreadScope> threadScopesByContainerScopeId = new LongKeyMap<AgentWebThreadScope>();
	final private LongKeyMap<AgentWebPartition> partitionsByContainerScopeId = new LongKeyMap<AgentWebPartition>();
	final private LongKeyMap<AgentWebContainerScope<?>> containerScopesByContainerScopeId = new LongKeyMap<AgentWebContainerScope<?>>();
	final private LongKeyMap<AgentWebProcessor> processorsByContainerScopeId = new LongKeyMap<AgentWebProcessor>();
	final private LongKeyMap<AgentWebDispatcher> dispatchersByContainerScopeId = new LongKeyMap<AgentWebDispatcher>();
	final private LongKeyMap<AgentWebDatabase> databases = new LongKeyMap<AgentWebDatabase>();
	final private LongKeyMap<AgentWebMsgTopic> topics = new LongKeyMap<AgentWebMsgTopic>();
	final private LongKeyMap<AgentWebContainerScope<F1AppContainerScope>> containers = new LongKeyMap<AgentWebContainerScope<F1AppContainerScope>>();
	final private LongKeyMap<AgentWebProperty> properties = new LongKeyMap<AgentWebProperty>();
	private AgentWebAppInstance appInstance;
	final private long machineInstanceId;

	public VortexClientF1AppState(F1AppInstance instance, long machineInstanceId) {
		this.snapshot = instance;
		createWebObject(instance);
		this.machineInstanceId = machineInstanceId;
	}
	public LongKeyMap<F1AppClass> getClasses() {
		return classes;
	}

	public long getMachineInstanceId() {
		return machineInstanceId;
	}

	public Map<String, AgentWebLogger> getLoggersById() {
		return loggersById;
	}

	public Map<String, AgentWebLoggerSink> getLoggerSinksById() {
		return loggerSinksById;
	}

	public LongKeyMap<AgentWebThreadScope> getThreadScopesByContainerScopeId() {
		return threadScopesByContainerScopeId;
	}

	public LongKeyMap<AgentWebPartition> getPartitionsByContainerScopeId() {
		return partitionsByContainerScopeId;
	}

	public LongKeyMap<AgentWebContainerScope<?>> getContainerScopesByContainerScopeId() {
		return containerScopesByContainerScopeId;
	}

	public LongKeyMap<AgentWebProcessor> getProcessorsByContainerScopeId() {
		return processorsByContainerScopeId;
	}

	public F1AppInstance getSnapshot() {
		return snapshot;
	}

	/*
	public void update(F1AppInstance snapshot, Set<Long> changesSink) {
		if (snapshot.getIsSnapshot()) {
			objects.clear();
			this.snapshot = AppMonitorUtils.clone(snapshot);
			for (F1AppEntity i : this.snapshot.getF1Objects().values())
				createWebObject(i);
		} else if (this.snapshot == null) {
			LH.warning(log, "ignoring update w/o snapshot: ", snapshot.getProcessUid(), " for ", snapshot.getMainClassName(), " user=", snapshot.getUserName());
			return;
		} else {//update
			AppMonitorUtils.copyChangingFields(snapshot, this.snapshot);
			if (snapshot.getF1Objects() != null) {
				for (Map.Entry<Long, F1AppEntity> e : snapshot.getF1Objects().entrySet()) {
					F1AppEntity obj = e.getValue().clone();
					this.snapshot.getF1Objects().put(e.getKey(), obj);
					createWebObject(obj);
				}
			}
		}
		if (snapshot.getStats() != null)
			AppMonitorUtils.processStats(snapshot.getStats(), this.snapshot.getF1Objects(), changesSink);
		if (snapshot.getF1Objects() != null) {
			changesSink.addAll(snapshot.getF1Objects().keySet());
		}
		Map<Long, F1AppEntity> objs = this.snapshot.getF1Objects();
		for (long id : changesSink) {
			F1AppEntity aobj = objs.get(id);
			AgentWebObject obj = this.objects.get(id);
			if (aobj == null) {
				LH.warning(log, "update for missing web object: ", AppMonitorUtils.describe(snapshot), ": ", id, " (", obj, ")");
				continue;
			}
			if (aobj.getObjectType() == F1AppEntity.TYPE_CLASS)
				continue;
			if (obj != null)
				obj.update(aobj);
			else
				LH.warning(log, "update for missing web object: ", AppMonitorUtils.describe(snapshot), ": ", id, " (", aobj, ")");
		}
		if (snapshot.getF1Objects() != null) {
			for (long i : snapshot.getF1Objects().keySet()) {
				AgentWebObject obj = this.getObject(i);
				if (obj != null)
					obj.bind();
			}
		}
	}
	*/
	public AgentWebObject<?> removeObject(long id) {
		AgentWebObject<?> r = objects.removeOrThrow(id);
		switch (r.getObject().getObjectType()) {
			case F1AppEntity.TYPE_LOGGER:
				CH.removeOrThrow(loggersById, ((AgentWebLogger) r).getObject().getLoggerId());
				break;
			case F1AppEntity.TYPE_LOGGER_SINK:
				CH.removeOrThrow(loggerSinksById, ((AgentWebLoggerSink) r).getObject().getSinkId());
				break;
			case F1AppEntity.TYPE_CONTAINER_THREADSCOPE:
				threadScopesByContainerScopeId.removeOrThrow(((AgentWebThreadScope) r).getObject().getContainerScopeId());
				break;
			case F1AppEntity.TYPE_DATABASE:
				partitionsByContainerScopeId.removeOrThrow(r.getObject().getId());
				break;
			case F1AppEntity.TYPE_MSG_TOPIC:
				topics.removeOrThrow(r.getObject().getId());
				break;
			case F1AppEntity.TYPE_PROPERTY:
				properties.removeOrThrow(r.getObject().getId());
				break;
			case F1AppEntity.TYPE_APP_INSTANCE:
				throw new RuntimeException("can nor remove app instance... remove this from manager");
			case F1AppEntity.TYPE_CONTAINER_SCOPE:
			case F1AppEntity.TYPE_CONTAINER_STATE:
				throw new ToDoException();
		}
		return r;
	}
	public AgentWebObject addObject(F1AppEntity f1entity) {
		return createWebObject(f1entity);
	}

	private AgentWebObject createWebObject(F1AppEntity ao) {
		switch (ao.getObjectType()) {
			case F1AppEntity.TYPE_CLASS: {
				AgentWebClass wo = new AgentWebClass((F1AppClass) ao);
				final F1AppClass clazz = (F1AppClass) ao;
				objects.put(wo.getId(), wo);
				classes.put(clazz.getClassInstanceId(), clazz);
				return wo;
			}
			case F1AppEntity.TYPE_LOGGER: {
				AgentWebLogger wo = new AgentWebLogger((F1AppLogger) ao);
				loggersById.put(wo.getObject().getLoggerId(), wo);
				objects.put(wo.getId(), wo);
				return wo;
			}
			case F1AppEntity.TYPE_LOGGER_SINK: {
				AgentWebLoggerSink wo = new AgentWebLoggerSink((F1AppLoggerSink) ao);
				loggerSinksById.put(wo.getObject().getSinkId(), wo);
				objects.put(wo.getId(), wo);
				return wo;
			}
			case F1AppEntity.TYPE_CONTAINER_THREADSCOPE: {
				AgentWebThreadScope wo = new AgentWebThreadScope((F1AppThreadScope) ao);
				threadScopesByContainerScopeId.put(wo.getObject().getContainerScopeId(), wo);
				objects.put(wo.getId(), wo);
				return wo;
			}
			case F1AppEntity.TYPE_CONTAINER_PARTITION: {
				AgentWebPartition wo = new AgentWebPartition((F1AppPartition) ao);
				partitionsByContainerScopeId.put(wo.getObject().getContainerScopeId(), wo);
				objects.put(wo.getId(), wo);
				return wo;
			}
			case F1AppEntity.TYPE_CONTAINER_SCOPE: {
				F1AppContainerScope acs = (F1AppContainerScope) ao;
				AgentWebContainerScope<?> wo;
				if (MH.anyBits(acs.getContainerScopeType(), F1AppContainerScope.TYPE_PROCESSOR)) {
					AgentWebProcessor wop = new AgentWebProcessor((F1AppProcessor) acs);
					wo = wop;
					processorsByContainerScopeId.put(wop.getObject().getContainerScopeId(), wop);
				} else if (MH.anyBits(acs.getContainerScopeType(), F1AppContainerScope.TYPE_DISPATCHER)) {
					AgentWebDispatcher wop = new AgentWebDispatcher((F1AppDispatcher) acs);
					wo = wop;
					dispatchersByContainerScopeId.put(wop.getObject().getContainerScopeId(), wop);
				} else if (MH.anyBits(acs.getContainerScopeType(), F1AppContainerScope.TYPE_CONTAINER)) {
					AgentWebContainerScope<F1AppContainerScope> woc = new AgentWebContainerScope<F1AppContainerScope>(acs);
					wo = woc;
					containers.put(woc.getId(), woc);
				} else
					wo = new AgentWebContainerScope<F1AppContainerScope>(acs);
				containerScopesByContainerScopeId.put(wo.getObject().getContainerScopeId(), wo);
				objects.put(wo.getId(), wo);
				return wo;
			}
			case F1AppEntity.TYPE_DATABASE: {
				AgentWebDatabase wo = new AgentWebDatabase((F1AppDatabase) ao);
				databases.put(wo.getObject().getId(), wo);
				objects.put(wo.getId(), wo);
				return wo;
			}
			case F1AppEntity.TYPE_MSG_TOPIC: {
				AgentWebMsgTopic wo = new AgentWebMsgTopic((F1AppMsgTopic) ao);
				topics.put(wo.getObject().getId(), wo);
				objects.put(wo.getId(), wo);
				return wo;
			}
			case F1AppEntity.TYPE_PROPERTY: {
				AgentWebProperty wo = new AgentWebProperty((F1AppProperty) ao);
				properties.put(wo.getObject().getId(), wo);
				objects.put(wo.getId(), wo);
				return wo;
			}
			case F1AppEntity.TYPE_APP_INSTANCE: {
				AgentWebAppInstance wo = new AgentWebAppInstance((F1AppInstance) ao);
				appInstance = wo;
				objects.put(wo.getId(), wo);
				return wo;
			}
			case F1AppEntity.TYPE_CONTAINER_STATE:
			default:
				throw new RuntimeException("Unknown object type: " + ao);
		}
	}
	public AgentWebObject getObject(Long id) {
		return objects.getOrThrow(id);
	}

	LongKeyMap<F1AppEntity> entities = new LongKeyMap<F1AppEntity>();
	public String getClassName(long classId) {
		F1AppClass r = (F1AppClass) this.classes.get(classId);
		if (r == null) {
			LH.warning(log, "Unknown class id for ", this.appInstance.getSnapshot().getAppName(), "(appid=", this.appInstance.getSnapshot().getId(), "): ", classId);
			return "<unknown>";
		}
		return r.getClassName();
	}

	public abstract class AgentWebObject<T extends F1AppEntity> {
		private T object;
		private String className;
		private LongKeyMap<AgentWebObject<?>> children = new LongKeyMap<AgentWebObject<?>>();
		protected AgentWebObject<?> parent;

		public AgentWebObject(T ob) {
			this.object = ob;
		}

		public long getId() {
			return object.getId();
		}

		public LongKeyMap<AgentWebObject<?>> getChildren() {
			return children;
		}

		public AgentWebObject<?> getParent() {
			return parent;
		}

		public F1AppInstance getSnapshot() {
			return snapshot;
		}

		public T getObject() {
			return object;
		}

		public String getClassName() {
			return className;
		}

		public VortexClientF1AppState getAppState() {
			return VortexClientF1AppState.this;
		}

		public void update(T obj) {
			this.object = obj;
		}

		public void bind() {
			if (this.className == null && getObject().getClassId() != 0)
				this.className = VortexClientF1AppState.this.getClassName(getObject().getClassId());
		}

		public String toString() {
			return SH.s(object, SH.toObjectString(object, new StringBuilder()).append(':')).toString();
		}
	}

	public class AgentWebContainerScope<T extends F1AppContainerScope> extends AgentWebObject<T> {

		public AgentWebContainerScope(T ob) {
			super(ob);
		}

		public void bind() {
			long parentId = getObject().getParentId();
			if (parentId >= 0) {
				parent = VortexClientF1AppState.this.getContainerScopesByContainerScopeId().get(parentId);
				if (parent != null)
					parent.getChildren().put(getObject().getId(), this);
			}
			super.bind();
		}
	}

	public class AgentWebLogger extends AgentWebObject<F1AppLogger> {

		public AgentWebLogger(F1AppLogger ob) {
			super(ob);
		}
	}

	public class AgentWebClass extends AgentWebObject<F1AppClass> {

		public AgentWebClass(F1AppClass ob) {
			super(ob);
		}
	}

	public class AgentWebLoggerSink extends AgentWebObject<F1AppLoggerSink> {

		public AgentWebLoggerSink(F1AppLoggerSink ob) {
			super(ob);
		}

	}

	public class AgentWebThreadScope extends AgentWebObject<F1AppThreadScope> {

		private AgentWebPartition currentPartition;
		private AgentWebProcessor currentProcessor;

		public AgentWebThreadScope(F1AppThreadScope ts) {
			super(ts);
		}

		@Override
		public void update(F1AppThreadScope ts) {
			long partitionId = ts.getCurrentPartitionId();
			long processorId = ts.getCurrentProcessorId();
			//if (getObject().getCurrentPartitionId() != partitionId)
			currentPartition = partitionId == -1 ? null : partitionsByContainerScopeId.get(partitionId);
			//if (getObject().getCurrentProcessorId() != ts.getCurrentProcessorId())
			currentProcessor = processorId == -1 ? null : processorsByContainerScopeId.get(processorId);
			super.update(ts);
		}
		public void bind() {
			currentPartition = partitionsByContainerScopeId.get(getObject().getCurrentPartitionId());
			currentProcessor = processorsByContainerScopeId.get(getObject().getCurrentProcessorId());
		}

		public AgentWebPartition getCurrentPartition() {
			return currentPartition;
		}
		public AgentWebProcessor getCurrentProcessor() {
			return currentProcessor;
		}

	}

	public class AgentWebPartition extends AgentWebObject<F1AppPartition> {

		private String partitionId;

		public AgentWebPartition(F1AppPartition ob) {
			super(ob);
			this.partitionId = OH.toString(ob.getPartitionId());
		}

		public String getPartitionId() {
			return partitionId;
		}

	}

	public class AgentWebProcessor extends AgentWebContainerScope<F1AppProcessor> {

		private String actionTypeClassName = null;
		public AgentWebProcessor(F1AppProcessor ob) {
			super(ob);
		}
		public void bind() {
			super.bind();
			if (this.actionTypeClassName == null && getObject().getActionTypeClassId() != 0)
				this.actionTypeClassName = VortexClientF1AppState.this.getClassName(getObject().getActionTypeClassId());
		}

		public String getActionTypeClassName() {
			return actionTypeClassName;
		}
	}

	public class AgentWebDispatcher extends AgentWebContainerScope<F1AppDispatcher> {

		public AgentWebDispatcher(F1AppDispatcher ob) {
			super(ob);
		}
	}

	public class AgentWebDatabase extends AgentWebObject<F1AppDatabase> {

		public AgentWebDatabase(F1AppDatabase ob) {
			super(ob);
		}
	}

	public class AgentWebProperty extends AgentWebObject<F1AppProperty> {

		public AgentWebProperty(F1AppProperty ob) {
			super(ob);
		}
	}

	public class AgentWebMsgTopic extends AgentWebObject<F1AppMsgTopic> {

		public AgentWebMsgTopic(F1AppMsgTopic ob) {
			super(ob);
		}
	}

	public class AgentWebAppInstance extends AgentWebObject<F1AppInstance> {

		public AgentWebAppInstance(F1AppInstance ob) {
			super(ob);
		}
	}

	public Iterable<AgentWebObject> getObjects() {
		return objects.values();
	}

	public LongKeyMap<AgentWebDispatcher> getDispatchersByContainerScopeId() {
		return dispatchersByContainerScopeId;
	}

	public LongKeyMap<AgentWebDatabase> getDatabases() {
		return databases;
	}

	public LongKeyMap<AgentWebMsgTopic> getTopics() {
		return topics;
	}

	public LongKeyMap<AgentWebContainerScope<F1AppContainerScope>> getContainers() {
		return containers;
	}

	public LongKeyMap<AgentWebProperty> getProperties() {
		return properties;
	}
	public AgentWebAppInstance getSnapshotState() {
		return this.appInstance;
	}

}
