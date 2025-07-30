package com.f1.utils.structs.table.stack;

import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.utils.structs.table.derived.MethodFactoryManager;

public class ChildCalcTypesStack implements CalcTypesStack {

	final private CalcTypesStack stack;
	final private CalcTypes frameTypes;
	final private MethodFactoryManager methodFactory;
	private boolean isParentVisible;

	public ChildCalcTypesStack(CalcTypes frame) {
		this(EmptyCalcFrameStack.INSTANCE, frame);
	}
	public ChildCalcTypesStack(CalcTypesStack stack, CalcTypes frame) {
		this(stack, true, frame, stack.getFactory());
	}
	public ChildCalcTypesStack(CalcTypesStack stack, CalcTypes frame, MethodFactoryManager methodFactory) {
		this(stack, true, frame, methodFactory);
	}
	public ChildCalcTypesStack(CalcTypesStack stack, boolean isParentVisible, CalcTypes frame) {
		this(stack, isParentVisible, frame, stack.getFactory());
	}
	public ChildCalcTypesStack(CalcTypesStack stack, boolean isParentVisible, CalcTypes frame, MethodFactoryManager methodFactory) {
		this.stack = stack;
		this.frameTypes = frame;
		this.methodFactory = methodFactory;
		this.isParentVisible = isParentVisible;
	}
	@Override
	public CalcFrame getGlobalConsts() {
		return this.stack == null ? EmptyCalcFrame.INSTANCE : this.stack.getGlobalConsts();
	}
	@Override
	public MethodFactoryManager getFactory() {
		return methodFactory;
	}
	@Override
	public CalcTypes getFrame() {
		return frameTypes;
	}
	@Override
	public boolean isParentVisible() {
		return this.isParentVisible;
	}
	@Override
	public CalcTypesStack getParent() {
		return this.stack;
	}
	@Override
	public CalcFrame getFrameConsts() {
		return EmptyCalcFrame.INSTANCE;
	}
	@Override
	public CalcTypes getGlobal() {
		return stack == null ? EmptyCalcFrame.INSTANCE : stack.getGlobal();
	}
	@Override
	public CalcTypesStack getTop() {
		return this.stack == null ? this : this.stack.getTop();
	}

}
