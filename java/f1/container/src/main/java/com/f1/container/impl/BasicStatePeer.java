package com.f1.container.impl;

import com.f1.utils.EH;

public class BasicStatePeer {
	public int countProcessAction = 0;
	public int countThrowables = 0;
	public long createdMs = EH.currentTimeMillis();
}
