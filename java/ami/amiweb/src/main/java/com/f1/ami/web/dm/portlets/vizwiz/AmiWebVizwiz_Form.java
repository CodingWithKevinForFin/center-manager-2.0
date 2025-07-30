package com.f1.ami.web.dm.portlets.vizwiz;

import java.util.List;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.AmiWebEditStylePortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.dm.AmiWebDmTablesetSchema;
import com.f1.ami.web.form.AmiWebQueryFormEditHtmlPortlet;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.form.queryfield.RangeQueryField;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.utils.CH;
import com.f1.utils.LH;

public class AmiWebVizwiz_Form extends AmiWebVizwiz<AmiWebQueryFormPortlet> {

	private static final Logger log = LH.get();

	private TabPortlet creatorTabsPortlet;
	private AmiWebEditStylePortlet styleForm;
	private final FormPortletButton addTextFieldButton;
	private final FormPortletButton addTextAreaFieldButton;
	private final FormPortletButton addSliderFieldButton;
	private final FormPortletButton addRangeFieldButton;
	private final FormPortletButton addCheckboxFieldButton;
	private final FormPortletButton addSelectFieldButton;
	private final FormPortletButton addDateFieldButton;
	private final FormPortletButton addFileUploadFieldButton;
	private final FormPortletButton addButtonFieldButton;
	private final FormPortletButton addDivFieldButton;
	private GridPortlet editorGrid;
	private HtmlPortlet blankPortlet;
	final private List<FormPortletButton> addButtonsList = CH.l(this.addTextFieldButton = new FormPortletButton("+ Text"),
			this.addTextAreaFieldButton = new FormPortletButton("+ Text Area"), this.addSliderFieldButton = new FormPortletButton("+ Slider"),
			this.addRangeFieldButton = new FormPortletButton("+ Range"), this.addCheckboxFieldButton = new FormPortletButton("+ Checkbox"),
			this.addSelectFieldButton = new FormPortletButton("+ Select"), this.addDateFieldButton = new FormPortletButton("+ Date "),
			this.addFileUploadFieldButton = new FormPortletButton("+ File Upload"), this.addButtonFieldButton = new FormPortletButton("+ Button"),
			this.addDivFieldButton = new FormPortletButton("+ Div"));

	public AmiWebVizwiz_Form(AmiWebService service, String layoutAlias) {
		super(service, "Html/Canvas");
		AmiWebQueryFormPortlet tm = (AmiWebQueryFormPortlet) service.getDesktop().newPortlet(AmiWebQueryFormPortlet.Builder.ID, layoutAlias);
		tm.setSnapSize(20);
		setPreviewPortlet(tm);
		//		AmiCenterGetAmiSchemaResponse response = getService().nw(AmiCenterGetAmiSchemaResponse.class);
		this.creatorTabsPortlet = new TabPortlet(generateConfig());
		this.creatorTabsPortlet.getTabPortletStyle().setBackgroundColor("#4c4c4c");
		this.creatorTabsPortlet.addChild("HTML", new AmiWebQueryFormEditHtmlPortlet(tm, generateConfig()).hideCloseButtons(true));
		this.editorGrid = new GridPortlet(generateConfig());
		this.editorGrid.addChild(this.blankPortlet = new HtmlPortlet(generateConfig(),
				"<BR><center>To edit a field, either add a new field using the green buttons or click on an existing field in the form."), 0, 0);
		this.blankPortlet.setCssStyle("_bg=#e2e2e2|style.fontSize=20px");
		this.creatorTabsPortlet.addChild("Edit Field", this.editorGrid);
		this.creatorTabsPortlet.addChild("Style", this.styleForm = new AmiWebEditStylePortlet(tm.getStylePeer(), generateConfig()).hideCloseButtons(true));
		this.creatorTabsPortlet.setIsCustomizable(false);
		addButtons(this.addButtonsList);
		this.addTextFieldButton.setId(QueryField.TYPE_ID_TEXT);
		this.addTextAreaFieldButton.setId(QueryField.TYPE_ID_TEXT_AREA);
		this.addSliderFieldButton.setId(QueryField.TYPE_ID_RANGE);
		this.addRangeFieldButton.setId(QueryField.TYPE_ID_SUBRANGE);
		this.addCheckboxFieldButton.setId(QueryField.TYPE_ID_CHECKBOX);
		this.addSelectFieldButton.setId(QueryField.TYPE_ID_SELECT);
		this.addDateFieldButton.setId(QueryField.TYPE_ID_DATE);
		this.addFileUploadFieldButton.setId(QueryField.TYPE_ID_FILE_UPLOAD);
		this.addButtonFieldButton.setId(QueryField.TYPE_ID_BUTTON);
		this.addDivFieldButton.setId(QueryField.TYPE_ID_DIV);
	}
	@Override
	public Portlet getCreatorPortlet() {
		return creatorTabsPortlet;
	}

