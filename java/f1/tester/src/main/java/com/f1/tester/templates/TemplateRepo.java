/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.tester.templates;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.f1.tester.TestH;
import com.f1.tester.json.TestingToJsonConverter;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.CachedFile;
import com.f1.utils.CachedFile.Cache;
import com.f1.utils.OH;
import com.f1.utils.RH;
import com.f1.utils.SH;
import com.f1.utils.StringFormatException;
import com.f1.utils.ToDoException;
import com.f1.utils.converter.json2.MapToJsonConverter;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.impl.SimpleObjectGeneratorForClass;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.MultiMap;

/**
 * repository of templates.
 */
public class TemplateRepo {
	public static final Logger log = Logger.getLogger(TemplateRepo.class.getName());

	public static final char KEY_DELIM = '.';
	private Map<String, Object> messages = new LinkedHashMap<String, Object>();
	private TestingToJsonConverter converter = new TestingToJsonConverter();
	private long checkFrequencyMs;
	private MultiMap<String, CachedFile.Cache, List<CachedFile.Cache>> fileCaches = new BasicMultiMap<String, CachedFile.Cache, List<Cache>>(ArrayList.class);
	private MapGenerator mapGenerator = new MapGenerator();
	private Map<String, Object> variables = new HashMap<String, Object>();
	private List<String> imports = new ArrayList<String>();

	public TemplateRepo() {
		MapToJsonConverter mapToJsonConverter = (MapToJsonConverter) converter.getConverter(Map.class);
		addImport("java.lang.*");
		addImport(TestH.class.getName());
	}

	public void reset() {
		messages.clear();
		variables.clear();
	}

	public void putVariableInitialValue(String name, Object value) {
		variables.put(name, value);
	}

	public Map<String, Object> getVariableInitialValues() {
		return variables;
	}

	public List<String> getImports() {
		return imports;
	}

	public Object getVariableInitialValue(String name) {
		return variables.get(name);
	}

	public void addImport(String importPath) {
		imports.add(importPath);
	}

	private boolean isMapAlphabetical = true;

	public void mergeMap(String mPath, Map<String, Object> value, boolean recurse, boolean allowReplace) {
		String[] parts = splitToParts(mPath);
		Object obj = messages;
		for (int i = 0; i < parts.length; i++)
			obj = traverse(parts, i, obj, true);
		Map<String, Object> map = (Map<String, Object>) obj;
		merge(map, value, recurse, allowReplace);
	}

	private void merge(Map<String, Object> existing, Map<String, Object> values, boolean recurse, boolean allowReplace) {
		for (Map.Entry<String, Object> e : values.entrySet()) {
			if (recurse && e.getValue() instanceof Map && existing.get(e.getKey()) instanceof Map) {
				merge((Map<String, Object>) existing.get(e.getKey()), (Map<String, Object>) e.getValue(), true, allowReplace);
			} else if (allowReplace)
				existing.put(e.getKey(), e.getValue());
			else
				CH.putOrThrow(existing, e.getKey(), e.getValue());
		}
	}

	public void put(String mPath, Object value) {
		put(mPath, value, false);
	}

	public void put(String mPath, Object value, boolean allowReplace) {
		String[] parts = splitToParts(mPath);
		Object obj = messages;
		for (int i = 0; i < parts.length - 1; i++) {
			obj = traverse(parts, i, obj, true);
		}
		Map<String, Object> map = (Map<String, Object>) obj;
		if (allowReplace)
			map.put(AH.last(parts), value);
		else
			CH.putOrThrow(map, AH.last(parts), value);
	}

	public Object get(String mPath) {
		checkFileCache(mPath);
		if (mPath.length() == 0)
			return messages;
		String[] parts = splitToParts(mPath);
		Object obj = messages;
		for (int i = 0; i < parts.length; i++)
			obj = traverse(parts, i, obj, i + 1 < parts.length);
		return obj;
	}

