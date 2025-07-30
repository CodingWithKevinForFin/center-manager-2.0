package com.f1.ami.center.table;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiPlugin;
import com.f1.ami.center.AmiCenterProperties;
import com.f1.ami.center.AmiDboFactoryWrapper;
import com.f1.ami.center.dbo.AmiDboFactory;
import com.f1.ami.center.dbo.AmiDboMethodWrapper;
import com.f1.ami.center.procs.AmiStoredProcFactory;
import com.f1.ami.center.table.persist.AmiTablePersisterFactory;
import com.f1.ami.center.timers.AmiTimerFactory;
import com.f1.ami.center.triggers.AmiTriggerFactory;
import com.f1.utils.CH;
import com.f1.utils.structs.table.derived.BasicMethodFactory;

public class AmiImdbFactoriesManager {

	private Map<String, AmiTablePersisterFactory> tablePersisterFactories = new TreeMap<String, AmiTablePersisterFactory>();
	private Map<String, AmiTriggerFactory> triggerFactories = new TreeMap<String, AmiTriggerFactory>();
	private Map<String, AmiTimerFactory> timerFactories = new TreeMap<String, AmiTimerFactory>();
	private Map<String, AmiStoredProcFactory> storedProcFactories = new TreeMap<String, AmiStoredProcFactory>();
	private Map<String, AmiDboFactoryWrapper> dboFactories = new TreeMap<String, AmiDboFactoryWrapper>();
	private AmiImdbImpl db;

	public AmiImdbFactoriesManager(AmiImdbImpl amiImdbImpl) {
		this.db = amiImdbImpl;
	}
	public AmiTablePersisterFactory getTablePersisterFactory(String type) {
		return this.tablePersisterFactories.get(type);
	}
	public AmiTablePersisterFactory getTablePersisterFactoryOrThrow(String type) {
		return CH.getOrThrow(this.tablePersisterFactories, type, "Persister Factory not registered in property " + AmiCenterProperties.PROPERTY_AMI_DB_PERSISTERS);
	}

	public Set<String> getTablePersisterTypes() {
		return this.tablePersisterFactories.keySet();
	}

	public void addTablePersisterFactory(AmiTablePersisterFactory p) {
		CH.putOrThrow(this.tablePersisterFactories, p.getPluginId().toUpperCase(), p);
	}
	public void addTriggerFactory(AmiTriggerFactory p) {
		CH.putOrThrow(this.triggerFactories, p.getPluginId().toUpperCase(), p);
	}

	public Set<String> getTriggerTypes() {
		return this.triggerFactories.keySet();
	}

	public AmiTriggerFactory getTriggerFactory(String type) {
		return this.triggerFactories.get(type.toUpperCase());
	}
	public AmiTriggerFactory getTriggerFactoryOrThrow(String type) {
		return CH.getOrThrow(this.triggerFactories, type.toUpperCase(), "Trigger Factory not registered in property " + AmiCenterProperties.PROPERTY_AMI_DB_TRIGGERS);
	}

	public void addTimerFactory(AmiTimerFactory p) {
		CH.putOrThrow(this.timerFactories, p.getPluginId().toUpperCase(), p);
	}

	public Set<String> getTimerTypes() {
		return this.timerFactories.keySet();
	}

	public AmiTimerFactory getTimerFactory(String type) {
		return this.timerFactories.get(type.toUpperCase());
	}
	public AmiTimerFactory getTimerFactoryOrThrow(String type) {
		return CH.getOrThrow(this.timerFactories, type.toUpperCase(), "Timer Factory not registered in property " + AmiCenterProperties.PROPERTY_AMI_DB_TIMERS);
	}
	public void addStoredProcFactory(AmiStoredProcFactory p) {
		CH.putOrThrow(this.storedProcFactories, p.getPluginId().toUpperCase(), p);
	}

	public Set<String> getStoredProcTypes() {
		return this.storedProcFactories.keySet();
	}

	public AmiStoredProcFactory getStoredProcFactory(String type) {
		return this.storedProcFactories.get(type.toUpperCase());
	}
	public AmiStoredProcFactory getStoredProcFactoryOrThrow(String type) {
		return CH.getOrThrow(this.storedProcFactories, type.toUpperCase(), "Procedure Factory not registered in property " + AmiCenterProperties.PROPERTY_AMI_DB_PROCEDURES);
	}

	public void addDboFactory(AmiDboFactoryWrapper p) {
		CH.putOrThrow(this.dboFactories, p.getPluginId(), p);
		BasicMethodFactory methodsFactory = this.db.getScriptManager().getPredefinedMethodFactory();
		methodsFactory.addVarType(p.getDboClassName(), p.getDboClassType());
		for (AmiDboMethodWrapper method : p.getMethods())
			methodsFactory.addMemberMethod(method);
	}

	public Set<String> getDboTypes() {
		return this.dboFactories.keySet();
	}

	public AmiDboFactoryWrapper getDboFactory(String type) {
		return this.dboFactories.get(type);
	}
	public AmiDboFactory getDboFactoryOrThrow(String type) {
		return CH.getOrThrow(this.dboFactories, type, "Dbo Factory not registered in property " + AmiCenterProperties.PROPERTY_AMI_DB_DBOS);
	}
	public AmiPlugin getPlugin(String pluginType, String pluginName) {
		if (AmiConsts.PLUGIN_TYPE_DATASOURCE.equals(pluginType))
			return db.getState().getDatasourceManager().getAmiDatasourcePlugin(pluginName);
		else if (AmiConsts.PLUGIN_TYPE_PERSISTER.equals(pluginType))
			return this.getTablePersisterFactory(pluginName);
		else if (AmiConsts.PLUGIN_TYPE_DBO.equals(pluginType))
			return this.getDboFactory(pluginName);
		else if (AmiConsts.PLUGIN_TYPE_PROCEDURE.equals(pluginType))
			return this.getStoredProcFactory(pluginName);
		else if (AmiConsts.PLUGIN_TYPE_TIMER.equals(pluginType))
			return this.getTimerFactory(pluginName);
		else if (AmiConsts.PLUGIN_TYPE_TRIGGER.equals(pluginType))
			return this.getTriggerFactory(pluginName);
		else
			throw new RuntimeException("Unknown type: " + pluginType);
	}
}
