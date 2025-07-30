package com.f1.vortexcommon.msg.agent.reqres;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.SBFDR")
public interface VortexAgentSendBackupFilesToDestinationResponse extends VortexAgentResponse {

	@PID(1)
	void setFilesUpdatedStats(long stats);
	long getFilesUpdatedStats();

	@PID(2)
	void setFilesAddedStats(long stats);
	long getFilesAddedStats();

	@PID(3)
	void setFilesDeletedStats(long stats);
	long getFilesDeletedStats();

	@PID(4)
	void setFilesUnchangedStats(long stats);
	long getFilesUnchangedStats();

	@PID(5)
	void setBytesTransfered(long stats);
	long getBytesTransfered();

}
