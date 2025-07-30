package com.f1.ami.center.replication;

import com.f1.ami.center.AmiCenterState;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.utils.LH;

public class AmiCenterReplicationDisconnectedProcessor extends BasicProcessor<AmiCenterReplicationDisconnectedMessage, AmiCenterState> {

	public AmiCenterReplicationDisconnectedProcessor() {
		super(AmiCenterReplicationDisconnectedMessage.class, AmiCenterState.class);
	}

	@Override
	public void processAction(AmiCenterReplicationDisconnectedMessage action, AmiCenterState state, ThreadScope threadScope) throws Exception {
		final AmiImdbImpl db = state.getAmiImdb();
		final AmiCenterReplicator replicator = db.getReplicator();
		AmiCenterReplicationCenter replications = replicator.getReplicationSource(action.getCenterId());
		if (replications != null) {
			replications.onDisconnect();
			for (AmiCenterReplication replication : replications.getReplications())
				if (replication.getOnDeleteMode() == AmiCenterReplication.DELETE_ON_DISCONNECT) {
					LH.info(log, "Clearing rows for " + replication.getName() + "::" + replication.getTargetTable() + " on disconnect");
					replication.clear(db, state.getReusableTopStackFrame());
				}
		}
	}

}
