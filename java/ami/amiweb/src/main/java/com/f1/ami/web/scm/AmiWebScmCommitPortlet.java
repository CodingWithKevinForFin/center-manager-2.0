package com.f1.ami.web.scm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.Form;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletMultiSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.SH;

public class AmiWebScmCommitPortlet extends GridPortlet implements FormPortletListener {

	private AmiWebScmBasePortlet owner;
	private List<String> files;
	private FormPortlet form;
	private FormPortletTextAreaField commentField;
	private FormPortletMultiSelectField<String> selectedField;
	private FormPortletButton commitButton;
	private FormPortletButton cancelButton;

	public AmiWebScmCommitPortlet(PortletConfig config, AmiWebScmBasePortlet owner, List<String> files) {
		super(config);
		this.owner = owner;
		this.files = files;
		this.form = this.addChild(new FormPortlet(generateConfig()), 0, 0);
		this.commentField = this.form.addField(new FormPortletTextAreaField(""));
		this.selectedField = this.form.addField(new FormPortletMultiSelectField<String>(String.class, ""));
		this.commitButton = this.form.addButton(new FormPortletButton("Commit(Alt+enter)"));
		this.cancelButton = this.form.addButton(new FormPortletButton("Cancel"));
		this.form.addField(new FormPortletTitleField("").setValue("Comment:").setLeftPosPx(10).setTopPosPx(10).setWidthPx(200).setHeightPx(20));
		this.commentField.setTopPosPx(30).setLeftPosPx(10).setRightPosPx(10).setBottomPosPx(200);
		this.form.addField(new FormPortletTitleField("").setValue("Files to Commit:").setLeftPosPx(10).setBottomPosPx(150).setWidthPx(200).setHeightPx(20));
		this.selectedField.setBottomPosPx(50).setLeftPosPx(10).setRightPosPx(10).setHeightPx(100);
		for (String id : files)
			this.selectedField.addOption(id, id);
		this.selectedField.setValue(new HashSet<String>(files));
		this.form.addFormPortletListener(this);
		setSuggestedSize(400, 400);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.cancelButton) {
			close();
			return;
		} else if (button == this.commitButton) {
			Set<String> selectedValueKeys = this.selectedField.getSelectedValueKeys();
			String comment = SH.trim(this.commentField.getValue());
			if (selectedValueKeys.isEmpty()) {
				getManager().showAlert("Must select at least one file to commit");
			} else if (SH.isnt(comment)) {
				getManager().showAlert("Comment required");
				this.commentField.focus();
			} else {
				if (owner.commit(new ArrayList<String>(selectedValueKeys), comment))
					close();
			}

		}
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		if (keycode == 13 && mask == Form.KEY_ALT)
			onButtonPressed(this.form, this.commitButton);
	}
}
