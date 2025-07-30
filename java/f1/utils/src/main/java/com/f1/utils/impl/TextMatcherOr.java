package com.f1.utils.impl;

import java.util.Arrays;

import com.f1.utils.AH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;
import com.f1.utils.concurrent.HasherSet;

public class TextMatcherOr implements TextMatcher {

	private HasherSet<CharSequence> values = null;
	private HasherSet<CharSequence> valuesIgnoreCase = null;
	private boolean locked;
	private TextMatcher[] extras = null;

	@Override
	public StringBuilder toString(StringBuilder sink) {
		return SH.join('|', values, sink);
	}

	@Override
	public boolean matches(CharSequence input) {
		locked = true;
		if (values != null && values.contains(input))
			return true;
		if (valuesIgnoreCase != null && valuesIgnoreCase.contains(input))
			return true;
		if (extras != null)
			for (TextMatcher m : extras)
				if (m.matches(input))
					return true;
		return false;
	}

	@Override
	public boolean matches(String input) {
		locked = true;
		if (values != null && values.contains(input))
			return true;
		if (valuesIgnoreCase != null && valuesIgnoreCase.contains(input))
			return true;
		if (extras != null)
			for (TextMatcher m : extras)
				if (m.matches(input))
					return true;
		return false;
	}

	private void addText(boolean ignoreCase, CharSequence text) {
		OH.assertFalse(locked);
		if (ignoreCase) {
			if (valuesIgnoreCase == null)
				valuesIgnoreCase = new HasherSet<CharSequence>(CaseInsensitiveHasher.INSTANCE);
			valuesIgnoreCase.add(text);
		} else {
			if (values == null)
				values = new HasherSet<CharSequence>(BasicHasher.INSTANCE);
			values.add(text);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != TextMatcherOr.class)
			return false;
		TextMatcherOr other = (TextMatcherOr) obj;
		return OH.eq(valuesIgnoreCase, other.valuesIgnoreCase) && OH.eq(values, other.values) && Arrays.equals(this.extras, other.extras);
	}

	private void addAll(TextMatcherOr or) {
		OH.assertFalse(locked);
		if (or.values != null) {
			if (values == null)
				values = new HasherSet<CharSequence>(BasicHasher.INSTANCE);
			values.addAll(or.values);
		}
		if (or.valuesIgnoreCase != null) {
			if (valuesIgnoreCase == null)
				valuesIgnoreCase = new HasherSet<CharSequence>(CaseInsensitiveHasher.INSTANCE);
			valuesIgnoreCase.addAll(or.valuesIgnoreCase);
		}

		if (or.extras != null) {
			if (extras == null)
				this.extras = or.extras.clone();
			else
				this.extras = AH.appendArray(extras, or.extras);
		}
	}
	private void addExtra(TextMatcher r) {
		OH.assertFalse(locked);
		if (extras == null)
			this.extras = new TextMatcher[] { r };
		this.extras = AH.append(extras, r);
	}
	public void addClause(TextMatcher r) {
		if (r instanceof SimpleTextMatcher) {//Or+Text
			SimpleTextMatcher r2 = (SimpleTextMatcher) r;
			addText(r2.ignoreCase(), r2.getText());
		} else if (r instanceof TextMatcherOr) {//Or+Or
			TextMatcherOr r2 = (TextMatcherOr) r;
			addAll(r2);
		} else {//Or+Extra
			addExtra(r);
		}
	}
}
