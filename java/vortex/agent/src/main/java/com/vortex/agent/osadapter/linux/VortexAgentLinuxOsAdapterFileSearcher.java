package com.vortex.agent.osadapter.linux;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.container.ContainerTools;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.Duration;
import com.f1.utils.FastBufferedInputStream;
import com.f1.utils.FastByteArrayDataInputStream;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.LoggingProgressCounter;
import com.f1.utils.LongArrayList;
import com.f1.utils.OH;
import com.f1.utils.ProgressCounter;
import com.f1.utils.SH;
import com.f1.utils.SearchPath;
import com.f1.utils.TextMatcher;
import com.f1.utils.mutable.Mutable;
import com.f1.utils.mutable.Mutable.Long;
import com.f1.vortexcommon.msg.agent.VortexAgentFile;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileSearchRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileSearchResponse;
import com.vortex.agent.VortexAgentUtils;
import com.vortex.agent.osadapter.VortexAgentOsAdapterFileSearcher;
import com.vortex.agent.state.VortexAgentOsAdapterState;

public class VortexAgentLinuxOsAdapterFileSearcher implements VortexAgentOsAdapterFileSearcher {

	private static final Logger log = LH.get(VortexAgentLinuxOsAdapterFileSearcher.class);

	@Override
	public VortexAgentFileSearchResponse searchFiles(VortexAgentFileSearchRequest req, VortexAgentOsAdapterState state) {
		LH.info(log, "Searching Files. Pos.Searches: [", req.getSearchInFileExpressions(), "] Root: [", req.getRootPaths(), "] search: [", req.getSearchExpression(), "]");
		ContainerTools tools = state.getPartition().getContainer().getTools();
		VortexAgentFileSearchResponse r = state.nw(VortexAgentFileSearchResponse.class);
		//if (req.getMaxDataSize() < 10240) {
		//r.setMessage("Illegal maxDataSize: " + req.getMaxDataSize());
		//return r;
		//}
		if (SH.length(req.getIncludeSearchPositionsExpression()) > 0 && CH.size(req.getSearchInFileExpressions()) <= 0) {
			r.setMessage("must include search in file expressions when include search position is enabled: " + req.getIncludeSearchPositionsExpression());
			return r;
		}
		r.setStartTime(tools.getNow());
		final List<File> allFiles = new ArrayList<File>();
		if (req.getRootPaths() == null) {
			CH.l(allFiles, File.listRoots());
		} else {
			for (String rp : req.getRootPaths()) {
				List<File> files = null;
				File singleFile = null;
				File file = new File(rp);
				if (file.isFile()) {
					allFiles.add(file);
				} else {
					final SearchPath sp = new SearchPath(rp);
					final ProgressCounter counter = new LoggingProgressCounter(log, Level.INFO, "scanning file system: ", 1000, 1, TimeUnit.SECONDS);

					int options = SearchPath.OPTION_CONTINUE_AFTER_FIRST_FIND | SearchPath.OPTION_IS_PATTERN | SearchPath.OPTION_INCLUDE_DIRECTORY_IN_RESULTS
							| SearchPath.OPTION_INCLUDE_DIRECTORY_IN_RESULTS | SearchPath.OPTION_FOLLOW_SYM_LINK;

					if (req.getSearchExpression() == null)
						options |= SearchPath.OPTION_INCLUDE_ROOT;

					if (req.getRecurse())
						options |= SearchPath.OPTION_RECURSE;

					allFiles.addAll(sp.search(req.getSearchExpression(), options, counter));
				}
			}
		}
		final TextMatcher cksmMatcher = SH.m(SH.noNull(req.getIncludeChecksumExpression()));
		final TextMatcher dataMatcher = SH.m(SH.noNull(req.getIncludeDataExpression()));
		final TextMatcher srchMatcher = SH.m(SH.noNull(req.getIncludeSearchPositionsExpression()));
		FastByteArrayDataInputStream is = new FastByteArrayDataInputStream(OH.EMPTY_BYTE_ARRAY);
		List<VortexAgentFile> agentFiles = new ArrayList<VortexAgentFile>();
		int filesRead = 0;
		int errors = 0;
		long dataOffset = req.getDataOffset();
		long maxDataSize = req.getMaxDataSize();
		Set<File> unique = new HashSet<File>(allFiles.size());
		for (File file : allFiles) {
			if (!unique.add(file))
				continue;
			VortexAgentFile af = state.nw(VortexAgentFile.class);
			final String path = file.getPath();
			final long mtime = file.lastModified();
			final long size = file.length();
			final boolean isDirectory = file.isDirectory();
			af.setPath(path);
			af.setModifiedTime(mtime);
			if (file.isFile())
				af.setSize(size);
			byte mask = 0;
			if (file.canExecute())
				mask |= VortexAgentFile.EXECUTABLE;
			if (file.canRead())
				mask |= VortexAgentFile.READABLE;
			if (file.canWrite())
				mask |= VortexAgentFile.WRITEABLE;
			if (file.isDirectory())
				mask |= VortexAgentFile.DIRECTORY;
			if (file.isFile())
				mask |= VortexAgentFile.FILE;
			if (file.isHidden())
				mask |= VortexAgentFile.HIDDEN;
			af.setMask(mask);

			if (!isDirectory)
				try {
					final boolean getData = dataMatcher.matches(path);
					final boolean getCksm = cksmMatcher.matches(path);
					final boolean doSearch = srchMatcher.matches(path);
					boolean mustReadAll = getCksm || doSearch;
					if (getData || mustReadAll) {
						LH.fine(log, "Reading file (" + size + " bytes): " + path);
						filesRead++;
						if (size <= maxDataSize) {//easy case, fits in memory
							if (mustReadAll) {//simply read data & do offline scan
								byte[] data = IOH.readData(file);
								if (req.getDataOffset() > 0) {
									af.setData(AH.subarray(data, (int) dataOffset, data.length - (int) dataOffset));
									af.setDataOffset(dataOffset);
								} else
									af.setData(data);
								if (getCksm)
									af.setChecksum(IOH.checkSumBsdLong(data));
								if (doSearch) {
									byte[] searchData;
									if (req.getIsSearchCaseSensitive()) {
										searchData = data;
									} else {
										searchData = data.clone();
										SH.uppercaseInplace(searchData);
									}
									HashMap<String, long[]> map = new HashMap<String, long[]>(req.getSearchInFileExpressions().size());
									LongArrayList buf = new LongArrayList();
									for (String s : req.getSearchInFileExpressions()) {
										buf.clear();
										byte[] findBytes = s.getBytes();
										if (!req.getIsSearchCaseSensitive())
											SH.uppercaseInplace(findBytes);
										int start = 0;
										for (;;) {
											start = AH.indexOf(searchData, findBytes, start);
											if (start == -1)
												break;
											buf.add(start);
											start += findBytes.length;
										}
										map.put(s, buf.toLongArray());
									}
									af.setSearchOffsets(map);
								}
								VortexAgentUtils.compressFile(af);
							} else {//no checksum, no search but offset, skip to the offset part of the file,read and get out
								af.setData(IOH.readData(file, dataOffset, (int) (size - dataOffset)));
								af.setDataOffset(dataOffset);
								VortexAgentUtils.compressFile(af);
							}
						} else {//too much data, we need to do an online scan
							if (getData) {
								af.setData(IOH.readData(file, dataOffset, (int) Math.min(maxDataSize, size - dataOffset)));
								af.setDataOffset(dataOffset);
								VortexAgentUtils.compressFile(af);
							}
							if (mustReadAll) {
								BufferedInputStream bis = null;
								try {
									int bufSize = 1024;
									bis = new BufferedInputStream(new FileInputStream(path), bufSize);
									if (!doSearch) {//only checksum
										af.setChecksum(IOH.checkSumBsdLong(bis));
									} else {//only search
										Long checksumSink = getCksm ? new Mutable.Long() : null;
										af.setSearchOffsets(doSearch(bis, req.getSearchInFileExpressions(), bufSize, checksumSink, req.getIsSearchCaseSensitive()));
										if (getCksm)
											af.setChecksum(checksumSink.value);
									}
								} finally {
									IOH.close(bis);
								}
							}
						}
					}
				} catch (IOException e) {
					if (SH.is(e.getMessage()))
						r.setMessage(e.getMessage());
					else
						r.setMessage("Unknown error on file: " + path);
					LH.warning(log, "Error reading data from file: ", path, " ", e);
					errors++;
				}
			agentFiles.add(af);
		}
		r.setFiles(agentFiles);
		r.setEndTime(tools.getNow());
		r.setJobId(req.getJobId());
		r.setOk(r.getMessage() == null);
		LH.info(log, "Found ", agentFiles.size(), " files, got data for ", filesRead, " files, encountered ", errors, " errors");
		return r;
	}

