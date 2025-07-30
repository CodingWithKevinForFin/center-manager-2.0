package com.vortex.testtrackagent;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.Executor;

import com.f1.container.RequestMessage;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.utils.EH;
import com.f1.utils.FastByteArrayInputStream;
import com.f1.utils.FastByteArrayOutputStream;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.MonitoredRunnable;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.StreamPiper;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunOsCommandRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunOsCommandResponse;
import com.vortex.agent.VortexAgentUtils;

public class AgentRunOsCommandProcessor extends BasicRequestProcessor<VortexAgentRunOsCommandRequest, State, VortexAgentRunOsCommandResponse> {

	public AgentRunOsCommandProcessor() {
		super(VortexAgentRunOsCommandRequest.class, State.class, VortexAgentRunOsCommandResponse.class);
	}

	@Override
	protected VortexAgentRunOsCommandResponse processRequest(RequestMessage<VortexAgentRunOsCommandRequest> action, State state, ThreadScope threadScope) throws Exception {
		VortexAgentRunOsCommandRequest req = action.getAction();

		VortexAgentRunOsCommandResponse r = nw(VortexAgentRunOsCommandResponse.class);
		final boolean commandsEnabled = getTools().getOptional("f1.agent.enable.commands", false);
		if (!commandsEnabled) {
			r.setMessage("f1.agent.enable.commands must be set to true");
			return r;
		}
		if (!EH.getProcessUid().equals(req.getTargetAgentProcessUid())) {
			r.setMessage("invalid processuid: " + req.getTargetAgentProcessUid());
			return r;
		}
		final String rawCommand = req.getCommand();
		final String owner = req.getOwner();
		final byte[] stdin = req.getStdin();
		req.getInvokedBy();
		req.getMaxCaptureStderr();
		req.getMaxCaptureStdout();
		long maxRuntime = req.getMaxRuntimeMs();
		final FastByteArrayOutputStream stderrStream = new FastByteArrayOutputStream();
		final FastByteArrayOutputStream stdoutStream = new FastByteArrayOutputStream();
		final FastByteArrayInputStream stdinStream = new FastByteArrayInputStream(stdin == null ? OH.EMPTY_BYTE_ARRAY : stdin);
		final Executor executor = getContainer().getThreadPoolController();
		final Process process;
		final boolean completed;
		final long startTime;
		final long endTime;
		String thisUser = EH.getUserName();
		final String command = VortexAgentUtils.toSudo(owner, rawCommand);
		r.setCommand(command);
		r.setOwner(owner);
		r.setStdin(stdin);
		try {
			if (SH.is(req.getPwd()))
				process = Runtime.getRuntime().exec(command, OH.EMPTY_STRING_ARRAY, new File(req.getPwd()));
			else
				process = Runtime.getRuntime().exec(command);
			final StreamPiper p1 = new StreamPiper(process.getErrorStream(), stderrStream, 4096);
			final StreamPiper p2 = new StreamPiper(process.getInputStream(), stdoutStream, 4096);
			final StreamPiper p3 = new StreamPiper(stdinStream, process.getOutputStream(), 4096);
			final MonitoredRunnable runnerForProcess = new MonitoredRunnable(new ProcessWaiter(process));
			final MonitoredRunnable runnerForStderr = new MonitoredRunnable(p1);
			final MonitoredRunnable runnerForStdout = new MonitoredRunnable(p2);
			final MonitoredRunnable runnerForStdin = new MonitoredRunnable(p3);
			executor.execute(runnerForStderr);
			executor.execute(runnerForStdout);
			executor.execute(runnerForStdin);
			executor.execute(runnerForProcess);
			startTime = EH.currentTimeMillis();
			completed = runnerForProcess.waitUntilComplete(maxRuntime);
			endTime = EH.currentTimeMillis();
			if (completed) {
				runnerForStderr.waitUntilComplete();
				runnerForStdout.waitUntilComplete();
				runnerForStdin.waitUntilComplete();
			} else {
				runnerForProcess.interruptThread();
				runnerForStderr.interruptThread();
				runnerForStdout.interruptThread();
				runnerForStdin.interruptThread();
			}
			IOH.close(process.getInputStream());
			IOH.close(process.getOutputStream());
			IOH.close(process.getErrorStream());

		} catch (Exception e) {
			r.setOk(false);
			r.setMessage("Unexpected Error: " + e);
			LH.w(log, "Error running command: ", r, e);
			return r;
		}

		r.setStartTime(startTime);
		r.setEndTime(endTime);

		if (completed) {
			r.setExitcode(process.exitValue());
			final byte[] stderr = stderrStream.toByteArray();
			r.setStderrLength(stderr.length);
			r.setStderr(trim(stderr, req.getMaxCaptureStderr()));

			final byte[] stdout = stdoutStream.toByteArray();
			r.setStdoutLength(stdout.length);
			r.setStdout(trim(stdout, req.getMaxCaptureStderr()));
			r.setOk(true);
		} else {
			r.setMessage("process timed out");
		}

		return r;
	}
	private byte[] trim(byte[] data, int maxLen) {
		if (data == null || data.length <= maxLen)
			return data;
		return Arrays.copyOf(data, maxLen);
	}

	public static class ProcessWaiter implements Runnable {

		private Process process;
		public ProcessWaiter(Process process) {
			this.process = process;
		}

		@Override
		public void run() {
			try {
				this.process.waitFor();
			} catch (InterruptedException e) {
				return;
			}
		}

	}
}
