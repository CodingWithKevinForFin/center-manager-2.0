package com.f1.ami.web.headless;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.AmiWebPortletManagerFactory;
import com.f1.ami.web.AmiWebProperties;
import com.f1.http.HttpServer;
import com.f1.http.HttpSession;
import com.f1.suite.web.HttpStateCreator;
import com.f1.suite.web.WebStatesManager;
import com.f1.utils.CH;
import com.f1.utils.CopyOnWriteHashMap;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.SafeFile;
import com.f1.utils.structs.Tuple2;

public class AmiWebHeadlessManager {
	public static final String SERVICE_ID = "AMI_HEADLESS_MANAGER";

	private static Logger log = LH.get();
	//	private Map<String, AmiWebHeadlessSession> sessionsByPgid = new CopyOnWriteHashMap<String, AmiWebHeadlessSession>();
	private Map<String, AmiWebHeadlessSession> sessionsByName = new CopyOnWriteHashMap<String, AmiWebHeadlessSession>();

	final private AmiWebPortletManagerFactory portletManagerFactory;

	private String[] lines = OH.EMPTY_STRING_ARRAY;

	private SafeFile path;
	private HttpServer server;
	private AtomicInteger modCount = new AtomicInteger();

	public AmiWebHeadlessManager(HttpServer server, AmiWebPortletManagerFactory portletManagerFactory, HttpStateCreator creator) throws IOException {
		this.portletManagerFactory = portletManagerFactory;
		File path = portletManagerFactory.getTools().getOptional(AmiWebProperties.PROPERTY_AMI_WEB_HEADLESS_FILE, File.class);
		this.path = path == null ? null : new SafeFile(path);
		this.server = server;
	}

	public void addSession(AmiWebHeadlessSession session) {
		CH.putOrThrow(this.sessionsByName, session.getSessionName(), session);
	}
	public void removeSession(AmiWebHeadlessSession session) {
		this.sessionsByName.remove(session.getSessionName());
	}

	public Set<String> getSessionNames() {
		return this.sessionsByName.keySet();
	}

	public AmiWebHeadlessSession getSessionByName(String name) {
		return this.sessionsByName.get(name);
	}

	public WebStatesManager getSessionManagerByUser(String user) {
		Collection<HttpSession> s = this.server.getHttpSessionManager().getSessions();
		for (HttpSession session : s) {
			WebStatesManager wsm = WebStatesManager.get(session);
			if (wsm != null && OH.eq(wsm.getUserName(), user)) {
				return wsm;
			}
		}
		return null;
	}

	public List<String> getActiveUsers() {
		List<String> activeUsers = new ArrayList<String>();
		Collection<HttpSession> s = this.server.getHttpSessionManager().getSessions();
		for (HttpSession session : s) {
			WebStatesManager wsm = WebStatesManager.get(session);
			if (wsm != null)
				activeUsers.add(wsm.getUserName());
		}
		return activeUsers;
	}

	public AmiWebHeadlessSession findSessionByPgid(String pgid) {
		for (AmiWebHeadlessSession i : this.sessionsByName.values()) {
			if (OH.eq(i.getPgid(), pgid))
				return i;
		}
		return null;
	}

	public void init() throws IOException {
		if (path == null)
			return;
		if (!path.exists()) {
			String lines[] = new String[] { "#Each line represents a headless session. Syntax is: HEADLESSNAME|USERNAME|SCREEN_WIDTH x SCREENHEIGHT|Key1=Value1|Key2=Value2|....",
					"#Start line with pound(#) for comment, start with bang(!) to not auto start session", "#headless1|headlessdemo|2000x1000|ISDEV=true" };
			try {
				LH.info(log, "headless file '", IOH.getFullPath(path.getFile()), " not found so creating a default one for demo purposes");
				path.setText(SH.join(SH.NEWLINE, lines));
			} catch (IOException e) {
				e.printStackTrace(System.err);
			}
		}
		String text = path.getText();
		if (SH.is(text)) {
			this.lines = SH.splitLines(text);
			int lineNumber = 0;
			for (String line : lines) {
				lineNumber++;
				if (SH.isnt(line) || line.startsWith("#"))
					continue;
				AmiWebHeadlessSession hs;
				try {
					hs = parseLine(line);
				} catch (Exception e) {
					throw new RuntimeException("Error in " + IOH.getFullPath(this.path.getFile()) + " at line " + lineNumber + ": " + e.getMessage(), e);
				}
				LH.info(log, "Added headless session: ", hs.getSessionName());
				addSession(hs);
			}
		}
	}

