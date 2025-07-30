package com.vortex.agent.itinerary;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.base.Message;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunDeploymentRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunDeploymentResponse;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunOsCommandRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunOsCommandResponse;
import com.f1.vortexcommon.msg.eye.VortexDeployment;
import com.vortex.agent.messages.VortexAgentDeploymentUpdateMessage;
import com.vortex.agent.messages.VortexAgentOsAdapterDeploymentRequest;
import com.vortex.agent.messages.VortexAgentOsAdapterRequest;
import com.vortex.agent.messages.VortexAgentOsAdapterResponse;
import com.vortex.agent.state.VortexAgentDeploymentWrapper;
import com.vortex.agent.state.VortexAgentState;

public class VortexAgentRunDeploymentItinerary extends AbstractVortexAgentItinerary<VortexAgentRunDeploymentRequest> {
	private static final Logger log = LH.get(VortexAgentRunDeploymentItinerary.class);

	private static final int STEP_SENT_SCRIPT_COMMAND_TO_OS = 1;
	private static final int STEP_SENT_DEPLOYMENT_TO_OS = 2;
	private static final int STEP_SENT_UNDEPLOYMENT_TO_OS = 5;
	private static final int STEP_SENT_EXPAND_VERIFY_TO_OS = 3;
	private static final int STEP_SENT_COMMAND_TO_VERIFY = 4;
	private static final int STEP_SENT_RUN_INSTALL_TO_OS = 6;
	private static final int STEP_SENT_RUN_UNINSTALL_TO_OS = 7;
	private static final int STEP_SENT_REMOVE_VERIFY_DIR = 8;
	private int step;

	private VortexAgentRunDeploymentResponse r;

	private int maxCaptureSize = 1000 * 1000;

	private VortexAgentDeploymentWrapper deployment;

	private File verifyTargetDir;

