package com.f1.ami.amiscript;

import java.util.Iterator;

import com.f1.base.CalcFrame;
import com.f1.utils.AH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodExample;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_Iterator extends AmiScriptBaseMemberMethods<Iterator> {

	private AmiScriptMemberMethods_Iterator() {
		super();

		addMethod(NEXT);
		addMethod(HAS_NEXT, "hasNext");
	}

	private static final AmiAbstractMemberMethod<Iterator> NEXT = new AmiAbstractMemberMethod<Iterator>(Iterator.class, "next", Object.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Iterator targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.next();
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
			return "Returns the next value from the iterator";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Set s = new Set(1, 2, 3);").append("\n");
			example.append("Iterator i = s.iterator();").append("\n");
			example.append("string nextvalue = i.next(); //iterate to the next element").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "nextvalue", "i.next()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<Iterator> HAS_NEXT = new AmiAbstractMemberMethod<Iterator>(Iterator.class, "hasNext", Object.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Iterator targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.hasNext();
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
			return "Returns \"true\" if there are more elements remaining in the iterator, i.e when next() returns an element, and \"false\" if not. ";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Set s = new Set(1, 2, 3);").append("\n");
			example.append("Iterator i = s.iterator();").append("\n");
			example.append("int setsize = 0;").append("\n");
			example.append("while(i.hasNext()) { ").append("\n");
			example.append("\t").append("i.next();").append("\n").append("\t").append("setsize++;").append("\n");
			example.append("}");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "setsize" }, "Where sets don't natively have a size() method, iterators can be used instead to determine the size of a set."));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};

	@Override
	public String getVarTypeName() {
		return "Iterator";
	}
	@Override
	public String getVarTypeDescription() {
		return null;
	}
	@Override
	public Class<Iterator> getVarType() {
		return Iterator.class;
	}
	@Override
	public Class<Iterator> getVarDefaultImpl() {
		return null;
	}

	public static AmiScriptMemberMethods_Iterator INSTANCE = new AmiScriptMemberMethods_Iterator();
}
