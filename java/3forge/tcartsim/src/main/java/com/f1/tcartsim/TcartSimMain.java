package com.f1.tcartsim;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.f1.bootstrap.Bootstrap;
import com.f1.utils.FastBufferedInputStream;
import com.f1.utils.FastBufferedOutputStream;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.TimeOfDay;

public class TcartSimMain implements Runnable {
	private static final Logger log = LH.get(TcartSimMain.class);
	private static final String SYM_2 = "";
	private static final String SYM_1 = "";
	//private static final int START_TIME = 34185955 - 4 * 60000;
	public static final byte TYPE_ORDER = 0;
	public static final byte TYPE_EX = 1;
	public static final byte TYPE_TRADE = 2;
	public static final byte TYPE_NBBO = 3;

	private static final int MAX_DELAY = 1;
	private static Bootstrap bs;

	public static void main(String[] a) throws IOException {
		bs = new Bootstrap(TcartSimMain.class, a);
		bs.setConfigDirProperty("./src/main/config");
		bs.setConsolePortProperty(-1);
		bs.setTerminateFileProperty("${f1.conf.dir}/../.tcartsim.prc");
		PropertyController p = bs.getProperties();
		double speedUp = bs.getProperties().getOptional("tcart.sim.speed", 1d);
		TimeOfDay skipUntilTime = parseDurationTo(bs.getProperties().getOptional("tcart.sim.skip.until", "09:27:00.000 EST5EDT"));
		TimeOfDay endTime = parseDurationTo(bs.getProperties().getOptional("tcart.sim.end.time", "16:00:00.000 EST5EDT"));
		TimeOfDay burstTime = parseDurationTo(bs.getProperties().getOptional("tcart.sim.burst.time", "9:30:00.000 EST5EDT"));
		bs.startup();
		IOH.copy(p.getRequired("tcart.sim.securities.in", File.class), p.getRequired("tcart.sim.securities.out", File.class), 10000, false);
		LH.info(log, "Streaming raw files...");
		List<TcartSimMain> players = new ArrayList<TcartSimMain>();
		players.add(process(TYPE_ORDER));
		//players.add(process(TYPE_EX));
		players.add(process(TYPE_TRADE));
		players.add(process(TYPE_NBBO));
		long startTime = System.currentTimeMillis();
		for (TcartSimMain i : players) {
			i.init(startTime, speedUp, skipUntilTime, endTime, burstTime);
			Thread t = new Thread(i);
			t.setDaemon(false);
			t.start();
		}
	}

	private static TimeOfDay parseDurationTo(String timeAndTz) {
		return new TimeOfDay(timeAndTz);
	}
	private long startTime;
	private TimeOfDay skipUntilTime;
	private TimeOfDay endTime;
	private TimeOfDay burstTime;

	private void init(long startTime, double speedUp, TimeOfDay skipUntilTime, TimeOfDay endTime, TimeOfDay burstTime) {
		this.startTime = -1;
		this.skipUntilTime = skipUntilTime;
		this.speedUp = speedUp;
		this.endTime = endTime;
		this.burstTime = burstTime;
	}
	public static TcartSimMain process(byte type) throws IOException {
		final TcartSimMain player = new TcartSimMain(type);
		return player;
	}

	private double speedUp;

