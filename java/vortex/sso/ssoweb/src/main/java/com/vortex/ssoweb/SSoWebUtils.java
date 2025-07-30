package com.vortex.ssoweb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.WebTableTextMatcherFilter;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.ChainedRowFilter;
import com.f1.utils.structs.table.RowFilter;
import com.vortex.ssoweb.NodeSelectionInterPortletMessage.Mask;

public class SSoWebUtils {

	public static RowFilter createFilter(NodeSelectionInterPortletMessage msg, Map<String, String> maskToColumnIdMapping, FastWebTable table) {
		List<RowFilter> orFilters = new ArrayList<RowFilter>(msg.getMasks().size());
		for (Mask i : msg.getMasks()) {
			List<RowFilter> andFilters = new ArrayList<RowFilter>();
			while (i != null) {
				String key = maskToColumnIdMapping.get(i.key);
				if (key == null)
					key = i.key;
				WebColumn column = table.getColumnNoThrow(key);
				if (column != null)
					andFilters.add(new WebTableTextMatcherFilter(column, SH.m(Caster_String.INSTANCE.cast(i.mask))));
				i = i.next;
			}
			if (!andFilters.isEmpty())
				orFilters.add(ChainedRowFilter.and(andFilters));
		}
		if (orFilters.size() == 0)
			return null;
		else
			return ChainedRowFilter.or(orFilters);
	}

	public static void applyFilter(NodeSelectionInterPortletMessage msg, FastWebTable table, Map<String, String> map) {
		table.setExternalFilter(createFilter(msg, map, table));

	}

}
