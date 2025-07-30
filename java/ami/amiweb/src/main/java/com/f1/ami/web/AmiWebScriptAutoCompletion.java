package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.amiscript.AmiWebAmiScriptDerivedCellParser.AmiWebDeclaredMethodFactory;
import com.f1.base.IterableAndSize;
import com.f1.stringmaker.StringTranslator;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.impl.WebMenuListener;
import com.f1.utils.CH;
import com.f1.utils.CharReader;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.impl.BasicCharMatcher;
import com.f1.utils.impl.CaseInsensitiveHasher;
import com.f1.utils.impl.CharMatcher;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.string.JavaExpressionParser;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.table.derived.DeclaredMethodFactory;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorMethod;
import com.f1.utils.structs.table.derived.DerivedCellMemberMethod;
import com.f1.utils.structs.table.derived.MemberMethodDerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodDerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodFactory;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.BasicCalcTypes;

public class AmiWebScriptAutoCompletion implements WebMenuListener {

	public static final CharMatcher SPECIAL_CHARS = new BasicCharMatcher("+\\-*/^()[],;<>= !&|~%:.}{\n\r?", false);
	private static final CharMatcher WS = StringCharReader.WHITE_SPACE;

	private static final BasicCharMatcher CMD_BREAKS = new BasicCharMatcher(";=+\\-/*&^%$#@!<>\\\\:?", false);
	public static final Set<String> KEYWORDS = CH.s("if(", "else", "else if(", "while(", "do{", "for(", "return ", "new "); // case insensitive
	public static final HasherSet<CharSequence> SQL_TRIGGER_WORDS = CH.s(new HasherSet<CharSequence>(CaseInsensitiveHasher.INSTANCE), (CharSequence) "CREATE", "DROP", "ALTER",
			"INSERT", "DELETE", "UPDATE", "ANALYZE", "PREPARE", "RENAME", "SELECT"); // case insensitive

	private StringTranslator CreateTable = new StringTranslator("\\s*CREATE\\s+TABLE\\s+(\\w+)\\s.*", Pattern.CASE_INSENSITIVE, "$1$");
	private StringTranslator RenameTable = new StringTranslator("\\s*RENAME\\s+TABLE\\s+\\w+\\s+TO\\s+(\\w+)\\b.*", Pattern.CASE_INSENSITIVE, "$1$");
	public static final Set<String> CONSTS = CH.s("null", "true", "false");
	private boolean menuActive = false;
	private static final Map<String, Object> menuOptions = new HashMap<String, Object>();
	private static final Logger log = LH.get();
	private StringBuilder hintHtmlBuffer = new StringBuilder();
	static {
		menuOptions.put("keepYPosition", true);
		menuOptions.put("runFirstItemOnEnter", true);
	}

	private static class MenuOption {
		public static final byte TYPE_HINT = 0;
		public static final byte TYPE_LCV = 1;//ordering matters, lower number wins
		public static final byte TYPE_VAR = 2;
		public static final byte TYPE_TYPE = 3;
		public static final byte TYPE_METHOD = 4;
		public static final byte TYPE_FUNC = 5;
		public static final byte TYPE_KEYWORD = 6;
		public static final byte TYPE_CONST = 7;
		long id;
		final String value;
		final String displayHtml;
		final int type;
		int replaceStart;
		int replaceEnd;
		String targetType;
		ParamsDefinition paramsDefinition;
		String fullForm;
		public List<MenuOption> children = new ArrayList<MenuOption>();
		public MenuOption parent;

		public MenuOption(MenuOption parent, long id, byte type, String value, String displayHtml, int replaceStart, int replaceEnd, String targetType,
				ParamsDefinition paramsDefinition, String fullForm) {
			this(id, type, value, displayHtml, replaceStart, replaceEnd, fullForm);
			this.parent = parent;
			if (parent != null)
				this.parent.children.add(this);
			this.targetType = targetType;
			this.paramsDefinition = paramsDefinition;

		}
		public MenuOption(long id, byte type, String value, String displayHtml, int replaceStart, int replaceEnd, String fullForm) {
			super();
			this.id = id;
			this.type = type;
			this.value = value;
			this.displayHtml = displayHtml;
			this.replaceStart = replaceStart;
			this.replaceEnd = replaceEnd;
			this.fullForm = fullForm;
		}

		@Override
		public String toString() {
			return "MenuOption [id=" + id + ", value=" + value + ", displayHtml=" + displayHtml + ", type=" + type + ", replaceStart=" + replaceStart + ", replaceEnd=" + replaceEnd
					+ "]";
		}

	}

	private int cursorPosition;
	private com.f1.utils.structs.table.stack.BasicCalcTypes varTypes = new com.f1.utils.structs.table.stack.BasicCalcTypes();
	private Set<String> tableNames = new HashSet<String>();
	private final StringBuilder tmpBuf = new StringBuilder();
	private final StringCharReader reader = new StringCharReader();
	private int cmdStart;
	private BasicCalcTypes globalVars = new com.f1.utils.structs.table.stack.BasicCalcTypes();
	private String cmd;
	private boolean cmdEndsWithWhitespace;
	private MethodFactoryManager factory;
	private LongKeyMap<MenuOption> options = new LongKeyMap<MenuOption>();
	private Set<String> types = new HashSet<String>();
	private AmiWebFormPortletAmiScriptField field;
	private AmiWebScriptManagerForLayout scriptManager;
	private int funcStart = -1;
	private boolean funcEnd = false;

	private MultiCompleter sqlCompleter;
	private String layoutAlias;
	private MethodDerivedCellCalculator activeMethod;
	private Class<?> activeType;
	final private AmiWebService service;

