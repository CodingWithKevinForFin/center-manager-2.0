package com.f1.fix2ami.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.fix2ami.Fix2AmiEvent;
import com.f1.fix2ami.Fix2AmiState;
import com.f1.fix2ami.processor.AbstractAmiPublishField.BooleanField2Ami;
import com.f1.fix2ami.processor.AbstractAmiPublishField.CharField2Ami;
import com.f1.fix2ami.processor.AbstractAmiPublishField.DoubleField2Ami;
import com.f1.fix2ami.processor.AbstractAmiPublishField.IntField2Ami;
import com.f1.fix2ami.processor.AbstractAmiPublishField.StringField2Ami;
import com.f1.fix2ami.processor.AbstractAmiPublishField.UtcDateOnlyField2Ami;
import com.f1.fix2ami.processor.AbstractAmiPublishField.UtcTimeOnlyField2Ami;
import com.f1.fix2ami.processor.AbstractAmiPublishField.UtcTimeStampField2Ami;
import com.f1.pofo.fix.MsgType;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;

import quickfix.BooleanField;
import quickfix.CharField;
import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.DefaultDataDictionaryProvider;
import quickfix.DoubleField;
import quickfix.Field;
import quickfix.FieldException;
import quickfix.FieldNotFound;
import quickfix.FieldType;
import quickfix.Group;
import quickfix.GroupAccess;
import quickfix.IntField;
import quickfix.Message;
import quickfix.StringField;
import quickfix.UtcDateOnlyField;
import quickfix.UtcTimeOnlyField;
import quickfix.UtcTimeStampField;
import quickfix.field.Country;
import quickfix.field.Currency;

public abstract class AbstractFix2AmiProcessor extends BasicProcessor<Fix2AmiEvent, Fix2AmiState> {
	private static final Logger log = Logger.getLogger(AbstractFix2AmiProcessor.class.getName());

	public final static int TAG_MsgType = 35;
	public final static int TAG_ClOrdID = 11;
	final static int TAG_OrigClOrdID = 41;
	public final static int TAG_OrdStatus = 39;
	public final static int TAG_TransactTime = 60;
	public final static int TAG_ExecType = 150;
	public final static int TAG_ExecTransType = 20;

	public final static char ExecType_BUST = 'H';
	public final static char ExecTransType_CANCEL = '1';
	public final static char ExecTransType_CORRECT = '2';

	final static String ATTR_RETAIN_TAG_NEW_ORDER = "ami.propagatingtag.NewOrder";
	final static String ATTR_RETAIN_TAG_CANCEL_REQUEST = "ami.propagatingtag.CancelRequest";
	final static String ATTR_RETAIN_TAG_CANCEL_REJECT = "ami.propagatingtag.CancelReject";
	final static String ATTR_RETAIN_TAG_REPLACE_REQUEST = "ami.propagatingtag.ReplaceRequest";
	final static String ATTR_RETAIN_TAG_EXECUTION_REPORT = "ami.propagatingtag.ExecutionReport";
	final static String ATTR_RETAIN_TAG_CANCEL_PENDING = "ami.propagatingtag.CancelPending";
	final static String ATTR_RETAIN_TAG_REPLACE_PENDING = "ami.propagatingtag.ReplacePending";
	final static String ATTR_RETAIN_TAG_CANCELLED = "ami.propagatingtag.Cancelled";
	final static String ATTR_RETAIN_TAG_REPLACED = "ami.propagatingtag.Replaced";
	final static String ATTR_RETAIN_TAG_DONE_FOR_DAY = "ami.propagatingtag.DoneForDay";
	final static String ATTR_RETAIN_TAG_ORDER_ACK = "ami.propagatingtag.OrderAck";
	final static String ATTR_RETAIN_TAG_REJECT = "ami.propagatingtag.Reject";
	final static String ATTR_RETAIN_TAG_ORDER_PENDING_NEW = "ami.propagatingtag.OrderPendingNew";
	final static String ATTR_DATA_DICTIONARY = "ami.DataDictionary";
	final static String ATTR_DATA_DICTIONARY_VERSION = "ami.datadictionary.Version";

