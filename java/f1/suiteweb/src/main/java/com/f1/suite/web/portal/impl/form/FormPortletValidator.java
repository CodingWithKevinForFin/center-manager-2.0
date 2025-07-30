package com.f1.suite.web.portal.impl.form;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.string.JavaExpressionParser;
import com.f1.utils.structs.table.derived.BasicDerivedCellParser;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.ChildCalcTypesStack;
import com.f1.utils.structs.table.stack.EmptyCalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class FormPortletValidator implements FormPortletListener, CalcFrame, CalcTypes {
	private final FormPortlet fp;
	private HashMap<FormPortletField, List<Validation>> fieldVM = new HashMap<FormPortletField, List<Validation>>();
	private List<Validation> formVL = new ArrayList<Validation>();
	private FormPortletValidatorListener listener;
	private HashMap<String, FormPortletField<?>> fldMap = new HashMap<String, FormPortletField<?>>();
	private BitSet bs;
	private int n = 0;

	public FormPortletValidator(FormPortlet f) {
		this.fp = f;
		this.fp.addFormPortletListener(this);
	}

	public void add(FormPortletField<?> field, String clause, String reason) {
		List<Validation> vc = fieldVM.get(field);
		if (vc == null) {
			vc = new ArrayList<Validation>();
			fieldVM.put(field, vc);
		}

		vc.add(new Validation(clause, reason, n++));
	}

	public void add(String clause, String reason) {
		formVL.add(new Validation(clause, reason, n++));
	}

	public void compile() {
		bs = new BitSet(n);

		JavaExpressionParser ep = new JavaExpressionParser();
		BasicDerivedCellParser cp = new BasicDerivedCellParser(ep);

		com.f1.utils.structs.table.stack.BasicCalcTypes types = new com.f1.utils.structs.table.stack.BasicCalcTypes();
		for (FormPortletField<?> field : fp.getFormFields()) {
			types.putType(field.getName(), field.getType());
			fldMap.put(field.getName(), field);
		}

		valFormula(cp, this, formVL);

		for (Entry<FormPortletField, List<Validation>> fe : fieldVM.entrySet()) {
			valFormula(cp, this, fe.getValue());
		}

		validate();
	}

	private void validate() {
		if (CH.isEmpty(fieldVM))
			validate((FormPortletField<?>) null);
		for (FormPortletField<?> f : fieldVM.keySet())
			validate(f);
	}

	public void validate(FormPortletField<?> field) {
		if (this.listener == null)
			return;

		if (bs == null)//TODO: is this correct?
			return;

		if (field != null) {
			//validate field
			List<Validation> vl = fieldVM.get(field);
			List<String> reasons = validate(vl);
			this.listener.setValid(field, reasons.size() <= 0, reasons);

			if (field instanceof FormPortletErrorField)
				((FormPortletErrorField) field).setErrors(reasons);
		}

		//validate the form
		List<String> reasons = validate(formVL);
		this.listener.setValid(bs.nextSetBit(0) < 0, reasons);
	}

	private void valFormula(BasicDerivedCellParser cp, com.f1.base.CalcTypes types, List<Validation> vl) {
		ChildCalcTypesStack context = new ChildCalcTypesStack(EmptyCalcFrameStack.INSTANCE, types);
		for (int i = 0; i < vl.size(); i++) {
			Validation v = vl.get(i);
			v.calc = cp.toCalc(v.clause, context);
			if (!Boolean.class.equals(v.calc.getReturnType()))
				throw new IllegalStateException("Formulat " + v.clause + " does not return boolean value");
		}
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		validate(field);
	}

	private List<String> validate(List<Validation> vl) {
		List<String> reasons = Collections.EMPTY_LIST;
		if (vl != null) {
			reasons = new ArrayList(vl.size());
			for (Validation v : vl) {
				if (Boolean.FALSE.equals(v.calc.get(new ReusableCalcFrameStack(EmptyCalcFrameStack.INSTANCE, this)))) {
					reasons.add(v.reason);
					bs.set(v.pos, true);
				} else
					bs.set(v.pos, false);
			}
		}

		return reasons;
	}

	public void setListener(FormPortletValidatorListener l) {
		this.listener = l;
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	public interface FormPortletValidatorListener {
		public void setValid(FormPortletField<?> field, boolean valid, List<String> reasons);
		public void setValid(boolean valid, List<String> reasons);
	}

	protected class Validation {
		final String clause;
		final String reason;
		DerivedCellCalculator calc;
		final int pos;

		public Validation(String clause, String reason, int pos) {
			this.clause = clause;
			this.reason = reason;
			this.pos = pos;

		}
	}

	@Override
	public boolean isVarsEmpty() {
		return fldMap.isEmpty();
	}

	@Override
	public Object getValue(String key) {
		return fldMap.get(key).getValue();
	}

	@Override
	public Object putValue(String key, Object value) {
		return null;
	}

	@Override
	public Iterable<String> getVarKeys() {
		return fldMap.keySet();
	}

	@Override
	public Class<?> getType(String key) {
		return OH.getClass(getValue(key));
	}
	@Override
	public int getVarsCount() {
		return fldMap.size();
	}

}
