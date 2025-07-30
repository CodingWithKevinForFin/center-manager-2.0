/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.concurrent;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import com.f1.base.Clearable;

public class FastNode<T> implements Clearable {
	public static final AtomicReferenceFieldUpdater<FastNode, FastNode> nextFieldUpdater = AtomicReferenceFieldUpdater.newUpdater(FastNode.class,
			FastNode.class, "next");

	volatile FastNode<T> next;
	volatile public T value;
	volatile public boolean in;

	@Override
	public void clear() {
	}
}
