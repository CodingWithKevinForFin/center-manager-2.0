package com.f1.suite.web.table;

import java.util.Map;

public interface WebCellEnumFormatter<K> extends WebCellFormatter {

	public Map<K, String> getEnumValuesAsText();
}
