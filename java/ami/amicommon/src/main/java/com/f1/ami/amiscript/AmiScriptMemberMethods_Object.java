package com.f1.ami.amiscript;

import com.f1.utils.AH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodExample;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_Object extends AmiScriptBaseMemberMethods<Object> {

	public static final ObjectToJsonConverter JSON_CONVERTER;
	static {
		JSON_CONVERTER = new ObjectToJsonConverter();
		JSON_CONVERTER.setCompactMode(ObjectToJsonConverter.MODE_COMPACT);
		JSON_CONVERTER.registerConverterLowPriority(new AmiJsonConverter());
	}

	//	private AmiService service;

	private AmiScriptMemberMethods_Object() {
		super();
		//		this.service = service;

		addMethod(GET_CLASS_NAME);
		addMethod(TO_JSON);
	}

	private static final AmiAbstractMemberMethod<Object> TO_JSON = new AmiAbstractMemberMethod<Object>(Object.class, "toJson", String.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Object targetObject, Object[] params, DerivedCellCalculator caller) {
			return JSON_CONVERTER.objectToString(targetObject);
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
			return "Returns a string of a json representation of this object.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			// BigDecimal toJson/string 
			example.append("BigDecimal bd = new BigDecimal(1.23456789);").append("\n");
			example.append("bd.toJson();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "bd.toJson()" }, "", "BigDecimal"));
			
			// BigInteger toJson
			example = new StringBuilder();
			example.append("Binary b = new Binary(\"abc 123\");").append("\n");
			example.append("b.toJson();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "b.toJson()" }, "", "Binary")); 
			
			// Binary toJson
			example = new StringBuilder();
			example.append("BigInteger bi = new BigInteger(123456789);").append("\n");
			example.append("bi.toJson();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "bi.toJson()" }, "", "BigInteger")); 
			
			// Byte toJson
			example = new StringBuilder();
			example.append("Byte b = 1b;").append("\n");
			example.append("b.toJson();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "b.toJson()" }, "", "Byte")); 
			
			// Integer toJson
			example = new StringBuilder();
			example.append("Int i = 100;").append("\n");
			example.append("i.toJson();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "i.toJson()" }, "", "Integer")); 
			
			// Long
			example = new StringBuilder();
			example.append("Long l = 100;").append("\n");
			example.append("l.toJson();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l.toJson()" }, "", "Long")); 
			
			
			// collection (set and list)
			example = new StringBuilder();
			example.append("Collection l = new List(\"a\", \"b\", \"c\");").append("\n");
			example.append("l.toJson();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l.toJson()" }, "", "Collection"));
			
			example = new StringBuilder();
			example.append("Collection s = new Set(\"a\", \"b\", \"c\");").append("\n");
			example.append("s.toJson();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.toJson()" }, "", "Collection"));
			
			// list toJson
			example = new StringBuilder();
			example.append("List l = new List(\"a\", \"b\", \"c\");").append("\n");
			example.append("l.toJson();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l.toJson()" }, "", "List"));
			
			// map
			example = new StringBuilder();
			example.append("Map m = new Map(\"dog\",\"Crouton\",\"cat\",\"Artemis\",\"fish\",\"Tofu\");").append("\n");
			example.append("m.toJson();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "m.toJson()" }, "", "Map"));
			
			// set toJson
			example = new StringBuilder();
			example.append("Set s = new Set(\"a\", \"b\", \"c\");").append("\n");
			example.append("s.toJson();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.toJson()" }, "", "Set"));
			
			// password
			example = new StringBuilder();
			example.append("Password p = new Password(\"pass\");").append("\n");
			example.append("p.toJson();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "p.toJson()" }, "", "Password"));
			
			// point
			example = new StringBuilder();
			example.append("Point p = new Point(3,4);").append("\n");
			example.append("p.toJson();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "p.toJson()" }, "", "Point"));
		
			// random
			example = new StringBuilder();
			example.append("Rand r = new Rand();").append("\n");
			example.append("r.toJson();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "r.toJson()" }, "", "Rand"));
		
			// rectangle
			example = new StringBuilder();
			example.append("Rectangle r = new Rectangle(0,0,3,4);").append("\n");
			example.append("r.toJson();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "r.toJson()" }, "", "Rectangle"));
			
			// string
			example = new StringBuilder();
			example.append("String s = \"3forge\";").append("\n");
			example.append("s.toJson();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.toJson()" }, "", "String"));
		
			// iterator
			example = new StringBuilder();
			example.append("Set s = new Set(\"a\", \"b\", \"c\");").append("\n");
			example.append("Iterator i = s.iterator();").append("\n");
			example.append("i.toJson();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "i.toJson()" }, "", "Iterator"));
			
			// iterable set + list
			example = new StringBuilder();
			example.append("Set s = new Set(\"a\", \"b\", \"c\");").append("\n");
			example.append("Iterable i = s;").append("\n");
			example.append("i.toJson();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "i.toJson()" }, "", "Iterable"));
			
			example = new StringBuilder();
			example.append("List l = new List(\"a\", \"b\", \"c\");").append("\n");
			example.append("Iterable i = l;").append("\n");
			example.append("i.toJson();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "i.toJson()" }, "", "Iterable"));
			
			// StringBuilder
			example = new StringBuilder();
			example.append("StringBuilder sb = new StringBuilder();").append("\n");
			example.append("sb.toJson();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "sb.toJson()" }, "", "StringBuilder"));
			
			
			return examples;
		}
		
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<Object> GET_CLASS_NAME = new AmiAbstractMemberMethod<Object>(Object.class, "getClassName", String.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Object targetObject, Object[] params, DerivedCellCalculator caller) {
			return sf.getFactory().forType(targetObject.getClass());
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
			return "Returns the string name of this object's class type.";
		}
		
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			// BigDecimal 
			example.append("BigDecimal bd = new BigDecimal(1.23456789);").append("\n");
			example.append("bd.getClassName();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "bd.getClassName()" }, "", "BigDecimal"));
			
			// BigInteger 
			example = new StringBuilder();
			example.append("Binary b = new Binary(\"abc 123\");").append("\n");
			example.append("b.getClassName();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "b.getClassName()" }, "", "Binary")); 
			
			// Binary
			example = new StringBuilder();
			example.append("BigInteger bi = new BigInteger(123456789);").append("\n");
			example.append("bi.getClassName();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "bi.getClassName()" }, "", "BigInteger")); 
			
			// Byte 
			example = new StringBuilder();
			example.append("Byte b = 1b;").append("\n");
			example.append("b.getClassName();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "b.getClassName()" }, "", "Byte")); 
			
			// Integer 
			example = new StringBuilder();
			example.append("Int i = 100;").append("\n");
			example.append("i.getClassName();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "i.getClassName()" }, "", "Integer")); 
			
			// Long
			example = new StringBuilder();
			example.append("Long l = 100;").append("\n");
			example.append("l.getClassName();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l.getClassName()" }, "", "Long")); 
			
			// collection (set and list)
			example = new StringBuilder();
			example.append("Collection l = new List(\"a\", \"b\", \"c\");").append("\n");
			example.append("l.getClassName();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l.getClassName()" }, "", "Collection"));
			
			example = new StringBuilder();
			example.append("Collection s = new Set(\"a\", \"b\", \"c\");").append("\n");
			example.append("s.getClassName();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.getClassName()" }, "", "Collection"));
			
			// list toJson
			example = new StringBuilder();
			example.append("List l = new List(\"a\", \"b\", \"c\");").append("\n");
			example.append("l.getClassName();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l.getClassName()" }, "", "List"));
			
			// map
			example = new StringBuilder();
			example.append("Map m = new Map(\"dog\",\"Crouton\",\"cat\",\"Artemis\",\"fish\",\"Tofu\");").append("\n");
			example.append("m.getClassName();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "m.getClassName()" }, "", "Map"));
			
			// set toJson
			example = new StringBuilder();
			example.append("Set s = new Set(\"a\", \"b\", \"c\");").append("\n");
			example.append("s.getClassName();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.getClassName()" }, "", "Set"));
			
			// password
			example = new StringBuilder();
			example.append("Password p = new Password(\"pass\");").append("\n");
			example.append("p.getClassName();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "p.getClassName()" }, "", "Password"));
			
			// point
			example = new StringBuilder();
			example.append("Point p = new Point(3,4);").append("\n");
			example.append("p.getClassName();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "p.getClassName()" }, "", "Point"));
		
			// random
			example = new StringBuilder();
			example.append("Rand r = new Rand();").append("\n");
			example.append("r.getClassName();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "r.getClassName()" }, "", "Rand"));
		
			// rectangle
			example = new StringBuilder();
			example.append("Rectangle r = new Rectangle(0,0,3,4);").append("\n");
			example.append("r.getClassName();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "r.getClassName()" }, "", "Rectangle"));
			
			// string
			example = new StringBuilder();
			example.append("String s = \"3forge\";").append("\n");
			example.append("s.getClassName();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.getClassName()" }, "", "String"));
		
			// iterator
			example = new StringBuilder();
			example.append("Set s = new Set(\"a\", \"b\", \"c\");").append("\n");
			example.append("Iterator i = s.iterator();").append("\n");
			example.append("i.getClassName();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "i.getClassName()" }, "", "Iterator"));
			
			// iterable set + list
			example = new StringBuilder();
			example.append("Set s = new Set(\"a\", \"b\", \"c\");").append("\n");
			example.append("Iterable i = s;").append("\n");
			example.append("i.getClassName();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "i.getClassName()" }, "", "Iterable"));
			
			example = new StringBuilder();
			example.append("List l = new List(\"a\", \"b\", \"c\");").append("\n");
			example.append("Iterable i = l;").append("\n");
			example.append("i.getClassName();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "i.getClassName()" }, "", "Iterable"));
			
			// StringBuilder
			example = new StringBuilder();
			example.append("StringBuilder sb = new StringBuilder();").append("\n");
			example.append("sb.getClassName();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "sb.getClassName()" }, "", "StringBuilder"));
			
			
			return examples;
		}
		
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};

	@Override
	public String getVarTypeName() {
		return "Object";
	}
	@Override
	public String getVarTypeDescription() {
		return null;
	}
	@Override
	public Class<Object> getVarType() {
		return Object.class;
	}
	@Override
	public Class<Object> getVarDefaultImpl() {
		return null;
	}

	public static AmiScriptMemberMethods_Object INSTANCE = new AmiScriptMemberMethods_Object();
}