	public AmiWebScriptAutoCompletion(AmiWebService service, AmiWebScriptManagerForLayout sm) {
		this.service = service;
		this.layoutAlias = sm.getLayoutAlias();
		this.sqlCompleter = new MultiCompleter();
		MultiCompleter alterTypes = new MultiCompleter();
		alterTypes.addCompleter(new SqlCompleter("ADD ? ?", null));
		alterTypes.addCompleter(new SqlCompleter("DROP ?", null));
		alterTypes.addCompleter(new SqlCompleter("MODIFY ? AS ? ?", null));
		alterTypes.addCompleter(new SqlCompleter("RENAME ? TO ?", null));
		MultiCompleter createTypes = new MultiCompleter();
		createTypes.addCompleter(new SqlCompleter("SELECT * FROM _", null));
		createTypes.addCompleter(new SqlCompleter("PREPARE * FROM _", null));
		createTypes.addCompleter(new SqlCompleter("ANALYZE * FROM _", null));
		MultiCompleter updateTypes = new MultiCompleter();
		updateTypes.addCompleter(new SqlCompleter("SET", null));

		this.sqlCompleter.addCompleter(new SqlCompleter("DROP TABLE _", null));
		this.sqlCompleter.addCompleter(new SqlCompleter("RENAME TABLE _ TO ?", null));
		this.sqlCompleter.addCompleter(new SqlCompleter("UPDATE _ ", updateTypes));
		this.sqlCompleter.addCompleter(new SqlCompleter("ALTER TABLE _", alterTypes));
		this.sqlCompleter.addCompleter(new SqlCompleter("CREATE TABLE ? AS", createTypes));
		for (Completer c : createTypes.completers)
			this.sqlCompleter.addCompleter(c);
		createTypes.addCompleter(new SqlCompleter("EXECUTE ", null));
		createTypes.addCompleter(new SqlCompleter("USE EXECUTE ", null));

		this.factory = sm.getMethodFactory();
		this.scriptManager = sm;
		//		for (MappingEntry<String, Class<?>> type : this.scriptManager.getGlobalVarTypes().entries())
		//			addVariable(type.getKey(), type.getValue());
		types.add("int");
		types.add("long");
		types.add("double");
		types.add("float");
		types.add("boolean");
		types.add("String");
		List<DerivedCellMemberMethod<Object>> sink = new ArrayList<DerivedCellMemberMethod<Object>>();
		this.factory.getMemberMethods(null, null, sink);
		for (DerivedCellMemberMethod<Object> i : sink) {
			String name = this.factory.forType(i.getTargetType());
			if (name != null)
				this.types.add(name);
		}
	}
	public void addAllVariables(com.f1.base.CalcTypes mapping) {
		this.globalVars.putAll(mapping);
		//		for (String name : mapping.keySet()) {
		//			String type2 = this.factory.forType(mapping.get(name));
		//			if (type2 != null)
		//				this.globalVars.put(name, type2);
		//		}
	}
	//	public void addVariable(String name, String type) {
	//		this.globalVars.put(name, type);
	//	}
	public void addVariable(String name, Class type) {
		//		String type2 = this.factory.forType(type);
		//		if (type2 != null)
		this.globalVars.putType(name, type);
	}

