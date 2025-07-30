package com.f1.qfix;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import quickfix.DefaultMessageFactory;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.SessionNotFound;
import quickfix.field.AvgPx;
import quickfix.field.BeginString;
import quickfix.field.BodyLength;
import quickfix.field.ClOrdID;
import quickfix.field.ContraBroker;
import quickfix.field.CumQty;
import quickfix.field.Currency;
import quickfix.field.CxlRejReason;
import quickfix.field.CxlRejResponseTo;
import quickfix.field.DeliverToCompID;
import quickfix.field.DeliverToLocationID;
import quickfix.field.DeliverToSubID;
import quickfix.field.ExDestination;
import quickfix.field.ExecBroker;
import quickfix.field.ExecID;
import quickfix.field.ExecInst;
import quickfix.field.ExecRefID;
import quickfix.field.ExecRestatementReason;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.HandlInst;
import quickfix.field.IDSource;
import quickfix.field.LastMkt;
import quickfix.field.LastMsgSeqNumProcessed;
import quickfix.field.LastPx;
import quickfix.field.LastShares;
import quickfix.field.LeavesQty;
import quickfix.field.LocateReqd;
import quickfix.field.MessageEncoding;
import quickfix.field.MsgSeqNum;
import quickfix.field.OnBehalfOfCompID;
import quickfix.field.OnBehalfOfLocationID;
import quickfix.field.OnBehalfOfSendingTime;
import quickfix.field.OnBehalfOfSubID;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.OrderCapacity;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;
import quickfix.field.OrigSendingTime;
import quickfix.field.PossDupFlag;
import quickfix.field.PossResend;
import quickfix.field.Price;
import quickfix.field.Rule80A;
import quickfix.field.SecureDataLen;
import quickfix.field.SecurityID;
import quickfix.field.SenderCompID;
import quickfix.field.SenderLocationID;
import quickfix.field.SenderSubID;
import quickfix.field.SendingTime;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.SymbolSfx;
import quickfix.field.TargetCompID;
import quickfix.field.TargetLocationID;
import quickfix.field.TargetSubID;
import quickfix.field.Text;
import quickfix.field.TimeInForce;
import quickfix.field.TransactTime;
import quickfix.field.XmlData;
import quickfix.field.XmlDataLen;
import quickfix.fix42.ExecutionReport.NoContraBrokers;

import com.f1.base.DateNanos;
import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.container.impl.BasicState;
import com.f1.pofo.fix.ChildMessage;
import com.f1.pofo.fix.ChildOrderId;
import com.f1.pofo.fix.FixCancelRequest;
import com.f1.pofo.fix.FixExecutionReport;
import com.f1.pofo.fix.FixMsg;
import com.f1.pofo.fix.FixOrderReplaceReject;
import com.f1.pofo.fix.FixOrderReplaceRequest;
import com.f1.pofo.fix.FixOrderRequest;
import com.f1.pofo.fix.FixReport;
import com.f1.pofo.fix.FixRequest;
import com.f1.pofo.fix.FixStatusReport;
import com.f1.pofo.fix.MsgType;
import com.f1.pofo.fix.child.FixChildOrderRequest;
import com.f1.pofo.fix.child.FixChildStatusReport;
import com.f1.qfix.msg.FixMsgConnection;
import com.f1.qfix.msg.FixMsgEvent;
import com.f1.qfix.msg.FixMsgOutputTopic;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.Formatter;
import com.f1.utils.GuidHelper;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.MH;
import com.f1.utils.SH;
import com.f1.utils.structs.IntSet;

public class QfixOutboundProcessor extends BasicProcessor<FixMsg, BasicState> {

	MessageFactory messageFactory = new DefaultMessageFactory();

	private OutputPort<FixEvent> output;
	public final OutputPort<FixMsg> reject;
	final IntSet passThru = new IntSet();

	private String outboundSessionName;
	private int brokerTag;
	private FixMsgConnection fixConnection;
	private FixMsgOutputTopic outboundSession;
	private boolean sendCxlExDestination = true;
	private boolean sendCxlCurrency = true;

