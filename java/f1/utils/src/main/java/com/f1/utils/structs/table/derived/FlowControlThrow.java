package com.f1.utils.structs.table.derived;

import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.Tuple2;

public class FlowControlThrow extends RuntimeException {

	public static class Frame {
		private DerivedCellCalculator position;
		private String originalSourceCode;
		private String originalSourceCodeLabel;
		private Frame next;
		private Frame prior;
		private int lineNumber = -2;
		private int lineNumberOffset = -2;

		public Frame(DerivedCellCalculator position) {
			this.position = position;

		}
		public String getCallSourceCode() {
			return originalSourceCode;
		}
		public String getCallSourceLabel() {
			return originalSourceCodeLabel;
		}
		public void setOriginalSourceCode(String label, String originalSourceCode) {
			OH.assertNull(this.originalSourceCode);
			OH.assertNotNull(originalSourceCode);
			this.originalSourceCode = originalSourceCode;
			this.originalSourceCodeLabel = label;
		}

		public DerivedCellCalculator getPosition() {
			return this.position;
		}

		public Frame getNext() {
			return next;
		}
		public void setPosition(DerivedCellCalculator position) {
			OH.assertNull(this.position);
			this.position = position;
		}

		public String getCallDescription(MethodFactoryManager mf) {
			return getCallDescription(mf, new StringBuilder()).toString();
		}
		public StringBuilder getCallDescription(MethodFactoryManager mf, StringBuilder sb) {
			if (position instanceof DerivedCellCalculatorMethod) {
				DerivedCellCalculatorMethod m = (DerivedCellCalculatorMethod) position;
				m.getDefinition().toString(mf, sb, false, false, true, true);
			} else if (position instanceof DerivedCellCalculatorMemberMethod) {
				DerivedCellCalculatorMemberMethod m = (DerivedCellCalculatorMemberMethod) position;
				m.getDefinition().toString(mf, sb, false, false, true, true);
			} else if (position != null)
				position.toString(sb);
			return sb;
		}
		public int getCallLineNumber() {
			initLineNumbers();
			return lineNumber;
		}
		public int getCallLineNumberOffset() {
			initLineNumbers();
			return lineNumberOffset;
		}
		private void initLineNumbers() {
			if (lineNumber != -2)
				return;
			else if (this.position == null || this.originalSourceCode == null) {
				lineNumber = -1;
				lineNumberOffset = -1;
			} else {
				Tuple2<Integer, Integer> pos = SH.getLinePosition(getCallSourceCode(), getPosition().getPosition());
				lineNumber = pos.getA() + 1;
				lineNumberOffset = pos.getB();
			}
		}
		public Frame getPrior() {
			return this.prior;
		}
		public int getCallCursorPosition() {
			return this.position == null ? -1 : this.position.getPosition();
		}
	}

	final private Object returnValue;
	private Frame head;
	private Frame tail;

	public FlowControlThrow(DerivedCellCalculator dcc, Object returnValue) {
		this.returnValue = returnValue;
		this.tail = this.head = new Frame(dcc);
	}

	public FlowControlThrow(DerivedCellCalculator dcc, Object returnValue, Throwable cause) {
		super(cause);
		this.returnValue = returnValue;
		this.tail = this.head = new Frame(dcc);
	}

	public FlowControlThrow(Object returnValue) {
		this.returnValue = returnValue;
		this.tail = this.head = new Frame(null);
	}

	public RuntimeException toRuntimeException() {
		Object r = getThrownValue();
		if (r instanceof Throwable)
			return OH.toRuntime((Throwable) r);
		return new RuntimeException(OH.toString(r));
	}
	public Object getThrownValue() {
		return this.returnValue;
	}

	@Override
	public String getMessage() {
		if (returnValue instanceof Throwable) {
			Throwable exe = (Throwable) returnValue;
			if (exe.getMessage() != null)
				return exe.getMessage();
		} else if (returnValue == null)
			return "null";
		return returnValue.toString();
	}

	public Frame getHeadFrame() {
		return this.head;
	}
	public Frame getTailFrame() {
		return this.tail;
	}
	public DerivedCellCalculator getPosition() {
		return this.tail.position;
	}

	public Frame addFrame(DerivedCellCalculator position) {
		Frame f = new Frame(position);
		if (head == null)
			this.head = f;
		else {
			this.tail.next = f;
			f.prior = this.tail;
		}
		this.tail = f;
		return f;
	}
	@Override
	public String toString() {
		if (getCause() != null && (!(getCause() instanceof ExpressionParserException)))
			return new StringBuilder(getMessage()).append(" <-- ").append(getCause()).toString();
		else
			return getMessage();
	}

	public String toStackString(MethodFactoryManager mf) {
		StringBuilder sb = new StringBuilder();
		int depth = 0;
		for (Frame i = this.tail; i != null; i = i.prior) {
			if (depth > 0) {
				SH.repeat(' ', depth * 2, sb);
				sb.append("+-> ");
			}
			depth++;
			if (i.getCallSourceLabel() == null)
				sb.append("ANONYMOUS");
			else
				sb.append(i.getCallSourceLabel());
			sb.append(":");
			int lineNumber = i.getCallLineNumber();
			if (lineNumber != -1)
				sb.append(lineNumber).append(':').append(i.getCallLineNumber());
			else
				sb.append("?:?");
			sb.append(' ');
			i.getCallDescription(mf, sb);

			sb.append('\n');
		}
		return sb.toString();
	}

	@Override
	public synchronized Throwable getCause() {
		return this.returnValue instanceof Throwable ? (Throwable) this.returnValue : super.getCause();
	}

}