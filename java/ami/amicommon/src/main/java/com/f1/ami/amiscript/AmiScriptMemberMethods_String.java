package com.f1.ami.amiscript;

import java.util.ArrayList;
import java.util.List;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodExample;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_String extends AmiScriptBaseMemberMethods<String> {

	private AmiScriptMemberMethods_String() {
		super();
		addMethod(INIT);
		addMethod(CHAR_AT);
		addMethod(ENDS_WITH);
		addMethod(INDEX_OF);
		addMethod(INDEX_OF1);
		addMethod(IS_EMPTY);
		addMethod(LAST_INDEX_OF);
		addMethod(LAST_INDEX_OF1);
		addMethod(LENGTH);
		addMethod(REPLACE_ALL);
		addMethod(SPLIT);
		addMethod(STARTS_WITH);
		addMethod(STARTS_WITH1);
		addMethod(SUBSTRING);
		addMethod(SUBSTRING1);
		addMethod(TO_LOWER);
		addMethod(TO_UPPER);
		addMethod(TRIM);
		addMethod(CHARS);
		addMethod(BEFORE);
		addMethod(BEFORE_LAST);
		addMethod(AFTER);
		addMethod(AFTER_LAST);
		addMethod(SPLIT_LINES);
		addMethod(CUT);
		addMethod(IS);
		addMethod(ISNT);
		addMethod(SPLICE);
		addMethod(STRIP);
		addMethod(AmiScriptMemberMethods_String.DECODE_TO_BYTE);
		addMethod(AmiScriptMemberMethods_String.DECODE_TO_LONG);
		addMethod(AmiScriptMemberMethods_String.DECODE_TO_SHORT);
	}

	private static final AmiAbstractMemberMethod<String> INIT = new AmiAbstractMemberMethod<String>(String.class, null, Object.class, false, String.class) {

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
		// string constructor
		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			return params[0].toString();
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"3forge\";").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s" }));
			
			return examples;
		}

		@Override
		protected String getHelp() {
			return "Initialize a string object";
		}

	};

	private static final AmiAbstractMemberMethod<String> CHAR_AT = new AmiAbstractMemberMethod<String>(String.class, "charAt", Character.class, Integer.class) {
		@Override
		protected String[] buildParamNames() {
			return new String[] { "index" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Location of the character" };
		}
		@Override
		protected String getHelp() {
			return "Returns the character at the specified index. Equivalent to the AmiScript method strChartAt().";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"3forge\";").append("\n");
			example.append("s.charAt(0);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.charAt(0)" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			Integer pos = (Integer) params[0];
			return pos == null ? null : targetObject.charAt(pos);
		};
	};

	private static final AmiAbstractMemberMethod<String> ENDS_WITH = new AmiAbstractMemberMethod<String>(String.class, "endsWith", Boolean.class, String.class, Boolean.class) {
		@Override
		protected String[] buildParamNames() {
			return new String[] { "suffix", "ignore_case" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "string to test", "to ignore case set to \"true\", \"false\" otherwise." };
		}
		@Override
		protected String getHelp() {
			return "Returns \"true\" if this string ends with the specified suffix, \"false\" otherwise. Same as AmiScript method strEndsWith().";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"3forge\";").append("\n");
			example.append("s.endsWith(\"forge\",true);").append("\n");
			example.append("s.endsWith(\"hello\",true);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.endsWith(\"forge\",true)","s.endsWith(\"hello\",true)" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			String find = (String) params[0];
			Boolean ignoreCase = (Boolean) params[1];
			if (find == null)
				return false;
			if (Boolean.TRUE.equals(ignoreCase))
				return SH.endsWithIgnoreCase(targetObject, find);
			return SH.endsWith(targetObject, find);
		};
	};

	private static final AmiAbstractMemberMethod<String> INDEX_OF = new AmiAbstractMemberMethod<String>(String.class, "indexOf", Integer.class, String.class) {
		@Override
		protected String[] buildParamNames() {
			return new String[] { "to_find" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "string to find" };
		}
		@Override
		protected String getHelp() {
			return "Returns the index within this string of the first occurrence of the specified string, returns -1 if no such occurrence is found. Same as AmiScript method strIndexOf() with one argument.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"3forge\";").append("\n");
			example.append("s.indexOf(\"forge\");").append("\n");
			example.append("s.indexOf(\"hello\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.indexOf(\"forge\")","s.indexOf(\"hello\")" }));
			
			return examples;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			return params[0] == null ? -1 : targetObject.indexOf((String) params[0]);
		};
	};

	private static final AmiAbstractMemberMethod<String> STRIP = new AmiAbstractMemberMethod<String>(String.class, "strip", String.class, String.class, String.class) {
		@Override
		protected String[] buildParamNames() {
			return new String[] { "prefix", "suffix" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "The prefix to strip", "The suffix to strip" };
		}
		@Override
		protected String getHelp() {
			return "Same as AmiScript method strStrip(). Returns the substring of supplied text with the prefix and suffix removed. If the string doesn't start with the specified prefix, then the prefix is ignored. If the string doesn't end with suffix, then the suffix is ignored.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"3forge\";").append("\n");
			example.append("s.strip(\"3\",\"\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.strip(\"3\",\"\")" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			String prefixVal = (String) params[0];
			String suffixVal = (String) params[1];
			return SH.strip(targetObject, prefixVal, suffixVal, false);
		};
	};

	private static final AmiAbstractMemberMethod<String> INDEX_OF1 = new AmiAbstractMemberMethod<String>(String.class, "indexOf", Integer.class, String.class, Integer.class) {
		@Override
		protected String[] buildParamNames() {
			return new String[] { "to_find", "start_index" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "string to find", "index to begin" };
		}
		@Override
		protected String getHelp() {
			return "Returns the index within this string of the first occurrence of the specified string, starting from a specific index, returns -1 if no such occurrence is found. Same as AmiScript method strIndexOf() with two arguments.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"3forge:3forge\";").append("\n");
			example.append("s.indexOf(\"3forge\",0);").append("\n");
			example.append("s.indexOf(\"3forge\",1);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.indexOf(\"3forge\",0)","s.indexOf(\"3forge\",1)" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			return params[0] == null || params[1] == null ? -1 : targetObject.indexOf((String) params[0], (Integer) params[1]);
		};
	};

	private static final AmiAbstractMemberMethod<String> IS_EMPTY = new AmiAbstractMemberMethod<String>(String.class, "isEmpty", Boolean.class) {
		@Override
		protected String getHelp() {
			return "Returns \"true\" if, and only if, length() is 0 (i.e, an empty string).";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s1 = \"3forge\";").append("\n");
			example.append("String s2 = \"\";").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s1.isEmpty()","s2.isEmpty()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.isEmpty();
		};
	};

	private static final AmiAbstractMemberMethod<String> LAST_INDEX_OF = new AmiAbstractMemberMethod<String>(String.class, "lastIndexOf", Integer.class, String.class) {
		@Override
		protected String[] buildParamNames() {
			return new String[] { "to_find" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "string to find" };
		}
		@Override
		protected String getHelp() {
			return "Returns the index within this string of the last occurrence of the specified string, returns -1 if not found. This method is case sensitive, to ignore case use the other lastIndexOf() method and set the ignore case flag. Also equivalent to AmiScript method strLastIndexOf().";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"3forge\";").append("\n");
			example.append("s.lastIndexOf(\"3f\");").append("\n");
			example.append("s.lastIndexOf(\"3F\");").append("\n");
					
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.lastIndexOf(\"3f\")","s.lastIndexOf(\"3F\")" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			return params[0] == null ? -1 : targetObject.lastIndexOf((String) params[0]);
		};
	};

	private static final AmiAbstractMemberMethod<String> LAST_INDEX_OF1 = new AmiAbstractMemberMethod<String>(String.class, "lastIndexOf", Integer.class, String.class,
			Integer.class, Boolean.class) {
		@Override
		protected String[] buildParamNames() {
			return new String[] { "to_find", "last", "ignore_case" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "string to find", "index to begin", "\"true\" if ignore case, \"false\" otherwise." };
		}
		@Override
		protected String getHelp() {
			return "Returns the index within this string of the last occurrence of the specified string by searching from the specified index backwards. Returns -1 if not found. Same as AmiScript method strLastIndexOf(), with a suppliable ignore case flag.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"3forge:3forge\";").append("\n");
			example.append("s.lastIndexOf(\"3F\",7,true);").append("\n");
			example.append("s.lastIndexOf(\"3F\",6,true);").append("\n");
			example.append("s.lastIndexOf(\"3F\",6,false);").append("\n");
					
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.lastIndexOf(\"3F\",7,true)","s.lastIndexOf(\"3F\",6,true)","s.lastIndexOf(\"3F\",6,false)" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			String find = (String) params[0];
			Integer index = (Integer) params[1];
			Boolean ignoreCase = (Boolean) params[2];
			if (index == null || find == null)
				return -1;
			if (Boolean.TRUE.equals(ignoreCase))
				return SH.lastIndexOfIgnoreCase(targetObject, find, index);
			else
				return targetObject.lastIndexOf(find, index);
		};
	};

	private static final AmiAbstractMemberMethod<String> LENGTH = new AmiAbstractMemberMethod<String>(String.class, "length", Integer.class) {
		@Override
		protected String getHelp() {
			return "Returns the length of this string. Same as AmiScript method strLen().";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"3forge\";").append("\n");
			example.append("s.length();").append("\n");
			
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.length()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.length();
		};
	};

	private static final AmiAbstractMemberMethod<String> REPLACE_ALL = new AmiAbstractMemberMethod<String>(String.class, "replaceAll", String.class, String.class, String.class) {
		@Override
		protected String[] buildParamNames() {
			return new String[] { "to_replace", "replacement" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "string to find", "string replacement" };
		}
		@Override
		protected String getHelp() {
			return "Replaces each substring of this string that matches the literal target sequence with the specified literal replacement sequence. Same as AmiScript method strReplace().";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"3 Forge\";").append("\n");
			example.append("s.replaceAll(\"3 F\",\"3f\");").append("\n");
			
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.replaceAll(\"3 F\",\"3f\")" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			return params[0] == null || params[1] == null ? null : SH.replaceAll((String) targetObject, (String) params[0], (String) params[1]);
		};
	};

	private static final AmiAbstractMemberMethod<String> SPLIT = new AmiAbstractMemberMethod<String>(String.class, "split", List.class, String.class) {
		@Override
		protected String[] buildParamNames() {
			return new String[] { "delimiter" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "delimiter to split by" };
		}
		@Override
		protected String getHelp() {
			return "Returns a list of strings, split by the given delimiter.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"Crouton, Artemis, Reggie\";").append("\n");
			example.append("s.split(\", \");").append("\n");
			
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.split(\", \")" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			String delim = (String) params[0];
			return delim == null ? null : CH.l(delim.length() == 1 ? SH.split(delim.charAt(0), targetObject) : SH.split(delim, targetObject));
		};
	};

	private static final AmiAbstractMemberMethod<String> STARTS_WITH = new AmiAbstractMemberMethod<String>(String.class, "startsWith", Boolean.class, String.class, Boolean.class) {
		@Override
		protected String[] buildParamNames() {
			return new String[] { "s", "ignore_case" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "string to find", "\"true\" to ignore case, \"false\" otherwise" };
		}
		@Override
		protected String getHelp() {
			return "Tests if this string starts with the specified prefix. Same as AmiScript strStartsWith().";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"3forge\";").append("\n");
			example.append("s.startsWith(\"3F\",true);").append("\n");
			
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.startsWith(\"3F\",true)" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			String find = (String) params[0];
			Boolean ignoreCase = (Boolean) params[1];
			if (find == null)
				return false;
			if (Boolean.TRUE.equals(ignoreCase))
				return SH.startsWithIgnoreCase(targetObject, find);
			return SH.startsWith(targetObject, find);
		};
	};

	private static final AmiAbstractMemberMethod<String> STARTS_WITH1 = new AmiAbstractMemberMethod<String>(String.class, "startsWith", Boolean.class, String.class, Integer.class,
			Boolean.class) {
		@Override
		protected String[] buildParamNames() {
			return new String[] { "s", "start", "ignore_case" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "string to find", "index to start", "\"true\" to ignore case, \"false\" otheriwse" };
		}
		@Override
		protected String getHelp() {
			return "Tests if this string starts with the specified prefix, starting from the given index.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"3forge\";").append("\n");
			example.append("s.startsWith(\"3f\",0,false);").append("\n");
			
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.startsWith(\"3f\",0,false)" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			String find = (String) params[0];
			Integer offset = (Integer) params[1];
			Boolean ignoreCase = (Boolean) params[2];
			if (find == null || offset == null)
				return false;
			if (Boolean.TRUE.equals(ignoreCase))
				return SH.startsWithIgnoreCase(targetObject, find, offset);
			return SH.startsWith(targetObject, find, offset);
		};
	};

	private static final AmiAbstractMemberMethod<String> SUBSTRING = new AmiAbstractMemberMethod<String>(String.class, "substring", String.class, Integer.class) {
		@Override
		protected String[] buildParamNames() {
			return new String[] { "begin" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "index to begin" };
		}
		@Override
		protected String getHelp() {
			return "Returns a string that is a substring of this string, starting from the specified index to the end of the string.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"3forge\";").append("\n");
			example.append("s.substring(1);").append("\n");
			
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.substring(1)" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			return params[0] == null ? null : SH.substring(targetObject, (Integer) params[0], targetObject.length());
		};
	};

	private static final AmiAbstractMemberMethod<String> SUBSTRING1 = new AmiAbstractMemberMethod<String>(String.class, "substring", String.class, Integer.class, Integer.class) {
		@Override
		protected String[] buildParamNames() {
			return new String[] { "begin", "end" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "index to begin", "index to end" };
		}
		@Override
		protected String getHelp() {
			return "Returns a string that is a substring of this string, start and end at specific index. Same as AmiScript strSubstring().";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"3forge\";").append("\n");
			example.append("s.substring(1,6);").append("\n");
			
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.substring(1,6)" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			return params[0] == null || params[1] == null ? null : SH.substring(targetObject, (Integer) params[0], (Integer) params[1]);
		};
	};

	private static final AmiAbstractMemberMethod<String> TO_LOWER = new AmiAbstractMemberMethod<String>(String.class, "toLower", String.class) {
		@Override
		protected String getHelp() {
			return "Converts all of the characters in this String to lower case using the rules of the default locale. Same as AmiScript strLower().";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"3FORGE\";").append("\n");
			example.append("s.toLower();").append("\n");
			
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.toLower()" }));
			
			return examples;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.toLowerCase();
		};
	};

	private static final AmiAbstractMemberMethod<String> TO_UPPER = new AmiAbstractMemberMethod<String>(String.class, "toUpper", String.class) {
		@Override
		protected String getHelp() {
			return "Converts all of the characters in this String to upper case using the rules of the default locale. Same as AmiScript strUpper().";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"3forge\";").append("\n");
			example.append("s.toUpper();").append("\n");
			
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.toUpper()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.toUpperCase();
		};
	};

	private static final AmiAbstractMemberMethod<String> TRIM = new AmiAbstractMemberMethod<String>(String.class, "trim", String.class) {
		@Override
		protected String getHelp() {
			return "Returns a string whose value is this string, with any leading and trailing whitespace removed. Same as strTrim().";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"   3forge   \";").append("\n");
			example.append("s.trim();").append("\n");
			
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.trim()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.trim();
		};
	};

	private static final AmiAbstractMemberMethod<String> CHARS = new AmiAbstractMemberMethod<String>(String.class, "chars", List.class) {
		@Override
		protected String getHelp() {
			return "Returns a list whose length is the length of this string and whose contents are initialized to contain the character sequence represented by this string.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"3forge\";").append("\n");
			example.append("s.chars();").append("\n");
			
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.chars()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			char[] cArray = targetObject.toCharArray();
			List<Character> res = new ArrayList<Character>(cArray.length);
			for (char c : cArray)
				res.add(c);
			return res;
		};
	};

	private static final AmiAbstractMemberMethod<String> BEFORE_LAST = new AmiAbstractMemberMethod<String>(String.class, "beforeLast", String.class, String.class, Boolean.class) {
		@Override
		protected String[] buildParamNames() {
			return new String[] { "toFind", "origIfNotFound" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "The substring to delimit before", "If the toFind parameter doesn't exist in the supplied string, then return the full string if true or null if false." };
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"3forge:company\";").append("\n");
			example.append("s.beforeLast(\":company\",true);").append("\n");
			example.append("s.beforeLast(\"3forge\",true);").append("\n");
			
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.beforeLast(\":company\",true)","s.beforeLast(\"3forge\",true)" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject == null)
				return null;
			String toFind = (String) params[0];
			Boolean origIfNotFound = (Boolean) params[1];
			if (toFind == null)
				return Boolean.TRUE.equals(origIfNotFound) ? targetObject : null;
			int i = targetObject.lastIndexOf(toFind);
			if (i == -1)
				return Boolean.TRUE.equals(origIfNotFound) ? targetObject : null;
			return targetObject.substring(0, i);
		}
		@Override
		protected String getHelp() {
			return "Get the portion of a string before the last occurrence of a delimiter. If the delimiter is not found, then return either the original string or null depending on origIfNotFound param.";
		}
	};

	private static final AmiAbstractMemberMethod<String> BEFORE = new AmiAbstractMemberMethod<String>(String.class, "before", String.class, String.class, Boolean.class) {
		@Override
		protected String[] buildParamNames() {
			return new String[] { "toFind", "origIfNotFound" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "The substring to delimit after", "If the toFind parameter doesn't exist in the text, then return text if true or null if false." };
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"3forge:company\";").append("\n");
			example.append("s.before(\":company\",true);").append("\n");
			example.append("s.before(\"3forge\",true);").append("\n");
			
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.before(\":company\",true)","s.before(\"3forge\",true)" }));
			
			return examples;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject == null)
				return null;
			String toFind = (String) params[0];
			Boolean origIfNotFound = (Boolean) params[1];
			if (toFind == null)
				return Boolean.TRUE.equals(origIfNotFound) ? targetObject : null;
			int i = targetObject.indexOf(toFind);
			if (i == -1)
				return Boolean.TRUE.equals(origIfNotFound) ? targetObject : null;
			return targetObject.substring(0, i);
		}

		@Override
		protected String getHelp() {
			return "Get the portion of a string before the first occurence of a delimiter. If the delimiter is not found, then return either the original string or null depending on origIfNotFound param. Same as AmiScript strBefore().";
		}
	};

	private static final AmiAbstractMemberMethod<String> AFTER = new AmiAbstractMemberMethod<String>(String.class, "after", String.class, String.class, Boolean.class) {
		@Override
		protected String[] buildParamNames() {
			return new String[] { "toFind", "origIfNotFound" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "The substring to find", "If the toFind parameter doesn't exist in the text, then return text if true or null if false." };
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"3forge:company\";").append("\n");
			example.append("s.after(\":company\",true);").append("\n");
			example.append("s.after(\"3forge\",true);").append("\n");
			
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.after(\":company\",true)","s.after(\"3forge\",true)" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject == null)
				return null;
			String toFind = (String) params[0];
			Boolean origIfNotFound = (Boolean) params[1];
			if (toFind == null)
				return Boolean.TRUE.equals(origIfNotFound) ? targetObject : null;
			int i = targetObject.indexOf(toFind);
			if (i == -1)
				return Boolean.TRUE.equals(origIfNotFound) ? targetObject : null;
			return targetObject.substring(i + toFind.length());
		}

		@Override
		protected String getHelp() {
			return "Get the portion of a string after the first occurence of a delimiter. If the delimiter is not found, then return either the original string or null depending on origIfNotFound param. Same as AmiScript strAfter().";
		}
	};

	private static final AmiAbstractMemberMethod<String> AFTER_LAST = new AmiAbstractMemberMethod<String>(String.class, "afterLast", String.class, String.class, Boolean.class) {
		@Override
		protected String[] buildParamNames() {
			return new String[] { "toFind", "origIfNotFound" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "The substring to find", "If the toFind parameter doesn't exist in the text, then return text if true or null if false." };
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"3forge:company\";").append("\n");
			example.append("s.afterLast(\":company\",true);").append("\n");
			example.append("s.afterLast(\"3forge\",true);").append("\n");
			
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.afterLast(\":company\",true)","s.afterLast(\"3forge\",true)" }));
			
			return examples;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject == null)
				return null;
			String toFind = (String) params[0];
			Boolean origIfNotFound = (Boolean) params[1];
			if (toFind == null)
				return Boolean.TRUE.equals(origIfNotFound) ? targetObject : null;
			int i = targetObject.lastIndexOf(toFind);
			if (i == -1)
				return Boolean.TRUE.equals(origIfNotFound) ? targetObject : null;
			return targetObject.substring(i + toFind.length());
		}

		@Override
		protected String getHelp() {
			return "Get the portion of a string after the last occurence of a delimiter. If the delimiter is not found, then return either the original string or null depending on origIfNotFound param. Same as AmiScript strAfterLast().";
		}
	};

	private static final AmiAbstractMemberMethod<String> SPLIT_LINES = new AmiAbstractMemberMethod<String>(String.class, "splitLines", List.class) {
		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			return CH.l(SH.splitLines(targetObject));
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"Crouton \n Reggie \n Artemis\";").append("\n");
			example.append("s.splitLines();").append("\n");
			
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.splitLines()" }));
			
			return examples;
		}
		@Override
		protected String getHelp() {
			return "Splits lines and returns a list of lines separated by line feed and line return chars.";
		}
	};

	private static final AmiAbstractMemberMethod<String> CUT = new AmiAbstractMemberMethod<String>(String.class, "cut", String.class, String.class, String.class) {
		@Override
		protected String[] buildParamNames() {
			return new String[] { "delim", "fieldList" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "delimiter, literal not a pattern.",
					"fields to return within a range n-m. Use n,m... for individual fields, or -n for number of fields up to and including that index, or n- for fields including and after that index." };
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"3,f,o,r,g,e\";").append("\n");
			example.append("s.cut(\",\",\"1-5\");").append("\n");
			example.append("s.cut(\",\", \"1-\");").append("\n");	
			example.append("s.cut(\",\", \"-3\");").append("\n");	
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.cut(\",\", \"1-5\")", "s.cut(\",\", \"1-\")", "s.cut(\",\", \"-3\")" }));
			
			return examples;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			String delim = (String) params[0];
			int[] fields = parseFields((String) params[1]);
			return fields == null ? null : SH.cut(targetObject, delim, fields);
		}

		public int[] parseFields(String string) {
			try {
				if (string == null)
					return null;
				else if (string.indexOf(',') != -1) {
					String[] parts = SH.split(',', string);
					int r[] = new int[parts.length * 2];
					int pos = 0;
					for (String part : parts) {
						String start = SH.trim(SH.beforeFirst(part, '-'));
						String end = SH.trim(SH.afterFirst(part, '-'));
						int s = "".equals(start) ? 0 : SH.parseIntSafe(start, false, false);
						int e = "".equals(end) ? Integer.MAX_VALUE : SH.parseIntSafe(end, false, false);
						r[pos++] = s;
						r[pos++] = e;
					}
					return r;
				} else if (string.indexOf('-') != -1) {
					String start = SH.trim(SH.beforeFirst(string, '-'));
					String end = SH.trim(SH.afterFirst(string, '-'));
					int s = "".equals(start) ? 0 : SH.parseIntSafe(start, false, false);
					int e = "".equals(end) ? Integer.MAX_VALUE : SH.parseIntSafe(end, false, false);
					return new int[] { s, e };
				} else {
					int t = SH.parseIntSafe(SH.trim(string), false, false);
					return new int[] { t, t };
				}
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		protected String getHelp() {
			return "Splits string using the supplied delimiter based on the supplied field positions. The string returned is the resulting strings or characters at the field positions joined by the delimiter. Same as AmiScript method strCut().";
		}
	};

	private static final AmiAbstractMemberMethod<String> IS = new AmiAbstractMemberMethod<String>(String.class, "is", Boolean.class) {
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"3forge\";").append("\n");
			example.append("s.is();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.is()"}));
			
			
			example = new StringBuilder();
			example.append("String s = \"  \";").append("\n");
			example.append("s.is();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.is()"}));
			
			return examples;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			return SH.is(AmiUtils.s(targetObject));
		}

		@Override
		protected String getHelp() {
			return "Returns \"true\" if the string contains characters other than whitespace characters, such as tabs, newlines, and spaces; returns \"false\" otherwise. Same as strIs(). Opposite of isnt().";
		}
	};

	private static final AmiAbstractMemberMethod<String> ISNT = new AmiAbstractMemberMethod<String>(String.class, "isnt", Boolean.class) {
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"3forge\";").append("\n");
			example.append("s.isnt();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.isnt()"}));
			
			
			example = new StringBuilder();
			example.append("String s = \"  \";").append("\n");
			example.append("s.isnt();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.isnt()"}));
			
			return examples;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			return SH.isnt(AmiUtils.s(targetObject));
		}

		@Override
		protected String getHelp() {
			return "Returns \"true\" if the string contains only the following whitespace characters: tabs, newlines, and spaces; return \"false\" otherwise. Same as strIsnt(). Opposite of is().";
		}
	};

	private static final AmiAbstractMemberMethod<String> SPLICE = new AmiAbstractMemberMethod<String>(String.class, "splice", String.class, Number.class, Number.class,
			String.class) {

		@Override
		protected String[] buildParamNames() {
			return new String[] { "start", "charsToReplace", "replacement" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "zero indexed start of replacement.", "length of chars to replace.",
					"text to replace specified subsequence with, null is treated as empty string." };
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			Number start = (Number) params[0];
			Number charsToReplace = (Number) params[1];
			String replacementText = (String) params[2];
			return splice(targetObject, start.intValue(), charsToReplace.intValue(), replacementText);
		}

		private String splice(String source, int start, int charsToRemove, String insert) {
			if (source == null)
				return null;
			final int length = source.length();
			if (insert == null)
				insert = "";
			if (start < 0)
				start = 0;
			if (start > length)
				start = length;
			if (charsToRemove < 0)
				charsToRemove = 0;
			int end = charsToRemove + start;
			if (end > length)
				end = length;
			if (charsToRemove == 0 && insert.length() == 0)
				return source;

			StringBuilder sink = new StringBuilder();
			return sink.append(source, 0, start).append(insert).append(source, end, length).toString();//copied from SH.splice(...)
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"3 Forge\";").append("\n");
			example.append("s.splice(0,3,\"3f\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.splice(0,3,\"3f\")"}));
			
			
			example = new StringBuilder();
			example.append("String s = \"3 forge\";").append("\n");
			example.append("s.splice(100,1,\" limit\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.splice(100,1,\" limit\")"}));
			
			
			example = new StringBuilder();
			example.append("String s = \"3 forge\";").append("\n");
			example.append("s.splice(1,1,null);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "s.splice(1,1,null)"}));
			
			return examples;
		}
		@Override
		protected String getHelp() {
			return "Replaces a base string's subsequence of chars with a replacement string. If start or charsToReplace extend beyond string limits, they will be set to string limits. Same as strSplice().";
		}
	};

	private static final AmiAbstractMemberMethod<String> DECODE_TO_BYTE = new AmiAbstractMemberMethod<String>(String.class, "decodeToByte", Byte.class) {

		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			return Byte.decode(targetObject);
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"10\";").append("\n");
			example.append("Object b = s.decodeToByte();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "b.getClassName()"}));
	
			return examples;
		}
		@Override
		protected String getHelp() {
			return "Decodes a string into a byte. Must be a number without any suffix.";
		}
	};

	private static final AmiAbstractMemberMethod<String> DECODE_TO_LONG = new AmiAbstractMemberMethod<String>(String.class, "decodeToLong", Long.class) {

		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			return Long.decode(targetObject);
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"10\";").append("\n");
			example.append("Object l = s.decodeToLong();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l.getClassName()"}));
	
			return examples;
		}
		@Override
		protected String getHelp() {
			return "Decodes a string into a long. Must be a number without any suffix.";
		}
	};

	private static final AmiAbstractMemberMethod<String> DECODE_TO_SHORT = new AmiAbstractMemberMethod<String>(String.class, "decodeToShort", Short.class) {

		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, String targetObject, Object[] params, DerivedCellCalculator caller) {
			return Short.decode(targetObject);
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("String s = \"10\";").append("\n");
			example.append("Object sh = s.decodeToShort();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "sh.getClassName()"}));
	
			return examples;
		}
		@Override
		protected String getHelp() {
			return "Decodes a string into a short. Must be a number without any suffix.";
		}
	};

	@Override
	public String getVarTypeName() {
		return "String";
	}

	@Override
	public String getVarTypeDescription() {
		return "A sequence of characters";
	}

	@Override
	public Class<String> getVarType() {
		return String.class;
	}

	@Override
	public Class<? extends String> getVarDefaultImpl() {
		return String.class;
	}

	public static AmiScriptMemberMethods_String INSTANCE = new AmiScriptMemberMethods_String();
}