	public QfixOutboundProcessor() {
		super(FixMsg.class, BasicState.class);
		output = newOutputPort(FixEvent.class);
		reject = newOutputPort(FixMsg.class);
		output.setConnectionOptional(true);
		reject.setConnectionOptional(true);
	}

	@Override
	public void start() {
		super.start();
		fixConnection = (FixMsgConnection) getServices().getMsgManager().getConnection("FIX");
		Properties props = getServices().getPropertyController().getProperties();
		this.brokerTag = getTools().getOptional(QfixProperties.OPTION_FIX_TARGET_BROKER_TAG, 115);
		this.sendCxlExDestination = getTools().getOptional("cxl.send.exdestination", true);
		this.sendCxlCurrency = getTools().getOptional("cxl.send.currency", true);
		outboundSessionName = props.getProperty(QfixProperties.OPTION_FIX_OUTBOUND_SESSION);
		if (outboundSessionName == null)
			throw new RuntimeException("Child Order Session not defined");
		if (!outboundSessionName.equals("\"NONE\""))//TODO: this is crazy
			outboundSession = fixConnection.getOutputTopic(outboundSessionName);
		else
			log.info("Special session name NONE indicates no outbound session!");
	}

	public void toFixExecutionReport(FixExecutionReport report, quickfix.Message msg) {
		if (report.getExecution() == null) {
			setInt(msg, ExecID.FIELD, 0);// put a GUID here
		} else {
			if (report.getExecution().getExecTransType() > 0) {
				setInt(msg, ExecTransType.FIELD, report.getExecution().getExecTransType());
				setString(msg, ExecRefID.FIELD, report.getExecution().getExecRefID());
			}
			setString(msg, ExecID.FIELD, report.getExecution().getId().toString());
			setInt(msg, LastShares.FIELD, report.getExecution().getExecQty());
			setDouble(msg, LastPx.FIELD, report.getExecution().getExecPx());
			setString(msg, ExecBroker.FIELD, report.getExecution().getExecBroker());
			setString(msg, TransactTime.FIELD, toString(report.getExecution().getExecTime().toDate()));
			if (report.getExecution().getExecBroker() != null)
				setString(msg, ExecBroker.FIELD, report.getExecution().getExecBroker());
			if (report.getExecution().getContraBroker() != null) {
				NoContraBrokers repeatingGroup = new quickfix.fix42.ExecutionReport.NoContraBrokers();
				repeatingGroup.set(new ContraBroker(report.getExecution().getContraBroker()));
				msg.addGroup(repeatingGroup);
			}
			if (report.getExecution().getLastMkt() != null)
				setString(msg, LastMkt.FIELD, report.getExecution().getLastMkt());
		}

		setString(msg, OrderID.FIELD, report.getExecution().getOrderId());
	}

	private void mapTags(Map<Integer, String> passThru, Message msg) {
		if (CH.isntEmpty(passThru))
			for (final Entry<Integer, String> entry : passThru.entrySet())
				setString(msg, entry.getKey(), entry.getValue());
	}

