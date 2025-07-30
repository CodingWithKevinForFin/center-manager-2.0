package com.f1.ami.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.functions.AmiWebFunctionFactory;
import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.portal.BasicPortletDownload;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletDownload;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet.Callback;
import com.f1.suite.web.portal.impl.HtmlPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.style.PortletStyleManager_Form;
import com.f1.suite.web.tree.WebTreeContextMenuListener;
import com.f1.suite.web.tree.WebTreeManager;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.RH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.sql.TablesetImpl;
import com.f1.utils.sql.aggs.AggregateFactory;
import com.f1.utils.sql.preps.AggregatePrepareFactory;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.table.derived.BasicMethodFactory;
import com.f1.utils.structs.table.derived.DeclaredMethodFactory;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorConst;
import com.f1.utils.structs.table.derived.DerivedCellMemberMethod;
import com.f1.utils.structs.table.derived.MemberMethodDerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodDerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodExample;
import com.f1.utils.structs.table.derived.MethodFactory;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.EmptyCalcFrameStack;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;
import com.f1.utils.tar.TarEntry;
import com.f1.utils.tar.TarOutputStream;

public class AmiWebDocumentationPortlet extends GridPortlet implements WebTreeContextMenuListener, FormPortletListener, AmiWebSpecialPortlet, HtmlPortletListener {
	final private static String h1Style = " style='font:bold 24pt Arial;color:#4181BD;margin:5pt 0pt 15pt 0pt;'";
	final private static String h2Style = " style='font:bold 19pt Arial;color:#7030a0;lowercase;margin:5pt 0pt 1pt 0pt;'";
	final private static String h3Style = " style='font:italic bold 13pt Arial;text-transform:uppercase;color:#459939;margin:15pt 0pt 3pt 0pt;'";
	final private static String hrStyle = " style='box-sizing:border-box;margin:0;height:2px;border:1.5px solid #7030a0;background:#7030a0;'";
	final private static String bodyStyle = " style='font:12pt Arial;color:black;linewheight:1;user-select:text'";
	final private static String codeStyle = " style='position:relative;font-size:12px;font-family:monospace;white-space: pre;user-select: text;background-color:#F5F5F5;padding:8px;tab-size:2'";
	final private static String s2Style = " style='position:initial;padding-bottom:11pt;'"; // space after section
	private static final Logger log = LH.get();

	final private AmiWebService service;
	final private DividerPortlet dividerPortlet;
	final private FastTreePortlet treePortlet;
	final private HtmlPortlet htmlPortlet;
	final private FormPortlet navPanel;
	final private WebTreeManager treeManager;
	final private BasicMultiMap.List<ParamsDefinition, WebTreeNode> nodesByName = new BasicMultiMap.List<ParamsDefinition, WebTreeNode>(
			new HasherMap<ParamsDefinition, List<WebTreeNode>>(ParamsDefinition.HASHER_DEF));
	final private Map<Class, WebTreeNode> classObjectsToNodes;
	final private AmiWebScriptManagerForLayout scriptManager;
	final private BasicMethodFactory methodFactory;
	final private FormPortletButtonField backButton;
	final private FormPortletButtonField nextButton;
	final private List<WebTreeNode> history = new ArrayList<WebTreeNode>();
	private int historyPos = -1;

