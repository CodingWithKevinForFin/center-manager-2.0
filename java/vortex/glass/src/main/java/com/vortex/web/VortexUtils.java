package com.vortex.web;

import java.util.Set;

import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.WebTableFilteredSetFilter;
import com.f1.utils.CH;
import com.vortex.web.messages.VortexInterPortletMessage;

public class VortexUtils {
	public static void applyIDFilter(VortexInterPortletMessage msg, FastWebTable table) {
		applyIDFilter(msg, table, msg.getType());
	}

	public static void applyIDFilter(VortexInterPortletMessage msg, FastWebTable table, String idColumn) {
		Set<?> selection = (Set<?>) msg.getSelected();

		if (CH.isEmpty(selection))
			table.setExternalFilter(null);
		else {
			WebColumn column = table.getColumnNoThrow(idColumn);
			//	System.out.println("applyng filter: " + selection.size());
			if (column != null)
				table.setExternalFilter(new WebTableFilteredSetFilter(column, selection));
		}
	}
}
