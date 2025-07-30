package com.f1.ami.relay;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.f1.ami.amicommon.msg.AmiRelayMessage;
import com.f1.container.ContainerTools;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.FastBufferedInputStream;
import com.f1.utils.FastBufferedOutputStream;
import com.f1.utils.FastByteArrayDataInputStream;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.converter.bytes.BasicFromByteArrayConverterSession;
import com.f1.utils.converter.bytes.BasicToByteArrayConverterSession;
import com.f1.utils.converter.bytes.FromByteArrayConverterSession;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;
import com.f1.utils.mutable.Mutable;
import com.f1.utils.mutable.Mutable.Long;
import com.f1.utils.structs.LongKeyMap;

public class AmiRelayJournal {

	private static final int LARGE_JOURNAL_LOG_SIZE = 1000;
	private static final Logger log = LH.get();
	private Map<Byte, Mutable.Long> centerIds2Seqnum = new HashMap<Byte, Mutable.Long>();
	private long minseqnum = -1L;
	private int centersAtMinseqnum = 0;
	private LongKeyMap<List<AmiRelayMessage>> messages = new LongKeyMap<List<AmiRelayMessage>>();
	Mutable.Long[] seqnums = new Mutable.Long[0];
	private long currentSeqnum = 0;
	private ObjectToByteArrayConverter converter;
	private RandomAccessFile journalOut;
	private File recoveryFile;
	private File keysFile;
	private DataOutputStream keysOut;
	private Map<Short, String> recoveredKeys = new HashMap<Short, String>();
	private boolean guaranteedMessagingEnabled;
	private long diskJournalSize = 0;
	private AmiRelayRoutes router;

