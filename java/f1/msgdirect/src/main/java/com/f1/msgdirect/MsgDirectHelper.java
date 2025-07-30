package com.f1.msgdirect;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.SH;

public class MsgDirectHelper {
	private static final String NEW_NORMAL_THREAD = "MSGDIRECT_NEW_NORMAL_THREAD";
	private static final String NEW_DEAMON_THREAD = "MSGDIRECT_NEW_DEAMON_THREAD";
	private static final Logger log = LH.get();
	public static final int MAX_MISSED_KEEP_ALIVES = 200;

	public static final String NEW_THREAD = "MSGDIRECT_NEW_THREAD";
	public static final String HANDSHAKE_ERROR = "MSGDIRECT_CONNECTION_HANDSHAKE_ERROR";
	public static final String CONNECT_FAILED = "MSGDIRECT_CONNECTION_FAILED";
	public static final String CONNECTED = "MSGDIRECT_CONNECTION_ESTABLISHED";
	public static final String CLOSED_ERROR_READING = "MSGDIRECT_CONNECTION_CLOSED_ERROR_READING";
	public static final String CLOSED_REMOTELY_EOF = "MSGDIRECT_CONNECTION_CLOSED_REMOTELY_EOF";
	public static final String CLOSED_REMOTELY = "MSGDIRECT_CONNECTION_CLOSED_REMOTELY";
	public static final String CLOSED_KEEP_ALIVE_FAILED = "MSGDIRECT_CONNECTION_CLOSED_KEEP_ALIVE_FAILED";
	public static final String CLOSED_KEEP_ALIVES_MISSED = "MSGDIRECT_CONNECTION_CLOSED_KEEP_ALIVES_MISSED";
	public static final String CLOSED_REMOTELY_SSL = "MSGDIRECT_CONNECTION_CLOSED_REMOTELY_BAD_SSL";
	public static final String CLOSED_SEND_MESSAGE_ERROR = "MSGDIRECT_CONNECTION_CLOSED_SEND_MESSAGE_ERROR";
	public static final String CONFIG_ERROR = "MSGDIRECT_CONNECTION_CONFIG_ERROR";

	public static final String THREAD_PROCESS_MESSAGES = "MSGDIRECT_PROC";
	public static final String THREAD_RECONNECT = "MSGDIRECT_RECN";
	public static final String THREAD_LISTENING = "MSGDIRECT_LSTN";

	public static final Object SERVER_LISTENING_OPEN = "MSGDIRECT_SERVER_OPEN";
	public static final Object SERVER_LISTENING_LISTEN = "MSGDIRECT_SERVER_LISTENING";
	public static final String SERVER_LISTENING_FAILED = "MSGDIRECT_SERVER_FAILED";
	public static final String SERVER_ACCEPTED_CONNECTION = "MSGDIRECT_SERVER_ACCEPT";

	private static final AtomicInteger THREAD_ID = new AtomicInteger(1);

	public static Thread newThread(Runnable runnable, String reason, String name, boolean isDeamon) {
		StringBuilder threadName = new StringBuilder(reason);
		int num = THREAD_ID.getAndIncrement();
		threadName.append("-");
		SH.repeat('0', 3 - MH.getDigitsCount(num, 10), threadName);
		SH.toString(num, 10, threadName);
		if (isDeamon)
			LH.fine(log, NEW_DEAMON_THREAD, ": ", threadName, " for ", name);
		else
			LH.fine(log, NEW_NORMAL_THREAD, ": ", threadName, " for ", name);
		Thread r = new Thread(runnable, threadName.toString());
		r.setDaemon(isDeamon);
		r.start();
		return r;

	}
}
