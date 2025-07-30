package com.f1.ami.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiDataEntity;
import com.f1.ami.amicommon.msg.AmiDatasourceColumn;
import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.amiscript.AmiDebugManager;
import com.f1.ami.web.amiscript.AmiWebCalcTypesStack;
import com.f1.ami.web.amiscript.AmiWebTopCalcFrameStack;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmManager;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.dm.AmiWebDmsImpl;
import com.f1.ami.web.dm.portlets.AmiWebAddPanelPortlet;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.style.AmiWebStyledPortlet;
import com.f1.base.Bytes;
import com.f1.base.CalcTypes;
import com.f1.base.Caster;
import com.f1.base.Complex;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.base.IterableAndSize;
import com.f1.base.NameSpaceCalcTypes;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.UUID;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletContainer;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.AbstractPortlet;
import com.f1.suite.web.portal.impl.DesktopPortlet;
import com.f1.suite.web.portal.impl.DesktopPortlet.Window;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet.Callback;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.RootPortletDialog;
import com.f1.suite.web.portal.impl.TabPlaceholderPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.OneToOne;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.sql.Tableset;
import com.f1.utils.structs.table.columnar.ColumnarRow;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.BasicMethodFactory;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellMemberMethod;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.FlowControlThrow;
import com.f1.utils.structs.table.derived.FlowControlThrow.Frame;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.BasicCalcTypes;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.CalcTypesTuple2;
import com.f1.utils.structs.table.stack.CalcTypesTuple3;

public class AmiWebUtils {

	public static final char SAVE_CODE_STRING = 'S';
	public static final char SAVE_CODE_DOUBLE = 'D';
	public static final char SAVE_CODE_FLOAT = 'F';
	public static final char SAVE_CODE_INT = 'I';
	public static final char SAVE_CODE_LONG = 'L';
	public static final char SAVE_CODE_UTC = 'T';
	public static final char SAVE_CODE_UTCN = 'N';
	public static final char SAVE_CODE_BOOLEAN = 'B';
	public static final char SAVE_CODE_BINARY = 'Y';
	public static final char SAVE_CODE_CHAR = 'C';
	public static final char SAVE_CODE_SHORT = 'H';
	public static final char SAVE_CODE_BYTE = 'E';
	public static final char SAVE_CODE_BIGINT = 'G';
	public static final char SAVE_CODE_BIGDEC = 'J';
	public static final char SAVE_CODE_COMPLEX = 'X';
	public static final char SAVE_CODE_UUID = 'U';
	public static final String AMI_SCRIPT_FIELD_STYLE = "style.background=#fff4e6|style.color=#42576a|style.font-size=14px";
	public static final Map<String, String> RESERVED_TYPES = new HashMap<String, String>();
	public static final Map<String, String> RESERVED_PARAMS = new HashMap<String, String>();
	private static final Logger log = LH.get();

	static {
		RESERVED_PARAMS.put(AmiConsts.PARAM_CONNECTION_PLUGINS, "Plugins");
		RESERVED_PARAMS.put(AmiConsts.PARAM_CONNECTION_MACHINEID, "Machine Id");
		RESERVED_PARAMS.put(AmiConsts.PARAM_CONNECTION_APPID, "App Id");
		RESERVED_PARAMS.put(AmiConsts.PARAM_CONNECTION_REMOTE_PORT, "Remote Port");
		RESERVED_PARAMS.put(AmiConsts.PARAM_CONNECTION_OPTIONS, "Options");
		RESERVED_PARAMS.put(AmiConsts.PARAM_CONNECTION_ID, "Connection Id");
		RESERVED_PARAMS.put(AmiConsts.PARAM_CONNECTION_TIME, "Time");
		RESERVED_PARAMS.put(AmiConsts.PARAM_CONNECTION_RELAY_ID, "Relay Id");
		RESERVED_PARAMS.put(AmiConsts.PARAM_CONNECTION_MESSAGESCOUNT, "Messages Count");
		RESERVED_PARAMS.put(AmiConsts.PARAM_CONNECTION_ERRORSCOUNT, "Errors Count");
		RESERVED_PARAMS.put(AmiConsts.PARAM_COMMAND_NAME, "Title");
		RESERVED_PARAMS.put(AmiConsts.PARAM_COMMAND_ID, "Command Id");
		RESERVED_PARAMS.put(AmiConsts.PARAM_COMMAND_HELP, "Help");
		RESERVED_PARAMS.put(AmiConsts.PARAM_COMMAND_LEVEL, "Level");
		RESERVED_PARAMS.put(AmiConsts.PARAM_COMMAND_PRIORITY, "Priority");
		RESERVED_PARAMS.put(AmiConsts.PARAM_COMMAND_ENABLED, "Enabled Expression");
		RESERVED_PARAMS.put(AmiConsts.PARAM_COMMAND_STYLE, "Style");
		RESERVED_PARAMS.put(AmiConsts.PARAM_COMMAND_SELECT_MODE, "Selection Mode");
		RESERVED_PARAMS.put(AmiConsts.PARAM_COMMAND_FIELDS, "Fields");
		RESERVED_PARAMS.put(AmiConsts.PARAM_OBJECT_USER, "User");
		RESERVED_PARAMS.put(AmiConsts.PARAM_COMMAND_ARGUMENTS, "Arguments");
		RESERVED_PARAMS.put(AmiConsts.PARAM_COMMAND_WHERE, "Where");
		RESERVED_PARAMS.put(AmiConsts.PARAM_COMMAND_FILTER, "Filter");

		RESERVED_PARAMS.put(AmiConsts.TABLE_PARAM_ID, "AMI-ID");
		RESERVED_PARAMS.put(AmiConsts.TABLE_PARAM_CENTER, "AMI-Center");
		//		RESERVED_PARAMS.put(AmiConsts.TABLE_PAERAM_PARAMS, "Params");
		RESERVED_PARAMS.put(AmiConsts.TABLE_PARAM_M, "Modified Time");
		RESERVED_PARAMS.put(AmiConsts.TABLE_PARAM_C, "Created Time");
		RESERVED_PARAMS.put(AmiConsts.TABLE_PARAM_V, "Revision");
		RESERVED_PARAMS.put(AmiConsts.TABLE_PARAM_W, "Current Time");
		RESERVED_PARAMS.put(AmiConsts.TABLE_PARAM_T, "Type");
		RESERVED_PARAMS.put(AmiConsts.TABLE_PARAM_P, "Application");
		RESERVED_PARAMS.put(AmiConsts.TABLE_PARAM_E, "Expires Time");
		RESERVED_PARAMS.put(AmiConsts.TABLE_PARAM_I, "Object");

		RESERVED_TYPES.put(AmiConsts.TYPE_COMMAND, "Commands");
		RESERVED_TYPES.put(AmiConsts.TYPE_CONNECTION, "Connections");
	}
	public static final byte CUSTOM_COL_TYPE_PROGRESS = 1;
	public static final byte CUSTOM_COL_TYPE_NUMERIC = 2;
	public static final byte CUSTOM_COL_TYPE_TEXT = 3;
	public static final byte CUSTOM_COL_TYPE_PRICE = 4;
	public static final byte CUSTOM_COL_TYPE_TIME_SEC = 5;
	public static final byte CUSTOM_COL_TYPE_DATE = 6;
	public static final byte CUSTOM_COL_TYPE_DATE_TIME_SEC = 7;
	public static final byte CUSTOM_COL_TYPE_DATE_TIME_MILLIS = 8;
	public static final byte CUSTOM_COL_TYPE_HTML = 9;
	public static final byte CUSTOM_COL_TYPE_JSON = 10;
	public static final byte CUSTOM_COL_TYPE_PERCENT = 11;
	public static final byte CUSTOM_COL_TYPE_SPARK_LINE = 12;
	public static final byte CUSTOM_COL_TYPE_TIME_MILLIS = 13;
	public static final byte CUSTOM_COL_TYPE_IMAGE = 14;
	public static final byte CUSTOM_COL_TYPE_TIME = 15;
	public static final byte CUSTOM_COL_TYPE_DATE_TIME = 16;
	public static final byte CUSTOM_COL_TYPE_TIME_MICROS = 17;
	public static final byte CUSTOM_COL_TYPE_TIME_NANOS = 18;
	public static final byte CUSTOM_COL_TYPE_DATE_TIME_MICROS = 19;
	public static final byte CUSTOM_COL_TYPE_DATE_TIME_NANOS = 20;
	public static final byte CUSTOM_COL_TYPE_CHECKBOX = 25;
	public static final byte CUSTOM_COL_TYPE_MASKED = 26;

