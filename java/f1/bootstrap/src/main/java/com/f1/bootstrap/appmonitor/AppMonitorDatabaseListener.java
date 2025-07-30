package com.f1.bootstrap.appmonitor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.f1.base.ObjectGeneratorForClass;
import com.f1.povo.f1app.F1AppDatabase;
import com.f1.povo.f1app.audit.F1AppAuditTrailEvent;
import com.f1.povo.f1app.audit.F1AppAuditTrailRule;
import com.f1.povo.f1app.audit.F1AppAuditTrailSqlEvent;
import com.f1.utils.AH;
import com.f1.utils.EH;
import com.f1.utils.OH;
import com.f1.utils.db.Database;
import com.f1.utils.db.DatabaseConnection;
import com.f1.utils.db.DatabaseListener;

public class AppMonitorDatabaseListener extends AbstractAppMonitorObjectListener<F1AppDatabase, Database> implements DatabaseListener {

	final private AtomicLong count = new AtomicLong();
	final private String name;
	private ObjectGeneratorForClass<F1AppAuditTrailSqlEvent> generator;

	public AppMonitorDatabaseListener(AppMonitorState state, String dbid, Database database) {
		super(state, database);
		this.name = dbid;
		this.generator = state.getPartition().getContainer().getGenerator(F1AppAuditTrailSqlEvent.class);
	}
	@Override
	public void onQuery(String sql, Object params[], DatabaseConnection connection) {
		AppMonitorAuditRule[] rules = getAuditRuleIdsOrNull();
		if (rules != null) {
			int matchCount = 0;
			boolean found[] = null;
			for (int i = 0; i < rules.length; i++) {
				AppMonitorSqlRule sqlRule = (AppMonitorSqlRule) rules[i];
				if (sqlRule.getStatementMask().matches(sql)) {
					if (found == null)
						found = new boolean[rules.length];
					found[i] = true;
					matchCount++;
				}
			}
			if (found != null) {
				F1AppAuditTrailSqlEvent event = generator.nw();
				long[] ruleIds = new long[matchCount];
				for (int i = 0, j = 0; i < rules.length; i++) {
					if (found[i])
						ruleIds[j++] = rules[i].getId();
				}
				event.setType(F1AppAuditTrailRule.EVENT_TYPE_SQL);
				event.setAgentRuleIds(ruleIds);
				event.setTimeMs(EH.currentTimeMillis());
				event.setAuditSequenceNumber(getState().nextAuditSequenceNumber());

				if (AH.isntEmpty(params)) {
					List<String> paramsString = new ArrayList<String>(params.length);
					for (Object o : params)
						paramsString.add(OH.toString(o));
					event.setParams(paramsString);
				}
				event.setPayloadAsString(sql);
				event.setPayloadFormat(F1AppAuditTrailEvent.FORMAT_STRING_TEXT);
				event.setAgentF1ObjectId(getAgentObject().getId());
				onAuditEvent(event);

			}
		}
		count.incrementAndGet();
		flagChanged();
	}

	public long getMessagesCount() {
		return count.get();
	}

	@Override
	public Class<F1AppDatabase> getAgentType() {
		return F1AppDatabase.class;
	}

	@Override
	protected void populate(Database source, F1AppDatabase sink) {
		int count = 0;
		int active = 0;
		for (DatabaseConnection i : source.getDatabaseConnections()) {
			if (!i.getIsInPool())
				active++;
			count++;
		}
		sink.setActiveConnectionsCount(active);
		sink.setConnectionsCount(count);
		sink.setUrl(source.getUrl());
		sink.setName(name);
		sink.setSqlSentCount(getMessagesCount());
	}

	@Override
	public byte getListenerType() {
		return TYPE_DATABASE;
	}
}
