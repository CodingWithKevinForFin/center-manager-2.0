/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

import com.f1.container.Partition;
import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.http.HttpSessionManager;
import com.f1.http.HttpSessionManagerListener;
import com.f1.http.HttpUtils;
import com.f1.http.impl.BasicHttpRequestResponse;
import com.f1.http.impl.HttpMultiPart;
import com.f1.suite.web.portal.PortletDownload;
import com.f1.suite.web.portal.PortletManager;
import com.f1.utils.CH;
import com.f1.utils.FastByteArrayOutputStream;
import com.f1.utils.FastPrintStream;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple3;

public class PortalHttpHandler extends HttpStateHandler implements HttpSessionManagerListener {

	public static final Logger log = LH.get();

	public static final int DEFAULT_COMPRESSION_LEVEL = 1024;

	private int compressionLevel;
	private int compressionMinSize;

	public PortalHttpHandler(PortalHttpStateCreator stateCreator, int compressionLevel, int compressionMinSize) {
		super(stateCreator);
		this.compressionLevel = compressionLevel;
		this.compressionMinSize = compressionMinSize;
	}
	public PortalHttpHandler(PortalHttpStateCreator stateCreator) {
		this(stateCreator, Deflater.DEFAULT_COMPRESSION, DEFAULT_COMPRESSION_LEVEL);
	}

	private boolean debug = false;

	private boolean needsLogin = true;

	@Override
	public void handle(HttpRequestResponse req) throws IOException {
		HttpSession session = req.getSession(false);
		if (session == null) {
			LH.fine(log, "No Session for uri=", req.getRequestUri(), ", so redirectToLogin" + " " + req.getParams());
			send(req, null, "redirectToLogin();", false);
			return;
		}
		try {
			super.handle(req);
		} catch (Exception e) {
			LH.warning(log, "For Session uri=", req.getRequestUri(), " error: ", e);
			send(req, null, "redirectToLogin();", false);
		}
	}

