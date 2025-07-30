package com.f1.ami.center.table;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiCommonProperties;
import com.f1.ami.center.AmiCenterGlobalProcess;
import com.f1.ami.center.AmiCenterProperties;
import com.f1.ami.center.AmiCenterState;
import com.f1.ami.center.AmiDboFactoryWrapper;
import com.f1.ami.center.dbo.AmiDbo;
import com.f1.ami.center.procs.AmiStoredProc;
import com.f1.ami.center.procs.AmiStoredProcFactory;
import com.f1.ami.center.replication.AmiCenterReplicator;
import com.f1.ami.center.sysschema.AmiSchema;
import com.f1.ami.center.table.persist.AmiTablePersisterFactory;
import com.f1.ami.center.timers.AmiTimer;
import com.f1.ami.center.timers.AmiTimerBindingImpl;
import com.f1.ami.center.timers.AmiTimerFactory;
import com.f1.ami.center.triggers.AmiServicePlugin;
import com.f1.ami.center.triggers.AmiTimedRunnable;
import com.f1.ami.center.triggers.AmiTrigger;
import com.f1.ami.center.triggers.AmiTriggerFactory;
import com.f1.ami.client.AmiCenterClient;
import com.f1.base.CalcFrame;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.LH;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiImdbImpl implements AmiImdb {

	private static final Logger log = LH.get();

	private AmiSchema systemSchema;
	private boolean startupComplete;
	final private AmiCenterState state;
	final private ContainerTools tools;
	final private AmiImdbTimerManager timerManager;
	final private AmiImdbObjectsManager objectsManager;
	final private AmiImdbScriptManager scriptManager;
	final private AmiImdbFactoriesManager factoryManager;
	final private AmiImdbSessionManagerService sessionManager;
	private int maxStackSize;
	private boolean timerLoggingOptionEnabled;
	private AmiCenterReplicator replicator;
	private final int defaultQueryLimit;

	public AmiImdbImpl(AmiCenterState state, CalcFrame globalVars) {
		AmiCenterClient client = new AmiCenterClient("!!AMICENTER!!", state.getPartition().getContainer());
		this.state = state;
		this.sessionManager = state.getSessionManager();
		this.tools = state.getPartition().getContainer().getTools();
		this.defaultQueryLimit = tools.getOptional(AmiCommonProperties.PROPERTY_AMI_AMISCRIPT_DEFAULT_LIMIT, AmiCommonProperties.DEFAULT_AMISCRIPT_LIMIT);
		this.replicator = new AmiCenterReplicator(this, client);
		this.maxStackSize = tools.getOptional(AmiCenterProperties.PROPERTY_AMI_MAX_STACK_SIZE, 10);
		this.timerLoggingOptionEnabled = tools.getOptional(AmiCenterProperties.PROPERTY_AMI_TIMER_LOGGING_ENABLED, true);
		this.timerManager = new AmiImdbTimerManager(this);
		this.objectsManager = new AmiImdbObjectsManager(this);
		this.factoryManager = new AmiImdbFactoriesManager(this);
		this.scriptManager = state.getScriptManager();
	}

	public void initSystemSchema() {
		this.systemSchema = new AmiSchema(this, this.getState().getGlobalSession());
	}

	public AmiCenterState getState() {
		return this.state;
	}

	public void flushPersisted(CalcFrameStack sf) {
		this.objectsManager.flushPersisted(sf);
	}

	public AmiSchema getSystemSchema() {
		return this.systemSchema;
	}
	public boolean isStartupComplete() {
		return this.startupComplete;
	}

	public void onStartupComplete() {
		AmiImdbSession globalSession = state.getGlobalSession();
		AmiCenterGlobalProcess globalProcess = state.getGlobalProcess();
		try {
			CalcFrameStack sf = globalSession.getReusableTopStackFrame();
			globalSession.lock(globalProcess, null);
			this.getState().getHdb().onStartupComplete();
			this.objectsManager.startupAmiSchema(sf);
			this.systemSchema.buildSystemTables(sf);
			this.startupComplete = true;
			globalProcess.setProcessStatus(AmiCenterProcess.PROCESS_RUN);
			this.replicator.onStaruptComplete();
			onSchemaChanged(sf);
			this.objectsManager.fireTriggers(sf);
		} finally {
			globalSession.unlock();
			globalProcess.setProcessStatus(AmiCenterProcess.PROCESS_IDLE);
		}
	}

	public void onSchemaChanged(CalcFrameStack sf) {
		if (startupComplete && this.systemSchema != null) {
			systemSchema.writeManagedSchemaFile(sf);
			systemSchema.buildSystemTables(sf);
			this.objectsManager.fireSchemaChanged(sf);
			this.replicator.onSchemaChanged(sf);
		}
	}

	public void onProcessedEventsComplete() {
		this.timerManager.onProcessedEventsComplete();
		flushPersisted(state.getReusableTopStackFrame());
		//TODO:this.globalSession.onProcessEventsComplete();
	}

	public void fireTimersFromTimerEvent() {
		this.timerManager.fireTimers(getNow(), true);
	}

	public AmiImdbObjectsManager getObjectsManager() {
		return this.objectsManager;
	}
	public AmiImdbFactoriesManager getFactoriesManager() {
		return this.factoryManager;
	}
	public AmiImdbScriptManager getScriptManager() {
		return this.scriptManager;
	}

	public void onMonitor(long now, CalcFrameStack sf) {
		objectsManager.processTableExpirys(now, sf);
	}

	public void registerNeedsflush(AmiTableImpl table) {
		this.objectsManager.registerNeedsflush(table);
	}
	public void registerTimer(AmiTimedRunnable trigger, long millis, int priority, Object correlationObject) {
		this.timerManager.registerTimer(trigger, millis, priority, correlationObject);
	}

	public AmiTableImpl getAmiTable(short type) {
		return this.objectsManager.getAmiTable(type);
	}
	public AmiTable createTable(AmiTableDef def, CalcFrameStack sf) {
		return this.objectsManager.addAmiTable(new AmiTableImpl(this, def), sf);
	}
	//////////////////
	//   GENERAL
	//////////////////
	@Override
	public ContainerTools getTools() {
		return this.tools;
	}

	@Override
	public int getStringPoolId(String text) {
		return state.getAmiStringPool(text);
	}

	@Override
	public String getStringPoolString(int text) {
		return state.getAmiValueString(text);
	}

	@Override
	public long getNow() {
		return this.tools.getNow();
	}
	//////////////////
	//   FACTORIES
	//////////////////
	@Override
	public Set<String> getTablePersisterTypes() {
		return this.factoryManager.getTablePersisterTypes();
	}
	@Override
	public AmiTablePersisterFactory getTablePersisterFactory(String type) {
		return this.factoryManager.getTablePersisterFactory(type);
	}

	@Override
	public Set<String> getTriggerTypes() {
		return this.factoryManager.getTriggerTypes();
	}

	@Override
	public AmiTriggerFactory getTriggerFactory(String type) {
		return this.factoryManager.getTriggerFactory(type);
	}

	@Override
	public Set<String> getTimerTypes() {
		return this.factoryManager.getTimerTypes();
	}

	@Override
	public AmiTimerFactory getTimerFactory(String type) {
		return this.factoryManager.getTimerFactory(type);
	}

	@Override
	public Set<String> getStoredProcTypes() {
		return this.factoryManager.getStoredProcTypes();
	}

	@Override
	public AmiStoredProcFactory getStoredProcFactory(String type) {
		return this.factoryManager.getStoredProcFactory(type);
	}

	//////////////////
	//   SCRIPT   
	//////////////////
	//	@Override
	//	public void executeSql(String sql, CalcFrame variables, DerivedCellTimeoutController timeout, int limit, SqlPlanListener listener, CalcFrameStack sf) {
	//		DerivedCellCalculatorExpression calc = scriptManager.prepareSql(sql, sf, variables.getTypes(), true, true);
	//		scriptManager.executeSql(calc, variables, AmiImdbScriptManager.ON_EXECUTE_AUTO_HANDLE, timeout, limit, listener, sf);
	//	}
	//
	//	@Override
	//	public DerivedCellCalculatorExpression prepareSql(String amiScript, com.f1.base.CalcTypes types, CalcFrameStack sf) {
	//		return scriptManager.prepareSql(amiScript, sf, types, false, true);
	//	}

	//////////////////
	//   OBJECTS   
	//////////////////
	@Override
	public boolean registerTimerFromNow(String timerName, long offset, TimeUnit timeunit) {
		AmiTimerBindingImpl t = objectsManager.getAmiTimerBinding(timerName);
		return t != null && t.setNextRunTime(timeunit.toMillis(getNow() + offset));
	}

	@Override
	public boolean registerTimerAtTime(String timerName, long timeMillis) {
		AmiTimerBindingImpl t = objectsManager.getAmiTimerBinding(timerName);
		return t != null && t.setNextRunTime(timeMillis);
	}

	@Override
	public AmiTimer getAmiTimer(String name) {
		return this.objectsManager.getAmiTimer(name);
	}
	@Override
	public AmiTrigger getAmiTrigger(String name) {
		return this.objectsManager.getAmiTrigger(name);
	}

	@Override
	public AmiTableImpl getAmiTable(String name) {
		return this.objectsManager.getAmiTable(name);
	}

	@Override
	public AmiTableImpl getAmiTableOrThrow(String name) {
		return this.objectsManager.getAmiTableOrThrow(name);
	}

	@Override
	public Set<String> getAmiTableNamesSorted() {
		return this.objectsManager.getAmiTableNamesSorted();
	}

	@Override
	public <T extends AmiTimer> T getAmiTimerOrThrow(String name, Class<T> clazz) {
		return this.objectsManager.getAmiTimerOrThrow(name, clazz);
	}

	@Override
	public Set<String> getAmiTimerNamesSorted() {
		return this.objectsManager.getAmiTimerNamesSorted();
	}

	@Override
	public AmiStoredProc getAmiStoredProc(String name) {
		return this.objectsManager.getAmiStoredProc(name);
	}

	@Override
	public <T extends AmiStoredProc> T getAmiStoredProcOrThrow(String name, Class<T> clazz) {
		return this.objectsManager.getAmiStoredProcOrThrow(name, clazz);
	}

	@Override
	public Set<String> getAmiStoredProcNamesSorted() {
		return this.objectsManager.getAmiStoredProcNamesSorted();
	}

	@Override
	public Collection<AmiTimer> getAmiTimers() {
		return this.objectsManager.getAmiTimers();
	}

	@Override
	public Collection<AmiStoredProc> getAmiStoredProcs() {
		return this.objectsManager.getAmiStoredProcs();
	}

	@Override
	public Collection<AmiTableImpl> getAmiTables() {
		return this.objectsManager.getAmiTables();
	}

	public Map<String, Table> getStats() {
		return AmiImdbStatsBuilder.getStats(this);
	}

	@Override
	public MethodFactoryManager getAmiScriptMethodFactory() {
		return this.scriptManager.getMethodFactory();
	}

	public int getDefaultQueryTimeoutMs() {
		return 100000;
	}

	public int getDefaultQueryLimit() {
		return defaultQueryLimit;
	}

	public AmiImdbSessionManagerService getSessionManager() {
		return this.sessionManager;
	}

	public int getMaxStackSize() {
		return this.maxStackSize;
	}

	public AmiImdbSession getGlobalSession() {
		return this.state.getGlobalSession();
	}

	public boolean getTimerLoggingOptionEnabled() {
		return this.timerLoggingOptionEnabled;
	}

	@Override
	public Set<String> getServiceNames() {
		return this.objectsManager.getAmiServiceNamesSorted();
	}

	@Override
	public AmiServicePlugin getAmiService(String name) {
		return this.objectsManager.getAmiService(name);
	}

	@Override
	public <T extends AmiServicePlugin> T getAmiServiceOrThrow(String name, Class<T> clazz) {
		return this.objectsManager.getAmiServiceOrThrow(name, clazz);
	}

	public AmiCenterGlobalProcess getGlobalProcess() {
		return this.state.getGlobalProcess();
	}

	public Table getTableNoThrow(String tableName) {
		AmiTableImpl r = this.objectsManager.getAmiTable(tableName);
		return r == null ? null : r.getTable();
	}

	public void assertNotLockedByTrigger(AmiTrigger me, String name) {
		for (AmiTrigger i : this.objectsManager.getAmiTriggers()) {
			if (i == me)
				continue;
			Set<String> lockedTables = i.getLockedTables();
			if (lockedTables != null && lockedTables.contains(name))
				throw new RuntimeException("TABLE " + name + " ALREADY CONTROLLED BY TRIGGER " + i.getBinding().getTriggerName());
		}
	}

	public AmiCenterReplicator getReplicator() {
		return this.replicator;
	}

	@Override
	public AmiDboFactoryWrapper getDboFactory(String typeName) {
		return this.factoryManager.getDboFactory(typeName);
	}
	@Override
	public Set<String> getDboTypes() {
		return this.factoryManager.getDboTypes();
	}
	@Override
	public Set<String> getAmiDboNamesSorted() {
		return this.objectsManager.getAmiDboNamesSorted();
	}
	@Override
	public AmiDbo getAmiDbo(String name) {
		return this.objectsManager.getAmiDbo(name);
	}

}
