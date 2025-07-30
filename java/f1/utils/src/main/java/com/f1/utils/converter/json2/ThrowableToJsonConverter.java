package com.f1.utils.converter.json2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.utils.DetailedException;
import com.f1.utils.SH;

public class ThrowableToJsonConverter extends AbstractJsonConverter<Throwable> {

	public ThrowableToJsonConverter() {
		super(Throwable.class);
	}

	@Override
	public Object stringToObject(FromJsonConverterSession session) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void objectToString(Throwable o, ToJsonConverterSession out) {
		Map<String, Object> top = null, current = null;
		StringBuilder buf = new StringBuilder();
		for (Throwable t = o; t != null; t = t.getCause()) {
			StackTraceElement[] stes = t.getStackTrace();
			Map<String, Object> last = current;
			current = new HashMap<String, Object>();
			if (last == null)
				top = current;
			else
				last.put("_cause", current);
			current.put("_", t.getClass().getName());
			if (SH.is(t.getMessage()))
				current.put("_message", t.getMessage());
			if (t instanceof DetailedException) {
				DetailedException de = (DetailedException) t;
				Set<String> keys = de.getKeys();
				if (keys.size() > 0) {
					HashMap<String, List<String>> details = new HashMap<String, List<String>>(keys.size());
					for (String key : keys)
						details.put(key, de.getValues(key));
					current.put("_details", details);
				}
			}
			if (stes != null) {
				List<String> stack = new ArrayList<String>(stes.length);
				for (StackTraceElement ste : stes) {
					String s = SH.clear(buf).append(ste.getClassName()).append('.').append(ste.getMethodName()).append('(').append(ste.getFileName()).append(':')
							.append(ste.getLineNumber()).append(')').toString();
					stack.add(s);
				}
				current.put("_callstack", stack);
			}
		}
		out.getConverter().objectToString(top, out);
	}
}
