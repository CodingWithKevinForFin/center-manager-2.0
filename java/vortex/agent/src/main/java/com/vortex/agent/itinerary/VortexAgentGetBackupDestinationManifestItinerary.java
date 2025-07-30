package com.vortex.agent.itinerary;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.f1.base.Message;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.utils.AH;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.vortexcommon.msg.agent.VortexAgentFile;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentGetBackupDestinationManifestRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentGetBackupDestinationManifestResponse;

public class VortexAgentGetBackupDestinationManifestItinerary extends AbstractVortexAgentItinerary<VortexAgentGetBackupDestinationManifestRequest> {
	public static final String DIR_BASE = "vortexstore";
	public static final String FILE_MANIFEST = "latest.manifest";

	private static final Logger log = LH.get(VortexAgentGetBackupDestinationManifestItinerary.class);
	public static final String DIR_CURRENT = "latest";
	public static final String FILE_HOSTS = "muids2hosts.txt";
	public static final char DELIM = '|';

	private VortexAgentGetBackupDestinationManifestResponse r;

	@Override
	public byte startJourney(VortexAgentItineraryWorker worker) {
		r = nw(VortexAgentGetBackupDestinationManifestResponse.class);
		final RequestMessage<VortexAgentGetBackupDestinationManifestRequest> action = getInitialRequest();
		final VortexAgentGetBackupDestinationManifestRequest req = action.getAction();
		final String destPath = req.getDestinationPath();
		final String backupPath = req.getBackupPath();
		final String sourceMuid = req.getSourceMuid();
		final String sourceHost = req.getSourceHostName();
		final File rootDir = new File(destPath, DIR_BASE);
		if (!ensureDir(rootDir))
			return STATUS_COMPLETE;
		if (rootDir.isFile()) {
			r.setMessage("Destination is existing file (must be directory): " + rootDir);
			return STATUS_COMPLETE;
		} else if (!rootDir.isDirectory()) {
			try {
				IOH.ensureDir(rootDir);
				if (rootDir.isDirectory()) {
					r.setBackupFiles(new ArrayList<VortexAgentFile>(0));
					return STATUS_COMPLETE;
				}
			} catch (IOException e) {
			}
			r.setMessage("Could not create destination root directory (ensure " + EH.getUserName() + " has create permissions): " + rootDir);
			return STATUS_COMPLETE;
		}
		List<VortexAgentFile> backupFiles = new ArrayList<VortexAgentFile>();
		final String backupPath2 = EH.isWindows() ? SH.replaceAll(backupPath, ':', '_') : backupPath;
		final File fullPath = IOH.joinPaths(rootDir, sourceMuid, DIR_CURRENT, backupPath2);
		final File hostsFile = IOH.joinPaths(rootDir, FILE_HOSTS);
		final File manifestsFile = IOH.joinPaths(rootDir, sourceMuid, FILE_MANIFEST);
		if (!ensureDir(fullPath))
			return STATUS_COMPLETE;
		if (!processManifest(manifestsFile, backupPath2, backupFiles))
			return STATUS_COMPLETE;
		try {
			final String prefix = sourceMuid + DELIM;
			final String entry = prefix + sourceHost;
			String[] lines = SH.trimArray(SH.splitLines(IOH.readText(hostsFile)));
			boolean found = false;
			boolean updated = false;
			for (int i = 0; i < lines.length; i++) {
				if (lines[i].startsWith(prefix)) {
					found = true;
					if (!lines[i].equals(entry)) {
						lines[i] = entry;
						updated = true;
					}
					break;
				}
			}
			if (!found || updated) {
				if (!found)
					Arrays.sort(lines = AH.append(lines, entry));
				IOH.writeText(hostsFile, SH.join(SH.NEWLINE, lines, new StringBuilder()).append(SH.NEWLINE).toString());
			}
		} catch (IOException e) {
			LH.warning(log, "Error processing hosts file: ", IOH.getFullPath(hostsFile), e);
			r.setMessage("Error processing hosts file (ensure " + EH.getUserName() + " has write permissions): " + IOH.getFullPath(hostsFile));
			return STATUS_COMPLETE;
		}

		r.setBackupFiles(backupFiles);
		r.setOk(true);
		return STATUS_COMPLETE;
	}
	private boolean processManifest(File manifest, String sourcePath, List<VortexAgentFile> sink) {
		if (!manifest.exists()) {
			try {
				IOH.writeText(manifest, "");
			} catch (Exception e) {
				LH.warning(log, "Error creating dummy manifest file: ", IOH.getFullPath(manifest), e);
				r.setMessage("Error creating dummy manifest file (ensure " + EH.getUserName() + " has write permissions): " + IOH.getFullPath(manifest));
				return false;
			}
		}
		if (manifest.isDirectory()) {
			r.setMessage("manifest is existing directory (must be file): " + IOH.getFullPath(manifest));
			return false;
		}
		String[] lines;
		try {
			lines = SH.trimArray(SH.splitLines(IOH.readText(manifest)));
		} catch (IOException e) {
			LH.warning(log, "Error processing manifest file: ", IOH.getFullPath(manifest), e);
			r.setMessage("Error processing manifest file (ensure " + EH.getUserName() + " has write permissions): " + IOH.getFullPath(manifest));
			return false;
		}
		for (String line : lines) {
			try {
				if (line.startsWith("#"))
					continue;
				if (!line.startsWith(sourcePath))
					continue;
				final String[] parts = SH.split(DELIM, line);
				final String fileName = parts[0];
				final long length = SH.parseLong(parts[1]);
				final long checksum = SH.parseLong(parts[2], 62);
				final long modTime = SH.parseLong(parts[3]);
				VortexAgentFile afile = nw(VortexAgentFile.class);
				afile.setChecksum(checksum);
				afile.setSize(length);
				afile.setPath(fileName);//cheating a bit
				afile.setModifiedTime(modTime);
				sink.add(afile);
			} catch (Exception e) {
				LH.warning(log, "Error processing manifist file: ", IOH.getFullPath(manifest), "line: ", line, e);
				r.setMessage("Error parsing manifest file: " + IOH.getFullPath(manifest));
				return false;
			}
		}
		return true;
	}
	private boolean ensureDir(File rootDir) {
		if (rootDir.isFile())
			r.setMessage("Destination is existing file (must be directory): " + rootDir);
		try {
			IOH.ensureDir(rootDir);
			if (rootDir.isDirectory())
				return true;
		} catch (IOException e) {
		}
		r.setMessage("Could not create destination root directory (ensure " + EH.getUserName() + " has create permissions): " + rootDir);
		return false;
	}
	@Override
	public byte onResponse(ResultMessage<?> result, VortexAgentItineraryWorker worker) {
		return STATUS_COMPLETE;
	}

	@Override
	public Message endJourney(VortexAgentItineraryWorker worker) {
		return r;
	}

}
