package com.f1.ami.center.triggers;

import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.center.AmiCenterUtils;
import com.f1.ami.center.sysschema.AmiSchema_TRIGGER;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiPreparedRow;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTableImpl;
import com.f1.ami.center.table.AmiTableUtils;
import com.f1.base.Caster;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableStackFramePool;

public class AmiTriggerBindingImpl implements AmiTriggerBinding {

	private static Logger log = LH.get();
	private String triggerName;
	final private AmiTrigger trigger;
	final private int priority;
	final private String[] tableNames;
	final private byte defType;
	final private String triggerType;
	final private Map<String, Object> options;
	final private Map<String, String> optionsStrings;
	private String tableNamesString;
	final private long statsCount[] = new long[8];
	final private long statsNanos[] = new long[8];
	final private long statsFalse[] = new long[8];
	final private long statsError[] = new long[8];
	private AmiImdbImpl db;
	private boolean supportedInserting;
	private boolean supportedInserted;
	private boolean supportedUpdating;
	private boolean supportedUpdated;
	private boolean supportedDeleting;
	private boolean enabled = true;
	private Exception lastException;
	private long lastExceptionTime;
	private boolean isValid;
	private ReusableStackFramePool pool;

	public AmiTriggerBindingImpl(String triggerName, AmiTrigger trigger, int priority, String tableName[], String triggerType, Map<String, Object> options,
			Map<String, String> optionsStrings, byte definedBy) {
		this.triggerName = triggerName;
		this.trigger = trigger;
		this.priority = priority;
		this.tableNames = tableName;
		this.tableNamesString = SH.join(',', this.tableNames);
		this.triggerType = triggerType;
		this.defType = definedBy;
		this.optionsStrings = optionsStrings;
		this.options = options;
	}

	@Override
	public AmiTrigger getTrigger() {
		return this.trigger;
	}

	@Override
	public int getPriority() {
		return this.priority;
	}

	@Override
	public String getTableNameAt(int i) {
		return this.tableNames[i];
	}

	@Override
	public int getTableNamesCount() {
		return this.tableNames.length;
	}

	@Override
	public String getTableNameForBindingAt(int i) {
		if (CH.isEmpty(this.trigger.getBindingTables())) {
			return this.getTableNameAt(i); //default case 
		} else
			return this.trigger.getBindingTables().get(i); //otherwise ask the trigger what the bindings are

	}

	@Override
	public int getTableNamesForBindingCount() {
		if (CH.isEmpty(this.trigger.getBindingTables())) {
			return this.getTableNamesCount();
		} else
			return this.trigger.getBindingTables().size();
	}

	public void onTableNameChanged(String oldName, String newName) {
		int i = AH.indexOf(oldName, this.tableNames);
		if (i != -1)
			this.tableNames[i] = newName;
		this.tableNamesString = SH.join(',', this.tableNames);
	}

	@Override
	public String getTriggerName() {
		return triggerName;
	}

	public void startup(AmiImdbImpl amiImdb, CalcFrameStack sf) {
		this.db = amiImdb;
		this.pool = amiImdb.getState().getStackFramePool();
		this.trigger.startup(amiImdb, this, sf);
	}

	@Override
	public String getTriggerType() {
		return this.triggerType;
	}

	@Override
	public byte getDefType() {
		return this.defType;
	}

	@Override
	public Map<String, Object> getOptions() {
		return options;
	}

	public Map<String, String> getOptionsStrings() {
		return optionsStrings;
	}

	@Override
	public <T> T getOption(Class<T> castType, String key) {
		return CH.getOrThrow(castType, this.options, key);
	}
	@Override
	public <T> T getOption(Class<T> castType, String key, T defaultValue) {
		return CH.getOr(castType, this.options, key, defaultValue);
	}
	@Override
	public <T> T getOption(Caster<T> caster, String key, T defaultValue) {
		return CH.getOr(caster, this.options, key, defaultValue);
	}
	@Override
	public boolean isSupported(byte type) {
		return this.trigger.isSupported(type);
	}

	public void onSchemaChanged(CalcFrameStack sf) {
		try {
			this.trigger.onSchemaChanged(this.db, sf);
			this.supportedInserting = isSupported(AmiTrigger.INSERTING);
			this.supportedInserted = isSupported(AmiTrigger.INSERTED);
			this.supportedUpdating = isSupported(AmiTrigger.UPDATING);
			this.supportedUpdated = isSupported(AmiTrigger.UPDATED);
			this.supportedDeleting = isSupported(AmiTrigger.DELETING);
			this.lastException = null;
			this.lastExceptionTime = 0;
			this.isValid = true;
		} catch (Exception e) {
			setIsEnabled(false, sf);
			LH.warning(log, "Trigger is being disabled, " + this.getTriggerName(), e);
			AmiCenterUtils.getSession(sf).onWarning("TRIGGER IS NO LONGER VALID", null, this.getTriggerName(), "ON_SCHEMA_CHANGE", e.getMessage(), null, e);
			LH.warning(log, "AmiStoredProc '", this.triggerName, "' generated error: ", e);
			this.isValid = false;
			this.lastException = e;
			this.lastExceptionTime = System.currentTimeMillis();
		}
	}

