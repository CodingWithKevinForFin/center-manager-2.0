package com.f1.utils.structs.table.derived;

import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;
import com.f1.utils.impl.PatternTextMatcher;
import com.f1.utils.math.PrimitiveBitwiseMath;
import com.f1.utils.math.PrimitiveMath;
import com.f1.utils.math.PrimitiveMathManager;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorMath implements DerivedCellCalculator {

	public static final int REGEX_OPTIONS = PatternTextMatcher.NO_THROW | PatternTextMatcher.CASE_INSENSITIVE | PatternTextMatcher.DOTALL;
	public static final byte TYPE_STRING_ADD = 1;
	public static final byte TYPE_STRING_EQ = 2;
	public static final byte TYPE_STRING_NE = 3;
	public static final byte TYPE_STRING_LT = 4;
	public static final byte TYPE_STRING_GT = 5;
	public static final byte TYPE_STRING_LE = 6;
	public static final byte TYPE_STRING_GE = 7;
	//	public static final byte TYPE_MATH_FUNC = 8;
	public static final byte TYPE_BOOL_EQ = 9;
	public static final byte TYPE_BOOL_NE = 10;
	public static final byte TYPE_BOOL_OR = 11;
	public static final byte TYPE_BOOL_AND = 12;
	public static final byte TYPE_CONST_FALSE = 13;
	public static final byte TYPE_STRING_REGEX = 14;
	public static final byte TYPE_MATH_EQ = 15;
	public static final byte TYPE_MATH_NE = 16;
	public static final byte TYPE_STRING_JREGEX = 18;
	public static final byte TYPE_STRING_NOT_JREGEX = 33;
	public static final byte TYPE_OBJECT_EQ = 19;
	public static final byte TYPE_OBJECT_NE = 20;
	public static final byte TYPE_MATH_SHIFT_RIGHT = 21;
	public static final byte TYPE_MATH_SHIFT_RIGHT_UNSIGNED = 22;
	public static final byte TYPE_MATH_SHIFT_LEFT = 23;
	public static final byte TYPE_MATH_ADD = 24;
	public static final byte TYPE_MATH_SUBTRACT = 25;
	public static final byte TYPE_MATH_MULTIPLY = 26;
	public static final byte TYPE_MATH_DIVIDE = 27;
	public static final byte TYPE_MATH_MOD = 28;
	public static final byte TYPE_MATH_GT = 29;
	public static final byte TYPE_MATH_LT = 30;
	public static final byte TYPE_MATH_GE = 31;
	public static final byte TYPE_MATH_LE = 32;

	public static final byte TYPE_OBJECT_ADD = 34;//could be string or math
	public static final byte TYPE_OBJECT_LT = 35;
	public static final byte TYPE_OBJECT_GT = 36;
	public static final byte TYPE_OBJECT_LE = 37;
	public static final byte TYPE_OBJECT_GE = 38;
	public static final byte TYPE_OBJECT_OR = 39;
	public static final byte TYPE_OBJECT_AND = 40;
	public static final byte TYPE_OBJECT_SUBTRACT = 47;
	public static final byte TYPE_OBJECT_MULTIPLY = 48;
	public static final byte TYPE_OBJECT_DIVIDE = 49;
	public static final byte TYPE_OBJECT_MOD = 50;
	final private DerivedCellCalculator right;
	final private DerivedCellCalculator left;
	final private PrimitiveMath<?> math;
	final private Class<?> returnType;
	final private byte type;
	final private byte operationCode;
	final private int position;

	private DerivedCellCalculatorMath(DerivedCellCalculatorMath i) {
		right = i.right.copy();
		left = i.left.copy();
		math = i.math;
		returnType = i.returnType;
		type = i.type;
		position = i.position;
		operationCode = i.operationCode;
	}
	final static public DerivedCellCalculator valueOf(int position, byte operation, DerivedCellCalculator left, DerivedCellCalculator right) {
		final PrimitiveMath<?> math;
		final Class<?> returnType;
		final byte type;
		//		final int code;
		//		switch (operation.length()) {
		//			case 1:
		//				code = operation.charAt(0);
		//				break;
		//			case 2:
		//				code = operation.charAt(0) | operation.charAt(1) << 8;
		//				break;
		//			case 3:
		//				code = operation.charAt(0) | operation.charAt(1) << 8 | operation.charAt(2) << 16;
		//				break;
		//			default:
		//				throw new ExpressionParserException(position, "Unknown operation: '" + operation + "'");
		//		}
		Class<?> lrt = left.getReturnType();
		Class<?> rrt = right.getReturnType();
		if (CharSequence.class.isAssignableFrom(lrt) || CharSequence.class.isAssignableFrom(rrt)) {
			if (lrt == Void.class)
				throw new ExpressionParserException(left.getPosition(), "Strings operates not available for void");
			if (rrt == Void.class)
				throw new ExpressionParserException(right.getPosition(), "Strings operates not available for void");
			switch (operation) {
				case OperationNode.OP_PLUS:
					type = TYPE_STRING_ADD;
					returnType = String.class;
					break;
				case OperationNode.OP_EQ_EQ:
					type = TYPE_STRING_EQ;
					returnType = Boolean.class;
					break;
				case OperationNode.OP_BANG_EQ:
					type = TYPE_STRING_NE;
					returnType = Boolean.class;
					break;
				case OperationNode.OP_GT_EQ:
					type = TYPE_STRING_GE;
					returnType = Boolean.class;
					break;
				case OperationNode.OP_LT_EQ:
					type = TYPE_STRING_LE;
					returnType = Boolean.class;
					break;
				case OperationNode.OP_GT:
					type = TYPE_STRING_GT;// >
					returnType = Boolean.class;
					break;
				case OperationNode.OP_LT:
					type = TYPE_STRING_LT;// <
					returnType = Boolean.class;
					break;
				case OperationNode.OP_STAR_EQ:
					type = TYPE_STRING_REGEX;
					if (CharSequence.class.isAssignableFrom(rrt) || rrt == Object.class) {
						returnType = Boolean.class;
					} else
						throw new ExpressionParserException(position, "Operator '*=' requires String on right side");
					break;
				case OperationNode.OP_EQ_TILDE:
					type = TYPE_STRING_JREGEX;
					if (CharSequence.class.isAssignableFrom(rrt) || rrt == Object.class) {
						returnType = Boolean.class;
					} else
						throw new ExpressionParserException(position, "Operator '=~' requires String on right side");
					break;
				case OperationNode.OP_BANG_TILDE:
					type = TYPE_STRING_NOT_JREGEX;
					if (CharSequence.class.isAssignableFrom(rrt) || rrt == Object.class) {
						returnType = Boolean.class;
					} else
						throw new ExpressionParserException(position, "Operator '!~' requires String on right side");
					break;
				case OperationNode.OP_TILDE_TILDE:
					type = TYPE_STRING_REGEX;
					if (CharSequence.class.isAssignableFrom(rrt) || rrt == Object.class) {
						returnType = Boolean.class;
					} else
						throw new ExpressionParserException(position, "Operator '~~' requires String on right side");
					break;
				default:
					throw new ExpressionParserException(position, "Operator '" + OperationNode.toString(operation) + "' not supported for String");
			}
			math = null;
		} else if (lrt == Boolean.class && rrt == Boolean.class) {
			switch (operation) {
				case OperationNode.OP_EQ_EQ:
					type = TYPE_BOOL_EQ;
					break;
				case OperationNode.OP_BANG_EQ:
					type = TYPE_BOOL_NE;
					break;
				case OperationNode.OP_PIPE_PIPE:
					if (left.isConst())
						return toBoolean(left.get(null)) ? new DerivedCellCalculatorConst(position, Boolean.TRUE, Boolean.class) : right;
					else if (right.isConst())
						return toBoolean(right.get(null)) ? new DerivedCellCalculatorConst(position, Boolean.TRUE, Boolean.class) : left;
					type = TYPE_BOOL_OR;
					break;
				case OperationNode.OP_AMP_AMP:
					type = TYPE_BOOL_AND;
					if (left.isConst())
						return toBoolean(left.get(null)) ? right : new DerivedCellCalculatorConst(position, Boolean.FALSE, Boolean.class);
					else if (right.isConst())
						return toBoolean(right.get(null)) ? left : new DerivedCellCalculatorConst(position, Boolean.FALSE, Boolean.class);
					break;
				default:
					throw new ExpressionParserException(position, "Operator '" + OperationNode.toString(operation) + "' not supported for Boolean");
			}
			math = null;
			returnType = Boolean.class;
		} else if (lrt == Character.class && rrt == Character.class) {
			switch (operation) {
				case OperationNode.OP_BANG_EQ:
					type = TYPE_BOOL_NE;
					returnType = Boolean.class;
					break;
				case OperationNode.OP_EQ_EQ:
					type = TYPE_BOOL_EQ;
					returnType = Boolean.class;
					break;
				case OperationNode.OP_PLUS:
					type = TYPE_STRING_ADD;
					returnType = String.class;
					break;
				case OperationNode.OP_GT:
					type = TYPE_STRING_GT;
					returnType = Boolean.class;
					break;
				case OperationNode.OP_LT:
					type = TYPE_STRING_LT;
					returnType = Boolean.class;
					break;
				case OperationNode.OP_GT_EQ:
					type = TYPE_STRING_GE;
					returnType = Boolean.class;
					break;
				case OperationNode.OP_LT_EQ:
					type = TYPE_STRING_LE;
					returnType = Boolean.class;
					break;
				default:
					throw new ExpressionParserException(position, "Unknown operation for Characters: '" + OperationNode.toString(operation) + "'");
			}
			math = null;
		} else {

			math = PrimitiveMathManager.INSTANCE.getNoThrow((Class<? extends Number>) lrt, (Class<? extends Number>) rrt);
			if (math == null) {
				//				if (lrt == rrt || lrt.isAssignableFrom(rrt) || rrt.isAssignableFrom(lrt)) {
				switch (operation) {
					case OperationNode.OP_EQ_EQ:
						type = TYPE_OBJECT_EQ;
						returnType = Boolean.class;
						break;
					case OperationNode.OP_BANG_EQ:
						type = TYPE_OBJECT_NE;
						returnType = Boolean.class;
						break;
					case OperationNode.OP_LT:
						if (!ensureCouldBe(lrt, rrt, Comparable.class) && !ensureCouldBe(lrt, rrt, Number.class))
							throw throwInvalidType(position, operation, lrt, rrt);
						type = TYPE_OBJECT_LT;
						returnType = Boolean.class;
						break;
					case OperationNode.OP_GT:
						if (!ensureCouldBe(lrt, rrt, Comparable.class) && !ensureCouldBe(lrt, rrt, Number.class))
							throw throwInvalidType(position, operation, lrt, rrt);
						type = TYPE_OBJECT_GT;
						returnType = Boolean.class;
						break;
					case OperationNode.OP_LT_EQ:
						if (!ensureCouldBe(lrt, rrt, Comparable.class) && !ensureCouldBe(lrt, rrt, Number.class))
							throw throwInvalidType(position, operation, lrt, rrt);
						type = TYPE_OBJECT_LE;
						returnType = Boolean.class;
						break;
					case OperationNode.OP_GT_EQ:
						if (!ensureCouldBe(lrt, rrt, Comparable.class) && !ensureCouldBe(lrt, rrt, Number.class))
							throw throwInvalidType(position, operation, lrt, rrt);
						type = TYPE_OBJECT_GE;
						returnType = Boolean.class;
						break;
					case OperationNode.OP_PIPE_PIPE:
						if (!ensureCouldBe(lrt, rrt, Boolean.class))
							throw throwInvalidType(position, operation, lrt, rrt);
						type = TYPE_OBJECT_OR;
						returnType = Boolean.class;
						break;
					case OperationNode.OP_AMP_AMP:
						if (!ensureCouldBe(lrt, rrt, Boolean.class))
							throw throwInvalidType(position, operation, lrt, rrt);
						type = TYPE_OBJECT_AND;
						returnType = Boolean.class;
						break;
					case OperationNode.OP_STAR:
						if (!ensureCouldBe(lrt, rrt, Number.class))
							throw throwInvalidType(position, operation, lrt, rrt);
						type = TYPE_OBJECT_MULTIPLY;
						returnType = Number.class;
						break;
					case OperationNode.OP_PLUS:
						if (!ensureCouldBe(lrt, rrt, Number.class))
							throw throwInvalidType(position, operation, lrt, rrt);
						type = TYPE_OBJECT_ADD;
						returnType = Object.class;
						break;
					case OperationNode.OP_MINUS:
						if (!ensureCouldBe(lrt, rrt, Number.class))
							throw throwInvalidType(position, operation, lrt, rrt);
						type = TYPE_OBJECT_SUBTRACT;
						returnType = Number.class;
						break;
					case OperationNode.OP_SLASH:
						if (!ensureCouldBe(lrt, rrt, Number.class))
							throw throwInvalidType(position, operation, lrt, rrt);
						type = TYPE_OBJECT_DIVIDE;
						returnType = Number.class;
						break;
					case OperationNode.OP_PERCENT:
						if (!ensureCouldBe(lrt, rrt, Number.class))
							throw throwInvalidType(position, operation, lrt, rrt);
						type = TYPE_OBJECT_MOD;
						returnType = Number.class;
						break;
					default:
						throw throwInvalidType(position, operation, lrt, rrt);
				}
			} else {
				switch (operation) {
					case OperationNode.OP_GT_GT:
						if (!(math instanceof PrimitiveBitwiseMath))
							throw new ExpressionParserException(position, "operator '" + OperationNode.toString(operation) + "' requires whole numbers");
						returnType = math.getReturnType();
						type = TYPE_MATH_SHIFT_RIGHT;
						break;
					case OperationNode.OP_GT_GT_GT:
						if (!(math instanceof PrimitiveBitwiseMath))
							throw new ExpressionParserException(position, "operator '" + OperationNode.toString(operation) + "' requires whole numbers");
						returnType = math.getReturnType();
						type = TYPE_MATH_SHIFT_RIGHT_UNSIGNED;
						break;
					case OperationNode.OP_LT_LT:
						if (!(math instanceof PrimitiveBitwiseMath))
							throw new ExpressionParserException(position, "operator '" + OperationNode.toString(operation) + "' requires whole numbers");
						returnType = math.getReturnType();
						type = TYPE_MATH_SHIFT_LEFT;
						break;
					case OperationNode.OP_PLUS:
						type = TYPE_MATH_ADD;
						returnType = math.getReturnType();
						break;
					case OperationNode.OP_MINUS:
						type = TYPE_MATH_SUBTRACT;
						returnType = math.getReturnType();
						break;
					case OperationNode.OP_STAR:
						type = TYPE_MATH_MULTIPLY;
						returnType = math.getReturnType();
						break;
					case OperationNode.OP_SLASH:
						type = TYPE_MATH_DIVIDE;
						returnType = math.getReturnType();
						break;
					case OperationNode.OP_PERCENT:
						type = TYPE_MATH_MOD;
						returnType = math.getReturnType();
						break;
					case OperationNode.OP_BANG_EQ:
						type = TYPE_MATH_NE;
						returnType = Boolean.class;
						break;
					case OperationNode.OP_EQ_EQ:
						type = TYPE_MATH_EQ;
						returnType = Boolean.class;
						break;
					case OperationNode.OP_GT:
						type = TYPE_MATH_GT;
						returnType = Boolean.class;
						break;
					case OperationNode.OP_LT:
						type = TYPE_MATH_LT;
						returnType = Boolean.class;
						break;
					case OperationNode.OP_GT_EQ:
						type = TYPE_MATH_GE;
						returnType = Boolean.class;
						break;
					case OperationNode.OP_LT_EQ:
						type = TYPE_MATH_LE;
						returnType = Boolean.class;
						break;
					default:
						throw new ExpressionParserException(position, "Operator '" + OperationNode.toString(operation) + "' not supported for Number");
				}
			}
		}

		DerivedCellCalculatorMath r = new DerivedCellCalculatorMath(position, operation, type, math, returnType, left, right);
		return r.isConst() ? new DerivedCellCalculatorConst(position, r.get(null), r.getReturnType()) : r;
	}
	private static ExpressionParserException throwInvalidType(int position, byte operation, Class<?> lrt, Class<?> rrt) {
		return new ExpressionParserException(position,
				"Operator '" + OperationNode.toString(operation) + "' not supported for: " + lrt.getSimpleName() + " and " + rrt.getSimpleName());
	}

	private static boolean ensureCouldBe(Class<?> lrt, Class<?> rrt, Class<?> type) {
		return ((lrt.isAssignableFrom(type) || type.isAssignableFrom(lrt)) && (rrt.isAssignableFrom(type) || type.isAssignableFrom(rrt)));
	}
	private DerivedCellCalculatorMath(int position, byte operationCode, byte type, PrimitiveMath<?> math, Class returnType, DerivedCellCalculator left,
			DerivedCellCalculator right) {
		this.left = left;
		this.right = right;
		this.position = position;
		this.operationCode = operationCode;
		this.type = type;
		this.returnType = returnType;
		this.math = math;
	}
	@Override
	public Object get(CalcFrameStack key) {
		Object l = left.get(key);
		if (l == null) {
			switch (type) {
				case TYPE_BOOL_AND:
					return false;
				case TYPE_MATH_SHIFT_RIGHT:
				case TYPE_MATH_SHIFT_RIGHT_UNSIGNED:
				case TYPE_MATH_SHIFT_LEFT:
				case TYPE_MATH_ADD:
				case TYPE_MATH_SUBTRACT:
				case TYPE_MATH_MULTIPLY:
				case TYPE_MATH_DIVIDE:
				case TYPE_MATH_MOD:
				case TYPE_MATH_GT:
				case TYPE_MATH_LT:
				case TYPE_MATH_GE:
				case TYPE_MATH_LE:
					return null;
			}
		}
		if (l instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) l, this, key, 0, null);
		switch (type) {
			case TYPE_BOOL_AND:
				if (!toBoolean(l))
					return Boolean.FALSE;
				break;
			case TYPE_BOOL_OR:
				if (toBoolean(l))
					return Boolean.TRUE;
				break;
		}
		Object r = right.get(key);
		if (r instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) r, this, key, 1, l);
		return eval(l, r);
	}
	private Object eval(Object l, Object r) {
		switch (type) {
			case TYPE_BOOL_NE:
				return l != r;
			case TYPE_BOOL_EQ:
				return l == r;
			case TYPE_BOOL_AND:
				return toBoolean(r);//we already checked left node before executing right had side
			case TYPE_BOOL_OR:
				return toBoolean(r);//we already checked left node before executing right had side
			case TYPE_OBJECT_EQ:
				return OH.eq(l, r);
			case TYPE_OBJECT_NE:
				return OH.ne(l, r);
			case TYPE_MATH_EQ:
				return (l == null || r == null) ? l == r : math.compare((Number) l, (Number) r) == 0;
			case TYPE_MATH_NE:
				return (l == null || r == null) ? l != r : math.compare((Number) l, (Number) r) != 0;
			case TYPE_MATH_SHIFT_RIGHT:
				return l == null || r == null ? null : ((PrimitiveBitwiseMath<Number>) math).shiftRight((Number) l, (Number) r);
			case TYPE_MATH_SHIFT_RIGHT_UNSIGNED:
				return l == null || r == null ? null : ((PrimitiveBitwiseMath<Number>) math).shiftRightUnsigned((Number) l, (Number) r);
			case TYPE_MATH_SHIFT_LEFT:
				return l == null || r == null ? null : ((PrimitiveBitwiseMath<Number>) math).shiftLeft((Number) l, (Number) r);
			case TYPE_MATH_ADD:
				return l == null || r == null ? null : math.add((Number) l, (Number) r);
			case TYPE_MATH_SUBTRACT:
				return l == null || r == null ? null : math.subtract((Number) l, (Number) r);
			case TYPE_MATH_MULTIPLY:
				return l == null || r == null ? null : math.multiply((Number) l, (Number) r);
			case TYPE_MATH_DIVIDE:
				return l == null || r == null ? null : math.divide((Number) l, (Number) r);
			case TYPE_MATH_MOD:
				return l == null || r == null ? null : math.mod((Number) l, (Number) r);
			case TYPE_MATH_GT:
				return l == null || r == null ? null : math.compare((Number) l, (Number) r) > 0;
			case TYPE_MATH_LT:
				return l == null || r == null ? null : math.compare((Number) l, (Number) r) < 0;
			case TYPE_MATH_GE:
				return l == null || r == null ? null : math.compare((Number) l, (Number) r) >= 0;
			case TYPE_MATH_LE:
				return l == null || r == null ? null : math.compare((Number) l, (Number) r) <= 0;
			case TYPE_STRING_ADD: {
				if (l == null)
					return r == null ? "" : DerivedHelper.toString(r);
				else if (r == null)
					return DerivedHelper.toString(l);
				return DerivedHelper.toString(l) + DerivedHelper.toString(r);
			}
			case TYPE_STRING_EQ:
				return OH.eq(DerivedHelper.toString(l), DerivedHelper.toString(r));
			case TYPE_STRING_NE:
				return OH.ne(DerivedHelper.toString(l), DerivedHelper.toString(r));
			case TYPE_STRING_LE:
				return OH.le(DerivedHelper.toString(l), DerivedHelper.toString(r), true);
			case TYPE_STRING_GE:
				return OH.ge(DerivedHelper.toString(l), DerivedHelper.toString(r), true);
			case TYPE_STRING_LT:
				return OH.lt(DerivedHelper.toString(l), DerivedHelper.toString(r), true);
			case TYPE_STRING_GT:
				return OH.gt(DerivedHelper.toString(l), DerivedHelper.toString(r), true);
			case TYPE_STRING_REGEX:
			case TYPE_STRING_JREGEX:
			case TYPE_STRING_NOT_JREGEX: {
				final boolean ret = (type == TYPE_STRING_REGEX || type == TYPE_STRING_JREGEX);
				if (r == null)
					return !ret;
				if (l == null)
					return !ret;
				final String r2 = DerivedHelper.toString(r);
				TextMatcherWithRegex pattern = patternCache;
				if (pattern == null || OH.ne(pattern.getRegex(), r2))
					patternCache = (pattern = new TextMatcherWithRegex(r2, toPattern(r2, this.operationCode, true)));
				return pattern.matches(DerivedHelper.toString(l));
			}
			case TYPE_OBJECT_LT: {
				int n = compare(l, r);
				return n == Integer.MIN_VALUE ? null : n < 0;
			}
			case TYPE_OBJECT_GT: {
				int n = compare(l, r);
				return n == Integer.MIN_VALUE ? null : n > 0;
			}
			case TYPE_OBJECT_LE: {
				int n = compare(l, r);
				return n == Integer.MIN_VALUE ? null : n <= 0;
			}
			case TYPE_OBJECT_GE: {
				int n = compare(l, r);
				return n == Integer.MIN_VALUE ? null : n >= 0;
			}
			case TYPE_OBJECT_OR:
				return (l instanceof Boolean && r instanceof Boolean) ? ((Boolean) l).booleanValue() || ((Boolean) r).booleanValue() : null;
			case TYPE_OBJECT_AND:
				return (l instanceof Boolean && r instanceof Boolean) ? ((Boolean) l).booleanValue() && ((Boolean) r).booleanValue() : null;
			case TYPE_OBJECT_ADD:
				if (l instanceof Number && r instanceof Number)
					return PrimitiveMathManager.INSTANCE.get((Number) l, (Number) r).add((Number) l, (Number) r);
				else
					return null;
			case TYPE_OBJECT_SUBTRACT:
				return l instanceof Number && r instanceof Number ? PrimitiveMathManager.INSTANCE.get((Number) l, (Number) r).subtract((Number) l, (Number) r) : null;
			case TYPE_OBJECT_MULTIPLY:
				return l instanceof Number && r instanceof Number ? PrimitiveMathManager.INSTANCE.get((Number) l, (Number) r).multiply((Number) l, (Number) r) : null;
			case TYPE_OBJECT_DIVIDE:
				return l instanceof Number && r instanceof Number ? PrimitiveMathManager.INSTANCE.get((Number) l, (Number) r).divide((Number) l, (Number) r) : null;
			case TYPE_OBJECT_MOD:
				return l instanceof Number && r instanceof Number ? PrimitiveMathManager.INSTANCE.get((Number) l, (Number) r).mod((Number) l, (Number) r) : null;
			default:
				return null;
		}
	}
	private int compare(Object l, Object r) {//return Integer.MIN_VALUE if values can't be compared
		if (l == r)
			return 0;
		else if (l == null)
			return -1;
		else if (r == null)
			return 1;
		else if (l instanceof Number && r instanceof Number)
			return PrimitiveMathManager.INSTANCE.get((Number) l, (Number) r).compare((Number) l, (Number) r);
		else if (l.getClass() == r.getClass() && l instanceof Comparable)
			return ((Comparable) l).compareTo(r);
		else
			return Integer.MIN_VALUE;
	}
	private PrimitiveMath getMathManager(Object l, Object r) {
		return l instanceof Number && r instanceof Number ? PrimitiveMathManager.INSTANCE.get((Number) l, (Number) r) : null;
	}

	private static class TextMatcherWithRegex implements TextMatcher {

		final private String regex;
		final private TextMatcher inner;

		public TextMatcherWithRegex(String regex, TextMatcher inner) {
			this.regex = regex;
			this.inner = inner;
		}

		public boolean matches(CharSequence input) {
			return inner.matches(input);
		}

		public boolean matches(String input) {
			return inner.matches(input);
		}

		public StringBuilder toString(StringBuilder sink) {
			return inner.toString(sink);
		}

		public String getRegex() {
			return regex;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || obj.getClass() != TextMatcherWithRegex.class)
				return false;
			TextMatcherWithRegex other = (TextMatcherWithRegex) obj;
			return OH.eq(regex, other.regex) && OH.eq(inner, other.inner);
		}

	}

	private transient TextMatcherWithRegex patternCache = null;

	private static boolean toBoolean(Object object) {
		return object == null ? false : ((Boolean) object).booleanValue();
	}

	@Override
	public Class<?> getReturnType() {
		return returnType;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append('(');
		left.toString(sink);
		sink.append(' ');
		sink.append(OperationNode.toString(this.operationCode));
		sink.append(' ');
		right.toString(sink);
		sink.append(')');
		return sink;
	}

	//	public String getOperation() {
	//		return operation;
	//	}

	public DerivedCellCalculator getLeft() {
		return left;
	}
	public DerivedCellCalculator getRight() {
		return right;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != DerivedCellCalculatorMath.class)
			return false;
		DerivedCellCalculatorMath o = (DerivedCellCalculatorMath) other;
		return OH.eq(type, o.type) && OH.eq(left, o.left) && OH.eq(right, o.right);
	}

	@Override
	public int hashCode() {
		return OH.hashCode(left, right, type);
	}

	@Override
	public int getPosition() {
		return position;
	}
	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}
	@Override
	public DerivedCellCalculatorMath copy() {
		return new DerivedCellCalculatorMath(this);
	}
	@Override
	public boolean isConst() {
		return left.isConst() && right.isConst();
	}

	public byte getType() {
		return this.type;
	}
	@Override
	public boolean isReadOnly() {
		return this.left.isReadOnly() && this.right.isReadOnly();
	}

	public static final TextMatcher toPattern(final String regex, byte operationCode, boolean threadSafe) {
		switch (operationCode) {
			case OperationNode.OP_EQ_TILDE:
				return new PatternTextMatcher(regex, REGEX_OPTIONS, threadSafe);
			case OperationNode.OP_BANG_TILDE:
				return new PatternTextMatcher(regex, REGEX_OPTIONS | PatternTextMatcher.NOT_MATCH, threadSafe);
			case OperationNode.OP_TILDE_TILDE:
				return SH.m(regex);
			default:
				return null;
		}
	}

	//	public static boolean isPatternOperatio(String operation) {
	//		if (operation.length() == 2 && operation.charAt(1) == '~') {
	//			char c = operation.charAt(0);
	//			switch (c) {
	//				case '=':
	//				case '~':
	//				case '!':
	//					return true;
	//			}
	//		}
	//		return false;
	//	}

	public byte getOperationType() {
		return this.type;
	}
	public byte getOperationNodeCode() {
		return this.operationCode;
	}
	@Override
	public Object resume(PauseStack paused) {
		Object l, r;
		if (paused.getState() == 0) {
			l = paused.getNext().resume();
			if (l instanceof FlowControlPause)
				return DerivedHelper.onFlowControl((FlowControlPause) l, this, paused.getLcvs(), 0, null);
			switch (type) {
				case TYPE_BOOL_AND:
					if (!toBoolean(l))
						return Boolean.FALSE;
					break;
				case TYPE_BOOL_OR:
					if (toBoolean(l))
						return Boolean.TRUE;
					break;
			}
			r = right.get(paused.getLcvs());
		} else { //state==1
			l = paused.getAttachment();
			r = paused.getNext().resume();
		}
		if (r instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) r, this, paused.getLcvs(), 1, l);
		return eval(l, r);
	}
	@Override
	public int getInnerCalcsCount() {
		return 2;
	}
	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		return n == 0 ? left : right;
	}
	@Override
	public boolean isPausable() {
		return false;
	}
	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (other.getClass() != this.getClass())
			return false;
		DerivedCellCalculatorMath o = (DerivedCellCalculatorMath) other;
		return OH.eq(this.type, o.type) && DerivedHelper.areSame(this.left, o.left) && DerivedHelper.areSame(this.right, o.right);
	}
	public String getOperationString() {
		return OperationNode.toString(getOperationNodeCode());
	}
}
