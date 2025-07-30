package com.f1.qfix;

import java.util.ArrayList;
import java.util.List;

import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.OutputPort;
import com.f1.container.impl.AbstractConnectable;
import com.f1.msg.MsgEvent;
import com.f1.msg.MsgEventListener;
import com.f1.msg.MsgInputTopic;
import com.f1.qfix.msg.FixMsgEvent;

public class FixMsgToProcessorAdapter extends AbstractConnectable implements MsgEventListener {

	private ObjectGeneratorForClass<FixEvent> generator;
	public final OutputPort<FixEvent> output = newOutputPort(FixEvent.class);
	private List<MsgEvent> pendingStart = new ArrayList<MsgEvent>();
	private volatile boolean hasPending = true;

	protected void sendEvent(MsgEvent event) {
		FixMsgEvent fixMsgEvent = (FixMsgEvent) event;
		FixEvent fixEvent = generator.nw();
		fixEvent.setMessage(fixMsgEvent.getMessage());
		fixEvent.setSessionName(fixMsgEvent.getSessionName());
		fixMsgEvent.transferAckerTo(fixEvent);
		output.send(fixEvent, null);
	}

	@Override
	public void onEvent(MsgEvent event, MsgInputTopic channel) {
		if (hasPending) {
			synchronized (pendingStart) {
				if (hasPending) {
					pendingStart.add(event);
					return;
				}
			}
		}
		sendEvent(event);
	}

	@Override
	public void start() {
		super.start();
		generator = getGenerator(FixEvent.class);
		synchronized (pendingStart) {
			for (MsgEvent e : pendingStart)
				sendEvent(e);
			pendingStart.clear();
			hasPending = false;
		}
	}
}
