package com.f1.fix2ami;

import static com.f1.common.TestUtils.DROPCOPY_SESSION_NAME;
import static com.f1.common.TestUtils.FIX2AMI_SESSION_NAME;
import static com.f1.common.TestUtils.IGNORE_TAGS;
import static com.f1.common.TestUtils.LOGON_DELAY;
import static com.f1.common.TestUtils.MSG_DELAY;
import static com.f1.common.TestUtils.sendAndVerify;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.f1.bootstrap.ContainerBootstrap;
import com.f1.common.TestUtils;
import com.f1.container.Container;
import com.f1.container.impl.BasicContainer;
import com.f1.fix2ami.processor.AbstractFix2AmiProcessor.MSG_PROCESS_STATUS;
import com.f1.fix2ami.processor.AmiPublishProcessor;
import com.f1.fix2ami.processor.MsgRoutingProcessor;
import com.f1.fix2ami.tool.ToolUtils;
import com.f1.transportManagement.SessionManager;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;

import quickfix.ConfigError;
import quickfix.FieldConvertError;
import quickfix.FieldNotFound;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.field.converter.UtcTimestampConverter;

public class ScenarioTest {
	private static final Logger log = Logger.getLogger(ScenarioTest.class.getName());

	private static final int PAUSE_TIME = 1000;

	private volatile static SessionManager SESSION_MANAGER = null;
	private volatile static SessionManager.FixSessionContext DROPCOPY_CONTEXT = null;
	private volatile static SessionManager.FixSessionContext FIX2AMI_CONTEXT = null;
	private volatile static ContainerBootstrap CAM = null;

	@BeforeClass
	public static void setup() throws ConfigError, FieldConvertError, InterruptedException {

		CAM = new ContainerBootstrap(Fix2AmiMain.class, null);
		CAM.setConfigDirProperty("./src/test/config/fix2ami");
		final PropertyController props = CAM.getProperties();
		Container mycontainer = new BasicContainer();
		CAM.prepareContainer(mycontainer);

		MsgRoutingProcessor msgRoutingProcessor = Fix2AmiMain.setup(mycontainer, props);

		CAM.startupContainer(mycontainer);
		CAM.keepAlive();

		SESSION_MANAGER = new SessionManager(mycontainer, msgRoutingProcessor, props);
		SESSION_MANAGER.start();

		DROPCOPY_CONTEXT = SESSION_MANAGER.getFixSessionContext(DROPCOPY_SESSION_NAME);
		FIX2AMI_CONTEXT = SESSION_MANAGER.getFixSessionContext(FIX2AMI_SESSION_NAME);

		ToolUtils.pause(LOGON_DELAY);
	}

	@AfterClass
	public static void tearDown() throws Exception {
		ToolUtils.pause(MSG_DELAY);

		LH.info(log, "Junit test - cleaning up started.");
		SESSION_MANAGER.shutdown();
	}

