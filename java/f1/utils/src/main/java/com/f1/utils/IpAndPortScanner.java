package com.f1.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import com.f1.base.ToStringable;
import com.f1.utils.concurrent.FastQueue;
import com.f1.utils.concurrent.NamedThreadFactory;

public class IpAndPortScanner implements Runnable {
	private static final Logger log = LH.get();

	public static void main(String a[]) throws IOException, InterruptedException {
		Executor ex = new ScheduledThreadPoolExecutor(100, new NamedThreadFactory("", true));
		IpAndPortScanner scanner = new IpAndPortScanner("97.74.205.186", "97.74.205.186", AH.ints(21, 22, 23), 100, 150, ex);
		scanner.run();
		List<Result> results = scanner.getResults();
		for (Result result : results)
			System.out.println(result);
	}

	final private FastQueue<Result> results = new FastQueue<Result>();
	final private AtomicInteger current;
	final private int[] ips;
	final private int timeoutMs;
	final private int threadCount;
	final private int[] ports;
	final private CountDownLatch latch;
	private Runner runner;
	final private Executor ex;
	private List<Result> resultsList;

	public IpAndPortScanner(String startIp, String endIp, int portsToScan[], int threadCount, int timeoutMs, Executor ex) {
		this(IOH.ip4ToInt(IOH.parseIp4(startIp)), IOH.ip4ToInt(IOH.parseIp4(endIp)), portsToScan, threadCount, timeoutMs, ex);
	}

	public List<Result> getResults() {
		return this.resultsList;
	}

	private class Runner implements Runnable {

		@Override
		public void run() {
			IntArrayList buf = new IntArrayList();
			try {
				for (;;) {
					int index = current.getAndIncrement();
					if (index >= ips.length)
						break;
					int ip = ips[index];
					try {
						testAddress(ip, buf);
					} catch (IOException e) {
					}
				}
			} finally {
				latch.countDown();
			}
		}

	}

	public IpAndPortScanner(int ips[], int ports[], int threadCount, int timeoutMs, Executor ex) {
		this.ex = ex;
		OH.assertBetween(threadCount, 1, 1000);
		OH.assertBetween(timeoutMs, 10, 10000);
		OH.assertBetween(ips.length, 1, 100000);
		this.runner = new Runner();
		this.ports = ports;
		this.threadCount = Math.min(threadCount, ips.length);
		this.timeoutMs = timeoutMs;
		this.current = new AtomicInteger(0);
		this.ips = ips;
		this.latch = new CountDownLatch(this.threadCount);
	}
	public IpAndPortScanner(int start, int end, int ports[], int threadCount, int timeoutMs, Executor ex) {
		this.ex = ex;
		OH.assertBetween(threadCount, 1, 1000);
		OH.assertBetween(timeoutMs, 10, 10000);
		OH.assertBetween(end - start + 1, 1, 100000);
		this.runner = new Runner();
		this.ports = ports;
		this.threadCount = Math.min(threadCount, end - start + 1);
		this.timeoutMs = timeoutMs;
		this.current = new AtomicInteger(0);
		this.ips = new int[end - start + 1];
		for (int i = start; i <= end; i++)
			this.ips[i - start] = i;
		this.latch = new CountDownLatch(this.threadCount);
	}
	@Override
	public void run() {
		try {
			for (int i = 0; i < threadCount - 1; i++)
				ex.execute(runner);
			runner.run();
			latch.await();
			List<Result> r = new ArrayList<Result>();
			for (Result result = results.get(); result != null; result = results.get())
				r.add(result);
			Collections.sort(r);
			this.resultsList = r;
		} catch (Exception e) {
			LH.warning(log, "error running port scan", e);
		}
	}

	public static class Result implements ToStringable, Comparable<Result> {
		private final int ip;
		private final String hostname;
		private final boolean isReachable;
		private final int portsFound[];

		public Result(int ip, String hostname, boolean isReachable, int[] portsFound) {
			this.ip = ip;
			this.hostname = hostname;
			this.isReachable = isReachable;
			this.portsFound = portsFound;
		}
		public int getIp() {
			return ip;
		}
		public String getHostname() {
			return hostname;
		}
		public boolean isReachable() {
			return isReachable;
		}
		public int[] getPortsFound() {
			return portsFound;
		}

		@Override
		public String toString() {
			return toString(new StringBuilder()).toString();
		}
		@Override
		public StringBuilder toString(StringBuilder sink) {
			IOH.formatIp(IOH.intToIp4(ip), sink);
			sink.append(" ==> ");
			if (isReachable)
				sink.append(" pingable ");

			if (hostname != null)
				sink.append(" (").append(hostname).append(") ");
			sink.append('[');
			SH.join(',', portsFound, sink);
			sink.append(']');
			return sink;
		}

		@Override
		public int compareTo(Result o) {
			return OH.compare(ip, o.ip);
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof Result && compareTo((Result) obj) == 0;
		}

	}

	private void testAddress(int ip, IntArrayList buf) throws IOException {
		InetAddress address = InetAddress.getByAddress(IOH.intToIp4(ip));
		buf.clear();
		boolean isReachable = address.isReachable(timeoutMs);
		if (AH.isntEmpty(this.ports))
			for (int port : this.ports)
				if (testPort(address, port))
					buf.add(port);
		int[] ports = buf.toIntArray();
		boolean isDns = !address.getHostAddress().equals(address.getHostName());
		String hostName = isDns ? address.getHostName() : null;
		if (isDns || isReachable || ports.length > 0) {
			results.put(new Result(ip, hostName, isReachable, ports));
		}

	}
	private boolean testPort(InetAddress address, int port) {
		final Socket s = new Socket();
		try {
			s.connect(new InetSocketAddress(address, port), timeoutMs);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			IOH.close(s);
		}
	}
}
