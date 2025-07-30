package com.f1.ami.center.replication;

import com.f1.ami.center.AmiCenterState;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.utils.LH;

public class AmiCenterReplicationConnectedProcessor extends BasicProcessor<AmiCenterReplicationConnectedMessage, AmiCenterState> {

	public AmiCenterReplicationConnectedProcessor() {
		super(AmiCenterReplicationConnectedMessage.class, AmiCenterState.class);
	}

	@Override
	public void processAction(AmiCenterReplicationConnectedMessage action, AmiCenterState state, ThreadScope threadScope) throws Exception {
		final AmiImdbImpl db = state.getAmiImdb();
		final AmiCenterReplicator replicator = db.getReplicator();
		AmiCenterReplicationCenter replications = replicator.getReplicationSource(action.getCenterId());
		for (AmiCenterReplication replication : replications.getReplications())
			if (replication.getOnDeleteMode() == AmiCenterReplication.DELETE_ON_CONNECT) {
				LH.info(log, "Clearing rows for " + replication.getName() + "::" + replication.getTargetTable() + " on connect");
				replication.clear(db, state.getReusableTopStackFrame());
			}
	}

}
