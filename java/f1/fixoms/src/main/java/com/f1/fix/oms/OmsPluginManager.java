package com.f1.fix.oms;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.f1.base.Lockable;
import com.f1.base.LockedException;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.container.Container;
import com.f1.container.exceptions.ContainerException;
import com.f1.fix.oms.plugin.CancelChildOrderPlugin;
import com.f1.fix.oms.plugin.CancelReplaceChildOrderPlugin;
import com.f1.fix.oms.plugin.NewChildOrderPlugin;
import com.f1.fix.oms.plugin.OmsPlugin;
import com.f1.fix.oms.plugin.ParentAckedPlugin;
import com.f1.fix.oms.plugin.ParentCanceledPlugin;
import com.f1.fix.oms.plugin.ParentExecutionPlugin;
import com.f1.fix.oms.plugin.ParentReplacedPlugin;
import com.f1.pofo.fix.FixOrderInfo;
import com.f1.pofo.fix.FixReport;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.RH;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.IntKeyMap.Node;
import com.f1.utils.structs.IntSet;
import com.f1.utils.structs.table.BasicTable;

public class OmsPluginManager implements Lockable {
	/**
	 * comma separated list of fix tags to retain for new order messages
	 */
	String OPTION_FIX_NEW_ORDER_RETAIN_FIELDS = "com.f1.fix.NewOrderRetainFields";

	/**
	 * comma separated list of fix tags to retain for cancel order messages
	 */
	String OPTION_FIX_CANCEL_RETAIN_FIELDS = "com.f1.fix.CancelRetainFields";

	/**
	 * comma separated list of fix tags to retain for replace order messages
	 */
	String OPTION_FIX_REPLACE_RETAIN_FIELDS = "com.f1.fix.ReplaceRetainFields";

	/**
	 * comma separated list of fix tags to retain for execution messages
	 */
	String OPTION_FIX_EXECUTION_RETAIN_FIELDS = "com.f1.fix.ExecutionRetainFields";

	public static final String OPTION_NEW_CHILD_PLUGIN = "NewChildOrderPlugin";
	public static final String OPTION_CANCEL_REPLACE_CHILD_PLUGIN = "CancelReplaceChildOrderPlugin";
	public static final String OPTION_CANCEL_CHILD_PLUGIN = "CancelChildOrderPlugin";
	public static final String OPTION_PARENT_EXECUTION_PLUGIN = "ParentExecutionPlugin";
	private static final String OPTION_PARENT_REPLACED_PLUGIN = "ParentReplacedPlugin";
	private static final String OPTION_PARENT_CANCELED_PLUGIN = "ParentCanceledPlugin";
	private static final String OPTION_PARENT_ACKED_PLUGIN = "ParentAckedPlugin";

	private static final Logger log = LH.get();

	public static final String SERVICE_ID = "OmsPluginService";

	private NewChildOrderPlugin newChildOrderPlugin = null;
	private CancelReplaceChildOrderPlugin cancelReplaceChildOrderPlugin = null;
	private CancelChildOrderPlugin cancelChildOrderPlugin = null;
	private ParentExecutionPlugin parentExecutionPlugin = null;
	private ParentCanceledPlugin parentCanceledPlugin = null;
	private ParentReplacedPlugin parentReplacedPlugin = null;
	private ParentAckedPlugin parentAckedPlugin = null;

	final private IntKeyMap<String> newChildOrderFixConsts = new IntKeyMap<String>();
	final private IntKeyMap<String> cancelReplaceChildOrderFixConsts = new IntKeyMap<String>();
	final private IntKeyMap<String> cancelChildOrderFixConsts = new IntKeyMap<String>();
	final private IntKeyMap<String> parentExecutionFixConsts = new IntKeyMap<String>();
	final private IntKeyMap<String> parentReplacedFixConsts = new IntKeyMap<String>();
	final private IntKeyMap<String> parentCanceledFixConsts = new IntKeyMap<String>();
	final private IntKeyMap<String> parentAckedFixConsts = new IntKeyMap<String>();

	final private IntKeyMap<Integer> newChildOrderFixTrans = new IntKeyMap<Integer>();
	final private IntKeyMap<Integer> cancelReplaceChildOrderFixTrans = new IntKeyMap<Integer>();
	final private IntKeyMap<Integer> cancelChildOrderFixTrans = new IntKeyMap<Integer>();
	final private IntKeyMap<Integer> parentExecutionFixTrans = new IntKeyMap<Integer>();

