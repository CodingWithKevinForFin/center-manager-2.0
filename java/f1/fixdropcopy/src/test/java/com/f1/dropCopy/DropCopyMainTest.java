package com.f1.dropCopy;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.f1.bootstrap.Bootstrap;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.transportManagement.SessionManager;
import com.f1.utils.CH;
import com.f1.utils.LH;

import quickfix.ConfigError;
import quickfix.Field;
import quickfix.FieldConvertError;
import quickfix.FieldNotFound;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.Message.Header;

/*
// System interaction:
//
// cSim (UPSTREAM1<->DROPCOPY1)  <---->    DropCopyMain   <---->  exchangeSim (DOWNSTREAM1<->DROPCOPY1)
//
//                                               |
//                                               |
//                                               |
//                                               V
//                                           FIX2AMMI (FIX2AMI<->DROPCOPY1)
*/

public class DropCopyMainTest {
	private static final Logger log = Logger.getLogger(DropCopyMain.class.getName());
	private volatile static SessionManager SESSION_MANAGER = null;

	private final static String UPSTREAM_SESSION_NAME = "upStream";
	private final static String DOWNSTREAM_SESSION_NAME = "downStream";
	private final static String DOWNSTREAM2_SESSION_NAME = "downStream2";
	private final static String DROPCOPY_SESSION_NAME = "dropStream";
	private final static String CSIM_SESSION_NAME = "cSim";
	private final static String CSIM2_SESSION_NAME = "cSim2";
	private final static String EXCHANGESIM_SESSION_NAME = "exchangeSim";
	private final static String FIX2AMI_SESSION_NAME = "fix2Ami";

	private volatile static SessionManager.FixSessionContext UPSTREAM_CONTEXT = null;
	private volatile static SessionManager.FixSessionContext DOWNSTREAM_CONTEXT = null;
	private volatile static SessionManager.FixSessionContext DROPCOPY_CONTEXT = null;
	private volatile static SessionManager.FixSessionContext CSIM_CONTEXT = null;
	private volatile static SessionManager.FixSessionContext EXCHANGESIM_CONTEXT = null;
	private volatile static SessionManager.FixSessionContext FIX2AMI_CONTEXT = null;

	// for StraightThrough testing.
	private volatile static SessionManager.FixSessionContext CSIM2_CONTEXT = null;
	private volatile static SessionManager.FixSessionContext DOWNSTREAM2_CONTEXT = null;

	@BeforeClass
	public static void setup() throws ConfigError, FieldConvertError, InterruptedException {
		Bootstrap bs = new ContainerBootstrap(DropCopyMain.class, null);
		bs.setLoggingOverrideProperty("quiet");
		bs.setConfigDirProperty("./src/test/config");
		bs.startup();
		SESSION_MANAGER = new SessionManager(bs.getProperties());

		SESSION_MANAGER.start();

		UPSTREAM_CONTEXT = SESSION_MANAGER.getFixSessionContext(UPSTREAM_SESSION_NAME);
		DOWNSTREAM_CONTEXT = SESSION_MANAGER.getFixSessionContext(DOWNSTREAM_SESSION_NAME);
		DOWNSTREAM2_CONTEXT = SESSION_MANAGER.getFixSessionContext(DOWNSTREAM2_SESSION_NAME);
		DROPCOPY_CONTEXT = SESSION_MANAGER.getFixSessionContext(DROPCOPY_SESSION_NAME);
		CSIM_CONTEXT = SESSION_MANAGER.getFixSessionContext(CSIM_SESSION_NAME);
		CSIM2_CONTEXT = SESSION_MANAGER.getFixSessionContext(CSIM2_SESSION_NAME);
		EXCHANGESIM_CONTEXT = SESSION_MANAGER.getFixSessionContext(EXCHANGESIM_SESSION_NAME);
		FIX2AMI_CONTEXT = SESSION_MANAGER.getFixSessionContext(FIX2AMI_SESSION_NAME);

		pause(LOGON_DELAY);
	}

	@AfterClass
	public static void tearDown() throws Exception {
		pause(MSG_DELAY);

		LH.info(log, "Junit test - cleaning up started.");
		SESSION_MANAGER.shutdown();
	}

