package com.f1.suite.web.table.impl;

import java.util.Map;

import com.f1.suite.web.portal.BasicPortletDownload;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletMultiSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;
import com.f1.suite.web.portal.style.PortletStyleManager_Form;
import com.f1.suite.web.tree.impl.Copyable;

public class CopyPortlet extends GridPortlet implements FormPortletListener {

	private FormPortlet selectColumnsPortlet;
	private FormPortlet txtForm;
	private Copyable copyable;
	private FormPortlet buttonsForm;
	private FormPortletButton closeButton;
	private FormPortletButton downloadButton;
	private boolean isDownload;
	private FormPortlet formatPortlet;
	private FormPortletMultiSelectField<String> visibleColumnsField;
	private FormPortletMultiSelectField<String> hiddenColumnsField;
	private FormPortletTitleField mainTitleField;
	private FormPortletTextAreaField downloadPrepField;
	private FormPortletTextField colDelField;
	private FormPortletTextField rowDelField;
	private FormPortletSelectField<String> headerOptionsField;
	private FormPortletField<String> inlineDelimiterTitleField;
	private FormPortletTextField inlineDelimiterField;
	private FormPortletTitleField inlineEnclosedTitleField;
	private FormPortletTextField inlineEnclosedField;
	private FormPortletToggleButtonsField<Boolean> copyAllToggle;

	public CopyPortlet(PortletConfig config, Copyable c, boolean isDownload) {
		super(config);
		this.copyable = c;
		this.isDownload = isDownload;
		preparePortlets();
		prepareGrid();
		addListeners();
		copyable.populateTextArea(this);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button.equals(this.closeButton)) {
			copyable.saveTableCopyOptions(this);
			this.close();
		} else if (button.equals(this.downloadButton)) {
			getManager().pushPendingDownload(new BasicPortletDownload(copyable.getTitle() + ".txt", copyable.prepareDownload(this)));
		}
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (copyAllToggle.equals(field)) {
			this.isDownload = copyAllToggle.getValue();
		} else if (headerOptionsField.equals(field)) {
			if ("inline".equals(headerOptionsField.getValue())) {
				inlineDelimiterTitleField.setVisible(true);
				inlineDelimiterField.setVisible(true);
				inlineEnclosedTitleField.setVisible(true);
				inlineEnclosedField.setVisible(true);
			} else {
				inlineDelimiterTitleField.setVisible(false);
				inlineDelimiterField.setVisible(false);
				inlineEnclosedTitleField.setVisible(false);
				inlineEnclosedField.setVisible(false);
			}
		}
		copyable.populateTextArea(this);
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	private void addListeners() {
		selectColumnsPortlet.addFormPortletListener(this);
		buttonsForm.addFormPortletListener(this);
		formatPortlet.addFormPortletListener(this);
	}

