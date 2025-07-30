package com.f1.utils;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.f1.base.Caster;
import com.f1.utils.casters.Caster_String;

public class PreferencesHelper {

	public static String toString(Preferences preferences) throws BackingStoreException {
		return toString(preferences, 0, 4, new StringBuilder()).toString();
	}
	public static StringBuilder toString(Preferences preferences, int indent, int tabSize, StringBuilder sb) throws BackingStoreException {
		SH.repeat(' ', indent * tabSize, sb);
		sb.append(preferences.name()).append('{');
		if (preferences.childrenNames().length == 0 && preferences.keys().length == 0)
			return sb.append('}').append(SH.NEWLINE);
		sb.append(SH.NEWLINE);
		indent++;
		for (String name : preferences.childrenNames()) {
			toString(preferences.node(name), indent, tabSize, sb);
		}

		for (String key : preferences.keys()) {
			SH.repeat(' ', indent * tabSize, sb);
			sb.append(key).append(": ");
			sb.append(preferences.get(key, null)).append(SH.NEWLINE);
		}
		indent--;
		SH.repeat(' ', indent * tabSize, sb).append('}').append(SH.NEWLINE);
		return sb;
	}

	static public Preferences getChildIfExists(String dotDelimPath) throws BackingStoreException {
		String remaining = SH.afterFirst(dotDelimPath, '.', null);
		return getChildIfExists(getRootForPath(dotDelimPath, false), remaining);
	}
	static public Preferences getChildIfExists(Preferences parent, String dotDelimPath) throws BackingStoreException {
		assertValidPath(dotDelimPath);
		if (parent == null || dotDelimPath.isEmpty())
			return parent;
		for (String part : SH.split('.', dotDelimPath)) {
			parent.sync();
			if (parent.nodeExists(part))
				parent = parent.node(part);
			else
				return null;
		}
		return parent;
	}
	static public String getValueIfExists(Preferences node, String keyName) throws BackingStoreException {
		node.sync();
		return AH.indexOf(keyName, node.keys()) == -1 ? null : node.get(keyName, null);
	}
	static public String setNestedValue(String pathDotKey, Object value) throws BackingStoreException {
		String remaining = SH.afterFirst(pathDotKey, '.', null);
		return setNestedValue(getRootForPath(pathDotKey, true), remaining, value);
	}

	public static Preferences getRootForPath(String pathDotKey, boolean throwOnNotFound) {
		String prefix = SH.beforeFirst(pathDotKey, '.', null);
		if ("system".equals(prefix))
			return Preferences.systemRoot();
		if ("user".equals(prefix))
			return Preferences.userRoot();
		if (throwOnNotFound)
			throw new IllegalArgumentException("fully qualified preferences path must start with 'user' or 'system'. Supplied path: " + pathDotKey);
		return null;
	}

	static public String setNestedValue(Preferences root, String pathDotKey, Object value) throws BackingStoreException {
		if (value == null)
			throw new NullPointerException("value can not be null");
		if (value.getClass() == byte[].class)
			throw new ToDoException("implement base64 ecnoding for byte[]");
		assertValidPath(pathDotKey);
		String nodePath = SH.beforeLast(pathDotKey, '.', "");
		String keyName = SH.afterLast(pathDotKey, '.', pathDotKey);
		Preferences node = root.node(SH.replaceAll(nodePath, '.', '/'));
		String r = getValueIfExists(node, keyName);
		node.put(keyName, Caster_String.INSTANCE.cast(value));
		root.flush();
		return r;
	}
	static public <T> T getNestedValueIfExists(String pathDotKey, Caster<T> caster) throws BackingStoreException {
		if (caster.getCastToClass() == byte[].class)
			throw new ToDoException("implement base64 ecnoding for byte[]");
		return caster.cast(getNestedValueIfExists(pathDotKey));
	}
	static public <T> T getNestedValueIfExists(Preferences root, String pathDotKey, Caster<T> caster) throws BackingStoreException {
		if (caster.getCastToClass() == byte[].class)
			throw new ToDoException("implement base64 ecnoding for byte[]");
		return caster.cast(getNestedValueIfExists(root, pathDotKey));
	}
	static public String getNestedValueIfExists(String pathDotKey) throws BackingStoreException {
		String remaining = SH.afterFirst(pathDotKey, '.', null);
		return getNestedValueIfExists(getRootForPath(pathDotKey, false), remaining);
	}
	static public String getNestedValueIfExists(Preferences root, String pathDotKey) throws BackingStoreException {
		if (root == null)
			return null;
		assertValidPath(pathDotKey);
		String nodePath = SH.beforeLast(pathDotKey, '.', "");
		String keyName = SH.afterLast(pathDotKey, '.', pathDotKey);
		Preferences node = getChildIfExists(root, nodePath);
		if (node == null)
			return null;
		return getValueIfExists(node, keyName);
	}
	public static boolean removeNested(String pathDotKey) throws BackingStoreException {
		String remaining = SH.afterFirst(pathDotKey, '.', null);
		return removeNested(getRootForPath(pathDotKey, false), remaining);
	}
	public static boolean removeNested(Preferences root, String pathDotKey) throws BackingStoreException {
		assertValidPath(pathDotKey);
		if (root == null)
			return false;
		String nodePath = SH.beforeLast(pathDotKey, '.', "");
		String keyName = SH.afterLast(pathDotKey, '.', pathDotKey);
		Preferences node = getChildIfExists(root, nodePath);
		if (node == null)
			return false;
		else if (getValueIfExists(node, keyName) != null) {
			node.remove(keyName);
			return true;
		} else if (node.nodeExists(keyName)) {
			node.node(keyName).removeNode();
			return true;
		} else
			return false;
	}
	private static void assertValidPath(String path) {
		if (path.indexOf('/') != -1)
			throw new IllegalArgumentException("foward slash (/) not allowed in path: " + path);

	}

}
