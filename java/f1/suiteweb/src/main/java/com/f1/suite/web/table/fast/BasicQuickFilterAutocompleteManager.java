package com.f1.suite.web.table.fast;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.base.Row;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.table.WebCellFormatter;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.impl.WebTableFilteredInFilter;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.LocalToolkit;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.impl.CaseInsensitiveHasher;
import com.f1.utils.impl.TextMatcherFactory;

public class BasicQuickFilterAutocompleteManager implements QuickFilterAutocompleteManager {

	@Override
	public void onQuickFilterUserAction(FastTablePortlet t, String columnId, String startsWith, int i) {
		Map<String, String> r = getAutocompleteValues(t.getTable(), columnId, startsWith, i);
		if (r != null)
			t.callSetAutocomplete(columnId, r);
	}
	private Map<String, String> getAutocompleteValues(FastWebTable table, String columnId, String startsWith, int i) {
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
		final WebColumn col = table.getColumn(columnId);
		final WebCellFormatter cf = col.getCellFormatter();
		final int columnIndex = table.getColumnPosition(columnId);
		HasherMap<String, String> r = new HasherMap<String, String>(CaseInsensitiveHasher.INSTANCE);
		List<Row> rows = table.getRows();
		WebTableFilteredInFilter f = table.getFiltererdIn(columnId);
		if (f != null) {
			Set<String> allFilteres = table.getFilteredInColumns();
			if (allFilteres.size() == 1) {
				for (Row row : table.getFilteredRows()) {
					Object rowval = col.getData(row);
					String val = rowval == null ? null : cf.formatCellToText(rowval);
					if (startsWith.length() > 0 && !SH.startsWithIgnoreCase(val, startsWith))
						continue;
					put(r, prefix, val);
					if (r.size() >= i)
						return r;
				}
			} else {
				StringBuilder tmp = new StringBuilder();
				LocalToolkit tk = new LocalToolkit();
				WebTableFilteredInFilter[] otherFilters = new WebTableFilteredInFilter[allFilteres.size() - 1];
				int pos = 0;
				for (String s : allFilteres)
					if (OH.ne(columnId, s))
						otherFilters[pos++] = table.getFiltererdIn(s);
				outer: for (Row row : table.getFilteredRows()) {
					String value = table.getValueAsText(row, columnIndex, SH.clear(tmp)).toString();
					if (startsWith.length() > 0 && !SH.startsWithIgnoreCase(value, startsWith))
						continue;
					if (r.size() >= i)
						break;
					for (WebTableFilteredInFilter otherFilter : otherFilters) {
						if (!otherFilter.shouldKeep(row, tk))
							continue outer;
					}
					put(r, prefix, value);
				}
			}
		}
		for (Row row : rows) {
			Object rowval = col.getData(row);
			String val = rowval == null ? null : cf.formatCellToText(rowval);
			if (startsWith.length() > 0 && !SH.startsWithIgnoreCase(val, startsWith))
				continue;
			put(r, prefix, val);
			if (r.size() > i)
				return r;
		}
		return r;
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