	final private IntKeyMap<Integer> parentReplacedFixTrans = new IntKeyMap<Integer>();
	final private IntKeyMap<Integer> parentCanceledFixTrans = new IntKeyMap<Integer>();
	final private IntKeyMap<Integer> parentAckedFixTrans = new IntKeyMap<Integer>();

	private final IntSet newOrderRetainFields = new IntSet();
	private final IntSet replaceOrderRetainFields = new IntSet();
	private final IntSet cancelOrderRetainFields = new IntSet();
	private final IntSet executionReportRetainFields = new IntSet();

	private boolean locked;

	public void processProperties(PropertyController pc) {
		LockedException.assertNotLocked(this);
		this.newChildOrderPlugin = processPlugin(pc, OPTION_NEW_CHILD_PLUGIN, NewChildOrderPlugin.class, this.newChildOrderFixTrans, this.newChildOrderFixConsts);
		this.cancelReplaceChildOrderPlugin = processPlugin(pc, OPTION_CANCEL_REPLACE_CHILD_PLUGIN, CancelReplaceChildOrderPlugin.class, this.cancelReplaceChildOrderFixTrans,
				this.cancelReplaceChildOrderFixConsts);
		this.cancelChildOrderPlugin = processPlugin(pc, OPTION_CANCEL_CHILD_PLUGIN, CancelChildOrderPlugin.class, this.cancelChildOrderFixTrans, this.cancelChildOrderFixConsts);
		this.parentExecutionPlugin = processPlugin(pc, OPTION_PARENT_EXECUTION_PLUGIN, ParentExecutionPlugin.class, this.parentExecutionFixTrans, this.parentExecutionFixConsts);

		this.parentReplacedPlugin = processPlugin(pc, OPTION_PARENT_REPLACED_PLUGIN, ParentReplacedPlugin.class, this.parentReplacedFixTrans, this.parentReplacedFixConsts);
		this.parentCanceledPlugin = processPlugin(pc, OPTION_PARENT_CANCELED_PLUGIN, ParentCanceledPlugin.class, this.parentCanceledFixTrans, this.parentCanceledFixConsts);
		this.parentAckedPlugin = processPlugin(pc, OPTION_PARENT_ACKED_PLUGIN, ParentAckedPlugin.class, this.parentAckedFixTrans, this.parentAckedFixConsts);

		newOrderRetainFields.addAllInts(this.newChildOrderFixTrans.values());
		replaceOrderRetainFields.addAllInts(this.newChildOrderFixTrans.values());

		newOrderRetainFields.addAllInts(this.cancelChildOrderFixTrans.values());
		replaceOrderRetainFields.addAllInts(this.cancelChildOrderFixTrans.values());

		newOrderRetainFields.addAllInts(this.cancelReplaceChildOrderFixTrans.values());
		replaceOrderRetainFields.addAllInts(this.cancelReplaceChildOrderFixTrans.values());

		executionReportRetainFields.addAllInts(this.parentExecutionFixTrans.values());
		executionReportRetainFields.addAllInts(this.parentReplacedFixTrans.values());
		executionReportRetainFields.addAllInts(this.parentCanceledFixTrans.values());
		executionReportRetainFields.addAllInts(this.parentAckedFixTrans.values());

		processRetain(pc, OPTION_FIX_NEW_ORDER_RETAIN_FIELDS, newOrderRetainFields);
		processRetain(pc, OPTION_FIX_REPLACE_RETAIN_FIELDS, replaceOrderRetainFields);
		processRetain(pc, OPTION_FIX_CANCEL_RETAIN_FIELDS, cancelOrderRetainFields);
		processRetain(pc, OPTION_FIX_EXECUTION_RETAIN_FIELDS, executionReportRetainFields);

	}
	private void processRetain(PropertyController pc, String key, IntSet sink) {
		String value = pc.getOptional(key);
		if (value != null) {
			try {
				for (String val : SH.split(',', value))
					if (SH.is(val))
						sink.add(Integer.parseInt(val.trim()));
			} catch (Exception e) {
				throw new RuntimeException("error parsing property: " + key, e);
			}
		}
	}
	private <T extends OmsPlugin> T processPlugin(PropertyController pc, String propKey, Class<T> clazz, IntKeyMap<Integer> fixTranslationSink, IntKeyMap<String> fixConstsSink) {
		try {
			PropertyController subPc = pc.getSubPropertyController(propKey + ".");
			for (String key : subPc.getKeys()) {
				if (key.length() == 0 || !OH.isBetween(key.charAt(0), '0', '9'))
					continue;
				final int fromTag;
				try {
					fromTag = Integer.parseInt(key);
				} catch (NumberFormatException e) {
					continue; //not a number... continue;
				}
				try {
					String value = subPc.getRequired(key).trim();
					if (value.startsWith("\"")) {
						fixConstsSink.put(fromTag, SH.strip(value, "\"", "\"", true));
					} else {
						fixTranslationSink.put(fromTag, Integer.parseInt(value));
					}
				} catch (Exception e) {
					throw new RuntimeException("Invalid fix tag translation value for " + key + ". It should either be an integer or a string enclosed in quotes (\")");
				}
			}
			if (fixTranslationSink.size() > 0)
				LH.info(log, "Fix translations for ", clazz.getSimpleName(), ": ", fixTranslationSink);
			if (fixConstsSink.size() > 0)
				LH.info(log, "Fix consts for ", clazz.getSimpleName(), ": ", fixTranslationSink);
			final T r;
			String className = subPc.getOptional("class", String.class);
			if (className != null) {
				LH.info(log, "Loading class for ", clazz.getSimpleName(), ": ", className);
				r = (T) RH.invokeConstructor(className);
				r.init(pc, subPc, this);
			} else
				r = null;
			return r;
		} catch (Exception e) {
			throw new ContainerException("Error processing oms plugin: " + clazz.getSimpleName(), e);
		}
	}

