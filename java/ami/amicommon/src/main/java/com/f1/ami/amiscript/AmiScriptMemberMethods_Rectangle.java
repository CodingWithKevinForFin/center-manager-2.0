package com.f1.ami.amiscript;

import com.f1.base.CalcFrame;
import com.f1.utils.AH;
import com.f1.utils.WebRectangle;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodExample;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_Rectangle extends AmiScriptBaseMemberMethods<WebRectangle> {

	private AmiScriptMemberMethods_Rectangle() {
		super();

		addMethod(INIT);
		addMethod(GET_WIDTH, "width");
		addMethod(GET_HEIGHT, "height");
		addMethod(GET_LEFT, "left");
		addMethod(GET_TOP, "top");

	}

	private static final AmiAbstractMemberMethod<WebRectangle> INIT = new AmiAbstractMemberMethod<WebRectangle>(WebRectangle.class, null, WebRectangle.class, false, Integer.class,
			Integer.class, Integer.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, WebRectangle targetObject, Object[] params, DerivedCellCalculator caller) {
			int left = (Integer) params[0];
			int top = (Integer) params[1];
			int width = (Integer) params[2];
			int height = (Integer) params[3];
			WebRectangle r = new WebRectangle(left, top, width, height);
			return r;
		}
		protected String[] buildParamNames() {
			return new String[] { "left", "top", "width", "height" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "A measure of how far this rectangle is from the leftmost pixel. Similar to X",
					"A measure of how far this rectangle is from the topmost pixel. Similar to Y", "width of rectangle", "height of rectangle" };
		}
		@Override
		protected String getHelp() {
			return "Creates a new Rectangle.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Rectangle r = new Rectangle(0,0,3,4);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "r" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<WebRectangle> GET_LEFT = new AmiAbstractMemberMethod<WebRectangle>(WebRectangle.class, "getLeft", Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, WebRectangle targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getLeft();
		}
		@Override
		protected String getHelp() {
			return "Returns the Left value as an Integer.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Rectangle r = new Rectangle(0,0,3,4);").append("\n");
			example.append("r.getLeft();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "r.getLeft()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<WebRectangle> GET_TOP = new AmiAbstractMemberMethod<WebRectangle>(WebRectangle.class, "getTop", Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, WebRectangle targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTop();
		}
		@Override
		protected String getHelp() {
			return "Returns the Top value as an Integer.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Rectangle r = new Rectangle(0,0,3,4);").append("\n");
			example.append("r.getTop();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "r.getTop()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<WebRectangle> GET_WIDTH = new AmiAbstractMemberMethod<WebRectangle>(WebRectangle.class, "getWidth", Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, WebRectangle targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getWidth();
		}
		@Override
		protected String getHelp() {
			return "Returns the Width value as an Integer.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Rectangle r = new Rectangle(0,0,3,4);").append("\n");
			example.append("r.getWidth();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "r.getWidth()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<WebRectangle> GET_HEIGHT = new AmiAbstractMemberMethod<WebRectangle>(WebRectangle.class, "getHeight", Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, WebRectangle targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getHeight();
		}
		@Override
		protected String getHelp() {
			return "Returns the Height value as an Integer.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Rectangle r = new Rectangle(0,0,3,4);").append("\n");
			example.append("r.getHeight();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "r.getHeight()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "Rectangle";
	}
	@Override
	public String getVarTypeDescription() {
		return null;
	}
	@Override
	public Class<WebRectangle> getVarType() {
		return WebRectangle.class;
	}
	@Override
	public Class<WebRectangle> getVarDefaultImpl() {
		return null;
	}

	public static AmiScriptMemberMethods_Rectangle INSTANCE = new AmiScriptMemberMethods_Rectangle();
}
