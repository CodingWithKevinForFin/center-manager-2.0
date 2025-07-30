package com.f1.ami.portlets;

import com.f1.suite.web.portal.impl.form.FormPortletListener;

public interface AmiWebHeaderSearchHandler extends FormPortletListener {
	public void doSearch();
	public void doSearchNext();
	public void doSearchPrevious();
}
