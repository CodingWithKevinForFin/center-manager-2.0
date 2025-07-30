package com.f1.bootstrap.appmonitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import com.f1.base.IdeableGenerator;
import com.f1.container.Partition;
import com.f1.container.impl.BasicState;
import com.f1.povo.f1app.F1AppClass;
import com.f1.povo.f1app.F1AppEntity;
import com.f1.povo.f1app.F1AppInstance;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.utils.impl.StrongReference;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.LongKeyMap;

public class AppMonitorState extends BasicState {
	private static final Logger log = Logger.getLogger(AppMonitorState.class.getName());

	final StrongReference<Object> tmp = new StrongReference<Object>();

	///////////////
	// state
	///////////////

	private IdeableGenerator generator;
	private F1AppInstance snapshot;
	public boolean isInit() {
		return this.snapshot != null;
	}

	public void init(F1AppInstance snapshot) {
		this.snapshot = snapshot;
		this.f1AppInstanceId = snapshot.getF1AppInstanceId();
		classClassId = resolveClassId(Class.class);
		classes.get(Class.class).setClassId(classClassId);
		this.snapshot.addListener(this.valuedListener);
	}

	@Override
	public void setPartition(Partition partition) {
		super.setPartition(partition);
		this.generator = partition.getContainer().getServices().getGenerator();
	}

	private long f1AppInstanceId;

	////////////////////
	// Id and Sequence fountains
	///////////////////
	final private AtomicLong auditSequenceNumber = new AtomicLong();

	public long nextAuditSequenceNumber() {
		return auditSequenceNumber.incrementAndGet();
	}

	private long monitorId = 0;
	public long generateNextMonitorId() {
		return ++monitorId;
	}

	private int nextSequenceNumber = 0;

	public long nextSequenceNumber() {
		return ++nextSequenceNumber;
	}

	public long currentSequenceNumber() {
		return nextSequenceNumber();
	}

	public Map<Class, F1AppClass> classes = new HashMap<Class, F1AppClass>();
	public Map<Long, F1AppClass> id2classes = new HashMap<Long, F1AppClass>();

	public F1AppInstance getSnapshot() {
		return snapshot;
	}

	//////////////////
	// classes
	//////////////////
	private List<F1AppClass> addedClasses = new ArrayList<F1AppClass>();
	private long classClassId;
	public List<F1AppClass> flushAddedClasses() {
		if (addedClasses.size() == 0)
			return Collections.EMPTY_LIST;
		List<F1AppClass> r = new ArrayList<F1AppClass>(addedClasses);
		addedClasses.clear();
		return r;
	}

	public long resolveClassId(Class clz) {
		if (clz == null)
			throw new NullPointerException("class is null");
		F1AppClass r = classes.get(clz);
		if (r == null) {
			r = generator.nw(F1AppClass.class);
			r.setObjectType(F1AppEntity.TYPE_CLASS);
			r.setClassName(clz.getName());
			r.setId(generateNextMonitorId());
			r.setClassInstanceId(r.getId());
			r.setClassId(classClassId);
			r.setF1AppInstanceId(f1AppInstanceId);
			classes.put(clz, r);
			addedClasses.add(r);
			entities.put(r.getId(), r);
			id2classes.put(r.getId(), r);
		}
		return r.getClassInstanceId();
	}

	public IdeableGenerator getGenerator() {
		return generator;
	}

	////////////////
	// Objects &  Listeners
	////////////////
	final private AppMonitorF1ObjectValuedListener valuedListener = new AppMonitorF1ObjectValuedListener();
	final private BasicMultiMap.List<Class, AppMonitorObjectListener<?, ?>> listeners = new BasicMultiMap.List<Class, AppMonitorObjectListener<?, ?>>();
	final private LongKeyMap<F1AppEntity> entities = new LongKeyMap<F1AppEntity>();

	public byte[] flushChanges() {
		return valuedListener.dump();
	}
	public void startListening() {
		this.valuedListener.startRecording();
	}

