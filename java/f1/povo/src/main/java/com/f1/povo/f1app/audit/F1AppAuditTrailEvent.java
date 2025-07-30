package com.f1.povo.f1app.audit;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.povo.f1app.F1AppEvent;

@VID("F1.FA.ATE")
public interface F1AppAuditTrailEvent extends F1AppEvent {

	byte PID_TYPE = 5;
	byte PID_AGENT_RULE_IDS = 1;
	byte PID_TIME_MS = 2;
	byte PID_AGENT_F1_OBJECT_ID = 4;
	byte PID_AUDIT_SEQUENCE_NUMBER = 6;

	byte STRING = 0;
	byte BYTES = 1;

	byte FORMAT_STRING_JSON = 2 | STRING;
	byte FORMAT_STRING_TEXT = 4 | STRING;
	byte FORMAT_BYTES_TEXT = 4 | BYTES;
	byte FORMAT_STRING_FIX = 8 | STRING;
	byte FORMAT_BYTES_FIX = 8 | BYTES;
	byte FORMAT_BYTES_F1 = 12 | BYTES;

	@PID(1)
	public long[] getAgentRuleIds();
	public void setAgentRuleIds(long[] rules);

	@PID(4)
	public long getAgentF1ObjectId();
	public void setAgentF1ObjectId(long id);

	@PID(5)
	public byte getType();
	public void setType(byte type);

	@PID(6)
	public long getAuditSequenceNumber();
	public void setAuditSequenceNumber(long id);

	@PID(7)
	public String getPayloadAsString();
	public void setPayloadAsString(String payloadAsString);

	//-- The payloadAsString and payloadAsBytes should be mutually exclusive. Use getPayloadFormat to determine which to use --
	@PID(8)
	public byte[] getPayloadAsBytes();
	public void setPayloadAsBytes(byte[] payloadAsBytes);

	@PID(9)
	public byte getPayloadFormat();
	public void setPayloadFormat(byte messageType);

}
