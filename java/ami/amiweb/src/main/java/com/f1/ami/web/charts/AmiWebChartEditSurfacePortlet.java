package com.f1.ami.web.charts;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.web.AmiWebMenuUtils;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.surface.AmiWebSurfaceSeries;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ColorPickerListener;
import com.f1.suite.web.portal.impl.ColorPickerPortlet;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.CH;
import com.f1.utils.SH;

public abstract class AmiWebChartEditSurfacePortlet<T extends AmiWebSurfaceSeries> extends GridPortlet
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
	protected List<FormPortletField<?>> requiredFields = new ArrayList<FormPortletField<?>>();

	final protected FormPortletButton previewButton;
	final protected FormPortletButton addButton;
	final protected FormPortletButton cancelButton;

	public AmiWebChartEditSurfacePortlet<T> setContainer(AmiWebChartSeriesContainer<T> container, T series) {
		this.container = container;
		this.existing = series;
		this.isNew = series.getId() == 0;
		this.initForm();
		return this;
	}
	public AmiWebChartEditSurfacePortlet(PortletConfig config) {
		super(config);
		this.form = new FormPortlet(generateConfig());
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
	protected abstract void initForm();

	protected void initField(FormPortletTextField field, AmiWebChartFormula formula) {
		field.setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true);
		field.setDefaultValue(formula.getValue());
		field.setCssStyle("_fm=courier");
		field.setCorrelationData(formula);
		if ("tooltip".equals(formula.getName())) {
			handleSpecialFormula(formula.getName(), field);
		}

		addFormulaFiller(new FormulaFiller(field.getName(), formula, field));
	}

	public void updateFields() {
		for (FormulaFiller i : this.formulaFillers.values())
			if (i.field != null)
				i.field.setValue(i.getFormula().getValue());
	}
	private void handleSpecialFormula(String name, FormPortletTextField field) {
		if ("tooltip".equals(name)) {
			field.setMaxChars(4048);
		}

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

	public AmiWebChartEditSurfacePortlet<?> showButtons(boolean show) {
		this.form.clearButtons();
		if (show) {
			this.form.addButton(this.previewButton);
			this.form.addButton(this.addButton);
			this.form.addButton(this.cancelButton);
		}
		return this;
	}
	public AmiWebChartEditSurfacePortlet<?> showCloseButtons(boolean show) {
		this.form.clearButtons();
		this.form.addButton(this.previewButton);
		if (show) {
			this.form.addButton(this.addButton);
			this.form.addButton(this.cancelButton);
		}
		return this;
	}

	public boolean preview() {
		StringBuilder sb = new StringBuilder();
		for (FormulaFiller i : this.formulaFillers.values()) {
			if (i.field == null)
				continue;
			String value = i.getValue();
			AmiWebChartFormula formula = i.formula;
			if (!formula.testValue(value, sb)) {
				getManager().showAlert(sb.toString());
				return false;
			}
		}
		for (FormulaFiller i : this.formulaFillers.values()) {
			String value = i.getValue();
			AmiWebChartFormula formula = (AmiWebChartFormula) i.formula;
			formula.setValue(value);
		}
		for (int i = 0, l = this.existing.getFormulasCount(); i < l; i++) {
			AmiWebChartFormula f = this.existing.getFormulaAt(i);
			if (!this.formulaFillers.containsKey(f.getName()))
				f.setValue("");//TODO: alert user first
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
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField field) {
		AmiWebMenuUtils.processContextMenuAction(AmiWebUtils.getService(getManager()), action, field);
	}

	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		AmiWebChartFormula formula = (AmiWebChartFormula) field.getCorrelationData();
		if (formula != null) {
			BasicWebMenu r = new BasicWebMenu();
			switch (formula.getType()) {
				case AmiWebChartSeries.TYPE_COLOR:
					AmiWebMenuUtils.createColorsMenu(r, this.container.getOwner().getStylePeer());
					break;
				case AmiWebChartSeries.TYPE_SHAPE:
					WebMenu shapes = new BasicWebMenu("Shapes", true);
					if (this.existing.getSeriesType() == AmiWebChartSeries.SERIES_TYPE_RADIAL)
						shapes.add(new BasicWebMenuLink("wedge", true, "co_wedge"));
					if (this.existing.getSeriesType() != AmiWebChartSeries.SERIES_TYPE_SURFACE)
						shapes.add(new BasicWebMenuLink("circle", true, "co_circle"));
					else
						shapes.add(new BasicWebMenuLink("diamond", true, "co_diamond"));
					shapes.add(new BasicWebMenuLink("square", true, "co_square"));
					shapes.add(new BasicWebMenuLink("triangle", true, "co_triangle"));
					if (this.existing.getSeriesType() == AmiWebChartSeries.SERIES_TYPE_XY) {
						shapes.add(new BasicWebMenuLink("Horizontal Bar", true, "co_hbar"));
						shapes.add(new BasicWebMenuLink("Vertical Bar", true, "co_vbar"));
					}
					r.add(shapes);
					break;
				case AmiWebChartSeries.TYPE_POSITION:
					WebMenu positions = new BasicWebMenu("Positions", true);
					positions.add(new BasicWebMenuLink("center", true, "co_center"));
					positions.add(new BasicWebMenuLink("top", true, "co_top"));
					positions.add(new BasicWebMenuLink("bottom", true, "co_bottom"));
					positions.add(new BasicWebMenuLink("left", true, "co_left"));
					positions.add(new BasicWebMenuLink("right", true, "co_right"));
					r.add(positions);
					break;
			}

			WebMenu variables = createVariablesMenu("Variables", "", this.existing.getDataModelSchema());
			r.add(variables);
			AmiWebMenuUtils.createAggOperatorsMenu(r, false);

			if (formula != existing.getNameFormula()) {
				variables.add(new BasicWebMenuDivider());
				variables.add(new BasicWebMenuLink(AmiWebChartSeries.VARNAME_ROWNUM + " (Row Number)", true, "var_" + AmiWebChartSeries.VARNAME_ROWNUM).setAutoclose(false)
						.setCssStyle("_fm=courier"));
				variables.add(new BasicWebMenuLink(AmiWebChartSeries.VARNAME_SERIESNUM + " (Series Number)", true, "var_" + AmiWebChartSeries.VARNAME_SERIESNUM).setAutoclose(false)
						.setCssStyle("_fm=courier"));
				variables.add(new BasicWebMenuLink(AmiWebChartSeries.VARNAME_SERIESCNT + " (Series Count)", true, "var_" + AmiWebChartSeries.VARNAME_SERIESCNT).setAutoclose(false)
						.setCssStyle("_fm=courier"));
			}
			AmiWebMenuUtils.createOperatorsMenu(r, AmiWebUtils.getService(getManager()), this.container.getAmiLayoutFullAlias());
			return r;
		} else
			return null;
	}
	public static WebMenu createVariablesMenu(String menuName, String prefix, AmiWebDmTableSchema amiWebDmTableSchema) {
		WebMenu variables = new BasicWebMenu(menuName, true);

		variables.add(new BasicWebMenuDivider());

		Set<String> columns = amiWebDmTableSchema.getColumnNames();
		for (String column : CH.sort(columns, SH.COMPARATOR_CASEINSENSITIVE_STRING)) {
			variables.add(new BasicWebMenuLink(column, true, "var_" + prefix + column).setAutoclose(false).setCssStyle("_fm=courier"));
		}
		return variables;
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

	protected void addPredefined(String name, AmiWebChartFormula formula, String value) {
		addFormulaFiller(new FormulaFiller(name, formula, value));
	}

	protected void addPredefined(String name, AmiWebChartFormula formula, AmiWebChartFormula value) {
		addFormulaFiller(new FormulaFiller(name, formula, value));

	}
	protected void addFormulaFiller(FormulaFiller ff) {
		CH.putOrThrow(this.formulaFillers, ff.formula.getName(), ff);
	}

	protected FormPortletTextField addField(String name, AmiWebChartFormula formula) {
		FormPortletTextField field = form.addField(new FormPortletTextField(name));
		initField(field, formula);
		return field;
	}

	protected void addTitleField(String string) {
		form.addField(new FormPortletTitleField(string));
	}

	private Map<String, FormulaFiller> formulaFillers = new LinkedHashMap<String, FormulaFiller>();

	private static class FormulaFiller {
		private final String label, value;
		private final AmiWebChartFormula formula;
		private final AmiWebChartFormula formulaValue;
		private final FormPortletTextField field;

		public FormulaFiller(String label, AmiWebChartFormula formula, String value) {
			super();
			this.label = label;
			this.value = value;
			this.formula = formula;
			this.formulaValue = null;
			this.field = null;
		}
		public FormulaFiller(String label, AmiWebChartFormula formula, AmiWebChartFormula value) {
			super();
			this.label = label;
			this.value = null;
			this.formula = formula;
			this.formulaValue = value;
			this.field = null;
		}
		public FormulaFiller(String label, AmiWebChartFormula formula, FormPortletTextField field) {
			super();
			this.label = label;
			this.value = null;
			this.formula = formula;
			this.formulaValue = null;
			this.field = field;
		}
		public String getLabel() {
			return label;
		}
		public String getValue() {
			if (this.field != null)
				return this.field.getValue();
			return this.formulaValue != null ? this.formulaValue.getValue() : value;
		}
		public AmiWebChartFormula getFormula() {
			return formula;
		}
		public FormPortletTextField getField() {
			return this.field;
		}
	}

	public boolean testRequiredFields() {
		for (FormPortletField<?> f : this.form.getFormFields())
			if (this.requiredFields.contains(f) && SH.isnt(f.getValue())) {
				getManager().showDialog("Required Field", new ConfirmDialogPortlet(generateConfig(),
						"The field \"" + SH.beforeLast(f.getTitle(), ":") + "\" is a required field. Please enter a value.", ConfirmDialogPortlet.TYPE_MESSAGE));
				return false;
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
}
