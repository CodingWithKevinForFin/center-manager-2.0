package com.f1.pofo.fix;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.pofo.oms.Execution;

/**
 * fix execution report.
 * 
 */
@VID("F1.FX.ER")
public interface FixExecutionReport extends FixReport {

	/**
	 * @return the execution details
	 */
	@PID(30)
	public Execution getExecution();

	public void setExecution(Execution e);
}
