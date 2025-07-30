package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmManager;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.style.AmiWebStyle;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuLink;
import com.f1.suite.web.menu.WebMenuLinkListener;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.impl.ColorPickerListener;
import com.f1.suite.web.portal.impl.ColorPickerPortlet;
import com.f1.suite.web.portal.impl.RootPortletDialog;
import com.f1.suite.web.portal.impl.RootPortletDialogListener;
import com.f1.suite.web.portal.impl.form.FormPortletColorField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTextEditField;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.ColorHelper;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.sql.aggs.AggMethodFactory;
import com.f1.utils.sql.aggs.AggregateFactory;
import com.f1.utils.structs.table.derived.BasicMethodFactory;
import com.f1.utils.structs.table.derived.DerivedCellMemberMethod;
import com.f1.utils.structs.table.derived.MethodFactory;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebMenuUtils {

	public static final class SetColorListener implements WebMenuLinkListener {

		final private FormPortletColorField target;
		final private String color;
		final private String text;

		public SetColorListener(FormPortletColorField target, String color, String text) {
			this.target = target;
			this.color = color;
			this.text = text;
		}

		@Override
		public boolean onMenuItem(WebMenuLink item) {
			target.setValue(color, text);
			target.getForm().fireFieldValueChangedTolisteners(target, Collections.EMPTY_MAP);
			return true;
		}

	}

	private static final String PREFIX_WIZARD_GET_DATAMODEL_AND_TABLE = "_wz1_";
	private static final String PREFIX_WIZARD_GET_DATAMODEL = "_wz2_";
	private static final String PREFIX_WIZARD_GET_PANEL = "_wz3_";
	private static final String PREFIX_WIZARD_GET_VAR = "_wz4_";
	private static final String PREFIX_WIZARD_GET_FIELD = "_wz5_";

	public static boolean processContextMenuAction(AmiWebService service, String action, FormPortletField<?> node) {
		if (!(node instanceof FormPortletTextEditField))
			return false;
		FormPortletTextEditField field = (FormPortletTextEditField) node;
		if ("_chooseColor".equals(action)) {
			showCustomColorChooser(field, true);
			return true;
		} else if ("_chooseColorNq".equals(action)) {
			showCustomColorChooser(field, false);
			return true;
		}
		String txt = parseContextMenuAction(service, action);
		if (txt == null)
			return false;
		if (action.startsWith("fm_") || action.startsWith("font_")) {
			String existingValue = SH.noNull(field.getValue());
			String newValue = null;
			int start = -1;

			int cnt = 0;
			for (char c : existingValue.toCharArray())
				if (c == '"')
					cnt++;
			if (existingValue.length() == 0) {
				newValue = '"' + txt + '"';
			} else if (SH.endsWith(existingValue, '"')) {
				start = existingValue.lastIndexOf("\"", existingValue.length() - 2);
				if (start == -1)
					newValue = existingValue + txt;
				else {
					String t = existingValue.substring(start + 1, existingValue.length() - 1);
					newValue = existingValue.substring(0, start) + '"' + cleanupStyle(AmiWebUtils.getService(node.getForm().getManager()), (t.length() == 0 ? t : t + ',') + txt)
							+ '\"';
				}
			} else {
				if (cnt % 2 == 0)
					newValue = existingValue + " \"" + txt + '"';
				else
					newValue = existingValue + txt;
			}
			field.setValue(newValue);
			// onFieldValueChanged will fire for table/tree
			field.getForm().fireFieldValueChangedTolisteners(field, Collections.EMPTY_MAP);
		} else if (action.startsWith("co_")) {
			applyColorText(txt, field, true);
		} else if (action.startsWith("conq_")) {
			applyColorText(txt, field, false);
		} else {
			if (action.startsWith("aggvar_") && SH.isnt(field.getValue()))
				txt = "sum(" + txt + ")";
			else if (action.startsWith("cntvar_") && SH.isnt(field.getValue()))
				txt = "count(" + txt + ")";
			if (field instanceof FormPortletTextAreaField) {
				FormPortletTextAreaField ta = (FormPortletTextAreaField) field;
				if (ta.getCursorPosition() == -1)
					ta.insertTextNoThrow((ta.getValue()).length(), txt);
				else
					ta.insertAtCursor(txt + " ");
			} else {
				field.insertAtCursor(txt);
				field.getForm().fireFieldValueChangedTolisteners(field, Collections.EMPTY_MAP);
			}
			field.focus();
		}
		return true;
	}
	public static String parseContextMenuAction(AmiWebService service, String action) {
		String txt = null;
		if (action.startsWith("var_")) {
			txt = AmiUtils.escapeVarName(SH.stripPrefix(action, "var_", true));
		} else if (action.startsWith("aggvar_")) {
			txt = AmiUtils.escapeVarName(SH.stripPrefix(action, "aggvar_", true));
		} else if (action.startsWith("cntvar_")) {
			txt = AmiUtils.escapeVarName(SH.stripPrefix(action, "cntvar_", true));
		} else if (action.startsWith("co_")) {
			txt = SH.stripPrefix(action, "co_", true);
		} else if (action.startsWith("conq_")) {
			txt = SH.stripPrefix(action, "conq_", true);
		} else if (action.startsWith("ag_")) {
			txt = SH.stripPrefix(action, "ag_", true) + "(";
		} else if (action.startsWith("prep_")) {
			txt = SH.stripPrefix(action, "prep_", true) + "(";
		} else if (action.startsWith("_dm_")) {
			txt = SH.stripPrefix(action, "_dm_", true);
		} else if (action.startsWith("op_")) {
			if (action.equals("op_add"))//TODO: make this a map
				txt = " + ";
			else if (action.equals("op_sub"))
				txt = " - ";
			else if (action.equals("op_mul"))
				txt = " * ";
			else if (action.equals("op_div"))
				txt = " / ";
			else if (action.equals("op_mod"))
				txt = " % ";
			else if (action.equals("op_eq"))
				txt = " == ";
			else if (action.equals("op_ne"))
				txt = " != ";
			else if (action.equals("op_an"))
				txt = " && ";
			else if (action.equals("op_or"))
				txt = " || ";
			else if (action.equals("op_lt"))
				txt = " < ";
			else if (action.equals("op_gt"))
				txt = " > ";
			else if (action.equals("op_le"))
				txt = " <= ";
			else if (action.equals("op_ge"))
				txt = " >= ";
			else if (action.equals("op_opp"))
				txt = " (";
			else if (action.equals("op_clp"))
				txt = ") ";
			else if (action.equals("op_str"))
				txt = "\"";
			else if (action.equals("op_str2"))
				txt = "\"\"\"";
			else if (action.equals("op_esc"))
				txt = "\\";
			else if (action.equals("op_if"))
				txt = " ? ";
			else if (action.equals("op_el"))
				txt = " : ";
			else if (action.equals("op_ma"))
				txt = " *= ";
		} else if (action.startsWith("sqlOp_")) {
			txt = " " + SH.stripPrefix(action, "sqlOp_", false) + " ";
		} else if (action.startsWith("sql_")) {
			txt = SH.stripPrefix(action, "sql_", false);
		} else if (action.startsWith("fm_")) {
			txt = SH.stripPrefix(action, "fm_", true);
		} else if (action.startsWith("fu_")) {
			txt = SH.stripPrefix(action, "fu_", true) + "(";
		} else if (action.startsWith("font_")) {
			txt = SH.replaceAll(SH.stripPrefix(action, "font_", true), '_', ' ');
		} else if (action.startsWith("_wz")) {
			return parseWizard(service, action);
		} else if (action.startsWith("dsOp_")) {
			txt = " " + SH.stripPrefix(action, "dsOp_", false) + " ";
		}
		return txt;
	}
	private static String parseWizard(AmiWebService service, String action) {
		if (action.startsWith(PREFIX_WIZARD_GET_DATAMODEL)) {
			String t = SH.stripPrefix(action, PREFIX_WIZARD_GET_DATAMODEL, false);
			String baseAlias = SH.beforeFirst(t, '!');
			String relativeAdn = SH.afterFirst(t, '!');
			AmiWebDm dm = service.getDmManager().getDmByAliasDotName(AmiWebUtils.getFullAlias(baseAlias, relativeAdn));
			if (dm == null)
				return null;
			return "Datamodel " + AmiUtils.escapeVarName("dm_" + dm.getDmName()) + " = layout.getDatamodel(\"" + relativeAdn + "\");\n";

		} else if (action.startsWith(PREFIX_WIZARD_GET_DATAMODEL_AND_TABLE)) {
			String t = SH.stripPrefix(action, PREFIX_WIZARD_GET_DATAMODEL_AND_TABLE, false);
			String baseAlias = SH.beforeFirst(t, '!');
			String t2 = SH.afterFirst(t, '!');
			String relativeAdn = SH.beforeFirst(t2, '!');
			String tableName = SH.afterFirst(t2, '!');
			final AmiWebDm dm = service.getDmManager().getDmByAliasDotName(AmiWebUtils.getFullAlias(baseAlias, relativeAdn));
			if (dm == null)
				return null;
			AmiWebScriptManagerForLayout sm = service.getScriptManager(dm.getAmiLayoutFullAlias());
			AmiWebDmTableSchema tb = dm.getResponseOutSchema().getTable(tableName);
			StringBuilder r = new StringBuilder("Datamodel " + AmiUtils.escapeVarName("dm_" + dm.getDmName()) + " = layout.getDatamodel(\"" + relativeAdn + "\");\n");
			String tbName = AmiUtils.escapeVarName("tb_" + tableName);
			r.append("  Table " + tbName + " = " + AmiUtils.escapeVarName("dm_" + dm.getDmName()) + ". getData().get(\"" + tableName + "\");\n");
			if (tb != null) {
				r.append("  for(int i=0;i<").append(tbName).append(".getRowsCount();i++){\n");
				r.append("    Row row=").append(tbName).append(".getRow(i);\n");
				for (String varname : tb.getClassTypes().getVarKeys()) {
					String type = sm.forType(tb.getClassTypes().getType(varname));
					r.append("    ").append(type).append(' ').append(AmiUtils.escapeVarName("_" + varname)).append(" = row.get(\"").append(varname).append("\");\n");
				}
				r.append(" }\n");
			}
			return r.toString();

		} else if (action.startsWith(PREFIX_WIZARD_GET_PANEL)) {
			String t = SH.stripPrefix(action, PREFIX_WIZARD_GET_PANEL, false);
			String baseAlias = SH.beforeFirst(t, '!');
			String relativeAdn = SH.afterFirst(t, '!');
			AmiWebAliasPortlet pnl = service.getPortletByAliasDotPanelId(AmiWebUtils.getFullAlias(baseAlias, relativeAdn));
			if (pnl == null)
				return null;
			String type = service.getScriptManager(pnl.getAmiLayoutFullAlias()).forType(pnl.getClass());
			return type + ' ' + AmiUtils.escapeVarName("pn_" + pnl.getAmiPanelId()) + " = layout.getPanel(\"" + relativeAdn + "\");\n";
		} else if (action.startsWith(PREFIX_WIZARD_GET_FIELD)) {
			String t = SH.stripPrefix(action, PREFIX_WIZARD_GET_FIELD, false);
			String[] parts = SH.split('!', t);
			String baseAlias = parts[0];
			String relativeAdn = parts[1];
			String id = parts[2];
			AmiWebQueryFormPortlet pnl = (AmiWebQueryFormPortlet) service.getPortletByAliasDotPanelId(AmiWebUtils.getFullAlias(baseAlias, relativeAdn));
			if (pnl == null)
				return null;
			AmiWebScriptManagerForLayout sm = pnl.getScriptManager();
			String type = sm.forType(pnl.getClass());
			QueryField<?> field = pnl.getFieldsById().get(id);
			String fieldtype = sm.forType(field.getClass());
			String validVarname = AmiUtils.escapeVarName("pn_" + pnl.getAmiPanelId());
			String r = type + ' ' + validVarname + " = layout.getPanel(\"" + relativeAdn + "\");\n";
			String r2 = fieldtype + ' ' + AmiUtils.escapeVarName("fl_" + field.getName()) + " = " + validVarname + ".getField(\"" + field.getName() + "\");\n";
			return r + r2;

		} else if (action.startsWith(PREFIX_WIZARD_GET_VAR)) {
			String name = SH.stripPrefix(action, PREFIX_WIZARD_GET_VAR, false);
			return "String " + AmiUtils.toValidVarName(name) + " = session.getProperty(\"" + name + "\");\n";
		} else
			return null;
	}
	public static void createPositionsMenu(WebMenu sink, boolean isExhaustive) {
		WebMenu positions = new BasicWebMenu("Positions", true);
		positions.add(new BasicWebMenuLink("center", true, "co_center"));
		positions.add(new BasicWebMenuLink("top", true, "co_top"));
		positions.add(new BasicWebMenuLink("bottom", true, "co_bottom"));
		if (isExhaustive) {
			positions.add(new BasicWebMenuLink("left", true, "co_left"));
			positions.add(new BasicWebMenuLink("right", true, "co_right"));
			positions.add(new BasicWebMenuLink("topleft", true, "co_topleft"));
			positions.add(new BasicWebMenuLink("topright", true, "co_topright"));
			positions.add(new BasicWebMenuLink("bottomleft", true, "co_bottomleft"));
			positions.add(new BasicWebMenuLink("bottomright", true, "co_bottomright"));
		}
		sink.add(positions);
	}
	public static void createColorsMenu(WebMenu sink, AmiWebStyle style) {
		sink.add(createColorsMenu(true, style));
	}
	public static WebMenu createColorsMenu(AmiWebStyle style) {
		return createColorsMenu(true, style);
	}
	public static void createColorsMenu(WebMenu sink, boolean quotes, AmiWebStyle style) {
		sink.add(createColorsMenu(quotes, style));
	}
	public static WebMenu createColorsMenu(boolean quotes, AmiWebStyle style) {
		WebMenu colors = new BasicWebMenu("Colors", true);
		colors.add(new BasicWebMenuLink("Custom Color...", true, quotes ? "_chooseColor" : "_chooseColorNq"));
		for (Entry<String, String> s : style.getVarValues().entrySet()) {
			String action = "conq_" + s.getKey();
			colors.add(createColorMenuItem(s.getKey(), s.getValue(), action));
		}
		colors.add(new BasicWebMenuDivider());
		for (String s : WebHelper.COMMON_COLORS) {
			String color = WebHelper.toHex(s);
			String action = (quotes ? "co_" : "conq_") + color;
			colors.add(createColorMenuItem(s, color, action));
		}
		return colors;
	}
	public static WebMenu createColorFieldMenu(AmiWebStyle style, FormPortletColorField target) {
		WebMenu colors = new BasicWebMenu();
		for (Entry<String, String> s : style.getVarValues().entrySet()) {
			BasicWebMenuLink ml = createColorMenuItem(s.getKey(), s.getValue(), null);
			ml.setAutoclose(true);
			ml.setListener(new SetColorListener(target, s.getValue(), s.getKey()));
			colors.add(ml);

		}
		return colors;
	}
	private static BasicWebMenuLink createColorMenuItem(String label, String color, String action) {
		String fg = ColorHelper.colorDodgeRgbToString(ColorHelper.parseRgb(color), 0xffffff);
		BasicWebMenuLink colorMenuItem = new BasicWebMenuLink(
				"<span style='border-radius:4px;font-family:courier;padding:0px;background:white;background-repeat:repeat-y;background-position:right;background-image:url(rsc/checkers.png);color:"
						+ fg + "'><span style='background:" + color + "'>&nbsp;" + SH.uppercaseFirstChar(label) + SH.repeat("&nbsp;", 16 - label.length()) + "</span></span>",
				true, action).setAutoclose(false);
		return colorMenuItem;
	}
	public static void createAggOperatorsMenu(WebMenu sink, boolean isRealtime) {

		WebMenu operators = new BasicWebMenu("Aggregate Functions", true);
		if (isRealtime) {
			operators.add(new BasicWebMenuLink("sum(<i>Number value</i>)", true, "ag_sum").setAutoclose(false).setCssStyle("_fm=courier"));
			operators.add(new BasicWebMenuLink("count(<i>Object value</i>)", true, "ag_count").setAutoclose(false).setCssStyle("_fm=courier"));
			operators.add(new BasicWebMenuLink("min(<i>Comparable value</i>)", true, "ag_min").setAutoclose(false).setCssStyle("_fm=courier"));
			operators.add(new BasicWebMenuLink("max(<i>Comparable value</i>)", true, "ag_max").setAutoclose(false).setCssStyle("_fm=courier"));
		} else {
			for (Entry<String, AggMethodFactory> i : AggregateFactory.AGG_METHOD_FACTORIES.entrySet()) {
				String desc = i.getValue().getDefinition().toString();
				operators.add(new BasicWebMenuLink(desc, true, "ag_" + i.getKey()).setAutoclose(false).setCssStyle("_fm=courier"));
				i.getKey();
			}
		}
		operators.sort();
		sink.add(operators);

	}

	public static void createOperatorsMenu(WebMenu sink, AmiWebService service, String layoutAlias) {
		WebMenu operators = new BasicWebMenu("Operators", true);
		operators.add(new BasicWebMenuLink("+&nbsp;&nbsp;&nbsp;Add", true, "op_add").setAutoclose(false).setCssStyle("_fm=courier"));
		operators.add(new BasicWebMenuLink("-&nbsp;&nbsp;&nbsp;Subtract", true, "op_sub").setAutoclose(false).setCssStyle("_fm=courier"));
		operators.add(new BasicWebMenuLink("*&nbsp;&nbsp;&nbsp;Multiple", true, "op_mul").setAutoclose(false).setCssStyle("_fm=courier"));
		operators.add(new BasicWebMenuLink("/&nbsp;&nbsp;&nbsp;Divide", true, "op_div").setAutoclose(false).setCssStyle("_fm=courier"));
		operators.add(new BasicWebMenuLink("%&nbsp;&nbsp;&nbsp;Modulus", true, "op_mod").setAutoclose(false).setCssStyle("_fm=courier"));
		operators.add(new BasicWebMenuLink("==&nbsp;&nbsp;Equal To", true, "op_eq").setAutoclose(false).setCssStyle("_fm=courier"));
		operators.add(new BasicWebMenuLink("!=&nbsp;&nbsp;Not Equal To", true, "op_ne").setAutoclose(false).setCssStyle("_fm=courier"));
		operators.add(new BasicWebMenuLink("&&&nbsp;&nbsp;And", true, "op_an").setAutoclose(false).setCssStyle("_fm=courier"));
		operators.add(new BasicWebMenuLink("||&nbsp;&nbsp;Or", true, "op_or").setAutoclose(false).setCssStyle("_fm=courier"));
		operators.add(new BasicWebMenuLink("?&nbsp;&nbsp;&nbsp;Then", true, "op_if").setAutoclose(false).setCssStyle("_fm=courier"));
		operators.add(new BasicWebMenuLink(":&nbsp;&nbsp;&nbsp;Else", true, "op_el").setAutoclose(false).setCssStyle("_fm=courier"));
		operators.add(new BasicWebMenuLink("<&nbsp;&nbsp;&nbsp;Less Than", true, "op_lt").setAutoclose(false).setCssStyle("_fm=courier"));
		operators.add(new BasicWebMenuLink(">&nbsp;&nbsp;&nbsp;Greater Than", true, "op_gt").setAutoclose(false).setCssStyle("_fm=courier"));
		operators.add(new BasicWebMenuLink("<=&nbsp;&nbsp;Less Than Or Equal To", true, "op_le").setAutoclose(false).setCssStyle("_fm=courier"));
		operators.add(new BasicWebMenuLink(">=&nbsp;&nbsp;Greater Than Or Equal To", true, "op_ge").setAutoclose(false).setCssStyle("_fm=courier"));
		operators.add(new BasicWebMenuLink("*=&nbsp;&nbsp;Matches Regex", true, "op_ma").setAutoclose(false).setCssStyle("_fm=courier"));
		operators.add(new BasicWebMenuLink("(&nbsp;&nbsp;&nbsp;Open", true, "op_opp").setAutoclose(false).setCssStyle("_fm=courier"));
		operators.add(new BasicWebMenuLink(")&nbsp;&nbsp;&nbsp;Close", true, "op_clp").setAutoclose(false).setCssStyle("_fm=courier"));
		operators.add(new BasicWebMenuLink("\"&nbsp;&nbsp;&nbsp;Start / End String", true, "op_str").setAutoclose(false).setCssStyle("_fm=courier"));
		operators.add(new BasicWebMenuLink("\"\"\"&nbsp;Start / End Const String", true, "op_str2").setAutoclose(false).setCssStyle("_fm=courier"));
		operators.add(new BasicWebMenuLink("\\&nbsp;&nbsp;&nbsp;Escape", true, "op_esc").setAutoclose(false).setCssStyle("_fm=courier"));
		WebMenu functions = new BasicWebMenu("Functions", true);
		StringBuilder sb = new StringBuilder();
		List<MethodFactory> sink2 = new ArrayList<MethodFactory>();
		service.getScriptManager(layoutAlias).getMethodFactory().getAllMethodFactories(sink2);
		for (MethodFactory s : sink2) {
			ParamsDefinition def = s.getDefinition();
			String name = def.getMethodName();
			sb.append(name);
			sb.append("(<i>");
			for (int i = 0; i < def.getParamsCount(); i++) {
				if (i > 0)
					sb.append(", ");
				SH.getSimpleName(def.getParamType(i), sb);
				sb.append(' ').append(def.getParamName(i));
			}
			sb.append("</i>)");
			functions.add(new BasicWebMenuLink(SH.toStringAndClear(sb), true, "fu_" + name).setAutoclose(true).setCssStyle("_fm=courier"));

		}
		functions.sort();
		sink.add(operators);
		sink.add(functions);
	}

	public static void createFormatsMenu(WebMenu sink, AmiWebService service) {
		BasicWebMenu decs = new BasicWebMenu("Decoration", true);
		BasicWebMenu aligns = new BasicWebMenu("Alignment", true);
		decs.add(new BasicWebMenuLink("<B>Bold</B>", true, "fm_bold").setAutoclose(false));
		decs.add(new BasicWebMenuLink("<U>Underline</U>", true, "fm_underline").setAutoclose(false));
		decs.add(new BasicWebMenuLink("<I>Italic</I>", true, "fm_italic").setAutoclose(false));
		decs.add(new BasicWebMenuLink("Hidden", true, "fm_hide").setAutoclose(false));
		decs.add(new BasicWebMenuLink("<strike>Strike</strike>", true, "fm_strike").setAutoclose(false));
		aligns.add(new BasicWebMenuLink("Left", true, "fm_left").setAutoclose(false));
		aligns.add(new BasicWebMenuLink("&nbsp;&nbsp;Center</center>", true, "fm_center").setAutoclose(false));
		aligns.add(new BasicWebMenuLink("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Right</right>", true, "fm_right").setAutoclose(false));
		createFontsMenu(sink, service);
		sink.add(aligns);
		sink.add(decs);
	}
	public static void createMemberMethodMenu(WebMenu sink, AmiWebService service, String layoutAlias) {
		createMemberMethodMenu(sink, service, true, false, false, layoutAlias);
	}
	public static void createMemberMethodMenu(WebMenu sink, AmiWebService service, boolean autoCloseMemberMethods, boolean autoClosePanels, boolean autoCloseDms,
			String layoutAlias) {
		BasicMethodFactory mf = service.getScriptManager(layoutAlias).getMethodFactory();
		List<DerivedCellMemberMethod<Object>> methods = new ArrayList<DerivedCellMemberMethod<Object>>();
		mf.getMemberMethods(null, null, methods);

		Map<Class, BasicWebMenu> memberMethods = new HashMap<Class, BasicWebMenu>();
		for (DerivedCellMemberMethod<Object> i : methods) {
			Class tt = i.getTargetType();
			BasicWebMenu mm = memberMethods.get(tt);
			if (mm == null)
				memberMethods.put(tt, mm = new BasicWebMenu(mf.forType(tt), true));
			String name;
			if (i.getParamTypes().length == 0)
				name = "_dm_" + i.getMethodName() + "()";
			else
				name = "_dm_" + i.getMethodName() + "(";
			mm.add(new BasicWebMenuLink(AmiWebUtils.getMemberMethodDescription(mf, i), true, name).setAutoclose(autoCloseMemberMethods));
		}
		for (BasicWebMenu i : memberMethods.values())
			i.sort();
		BasicWebMenu mm = new BasicWebMenu("Member Methods... ", true, AH.toArray(memberMethods.values(), BasicWebMenu.class));

		BasicWebMenu wz = new BasicWebMenu("Wizards... ", true);
		AmiWebDmManager dmgr = service.getDmManager();
		BasicWebMenu wzGetPanel = new BasicWebMenu("Get Panel...", true);
		BasicWebMenu wzGetField = new BasicWebMenu("Get Field...", true);
		BasicWebMenu wzGetDatamodel = new BasicWebMenu("Get Datamodel...", true);
		BasicWebMenu wzGetDatamodelAndTable = new BasicWebMenu("Get Datamodel and Table...", true);
		BasicWebMenu wzGetVars = new BasicWebMenu("Get Session Property...", true);
		wz.add(wzGetPanel);
		wz.add(wzGetDatamodel);
		wz.add(wzGetDatamodelAndTable);
		wz.add(wzGetVars);
		wz.add(wzGetField);
		for (AmiWebPortlet i : PortletHelper.findPortletsByType(service.getDesktop(), AmiWebPortlet.class)) {
			String t = AmiWebUtils.getRelativeAliasNoThrow(layoutAlias, i.getAmiLayoutFullAliasDotId());
			if (SH.isnt(t))
				continue;
			String type = mf.forType(i.getClass());
			wzGetPanel.add(
					new BasicWebMenuLink(i.getAmiLayoutFullAliasDotId() + " (" + type + ")", true, PREFIX_WIZARD_GET_PANEL + layoutAlias + "!" + t).setAutoclose(autoClosePanels));
			if (i instanceof AmiWebQueryFormPortlet) {
				AmiWebQueryFormPortlet fp = (AmiWebQueryFormPortlet) i;
				for (QueryField<?> e : fp.getFieldsById().values()) {
					wzGetField.add(new BasicWebMenuLink(i.getAmiLayoutFullAliasDotId() + "." + e.getName(), true, PREFIX_WIZARD_GET_FIELD + layoutAlias + "!" + t + "!" + e.getId())
							.setAutoclose(autoClosePanels));
				}
			}

		}
		for (AmiWebDm dm : dmgr.getDatamodels()) {
			String t = AmiWebUtils.getRelativeAliasNoThrow(layoutAlias, dm.getAmiLayoutFullAliasDotId());
			if (SH.isnt(t))
				continue;
			wzGetDatamodel.add(new BasicWebMenuLink(dm.getAmiLayoutFullAliasDotId(), true, PREFIX_WIZARD_GET_DATAMODEL + layoutAlias + "!" + t).setAutoclose(autoCloseDms));
			for (String tbName : dm.getResponseOutSchema().getTableNamesSorted()) {
				wzGetDatamodelAndTable.add(new BasicWebMenuLink(dm.getAmiLayoutFullAliasDotId() + " --> " + tbName, true,
						PREFIX_WIZARD_GET_DATAMODEL_AND_TABLE + layoutAlias + "!" + t + "!" + tbName));
			}
		}
		//		List<String> varsInOrder = CH.l(service.getScriptManager(layoutAlias).getGlobalVarValues().keySet());
		//		for (String var : varsInOrder) {
		//			wzGetVars.add(new BasicWebMenuLink(var, true, PREFIX_WIZARD_GET_VAR + var));
		//
		//		}
		wzGetDatamodel.sort();
		wzGetDatamodelAndTable.sort();
		wzGetPanel.sort();
		wzGetVars.sort();
		mm.sort();

		sink.add(mm);
		sink.add(wz);
	}

	//table settings, table column,aggregate table settings, treemap settings, field,tree settings,tab entry, tab,tree column, tree groupings 
	public static WebMenu createVariablesMenu(WebMenu sink, boolean isAggregate, final AmiWebAbstractPortlet tablePortlet) {
		WebMenu variables = new BasicWebMenu(isAggregate ? "Variables To Aggregate On" : "Variables", true);

		com.f1.base.CalcTypes availableVariables = AmiWebUtils.getAvailableVariables(tablePortlet.getService(), tablePortlet);
		com.f1.utils.structs.table.stack.BasicCalcTypes specialVars = new com.f1.utils.structs.table.stack.BasicCalcTypes(tablePortlet.getSpecialVariables());
		for (String name : CH.sortUniq(availableVariables.getVarKeys())) {
			String displayName;
			if (tablePortlet.getSpecialVariables().getType(name) != null) {
				specialVars.putType(name, availableVariables.getType(name));
				continue;
			} else
				displayName = name;
			Class<?> type = availableVariables.getType(name);
			if (!isAggregate)
				variables.add(new BasicWebMenuLink(displayName, true, "var_" + name).setAutoclose(false).setCssStyle("_fm=courier"));
			else
				variables.add(new BasicWebMenuLink(displayName, true, (Number.class.isAssignableFrom(type) ? "aggvar_" : "cntvar_") + name).setAutoclose(false)
						.setCssStyle("_fm=courier"));
		}

		if (!specialVars.isVarsEmpty()) {
			variables.add(new BasicWebMenuDivider());
			for (String name : CH.sort(specialVars.getVarKeys())) {
				String title = tablePortlet.getSpecialVariableTitleFor(name);
				if (OH.ne(title, name))
					title = "  (" + title + ")";
				else
					title = "";

				Class<?> type = specialVars.getType(name);
				if (!isAggregate)
					variables.add(new BasicWebMenuLink(name + title, true, "var_" + name).setAutoclose(false).setCssStyle("_fm=courier"));
				else
					variables.add(new BasicWebMenuLink(name + title, true, (Number.class.isAssignableFrom(type) ? "aggvar_" : "cntvar_") + name).setAutoclose(false)
							.setCssStyle("_fm=courier"));
			}
		}
		variables.add(new BasicWebMenuDivider());
		addConstVars(tablePortlet.getService(), "", variables, tablePortlet);
		sink.add(variables);
		if (isAggregate)
			createAggOperatorsMenu(sink, true);
		else
			createOperatorsMenu(sink, tablePortlet.getService(), tablePortlet.getAmiLayoutFullAlias());
		return variables;
	}
	private static void addConstVars(AmiWebService service, String prefix, WebMenu variables, AmiWebDomObject stylePeer) {
		Iterable<String> consts = service.getScriptManager(stylePeer.getAmiLayoutFullAlias()).getConsts(stylePeer);
		for (String var : CH.sort(consts, SH.COMPARATOR_CASEINSENSITIVE))
			variables.add(new BasicWebMenuLink(var, true, "var_" + prefix + var).setCssStyle("_fm=courier"));
	}

	public static WebMenu createIconsMenu(boolean htmlFormat) {
		WebMenu r = new BasicWebMenu("Icons", true);
		String[] icons = new String[] { "up", "down", "warn", "info", "error", "okay" };
		for (String icon : icons) {
			String prefix;
			String suffix;
			if (htmlFormat) {
				prefix = "<img src='";
				suffix = "'>";
			} else {
				prefix = "";
				suffix = "";
			}
			r.add(new BasicWebMenuLink("<img src='pub/" + icon + ".png'> - " + SH.uppercaseFirstChar(icon), true, "co_" + prefix + "pub/" + icon + ".png" + suffix));
		}
		return r;

	}
	private static void createFontsMenu(WebMenu sink, AmiWebService service) {
		BasicWebMenu fonts = new BasicWebMenu("Fonts", true);
		for (String font : AmiWebUtils.getFonts(service))
			fonts.add(new BasicWebMenuLink("<font face='" + font + "'>" + font + "</font", true, "font_" + SH.replaceAll(font, ' ', '_')).setAutoclose(false));
		sink.add(fonts);
	}
	public static void applyColorText(String color, FormPortletTextEditField teField, boolean quotes) {
		String toReplace = quotes ? extractColorText(teField) : extractColorTextNq(teField);
		if (toReplace == null) {
			if (quotes)
				teField.insertAtCursor("\"" + color + "\"");
			else
				teField.insertAtCursor(color);
			teField.getForm().fireFieldValueChangedTolisteners(teField, Collections.EMPTY_MAP);
			return;
		}
		String value = teField.getValue();
		int position = teField.getCursorPosition();
		int pos = value.lastIndexOf(toReplace, position - 1);
		if (pos == -1) {
			if (quotes)
				teField.insertAtCursor("\"" + color + "\"");
			else
				teField.insertAtCursor(color);
			teField.getForm().fireFieldValueChangedTolisteners(teField, Collections.EMPTY_MAP);
			return;
		}
		teField.setValue(value.substring(0, pos) + color + value.substring(pos + toReplace.length()));
		teField.getForm().fireFieldValueChangedTolisteners(teField, Collections.EMPTY_MAP);

	}
	public static void showCustomColorChooser(FormPortletTextEditField field) {
		showCustomColorChooser(field, true);
	}
	public static void showCustomColorChooser(FormPortletTextEditField field, boolean quotes) {
		String orig = (quotes ? extractColorText(field) : extractColorTextNq(field));
		ColorApplier ca = new ColorApplier(field, quotes, orig != null);
		ColorPickerPortlet cpp = new ColorPickerPortlet(field.getForm().generateConfig(), orig, ca);
		cpp.setCorrelationData(field);
		RootPortletDialog dialog = field.getForm().getManager().showDialog("Choose Color", cpp);
		dialog.addListener(ca);
		dialog.setShadeOutside(false);
	}
	private static String extractColorTextNq(FormPortletTextEditField teField) {
		String value = teField.getValue();
		int position = teField.getCursorPosition();
		if (value == null)
			return null;
		if (position >= 0 && position < value.length()) {
			int start = value.lastIndexOf('#', position - 1);
			int end = start + 1;
			for (; end < value.length(); end++) {
				char c = value.charAt(end);
				if (OH.isntBetween(c, 'a', 'f') && OH.isntBetween(c, 'A', 'F') && OH.isntBetween(c, '0', '9'))
					break;
			}
			if (start != -1 && end != -1) {
				String r = value.substring(start, end);
				if (WebHelper.isColor(r))
					return r;
			}
			start = value.lastIndexOf('#', position - 1);
			end = start + 7;
			if (start != -1 && end < value.length() && position <= end) {
				String r = value.substring(start, end);
				if (WebHelper.isColor(r))
					return r;
			}

		}
		value = value.trim();
		if (value.length() == 7 && value.startsWith("#"))
			return value;
		return null;
	}
	private static String extractColorText(FormPortletTextEditField teField) {
		String value = teField.getValue();
		int position = teField.getCursorPosition();
		if (value == null)
			return null;
		if (position >= 0 && position < value.length()) {
			int start = value.lastIndexOf('"', position - 1);
			int end = value.indexOf('"', position);
			if (start != -1 && end != -1) {
				String r = value.substring(start + 1, end);
				if (WebHelper.isColor(r))
					return r;
			}
			start = value.lastIndexOf('#', position - 1);
			end = start + 7;
			if (start != -1 && end < value.length() && position <= end) {
				String r = value.substring(start, end);
				if (WebHelper.isColor(r))
					return r;
			}

		}
		value = value.trim();
		if (value.length() == 9 && value.startsWith("\"#") && value.endsWith("\""))
			return value.substring(1, value.length() - 1);
		return null;
	}
	private static String cleanupStyle(AmiWebService service, String style) {
		String[] parts = SH.split(',', style);
		String font = null;
		String align = null;
		boolean underline = false, strike = false, italic = false, bold = false;
		for (String part : parts) {
			part = part.trim();
			if (service.getFontsManager().getFonts().contains(part)) {
				font = part;
			} else if ("underline".equals(part)) {
				underline = true;
			} else if ("strike".equals(part)) {
				strike = true;
			} else if ("italic".equals(part)) {
				italic = true;
			} else if ("bold".equals(part)) {
				bold = true;
			} else if ("left".equals(part)) {
				align = part;
			} else if ("center".equals(part)) {
				align = part;
			} else if ("right".equals(part)) {
				align = part;
			}
		}
		StringBuilder r = new StringBuilder();
		if (font != null)
			r.append(',').append(font);
		if (align != null)
			r.append(',').append(align);
		if (underline)
			r.append(",underline");
		if (strike)
			r.append(",strike");
		if (italic)
			r.append(",italic");
		if (bold)
			r.append(",bold");
		return r.length() == 0 ? "" : r.substring(1);
	}

	private static class ColorApplier implements ColorPickerListener, RootPortletDialogListener {

		final private FormPortletTextEditField field;
		private boolean quotes;
		private String origValue;
		private int origPos;
		private boolean canReplace;

		public ColorApplier(FormPortletTextEditField field, boolean quotes, boolean canReplace) {
			this.field = field;
			this.canReplace = canReplace;
			this.origValue = this.field.getValue();
			this.origPos = this.field.getCursorPosition();
			this.quotes = quotes;
		}

		@Override
		public void onColorChanged(ColorPickerPortlet target, String oldColor, String nuwColor) {
			if (!canReplace)
				return;
			String color = target.getColor();
			if (color != null)
				applyColorText(target.getColor(), this.field, quotes);
			else
				this.field.setValue("");
		}

		@Override
		public void onOkayPressed(ColorPickerPortlet target) {
			String color = target.getColor();
			if (color != null)
				applyColorText(target.getColor(), this.field, quotes);
			else
				this.field.setValue("");
			target.close();
		}

		@Override
		public void onCancelPressed(ColorPickerPortlet target) {
			this.field.setValue(this.origValue);
			if (origPos != -1) {
				this.field.setCursorPosition(origPos);
				this.field.getForm().fireFieldValueChangedTolisteners(this.field, Collections.EMPTY_MAP);
			}
			target.close();
		}

		@Override
		public void onDialogClickoutside(RootPortletDialog dialog) {
			dialog.close();
		}

		@Override
		public void onDialogVisible(RootPortletDialog rootPortletDialog, boolean b) {
		}

		@Override
		public void onDialogMoved(RootPortletDialog rootPortletDialog) {
		}

		@Override
		public void onDialogClosed(RootPortletDialog rootPortletDialog) {

		}

		@Override
		public void onUserCloseDialog(RootPortletDialog rootPortletDialog) {

		}

	}

	static public WebMenu createGlobalVariablesMenu(String menuName, String prefix, AmiWebService service, AmiWebDomObject obj) {
		WebMenu variables = new BasicWebMenu(menuName, true);
		addConstVars(service, prefix, variables, obj);
		return variables;
	}

	//tab entry, tab,tree column, tree groupings 
	static public WebMenu createVariablesMenu(String menuName, String prefix, com.f1.base.CalcTypes vars) {
		WebMenu variables = new BasicWebMenu(menuName, true);
		boolean hasSpecial = false;
		for (String i : CH.sort(vars.getVarKeys())) {
			if (AmiWebUtils.isReservedVar(i))
				hasSpecial = true;
			else
				variables.add(new BasicWebMenuLink(i, true, "var_" + prefix + i).setAutoclose(false));
		}
		if (hasSpecial) {
			variables.add(new BasicWebMenuDivider());
			for (String i : CH.sort(vars.getVarKeys())) {
				if (AmiWebUtils.isReservedVar(i)) {
					String name = AmiWebUtils.RESERVED_PARAMS.get(i);
					variables.add(new BasicWebMenuLink(name == null ? i : i + " (" + name + ")", true, "var_" + prefix + i).setAutoclose(false));
				}
			}
		}
		return variables;
	}

	//mapbox settings,filter  settings, filter link 
	public static WebMenu createVariablesMenu(AmiWebService amiWebManager, String menuName, String prefix, AmiWebDmPortlet tablePortlet, String dmAliasDotName, String tableName) {
		WebMenu variables = new BasicWebMenu(menuName, true);
		variables.add(new BasicWebMenuDivider());
		List<String> sortedUnique = CH.uniqInplace(CH.sort(AmiWebUtils.getAvailableVariables(amiWebManager, tablePortlet, dmAliasDotName, tableName).getVarKeys()));
		for (String name : sortedUnique) {
			variables.add(new BasicWebMenuLink(name, true, "var_" + prefix + name).setAutoclose(false).setCssStyle("_fm=courier"));
		}
		return variables;
	}

}
