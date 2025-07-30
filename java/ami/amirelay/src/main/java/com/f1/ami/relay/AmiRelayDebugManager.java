package com.f1.ami.relay;

import java.util.List;

import com.f1.ami.amiscript.AmiDebugManager;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.amiscript.AmiDebugMessageListener;

public class AmiRelayDebugManager implements AmiDebugManager {

	@Override
	public void addMessage(AmiDebugMessage e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean shouldDebug(byte severity) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<AmiDebugMessage> getMessages(byte severity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearMessages(byte severity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeMessage(AmiDebugMessage em) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeDebugMessageListener(AmiDebugMessageListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addDebugMessageListener(AmiDebugMessageListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setShouldDebug(byte severityInfo, boolean b) {
		// TODO Auto-generated method stub

	}

}
