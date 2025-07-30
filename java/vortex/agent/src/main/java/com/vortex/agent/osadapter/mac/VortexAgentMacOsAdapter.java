package com.vortex.agent.osadapter.mac;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.utils.AH;
import com.f1.utils.CharReader;
import com.f1.utils.EH;
import com.f1.utils.GuidHelper;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.structs.Tuple3;
import com.f1.vortexcommon.msg.agent.VortexAgentCron;
import com.f1.vortexcommon.msg.agent.VortexAgentFileSystem;
import com.f1.vortexcommon.msg.agent.VortexAgentMachine;
import com.f1.vortexcommon.msg.agent.VortexAgentMachineEventStats;
import com.f1.vortexcommon.msg.agent.VortexAgentNetAddress;
import com.f1.vortexcommon.msg.agent.VortexAgentNetConnection;
import com.f1.vortexcommon.msg.agent.VortexAgentNetLink;
import com.f1.vortexcommon.msg.agent.VortexAgentProcess;
import com.vortex.agent.osadapter.VortexAgentAbstractOsAdapter;
import com.vortex.agent.state.VortexAgentOsAdapterState;

public class VortexAgentMacOsAdapter extends VortexAgentAbstractOsAdapter {
	private static final Logger log = LH.get(VortexAgentMacOsAdapter.class);

	public static final Map<String, Integer> MONTHS = new HashMap<String, Integer>();
	public static final Map<String, Short> NETLINKSTATES = new HashMap<String, Short>();
	public static final Map<String, Byte> NETADDRESSSCOPES = new HashMap<String, Byte>();
	static {
		NETLINKSTATES.put("ALLMULTI", VortexAgentNetLink.STATE_ALLMULTI);
		NETLINKSTATES.put("BROADCAST", VortexAgentNetLink.STATE_BROADCAST);
		NETLINKSTATES.put("DYNAMIC", VortexAgentNetLink.STATE_DYNAMIC);
		NETLINKSTATES.put("LOOPBACK", VortexAgentNetLink.STATE_LOOPBACK);
		NETLINKSTATES.put("LOWER_UP", VortexAgentNetLink.STATE_LOWER_UP);
		NETLINKSTATES.put("MULTICAST", VortexAgentNetLink.STATE_MULTICAST);
		NETLINKSTATES.put("NOARP", VortexAgentNetLink.STATE_NOARP);
		NETLINKSTATES.put("POINTTOPOINT", VortexAgentNetLink.STATE_POINTTOPOINT);
		NETLINKSTATES.put("PROMISC", VortexAgentNetLink.STATE_PROMISC);
		NETLINKSTATES.put("SLAVE", VortexAgentNetLink.STATE_SLAVE);
		NETLINKSTATES.put("UP", VortexAgentNetLink.STATE_UP);
		NETADDRESSSCOPES.put("global", VortexAgentNetAddress.SCOPE_GLOBAL);
		NETADDRESSSCOPES.put("host", VortexAgentNetAddress.SCOPE_HOST);
		NETADDRESSSCOPES.put("link", VortexAgentNetAddress.SCOPE_LINK);
		NETADDRESSSCOPES.put("site", VortexAgentNetAddress.SCOPE_SITE);
		MONTHS.put("Jan", 0);
		MONTHS.put("Feb", 1);
		MONTHS.put("Mar", 2);
		MONTHS.put("Apr", 3);
		MONTHS.put("May", 4);
		MONTHS.put("Jun", 5);
		MONTHS.put("Jul", 6);
		MONTHS.put("Aug", 7);
		MONTHS.put("Sep", 8);
		MONTHS.put("Oct", 9);
		MONTHS.put("Nov", 10);
		MONTHS.put("Dec", 11);
	}

