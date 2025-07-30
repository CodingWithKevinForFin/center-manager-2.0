/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.povo.db;

import java.util.List;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.Table;
import com.f1.base.VID;
import com.f1.base.Valued;

@VID("F1.DB.RS")
public interface DbResultMessage extends Message {

	byte PID_OUT_PARAMS = 2;
	byte PID_OK = 3;
	byte PID_MESSAGE = 4;
	byte PID_RESULTS_VALUED = 5;
	byte PID_RESULTS_TABLE = 6;
	byte PID_QUERY_TYPE = 7;
	byte PID_NEXT_RESULT = 8;

	@PID(PID_RESULTS_TABLE)
	public Table getResultsTable();
	public void setResultsTable(Table results);

	@PID(PID_RESULTS_VALUED)
	public List<? extends Valued> getResultsValued();
	public void setResultsValued(List<? extends Valued> results);

	@PID(PID_OUT_PARAMS)
	public List<?> getOutParams();
	public void setOutParams(List<?> outParams);

	@PID(PID_OK)
	public void setOk(boolean b);
	public boolean getOk();

	@PID(PID_MESSAGE)
	public void setMessage(String b);
	public String getMessage();

	@PID(PID_QUERY_TYPE)
	public void setQueryType(byte b);
	public byte getQueryType();

	@PID(PID_NEXT_RESULT)
	public void setNextResult(DbResultMessage message);
	public DbResultMessage getNextResult();
}
