package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.tree.AmiWebTreeColumn;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_TreePanelColumn extends AmiWebScriptBaseMemberMethods<AmiWebTreeColumn> {

	private AmiWebScriptMemberMethods_TreePanelColumn() {
		super();
		addMethod(GET_ID, "id");
		addMethod(GET_NAME, "name");
		addMethod(GET_HELP, "help");
		addMethod(GET_WIDTH, "width");
		addMethod(IS_SELECTABLE, "selectable");
		addMethod(GET_CSS_CLASS, "cssClass");
		addMethod(SET_HEADER_STYLE);
		addMethod(SET_WIDTH);
		addMethod(AUTO_SIZE);
	}

	@Override
	public String getVarTypeName() {
		return "TreePanelColumn";
	}

	@Override
	public String getVarTypeDescription() {
		return "Represents a column within a TreePanel object. It can be accessed using treePanelObj.getColumn(colName).";
	}

	@Override
	public Class<AmiWebTreeColumn> getVarType() {
		return AmiWebTreeColumn.class;
	}

	@Override
	public Class<AmiWebTreeColumn> getVarDefaultImpl() {
		return null;
	}

	private static final AmiAbstractMemberMethod<AmiWebTreeColumn> GET_ID = new AmiAbstractMemberMethod<AmiWebTreeColumn>(AmiWebTreeColumn.class, "getId", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreeColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getAmiId();
		}

		@Override
		protected String getHelp() {
			return "Returns the column id.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTreeColumn> GET_NAME = new AmiAbstractMemberMethod<AmiWebTreeColumn>(AmiWebTreeColumn.class, "getName", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreeColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getColumn().getColumnName();
		}

		@Override
		protected String getHelp() {
			return "Returns the name of the column (i.e. Country, Population etc).";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebTreeColumn> GET_HELP = new AmiAbstractMemberMethod<AmiWebTreeColumn>(AmiWebTreeColumn.class, "getHelp", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreeColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getColumn().getHelp();
		}

		@Override
		protected String getHelp() {
			return "Returns the help text associated with the column.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTreeColumn> GET_WIDTH = new AmiAbstractMemberMethod<AmiWebTreeColumn>(AmiWebTreeColumn.class, "getWidth", Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreeColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getColumn().getWidth();
		}

		@Override
		protected String getHelp() {
			return "Returns the column width.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTreeColumn> IS_SELECTABLE = new AmiAbstractMemberMethod<AmiWebTreeColumn>(AmiWebTreeColumn.class, "isSelectable",
			Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreeColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getColumn().isSelectable();
		}

		@Override
		protected String getHelp() {
			return "Returns true if column is selectable, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTreeColumn> GET_CSS_CLASS = new AmiAbstractMemberMethod<AmiWebTreeColumn>(AmiWebTreeColumn.class, "getCssClass",
			String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreeColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getColumn().getColumnCssClass();
		}

		@Override
		protected String getHelp() {
			return "Returns the css class associated with the column.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTreeColumn> SET_HEADER_STYLE = new AmiAbstractMemberMethod<AmiWebTreeColumn>(AmiWebTreeColumn.class, "setHeaderStyle",
			Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreeColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			String headerStyle = (String) params[0];
			if (headerStyle == null) {
				return false;
			}
			FastWebTreeColumn c = targetObject.getColumn();
			targetObject.setHeaderStyle(headerStyle);
			c.setHeaderStyle(targetObject.getHeaderStyleExpression());
			targetObject.getTree().getTree().onColumnChanged(c);
			return true;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "headerStyle" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "headerStyle" };
		}

		@Override
		protected String getHelp() {
			return "Sets the header style of the column. Able to set fonts,alignment,decoration,colors e.g: impact,right,italic,#ff0000";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebTreeColumn> SET_WIDTH = new AmiAbstractMemberMethod<AmiWebTreeColumn>(AmiWebTreeColumn.class, "setWidth", Object.class,
			Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreeColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			FastWebTreeColumn c = targetObject.getColumn();
			Integer i = (Integer) params[0];
			if (i != null && i >= 0 && i != c.getWidth()) {
				c.setWidth(i);
				targetObject.getTree().getTree().onColumnChanged(c);
			}
			return null;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "columnWidth" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Width of the column, must not be negative" };
		}

		@Override
		protected String getHelp() {
			return "Sets the width of the column in the TreePanel.";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebTreeColumn> AUTO_SIZE = new AmiAbstractMemberMethod<AmiWebTreeColumn>(AmiWebTreeColumn.class, "autosize", Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreeColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				targetObject.getTree().getFastTreePortlet().autoSizeColumn(targetObject.getColumnId());
				return true;
			} catch (Exception e) {
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "Autosizes this tree column. Returns true if operation is successful, false otherwise.";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	public final static AmiWebScriptMemberMethods_TreePanelColumn INSTANCE = new AmiWebScriptMemberMethods_TreePanelColumn();
}
