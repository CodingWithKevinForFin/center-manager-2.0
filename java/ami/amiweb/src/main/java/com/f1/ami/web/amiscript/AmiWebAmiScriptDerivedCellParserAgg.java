package com.f1.ami.web.amiscript;

import com.f1.container.ContainerTools;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.string.ExpressionParser;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.node.MethodNode;
import com.f1.utils.structs.table.derived.AggregateTable;
import com.f1.utils.structs.table.derived.AggregateTable.DerivedCellAgg;
import com.f1.utils.structs.table.stack.CalcTypesStack;
import com.f1.utils.structs.table.derived.BasicExternFactoryManager;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorRef;

public class AmiWebAmiScriptDerivedCellParserAgg extends AmiWebAmiScriptDerivedCellParser {

	public AmiWebAmiScriptDerivedCellParserAgg(ExpressionParser parser, SqlProcessor sqlProcessor, ContainerTools tools, BasicExternFactoryManager externFactory,
			String layoutAlias, boolean optimized) {
		super(parser, sqlProcessor, tools, externFactory, layoutAlias, optimized);
	}

	private boolean inAgg;

	@Override
	public DerivedCellCalculatorRef newDerivedCellCalculatorRef(int position, Class type, String varname) {
		DerivedCellCalculatorRef r = super.newDerivedCellCalculatorRef(position, type, varname);
		if (!inAgg) //handle the case where we are referencing a field outside of an aggregate, just use first(...)
			r = new DerivedCellAgg(position, "first", type, r);
		return r;
	}

	//TODO:This is copy&paste from AggregateTable
	@Override
	protected DerivedCellCalculator processMethod(MethodNode mn, CalcTypesStack context) {
		String methodName = mn.getMethodName();

		if (mn.getParamsCount() == 1 && AggregateTable.AGG_METHODS.contains(methodName)) {
			int position = mn.getPosition();
			if (this.inAgg)
				throw new ExpressionParserException(position, methodName + "(..) does not support nested aggregates");
			try {
				this.inAgg = true;
				DerivedCellCalculator innerCalc = super.toCalc(mn.getParamAt(0), context);
				Class<?> type = innerCalc.getReturnType();
				if (methodName.equals("first"))//TODO: this hardcoding is bad 
					type = type;
				else if (methodName.equals("count"))//TODO: this hardcoding is bad 
					type = Integer.class;
				else if (methodName.equals("max") && type == String.class)//TODO: this hardcoding is bad 
					type = String.class;
				else if (methodName.equals("min") && type == String.class)//TODO: this hardcoding is bad 
					type = String.class;
				else if (!Number.class.isAssignableFrom(innerCalc.getReturnType()))
					throw new ExpressionParserException(position, methodName + "(..) does not support: " + innerCalc.getReturnType().getSimpleName());

				return new DerivedCellAgg(position, methodName, type, innerCalc);
			} finally {
				this.inAgg = false;
			}

		} else
			return super.processMethod(mn, context);
	}

}
