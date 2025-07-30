/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.utils.msg;

import com.f1.base.Action;
import com.f1.container.Partition;
import com.f1.container.Processor;
import com.f1.container.StateGenerator;
import com.f1.container.impl.AbstractContainerScope;

public class ConverterStateGenerator<A extends Action> extends AbstractContainerScope implements StateGenerator<A, ConverterState> {

	private static final Class STATE_TYPE = ConverterState.class;

	@Override
	public Class getStateType() {
		return STATE_TYPE;
	}

	@Override
	public ConverterState createState(Partition partition_, A a_, Processor<? extends A, ?> processor_) {
		return new ConverterState(getServices().getGenerator(), getServices().getConverter());
	}

}
