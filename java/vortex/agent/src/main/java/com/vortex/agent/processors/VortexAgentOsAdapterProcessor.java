package com.vortex.agent.processors;

import java.util.ArrayList;
import java.util.List;

import com.f1.container.PartitionResolver;
import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.utils.LH;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileDeleteRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileDeleteResponse;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileSearchRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileSearchResponse;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunDeploymentResponse;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunOsCommandRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunOsCommandResponse;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunSignalProcessRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunSignalProcessResponse;
import com.vortex.agent.messages.VortexAgentOsAdapterDeploymentRequest;
import com.vortex.agent.messages.VortexAgentOsAdapterRequest;
import com.vortex.agent.messages.VortexAgentOsAdapterResponse;
import com.vortex.agent.state.VortexAgentOsAdapterManager;
import com.vortex.agent.state.VortexAgentOsAdapterState;

public class VortexAgentOsAdapterProcessor extends BasicRequestProcessor<VortexAgentOsAdapterRequest, VortexAgentOsAdapterState, VortexAgentOsAdapterResponse> implements
		PartitionResolver<RequestMessage<VortexAgentOsAdapterRequest>> {

	public VortexAgentOsAdapterProcessor() {
		super(VortexAgentOsAdapterRequest.class, VortexAgentOsAdapterState.class, VortexAgentOsAdapterResponse.class);
		setPartitionResolver(this);
	}

	@Override
	protected VortexAgentOsAdapterResponse processRequest(RequestMessage<VortexAgentOsAdapterRequest> action, VortexAgentOsAdapterState state, ThreadScope threadScope)
			throws Exception {
		final VortexAgentOsAdapterRequest request = action.getAction();
		final int commandType = request.getCommandType();
		final VortexAgentOsAdapterResponse response = nw(VortexAgentOsAdapterResponse.class);
		final VortexAgentOsAdapterManager manager = state.getManager();

		List<VortexAgentEntity> entities = new ArrayList<VortexAgentEntity>();
		try {
			switch (commandType) {
				case VortexAgentOsAdapterRequest.INSPECT_PROCESSES:
					if (manager.getInspectProcesses() != null)
						entities.addAll(manager.getInspectProcesses().inspectProcesses(state));
					break;
				case VortexAgentOsAdapterRequest.INSPECT_MACHINE:
					if (manager.getInspectMachine() != null)
						entities.add(manager.getInspectMachine().inspectMachine(state));
					break;
				case VortexAgentOsAdapterRequest.INSPECT_FILESYSTEMS:
					if (manager.getInspectFileSystems() != null)
						entities.addAll(manager.getInspectFileSystems().inspectFileSystems(state));
					break;
				case VortexAgentOsAdapterRequest.INSPECT_NET_CONNECTIONS:
					if (manager.getInspectNetConnections() != null)
						entities.addAll(manager.getInspectNetConnections().inspectNetConnections(state));
					break;
				case VortexAgentOsAdapterRequest.INSPECT_NET_LINKS:
					if (manager.getInspectNetLinks() != null)
						entities.addAll(manager.getInspectNetLinks().inspectNetLinks(state));
					break;
				case VortexAgentOsAdapterRequest.INSPECT_NET_ADDRESSES:
					if (manager.getInspectNetAddresses() != null)
						entities.addAll(manager.getInspectNetAddresses().inspectNetAddresses(state));
					break;
				case VortexAgentOsAdapterRequest.INSPECT_CRON:
					if (manager.getInspectCron() != null)
						entities.addAll(manager.getInspectCron().inspectCron(state));
					break;
				case VortexAgentOsAdapterRequest.INSPECT_AGENT_MACHINE_EVENTS:
					if (manager.getInspectMachineEvents() != null)
						entities.addAll(manager.getInspectMachineEvents().inspectMachineEvents(state, getTools().getNow(), (byte) 0));//TODO: fix arguments
					break;
				case VortexAgentOsAdapterRequest.DELETE_FILE: {
					VortexAgentFileDeleteResponse result = manager.getFileDeleter().deleteFiles((VortexAgentFileDeleteRequest) request.getRequestMessage(), state);
					response.setResponseMessage(result);
					break;
				}
				case VortexAgentOsAdapterRequest.RUN_COMMAND: {
					if (manager.getCommandRunner() != null) {
						VortexAgentRunOsCommandResponse result = manager.getCommandRunner().runCommand((VortexAgentRunOsCommandRequest) request.getRequestMessage(), state);
						response.setResponseMessage(result);
					}
					break;
				}
				case VortexAgentOsAdapterRequest.RUN_DEPLOYMENT: {
					if (manager.getDeploymentRunner() != null) {
						VortexAgentRunDeploymentResponse result = manager.getDeploymentRunner().runDeployment((VortexAgentOsAdapterDeploymentRequest) request.getRequestMessage(),
								state);
						response.setResponseMessage(result);
					}
					break;
				}
				case VortexAgentOsAdapterRequest.FILE_SEARCH: {
					if (manager.getFileSearcher() != null) {
						VortexAgentFileSearchResponse result = manager.getFileSearcher().searchFiles((VortexAgentFileSearchRequest) request.getRequestMessage(), state);
						response.setResponseMessage(result);
					}
					break;
				}
				case VortexAgentOsAdapterRequest.SEND_SIGNAL: {
					if (manager.getSendSignal() != null) {
						VortexAgentRunSignalProcessResponse result = manager.getSendSignal().sendSignal((VortexAgentRunSignalProcessRequest) request.getRequestMessage(), state);
						response.setResponseMessage(result);
					}
					break;
				}
				default:
					throw new RuntimeException("unknown command type: " + commandType);
			}
		} catch (Exception e) {
			LH.warning(log, "Error running command: ", commandType, e);
		}
		response.setCommandType(commandType);
		response.setEntities(entities);
		return response;
	}

	@Override
	public Object getPartitionId(RequestMessage<VortexAgentOsAdapterRequest> action) {
		return action.getAction().getPartitionId();
	}
}
