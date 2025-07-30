package com.f1.utils.css;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.base.ToStringable;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.CharReader;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.impl.BasicCharMatcher;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.JavaExpressionParser;
import com.f1.utils.structs.Tuple2;

public class CssParser {

	public static void main(String a[]) throws IOException {
		String name = "/home/rcooke/p4base/dev/java/3forge/website/src/main/resources/WebContent/v2/stylesheets/bootstrap.css";
		File file = new File(name);//"/tmp/custom.css");
		String text = IOH.readText(file);
		List<CssStatement> css = parseCss(text);
		for (CssStatement i : css) {
			Tuple2<Integer, Integer> t = SH.getLinePosition(text, i.getPosition());
			System.out.println("\n\n---- At " + (t.getA() + 1) + ":" + (t.getB() + 1) + "  ----");
			System.out.println(i);
		}

	}

	public static List<CssStatement> parseCss(String css) {
		List<CssStatement> r = new ArrayList<CssStatement>();
		StringCharReader scr = new StringCharReader(css);
		scr.setToStringIncludesLocation(true);
		StringBuilder sink = new StringBuilder();
		try {
			for (;;) {
				JavaExpressionParser.sws(scr);
				if (scr.isEof())
					break;
				r.add(parseStatement(scr, sink));
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ExpressionParserException(css, Math.max(css.length() - 1, 0), "Unexpected end of css");
		} catch (ExpressionParserException e) {
			e.setExpression(css);
			throw e;
		}
		return r;
	}
	private static CssStatement parseStatement(StringCharReader scr, StringBuilder sink) {
		if (scr.peak() == '@') {
			return parseCssAtRule(scr, sink);
		} else {
			return parseRuleSet(scr, sink);
		}
	}

	private static final CssStatement[] EMPTY_STATEMENTS = new CssStatement[0];

	private static CssAtRule parseCssAtRule(StringCharReader scr, StringBuilder sink) {
		int position = scr.getCountRead();
		scr.expect('@');
		scr.readUntilAny(" \t\n\t\r{", false, SH.clear(sink));
		String rule = SH.toStringAndClear(sink);
		if (SH.isnt(rule))
			throw new ExpressionParserException(scr.getCountRead(), "Invalid At-Rule, can't be black");
		JavaExpressionParser.sws(scr);
		scr.readUntilAny(";{", false, SH.clear(sink));//TODO: handle quotes, etc
		String arguments = SH.toStringAndClear(sink);
		if (scr.peak() == '{') {
			CssStatement[] statements = EMPTY_STATEMENTS;
			if ("page".equals(rule)) {
				Map<String, String> declarations = parseDeclarations(scr, sink);
				return new CssAtRule(position, rule, arguments, statements, declarations);
			} else {
				scr.expect('{');
				for (;;) {
					JavaExpressionParser.sws(scr);
					CssStatement statement = parseStatement(scr, sink);
					statements = AH.append(statements, statement);
					JavaExpressionParser.sws(scr);
					if (scr.expectNoThrow('}'))
						return new CssAtRule(position, rule, arguments, statements, null);
				}
			}
		} else {
			scr.expect(';');
			return new CssAtRule(position, rule, arguments, null, null);
		}
	}

	public static CssRuleset parseRuleSet(CharReader scr, StringBuilder tmp) {
		JavaExpressionParser.sws(scr);
		int position = scr.getCountRead();
		CssSelector[] selectors = new CssSelector[] { parseSelector(scr, tmp) };
		while (scr.expectNoThrow(','))
			selectors = AH.append(selectors, parseSelector(scr, tmp));
		Map<String, String> declarations = parseDeclarations(scr, tmp);
		return new CssRuleset(position, selectors, declarations);
	}

	private static Map<String, String> parseDeclarations(CharReader scr, StringBuilder tmp) {
		JavaExpressionParser.sws(scr);
		scr.expect('{');
		LinkedHashMap<String, String> r = new LinkedHashMap<String, String>();
		for (;;) {
			JavaExpressionParser.sws(scr);
			if (scr.expectNoThrow('}'))
				return r;
			scr.readUntilAny(":\n\t\r", false, SH.clear(tmp));
			SH.trimInplace(tmp);
			String key = SH.toStringAndClear(tmp);
			scr.skip(StringCharReader.WHITE_SPACE);
			scr.expect(':');
			scr.skip(StringCharReader.WHITE_SPACE);

			if (SH.isnt(key))
				throw new ExpressionParserException(scr.getCountRead(), "Expecting key");
			inner: for (;;) {
				char c = (char) scr.readUntilAny("\"\';\n\r\t}", false, tmp);
				switch (c) {

					case '\"':
						parseStringDoubleQuote(scr, c, tmp);
						break;
					case '\'':
						parseStringSingleQuote(scr, c, tmp);
						break;
					case '\n':
					case '\r':
					case '\t':
						tmp.append(' ');
						scr.skip(StringCharReader.WHITE_SPACE);
						break;
					case '}': {
						scr.expect('}');
						SH.trimInplace(tmp);
						String value = SH.toStringAndClear(tmp);
						r.put(key, value);
						return r;
					}
					case ';': {
						scr.expect(';');
						SH.trimInplace(tmp);
						String value = SH.toStringAndClear(tmp);
						r.put(key, value);
						break inner;
					}
				}
			}
		}
	}
	private static void parseStringSingleQuote(CharReader scr, char c, StringBuilder tmp) {
		scr.expect('\'');
		tmp.append('\'');
		for (;;) {
			if (scr.readUntilAny("\'\\", false, tmp) == '\\') {
				scr.expect('\\');
				char t = scr.readChar();
				switch (t) {
					case '\n':
						scr.expectNoThrow('\r');
						break;
					case '\r':
						scr.expectNoThrow('\n');
						break;
					default:
						tmp.append('\\');
						tmp.append(t);

				}
			} else {
				scr.expect('\'');
				tmp.append('\'');
				return;
			}
		}

	}
	private static void parseStringDoubleQuote(CharReader scr, char c, StringBuilder tmp) {
		scr.expect('\"');
		tmp.append('\"');
		for (;;) {
			if (scr.readUntilAny("\"\\", false, tmp) == '\\') {
				scr.expect('\\');
				char t = scr.readChar();
				switch (t) {
					case '\n':
						scr.expectNoThrow('\r');
						break;
					case '\r':
						scr.expectNoThrow('\n');
						break;
					default:
						tmp.append('\\');
						tmp.append(t);

				}
			} else {
				scr.expect('\"');
				tmp.append('\"');
				return;
			}
		}

	}

	private static final BasicCharMatcher CHARS = new BasicCharMatcher(".[#: {,+~>\n\t\r()", true);
	private static final BasicCharMatcher CHARS_AND_STAR = new BasicCharMatcher("*.[#: {,+~>\n\t\r()", true);
	private static final CssAttribute[] EMPTY_ATTR = new CssAttribute[0];
	private static final CssPseudoClass[] EMPTY_PSEUDO_CLASSES = new CssPseudoClass[0];
	private static final Set<String> PSEUDO_CLASSES = CH.s(":active", ":any-link", ":autofill", ":checked", ":current", ":default", ":disabled", ":dir", ":defined", ":empty",
			":enabled", ":first", ":first-child", ":first-of-type", ":focus", ":focus-visible", ":focus-within", ":fullscreen", ":future", ":has", ":host", ":host-context",
			":hover", ":in-range", ":indeterminate", ":invalid", ":is", ":lang", ":last-child", ":last-of-type", ":left", ":link", ":local-link", ":modal", ":not", ":nth-child",
			":nth-last-child", ":nth-last-of-type", ":nth-of-type", ":only-child", ":only-of-type", ":open", ":optional", ":out-of-range", ":past", ":paused",
			":picture-in-picture", ":placeholder-shown", ":playing", ":popover-open", ":read-only", ":read-write", ":required", ":right", ":root", ":scope", ":state", ":target",
			":user-invalid", ":valid", ":visited", ":where");

	private static CssSelector parseSelector(CharReader scr, StringBuilder tmp) {
		scr.skip(StringCharReader.WHITE_SPACE);
		int position = scr.getCountRead();
		String element = null, id = null, classes[] = OH.EMPTY_STRING_ARRAY, pseudoElement = null;
		CssPseudoClass[] pseudoClasses = EMPTY_PSEUDO_CLASSES;
		CssAttribute[] attributes = EMPTY_ATTR;
		switch (scr.readUntilAny(CHARS_AND_STAR, true, SH.clear(tmp))) {
			case CharReader.EOF:
				throw new ExpressionParserException(scr.getCountRead(), "Invalid Selector, unexpected EOF");
			case '*':
				if (tmp.length() != 0)
					throw new ExpressionParserException(scr.getCountRead(), "Invalid Selector, unexpected: *");
				break;
			default:
				if (tmp.length() > 0)
					element = SH.toStringAndClear(tmp);
				break;
		}
		for (;;) {
			switch (scr.peak()) {
				case '*':
					scr.expect('*');
					if (element != null)
						throw new ExpressionParserException(scr.getCountRead(), "Invalid Selector, can't have multiple elements");
					element = "*";
					break;
				case '#':
					scr.expect('#');
					if (id != null)
						throw new ExpressionParserException(scr.getCountRead(), "Invalid Selector, can't have multiple ids");
					readId(scr, tmp);
					id = SH.toStringAndClear(tmp);

					if (SH.isnt(id))
						throw new ExpressionParserException(scr.getCountRead(), "Invalid Selector, id is blank");
					break;
				case '.':
					scr.expect('.');
					readId(scr, tmp);
					String clazz = SH.toStringAndClear(tmp);
					if (SH.isnt(clazz))
						throw new ExpressionParserException(scr.getCountRead(), "Invalid Selector, class is blank");
					classes = AH.append(classes, clazz);
					break;
				case ':':
					scr.expect(':');
					if (scr.expectNoThrow(':')) {
						if (pseudoElement != null)
							throw new ExpressionParserException(scr.getCountRead(), "Invalid Selector, can't have multiple pseudo-elements");
						readId(scr, tmp);
						pseudoElement = SH.toStringAndClear(tmp);
						if (SH.isnt(pseudoElement))
							throw new ExpressionParserException(scr.getCountRead(), "Invalid Selector, pseudo-element is blank");
					} else {

						readId(scr, tmp);
						String pseudoClass = SH.toStringAndClear(tmp);
						if (SH.isnt(pseudoClass))
							throw new ExpressionParserException(scr.getCountRead(), "Invalid Selector, pseudo-classes is blank");
						else if (!PSEUDO_CLASSES.contains(":" + pseudoClass))
							throw new ExpressionParserException(scr.getCountRead(), "Invalid Selector, unknown pseudo-class");
						scr.mark();
						scr.skip(StringCharReader.WHITE_SPACE);
						CssSelector pseudoClassSelector;
						if (scr.expectNoThrow('(')) {
							pseudoClassSelector = parseSelector(scr, tmp);
							scr.skip(StringCharReader.WHITE_SPACE);
							scr.expect(')');
						} else {
							scr.returnToMark();
							pseudoClassSelector = null;
						}
						pseudoClasses = AH.append(pseudoClasses, new CssPseudoClass(pseudoClass, pseudoClassSelector));
					}
					break;
				case '[':
					attributes = AH.append(attributes, parseAttribute(scr, tmp));
					break;
				case ' ':
				case '\n':
				case '\r':
					scr.skip(StringCharReader.WHITE_SPACE);
					switch (scr.peak()) {
						case '+':
						case '~':
						case '>':
						case ',':
						case '{':
						case ')':
							break;
						default:
							return new CssSelector(position, element, classes, attributes, id, pseudoClasses, pseudoElement, " ", parseSelector(scr, tmp));
					}
					break;
				case '+':
				case '~':
				case '>': {
					String combinationOperation = SH.toString(scr.readChar());
					return new CssSelector(position, element, classes, attributes, id, pseudoClasses, pseudoElement, combinationOperation, parseSelector(scr, tmp));
				}
				case ',':
				case '{':
				case ')':
					return new CssSelector(position, element, classes, attributes, id, pseudoClasses, pseudoElement, null, null);
				default:
					throw new ExpressionParserException(scr.getCountRead(), "Unexpected token");
			}
		}
	}

	private static void readId(CharReader scr, StringBuilder tmp) {
		scr.readUntilAny(CHARS, true, SH.clear(tmp));
		for (int i = 0; i < tmp.length(); i++) {
			char c = tmp.charAt(i);
			if (OH.isntBetween(c, '0', '9') && OH.isntBetween(c, 'a', 'z') && OH.isntBetween(c, 'A', 'Z') && c != '_' && c != '-')
				throw new ExpressionParserException(scr.getCountRead() - tmp.length() + i, "Invalid Character in Selector: " + c);
		}
	}

	public static interface CssStatement extends ToStringable {

		public StringBuilder toString(int indent, StringBuilder sink);
		public int getPosition();
		public boolean referencesVar(String key);

	}

	public static class CssAtRule implements CssStatement {
		final private CssStatement statements[];
		final private String rule;
		final private String arguments;
		final private Map<String, String> declarations;
		final private int position;

		public CssAtRule(int position, String rule, String arguments, CssStatement[] statements, Map<String, String> declarations) {
			this.position = position;
			this.rule = rule;
			this.arguments = arguments;
			this.statements = statements;
			this.declarations = declarations;
		}

		@Override
		public String toString() {
			return toString(0, new StringBuilder()).toString();
		}

		@Override
		public StringBuilder toString(StringBuilder sink) {
			return toString(0, sink);
		}

		@Override
		public StringBuilder toString(int indent, StringBuilder sink) {
			return toString(indent, sink, "");
		}
		public StringBuilder toString(int indent, StringBuilder sink, String argumentPrefix) {
			SH.repeat(' ', indent * 2, sink);
			sink.append('@').append(rule);
			if (arguments != null) {
				if (SH.is(argumentPrefix))
					sink.append(' ').append(argumentPrefix).append('_').append(arguments);
				else
					sink.append(' ').append(arguments);
			}
			if (AH.isEmpty(statements) && CH.isEmpty(declarations))
				sink.append(";\n");
			else {
				sink.append("{\n");
				for (CssStatement i : statements)
					i.toString(indent + 1, sink);
				if (CH.isntEmpty(declarations))
					for (Entry<String, String> i : declarations.entrySet()) {
						SH.repeat(' ', (1 + indent) * 2, sink);
						sink.append(i.getKey()).append(':').append(i.getValue()).append(";\n");
					}
				sink.append("}\n");
			}
			return sink;
		}

		@Override
		public int getPosition() {
			return this.position;
		}

		public CssStatement[] getStatements() {
			return statements;
		}

		public String getRule() {
			return rule;
		}

		public String getArguments() {
			return arguments;
		}

		public Map<String, String> getDeclarations() {
			return declarations;
		}

		@Override
		public boolean referencesVar(String key) {
			if (declarations != null)
				for (String i : declarations.values())
					if (i.contains(key))
						return true;
			return false;
		}

	}

	public static class CssPseudoClass implements ToStringable {
		private final String element;
		private final CssSelector argument;

		public CssPseudoClass(String element, CssSelector argument) {
			super();
			this.element = element;
			this.argument = argument;
		}

		@Override
		public StringBuilder toString(StringBuilder sink) {
			sink.append(':').append(element);
			if (argument != null) {
				sink.append('(');
				argument.toString(sink);
				sink.append(')');
			}
			return sink;
		}
		@Override
		public String toString() {
			return toString(new StringBuilder()).toString();
		}

		public String getElement() {
			return element;
		}

		public CssSelector getArgument() {
			return argument;
		}

	}

	public static CssAttribute parseAttribute(CharReader scr, StringBuilder tmp) {
		scr.expect('[');
		scr.skip(StringCharReader.WHITE_SPACE);
		scr.readUntilAny("]=~^$|* \t\r\n", true, SH.clear(tmp));
		String attr = SH.toStringAndClear(tmp);
		scr.skip(StringCharReader.WHITE_SPACE);
		String operator;
		char operatorC = scr.readChar();
		switch (operatorC) {
			case ']':
				return new CssAttribute(attr, null, null);
			case '=':
				operator = "=";
				break;
			case '~':
				scr.expect('=');
				operator = "~=";
				break;
			case '^':
				scr.expect('=');
				operator = "^=";
				break;
			case '$':
				scr.expect('=');
				operator = "$=";
				break;
			case '|':
				scr.expect('=');
				operator = "|=";
				break;
			case '*':
				scr.expect('=');
				operator = "*=";
				break;
			default:
				throw new ExpressionParserException(scr.getCountRead(), "Invalid attribute operator");
		}
		scr.skip(StringCharReader.WHITE_SPACE);

		char c = scr.peak();
		String value;
		if (c == '\'' || c == '"') {
			scr.expect(c);
			scr.readUntil(c, '\\', SH.clear(tmp));
			scr.expect(c);
			value = SH.toStringAndClear(tmp);
		} else {
			scr.readUntil(']', '\\', SH.clear(tmp));
			SH.trimInplace(tmp);
			value = SH.toStringAndClear(tmp);
		}
		scr.skip(StringCharReader.WHITE_SPACE);
		scr.expect(']');
		return new CssAttribute(attr, operator, value);
	}

	public static class CssRuleset implements CssStatement {

		final private CssSelector selectors[];
		final private Map<String, String> declaration;
		final private int position;

		public CssRuleset(int position, CssSelector selectors[], Map<String, String> declarations) {
			this.position = position;
			this.selectors = selectors;
			this.declaration = declarations;
		}

		@Override
		public String toString() {
			return toString(0, new StringBuilder()).toString();
		}

		@Override
		public StringBuilder toString(int indent, StringBuilder sink) {
			SH.repeat(' ', indent * 2, sink);
			SH.join(", ", selectors, sink);
			toDeclarationString(indent, sink);
			return sink;
		}

		public void toDeclarationString(int indent, StringBuilder sink) {
			sink.append(" {\n");
			for (Entry<String, String> e : this.declaration.entrySet()) {
				SH.repeat(' ', indent * 2, sink);
				sink.append("  ").append(e.getKey()).append(": ");
				sink.append(e.getValue());
				sink.append(";\n");
			}
			SH.repeat(' ', indent * 2, sink);
			sink.append("}\n");
		}

		@Override
		public StringBuilder toString(StringBuilder sink) {
			return toString(0, sink);
		}
		@Override
		public int getPosition() {
			return this.position;
		}

		public CssSelector[] getSelectors() {
			return selectors;
		}

		public Map<String, String> getDeclaration() {
			return declaration;
		}

		@Override
		public boolean referencesVar(String key) {
			for (String i : declaration.values())
				if (i.contains(key))
					return true;
			return false;
		}

	}

	public static class CssSelector implements ToStringable {
		final private String element;
		final private String classes[];
		final private CssAttribute attributes[];
		final private String id;
		final private String pseudoElement;
		final private String combinatorOperation;
		final private CssSelector combinatorSelector;
		final private CssPseudoClass pseudoClasses[];
		final private int position;

		public CssSelector(int position, String element, String[] classes, CssAttribute[] attributes, String id, CssPseudoClass[] pseudoClasses, String pseudoElement,
				String combinationOperation, CssSelector combinationSelector) {
			this.position = position;
			this.element = element;
			this.classes = classes;
			this.attributes = attributes;
			this.id = id;
			this.pseudoClasses = pseudoClasses;
			this.pseudoElement = pseudoElement;
			this.combinatorOperation = combinationOperation;
			this.combinatorSelector = combinationSelector;
		}

		@Override
		public String toString() {
			return toString(new StringBuilder()).toString();
		}

		@Override
		public StringBuilder toString(StringBuilder sink) {
			if (element != null)
				sink.append(element);
			if (AH.isntEmpty(classes))
				for (String clazz : classes)
					sink.append('.').append(clazz);
			if (id != null)
				sink.append('#').append(id);
			for (CssAttribute attribute : attributes)
				attribute.toString(sink);
			for (CssPseudoClass pc : pseudoClasses)
				pc.toString(sink);
			if (pseudoElement != null)
				sink.append("::").append(pseudoElement);
			if (combinatorOperation != null)
				sink.append(combinatorOperation);
			if (combinatorSelector != null)
				combinatorSelector.toString(sink);
			return sink;
		}

		public String getElement() {
			return element;
		}

		public String[] getClasses() {
			return classes;
		}

		public CssAttribute[] getAttributes() {
			return attributes;
		}

		public String getId() {
			return id;
		}

		public String getPseudoElement() {
			return pseudoElement;
		}

		public String getCombinatorOperation() {
			return combinatorOperation;
		}

		public CssSelector getCombinatorSelector() {
			return combinatorSelector;
		}

		public CssPseudoClass[] getPseudoClasses() {
			return pseudoClasses;
		}
		public int getPosition() {
			return this.position;
		}

	}

	public static class CssAttribute implements ToStringable {
		final private String attr;
		final private String operator;
		final private String value;

		public CssAttribute(String attr, String operator, String value) {
			this.attr = attr;
			this.operator = operator;
			this.value = value;
		}

		@Override
		public String toString() {
			return toString(new StringBuilder()).toString();
		}

		@Override
		public StringBuilder toString(StringBuilder sink) {
			sink.append('[');
			sink.append(attr);
			if (operator != null)
				sink.append(operator);
			if (value != null)
				SH.quoteToJavaConst('"', value, sink);
			return sink.append(']');
		}

		public String getAttribute() {
			return attr;
		}

		public String getOperator() {
			return operator;
		}

		public String getValue() {
			return value;
		}

	}

}