	@Override
	public List<VortexAgentNetAddress> inspectNetAddresses(VortexAgentOsAdapterState state) {
		List<VortexAgentNetAddress> r = new ArrayList<VortexAgentNetAddress>();
		String stdout = new String(execute(state, sudo() + "ifconfig -a").getB()).trim();

		String[] lines = SH.splitLines(stdout);
		StringBuilder sb = new StringBuilder();
		StringCharReader sr = new StringCharReader(OH.EMPTY_CHAR_ARRAY);
		for (int i = 0; i < lines.length;) {
			sr.reset(lines[i++]);// skip next line
			sr.readUntil(':', SH.clear(sb));

			final String name = sb.toString();
			//	LH.warning(log, "Net name= " + name + "  "+ lines[i]);
			sr.expect(':');

			try {
				while (i < lines.length && (lines[i].startsWith(" ") || lines[i].startsWith("\t"))) {
					final String line = lines[i++].trim();
					String[] parts = SH.splitContinous(' ', line);
					String type = parts[0], scope = null, address = null, broadcast = null;
					byte type2;
					if (type.equals("inet6")) {
						type2 = (VortexAgentNetAddress.TYPE_INET6);
					} else if (type.equals("inet")) {
						type2 = (VortexAgentNetAddress.TYPE_INET);
					} else
						continue;
					final VortexAgentNetAddress l = state.nw(VortexAgentNetAddress.class);
					address = parts[1];
					if (address != null)
						l.setAddress(address);

					l.setType(type2);

					int idx2 = AH.indexOf("broadcast", parts);
					if (idx2 != -1)
						broadcast = parts[idx2 + 1];
					//	idx2 = AH.indexOf("scope", parts);
					//	if (idx2 != -1)
					//	scope = parts[idx2 + 1];
					//Byte scopeMask = NETADDRESSSCOPES.get(scope);
					//if (scopeMask != null)
					//	l.setScope(scopeMask);

					if (broadcast != null)
						l.setBroadcast(broadcast);

					l.setLinkName(name);
					r.add(l);

				}

			} catch (Exception e) {
				LH.warning(log, "Error with lines: ", stdout);
			}
		}
		return r;
	}