	AmiRelayJournal(ContainerTools tools, AmiRelayRoutes router2) throws IOException {
		this.converter = new ObjectToByteArrayConverter(true);
		this.converter.setIdeableGenerator(tools.getServices().getGenerator());
		this.router = router2;
		File persistDir = tools.getOptional(AmiRelayProperties.OPTION_AMI_RELAY_PERSIST_DIR, new File("./persist"));
		this.guaranteedMessagingEnabled = tools.getOptional(AmiRelayProperties.OPTION_AMI_GUARANTEED_MESSAGING_ENABLED, Boolean.FALSE);
		IOH.ensureDir(persistDir);
		this.recoveryFile = new File(persistDir, "messages.recovery");
		this.keysFile = new File(persistDir, "keys.recovery");
		if (!guaranteedMessagingEnabled) {
			this.recoveryFile.delete();
			this.keysFile.delete();
			LH.info(log, "Guaranteed Messaging Disabled");
		} else {
			LH.info(log, "Guaranteed Messaging Enabled, Recovery Files located at ", IOH.getFullPath(recoveryFile), ", ", IOH.getFullPath(keysFile));
			long position = recover();
			this.journalOut = new RandomAccessFile(recoveryFile, "rw");
			this.keysOut = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(keysFile, !this.recoveredKeys.isEmpty())));
			journalOut.seek(position);
		}
	}

	public Map<Short, String> getRecovedKeys() {
		return this.recoveredKeys;
	}

	private long recover() throws FileNotFoundException, IOException {
		long length = recoveryFile.length();
		long position = 0;
		if (length > 0) {
			DataInputStream fis = new DataInputStream(new FastBufferedInputStream(new FileInputStream(recoveryFile)));
			int totMessages = 0;
			try {
				FastByteArrayDataInputStream buf = new FastByteArrayDataInputStream(OH.EMPTY_BYTE_ARRAY);
				FromByteArrayConverterSession session = new BasicFromByteArrayConverterSession(converter, buf);
				while (length - position > 12) {
					long seqnum = fis.readLong();
					int chunkSize = fis.readInt();
					if (position + 12 + chunkSize > length)
						break;
					byte[] chunk = IOH.readData((InputStream) fis, chunkSize);
					buf.reset(chunk);
					List<AmiRelayMessage> t = (List<AmiRelayMessage>) this.converter.read(session);
					totMessages += t.size();
					this.messages.put(seqnum, t);
					this.currentSeqnum = seqnum;
					if (this.minseqnum == -1)
						this.minseqnum = seqnum - 1;
					position += 12 + chunkSize;
				}
			} catch (Exception e) {
				throw new IOException("Failed to recover journal from: " + IOH.getFullPath(recoveryFile), e);
			} finally {
				IOH.close(fis);
			}
			diskJournalSize = totMessages;
			LH.info(log, "Recovered from '", IOH.getFullPath(this.recoveryFile), "': ", totMessages, " message(s), start seqnum=", this.minseqnum, ",  current seqnum=",
					this.currentSeqnum);
			DataInputStream keysIn = new DataInputStream(new FastBufferedInputStream(new FileInputStream(keysFile)));
			try {
				for (;;) {
					int ch1 = keysIn.read();
					if (ch1 == -1)
						break;
					int ch2 = keysIn.read();
					if (ch2 == -1)
						throw new EOFException();
					short key = (short) ((ch1 << 8) + (ch2 << 0));
					String value = keysIn.readUTF();
					this.recoveredKeys.put(key, value);
				}
			} catch (Exception e) {
				throw new IOException("Failed to recover keys from: " + IOH.getFullPath(recoveryFile), e);
			} finally {
				IOH.close(keysIn);
			}
			LH.info(log, "Recovered following keys from '", IOH.getFullPath(this.recoveryFile), "': ", this.recoveredKeys);
		} else {
			LH.info(log, "Centers were up-to-date so nothing to recover");
			minseqnum = 0;
		}
		return position;
	}
	public void addCenter(byte centerId) {
		if (!guaranteedMessagingEnabled)
			return;
		if (centerIds2Seqnum.containsKey(centerId))
			throw new RuntimeException("Duplicate centerId: " + centerId);
		Mutable.Long value = new Mutable.Long(0);
		centerIds2Seqnum.put(centerId, value);
		this.seqnums = AH.append(this.seqnums, value);
		centersAtMinseqnum++;
	}

	public void journal(long seqnum, List<AmiRelayMessage> amiEvents, Map<Short, String> map) {
		if (!guaranteedMessagingEnabled)
			return;
		this.currentSeqnum = seqnum;
		this.messages.putOrThrow(seqnum, amiEvents);
		if (diskJournalSize / LARGE_JOURNAL_LOG_SIZE < (diskJournalSize + amiEvents.size()) / LARGE_JOURNAL_LOG_SIZE) {
			diskJournalSize += amiEvents.size();
			LH.info(log, "Slow Consumers, Disk journal has " + diskJournalSize, " messages.");
		} else
			diskJournalSize += amiEvents.size();
		FastByteArrayDataOutputStream buf = new FastByteArrayDataOutputStream();
		ToByteArrayConverterSession session = new BasicToByteArrayConverterSession(converter, buf, false);
		if (!map.isEmpty()) {
			try {
				for (Entry<Short, String> e : map.entrySet()) {
					this.keysOut.writeShort(e.getKey());
					this.keysOut.writeUTF(e.getValue());
				}
				this.keysOut.flush();
			} catch (Exception e) {
				LH.warning(log, "Could not write keys ", map, "to ", IOH.getFullPath(this.keysFile), e);
			}
		}
		try {
			this.converter.write(amiEvents, session);
			int chunkSize = buf.getCount();
			journalOut.writeLong(seqnum);
			journalOut.writeInt(chunkSize);
			journalOut.write(buf.getBuffer(), 0, chunkSize);

		} catch (Exception e) {
			LH.warning(log, "Could not write journal to " + IOH.getFullPath(this.recoveryFile), " for seqnum ", seqnum, e);
		}
	}
	public void onCenterAcked(byte centerId, long seqnum) {
		if (!guaranteedMessagingEnabled)
			return;
		Long t = CH.getOrThrow(centerIds2Seqnum, centerId);
		if (seqnum == t.value + 1) {
			if (t.value == minseqnum)
				centersAtMinseqnum--;
			t.value = seqnum;
			if (centersAtMinseqnum == 0) {
				calculateMinseqnum();
			}
		} else if (seqnum > t.value) {
			t.value = seqnum;
			calculateMinseqnum();
		}

	}

	private void calculateMinseqnum() {
		long newMinSeqnum = seqnums[0].value;
		centersAtMinseqnum = 1;
		for (int i = 1; i < this.seqnums.length; i++) {
			long sn = this.seqnums[i].value;
			if (sn < newMinSeqnum) {
				this.centersAtMinseqnum = 1;
				newMinSeqnum = sn;
			} else if (sn == newMinSeqnum)
				this.centersAtMinseqnum++;
		}
		setMinimumAcked(newMinSeqnum);
	}
	private void setMinimumAcked(long newMinSeqnum) {
		if (!guaranteedMessagingEnabled)
			return;
		if (newMinSeqnum == this.minseqnum)
			return;
		while (minseqnum < newMinSeqnum)
			this.messages.removeOrThrow(++minseqnum);
		if (this.messages.size() == 0) {
			try {
				this.journalOut.seek(0);
				this.journalOut.setLength(0);
			} catch (IOException e) {
				LH.warning(log, "Could not reset ", IOH.getFullPath(this.recoveryFile), e);
			}
			if (this.diskJournalSize >= LARGE_JOURNAL_LOG_SIZE)
				LH.info(log, "Consumers have recoved, cleared journal");
			this.diskJournalSize = 0;
		}
	}

	public void onCenterDisconnected(byte centerId) {
		if (!guaranteedMessagingEnabled)
			return;
		Long seqnum = centerIds2Seqnum.get(centerId);
		if (seqnum == null)
			LH.info(log, "Center '", centerId, "' disconnected");
		else
			LH.info(log, "Center '", centerId, "' disconnected, last known acked seqnum: ", seqnum.value);
	}

	public void getEventsAfterSeqnum(byte centerId, long seqnum, List<AmiRelayMessage> sink) {
		if (!guaranteedMessagingEnabled)
			return;
		int size = sink.size();
		if (seqnum < this.minseqnum) {
			LH.w(log, "Center '", centerId, "' request snapshot from old seqnum: " + seqnum, " resetting to ", this.minseqnum);
			seqnum = this.minseqnum;
		}
		while (seqnum < this.currentSeqnum) {
			List<AmiRelayMessage> t = this.messages.get(++seqnum);
			if (t != null) {
				for (int i = 0, s = t.size(); i < s; i++) {
					AmiRelayMessage msg = t.get(i);
					if (this.router.getThreadSafeRouter().shouldRoute(msg, centerId))
						sink.add(msg);
				}
			}
		}
		LH.info(log, "Center '", centerId, "' requested Recovery after seqnum ", seqnum, ", Sent back ", sink.size() - size, " events to catch up to seqnum", this.currentSeqnum);
	}
	public long getCurrentSeqnum() {
		return this.currentSeqnum;
	}

	public boolean guaranteedMessagingEnabled() {
		return this.guaranteedMessagingEnabled;
	}

}
