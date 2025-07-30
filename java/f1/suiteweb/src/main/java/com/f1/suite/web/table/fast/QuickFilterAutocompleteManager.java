package com.f1.suite.web.table.fast;

import com.f1.suite.web.portal.impl.FastTablePortlet;

public interface QuickFilterAutocompleteManager {

	void onQuickFilterUserAction(FastTablePortlet t, String columnId, String val, int i);

}
