package com.f1.utils.grpc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.grpc.ProtobufUtils.ProtobufObjectTemplate;

public class ProtobufParser {

	//METHOD methodName VAR1 VAR2 CONSTRUCT constructName VAR3 VAR4 CONSTRUCT_END METHOD_END
	public static final byte INSTRUCTION_CALL_METHOD = 0x1;
	public static final byte INSTRUCTION_CALL_METHOD_END = 0x2;
	public static final byte INSTRUCTION_CONSTRUCT_OBJECT = 0x3;
	public static final byte INSTRUCTION_CONSTRUCT_OBJECT_END = 0x4;
	public static final byte INSTRUCTION_DESCRIBE_OBJECT = 0x5;

	private final String classPrefix;

	private ArrayList<Object> compiledInstructions = new ArrayList<Object>();

	// new ( new a(), new b("()())"));
	private int getMatchingClosingBracket(String query, int openingBracketsIndex) {
		int unmatchedCount = 0;
		boolean inQuotes = false;
		for (int i = openingBracketsIndex + 1; i < query.length(); ++i) {
			if (inQuotes) {
				if (query.charAt(i) == '\"')
					inQuotes = false;
			} else {
				final char curr = query.charAt(i);
				if (curr == '\"')
					inQuotes = true;
				else if (curr == ')') {
					if (unmatchedCount == 0)
						return i;
					else
						--unmatchedCount;
				} else if (curr == '(')
					++unmatchedCount;
			}
		}
		return -1;
	}

	private void parse(String query, final List<Object> instructions) {
		while (SH.is(query)) {
			//Deduce next instruction
			if (query.startsWith("new ")) { //Constructing a class
				int openingBracketsIndex = SH.indexOf(query, '(', 4);
				if (openingBracketsIndex == -1)
					throw new RuntimeException("Expecting ( after new statement");
				int closingBracketsIndex = getMatchingClosingBracket(query, openingBracketsIndex);
				if (closingBracketsIndex == -1)
					throw new RuntimeException("Expecting ) after new statement");
				String className = SH.trim(SH.substring(query, 4, openingBracketsIndex));
				if (SH.isnt(className))
					throw new RuntimeException("Failed to get class name after new statement");
				if (!className.contains(".") && SH.is(classPrefix) && !SH.equals(SH.toUpperCase(className), "MAP") && !SH.equals(SH.toUpperCase(className), "LIST"))
					className = classPrefix + "." + className;
				compiledInstructions.add(INSTRUCTION_CONSTRUCT_OBJECT);
				compiledInstructions.add(className);
				String innerQuery = SH.trim(SH.substring(query, openingBracketsIndex + 1, closingBracketsIndex));
				parse(innerQuery, instructions);
				compiledInstructions.add(INSTRUCTION_CONSTRUCT_OBJECT_END);
				query = SH.trim(SH.substring(query, closingBracketsIndex + 1, query.length()));
				if (SH.is(query) && query.charAt(0) == ',')
					query = SH.trim(SH.substring(query, 1, query.length()));
			} else { //Parsing a variable
				int commaIndex = SH.indexOf(query, ',', 0);
				String var = "";
				if (commaIndex == -1) {
					var = SH.trim(query);
					query = "";
				} else {
					var = SH.trim(SH.substring(query, 0, commaIndex));
					query = SH.trim(SH.substring(query, commaIndex + 1, query.length()));
					if (SH.isnt(query))
						throw new RuntimeException("Missing argument");
				}
				//Get underlying variable type
				if (var.charAt(0) == '\"') { //String param
					int endQuoteIndex = SH.indexOf(var, '\"', 1);
					if (endQuoteIndex == -1)
						throw new RuntimeException("Failed to find closing double quotes: " + var);
					if (endQuoteIndex != var.length() - 1)
						throw new RuntimeException("Unexpected values after ending quotes: " + var);
					String val = SH.substring(var, 1, endQuoteIndex);
					compiledInstructions.add(val);

				} else if (SH.toUpperCase(var).equals("TRUE"))
					instructions.add(true);
				else if (SH.toUpperCase(var).equals("FALSE"))
					instructions.add(false);
				else if (var.contains("L"))
					instructions.add(SH.parseLong(var));
				else if (var.contains(".")) {
					if (var.contains("f"))
						instructions.add(SH.parseFloat(var));
					else
						instructions.add(SH.parseDouble(var));
				} else
					instructions.add(SH.parseInt(var));
			}
		}
	}

	public int getInstructionSize() {
		return this.compiledInstructions.size();
	}

	public Object pop() {
		if (this.compiledInstructions.isEmpty())
			return null;
		return this.compiledInstructions.remove(0);
	}

	public ProtobufParser(String query, String classPrefix) {
		this.classPrefix = classPrefix;
		query = SH.trim(query);
		query = SH.replaceAll(query, '\n', "");

		if (SH.toLowerCase(query).startsWith("describe")) {
			this.compiledInstructions.add(INSTRUCTION_DESCRIBE_OBJECT);
			String objectName = SH.trim(SH.substring(query, 9, query.length()));
			this.compiledInstructions.add(objectName);
			return;
		}

		int startBracketIndex = SH.indexOfFirst(query, 0, '(');
		if (startBracketIndex == -1)
			throw new RuntimeException("Failed to parse method starting bracket: " + query);
		if (query.charAt(query.length() - 1) != ')')
			throw new RuntimeException("Failed to parse method ending bracket: " + query);

		String methodName = SH.trim(SH.substring(query, 0, startBracketIndex));
		if (SH.isnt(methodName))
			throw new RuntimeException("Failed to get method name");
		compiledInstructions.add(INSTRUCTION_CALL_METHOD);
		compiledInstructions.add(methodName);
		query = SH.trim(SH.substring(query, startBracketIndex + 1, query.length() - 1));

		parse(query, compiledInstructions);
		compiledInstructions.add(INSTRUCTION_CALL_METHOD_END);
	}

	public static Object compileObject(final ProtobufParser parser, final Map<String, ProtobufObjectTemplate> templates) {
		ArrayList<Object> vars = new ArrayList<Object>();
		String className = (String) parser.pop();
		if (SH.isnt(className))
			throw new RuntimeException("Could not get class name for object compilation");

		for (Object o = parser.pop(); o != null; o = parser.pop()) {
			if (o instanceof Byte) {
				byte b = (byte) o;
				if (b == ProtobufParser.INSTRUCTION_CONSTRUCT_OBJECT_END)
					break;
				else if (b == ProtobufParser.INSTRUCTION_CONSTRUCT_OBJECT)
					vars.add(compileObject(parser, templates));
				else
					throw new UnsupportedOperationException("Could not compile object: " + className + ", last byte: " + b);
			} else {
				vars.add(o);
			}
		}

		String upper = SH.toUpperCase(className);
		if (upper.equals("LIST")) {
			return CH.l(vars);
		} else if (upper.equals("MAP")) {
			return CH.m(vars);
		} else {
			ProtobufObjectTemplate template = ProtobufUtils.getOrSetTemplate(className, templates);
			return template.construct(vars);
		}
	}
}