	private void prepareGrid() {
		this.addChild(selectColumnsPortlet, 0, 0);
		this.addChild(txtForm, 1, 0);
		this.addChild(formatPortlet, 2, 0);
		this.addChild(buttonsForm, 0, 1, 3, 1);
		this.setRowSize(1, 35);
		this.setColSize(0, 220);
		this.setColSize(2, 220);
	}
	private void preparePortlets() {
		this.selectColumnsPortlet = new FormPortlet(generateConfig());
		FormPortletTitleField visibleColumnsTitle = selectColumnsPortlet.addField(new FormPortletTitleField("Visible Columns"));
		int leftPosPx = 10;
		int initialTopPosPx = 5;
		int fieldSpacingPx = 10;
		visibleColumnsTitle.setLeftPosPx(leftPosPx);
		visibleColumnsTitle.setTopPosPx(initialTopPosPx);
		visibleColumnsTitle.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		visibleColumnsTitle.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
		this.setVisibleColumnsField(selectColumnsPortlet.addField(new FormPortletMultiSelectField<String>(String.class, "")));
		copyable.populateVisibleColumnsField(getVisibleColumnsField());
		this.visibleColumnsField.setLeftPosPx(leftPosPx);
		this.visibleColumnsField.setTopPosPx(visibleColumnsTitle.getTopPosPx() + visibleColumnsTitle.getHeightPx() + fieldSpacingPx);
		this.visibleColumnsField.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.visibleColumnsField.setHeightPx(100);

		FormPortletTitleField hiddenColumnsTitle = selectColumnsPortlet.addField(new FormPortletTitleField("Hidden Columns"));
		hiddenColumnsTitle.setLeftPosPx(leftPosPx);
		hiddenColumnsTitle.setTopPosPx(this.visibleColumnsField.getTopPosPx() + this.visibleColumnsField.getHeightPx() + fieldSpacingPx);
		hiddenColumnsTitle.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		hiddenColumnsTitle.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
		this.setHiddenColumnsField(selectColumnsPortlet.addField(new FormPortletMultiSelectField<String>(String.class, "")));
		copyable.populateHiddenColumnsField(getHiddenColumnsField());
		this.hiddenColumnsField.setLeftPosPx(leftPosPx);
		this.hiddenColumnsField.setTopPosPx(hiddenColumnsTitle.getTopPosPx() + hiddenColumnsTitle.getHeightPx() + fieldSpacingPx);
		this.hiddenColumnsField.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.hiddenColumnsField.setHeightPx(100);
		FormPortletTitleField rowsToUseTitle = selectColumnsPortlet.addField(new FormPortletTitleField("Rows to use"));
		rowsToUseTitle.setLeftPosPx(leftPosPx);
		rowsToUseTitle.setTopPosPx(this.hiddenColumnsField.getTopPosPx() + this.hiddenColumnsField.getHeightPx() + fieldSpacingPx);
		rowsToUseTitle.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		rowsToUseTitle.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
		this.copyAllToggle = selectColumnsPortlet.addField(new FormPortletToggleButtonsField<Boolean>(Boolean.class, ""));
		this.copyAllToggle.addOption(true, "All");
		this.copyAllToggle.addOption(false, "Selected");
		this.copyAllToggle.setValue(isDownload);
		this.copyAllToggle.setLeftPosPx(leftPosPx);
		this.copyAllToggle.setTopPosPx(rowsToUseTitle.getTopPosPx() + rowsToUseTitle.getHeightPx() + fieldSpacingPx);
		this.copyAllToggle.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.copyAllToggle.setHeightPx(25);

		this.txtForm = new FormPortlet(generateConfig());
		this.setMainTitleField(txtForm.addField(new FormPortletTitleField("Selected Values")));
		this.mainTitleField.setLeftPosPx(leftPosPx);
		this.mainTitleField.setTopPosPx(initialTopPosPx);
		this.mainTitleField.setWidthPx(450);
		this.mainTitleField.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
		this.setDownloadPrepField(txtForm.addField(new FormPortletTextAreaField("")));
		this.downloadPrepField.setLeftPosPx(leftPosPx);
		this.downloadPrepField.setTopPosPx(this.mainTitleField.getTopPosPx() + this.mainTitleField.getHeightPx() + fieldSpacingPx);
		this.downloadPrepField.setWidthPx(630);
		this.downloadPrepField.setHeightPx(100);
		getDownloadPrepField().setHeight(250);
		txtForm.getFormPortletStyle().setLabelsWidth(1);

		buttonsForm = new FormPortlet(generateConfig());
		this.downloadButton = buttonsForm.addButton(new FormPortletButton("Download"));
		this.closeButton = buttonsForm.addButton(new FormPortletButton("Close"));
		getVisibleColumnsField().setValue(getVisibleColumnsField().getOptionKeys());
		this.formatPortlet = new FormPortlet(generateConfig());
		formatPortlet.getFormPortletStyle().setLabelsWidth(1);
		FormPortletTitleField columnDelimiterTitle = formatPortlet.addField(new FormPortletTitleField("Column Delimiter"));
		columnDelimiterTitle.setLeftPosPx(leftPosPx);
		columnDelimiterTitle.setTopPosPx(initialTopPosPx);
		columnDelimiterTitle.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		columnDelimiterTitle.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
		this.setColDelField(formatPortlet.addField(new FormPortletTextField("")));
		this.colDelField.setLeftPosPx(leftPosPx);
		this.colDelField.setTopPosPx(columnDelimiterTitle.getTopPosPx() + columnDelimiterTitle.getHeightPx() + fieldSpacingPx);
		this.colDelField.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.colDelField.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
		getColDelField().setValue(copyable.getColumnDelimiter());
		FormPortletTitleField rowDelimiterTitle = formatPortlet.addField(new FormPortletTitleField("Row Delimiter"));
		rowDelimiterTitle.setLeftPosPx(leftPosPx);
		rowDelimiterTitle.setTopPosPx(this.colDelField.getTopPosPx() + this.colDelField.getHeightPx() + fieldSpacingPx);
		rowDelimiterTitle.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		rowDelimiterTitle.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
		this.setRowDelField(formatPortlet.addField(new FormPortletTextField("")));
		this.rowDelField.setLeftPosPx(leftPosPx);
		this.rowDelField.setTopPosPx(rowDelimiterTitle.getTopPosPx() + rowDelimiterTitle.getHeightPx() + fieldSpacingPx);
		this.rowDelField.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.rowDelField.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
		getRowDelField().setValue(copyable.getRowDelimiter());
		FormPortletTitleField headerOptionsTitle = formatPortlet.addField(new FormPortletTitleField("Header Options"));
		headerOptionsTitle.setLeftPosPx(leftPosPx);
		headerOptionsTitle.setTopPosPx(this.rowDelField.getTopPosPx() + this.rowDelField.getHeightPx() + fieldSpacingPx);
		headerOptionsTitle.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		headerOptionsTitle.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
		this.setHeaderOptionsField(formatPortlet.addField(new FormPortletSelectField<String>(String.class, "")));
		this.headerOptionsField.setLeftPosPx(leftPosPx);
		this.headerOptionsField.setTopPosPx(headerOptionsTitle.getTopPosPx() + headerOptionsTitle.getHeightPx() + fieldSpacingPx);
		this.headerOptionsField.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.headerOptionsField.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
		getHeaderOptionsField().addOption("hot", "Header on top");
		getHeaderOptionsField().addOption("inline", "Inline");
		getHeaderOptionsField().addOption("none", "No header");
		getHeaderOptionsField().setValue(copyable.getHeaderOptions());
		this.inlineDelimiterTitleField = formatPortlet.addField(new FormPortletTitleField("Inline Delimiter")).setVisible("inline".equals(getHeaderOptionsField().getValue()));
		this.inlineDelimiterTitleField.setLeftTopWidthHeightPx(leftPosPx, this.headerOptionsField.getTopPosPx() + this.headerOptionsField.getHeightPx() + fieldSpacingPx,
				FormPortletField.DEFAULT_WIDTH, FormPortletField.DEFAULT_HEIGHT);
		this.setInlineDelimiterField(formatPortlet.addField(new FormPortletTextField("")));
		this.inlineDelimiterField.setLeftTopWidthHeightPx(leftPosPx, this.inlineDelimiterTitleField.getTopPosPx() + this.inlineDelimiterTitleField.getHeightPx() + fieldSpacingPx,
				FormPortletField.DEFAULT_WIDTH, FormPortletField.DEFAULT_HEIGHT);
		getInlineDelimiterField().setVisible("inline".equals(getHeaderOptionsField().getValue()));
		getInlineDelimiterField().setValue(copyable.getInlineDelimiter());
		this.inlineEnclosedTitleField = formatPortlet.addField(new FormPortletTitleField("Enclose strings with"));
		this.inlineEnclosedTitleField.setLeftTopWidthHeightPx(leftPosPx, this.inlineDelimiterField.getTopPosPx() + this.inlineDelimiterField.getHeightPx() + fieldSpacingPx,
				FormPortletField.DEFAULT_WIDTH, FormPortletField.DEFAULT_HEIGHT);
		inlineEnclosedTitleField.setVisible("inline".equals(getHeaderOptionsField().getValue()));
		this.setInlineEnclosedField(formatPortlet.addField(new FormPortletTextField("")));
		this.inlineEnclosedField.setLeftTopWidthHeightPx(leftPosPx, this.inlineEnclosedTitleField.getTopPosPx() + this.inlineEnclosedTitleField.getHeightPx() + fieldSpacingPx,
				FormPortletField.DEFAULT_WIDTH, FormPortletField.DEFAULT_HEIGHT);
		getInlineEnclosedField().setVisible("inline".equals(getHeaderOptionsField().getValue()));
		getInlineEnclosedField().setValue(copyable.getInlineEnclosed());
		formatPortlet.addFormPortletListener(this);
	}

