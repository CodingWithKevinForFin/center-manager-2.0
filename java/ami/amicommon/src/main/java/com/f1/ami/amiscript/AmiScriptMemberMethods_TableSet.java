package com.f1.ami.amiscript;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.base.Table;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.sql.Tableset;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorExpression;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.derived.FlowControlThrow;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;

public class AmiScriptMemberMethods_TableSet extends AmiScriptBaseMemberMethods<Tableset> {

	private AmiScriptMemberMethods_TableSet() {
		super();

		addMethod(PUT);
		addMethod(GET);
		addMethod(EXISTS);
		addMethod(GET_ROWS_COUNT);
		addMethod(QUERY);
		addMethod(REMOVE);
		addMethod(CLEAR);
		addMethod(GET_NAMES);
		addCustomDebugProperty("tables", Map.class);
	}
	@Override
	protected Object getCustomDebugProperty(String name, Tableset value) {
		if ("tables".equals(name)) {
			Map<String, Table> tables = new LinkedHashMap<String, Table>();
			for (String i : value.getTableNames())
				tables.put(i, value.getTable(i));
			return tables;
		}
		return super.getCustomDebugProperty(name, value);
	}

	private final static AmiAbstractMemberMethod<Tableset> PUT = new AmiAbstractMemberMethod<Tableset>(Tableset.class, "put", Object.class, String.class, Table.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Tableset targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				Table r = targetObject.getTableNoThrow((String) params[0]);
				targetObject.putTable((String) params[0], (Table) params[1]);
				return r;
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "tableName", "table" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "name of the table to add", "table to add" };
		}
		@Override
		protected String getHelp() {
			return "Sets or replaces a table on this tableset. Returns the replaced table or null if there was an error.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private final static AmiAbstractMemberMethod<Tableset> GET = new AmiAbstractMemberMethod<Tableset>(Tableset.class, "get", BasicTable.class, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Tableset targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return targetObject.getTableNoThrow((String) params[0]);
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "tableName" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "name of the table to get" };
		}
		@Override
		protected String getHelp() {
			return "Returns a table from this tableset or null if table is not found.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private final static AmiAbstractMemberMethod<Tableset> EXISTS = new AmiAbstractMemberMethod<Tableset>(Tableset.class, "exists", Boolean.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Tableset targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return targetObject.getTableNoThrow((String) params[0]) != null;
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "tableName" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "name of the table to check for existence" };
		}
		@Override
		protected String getHelp() {
			return "Returns true if the table supplied exists in this tableset.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private final static AmiAbstractMemberMethod<Tableset> REMOVE = new AmiAbstractMemberMethod<Tableset>(Tableset.class, "remove", Object.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Tableset targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return targetObject.removeTable((String) params[0]);
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "tableName" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "name of the table to remove" };
		}
		@Override
		protected String getHelp() {
			return "Removes a table from this tableset. Returns the removed table or null if table is not found.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private final static AmiAbstractMemberMethod<Tableset> CLEAR = new AmiAbstractMemberMethod<Tableset>(Tableset.class, "clear", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Tableset targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject.getTableNames().isEmpty())
				return false;
			targetObject.clearTables();
			return true;
		}
		@Override
		protected String getHelp() {
			return "Clears the tables. Returns true if the tableset is changed as a result.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private final static AmiAbstractMemberMethod<Tableset> GET_NAMES = new AmiAbstractMemberMethod<Tableset>(Tableset.class, "getNames", Set.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Tableset targetObject, Object[] params, DerivedCellCalculator caller) {
			return new LinkedHashSet<String>(targetObject.getTableNames());
		}
		@Override
		protected String getHelp() {
			return "Returns a set of table names in the tableset.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private final static AmiAbstractMemberMethod<Tableset> QUERY = new AmiAbstractMemberMethod<Tableset>(Tableset.class, "query", Object.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack stackFrame, Tableset targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiCalcFrameStack ei = AmiUtils.getExecuteInstance2(stackFrame);

			//			AmiWebService service = getWebService(stackFrame);
			AmiService service = getService(stackFrame);
			String sql = (String) params[0];
			DerivedCellCalculatorExpression c;
			AmiWebChildCalcFrameStack t = new AmiWebChildCalcFrameStack(caller, stackFrame, EmptyCalcFrame.INSTANCE, EmptyCalcFrame.INSTANCE, targetObject, targetObject, null,
					SqlProcessor.NO_LIMIT, null, ei == null ? null : ei.getDefaultDatasource());
			try {
				c = service.getSqlProcessor().toCalc(sql, t);
			} catch (FlowControlThrow e) {
				e.getTailFrame().setOriginalSourceCode(null, sql);
				e.addFrame(caller);
				throw e;
			} catch (ExpressionParserException e) {
				throw new FlowControlThrow(caller, "Compiler Error: " + e.getMessage(), e);
			} catch (Exception e) {
				throw new FlowControlThrow(caller, "Internal Error", e);
			}
			try {
				return c.get(t);
			} catch (FlowControlThrow e) {
				e.getTailFrame().setOriginalSourceCode(null, sql);
				e.addFrame(caller);
				throw e;
			} catch (ExpressionParserException e) {
				throw new FlowControlThrow(caller, "Runtime Error: " + e.getMessage(), e);
			} catch (Exception e) {
				throw new FlowControlThrow(caller, "Internal Error", e);
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "amiSql" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "amiSql to run" };
		}
		@Override
		protected String getHelp() {
			return "Runs the specified AmiSql on this set of tables. Returns the resulting table.";
		}
		@Override
		public Object resumeMethod(CalcFrameStack sf, Tableset target, Object[] params, PauseStack paused, FlowControlPause fp, DerivedCellCalculator caller) {
			try {
				return paused.getNext().resume();
			} catch (FlowControlThrow e) {
				e.getTailFrame().setOriginalSourceCode(null, (String) params[0]);
				e.addFrame(caller);
				throw e;
			} catch (ExpressionParserException e) {
				throw new FlowControlThrow(caller, "Runtime Error: " + e.getMessage(), e);
			} catch (Exception e) {
				throw new FlowControlThrow(caller, "Internal Error", e);
			}
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
		@Override
		public boolean isPausable() {
			return false;
		}
	};

	private final static AmiAbstractMemberMethod<Tableset> GET_ROWS_COUNT = new AmiAbstractMemberMethod<Tableset>(Tableset.class, "getRowsCount", Integer.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Tableset targetObject, Object[] params, DerivedCellCalculator caller) {

			String tableName = (String) params[0];
			Table table = targetObject.getTableNoThrow(tableName);
			if (table == null)
				return -1;
			return table.getSize();
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "tableName" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "name of the table" };
		}
		@Override
		protected String getHelp() {
			return "Returns the number of rows for the given table, or -1 if table not found.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "TableSet";
	}
	@Override
	public String getVarTypeDescription() {
		return "Represents a set of named tables. Tables can be added or removed. This data structure represents the requests and responses to/from a datamodel";
	}
	@Override
	public Class<Tableset> getVarType() {
		return Tableset.class;
	}
	@Override
	public Class<Tableset> getVarDefaultImpl() {
		return null;
	}

	public static final AmiScriptMemberMethods_TableSet INSTANCE = new AmiScriptMemberMethods_TableSet();

}
