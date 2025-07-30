package com.f1.strategy;

public interface StrategyFactory<T extends Strategy> {

	public T createStrategy(OrderManager manager);

	public String getStrategyId();
}
