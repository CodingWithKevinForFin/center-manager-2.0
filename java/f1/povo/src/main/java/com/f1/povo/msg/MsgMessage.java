/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.povo.msg;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.ST.MS")
public interface MsgMessage extends Message {

	byte PID_MESSAGE = 1;
	byte PID_CORRELATION_ID = 2;
	byte PID_RESULT_TOPIC_SUFFIX = 3;
	byte PID_REQUEST_TOPIC_SUFFIX = 4;
	byte PID_SOURCE = 5;

	@PID(PID_MESSAGE)
	public Message getMessage();
	public void setMessage(Message message);

	@PID(PID_CORRELATION_ID)
	public Object getCorrelationId();
	public void setCorrelationId(Object correlationId);

	@PID(PID_RESULT_TOPIC_SUFFIX)
	public String getResultTopicSuffix();
	public void setResultTopicSuffix(String responseTopic);

	@PID(PID_REQUEST_TOPIC_SUFFIX)
	public void setRequestTopicSuffix(String processUid);
	public String getRequestTopicSuffix();

	@PID(PID_SOURCE)
	public String getSource();
	public void setSource(String source);

}
