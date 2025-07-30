package com.f1.console.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.f1.base.Console;
import com.f1.base.Table;
import com.f1.console.ConsoleConnection;
import com.f1.console.ConsoleManager;
import com.f1.console.ConsoleSession;
import com.f1.console.impl.shell.ShellAutoCompletion;
import com.f1.utils.OH;
import com.f1.utils.RH;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.f1.utils.structs.table.BasicTable;

public class ShowObjectsConsoleService extends AbstractConsoleService {
	public ShowObjectsConsoleService() {
		super("show objects", "SHOW +OBJECTS", "Shows all objects that can be invoked via console. Usage: SHOW OBJECTS");
	}

	private static final Logger log = Logger.getLogger(ShowObjectsConsoleService.class.getName());
	public static final Pattern PATTERN = Pattern.compile("SHOW +OBJECTS", Pattern.CASE_INSENSITIVE);
	public static final String GLOBAL_INVOKABLES = "ManagedInvokers";
	public static final String LOCAL_INVOKABLES = "LocalInvokables";
	private static final String LOCAL_IMPORTS = "LocalImports";
	private static final String GLOBAL_IMPORTS = "GlobalImports";

	@Override
	public void doRequest(ConsoleSession session, String[] options) {
		ConsoleConnection out = session.getConnection();
		Table t = new BasicTable(String.class, "Object", String.class, "Type", String.class, "Help");
		Map<String, Object> globalInvokables = getGlobalInvokables(session.getManager());
		t.setTitle("SHOW OBJECTS (" + globalInvokables.size() + " objects)");
		for (Map.Entry<String, Object> i : globalInvokables.entrySet()) {
			Class<? extends Object> v = i.getValue().getClass();
			String name, help;
			Console annotation = v.getAnnotation(Console.class);
			if (annotation != null && SH.is(annotation.name())) {
				name = annotation.name();
				help = annotation.help();
			} else {
				name = RH.toLegibleString(v);
				help = "";
			}
			t.getRows().addRow(i.getKey(), name, help);
		}
		out.print(TableHelper.toString(t, "", TableHelper.SHOW_ALL_BUT_TYPES));
	}

	public static void describe(boolean detailed, String name, Class<?> clazz, StringBuilder out) {
		Console annotation = clazz.getAnnotation(Console.class);
		if (annotation == null)
			return;

		Table t = new BasicTable(String.class, "Method", String.class, "Description");
		String help = annotation.help();
		t.setTitle(name + " - " + (detailed ? help : SH.ddd(help, 80)));
		StringBuilder sb = new StringBuilder();
		for (Method m : clazz.getMethods()) {
			annotation = m.getAnnotation(Console.class);
			if (annotation == null)
				continue;
			help = annotation.help();
			SH.clear(sb);
			sb.append(name).append(".");
			sb.append(m.getName()).append('(');
			String[] paramNames = annotation.params();
			Class<?>[] types = m.getParameterTypes();
			if (paramNames.length == types.length) {
				for (int i = 0; i < types.length; i++) {
					if (i != 0)
						sb.append(", ");
					sb.append(types[i].getSimpleName()).append(' ').append(paramNames[i]);
				}
			} else {
				for (int i = 0; i < types.length; i++) {
					if (i != 0)
						sb.append(", ");
					sb.append(types[i].getSimpleName());
				}
			}
			sb.append(")");
			t.getRows().addRow(sb.toString(), detailed ? help : SH.ddd(SH.replaceAll(help, "\n", ". "), 80));
			sb.setLength(0);
		}
		out.append(TableHelper.toString(t, "", TableHelper.SHOW_ALL_BUT_TYPES));
	}

	@Override
	public void doStartup(ConsoleSession session) {
		HashMap<String, Object> m = new HashMap<String, Object>();
		m.put("out", session.getConnection().getOut());
		setLocalInvokables(session, m);

		setLocalImports(session, new ArrayList<String>());
	}

	public static Map<String, Object> getGlobalInvokables(ConsoleManager app) {
		return (Map<String, Object>) app.getValue(GLOBAL_INVOKABLES);
	}

