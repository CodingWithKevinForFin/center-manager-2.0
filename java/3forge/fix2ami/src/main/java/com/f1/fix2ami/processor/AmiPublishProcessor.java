package com.f1.fix2ami.processor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.f1.ami.client.AmiClient;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.fix2ami.Fix2AmiEvent;
import com.f1.fix2ami.Fix2AmiState;
import com.f1.fix2ami.processor.AbstractFix2AmiProcessor.MSG_PROCESS_STATUS;
import com.f1.fix2ami.processor.AbstractFix2AmiProcessor.TAG_FILTER_TYPE;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;

import quickfix.FieldNotFound;
import quickfix.Message;

public class AmiPublishProcessor extends BasicProcessor<Fix2AmiEvent, Fix2AmiState> {
	private static final String ATTR_AMI_ORDER_TABLE_NAME = "ami.ordertable.Name";
	private static final String ATTR_AMI_ORDERFULL_TABLE_NAME = "ami.orderfulltable.Name";
	private static final String ATTR_AMI_EXEC_TABLE_NAME = "ami.tradetable.Name";
	private static final String ATTR_AMI_EXECFULL_TABLE_NAME = "ami.tradefulltable.Name";
	private static final String ATTR_AMI_FIX_MSG_TABLE_NAME = "ami.fixmsgtable.Name";
	private static final String ATTR_AMI_UNSUPPORT_MSG_TABLE_NAME = "ami.exceptiontable.Name";
	private static final String ATTR_AMI_BROKEN_ORDER_TABLE_NAME = "ami.brokenordertable.Name";
	private static final String ATTR_AMI_BROKEN_TRADE_TABLE_NAME = "ami.brokentradetable.Name";
	private static final String ATTR_AMI_HOST = "ami.Host";
	private static final String ATTR_AMI_PORT = "ami.Port";
	private static final String ATTR_AMI_LOGIN = "ami.Login";

	private static final String ATTR_AMI_MSG_SEQUENCE_PER_ORDER = "ami.msg.SequencePerOrder";
	private static final String ATTR_TRACK_LAST_MSG = "ami.fix2ami.TrackLastMessage";
	private static final String ATTR_LOG_ON_ERROR = "ami.logging.OnError";
	private static final String ATTR_LOG_ERROR_AS_INFO = "ami.logging.ErrorAsInfo";
	private static final String ATTR_ORDER_TABLE_RETAIN_TAG = "ami.ordertable.RetainFixTag";
	private static final String ATTR_TRADE_TABLE_RETAIN_TAG = "ami.tradetable.RetainFixTag";
	private static final String ATTR_FIX_MSG_TABLE_COLUMN = "ami.fixmsgtable.ColumnAttributeTag";
	private static final String ATTR_ORDER_STATUS_COLUMN_NAME = "ami.ordertable.OrderStatusColumnName";
	private static final String ATTR_TRADE_STATUS_COLUMN_NAME = "ami.ordertable.TradeStatusColumnName";
	private static final String ATTR_TRADE_BUST_INDICATOR = "ami.status.tradeBustIndicator";
	private static final String ATTR_TRADE_CORRECTION_INDICATOR = "ami.status.tradeCorrectionIndicator";
	private static final String ATTR_AMI_FIX_FIELD_SEPARATOR = "ami.fixmsgtable.FieldSeparator";

	private final String amiOrderTableName;
	private final String amiOrderFullTableName;
	private final String amiExecTableName;
	private final String amiExecFullTableName;
	private final String amiFixMsgTableName;
	private final String amiExceptionMsgTableName;
	private final String amiBrokenOrderTableName;
	private final String amiBrokenTradeTableName;

	private final AmiClient amiClient;

	private final Set<Integer> fixFilter;
	private final String orderStatusColumnName;
	private final String tradeStatusColumnName;

	private final String clOrdIDTagName;
	private final String origClOrdIDTagName;

	private final boolean logOnError;
	private final boolean logErrorAsInfo;
	private final boolean amiMsgSequencePerOrder;
	private final char fixFieldSeparator;

	// message for JUNIT testing.
	private static volatile boolean trackLastMessage = false;
	private static final Map<String, Message> orderMsgs = new ConcurrentHashMap<>();
	private static final Map<String, Message> tradeMsgs = new ConcurrentHashMap<>();
	private static final Map<String, Message> brokenOrderChainMsgs = new ConcurrentHashMap<>();
	private static final Map<String, Message> brokenTradeMsgs = new ConcurrentHashMap<>();
	private static final Map<String, Message> unsupportMsgs = new ConcurrentHashMap<>();
	private static final Map<String, Message> processErrorMsgs = new ConcurrentHashMap<>();

