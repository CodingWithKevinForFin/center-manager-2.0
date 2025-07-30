package com.f1.qfix;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import quickfix.Field;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.Message.Header;
import quickfix.field.Account;
import quickfix.field.AvgPx;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.Currency;
import quickfix.field.CxlRejReason;
import quickfix.field.CxlRejResponseTo;
import quickfix.field.ExDestination;
import quickfix.field.ExecBroker;
import quickfix.field.ExecID;
import quickfix.field.ExecInst;
import quickfix.field.ExecRefID;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.IDSource;
import quickfix.field.LastPx;
import quickfix.field.LastShares;
import quickfix.field.LocateReqd;
import quickfix.field.OnBehalfOfCompID;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.OrderCapacity;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;
import quickfix.field.PossDupFlag;
import quickfix.field.PossResend;
import quickfix.field.Price;
import quickfix.field.Rule80A;
import quickfix.field.SecurityID;
import quickfix.field.SenderSubID;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.SymbolSfx;
import quickfix.field.Text;
import quickfix.field.TimeInForce;
import quickfix.field.TransactTime;

import com.f1.base.DateNanos;
import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.pofo.fix.ChildMessage;
import com.f1.pofo.fix.ChildOrderId;
import com.f1.pofo.fix.FixCancelRequest;
import com.f1.pofo.fix.FixExecutionReport;
import com.f1.pofo.fix.FixMsg;
import com.f1.pofo.fix.FixOrderInfo;
import com.f1.pofo.fix.FixOrderReplaceRequest;
import com.f1.pofo.fix.FixOrderRequest;
import com.f1.pofo.fix.FixReport;
import com.f1.pofo.fix.FixRequest;
import com.f1.pofo.fix.FixStatusReport;
import com.f1.pofo.fix.MsgType;
import com.f1.pofo.fix.child.FixChildExecutionReport;
import com.f1.pofo.fix.child.FixChildReplaceReject;
import com.f1.pofo.fix.child.FixChildStatusReport;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.Order;
import com.f1.utils.Formatter;
import com.f1.utils.LH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.SH;
import com.f1.utils.structs.IntSet;

public class QfixInboundProcessor extends BasicProcessor<FixEvent, FixState> {
	private final IntSet newOrderRetainFields = new IntSet();
	private final IntSet replaceOrderRetainFields = new IntSet();
	private final IntSet cancelOrderRetainFields = new IntSet();
	private final IntSet executionReportRetainFields = new IntSet();

	ObjectGeneratorForClass<FixOrderRequest> nog;
	ObjectGeneratorForClass<Order> nfog;
	ObjectGeneratorForClass<FixOrderInfo> oinfog;
	ObjectGeneratorForClass<FixCancelRequest> cog;
	ObjectGeneratorForClass<FixOrderReplaceRequest> org;
	ObjectGeneratorForClass<FixChildExecutionReport> eog;
	ObjectGeneratorForClass<FixChildStatusReport> sog;
	ObjectGeneratorForClass<FixChildReplaceReject> cxlrejg;
	ObjectGeneratorForClass<Execution> execg;
	ObjectGeneratorForClass<ChildOrderId> coidg;
	public final OutputPort<FixMsg> output;
	private int brokerTag;

	public QfixInboundProcessor() {
		super(FixEvent.class, FixState.class);
		output = newOutputPort(FixMsg.class);
	}

	public void addNewOrderRetainTag(int tag) {
		assertNotStarted();
		newOrderRetainFields.add(tag);
	}
	public void addReplaceOrderRetainTag(int tag) {
		assertNotStarted();
		replaceOrderRetainFields.add(tag);
	}
	public void addCancelOrderRetainTag(int tag) {
		assertNotStarted();
		cancelOrderRetainFields.add(tag);
	}
	public void addExecutionReportRetainTag(int tag) {
		assertNotStarted();
		executionReportRetainFields.add(tag);
	}