	@Override
	public byte startJourney(VortexAgentItineraryWorker worker) {
		RequestMessage<VortexAgentRunDeploymentRequest> action = getInitialRequest();

		VortexAgentState state = getState();
		VortexAgentRunDeploymentRequest req = action.getAction();
		r = nw(VortexAgentRunDeploymentResponse.class);
		//deployment.init(action.getAction());
		deployment = state.getDeployment(action.getAction().getDeploymentId());
		if (deployment == null) {
			r.setMessage("Deployment not found: " + action.getAction().getDeploymentId());
			return STATUS_COMPLETE;
		}
		VortexDeployment dep = deployment.getDeployment();
		int status = dep.getStatus();

		final boolean commandsEnabled = getTools().getOptional("f1.agent.enable.commands", false);
		if (!commandsEnabled) {
			r.setMessage("f1.agent.enable.commands must be set to true");
			return STATUS_COMPLETE;
		}
		if (!EH.getProcessUid().equals(req.getTargetAgentProcessUid())) {
			r.setMessage("invalid processuid: " + req.getTargetAgentProcessUid());
			return STATUS_COMPLETE;
		}

		switch (req.getCommandType()) {
			case VortexAgentRunDeploymentRequest.TYPE_START_SCRIPT: {
				sendRunScript(worker, "Start Script", deployment.getStartScript(), deployment.getTargetDir(), VortexDeployment.STATUS_ACTION_STARTING______);
				step = STEP_SENT_SCRIPT_COMMAND_TO_OS;
				return STATUS_ACTIVE;
			}
			case VortexAgentRunDeploymentRequest.TYPE_RUN_SCRIPT: {
				File path = IOH.joinPaths(dep.getTargetDirectory(), dep.getScriptsDirectory(), req.getTargetFile());
				sendRunScript(worker, "Stop Script", path.toString(), deployment.getTargetDir(), VortexDeployment.STATUS_ACTION_RUNNING_SCRIPT);
				step = STEP_SENT_SCRIPT_COMMAND_TO_OS;
				return STATUS_ACTIVE;
			}
			case VortexAgentRunDeploymentRequest.TYPE_STOP_SCRIPT: {
				sendRunScript(worker, "Custom Script", deployment.getStopScript(), deployment.getTargetDir(), VortexDeployment.STATUS_ACTION_STOPPING______);
				step = STEP_SENT_SCRIPT_COMMAND_TO_OS;
				return STATUS_ACTIVE;
			}
			case VortexAgentRunDeploymentRequest.TYPE_DELETE_ALL_FILES: {
				if (deployment.getUninstallScript() != null) {
					sendRunScript(worker, "Uninstall Script", deployment.getUninstallScript(), deployment.getTargetDir(), VortexDeployment.STATUS_ACTION_UNINSTALLING__);
					step = STEP_SENT_RUN_UNINSTALL_TO_OS;
				} else {
					sendUndeployment(worker);
					step = STEP_SENT_UNDEPLOYMENT_TO_OS;
				}
				return STATUS_ACTIVE;
			}
			case VortexAgentRunDeploymentRequest.TYPE_DEPLOY: {
				setAndSendStatus(deployment.getId(), VortexDeployment.STATUS_ACTION_INSTALLING____, VortexDeployment.MASK_ACTIONS, "deploy to: " + dep.getTargetDirectory(), worker);
				dep = deployment.getDeployment().clone();
				dep.setCurrentBuildInvokedBy(req.getInvokedBy());
				dep.setDeployedInstanceId(req.getDeployedInstanceId());
				dep.setCurrentBuildResultId(req.getBuildResultId());
				updateAndSendDeployment(dep, worker);

				VortexAgentOsAdapterRequest osRequest = getState().nw(VortexAgentOsAdapterRequest.class);
				osRequest.setCommandType(VortexAgentOsAdapterRequest.RUN_DEPLOYMENT);
				osRequest.setPartitionId("DEPLOYMENT_" + dep.getId());
				VortexAgentOsAdapterDeploymentRequest osDeploymentRequest = nw(VortexAgentOsAdapterDeploymentRequest.class);
				osDeploymentRequest.setData(req.getData());
				osDeploymentRequest.setInvokedBy(req.getInvokedBy());
				osDeploymentRequest.setPropertyFiles(req.getPropertyFiles());
				osDeploymentRequest.setTargetDirectory(dep.getTargetDirectory());
				osDeploymentRequest.setAutoDeleteFiles(dep.getAutoDeleteFiles());
				osDeploymentRequest.setOwner(dep.getTargetUser());
				osRequest.setRequestMessage(osDeploymentRequest);
				osDeploymentRequest.setType(VortexAgentOsAdapterDeploymentRequest.TYPE_DEPLOY);
				worker.sendRequestToOsAdapter(this, osRequest);
				step = STEP_SENT_DEPLOYMENT_TO_OS;
				return STATUS_ACTIVE;
			}
			case VortexAgentRunDeploymentRequest.TYPE_VERIFY: {
				if (SH.isnt(dep.getVerifyScriptFile())) {
					r.setMessage("Could not verify, no verify script defined");
					return STATUS_COMPLETE;
				}
				dep = deployment.getDeployment().clone();
				//dep.setCurrentBuildInvokedBy(req.getInvokedBy());
				//dep.setCurrentBuildResultId(req.getBuildResultId());
				//deployment.init(dep);//TODO:fix
				VortexAgentOsAdapterRequest osRequest = getState().nw(VortexAgentOsAdapterRequest.class);
				osRequest.setCommandType(VortexAgentOsAdapterRequest.RUN_DEPLOYMENT);
				osRequest.setPartitionId("DEPLOYMENT_" + dep.getId());

				if (req.getVerifyData() != null) {
					this.verifyTargetDir = null;
					int id = 0;
					do {
						String scratchDir = state.getPartition().getContainer().getTools().getOptional("scratch_dir", "/tmp/vortex_tmp");
						verifyTargetDir = new File(scratchDir, "scratch_" + id).getAbsoluteFile();
						id++;
					} while (verifyTargetDir.exists());

					LH.info(log, "Verify scratch dir: ", IOH.getFullPath(verifyTargetDir));
					try {
						IOH.ensureDir(verifyTargetDir);
					} catch (IOException e) {
						LH.warning(log, "Could not create scratch dir: ", verifyTargetDir, e);
						r.setMessage("Could not create scratch dir: " + verifyTargetDir);
					}
					if (OH.ne(dep.getTargetUser(), EH.getUserName())) {
						verifyTargetDir.setReadable(true, false);
						verifyTargetDir.setWritable(true, false);
						verifyTargetDir.setExecutable(true, false);
					}
					VortexAgentOsAdapterDeploymentRequest osDeploymentRequest = nw(VortexAgentOsAdapterDeploymentRequest.class);
					osDeploymentRequest.setData(req.getVerifyData());
					osDeploymentRequest.setInvokedBy(req.getInvokedBy());
					osDeploymentRequest.setTargetDirectory(verifyTargetDir.getAbsolutePath());
					osDeploymentRequest.setOwner(dep.getTargetUser());
					osRequest.setRequestMessage(osDeploymentRequest);
					osDeploymentRequest.setType(VortexAgentOsAdapterDeploymentRequest.TYPE_VERIFY);
					worker.sendRequestToOsAdapter(this, osRequest);
					step = STEP_SENT_EXPAND_VERIFY_TO_OS;
				} else {
					File command = new File(dep.getVerifyScriptFile());
					sendRunScript(worker, "Verify Script", command.toString(), null, VortexDeployment.STATUS_ACTION_VERIFYING_____);
					step = STEP_SENT_COMMAND_TO_VERIFY;
					return STATUS_ACTIVE;
				}
				return STATUS_ACTIVE;
			}
		}
		r.setMessage("Unknown type: " + req.getCommandType());
		return STATUS_COMPLETE;
	}
	@Override
	public byte onResponse(ResultMessage<?> result, VortexAgentItineraryWorker worker) {
		VortexDeployment dep = deployment.getDeployment();
		int status = dep.getStatus();
		switch (step) {
			case STEP_SENT_SCRIPT_COMMAND_TO_OS: {
				VortexAgentOsAdapterResponse osAdapterResponse = (VortexAgentOsAdapterResponse) result.getAction();
				VortexAgentRunOsCommandResponse res = (VortexAgentRunOsCommandResponse) osAdapterResponse.getResponseMessage();
				r.setMessage(res.getMessage());
				r.setOk(res.getOk());
				if (r.getOk())
					setAndSendStatus(deployment.getId(), 0, VortexDeployment.MASK_ACTIONS, SH.noNull(r.getMessage()), worker);
				break;
			}
			case STEP_SENT_UNDEPLOYMENT_TO_OS: {
				VortexAgentOsAdapterResponse osAdapterResponse = (VortexAgentOsAdapterResponse) result.getAction();
				VortexAgentRunDeploymentResponse res = (VortexAgentRunDeploymentResponse) osAdapterResponse.getResponseMessage();
				deployment.getTargetDir().delete();
				r.setMessage(res.getMessage());
				r.setOk(res.getOk());
				if (r.getOk())
					setAndSendStatus(deployment.getId(), 0, VortexDeployment.MASK_ACTIONS, SH.noNull(r.getMessage()), worker);
				break;
			}
			case STEP_SENT_DEPLOYMENT_TO_OS: {
				VortexAgentOsAdapterResponse osAdapterResponse = (VortexAgentOsAdapterResponse) result.getAction();
				VortexAgentRunDeploymentResponse res = (VortexAgentRunDeploymentResponse) osAdapterResponse.getResponseMessage();
				if (!res.getOk()) {
					r.setMessage(res.getMessage());
					r.setOk(false);
				} else if (deployment.getInstallScript() != null) {
					sendRunScript(worker, "Install Script", deployment.getInstallScript(), deployment.getTargetDir(), VortexDeployment.STATUS_ACTION_INSTALLING____);
					step = STEP_SENT_RUN_INSTALL_TO_OS;
					return STATUS_ACTIVE;
				} else {
					r.setOk(true);
					setAndSendStatus(deployment.getId(), 0, VortexDeployment.MASK_ACTIONS, SH.noNull(r.getMessage()), worker);
				}
				break;
			}
			case STEP_SENT_RUN_UNINSTALL_TO_OS: {
				VortexAgentOsAdapterResponse osAdapterResponse = (VortexAgentOsAdapterResponse) result.getAction();
				VortexAgentRunOsCommandResponse res = (VortexAgentRunOsCommandResponse) osAdapterResponse.getResponseMessage();
				r.setMessage(res.getMessage());
				r.setOk(res.getOk());
				r.setUninstallExitCode(res.getExitcode());
				r.setUninstallStderr(res.getStderr());
				r.setUninstallStdout(res.getStdout());
				if (r.getOk()) {
					sendUndeployment(worker);
					step = STEP_SENT_UNDEPLOYMENT_TO_OS;
					return STATUS_ACTIVE;
				}
				break;
			}
			case STEP_SENT_RUN_INSTALL_TO_OS: {
				VortexAgentOsAdapterResponse osAdapterResponse = (VortexAgentOsAdapterResponse) result.getAction();
				VortexAgentRunOsCommandResponse res = (VortexAgentRunOsCommandResponse) osAdapterResponse.getResponseMessage();
				r.setMessage(res.getMessage());
				r.setOk(res.getOk());
				r.setInstallExitCode(res.getExitcode());
				r.setInstallStderr(res.getStderr());
				r.setInstallStdout(res.getStdout());
				if (r.getOk())
					setAndSendStatus(deployment.getId(), 0, VortexDeployment.MASK_ACTIONS, SH.noNull(r.getMessage()), worker);
				break;
			}
			case STEP_SENT_EXPAND_VERIFY_TO_OS: {
				VortexAgentOsAdapterResponse osAdapterResponse = (VortexAgentOsAdapterResponse) result.getAction();
				VortexAgentRunDeploymentResponse res = (VortexAgentRunDeploymentResponse) osAdapterResponse.getResponseMessage();
				if (res.getOk()) {
					if (verifyTargetDir != null) {
						File command = IOH.joinPaths(verifyTargetDir, dep.getVerifyScriptFile());
						sendRunScript(worker, "Verify Script", command.toString(), verifyTargetDir, VortexDeployment.STATUS_ACTION_VERIFYING_____);
						step = STEP_SENT_COMMAND_TO_VERIFY;
					}
					return STATUS_ACTIVE;
				} else {
					r.setMessage(res.getMessage());
					r.setOk(res.getOk());
					if (verifyTargetDir != null) {
						sendRemoveVerifyDir(worker);
						step = STEP_SENT_REMOVE_VERIFY_DIR;
					}
				}
				break;
			}
			case STEP_SENT_COMMAND_TO_VERIFY: {
				VortexAgentOsAdapterResponse osAdapterResponse = (VortexAgentOsAdapterResponse) result.getAction();
				VortexAgentRunOsCommandResponse res = (VortexAgentRunOsCommandResponse) osAdapterResponse.getResponseMessage();
				LH.info(log, "Verify finished, removing scratchdir: ", IOH.getFullPath(verifyTargetDir));
				if (res.getOk()) {
					r.setVerifyExitCode(res.getExitcode());
					r.setVerifyStderr(res.getStderr());
					r.setVerifyStdout(res.getStdout());
					r.setOk(true);
				} else {
					r.setMessage(res.getMessage());
					r.setOk(res.getOk());
				}
				if (verifyTargetDir != null) {
					sendRemoveVerifyDir(worker);
					step = STEP_SENT_REMOVE_VERIFY_DIR;
					return STATUS_ACTIVE;
				} else
					setAndSendStatus(deployment.getId(), 0, VortexDeployment.STATUS_ACTION_VERIFYING_____, SH.noNull(r.getMessage()), worker);
				break;
			}
			case STEP_SENT_REMOVE_VERIFY_DIR: {
				if (verifyTargetDir != null)
					try {
						IOH.delete(verifyTargetDir);
					} catch (IOException e) {
						LH.log(log, Level.WARNING, "Error removing scratch dir", e);
					}
				setAndSendStatus(deployment.getId(), 0, VortexDeployment.STATUS_ACTION_VERIFYING_____, SH.noNull(r.getMessage()), worker);
				return STATUS_COMPLETE;
			}
		}
		return STATUS_COMPLETE;
	}
	private void sendRemoveVerifyDir(VortexAgentItineraryWorker worker) {
		VortexDeployment dep = deployment.getDeployment();
		VortexAgentOsAdapterRequest osRequest = getState().nw(VortexAgentOsAdapterRequest.class);
		osRequest.setCommandType(VortexAgentOsAdapterRequest.RUN_DEPLOYMENT);
		osRequest.setPartitionId("DEPLOYMENT_" + dep.getId());
		VortexAgentOsAdapterDeploymentRequest osDeploymentRequest = nw(VortexAgentOsAdapterDeploymentRequest.class);
		osDeploymentRequest.setInvokedBy(getInitialRequest().getAction().getInvokedBy());
		osDeploymentRequest.setTargetDirectory(verifyTargetDir.getAbsolutePath());
		osDeploymentRequest.setOwner(dep.getTargetUser());
		osDeploymentRequest.setType(VortexAgentOsAdapterDeploymentRequest.TYPE_UNDEPLOY);
		osRequest.setRequestMessage(osDeploymentRequest);
		worker.sendRequestToOsAdapter(this, osRequest);

	}
	private void sendRunScript(VortexAgentItineraryWorker worker, String description, String cmd, File pwd, int actionStatus) {
		RequestMessage<VortexAgentRunDeploymentRequest> action = getInitialRequest();
		VortexAgentRunDeploymentRequest req = action.getAction();
		VortexDeployment dep = deployment.getDeployment();
		int status = dep.getStatus();
		setAndSendStatus(deployment.getId(), actionStatus, VortexDeployment.MASK_ACTIONS, description + ": " + SH.afterLast(cmd, '/'), worker);
		VortexAgentOsAdapterRequest osRequest = getState().nw(VortexAgentOsAdapterRequest.class);
		osRequest.setCommandType(VortexAgentOsAdapterRequest.RUN_COMMAND);
		osRequest.setPartitionId("DEPLOYMENT_" + dep.getId());
		VortexAgentRunOsCommandRequest cmdRequest = nw(VortexAgentRunOsCommandRequest.class);
		if (pwd != null)
			cmdRequest.setPwd(pwd.toString());
		cmdRequest.setCommand(cmd);
		cmdRequest.setInvokedBy(req.getInvokedBy());
		cmdRequest.setMaxCaptureStderr(maxCaptureSize);
		cmdRequest.setMaxCaptureStdout(maxCaptureSize);
		cmdRequest.setOwner(dep.getTargetUser());
		cmdRequest.setMaxRuntimeMs(60 * 1000);
		cmdRequest.setStdin(OH.EMPTY_BYTE_ARRAY);
		cmdRequest.setEnvVars(deployment.getEnvVars());
		osRequest.setRequestMessage(cmdRequest);
		worker.sendRequestToOsAdapter(this, osRequest);

	}
	private void sendUndeployment(VortexAgentItineraryWorker worker) {
		VortexDeployment dep = deployment.getDeployment();
		int status = dep.getStatus();
		setAndSendStatus(deployment.getId(), VortexDeployment.STATUS_ACTION_UNINSTALLING__, VortexDeployment.MASK_ACTIONS, "removing: " + dep.getTargetDirectory(), worker);
		VortexAgentOsAdapterRequest osRequest = getState().nw(VortexAgentOsAdapterRequest.class);
		osRequest.setCommandType(VortexAgentOsAdapterRequest.RUN_DEPLOYMENT);
		osRequest.setPartitionId("DEPLOYMENT_" + dep.getId());
		VortexAgentOsAdapterDeploymentRequest osDeploymentRequest = nw(VortexAgentOsAdapterDeploymentRequest.class);
		osDeploymentRequest.setInvokedBy(getInitialRequest().getAction().getInvokedBy());
		osDeploymentRequest.setTargetDirectory(dep.getTargetDirectory());
		osDeploymentRequest.setOwner(dep.getTargetUser());
		osDeploymentRequest.setType(VortexAgentOsAdapterDeploymentRequest.TYPE_UNDEPLOY);
		osRequest.setRequestMessage(osDeploymentRequest);
		worker.sendRequestToOsAdapter(this, osRequest);
	}

