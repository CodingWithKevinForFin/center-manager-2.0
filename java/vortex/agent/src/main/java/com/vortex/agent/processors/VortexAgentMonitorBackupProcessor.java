package com.vortex.agent.processors;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.f1.container.OutputPort;
import com.f1.container.PartitionResolver;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.vortexcommon.msg.agent.VortexAgentBackupFile;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexUpdateBackupStatusesFromAgent;
import com.vortex.agent.VortexAgentUtils;
import com.vortex.agent.messages.VortexAgentBackupUpdateMessage;
import com.vortex.agent.state.VortexAgentBackupState;

public class VortexAgentMonitorBackupProcessor extends BasicProcessor<VortexAgentBackupUpdateMessage, VortexAgentBackupState> implements
		PartitionResolver<VortexAgentBackupUpdateMessage> {

	final public OutputPort<VortexAgentBackupUpdateMessage> loopback = newOutputPort(VortexAgentBackupUpdateMessage.class);
	final public OutputPort<VortexUpdateBackupStatusesFromAgent> toEye = newOutputPort(VortexUpdateBackupStatusesFromAgent.class);
	public VortexAgentMonitorBackupProcessor() {
		super(VortexAgentBackupUpdateMessage.class, VortexAgentBackupState.class);
		setPartitionResolver(this);
	}

	@Override
	public void processAction(VortexAgentBackupUpdateMessage action, VortexAgentBackupState state, ThreadScope threadScope) throws Exception {
		boolean needsPolling = true;
		if (action.getBackup() != null) {//this is an update
			if (action.getBackup().getRevision() == VortexAgentEntity.REVISION_DONE) {
				state.clear();
				return;
			} else {
				state.init(action.getBackup(), action.getFiles());
				if (!state.isPolling()) {
					state.setIsPolling(true);
					action.setBackup(null);
				} else {
					needsPolling = false;
				}
			}
		} else if (state.getBackup() == null) {//was cleared out
			state.setIsPolling(false);
			return;
		}

		final Map<String, VortexAgentBackupFile> addedSink = new HashMap<String, VortexAgentBackupFile>();
		final Map<String, VortexAgentBackupFile> updatedSink = new HashMap<String, VortexAgentBackupFile>();
		final Map<String, VortexAgentBackupFile> removedSink = new HashMap<String, VortexAgentBackupFile>();
		try {
			state.evaluate(addedSink, updatedSink, removedSink);
			if (log.isLoggable(Level.FINE))
				LH.fine(log, state.getBackup().getSourcePath(), ": ", addedSink.size(), " added, ", updatedSink.keySet(), " updated, ",
						removedSink.size() + " removed, " + state.getFilesCount() + " total");

			if (!addedSink.isEmpty() || !removedSink.isEmpty() || !updatedSink.isEmpty()) {
				VortexUpdateBackupStatusesFromAgent updmsg = nw(VortexUpdateBackupStatusesFromAgent.class);
				updmsg.setAddedFiles(CH.l(addedSink.values()));
				updmsg.setUpdatedFiles(CH.l(updatedSink.values()));
				updmsg.setRemovedFiles(CH.l(removedSink.values()));
				updmsg.setUpdated(CH.l(state.evalBackup()));

				toEye.send(updmsg, threadScope);
			}
		} catch (Exception e) {
			LH.warning(log, "Monitor for managed directory failed: BU-", state.getBackup().getId(), " dir: ", state.getBackup().getSourcePath(), e);
		}

		if (needsPolling) {
			loopback.sendDelayed(action, threadScope, state.getDelay(), TimeUnit.MILLISECONDS);
		}
	}
	private int processData(VortexAgentBackupFile file) {
		if (file.getSize() <= VortexAgentBackupState.MAX_DATA_CAPTURE_SIZE) {
			try {
				final byte[] data = IOH.readData(new File(file.getPath()));
				if (data.length < VortexAgentBackupState.MAX_DATA_CAPTURE_SIZE) {
					file.setMask(MH.clearBits(file.getMask(), VortexAgentBackupFile.DATA_DEFPLATED));
					file.setData(data);
				}
				VortexAgentUtils.compressFile(file);
				file.setDataOffset(0L);
				return file.getData().length;
			} catch (IOException e) {
				LH.warning(log, "error with file: ", file, e);
			}
		}
		return 0;
	}

	@Override
	public Object getPartitionId(VortexAgentBackupUpdateMessage action) {
		return action.getPartitionId();
	}

}
