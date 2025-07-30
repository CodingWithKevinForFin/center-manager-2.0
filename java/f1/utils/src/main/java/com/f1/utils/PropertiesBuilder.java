/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.f1.base.Decrypter;
import com.f1.utils.impl.BasicPropertyController;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.Tuple2;

public class PropertiesBuilder {
	private static final Logger log = Logger.getLogger(PropertiesBuilder.class.getName());
	public static final String DIRECTIVE_INCLUDE_RESOURCE = "#INCLUDE_RESOURCE ";
	public static final String DIRECTIVE_INCLUDE_PREFS = "#INCLUDE_PREFS ";
	public static final String DIRECTIVE_INCLUDE = "#INCLUDE ";
	public static final String DIRECTIVE_XINCLUDE = "#INCLUDE_FROM_ROOT ";
	public static final String DIRECTIVE_EXPORT_SYSTEM = "#EXPORT_SYSTEM ";
	public static final String DIRECTIVE_EXPORT_PREF = "#EXPORT_PREF ";
	public static final String DIRECTIVE_UNDEFINE = "#UNDEFINE ";
	private List<Tuple2<String, String>> systemExports = new ArrayList<Tuple2<String, String>>();
	private List<Tuple2<String, String>> prefExports = new ArrayList<Tuple2<String, String>>();

	private SearchPath rootDirectories = new SearchPath();

	private Properties properties = new Properties();
	private Map<String, String> envProperties = new HashMap<String, String>();
	PropertiesHelper propertiesHelper = new PropertiesHelper();

	public PropertiesBuilder() {
		propertiesHelper.getEnvProperties(this.envProperties, this.sources);
	}

	public void readFile(String fileName) {
		if (rootDirectories == null)
			throw new IllegalStateException("call setRootDirectory() first");
		File file = searchForFile(fileName);
		loadFile(file);
	}

	public File searchForFile(String fileNames) {
		fileNames = propertiesHelper.resolveProperty(this.envProperties, properties, fileNames, fileNames, new Stack<String>(), new HashSet<String>());
		File file = rootDirectories.searchFirst(fileNames);
		if (file == null)
			LH.warning(log, "File not found(in order of search path): ", SH.join(File.pathSeparatorChar, rootDirectories.createSearchListAbsolute(fileNames)));
		return file;

	}

	private Set<String> loadingFiles = new HashSet<String>();

	private void loadFile(File file) {
		if (file == null)
			return;
		if (!file.exists()) {
			LH.warning(log, "File not found, skipping: ", file);
		} else {
			String path = IOH.getFullPath(file);
			LH.info(log, "Loading Properties File: ", path);
			if (!loadingFiles.add(path))
				throw new RuntimeException("Recursive #INCLUDE directive for file: " + path);
			try {
				loadStream(false, file.getParent(), Property.TYPE_FILE, IOH.getFullPath(file), new FileReader(file));
			} catch (Exception e) {
				throw new RuntimeException("error with file " + IOH.getFullPath(file), e);
			} finally {
				loadingFiles.remove(path);
			}
		}
	}

	public void loadResource(String file) {
		if (file == null)
			return;
		String text;
		try {

			text = IOH.readTextFromResource(file);
		} catch (Exception e) {
			LH.warning(log, "Resource not found, skipping: ", file);
			return;
		}
		LH.info(log, "Loading Properties Resource: ", file);
		try {
			loadStream(true, SH.beforeLast(SH.beforeLast(file, "\\"), "/"), Property.TYPE_RESOURCE, file, new StringReader(text));
		} catch (Exception e) {
			throw new RuntimeException("error with resource: " + file, e);
		}
	}

	public void loadPreferences(String prefix) {
		try {
			boolean found = false;
			{
				String value = PreferencesHelper.getNestedValueIfExists(prefix);
				if (value != null) {
					sources.putMulti(prefix, new Property(prefix, value, Property.TYPE_PREFERENCE));
					applyProperty(prefix, value, properties);
					found = true;
				}
			}
			Preferences child = PreferencesHelper.getChildIfExists(prefix);
			if (child != null) {
				found = true;
				loadPreferences(child, prefix);
			}
			if (!found) {
				PreferencesHelper.getRootForPath(prefix, true);// ensure a valid path!
				LH.warning(log, "Preferences not found, skipping: ", prefix);
			}
		} catch (BackingStoreException e) {
			throw new RuntimeException("error reading preferences with prefix: " + prefix, e);
		}
	}

