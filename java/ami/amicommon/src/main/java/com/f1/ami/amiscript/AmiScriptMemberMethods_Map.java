package com.f1.ami.amiscript;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.utils.AH;
import com.f1.utils.SH;
import com.f1.utils.assist.RootAssister;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodExample;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_Map extends AmiScriptBaseMemberMethods<Map> {

	private AmiScriptMemberMethods_Map() {
		super();

		addMethod(INIT);
		addMethod(PUT);
		addMethod(PUT_ALL);
		addMethod(GET);
		addMethod(SIZE);
		addMethod(REMOVE);
		addMethod(GET_KEYS);
		addMethod(GET_VALUES);
		addMethod(CONTAINS_KEY);
		addMethod(CONTAINS_VALUE);
		addMethod(CLEAR);
		addMethod(JSON_PATH);
	}

	private static final AmiAbstractMemberMethod<Map> INIT = new AmiAbstractMemberMethod<Map>(Map.class, null, Object.class, true, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Map targetObject, Object[] params, DerivedCellCalculator caller) {
			if ((params.length & 1) == 1)
				throw new ExpressionParserException(caller.getPosition(), "Constructor expecting even number of arguments. Argument should be supplied as \"key\",\"value\".");
			LinkedHashMap r = new LinkedHashMap(Math.max(10, params.length / 2));
			for (int i = 0; i < params.length - 1;)
				r.put(params[i++], params[i++]);
			return r;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "key_values" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "initialize list with arguments" };
		}
		@Override
		protected String getHelp() {
			return "Initialize map";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Map m = new Map(\"dog\", \"Crouton\", \"cat\", new list(\"Artemis\",\"Reggie\"));").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "m" }));
			
			example = new StringBuilder();
			example.append("Map py_map = {\"dog\":\"Crouton\", \"cat\":[\"Artemis\",\"Reggie\"]};").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "py_map" }, "Python-style syntax for initiating maps in AMI."));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Map> PUT = new AmiAbstractMemberMethod<Map>(Map.class, "put", Object.class, Object.class, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Map targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.put(params[0], params[1]);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "key", "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key of entry", "value of entry" };
		}
		@Override
		protected String getHelp() {
			return "Adds the specified key value pair to this map and returns old value associated with the key. Returns null if key does not exist.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Map m = new Map(\"dog\", \"Crouton\");").append("\n");
			example.append("m.put(\"cat\",\"Artemis\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "m" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<Map> PUT_ALL = new AmiAbstractMemberMethod<Map>(Map.class, "putAll", Object.class, Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Map targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.putAll((Map) params[0]);
			return null;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "m" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "map to add" };
		}
		@Override
		protected String getHelp() {
			return "Copies all the mappings of the target map to this map.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Map m = new Map(\"dog\", \"Crouton\");").append("\n");
			example.append("Map m2 = new Map(\"cat\",\"Artemis\",\"fish\",\"Tofu\");").append("\n");
			example.append("m.putAll(m2);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "m" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<Map> GET = new AmiAbstractMemberMethod<Map>(Map.class, "get", Object.class, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Map targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return targetObject.get(params[0]);
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "key" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key of entry to get" };
		}
		@Override
		protected String getHelp() {
			return "Returns the value associated with the given key. Returns null if key does not exist.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Map m = new Map(\"dog\",\"Crouton\", \"cat\",new list(\"Artemis\",\"Reggie\"));").append("\n");
			example.append("m.get(\"cat\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "m.get(\"cat\")" }));
			
			example = new StringBuilder();
			example.append("Map py_map = {\"dog\":\"Crouton\", \"cat\":[\"Artemis\",\"Reggie\"]};").append("\n");
			example.append("py_map[\"cat\"][0];").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "py_map[\"cat\"][2]" }, "Python-style syntax for retrieving nested elements in maps. Alternatively, use jsonPath() instead."));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Map> REMOVE = new AmiAbstractMemberMethod<Map>(Map.class, "remove", Object.class, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Map targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return targetObject.remove(params[0]);
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "key" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key of entry to remove" };
		}
		@Override
		protected String getHelp() {
			return "Removes the given key and returns the value associated with the key. Returns null if the key does not exist.";
		}
		
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Map m = new Map(\"dog\",\"Crouton\",\"cat\",\"Artemis\",\"fish\",\"Tofu\");").append("\n");
			example.append("m.remove(\"fish\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "m" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<Map> GET_KEYS = new AmiAbstractMemberMethod<Map>(Map.class, "getKeys", Set.class) {
		@Override
		public Set invokeMethod2(CalcFrameStack sf, Map targetObject, Object[] params, DerivedCellCalculator caller) {
			return new LinkedHashSet(targetObject.keySet());
		}
		@Override
		protected String getHelp() {
			return "Returns the keys as a set.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Map m = new Map(\"dog\",\"Crouton\",\"cat\",\"Artemis\",\"fish\",\"Tofu\");").append("\n");
			example.append("m.getKeys();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "m.getKeys()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Map> GET_VALUES = new AmiAbstractMemberMethod<Map>(Map.class, "getValues", List.class) {
		@Override
		public List invokeMethod2(CalcFrameStack sf, Map targetObject, Object[] params, DerivedCellCalculator caller) {
			return new ArrayList(targetObject.values());
		}
		@Override
		protected String getHelp() {
			return "Returns the values as a list.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Map m = new Map(\"dog\",\"Crouton\",\"cat\",\"Artemis\",\"fish\",\"Tofu\");").append("\n");
			example.append("m.getValues();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "m.getValues()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Map> CLEAR = new AmiAbstractMemberMethod<Map>(Map.class, "clear", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Map targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject.size() == 0)
				return false;
			targetObject.clear();
			return true;
		}
		@Override
		protected String getHelp() {
			return "Removes all the mappings from this map. Returns \"true\" if the map is changed as a result.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Map m = new Map(\"dog\",\"Crouton\",\"cat\",\"Artemis\",\"fish\",\"Tofu\");").append("\n");
			example.append("m.clear();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "m" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<Map> SIZE = new AmiAbstractMemberMethod<Map>(Map.class, "size", Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Map targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return targetObject.size();
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String getHelp() {
			return "Returns the number of key/value pairs in this map.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Map m = new Map(\"dog\",\"Crouton\",\"cat\",\"Artemis\",\"fish\",\"Tofu\");").append("\n");
			example.append("m.size();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "m.size()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<Map> CONTAINS_KEY = new AmiAbstractMemberMethod<Map>(Map.class, "containsKey", Boolean.class, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Map targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return targetObject.containsKey(params[0]);
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "key" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key to check" };
		}
		@Override
		protected String getHelp() {
			return "Returns true if this map contains the specified key. ";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Map m = new Map(\"dog\",\"Crouton\",\"cat\",\"Artemis\",\"fish\",\"Tofu\");").append("\n");
			example.append("m.containsKey(\"cat\");").append("\n");
			example.append("m.containsKey(\"bird\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "m.containsKey(\"cat\")","m.containsKey(\"bird\")" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<Map> CONTAINS_VALUE = new AmiAbstractMemberMethod<Map>(Map.class, "containsValue", Boolean.class, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Map targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return targetObject.containsValue(params[0]);
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
			return new String[] { "value to check" };
		}
		@Override
		protected String getHelp() {
			return "Returns true if this map contains the specified value. ";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "Map";
	}

	@Override
	public String getVarTypeDescription() {
		return "A mutable collection of unique keys which are mapped to values. Attempts to add duplicates will override existing key-value pairs. Entries are stored in the order inserted (Backed by Java LinkedHashMap)";
	}

	@Override
	public Class<Map> getVarType() {
		return Map.class;
	}

	@Override
	public Class<? extends Map> getVarDefaultImpl() {
		return LinkedHashMap.class;
	}

	private static final AmiAbstractMemberMethod<Map> JSON_PATH = new AmiAbstractMemberMethod<Map>(Map.class, "jsonPath", Object.class, false, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Map targetObject, Object[] params, DerivedCellCalculator caller) {
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
			return "Checks the mapped path of the string supllied and returns the object at the end of the path if it exists. Returns itself if path is null. Use dot(.) to delimit path. Use numbers to traverse list elements and keys to traverse Maps."
					+ "Alternatively, use Python-like syntax to access map element. ";
		}@Override
		public String getMethodName() {
			// TODO Auto-generated method stub
			return super.getMethodName();
		}		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Map m = new Map(\"dogs\",new list(\"Crouton\"), \"cats\",new List(\"Artemis\",\"Reggie\"));").append("\n");
			example.append("String cats = m.jsonPath(\"cats\");").append("\n");
			example.append("String firstCat = m.jsonPath(\"cats.0\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "m.jsonPath(\"cats\")", "cats", "firstCat"} , ""
					+ "The Map \"m\" is a map of lists mapping a pet type to the list of pets corresponding to that type. \n"
					+ "In this example, jsonPath() is used to extract the corresponding value (name of pets) of the supplied key, the list \"cats\"."
					, "Map"));
			
			example = new StringBuilder();
			example.append("Map m = new Map(\"dogs\",new list(\"Crouton\"), \"cats\",new List(\"Artemis\",\"Reggie\"));").append("\n");
			example.append("String firstCat = m[\"cats\"][0];").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "m[\"cats\"][0]"}, "You can access nested map elements using Python-like syntax instead by supplying each key/index in a series of square brackets." ));
			
		
			example = new StringBuilder();
			example.append("Map m = new Map(\"dogs\",new list(\"Crouton\"), \"cats\",new List(\"Artemis\",\"Reggie\"));").append("\n");
			example.append("String q1 = m.jsonPath(null);").append("\n");
			example.append("String q2 = m.jsonPath(\"\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "m.jsonPath(null)", "m.jsonPath(\"\")"} ));

	
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static AmiScriptMemberMethods_Map INSTANCE = new AmiScriptMemberMethods_Map();
}
