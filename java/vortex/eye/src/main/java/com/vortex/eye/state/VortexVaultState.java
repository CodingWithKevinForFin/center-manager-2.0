package com.vortex.eye.state;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import com.f1.container.Container;
import com.f1.container.impl.BasicState;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.LongSet;
import com.f1.vortexcommon.msg.eye.VortexVaultEntry;
import com.vortex.eye.VortexEyeDbService;

public class VortexVaultState extends BasicState {
	final private static Logger log = LH.get(VortexVaultState.class);
	public static final String SUFFIX = ".vvt";
	private static final long MAX_CACHE = 1024 * 1024 * 1000;
	final private File baseDir;

	private LongSet idsToDiskEntries = new LongSet();
	private LongKeyMap<VortexVaultEntry> idsToEntries = new LongKeyMap<VortexVaultEntry>();
	private LinkedList<VortexVaultEntry> cached = new LinkedList<VortexVaultEntry>();
	long memcachedSize = 0;
	private BasicMultiMap.List<Long, VortexVaultEntry> checksumToHardEntries = new BasicMultiMap.List<Long, VortexVaultEntry>();
	final private VortexEyeDbService dbservice;

	public VortexVaultState(Container container, VortexEyeDbService dbservice) {
		this.baseDir = container.getTools().getOptional("vortex.vault.dir", new File("data/vault"));
		this.dbservice = dbservice;
	}
	public LongSet scanDisk() {
		try {
			IOH.ensureDir(baseDir);
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
		LongSet r = new LongSet();
		for (String value : baseDir.list()) {
			if (value.endsWith(SUFFIX)) {
				long id = Long.parseLong(SH.stripSuffix(value, SUFFIX, true));
				r.add(id);
			}
		}
		LH.info(log, "Vortex Vault located at ", baseDir, ".  Contains: ", r.size(), " file(s)");
		return r;
	}

	public void init() throws Exception {
		if (dbservice == null) {
			log.info("Skipping Vortex Vault initialization, No database");
			return;
		}
		Connection conn = dbservice.getConnection();
		Iterable<VortexVaultEntry> entries;
		try {
			log.info("Query Vortex Vault");
			entries = dbservice.queryVortexVault(conn);
		} finally {
			IOH.close(conn);
		}
		LongSet cachedToDisk = scanDisk();
		long totalSize = 0;
		for (VortexVaultEntry entry : entries) {
			totalSize += entry.getDataLength();
			if (entry.getData() != null)
				throw new IllegalArgumentException("Initial set should not have data: " + entry);
			final long id = entry.getId();
			idsToEntries.putOrThrow(id, entry);
			if (cachedToDisk.remove(id)) {
				idsToDiskEntries.add(id);
			}
			if (entry.getSoftlinkVvid() == 0L)
				checksumToHardEntries.putMulti(entry.getChecksum(), entry);
		}
		if (cachedToDisk.size() > 0)
			LH.warning(log, "Old entries found in vortex vault cache:" + SH.join(",", cachedToDisk));
		LH.info(log, "Initialized Vortex Vault: ", idsToDiskEntries.size(), " in database. ", cachedToDisk.size(), " on disk (at ", baseDir, "). Size in db: ",
				SH.formatMemory(totalSize));
	}

	public byte[] getDataFromVault(long vvid) throws Exception {
		VortexVaultEntry entry = idsToEntries.getOrThrow(vvid);
		final long id;

		//check to see if this is a soft link
		if (entry.getSoftlinkVvid() != 0L)
			entry = idsToEntries.get(id = entry.getSoftlinkVvid());
		else
			id = vvid;
		if (entry.getData() != null) {
			return entry.getData();
		}

		//does this exist on disk?
		byte r[] = null;
		if (idsToDiskEntries.contains(id)) {
			try {
				r = IOH.readData(toFile(id));
				OH.assertEq(entry.getDataLength(), r.length);
			} catch (Exception e) {
				r = null;
				LH.warning(log, "bad entry on disk: ", id, e);
			}
		}

		//not on disk, fall back to the database
		if (r == null) {
			Connection conn = dbservice.getConnection();
			try {
				List<VortexVaultEntry> dbentries = dbservice.queryVortexVaultData(conn, id);
				if (dbentries.size() == 1)
					r = dbentries.get(0).getData();
			} finally {
				IOH.close(conn);
			}
			if (r == null)
				throw new RuntimeException("data not found in db: " + id + " (vvid=" + vvid + ")");
			OH.assertEq(entry.getDataLength(), r.length);
			storeToDisk(id, r);
		}
		entry.setData(r);
		storeInCache(entry);
		return r;
	}
	public void storeDataToVault(long id, byte[] data) throws Exception {

		if (idsToEntries.containsKey(id))
			throw new RuntimeException("already exists: " + id);
		long checksum = IOH.checkSumBsdLong(data);
		VortexVaultEntry entry = nw(VortexVaultEntry.class);
		entry.setId(id);
		entry.setNow(getPartition().getContainer().getTools().getNow());
		entry.setDataLength(data.length);
		entry.setChecksum(checksum);

		//can we make this a soft link?
		List<VortexVaultEntry> entries = this.checksumToHardEntries.get(checksum);
		if (entries != null) {
			for (VortexVaultEntry sameCsum : entries) {//same checksum
				if (sameCsum.getDataLength() == data.length) {//and same length.. this is looking likely
					if (Arrays.equals(getDataFromVault(sameCsum.getId()), data)) {
						entry.setSoftlinkVvid(sameCsum.getId());
						break;
					}
				}
			}
		}

		//Not a soft link...
		if (entry.getSoftlinkVvid() == 0L)
			entry.setData(data);

		Connection conn = dbservice.getConnection();
		try {
			dbservice.insertVortexVaultEntry(conn, entry);
		} finally {
			IOH.close(conn);
		}

		idsToEntries.put(id, entry);
		if (entry.getSoftlinkVvid() == 0L) {
			storeToDisk(id, data);
			storeInCache(entry);
			checksumToHardEntries.putMulti(entry.getChecksum(), entry);
		}
	}

	private File toFile(long id) {
		return new File(baseDir, id + SUFFIX);
	}

	private void storeToDisk(long id, byte[] data) {
		try {
			IOH.writeData(toFile(id), data);
			idsToDiskEntries.add(id);
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}

	}
	private void storeInCache(VortexVaultEntry entry) {
		final byte[] data = entry.getData();
		if (data.length > MAX_CACHE) {
			entry.setData(null);
		} else {
			entry.setData(data);
			this.memcachedSize += data.length;
			while (this.memcachedSize > MAX_CACHE && !cached.isEmpty()) {
				final VortexVaultEntry uncache = this.cached.removeFirst();
				this.memcachedSize -= uncache.getData().length;
				uncache.setData(null);
			}
			cached.add(entry);
			LH.fine(log, "Vortex Vault Cached: ", cached.size(), " entries, ", SH.formatMemory(memcachedSize));
		}
	}

}