	@Test
	public void testUnsupportMsg() throws ConfigError, FieldConvertError, InvalidMessage, ParseException {
		LH.info(log, "\n\nScenario test (unsupport message) started...");

		LH.info(log, "\n   Allocation (FIX session reject)");
		// create new order message
		SU_NEW_ORDER.put(TestUtils.TAG_TransactTime, UtcTimestampConverter.convert(new Date(), false));
		Message msg = TestUtils.buildMessage((Map) CH.m(TestUtils.TAG_MsgType, quickfix.field.MsgType.ALLOCATION_INSTRUCTION), SU_NEW_ORDER);

		//  DROPCOPY1 -> FIX2AMI.
		DROPCOPY_CONTEXT.getSenderFixSession().getSenderSession().send(msg);
		ToolUtils.pause(MSG_DELAY);

		String clOrdID = SU_NEW_ORDER.get(TestUtils.TAG_ClOrdID);
		Message lasIncomingMsg = MsgRoutingProcessor.getLastIncomingMessage(clOrdID);

		// both incoming and un-support message will be null since QuickFix reject the message at the session layer due to missing require tag (53).
		Assert.assertTrue("Unsupport messge type scenario: New Order (Allocation) did not get through to unsupport message table.",
				TestUtils.isMessageMatch(lasIncomingMsg, AmiPublishProcessor.getlastMsg(MSG_PROCESS_STATUS.UNSUPPORT_MSG_TYPE, clOrdID, TestUtils.ORDER_TYPE), IGNORE_TAGS));

		LH.info(log, "\n   EMAIL (Unsupport Valid message)");
		// send a well form EMAIL message which should be routed to UNn-support message table.
		msg = TestUtils.buildMessage((Map) CH.m(TestUtils.TAG_MsgType, quickfix.field.MsgType.EMAIL), SU_EMAIL);

		//  DROPCOPY1 -> FIX2AMI.
		DROPCOPY_CONTEXT.getSenderFixSession().getSenderSession().send(msg);
		ToolUtils.pause(MSG_DELAY);

		clOrdID = SU_EMAIL.get(TestUtils.TAG_ClOrdID);
		lasIncomingMsg = MsgRoutingProcessor.getLastIncomingMessage(clOrdID);

		// both incoming and un-support message will be null since QuickFix reject the message at the session layer due to missing require tag (53).
		Assert.assertTrue("Unsupport messge type scenario: Email message did not get through to unsupport message table.",
				TestUtils.isMessageMatch(lasIncomingMsg, AmiPublishProcessor.getlastMsg(MSG_PROCESS_STATUS.UNSUPPORT_MSG_TYPE, clOrdID, TestUtils.ORDER_TYPE), IGNORE_TAGS));

		LH.info(log, "\n   New Order Ack (missing New Order - Broken order chain)");
		// create new order message
		SBO_NEW_ORDER_ACK.put(TestUtils.TAG_TransactTime, UtcTimestampConverter.convert(new Date(), false));
		msg = TestUtils.buildMessage((Map) CH.m(TestUtils.TAG_MsgType, quickfix.field.MsgType.EXECUTION_REPORT), SBO_NEW_ORDER_ACK);

		//  DROPCOPY1 -> FIX2AMI.
		DROPCOPY_CONTEXT.getSenderFixSession().getSenderSession().send(msg);
		ToolUtils.pause(MSG_DELAY);

		clOrdID = SBO_NEW_ORDER_ACK.get(TestUtils.TAG_ClOrdID);
		lasIncomingMsg = MsgRoutingProcessor.getLastIncomingMessage(clOrdID);

		// both incoming and un-support message will be null since QuickFix reject the message at the session layer due to missing require tag (53).
		Assert.assertTrue("Broken order chain scenario: New Order Ack did not get through to Broken Order message table.",
				TestUtils.isMessageMatch(lasIncomingMsg, AmiPublishProcessor.getlastMsg(MSG_PROCESS_STATUS.BROKEN_ORDER_CHAIN, clOrdID, TestUtils.ORDER_TYPE), IGNORE_TAGS));

		LH.info(log, "\n   Partial Fill (missing New Order - Broken order chain)");
		// create new order message
		SBO_PARTIAL_FILL.put(TestUtils.TAG_TransactTime, UtcTimestampConverter.convert(new Date(), false));
		msg = TestUtils.buildMessage((Map) CH.m(TestUtils.TAG_MsgType, quickfix.field.MsgType.EXECUTION_REPORT), SBO_PARTIAL_FILL);

		//  DROPCOPY1 -> FIX2AMI.
		DROPCOPY_CONTEXT.getSenderFixSession().getSenderSession().send(msg);
		ToolUtils.pause(MSG_DELAY);

		clOrdID = SBO_PARTIAL_FILL.get(TestUtils.TAG_ClOrdID);
		lasIncomingMsg = MsgRoutingProcessor.getLastIncomingMessage(clOrdID);

		// both incoming and un-support message will be null since QuickFix reject the message at the session layer due to missing require tag (53).
		Assert.assertTrue("Broken order chain scenario: Partial Fill did not get through to Broken Trade message table.",
				TestUtils.isMessageMatch(lasIncomingMsg, AmiPublishProcessor.getlastMsg(MSG_PROCESS_STATUS.BROKEN_ORDER_CHAIN, clOrdID, TestUtils.TRADE_TYPE), IGNORE_TAGS));
	}

