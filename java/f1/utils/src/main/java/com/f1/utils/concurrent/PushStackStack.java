/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

public class PushStackStack<NODE extends ConcurrentNode> {

	FastStack<ConcurrentStack> unused = new FastStack<ConcurrentStack>();
	FastStack<ConcurrentStack> stacks = new FastStack<ConcurrentStack>();
	AtomicInteger size = new AtomicInteger();

	// drains the contents out of source
	public void push(ConcurrentStack<NODE> source) {
		size.addAndGet(source.getSize());
		ConcurrentStack stack = (ConcurrentStack) unused.pop();
		if (stack == null)
			stack = new FastStack<NODE>();
		stack.reset(source.peek(), source.getSize());
		source.reset(null, 0);
		stacks.push((ConcurrentNode) stack);
	}

	// replaces the contents of sink
	public void pop(ConcurrentStack<NODE> sink) {
		ConcurrentStack stack = (ConcurrentStack) stacks.pop();
		if (stack == null) {
			sink.reset(null, 0);
			return;
		}
		sink.reset(stack.peek(), stack.getSize());
		size.addAndGet(-stack.getSize());
		unused.push((ConcurrentNode<ConcurrentStack>) stack);
	}

	public int getSize() {
		return size.get();
	}

}
