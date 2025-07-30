package com.f1.ami.center.console;

import java.util.ArrayList;
import java.util.List;

import com.f1.utils.SH;
import com.f1.utils.TextMatcher;

public class AmiCenterConsoleCmd_ShowHistory extends AmiCenterConsoleCmd {

	public AmiCenterConsoleCmd_ShowHistory() {
		super("show history <pattern>", "print history of commands executed on this command line interface");
	}

	@Override
	public void process(AmiCenterConsoleClient client, String cmd, String[] cmdParts) {
		StringBuilder sb = new StringBuilder();
		int limit = 1000;
		TextMatcher regex;
		if (cmdParts.length > 2) {
			regex = SH.m(cmdParts[2]);
		} else
			regex = null;
		List<String> origHistory = client.getHistory();
		List<String> history;
		if (regex != null) {
			history = new ArrayList<String>();
			for (String s : origHistory)
				if (regex.matches(s))
					history.add(s);
		} else
			history = origHistory;
		int total = history.size();
		if (limit > total)
			limit = total;
		int start = total - limit;

		for (int i = start; i < total; i++)
			sb.append(history.get(i)).append('\n');
		if (regex != null)
			sb.append("(SHOWING LAST ").append(limit).append(" OF ").append(history.size()).append(" MATCHING ROWS)").append('\n');
		else if (limit == total)
			sb.append("(SHOWING ALL ").append(origHistory.size()).append(" ROWS)").append('\n');
		else
			sb.append("(SHOWING LAST ").append(limit).append(" OF ").append(origHistory.size()).append(" ROWS)").append('\n');
		client.getOutputStream().print(sb);
	}
}
