package com.f1.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.f1.base.Getter;
import com.f1.base.IntIterator;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.mutable.Mutable;
import com.f1.utils.mutable.Mutable.Int;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.IntKeyMap.Node;
import com.f1.utils.structs.IntSet;

public class RandomStringPattern {

	private static final Gen GEN_NUM = new ChoiceGen(AH.toSequence('0', '9'));
	private static final Gen GEN_LOWER = new ChoiceGen(AH.toSequence('a', 'z'));
	private static final Gen GEN_UPPER = new ChoiceGen(AH.toSequence('A', 'Z'));
	private static final Gen GEN_ALPHANUM = new ChoiceGen(AH.union(AH.toSequence('A', 'Z'), AH.toSequence('a', 'z'), AH.toSequence('0', '9')));

	public static final char NUM = '#';
	public static final char LOWER = '$';
	public static final char UPPER = '^';
	public static final char REF = '`';
	public static final char ALPHANUM = '*';
	public static final char IF = '%';
	public static final char CHOICE_START = '[';
	public static final char CHOICE_END = ']';
	public static final char SEQUENCE_START = '(';
	public static final char SEQUENCE_END = ')';
	public static final char REPEAT_START = '{';
	public static final char REPEAT_END = '}';
	public static final char ESCAPE = '\\';

	final private Gen gen;
	private String constString;
	private Long constLong;
	private Double constDouble;
	private boolean isConst;

	public RandomStringPattern(String pattern) {
		UnionGen g = new UnionGen();
		StringCharReader scr = new StringCharReader(pattern);
		for (;;) {
			Gen t = toGen(scr);
			if (t == null)
				break;
			g.add(t);
		}
		this.gen = g;
		this.pattern = pattern;
		this.isConst = gen.isConst() && !gen.usesVars();
	}

	final private StringBuilder tmp = new StringBuilder();
	final private String pattern;

	private Gen toGen(StringCharReader scr) {
		int c = scr.readCharOrEof();
		switch (c) {
			case CharReader.EOF:
				return null;
			case SEQUENCE_END:
				return null;
			case CHOICE_END:
				return null;
			case NUM:
				return GEN_NUM;
			case LOWER:
				return GEN_LOWER;
			case UPPER:
				return GEN_UPPER;
			case ALPHANUM:
				return GEN_ALPHANUM;
			case REF:
				scr.readUntil(REF, SH.clear(tmp));
				scr.expect(REF);
				return new RefGen(tmp.toString());
			case IF:
				SH.clear(tmp).append(scr.readChar()).append(scr.readChar());
				int pct = SH.parseInt(tmp, 10);
				return new IfGen(pct, toGen(scr), toGen(scr));
			case ESCAPE:
				return new ConstGen(scr.readChar());
			case SEQUENCE_START: {
				UnionGen g = new UnionGen();
				for (;;) {
					Gen t = toGen(scr);
					if (t == null)
						break;
					g.add(t);
				}
				return g;
			}
			case CHOICE_START: {
				OptionGen g = new OptionGen();
				for (;;) {
					Gen t = toGen(scr);
					if (t == null)
						break;
					g.add(t);
				}
				return g;
			}
			case REPEAT_START: {
				scr.readUntil(REPEAT_END, SH.clear(tmp));
				scr.expect(REPEAT_END);
				int i = SH.indexOf(tmp, '-', 1);
				if (i == -1) {
					int cnt = SH.parseInt(tmp, 10);
					return new Repeat(toGen(scr), cnt, cnt);
				} else {
					int str = SH.parseInt(tmp, 0, i, 10);
					int end = SH.parseInt(tmp, i + 1, tmp.length(), 10);
					return new Repeat(toGen(scr), str, end);
				}
			}
			default:
				return new ConstGen((char) c);
		}
	}

	public boolean isInt() {
		IntSet starts = new IntSet();
		IntSet ends = new IntSet();
		IntKeyMap<Int> counts = new IntKeyMap<Mutable.Int>();
		gen.evaluate(starts, counts, ends);
		if (starts.size() == 0)
			return false;
		if (counts.containsKey('-') && (counts.size() == 1 || counts.get('-').value != 1))
			return false;
		for (int i : starts)
			if (OH.isntBetween(i, '0', '9') && i != '-')
				return false;
		for (int i : ends)
			if (OH.isntBetween(i, '0', '9'))
				return false;
		for (Node<Int> i : counts)
			if (OH.isntBetween((char) (int) i.getKey(), '0', '9') && i.getKey() != '-')
				return false;
		return true;
	}
	public boolean isDouble() {
		IntSet starts = new IntSet();
		IntSet ends = new IntSet();
		IntKeyMap<Int> counts = new IntKeyMap<Mutable.Int>();
		gen.evaluate(starts, counts, ends);
		if (starts.size() == 0)
			return false;
		if (counts.containsKey('-') && (counts.size() == 1 || counts.get('-').value != 1))
			return false;
		if (counts.containsKey('.') && (counts.size() == 1 || counts.get('.').value != 1))
			return false;
		if (counts.containsKey('-') && counts.containsKey('.') && counts.size() == 2)
			return false;
		for (int i : starts)
			if (OH.isntBetween(i, '0', '9') && i != '-' && i != '.')
				return false;
		for (int i : ends)
			if (OH.isntBetween(i, '0', '9'))
				return false;
		for (Node<Int> i : counts)
			if (OH.isntBetween((char) (int) i.getKey(), '0', '9') && i.getKey() != '-' && i.getKey() != '.')
				return false;
		return true;
	}

