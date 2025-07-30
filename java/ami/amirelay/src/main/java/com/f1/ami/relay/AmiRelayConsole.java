package com.f1.ami.relay;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.f1.ami.relay.fh.AmiFH;
import com.f1.ami.relay.fh.AmiRelayUtils;
import com.f1.base.Console;
import com.f1.base.Table;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.f1.utils.structs.Tuple3;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.stack.BasicCalcTypes;

@Console(name = "AmiRelayServer", help = "Used to diagnose/control relay connections, transforms and routings")
public class AmiRelayConsole {

	private AmiRelayState state;
	String timeZone = "GMT";

	public AmiRelayConsole(AmiRelayState state) {
		this.state = state;
	}

	@Console(help = "Show all connections (aka feed handlers) and related statistics")
	public Table showConnections() {
		Table r = new BasicTable(Integer.class, "Login", String.class, "ID", String.class, "Name", String.class, "Class", String.class, "StartTime(" + timeZone + ")", String.class,
				"MessagesCount", Long.class, "ErrorsCount", Long.class, "RemoteIp", Integer.class, "RemotePort", String.class, "Status");
		r.setTitle("Feedhandler Connections");
		state.getPartition().lockForRead(1000, TimeUnit.MILLISECONDS);
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		try {
			Collection<AmiFHPeer> sessions = state.getAmiServer().getSessions();
			for (AmiFHPeer i : sessions) {
				AmiFH fh = i.getFh();
				String ct = df.format(fh.getConnectionTime());
				String ri = fh.getRemoteIp();
				int rp = fh.getRemotePort();
				String status = AmiRelayUtils.toStatusString(fh.getStatus());
				long mc = i.getMessageCount();
				long ec = i.getErrorsCount();
				String login = i.getAppId();
				r.getRows().addRow(i.getFhId(), login, i.getFhName(), fh.getClass().getSimpleName(), ct, mc, ec, ri, rp, status);
			}
		} finally {
			state.getPartition().unlockForRead();
		}
		return r;
	}
	@Console(help = "Enable transform debugging, which is printed to the AmiOne.log file")
	public void enableTransformDebug() {
		state.getTransformManager().setDebugMode(true);
	}

	@Console(help = "Disasbler transform debugging ")
	public void disableTransformDebug() {
		state.getTransformManager().setDebugMode(false);
	}
	@Console(help = "Enable routes debugging, which is printed to the AmiOne.log file")
	public void enableRoutesDebug() {
		state.getRouter().setDebugMode(true);
	}

	@Console(help = "Disasbler routes debugging ")
	public void disableRoutesDebug() {
		state.getRouter().setDebugMode(false);
	}

	@Console(help = "Show all translations in order of priority and related statistics")
	public String showTransforms() {
		AmiRelayTransforms tm = state.getTransformManager().getThreadSafeTransforms();
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		sb.append("       Compile Time: " + df.format(new Date(tm.getStartTime()))).append('\n');
		sb.append("     Transform File: " + tm.getTransformFileName()).append('\n');
		sb.append("   Dictionary Files: " + SH.join(',', tm.getDictionaries().keySet())).append('\n');
		Throwable parseException = state.getTransformManager().getParseException();
		if (parseException != null) {
			sb.append(" CONFIG OUT-OF-DATE: " + parseException.getMessage()).append('\n');
			sb.append("                     (See logs for details)\n");
		}
		sb.append("Process Time(Milis): " + tm.getStatesTotalNanos() / 1000000d).append('\n');
		sb.append("        Messages In: " + tm.getStatsInCount()).append('\n');
		sb.append("       Messages Out: " + tm.getStatsOutCount()).append('\n');
		sb.append("   Messages Dropped: " + tm.getStatsDropCount()).append('\n');
		sb.append("         Debug Mode: " + (tm.getDebugMode() ? "ON" : "OFF")).append('\n');
		AmiRelayTransform[] transforms = tm.getTransforms();
		BasicTable r = new BasicTable(String.class, "Name", String.class, "Dictionary", Long.class, "MatchCount", Long.class, "MismatchCount", Long.class, "MessagesSent",
				Double.class, "MillisSpent");
		r.setTitle("Transforms(Order by Priority)");
		if (transforms != null) {
			for (AmiRelayTransform t : transforms) {
				long match = t.getMatchCount();
				long skip = t.getMismatchCount();
				long sent = t.getSentCount();
				long nanos = t.getNanos();
				double millis = nanos / 1000000d;
				r.getRows().addRow(t.getName(), t.getDictionaryName(), match, skip, sent, millis);
			}
		}
		r.toString(sb);
		return sb.toString();
	}

