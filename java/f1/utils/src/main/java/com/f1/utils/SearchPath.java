package com.f1.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.f1.utils.impl.SimpleTextMatcher;
import com.f1.utils.impl.TextMatcherFactory;

public class SearchPath {
	public static final int OPTION_THROW_ON_MISSING = 1;
	public static final int OPTION_INCLUDE_DIRECTORY_IN_RESULTS = 2;
	public static final int OPTION_SUPPRESS_NORMAL_FILES = 4;
	public static final int OPTION_RECURSE = 8;
	public static final int OPTION_CONTINUE_AFTER_FIRST_FIND = 16;
	public static final int OPTION_IS_PATTERN = 32;
	public static final int OPTION_INCLUDE_FULL_PATH_FOR_MATCHING = 64;//if false only the file name is used
	public static final int OPTION_FOLLOW_SYM_LINK = 128;
	public static final int OPTION_INCLUDE_BROKEN_FILES = 256;
	public static final int OPTION_INCLUDE_ROOT = 512;

	private File[] files = OH.EMPTY_FILE_ARRAY;

	public SearchPath() {
	}

	public SearchPath(File... directories) {
		addDirectories(directories);
	}
	public SearchPath(List<File> directories) {
		addDirectories(directories);
	}

	public SearchPath(String... directories) {
		addDirectories(directories);
	}

	public void addDirectories(String... pathsList) {
		for (String paths : pathsList)
			for (String path : SH.split(File.pathSeparatorChar, paths))
				addDirectory(new File(path));
	}

	public void addDirectories(File... file) {
		for (File dir : file)
			addDirectory(dir);
	}
	public void addDirectories(Iterable<File> file) {
		for (File dir : file)
			addDirectory(dir);
	}

	public void addDirectory(File dir) {
		files = AH.insert(files, files.length, dir);
	}

	public List<File> search(String file, int options) {
		return search(file, options, (ProgressCounter) null);
	}

	public List<File> search(String file, int options, ProgressCounter counter) {
		return search(file, options, new ArrayList<File>(), counter);
	}

	public List<File> createSearchList(String file) {
		final List<File> r = new ArrayList<File>();
		String[] parts = SH.split(File.pathSeparatorChar, file);
		for (File f : files)
			for (String part : parts)
				if (!f.isFile())
					r.add(new File(f, part));
		return r;
	}

	public List<File> createSearchListAbsolute(String file) {
		final List<File> r = new ArrayList<File>();
		String[] parts = SH.split(File.pathSeparatorChar, file);
		for (File f : files)
			for (String part : parts)
				if (!f.isFile())
					r.add(new File(f, part).getAbsoluteFile());
		return r;
	}

	public List<File> search(String file, int options, List<File> sink) {
		return search(file, options, sink, null);
	}

