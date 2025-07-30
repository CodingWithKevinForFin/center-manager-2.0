package com.f1.common;

import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.junit.Assert;

import com.f1.fix2ami.processor.AbstractFix2AmiProcessor;
import com.f1.fix2ami.processor.AbstractFix2AmiProcessor.MSG_PROCESS_STATUS;
import com.f1.fix2ami.processor.AmiPublishProcessor;
import com.f1.fix2ami.processor.MsgRoutingProcessor;
import com.f1.fix2ami.tool.ToolUtils;
import com.f1.transportManagement.SessionManager;
import com.f1.utils.CH;
import com.f1.utils.LH;

import quickfix.DataDictionary;
import quickfix.DefaultDataDictionaryProvider;
import quickfix.Field;
import quickfix.FieldConvertError;
import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.field.converter.UtcTimestampConverter;

public class TestUtils {
	private static final Logger log = Logger.getLogger(TestUtils.class.getName());
	private static final String DICTIONARY_VERSION = "FIX.4.2";

	public final static String FIX2AMI_SESSION_NAME = "fix2ami";
	public final static String DROPCOPY_SESSION_NAME = "dropstream";

	public final static int LOGON_DELAY = 5000; // 5 second
	public final static int MSG_DELAY = 500; // .5 second

	public static final String ORDER_TYPE = "ORDER";
	public static final String TRADE_TYPE = "TRADE";

	public static int TAG_MsgType = 35;

	public static int TAG_ClOrdID = 11;
	public static int TAG_Account = 1; // 3forge
	public static int TAG_Country = 421;
	public static int TAG_ExecInst = 18; // 1 (Not held)
	public static int TAG_HandlInst = 21; // 1 (auto,public,no broker intervention
	public static int TAG_IDSource = 22; // 5 (RIC code)
	public static int TAG_OrderQty = 38;
	public static int TAG_OrdType = 40; // 1 (market)
	public static int TAG_SecuirtyID = 48; // ZVZZT
	public static int TAG_Side = 54; // 1 (buy)
	public static int TAG_Symbol = 55; // ZVZZT
	public static int TAG_TimeInForce = 59; // 0 (Day)
	public static int TAG_TransactTime = 60;
	public static int TAG_ExDestination = 100; // NYSE
	public static int TAG_ExecType = 150; // A (PENDING NEW)
	public static int TAG_ExecID = 17; // EXHANG_SIM_EXEC_ID1
	public static int TAG_ExecTransType = 20; // 0 (NEW)
	public static int TAG_OrderID = 37; // 20210201-000001
	public static int TAG_OrdStatus = 39; // A (PENDING NEW)

	public static int TAG_BodyLength = 9;
	public static int TAG_MsgSeqNum = 34;
	public static int TAG_CheckSum = 10;
	public static int TAG_SenderCompID = 49;
	public static int TAG_TargetCompID = 56;
	public static int TAG_SendingTime = 52;
	public static int TAG_OnBehalfOfCompID = 115;
	public static int TAG_SenderSubID = 50;
	public static int TAG_TargetSubID = 57;
	public static int TAG_SecurityExchange = 207;

	public static final Set<Integer> IGNORE_TAGS = CH.s(TAG_BodyLength, TAG_MsgSeqNum, TAG_SenderCompID, TAG_TargetCompID, TAG_CheckSum, TAG_SendingTime, TAG_OnBehalfOfCompID,
			TAG_SenderSubID, TAG_TargetSubID);

	private static DataDictionary DICTIONARY = new DefaultDataDictionaryProvider().getSessionDataDictionary(DICTIONARY_VERSION);

	public static void removeTags(Map<Integer, String> msgMap, int... tags) {
		for (int aTag : tags) {
			msgMap.remove(aTag);
		}
	}