	private void loadPreferences(Preferences node, String prefix) throws BackingStoreException {
		if (node != null) {
			for (String name : node.keys()) {
				String key = prefix + '.' + name;
				String value = node.get(name, null);
				if (value != null) {
					sources.putMulti(key, new Property(key, value, Property.TYPE_PREFERENCE));
					applyProperty(key, value, properties);
				}

			}
			for (String name : node.childrenNames())
				loadPreferences(node.node(name), prefix + '.' + name);
		}
	}

	private void loadStream(boolean isResource, String parent, byte propertyType, String description, Reader stream) throws IOException {
		StringBuilder sb = new StringBuilder();
		Properties p = new Properties();
		LineNumberReader reader = new LineNumberReader(stream);
		String line;
		String namespace = "";
		while ((line = reader.readLine()) != null) {
			final String trimLine = line.trim();
			if (trimLine.startsWith("[") && trimLine.endsWith("]")) {
				namespace = SH.strip(trimLine, "[", "]", true);
			} else if (trimLine.startsWith(DIRECTIVE_INCLUDE)) {
				final String fileName = stripAndResolve(trimLine, DIRECTIVE_INCLUDE);
				if (isResource)
					loadResource(SH.path(File.separatorChar, parent, fileName));
				else {
					File f = IOH.isAbsolutePath(fileName) ? new File(fileName) : new File(parent, fileName);
					loadFile(f);
				}
			} else if (trimLine.startsWith(DIRECTIVE_XINCLUDE)) {
				loadFile(searchForFile(stripAndResolve(trimLine, DIRECTIVE_XINCLUDE)));
			} else if (trimLine.startsWith(DIRECTIVE_EXPORT_SYSTEM)) {
				addSystemExport(stripAndResolve(trimLine, DIRECTIVE_EXPORT_SYSTEM));
			} else if (trimLine.startsWith(DIRECTIVE_EXPORT_PREF)) {
				addPrefExport(stripAndResolve(trimLine, DIRECTIVE_EXPORT_PREF));
			} else if (trimLine.startsWith(DIRECTIVE_INCLUDE_PREFS)) {
				loadPreferences(stripAndResolve(trimLine, DIRECTIVE_INCLUDE_PREFS));
			} else if (trimLine.startsWith(DIRECTIVE_INCLUDE_RESOURCE)) {
				loadResource(stripAndResolve(trimLine, DIRECTIVE_INCLUDE_RESOURCE));
			} else if (trimLine.startsWith(DIRECTIVE_UNDEFINE)) {
				properties.remove(SH.stripPrefix(trimLine, DIRECTIVE_UNDEFINE, true).trim());
			} else if (!trimLine.isEmpty()) {
				int lineNumber = reader.getLineNumber();
				SH.clear(sb).append(namespace).append(line).append(SH.NEWLINE);
				while (line.endsWith("\\")) {
					line = reader.readLine();
					if (line == null)
						throw new EOFException("unexpected end of file, last line ended with '\\'");
					sb.append(line).append(SH.NEWLINE);
				}
				p.clear();
				p.load(new StringReader(SH.toStringAndClear(sb)));
				applyProperties(p, properties);
				for (Object key : p.keySet()) {
					String k = key.toString();
					sources.putMulti(k, new Property(k, SH.toString(properties.get(key)), propertyType, description, lineNumber, false));
				}
			}
		}
	}

	private String stripAndResolve(String line, String prefix) {
		return resolve(SH.stripPrefix(line, prefix, true).trim());
	}

	private String resolve(String value) {
		Set<String> secureKeys = new HashSet<String>();
		String r = propertiesHelper.resolveProperty(this.envProperties, properties, null, value, new Stack(), secureKeys);
		if (!secureKeys.isEmpty())
			throw new RuntimeException("Special instructions can not contain secure keys: " + value);
		return r;
	}

	private void addSystemExport(String text) {
		int split = text.indexOf('=');
		if (split == -1)
			throw new RuntimeException("format not " + DIRECTIVE_EXPORT_SYSTEM + " key=value: " + text);
		systemExports.add(new Tuple2<String, String>(text.substring(0, split).trim(), text.substring(split + 1).trim()));
	}

	private void addPrefExport(String text) {
		int split = text.indexOf('=');
		if (split == -1)
			throw new RuntimeException("format not " + DIRECTIVE_EXPORT_PREF + " key=value: " + text);
		prefExports.add(new Tuple2<String, String>(text.substring(0, split).trim(), text.substring(split + 1).trim()));
	}

