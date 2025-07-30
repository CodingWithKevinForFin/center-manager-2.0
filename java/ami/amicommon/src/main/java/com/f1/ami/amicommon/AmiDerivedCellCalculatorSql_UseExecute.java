package com.f1.ami.amicommon;

import java.util.Map;

import com.f1.container.ContainerTools;
import com.f1.utils.sql.DerivedCellCalculatorSql;
import com.f1.utils.sql.SqlDerivedCellParser;
import com.f1.utils.string.sqlnode.ExecuteNode;
import com.f1.utils.string.sqlnode.SqlNode;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiDerivedCellCalculatorSql_UseExecute extends DerivedCellCalculatorSql {

	final private ExecuteNode node;
	final private Map<String, DerivedCellCalculator> use;

	public AmiDerivedCellCalculatorSql_UseExecute(ContainerTools tools, SqlDerivedCellParser dcp, SqlNode node, Map<String, DerivedCellCalculator> map, ExecuteNode next) {
		super(node, dcp);
		this.use = map;
		this.node = next;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		node.toString(sink);
		return sink;
	}
	@Override
	public FlowControl get(CalcFrameStack sf) {
		return new AmiFlowControlPauseSql_UseExecute(this, this.node, this.use, sf).push(this, sf, 0);
	}

	@Override
	public FlowControl resume(PauseStack paused) {
		AmiFlowControlPauseSql_UseExecute pause = (AmiFlowControlPauseSql_UseExecute) paused.getFlowControlPause();
		pause.throwIfError(this);
		return pause.getTableReturn();
	};
	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (!super.isSame(other))
			return false;
		AmiDerivedCellCalculatorSql_UseExecute o = (AmiDerivedCellCalculatorSql_UseExecute) other;
		return DerivedHelper.areSame(this.node, o.node) && DerivedHelper.areSame(this.use, o.use);
	}

	@Override
	public boolean isPausable() {
		return true;
	}
}