	public void clearGlobalVars() {
		this.globalVars.clear();
	}
	public AmiWebScriptAutoCompletion reset(AmiWebFormPortletAmiScriptField field, int cp) {
		this.activeMethod = null;
		this.activeType = null;
		this.field = field;
		this.cursorPosition = cp;
		String origValue = (String) field.getValue();
		if (origValue.length() < cp)
			return this;
		reader.reset(origValue);
		reader.skipChars(cp);
		int fullCommandEnd = -1;
		int depth = 0;
		while (!reader.isEof()) {
			int c = reader.readUntilAny(new int[] { ';', ';', '(', ')', CharReader.EOF, '"' }, null);
			if (c == '(') {
				reader.expect('(');
				depth++;
			} else if (c == ')') {
				if (depth == 0) {
					fullCommandEnd = reader.getCountRead();
					break;
				}
				depth--;
				reader.expect(')');
			} else if (c == ';') {
				fullCommandEnd = reader.getCountRead();
				break;
			} else if (c == '"') {
				reader.readUntilSkipEscaped('"', '\\');
				reader.expectNoThrow('"');
			}
		}

		String value = origValue.substring(0, cp);
		this.varTypes.clear();
		this.tableNames.clear();
		this.varTypes.putAll(this.globalVars);
		Map<String, String> parseParts = parseParts(value);
		for (Entry<String, String> e : parseParts.entrySet()) {
			Class<?> type = factory.forNameNoThrow(e.getValue());
			if (type != null)
				this.varTypes.putType(e.getKey(), type);
		}
		this.options.clear();
		this.cmd = value.substring(this.cmdStart);
		if (fullCommandEnd != -1) {
			String fullCommand = origValue.substring(this.cmdStart, fullCommandEnd).trim();
			if (!fullCommand.endsWith(";"))
				fullCommand = fullCommand + ';';
			String word = SH.beforeFirst(fullCommand, ' ', fullCommand);
			this.activeType = this.factory.forNameNoThrow(word);
			try {
				DerivedCellCalculator calc = scriptManager.toCalcNotOptimized(fullCommand, this.varTypes, null, null);
				this.activeMethod = findMethodAtPosition(calc, cp - this.cmdStart);
			} catch (Exception e) {
				if (LH.isFine(log))
					LH.fine(log, this.service.getUserName() + ": Problem with Text ", e);
			}
		}
		cmdEndsWithWhitespace = value.length() > 0 && SH.isnt(value.charAt(value.length() - 1));
		if (this.funcStart != -1 && this.cursorPosition == this.cmdStart) {
			String func = value.substring(funcStart, cmdStart - 1).trim();
			if (func.indexOf('.') != -1) {
				//Hint on Member Function contents
				String prefix = SH.beforeLast(func, '.', null);
				String name = SH.afterLast(func, '.', null);
				for (DerivedCellMemberMethod<Object> i : getMethods(getType(prefix), name)) {
					SH.clear(tmpBuf).append("<I>Jump to ").append(factory.forType(i.getTargetType())).append("::").append(i.getMethodName()).append("(");
					for (int j = 0; j < i.getParamNames().length; j++) {
						String p = i.getParamNames()[j];
						if (j > 0)
							tmpBuf.append(',');
						tmpBuf.append(p);
						if (j == i.getParamNames().length - 1 && i.getVarArgType() != null)
							tmpBuf.append(" ... ");
					}
					tmpBuf.append("</i>)");
					tmpBuf.append("&nbsp;&nbsp;<i style='color:#888888'>").append(this.factory.forType(i.getReturnType())).append("</i>");

					MenuOption option = new MenuOption(null, this.options.size(), MenuOption.TYPE_HINT, "", tmpBuf.toString(), cp, cp, prefix, i.getParamsDefinition(),
							i.getMethodName());
					this.options.put(option.id, option);
				}
			} else {
				//Hint on static Function contents
				List<MethodFactory> sink = new ArrayList<MethodFactory>();
				scriptManager.getMethodFactory().getAllMethodFactories(sink);
				for (MethodFactory i : sink) {
					if (i instanceof AmiWebDeclaredMethodFactory)
						continue;//skip custom methods
					String type = i.getDefinition().getMethodName();
					if (type.equalsIgnoreCase(func)) {
						MenuOption option = new MenuOption(null, this.options.size(), MenuOption.TYPE_HINT, "", "<i>Jump to " + toString(i), cp, cp, null, i.getDefinition(), type);
						this.options.put(option.id, option);
					}
				}
			}
		}
		if (cmd.indexOf('.') != -1) {
			String prefix = SH.beforeLast(cmd, '.', null);
			Class type = null;
			if (prefix.indexOf('(') != -1) {
				try {
					type = evaluateType(prefix);
				} catch (Exception e) {
					if (LH.isFine(log))
						LH.fine(log, "For ", this.field.getForm().getManager().getUserName(), ": ", e);
				}
			} else
				type = getType(prefix);
			String name = SH.trimStart(' ', SH.afterLast(cmd, '.', null));
			name = SH.trimWhitespace(name);
			if (!cmdEndsWithWhitespace || name.isEmpty()) {
				for (DerivedCellMemberMethod<Object> i : getMethods(type, name)) {
					if (i.getMethodName() == null)//constructor
						continue;
					String remaining = SH.stripPrefixIgnoreCase(i.getMethodName(), name, false);
					String methodName = i.getMethodName();
					if (i.getParamTypes().length > 0 || i.getVarArgType() != null) {
						remaining += "(";
						methodName += "(";
					} else {
						remaining += "()";
						methodName += "()";
					}

					SH.clear(tmpBuf).append(i.getMethodName());
					paramsToString(i, tmpBuf);
					tmpBuf.append("&nbsp;&nbsp;<i style='color:#888888'>").append(this.factory.forType(i.getReturnType())).append("</i>");

					MenuOption option = new MenuOption(this.options.size(), MenuOption.TYPE_METHOD, remaining, tmpBuf.toString(), cp, cp, methodName);
					this.options.put(option.id, option);
					if (i instanceof AmiAbstractMemberMethod) {
						Map<String, String> autocomplete = ((AmiAbstractMemberMethod) i).getAutocompleteOptions(service);
						if (CH.isntEmpty(autocomplete)) {
							for (Entry<String, String> e : autocomplete.entrySet()) {
								SH.clear(tmpBuf).append(i.getMethodName()).append("(").append(e.getKey());
								if (!e.getKey().endsWith(")"))
									tmpBuf.append(" ...)");
								tmpBuf.append("&nbsp;&nbsp;<i style='color:#888'>").append(e.getValue()).append("</i>");
								//								MenuOption option2 = new MenuOption(option, this.options.size(), MenuOption.TYPE_METHOD, remaining + e.getKey(), tmpBuf.toString(), cp, cp, prefix,
								//										i.getMethodName() + "(" + e.getKey());
								//								this.options.put(option2.id, option2);
							}

						}
					}
				}
				for (String s : this.globalVars.getVarKeys()) {
					if (SH.startsWithIgnoreCase(s, cmd)) {
						SH.clear(tmpBuf).append(s);
						String remaining = SH.stripPrefixIgnoreCase(s, cmd, false);
						MenuOption option = new MenuOption(this.options.size(), MenuOption.TYPE_VAR, remaining, s, cp, cp, s);
						this.options.put(option.id, option);
					}
				}
			}

		} else if ("return".equalsIgnoreCase(cmd)) {
			for (String i : this.varTypes.getVarKeys()) {
				MenuOption option = new MenuOption(this.options.size(), this.globalVars.getType(i) != null ? MenuOption.TYPE_VAR : MenuOption.TYPE_LCV, i, i, cp, cp, i);
				this.options.put(option.id, option);
			}
		} else {
			if (SH.startsWithIgnoreCase(cmd, "new")) {
				List<DerivedCellMemberMethod<Object>> sink = new ArrayList<DerivedCellMemberMethod<Object>>();
				this.factory.getMemberMethods(null, null, sink);
				for (DerivedCellMemberMethod<Object> i : sink) {
					if (i.getMethodName() != null)
						continue;
					String forType = this.factory.forType(i.getTargetType());
					String type = "new " + forType;
					if (SH.startsWithIgnoreCase(type, cmd)) {
						String remaining = SH.stripPrefixIgnoreCase(type, cmd, false);
						SH.clear(tmpBuf).append(type);
						paramsToString(i, tmpBuf);
						MenuOption option = new MenuOption(this.options.size(), MenuOption.TYPE_TYPE, remaining + "(", tmpBuf.toString(), cp, cp, type + "(");
						this.options.put(option.id, option);
					}
				}
			} else
				for (String i : this.varTypes.getVarKeys()) {
					if (SH.startsWithIgnoreCase(i, cmd)) {
						String remaining = SH.stripPrefixIgnoreCase(i, cmd, false);
						MenuOption option = new MenuOption(this.options.size(), this.globalVars.getType(i) != null ? MenuOption.TYPE_VAR : MenuOption.TYPE_LCV, remaining, i, cp,
								cp, i);
						this.options.put(option.id, option);
					}
				}
			if (funcStart == -1) {
				for (String type : types) {
					if (SH.startsWithIgnoreCase(type, cmd)) {
						String remaining = SH.stripPrefixIgnoreCase(type, cmd, false);
						MenuOption option = new MenuOption(this.options.size(), MenuOption.TYPE_TYPE, remaining + " ", type, cp, cp, type + " ");
						this.options.put(option.id, option);
					}
				}
				for (String type : KEYWORDS) {
					if (SH.startsWithIgnoreCase(type, cmd, 0)) {
						String remaining = SH.stripPrefixIgnoreCase(type, cmd, false);
						MenuOption option = new MenuOption(this.options.size(), MenuOption.TYPE_KEYWORD, remaining, type, cp, cp, type);
						this.options.put(option.id, option);
					}
				}
				List<MenuOption> sink = new ArrayList<AmiWebScriptAutoCompletion.MenuOption>();
				this.sqlCompleter.autoComplete(new StringCharReader(cmd).setToStringIncludesLocation(true), tableNames, sink);
				for (MenuOption i : sink) {
					i.replaceEnd += cmdStart;
					i.replaceStart += cmdStart;
					i.id = this.options.size();
					this.options.put(i.id, i);
				}
			}
			for (String type : CONSTS) {
				if (SH.startsWithIgnoreCase(type, cmd)) {
					String remaining = SH.stripPrefixIgnoreCase(type, cmd, false);
					MenuOption option = new MenuOption(this.options.size(), MenuOption.TYPE_CONST, remaining, type, cp, cp, type);
					this.options.put(option.id, option);
				}
			}
			List<MethodFactory> sink = new ArrayList<MethodFactory>();
			this.factory.getAllMethodFactories(sink);
			for (MethodFactory i : sink) {
				String type = i.getDefinition().getMethodName();
				if (type != null && SH.startsWithIgnoreCase(type, cmd)) {
					String remaining = SH.stripPrefixIgnoreCase(type, cmd, false);
					MenuOption option = new MenuOption(this.options.size(), MenuOption.TYPE_FUNC, remaining + "(", toString(i), cp, cp, type + "(");
					this.options.put(option.id, option);
				}
			}
		}
		WebMenu menu = generateMenu();
		if (menu.getChildren().isEmpty()) {
			this.service.getPortletManager().closeContextMenu();
			this.setMenuActive(false);
		} else {
			this.service.getPortletManager().showContextMenu(menu, this, field.getCusorPageX(), field.getCusorPageY() + 15, menuOptions);
			this.setMenuActive(true);
		}
		return this;
	}

