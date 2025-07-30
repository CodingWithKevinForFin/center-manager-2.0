package com.f1.ami.amicommon;

import java.util.Map;

import com.f1.container.ContainerTools;
import com.f1.utils.sql.DerivedCellCalculatorSql;
import com.f1.utils.sql.SqlDerivedCellParser;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.sqlnode.SqlNode;
import com.f1.utils.string.sqlnode.SqlShowNode;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiDerivedCellCalculatorSql_UseShow extends DerivedCellCalculatorSql {

	final private SqlShowNode show;
	final private Map<String, DerivedCellCalculator> use;

	public AmiDerivedCellCalculatorSql_UseShow(ContainerTools tools, SqlDerivedCellParser dcp, SqlNode node, Map<String, DerivedCellCalculator> use, SqlShowNode show) {
		super(node, dcp);
		if (show.getFrom() != null)
			throw new ExpressionParserException(show.getFrom().getPosition(), "SHOW ... FROM ... not supported");
		this.show = show;
		this.use = use;
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		if (show.getName() != null) {
			String tableName = show.getName().getVarname();
			sink.append("SHOW TABLE ").append(tableName);
		} else
			sink.append("SHOW TABLES");
		return sink;
	}

	@Override
	public FlowControl get(CalcFrameStack sf) {
		return new AmiFlowControlPauseSql_UseShow(this, this.show, this.use, sf).push(this, sf, 0);
	}

	@Override
	public FlowControl resume(PauseStack paused) {
		AmiFlowControlPauseSql_UseShow pause = (AmiFlowControlPauseSql_UseShow) paused.getFlowControlPause();
		pause.throwIfError(this);
		return pause.getTableReturn();
	};

	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (!super.isSame(other))
			return false;
		AmiDerivedCellCalculatorSql_UseShow o = (AmiDerivedCellCalculatorSql_UseShow) other;
		return DerivedHelper.areSame(this.show, o.show) && DerivedHelper.areSame(this.use, o.use);
	}
	@Override
	public boolean isPausable() {
		return true;
	}
}
