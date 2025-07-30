/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.concurrent;

import com.f1.base.ObjectGeneratorForClass;

public class GlobalObjectPool<T extends ConcurrentNode> {
	final private PushStackStack<T> stack = new PushStackStack<T>();

	final public ObjectGeneratorForClass<T> generator;

	final private int localSize;

	final private int globalSize;

	public GlobalObjectPool(ObjectGeneratorForClass<T> generator, int localSize, int globalSize) {
		this.generator = generator;
		this.localSize = localSize;
		this.globalSize = globalSize;
	}

	public void push(ConcurrentStack<T> source) {
		stack.push(source);
	}

	// replaces the contents of sink public void pop(FastStack<T> sink) {
	public void pop(ConcurrentStack<T> sink) {
		stack.pop(sink);
	}

	public int getSize() {
		return stack.getSize();
	}

	public T nw() {
		return generator.nw();
	}

	public int getLocalSize() {
		return localSize;
	}

	public int getGlobalSize() {
		return globalSize;
	}
}