	@Test
	public void testCancelScenario() throws ConfigError, FieldConvertError, InvalidMessage, FieldNotFound, ParseException {
		LH.info(log, "\n\nScenario test (Cancel) started...");

		sendAndVerify(DROPCOPY_CONTEXT, FIX2AMI_CONTEXT, SC_NEW_ORDER, false, quickfix.field.MsgType.ORDER_SINGLE, "   New Order",
				"Cancel scenario: New Order did not get through.", PAUSE_TIME, IGNORE_TAGS);

		sendAndVerify(DROPCOPY_CONTEXT, FIX2AMI_CONTEXT, SC_NEW_ORDER_ACK, false, quickfix.field.MsgType.EXECUTION_REPORT, "   New Order Ack",
				"Cancel scenario: New Order Ack did not get through.", PAUSE_TIME, IGNORE_TAGS);

		sendAndVerify(DROPCOPY_CONTEXT, FIX2AMI_CONTEXT, SC_CANCEL_REQUEST, false, quickfix.field.MsgType.ORDER_CANCEL_REQUEST, "   Cancel Request",
				"Cancel scenario: Cancel Request did not get through.", PAUSE_TIME, IGNORE_TAGS);

		sendAndVerify(DROPCOPY_CONTEXT, FIX2AMI_CONTEXT, SC_CANCEL_PENDING, false, quickfix.field.MsgType.EXECUTION_REPORT, "   Cancel Pending",
				"Cancel scenario: Cancel Pending did not get through.", PAUSE_TIME, IGNORE_TAGS);

		sendAndVerify(DROPCOPY_CONTEXT, FIX2AMI_CONTEXT, SC_CANCELLED, false, quickfix.field.MsgType.EXECUTION_REPORT, "   Cancelled",
				"Cancel scenario: Cancelled did not get through.", PAUSE_TIME, IGNORE_TAGS);
	}

	@Test
	public void testDoubleModScenario() throws ConfigError, FieldConvertError, InvalidMessage, FieldNotFound, ParseException {
		LH.info(log, "\n\nScenario test (Double Mod) started...");

		sendAndVerify(DROPCOPY_CONTEXT, FIX2AMI_CONTEXT, SR_NEW_ORDER, false, quickfix.field.MsgType.ORDER_SINGLE, "   New Order",
				"Double Mod scenario: New Order did not get through.", PAUSE_TIME, IGNORE_TAGS);

		sendAndVerify(DROPCOPY_CONTEXT, FIX2AMI_CONTEXT, SR_NEW_ORDER_ACK, false, quickfix.field.MsgType.EXECUTION_REPORT, "   New Order Ack",
				"Double Mod scenario: New Order Ack did not get through.", PAUSE_TIME, IGNORE_TAGS);

		sendAndVerify(DROPCOPY_CONTEXT, FIX2AMI_CONTEXT, SR_REPLACE_REQUEST, false, quickfix.field.MsgType.ORDER_CANCEL_REPLACE_REQUEST, "   Mod Request",
				"Double Mod scenario: Replace Request did not get through.", PAUSE_TIME, IGNORE_TAGS);

		sendAndVerify(DROPCOPY_CONTEXT, FIX2AMI_CONTEXT, SR_REPLACE_PENDING, false, quickfix.field.MsgType.EXECUTION_REPORT, "   Mod Pending",
				"Double Mod scenario: Replace Pending did not get through.", PAUSE_TIME, IGNORE_TAGS);

		sendAndVerify(DROPCOPY_CONTEXT, FIX2AMI_CONTEXT, SR_REPLACED, false, quickfix.field.MsgType.EXECUTION_REPORT, "   Replaced",
				"Double Mod scenario: Replaced did not get through.", PAUSE_TIME, IGNORE_TAGS);

		sendAndVerify(DROPCOPY_CONTEXT, FIX2AMI_CONTEXT, SR_REPLACE_REQUEST2, false, quickfix.field.MsgType.ORDER_CANCEL_REPLACE_REQUEST, "   Mod Request 2",
				"Double Mod scenario: Replace Request 2 did not get through.", PAUSE_TIME, IGNORE_TAGS);

		sendAndVerify(DROPCOPY_CONTEXT, FIX2AMI_CONTEXT, SR_REPLACE_PENDING2, false, quickfix.field.MsgType.EXECUTION_REPORT, "   Mod Pending 2",
				"Double Mod scenario: Replace Pending 2 did not get through.", PAUSE_TIME, IGNORE_TAGS);

		sendAndVerify(DROPCOPY_CONTEXT, FIX2AMI_CONTEXT, SR_REPLACED2, false, quickfix.field.MsgType.EXECUTION_REPORT, "   Replaced 2",
				"Double Mod scenario: Replaced 2 did not get through.", PAUSE_TIME, IGNORE_TAGS);

	}

