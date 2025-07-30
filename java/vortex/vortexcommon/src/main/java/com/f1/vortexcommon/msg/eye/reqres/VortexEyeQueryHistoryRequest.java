package com.f1.vortexcommon.msg.eye.reqres;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.VE.QHQ")
public interface VortexEyeQueryHistoryRequest extends PartialMessage {

	final byte TYPE_MACHINE = 0;
	final byte TYPE_PROCESS = 1;
	final byte TYPE_CONNECTION = 2;
	final byte TYPE_NETLINK = 3;
	final byte TYPE_CRON = 4;
	final byte TYPE_FILE_SYSTEM = 5;
	final byte TYPE_ADDRESS = 6;
	final byte TYPE_EVENT = 7;
	final byte TYPE_AGENT_MACHINE_EVENT = 8;
	final byte TYPE_PROCESS_CONNECTION = 9;
	final byte TYPE_MACHINE_ALL = 10;
	final byte TYPE_DATABASE = 11;

	byte PID_TYPE = 23;
	byte PID_IDS = 24;
	byte PID_MACHINE_INSTANCE_ID = 25;
	byte PID_PIDS = 26;
	byte PID_MACHINE_HISTORY = 27;

	@PID(PID_TYPE)
	public byte getType();
	public void setType(byte type);

	@PID(PID_IDS)
	public List<Long> getIds();
	public void setIds(List<Long> ids);

	@PID(PID_MACHINE_INSTANCE_ID)
	public long getMachineInstanceId();
	public void setMachineInstanceId(long machineInstanceId);

	@PID(PID_PIDS)
	public List<String> getPids();
	public void setPids(List<String> pids);

	@PID(PID_MACHINE_HISTORY)
	public boolean getMachineHistory();
	public void setMachineHistory(boolean machineHistory);
}
