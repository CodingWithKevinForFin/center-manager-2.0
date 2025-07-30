package com.f1.ami.amiscript;

import java.util.List;

public interface AmiDebugManager {

	//	void debug(byte level, byte type, String panelId, String targetType, String target, String message, Map<Object, Object> details, Throwable ex);
	void addMessage(AmiDebugMessage e);
	boolean shouldDebug(byte severity);
	List<AmiDebugMessage> getMessages(byte severity);
	void clearMessages(byte severity);
	void removeMessage(AmiDebugMessage em);
	void removeDebugMessageListener(AmiDebugMessageListener listener);
	void addDebugMessageListener(AmiDebugMessageListener listener);
	void setShouldDebug(byte severityInfo, boolean b);
}