	private MethodDerivedCellCalculator findMethodAtPosition(DerivedCellCalculator calc, int pos) {
		if (calc instanceof MethodDerivedCellCalculator) {
			MethodDerivedCellCalculator mc = (MethodDerivedCellCalculator) calc;
			if (calc.getPosition() <= pos && pos <= calc.getPosition() + mc.getMethodName().length())
				return mc;
		}

		for (int i = 0, l = calc.getInnerCalcsCount(); i < l; i++) {
			MethodDerivedCellCalculator r = findMethodAtPosition(calc.getInnerCalcAt(i), pos);
			if (r != null)
				return r;
		}
		return null;
	}
	//	private void setTargetMethod(String objectType, String methodName, String[] types) {
	//		System.out.println(objectType + "." + methodName + "(" + SH.join(',', types) + ")");
	//	}
	//	private String determineType(Node node) {
	//		if (node instanceof VariableNode) {
	//			VariableNode vnNode = (VariableNode) node;
	//			return varTypes.get(vnNode.varname);
	//		}
	//		if (node instanceof ConstNode) {
	//			ConstNode cn = (ConstNode) node;
	//
	//		}
	//		return null;
	//	}
	private Class<?> evaluateType(String text) {
		//		com.f1.utils.BasicTypes vars = new com.f1.utils.BasicTypes();
		//		for (Entry<String, Class> e : this.varTypes.entrySet()) {
		//			try {
		//				vars.put(e.getKey(), e.getValue());
		//			} catch (Exception ex) {
		//			}
		//		}
		try {
			DerivedCellCalculator t = scriptManager.toCalcNotOptimized(text, this.varTypes, null, null);
			return t.getReturnType();
		} catch (Exception ex) {
			if (LH.isFine(log))
				LH.fine(log, this.service.getUserName() + ": Problem with Text '", text + "': ", ex);
			return null;
		}
	}

	private void paramsToString(DerivedCellMemberMethod<Object> i, StringBuilder tmpBuf) {
		tmpBuf.append("(<i>");
		int length = i.getParamNames().length;
		for (int j = 0; j < length; j++) {
			String p = i.getParamNames()[j];
			Class type = j < length - 1 || i.getVarArgType() == null ? i.getParamTypes()[j] : i.getVarArgType();
			if (j > 0)
				tmpBuf.append(',');
			tmpBuf.append("<i style='color:#888888'>");
			tmpBuf.append(this.factory.forType(type)).append(' ');
			tmpBuf.append("</i>");
			tmpBuf.append(p);
			if (j == length - 1 && i.getVarArgType() != null)
				tmpBuf.append(" ... ");
		}
		tmpBuf.append("</i>)");
	}
	private String toString(MethodFactory f) {
		ParamsDefinition def = f.getDefinition();
		StringBuilder sb = new StringBuilder(def.getMethodName());
		sb.append("(");
		for (int i = 0; i < def.getParamsCount(); i++) {
			if (i > 0)
				sb.append(",");
			sb.append(def.getParamName(i));
			if (i == def.getParamsCount() - 1 && def.isVarArg())
				sb.append(" ... ");
		}
		sb.append(")");
		return sb.toString();

	}

	public IterableAndSize<MenuOption> getOptions() {
		return this.options.values();
	}

