package com.f1.stringmaker.impl;

import java.util.ArrayList;
import java.util.List;

import com.f1.stringmaker.StringMaker;
import com.f1.stringmaker.StringMakerFactory;
import com.f1.stringmaker.StringMakerFormatter;
import com.f1.stringmaker.StringMakerFormatterFactory;
import com.f1.stringmaker.impl.BasicStringMaker.Multi;
import com.f1.utils.CharReader;
import com.f1.utils.SH;
import com.f1.utils.StringFormatException;
import com.f1.utils.impl.StringCharReader;

public class BasicStringMakerFactory implements StringMakerFactory {

	private static final int[] DOLLAR_OR_EOF = new int[] { '$', CharReader.EOF };
	private static final int[] DOLLAR_COL_PARENTHESIS_SEMI = new int[] { '$', ':', '(', ';' };
	private static final int[] DOLLAR_CLOSE_EOF = { '$', '}', CharReader.EOF };
	private StringMakerFormatterFactory formatterFactory = null;

	@Override
	public StringMaker get(String text) {
		final StringCharReader reader = new StringCharReader(text);
		final StringBuilder tmp = new StringBuilder();
		try {
			return parseMulti(reader, tmp);
		} catch (Exception e) {
			throw new StringFormatException("Error parsing ", e, text, reader.getCountRead());
		}
	}

	private BasicStringMaker.Multi parseMulti(StringCharReader reader, StringBuilder tmp) {
		List<StringMaker> l = new ArrayList<StringMaker>();
		StringMaker terminator = BasicStringMaker.Const.EMPTY;
		boolean inFreeText = true;
		while (reader.peakOrEof() != CharReader.EOF) {
			int c = reader.peakOrEof();
			if (c == CharReader.EOF)
				break;
			if (c == '}') {
				reader.mark();
				reader.expect('}');
				int c2 = reader.peakOrEof();
				reader.returnToMark();
				if (c2 == '$')
					break;
			}
			StringMaker r = inFreeText ? parseConst(reader, tmp) : parseComplex(reader, tmp);
			if (r != null) {
				if (r == BasicStringMaker.Const.ENDIF || r == BasicStringMaker.Const.ELSE || r instanceof BasicStringMaker.ElseIf) {
					terminator = r;
					break;
				}
				l.add(r);
			}
			inFreeText = !inFreeText;
		}
		return new BasicStringMaker.Multi(l.toArray(new StringMaker[l.size()]), terminator);
	}

	private StringMaker parseConst(StringCharReader reader, StringBuilder tmp) {
		SH.clear(tmp);
		for (;;) {
			int c = reader.readUntilAny(DOLLAR_CLOSE_EOF, '\\', tmp);
			if (c != '}') {
				reader.expectAny(DOLLAR_CLOSE_EOF);
				break;
			} else {
				reader.mark();
				reader.expect('}');
				int c2 = reader.peakOrEof();
				if (c2 == '$') {
					reader.returnToMark();
					break;
				}
				tmp.append('}');
			}
		}
		if (tmp.length() == 0)
			return null;
		return new BasicStringMaker.Const(tmp.toString());
	}