	public void addSessionByLine(String line) {
		AmiWebHeadlessSession hs;
		try {
			hs = parseLine(line);
		} catch (Exception e) {
			throw new RuntimeException("Error in " + IOH.getFullPath(this.path.getFile()) + " at " + line + ": " + e.getMessage(), e);
		}
		LH.info(log, "Added headless session: ", hs.getSessionName());
		addSession(hs);
	}

	static public Map<String, Object> parseAttributes(String parts) {
		return parseAttributes(0, SH.splitWithEscape('|', '\\', parts));
	}
	static public Map<String, Object> parseAttributes(int start, String[] parts) {
		final Map<String, Object> attributes = new HashMap<String, Object>();
		for (int i = start; i < parts.length; i++) {
			final String part = parts[i];
			if (SH.isnt(part))
				continue;
			final String key = SH.beforeFirst(part, '=', null);
			final String val = SH.afterFirst(part, '=', null);
			if (key != null && val != null)
				attributes.put(key, val);
		}
		return attributes;
	}

	static public Tuple2<Integer, Integer> parseResolution(String resolution) {
		final String w = SH.trim(SH.beforeFirst(resolution, 'x', null));
		final String h = SH.trim(SH.afterFirst(resolution, 'x', null));
		return new Tuple2<Integer, Integer>(parseInt(w), parseInt(h));
	}

	static private int parseInt(String n) {
		if (SH.is(n) && SH.areBetween(n, '0', '9')) {
			int r = SH.parseInt(n);
			if (r > 0)
				return r;
		}
		throw new RuntimeException("Invalid resolution, expecting format: NNN x NNN");
	}

	public AmiWebPortletManagerFactory getPortletManagerFactory() {
		return portletManagerFactory;
	}

	public void writeSessionsToDisk() {
		StringBuilder sb = new StringBuilder();
		Map<String, AmiWebHeadlessSession> t = new HashMap<String, AmiWebHeadlessSession>(sessionsByName);
		for (String s : this.lines) {
			if (SH.isnt(s))
				continue;
			if (s.startsWith("#"))
				sb.append(s).append(SH.NEWLINE);
			else {
				String name = SH.beforeFirst(s, '|');
				AmiWebHeadlessSession existing = t.remove(name);
				if (existing != null) {
					toLine(existing, sb);
					sb.append(SH.NEWLINE);
				}
			}
		}
		for (String s : CH.sort(t.keySet())) {
			toLine(t.get(s), sb);
			sb.append(SH.NEWLINE);
		}

		try {
			this.path.setText(sb.toString());
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	private AmiWebHeadlessSession parseLine(String line) {
		String[] parts = SH.splitWithEscape('|', '\\', line);
		if (parts.length < 3)
			throw new RuntimeException("Headless session requires at least 3 parameter: HEADLESSNAME|USERNAME|RESOLUTION|...");
		String name = parts[0];
		final String user = parts[1];
		final String resolution = parts[2];
		boolean autoStart;
		if (SH.startsWith(name, '!')) {
			autoStart = false;
			name = name.substring(1);
		} else
			autoStart = true;
		if (!AmiUtils.isValidVariableName(name, false, false))
			throw new RuntimeException("Headless session must be valid variable name");
		if (SH.isnt(user))
			throw new RuntimeException("Headless session username required");
		final Map<String, Object> attributes = parseAttributes(3, parts);
		Tuple2<Integer, Integer> res = parseResolution(resolution);
		AmiWebHeadlessSession r = new AmiWebHeadlessSession(this, name, user, res.getA(), res.getB(), attributes);
		if (autoStart)
			r.start();
		return r;
	}

	public static void toLine(AmiWebHeadlessSession s, StringBuilder sink) {
		if (!s.isAlive())
			sink.append('!');
		SH.escape(s.getSessionName(), '|', '\\', sink).append('|');
		SH.escape(s.getUsername(), '|', '\\', sink).append('|');
		sink.append(s.getDefaultWidth()).append('x').append(s.getDefaultHeight());
		if (CH.isntEmpty(s.getAttributes()))
			SH.joinMap('|', '=', '\\', s.getAttributes(), sink.append('|'));
	}

	public void incrementModCount() {
		modCount.getAndIncrement();
	}

	public int getModCount() {
		return this.modCount.get();
	}

}
