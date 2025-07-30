package com.f1.suite.web.portal.impl.form;

import java.util.Map;

public interface FormPortletExportImportManager {
	void importFromText(FormPortlet target, Map<String, Object> values, StringBuilder errorSink);
	Map<String, Object> exportToText(FormPortlet target);

}
