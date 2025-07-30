package com.f1.utils.impl;

import com.f1.utils.OH;
import com.f1.utils.TextMatcher;

public class ConditionalMatcher {

	public static class Not implements TextMatcher {
		final private TextMatcher inner;

		public Not(TextMatcher inner) {
			this.inner = inner;
		}

		@Override
		public boolean matches(CharSequence input) {
			return !inner.matches(input);
		}

		@Override
		public boolean matches(String input) {
			return !inner.matches(input);
		}

		@Override
		public String toString() {
			return toString(new StringBuilder()).toString();
		}

		@Override
		public StringBuilder toString(StringBuilder sink) {
			sink.append("!(");
			inner.toString(sink);
			sink.append(')');
			return sink;

		}
		@Override
		public boolean equals(Object obj) {
			if (obj == null || obj.getClass() != Not.class)
				return false;
			Not other = (Not) obj;
			return OH.eq(inner, other.inner);
		}

	}

	public static class And implements TextMatcher {
		final private TextMatcher left;
		final private TextMatcher right;

		public And(TextMatcher left, TextMatcher right) {
			this.left = left;
			this.right = right;
		}

		@Override
		public boolean matches(CharSequence input) {
			return left.matches(input) && right.matches(input);
		}
		@Override
		public boolean matches(String input) {
			return left.matches(input) && right.matches(input);
		}
		@Override
		public String toString() {
			return toString(new StringBuilder()).toString();
		}
		@Override
		public StringBuilder toString(StringBuilder sink) {
			sink.append('(');
			left.toString(sink);
			sink.append(")&(");
			right.toString(sink);
			sink.append(')');
			return sink;
		}

		public boolean equals(Object obj) {
			if (obj == null || obj.getClass() != And.class)
				return false;
			And other = (And) obj;
			return OH.eq(left, other.left) && OH.eq(right, other.right);
		}

	}

	//	public static class Or implements TextMatcher {
	//		final private TextMatcher left;
	//		final private TextMatcher right;
	//
	//		public Or(TextMatcher left, TextMatcher right) {
	//			this.left = left;
	//			this.right = right;
	//		}
	//
	//		@Override
	//		public boolean matches(CharSequence input) {
	//			return left.matches(input) || right.matches(input);
	//		}
	//
	//		@Override
	//		public boolean matches(String input) {
	//			return left.matches(input) || right.matches(input);
	//		}
	//
	//		@Override
	//		public String toString() {
	//			return toString(new StringBuilder()).toString();
	//		}
	//
	//		@Override
	//		public StringBuilder toString(StringBuilder sink) {
	//			sink.append('(');
	//			left.toString(sink);
	//			sink.append(")|(");
	//			right.toString(sink);
	//			sink.append(')');
	//			return sink;
	//		}
	//
	//		public TextMatcher getLeft() {
	//			return left;
	//		}
	//		public TextMatcher getRight() {
	//			return right;
	//		}
	//
	//		@Override
	//		public boolean equals(Object obj) {
	//			if (obj == null || obj.getClass() != Or.class)
	//				return false;
	//			Or other = (Or) obj;
	//			return OH.eq(left, other.left) && OH.eq(right, other.right);
	//		}
	//
	//	}

	public static class If implements TextMatcher {
		final private TextMatcher condition;
		final private TextMatcher left;
		final private TextMatcher right;

		public If(TextMatcher condition, TextMatcher left, TextMatcher right) {
			this.condition = condition;
			this.left = left;
			this.right = right;
		}

		@Override

		public boolean matches(CharSequence input) {
			return condition.matches(input) ? left.matches(input) : right.matches(input);
		}

		@Override

		public boolean matches(String input) {
			return condition.matches(input) ? left.matches(input) : right.matches(input);
		}

		@Override

		public String toString() {
			return toString(new StringBuilder()).toString();
		}

		@Override

		public StringBuilder toString(StringBuilder sink) {
			sink.append('(');
			condition.toString(sink);
			sink.append(")?(");
			left.toString(sink);
			sink.append("):(");
			right.toString(sink);
			sink.append(')');
			return sink;
		}
		public TextMatcher getLeft() {
			return left;
		}
		public TextMatcher getRight() {
			return right;
		}
		@Override
		public boolean equals(Object obj) {
			if (obj == null || obj.getClass() != If.class)
				return false;
			If other = (If) obj;
			return OH.eq(condition, other.condition) && OH.eq(left, other.left) && OH.eq(right, other.right);
		}

	}

}