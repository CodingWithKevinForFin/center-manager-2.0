/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.concurrent;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import com.f1.base.Clearable;

public class ConcurrentNode<T> implements Clearable {
	public static final AtomicReferenceFieldUpdater<ConcurrentNode, ConcurrentNode> nextFieldUpdater = AtomicReferenceFieldUpdater.newUpdater(
			ConcurrentNode.class, ConcurrentNode.class, "next");

	volatile ConcurrentNode<T> next;
	volatile public T value;

	@Override
	public void clear() {
	}

	public ConcurrentNode() {

	}

	public ConcurrentNode(T value) {
		this.value = value;

	}
}