	@Test
	public void testFillScenario() throws ConfigError, FieldConvertError, InvalidMessage, FieldNotFound, ParseException {
		LH.info(log, "\n\nScenario test (Fill) started...");

		sendAndVerify(DROPCOPY_CONTEXT, FIX2AMI_CONTEXT, SF_NEW_ORDER, false, quickfix.field.MsgType.ORDER_SINGLE, "   New Order", "Fill scenario: New Order did not get through.",
				PAUSE_TIME, IGNORE_TAGS);

		sendAndVerify(DROPCOPY_CONTEXT, FIX2AMI_CONTEXT, SF_NEW_ORDER_ACK, false, quickfix.field.MsgType.EXECUTION_REPORT, "   New Order Ack",
				"Fill scenario: New Order Ack did not get through.", PAUSE_TIME, IGNORE_TAGS);

		sendAndVerify(DROPCOPY_CONTEXT, FIX2AMI_CONTEXT, SF_PARTIAL_FILL, true, quickfix.field.MsgType.EXECUTION_REPORT, "   Partially fill",
				"Fill scenario: Partially fill did not get through.", PAUSE_TIME, IGNORE_TAGS);

		sendAndVerify(DROPCOPY_CONTEXT, FIX2AMI_CONTEXT, SF_FILLED, true, quickfix.field.MsgType.EXECUTION_REPORT, "   Fill", "Fill scenario: Filled did not get through.",
				PAUSE_TIME, IGNORE_TAGS);
		//		pause(1000000);
		sendAndVerify(DROPCOPY_CONTEXT, FIX2AMI_CONTEXT, SF_DONE_FOR_DAY, false, quickfix.field.MsgType.EXECUTION_REPORT, "   Done For Day",
				"Fill scenario: Done For Day did not get through.", PAUSE_TIME, IGNORE_TAGS);
	}

	@Test
	public void testCancelReject() throws ConfigError, FieldConvertError, InvalidMessage, FieldNotFound, ParseException {
		LH.info(log, "\n\nScenario test (Cancel Reject) started...");

		sendAndVerify(DROPCOPY_CONTEXT, FIX2AMI_CONTEXT, SCR_NEW_ORDER, false, quickfix.field.MsgType.ORDER_SINGLE, "   New Order",
				"Cancel Reject scenario: New Order did not get through.", PAUSE_TIME, IGNORE_TAGS);

		sendAndVerify(DROPCOPY_CONTEXT, FIX2AMI_CONTEXT, SCR_NEW_ORDER_ACK, false, quickfix.field.MsgType.EXECUTION_REPORT, "   New Order Ack",
				"Cancel Reject scenario: New Order Ack did not get through.", PAUSE_TIME, IGNORE_TAGS);

		sendAndVerify(DROPCOPY_CONTEXT, FIX2AMI_CONTEXT, SCR_CANCEL_REQUEST, false, quickfix.field.MsgType.ORDER_CANCEL_REQUEST, "   Cancel Request",
				"Cancel Reject scenario: Cancel Request did not get through.", PAUSE_TIME, IGNORE_TAGS);

		sendAndVerify(DROPCOPY_CONTEXT, FIX2AMI_CONTEXT, SCR_FILLED, true, quickfix.field.MsgType.EXECUTION_REPORT, "   Fill",
				"Cancel Reject scenario: Filled did not get through.", PAUSE_TIME, IGNORE_TAGS);

		sendAndVerify(DROPCOPY_CONTEXT, FIX2AMI_CONTEXT, SCR_CANCEL_REJECT, false, quickfix.field.MsgType.ORDER_CANCEL_REJECT, "   Cancel Reject",
				"Cancel Reject scenario: Cancel Reject did not get through.", PAUSE_TIME, IGNORE_TAGS);
	}

	@Test
	public void testTradeBust() throws ConfigError, FieldConvertError, InvalidMessage, FieldNotFound, ParseException {
		LH.info(log, "\n\nScenario test (Trade Bust) started...");

		sendAndVerify(DROPCOPY_CONTEXT, FIX2AMI_CONTEXT, SB_NEW_ORDER, false, quickfix.field.MsgType.ORDER_SINGLE, "   New Order",
				"Trade Bust scenario: New Order did not get through.", PAUSE_TIME, IGNORE_TAGS);

		sendAndVerify(DROPCOPY_CONTEXT, FIX2AMI_CONTEXT, SB_NEW_ORDER_ACK, false, quickfix.field.MsgType.EXECUTION_REPORT, "   New Order Ack",
				"Trade Bust scenario: New Order Ack did not get through.", PAUSE_TIME, IGNORE_TAGS);

		sendAndVerify(DROPCOPY_CONTEXT, FIX2AMI_CONTEXT, SB_FILL, true, quickfix.field.MsgType.EXECUTION_REPORT, "   Fill", "Trade Bust scenario: Filled did not get through.",
				PAUSE_TIME, IGNORE_TAGS);

		sendAndVerify(DROPCOPY_CONTEXT, FIX2AMI_CONTEXT, SB_TRADE_CORRECT, true, quickfix.field.MsgType.EXECUTION_REPORT, "   Trade Correct (lastPx)",
				"Trade Bust scenario: Cancel Request did not get through.", PAUSE_TIME, IGNORE_TAGS);

		sendAndVerify(DROPCOPY_CONTEXT, FIX2AMI_CONTEXT, SB_TRADE_BUST, true, quickfix.field.MsgType.EXECUTION_REPORT, "   Trade Bust",
				"Trade Bust scenario: Trade bust did not get through.", PAUSE_TIME, IGNORE_TAGS);
	}

