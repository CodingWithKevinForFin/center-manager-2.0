/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger.impl.test;

import java.io.File;
import java.util.Properties;

import com.f1.speedlogger.SpeedLogger;
import com.f1.speedlogger.SpeedLoggerLevels;
import com.f1.speedlogger.SpeedLoggerManager;
import com.f1.speedlogger.impl.AutoFlushWriterRing;
import com.f1.speedlogger.impl.BasicSpeedLoggerConfigParser;
import com.f1.speedlogger.impl.SpeedLoggerInstance;
import com.f1.utils.ArgParser;
import com.f1.utils.ArgParser.Arguments;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.SH;

public class TestSpeedLogger {

	public static void main(String a[]) {
		System.out.println(SH.join("|", a));
		ArgParser ap = new ArgParser("TestSpeedLogger");

		System.out.println(EH.getPid());
		try {
			SpeedLoggerManager manager = SpeedLoggerInstance.getInstance();
			ap.addSwitchOptional("f", "file", "*", "target file");
			ap.addSwitchOptional("c", "testCount", "*", "number of times to rerun the test");
			ap.addSwitchOptional("t", "threadCount", "*", "threads count");
			ap.addSwitchOptional("m", "messageCount", "*", "messages per thread");
			ap.addSwitchOptional("s", "messageSize", "*", "messages size");
			ap.addSwitchOptional("p", "pattern", "*", "pattern for speed logger formatter");
			ap.addSwitchOptional("b", "bufferCount", "*", "number of buffers in ring");
			ap.addSwitchOptional("r", "ringAggTimeout", "*", "ring aggressive timeout");

			Arguments aa = ap.parse(a);

			final String target = aa.getRequired("f");// "/tmp/logger.test";
			final int threadsCount = aa.getOptional("t", 10);
			final long runCount = aa.getOptional("m", 100000);
			final long msgSize = aa.getOptional("s", 500);
			final int testCount = aa.getOptional("c", 4);
			final int bufferCount = aa.getOptional("b", 1);
			final long ringTimeout = aa.getOptional("r", AutoFlushWriterRing.DEFAULT_AGGRESIVE_TIMEOUT_NANOS);
			String pattern = aa.getOptional("p", "%P %t %m %D%n");
			Properties text = new Properties();
			text.put("speedlogger.appender.BASIC_APPENDER.type", "BasicAppender");
			text.put("speedlogger.appender.BASIC_APPENDER.timezone", "EST5EDT");
			text.put("speedlogger.appender.BASIC_APPENDER.pattern", pattern);
			for (int tc = 0; tc < testCount; tc++) {
				File file = new File(target + tc);
				IOH.delete(file);
				text.put("speedlogger.sink.FILE_SINK" + tc + ".type", "file");
				text.put("speedlogger.sink.FILE_SINK" + tc + ".maxFileSizeMb", "10000");
				text.put("speedlogger.sink.FILE_SINK" + tc + ".bufferCount", "" + bufferCount);
				text.put("speedlogger.sink.FILE_SINK" + tc + ".fileName", target + tc);
				text.put("speedlogger.sink.FILE_SINK" + tc + ".ringAggressiveTimeoutNanos", ringTimeout);
				text.put("speedlogger.stream.test" + tc + "_", "BASIC_APPENDER;FILE_SINK" + tc + ";INF");
			}
			System.out.println("config:\n" + text);
			new BasicSpeedLoggerConfigParser(manager).process(text);
			StringBuilder s = new StringBuilder();
			for (long i = 0; i < msgSize; i++) {
				s.append('a');
			}
			final String t = s.toString();
			System.out.println("Writing to " + target + " using " + threadsCount + " thread(s), running " + testCount + " time(s)");
			for (int tc = 0; tc < testCount; tc++) {
				File file = new File(target + tc);
				final SpeedLogger logger = manager.getLogger("test" + tc + "_");
				final Thread[] threads = new Thread[threadsCount];
				for (int j = 0; j < threadsCount; j++) {
					threads[j] = new Thread() {
						public void run() {
							long start = System.currentTimeMillis();
							for (long i = 0; i < runCount; i++) {
								logger.log(SpeedLoggerLevels.SEVERE, t);
							}
							long end = System.currentTimeMillis();
						}
					};
				}
				long start = System.nanoTime();
				for (Thread thread : threads) {
					thread.setName("THREAD");
					thread.start();
				}
				for (Thread thread : threads)
					thread.join();
				long end = System.nanoTime();
				long length = file.length();
				long dur = end - start;
				System.out.println("test #" + (tc + 1) + ": Wrote " + length + " bytes in " + dur + " nanos, about " + SH.formatMemory(1000 * 1000 * 1000 * length / dur)
						+ " per second");
			}
		} catch (Exception e) {
			System.out.println(ap.toLegibleString());
			System.out.println(SH.printStackTrace(e));
		}
	}

}
