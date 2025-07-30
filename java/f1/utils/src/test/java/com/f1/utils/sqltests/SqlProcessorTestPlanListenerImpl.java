package com.f1.utils.sqltests;

import com.f1.utils.SH;
import com.f1.utils.sql.SqlPlanListener;

public class SqlProcessorTestPlanListenerImpl implements SqlPlanListener {

	private final StringBuilder steps = new StringBuilder();
	private final StringBuilder start = new StringBuilder();
	private Object end;

	@Override
	public void onStep(String step, String msg) {
		this.steps.append(step + " : \n" + msg + "\n\n");
	}
	public String getSteps() {
		return this.steps.toString();
	}
	public String getStart() {
		return this.start.toString();
	}
	public Object getEnd() {
		return this.end;
	}
	@Override
	public void onStart(String query) {
		if (query.startsWith("CREATE PUBLIC TABLE "))
			query = SH.replaceAll(query, "CREATE PUBLIC TABLE", "CREATE PUBLIC TABLE AS") + "  ";
		else if (query.startsWith("CREATE TABLE "))
			query = SH.replaceAll(query, "CREATE TABLE", "CREATE TABLE AS") + "  ";
		else if (query.startsWith("DELETE "))
			query = SH.replaceAll(query, "DELETE ", "DELETE FROM ");
		this.start.append(query);
	}
	@Override
	public void onEnd(Object result) {
		this.end = result;
	}
	@Override
	public void onEndWithError(Exception e) {
	}

}
