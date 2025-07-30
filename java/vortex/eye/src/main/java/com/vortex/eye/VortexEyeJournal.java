package com.vortex.eye;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.f1.base.F1LicenseInfo;
import com.f1.base.IdeableGenerator;
import com.f1.base.ObjectGeneratorForClass;
import com.f1.utils.CharReader;
import com.f1.utils.EH;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.FastOutputStreamWriter;
import com.f1.utils.IOH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.agg.LongAggregator;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.structs.IntKeyMap;
import com.f1.vortexcommon.msg.eye.VortexEyeJournalReport;

public class VortexEyeJournal implements Runnable {

	private static final String CHECKSUM_DELIM = "|";
	private static final int[] CHAR_NEWLINE_OR_EOF = new int[] { CharReader.EOF, SH.CHAR_NEWLINE };
	private long checksum;
	private int checksumPoint;
	private FastByteArrayDataOutputStream buffer;
	private FastOutputStreamWriter bufferWriter;
	private FileOutputStream fileWriter;
	private long nextPing = 0;
	private long PING_FREQUENCY = TimeUnit.SECONDS.toMillis(10);
	private IntKeyMap<VortexEyeJournalReport> reports = new IntKeyMap<VortexEyeJournalReport>();
	private IdeableGenerator generator;
	private ObjectGeneratorForClass<VortexEyeJournalReport> reportGenerator;
	private VortexEyeJournalReport latestReport;

	public Iterable<VortexEyeJournalReport> getReports() {
		return reports.values();
	}

	public static void main(String a[]) throws IOException {
		IdeableGenerator gen = null;
		VortexEyeJournal vej = new VortexEyeJournal(new File("/tmp/out2.txt"), gen);
		Random r = new Random();
		for (int i = 0; i < 10; i++) {
			for (int year = 2013; year < 2018; year++) {
				System.out.println("year: " + year);
				for (int month = 1; month <= 12; month++) {
					System.out.println("month: " + month);
					for (int minutes = 0; minutes < 31 * 24 * 60; minutes += r.nextInt(1000)) {
						for (int magents = 0; magents < 8192; magents += r.nextInt(1000)) {
							for (int mavg = 0; mavg <= magents; mavg += r.nextInt(1000)) {
								for (int maccounts = 0; maccounts < 1024; maccounts += r.nextInt(1000)) {

									VortexEyeJournalReport report1 = vej.reportGenerator.nw();
									report1.setYear(year);
									report1.setMonth(month);
									report1.setMinutesUp(minutes);
									report1.setMaxAgents(magents);
									report1.setAvgAgents(mavg);
									report1.setMaxAccounts(maccounts);
									//= new VortexEyeJournalReport(year, month, minutes, magents, mavg, maccounts);
									String val = null;
									try {
										vej.populateText(report1);
										VortexEyeJournalReport report2 = vej.parseReport(val);
										OH.assertEq(report1.getMaxAccounts(), report2.getMaxAccounts());
										OH.assertEq(report1.getMaxAgents(), report2.getMaxAgents());
										OH.assertEq(report1.getMonth(), report2.getMonth());
										OH.assertEq(report1.getYear(), report2.getYear());
										OH.assertEq(report1.getMinutesUp() / 60, report2.getMinutesUp() / 60);
										//System.out.println(val);
										//System.out.println(report1);
										//System.out.println(report2);
										//System.out.println();
									} catch (RuntimeException e) {
										System.err.println(report1);
										System.err.println(val);
										throw e;
									}
								}
							}
						}
					}
				}
			}
		}
		System.exit(0);
		LineNumberReader in = new LineNumberReader(new InputStreamReader(System.in));
		for (;;) {
			String line = in.readLine();
			if (line == null)
				return;
			vej.writeLine(line);
		}
	}
	public void validateReport(VortexEyeJournalReport report) {
		if (!OH.isBetween(report.getMaxAgents(), 0, 8191))
			throw new RuntimeException("invalid max Agents: " + report.getMaxAgents());
		if (!OH.isBetween(report.getAvgAgents(), 0, report.getMaxAgents()))
			throw new RuntimeException("invalid avg agents: " + report.getAvgAgents() + ", maxagents: " + report.getMaxAgents());
		if (!OH.isBetween(report.getMaxAccounts(), 0, 1023))
			throw new RuntimeException("invalid max Accounts: " + report.getMaxAccounts());
		if (!OH.isBetween(report.getMinutesUp(), 0, 24 * 31 * 60))
			throw new RuntimeException("invalid hours: " + report.getMinutesUp());
		if (!OH.isBetween(report.getMonth(), 1, 12))
			throw new RuntimeException("invalid month: " + report.getMonth());
		if (!OH.isBetween(report.getYear(), 2013, 2999))
			throw new RuntimeException("invalid year: " + report.getYear());
	}
	public VortexEyeJournal(File file, IdeableGenerator generator) throws IOException {
		this.generator = generator;
		this.reportGenerator = this.generator.getGeneratorForClass(VortexEyeJournalReport.class);
		this.buffer = new FastByteArrayDataOutputStream();
		this.bufferWriter = new FastOutputStreamWriter(this.buffer);
		if (file.exists())
			processFile(file);
		updateChecksum();
		fileWriter = new FileOutputStream(file, true);
		Runtime.getRuntime().addShutdownHook(new Thread(this));
	}

