package com.f1.generator;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.pofo.fix.MsgType;

@VID("F2A.ORDEREV")
public interface OrderEvent extends Message {

	@PID(1)
	MsgType getMsgType();
	void setMsgType(final MsgType msgType);

	@PID(2)
	String getPartitionId();
	void setPartitionId(final String partitionId);

	@PID(3)
	quickfix.Message getFIXMessage();
	void setFIXMessage(final quickfix.Message message);

	@PID(4)
	Order getOrder();
	void setOrder(final Order order);

	@PID(5)
	String getClOrdID();
	void setClOrdID(final String clOrdID);

	@PID(6)
	String getSymbol();
	void setSymbol(final String symbol);

}