	private Class<?> getType(String str) {
		str = str.trim();
		Class type = this.varTypes.getType(str);
		if (type == null)
			type = this.globalVars.getType(str);
		return type;
		//		if (type == null)
		//			return null;
		//		Class<?> targetType;
		//		try {
		//			targetType = this.factory.forName(type);
		//		} catch (ClassNotFoundException e) {
		//			return null;
		//		}
		//		return targetType;
	}

	private DerivedCellMemberMethod<Object> getMethod(Class<?> targetType, String methodName) {
		if (targetType == null)
			return null;
		List<DerivedCellMemberMethod<Object>> sink = new ArrayList<DerivedCellMemberMethod<Object>>();
		this.factory.getMemberMethods(targetType, null, sink);
		if (methodName.length() > 0) {
			for (int i = 0; i < sink.size(); i++) {
				DerivedCellMemberMethod<Object> m = sink.get(i);
				if (m.getMethodName() == null)//constructor
					continue;
				if (m.getMethodName().equalsIgnoreCase(methodName))
					return m;
			}
		}
		return null;
	}

	private List<DerivedCellMemberMethod<Object>> getMethods(Class<?> targetType, String methodPrefix) {
		if (targetType == null)
			return Collections.EMPTY_LIST;
		methodPrefix = methodPrefix.trim();
		List<DerivedCellMemberMethod<Object>> sink = new ArrayList<DerivedCellMemberMethod<Object>>();
		this.factory.getMemberMethods(targetType, null, sink);
		Set<ParamsDefinition> t = new HasherSet<ParamsDefinition>(ParamsDefinition.HASHER_DEF);
		List<DerivedCellMemberMethod<Object>> sink2 = new ArrayList<DerivedCellMemberMethod<Object>>(sink.size());
		for (int i = 0; i < sink.size(); i++) {
			DerivedCellMemberMethod<Object> m = sink.get(i);
			if (m.getMethodName() == null)//constructor
				continue;
			if (SH.startsWithIgnoreCase(m.getMethodName(), methodPrefix)) {
				if (t.add(m.getParamsDefinition()))
					sink2.add(m);
			}
		}

		return sink2;
	}

	private Map<String, String> parseParts(String value) {
		reader.reset(value);
		reader.setToStringIncludesLocation(true);
		this.cmdStart = 0;
		this.funcStart = -1;
		//System.out.println("start parse");
		Map<String, String> r = parseParts(reader, false);
		//System.out.println("end parse\n\n");
		int parenthesisCount = 0;
		outer: for (int i = value.length() - 1; i > cmdStart; i--) {
			switch (value.charAt(i)) {
				case '(':
					if (parenthesisCount == 0) {
						cmdStart = i + 1;
						break outer;
					} else {
						parenthesisCount--;
						continue;
					}
				case ',':
					if (parenthesisCount == 0) {
						cmdStart = i + 1;
						break outer;
					} else
						continue;
				case ')':
					parenthesisCount++;
					continue;
				case ';':
					break outer;
			}
		}
		while (cmdStart < value.length() && WS.matches(value.charAt(cmdStart)))
			cmdStart++;
		return r;
	}

	private Map<String, String> parseParts(StringCharReader cr, boolean inForLoop) {
		SH.clear(tmpBuf);
		Map<String, String> r = new HashMap<String, String>();
		String type = null; //type is returnType of method
		outer: for (;;) {
			cr.skip(WS);
			if (cr.expectNoThrow('"')) {
				cr.readUntil('"', '\\', null);
				if (!cr.expectNoThrow('"'))
					break;
			}
			cr.skip(WS);
			cr.readUntilAny(JavaExpressionParser.SPECIAL_CHARS_AND_DOT, true, SH.clear(tmpBuf));
			if (SH.equalsIgnoreCase(tmpBuf, "IN")) {
				cr.skip(WS);
				r = parseParts(cr, false);
				return r;
			}
			cr.mark();
			cr.skip(WS);
			if (cr.peakOrEof() != '=' && cr.peakOrEof() != '(')
				cr.returnToMark();
			int c = cr.readCharOrEof();
			switch (c) {
				case '/':
					if (cr.peakOrEof() == '/') {
						cr.readUntil('\n', null);
						this.cmdStart = reader.getCountRead();
						continue;
					}
				case '(':
					this.funcStart = cmdStart;
					if (SH.equalsIgnoreCase("for", tmpBuf) || SH.equalsIgnoreCase("while", tmpBuf) || SH.equalsIgnoreCase("catch", tmpBuf) || SH.equalsIgnoreCase("if", tmpBuf)) {
						this.funcStart = -1;
						Map<String, String> forVars = parseParts(cr, true);
						cr.skip(WS);

						if (cr.expectNoThrow('{')) {
							this.cmdStart = reader.getCountRead();
							r.putAll(parseParts(cr, false));
							if (cr.isEof()) {
								r.putAll(forVars);
								break outer;
							}
						} else if (cr.isEof()) {
							r.putAll(forVars);
							break outer;
						} else {
							this.cmdStart = reader.getCountRead();
							r.putAll(forVars);
						}
						cr.skip(WS);
						cr.mark();
						cr.readUntilAny(JavaExpressionParser.SPECIAL_CHARS_AND_DOT, true, SH.clear(tmpBuf));
						if (SH.equalsIgnoreCase(tmpBuf, "else")) {
							cr.skip(WS);
						} else
							cr.returnToMark();
					} else if (type != null) {
						this.cmdStart = reader.getCountRead();
						this.funcStart = -1;
						r.clear();
						r.putAll(parseMethodDeclarationArgs(cr));
					} else {
						r.putAll(parseParts(cr, false));
					}
					break;

				case '{':
					this.cmdStart = reader.getCountRead();
					this.funcStart = -1;
					this.funcEnd = false;
					r.putAll(parseParts(cr, false));
					break;
				case '}':
					this.cmdStart = reader.getCountRead();
					this.funcStart = -1;
					reader.skip(WS);
					if (reader.peakSequence("catch")) {
						reader.expectSequence("catch");
						reader.skip(WS);
						if (reader.expectNoThrow('(')) {
							reader.skip(WS);
							reader.readUntilAny(SPECIAL_CHARS, true, SH.clear(tmpBuf));
							String typ = SH.toStringAndClear(tmpBuf);
							reader.skip(WS);
							reader.readUntilAny(SPECIAL_CHARS, true, SH.clear(tmpBuf));
							String val = SH.toStringAndClear(tmpBuf);
							reader.skip(WS);
							reader.expectNoThrow(')');
							r.put(val, typ);
							r.putAll(parseParts(cr, false));
							return r;
						}
					}
					return Collections.EMPTY_MAP;
				case ')':
					if (this.funcStart != -1)
						return inForLoop ? r : Collections.EMPTY_MAP;
				case CharReader.EOF:
					break outer;
				default:
					if (CMD_BREAKS.matches(c)) {
						while (CMD_BREAKS.matches(cr.peakOrEof()))
							cr.readChar();
						this.cmdStart = reader.getCountRead();
						this.funcStart = -1;
						if (this.funcEnd == true) {
							type = null;
							r.clear();
							this.funcEnd = false;
						}
					}
					if (SH.equalsIgnoreCase("throw", tmpBuf) || SH.equalsIgnoreCase("break", tmpBuf) || SH.equalsIgnoreCase("continue", tmpBuf)
							|| SH.equalsIgnoreCase("new", tmpBuf) || SH.equalsIgnoreCase("return", tmpBuf)) {
						continue;
					} else if (StringCharReader.WHITE_SPACE.matches(c) && SQL_TRIGGER_WORDS.contains(tmpBuf)) {
						tmpBuf.append(' ');
						skipTillDefBreak(cr, tmpBuf);
						String tableName = CreateTable.translate(tmpBuf);
						if (SH.isnt(tableName))
							tableName = RenameTable.translate(tmpBuf);
						if (SH.is(tableName))
							this.tableNames.add(tableName.trim());
					} else if (type == null) {
						if (WS.matches(c))
							type = tmpBuf.toString();
					} else {
						r.put(tmpBuf.toString(), type);
						if (c != ';')
							skipTillDefBreak(cr, tmpBuf);
						if (!cr.expectNoThrow(','))
							type = null;
					}

			}
		}

		return r;
	}

