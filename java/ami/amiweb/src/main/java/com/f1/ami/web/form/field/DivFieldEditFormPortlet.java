package com.f1.ami.web.form.field;

import com.f1.ami.web.AmiWebFormPortletAmiScriptField;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormDivFieldFactory;
import com.f1.ami.web.form.queryfield.DivQueryField;
import com.f1.suite.web.portal.impl.form.FormPortletDivField;

public class DivFieldEditFormPortlet extends BaseEditFieldPortlet<DivQueryField> {
	private AmiWebFormPortletAmiScriptField editHtmlField;

	public DivFieldEditFormPortlet(AmiWebFormDivFieldFactory factory, AmiWebQueryFormPortlet queryFormPortlet, int fieldX, int fieldY) {
		super(factory, queryFormPortlet, fieldX, fieldY);
		FormPortletDivField space = new FormPortletDivField("");
		getSettingsForm().addField(space);
		space.setHeightPx(30);
		space.setLeftPosPx(0);
		space.setRightPosPx(0);
		space.setZIndex(-1);
		this.editHtmlField = getSettingsForm().addField(new AmiWebFormPortletAmiScriptField("Edit Html:", getManager(), queryFormPortlet.getAmiLayoutFullAlias()));
		editHtmlField.setHeight(180);
		editHtmlField.setLeftPosPx(0);
		editHtmlField.setRightPosPx(200);
		editHtmlField.setLeftPosPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX);
		editHtmlField.setTopPosPx(420);
		this.dmExpressionField.setVisible(false);

	}
	@Override
	public void readFromField(DivQueryField field) {
		if (field.getField().getValue() != null)
			editHtmlField.setValue((String) field.getHtmlValue(false));
	}

	@Override
	public void writeToField(DivQueryField field) {
		field.setHtmlValue(editHtmlField.getValue(), false);
	}
	@Override
	public boolean submit() {
		String value = editHtmlField.getValue();
		StringBuilder sink = new StringBuilder();
		this.queryField.testHtml(value, sink);
		if (sink.length() > 0) {
			getManager().showAlert("Error for html: " + sink);
			return false;
		}
		boolean success = super.submit();

		this.queryField.setHtmlValue(value, false);
		return success;
	}

}
