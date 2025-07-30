package com.f1.ami.amiscript;

import java.util.Collection;
import java.util.List;

import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodExample;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_Collection extends AmiScriptBaseMemberMethods<Collection> {

	private AmiScriptMemberMethods_Collection() {
		super();
		addMethod(ADD_ALL);
		addMethod(SIZE);
		addMethod(CLEAR);
		addMethod(SORT);
	}

	private final static AmiAbstractMemberMethod<Collection> ADD_ALL = new AmiAbstractMemberMethod<Collection>(Collection.class, "addAll", Object.class, Iterable.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Collection targetObject, Object[] params, DerivedCellCalculator caller) {
			Iterable<?> i = (Iterable<?>) params[0];
			if (i != null)
				for (Object o : i)
					targetObject.add(o);
			return targetObject;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "valuesToAdd" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "The values to add to this collection" };
		}
		@Override
		protected String getHelp() {
			return "Adds all elements contained in the iterable to this collection";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("List l1 = new List(\"a\",\"b\");").append("\n");
			example.append("List l2 = new List(\"c\",\"d\");").append("\n");
			example.append("List l = l1.addAll(l2);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l" }, "", "List"));
			
			example = new StringBuilder();
			example.append("Set s1 = new Set(\"a\",\"b\",\"c\");").append("\n");
			example.append("Set s2 = new Set(\"c\",\"d\");").append("\n");
			example.append("Set s = s1.addAll(s2);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s" }, "", "Set"));

			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private final static AmiAbstractMemberMethod<Collection> CLEAR = new AmiAbstractMemberMethod<Collection>(Collection.class, "clear", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Collection targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject.size() == 0)
				return false;
			targetObject.clear();
			return true;
		}
		@Override
		protected String getHelp() {
			return "Removes all elements in this collection. Returns \"true\" if this operation removed at least 1 element, returns \"false\" if it was empty.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("List l = new List(\"a\",\"b\",\"c\");").append("\n");
			example.append("l.clear();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l" }, "", "List"));
			
			example = new StringBuilder();
			example.append("Set s = new Set(\"a\",\"b\",\"c\");").append("\n");
			example.append("s.clear();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s" }, "", "Set"));

			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private final static AmiAbstractMemberMethod<Collection> SIZE = new AmiAbstractMemberMethod<Collection>(Collection.class, "size", Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Collection targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return targetObject.size();
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String getHelp() {
			return "Returns the size of this collection";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("List l = new List(\"a\",\"b\",\"c\");").append("\n");
			example.append("l.size();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l.size()" }, "", "List"));
			
			example = new StringBuilder();
			example.append("Set s = new Set(\"a\",\"b\",\"c\");").append("\n");
			example.append("s.size();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.size()" }, "", "Set"));

			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<Collection> SORT = new AmiAbstractMemberMethod<Collection>(Collection.class, "sort", List.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Collection targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return CH.sort(targetObject);
			} catch (Exception e) {
				return CH.l(targetObject);
			}
		}
		@Override
		protected String getHelp() {
			return "Returns a sorted list of the specified collection. Sorting is done according to the elements' type's natural ordering. This sort is guaranteed to be stable, equal elements will not be reordered as a result of the sort.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("List l1 = new List(\"c\",\"b\",\"a\");").append("\n");
			example.append("List l2 = new List(3,1,2);").append("\n");
			example.append("l1.sort();").append("\n");
			example.append("l2.sort();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l1.sort()", "l2.sort()" }, "", "List"));
			
			example = new StringBuilder();
			example.append("Set s1 = new Set(\"c\",\"b\",\"a\");").append("\n");
			example.append("Set s2 = new Set(3,1,2);").append("\n");
			example.append("s1.sort();").append("\n");
			example.append("s2.sort();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s1.sort()", "s2.sort()" }, "", "Set"));

			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};

	@Override
	public String getVarTypeName() {
		return "Collection";
	}
	@Override
	public String getVarTypeDescription() {
		return null;
	}
	@Override
	public Class<Collection> getVarType() {
		return Collection.class;
	}
	@Override
	public Class<Collection> getVarDefaultImpl() {
		return null;
	}

	public static AmiScriptMemberMethods_Collection INSTANCE = new AmiScriptMemberMethods_Collection();
}
