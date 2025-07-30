package com.f1.suite.web.tree.impl;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.WebTreeNodeFormatter;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.impl.CaseInsensitiveHasher;
import com.f1.utils.impl.TextMatcherFactory;

public class BasicTreeQuickFilterAutocompleteManager implements TreeQuickFilterAutocompleteManager {

	@Override
	public void onQuickFilterUserAction(FastTreePortlet t, Integer columnId, String startsWith, int i) {
		Map<String, String> r = getAutocompleteValues(t.getTree(), columnId, startsWith, i);
		if (r != null)
			t.callSetAutocomplete(columnId, r);
	}
	private Map<String, String> getAutocompleteValues(FastWebTree table, Integer columnId, String startsWith, int i) {
		final String prefix;
		if (SH.startsWith(startsWith, ">= ")) {
			prefix = ">= ";
		} else if (SH.startsWith(startsWith, "<= ")) {
			prefix = "<= ";
		} else if (SH.startsWith(startsWith, "< ")) {
			prefix = "< ";
		} else if (SH.startsWith(startsWith, "> ")) {
			prefix = "> ";
		} else {
			int n = SH.lastIndexOf(startsWith, '|');
			if (n != -1) {
				prefix = startsWith.substring(0, n + 1);
			} else {
				n = SH.lastIndexOf(startsWith, '-');
				if (n != -1 && n != 0)
					prefix = startsWith.substring(0, n + 1);
				else
					prefix = null;
			}
		}
		if (prefix != null)
			startsWith = SH.stripPrefix(startsWith, prefix, false);
		final FastWebTreeColumn col = table.getColumn(columnId);
		final int columnIndex = table.getColumnPosition(columnId);
		HasherMap<String, String> r = new HasherMap<String, String>(CaseInsensitiveHasher.INSTANCE);
		Iterable<WebTreeNode> r1 = table.getNodes();
		ArrayList<WebTreeNode> rows = new ArrayList<WebTreeNode>();
		if (col == table.getTreeColumn())
			CH.addAll(rows, r1);
		else
			for (WebTreeNode wtn : r1)
				if (wtn.getChildrenCount() == 0 && wtn.getFilteredChildrenCount() == 0)
					rows.add(wtn);

		//		int l = rows.size();
		WebTreeFilteredInFilter f = table.getFiltererdIn(columnId);
		if (f != null) {
			Set<Integer> allFilteres = table.getFilteredInColumns();
			if (allFilteres.size() == 1) {
				for (WebTreeNode row : table.getFilteredRows()) {
					String val = format(col, row);
					if (startsWith.length() > 0 && !SH.startsWithIgnoreCase(val, startsWith))
						continue;
					put(r, prefix, val);
					if (r.size() >= i)
						return r;
				}
			} else {
				StringBuilder tmp = new StringBuilder();
				WebTreeFilteredInFilter[] otherFilters = new WebTreeFilteredInFilter[allFilteres.size() - 1];
				int pos = 0;
				for (Integer s : allFilteres)
					if (OH.ne(columnId, s))
						otherFilters[pos++] = table.getFiltererdIn(s);
				outer: for (WebTreeNode row : table.getFilteredRows()) {
					String value = table.getValueAsText(row, columnIndex, SH.clear(tmp)).toString();
					if (startsWith.length() > 0 && !SH.startsWithIgnoreCase(value, startsWith))
						continue;
					if (r.size() >= i)
						break;
					for (WebTreeFilteredInFilter otherFilter : otherFilters) {
						if (!otherFilter.shouldKeep(row))
							continue outer;
					}
					put(r, prefix, value);
				}
			}
		}
		for (WebTreeNode row : rows) {
			String val = format(col, row);
			if (startsWith.length() > 0 && !SH.startsWithIgnoreCase(val, startsWith))
				continue;
			if(!row.getShouldShowInQuickFilter())
				continue;
			put(r, prefix, val);
			if (r.size() > i)
				return r;
		}
		return r;
	}

	private String format(FastWebTreeColumn col, WebTreeNode row) {
		WebTreeNodeFormatter formatter = col.getFormatter();
		Object data;
		data = formatter.getValueDisplay(row);
		return col.getFormatter().formatToText(data);
	}

	static private void put(HasherMap<String, String> r, String prefix, String val) {
		String val2 = val == null ? "<i>&lt;null&gt;</i>" : ("".equals(val) ? "<i>&lt;Empty String&gt;</i>" : WebHelper.escapeHtml(val));
		Entry<String, String> e = r.getOrCreateEntry(val2);
		if (e.getValue() == null) {
			val = TextMatcherFactory.escapeToPattern(val, false);
			if (prefix != null)
				val = prefix + val;
			e.setValue(val);
		}
	}

}
