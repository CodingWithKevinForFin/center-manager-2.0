package com.f1.msg;

import java.io.Closeable;

public interface MsgExternalConnection extends Closeable {

	@Override
	public String toString();

	public String getRemoteProcessUid();
}
