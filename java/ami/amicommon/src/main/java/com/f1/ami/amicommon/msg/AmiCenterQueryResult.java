package com.f1.ami.amicommon.msg;

import java.util.List;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.Table;
import com.f1.base.VID;

@VID("F1.VE.ACQRS")
public interface AmiCenterQueryResult extends Message {

	@PID(1)
	/**
	 * @return the ordered list of tables returned as a result of running a query/upload
	 */
	public void setTables(List<Table> query);
	public List<Table> getTables();

	@PID(3)
	/**
	 * @return the number of rows effected as a result of running the query/upload
	 */
	public long getRowsEffected();
	public void setRowsEffected(long limit);

	@PID(4)
	public void setReturnType(Class<?> returnType);
	public Class<?> getReturnType();

	@PID(5)
	public void setReturnValue(Object t);
	public Object getReturnValue();

	@PID(6)
	public void setGeneratedKeys(List<Object> query);
	public List<Object> getGeneratedKeys();
}
