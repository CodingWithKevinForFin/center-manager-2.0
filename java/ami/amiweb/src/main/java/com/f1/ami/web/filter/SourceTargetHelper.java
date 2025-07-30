package com.f1.ami.web.filter;

import java.util.HashMap;
import java.util.Map;

import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class SourceTargetHelper {

	public static class SourceTargetValuesMap implements CalcFrame {

		final private char sourcePrefix, targetPrefix;
		private CalcFrame sourceValues;
		private CalcFrame targetValues;
		final private SourceTargetTypesMapping types;
		private CalcFrame vars;

		public SourceTargetValuesMap(SourceTargetTypesMapping types, CalcFrame vars) {
			this.sourcePrefix = types.sourcePrefix.charAt(0);
			this.targetPrefix = types.targetPrefix.charAt(0);
			this.types = types;
			this.vars = vars;
			OH.assertNe(this.sourcePrefix, this.targetPrefix);
		}
		public void resetUnderlyingSourceValues(CalcFrame sr) {
			this.sourceValues = sr;
		}
		public void resetUnderlyingTargetValues(CalcFrame tr) {
			this.targetValues = tr;
		}
		public String getUnderlyingName(String s) {
			return this.types.varNames.get(s);
		}
		public boolean isSource(String s) {
			return s.startsWith(types.sourcePrefix);
		}
		public boolean isTarget(String s) {
			return s.startsWith(types.targetPrefix);
		}

		@Override
		public Object getValue(String key) {
			String name = types.varNames.get(key);
			if (name == null && !SH.startsWith(key.toString(), this.types.targetPrefix))
				return vars.getValue(key);
			if (key.toString().charAt(0) == sourcePrefix)
				return this.sourceValues.getValue(name);
			else {
				if (this.targetValues == null)
					return key.toString().substring(this.types.targetPrefix.length());//seems wrong to me
				return this.targetValues.getValue(name);
			}
		}

		@Override
		public Object putValue(String key, Object value) {
			throw new UnsupportedOperationException();
		}
		@Override
		public Class<?> getType(String key) {
			return this.types.getType(key);
		}
		@Override
		public boolean isVarsEmpty() {
			return this.types.isVarsEmpty();
		}
		@Override
		public Iterable<String> getVarKeys() {
			return this.types.getVarKeys();
		}
		@Override
		public int getVarsCount() {
			return this.types.getVarsCount();
		}

	}

	public static class SourceTargetTypesMapping implements com.f1.base.CalcTypes {

		private String sourcePrefix;
		private String targetPrefix;
		private CalcTypes source;
		private CalcTypes target;
		final private Map<String, String> varNames = new HashMap<String, String>();

		public SourceTargetTypesMapping(String sourcePrefix, CalcTypes source, String targetPrefix, CalcTypes target) {
			this.sourcePrefix = sourcePrefix;
			this.source = source;
			this.targetPrefix = targetPrefix;
			this.target = target;
			if (source != null)
				for (String s : source.getVarKeys())
					varNames.put(sourcePrefix + s, s);
			if (target != null)
				for (String s : target.getVarKeys())
					varNames.put(targetPrefix + s, s);
		}
		@Override
		public Class<?> getType(String key) {
			if (key.startsWith(sourcePrefix))
				return this.source.getType(key.substring(sourcePrefix.length()));
			else if (key.startsWith(targetPrefix)) {
				if (target == null)
					return String.class;
				else
					return this.target.getType(key.substring(targetPrefix.length()));
			} else
				return null;
		}
		@Override
		public boolean isVarsEmpty() {
			return source.isVarsEmpty() && target.isVarsEmpty();
		}
		@Override
		public Iterable<String> getVarKeys() {
			return varNames.keySet();
		}
		@Override
		public int getVarsCount() {
			return varNames.size();
		}

	}
}