	@Override
	public void run() {
		LH.info(log, "Processing File: ", IOH.getFullPath(this.inFile), " ==> ", IOH.getFullPath(this.outFile));
		try {
			StringBuilder line = new StringBuilder();
			//int previousCnt = 0;
			long measureTime = System.currentTimeMillis();
			//double offset = Double.NaN;
			//double totDelay = 0;
			long delayDelta = -1;//Math.max(burstTime.getTimeForToday(0), skipUntilTime.getTimeForToday(0));
			//long delayDelta = Math.max(burstTime, skipUntilTime);
			for (int cnt = 0;;) {
				readLine(reader, SH.clear(line));
				long timeFromFile = parseTimeFromLine(line.toString());
				if (type == TYPE_NBBO || type == TYPE_TRADE) {
					if (skipUntilTime.isLt(timeFromFile))
						//if (timeFromFile < skipUntilTime)
						continue;
				}
				if (endTime.isGt(timeFromFile)) { //if (timeFromFile > endTime) {
					writer.flush();
					break;
				}

				long ctm = System.currentTimeMillis();

				if (burstTime.isGe(timeFromFile)) { //if (timeFromFile >= burstTime) {
					if (delayDelta == -1)
						delayDelta = Math.max(burstTime.getTimeForToday(timeFromFile), skipUntilTime.getTimeForToday(timeFromFile));
					if (startTime == -1)
						startTime = System.currentTimeMillis();
					double delay = (timeFromFile - delayDelta) / speedUp - (ctm - startTime);
					if (delay >= 1d) {
						if (delay > 10000)
							LH.info(log, "Delaying File: ", IOH.getFullPath(this.inFile), " for ", delay, " ms");
						//totDelay += delay;
						OH.sleep((long) delay);
					}
				}

				for (int i = 0, l = line.length(); i < l; i++)
					writer.write(line.charAt(i));
				writer.write('\n');
				linesWritten++;
				cnt++;
				if (ctm > measureTime) {
					//					long pastTime = (long) ((System.currentTimeMillis() - startTime) * speedUp);
					//double timeWeWantInFile = pastTime + skipUntilTime;
					//					LH.info(log, inFile.getName() + " => Delay: " + (timeWeWantInFile - timeFromFile) + " ms, Processed: " + (cnt - previousCnt) + ", Total: " + cnt + "  delay="
					//							+ (long) totDelay);

					//previousCnt = cnt;
					measureTime += 5000;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOH.close(reader);
		}
	}
	private byte type;
	private File inFile;
	private File outFile;
	private FastBufferedInputStream reader;
	private FastBufferedOutputStream writer;
	private int linesWritten;

	public TcartSimMain(byte type) throws IOException {
		this.type = type;
		initInAndOutFiles();

		this.linesWritten = 0;

	}
	private void initInAndOutFiles() throws FileNotFoundException {
		PropertyController p = bs.getProperties();
		for (;;) {
			switch (type) {
				case TYPE_ORDER:
					this.inFile = p.getOptional("tcart.sim.orders.in", new File("F:/temp/realTimeTca/ORDERS_RAW.txt"));
					this.outFile = p.getOptional("tcart.sim.orders.out", new File("F:/temp/realTimeTca/ORDERS.txt"));
					break;
				case TYPE_EX:
					inFile = p.getOptional("tcart.sim.executions.in", new File("F:/temp/realTimeTca/EXECUTIONS_RAW.txt"));
					outFile = p.getOptional("tcart.sim.executions.out", new File("F:/temp/realTimeTca/EXECUTIONS.txt"));
					break;
				case TYPE_TRADE:
					inFile = p.getOptional("tcart.sim.trades.in", new File("F:/temp/realTimeTca/TRADES_RAW.txt"));
					outFile = p.getOptional("tcart.sim.trades.out", new File("F:/temp/realTimeTca/TRADES.txt"));
					break;
				case TYPE_NBBO:
					inFile = p.getOptional("tcart.sim.nbbo.in", new File("F:/temp/realTimeTca/NBBO_RAW.txt"));
					outFile = p.getOptional("tcart.sim.nbbo.out", new File("F:/temp/realTimeTca/NBBO.txt"));
					break;
			}
			if (outFile.exists()) {
				LH.info(log, "Waiting for file to be deleted: " + IOH.getFullPath(outFile));
				OH.sleep(1000);
			} else
				break;
		}
		this.reader = new FastBufferedInputStream(new FileInputStream(inFile), 10000);
		this.writer = new FastBufferedOutputStream(new FileOutputStream(outFile), 10000);
	}
	private static boolean readLine(FastBufferedInputStream reader, StringBuilder line) throws IOException {
		for (;;) {
			char t = (char) reader.readByte();
			if (t == '\n')
				break;
			line.append(t);
		}
		return true;
	}
	private static long parseTimeFromLine(String line) {
		int i = line.indexOf('|');
		return SH.parseLong(line, 0, i, 10);
	}

}