	@Console(help = "Show all dictionaries")
	public String showDictionaries() {
		Map<String, AmiRelayDictionary> dictionaries = state.getTransformManager().getThreadSafeTransforms().getDictionaries();
		BasicTable r = new BasicTable(String.class, "Name", String.class, "Extends", String.class, "File Location");
		r.setTitle("Dictionaries");
		for (Entry<String, AmiRelayDictionary> entry : dictionaries.entrySet())
			r.getRows().addRow(entry.getKey(), SH.join(',', entry.getValue().getExtendsNames()), entry.getValue().getFileLocation());
		TableHelper.sort(r, "Name");
		return r.toString();
	}

	@Console(help = "Show Dictionary materialized field mappings (includes dictionary mappings from extended dictionaries)")
	public String showDictionary(String name) {
		Map<String, AmiRelayDictionary> dictionaries = state.getTransformManager().getThreadSafeTransforms().getDictionaries();
		AmiRelayDictionary d = dictionaries.get(name);
		if (d == null)
			return "Dictionary not found (Existing dictionaries are: " + SH.join(",", dictionaries.keySet());
		StringBuilder sb = new StringBuilder();
		sb.append("         Name: ").append(d.getName()).append('\n');
		sb.append("File Location: ").append(d.getFileLocation()).append('\n');
		sb.append("      Extends: ").append(SH.join(',', d.getExtendsNames())).append('\n');
		BasicTable r = new BasicTable(String.class, "Type", String.class, "Name");
		r.setTitle("Types");
		BasicCalcTypes types = d.getTypes();
		MethodFactoryManager mf = state.getScriptManager().getMethodFactory();
		for (String s : types.getVarKeys())
			r.getRows().addRow(s, mf.forType(types.getType(s)));
		r.toString(sb);
		sb.append("\n");
		r = new BasicTable(String.class, "Target", String.class, "Formula");
		r.setTitle("Fields");
		Tuple3<String, String[], DerivedCellCalculator>[] fields = d.getFields();
		for (Tuple3<String, String[], DerivedCellCalculator> field : fields) {
			r.getRows().addRow(field.getA(), field.getC());
		}
		sb.append("\n");
		r.toString(sb);
		sb.append("\n");
		return sb.toString();

	}
	public String resetRoutesStats() {
		state.getRouter().getThreadSafeRouter().resetStats();
		return "Routing Statistics have been reset";
	}
	public String resetTransformsStats() {
		state.getTransformManager().getThreadSafeTransforms().resetStats();
		return "Transform Statistics have been reset";
	}

	@Console(help = "Show Dictionary materialized field mappings (includes dictionary mappings from extended dictionaries)")
	public String showRoutes() {
		AmiRelayRouter rr = state.getRouter().getThreadSafeRouter();
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		sb.append("       Compile Time: " + df.format(new Date(rr.getStartTime()))).append('\n');
		sb.append("     Transform File: " + rr.getFileName()).append('\n');
		Throwable parseException = state.getRouter().getParseException();
		if (parseException != null) {
			sb.append(" CONFIG OUT-OF-DATE: " + parseException.getMessage()).append('\n');
			sb.append("                     (See logs for details)\n");
		}
		sb.append("Process Time(Milis): " + rr.getStatesTotalNanos() / 1000000d).append('\n');
		sb.append("        Messages In: " + rr.getStatsInCount()).append('\n');
		sb.append("       Messages Out: " + rr.getStatsOutCount()).append('\n');
		sb.append("   Messages Dropped: " + rr.getStatsDropCount()).append('\n');
		sb.append("         Debug Mode: " + (rr.getDebugMode() ? "ON" : "OFF")).append('\n');
		AmiRelayRoute[] routes = rr.getRoutes();
		BasicTable r = new BasicTable(String.class, "Name", String.class, "TargetCenters", Long.class, "MatchCount");
		r.setTitle("Transforms(Order by Priority)");
		if (routes != null) {
			for (AmiRelayRoute t : routes) {
				long match = t.getMatchCount();
				r.getRows().addRow(t.getRouteName(), SH.join(",", t.getRouteList()), match);
			}
		}
		r.toString(sb);
		return sb.toString();
	}

}