	private StringMaker parseComplex(StringCharReader reader, StringBuilder tmp) {
		if (reader.peakOrEof() == '!') {
			reader.expect('!');
			reader.readUntil('!', SH.clear(tmp));
			reader.expect('!');
			reader.expect('$');
			return new BasicStringMaker.Comment(tmp.toString());
		}
		char c = (char) reader.readUntilAny(DOLLAR_COL_PARENTHESIS_SEMI, '\\', SH.clear(tmp));
		reader.expect(c);
		switch (c) {
			case '$':
				if (SH.equals("endif", tmp))
					return BasicStringMaker.Const.ENDIF;
				if (SH.equals("else", tmp))
					return BasicStringMaker.Const.ELSE;
				String text = tmp.toString();
				return new BasicStringMaker.Reference(text, getFormatter(null, null), null, null);
			case ';':
				text = SH.toStringAndClear(tmp);
				reader.skip(StringCharReader.WHITE_SPACE);
				reader.expectSequence("format=\"");
				reader.readUntil('"', '\\', tmp);
				reader.expect('"');
				reader.skip(StringCharReader.WHITE_SPACE);
				String format = SH.toStringAndClear(tmp);
				String args;
				if (reader.peak() == ';') {
					reader.skip(StringCharReader.WHITE_SPACE);
					reader.expectSequence("args=\"");
					reader.readUntil('"', '\\', tmp);
					reader.expect('"');
					reader.skip(StringCharReader.WHITE_SPACE);
					args = SH.toStringAndClear(tmp);
				} else
					args = null;
				reader.expect('$');
				return new BasicStringMaker.Reference(text, getFormatter(format, args), null, null);
			case '(':
				if (SH.equals("if", tmp) || SH.equals("elseif", tmp)) {
					boolean elseIf = tmp.charAt(0) == 'e';// is elseif
					boolean notClause;
					if (notClause = reader.peak() == '!')
						reader.expect('!');
					reader.readUntil(')', '\\', SH.clear(tmp));
					reader.expect(')');
					reader.expect('$');
					final String key = tmp.toString();
					final BasicStringMaker.Multi trueClause = parseMulti(reader, tmp);
					final StringMaker falseClause;
					StringMaker terminator = trueClause.getTerminator();
					if (terminator == BasicStringMaker.Const.ELSE)
						falseClause = parseMulti(reader, tmp);
					else if (terminator == BasicStringMaker.Const.ENDIF)
						falseClause = null;
					else if (terminator instanceof BasicStringMaker.ElseIf)
						falseClause = trueClause.getTerminator();
					else
						throw new RuntimeException("unknown: " + terminator);
					if (elseIf) {
						if (notClause)
							return new BasicStringMaker.ElseIfNot(key, trueClause, falseClause);
						else
							return new BasicStringMaker.ElseIf(key, trueClause, falseClause);
					} else {
						if (notClause)
							return new BasicStringMaker.IfNot(key, trueClause, falseClause);
						else
							return new BasicStringMaker.If(key, trueClause, falseClause);
					}
				} else if (SH.equals("length", tmp)) {
					reader.readUntil(')', '\\', SH.clear(tmp));
					String key = SH.trim(tmp);
					reader.expect(')');
					reader.expect('$');
					return new BasicStringMaker.Length(key);
				} else if (SH.equals("first", tmp) || SH.equals("rest", tmp)) {
					char ch = tmp.charAt(0);
					reader.readUntil(')', '\\', SH.clear(tmp));
					reader.expect(')');
					reader.expect(':');
					reader.expect('{');
					String key = SH.trim(tmp);
					reader.readUntil('|', SH.clear(tmp));
					reader.expect('|');
					String lcv = SH.trim(tmp);
					Multi inner = parseMulti(reader, tmp);
					reader.expect('}');
					reader.expect('$');
					switch (ch) {
						case 'f':
							return new BasicStringMaker.First(key, lcv, inner);
						case 'r':
							return new BasicStringMaker.Rest(key, lcv, inner);
						default:
							throw new RuntimeException("unknown: " + ch);
					}
				} else
					throw new StringMakerException("Expecting if: " + tmp);
			case ':': // $users:{s|<li>$s$</li>}$
				String key = SH.trim(tmp);
				reader.expect('{');
				reader.readUntil('|', SH.clear(tmp));
				reader.expect('|');
				String lcv = SH.trim(tmp);
				Multi inner = parseMulti(reader, tmp);
				reader.expect('}');
				reader.expect('$');
				return new BasicStringMaker.Loop(key, lcv, inner);
		}
		throw new IllegalStateException("" + c);

	}

	private StringMakerFormatter getFormatter(String format, String args) {
		if (formatterFactory == null) {
			if (format != null)
				throw new StringMakerException("format not supported: " + format);
			return BasicStringMakerFormatter.INSTANCE;
		}
		return formatterFactory.getFormatter(format, args);
	}

	@Override
	public void setFormatterFactory(StringMakerFormatterFactory factory) {
		this.formatterFactory = factory;

	}

	@Override
	public StringMakerFormatterFactory getFormatterFactory() {
		return formatterFactory;
	}
}
