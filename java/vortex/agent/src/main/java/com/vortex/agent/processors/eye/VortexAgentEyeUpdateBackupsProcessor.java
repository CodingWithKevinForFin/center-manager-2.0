package com.vortex.agent.processors.eye;

import java.util.ArrayList;
import java.util.List;

import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.structs.LongSet;
import com.f1.vortexcommon.msg.agent.VortexAgentBackupFile;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentUpdateBackupRequest;
import com.f1.vortexcommon.msg.eye.VortexEyeBackup;
import com.vortex.agent.messages.VortexAgentBackupUpdateMessage;
import com.vortex.agent.processors.VortexAgentBasicProcessor;
import com.vortex.agent.state.VortexAgentState;

public class VortexAgentEyeUpdateBackupsProcessor extends VortexAgentBasicProcessor<VortexAgentUpdateBackupRequest> {

	public final OutputPort<VortexAgentBackupUpdateMessage> toBackups = newOutputPort(VortexAgentBackupUpdateMessage.class);
	public VortexAgentEyeUpdateBackupsProcessor() {
		super(VortexAgentUpdateBackupRequest.class);
	}

	@Override
	public void processAction(VortexAgentUpdateBackupRequest action, VortexAgentState state, ThreadScope threadScope) throws Exception {
		LongSet toRemoveDueToSnapshot = new LongSet();
		if (action.getIsSnapshot())
			for (VortexEyeBackup i : state.getBackups())
				toRemoveDueToSnapshot.add(i.getId());
		if (CH.isntEmpty(action.getUpdated())) {
			for (VortexEyeBackup backup : action.getUpdated()) {
				backup.lock();
				toRemoveDueToSnapshot.remove(backup.getId());
				state.addBackup(backup);
				String partitionId = "BACKUP_" + backup.getId();
				VortexAgentBackupUpdateMessage updateMsg = nw(VortexAgentBackupUpdateMessage.class);
				updateMsg.setPartitionId(partitionId);
				updateMsg.setBackup(backup);
				List<VortexAgentBackupFile> files = new ArrayList<VortexAgentBackupFile>();
				for (VortexAgentBackupFile file : CH.i(action.getFiles()))
					if (file.getBackupId() == backup.getId())
						files.add(file);
				updateMsg.setFiles(files);
				toBackups.send(updateMsg, threadScope);
			}
		}

		for (long id : AH.i(action.getRemoved()))
			toRemoveDueToSnapshot.add(id);

		for (long id : toRemoveDueToSnapshot.toLongArray()) {
			if (state.removeBackup(id) != null) {
				String partitionId = "BACKUP_" + id;
				VortexAgentBackupUpdateMessage updateMsg = nw(VortexAgentBackupUpdateMessage.class);
				updateMsg.setPartitionId(partitionId);
				VortexEyeBackup backup = nw(VortexEyeBackup.class);
				backup.setRevision(VortexAgentEntity.REVISION_DONE);
				backup.setId(id);
				updateMsg.setBackup(backup);
				toBackups.send(updateMsg, threadScope);
			}
		}
	}

}
