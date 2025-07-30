package com.f1.pofo.oms;

import java.util.Map;

import com.f1.base.DateNanos;
import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;
import com.f1.base.ValuedListenable;

/**
 * Represents the execution of an order
 * 
 */
@VID("F1.FX.EX")
public interface Execution extends Revisioned, ValuedListenable, PartialMessage {

	/**
	 * @return the executed quantity
	 */
	@PID(1)
	public int getExecQty();
	public void setExecQty(int execQty);

	/**
	 * 
	 * @return the average executed price
	 */
	@PID(2)
	public double getExecPx();
	public void setExecPx(double execPx);

	/**
	 * 
	 * @return the last market
	 */
	@PID(3)
	public String getLastMkt();
	public void setLastMkt(String lastMkt);

	/**
	 * 
	 * @return the executing broker
	 */
	@PID(4)
	public String getExecBroker();

	public void setExecBroker(String execBroker);

	/**
	 * 
	 * @return the contra broker
	 */
	@PID(5)
	public String getContraBroker();
	public void setContraBroker(String contraBroker);

	// Identification

	/**
	 * @return the id of the group which relates child and parent executions
	 */
	@PID(6)
	public String getExecGroupID();
	public void setExecGroupID(String id);

	/**
	 * 
	 * @return the id of the order this execution is for
	 */
	@PID(7)
	public String getOrderId();
	public void setOrderId(String id);

	/**
	 * @return the revision of the order that this execution is related to
	 */
	@PID(8)
	public int getOrderRevision();
	public void setOrderRevision(int revision);

	/**
	 * 
	 * @return true indicates that it has not been modified (ex busted)
	 */
	@PID(9)
	public boolean getIsLatest();
	public void setIsLatest(boolean isLatest);

	/**
	 * @return the status of this execution. 0=new, 1=bust,2=correct
	 */
	@PID(11)
	public int getExecStatus();
	public void setExecStatus(int status);

	/**
	 * @return when the execution occurred
	 */
	@PID(12)
	public DateNanos getExecTime();
	public void setExecTime(DateNanos transactTime);

	/**
	 * @return user-defined custom tags, typically the key will be a fix tag
	 */
	@PID(13)
	public Map<Integer, String> getPassThruTags();
	public void setPassThruTags(Map<Integer, String> passThruTags);

	/**
	 * @return ExecTransType (0-New,1-Bust,2-Correct)
	 */
	@PID(14)
	public int getExecTransType();
	public void setExecTransType(int exTT);

	@PID(15)
	public String getExecRefID();
	public void setExecRefID(String refId);

	@PID(16)
	public void setText(String text);
	public String getText();

	public Execution clone();

}