	@Test
	public void testNewOrderMessageInput() throws ConfigError, FieldConvertError, InvalidMessage {
		// create new order message
		SAMPLE_MSG.put(TAG_ClOrdID, "1111");
		SAMPLE_MSG.put(TAG_OrderQty, "101");
		Message msg = buildMessage((Map) CH.m(TAG_MsgType, MSG_TYPE_ORDER_SINGLE), SAMPLE_MSG);

		// cSim send to DropCopyMain (UPSTREAM1->DROPCOPY1)
		CSIM_CONTEXT.getSenderFixSession().getSenderSession().send(msg);
		pause(MSG_DELAY);

		// expect message being sent to downStream and dropCopy session
		Assert.assertTrue("DropCopyMain: upstream did not receive csim new order.",
				isMessageMatch(msg, UPSTREAM_CONTEXT.getSenderFixSession().getLastIncomingDataMessage(), IGNORE_TAGS));
		Assert.assertTrue("DropCopyMain: downStream did not receive csim new order.",
				isMessageMatch(msg, DOWNSTREAM_CONTEXT.getSenderFixSession().getLastOutgoingDataMessage(), IGNORE_TAGS));
		Assert.assertTrue("DropCopyMain: dropStream did not receive csim new order.",
				isMessageMatch(msg, DROPCOPY_CONTEXT.getSenderFixSession().getLastOutgoingDataMessage(), IGNORE_TAGS));
		Assert.assertTrue("exchnageSim did not receive exchange new order.",
				isMessageMatch(msg, EXCHANGESIM_CONTEXT.getSenderFixSession().getLastIncomingDataMessage(), IGNORE_TAGS));
		Assert.assertTrue("fix2Ami did not receive csim new order.", isMessageMatch(msg, FIX2AMI_CONTEXT.getSenderFixSession().getLastIncomingDataMessage(), IGNORE_TAGS));
	}

	@Test
	public void testNewOrderAckMessageReply() throws InvalidMessage {
		// create new order ack message	
		SAMPLE_MSG.put(TAG_ClOrdID, "4444");
		SAMPLE_MSG.put(TAG_OrderQty, "104");
		SAMPLE_MSG.put(TAG_ExecType, "0");
		SAMPLE_MSG.put(TAG_ExecID, "EXHANG_SIM_EXEC_ID1");
		SAMPLE_MSG.put(TAG_ExecTransType, "0");
		SAMPLE_MSG.put(TAG_OrderID, "20210201-000001");
		SAMPLE_MSG.put(TAG_OrdStatus, "A");
		Message msg = buildMessage((Map) CH.m(TAG_MsgType, MSG_TYPE_EXECUTION_REPORT), SAMPLE_MSG);
		removeTags(TAG_ExecType, TAG_ExecID, TAG_ExecTransType, TAG_OrderID, TAG_OrdStatus);

		// exchangeSim sends to DropCopyMain (downStream->DropCopy)
		EXCHANGESIM_CONTEXT.getSenderFixSession().getSenderSession().send(msg);
		pause(MSG_DELAY);

		// expect message being sent to upstream and dropcopy session
		Assert.assertTrue("DropCopyMain: downStream did not receive exchange new order ack.",
				isMessageMatch(msg, DOWNSTREAM_CONTEXT.getSenderFixSession().getLastIncomingDataMessage(), IGNORE_TAGS));
		Assert.assertTrue("DropCopyMain: upstream did not receive exchange new order ack.",
				isMessageMatch(msg, UPSTREAM_CONTEXT.getSenderFixSession().getLastOutgoingDataMessage(), IGNORE_TAGS));
		Assert.assertTrue("DropCopyMain: dropCopy did not receive exchange new order ack.",
				isMessageMatch(msg, DROPCOPY_CONTEXT.getSenderFixSession().getLastOutgoingDataMessage(), IGNORE_TAGS));
		Assert.assertTrue("csim did not receive exchange new order ack.", isMessageMatch(msg, CSIM_CONTEXT.getSenderFixSession().getLastIncomingDataMessage(), IGNORE_TAGS));
		Assert.assertTrue("fix2Ami did not receive csim new order ack.", isMessageMatch(msg, FIX2AMI_CONTEXT.getSenderFixSession().getLastIncomingDataMessage(), IGNORE_TAGS));
	}

