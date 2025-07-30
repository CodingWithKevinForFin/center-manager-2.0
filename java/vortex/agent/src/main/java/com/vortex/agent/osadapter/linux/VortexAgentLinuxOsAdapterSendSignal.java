package com.vortex.agent.osadapter.linux;

import java.util.logging.Logger;

import com.f1.utils.EH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple3;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunSignalProcessRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunSignalProcessResponse;
import com.vortex.agent.VortexAgentUtils;
import com.vortex.agent.osadapter.VortexAgentOsAdapterSendSignal;
import com.vortex.agent.state.VortexAgentOsAdapterState;

public class VortexAgentLinuxOsAdapterSendSignal implements VortexAgentOsAdapterSendSignal {

	private static final Logger log = LH.get(VortexAgentLinuxOsAdapterSendSignal.class);

	@Override
	public VortexAgentRunSignalProcessResponse sendSignal(VortexAgentRunSignalProcessRequest req, VortexAgentOsAdapterState state) {
		LH.info(log, "Sending " + req.getSignal() + " signal  to process: " + req.getProcessId());
		VortexAgentRunSignalProcessResponse r = state.nw(VortexAgentRunSignalProcessResponse.class);
		if (!EH.getProcessUid().equals(req.getTargetAgentProcessUid())) {
			r.setMessage("invalid processuid: " + req.getTargetAgentProcessUid());
			return r;
		}
		String thisUser = EH.getUserName();
		final String owner = req.getProcessOwner();
		final String sudoCommand = VortexAgentUtils.toSudo(owner, "");
		Tuple3<Process, byte[], byte[]> result = EH.exec(state.getPartition().getContainer().getThreadPoolController(),
				SH.splitContinous(' ', sudoCommand + " kill -" + req.getSignal() + " " + req.getProcessPid()));
		int exitCode = result.getA().exitValue();
		if (exitCode == 0)
			r.setOk(true);
		else
			r.setMessage(new String(result.getC()));
		return r;
	}

}