	public static OneToOne<Byte, String> CUSTOM_COL_NAMES = new OneToOne<Byte, String>();
	static {
		CUSTOM_COL_NAMES.put(CUSTOM_COL_TYPE_PROGRESS, "progress");
		CUSTOM_COL_NAMES.put(CUSTOM_COL_TYPE_NUMERIC, "numeric");
		CUSTOM_COL_NAMES.put(CUSTOM_COL_TYPE_TEXT, "text");
		CUSTOM_COL_NAMES.put(CUSTOM_COL_TYPE_PRICE, "price");
		CUSTOM_COL_NAMES.put(CUSTOM_COL_TYPE_TIME_SEC, "time_sec");
		CUSTOM_COL_NAMES.put(CUSTOM_COL_TYPE_DATE, "date");
		CUSTOM_COL_NAMES.put(CUSTOM_COL_TYPE_DATE_TIME_SEC, "datetime_sec");
		CUSTOM_COL_NAMES.put(CUSTOM_COL_TYPE_DATE_TIME_MILLIS, "datetime_millis");
		CUSTOM_COL_NAMES.put(CUSTOM_COL_TYPE_HTML, "html");
		CUSTOM_COL_NAMES.put(CUSTOM_COL_TYPE_JSON, "json");
		CUSTOM_COL_NAMES.put(CUSTOM_COL_TYPE_PERCENT, "percent");
		CUSTOM_COL_NAMES.put(CUSTOM_COL_TYPE_SPARK_LINE, "spark");
		CUSTOM_COL_NAMES.put(CUSTOM_COL_TYPE_TIME_MILLIS, "time_millis");
		CUSTOM_COL_NAMES.put(CUSTOM_COL_TYPE_IMAGE, "image");
		CUSTOM_COL_NAMES.put(CUSTOM_COL_TYPE_TIME, "time");
		CUSTOM_COL_NAMES.put(CUSTOM_COL_TYPE_DATE_TIME, "datetime");
		CUSTOM_COL_NAMES.put(CUSTOM_COL_TYPE_TIME_MICROS, "time_micros");
		CUSTOM_COL_NAMES.put(CUSTOM_COL_TYPE_TIME_NANOS, "time_nanos");
		CUSTOM_COL_NAMES.put(CUSTOM_COL_TYPE_DATE_TIME_MICROS, "datetime_micros");
		CUSTOM_COL_NAMES.put(CUSTOM_COL_TYPE_DATE_TIME_NANOS, "datetime_nanos");
		CUSTOM_COL_NAMES.put(CUSTOM_COL_TYPE_CHECKBOX, "checkbox");
		CUSTOM_COL_NAMES.put(CUSTOM_COL_TYPE_MASKED, "Masked");
	}
	public static LinkedHashMap<Byte, String> CUSTOM_COL_DESCRIPTIONS = new LinkedHashMap<Byte, String>();
	static {
		CUSTOM_COL_DESCRIPTIONS.put(AmiWebUtils.CUSTOM_COL_TYPE_TEXT, "Text");
		CUSTOM_COL_DESCRIPTIONS.put(AmiWebUtils.CUSTOM_COL_TYPE_NUMERIC, "Numeric");
		CUSTOM_COL_DESCRIPTIONS.put(AmiWebUtils.CUSTOM_COL_TYPE_PROGRESS, "Bar");
		CUSTOM_COL_DESCRIPTIONS.put(AmiWebUtils.CUSTOM_COL_TYPE_TIME, "Time");
		CUSTOM_COL_DESCRIPTIONS.put(AmiWebUtils.CUSTOM_COL_TYPE_TIME_SEC, "Time w/ seconds");
		CUSTOM_COL_DESCRIPTIONS.put(AmiWebUtils.CUSTOM_COL_TYPE_TIME_MILLIS, "Time w/ millis");
		CUSTOM_COL_DESCRIPTIONS.put(AmiWebUtils.CUSTOM_COL_TYPE_TIME_MICROS, "Time w/ micros");
		CUSTOM_COL_DESCRIPTIONS.put(AmiWebUtils.CUSTOM_COL_TYPE_TIME_NANOS, "Time w/ nanos");
		CUSTOM_COL_DESCRIPTIONS.put(AmiWebUtils.CUSTOM_COL_TYPE_DATE, "Date");
		CUSTOM_COL_DESCRIPTIONS.put(AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME, "Date & Time");
		CUSTOM_COL_DESCRIPTIONS.put(AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_SEC, "Date & Time w/ seconds");
		CUSTOM_COL_DESCRIPTIONS.put(AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_MILLIS, "Date & Time w/ millis");
		CUSTOM_COL_DESCRIPTIONS.put(AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_MICROS, "Date & Time w/ micros");
		CUSTOM_COL_DESCRIPTIONS.put(AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_NANOS, "Date & Time w/ nanos");
		CUSTOM_COL_DESCRIPTIONS.put(AmiWebUtils.CUSTOM_COL_TYPE_PRICE, "Price");
		CUSTOM_COL_DESCRIPTIONS.put(AmiWebUtils.CUSTOM_COL_TYPE_PERCENT, "Percent");
		CUSTOM_COL_DESCRIPTIONS.put(AmiWebUtils.CUSTOM_COL_TYPE_HTML, "HTML");
		CUSTOM_COL_DESCRIPTIONS.put(AmiWebUtils.CUSTOM_COL_TYPE_JSON, "JSON");
		CUSTOM_COL_DESCRIPTIONS.put(AmiWebUtils.CUSTOM_COL_TYPE_SPARK_LINE, "Spark Chart-Line");
		CUSTOM_COL_DESCRIPTIONS.put(AmiWebUtils.CUSTOM_COL_TYPE_IMAGE, "Image");
		CUSTOM_COL_DESCRIPTIONS.put(AmiWebUtils.CUSTOM_COL_TYPE_CHECKBOX, "Checkbox");
		CUSTOM_COL_DESCRIPTIONS.put(AmiWebUtils.CUSTOM_COL_TYPE_MASKED, "Masked");
	}

	static public Class<?> toClass(byte type) {
		return AmiUtils.getClassForValueType(type);
	}
	public static Map<String, Window> getAmiWindowsWithAmiPanels(AmiWebDesktopPortlet amidesktop) {
		Collection<Window> windows = amidesktop.getDesktop().getWindows();
		Map<String, Window> mapPanelIdToWindows = new LinkedHashMap<String, DesktopPortlet.Window>();
		for (Window w : windows) {
			Portlet p = w.getPortlet();
			if (p == null)
				continue;
			if (AmiWebAliasPortlet.class.isInstance(p)) {
				AmiWebAliasPortlet pnl = AmiWebAliasPortlet.class.cast(p);
				mapPanelIdToWindows.put(pnl.getAmiLayoutFullAliasDotId(), w);
			}

		}
		return mapPanelIdToWindows;
	}

	public static LinkedHashMap<String, Window> getWindowsWithUniqueNames(AmiWebDesktopPortlet amidesktop) {
		AmiWebInnerDesktopPortlet desktop = amidesktop.getDesktop();
		LinkedHashMap<String, Window> r = new LinkedHashMap<String, DesktopPortlet.Window>();
		Set<String> visited = new HashSet<String>();
		Set<Window> undocked = new IdentityHashSet<Window>();
		for (TabPlaceholderPortlet p : PortletHelper.findPortletsByType(desktop, TabPlaceholderPortlet.class)) {
			undocked.add(p.getTearoutWindow());
		}

		for (String child : desktop.getChildren().keySet()) {
			final Window win = desktop.getWindow(child);
			if (desktop.isPlaceholder(win))
				continue;
			String name = win.getName();
			if (visited.add(name))
				r.put(name, win);
			else
				r.remove(name);
		}
		return r;
	}

	public static String toSuggestedVarname(String text) {
		if (AmiUtils.isValidVariableName(text, false, false, false))
			return text;
		int len = SH.length(text);
		if (len <= 0)
			return "unknown";
		StringBuilder sb = new StringBuilder(len + 1);
		char c = text.charAt(0);
		if (OH.isBetween(c, '0', '9'))
			sb.append('_').append(c);
		else if (OH.isBetween(c, 'a', 'z') || OH.isBetween(c, 'A', 'Z') || c == '_')
			sb.append(c);
		for (int i = 1; i < len; i++) {
			c = text.charAt(i);
			if (OH.isBetween(c, 'a', 'z') || OH.isBetween(c, 'A', 'Z') || OH.isBetween(c, '0', '9') || c == '_')
				sb.append(c);
			else
				sb.append('_');
		}

		return sb.toString();
	}
	public static String toValidVarname(String varname) {
		return AmiUtils.escapeVarName(varname);
	}

	static public com.f1.base.CalcTypes getAvailableVariables(AmiWebService service, AmiWebAbstractPortlet portlet) {
		if (portlet instanceof AmiWebDmPortlet) {
			AmiWebDmPortlet dmPortlet = (AmiWebDmPortlet) portlet;
			Set<String> names = dmPortlet.getUsedDmAliasDotNames();
			if (names.size() == 0)
				return portlet.getUserDefinedVariables();
			String dmName = CH.first(names);
			Set<String> tables = dmPortlet.getUsedDmTables(dmName);
			if (tables.size() != 1)
				return portlet.getUserDefinedVariables();
			String dmTableName = CH.first(tables);
			return getAvailableVariables(service, dmPortlet, dmName, dmTableName);
		}
		if (portlet instanceof AmiWebRealtimePortlet) {
			AmiWebRealtimePortlet rtp = (AmiWebRealtimePortlet) portlet;
			BasicCalcTypes m = new BasicCalcTypes();
			for (String s : rtp.getLowerRealtimeIds()) {
				if (SH.startsWith(s, "!PNL!")) {
					AmiWebRealtimePortlet panel = (AmiWebRealtimePortlet) service.getPortletByAliasDotPanelId(SH.stripPrefix(s, "!PNL!", true));
					m.putAll(panel.getRealtimeObjectsOutputSchema());
					//					for (String s2 : panel.getLowerRealtimeIds()) {
					//						BasicCalcTypes t = service.getSystemObjectsManager().getTableSchema(s2);
					//						if (t != null)
					//							m.putAll(t);
					//					}
				} else {
					BasicCalcTypes t = service.getSystemObjectsManager().getTableSchema(SH.afterFirst(s, ':'));
					if (t != null)
						m.putAll(t);
				}
			}
			AmiWebRealtimePortlet realtime = (AmiWebRealtimePortlet) portlet;
			return new com.f1.utils.structs.table.stack.CalcTypesTuple4(portlet.getUserDefinedVariables(), m, realtime.getRealtimeObjectschema(), portlet.getSpecialVariables());
		}
		throw new RuntimeException("Cant handle: " + portlet);
	}
	static public com.f1.base.CalcTypes getAvailableVariables(AmiWebService service, AmiWebDmPortlet portlet, String dmName, String dmTableName) {
		AmiWebDm dmByAliasDotName = service.getDmManager().getDmByAliasDotName(dmName);
		if (dmByAliasDotName == null)
			return portlet.getUserDefinedVariables();
		AmiWebDmTableSchema table = dmByAliasDotName.getResponseOutSchema().getTable(dmTableName); //Json Config Table Schema
		if (table == null)
			return portlet.getUserDefinedVariables();
		Tableset dmTableSet = dmByAliasDotName.getResponseTablesetBeforeFilter();
		Table table2 = dmTableSet.getTableNoThrow(dmTableName);
		if (table2 == null)
			return new CalcTypesTuple2(portlet.getUserDefinedVariables(), table.getClassTypes());
		NameSpaceCalcTypes columnTypesMapping = table2.getColumnTypesMapping(); //DM Schema
		return new CalcTypesTuple3(portlet.getUserDefinedVariables(), columnTypesMapping, table.getClassTypes());
	}