	@Override
	public Object handle(HttpRequestAction action, WebState state) {
		WebStatesManager wsm = state.getWebStatesManager();
		if (debug) {
			String s = (action.getRequest().getRequestContentLength() > 0)
					? new String(action.getRequest().getRequestContentBuffer(), 0, action.getRequest().getRequestContentLength())
					: action.getRequest().getQueryString();
			if (!SH.startsWith(s, "type=polling") || s.indexOf('\n') != -1)
				LH.info(log, state == null || wsm.getUser() == null ? null : wsm.getUser().getUserName(), ": ", s);
		}
		HttpSession session = wsm.getSession();
		long now = getTools().getNow();
		if (session.hasExpired(now) || !session.isAlive()) {
			LH.info(log, "Session expired: " + state.describeUser());
			try {
				session.kill();
			} catch (Exception e) {
				LH.warning(log, "Error closing porlet manager for ", state.describeUser(), e);
			}
			state.removeMeFromManager();
		}
		state.touch(now);
		if (!wsm.isLoggedIn()) {
			if (isNeedsLogin()) {
				LH.fine(log, "Not logged in for uri=", action.getRequest().getRequestUri(), ", user=", state.describeUser(), " so redirectToLogin");
				return send(action.getRequest(), state, "redirectToLogin();", false);
			}
		}
		PortletManager portletManager = state.getPortletManager();
		if (portletManager == null) {
			LH.fine(log, "No portletManager for uri=", action.getRequest().getRequestUri(), ", user=", state.describeUser(), " so redirectToLogin");
			return send(action.getRequest(), state, "redirectToLogin();", false);
		}
		return handle(portletManager, action);
	}
	private Object handle(PortletManager portletManager, HttpRequestAction action) {
		final HttpRequestResponse request = action.getRequest();
		final StringBuilder t = new StringBuilder();
		if (request.getIsMultipart()) {
			Map<String, String> params = new HashMap<String, String>(request.getParams());
			try {
				String type = params.get("type");
				if ("drop".equals(type))
					params.put("type", "onchange");
				HttpMultiPart value = (HttpMultiPart) request.getParamAsList("fileData").get(0);
				if (OH.ne("text/plain", value.getContentType())) {
					((Map) params).put("fileData64", value.getData());//HACK ATTACK
					params.remove("fileData");
				}
				params.put("fileType", value.getContentType());
				params.put("fileName", value.getFileName());
				portletManager.setCurrentAction(request);
				portletManager.handleCallback(params, action);
			} catch (Exception e) {
				handleGeneralError(portletManager, action, t, params, e);
			} finally {
				portletManager.setCurrentAction(null);
			}
			LH.fine(log, "received multipart, not draining js this round");
			return null;
		}
		try {
			boolean drain = false;
			if (request.getRequestContentLength() == 0)
				drain = processParams(request.getParams(), action, portletManager, 1);
			else {
				request.getRequestContentBuffer(t);
				String[] parts = SH.split('\n', t);
				if (parts.length > 1 && SH.count("__CONFLATE=", t, true) > 1) {//we need at least 2 __CONFLATE entries to even consider conflation
					t.setLength(0);
					final Map<String, String> params[] = new HashMap[parts.length];
					Set<Tuple3<String, String, String>> toConflate = new HashSet<Tuple3<String, String, String>>();
					for (int i = parts.length - 1; i >= 0; i--) {//work from the end to the front
						final String part = parts[i];
						final HashMap<String, String> p = new HashMap<String, String>();
						BasicHttpRequestResponse.parseParams(part, 0, part.length(), p, null, t);
						final String conflateKey = p.get("__CONFLATE");
						if (conflateKey != null && !toConflate.add(new Tuple3<String, String, String>(conflateKey, p.get("type"), p.get("portletId"))))
							continue;//we already have a conflated message for this type/portletId so skip me
						params[i] = p;
					}
					for (Map<String, String> p : params)
						if (p != null)
							drain = processParams(p, action, portletManager, parts.length) || drain;
				} else {
					t.setLength(0);
					final Map<String, String> params = new HashMap<String, String>();
					for (String part : parts) {
						params.clear();
						BasicHttpRequestResponse.parseParams(part, 0, part.length(), params, null, t);
						drain = processParams(params, action, portletManager, parts.length) || drain;
					}
				}
			}
			t.setLength(0);
			if (drain)
				portletManager.drainPendingJs(t);
			logRequest(portletManager, action, t);
			return send(request, portletManager.getState(), t, true);
		} catch (Exception e) {
			portletManager.drainPendingJs(t);
			handleGeneralError(portletManager, action, t, request.getParams(), e);
			return null;
		} finally {
			portletManager.setCurrentAction(null);
		}
	}
	private void handleGeneralError(PortletManager portletManager, HttpRequestAction action, final StringBuilder t, Map<String, String> params, Exception e) {
		String ticket = getTools().generateErrorTicket();
		LH.warning(log, ticket, ": An Error occurred from session: ", portletManager.getState().getPartitionId(), " for user: ", getUserName(portletManager), e);
		LH.warning(log, "The following javascript was dropped for user: ", getUserName(portletManager), " for action: ", action, " and params: ", params, ": ", t);
		try {
			portletManager.handlGeneralError(ticket, e);
		} catch (Throwable th) {
			LH.severe(log, "Error handling error for user: ", getUserName(portletManager), " for action: ", action, " and params: ", params, ": ", t, th);
		}

	}