	@Override
	public void processAction(FixMsg action, BasicState state, ThreadScope threadScope) throws Exception {
		quickfix.Message msg = messageFactory.create("FIX.4.2", SH.toString((char) action.getType().getEnumValue()));
		boolean response = false;
		switch (action.getType()) {
			case EXECUTION_REPORT: {
				toFixReport((FixReport) action, msg);
				if (action instanceof FixExecutionReport) {
					toFixExecutionReport((FixExecutionReport) action, msg);
				} else {
					toFixStatusReport((FixStatusReport) action, msg);
				}
				mapTags(((FixReport) action).getPassThruTags(), msg);
				response = true;
			}
				break;
			case CANCEL_REQUEST: {
				toFixCancelRequest((FixCancelRequest) action, msg);
				response = false;
			}
				break;
			case REPLACE_REQUEST: {
				toFixReplaceRequest((FixOrderReplaceRequest) action, msg);
				response = false;
				break;
			}
			case NEW_ORDER_SINGLE: {
				toFixNewOrderSingle((FixOrderRequest) action, msg);
				response = false;
			}
				break;
			case CANCEL_REJECT: {
				toFixCxlReject((FixOrderReplaceReject) action, msg);
				response = true;
			}
				break;
		}
		try {
			if (response) {
				FixMsgOutputTopic outbound = fixConnection.getOutputTopic(action.getSessionName());
				FixMsgEvent event = outbound.createMessage();
				event.setMessage(msg);
				event.setSessionName(action.getSessionName());
				outbound.send(event);
			} else {
				msg.getHeader().setString(brokerTag, action.getSessionName());

				FixMsgEvent event = outboundSession.createMessage();
				event.setMessage(msg);
				event.setSessionName(outboundSessionName);
				outboundSession.send(event);
			}
		} catch (Exception e) {
			if (action instanceof FixRequest) {
				switch (action.getType()) {
					case CANCEL_REQUEST: {
						/*
						 * FixOrderReplaceReject
						 * reject=nw(FixOrderReplaceReject.class);
						 * reject.setRootOrderId(action.getRootOrderId());
						 * reject.setResponseTo(responseTo)
						 */
					}
						break;
					case REPLACE_REQUEST: {

					}
						break;
					case NEW_ORDER_SINGLE: {
						if (action instanceof ChildMessage) {
							FixChildOrderRequest request = (FixChildOrderRequest) action;
							FixChildStatusReport report = nw(FixChildStatusReport.class);
							report.setChildId(request.getChildId());
							report.setExecType(com.f1.pofo.fix.ExecType.REJECTED);
							report.setOrdStatus(com.f1.pofo.fix.OrdStatus.REJECTED.getIntMask());
							report.setTransactTime(new DateNanos(EH.currentTimeNanos()));
							report.setSessionName(action.getSessionName());
							report.setType(MsgType.EXECUTION_REPORT);
							report.setRootOrderId(action.getRootOrderId());
							report.setText("START: Error sending Order");
							reject.send(report, threadScope);
						}
					}

				}
			}
			throw e;
		}
	}

	private void toFixReplaceRequest(FixOrderReplaceRequest action, Message msg) {
		setString(msg, Symbol.FIELD, action.getSymbol());
		if (action.getSymbolSfx() != null)
			setString(msg, SymbolSfx.FIELD, action.getSymbolSfx());

		if (action.getRule80A() != null)
			setString(msg, Rule80A.FIELD, action.getRule80A());

		if (action.getOrderCapacity() != null)
			setString(msg, OrderCapacity.FIELD, action.getOrderCapacity());

		if (action.getLocateBrokerRequired() != null)
			setString(msg, LocateReqd.FIELD, action.getLocateBrokerRequired() ? "Y" : "N");

		if (action.getSenderSubId() != null)
			msg.getHeader().setString(SenderSubID.FIELD, action.getSenderSubId());

		if (action.getOnBehalfOfCompId() != null)
			msg.getHeader().setString(OnBehalfOfCompID.FIELD, action.getOnBehalfOfCompId());

		if (action.getLocateBroker() != null)
			setString(msg, 5700, action.getLocateBroker());
		if (action.getLocateId() != null)
			setString(msg, 5701, action.getLocateId());

		if (action.getDestination() != null)
			setString(msg, ExDestination.FIELD, action.getDestination());

		if (action.getOrderInfo().getCurrency() != null)
			setString(msg, Currency.FIELD, action.getOrderInfo().getCurrency());

		if (action instanceof ChildMessage) {
			ChildOrderId cid = ((ChildMessage) action).getChildId();
			String requestId = cid.getOrderId() + "-" + cid.getRequestId();
			String origRequestId = cid.getOrderId() + "-" + cid.getOrderRevisionId();
			setString(msg, ClOrdID.FIELD, requestId);
			setString(msg, OrigClOrdID.FIELD, origRequestId);
		} else {
			setString(msg, ClOrdID.FIELD, action.getRequestId());
			setString(msg, OrigClOrdID.FIELD, action.getRefId());
		}
		setInt(msg, OrderQty.FIELD, action.getOrderInfo().getOrderQty());
		setDouble(msg, Price.FIELD, action.getOrderInfo().getLimitPx());
		setChar(msg, Side.FIELD, action.getOrderInfo().getSide().getEnumValue());
		setChar(msg, TimeInForce.FIELD, com.f1.pofo.oms.TimeInForce.getTIF(action.getOrderInfo().getOrderType(), action.getOrderInfo().getTimeInForce()));
		setChar(msg, OrdType.FIELD, com.f1.pofo.oms.OrderType.getOrdType(action.getOrderInfo().getOrderType(), action.getOrderInfo().getTimeInForce()));
		setString(msg, TransactTime.FIELD, toString(getTools().getNowDate()));
		if (action.getOrderInfo().getExecInstructions() != null) {
			setString(msg, ExecInst.FIELD, action.getOrderInfo().getExecInstructions());
		}

		// Default tags:
		setChar(msg, HandlInst.FIELD, HandlInst.AUTOMATED_EXECUTION_ORDER_PUBLIC);
		mapTags(action.getOrderInfo().getPassThruTags(), msg);
	}

