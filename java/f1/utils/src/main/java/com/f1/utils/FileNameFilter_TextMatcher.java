package com.f1.utils;

import java.io.File;
import java.io.FilenameFilter;

public class FileNameFilter_TextMatcher implements FilenameFilter {

	final private TextMatcher matcher;

	public FileNameFilter_TextMatcher(TextMatcher matcher) {
		super();
		this.matcher = matcher;
	}

	@Override
	public boolean accept(File dir, String name) {
		return this.matcher.matches(name);
	}

}