	private Map<String, String> parseMethodDeclarationArgs(StringCharReader cr) {
		Map<String, String> r = new HashMap();
		StringBuilder tmp = new StringBuilder();
		for (;;) {
			JavaExpressionParser.sws(cr);
			if (cr.expectNoThrow(')'))
				return r;
			cr.readWhileAny(StringCharReader.ALPHA_NUM_UNDERBAR, tmp);
			JavaExpressionParser.sws(cr);
			String type = SH.toStringAndClear(tmp);
			cr.readWhileAny(StringCharReader.ALPHA_NUM_UNDERBAR, tmp);
			String name = SH.toStringAndClear(tmp);
			if (SH.isnt(type) || SH.isnt(name))
				return Collections.EMPTY_MAP;
			r.put(name, type);
			JavaExpressionParser.sws(cr);
			if (cr.expectNoThrow(','))
				continue;
			return cr.expectNoThrow(')') ? r : Collections.EMPTY_MAP;
		}
	}
	private void skipTillDefBreak(StringCharReader cr, StringBuilder buf) {
		for (;;) {
			cr.readUntilAny(")(;,\"\'?:-*/+", true, null);
			if (cr.isEof())
				return;
			switch (cr.peak()) {
				case '"':
					buf.append(cr.expect('"'));
					cr.readUntil('"', '\\', buf);
					if (cr.isEof())
						return;
					buf.append(cr.expect('"'));
					continue;
				case '\'':
					buf.append(cr.expect('\''));
					cr.readUntil('\'', '\\', buf);
					if (cr.isEof())
						return;
					buf.append(cr.expect('\''));
					continue;
				default:
					return;
			}
		}
	}

	public com.f1.base.CalcTypes getVarTypes() {
		return this.varTypes;
	}

	private static final Comparator<MenuOption> MENU_SORTER = new Comparator<AmiWebScriptAutoCompletion.MenuOption>() {

		@Override
		public int compare(MenuOption o1, MenuOption o2) {
			int r = OH.compare(o1.type, o2.type);
			if (r == 0)
				r = OH.compare(o1.displayHtml, o2.displayHtml);
			return r;
		}

	};

