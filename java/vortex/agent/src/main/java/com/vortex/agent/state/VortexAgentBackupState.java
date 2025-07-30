package com.vortex.agent.state;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.f1.container.impl.BasicState;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.FastBufferedInputStream;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;
import com.f1.utils.mutable.Mutable;
import com.f1.utils.structs.Tuple2;
import com.f1.vortexcommon.msg.agent.VortexAgentBackupFile;
import com.f1.vortexcommon.msg.agent.VortexAgentFile;
import com.f1.vortexcommon.msg.eye.VortexEyeBackup;
import com.vortex.agent.VortexAgentUtils;

public class VortexAgentBackupState extends BasicState {
	final static private Logger log = LH.get(VortexAgentBackupState.class);

	public static final long MIN_EVALUATE_FILE_PERIOD_MS = 10000; //After a file has been evaluated, wait at least this long before evaluating again.
	public static final long MIN_STABLE_FILE_PERIOD_MS = 5000; //Do not evaluate files where the modified timestamp is within this period of now.
	public static final long CHECK_FILES_PERIOD_MS = 60000; //The period to wait between scanning directories 
	public static final long MAX_DATA_CAPTURE_SIZE = 1000 * 1000 * 10; //The period to wait between scanning directories 

	public static final int MAX_DATA_SEND_PER_MESSAGE = 1000 * 1000 * 50;//Don't allow a single message to send more than this much data

	private VortexEyeBackup backup;
	private SortedMap<String, VortexAgentBackupFile> files = new TreeMap<String, VortexAgentBackupFile>();
	private TextMatcher ignoreExpression;
	private int filesCount;
	private int ignoredFilesCount;
	private long bytesCount;
	private long latestModifiedTime;
	//private String manifest;
	//private long lastManifestCheckTime;

	private String rootPath;

	private long backupId;

	private boolean polling;

	private long checkDelay;

	public void init(VortexEyeBackup backup, List<VortexAgentBackupFile> files) {
		this.files.clear();
		for (VortexAgentBackupFile file : files) {
			if (file.getData() != null)
				throw new IllegalArgumentException("should not have data");
			file.setMask(MH.clearBits(file.getMask(), VortexAgentFile.DATA_DEFPLATED));
			this.files.put(file.getPath(), file);
		}
		this.filesCount = -1;
		this.bytesCount = -1;
		this.backup = backup;
		this.backupId = backup.getId();
		this.rootPath = this.backup.getSourcePath();
		this.ignoreExpression = SH.m(backup.getIgnoreExpression());
		this.checkDelay = this.getPartition().getContainer().getTools().getOptional("f1.backup.check.period.ms", CHECK_FILES_PERIOD_MS);
	}

