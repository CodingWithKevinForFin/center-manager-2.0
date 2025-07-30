package com.f1.utils.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.f1.base.DoubleIterable;
import com.f1.base.DoubleIterator;
import com.f1.base.IntIterable;
import com.f1.base.IntIterator;
import com.f1.base.ToStringable;
import com.f1.utils.AH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

public class JsonBuilder implements ToStringable {

	private static final byte MAP = 1;
	private static final byte LIST = 2;
	private static final byte ENTRY_ADDED = 4;
	private static final byte KEY_ONLY = 8;
	private static final byte QUOTE = 16;

	private StringBuilder js;
	private boolean strict = true;
	private byte[] states = new byte[10];
	int depth = -1;
	private byte state = 0;
	private int jsStart;

	public JsonBuilder(StringBuilder js) {
		this.jsStart = js.length();
		this.js = js;
	}
	public JsonBuilder() {
		this.jsStart = 0;
		this.js = new StringBuilder();
	}

	public JsonBuilder startMap() {
		if (state == QUOTE)
			throw new IllegalStateException("attempting to start list when in quote");
		if (state == ENTRY_ADDED)
			throw new IllegalStateException("attempting to start list after json is complete");
		if (MH.anyBits(state, MAP) && !MH.anyBits(state, KEY_ONLY))
			throw new IllegalStateException("must apply a key when adding map to a map");
		if (MH.anyBits(state, ENTRY_ADDED) && !MH.anyBits(state, KEY_ONLY))
			js.append(',');
		else
			state |= ENTRY_ADDED;
		pushState(MAP);
		js.append('{');
		return this;
	}
	public JsonBuilder startList() {
		//Make sure not in map w/o entry
		if (state == QUOTE)
			throw new IllegalStateException("attempting to start list when in quote");
		if (state == ENTRY_ADDED)
			throw new IllegalStateException("attempting to start list after json is complete");
		if (MH.anyBits(state, ENTRY_ADDED) && !MH.anyBits(state, KEY_ONLY))
			js.append(',');
		else
			state |= ENTRY_ADDED;
		pushState(LIST);
		js.append('[');
		return this;
	}

	public JsonBuilder endMap() {
		if (!MH.anyBits(state, MAP))
			throw new IllegalStateException("attempting to end map when nested in list");
		js.append('}');
		popState();
		return this;
	}

	public JsonBuilder endList() {
		if (!MH.anyBits(state, LIST))
			throw new IllegalStateException("attempting to end list when nested in map");
		js.append(']');
		popState();
		return this;
	}

	public JsonBuilder addEntryJson(CharSequence value) {
		if (!MH.anyBits(state, LIST) && state != 0)
			throw new IllegalStateException("attempting to add list entry, but not in list");
		if (MH.anyBits(state, ENTRY_ADDED))
			js.append(',');
		else
			state |= ENTRY_ADDED;
		js.append(value);
		return this;
	}
	public JsonBuilder addEntry(long value) {
		if (!MH.anyBits(state, LIST) && state != 0)
			throw new IllegalStateException("attempting to add list entry, but not in list");
		if (MH.anyBits(state, ENTRY_ADDED))
			js.append(',');
		else
			state |= ENTRY_ADDED;
		js.append(value);
		return this;
	}
	public JsonBuilder addEntry(double value) {
		if (!MH.anyBits(state, LIST) && state != 0)
			throw new IllegalStateException("attempting to add list entry, but not in list");
		if (MH.anyBits(state, ENTRY_ADDED))
			js.append(',');
		else
			state |= ENTRY_ADDED;
		appendDouble(value);
		return this;
	}
	public JsonBuilder addEntry(boolean value) {
		if (!MH.anyBits(state, LIST) && state != 0)
			throw new IllegalStateException("attempting to add list entry, but not in list");
		if (MH.anyBits(state, ENTRY_ADDED))
			js.append(',');
		else
			state |= ENTRY_ADDED;
		js.append(value);
		return this;
	}
	public JsonBuilder addEntry(Boolean value) {
		if (!MH.anyBits(state, LIST) && state != 0)
			throw new IllegalStateException("attempting to add list entry, but not in list");
		if (MH.anyBits(state, ENTRY_ADDED))
			js.append(',');
		else
			state |= ENTRY_ADDED;
		if (value == null)
			js.append("null");
		else
			js.append(value.booleanValue());
		return this;
	}
	public JsonBuilder addEntry(Number value) {
		if (!MH.anyBits(state, LIST) && state != 0)
			throw new IllegalStateException("attempting to add list entry, but not in list");
		if (MH.anyBits(state, ENTRY_ADDED))
			js.append(',');
		else
			state |= ENTRY_ADDED;
		appendNumber(value);
		return this;
	}
	public JsonBuilder addEntryQuoted(Object value) {
		if (!MH.anyBits(state, LIST) && state != 0)
			throw new IllegalStateException("attempting to add list entry, but not in list");
		if (MH.anyBits(state, ENTRY_ADDED))
			js.append(',');
		else
			state |= ENTRY_ADDED;
		if (value != null)
			quote(OH.toString(value), js);
		else
			js.append("null");
		return this;
	}
	public JsonBuilder addEntryQuoted(long value) {
		if (!MH.anyBits(state, LIST) && state != 0)
			throw new IllegalStateException("attempting to add list entry, but not in list");
		if (MH.anyBits(state, ENTRY_ADDED))
			js.append(',');
		else
			state |= ENTRY_ADDED;
		js.append('\"');
		js.append(value);
		js.append('\"');
		return this;
	}
	public JsonBuilder addEntryQuoted(double value) {
		if (!MH.anyBits(state, LIST) && state != 0)
			throw new IllegalStateException("attempting to add list entry, but not in list");
		if (MH.anyBits(state, ENTRY_ADDED))
			js.append(',');
		else
			state |= ENTRY_ADDED;
		js.append('\"');
		js.append(value);
		js.append('\"');
		return this;
	}
	public JsonBuilder addEntryQuoted(boolean value) {
		if (!MH.anyBits(state, LIST) && state != 0)
			throw new IllegalStateException("attempting to add list entry, but not in list");
		if (MH.anyBits(state, ENTRY_ADDED))
			js.append(',');
		else
			state |= ENTRY_ADDED;
		js.append('\"');
		js.append(value);
		js.append('\"');
		return this;
	}

