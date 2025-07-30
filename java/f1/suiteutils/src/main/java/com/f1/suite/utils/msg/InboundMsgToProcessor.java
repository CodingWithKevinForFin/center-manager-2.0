/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.utils.msg;

import java.util.ArrayList;
import java.util.List;

import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.OutputPort;
import com.f1.container.impl.AbstractConnectable;
import com.f1.container.impl.BasicOutputPort;
import com.f1.msg.MsgEvent;
import com.f1.msg.MsgEventListener;
import com.f1.msg.MsgInputTopic;
import com.f1.utils.structs.Tuple2;

public class InboundMsgToProcessor extends AbstractConnectable implements MsgEventListener {

	public final OutputPort<MsgAction> output;
	private ObjectGeneratorForClass<MsgAction> generator;
	private List<Tuple2<MsgEvent, String>> pendingStart = new ArrayList<Tuple2<MsgEvent, String>>();
	private volatile boolean hasPending = true;

	public InboundMsgToProcessor() {
		addOutputPort(this.output = new BasicOutputPort<MsgAction>(MsgAction.class, this));
	}

	@Override
	public void onEvent(MsgEvent event, MsgInputTopic channel) {
		if (hasPending) {
			synchronized (pendingStart) {
				if (hasPending) {
					pendingStart.add(new Tuple2<MsgEvent, String>(event, channel.getFullTopicName()));
					return;
				}
			}
		}
		sendEvent(event, channel.getFullTopicName());
	}

	protected void sendEvent(MsgEvent event, String topicName) {
		MsgAction msg = generator.nw();
		msg.setMsgEvent(event);
		msg.setTopic(topicName);
		msg.setSource(event.getSource());
		event.transferAckerTo(msg);
		output.send(msg, null);
	}

	@Override
	public void start() {
		super.start();
		generator = getGenerator(MsgAction.class);
		synchronized (pendingStart) {
			for (Tuple2<MsgEvent, String> e : pendingStart)
				sendEvent(e.getA(), e.getB());
			pendingStart.clear();
			hasPending = false;
		}
	}
}