	public List<File> search(String file, int options, List<File> sink, ProgressCounter counter) {
		final boolean recurse = MH.areAnyBitsSet(options, OPTION_RECURSE);
		final boolean allowDirectory = MH.areAnyBitsSet(options, OPTION_INCLUDE_DIRECTORY_IN_RESULTS);
		final boolean allowFile = !MH.areAnyBitsSet(options, OPTION_SUPPRESS_NORMAL_FILES);
		final boolean stopOnFirst = !MH.areAnyBitsSet(options, OPTION_CONTINUE_AFTER_FIRST_FIND);
		final boolean isPattern = MH.areAnyBitsSet(options, OPTION_IS_PATTERN);
		final boolean includeDirsInSearch = MH.areAnyBitsSet(options, OPTION_INCLUDE_FULL_PATH_FOR_MATCHING);
		final boolean followSymLink = MH.areAnyBitsSet(options, OPTION_FOLLOW_SYM_LINK);
		final boolean includeBroken = MH.areAnyBitsSet(options, OPTION_INCLUDE_BROKEN_FILES);
		final boolean includeRoot = MH.areAnyBitsSet(options, OPTION_INCLUDE_ROOT);
		TextMatcher matcher;
		try {
			if (isPattern) {
				matcher = TextMatcherFactory.DEFAULT.toMatcher(file);
				sink = search(files, matcher, recurse, allowDirectory, allowFile, stopOnFirst, !includeRoot, includeDirsInSearch, followSymLink, includeBroken, sink, counter);
			} else {
				file = SH.trim('/', SH.replaceAll(file, '\\', '/'));

				if (file.indexOf('/') != -1) {
					sink = SearchDirectoryFile(file, recurse, allowDirectory, allowFile, includeDirsInSearch, followSymLink, includeBroken, !includeRoot, sink, counter);
				} else {
					matcher = new SimpleTextMatcher(SH.trim('/', file), false);
					sink = search(files, matcher, recurse, allowDirectory, allowFile, stopOnFirst, true, includeDirsInSearch, followSymLink, includeBroken, sink, counter);
				}
			}
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
		int size = CH.size(sink);
		final boolean throwOnNotFound = MH.areAnyBitsSet(options, OPTION_THROW_ON_MISSING);
		if (CH.size(sink) == size && throwOnNotFound)//TODO: this can't be right???
			throw new DetailedException("Could not find file in search path").set("file", file).set("search_path", files);
		return sink;
	}

	private List<File> SearchDirectoryFile(String file, boolean recurse, boolean allowDirectories, boolean allowFiles, boolean includeDirsInMatch, boolean followSymLink,
			boolean includeBroken, boolean isRoot, List<File> sink, ProgressCounter counter) throws IOException {
		String parts[] = SH.split('/', file);
		List<File> sink2 = CH.l(files);
		for (int index = 0; index < parts.length && !sink2.isEmpty(); index++) {
			final File[] searchFiles = sink2.toArray(new File[sink2.size()]);
			sink2.clear();
			final boolean atEnd = index + 1 == parts.length, rec = recurse && index == 0;
			final boolean searchDir = allowDirectories || !atEnd;
			final boolean searchFile = allowFiles && atEnd;
			search(searchFiles, new SimpleTextMatcher(parts[index], false), rec, searchDir, searchFile, false, isRoot, includeDirsInMatch, followSymLink, includeBroken, sink2,
					counter);
		}
		if (sink == null)
			sink = new ArrayList<File>(1);
		return CH.l(sink, sink2);
	}

	private static List<File> search(File files[], TextMatcher file, boolean recurse, boolean allowDirectory, boolean allowFile, boolean stopOnFirst, boolean isRoot,
			boolean includeDirsInMatch, boolean followSymlink, boolean includeBroken, List<File> sink, ProgressCounter counter) throws IOException {
		for (File f : files) {
			if (counter != null)
				counter.onProgress(1, f);
			final boolean isDir = f.isDirectory();
			final boolean isFile = f.isFile();
			final boolean include;
			if (isRoot)
				include = false;
			else if (isDir)
				include = allowDirectory && file.matches(includeDirsInMatch ? f.getPath() : f.getName());
			else if (isFile)
				include = allowFile && file.matches(includeDirsInMatch ? f.getPath() : f.getName());
			else
				include = includeBroken && file.matches(includeDirsInMatch ? f.getPath() : f.getName());
			if (include) {
				if (sink == null)
					sink = new ArrayList<File>(1);
				sink.add(f);
				if (stopOnFirst)
					break;
			}
			if (isDir) {
				if (includeDirsInMatch && !file.matches(f.getPath()))
					continue;
				if (!followSymlink) {
					if (IOH.isSymlink(f))
						continue;
				}
				// searches depth first
				if (isRoot || recurse) {
					int size = Math.max(CH.size(sink), 0);
					sink = search(IOH.listFiles(f, false), file, recurse, allowDirectory, allowFile, stopOnFirst, false, includeDirsInMatch, followSymlink, includeBroken, sink,
							counter);
					if (stopOnFirst && size < Math.max(CH.size(sink), 0))
						break;
				}
			}
		}
		return sink;
	}

	@Override
	public String toString() {
		return SH.join(File.pathSeparatorChar, files);
	}

	public File searchFirst(String file) {
		return CH.getOr(search(file, 0), 0, null);
	}
}