	private boolean processParams(Map<String, String> params, HttpRequestAction action, PortletManager portletManager, int partsLength) throws IOException {
		HttpRequestResponse request = action.getRequest();
		String downloadFile = params.get("downloadFile");
		if (downloadFile == null) {
			portletManager.setCurrentAction(request);
			portletManager.handleCallback(params, action);
			return true;
		} else if ("true".equals(downloadFile)) {
			if (partsLength > 1)
				throw new RuntimeException("pending download request should not have multiple lines: " + partsLength);
			PortletDownload download = portletManager.popPendingDownload();
			try {
				HttpUtils.respondWithFile(download.getName(), download.getData(), request);
				return false;
			} catch (Exception e) {
				throw OH.toRuntime(e);
			}
		} else if ("content".equals(downloadFile)) {
			portletManager.setCurrentAction(request);
			PortletDownload download = portletManager.handleContentRequest(params, action);
			if (download == null)
				send(request, portletManager.getState(), "Download failed", false);
			else
				HttpUtils.respondWithFile(download.getName(), download.getData(), request);
			return false;
		} else
			throw new RuntimeException("Invalid value for downloadFile");

	}
	private Object send(HttpRequestResponse request, WebState state, CharSequence string, boolean allowCompression) {
		if (debug) {
			if (SH.count('\n', string) > 3) {
				HttpSession session = request.getSession(false);
				String sessionId = (String) (session == null ? null : session.getSessionId());
				LH.info(log, state == null ? null : state.getUserName(), "@", sessionId, ": ", string);
			}
		}
		if (allowCompression == false || string.length() < this.compressionMinSize) {
			request.getOutputStream().append(string);
			request.setContentType("text/plain");
			return null;
		} else
			return new NeedsCompression(string.toString());

	}
	private void logRequest(PortletManager portletManager, HttpRequestAction action, StringBuilder sb) {
		if (log.isLoggable(Level.FINE) && (sb.length() > 110)) {
			StringBuilder out = new StringBuilder();
			out.append("For Session ").append(portletManager.getState().getPartitionId());
			out.append(" User: ").append(getUserName(portletManager)).append(SH.NEWLINE);
			out.append("----- Request -----").append(SH.NEWLINE);
			out.append(action.getRequest().getRequestUrl()).append(SH.NEWLINE);
			out.append("----- Portlet Callback -----").append(SH.NEWLINE);
			out.append("(").append(sb.length()).append(" byte payload)").append(SH.NEWLINE);
			out.append(sb);
			LH.fine(log, out.toString());
		}
	}

