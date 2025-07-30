package com.f1.persist.sinks;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.base.IdeableGenerator;
import com.f1.persist.PersistEvent;
import com.f1.persist.PersistException;
import com.f1.persist.PersistReadStore;
import com.f1.persist.PersistSink;
import com.f1.persist.impl.BasicPersistValuedListener;
import com.f1.persist.impl.converter.PersistToByteArrayConverterSession;
import com.f1.persist.reader.TransactionalPersistReader;
import com.f1.persist.writer.SnapshotPersistStore;
import com.f1.persist.writer.TransactionalPersistWriter;
import com.f1.persist.writer.TransactionalPersistWriterFactory;
import com.f1.utils.FastBufferedOutputStream;
import com.f1.utils.FastByteArrayDataInputStream;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;

public class FilePersister implements PersistSink, Runnable {
	public static final String FILE_NAME_PREFIX = "persist";
	private static final Logger log = Logger.getLogger(FilePersister.class.getName());

	public static final int DEFAULT_MAX_DELTA_SIZE = 1024 * 1024;
	private FileOutputStream deltaOut;
	final private File deltaFile;
	final private File snapshotFile;
	final private File tmpSnapshotFile;
	final private File trashSnapshotFile;
	final private TransactionalPersistWriterFactory persistWriter;
	final private long maxDeltaSize;
	final private AtomicBoolean isWritingSnapshot = new AtomicBoolean(false);
	final private Thread snapshotThread;
	final private Collection<Object> recoveredObjects;
	final private IdeableGenerator generator;
	final private boolean async;
	final private Object snapshotSemephore = new Object();

	private long deltaFileSize;
	private long lastCheckPoint = 0;
	private volatile boolean running;

	public FilePersister(File outputFile, TransactionalPersistWriterFactory persistWriter, IdeableGenerator generator, boolean async) throws Exception {
		this(outputFile, persistWriter, generator, async, DEFAULT_MAX_DELTA_SIZE);
	}

	public FilePersister(File outputFile, TransactionalPersistWriterFactory persistWriter, IdeableGenerator generator, boolean async, long maxDeltaSize) throws Exception {
		if (generator == null)
			throw new NullPointerException("generator");
		this.generator = generator;
		this.deltaFile = new File(outputFile, FILE_NAME_PREFIX + ".deltas");
		this.snapshotFile = new File(outputFile, FILE_NAME_PREFIX + ".snapshot");
		this.tmpSnapshotFile = new File(outputFile, FILE_NAME_PREFIX + ".snapshot.tmp");
		this.trashSnapshotFile = new File(outputFile, FILE_NAME_PREFIX + ".snapshot.trash");
		this.persistWriter = persistWriter;
		this.lastCheckPoint = 0;
		this.snapshotThread = new Thread(this, "File Snapshot Thread");
		this.recoveredObjects = new ArrayList<Object>();
		this.running = false;
		this.async = async;
		this.maxDeltaSize = maxDeltaSize;
	}

	public void startup() throws Exception {
		if (running)
			throw new IllegalStateException("already started");
		if (trashSnapshotFile.exists()) {
			LH.info(log, "Recovering from previous crash, where move from tmp to snapshot was in progress, recoving trash file:", IOH.getFullPath(trashSnapshotFile));
			snapshotFile.delete();
			IOH.renameOrThrow(trashSnapshotFile, snapshotFile);
		}
		BasicPersistValuedListener listener = persistWriter.createListener();

		this.recoveredObjects.addAll(processingExistingData(generator));
		try {
		} catch (Exception e) {
			throw new PersistException("error writing initial snapshot: " + IOH.getFullPath(snapshotFile), e);
		}
		LH.info(log, "Recovered ", recoveredObjects.size(), " persisted object(s) from: ", IOH.getFullPath(deltaFile), " and ", IOH.getFullPath(snapshotFile));
		deltaFile.delete();
		snapshotFile.delete();
		this.running = true;
		snapshotThread.start();
		this.deltaOut = new FileOutputStream(deltaFile, false);
		persistWriter.addSink(this, async, true);
		tmpSnapshotFile.delete();
	}

	public void popRecoveredObjects(Collection<Object> sink) {
		sink.addAll(recoveredObjects);
		recoveredObjects.clear();
	}