	private void processFile(File file) throws IOException {
		StringCharReader scr = new StringCharReader(IOH.readText(file));
		StringBuilder buf = new StringBuilder();
		long previousTime = 0;
		while (scr.peakOrEof() != CharReader.EOF) {
			scr.readUntilAny(CHAR_NEWLINE_OR_EOF, SH.clear(buf));
			String line = buf.toString();
			if (scr.peakOrEof() == CharReader.EOF) {
				System.err.print("Reparing file, trailing chars found: " + line);
				IOH.writeData(file, buffer.toByteArray());
				break;
			} else {
				scr.expect(SH.CHAR_NEWLINE);
				String text = SH.beforeLast(line, CHECKSUM_DELIM);
				processExistingLine(text, previousTime);
				writeLine(text, false);
				long checksum = SH.parseLong(SH.afterLast(line, CHECKSUM_DELIM), 62);
				if (this.checksum != checksum)
					throw new RuntimeException("invalid");
			}
		}
	}

	private static class Ping {
		final long time;
		final int agentsCount;
		final int users;
		public Ping(long time, int agentsCount, int users) {
			super();
			this.time = time;
			this.agentsCount = agentsCount;
			this.users = users;
		}
		public long getTime() {
			return time;
		}
		public int getAgentsCount() {
			return agentsCount;
		}
		public int getUsersCount() {
			return users;
		}

	}

	private void processExistingLine(String text, long previousTime) {
		String[] parts = SH.split('|', text);
		long time = Long.parseLong(parts[0]);
		if (time < previousTime)
			throw new RuntimeException("out of sequence: " + time);
		String type = parts[1];
		if ("RP".equals(type)) {
			VortexEyeJournalReport report = parseReport(parts[2]);
			reports.put(report.getYear() * 100 + report.getMonth(), report);
			pings.clear();
		} else if ("PN".equals(type)) {
			pings.add(new Ping(time, Integer.parseInt(parts[2]), Integer.parseInt(parts[3])));
		}
	}
	private void updateChecksum() {
		checksum = IOH.checkSumBsdLong(checksum, buffer.getBuffer(), checksumPoint, buffer.size());
		checksumPoint = buffer.size();
	}

