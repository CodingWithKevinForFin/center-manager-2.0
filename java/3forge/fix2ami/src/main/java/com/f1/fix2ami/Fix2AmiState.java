package com.f1.fix2ami;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.container.impl.BasicState;
import com.f1.fix2ami.processor.AbstractAmiPublishField;

public class Fix2AmiState extends BasicState {
	final private Map<String, String> clOrdID2OrigClOrdID = new HashMap<>();
	//                  ^        ^
	//                  |        |
	//              clOrdID   origClOrdID
	//
	// ex.
	//    New Order   new1     new1
	//    first mod   mod1     new1
	//   second mod   mod2     new1

	final private Map<String, Map<Integer, AbstractAmiPublishField>> orderState = new HashMap<>();
	//                  ^            ^        ^
	//                  |            |        |
	//            origClOrdID       tag     value
	//
	// ex.
	//               new1        38(Qty)     100
	//                           54(Side)    1(BUY)
	//                           55(Symbol)  IBM

	final private Map<String, Map<Integer, List<Object>>> repeatingGroupByClOrdID = new HashMap<>();
	//                 ^            ^              ^
	//                 |            |              |
	//         origClOrdID ->  groupCountTag -> 2 (count), (first group), (second group)
	//                                                         ^                 ^
	//                                                         |                 |
	//                                                    tag1:value1            |
	//                                                    tag2:value2            |
	//                                                                      tag1:value3
	//                                                                      tag2:value4
	//                                                                      tag3:value5 (optional tag)
	//
	// ex.
	//            new1      215(NoRoutingIDs)   2,[216(RoutingType):2,217(RoutingID):Dest1],[216:4,217:Dest2]

	private final Map<String, StringBuilder> orderStatusChain = new HashMap<>();
	//                  ^       ^
	//                  |       |
	//           origClOrdID  <orderStateChain(tag39 values)>
	//
	// ex.
	//           20210210-001   A,0					(pending new, new).
	//           20210210-002   A,0,1,1,E,5,2         (pending new, new, partially filled, partially filled, pending replace, replaced, filled)

	private final Map<String, StringBuilder> tradeStatusChain = new HashMap<>();
	//                  ^       ^
	//                  |       |
	//           origClOrdID  <tradeStateChain(tag39 values)>
	//
	// ex.
	//           20210210-001   A,0					(pending new, new).
	//           20210210-002   A,0,1,1,E,5,2         (pending new, new, partially filled, partially filled, pending replace, replaced, filled)

	private int fixMsgSeqno = 0;

	private final Map<String, Integer> amiMsgSequence = new HashMap<>();
	//                  ^       ^
	//                  |       |
	//           origClOrdID  <AMI Msg sequence>            
	//
	// ex.
	//           20210210-001   0
	//           20210210-002   1
	private int msgSequence = 0;

	public int getAmiMsgSequence(final String origClOrdID, boolean amiMsgSequencePerOrder) {
		Integer tmp = amiMsgSequencePerOrder ? amiMsgSequence.get(origClOrdID) : null;

		if (null == tmp) {
			tmp = new Integer(msgSequence++);
			amiMsgSequence.put(origClOrdID, tmp);
		}
		return tmp;
	}

	public int getFixMsgSequence() {
		return fixMsgSeqno++;
	}

	private void updateStatusChain(Map<String, StringBuilder> statusChain, final String origClOrdID, final String status) {
		final StringBuilder oldStatusChain = statusChain.get(origClOrdID);
		if (null == oldStatusChain) {
			statusChain.put(origClOrdID, new StringBuilder(status));
		} else {
			oldStatusChain.append(',').append(status);
		}
	}

	public String getOrderStatusChain(final String origClOrdID) {
		return orderStatusChain.get(origClOrdID).toString();
	}

	public void saveOrderStatusChain(final String origClOrdID, final String orderStatus) {
		updateStatusChain(orderStatusChain, origClOrdID, orderStatus);
	}

	public String getTradeStatusChain(final String origClOrdID) {
		return tradeStatusChain.get(origClOrdID).toString();
	}

	public void saveTradeStatusChain(final String origClOrdID, final String tradeStatus) {
		updateStatusChain(tradeStatusChain, origClOrdID, tradeStatus);
	}

	public String getOrigClOrdID(final String clOrdID) {
		return clOrdID2OrigClOrdID.get(clOrdID);
	}

	// for mod, cancel ...
	public void addClOrdID(final String clOrdID, final String origClOrdID) {
		String tmpOrigClOrdID = clOrdID2OrigClOrdID.get(origClOrdID);
		if (null == tmpOrigClOrdID) {
			// error - possibly order chain missing a New Order message.
			throw new IllegalStateException("root client order id is missing (first clOrdID).");
		}
		clOrdID2OrigClOrdID.put(clOrdID, tmpOrigClOrdID);
	}

	// for New Order
	public void addClOrdID(final String clOrdID) {
		clOrdID2OrigClOrdID.put(clOrdID, clOrdID);
	}

	public Map<Integer, AbstractAmiPublishField> getState(final String origClOrdID) {
		return orderState.get(origClOrdID);
	}

	public void setOrderState(final String origClOrdID, Map<Integer, AbstractAmiPublishField> state) {
		orderState.put(origClOrdID, state);
	}

	public Map<Integer, List<Object>> getRepeatingGroupMapByClOrdID(final String origClOrdID) {
		return repeatingGroupByClOrdID.get(origClOrdID);
	}

	public void setRepeatingGroupMapByClOrdID(final String origClOrdID, Map<Integer, List<Object>> repeatingGroupMap) {
		Map<Integer, List<Object>> repeatingGroupCountTagMap = repeatingGroupByClOrdID.get(origClOrdID);
		if (null == repeatingGroupCountTagMap) {
			repeatingGroupCountTagMap = new HashMap<Integer, List<Object>>();
			repeatingGroupByClOrdID.put(origClOrdID, repeatingGroupCountTagMap);
		}
		repeatingGroupCountTagMap.putAll(repeatingGroupMap);
	}

	//	public void setRepeatingGroup(final String origClOrdID, int repeatingGroupCountTag, final List<Object> repeatingGroupData) {
	//		Map<Integer, List<Object>> repeatingGroupCountTagMap = repeatingGroupByClOrdID.get(origClOrdID);
	//		if (null == repeatingGroupCountTagMap) {
	//			repeatingGroupCountTagMap = new HashMap<Integer, List<Object>>();
	//			repeatingGroupByClOrdID.put(origClOrdID, repeatingGroupCountTagMap);
	//		}
	//
	//		repeatingGroupCountTagMap.put(repeatingGroupCountTag, repeatingGroupData);
	//	}
}