	public static boolean isMessageMatch(final Message expectedMessage, final Message actualMessage, final Set<Integer> excludedTags) {
		if (null == expectedMessage || null == actualMessage) {
			return expectedMessage == actualMessage;
		}

		String clOrdID = AbstractFix2AmiProcessor.getTagValue(DICTIONARY, expectedMessage, TAG_ClOrdID);
		boolean matched = true;
		String msgType = AbstractFix2AmiProcessor.getTagValue(DICTIONARY, expectedMessage, AbstractFix2AmiProcessor.TAG_MsgType);
		Iterator<Field<?>> itr = expectedMessage.iterator();
		while (itr.hasNext()) {
			Field<?> field = itr.next();

			if (excludedTags.contains(field.getTag())) {
				LH.info(log, "ignore tag: ", field.getTag());
				continue;
			}

			if (!DICTIONARY.isGroup(msgType, field.getTag())) {
				// verify field.
				String actualValue = null;
				try {
					actualValue = actualMessage.getString(field.getTag());
				} catch (FieldNotFound fe) {
					LH.info(log, "field is missing in actual message: ", field.getTag());
				}

				String expectedValue = (String) field.getObject();
				if ((null == expectedValue && null != actualValue) || !expectedValue.equals(actualValue)) {
					LH.info(log, "Mismatch value - tag: ", field.getTag(), " expectedValue: ", expectedValue, " actualValue: ", actualValue);
					matched = false;
					break;
				}
			}
		}

		// verify group.
		Iterator<Integer> groupsKeys = expectedMessage.groupKeyIterator();
		while (groupsKeys.hasNext() && matched) {
			int groupCountTag = groupsKeys.next();

			// process each set of repeating group.
			Group expectedGroup = new Group(groupCountTag, 1);
			Group actualGroup = new Group(groupCountTag, 1);
			int i = 1;

			// process each repeating group in a repeating group set.
			while (expectedMessage.hasGroup(i, groupCountTag) && matched) {
				try {
					expectedMessage.getGroup(i, expectedGroup);
				} catch (FieldNotFound fe) {
					LH.warning(log, "expected message with ClOrdID(", clOrdID, ") repeating group ", i, " is missing\n", fe);
				}

				try {
					actualMessage.getGroup(i, actualGroup);
				} catch (FieldNotFound fe) {
					LH.warning(log, "actual message with ClOrdID(", clOrdID, ") repeating group ", i, " is missing\n", fe);
				}

				Iterator<Field<?>> expectedGroupFieldItr = expectedGroup.iterator();
				Field<?> expectedGroupField;
				while (expectedGroupFieldItr.hasNext()) {
					expectedGroupField = expectedGroupFieldItr.next();
					try {
						if (!actualGroup.getString(expectedGroupField.getTag()).equals(expectedGroupField.getObject())) {
							matched = false;
							break;
						}
					} catch (FieldNotFound fne) {
						matched = false;
						break;
					}
				}
				i++;
			}
		}
		return matched;
	}

	public static void sendAndVerify(SessionManager.FixSessionContext senderContext, SessionManager.FixSessionContext targetContext, Map<Integer, String> msgMap,
			boolean verifyTradeToo, final String msgType, final String logComment, final String errorMsg, int puaseTime, final Set<Integer> ignoreTags)
			throws FieldNotFound, InvalidMessage, ParseException, FieldConvertError {
		LH.info(log, logComment);
		msgMap.put(TAG_TransactTime, UtcTimestampConverter.convert(new Date(), false));

		Message msg = ToolUtils.buildMessage(DICTIONARY, (Map) CH.m(TAG_MsgType, msgType), msgMap);

		senderContext.getSenderFixSession().getSenderSession().send(msg);
		ToolUtils.pause(puaseTime);

		final String clOrdID = msg.getString(TAG_ClOrdID);
		Message lastIncomingMsg = MsgRoutingProcessor.getLastIncomingMessage(clOrdID);

		Assert.assertTrue(errorMsg, isMessageMatch(lastIncomingMsg, AmiPublishProcessor.getlastMsg(MSG_PROCESS_STATUS.NO_ERROR, clOrdID, ORDER_TYPE), ignoreTags));
		if (verifyTradeToo) {
			Assert.assertTrue(errorMsg, isMessageMatch(lastIncomingMsg, AmiPublishProcessor.getlastMsg(MSG_PROCESS_STATUS.NO_ERROR, clOrdID, TRADE_TYPE), ignoreTags));
		}
	}

	public static Message buildMessage(Map<Integer, String> header, Map<Integer, String> body) throws ParseException, FieldConvertError {
		return ToolUtils.buildMessage(DICTIONARY, header, body);
	}

}