	public boolean onInserting(AmiTableImpl table, AmiRow row, CalcFrameStack sf) {
		if (!supportedInserting || !enabled)
			return true;
		long start = System.nanoTime();
		ReusableCalcFrameStack rsf = push(AmiTrigger.INSERTING, sf);
		try {
			boolean r = this.trigger.onInserting(table, row, rsf);
			onComplete(System.nanoTime() - start, r, AmiTrigger.INSERTING);
			return r;
		} catch (Exception e) {
			onError(System.nanoTime() - start, table, row, AmiTrigger.INSERTING, e, rsf);
		} finally {
			pop(rsf);
		}
		return true;
	}

	public void onInitialized(CalcFrameStack sf) {
		if (!supportedInserted || !enabled)
			return;
		long start = System.nanoTime();
		ReusableCalcFrameStack rsf = push(AmiTrigger.INSERTED, sf);
		try {
			this.trigger.onInitialized(rsf);
		} catch (Exception e) {
			onError(System.nanoTime() - start, null, null, AmiTrigger.INSERTED, e, rsf);
		} finally {
			pop(rsf);
		}
	}
	public void onInserted(AmiTableImpl table, AmiRow row, CalcFrameStack sf) {
		if (!supportedInserted || !enabled)
			return;
		long start = System.nanoTime();
		ReusableCalcFrameStack rsf = push(AmiTrigger.INSERTED, sf);
		try {
			this.trigger.onInserted(table, row, rsf);
			onComplete(System.nanoTime() - start, true, AmiTrigger.INSERTED);
		} catch (Exception e) {
			onError(System.nanoTime() - start, table, row, AmiTrigger.INSERTED, e, rsf);
		} finally {
			pop(rsf);
		}
	}

	public boolean onUpdating(AmiTableImpl table, AmiRow row, AmiPreparedRow newValues, CalcFrameStack sf) {
		if (!supportedUpdating || !enabled)
			return true;
		long start = System.nanoTime();
		ReusableCalcFrameStack rsf = push(AmiTrigger.UPDATING, sf);
		try {
			boolean r = this.trigger.onUpdating(table, row, newValues, rsf);
			onComplete(System.nanoTime() - start, true, AmiTrigger.UPDATING);
			return r;
		} catch (Exception e) {
			onError(System.nanoTime() - start, table, row, AmiTrigger.UPDATING, e, rsf);
		} finally {
			pop(rsf);
		}
		return true;
	}

	public boolean onUpdating(AmiTableImpl table, AmiRow row, CalcFrameStack sf) {
		if (!supportedUpdating || !enabled)
			return true;
		long start = System.nanoTime();
		ReusableCalcFrameStack rsf = push(AmiTrigger.UPDATING, sf);
		try {
			boolean r = this.trigger.onUpdating(table, row, rsf);
			onComplete(System.nanoTime() - start, true, AmiTrigger.UPDATING);
			return r;
		} catch (Exception e) {
			onError(System.nanoTime() - start, table, row, AmiTrigger.UPDATING, e, rsf);
		} finally {
			pop(rsf);
		}
		return true;
	}
	public void onUpdatingRejected(AmiTableImpl amiTableImpl, AmiRow row, AmiPreparedRow newValues, CalcFrameStack sf) {
		if (!supportedUpdating || !enabled)
			return;
		this.trigger.onUpdatingRejected(amiTableImpl, row);
	}

	public void onUpdated(AmiTableImpl table, AmiRow row, CalcFrameStack sf) {
		if (!supportedUpdated || !enabled)
			return;
		long start = System.nanoTime();
		ReusableCalcFrameStack rsf = push(AmiTrigger.UPDATED, sf);
		try {
			this.trigger.onUpdated(table, row, rsf);
			onComplete(System.nanoTime() - start, true, AmiTrigger.UPDATED);
		} catch (Exception e) {
			onError(System.nanoTime() - start, table, row, AmiTrigger.UPDATED, e, rsf);
		} finally {
			pop(rsf);
		}
	}
	public boolean onDeleting(AmiTableImpl table, AmiRow row, CalcFrameStack sf) {
		if (!supportedDeleting || !enabled)
			return true;
		long start = System.nanoTime();
		ReusableCalcFrameStack rsf = push(AmiTrigger.DELETING, sf);
		try {
			boolean r = this.trigger.onDeleting(table, row, sf);
			onComplete(System.nanoTime() - start, r, AmiTrigger.DELETING);
			return r;
		} catch (Exception e) {
			onError(System.nanoTime() - start, table, row, AmiTrigger.DELETING, e, sf);
		} finally {
			pop(rsf);
		}
		return true;
	}

