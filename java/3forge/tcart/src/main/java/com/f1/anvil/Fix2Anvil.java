package com.f1.anvil;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.f1.anvil.loader.FixMessageParser;
import com.f1.base.Generator;
import com.f1.bootstrap.F1Constants;
import com.f1.stringmaker.StringTranslator;
import com.f1.utils.ArgParser;
import com.f1.utils.ArgParser.Arguments;
import com.f1.utils.FastBufferedInputStream;
import com.f1.utils.FastBufferedOutputStream;
import com.f1.utils.FastBufferedReader;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.ObjectPoolForClearable;
import com.f1.utils.impl.CharSequenceHasher;
import com.f1.utils.structs.BasicMultiMap;

public class Fix2Anvil {
	public static final char FIXVAL_MsgType_ExecutionReport = '8';
	public static final char FIXVAL_MsgType_OrderSingle = 'D';
	public static final char FIXVAL_MsgType_OrderCancelRequest = 'F';
	public static final char FIXVAL_MsgType_OrderCancelReplaceRequest = 'G';
	public static final char FIXVAL_MsgType_OrderCancelReject = '9';

	public static final char FIXVAL_ExecType_New = '0';
	public static final char FIXVAL_ExecType_PartialFill = '1';
	public static final char FIXVAL_ExecType_Fill = '2';
	public static final char FIXVAL_ExecType_DoneForDay = '3';
	public static final char FIXVAL_ExecType_Canceled = '4';
	public static final char FIXVAL_ExecType_Replace = '5';
	public static final char FIXVAL_ExecType_Pending = '6';
	public static final char FIXVAL_ExecType_Rejected = '8';
	public static final char FIXVAL_ExecType_PendingNew = 'A';
	public static final char FIXVAL_ExecType_Restated = 'D';
	public static final char FIXVAL_ExecType_PendingReplace = 'E';

	private static final int FIX_LAST_LIQUIDITY_IND = 851;

	public static final int FIXTAG_Account = 1;
	public static final int FIXTAG_AvgPx = 6;
	public static final int FIXTAG_ClOrdID = 11;
	public static final int FIXTAG_CumQty = 14;
	public static final int FIXTAG_Currency = 15;
	public static final int FIXTAG_CxlRejReason = 102;
	public static final int FIXTAG_CxlRejResponseTo = 434;
	public static final int FIXTAG_ExDestination = 100;
	public static final int FIXTAG_ExecBroker = 76;
	public static final int FIXTAG_ExecID = 17;
	public static final int FIXTAG_ExecInst = 18;
	public static final int FIXTAG_ExecRefID = 19;
	public static final int FIXTAG_ExecRestatementReason = 378;
	public static final int FIXTAG_ExecTransType = 20;
	public static final int FIXTAG_ExecType = 150;
	public static final int FIXTAG_HandlInst = 21;
	public static final int FIXTAG_IDSource = 22;
	public static final int FIXTAG_LastMkt = 30;
	public static final int FIXTAG_LastPx = 31;
	public static final int FIXTAG_LastShares = 32;
	public static final int FIXTAG_LeavesQty = 151;
	public static final int FIXTAG_LocateBroker = 5700;
	public static final int FIXTAG_LocateId = 5701;
	public static final int FIXTAG_LocateReqd = 114;
	public static final int FIXTAG_MsgType = 35;
	public static final int FIXTAG_NoContraBrokers = 382;
	public static final int FIXTAG_OnBehalfOfCompID = 115;
	public static final int FIXTAG_OrderCapacity = 528;
	public static final int FIXTAG_OrderID = 37;
	public static final int FIXTAG_OrderQty = 38;
	public static final int FIXTAG_OrdStatus = 39;
	public static final int FIXTAG_OrdType = 40;
	public static final int FIXTAG_OrigClOrdID = 41;
	public static final int FIXTAG_PossResend = 97;
	public static final int FIXTAG_Price = 44;
	public static final int FIXTAG_Rule80A = 47;
	public static final int FIXTAG_SecurityID = 48;
	public static final int FIXTAG_SenderSubID = 50;
	public static final int FIXTAG_Side = 54;
	public static final int FIXTAG_Symbol = 55;
	public static final int FIXTAG_SymbolSfx = 65;
	public static final int FIXTAG_Text = 58;
	public static final int FIXTAG_TimeInForce = 59;
	public static final int FIXTAG_TransactTime = 60;