	@Override
	public Message endJourney(VortexAgentItineraryWorker worker) {
		return r;
	}
	private void updateAndSendDeployment(VortexDeployment dep, VortexAgentItineraryWorker worker) {
		this.deployment = new VortexAgentDeploymentWrapper(dep);
		getState().addDeployment(this.deployment);
		VortexAgentDeploymentUpdateMessage updmsg = nw(VortexAgentDeploymentUpdateMessage.class);
		updmsg.setDeployment(dep);
		updmsg.setPartitionId("DP_" + dep.getId());
		worker.sendToDeployment(this, updmsg);
	}

	private void setAndSendStatus(long dpid, int statusToSet, int statusToClear, String msg, VortexAgentItineraryWorker worker) {
		int status = statusToSet;
		//if (deployment.getId() == 1013640)
		//System.out.println("adding-status to DP-" + deployment.getId() + ": " + SH.toHex(deployment.getStatus()) + " ==> " + SH.toHex(status));
		//long now = (getTools().getNow());
		//VortexDeployment stat = t.getDeployment();
		//if (t.getDeployment().getStatus() == status && OH.eq(t.getDeployment().getMessage(), msg))
		//return;
		//stat = stat.clone();
		//stat.setStatus(status);
		//stat.setMessage(msg);
		//stat.setNow(now);
		VortexAgentDeploymentUpdateMessage updmsg = nw(VortexAgentDeploymentUpdateMessage.class);
		updmsg.setPartitionId("DP_" + dpid);
		updmsg.setStatusBitsToSet(statusToSet);
		updmsg.setStatusBitsToClear(statusToClear);
		updmsg.setMessage(msg);
		worker.sendToDeployment(this, updmsg);
		//VortexUpdateDeploymentStatusesFromAgent updmsg = nw(VortexUpdateDeploymentStatusesFromAgent.class);
		//updmsg.setUpdated(CH.l(stat));
		//worker.sendToEye(this, updmsg);
	}

}
