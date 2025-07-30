package com.f1.anvil.loader;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import com.f1.ami.client.AmiClient;
import com.f1.ami.relay.AmiRelayProperties;
import com.f1.anvil.utils.AnvilMarketData;
import com.f1.anvil.utils.AnvilMarketDataMap;
import com.f1.bootstrap.Bootstrap;
import com.f1.container.impl.dispatching.RootPartitionActionRunner;
import com.f1.utils.CH;
import com.f1.utils.CopyOnWriteHashMap;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;
import com.f1.utils.TimeOfDay;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.ConcurrentHashSet;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.impl.CharSequenceHasher;
import com.f1.utils.structs.Tuple2;

public class AnvilFileLoaderManager implements Runnable {

	private static final Logger log = LH.get();
	public static final String OPTION_ANVIL_FILES = "anvil.files";
	public static final String OPTION_ANVIL_REFERENCE_FILES = "anvil.reference.files";
	public static final String OPTION_ANVIL_DELETE_FILES = "anvil.delete.files";
	public static final String OPTION_ANVIL_HISTORY_SNAPSHOT_PERIOD_MS = "anvil.nistory.snapshot.period.ms";
	public static final String OPTION_ANVIL_REPLAY_SKIP_UNTIL = "anvil.replay.skip.until.time";
	public static final String OPTION_ANVIL_REPLAY_START_TIME = "anvil.replay.start.time";
	public static final String OPTION_ANVIL_REPLAY_END_TIME = "anvil.replay.end.time";
	public static final String OPTION_ANVIL_REPLAY_SYMBOLS = "anvil.replay.symbols";
	public static final String OPTION_ANVIL_REPLAY_SPEED = "anvil.replay.speed";
	public static final String OPTION_ANVIL_REPLAY_ENABLED = "anvil.replay.enabled";

	public static final String OPTION_ANVIL_FILE_FLUSH_MAX_QUEUE_SIZE = "anvil.file.flush.max.queue.size";
	public static final String OPTION_ANVIL_FILE_FLUSH_PERIOD = "anvil.file.flush.period";
	public static final String OPTION_ANVIL_FILE_LOADER_LIMIT = "anvil.file.loader.limit";
	public static final String OPTION_ANVIL_LOG_FILE_STATS_RATE = "anvil.log.file.stats.rate";
	public static final String OPTION_ANVIL_WATCH_PERIOD = "anvil.file.watch.period";

	public static final String OPTION_ANVIL_SPOT_CURRENCY = "anvil.spot.currency";//currency
	public static final String OPTION_ANVIL_OPEN_TIME = "anvil.open.time";//format 
	public static final String OPTION_ANVIL_CLOSE_TIME = "anvil.close.time";//format 

	final private RootPartitionActionRunner actionRunner;

	final private PropertyController properties;
	final private long startTime;

	final private SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss.SSSS");
	final private long logRate;
	final private int limit;
	final private int maxQueueSize;
	final private AnvilMarketDataMap marketdata;
	final private boolean replayEnabled;
	final private long flushPeriod;
	final private long historySnapshotPeriod;
	final private int port;
	final private double replaySpeed;
	final private TimeOfDay replaySkipTime;
	final private TimeOfDay replayEndTime;
	final private TimeOfDay replayStartTime;
	final private long fileWatchPeriod;
	final private String replaySymbols;
	final private HasherSet<CharSequence> replaySymbolsSet;

	final private Set<String> watchFiles = new ConcurrentHashSet<String>();
	final private Set<Tuple2<String, String>> watchDirs = new ConcurrentHashSet<Tuple2<String, String>>();
	final private AtomicReference<String> tradesFileName = new AtomicReference<String>();
	final private AtomicReference<String> nbbosFileName = new AtomicReference<String>();
	final private Map<String, AnvilLoader> tailingFiles = new HashMap<String, AnvilLoader>();
	final private AnvilMarketData marketDataInstance;
	final private boolean hasSymbolFilterList;
	final private Set<String> referenceFilesSet;
	final private List<AnvilFileLoader> refFileLoaders = new ArrayList<AnvilFileLoader>();
	final private ArrayList<String> stopWatchingFor = new ArrayList<String>();