	public boolean isNeedsLogin() {
		return needsLogin;
	}
	public void setNeedsLogin(boolean needsLogin) {
		this.needsLogin = needsLogin;
	}
	@Override
	public void onNewSession(HttpSessionManager manager, HttpSession session) {
		assertInit();
		LH.info(log, "Session Created: ", session.getSessionId());
	}
	@Override
	public void onSessionClosed(HttpSessionManager manager, HttpSession session) {

		LH.info(log, "Session Closed: ", session.getSessionId());
		final WebStatesManager wsm = WebStatesManager.get(session);
		try {
			if (wsm != null)
				wsm.onHttpSessionClosed();
		} catch (Exception e) {
			LH.warning(log, e);
		}
		if (wsm != null)
			for (String s : CH.l(wsm.getPgIds())) {
				WebState ws = wsm.getState(s);
				if (ws == null || !ws.isAlive())
					continue;
				ws.removeMeFromManager();
				Partition partition = ws.getPartition();
				if (partition != null) {
					int timeout = getStateTimeoutSeconds();
					if (!partition.lockForWrite(timeout, TimeUnit.SECONDS)) {
						LH.info(log, "Session timeout for closing(", timeout, " seconds) for session: ", session.getSessionId());
					} else {
						try {
							ws.killWebState();
						} finally {
							partition.unlockForWrite();
						}
					}
				}
			}
	}
	@Override
	public void onSessionExpired(HttpSessionManager manager, HttpSession session) {
		LH.info(log, "Session Expired: ", session.getSessionId());
	}
	public boolean isDebug() {
		return debug;
	}
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public static class Compressor extends GZIPOutputStream {
		private final static int GZIP_MAGIC = 0x8b1f;
		private final static int TRAILER_SIZE = 8;
		private final static byte[] header = { (byte) GZIP_MAGIC, // Magic number (short)
				(byte) (GZIP_MAGIC >> 8), // Magic number (short)
				Deflater.DEFLATED, // Compression method (CM)
				0, // Flags (FLG)
				0, // Modification time MTIME (int)
				0, // Modification time MTIME (int)
				0, // Modification time MTIME (int)
				0, // Modification time MTIME (int)
				0, // Extra flags (XFLG)
				0 // Operating system (OS)
		};

		private final FastByteArrayOutputStream buf;
		private int level;

		public Compressor(int level) throws IOException {
			super(new FastByteArrayOutputStream(), 4096);
			if (level == Deflater.DEFAULT_COMPRESSION)
				level = 6;
			this.level = level;
			this.def = new Deflater(level, true);
			this.buf = (FastByteArrayOutputStream) this.out;
		}
		synchronized public void compress(CharSequence string, FastPrintStream sink) throws IOException {
			long start = System.nanoTime();
			super.crc.reset();
			super.def.reset();
			this.buf.reset();
			this.buf.write(header);
			SH.writeUTF(string, this);
			super.finish();
			int count = this.buf.getCount();
			writeTrailer(trailer, 0);
			this.buf.write(trailer);
			sink.write(this.buf.getBuffer(), 0, count);
			long end = System.nanoTime();
			if (end - start > 10000000)
				LH.info(log, "Compressed at Level ", this.level, ": ", string.length(), "==>", count, " (%", (100 * count / string.length()), ") in ", (end - start) / 1000000,
						" millis");
			this.buf.reset(4096);
		}

		byte[] trailer = new byte[TRAILER_SIZE];

		private void writeTrailer(byte[] buf, int offset) throws IOException {
			writeInt((int) crc.getValue(), buf, offset); // CRC-32 of uncompr. data
			writeInt(def.getTotalIn(), buf, offset + 4); // Number of uncompr. bytes
		}

		/*
		 * Writes integer in Intel byte order to a byte array, starting at a
		 * given offset.
		 */
		private void writeInt(int i, byte[] buf, int offset) throws IOException {
			writeShort(i & 0xffff, buf, offset);
			writeShort((i >> 16) & 0xffff, buf, offset + 2);
		}

		/*
		 * Writes short integer in Intel byte order to a byte array, starting
		 * at a given offset
		 */
		private void writeShort(int s, byte[] buf, int offset) throws IOException {
			buf[offset] = (byte) (s & 0xff);
			buf[offset + 1] = (byte) ((s >> 8) & 0xff);
		}
	}

	private final ThreadLocal<Compressor> COMPRESSOR_FACTORY = new ThreadLocal<Compressor>() {
		@Override
		protected Compressor initialValue() {
			try {
				return new Compressor(compressionLevel);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	};

	static public class NeedsCompression {

		private String data;

		public NeedsCompression(String data) {
			this.data = data;
		}

	}

	@Override
	public void handleAfterUnlock(HttpRequestResponse req, Object data) {
		if (data instanceof NeedsCompression) {
			String text = ((NeedsCompression) data).data;
			try {
				COMPRESSOR_FACTORY.get().compress(text, req.getOutputStream());
			} catch (IOException e) {
				throw new RuntimeException("Critical Error, compressing failed", e);
			}
		}
	}
	private String getUserName(PortletManager pm) {
		return pm == null ? null : pm.getUserName();
	}

	@Override
	public void onSessionReaperRan(HttpSessionManager manager, long now) {
		for (HttpSession session : manager.getSessions()) {
			final long timeout = session.getTimeout();
			if (timeout == -1)
				continue;
			WebStatesManager wsm = WebStatesManager.get(session);
			if (wsm == null)
				continue;
			for (String s : wsm.getPgIds()) {
				WebState ws = wsm.getState(s);
				if (ws == null)
					continue;
				if (ws.getLastAccess() + timeout < now) {
					LH.info(log, "Closing timedout session for user '", ws.getUserName(), "': " + ws.getSessionId());
					ws.getPortletManager().close();
				}
			}
		}
	}
}
