package com.f1.fix2ami.processor;

import java.util.logging.Logger;

import com.f1.container.ThreadScope;
import com.f1.fix2ami.Fix2AmiEvent;
import com.f1.fix2ami.Fix2AmiState;
import com.f1.fix2ami.processor.AbstractAmiPublishField.StringField2Ami;
import com.f1.pofo.fix.MsgType;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;

import quickfix.ConfigError;
import quickfix.StringField;

public class ReplaceRequestProcessor extends AbstractFix2AmiProcessor {
	private static final Logger log = Logger.getLogger(ReplaceRequestProcessor.class.getName());

	public ReplaceRequestProcessor(final PropertyController props) throws ConfigError {
		super(props, ATTR_RETAIN_TAG_REPLACE_REQUEST);
	}

	@Override
	public void processAction(Fix2AmiEvent event, Fix2AmiState state, ThreadScope threadScope) throws Exception {
		LH.info(log, "Got a ReplaceRequest: " + event);
		ProcessResult pr = processOrder(event, state, propagatingTags);
		pr.orderState.put(TAG_OrigClOrdID, new StringField2Ami(ORIG_CLORDID_TAG_NAME, new StringField(TAG_OrigClOrdID, pr.origClOrdID)));

		state.saveOrderStatusChain(pr.origClOrdID, String.valueOf(MsgType.REPLACE_REQUEST.getEnumValue()));
		if (pr.missingOldState) {
			state.setOrderState(pr.origClOrdID, pr.orderState);
			state.setRepeatingGroupMapByClOrdID(pr.origClOrdID, pr.repeatingGroup);
			event.setMsgProcessStatus(MSG_PROCESS_STATUS.BROKEN_ORDER_CHAIN);
		} else {
			event.setMsgProcessStatus(MSG_PROCESS_STATUS.NO_ERROR);
		}

		try {
			state.addClOrdID(pr.clOrdID, pr.origClOrdID);
		} catch (IllegalStateException ise) {
			LH.info(log, "broken order chain exception");
			event.setMsgProcessStatus(MSG_PROCESS_STATUS.BROKEN_ORDER_CHAIN);
		}

		event.setOrderStatusChain(state.getOrderStatusChain(pr.origClOrdID));
		event.setAmiOrderMsg(applyFilter(pr, TAG_FILTER_TYPE.ORDER));

		amiPublishPort.send(event, threadScope);
	}
}