	public boolean evaluate(Map<String, VortexAgentBackupFile> addedSink, Map<String, VortexAgentBackupFile> updatedSink, Map<String, VortexAgentBackupFile> removedSink) {
		long now = getPartition().getContainer().getTools().getNow();
		Map<String, VortexAgentBackupFile> sink = new HashMap<String, VortexAgentBackupFile>();
		Mutable.Int ignoreSink = new Mutable.Int();
		evaluate(now, new File(rootPath), sink, ignoreSink);
		boolean r = false;
		if (this.ignoredFilesCount != ignoreSink.value) {
			r = true;
			this.ignoredFilesCount = ignoreSink.value;
		}

		boolean manifestChanged = this.filesCount == -1;
		int totalBytesForThisMessage = 0;
		Set<String> revertDueToMessageSize = new HashSet<String>();
		for (Tuple2<VortexAgentBackupFile, VortexAgentBackupFile> e : CH.join(this.files, sink).values()) {
			final VortexAgentBackupFile old = e.getA(), nuw = e.getB();
			if (old == nuw)
				continue;
			manifestChanged = true;
			if (old == null) {//add
				long size = nuw.getSize();
				if (size >= MAX_DATA_CAPTURE_SIZE) {
					addedSink.put(nuw.getPath(), nuw);
				} else {
					if (totalBytesForThisMessage < MAX_DATA_SEND_PER_MESSAGE) {
						VortexAgentBackupFile nuw2 = nuw.clone();
						int bytesCount = processData(nuw2);
						totalBytesForThisMessage += bytesCount;
						addedSink.put(nuw2.getPath(), nuw2);
					} else
						revertDueToMessageSize.add(nuw.getPath());
				}
			} else if (nuw == null) {//remove
				VortexAgentBackupFile old2 = old.clone();
				old2.setModifiedTime(now);
				removedSink.put(old2.getPath(), old2);
			} else {//update
				long size = nuw.getSize();
				if (size >= MAX_DATA_CAPTURE_SIZE || (old.getSize() == nuw.getSize() && old.getChecksum() == nuw.getChecksum())) {
					updatedSink.put(nuw.getPath(), nuw);
				} else {
					if (totalBytesForThisMessage < MAX_DATA_SEND_PER_MESSAGE) {
						if (nuw.getChecksum() != old.getChecksum() || nuw.getSize() != old.getSize()) {
							VortexAgentBackupFile nuw2 = nuw.clone();
							int bytesCount = processData(nuw2);
							totalBytesForThisMessage += bytesCount;
							updatedSink.put(nuw2.getPath(), nuw2);
						} else
							updatedSink.put(nuw.getPath(), nuw);
					} else
						revertDueToMessageSize.add(nuw.getPath());
				}
			}
		}

		if (manifestChanged) {
			this.filesCount = 0;
			this.latestModifiedTime = 0;
			this.bytesCount = 0;

			for (String file : revertDueToMessageSize) {//
				VortexAgentBackupFile old = this.files.get(file);
				if (old == null)
					sink.remove(file);
				else
					sink.put(file, old);
			}
			this.files.clear();
			this.files.putAll(sink);
			for (VortexAgentFile bf : this.files.values()) {
				this.filesCount++;
				this.bytesCount += bf.getSize();
				this.latestModifiedTime = Math.max(this.latestModifiedTime, bf.getModifiedTime());
			}
			r = true;
		}
		return r;
	}
	private void evaluate(long now, File dir, Map<String, VortexAgentBackupFile> sink, Mutable.Int ignoredSink) {
		if (!dir.isDirectory())
			return;
		for (File file : AH.noEmpty(dir.listFiles(), OH.EMPTY_FILE_ARRAY)) {
			if (file.isDirectory()) {
				evaluate(now, file, sink, ignoredSink);
			} else {
				String fullPath = IOH.getFullPath(file);
				fullPath = IOH.toUnixFormat(fullPath);
				if (shouldIgnore(fullPath)) {
					ignoredSink.value++;
				} else {
					VortexAgentBackupFile existing = this.files.get(fullPath);
					if (existing != null && existing.getNow() > now - MIN_EVALUATE_FILE_PERIOD_MS) {
						existing = setStatus(existing, VortexAgentBackupFile.STATUS_JUST_UPDATED);
						sink.put(fullPath, existing);
						continue;
					}
					final long modified = file.lastModified();
					if (MH.diff(modified, now) < MIN_STABLE_FILE_PERIOD_MS) {
						if (existing != null) {
							existing = setStatus(existing, VortexAgentBackupFile.STATUS_UNSTABLE);
							sink.put(fullPath, existing);
						}
						continue;
					}
					final long length = file.length();
					final short mask = getMask(file);
					this.bytesCount += length;
					if (existing != null && length == existing.getSize() && modified == existing.getModifiedTime() && mask == existing.getMask())
						sink.put(existing.getPath(), setStatus(existing, VortexAgentBackupFile.STATUS_NONE));
					else
						sink.put(fullPath, newVortexAgentFile(now, fullPath, mask, length, modified, getChecksum(file)));
				}
			}
		}
	}
	private VortexAgentBackupFile setStatus(VortexAgentBackupFile bf, byte status) {
		if (bf.getStatus() == status)
			return bf;
		//if (bf.isLocked())
		bf = bf.clone();
		bf.setStatus(status);
		bf.lock();
		return bf;
	}

	private VortexAgentBackupFile newVortexAgentFile(long now, String fullPath, short mask, long length, long modified, long checksum) {
		VortexAgentBackupFile r = nw(VortexAgentBackupFile.class);
		r.setPath(fullPath);
		r.setMask(mask);
		r.setSize(length);
		r.setModifiedTime(modified);
		r.setChecksum(checksum);
		r.setBackupId(this.backupId);
		r.setNow(now);
		r.setStatus(VortexAgentBackupFile.STATUS_JUST_UPDATED);
		r.lock();
		return r;
	}

