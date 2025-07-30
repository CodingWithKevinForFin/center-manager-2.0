package com.f1.stringmaker.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.f1.stringmaker.StringMaker;
import com.f1.stringmaker.StringMakerFormatter;
import com.f1.stringmaker.StringMakerSession;
import com.f1.utils.SH;

public class BasicStringMaker {

	public static class Const implements StringMaker {
		public static final Const EMPTY = new Const("");
		public static final Const ENDIF = new Const("endif");
		public static final Const ELSE = new Const("else");
		final String text;

		public Const(String text) {
			this.text = text;
		}

		@Override
		public void toString(StringMakerSession session) {
			session.append(text);
		}

		@Override
		public String toString() {
			return getClass().getName() + ": " + text;
		}

		@Override
		public Set<String> getReferences(Set<String> sink) {
			return sink;
		}

	}

	public static class Reference implements StringMaker {
		final String[] key;
		final private String origKey;
		final private String format;
		final private String formatArgs;
		final private StringMakerFormatter formatter;

		public Reference(String key, StringMakerFormatter formatter, String format, String formatArgs) {
			this.key = SH.split('.', key);
			this.format = format;
			this.formatArgs = formatArgs;
			this.origKey = key;
			this.formatter = formatter;
		}

		@Override
		public void toString(StringMakerSession session) {
			formatter.append(session.getValue(key, origKey), format, formatArgs, session);
		}

		@Override
		public Set<String> getReferences(Set<String> sink) {
			sink.add(origKey);
			return sink;
		}

	}

	public static class Multi implements StringMaker {

		final private StringMaker[] parts;
		private StringMaker terminator;

		public Multi(StringMaker parts[], StringMaker terminator) {
			this.parts = parts;
			this.terminator = terminator;
		}

		@Override
		public void toString(StringMakerSession session) {
			for (StringMaker part : parts)
				part.toString(session);
		}

		public StringMaker getTerminator() {
			return terminator;
		}

		@Override
		public Set<String> getReferences(Set<String> sink) {
			for (StringMaker part : parts)
				part.getReferences(sink);
			return sink;
		}

	}

	public static class Join implements StringMaker {

		final private String[] key;
		final private String delim;
		private String origKey;

		public Join(String key, String delim) {
			this.key = SH.split('.', key);
			this.origKey = key;
			this.delim = delim;
		}

		@Override
		public void toString(StringMakerSession session) {
			final Iterator<?> i = session.toIterator(session.getValue(key, origKey));
			boolean first = true;
			while (i.hasNext()) {
				if (first)
					first = false;
				else
					session.append(delim);
				session.append(i.next());
			}
		}

		@Override
		public Set<String> getReferences(Set<String> sink) {
			return null;
		}
	}

	public static class Comment implements StringMaker {
		final private String comments;

		public Comment(String comments) {
			this.comments = comments;
		}

		@Override
		public void toString(StringMakerSession session) {
		}

		@Override
		public Set<String> getReferences(Set<String> sink) {
			return sink;
		}
	}

	public static class Nest implements StringMaker {
		private final String stringMakerName;
		private final Map<String, String> parameters;

		public Nest(String stringMakerName, Map<String, String> parameters) {
			this.stringMakerName = stringMakerName;
			this.parameters = parameters;
		}

		@Override
		public void toString(StringMakerSession session) {
			final StringMaker maker = session.getStringMaker(stringMakerName);
			if (parameters != null) {
				for (final Map.Entry<String, String> e : parameters.entrySet())
					session.pushValue(e.getKey(), session.getValue(e.getValue()));
				try {
					maker.toString(session);
				} finally {
					for (String e : parameters.keySet())
						session.popValue(e);
				}
			} else
				maker.toString(session);
		}

		@Override
		public Set<String> getReferences(Set<String> sink) {
			sink.addAll(parameters.values());
			return sink;
		}
	}

	public static class If implements StringMaker {

		final private String[] key;
		final private StringMaker trueCondition;
		final private StringMaker falseCondition;
		private String origKey;

		public If(String key, StringMaker trueCondition, StringMaker falseCondition) {
			this.key = SH.split('.', key);
			this.origKey = key;
			this.trueCondition = trueCondition;
			this.falseCondition = falseCondition;
		}

