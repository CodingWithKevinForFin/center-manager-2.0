package com.f1.ami.extern;

import org.python.core.PyObject;

import com.f1.utils.OH;
import com.f1.utils.structs.table.derived.DerivedCellMemberMethod;
import com.f1.utils.structs.table.stack.EmptyCalcFrameStack;

public class AmiPyMethod extends PyObject {

	private AmiPyObject target;
	private String methodName;

	public AmiPyMethod(AmiPyObject target, String name) {
		this.target = target;
		this.methodName = name;
	}

	@Override
	public PyObject __call__(PyObject[] args, String[] keywords) {
		Object r;
		if (args.length == 0) {
			DerivedCellMemberMethod<Object> result = target.getMethodFactory().findMemberMethod(this.target.getTarget().getClass(), this.methodName, OH.EMPTY_CLASS_ARRAY);
			r = result.invokeMethod(EmptyCalcFrameStack.INSTANCE, this.target.getTarget(), OH.EMPTY_OBJECT_ARRAY, null);
		} else {
			Object[] jArgs = new Object[args.length];
			Class[] jTypes = new Class[args.length];
			for (int i = 0; i < args.length; i++) {
				Object obj = args[i].__tojava__(Object.class);
				jArgs[i] = obj;
				jTypes[i] = obj == null ? Object.class : obj.getClass();
			}
			DerivedCellMemberMethod<Object> result = target.getMethodFactory().findMemberMethod(this.target.getTarget().getClass(), this.methodName, jTypes);
			r = result.invokeMethod(EmptyCalcFrameStack.INSTANCE, this.target.getTarget(), jArgs, null);
		}

		return AmiPyObject.castToPy(target.getMethodFactory(), r);
	}

}
