package com.f1.ami.center.hdb;

import com.f1.ami.amicommon.AmiFlowControlPause;
import com.f1.ami.center.hdb.events.AmiHdbRequest;
import com.f1.base.Action;
import com.f1.base.CalcFrame;
import com.f1.container.ContainerTools;
import com.f1.utils.sql.DerivedCellCalculatorSql;
import com.f1.utils.sql.SqlPlanListener;
import com.f1.utils.sql.TableReturn;
import com.f1.utils.structs.table.derived.FlowControlThrow;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;

public abstract class AmiHdbSqlFlowControl extends AmiFlowControlPause {

	private AmiHdbTable table;
	private DerivedCellCalculatorSql sqlNode;
	private FlowControlThrow error;
	protected TableReturn tableReturn;
	private CalcFrame vars;
	private SqlPlanListener sqlPlanListener;

	public AmiHdbSqlFlowControl(DerivedCellCalculatorSql node, AmiHdbTable table, CalcFrame vars, SqlPlanListener sqlPlanListener) {
		super(node);
		this.sqlPlanListener = sqlPlanListener;
		this.vars = vars;
		this.sqlNode = node;
		this.table = table;
	}

	public AmiHdbTable getTable() {
		return this.table;
	}

	public abstract void run() throws Exception;

	@Override
	public TableReturn getTableReturn() {
		return this.tableReturn;
	}

	protected CalcFrameStack createStackFrame() {
		return new TopCalcFrameStack(this.vars, this.sqlPlanListener);
	}
	@Override
	public Action toRequest(ContainerTools tools) {
		AmiHdbRequest rm = tools.nw(AmiHdbRequest.class);
		rm.setSqlFlowControl(this);
		return rm;
	}

	@Override
	public void processResponse(Action response) {
		throwIfError(this.sqlNode);
	}

}
