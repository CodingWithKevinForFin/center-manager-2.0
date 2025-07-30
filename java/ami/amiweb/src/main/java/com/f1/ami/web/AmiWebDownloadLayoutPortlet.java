package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.suite.web.portal.BasicPortletDownload;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.StringFormatException;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

public class AmiWebDownloadLayoutPortlet extends GridPortlet implements FormPortletListener {
	private static final Logger log = LH.get();
	private final AmiWebService service;
	private final FormPortlet form;
	private final FormPortletTextField layoutNameField;
	private final FormPortletToggleButtonsField<Byte> layoutContentFormattingToggle;
	private final FormPortletTextField previewTextField;
	private final FormPortletTextAreaField previewTextAreaField;
	private Map<String, Object> configuration;
	private final FormPortletButton downloadLayoutButton;
	private final FormPortletButton cancelDownloadLayoutButton;

	public AmiWebDownloadLayoutPortlet(PortletConfig config) {
		super(config);
		this.service = (AmiWebService) getManager().getService(AmiWebService.ID);
		this.service.getLayoutManager();
		this.form = new FormPortlet(generateConfig());
		this.layoutNameField = new FormPortletTextField("Layout Name:");
		this.layoutNameField.setValue(SH.afterLast(this.service.getLayoutFilesManager().getLayoutName(), '/'));
		this.layoutContentFormattingToggle = new FormPortletToggleButtonsField<Byte>(Byte.class, "Text Formatting:");
		layoutContentFormattingToggle.addOption(ObjectToJsonConverter.MODE_COMPACT, "Compact");
		layoutContentFormattingToggle.addOption(ObjectToJsonConverter.MODE_SEMI, "Legible");
		layoutContentFormattingToggle.addOption(ObjectToJsonConverter.MODE_CLEAN, "Expanded");
		this.layoutContentFormattingToggle.setValue(ObjectToJsonConverter.MODE_SEMI);
		this.previewTextField = new FormPortletTextField("").setValue("Preview");
		this.previewTextAreaField = new FormPortletTextAreaField("");
		this.configuration = new HashMap<String, Object>();
		setupPreviewConfiguration();
		this.downloadLayoutButton = new FormPortletButton("Download").setId("download_layout");
		this.cancelDownloadLayoutButton = new FormPortletButton("Cancel").setId("cancel_download");
		this.form.addField(this.layoutNameField);
		this.form.addField(this.layoutContentFormattingToggle);
		this.form.addField(this.previewTextField);
		this.form.addField(previewTextAreaField);
		this.form.addButton(downloadLayoutButton);
		this.form.addButton(cancelDownloadLayoutButton);
		addStylesToFields();
		addChild(this.form, 0, 0);
		this.form.addFormPortletListener(this);

	}

	private void setupPreviewConfiguration() {
		this.configuration.put("topId", "cok5jzv6ar");
		this.configuration.put("portletBuilderId", "desktop");
		ArrayList<String> windows = new ArrayList<String>(Arrays.asList("window 1", "window 2", "window 3"));
		this.configuration.put("windows", windows);
		updatePreviewTextArea();
	}
	private void updatePreviewTextArea() {
		ObjectToJsonConverter c = ObjectToJsonConverter.getInstance(this.layoutContentFormattingToggle.getValue());
		this.previewTextAreaField.setValue(c.objectToString(this.configuration));
	}
	private void addStylesToFields() {
		this.layoutNameField.setCssStyle("style.padding=5px");
		this.previewTextField.setDisabled(true);
		this.previewTextAreaField.setHeightPx(100).setWidthPx(400).setDisabled(true);
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (OH.eq(button.getId(), "download_layout")) {
			if (downloadLayout())
				this.close();
		} else if (OH.eq(button.getId(), "cancel_download")) {
			this.close();
		}
	}
	private boolean downloadLayout() {
		if (AmiWebLayoutHelper.isValidLayoutName(this.layoutNameField.getValue(), service)) {
			String layoutContentStr = AmiWebLayoutHelper.toJson(this.service.getLayoutFilesManager().getLayoutConfiguration(false), layoutContentFormattingToggle.getValue());
			getManager().pushPendingDownload(new BasicPortletDownload(this.layoutNameField.getValue(), layoutContentStr.getBytes()));
			return true;
		}
		return false;
		//TODO: commented out temporarily, will be used later.
		//		if (this.service.getLayoutFilesManager().hasMultipleLayouts()) {
		//			byte[] zippedContent = AmiWebUtils.zipLayoutFiles(this.service.getLayoutFilesManager().getLayout().getChildrenRecursive(true), service,
		//					layoutContentFormattingToggle.getValue());
		//			getManager().pushPendingDownload(new BasicPortletDownload("layouts.zip", zippedContent));
		//		} else {
		//			AmiWebLayoutFile layoutFile = this.service.getLayoutFilesManager().getLayoutByFullAlias("");
		//			String layoutContent = AmiWebLayoutHelper.toJson(layoutFile.buildCurrentJson(this.service), layoutContentFormattingToggle.getValue());
		//			getManager().pushPendingDownload(new BasicPortletDownload(SH.trim(this.layoutNameField.getValue()), layoutContent.getBytes()));
		//			return;
		//		}
	}
	public String getOriginalLayoutName() {
		return this.service.getLayoutFilesManager().getLayoutName();
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.layoutContentFormattingToggle)
			updatePreviewTextArea();
		if (field == this.previewTextAreaField)
			updateConfiguration();
	}
	private void updateConfiguration() {
		ObjectToJsonConverter c = ObjectToJsonConverter.getInstance(this.layoutContentFormattingToggle.getValue());
		try {
			Map<String, Object> newConfig = (Map<String, Object>) c.stringToObject(this.previewTextAreaField.getValue());
			this.configuration = newConfig;
			this.layoutContentFormattingToggle.setDisabled(false);
		} catch (StringFormatException e) {
			this.layoutContentFormattingToggle.setDisabled(true);
		}
	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}
}
