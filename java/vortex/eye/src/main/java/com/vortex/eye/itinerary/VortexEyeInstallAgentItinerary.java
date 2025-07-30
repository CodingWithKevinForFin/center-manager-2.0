package com.vortex.eye.itinerary;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadPoolController;
import com.f1.povo.standard.RunnableRequestMessage;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeInstallAgentRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeInstallAgentResponse;
import com.vortex.eye.VortexEyeSshClient;
import com.vortex.eye.VortexEyeUtils;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;
import com.vortex.eye.state.VortexEyeState.AgentInterface;

public class VortexEyeInstallAgentItinerary extends AbstractVortexEyeItinerary<VortexEyeInstallAgentRequest> {
	private static final Logger log = LH.get(VortexEyeInstallAgentItinerary.class);

	private static final int TIMEOUT = 60 * 1000;
	final private static byte STEP_PREPARE_SUDO = 1;
	final private static byte STEP_STOP_CURRENT = 2;
	final private static byte STEP_CREATE_DIR = 3;
	final private static byte STEP_COPY_FILE = 4;
	final private static byte STEP_UNZIP = 5;
	final private static byte STEP_RUN_INSTALL_KEY = 6;
	final private static byte STEP_RUN_INSTALL = 7;

	private static final byte STEP_STARTUP = 0;

	private VortexEyeInstallAgentResponse r;
	private int currentPosition;
	private List<VortexEyeSshClient> results = new ArrayList<VortexEyeSshClient>();
	private VortexEyeSshClient currentSsh;

	private byte step;

	private byte[] keyFileData;

	private AgentInterface agentInterface;

	@Override
	public byte startJourney(VortexEyeItineraryWorker worker) {
		this.r = getState().nw(VortexEyeInstallAgentResponse.class);
		final VortexEyeInstallAgentRequest req = this.getInitialRequest().getAction();
		this.agentInterface = CH.getOrThrow(getState().getAgentInterfaces(), req.getAgentInterface());
		if (this.agentInterface.isSecure) {
			try {
				this.keyFileData = IOH.readData(new File(this.agentInterface.keyFile));
			} catch (IOException e) {
				LH.warning(log, "Could not load key file data", e);
				r.setMessage("Error loading secure key file");
				return STATUS_COMPLETE;
			}
		}
		runCommand(worker, "sed -i 's/^Defaults *requiretty/#Defaults requiretty/g' /etc/sudoers", null, true);
		step = STEP_PREPARE_SUDO;
		return STATUS_ACTIVE;
	}
	private boolean runCommand(VortexEyeItineraryWorker worker, String command, byte[] stdin, boolean useDumbTerminal) {
		final VortexEyeInstallAgentRequest req = this.getInitialRequest().getAction();
		LH.info(log, "Running command on ", req.getUsername(), "@", req.getHostName(), ": ", command);
		final int start = this.currentPosition++;
		ThreadPoolController tpc = getTools().getContainer().getThreadPoolController();
		String password = VortexEyeUtils.decryptToString(req.getPassword());
		String publicKeyData = VortexEyeUtils.decryptToString(req.getPublicKeyData());
		currentSsh = new VortexEyeSshClient(req.getHostName(), req.getPort(), req.getUsername(), password, "sudo " + command, stdin, tpc, TIMEOUT, publicKeyData == null ? null
				: publicKeyData.toCharArray(), useDumbTerminal);
		RunnableRequestMessage rm = getState().nw(RunnableRequestMessage.class);
		rm.setPartitionId(req.getHostName());
		rm.setRunnable(currentSsh);
		rm.setTimeoutMs(TIMEOUT);
		worker.sendRunnable(this, rm);
		return true;
	}

	private static String TEMP_AGENT_FILE = "/agent.tar.gz";