	// sample messages
	//
	//
	// Unsupport message type
	final static Map<Integer, String> SU_NEW_ORDER = ToolUtils.createMap(11, "NF 0500/03252009", 54, "1", 38, "3000", 55, "MMM", 40, "2", 44, "169.33", 59, "0", 47, "A", 60,
			"20090325-15:08:17", 21, "1", 207, "N", 58, "New Order (Unsupport Message Type)");
	// 35=C
	final static Map<Integer, String> SU_EMAIL = ToolUtils.createMap(164, "emailId123", 94, "0", 147, "testing", 33, "1", 58, "first line of text", 11, "NF 0501/03252009");

	//
	// Broken Order chain
	final static Map<Integer, String> SBO_NEW_ORDER_ACK = ToolUtils.createMap(55, "NVDA", 37, "NF 0333/03262009", 11, "NF 0333/03262009", 17, "0", 20, "0", 39, "0", 150, "0", 54,
			"1", 38, "200", 40, "2", 59, "0", 31, "0", 32, "0", 14, "0", 6, "0", 151, "200", 60, "20090402-18:11:47", 58, "Ack (Broken order chain)", 47, "A", 207, "N", 30, "N");

	final static Map<Integer, String> SBO_PARTIAL_FILL = ToolUtils.createMap(55, "NVDA", 37, "NF 0334/04022009", 11, "NF 0334/04022009", 17, "NF 0334/04022009001001001", 20, "0",
			39, "1", 150, "1", 54, "1", 38, "200", 40, "2", 59, "0", 31, "599.50", 32, "100", 14, "100", 6, "599.50", 151, "100", 60, "20090402-18:11:47", 58,
			"Partial Fill (Broken order chain)", 30, "N", 76, "0034", 207, "N", 47, "A", 9430, "NX", 9483, "000006", 9578, "1", 382, "1", 375, "TOD", 337, "0000", 437, "100", 438,
			"20090402-18:11:47", 9579, "0000100001", 9426, "1", 9433, "0034", 29, "1", 63, "0", 9440, "001001001");

	//
	// Cancel scenario
	final static Map<Integer, String> SC_NEW_ORDER = ToolUtils.createMap(11, "NF 0570/03252009", 54, "1", 38, "1000", 55, "PLTR", 40, "2", 44, "28.47", 59, "0", 47, "A", 60,
			"20090325-15:08:17", 21, "1", 207, "N", 58, "New Order (Cancel scenario)");

	final static Map<Integer, String> SC_NEW_ORDER_ACK = ToolUtils.createMap(55, "PLTR", 37, "NF 0570/03252009", 11, "NF 0570/03252009", 17, "0", 20, "0", 39, "0", 150, "0", 54,
			"1", 38, "1000", 40, "2", 59, "0", 31, "0", 32, "0", 14, "0", 6, "0", 151, "1000", 60, "20090402-18:11:47", 58, "Ack (Cancel scenario)", 47, "A", 207, "N", 30, "N");

	final static Map<Integer, String> SC_CANCEL_REQUEST = ToolUtils.createMap(41, "NF 0570/03252009", 37, "NF 0570/03252009", 11, "NF 0571/03252009", 54, "1", 38, "1000", 55,
			"PLTR", 60, "20090325-15:08:50", 207, "N", 9428, "100", 9429, "900", 58, "Cancel Request (Cancel scenario)");

	final static Map<Integer, String> SC_CANCEL_PENDING = ToolUtils.createMap(55, "PLTR", 37, "NF 0570/03252009", 11, "NF 0571/03252009", 41, "NF 0570/03252009", 17, "0", 20, "0",
			39, "6", 150, "6", 54, "1", 38, "1000", 31, "0", 32, "0", 14, "0", 6, "0", 151, "0", 60, "20090325-15:08:50", 58, "Cancel Pending (Cancel scenario)", 207, "N", 30,
			"N");