	public static Map<String, long[]> doSearch(InputStream bis, List<String> searchs, int bufSize, Mutable.Long checksumSink, boolean caseSensitive) throws IOException {

		//prepare the search byte arrays and there index bufs
		int searchCount = searchs.size();
		byte findBytes[][] = new byte[searchCount][];
		LongArrayList[] foundIndexes = new LongArrayList[searchCount];
		int maxSearchLength = 0;
		for (int i = 0; i < searchCount; i++) {
			byte[] data = searchs.get(i).getBytes();
			if (!caseSensitive)
				upperCase(data);
			findBytes[i] = data;
			foundIndexes[i] = new LongArrayList();
			maxSearchLength = Math.max(maxSearchLength, findBytes[i].length - 1);
		}
		long checksum = 0;
		byte[] buf = new byte[bufSize];
		int bytesToKeepFromLastBuf = 0;

		// Read into buffer and loop through search expression, capturing all occurrences. 
		// Here is the trick: For each buffer, except the last one we need to keep the n-1 last bytes and move them over to the front of the subsequent 
		// buf, where n is the max length of all search sequences.
		// For example: if a search expression is 'happy' and a buffer we pull in looks like [ab......happ], we must take those last 4
		// chars and prepend it to next buffer such that if the following buffer is [y birthday]. it becomes [happy birthday]. Note that
		// if there was a second expression with fewer than n, say 'app' then its critical we start the search at n-app.length in the second
		// go around, to avoid duplicates
		int pos = 0;
		for (;;) {
			if (bytesToKeepFromLastBuf > 0)
				System.arraycopy(buf, buf.length - bytesToKeepFromLastBuf, buf, 0, bytesToKeepFromLastBuf);
			int bytesRead = IOH.readDataNoThrow(bis, buf, bytesToKeepFromLastBuf, buf.length - bytesToKeepFromLastBuf);
			if (!caseSensitive)
				SH.uppercaseInplace(buf, bytesToKeepFromLastBuf, bytesRead + bytesToKeepFromLastBuf);
			int endPos = bytesRead + bytesToKeepFromLastBuf;
			if (checksumSink != null)
				for (int i = bytesToKeepFromLastBuf; i < endPos; i++)
					checksum = IOH.applyChecksum64(checksum, buf[i]);
			for (int i = 0; i < searchCount; i++) {
				byte[] find = findBytes[i];
				int start = bytesToKeepFromLastBuf == 0 ? 0 : bytesToKeepFromLastBuf + 1 - find.length;
				if (start < 0) {
					System.out.println("what!");
				}
				for (;;) {
					start = AH.indexOf(buf, find, start);
					if (start == -1 || start > endPos - find.length)
						break;
					foundIndexes[i].add(start + pos);
					start += find.length;
				}
			}
			if (endPos != buf.length)
				break;
			if (bytesToKeepFromLastBuf > 0)
				pos += bytesRead;
			else
				pos += bytesRead - maxSearchLength;
			bytesToKeepFromLastBuf = maxSearchLength;
		}
		Map<String, long[]> r = new HashMap<String, long[]>(searchCount);
		for (int i = 0; i < searchCount; i++)
			r.put(searchs.get(i), foundIndexes[i].toLongArray());
		if (checksumSink != null)
			checksumSink.value = checksum;
		return r;
	}
	private static void upperCase(byte[] bytes) {
		SH.uppercaseInplace(bytes);
	}

	public static void main(String a[]) throws IOException {
		int bufSize = 1024 * 10;
		String file = "/tmp/test3.log";
		Duration d = new Duration();
		FastBufferedInputStream bis = new FastBufferedInputStream(new FileInputStream(file), bufSize);
		Long sink = new Mutable.Long();
		String txt = "test";
		String txt2 = "testing";
		Map<String, long[]> indexes = doSearch(bis, CH.l(txt, txt2), bufSize, sink, false);
		bis.close();
		//bis = new FastBufferedInputStream(new FileInputStream(file ), bufSize);
		//System.out.println(sink.value + " vs " + IOH.checkSumBsdLong(bis));
		//bis.close();
		System.out.println(indexes.get(txt).length);
		System.out.println(indexes.get(txt2).length);
		d.stampStdout();
	}
}