	@Test
	public void testMessageToDropCopySession() throws InvalidMessage {
		// create a new order message.
		SAMPLE_MSG.put(TAG_ClOrdID, "2222");
		SAMPLE_MSG.put(TAG_OrderQty, "102");
		Message msg = buildMessage((Map) CH.m(TAG_MsgType, MSG_TYPE_ORDER_SINGLE), SAMPLE_MSG);

		// take a snapshot of last message.
		Message cSimLastIncomingMessage = CSIM_CONTEXT.getSenderFixSession().getLastIncomingDataMessage();
		Message exchangeSimLastIncomingMessage = EXCHANGESIM_CONTEXT.getSenderFixSession().getLastIncomingDataMessage();
		Message fix2AmiLastIncomingMessage = FIX2AMI_CONTEXT.getSenderFixSession().getLastIncomingDataMessage();

		// send to DropCopy (FIX2AMI->DropCopy)
		DROPCOPY_CONTEXT.getSenderFixSession().getSenderSession().send(msg);
		pause(MSG_DELAY);

		// expect no message to either upStream or downStream session.
		// fix2Ami fromApp still try to write out to target and lastOutgoingDataMessage will be the same as message being sent.
		Assert.assertTrue("DropCopy should not forward incoming message to any target.",
				isMessageMatch(msg, DROPCOPY_CONTEXT.getSenderFixSession().getLastOutgoingDataMessage(), IGNORE_TAGS));
		Assert.assertFalse("DropCopy should not forward incoming message to cSim.", isMessageMatch(cSimLastIncomingMessage, msg, IGNORE_TAGS));
		Assert.assertFalse("DropCopy should not forward incoming message to exchangeSim.", isMessageMatch(exchangeSimLastIncomingMessage, msg, IGNORE_TAGS));
		Assert.assertFalse("DropCopy should not forward incoming message to fix2Ami.", isMessageMatch(fix2AmiLastIncomingMessage, msg, IGNORE_TAGS));
	}

	@Test
	public void testTargetSessionDown() throws InvalidMessage {
		// create a new order message.
		SAMPLE_MSG.put(TAG_ClOrdID, "3333");
		SAMPLE_MSG.put(TAG_OrderQty, "103");
		Message msg = buildMessage((Map) CH.m(TAG_MsgType, MSG_TYPE_ORDER_SINGLE), SAMPLE_MSG);

		// cSim2 sends a msg to the corresponding exchange simulator which is not running.
		CSIM2_CONTEXT.getSenderFixSession().getSenderSession().send(msg);
		pause(MSG_DELAY);

		// downStream should not get any message since it is not connected to an exchange.
		Assert.assertTrue("Target should not receive any incoming message due to traget is not connected",
				null == DOWNSTREAM2_CONTEXT.getSenderFixSession().getLastIncomingDataMessage());
	}

	@Test
	public void testMessagePersistence() throws ConfigError, InvalidMessage {
		// create new order message
		SAMPLE_MSG.put(TAG_ClOrdID, "5555");
		SAMPLE_MSG.put(TAG_OrderQty, "105");
		Message msg = buildMessage((Map) CH.m(TAG_MsgType, MSG_TYPE_ORDER_SINGLE), SAMPLE_MSG);

		// shutdown fix2Ami session.
		FIX2AMI_CONTEXT.getSenderFixSession().shutdown();
		pause(LOGON_DELAY);

		// cSim send to DropCopyMain (UPSTREAM1->DROPCOPY1)
		CSIM_CONTEXT.getSenderFixSession().getSenderSession().send(msg);

		// restore fix2Ami session.
		FIX2AMI_CONTEXT.getSenderFixSession().start();
		pause(LOGON_DELAY);

		// expect message being sent to downStream and dropCopy session
		Assert.assertTrue("DropCopyMain: upstream did not receive csim new order.",
				isMessageMatch(msg, UPSTREAM_CONTEXT.getSenderFixSession().getLastIncomingDataMessage(), IGNORE_TAGS));
		Assert.assertTrue("DropCopyMain: downStream did not receive csim new order.",
				isMessageMatch(msg, DOWNSTREAM_CONTEXT.getSenderFixSession().getLastOutgoingDataMessage(), IGNORE_TAGS));
		Assert.assertTrue("DropCopyMain: dropStream did not receive csim new order.",
				isMessageMatch(msg, DROPCOPY_CONTEXT.getSenderFixSession().getLastOutgoingDataMessage(), IGNORE_TAGS));
		Assert.assertTrue("exchnageSim did not receive exchange new order.",
				isMessageMatch(msg, EXCHANGESIM_CONTEXT.getSenderFixSession().getLastIncomingDataMessage(), IGNORE_TAGS));
		Assert.assertTrue("fix2Ami did not receive csim new order.", isMessageMatch(msg, FIX2AMI_CONTEXT.getSenderFixSession().getLastIncomingDataMessage(), IGNORE_TAGS));
	}

