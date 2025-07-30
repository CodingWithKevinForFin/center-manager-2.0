package com.f1.ami.web;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.msg.AmiRelayCommandDefMessage;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.base.CalcFrame;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorConst;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;
import com.f1.utils.structs.table.stack.CalcTypesTuple2;
import com.f1.utils.structs.table.stack.CalcFrameTuple2;

public class AmiWebCommandWrapper {

	private static final DerivedCellCalculatorConst CALC_FALSE = new DerivedCellCalculatorConst(0, Boolean.FALSE);
	final private String appName;
	final private String title;
	final private String id;
	final private AmiWebObject object;
	final private String help;
	final private Integer relayConnectionId;
	final private Long relayId;
	final private int priority;
	final private Style style;
	final private int selectCountMin;
	final private int selectCountMax;
	final private String[] fields;
	final private String arguments;
	final private boolean isManySelect;
	final private int callbacksMask;
	final private String amiscript;
	final private String whereClause;
	final private String filterClause;
	final private String enabledClause;
	//	final private Node whereNode;
	//	final private Node filteredNode;
	//	final private Node enabledNode;
	final private List<CalcFrame> tmp = new ArrayList<CalcFrame>();
	final private AmiWebService service;
	private String layoutAlias = "";

	private static final Logger log = LH.get();

	public AmiWebCommandWrapper(AmiWebObject_Feed def, AmiWebManager manager, AmiWebService service) {
		this.object = def;
		this.service = service;
		this.appName = def.getAmiApplicationIdName();//(String) manager.getAmiKeyStringFromPool((Short) object.get("P"));//AmiConsts.RESERVED_PARAM_APPLICATION);
		this.whereClause = (String) def.get(AmiConsts.PARAM_COMMAND_WHERE);
		this.filterClause = (String) def.get(AmiConsts.PARAM_COMMAND_FILTER);
		this.enabledClause = (String) def.get(AmiConsts.PARAM_COMMAND_ENABLED);
		//		this.whereNode = parse(this.whereClause, "where");
		//		this.filteredNode = parse(this.filterClause, "filter");
		//		this.enabledNode = parse(this.enabledClause, "enabled");
		String arguments = (String) def.get(AmiConsts.PARAM_COMMAND_ARGUMENTS);
		this.amiscript = (String) def.get(AmiConsts.PARAM_COMMAND_AMISCRIPT);
		String style = (String) def.get(AmiConsts.PARAM_COMMAND_STYLE);
		String selectMode = (String) def.get(AmiConsts.PARAM_COMMAND_SELECT_MODE);
		String fields = (String) def.get(AmiConsts.PARAM_COMMAND_FIELDS);
		this.callbacksMask = (Integer) def.get(AmiConsts.PARAM_COMMAND_CALLBACKS);
		this.title = (String) def.get(AmiConsts.PARAM_COMMAND_NAME);
		this.id = (String) def.get(AmiConsts.PARAM_COMMAND_ID);
		this.help = (String) def.get(AmiConsts.PARAM_COMMAND_HELP);
		this.relayConnectionId = (Integer) def.get(AmiConsts.PARAM_CONNECTION_ID);
		this.relayId = (Long) def.get(AmiConsts.PARAM_CONNECTION_RELAY_ID);
		this.arguments = arguments;
		this.tmp.add(null);
		Object priority = def.get(AmiConsts.PARAM_COMMAND_PRIORITY);
		if (priority != null)
			this.priority = (Integer) priority;
		else
			this.priority = -1;
		this.style = new Style(style);

		boolean isManySelect;
		int selectCountMin;
		int selectCountMax;
		try {
			if (SH.isnt(selectMode)) {
				selectCountMax = selectCountMin = 1;
				isManySelect = false;
			} else if (selectMode.indexOf('-') != -1) {
				isManySelect = true;
				if (selectMode.endsWith("-")) {
					selectCountMin = SH.parseInt(SH.beforeFirst(selectMode, '-'));
					selectCountMax = Integer.MAX_VALUE;
				} else if (selectMode.startsWith("-")) {
					selectCountMin = 0;
					selectCountMax = SH.parseInt(SH.afterFirst(selectMode, '-'));
				} else {
					selectCountMin = SH.parseInt(SH.beforeFirst(selectMode, '-'));
					selectCountMax = SH.parseInt(SH.afterFirst(selectMode, '-'));
				}
			} else {
				isManySelect = true;
				selectCountMin = selectCountMax = SH.parseInt(selectMode);
			}
		} catch (Exception e) {
			isManySelect = false;
			selectCountMin = selectCountMax = -1;
		}
		this.selectCountMax = selectCountMax;
		this.selectCountMin = selectCountMin;
		this.isManySelect = isManySelect;

		if (SH.is(fields))
			this.fields = SH.trimArray(SH.split(',', fields));
		else
			this.fields = new String[0];

	}
	public String getAppName() {
		return this.appName;
	}
	public AmiWebObject getObject() {
		return this.object;
	}

