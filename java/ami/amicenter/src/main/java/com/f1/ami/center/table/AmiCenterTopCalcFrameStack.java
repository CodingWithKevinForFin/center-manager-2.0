package com.f1.ami.center.table;

import com.f1.ami.amiscript.AmiCalcFrameStack;
import com.f1.ami.amiscript.AmiDebugManager;
import com.f1.ami.amiscript.AmiService;
import com.f1.base.CalcFrame;
import com.f1.utils.sql.SqlPlanListener;
import com.f1.utils.sql.Tableset;
import com.f1.utils.structs.table.derived.BreakpointManager;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.derived.TimeoutController;
import com.f1.utils.structs.table.stack.SqlResultset;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;

public class AmiCenterTopCalcFrameStack extends TopCalcFrameStack implements AmiCalcFrameStack {

	private AmiImdbSession session;

	public AmiCenterTopCalcFrameStack(AmiImdbSession session, int stackLimit, Tableset tableset, int limit, TimeoutController timeoutController, SqlPlanListener sqlPlanListener,
			BreakpointManager breakpointManager, CalcFrame globalVars, CalcFrame frameConsts, MethodFactoryManager methodFactory, CalcFrame consts, SqlResultset resultset) {
		super(stackLimit, tableset, limit, timeoutController, sqlPlanListener, breakpointManager, globalVars, frameConsts, methodFactory, consts, resultset);
		this.session = session;
	}

	@Override
	public AmiDebugManager getDebugManager() {
		return this.session.getDebugManager();
	}

	@Override
	public AmiService getService() {
		return this.session;
	}

	@Override
	public byte getSourceDebugType() {
		return -1;
	}

	@Override
	public String getUserName() {
		return session.getUsername();
	}

	@Override
	public String getSourceAri() {
		return null;
	}

	@Override
	public String getLayoutAlias() {
		return null;
	}

	@Override
	public String getDefaultDatasource() {
		return null;
	}

	@Override
	public String getCallbackName() {
		return null;
	}

	@Override
	public Object getThis() {
		return null;
	}

}