	public AmiWebDocumentationPortlet(PortletConfig config, AmiWebService service) {
		super(config);
		this.service = service;
		this.scriptManager = service.getScriptManager("");
		this.methodFactory = this.scriptManager.getMethodFactory();
		this.dividerPortlet = this.addChild(new DividerPortlet(generateConfig(), true), 0, 0);
		int rowSize = 30;
		if (service.isDebug()) {
			FormPortlet t = new FormPortlet(generateConfig());
			this.addChild(t, 0, 1);
			t.addButton(new FormPortletButton("Export").setCorrelationData("EXPORT"));
			t.addFormPortletListener(this);
			rowSize = 40;
		}
		this.setRowSize(1, rowSize);
		this.setSuggestedSize(1000, 600);
		this.treePortlet = new FastTreePortlet(generateConfig());
		this.htmlPortlet = new HtmlPortlet(generateConfig());
		this.htmlPortlet.addListener(this);
		this.navPanel = new FormPortlet(generateConfig());
		this.navPanel.addFormPortletListener(this);
		this.navPanel.setStyle(new PortletStyleManager_Form());
		GridPortlet gp = new GridPortlet(generateConfig());
		gp.addChild(this.navPanel, 0, 0);
		gp.addChild(this.htmlPortlet, 0, 1);
		gp.setRowSize(0, 20);
		this.navPanel.addField(this.backButton = new FormPortletButtonField("").setValue("<<")).setLeftTopWidthHeightPx(2, 2, 30, 16);
		this.navPanel.addField(this.nextButton = new FormPortletButtonField("").setValue(">>")).setLeftTopWidthHeightPx(34, 2, 30, 16);
		setHistoryPosition(-1);
		this.dividerPortlet.addChild(this.treePortlet);
		this.dividerPortlet.addChild(gp);
		this.dividerPortlet.setOffsetFromTopPx(240);
		this.treeManager = this.treePortlet.getTreeManager();
		this.treeManager.getRoot().setIconCssStyle("_bgi=url(" + AmiWebConsts.ICON_FILES + ")");
		this.treeManager.getRoot().setName("AMI Documentation");
		WebTreeNode functionsNode = this.treeManager.createNode("AMI-Script Functions", this.treeManager.getRoot(), false)
				.setIconCssStyle("_bgi=url(" + AmiWebConsts.ICON_FOLDER + ")");
		WebTreeNode classTypesNode = this.treeManager.createNode("AMI-Script Class Types", this.treeManager.getRoot(), false)
				.setIconCssStyle("_bgi=url(" + AmiWebConsts.ICON_FOLDER + ")");
		WebTreeNode aggsNode = this.treeManager.createNode("SQL Aggregate Functions", this.treeManager.getRoot(), false)
				.setIconCssStyle("_bgi=url(" + AmiWebConsts.ICON_FOLDER + ")");
		WebTreeNode prepsNode = this.treeManager.createNode("SQL Prepare Functions ", this.treeManager.getRoot(), false)
				.setIconCssStyle("_bgi=url(" + AmiWebConsts.ICON_FOLDER + ")");
		List<MethodFactory> sink = new ArrayList<MethodFactory>();
		methodFactory.getAllMethodFactories(sink);
		for (MethodFactory s : sink) {
			if (s instanceof DeclaredMethodFactory)
				continue;
			WebTreeNode node = this.treeManager.createNode(s.getDefinition().getMethodName(), functionsNode, false).setIconCssStyle("_bgi=url(" + AmiWebConsts.ICON_METHOD + ")")
					.setCssClass("clickable").setData(s);
			this.nodesByName.putMulti(s.getDefinition(), node);
		}
		for (MethodFactory s : AggregateFactory.AGG_METHOD_FACTORIES.values()) {
			WebTreeNode node = this.treeManager.createNode(s.getDefinition().getMethodName(), aggsNode, false).setIconCssStyle("_bgi=url(" + AmiWebConsts.ICON_METHOD + ")")
					.setCssClass("clickable").setData(s);
			this.nodesByName.putMulti(s.getDefinition(), node);
		}
		for (MethodFactory s : AggregatePrepareFactory.PREP_METHOD_FACTORIES.values()) {
			WebTreeNode node = this.treeManager.createNode(s.getDefinition().getMethodName(), prepsNode, false).setIconCssStyle("_bgi=url(" + AmiWebConsts.ICON_METHOD + ")")
					.setCssClass("clickable").setData(s);
			this.nodesByName.putMulti(s.getDefinition(), node);
		}

		List<DerivedCellMemberMethod<Object>> methodsAll = new ArrayList<DerivedCellMemberMethod<Object>>();
		methodFactory.getMemberMethods(null, null, methodsAll);
		this.classObjectsToNodes = new HashMap<Class, WebTreeNode>();
		Set<Class<?>> sink2 = new HashSet<Class<?>>();
		methodFactory.getTypes(sink2);
		for (DerivedCellMemberMethod<Object> method : methodsAll)
			sink2.add(method.getTargetType());
		for (Class classObject : sink2) {
			classObjectsToNodes.put(classObject, this.treeManager.createNode(scriptManager.forType(classObject), classTypesNode, false)
					.setIconCssStyle("_bgi=url(" + AmiWebConsts.ICON_CLASS + ")").setCssClass("clickable").setData(classObject));
		}
		ArrayList<DerivedCellMemberMethod<Object>> methodsSink;
		for (Class<?> classObject : classObjectsToNodes.keySet()) {
			List<ParamsDefinition> callbacks = service.getScriptManager().getCallbackDefinitions(classObject);
			for (ParamsDefinition i : callbacks) {
				WebTreeNode node = this.treeManager.createNode("(Callback) " + i.getMethodName(), classObjectsToNodes.get(classObject), false)
						.setIconCssStyle("_bgi=url(" + AmiWebConsts.ICON_CALLBACK + ")").setCssClass("clickable").setData(i);
				this.nodesByName.putMulti(i, node);
			}
			methodsSink = new ArrayList<DerivedCellMemberMethod<Object>>();
			methodFactory.getMemberMethods(classObject, null, methodsSink);
			Collections.sort(methodsSink, new Comparator<DerivedCellMemberMethod>() {

				@Override
				public int compare(DerivedCellMemberMethod o1, DerivedCellMemberMethod o2) {
					return OH.compare(o1.getMethodName(), o2.getMethodName());
				}
			});
			for (DerivedCellMemberMethod<Object> method : methodsSink) {
				WebTreeNode node = this.treeManager.createNode(OH.noNull(method.getMethodName(), "&lt;Constructor&gt;"), classObjectsToNodes.get(classObject), false)
						.setIconCssStyle("_bgi=url(" + AmiWebConsts.ICON_METHOD + ")").setCssClass("clickable").setData(method);
				this.nodesByName.putMulti(method.getParamsDefinition(), node);
			}
		}

		this.treePortlet.getTree().addMenuContextListener(this);
		this.treePortlet.addOption(FastTreePortlet.OPTION_SEARCH_BUTTONS_COLOR, "#007608");
		this.treePortlet.addOption(FastTreePortlet.OPTION_GRIP_COLOR, "_bg=#ffffff");
		this.treePortlet.addOption(FastTreePortlet.OPTION_SCROLL_BUTTON_COLOR, "_bg=#ffffff");
		this.treePortlet.addOption(FastTreePortlet.OPTION_SCROLL_ICONS_COLOR, "#007608");
	}
	@Override
	public void onContextMenu(FastWebTree tree, String action) {
	}

