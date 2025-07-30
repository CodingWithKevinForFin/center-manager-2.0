package com.f1.utils.structs.table.derived;

import java.util.ArrayList;
import java.util.List;

import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorMemberMethod implements MemberMethodDerivedCellCalculator, DerivedCellCalculator {

	private int position;
	private String methodName;
	private DerivedCellCalculator target;
	private DerivedCellCalculator[] arguments;
	private DerivedCellMemberMethod memberMethod;

	public DerivedCellCalculatorMemberMethod(int position, DerivedCellCalculator target, String methodName, DerivedCellCalculator[] arguments, MethodFactoryManager mFactory) {
		this.position = position;
		this.methodName = methodName;
		this.arguments = arguments;
		this.target = target;
		Class<?> types[] = new Class[arguments.length];
		for (int i = 0; i < arguments.length; i++) {
			DerivedCellCalculator arg = arguments[i];
			if (arg == null)
				continue;
			if (arg.getReturnType() == Object.class && arg instanceof DerivedCellCalculatorConst && arg.get(null) == null)
				types[i] = Void.class;
			else
				types[i] = arg.getReturnType();
		}
		this.memberMethod = mFactory.findMemberMethod(this.target.getReturnType(), methodName, types);
		if (this.memberMethod == null) {
			List<DerivedCellMemberMethod<Object>> candidates = new ArrayList<DerivedCellMemberMethod<Object>>();
			mFactory.getMemberMethods(this.target.getReturnType(), methodName, candidates);
			StringBuilder sb = new StringBuilder();
			if (CH.isEmpty(candidates)) {
				sb.append("No such member method: ");
			} else {
				sb.append("Argument mismatch: ");
			}
			String name = mFactory.forType(this.target.getReturnType());
			sb.append(name);
			sb.append("::").append(methodName).append("(");
			for (int i = 0; i < types.length; i++) {
				if (i > 0)
					sb.append(',');
				String t = mFactory.forType(types[i]);
				sb.append(t);
			}
			sb.append(")");

			if (CH.isntEmpty(candidates)) {
				sb.append(" ==> expecting ");
				for (int i = 0; i < candidates.size(); i++) {
					if (i > 0)
						sb.append(" or ");
					candidates.get(i).toString(sb, mFactory);
				}
			}

			throw new ExpressionParserException(position, sb.toString());
		}
	}
	public DerivedCellCalculatorMemberMethod(int position, DerivedCellCalculator target, String methodName, DerivedCellCalculator[] arguments,
			DerivedCellMemberMethod memberMethod) {
		this.position = position;
		this.methodName = methodName;
		this.arguments = arguments;
		this.target = target;
		this.memberMethod = memberMethod;
	}

	@Override
	public Object get(CalcFrameStack lcvs) {
		return run(-1, lcvs, null, null, null);

	}
	private Object run(int state, CalcFrameStack lcvs, PauseStack paused, Object targetObject, Object[] params) {
		if (state == -1) {
			if (paused != null) {
				targetObject = paused.resume();
				paused = null;
			} else
				targetObject = target.get(lcvs);
			if (targetObject == null)
				return null;
			if (targetObject instanceof FlowControlPause) {
				((FlowControlPause) targetObject).push(this, lcvs, state, new Object[] { targetObject, params, null });
				return targetObject;
			}
			state = 0;
		}
		if (params == null) {
			if (AH.isEmpty(arguments))
				params = OH.EMPTY_OBJECT_ARRAY;
			else
				params = new Object[arguments.length];
		}

		for (; state < params.length; state++) {
			Object p;
			if (paused != null) {
				p = paused.resume();
				paused = null;
			} else
				p = this.arguments[state].get(lcvs);
			if (p instanceof FlowControlPause)
				return DerivedHelper.onFlowControl((FlowControlPause) p, this, lcvs, state, new Object[] { targetObject, params, null });
			params[state] = p;
		}
		try {
			Object r = paused != null ? paused.resume() : memberMethod.invokeMethod(lcvs, targetObject, params, this);
			if (r instanceof FlowControlPause) {
				if (!memberMethod.isPausable())
					throw new IllegalStateException("Can not return pause: " + memberMethod);
				return DerivedHelper.onFlowControl((FlowControlPause) r, this, lcvs, state, new Object[] { targetObject, params, r });
				//				if (r instanceof FlowControlPause)
				//					((FlowControlPause) r).push(this, lcvs, state, new Object[] { targetObject, params, r });
				//				else if (r instanceof FlowControlThrow) {
				//					FlowControlThrow fct = (FlowControlThrow) r;
				//					FlowControlThrow r2 = new FlowControlThrow(this, null, fct);
				//					if (fct.getOriginalSourceCode() != null)
				//						r2.setOriginalSourceCode(fct.getOriginalSourceCode());
				//					return r2;
				//				}
			}
			return r;
		} catch (Throwable e) {
			throw DerivedHelper.onThrowable(this, e);
			//			throw new ExpressionParserException(position, "Runtime Exception in " + memberMethod.getMethodName() + "(...) ==> " + e.getMessage(), e);
		}
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		target.toString(sink);
		sink.append('.').append(methodName);
		sink.append('(');
		if (AH.isntEmpty(arguments))
			SH.join(',', this.arguments, sink);
		sink.append(')');
		return sink;
	}

	@Override
	public Class<?> getReturnType() {
		return this.memberMethod.getReturnType();
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public DerivedCellCalculator copy() {
		return new DerivedCellCalculatorMemberMethod(position, target.copy(), methodName, DerivedHelper.copy(arguments), memberMethod);
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public boolean isReadOnly() {
		if (!this.memberMethod.isReadOnly())
			return false;
		for (DerivedCellCalculator i : this.arguments)
			if (!i.isReadOnly())
				return false;
		return true;
	}

	public String toString() {
		return toString(new StringBuilder()).toString();
	}
	@Override
	public Object resume(PauseStack paused) {
		if (paused.getState() == -1)
			return run(-1, paused.getLcvs(), paused.getNext(), null, null);
		Object[] t = (Object[]) paused.getAttachment();
		Object target = t[0];
		Object params[] = (Object[]) t[1];
		FlowControlPause fp = (FlowControlPause) t[2];
		if (paused.getState() == params.length) {
			Object r = this.memberMethod.resumeMethod(paused.getLcvs(), target, params, paused, fp, this);
			if (r instanceof FlowControlPause) {
				return DerivedHelper.onFlowControl((FlowControlPause) r, this, paused.getLcvs(), paused.getState(), new Object[] { target, params, r });
			}
			return r;
		} else
			return run(paused.getState(), paused.getLcvs(), paused.getNext(), target, params);

	}
	@Override
	public int getParamsCount() {
		return this.arguments.length;
	}
	@Override
	public DerivedCellCalculator getParamAt(int n) {
		return this.arguments[n];
	}
	@Override
	public int getInnerCalcsCount() {
		return 1 + this.arguments.length;
	}
	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		return n == 0 ? this.target : this.arguments[n - 1];
	}
	@Override
	public String getMethodName() {
		return this.methodName;
	}
	@Override
	public ParamsDefinition getDefinition() {
		return memberMethod.getParamsDefinition();
	}
	@Override
	public DerivedCellCalculator getTarget() {
		return target;
	}
	@Override
	public boolean isPausable() {
		return memberMethod.isPausable();
	}
	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (other.getClass() != this.getClass())
			return false;
		DerivedCellCalculatorMemberMethod o = (DerivedCellCalculatorMemberMethod) other;
		return OH.eq(this.methodName, o.methodName) && DerivedHelper.areSame(target, o.target) && DerivedHelper.areSame(arguments, o.arguments);
	}

}
