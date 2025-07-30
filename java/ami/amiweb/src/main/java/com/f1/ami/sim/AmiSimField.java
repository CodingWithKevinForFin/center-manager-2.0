package com.f1.ami.sim;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.f1.utils.MH;
import com.f1.utils.RandomStringPattern;
import com.f1.utils.SH;
import com.f1.utils.math.PrimitiveMathManager;
import com.f1.utils.structs.SkipListDataEntry;

public abstract class AmiSimField<T> {

	private String paramName;
	private double update = 0;

	public AmiSimField() {
	}

	public AmiSimField<T> setParamName(String paramName) {
		this.paramName = paramName;
		return this;
	}

	abstract public T generateValue(AmiSimObject object, T current, AmiSimSession session);

	public String getParamName() {
		return paramName;
	}

	public double canUpdate() {
		return update;
	}
	protected RandomStringPattern toNumber(String pattern) {
		if (pattern == null)
			return null;
		RandomStringPattern r = new RandomStringPattern(pattern);
		if (!r.isInt())
			throw new AmiSimException(getParamName() + ": not a whole number: " + pattern);
		return r;
	}
	protected RandomStringPattern toDouble(String pattern) {
		if (pattern == null)
			return null;
		RandomStringPattern r = new RandomStringPattern(pattern);
		if (!r.isDouble())
			throw new AmiSimException(getParamName() + ": not a number: " + pattern);
		return r;
	}

	public AmiSimField<T> setCanUpdate(double canUpdate) {
		this.update = canUpdate;
		return this;
	}

	public static class NumberSim extends AmiSimField<Object> {
		public static final char TYPE_LONG = 'L';
		public static final char TYPE_DOUBLE = 'D';
		public static final char TYPE_FLOAT = 'F';
		public static final char TYPE_INT = 'I';
		public static final char TYPE_RAND = 'R';
		private static final char[] types = new char[] { TYPE_LONG, TYPE_DOUBLE, TYPE_FLOAT, TYPE_INT };

		private RandomStringPattern start;
		private RandomStringPattern delta;
		private RandomStringPattern max;
		private RandomStringPattern min;
		private char type;

		@Override
		public Object generateValue(AmiSimObject object, Object value, AmiSimSession session) {
			Random random = session.getRand();
			final char typ;
			if (type == 'R') {
				typ = types[random.nextInt(types.length)];
			} else
				typ = type;
			if (value == null) {
				switch (typ) {
					case TYPE_LONG:
						return (long) clip(start.generateLong(random), random);
					case TYPE_INT:
						return (int) clip(start.generateLong(random), random);
					case TYPE_DOUBLE:
						return (double) clip(start.generateDouble(random), random);
					case TYPE_FLOAT:
						return (float) clip(start.generateDouble(random), random);
					default:
						throw new RuntimeException("unknown type: " + type);
				}
			} else if (delta != null && value instanceof Number) {
				Number num = (Number) value;
				switch (typ) {
					case TYPE_LONG:
						return (long) clip(num.longValue() + delta.generateLong(random), random);
					case TYPE_INT:
						return (int) clip(num.longValue() + delta.generateLong(random), random);
					case TYPE_DOUBLE:
						return (double) clip(num.doubleValue() + delta.generateDouble(random), random);
					case TYPE_FLOAT:
						return (float) clip(num.doubleValue() + delta.generateDouble(random), random);
					default:
						throw new RuntimeException("unknown type: " + type);
				}
			} else
				return value;
		}
		public NumberSim setStartValue(String start) {
			this.start = toDouble(start);
			return this;
		}
		public NumberSim setDeltaValue(String delta) {
			this.delta = toDouble(delta);
			return this;
		}
		public NumberSim setMinValue(String min) {
			this.min = toNumber(min);
			return this;
		}
		public NumberSim setMaxValue(String max) {
			this.max = toNumber(max);
			return this;
		}

		private long clip(long vl, Random random) {
			if (min != null)
				vl = Math.max(min.generateLong(random), vl);
			if (max != null)
				vl = Math.min(max.generateLong(random), vl);
			return vl;
		}
		private double clip(double vl, Random random) {
			if (min != null)
				vl = Math.max(min.generateDouble(random), vl);
			if (max != null)
				vl = Math.min(max.generateDouble(random), vl);
			return vl;
		}
		public NumberSim setType(String type) {
			this.type = Character.toUpperCase(type.charAt(0));
			return this;
		}
	}

	public static class TimeSim extends AmiSimField<Long> {

		private RandomStringPattern offset;

		@Override
		public Long generateValue(AmiSimObject object, Long value, AmiSimSession session) {
			Random random = session.getRand();
			return System.currentTimeMillis() + (offset == null ? 0L : offset.generateLong(random));
		}

		public void setOffsetRange(String pattern) {
			this.offset = toNumber(pattern);
		}

		public String getOffset() {
			return offset.getPattern();
		}

	}

	public static class IdSim extends AmiSimField<String> {

		private String prefix = "ID-";
		private int next = 0;
		private int digits = 8;
		private StringBuilder sb = new StringBuilder();

		public IdSim(String prefix) {
			this.prefix = prefix;
		}
		public IdSim() {
		}

		@Override
		public String generateValue(AmiSimObject object, String value, AmiSimSession session) {
			sb.append(prefix);
			SH.repeat('0', digits - MH.getDigitsCount(next, 10), sb);
			SH.toString(next++, 10, sb);
			return SH.toStringAndClear(sb);
		}

