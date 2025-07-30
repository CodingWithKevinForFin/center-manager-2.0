package com.vortex.agent.filetail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.FastByteArrayOutputStream;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SearchPath;
import com.f1.utils.structs.Tuple3;

public class FileTailManager {

	private static final Logger log = Logger.getLogger(FileTailManager.class.getName());

	public static void main(String a[]) throws IOException {

		// FileInputStream r = new FileInputStream(new File("/home/rcooke/tmp/1"));
		// while (true) {
		// if (r.available() == 0)
		// OH.sleep(100);
		// else
		// System.out.print((char) r.read());
		// }

		SearchPath sp = new SearchPath(new File("/home/rcooke/java/vortex/testtrack/log"));
		String search = "*.log";
		int options = SearchPath.OPTION_CONTINUE_AFTER_FIRST_FIND | SearchPath.OPTION_IS_PATTERN;
		FileTailManagerListener l = new FileTailManagerListener() {

			@Override
			public void onReset(FileTail file, File existing) {
				if (existing != null)
					System.out.println("  onReset:" + file.getFileName() + " <-- " + existing);
				else
					System.out.println("  onReset:" + file.getFileName());
			}

			@Override
			public void onRemoved(FileTail file) {
				System.out.println("onRemoved:" + file.getFileName());

			}

			@Override
			public void onOpened(FileTail file, File existing) {
				if (existing != null)
					System.out.println(" onOpened:" + file.getFileName() + " <-- " + existing);
				else
					System.out.println(" onOpened:" + file.getFileName());

			}

			@Override
			public void onData(FileTail file, long position, byte[] data, int start, int length, boolean posDup) {
				if (posDup)
					System.out.println("onDataDup:" + file.getFileName() + ": " + length + " byte(s) at position " + position);// + new String(data, start, length));
				else
					System.out.println("   onData:" + file.getFileName() + ": " + length + " byte(s) at position " + position);// + new String(data, start, length));

			}
		};
		FileTailManager ftm = new FileTailManager(1024000, 10240, 1000);
		List<Tuple3<SearchPath, String, Integer>> searchList = new ArrayList<Tuple3<SearchPath, String, Integer>>();
		Tuple3<SearchPath, String, Integer> srch = new Tuple3<SearchPath, String, Integer>(sp, search, options);
		searchList.add(srch);
		ftm.setSearches(searchList);
		for (;;) {
			if (!ftm.pump(l))
				OH.sleep(100);
		}

	}

	private int checkSumSize;
	private int readback;
	private long checkFileSystemChangesFrequencyMs;
	private List<Tuple3<SearchPath, String, Integer>> searches = new ArrayList<Tuple3<SearchPath, String, Integer>>();

	public FileTailManager(int readback, int checkSumSize, long checkFileSystemChangesFrequencyMs) {
		this.readback = readback;
		this.checkSumSize = checkSumSize;
		this.checkFileSystemChangesFrequencyMs = checkFileSystemChangesFrequencyMs;
	}

	public void setSearches(List<Tuple3<SearchPath, String, Integer>> searchList) {
		this.searches = searchList;
	}

	Map<String, FileTail> fileTails = new HashMap<String, FileTail>();
	Map<Long, File> checksumToFiles = new HashMap<Long, File>();

	public boolean pump(FileTailManagerListener listener) throws IOException {
		boolean changes = false;
		FastByteArrayOutputStream out = new FastByteArrayOutputStream();
		long lastCheck = 0;
		long now = EH.currentTimeMillis();
		if (now - lastCheck > checkFileSystemChangesFrequencyMs) {
			lastCheck = now;
			Map<String, File> found = new HashMap<String, File>();
			for (Tuple3<SearchPath, String, Integer> search : searches) {
				List<File> files = search.getA().search(search.getB(), search.getC());
				for (File file : files)
					found.put(file.getPath(), file);
			}
			for (String add : CH.comm(fileTails.keySet(), found.keySet(), false, true, false)) {
				FileTail ft;
				fileTails.put(add, ft = new FileTail(found.get(add), readback, checkSumSize));
			}
			for (String remove : CH.comm(fileTails.keySet(), found.keySet(), true, false, false)) {
				FileTail removed = fileTails.remove(remove);
				if (removed.isOpen()) {
					IOH.close(removed);
					listener.onRemoved(removed);
					changes = true;
				}
			}
		}
		for (FileTail i : fileTails.values()) {
			if (i.hasChecksum())
				checksumToFiles.put(i.getChecksum(), i.getFile());
			int state = i.getState();
			switch (state) {
				case 0:
					continue;
				case FileTail.STATE_OPEN | FileTail.STATE_EXISTS:
					out.reset();
					long position = i.getPosition();
					long read = i.readAvailable(out);
					if (read == 0)
						continue;
					listener.onData(i, position, out.getBuffer(), 0, out.getCount(), false);
					break;
				case FileTail.STATE_OPEN | FileTail.STATE_EXISTS | FileTail.STATE_POSDUP:
					out.reset();
					position = i.getPosition();
					read = i.readAvailable(out);
					if (read == 0)
						continue;
					listener.onData(i, position, out.getBuffer(), 0, out.getCount(), true);
					break;
				case FileTail.STATE_OPEN:
					IOH.close(i);
					listener.onRemoved(i);
					break;
				case FileTail.STATE_OPEN | FileTail.STATE_RESET | FileTail.STATE_EXISTS | FileTail.STATE_POSDUP:
				case FileTail.STATE_OPEN | FileTail.STATE_RESET | FileTail.STATE_EXISTS:
					IOH.close(i);
					i.open();
					if (i.hasChecksum()) {
						File existing = checksumToFiles.get(i.getChecksum());
						if (existing != null) {
							i.moveToHead();
							listener.onReset(i, existing);
						}
					} else
						listener.onReset(i, null);
					changes = true;
					break;
				case FileTail.STATE_EXISTS:
				case FileTail.STATE_EXISTS | FileTail.STATE_POSDUP:
					i.open();
					if (i.hasChecksum()) {
						File existing = checksumToFiles.get(i.getChecksum());
						if (existing != null) {
							i.moveToHead();
							listener.onOpened(i, existing);
						}
					} else
						listener.onOpened(i, null);
					break;
				default:
					LH.warning(log, i.getFileName(), " unkown state: ", state);
					continue;
			}
			changes = true;
		}
		return changes;
	}

}
