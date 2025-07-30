package com.f1.console.impl;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.f1.base.Console;
import com.f1.console.ConsoleConnection;
import com.f1.console.ConsoleSession;
import com.f1.console.impl.shell.ShellAutoCompletion;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.RH;
import com.f1.utils.ReflectionException;
import com.f1.utils.SH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.InvokerException;
import com.f1.utils.string.JavaExpressionParser;
import com.f1.utils.string.JavaInvoker;
import com.f1.utils.string.JavaInvoker.PartialOperation;
import com.f1.utils.string.Node;

/**
 * Supports a java like parse tree, with a few exceptions:<BR>
 * (1) No types, no casting and declared variables are not strongly typed<BR>
 * (2) No stack / block scope for variables<BR>
 * (3) An expression without an operand will be evaluated by calling toString()<BR>
 * (4) The final value evaluated will have toString() invoked.<BR>
 * <P>
 * The following statements are supported:<BR>
 * (1) <B>BLOCK</B> { <STATEMENT> } -- (returns void) <BR>
 * (2) <B>FOR LOOP</B> - for(initialization;termination;increment) <STATEMENT> -- (returns void)<BR>
 * (3) <B>FOREACH LOOP</B> - for(initialization;iterable_or_array) <STATEMENT> -- (returns void)<BR>
 * (4) <B>WHILE LOOP</B> - while(expression) <STATEMENT> -- (returns void)<BR>
 * (5) <B>DO WHILE LOOP</B> do { <STATEMENT> } while(expression); -- (returns void)<BR>
 * (6) <B>IF CONTROL</B> if(boolean_expression) true_expression (7) <B>IF ELSE CONTROL</B> if(boolean_expression) true_expression else false_expression -- (returns void)<BR>
 * (8) <B>ASSIGNMENT</B> variable assignmentOperator expression -- (returns evaluated expression)
 * <P>
 * 
 * The following declarations are supported:<BR>
 * (1) <B>NEW DECLARATION<B> new classtype(argmumentlist);<BR>
 * (2) <B>ARRAY DECLARATION<B> new classtype[]{valuelist};<BR>
 * (3) <B>ARRAY DEFAULT DECLARATION<B> new classtype[size];<BR>
 * (4) <B>STRING CONSTANT<B>"string";<BR>
 * (5) <B>CHAR CONSTANT<B>'character';<BR>
 * (6) <B>INT CONSTANT<B>number;<BR>
 * (7) <B>HEX INT CONSTANT<B>0xnumber;<BR>
 * (8) <B>OCT INT CONSTANT<B>0number;<BR>
 * (9) <B>LONG CONSTANT<B>numberL;<BR>
 * (10) <B>HEX LONG CONSTANT<B>0xnumberL;<BR>
 * (11) <B>OCT LONG CONSTANT<B>0numberL;
 * <P>
 * 
 * The following operators are supported:
 * 
 * Priority 1: <B>[ ]</B> array index <BR>
 * Priority 1: <B>()</B> method call<BR>
 * Priority 1: <B>.</B> member access<BR>
 * Priority 2: <B>++</B> pre- or postfix increment <BR>
 * Priority 2: <B>--</B> pre- or postfix decrement<BR>
 * Priority 2: <B>+ -</B> unary plus, minus<BR>
 * Priority 2: <B>~</B> bitwise NOT<BR>
 * Priority 2: <B>!</B> boolean (logical) NOT<BR>
 * Priority 2: <B>new</B> object creation<BR>
 * Priority 3: <B>* / %</B> multiplication, division, remainder <BR>
 * Priority 4: <B>+ -</B> addition, substraction,string concatenation <BR>
 * Priority 5: <B><< </B> signed bit shift <BR>
 * Priority 5: <B>>> </B>signed bit shift right<BR>
 * Priority 5: <B>>>> </B>unsigned bit shift right<BR>
 * Priority 6: <B>< <= </B>less than, less than or equal to <BR>
 * Priority 6: <B>> >= </B>greater than, greater than or equal to<BR>
 * Priority 6: <B>instanceof </B>reference test<BR>
 * Priority 7: <B>== </B>equal to <BR>
 * Priority 7: <B>!= </B>not equal to<BR>
 * Priority 8: <B>& </B>bitwise AND <BR>
 * Priority 8: <B>& </B>boolean (logical) AND<BR>
 * Priority 9: <B>^ </B>bitwise XOR <BR>
 * Priority 9: <B>^ </B>boolean (logical) XOR<BR>
 * Priority 10: <B>| </B>bitwise OR <BR>
 * Priority 10: <B>| </B>boolean (logical) OR<BR>
 * Priority 11: <B>&& </B>boolean (logical) AND <BR>
 * Priority 12: <B>|| </B>boolean (logical) OR <BR>
 * Priority 13: <B>? : </B>conditional <BR>
 * Priority 14: <B>= </B>assignment <BR>
 * Priority 14: <B>= /= += -= %= <<= >>= >>>= &= ^= |= </B>combined assigment<BR>
 * <BR>
 * show
 */