	@Override
	public List<VortexAgentNetLink> inspectNetLinks(VortexAgentOsAdapterState state) {
		List<VortexAgentNetLink> r = new ArrayList<VortexAgentNetLink>();
		String stdout1 = new String(execute(state, sudo() + "netstat -ibd ").getB()).trim();
		String stdout = new String(execute(state, sudo() + "ifconfig -a ").getB()).trim();

		Map<String, String> links = new HashMap<String, String>();

		String[] lines1 = SH.splitLines(stdout1);

		for (int i = 1; i < lines1.length; i++) {

			String[] parts = SH.splitContinous(' ', lines1[i]);
			if (SH.startsWith(parts[2], '<'))
				continue;
			links.put(parts[0], SH.join(':', parts[0], parts[1], parts[4], parts[5], parts[7], parts[8], parts[10]));

		}

		String[] lines = SH.splitLines(stdout);
		StringBuilder sb = new StringBuilder();
		StringCharReader sr = new StringCharReader(OH.EMPTY_CHAR_ARRAY);
		for (int i = 0; i < lines.length;) {
			sr.reset(lines[i++]);
			sr.readUntil(':', SH.clear(sb));

			final String name = sb.toString();
			String link = links.get(name);
			sr.readUntil('<', SH.clear(sb));
			sr.skipChars(1);
			sr.readUntil('>', SH.clear(sb));
			sr.skipChars(1);

			short lstateMask = 0;
			for (String s : SH.split(',', sb.toString())) {
				Short mask = NETLINKSTATES.get(s);
				if (mask != null)
					lstateMask |= mask;

			}

			String broadcast = null, mac = null;
			try {
				while (i < lines.length && (lines[i].startsWith(" ") || lines[i].startsWith("\t"))) {

					if (link == null) {
						i++;
						continue;
					}
					final String line = lines[i++].trim();
					String[] parts = SH.splitContinous(' ', line);
					String type = parts[0];

					if (type.equals("ether")) {
						mac = parts[1];
					} else
						continue;

					int idx2 = AH.indexOf("broadcast", parts);
					if (idx2 != -1)
						broadcast = parts[idx2 + 1];

				}
				if (link != null) {

					String[] parts = SH.split(':', link);
					VortexAgentNetLink l = state.nw(VortexAgentNetLink.class);
					l.setName(parts[0]);
					l.setMtu(SH.parseLong(parts[1]));

					l.setRxPackets(SH.parseLong(parts[2]));
					if ("-".equals(parts[3]) == false)
						l.setRxErrors(SH.parseLong(parts[3]));
					else
						l.setRxErrors(0);

					l.setTxPackets(SH.parseLong(parts[4]));
					if ("-".equals(parts[5]) == false)
						l.setTxErrors(SH.parseLong(parts[5]));
					else
						l.setTxErrors(0);

					if ("-".equals(parts[6]) == false)
						l.setTxCollsns(SH.parseLong(parts[6]));
					else
						l.setTxCollsns(0);

					if (broadcast != null)
						l.setBroadcast(broadcast);
					if (mac != null)
						l.setMac(mac);

					l.setState(lstateMask);

					r.add(l);

				}
			} catch (Exception e) {
				LH.warning(log, "Problem processing Net Links ", e);
			}
		}
		return r;
	}
	@Override
	public List<VortexAgentNetConnection> inspectNetConnections(VortexAgentOsAdapterState state) {
		String stdout = new String(execute(state, sudo() + "lsof -Pni ").getB());
		final List<VortexAgentNetConnection> r = new ArrayList<VortexAgentNetConnection>();
		String[] lines = SH.splitLines(stdout);
		//LH.warning(log," lsof returned = ", stdout);
		for (int i = 1; i < lines.length; i++) {
			final String line = lines[i];
			//		LH.warning(log," lsof returned = ", line);
			try {
				if (SH.isnt(line))
					continue;
				final String parts[] = SH.splitContinous(' ', line);
				if ("TCP".equals(parts[7]) == false)
					continue;
				String netState = SH.trim('(', ')', parts[9]);
				if (netState == null)
					continue;

				VortexAgentNetConnection netConnection = state.nw(VortexAgentNetConnection.class);
				if ("LISTEN".equals(netState)) {
					String localAddress = SH.beforeLast(parts[8], ":", null);
					String localPort = SH.afterLast(parts[8], ":", null);
					netConnection.setLocalHost(localAddress);
					netConnection.setLocalPort(SH.parseInt(localPort));
					netConnection.setState(VortexAgentNetConnection.STATE_LISTEN);
				} else if ("ESTABLISHED".equals(netState)) {
					String[] parts2 = SH.split("->", parts[8]);

					String localAddress = SH.beforeLast(parts2[0], ":", null);
					String localPort = SH.afterLast(parts2[0], ":", null);
					String remoteAddress = SH.beforeLast(parts2[1], ":", null);
					String remotePort = SH.afterLast(parts2[1], ":", null);
					netConnection.setLocalHost(localAddress);
					netConnection.setLocalPort(SH.parseInt(localPort));
					netConnection.setForeignHost(remoteAddress);
					netConnection.setForeignPort(SH.parseInt(remotePort));
					netConnection.setState(VortexAgentNetConnection.STATE_ESTABLISHED);
				}

				else if ("CLOSE_WAIT".equals(netState))
					netConnection.setState(VortexAgentNetConnection.STATE_CLOSE_WAIT);
				else if ("TIME_WAIT".equals(netState)) {
					continue;//too many of these!
					//netConnection.setState(AgentNetConnection.STATE_TIME_WAIT);
				} else {
					LH.info(log, "Uknown state: " + line);
					continue;
				}
				netConnection.setLocalPid(parts[1]);
				netConnection.setLocalAppName(parts[0]);

				r.add(netConnection);
			} catch (Exception e) {
				LH.warning(log, "Error with 'lsof' line: ", line, e);
			}
		}
		return r;
	}

