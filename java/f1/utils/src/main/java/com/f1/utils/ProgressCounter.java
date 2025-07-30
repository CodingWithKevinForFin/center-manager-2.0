package com.f1.utils;

public interface ProgressCounter {
	public void onProgress(int countSinceLast, Object optionalDescription);
}
