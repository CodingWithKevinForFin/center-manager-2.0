package com.f1.ami.center.procs;

import java.util.List;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.ASPQ")
public interface AmiStoredProcRequest extends Message {

	@PID(1)
	public List<Object> getArguments();
	public void setArguments(List<Object> arguments);

	@PID(2)
	public void setLimitOffset(int limitOffset);//0=no offset
	public int getLimitOffset();

	@PID(3)
	public void setLimit(int limit);//-1=no limit
	public int getLimit();

	@PID(4)
	public void setInvokedBy(String username);
	public String getInvokedBy();
}
