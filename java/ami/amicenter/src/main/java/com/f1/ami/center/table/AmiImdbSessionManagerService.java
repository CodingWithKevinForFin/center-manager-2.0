package com.f1.ami.center.table;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import com.f1.ami.center.AmiCenterProperties;
import com.f1.ami.center.AmiCenterState;
import com.f1.ami.center.dbo.AmiDbo;
import com.f1.base.CalcFrame;
import com.f1.container.ContainerTools;
import com.f1.utils.LH;
import com.f1.utils.LongArrayList;
import com.f1.utils.SH;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.LongKeyMap.Node;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.MutableCalcFrame;

public class AmiImdbSessionManagerService {

	final private LongKeyMap<AmiImdbSession> sessions = new LongKeyMap<AmiImdbSession>();
	private AmiImdbImpl db;
	private ContainerTools tools;
	private AtomicLong nextId = new AtomicLong(1);
	private long timeoutMs;
	private long lastCheckedForTimedoutSessions = 0;
	private long checkPeriodMs;
	private AmiCenterState state;
	private static final Logger log = LH.get();

	public AmiImdbSessionManagerService(ContainerTools tools) {
		this.tools = tools;
	}
	public void init(AmiCenterState state) {
		if (this.state != null)
			throw new IllegalStateException("already init");
		this.state = state;

		this.timeoutMs = SH.parseDurationTo(this.tools.getOptional(AmiCenterProperties.PROPERTY_AMI_CENTER_DB_SESSION_TIMEOUT, "24 HOURS"), TimeUnit.MILLISECONDS);
		this.checkPeriodMs = this.tools.getOptional(AmiCenterProperties.PROPERTY_AMI_CENTER_DB_SESSION_CHECK_PERIOD_SECONDS, 60L) * 1000;
	}

	public AmiImdbSession getSession(long sessionId) {
		if (sessionId < 0)
			throw new IllegalStateException("bad session id: " + sessionId);
		AmiImdbSession r;
		long now = this.tools.getNow();
		long cutoff = now - timeoutMs;
		synchronized (sessions) {
			r = sessions.get(sessionId);
			if (r != null && r.getLastUsedTime() < cutoff) {
				sessions.remove(r.getSessionId());
				r = null;
			}
		}
		if (r == null)
			LH.info(log, "IMDB Session has timedout: ", sessionId);
		else
			r.touch(now);
		return r;
	}

	public AmiImdbSession removeSession(long sessionId) {
		if (sessionId < 0)
			throw new IllegalStateException("bad session id: " + sessionId);
		AmiImdbSession r;
		synchronized (sessions) {
			r = this.sessions.remove(sessionId);
		}
		return r;
	}
	public AmiImdbSession newTempSession(byte definedBy, byte originType, String username, String description, byte permissions, int defaultTimeoutMillis, int defaultLimit,
			CalcFrame vars) {
		//TODO: create a new script manager?
		AmiImdbSession r = new AmiImdbSession(nextId.getAndIncrement(), true, this.state, definedBy, originType, username, description, permissions, defaultTimeoutMillis,
				defaultLimit, vars, this.state.getScriptManager().getTimezone());
		r.touch(this.tools.getNow());
		synchronized (sessions) {
			this.sessions.put(r.getSessionId(), r);
		}
		return r;
	}
	public AmiImdbSession newSession(byte definedBy, byte originType, String username, String description, byte permissions, int defaultTimeoutMillis, int defaultLimit,
			CalcFrame variables) {
		//TODO: create a new script manager?
		AmiImdbSession r = new AmiImdbSession(nextId.getAndIncrement(), false, this.state, definedBy, originType, username, description, permissions, defaultTimeoutMillis,
				defaultLimit, variables, this.state.getScriptManager().getTimezone());
		r.touch(this.tools.getNow());
		synchronized (sessions) {
			this.sessions.put(r.getSessionId(), r);
		}
		return r;
	}

	public void purgeStaleSessions(long now) {

		if (lastCheckedForTimedoutSessions + this.checkPeriodMs > now)
			return;
		lastCheckedForTimedoutSessions = now;
		if (!sessions.isEmpty()) {
			final long cutoff = now - timeoutMs;
			LongArrayList toRemove = null;
			synchronized (sessions) {
				for (Node<AmiImdbSession> e : this.sessions) {
					if (e.getValue().getLastUsedTime() < cutoff) {
						if (toRemove == null)
							toRemove = new LongArrayList();
						toRemove.add(e.getLongKey());
					}
				}
				if (toRemove != null)
					for (int i = 0; i < toRemove.size(); i++)
						this.sessions.remove(toRemove.get(i));
			}
			if (toRemove != null)
				LH.info(log, "IMDB Sessions have timedout: ", toRemove);
		}
	}

	public AmiImdbSession[] getSessions() {
		synchronized (sessions) {
			return this.sessions.getValues(new AmiImdbSession[this.sessions.size()]);
		}
	}
	public void putConst(CalcFrameStack fs, String name, Class<?> type, AmiDbo value) {
		if (sessions != null)
			synchronized (sessions) {
				for (AmiImdbSession i : this.getSessions()) {
					if (fs.getTableset() != i)
						i.lock(null, null);
					try {
						MutableCalcFrame mcf = i.getConsts();
						mcf.putTypeValue(name, type, value);
					} finally {
						if (fs.getTableset() != i)
							i.unlock();
					}
				}
			}

	}
	public void removeConst(CalcFrameStack fs, String name) {
		if (sessions != null)
			synchronized (sessions) {
				for (AmiImdbSession i : this.getSessions()) {
					if (fs.getTableset() != i)
						i.lock(null, null);
					try {
						MutableCalcFrame mcf = i.getConsts();
						mcf.removeTypeValue(name);
					} finally {
						if (fs.getTableset() != i)
							i.unlock();
					}
				}
			}
	}
}
