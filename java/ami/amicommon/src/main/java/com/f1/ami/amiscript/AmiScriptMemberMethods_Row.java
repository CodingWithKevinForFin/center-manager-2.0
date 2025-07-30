package com.f1.ami.amiscript;

import com.f1.base.Column;
import com.f1.base.CalcFrame;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_Row extends AmiScriptBaseMemberMethods<Row> {

	private AmiScriptMemberMethods_Row() {
		super();

		addMethod(SET_VALUE);
		addMethod(GET_VALUE);
		addMethod(GET_VALUE_AT);
		addMethod(GET_TABLE);
		addMethod(GET_LOCATION);
	}

	private static final AmiAbstractMemberMethod<Row> SET_VALUE = new AmiAbstractMemberMethod<Row>(Row.class, "setValue", Object.class, String.class, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Row targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				String key = (String) params[0];
				Object val = params[1];
				Column col = targetObject.getTable().getColumnsMap().get(key);
				if (col == null)
					return null;
				return targetObject.put(key, col.getTypeCaster().cast(val));
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "columnName", "value" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "name of the column to set the value for", "value to set" };
		}

		@Override
		protected String getHelp() {
			return "Sets the value by column name, returns the old value.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<Row> GET_VALUE = new AmiAbstractMemberMethod<Row>(Row.class, "getValue", Object.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Row targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				String key = (String) params[0];
				Column col = targetObject.getTable().getColumnsMap().get(key);
				if (col == null)
					return null;
				return targetObject.get(key);
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "columnName" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "name of the column to get the value for" };
		}

		@Override
		protected String getHelp() {
			return "Returns the value by column name.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Row> GET_VALUE_AT = new AmiAbstractMemberMethod<Row>(Row.class, "getValueAt", Object.class, Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Row targetObject, Object[] params, DerivedCellCalculator caller) {
			Integer key = (Integer) params[0];
			if (key == null)
				return null;
			int k = key.intValue();
			if (k < 0 || k >= targetObject.size())
				return null;
			return targetObject.getAt(k);
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "1-based index" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "index of the list to get the value for" };
		}

		@Override
		protected String getHelp() {
			return "Returns the value by the index.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Row> GET_TABLE = new AmiAbstractMemberMethod<Row>(Row.class, "getTable", Table.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Row targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTable();
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] {};
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {};
		}

		@Override
		protected String getHelp() {
			return "Returns the table this row is a member of.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Row> GET_LOCATION = new AmiAbstractMemberMethod<Row>(Row.class, "getLocation", Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Row targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getLocation();
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] {};
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {};
		}

		@Override
		protected String getHelp() {
			return "Returns the location of this row within the table it belongs.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "Row";
	}
	@Override
	public String getVarTypeDescription() {
		return "Represents a row within a Table object. A row has one cell for each column within its table, and can be accessed using that table's column names.";
	}
	@Override
	public Class<Row> getVarType() {
		return Row.class;
	}
	@Override
	public Class<Row> getVarDefaultImpl() {
		return null;
	}

	public static AmiScriptMemberMethods_Row INSTANCE = new AmiScriptMemberMethods_Row();
}
