package com.f1.suite.web.portal.impl.form;

import java.util.Map;

import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.RootPortletDialog;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;

public class FormPortletSelectPortletField extends FormPortletField<String> {

	static private int AVG_CHAR_WIDTH = 10;
	private RootPortletDialog dialog;

	public FormPortletSelectPortletField(String title) {
		super(String.class, title);
		setDefaultValue("");
	}

	@Override
	public String getjsClassName() {
		return "PortletSelectField";
	}

	@Override
	public void rebuildJs(StringBuilder pendingJs) {
		this.dialog = null;
		super.rebuildJs(pendingJs);
	}

	@Override
	public FormPortletSelectPortletField setValue(String value) {
		super.setValue(value);
		return this;
	}
	@Override
	public boolean onUserValueChanged(Map<String, String> attributes) {
		if (attributes.containsKey("click")) {
			RootPortlet root = (RootPortlet) this.getForm().getManager().getRoot();
			for (RootPortletDialog dialog : root.getDialogs()) {
				if (PortletHelper.isParentOfChild(dialog.getPortlet(), getForm())) {
					this.dialog = dialog;
					root.hideDialog(dialog);
					break;
				}
			}
			return false;
		} else if (attributes.containsKey("value")) {
			Integer target = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "value");
			if (this.dialog != null) {
				RootPortlet root = (RootPortlet) this.getForm().getManager().getRoot();
				root.showDialog(this.dialog);
				this.dialog = null;
			}
			setValue(SH.toString(target));
			return true;
		}
		return false;
	}

	public Portlet getValueAsPortlet() {
		String val = getValue();
		if (SH.isnt(val))
			return null;
		return getForm().getManager().getPortletNoThrow(val);
	}

	public String getJsValue() {
		Portlet portlet = getValueAsPortlet();
		if (portlet == null)
			return "";
		return portlet.getTitle();
	}

	@Override
	public FormPortletSelectPortletField setName(String name) {
		super.setName(name);
		return this;
	}
}