	@Override
	public void init() {
		getContainer()
				.getServices()
				.getGenerator()
				.register(FixMsg.class, FixRequest.class, FixOrderRequest.class, Order.class, FixOrderInfo.class, FixCancelRequest.class, FixOrderReplaceRequest.class,
						FixExecutionReport.class, FixStatusReport.class, Execution.class);
		super.init();
	}

	@Override
	public void start() {
		super.start();
		this.brokerTag = getTools().getOptional(QfixProperties.OPTION_FIX_TARGET_BROKER_TAG, 115);
		getContainer()
				.getServices()
				.getGenerator()
				.register(FixMsg.class, FixRequest.class, FixOrderRequest.class, Order.class, FixOrderInfo.class, FixCancelRequest.class, FixOrderReplaceRequest.class,
						FixExecutionReport.class, FixStatusReport.class, Execution.class);
		nog = getGenerator(FixOrderRequest.class);
		nfog = getGenerator(Order.class);
		oinfog = getGenerator(FixOrderInfo.class);
		cog = getGenerator(FixCancelRequest.class);
		org = getGenerator(FixOrderReplaceRequest.class);
		eog = getGenerator(FixChildExecutionReport.class);
		execg = getGenerator(Execution.class);
		sog = getGenerator(FixChildStatusReport.class);
		coidg = getGenerator(ChildOrderId.class);
		cxlrejg = getGenerator(FixChildReplaceReject.class);

		final Properties props = getServices().getPropertyController().getProperties();

		//final String CommonRetainFields = props.getProperty(QfixProperties.OPTION_FIX_COMMON_RETAIN_FIELDS);
		//final String NewRetainFields = props.getProperty(QfixProperties.OPTION_FIX_NEW_ORDER_RETAIN_FIELDS);
		//final String CancelRetainFields = props.getProperty(QfixProperties.OPTION_FIX_CANCEL_RETAIN_FIELDS);
		//final String ReplaceRetainFields = props.getProperty(QfixProperties.OPTION_FIX_REPLACE_RETAIN_FIELDS);
		//final String ExecutionRetainFields = props.getProperty(QfixProperties.OPTION_FIX_EXECUTION_RETAIN_FIELDS);

		//populateFields(newOrderRetainFields, NewRetainFields);
		//populateFields(cancelOrderRetainFields, CancelRetainFields);
		//populateFields(replaceOrderRetainFields, ReplaceRetainFields);
		//populateFields(executionReportRetainFields, ExecutionRetainFields);

		//final IntSet common = new IntSet();
		//populateFields(common, CommonRetainFields);
		//if (common.size() > 0) {
		//newOrderRetainFields.addAll(common);
		//cancelOrderRetainFields.addAll(common);
		//replaceOrderRetainFields.addAll(common);
		//executionReportRetainFields.addAll(common);
		//}
	}

	//public static final void populateFields(IntSet fields, String fieldsString) {
	//if (fieldsString == null)
	//return;
	//for (String s : SH.split(',', fieldsString))
	//fields.add(Integer.parseInt(s));
	//}

