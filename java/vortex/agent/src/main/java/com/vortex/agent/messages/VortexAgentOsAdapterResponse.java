package com.vortex.agent.messages;

import java.util.List;

import com.f1.base.Message;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;

public interface VortexAgentOsAdapterResponse extends Message {

	public int getCommandType();
	public void setCommandType(int type);

	public void setEntities(List<VortexAgentEntity> revisions);
	public List<VortexAgentEntity> getEntities();

	public Message getResponseMessage();
	public void setResponseMessage(Message message);
}
