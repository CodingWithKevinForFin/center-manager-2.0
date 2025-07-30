package com.f1.utils;

import java.io.IOException;

public interface FastRandomAccessFileListener {

	public void onUsed(FastRandomAccessFile file) throws IOException;
	public void onOpened(FastRandomAccessFile file) throws IOException;
	public void onClosed(FastRandomAccessFile file) throws IOException;

}
