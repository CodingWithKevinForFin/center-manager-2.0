package com.f1.ami.web.tree;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.portlets.AmiWebHeaderPortlet;
import com.f1.ami.web.AmiWebCustomColumn;
import com.f1.ami.web.AmiWebMenuUtils;
import com.f1.ami.web.AmiWebScriptManagerForLayout;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.FormExportPortlet;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.portal.impl.ColorPickerListener;
import com.f1.suite.web.portal.impl.ColorPickerPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.form.BasicFormPortletExportImportManager;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.sql.aggs.AggregateFactory;

public class AmiWebTreeEditColumnPortlet extends GridPortlet implements FormPortletContextMenuFactory, FormPortletContextMenuListener, FormPortletListener, ColorPickerListener {

	private FormPortlet form;

	private FormPortletButton okayButton;
	private FormPortletButton cancelButton;
	private FormPortletButton importerExporter;
	private FormPortletTextField valueField;
	private FormPortletTextField orderByField;
	private FormPortletTextField nameField;
	private FormPortletTextField idField;
	private FormPortletTextField colorField;
	private FormPortletTextField styleField;
	private FormPortletNumericRangeField decimalsField;
	private AmiWebTreePortlet target;
	private AmiWebTreeColumn existing;
	private FormPortletSelectField<Integer> positionField;
	private FastWebTreeColumn column;
	private final AmiWebHeaderPortlet header;
	private boolean changed = false;

	private FormPortletTextField backgroundField;

	private boolean isCopy;

	private FormPortletTextField helpField;

	final private FormPortletSelectField<Byte> formatField;
	private FormPortletTextField headerStyleField;

	private static final int HEADER_ROW_SIZE = 100;

