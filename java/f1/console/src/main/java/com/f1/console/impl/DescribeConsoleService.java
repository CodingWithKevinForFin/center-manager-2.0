package com.f1.console.impl;

import com.f1.console.ConsoleConnection;
import com.f1.console.ConsoleSession;
import com.f1.utils.SH;

public class DescribeConsoleService extends AbstractConsoleService {

	public DescribeConsoleService() {
		super("show object", "SHOW +(DETAILED )? *OBJECT (.*)", "show the methods of an object. Usage: SHOW [DETAILED] OBJECT <objectname>");
	}

	@Override
	public void doRequest(ConsoleSession session, String[] options) {
		String var = SH.trim(options[2]);
		boolean detailed = options[1] != null;
		Object val = ShowObjectsConsoleService.getInvokable(session, var);
		if (val == null) {
			session.getConnection().comment(ConsoleConnection.COMMENT_ERROR, "Object not found");
			return;
		}
		StringBuilder sb = new StringBuilder();
		ShowObjectsConsoleService.describe(detailed, var, val.getClass(), sb);
		session.getConnection().getOut().print(sb);

	}

}
