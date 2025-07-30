package com.f1.ami.center;

import java.util.logging.Logger;

import com.f1.utils.LH;
import com.f1.utils.sql.SqlPlanListener;

public class AmiCenterSqlPlanListener implements SqlPlanListener {

	private static final Logger log = LH.get();
	private boolean isVerbose;
	private String description;

	public AmiCenterSqlPlanListener(String description, boolean isVerbose) {
		this.description = description;
		this.isVerbose = isVerbose;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onStart(String query) {
		LH.info(log, description, "::START_QUERY ", query);

	}

	@Override
	public void onStep(String step, String msg) {
		if (isVerbose)
			LH.info(log, description, "::QUERY_STEP ", step, " ==> ", msg);

	}
	@Override
	public void onEnd(Object result) {
		LH.info(log, description, "::END_QUERY");
	}

	@Override
	public void onEndWithError(Exception e) {
		LH.info(log, description, "::END_IN_ERROR: ", e);
	}

}
