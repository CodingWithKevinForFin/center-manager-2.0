package com.f1.ami.amicommon.msg;

import com.f1.base.PID;
import com.f1.base.Table;
import com.f1.base.VID;

@VID("F1.VE.DSTE")
public interface AmiCenterQueryDsTrackerEvent extends AmiCenterResponse {
	public byte TYPE_ERROR = 1;
	public byte TYPE_QUERY = 2;
	public byte TYPE_QUERY_START = 3;
	public byte TYPE_QUERY_END = 4;
	public byte TYPE_QUERY_END_ERROR = 5;
	public byte TYPE_QUERY_STEP = 6;

	@PID(3)
	public void setTimestamp(long timestamp);
	public long getTimestamp();

	@PID(4)
	public void setString(String event);
	public String getString();

	@PID(5)
	public void setType(byte timestamp);
	public byte getType();

	@PID(7)
	public void setDuration(long duration);
	public long getDuration();

	@PID(8)
	public void setResultTableSize(int result);
	public int getResultTableSize();

	@PID(9)
	public Table getResultTableSample();
	public void setResultTableSample(Table table);

	@PID(10)
	public void setResultTableName(String title);
	public String getResultTableName();
}
