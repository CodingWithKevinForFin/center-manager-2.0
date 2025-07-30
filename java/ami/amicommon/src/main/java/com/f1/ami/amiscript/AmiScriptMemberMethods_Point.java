package com.f1.ami.amiscript;

import com.f1.base.CalcFrame;
import com.f1.utils.AH;
import com.f1.utils.WebPoint;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodExample;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_Point extends AmiScriptBaseMemberMethods<WebPoint> {

	private AmiScriptMemberMethods_Point() {
		super();

		addMethod(INIT);
		addMethod(GET_X, "x");
		addMethod(GET_Y, "y");

	}

	private static final AmiAbstractMemberMethod<WebPoint> INIT = new AmiAbstractMemberMethod<WebPoint>(WebPoint.class, null, WebPoint.class, false, Integer.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, WebPoint targetObject, Object[] params, DerivedCellCalculator caller) {
			int x = (Integer) params[0];
			int y = (Integer) params[1];
			WebPoint r = new WebPoint(x, y);
			return r;
		}
		protected String[] buildParamNames() {
			return new String[] { "x", "y" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "x position", "y position" };
		}
		@Override
		protected String getHelp() {
			return "creates a new web point at the specified coordinate space";
		}
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Point p = new Point(3,4);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "p" }));
			
			return examples;
		}
		
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<WebPoint> GET_X = new AmiAbstractMemberMethod<WebPoint>(WebPoint.class, "getX", Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, WebPoint targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getX();
		}
		@Override
		protected String getHelp() {
			return "Get the x position";
		}
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Point p = new Point(3,4);").append("\n");
			example.append("p.getX();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "p.getX()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<WebPoint> GET_Y = new AmiAbstractMemberMethod<WebPoint>(WebPoint.class, "getY", Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, WebPoint targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getY();
		}
		@Override
		protected String getHelp() {
			return "Get the y position";
		}
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Point p = new Point(3,4);").append("\n");
			example.append("p.getY();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "p.getY()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};

	@Override
	public String getVarTypeName() {
		return "Point";
	}
	@Override
	public String getVarTypeDescription() {
		return null;
	}
	@Override
	public Class<WebPoint> getVarType() {
		return WebPoint.class;
	}
	@Override
	public Class<WebPoint> getVarDefaultImpl() {
		return null;
	}

	public static AmiScriptMemberMethods_Point INSTANCE = new AmiScriptMemberMethods_Point();
}
