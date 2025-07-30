package com.f1.povo.f1app;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.PO")
public interface F1AppPort extends F1AppContainerScope {

	public int NOT_CONNECTED = -1;

	
	byte PID_CONNECTED_TO=10;
	byte PID_PORT_TYPE=11;
	byte PID_ACTION_TYPE_CLASS_ID=12;
	byte PID_RESULT_ACTION_TYPE_CLASS_ID=13;
	
	//-1 means not connected
	@PID(PID_CONNECTED_TO)
	public long getConnectedTo();
	public void setConnectedTo(long connectedTo);

	@PID(PID_PORT_TYPE)
	public byte getPortType();
	public void setPortType(byte type);

	@PID(PID_ACTION_TYPE_CLASS_ID)
	public long getActionTypeClassId();
	public void setActionTypeClassId(long actionType);

	@PID(PID_RESULT_ACTION_TYPE_CLASS_ID)
	public long getResultActionTypeClassId();
	public void setResultActionTypeClassId(long resultActionType);
}