	public String getHelp() {
		return help;
	}

	public String getTitle() {
		return title;
	}

	public String getCmdId() {
		return id;
	}
	public long getId() {
		return object.getId();
	}

	public String getArguments() {
		return arguments;
	}

	public Integer getRelayConnectionId() {
		return relayConnectionId;
	}

	public Long getRelayId() {
		return relayId;
	}

	public static class Argument {
		final public String title;
		final public String id;

		public Argument(String id, String title) {
			this.id = id;
			this.title = title;
		}

		public String getId() {
			return id;
		}

		public String getTitle() {
			return title;
		}
	}

	public Style getStyle() {
		return style;
	}

	public int getPriority() {
		return priority;
	}

	public int getSelectCountMin() {
		return selectCountMin;
	}
	public int getSelectCountMax() {
		return selectCountMax;
	}

	public String[] getFields() {
		return fields;
	}

	public static final String SEPARATOR_TOP = "T";
	public static final String SEPARATOR_BOTTOM = "B";
	public static final String SEPARATOR_BOTH = "TB";

	public enum Separator {
							TOP(SEPARATOR_TOP),
							BOTTOM(SEPARATOR_BOTTOM),
							BOTH(SEPARATOR_BOTH),
							NONE(null);

		public static final Separator DEFAULT = Separator.NONE;

		private String v;

		private Separator(String v) {
			this.v = v;
		}
	};

	class Style {
		public final Separator separator;

		public Style(String json) {
			if (json != null) {
				ObjectToJsonConverter converter = service.getPortletManager().getJsonConverter();
				Map jsonRoot;
				Object object = converter.stringToObject(json);
				if (object instanceof Map)
					jsonRoot = (Map) object;
				else
					throw new RuntimeException("top level of json structure must be a map");
				this.separator = CH.getOrNoThrow(Separator.class, jsonRoot, "separator", Separator.NONE);
			} else
				this.separator = Separator.DEFAULT;
		}
	}

	public boolean getIsManySelect() {
		return this.isManySelect;
	}

	public int getCallbacksMask() {
		return this.callbacksMask;
	}

	public void executeScript(AmiWebDomObject optionalTarget) {
		if (SH.is(amiscript))
			this.service.getScriptManager(layoutAlias).parseAndExecuteAmiScript(amiscript, null, null, this.service.getDebugManager(), AmiDebugMessage.TYPE_CMD_RESPONSE,
					optionalTarget == null ? this.service : optionalTarget, getCmdId());
	}
	public String getAmiScript() {
		return amiscript;
	}
	public String getWhereClause() {
		return whereClause;
	}
	public String getFilterClause() {
		return filterClause;
	}
	public String getEnabledClause() {
		return enabledClause;
	}
	//	public Node getWhereNode() {
	//		return whereNode;
	//	}
	//	public Node getFilteredNode() {
	//		return filteredNode;
	//	}
	//	public Node getEnabledNode() {
	//		return enabledNode;
	//	}

	public boolean isCallbackNow() {
		return MH.allBits(this.callbacksMask, AmiRelayCommandDefMessage.CALLBACK_NOW);
	}
	public boolean isCallbackLogin() {
		return MH.allBits(this.callbacksMask, AmiRelayCommandDefMessage.CALLBACK_USER_LOGIN);
	}
	public boolean isCallbackLogut() {
		return MH.allBits(this.callbacksMask, AmiRelayCommandDefMessage.CALLBACK_USER_LOGOUT);
	}
	public boolean isCallbackClick() {
		return MH.allBits(this.callbacksMask, AmiRelayCommandDefMessage.CALLBACK_USER_CLICK);
	}