public class InvokerConsoleService extends AbstractConsoleService {

	public InvokerConsoleService() {
		super("<expression>", Pattern.compile("([a-z0-9]+\\w*\\.\\w*[a-z][a-z0-9]*\\w*\\(.*)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
				"Call a method on an object. Usage object.method(args);");
	}

	private static final Logger log = Logger.getLogger(InvokerConsoleService.class.getName());
	public static final Pattern PATTERN_VARNAME = Pattern.compile("[a-zA-Z_][\\S]*");
	public static final Object VOID = new Object() {
		@Override
		public String toString() {
			return "VOID";
		}
	};
	public static final Pattern PATTERN = Pattern.compile("C(?:ALL)? (.*)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	@Override
	public void doRequest(ConsoleSession session, String[] options) {

		Object result = doRequest(session, options[1]);
		PrintWriter out = session.getConnection().getOut();
		if (result != VOID && !RH.isVoidReturn(result))
			out.println(result);
	}

	public static Object doRequest(ConsoleSession session, String expression) {
		final ConsoleConnection out = session.getConnection();
		expression = expression.trim();
		if (!expression.endsWith(";"))
			expression += ";";
		Exception ex;
		String exMessage;
		try {

			final HashMap<String, Object> objects = new HashMap<String, Object>();
			Map<String, Object> global, local;
			objects.putAll(global = ShowObjectsConsoleService.getGlobalInvokables(session.getManager()));
			objects.putAll(local = ShowObjectsConsoleService.getLocalInvokables(session));
			final List<String> imports = new ArrayList<String>(ShowObjectsConsoleService.getGlobalImports(session.getManager()));
			imports.addAll(ShowObjectsConsoleService.getLocalImports(session));
			final JavaExpressionParser javaExpressionParser = new JavaExpressionParser();
			final JavaInvoker evaluator = new JavaInvoker();
			Node node = null;
			Object result = null;
			objects.putAll(global = ShowObjectsConsoleService.getGlobalInvokables(session.getManager()));

			try {
				node = javaExpressionParser.parse(expression);
			} catch (ExpressionParserException e) {
				throw e;
			} catch (Exception e) {
				throw new InvokerException("general error parsing message", e);
			}
			if (node == null) {
				result = VOID;
			}
			result = evaluator.evaluate(node, new JavaInvoker.MapBackedObjectScope(objects, imports));
			if (result instanceof Package)
				throw new InvokerException("object not found: " + result);
			// TODO:local vars overriding global vars
			for (Map.Entry<String, Object> e : objects.entrySet())
				if (!global.containsKey(e.getKey()))
					local.put(e.getKey(), e.getValue());
			imports.removeAll(ShowObjectsConsoleService.getGlobalImports(session.getManager()));
			ShowObjectsConsoleService.setLocalImports(session, imports);
			return result;
		} catch (InvokerException e) {
			ex = e;
			exMessage = e.getMessage();
		} catch (ReflectionException e) {
			ex = e;
			exMessage = e.getMessage();
		} catch (ExpressionParserException e) {
			ex = e;
			exMessage = e.getMessage();
		} catch (NumberFormatException e) {
			ex = e;
			exMessage = "Error parsing number " + e.getMessage();
		} catch (RuntimeException e) {
			throw e;
		}
		LH.info(log, "Handled Exception", ex);
		out.comment(ConsoleConnection.COMMENT_ERROR, "  " + exMessage);
		return VOID;
	}

	@Override
	public void doStartup(ConsoleSession session) {
	}

	@Override
	public void doShutdown(ConsoleSession session) {
	}

	@Override
	public String getHelp() {
		String help = "call <java_like_expression>;";
		return help;
	}

	@Override
	public String getName() {
		return "call";
	}

	@Override
	public String getDescription() {
		return "Calls a method.  If variable specified the result will be stored accordingly, otherwise displayed to console";
	}

	@Override
	public boolean canAutoComplete(String partialText) {
		return super.canProcessRequest(partialText);
	}

	@Override
	public ShellAutoCompletion autoComplete(ConsoleSession session, String partialText) {
		final HashMap<String, Object> objects = new HashMap<String, Object>();
		final BasicTelnetAutoCompletion r = new BasicTelnetAutoCompletion(partialText);
		Map<String, Object> global, local;
		objects.putAll(global = ShowObjectsConsoleService.getGlobalInvokables(session.getManager()));
		objects.putAll(local = ShowObjectsConsoleService.getLocalInvokables(session));
		final List<String> imports = new ArrayList<String>(ShowObjectsConsoleService.getGlobalImports(session.getManager()));
		imports.addAll(ShowObjectsConsoleService.getLocalImports(session));
		final JavaExpressionParser javaExpressionParser = new JavaExpressionParser();
		final JavaInvoker evaluator = new JavaInvoker();
		evaluator.setAllowPartial(true);
		javaExpressionParser.setAllowEof(true);
		String text = parsePattern(partialText)[1];
		Node node = null;
		Object result = null;
		try {
			// TODO:HACK UNTIL MAP IS PASSED IN
			objects.putAll(global = ShowObjectsConsoleService.getGlobalInvokables(session.getManager()));
			try {
				node = javaExpressionParser.parse(text);
			} catch (ExpressionParserException e) {
				throw e;
			} catch (Exception e) {
				throw new InvokerException("general error parsing message", e);
			}
			if (node == null) {
				result = VOID;
			}
			result = evaluator.evaluate(node, new JavaInvoker.MapBackedObjectScope(objects, imports));
			if (result instanceof JavaInvoker.Package) {
				JavaInvoker.Package pkg = (com.f1.utils.string.JavaInvoker.Package) result;
				String left = SH.beforeLast(pkg.name, '.', null);
				Object l = objects.get(left);
				String right = SH.afterLast(pkg.name, '.', pkg.name);
				if (l == null) {
					try {
						l = evaluator.findClass(left, imports);
					} catch (Exception e) {
						l = null;
					}
				}
				autoFill(l, right, session, r, objects);

			} else if (result instanceof JavaInvoker.PartialOperation) {
				PartialOperation po = (JavaInvoker.PartialOperation) result;
				autoFill(po.left, po.right, session, r, objects);

				return r;
			}
			return r;
		} catch (Exception e) {
			LH.info(log, "Error autocompleting", e);
			return null;
		}

	}

	private void autoFill(Object target, String prefix, ConsoleSession session, BasicTelnetAutoCompletion sink, HashMap<String, Object> objects) {
		if (target == null) {
			for (String key : objects.keySet()) {
				if (key.startsWith(prefix))
					sink.add(key.substring(prefix.length()));
			}
			return;
		}

		if (prefix.endsWith("(")) {
			prefix = prefix.substring(0, prefix.length() - 1);
			Class<?> clazz;
			boolean statik;
			if (target instanceof Class) {
				clazz = (Class<?>) target;
				statik = true;
			} else {
				clazz = target.getClass();
				statik = false;
			}
			for (Method m : RH.getMethods(clazz)) {
				if (statik != Modifier.isStatic(m.getModifiers()))
					continue;
				if (!m.getName().startsWith(prefix))
					continue;
				Console console = m.getAnnotation(Console.class);
				if (console != null && console.help() != null)
					sink.addComment(SH.prefixLines(console.help(), "// "));
				String[] paramNames = console == null ? OH.EMPTY_STRING_ARRAY : console.params();
				sink.addComment("  " + RH.toLegibleString(m, true, true, paramNames));
			}
		} else {
			Class clazz;
			boolean statik;
			if (target instanceof Class) {
				clazz = (Class) target;
				statik = true;
			} else {
				clazz = target.getClass();
				statik = false;
			}
			if (prefix.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				ShowObjectsConsoleService.describe(false, null, clazz, sb);
				sink.addComment(sb.toString());
			}
			Console console = (Console) clazz.getAnnotation(Console.class);
			boolean hide = console != null && console.hideUndeclared();
			for (Method m : RH.getMethods(clazz)) {
				if (hide && m.getAnnotation(Console.class) == null)
					continue;
				if (statik != Modifier.isStatic(m.getModifiers()))
					continue;
				if (!m.getName().startsWith(prefix))
					continue;
				if (m.getParameterTypes().length == 0)
					sink.add(m.getName().substring(prefix.length()) + "()");
				else
					sink.add(m.getName().substring(prefix.length()) + "(");
			}
			for (Field m : RH.getFields(clazz)) {
				if (hide && m.getAnnotation(Console.class) == null)
					continue;
				if (statik != Modifier.isStatic(m.getModifiers()))
					continue;
				if (!m.getName().startsWith(prefix))
					continue;
				sink.add(m.getName().substring(prefix.length()));
			}
		}

	}

}
