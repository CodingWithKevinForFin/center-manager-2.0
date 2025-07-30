package com.f1.ami.center.timers;

import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.center.AmiCenterGlobalProcess;
import com.f1.ami.center.sysschema.AmiSchema_TIMER;
import com.f1.ami.center.table.AmiCenterProcess;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiImdbSession;
import com.f1.ami.center.table.AmiTableUtils;
import com.f1.ami.center.triggers.AmiTimedRunnable;
import com.f1.base.Caster;
import com.f1.base.DateMillis;
import com.f1.utils.CH;
import com.f1.utils.CronTab;
import com.f1.utils.EH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.Timer;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;

public class AmiTimerBindingImpl implements AmiTimedRunnable, AmiTimerBinding {

	private static final Logger log = LH.get();
	private String timerName;
	final private AmiTimer timer;
	final private int priority;
	final private String schedule;
	final private byte defType;
	final private String timerType;
	final private Map<String, String> optionsStrings;
	final private Timer cronTab;
	private long nextRunTime;
	private AmiImdbImpl db;
	private boolean running;
	private long lastRunTime;
	private Map<String, Object> options;
	final private long durration;
	private boolean enabled = true;
	private long lastStart = -1;
	private AmiImdbSession timerSession;
	private Integer correlationId = 1;
	private int timeoutMillis;
	private int limit;

	public AmiTimerBindingImpl(String timerName, AmiTimer timer, int priority, String schedule, String timerType, Map<String, Object> options, Map<String, String> optionsStrings,
			byte definedBy) {
		this.timerName = timerName;
		this.timer = timer;
		this.priority = priority;
		this.schedule = schedule;
		this.timerType = timerType;
		this.defType = definedBy;
		this.optionsStrings = optionsStrings;
		this.lastRunTime = -1L;
		this.nextRunTime = -1L;
		this.options = options;

		if (SH.isnt(schedule)) {
			this.cronTab = null;
			this.durration = -1;
		} else {

			if (SH.areBetween(schedule, '0', '9')) {
				try {
					this.durration = SH.parseLong(schedule);
				} catch (Exception e) {
					throw new IllegalArgumentException("Expression must be either a millisecond durration or include 7 fields(second minute hour day month weekday timezone)", e);
				}
				if (this.durration < 1)
					throw new IllegalArgumentException("durration must be at least 1 millisecond");
				this.cronTab = null;
			} else {
				this.durration = -1;
				int t = SH.lastIndexOf(schedule, ' ');
				if (t == -1)
					throw new IllegalArgumentException("Expression must end with valid timezone");
				final String tzs = schedule.substring(t + 1);
				final TimeZone tz = EH.getTimeZoneNoThrow(tzs);
				if (tz == null)
					throw new IllegalArgumentException("Expression must end with valid timezone");
				this.cronTab = CronTab.parse(schedule.substring(0, t), tz);
			}
		}
	}
	@Override
	public AmiTimer getTimer() {
		return this.timer;
	}

	@Override
	public int getPriority() {
		return this.priority;
	}

	@Override
	public String getSchedule() {
		return this.schedule;
	}

	@Override
	public String getTimerName() {
		return timerName;
	}

	public void startup(AmiImdbImpl amiImdb, CalcFrameStack sf) {
		try {
			this.db = amiImdb;
			this.running = true;
			if (enabled) {
				if (this.cronTab != null)
					this.nextRunTime = this.cronTab.calculateNextOccurance(amiImdb.getNow());
				else if (this.durration > 0)
					this.nextRunTime = amiImdb.getNow() + this.durration;
			}
			this.timeoutMillis = getOption(Integer.class, "timeout", db.getDefaultQueryTimeoutMs());
			this.limit = getOption(Integer.class, "limit", db.getDefaultQueryLimit());
			this.timerSession = db.getSessionManager().newSession(AmiTableUtils.DEFTYPE_USER, AmiCenterQueryDsRequest.ORIGIN_SYSTEM, "__SCHEDULER", "TIMER " + this.getTimerName(),
					AmiImdbSession.PERMISSIONS_FULL, this.getTimeoutMillis(), this.getLimit(), EmptyCalcFrame.INSTANCE);
			this.stackFrame = this.timerSession.getReusableTopStackFrame();
			this.timer.startup(amiImdb, this, stackFrame);
			this.db.getSystemSchema().__TIMER.updateRowTimes(this.getTimerName(), lastRunTime, nextRunTime, sf);
			if (enabled) {
				registerTimer();
			}
		} catch (RuntimeException e) {
			stop();
			throw e;
		}
	}