	public WebMenu generateMenu() {
		WebMenu r = new BasicWebMenu();
		if (this.activeMethod != null) {
			String description = activeMethod.getDefinition().toString();
			if (activeMethod instanceof MemberMethodDerivedCellCalculator)
				description = factory.forType(((MemberMethodDerivedCellCalculator) activeMethod).getTarget().getReturnType()) + "::" + description;
			r.add(new BasicWebMenuLink("Jump to <i>" + description + " </i>", true, "active_method").setCssStyle("_cna=menu_item_help"));
		}
		if (this.activeType != null) {
			String description = factory.forType(activeType);
			r.add(new BasicWebMenuLink("Jump to <i>" + description + " </i>", true, "active_type").setCssStyle("_cna=menu_item_help"));
		}

		if (!SH.endsWith(this.cmd, '.')) // exclude wizard menus when user types period
			AmiWebMenuUtils.createMemberMethodMenu(r, AmiWebUtils.getService(this.field.getForm().getManager()), true, true, true, this.layoutAlias);
		for (MenuOption i : CH.sort(getOptions(), MENU_SORTER)) {
			if (i.parent == null)
				optionsToMenu(r, i);
		}
		return r;
	}
	private void optionsToMenu(WebMenu r, MenuOption i) {
		if (CH.isntEmpty(i.children)) {
			BasicWebMenu m = new BasicWebMenu(i.displayHtml, true);
			int type = "this".equalsIgnoreCase(i.displayHtml) ? MenuOption.TYPE_CONST : i.type;//TODO: Not correct
			switch (type) {
				case MenuOption.TYPE_TYPE:
				case MenuOption.TYPE_KEYWORD:
				case MenuOption.TYPE_CONST:
					m.setCssStyle("_fm=monospace|_fg=#7f0055");
					break;
				case MenuOption.TYPE_VAR:
					m.setCssStyle("_fm=monospace|_fg=#007850");
					break;
				case MenuOption.TYPE_HINT:
					m.setEnabled(true);
					m.setCssStyle("_cna=menu_item_help");
					break;
				default:
					m.setCssStyle("_fm=monospace");
					break;
			}
			for (MenuOption child : i.children)
				optionsToMenu(m, child);
			r.add(m);
		} else {
			BasicWebMenuLink m = new BasicWebMenuLink(i.displayHtml, true, SH.toString(i.id));
			int type = "this".equalsIgnoreCase(i.displayHtml) ? MenuOption.TYPE_CONST : i.type;//TODO: Not correct
			switch (type) {
				case MenuOption.TYPE_TYPE:
				case MenuOption.TYPE_KEYWORD:
				case MenuOption.TYPE_CONST:
					m.setCssStyle("_fm=monospace|_fg=#7f0055");
					break;
				case MenuOption.TYPE_VAR:
					m.setCssStyle("_fm=monospace|_fg=#007850");
					break;
				case MenuOption.TYPE_HINT:
					m.setEnabled(true);
					m.setCssStyle("_cna=menu_item_help");
					break;
				default:
					m.setCssStyle("_fm=monospace");
					break;
			}
			r.add(m);
		}
	}
	@Override
	public void onMenuItem(String id) {
		if ("active_type".equals(id)) {
			this.service.getDesktop().showDocumentationPortlet();
			AmiWebDocumentationPortlet dp = this.service.getDesktop().getSpecialPortlet(AmiWebDocumentationPortlet.class);
			dp.setActiveClass(this.activeType);
			return;
		} else if ("active_method".equals(id)) {
			if (this.activeMethod instanceof DerivedCellCalculatorMethod) {
				DerivedCellCalculatorMethod dmm = (DerivedCellCalculatorMethod) this.activeMethod;
				DeclaredMethodFactory mf = dmm.getMethodFactory();
				if (mf instanceof AmiWebDeclaredMethodFactory) {
					this.service.getDesktop().showCustomMethodsPortlet();
					AmiWebMethodPortlet mmp = service.getDesktop().getMethodPortlet().getMethodPortlet(((AmiWebDeclaredMethodFactory) mf).getLayoutAlias());
					if (mmp != null) {
						service.getDesktop().getMethodPortlet().getTabs().bringToFront(mmp.getPortletId());
						int position = mf.getInner().getPosition();
						mmp.getAmiScriptEditor().moveCursor(position, true);
						int sLine = SH.getLinePosition(mmp.getAmiScriptEditor().getValue(), position).getA();
						int eLine = SH.getLinePosition(mmp.getAmiScriptEditor().getValue(), position + mf.getBodyText().length()).getA();
						mmp.getAmiScriptEditor().flashRows(sLine, eLine, "yellow");
					}
				}
			} else if (this.activeMethod instanceof MethodDerivedCellCalculator) {
				MethodDerivedCellCalculator mm = (MethodDerivedCellCalculator) this.activeMethod;
				this.service.getDesktop().showDocumentationPortlet();
				AmiWebDocumentationPortlet dp = this.service.getDesktop().getSpecialPortlet(AmiWebDocumentationPortlet.class);
				if (mm instanceof MemberMethodDerivedCellCalculator)
					dp.setActiveMemberMethod(((MemberMethodDerivedCellCalculator) mm).getTarget().getReturnType(), mm.getDefinition());
				else
					dp.setActiveMethod(mm.getDefinition());
			}
			return;
		}
		if (AmiWebMenuUtils.processContextMenuAction(service, id, this.field)) {
			setMenuActive(false);
			return;
		}
		MenuOption option = this.options.get(SH.parseLong(id));
		//If Option is of type hint bring up help
		if (option.type == MenuOption.TYPE_HINT) {
			SH.clear(hintHtmlBuffer);
			if (option.targetType == null) {
				//AmiWebFunction
				this.service.getDesktop().showDocumentationPortlet();
				AmiWebDocumentationPortlet dp = this.service.getDesktop().getSpecialPortlet(AmiWebDocumentationPortlet.class);
				dp.setActiveMethod(option.paramsDefinition);
			} else if (option.paramsDefinition == null) {
				//Class
				Class<?> clazz = getType(option.targetType);
				if (clazz != null) {
					this.service.getDesktop().showDocumentationPortlet();
					AmiWebDocumentationPortlet dp = this.service.getDesktop().getSpecialPortlet(AmiWebDocumentationPortlet.class);
					dp.setActiveClass(clazz);
				}
			} else {
				//AmiAbstractMemberMethod
				//				DerivedCellMemberMethod<Object> method = getMethod(getType(option.targetType), option.methodName);
				Class<?> type = getType(option.targetType);
				if (type != null) {
					this.service.getDesktop().showDocumentationPortlet();
					AmiWebDocumentationPortlet dp = this.service.getDesktop().getSpecialPortlet(AmiWebDocumentationPortlet.class);
					dp.setActiveMemberMethod(type, option.paramsDefinition);
				}
			}
		} else {
			//Autocomplete
			// ex: ...session...
			//          ^
			// find out where the word begins
			int prevLen = this.field.getValue().length();
			int formLength = option.fullForm.length();
			int curLen = formLength - option.value.length();

			int start = option.replaceStart - curLen;
			if (option.type == MenuOption.TYPE_KEYWORD) {
				// for SQL and other predefined keywords, append
				this.field.setValue(SH.splice(this.field.getValue(), option.replaceStart, option.replaceEnd - option.replaceStart, option.value));
			} else {
				// replace everything
				this.field.setValue(SH.splice(this.field.getValue(), start, curLen, option.fullForm));
			}
			// I think this can be done in js as well: ami_scripteditor::setValue
			if (this.field.getValue().length() > prevLen)
				this.field.setCursorPosition(start + formLength);
			this.field.focus();
		}
	}
	@Override
	public void onMenuDismissed() {
		this.setMenuActive(false);
	}

