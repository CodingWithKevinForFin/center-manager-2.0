package com.f1.utils.structs.table.derived;

import com.f1.utils.AH;
import com.f1.utils.SH;
import com.f1.utils.string.node.StringTemplateNode;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.StringTemplateCalcFrameStack;

public class DerivedCellCalculatorStringTemplate implements DerivedCellCalculator {

	private final DerivedCellCalculator[] params;
	private final char[] escapeQuotes;
	private final boolean isNested;//is this inside another string template,ex :  ${for(...)}i am inside${}

	public DerivedCellCalculatorStringTemplate(DerivedCellCalculator params[], char[] escapeQuotes, boolean isNested) {
		this.params = params;
		this.escapeQuotes = escapeQuotes;
		this.isNested = isNested;
	}
	@Override
	public Object get(CalcFrameStack lcvs) {
		return get(lcvs, false);
	}

	public Object get(CalcFrameStack lcvs, boolean escapeSc) {
		final StringBuilder sb;
		for (CalcFrameStack t = lcvs;;) {
			if (t instanceof StringTemplateCalcFrameStack) {
				sb = ((StringTemplateCalcFrameStack) t).getStringBuilder();
				break;
			} else if (t.isParentVisible())
				t = t.getParent();
			else {
				lcvs = new StringTemplateCalcFrameStack(this, lcvs, sb = new StringBuilder());
				break;
			}
		}
		return eval(lcvs, 0, escapeSc, sb);
	}
	private Object eval(CalcFrameStack lcvs, int pos, boolean escapeSc, StringBuilder sb) {
		while (pos < params.length) {
			DerivedCellCalculator dcc = params[pos];
			Object o = dcc.get(lcvs);
			if (o instanceof FlowControlPause) {
				Tuple2<Boolean, StringBuilder> attachment = new Tuple2<Boolean, StringBuilder>(escapeSc, sb);
				return DerivedHelper.onFlowControl((FlowControlPause) o, this, lcvs, pos, attachment);
			}
			eval2(pos, escapeSc, sb, o);
			pos++;
		}
		return sb.toString();
	}
	private void eval2(int pos, boolean escapeSc, StringBuilder sb, Object o) {
		if (params[pos].getReturnType() != Void.class) {
			char escape = escapeQuotes[pos];
			if (escape == StringTemplateNode.NO_ESCAPE) {
				if (o != null) {
					DerivedHelper.toString(o, sb);
				}
			} else {
				sb.append(escape);
				if (o != null) {
					if (escapeSc)
						escapeSc(SH.s(o), sb, escape, escapeSc);
					else
						SH.toStringEncode(SH.s(o), escape, sb);
				}
				sb.append(escape);
			}
		}
	}
	static public StringBuilder escapeSc(String s, StringBuilder sink, char escape, boolean escapeSc) {
		if (s != null) {
			for (int i = 0, n = s.length(); i < n; i++) {
				char c = s.charAt(i);
				if (c == escape) {
					sink.append('\\');
					sink.append(c);
					continue;
				}
				switch (c) {
					case '\\':
						sink.append("\\\\");
						continue;
					case ';':
						if (escapeSc) {
							sink.append("\\;");
						} else
							sink.append(";");
						continue;
					case '$':
						if (escapeSc) {
							sink.append("\\$");
						} else
							sink.append("$");
						continue;
					case SH.CHAR_TAB:
						sink.append("\\t");
						continue;
					case SH.CHAR_NEWLINE:
						sink.append("\\n");
						continue;
					case SH.CHAR_BACKSPACE:
						sink.append("\\r");
						continue;
					case SH.CHAR_FORMFEED:
						sink.append("\\n");
						continue;
					case SH.CHAR_RETURN:
						sink.append("\\r");
						continue;
				}
				if (SH.isntUnicode(c))
					SH.rightAlign('0', Integer.toHexString(c), 4, true, sink.append("\\u"));
				else
					sink.append(c);
			}
		}

		return sink;
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		for (DerivedCellCalculator i : params)
			i.toString(sink);
		return sink;
	}

	@Override
	public Class<?> getReturnType() {
		return String.class;
	}

	@Override
	public int getPosition() {
		return params[0].getPosition();
	}

	@Override
	public DerivedCellCalculator copy() {
		return new DerivedCellCalculatorStringTemplate(DerivedHelper.copy(params), this.escapeQuotes.clone(), isNested);
	}
	@Override
	public boolean isConst() {
		for (DerivedCellCalculator i : this.params)
			if (!i.isConst())
				return false;
		return true;
	}

	@Override
	public boolean isReadOnly() {
		return false;//TODO
	}

	public String toString() {
		return toString(new StringBuilder()).toString();
	}
	@Override
	public Object resume(PauseStack paused) {
		Object r = paused.getNext().resume();
		if (r instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) r, this, paused.getLcvs(), paused.getState(), paused.getAttachment());
		Tuple2<Boolean, StringBuilder> attachment = (Tuple2<Boolean, StringBuilder>) paused.getAttachment();
		eval2(paused.getState(), attachment.getA(), attachment.getB(), r);//handle current
		return eval(paused.getLcvs(), paused.getState() + 1, attachment.getA(), attachment.getB());//do rest
	}
	@Override
	public int getInnerCalcsCount() {
		return params.length;
	}
	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		return params[n];
	}
	@Override
	public boolean isPausable() {
		return false;
	}
	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (other.getClass() != this.getClass())
			return false;
		DerivedCellCalculatorStringTemplate o = (DerivedCellCalculatorStringTemplate) other;
		return isNested == o.isNested && DerivedHelper.areSame(this.params, o.params) && AH.eq(this.escapeQuotes, o.escapeQuotes);
	}

}
