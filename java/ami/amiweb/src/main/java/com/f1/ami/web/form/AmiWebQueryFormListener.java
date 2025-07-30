package com.f1.ami.web.form;

import com.f1.ami.web.form.queryfield.QueryField;

public interface AmiWebQueryFormListener {

	void onFieldAriChanged(QueryField<?> queryField, String oldAri);
	void onFieldRemoved(QueryField<?> queryField);
	void onFieldAdded(QueryField<?> queryField);

}