	private StringBuilder key(Object key) {
		if (strict)
			quote(key.toString(), js);
		else
			js.append(key);
		return js;
	}
	public JsonBuilder addKeyValue(Object key, boolean value) {
		if (!MH.anyBits(state, MAP))
			throw new IllegalStateException("attempting to add map entry, but not in map");
		if (MH.anyBits(state, KEY_ONLY))
			throw new IllegalStateException("key already aded for current entry");
		if (MH.anyBits(state, ENTRY_ADDED))
			js.append(',');
		else
			state |= ENTRY_ADDED;
		key(key).append(':').append(value);
		return this;
	}
	public JsonBuilder addKeyValue(Object key, Boolean value) {
		if (!MH.anyBits(state, MAP))
			throw new IllegalStateException("attempting to add map entry, but not in map");
		if (MH.anyBits(state, KEY_ONLY))
			throw new IllegalStateException("key already aded for current entry");
		if (MH.anyBits(state, ENTRY_ADDED))
			js.append(',');
		else
			state |= ENTRY_ADDED;
		if (value == null)
			key(key).append(":null");
		else
			key(key).append(':').append(value.booleanValue());
		return this;
	}
	public JsonBuilder addKeyValue(Object key, long value) {
		if (!MH.anyBits(state, MAP))
			throw new IllegalStateException("attempting to add map entry, but not in map");
		if (MH.anyBits(state, KEY_ONLY))
			throw new IllegalStateException("key already aded for current entry");
		if (MH.anyBits(state, ENTRY_ADDED))
			js.append(',');
		else
			state |= ENTRY_ADDED;
		key(key).append(':').append(value);
		return this;
	}
	public JsonBuilder addKeyValue(Object key, double value) {
		if (!MH.anyBits(state, MAP))
			throw new IllegalStateException("attempting to add map entry, but not in map");
		if (MH.anyBits(state, KEY_ONLY))
			throw new IllegalStateException("key already aded for current entry");
		if (MH.anyBits(state, ENTRY_ADDED))
			js.append(',');
		else
			state |= ENTRY_ADDED;
		key(key).append(':');
		appendDouble(value);
		return this;
	}
	public JsonBuilder addKeyValue(Object key, Number value) {
		if (!MH.anyBits(state, MAP))
			throw new IllegalStateException("attempting to add map entry, but not in map");
		if (MH.anyBits(state, KEY_ONLY))
			throw new IllegalStateException("key already aded for current entry");
		if (MH.anyBits(state, ENTRY_ADDED))
			js.append(',');
		else
			state |= ENTRY_ADDED;
		key(key).append(':');
		appendNumber(value);
		return this;
	}
	public JsonBuilder addKeyValue(Object key, Iterable value, boolean includeNulls) {
		if (!MH.anyBits(state, MAP))
			throw new IllegalStateException("attempting to add map entry, but not in map");
		if (MH.anyBits(state, KEY_ONLY))
			throw new IllegalStateException("key already aded for current entry");
		if (MH.anyBits(state, ENTRY_ADDED))
			js.append(',');
		else
			state |= ENTRY_ADDED;
		key(key).append(':');
		byte oldState = state;
		state = 0;
		addAutotype((Iterable) value, includeNulls);
		state = oldState;
		return this;
	}
	public JsonBuilder addKeyValue(Object key, Map value, boolean includeNulls) {
		if (!MH.anyBits(state, MAP))
			throw new IllegalStateException("attempting to add map entry, but not in map");
		if (MH.anyBits(state, KEY_ONLY))
			throw new IllegalStateException("key already aded for current entry");
		if (MH.anyBits(state, ENTRY_ADDED))
			js.append(',');
		else
			state |= ENTRY_ADDED;
		key(key).append(':');
		byte oldState = state;
		state = 0;
		addAutotype((Map) value, includeNulls);
		state = oldState;
		return this;
	}
	public JsonBuilder addKeyValueJson(Object key, CharSequence json) {
		if (!MH.anyBits(state, MAP))
			throw new IllegalStateException("attempting to add map value, but no key supplied");
		if (MH.anyBits(state, KEY_ONLY))
			throw new IllegalStateException("key already aded for current entry");
		if (MH.anyBits(state, ENTRY_ADDED))
			js.append(',');
		else
			state |= ENTRY_ADDED;
		key(key).append(':').append(json);
		return this;
	}