	private volatile static DataDictionary DATA_DICTIONARY = null;
	static volatile String ORIG_CLORDID_TAG_NAME = "OrigClOrdID";
	private static final Map<TAG_FILTER_TYPE, Set<Integer>> SELECTED_TAG_MAP = new HashMap<>();

	private static volatile String ORDER_STATUS_COLUMN_NAME = "OrderStatus";
	private static volatile String TRADE_STATUS_COLUMN_NAME = "TradeStatus";

	volatile static char TRADE_BUST_INDICATOR = 'H';
	volatile static char TRADE_CORRECTION_INIDCATOR = 'C';

	final Map<Integer, List<TagProcessListener>> tagCallbacks = new HashMap<>();
	final Map<Integer, List<GroupTagProcessListener>> groupTagCallbacks = new HashMap<>();
	final Set<Integer> propagatingTags;

	public enum MSG_PROCESS_STATUS {
									UNSUPPORT_MSG_TYPE,
									EXECUTIONREPORT_PROCESSING_ERROR,
									BROKEN_ORDER_CHAIN,
									NO_ERROR
	}

	enum TAG_FILTER_TYPE {
							ORDER,
							TRADE,
							FIX
	}

	public static void setOrderStatusColumnName(final String orderStatusColumnName) {
		ORDER_STATUS_COLUMN_NAME = orderStatusColumnName;
	}

	public static String getOrderStatusColumnName() {
		return ORDER_STATUS_COLUMN_NAME;
	}

	public static void setTradeStatusColumnName(final String tradeStatusColumnName) {
		TRADE_STATUS_COLUMN_NAME = tradeStatusColumnName;
	}

	public static String getTradeStatusColumnName() {
		return TRADE_STATUS_COLUMN_NAME;
	}

	public static DataDictionary getDataDictionary() {
		return DATA_DICTIONARY;
	}

	public OutputPort<Fix2AmiEvent> amiPublishPort = newOutputPort(Fix2AmiEvent.class);

	public AbstractFix2AmiProcessor(final PropertyController props, final String attrRetainTag) throws ConfigError {
		super(Fix2AmiEvent.class, Fix2AmiState.class);
		if (null != props) {
			this.propagatingTags = splitToSet(",", props.getOptional(attrRetainTag));
		} else {
			this.propagatingTags = Collections.emptySet();
		}

		if (null == DATA_DICTIONARY) {
			synchronized (AbstractFix2AmiProcessor.class) {
				if (null == DATA_DICTIONARY) {
					final String dictionaryLocation = props.getOptional(ATTR_DATA_DICTIONARY);
					if (null != dictionaryLocation) {
						DATA_DICTIONARY = new DataDictionary(dictionaryLocation);
					} else {
						DATA_DICTIONARY = new DefaultDataDictionaryProvider().getSessionDataDictionary(props.getOptional(ATTR_DATA_DICTIONARY_VERSION, "FIX.4.2"));
					}
					ORIG_CLORDID_TAG_NAME = DATA_DICTIONARY.getFieldName(AbstractFix2AmiProcessor.TAG_OrigClOrdID);
				}
			}
		}
	}

	public static void addSelectedTags(TAG_FILTER_TYPE tableType, final Set<Integer> selectedTags) {
		Set<Integer> currentSelectedTags = SELECTED_TAG_MAP.get(tableType);
		if (null == currentSelectedTags) {
			currentSelectedTags = new HashSet<>();
			SELECTED_TAG_MAP.put(tableType, currentSelectedTags);
		}
		currentSelectedTags.addAll(selectedTags);
	}

	static void setTradeBustIndicator(char indicator) {
		TRADE_BUST_INDICATOR = indicator;
	}

	static char getTradeBustIndicator() {
		return TRADE_BUST_INDICATOR;
	}

	static void setTradeCorrectionIndicator(char indicator) {
		TRADE_CORRECTION_INIDCATOR = indicator;
	}

	static char getTradeCorrectionIndicator() {
		return TRADE_CORRECTION_INIDCATOR;
	}