	@Override
	public void processAction(FixEvent action, FixState state, ThreadScope threadScope) throws Exception {
		quickfix.Message msg = action.getMessage();
		String msgTypeStr = msg.getHeader().getString(MsgType.getTagValue());
		MsgType msgType = MsgType.get(SH.parseChar(msgTypeStr));
		if (msgType == null) {
			LH.severe(log, "Unknown message type received ", msgTypeStr);
			return;
		}
		Map<Integer, String> passThru = new HashMap<Integer, String>();
		FixMsg evt = null;
		switch (msgType) {
			case NEW_ORDER_SINGLE: {
				FixOrderRequest req = nog.nw();
				handlePosDup(msg, req);
				FixOrderInfo oinfo = oinfog.nw();
				oinfo.setPassThruTags(new HashMap<Integer, String>());
				req.setSessionName(action.getSessionName());
				req.setOrderInfo(oinfo);
				req.setType(MsgType.NEW_ORDER_SINGLE);
				Iterator<Field<?>> iter = msg.iterator();
				if (msg.getHeader().isSetField(SenderSubID.FIELD)) {
					String senderSubId = msg.getHeader().getString(SenderSubID.FIELD);
					req.setSenderSubId(senderSubId);
				}
				if (msg.getHeader().isSetField(OnBehalfOfCompID.FIELD)) {
					req.setOnBehalfOfCompId(msg.getHeader().getString(OnBehalfOfCompID.FIELD));
				}
				processHeaderPassthroughs(passThru, newOrderRetainFields, msg);
				while (iter.hasNext()) {
					Field<?> f = iter.next();
					switch (f.getTag()) {
						case Symbol.FIELD:
							req.setSymbol(toString(f));
							break;
						case SymbolSfx.FIELD:
							req.setSymbolSfx(toString(f));
							break;
						case OrderCapacity.FIELD:
							req.setOrderCapacity(toString(f));
							break;
						case Rule80A.FIELD:
							req.setRule80A(toString(f));
							break;
						case LocateReqd.FIELD:
							req.setLocateBrokerRequired("Y".equals(toString(f)));
							break;
						case 5700:
							req.setLocateBroker(toString(f));
							break;
						case 5701:
							req.setLocateId(toString(f));
							break;
						case SecurityID.FIELD:
							req.setSecurityID(toString(f));
							break;
						case IDSource.FIELD:
							req.setIDType(toInt(f));
							break;
						case ExDestination.FIELD:
							req.setDestination(toString(f));
							break;
						case ClOrdID.FIELD:
							req.setRequestId(toString(f));
							break;
						case OrderQty.FIELD:
							oinfo.setOrderQty(toInt(f));
							break;
						case Price.FIELD:
							oinfo.setLimitPx(toDouble(f));
							break;
						case Side.FIELD:
							oinfo.setSide(com.f1.pofo.oms.Side.get(toChar(f)));
							break;
						case Text.FIELD:
							oinfo.setText(toString(f));
							break;
						case Account.FIELD:
							oinfo.setAccount(toString(f));
							break;
						case OrdType.FIELD:
							oinfo.setOrderType(com.f1.pofo.oms.OrderType.getOrdType(toChar(f)));
							break;
						case TimeInForce.FIELD:
							oinfo.setTimeInForce(com.f1.pofo.oms.TimeInForce.getTIF(getString(msg, OrdType.FIELD), toString(f)));
							break;
						case ExecInst.FIELD:
							oinfo.setExecInstructions(toString(f));
							break;
						case Currency.FIELD:
							oinfo.setCurrency(toString(f));
							break;

						default:
					}
					if (newOrderRetainFields.contains(f.getTag()))
						passThru.put(f.getTag(), toString(f));
				}
				if (passThru.size() > 0) {
					oinfo.setPassThruTags(passThru);
				}
				evt = req;
				break;
			}
			case CANCEL_REQUEST: {
				FixCancelRequest req = cog.nw();
				handlePosDup(msg, req);
				req.setSessionName(action.getSessionName());
				req.setType(MsgType.CANCEL_REQUEST);
				FixOrderInfo oinfo = oinfog.nw();
				req.setOrderInfo(oinfo);
				Iterator<Field<?>> iter = msg.iterator();
				processHeaderPassthroughs(passThru, cancelOrderRetainFields, msg);
				while (iter.hasNext()) {
					Field<?> f = iter.next();
					switch (f.getTag()) {
						case ClOrdID.FIELD:
							req.setRequestId(toString(f));
							break;
						case OrigClOrdID.FIELD:
							req.setRefId(toString(f));
							break;
						default:
					}
					if (cancelOrderRetainFields.contains(f.getTag()))
						passThru.put(f.getTag(), toString(f));
				}
				if (passThru.size() > 0) {
					oinfo.setPassThruTags(passThru);
				}
				evt = req;
				break;
			}
			case REPLACE_REQUEST: {
				FixOrderReplaceRequest replace = org.nw();
				FixOrderInfo oinfo = oinfog.nw();
				handlePosDup(msg, replace);
				replace.setSessionName(action.getSessionName());
				replace.setOrderInfo(oinfo);
				replace.setType(MsgType.REPLACE_REQUEST);
				Iterator<Field<?>> iter = msg.iterator();
				if (msg.getHeader().isSetField(SenderSubID.FIELD)) {
					String senderSubId = msg.getHeader().getString(SenderSubID.FIELD);
					replace.setSenderSubId(senderSubId);
				}
				if (msg.getHeader().isSetField(OnBehalfOfCompID.FIELD)) {
					replace.setOnBehalfOfCompId(msg.getHeader().getString(OnBehalfOfCompID.FIELD));
				}
				processHeaderPassthroughs(passThru, replaceOrderRetainFields, msg);
				while (iter.hasNext()) {
					Field<?> f = iter.next();
					switch (f.getTag()) {
						case Symbol.FIELD:
							replace.setSymbol(toString(f));
							break;
						case LocateReqd.FIELD:
							replace.setLocateBrokerRequired("Y".equals(toString(f)));
							break;
						case 5700:
							replace.setLocateBroker(toString(f));
							break;
						case 5701:
							replace.setLocateId(toString(f));
							break;
						case OrderCapacity.FIELD:
							replace.setOrderCapacity(toString(f));
							break;
						case Rule80A.FIELD:
							replace.setRule80A(toString(f));
							break;
						case SymbolSfx.FIELD:
							replace.setSymbolSfx(toString(f));
							break;
						case OrderQty.FIELD:
							oinfo.setOrderQty(toInt(f));
							break;
						case Price.FIELD:
							oinfo.setLimitPx(toDouble(f));
							break;
						case Side.FIELD:
							oinfo.setSide(com.f1.pofo.oms.Side.get(toChar(f)));
							break;
						case OrdType.FIELD:
							oinfo.setOrderType(com.f1.pofo.oms.OrderType.getOrdType(toChar(f)));
							break;
						case TimeInForce.FIELD:
							oinfo.setTimeInForce(com.f1.pofo.oms.TimeInForce.getTIF(getString(msg, OrdType.FIELD), f.getObject().toString()));
							break;
						case OrigClOrdID.FIELD:
							replace.setRefId(toString(f));
							break;
						case ClOrdID.FIELD:
							replace.setRequestId(toString(f));
							break;
						case Currency.FIELD:
							oinfo.setCurrency(toString(f));
							break;
						default:
							if (replaceOrderRetainFields.contains(f.getTag()))
								passThru.put(f.getTag(), toString(f));
					}
				}
				if (passThru.size() > 0) {
					oinfo.setPassThruTags(passThru);
				}
				evt = replace;
				break;
			}
			case CANCEL_REJECT: {
				FixChildReplaceReject rej = cxlrejg.nw();
				handlePosDup(msg, rej);
				rej.setType(MsgType.CANCEL_REJECT);
				Iterator<Field<?>> iter = msg.iterator();
				ChildOrderId cid = coidg.nw();
				rej.setChildId(cid);
				while (iter.hasNext()) {
					Field<?> f = iter.next();
					int tag = f.getTag();
					switch (tag) {
						case ClOrdID.FIELD:
							rej.setRequestID(toString(f));
							setChildId(rej.getRequestID(), cid, false);
							break;
						case OrigClOrdID.FIELD:
							rej.setRefId(toString(f));
							setChildId(rej.getRefId(), cid, true);
							break;
						case OrdStatus.FIELD:
							rej.setOrderStatus(toInt(f));
							break;
						case CxlRejResponseTo.FIELD:
							rej.setResponseTo(toInt(f));
							break;
						case CxlRejReason.FIELD:
							rej.setReason(toInt(f));
							break;
						case Text.FIELD:
							rej.setText(toString(f));
							break;
					}
				}
				evt = rej;
				break;
			}
			case EXECUTION_REPORT: {
				com.f1.pofo.fix.ExecType type = com.f1.pofo.fix.ExecType.get(SH.parseChar(msg.getString(ExecType.FIELD)));
				FixReport exec;
				Execution execution = null;
				ChildOrderId cid = coidg.nw();
				int execTransType = msg.getInt(ExecTransType.FIELD);
				if (execTransType == 1 || execTransType == 2) {
					exec = eog.nw();
					execution = execg.nw();
					((FixExecutionReport) exec).setExecution(execution);
					execution.setExecTransType(execTransType);
					String refId = msg.getString(ExecRefID.FIELD);
					execution.setExecRefID(refId);
				} else if (execTransType == 3) {
					exec = eog.nw();
					execution = execg.nw();
					((FixExecutionReport) exec).setExecution(execution);
					execution.setExecTransType(execTransType);
				} else
					switch (type) {
						case PARTIAL:
						case FILLED:
							exec = eog.nw();
							execution = execg.nw();
							((FixExecutionReport) exec).setExecution(execution);
							break;
						default:
							exec = sog.nw();
							break;
					}
				((ChildMessage) exec).setChildId(cid);
				exec.setSessionName(action.getSessionName());
				exec.setType(MsgType.EXECUTION_REPORT);
				exec.setExecType(type);
				// dont always need to
				// instantiate: only look for
				// ExecType and figure it out
				Iterator<Integer> giter = msg.groupKeyIterator();
				while (giter.hasNext()) {
					int group = giter.next();
					switch (group) {
						case quickfix.field.NoContraBrokers.FIELD:
							quickfix.fix42.ExecutionReport.NoContraBrokers grp = new quickfix.fix42.ExecutionReport.NoContraBrokers();
							msg.getGroup(1, grp);
							if (execution != null)
								execution.setContraBroker(grp.getContraBroker().getValue());
							break;
					}
				}
				handlePosDup(msg, exec);
				Iterator<Field<?>> iter = msg.iterator();
				processHeaderPassthroughs(passThru, executionReportRetainFields, msg);
				while (iter.hasNext()) {
					Field<?> f = iter.next();
					int tag = f.getTag();
					switch (tag) {
						case ExecID.FIELD:
							if (execution != null)
								execution.setId(toString(f));
							break;
						case OrderQty.FIELD:
							exec.setOrderQty(toInt(f));
							break;
						case Price.FIELD:
							exec.setLimitPx(toDouble(f));
							break;
						case Side.FIELD:
							exec.setSide(com.f1.pofo.oms.Side.get(toChar(f)));
							break;
						case Currency.FIELD:
							exec.setCurrency(toString(f));
							break;
						case OrdType.FIELD:
							exec.setOrderType(com.f1.pofo.oms.OrderType.getOrdType(toChar(f)));
							break;
						case TimeInForce.FIELD:
							exec.setTimeInForce(com.f1.pofo.oms.TimeInForce.getTIF(getString(msg, OrdType.FIELD), toString(f)));
							break;
						case ClOrdID.FIELD:
							exec.setRequestId(toString(f));
							setChildId(exec.getRequestId(), cid, false);
							break;
						case OrigClOrdID.FIELD:
							((FixStatusReport) exec).setOrigId(toString(f));
							setChildId(exec.getRequestId(), cid, true);
							break;
						case OrdStatus.FIELD:
							exec.setOrdStatus(com.f1.pofo.fix.OrdStatus.getMask(toChar(f)));
							break;
						case CumQty.FIELD:
							exec.setCumQty(toInt(f));
							break;
						case AvgPx.FIELD:
							exec.setExecValue(toDouble(f) * Integer.parseInt(msg.getString(CumQty.FIELD)));
							break;
						case ExecType.FIELD:
							exec.setExecType(com.f1.pofo.fix.ExecType.get(toChar(f)));
							break;
						// case TransactTime.FIELD:
						// state.setLastUpdateTime(/)
						case LastShares.FIELD:
							if (execution != null)
								execution.setExecQty(toInt(f));
							break;
						case LastPx.FIELD:
							if (execution != null)
								execution.setExecPx(toDouble(f));
							break;
						case ExecBroker.FIELD:
							if (execution != null)
								execution.setExecBroker(toString(f));
							break;
						case ExecRefID.FIELD:
							if (execution != null)
								execution.setExecRefID(toString(f));
							break;
						case TransactTime.FIELD:
							if (exec instanceof FixStatusReport)
								((FixStatusReport) exec).setTransactTime(toNanoDate(f));
							else if (execution != null)
								execution.setExecTime(toNanoDate(f));
							break;
						case OrderID.FIELD:
							if (exec instanceof FixStatusReport)
								((FixStatusReport) exec).setOrderId(toString(f));
							break;
						case Text.FIELD:
							exec.setText(toString(f));
						default:
							if (brokerTag == tag) {
								if (execution != null)
									execution.setLastMkt(toString(f));
							} else if (executionReportRetainFields.contains(tag))
								passThru.put(f.getTag(), toString(f));
					}
				}
				if (passThru.size() > 0) {
					exec.setPassThruTags(passThru);
				}
				evt = exec;
				break;
			}

		}
		if (evt != null) {
			// System.out.println("Received event : " + evt.toString());
			output.send(evt, threadScope);
		}
	}

