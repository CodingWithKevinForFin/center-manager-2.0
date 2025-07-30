package com.f1.ami.amiscript;

import java.util.Collections;
import java.util.List;

public class AmiDebugManagerWrapper implements AmiDebugManager {

	private AmiDebugManager inner;

	public AmiDebugManagerWrapper(AmiDebugManager inner) {
		super();
		this.inner = inner;
	}

	@Override
	public void addMessage(AmiDebugMessage e) {
		if (inner != null)
			inner.addMessage(e);
	}

	@Override
	public boolean shouldDebug(byte severity) {
		if (inner != null)
			return inner.shouldDebug(severity);
		return false;
	}

	@Override
	public List<AmiDebugMessage> getMessages(byte severity) {
		if (inner != null)
			return inner.getMessages(severity);
		return Collections.EMPTY_LIST;
	}

	@Override
	public void clearMessages(byte severity) {
		if (inner != null)
			inner.clearMessages(severity);
	}

	@Override
	public void removeMessage(AmiDebugMessage em) {
		if (inner != null)
			inner.removeMessage(em);
	}

	@Override
	public void removeDebugMessageListener(AmiDebugMessageListener listener) {
		if (inner != null)
			inner.removeDebugMessageListener(listener);
	}

	@Override
	public void addDebugMessageListener(AmiDebugMessageListener listener) {
		if (inner != null)
			inner.addDebugMessageListener(listener);
	}

	@Override
	public void setShouldDebug(byte severityInfo, boolean b) {
		if (inner != null)
			inner.setShouldDebug(severityInfo, b);
	}

	public AmiDebugManager getInner() {
		return inner;
	}

	public void setInner(AmiDebugManager inner) {
		this.inner = inner;
	}

}
