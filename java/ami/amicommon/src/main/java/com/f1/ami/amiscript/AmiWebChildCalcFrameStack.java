package com.f1.ami.amiscript;

import com.f1.base.CalcFrame;
import com.f1.utils.sql.Tableset;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.TimeoutController;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ChildCalcFrameStack;

public class AmiWebChildCalcFrameStack extends ChildCalcFrameStack implements AmiCalcFrameStack {

	private Object thiz;
	private String callbackName;
	private String defaultDatasource;
	private AmiCalcFrameStack top;

	public AmiWebChildCalcFrameStack(DerivedCellCalculator calc, CalcFrameStack parent, CalcFrame frame, CalcFrame frameConsts, Tableset tableset, Object thiz, String callbackName,
			int limit, TimeoutController tc, String defaultDatasource) {
		super(calc, false, parent, frame, frameConsts, tableset, limit, tc);
		this.thiz = thiz;
		this.callbackName = callbackName;
		this.defaultDatasource = defaultDatasource;
		this.top = (AmiCalcFrameStack) parent.getTop();
	}

	@Override
	public AmiDebugManager getDebugManager() {
		return top.getDebugManager();
	}

	@Override
	public AmiService getService() {
		return top.getService();
	}

	@Override
	public String getSourceAri() {
		return top.getSourceAri();
	}

	@Override
	public String getCallbackName() {
		return this.callbackName;
	}

	@Override
	public byte getSourceDebugType() {
		return top.getSourceDebugType();
	}

	@Override
	public Object getThis() {
		return thiz;
	}

	@Override
	public String getUserName() {
		return top.getUserName();
	}

	@Override
	public String getDefaultDatasource() {
		return defaultDatasource;
	}

	@Override
	public String getLayoutAlias() {
		return top.getLayoutAlias();
	}

}
