package com.f1.ami.web;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.amiscript.AmiDebugManager;
import com.f1.ami.web.amiscript.AmiWebTopCalcFrameStack;
import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.sql.Tableset;
import com.f1.utils.sql.aggs.AggregateFactory;
import com.f1.utils.string.ExpressionParser;
import com.f1.utils.structs.table.derived.AggregateTable;
import com.f1.utils.structs.table.derived.BasicMethodFactory;
import com.f1.utils.structs.table.derived.DeclaredMethodFactory;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellParser;

public interface AmiWebScriptManagerForLayout {

	//identifiers
	String getLayoutAlias();

	//Parsers
	DerivedCellCalculator toCalc(String formula, CalcTypes varTypes, AmiWebDomObject portlet, Set<String> constsUsedSink);//21
	DerivedCellCalculator toCalcTemplate(String formula, CalcTypes varTypes, AmiWebDomObject portlet, Set<String> constsUsedSink);//21
	DerivedCellCalculator toCalcNotOptimized(String formula, CalcTypes varTypes, AmiWebDomObject portlet, Set<String> constsUsedSink);//2 (only for debug purposes, to avoid compiling away const expressions)

	DerivedCellCalculator parseAmiScript(String formula, CalcTypes varTypes, StringBuilder errorSink, AmiDebugManager debugManager, byte debugType, AmiWebDomObject thiz,
			String callback, boolean throwException, Set<String> constsUsedSink);//10+1

	DerivedCellCalculator parseAmiScriptTemplate(String formula, CalcTypes varTypes, StringBuilder errorSink, AmiDebugManager debugManager, byte debugType, AmiWebDomObject thiz,
			String callback, Set<String> constsUsedSink);//5

	Object parseAndExecuteAmiScript(String amiscript, StringBuilder errorSink, CalcFrame values, AmiDebugManager debugManager, byte debugType, AmiWebDomObject thiz,
			String callback);//15

	Object executeAmiScript(String amiscript, StringBuilder errorSink, DerivedCellCalculator calc, CalcFrame values, AmiDebugManager debugManager, byte debugType,
			AmiWebDomObject thiz, String callback, Tableset ts, int timeoutMs, int limit, String defaultDatasource);//1+1

	Object executeAmiScript(String amiscript, StringBuilder errorSink, DerivedCellCalculator calc, CalcFrame values, AmiDebugManager debugManager, byte debugType,
			AmiWebDomObject thiz, String callback);//4+1
	AmiWebTopCalcFrameStack createExecuteInstance(AmiDebugManager debugManager, byte debugType, AmiWebDomObject thiz, String callback, Tableset tableset, int timeoutMs, int limit,
			String defaultDatasource, CalcFrame vars);

	//Aggregate Calcs
	AggregateFactory createAggregateFactory();
	DerivedCellCalculator toAggCalc(String formula, CalcTypes varTypes, AggregateTable aggregateTable, AmiWebDomObject portlet, Set<String> constsUsedSink);//3
	DerivedCellCalculator toAggCalc(String formula, CalcTypes variables, AggregateFactory mf, AmiWebDomObject portlet, Set<String> constsUsedSink);//14

	//Types Definitions
	String forType(Class<?> type);
	Class<?> forName(String name);
	Class<?> forName(int pos, String typeName);
	Class<?> forNameNoThrow(String amiscriptClassname);

	//AVOID USING THESE
	BasicMethodFactory getMethodFactory();
	DerivedCellParser getParser();
	ExpressionParser getExpressionParser();
	SqlProcessor getSqlProcessor();

	//Consts
	Iterable<String> getConsts(AmiWebDomObject obj);
	Object getConstValue(AmiWebDomObject obj, String i);
	Class<?> getConstType(AmiWebDomObject obj, String i);
	CalcFrame getConstsMap(AmiWebDomObject obj);

	//Custom declared methods
	List<DeclaredMethodFactory> getDeclaredMethodFactories();
	String getDeclaredMethodsScript();
	BasicMethodFactory getDeclaredMethods();
	boolean setDeclaredMethods(String code, AmiDebugManager debugManager, StringBuilder errorSink);
	void setDeclaredMethodsNoCompile(String customMethods);

	//Layout callbacks
	AmiWebAmiScriptCallbacks getLayoutCallbacks();

	//Layout Variables
	com.f1.base.CalcTypes getLayourVariableTypes();
	CalcFrame getLayoutVariableValues();
	Map<String, String> getLayoutVariableScripts();
	AmiWebLayoutFile getFile();
	boolean putLayoutVariableScript(String name, String amiscript, StringBuilder errorSink);
	void removeLayoutVariable(String name);

}
