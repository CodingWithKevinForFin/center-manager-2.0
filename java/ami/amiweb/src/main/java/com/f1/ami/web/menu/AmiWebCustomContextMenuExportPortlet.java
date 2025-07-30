package com.f1.ami.web.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

public class AmiWebCustomContextMenuExportPortlet extends GridPortlet implements FormPortletListener {

	final private AmiWebCustomContextMenu target;
	final private FormPortletButton submitButton = new FormPortletButton("Close");
	final private FormPortlet form;
	final private FormPortletTextAreaField textAreaField = new FormPortletTextAreaField("");
	final private String origText;

	public AmiWebCustomContextMenuExportPortlet(PortletConfig config, AmiWebCustomContextMenu target) {
		super(config);
		this.target = target;
		this.form = addChild(new FormPortlet(generateConfig()), 0, 0);
		this.form.addField(this.textAreaField);
		List<Map<String, Object>> r = new ArrayList<Map<String, Object>>();
		for (AmiWebCustomContextMenu i : this.target.getChildrenNested(true, new ArrayList<AmiWebCustomContextMenu>()))
			if (!i.isRoot())
				r.add(i.getConfiguration());

		this.textAreaField.setValue(this.origText = ObjectToJsonConverter.INSTANCE_CLEAN.objectToString(r));
		this.textAreaField.setSelection(0, this.origText.length());
		this.textAreaField.setTopPosPx(15);
		this.textAreaField.setBottomPosPx(55);
		this.textAreaField.setLeftPosPx(15);
		this.textAreaField.setRightPosPx(15);
		this.form.addButton(this.submitButton);
		this.form.addFormPortletListener(this);
		setSuggestedSize(400, 500);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.submitButton) {
			close();
		}
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {

	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}

}
