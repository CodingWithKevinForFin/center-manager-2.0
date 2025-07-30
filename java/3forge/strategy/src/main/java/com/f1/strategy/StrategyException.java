package com.f1.strategy;

public class StrategyException extends RuntimeException{

	public StrategyException(RuntimeException e) {
		super(e);
		
	}
	public StrategyException(String e){
		super(e);
	}
	public StrategyException(){
		super();
	}

}
