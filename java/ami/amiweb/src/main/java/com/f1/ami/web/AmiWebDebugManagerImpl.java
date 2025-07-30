package com.f1.ami.web;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.f1.ami.amiscript.AmiDebugManager;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.amiscript.AmiDebugMessageListener;
import com.f1.utils.CH;
import com.f1.utils.CircularList;
import com.f1.utils.LH;

public class AmiWebDebugManagerImpl implements AmiDebugManager {
	private static final Logger log = LH.get();

	private long debugSeqNum = 0;
	private int maxMessagesWarning = 100;
	private int maxMessagesInfo = 100;
	private List<AmiDebugMessage> warningMessages = new CircularList<AmiDebugMessage>();
	private List<AmiDebugMessage> infoMessages = new CircularList<AmiDebugMessage>();
	final private List<AmiDebugMessageListener> debugMessageListener = new ArrayList<AmiDebugMessageListener>();
	final private AmiWebService service;

	private boolean shouldDebugWarning;

	private boolean shouldDebugInfo;

	public AmiWebDebugManagerImpl(AmiWebService service) {
		this.service = service;
	}
	public void fireDebugMessage(AmiDebugMessage message) {
		for (int i = 0; i < this.debugMessageListener.size(); i++)
			this.debugMessageListener.get(i).onAmiDebugMessage(this, message);
	}
	@Override
	public void removeDebugMessageListener(AmiDebugMessageListener listener) {
		CH.removeOrThrow(this.debugMessageListener, listener);
	}
	@Override
	public void addDebugMessageListener(AmiDebugMessageListener listener) {
		CH.addIdentityOrThrow(this.debugMessageListener, listener);
	}
	private void fireMessageRemoved(AmiDebugMessage message) {
		for (int i = 0; i < this.debugMessageListener.size(); i++)
			this.debugMessageListener.get(i).onAmiDebugMessagesRemoved(this, message);
	}
	@Override
	public void addMessage(AmiDebugMessage e) {
		if (!shouldDebug(e.getSeverity())) {
			LH.warning(log, new RuntimeException("SHOULD NOT BE DEBUGGING THIS"));
			return;
		}
		e.init(this.service.getPortletManager().getNow(), debugSeqNum++);
		List<AmiDebugMessage> t;
		switch (e.getSeverity()) {
			case AmiDebugMessage.SEVERITY_INFO:
				t = this.infoMessages;
				while (t.size() >= maxMessagesInfo)
					fireMessageRemoved(t.remove(0));
				break;
			case AmiDebugMessage.SEVERITY_WARNING:
				t = this.warningMessages;
				while (t.size() >= maxMessagesWarning)
					fireMessageRemoved(t.remove(0));
				break;
			default:
				throw new RuntimeException("Unknown severity: " + e.getSeverity());
		}
		t.add(e);
		fireDebugMessage(e);
	}

	public void setShouldDebug(byte severity, boolean should) {
		switch (severity) {
			case AmiDebugMessage.SEVERITY_INFO:
				this.shouldDebugInfo = should;
				break;
			case AmiDebugMessage.SEVERITY_WARNING:
				this.shouldDebugWarning = should;
				break;
			default:
				throw new RuntimeException("Unknown severity: " + severity);
		}
	}
	@Override
	public boolean shouldDebug(byte severity) {
		switch (severity) {
			case AmiDebugMessage.SEVERITY_INFO:
				return this.shouldDebugInfo;
			case AmiDebugMessage.SEVERITY_WARNING:
				return this.shouldDebugWarning;
			default:
				throw new RuntimeException("Unknown severity: " + severity);
		}
	}
	public void setMaxMessages(byte severity, int max) {
		switch (severity) {
			case AmiDebugMessage.SEVERITY_INFO:
				this.maxMessagesInfo = max;
				break;
			case AmiDebugMessage.SEVERITY_WARNING:
				this.maxMessagesWarning = max;
				break;
			default:
				throw new RuntimeException("Unknown severity: " + severity);
		}
	}
	public int getMaxMessages(byte severity) {
		switch (severity) {
			case AmiDebugMessage.SEVERITY_INFO:
				return this.maxMessagesInfo;
			case AmiDebugMessage.SEVERITY_WARNING:
				return this.maxMessagesWarning;
			default:
				throw new RuntimeException("Unknown severity: " + severity);
		}
	}
	public void removeMessage(AmiDebugMessage em) {
		if (getMessages(em.getSeverity()).remove(em))
			fireMessageRemoved(em);
	}

	public List<AmiDebugMessage> getWarningMessages() {
		return this.warningMessages;
	}

	public void clearMessages(byte severity) {
		List<AmiDebugMessage> t = getMessages(severity);
		for (AmiDebugMessage i : t)
			fireMessageRemoved(i);
		if (t.isEmpty())
			return;
		t.clear();
	}
	public List<AmiDebugMessage> getMessages(byte severity) {
		switch (severity) {
			case AmiDebugMessage.SEVERITY_INFO:
				return this.infoMessages;
			case AmiDebugMessage.SEVERITY_WARNING:
				return this.warningMessages;
			default:
				throw new RuntimeException("Unknown severity: " + severity);
		}
	}
}
