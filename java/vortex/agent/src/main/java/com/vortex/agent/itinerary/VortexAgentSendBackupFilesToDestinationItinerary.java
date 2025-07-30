package com.vortex.agent.itinerary;

import static com.vortex.agent.itinerary.VortexAgentGetBackupDestinationManifestItinerary.DELIM;
import static com.vortex.agent.itinerary.VortexAgentGetBackupDestinationManifestItinerary.DIR_BASE;
import static com.vortex.agent.itinerary.VortexAgentGetBackupDestinationManifestItinerary.DIR_CURRENT;
import static com.vortex.agent.itinerary.VortexAgentGetBackupDestinationManifestItinerary.FILE_MANIFEST;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.f1.base.Message;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.MH;
import com.f1.utils.SH;
import com.f1.vortexcommon.msg.agent.VortexAgentFile;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentSendBackupFilesToDestinationRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentSendBackupFilesToDestinationResponse;
import com.vortex.agent.VortexAgentUtils;

public class VortexAgentSendBackupFilesToDestinationItinerary extends AbstractVortexAgentItinerary<VortexAgentSendBackupFilesToDestinationRequest> {
	private static final Logger log = LH.get(VortexAgentSendBackupFilesToDestinationItinerary.class);
	private VortexAgentSendBackupFilesToDestinationResponse r;

	@Override
	public byte startJourney(VortexAgentItineraryWorker worker) {
		r = nw(VortexAgentSendBackupFilesToDestinationResponse.class);
		final RequestMessage<VortexAgentSendBackupFilesToDestinationRequest> request = getInitialRequest();
		final VortexAgentSendBackupFilesToDestinationRequest req = request.getAction();
		final long now = getTools().getNow();

		final String destPath = req.getDestinationPath();
		final String backupPath = req.getBackupPath();
		final String sourceMuid = req.getSourceMuid();
		final String sourceHost = req.getSourceHostName();
		final File rootDir = IOH.joinPaths(destPath, DIR_BASE, sourceMuid);//, req.getSourceMuid(), DIR_CURRENT);

		final File manifestsFile = IOH.joinPaths(rootDir, FILE_MANIFEST);
		final File destDir = IOH.joinPaths(rootDir, DIR_CURRENT);

		List<String> manifest = new ArrayList<String>();
		if (!manifestsFile.canWrite()) {
			LH.warning(log, "Must be able to write to manifest: ", IOH.getFullPath(manifestsFile));
			r.setMessage("Must be able to write to manifest: " + IOH.getFullPath(manifestsFile));
			return STATUS_COMPLETE;
		}
		try {
			for (String line : SH.trimArray(SH.splitLines(IOH.readText(manifestsFile)))) {
				if (line.startsWith("#"))
					continue;
				if (!line.startsWith(backupPath))
					manifest.add(line);
			}
		} catch (IOException e) {
			LH.warning(log, "Error reading manifest: ", IOH.getFullPath(manifestsFile), e);
			r.setMessage("Error processing manifest: " + IOH.getFullPath(manifestsFile));
			return STATUS_COMPLETE;
		}
		long bytesTransferredStats = 0;
		long filesAddedStats = 0;
		long filesDeletedStats = 0;
		long filesUpdatedStats = 0;
		long filesUnchangedStats = 0;

		for (VortexAgentFile file : req.getFiles()) {
			String filePath = file.getPath();
			if (EH.isWindows())
				filePath = SH.replaceAll(filePath, ':', '_');
			File path = IOH.joinPaths(destDir, filePath);
			try {
				if (!MH.anyBits(file.getMask(), VortexAgentFile.DELETED)) {
					boolean updatePermissions = false;
					if (file.getData() != null) {
						VortexAgentUtils.decompressFile(file);
						bytesTransferredStats += file.getData().length;
						IOH.ensureDir(path.getParentFile());
						if (path.isFile())
							filesUpdatedStats++;
						else
							filesAddedStats++;
						IOH.writeData(path, file.getData());
						path.setLastModified(file.getModifiedTime());
						path.setExecutable(MH.areAnyBitsSet(file.getMask(), VortexAgentFile.EXECUTABLE));
						path.setWritable(MH.areAnyBitsSet(file.getMask(), VortexAgentFile.WRITEABLE));
						path.setReadable(MH.areAnyBitsSet(file.getMask(), VortexAgentFile.READABLE));
					} else if (!path.isFile()) {
						LH.warning(log, "update for unknown file: ", path, " mask: ", file.getMask());
						r.setMessage("Error processing file: " + IOH.getFullPath(path));
						return STATUS_COMPLETE;
					} else if (file.getSize() != path.length()) {
						LH.warning(log, "file size incorrect: ", path, " source: ", file.getSize(), " current: ", path.length());
						r.setMessage("Error processing file: " + IOH.getFullPath(path));
						return STATUS_COMPLETE;
					} else if (path.lastModified() != file.getModifiedTime() || path.canExecute() != MH.areAnyBitsSet(file.getMask(), VortexAgentFile.EXECUTABLE)
							|| path.canRead() != MH.areAnyBitsSet(file.getMask(), VortexAgentFile.READABLE)
							|| path.canWrite() != MH.areAnyBitsSet(file.getMask(), VortexAgentFile.WRITEABLE)) {
						updatePermissions = true;
						filesUpdatedStats++;
					} else
						filesUnchangedStats++;
					if (updatePermissions) {
						path.setLastModified(file.getModifiedTime());
						path.setExecutable(MH.areAnyBitsSet(file.getMask(), VortexAgentFile.EXECUTABLE));
						path.setWritable(MH.areAnyBitsSet(file.getMask(), VortexAgentFile.WRITEABLE));
						path.setReadable(MH.areAnyBitsSet(file.getMask(), VortexAgentFile.READABLE));
					}

					manifest.add(filePath + DELIM + file.getSize() + DELIM + SH.toString(file.getChecksum(), 62) + DELIM + file.getModifiedTime());
				} else {
					if (path.isFile())
						IOH.deleteForce(path);
					else
						LH.warning(log, "Delete for unknown file: " + IOH.getFullPath(path));
					filesDeletedStats++;
				}
			} catch (Exception e) {
				LH.warning(log, "could not process file: ", path, " mask: ", file.getMask(), e);
				r.setMessage("Error processing file: " + IOH.getFullPath(path));
				return STATUS_COMPLETE;
			}
		}
		Collections.sort(manifest);
		manifest.add("# Updated on " + getTools().getServices().getLocaleFormatter().getDateFormatter(LocaleFormatter.DATETIME).format(now));

		try {
			IOH.writeText(manifestsFile, SH.join(SH.NEWLINE, manifest));
		} catch (IOException e) {
			LH.warning(log, "Error writing manifest: ", IOH.getFullPath(manifestsFile), e);
			r.setMessage("Error writing manifest: " + IOH.getFullPath(manifestsFile));
			return STATUS_COMPLETE;
		}
		r.setBytesTransfered(bytesTransferredStats);
		r.setFilesAddedStats(filesAddedStats);
		r.setFilesDeletedStats(filesDeletedStats);
		r.setFilesUpdatedStats(filesUpdatedStats);
		r.setFilesUnchangedStats(filesUnchangedStats);
		r.setOk(true);
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