	private final static int LOGON_DELAY = 5000; // 5 second
	private final static int MSG_DELAY = 500; // .5 second

	private static final String MSG_TYPE_ORDER_SINGLE = "D";
	private static final String MSG_TYPE_EXECUTION_REPORT = "8";

	private static int TAG_MsgType = 35;

	private static int TAG_ClOrdID = 11;
	private static int TAG_Account = 1; // 3forge
	private static int TAG_ExecInst = 18; // 1 (Not held)
	private static int TAG_HandlInst = 21; // 1 (auto,private,no broker intervention
	private static int TAG_IDSource = 22; // 5 (RIC code)
	private static int TAG_OrderQty = 38;
	private static int TAG_OrdType = 40; // 1 (market)
	private static int TAG_SecuirtyID = 48; // ZVZZT
	private static int TAG_Side = 54; // 1 (buy)
	private static int TAG_Symbol = 55; // ZVZZT
	private static int TAG_TimeInForce = 59; // 0 (Day)
	private static int TAG_TransactTime = 60;
	private static int TAG_ExDestination = 100; // NYSE
	private static int TAG_ExecType = 150; // A (PENDING NEW)
	private static int TAG_ExecID = 17; // EXHANG_SIM_EXEC_ID1
	private static int TAG_ExecTransType = 20; // 0 (NEW)
	private static int TAG_OrderID = 37; // 20210201-000001
	private static int TAG_OrdStatus = 39; // A (PENDING NEW)

	private static int TAG_BodyLength = 9;
	private static int TAG_MsgSeqNum = 34;
	private static int TAG_CheckSum = 10;
	private static int TAG_SenderCompID = 49;
	private static int TAG_TargetCompID = 56;
	private static int TAG_SendingTime = 52;

	private static final Set<Integer> IGNORE_TAGS = CH.s(TAG_BodyLength, TAG_MsgSeqNum, TAG_SenderCompID, TAG_TargetCompID, TAG_CheckSum, TAG_SendingTime);

	private final static Map<Integer, String> SAMPLE_MSG = CH.m(TAG_ClOrdID, "1111", TAG_Account, "3forge", TAG_ExecInst, "1", TAG_HandlInst, "1", TAG_IDSource, "5", TAG_OrderQty,
			"101", TAG_OrdType, "1", TAG_SecuirtyID, "ZVZZT", TAG_Side, "1", TAG_Symbol, "ZVZZT", TAG_TimeInForce, "0", TAG_TransactTime, "20210201-19:23:09.390",
			TAG_ExDestination, "NYSE");

	private static Message buildMessage(Map<Integer, String> header, Map<Integer, String> body) {
		final Message message = new Message();
		final Header headerMessage = message.getHeader();

		for (Map.Entry<Integer, String> e : body.entrySet())
			message.setString(e.getKey(), e.getValue());

		for (Map.Entry<Integer, String> e : header.entrySet())
			headerMessage.setString(e.getKey(), e.getValue());
		return message;
	}

	private static void removeTags(int... tags) {
		for (int aTag : tags) {
			SAMPLE_MSG.remove(aTag);
		}
	}

	private static void pause(int intervalInMillis) {
		try {
			Thread.sleep(intervalInMillis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean isMessageMatch(final Message expectedMessage, final Message actualMessage, final Set<Integer> excludedTags) {
		if (null == expectedMessage || null == actualMessage) {
			return expectedMessage == actualMessage;
		}

		Iterator<Field<?>> itr = expectedMessage.iterator();

		boolean matched = true;
		while (itr.hasNext()) {
			Field<?> field = itr.next();
			if (excludedTags.contains(field.getTag())) {
				LH.fine(log, "ignore tag: ", field.getTag());
				continue;
			}

			String actualValue = null;
			try {
				actualValue = actualMessage.getString(field.getTag());
			} catch (FieldNotFound fe) {
				LH.fine(log, "field is missing in actual message: ", field.getTag());
			}

			String expectedValue = (String) field.getObject();
			if ((null == expectedValue && null != actualValue) || !expectedValue.equals(actualValue)) {
				LH.fine(log, "Mismatch value - tag: ", field.getTag(), " expectedValue: ", expectedValue, " actualValue: ", actualValue);
				matched = false;
				break;
			}
		}

		return matched;
	}

}
