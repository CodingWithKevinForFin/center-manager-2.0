package com.vortex.agent.osadapter.windows;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.FastByteArrayDataInputStream;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.LoggingProgressCounter;
import com.f1.utils.OH;
import com.f1.utils.ProgressCounter;
import com.f1.utils.SH;
import com.f1.utils.SearchPath;
import com.f1.utils.TextMatcher;
import com.f1.vortexcommon.msg.agent.VortexAgentFile;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileSearchRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileSearchResponse;
import com.vortex.agent.VortexAgentUtils;
import com.vortex.agent.osadapter.VortexAgentOsAdapterFileSearcher;
import com.vortex.agent.state.VortexAgentOsAdapterState;

public class VortexAgentWindowsOsAdapterFileSearcher implements VortexAgentOsAdapterFileSearcher {

	private static final Logger log = LH.get(VortexAgentWindowsOsAdapterFileSearcher.class);

	@Override
	public VortexAgentFileSearchResponse searchFiles(VortexAgentFileSearchRequest req, VortexAgentOsAdapterState state) {
		ContainerTools tools = state.getPartition().getContainer().getTools();
		VortexAgentFileSearchResponse r = state.nw(VortexAgentFileSearchResponse.class);
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
							| SearchPath.OPTION_INCLUDE_DIRECTORY_IN_RESULTS;
					if (req.getSearchExpression() == null)
						options |= SearchPath.OPTION_INCLUDE_ROOT;

					if (req.getRecurse())
						options |= SearchPath.OPTION_RECURSE;

					allFiles.addAll(sp.search(req.getSearchExpression(), options, counter));
				}
			}
		}
		final TextMatcher cksmMatcher = SH.m(req.getIncludeChecksumExpression());
		final TextMatcher dataMatcher = SH.m(req.getIncludeDataExpression());
		FastByteArrayDataInputStream is = new FastByteArrayDataInputStream(OH.EMPTY_BYTE_ARRAY);
		List<VortexAgentFile> agentFiles = new ArrayList<VortexAgentFile>();
		int filesRead = 0;
		int errors = 0;
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
					if (getData || getCksm) {
						LH.fine(log, "Reading file (" + size + " bytes): " + path);
						filesRead++;
					}
					if (getData && size <= req.getMaxDataSize()) {
						byte[] data = IOH.readData(file);
						af.setData(data);
						if (getCksm)
							af.setChecksum(IOH.checkSumBsdLong(is.reset(data)));
						VortexAgentUtils.compressFile(af);
					} else if (getCksm) {
						BufferedInputStream bis = null;
						try {
							bis = new BufferedInputStream(new FileInputStream(path));
							af.setChecksum(IOH.checkSumBsdLong(bis));
						} finally {
							IOH.close(bis);
						}
					}
				} catch (IOException e) {
					LH.warning(log, "Error reading data from file: ", path, " ", e);
					errors++;
				}
			agentFiles.add(af);
		}
		r.setFiles(agentFiles);
		r.setEndTime(tools.getNow());
		r.setJobId(req.getJobId());
		r.setOk(true);
		LH.info(log, "Found ", agentFiles.size(), " files, got data for ", filesRead, " files, encountered ", errors, " errors");
		return r;
	}

}