	public AmiPublishProcessor(final PropertyController props) {
		super(Fix2AmiEvent.class, Fix2AmiState.class);

		fixFieldSeparator = props.getOptional(ATTR_AMI_FIX_FIELD_SEPARATOR, '\u0001');
		trackLastMessage = props.getOptional(ATTR_TRACK_LAST_MSG, false);
		logOnError = props.getOptional(ATTR_LOG_ON_ERROR, true);
		logErrorAsInfo = props.getOptional(ATTR_LOG_ERROR_AS_INFO, false);
		amiMsgSequencePerOrder = props.getOptional(ATTR_AMI_MSG_SEQUENCE_PER_ORDER, true);
		AbstractFix2AmiProcessor.addSelectedTags(TAG_FILTER_TYPE.ORDER, AbstractFix2AmiProcessor.splitToSet(",", props.getRequired(ATTR_ORDER_TABLE_RETAIN_TAG)));
		AbstractFix2AmiProcessor.addSelectedTags(TAG_FILTER_TYPE.TRADE, AbstractFix2AmiProcessor.splitToSet(",", props.getRequired(ATTR_TRADE_TABLE_RETAIN_TAG)));
		AbstractFix2AmiProcessor.addSelectedTags(TAG_FILTER_TYPE.FIX, AbstractFix2AmiProcessor.splitToSet(",", props.getRequired(ATTR_FIX_MSG_TABLE_COLUMN)));

		clOrdIDTagName = AbstractFix2AmiProcessor.getDataDictionary().getFieldName(AbstractFix2AmiProcessor.TAG_ClOrdID);
		origClOrdIDTagName = AbstractFix2AmiProcessor.getDataDictionary().getFieldName(AbstractFix2AmiProcessor.TAG_OrigClOrdID);
		fixFilter = AbstractFix2AmiProcessor.getFilter(TAG_FILTER_TYPE.FIX);

		final String orderStatusColumnNameTmp = props.getOptional(ATTR_ORDER_STATUS_COLUMN_NAME);
		if (null != orderStatusColumnNameTmp) {
			AbstractFix2AmiProcessor.setOrderStatusColumnName(orderStatusColumnNameTmp);
			orderStatusColumnName = orderStatusColumnNameTmp;
		} else {
			orderStatusColumnName = AbstractFix2AmiProcessor.getOrderStatusColumnName();
		}

		final String tradeStatusColumnNameTmp = props.getOptional(ATTR_TRADE_STATUS_COLUMN_NAME);
		if (null != tradeStatusColumnNameTmp) {
			AbstractFix2AmiProcessor.setOrderStatusColumnName(tradeStatusColumnNameTmp);
			tradeStatusColumnName = tradeStatusColumnNameTmp;
		} else {
			tradeStatusColumnName = AbstractFix2AmiProcessor.getTradeStatusColumnName();
		}

		final Character tradeBustIndicator = props.getOptional(ATTR_TRADE_BUST_INDICATOR, Character.class);
		if (null != tradeBustIndicator) {
			AbstractFix2AmiProcessor.setTradeBustIndicator(tradeBustIndicator);
		}

		final Character tradeCorrectionIndicator = props.getOptional(ATTR_TRADE_CORRECTION_INDICATOR, Character.class);
		if (null != tradeCorrectionIndicator) {
			AbstractFix2AmiProcessor.setTradeCorrectionIndicator(tradeCorrectionIndicator);
		}

		amiOrderTableName = props.getRequired(ATTR_AMI_ORDER_TABLE_NAME);
		amiOrderFullTableName = props.getRequired(ATTR_AMI_ORDERFULL_TABLE_NAME);
		amiExecTableName = props.getRequired(ATTR_AMI_EXEC_TABLE_NAME);
		amiExecFullTableName = props.getRequired(ATTR_AMI_EXECFULL_TABLE_NAME);
		amiFixMsgTableName = props.getRequired(ATTR_AMI_FIX_MSG_TABLE_NAME);
		amiExceptionMsgTableName = props.getOptional(ATTR_AMI_UNSUPPORT_MSG_TABLE_NAME);
		amiBrokenOrderTableName = props.getOptional(ATTR_AMI_BROKEN_ORDER_TABLE_NAME);
		amiBrokenTradeTableName = props.getOptional(ATTR_AMI_BROKEN_TRADE_TABLE_NAME);

		final String amiHost = props.getRequired(ATTR_AMI_HOST);
		final int amiPort = props.getRequired(ATTR_AMI_PORT, Integer.class);
		final String amiLogin = props.getRequired(ATTR_AMI_LOGIN);

		amiClient = new AmiClient();
		amiClient.start(amiHost, amiPort, amiLogin, AmiClient.ENABLE_QUIET);
	}