	private void appendNumber(Number value) {
		js.append(value);//TODO: handle Nan & Infinity 
	}
	private void appendDouble(double value) {
		if (Double.isNaN(value))
			js.append("null");
		else if (Double.isInfinite(value))
			js.append(value > 0 ? Double.MAX_VALUE : Double.MIN_VALUE);
		else
			js.append(value);

	}
	public JsonBuilder addKeyValueQuoted(Object key, Object value) {
		if (!MH.anyBits(state, MAP))
			throw new IllegalStateException("attempting to add map entry, but not in map");
		if (MH.anyBits(state, KEY_ONLY))
			throw new IllegalStateException("key already aded for current entry");
		if (MH.anyBits(state, ENTRY_ADDED))
			js.append(',');
		else
			state |= ENTRY_ADDED;
		key(key);
		if (value != null) {
			quote(OH.toString(value), js.append(':'));
		} else
			js.append(":null");
		return this;
	}
	public JsonBuilder addKeyValueQuoted(Object key, char value) {
		if (!MH.anyBits(state, MAP))
			throw new IllegalStateException("attempting to add map entry, but not in map");
		if (MH.anyBits(state, KEY_ONLY))
			throw new IllegalStateException("key already aded for current entry");
		if (MH.anyBits(state, ENTRY_ADDED))
			js.append(',');
		else
			state |= ENTRY_ADDED;
		key(key).append(':');
		js.append('\"');
		if (value == '\\' || value == '\"')
			js.append('\\');
		js.append(value);
		js.append('\"');
		return this;
	}
	public JsonBuilder addKeyValueQuoted(Object key, long value) {
		if (!MH.anyBits(state, MAP))
			throw new IllegalStateException("attempting to add map entry, but not in map");
		if (MH.anyBits(state, KEY_ONLY))
			throw new IllegalStateException("key already aded for current entry");
		if (MH.anyBits(state, ENTRY_ADDED))
			js.append(',');
		else
			state |= ENTRY_ADDED;
		key(key).append(':');
		js.append('\"');
		js.append(value);
		js.append('\"');
		return this;
	}
	public JsonBuilder addKeyValueQuoted(Object key, double value) {
		if (!MH.anyBits(state, MAP))
			throw new IllegalStateException("attempting to add map entry, but not in map");
		if (MH.anyBits(state, KEY_ONLY))
			throw new IllegalStateException("key already aded for current entry");
		if (MH.anyBits(state, ENTRY_ADDED))
			js.append(',');
		else
			state |= ENTRY_ADDED;
		key(key).append(':');
		js.append('\"');
		js.append(value);
		js.append('\"');
		return this;
	}
	public JsonBuilder addKey(Object key) {
		if (!MH.anyBits(state, MAP))
			throw new IllegalStateException("attempting to add map entry, but not in map");
		if (MH.anyBits(state, KEY_ONLY))
			throw new IllegalStateException("key already aded for current entry");
		else
			state |= KEY_ONLY;
		if (MH.anyBits(state, ENTRY_ADDED))
			js.append(',');
		else
			state |= ENTRY_ADDED;
		key(key).append(':');
		return this;
	}

