package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiCalcFrameStack;
import com.f1.ami.amiscript.AmiDebugManager;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebService;
import com.f1.base.CalcFrame;
import com.f1.utils.ToDoException;
import com.f1.utils.sql.Tableset;
import com.f1.utils.structs.table.derived.BreakpointManager;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.derived.TimeoutController;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;

public class AmiWebTopCalcFrameStack extends TopCalcFrameStack implements AmiCalcFrameStack {

	final private AmiWebDomObject thiz;
	final private byte debugType;
	final private String callbackName;
	final private AmiWebService service;
	final private AmiDebugManager debugManager;
	final private String defaultDatasource;
	final private String layoutAlias;

	public AmiWebTopCalcFrameStack(Tableset ts, int limit, TimeoutController tc, String defaultDatasource, BreakpointManager bpm, MethodFactoryManager mf, CalcFrame vars,
			CalcFrame globalConsts, AmiDebugManager debugManager, AmiWebService service, String callbackName, byte debugType, AmiWebDomObject thiz, String layoutAlias,
			CalcFrame consts) {
		super(service.getVarsManager().getStackLimit(), ts, limit, tc, null, bpm, vars, consts, mf, globalConsts, null);
		this.service = service;
		this.debugManager = debugManager;
		this.callbackName = callbackName;
		this.defaultDatasource = defaultDatasource;
		this.debugType = debugType;
		this.thiz = thiz;
		this.layoutAlias = layoutAlias;
	}

	@Override
	public AmiDebugManager getDebugManager() {
		return this.debugManager;
	}

	@Override
	public AmiWebService getService() {
		return this.service;
	}

	@Override
	public String getSourceAri() {
		return this.thiz.getAri();
	}

	@Override
	public String getCallbackName() {
		return this.callbackName;
	}

	@Override
	public byte getSourceDebugType() {
		return this.debugType;
	}

	@Override
	public AmiWebDomObject getThis() {
		return this.thiz;
	}

	@Override
	public String getUserName() {
		return this.service.getUserName();
	}

	@Override
	public String getDefaultDatasource() {
		return this.defaultDatasource;
	}

	@Override
	public String getLayoutAlias() {
		return this.layoutAlias;
	}

	public AmiWebScriptRunner getRunner() {
		throw new ToDoException();
	}

}