	public static Class saveCodeToType(AmiWebService service, String type) {
		if (type == null)
			throw new RuntimeException("bad type, is null");
		if (type.length() == 1)
			switch (type.charAt(0)) {
				case AmiWebUtils.SAVE_CODE_BOOLEAN:
					return Boolean.class;
				case AmiWebUtils.SAVE_CODE_FLOAT:
					return Float.class;
				case AmiWebUtils.SAVE_CODE_DOUBLE:
					return Double.class;
				case AmiWebUtils.SAVE_CODE_INT:
					return Integer.class;
				case AmiWebUtils.SAVE_CODE_LONG:
					return Long.class;
				case AmiWebUtils.SAVE_CODE_UTC:
					return DateMillis.class;
				case AmiWebUtils.SAVE_CODE_UTCN:
					return DateNanos.class;
				case AmiWebUtils.SAVE_CODE_CHAR:
					return Character.class;
				case AmiWebUtils.SAVE_CODE_BINARY:
					return Bytes.class;
				case AmiWebUtils.SAVE_CODE_STRING:
					return String.class;
				case AmiWebUtils.SAVE_CODE_SHORT:
					return Short.class;
				case AmiWebUtils.SAVE_CODE_BYTE:
					return Byte.class;
				case AmiWebUtils.SAVE_CODE_BIGINT:
					return BigInteger.class;
				case AmiWebUtils.SAVE_CODE_BIGDEC:
					return BigDecimal.class;
				case AmiWebUtils.SAVE_CODE_COMPLEX:
					return Complex.class;
				case AmiWebUtils.SAVE_CODE_UUID:
					return UUID.class;
			}
		return service.getScriptManager("").forName(type);
	}
	public static String formatType(byte vtype) {
		if (vtype == AmiDataEntity.PARAM_TYPE_NULL)
			return "<null";
		String r = AmiUtils.toTypeName(vtype);
		return r == null ? ("vtype:" + vtype) : r;
	}

	public static AmiWebService getService(PortletManager portletManager) {
		return (AmiWebService) portletManager.getService(AmiWebService.ID);
	}

	public static Map<String, AmiDatasourceColumn> toMapByNames(List<AmiDatasourceColumn> columns) {
		Map<String, AmiDatasourceColumn> r = new HashMap<String, AmiDatasourceColumn>();
		for (AmiDatasourceColumn i : columns)
			CH.putOrThrow(r, i.getName(), i);
		return r;
	}

	static public void showRunCommandDialog(AmiWebDomObject optionalTarget, AmiWebService amiWebManager, AmiWebCommandWrapper cmd, AmiWebObject[] wos, Row[] rows,
			AmiWebAbstractTablePortlet datamodel) {
		cmd.executeScript(optionalTarget);
		PortletManager manager = amiWebManager.getPortletManager();
		if (cmd.getAmiScript() != null && cmd.getArguments() == null)
			return;
		AmiWebRunCommandDialogPortlet fp = new AmiWebRunCommandDialogPortlet(manager.generateConfig(), wos, rows, datamodel);
		if (!fp.initFromCommand(cmd))
			return;
		RootPortlet rp = (RootPortlet) manager.getRoot();
		RootPortletDialog dialog = rp.addDialog(SH.is(cmd.getTitle()) ? cmd.getTitle() : cmd.getCmdId(), fp);
		dialog.setStyle(AmiWebUtils.getService(manager).getUserDialogStyleManager());
		dialog.setHasCloseButton(fp.shouldHaveCloseButton());
		dialog.setEscapeKeyCloses(fp.shouldHaveCloseButton());
		amiWebManager.getPortletManager().onPortletAdded(fp);
		if (fp.shouldAutoSend())
			fp.sendRequest();
	}
	public static Object castNoThrow(Object value, Caster<?> caster) {
		return caster.cast(value, false, false);
	}
	public static Collection<AmiWebDmTableSchema> getUsedTableSchemas(AmiWebDmPortlet sourcePortlet) {
		AmiWebDmManager dm = sourcePortlet.getService().getDmManager();
		Collection<AmiWebDmTableSchema> r = new ArrayList<AmiWebDmTableSchema>();
		for (String dmname : sourcePortlet.getUsedDmAliasDotNames())
			for (String tbname : sourcePortlet.getUsedDmTables(dmname)) {
				AmiWebDm dm2 = dm.getDmByAliasDotName(dmname);
				if (dm2 != null) {
					AmiWebDmTableSchema table = dm2.getResponseOutSchema().getTable(tbname);
					if (table != null)
						r.add(table);
				}
			}

		return r;
	}
	public static boolean isReservedVar(String i) {
		return i.length() < 3 && SH.areBetween(i, 'A', 'Z');
	}
	public static byte getCustomColumnType(Class type) {
		if (type == Boolean.class)
			return AmiWebUtils.CUSTOM_COL_TYPE_TEXT;
		else if (type == DateNanos.class || type == DateMillis.class)
			return AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_SEC;
		else if (AmiUtils.isNumericType(type))
			return AmiWebUtils.CUSTOM_COL_TYPE_NUMERIC;
		else
			return AmiWebUtils.CUSTOM_COL_TYPE_TEXT;
	}

	static public String parseJoinLanquage(String text, AmiWebService service) {
		if (SH.isnt(text))
			return text;
		StringBuilder sb = new StringBuilder(text.length() + 10);
		sb.append('\"');
		int currlyCount = 0;
		for (int i = 0, l = text.length(); i < l; i++) {
			char c = text.charAt(i);
			if (currlyCount == 0) {
				switch (c) {
					case '\\':
						sb.append(c);
						i++;
						if (i < l)
							sb.append(text.charAt(i));
						break;
					case '$':
						sb.append("$");
						if (i + 1 < l && text.charAt(i + 1) == '{') {
							sb.append('{');
							currlyCount++;
						}
						break;
					case '"':
						sb.append("\\\"");
						break;
					case '\n':
						sb.append("\\n");
					case '\r':
						sb.append("\\r");
						break;
					case '\t':
						sb.append("\\t");
						break;
					default:
						sb.append(c);
				}
			}
		}
		sb.append('\"');
		return sb.toString();
	}

	public static String getFunctionDescription(BasicMethodFactory mf2, ParamsDefinition m) {
		StringBuilder sb = new StringBuilder();
		sb.append(m.getMethodName());
		sb.append('(');
		sb.append("<i>");
		for (int i = 0; i < m.getParamsCount(); i++) {
			if (i > 0)
				sb.append(", ");
			sb.append(mf2.forType(m.getParamType(i)));
			if (m.isVarArg() && i == m.getParamsCount() - 1)
				sb.append(" ... ");
			sb.append(' ').append(m.getParamName(i));
		}
		sb.append("</i>");
		sb.append(") ");
		return sb.toString();
	}

	public static String getMemberMethodDescription(BasicMethodFactory mf, DerivedCellMemberMethod<Object> m) {
		StringBuilder sb = new StringBuilder();
		sb.append(m.getMethodName());
		sb.append('(');
		sb.append("<i>");
		Class[] types = m.getParamTypes();
		String[] names = null;
		if (m instanceof AmiAbstractMemberMethod) {
			names = ((AmiAbstractMemberMethod) m).getParamNames();
		}
		for (int i = 0; i < types.length; i++) {
			if (i > 0)
				sb.append(", ");
			sb.append(mf.forType(types[i]));
			if (names != null)
				sb.append(' ').append(names[i]);
		}
		if (m.getVarArgType() != null) {
			if (types.length > 0) {
				sb.append(", ");
			}
			for (int i = types.length; names != null && i < names.length - 1; i++) {
				sb.append(mf.forType(m.getVarArgType()));
				if (names != null)
					sb.append(' ').append(names[i]).append(", ");
			}
			sb.append(mf.forType(m.getVarArgType()));
			sb.append("... ");
			if (names != null)
				sb.append(' ').append(names[names.length - 1]);
		}
		sb.append("</i>");
		sb.append(") ");
		sb.append("<span style='color:grey'> ");
		sb.append(mf.forType(m.getReturnType())).append("</span>");

		return sb.toString();
	}