	private void putAMessage(final String tableName, final Map<String, AbstractAmiPublishField> msgMap, final String statusChainName, final String statusChain,
			final String msgSequence) {
		if (null != msgMap) {
			LH.fine(log, "publishing to table ", tableName, " sequence ", msgSequence);
			amiClient.startObjectMessage(tableName, msgSequence);

			for (Map.Entry<String, AbstractAmiPublishField> entry : msgMap.entrySet()) {
				entry.getValue().publish(amiClient);
			}

			amiClient.addMessageParamString(statusChainName, statusChain);
			amiClient.sendMessage();
		}
	}
	private void putAMessageFull(final String tableName, final Map<String, AbstractAmiPublishField> msgMap, final String statusChainName, final String statusChain) {
		if (null != msgMap) {
			LH.fine(log, "publishing to table ", tableName);
			amiClient.startObjectMessage(tableName, null);
			for (Map.Entry<String, AbstractAmiPublishField> entry : msgMap.entrySet()) {
				entry.getValue().publish(amiClient);
			}

			amiClient.addMessageParamString(statusChainName, statusChain);
			amiClient.sendMessage();
		}
	}

	@Override
	public void processAction(Fix2AmiEvent event, Fix2AmiState state, ThreadScope threadScope) throws Exception {
		LH.info(log, "Got a Ami Publish request: " + event);

		Message msg = event.getFIXMessage();
		final String fixMsgSequence = String.valueOf(state.getFixMsgSequence());

		if (null != msg) {
			amiClient.startObjectMessage(amiFixMsgTableName, fixMsgSequence);

			for (int tag : fixFilter) {
				try {
					final String tagValue = msg.getString(tag);
					amiClient.addMessageParamString(AbstractFix2AmiProcessor.getDataDictionary().getFieldName(tag), tagValue);
				} catch (FieldNotFound fne) {
					if (logOnError) {
						if (MSG_PROCESS_STATUS.UNSUPPORT_MSG_TYPE != event.getMsgProcessStatus()) {
							LH.info(log, clOrdIDTagName, "(", event.getClOrdID(), ") does not have tag ", tag);
						}
					}
				}
			}

			if (fixFieldSeparator != '\u0001') {
				amiClient.addMessageParamString("rawMessage", msg.toString().replace('\u0001', fixFieldSeparator));
			} else {
				amiClient.addMessageParamString("rawMessage", msg.toString());
			}
			amiClient.sendMessage();
		}

		switch (event.getMsgProcessStatus()) {
			case UNSUPPORT_MSG_TYPE:
			case EXECUTIONREPORT_PROCESSING_ERROR:
				if (null != amiExceptionMsgTableName) {
					amiClient.startObjectMessage(amiExceptionMsgTableName, fixMsgSequence);
					if (fixFieldSeparator != '\u0001') {
						amiClient.addMessageParamString("RawMessage", msg.toString().replace('\u0001', fixFieldSeparator));
					} else {
						amiClient.addMessageParamString("RawMessage", msg.toString());
					}
					amiClient.addMessageParamString("ErrorCode", event.getMsgProcessStatus().name());
					amiClient.addMessageParamString(clOrdIDTagName, event.getClOrdID() == null ? "" : event.getClOrdID());
					amiClient.addMessageParamString(origClOrdIDTagName, event.getOrigClOrdID() == null ? "" : event.getOrigClOrdID());
					amiClient.sendMessage();
				}
				if (logOnError) {
					if (logErrorAsInfo) {
						LH.info(log, "processing error (", event.getMsgProcessStatus().name(), "): ", clOrdIDTagName, "=", event.getClOrdID(), " ", origClOrdIDTagName, "=",
								event.getOrigClOrdID(), " msg=", msg.toString());
					} else {
						LH.warning(log, "processing error (", event.getMsgProcessStatus().name(), "): ", clOrdIDTagName, "=", event.getClOrdID(), " ", origClOrdIDTagName, "=",
								event.getOrigClOrdID(), " msg=", msg.toString());
					}
				}
				trackLastMsg(event);
				break;
			case BROKEN_ORDER_CHAIN:
				if (null != amiBrokenOrderTableName) {
					putAMessage(amiBrokenOrderTableName, event.getAmiOrderMsg(), orderStatusColumnName, event.getOrderStatusChain(), fixMsgSequence);
				}

				if (null != amiBrokenTradeTableName) {
					putAMessage(amiBrokenTradeTableName, event.getAmiExecMsg(), tradeStatusColumnName, event.getOrderStatusChain(), fixMsgSequence);
				}

				if (logOnError) {
					if (logErrorAsInfo) {
						LH.info(log, "broken order chain ", clOrdIDTagName, "=", event.getClOrdID(), " ", origClOrdIDTagName, "=", event.getOrigClOrdID(), " msg=", msg.toString());
					} else {
						LH.warning(log, "broken order chain ", clOrdIDTagName, "=", event.getClOrdID(), " ", origClOrdIDTagName, "=", event.getOrigClOrdID(), " msg=",
								msg.toString());
					}
				}
				trackLastMsg(event);
				break;
			case NO_ERROR:
			default:
				final String amiMsgSequence = String.valueOf(state.getAmiMsgSequence(event.getOrigClOrdID(), amiMsgSequencePerOrder));
				putAMessageFull(amiOrderFullTableName, event.getAmiOrderMsg(), orderStatusColumnName, event.getOrderStatusChain());
				putAMessageFull(amiExecFullTableName, event.getAmiExecMsg(), tradeStatusColumnName, event.getTradeStatusChain());
				putAMessage(amiOrderTableName, event.getAmiOrderMsg(), orderStatusColumnName, event.getOrderStatusChain(), amiMsgSequence);
				putAMessage(amiExecTableName, event.getAmiExecMsg(), tradeStatusColumnName, event.getTradeStatusChain(), amiMsgSequence);
				trackLastMsg(event);
				break;
		}
		amiClient.flush();
	}