	private Collection<Object> processingExistingData(IdeableGenerator generator) throws Exception {
		LH.info(log, "Recoving from disk....");
		Map<Long, Object> sink = new HashMap<Long, Object>();
		TransactionalPersistReader persistReader = new TransactionalPersistReader(generator);
		PersistReadStore store = persistReader.getStore();
		Collection<Object> r = new ArrayList<Object>();
		long checkPoint = 0;
		int snapshotTransactions = 0;
		if (snapshotFile.exists()) {

			FastByteArrayDataInputStream in = new FastByteArrayDataInputStream(IOH.readData(snapshotFile));
			checkPoint = in.readLong();
			while (in.available() > 0) {
				if (!persistReader.consumeTransaction(in))
					throw new PersistException("bad snapshot file: " + IOH.getFullPath(snapshotFile));
				try {
					persistReader.pumpTransaction();
				} catch (Exception e) {
					if (in.available() == 0) {
						LH.warning(log, "End of file reached, so dropping last transaction: ", IOH.getFullPath(snapshotFile), "exception message: ", e);
						break;
					} else
						throw new RuntimeException("Error near byte: " + in.getPosition() + " (remaining bytes are " + in.available() + ") in snapshot file: "
								+ IOH.getFullPath(snapshotFile), e);
				}
				snapshotTransactions++;
			}
			in.close();
			LH.info(log, "Processed existing Snapshot file: ", IOH.getFullPath(snapshotFile));
		}
		store.getObjects(sink);
		if (log.isLoggable(Level.FINEST))
			for (Map.Entry<Long, Object> e : sink.entrySet()) {
				LH.finest(log, "FROM SNAPSHOT: ", e.getKey(), " = ", SH.s(e.getValue()));
			}
		int deltaTransactions = 0;
		boolean wasPartialTransaction = false;
		if (deltaFile.exists()) {

			final DataInputStream in = new DataInputStream(new FileInputStream(deltaFile));
			try {
				IOH.skip(in, checkPoint);
				for (;;) {
					if (in.available() == 0)
						break;
					else if (!persistReader.consumeTransaction(in)) {
						wasPartialTransaction = true;
						break;
					}
					persistReader.pumpTransaction();
					deltaTransactions++;
				}
				in.close();
			} catch (Exception e) {
				throw new RuntimeException("Error (remaining bytes are " + in.available() + ") in delta file: " + IOH.getFullPath(snapshotFile), e);
			}
		}
		LH.info(log, "Processed ", deltaTransactions, " delta transaction(s) & ", snapshotTransactions, " snapshot transaction(s). ",
				(wasPartialTransaction ? " (Last Transaction was partial)" : ""), " from existing delta file (check point byte=", checkPoint + "): ", IOH.getFullPath(deltaFile));
		store.getObjects(sink);
		r.addAll(sink.values());
		return r;
	}

	@Override
	public void write(byte[] data) {
		try {
			deltaOut.write(data);
			deltaFileSize += data.length;
			deltaOut.flush();
			if (deltaFileSize - lastCheckPoint > maxDeltaSize && !isWritingSnapshot.get()) {
				synchronized (snapshotSemephore) {
					if (deltaFileSize - lastCheckPoint > maxDeltaSize && isWritingSnapshot.compareAndSet(false, true)) {
						lastCheckPoint = deltaFileSize;
						snapshotSemephore.notify();
					}
				}
			}
		} catch (IOException e) {
			throw new PersistException("error writing to file: " + IOH.getFullPath(deltaFile), e);
		}
	}

	private void writeSnapshot(TransactionalPersistWriterFactory persistWriter) throws Exception {
		if (log.isLoggable(Level.INFO))
			LH.info(log, "Writing a persist snapshot to ", IOH.getFullPath(this.tmpSnapshotFile));
		Map<Object, Long> snapshot = new HashMap<Object, Long>();
		persistWriter.getObjects(snapshot);
		FastByteArrayDataOutputStream snapshotBuffer = new FastByteArrayDataOutputStream();
		SnapshotPersistStore store = new SnapshotPersistStore(snapshot, persistWriter);
		ToByteArrayConverterSession session = new PersistToByteArrayConverterSession(persistWriter.getConverter(), snapshotBuffer, store, false);
		try {
			for (Map.Entry<Object, Long> e : snapshot.entrySet()) {
				Long id = snapshot.get(e.getKey());
				if (id == null || id.equals(0L))
					continue;
				if (PersistEvent.WRITE_CHECKSUM) {
					snapshotBuffer.writeByte(PersistEvent.CHECKSUM1);
					snapshotBuffer.writeByte(PersistEvent.CHECKSUM2);
				}
				snapshotBuffer.writeByte(PersistEvent.ADD);
				persistWriter.getConverter().write(e.getKey(), session);
			}
		} catch (IOException ex) {
			throw OH.toRuntime(ex);
		}
		FastBufferedOutputStream out = new FastBufferedOutputStream(new FileOutputStream(tmpSnapshotFile));
		out.writeLong(lastCheckPoint);
		out.write(TransactionalPersistWriter.toTransaction(snapshotBuffer));
		out.close();
		if (snapshotFile.exists())
			IOH.renameOrThrow(snapshotFile, trashSnapshotFile);
		IOH.renameOrThrow(tmpSnapshotFile, snapshotFile);
		trashSnapshotFile.delete();
		if (log.isLoggable(Level.INFO))
			LH.info(log, "finished with persist snapshot to ", IOH.getFullPath(this.snapshotFile) + ", wrote ", snapshot.size(), " entities, last delta checkpoint: ",
					lastCheckPoint, ", file size: ", snapshotFile.length(), " byte(s)");
	}

	@Override
	public void run() {
		while (running) {
			synchronized (snapshotSemephore) {
				OH.wait(snapshotSemephore);
			}
			if (isWritingSnapshot.get()) {
				try {
					writeSnapshot(this.persistWriter);
				} catch (Exception e) {
					LH.severe(log, "Error writing snapshot", e);
				}
				isWritingSnapshot.set(false);
			}

		}
	}
}