	public static void getColors(String text, Set<String> sink) {
		PortletHelper.getColors(text, sink);
	}
	public static String toCssExpression(String styles) {
		if (SH.isnt(styles))
			return null;
		StringBuilder sb = new StringBuilder();
		styles = SH.trim('"', styles);
		String color = null;
		String size = null;
		for (String part : SH.trimStrings(SH.split(',', styles))) {
			if (SH.isnt(part))
				continue;
			char c = part.charAt(0);
			if (c == '#')
				color = part;
			else if (OH.isBetween(c, '0', '9'))
				size = part;
			else
				sb.append(sb.length() == 0 ? "|_fm=" : ",").append(part);
		}
		if (color != null)
			sb.append("|_fg=").append(color);
		if (size != null)
			sb.append("|_fs=").append(size);
		if (sb.length() == 0)
			return null;
		return sb.substring(1);
	}
	public static FormPortletTextField applyFormulaStyle(FormPortletTextField t) {
		t.setCssStyle("_fm=courier");
		return t.setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true);
	}

	public static <T> void findPortletsByTypeFollowUndocked(Portlet container, Class<T> type, Collection<T> sink) {
		if (type.isInstance(container))
			sink.add((T) container);
		if (container instanceof TabPlaceholderPortlet)
			findPortletsByTypeFollowUndocked(((TabPlaceholderPortlet) container).getTearoutWindow().getPortlet(), type, sink);
		if (container instanceof PortletContainer)
			for (Portlet child : ((PortletContainer) container).getChildren().values())
				findPortletsByTypeFollowUndocked(child, type, sink);
	}

	public static <T> Collection<T> findPortletsByTypeFollowUndocked(Portlet container, Class<T> type) {
		Collection<T> sink = new ArrayList<T>();
		findPortletsByTypeFollowUndocked(container, type, sink);
		return sink;
	}
	static public String toPrettyName(String text) {

		if (text == null)
			return null;
		StringBuilder out = new StringBuilder();
		return toPrettyName(text, out).toString();
	}
	static public String toPrettyVarName(String text, String onBlank) {
		if (text == null)
			return onBlank;
		return toPrettyVarName(text, onBlank, new StringBuilder(text.length())).toString();

	}
	static public StringBuilder toPrettyVarName(String text, String onBlank, StringBuilder out) {
		int origLength = out.length();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == '_' || OH.isBetween(c, 'a', 'z') || OH.isBetween(c, 'A', 'Z') || (OH.isBetween(c, '0', '9') && out.length() != origLength)) {
				if (out.length() == origLength)
					out.append(Character.toLowerCase(c));
				else
					out.append(c);
			}
		}
		if (out.length() == origLength)
			out.append(onBlank);
		return out;
	}

	static public StringBuilder toPrettyName(String text, StringBuilder out) {
		if (text == null)
			return out;
		boolean upperCase = SH.isUpperCase(text);
		if (upperCase && text.length() < 4)
			return out.append(text);
		boolean isMixed = !upperCase && !SH.isLowerCase(text);
		for (int i = 0; i < text.length();) {
			int j = i;
			while (j < text.length() && Character.isUpperCase(text.charAt(j)))
				j++;
			int wordBreak = findBreak(text, j);
			if (out.length() > 0)
				out.append(' ');

			if (isMixed) {
				out.append(text, i, wordBreak);
			} else {
				out.append(Character.toUpperCase(text.charAt(i++)));
				while (i < wordBreak) {
					out.append(Character.toLowerCase(text.charAt(i++)));
				}
			}
			i = wordBreak;
			while (i < text.length() && (text.charAt(i) == ' ' || text.charAt(i) == '_'))
				i++;
		}
		return out;
	}

	private static int findBreak(String text, int start) {
		int lastType = 0;
		for (int i = start + 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == ' ' || c == '_')
				return i;
			int type;
			if (c == '-' || c == '&' || c == '+')
				continue;
			if (Character.isDigit(c))
				type = 1;
			else if (Character.isUpperCase(c))
				type = 2;
			else if (Character.isLowerCase(c))
				type = 3;
			else
				type = 4;
			if (lastType == 0)
				lastType = type;
			else if (lastType != type)
				return i;
		}
		return text.length();
	}

	public static RootPortletDialog showStyleDialog(String title, Portlet origPortlet, Portlet newPortlet, PortletConfig config) {
		return AmiWebUtils.showStyleDialog(title, origPortlet, newPortlet, config, -1, -1, null);
	}
	private static RootPortletDialog showStyleDialog(String title, Portlet origPortlet, Portlet newPortlet, PortletConfig config, int arrowX, int arrowY,
			QueryField<?> targetField) {

		DesktopPortlet desktop = AmiWebUtils.getService(origPortlet.getManager()).getDesktop().getDesktop();

		RootPortletDialog dialog;
		if (newPortlet.getWidth() != 0 && newPortlet.getHeight() != 0)
			dialog = config.getPortletManager().showDialog(title, newPortlet, newPortlet.getWidth(), newPortlet.getHeight());
		else
			dialog = config.getPortletManager().showDialog(title, newPortlet);
		RootPortlet origPortletRoot = PortletHelper.findParentByType(origPortlet, RootPortlet.class);
		RootPortlet desktopRoot = PortletHelper.findParentByType(desktop, RootPortlet.class);
		boolean isPoppedOut = origPortletRoot != desktopRoot;
		boolean isDivider = origPortlet instanceof AmiWebDividerPortlet;
		boolean isTabs = origPortlet instanceof AmiWebTabPortlet;
		boolean tabsRight = isTabs && ((AmiWebTabPortlet) origPortlet).getInnerContainer().getTabPortletStyle().getIsOnRight();

		int dialogBorderSize = dialog.getBorderSize();
		int dialogHeaderSize = dialog.getHeaderSize();

		AbstractPortlet root;
		if (!isPoppedOut)
			root = desktop;
		else
			root = origPortletRoot;

		// Check to make sure dialog fits on screen
		boolean fitsRootWindowVertically = root.getHeight() >= dialog.getOuterHeight();
		boolean fitsRootWindowHorizontally = root.getWidth() >= dialog.getOuterWidth();
		if (!fitsRootWindowVertically)
			dialog.getPortlet().setSize(dialog.getPortlet().getWidth(), dialog.getPortlet().getHeight() - MH.abs(root.getHeight() - dialog.getOuterHeight()));
		if (!fitsRootWindowHorizontally)
			dialog.getPortlet().setSize(dialog.getPortlet().getWidth() - MH.abs(root.getWidth() - dialog.getOuterWidth()), dialog.getPortlet().getHeight());

		int rootX = PortletHelper.getAbsoluteLeft(root);
		int rootY = PortletHelper.getAbsoluteTop(root);
		int rootWidth = root.getWidth();
		int rootHeight = root.getHeight();

		int origX = PortletHelper.getAbsoluteLeft(origPortlet) - rootX; // these two work for both popped-in and popped-out
		int origY = PortletHelper.getAbsoluteTop(origPortlet) - rootY;
		int origWidth = origPortlet.getWidth();
		int origHeight = origPortlet.getHeight();
		int origButtonX = getAmiButtonX(origPortlet); // gives position w.r.t. root portlet (i.e. is correct even for popped-out window)
		int origButtonY = getAmiButtonY(origPortlet);
		boolean leftTopIsWider = false;
		if (isDivider) {
			boolean isVertical = ((AmiWebDividerPortlet) origPortlet).isVertical();
			int origDividerOffset = isVertical ? getAmiButtonX(origPortlet) - PortletHelper.getAbsoluteLeft(origPortlet)
					: getAmiButtonY(origPortlet) - PortletHelper.getAbsoluteTop(origPortlet);
			leftTopIsWider = origDividerOffset > (isVertical ? origWidth : origHeight) / 2;
		}

		int newX; // These are the coordinates for the PORTLET, not the outer corner of the dialog. They are adjusted at the end to set the coordinates for the outer corner.
		int newY;
		int newInnerWidth = dialog.getPortlet().getWidth();
		int newInnerHeight = dialog.getPortlet().getHeight();
		int newOuterWidth = dialog.getOuterWidth(); // includes border width
		int newOuterHeight = dialog.getOuterHeight(); // includes header and border heights

		int gapOrigRightEdge = rootWidth - (origX + origWidth); // Gap between origPortlet and right edge of screen
		int gapOrigBottomEdge = rootHeight - (origY + origHeight); // Gap between origPortlet and bottom edge of screen

		boolean newFitsOnRight;
		boolean newFitsOnLeft;
		boolean newFitsOnTop;
		boolean newFitsOnBottom;

		newFitsOnRight = gapOrigRightEdge > newOuterWidth;
		newFitsOnLeft = origX > newOuterWidth;
		newFitsOnTop = origY > newOuterHeight;
		newFitsOnBottom = gapOrigBottomEdge > newOuterHeight;

		boolean newFitsNowhere = !(newFitsOnRight || newFitsOnLeft || newFitsOnTop || newFitsOnBottom);
		boolean newIsLeftRight = newFitsOnLeft || newFitsOnRight;
		int eps = 30; // spacing between orig and new windows

		int newDivX = leftTopIsWider ? origButtonX - (eps + dialogBorderSize + newInnerWidth) : origButtonX + eps + dialogBorderSize;
		int newDivY = leftTopIsWider ? origButtonY - (newInnerHeight + dialogBorderSize + eps) : origButtonY + dialogHeaderSize + dialogBorderSize + eps;

		if (isDivider) {
			if (((AmiWebDividerPortlet) origPortlet).isVertical()) {
				newX = newDivX;
				newY = origButtonY - newOuterHeight / 2 + dialogHeaderSize + dialogBorderSize;
			} else {
				newX = origButtonX - newOuterWidth / 2 + dialogBorderSize;
				newY = newDivY;
			}
		} else {
			if (newFitsOnRight) { // New portlet fits between old portlet and right edge of screen
				newX = origButtonX + (isTabs && tabsRight ? 0 : origWidth / 2) + eps + dialogBorderSize;
				newY = origButtonY - newOuterHeight / 2 + dialogHeaderSize + dialogBorderSize;
			} else if (newFitsOnLeft) { // New portlet fits between old portlet and left edge of screen
				newX = origButtonX - ((isTabs && !tabsRight ? 0 : origWidth / 2) + eps + dialogBorderSize + newInnerWidth);
				newY = origButtonY - newOuterHeight / 2 + dialogHeaderSize + dialogBorderSize;
			} else if (newFitsOnTop) { // New portlet fits between old portlet and top edge of screen
				newX = origButtonX - newOuterWidth / 2 + dialogBorderSize;
				newY = origButtonY - origHeight / 2 - dialogBorderSize - newInnerHeight - eps;
			} else if (newFitsOnBottom) { // New portlet fits between old portlet and bottom edge of screen
				newX = origButtonX - newOuterWidth / 2 + dialogBorderSize;
				newY = origButtonY + origHeight / 2 + dialogHeaderSize + dialogBorderSize + eps;
			} else { // New portlet doesn't fit in anywhere. :( Put at edge of screen. 
				if (gapOrigRightEdge >= origX) // If there is more space on the right, place on right edge of screen
					newX = rootWidth - newOuterWidth - 5;
				else
					// Otherwise, place on left edge of screen. 
					newX = dialogBorderSize;
				newY = (rootHeight - newOuterHeight) / 2 + dialogHeaderSize + dialogBorderSize;
			}
		}
		if (isPoppedOut && !newFitsNowhere) {
			newX -= rootX;
			newY -= rootY;
		}

		boolean isOffscreenTop;
		boolean isOffscreenBottom;
		boolean isOffscreenLeft;
		boolean isOffscreenRight;

		if (isDivider) {
			isOffscreenTop = leftTopIsWider && (newY - dialogBorderSize - dialogHeaderSize < 0);
			isOffscreenBottom = !leftTopIsWider && (newY + newInnerHeight + dialogBorderSize) > rootHeight;
			isOffscreenLeft = leftTopIsWider && (newX - dialogBorderSize < 0);
			isOffscreenRight = !leftTopIsWider && (newX + newInnerWidth + dialogBorderSize) > rootWidth;
		} else {
			if (!newIsLeftRight) {
				isOffscreenTop = isPoppedOut ? newY < 0 : newY - dialogBorderSize - dialogHeaderSize < 0;
				isOffscreenBottom = isPoppedOut ? (newY + newInnerHeight + dialogBorderSize) > rootHeight : (newY + newOuterHeight + dialogBorderSize) > rootHeight;
				isOffscreenLeft = isPoppedOut ? newX < 0 : newX - dialogBorderSize < 0;
				isOffscreenRight = isPoppedOut ? (newX + newOuterWidth) > (rootX + rootWidth) : (newX + newOuterWidth + dialogBorderSize) > rootWidth;
			} else {
				isOffscreenTop = isPoppedOut ? (newY - dialogBorderSize - dialogHeaderSize) < 0 : newY - dialogBorderSize - dialogHeaderSize < 0;
				isOffscreenBottom = isPoppedOut ? (newY + newInnerHeight + dialogBorderSize) > rootHeight : (newY + newOuterHeight + dialogBorderSize) > rootHeight;
				isOffscreenLeft = isPoppedOut ? newX < 0 : newX - dialogBorderSize < 0;
				isOffscreenRight = isPoppedOut ? (newX + newOuterWidth) > (rootX + rootWidth) : (newX + newOuterWidth + dialogBorderSize) > rootWidth;
			}
		}

		boolean isOffscreen = isOffscreenTop || isOffscreenBottom || isOffscreenLeft || isOffscreenRight;

		if (isOffscreenTop)
			newY = isPoppedOut ? dialogHeaderSize + dialogBorderSize : newY + MH.abs(newY - rootY);
		if (isOffscreenBottom)
			newY = isPoppedOut ? MH.abs(newOuterHeight - rootHeight) : newY - MH.abs((newY + newOuterHeight) - (rootY + rootHeight));
		if (isOffscreenLeft)
			newX = isPoppedOut ? dialogBorderSize : newX + MH.abs(newX - rootX);
		if (isOffscreenRight)
			newX = isPoppedOut ? MH.abs(newOuterWidth - rootWidth) : newX - MH.abs((newX + newOuterWidth) - (rootX + rootWidth));
		if (newFitsNowhere && newY == (dialogHeaderSize + dialogBorderSize))
			newY = (rootHeight - newOuterHeight) / 2 + dialogHeaderSize + dialogBorderSize;

		if (!isDivider && !isPoppedOut && isOffscreen) {
			if (isOffscreenLeft || isOffscreenRight)
				newX = newX + dialogBorderSize;
			if (isOffscreenTop || isOffscreenBottom)
				newY = newY + dialogBorderSize + dialogHeaderSize;
		}

		// Check to make sure that dialog is not covering divider button after it has been moved back onscreen
		if (isDivider) {
			if (((AmiWebDividerPortlet) origPortlet).isVertical()) {
				boolean coveringFromRight = isOffscreenRight && newX < (origButtonX + eps);
				boolean coveringFromLeft = isOffscreenLeft && (newX + newOuterWidth) > (origButtonX - eps);
				if (coveringFromRight) // covering from right
					newX = origButtonX - (newInnerWidth + dialogBorderSize + eps);
				else if (coveringFromLeft) { // covering from left
					newX = origButtonX + dialogBorderSize + eps;
				}
				if ((isPoppedOut && !newFitsNowhere) && (coveringFromRight || coveringFromLeft))
					newX -= rootX;
			} else {
				boolean coveringFromBottom = isOffscreenBottom && newY < (origButtonY + eps);
				boolean coveringFromTop = isOffscreenTop && (newY + newOuterHeight) > (origButtonY - eps);
				if (coveringFromBottom) // covering from bottom
					newY = origButtonY - (newInnerHeight + dialogBorderSize + eps);
				else if (coveringFromTop) // covering from top
					newY = origButtonY + dialogHeaderSize + dialogBorderSize + eps;
				if ((isPoppedOut && !newFitsNowhere) && (coveringFromBottom || coveringFromTop))
					newY -= rootY;
			}
		}

		dialog.setPosition(newX, newY);

		dialog.setCloseOnClickOutside(true);
		dialog.setShadeOutside(false);

		if (targetField != null)
			getService(config.getPortletManager()).getDesktop().setDialogArrow(origPortlet, dialog, targetField);
		else if (arrowX != -1 && arrowY != -1)
			getService(config.getPortletManager()).getDesktop().setDialogArrow(origPortlet, dialog, arrowX, arrowY);
		else
			getService(config.getPortletManager()).getDesktop().setDialogArrow(origPortlet, dialog);

		return dialog;
	}
	static public int getAmiButtonX(Portlet portlet) {
		int x = PortletHelper.getAbsoluteLeft(portlet);
		if (portlet instanceof AmiWebDividerPortlet) {
			DividerPortlet div = ((AmiWebDividerPortlet) portlet).getInnerContainer();
			if (div.isVertical())
				return x + div.getFirstChild().getWidth() + div.getThickness() / 2;
		} else if (portlet instanceof AmiWebTabPortlet) {
			TabPortlet tab = ((AmiWebTabPortlet) portlet).getInnerContainer();
			if (tab.getTabPortletStyle().getIsOnRight())
				return x + tab.getWidth() - tab.getTabPortletStyle().getTabHeight() / 2;
			else
				return x + tab.getTabPortletStyle().getTabHeight() / 2;
		} else {
			for (PortletContainer parent = portlet.getParent(); parent instanceof GridPortlet; parent = parent.getParent())
				if (parent instanceof AmiWebAbstractPortlet) {
					x = PortletHelper.getAbsoluteLeft(parent);
					return x + parent.getWidth() / 2;
				}
		}
		return x + portlet.getWidth() / 2;
	}
	static public int getAmiButtonY(Portlet portlet) {
		int y = PortletHelper.getAbsoluteTop(portlet);
		if (portlet instanceof AmiWebDividerPortlet) {
			DividerPortlet div = ((AmiWebDividerPortlet) portlet).getInnerContainer();
			if (!div.isVertical())
				return y + div.getFirstChild().getHeight() + div.getThickness() / 2;
		} else if (portlet instanceof AmiWebTabPortlet) {
			TabPortlet tab = ((AmiWebTabPortlet) portlet).getInnerContainer();
			if (tab.getTabPortletStyle().getIsOnBottom())
				return y + tab.getHeight() - tab.getTabPortletStyle().getTabHeight() / 2;
			else
				return y + tab.getTabPortletStyle().getTabHeight() / 2;
		} else {
			for (PortletContainer parent = portlet.getParent(); parent instanceof GridPortlet; parent = parent.getParent())
				if (parent instanceof AmiWebAbstractPortlet) {
					y = PortletHelper.getAbsoluteTop(parent);
					return y + parent.getHeight() / 2;
				}
		}
		return y + portlet.getHeight() / 2;
	}
	public static String spellCheck(String prefix, String methodName, Set<String> keySet) {
		for (String s : keySet) {
			if (SH.equalsIgnoreCase(methodName, s)) {
				return prefix + "\"" + s + "\"";
			}
		}
		return "";
	}

	public static void createNewDesktopPortlet(AmiWebDesktopPortlet desktop, AmiWebPortlet portlet) {
		if (portlet.getParent() != null)
			portlet.getParent().removeChild(portlet.getPortletId());
		desktop.createNewWindow(portlet);
	}

	public static void applyEndUserTableStyle(FastTablePortlet tablePortlet) {
		tablePortlet.addOption(FastTablePortlet.OPTION_TITLE_BAR_COLOR, "#6f6f6f");
		tablePortlet.addOption(FastTablePortlet.OPTION_TITLE_DIVIDER_HIDDEN, true);
		tablePortlet.addOption(FastTablePortlet.OPTION_GRIP_COLOR, "#4c4c4c");
		tablePortlet.addOption(FastTablePortlet.OPTION_TRACK_BUTTON_COLOR, "#4c4c4c");
		tablePortlet.addOption(FastTablePortlet.OPTION_SCROLL_ICONS_COLOR, "#ffffff");
	}

	public static void setJavascriptBreakpoint(PortletManager manager) {
		manager.getPendingJs().append("debugger;");
	}

	public static void showEditDmPortlet(AmiWebService service, List<AmiWebDatasourceWrapper> l, List<AmiWebDmsImpl> m, List<String> rtList, String title) {
		//TODO: title should be dropped; its the same
		service.getAmiWebDmEditorsManager().showAddDmPortlet(service, l, m, rtList);
	}
	public static AmiWebAddPanelPortlet showEditDmPortlet(AmiWebService service, AmiWebDmsImpl amiWebDm, String title) {
		//TODO: title should be dropped; its the same
		return service.getAmiWebDmEditorsManager().showEditDmPortlet(service, amiWebDm);
	}

	public static Portlet getFirstInDividers(AmiWebDividerPortlet divider) {
		Portlet first = divider.getFirstChild();
		if (first instanceof AmiWebDividerPortlet && divider.isVertical() == ((AmiWebDividerPortlet) first).isVertical()) {
			return getFirstInDividers((AmiWebDividerPortlet) first);
		} else {
			return first;
		}
	}
	public static Portlet getLastInDividers(AmiWebDividerPortlet divider) {
		Portlet second = divider.getSecondChild();
		if (second instanceof AmiWebDividerPortlet && divider.isVertical() == ((AmiWebDividerPortlet) second).isVertical()) {
			return getLastInDividers((AmiWebDividerPortlet) second);
		} else {
			return second;
		}
	}
	public static AmiWebDividerPortlet getRootDividerAlongAxis(AmiWebDividerPortlet source) {
		AmiWebAbstractContainerPortlet parent = getParentAmiContainer(source.getParent());
		if (parent instanceof AmiWebDividerPortlet && ((AmiWebDividerPortlet) parent).isVertical() == source.isVertical()) {
			return getRootDividerAlongAxis((AmiWebDividerPortlet) parent);
		} else {
			return source;
		}
	}
	public static Portlet getAdjacentPortletAlongDividerAxis(Portlet portlet, boolean before) {
		AmiWebAbstractContainerPortlet parent = getParentAmiContainer(portlet.getParent());
		if (parent instanceof AmiWebDividerPortlet) {
			AmiWebDividerPortlet divider = (AmiWebDividerPortlet) parent;
			boolean first = portlet == divider.getFirstChild();
			if (first && !before) {
				return divider.getSecondChild();
			} else if (!first && before) {
				return divider.getFirstChild();
			} else {
				Portlet source = first ? divider.getFirstChild() : divider.getSecondChild();
				Portlet target = null;
				List<Portlet> portlets = new ArrayList<Portlet>();
				getOrderedDividerPortletsAlongDividerAxis(getRootDividerAlongAxis(divider), portlets);
				int size = portlets.size();
				for (int i = 0; i < size; i++) {
					if (portlets.get(i) == source) {
						if ((i == 0 && before) || (i == size - 1 && !before)) { // portlet already at "end"
							break;
						} else {
							target = before ? portlets.get(i - 1) : portlets.get(i + 1);
							break;
						}
					}
				}
				return target;
			}
		} else {
			return null;
		}
	}
	public static void getOrderedDividerPortletsAlongDividerAxis(AmiWebDividerPortlet divider, List<Portlet> sink) {
		Portlet first = divider.getFirstChild();
		Portlet second = divider.getSecondChild();
		if (first instanceof AmiWebDividerPortlet && ((AmiWebDividerPortlet) first).isVertical() == divider.isVertical()) {
			getOrderedDividerPortletsAlongDividerAxis((AmiWebDividerPortlet) first, sink);
		} else {
			sink.add(first);
		}
		if (second instanceof AmiWebDividerPortlet && ((AmiWebDividerPortlet) second).isVertical() == divider.isVertical()) {
			getOrderedDividerPortletsAlongDividerAxis((AmiWebDividerPortlet) second, sink);
		} else {
			sink.add(second);
		}
	}
	public static boolean allPortletsAlongDividerAxisMatchType(Class<? extends Portlet> sourceClass, Portlet target) {
		if (target instanceof AmiWebDividerPortlet) {
			List<Portlet> targetPortlets = new ArrayList<Portlet>();
			getOrderedDividerPortletsAlongDividerAxis((AmiWebDividerPortlet) target, targetPortlets);
			boolean allMatch = true;
			for (Portlet p : targetPortlets) {
				if (p.getClass() != sourceClass) {
					allMatch = false;
					break;
				}
			}
			return allMatch;
		} else {
			return sourceClass == target.getClass();
		}
	}
	public static void distributeDividers(AmiWebDividerPortlet divider) {
		Portlet first = divider.getFirstChild();
		Portlet second = divider.getSecondChild();
		double numPanelsFirst, numPanelsSecond;
		boolean firstIsDividerAlongAxis = first instanceof AmiWebDividerPortlet && ((AmiWebDividerPortlet) first).isVertical() == divider.isVertical();
		boolean secondIsDividerAlongAxis = second instanceof AmiWebDividerPortlet && ((AmiWebDividerPortlet) second).isVertical() == divider.isVertical();
		if (firstIsDividerAlongAxis) {
			numPanelsFirst = countPortletsAlongDividerAxis((AmiWebDividerPortlet) first);
		} else {
			numPanelsFirst = 1;
		}
		if (secondIsDividerAlongAxis) {
			numPanelsSecond = countPortletsAlongDividerAxis((AmiWebDividerPortlet) second);
		} else {
			numPanelsSecond = 1;
		}
		// calculate position of divider...
		divider.getInnerContainer().setOffset(numPanelsFirst / (numPanelsFirst + numPanelsSecond));
		if (firstIsDividerAlongAxis) {
			distributeDividers((AmiWebDividerPortlet) first);
		}
		if (secondIsDividerAlongAxis) {
			distributeDividers((AmiWebDividerPortlet) second);
		}
	}
	public static int countPortletsAlongDividerAxis(AmiWebDividerPortlet divider) {
		int cnt = 0;
		Portlet first = divider.getFirstChild();
		Portlet second = divider.getSecondChild();
		if (first instanceof AmiWebDividerPortlet && ((AmiWebDividerPortlet) first).isVertical() == divider.isVertical()) {
			cnt = cnt + countPortletsAlongDividerAxis((AmiWebDividerPortlet) first);
		} else {
			cnt++;
		}
		if (second instanceof AmiWebDividerPortlet && ((AmiWebDividerPortlet) second).isVertical() == divider.isVertical()) {
			cnt = cnt + countPortletsAlongDividerAxis((AmiWebDividerPortlet) second);
		} else {
			cnt++;
		}
		return cnt;
	}
	public static AmiWebAbstractContainerPortlet getParentAmiContainer(PortletContainer node) {
		while (node != null) {
			node = node.getParent();
			if (node instanceof AmiWebAbstractContainerPortlet)
				return (AmiWebAbstractContainerPortlet) node;
		}
		return null;
	}
	public static String getPanelId(Portlet source) {
		if (source == null)
			return null;
		else if (source instanceof AmiWebPortlet)
			return ((AmiWebPortlet) source).getAmiPanelId();
		else
			return source.getPortletId();
	}
	public static AmiDebugManager getDebugManager(AmiWebService service) {
		return service.getDebugManager();
	}

	public static Set<String> getFonts(AmiWebService service) {
		return service.getFontsManager().getFonts();
	}
	public static Set<String> getFonts(Portlet p) {
		return getFonts(getService(p.getManager()));
	}

	public static final String ROOT_ALIAS_FORMAT = "<root>";

	public static String formatLayoutAlias(String layoutAlias) {
		return "".equals(layoutAlias) ? ROOT_ALIAS_FORMAT : layoutAlias;
	}
	public static String getFullAlias(String parent, String child) {
		if (parent == null || child == null)
			return null;
		if (OH.eq("", child))
			return parent;
		else if (OH.eq("", parent))
			return child;
		else {
			// TODO we need to prevent cases where we are double adding the alias...
			return parent + "." + child;
		}
	}
	public static String getRelativeAlias(String parent, String fullAlias) {
		OH.assertNotNull(parent);
		if (fullAlias == null)
			return null;
		if (OH.eq(parent, fullAlias))
			return "";
		if (OH.eq(parent, ""))
			return fullAlias;
		if (fullAlias.startsWith(parent) && fullAlias.length() > parent.length() + 1 && fullAlias.charAt(parent.length()) == '.')
			return fullAlias.substring(parent.length() + 1);
		throw new RuntimeException(fullAlias + " is not a child of " + parent);
	}
	public static String getRelativeAliasNoThrow(String parent, String adn) {
		OH.assertNotNull(parent);
		if (adn == null)
			return null;
		if (OH.eq(parent, adn))
			return "";
		if (OH.eq(parent, ""))
			return adn;
		if (adn.startsWith(parent) && adn.length() > parent.length() + 1 && adn.charAt(parent.length()) == '.')
			return adn.substring(parent.length() + 1);
		return null;
	}
	public static String getAliasFromAdn(String adn) {
		OH.assertNotNull(adn);
		return SH.beforeLast(adn, '.', "");
	}
	public static String getNameFromAdn(String adn) {
		OH.assertNotNull(adn);
		return SH.afterLast(adn, '.', adn);
	}

	public static boolean isParentAliasOrSame(String parent, String child) {
		return OH.eq(parent, child) || isParentAlias(parent, child);
	}
	public static boolean isParentAlias(String parent, String child) {
		if ("".equals(parent))
			return true;
		return child.startsWith(parent) && child.length() > parent.length() + 1 && child.charAt(parent.length()) == '.';
	}

	public static String getCommanAliasRoot(Iterable<String> cs) {
		String r = null;
		for (String s : cs)
			r = r == null ? s : getCommanAliasRoot(r, s);
		return r;
	}
	//ex:   "a.b.c","a.b.d" -> "a.b"
	//ex:   "a.b.c","d.e.f" -> ""
	public static String getCommanAliasRoot(String l, String r) {
		if (r == null || l == null)
			return null;
		if (OH.eq(r, l))
			return r;
		if (isParentAlias(r, l))
			return r;
		if (isParentAlias(l, r))
			return l;
		int lastPeriod = 0;
		for (int i = 0, max = Math.min(r.length(), l.length()) - 1; i < max; i++)
			if (i == r.length() || i == l.length() || r.charAt(i) != l.charAt(i))
				break;
			else if (r.charAt(i) == '.')
				lastPeriod = i;
		return r.substring(0, lastPeriod);
	}

	public static void putSkipEmpty(Map<String, Object> sink, String key, Map<?, ?> value) {
		if (CH.isntEmpty(value))
			CH.putOrThrow(sink, key, value);
	}
	public static void putSkipEmpty(Map<String, Object> sink, String key, Collection<?> value) {
		if (CH.isntEmpty(value))
			CH.putOrThrow(sink, key, value);
	}

	public static byte[] zipLayoutFiles(Iterable<AmiWebLayoutFile> layoutFiles, AmiWebService service, Byte layoutFormatting) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);
		try {
			for (AmiWebLayoutFile lf : layoutFiles) {
				String name = SH.isEmpty(lf.getFullAlias()) ? "root.ami" : lf.getFullAlias() + ".ami";
				String layoutContent = AmiWebLayoutHelper.toJson(lf.buildCurrentJson(service), layoutFormatting);
				ZipEntry ze = new ZipEntry(name);
				zos.putNextEntry(ze);
				zos.write(layoutContent.getBytes());
				zos.closeEntry();
			}
			zos.flush();
			zos.close();
			byte[] zipContent = baos.toByteArray();
			baos.flush();
			baos.close();
			return zipContent;
		} catch (IOException e) {
			LH.severe(log, "Unable to zip layout file(s)" + e);
		}
		return null;
	}

	public static Collection<AmiWebAliasPortlet> getVisiblePanels(AmiWebService service) {
		Collection<AmiWebAliasPortlet> visiblePanelsSink = new ArrayList<AmiWebAliasPortlet>();
		for (AmiWebAliasPortlet containerPortlet : service.getDesktop().getDesktop().getAmiChildren())
			getVisiblePanelsRecursive(containerPortlet, visiblePanelsSink);
		return visiblePanelsSink;
	}

	private static void getVisiblePanelsRecursive(AmiWebAliasPortlet currentVisiblePortlet, Collection<AmiWebAliasPortlet> sink) {
		sink.add(currentVisiblePortlet);
		for (AmiWebAliasPortlet child : currentVisiblePortlet.getAmiChildren())
			getVisiblePanelsRecursive(child, sink);
	}

	public static Collection<AmiWebPortletDef> getHiddenPanels(AmiWebService service, boolean skipDesktop) {
		Collection<AmiWebPortletDef> hiddenPanelsSink = new ArrayList<AmiWebPortletDef>();
		for (AmiWebLayoutFile child : service.getLayoutFilesManager().getLayout().getChildrenRecursive(true)) {
			for (String panelId : child.getHiddenPanelIds()) {
				AmiWebPortletDef hiddenPanel = child.getHiddenPanelByPanelId(panelId);
				if (skipDesktop && AmiWebInnerDesktopPortlet.Builder.ID.equals(hiddenPanel.getBuilderId())) // skips desktop
					continue;
				hiddenPanelsSink.add(hiddenPanel);
			}
		}
		return hiddenPanelsSink;
	}
	public static void diffConfigurations(AmiWebService service, String leftJsonText, String rightJsonText, String leftTitle, String rightTitle, String searchText) {
		AmiWebDifferPortlet differ = new AmiWebDifferPortlet(service.getPortletManager().generateConfig());
		differ.setText(leftJsonText, rightJsonText);
		differ.setTitles(leftTitle, rightTitle);
		if (searchText != null)
			differ.setSearch(searchText);
		service.getPortletManager().showDialog("Configuration Diff", differ);
	}
	public static void showConfiguration(AmiWebService service, String jsonText, String title, String searchText) {
		AmiWebDifferPortlet differ = new AmiWebDifferPortlet(service.getPortletManager().generateConfig());
		differ.setText(jsonText, jsonText);
		differ.setTitles(title, title);
		if (searchText != null)
			differ.setSearch(searchText);
		differ.showOnlyLeft();
		service.getPortletManager().showDialog("Configuration", differ);
	}
	public static void putSkipEmpty(Map<String, Object> sink, String key, String value) {
		if (SH.is(value))
			CH.putOrThrow(sink, key, value);
	}

	private static class JsonComparator implements Comparator<Map> {

		final private String key;

		public JsonComparator(String key) {
			this.key = key;
		}

		@Override
		public int compare(Map o1, Map o2) {
			if (o1 == null)
				return o2 == null ? 0 : 1;
			else if (o2 == null)
				return -1;
			final Comparable v1 = (Comparable) o1.get(key);
			final Comparable v2 = (Comparable) o2.get(key);
			return OH.compare(v1, v2);
		}

	}

	static public List<Map<String, Object>> sortJsonList(List<Map<String, Object>> values, String key) {
		if (values == null || values.size() < 2)
			return values;
		Comparator<Map> comparator = new JsonComparator(key);
		Collections.sort(values, comparator);
		return values;
	}

	public static void showStyleDialog(String title, AmiWebAbstractPortlet target) {
		AmiWebEditStylePortlet t = new AmiWebEditStylePortlet(target.getStylePeer(), target.generateConfig());
		showStyleDialog(title, target, t, target.generateConfig());

	}

	public static Table toTable(AmiWebManagers managers, String type) {
		return toTable(managers.getAmiObjectsByType(type));
	}

	private static final Comparator<AmiWebObject> COMAPRATOR = new Comparator<AmiWebObject>() {

		@Override
		public int compare(AmiWebObject o1, AmiWebObject o2) {
			long l = o1.getUniqueId();
			long r = o2.getUniqueId();
			if (l < 0 && r < 0)
				return OH.compare(r, l);
			else
				return OH.compare(l, r);
		}
	};

	public static Table toTable(AmiWebRealtimeObjectManager amiObjectsByType) {
		com.f1.base.CalcTypes cols = amiObjectsByType.getRealtimeObjectsOutputSchema();
		if (cols.isVarsEmpty()) {
			ColumnarTable r = new ColumnarTable();
			r.setTitle(amiObjectsByType.getRealtimeId());
		}

		int size = cols.getVarsCount();
		Class<?>[] types = new Class[size];
		String[] names = new String[size];
		Caster<?>[] casters = new Caster[size];
		int n = 0;
		for (String i : cols.getVarKeys()) {
			Class<?> type = cols.getType(i);
			names[n] = i;
			casters[n] = OH.getCaster(type);
			types[n] = type;
			n++;
		}

		ColumnarTable r = new ColumnarTable(types, names);
		r.setTitle(amiObjectsByType.getRealtimeId());
		if (amiObjectsByType != null) {
			IterableAndSize<AmiWebObject> amiObjects = amiObjectsByType.getAmiObjects();
			AmiWebObject[] a = AH.toArray(amiObjects, AmiWebObject.class);
			Arrays.sort(a, COMAPRATOR);
			for (AmiWebObject obj : a) {
				ColumnarRow row = r.newEmptyRow();
				for (int i = 0; i < names.length; i++) {
					Object o = obj.getParam(names[i]);
					if (o != null)
						row.putAt(i, casters[i].castNoThrow(o));
				}
				r.getRows().add(row);
			}
		}
		return r;
	}

	public static Table toTable(com.f1.base.CalcTypes cols, Iterable<AmiWebObject> selected) {
		if (cols.isVarsEmpty())
			return new ColumnarTable();

		int size = cols.getVarsCount();
		Class<?>[] types = new Class[size];
		String[] names = new String[size];
		Caster<?>[] casters = new Caster[size];
		int n = 0;
		for (String i : cols.getVarKeys()) {
			Class<?> type = cols.getType(i);
			names[n] = i;
			casters[n] = OH.getCaster(type);
			types[n] = type;
			n++;
		}
		ColumnarTable r = new ColumnarTable(types, names);
		for (AmiWebObject obj : selected) {
			ColumnarRow row = r.newEmptyRow();
			for (int i = 0; i < names.length; i++) {
				Object o = obj.getParam(names[i]);
				if (o != null)
					row.putAt(i, casters[i].castNoThrow(o));
			}
			r.getRows().add(row);
		}
		return r;
	}

	public static String getAmiScript(Map<String, Object> map, String key, String dflt) {
		Object o = map.get(key);
		if (o == null)
			return dflt;
		else if (o instanceof String)
			return (String) o;
		else if (o instanceof List)
			return SH.join("", (List) o);
		else
			throw new RuntimeException(key + " expecting map or list, not: " + o);
	}

	public static void putAmiScript(Map<String, Object> map, String key, String amiscript) {
		if (SH.isnt(amiscript))
			return;
		int c = 1;
		for (int i = 0; (i = amiscript.indexOf('\n', i)) != -1; c++) {
			if (i == amiscript.length() - 1)
				break;
			i++;
		}
		if (c == 1) {
			map.put(key, amiscript);
			return;
		}
		List<String> r = new ArrayList<String>(c);
		int j = 0;
		for (int i = 0; (i = amiscript.indexOf('\n', i)) != -1;) {
			r.add(amiscript.substring(j, i + 1));
			i++;
			j = i;
		}
		if (j < amiscript.length())
			r.add(amiscript.substring(j));
		map.put(key, r);
	}

	static public Map<String, String> toVarTypesConfiguration(AmiWebService service, String layoutAlias, CalcTypes vars, Map<String, String> sink) {
		AmiWebScriptManagerForLayout sm = service.getScriptManager(layoutAlias);
		for (String e : vars.getVarKeys())
			sink.put(e, sm.forType(vars.getType(e)));
		return sink;
	}

	static public BasicCalcTypes fromVarTypesConfiguration(AmiWebService service, Map<String, String> m) {
		if (m == null)
			return null;
		com.f1.utils.structs.table.stack.BasicCalcTypes r = new com.f1.utils.structs.table.stack.BasicCalcTypes(m.size());
		for (Entry<String, String> e : m.entrySet())
			r.putType(e.getKey(), AmiWebUtils.saveCodeToType(service, e.getValue()));
		return r;
	}

	public static Set<String> updateRealtimeIds(AmiWebRealtimeObjectListener[] amiListeners, Set<String> sink) {
		boolean same = true;
		int len = 0;
		for (AmiWebRealtimeObjectListener i : amiListeners)
			if (i instanceof AmiWebRealtimeObjectManager) {
				len++;
				if (!sink.contains(((AmiWebRealtimeObjectManager) i).getRealtimeId())) {
					same = false;
					break;
				}
			}
		if (!same || len != sink.size()) {
			sink.clear();
			for (AmiWebRealtimeObjectListener i : amiListeners)
				if (i instanceof AmiWebRealtimeObjectManager)
					sink.add(((AmiWebRealtimeObjectManager) i).getRealtimeId());
		}
		return sink;
	}

	/**
	 * "unwinds" a runtime AMI web object id to its base, e.g. in the parent layout, child layout objects have aliases and AMI ensures child object references are functional in
	 * parent layout by adding the appropriate alias in their ids. However when user exports configuration, he/she should see the original id of the object. <br>
	 * <br>
	 * ex: PROCESSOR:child.myProc -> PROCESSOR:myProc
	 * 
	 * @param alias
	 *            the alias of the layout
	 * @param id
	 *            id of the ami object
	 * @return the base AMI web object id. No change if there is no alias or if id already is in the base form
	 */
	public static String getRelativeRealtimeId(String alias, String id) {
		if (SH.startsWith(id, AmiWebManagers.FEED))
			return id;
		final int i = id.indexOf(':') + 1;
		return id.substring(0, i) + AmiWebUtils.getRelativeAlias(alias, id.substring(i));
	}

	public static String getFullRealtimeId(String amiLayoutFullAlias, String s) {
		if (SH.startsWith(s, AmiWebManagers.FEED) || SH.isnt(amiLayoutFullAlias))
			return s;
		final int i = s.indexOf(':') + 1;
		return s.substring(0, i) + AmiWebUtils.getFullAlias(amiLayoutFullAlias, s.substring(i));
	}

	public static String ari2adn(String i) {
		return SH.afterFirst(i, ':');
	}

	public static void ensureVisibleWithDivider(Portlet p, double dividerUnsnapOffset, boolean ignoreDividerLock) {
		if (p instanceof RootPortlet && !((RootPortlet) p).isPopupWindow())
			return;
		PortletContainer parent = p.getParent();
		if (parent == null)
			throw new RuntimeException("missing parent: " + p);
		if (parent instanceof AmiWebInnerDividerPortlet) {
			AmiWebInnerDividerPortlet awidp = (AmiWebInnerDividerPortlet) parent;
			AmiWebDividerPortlet awdp = awidp.getOwner();
			boolean lockPosition = awdp.getInnerContainer().getLockPosition(); // true means divider is locked

			if (ignoreDividerLock || !lockPosition) { // ignoreDividerLock is true when called from object browser and false when called from panel.bringToFront()
				double offset = awdp.getInnerContainer().getOffset();
				Portlet firstChild = awdp.getInnerContainer().getFirstChild();
				Portlet secondChild = awdp.getInnerContainer().getSecondChild();
				//				if (awdp.getSnapSetting() == AmiWebDividerPortlet.SNAP_SETTING_START || awdp.getSnapSetting() == AmiWebDividerPortlet.SNAP_SETTING_END) {
				if ((p == firstChild && awdp.getSnapSetting(true) == AmiWebDividerPortlet.SNAP_SETTING_START)
						|| (p == secondChild && awdp.getSnapSetting(true) == AmiWebDividerPortlet.SNAP_SETTING_END)) {
					awdp.unsnap();
					// if divider is already unsnapped and offset is less than default, it will not move, hence not catching user's attention
					// therefore move it to it's default offset
					double newOffset = awdp.getUnsnapMinPct(true);
					if (p == firstChild && offset < newOffset)
						awdp.getInnerContainer().setOffset(newOffset);
					if (p == secondChild && offset > newOffset)
						awdp.getInnerContainer().setOffset(newOffset);
				} else {
					if (p == firstChild && offset < dividerUnsnapOffset)
						awdp.getInnerContainer().setOffset(dividerUnsnapOffset);
					else if (p == secondChild && offset > (1 - dividerUnsnapOffset))
						awdp.getInnerContainer().setOffset(1 - dividerUnsnapOffset);
				}
			}
		}
		ensureVisibleWithDivider(parent, dividerUnsnapOffset, ignoreDividerLock);
		parent.bringToFront(p.getPortletId());
	}
	public static String formatDownstreamMode(byte mode) {
		switch (mode) {
			case AmiWebRealtimePortlet.DOWN_STREAM_MODE_OFF:
				return "OFF";
			case AmiWebRealtimePortlet.DOWN_STREAM_MODE_SELECTED_OR_ALL:
				return "SELECTED_OR_ALL";
			default:
				return "UNKNOWN:" + mode;
		}
	}
	public static byte parseDownstreamMode(String mode) {
		if ("OFF".equals(mode))
			return AmiWebRealtimePortlet.DOWN_STREAM_MODE_OFF;
		if ("SELECTED_OR_ALL".equals(mode))
			return AmiWebRealtimePortlet.DOWN_STREAM_MODE_SELECTED_OR_ALL;
		return -1;
	}
	public static String toHtmlIdSelector(AmiWebDomObject object) {
		String name = object.getAri();
		if (SH.isnt(name))
			return null;
		StringBuilder sb = new StringBuilder(5 + name.length());
		sb.append("AMI:");
		SH.replaceAll(name, '.', '-', sb);
		return sb.toString();
	}
	public static String toHtml(AmiWebService service, FlowControlThrow ftc, HtmlPortlet sink) {
		StringBuilder sb = new StringBuilder();
		int depth = 0;
		sb.append("<span style='font-family:courier'>");
		for (;;) {

			for (Frame t = ftc.getTailFrame(); t != null; t = t.getPrior()) {
				SH.repeat("&nbsp;", depth * 2, sb);
				if (depth > 50) {
					sb.append("<span style='background:red; color:white'>STACK TOO LARGE. SKIPPING REMAINING ENTRIES</span><BR>");
					break;
				}
				int linenum = t.getCallLineNumber();
				int offsetnum = t.getCallLineNumberOffset();
				int cursorpos = t.getCallCursorPosition();
				String ari = t.getCallSourceLabel();
				if (ari != null && sink != null) {
					final Callback cb = new HtmlPortlet.Callback("open_ari").setListener(service);
					cb.addAttribute("linenum", linenum).addAttribute("lineoffset", offsetnum).addAttribute("ari", ari).addAttribute("cursorpos", cursorpos);
					sb.append("&#10551;<a href='#' style='font-family:courier;color:#0000FF'  onclick='").append(sink.generateCallback(cb)).append("'>");
				} else {
					sb.append("&#10551;<a href='#' style='text-decoration:none;font-family:courier;color:#0000FF'>");
				}
				if (ari == null)
					sb.append("ANONYMOUS");
				else
					WebHelper.escapeHtml(ari, sb);
				sb.append(':');
				if (linenum != -1)
					sb.append(linenum).append(':').append(offsetnum);
				else
					sb.append("?:?");
				sb.append("</A>&nbsp;<B style='font-family:courier'>");
				String cd = t.getCallDescription(service.getMethodFactory());
				WebHelper.escapeHtml(cd, sb);
				sb.append("</B>");
				sb.append("<BR>");
				depth++;
			}
			String thrownValue = SH.toString(ftc.getThrownValue());
			sb.append("<span style='color:#880000'>");
			for (String s : SH.splitLines(thrownValue)) {
				SH.repeat("&nbsp;", depth * 2, sb);
				WebHelper.escapeHtml(s, sb).append("<BR>");
			}
			sb.append("</span>");
			Throwable cause = ftc.getCause();
			if (cause instanceof FlowControlThrow) {
				ftc = (FlowControlThrow) cause;
				sb.append("CAUSED BY<BR>");
			} else
				break;
		}
		sb.append("</span>");
		String r = sb.toString();
		if (sink != null)
			sink.setHtml(r);
		return r;
	}
	public static Exception testFormula(AmiWebFormula f, String exp, StringBuilder sb) {
		Exception e = f.testFormula(exp);
		if (e != null)
			sb.append("Error with ").append(f.getFormulaId()).append(": ").append(e.getMessage());
		return e;
	}
	public static byte guessColumnType(Class<?> type, String name) {
		byte t = AmiWebUtils.getCustomColumnType(type);
		if (type == Long.class) {
			if (SH.indexOfIgnoreCase(name, "date", 0) != -1)
				t = AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_SEC;
			else if (SH.indexOfIgnoreCase(name, "time", 0) != -1)
				t = AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_SEC;
		}
		return t;
	}
	public static int guessColumnPrecision(Class<?> type, String name) {
		byte t = guessColumnType(type, name);
		if (t == AmiWebUtils.CUSTOM_COL_TYPE_NUMERIC && !OH.isFloat(type))
			return 0;
		return AmiConsts.DEFAULT;
	}
	public static void onVarConstChanged(AmiWebStyledPortlet portlet, String var) {
		if (portlet instanceof AmiWebDomObject)
			onVarConstChanged((AmiWebDomObject) portlet, var);
	}
	private static void onVarConstChanged(AmiWebDomObject target, String var) {
		if (target.getFormulas() != null)
			target.getFormulas().onVarConstChanged(var);
		if (target.getAmiScriptCallbacks() != null)
			target.getAmiScriptCallbacks().onVarConstChanged(var);
		for (AmiWebDomObject i : target.getChildDomObjects())
			onVarConstChanged(i, var);
	}
	public static void recompileAmiscript(AmiWebDomObject target) {
		if (target.getFormulas() != null)
			target.getFormulas().recompileAmiscript();
		if (target.getAmiScriptCallbacks() != null)
			target.getAmiScriptCallbacks().recompileAmiscript();
		for (AmiWebDomObject i : target.getChildDomObjects())
			recompileAmiscript(i);
	}
	public static boolean isFromPrimaryCenter(AmiWebObject obj) {
		return (obj.getUniqueId() >> 56L) == 0;
	}

	static public Map<String, Object> deepCloneConfig(Map<String, Object> o) {
		return (Map<String, Object>) deepClone(o);

	}
	static private Object deepClone(Object o) {
		if (o instanceof Map) {
			Map<Object, Object> m = (Map) o;
			Map<Object, Object> r;
			if (m instanceof HasherMap)
				r = new HasherMap<Object, Object>(m);
			else if (m instanceof TreeMap)
				r = new TreeMap<Object, Object>(m);
			else
				r = new HashMap<Object, Object>(m);
			for (Map.Entry<?, Object> e : m.entrySet())
				e.setValue(deepClone(e.getValue()));
			return r;
		} else if (o instanceof List) {
			ArrayList r = new ArrayList((List) o);
			for (int i = 0; i < r.size(); i++)
				r.set(i, deepClone(r.get(i)));
			return r;
		} else
			return o;
	}

	public static boolean isRealtime(String id) {
		return SH.startsWith(id, AmiWebManagers.FEED) || SH.startsWith(id, AmiWebManagers.PANEL) || SH.startsWith(id, AmiWebManagers.PROCESSOR);
	}
	public static void getUsedVars(AmiWebFormulas formulas, CalcTypes source, BasicCalcTypes sink) {
		for (String s : formulas.getFormulaIds()) {
			AmiWebFormula f = formulas.getFormula(s);
			DerivedCellCalculator calc = f.getFormulaCalc();
			if (calc != null) {
				for (Object id : DerivedHelper.getDependencyIds(calc))
					if (sink.getType((String) id) == null) {
						Class type = source.getType((String) id);
						if (type != null)
							sink.putType((String) id, type);
					}
			}
		}

	}
	static public boolean isValidPanelId(CharSequence value) {
		return AmiUtils.isValidVariableName(value, false, false, true);
	}
	public static AmiWebService getService(CalcFrameStack sf) {
		CalcFrameStack top = sf.getTop();
		if (top instanceof AmiWebCalcTypesStack)
			return ((AmiWebTopCalcFrameStack) top).getService();
		return null;
	}
}