	public JsonBuilder startQuote() {
		if (!MH.anyBits(state, MAP | LIST) && state != 0)
			throw new IllegalStateException("attempting to add value, but no key supplied and not in list");
		if (MH.allBits(state, ENTRY_ADDED | LIST))
			js.append(',');
		else
			state |= ENTRY_ADDED;
		pushState(QUOTE);
		js.append('\"');
		return this;
	}
	public JsonBuilder endQuote() {
		if (state != QUOTE)
			throw new IllegalStateException("attempting to end quote, but not in quote");
		popState();
		js.append('\"');
		return this;
	}

	private JsonBuilder addValueQuoted(String value) {
		if (!MH.anyBits(state, MAP))
			throw new IllegalStateException("attempting to add map value, but no key supplied");
		if (value != null)
			quote(OH.toString(value), js);
		else
			js.append("null");
		state = (byte) (MH.clearBits(state, KEY_ONLY) | ENTRY_ADDED);
		return this;
	}
	public JsonBuilder add(Map<?, ? extends Number> map) {
		startMap();
		for (Map.Entry<?, ? extends Number> e : map.entrySet())
			addKeyValue(e.getKey(), e.getValue());
		endMap();
		return this;
	}
	public JsonBuilder addQuoted(Map<?, ?> map) {
		startMap();
		for (Map.Entry<?, ?> e : map.entrySet())
			addKeyValueQuoted(e.getKey(), e.getValue());
		endMap();
		return this;
	}
	public JsonBuilder addAutotype(Map<?, ?> map, boolean includeNulls) {
		startMap();
		for (Map.Entry<?, ?> e : map.entrySet()) {
			addKeyValueAutotype(e.getKey(), e.getValue(), includeNulls);
		}
		endMap();
		return this;
	}
	public JsonBuilder addAutotype(Iterable<?> list, boolean includeNulls) {
		startList();
		for (Object val : list) {
			addEntryAutotype(val, includeNulls);
		}
		endList();
		return this;
	}
	public void addEntryAutotype(Object val, boolean includeNulls) {
		if (val instanceof Number)
			addEntry((Number) val);
		else if (val instanceof Boolean)
			addEntry((Boolean) val);
		else if (val != null || includeNulls)
			addEntryQuoted(val);
	}
	public void addKeyValueAutotype(Object key, Object val, boolean includeNulls) {
		if (val instanceof Number)
			addKeyValue(key, (Number) val);
		else if (val instanceof Boolean)
			addKeyValue(key, (Boolean) val);
		else if (val instanceof Map)
			addKeyValue(key, (Map) val, includeNulls);
		else if (val instanceof Iterable)
			addKeyValue(key, (Iterable) val, includeNulls);
		else if (val != null || includeNulls)
			addKeyValueQuoted(key, val);
	}
	public JsonBuilder add(Iterable<? extends Number> list) {
		startList();
		for (Number o : list)
			addEntry(o);
		endList();
		return this;
	}
	public JsonBuilder addBooleans(Iterable<? extends Boolean> list) {
		startList();
		for (Boolean o : list)
			addEntry(o);
		endList();
		return this;
	}
	public JsonBuilder add(IntIterable list) {
		startList();
		for (IntIterator it = list.iterator(); it.hasNext();)
			addEntry(it.nextInt());
		endList();
		return this;
	}
	public JsonBuilder add(DoubleIterable list) {
		startList();
		for (DoubleIterator it = list.iterator(); it.hasNext();)
			addEntry(it.nextDouble());
		endList();
		return this;
	}
	public JsonBuilder addQuoted(Collection<?> list) {
		startList();
		for (Object o : list)
			addEntryQuoted(o);
		endList();
		return this;
	}
	public JsonBuilder addQuoted(Object[] list, int start, int end) {
		startList();
		for (int i = start; i < end; i++)
			addEntryQuoted(list[i]);
		endList();
		return this;
	}
	public JsonBuilder addQuoted(Object[] list) {
		return addQuoted(list, 0, list.length);
	}

	public JsonBuilder add(double[] list, int start, int end) {
		startList();
		for (int i = start; i < end; i++)
			addEntry(list[i]);
		endList();
		return this;
	}
	public JsonBuilder add(double[] list) {
		return add(list, 0, list.length);
	}
	public JsonBuilder add(float[] list, int start, int end) {
		startList();
		for (int i = start; i < end; i++)
			addEntry(list[i]);
		endList();
		return this;
	}
	public JsonBuilder add(float[] list) {
		return add(list, 0, list.length);
	}

