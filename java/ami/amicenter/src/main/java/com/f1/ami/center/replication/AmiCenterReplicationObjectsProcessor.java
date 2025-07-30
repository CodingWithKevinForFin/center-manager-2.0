package com.f1.ami.center.replication;

import com.f1.ami.amicommon.centerclient.AmiCenterClientObjectMessageImpl;
import com.f1.ami.center.AmiCenterGlobalProcess;
import com.f1.ami.center.AmiCenterState;
import com.f1.ami.center.table.AmiCenterProcess;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiImdbSession;
import com.f1.ami.center.table.AmiTableImpl;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiCenterReplicationObjectsProcessor extends BasicProcessor<AmiCenterReplicationObjectsMessage, AmiCenterState> {

	public AmiCenterReplicationObjectsProcessor() {
		super(AmiCenterReplicationObjectsMessage.class, AmiCenterState.class);
	}

	@Override
	public void processAction(AmiCenterReplicationObjectsMessage action, AmiCenterState state, ThreadScope threadScope) throws Exception {
		AmiImdbSession session = state.getRtFeedSession();
		AmiCenterGlobalProcess proc = state.getAmiImdb().getGlobalProcess();
		proc.setProcessStatus(AmiCenterProcess.PROCESS_RUN_REPLICATION);
		try {
			session.lock(proc, null);
			final AmiImdbImpl db = state.getAmiImdb();
			AmiCenterReplicator replicator = db.getReplicator();
			AmiCenterReplication replication = null;
			String lastName = null;
			AmiTableImpl table = null;
			CalcFrameStack sf = session.getReusableTopStackFrame();
			AmiCenterReplicationCenter replications = replicator.getReplicationSource(action.getCenterId());
			if (action.getSchemaHead() != null) {
				for (AmiCenterClientObjectMessageImpl msg = action.getSchemaHead(); msg != null; msg = msg.getNext()) {
					replications.onSchema(msg);
				}
				replications.onSchemaChanged(sf);
			}
			for (AmiCenterClientObjectMessageImpl msg = action.getHead(); msg != null; msg = msg.getNext()) {
				try {
					String name = msg.getTypeName();
					if (OH.ne(lastName, name)) {
						lastName = name;
						replication = replications.getReplication(name);
						if (replication == null)
							continue;
						String targetName = replication.getTargetTable();
						table = db.getAmiTable(targetName);
						if (table == null)
							continue;
					}
					if (replication != null)
						replication.process(msg, session, sf);
				} catch (Exception e) {
					LH.warning(log, "Error replicating message: " + msg, e);
				}
			}
		} finally {
			proc.setProcessStatus(AmiCenterProcess.PROCESS_IDLE);
			session.unlock();
		}

	}

}
