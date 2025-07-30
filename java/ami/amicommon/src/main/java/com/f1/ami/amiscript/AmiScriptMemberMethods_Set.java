package com.f1.ami.amiscript;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.f1.base.CalcFrame;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodExample;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_Set extends AmiScriptBaseMemberMethods<Set> {

	private AmiScriptMemberMethods_Set() {
		super();

		addMethod(INIT);
		addMethod(CONTAINS);
		addMethod(ADD);
		addMethod(REMOVE);
		addMethod(VENN);
		//		addMethod(SIZE);
		//		addMethod(CLEAR);
	}

	private static final AmiAbstractMemberMethod<Set> INIT = new AmiAbstractMemberMethod<Set>(Set.class, null, Set.class, true, Object.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, Set targetObject, Object[] params, DerivedCellCalculator caller) {
			Set r = new LinkedHashSet(Math.max(8, params.length));
			for (Object param : params)
				r.add(param);
			return r;
		}
		protected String[] buildParamNames() {
			return new String[] { "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "values contained in set" };
		}
		@Override
		protected String getHelp() {
			return "Construct a new set, duplicate values will be omitted";
		}
		
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Set s = new Set(\"a\",\"b\",\"c\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s" }));
			
			example = new StringBuilder();
			example.append("Set s = new Set(\"a\", \"a\", \"b\", null);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s" }));

			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Set> CONTAINS = new AmiAbstractMemberMethod<Set>(Set.class, "contains", Boolean.class, true, Object.class) {

		@Override
		public Boolean invokeMethod2(CalcFrameStack sf, Set targetObject, Object[] params, DerivedCellCalculator caller) {
			for (Object param : params)
				if (targetObject.contains(param))
					return true;
			return false;
		}
		protected String[] buildParamNames() {
			return new String[] { "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "value(s) to check" };
		}
		@Override
		protected String getHelp() {
			return "Returns true if the set contains ANY of the supplied values";
		}
		
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Set s = new Set(\"a\",\"b\",\"c\");").append("\n");
			example.append("s.contains(\"a\");").append("\n");
			example.append("s.contains(\"d\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.contains(\"a\")", "s.contains(\"d\")" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Set> ADD = new AmiAbstractMemberMethod<Set>(Set.class, "add", Boolean.class, true, Object.class) {
		@Override
		public Boolean invokeMethod2(CalcFrameStack sf, Set targetObject, Object[] params, DerivedCellCalculator caller) {
			boolean r = false;
			for (Object o : params)
				r |= targetObject.add(o);
			return r;
		}
		protected String[] buildParamNames() {
			return new String[] { "values" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "values to add" };
		}
		@Override
		protected String getHelp() {
			return "Adds all supplied values if they don't already exist. Returns \"true\" if the set changed as a result of this call, \"false\" if not.";
		}
		
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Set s = new Set(\"a\",\"b\",\"c\");").append("\n");
			example.append("s.add(\"a\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s"}));
			
			example = new StringBuilder();
			example.append("Set s = new Set(\"a\",\"b\",\"c\");").append("\n");
			example.append("s.add(\"d\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s"}));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<Set> VENN = new AmiAbstractMemberMethod<Set>(Set.class, "venn", Set.class, Set.class, Boolean.class, Boolean.class,
			Boolean.class) {
		@Override
		public Set invokeMethod2(CalcFrameStack sf, Set targetObject, Object[] params, DerivedCellCalculator caller) {
			Set right = (Set) params[0];
			if (right == null)
				right = Collections.EMPTY_SET;
			boolean r = Boolean.TRUE.equals((Boolean) params[1]);
			boolean l = Boolean.TRUE.equals((Boolean) params[2]);
			boolean b = Boolean.TRUE.equals((Boolean) params[3]);
			return CH.comm(targetObject, right, r, l, b);
		}
		protected String[] buildParamNames() {
			return new String[] { "otherSet", "inThis", "inOther", "inBoth" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "the other set, that along with this set constitutes a Venn diagram", "include values that are <b>only</b> in this set",
					"include values that are <b>only</b> in the other set", "include values that are in <b>both</b> sets" };
		}
		@Override
		protected String getHelp() {
			return "Returns a new set of values which is a combination of this set and the other set according to the Boolean flags set by the user. "
					+ "Each Boolean flag determines which portions of the Venn diagram are included in the final set.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Set s1 = new Set(\"a\",\"b\",\"c\",\"d\");").append("\n");
			example.append("Set s2 = new Set(\"d\",\"e\",\"f\");").append("\n");
			example.append("s1.venn(s2,true,true,false);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s1.venn(s2,true,true,false)"},  "this is the set containing elements in s1 and s2, but not in both."));
			
			example = new StringBuilder();
			example.append("Set s1 = new Set(\"a\",\"b\",\"c\",\"d\");").append("\n");
			example.append("Set s2 = new Set(\"d\",\"e\",\"f\");").append("\n");
			example.append("s1.venn(s2,false,true,false);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s1.venn(s2,false,true,false);"}, "this is the set containing no elements from s1 and only elements from s2 that are also not in s1."));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Set> REMOVE = new AmiAbstractMemberMethod<Set>(Set.class, "remove", Boolean.class, true, Object.class) {
		@Override
		public Boolean invokeMethod2(CalcFrameStack sf, Set targetObject, Object[] params, DerivedCellCalculator caller) {
			boolean r = false;
			for (Object o : params)
				r |= targetObject.remove(o);
			return r;
		}
		protected String[] buildParamNames() {
			return new String[] { "values" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "values to remove" };
		}
		@Override
		protected String getHelp() {
			return "Remove all supplied values if they exist. Returns true if the set changed as a result of this call.";
		}
		
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Set s = new Set(\"a\",\"b\",\"c\",\"d\");").append("\n");
			example.append("s.remove(\"d\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	@Override
	public String getVarTypeName() {
		return "Set";
	}
	@Override
	public String getVarTypeDescription() {
		return "A mutable collection of unique data entries. Attempts to add duplicates will be ignored.  Entries are stored in the order inserted (Backed by Java LinkedHashSet)";
	}
	@Override
	public Class<Set> getVarType() {
		return Set.class;
	}
	@Override
	public Class<? extends Set> getVarDefaultImpl() {
		return LinkedHashSet.class;
	}

	public static AmiScriptMemberMethods_Set INSTANCE = new AmiScriptMemberMethods_Set();
}