	final static Map<Integer, String> SC_CANCELLED = ToolUtils.createMap(55, "PLTR", 37, "NF 0570/03252009", 11, "NF 0571/03252009", 41, "NF 0570/03252009", 17, "0", 20, "0", 39,
			"4", 150, "4", 54, "1", 38, "1000", 40, "2", 44, "28.4700", 59, "0", 31, "0", 32, "0", 14, "0", 6, "0", 151, "0", 60, "20090325-15:08:50", 58, "Cancelled (Done)", 30,
			"N", 47, "A", 207, "N", 29, "1");

	//
	//
	// Replace scenario
	final static Map<Integer, String> SR_NEW_ORDER = ToolUtils.createMap(11, "NF 0573/03252009", 54, "1", 38, "1000", 55, "LI", 40, "2", 44, "25.47", 59, "0", 47, "A", 60,
			"20090325-15:14:27", 21, "1", 207, "N", 58, "New Order (2 Replace scenario)");

	final static Map<Integer, String> SR_NEW_ORDER_ACK = ToolUtils.createMap(55, "LI", 37, "NF 0573/03252009", 11, "NF 0573/03252009", 17, "0", 20, "0", 39, "0", 150, "0", 54, "1",
			38, "1000", 40, "2", 59, "0", 31, "0", 32, "0", 14, "0", 6, "0", 151, "1000", 60, "20090402-18:11:47", 58, "Ack (2 Replace scenario)", 47, "A", 207, "N", 30, "N");

	// 35=G change Qty.
	final static Map<Integer, String> SR_REPLACE_REQUEST = ToolUtils.createMap(11, "NF 0574/03252009", 37, "NF 0573/03252009", 41, "NF 0573/03252009", 54, "1", 38, "2000", 55,
			"LI", 40, "2", 44, "25.47", 59, "0", 47, "A", 60, "20090325-15:14:47", 21, "1", 207, "N", 58, "Replace Request (Qty to 2000)");

	// 35=8
	final static Map<Integer, String> SR_REPLACE_PENDING = ToolUtils.createMap(55, "LI", 37, "NF 0574/03252009", 11, "NF 0574/03252009", 41, "NF 0573/03252009", 17, "0", 20, "0",
			39, "E", 150, "E", 54, "1", 38, "2000", 40, "2", 31, "0", 32, "0", 14, "0", 6, "0", 151, "2000", 60, "20090325-15:14:48", 58, "Replace Pending (Qty to 2000)", 207, "N",
			30, "N");

	// 35=8
	final static Map<Integer, String> SR_REPLACED = ToolUtils.createMap(55, "LI", 37, "NF 0573/03252009", 11, "NF 0574/03252009", 41, "NF 0573/03252009", 17, "0", 20, "0", 39, "5",
			150, "5", 54, "1", 38, "2000", 40, "2", 44, "25.4700", 59, "0", 31, "0", 32, "0", 14, "0", 6, "0", 151, "2000", 60, "20090325-15:14:48", 58, "Replaced (Qty to 2000)",
			30, "N", 47, "A", 207, "N", 29, "1");

	//35=G change price.
	final static Map<Integer, String> SR_REPLACE_REQUEST2 = ToolUtils.createMap(11, "NF 0575/03252009", 37, "NF 0574/03252009", 41, "NF 0574/03252009", 54, "1", 38, "2000", 55,
			"LI", 40, "2", 44, "30.22", 59, "0", 47, "A", 60, "20090325-15:15:04", 21, "1", 207, "N", 58, "Replace Request (Px to 30.22)");

	// 35=8 
	final static Map<Integer, String> SR_REPLACE_PENDING2 = ToolUtils.createMap(55, "LI", 37, "NF 0575/03252009", 11, "NF 0575/03252009", 41, "NF 0574/03252009", 17, "0", 20, "0",
			39, "E", 150, "E", 54, "1", 38, "2000", 40, "2", 31, "0", 32, "0", 14, "0", 6, "0", 151, "2000", 60, "20090325-15:15:05", 58, "Replace Pending (Px to 30.22)", 207, "N",
			30, "N");