	@Override
	public List<VortexAgentProcess> inspectProcesses(VortexAgentOsAdapterState state) {
		final List<VortexAgentProcess> r = new ArrayList<VortexAgentProcess>();
		String command = "ps -eo pcpu,rss,ruser,pid,ppid,lstart,comm,args";
		long now = state.getPartition().getContainer().getTools().getNow();
		final String stdout = new String(execute(state, sudo() + command).getB());
		final String[] lines = SH.splitLines(stdout);
		final StringBuilder sb = new StringBuilder();
		for (int i = 1; i < lines.length; i++) {
			final String line = lines[i];
			if (SH.isnt(line))
				continue;
			try {
				final VortexAgentProcess ap = state.nw(VortexAgentProcess.class);
				final StringCharReader reader = new StringCharReader(line);
				reader.skip(' ');

				reader.readUntil(' ', SH.clear(sb));
				ap.setCpuPercent(Double.parseDouble(sb.toString()) / 100);
				reader.skip(' ');

				reader.readUntil(' ', SH.clear(sb));
				ap.setMemory(1024 * Long.parseLong(sb.toString()));
				reader.skip(' ');

				reader.readUntil(' ', SH.clear(sb));
				ap.setUser(sb.toString());
				reader.skip(' ');

				reader.readUntil(' ', SH.clear(sb));
				ap.setPid(sb.toString());
				reader.skip(' ');

				reader.readUntil(' ', SH.clear(sb));
				ap.setParentPid(sb.toString());
				reader.skip(' ');

				reader.readUntil(' ', SH.clear(sb));

				reader.skip(' ');

				reader.readUntil(' ', SH.clear(sb));
				String monthOfYear = sb.toString();
				reader.skip(' ');

				reader.readUntil(' ', SH.clear(sb));
				String dayOfMonth = sb.toString();
				reader.skip(' ');

				reader.readUntil(':', SH.clear(sb));
				String hourOfDay = sb.toString();
				reader.skip(':');
				reader.readUntil(':', SH.clear(sb));
				String minute = sb.toString();
				reader.skip(':');
				reader.readUntil(' ', SH.clear(sb));
				String seconds = sb.toString();
				reader.skip(' ');
				reader.readUntil(' ', SH.clear(sb));
				String year = sb.toString();
				reader.skip(' ');

				final Calendar cal = Calendar.getInstance();
				cal.clear();
				cal.set(Calendar.MONTH, MONTHS.get(monthOfYear.toString()));
				cal.set(Calendar.YEAR, SH.parseInt(year));
				cal.set(Calendar.DAY_OF_MONTH, SH.parseInt(dayOfMonth));
				cal.set(Calendar.HOUR_OF_DAY, SH.parseInt(hourOfDay));
				cal.set(Calendar.MINUTE, SH.parseInt(minute));
				cal.set(Calendar.SECOND, SH.parseInt(seconds));
				ap.setStartTime(cal.getTimeInMillis());

				reader.skip(' ');
				reader.readUntil(CharReader.EOF, SH.clear(sb));
				ap.setCommand(sb.toString());
				if (ap.getCommand().contains(command))
					continue;
				r.add(ap);
			} catch (Exception e) {
				LH.warning(log, "Error parsing 'ps' line: ", line, e);
			}
		}

		return r;
	}
	@Override
	public long runLastReboot(VortexAgentOsAdapterState state) {
		final String stdout = new String(execute(state, sudo() + "sysctl -n kern.boottime  ").getB());
		String line = SH.beforeFirst(stdout, '\n');
		if (SH.isnt(line))
			return 0;
		try {
			final StringCharReader reader = new StringCharReader(line);
			final StringBuilder sb = new StringBuilder();
			reader.readUntil('}', sb);
			reader.skip('}');
			reader.skip(' ');

			reader.readChars(24, SH.clear(sb));
			LH.warning(log, "Boot time =" + sb.toString());
			return Date.parse(sb.toString());
		} catch (Exception e) {
			LH.warning(log, "Error parsing 'sysctl kern.boottime' cmd: ", stdout, e);
			return 0;
		}

	}

