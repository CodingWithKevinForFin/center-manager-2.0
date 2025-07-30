package com.vortex.agent;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileDeleteRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileDeleteResponse;
import com.vortex.agent.osadapter.VortexAgentOsAdapterFileDeleter;
import com.vortex.agent.state.VortexAgentOsAdapterState;

public class VortexAgentFileDeleter implements VortexAgentOsAdapterFileDeleter {
	private static final Logger log = LH.get(VortexAgentFileDeleter.class);
	private File backup;

	public VortexAgentFileDeleter(File backup) {
		this.backup = backup;
		try {
			IOH.ensureDir(this.backup);
		} catch (IOException e) {
			throw new RuntimeException("Could not create backup directory for deletes: " + IOH.getFullPath(this.backup), e);
		}
	}

	@Override
	public VortexAgentFileDeleteResponse deleteFiles(VortexAgentFileDeleteRequest requestMessage, VortexAgentOsAdapterState state) {
		VortexAgentFileDeleteResponse r = state.nw(VortexAgentFileDeleteResponse.class);
		r.setOk(true);
		for (String fileText : requestMessage.getFiles()) {
			final File file = new File(fileText);
			try {
				if (requestMessage.getIsPermanent()) {
					LH.warning(log, "Force deleting file: ", IOH.getFullPath(file));
					IOH.deleteForce(file);
				} else {
					File dest = new File(backup, state.getPartition().getContainer().getTools().getNow() + "_" + file.getName());
					LH.warning(log, "Moving to recylycing bin: ", IOH.getFullPath(file), " ==> ", dest);
					IOH.moveForce(file, dest);
				}
			} catch (Exception e) {
				LH.warning(log, "Error deleting file: ", IOH.getFullPath(file), e);
				r.setMessage("Error deleting file: " + IOH.getFullPath(file));
				r.setOk(false);
				break;
			}
		}
		return r;
	}
}