	// 35=8
	final static Map<Integer, String> SR_REPLACED2 = ToolUtils.createMap(55, "LI", 37, "NF 0574/03252009", 11, "NF 0575/03252009", 41, "NF 0574/03252009", 17, "0", 20, "0", 39,
			"5", 150, "5", 54, "1", 38, "2000", 40, "2", 44, "30.22", 59, "0", 31, "0", 32, "0", 14, "0", 6, "0", 151, "2000", 60, "20090325-15:15:05", 58,
			"Replaced Px to 30.22 (Done)", 30, "N", 47, "A", 207, "N", 29, "1");

	//	
	//
	// partial fill, filled, done for day.
	final static Map<Integer, String> SF_NEW_ORDER = ToolUtils.createMap(11, "NF 0644/04022009", 54, "1", 38, "500", 55, "PAC", 40, "1", 59, "0", 47, "A", 60, "20090402-18:11:47",
			21, "1", 207, "N", 58, "New Order (Done For Day)");

	final static Map<Integer, String> SF_NEW_ORDER_ACK = ToolUtils.createMap(55, "PAC", 37, "NF 0644/04022009", 11, "NF 0644/04022009", 17, "0", 20, "0", 39, "0", 150, "0", 54,
			"1", 38, "500", 40, "1", 59, "0", 31, "0", 32, "0", 14, "0", 6, "0", 151, "500", 60, "20090402-18:11:47", 58, "Ack (Done For Day)", 47, "A", 207, "N", 30, "N");

	final static Map<Integer, String> SF_PARTIAL_FILL = ToolUtils.createMap(55, "PAC", 37, "NF 0644/04022009", 11, "NF 0644/04022009", 17, "NF 0644/04022009001001001", 20, "0", 39,
			"1", 150, "1", 54, "1", 38, "500", 40, "1", 59, "0", 31, "17.00", 32, "100", 14, "100", 6, "17.00", 151, "400", 60, "20090402-18:11:47", 58,
			"Partial Fill (Done For Day)", 30, "N", 76, "0034", 207, "N", 47, "A", 9430, "NX", 9483, "000006", 9578, "1", 382, "1", 375, "TOD", 337, "0000", 437, "100", 438,
			"20090402-18:11:47", 9579, "0000100001", 9426, "1", 9433, "0034", 29, "1", 63, "0", 9440, "001001001");

	final static Map<Integer, String> SF_FILLED = ToolUtils.createMap(55, "PAC", 37, "NF 0644/04022009", 11, "NF 0644/04022009", 17, "NF 0644/04022009002002002", 20, "0", 39, "2",
			150, "2", 54, "1", 38, "500", 40, "1", 59, "0", 31, "18.00", 32, "400", 14, "500", 6, "17.80", 151, "0", 60, "20090402-18:11:47", 58, "Fill (Done For Day)", 30, "N",
			76, "0034", 207, "N", 47, "A", 9430, "NX", 9483, "000007", 9578, "1", 382, "1", 375, "TOD", 337, "0000", 437, "400", 438, "20090402-18:11:57", 9579, "0000200002", 9433,
			"0034", 29, "1", 63, "0", 9440, "002002002");

	final static Map<Integer, String> SF_DONE_FOR_DAY = ToolUtils.createMap(55, "PAC", 37, "NF 0644/04022009", 11, "NF 0644/04022009", 17, "NF 0644/04022009002002002", 20, "0", 39,
			"3", 150, "3", 54, "1", 38, "500", 40, "1", 59, "0", 31, "0", 32, "0", 14, "500", 6, "17.80", 151, "0", 1, "abc", 60, "20090402-18:55:00", 58, "Done for Day (Done)",
			30, "N", 47, "A", 207, "N", 29, "1");

	//
	//
	// Busted
	final static Map<Integer, String> SB_NEW_ORDER = ToolUtils.createMap(11, "NF 0710/04032009", 54, "1", 38, "100", 55, "MSFT", 40, "1", 59, "0", 47, "A", 60, "20090403-18:11:47",
			21, "1", 207, "N", 58, "New order (Correct and Bust)");

	final static Map<Integer, String> SB_NEW_ORDER_ACK = ToolUtils.createMap(55, "MSFT", 37, "NF 0710/04032009", 11, "NF 0710/04032009", 17, "0", 20, "0", 39, "0", 150, "0", 54,
			"1", 38, "100", 40, "1", 59, "0", 31, "0", 32, "0", 14, "0", 6, "0", 151, "100", 60, "20090403-18:11:47", 58, "Ack (Correct and Bust)", 47, "A", 207, "N", 30, "N");

