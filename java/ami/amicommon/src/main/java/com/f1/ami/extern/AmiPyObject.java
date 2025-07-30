package com.f1.ami.extern;

import org.python.core.Py;
import org.python.core.PyObject;

import com.f1.base.BasicTypes;
import com.f1.utils.OH;
import com.f1.utils.structs.table.derived.MethodFactoryManager;

public class AmiPyObject extends PyObject {

	final private MethodFactoryManager methodFactory;
	final private Object target;

	public AmiPyObject(MethodFactoryManager methodFactory, Object target) {
		this.methodFactory = methodFactory;
		this.target = target;
	}
	@Override
	public PyObject invoke(String name, PyObject[] args) {
		// TODO Auto-generated method stub
		return super.invoke(name, args);
	}
	@Override
	public PyObject __findattr_ex__(String name) {
		return new AmiPyMethod(this, name);
	}
	public MethodFactoryManager getMethodFactory() {
		return methodFactory;
	}
	public Object getTarget() {
		return target;
	}

	@Override
	public Object __tojava__(Class<?> c) {
		return OH.cast(target, c);
	}

	public static PyObject castToPy(MethodFactoryManager mfm, Object r) {
		// Check if the return value is null
		if (r == null)
			return Py.None;
		byte basicType = OH.getBasicType(r.getClass());
		// If the return value is not a basic Java Type Map or Collection, wrap the return value in an AmiPyObject
		if (basicType == BasicTypes.NULL || basicType == BasicTypes.UNDEFINED) {
			return new AmiPyObject(mfm, r);
		} else
			// Otherwise return the PyObject of the result
			return Py.java2py(r);
	}

}
