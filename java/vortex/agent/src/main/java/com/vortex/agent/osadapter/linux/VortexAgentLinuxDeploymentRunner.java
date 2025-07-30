package com.vortex.agent.osadapter.linux;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.f1.utils.FastByteArrayDataInputStream;
import com.f1.utils.FileMagic;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.SH;
import com.f1.utils.tar.TarEntry;
import com.f1.utils.tar.TarInputStream;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunDeploymentResponse;
import com.vortex.agent.VortexAgentOwnerBasedFilesManager;
import com.vortex.agent.VortexAgentUtils;
import com.vortex.agent.messages.VortexAgentOsAdapterDeploymentRequest;
import com.vortex.agent.osadapter.VortexAgentOsAdapterDeploymentRunner;
import com.vortex.agent.state.VortexAgentOsAdapterState;

public class VortexAgentLinuxDeploymentRunner implements VortexAgentOsAdapterDeploymentRunner {
	private static final Logger log = LH.get(VortexAgentLinuxDeploymentRunner.class);

	@Override
	public VortexAgentRunDeploymentResponse runDeployment(VortexAgentOsAdapterDeploymentRequest req, VortexAgentOsAdapterState state) {
		VortexAgentRunDeploymentResponse r = state.nw(VortexAgentRunDeploymentResponse.class);
		try {

			final String targetDirectory = req.getTargetDirectory();

			switch (req.getType()) {
				case VortexAgentOsAdapterDeploymentRequest.TYPE_VERIFY: {
					final byte[] data = req.getData();
					List<FileEntry> files = expandFiles(data, r);
					if (files == null)
						return r;
					final String sudoCommand = VortexAgentUtils.toSudo(req.getOwner(), "");
					VortexAgentOwnerBasedFilesManager ttfc;
					try {
						ttfc = new VortexAgentOwnerBasedFilesManager(state.getPartition().getContainer().getTools().getContainer().getThreadPoolController(), sudoCommand,
								System.err);
					} catch (Exception e) {
						LH.info(log, "Error starting files manager", e);
						r.setMessage("Prepare for verification failed as user: " + req.getOwner());
						r.setOk(false);
						return r;
					}
					File targetDir = new File(targetDirectory);
					try {
						ttfc.createDirectory(targetDir, true, true, true);
					} catch (Exception e) {
						r.setMessage("target directory not created: " + targetDirectory);
						r.setOk(false);
						return r;
					}
					for (FileEntry entry : files) {
						File file = new File(targetDirectory + File.separatorChar + entry.fileName);
						if (entry.isDirectory) {
							log.info("Deploying Directory: " + file);
							ttfc.createDirectory(file, entry.isExecutable, entry.isReadable, entry.isWriteable);
						} else {
							log.info("Deploying File: " + file + " " + entry.data.length + " byte(s) " + entry.isExecutable + ", " + entry.isReadable + ", " + entry.isWriteable);

							ttfc.createFile(file, entry.data, entry.isExecutable, entry.isReadable, entry.isWriteable);
						}
					}
					ttfc.done();
					r.setOk(true);
					break;
				}
				case VortexAgentOsAdapterDeploymentRequest.TYPE_DEPLOY: {
					if (req.getAutoDeleteFiles() != null) {
						long now = state.getPartition().getContainer().getTools().getNow();
						final File backup = state.getPartition().getContainer().getTools().getOptional("recycling.dir", new File("/tmp/recycling"));
						String[] files = SH.split(',', req.getAutoDeleteFiles());
						for (String fileName : files) {
							File file = new File(targetDirectory + File.separatorChar + fileName);
							if (file.exists()) {
								File dest = new File(backup, (now++) + "_" + file.getName());
								LH.info(log, "Moving ", file, " ==> ", dest);
								IOH.moveForce(file, dest);
							}
						}
					}
					final byte[] data = req.getData();
					List<FileEntry> files = expandFiles(data, r);
					if (files == null)
						return r;
					final String sudoCommand = VortexAgentUtils.toSudo(req.getOwner(), "");
					for (Entry<String, String> propEntry : req.getPropertyFiles().entrySet()) {
						files.add(new FileEntry(false, propEntry.getKey(), propEntry.getValue().getBytes(), false, true, false));
					}
					final Set<String> foundFiles = new HashSet<String>();
					for (FileEntry fe : files) {
						if (!foundFiles.add(IOH.getFullPath(new File(fe.fileName)))) {
							r.setMessage("duplicate entry in deployment: " + fe.fileName);
							r.setOk(false);
							return r;
						}
					}
					try {
						StringBuilder manifest = new StringBuilder();
						VortexAgentOwnerBasedFilesManager ttfc = new VortexAgentOwnerBasedFilesManager(state.getPartition().getContainer().getTools().getContainer()
								.getThreadPoolController(), sudoCommand, System.err);
						File targetDir = new File(targetDirectory);
						try {
							ttfc.createDirectory(targetDir, true, true, true);
						} catch (Exception e) {
							r.setMessage("target directory not created: " + targetDirectory);
							r.setOk(false);
							return r;
						}
						for (FileEntry entry : files) {
							File file = new File(targetDirectory + File.separatorChar + entry.fileName);
							if (entry.isDirectory) {
								log.info("Deploying Directory: " + file);
								ttfc.createDirectory(file, entry.isExecutable, entry.isReadable, entry.isWriteable);
							} else {
								log.info("Deploying File: " + file + " " + entry.data.length + " byte(s) " + entry.isExecutable + ", " + entry.isReadable + ", "
										+ entry.isWriteable);

								manifest.append(file.toString()).append(' ').append(SH.toString(entry.data.length)).append(' ').append(chsum(IOH.checkSumBsdLong(entry.data)))
										.append(' ').append(entry.getMode()).append(SH.NEWLINE);
								ttfc.createFile(file, entry.data, entry.isExecutable, entry.isReadable, entry.isWriteable);
							}
						}

						long chsum = IOH.checkSumBsdLong(manifest.toString().getBytes());
						manifest.append("#CHKSUM: ").append(chsum(chsum)).append(SH.NEWLINE);
						ttfc.createFile(new File(targetDirectory, ".f1deploy.txt"), manifest.toString().getBytes(), false, true, false);
						ttfc.done();
					} catch (Exception e) {
						LH.warning(log, "Deployment failed for target directory: ", targetDirectory, e);
						r.setMessage("Failure writing to target directory: " + targetDirectory);
						r.setOk(false);
						return r;
					}
					r.setOk(true);
					break;
				}
				case VortexAgentOsAdapterDeploymentRequest.TYPE_UNDEPLOY: {
					final String sudoCommand = VortexAgentUtils.toSudo(req.getOwner(), "");
					try {
						VortexAgentOwnerBasedFilesManager ttfc = new VortexAgentOwnerBasedFilesManager(state.getPartition().getContainer().getTools().getContainer()
								.getThreadPoolController(), sudoCommand, System.err);
						File targetDir = new File(targetDirectory);
						if (targetDir.isDirectory()) {
							for (File f : targetDir.listFiles())
								ttfc.deleteFile(f);
							ttfc.deleteFileNoThrow(targetDir);
						}
						ttfc.done();
					} catch (Exception e) {
						LH.warning(log, "Undeployment failed for target directory: ", targetDirectory, e);
						r.setMessage("Failure writing to target directory: " + targetDirectory);
						r.setOk(false);
						return r;
					}
					r.setOk(true);
					break;
				}
			}
		} catch (Exception e) {
			switch (req.getType()) {
				case VortexAgentOsAdapterDeploymentRequest.TYPE_DEPLOY:
					LH.warning(log, "Error running verify: " + req, e);
					r.setMessage("General error running verify");
					break;
				case VortexAgentOsAdapterDeploymentRequest.TYPE_UNDEPLOY:
					LH.warning(log, "Error running deployment: " + req, e);
					r.setMessage("General error running deployment");
					break;
				case VortexAgentOsAdapterDeploymentRequest.TYPE_VERIFY:
					LH.warning(log, "Error running deployment: " + req, e);
					r.setMessage("General error running deployment");
					break;
			}
		}
		return r;
	}
	private List<FileEntry> expandFiles(byte[] data, VortexAgentRunDeploymentResponse r) throws IOException {
		byte[] currentData = data;
		boolean extracted = false;
		List<FileEntry> files = new ArrayList<FileEntry>();
		while (!extracted) {
			int type = FileMagic.getType(currentData);
			switch (type) {
				case FileMagic.FILE_TYPE_GZIP_COMPRESSED_DATA: {
					currentData = IOH.readData(new GZIPInputStream(new FastByteArrayDataInputStream(currentData)));
					continue;
				}
				case FileMagic.FILE_TYPE_GNU_TAR_ARCHIVE:
				case FileMagic.FILE_TYPE_POSIX_TAR_ARCHIVE: {
					TarInputStream tar = new TarInputStream(new FastByteArrayDataInputStream(currentData));
					TarEntry entry;
					while ((entry = tar.getNextEntry()) != null) {
						final byte[] entryData = IOH.readData(tar);
						log.info("mode for " + entry.getName() + ": " + entry.getMode());
						files.add(new FileEntry(entry.isDirectory(), entry.getName(), entryData, entry.isModeExecuteByOwner(), entry.isModeReadableByOwner(), entry
								.isModeWriteableByOwner()));
					}
					extracted = true;
					continue;
				}
				case FileMagic.FILE_TYPE_ZIP_ARCHIVE_DATA: {
					ZipInputStream zip = new ZipInputStream(new FastByteArrayDataInputStream(currentData));
					ZipEntry entry;
					while ((entry = zip.getNextEntry()) != null) {
						final byte[] entryData = IOH.readData(zip);
						files.add(new FileEntry(entry.isDirectory(), entry.getName(), entryData, true, true, true));
					}
					extracted = true;
					continue;
				}
				default:
					if (!extracted) {
						r.setMessage("Could not process file, unknown file type (magic code): " + type);
						return null;
					}
			}
		}
		return files;
	}
	static private String chsum(long chsum) {
		return SH.toString(MH.abs(chsum), 62);
	}

	public static class FileEntry {
		public final boolean isDirectory;
		public final byte[] data;
		public final String fileName;
		public final boolean isExecutable;
		public final boolean isReadable;
		public final boolean isWriteable;

		public FileEntry(boolean isDirectory, String fileName, byte[] data, boolean isExecutable, boolean isReadable, boolean isWriteable) {
			this.isDirectory = isDirectory;
			this.fileName = fileName;
			this.data = data;
			this.isExecutable = isExecutable;
			this.isReadable = isReadable;
			this.isWriteable = isWriteable;
		}

		public String getMode() {
			if (isReadable) {
				if (isWriteable)
					return isExecutable ? "rwx" : "rw";
				else
					return isExecutable ? "rx" : "r";
			} else {
				if (isWriteable)
					return isExecutable ? "wx" : "rw";
				else
					return isExecutable ? "x" : "";
			}
		}
	}

}