	char delim;
	char associator;
	boolean isParent;
	private int strategyField = -1;
	private int systemField = -1;
	private int startTimeField = -1;
	private int endTimeFiled = -11;
	private int accountField = FIXTAG_SenderSubID;
	private int origClOrdIdField = FIXTAG_OrigClOrdID;
	private int clOrdIdField = FIXTAG_ClOrdID;
	private int symbolField = FIXTAG_Symbol;
	private int priceField = FIXTAG_Price;
	private int sideField = FIXTAG_Side;
	private int sizeField = FIXTAG_OrderQty;
	private int currencyField = FIXTAG_Currency;
	private int lastmktField = FIXTAG_LastMkt;
	private int exIndicatorField = FIX_LAST_LIQUIDITY_IND;
	private int exIdField = FIXTAG_ExecID;
	private long timezoneOffsetMs;

	public static class SessionMaps {
		private Map<CharSequence, String> orderIdToAnvilOrderId = new HasherMap<CharSequence, String>(CharSequenceHasher.INSTANCE);//order chain ids
		private Map<CharSequence, String> clOrderIdToAnvilOrderId = new HasherMap<CharSequence, String>(CharSequenceHasher.INSTANCE);//order chain ids
		private Map<CharSequence, String> exIdToAnvilExecutionId = new HasherMap<CharSequence, String>(CharSequenceHasher.INSTANCE);//execution chain ids
		private Map<CharSequence, FixMessageParser> msgBy11 = new HasherMap<CharSequence, FixMessageParser>(CharSequenceHasher.INSTANCE);//messages awaiting ack
		private CharSequence getOrderId(CharSequence origOrderId, CharSequence clOrderId) {
			if (SH.isnt(origOrderId)) {
				String existing = this.clOrderIdToAnvilOrderId.get(clOrderId);
				return existing != null ? existing : clOrderId;
			}
			String existing = this.clOrderIdToAnvilOrderId.get(origOrderId);
			if (existing == null)
				existing = origOrderId.toString();
			this.clOrderIdToAnvilOrderId.put(clOrderId.toString(), existing);
			return existing;
		}
		private CharSequence getExecutionId(CharSequence execRefId, CharSequence execId) {
			if (SH.isnt(execRefId))
				return execId;
			String existing = this.exIdToAnvilExecutionId.get(execRefId);
			if (existing == null)
				existing = execRefId.toString();
			this.clOrderIdToAnvilOrderId.put(execId.toString(), existing);
			return existing;
		}
	}
	private Map<String, String> childId2parentId = new HashMap<String, String>();//linking child id to it's parent id
	private BasicMultiMap.Set<String, String> parentId2childIds = new BasicMultiMap.Set<String, String>();

	private SessionMaps parentMaps = new SessionMaps();
	private SessionMaps childMaps = new SessionMaps();

	final private boolean success;
	private ObjectPoolForClearable<FixMessageParser> fixMessages = new ObjectPoolForClearable<FixMessageParser>(new Generator<FixMessageParser>() {
		@Override
		public FixMessageParser nw() {
			FixMessageParser r = new FixMessageParser();
			r.setDelimiter(delim);
			r.setAssociator(associator);
			return r;
		}
	}, 1000);

	private boolean logging = true;
	private StringBuilder bufOut = new StringBuilder();
	private Matcher matcherIn;
	private Matcher matcherOut;
	private Matcher matcherParent;
	private Matcher matcherChild;
	private StringTranslator childParentPattern;
	private String systemName;
	private int parentFixTag;

