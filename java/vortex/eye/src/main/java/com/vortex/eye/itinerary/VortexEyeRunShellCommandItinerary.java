package com.vortex.eye.itinerary;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadPoolController;
import com.f1.povo.standard.RunnableRequestMessage;
import com.f1.povo.standard.TextMessage;
import com.f1.utils.LH;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunShellCommandRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunShellCommandResponse;
import com.vortex.eye.VortexEyeSshClient;
import com.vortex.eye.VortexEyeUtils;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;

public class VortexEyeRunShellCommandItinerary extends AbstractVortexEyeItinerary<VortexEyeRunShellCommandRequest> {
	private static final Logger log = LH.get(VortexEyeRunShellCommandItinerary.class);

	private VortexEyeRunShellCommandResponse r;
	private int currentPosition;
	private List<VortexEyeSshClient> results = new ArrayList<VortexEyeSshClient>();
	private VortexEyeSshClient currentSsh;

	@Override
	public byte startJourney(VortexEyeItineraryWorker worker) {
		this.r = getState().nw(VortexEyeRunShellCommandResponse.class);
		VortexEyeRunShellCommandRequest req = this.getInitialRequest().getAction();
		int timeout = req.getTimeoutMs();
		if (timeout <= 0) {
			r.setMessage("Invalid timeout: " + timeout);
			return STATUS_COMPLETE;
		}
		currentPosition = 0;

		runNextCommand(worker);
		return STATUS_ACTIVE;
	}
	private boolean runNextCommand(VortexEyeItineraryWorker worker) {
		final VortexEyeRunShellCommandRequest req = this.getInitialRequest().getAction();
		final int start = this.currentPosition++;
		if (start >= req.getCommands().size())
			return false;
		byte[] stdin = req.getStdins() == null ? null : req.getStdins().get(start);
		ThreadPoolController tpc = getTools().getContainer().getThreadPoolController();
		int timeout = req.getTimeoutMs();
		String password = VortexEyeUtils.decryptToString(req.getPassword());
		String publicKeyData = VortexEyeUtils.decryptToString(req.getPublicKeyData());
		String command = req.getCommands().get(start);
		int port = req.getPort() == 0 ? 22 : req.getPort();
		LH.info(log, "Running command on ", req.getUsername(), "@", req.getHostName(), ":", port, ": ", command);
		currentSsh = new VortexEyeSshClient(req.getHostName(), port, req.getUsername(), password, command, stdin, tpc, timeout, publicKeyData == null ? null
				: publicKeyData.toCharArray(), req.getUseTTY());
		RunnableRequestMessage rm = getState().nw(RunnableRequestMessage.class);
		rm.setPartitionId(req.getHostName());
		rm.setRunnable(currentSsh);
		rm.setTimeoutMs(timeout);
		worker.sendRunnable(this, rm);
		return true;
	}
	@Override
	public byte onResponse(ResultMessage<?> result, VortexEyeItineraryWorker worker) {
		TextMessage txt = (TextMessage) result.getAction();
		if (txt.getText() != null) {
			r.setMessage(txt.getText());
			return STATUS_COMPLETE;
		} else {
			results.add(this.currentSsh);
			if (!this.currentSsh.isComplete())
				throw new IllegalStateException("still running");
			if (this.currentSsh.getException() != null) {
				Exception e = this.currentSsh.getException();
				if (currentSsh.getExitCode() == VortexEyeSshClient.GENERAL_ERROR)
					LH.warning(log, "Error running command: " + currentSsh.getCommand(), e);
			}
			if (runNextCommand(worker)) {
				return STATUS_ACTIVE;
			} else {
				r.setOk(true);
				return STATUS_COMPLETE;
			}
		}
	}
	@Override
	public Message endJourney(VortexEyeItineraryWorker worker) {
		final VortexEyeRunShellCommandRequest req = this.getInitialRequest().getAction();
		int size = results.size();
		r.setExitCodes(new int[size]);
		r.setStderrs(new ArrayList<byte[]>(size));
		r.setStdouts(new ArrayList<byte[]>(size));
		for (int i = 0; i < results.size(); i++) {
			VortexEyeSshClient result = results.get(i);
			if (result.getException() != null) {
				r.getExitCodes()[i] = -1;
				r.getStderrs().add(result.getException().getClass().getName().getBytes());
			} else {
				r.getExitCodes()[i] = result.getExitCode();
				r.getStderrs().add(result.getStderr());
				r.getStdouts().add(result.getStdout());
			}
		}
		return r;
	}
	@Override
	protected void populateAuditEvent(VortexEyeRunShellCommandRequest action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink) {
		sink.setEventType(VortexEyeClientEvent.TYPE_RUN_SHELL_COMMAND);
		sink.getParams().put("HOST", action.getHostName());
		sink.getParams().put("USER", action.getUsername());
		auditList(sink, "CMDS", action.getCommands());
	}
}