	public static AnvilFileLoaderManager main(Bootstrap cb, RootPartitionActionRunner actionRunner, AnvilMarketData mdi) throws IOException {
		PropertyController properties = cb.getProperties();
		String cleanFiles = properties.getOptional(OPTION_ANVIL_DELETE_FILES, Caster_String.INSTANCE);

		if (SH.is(cleanFiles))
			for (String file : SH.split(",", cleanFiles)) {
				IOH.delete(new File(file));
			}
		long startTime = System.currentTimeMillis();
		return new AnvilFileLoaderManager(actionRunner, properties, startTime, mdi);
	}
	public AnvilFileLoaderManager(RootPartitionActionRunner actionRunner, PropertyController properties, long startTime, AnvilMarketData marketDataInstance) {
		this.actionRunner = actionRunner;
		this.timeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		this.properties = properties;
		this.marketDataInstance = marketDataInstance;
		this.marketdata = this.marketDataInstance.createLocalMarketDataMap();
		this.logRate = properties.getOptional(OPTION_ANVIL_LOG_FILE_STATS_RATE, 100000);
		this.limit = properties.getOptional(OPTION_ANVIL_FILE_LOADER_LIMIT, -1);
		this.flushPeriod = properties.getOptional(OPTION_ANVIL_FILE_FLUSH_PERIOD, 1000);
		this.maxQueueSize = properties.getOptional(OPTION_ANVIL_FILE_FLUSH_MAX_QUEUE_SIZE, 10000);
		this.replayEnabled = properties.getOptional(OPTION_ANVIL_REPLAY_ENABLED, Boolean.FALSE);
		this.fileWatchPeriod = properties.getOptional(OPTION_ANVIL_WATCH_PERIOD, 1000);

		this.historySnapshotPeriod = properties.getOptional(OPTION_ANVIL_HISTORY_SNAPSHOT_PERIOD_MS, 60000L);
		this.port = properties.getOptional(AmiRelayProperties.OPTION_AMI_PORT, AmiClient.DEFAULT_PORT);

		if (replayEnabled) {
			this.replaySpeed = properties.getOptional(OPTION_ANVIL_REPLAY_SPEED, 1d);
			this.replaySkipTime = parseTime(properties, OPTION_ANVIL_REPLAY_SKIP_UNTIL);
			this.replayStartTime = parseTime(properties, OPTION_ANVIL_REPLAY_START_TIME);
			this.replayEndTime = parseTime(properties, OPTION_ANVIL_REPLAY_END_TIME);
			this.replaySymbols = properties.getOptional(OPTION_ANVIL_REPLAY_SYMBOLS);
			if (SH.isnt(this.replaySymbols)) {
				this.replaySymbolsSet = null;
			} else {
				this.replaySymbolsSet = new HasherSet<CharSequence>(CharSequenceHasher.INSTANCE);
				for (String s : SH.splitWithEscape(',', '\\', this.replaySymbols))
					if (SH.is(s))
						this.replaySymbolsSet.add(SH.trim(s));
			}
		} else {
			this.replaySpeed = 1D;
			this.replaySymbols = null;
			this.replaySymbolsSet = null;
			this.replaySkipTime = this.replayStartTime = this.replayEndTime = null;
		}
		this.startTime = startTime;
		final String referenceFiles[] = SH.split(",", properties.getOptional(OPTION_ANVIL_REFERENCE_FILES, ""));
		final String files[] = SH.split(",", properties.getOptional(OPTION_ANVIL_FILES, ""));
		this.referenceFilesSet = CH.s(referenceFiles);
		final Set<String> dup = CH.comm(referenceFilesSet, CH.s(files), false, false, true);
		if (!dup.isEmpty())
			throw new RuntimeException("File exists in both " + OPTION_ANVIL_FILES + " and " + OPTION_ANVIL_REFERENCE_FILES + ": " + dup);

		for (String fileName : referenceFiles) {
			File file = new File(fileName);
			IOH.assertFileExists(file, "reference file defined in " + OPTION_ANVIL_REFERENCE_FILES);
			String fullPath = IOH.getFullPath(file);
			AnvilFileLoader loader = new AnvilFileLoader(this, fullPath);
			refFileLoaders.add(loader);
			tailingFiles.put(fullPath, loader);
		}

		for (String file : files)
			if (SH.isnt(file))
				continue;
			else if (file.indexOf('*') != -1)
				addToDirectoryWatchList(file);
			else
				addToFileWatchList(file);
		this.hasSymbolFilterList = SH.isnt(getReplaySymbols());
		new Thread(this, "FileWatcher").start();
	}
	private TimeOfDay parseTime(PropertyController p, String option) {
		String time = p.getRequired(option);
		try {
			return getTimeOfDay(time);
		} catch (Exception e) {
			throw new RuntimeException("Property " + option + " must be in format hh:mm:ss ZZZ", e);
		}
	}
	private String formatTime(long time) {
		return this.timeFormatter.format(new Date(time));
	}

	private void addToFileWatchList(String file) {
		this.watchFiles.add(file);
	}

	private void addToDirectoryWatchList(String file) {
		final String fileName, mask;
		if (file.indexOf('*') == -1) {
			fileName = file;
			mask = ".*";
		} else {
			file = SH.replaceAll(file, '\\', '/');
			int start = file.lastIndexOf('/');
			if (start == -1) {
				fileName = ".";
				mask = file;
			} else {
				fileName = file.substring(0, start);
				mask = file.substring(start + 1);
			}
		}
		if (fileName.indexOf('*') != -1)
			LH.warning(log, "Not a valid wildcard, star(*) must not be in directory, only file name: ", file);
		else {
			LH.info(log, "Directory found, will watch directory: ", fileName, "  (mask=", mask, ")");
			this.watchDirs.add(new Tuple2<String, String>(fileName, mask));
		}
	}
	private void getRemainingRefFileLoaders(List<AnvilFileLoader> sink) {
		sink.clear();
		for (AnvilFileLoader i : this.refFileLoaders)
			if (!i.hasReachedEof())
				sink.add(i);
	}

