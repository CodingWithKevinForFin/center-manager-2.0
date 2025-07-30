package com.f1.console.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.f1.base.Table;
import com.f1.console.ConsoleConnection;
import com.f1.console.ConsoleEvent;
import com.f1.console.ConsoleSession;
import com.f1.utils.Iterator2Iterable;
import com.f1.utils.ReverseListIterator;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.f1.utils.TextMatcher;
import com.f1.utils.impl.TextMatcherFactory;
import com.f1.utils.structs.table.BasicTable;

public class QuickHistoryConsoleService extends AbstractConsoleService {

	public QuickHistoryConsoleService() {
		super("H", "H(D)?(?: +(.*?) *)?", "Lists user command history. Usage: H <search expression>");
	}

	@Override
	public void doRequest(ConsoleSession session, String[] options) {
		ConsoleConnection out = session.getConnection();
		List<ConsoleEvent> history = session.getCommandHistory();
		TextMatcher filter = TextMatcherFactory.DEFAULT.toMatcher(SH.is(options[2]) ? options[2] : null);
		boolean detailed = options[1] != null;

		Table t = new BasicTable(Integer.class, "id", String.class, "time", String.class, "connection", String.class, "command");

		if (detailed) {
			int j = 1;
			for (int i = 0, l = history.size(); i < l; i++) {
				ConsoleEvent h = history.get(i);
				if (h.getText() == null || !filter.matches(h.getText()))
					continue;
				t.getRows().addRow(i + 1, "[" + new Date(h.getTime()).toString() + "]", "[" + h.getConnectionDescription() + "]", h.getText());
			}
			out.println(TableHelper.toString(t, "", 0, new StringBuilder(), SH.NEWLINE, ' ', ' ', ' '));
		} else {

			List<ConsoleEvent> history2 = new ArrayList<ConsoleEvent>();
			Set<String> existing = new HashSet<String>();
			for (ConsoleEvent e : new Iterator2Iterable<ConsoleEvent>(new ReverseListIterator<ConsoleEvent>(history))) {
				if (existing.add(e.getText()))
					history2.add(e);
				else
					history2.add(null);
			}
			Collections.reverse(history2);
			history = history2;

			int padding = Integer.toString(history.size() + 1).length();
			for (int i = 0, l = history.size(); i < l; i++) {
				ConsoleEvent h = history.get(i);
				if (h == null)
					continue;
				if (h.getText() == null || !filter.matches(h.getText()))
					continue;
				out.println(SH.rightAlign(' ', Integer.toString(i + 1), padding, false) + "  " + h.getText());
			}
		}
	}

	@Override
	public void doStartup(ConsoleSession session) {
	}

	@Override
	public void doShutdown(ConsoleSession session) {
	}

	@Override
	public String getHelp() {
		return "H [<word_portion>] - global history includes all users, across multple jvm runs";
	}

}