		public IdSim setDigits(int digits) {
			this.digits = digits;
			return this;
		}
		public IdSim setPrefix(String prefix) {
			this.prefix = prefix;
			return this;
		}

		public long getDigits() {
			return digits;
		}
		public long getNext() {
			return next;
		}

	}

	public static class RefSim extends AmiSimField<Object> {

		private String refType, refParam;
		private RandomStringPattern offset;

		@Override
		public Object generateValue(AmiSimObject object, Object value, AmiSimSession session) {
			AmiSimObject refObj = object.getReference(refType);
			if (refObj == null) {
				Random random = session.getRand();
				AmiSim sim = session.getSim();
				AmiSimType type = sim.getType(refType);
				if (type == null)
					return null;
				List<SkipListDataEntry<AmiSimObject>> objects = type.getObjects();
				if (objects.isEmpty())
					return null;
				refObj = objects.get(random.nextInt(objects.size())).getData();
			}
			Object r = refObj.getParams().get(refParam);
			if (offset != null && r instanceof Number) {
				Number num = (Number) r;
				return PrimitiveMathManager.INSTANCE.get(r.getClass()).add(offset.generateDouble(session.getRand()), (Number) r);
			} else
				return r;
		}
		public void setOffsetRange(String pattern) {
			this.offset = toDouble(pattern);
		}

		public String getOffset() {
			return offset.getPattern();
		}

		public RefSim setRef(String refType, String refParam) {
			this.refType = refType;
			this.refParam = refParam;
			return this;
		}

		public String getRefType() {
			return refType;
		}

		public String getRefParam() {
			return refParam;
		}

		@Override
		public void validate(AmiSimSession session) {
			AmiSimType type = session.getSim().getType(refType);
			if (type == null)
				throw new AmiSimException("unknown refType: " + refType);
			else if (!type.getFields().containsKey(refParam))
				throw new AmiSimException("unknown refParam for '" + refType + "': " + refParam);
			super.validate(session);
		}

	}

	public static class PatternSim extends AmiSimField<Object> {

		public static final char TYPE_LONG = 'L';
		public static final char TYPE_DOUBLE = 'D';
		public static final char TYPE_FLOAT = 'F';
		public static final char TYPE_INT = 'I';
		public static final char TYPE_STRING = 'S';
		public static final char TYPE_ENUM = 'E';
		public static final char TYPE_BOOLEAN = 'B';
		private RandomStringPattern pattern;
		private final StringBuilder sink = new StringBuilder();
		private char type;
		private int maxUniqueValues = -1;
		private Set<Object> uniqueValues = new HashSet<Object>();
		private List<Object> uniqueValuesList = new ArrayList<Object>();

		@Override
		public Object generateValue(AmiSimObject object, Object current, AmiSimSession session) {

			if (maxUniqueValues < 0) {
				return generate(session.getRand(), object);
			} else {
				final int pos = session.getRand().nextInt(maxUniqueValues);
				if (pos < uniqueValuesList.size())
					return uniqueValuesList.get(pos);
				for (int i = 0; i < 1000; i++) {
					Object r = generate(session.getRand(), object);
					if (uniqueValues.add(r)) {
						uniqueValuesList.add(r);
						return r;
					}
				}
				maxUniqueValues = uniqueValues.size();
				return this.generateValue(object, current, session);
			}
		}

		StringBuilder tmp = new StringBuilder();
		private Object generate(Random rand, AmiSimObject object) {
			final Object r;
			switch (type) {
				case TYPE_LONG:
					return pattern.generateLong(rand);
				case TYPE_INT:
					return (int) pattern.generateLong(rand);
				case TYPE_DOUBLE:
					return pattern.generateDouble(rand);
				case TYPE_FLOAT:
					return (float) pattern.generateDouble(rand);
				case TYPE_STRING:
					if (pattern.isConst())
						return pattern.generate((Random) null);
					else
						return new String(pattern.generate(rand, SH.clear(tmp), object));
				case TYPE_ENUM:
					return pattern.generate(rand).toCharArray();
				case TYPE_BOOLEAN:
					pattern.generate(rand, SH.clear(sink));
					return SH.equals("t", sink);
				default:
					throw new RuntimeException("unknown type: " + type);
			}

		}

		public String getPattern() {
			return pattern.getPattern();
		}
		public PatternSim setPattern(String pattern) {
			this.pattern = new RandomStringPattern(pattern);
			return this;
		}
		public PatternSim setMaxUniqueValues(int max) {
			this.maxUniqueValues = max;
			return this;
		}

		@Override
		public void validate(AmiSimSession session) {
			super.validate(session);
			if (pattern == null)
				throw new AmiSimException("pattern required");
			switch (type) {
				case TYPE_LONG:
				case TYPE_INT:
					if (!pattern.isInt())
						throw new AmiSimException("pattern must be a whole number: " + pattern.getPattern());
					break;
				case TYPE_DOUBLE:
				case TYPE_FLOAT:
					if (!pattern.isDouble())
						throw new AmiSimException("pattern must be a number: " + pattern.getPattern());
			}
		}

		public void setType(String type) {
			this.type = Character.toUpperCase(type.charAt(0));
		}

	}

	public void validate(AmiSimSession session) {
	}

}