	public boolean isConst() {
		return isConst;
	}

	private static interface Gen {
		public void gen(Random rand, StringBuilder sink, Getter<String, Object> variables);
		public boolean isConst();
		public boolean usesVars();
		void evaluate(IntSet starts, IntKeyMap<Int> counts, IntSet ends);

	}

	private static class Repeat implements Gen {

		private Gen inner;
		private int start, end;

		public Repeat(Gen inner, int start, int end) {
			if (inner == null)
				throw new NullPointerException("inner");
			this.inner = inner;
			this.start = start;
			this.end = end;
		}

		@Override
		public void gen(Random rand, StringBuilder sink, Getter<String, Object> variables) {
			final int count = start == end ? start : (rand.nextInt(end - start + 1) + start);
			for (int i = 0; i < count; i++)
				inner.gen(rand, sink, variables);
		}

		@Override
		public void evaluate(IntSet starts, IntKeyMap<Int> counts, IntSet ends) {
			IntKeyMap<Int> t = new IntKeyMap<Int>();
			for (Node<Int> e : t)
				increment(counts, (char) e.getIntKey(), e.getValue().value * end);
			inner.evaluate(starts, t, ends);
		}

		@Override
		public boolean isConst() {
			return start == end && inner.isConst();
		}

		@Override
		public boolean usesVars() {
			return inner.usesVars();
		}

	}

	private static class IfGen implements Gen {

		final private int pct;
		final private Gen trueCase;
		final private Gen falseCase;

		public IfGen(int pct, Gen t, Gen f) {
			this.trueCase = t;
			this.falseCase = f;
			this.pct = pct;
		}

		@Override
		public void gen(Random rand, StringBuilder sink, Getter<String, Object> variables) {
			(rand == null || rand.nextInt(100) < pct ? trueCase : falseCase).gen(rand, sink, variables);
		}

		@Override
		public void evaluate(IntSet starts, IntKeyMap<Int> counts, IntSet ends) {
			trueCase.evaluate(starts, counts, ends);
			IntKeyMap<Int> t = new IntKeyMap<Int>();
			falseCase.evaluate(starts, t, ends);
			for (Node<Int> e : t) {
				Int cnt = counts.get(e.getIntKey());
				if (cnt == null || e.getValue().value > cnt.value)
					counts.put(e.getIntKey(), e.getValue());
			}
		}
		@Override
		public boolean isConst() {
			if (pct == 0 && trueCase.isConst())
				return true;
			if (trueCase.isConst() && falseCase.isConst()) {
				StringBuilder s1 = new StringBuilder();
				StringBuilder s2 = new StringBuilder();
				trueCase.gen(null, s1, null);
				falseCase.gen(null, s2, null);
				return SH.equals(s1, s2);
			} else
				return false;
		}
		@Override
		public boolean usesVars() {
			if (pct == 0)
				return trueCase.usesVars();
			return trueCase.usesVars() || falseCase.usesVars();
		}

	}

	private static class OptionGen implements Gen {

		List<Gen> gens = new ArrayList<Gen>();

		public void add(Gen gen) {
			this.gens.add(gen);
		}
		@Override
		public void gen(Random rand, StringBuilder sink, Getter<String, Object> variables) {
			if (gens.size() == 0)
				return;
			else if (gens.size() == 1 || rand == null)
				gens.get(0).gen(rand, sink, variables);
			else
				gens.get(rand.nextInt(gens.size())).gen(rand, sink, variables);
		}
		@Override
		public void evaluate(IntSet starts, IntKeyMap<Int> counts, IntSet ends) {
			IntKeyMap<Int> t = new IntKeyMap<Int>();
			for (Gen gen : gens) {
				t.clear();
				gen.evaluate(starts, t, ends);
				for (Node<Int> e : t) {
					Int cnt = counts.get(e.getIntKey());
					if (cnt == null || e.getValue().value > cnt.value)
						counts.put(e.getIntKey(), e.getValue());
				}
			}
		}
		@Override
		public boolean isConst() {
			final int size = gens.size();
			if (size == 0)
				return true;
			if (size == 1)
				return gens.get(0).isConst();

			for (Gen gen : gens)
				if (!gen.isConst())
					return false;
			StringBuilder s1 = new StringBuilder();
			gens.get(0).gen(null, s1, null);
			StringBuilder s2 = new StringBuilder();
			for (int i = 1; i < size; i++) {
				gens.get(i).gen(null, SH.clear(s2), null);
				if (!SH.equals(s1, s2))
					return false;
			}
			return true;
		}
		@Override
		public boolean usesVars() {
			for (int i = 0; i < gens.size(); i++)
				if (gens.get(i).usesVars())
					return true;
			return false;
		}

	}

