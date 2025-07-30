package com.f1.ami.plugins.bloomberg;

import java.util.concurrent.ConcurrentHashMap;

import com.bloomberglp.blpapi.Session;
import com.f1.utils.structs.Tuple2;

public class ConnectionPool {
	private static final ConcurrentHashMap<Tuple2<String, String>, Session> activeConnections = new ConcurrentHashMap<Tuple2<String, String>, Session>();

	public static ConcurrentHashMap<Tuple2<String, String>, Session> getActiveConnections() {
		return activeConnections;
	}

	public static Session getSession(Tuple2<String, String> userDetails) {
		return activeConnections.get(userDetails);
	}

	public static void addConnection(Tuple2<String, String> userDetails, Session s) {
		activeConnections.putIfAbsent(userDetails, s);
	}

	public static void removeConnection(Tuple2<String, String> userDetails) {
		if (activeConnections.containsKey(userDetails))
			activeConnections.remove(userDetails);
	}

}
