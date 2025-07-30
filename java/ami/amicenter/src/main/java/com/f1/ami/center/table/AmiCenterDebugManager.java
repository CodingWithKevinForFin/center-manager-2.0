package com.f1.ami.center.table;

import java.util.List;

import com.f1.ami.amiscript.AmiDebugManager;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.amiscript.AmiDebugMessageListener;

public class AmiCenterDebugManager implements AmiDebugManager {

	@Override
	public void addMessage(AmiDebugMessage e) {
	}

	@Override
	public boolean shouldDebug(byte severity) {
		return false;
	}

	@Override
	public List<AmiDebugMessage> getMessages(byte severity) {
		return null;
	}

	@Override
	public void clearMessages(byte severity) {
	}

	@Override
	public void removeMessage(AmiDebugMessage em) {
	}

	@Override
	public void removeDebugMessageListener(AmiDebugMessageListener listener) {
	}

	@Override
	public void addDebugMessageListener(AmiDebugMessageListener listener) {
	}

	@Override
	public void setShouldDebug(byte severityInfo, boolean b) {
	}

}