	private static class UnionGen implements Gen {

		List<Gen> gens = new ArrayList<Gen>();

		public void add(Gen gen) {
			this.gens.add(gen);
		}
		@Override
		public void gen(Random rand, StringBuilder sink, Getter<String, Object> variables) {
			for (Gen gen : gens)
				gen.gen(rand, sink, variables);
		}
		@Override
		public void evaluate(IntSet starts, IntKeyMap<Int> counts, IntSet ends) {
			int len = gens.size();
			if (len == 1) {
				gens.get(0).evaluate(starts, counts, ends);
			} else if (len > 1) {
				gens.get(0).evaluate(starts, counts, null);
				for (int i = 1; i < len - 1; i++)
					gens.get(i).evaluate(null, counts, null);
				gens.get(len - 1).evaluate(null, counts, ends);
			}
		}
		@Override
		public boolean isConst() {
			for (Gen gen : gens)
				if (!gen.isConst())
					return false;
			return true;
		}
		@Override
		public boolean usesVars() {
			for (int i = 0; i < gens.size(); i++)
				if (gens.get(i).usesVars())
					return true;
			return false;
		}

	}

	private static class ConstGen implements Gen {

		final private char c;

		public ConstGen(char c) {
			this.c = c;
		}
		@Override
		public void gen(Random rand, StringBuilder sink, Getter<String, Object> variables) {
			sink.append(c);
		}
		@Override
		public void evaluate(IntSet starts, IntKeyMap<Mutable.Int> counts, IntSet ends) {
			if (starts != null)
				starts.add(c);
			if (ends != null)
				ends.add(c);
			if (counts != null)
				increment(counts, c, 1);
		}
		@Override
		public boolean isConst() {
			return true;
		}
		@Override
		public boolean usesVars() {
			return false;
		}

	}

	private static class ChoiceGen implements Gen {

		private char[] choices;

		public ChoiceGen(char choices[]) {
			if (choices.length == 0)
				throw new IllegalArgumentException("empty array");
			IntSet t = new IntSet(choices.length);
			for (char c : choices)
				t.add(c);
			if (t.size() == choices.length)
				this.choices = choices.clone();
			else {
				this.choices = new char[t.size()];
				int j = 0;
				for (IntIterator i = t.iterator(); i.hasNext();)
					this.choices[j++] = (char) i.nextInt();
			}
		}

		@Override
		public void gen(Random rand, StringBuilder sink, Getter<String, Object> variables) {
			if (choices.length == 1)
				sink.append(choices[0]);
			else
				sink.append(choices[rand.nextInt(choices.length)]);

		}

		@Override
		public void evaluate(IntSet starts, IntKeyMap<Mutable.Int> counts, IntSet ends) {
			if (starts != null)
				for (char c : choices)
					starts.add(c);
			if (ends != null)
				for (char c : choices)
					ends.add(c);
			if (counts != null)
				for (char c : choices)
					increment(counts, c, 1);
		}

		@Override
		public boolean isConst() {
			return choices.length == 1;
		}
		public boolean usesVars() {
			return false;
		}

	}

	static private void increment(IntKeyMap<Int> counts, char c, int count) {
		Int cnt = counts.get(c);
		if (cnt == null)
			counts.put(c, new Int(count));
		else
			cnt.value += count;
	}

	public static String generate(String pattern) {
		return new RandomStringPattern(pattern).generate(new Random());
	}

	public double generateDouble(Random r) {
		if (isConst) {
			if (constDouble != null)
				return constDouble;
			generate(r, SH.clear(tmp));
			return constDouble = SH.parseDouble(tmp);
		}
		generate(r, SH.clear(tmp));
		return SH.parseDouble(tmp);
	}
	public long generateLong(Random r) {
		if (isConst) {
			if (constLong != null)
				return constLong;
			generate(r, SH.clear(tmp));
			return constLong = SH.parseLong(tmp, 10);
		}
		generate(r, SH.clear(tmp));
		return SH.parseLong(tmp, 10);
	}

	public StringBuilder generate(Random r, StringBuilder sink) {
		return generate(r, sink, null);
	}
	public StringBuilder generate(Random r, StringBuilder sink, Getter<String, Object> variables) {
		if (constString != null)
			sink.append(constString);
		else {
			if (isConst) {
				int len = sink.length();
				gen.gen(r, sink, null);
				constString = sink.substring(len);
			} else {
				gen.gen(r, sink, variables);
			}
		}
		return sink;
	}

	public String generate(Random r) {
		return generate(r, SH.clear(tmp)).toString();
	}

	public String getPattern() {
		return pattern;
	}

	public static class RefGen implements Gen {

		private String varName;

		public RefGen(String varName) {
			this.varName = varName;
		}

		@Override
		public void gen(Random rand, StringBuilder sink, Getter<String, Object> variables) {
			Object value = variables.get(varName);
			if (value != null)
				sink.append(value);
		}

		@Override
		public boolean isConst() {
			return false;
		}

		@Override
		public boolean usesVars() {
			return true;
		}

		@Override
		public void evaluate(IntSet starts, IntKeyMap<Int> counts, IntSet ends) {
		}

	}

}
