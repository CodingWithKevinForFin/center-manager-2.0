package com.f1.suite.web.portal.impl.form;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class BasicFormPortletExportImportManager implements FormPortletExportImportManager {

	Map<String, String> nameToExport;

	public BasicFormPortletExportImportManager(Map<String, String> n2e) {
		this.nameToExport = n2e;
	}

	@Override
	public Map<String, Object> exportToText(FormPortlet target) {
		LinkedHashMap<String, Object> values = new LinkedHashMap<String, Object>();
		Set<String> names = new HashSet<String>();
		for (FormPortletField<?> i : target.getFormFields()) {
			if (!i.isExportImportSupported() || !i.isVisible())
				continue;
			String name = SH.trim(i.getName());
			if (nameToExport != null) {
				if (nameToExport.containsKey(name)) {
					name = nameToExport.get(name);
					if (name == null)
						continue;
				}
			}
			name = SH.getNextId(name, names);
			names.add(name);
			values.put(name, i.getValue());
		}
		return values;

	}
	@Override
	public void importFromText(FormPortlet target, Map<String, Object> values, StringBuilder errorSink) {

		Set<String> names = new HashSet<String>();
		for (FormPortletField i : CH.l(target.getFormFields())) {
			if (!i.isExportImportSupported() || i.isDisabled() || !i.isVisible())
				continue;
			String name = SH.trim(i.getName());
			name = SH.getNextId(name, names);
			if (nameToExport != null) {
				if (nameToExport.containsKey(name)) {
					name = nameToExport.get(name);
					if (name == null)
						continue;
				}
			}
			names.add(name);
			Object value = values.remove(name);
			try {
				Object val = i.getCaster().cast(value);
				if (OH.ne(val, i.getValue())) {
					i.setValue(val);
					target.fireFieldValueChangedTolisteners(i, Collections.EMPTY_MAP);
				}
			} catch (Exception e) {
				errorSink.append("Error setting property for " + name + ": ").append(e.getMessage()).append(". ");
			}
		}
		if (!values.isEmpty())
			errorSink.append(" Ignored Unknown fields: ").append(values.keySet());
	}

}