	private Object traverse(String parts[], int offset, Object parent, boolean autoCreate) {
		String key = parts[offset];
		if (parent instanceof List) {
			List<Object> list = (List<Object>) parent;
			if (RH.LENGTH.equals(key))
				return list.size();
			int index = SH.parseInt(key);
			if (autoCreate)
				while (list.size() <= index)
					list.add(null);
			Object r = list.get(index);
			if (autoCreate && r == null)
				list.set(index, r = newMap());
			return r;
		}
		if (parent instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) parent;
			Object r = map.get(key);
			if (autoCreate && r == null)
				map.put(key, r = newMap());
			return r;
		}
		throw new RuntimeException("not a map or list at " + SH.joinSub(KEY_DELIM, 0, offset + 1, parts));
	}

	private void checkFileCache(String mPath) {
		if (fileCaches.size() == 0)
			return;
		for (Map.Entry<String, List<CachedFile.Cache>> f : fileCaches.entrySet())
			if (f.getKey().startsWith(mPath) || mPath.startsWith(f.getKey())) {
				List<CachedFile.Cache> old = new ArrayList<CachedFile.Cache>();
				for (CachedFile.Cache cache : f.getValue())
					if (cache.isOld())
						old.add(cache);
				for (CachedFile.Cache c : old) {
					Cache c2 = c.getUpdated();
					try {
						Map<String, Object> map = (Map<String, Object>) converter.bytes2Object(c.getBytes());
						removeMap(f.getKey(), map);
					} catch (Exception e) {
					}
					try {
						fileCaches.removeMulti(f.getKey(), c);
						fileCaches.putMulti(f.getKey(), c2);
						Map<String, Object> map2 = (Map<String, Object>) converter.bytes2Object(c2.getBytes());
						mergeMap(f.getKey(), map2, true, true);
					} catch (Exception e) {
						LH.warning( log , "Error while replacing cached element " + f.getKey(), e);
					}
				}
			}
	}

	private void removeMap(String mPath, Map<String, Object> toRemove) {
		String[] parts = splitToParts(mPath);
		Map<String, Object> map = messages;
		for (int i = 0; i < parts.length; i++) {
			Object obj = map.get(parts[i]);
			if (obj == null)
				map.put(parts[i], obj = newMap());
			else if (!(obj instanceof Map))
				throw new RuntimeException("not a map at " + SH.joinSub(KEY_DELIM, 0, i + 1, parts));
			map = (Map<String, Object>) obj;
		}
		remove(map, toRemove);
	}

	public Map<String, Object> newMap() {
		return mapGenerator.nw();
	}

	// TODO: support lists
	private void remove(Map<String, Object> map, Map<String, Object> toRemove) {
		List<String> toRemoveKeys = new ArrayList<String>();
		for (Map.Entry<String, Object> e : toRemove.entrySet()) {
			Object val = map.get(e.getKey());
			if (val instanceof Map && e.getValue() instanceof Map) {
				remove((Map<String, Object>) val, (Map<String, Object>) e.getValue());
				if (((Map) val).isEmpty())
					toRemoveKeys.add(e.getKey());
			} else if (val instanceof List && e.getValue() instanceof List)
				throw new ToDoException();
			else if (OH.eq(val, e.getValue()))
				toRemoveKeys.add(e.getKey());
			else
				System.out.println("values not equal for " + e.getKey() + "   " + val + "   " + e.getValue());
		}
		for (String key : toRemoveKeys)
			map.remove(key);
	}

	public void putJson(String mPath, String json) {
		try {
			Object o = converter.bytes2Object(json.getBytes());
			if (o instanceof Map) {
				Map<String, Object> map = (Map<String, Object>) o;
				mergeMap(mPath, map, true, false);
			} else
				put(mPath, o);
		} catch (StringFormatException e) {
		LH.info(log,e.toLegibleString());
			throw e;
		}
	}

	public void putFile(String mPath, File location) {
		try {
			Cache cache = new CachedFile(location, checkFrequencyMs).getData();
			if (!cache.exists())
				throw new RuntimeException("File not found: " + location);
			Map<String, Object> map = (Map<String, Object>) converter.bytes2Object(cache.getBytes());
			mergeMap(mPath, map, true, true);
			fileCaches.putMulti(mPath, cache);
		} catch (StringFormatException e) {
		LH.info(log,e.toLegibleString());
			throw new RuntimeException("Error Loading file (see logs for details): " + location, e);
		} catch (RuntimeException e) {
			throw new RuntimeException("Error Loading file: " + location, e);
		}
	}

	final private String[] splitToParts(String key) {
		return SH.split(KEY_DELIM, key);
	}

	public ObjectToJsonConverter getConverter() {
		return converter;
	}

	public String toJson(Object o) {
		return new String(converter.object2Bytes(o));
	}

	public String toJson() {

		return toJson(get(""));
	}

	public void setMapAlphabetical(boolean isMapAlphabetical) {
		this.isMapAlphabetical = isMapAlphabetical;
	}

	public boolean isMapAlphabetical() {
		return isMapAlphabetical;
	}

	public class MapGenerator extends SimpleObjectGeneratorForClass<Map<String, Object>> {

		public MapGenerator() {
			super((Class) Map.class);
		}

		@Override
		public Map<String, Object> nw() {
			if (isMapAlphabetical)
				return new TreeMap<String, Object>();
			else
				return new LinkedHashMap<String, Object>();
		}

	}
}
