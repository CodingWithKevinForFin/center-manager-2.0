package com.f1.container;

import java.util.Iterator;

import com.f1.base.Action;

/**
 * 
 * A specialized processor that will receive a batch of actions for processing. In some cases, this is more efficient than processing single messages at a tiem.
 * 
 * @param <A>
 * @param <S>
 */
public interface MultiProcessor<A extends Action, S extends State> extends Processor<A, S> {

	void processActions(Iterator<A> actions, S state, ThreadScope threadScope) throws Exception;

}
