package com.vortex.agent.itinerary;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.base.Message;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SearchPath;
import com.f1.vortexcommon.msg.agent.VortexAgentFile;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentGetBackupChangedFilesRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentGetBackupChangedFilesResponse;
import com.vortex.agent.VortexAgentUtils;

public class VortexAgentGetBackupChangedFilesItinerary extends AbstractVortexAgentItinerary<VortexAgentGetBackupChangedFilesRequest> {
	private static final Logger log = LH.get(VortexAgentGetBackupChangedFilesItinerary.class);
	private static final long MAX_FILE_LENGTH = 1024 * 1024 * 1024;//ONE GIG
	private static final long MAX_PACKET_SIZE = 1024 * 1024 * 50;//TEN MEGS
	private VortexAgentGetBackupChangedFilesResponse r;

	@Override
	public byte startJourney(VortexAgentItineraryWorker worker) {
		r = nw(VortexAgentGetBackupChangedFilesResponse.class);
		final RequestMessage<VortexAgentGetBackupChangedFilesRequest> request = getInitialRequest();
		final VortexAgentGetBackupChangedFilesRequest req = request.getAction();
		final SearchPath sp = new SearchPath(req.getBackupPath());
		final List<File> srcFiles = sp.search("*", SearchPath.OPTION_CONTINUE_AFTER_FIRST_FIND | SearchPath.OPTION_IS_PATTERN | SearchPath.OPTION_RECURSE
				| SearchPath.OPTION_INCLUDE_ROOT);
		final List<VortexAgentFile> backupFiles = new ArrayList<VortexAgentFile>();
		final long now = getTools().getNow();

		final Map<String, VortexAgentFile> dstFiles = new HashMap<String, VortexAgentFile>(req.getDestinationManifest().size());
		for (VortexAgentFile dstFile : req.getDestinationManifest())
			dstFiles.put(dstFile.getPath(), dstFile);

		long totalTransfer = 0;
		for (File file : srcFiles) {
			try {
				LH.info(log, "Processing File ", file);
				final String path = IOH.toUnixFormat(file.getAbsolutePath());
				final VortexAgentFile vf = nw(VortexAgentFile.class);
				final VortexAgentFile existingFile = dstFiles.remove(path);
				final long lastModified = file.lastModified();
				final long length = file.length();
				if (length > MAX_FILE_LENGTH) {
					//TODO:HANDLE BETTER
					r.setMessage("File too large: " + IOH.getFullPath(file));
					return STATUS_COMPLETE;
				}
				if (existingFile == null || existingFile.getSize() != file.length()) {//added of clearly different
					vf.setData(IOH.readData(file));
					vf.setChecksum(IOH.checkSumBsdLong(vf.getData()));

				} else if (file.lastModified() != existingFile.getModifiedTime()) {//touched, did it change? 
					byte[] data = IOH.readData(file);
					long checksum = IOH.checkSumBsdLong(data);
					if (checksum != existingFile.getChecksum())
						vf.setData(data);
					vf.setChecksum(checksum);
				} else {//time and length are the same... assume checksum is the same (this should be an option)
					vf.setChecksum(existingFile.getChecksum());
				}
				vf.setModifiedTime(file.lastModified());
				vf.setPath(path);
				vf.setSize(length);
				vf.setNow(now);

				byte mask = VortexAgentFile.FILE;
				if (file.isHidden())
					mask |= VortexAgentFile.HIDDEN;
				if (file.canExecute())
					mask |= VortexAgentFile.EXECUTABLE;
				if (file.canRead())
					mask |= VortexAgentFile.READABLE;
				if (file.canWrite())
					mask |= VortexAgentFile.WRITEABLE;
				vf.setMask(mask);
				vf.setModifiedTime(lastModified);
				VortexAgentUtils.compressFile(vf);
				if (lastModified != file.lastModified() || length != file.length()) {
					r.setMessage("File changed durring backup: " + IOH.getFullPath(file));
					return STATUS_COMPLETE;
				}
				if (vf.getData() != null) {
					long newTotal = totalTransfer + vf.getData().length;
					if (newTotal > MAX_PACKET_SIZE) {//this file pushes the total over the packet size, so send an 'incomplete' packet
						if (totalTransfer == 0)//if there are no other files, then we need to add at least this one!
							backupFiles.add(vf);
						r.setOk(true);
						r.setIsComplete(false);
						backupFiles.addAll(dstFiles.values());//pretend the rest of them didn't change
						r.setBackupFiles(backupFiles);
						return STATUS_COMPLETE;
					}
					totalTransfer += newTotal;
				}
				backupFiles.add(vf);
			} catch (IOException e) {
				r.setMessage("Error processing file: " + IOH.getFullPath(file));
				return STATUS_COMPLETE;
			}
		}
		for (VortexAgentFile removed : dstFiles.values()) {
			final VortexAgentFile vf = nw(VortexAgentFile.class);
			vf.setPath(removed.getPath());
			vf.setMask(VortexAgentFile.DELETED);
			backupFiles.add(vf);
		}
		r.setOk(true);
		r.setIsComplete(true);
		r.setBackupFiles(backupFiles);
		return STATUS_COMPLETE;
	}
	@Override
	public byte onResponse(ResultMessage<?> result, VortexAgentItineraryWorker worker) {
		return getPendingRequests().isEmpty() ? STATUS_COMPLETE : STATUS_ACTIVE;
	}
	@Override
	public Message endJourney(VortexAgentItineraryWorker worker) {
		return r;
	}

}