	@Override
	public void run() {
		List<AnvilFileLoader> sink = new ArrayList<AnvilFileLoader>();
		LH.info(log, "Processing ", this.refFileLoaders.size(), " reference File(s)...");
		for (;;) {
			getRemainingRefFileLoaders(sink);
			if (sink.isEmpty())
				break;
			LH.info(log, "Still waiting for ", sink.size(), " Reference file(s) to complete loading...");
			OH.sleep(2000);
		}
		LH.info(log, "Processed Ref Files.");
		int cnt = 0;
		for (;;) {
			for (String fileName : this.watchFiles) {
				try {
					File file = new File(fileName);
					String fullPath = IOH.getFullPath(file);
					if (!file.exists()) {
						if (cnt % 10 == 0)
							LH.info(log, "Waiting for file: " + fullPath);
					} else if (tailingFiles.containsKey(fullPath)) {
						stopWatchingFor.add(fileName);
					} else if (!file.canRead()) {
						LH.warning(log, "Permission denied for file: " + fullPath);
					} else if (file.isDirectory()) {
						addToDirectoryWatchList(fileName);
						stopWatchingFor.add(fileName);
					} else if (file.isFile()) {
						stopWatchingFor.add(fileName);
						tailingFiles.put(fullPath, newAnvilFileLoader(fullPath));
					}
				} catch (Exception e) {
					LH.severe(log, "Unknown error processing file ", fileName, e);
				}
			}

			for (Tuple2<String, String> dirName : this.watchDirs) {
				try {
					File dir = new File(dirName.getA());
					String fullDirPath = IOH.getFullPath(dir);
					if (!dir.exists()) {
						if (cnt % 10 == 0)
							LH.info(log, "Waiting for Directory: " + fullDirPath);
					} else {
						TextMatcher matcher = SH.m(dirName.getB());
						for (File file : dir.listFiles()) {
							if (file.isDirectory())
								continue;
							if (!matcher.matches(file.getName())) {
								continue;
							}
							String fullPath = IOH.getFullPath(file);
							if (tailingFiles.containsKey(fullPath)) {
								continue;
							} else if (!file.canRead()) {
								LH.warning(log, "Permission denied for file: " + fullPath);
							} else {
								tailingFiles.put(fullPath, newAnvilFileLoader(fullPath));
							}
						}
					}
				} catch (Exception e) {
					LH.severe(log, "Unknown error processing dir ", dirName, e);
				}
			}
			if (!stopWatchingFor.isEmpty()) {
				this.watchFiles.removeAll(stopWatchingFor);
				stopWatchingFor.clear();
			}
			OH.sleep(fileWatchPeriod);
			cnt++;
		}
	}
	private AnvilLoader newAnvilFileLoader(String fullPath) {
		return new AnvilFileLoader(this, fullPath);
	}
	public long getStartTime() {
		return startTime;
	}

	public long getLogRate() {
		return logRate;
	}

	public int getLimit() {
		return limit;
	}

	public int getMaxQueueSize() {
		return maxQueueSize;
	}

	public boolean getReplayEnabled() {
		return replayEnabled;
	}

	public long getFlushPeriod() {
		return flushPeriod;
	}

	public long getHistorySnapshotPeriod() {
		return historySnapshotPeriod;
	}

	public int getPort() {
		return port;
	}

	public double getReplaySpeed() {
		return replaySpeed;
	}

	public TimeOfDay getReplaySkipTime() {
		return replaySkipTime;
	}

	public TimeOfDay getReplayEndTime() {
		return replayEndTime;
	}

	public TimeOfDay getReplayStartTime() {
		return replayStartTime;
	}

	public boolean setNbboFileName(String filename) {
		return nbbosFileName.compareAndSet(null, filename);
	}
	public boolean setTradesFileName(String filename) {
		return tradesFileName.compareAndSet(null, filename);
	}

	public String getNbboFileName() {
		return this.nbbosFileName.get();
	}
	public String getTradesFileName() {
		return this.tradesFileName.get();
	}

	public PropertyController getProperties() {
		return properties;
	}

	public AnvilMarketData getMarketData() {
		return this.marketDataInstance;
	}

	public boolean shouldProcessSymbol(CharSequence symbol) {
		return this.replaySymbolsSet == null || this.replaySymbolsSet.contains(symbol);
	}

	public String getReplaySymbols() {
		return this.replaySymbols;
	}
	public long getAnvilMainQueueSize() {
		return this.actionRunner.getQueueSize();
	}
	public boolean hasSymbolFilterList() {
		return this.hasSymbolFilterList;
	}

	private ConcurrentMap<String, TimeOfDay> timeofDays = new CopyOnWriteHashMap<String, TimeOfDay>();

	public TimeOfDay getTimeOfDay(String timeAndTz) {
		TimeOfDay r = this.timeofDays.get(timeAndTz);
		if (r == null)
			this.timeofDays.put(timeAndTz, r = new TimeOfDay(timeAndTz));
		return r;
	}
}
