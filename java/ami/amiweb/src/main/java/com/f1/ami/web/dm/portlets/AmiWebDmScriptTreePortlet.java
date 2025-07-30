package com.f1.ami.web.dm.portlets;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.ami.web.AmiWebAmiObjectsVariablesHelper;
import com.f1.ami.web.AmiWebAmiScriptCallback;
import com.f1.ami.web.AmiWebAmiScriptCallbacks;
import com.f1.ami.web.AmiWebCompilerListener;
import com.f1.ami.web.AmiWebConsts;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebDomObjectDependency;
import com.f1.ami.web.AmiWebEditAmiScriptCallbackPortlet;
import com.f1.ami.web.AmiWebFormPortletAmiScriptField;
import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.AmiWebFormulaImpl;
import com.f1.ami.web.AmiWebFormulas;
import com.f1.ami.web.AmiWebMenuUtils;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebTabEntry;
import com.f1.ami.web.charts.AmiWebChartFormula;
import com.f1.ami.web.dm.portlets.AmiWebDmTreePortlet.DomObjectWrapper;
import com.f1.ami.web.graph.AmiWebGraphHelper;
import com.f1.ami.web.graph.AmiWebGraphNode;
import com.f1.ami.web.style.AmiWebStyledPortlet;
import com.f1.base.CalcTypes;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.DesktopPortlet;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.TreeStateCopier;
import com.f1.suite.web.portal.impl.TreeStateCopierIdGetter;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextEditField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.tree.WebTreeContextMenuListener;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.impl.IdentityHasher;
import com.f1.utils.sql.Tableset;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;

