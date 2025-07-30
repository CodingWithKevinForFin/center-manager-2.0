package com.f1.utils.jsonmap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.base.Getter;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

public class JsonMapHelper {
	public final static NestedGetterSingle INSTANCE_JSON_NESTED_GETTER_SINGLE = new NestedGetterSingle("", '.');

	public static class NestedGetterSingle implements Getter<Object, Object> {
		private static Logger log = LH.get();
		//TODO:Slice lists

		// support getting values 
		// a.b.c key a, key b, key c
		// a.1.c key a, first elem if list, if map key 1, key c
		// a.1:4.c key a, first elem if list, if map key 1, key c
		// a,b,c.1:4
		// a,b,c.1,3:5,7
		// a,b.1,2,3
		// a,b.1,2,3.1,2
		//		{
		//			a [1,2,3],
		//			b [1,2,3]
		//		}
		private String fullPath;
		private String pathParts[];
		//		private char multValuesChar; // ie a,c,d or 1,4,5
		//		private char listSliceChar; // ie 1:4 ... 1,2,3,4 or : or 1: or : 
		// 1,3,7:5 -> 1 .. 3 .. 7:5 reverse

		public NestedGetterSingle(String fullPath, char delim) {
			//			this.multValuesChar = ',';
			//			this.listSliceChar = ':';
			this.fullPath = fullPath;
			OH.assertNe(this.fullPath, null);
			// valid key: space, empty string etc..
			this.pathParts = SH.split(delim, fullPath);
		}
		private static String[] getParts(String fullPath, char delim) {
			return SH.split(delim, fullPath);
		}

		// Alternative getter
		public Object getAlt(Object doc, String[] pathParts) {
			if (pathParts.length == 0)
				return doc;
			int last = pathParts.length - 1;
			Object currVal = doc;
			int idx = 0;
			while (idx < last && currVal != null) {
				String currKey = pathParts[idx];
				currVal = getObjectSimpleNoThrow(currVal, currKey);
				idx++;
			}

			// Handle last
			if (currVal != null) {
				String currKey = pathParts[idx];
				currVal = getObjectSimpleNoThrow(currVal, currKey);
			}
			return currVal;
		}

		// Alternative getter
		public Object getAlt(Object doc, String path, char delim) {
			return this.getAlt(doc, getParts(path, delim));
		}

		@Override
		public Object get(Object doc) {
			return this.getAlt(doc, this.pathParts);
		}

	}

	private static Object getObjectFromMapNoThrow(Object doc, String key) {
		Map m = (Map) doc;
		if (m == null)
			return null;
		return m.get(key);
	}
	private static Object getObjectFromListNoThrow(Object doc, String key) {
		List l = (List<Object>) doc;
		if (l == null)
			return null;
		int sz = l.size();
		if (sz == 0)
			return null;

		Integer idx = Caster_Integer.INSTANCE.castNoThrow(key);
		if (idx == null || idx >= sz)
			return null;
		if (idx < 0)
			idx = sz + idx;
		if (idx < 0)
			return null;
		return l.get(idx);
	}

	// Get a child value from an object whether it's a map or list based on a string key
	private static Object getObjectSimpleNoThrow(Object doc, String key) {
		if (doc == null)
			return null;
		if (doc instanceof Map) {
			return getObjectFromMapNoThrow(doc, key);
		} else if (doc instanceof List) {
			return getObjectFromListNoThrow(doc, key);
		} else
			return null;
	}

	private static Object getObjectFromListMultNoThrow(Object doc, String key, char multDelim) {
		List l = (List<Object>) doc;
		if (l == null) //E?
			return null;
		int sz = l.size();
		if (sz == 0) //E?
			return null;

		if (SH.indexOf(key, multDelim, 0, key.length()) == -1) {
			Integer idx = Caster_Integer.INSTANCE.castNoThrow(key);
			if (idx == null || idx >= sz) //E?
				return null;
			if (idx < 0)
				idx = sz + idx;
			if (idx < 0) //E?
				return null;
			return l.get(idx);
		} else {
			String[] keyParts = SH.split(multDelim, key);
			List<Object> ret = new ArrayList<Object>();
			for (int i = 0; i < keyParts.length; i++) {
				String currKey = keyParts[i];
				Integer idx = Caster_Integer.INSTANCE.castNoThrow(currKey);

				if (idx == null || idx >= sz) { //E?
					ret.add(null); // TODO: should we not add it? if we can't find it or do we add null;
					continue;
				}
				if (idx < 0)
					idx = sz + idx;
				if (idx < 0) { //E?
					ret.add(null); // TODO: should we not add it? if we can't find it or do we add null;
					continue;
				}
				ret.add(l.get(idx));
			}
			return ret;
		}
	}
	// Can return multiple values
	private static Object getObjectFromMapMultNoThrow(Object doc, String key, char multDelim) {
		Map m = (Map) doc;
		if (m == null)
			return null;
		//		System.out.println("key : " + key + " " + doc);
		//		System.out.println(SH.indexOf(key, multDelim, 0, key.length()));
		if (SH.indexOf(key, multDelim, 0, key.length()) == -1)
			return m.get(key);
		else {
			String[] keyParts = SH.split(multDelim, key);
			Map<String, Object> ret = new LinkedHashMap<String, Object>();
			for (int i = 0; i < keyParts.length; i++) {
				String currKey = keyParts[i];
				ret.put(currKey, m.get(currKey));
			}
			return ret;
		}
	}

	// TODO: Use later for json.
	/*
	private static Object getObjectMultNoThrow(Object doc, String key, char multDelim) {
		if (doc == null)
			return null;
		if (doc instanceof Map) {
			return getObjectFromMapMultNoThrow(doc, key, multDelim);
		} else if (doc instanceof List) {
			return getObjectFromListMultNoThrow(doc, key, multDelim);
		} else
			return null;
	}
	*/

	public static void main(String[] args) {
		String json = "{\"menu\": { \"id\": \"file\", \"value\": \"File\", \"popup\": { \"menuitem\": [ {\"value\": \"New\", \"onclick\": \"CreateNewDoc()\"}, {\"value\": \"Open\", \"onclick\": \"OpenDoc()\"}, {\"value\": \"Close\", \"onclick\": \"CloseDoc()\"} ] } }}";
		Object doc = ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject(json);

		NestedGetterSingle getter = JsonMapHelper.INSTANCE_JSON_NESTED_GETTER_SINGLE;

		System.out.println(getter.getAlt(doc, "menu.popup.menuitem.-1", '.'));

	}
}