		@Override
		public void toString(StringMakerSession session) {
			boolean bool = session.toBoolean(session.getValue(key, origKey));
			if (bool)
				trueCondition.toString(session);
			else if (falseCondition != null)
				falseCondition.toString(session);
		}

		@Override
		public Set<String> getReferences(Set<String> sink) {
			sink.add(origKey);
			return sink;
		}
	}

	public static class ElseIf extends If {

		public ElseIf(String key, StringMaker trueCondition, StringMaker falseCondition) {
			super(key, trueCondition, falseCondition);
		}

	}

	public static class ElseIfNot extends IfNot {

		public ElseIfNot(String key, StringMaker trueCondition, StringMaker falseCondition) {
			super(key, trueCondition, falseCondition);
		}

	}

	public static class IfNot implements StringMaker {

		final private String[] key;
		final private StringMaker trueCondition;
		final private StringMaker falseCondition;
		private String origKey;

		public IfNot(String key, StringMaker trueCondition, StringMaker falseCondition) {
			this.key = SH.split('.', key);
			this.origKey = key;
			this.trueCondition = trueCondition;
			this.falseCondition = falseCondition;
		}

		@Override
		public void toString(StringMakerSession session) {
			boolean bool = !session.toBoolean(session.getValue(key, origKey));
			if (bool)
				trueCondition.toString(session);
			else if (falseCondition != null)
				falseCondition.toString(session);
		}
		@Override
		public Set<String> getReferences(Set<String> sink) {
			sink.add(origKey);
			return sink;
		}
	}

	public static class Loop implements StringMaker {
		private final String key[], lcv;
		private final StringMaker inner;
		private String origKey;

		public Loop(String key, String lcv, StringMaker inner) {
			this.key = SH.split('.', key);
			this.origKey = key;
			this.inner = inner;
			this.lcv = lcv;
		}

		@Override
		public void toString(StringMakerSession session) {
			for (final Iterator<?> i = session.toIterator(session.getValue(key, origKey)); i.hasNext();) {
				try {
					Object o = i.next();
					if (o == null)
						break;
					session.pushValue(lcv, o);
					inner.toString(session);
				} finally {
					session.popValue(lcv);
				}
			}
		}
		@Override
		public Set<String> getReferences(Set<String> sink) {
			sink.add(origKey);
			return sink;
		}
	}

	public static class First implements StringMaker {
		private final String key[], lcv;
		private final StringMaker inner;
		private String origKey;

		public First(String key, String lcv, StringMaker inner) {
			this.key = SH.split('.', key);
			this.origKey = key;
			this.inner = inner;
			this.lcv = lcv;
		}

		@Override
		public void toString(StringMakerSession session) {
			for (final Iterator<?> i = session.toIterator(session.getValue(key, origKey)); i.hasNext();) {
				try {
					Object o = i.next();
					if (o == null)
						break;
					session.pushValue(lcv, o);
					inner.toString(session);
					break;
				} finally {
					session.popValue(lcv);
				}
			}
		}
		@Override
		public Set<String> getReferences(Set<String> sink) {
			sink.add(origKey);
			return sink;
		}
	}

	public static class Rest implements StringMaker {
		private final String key[], lcv;
		private final StringMaker inner;
		private String origKey;

		public Rest(String key, String lcv, StringMaker inner) {
			this.key = SH.split('.', key);
			this.origKey = key;
			this.inner = inner;
			this.lcv = lcv;
		}

		@Override
		public void toString(StringMakerSession session) {
			final Iterator<?> i = session.toIterator(session.getValue(key, origKey));
			if (!i.hasNext())
				return;
			i.next();
			while (i.hasNext()) {
				try {
					Object o = i.next();
					if (o == null)
						break;
					session.pushValue(lcv, o);
					inner.toString(session);
				} finally {
					session.popValue(lcv);
				}
			}
		}
		@Override
		public Set<String> getReferences(Set<String> sink) {
			sink.add(origKey);
			return sink;
		}
	}

	public static class Length implements StringMaker {
		private final String key[], origKey;

		public Length(String key) {
			this.key = SH.split('.', key);
			this.origKey = key;
		}

		@Override
		public void toString(StringMakerSession session) {
			session.append(session.toLength(session.getValue(key, origKey)));
		}
		@Override
		public Set<String> getReferences(Set<String> sink) {
			sink.add(origKey);
			return sink;
		}
	}

}