	static public long getChecksum(File file) {
		InputStream is = null;
		try {
			is = new FastBufferedInputStream(new FileInputStream(file));
			return MH.abs(IOH.checkSumBsdLong(is));
		} catch (Exception e) {
			//LH.warning(log, "Error processing checksum for file: ", IOH.getFullPath(file), e);
			return -1;
		} finally {
			IOH.close(is);
		}
	}
	static public short getMask(File file) {
		short r = 0;
		if (file.canRead())
			r |= VortexAgentFile.READABLE;
		if (file.canWrite())
			r |= VortexAgentFile.WRITEABLE;
		if (file.canExecute())
			r |= VortexAgentFile.EXECUTABLE;
		if (file.isDirectory())
			r |= VortexAgentFile.DIRECTORY;
		else if (file.isFile())
			r |= VortexAgentFile.FILE;
		if (file.getName().startsWith("."))
			r |= VortexAgentFile.HIDDEN;

		return r;

	}
	private boolean shouldIgnore(String fullPath) {
		return ignoreExpression.matches(SH.stripPrefix(fullPath, rootPath, true));
	}

	//	private static class BackupFile {
	//		final private String fullPath, mode;
	//		final private long length, modified, checksum;
	//
	//		public BackupFile(String fullpath, String mode, long length, long modified, long checksum) {
	//			this.fullPath = fullpath;
	//			this.mode = mode;
	//			this.length = length;
	//			this.modified = modified;
	//			this.checksum = checksum;
	//		}
	//	}

	public VortexEyeBackup getBackup() {
		return backup;
	}
	public SortedMap<String, VortexAgentBackupFile> getFiles() {
		return files;
	}
	public TextMatcher getIgnoreExpression() {
		return ignoreExpression;
	}
	public int getFilesCount() {
		return filesCount;
	}
	public int getIgnoredFilesCount() {
		return ignoredFilesCount;
	}
	public long getBytesCount() {
		return bytesCount;
	}
	//public long getLatestModifiedTime() {
	//return latestModifiedTime;
	//}
	//public String getManifest() {
	//return manifest;
	//}
	//public long getLastManifestCheckTime() {
	//return lastManifestCheckTime;
	//}

	public long getDelay() {
		return checkDelay;
	}

	public void clear() {
		this.backup = null;
		this.files.clear();
	}

	public boolean isPolling() {
		return polling;
	}

	public void setIsPolling(boolean b) {
		polling = b;
	}

	public VortexEyeBackup evalBackup() {
		VortexEyeBackup r = this.backup.clone();
		r.setBytesCount(this.bytesCount);
		r.setFileCount(this.filesCount);
		r.setIgnoredFileCount(this.ignoredFilesCount);
		r.setLatestModifiedTime(this.latestModifiedTime);
		return r;
	}

	//Set<String> queuedForNextMessage = new LinkedHashSet<String>();
	//public void queueFileForDataSend(String filePath) {
	//queuedForNextMessage.add(filePath);
	//}
	//
	//public boolean dequeueFileForDataSend(String filePath) {
	//return queuedForNextMessage.remove(filePath);
	//}
	//
	//public Set<String> getQueuedForDataSend() {
	//return queuedForNextMessage;
	//}
	//
	//public void dequeueFileForDataSend(Set<String> filePaths) {
	//queuedForNextMessage.removeAll(filePaths);
	//}

	//public static void main(String a[]) {
	//VortexAgentBackupState vabs = new VortexAgentBackupState("/tmp/rob", "^*.log*");
	//while (true) {
	//OH.sleep(1000);
	//if (vabs.evaluate(System.currentTimeMillis())) {
	//System.out.println("manifest length: " + vabs.getManifest().length());
	//System.out.println("ignored: " + vabs.getIgnoredFilesCount());
	//System.out.println("total bytes: " + vabs.getBytesCount());
	//System.out.println("total files: " + vabs.getFilesCount());
	//System.out.println("latest mod:  " + new Date(vabs.getLatestModifiedTime()));
	//}
	//}

	//}

	static int processData(VortexAgentBackupFile file) {
		if (file.getSize() <= VortexAgentBackupState.MAX_DATA_CAPTURE_SIZE) {
			try {
				final byte[] data = IOH.readData(new File(file.getPath()));
				if (data.length < VortexAgentBackupState.MAX_DATA_CAPTURE_SIZE)
					file.setData(data);
				VortexAgentUtils.compressFile(file);
				file.setDataOffset(0L);
				return file.getData().length;
			} catch (IOException e) {
				LH.warning(log, "error with file: ", file, e);
			}
		}
		return 0;
	}
}