	private void processHeaderPassthroughs(Map<Integer, String> passThru, IntSet retainsFields, Message msg) {
		Header header = msg.getHeader();
		if (header == null)
			return;
		for (Iterator<Field<?>> i = header.iterator(); i.hasNext();) {
			final Field<?> f = i.next();
			if (retainsFields.contains(f.getTag()))
				passThru.put(f.getTag(), toString(f));
		}
	}

	private void handlePosDup(Message msg, FixMsg exec) throws FieldNotFound {
		if (msg.getHeader().isSetField(PossResend.FIELD)) {
			String posResend = msg.getHeader().getString(PossResend.FIELD);
			exec.setPosResend("Y".equals(posResend));
		}
		if (msg.getHeader().isSetField(PossDupFlag.FIELD)) {
			String posDup = msg.getHeader().getString(PossDupFlag.FIELD);
			exec.setPosResend("Y".equals(posDup));
		}

	}

	private static void setChildId(String requestId, ChildOrderId id, boolean orig) {// TODO:Where
																						// does
																						// this
																						// belong?
		int index = requestId.lastIndexOf('-');
		String orderId = requestId.substring(0, index);
		int reqRev = Integer.parseInt(requestId.substring(index + 1));
		if (id.getOrderId() == null)
			id.setOrderId(orderId);
		else if (!id.getOrderId().equals(orderId)) {
			throw new RuntimeException("The ids " + id.getOrderId() + " , " + orderId + " do not match!");
		}
		if (orig) {
			id.setOrderRevisionId(reqRev);
		} else {
			id.setRequestId(reqRev);
		}
	}

	private String toString(Field<?> field) {
		return SH.toString(field.getObject());
	}

	private int toInt(Field<?> field) {
		return Integer.parseInt(field.getObject().toString());
	}

	private double toDouble(Field<?> field) {
		return SH.parseDouble(field.getObject().toString());
	}

	private char toChar(Field<?> o) {
		return SH.parseChar(o.getObject().toString());
	}

	private DateNanos toNanoDate(Field<?> o) throws ParseException {
		final String s = o.getObject().toString();
		Formatter formatter = getTools().getThreadScope().getFormatter().getDateFormatter(LocaleFormatter.DATETIME_FULL);
		if (!formatter.canParse(s))
			formatter = getTools().getThreadScope().getFormatter().getDateFormatter(LocaleFormatter.DATETIME);
		return new DateNanos((java.util.Date) formatter.parse(s));
	}
	static private String getString(Message msg, int field) throws FieldNotFound {
		if (msg.isSetField(field))
			return msg.getString(field);
		else
			return null;
	}

}