	public boolean isMenuActive() {
		return menuActive;
	}

	public void setMenuActive(boolean menuActive) {
		this.menuActive = menuActive;
	}

	public static interface Completer {

		public void autoComplete(CharReader cr, Collection<String> tableNames, List<MenuOption> sink);
	}

	public static class MultiCompleter implements Completer {
		private List<Completer> completers = new ArrayList<Completer>();

		@Override
		public void autoComplete(CharReader cr, Collection<String> tableNames, List<MenuOption> sink) {
			for (Completer i : completers) {
				int position = cr.getCountRead();
				try {
					i.autoComplete(cr, tableNames, sink);
				} finally {
					((StringCharReader) cr).setCountRead(position);
				}
			}
		}

		public MultiCompleter addCompleter(Completer completer) {
			this.completers.add(completer);
			return this;
		}

	}

	public static class SqlCompleter implements Completer {

		private String[] tokenList;
		private String token;
		private Completer next;

		public SqlCompleter(String template, Completer next) {
			this.tokenList = SH.split(' ', template);
			this.next = next;
		}

		public void autoComplete(CharReader cr, Collection<String> tableNames, List<MenuOption> sink) {
			StringBuilder buf = new StringBuilder();
			cr.setCaseInsensitive(true);
			String soFar = cr.getText();
			String cur = soFar; // tracks user input against valid tokens
			String fullForm = ""; // records the complete form of a SQL command, e.g. CREATE TABLE AS
			Boolean complete = false;
			for (int i = 0; i < tokenList.length; i++) {
				String token = tokenList[i];
				cr.skip(WS);
				int start = cr.getCountRead();
				if ("?".equals(token)) {
					cr.readUntilAny(WS, true, SH.clear(buf));
					// accounts for user table name
					fullForm += " " + cur;
				} else if ("_".equals(token)) {
					cr.readUntilAny(WS, true, SH.clear(buf));
					int len = buf.length();
					String t = buf.toString();
					if (!tableNames.contains(t)) {
						if (cr.isEof()) {
							for (String name : tableNames)
								if (SH.startsWithIgnoreCase(name, t)) {
									String name2;
									if (len == 0)
										name2 = ' ' + name + ' ';
									else
										name2 = name + ' ';
									sink.add(new MenuOption(0, MenuOption.TYPE_KEYWORD, name2, name, start, cr.getCountRead(), name));
								}
							return;
						}
					}
				} else if (cr.peakSequence(token)) {
					// check if user typed in a valid token
					cr.expectSequence(token);
					if (cr.skip(WS) == 0) {
						if (!cr.isEof())
							return;
					}
					// if previous token is a complete match, we need a white space before next token.
					if (complete) {
						fullForm += " ";
						complete = false;
					} else {
						complete = true;
					}
					fullForm += token;
					// check off current token, and trim.
					cur = SH.stripPrefixIgnoreCase(cur, token, false).trim();
				} else {
					// check for partial match
					cr.readUntil(CharReader.EOF, SH.clear(buf));
					// check best match between a token and the rest of what user has typed so far
					if (SH.startsWithIgnoreCase(token, buf)) {
						int len = buf.length();
						SH.clear(buf);
						if ((start > 0 && len == 0)) {
							buf.append(' ');
						} else if (complete) {
							// previous token completely matched, add whitespace
							fullForm += " ";
							complete = false;
						}
						boolean appendTables = false;
						// grab remaining consecutive tokens for suggestion
						for (int n = i; n < tokenList.length; n++) {
							String token2 = tokenList[n];
							if ("_".equals(token2)) {
								appendTables = true;
								break;
							}
							if ("?".equals(token2)) {
								break;
							}
							buf.append(token2);
							buf.append(' ');
						}
						String tokens = buf.toString();
						fullForm += tokens;
						// discount the remainder of user input from valid tokens to form the final suggestion
						String remain = SH.stripPrefixIgnoreCase(tokens, cur, false);
						if (appendTables) {
							for (String name : tableNames)
								sink.add(new MenuOption(0, MenuOption.TYPE_KEYWORD, tokens + name, tokens + name, start, cr.getCountRead(), name));
						} else {
							int replaceStart = soFar.length();
							sink.add(new MenuOption(0, MenuOption.TYPE_KEYWORD, remain, tokens, replaceStart, replaceStart, fullForm));
						}

						return;
					} else
						return;
				}
			}
			if (next != null)
				next.autoComplete(cr, tableNames, sink);
		}
	}

	public static void main(String a[]) {

		MultiCompleter t = new MultiCompleter();
		MultiCompleter alter = new MultiCompleter().addCompleter(new SqlCompleter("ADD", null)).addCompleter(new SqlCompleter("MODIFY", null));
		t.addCompleter(new SqlCompleter("ALTER TABLE _ ", alter));
		test(t, "ALTERTABLE ");
		test(t, "ALTER TABLE test ");
		test(t, "ALTER TABLE blah FROM SEL");
		test(t, "ALTA");
		test(t, "ALTER");
		test(t, "ALTER TA");
		test(t, "ALTER TAE");

	}
	private static void test(Completer t, String string) {
		List<MenuOption> sink = new ArrayList<AmiWebScriptAutoCompletion.MenuOption>();
		Collection<String> tn = CH.l("test", "this", "out");
		t.autoComplete(new StringCharReader(string).setToStringIncludesLocation(true), tn, sink);
		System.out.println();
		System.out.println();
		System.out.println(string);
		System.out.println(SH.join("\n", sink));
	}

	public void setAmiLayoutFullAlias(String alias) {
		if (OH.ne(this.layoutAlias, alias)) {
			this.layoutAlias = alias;
			this.scriptManager = this.service.getScriptManager(alias);
			this.factory = scriptManager.getMethodFactory();
		}
	}
}