	private void trackLastMsg(final Fix2AmiEvent event) {
		if (trackLastMessage) {
			switch (event.getMsgProcessStatus()) {
				case UNSUPPORT_MSG_TYPE:
					unsupportMsgs.put(event.getClOrdID(), event.getFIXMessage());
					LH.info(log, "UNSUPPORT_MSG_TYPE clOrdID: ", event.getClOrdID(), " unsupportMsg: ", event.getFIXMessage());
					break;
				case EXECUTIONREPORT_PROCESSING_ERROR:
					processErrorMsgs.put(event.getClOrdID(), event.getFIXMessage());
					LH.info(log, "EXECUTIONREPORT_PROCESSING_ERROR clOrdID: ", event.getClOrdID(), " processErrorMsg: ", event.getFIXMessage());
					break;
				case BROKEN_ORDER_CHAIN:
					if (null != event.getAmiOrderMsg()) {
						brokenOrderChainMsgs.put(event.getClOrdID(), event.getFIXMessage());
					}

					if (null != event.getAmiExecMsg()) {
						brokenTradeMsgs.put(event.getClOrdID(), event.getFIXMessage());
					}
					break;
				case NO_ERROR:
					if (null != event.getAmiOrderMsg()) {
						orderMsgs.put(event.getClOrdID(), event.getFIXMessage());
					}

					if (null != event.getAmiExecMsg()) {
						tradeMsgs.put(event.getClOrdID(), event.getFIXMessage());
					}
					break;
			}
		}
	}

	public static Message getlastMsg(final MSG_PROCESS_STATUS statusType, final String clOrdID, final String orderOrTrade) {
		switch (statusType) {
			case UNSUPPORT_MSG_TYPE:
				return unsupportMsgs.get(clOrdID);
			case EXECUTIONREPORT_PROCESSING_ERROR:
				return processErrorMsgs.get(clOrdID);
			case BROKEN_ORDER_CHAIN:
				if (orderOrTrade.equalsIgnoreCase("ORDER")) {
					return brokenOrderChainMsgs.get(clOrdID);
				} else {
					return brokenTradeMsgs.get(clOrdID);
				}
			case NO_ERROR:
				if (orderOrTrade.equalsIgnoreCase("ORDER")) {
					return orderMsgs.get(clOrdID);
				} else {
					return tradeMsgs.get(clOrdID);
				}
		}
		return null;
	}

	public static boolean getLastTrackLastMessage() {
		return trackLastMessage;
	}

}
