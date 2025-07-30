package com.f1.fix.oms.schema;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.pofo.fix.FixMsg;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsClientAction;

@VID("F1.OM.OE")
public interface OrderEventCtx extends Message {

	/**
	 * @return mandatory fix details
	 */
	@PID(1)
	FixMsg getFixMsgEvent();
	void setFixMsgEvent(FixMsg event);

	/**
	 * 
	 * @return the causing action
	 */
	@PID(2)
	public OmsClientAction getClientMsg();
	public void setClientMsg(OmsClientAction msg);

	/**
	 * @return the message type
	 */
	@PID(3)
	OmsAction getOrderAction();
	void setOrderAction(OmsAction action);

	/**
	 * @return the related order
	 */
	@PID(4)
	OmsOrder getOrder();
	void setOrder(OmsOrder o);

	/**
	 * @return the related child order
	 */
	@PID(5)
	OmsOrder getChildOrder();
	void setChildOrder(OmsOrder o);

	/**
	 * @return fix text (typically fix tag 58)
	 */
	@PID(6)
	String getText();
	void setText(String text);

	/**
	 * @return reject text, populated if there was an issue processing this message
	 */
	@PID(7)
	String getRejectText();
	void setRejectText(String text);

}
