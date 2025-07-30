package com.f1.suite.web;

import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

import com.f1.utils.CachedFile;
import com.f1.utils.CopyOnWriteHashMap;
import com.f1.utils.concurrent.FastThreadPool;

public class TestWeb2 {

	static ConcurrentMap<String, CachedFile> files = new CopyOnWriteHashMap<String, CachedFile>();

	public static void main(String t[]) throws IOException {
		FastThreadPool tp = new FastThreadPool(110, "T");
	}

}