	@Override
	public byte onResponse(ResultMessage<?> result, VortexEyeItineraryWorker worker) {
		final VortexEyeInstallAgentRequest req = this.getInitialRequest().getAction();
		if (currentSsh.getExitCode() != 0 && step != STEP_STOP_CURRENT) {
			r.setMessage("For step " + step + ": Bad exit code: " + currentSsh.getExitCode() + " (cmd: " + currentSsh.getCommand() + ")");
			r.setOk(false);
			LH.warning(log, "Error installing agent on ", req.getUsername(), " @ ", req.getHostName(), " ==> Command: ", currentSsh.getCommand(), ", stdErr: ",
					currentSsh.getStderrAsString(1000), currentSsh.getException());
			return STATUS_COMPLETE;
		}
		switch (step) {
			case STEP_PREPARE_SUDO: {
				runCommand(worker, req.getTargetPath() + "/scripts/stop.sh ", null, false);
				step = STEP_STOP_CURRENT;
				return STATUS_ACTIVE;
			}
			case STEP_STOP_CURRENT: {
				step = STEP_CREATE_DIR;
				runCommand(worker, "mkdir -p " + req.getTargetPath(), null, false);
				return STATUS_ACTIVE;
			}
			case STEP_CREATE_DIR: {
				step = STEP_COPY_FILE;
				byte[] data = getState().getAgentPackageData(req.getAgentVersion()); //new File("/opt/build/final/vortexagent.2079.dev.tar.gz");
				runCommand(worker, "tee " + req.getTargetPath() + TEMP_AGENT_FILE + " > /dev/null", data, false);
				return STATUS_ACTIVE;
			}
			case STEP_COPY_FILE: {
				step = STEP_UNZIP;
				runCommand(worker, "tar -xzf " + req.getTargetPath() + TEMP_AGENT_FILE + " -C " + req.getTargetPath(), null, false);
				return STATUS_ACTIVE;
			}
			case STEP_UNZIP: {
				StringBuilder sb = new StringBuilder();

				String eyeHostName = new String(EH.exec(getTools().getContainer().getThreadPoolController(), "hostname").getB());
				int eyePort = getTools().getRequired("vortex.eye.port", Caster_Integer.INSTANCE);
				sb.append("vortex.eye.host=").append(agentInterface.hostname).append(SH.NEWLINE);
				sb.append("vortex.eye.backup.host=").append(agentInterface.hostname).append(SH.NEWLINE);
				sb.append("vortex.agent.deployuid=").append(req.getDeployUid()).append(SH.NEWLINE);
				sb.append("vortex.agent.version=").append(req.getAgentVersion()).append(SH.NEWLINE);
				sb.append("vortex.agent.eye.interface=").append(req.getAgentInterface()).append(SH.NEWLINE);
				sb.append("vortex.eye.port=").append(agentInterface.port).append(SH.NEWLINE);
				sb.append("vortex.eye.backup.port=").append(agentInterface.port).append(SH.NEWLINE);
				if (agentInterface.isSecure) {
					sb.append("keystore.file=config/key.jks").append(SH.NEWLINE);
					sb.append("keystore.password=").append(agentInterface.keyPassword).append(SH.NEWLINE);
					sb.append("vortex.ssl.port=").append(agentInterface.port).append(SH.NEWLINE);
					sb.append("vortex.ssl.backup.port=").append(agentInterface.port).append(SH.NEWLINE);
					sb.append("vortex.ssl.backup.host=").append(agentInterface.hostname).append(SH.NEWLINE);
					step = STEP_RUN_INSTALL_KEY;
				} else {
					step = STEP_RUN_INSTALL;
				}

				for (Entry<Object, Object> prop : getState().getAgentProperties().entrySet())
					sb.append(prop.getKey()).append('=').append(prop.getValue()).append(SH.NEWLINE);

				byte[] properties = sb.toString().getBytes();
				runCommand(worker, "tee " + req.getTargetPath() + "/config/local.properties > /dev/null", properties, false);
				return STATUS_ACTIVE;
			}
			case STEP_RUN_INSTALL_KEY: {
				runCommand(worker, "tee " + req.getTargetPath() + "/config/key.jks > /dev/null", keyFileData, false);
				step = STEP_RUN_INSTALL;
				return STATUS_ACTIVE;
			}
			case STEP_RUN_INSTALL: {
				step = STEP_STARTUP;
				runCommand(worker, "" + req.getTargetPath() + "/scripts/start.sh Unlocked", null, false);
				return STATUS_ACTIVE;
			}
			case STEP_STARTUP: {
				r.setOk(true);
				return STATUS_COMPLETE;
			}
			default:
				throw new RuntimeException("Unknown step: " + step);

		}
	}
	@Override
	public Message endJourney(VortexEyeItineraryWorker worker) {
		final VortexEyeInstallAgentRequest req = this.getInitialRequest().getAction();
		return r;
	}
	@Override
	protected void populateAuditEvent(VortexEyeInstallAgentRequest action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink) {
		sink.setEventType(VortexEyeClientEvent.TYPE_INSTALL_AGENT);
		sink.getParams().put("HOST", action.getHostName());
		sink.getParams().put("USER", action.getUsername());
		sink.getParams().put("PATH", action.getTargetPath());
	}
}