	final static Map<Integer, String> SB_FILL = ToolUtils.createMap(55, "MSFT", 37, "NF 0710/04032009", 11, "NF 0710/04032009", 17, "NF 0710/04032009001001001", 20, "0", 39, "2",
			150, "2", 54, "1", 38, "100", 40, "1", 59, "0", 31, "243", 32, "100", 14, "100", 6, "243", 151, "0", 60, "20090403-18:20:06", 58, "Fill (Correct and Bust)", 76, "8080",
			9433, "8080", 47, "A", 9579, "0000100001", 9440, "001001001", 382, "1", 375, "BARC", 337, "0000", 437, "100", 438, "20090403-18:11:55", 63, "0", 29, "1", 207, "N", 30,
			"N");

	final static Map<Integer, String> SB_TRADE_CORRECT = ToolUtils.createMap(55, "MSFT", 37, "NF 0710/04032009", 11, "NF 0710/04032009", 17, "NF 0710/04032009001001002", 20, "2",
			39, "2", 150, "2", 54, "1", 38, "100", 40, "1", 59, "0", 31, "243.77", 32, "100", 14, "100", 6, "243.77", 151, "0", 60, "20090402-18:20:28", 47, "A", 9579,
			"0000100002", 9440, "001001002", 19, "NF 0710/04032009 001001001", 9704, "0000100001", 29, "1", 9425, "5", 382, "1", 375, "BARC", 337, "0000", 437, "100", 438,
			"20090403-18:12:01", 63, "0", 207, "N", 30, "N", 58, "LastPx correction to 243.77");

	final static Map<Integer, String> SB_TRADE_BUST = ToolUtils.createMap(55, "MSFT", 37, "NF 0710/04032009", 11, "NF 0710/04032009", 17, "NF 0710/04032009001001003", 20, "1", 39,
			"1", 150, "1", 54, "1", 38, "100", 40, "1", 59, "0", 31, "243.77", 32, "100", 14, "0", 6, "243.77", 151, "0", 60, "20090403-18:20:40", 9579, "0000100003", 9440,
			"001001003", 382, "1", 375, "BARC", 337, "0000", 437, "100", 438, "20090403-18:12:06", 76, "8080", 9433, "8080", 19, "NF0710/04032009 001001002", 9704, "0000100002",
			9425, "1", 47, "A", 63, "0", 29, "1", 207, "N", 30, "N", 58, "Trade bust - CumQty to 0 (Done)");

	//
	//
	// Cancel Reject
	final static Map<Integer, String> SCR_NEW_ORDER = ToolUtils.createMap(11, "NF 0810/04032009", 15, "USD", 21, "2", 38, "100", 40, "2", 44, "33.5", 54, "2", 55, "SBH", 59, "0",
			60, "20150413-13:32:51.020", 100, "NYSE", 58, "New Order (Cancel Reject)", 47, "A");
	final static Map<Integer, String> SCR_NEW_ORDER_ACK = ToolUtils.createMap(37, "NF 0810/04032009", 17, "NF 0810/04032009001001", 150, "0", 20, "0", 39, "0", 55, "SBH", 54, "2",
			38, "100", 32, "0", 31, "0.000000", 151, "100", 14, "0", 6, "0.000000", 11, "NF 0810/04032009", 40, "2", 44, "33.500000", 60, "20150413-13:32:51", 59, "0", 47, "A", 15,
			"USD", 58, "Ack (Cancel Reject)");
	final static Map<Integer, String> SCR_CANCEL_REQUEST = ToolUtils.createMap(11, "NF 0811/04032009", 38, "100", 41, "NF 0810/04032009", 54, "2", 55, "SBH", 60,
			"20150413-13:32:56.012", 58, "Cancel Request (Cancel Reject)");
	final static Map<Integer, String> SCR_FILLED = ToolUtils.createMap(37, "NF 0810/04032009", 17, "NF 0810/04032009001001", 150, "2", 20, "0", 39, "2", 55, "SBH", 54, "2", 38,
			"100", 32, "100", 31, "33.500000", 151, "0", 14, "100", 6, "33.500000", 11, "NF 0810/04032009", 40, "2", 44, "33.500000", 60, "20150413-13:32:55.000", 59, "0", 47, "A",
			15, "USD", 30, "XNYS", 29, "1", 851, "1", 58, "Fill (Cancel Reject)");

	// cancel reject 35=9.
	final static Map<Integer, String> SCR_CANCEL_REJECT = ToolUtils.createMap(37, "NF 0810/04032009", 11, "NF 0811/04032009", 41, "NF 0810/04032009", 39, "8", 434, "1", 58,
			"1: Order already complete (Done)");

}