	public AmiWebTreeEditColumnPortlet(AmiWebTreePortlet portlet, AmiWebTreeColumn amiWebTreeColumn, int position, boolean isCopy) {
		super(portlet.generateConfig());
		this.isCopy = isCopy;
		this.target = portlet;
		this.form = new FormPortlet(generateConfig());
		this.header = new AmiWebHeaderPortlet(generateConfig());
		this.header.setShowSearch(false);
		//		this.header.setCssStyle("Custom AMI Column");
		this.header.updateBlurbPortletLayout("Custom AMI Tree Column", "");
		this.header.setShowLegend(false);
		this.header.setInformationHeaderHeight(115);
		this.header.setShowBar(false);
		addChild(this.header);
		addChild(form, 0, 1);
		this.existing = amiWebTreeColumn;
		form.addField(new FormPortletTitleField("Cell Contents"));
		valueField = form.addField(new FormPortletTextField("Display:").setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true)).setName(AmiWebCustomColumn.KEY_DISPLAY);
		valueField.focus();
		orderByField = form.addField(new FormPortletTextField("Sorting:").setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true)).setName(AmiWebCustomColumn.KEY_SORTING);
		this.formatField = form.addField(new FormPortletSelectField<Byte>(Byte.class, "Format:")).setName(AmiWebCustomColumn.KEY_FORMAT);
		for (Entry<Byte, String> e : AmiWebUtils.CUSTOM_COL_DESCRIPTIONS.entrySet())
			formatField.addOption(e.getKey(), e.getValue());
		this.decimalsField = new FormPortletNumericRangeField("Decimal Override:").setRange(0, 8).setValue(0).setDecimals(0).setNullable(true);
		form.addFieldAfter(this.formatField, this.decimalsField);
		form.addField(new FormPortletTitleField("Column Header"));
		nameField = form.addField(new FormPortletTextField("Title:").setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(false)).setName(AmiWebCustomColumn.KEY_TITLE);
		idField = form.addField(new FormPortletTextField("Column Id:").setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(false)).setName(AmiWebCustomColumn.KEY_TITLE);
		helpField = form.addField(new FormPortletTextField("Description:")).setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(false)
				.setName(AmiWebCustomColumn.KEY_DESCRIPTION);
		//add header style field GUI
		headerStyleField = form.addField(new FormPortletTextField("Header Style:")).setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true)
				.setName(AmiWebCustomColumn.KEY_HEADER_SYTLE);
		positionField = form.addField(new FormPortletSelectField<Integer>(Integer.class, "Column Position:")).setName(AmiWebCustomColumn.KEY_POSITION);

		form.addField(new FormPortletTitleField("Formatting (optional)"));
		styleField = form.addField(new FormPortletTextField("Style:").setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true)).setName(AmiWebCustomColumn.KEY_STYLE);
		colorField = form.addField(new FormPortletTextField("Foreground Color:").setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true))
				.setName(AmiWebCustomColumn.KEY_FG_CL);
		backgroundField = form.addField(new FormPortletTextField("Background Color:").setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true))
				.setName(AmiWebCustomColumn.KEY_BG_CL);

		FastWebTree tree = target.getTree();
		int cnt = tree.getVisibleColumnsCount();
		positionField.addOption(0, "1) Left Most");
		for (int i = 2; i < cnt + 1; i++) {
			positionField.addOption(i - 1, (i) + ") Between '" + tree.getVisibleColumn(i - 1).getColumnName() + "' and '" + tree.getVisibleColumn(i).getColumnName() + "'");
		}
		if (cnt > 0)
			positionField.addOption(cnt, (cnt + 1) + ") Right Most");
		positionField.addOption(-1, "<Hidden Column>");
		positionField.setValue(position);

		setSuggestedSize(700, 700);
		if (amiWebTreeColumn != null) {
			AmiWebTreeGroupByFormatter f = amiWebTreeColumn.getFormatter();
			this.column = tree.getColumn(amiWebTreeColumn.getColumnId());
			this.valueField.setValue(f.getDescription(false));
			this.orderByField.setValue(f.getOrderBy(false));
			if (isCopy)
				nameField.setValue("Copy of " + this.column.getColumnName());
			else
				nameField.setValue(this.column.getColumnName());
			idField.setValue(amiWebTreeColumn.getAmiId());
			helpField.setValue(this.column.getHelp());
			headerStyleField.setValue(amiWebTreeColumn.getHeaderStyle());
			colorField.setValue(f.getColor(false));
			backgroundField.setValue(f.getBackgroundColor(false));
			styleField.setValue(f.getStyle(false));
			int columnPosition = tree.getColumnPosition(amiWebTreeColumn.getColumnId());
			positionField.setValue(columnPosition == -1 ? -1 : columnPosition - 1);
			formatField.setValue(this.existing.getFormatterType());
			if (this.existing.getDecimals() == AmiConsts.DEFAULT)
				decimalsField.setValue(null);
			else
				decimalsField.setValue(this.existing.getDecimals());
		}
		updateFormat();
		form.setMenuFactory(this);
		form.addMenuListener(this);
		form.addFormPortletListener(this);
		okayButton = form.addButton(new FormPortletButton("Submit"));
		cancelButton = form.addButton(new FormPortletButton("Cancel"));
		this.importerExporter = this.form.addButton(new FormPortletButton("Export/Import"));
		//		this.onFieldValueChanged(form, this.formatField, null);
	}

	public AmiWebTreeEditColumnPortlet hideCloseButtons(boolean hide) {
		this.form.clearButtons();
		this.form.addButton(this.okayButton);
		if (!hide)
			this.form.addButton(this.cancelButton);
		return this;
	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		AmiWebMenuUtils.processContextMenuAction(target.getService(), action, node);
	}
	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {

		BasicWebMenu r = new BasicWebMenu();
		if (field == this.headerStyleField) {
			AmiWebMenuUtils.createFormatsMenu(r, AmiWebUtils.getService(this.getManager()));
			AmiWebMenuUtils.createColorsMenu(r, this.target.getStylePeer());
		} else {
			WebMenu vars = AmiWebMenuUtils.createVariablesMenu(r, false, target);
			vars.add(new BasicWebMenuDivider());
			AmiWebTreeEditGroupingsPortlet.addSpecialVars(vars);
			r.add(vars);

			AmiWebMenuUtils.createOperatorsMenu(r, target.getService(), target.getAmiLayoutFullAlias());
			AmiWebMenuUtils.createAggOperatorsMenu(r, false);
			if (field == this.backgroundField || field == this.colorField) {
				AmiWebMenuUtils.createColorsMenu(r, this.target.getStylePeer());
			}
			if (field == this.styleField)
				AmiWebMenuUtils.createFormatsMenu(r, AmiWebUtils.getService(this.getManager()));
		}
		return r;
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {

		if (button == this.cancelButton)
			close();
		else if (button == this.okayButton) {
			boolean okay = onUpdate();
			if (okay && this.getParent() instanceof RootPortlet)
				close();
		} else if (button == this.importerExporter) {
			getManager().showDialog("Export/Import",
					new FormExportPortlet(this.form, new BasicFormPortletExportImportManager((Map) CH.m(AmiWebCustomColumn.KEY_POSITION, null)), false));
		}
	}
	public boolean onUpdate() {
		// this will get fired: 
		// 1. if you click on any row in the tree grid (excluding the column header)
		// 2. if you hit submit on the column editor (not the submit button in bottom center)
		this.changed = false;
		StringBuilder errorSink = new StringBuilder();
		AmiWebScriptManagerForLayout sm = target.getScriptManager();
		AggregateFactory af = sm.createAggregateFactory();
		try {
			sm.toAggCalc(valueField.getValue(), target.getFormulaVarTypes(null), af, target, new HashSet<String>());
		} catch (Exception e) {
			getManager().showAlert("Error for display field: " + e.getMessage(), e);
			return false;
		}
		try {
			sm.toAggCalc(orderByField.getValue(), target.getFormulaVarTypes(null), af, target, new HashSet<String>());
		} catch (Exception e) {
			getManager().showAlert("Error for Sorting field: " + e.getMessage(), e);
			return false;
		}
		try {
			sm.toAggCalc(styleField.getValue(), target.getFormulaVarTypes(null), af, target, new HashSet<String>());
		} catch (Exception e) {
			getManager().showAlert("Error for Style field: " + e.getMessage(), e);
			return false;
		}
		try {
			sm.toAggCalc(colorField.getValue(), target.getFormulaVarTypes(null), af, target, new HashSet<String>());
		} catch (Exception e) {
			getManager().showAlert("Error for Color field: " + e.getMessage(), e);
			return false;
		}
		try {
			sm.toAggCalc(backgroundField.getValue(), target.getFormulaVarTypes(null), af, target, new HashSet<String>());
		} catch (Exception e) {
			getManager().showAlert("Error for Background Color field: " + e.getMessage(), e);
			return false;
		}
		String help = helpField.getValue();
		String name = nameField.getValue();
		String headerStyle = headerStyleField.getValue();
		if (SH.isnt(name))
			name = SH.ddd(SH.trim(valueField.getValue()), 20);
		int colId = (existing == null || isCopy) ? -1 : existing.getColumnId();
		String amiId = this.idField.getValue();
		Integer decimals = decimalsField.getForm() != null ? decimalsField.getIntValue() : null;
		if (target.addColumnFormula(colId, amiId, name, valueField.getValue(), orderByField.getValue(), styleField.getValue(), colorField.getValue(), backgroundField.getValue(),
				formatField.getValue(), decimals == null ? AmiConsts.DEFAULT : decimals, positionField.getValue(), errorSink, help, headerStyle, true) == null) {
			getManager().showAlert(errorSink.toString());
			return false;
		}
		return true;

	}

	public void updateFormat() {
		this.decimalsField.setVisible(false);
		switch (formatField.getValue()) {
			case AmiWebUtils.CUSTOM_COL_TYPE_NUMERIC:
				this.decimalsField.setVisible(true);
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_PRICE:
				this.decimalsField.setVisible(true);
				if (isZero(this.decimalsField.getValue()))
					this.decimalsField.setValue(2);
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_PERCENT:
				this.decimalsField.setVisible(true);
				if (isZero(this.decimalsField.getValue()))
					this.decimalsField.setValue(2);
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_PROGRESS:
				this.decimalsField.setVisible(true);
				if (isZero(this.decimalsField.getValue()))
					this.decimalsField.setValue(2);
				if (SH.isnt(this.backgroundField.getValue()))
					this.backgroundField.setValue("\"#77EE77\"");
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_TEXT:
			case AmiWebUtils.CUSTOM_COL_TYPE_DATE:
			case AmiWebUtils.CUSTOM_COL_TYPE_TIME_SEC:
			case AmiWebUtils.CUSTOM_COL_TYPE_TIME_MILLIS:
			case AmiWebUtils.CUSTOM_COL_TYPE_TIME_MICROS:
			case AmiWebUtils.CUSTOM_COL_TYPE_TIME_NANOS:
			case AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_SEC:
			case AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_MILLIS:
			case AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_MICROS:
			case AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_NANOS:
				break;
		}
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.formatField) {
			updateFormat();
		}
		if (!changed)
			this.changed = true;
	}
	private static boolean isZero(Double value) {
		return value != null && value == 0d;
	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
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
	}

	public AmiWebTreeEditColumnPortlet hideHeader(boolean hide) {
		if (hide)
			setRowSize(0, 0);
		else
			setRowSize(0, HEADER_ROW_SIZE);
		return this;
	}

	public FormPortlet getForm() {
		return form;
	}

	public boolean isChanged() {
		return changed;
	}

	public AmiWebTreeColumn getExisting() {
		return existing;
	}
}
