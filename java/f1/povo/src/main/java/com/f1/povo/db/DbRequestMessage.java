/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.povo.db;

import java.util.Map;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.base.Valued;

@VID("F1.DB.RQ")
public interface DbRequestMessage extends Message {

	byte PID_ID = 1;
	byte PID_PARAMS = 2;
	byte PID_TYPE = 3;

	byte TYPE_INSERT = 0;
	byte TYPE_UPDATE = 1;//TODO
	byte TYPE_QUERY_TO_TABLE = 2;
	byte TYPE_QUERY_TO_VALUED = 3;
	byte PID_RESULT_VALUED_CLASS = 4;
	byte PID_NEXT_REQUEST = 5;

	@PID(PID_ID)
	public String getId();
	public void setId(String id);

	@PID(PID_PARAMS)
	public Map<Object, Object> getParams();
	public void setParams(Map<Object, Object> params);

	@PID(PID_TYPE)
	public byte getType();
	public void setType(byte type);

	@PID(PID_RESULT_VALUED_CLASS)
	public Class<? extends Valued> getResultValuedClass();
	public void setResultValuedClass(Class<? extends Valued> resultType);

	@PID(PID_NEXT_REQUEST)
	public DbRequestMessage getNextRequest();
	public void setNextRequest(DbRequestMessage request);
}
