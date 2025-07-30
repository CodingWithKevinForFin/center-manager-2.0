package com.f1.console.impl;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.f1.base.Table;
import com.f1.console.ConsoleConnection;
import com.f1.console.ConsoleEvent;
import com.f1.console.ConsoleRequest;
import com.f1.console.ConsoleService;
import com.f1.console.ConsoleSession;
import com.f1.console.impl.shell.ShellAutoCompletion;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.f1.utils.structs.table.BasicTable;

public class HistoryConsoleService implements ConsoleService {
	public static Pattern PATTERN = Pattern.compile("SHOW +(DETAILED )? *HISTORY *(.*?) *", Pattern.CASE_INSENSITIVE);

	@Override
	public void doRequest(ConsoleSession session, ConsoleRequest request) {
		if (!session.isLoggedIn() && session.getManager().getAuthenticator() != null)
			return;
		Matcher matcher = PATTERN.matcher(request.getText());
		if (!matcher.matches())
			return;
		String[] options = new String[matcher.groupCount() + 1];
		for (int i = 0; i < options.length; i++)
			options[i] = matcher.group(i);
		ConsoleConnection out = session.getConnection();
		List<ConsoleEvent> history = session.getCommandHistory();
		Pattern filter = SH.is(options[2]) ? Pattern.compile(options[2]) : null;
		boolean detailed = options[1] != null;

		Table t = new BasicTable(Integer.class, "id", String.class, "time", String.class, "connection", String.class, "command");

		if (detailed) {
			int j = 1;
			for (int i = 0, l = history.size(); i < l; i++) {
				ConsoleEvent h = history.get(i);
				if (filter != null && h.getText() != null && !filter.matcher(h.getText()).matches())
					continue;
				t.getRows().addRow(j, "[" + new Date(h.getTime()).toString() + "]", "[" + h.getConnectionDescription() + "]", h.getText());
				j++;
			}
			out.println(TableHelper.toString(t, "", 0, new StringBuilder(), SH.NEWLINE, ' ', ' ', ' '));
		} else {

			int j = 0;
			int padding = Integer.toString(history.size() + 1).length();
			for (int i = 0, l = history.size(); i < l; i++) {
				ConsoleEvent h = history.get(i);
				j++;
				if (h.getText() == null || (filter != null && !filter.matcher(h.getText()).matches()))
					continue;
				out.println(SH.rightAlign(' ', Integer.toString(j), padding, false) + "  " + h.getText());
			}
		}
	}

	@Override
	public void doStartup(ConsoleSession session) {
	}

	@Override
	public void doShutdown(ConsoleSession session) {
	}

	public Pattern getPattern() {
		return PATTERN;
	}

	@Override
	public String getHelp() {
		return "global history includes all users, across multple jvm runs. Usage: show [detailed] history [<pattern>]";
	}

	@Override
	public String getDescription() {
		return "Lists user command history";
	}

	@Override
	public String getName() {
		return "show history";
	}

	@Override
	public boolean canProcessRequest(String request) {
		return PATTERN.matcher(request).matches();
	}

	@Override
	public boolean canAutoComplete(String partialText) {
		return false;
	}

	@Override
	public ShellAutoCompletion autoComplete(ConsoleSession session, String partialText) {
		return null;
	}

	@Override
	public boolean availableWihtoutLogin() {
		return false;
	}

	@Override
	public boolean saveCommandToHistory() {
		return false;
	}
}
