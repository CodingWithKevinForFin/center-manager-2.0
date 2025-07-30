package com.f1.ami.web.charts;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.charts.AmiWebChartSeries.Grouping;
import com.f1.ami.web.dm.AmiWebDmsImpl;
import com.f1.ami.web.dm.portlets.AmiWebDmViewDataPortlet;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ColorPickerListener;
import com.f1.suite.web.portal.impl.ColorPickerPortlet;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.table.WebCellFormatter;
import com.f1.utils.CH;
import com.f1.utils.ColorHelper;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.IndexedList;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public abstract class AmiWebChartEditSeriesPortlet<T extends AmiWebChartSeries> extends GridPortlet
		implements FormPortletListener, FormPortletContextMenuListener, FormPortletContextMenuFactory, ColorPickerListener {

	public static final String TYPE_3D_ADVANCED = "3dAdv";
	public static final String TYPE_2D_ADVANCED = "2dAdv";
	public static final String TYPE_2D_BAR_V = "2dBarV";
	public static final String TYPE_2D_BAR_H = "2dBarH";
	public static final String TYPE_2D_LINE = "2dLine";
	public static final String TYPE_2D_SCATTER = "2dScatter";
	public static final String TYPE_2D_AREA = "2dArea";
	public static final String TYPE_RADIAL_ADVANCED = "raAdv";
	public static final String TYPE_RADIAL_PIE = "raPie";
	public static final String TYPE_RADIAL_BAR = "raBar";
	public static final String TYPE_RADIAL_SPEEDOMETER = "raSpeed";

	final protected FormPortlet form;
	protected T existing;
	protected AmiWebChartSeriesContainer<T> container;
	protected boolean isNew;
	private boolean hadPreview;
	final protected FormPortletButton previewButton;
	final protected FormPortletButton addButton;
	final protected FormPortletButton cancelButton;
	final private AmiWebService service;
	private int pos = 25;

	protected int incrementPosition(int inc) {
		return this.pos += inc;
	}

	private Map<String, AmiWebChartEditFormula> formulaEditors = new LinkedHashMap<String, AmiWebChartEditFormula>();
	protected FormPortletButtonField viewDataButton;
	protected FormPortletButtonField testDataButton;

	public AmiWebChartEditSeriesPortlet<T> setContainer(AmiWebChartSeriesContainer<T> container, T series) {
		this.container = container;
		this.existing = series;
		this.isNew = series.getId() == 0;
		this.initForm();
		return this;
	}
	public AmiWebChartEditSeriesPortlet(PortletConfig config) {
		super(config);
		this.service = AmiWebUtils.getService(getManager());
		this.form = new FormPortlet(generateConfig());
		this.viewDataButton = this.form.addField(new FormPortletButtonField("")).setValue("View Underlying Data");
		this.testDataButton = this.form.addField(new FormPortletButtonField("")).setValue("View Prepared Data");
		viewDataButton.setLeftTopWidthHeightPx(120, 5, 130, 20);
		testDataButton.setLeftTopWidthHeightPx(260, 5, 130, 20);
		this.form.getFormPortletStyle().setLabelsWidth(150);

		this.addChild(form, 0, 0);
		this.form.addFormPortletListener(this);
		this.previewButton = this.form.addButton(new FormPortletButton("Apply"));
		if (isNew) {
			this.addButton = this.form.addButton(new FormPortletButton("Add"));
		} else {
			this.addButton = this.form.addButton(new FormPortletButton("Update"));
		}
		this.cancelButton = this.form.addButton(new FormPortletButton("Cancel"));
		showButtons(false);
		this.form.addMenuListener(this);
		this.setSuggestedSize(650, 900);
		this.form.setMenuFactory(this);
	}
	protected void initForm() {
		String group = null;
		for (int i = 0; i < this.existing.getFormulasCount(); i++) {
			AmiWebChartFormula f = this.existing.getFormulaAt(i);
			if (OH.ne(group, f.getLabelGroup())) {
				addTitleField(group = f.getLabelGroup());
			}
			addField(f);
		}

	}

	public void updateFields() {
		for (AmiWebChartEditFormula i : this.formulaEditors.values())
			i.resetFromFormula();
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == cancelButton) {
			close();
		} else if (button == addButton || button == previewButton) {
			preview();
			if (button == addButton)
				close();
		}
	}

	public AmiWebChartEditSeriesPortlet<?> showButtons(boolean show) {
		this.form.clearButtons();
		if (show) {
			this.form.addButton(this.previewButton);
			this.form.addButton(this.addButton);
			this.form.addButton(this.cancelButton);
		}
		return this;
	}
	public AmiWebChartEditSeriesPortlet<?> showCloseButtons(boolean show) {
		this.form.clearButtons();
		this.form.addButton(this.previewButton);
		if (show) {
			this.form.addButton(this.addButton);
			this.form.addButton(this.cancelButton);
		}
		return this;
	}

	public boolean preview() {
		if (!testRequiredFields())
			return false;
		StringBuilder sb = new StringBuilder();
		for (AmiWebChartEditFormula i : this.formulaEditors.values()) {
			if (!i.test(sb)) {
				getManager().showAlert("<b> " + this.existing.getName() + "</b>: " + sb.toString());
				return false;
			}
		}
		for (AmiWebChartEditFormula i : this.formulaEditors.values())
			i.applyValue();
		for (int i = 0, l = this.existing.getFormulasCount(); i < l; i++) {
			AmiWebChartFormula f = this.existing.getFormulaAt(i);
			if (!this.formulaEditors.containsKey(f.getName()))
				f.setValue(null);//TODO: alert user first
		}
		this.container.setSeries(existing);
		existing.fireOnDataChanged();
		hadPreview = true;

		//the type has changed, so lets re-establish the container itself
		prepareContainer(this.container);
		this.existing.setEditorTypeId(this.getEditorTypeId());
		this.container.flagNeedsRepaint();
		return true;
	}
	protected void prepareContainer(AmiWebChartSeriesContainer<T> container2) {
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field.getCorrelationData() instanceof AmiWebChartEditFormula_Color) {
			((AmiWebChartEditFormula_Color) field.getCorrelationData()).onFieldChanged(field);
		}
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField field) {
		if (field == this.viewDataButton) {
			AmiWebDmViewDataPortlet vdp = new AmiWebDmViewDataPortlet(generateConfig());
			AmiWebDmsImpl dm = service.getDmManager().getDmByAliasDotName(this.existing.getDmAliasDotName());
			vdp.addDmToMultiDividerPortlet(dm, null, true, AmiWebDmViewDataPortlet.RESPONSEDATA_AFTER_FILTER);
			getManager().showDialog("Underlying Data", vdp);
		} else if (field == this.testDataButton) {
			showTestData();
		} else {
			AmiWebChartEditFormula cb = (AmiWebChartEditFormula) field.getCorrelationData();
			cb.onContextMenu(field, action);
		}

	}

	private void showTestData() {
		if (!preview())
			return;

		Table table = new ColumnarTable();
		table.setTitle("Layer Formula Results");
		List<AmiWebChartFormula> formulas = new ArrayList<AmiWebChartFormula>();
		List<WebCellFormatter> formatters = new ArrayList<WebCellFormatter>();
		for (int i = 0, l = existing.getFormulasCount(); i < l; i++) {
			AmiWebChartFormula formula = existing.getFormulaAt(i);
			Class<?> returnType = formula.getReturnType();
			if (returnType == null)
				continue;
			if (formula.isReturnTypeColor()) {
				returnType = String.class;
				formatters.add(this.service.getFormatterManager().getColorWebCellFormatter());
			} else
				formatters.add(null);
			table.addColumn(returnType, formula.getLabelGroup() + " - " + SH.beforeFirst(SH.stripSuffix(formula.getLabel(), ":", false), "("));
			formulas.add(formula);
		}
		IndexedList<String, Grouping> groupings = existing.getUserSelectedGroupings();
		for (Grouping g : groupings.values()) {
			Row[] rows = new Row[g.getSize()];
			for (int i = 0; i < g.getSize(); i++)
				rows[i] = table.newEmptyRow();
			for (int x = 0, l = formulas.size(); x < l; x++) {
				AmiWebChartFormula formula = formulas.get(x);
				g.getSize();
				if (formula.getIsHidden()) {
					ReusableCalcFrameStack rsf = new ReusableCalcFrameStack(this.getSeries().getPortlet().getStackFrame());
					for (int y = 0; y < rows.length; y++) {
						rows[y].putAt(x, formula.getData(rsf.reset(g.getOrigRows().get(y))));
					}
				} else {
					List values = g.getValuesForFormula(formula.getName());
					if (formula.isReturnTypeColor() && existing.getLayer() != null) {
						Color[] t = existing.getLayer() == null ? null : (existing.getLayer().getColors((AmiWebChartFormula_Color) formula, values));
						if (t != null) {
							values = new ArrayList<Object>(values);
							for (int i = 0; i < values.size(); i++)
								if (t[i] != null)
									values.set(i, ColorHelper.toString(t[i]));
						}

					}
					if (CH.isntEmpty(values)) {
						if (values.size() == 1) {
							Object value = values.get(0);
							for (int y = 0; y < rows.length; y++) {
								rows[y].putAt(x, value);
							}
						} else {
							for (int y = 0; y < rows.length; y++) {
								rows[y].putAt(x, values.get(y));
							}
						}
					}
				}
			}
			for (Row row : rows)
				table.getRows().add(row);
		}
		AmiWebDmViewDataPortlet vdp = new AmiWebDmViewDataPortlet(generateConfig());
		vdp.addTable(table, formatters);
		getManager().showDialog("Prepared Data", vdp);
	}
	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		AmiWebChartEditFormula t = (AmiWebChartEditFormula) field.getCorrelationData();
		return t.createMenu(formPortlet, field, cursorPosition);
	}
	public FormPortlet getForm() {
		return form;
	}
	@Override
	public void onColorChanged(ColorPickerPortlet target, String oldColor, String nuwColor) {
	}
	@Override
	public void onOkayPressed(ColorPickerPortlet target) {
		FormPortletTextField field = (FormPortletTextField) target.getCorrelationData();
		String color = "\"" + target.getColor() + "\"";
		field.insertAtCursor(color);
		target.close();
	}
	@Override
	public void onCancelPressed(ColorPickerPortlet target) {
		target.close();

	}
	public boolean getHadPreview() {
		return hadPreview;
	}
	@Override
	public boolean onUserKeyEvent(KeyEvent keyEvent) {
		if (KeyEvent.ENTER.equals(keyEvent.getKey())) {
			preview();
			return true;
		}
		return super.onUserKeyEvent(keyEvent);
	}

	public abstract String getEditorLabel();
	public abstract String getEditorTypeId();

	protected AmiWebChartEditFormula_Simple addPredefined(AmiWebChartFormula_Simple formula) {
		return (AmiWebChartEditFormula_Simple) addField(formula, false);
	}
	protected AmiWebChartEditFormula_Color addPredefined(AmiWebChartFormula_Color formula) {
		return (AmiWebChartEditFormula_Color) addField(formula, false);
	}
	protected AmiWebChartEditFormula addField(AmiWebChartFormula formula) {
		return addField(formula, true);
	}
	protected AmiWebChartEditFormula_Simple addRequiredField(AmiWebChartFormula_Simple formula) {
		AmiWebChartEditFormula r = addField(formula, true);
		r.setRequired(true);
		return (AmiWebChartEditFormula_Simple) r;
	}
	protected AmiWebChartEditFormula_Color addRequiredField(AmiWebChartFormula_Color formula) {
		AmiWebChartEditFormula r = addField(formula, true);
		r.setRequired(true);
		return (AmiWebChartEditFormula_Color) r;
	}
	protected AmiWebChartEditFormula addField(AmiWebChartFormula formula, boolean visible) {
		AmiWebChartEditFormula ff;
		int pos = visible ? this.pos : -1;
		if (formula.getType() == AmiWebChartSeries.TYPE_COLOR) {
			ff = new AmiWebChartEditFormula_Color(pos, this, (AmiWebChartFormula_Color) formula);
		} else if (formula.getType() == AmiWebChartSeries_Graph.TYPE_LINE_TYPE)
			ff = new AmiWebChartEditFormula_LineType(pos, this, (AmiWebChartFormula_Simple) formula);
		else
			ff = new AmiWebChartEditFormula_Simple(pos, this, (AmiWebChartFormula_Simple) formula);
		if (visible)
			this.pos += 20 + 4;
		CH.putOrThrow(this.formulaEditors, ff.getFormula().getName(), ff);
		return ff;
	}

	public AmiWebChartEditFormula_Simple getEditor(AmiWebChartFormula_Simple f) {
		return (AmiWebChartEditFormula_Simple) this.formulaEditors.get(f.getName());
	}
	public AmiWebChartEditFormula_Color getEditor(AmiWebChartFormula_Color f) {
		return (AmiWebChartEditFormula_Color) this.formulaEditors.get(f.getName());
	}

	protected void addTitleField(String string) {
		FormPortletTitleField r = new FormPortletTitleField(string);
		pos += 4;
		r.setTopPosPx(pos).setRightPosPx(15).setLeftPosPx(120).setHeight(20);
		pos += 20 + 4;
		form.addField(r);
	}

	public boolean testRequiredFields() {
		for (AmiWebChartEditFormula<?> f : this.formulaEditors.values()) {
			if (f.getRequired() && !f.isPopulated()) {
				String title = f.getTitle();
				String msg = "The field \"" + title + "\" is a required field. Please enter a value.";
				getManager().showDialog("Required Field", new ConfirmDialogPortlet(generateConfig(), msg, ConfirmDialogPortlet.TYPE_MESSAGE));
				return false;
			}
		}
		return true;
	}
	protected static String getDefaultNumberFormatFormula(String varname, AmiWebChartFormula formula) {
		return isDate(varname, formula) ? "formatDate(" + varname + ")" : "formatNumber(" + varname + ", \"#.000\", \"\")";
	}
	protected static boolean isDate(String varname, AmiWebChartFormula formula) {
		Class<?> variableReturnType = formula.getVariableReturnType(varname);
		return variableReturnType == DateMillis.class || variableReturnType == DateNanos.class;
	}
	public AmiWebService getService() {
		return this.service;
	}
	public AmiWebChartSeriesContainer<T> getContainer() {
		return this.container;
	}
	public T getSeries() {
		return this.existing;
	}

	abstract public void fillDefaultFields();

	// for 3D chart
	public void setUserRequiredFields(String[] fieldNames) {
		Map<String, AmiWebChartEditFormula> allFields = this.formulaEditors;
		for (String fieldName : fieldNames) {
			if (allFields.containsKey(fieldName)) {
				AmiWebChartEditFormula amiWebChartEditFormula = allFields.get(fieldName);
				amiWebChartEditFormula.setRequired(true);
			}
		}

	}

}
