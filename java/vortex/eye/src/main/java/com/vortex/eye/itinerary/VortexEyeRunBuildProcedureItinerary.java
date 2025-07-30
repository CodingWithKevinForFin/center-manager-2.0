package com.vortex.eye.itinerary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.f1.base.Message;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.povo.db.DbRequestMessage;
import com.f1.povo.db.DbResultMessage;
import com.f1.utils.AH;
import com.f1.utils.ArchiveFileReader;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.LocalToolkit;
import com.f1.utils.PropertiesHelper;
import com.f1.utils.SH;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentFile;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileSearchRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileSearchResponse;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunOsCommandRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunOsCommandResponse;
import com.f1.vortexcommon.msg.eye.VortexBuildProcedure;
import com.f1.vortexcommon.msg.eye.VortexBuildResult;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunBuildProcedureRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunBuildProcedureResponse;
import com.vortex.agent.VortexAgentUtils;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.VortexEyeUtils;
import com.vortex.eye.messages.VortexVaultRequest;
import com.vortex.eye.messages.VortexVaultResponse;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;
import com.vortex.eye.state.VortexEyeMachineState;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeRunBuildProcedureItinerary extends AbstractVortexEyeItinerary<VortexEyeRunBuildProcedureRequest> {
	private static final Logger log = LH.get(VortexEyeRunBuildProcedureItinerary.class);

	private static final int MAX_STD_SIZE = 1024;
	private static final int DEFAULT_MAX_DATA = 1024 * 1024 * 100;//one hundred megs.
	private static final long DEFAULT_MAX_RUNTIME = TimeUnit.MINUTES.toMillis(60);//one hour

	private static final int STEP1_INSERT_BUILD_RESULT_BEFORE_BUILD = 1;
	private static final int STEP2_SENT_COMMAND_TO_AGENT = 2;
	private static final int STEP3_INSERT_TO_VAULT_AFTER_BUILD = 3;
	private static final int STEP4_INSERT_BUILD_RESULT_AFTER_BUILD = 4;
	private static final int STEP5_GET_FILE_FROM_AGENT = 5;
	private static final int STEP6_INSERT_TO_VORTEX_VAULT_AFTER_FILE_GET = 6;
	private static final int STEP7_INSERT_BUILD_RESULT_AFTER_FILE_GET = 7;

	static final int MAX_LENGTH = 1024;

	private VortexEyeRunBuildProcedureResponse r;
	private VortexBuildResult buildResult;
	private int step;

	@Override
	public byte startJourney(VortexEyeItineraryWorker worker) {
		VortexEyeState state = getState();
		RequestMessage<VortexEyeRunBuildProcedureRequest> action = getInitialRequest();
		r = state.nw(VortexEyeRunBuildProcedureResponse.class);
		final long now = getTools().getNow();
		final VortexEyeRunBuildProcedureRequest request = action.getAction();
		final VortexBuildProcedure buildProcedure = state.getBuildProcedure(request.getBuildProcedureId());
		if (buildProcedure == null) {
			r.setMessage("build procedure not found: " + request.getBuildProcedureId());
			return STATUS_COMPLETE;
		}
		final String buildMachineUid = buildProcedure.getBuildMachineUid();
		VortexEyeMachineState machine = state.getMachineByMuidNoThrow(buildMachineUid);
		if (machine == null) {
			r.setMessage("build machine not found: " + buildMachineUid);
			return STATUS_COMPLETE;
		}
		if (machine.getAgentState() == null) {
			r.setMessage("build machine not available: " + machine.getRemoteHost() + " (uid=" + buildMachineUid + ")");
			return STATUS_COMPLETE;
		}

		buildResult = state.nw(VortexBuildResult.class);
		buildResult.setMetadata(request.getMetadata());
		if (!VortexEyeManageMetadataFieldItinerary.validateMetadata(buildResult, state, r))
			return STATUS_COMPLETE;

		final Map<String, String> variables = request.getBuildProcedureVariables();
		final LocalToolkit tk = new LocalToolkit();
		final Set<String> missingSink = new HashSet<String>();
		final String command = replaceVariables(buildProcedure.getTemplateCommand(), variables, missingSink, tk);
		final String owner = replaceVariables(buildProcedure.getTemplateUser(), variables, missingSink, tk);
		final String stdin = replaceVariables(buildProcedure.getTemplateStdin(), variables, missingSink, tk);
		final String filename = replaceVariables(buildProcedure.getTemplateResultFile(), variables, missingSink, tk);
		final String vfilename = replaceVariables(buildProcedure.getTemplateResultVerifyFile(), variables, missingSink, tk);
		final String name = replaceVariables(buildProcedure.getTemplateResultName(), variables, missingSink, tk);
		final String version = replaceVariables(buildProcedure.getTemplateResultVersion(), variables, missingSink, tk);
		if (missingSink.size() > 0) {
			r.setMessage("The following variables are missing: " + SH.join(',', missingSink));
			return STATUS_COMPLETE;
		}

		buildResult.setBuildVariables(variables);
		buildResult.setInvokedBy(request.getInvokedBy());
		buildResult.setProcedureId(buildProcedure.getId());
		buildResult.setProcedureRevision(buildProcedure.getRevision());
		buildResult.setProcedureName(buildProcedure.getName());
		buildResult.setData(null);
		buildResult.setVerifyData(null);
		buildResult.setDataLength(-1L);
		buildResult.setVerifyDataLength(-1L);
		buildResult.setBuildStderrLength(-1L);
		buildResult.setBuildStdoutLength(-1L);
		buildResult.setName(buildProcedure.getTemplateResultName());
		buildResult.setNow(now);
		buildResult.setStartTime(now);
		buildResult.setState(VortexBuildResult.STATE_RUNNING);
		buildResult.setBuildCommand(command);
		buildResult.setBuildStdin(stdin);
		buildResult.setBuildUser(owner);
		buildResult.setFile(filename);
		buildResult.setVerifyFile(vfilename);
		buildResult.setName(name);
		buildResult.setVersion(version);
		buildResult.setBuildMachineUid(buildProcedure.getBuildMachineUid());
		buildResult.setId(getState().createNextId());
		//TODO:increment revision
		insertBuildResult(buildResult, worker, this);
		this.step = STEP1_INSERT_BUILD_RESULT_BEFORE_BUILD;
		return STATUS_ACTIVE;
	}

	@Override
	public byte onResponse(ResultMessage<?> result, VortexEyeItineraryWorker worker) {
		for (;;) {
			switch (step) {
				case STEP1_INSERT_BUILD_RESULT_BEFORE_BUILD: {
					DbResultMessage dbresult = (DbResultMessage) result.getAction();
					if (!dbresult.getOk()) {
						r.setMessage(dbresult.getMessage());
						return STATUS_COMPLETE;
					}
					sendBuildResultToClient(worker, null, this.buildResult);
					VortexEyeMachineState machine = getState().getMachineByMuidNoThrow(buildResult.getBuildMachineUid());

					//TODO: handle null machine (closed during db call)
					//let clients know of the new build result

					//send build command to the agent
					final VortexAgentRunOsCommandRequest req = getState().nw(VortexAgentRunOsCommandRequest.class);
					req.setCommand(buildResult.getBuildCommand());
					req.setMaxCaptureStderr(DEFAULT_MAX_DATA);
					req.setMaxCaptureStdout(DEFAULT_MAX_DATA);
					req.setMaxRuntimeMs(DEFAULT_MAX_RUNTIME);
					req.setInvokedBy(getInitialRequest().getAction().getInvokedBy());
					req.setOwner(buildResult.getBuildUser());
					req.setStdin(buildResult.getBuildStdin().getBytes());
					req.setTargetAgentProcessUid(machine.getAgentState().getProcessUid());
					worker.sendRequestToAgent(this, req, machine.getAgentState().getProcessUid());
					step = STEP2_SENT_COMMAND_TO_AGENT;
					return STATUS_ACTIVE;
				}
				case STEP2_SENT_COMMAND_TO_AGENT: {
					VortexEyeState state = getState();
					VortexAgentRunOsCommandResponse res = (VortexAgentRunOsCommandResponse) result.getAction();
					if (!res.getOk()) {
						r.setMessage(res.getMessage());
						return STATUS_COMPLETE;
					}
					VortexBuildResult buildResult = this.buildResult.clone();
					VortexVaultRequest vvq = getTools().nw(VortexVaultRequest.class);
					if (result.getError() != null) {
						buildResult.setBuildStderr(("Error message from vortex agent: " + result.getError()).getBytes());
						buildResult.setBuildStderrLength(buildResult.getBuildStderr().length);
						buildResult.setState(VortexBuildResult.STATE_ERROR);
					} else if (!res.getOk()) {
						if (SH.is(res.getMessage())) {
							buildResult.setBuildStderr(("Error message from vortex agent: " + res.getMessage()).getBytes());
							buildResult.setBuildStderrLength(buildResult.getBuildStderr().length);
						}
						buildResult.setState(VortexBuildResult.STATE_ERROR);
					} else {
						buildResult.setBuildExitcode(res.getExitcode());
						buildResult.setState(buildResult.getBuildExitcode() == 0 ? VortexBuildResult.STATE_TRANSFERRING : VortexBuildResult.STATE_FAILURE);
						buildResult.setStartTime(res.getStartTime());
						buildResult.setBuildStderr(res.getStderr());
						buildResult.setBuildStderrLength(res.getStderrLength());
						buildResult.setBuildStdout(res.getStdout());
						buildResult.setBuildStdoutLength(res.getStdoutLength());
					}

					buildResult.setRevision(buildResult.getRevision() + 1);
					buildResult.setNow(getTools().getNow());
					buildResult.setBuildStderrVvid(VortexEyeUtils.addToVortexVaultRequest(buildResult.getBuildStderr(), vvq, MAX_LENGTH, getState()));
					buildResult.setBuildStdoutVvid(VortexEyeUtils.addToVortexVaultRequest(buildResult.getBuildStdout(), vvq, MAX_LENGTH, getState()));

					//free up memory if stdout / stderror are too large
					buildResult.setBuildStderr(trim(buildResult.getBuildStderr()));
					buildResult.setBuildStdout(trim(buildResult.getBuildStdout()));

					step = STEP3_INSERT_TO_VAULT_AFTER_BUILD;//let it loop around 
					sendBuildResultToClient(worker, this.buildResult, buildResult);
					this.buildResult = buildResult;
					if (CH.isntEmpty(vvq.getDataToStore())) {
						worker.sendToVault(this, vvq);
						return STATUS_ACTIVE;
					}
					break;
				}
				case STEP3_INSERT_TO_VAULT_AFTER_BUILD: {
					if (result.getAction() instanceof VortexVaultResponse) {
						VortexVaultResponse vvr = (VortexVaultResponse) result.getAction();
						if (!vvr.getOk()) {
							r.setMessage(vvr.getMessage());
							VortexBuildResult buildResult = this.buildResult.clone();
							buildResult.setState(VortexBuildResult.STATE_ERROR);
							this.buildResult = buildResult;
							sendBuildResultToClient(worker, this.buildResult, buildResult);
							LH.warning(log, "Error occured inserting package into the vault for buildResult: ", buildResult, ", message: ", vvr.getMessage());
							return STATUS_COMPLETE;
						}
					}

					insertBuildResult(buildResult, worker, this);
					step = STEP4_INSERT_BUILD_RESULT_AFTER_BUILD;//let it loop around
					return STATUS_ACTIVE;
				}
				case STEP4_INSERT_BUILD_RESULT_AFTER_BUILD: {
					DbResultMessage dbresult = (DbResultMessage) result.getAction();
					if (!dbresult.getOk()) {
						r.setMessage(dbresult.getMessage());
						VortexBuildResult buildResult = this.buildResult.clone();
						buildResult.setState(VortexBuildResult.STATE_ERROR);
						this.buildResult = buildResult;
						sendBuildResultToClient(worker, this.buildResult, buildResult);
						LH.warning(log, "Error occured updated db for buildResult: ", buildResult, ", message: ", dbresult.getMessage());
						return STATUS_COMPLETE;
					}
					String buildMachineUid = buildResult.getBuildMachineUid();
					VortexEyeMachineState machine = getState().getMachineByMuidNoThrow(buildResult.getBuildMachineUid());
					if (buildResult.getState() != VortexBuildResult.STATE_TRANSFERRING) {
						r.setOk(true);
						return STATUS_COMPLETE;
					}
					VortexAgentFileSearchRequest getFileRequest = getState().nw(VortexAgentFileSearchRequest.class);
					getFileRequest.setIncludeChecksumExpression("*");
					getFileRequest.setIncludeDataExpression("*");
					if (SH.is(buildResult.getVerifyFile())) {
						getFileRequest.setRootPaths(CH.l(buildResult.getFile(), buildResult.getVerifyFile()));
					} else
						getFileRequest.setRootPaths(CH.l(buildResult.getFile()));
					getFileRequest.setMaxDataSize(1024 * 1024 * 1000);
					getFileRequest.setSearchExpression(null);
					worker.sendRequestToAgent(this, getFileRequest, machine.getAgentState().getProcessUid());
					step = STEP5_GET_FILE_FROM_AGENT;
					return STATUS_ACTIVE;
				}
				case STEP5_GET_FILE_FROM_AGENT: {
					VortexAgentFileSearchResponse res = (VortexAgentFileSearchResponse) result.getAction();
					byte[] data = null;
					byte[] verifyData = null;
					VortexBuildResult buildResult = this.buildResult.clone();
					List<VortexAgentFile> files = res.getFiles();
					if (files.size() >= 1) {
						VortexAgentUtils.decompressFile(files.get(0));
						data = files.get(0).getData();
					}
					if (files.size() >= 2) {
						VortexAgentUtils.decompressFile(files.get(1));
						verifyData = files.get(1).getData();
					}
					ArchiveFileReader afr = new ArchiveFileReader();
					boolean verifyMissing = false;
					if (verifyData != null) {
						buildResult.setVerifyData(verifyData);
						try {
							long cs = afr.read(files.get(0).getPath(), verifyData).getChecksum();
							buildResult.setVerifyDataChecksum(cs);
						} catch (Exception e) {
							buildResult.setVerifyDataChecksum(0);
						}
						buildResult.setVerifyDataLength(verifyData.length);
					} else if (SH.is(buildResult.getVerifyFile())) {
						verifyMissing = true;
					}
					if (verifyMissing || data == null) {
						buildResult.setVerifyData(null);
						buildResult.setState(VortexBuildResult.STATE_FILE_NOT_FOUND);
					} else {
						buildResult.setData(data);
						try {
							long cs = afr.read(files.get(0).getPath(), data).getChecksum();
							buildResult.setDataChecksum(cs);//files.get(0).getChecksum());
						} catch (Exception e) {
							buildResult.setDataChecksum(0);
						}
						buildResult.setDataLength(data.length);
						buildResult.setState(VortexBuildResult.STATE_SUCCCESS);
					}

					buildResult.setNow(getTools().getNow());
					VortexVaultRequest vvq = getTools().nw(VortexVaultRequest.class);
					buildResult.setDataVvid(VortexEyeUtils.addToVortexVaultRequest(buildResult.getData(), vvq, MAX_LENGTH, getState()));
					buildResult.setVerifyDataVvid(VortexEyeUtils.addToVortexVaultRequest(buildResult.getVerifyData(), vvq, MAX_LENGTH, getState()));
					buildResult.setNow(getTools().getNow());
					buildResult.setRevision(buildResult.getRevision() + 1);
					sendBuildResultToClient(worker, this.buildResult, buildResult);
					this.buildResult = buildResult;
					step = STEP6_INSERT_TO_VORTEX_VAULT_AFTER_FILE_GET;
					if (CH.isntEmpty(vvq.getDataToStore())) {
						worker.sendToVault(this, vvq);
						return STATUS_ACTIVE;
					}
					break;
				}
				case STEP6_INSERT_TO_VORTEX_VAULT_AFTER_FILE_GET: {
					insertBuildResult(buildResult, worker, this);
					step = STEP7_INSERT_BUILD_RESULT_AFTER_FILE_GET;
					return STATUS_ACTIVE;
				}
				case STEP7_INSERT_BUILD_RESULT_AFTER_FILE_GET: {
					DbResultMessage dbresult = (DbResultMessage) result.getAction();
					VortexBuildResult buildResult = this.buildResult.clone();
					if (!dbresult.getOk()) {
						r.setMessage(dbresult.getMessage());
						buildResult.setState(VortexBuildResult.STATE_ERROR);
						LH.warning(log, "Error occured updated db for buildResult: ", buildResult, ", message: ", dbresult.getMessage());
						sendBuildResultToClient(worker, this.buildResult, buildResult);
						this.buildResult = buildResult;
					} else {
						r.setOk(true);
					}
					return STATUS_COMPLETE;
				}
				default:
					throw new RuntimeException("unknown step: " + step);
			}
		}
	}
	@Override
	public Message endJourney(VortexEyeItineraryWorker worker) {
		return r;
	}

	private void sendBuildResultToClient(VortexEyeItineraryWorker worker, VortexBuildResult old, VortexBuildResult nuw) {
		if (AH.length(nuw.getData()) > MAX_LENGTH)
			nuw.setData(AH.subarray(nuw.getData(), 0, MAX_LENGTH));
		if (AH.length(nuw.getVerifyData()) > MAX_LENGTH)
			nuw.setVerifyData(AH.subarray(nuw.getVerifyData(), 0, MAX_LENGTH));
		if (AH.length(nuw.getBuildStdout()) > MAX_LENGTH)
			nuw.setBuildStdout(AH.subarray(nuw.getBuildStdout(), 0, MAX_LENGTH));
		if (AH.length(nuw.getBuildStderr()) > MAX_LENGTH)
			nuw.setBuildStderr(AH.subarray(nuw.getBuildStderr(), 0, MAX_LENGTH));
		getState().addBuildResult(nuw);
		VortexEyeChangesMessageBuilder cmb = getState().getChangesMessageBuilder();
		cmb.writeTransition(old, nuw);
		worker.sendToClients(this, cmb.popToChangesMsg(getState().nextSequenceNumber()));
	}
	private static byte[] trim(byte[] data) {
		if (data == null || data.length < MAX_STD_SIZE)
			return data;
		return Arrays.copyOf(data, MAX_STD_SIZE);
	}

	public static void insertBuildResult(VortexBuildResult buildResult, VortexEyeItineraryWorker worker, VortexEyeItinerary<?> source) {
		boolean active = buildResult.getRevision() < VortexAgentEntity.REVISION_DONE;
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("active", active);
		params.put("id", buildResult.getId());
		params.put("revision", buildResult.getRevision());
		params.put("now", buildResult.getNow());
		params.put("name", buildResult.getName());
		params.put("version", buildResult.getVersion());
		params.put("file", buildResult.getFile());
		params.put("verify_file", buildResult.getVerifyFile());
		params.put("procedure_id", buildResult.getProcedureId());
		params.put("procedure_revision", buildResult.getProcedureRevision());
		params.put("procedure_name", buildResult.getProcedureName());
		params.put("build_machine_uid", buildResult.getBuildMachineUid());
		params.put("build_user", buildResult.getBuildUser());
		params.put("build_command", buildResult.getBuildCommand());
		params.put("build_stdin", buildResult.getBuildStdin());
		params.put("build_variables", source.getState().getPartition().getContainer().getServices().getJsonConverter().objectToString(buildResult.getBuildVariables()));

		params.put("build_stdout", AH.subarrayNoThrow(buildResult.getBuildStdout(), 0, MAX_LENGTH));
		params.put("build_stderr", AH.subarrayNoThrow(buildResult.getBuildStderr(), 0, MAX_LENGTH));
		params.put("data", AH.subarrayNoThrow(buildResult.getData(), 0, MAX_LENGTH));
		params.put("verify_data", AH.subarrayNoThrow(buildResult.getVerifyData(), 0, MAX_LENGTH));

		params.put("build_stdout_length", buildResult.getBuildStdoutLength());
		params.put("build_stderr_length", buildResult.getBuildStderrLength());
		params.put("data_length", buildResult.getDataLength());
		params.put("verify_data_length", buildResult.getVerifyDataLength());

		params.put("build_stdout_vvid", buildResult.getBuildStdoutVvid());
		params.put("build_stderr_vvid", buildResult.getBuildStderrVvid());
		params.put("data_vvid", buildResult.getDataVvid());
		params.put("verify_data_vvid", buildResult.getVerifyDataVvid());

		//params.put("build_stdout", buildResult.getBuildStdoutLength() == AH.length(buildResult.getBuildStdout()) ? buildResult.getBuildStdout() : null);
		//params.put("build_stderr", buildResult.getBuildStderrLength() == AH.length(buildResult.getBuildStderr()) ? buildResult.getBuildStderr() : null);
		//params.put("data", buildResult.getDataLength() == AH.length(buildResult.getData()) ? buildResult.getData() : null);
		//params.put("verify_data", buildResult.getVerifyDataLength() == AH.length(buildResult.getVerifyData()) ? buildResult.getVerifyData() : null);
		params.put("build_exitcode", buildResult.getBuildExitcode());
		params.put("invoked_by", buildResult.getInvokedBy());
		params.put("data", buildResult.getData());
		params.put("data_checksum", buildResult.getDataChecksum());
		params.put("verify_data_checksum", buildResult.getVerifyDataChecksum());
		params.put("start_time", buildResult.getStartTime());
		params.put("state", buildResult.getState());
		params.put("metadata", buildResult.getMetadata() == null ? null : VortexEyeUtils.joinMap(buildResult.getMetadata()));
		final DbRequestMessage msg = source.getState().nw(DbRequestMessage.class);
		msg.setId("insert_build_result");
		msg.setParams(params);
		worker.sendToDb(source, msg);
	}

	public static String replaceVariables(String text, Map<String, String> values, Set<String> missingVariablesSink, LocalToolkit tk) {
		if (SH.isnt(text))
			return "";
		try {
			final ArrayList<String> sink = tk.borrowArrayList(String.class);
			final StringBuilder buf = tk.borrowStringBuilder();
			PropertiesHelper.splitVariables(text, sink, buf);
			SH.clear(buf);
			for (int i = 0; i < sink.size(); i++) {
				if ((i & 1) == 1) //odd entries are variables text
				{
					final String variableName = sink.get(i);
					final String value = values.get(variableName);
					if (value == null) {
						missingVariablesSink.add(variableName);
					} else
						buf.append(value);
				} else
					buf.append(sink.get(i));
			}
			return buf.toString();
		} finally {
			tk.returnAll();
		}
	}
	@Override
	protected void populateAuditEvent(VortexEyeRunBuildProcedureRequest action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink) {
		sink.setEventType(VortexEyeClientEvent.TYPE_RUN_BUILD_PROCEDURE);
		sink.getParams().put("BPID", SH.toString(action.getBuildProcedureId()));
		auditMap(sink, "VARS", action.getBuildProcedureVariables());
		auditMap(sink, "META", action.getMetadata());
	}
}
