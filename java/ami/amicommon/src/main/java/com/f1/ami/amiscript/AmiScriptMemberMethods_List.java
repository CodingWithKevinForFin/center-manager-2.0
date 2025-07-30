package com.f1.ami.amiscript;

import java.util.ArrayList;
import java.util.List;

import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.assist.RootAssister;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodExample;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_List extends AmiScriptBaseMemberMethods<List> {

	private AmiScriptMemberMethods_List() {
		super();

		addMethod(INIT);
		addMethod(GET);
		addMethod(REMOVE);
		addMethod(SET);
		addMethod(INDEX_OF);
		addMethod(ADD);
		addMethod(SPLICE);
		addMethod(BATCH);
		addMethod(CONTAINS);
		addMethod(JSON_PATH);
	}

	private static final AmiAbstractMemberMethod<List> INIT = new AmiAbstractMemberMethod<List>(List.class, null, Object.class, true, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, List targetObject, Object[] params, DerivedCellCalculator caller) {
			try {

				ArrayList r = new ArrayList(Math.max(10, params.length));
				for (int i = 0; i < params.length; i++)
					r.add(params[i]);
				return r;
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "list_elements" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "element to be added in list construction" };
		}
		@Override
		protected String getHelp() {
			return "Initialize a list containing the specified elements in the order they were supplied.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("List l = new List(\"a\", \"b\", \"c\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l" }));
			
			example = new StringBuilder();
			example.append("List l = new List(\"a\", null, 1);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l" }));

			example = new StringBuilder();
			example.append("List py_list = [\"a\", \"b\", \"c\"];").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "py_list" }, "Python-style syntax for initiating lists in AMI"));

			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<List> CONTAINS = new AmiAbstractMemberMethod<List>(List.class, "contains", Boolean.class, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, List targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				Object value = (Object) params[0];
				return targetObject.contains(value);
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "value of element to check is in the list" };
		}
		@Override
		protected String getHelp() {
			return "Returns \"true\" if the element is in the list, \"false\" if not.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("List l = new List(\"a\", \"b\", \"c\");").append("\n");
			example.append("l.contains(\"a\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l.contains(\"a\")" }));
		
			example = new StringBuilder();
			example.append("List l = new List(\"a\", \"b\", \"c\");").append("\n");
			example.append("l.contains(\"d\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l.contains(\"d\")" })); 

			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<List> INDEX_OF = new AmiAbstractMemberMethod<List>(List.class, "indexOf", Integer.class, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, List targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				Object value = (Object) params[0];
				return targetObject.indexOf(value);
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "element to get index of" };
		}
		@Override
		protected String getHelp() {
			return "Returns the index of the first occurrence of the specified element in this list; returns -1 if no such index exists.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("List l = new List(\"a\",\"b\",\"c\");").append("\n");
			example.append("l.indexOf(\"a\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l.indexOf(\"a\")" }));
		
			example = new StringBuilder();
			example.append("List l = new List(\"a\",\"b\",\"c\");").append("\n");
			example.append("l.indexOf(\"d\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l.indexOf(\"d\")" })); 

			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};

	private static final AmiAbstractMemberMethod<List> GET = new AmiAbstractMemberMethod<List>(List.class, "get", Object.class, Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, List targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				Integer index = (Integer) params[0];
				if (index == null || index < 0 || index >= targetObject.size())
					return null;
				return targetObject.get(index);
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "index" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "zero based index" };
		}
		@Override
		protected String getHelp() {
			return "Returns the value found at the supplied index, or \"null\" if the index is null or out of bounds.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("List l = new List(\"a\",\"b\",\"c\");").append("\n");
			example.append("l.get(0);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l.get(0)" }));
		
			example = new StringBuilder();
			example.append("List l = new List(\"a\", \"b\", \"c\");").append("\n");
			example.append("l.get(3);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l.get(3);" })); 

			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<List> BATCH = new AmiAbstractMemberMethod<List>(List.class, "batch", List.class, Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, List targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				Integer batchSize = (Integer) params[0];
				return CH.batchSublists(targetObject, batchSize, false);
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "batchSize" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "the maximum size of each batch" };
		}
		@Override
		protected String getHelp() {
			return "Divides the list into batches (a list of lists), based on the supplied batch size.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder(); 
			example.append("List l = new List(\"a\", \"b\", \"c\", \"d\", \"e\", \"f\", \"g\", \"h\", \"i\", \"j\", \"k\", \"l\", \"m\");").append("\n");
			example.append("List l_batched = l.batch(5);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l_batched" } , 
					""
					+ "<br> For this example containing a list of 13 elements (the alphabet) batched using 5 would result in these three lists: <ul>"
					+ "<li> A list containing elements 1-5"
					+ "<li> A list containing elements 6-10"
					+ "<li> A list containing the remaining elements 11-13 </ul>"));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<List> ADD = new AmiAbstractMemberMethod<List>(List.class, "add", Object.class, true, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, List targetObject, Object[] params, DerivedCellCalculator caller) {
			for (Object i : params)
				targetObject.add(i);
			return null;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "values" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "ordered list of values" };
		}
		@Override
		protected String getHelp() {
			return "Adds the value(s) to the end of the list.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("List l = new List(\"a\");").append("\n");
			example.append("l.add(\"b\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l" }));

			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<List> SET = new AmiAbstractMemberMethod<List>(List.class, "set", Object.class, Integer.class, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, List targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				Integer index = (Integer) params[0];
				Object element = (Object) params[1];
				return targetObject.set((int) index, element);
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "index", "element" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "position", "new value" };
		}
		@Override
		protected String getHelp() {
			return "Replaces the element at the specified position in this list with the specified element. Returns the original value of the replaced element.";
		}
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("List l = new List(\"one\", 2, 3);").append("\n");
			example.append("l.set(0,1);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<List> REMOVE = new AmiAbstractMemberMethod<List>(List.class, "remove", Object.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, List targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				Integer index = (Integer) params[0];
				if (index == null || index < 0 || index >= targetObject.size())
					return null;
				return targetObject.remove((int) index);
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "index" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "position of value to remove" };
		}
		@Override
		protected String getHelp() {
			return "Removes the value at the supplied index. Returns the removed element.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("List l = new List(1, 2, 3, \"four\");").append("\n");
			example.append("String r = l.remove(3);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l","r" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<List> SPLICE = new AmiAbstractMemberMethod<List>(List.class, "splice", Integer.class, true, Integer.class, Integer.class,
			Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, List targetObject, Object[] params, DerivedCellCalculator caller) {
			int index = (Integer) params[0];
			int howmany = (Integer) params[1];
			CH.splice(targetObject, index, howmany, params, 2, params.length);
			return targetObject.size();
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "index", "howmany", "values" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "zero based index", "number of items to replace", "values to use in replacement" };
		}
		@Override
		protected String getHelp() {
			return "Inserts or overwrites elements in a list based on the supplied index and number of elements to replace. Returns ths size of the resulting list.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("List l = new List(\"Crooton\", \"Reggie\");").append("\n");
			example.append("l.splice(0,1,\"Crouton\", \"Artemis\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l" } , ""
					+ "This example replaces one element at index 0 with the first supplied new element, and adds the second element after."
					));
			
			example = new StringBuilder();
			example.append("List l = new List(\"Crouton\", \"Reggie\");").append("\n");
			example.append("l.splice(1,0,\"Artemis\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l" } , ""
					+ "This example does not replace any element, essentially acting as an insertion at index 1 with the supplied string \"Artemis\"."
					));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};

	@Override
	public String getVarTypeName() {
		return "List";
	}
	@Override
	public String getVarTypeDescription() {
		return "An ordered collection of data used for random access based on position. Entries are stored in the order added (Backed by Java ArrayList).";
	}
	@Override
	public Class<List> getVarType() {
		return List.class;
	}
	@Override
	public Class<? extends List> getVarDefaultImpl() {
		return ArrayList.class;
	}

	private static final AmiAbstractMemberMethod<List> JSON_PATH = new AmiAbstractMemberMethod<List>(List.class, "jsonPath", Object.class, false, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, List targetObject, Object[] params, DerivedCellCalculator caller) {
			String s = (String) params[0];
			if (s == null)
				return targetObject;
			return RootAssister.INSTANCE.getNestedValue(targetObject, s, false);
		}
		protected String[] buildParamNames() {
			return new String[] { "jsonPath" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "dot(.) delimited path" };
		}

		@Override
		protected String getHelp() {
			return "For lists with nested elements, returns the json at the supplied element path. Use dot (.) to delimit the path. If supplied null, the object is returned. "
					+ "Use numbers to traverse list elements and keys to traverse maps";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("List l = new List(new Map(\"Crouton\", \"Dog\"), new Map(\"Artemis\", \"Cat\"));").append("\n");
			example.append("String pet = l.jsonPath(\"1.Artemis\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l.jsonPath(\"1.Artemis\")", "pet" } , ""
					+ "The list \"l\" is a nested list containing maps as its nested elements. \n"
					+ "In this example, jsonPath() is used to extract the corresponding value (the type of pet) of the 1st nested map with the key \"Artemis\"."
					));
			
			example = new StringBuilder();
			example.append("List l = new List(new Map(\"Crouton\", \"Dog\"), new Map(\"Artemis\", \"Cat\"));").append("\n");
			example.append("String pet = l[1][\"Artemis\"];").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l[1][\"Artemis\"]"}, "You can access nested list elements using Python-like syntax instead by supplying each key/index in a series of square brackets." ));
			
			example = new StringBuilder();
			example.append("List l = new List(new Map(\"Crouton\", \"Dog\"), new Map(\"Artemis\", \"Cat\"));").append("\n");
			example.append("String q1 = l.jsonPath(null);").append("\n");
			example.append("String q2 = l.jsonPath(\"\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l.jsonPath(null)","l.jsonPath(\"\")"} ));
			// got null pointer exception for trying to pass "" in :( will need to add that example later
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};

	public static AmiScriptMemberMethods_List INSTANCE = new AmiScriptMemberMethods_List();
}