	public NewChildOrderPlugin getNewChildOrderPlugin() {
		return newChildOrderPlugin;
	}
	public void setNewChildOrderPlugin(NewChildOrderPlugin newChildOrderPlugin) {
		LockedException.assertNotLocked(this);
		this.newChildOrderPlugin = newChildOrderPlugin;
	}

	public CancelReplaceChildOrderPlugin getCancelReplaceChildOrderPlugin() {
		return cancelReplaceChildOrderPlugin;
	}
	public void setCancelReplaceChildOrderPlugin(CancelReplaceChildOrderPlugin cancelReplaceChildOrderPlugin) {
		LockedException.assertNotLocked(this);
		this.cancelReplaceChildOrderPlugin = cancelReplaceChildOrderPlugin;
	}

	public CancelChildOrderPlugin getCancelChildOrderPlugin() {
		return cancelChildOrderPlugin;
	}
	public void setCancelChildOrderPlugin(CancelChildOrderPlugin cancelChildOrderPlugin) {
		LockedException.assertNotLocked(this);
		this.cancelChildOrderPlugin = cancelChildOrderPlugin;
	}

	public ParentExecutionPlugin getParentExecutionPlugin() {
		return parentExecutionPlugin;
	}
	public void setParentExecutionPlugin(ParentExecutionPlugin parentExecutionPlugin) {
		LockedException.assertNotLocked(this);
		this.parentExecutionPlugin = parentExecutionPlugin;
	}

	public ParentCanceledPlugin getParentCanceledPlugin() {
		return parentCanceledPlugin;
	}
	public void setParentCanceledPlugin(ParentCanceledPlugin parentCanceledPlugin) {
		LockedException.assertNotLocked(this);
		this.parentCanceledPlugin = parentCanceledPlugin;
	}

	public ParentReplacedPlugin getParentReplacedPlugin() {
		return parentReplacedPlugin;
	}
	public void setParentReplacedPlugin(ParentReplacedPlugin parentReplacedPlugin) {
		LockedException.assertNotLocked(this);
		this.parentReplacedPlugin = parentReplacedPlugin;
	}

	public ParentAckedPlugin getParentAckedPlugin() {
		return parentAckedPlugin;
	}
	public void setParentAckedPlugin(ParentAckedPlugin parentAckedPlugin) {
		LockedException.assertNotLocked(this);
		this.parentAckedPlugin = parentAckedPlugin;
	}

