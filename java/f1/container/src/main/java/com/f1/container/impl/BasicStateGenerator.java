/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl;

import com.f1.base.Action;
import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.Partition;
import com.f1.container.Processor;
import com.f1.container.State;
import com.f1.container.StateGenerator;

public class BasicStateGenerator<S extends State> extends AbstractContainerScope implements StateGenerator<Action, S> {

	final private Class<S> clazz;
	final private Class stateType;
	private ObjectGeneratorForClass<S> stateGenerator;

	public BasicStateGenerator(Class<S> clazz) {
		this(clazz, clazz);
	}

	public BasicStateGenerator(Class<S> clazz, Class stateType) {
		this.clazz = clazz;
		this.stateType = stateType;
	}

	@Override
	public Class getStateType() {
		return stateType;
	}

	@Override
	public void start() {
		super.start();
		this.stateGenerator = getGenerator(this.clazz);
	}

	@Override
	public S createState(Partition partition, Action a, Processor<? extends Action, ?> processor) {
		return stateGenerator == null ? nw(clazz) : stateGenerator.nw();
	}

}
