package com.f1.fix2ami.processor;

import java.util.logging.Logger;

import com.f1.container.ThreadScope;
import com.f1.fix2ami.Fix2AmiEvent;
import com.f1.fix2ami.Fix2AmiState;
import com.f1.utils.LH;

import quickfix.ConfigError;

public class UnsupportMsgProcessor extends AbstractFix2AmiProcessor {
	private static final Logger log = Logger.getLogger(UnsupportMsgProcessor.class.getName());

	public UnsupportMsgProcessor() throws ConfigError {
		super(null, null);
	}

	@Override
	public void processAction(Fix2AmiEvent event, Fix2AmiState state, ThreadScope threadScope) throws Exception {
		LH.info(log, "Got unsupoort msg type: ", event.getMsgType());
		event.setOrigClOrdID(state.getOrigClOrdID(event.getClOrdID()));
		event.setMsgProcessStatus(MSG_PROCESS_STATUS.UNSUPPORT_MSG_TYPE);

		amiPublishPort.send(event, threadScope);
	}

}
