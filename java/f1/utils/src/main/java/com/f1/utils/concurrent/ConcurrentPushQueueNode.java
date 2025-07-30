/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.concurrent;

import com.f1.base.Clearable;

public class ConcurrentPushQueueNode implements Clearable {

	volatile public ConcurrentPushQueueNode next;

	@Override
	public void clear() {
	}

}
