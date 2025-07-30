package com.f1.ami.center.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.f1.ami.center.dbo.AmiDbo;
import com.f1.ami.center.dbo.AmiDboBindingImpl;
import com.f1.ami.center.procs.AmiStoredProc;
import com.f1.ami.center.procs.AmiStoredProcBindingImpl;
import com.f1.ami.center.timers.AmiTimer;
import com.f1.ami.center.timers.AmiTimerBindingImpl;
import com.f1.ami.center.triggers.AmiServicePlugin;
import com.f1.ami.center.triggers.AmiTrigger;
import com.f1.ami.center.triggers.AmiTriggerBindingImpl;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiImdbObjectsManager {
	private static final Comparator<String> COMP = SH.COMPARATOR_CASEINSENSITIVE_STRING;
	private static final Logger log = LH.get();

	final private AmiImdbImpl db;
	private boolean startupComplete;

	final private TreeMap<String, AmiServicePlugin> amiServicesByName = new TreeMap<String, AmiServicePlugin>(COMP);

	final private TreeMap<String, AmiTableImpl> amiTablesByName = new TreeMap<String, AmiTableImpl>(COMP);
	final private IntKeyMap<AmiTableImpl> amiTablesByType = new IntKeyMap<AmiTableImpl>();
	private AmiTableImpl[] amiTablesWithExpiry = new AmiTableImpl[0];
	final private List<AmiImdbFlushable> tablesWithPersistersNeedingFlush = new ArrayList<AmiImdbFlushable>();

	final private Map<String, AmiTimerBindingImpl> amiTimerBindingsByName = new TreeMap<String, AmiTimerBindingImpl>(COMP);
	final private Map<String, AmiTimer> amiTimersByName = new TreeMap<String, AmiTimer>(COMP);

	final private Map<String, AmiTriggerBindingImpl> amiTriggerBindingsByName = new TreeMap<String, AmiTriggerBindingImpl>(COMP);
	final private Map<String, AmiTrigger> amiTriggersByName = new TreeMap<String, AmiTrigger>(COMP);

	final private Map<String, AmiStoredProcBindingImpl> amiStoredProcBindingsByName = new TreeMap<String, AmiStoredProcBindingImpl>(COMP);
	final private Map<String, AmiStoredProc> amiStoredProcsByName = new TreeMap<String, AmiStoredProc>(COMP);

	final private Map<String, AmiDboBindingImpl> amiDboBindingsByName = new TreeMap<String, AmiDboBindingImpl>(COMP);
	final private Map<String, AmiDbo> amiDbosByName = new TreeMap<String, AmiDbo>(COMP);

	public AmiImdbObjectsManager(AmiImdbImpl amiImdbImpl) {
		this.db = amiImdbImpl;
	}

	public void flushPersisted(CalcFrameStack sf) {
		if (this.tablesWithPersistersNeedingFlush.size() > 0) {
			for (int i = 0; i < this.tablesWithPersistersNeedingFlush.size(); i++) {
				AmiImdbFlushable amiTableImpl = this.tablesWithPersistersNeedingFlush.get(i);
				try {
					amiTableImpl.flushPersister(sf);
				} catch (Exception e) {
					LH.warning(log, "Critical error persisting changes on " + amiTableImpl.getFlushableName(), e);
				}
			}
			this.tablesWithPersistersNeedingFlush.clear();
		}
	}
	private void onSchemaChanged(CalcFrameStack sf) {
		this.db.onSchemaChanged(sf);
	}

	public void processTableExpirys(long now, CalcFrameStack sf) {
		for (AmiTableImpl e : this.amiTablesWithExpiry)
			e.removeExpired(now, sf);
	}
	public void startupAmiSchema(CalcFrameStack sf) {
		startServices(AmiServicePlugin.STATE_STEP1_IMDB_INITIALIZED, sf);
		LH.info(log, "Starting AmiTables");
		for (AmiTableImpl i : this.amiTablesByName.values()) {
			try {
				LH.info(log, "Starting AmiTable: ", i.getName());
				i.startup(this.db, sf);
			} catch (Exception e) {
				throw new RuntimeException("Table failed to startup: " + i.getName(), e);
			}
		}
		startServices(AmiServicePlugin.STATE_STEP2_TABLES_INITIALIZED, sf);
		LH.info(log, "Starting AmiProcedures");
		for (AmiStoredProcBindingImpl i : this.amiStoredProcBindingsByName.values()) {
			try {
				LH.info(log, "Starting AmiStoredProc: ", i.getStoredProcName(), " (", OH.getClassName(i.getStoredProc()), ")");
				i.startup(this.db, sf);
			} catch (Exception e) {
				throw new RuntimeException("Stored Procedure " + i.getStoredProcName() + " failed to startup: ", e);
			}
		}
		startServices(AmiServicePlugin.STATE_STEP3_TRIGGERS_INITIALIZED, sf);
		LH.info(log, "Starting AmiTriggers");
		for (AmiTriggerBindingImpl i : CH.sort(getAmiTriggerBindings(), AmiTableUtils.TRIGGER_PRIORITY_COMPARATOR)) {
			try {
				LH.info(log, "Starting AmiTrigger on ", i.getTableNames(), ": ", i.getTriggerName(), " (", OH.getClassName(i.getTrigger()), ")");
				bindTriggersToTables(i, sf);
				i.startup(this.db, sf);
			} catch (Exception e) {
				throw new RuntimeException("Trigger failed to startup: " + i.getTableNames() + ": " + i.getTriggerName(), e);
			}
		}
		startServices(AmiServicePlugin.STATE_STEP4_PROCEDURES_INITIALIZED, sf);
		LH.info(log, "Starting AmiTimers");
		for (AmiTimerBindingImpl i : CH.sort(this.amiTimerBindingsByName.values(), AmiTableUtils.TIMER_PRIORITY_COMPARATOR)) {
			try {
				LH.info(log, "Starting AmiTimer: ", i.getTimerName(), " (", OH.getClassName(i.getTimer()), ")");
				i.startup(this.db, sf);
			} catch (Exception e) {
				throw new RuntimeException("Timer failed to startup: " + i.getTimerName(), e);
			}
		}
		startServices(AmiServicePlugin.STATE_STEP5_DBOS_INITIALIZED, sf);
		LH.info(log, "Starting AmiDbos");
		for (AmiDboBindingImpl i : CH.sort(this.amiDboBindingsByName.values(), AmiTableUtils.DBO_PRIORITY_COMPARATOR)) {
			try {
				LH.info(log, "Starting AmiTimer: ", i.getDboName(), " (", OH.getClassName(i.getDbo()), ")");
				i.startup(this.db, sf);
			} catch (Exception e) {
				throw new RuntimeException("Timer failed to startup: " + i.getDboName(), e);
			}
		}
	}

	public void fireTriggers(CalcFrameStack sf) {
		startServices(AmiServicePlugin.STATE_STEP6_TRIGGERS_FIRED, sf);
		LH.info(log, "Calling onInserted(...) on AmiTriggers from persisted data");
		for (AmiTriggerBindingImpl i : CH.sort(getAmiTriggerBindings(), AmiTableUtils.TRIGGER_PRIORITY_COMPARATOR)) {
			try {
				LH.info(log, "Starting AmiTrigger on ", i.getTableNames(), ": ", i.getTriggerName(), " (", OH.getClassName(i.getTrigger()), ")");
				i.onInitialized(sf);
			} catch (Exception e) {
				throw new RuntimeException("Trigger failed to startup: " + i.getTableNames() + ": " + i.getTriggerName(), e);
			}
		}
		startServices(AmiServicePlugin.STATE_STEP7_TIMERS_INITIALIZED, sf);
		this.startupComplete = true;
		LH.info(log, "Imdb Startup Complete");
	}
	private void startServices(byte step, CalcFrameStack sf) {
		String stepStr = AmiTableUtils.toStringForServiceStep(step);
		LH.info(log, "Starting AmiServices, step " + stepStr);
		for (AmiServicePlugin i : this.amiServicesByName.values()) {
			try {
				i.startup(this.db, this.db.getGlobalSession(), step);
			} catch (Exception e) {
				throw new RuntimeException("Service failed to startup on " + stepStr + ": " + i.getPluginId(), e);
			}
		}
	}

	public void fireSchemaChanged(CalcFrameStack sf) {
		if (!this.amiTimerBindingsByName.isEmpty())
			for (AmiTimerBindingImpl i : CH.sort(this.amiTimerBindingsByName.values(), AmiTableUtils.TIMER_PRIORITY_COMPARATOR))
				i.onSchemaChanged(sf);
		for (AmiTriggerBindingImpl i : CH.sort(this.getAmiTriggerBindings(), AmiTableUtils.TRIGGER_PRIORITY_COMPARATOR))
			i.onSchemaChanged(sf);
		for (AmiTableImpl i : this.getAmiTables())
			i.onSchemaChanged();
		if (!this.amiStoredProcBindingsByName.isEmpty())
			for (AmiStoredProcBindingImpl i : this.amiStoredProcBindingsByName.values())
				i.onSchemaChanged(sf);
		if (!this.amiDboBindingsByName.isEmpty())
			for (AmiDboBindingImpl i : this.amiDboBindingsByName.values())
				i.onSchemaChanged(sf);
	}
	public void registerNeedsflush(AmiImdbFlushable table) {
		this.tablesWithPersistersNeedingFlush.add(table);
	}

	///////////////////////
	//   TABLES   
	///////////////////////
	public Set<String> getAmiTableNamesSorted() {
		return this.amiTablesByName.keySet();
	}
	public AmiTableImpl getAmiTable(String name) {
		if (name == null)
			return null;
		return amiTablesByName.get(name);
	}
	public AmiTableImpl getAmiTableOrThrow(String name) {
		return CH.getOrThrow(amiTablesByName, name, "Table not found: ");
	}

	public Collection<AmiTableImpl> getAmiTables() {
		return this.amiTablesByName.values();
	}

	public Collection<AmiServicePlugin> getAmiServices() {
		return this.amiServicesByName.values();
	}
	public Set<String> getAmiServiceNamesSorted() {
		return this.amiServicesByName.keySet();
	}
	public void renameTable(AmiTableImpl table, String newName, CalcFrameStack sf) {
		String oldName = table.getName();
		if (OH.eq(oldName, newName))
			return;
		if (this.amiTablesByName.containsKey(newName))
			throw new RuntimeException("Table already exists: " + newName);
		short oldType = table.getType();
		table.rename(newName, sf);
		this.amiTablesByName.remove(oldName);
		this.amiTablesByName.put(table.getName(), table);
		this.amiTablesByType.remove(oldType);
		this.amiTablesByType.put(table.getType(), table);
		onSchemaChanged(sf);
	}
	public void renameTrigger(String oldName, String newName, CalcFrameStack sf) {
		if (OH.eq(oldName, newName))
			return;
		AmiTriggerBindingImpl triggerBinding = this.amiTriggerBindingsByName.get(oldName);
		if (triggerBinding == null)
			throw new RuntimeException("Trigger not found: " + oldName);
		if (this.amiTriggerBindingsByName.containsKey(newName))
			throw new RuntimeException("Trigger already exists: " + newName);
		triggerBinding.rename(newName);
		this.amiTriggerBindingsByName.remove(oldName);
		this.amiTriggerBindingsByName.put(newName, triggerBinding);
		this.amiTriggersByName.remove(oldName);
		this.amiTriggersByName.put(newName, triggerBinding.getTrigger());
		for (int i = 0; i < triggerBinding.getTableNamesCount(); i++)
			this.getAmiTable(triggerBinding.getTableNameAt(i)).onTriggerRenamed(oldName, newName);
		onSchemaChanged(sf);
	}
	public void renameDbo(String oldName, String newName, CalcFrameStack sf) {
		if (OH.eq(oldName, newName))
			return;
		AmiDboBindingImpl dboBinding = this.amiDboBindingsByName.get(oldName);
		if (dboBinding == null)
			throw new RuntimeException("Dbo not found: " + oldName);
		if (this.amiDboBindingsByName.containsKey(newName))
			throw new RuntimeException("table already exists: " + newName);
		dboBinding.rename(newName);
		this.amiDboBindingsByName.remove(oldName);
		this.amiDboBindingsByName.put(newName, dboBinding);
		this.amiDbosByName.remove(oldName);
		this.amiDbosByName.put(newName, dboBinding.getDbo());
		this.db.getSessionManager().removeConst(sf, oldName);
		this.db.getSessionManager().putConst(sf, dboBinding.getDboName(), dboBinding.getClassType(), dboBinding.getDbo());
		onSchemaChanged(sf);
	}
	public void renameTimer(String oldName, String newName, CalcFrameStack sf) {
		if (OH.eq(oldName, newName))
			return;
		AmiTimerBindingImpl timerBinding = this.amiTimerBindingsByName.get(oldName);
		if (timerBinding == null)
			throw new RuntimeException("Timer not found: " + oldName);
		if (this.amiTimerBindingsByName.containsKey(newName))
			throw new RuntimeException("Timer already exists: " + newName);
		timerBinding.rename(newName);
		this.amiTimerBindingsByName.remove(oldName);
		this.amiTimerBindingsByName.put(newName, timerBinding);
		this.amiTimersByName.remove(oldName);
		this.amiTimersByName.put(newName, timerBinding.getTimer());
		onSchemaChanged(sf);
	}
	public void renameProcedure(String oldName, String newName, CalcFrameStack sf) {
		if (OH.eq(oldName, newName))
			return;
		AmiStoredProcBindingImpl procedureBinding = this.amiStoredProcBindingsByName.get(oldName);
		if (procedureBinding == null)
			throw new RuntimeException("Procedure not found: " + oldName);
		if (this.amiStoredProcBindingsByName.containsKey(newName))
			throw new RuntimeException("Procedure already exists: " + newName);
		procedureBinding.rename(newName);
		this.amiStoredProcBindingsByName.remove(oldName);
		this.amiStoredProcBindingsByName.put(newName, procedureBinding);
		this.amiStoredProcsByName.remove(oldName);
		this.amiStoredProcsByName.put(newName, procedureBinding.getStoredProc());
		onSchemaChanged(sf);
	}

	public AmiTableImpl removeAmiTable(String name, CalcFrameStack sf) {
		AmiTableImpl r = getAmiTableOrThrow(name);
		r.onDropping(sf);
		amiTablesByType.remove(r.getType());
		amiTablesByName.remove(r.getName());
		if (r.getColumnNoThrow("E") != null)
			unregisterTableWithExpiry(r);
		onSchemaChanged(sf);
		return r;
	}

	public void unregisterTableWithExpiry(AmiTableImpl r) {
		amiTablesWithExpiry = AH.remove(amiTablesWithExpiry, AH.indexOf(r, amiTablesWithExpiry));
	}
	public void registerTableWithExpiry(AmiTableImpl r) {
		amiTablesWithExpiry = AH.append(amiTablesWithExpiry, r);
	}
	public AmiTableImpl addAmiTable(AmiTableImpl r, CalcFrameStack sf) {
		CH.putOrThrow(amiTablesByName, r.getName(), r);
		if (r.getColumnNoThrow("E") != null)
			registerTableWithExpiry(r);
		amiTablesByType.put(r.getType(), r);
		if (startupComplete)
			r.startup(this.db, sf);
		onSchemaChanged(sf);
		return r;
	}

	public AmiTableImpl getAmiTable(short typeKeyId) {
		return amiTablesByType.get(typeKeyId);
	}

	///////////////////////
	//   TIMERS   
	///////////////////////
	public Set<String> getAmiTimerNamesSorted() {
		return this.amiTimersByName.keySet();
	}
	public Collection<AmiTimer> getAmiTimers() {
		return this.amiTimersByName.values();
	}
	public AmiTimer getAmiTimer(String name) {
		return this.amiTimersByName.get(name);
	}
	public AmiTimerBindingImpl getAmiTimerBinding(String name) {
		return this.amiTimerBindingsByName.get(name);
	}
	public Collection<AmiTimerBindingImpl> getAmiTimerBindings() {
		return this.amiTimerBindingsByName.values();
	}
	public <T extends AmiTimer> T getAmiTimerOrThrow(String name, Class<T> clazz) {
		return CH.getOrThrow(clazz, this.amiTimersByName, name, "Timer not found: ");
	}

	public void addAmiTimerBinding(AmiTimerBindingImpl timer, CalcFrameStack sf) {
		if (this.startupComplete) {
			timer.startup(this.db, sf);
		}
		CH.putOrThrow(amiTimerBindingsByName, timer.getTimerName(), timer);
		CH.putOrThrow(amiTimersByName, timer.getTimerName(), timer.getTimer());
		onSchemaChanged(sf);
	}

	public AmiTimerBindingImpl removeAmiTimer(String timerName, CalcFrameStack sf) {
		AmiTimerBindingImpl timer = CH.removeOrThrow(amiTimerBindingsByName, timerName);
		this.amiTimersByName.remove(timerName);
		if (this.startupComplete)
			timer.stop();
		onSchemaChanged(sf);
		return timer;
	}

	public void addAmiService(AmiServicePlugin service) {
		if (this.startupComplete)
			throw new IllegalStateException();
		CH.putOrThrow(this.amiServicesByName, service.getPluginId(), service);
	}

	public <T extends AmiServicePlugin> T getAmiServiceOrThrow(String name, Class<T> clazz) {
		return CH.getOrThrow(clazz, this.amiServicesByName, name);
	}
	public AmiServicePlugin getAmiService(String name) {
		return this.amiServicesByName.get(name);
	}

	///////////////////////
	//   TRIGGERS   
	///////////////////////
	public Set<String> getAmiTriggerNamesSorted() {
		return this.amiTriggersByName.keySet();
	}
	public Collection<AmiTrigger> getAmiTriggers() {
		return this.amiTriggersByName.values();
	}
	public AmiTrigger getAmiTrigger(String name) {
		return this.amiTriggersByName.get(name);
	}
	public AmiTriggerBindingImpl getAmiTriggerBinding(String name) {
		return this.amiTriggerBindingsByName.get(name);
	}
	public Collection<AmiTriggerBindingImpl> getAmiTriggerBindings() {
		return this.amiTriggerBindingsByName.values();
	}
	public <T extends AmiTrigger> T getAmiTriggerOrThrow(String name, Class<T> clazz) {
		return CH.getOrThrow(clazz, this.amiTriggersByName, name, "Trigger not found: ");
	}

	public void addTrigger(AmiTriggerBindingImpl trigger, CalcFrameStack fs) {
		CH.putOrThrow(amiTriggerBindingsByName, trigger.getTriggerName(), trigger);
		CH.putOrThrow(amiTriggersByName, trigger.getTriggerName(), trigger.getTrigger());
	}
	public boolean bindTriggersToTables(AmiTriggerBindingImpl trigger, CalcFrameStack sf) {
		OH.assertEqIdentity(CH.getOrThrow(amiTriggerBindingsByName, trigger.getTriggerName()), trigger);
		for (int i = 0; i < trigger.getTableNamesCount(); i++) {
			final AmiTableImpl amiTable = getAmiTable(trigger.getTableNameAt(i));
			if (amiTable == null) {
				throw new RuntimeException("Trigger " + trigger.getTriggerName() + " references missing table: " + trigger.getTableNameAt(i));
			}
		}
		for (int i = 0; i < trigger.getTableNamesForBindingCount(); i++) {
			final AmiTableImpl amiTable = getAmiTable(trigger.getTableNameForBindingAt(i));
			amiTable.addTrigger(trigger, sf);
		}
		return true;
	}

	public AmiTriggerBindingImpl removeAmiTrigger(String triggerName, CalcFrameStack sf) {
		AmiTriggerBindingImpl trigger = CH.removeOrThrow(amiTriggerBindingsByName, triggerName);
		this.amiTriggersByName.remove(triggerName);
		if (this.startupComplete)
			trigger.stop(sf);
		for (int i = 0; i < trigger.getTableNamesForBindingCount(); i++)
			getAmiTable(trigger.getTableNameForBindingAt(i)).removeTrigger(trigger.getTriggerName(), sf);
		onSchemaChanged(sf);
		return trigger;
	}
	///////////////////////
	//   STORED PROCEDURE   
	///////////////////////
	public Set<String> getAmiStoredProcNamesSorted() {
		return this.amiStoredProcsByName.keySet();
	}
	public Collection<AmiStoredProc> getAmiStoredProcs() {
		return this.amiStoredProcsByName.values();
	}
	public AmiStoredProc getAmiStoredProc(String name) {
		return this.amiStoredProcsByName.get(name);
	}
	public AmiStoredProcBindingImpl getAmiStoredProcBinding(String name) {
		return this.amiStoredProcBindingsByName.get(name);
	}
	public Collection<AmiStoredProcBindingImpl> getAmiStoredProcBindings() {
		return this.amiStoredProcBindingsByName.values();
	}
	public <T extends AmiStoredProc> T getAmiStoredProcOrThrow(String name, Class<T> clazz) {
		return CH.getOrThrow(clazz, this.amiStoredProcsByName, name, "StoredProc not found: ");
	}

	public void addAmiStoredProcBinding(AmiStoredProcBindingImpl storedProc, CalcFrameStack sf) {
		if (this.startupComplete)
			storedProc.startup(this.db, sf);
		CH.putOrThrow(amiStoredProcBindingsByName, storedProc.getStoredProcName(), storedProc);
		CH.putOrThrow(amiStoredProcsByName, storedProc.getStoredProcName(), storedProc.getStoredProc());
		onSchemaChanged(sf);
	}

	public AmiStoredProcBindingImpl removeAmiStoredProc(String storedProcName, CalcFrameStack sf) {
		AmiStoredProcBindingImpl timer = CH.removeOrThrow(amiStoredProcBindingsByName, storedProcName);
		this.amiStoredProcsByName.remove(storedProcName);
		if (this.startupComplete)
			timer.stop();
		onSchemaChanged(sf);
		return timer;
	}

	///////////////////////
	//   DBOS   
	///////////////////////
	public Set<String> getAmiDboNamesSorted() {
		return this.amiDbosByName.keySet();
	}
	public Collection<AmiDbo> getAmiDbos() {
		return this.amiDbosByName.values();
	}
	public AmiDbo getAmiDbo(String name) {
		return this.amiDbosByName.get(name);
	}
	public AmiDboBindingImpl getAmiDboBinding(String name) {
		return this.amiDboBindingsByName.get(name);
	}
	public Collection<AmiDboBindingImpl> getAmiDboBindings() {
		return this.amiDboBindingsByName.values();
	}
	public <T extends AmiDbo> T getAmiDboOrThrow(String name, Class<T> clazz) {
		return CH.getOrThrow(clazz, this.amiDbosByName, name, "Dbo not found: ");
	}

	public void addDbo(AmiDboBindingImpl dbo, CalcFrameStack fs) {
		CH.putOrThrow(amiDboBindingsByName, dbo.getDboName(), dbo);
		CH.putOrThrow(amiDbosByName, dbo.getDboName(), dbo.getDbo());

		this.db.getSessionManager().putConst(fs, dbo.getDboName(), dbo.getClassType(), dbo.getDbo());
	}
	public AmiDboBindingImpl removeAmiDbo(String triggerName, CalcFrameStack fs) {
		AmiDboBindingImpl dbo = CH.removeOrThrow(amiDboBindingsByName, triggerName);
		this.amiDbosByName.remove(triggerName);
		if (this.startupComplete)
			dbo.stop(fs);

		this.db.getSessionManager().removeConst(fs, dbo.getDboName());
		onSchemaChanged(fs);
		return dbo;
	}

}
