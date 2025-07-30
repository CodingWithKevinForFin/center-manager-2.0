package com.f1.fix2ami.processor;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.container.ThreadScope;
import com.f1.fix2ami.Fix2AmiEvent;
import com.f1.fix2ami.Fix2AmiState;
import com.f1.pofo.fix.MsgType;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;

import quickfix.ConfigError;

public class NewOrderProcessor extends AbstractFix2AmiProcessor {
	private static final Logger log = Logger.getLogger(NewOrderProcessor.class.getName());

	private final boolean testMode;
	private final Set<Integer> modifiedPropagatingTags = new HashSet<>();

	public NewOrderProcessor(final PropertyController props) throws ConfigError {
		super(props, ATTR_RETAIN_TAG_NEW_ORDER);

		// this is for testing and should not have old state for New Order.
		modifiedPropagatingTags.addAll(propagatingTags);
		testMode = props.getOptional("ami.fix2ami.TestMode", false);
		LH.info(log, "****testMode: ", testMode);
		if (testMode) {
			modifiedPropagatingTags.add(TAG_TransactTime);
		}
	}

	@Override
	public void processAction(Fix2AmiEvent event, Fix2AmiState state, ThreadScope threadScope) throws Exception {
		LH.info(log, "Got a new order: " + event);
		ProcessResult pr = processOrder(event, state, modifiedPropagatingTags);

		state.saveOrderStatusChain(pr.origClOrdID, String.valueOf(MsgType.NEW_ORDER_SINGLE.getEnumValue()));
		state.setOrderState(pr.origClOrdID, pr.orderState);
		state.setRepeatingGroupMapByClOrdID(pr.origClOrdID, pr.repeatingGroup);
		state.addClOrdID(pr.clOrdID);

		event.setOrderStatusChain(state.getOrderStatusChain(pr.origClOrdID));
		event.setAmiOrderMsg(applyFilter(pr, TAG_FILTER_TYPE.ORDER));
		event.setMsgProcessStatus(MSG_PROCESS_STATUS.NO_ERROR);

		amiPublishPort.send(event, threadScope);
	}
}
