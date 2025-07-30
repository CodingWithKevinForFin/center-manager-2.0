package com.f1.ami.web.form.factory;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.field.BaseEditFieldPortlet;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.style.AmiWebStyleType;

public abstract class AmiWebFormFieldFactory<QUERY_TYPE extends QueryField<?>> {

	//	private static final Set<String> DEFAULT_SUPPORTED_EVENT_TYPES = CH.s(new LinkedHashSet(), QueryField.EVENT_ONCHANGE, QueryField.EVENT_ONENTERKEY, QueryField.EVENT_ONFOCUS);

	abstract public String getType();
	abstract public Class<QUERY_TYPE> getClassType();
	abstract public String getEditorTypeId();
	abstract public String getUserLabel();
	abstract public String getIcon();
	abstract public QUERY_TYPE createQueryField(AmiWebQueryFormPortlet form);
	abstract public BaseEditFieldPortlet<QUERY_TYPE> createEditor(AmiWebQueryFormPortlet form, int x, int y);

	abstract public AmiWebStyleType getStyleType();

	public int getDefaultWidth() {
		return 200;
	}
	public int getDefaultHeight() {
		return 20;
	}

}
