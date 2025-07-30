package com.f1.ami.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.ami.web.amiscript.AmiWebAmiScriptDerivedCellParser;
import com.f1.ami.web.amiscript.AmiWebAmiScriptDerivedCellParser.AmiWebDeclaredMethodFactory;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.tree.WebTreeContextMenuListener;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.sql.DerivedCellCalculatorSql;
import com.f1.utils.string.node.MethodNode;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.derived.DeclaredMethodFactory;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorMethod;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebMethodUsagesTreePortlet extends GridPortlet implements WebTreeContextMenuListener {

	private static final String CUSTOM_METHODS_SUFFIX = "!!CustomMethods";
	public static final String ICON_CALLBACK = "_bgi=url(" + AmiWebConsts.ICON_CALLBACK + ")";
	public static final String ICON_METHOD = "_bgi=url(" + AmiWebConsts.ICON_METHOD + ")";
	public static final String ICON_METHODS = "_bgi=url(" + AmiWebConsts.ICON_CUSTOM_METHODS + ")";

	private AmiWebService service;
	private FastTreePortlet tree;
	private int usagesCount;
	private String alias;
	private HtmlPortlet html;

	public AmiWebMethodUsagesTreePortlet(PortletConfig config, AmiWebService service, String alias) {
		super(config);
		this.service = service;
		this.tree = new FastTreePortlet(generateConfig());
		this.tree.addOption(FastTreePortlet.OPTION_SEARCH_BAR_HIDDEN, "true");
		this.tree.getTree().setRootLevelVisible(false);
		this.alias = alias;
		this.html = new HtmlPortlet(generateConfig());
		addChild(this.html, 0, 0);
		setRowSize(0, 20);
		addChild(this.tree, 0, 1);
		this.html.setHtml("Usages (" + this.usagesCount + ")");
		this.html.setCssStyle("_bg=#cccccc");
		this.tree.getTree().addMenuContextListener(this);
	}
	public void buildTree(List<ParamsDefinition> paramDefs) {
		this.tree.clear();
		this.usagesCount = 0;
		Set<ParamsDefinition> toFind = new HasherSet<ParamsDefinition>(ParamsDefinition.HASHER_DEF_IGNORE_RETURNTYPE);
		toFind.addAll(paramDefs);
		AmiWebDomObjectsManager dom = this.service.getDomObjectsManager();
		List<DeclaredMethodFactory> mf = this.service.getScriptManager().getLayout(alias).getDeclaredMethodFactories();
		AmiWebLayoutFile layout = this.service.getLayoutFilesManager().getLayoutByFullAlias(alias);
		//Custom Methods
		for (DeclaredMethodFactory dmf : mf) {
			AmiWebAmiScriptDerivedCellParser.AmiWebDeclaredMethodFactory t = (AmiWebDeclaredMethodFactory) dmf;
			List<DerivedCellCalculatorMethod> sink = new ArrayList<DerivedCellCalculatorMethod>();
			DerivedCellCalculator inner = t.getInner();
			DerivedHelper.find(inner, DerivedCellCalculatorMethod.class, sink);
			for (DerivedCellCalculatorMethod i : sink) {
				if (toFind.contains(i.getDefinition())) {
					WebTreeNode objectNode = getNode(layout);
					WebTreeNode cbNode = objectNode.getChildByKey(CUSTOM_METHODS_SUFFIX);
					if (cbNode == null) {
						cbNode = this.tree.createNode("Custom Methods", objectNode, true);
						cbNode.setKey(CUSTOM_METHODS_SUFFIX);
						cbNode.setIconCssStyle(ICON_METHODS);
					}
					WebTreeNode mtNode = cbNode.getChildByKey(t.getDefinition().toString());
					if (mtNode == null) {
						mtNode = this.tree.createNode(t.getDefinition().toString(), cbNode, true);
						mtNode.setKey(t.getDefinition().toString());
						mtNode.setIconCssStyle(ICON_METHOD);
					}
					Tuple2<Integer, Integer> pos = SH.getLinePosition(this.service.getScriptManager().getLayout(alias).getDeclaredMethodsScript(), i.getPosition());
					WebTreeNode node2 = this.tree.createNode(i.getDefinition() + " at " + (pos.getA() + 1) + ":" + (pos.getB() + 1), mtNode, true);
					node2.setCssClass("blue");
					node2.setData(new Tuple2<String, Integer>(alias + CUSTOM_METHODS_SUFFIX, i.getPosition()));
					this.usagesCount++;
				}
			}
		}
		BasicMultiMap.List<String, ParamsDefinition> names = new BasicMultiMap.List<String, ParamsDefinition>();
		for (ParamsDefinition i : paramDefs)
			names.putMulti(i.getMethodName(), i);

		for (AmiWebDomObject domobject : dom.getManagedDomObject()) {
			//AmiScript callbacks
			AmiWebAmiScriptCallbacks scripts = domobject.getAmiScriptCallbacks();
			if (scripts != null) {
				for (Entry<String, AmiWebAmiScriptCallback> e : scripts.getAmiScriptCallbackDefinitionsMap().entrySet()) {
					DerivedCellCalculator calc = e.getValue().getCalc(true);
					if (calc != null) {
						List<DerivedCellCalculatorMethod> sink = new ArrayList<DerivedCellCalculatorMethod>();
						DerivedHelper.find(calc, DerivedCellCalculatorMethod.class, sink);
						for (DerivedCellCalculatorMethod i : sink) {
							if (toFind.contains(i.getDefinition())) {
								WebTreeNode cbNode = getTreeNode(domobject, "Callback " + e.getValue().getName(), e.getValue().getAri());

								Tuple2<Integer, Integer> pos = SH.getLinePosition(e.getValue().getAmiscript(true), i.getPosition());
								WebTreeNode node2 = this.tree.createNode(i.getDefinition() + " at " + (pos.getA() + 1) + ":" + (pos.getB() + 1), cbNode, true);
								node2.setCssClass("blue");
								node2.setData(new Tuple2<String, Integer>(e.getValue().getAri(), i.getPosition()));
								this.usagesCount++;
							}
						}
						//Hacky look in SQL calls
						List<DerivedCellCalculatorSql> sink2 = new ArrayList<DerivedCellCalculatorSql>();
						DerivedHelper.find(calc, DerivedCellCalculatorSql.class, sink2);
						for (DerivedCellCalculatorSql i : sink2) {
							List<MethodNode> sink3 = new ArrayList<MethodNode>();
							DerivedHelper.find(i.getNode(), MethodNode.class, sink3);
							for (MethodNode mn : sink3) {
								List<ParamsDefinition> candidates = names.get(mn.getMethodName());
								if (candidates != null)
									for (ParamsDefinition pd : candidates) {
										if (OH.eq(pd.getMethodName(), mn.getMethodName()) && pd.getParamsCount() == mn.getParamsCount()) {
											WebTreeNode cbNode = getTreeNode(domobject, "Callback " + e.getValue().getName(), e.getValue().getAri());
											Tuple2<Integer, Integer> pos = SH.getLinePosition(e.getValue().getAmiscript(true), mn.getPosition());
											WebTreeNode node2;
											if (pd.getParamsCount() == 0)
												node2 = this.tree.createNode(pd.getMethodName() + "() at " + (pos.getA() + 1) + ":" + (pos.getB() + 1), cbNode, true);
											else
												node2 = this.tree.createNode("<i>" + pd.getMethodName() + "(?" + SH.repeat(",?", pd.getParamsCount() - 1) + ") at "
														+ (pos.getA() + 1) + ":" + (pos.getB() + 1), cbNode, true);
											node2.setCssClass("blue");
											node2.setData(new Tuple2<String, Integer>(e.getValue().getAri(), mn.getPosition()));
											this.usagesCount++;
											break;
										}

									}
							}
						}
					}

				}
			}
			//formulas
			AmiWebFormulas formulas = domobject.getFormulas();
			if (formulas != null && !formulas.getFormulaIds().isEmpty()) {
				for (String s : formulas.getFormulaIds()) {
					AmiWebFormula f = formulas.getFormula(s);
					DerivedCellCalculator calc = f.getFormulaCalc();
					if (calc != null) {
						List<DerivedCellCalculatorMethod> sink = new ArrayList<DerivedCellCalculatorMethod>();
						DerivedHelper.find(calc, DerivedCellCalculatorMethod.class, sink);
						for (DerivedCellCalculatorMethod i : sink) {
							if (toFind.contains(i.getDefinition())) {
								WebTreeNode cbNode = getTreeNode(domobject, "Formula " + f.getFormulaId(), f.getAri());
								Tuple2<Integer, Integer> pos = SH.getLinePosition(f.getFormula(true), i.getPosition());
								WebTreeNode node2 = this.tree.createNode(i.getDefinition() + " at " + (pos.getA() + 1) + ": " + (pos.getB() + 1), cbNode, true);
								node2.setCssClass("blue");
								node2.setData(new Tuple2<String, Integer>(f.getAri(), i.getPosition()));
								this.usagesCount++;
							}
						}
					}
				}
			}
		}
		this.html.setHtml("Usages (" + this.usagesCount + ")");
	}
	private WebTreeNode getTreeNode(AmiWebDomObject domobject, String ari, String name) {
		WebTreeNode objectNode = getNode(domobject);
		WebTreeNode cbNode = objectNode.getChildByKey(ari);
		if (cbNode == null) {
			cbNode = this.tree.createNode(name, objectNode, true);
			cbNode.setKey(ari);
			cbNode.setIconCssStyle(ICON_CALLBACK);
		}
		return cbNode;
	}

	private WebTreeNode getNode(AmiWebDomObject domobject) {
		if (domobject == null || domobject instanceof AmiWebService)
			return tree.getRoot();
		WebTreeNode t = getNode(domobject.getParentDomObject());
		WebTreeNode r = t.getChildByKey(domobject.getAri());
		if (r == null) {
			String icon = AmiWebAmiObjectsVariablesHelper.getAmiIconStyleForDomObjectType(domobject);
			r = tree.createNode(domobject.getAriType() + " " + domobject.getDomLabel(), t, true);
			r.setIconCssStyle(icon);
			r.setKey(domobject.getAri());
		}
		return r;
	}

	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
		List<WebTreeNode> sel = tree.getTree().getSelected();
		if (sel.size() == 1) {
			WebTreeNode row = sel.get(0);
			Object data = row.getData();
			if (data instanceof Tuple2) {
				Tuple2<String, Integer> tuple = (Tuple2<String, Integer>) data;
				String fullAri = tuple.getA();
				if (SH.endsWith(fullAri, CUSTOM_METHODS_SUFFIX)) {
					String alias = SH.stripSuffix(fullAri, CUSTOM_METHODS_SUFFIX, true);
					AmiWebMethodsPortlet mp = this.service.getDesktop().showCustomMethodsPortlet();
					mp.getTabs().bringToFront(mp.getMethodPortlet(alias).getPortletId());
					mp.getMethodPortlet(alias).setCursorPosition(tuple.getB());
				} else if (SH.indexOf(fullAri, '!', 0) != -1) {
					String ari = SH.beforeFirst(fullAri, "!");
					String cb = SH.afterFirst(fullAri, "!");
					AmiWebEditAmiScriptCallbackPortlet editor = this.service.getDomObjectsManager().showCallbackEditor(ari, cb);
					editor.setCursorPosition(tuple.getB());
				} else if (SH.indexOf(fullAri, '^', 0) != -1) {
					String ari = SH.beforeFirst(fullAri, "^");
					String cb = SH.afterFirst(fullAri, "^");
					this.service.getDomObjectsManager().showFormulaEditor(ari, cb);
				}
			}
		}
	}

	@Override
	public void onContextMenu(FastWebTree tree, String action) {
	}

	@Override
	public void onNodeClicked(FastWebTree tree, WebTreeNode node) {
	}

	@Override
	public void onCellMousedown(FastWebTree tree, WebTreeNode start, FastWebTreeColumn col) {
	}

	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node) {
	}

	public int getUsagesCount() {
		return this.usagesCount;
	}

}
