package com.f1.ami.amiscript;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.utils.AH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodExample;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_StringBuilder extends AmiScriptBaseMemberMethods<StringBuilder> {
	private static final String VAR_TYPE_DESC = "A mutable sequence of characters";
	private static final String VAR_TYPE_NAME = "StringBuilder";

	private AmiScriptMemberMethods_StringBuilder() {
		super();
		addMethod(INIT);
		addMethod(INIT2);
		addMethod(INIT3);
		addMethod(APPEND);
		addMethod(INSERT);
		addMethod(DELETE);
		addMethod(SPLICE);
		addMethod(GET_CAPACITY, "capacity");
		addMethod(GET_LENGTH, "length");
		addMethod(ENSURE_CAPACITY);
		addMethod(TRIM_CAPACITY);
		addMethod(REVERSE);
		addMethod(CLEAR);
		addMethod(SUBSTRING);
		addMethod(TO_STRING);
		addMethod(TO_STRING_AND_CLEAR);
		addMethod(GET_CHAR_AT);
		addMethod(SET_CHAR_AT);
		addMethod(DELETE_CHAR_AT);
	}

	@Override
	public String getVarTypeName() {
		return VAR_TYPE_NAME;
	}

	@Override
	public String getVarTypeDescription() {
		return VAR_TYPE_DESC;
	}

	@Override
	public Class<StringBuilder> getVarType() {
		return StringBuilder.class;
	}

	@Override
	public Class<? extends StringBuilder> getVarDefaultImpl() {
		return StringBuilder.class;
	}

	private static final AmiAbstractMemberMethod<StringBuilder> INIT = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, null, StringBuilder.class) {

		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return new StringBuilder();
		}
		
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("StringBuilder sb = new StringBuilder();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "sb.toJson()" }, "Initializes an empty StringBuilder. The toJson shows the current contents of the StringBuilder."));

			return examples;
		}
		@Override
		protected String getHelp() {
			return "Initialize a StringBuilder object";
		}
	};
	private static final AmiAbstractMemberMethod<StringBuilder> INIT2 = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, null, StringBuilder.class,
			CharSequence.class) {

		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "s" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "a String" };
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return new StringBuilder((CharSequence) params[0]);
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("StringBuilder sb = new StringBuilder(\"3f\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "sb.toJson()" }));

			return examples;
		}
		@Override
		protected String getHelp() {
			return "Initialize a StringBuilder object with the same characters as the string provided";
		}
	};
	private static final AmiAbstractMemberMethod<StringBuilder> INIT3 = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, null, StringBuilder.class, Integer.class) {

		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "capacity" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "int capacity" };
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return new StringBuilder((int) params[0]);
		}

		@Override
		protected String getHelp() {
			return "Initialize a StringBuilder object with an initial capacity";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("StringBuilder sb = new StringBuilder(1);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "sb.getCapacity()" }));

			return examples;
		}
	};

	private static final AmiAbstractMemberMethod<StringBuilder> APPEND = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "append", StringBuilder.class,
			Object.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "value to append" };
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.append(AmiUtils.s(params[0]));
		}

		@Override
		protected String getHelp() {
			return "Appends the object to the StringBuilder";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("StringBuilder sb = new StringBuilder();").append("\n");
			example.append("sb.append(\"3forge\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "sb.toJson()" }));

			return examples;
		}
	};
	private static final AmiAbstractMemberMethod<StringBuilder> INSERT = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "insert", StringBuilder.class,
			Integer.class, Object.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "offset", "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "offset to insert at", "value to append" };
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.insert((Integer) params[0], AmiUtils.s(params[1]));
		}

		@Override
		protected String getHelp() {
			return "Inserts the object to the StringBuilder at an offset";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("StringBuilder sb = new StringBuilder(\"3forge\");").append("\n");
			example.append("sb.insert(6,\" LLC\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "sb.toJson()" }));

			return examples;
		}
	};
	private static final AmiAbstractMemberMethod<StringBuilder> DELETE = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "delete", StringBuilder.class,
			Integer.class, Integer.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "start", "end" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "start position inclusive", "end position exclusive" };
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.delete((int) params[0], (int) params[1]);
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("StringBuilder sb = new StringBuilder(\"3forge\");").append("\n");
			example.append("sb.delete(2,6);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "sb.toJson()" }));

			return examples;
		}
		@Override
		protected String getHelp() {
			return "Deletes the characters in the range from the StringBuidler";
		}
	};
	private static final AmiAbstractMemberMethod<StringBuilder> SPLICE = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "splice", StringBuilder.class,
			Integer.class, Integer.class, Object.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "start", "end", "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "start position inclusive", "end position exclusive", "value to replace with" };
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.replace((int) params[0], (int) params[1], AmiUtils.s(params[2]));
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("StringBuilder sb = new StringBuilder(\"3 Forge\");").append("\n");
			example.append("sb.splice(0,3,\"3f\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "sb.toJson()" }));

			return examples;
		}
		@Override
		protected String getHelp() {
			return "Splice and replace the characters in the range from the StringBuidler with the value";
		}
	};

	private static final AmiAbstractMemberMethod<StringBuilder> GET_LENGTH = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "length", Integer.class) {

		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.length();
		}

		@Override
		protected String getHelp() {
			return "Get the length of the StringBuilder";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("StringBuilder sb = new StringBuilder(\"3forge\");").append("\n");
			example.append("sb.length();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "sb.length()" }));

			return examples;
		}
	};
	private static final AmiAbstractMemberMethod<StringBuilder> GET_CAPACITY = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "getCapacity", Integer.class) {

		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.capacity();
		}

		@Override
		protected String getHelp() {
			return "Get the capcity of the StringBuilder";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("StringBuilder sb = new StringBuilder();").append("\n");
			example.append("sb.getCapacity();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "sb.getCapacity()" }));

			return examples;
		}
	};

	private static final AmiAbstractMemberMethod<StringBuilder> ENSURE_CAPACITY = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "ensureCapacity",
			StringBuilder.class, Integer.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "capacity" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "minimum capacity" };
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.ensureCapacity((int) params[0]);
			return targetObject;
		}

		@Override
		protected String getHelp() {
			return "Ensures the capacity of the Stringbuilder is at a minimum the specified capacity";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("StringBuilder sb = new StringBuilder(0);").append("\n");
			example.append("sb.ensureCapacity(100);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "sb.getCapacity()" }));

			return examples;
		}
	};

	private static final AmiAbstractMemberMethod<StringBuilder> TRIM_CAPACITY = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "trimCapacity",
			StringBuilder.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.trimToSize();
			return targetObject;
		}

		@Override
		protected String getHelp() {
			return "Tries to reduce the capacity of the StringBuilder if the capacity is currently larger than necessary";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("StringBuilder sb = new StringBuilder(100);").append("\n");
			example.append("sb.append(\"3f\");").append("\n");
			example.append("sb.trimCapacity();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "sb.getCapacity()" }));

			return examples;
		}
	};
	private static final AmiAbstractMemberMethod<StringBuilder> CLEAR = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "clear", StringBuilder.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return SH.clear(targetObject);
		}

		@Override
		protected String getHelp() {
			return "Clears the StringBuilder";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("StringBuilder sb = new StringBuilder(\"3forge\");").append("\n");
			example.append("sb.clear();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "sb.toJson()" }));

			return examples;
		}
	};
	private static final AmiAbstractMemberMethod<StringBuilder> REVERSE = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "reverse", StringBuilder.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.reverse();
		}
		
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("StringBuilder sb = new StringBuilder(\"3forge\");").append("\n");
			example.append("sb.reverse();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "sb.toJson()" }));

			return examples;
		}
		@Override
		protected String getHelp() {
			return "Reverses the character sequence in the StringBuilder";
		}
	};

	private static final AmiAbstractMemberMethod<StringBuilder> SUBSTRING = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "substring", String.class,
			Integer.class, Integer.class) {

		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "start", "end" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "start position inclusive", "end position exclusive" };
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.substring((int) params[0], (int) params[1]);
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("StringBuilder sb = new StringBuilder(\"3forge\");").append("\n");
			example.append("sb.substring(1,6);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "sb.substring(1,6)" }));

			return examples;
		}
		@Override
		protected String getHelp() {
			return "Returns the substring within the range of StringBuilder";
		}
	};
	private static final AmiAbstractMemberMethod<StringBuilder> TO_STRING = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "toString", String.class) {

		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.toString();
		}

		@Override
		protected String getHelp() {
			return "Evaluates StringBuilder to a String";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("StringBuilder sb = new StringBuilder(\"3forge\");").append("\n");
			example.append("String s = sb.toString();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s" }));

			return examples;
		}
	};
	private static final AmiAbstractMemberMethod<StringBuilder> TO_STRING_AND_CLEAR = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "toStringAndClear",
			String.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return SH.toStringAndClear(targetObject);
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("StringBuilder sb = new StringBuilder(\"3forge\");").append("\n");
			example.append("String s = sb.toStringAndClear();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s","sb.toJson()" }));

			return examples;
		}
		@Override
		protected String getHelp() {
			return "Evaluates StringBuilder to a String and clears it";
		}
	};

	private static final AmiAbstractMemberMethod<StringBuilder> GET_CHAR_AT = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "getCharAt", Character.class,
			Integer.class) {

		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "offset" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "offset/index to get character at" };
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.charAt((int) params[0]);
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("StringBuilder sb = new StringBuilder(\"3forge\");").append("\n");
			example.append("sb.getCharAt(0);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "sb.getCharAt(0)" }));

			return examples;
		}
		@Override
		protected String getHelp() {
			return "Returns the char at the offset in the Stringbuilder";
		}
	};
	private static final AmiAbstractMemberMethod<StringBuilder> SET_CHAR_AT = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "setCharAt", StringBuilder.class,
			Integer.class, Character.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "offset", "char" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "offset/index of string", "character to set" };
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.setCharAt((int) params[0], (char) params[1]);
			return targetObject;
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("StringBuilder sb = new StringBuilder(\"3Forge\");").append("\n");
			example.append("sb.setCharAt(1,'f');").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "sb.toJson()" }));

			return examples;
		}
		@Override
		protected String getHelp() {
			return "Set the char at the offset in the StringBuilder";
		}
	};

	private static final AmiAbstractMemberMethod<StringBuilder> DELETE_CHAR_AT = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "deleteCharAt",
			StringBuilder.class, Integer.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "offset" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "offset/index of string to delete character from" };
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.deleteCharAt((int) params[0]);
		}

		@Override
		protected String getHelp() {
			return "Delete the char at the offset in the StringBuilder";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("StringBuilder sb = new StringBuilder(\"3forgee\");").append("\n");
			example.append("sb.deleteCharAt(6);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "sb.toJson()" }));

			return examples;
		}
	};

	public static final AmiScriptMemberMethods_StringBuilder INSTANCE = new AmiScriptMemberMethods_StringBuilder();
}