	public static void setGlobalInvokables(ConsoleManager app, Map<String, Object> methods) {
		app.setValue(GLOBAL_INVOKABLES, methods);
	}

	public static Object getInvokable(ConsoleSession session, String name) {
		Object r = null;
		Map<String, Object> t = getLocalInvokables(session);
		if (t != null)
			r = t.get(name);
		if (r == null)
			r = ShowObjectsConsoleService.getGlobalInvokables(session.getManager()).get(name);
		return r;
	}

	public static Map<String, Object> getLocalInvokables(ConsoleSession session) {
		return (Map<String, Object>) session.getValue(LOCAL_INVOKABLES);
	}

	public static void setLocalInvokables(ConsoleSession session, Map<String, Object> methods) {
		session.setValue(LOCAL_INVOKABLES, methods);
	}

	public static List<String> getLocalImports(ConsoleSession session) {
		return (List<String>) session.getValue(LOCAL_IMPORTS);
	}

	public static void setLocalImports(ConsoleSession session, List<String> imports) {
		session.setValue(LOCAL_IMPORTS, imports);
	}

	public static List<String> getGlobalImports(ConsoleManager session) {
		return (List<String>) session.getValue(GLOBAL_IMPORTS);
	}

	public static void setGlobalImports(ConsoleManager session, List<String> imports) {
		session.setValue(GLOBAL_IMPORTS, imports);
	}
	@Override
	public boolean canAutoComplete(String partialText) {
		return true;
	}

	@Override
	public ShellAutoCompletion autoComplete(ConsoleSession session, String partialText) {
		Map<String, Object> globalInvokables = getGlobalInvokables(session.getManager());
		String target = SH.beforeFirst(partialText, '.', null);
		final BasicTelnetAutoCompletion r;
		r = new BasicTelnetAutoCompletion(partialText);
		if (target != null) {
			Object invokable = globalInvokables.get(target);
			if (invokable == null)
				return null;
			Class<? extends Object> clazz = invokable.getClass();
			Console annotation = clazz.getAnnotation(Console.class);
			if (annotation == null)
				return null;
			String methodPartialText = SH.afterFirst(partialText, '.', null);
			if (methodPartialText.indexOf('(') != -1) {
				String method = SH.beforeFirst(methodPartialText, '(');
				for (Method m : clazz.getMethods()) {
					annotation = m.getAnnotation(Console.class);
					if (annotation == null)
						continue;
					String name = m.getName();
					if (OH.eq(name, method)) {
						StringBuilder sb = new StringBuilder();
						String[] paramNames = annotation.params();
						Class<?>[] types = m.getParameterTypes();
						if (paramNames.length == types.length) {
							for (int i = 0; i < types.length; i++) {
								if (i != 0)
									sb.append(", ");
								sb.append(types[i].getSimpleName()).append(' ').append(paramNames[i]);
							}
						} else {
							for (int i = 0; i < types.length; i++) {
								if (i != 0)
									sb.append(", ");
								sb.append(types[i].getSimpleName());
							}
						}
						sb.append(")");
						r.addComment(sb.toString());
					}
				}
			} else {
				for (Method m : clazz.getMethods()) {
					annotation = m.getAnnotation(Console.class);
					if (annotation == null)
						continue;
					String name = m.getName();
					if (name.startsWith(methodPartialText)) {
						if (m.getParameterTypes().length == 0)
							r.add(name.substring(methodPartialText.length()) + "()");
						else
							r.add(name.substring(methodPartialText.length()) + "(");
					}
				}
			}
		} else {
			for (String s : globalInvokables.keySet()) {
				if (s.startsWith(partialText))
					r.add(s.substring(partialText.length()) + ".");
			}
		}
		if (r.isEmpty())
			return null;
		return r;
	}

	private String common(String text, String s) {
		if (text == null)
			return s;
		else if (s.startsWith(text))
			return text;
		else if (text.startsWith(s))
			return s;
		else
			for (int i = 0; true; i++)
				if (text.charAt(i) != s.charAt(i))
					return text.substring(0, i);
	}
}
