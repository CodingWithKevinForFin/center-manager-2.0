package com.f1.ami.amiscript;

public interface AmiDebugMessageListener {
	public void onAmiDebugMessage(AmiDebugManager manager, AmiDebugMessage message);
	public void onAmiDebugMessagesRemoved(AmiDebugManager manager, AmiDebugMessage message);
}