	public List<VortexAgentFileSystem> inspectFileSystems(VortexAgentOsAdapterState state) {

		final String stdout = new String(execute(state, sudo() + "df -b ").getB());
		final String stdout1 = new String(execute(state, sudo() + "mount").getB());
		final String[] lines = SH.trimArray(SH.splitLines(stdout));
		final String[] lines1 = SH.trimArray(SH.splitLines(stdout1));
		Map<String, String> fsTypes = new HashMap<String, String>();
		for (int j = 0; j < lines1.length; j++) {
			String fsName;
			int index = 3;
			final String fsType = lines1[j];
			String[] parts = SH.splitContinous(' ', fsType);
			if (parts[0].equals("map")) {
				fsName = SH.join(' ', parts[0], parts[1]);
				index = 4;
			} else
				fsName = parts[0];
			if (SH.startsWith(parts[index], '(') == false)
				index++;
			String type = SH.trim('(', ',', parts[index]);
			//	LH.warning(log," fsname = " + fsName + " and type = " + type);
			fsTypes.put(fsName, type);
		}
		
		if (lines.length > 0 && !lines[0].startsWith("Filesystem"))
			LH.warning(log, "unexpected header for df: ", lines[0]);
		final List<VortexAgentFileSystem> fileSystems = new ArrayList<VortexAgentFileSystem>();
		for (int i = 1; i < lines.length; i++) {
			boolean twoLines = false;
			try {
				String name;
				int index = 1;
				String[] parts = SH.splitContinous(' ', lines[i]);
				if (parts.length == 1) {//it wrapped to 2nd line
					String[] parts2 = SH.splitContinous(' ', lines[++i]);
					parts = AH.cat(parts, parts2);
					twoLines = true;
				}
				VortexAgentFileSystem fs = state.nw(VortexAgentFileSystem.class);
				if (parts[0].equals("map")) {
					name = SH.join(' ', parts[0], parts[1]);
					index = 2;
				} else
					name = parts[0];

				String type = fsTypes.get(name);
				long totalSpace = Long.parseLong(parts[index]) * 512;
				//long usedSpace = Long.parseLong(parts[3]);
				long freeSpace = Long.parseLong(parts[index + 2]) * 512;
				fs.setFreeSpace(freeSpace);
				fs.setUsableSpace(totalSpace);
				fs.setTotalSpace(totalSpace);
				fs.setName(name);
				fs.setType(type);
				//	LH.warning(log," entry found for fsname = " + name + " and type = " + type);
				fileSystems.add(fs);
			} catch (Exception e) {
				if (twoLines)
					LH.warning(log, "Error parsing 2 lines of 'df' result: ", lines[i - 1], SH.NEWLINE, lines[i], e);
				else
					LH.warning(log, "Error parsing line of 'df' result: ", lines[i], e);
			}
		}
		return fileSystems;
	}
	public void runFree(VortexAgentOsAdapterState state, VortexAgentMachine msg) {

		try {
			String stdout = new String(execute(state, sudo() + "sysctl vm.swapusage").getB());
			String stdout1 = new String(execute(state, sudo() + "vm_stat").getB());

			final String[] memLines = SH.splitLines(stdout1);
			String[] swp = SH.splitContinous(' ', stdout);
			msg.setTotalSwapMemory((long) (SH.parseFloat(SH.stripSuffix(swp[3], "M", false)) * 1048576));
			msg.setUsedSwapMemory((long) (SH.parseFloat(SH.stripSuffix(swp[6], "M", false)) * 1048576));

			long totalMem = 0, usedMem = 0;
			for (int i = 1; i < 6; i++) {
				String[] mem = SH.splitContinous(' ', memLines[i]);
				long memCount = SH.parseLong(SH.stripSuffix(mem[2], ".", false));
				totalMem += memCount;
				if (i == 2)
					usedMem += memCount;

			}
			String[] mem = SH.splitContinous(' ', memLines[6]);
			long memCount = SH.parseLong(SH.stripSuffix(mem[3], ".", false));
			totalMem += memCount;
			usedMem += memCount;

			msg.setTotalMemory(totalMem);
			msg.setUsedMemory(usedMem);

		} catch (Exception e) {
			LH.warning(log, "Error parsing 'sysctl or vm_stat' cmd: ", e);

		}

		final String stdout2 = new String(execute(state, sudo() + "iostat -dC").getB());
		try {
			final String[] lines = SH.splitLines(stdout2);

			String[] parts = SH.splitContinous(' ', lines[2]);
			double pctIdle = Double.parseDouble(parts[parts.length - 1]);
			msg.setSystemLoadAverage((100 - pctIdle) / 100);

		} catch (Exception e) {
			LH.warning(log, "Error parsing 'iostat' cmd: ", stdout2, e);
		}

	}

