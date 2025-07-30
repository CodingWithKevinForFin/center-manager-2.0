/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.prefs.BackingStoreException;

import com.f1.base.Caster;
import com.f1.base.Decrypter;
import com.f1.utils.structs.BasicMultiMap;

public class PropertiesHelper {

	private CopyOnWriteHashMap<String, Decrypter> decrypters = new CopyOnWriteHashMap<String, Decrypter>();

	public void addDecrypter(String enckey, Decrypter encrypter) {
		System.out.println("REGISTERED DECRYPTED '" + enckey + "' OF TYPE " + encrypter);
		if (enckey == null)
			enckey = "";
		this.decrypters.put(enckey, encrypter);
	}

	public String decrypt(String prop, String enckey, String ciphertext) {
		if (enckey == null) {
			System.err.println("WARNING: PROPERTY " + prop + " IS USING LEGACY SYNTAX, SHOULD BE: ${CIPHER:DecrypterName:ciphertext} instead of ${CIPHER:ciphertext}");
			enckey = "";
		}
		if (this.decrypters.size() == 0)
			throw new RuntimeException("No encrypters registered");
		ciphertext = SH.trim(ciphertext);
		Decrypter i = CH.getOr(this.decrypters, enckey, null);
		if (i == null)
			throw new RuntimeException("For Property '" + prop + "' Could not find secret key or decoder for given ciphertext key: '" + enckey + "'");
		try {
			return i.decryptString(ciphertext);
		} catch (Exception e) {
			throw new RuntimeException("Failed to decypher property: " + prop, e);
		}
	}

	public String resolveProperty(Map<String, String> envProperties, Properties src, String key, String value, Stack<String> stack, Set<String> secureKeysSink) {
		return resolveProperty(envProperties, src, key, value, stack, false, secureKeysSink);
	}
	private String resolveProperty(Map<String, String> envProperties, Properties src, String key, String value, Stack<String> stack, boolean allowMissingRef,
			Set<String> secureKeysSink) {
		try {
			if (key != null)
				stack.push(key);
			StringBuilder sb = new StringBuilder();
			int i, j = 0;
			while ((i = value.indexOf("${", j)) != -1) {

				sb.append(value, j, i);
				j = getClosing(key, value, i + 2) + 1;
				String backRef = value.substring(i + 2, j - 1);
				if (backRef.startsWith("CIPHER:")) {
					String cipher = SH.stripPrefix(backRef, "CIPHER:", true);
					int n = cipher.indexOf(':');
					String data;
					if (n == -1)
						data = decrypt(key, null, cipher);
					else
						data = decrypt(key, cipher.substring(0, n), cipher.substring(n + 1));
					sb.append(data);
					secureKeysSink.addAll(stack);
					continue;
				}
				backRef = resolveProperty(envProperties, src, key, backRef, stack, allowMissingRef, secureKeysSink);

				if (backRef.indexOf('|') != -1) {
					for (String t : SH.split('|', backRef)) {
						if (src.getProperty(t) != null) {
							backRef = t;
							break;
						}
					}
				}

				String replacement = envProperties.get(backRef);
				if (replacement == null)
					replacement = src.getProperty(backRef);
				if (replacement == null) {
					if (allowMissingRef)
						replacement = backRef;
					else
						throwBecauseOfMissing(backRef);
				}
				if (stack.contains(backRef))
					throw new RuntimeException("Circular reference : " + SH.join(" ==> ", stack.subList(stack.indexOf(backRef), stack.size())) + " ==> " + backRef);
				sb.append(resolveProperty(envProperties, src, backRef, replacement, stack, allowMissingRef, secureKeysSink));
			}
			sb.append(value, j, value.length());

			if (key != null)
				stack.pop();
			return sb.toString();
		} catch (Exception e) {
			if (key == null)
				throw new RuntimeException("While resolving directive: " + value, e);
			else
				throw new RuntimeException("While resolving: " + key + "=" + value, e);
		}
	}

	private void throwBecauseOfMissing(String key) {
		String val = System.getProperty(key);
		if (SH.is(val))
			throw new RuntimeException(
					"property '" + key + "' not found, but exists as a system property, perhaps you mean: -D" + F1GlobalProperties.getSysPropertyPrefix() + key + "=" + val);
		val = System.getenv(key);
		if (SH.is(val))
			throw new RuntimeException("property '" + key + "' not found, but exists as an environment property, perhaps you mean: EXPORT "
					+ F1GlobalProperties.getEnvPropertyPrefix() + key + "=" + val);
		try {
			val = PreferencesHelper.getNestedValueIfExists(key);
		} catch (BackingStoreException e) {
		}
		if (val != null)
			throw new RuntimeException("property '" + key + "' not found but exists as a preference, perhaps you should add to property file: "
					+ PropertiesBuilder.DIRECTIVE_INCLUDE_PREFS + " " + key);
		throw new RuntimeException("property '" + key + "' not found");

	}
	private int getClosing(String key, String value, int i) {
		do {
			int e = SH.indexOfFirst(value, i, "${", "}");
			if (e == -1) {
				if (key == null)
					throw new RuntimeException("missing } in value: " + value);
				else
					throw new RuntimeException("missing } in property: " + key + "=" + value);
			} else if (value.charAt(e) == '}')
				return e;
			i = getClosing(key, value, e + 2) + 1;
		} while (true);
	}