	@Override
	public void onNodeClicked(FastWebTree tree, WebTreeNode node) {
		selectNode(node, true);
	}
	private boolean selectNode(WebTreeNode node, boolean appendToHistory) {
		if (node == null)
			return false;
		if (!history.isEmpty() && history.get(this.historyPos) == node)
			return false;
		Object data = node.getData();

		for (WebTreeNode i : this.treePortlet.getTree().getSelected())
			i.setSelected(false);
		if (node.isFiltered() && node.getParent() != null) {
			node.getParent().setChildFilterered(node, false);
		}
		node.setSelected(true);

		StringBuilder sb = new StringBuilder();
		if (data instanceof MethodFactory) {
			MethodFactory data2 = (MethodFactory) data;
			buildDocumentation(data2, data2.getDefinition(), sb);
		} else if (data instanceof Class) {
			buildDocumentation((Class) data, sb);
		} else if (data instanceof AmiAbstractMemberMethod) {
			WebTreeNode parent = node.getParent();
			Class parentClass = (Class) parent.getData();
			buildDocumentation((AmiAbstractMemberMethod) data, sb, parentClass);
		} else if (data instanceof ParamsDefinition) {
			buildDocumentation(null, (ParamsDefinition) data, sb);
		} else {
			node.setIsExpanded(true);
			return false;
		}
		this.htmlPortlet.setHtml(sb.toString());
		if (appendToHistory) {
			while (history.size() > historyPos + 1)
				history.remove(history.size() - 1);
			history.add(node);
			setHistoryPosition(this.historyPos + 1);
		}
		return true;
	}
	private void setHistoryPosition(int i) {
		this.historyPos = i;
		this.backButton.setDisabled(this.historyPos <= 0);
		this.nextButton.setDisabled(this.historyPos >= this.history.size() - 1);
	}