	public static void main(String a[]) throws IOException {
		Fix2Anvil f2a = new Fix2Anvil(a);
		System.exit(f2a.success ? 0 : F1Constants.EXITCODE_MAINTHREAD_UNHANDLED_EXCEPTION);
	}
	private Fix2Anvil(String[] a) throws IOException {
		ArgParser ap = new ArgParser("fix2Anvil");
		ap.setDescription("Converts Fix to Anvil format by extracting select fields");
		ap.addSwitch("d", "delimiter", SH.m("*"), false, false, "Delimiter seperating pairs, default is \u0001");
		ap.addSwitch("a", "associator", SH.m("*"), false, false, "Associator for key and value, default is =");
		ap.addSwitch("t", "type", SH.m("*"), false, false, "Must be either PARENT or CHILD");
		ap.addSwitch("f", "infile", SH.m("*"), false, false, "file to read from (default is stdin");
		ap.addSwitch("o", "outfile", SH.m("*"), false, false, "File to write to (default is stdout)");
		ap.addSwitch("ptn_i", "pattern_incoming", SH.m("*"), false, false, "Pattern for Identifying messages arriving into system");
		ap.addSwitch("ptn_o", "pattern_outgoing", SH.m("*"), false, false, "Pattern for Identfying messages originating from system");
		ap.addSwitch("ptn_p", "pattern_parent", SH.m("*"), false, false, "Pattern for Identifying messages that are on the parent/client side");
		ap.addSwitch("ptn_c", "pattern_child", SH.m("*"), false, false, "Pattern for Identfying messages that are child/street side");

		ap.addSwitch("ptn_corr", "pattern_child_parent_id_correlation", SH.m("*"), false, false, "Pattern for Identfying messages that correlate child to parent");
		ap.addSwitch("tem_corr", "template_child_parent_id_correlation", SH.m("*"), false, false, "Template must produce a pattern such that: childId|parentId");
		ap.addSwitch("f_pid", "fixtag_parentid", SH.m("*"), false, false, "The tag on a child containing the parent id");

		ap.addSwitch("f_str", "fixtag_strategy", SH.m("*"), false, false, "Fix tag for Strategy, must be number");
		ap.addSwitch("f_sys", "fixtag_system", SH.m("*"), false, false, "Fix tag for System, must be number");
		ap.addSwitch("sys", "system", SH.m("*"), false, false, "Hard coded system name");
		ap.addSwitch("f_stm", "fixtag_starttime", SH.m("*"), false, false, "Fix tag for Start Time, must be number");
		ap.addSwitch("f_etm", "fixtag_endtime", SH.m("*"), false, false, "Fix tag for End Time, must be number");
		ap.addSwitch("f_act", "fixtag_account", SH.m("*"), false, false, "Fix tag for Account, must be number");
		ap.addSwitch("tz", "timezone_offset", SH.m("*"), false, false, "Offsets in timezone: ex, for EST do -05:00:00");

		Arguments options;
		try {
			options = ap.parse(a);
			this.delim = options.getOptional("d", (char) 01);
			this.associator = options.getOptional("a", '=');
			this.isParent = "PARENT".equals(options.getRequiredEnum("t", "PARENT", "CHILD"));
			this.strategyField = options.getOptional("f_str", this.strategyField);
			this.systemField = options.getOptional("f_sys", this.systemField);
			this.systemName = options.getOptional("sys");
			this.startTimeField = options.getOptional("f_stm", this.startTimeField);
			this.endTimeFiled = options.getOptional("f_etm", this.endTimeFiled);
			this.matcherIn = toMatcher(options, "ptn_i");
			this.matcherOut = toMatcher(options, "ptn_o");
			this.matcherParent = toMatcher(options, "ptn_p");
			this.matcherChild = toMatcher(options, "ptn_c");
			String ptn_corr = options.getOptional("ptn_corr");
			String tem_corr = options.getOptional("tem_corr");
			this.parentFixTag = options.getOptional("f_pid", -1);
			if (SH.is(ptn_corr) != SH.is(tem_corr))
				throw new RuntimeException("ptn_corr and tem_corr options are mutually inclusive");
			if (SH.is(ptn_corr))
				this.childParentPattern = new StringTranslator(ptn_corr, tem_corr);
			this.accountField = options.getOptional("f_act", this.accountField);
			this.timezoneOffsetMs = SH.parseDurationTo(options.getOptional("tz", "0:0:0"), TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			System.out.println(SH.printStackTrace(e));
			System.out.println(ap.toLegibleString());
			this.success = false;
			return;
		}

		String file = options.getOptional("f");
		String outFile = options.getOptional("o");

		final InputStream in = file == null ? System.in : new FileInputStream(file);
		final OutputStream out = outFile == null ? System.out : new FileOutputStream(outFile);
		process(new FastBufferedInputStream(in), new PrintStream(new FastBufferedOutputStream(out)));
		this.success = true;

	}
	static private Matcher toMatcher(Arguments options, String key) {
		String pattern = options.getOptional(key);
		if (pattern == null)
			return null;
		try {
			return Pattern.compile(pattern).matcher("");
		} catch (Exception e) {
			throw new RuntimeException("Option '" + key + "' has invalid pattern", e);
		}
	}

	private void process(InputStream in, PrintStream out) throws IOException {
		final FastBufferedReader reader = new FastBufferedReader(new InputStreamReader(in));
		FixMessageParser fixMessage = this.fixMessages.nw();
		while (true) {
			final StringBuilder bufIn = fixMessage.getBufferUnsafe();
			bufIn.setLength(0);
			if (!reader.readLine(bufIn)) {
				out.flush();
				break;
			}
			handleChildParentPattern(bufIn);

			//Parse the fix message and if we don't get a 35,60 and 11 skip the message
			fixMessage.build();
			final CharSequence msgType = fixMessage.getValue(FIXTAG_MsgType);
			if (msgType == null || msgType.length() != 1)
				continue;
			if (parseTime(fixMessage.getValue(FIXTAG_TransactTime)) == -1)
				continue;
			final char type = msgType.charAt(0);
			final Boolean isParent = isParent(bufIn, type);
			if (isParent == null)
				continue;
			CharSequence clOrdID = fixMessage.getValue(FIXTAG_ClOrdID);
			if (clOrdID == null)
				continue;
			SessionMaps maps = isParent ? parentMaps : childMaps;

			switch (type) {
				case FIXVAL_MsgType_OrderSingle: {
					String strClOrdID = clOrdID.toString();
					maps.msgBy11.put(strClOrdID, fixMessage);
					if (this.parentFixTag != -1) {
						CharSequence parentId = fixMessage.getValue(this.parentFixTag);
						if (SH.is(parentId))
							this.handleChildParentPattern(strClOrdID, parentId.toString());
					}
					fixMessage = fixMessages.nw();
					break;
				}
				case FIXVAL_MsgType_OrderCancelRequest:
				case FIXVAL_MsgType_OrderCancelReplaceRequest: {
					CharSequence origClOrdID = getRequired(fixMessage, FIXTAG_OrigClOrdID);
					if (origClOrdID == null)
						continue;
					String strClOrdID = clOrdID.toString();
					maps.getOrderId(origClOrdID.toString(), strClOrdID);
					if (this.parentFixTag != -1) {
						CharSequence parentId = fixMessage.getValue(this.parentFixTag);
						if (SH.is(parentId))
							this.handleChildParentPattern(strClOrdID, parentId.toString());
					}
					maps.msgBy11.put(strClOrdID, fixMessage);
					fixMessage = fixMessages.nw();
					break;
				}
				case FIXVAL_MsgType_OrderCancelReject: {
					FixMessageParser clientMsg = removeRequired(maps.msgBy11, clOrdID, fixMessage, false);
					if (clientMsg == null)
						continue;
					this.fixMessages.recycle(clientMsg);
					break;
				}
				case FIXVAL_MsgType_ExecutionReport: {
					CharSequence execType = getRequired(fixMessage, FIXTAG_ExecType);
					if (execType == null || execType.length() != 1)
						continue;
					char etype = execType.charAt(0);
					switch (etype) {
						case FIXVAL_ExecType_Canceled: {
							FixMessageParser clientMsg = removeRequired(maps.msgBy11, clOrdID, fixMessage, false);
							if (clientMsg != null) {
								sendMessageOnAck(clientMsg, out, maps);
								this.fixMessages.recycle(clientMsg);
							} else
								sendCancel(fixMessage, out, maps);
							break;
						}

						case FIXVAL_ExecType_Replace:
						case FIXVAL_ExecType_New: {
							CharSequence orderId = getRequired(fixMessage, FIXTAG_OrderID);

							if (orderId == null)
								continue;
							CharSequence origClOrdID = fixMessage.getValue(FIXTAG_OrigClOrdID);
							maps.orderIdToAnvilOrderId.put(orderId.toString(), maps.getOrderId(origClOrdID, clOrdID).toString());
							FixMessageParser clientMsg = removeRequired(maps.msgBy11, clOrdID, fixMessage, true);
							if (clientMsg == null)
								continue;
							sendMessageOnAck(clientMsg, out, maps);
							this.fixMessages.recycle(clientMsg);
							break;
						}

						case FIXVAL_ExecType_PartialFill:
						case FIXVAL_ExecType_Fill: {
							sendExecution(fixMessage, etype, out, maps);
							break;
						}

						case FIXVAL_ExecType_Rejected: {
							FixMessageParser clientMsg = removeRequired(maps.msgBy11, clOrdID, fixMessage, true);
							if (clientMsg == null)
								continue;
							this.fixMessages.recycle(clientMsg);
							break;
						}

						case FIXVAL_ExecType_Pending:
						case FIXVAL_ExecType_PendingNew:
						case FIXVAL_ExecType_PendingReplace:
							//not interested in pendings
							break;

						case FIXVAL_ExecType_Restated:
						case FIXVAL_ExecType_DoneForDay:
							//don't support at the moment
							break;
					}
					break;
				}
				default:
					break;
			}
		}
	}

	private static final byte MASK_MATCH_P = 1;
	private static final byte MASK_MATCH_C = 2;
	private static final byte MASK_MATCH_I = 4;
	private static final byte MASK_MATCH_O = 8;
	private static final byte MASK_IS_REQ = 16;
	private Boolean isParent(StringBuilder bufIn, char type) {
		byte flag = 0;
		flag = MH.setBits(flag, (byte) MASK_MATCH_P, matches(matcherParent, bufIn));
		flag = MH.setBits(flag, (byte) MASK_MATCH_C, matches(matcherChild, bufIn));
		flag = MH.setBits(flag, (byte) MASK_MATCH_I, matches(matcherIn, bufIn));
		flag = MH.setBits(flag, (byte) MASK_MATCH_O, matches(matcherOut, bufIn));
		flag = MH.setBits(flag, (byte) MASK_IS_REQ, isReqType(type));
		switch (flag) {
			case MASK_MATCH_P:
			case MASK_MATCH_P | MASK_IS_REQ:
			case MASK_MATCH_I | MASK_IS_REQ: //an input request is on the parent
			case MASK_MATCH_O: //an output response is on the parent side
			case MASK_MATCH_P | MASK_MATCH_I | MASK_IS_REQ://Doubly sure
			case MASK_MATCH_P | MASK_MATCH_O://Doubly sure
				return Boolean.TRUE;

			case MASK_MATCH_C:
			case MASK_MATCH_C | MASK_IS_REQ:
			case MASK_MATCH_I://an input response is on the child side
			case MASK_MATCH_O | MASK_IS_REQ://an output request is on the child side
			case MASK_MATCH_C | MASK_MATCH_I://Doubly sure
			case MASK_MATCH_C | MASK_MATCH_O | MASK_IS_REQ://doubly sure
				return Boolean.FALSE;
			case 0:
			case MASK_IS_REQ:
				return isParent;//didn't match any regular expressions, just return the defaults
			case MASK_MATCH_I | MASK_MATCH_O:
			case MASK_MATCH_I | MASK_MATCH_O | MASK_IS_REQ:

			case MASK_MATCH_I | MASK_MATCH_O | MASK_MATCH_P | MASK_IS_REQ:
			case MASK_MATCH_I | MASK_MATCH_O | MASK_MATCH_C:
			case MASK_MATCH_I | MASK_MATCH_O | MASK_MATCH_C | MASK_IS_REQ:
			case MASK_MATCH_I | MASK_MATCH_O | MASK_MATCH_P | MASK_MATCH_C:
			case MASK_MATCH_I | MASK_MATCH_O | MASK_MATCH_P | MASK_MATCH_C | MASK_IS_REQ:
				if (logging)
					log("Matches Conflicting Input and Output Patterns: " + bufIn);
				return null;
			case MASK_MATCH_P | MASK_MATCH_C:
			case MASK_MATCH_P | MASK_MATCH_C | MASK_IS_REQ:
			case MASK_MATCH_P | MASK_MATCH_C | MASK_MATCH_I:
			case MASK_MATCH_P | MASK_MATCH_C | MASK_MATCH_I | MASK_IS_REQ:
			case MASK_MATCH_P | MASK_MATCH_C | MASK_MATCH_O:
			case MASK_MATCH_P | MASK_MATCH_C | MASK_MATCH_O | MASK_IS_REQ:
				if (logging)
					log("Matches Conflicting Parent and Child Patterns: " + bufIn);
				return null;
			case MASK_MATCH_P | MASK_MATCH_I:
				if (logging)
					log("Matches Conflicting Parent and input pattern for non-request msg: " + bufIn);
				return null;
			case MASK_MATCH_P | MASK_MATCH_O | MASK_IS_REQ:
				if (logging)
					log("Matches Conflicting Parent and output pattern for request msg: " + bufIn);
				return null;
			case MASK_MATCH_C | MASK_MATCH_I | MASK_IS_REQ:
				if (logging)
					log("Matches Conflicting Child and input pattern for request msg: " + bufIn);
				return null;
			case MASK_MATCH_C | MASK_MATCH_O:
				if (logging)
					log("Matches Conflicting Child and output pattern for non-request msg: " + bufIn);
				return null;
			default:
				throw new IllegalStateException(SH.toString(flag));//can't reach here, already considered each of the 2^5 (32) states
		}
	}
	static private boolean isReqType(char type) {
		switch (type) {
			case FIXVAL_MsgType_OrderCancelReplaceRequest:
			case FIXVAL_MsgType_OrderCancelRequest:
			case FIXVAL_MsgType_OrderSingle:
				return true;
			case FIXVAL_MsgType_ExecutionReport:
			case FIXVAL_MsgType_OrderCancelReject:
				return false;
		}
		return false;
	}

	///////////////////////////////
	// Anvil Message formation
	private void sendExecution(FixMessageParser fm, char etype, PrintStream out, SessionMaps maps) {
		boolean isParent = maps == this.parentMaps;

		//Determine the OrderId as anvil knows it
		CharSequence origOrderId = OH.noNull(fm.getValue(origClOrdIdField), "");
		CharSequence clOrderId = OH.noNull(fm.getValue(clOrdIdField), "");
		CharSequence orderId = maps.getOrderId(origOrderId, clOrderId);
		if (isParent && parentId2childIds.containsKey(orderId.toString()))
			return;//This execution's order has registered children so lets assume the exec will come on the child too and avoid double counting executions.

		CharSequence execId = getRequired(fm, FIXTAG_ExecID);
		CharSequence execRefId = fm.getValue(FIXTAG_ExecRefID);
		CharSequence executionId = maps.getExecutionId(execRefId, execId);

		long time = parseTime(fm.getValue(FIXTAG_TransactTime));
		CharSequence symbol = OH.noNull(fm.getValue(symbolField), "");
		CharSequence px = OH.noNull(fm.getValue(FIXTAG_LastPx), "");
		CharSequence exchange = OH.noNull(fm.getValue(lastmktField), "");
		CharSequence side = parseSide(fm.getValue(sideField));
		CharSequence size = OH.noNull(fm.getValue(FIXTAG_LastShares), "");
		CharSequence currency = OH.noNull(fm.getValue(currencyField), "");
		CharSequence exIndicator = OH.noNull(fm.getValue(exIndicatorField), "");
		CharSequence oId = isParent ? "" : orderId;
		CharSequence parentOrderId = !isParent ? OH.noNull(this.childId2parentId.get(orderId), "") : orderId;
		SH.clear(bufOut);
		bufOut.append(time).append('|');
		bufOut.append('E').append('|');
		bufOut.append(symbol).append('|');
		bufOut.append(exchange).append('|');
		bufOut.append(px).append('|');
		appendInt(bufOut, size).append('|');
		bufOut.append(oId).append('|');
		bufOut.append(parentOrderId).append('|');
		bufOut.append(side).append('|');
		bufOut.append(exIndicator).append('|');
		bufOut.append(executionId).append('|');
		bufOut.append(currency).append('|');
		out.println(bufOut);
		SH.clear(bufOut);
	}
	private void sendCancel(FixMessageParser fm, PrintStream out, SessionMaps maps) {
		boolean isParent = maps == this.parentMaps;

		//Determine the OrderId as anvil knows it
		CharSequence origOrderId = OH.noNull(fm.getValue(origClOrdIdField), "");
		CharSequence clOrderId = OH.noNull(fm.getValue(clOrdIdField), "");
		CharSequence orderId = maps.getOrderId(origOrderId, clOrderId);

		long time = parseTime(fm.getValue(FIXTAG_TransactTime));
		CharSequence status = "C";
		if (isParent) {
			CharSequence symbol = OH.noNull(fm.getValue(symbolField), "");
			CharSequence limitPx = OH.noNull(fm.getValue(priceField), "");
			CharSequence side = parseSide(fm.getValue(sideField));
			CharSequence size = OH.noNull(fm.getValue(sizeField), "");
			CharSequence currency = OH.noNull(fm.getValue(currencyField), "");
			SH.clear(bufOut);
			bufOut.append(time).append('|');
			bufOut.append('O').append('|');
			bufOut.append(symbol).append('|');
			bufOut.append(side).append('|');
			bufOut.append(limitPx).append('|');
			appendInt(bufOut, size).append('|');
			bufOut.append(orderId).append('|');
			bufOut.append(status).append('|');
			bufOut.append("").append('|');
			bufOut.append("").append('|');
			bufOut.append("").append('|');
			bufOut.append("").append('|');
			bufOut.append("").append('|');
			bufOut.append(currency).append('|');
			out.println(bufOut);
			SH.clear(bufOut);
		} else {
			CharSequence symbol = OH.noNull(fm.getValue(symbolField), "");
			CharSequence limitPx = OH.noNull(fm.getValue(priceField), "");
			CharSequence size = OH.noNull(fm.getValue(sizeField), "");
			CharSequence currency = OH.noNull(fm.getValue(currencyField), "");
			CharSequence parentId = childId2parentId.get(orderId.toString());
			if (parentId == null)
				parentId = "";

			SH.clear(bufOut);
			bufOut.append(time).append('|');
			bufOut.append('C').append('|');
			bufOut.append(symbol).append('|');
			bufOut.append(limitPx).append('|');
			appendInt(bufOut, size).append('|');
			bufOut.append(parentId).append('|');
			bufOut.append(status).append('|');
			bufOut.append(orderId).append('|');
			bufOut.append(origOrderId).append('|');
			bufOut.append(currency).append('|');
			out.println(bufOut);
			SH.clear(bufOut);
		}
		//break;
	}
	private void sendMessageOnAck(FixMessageParser fm, PrintStream out, SessionMaps maps) {
		boolean isParent = maps == this.parentMaps;

		//Determine the OrderId as anvil knows it
		CharSequence origOrderId = OH.noNull(fm.getValue(origClOrdIdField), "");
		CharSequence clOrderId = OH.noNull(fm.getValue(clOrdIdField), "");
		CharSequence orderId = maps.getOrderId(origOrderId, clOrderId);

		long time = parseTime(fm.getValue(FIXTAG_TransactTime));
		char type = fm.getValue(FIXTAG_MsgType).charAt(0);
		CharSequence status = type == 'D' ? "N" : (type == 'F' ? "C" : "U");
		if (isParent) {
			CharSequence symbol = OH.noNull(fm.getValue(symbolField), "");
			CharSequence limitPx = OH.noNull(fm.getValue(priceField), "");
			CharSequence side = parseSide(fm.getValue(sideField));
			CharSequence size = OH.noNull(fm.getValue(sizeField), "");
			CharSequence currency = OH.noNull(fm.getValue(currencyField), "");
			CharSequence system = systemName != null ? systemName : OH.noNull(fm.getValue(systemField), "");
			CharSequence strategy = OH.noNull(fm.getValue(strategyField), "");
			CharSequence startTime = parseTimeSafe(fm.getValue(startTimeField));
			CharSequence endTime = parseTimeSafe(fm.getValue(endTimeFiled));
			CharSequence account = OH.noNull(fm.getValue(accountField), "");
			SH.clear(bufOut);
			bufOut.append(time).append('|');
			bufOut.append('O').append('|');
			bufOut.append(symbol).append('|');
			bufOut.append(side).append('|');
			bufOut.append(limitPx).append('|');
			appendInt(bufOut, size).append('|');
			bufOut.append(orderId).append('|');
			bufOut.append(status).append('|');
			bufOut.append(system).append('|');
			bufOut.append(strategy).append('|');
			bufOut.append(account).append('|');
			bufOut.append(startTime).append('|');
			bufOut.append(endTime).append('|');
			bufOut.append(currency).append('|');
			out.println(bufOut);
			SH.clear(bufOut);
		} else {
			CharSequence symbol = OH.noNull(fm.getValue(symbolField), "");
			CharSequence limitPx = OH.noNull(fm.getValue(priceField), "");
			CharSequence size = OH.noNull(fm.getValue(sizeField), "");
			CharSequence currency = OH.noNull(fm.getValue(currencyField), "");
			CharSequence parentId = childId2parentId.get(orderId.toString());
			if (parentId == null)
				parentId = "";

			SH.clear(bufOut);
			bufOut.append(time).append('|');
			bufOut.append('C').append('|');
			bufOut.append(symbol).append('|');
			bufOut.append(limitPx).append('|');
			appendInt(bufOut, size).append('|');
			bufOut.append(parentId).append('|');
			bufOut.append(status).append('|');
			bufOut.append(orderId).append('|');
			bufOut.append(origOrderId).append('|');
			bufOut.append(currency).append('|');
			out.println(bufOut);
			SH.clear(bufOut);
		}
		//break;

	}
	private void handleChildParentPattern(StringBuilder bufIn) {
		if (this.childParentPattern == null)
			return;
		String result = this.childParentPattern.translate(bufIn);
		if (result == null)
			return;
		int i = result.indexOf('|');
		if (i == -1 || i == 0 || i == result.length() - 1) {
			if (logging)
				log("tem_corr pattern is bad, should produce string with pattern CHILD_ID|PARENT_ID, not: '" + result + "' for: " + bufIn);
			return;
		}
		final String childId = result.substring(0, i);
		final String parentId = result.substring(i + 1, result.length());
		handleChildParentPattern(childId, parentId);
	}
	private void handleChildParentPattern(String childId, String parentId) {
		String parentOrderId = this.parentMaps.orderIdToAnvilOrderId.get(parentId);
		if (parentOrderId == null)
			parentOrderId = this.parentMaps.clOrderIdToAnvilOrderId.get(parentId);
		String childOrderId = this.childMaps.clOrderIdToAnvilOrderId.get(childId);
		if (parentOrderId != null)
			parentId = parentOrderId;
		if (childOrderId != null)
			childId = childOrderId;
		childId2parentId.put(childId, parentId);
		parentId2childIds.putMulti(parentId, childId);
	}

	/////////////////////////////
	//Two-in-One Field extraction
	private FixMessageParser removeRequired(Map<CharSequence, FixMessageParser> map, CharSequence key, FixMessageParser fixMessage, boolean required) {
		FixMessageParser r = map.remove(key);
		if (r == null && logging && required)
			log("Record not found for id '" + key + "' On Message: " + fixMessage);
		return r;
	}
	private CharSequence getRequired(FixMessageParser fixMessage, int tag) {
		CharSequence r = fixMessage.getValue(tag);
		if (SH.isnt(r) && logging)
			log("Requred Tag " + tag + " Missing: " + fixMessage);
		return r;
	}

	/////////////////////////////
	//ID management

	/////////////////////////////
	//Sepcialized Helpers
	private CharSequence parseSide(CharSequence value) {
		if (SH.length(value) != 1)
			return "";
		switch (value.charAt(0)) {
			case '1':
			case '3':
				return "B";
			case '2':
			case '4':
			case '5':
			case '6':
				return "S";
			default:
				return "";
		}
	}
	private long parseTime(CharSequence cs) {
		if (cs == null)
			return -1;
		try {
			int hh;
			int mm;
			int ss;
			int ms;
			if (cs.length() == 21) {
				hh = SH.parseInt(cs, 9, 11, 10);
				mm = SH.parseInt(cs, 12, 14, 10);
				ss = SH.parseInt(cs, 15, 17, 10);
				ms = SH.parseInt(cs, 18, 21, 10);
			} else if (cs.length() == 17) {
				hh = SH.parseInt(cs, 9, 11, 10);
				mm = SH.parseInt(cs, 12, 14, 10);
				ss = SH.parseInt(cs, 15, 17, 10);
				ms = 0;
			} else
				return -1;
			return hh * 3600000 + mm * 60000 + ss * 1000 + ms + timezoneOffsetMs;
		} catch (Exception e) {
			return -1;
		}
	}
	private StringBuilder appendInt(StringBuilder out, CharSequence i) {
		final int pos = SH.indexOf(i, '.', 0);
		return pos == -1 ? out.append(i) : out.append(i, 0, pos);
	}
	private CharSequence parseTimeSafe(CharSequence value) {
		if (SH.isnt(value))
			return "";
		long r = -1;
		if (value.length() == 21 || value.length() == 17) {
			r = parseTime(value);
		}
		if (r == -1)
			return "";
		return SH.toString(r);
	}
	static private boolean matches(Matcher matcher, CharSequence text) {
		if (matcher == null)
			return false;
		matcher.reset(text);
		return matcher.matches();
	}
	private void log(String string) {
		System.err.println(string);
	}
}