	private String toString(Date nd) {
		Formatter formatter = getTools().getThreadScope().getFormatter().getDateFormatter(LocaleFormatter.DATETIME_FULL);
		return formatter.format(nd);
	}

	private void toFixStatusReport(FixStatusReport report, Message msg) {
		if (report.getExecTransType() != 0)
			setInt(msg, ExecTransType.FIELD, report.getExecTransType());
		setString(msg, ExecID.FIELD, GuidHelper.getGuid(62));
		setString(msg, OrderID.FIELD, report.getOrderId());
		setString(msg, TransactTime.FIELD, toString(getTools().getNowDate()));
		setInt(msg, LastShares.FIELD, 0);
		setDouble(msg, LastPx.FIELD, 0d);
		setString(msg, OrigClOrdID.FIELD, report.getOrigId());
	}

	private void toFixReport(FixReport report, Message msg) {
		setInt(msg, ExecTransType.FIELD, 0);
		if (report.getPosResend()) {
			msg.getHeader().setString(PossResend.FIELD, "Y");
		}

		setString(msg, Symbol.FIELD, report.getSymbol());
		setString(msg, SymbolSfx.FIELD, report.getSymbolSfx());

		if (report.getSecurityID() != null) {
			setString(msg, SecurityID.FIELD, report.getSecurityID());
			setInt(msg, IDSource.FIELD, report.getIDType());
		}
		com.f1.pofo.oms.OrderType ordType = report.getOrderType();
		setInt(msg, OrderQty.FIELD, report.getOrderQty());
		if (ordType != com.f1.pofo.oms.OrderType.MARKET) {
			setDouble(msg, Price.FIELD, report.getLimitPx());
		} else
			setDouble(msg, Price.FIELD, 0d);
		setChar(msg, OrdType.FIELD, com.f1.pofo.oms.OrderType.getOrdType(ordType, report.getTimeInForce()));

		setString(msg, Currency.FIELD, report.getCurrency());
		setString(msg, Text.FIELD, report.getText());
		setChar(msg, Side.FIELD, report.getSide().getEnumValue());
		setChar(msg, TimeInForce.FIELD, com.f1.pofo.oms.TimeInForce.getTIF(ordType, report.getTimeInForce()));
		setChar(msg, OrdStatus.FIELD, com.f1.pofo.fix.OrdStatus.get(MH.indexOfLastBitSet(report.getOrdStatus())).getFixOrdStatus());
		setInt(msg, CumQty.FIELD, report.getCumQty());
		if (report.getCumQty() > 0) {
			setDouble(msg, AvgPx.FIELD, report.getExecValue() / report.getCumQty());
		} else {
			setString(msg, AvgPx.FIELD, "0");
		}
		setInt(msg, LeavesQty.FIELD, report.getOrderQty() - report.getCumQty());
		setChar(msg, ExecType.FIELD, report.getExecType().getEnumValue());
		if (report.getExecType().getEnumValue() == ExecType.RESTATED)
			msg.setInt(ExecRestatementReason.FIELD, report.getRestatementReason());

		setString(msg, ClOrdID.FIELD, report.getRequestId());
		// Fix Tag Munging happens here I guess??
	}
	private void toFixCxlReject(FixOrderReplaceReject action, Message msg) {
		setString(msg, ClOrdID.FIELD, action.getRequestID());
		setString(msg, OrigClOrdID.FIELD, action.getRefId());
		setInt(msg, CxlRejResponseTo.FIELD, action.getResponseTo());
		setInt(msg, CxlRejReason.FIELD, action.getReason());
		setString(msg, OrderID.FIELD, action.getOrderID());
		setChar(msg, OrdStatus.FIELD, com.f1.pofo.fix.OrdStatus.get(MH.indexOfLastBitSet(action.getOrderStatus())).getFixOrdStatus());
		setString(msg, Text.FIELD, action.getText());
	}
	private void toFixCancelRequest(FixCancelRequest action, Message msg) {
		setString(msg, Symbol.FIELD, action.getSymbol());
		setString(msg, SymbolSfx.FIELD, action.getSymbolSfx());
		setString(msg, Rule80A.FIELD, action.getRule80A());

		setString(msg, OrderCapacity.FIELD, action.getOrderCapacity());

		if (action.getLocateBrokerRequired() != null)
			setString(msg, LocateReqd.FIELD, action.getLocateBrokerRequired() ? "Y" : "N");
		setString(msg, 5700, action.getLocateBroker());
		setString(msg, 5701, action.getLocateId());
		if (action.getSenderSubId() != null)
			msg.getHeader().setString(SenderSubID.FIELD, action.getSenderSubId());
		if (action.getOnBehalfOfCompId() != null)
			msg.getHeader().setString(OnBehalfOfCompID.FIELD, action.getOnBehalfOfCompId());
		if (this.sendCxlExDestination)
			setString(msg, ExDestination.FIELD, action.getDestination());
		if (this.sendCxlCurrency)
			setString(msg, Currency.FIELD, action.getOrderInfo().getCurrency());
		if (action instanceof ChildMessage) {
			ChildOrderId cid = ((ChildMessage) action).getChildId();
			String requestId = cid.getOrderId() + "-" + cid.getRequestId();
			String origRequestId = cid.getOrderId() + "-" + cid.getOrderRevisionId();
			setString(msg, ClOrdID.FIELD, requestId);
			setString(msg, OrigClOrdID.FIELD, origRequestId);
		} else {
			setString(msg, ClOrdID.FIELD, action.getRequestId());
			setString(msg, OrigClOrdID.FIELD, action.getRefId());
		}
		setInt(msg, OrderQty.FIELD, action.getOrderInfo().getOrderQty());
		setChar(msg, Side.FIELD, action.getOrderInfo().getSide().getEnumValue());
		setString(msg, TransactTime.FIELD, toString(new Date()));
		mapTags(action.getOrderInfo().getPassThruTags(), msg);
	}