	@Override
	public int getTimeoutMillis() {
		return this.timeoutMillis;
	}
	@Override
	public int getLimit() {
		return this.limit;
	}

	private boolean isRegistered = false;

	private void registerTimer() {
		if (isRegistered)
			this.correlationId++;//this will void out existing timers
		else
			isRegistered = true;
		this.db.registerTimer(this, nextRunTime, this.priority, correlationId);
	}

	@Override
	public String getTimerType() {
		return this.timerType;
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
	public void onTimer(long timerId, Object correlationId) {
		if (correlationId != this.correlationId)
			return;
		this.isRegistered = false;
		if (!running || !enabled)
			return;
		this.lastRunTime = this.nextRunTime;
		this.lastStart = System.nanoTime();
		//		if (!this.timerSession.pushStack(AmiTrigger.TIMER, this.timerName))
		//			return;

		AmiCenterGlobalProcess globalProcess = this.db.getGlobalProcess();
		try {
			this.timerSession.touch(System.currentTimeMillis());
			globalProcess.setProcessStatus(AmiCenterProcess.PROCESS_RUN);
			boolean completed = this.timer.onTimer(this.lastRunTime, globalProcess, this.stackFrame);
			if (!completed)
				return;
			statsNanos += System.nanoTime() - lastStart;
			statsCount++;
		} catch (Exception e) {
			statsNanos += System.nanoTime() - lastStart;
			statsCount++;
			long errors = ++statsError;
			this.lastException = e;
			this.lastExceptionTime = System.currentTimeMillis();
			if (errors < 100)
				LH.warning(log, "Timer '", timerName, "' threw exception", e);
			else if (errors == 100)
				LH.warning(log, "Timer '", timerName, "' THREW TOO MANY EXCEPTIONS, NOT LOGGING ANYMORE");
		} finally {
			//			this.timerSession.popStack(AmiTrigger.TIMER, this.timerName);
			globalProcess.setProcessStatus(AmiCenterProcess.PROCESS_IDLE);
		}
		lastStart = -1L;
		if (this.cronTab != null) {
			long next = this.cronTab.calculateNextOccurance(this.nextRunTime + 1);
			if (next < db.getNow()) {
				long next2 = this.cronTab.calculateNextOccurance(db.getNow());
				LH.warning(log, "Timer '", timerName, "' crontab scheduled past time so skipping from ", DateMillis.format(next), " to " + DateMillis.format(next2));
				next = next2;
			}
			setNextRunTime(next);
		} else if (this.durration > 0)
			setNextRunTime(db.getNow() + this.durration);
	}
	public void stop() {
		this.running = false;
		this.timerSession.close();
	}
	@Override
	public long getNextRunTime() {
		return this.nextRunTime;
	}
	@Override
	public long getLastRunTime() {
		return this.lastRunTime;
	}

	public boolean setNextRunTime(long millis) {
		if (millis <= 0) {
			if (millis == 0)
				millis = 1L;
			else
				return false;
		}
		this.nextRunTime = millis;
		updateTimerTable();
		registerTimer();
		return true;
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

	public void onSchemaChanged(CalcFrameStack sf) {
		try {
			this.timer.onSchemaChanged(this.db, sf);
		} catch (Exception e) {
			LH.warning(log, "AmiStoredProc '", this.timerName, "' generated error: ", e);
		}
	}

	private long statsCount = 0;
	private long statsNanos = 0;
	private long statsError = 0;
	private Exception lastException;
	private long lastExceptionTime = -1;
	private CalcFrameStack stackFrame;

	public long getStatsCount() {
		return this.statsCount;
	}
	public long getStatsNanos() {
		return this.statsNanos;
	}
	public long getStatsErrors() {
		return this.statsError;
	}
	public void setIsEnabled(boolean enable, CalcFrameStack sf) {
		if (this.enabled == enable)
			return;
		this.enabled = enable;
		if (this.db != null) {//have we started up yet?
			if (!enabled) {
				this.nextRunTime = -1L;
				this.db.getSystemSchema().__TIMER.updateRowTimes(this.getTimerName(), this.lastRunTime, -1L, sf);
			} else {
				if (this.cronTab != null)
					setNextRunTime(this.cronTab.calculateNextOccurance(this.db.getNow()));
				else if (this.durration > 0)
					setNextRunTime(db.getNow() + this.durration);
			}
			AmiSchema_TIMER __TIMER = this.db.getSystemSchema().__TIMER;
			__TIMER.getRowsByName().get(this.getTimerName()).setLong(__TIMER.enabled, enabled ? 1 : 0, sf);
			this.db.getSystemSchema().writeManagedSchemaFile(sf);
		}
	}
	@Override
	public boolean getIsEnabled() {
		return this.enabled;
	}
	public void rename(String newName) {
		this.timerName = newName;
	}
	@Override
	public void onTimerCompleted() {
		OH.assertNe(lastStart, -1L);
		statsNanos += System.nanoTime() - lastStart;
		statsCount++;
		lastStart = -1L;
		if (this.cronTab != null) {
			long next = this.cronTab.calculateNextOccurance(this.nextRunTime + 1);
			if (next < db.getNow()) {
				long next2 = this.cronTab.calculateNextOccurance(db.getNow());
				LH.warning(log, "Timer '", timerName, "' crontab scheduled past time so skipping from ", DateMillis.format(next), " to " + DateMillis.format(next2));
				next = next2;
			}
			setNextRunTime(next);
		} else if (this.durration > 0)
			setNextRunTime(db.getNow() + this.durration);
	}
	@Override
	public void onTimerCompletedWithError(Exception e) {
		OH.assertNe(lastStart, -1L);
		statsNanos += System.nanoTime() - lastStart;
		statsCount++;
		lastStart = -1L;
		long errors = ++statsError;
		this.lastException = e;
		this.lastExceptionTime = System.currentTimeMillis();
		if (errors < 100)
			LH.warning(log, "Timer '", timerName, "' threw exception", e);
		else if (errors == 100)
			LH.warning(log, "Timer '", timerName, "' THREW TOO MANY EXCEPTIONS, NOT LOGGING ANYMORE");
		if (this.cronTab != null) {
			long next = this.cronTab.calculateNextOccurance(this.nextRunTime + 1);
			if (next < db.getNow()) {
				long next2 = this.cronTab.calculateNextOccurance(db.getNow());
				LH.warning(log, "Timer '", timerName, "' crontab scheduled past time so skipping from ", DateMillis.format(next), " to " + DateMillis.format(next2));
				next = next2;
			}
			setNextRunTime(next);
		} else if (this.durration > 0)
			setNextRunTime(db.getNow() + this.durration);
	}

	public boolean getIsRunning() {
		return this.lastStart != -1;
	}
	public void clearCount(boolean executed, boolean errors) {
		if (executed) {
			LH.info(log, "Resetting timer statistics for ", this.timerName, " from count=", this.statsCount, ", nanos=", this.statsNanos);
			this.statsCount = this.statsNanos = 0;
		}
		if (errors) {
			LH.info(log, "Resetting timer statistics for ", this.timerName, " from errorCount=", this.statsError, ", lastExceptionTime=", this.lastExceptionTime);
			this.statsError = 0;
			this.lastException = null;
			this.lastExceptionTime = 0;
		}
		updateTimerTable();
	}
	private void updateTimerTable() {
		this.db.getSystemSchema().__TIMER.updateRowTimes(this.getTimerName(), this.lastRunTime, nextRunTime, this.stackFrame);
	}

	public Exception getLastException() {
		return this.lastException;
	}
	public long getLastExceptionTime() {
		return this.lastExceptionTime;
	}

}
