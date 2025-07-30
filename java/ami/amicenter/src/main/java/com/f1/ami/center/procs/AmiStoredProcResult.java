package com.f1.ami.center.procs;

import java.util.List;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.Table;
import com.f1.base.VID;

@VID("F1.VE.ASPR")
public interface AmiStoredProcResult extends Message {

	@PID(1)
	/**
	 * @return the ordered list of tables returned as a result of running the stored procedure
	 */
	public void setTables(List<Table> query);
	public List<Table> getTables();
}
