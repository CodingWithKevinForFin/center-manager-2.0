package com.f1.fix2ami.processor;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.fix2ami.Fix2AmiEvent;
import com.f1.fix2ami.Fix2AmiState;
import com.f1.fix2ami.processor.AbstractAmiPublishField.StringField2Ami;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;

import quickfix.ConfigError;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.StringField;

public class ExecutionReportProcessor extends AbstractFix2AmiProcessor {
	private static final Logger log = Logger.getLogger(ExecutionReportProcessor.class.getName());

	final Set<Integer> propagatingTagsForCancelPending;
	final Set<Integer> propagatingTagsForReplacePending;
	final Set<Integer> propagatingTagsForCancelled;
	final Set<Integer> propagatingTagsForReplaced;
	final Set<Integer> propagatingTagsForDoneForDay;
	final Set<Integer> propagatingTagsForOrderAck;
	final Set<Integer> propagatingTagsForReject;
	final Set<Integer> propagatingTagsForOrderPendingNew;

	public OutputPort<Fix2AmiEvent> unsupportMessagePort = newOutputPort(Fix2AmiEvent.class);

	public ExecutionReportProcessor(final PropertyController props) throws ConfigError {
		super(props, ATTR_RETAIN_TAG_EXECUTION_REPORT);
		propagatingTagsForCancelPending = splitToSet(",", props.getOptional(ATTR_RETAIN_TAG_CANCEL_PENDING));
		propagatingTagsForReplacePending = splitToSet(",", props.getOptional(ATTR_RETAIN_TAG_REPLACE_PENDING));
		propagatingTagsForCancelled = splitToSet(",", props.getOptional(ATTR_RETAIN_TAG_CANCELLED));
		propagatingTagsForReplaced = splitToSet(",", props.getOptional(ATTR_RETAIN_TAG_REPLACED));
		propagatingTagsForDoneForDay = splitToSet(",", props.getOptional(ATTR_RETAIN_TAG_DONE_FOR_DAY));
		propagatingTagsForOrderAck = splitToSet(",", props.getOptional(ATTR_RETAIN_TAG_ORDER_ACK));
		propagatingTagsForReject = splitToSet(",", props.getOptional(ATTR_RETAIN_TAG_REJECT));
		propagatingTagsForOrderPendingNew = splitToSet(",", props.getOptional(ATTR_RETAIN_TAG_ORDER_PENDING_NEW));

	}

	@Override
	public void processAction(Fix2AmiEvent event, Fix2AmiState state, ThreadScope threadScope) throws Exception {
		LH.info(log, "Got a execution report: " + event);
		ProcessResult pr = null;

		final Message msg = event.getFIXMessage();
		char status = ' ';
		boolean error = false;

		try {
			final String ordStatus = msg.getString(TAG_OrdStatus);
			if (null != ordStatus) {
				status = ordStatus.charAt(0);
				switch (ordStatus) {
					case "0": // Ack
						pr = processResponse(event, state, propagatingTagsForOrderAck);
						break;
					case "3":
						pr = processResponse(event, state, propagatingTagsForDoneForDay);
						break;
					case "4": // Canceled
						pr = processResponse(event, state, propagatingTagsForCancelled);
						break;
					case "5": // Replaced
						pr = processResponse(event, state, propagatingTagsForReplaced);
						break;
					case "6": // pending cancel
						pr = processResponse(event, state, propagatingTagsForCancelPending);
						break;
					case "8": // rejected
						pr = processResponse(event, state, propagatingTagsForReject);
						break;
					case "A": // pending ack
						pr = processResponse(event, state, propagatingTagsForOrderPendingNew);
						break;
					case "E": // pending replace
						pr = processResponse(event, state, propagatingTagsForReplacePending);
						break;
					case "1": // partial fill
					case "2": // fill
						pr = processResponse(event, state, propagatingTags);

						// trade correct or trade bust.
						final String execTransType = AbstractFix2AmiProcessor.getTagValue(msg, AbstractFix2AmiProcessor.TAG_ExecTransType);
						if (execTransType.isEmpty()) {
							final String execType = AbstractFix2AmiProcessor.getTagValue(msg, AbstractFix2AmiProcessor.TAG_ExecType);
							if (!execType.isEmpty()) {
								if (execType.charAt(0) == AbstractFix2AmiProcessor.ExecType_BUST) {
									status = AbstractFix2AmiProcessor.getTradeBustIndicator();
								}
							}
						} else {
							switch (execTransType.charAt(0)) {
								case AbstractFix2AmiProcessor.ExecTransType_CANCEL:
									status = AbstractFix2AmiProcessor.getTradeBustIndicator();
									break;
								case AbstractFix2AmiProcessor.ExecTransType_CORRECT:
									status = AbstractFix2AmiProcessor.getTradeCorrectionIndicator();
									break;
								default:
									break;
							}
						}

						break;
					default:
						error = true;
						break;
				}
				if (error == false) {
					state.saveTradeStatusChain(pr.origClOrdID, String.valueOf(status));
					event.setTradeStatusChain(state.getTradeStatusChain(pr.origClOrdID));
					event.setAmiExecMsg(applyFilter(pr, TAG_FILTER_TYPE.TRADE));
				}
			} else {
				error = true;
			}

			pr.orderState.put(TAG_OrigClOrdID, new StringField2Ami(ORIG_CLORDID_TAG_NAME, new StringField(TAG_OrigClOrdID, pr.origClOrdID)));
		} catch (FieldNotFound fe) {
			LH.warning(log, "ClOrdID(", event.getClOrdID(), ") does not have OrdStatus(39) tag");
			//System.out.println("EX Exception: " + fe);
			error = true;
		} catch (Exception e) {
			LH.warning(log, "Exception: " + e);
			//System.out.println("EX Exception: " + e);
			error = true;
		}

		if (error) {
			// forward message to UnsupportMsgProcessor.
			unsupportMessagePort.send(event, threadScope);
			return;
		}

		state.saveOrderStatusChain(pr.origClOrdID, String.valueOf(status));

		state.setOrderState(pr.origClOrdID, pr.orderState);
		state.setRepeatingGroupMapByClOrdID(pr.origClOrdID, pr.repeatingGroup);

		event.setOrderStatusChain(state.getOrderStatusChain(pr.origClOrdID));
		event.setAmiOrderMsg(applyFilter(pr, TAG_FILTER_TYPE.ORDER));

		if (pr.missingOldState) {
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

		amiPublishPort.send(event, threadScope);
	}

}