	private void buildDocumentation(AmiAbstractMemberMethod data, StringBuilder sb, Class parentClass) {
		sb.append("<div").append(bodyStyle).append(">");

		sb.append("<div").append(s2Style).append(">");
		sb.append("<h3").append(h3Style).append(">Definition</h3><B>");
		if (data.getMethodName() != null) {
			addClassLink(sb, data.getReturnType());
			sb.append(' ');
			addClassLink(sb, data.getTargetType());
			sb.append("::").append(data.getMethodName());
		} else {
			addClassLink(sb, data.getTargetType());
			sb.append(' ');
			sb.append(scriptManager.forType(data.getTargetType()).toLowerCase());
			sb.append(" = new ");
			addClassLink(sb, data.getTargetType());
		}
		sb.append("(");
		Class[] paramTypes = data.getParamTypes();
		String[] paramNames = data.getParamNames();
		for (int i = 0; i < paramNames.length; i++) {
			if (i > 0)
				sb.append(", ");
			if (i == paramTypes.length) {
				addClassLink(sb, data.getVarArgType());
				sb.append(" ... ");
			} else {
				addClassLink(sb, paramTypes[i]);
				sb.append(' ');
			}
			sb.append(data.getParamNames()[i]);
		}
		sb.append(")</B>");
		sb.append("</div>");

		String description = data.getDescription();
		if (SH.is(description)) {
			sb.append("<div").append(s2Style).append(">");
			sb.append("<h3").append(h3Style).append(">Description</h3>");
			sb.append(description);
			sb.append("</div>");
		}

		sb.append("<div").append(s2Style).append(">");
		if (paramNames.length > 0) {
			sb.append("<h3").append(h3Style).append(">Parameter Definition</h3>");
			sb.append("<table style='border-collapse:collapse;'>");
			for (int i = 0; i < paramNames.length; i++) {
				sb.append("<tr style='min-height:11pt;'><td style='border:1pt solid grey'>");

				if (i == paramTypes.length) {
					sb.append("<B>");
					addClassLink(sb, data.getVarArgType());
					sb.append("&nbsp;...&nbsp;");
				} else {
					sb.append("<B>");
					addClassLink(sb, paramTypes[i]);
					sb.append("&nbsp;");
				}
				sb.append(paramNames[i]).append("</B>");
				sb.append("<td style='border:1px solid grey;min-width:100px;'>");
				String des = data.getParamDescriptions()[i];
				if (SH.is(des) && OH.ne(des, paramNames[i]))
					sb.append("<i>").append(des).append("</i>");
				sb.append("</tr>");
			}
		}

		sb.append("</table>");
		sb.append("</div>");

		MethodExample[] examples = data.getExamples();
		if (examples.length > 0) {
			sb.append("<div").append(s2Style).append(">");
			for (int i = 0; i < examples.length; i++) {
				if ((!SH.equals(examples[i].getOwningClass(), scriptManager.forType(parentClass))) && SH.is(examples[i].getOwningClass()))
					continue;
				sb.append("<div").append(s2Style).append(">");
				sb.append("<h3").append(h3Style).append(">Example ").append(i + 1).append("</h3>");
				sb.append("<div").append(codeStyle).append(">");
				sb.append(examples[i].getScript().replaceAll("\n", "<br>"));
				if (!SH.endsWith(sb, ("<br>")))
					sb.append("<br>");
				String[] returns = examples[i].getReturns();
				String[] evaluableScripts = examples[i].getEvaluableScripts();
				for (int j = 0; j < returns.length; j++) {
					Object result = scriptManager.toCalc(evaluableScripts[j], EmptyCalcTypes.INSTANCE, null, null).get(EmptyCalcFrameStack.INSTANCE);
					String evaluation = result == null ? "null" : result.toString();
					sb.append("// ").append(returns[j]).append(" = ").append(evaluation).append("<br>");
				}
				sb.append("</div>");
				String exampleDescription = examples[i].getDescription();
				if (SH.isntEmpty(exampleDescription)) {
					sb.append("<p>").append(exampleDescription).append("</p>");
				}
				sb.append("</div>");
			}
			sb.append("</div>");
		}

		sb.append("</div>");
	}