public class AmiWebDmScriptTreePortlet extends GridPortlet implements WebTreeContextMenuListener, FormPortletContextMenuFactory, FormPortletListener,
		FormPortletContextMenuListener, AmiWebCompilerListener, TreeStateCopierIdGetter, Comparator<WebTreeNode>, AmiWebDomObjectDependency {

	private static final Comparator<AmiWebDomObject> FORMULAS_COMPARATOR = new Comparator<AmiWebDomObject>() {

		@Override
		public int compare(AmiWebDomObject o1, AmiWebDomObject o2) {
			return OH.compare(o1.getAri(), o2.getAri());
		}
	};
	private static final Comparator<AmiWebFormula> FORMULA_COMPARATOR = new Comparator<AmiWebFormula>() {

		@Override
		public int compare(AmiWebFormula o1, AmiWebFormula o2) {
			return OH.compare(o1.getFormulaId(), o2.getFormulaId());
		}
	};
	private static final Comparator<AmiWebAmiScriptCallback> CALLBACK_COMPARATOR = new Comparator<AmiWebAmiScriptCallback>() {

		@Override
		public int compare(AmiWebAmiScriptCallback o1, AmiWebAmiScriptCallback o2) {
			return OH.compare(o1.getName(), o2.getName());
		}
	};
	private static final Comparator<AmiWebDomObject> DOM_COMPARATOR = new Comparator<AmiWebDomObject>() {

		@Override
		public int compare(AmiWebDomObject o1, AmiWebDomObject o2) {
			return OH.compare(o1.getAri(), o2.getAri());
		}
	};

	final private DividerPortlet divider;
	final private FastTreePortlet tree;
	final private FormPortlet form;
	final private AmiWebService service;
	final private IdentityHashSet<FormPortletTextField> editedFormulas = new IdentityHashSet<FormPortletTextField>();
	final private IdentityHashSet<AmiWebFormPortletAmiScriptField> editedCallbacks = new IdentityHashSet<AmiWebFormPortletAmiScriptField>();
	final private FormPortletButton applyButton;
	final private FormPortletButton cancelButton;
	private boolean changed;
	private List<AmiWebDomObject> selected;

	public AmiWebDmScriptTreePortlet(PortletConfig config, AmiWebService service) {
		super(config);
		this.service = service;
		this.divider = new DividerPortlet(generateConfig(), true);
		this.addChild(this.divider);
		this.tree = new FastTreePortlet(generateConfig());
		this.tree.getTree().setComparator(this);
		this.form = new FormPortlet(generateConfig());
		this.form.setMenuFactory(this);
		this.form.addMenuListener(this);
		this.form.addFormPortletListener(this);
		this.applyButton = new FormPortletButton("Apply");
		this.cancelButton = new FormPortletButton("Reset");
		this.applyButton.setEnabled(false);
		this.cancelButton.setEnabled(false);
		form.addButton(applyButton);
		form.addButton(cancelButton);
		this.divider.addChild(this.tree);
		this.divider.setOffsetFromTopPx(250);
		this.divider.addChild(this.form);
		this.tree.getTree().setRootLevelVisible(false);
		this.tree.getTree().addMenuContextListener(this);
		this.service.addCompilerListener(this);
		this.service.getDomObjectsManager().addGlobalListener(this);
	}

	@Override
	public void onClosed() {
		this.service.removeCompilerListener(this);
		this.service.getDomObjectsManager().removeGlobalListener(this);
		super.onClosed();
	}

	public void build(List<AmiWebDomObject> selected) {
		this.selected = CH.l(selected);
		rebuild();
	}
	private void rebuild() {
		final TreeStateCopier tsc = new TreeStateCopier(this.tree, this);
		this.tree.clear();
		for (AmiWebDomObject node : CH.sort(selected, DOM_COMPARATOR)) {
			String icon = AmiWebAmiObjectsVariablesHelper.getAmiIconStyleForDomObjectType(node);
			String label = node.getAri();
			//			if (SH.is(desc))
			//				label += " (" + desc + ")";
			WebTreeNode r = tree.createNode(label, tree.getRoot(), false, node);
			r.setIconCssStyle(icon);
			//			Object o = node.getInner();
			if (node instanceof AmiWebDomObject)
				visitDom(r, (AmiWebDomObject) node);
		}
		this.applyErrors(this.tree.getRoot());
		tsc.reapplyState();
	}
	@Override
	public void drainJavascript() {
		super.drainJavascript();
		if (changed) {
			changed = false;
			rebuild();
		}
	}
	private boolean applyErrors(WebTreeNode node) {
		boolean r = hasError(node);

		for (WebTreeNode i : node.getChildren()) {
			if (applyErrors(i))
				r = true;
		}
		String icon = SH.afterLast(node.getIconCssStyle(), '=');
		if (r) {
			node.setIconCssStyle("_bgi=url('" + AmiWebConsts.DM_TREE_ICON_ERROR + "')," + icon);
		} else {
			node.setIconCssStyle("_bgi=" + icon);
		}
		return r;
	}
	private boolean hasError(WebTreeNode node) {
		Object data = node.getData();
		AmiWebDomObject dom = null;
		if (data instanceof AmiWebGraphNode) {
			Object t = ((AmiWebGraphNode) data).getInner();
			if (t instanceof AmiWebDomObject)
				dom = (AmiWebDomObject) t;
		} else if (data instanceof AmiWebDomObject)
			dom = (AmiWebDomObject) data;
		else if (data instanceof AmiWebFormula)
			return ((AmiWebFormula) data).getFormulaError(false) != null;
		else if (data instanceof AmiWebAmiScriptCallback) {
			return ((AmiWebAmiScriptCallback) data).getError(false) != null;
		}
		return dom != null && hasError(dom);
	}

	private boolean hasError(AmiWebDomObject dom) {
		AmiWebAmiScriptCallbacks asc = dom.getAmiScriptCallbacks();
		if (asc != null) {
			for (AmiWebAmiScriptCallback i : asc.getAmiScriptCallbackDefinitionsMap().values())
				if (i.hasError(false))
					return true;
		}
		AmiWebFormulas formulas = dom.getFormulas();
		if (formulas != null) {
			for (String i : formulas.getFormulaIds())
				if (formulas.getFormula(i).getFormulaError(false) != null)
					return true;
		}
		List<AmiWebDomObject> children = dom.getChildDomObjects();
		if (CH.isntEmpty(children))
			for (AmiWebDomObject i : children)
				if (hasError(i))
					return true;

		return false;
	}

	private void visitDom(WebTreeNode r, AmiWebDomObject dom) {
		for (AmiWebDomObject i : dom.getChildDomObjects()) {
			byte t = AmiWebAmiObjectsVariablesHelper.parseType(i.getAriType());
			switch (t) {
				case AmiWebDomObject.ARI_CODE_FIELD_VALUE:
				case AmiWebDomObject.ARI_CODE_PANEL:
				case AmiWebDomObject.ARI_CODE_DATAMODEL:
					continue;

			}
			String icon = AmiWebAmiObjectsVariablesHelper.getAmiIconStyleForDomObjectType(i);
			WebTreeNode r2 = this.tree.createNode(i.getAri(), r, false, new DomObjectWrapper(i));
			r2.setIconCssStyle(icon);
			visitDom(r2, i);
		}
		AmiWebFormulas formulas = dom.getFormulas();
		if (formulas != null) {
			for (String i : CH.sort(formulas.getFormulaIds())) {
				AmiWebFormula formula = formulas.getFormula(i);
				String formula2 = formula.getFormula(false);
				WebTreeNode r2 = this.tree.createNode((SH.is(formula2) ? "<B>" : "") + formula.getFormulaId(), r, false, formula);
				r2.setIconCssStyle("_bgi=url(" + AmiWebConsts.ICON_METHOD + ")");
			}
		}
		AmiWebAmiScriptCallbacks callbacks = dom.getAmiScriptCallbacks();
		if (callbacks != null) {
			for (String i : CH.sort(callbacks.getAmiScriptCallbackDefinitions())) {
				AmiWebAmiScriptCallback callback = callbacks.getCallback(i);
				String formula = callback.getAmiscript(false);
				WebTreeNode r2 = this.tree.createNode((SH.is(formula) ? "<B>" : "") + i, r, false, callback);
				r2.setIconCssStyle("_bgi=url(" + AmiWebConsts.ICON_CALLBACK + ")");
			}
		}
	}

	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
		List<WebTreeNode> nodes = this.tree.getTree().getSelected();
		if (nodes.size() != 1)
			return;
		Object data = nodes.get(0).getData();
		if (data instanceof AmiWebAmiScriptCallback) {
			AmiWebAmiScriptCallback cb = (AmiWebAmiScriptCallback) data;
			AmiWebDomObject dom = cb.getParent().getThis();
			AmiWebEditAmiScriptCallbackPortlet editor = this.service.getDomObjectsManager().showCallbackEditor(dom.getAri(), cb.getName());
		} else if (data instanceof AmiWebGraphNode) {
			AmiWebGraphHelper.openEditor(service, (AmiWebGraphNode) data);
		} else if (data instanceof DomObjectWrapper) {
			AmiWebDomObject dom = ((DomObjectWrapper) data).getInner();
			this.service.getDomObjectsManager().showFormulaEditor(dom.getAri(), null);
		}

	}

	@Override
	public void onContextMenu(FastWebTree tree, String action) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNodeClicked(FastWebTree tree, WebTreeNode node) {
		if (!this.editedFormulas.isEmpty() || !this.editedCallbacks.isEmpty()) {
			getManager().showAlert("You are in the middle of an edit, please <B>apply</B> or <B>Reset</B> changes first");
			return;
		}
		this.applyButton.setEnabled(false);
		this.cancelButton.setEnabled(false);
		this.form.clearFields();

		Map<AmiWebDomObject, Tuple2<Set<AmiWebFormula>, Set<AmiWebAmiScriptCallback>>> byFormulas = new HasherMap<AmiWebDomObject, Tuple2<Set<AmiWebFormula>, Set<AmiWebAmiScriptCallback>>>(
				IdentityHasher.INSTANCE);
		BasicMultiMap.Set<AmiWebAmiScriptCallbacks, AmiWebAmiScriptCallback> byCallbacks = new BasicMultiMap.Set<AmiWebAmiScriptCallbacks, AmiWebAmiScriptCallback>();
		for (WebTreeNode n : tree.getSelected())
			add(n, byFormulas);

		int y = 5;
		List<AmiWebDomObject> formulasSet = CH.sort(byFormulas.keySet(), FORMULAS_COMPARATOR);
		for (AmiWebDomObject domObject : formulasSet) {

			Tuple2<Set<AmiWebFormula>, Set<AmiWebAmiScriptCallback>> i = byFormulas.get(domObject);
			FormPortletTitleField t = new FormPortletTitleField(domObject.getAri());
			t.setLeftPosPx(150);
			t.setRightPosPx(20);
			t.setTopPosPx(y);
			t.setHeightPx(20);
			this.form.addField(t);
			y += t.getHeightPx() + 5;
			for (AmiWebFormula f : CH.sort(i.getA(), FORMULA_COMPARATOR)) {
				FormPortletTextField tf;
				if (f instanceof AmiWebChartFormula)
					tf = new FormPortletTextField(((AmiWebChartFormula) f).getLabel());
				else
					tf = new FormPortletTextField(f.getFormulaId() + ": ");
				tf.setLeftPosPx(150);
				tf.setRightPosPx(20);
				tf.setTopPosPx(y);
				tf.setHeightPx(20);
				tf.setHasButton(true);
				tf.setValue(f.getFormula(false));
				tf.setCorrelationData(f);
				this.form.addField(tf);
				onFieldChanged(tf);
				y += tf.getHeightPx() + 5;
			}
			for (AmiWebAmiScriptCallback f : CH.sort(i.getB(), CALLBACK_COMPARATOR)) {
				AmiWebFormPortletAmiScriptField tf = new AmiWebFormPortletAmiScriptField(f.getName() + ": ", this.getManager(), f.getAmiLayoutAlias());
				tf.setHelp(f.getParamsDef().toString(service.getScriptManager(domObject.getAmiLayoutFullAlias()).getMethodFactory()));
				tf.clearVariables();
				tf.setThis(domObject);
				tf.addVariable("tableset", Tableset.class);
				CalcTypes paramTypesMapping = f.getParamsDef().getParamTypesMapping();
				for (String e : paramTypesMapping.getVarKeys())
					tf.addVariable(e, paramTypesMapping.getType(e));
				for (Entry<String, String> var : f.getVarNameToAriMap().entrySet()) {
					String varname = var.getKey();
					String ari = var.getValue();
					AmiWebDomObject variable = AmiWebAmiObjectsVariablesHelper.getAmiWebDomObjectFromFullAri(ari, this.service);
					if (variable == null)
						continue;
					Class<?> classType = variable.getDomClassType();
					tf.addVariable(varname, classType);
				}
				tf.setLeftPosPx(150);
				tf.setRightPosPx(20);
				tf.setTopPosPx(y);
				tf.setHeightPx(60);
				//				tf.setHasButton(true);
				tf.setValue(f.getAmiscript(false));
				tf.setCorrelationData(f);
				this.form.addField(tf);
				onFieldChanged(tf);
				y += tf.getHeightPx() + 5;
			}
		}
	}

	private void add(WebTreeNode node, Map<AmiWebDomObject, Tuple2<Set<AmiWebFormula>, Set<AmiWebAmiScriptCallback>>> sink) {
		if (node.getData() instanceof AmiWebFormula) {
			AmiWebFormula f = (AmiWebFormula) node.getData();
			Tuple2<Set<AmiWebFormula>, Set<AmiWebAmiScriptCallback>> existing = get(sink, f.getOwnerFormulas().getThis());
			existing.getA().add(f);
		} else if (node.getData() instanceof AmiWebAmiScriptCallback) {
			AmiWebAmiScriptCallback f = (AmiWebAmiScriptCallback) node.getData();
			Tuple2<Set<AmiWebFormula>, Set<AmiWebAmiScriptCallback>> existing = get(sink, f.getParent().getThis());
			existing.getB().add(f);
		}
		for (WebTreeNode i : node.getChildren())
			add(i, sink);
	}

	private Tuple2<Set<AmiWebFormula>, Set<AmiWebAmiScriptCallback>> get(Map<AmiWebDomObject, Tuple2<Set<AmiWebFormula>, Set<AmiWebAmiScriptCallback>>> sink, AmiWebDomObject t) {
		Tuple2<Set<AmiWebFormula>, Set<AmiWebAmiScriptCallback>> r = sink.get(t);
		if (r == null)
			sink.put(t, r = new Tuple2<Set<AmiWebFormula>, Set<AmiWebAmiScriptCallback>>(new IdentityHashSet<AmiWebFormula>(), new IdentityHashSet<AmiWebAmiScriptCallback>()));
		return r;
	}

	@Override
	public void onCellMousedown(FastWebTree tree, WebTreeNode start, FastWebTreeColumn col) {

	}

	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		Object o = field.getCorrelationData();
		WebMenu r = new BasicWebMenu();
		if (o instanceof AmiWebFormula) {
			WebMenu vm = new BasicWebMenu("Variables", true);
			AmiWebFormula awf = (AmiWebFormula) o;
			com.f1.base.CalcTypes vars = awf.getOwnerFormulas().getThis().getFormulaVarTypes(awf);
			List<String> names = CH.sort(vars.getVarKeys());
			for (String name : names) {
				vm.add(new BasicWebMenuLink(name, true, "var_" + name));
			}
			//check whether awf's formulaId is associated with color
			switch (awf.getFormulaType()) {
				case AmiWebFormula.FORMULA_TYPE_AGG:
					AmiWebMenuUtils.createAggOperatorsMenu(r, false);
					break;
				case AmiWebFormula.FORMULA_TYPE_AGG_RT:
					AmiWebMenuUtils.createAggOperatorsMenu(r, true);
					break;
			}
			r.add(vm);
			AmiWebMenuUtils.createOperatorsMenu(r, service, awf.getOwnerFormulas().getThis().getAmiLayoutFullAlias());
			r.add(AmiWebMenuUtils.createGlobalVariablesMenu("Global Variables", "", service, awf.getOwnerFormulas().getThis()));

			AmiWebDomObject parentDomObject = awf.getOwnerFormulas().getThis().getParentDomObject();
			if (parentDomObject == null)
				return r;
			Object portlet = this.service.getPortletByAliasDotPanelId(parentDomObject.getAmiLayoutFullAliasDotId());
			if (awf instanceof AmiWebFormulaImpl) {//check tree/table
				if ("backgroundColor".equals(awf.getFormulaId()) || "color".equals(awf.getFormulaId())) {
					if (portlet instanceof AmiWebStyledPortlet) {
						r.add(0, AmiWebMenuUtils.createColorsMenu(((AmiWebStyledPortlet) portlet).getStylePeer()));
					}
				}
			} else if (awf instanceof AmiWebChartFormula) {//check chart
				if (((AmiWebChartFormula) awf).getLabel().contains("Color")) {
					if (portlet instanceof AmiWebStyledPortlet) {
						r.add(0, AmiWebMenuUtils.createColorsMenu(((AmiWebStyledPortlet) portlet).getStylePeer()));
					}
				}
			}
		}
		if (field instanceof AmiWebFormPortletAmiScriptField)
			((AmiWebFormPortletAmiScriptField) field).resetAutoCompletion();
		return r;
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (this.applyButton == button) {
			for (FormPortletTextField i : CH.l(this.editedFormulas)) {
				AmiWebFormula o = (AmiWebFormula) i.getCorrelationData();
				if (SH.isnt(i.getValue()))
					o.setFormula(null, false);
				else
					o.setFormula(i.getValue(), false);
				onFieldChanged(i);
			}
			for (AmiWebFormPortletAmiScriptField i : CH.l(this.editedCallbacks)) {
				AmiWebAmiScriptCallback o = (AmiWebAmiScriptCallback) i.getCorrelationData();
				if (SH.isnt(i.getValue()))
					o.setAmiscript(null, false);
				else
					o.setAmiscript(i.getValue(), false);
				onFieldChanged(i);
			}
		} else if (this.cancelButton == button) {
			for (FormPortletTextField i : CH.l(this.editedFormulas)) {
				AmiWebFormula o = (AmiWebFormula) i.getCorrelationData();
				i.setValue(o.getFormula(false));
				onFieldChanged(i);
			}
			for (AmiWebFormPortletAmiScriptField i : CH.l(this.editedCallbacks)) {
				AmiWebAmiScriptCallback o = (AmiWebAmiScriptCallback) i.getCorrelationData();
				i.setValue(o.getAmiscript(false));
				onFieldChanged(i);
			}
		}
	}

	private void onFieldChanged(FormPortletField field) {
		boolean hadNoChanges = this.editedFormulas.isEmpty() && this.editedCallbacks.isEmpty();
		Object o = field.getCorrelationData();
		if (o instanceof AmiWebFormula) {
			FormPortletTextField tf = (FormPortletTextField) field;
			AmiWebFormula awf = (AmiWebFormula) o;
			String s = awf.getFormula(false);
			String value = (String) field.getValue();
			if (OH.eq(value, s) || (SH.isnt(field.getValue()) && s == null)) {
				this.editedFormulas.remove(tf);
				Exception ex = awf.getFormulaError(false);
				if (ex != null) {
					field.setCssStyle("_bg=#FFFFFF|_fg=#FF0000");
					field.setHelp(ex.getMessage());
				} else {
					field.setCssStyle("_bg=#FFFFFF|_fg=#0000AA");
					field.setHelp(null);
				}
			} else {
				this.editedFormulas.add(tf);
				Exception ex = awf.testFormula(value);
				if (ex != null) {
					field.setCssStyle("_bg=#FFFFAA|_fg=#FF0000");
					field.setHelp(ex.getMessage());
				} else {
					field.setCssStyle("_bg=#FFFFAA|_fg=#0000AA");
					field.setHelp(null);
				}
			}
		} else if (o instanceof AmiWebAmiScriptCallback) {
			AmiWebFormPortletAmiScriptField tf = (AmiWebFormPortletAmiScriptField) field;
			AmiWebAmiScriptCallback awf = (AmiWebAmiScriptCallback) o;
			String s = awf.getAmiscript(false);
			String value = (String) field.getValue();
			if (OH.eq(value, s) || (SH.isnt(field.getValue()) && s == null)) {
				this.editedCallbacks.remove(tf);
				Exception ex = awf.getError(false);
				if (ex != null) {
					field.setCssStyle("_bg=#FFFFFF");
					tf.setAnnotation(0, "error", ex.getMessage());
				} else {
					field.setCssStyle("_bg=#FFFFFF");
					tf.clearAnnotation();
				}
			} else {
				this.editedCallbacks.add(tf);
				Exception ex = awf.testAmiscript(value);
				if (ex != null) {
					field.setCssStyle("_bg=#FFFFAA");
					tf.setAnnotation(0, "error", ex.getMessage());
				} else {
					field.setCssStyle("_bg=#FFFFAA");
					tf.clearAnnotation();
				}
			}
		}
		boolean hasNoChanges = this.editedFormulas.isEmpty() && this.editedCallbacks.isEmpty();
		if (hasNoChanges != hadNoChanges) {
			this.applyButton.setEnabled(!hasNoChanges);
			this.cancelButton.setEnabled(!hasNoChanges);
		}
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		onFieldChanged(field);
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		if (keycode == 13) {//enter key
			Object o = field.getCorrelationData();
			if (o instanceof AmiWebFormula) {
				AmiWebFormula awf = (AmiWebFormula) o;
				awf.setFormula((String) field.getValue(), false);
				onFieldValueChanged(formPortlet, field, null);
			}
			if (o instanceof AmiWebAmiScriptCallback) {
				AmiWebAmiScriptCallback awf = (AmiWebAmiScriptCallback) o;
				awf.setAmiscript((String) field.getValue(), false);
				onFieldValueChanged(formPortlet, field, null);
			}
		}
		if (field instanceof AmiWebFormPortletAmiScriptField)
			((AmiWebFormPortletAmiScriptField) field).onSpecialKeyPressed(formPortlet, field, keycode, mask, cursorPosition);
	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		if (node instanceof AmiWebFormPortletAmiScriptField) {
			AmiWebMenuUtils.processContextMenuAction(service, action, (FormPortletTextEditField) node);
			return;
		}
		FormPortletTextField tf = (FormPortletTextField) node;
		String s = AmiWebMenuUtils.parseContextMenuAction(service, action);
		String fieldName = SH.beforeFirst(node.getName(), ":");
		if (SH.is(s)) {
			if (fieldName.contains("color") || fieldName.contains("Color")) {
				AmiWebMenuUtils.applyColorText(s, (FormPortletTextEditField) node, true);
				onFieldChanged(node);
			} else {
				tf.setValue(tf.getValue() + s);
				onFieldChanged(node);
			}
		}

	}

	@Override
	public void onFormulaChanged(AmiWebFormula formula, DerivedCellCalculator old, DerivedCellCalculator nuw) {
		for (String i : this.form.getFields()) {
			FormPortletField field = this.form.getField(i);
			Object o = field.getCorrelationData();
			if (o instanceof AmiWebFormula) {
				AmiWebFormula f = (AmiWebFormula) o;
				if (OH.eq(f.getAri(), formula.getAri())) {
					field.setCorrelationData(formula);
					field.setValue((String) formula.getFormula(false));
					onFieldChanged(field);
				}
			}
		}
		onChanged();
	}

	private void onChanged() {
		this.changed = true;
		flagPendingAjax();
	}

	@Override
	public void onRecompiled() {
		for (String i : this.form.getFields()) {
			FormPortletField<?> field = this.form.getField(i);
			if (field.getCorrelationData() instanceof AmiWebFormula)
				onFieldChanged(field);
			else if (field.getCorrelationData() instanceof AmiWebAmiScriptCallback)
				onFieldChanged(field);
		}
		onChanged();
	}

	@Override
	public void onCallbackChanged(AmiWebAmiScriptCallback callback, DerivedCellCalculator old, DerivedCellCalculator nuw) {
		for (String i : this.form.getFields()) {
			FormPortletField<?> field = this.form.getField(i);
			if (field.getCorrelationData() instanceof AmiWebFormula)
				onFieldChanged(field);
		}
		onChanged();
		//		applyErrors(this.tree.getRoot());
	}

	@Override
	public Object getId(WebTreeNode node) {
		return node.getName();
	}

	@Override
	public int compare(WebTreeNode o1, WebTreeNode o2) {
		Object d1 = o1.getData();
		Object d2 = o2.getData();
		int v1 = getSort(d1);
		int v2 = getSort(d2);
		if (v1 == v2)
			return SH.COMPARATOR_CASEINSENSITIVE_STRING.compare(o1.getName(), o2.getName());
		else
			return OH.compare(v1, v2);
	}

	private int getSort(Object d) {
		if (d instanceof AmiWebGraphNode)
			return 0;
		if (d instanceof DesktopPortlet.Window)
			return 1;
		if (d instanceof AmiWebTabEntry)
			return 2;
		if (d instanceof DomObjectWrapper)
			return 3;
		if (d instanceof AmiWebAmiScriptCallback)
			return 4;
		if (d instanceof AmiWebFormula)
			return 5;
		return 100;
	}

	@Override
	public void initLinkedVariables() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDomObjectAriChanged(AmiWebDomObject target, String oldAri) {

	}

	@Override
	public void onDomObjectEvent(AmiWebDomObject object, byte eventType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDomObjectRemoved(AmiWebDomObject object) {
		for (String i : CH.l(this.form.getFields())) {
			FormPortletField field = this.form.getField(i);
			Object o = field.getCorrelationData();
			if (o instanceof AmiWebFormula) {
				AmiWebFormula f = (AmiWebFormula) o;
				if (f.getOwnerFormulas().getThis() == object) {
					this.form.removeField(field);
					this.editedFormulas.remove(field);
				}
			} else if (o instanceof AmiWebAmiScriptCallback) {
				AmiWebAmiScriptCallback f = (AmiWebAmiScriptCallback) o;
				if (f.getParent().getThis() == object) {
					this.form.removeField(field);
					this.editedCallbacks.remove(field);
				}
			}
		}
		onChanged();
	}

	@Override
	public void onDomObjectAdded(AmiWebDomObject object) {
		onChanged();
	}

}
