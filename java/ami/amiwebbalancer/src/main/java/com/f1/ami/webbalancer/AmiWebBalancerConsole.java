package com.f1.ami.webbalancer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.f1.ami.webbalancer.serverselector.AmiWebBalancerServerTestUrlResults_Stats;
import com.f1.base.Console;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.f1.utils.mutable.Mutable;
import com.f1.utils.mutable.Mutable.Long;
import com.f1.utils.structs.table.BasicTable;

@Console(name = "AmiWebBalancerServer", help = "Used to diagnose/control the web balancer")
public class AmiWebBalancerConsole {

	private AmiWebBalancerServer server;

	public AmiWebBalancerConsole(AmiWebBalancerServer server) {
		this.server = server;
	}

	@Console(help = "show all web servers")
	public String showWebServers() {
		String timeZone = "GMT";
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		BasicTable t = new BasicTable();
		t.addColumn(String.class, "Url");
		t.addColumn(String.class, "Status");
		t.addColumn(String.class, "LastUptime(" + timeZone + ")");
		t.addColumn(Integer.class, "Sessions");
		t.addColumn(Long.class, "Connections");
		t.addColumn(Long.class, "BytesToClient");
		t.addColumn(Long.class, "BytesToServer");
		t.addColumn(Long.class, "HttpRequests");
		t.addColumn(Double.class, "AvgResponseMillis");
		for (AmiWebBalancerServerInstance i : server.getAvailableServers()) {
			String uptime;
			long lastActiveTime = i.getLastActiveTime();
			if (lastActiveTime < 0)
				uptime = null;
			else
				uptime = df.format(new Date(lastActiveTime));
			Long connectionsCount = new Long();
			Long bytesToClient = new Mutable.Long();
			Long bytesToServer = new Mutable.Long();
			Long cnt = new Mutable.Long();
			Long nanos = new Mutable.Long();
			i.getConnectionStats(connectionsCount, bytesToClient, bytesToServer, cnt, nanos);
			t.getRows().addRow(i.getHostPort(), i.isAlive() ? "UP" : "DOWN", uptime, i.getActiveSessionsCount(), connectionsCount.value, bytesToClient.value, bytesToServer.value,
					cnt.value, cnt.value == 0 ? Double.NaN : nanos.value / cnt.value / 1000000d);
		}
		return TableHelper.toString(t, "", TableHelper.SHOW_ALL_BUT_TYPES);
	}
	@Console(help = "show all web server test url stats")
	public String showWebServerTestUrlStats() {
		String timeZone = "GMT";
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		BasicTable t = new BasicTable();
		t.addColumn(String.class, "Url");
		t.addColumn(String.class, "Status");
		t.addColumn(Integer.class, "Logins");
		t.addColumn(Integer.class, "Sessions");
		t.addColumn(Double.class, "CpuPct");
		t.addColumn(String.class, "MemUsed");
		t.addColumn(String.class, "MemUsedAfterGc");
		t.addColumn(String.class, "MemMax");
		t.addColumn(String.class, "StartTime");
		t.addColumn(Integer.class, "Threads");
		t.addColumn(Integer.class, "ThreadsTotal");
		for (AmiWebBalancerServerInstance i : server.getAvailableServers()) {
			AmiWebBalancerServerTestUrlResults_Stats stats = (AmiWebBalancerServerTestUrlResults_Stats) i.getTestUrlStats();
			if (stats != null) {
				t.getRows().addRow(i.getHostPort(), i.isAlive() ? "UP" : "DOWN", stats.getLogins(), stats.getSessions(), stats.getCpuPct(), SH.formatMemory(stats.getMemUsed()),
						SH.formatMemory(stats.getMemUsedAfterGc()), SH.formatMemory(stats.getMemMax()), df.format(new Date(stats.getStartTime())), stats.getThreadsRunnable(),
						stats.getThreadsTotal());
			}
		}
		return TableHelper.toString(t, "", TableHelper.SHOW_ALL_BUT_TYPES);
	}
	@Console(help = "show all connections")
	public String showConnections() {
		String timeZone = "GMT";
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		BasicTable t = new BasicTable();
		t.addColumn(String.class, "ClientAddr");
		t.addColumn(String.class, "ClientProto");
		t.addColumn(String.class, "ServerUrl");
		t.addColumn(String.class, "Started(" + timeZone + ")");
		t.addColumn(Long.class, "BytesToClient");
		t.addColumn(Long.class, "BytesToServer");
		t.addColumn(Long.class, "HttpRequests");
		t.addColumn(Double.class, "AvgResponseMillis");
		List<AmiWebBalancerConnection> sink = new ArrayList<AmiWebBalancerConnection>();
		server.getConnections(sink);
		for (AmiWebBalancerConnection i : sink) {
			String uptime = df.format(new Date(i.getStartTime()));
			long cnt = i.getRequestCounts();
			long nanos = i.getRequestNanos();
			t.getRows().addRow(i.getClientAddres(), i.getClientSecure() ? "https" : " http", i.getServerURL(), uptime, i.getBytesToClient(), i.getBytesToServer(), cnt,
					cnt == 0 ? Double.NaN : nanos / cnt / 1000000d);
		}
		return TableHelper.toString(t, "", TableHelper.SHOW_ALL_BUT_TYPES);
	}

}