	public VortexAgentMachine inspectMachine(VortexAgentOsAdapterState state) throws IOException {
		VortexAgentMachine r = super.inspectMachine(state);
		r.setAgentProcessUid(EH.getProcessUid());
		Tuple3<Process, byte[], byte[]> result = execute(state, sudo() + "sysctl -n machdep.cpu.brand_string");
		String stdout = new String(result.getB());
		String[] lines = SH.splitLines(stdout);
		//	LH.warning(log,"sysctl returns"+lines);
		for (String line : lines) {
			//		LH.warning( log, "OS Arch = " + line);
			r.setOsArchitecture(line);

		}
		/*
		Tuple3<Process, byte[], byte[]> result1 = execute(state, sudo() + "/usr/sbin/system_profiler | /usr/bin/grep ", new File(".")," \"Number of Processors\"");
		 stdout = new String(result.getB());
		String[] lines1 = SH.splitLines(stdout);
		LH.warning(log,"profiler returns"+lines1);
		for (String line : lines1) {
			LH.warning( log, "CPU Count = " + line);
			String[] parts = SH.split(' ', line);
			r.setCpuCount(SH.parseInt(parts[3]));	
			
		}
		*/
		return r;
	}

	@Override
	public String getHostName(VortexAgentOsAdapterState state) throws IOException {
		return new String(execute(state, sudo() + "hostname").getB()).trim();
	}

	@Override
	public String getMachineUid(VortexAgentOsAdapterState state) throws IOException {
		final File file = new File("/etc/3forge/machineuid.txt");
		Tuple3<Process, byte[], byte[]> result = execute(state, sudo() + "cat " + file.getAbsolutePath());
		String r = null;
		if (result.getA().exitValue() == 1) {
			final String machineuid = GuidHelper.getGuid(62);
			final File tmpFile = File.createTempFile("machineuid", "");
			IOH.writeText(tmpFile, machineuid + SH.NEWLINE);
			assertExitCode(execute(state, sudo() + "mkdir -p " + file.getParentFile().getAbsolutePath()));
			assertExitCode(execute(state, sudo() + "cp " + tmpFile.getAbsolutePath() + " " + file.getAbsolutePath()));
			tmpFile.delete();
			assertExitCode(execute(state, sudo() + "chmod 444 " + file.getAbsolutePath()));
			result = execute(state, sudo() + "cat " + file.getAbsolutePath());
			assertExitCode(result);
			r = new String(result.getB());
		}
		r = new String(result.getB());
		for (char c : "\n\r ".toCharArray())
			r = SH.trim(c, r);
		if (SH.isnt(r))
			throw new RuntimeException("blank machine name");
		return r;

	}

	/*private Tuple3<Process, byte[], byte[]> assertExitCode(Tuple3<Process, byte[], byte[]> result) {
		if (result.getA().exitValue() != 0)
			throw new RuntimeException("exit code " + result.getA().exitValue() + ": " + new String(result.getC()));
		return result;
	}*/

	protected String sudo() {
		if ("root".equals(EH.getUserName()))
			return "";
		return "sudo ";
	}

	protected Tuple3<Process, byte[], byte[]> execute(VortexAgentOsAdapterState state, String command) {
		if (log.isLoggable(Level.FINE))
			LH.fine(log, command);
		Tuple3<Process, byte[], byte[]> process = EH.exec(state.getPartition().getContainer().getThreadPoolController(), SH.splitContinous(' ',command));
		if (log.isLoggable(Level.FINE)) {
			//		LH.fine(log,"STDOUT\n" + new String(process.getB()));
			LH.fine(log, "STDERR\n" + new String(process.getC()));
		}
		return process;
	}

