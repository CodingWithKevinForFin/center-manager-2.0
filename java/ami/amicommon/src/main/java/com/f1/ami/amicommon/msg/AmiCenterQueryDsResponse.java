package com.f1.ami.amicommon.msg;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.Table;
import com.f1.base.VID;

@VID("F1.VE.QDOR")
public interface AmiCenterQueryDsResponse extends AmiCenterResponse {

	@PID(2)
	public List<Table> getTables();
	public void setTables(List<Table> records);

	@PID(3)
	public void setDurrationNanos(long durrationNanos);
	public long getDurrationNanos();

	@PID(4)
	public void setQuerySessionId(long durrationNanos);
	public long getQuerySessionId();

	@PID(5)
	public long getRowsEffected();
	public void setRowsEffected(long rowsEffected);

	@PID(6)
	public List<AmiDatasourceTable> getPreviewTables();
	public void setPreviewTables(List<AmiDatasourceTable> records);

	@PID(7)
	public void setReturnType(Class<?> returnType);
	public Class<?> getReturnType();

	@PID(8)
	public void setReturnValue(Object t);
	public Object getReturnValue();//this will not be populated if the ReutnrValueIsTableAt is set.

	@PID(9)
	public void setGeneratedKeys(List<Object> list);
	public List<Object> getGeneratedKeys();

	//This is to avoid double [de]marshalling the return value when it already exists in the return tables
	@PID(10)
	public void setReturnValueTablePos(int n);//index of return value within getTables(), if -1 use getReturnValue()
	public int getReturnValueTablePos();

	@PID(11)
	public void setDisableLogging(boolean disableLogging);
	public boolean getDisableLogging();
}