	private void applyProperties(Properties src, Properties dest) {
		for (Entry<Object, Object> e : src.entrySet()) {
			e.setValue(propertiesHelper.substituteBackReferences(e.getKey().toString(), e.getValue().toString(), dest));
		}
		dest.putAll(src);
	}

	private void applyProperty(String key, String value, Properties dest) {
		if (value == null)
			throw new NullPointerException("for key: " + key);
		dest.put(key, propertiesHelper.substituteBackReferences(key, value, dest));
	}

	private final BasicMultiMap.List<String, Property> sources = new BasicMultiMap.List<String, Property>();

	public PropertyController resolveProperties(boolean allowMissingRef) {
		BasicMultiMap.List<String, Property> sources2 = new BasicMultiMap.List<String, Property>();
		sources2.putAll(sources);
		Properties r = new Properties();
		r.putAll(this.properties);
		r.putAll(this.envProperties);
		Set<String> secureKeys = new HashSet<String>();
		r = propertiesHelper.resolveProperties(this.envProperties, r, allowMissingRef, secureKeys);
		if (!allowMissingRef) {//this is the final run
			for (Tuple2<String, String> e : systemExports) {
				String key = e.getA();
				String value = propertiesHelper.resolveProperty(this.envProperties, r, e.getB(), e.getB(), new Stack(), secureKeys);
				System.setProperty(key, value);
				if (r.containsKey(key)) {
					r.put(key, value);
				}

			}
			for (Tuple2<String, String> e : prefExports) {
				String key = e.getA();
				String value = propertiesHelper.resolveProperty(this.envProperties, r, e.getB(), e.getB(), new Stack(), secureKeys);
				try {
					if (SH.isnt(value))
						PreferencesHelper.removeNested(key);
					else
						PreferencesHelper.setNestedValue(key, value);
					if (r.containsKey(key)) {
						if (value == null)
							r.remove(key);
						else
							r.put(key, value);
					}
				} catch (BackingStoreException e1) {
					throw OH.toRuntime(e1);
				}
			}
		}
		for (Entry<Object, Object> i : r.entrySet()) {
			String key = SH.toString(i.getKey());
			String value = SH.toString(i.getValue());
			List<Property> l = sources2.get(key);
			if (l != null) {
				Property t = l.get(0);
				l.set(0, new Property(t.getKey(), value, t.getSourceType(), t.getSource(), t.getSourceLineNumber(), secureKeys.contains(t.getKey())));
			}
		}
		return new BasicPropertyController(r, sources2);
	}

	public void readProperties(Properties p) {
		applyProperties(p, properties);
	}

	public void setProperty(String key, String value) {
		StackTraceElement ste = getSTE();
		sources.putMulti(key,
				new Property(key, value, Property.TYPE_CODE, ste == null ? "" : ste.getFileName(), ste == null ? Property.NO_LINE_NUMBER : ste.getLineNumber(), false));
		applyProperty(key, value, properties);
	}

	private static String IGNORE_PACKAGES[] = new String[] { PropertiesBuilder.class.getPackage().getName(), "com.f1.bootstrap", Thread.class.getPackage().getName() };

	private StackTraceElement getSTE() {

		StackTraceElement[] stes = Thread.currentThread().getStackTrace();
		OUTER: for (StackTraceElement ste : stes) {
			for (String p : IGNORE_PACKAGES) {
				if (ste.getClassName().startsWith(p))
					continue OUTER;
			}
			return ste;
		}
		return null;
	}

	public void setRootDirectorySearchPath(File[] files) {
		rootDirectories.addDirectories(files);
	}

	public void setRootDirectorySearchPath(String path) {
		rootDirectories.addDirectories(path);
	}

	public SearchPath getRootDirectorySearchPath() {
		return rootDirectories;
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	public void readPropertiesNoOverride(Properties p) {
		Properties p2 = new Properties();
		for (Object key : p.keySet()) {
			if (!properties.containsKey(key))
				p2.put(key, p.get(key));
		}
		readProperties(p2);
	}

	public void readProperties(String text) {
		StringReader sr = null;
		try {
			final Properties props = new Properties();
			props.load(sr = new StringReader(text));
			readProperties(props);
		} catch (IOException e) {
			throw OH.toRuntime(e);
		} finally {
			IOH.close(sr);
		}
	}

	public String toString() {
		return new TreeMap(this.properties).toString();
	}

	public void addDecrypter(String decname, Decrypter aesEncrypter) {
		this.propertiesHelper.addDecrypter(decname, aesEncrypter);
	}

	public String getEnvProperty(String propertySecretKeyFiles) {
		return this.envProperties.get(propertySecretKeyFiles);
	}
}
