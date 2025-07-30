package com.f1.ami.amiscript;

import java.util.Iterator;

import com.f1.base.CalcFrame;
import com.f1.utils.AH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodExample;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_Iterable extends AmiScriptBaseMemberMethods<Iterable> {

	private AmiScriptMemberMethods_Iterable() {
		super();

		addMethod(ITERATOR);
	}

	private static final AmiAbstractMemberMethod<Iterable> ITERATOR = new AmiAbstractMemberMethod<Iterable>(Iterable.class, "iterator", Iterator.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Iterable targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.iterator();
		}
		protected String[] buildParamNames() {
			return new String[] {};
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {};
		}

		@Override
		protected String getHelp() {
			return "Returns an iterator over this container's objects. Methods are defined in the Iterator class.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			// set iterator
			example.append("Set s = new Set(\"3\",\"f\",\"o\",\"r\",\"g\",\"e\");").append("\n");
			example.append("Iterator i = s.iterator();").append("\n");
			example.append("string temp = \"\";").append("\n");
			example.append("while(i.hasNext()) { ").append("\n");
			example.append("\t").append("temp += i.next();").append("\n");
			example.append("}");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "i.hasNext()", "temp" }, "", "Set"));
			
			// iterable iterator
			example = new StringBuilder();
			example.append("Set s = new Set(\"3\",\"f\",\"o\",\"r\",\"g\",\"e\");").append("\n");
			example.append("Iterator i = s.iterator();").append("\n");
			example.append("string temp = \"\";").append("\n");
			example.append("while(i.hasNext()) { ").append("\n");
			example.append("\t").append("temp += i.next();").append("\n");
			example.append("}");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "i.hasNext()", "temp" }, "", "Iterable"));
			
			// list iterator
			example = new StringBuilder();
			example.append("List l = new List(\"3\",\"f\",\"o\",\"r\",\"g\",\"e\");").append("\n");
			example.append("Iterator i = l.iterator();").append("\n");
			example.append("string temp = \"\";").append("\n");
			example.append("while(i.hasNext()) { ").append("\n");
			example.append("\t").append("temp += i.next();").append("\n");
			example.append("}");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "i.hasNext()", "temp" }, "", "List"));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};

	@Override
	public String getVarTypeName() {
		return "Iterable";
	}
	@Override
	public String getVarTypeDescription() {
		return null;
	}
	@Override
	public Class<Iterable> getVarType() {
		return Iterable.class;
	}
	@Override
	public Class<Iterable> getVarDefaultImpl() {
		return null;
	}

	public static AmiScriptMemberMethods_Iterable INSTANCE = new AmiScriptMemberMethods_Iterable();
}