	private void buildDocumentation(Class data, StringBuilder sb) {
		Set<Class<?>> allTypes = new LinkedHashSet<Class<?>>();
		methodFactory.getTypes(allTypes);
		sb.append("<div").append(bodyStyle).append(">");

		sb.append("<div").append(s2Style).append(">");
		sb.append("<h3").append(h3Style).append(">Definition</h3><B>");
		String type = scriptManager.forType(data);
		sb.append(type).append("</B> var = .... ;<BR> ");
		{
			sb.append("<h3").append(h3Style).append(">Extends</h3><B>");
			LinkedHashSet<Class<?>> classes = new LinkedHashSet<Class<?>>();
			RH.getImplementedClassesAndInterfaces(data, classes);
			classes.add(Object.class);
			for (Class<?> i : classes) {
				if (allTypes.contains(i)) {
					addClassLink(sb, i);
					sb.append("<BR>");
				}
			}
		}
		{
			TreeSet<String> names = new TreeSet<String>();
			sb.append("<h3").append(h3Style).append(">Extended By</h3><B>");
			for (Class<?> i : allTypes) {
				if (data != i && OH.isAssignableFrom(data, i)) {
					names.add(scriptManager.forType(i));
				}
			}
			for (String t : names) {
				addClassLink(sb, t);
				sb.append("<BR>");
			}
		}
		sb.append("</div>");

		String description = methodFactory.getVarTypeDescription(type);
		if (SH.is(description)) {
			sb.append("<div").append(s2Style).append(">");
			sb.append("</B><h3").append(h3Style).append(">Description</h3>");
			sb.append(description);
			sb.append("</div>");
		}

		List<ParamsDefinition> callbacks = service.getScriptManager().getCallbackDefinitions(data);
		if (CH.isntEmpty(callbacks)) {
			sb.append("<div").append(s2Style).append(">");
			sb.append("<h3").append(h3Style).append(">Custom Callbacks</h3>");
			sb.append("<table style='border-collapse:collapse'>");
			sb.append("<tr style='min-height:11pt;'>");
			sb.append("<td style='border:1px solid grey'><B>Returns&nbsp;");
			sb.append("<td style='border:1px solid grey'><B>Name&nbsp;");
			sb.append("<td style='border:1px solid grey'><B>Description&nbsp;");
			for (ParamsDefinition i : callbacks) {
				sb.append("<tr style='min-height:11pt;'><td style='border:1px solid grey'>&nbsp;");
				addClassLink(sb, i.getReturnType());
				sb.append("&nbsp;<td style='border:1px solid grey'>&nbsp;");
				String cb = this.htmlPortlet.generateCallback(new Callback("member_callback").addAttribute("m", i).addAttribute("c", data));
				sb.append("<A href='#' onclick=").append(cb).append(">");
				sb.append(OH.noNull(i.getMethodName(), "&lt;Constructor&gt;"));
				sb.append("</a>&nbsp;<td style='border:1px solid grey;min-width:100px'>");
				sb.append(i.getDescriptionHtml());
			}
			sb.append("</tr>");
			sb.append("</table>");
		}

		sb.append("<div").append(s2Style).append(">");
		sb.append("<h3").append(h3Style).append(">Method Definitions</h3>");
		sb.append("<table style='border-collapse:collapse'>");
		sb.append("<tr style='min-height:11pt;'>");
		sb.append("<td style='border:1px solid grey'><B>Returns&nbsp;");
		sb.append("<td style='border:1px solid grey'><B>Name&nbsp;");
		sb.append("<td style='border:1px solid grey'><B>Description&nbsp;");
		List<DerivedCellMemberMethod<Object>> sink = new ArrayList<DerivedCellMemberMethod<Object>>();
		methodFactory.getMemberMethods(data, null, sink);
		Collections.sort(sink, new Comparator<DerivedCellMemberMethod>() {

			@Override
			public int compare(DerivedCellMemberMethod o1, DerivedCellMemberMethod o2) {
				return OH.compare(o1.getMethodName(), o2.getMethodName());
			}
		});
		for (DerivedCellMemberMethod<Object> i : sink) {
			sb.append("<tr style='min-height:11pt;'><td style='border:1px solid grey'>&nbsp;");
			addClassLink(sb, i.getReturnType());
			sb.append("&nbsp;<td style='border:1px solid grey;min-width:100px'>&nbsp;");
			String cb = this.htmlPortlet.generateCallback(new Callback("member_method").addAttribute("m", i).addAttribute("c", data));
			sb.append("<A href='#' onclick=").append(cb).append(">");
			sb.append(OH.noNull(i.getMethodName(), "&lt;Constructor&gt;"));
			sb.append("</a>&nbsp;");
			if (i instanceof AmiAbstractMemberMethod) {
				sb.append("<td style='border:1px solid grey;min-width:100px'>");
				AmiAbstractMemberMethod i2 = (AmiAbstractMemberMethod) i;
				sb.append(i2.getDescription());
			}
			sb.append("</tr>");
		}
		sb.append("</table>");
		sb.append("</div>");

		sb.append("</div>");
	}
	private void addClassLink(StringBuilder sb, Class clazz) {
		addClassLink(sb, scriptManager.forType(clazz));
	}
	private void addClassLink(StringBuilder sb, String t) {
		String cb = this.htmlPortlet.generateCallback(new Callback("class").addAttribute("cn", t));
		sb.append("<A href='#' onclick=").append(cb).append(">").append(t).append("</a>");
	}
	private void buildDocumentation(MethodFactory function, ParamsDefinition def, StringBuilder sb) {
		sb.append("<div").append(bodyStyle).append(">");
		sb.append("<div").append(s2Style).append(">");
		sb.append("<h3").append(h3Style).append(">Definition</h3><B>");
		addClassLink(sb, def.getReturnType());
		sb.append(' ');
		sb.append(AmiWebUtils.getFunctionDescription(methodFactory, def));
		sb.append("</div>");

		String description = def.getDescriptionHtml();
		if (SH.is(description)) {
			sb.append("<div").append(s2Style).append(">");
			sb.append("</B><h3").append(h3Style).append(">Description</h3>");
			sb.append(description);
			sb.append("</div>");
		}

		sb.append("<div").append(s2Style).append(">");
		int paramsCount = def.getParamsCount();
		if (paramsCount > 0) {
			sb.append("<h3").append(h3Style).append(">Parameter Definition</h3>");
			sb.append("<table style='border-collapse:collapse'>");
			for (int i = 0; i < paramsCount; i++) {
				sb.append("<tr style='min-height:11pt;'><td style='border:1px solid grey'>");
				addClassLink(sb, def.getParamType(i));
				if (def.isVarArg() && i == paramsCount - 1)
					sb.append("...");
				sb.append("&nbsp;");
				sb.append(def.getParamName(i)).append("</B>");
				sb.append("<td style='border:1px solid grey;min-width:100px'>");
				String des = def.getParamDescriptionHtml(i);
				if (SH.is(des))
					sb.append("<i>").append(des).append("</i>");
				sb.append("</tr>");
			}
		}

		sb.append("</table>");
		sb.append("</div>");

		if (function != null) {
			// Build simple examples
			Object[][] ex = def.getExamples();
			if (ex.length > 0) {
				sb.append("<div").append(s2Style).append(">");
				sb.append("<h3").append(h3Style).append(">Examples</h3>");
				sb.append("<div").append(codeStyle).append(">");
				int i = 0;
				for (Object o[] : ex) {
					i++;
					MethodDerivedCellCalculator func = (MethodDerivedCellCalculator) function.toMethod(0, def.getMethodName(), toConsts(o, def), EmptyCalcFrameStack.INSTANCE);
					Object value = func.get(EmptyCalcFrameStack.INSTANCE);
					addClassLink(sb, value == null ? Object.class : value.getClass());
					sb.append(" r").append(i).append(" = ");
					toString(func, sb);
					sb.append("; <i>// r").append(i).append(" == ").append(value).append("</i><BR>");

				}
				sb.append("</div>");
				sb.append("</div>");
			}

			// Build advanced examples
			MethodExample[] examples = def.getAdvancedExamples();
			if (examples.length > 0) {
				sb.append("<div").append(s2Style).append(">");
				for (int i = 0; i < examples.length; i++) {
					sb.append("<div").append(s2Style).append(">");
					sb.append("<h3").append(h3Style).append(">Example ").append(i + 1).append("</h3>");
					sb.append("<div").append(codeStyle).append(">");
					sb.append(examples[i].getScript().replaceAll("\n", "<br>"));
					if (!SH.endsWith(sb, ("<br>")))
						sb.append("<br>");
					String[] returns = examples[i].getReturns();
					String[] evaluableScripts = examples[i].getEvaluableScripts();
					for (int j = 0; j < returns.length; j++) {
						sb.append("<br>");
						Object result = scriptManager.toCalc(evaluableScripts[j], EmptyCalcTypes.INSTANCE, null, null)
								.get(new TopCalcFrameStack(new TablesetImpl(), EmptyCalcFrame.INSTANCE));
						String evaluation = result == null ? "null" : result.toString();
						sb.append("// ").append(returns[j]).append(" = ");
						if (evaluation.contains("\n"))
							sb.append("<br>// ");
						if (evaluation.endsWith("\n"))
							evaluation = evaluation.substring(0, evaluation.length() - 1);
						sb.append(evaluation.replaceAll("\n", "<br>// ")).append("<br>");
					}
					sb.append("</div>");
					String exampleDescription = examples[i].getDescription();
					if (SH.isntEmpty(exampleDescription)) {
						sb.append("<p>").append(exampleDescription).append("</p>");
					}
					sb.append("</div>");
				}
				sb.append("</div>");
			}
			sb.append("</div>");
		}

	}
	private StringBuilder toString(MethodDerivedCellCalculator func, StringBuilder sink) {
		sink.append(func.getMethodName());
		//		DerivedCellCalculator[] params = func.getPa();
		int paramsCount = func.getParamsCount();
		if (paramsCount == 0)
			return sink.append("()");
		for (int i = 0; i < paramsCount; i++) {
			sink.append(i == 0 ? '(' : ',');
			Object value = func.getParamAt(i).get(null);
			if (value instanceof List) {
				sink.append("new List(");
				List t = (List) value;
				for (int n = 0; n < t.size(); n++) {
					if (n > 0)
						sink.append(", ");
					AmiUtils.toConstString(t.get(n), sink);
				}
				sink.append(")");
			} else if (value instanceof Set) {
				sink.append("new Set(");
				Set t = (Set) value;
				boolean first = true;
				for (Object v : t) {
					if (first)
						first = false;
					else
						sink.append(", ");
					AmiUtils.toConstString(v, sink);
				}
				sink.append(")");
			} else if (value instanceof Map) {
				sink.append("new Map(");
				Map<Object, Object> t = (Map) value;
				boolean first = true;
				for (Map.Entry<Object, Object> v : t.entrySet()) {
					if (first)
						first = false;
					else
						sink.append(", ");
					AmiUtils.toConstString(v.getKey(), sink);
					sink.append(", ");
					AmiUtils.toConstString(v.getValue(), sink);
				}
				sink.append(")");
			} else
				func.getParamAt(i).toString(sink);
		}
		return sink.append(')');
	}
	public DerivedCellCalculator[] toConsts(Object[] o, ParamsDefinition def) {
		DerivedCellCalculator[] r = new DerivedCellCalculator[o.length];
		for (int i = 0; i < r.length; i++) {

			Class<?> paramType = def.getParamType(Math.min(i, def.getParamsCount() - 1));
			r[i] = new DerivedCellCalculatorConst(0, paramType.cast(o[i]),
					o[i] == null && (i < def.getParamsCount() - 1 || !def.isVarArg()) ? paramType : (o[i] == null ? paramType : o[i].getClass()));
		}
		return r;
	}
	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node) {

	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TarOutputStream tar = new TarOutputStream(out);
		PrintStream tarPrintStream = new PrintStream(tar);
		if ("EXPORT".equals(button.getCorrelationData())) {
			List<MethodFactory> sink = new ArrayList<MethodFactory>();
			methodFactory.getAllMethodFactories(sink);
			StringBuilder sb = new StringBuilder();
			for (MethodFactory s : sink) {
				if (s instanceof DeclaredMethodFactory)
					continue;
				ParamsDefinition def = s.getDefinition();
				SH.clear(sb).append(def.getMethodName()).append("(");
				for (int i = 0; i < def.getParamsCount(); i++) {
					if (i != 0)
						sb.append(",");
					sb.append(def.getParamName(i));
				}
				sb.append(')');
				String fileName = SH.toStringAndClear(sb);
				buildDocumentation((AmiWebFunctionFactory) s, s.getDefinition(), sb);
				try {
					TarEntry entry = new TarEntry("functions/" + fileName + ".html", false);
					tar.putNextEntry(entry);
					tarPrintStream.print("<html>" + sb + "</html>");
					tarPrintStream.flush();
					tar.closeEntry();
				} catch (IOException e) {
					tarPrintStream.close();
					LH.warning(log, "Unexpected error: ", e);
				}
			}

			List<DerivedCellMemberMethod<Object>> methodsAll = new ArrayList<DerivedCellMemberMethod<Object>>();
			methodFactory.getMemberMethods(null, null, methodsAll);
			BasicMultiMap.List<Class, DerivedCellMemberMethod<?>> map = new BasicMultiMap.List<Class, DerivedCellMemberMethod<?>>();
			for (DerivedCellMemberMethod<Object> method : methodsAll)
				map.putMulti(method.getTargetType(), method);

			StringBuilder sb2 = new StringBuilder();
			for (Entry<Class, List<DerivedCellMemberMethod<?>>> classObject : map.entrySet()) {
				SH.clear(sb);
				String fileName = scriptManager.forType(classObject.getKey());
				List<DerivedCellMemberMethod<?>> methodsSink = classObject.getValue();

				sb.append("<div").append(s2Style).append(">");
				sb.append("<h1").append(h1Style).append(">");
				appendBookmark(sb, fileName + " Class Overview");
				sb.append("</h1>");
				buildDocumentation(classObject.getKey(), sb);
				sb.append("</div>");

				for (DerivedCellMemberMethod<?> method : methodsSink) {
					sb.append("<div").append(s2Style).append(">");

					SH.clear(sb2).append(OH.noNull(method.getMethodName(), "constructor")).append("(");
					String[] t = method.getParamNames();
					for (int i = 0; i < t.length; i++) {
						if (i != 0)
							sb2.append(",");
						sb2.append(t[i]);
					}
					sb2.append(')');
					sb.append("<h2").append(h2Style).append(">");
					appendBookmark(sb, sb2.toString());
					sb.append("</h2><hr").append(hrStyle).append(">");

					buildDocumentation((AmiAbstractMemberMethod) method, sb, classObject.getKey());
					sb.append("</div>");
				}
				try {
					TarEntry entry = new TarEntry("classes/" + fileName + ".html", false);
					tar.putNextEntry(entry);
					tarPrintStream.print("<html>" + sb + "</html>");
					tarPrintStream.flush();
					tar.closeEntry();
				} catch (IOException e) {
					tarPrintStream.close();
					LH.warning(log, "Unexpected error: ", e);
				}
			}
			PortletDownload download = new BasicPortletDownload("robohelp.tar", out.toByteArray());
			getManager().pushPendingDownload(download);
			tarPrintStream.close();
		} else
			close();
	}
	private void appendBookmark(StringBuilder sb, String bm) {
		sb.append("\n<a name=\"" + bm + "\" id=\"" + bm + "\"><span>" + bm + "</span></a>\n");
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {

		if (field == this.backButton) {
			if (this.historyPos > 0) {
				WebTreeNode node = this.history.get(this.historyPos - 1);
				selectNode(node, false);
				setHistoryPosition(this.historyPos - 1);
				node.ensureVisible();
			}
		} else if (field == this.nextButton) {
			if (this.historyPos < this.history.size() - 1) {
				WebTreeNode node = this.history.get(this.historyPos + 1);
				selectNode(node, false);
				setHistoryPosition(this.historyPos + 1);
				node.ensureVisible();
			}
		}
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		System.out.println("This key (code) is pressed: " + keycode);
	}

	@Override
	public void onCellMousedown(FastWebTree tree, WebTreeNode start, FastWebTreeColumn col) {
	}
	public void setActiveMemberMethod(Class<?> returnType, ParamsDefinition definition) {
		List<WebTreeNode> candidates = this.nodesByName.get(definition);
		if (candidates != null) {
			for (WebTreeNode i : candidates) {
				Object o = i.getData();
				if (o instanceof DerivedCellMemberMethod) {
					Class type = (Class) i.getParent().getData();
					if (type.isAssignableFrom(returnType)) {
						//						i.setSelected(true);
						for (WebTreeNode p = i.getParent(); p != null; p = p.getParent())
							p.setIsExpanded(true);
						selectNode(i, true);
						i.ensureVisible();
						return;
					}

				}
			}
		}

	}
	public void setActiveMethod(ParamsDefinition definition) {
		List<WebTreeNode> candidates = this.nodesByName.get(definition);
		if (candidates != null) {
			for (WebTreeNode i : candidates) {
				Object o = i.getData();
				if (!(o instanceof MemberMethodDerivedCellCalculator)) {
					//					i.setSelected(true);
					for (WebTreeNode p = i.getParent(); p != null; p = p.getParent())
						p.setIsExpanded(true);
					selectNode(i, true);
					i.ensureVisible();
				}
			}
		}
	}
	public void setActiveCallback(Class c, ParamsDefinition definition) {
		List<WebTreeNode> candidates = this.nodesByName.get(definition);
		if (candidates != null) {
			for (WebTreeNode i : candidates) {
				Object o = i.getData();
				if (o instanceof ParamsDefinition && i.getParent().getData() == c) {
					//					i.setSelected(true);
					for (WebTreeNode p = i.getParent(); p != null; p = p.getParent())
						p.setIsExpanded(true);
					selectNode(i, true);
					i.ensureVisible();
				}
			}
		}
	}
	public void setActiveClass(Class<?> activeType) {
		WebTreeNode node = this.classObjectsToNodes.get(activeType);
		//		node.setSelected(true);
		for (WebTreeNode p = node; p != null; p = p.getParent()) {
			p.setIsExpanded(true);
		}
		if (node != null)
			selectNode(node, true);
		node.ensureVisible();
	}
	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
	}
	@Override
	public void onUserClick(HtmlPortlet portlet) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onUserCallback(HtmlPortlet htmlPortlet, String id, int mouseX, int mouseY, Callback cb) {
		if ("class".equals(cb.getId())) {
			String cn = (String) cb.getAttribute("cn");
			Class<?> cl = scriptManager.forName(cn);
			setActiveClass(cl);
		} else if ("member_method".equals(cb.getId())) {
			DerivedCellMemberMethod mm = (DerivedCellMemberMethod) cb.getAttribute("m");
			Class clazz = (Class) cb.getAttribute("c");
			setActiveMemberMethod(clazz, mm.getParamsDefinition());
		} else if ("member_callback".equals(cb.getId())) {
			ParamsDefinition mm = (ParamsDefinition) cb.getAttribute("m");
			Class c = (Class) cb.getAttribute("c");
			setActiveCallback(c, mm);
		}

	}
	@Override
	public void onHtmlChanged(String old, String nuw) {
		// TODO Auto-generated method stub

	}

}
