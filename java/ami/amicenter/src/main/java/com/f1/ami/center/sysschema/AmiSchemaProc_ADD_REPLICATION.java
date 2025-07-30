package com.f1.ami.center.sysschema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.ami.center.procs.AmiAbstractStoredProc;
import com.f1.ami.center.procs.AmiStoredProcBindingImpl;
import com.f1.ami.center.procs.AmiStoredProcRequest;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiTableUtils;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.string.JavaExpressionParser;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.string.node.VariableNode;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiSchemaProc_ADD_REPLICATION extends AmiAbstractStoredProc {

	private static final String TYPE = "__SYSTEM";
	private static final String NAME = "__ADD_REPLICATION";
	private static final List<AmiFactoryOption> __ARGUMENTS = new ArrayList<AmiFactoryOption>();
	static {
		__ARGUMENTS.add(new AmiFactoryOption("Definition", String.class, true));
		__ARGUMENTS.add(new AmiFactoryOption("Name", String.class, false));
		__ARGUMENTS.add(new AmiFactoryOption("Mapping", String.class, false));
		__ARGUMENTS.add(new AmiFactoryOption("Options", String.class, false));
	}
	private AmiImdbImpl imdb;

	public AmiSchemaProc_ADD_REPLICATION(AmiImdbImpl imdb, CalcFrameStack sf) {
		this.imdb = imdb;
		this.imdb.getObjectsManager()
				.addAmiStoredProcBinding(new AmiStoredProcBindingImpl(this.imdb, NAME, this, TYPE, Collections.EMPTY_MAP, Collections.EMPTY_MAP, AmiTableUtils.DEFTYPE_SYSTEM), sf);
	}
	@Override
	public FlowControl execute(AmiStoredProcRequest request, CalcFrameStack sf) throws Exception {
		List<Object> arguments = request.getArguments();
		String definition = (String) arguments.get(0);
		String name = (String) arguments.get(1);
		String mapping = (String) arguments.get(2);
		String options = (String) arguments.get(3);
		if (SH.isnt(mapping))
			mapping = null;
		if (SH.isnt(options))
			options = null;
		OperationNode node;
		try {
			node = (OperationNode) new JavaExpressionParser().parse(definition);
		} catch (Exception e) {
			throw new RuntimeException("Sytax error in definition, expecting Center.Table or TargetTable=Center.Table", e);
		}
		String sourceCenter;
		String sourceTable;
		String targetTable;
		if ((node.getOp() == OperationNode.OP_EQ)) {
			OperationNode right = (OperationNode) node.getRight();
			OH.assertEq(right.getOp(), OperationNode.OP_PERIOD);
			targetTable = ((VariableNode) node.getLeft()).getVarname();
			sourceCenter = ((VariableNode) right.getLeft()).getVarname();
			sourceTable = ((VariableNode) right.getRight()).getVarname();
		} else {
			OH.assertEq(node.getOp(), OperationNode.OP_PERIOD);
			sourceCenter = ((VariableNode) node.getLeft()).getVarname();
			sourceTable = ((VariableNode) node.getRight()).getVarname();
			targetTable = sourceTable;
		}
		if (SH.isnt(name))
			name = sourceCenter + "." + sourceTable;
		imdb.getReplicator().addReplication(name, targetTable, sourceCenter, sourceTable, mapping, options, sf);
		return null;
	}

	@Override
	public List<AmiFactoryOption> getArguments() {
		return __ARGUMENTS;
	}

	@Override
	protected void onStartup(CalcFrameStack sf) {
	}

	public static void main(String[] t) {
		OperationNode node = (OperationNode) new JavaExpressionParser().parse("`test`=this.that");
		System.out.println(node);

	}

}
