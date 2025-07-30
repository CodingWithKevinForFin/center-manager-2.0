package com.f1.ami.center.triggers;

import java.util.List;
import java.util.Map;

import com.f1.ami.amicommon.msg.AmiCenterPassToRelayRequest;
import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandRequest;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTable;
import com.f1.container.RequestMessage;

/**
 * Represents a command request from the subscriber (usually front end)
 * 
 */
public class AmiCommandRequest {

	private AmiRelayRunAmiCommandRequest commandRequest;
	private RequestMessage<AmiCenterPassToRelayRequest> initialRequest;

	public AmiCommandRequest(RequestMessage<AmiCenterPassToRelayRequest> initialRequest, AmiRelayRunAmiCommandRequest commandRequest) {
		this.initialRequest = initialRequest;
		this.commandRequest = commandRequest;
	}

	/**
	 * @return If this target is destined for a particular relay, return the uid of the relay
	 */
	public String getTargetAgentProcessUid() {
		return commandRequest.getTargetAgentProcessUid();
	}

	/**
	 * @return The arguments passed into the command
	 */
	public Map<String, Object> getArguments() {
		return commandRequest.getArguments();
	}

	/**
	 * @return The id of the subscriber name (for front end this is the username supplied at the login page). This will be the same as {@link #getUserId()}, except when there is an
	 *         intermediate party handling and forwarding the request (in which case the intermediate party id will be returned here).
	 */
	public String getInvokedBy() {
		return commandRequest.getInvokedBy();
	}

	/**
	 * @return The id of the command, used for distinguishing different types of command. Generally, all commands should have unique ids, and this responsibility is left to the
	 *         architect of command work flows.
	 */
	public String getCommandDefinitionId() {
		return commandRequest.getCommandDefinitionId();
	}

	/**
	 * @return If the user selected multiple rows in a table when running the command, this represents those fields.
	 */
	public List<Map<String, Object>> getFields() {
		return commandRequest.getFields();
	}

	/**
	 * @return The ip of the caller. For frontend, this is ip address of the browser as recorded by the webserver.
	 */
	public String getHostIp() {
		return commandRequest.getHostIp();
	}

	/**
	 * @return Amount of time in milliseconds (as specified by caller) before the command will be timed out by the AMI framework
	 */
	public int getTimeoutMs() {
		return commandRequest.getTimeoutMs();
	}

	/**
	 * @return A GUID assigned by the AMI framework at time of calling that uniquely represents this command execution
	 */
	public String getCommandUid() {
		return commandRequest.getCommandUid();
	}

	/**
	 * In the case that the command is executed over realtime rows (ex: user selects a record in the frontend backed by an {@link AmiRow}) then this is populated with those rows'
	 * owning Table names. See {@link AmiRow#getAmiTable()} and {@link AmiTable#getName()}
	 */
	public String[] getObjectTypes() {
		return commandRequest.getObjectTypes();
	}

	/**
	 * @return The "I" field of the rows, if supported. Mostly legacy
	 */
	public String[] getObjectIds() {
		return commandRequest.getObjectIds();
	}

	/**
	 * Internal proprietary
	 */
	public int getRelayConnectionId() {
		return commandRequest.getRelayConnectionId();
	}

	/**
	 * Internal, proprietary
	 */
	public long getCommandId() {
		return commandRequest.getCommandId();
	}

	/**
	 * In the case that the command is executed over realtime rows (ex: user selects a record in the frontend backed by an {@link AmiRow}) then this is populated with those rows'
	 * ids. See {@link AmiRow#getAmiId()} and {@link AmiTable#getAmiRowByAmiId(long)}
	 */
	public long[] getAmiObjectIds() {
		return commandRequest.getAmiObjectIds();
	}

	/**
	 * Internal, proprietary
	 */
	public String getAppId() {
		return commandRequest.getAppId();
	}

	/**
	 * @return true if this command may return multiple rows for {@link #getFields()}
	 * 
	 */
	public boolean getIsManySelect() {
		return commandRequest.getIsManySelect();
	}

	/**
	 * @return All subscribers, at time of subscription/registration with AMI, are assigned a GUID representing that session. For end users, the life cycle extends from login to
	 *         logout.
	 */
	public String getSessionId() {
		return commandRequest.getSessionId();
	}

	/**
	 * @return The id of the subscriber name (for front end this is the username supplied at the login page).
	 */
	public String getUserId() {
		return initialRequest.getAction().getInvokedBy();
	}

}
