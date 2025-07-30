package com.f1.ami.web;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.portlets.AmiWebHeaderPortlet;
import com.f1.ami.web.charts.AmiWebChartGraphicsWrapper;
import com.f1.ami.web.charts.AmiWebChartZoomMetrics;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.portlets.AmiWebDmUtils;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_Heatmap;
import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableListener;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.visual.TreemapNode;
import com.f1.suite.web.portal.impl.visual.TreemapPortlet;
import com.f1.suite.web.portal.impl.visual.WebTreemapContextMenuFactory;
import com.f1.suite.web.portal.impl.visual.WebTreemapContextMenuListener;
import com.f1.utils.CH;
import com.f1.utils.ColorGradient;
import com.f1.utils.ColorHelper;
import com.f1.utils.EmptyIterable;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.MapInMap;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorConst;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public abstract class AmiWebTreemapPortlet extends AmiWebAbstractPortlet
		implements TableListener, WebTreemapContextMenuFactory, WebTreemapContextMenuListener, AmiWebFormulasListener {
	public static final ParamsDefinition CALLBACK_DEF_ONSELECTED = new ParamsDefinition("onSelected", Object.class, "");
	static {
		CALLBACK_DEF_ONSELECTED.addDesc("Called when the selection status of a cell (or cells) has changed");
	}

	protected static final String DEFAULT_HEAT_FORMULA = "\"#ffffff\"";
	private static final String COLUMN_SIZE = "size";
	private static final String COLUMN_TTIP = "ttip";
	private static final String COLUMN_HEAT = "heat";
	public static final String COLUMN_TOP_GROUPING = "TopGrouping";
	public static final String COLUMN_SUB_GROUPING = "SubGrouping";

	DerivedCellCalculatorConst CONST_BLANK = new DerivedCellCalculatorConst(0, "");
	DerivedCellCalculatorConst CONST_ONE = new DerivedCellCalculatorConst(0, 1d);
	private static final Logger log = LH.get();
	private static final int DEFAULT_STICKYNESS = 5;
	protected TreemapPortlet treemap;
	protected AmiWebAggregator aggregator;
	private BasicTable table;
	final private AmiWebFormula groupingFormula;
	final private AmiWebFormula labelFormula;
	final private AmiWebFormula sizeFormula;
	final private AmiWebFormula heatFormula;
	final private AmiWebFormula tooltipFormula;

	private Integer groupingFontSize;
	private String groupingFontColor;
	private Integer groupingBorderSize;
	private String groupingBorderColor;
	private Integer nodeFontSize;
	private String nodeFontColor1;
	private String nodeFontColor2;
	private Integer nodeBorderSize;
	private String nodeBorderColor;
	private String selectBorderColor1;
	private String selectBorderColor2;
	private String groupingBgColor;
	private String fontFamily;

	//add fields, to do
	private BufferedImage bf;
	private Graphics2D g;

	private AmiWebOverrideValue<Double> ratio = new AmiWebOverrideValue<Double>(1.0);
	private AmiWebOverrideValue<Integer> stickyness = new AmiWebOverrideValue<Integer>(1);

	public AmiWebTreemapPortlet(PortletConfig config) {
		super(config);

		treemap = new TreemapPortlet(generateConfig());
		treemap.addMenuContextListener(this);
		treemap.setMenuContextFactory(this);
		setFontSize(13);
		setChild(treemap);
		table = new BasicTable();
		table.addTableListener(this);
		aggregator = new AmiWebAggregator(getService(), null, table, this.getAmiLayoutFullAlias(), this.getService().createStackFrame(this));
		this.formulas.setAggregateTable(this.aggregator.getAggregateTable());
		this.groupingFormula = this.formulas.addFormula("grouping", Object.class);
		this.labelFormula = this.formulas.addFormula("label", Object.class);
		this.sizeFormula = this.formulas.addFormulaAggRt("size", Number.class);
		this.heatFormula = this.formulas.addFormulaAggRt("heat", Object.class);
		this.tooltipFormula = this.formulas.addFormulaAggRt("tooltip", Object.class);
		this.formulas.addFormulasListener(this);
		this.setStickyness(DEFAULT_STICKYNESS, false);
		this.getStylePeer().initStyle();
		setGroupingFontColor("#FFFFFF");
	}

	@Override
	public void populateConfigMenu(WebMenu headMenu) {
		headMenu.add(new BasicWebMenuLink("Heatmap Formulas...", true, "config_formulas"));
	}

	public String getGroupingFormula(boolean override) {
		return groupingFormula.getFormula(override);
	}

	public String getLabelFormula(boolean override) {
		return labelFormula.getFormula(override);
	}

	public String getSizeFormula(boolean override) {
		return sizeFormula.getFormula(override);
	}

	public String getHeatFormula(boolean override) {
		return heatFormula.getFormula(override);
	}
	public String getTooltipFormula(boolean override) {
		return tooltipFormula.getFormula(override);
	}
	public boolean onAmiContextMenu(String action) {
		if (action.equals("config_formulas")) {
			AmiTreemapConfigPortlet t = new AmiTreemapConfigPortlet(generateConfig(), this);
			getManager().showDialog("Heatmap Formulas", t);
			//			this.pendingAction = action;
			return true;
		} else if (action.equals("debug")) {
			StringBuilder sink = new StringBuilder();
			TableHelper.toString(table, "", TableHelper.SHOW_ALL, sink);
			System.out.println(sink);
			return true;
		} else
			return super.onAmiContextMenu(action);
	}
	//	@Override
	//	public void onBackendResponse(ResultMessage<Action> result) {
	//		if (result.getAction() instanceof AmiCenterGetAmiSchemaResponse) {
	//			if ("config_formulas".equals(pendingAction)) {
	//				AmiCenterGetAmiSchemaResponse response = (AmiCenterGetAmiSchemaResponse) result.getAction();
	//				AmiTreemapConfigPortlet t = new AmiTreemapConfigPortlet(generateConfig(), this, response);
	//				getManager().showDialog("Heatmap Formulas", t);
	//			}
	//		} else
	//			super.onBackendResponse(result);
	//	}
	public boolean setFormulas(String grouping, String label, String size, String heat, String tooltip, StringBuilder errorSink) {
		Exception e = this.groupingFormula.testFormula(grouping);
		if (e != null) {
			errorSink.append("Invalid top level grouping: ").append(e.getMessage());
			return false;
		}

		e = this.labelFormula.testFormula(label);
		if (e != null) {
			errorSink.append("Invalid sub grouping grouping: ").append(e.getMessage());
			return false;
		}
		e = this.sizeFormula.testFormula(size);
		if (e != null) {
			errorSink.append("Invalid size: ").append(e.getMessage());
			return false;
		}
		e = this.heatFormula.testFormula(heat);
		if (e != null) {
			errorSink.append("Invalid heat: ").append(e.getMessage());
			return false;
		}
		e = this.tooltipFormula.testFormula(tooltip);
		if (e != null) {
			errorSink.append("Invalid tooltip: ").append(e.getMessage());
			return false;
		}

		this.groupingFormula.setFormula(grouping, false);
		this.labelFormula.setFormula(label, false);
		this.sizeFormula.setFormula(size, false);
		this.heatFormula.setFormula(heat, false);
		this.tooltipFormula.setFormula(tooltip, false);
		return rebuildFormulas();
	}
	protected boolean rebuildFormulas() {
		com.f1.base.CalcTypes variables = getFormulaVarTypes(null);
		try {
			clearAmiData();
			aggregator.setNotifySink(false);
			if (table.getColumnIds().contains(COLUMN_HEAT))
				aggregator.removeAggregateColumn(COLUMN_HEAT);
			if (table.getColumnIds().contains(COLUMN_TTIP))
				aggregator.removeAggregateColumn(COLUMN_TTIP);
			if (table.getColumnIds().contains(COLUMN_SIZE))
				aggregator.removeAggregateColumn(COLUMN_SIZE);
			if (table.getColumnIds().contains(COLUMN_SUB_GROUPING))
				aggregator.removeAggregateColumn(COLUMN_SUB_GROUPING);
			if (table.getColumnIds().contains(COLUMN_TOP_GROUPING))
				aggregator.removeAggregateColumn(COLUMN_TOP_GROUPING);
			Set<String> deps = new HashSet<String>();
			this.usedVariables.clear();
			aggregator.addGroupBy(COLUMN_TOP_GROUPING, toCalc(this.groupingFormula, true), variables);
			aggregator.addGroupBy(COLUMN_SUB_GROUPING, toCalc(this.labelFormula, true), variables);
			aggregator.addAggregateColumn(COLUMN_HEAT, toCalc(this.heatFormula, false), variables);
			aggregator.addAggregateColumn(COLUMN_SIZE, toCalc(this.sizeFormula, false), variables);
			aggregator.addAggregateColumn(COLUMN_TTIP, toCalc(this.tooltipFormula, false), variables);
			aggregator.removeUnusedVariableColumns();

			aggregator.getInnerDependencies(COLUMN_TOP_GROUPING, deps);
			aggregator.getInnerDependencies(COLUMN_SUB_GROUPING, deps);
			aggregator.getInnerDependencies(COLUMN_HEAT, deps);
			aggregator.getInnerDependencies(COLUMN_SIZE, deps);
			aggregator.getInnerDependencies(COLUMN_TTIP, deps);

			for (String o : deps) {
				Class<?> type = variables.getType(o);
				if (type != null) {
					this.usedVariables.putType(o.toString(), type);
					this.putUserDefinedVariable(o.toString(), type);
				}
			}
		} catch (Exception e) {
			//			errorSink.append(e.getMessage());
			LH.warning(log, "Error creating heatmap: ", e);
			return false;
		} finally {
			aggregator.setNotifySink(true);
			this.needsFormulasRebuilt = false;
			rebuildAmiData();
		}
		return true;
	}
	private DerivedCellCalculator toCalc(AmiWebFormula f, boolean isGrouping) {
		DerivedCellCalculator r = f.getFormulaCalc();
		if (r == null)
			return f.getFormulaReturnType() == String.class ? CONST_BLANK : CONST_ONE;
		else
			return r;

	}
	//	private DerivedCellCalculator parse(boolean isGrouping, String label, String formula, StringBuilder errorSink, Map<String, String> groupingFormulas,
	//			com.f1.base.Types variables, Class retType) {
	//		try {
	//			if (SH.isnt(formula))
	//				return retType == String.class ? CONST_BLANK : CONST_ONE;
	//			AmiWebScriptManagerForLayout sm = getScriptManager();
	//			if (isGrouping) {
	//				DerivedCellCalculator r = sm.toCalc(formula, variables);
	//				groupingFormulas.put(formula, label);
	//				return r;
	//			} else {
	//
	//				//				Node node = sm.parseForAgg(formula);
	//				//				try {
	//				//					replaceToplevelVars(node, groupingFormulas);
	//				//				} catch (Exception e) {
	//				//					errorSink.append("Error with " + label + ": " + e.getMessage());
	//				//					return null;
	//				//				}
	//
	//				DerivedCellCalculator r = sm.toAggCalc(formula, this.getFormulaVarTypes(), aggregator.getAggregateTable());
	//				//				DerivedCellCalculator r = sm.toAggCalc(aggregator.getAggregateTable(), node, variables);
	//				if (!retType.isAssignableFrom(r.getReturnType())) {
	//					errorSink.append("Error with " + label + ": Must return a " + retType.getSimpleName() + ", not " + r.getReturnType().getSimpleName());
	//					return null;
	//				}
	//				r = DerivedHelper.reduceConsts(r);
	//				return r;
	//			}
	//		} catch (ExpressionParserException e) {
	//			errorSink.append("Error with " + label + ": " + e.getMessage());
	//			LH.info(log, "In panel ", getAmiPanelId(), ": ", e);
	//			return null;
	//		}
	//	}

	//	private void replaceToplevelVars(Node node, Map<String, String> groupingFormulas) {
	//		if (node instanceof VariableNode) {
	//			VariableNode varnode = (VariableNode) node;
	//			String t = groupingFormulas.get(varnode.varname);
	//			if (t == null)
	//				throw new RuntimeException("Variable not found: " + varnode.varname);
	//			varnode.varname = t;
	//		} else if (node instanceof OperationNode) {
	//			replaceToplevelVars(((OperationNode) node).left, groupingFormulas);
	//			replaceToplevelVars(((OperationNode) node).right, groupingFormulas);
	//		} else if (node instanceof MethodNode) {
	//		} else if (node instanceof ExpressionNode) {
	//			replaceToplevelVars(((ExpressionNode) node).value, groupingFormulas);
	//		}
	//	}

	public static class AmiTreemapConfigPortlet extends GridPortlet implements FormPortletContextMenuListener, FormPortletListener, FormPortletContextMenuFactory {

		final private FormPortlet fm;
		final private FormPortletTextField groupField;
		final private FormPortletTextField labelField;
		final private FormPortletTextField sizeField;
		final private FormPortletTextField heatField;
		final private FormPortletTextField tooltipField;
		final private AmiWebTreemapPortlet amiPortlet;
		//		final private AmiCenterGetAmiSchemaResponse schema;
		final private AmiWebService service;
		final private FormPortletButton updateButton;
		final private AmiWebHeaderPortlet header;

		final private static int HEADER_ROW_SIZE = 100;

		public AmiTreemapConfigPortlet(PortletConfig config, AmiWebTreemapPortlet portlet) {
			super(config);
			this.service = AmiWebUtils.getService(config.getPortletManager());
			this.amiPortlet = portlet;
			//			this.schema = schema;
			this.header = new AmiWebHeaderPortlet(generateConfig());
			this.header.setShowSearch(false);
			//this.header.setCssStyle("Custom AMI Column");
			this.header.updateBlurbPortletLayout("Custom AMI Column", "");
			this.header.setShowLegend(false);
			this.header.setInformationHeaderHeight(100);
			this.header.setShowBar(false);
			addChild(this.header);
			fm = addChild(new FormPortlet(generateConfig()), 0, 1);
			this.fm.getFormPortletStyle().setLabelsWidth(10);
			fm.addField(new FormPortletTitleField("Top Level Grouping"));
			groupField = fm.addField(new FormPortletTextField("")).setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true)
					.setValue(portlet.groupingFormula.getFormula(false));
			fm.addField(new FormPortletTitleField("Group By"));
			labelField = fm.addField(new FormPortletTextField("")).setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true).setValue(portlet.labelFormula.getFormula(false));
			fm.addField(new FormPortletTitleField("Size"));
			sizeField = fm.addField(new FormPortletTextField("")).setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true).setValue(portlet.sizeFormula.getFormula(false));
			fm.addField(new FormPortletTitleField("Heat"));
			heatField = fm.addField(new FormPortletTextField("")).setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true).setValue(portlet.heatFormula.getFormula(false));

			fm.addField(new FormPortletTitleField("Tooltip"));
			tooltipField = fm.addField(new FormPortletTextField("")).setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true)
					.setValue(portlet.tooltipFormula.getFormula(false));
			groupField.setCssStyle("_fm=courier");
			labelField.setCssStyle("_fm=courier");
			sizeField.setCssStyle("_fm=courier");
			heatField.setCssStyle("_fm=courier");
			tooltipField.setCssStyle("_fm=courier");
			this.updateButton = fm.addButton(new FormPortletButton("Update"));
			fm.addMenuListener(this);
			fm.setMenuFactory(this);
			fm.addFormPortletListener(this);
			setSuggestedSize(700, 800);
		}

		public AmiTreemapConfigPortlet hideButtonsForm(boolean hide) {
			this.fm.clearButtons();
			if (!hide)
				this.fm.addButton(this.updateButton);
			return this;
		}

		@Override
		public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
			BasicWebMenu r = new BasicWebMenu();
			if (field == this.groupField || field == this.labelField) {
				AmiWebMenuUtils.createVariablesMenu(r, false, this.amiPortlet);
				return r;
			} else if (field == this.sizeField || field == this.heatField || field == this.tooltipField) {
				if (field == this.heatField) {
					AmiWebMenuUtils.createColorsMenu(r, this.amiPortlet.getStylePeer());
					r.add(new BasicWebMenuDivider());
				}

				AmiWebMenuUtils.createOperatorsMenu(r, service, this.amiPortlet.getAmiLayoutFullAlias());
				r.add(new BasicWebMenuDivider());
				AmiWebMenuUtils.createVariablesMenu(r, true, this.amiPortlet);
				return r;
			}
			return null;
		}
		@Override
		public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
			if (applySettings())
				if (getParent() instanceof RootPortlet)
					close();
		}

		public boolean applySettings() {
			StringBuilder errorSink = new StringBuilder();
			//			com.f1.base.Types variables = AmiWebUtils.getAvailableVariables(this.amiPortlet.getService(), this.amiPortlet);
			if (!this.amiPortlet.setFormulas(this.groupField.getValue(), this.labelField.getValue(), this.sizeField.getValue(), this.heatField.getValue(),
					this.tooltipField.getValue(), errorSink)) {
				getManager().showAlert(errorSink.toString());
				return false;
			}
			return true;
		}
		@Override
		public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		}

		@Override
		public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		}

		@Override
		public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
			AmiWebMenuUtils.processContextMenuAction(service, action, node);
		}

		@Override
		public boolean onUserKeyEvent(KeyEvent keyEvent) {
			if (KeyEvent.ENTER.equals(keyEvent.getKey())) {
				this.onButtonPressed(this.fm, this.updateButton);
				return true;
			}
			return super.onUserKeyEvent(keyEvent);
		}

		public AmiTreemapConfigPortlet hideHeader(boolean hide) {
			if (hide)
				setRowSize(0, 0);
			else
				setRowSize(0, HEADER_ROW_SIZE);
			return this;
		}
	}

	@Override
	public void onColumnAdded(Column nuw) {
	}

	@Override
	public void onColumnRemoved(Column old) {
	}

	@Override
	public void onColumnChanged(Column old, Column nuw) {
	}

	private MapInMap<String, String, TreemapNode> nodes = new MapInMap<String, String, TreemapNode>();
	private boolean amiSelectionChanged = false;
	private int _stickyness;
	private double _ratio = 1;

	protected void addRow(Row add) {
		try {
			Object h = add.get(COLUMN_HEAT);
			if (h == null)
				return;
			final Number value = (Number) add.get(COLUMN_SIZE);
			if (value == null)
				return;
			String groupName = AmiUtils.s(add.get(COLUMN_TOP_GROUPING));
			String label = AmiUtils.s(add.get(COLUMN_SUB_GROUPING));
			if (groupName == null)
				groupName = "<N/A>";
			if (label == null)
				label = "<N/A>";
			String tooltip = AmiUtils.s(add.get(COLUMN_TTIP));

			double heat;
			String bgColor;
			if (h instanceof Number) {
				heat = ((Number) h).doubleValue();
				bgColor = null;
			} else if (h instanceof String) {
				heat = Double.NaN;
				bgColor = (String) h;
			} else
				return;
			TreemapNode node = nodes.getMulti(groupName, label);
			double v = value.doubleValue();
			if (MH.isntNumber(v))
				v = 0;
			if (node == null) {
				TreemapNode group = this.treemap.getRootNode().getChild(groupName);
				if (group == null)
					group = this.treemap.addNode(this.treemap.getRootNode(), groupName, v, Double.NaN, this.groupingBgColor, this.groupingFontColor, label, tooltip);
				else
					group.incrementValue(v);
				nodes.putMulti(groupName, label, node = this.treemap.addNode(group, label, v, heat, bgColor, this.nodeFontColor1, label, tooltip));
				node.setCorrelationData(add);
				onRowAddedToTreemap(add, node);
			} else {
				double old = node.getValue();
				node.getParent().incrementValue(v - old);
				node.setBgColor(bgColor);
				node.setHeat(heat);
				node.setTooltip(getService().cleanHtml(tooltip));
				node.setValue(v);
				onRowUpdateToTreemap(add, node);
			}
		} catch (Exception e) {
			LH.warning(log, "Error adding row: " + add, e);
		}
	}

	protected void onRowUpdateToTreemap(Row add, TreemapNode node) {
	}

	protected void onRowAddedToTreemap(Row add, TreemapNode node) {
	}

	protected void onRowRemovedFromTreemap(Row row, TreemapNode node, boolean wasSelected) {
	}

	protected void removeRow(Row row) {
		String groupName = AmiUtils.s(row.get(COLUMN_TOP_GROUPING));
		if (groupName == null)
			groupName = "<N/A>";
		String label = AmiUtils.s(row.get(COLUMN_SUB_GROUPING));
		if (label == null)
			label = "<N/A>";
		TreemapNode node = nodes.removeMulti(groupName, label);
		if (node != null) {
			boolean wasSelected = node.isSelected();
			TreemapNode parent = node.getParent();
			treemap.removeNode(node.getId());
			double value = node.getValue();
			if (MH.isNumber(value))
				parent.incrementValue(-value);
			if (parent.isEmpty())
				treemap.removeNode(parent.getId());
			onRowRemovedFromTreemap(row, node, wasSelected);
		}

	}

	public com.f1.base.CalcTypes getSpecialVariables() {
		return this.aggregator.getSpecialVariables();
	}

	@Override
	public void clearAmiData() {
		boolean enable = this.aggregator.setNotifySink(false);
		this.aggregator.clear();
		this.treemap.clearNodes();
		this.nodes.clear();
		this.table.clear();
		if (enable)
			this.aggregator.setNotifySink(true);

	}

	protected void onAmiSelectedChanged() {
		this.amiSelectionChanged = true;
		this.flagPendingAjax();
	}

	@Override
	public void drainJavascript() {
		super.drainJavascript();
		if (needsFormulasRebuilt)
			rebuildFormulas();
		if (this.amiSelectionChanged) {
			updateChildTables();
			this.amiSelectionChanged = false;
		}
	}
	protected void updateChildTables() {
		for (AmiWebDmLink link : getDmLinksFromThisPortlet())
			AmiWebDmUtils.sendRequest(getService(), link);
	}

	@Override
	public boolean hasSelectedRows(AmiWebDmLink link) {
		return this.treemap.getSelected().size() > 0;
	}

	@Override
	public Table getSelectableRows(AmiWebDmLink link, byte type) {
		return getSelectableRows(type);
	}

	public Table getSelectableRows(byte type) {
		Table t = new BasicTable(new Column[] { aggregator.getAggregateTable().getColumn(COLUMN_TOP_GROUPING), aggregator.getAggregateTable().getColumn(COLUMN_SUB_GROUPING),
				aggregator.getAggregateTable().getColumn(COLUMN_HEAT), aggregator.getAggregateTable().getColumn(COLUMN_SIZE) });
		switch (type) {
			case NONE:
				return t;
			case ALL:
				for (TreemapNode n : treemap.getNodes())
					t.getRows().addRow(n.getParent().getGroupId(), n.getGroupId(), n.getHeat(), n.getValue());
				return t;
			case SELECTED:
				IntKeyMap<TreemapNode> selected = treemap.getSelected();
				for (TreemapNode n : selected.values())
					t.getRows().addRow(n.getParent().getGroupId(), n.getGroupId(), n.getHeat(), n.getValue());
				return t;
			default:
				throw new RuntimeException("Bad type: " + type);
		}
	}
	public Table getUnderlyingSelectableRows(byte type) {
		switch (type) {
			case ALL:
				return new BasicTable(aggregator.getInnerTable());
			case NONE:
				return new BasicTable(aggregator.getInnerTable().getColumns());
			case SELECTED:
				IntKeyMap<TreemapNode> selected = treemap.getSelected();
				Table t = new BasicTable(aggregator.getInnerTable().getColumns());
				for (TreemapNode n : selected.values()) {
					Iterable<Row> inners = aggregator.getUnderlyingRows((Row) n.getCorrelationData());
					for (Row inner : inners)
						t.getRows().addRow(inner.getValuesCloned());
				}
				return t;
			default:
				throw new RuntimeException("Bad type: " + type);
		}
	}

	@Override
	public void onContextMenu(TreemapPortlet treemap, String action, TreemapNode sel) {
		IntKeyMap<TreemapNode> selected = this.treemap.getSelected();
		if (selected.size() > 0 && action.startsWith("query_")) {
			String remotePortletIds = SH.stripPrefix(action, "query_", true);
			for (String st : SH.split('_', remotePortletIds)) {
				AmiWebDmLink link = getService().getDmManager().getDmLink(st);
				AmiWebDmUtils.sendRequest(getService(), link);
			}
		} else if (isCustomContextMenuAction(action)) {
			processCustomContextMenuAction(action);
		}
	}

	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		r.put("amiTitle", getAmiTitle(false));
		ArrayList<Map> colors = new ArrayList<Map>();
		r.put("amiColors", colors);
		r.put("amiGrouping", groupingFormula.getFormulaConfig());
		r.put("amiLabel", labelFormula.getFormulaConfig());
		r.put("amiSize", sizeFormula.getFormulaConfig());
		r.put("amiHeat", heatFormula.getFormulaConfig());
		r.put("amiTip", tooltipFormula.getFormulaConfig());
		r.put("fontSize", getFontSize());
		r.put("amiStickyness", getStickyness(false));
		r.put("ratio", getRatio(false));
		Map<String, String> vtypes = new HashMap<String, String>();
		AmiWebUtils.toVarTypesConfiguration(getService(), this.getAmiLayoutFullAlias(), this.getUserDefinedVariables(), vtypes);
		AmiWebUtils.toVarTypesConfiguration(getService(), this.getAmiLayoutFullAlias(), this.getUsedVariables(), vtypes);
		r.put("varTypes", vtypes);

		//		CH.putNoNull(r, "gfs", getGroupingFontSize());
		//		CH.putNoNull(r, "gfc", getGroupingFontColor());
		//		CH.putNoNull(r, "gbs", getGroupingBorderSize());
		//		CH.putNoNull(r, "gbc", getGroupingBorderColor());
		//		CH.putNoNull(r, "ggc", getGroupingBgColor());
		//		CH.putNoNull(r, "nfs", getNodeFontSize());
		//		CH.putNoNull(r, "nfc1", getNodeFontColor1());
		//		CH.putNoNull(r, "nfc2", getNodeFontColor2());
		//		CH.putNoNull(r, "nbs", getNodeBorderSize());
		//		CH.putNoNull(r, "nbc", getNodeBorderColor());
		//		CH.putNoNull(r, "sbc1", getSelectBorderColor1());
		//		CH.putNoNull(r, "sbc2", getSelectBorderColor2());

		return r;
	}

	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		this.groupingFormula.initFormula(CH.getOr(Caster_String.INSTANCE, configuration, "amiGrouping", null));
		this.labelFormula.initFormula(CH.getOr(Caster_String.INSTANCE, configuration, "amiLabel", null));
		this.sizeFormula.initFormula(CH.getOr(Caster_String.INSTANCE, configuration, "amiSize", null));
		this.heatFormula.initFormula(CH.getOr(Caster_String.INSTANCE, configuration, "amiHeat", DEFAULT_HEAT_FORMULA));
		if (SH.isnt(this.heatFormula)) {
			this.heatFormula.setFormula(DEFAULT_HEAT_FORMULA, false);
		}
		this.tooltipFormula.initFormula(CH.getOr(Caster_String.INSTANCE, configuration, "amiTip", null));
		setStickyness(CH.getOr(Caster_Integer.PRIMITIVE, configuration, "amiStickyness", DEFAULT_STICKYNESS), false);
		setRatio(CH.getOr(Caster_Double.PRIMITIVE, configuration, "ratio", 1d), false);
		final com.f1.utils.structs.table.stack.BasicCalcTypes varTypes = AmiWebUtils.fromVarTypesConfiguration(getService(), (Map<String, String>) configuration.get("varTypes"));
		//		Map<String, String> vtypes = (Map<String, String>) CH.getOr(Caster_Simple.OBJECT, configuration, "varTypes", null);
		if (varTypes != null) {
			//			com.f1.utils.BasicTypes varTypes = new com.f1.utils.BasicTypes(vtypes.size());
			//			for (Entry<String, String> e : vtypes.entrySet())
			//				varTypes.put(e.getKey(), AmiWebUtils.toClass(AmiWebUtils.saveCodeToType(e.getValue())));
			setUserDefinedVariables(varTypes);
		}
		super.init(configuration, origToNewIdMapping, sb);
		this.rebuildFormulas();
	}
	public int getStickyness(boolean isOverride) {
		return this.stickyness.getValue(isOverride);
	}

	public void setStickyness(int stickynessPassed, boolean isOverride) {
		treemap.addOption(TreemapPortlet.OPTION_STICKYNESS, stickynessPassed / 2d);
		stickyness.setValue(stickynessPassed, isOverride);

	}
	public void setRatio(double ratioPassed, boolean isOverride) {
		treemap.addOption(TreemapPortlet.OPTION_RATIO, ratioPassed);
		ratio.setValue(ratioPassed, isOverride);
	}
	public double getRatio(boolean isOverride) {
		return this.ratio.getValue(isOverride);
	}

	@Override
	public void onNodeClicked(TreemapPortlet portlet, TreemapNode node, int btn) {
	}

	@Override
	public WebMenu createMenu(TreemapPortlet treemap, TreemapNode selected) {
		List<WebMenuItem> entries = new ArrayList<WebMenuItem>();
		BasicMultiMap.List<String, String> title2portletId = new BasicMultiMap.List<String, String>();
		if (treemap.getSelected().size() > 0) {
			for (AmiWebDmLink link : getDmLinksFromThisPortlet()) {
				if (link.isRunOnSelect() || link.isRunOnAmiScript() || link.isRunOnDoubleClick())
					continue;
				for (String s : SH.split('|', link.getTitle())) {
					title2portletId.putMulti(SH.trimWhitespace(s), link.getLinkUid());
				}
			}
		}
		for (String s : CH.sort(title2portletId.keySet())) {
			entries.add(new BasicWebMenuLink(s, true, SH.join('_', title2portletId.get(s), new StringBuilder("query_")).toString()));
		}
		BasicWebMenu m = new BasicWebMenu("", true, entries);
		m.sort();
		addCustomMenuItems(m);
		return m;
	}
	@Override
	public void onSelectionChanged(TreemapPortlet treemapPortlet, IntKeyMap<TreemapNode> selected, boolean userDriven) {
		this.callbacks.execute(CALLBACK_DEF_ONSELECTED.getMethodName());
		onAmiSelectedChanged();
	}

	public String getColumnTitleFor(String name) {
		return this.aggregator.getUnderlyingColumnTitleFor(name);
	}

	public String getSpecialVariableTitleFor(String name) {
		return this.aggregator.getUnderlyingColumnTitleFor(name);
	}

	private Set<String> groupByColumnIds = new HashSet<String>();
	private com.f1.utils.structs.table.stack.BasicCalcTypes usedVariables = new com.f1.utils.structs.table.stack.BasicCalcTypes();
	private String textAlign;
	private String verticalAlign;
	private boolean needsFormulasRebuilt;

	public Set<String> getGroupByColumnIds() {
		return groupByColumnIds;
	}

	protected com.f1.base.CalcTypes getUsedVariables() {
		return this.usedVariables;
	}

	protected void clearTreemap() {
		this.treemap.clearNodes();
		this.nodes.clear();
	}

	@Override
	public void clearUserSelection() {
		treemap.clearSelected();
	}

	@Override
	public void getUsedColors(Set<String> sink) {
		AmiWebUtils.getColors(this.heatFormula.getFormula(false), sink);
	}

	public void setGroupingFontSize(Integer size) {
		if (size == null)
			return;
		this.flagPendingAjax();
		this.treemap.setTextSize(0, size);
		this.groupingFontSize = size;
	}

	public void setGroupingFontColor(String color) {
		this.flagPendingAjax();
		for (TreemapNode i : this.treemap.getRootNode().getChildren()) {
			i.setTextColor(color);
		}
		this.treemap.setTextColor(0, color);
		this.groupingFontColor = color;
	}
	public void setGroupingBgColor(String color) {
		this.flagPendingAjax();
		for (TreemapNode i : this.treemap.getRootNode().getChildren()) {
			i.setBgColor(color);
		}
		this.treemap.setBackgroundColor(0, color);
		this.treemap.addOption(TreemapPortlet.OPTION_BACKGROUND_COLOR, color);
		this.groupingBgColor = color;
	}

	public void setGroupingBorderSize(Integer size) {
		if (size == null)
			return;
		this.flagPendingAjax();
		this.treemap.setBorderSize(0, size);
		this.groupingBorderSize = size;
	}

	public void setGroupingBorderColor(String color) {
		this.flagPendingAjax();
		this.treemap.setBorderColor(0, color);
		this.groupingBorderColor = color;
	}

	public void setNodeFontSize(Integer size) {
		if (size == null)
			return;
		this.flagPendingAjax();
		this.treemap.setTextSize(1, size);
		this.nodeFontSize = size;
	}

	public void setNodeFontColor1(String color) {
		this.flagPendingAjax();
		this.treemap.setTextColor(1, color);
		for (TreemapNode i : this.treemap.getRootNode().getChildren()) {
			for (TreemapNode j : i.getChildren()) {
				j.setTextColor(color);
			}
		}
		this.nodeFontColor1 = color;
	}
	public void setNodeFontColor2(String color) {
		this.flagPendingAjax();
		this.nodeFontColor2 = color;
	}

	public void setNodeBorderSize(Integer size) {
		if (size == null)
			return;
		this.flagPendingAjax();
		this.treemap.setBorderSize(1, size);
		this.nodeBorderSize = size;
	}

	public void setNodeBorderColor(String color) {
		this.flagPendingAjax();
		this.treemap.setBorderColor(1, color);
		this.nodeBorderColor = color;
	}
	private void setGradient(ColorGradient nuw) {
		this.flagPendingAjax();
		this.treemap.setGradient(nuw);
	}

	public void setSelectBorderColor1(String color) {
		this.flagPendingAjax();
		this.treemap.addOption(TreemapPortlet.OPTION_SELECT_BORDER_COLOR1, color);
		this.selectBorderColor1 = color;
	}
	public void setSelectBorderColor2(String color) {
		this.flagPendingAjax();
		this.treemap.addOption(TreemapPortlet.OPTION_SELECT_BORDER_COLOR2, color);
		this.selectBorderColor2 = color;
	}
	public void setTextAlign(String align) {
		this.flagPendingAjax();
		this.treemap.addOption(TreemapPortlet.OPTION_TEXT_H_ALIGN, align);
		this.textAlign = align;
	}
	public String getTextAlign() {
		return this.textAlign;
	}

	public void setVerticalAlign(String align) {
		this.flagPendingAjax();
		this.treemap.addOption(TreemapPortlet.OPTION_TEXT_V_ALIGN, align);
		this.verticalAlign = align;
	}
	@Override
	public void setFontFamily(String fontFamily) {
		this.flagPendingAjax();
		this.treemap.addOption(TreemapPortlet.OPTION_FONT_FAMILY, fontFamily);
		this.fontFamily = fontFamily;
	}
	public String getVerticalAlign() {
		return this.verticalAlign;
	}

	public Integer getGroupingFontSize() {
		return this.groupingFontSize;
	}

	public String getGroupingFontColor() {
		return this.groupingFontColor;
	}
	public String getGroupingBgColor() {
		return this.groupingBgColor;
	}

	public Integer getGroupingBorderSize() {
		return this.groupingBorderSize;
	}

	public String getGroupingBorderColor() {
		return this.groupingBorderColor;
	}

	public Integer getNodeFontSize() {
		return this.nodeFontSize;
	}

	public String getNodeFontColor1() {
		return this.nodeFontColor1;
	}

	public String getNodeFontColor2() {
		return this.nodeFontColor2;
	}

	public Integer getNodeBorderSize() {
		return this.nodeBorderSize;
	}

	public String getNodeBorderColor() {
		return this.nodeBorderColor;
	}

	public String getSelectBorderColor1() {
		return this.selectBorderColor1;
	}

	public String getSelectBorderColor2() {
		return this.selectBorderColor2;
	}
	public String getFontFamily() {
		return this.fontFamily;
	}
	@Override
	public String getStyleType() {
		return AmiWebStyleTypeImpl_Heatmap.TYPE_HEATMAP;
	}
	@Override
	public void onStyleValueChanged(short key, Object old, Object nuw) {
		super.onStyleValueChanged(key, old, nuw);

		switch (key) {
			case AmiWebStyleConsts.CODE_GROUP_FONT_SZ:
				setGroupingFontSize(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_GROUP_FONT_CL:
				setGroupingFontColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_GROUP_BDR_SZ:
				setGroupingBorderSize(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_GROUP_BDR_CL:
				setGroupingBorderColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_GROUP_BG_CL:
				setGroupingBgColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_NODE_FONT_SZ:
				setNodeFontSize(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_NODE_FONT_CL:
				setNodeFontColor1((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_NODE_BDR_SZ:
				setNodeBorderSize(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_NODE_BDR_CL:
				setNodeBorderColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_NODE_GRADIENT:
				setGradient((ColorGradient) nuw);
				break;
			case AmiWebStyleConsts.CODE_SEL_BDR_CL1:
				setSelectBorderColor1((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_SEL_BDR_CL2:
				setSelectBorderColor2((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_TXT_ALIGN:
				setTextAlign((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_VT_ALIGN:
				setVerticalAlign((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_FONT_FAM:
				setFontFamily((String) nuw);
				break;
		}
	}

	public void setGroupingFormulaOverride(String formula) {
		this.groupingFormula.setFormula(formula, true);
	}

	public void setLabelFormulaOverride(String formula) {
		this.labelFormula.setFormula(formula, true);
	}
	public void setSizeFormulaOverride(String formula) {
		this.sizeFormula.setFormula(formula, true);
	}
	public void setHeatFormulaOverride(String formula) {
		this.heatFormula.setFormula(formula, true);
	}
	public void setTooltipFormulaOverride(String formula) {
		this.tooltipFormula.setFormula(formula, true);
	}

	protected BasicTable getTable() {
		return this.table;
	}

	@Override
	public void onFormulaChanged(AmiWebFormula formula, DerivedCellCalculator old, DerivedCellCalculator nuw) {
		flagNeedsFormulasRebuilt();
	}

	private void flagNeedsFormulasRebuilt() {
		this.needsFormulasRebuilt = true;
		flagPendingAjax();
	}

	//These are the member methods related to heatmap_to_png feature
	private void layoutChildren(TreemapNode data, int x, int y, int w, int h, boolean isLeftMost, boolean isTopMost) {
		int pbw = data.getDepth() == -1 ? 0 : data.getTreemap().getBorderSize(data.getDepth());//definately correct
		int pbh = data.getDepth() == -1 ? 0 : data.getTreemap().getBorderSize(data.getDepth());
		if (pbw > w / 20)
			pbw = w / 20;
		if (pbh > h / 20)
			pbh = h / 20;
		if (!isLeftMost) {
			data.setX(x);//data_x = x;
			data.setW(w - pbw);//data_w = w - pbw;
		} else {
			data.setX(x + pbw);//data_x = x + pbw;
			data.setW(w - pbw - pbw);//data_w = w - pbw - pbw;
		}
		if (!isTopMost) {
			data.setY(y);//data_y = y;
			data.setH(h - pbh);//data_h = h - pbh;
		} else {
			data.setY(y + pbh);//data_y = y + pbh;
			data.setH(h - pbh - pbh);//data_h = h - pbh - pbh;
		}
		//		int data_innerX = data_x;
		//		int data_innerY = data_y;
		//		int data_innerW = data_w;
		//		int data_innerH = data_h;
		data.setInnerX(data.getX());
		data.setInnerY(data.getY());
		data.setInnerW(data.getW());
		data.setInnerH(data.getH());
		if (data.getChildren() != EmptyIterable.INSTANCE && data.getDepth() >= 0) {
			int titleSize = data.getTreemap().getTextSize(data.getDepth()) + 1;
			if (titleSize * 6 > h)
				titleSize = data.getH() / 6; //titleSize = data_h / 6;
			data.setInnerY(data.getInnerY() + titleSize);//data_innerY += titleSize;
			data.setInnerH(data.getInnerH() - titleSize);//data_innerH -= titleSize;
		}
		if (data.getChildren() != EmptyIterable.INSTANCE) {
			//replicate layout()
			layout(data);//layout(data, data_innerW, data_innerH, data_innerX, data_innerY);
		}
	}

	private double diffNums(double a) {
		return (a > 1) ? Math.abs(1 / a - 1 / this._ratio) : Math.abs(a - this._ratio);
	}

	private void layout(TreemapNode data) {
		int x, y, w, h;
		x = data.getInnerX();
		y = data.getInnerY();
		w = data.getInnerW();
		h = data.getInnerH();
		final int totalChildrenNumber = data.childrenSorted().size(); //children qty
		double total = data.sumChildrenValue();
		int dataOffset = 0;
		List<TreemapNode> childrenSorted = data.childrenSorted();
		HashMap<Integer, HashMap<String, Number>> childrenSticky = new HashMap<>();
		int stickyDepth = 0;
		//start of 1st while loop
		while (dataOffset < totalChildrenNumber) {
			double pixelRatio = w * h / total;
			int lastLayoutAspect = 1000000;
			int lastLayoutOffset = -1;
			if (stickyDepth < childrenSticky.size()) {
				lastLayoutAspect = (int) childrenSticky.get(stickyDepth).get("aspect");//childrenSticky.get(stickyDepth).aspect;
				lastLayoutOffset = (int) childrenSticky.get(stickyDepth).get("offset");
			}
			double totalSize = 0;
			int endOffset = dataOffset;
			boolean isWide = w >= h;
			double t = (isWide ? (h * h) : (w * w)) / pixelRatio;
			double lastAspect = -1;
			double aspectAtBreak = -1;
			//start of 2nd while
			while (endOffset < data.childrenSorted().size()) { //while endoffset<childrenLength
				//shift these varaibles outwards of while loop
				double childVal = childrenSorted.get(endOffset).getValue(); //var val=data[endOffset].size;
				double tot = totalSize + childVal;
				double aspect = childVal * t / tot / tot;
				if (isWide)
					aspect = 1 / aspect;
				if (lastAspect != -1 && this.diffNums(aspect) >= this.diffNums(lastAspect)) { //TBD, whether lastAspect is null or -1
					aspectAtBreak = aspect;
					break;
				}
				lastAspect = aspect;
				totalSize = tot;
				endOffset++;
			} //end of 2nd while
			if (lastLayoutOffset != endOffset && lastLayoutOffset != -1) {//start if
				double change = lastLayoutAspect / aspectAtBreak;
				if (change < 6 && this._ratio == 1) { //6 == his.aspectStickyness
					aspectAtBreak = lastLayoutAspect;
					endOffset = lastLayoutOffset;
					totalSize = 0;
					for (int i = dataOffset; i < endOffset; i++)
						totalSize += childrenSorted.get(i).getValue();
				} else {
					childrenSticky = null;
				}
			} //end if
			if (childrenSticky != null) { // if the childrenSticky arraylist contains sth
				HashMap<String, Number> entryMap = new HashMap<>();
				if (aspectAtBreak != -1) { //if it breaks
					entryMap.put("aspect", aspectAtBreak);
				} else {
					entryMap.put("aspect", lastAspect);
				}
				entryMap.put("offset", endOffset);
				childrenSticky.put(new Integer(stickyDepth), entryMap);
				//endRes: sticky[stickyDepth]={aspect:aspect,offset:endOffset};
				if (isWide) {
					int split = (int) (endOffset == childrenSorted.size() ? w : Math.floor(pixelRatio * (totalSize / h)));
					int yy = y;
					long runSize = 0;
					for (int i = dataOffset; i < endOffset; i++) {
						TreemapNode d = childrenSorted.get(i);
						runSize += d.getValue();
						int yy2 = (int) (i + 1 == endOffset ? y + h : Math.floor(y + runSize * (h / totalSize)));
						this.layoutChildren(d, x, yy, split, yy2 - yy, x == data.getInnerX(), yy == data.getInnerY());
						yy = yy2;
					}
					x += split;
					w -= split;
				} else {
					int split = (int) (endOffset == childrenSorted.size() ? h : Math.floor(pixelRatio * (totalSize / w)));
					int xx = x;
					int runSize = 0;
					for (int i = dataOffset; i < endOffset; i++) {
						TreemapNode d = childrenSorted.get(i);
						runSize += d.getValue();
						int xx2 = (int) (i + 1 == endOffset ? x + w : Math.floor(x + runSize * (w / totalSize)));
						this.layoutChildren(d, xx, y, xx2 - xx, split, xx == data.getInnerX(), y == data.getInnerY());
						xx = xx2;
					}
					y += split;
					h -= split;
				}
				stickyDepth++;
				dataOffset = endOffset;
				total -= totalSize;
			}
		}
		//set childrenSticky
		data.setChildrenSticky(childrenSticky);
	}

	private void paintChildren(TreemapNode node, boolean isRoot) {
		if (node.getChildren() == EmptyIterable.INSTANCE) { // if node does not have children
			fillChildRect(node.getX(), node.getY(), node.getW(), node.getH(), node);
			return;
		}
		int boxSize = node.getW() * node.getH(); //LATER need to add treemapPortlet.zoom, i.e. w*h*this.zoom*this.zoom
		OH.assertTrue(boxSize > 0, "Heatmap box size must be greater than zero. Please double check the heatmap formula. Positive box size");
		int totSize = bf.getWidth() * bf.getHeight();
		if (boxSize < totSize / 500) {
			this.fillChildRect(node.getX(), node.getY(), node.getW(), node.getH(), node);
		} else {
			String hMapPanelBgColor = treemap.getStyle(node.getDepth() + 1).getBorderColor();
			Color processed_hMapPanelBgColor = ColorHelper.parseColor(hMapPanelBgColor);
			if (!isRoot) {
				//1.this.context.fillStyle=this.borderColors[node.depth+1];
				g.setColor(processed_hMapPanelBgColor);
				g.fillRect(node.getX(), node.getInnerY(), node.getW(), node.getInnerH());
			} else {
				//step1. initialize heatmap panel background color- gray
				g.setColor(processed_hMapPanelBgColor);
				g.fillRect(node.getX(), node.getY(), node.getW(), node.getH());
			}
			ArrayList<TreemapNode> childrenSorted = (ArrayList<TreemapNode>) node.childrenSorted();
			for (int i = 0; i < childrenSorted.size(); i++) {
				TreemapNode child = childrenSorted.get(i);
				if (this.outsideClip(child))
					continue;
				int x = child.getX();
				int y = child.getY();
				int w = child.getW();
				int h = child.getH();
				if (w != 0 && h != 0) {
					this.paintChildren(child, false); // recursive call, focus on this
				}
			}
		}
		if (!isRoot) {
			//this.context.fillStyle = node.heat;
			g.setColor(ColorHelper.parseColor(node.getBgColor()));//gray
			if (boxSize < totSize / 100) {
				//this.context.globalAlpha = 1 - boxSize / (totSize / 100);
				float globalAlpha = 1 - (float) boxSize / (totSize / 100);
				AlphaComposite alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, globalAlpha);
				g.setComposite(alcom);
				//this.context.fillRect(node.innerX, node.innerY, node.innerW, node.innerH);
				g.fillRect(node.getInnerX(), node.getInnerY(), node.getInnerW(), node.getInnerH());
				//this.context.globalAlpha = 1;
				globalAlpha = 1; //set alpha back to 1 and apply
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, globalAlpha));
			}
			//this.context.fillRect(node.x, node.y, node.w, node.innerY - node.y);
			g.fillRect(node.getX(), node.getY(), node.getW(), node.getInnerY() - node.getY());
			//var c = node.textColor;
			String c = node.getTextColor();
			this.writeText2(node.getX(), node.getY(), node.getW(), node.getH() - node.getInnerH(), node.getTreemap().getTextSize(node.getDepth()),
					c != null ? c : node.getTreemap().getTextColor(node.getDepth()), node, 2);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			//draw the triangular arrow
			//step1: fill the shape
			//1. set the stoke, strokewidth=1
			g.setStroke(new BasicStroke(1));

			//2. set the fill color
			g.setColor(ColorHelper.parseColor(node.getBgColor()));

			//3.draw the outline of the triangle
			//var t = Math.min(5,this.zoom*node.w/10)/this.zoom;->
			int t = Math.min(5, node.getW() / 10);
			Path2D.Double path = new Path2D.Double();
			path.moveTo(node.getX() + t, node.getInnerY());
			path.lineTo(node.getX() + 2 * t, node.getInnerY() + t);
			path.lineTo(node.getX() + 3 * t, node.getInnerY());
			g.fill(path);

			//Step2:draw the border
			//1 set the stroke color
			g.setColor(ColorHelper.parseColor(node.getBorderColor() == null ? treemap.getStyle(node.getDepth()).getBorderColor() : node.getBorderColor()));

			g.drawLine(node.getX() + t, node.getInnerY(), node.getX() + 2 * t, node.getInnerY() + t);
			g.drawLine(node.getX() + 2 * t, node.getInnerY() + t, node.getX() + 3 * t, node.getInnerY());

		}
	}

	private boolean outsideClip(TreemapNode node) {
		//return (node.x+node.w<this.minX || node.y+node.h<this.minY || node.x>this.maxX || node.y>this.maxY); // need to consider scale
		return (node.getX() + node.getW() < 0 || node.getY() + node.getH() < 0 || node.getX() > node.getTreemap().getWidth() || node.getY() > node.getTreemap().getHeight());
	}

	private void fillChildRect(int x, int y, int w, int h, TreemapNode node) {
		if (w < 1 && h < 1) //if(w<1/this.zoom && h<1/this.zoom), for now zoom=1
			return;
		//this.context.fillStyle=node.heat;->
		String nodeBgColor = node.getBgColor(); //
		if (nodeBgColor != null)
			g.setColor(ColorHelper.parseColor(nodeBgColor));
		//this.context.fillRect(x,y,w,h);->
		g.fillRect(x, y, w, h);
		//var c=node.textColor;->
		String textColor = node.getTextColor();
		if (textColor == null)
			textColor = node.getTreemap().getTextColor(node.getDepth());
		if (w > 10 / 1 && h > 10 / 1) //assume zoom=1???why comment out?
			writeText2(x, y, w, h, node.getTreemap().getTextSize(node.getDepth()), textColor, node, 2);
	}

	private void writeText2(int x, int y, int w, int h, int fontSize, String fontColor, TreemapNode node, double margin) {
		//if(!node.names)
		if (node.getGroupId() == null)
			return;
		margin = margin / 1; //ideally divider zoom, assume zoom=1
		w -= margin + margin;
		h -= margin + margin;
		int hz = h * 1; //same here, assume zoom = 1
		int wz = w * 1;
		if (hz < 6 || wz < 6)
			return;
		x += margin;
		y += margin;
		int maxFs = fontSize;
		//fontSize=cl(fontSize*this.zoom);
		//var names=node.names;->
		fontSize = (int) Math.ceil(fontSize * 1);
		String names = node.getGroupId();
		//this.context.font=this.getFont(fontSize);->
		String[] namesArr = names.split("\n");
		Font f = new Font("Arial", Font.PLAIN, fontSize);
		g.setFont(f);
		int textHeight = fontSize;
		if (textHeight > hz / namesArr.length) {
			textHeight = (int) Math.floor(hz / namesArr.length);
			fontSize = textHeight;
			//this.context.font=this.getFont(fontSize);->
			Font f1 = new Font("Arial", Font.PLAIN, fontSize);
			g.setFont(f1);
		}
		//int textWidth=this.context.measureText(node.maxName).width;->
		FontMetrics metrics = g.getFontMetrics(f);
		int textWidth = metrics.stringWidth(names);
		if (textWidth > wz) {
			fontSize = (int) (fontSize * ((double) wz / (double) textWidth));
			//this.context.font=this.getFont(fontSize);->
			Font f3 = new Font("Arial", Font.PLAIN, fontSize);
			g.setFont(f3);
			textHeight = fontSize;
		}
		if (fontSize < 8) {
			if (fontSize < 6)
				return;
			fontSize = 8;
			//this.context.font=this.getFont(fontSize);->
			Font f4 = new Font("Arial", Font.PLAIN, fontSize);
			g.setFont(f4);
		}
		//this.context.fillStyle=fontColor;->
		g.setColor(ColorHelper.parseColor(fontColor));
		double centerX, centerY;
		double hAlignScale = 0.5;
		double vAlignScale = 0.5;
		double padding = 1;
		if (node.getDepth() == 0) {
			hAlignScale = .5;
			vAlignScale = .5;
			padding = 0;
		} else {
			//hAlignScale=this.hAlignScale;->
			hAlignScale = 0.5;//(double) treemap.getOption("textHAlign");
			//vAlignScale=this.vAlignScale;->
			vAlignScale = 0.5;//(double) treemap.getOption("textVAlign");
			padding = 4;
		}
		//padding/=this.zoom;->
		padding /= 1;

		centerX = padding + x + ((w - padding * 2) * hAlignScale);
		centerY = padding + y + ((h - padding * 2) * vAlignScale);

		//1. calculate bounding box dimension {_W,_H,_X,_Y}
		int _W, _H, _X, _Y;

		//1.1 calculate the maxNameLength
		String longestName = null;
		int maxNameLength = 0;
		for (String n : namesArr) {
			if (n.length() > maxNameLength) {
				longestName = n;
				maxNameLength = n.length();
			}
		}

		_W = (int) (maxNameLength + (int) 2 * padding);
		_H = (int) (namesArr.length * textHeight + 2 * padding + (namesArr.length - 1) * padding);
		_X = (int) ((node.getInnerW() - _W) / 2 + node.getInnerX());
		_Y = (int) ((node.getInnerH() - _H) / 2 + node.getInnerY());

		//2. loop over each name in namesArr, calcuate xi yi and drawString
		int yPadding = 0;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		for (String n : namesArr) {
			//int li = n.length();
			if (node.getDepth() == 0) {//if the node is first order group by node
				Font f_TopGroup = new Font("Arial", Font.BOLD, fontSize);
				g.setFont(f_TopGroup);
				g.drawString(n, (int) ((int) centerX - 0.5 * g.getFontMetrics().stringWidth(n)), (int) (centerY + 1 * g.getFontMetrics().getDescent()));
			} else {
				int li = g.getFontMetrics().stringWidth(n);
				int xi = (int) ((_W - li) / 2 + padding + _X - 0.5 * textHeight);
				int yi = (int) (padding + _Y) + yPadding + textHeight;
				g.drawString(n, xi, yi);
				yPadding += padding + textHeight;
			}
		}

	}

	public byte[] treemapPortletToPng(int w, int h) {
		//0.initialize the g2d object
		bf = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		g = bf.createGraphics();

		TreemapNode root = treemap.getRootNode();

		//1. layout Children
		layoutChildren(root, 0, 0, w, h, false, true);

		//2. paint childrem
		paintChildren(root, true);

		//3. last step
		g.dispose();

		try {
			//byte[] img = AmiWebChartGraphicsWrapper.testDrawHeatmapBorder();
			AmiWebChartGraphicsWrapper wrapper = new AmiWebChartGraphicsWrapper();
			AmiWebChartZoomMetrics z = new AmiWebChartZoomMetrics(720, 1, 0, 115, 1, 0);
			Map<String, String> fMap = new HashMap<>();
			wrapper.init(z, fMap);
			wrapper.setBufferedImage(bf);
			byte[] img = wrapper.renderPNG(1);
			return img;
		} catch (Exception e) {
			LH.warning(AmiWebTreemapPortlet.log, e.getMessage(), e);
		}
		return new byte[1];
	}

	public TreemapNode getNodeByGroupPath(List<String> paths) {
		return this.treemap.getNodeByGroupPath(paths);
	}

	public boolean isNodeSelected(List<String> paths) {
		TreemapNode node = getNodeByGroupPath(paths);
		// ensure node is not a top level grouping node
		return node != null && node.getDepth() > 0 && node.isSelected();
	}

	public boolean selectNodeByGroupPath(List<String> paths, boolean shouldSelect) {
		TreemapNode node = getNodeByGroupPath(paths);
		if (node == null || OH.eq(node.isSelected(), shouldSelect))
			return false;
		node.setSelected(shouldSelect);
		return true;
	}

	public boolean setNodeBorderColorByGroupPath(List<String> paths, String color) {
		TreemapNode node = this.getNodeByGroupPath(paths);
		if (node == null || node.getDepth() < 1) {
			// -1 depth is base, 0 depth is top grouping
			return false;
		}
		return setNodeBorderColor(node, color);
	}

	public boolean setNodeBorderColor(TreemapNode node, String color) {
		return this.treemap.setNodeBorderColor(node, color);
	}

	public boolean isNodeHighlighted(List<String> path) {
		TreemapNode node = getNodeByGroupPath(path);
		return node != null && node.getDepth() > 0 && this.treemap.isNodeHighlighted(node);
	}
}
