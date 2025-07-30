package com.f1.ami.center;

import java.util.concurrent.TimeUnit;

import com.f1.ami.amicommon.AmiCommonProperties;
import com.f1.ami.amicommon.msg.AmiCenterChanges;
import com.f1.ami.amicommon.msg.AmiCenterRequest;
import com.f1.ami.amicommon.msg.AmiCenterResponse;
import com.f1.ami.center.table.AmiCenterProcess;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiImdbSession;
import com.f1.container.OutputPort;
import com.f1.container.RequestOutputPort;
import com.f1.container.ThreadScope;
import com.f1.povo.standard.CountMessage;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiCenterMonitorProcessor extends AmiCenterBasicProcessor<CountMessage> {

	private static final int LOG_STATS_PERIOD_MS = 15000;
	private static final int PUBLISH_CHANGES_PERIOD_MS = 500;
	public final OutputPort<CountMessage> loopback = newOutputPort(CountMessage.class);
	public final RequestOutputPort<AmiCenterRequest, AmiCenterResponse> toEyePort = newRequestOutputPort(AmiCenterRequest.class, AmiCenterResponse.class);
	private int minPeriodMs;
	private int monitorPeriodMs;
	private int monitorResourcesPeriodMs;
	private int logPeriodMs;
	private int statsPeriodMs;
	private int statsRetentionMs;
	private AmiCenterLoggerHelper statsLogger = new AmiCenterLoggerHelper();

	public AmiCenterMonitorProcessor() {
		super(CountMessage.class);
	}

	@Override
	public void init() {
		this.monitorPeriodMs = getPeriod(AmiCenterProperties.PROPERTY_AMI_CENTER_PUBLISH_CHANGES_PERIOD_MS, PUBLISH_CHANGES_PERIOD_MS);
		this.monitorResourcesPeriodMs = getPeriod(AmiCenterProperties.PROPERTY_AMI_RESOURCES_MONITOR_PERIOD_MS, AmiCenterResourcesManager.DEFAULT_PERIOD);
		this.logPeriodMs = getPeriod(AmiCenterProperties.PROPERTY_AMI_CENTER_LOG_STATS_PERIOD_MS, LOG_STATS_PERIOD_MS);
		this.statsPeriodMs = getPeriod(AmiCommonProperties.PROPERTY_AMI_STATS_TABLE_PERIOD_MS, AmiCommonProperties.DEFAULT_STATS_PERIOD_MS);
		this.statsRetentionMs = getPeriod(AmiCommonProperties.PROPERTY_AMI_STATS_TABLE_RETENTION_MS, AmiCommonProperties.DEFAULT_STATS_RETENTION_MS);
		this.minPeriodMs = MH.min(this.monitorPeriodMs, this.monitorResourcesPeriodMs, this.logPeriodMs);
		LH.info(log, "Min Period for checking: " + this.minPeriodMs);
		super.init();
	}
	private int getPeriod(String key, int dflt) {
		int r = this.getTools().getOptional(key, dflt);
		if (r < 1)
			throw new RuntimeException("Invalid value for " + key + ":  Must be positive number, not " + r);
		return r;
	}

	private long lastLoggedTime = 0L;
	private long lastStatsTime = 0L;
	private long lastMonitoredTime = 0L;
	private long lastResourceCheckTime = 0L;

	@Override
	public void processAction(CountMessage action, AmiCenterState state, ThreadScope threadScope) throws Exception {
		final long now = getTools().getNow();

		final long durationToMonitoredTime = lastMonitoredTime + monitorPeriodMs - now;
		final long durationToMonitorResourcesTime = lastResourceCheckTime + monitorResourcesPeriodMs - now;
		final long durationToLogTime = lastLoggedTime + logPeriodMs - now;
		final long durationToStatsTime = lastStatsTime + statsPeriodMs - now;

		{
			long periodMs = minPeriodMs;
			if (durationToMonitoredTime > 0 && durationToMonitoredTime < periodMs)
				periodMs = durationToMonitoredTime;
			if (durationToMonitorResourcesTime > 0 && durationToMonitorResourcesTime < periodMs)
				periodMs = durationToMonitorResourcesTime;
			if (durationToLogTime > 0 && durationToLogTime < periodMs)
				periodMs = durationToLogTime;
			loopback.sendDelayed(action, threadScope, periodMs, TimeUnit.MILLISECONDS);
		}

		final AmiImdbImpl amiImdb = state.getAmiImdb();
		AmiImdbSession globalSession = amiImdb.getGlobalSession();
		AmiCenterGlobalProcess proc = state.getAmiImdb().getGlobalProcess();
		proc.setProcessStatus(AmiCenterProcess.PROCESS_RUN_MONITOR);
		try {
			globalSession.lock(proc, null);
			CalcFrameStack sf = globalSession.getReusableTopStackFrame();
			if (durationToMonitoredTime <= 0) {
				lastMonitoredTime = now;
				AmiCenterChangesMessageBuilder msgBuilder = state.getChangesMessageBuilderNoReset();
				for (AmiCenterConnection connection : state.getAmiConnections())
					if (connection.needsStatsUpdate())
						connection.applyCounts(sf);

				amiImdb.onMonitor(now, sf);
				amiImdb.getSessionManager().purgeStaleSessions(now);

				if (msgBuilder.hasChanges()) {
					AmiCenterChanges toClient = msgBuilder.popToChangesMsg(state.nextSequenceNumber());
					sendToClients(toClient);
				}
			}
			if (durationToLogTime <= 0) {
				lastLoggedTime = now;
				try {
					statsLogger.log(state);
				} catch (Exception e) {
					LH.severe(log, "Error logging stats: ", e);
				}
			}
			if (durationToStatsTime <= 0) {
				lastStatsTime = now;
				state.getAmiImdb().getSystemSchema().__STATS.addRow(this.statsRetentionMs, sf);
			}

			if (durationToMonitorResourcesTime <= 0) {
				lastResourceCheckTime = now;
				state.syncResourcesToTable(globalSession);
			}
		} finally {
			proc.setProcessStatus(AmiCenterProcess.PROCESS_IDLE);
			globalSession.unlock();
		}
	}
}
