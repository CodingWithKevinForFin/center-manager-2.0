package com.f1.ami.web;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class AmiWebSaveMultiLayoutPortlet extends GridPortlet implements FormPortletListener {

	public static final String ROOT_LAYOUT_NAME = "&ltroot&gt";
	public static final String READONLY_TEXT = "[Readonly]&nbsp;";
	private int TITLE_FIELD_TOP_PX = 10;
	private int TITLE_FIELD_LEFT_PX = 20;
	private int TITLE_FIELD_WIDTH_PX = 350;
	private int TITLE_FIELD_HEIGHT_PX = 10;

	private int CHECKBOX_FIELD_TOP_PX = TITLE_FIELD_TOP_PX + 10;
	private int CHECKBOX_FIELD_LEFT_PX = 20;
	private int CHECKBOX_FIELD_WIDTH_PX = 19;
	private int CHECKBOX_FIELD_HIEGHT_PX = 19;

	private static final Logger log = LH.get();
	private final AmiWebService service;
	private final FormPortlet form;
	private final AmiWebLayoutFile rootLayout;
	private final FormPortletTitleField changedFilesTitleField;
	private final FormPortletTitleField unchangedFilesTitleField;
	private final FormPortletButton save;
	private final FormPortletButton cancel;

	public AmiWebSaveMultiLayoutPortlet(PortletConfig config, AmiWebService service, AmiWebLayoutFile rootLayout, List<AmiWebLayoutFile> changedLayouts,
			List<AmiWebLayoutFile> unchangedLayouts) {
		super(config);
		this.service = service;
		this.form = new FormPortlet(generateConfig());
		this.rootLayout = rootLayout;
		this.changedFilesTitleField = new FormPortletTitleField("The following file(s) have changed:");
		this.unchangedFilesTitleField = new FormPortletTitleField("The following file(s) have NOT changed:");
		this.save = new FormPortletButton("Save");
		this.cancel = new FormPortletButton("Cancel");

		changedFilesTitleField.setLeftTopWidthHeightPx(TITLE_FIELD_LEFT_PX, TITLE_FIELD_TOP_PX, TITLE_FIELD_WIDTH_PX, TITLE_FIELD_HEIGHT_PX);
		this.form.addField(changedFilesTitleField);

		for (AmiWebLayoutFile layout : changedLayouts) {
			FormPortletCheckboxField checkboxField;
			if (!layout.isReadonly())
				checkboxField = AmiWebLayoutHelper.DEFAULT_ROOT_ALIAS.equals(layout.getFullAlias()) ? new FormPortletCheckboxField(ROOT_LAYOUT_NAME, true)
						: new FormPortletCheckboxField(layout.getFullAlias(), true);
			else {
				checkboxField = AmiWebLayoutHelper.DEFAULT_ROOT_ALIAS.equals(layout.getFullAlias()) ? new FormPortletCheckboxField(READONLY_TEXT + ROOT_LAYOUT_NAME, false)
						: new FormPortletCheckboxField(READONLY_TEXT + layout.getFullAlias(), false);
				checkboxField.setDisabled(true);
			}
			checkboxField.setCorrelationData(layout);
			CHECKBOX_FIELD_TOP_PX += 20;
			checkboxField.setLeftTopWidthHeightPx(CHECKBOX_FIELD_LEFT_PX, CHECKBOX_FIELD_TOP_PX, CHECKBOX_FIELD_WIDTH_PX, CHECKBOX_FIELD_HIEGHT_PX);
			checkboxField.setLabelSide(FormPortletField.LABEL_SIDE_RIGHT);
			checkboxField.setLabelSideAlignment(FormPortletField.LABEL_SIDE_ALIGN_CENTER);
			this.form.addField(checkboxField);
		}

		TITLE_FIELD_TOP_PX = CHECKBOX_FIELD_TOP_PX + 30;
		unchangedFilesTitleField.setLeftTopWidthHeightPx(TITLE_FIELD_LEFT_PX, TITLE_FIELD_TOP_PX, TITLE_FIELD_WIDTH_PX, TITLE_FIELD_HEIGHT_PX);
		this.form.addField(unchangedFilesTitleField);

		CHECKBOX_FIELD_TOP_PX = TITLE_FIELD_TOP_PX + 10;
		for (AmiWebLayoutFile layout : unchangedLayouts) {
			FormPortletCheckboxField checkboxField;
			if (!layout.isReadonly())
				checkboxField = AmiWebLayoutHelper.DEFAULT_ROOT_ALIAS.equals(layout.getFullAlias()) ? new FormPortletCheckboxField(ROOT_LAYOUT_NAME, false)
						: new FormPortletCheckboxField(layout.getFullAlias(), false);
			else {
				checkboxField = AmiWebLayoutHelper.DEFAULT_ROOT_ALIAS.equals(layout.getFullAlias()) ? new FormPortletCheckboxField(READONLY_TEXT + ROOT_LAYOUT_NAME, false)
						: new FormPortletCheckboxField(READONLY_TEXT + layout.getFullAlias(), false);
				checkboxField.setDisabled(true);
			}
			checkboxField.setCorrelationData(layout);
			CHECKBOX_FIELD_TOP_PX += 20;
			checkboxField.setLeftTopWidthHeightPx(CHECKBOX_FIELD_LEFT_PX, CHECKBOX_FIELD_TOP_PX, CHECKBOX_FIELD_WIDTH_PX, CHECKBOX_FIELD_HIEGHT_PX);
			checkboxField.setLabelSide(FormPortletField.LABEL_SIDE_RIGHT);
			checkboxField.setLabelSideAlignment(FormPortletField.LABEL_SIDE_ALIGN_CENTER);
			this.form.addField(checkboxField);
		}
		this.form.setLabelsWidth(800);
		this.form.addButton(save);
		this.form.addButton(cancel);

		this.form.addFormPortletListener(this);
		this.addChild(this.form, 0, 0);

	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (OH.eq(button, save)) {
			for (FormPortletField<?> field : portlet.getFormFields()) {
				if (field instanceof FormPortletCheckboxField && ((FormPortletCheckboxField) field).getBooleanValue()) {
					FormPortletCheckboxField option = (FormPortletCheckboxField) field;
					AmiWebLayoutFile layout = (AmiWebLayoutFile) option.getCorrelationData();
					if (layout.isReadonly()) // not necessary 
						continue;
					this.service.getLayoutFilesManager().saveSingleLayoutFile(layout);
					LH.info(log, "layout saved: " + layout.getAlias() + "(" + layout.getAbsoluteLocation() + ")");
				}
			}
			this.close();
		} else if (OH.eq(button, cancel))
			this.close();
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

}