	public static Set<Integer> getFilter(TAG_FILTER_TYPE tableType) {
		Set<Integer> filter = SELECTED_TAG_MAP.get(tableType);
		if (null == filter) {
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(filter);
	}

	static interface TagProcessListener {
		public void onTag(int tag, Fix2AmiEvent event, Map<Integer, AbstractAmiPublishField> previousOrderstate, Map<Integer, AbstractAmiPublishField> currentOrderState);
	}

	static interface GroupTagProcessListener {
		public void onGroupTag(int tag, Fix2AmiEvent evnt, List<Object> previousRepeatingGroup, List<Object> currentRepeatingGroup);
	}

	void addTagListener(int tag, TagProcessListener listener) {
		List<TagProcessListener> listeners = tagCallbacks.get(tag);
		if (null == listeners) {
			listeners = new CopyOnWriteArrayList<TagProcessListener>();
		}
		listeners.add(listener);
	}

	void removeTagListener(int tag, TagProcessListener listener) {
		List<TagProcessListener> listeners = tagCallbacks.get(tag);
		if (null == listeners) {
			return;
		}
		listeners.remove(listener);
	}

	void removeGroupTagListener(int tag, GroupTagProcessListener listener) {
		List<GroupTagProcessListener> listeners = groupTagCallbacks.get(tag);
		if (null == listeners) {
			return;
		}
		listeners.remove(listener);
	}

	void addGroupTagListener(int tag, GroupTagProcessListener listener) {
		List<GroupTagProcessListener> listeners = groupTagCallbacks.get(tag);
		if (null == listeners) {
			listeners = new CopyOnWriteArrayList<GroupTagProcessListener>();
		}
		listeners.add(listener);
	}

	boolean invokeTagListener(int tag, Fix2AmiEvent event, Map<Integer, AbstractAmiPublishField> previousOrderstate, Map<Integer, AbstractAmiPublishField> currentOrderState) {
		boolean invokedListener = false;
		List<TagProcessListener> listeners = tagCallbacks.get(tag);
		if (null == listeners) {
			return false;
		}
		for (TagProcessListener listener : listeners) {
			listener.onTag(tag, event, previousOrderstate, currentOrderState);
			invokedListener = true;
		}
		return invokedListener;
	}

	boolean invokeGroupTagListener(int tag, Fix2AmiEvent event, List<Object> previousRepeatingGroup, List<Object> currentRepeatingGroup) {
		boolean invokedListener = false;
		List<GroupTagProcessListener> listeners = groupTagCallbacks.get(tag);
		if (null != listeners) {
			for (GroupTagProcessListener listener : listeners) {
				listener.onGroupTag(tag, event, previousRepeatingGroup, currentRepeatingGroup);
				invokedListener = true;
			}
		}
		return invokedListener;
	}

	static class ProcessResult {
		final String clOrdID;
		final String origClOrdID;
		final Map<Integer, AbstractAmiPublishField> orderState;
		final Map<Integer, List<Object>> repeatingGroup;
		final boolean missingOldState;

		ProcessResult(final String clOrdID, final String origClOrdID, final Map<Integer, AbstractAmiPublishField> orderState, final Map<Integer, List<Object>> repeatingGroup,
				boolean missingOldState) {
			this.clOrdID = clOrdID;
			this.origClOrdID = origClOrdID;
			this.orderState = orderState;
			this.repeatingGroup = repeatingGroup;
			this.missingOldState = missingOldState;
		}
	}

	private void retainTags(final Map<Integer, AbstractAmiPublishField> orderMap, final Set<Integer> retainTagsIfNotEmpty) {
		if (retainTagsIfNotEmpty.isEmpty()) {
			return;
		}

		Map<Integer, AbstractAmiPublishField> holder = new HashMap<>();
		for (Integer i : retainTagsIfNotEmpty) {
			holder.put(i, orderMap.get(i));
		}
		orderMap.clear();
		orderMap.putAll(holder);
	}

	ProcessResult processOrder(final Fix2AmiEvent event, final Fix2AmiState state, final Set<Integer> retainTagsIfOldOrderStatePresent) {
		final MsgType msgType = event.getMsgType();
		final Map<Integer, AbstractAmiPublishField> orderState = new HashMap<>();
		final String clOrdID = event.getClOrdID();
		String origClOrdID = null;
		if (MsgType.NEW_ORDER_SINGLE == msgType) {
			origClOrdID = clOrdID;
		} else {
			origClOrdID = state.getOrigClOrdID(getTagValue(event, TAG_OrigClOrdID));
		}
		event.setOrigClOrdID(origClOrdID);

		final Map<Integer, AbstractAmiPublishField> oldOrderState = state.getState(origClOrdID);
		Map<Integer, List<Object>> repeatingGroupMap = new HashMap<>();

		process(event, state, Collections.EMPTY_MAP, orderState, repeatingGroupMap);

		if (null != oldOrderState) {
			retainTags(orderState, retainTagsIfOldOrderStatePresent);
		}

		return new ProcessResult(clOrdID, origClOrdID, orderState, repeatingGroupMap, null == oldOrderState);
	}

	ProcessResult processResponse(final Fix2AmiEvent event, final Fix2AmiState state, final Set<Integer> retainTags) {
		final String clOrdID = event.getClOrdID();
		final String origClOrdID = state.getOrigClOrdID(clOrdID);
		event.setOrigClOrdID(origClOrdID);
		final Map<Integer, AbstractAmiPublishField> oldOrderState = state.getState(origClOrdID);

		final Map<Integer, AbstractAmiPublishField> orderState = new HashMap<>();
		final Map<Integer, List<Object>> repeatingGroupMap = new HashMap<>();

		if (null != oldOrderState) {
			orderState.putAll(oldOrderState);
		}

		process(event, state, oldOrderState, orderState, repeatingGroupMap);

		if (null != retainTags && !retainTags.isEmpty()) {
			retainTags(orderState, retainTags);
		}

		return new ProcessResult(clOrdID, origClOrdID, orderState, repeatingGroupMap, null == oldOrderState);
	}

	private void process(final Fix2AmiEvent event, final Fix2AmiState state, final Map<Integer, AbstractAmiPublishField> oldOrderState,
			final Map<Integer, AbstractAmiPublishField> orderState, final Map<Integer, List<Object>> repeatingGroupMap) {
		final Message msg = event.getFIXMessage();
		final MsgType msgType = event.getMsgType();
		Field<?> field = null;
		AbstractAmiPublishField amiPublishField = null;

		int tag;
		Iterator<Field<?>> itr = msg.iterator();
		while (itr.hasNext()) {
			field = itr.next();
			tag = field.getTag();

			if (!DATA_DICTIONARY.isGroup(String.valueOf(msgType.getEnumValue()), tag)) {
				if (!invokeTagListener(tag, event, oldOrderState, orderState)) {
					amiPublishField = getField(DATA_DICTIONARY, msg, tag);
					if (null != amiPublishField) {
						orderState.put(tag, amiPublishField);
					}
				}
			}
		}

		// process repeating group field
		Map<Integer, List<Object>> oldRepeatingGroupMap = state.getRepeatingGroupMapByClOrdID(event.getOrigClOrdID());

		// callback  use List<OBject>  may change to List<Objec>???
		//		List<Object> currentRepeatingGroupList = null;
		List<Object> oldRepeatingGroupList = null;

		// process repeating groups.
		Iterator<Integer> groupsKeys = msg.groupKeyIterator();
		while (groupsKeys.hasNext()) {
			int groupCountTag = groupsKeys.next();
			int fieldOrder[] = GroupAccess.getFieldOrder(msg, groupCountTag);

			//			oldRepeatingGroup.clear();
			if (null != oldRepeatingGroupMap) {
				oldRepeatingGroupList = oldRepeatingGroupMap.get(groupCountTag);
			}

			// process each set of repeating group.
			Group g = new Group(groupCountTag, 1);
			int i = 1;
			List<Object> repeatingGroupList = new ArrayList<Object>(8);
			repeatingGroupList.add(0); // count being initialized to zero.

			// process each repeating group in a repeating group set.
			Map<Integer, Object> eachGroupData = null;
			while (msg.hasGroup(i, groupCountTag)) {
				eachGroupData = new LinkedHashMap<>();

				try {
					msg.getGroup(i, g);
				} catch (FieldNotFound fe) {
					LH.warning(log, "ClOrdID(", event.getClOrdID(), ") repeating group ", i, " is missing\n", fe);
				}

				//				Iterator<Field<?>> groupFieldItr = g.iterator();
				//				Field<?> groupField;
				//				int groupFieldTag;
				//				Object groupFieldValue;
				//				while (groupFieldItr.hasNext()) {
				//					groupField = groupFieldItr.next();
				//					groupFieldTag = groupField.getTag();
				//					groupFieldValue = groupField.getObject();
				//					eachGroupData.put(groupFieldTag, groupFieldValue);
				//				};

				for (int currentTag : fieldOrder) {
					try {
						eachGroupData.put(currentTag, g.getString(currentTag));
					} catch (FieldNotFound fne) {
						LH.fine(log, "possible optional value was not populated.");
					}
				}
				repeatingGroupList.set(0, i);
				repeatingGroupList.add(eachGroupData);

				//				currentRepeatingGroup.clear();
				//				currentRepeatingGroup.put(groupCountTag, repeatingGroupList);

				// each callback is provided a Map<repeatingGroupCountTag,List<repeatingGroup data, ex. count,
				// Map<Integer,Object> for each repeating group data set.
				//
				//                        groupCountTag -> 2 (count), (first group), (second group)
				//                                                         ^                 ^
				//                                                         |                 |
				//                                                    tag1:value1            |
				//                                                    tag2:value2            |
				//                                                                      tag1:value3
				//                                                                      tag2:value4
				//                                                                      tag3:value5 (optional tag)
				//
				// Listener will need use downcast to specific type for a particular piece of data.
				invokeGroupTagListener(groupCountTag, event, oldRepeatingGroupList, repeatingGroupList);
				i++;
			}

			repeatingGroupMap.put(groupCountTag, repeatingGroupList);
		}
	}

	Map<String, AbstractAmiPublishField> applyFilter(final ProcessResult processResult, TAG_FILTER_TYPE tagFilterType) {
		final Map<Integer, AbstractAmiPublishField> msgMap = processResult.orderState;
		final Map<Integer, List<Object>> repeatingGroupMap = processResult.repeatingGroup;
		final Set<Integer> filter = getFilter(tagFilterType);
		Map<String, AbstractAmiPublishField> result = new HashMap<>();

		for (Map.Entry<Integer, AbstractAmiPublishField> entry : msgMap.entrySet()) {
			if (filter.contains(entry.getKey()) || filter.isEmpty()) {
				if (null != entry.getValue()) {
					result.put(entry.getValue().getColumnName(), entry.getValue());
				}
			}
		}

		StringBuilder builder = new StringBuilder();
		String repeatingGroupCountTagName = null;
		List<Object> repeatingGroupDataList = null;
		for (Map.Entry<Integer, List<Object>> entry : repeatingGroupMap.entrySet()) {
			if (filter.contains(entry.getKey()) || filter.isEmpty()) {
				// concatenate each repeating group onto a csv
				// Client may want to separate the repeating group into different table.
				repeatingGroupCountTagName = DATA_DICTIONARY.getFieldName(entry.getKey());
				if (null == repeatingGroupCountTagName) {
					repeatingGroupCountTagName = String.valueOf(entry.getKey());
				}
				repeatingGroupDataList = entry.getValue();

				builder.setLength(0);
				builder.append(repeatingGroupCountTagName).append('=').append((Integer) repeatingGroupDataList.get(0));
				for (int i = 1; i < repeatingGroupDataList.size(); i++) {
					Map<Integer, Object> aRepeatingGroupMap = (Map<Integer, Object>) repeatingGroupDataList.get(i);
					builder.append(':');
					for (Map.Entry<Integer, Object> tagValueEntry : aRepeatingGroupMap.entrySet()) {
						String name = DATA_DICTIONARY.getFieldName(tagValueEntry.getKey());
						if (null == name) {
							name = String.valueOf(tagValueEntry.getKey());
						}
						builder.append(name).append('=').append(tagValueEntry.getValue().toString()).append(',');
					}
					builder.setLength(builder.length() - 1);
				}
				result.put(repeatingGroupCountTagName, new StringField2Ami(repeatingGroupCountTagName, new StringField(entry.getKey(), builder.toString())));
			}
		}
		return result;
	}

	String getOrderStatus(final Fix2AmiEvent event, char defaultValue) {
		String orderStatus = null;
		try {
			orderStatus = event.getFIXMessage().getString(TAG_OrdStatus);
		} catch (FieldNotFound fne) {
			LH.info(log, "clOrdID(", event.getClOrdID(), "): FIX message did not have tag 39.");
		}

		if (null == orderStatus) {
			orderStatus = String.valueOf(defaultValue);
		}
		return orderStatus;
	}

	String getTagValue(final Fix2AmiEvent event, int tag) {
		String value = null;

		try {
			value = event.getFIXMessage().getString(tag);
		} catch (FieldNotFound fne) {
			LH.fine(log, "clOrdID(", event.getClOrdID(), "): FIX message did not have tag ", tag);
		}
		return value;
	}

	static Set<Integer> splitToSet(final String delimitor, final String string) {
		Set<Integer> result = new HashSet<>();
		if (null != string && !string.isEmpty()) {
			for (String tmp : string.split(delimitor)) {
				result.add(Integer.parseInt(tmp));
			}
		}
		if (result.isEmpty()) {
			return Collections.emptySet();
		}
		return result;
	}

	public static String getTagValue(final Message msg, int tag) {
		return getTagValue(DATA_DICTIONARY, msg, tag);
	}

	public static String getTagValue(final DataDictionary dictionary, final Message msg, int tag) {
		String tagValue = "";
		try {
			if (dictionary.isHeaderField(tag)) {
				tagValue = msg.getHeader().getString(tag);
			} else {
				tagValue = msg.getString(tag);
			}
		} catch (FieldNotFound fne) {
			LH.info(log, "message did not have tag ", tag);
		}
		return tagValue;
	}

	public static AbstractAmiPublishField getField(final DataDictionary dataDictionary, final Message msg, int tag) {
		AbstractAmiPublishField field = null;

		FieldType fieldType = dataDictionary.getFieldType(tag);
		if (null == fieldType) {
			fieldType = FieldType.STRING;
		}
		String name = dataDictionary.getFieldName(tag);
		if (null == name) {
			name = String.valueOf(tag);
		}
		try {
			switch (fieldType.name()) {
				case "AMT":
				case "PRICE":
				case "QTY":
				case "FLOAT":
				case "PRICEOFFSET":
				case "PERCENTAGE":
					field = new DoubleField2Ami(name, msg.getField(new DoubleField(tag)));
					break;
				case "INT":
				case "DAYOFMONTH":
				case "NUMINGROUP":
				case "SEQNUM":
				case "LENGTH":
					field = new IntField2Ami(name, msg.getField(new IntField(tag)));
					break;
				case "CHAR":
					field = new CharField2Ami(name, msg.getField(new CharField(tag)));
					break;
				case "BOOLEAN":
					field = new BooleanField2Ami(name, msg.getField(new BooleanField(tag)));
					break;
				case "UTCDATEONLY":
				case "UTCDATE":
					field = new UtcDateOnlyField2Ami(name, msg.getField(new UtcDateOnlyField(tag)));
					break;
				case "UTCTIMEONLY":
					field = new UtcTimeOnlyField2Ami(name, msg.getField(new UtcTimeOnlyField(tag)));
					break;
				case "LOCALMKTDATE":
				case "UTCTIMESTAMP":
					field = new UtcTimeStampField2Ami(name, msg.getField(new UtcTimeStampField(tag)));
					break;
				case "COUNTRY":
					field = new StringField2Ami(name, msg.getField(new Country()));
					break;
				case "CURRENCY":
					field = new StringField2Ami(name, msg.getField(new Currency()));
					break;
				case "STRING":
				case "EXCHANGE":
				case "MONTHYEAR": // YYYYMM
				case "MULTIPLEVALUESTRING": // space delimited multiple strings	
				case "DATA":
				case "UNKNOWN":
				case "TIME":
				default:
					field = new StringField2Ami(name, msg.getField(new StringField(tag)));
					break;
			}
		} catch (FieldException fe) {
			LH.warning(log, "Conversion encoutered error for tag: ", tag, " expected format " + fieldType.name() + ", instead defaulted to String\n" + fe);
			try {
				field = new StringField2Ami(name, msg.getField(new StringField(tag)));
			} catch (FieldNotFound fne) {
				LH.info(log, "tag: ", tag, " does not exist in the message.");
			}
		} catch (FieldNotFound fne) {
			LH.info(log, "tag: ", tag, " does not exist in the message.");
		}
		return field;
	}

	@Override
	abstract public void processAction(Fix2AmiEvent event, Fix2AmiState state, ThreadScope threadScope) throws Exception;
}
