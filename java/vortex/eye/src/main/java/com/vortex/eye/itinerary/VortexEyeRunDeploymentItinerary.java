package com.vortex.eye.itinerary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.base.Message;
import com.f1.bootstrap.F1Constants;
import com.f1.container.ResultMessage;
import com.f1.utils.CH;
import com.f1.utils.LocalToolkit;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.PropertiesHelper;
import com.f1.utils.SH;
import com.f1.utils.ToDoException;
import com.f1.utils.structs.Tuple2;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunDeploymentRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunDeploymentResponse;
import com.f1.vortexcommon.msg.eye.VortexBuildResult;
import com.f1.vortexcommon.msg.eye.VortexDeployment;
import com.f1.vortexcommon.msg.eye.VortexDeploymentSet;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunDeploymentRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunDeploymentResponse;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.messages.VortexVaultRequest;
import com.vortex.eye.messages.VortexVaultResponse;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;
import com.vortex.eye.processors.agent.VortexEyeAgentDeploymentChangesProcessor;
import com.vortex.eye.state.VortexEyeMachineState;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeRunDeploymentItinerary extends AbstractVortexEyeItinerary<VortexEyeRunDeploymentRequest> {

	private static final byte STEP2_GET_BUILD_RESULTS_DATA = 2;
	private static final byte STEP3_SEND_DEPLOYMENT_TO_AGENT = 3;
	private VortexEyeRunDeploymentResponse r;
	private VortexBuildResult buildResult;
	private VortexDeployment deployment;
	private byte step;

	@Override
	public byte startJourney(VortexEyeItineraryWorker worker) {
		VortexEyeState state = getState();
		r = getState().nw(VortexEyeRunDeploymentResponse.class);
		final long now = getTools().getNow();
		final VortexEyeRunDeploymentRequest request = getInitialRequest().getAction();
		final Tuple2<VortexDeployment, Object> tuple = null;//state.getDeployment(request.getDeploymentId());
		this.deployment = getState().getDeployment(request.getDeploymentId());
		if (deployment == null) {
			r.setMessage("deployment not found: " + request.getDeploymentId());
			return STATUS_COMPLETE;
		}
		final String buildMachineUid = deployment.getTargetMachineUid();
		final VortexEyeMachineState machine = getState().getMachineByMuidNoThrow(buildMachineUid);
		if (machine == null || machine.getAgentState() == null) {
			r.setMessage("target machine not available: " + buildMachineUid);
			return STATUS_COMPLETE;
		}
		if (machine.getAgentState().getProcessUid() == null) {
			r.setMessage("build machine not available: " + machine.getRemoteHost() + " (uid=" + buildMachineUid + ")");
			return STATUS_COMPLETE;
		}

		final long buildResultId = request.getBuildResultId();
		this.buildResult = state.getBuildResult(buildResultId);
		if (buildResult == null) {
			r.setMessage("build result not available: " + buildResultId);
			return STATUS_COMPLETE;
		}
		if (buildResult.getState() != VortexBuildResult.STATE_SUCCCESS) {
			r.setMessage("build result not in success state: " + buildResultId);
			return STATUS_COMPLETE;
		}

		final VortexDeploymentSet deploymentSet = state.getDeploymentSet(deployment.getDeploymentSetId());
		if (deploymentSet == null) {
			r.setMessage("deployment set not found: " + deploymentSet);
			return STATUS_COMPLETE;
		}

		//get build results

		byte type = getInitialRequest().getAction().getType();
		if (type != VortexEyeRunDeploymentRequest.TYPE_VERIFY) {
			VortexDeployment deploymentOrig = deployment;
			this.deployment = deployment.clone();
			deployment.setStatus(VortexDeployment.STATUS_ACTION_RETRIEVING____);
			deployment.setCurrentBuildInvokedBy(request.getInvokedBy());

			VortexEyeChangesMessageBuilder cmb = getState().getChangesMessageBuilder();
			cmb.writeTransition(deploymentOrig, deployment);
			state.addDeployment(deployment);
			worker.sendToClients(this, cmb.popToChangesMsg(getState().nextSequenceNumber()));
			VortexEyeAgentDeploymentChangesProcessor.insertDeploymentStatus(deployment, getTools());
		}

		//DbRequestMessage dbMsg = getState().nw(DbRequestMessage.class);
		//final Map<Object, Object> params = new HashMap<Object, Object>();
		//HashSet<Long> ids = new HashSet<Long>();
		//ids.add(buildResult.getId());
		//params.put("ids", ids);
		//switch (getInitialRequest().getAction().getType()) {
		//case VortexEyeRunDeploymentRequest.TYPE_DEPLOY: {
		//dbMsg.setId("query_build_result_detailed");
		//break;
		//}
		//case VortexEyeRunDeploymentRequest.TYPE_DEPLOY_CONFIG: {
		//dbMsg.setId("query_build_result_detailed");
		//break;
		//}
		//case VortexEyeRunDeploymentRequest.TYPE_VERIFY: {
		//dbMsg.setId("query_build_result_detailed");
		//break;
		//}
		//}
		//dbMsg.setParams(params);
		//dbMsg.setType(DbRequestMessage.TYPE_QUERY_TO_VALUED);
		//dbMsg.setResultValuedClass(VortexBuildResult.class);

		VortexVaultRequest vvq = getState().nw(VortexVaultRequest.class);

		Set<Long> ids = new HashSet<Long>();
		if (type == VortexEyeRunDeploymentRequest.TYPE_DEPLOY && buildResult.getDataLength() > VortexEyeRunBuildProcedureItinerary.MAX_LENGTH)
			ids.add(buildResult.getDataVvid());
		if (type == VortexEyeRunDeploymentRequest.TYPE_VERIFY && buildResult.getVerifyDataLength() > VortexEyeRunBuildProcedureItinerary.MAX_LENGTH)
			ids.add(buildResult.getVerifyDataVvid());

		vvq.setVvidsToRetrieve(ids);
		//worker.sendToDb(this, dbMsg);
		worker.sendToVault(this, vvq);
		step = STEP2_GET_BUILD_RESULTS_DATA;
		return STATUS_ACTIVE;

	}
	@Override
	public byte onResponse(ResultMessage<?> result, VortexEyeItineraryWorker worker) {
		VortexEyeRunDeploymentRequest request = getInitialRequest().getAction();
		switch (step) {
			case STEP2_GET_BUILD_RESULTS_DATA: {
				VortexVaultResponse vvresult = (VortexVaultResponse) result.getAction();
				//DbResultMessage dbresult = (DbResultMessage) result.getAction();

				//up front validations...
				if (!vvresult.getOk()) {
					r.setMessage(vvresult.getMessage());
					return STATUS_COMPLETE;
				}
				final String buildMachineUid = deployment.getTargetMachineUid();
				final VortexEyeMachineState machine = getState().getMachineByMuidNoThrow(buildMachineUid);
				if (machine == null || machine.getAgentState() == null) {
					r.setMessage("target machine not available: " + buildMachineUid);
					return STATUS_COMPLETE;
				}
				final VortexDeploymentSet deploymentSet = getState().getDeploymentSet(deployment.getDeploymentSetId());
				if (deploymentSet == null) {
					r.setMessage("deployment set not found: " + deploymentSet);
					return STATUS_COMPLETE;
				}

				//get data from db response
				byte[] buildResultData = buildResult.getData();
				byte[] buildResultVerifyData = buildResult.getVerifyData();

				if (CH.isntEmpty(vvresult.getData())) {
					for (Entry<Long, byte[]> i : vvresult.getData().entrySet()) {
						if (i.getKey() == buildResult.getDataVvid()) {
							buildResultData = i.getValue();
						} else if (i.getKey() == buildResult.getVerifyDataVvid()) {
							buildResultVerifyData = i.getValue();
						}
					}
				}
				//for (Valued value : vvresult.getResultsValued()) {
				//VortexBuildResult buildResult = (VortexBuildResult) value;
				//if (AH.length(buildResult.getData()) == buildResult.getDataLength())
				//buildResultData = buildResult.getData();
				//if (AH.length(buildResult.getVerifyData()) == buildResult.getVerifyDataLength())
				//buildResultVerifyData = buildResult.getVerifyData();
				//}

				//prepare agent request basics...
				final VortexAgentRunDeploymentRequest toAgentRequest = getState().nw(VortexAgentRunDeploymentRequest.class);
				toAgentRequest.setDeploymentId(deployment.getId());
				toAgentRequest.setInvokedBy(request.getInvokedBy());
				toAgentRequest.setTargetAgentProcessUid(machine.getAgentState().getProcessUid());
				toAgentRequest.setGeneratedPropertiesFile(deployment.getGeneratedPropertiesFile());
				toAgentRequest.setBuildResultId(buildResult.getId());
				toAgentRequest.setJobId(buildResult.getId());

				switch (getInitialRequest().getAction().getType()) {
					case VortexEyeRunDeploymentRequest.TYPE_DEPLOY: {
						toAgentRequest.setCommandType(VortexAgentRunDeploymentRequest.TYPE_DEPLOY);
						toAgentRequest.setData(buildResultData);
						toAgentRequest.setDataFileName(buildResult.getFile());
						if (buildResultData == null) {
							r.setMessage("Build result Data not found in db");
							return STATUS_COMPLETE;
						}
						final Map<String, String> variables = request.getDeploymentVariables();
						final LocalToolkit tk = new LocalToolkit();
						final Set<String> missingSink = new HashSet<String>();
						//Map<String, StringBuilder> propertyFiles = new HashMap<String, StringBuilder>();
						//StringBuilder properties = new StringBuilder();

						String text = "";

						final Map<String, String> generatedFiles = new HashMap<String, String>();
						if (CH.isntEmpty(deployment.getGeneratedFiles())) {
							generatedFiles.putAll(deployment.getGeneratedFiles());
						} else {
							if (SH.is(deployment.getProperties()))
								generatedFiles.put(deployment.getGeneratedPropertiesFile(), deployment.getProperties());
						}
						//text += "## Files generated " + CH.size(deployment.getGeneratedFiles()) + "\n";
						//if (CH.isntEmpty(deployment.getGeneratedFiles())) {//TODO:HACK
						//for (String e : deployment.getGeneratedFiles().values()) {
						//text += replaceVariables(e, variables, missingSink, tk) + SH.NEWLINE;
						//}
						//}
						//if (SH.is(text))
						//parseProperties(text, propertyFiles);
						//
						//properties.append(SH.NEWLINE);
						//properties.append(SH.NEWLINE);

						//check for namespace directives, if any exist we need to close it out
						//boolean needsNamespaceClosed = false;
						//for (String line : SH.splitLines(properties.toString())) {
						//if (line.startsWith("[")) {
						//needsNamespaceClosed = true;
						//break;
						//}
						//}
						//if (needsNamespaceClosed) {
						//properties.append("[]").append(SH.NEWLINE);
						//}
						StringBuilder autogenProperties = new StringBuilder();
						autogenProperties.append(SH.NEWLINE);
						autogenProperties.append("## Properties auto-generated by Vortex deployment process ");
						autogenProperties.append(SH.NEWLINE);
						long deployedInstanceId = getState().createNextId();
						final String ts = getTools().getServices().getLocaleFormatter().getDateFormatter(LocaleFormatter.DATETIME_LONG_FULL).format(getTools().getNow());
						autogenProperties.append(F1Constants.PROPERTY_DEPLOYMENT_INVOKED_BY).append('=').append(request.getInvokedBy()).append(SH.NEWLINE);
						autogenProperties.append(F1Constants.PROPERTY_DEPLOYMENT_TIMESTAMP).append('=').append(ts).append(SH.NEWLINE);
						autogenProperties.append(F1Constants.PROPERTY_DEPLOYMENT_INSTANCE_ID).append("=DI-").append(deployedInstanceId).append(SH.NEWLINE);
						autogenProperties.append(F1Constants.PROPERTY_DEPLOYMENT_ID).append("=DP-").append(deployment.getId()).append("").append(SH.NEWLINE);
						autogenProperties.append(F1Constants.PROPERTY_DEPLOYMENT_SETID).append("=DS-").append(deploymentSet.getId()).append("").append(SH.NEWLINE);
						autogenProperties.append(F1Constants.PROPERTY_DEPLOYMENT_NAME).append("=").append(SH.noNull(buildResult.getName())).append("").append(SH.NEWLINE);
						autogenProperties.append(F1Constants.PROPERTY_DEPLOYMENT_VERSION).append("=").append(SH.noNull(buildResult.getVersion())).append("").append(SH.NEWLINE);
						autogenProperties.append(F1Constants.PROPERTY_DEPLOYMENT_DESCRIPTION);
						autogenProperties.append("=${").append(F1Constants.PROPERTY_DEPLOYMENT_ID).append("} ${").append(F1Constants.PROPERTY_DEPLOYMENT_NAME).append("} ${")
								.append(F1Constants.PROPERTY_DEPLOYMENT_VERSION).append("}");
						autogenProperties.append(SH.NEWLINE);
						autogenProperties.append(SH.NEWLINE);

						if (SH.is(deploymentSet.getProperties()) && SH.is(deploymentSet.getProperties())) {
							autogenProperties.append("## Properties from deployment set ").append(deploymentSet.getName());
							autogenProperties.append(SH.NEWLINE);
							autogenProperties.append(SH.NEWLINE);
							autogenProperties.append(deploymentSet.getProperties());
							//String text = replaceVariables(deploymentSet.getProperties(), variables, missingSink, tk);
							autogenProperties.append(text);
							autogenProperties.append(SH.NEWLINE);
							autogenProperties.append(SH.NEWLINE);
							//parseProperties(text, propertyFiles);
						}
						String appendToAutogen = generatedFiles.remove(deployment.getGeneratedPropertiesFile());
						if (SH.is(appendToAutogen)) {
							autogenProperties.append("## From Generated Files");
							autogenProperties.append(SH.NEWLINE);
							autogenProperties.append(SH.NEWLINE);
							autogenProperties.append(appendToAutogen);
							autogenProperties.append(SH.NEWLINE);
							autogenProperties.append(SH.NEWLINE);
						}

						autogenProperties.append("## End of file");
						autogenProperties.append(SH.NEWLINE);

						//toAgentRequest.setProperties(properties + autogenProperties.toString());
						toAgentRequest.setDeployedInstanceId(deployedInstanceId);
						//parseProperties(SH.NEWLINE + autogenProperties.toString(), propertyFiles);
						Map<String, String> propertyFiles2 = new HashMap<String, String>(generatedFiles.size() + 1);
						propertyFiles2.put(SH.isnt(deployment.getGeneratedPropertiesFile()) ? ".deployment.properties" : deployment.getGeneratedPropertiesFile(),
								autogenProperties.toString());
						propertyFiles2.putAll(generatedFiles);
						//for (Entry<String, StringBuilder> e : propertyFiles.entrySet())
						//propertyFiles2.put(e.getKey(), e.getValue().toString());
						toAgentRequest.setPropertyFiles(propertyFiles2);
						break;
					}
					case VortexEyeRunDeploymentRequest.TYPE_VERIFY: {
						toAgentRequest.setCommandType(VortexAgentRunDeploymentRequest.TYPE_VERIFY);
						toAgentRequest.setVerifyData(buildResultVerifyData);
						toAgentRequest.setVerifyDataFileName(buildResult.getVerifyFile());
						break;
					}
					case VortexEyeRunDeploymentRequest.TYPE_DEPLOY_CONFIG: {
						toAgentRequest.setCommandType(VortexAgentRunDeploymentRequest.TYPE_DEPLOY_CONFIG);
						ToDoException.throwNow("Deploy config");
						break;
					}
				}

				worker.sendRequestToAgent(this, toAgentRequest, machine.getAgentState().getProcessUid());
				step = STEP3_SEND_DEPLOYMENT_TO_AGENT;
				return STATUS_ACTIVE;
			}
			case STEP3_SEND_DEPLOYMENT_TO_AGENT:
				VortexAgentRunDeploymentResponse res = (VortexAgentRunDeploymentResponse) result.getAction();
				r.setOk(res.getOk());
				r.setMessage(res.getMessage());
				r.setVerifyExitCode(res.getVerifyExitCode());
				r.setVerifyStderr(res.getVerifyStderr());
				r.setVerifyStdout(res.getVerifyStdout());
				r.setInstallExitCode(res.getInstallExitCode());
				r.setInstallStderr(res.getInstallStderr());
				r.setInstallStdout(res.getInstallStdout());
				r.setUninstallExitCode(res.getUninstallExitCode());
				r.setUninstallStderr(res.getUninstallStderr());
				r.setUninstallStdout(res.getUninstallStdout());
				return STATUS_COMPLETE;
			default:
				throw new RuntimeException("unknown step: " + step);
		}

		//TODO: Send to guis
		//final TestTrackDeltas revision = nw(TestTrackDeltas.class);
		//revision.setRevisions(CH.l((AgentRevision) buildResult));
		//agentRevisionPort.send(revision, threadScope);

		// TODO Auto-generated method stub
	}
	private void parseProperties(String properties, Map<String, StringBuilder> sink) {
		if (SH.isnt(properties))
			return;
		String currentFile = deployment.getGeneratedPropertiesFile();
		StringBuilder sb = sink.get(currentFile);
		if (sb == null)
			sink.put(currentFile, sb = new StringBuilder());
		String lines[] = SH.splitLines(properties);
		for (String line : lines) {
			if (SH.startsWith(line, '>')) {
				if (line.startsWith(">>"))//escape
					sb.append(line, 1, line.length()).append(SH.NEWLINE);
				else {
					currentFile = line.substring(1);
					sb = sink.get(currentFile);
					if (sb == null)
						sink.put(currentFile, sb = new StringBuilder());
				}

			} else {
				sb.append(line).append(SH.NEWLINE);
			}
		}
	}
	@Override
	public Message endJourney(VortexEyeItineraryWorker worker) {
		return r;
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
	protected void populateAuditEvent(VortexEyeRunDeploymentRequest action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink) {
		switch (action.getType()) {
			case VortexEyeRunDeploymentRequest.TYPE_DEPLOY:
				sink.setEventType(VortexEyeClientEvent.TYPE_RUN_DEPLOYMENT);
				break;
			case VortexEyeRunDeploymentRequest.TYPE_DEPLOY_CONFIG:
				sink.setEventType(VortexEyeClientEvent.TYPE_DEPLOYMENT_CONFIG);
				break;
			case VortexEyeRunDeploymentRequest.TYPE_VERIFY:
				sink.setEventType(VortexEyeClientEvent.TYPE_DEPLOYMENT_VERIFY);
				break;
		}
		sink.getParams().put("BRID", SH.toString(action.getBuildResultId()));
		sink.getParams().put("DPID", SH.toString(action.getDeploymentId()));
		auditMap(sink, "VARS", action.getDeploymentVariables());
	}
}
