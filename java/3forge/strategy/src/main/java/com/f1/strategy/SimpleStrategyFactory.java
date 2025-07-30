package com.f1.strategy;

import com.f1.base.ObjectGeneratorForClass;

public class SimpleStrategyFactory<T extends Strategy> implements StrategyFactory<T> {

	final private ObjectGeneratorForClass<T> generator;
	final private String strategyId;

	public SimpleStrategyFactory(String strategyId, ObjectGeneratorForClass<T> factory) {
		if (factory == null)
			throw new NullPointerException("generator");
		if (strategyId == null)
			throw new NullPointerException("strategyId");
		this.generator = factory;
		this.strategyId = strategyId;
	}

	@Override
	public T createStrategy(OrderManager manager) {
		return this.generator.nw();
	}

	@Override
	public String getStrategyId() {
		return strategyId;
	}

}