	@Override
	public boolean initDm(AmiWebDm dm, Portlet initForm, String dmTableName) {
		AmiWebQueryFormPortlet previewPortlet = getPreviewPortlet();
		previewPortlet.setUsedDatamodel(dm.getAmiLayoutFullAliasDotId(), dmTableName);
		previewPortlet.onDmDataChanged(dm);
		previewPortlet.setAmiTitle(dmTableName, false);
		addColumnFields(dm, dmTableName);
		previewPortlet.onDmDataChanged(dm);
		return true;
	}
	private void addColumnFields(AmiWebDm dm, String dmTableName) {
		if (dm == null) {
			return;
		}
		if (dmTableName == null) {
			LH.warning(log, "Datamodel Table Name is null for dm: " + dm.getAmiLayoutFullAliasDotId());
			return;
		}
		AmiWebDmTablesetSchema requestInSchema = dm.getRequestInSchema();
		AmiWebQueryFormPortlet previewPortlet = getPreviewPortlet();
		AmiWebDmTableSchema schema;
		Class colType;
		String colVarName;
		String fieldType;
		QueryField<?> queryField;
		int fieldTopPosPx = 0;
		boolean isInteger;
		isInteger = false;
		schema = requestInSchema.getTable(dmTableName);
		for (String colName : schema.getColumnNames()) {
			colType = schema.getClassType(colName);
			colVarName = AmiUtils.toValidVarName(colName);
			if (colType == Boolean.class)
				fieldType = QueryField.TYPE_ID_CHECKBOX;
			else if (AmiUtils.isNumericType(colType))
				fieldType = QueryField.TYPE_ID_RANGE;
			else if (AmiUtils.isDateType(colType))
				fieldType = QueryField.TYPE_ID_DATE;
			else
				fieldType = QueryField.TYPE_ID_TEXT;
			queryField = getService().getFormFieldFactory(fieldType).createQueryField(previewPortlet);
			queryField.setVarName(colVarName);
			queryField.getDefaultValueFormula().setFormula(colVarName, false);
			FormPortletField<?> field = queryField.getField();
			queryField.setLabel(AmiWebUtils.toPrettyName(colName));
			if (queryField instanceof RangeQueryField) {
				RangeQueryField queryField2 = (RangeQueryField) queryField;
				queryField2.setRange(-100, 100);
				FormPortletNumericRangeField range = queryField2.getField();
				range.setDecimals(isInteger ? 0 : 2);//not sure if this will stick
			}
			fieldTopPosPx += FormPortletField.DEFAULT_HEIGHT + FormPortletField.DEFAULT_PADDING_PX;
			queryField.ensureHorizontalPosDefined();
			queryField.ensureVerticalPosDefined(fieldTopPosPx);
			previewPortlet.addQueryField(queryField, true);
		}
	}

	@Override
	public void onButton(FormPortletButton button) {
		if (this.addButtonsList.contains(button)) {
			AmiWebQueryFormPortlet t = this.getPreviewPortlet();

			//This is creating a new field so it should pass in the type of field to be made
			this.getService().getAmiQueryFormEditorsManager().showAddNewFieldEditor(t, button.getId());
		} else
			super.onButton(button);
	}
	@Override
	public int getCreatorPortletWidth() {
		return this.styleForm.getWidth();
	}
}