	private void toFixNewOrderSingle(FixOrderRequest action, Message msg) throws SessionNotFound {
		setString(msg, Symbol.FIELD, action.getSymbol());
		setString(msg, SymbolSfx.FIELD, action.getSymbolSfx());
		setString(msg, Rule80A.FIELD, action.getRule80A());

		setString(msg, OrderCapacity.FIELD, action.getOrderCapacity());

		if (action.getLocateBrokerRequired() != null)
			setString(msg, LocateReqd.FIELD, action.getLocateBrokerRequired() ? "Y" : "N");
		setString(msg, 5700, action.getLocateBroker());
		setString(msg, 5701, action.getLocateId());
		if (action.getSenderSubId() != null)
			msg.getHeader().setString(SenderSubID.FIELD, action.getSenderSubId());
		if (action.getOnBehalfOfCompId() != null)
			msg.getHeader().setString(OnBehalfOfCompID.FIELD, action.getOnBehalfOfCompId());

		setString(msg, Currency.FIELD, action.getOrderInfo().getCurrency());
		setString(msg, ExDestination.FIELD, action.getDestination());
		if (action instanceof ChildMessage) {
			ChildOrderId cid = ((ChildMessage) action).getChildId();
			String requestId = cid.getOrderId() + "-" + cid.getRequestId();
			setString(msg, ClOrdID.FIELD, requestId);
		} else
			setString(msg, ClOrdID.FIELD, action.getRequestId());
		setInt(msg, OrderQty.FIELD, action.getOrderInfo().getOrderQty());
		setDouble(msg, Price.FIELD, action.getOrderInfo().getLimitPx());
		setChar(msg, Side.FIELD, action.getOrderInfo().getSide().getEnumValue());
		setString(msg, TransactTime.FIELD, toString(new Date()));
		setChar(msg, TimeInForce.FIELD, com.f1.pofo.oms.TimeInForce.getTIF(action.getOrderInfo().getOrderType(), action.getOrderInfo().getTimeInForce()));
		setChar(msg, OrdType.FIELD, com.f1.pofo.oms.OrderType.getOrdType(action.getOrderInfo().getOrderType(), action.getOrderInfo().getTimeInForce()));
		setString(msg, ExecInst.FIELD, action.getOrderInfo().getExecInstructions());
		// Default tags:
		setChar(msg, HandlInst.FIELD, HandlInst.AUTOMATED_EXECUTION_ORDER_PUBLIC);
		mapTags(action.getOrderInfo().getPassThruTags(), msg);
	}