	public void onStartup(Container container) {
		LockedException.assertNotLocked(this);
		if (this.newChildOrderPlugin != null)
			this.newChildOrderPlugin.onStartup(container);
		if (this.cancelReplaceChildOrderPlugin != null)
			this.cancelReplaceChildOrderPlugin.onStartup(container);
		if (this.cancelChildOrderPlugin != null)
			this.cancelChildOrderPlugin.onStartup(container);
		if (this.parentExecutionPlugin != null)
			this.parentExecutionPlugin.onStartup(container);

		Table mappingTable = new BasicTable(new String[] { "Outbound Message", "Outbound Tag", "Value", "Source" });
		mappingTable.setTitle("Fix Tag Mappings");
		TableList rows = mappingTable.getRows();
		for (Entry<Integer, Integer> map : cancelChildOrderFixTrans)
			rows.addRow("Child Cancel", map.getKey(), map.getValue(), "Constant Value");
		for (Entry<Integer, String> map : cancelChildOrderFixConsts)
			rows.addRow("Child Cancel", map.getKey(), map.getValue(), "Parent Order / Replace");
		for (Entry<Integer, String> map : cancelReplaceChildOrderFixConsts)
			rows.addRow("Child Replace", map.getKey(), map.getValue(), "Constant Value");
		for (Entry<Integer, Integer> map : cancelReplaceChildOrderFixTrans)
			rows.addRow("Child Replace", map.getKey(), map.getValue(), "Parent Order / Replace");
		for (Entry<Integer, String> map : newChildOrderFixConsts)
			rows.addRow("Child Order", map.getKey(), map.getValue(), "Constant Value");
		for (Entry<Integer, Integer> map : newChildOrderFixTrans)
			rows.addRow("Child Order", map.getKey(), map.getValue(), "Parent Order / Replace");
		for (Entry<Integer, String> map : parentExecutionFixConsts)
			rows.addRow("Parent Execution", map.getKey(), map.getValue(), "Constant Value");
		for (Entry<Integer, Integer> map : parentExecutionFixTrans)
			rows.addRow("Parent Execution", map.getKey(), map.getValue(), "Child Execution");
		for (Entry<Integer, Integer> map : parentExecutionFixTrans)
			rows.addRow("Parent Canceled", map.getKey(), map.getValue(), "Parent Order");
		for (Entry<Integer, Integer> map : parentExecutionFixTrans)
			rows.addRow("Parent Replaced", map.getKey(), map.getValue(), "Parent Order");
		for (Entry<Integer, Integer> map : parentExecutionFixTrans)
			rows.addRow("Parent Acked", map.getKey(), map.getValue(), "Parent Order");

		Table retain = new BasicTable(new String[] { "Type", "Values" });
		retain.getRows().addRow("New Order", CH.sort(newOrderRetainFields));
		retain.getRows().addRow("Cancel Order", CH.sort(cancelOrderRetainFields));
		retain.getRows().addRow("Replace Order", CH.sort(replaceOrderRetainFields));
		retain.getRows().addRow("Execution Report", CH.sort(executionReportRetainFields));
		Table plugins = new BasicTable(new String[] { "Type", "Class Name" });
		plugins.getRows().addRow("New child order plugin", OH.getClassName(this.newChildOrderPlugin));
		plugins.getRows().addRow("Cancel child order plugin", OH.getClassName(this.cancelChildOrderPlugin));
		plugins.getRows().addRow("Cancel Replace child order plugin", OH.getClassName(this.cancelReplaceChildOrderPlugin));
		plugins.getRows().addRow("Parent Execution plugin", OH.getClassName(this.parentExecutionPlugin));
		plugins.getRows().addRow("Parent Replaced plugin", OH.getClassName(this.parentReplacedPlugin));
		plugins.getRows().addRow("Parent Canceled plugin", OH.getClassName(this.parentCanceledPlugin));
		plugins.getRows().addRow("Parent Acked plugin", OH.getClassName(this.parentAckedPlugin));
		LH.info(log, "Fix Tags Mapped for Outbound Messages:" + SH.NEWLINE, TableHelper.toString(mappingTable, "", TableHelper.SHOW_ALL_BUT_TYPES));
		LH.info(log, "Fix Tags Retained for Inbound Messages:" + SH.NEWLINE, TableHelper.toString(retain, "", TableHelper.SHOW_ALL_BUT_TYPES));
		LH.info(log, "Fix Plugins:" + SH.NEWLINE, TableHelper.toString(plugins, "", TableHelper.SHOW_ALL_BUT_TYPES));
		lock();
	}

	public void addNewOrderRetainTag(int tag) {
		LockedException.assertNotLocked(this);
		newOrderRetainFields.add(tag);
	}
	public void addReplaceOrderRetainTag(int tag) {
		LockedException.assertNotLocked(this);
		replaceOrderRetainFields.add(tag);
	}
	public void addCancelOrderRetainTag(int tag) {
		LockedException.assertNotLocked(this);
		cancelOrderRetainFields.add(tag);
	}
	public void addExecutionReportRetainTag(int tag) {
		LockedException.assertNotLocked(this);
		executionReportRetainFields.add(tag);
	}
	static private Map<Integer, String> mapTags(IntKeyMap<Integer> mapping, IntKeyMap<String> consts, Map<Integer, String> source, Map<Integer, String> sink) {
		if (CH.isntEmpty(source)) {
			if (mapping.size() != 0) {
				for (final Node<Integer> entry : mapping) {
					final String value = source.get(entry.getValue());
					if (value != null) {
						if (sink == null)
							sink = new HashMap<Integer, String>();
						sink.put(entry.getIntKey(), value);
					}
				}
			}
		}
		if (consts.size() != 0) {
			if (sink == null)
				sink = new HashMap<Integer, String>();
			for (final Node<String> entry : consts) {
				sink.put(entry.getIntKey(), entry.getValue());
			}
		}
		return sink;
	}