	public boolean needsPing() {
		return EH.currentTimeMillis() >= nextPing;

	}
	private void writeLine(long now, String code, String params) {
		nextPing = MH.roundBy(now + PING_FREQUENCY, (int) PING_FREQUENCY, MH.ROUND_DOWN);
		try {
			writeLine(now + "|" + code + "|" + params);
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
	}
	private void writeLine(String text) throws IOException {
		writeLine(text, true);
	}
	synchronized private void writeLine(String text, boolean toFile) throws IOException {
		int position = buffer.size();
		bufferWriter.write(text);
		bufferWriter.write(CHECKSUM_DELIM);
		bufferWriter.flush();
		updateChecksum();
		String checksumText = SH.toString(this.checksum, 62);
		bufferWriter.write(checksumText);
		bufferWriter.write(SH.CHAR_NEWLINE);
		bufferWriter.flush();
		if (toFile) {
			fileWriter.write(buffer.getBuffer(), position, buffer.size() - position);
			fileWriter.flush();
		}
	}

	public void populateText(VortexEyeJournalReport report) {
		//06 bits: Month 0=January, 2013
		//10 bits: Hours Eye was up
		//04 bits: Agent avg as percent, 10=100
		//13 bits: Max Agents
		//10 bits: Max Accounts

		int months = ((report.getYear() - 2013) * 12 + (report.getMonth() - 1)) % 64;
		int avgAgentUptime = report.getMaxAgents() == 0 ? 0 : (10 * (report.getAvgAgents() + report.getMaxAgents() / 20)) / report.getMaxAgents();
		long value = 0;

		long licenseChecksum = MH.abs(IOH.checkSumBsdLong((report.getClient() + report.getHostname()).getBytes())) % 8;

		value |= ((long) months) << (0);
		value |= ((long) report.getMinutesUp() / 60) << (6);
		value |= ((long) avgAgentUptime) << (6 + 10);
		value |= ((long) report.getMaxAgents()) << (6 + 10 + 4);
		value |= ((long) report.getMaxAccounts()) << (6 + 10 + 4 + 13);
		value |= ((long) licenseChecksum) << (6 + 10 + 4 + 13 + 10);
		StringBuilder sb = new StringBuilder();
		sb.append(report.getYear()).append('-').append(MONTHS[report.getMonth() - 1]);
		sb.append(", ");
		sb.append(F1LicenseInfo.getLicenseInstance());
		sb.append(", ");
		sb.append(F1LicenseInfo.getLicenseHost());
		sb.append(", ");
		SH.rightAlign('0', SH.toString(report.getMinutesUp() / 60), 2, false, sb);
		sb.append(":");
		SH.rightAlign('0', SH.toString(report.getMinutesUp() % 60), 2, false, sb);
		sb.append(" Up, ");
		sb.append(report.getMaxAgents()).append(" Hosts, ");
		sb.append(report.getMaxAccounts()).append(" Users, [");
		BitSet bits = MH.toBitSet(value);
		for (int i = 0, j = 46; i < j; i += 2, j -= 2) {
			boolean t = bits.get(i);
			bits.set(i, bits.get(j));
			bits.set(j, t);
		}
		sb.append(SH.toString(MH.toLong(bits) ^ MASK, 62));
		sb.append("]");
		report.setText(sb.toString());
	}
	private static final String MONTHS[] = new String[] { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
	private static long MASK = 139624182947283L;

	public VortexEyeJournalReport parseReport(String text) {
		String parts[] = SH.split(", ", text);
		String code = parts[6];

		long value = SH.parseLong(SH.trim('[', ']', code), 62) ^ MASK;
		BitSet bits = MH.toBitSet(value);
		for (int i = 0, j = 46; i < j; i += 2, j -= 2) {
			boolean t = bits.get(i);
			bits.set(i, bits.get(j));
			bits.set(j, t);
		}
		value = MH.toLong(bits);

		VortexEyeJournalReport r = reportGenerator.nw();

		int months = (int) (value & 63L);
		value >>= 6L;

		r.setMinutesUp((int) (value & 1023L) * 60);
		value >>= 10L;

		int avgAgentsPct = (int) (value & 15L);
		value >>= 4L;

		r.setMaxAgents((int) (value & 8191));
		value >>= 13L;

		r.setMaxAccounts((int) (value & 1023));
		value >>= 10L;
		long licenseChecksum = (long) (value & 7);
		value >>= 3L;
		//if (value != 0)
		//throw new RuntimeException("invalid report code: " + text);

		r.setMonth((months % 12) + 1);
		r.setYear(2013 + (months / 12));
		r.setAvgAgents(r.getMaxAgents() * avgAgentsPct / 10);
		validateReport(r);
		r.setText(text);

		String licenseInstance = parts[1];
		String licenseHost = parts[2];
		r.setClient(licenseInstance);
		r.setHostname(licenseHost);
		r.lock();

		String date = parts[0];
		String up = SH.stripSuffix(parts[3], " Up", true);
		String host = SH.stripSuffix(parts[4], " Hosts", true);
		String user = SH.stripSuffix(parts[5], " Users", true);
		int year = SH.parseInt(SH.beforeFirst(date, '-'));
		String month = SH.afterFirst(date, '-');
		int hours = Integer.parseInt(SH.beforeFirst(up, ':'));
		int minutes = Integer.parseInt(SH.afterFirst(up, ':'));
		int hosts = SH.parseInt(host);
		int users = SH.parseInt(user);
		long licenseChecksum2 = MH.abs(IOH.checkSumBsdLong((r.getClient() + r.getHostname()).getBytes())) % 8;
		OH.assertEq(licenseChecksum, licenseChecksum2, "license");
		OH.assertEq(hosts, r.getMaxAgents(), "Agents");
		OH.assertEq(users, r.getMaxAccounts(), "Accounts");
		OH.assertEq(year, r.getYear(), "Year");
		OH.assertEq(month, MONTHS[r.getMonth() - 1], "Month");
		OH.assertEq(hours, r.getMinutesUp() / 60, "Hours");
		//OH.assertEq(licenseInstance, F1LicenseInfo.getLicenseInstance());
		OH.assertEq(licenseHost, F1LicenseInfo.getLicenseHost());
		return r;
	}

	private int parseMonth(String text) {
		for (int i = 0; i < MONTHS.length; i++)
			if (MONTHS[i].equals(text))
				return i;
		throw new RuntimeException("invalid month: " + text);
	}

	@Override
	public void run() {
		logShutdown();
	}

	private List<Ping> pings = new ArrayList<Ping>();
	public void logPing(int agents, int accounts) {
		long now = EH.currentTimeMillis();
		pings.add(new Ping(now, agents, accounts));
		writeLine(now, "PN", SH.toString(agents) + "|" + SH.toString(accounts));
	}

	public void logStartup() {
		long now = EH.currentTimeMillis();
		writeLine(now, "UP", EH.getPid());
	}
	public void logShutdown() {
		long now = EH.currentTimeMillis();
		writeLine(now, "DN", EH.getPid());
	}
	public boolean needsReport() {
		long now = EH.currentTimeMillis();
		Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GTC"));
		cal.setTimeInMillis(now);
		int year = cal.get(Calendar.YEAR);
		int month = 1 + cal.get(Calendar.MONTH);
		return latestReport == null || (latestReport.getYear() <= year && latestReport.getMonth() < month);
	}
	public VortexEyeJournalReport logReport() {
		long now = EH.currentTimeMillis();
		Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GTC"));
		cal.setTimeInMillis(now);
		int year = cal.get(Calendar.YEAR);
		int month = 1 + cal.get(Calendar.MONTH);
		VortexEyeJournalReport rpt = reportGenerator.nw();
		rpt.setYear(year);
		rpt.setMonth(month);
		rpt.setClient(F1LicenseInfo.getLicenseInstance());
		rpt.setHostname(F1LicenseInfo.getLicenseHost());
		rpt.setMinutesUp(1000);

		long ping_duration = this.PING_FREQUENCY * 2;//perhaps it can get 
		long timeUpMs = 0;
		long lastPingTime = 0;
		LongAggregator accounts = new LongAggregator();
		LongAggregator agents = new LongAggregator();
		for (Ping ping : pings) {
			if (ping.getTime() - lastPingTime < this.PING_FREQUENCY * 2)
				timeUpMs += ping.getTime() - lastPingTime;
			lastPingTime = ping.getTime();
			accounts.add(ping.getUsersCount());
			agents.add(ping.getAgentsCount());
		}
		rpt.setMaxAccounts((int) accounts.getMax());
		rpt.setMaxAgents((int) agents.getMax());
		rpt.setAvgAgents((int) agents.getAverage());
		rpt.setMinutesUp((int) TimeUnit.MILLISECONDS.toMinutes(timeUpMs + 30000));//round

		populateText(rpt);
		addReports(rpt);
		writeLine(now, "RP", rpt.getText());
		pings.clear();
		return rpt;
	}
	private void addReports(VortexEyeJournalReport rpt) {
		reports.put(rpt.getYear() * 100 + rpt.getMonth(), rpt);
		if (latestReport == null || (latestReport.getYear() <= rpt.getYear() && latestReport.getMonth() < rpt.getMonth()))
			this.latestReport = rpt;
	}

}