	public <T extends F1AppEntity> T newAgentF1Object(AppMonitorObjectListener<T, ?> listener) {
		Class<T> clazz = listener.getAgentType();
		Object src = listener.getObject();
		if (src == null) {
			return null;
		}
		T r = generator.nw(clazz);
		r.setClassId(resolveClassId(src.getClass()));
		addEntity(r);
		listener.setAgentObject(r);
		r.addListener(valuedListener);
		listeners.putMulti(listener.getClass(), listener);
		return r;
	}

	public void addEntity(F1AppEntity entity) {
		entity.setStartedMs(EH.currentTimeMillis());
		entity.setId(generateNextMonitorId());
		entity.setF1AppInstanceId(f1AppInstanceId);
		byte type = AppMonitorUtils.getType(entity.askSchema().askOriginalType());
		entity.setObjectType(type);
		entities.put(entity.getId(), entity);
	}

	public Iterable<AppMonitorObjectListener<?, ?>> getListeners() {
		return listeners.valuesMulti();
	}
	public <T extends AppMonitorObjectListener<?, ?>> Iterable<T> getListeners(Class<T> listenerType) {
		return (Iterable<T>) CH.i(listeners.get(listenerType));
	}
	public Iterable<F1AppEntity> getEntities() {
		return entities.values();
	}

	/////////////////////
	//Audit trail rules
	////////////////////
	private LongKeyMap<AppMonitorAuditRule<?>> auditTrailRules = new LongKeyMap<AppMonitorAuditRule<?>>();
	public void addAuditTrailRule(AppMonitorAuditRule<?> appMonitorRule) {
		auditTrailRules.put(appMonitorRule.getId(), appMonitorRule);
	}
	public AppMonitorAuditRule<?> getAuditTrailRule(long serverId) {
		return auditTrailRules.get(serverId);
	}
	public Iterable<AppMonitorAuditRule<?>> getAuditTrailRules() {
		return auditTrailRules.values();
	}
	public AppMonitorAuditRule<?> removeAuditTrailRule(long id) {
		return auditTrailRules.remove(id);
	}
	public int getAuditTrailRulesCount() {
		return auditTrailRules.size();
	}

	public Iterable<AppMonitorObjectListener<?, ?>> getAuditedListeners() {
		List<AppMonitorObjectListener<?, ?>> r = new ArrayList<AppMonitorObjectListener<?, ?>>();
		for (AppMonitorObjectListener<?, ?> l : getListeners())
			if (l.getIsAudited())
				r.add(l);
		return r;
	}

	///////////////////
	// Generic Converter
	///////////////////
	private ObjectToByteArrayConverter genericConverter;

	public void setGenericConverter(ObjectToByteArrayConverter genericConverter) {
		this.genericConverter = genericConverter;
	}

	public ObjectToByteArrayConverter getGenericConverter() {
		return genericConverter;
	}

	/////////////////
	// App clients
	/////////////////
	private Map<String, AppMonitorClientState> appMonitorClientStatesByPuid = new HashMap<String, AppMonitorClientState>();

	private boolean pingInQueue;

	public Collection<AppMonitorClientState> getAppMonitorClients() {
		return appMonitorClientStatesByPuid.values();
	}

	public AppMonitorClientState addAppMonitorClient(String processUid) {
		AppMonitorClientState r = new AppMonitorClientState(this, processUid, EH.currentTimeMillis());
		CH.putOrThrow(appMonitorClientStatesByPuid, processUid, r);
		return r;
	}

	public AppMonitorClientState removeAppMonitorClientNoThrow(String processUid) {
		return appMonitorClientStatesByPuid.remove(processUid);
	}

	public AppMonitorClientState getAppMonitorClientNoThrow(String puid) {
		return appMonitorClientStatesByPuid.get(puid);
	}

	public void setPingInQueue(boolean b) {
		pingInQueue = b;
	}
	public boolean getPingInQueue() {
		return pingInQueue;
	}

}