	@Override
	public List<VortexAgentMachineEventStats> inspectMachineEvents(VortexAgentOsAdapterState state, long onwards, byte level) {

		//		long id = getTools().getUidLong("AgentMachineEventStats");
		final List<VortexAgentMachineEventStats> stats = new ArrayList<VortexAgentMachineEventStats>();
		final String toExecute = sudo() + "last";
		final String stdout = new String(execute(state, toExecute).getB());
		final StringBuilder sb = new StringBuilder();
		String[] lines = SH.splitLines(stdout);
		String line;
		for (int i = 0; i < lines.length; i++) {

			line = lines[i];
			if (SH.isnt(line))
				continue;
			try {

				final StringCharReader reader = new StringCharReader(line);
				VortexAgentMachineEventStats stat = state.nw(VortexAgentMachineEventStats.class);

				reader.skip(' ');
				reader.readUntil(' ', SH.clear(sb));
				String name = sb.toString();
				if ("wtmp".equals(name))
					continue;

				reader.skip(' ');
				reader.readUntil(' ', SH.clear(sb));
				String msg = sb.toString();
/*
				if ("~".equals(msg)) {
					reader.skip(' ');
					reader.readUntil(' ', SH.clear(sb));
					msg += " " + sb.toString();
				}

				reader.skip(' ');
				reader.readUntil(' ', SH.clear(sb));
				String host = sb.toString();
				Date date;
				long gen = 0;
				try {
					reader.skip(' ');
					reader.readChars(24, SH.clear(sb));
					date = state.parseSystemDate(sb.toString());
					gen = date.getTime();
				} catch (ParseException pe) {
					LH.warning(log, "Error parsing date: ", sb.toString());
				}

				reader.skip(' ');
				reader.readUntil(' ', SH.clear(sb));
				String qualifier = sb.toString();
				if ("-".equals(qualifier)) {
					reader.skip(' ');
					reader.mark();
					reader.readChars(13, SH.clear(sb));
					try {
						date = state.parseSystemDate(sb.toString());
						long end = date.getTime();
						stat.setEndTime(end);
					} catch (ParseException pe) {
						reader.returnToMark();
						reader.readUntil('(', SH.clear(sb));
						stat.setNotEnded(sb.toString());
					}
					BitSet set = new BitSet();
					set.set(' ');
					set.set('(');
					reader.skip(set);
					reader.readUntil(')', SH.clear(sb));
					stat.setDuration(sb.toString());
				} else {
					reader.readUntil('\n', sb);
					qualifier = sb.toString();
					stat.setNotEnded(qualifier);
				}

				stat.setLevel((byte) 6);
				stat.setTimeGenerated(gen);
				stat.setHost(host);
				*/
				//				stat.setId(id);
				stat.setMessage(msg);
				stat.setName("MacOS");
				stat.setUserName(name);
				stat.setSource("wtmp");

				stats.add(stat);
			} catch (Exception e) {
				LH.warning(log, "Error parsing 'last' cmd: ", stdout, e);
				return stats;
			}
		}

		return stats;
	}

	@Override
	public List<VortexAgentCron> inspectCron(VortexAgentOsAdapterState state) {
		Tuple3<Process, byte[], byte[]> lsresult = execute(state, sudo() + "ls /var/spool/cron");
		if (lsresult.getA().exitValue() != 0)
			return Collections.EMPTY_LIST;
		List<VortexAgentCron> crons = new ArrayList<VortexAgentCron>();
		for (String filename : SH.splitLines(new String(lsresult.getB()))) {
			if (!SH.is(filename))
				continue;
			final String user = filename.trim();
			Tuple3<Process, byte[], byte[]> result = execute(state, sudo() + "cat /var/spool/cron/" + user);
			final String stdout = new String(result.getB());
			final StringBuilder sb = new StringBuilder();
			String[] lines = SH.splitLines(stdout);
			final StringCharReader reader = new StringCharReader();
			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				line = SH.replaceAll(line, SH.CHAR_TAB, ' ');
				if (SH.isnt(line))
					continue;
				try {

					reader.reset(line);
					VortexAgentCron cron = state.nw(VortexAgentCron.class);
					cron.setUser(user);

					reader.skip(' ');
					reader.readUntil(' ', SH.clear(sb));
					cron.setMinute(sb.toString());

					reader.skip(' ');
					reader.readUntil(' ', SH.clear(sb));
					cron.setHour(sb.toString());

					reader.skip(' ');
					reader.readUntil(' ', SH.clear(sb));
					cron.setDayOfMonth(sb.toString());

					reader.skip(' ');
					reader.readUntil(' ', SH.clear(sb));
					cron.setMonth(sb.toString());

					reader.skip(' ');
					reader.readUntil(' ', SH.clear(sb));
					cron.setDayOfWeek(sb.toString());

					reader.skip(' ');
					reader.readUntil(CharReader.EOF, SH.clear(sb));
					cron.setCommand(sb.toString());

					cron.setTimeZone("" + TimeZone.getDefault().getOffset(System.currentTimeMillis()));
					cron.setSecond("0");
					//if (null != state.current.getMachine())
					//cron.setMachineInstanceId(state.current.getMachine().getMachineInstanceId());
					crons.add(cron);
				} catch (Exception e) {
					LH.warning(log, "Error parsing ' cat cron' cmd: ", stdout, e);
				}
			}
		}

		return crons;
	}
}
