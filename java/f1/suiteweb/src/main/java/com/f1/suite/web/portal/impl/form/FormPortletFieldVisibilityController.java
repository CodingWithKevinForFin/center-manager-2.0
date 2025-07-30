package com.f1.suite.web.portal.impl.form;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.f1.base.CalcFrame;
import com.f1.utils.CH;
import com.f1.utils.string.JavaExpressionParser;
import com.f1.utils.structs.table.derived.BasicDerivedCellParser;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;

public class FormPortletFieldVisibilityController implements FormPortletListener {

	private final FormPortlet fp;
	private List<EnabledClause> ecList = new LinkedList<EnabledClause>();
	private HashMap<String, FormPortletField<?>> fldMap = new HashMap<String, FormPortletField<?>>();

	public FormPortletFieldVisibilityController(FormPortlet f) {
		this.fp = f;
		this.fp.addFormPortletListener(this);
	}

	public void add(FormPortletField<?> field, String clause) {
		if (clause != null) {
			EnabledClause ec = new EnabledClause(field, clause);
			ecList.add(ec);
		}
	}

	public void compile() {
		JavaExpressionParser ep = new JavaExpressionParser();
		BasicDerivedCellParser cp = new BasicDerivedCellParser(ep);

		valFormula(cp, ecList);

		invalidate();
	}

	private void valFormula(BasicDerivedCellParser cp, Collection<EnabledClause> ec) {
		CalcFrame frame = DerivedHelper.toFrame(fldMap);
		CalcFrameStack stack = new TopCalcFrameStack(frame);
		for (EnabledClause v : ec) {
			v.calc = cp.toCalc(v.clause, stack);
			if (!Boolean.class.equals(v.calc.getReturnType()))
				throw new IllegalStateException("Formula " + v.clause + " does not return boolean value");
		}
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		invalidate();
	}

	private void invalidate() {
		invalidate(ecList);
	}

	private void invalidate(Collection<EnabledClause> c) {
		CalcFrame frame = DerivedHelper.toFrame(fldMap);
		CalcFrameStack stack = new TopCalcFrameStack(frame);
		if (CH.isntEmpty(c)) {
			for (EnabledClause ec : c)
				ec.field.setVisible(Boolean.TRUE.equals(ec.calc.get(stack)));
		}
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	protected class EnabledClause {
		final String clause;
		DerivedCellCalculator calc;
		final FormPortletField<?> field;

		public EnabledClause(FormPortletField<?> field, String clause) {
			this.field = field;
			this.clause = clause;
		}
	}

}