	private void onError(long durration, AmiTableImpl table, AmiRow row, byte type, Exception e, CalcFrameStack sf) {
		statsCount[type]++;
		statsNanos[type] += durration;
		long errors = ++statsError[type];
		this.lastException = e;
		this.lastExceptionTime = System.currentTimeMillis();
		AmiCenterUtils.getSession(sf).onWarning("TRIGGER_ERROR", table, getTriggerName(), AmiTableUtils.toStringForTriggerType(type), e.getMessage(), row, e);
		if (errors < 100)
			LH.warning(log, "Trigger '", triggerName, "' threw exception for row ", row, e);
		else if (errors == 100)
			LH.warning(log, "Trigger '", triggerName, "' THREW TOO MANY EXCEPTIONS, NOT LOGGING ANYMORE");
	}
	private void onComplete(long durration, boolean result, byte type) {
		statsCount[type]++;
		statsNanos[type] += durration;
		if (!result)
			statsFalse[type]++;
	}

	public long getStatsCount(byte type) {
		return this.statsCount[type];
	}
	public long getStatsNanos(byte type) {
		return this.statsNanos[type];
	}
	public long getStatsReturnedFalse(byte type) {
		return this.statsFalse[type];
	}
	public long getStatsErrors(byte type) {
		return this.statsError[type];
	}
	public long getStatsCountTotal() {
		return MH.sum(this.statsCount);
	}
	public long getStatsNanosTotal() {
		return MH.sum(this.statsNanos);
	}
	public long getStatsReturnedFalseTotal() {
		return MH.sum(this.statsFalse);
	}
	public long getStatsErrorsTotal() {
		return MH.sum(this.statsError);
	}

	public void stop(CalcFrameStack sf) {
		try {
			this.trigger.onClosed();
		} catch (Exception e) {
			this.lastException = e;
			this.lastExceptionTime = System.currentTimeMillis();
			AmiCenterUtils.getSession(sf).onWarning("TRIGGER_ERROR", null, getTriggerName(), "ON_CLOSED_UNHANDLED_EXCEPTION", e.getMessage(), null, e);
		}
	}

	public String getTableNames() {
		return this.tableNamesString;
	}

	public void setIsEnabled(boolean enable, CalcFrameStack sf) {
		if (this.enabled == enable)
			return;
		if (enable && !isValid) {
			AmiCenterUtils.getSession(sf).onWarning("TRIGGER_ERROR", null, getTriggerName(), "TRIGGER_NOT_VALID", "From Last Build Attempt: " + lastException.getMessage(), null,
					lastException);
			return;
		}
		this.enabled = enable;
		if (this.db != null) {
			AmiSchema_TRIGGER __TRIGGER = this.db.getSystemSchema().__TRIGGER;
			__TRIGGER.getRowsByTriggerName().get(this.getTriggerName()).setLong(__TRIGGER.enabled, enabled ? 1 : 0, sf);
			this.db.getSystemSchema().writeManagedSchemaFile(sf);
		}
		try {
			this.trigger.onEnabled(enable, sf);
		} catch (Exception e) {
			this.lastException = e;
			this.lastExceptionTime = System.currentTimeMillis();
			AmiCenterUtils.getSession(sf).onWarning("TRIGGER_ERROR", null, getTriggerName(), "ON_ENABLED_UNHANDLED_EXCEPTION", e.getMessage(), null, e);
		}
	}

	public boolean getIsEnabled() {
		return this.enabled;
	}

	public void rename(String newName) {
		this.triggerName = newName;
	}

	public Exception getLastException() {
		return this.lastException;
	}
	public long getLastExceptionTime() {
		return this.lastExceptionTime;
	}

	public void clearStats() {
		LH.info(log, "Resetting trigger statistics for ", this.triggerName, " from counts=", joinStats(statsCount), ", errors=", joinStats(statsError), ", falses=",
				joinStats(this.statsFalse), ", nanos=", joinStats(this.statsNanos), ", lastExceptionTime=", this.lastExceptionTime);
		AH.fill(this.statsCount, 0);
		AH.fill(this.statsError, 0);
		AH.fill(this.statsFalse, 0);
		AH.fill(this.statsNanos, 0);
		this.lastExceptionTime = 0;
		this.lastException = null;
	}
	private String joinStats(long[] stats) {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		if (this.supportedInserting)
			sb.append("inserting=").append(stats[AmiTrigger.INSERTING]).append(',');
		if (this.supportedInserted)
			sb.append("inserted=").append(stats[AmiTrigger.INSERTED]).append(',');
		if (this.supportedUpdating)
			sb.append("updating=").append(stats[AmiTrigger.UPDATING]).append(',');
		if (this.supportedUpdated)
			sb.append("updated=").append(stats[AmiTrigger.UPDATED]).append(',');
		if (this.supportedDeleting)
			sb.append("deleting=").append(stats[AmiTrigger.DELETING]).append(',');
		if (SH.endsWith(sb, ','))
			sb.setLength(sb.length() - 1);
		sb.append(']');
		return sb.toString();
	}
	private void pop(ReusableCalcFrameStack sf) {
		pool.release(sf);
	}

	private ReusableCalcFrameStack push(byte inserting, CalcFrameStack sf) {
		return pool.borrow(sf, EmptyCalcFrame.INSTANCE);
	}
}