	public boolean getIsDownload() {
		return isDownload;
	}

	public FormPortletMultiSelectField<String> getVisibleColumnsField() {
		return visibleColumnsField;
	}

	public void setVisibleColumnsField(FormPortletMultiSelectField<String> visibleColumnsField) {
		this.visibleColumnsField = visibleColumnsField;
	}

	public FormPortletMultiSelectField<String> getHiddenColumnsField() {
		return hiddenColumnsField;
	}

	public void setHiddenColumnsField(FormPortletMultiSelectField<String> hiddenColumnsField) {
		this.hiddenColumnsField = hiddenColumnsField;
	}

	public FormPortletTextAreaField getDownloadPrepField() {
		return downloadPrepField;
	}

	public void setDownloadPrepField(FormPortletTextAreaField downloadPrepField) {
		this.downloadPrepField = downloadPrepField;
	}

	public FormPortletSelectField<String> getHeaderOptionsField() {
		return headerOptionsField;
	}

	public void setHeaderOptionsField(FormPortletSelectField<String> headerOptionsField) {
		this.headerOptionsField = headerOptionsField;
	}

	public FormPortletTitleField getMainTitleField() {
		return mainTitleField;
	}

	public void setMainTitleField(FormPortletTitleField mainTitleField) {
		this.mainTitleField = mainTitleField;
	}

	public FormPortletTextField getColDelField() {
		return colDelField;
	}

	public void setColDelField(FormPortletTextField colDelField) {
		this.colDelField = colDelField;
	}

	public FormPortletTextField getRowDelField() {
		return rowDelField;
	}

	public void setRowDelField(FormPortletTextField rowDelField) {
		this.rowDelField = rowDelField;
	}

	public FormPortletTextField getInlineDelimiterField() {
		return inlineDelimiterField;
	}

	public void setInlineDelimiterField(FormPortletTextField inlineDelimiterField) {
		this.inlineDelimiterField = inlineDelimiterField;
	}

	public FormPortletTextField getInlineEnclosedField() {
		return inlineEnclosedField;
	}

	public void setInlineEnclosedField(FormPortletTextField inlineEnclosedField) {
		this.inlineEnclosedField = inlineEnclosedField;
	}

	public CopyPortlet setFormStyle(PortletStyleManager_Form styleManager) {
		this.buttonsForm.setStyle(styleManager);
		this.selectColumnsPortlet.setStyle(styleManager);
		this.txtForm.setStyle(styleManager);
		this.formatPortlet.setStyle(styleManager);
		return this;
	}
}
