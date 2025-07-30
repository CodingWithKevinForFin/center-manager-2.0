package com.vortex.agent.filetail;

import java.io.File;

public interface FileTailManagerListener {

	void onOpened(FileTail file, File existing);

	void onRemoved(FileTail file);

	void onReset(FileTail file, File existing);

	void onData(FileTail file, long filePosition, byte[] data, int start, int length, boolean posDup);

}