	private void setInt(Message msg, int field, int value) {
		try {
			msg.setInt(field, value);
		} catch (Exception e) {
			throw new RuntimeException("Error setting fix tag " + field + " to: " + value, e);
		}
	}

	private void setDouble(Message msg, int field, double value) {
		try {
			msg.setDouble(field, value);
		} catch (Exception e) {
			throw new RuntimeException("Error setting fix tag " + field + " to: " + value, e);
		}

	}

	private void setString(Message msg, int field, String value) {
		if (value == null)
			return;
		try {
			if (isHeaderField(field))
				msg.getHeader().setString(field, value);
			else
				msg.setString(field, value);
		} catch (Exception e) {
			throw new RuntimeException("Error setting fix tag " + field + " to: " + value, e);
		}
	}

	private void setChar(Message msg, int field, Character value) {
		if (value == null)
			return;
		try {
			msg.setChar(field, value);
		} catch (Exception e) {
			throw new RuntimeException("Error setting fix tag " + field + " to: " + value, e);
		}
	}
	private void setChar(Message msg, int field, char value) {
		try {
			msg.setChar(field, value);
		} catch (Exception e) {
			throw new RuntimeException("Error setting fix tag " + field + " to: " + value, e);
		}
	}
	static boolean isHeaderField(int field) {
		switch (field) {
			case BeginString.FIELD:
			case BodyLength.FIELD:
			case SenderCompID.FIELD:
			case TargetCompID.FIELD:
			case OnBehalfOfCompID.FIELD:
			case DeliverToCompID.FIELD:
			case SecureDataLen.FIELD:
			case MsgSeqNum.FIELD:
			case SenderSubID.FIELD:
			case SenderLocationID.FIELD:
			case TargetSubID.FIELD:
			case TargetLocationID.FIELD:
			case OnBehalfOfSubID.FIELD:
			case OnBehalfOfLocationID.FIELD:
			case DeliverToSubID.FIELD:
			case DeliverToLocationID.FIELD:
			case PossDupFlag.FIELD:
			case PossResend.FIELD:
			case SendingTime.FIELD:
			case OrigSendingTime.FIELD:
			case XmlDataLen.FIELD:
			case XmlData.FIELD:
			case MessageEncoding.FIELD:
			case LastMsgSeqNumProcessed.FIELD:
			case OnBehalfOfSendingTime.FIELD:
				return true;
			default:
				return false;
		}
	}

}