	public JsonBuilder add(boolean[] list, int start, int end) {
		startList();
		for (int i = start; i < end; i++)
			addEntry(list[i]);
		endList();
		return this;
	}

	public JsonBuilder add(boolean[] list) {
		return add(list, 0, list.length);
	}

	public JsonBuilder add(long[] list, int start, int end) {
		startList();
		for (int i = start; i < end; i++)
			addEntry(list[i]);
		endList();
		return this;
	}

	public JsonBuilder add(long[] list) {
		return add(list, 0, list.length);
	}
	public JsonBuilder add(int[] list, int start, int end) {
		startList();
		for (int i = start; i < end; i++)
			addEntry(list[i]);
		endList();
		return this;
	}
	public JsonBuilder add(int[] list) {
		return add(list, 0, list.length);
	}

	public JsonBuilder close() {
		if (state != ENTRY_ADDED)
			throw new IllegalStateException("did not end all maps / lists");
		return this;
	}

	public boolean isEmpty() {
		return state == 0;
	}
	public boolean isClosed() {
		return state == ENTRY_ADDED;
	}
	public JsonBuilder end() {
		close();
		js.append(SH.NEWLINE);
		return this;
	}

	private void pushState(byte state) {
		depth++;
		if (states.length == depth)
			states = Arrays.copyOf(states, states.length * 2);
		states[depth] = this.state;
		this.state = state;
	}
	private void popState() {
		if (depth < 0)
			throw new IllegalStateException("no depth in json");
		state = (byte) MH.clearBits(states[depth--], KEY_ONLY);
	}

	public int getDepth() {
		return depth;
	}

	public JsonBuilder clear() {
		this.js.setLength(this.jsStart);
		this.state = 0;
		this.depth = 0;
		AH.fill(states, (byte) 0);
		return this;
	}
	public JsonBuilder reset(StringBuilder js) {
		this.js = js;
		this.jsStart = js.length();
		this.state = 0;
		this.depth = 0;
		AH.fill(states, (byte) 0);
		return this;
	}

	public StringBuilder getStringBuilder() {
		return js;
	}
	static public StringBuilder quote(String text, StringBuilder sb) {
		sb.append('\"');
		for (int i = 0, l = text.length(); i < l; i++) {
			char c = text.charAt(i);
			switch (c) {
				case '\"':
					sb.append("\\\"");
					break;
				case '\\':
					sb.append("\\\\");
					break;
				case '\n':
					sb.append("\\n");
					break;
				case '\f':
					sb.append("\\f");
					break;
				case '\b':
					sb.append("\\b");
					break;
				case '\t':
					sb.append("\\t");
					break;
				case '\r':
					break;
				default:
					if (c > 0x7e)
						SH.toUnicodeHex(sb, c);
					else
						sb.append(c);
			}
		}
		sb.append('\"');
		return sb;
	}

	@Override
	public String toString() {
		return getStringBuilder().toString();
	}

	public String toStringAssertClosed() {
		assertClosed();
		return getStringBuilder().toString();
	}

	public StringBuilder toStringAssertClosed(StringBuilder sink) {
		assertClosed();
		return sink.append(getStringBuilder());
	}

	private void assertClosed() {
		if (!isClosed())
			throw new IllegalStateException("not done");
	}
	public boolean isDone() {
		return state == ENTRY_ADDED;

	}

	public boolean isStrict() {
		return strict;
	}
	public JsonBuilder setStrict(boolean strict) {
		this.strict = strict;
		return this;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		return sink.append(getStringBuilder());
	}

	public static void main(String[] args) {
		JsonBuilder jb = new JsonBuilder();
		jb.startMap();
		jb.addKeyValueAutotype("abc", 123, true);
		List l = new ArrayList();
		l.add("hello");
		l.add("world");
		l.add(987);
		jb.addKeyValueAutotype("k", l, true);
		Map n = new LinkedHashMap<String, Object>();
		n.put("abc", "123");
		n.put("def", "777");
		n.put("def", 938);
		jb.addKeyValueAutotype("j", false, true);
		jb.addKeyValueAutotype("n", n, true);
		jb.addKeyValueAutotype("m", "testing", true);
		jb.endMap();
		System.out.println(jb.toString());

		Map m = new LinkedHashMap<String, Object>();
		m.put("abc", 123);
		m.put("k", l);
		m.put("j", false);
		m.put("n", n);
		m.put("m", "testing");

		System.out.println(ObjectToJsonConverter.INSTANCE_COMPACT.objectToString(m));
	}
}