	public void mapParentExecutionTags(Map<Integer, String> tags, FixReport msg) {
		LockedException.assertLocked(this);
		msg.setPassThruTags(mapTags(this.parentExecutionFixTrans, this.parentExecutionFixConsts, tags, msg.getPassThruTags()));
	}
	public void mapParentCanceled(Map<Integer, String> tags, FixReport msg) {
		LockedException.assertLocked(this);
		msg.setPassThruTags(mapTags(this.parentCanceledFixTrans, this.parentCanceledFixConsts, tags, msg.getPassThruTags()));
	}
	public void mapParentReplaced(Map<Integer, String> tags, FixReport msg) {
		LockedException.assertLocked(this);
		msg.setPassThruTags(mapTags(this.parentReplacedFixTrans, this.parentReplacedFixConsts, tags, msg.getPassThruTags()));
	}
	public void mapParentAcked(Map<Integer, String> tags, FixReport msg) {
		LockedException.assertLocked(this);
		msg.setPassThruTags(mapTags(this.parentAckedFixTrans, this.parentAckedFixConsts, tags, msg.getPassThruTags()));
	}
	public void mapNewChildOrderTags(Map<Integer, String> tags, FixOrderInfo msg) {
		LockedException.assertLocked(this);
		msg.setPassThruTags(mapTags(this.newChildOrderFixTrans, this.newChildOrderFixConsts, tags, msg.getPassThruTags()));
	}
	public void mapReplaceChildOrderTags(Map<Integer, String> tags, FixOrderInfo msg) {
		LockedException.assertLocked(this);
		msg.setPassThruTags(mapTags(this.cancelReplaceChildOrderFixTrans, this.cancelReplaceChildOrderFixConsts, tags, msg.getPassThruTags()));
	}
	public void mapCancelChildOrderTags(Map<Integer, String> tags, FixOrderInfo msg) {
		LockedException.assertLocked(this);
		msg.setPassThruTags(mapTags(this.cancelChildOrderFixTrans, this.cancelChildOrderFixConsts, tags, msg.getPassThruTags()));
	}
	@Override
	public void lock() {
		this.locked = true;
	}
	@Override
	public boolean isLocked() {
		return locked;
	}
	public IntSet getNewOrderRetainFields() {
		return newOrderRetainFields;
	}
	public IntSet getReplaceOrderRetainFields() {
		return replaceOrderRetainFields;
	}
	public IntSet getCancelOrderRetainFields() {
		return cancelOrderRetainFields;
	}
	public IntSet getExecutionReportRetainFields() {
		return executionReportRetainFields;
	}

	public void addNewChildOrderMapping(int parentTag, int childTag) {
		LockedException.assertNotLocked(this);
		this.newChildOrderFixTrans.put(parentTag, childTag);
	}
	public void addCancelChildOrderMapping(int parentTag, int childTag) {
		LockedException.assertNotLocked(this);
		this.cancelChildOrderFixTrans.put(parentTag, childTag);
	}
	public void addReplaceChildOrderMapping(int parentTag, int childTag) {
		LockedException.assertNotLocked(this);
		this.cancelReplaceChildOrderFixTrans.put(parentTag, childTag);
	}
	public void addParentExecutionMapping(int parentTag, int childTag) {
		LockedException.assertNotLocked(this);
		this.parentExecutionFixTrans.put(parentTag, childTag);
	}

	public void addNewChildOrderConst(int parentTag, String value) {
		LockedException.assertNotLocked(this);
		this.newChildOrderFixConsts.put(parentTag, value);
	}
	public void addCancelChildOrderConst(int parentTag, String value) {
		LockedException.assertNotLocked(this);
		this.cancelChildOrderFixConsts.put(parentTag, value);
	}
	public void addReplaceChildOrderConst(int parentTag, String value) {
		LockedException.assertNotLocked(this);
		this.cancelReplaceChildOrderFixConsts.put(parentTag, value);
	}
	public void addParentExecutionConst(int parentTag, String value) {
		LockedException.assertNotLocked(this);
		this.parentExecutionFixConsts.put(parentTag, value);
	}

}
