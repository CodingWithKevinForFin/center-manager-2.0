/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.concurrent;

public interface ConcurrentStack<T> {

	public ConcurrentNode<T> pop();

	public void push(ConcurrentNode<T> node);

	public ConcurrentNode<T> peek();

	public int getSize();

	public void reset(ConcurrentNode<T> head, int i);
}