	public void getEnvProperties(Map<String, String> r, BasicMultiMap.List<String, Property> sources) {
		for (Object o : System.getenv().keySet()) {
			String key = o.toString();
			if (!key.startsWith(F1GlobalProperties.getEnvPropertyPrefix()))
				continue;
			String value = System.getenv(key), k;
			r.put(k = SH.replaceAll(SH.stripPrefix(key, F1GlobalProperties.getEnvPropertyPrefix(), true), '_', '.'), value);
			if (sources != null)
				sources.putMulti(k, new Property(k, value, Property.TYPE_SYSTEM_ENV, key, Property.NO_LINE_NUMBER, false));
		}
		for (Object o : System.getProperties().keySet()) {
			String key = o.toString();
			if (!key.startsWith(F1GlobalProperties.getSysPropertyPrefix()))
				continue;
			String value = System.getProperty(key), k;
			r.put(k = SH.stripPrefix(key, F1GlobalProperties.getSysPropertyPrefix(), true), value);
			if (sources != null)
				sources.putMulti(k, new Property(k, value, Property.TYPE_SYSTEM_PROPERTY, key, Property.NO_LINE_NUMBER, false));
		}
	}
	public Properties replaceWithEnvProperties(Properties src, BasicMultiMap.List<String, Property> sources) {
		Properties r = new Properties();
		if (src != null)
			r.putAll(src);
		for (Object o : System.getenv().keySet()) {
			String key = o.toString();
			if (!key.startsWith(F1GlobalProperties.getEnvPropertyPrefix()))
				continue;
			String value = System.getenv(key), k;
			r.put(k = SH.replaceAll(SH.stripPrefix(key, F1GlobalProperties.getEnvPropertyPrefix(), true), '_', '.'), value);
			if (sources != null)
				sources.putMulti(k, new Property(k, value, Property.TYPE_SYSTEM_ENV, key, Property.NO_LINE_NUMBER, false));
		}
		for (Object o : System.getProperties().keySet()) {
			String key = o.toString();
			if (!key.startsWith(F1GlobalProperties.getSysPropertyPrefix()))
				continue;
			String value = System.getProperty(key), k;
			r.put(k = SH.stripPrefix(key, F1GlobalProperties.getSysPropertyPrefix(), true), value);
			if (sources != null)
				sources.putMulti(k, new Property(k, value, Property.TYPE_SYSTEM_PROPERTY, key, Property.NO_LINE_NUMBER, false));
		}

		return r;
	}

	public Properties resolveProperties(Map<String, String> envProperties, Properties src, boolean allowMissingRef, Set<String> secureKeysSink) {
		Properties r = new Properties();
		for (Map.Entry<Object, Object> o : src.entrySet()) {
			r.put(o.getKey(), resolveProperty(envProperties, src, o.getKey().toString(), o.getValue().toString(), new Stack<String>(), allowMissingRef, secureKeysSink));
		}
		return r;
	}

	public String substituteBackReferences(String key, String value, Properties src) {
		StringBuilder sb = new StringBuilder();
		int i, j = 0;
		while ((i = value.indexOf("$${", j)) != -1) {
			sb.append(value, j, i);
			j = value.indexOf("}", i) + 1;
			String backRef = value.substring(i + 3, j - 1);
			String replacement = src.getProperty(backRef);
			if (replacement == null)
				throw new RuntimeException("Back reference '" + backRef + "' not found for: " + key + "=" + value);
			sb.append(replacement);
		}
		sb.append(value, j, value.length());

		return sb.toString();
	}

	//Sink is populated with an alternating text,variable sequence. Sink will always have an odd number of elements added, such that  first and last elements will be text. In other words,
	//if supplied text starts w/ a variable then first entry in sink will be an empty string. Likewise, if supplied test ends w/ a variable then last entry will be an empty string. Also, tow consecutive variables
	//will have an empty string between them.
	static public void splitVariables(String text, List<String> sink) {
		splitVariables(text, sink, new StringBuilder());
	}
	static public void splitVariables(String text, List<String> sink, StringBuilder buf) {
		SH.clear(buf);
		for (int i = 0;; i++) {
			i = SH.unescapeUntil(text, i, SH.CHAR_BACKSLASH, '$', buf);
			if (i == -1) {
				sink.add(SH.toStringAndClear(buf));
				return;
			} else if (i + 1 < text.length() && text.charAt(i + 1) == '{') {
				sink.add(SH.toStringAndClear(buf));
				i = SH.unescapeUntil(text, i + 2, SH.CHAR_BACKSLASH, '}', buf);
				if (i == -1)
					throw new RuntimeException("missing closing '}': " + text);
				sink.add(SH.toStringAndClear(buf));
			} else {
				buf.append('$');
			}
		}
	}

	static public void addPropertiesWithoutNulls(Properties props, String namespace, Map<String, Object> m) {
		for (Entry<String, Object> e : m.entrySet()) {
			if (e.getValue() != null)
				props.put(namespace + e.getKey(), e.getValue());
		}
	}

	static public <T> T getRequiredForAny(PropertyController props, Caster<T> caster, String... options) {
		if (options.length == 0)
			throw new IllegalArgumentException("optiosn is empty");
		for (String option : options)
			if (props.getKeys().contains(option))
				return props.getRequired(option, caster);
		try {
			return props.getRequired(options[0], caster);
		} catch (DetailedException e) {
			if (options.length > 1)
				e.addKeyValues("Other keys checked", CH.l(AH.remove(options, 0)));
			throw e;
		}
	}

}