	public boolean matchesFilter(com.f1.base.CalcTypes varTypes, CalcFrame values, AmiWebDomObject target) {
		return matches(target, filterClause, varTypes, values);
	}
	public boolean matchesEnabled(com.f1.base.CalcTypes varTypes, CalcFrame values, AmiWebDomObject target) {
		if (OH.isntBetween(1, this.selectCountMin, this.selectCountMax))
			return false;
		return matches(target, enabledClause, varTypes, values);
	}
	public boolean matchesWhere(com.f1.base.CalcTypes varTypes, CalcFrame values, AmiWebDomObject target) {
		if (OH.isntBetween(1, this.selectCountMin, this.selectCountMax))
			return false;
		return matches(target, whereClause, varTypes, values);
	}
	public boolean matchesEnabled(com.f1.base.CalcTypes varTypes, List<? extends CalcFrame> values, AmiWebDomObject target) {
		if (OH.isntBetween(values.size(), this.selectCountMin, this.selectCountMax))
			return false;
		return matches(target, enabledClause, varTypes, values);
	}
	public boolean matchesWhere(com.f1.base.CalcTypes varTypes, List<? extends CalcFrame> values, AmiWebDomObject target) {
		if (OH.isntBetween(values.size(), this.selectCountMin, this.selectCountMax))
			return false;
		return matches(target, whereClause, varTypes, values);
	}

	public boolean matchesFilteredAndWhere(com.f1.base.CalcTypes filterTypes, CalcFrame filterValues, com.f1.base.CalcTypes whereTypes, List<? extends CalcFrame> whereValues,
			AmiWebDomObject target) {
		if (OH.isntBetween(whereValues.size(), this.selectCountMin, this.selectCountMax))
			return false;
		if (!matchesFilter(filterTypes, filterValues, target))
			return false;
		com.f1.utils.structs.table.stack.CalcTypesTuple2 unionedTypes = new CalcTypesTuple2(filterTypes, whereTypes);
		if (filterValues.isVarsEmpty()) {
			return matchesWhere(unionedTypes, whereValues, target);
		} else {
			if (CH.isEmpty(whereValues)) {
				if (!matches(target, whereClause, unionedTypes, filterValues))
					return false;
			}
			CalcFrameTuple2 unionedValues = new CalcFrameTuple2(null, null);
			for (int i = 0; i < whereValues.size(); i++) {
				unionedValues.setFrame(filterValues, whereValues.get(i));
				if (!matches(target, whereClause, unionedTypes, unionedValues))
					return false;
			}
		}
		return true;
	}
	private boolean matches(AmiWebDomObject target, String expression, com.f1.base.CalcTypes varTypes, CalcFrame values) {
		if (SH.isnt(expression))
			return true;
		tmp.set(0, values);
		try {
			return matches(target, expression, varTypes, tmp);
		} finally {
			tmp.set(0, null);
		}
	}

	private Tuple2<String, com.f1.base.CalcTypes> tmpKey = new Tuple2<String, com.f1.base.CalcTypes>();

	private boolean matches(AmiWebDomObject target, String expression, com.f1.base.CalcTypes varTypes, List<? extends CalcFrame> values) {
		if (SH.isnt(expression))
			return true;
		tmpKey.setAB(expression, varTypes);
		DerivedCellCalculator calc = cachedCalcs.get(tmpKey);
		if (calc == null) {
			Tuple2<String, com.f1.base.CalcTypes> t = new Tuple2<String, com.f1.base.CalcTypes>(expression, new com.f1.utils.structs.table.stack.BasicCalcTypes(varTypes));
			try {
				calc = this.service.getScriptManager(this.layoutAlias).toCalc(expression, varTypes, null, null);
			} catch (Exception e) {
				LH.info(log, "Could not parse: ", expression);
				calc = CALC_FALSE;

			}
			cachedCalcs.put(t, calc);
		}
		try {
			ReusableCalcFrameStack sf = new ReusableCalcFrameStack(service.createStackFrame(target));
			for (CalcFrame value : values) {
				if (!Boolean.TRUE.equals(calc.get(sf.reset(value))))
					return false;
			}
		} catch (Exception e) {
			LH.info(log, "Could not process: ", calc);
			return false;
		}
		return true;
	}

	private IdentityHashMap<Tuple2<String, com.f1.base.CalcTypes>, DerivedCellCalculator> cachedCalcs = new IdentityHashMap<Tuple2<String, com.f1.base.CalcTypes>, DerivedCellCalculator>();

}
