package com.f1.fixclientsim;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import quickfix.Message;

import com.f1.utils.LH;

public class SwingSafeFixClientSimListener implements FixClientSimListener {
	private static final Logger log = Logger.getLogger(SwingSafeFixClientSimListener.class.getName());

	final public static byte ON_MESSAGE = 1;
	final public static byte ON_LOGON = 2;
	final public static byte ON_LOGOUT = 3;
	final public static byte ON_ADMIN_MESSAGE = 4;
	final public static byte ON_TEXT = 5;
	private FixClientSimListener inner;
	private AtomicInteger count = new AtomicInteger();

	public SwingSafeFixClientSimListener(FixClientSimListener inner) {
		this.inner = inner;
	}

	@Override
	public void onMessage(FixClientSimSession session, Message message) {
		if (!maxMessagesExceeded(session))
			SwingUtilities.invokeLater(new Runner(ON_MESSAGE, session, message));
	}

	@Override
	public void onLogon(FixClientSimSession session) {
		SwingUtilities.invokeLater(new Runner(ON_LOGON, session, null));

	}

	@Override
	public void onLogout(FixClientSimSession session) {
		SwingUtilities.invokeLater(new Runner(ON_LOGOUT, session, null));

	}

	@Override
	public void onAdminMessage(FixClientSimSession session, Message message) {

		SwingUtilities.invokeLater(new Runner(ON_ADMIN_MESSAGE, session, message));
	}

	private boolean maxMessagesExceeded(FixClientSimSession session) {
		if (count.incrementAndGet() < 1000)
			return false;
		if (count.get() % 1000 == 0)
			SwingUtilities.invokeLater(new Runner("Received " + count.get() + " messages (no longer updating)"));
		return true;
	}

	private class Runner implements Runnable {
		final private FixClientSimSession session;
		final private Message message;
		final private byte type;
		private String text;

		private Runner(byte type, FixClientSimSession session, Message message) {
			this.session = session;
			this.message = message;
			this.type = type;
		}

		public Runner(String text) {
			this(ON_TEXT, null, null);
			this.text = text;
		}

		@Override
		public void run() {
			try {
				switch (type) {
					case ON_TEXT:
						inner.onText(text);
						break;
					case ON_MESSAGE:
						inner.onMessage(session, message);
						break;
					case ON_LOGON:
						inner.onLogon(session);
						break;
					case ON_LOGOUT:
						inner.onLogout(session);
						break;
					case ON_ADMIN_MESSAGE:
						inner.onAdminMessage(session, message);
						break;
					default:
						throw new RuntimeException("unkown type: " + type);
				}
			} catch (Exception e) {
				LH.warning(log, "Error for ", type, ": ", session, " , ", message, e);
			}
		}
	}

	@Override
	public void onText(String text) {
		SwingUtilities.invokeLater(new Runner(text));
	}

}
