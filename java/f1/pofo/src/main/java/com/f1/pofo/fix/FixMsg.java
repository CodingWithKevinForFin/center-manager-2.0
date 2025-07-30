package com.f1.pofo.fix;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

/**
 * 
 * any fix message
 * 
 */
@VID("F1.FX.FM")
public interface FixMsg extends Message {

	/**
	 * @return the type of message(typically fix tag 35)
	 */
	@PID(1)
	public MsgType getType();

	public void setType(MsgType type);

	/**
	 * @return the fix session name
	 */
	@PID(2)
	public String getSessionName();

	public void setSessionName(String sessionName);

	/**
	 * @return the id of the root order.
	 */
	@PID(3)
	public String getRootOrderId();

	public void setRootOrderId(String orderId);


	/**
	 * @return is a possible resend (typically fix tag 97). true='Y', false ='N'
	 */
	@PID(27)
	public boolean getPosResend();
	public void setPosResend(boolean posResend);


	/**
	 * @return is a possible dup (typically fix tag 43). true='Y', false ='N'
	 */
	@PID(90)
	public boolean getPosDup();
	public void setPosDup(boolean posDup);
}
