package com.f1.tester.diff;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.f1.base.IntIterator;
import com.f1.base.Message;
import com.f1.base.Transient;
import com.f1.base.Valued;
import com.f1.base.ValuedParam;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.fix.FixMap;

public class MapDiffer implements Differ {

	@Override
	public DiffResult diff(String path, Object left, Object right, DiffSession session) {
		DiffResult r = new DiffResult(left, right, DifferConstants.MAP_MISMATCH);
		final Mapper leftMap = wrap(left);
		final Mapper rightMap = wrap(right);
		if (OH.ne(leftMap.getType(), rightMap.getType()))
			return new DiffResult(left, right, "Map Type Mismatch", leftMap.getType() + "!=" + rightMap.getType());
		final Map<String, Object> leftKeys = toStringMap(leftMap.getKeys());
		final Map<String, Object> rightKeys = toStringMap(rightMap.getKeys());
		for (String key : CH.comm(leftKeys.keySet(), rightKeys.keySet(), true, false, false)) {
			if (session.canIgnore(path + "." + key))
				continue;
			if (leftMap.getValue(leftKeys.get(key)) != null)
				r.addChild(new DiffResult(leftMap.getValue(leftKeys.get(key)), null, DifferConstants.RIGHT_KEY_ABSENT).setKey(key));
		}
		for (String key : CH.comm(leftKeys.keySet(), rightKeys.keySet(), false, true, false)) {
			if (session.canIgnore(path + "." + key))
				continue;
			if (rightMap.getValue(rightKeys.get(key)) != null)
				r.addChild(new DiffResult(null, rightMap.getValue(rightKeys.get(key)), DifferConstants.LEFT_KEY_ABSENT).setKey(key));
		}
		for (String key : CH.comm(leftKeys.keySet(), rightKeys.keySet(), false, false, true)) {
			if (session.canIgnore(path + "." + key))
				continue;
			DiffResult result = session.getRootDiffer().diff(path + "." + key, leftMap.getValue(leftKeys.get(key)), rightMap.getValue(rightKeys.get(key)),
					session);
			if (result != null)
				r.addChild(result.setKey(key));
		}
		if (r.getChildren().size() == 0)
			return null;
		return r;
	}

	public Map<String, Object> toStringMap(Set<Object> set) {
		Map<String, Object> r = new HashMap<String, Object>();
		for (Object o : set)
			CH.putOrThrow(r, o.toString(), o);
		return r;
	}

	private Mapper wrap(Object m) {
		if (m instanceof Map)
			return new MapMapper((Map) m);
		if (m instanceof FixMap)
			return new FixMapMapper((FixMap) m);
		if (m instanceof Valued)
			return new ValuedMapper((Valued) m);
		throw new IllegalStateException(m.toString());
	}

	@Override
	public boolean canDiff(Object left, Object right) {
		return isMap(left) && isMap(right);
	}

	private boolean isMap(Object o) {
		return o instanceof Map || o instanceof FixMap || o instanceof Valued;
	}

	private static interface Mapper {
		public Set<Object> getKeys();

		public Object getValue(Object key);

		public String getType();
	}

	private static class MapMapper implements Mapper {

		private Map inner;

		public MapMapper(Map inner) {
			this.inner = inner;
		}

		@Override
		public Set<Object> getKeys() {
			Set<Object> r = new HashSet<Object>();
			for (Object o : inner.keySet())
				r.add(o);
			return r;
		}

		@Override
		public Object getValue(Object key) {
			return inner.get(key);
		}

		@Override
		public String getType() {
			return OH.toString(getValue("_"));
		}
	}

	private static class ValuedMapper implements Mapper {

		private Valued inner;

		public ValuedMapper(Valued inner) {
			this.inner = inner;
		}

		@Override
		public Set<Object> getKeys() {
			Set r = new HashSet<Object>();
			for (ValuedParam param : inner.askSchema().askValuedParams())
				if (param.getTransience() == Transient.NONE)
					r.add(param.getName());
			r.add("_");// this is a hack for the class name
			return r;
		}

		@Override
		public Object getValue(Object key) {
			if ("_".equals(key))
				return inner.askSchema().askOriginalType().getName();
			return inner.ask(OH.cast(key, String.class));
		}

		@Override
		public String getType() {
			if (inner instanceof Message)
				return ((Message) inner).askIdeableName();
			return inner.askSchema().askOriginalType().getName();
		}
	}

	private static class FixMapMapper implements Mapper {

		private FixMap inner;

		public FixMapMapper(FixMap inner) {
			this.inner = inner;
		}

		@Override
		public Set<Object> getKeys() {
			Set<Object> r = new HashSet<Object>();
			for (IntIterator i = inner.getKeys(); i.hasNext();)
				r.add(i.next());
			return r;
		}

		@Override
		public Object getValue(Object key) {
			return inner.get(OH.cast(key, Integer.class));
		}

		@Override
		public String getType() {
			return null;
		}
	}

}
