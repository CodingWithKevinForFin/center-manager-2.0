/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import com.f1.speedlogger.SpeedLogger;
import com.f1.speedlogger.SpeedLogger2Streams;
import com.f1.speedlogger.SpeedLoggerAppender;
import com.f1.speedlogger.SpeedLoggerAppenderFactory;
import com.f1.speedlogger.SpeedLoggerEventListener;
import com.f1.speedlogger.SpeedLoggerManager;
import com.f1.speedlogger.SpeedLoggerManagerListener;
import com.f1.speedlogger.SpeedLoggerSink;
import com.f1.speedlogger.SpeedLoggerSinkFactory;
import com.f1.speedlogger.SpeedLoggerStream;
import com.f1.speedlogger.StackTraceFormatter;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.F1GlobalProperties;
import com.f1.utils.ToDoException;

public class BasicSpeedLoggerManager implements SpeedLoggerManager {

	final private long uid = SpeedLoggerUtils.generateUid();
	public static final String PROPERTY_PREINIT_FORMAT = F1GlobalProperties.getSysPropertyPrefix() + "speedlogger.manager.preinit.format";
	public static final String PROPERTY_PREINIT_FORMAT_DEFAULT = "F1 Speedlogger PREINIT: %m %D%n";

	public static final String PROPERTY_PREINIT_SINK = F1GlobalProperties.getSysPropertyPrefix() + "speedlogger.manager.preinit.sink";
	public static final String PROPERTY_PREINIT_SINK_DEFAULT = "stdout";

	private Map<String, SpeedLogger2Streams> logger2streams = new ConcurrentHashMap<String, SpeedLogger2Streams>();
	private Map<String, List<SpeedLoggerStream>> streams = new HashMap<String, List<SpeedLoggerStream>>();
	private Map<String, SpeedLoggerSinkFactory> sinkFactories = new HashMap<String, SpeedLoggerSinkFactory>();
	private Map<String, SpeedLoggerSink> sinks = new HashMap<String, SpeedLoggerSink>();

	private Map<String, SpeedLoggerAppenderFactory> appenderFactories = new HashMap<String, SpeedLoggerAppenderFactory>();
	private Map<String, SpeedLoggerAppender> appenders = new HashMap<String, SpeedLoggerAppender>();
	private boolean running = false;
	final private char preinitOut;

	public BasicSpeedLoggerManager() {
		addAppenderFactory(new BasicSpeedLoggerAppenderFactory(this));
		addSinkFactory(new ConsoleSpeedLoggerSinkFactory("stdout", System.out));
		addSinkFactory(new ConsoleSpeedLoggerSinkFactory("stderr", System.err));
		addSinkFactory(new FileSpeedLoggerSinkFactory());
		String preinitOut = System.getProperty(PROPERTY_PREINIT_SINK, PROPERTY_PREINIT_SINK_DEFAULT);
		if ("stdout".equals(preinitOut))
			this.preinitOut = 'o';
		else if ("stderr".equals(preinitOut))
			this.preinitOut = 'e';
		else if ("off".equals(preinitOut))
			this.preinitOut = ' ';
		else
			throw new RuntimeException("invalid value for " + PROPERTY_PREINIT_SINK + "(should be stdout, stderr or off): " + preinitOut);
	}

	@Override
	public void addAppenderFactory(SpeedLoggerAppenderFactory appenderFactory) {
		CH.putOrThrow(appenderFactories, appenderFactory.getId(), appenderFactory);
	}

	@Override
	public void addSinkFactory(SpeedLoggerSinkFactory sinkFactory) {
		CH.putOrThrow(sinkFactories, sinkFactory.getId(), sinkFactory);
	}

	@Override
	public BasicSpeedLoggerStream addStream(String appenderId, String sinkId, int level, String id) {
		SpeedLoggerAppender appender = getAppender(appenderId);
		SpeedLoggerSink sink = getSink(sinkId);
		BasicSpeedLoggerStream r = new BasicSpeedLoggerStream(id, this, appender, sink, level);
		addStream(r);
		return r;
	}

	@Override
	public synchronized void addStream(SpeedLoggerStream r) {
		System.out.println("F1 SpeedLogger Stream: '" + r.getId() + "' ==> " + r.describe());
		String id = r.getId();
		List<SpeedLoggerStream> l = streams.get(id);
		if (l == null)
			streams.put(id, l = new ArrayList<SpeedLoggerStream>());
		l.add(r);
		for (Map.Entry<String, SpeedLogger2Streams> e : logger2streams.entrySet())
			if (e.getKey().startsWith(r.getId()))
				e.getValue().addStreams(Collections.singletonList((SpeedLoggerStream) r));
	}
	@Override
	public SpeedLoggerAppender getAppender(String id) {
		return SpeedLoggerUtils.getOrThrow(appenders, id, "appender");
	}

	@Override
	public SpeedLoggerAppenderFactory getAppenderFactory(String id) {
		return appenderFactories.get(id);
	}

	@Override
	public Set<String> getAppenderFactoryIds() {
		return appenderFactories.keySet();
	}

	@Override
	public Set<String> getAppenderIds() {
		return appenders.keySet();
	}

	@Override
	public SpeedLogger getLogger(String id) {
		SpeedLogger2Streams r = logger2streams.get(id);
		if (r == null) {
			synchronized (this) {
				r = logger2streams.get(id);
				if (r != null)
					return r;
				Map<String, SpeedLoggerStream> found = new HashMap<String, SpeedLoggerStream>();
				for (List<SpeedLoggerStream> l : streams.values())
					for (SpeedLoggerStream c : l)
						if (id.startsWith(c.getId())) {
							SpeedLoggerStream current = found.get(c.getSinkId());
							if (current == null || c.getId().length() > current.getId().length())
								found.put(c.getSinkId(), c);
						}
				r = new BasicSpeedLogger(id, this);
				r.addStreams(found.values());

				for (SpeedLoggerManagerListener listener : managerListeners)
					listener.onNewLogger(r);
				logger2streams.put(id, r);

			}
		}
		return r;
	}

	@Override
	public SpeedLoggerSink getSink(String id) {
		return SpeedLoggerUtils.getOrThrow(sinks, id, "sinks");
	}

	@Override
	public SpeedLoggerSinkFactory getSinkFactory(String id) {
		return sinkFactories.get(id);
	}

	@Override
	public Set<String> getSinkFactoryIds() {
		return sinkFactories.keySet();
	}

	@Override
	public Set<String> getSinkIds() {
		return sinks.keySet();
	}

	@Override
	public Set<String> getStreamIds() {
		return new HashSet<String>(streams.keySet());
	}

	@Override
	public Collection<SpeedLoggerStream> getStream(String streamId) {
		List<SpeedLoggerStream> r = streams.get(streamId);
		return (r == null) ? Collections.EMPTY_LIST : new ArrayList<SpeedLoggerStream>(r);
	}

	@Override
	public void start() {
		if (running)
			throw new IllegalStateException("start already called");
		running = true;
	}

	@Override
	public boolean isStarted() {
		return running;
	}

	@Override
	public SpeedLoggerAppender newAppender(String type, String id, Map<String, String> configuration) {
		SpeedLoggerAppenderFactory f = SpeedLoggerUtils.getOrThrow(appenderFactories, type, "appender factory");
		if (appenders.containsKey(id)) {
			System.err.println("Overriding appender: " + id);
		}
		Map<String, String> config = new HashMap<String, String>(f.getConfiguration());
		for (Map.Entry<String, String> e : configuration.entrySet()) {
			if (config.put(e.getKey(), e.getValue()) == null)
				throw new RuntimeException("invalid option: " + e.getKey() + " options include: " + f.getConfiguration().keySet());
			config.put(e.getKey(), e.getValue());
		}
		SpeedLoggerAppender r;
		try {
			r = f.createAppender(id, config);
		} catch (Exception e) {
			throw new RuntimeException("Error creating appender '" + id + "' with options: " + config, e);
		}
		appenders.put(r.getId(), r);
		return r;
	}

	@Override
	public SpeedLoggerSink newSink(String type, String id, Map<String, String> configuration) {
		if (sinks.containsKey(id))
			throw new RuntimeException("sink already exists: " + id);
		SpeedLoggerSinkFactory f = SpeedLoggerUtils.getOrThrow(sinkFactories, type, "sink type ");
		Map<String, String> config = new HashMap<String, String>(f.getConfiguration());
		for (Map.Entry<String, String> e : configuration.entrySet())
			if (config.put(e.getKey(), e.getValue()) == null)
				throw new RuntimeException("invalid option: " + e.getKey() + " options include: " + f.getConfiguration());
		SpeedLoggerSink r;
		try {
			r = f.createSink(id, config);
		} catch (Exception e) {
			throw new RuntimeException("Error creating sink '" + id + "' with options:" + config, e);
		}
		sinks.put(r.getId(), r);
		for (SpeedLoggerManagerListener listener : managerListeners)
			listener.onNewSink(r);
		return r;
	}

	@Override
	public void prelog(SpeedLogger logger, int level, Object msg) {
		try {
			BufferedWriter err = new BufferedWriter(1024);
			preAppender.append(err, msg, level, logger, preAppender.getRequiresTimeMs() ? EH.currentTimeMillis() : 0,
					preAppender.getRequiresStackTrace() ? BasicSpeedLogger.getStackTraceElement() : null);
			switch (preinitOut) {
				case 'o':
					EH.toStdout(err.toString(), false);
					break;
				case 'e':
					EH.toStderr(err.toString(), false);
					break;
			}
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

	private SpeedLoggerAppender preAppender = new BasicSpeedLoggerAppender("", System.getProperty(PROPERTY_PREINIT_FORMAT, PROPERTY_PREINIT_FORMAT_DEFAULT), TimeZone.getDefault(),
			new BasicStackTraceFormatter(), BasicSpeedLoggerAppender.DEFAULT_MAXLENGTH, null, null);
	private StackTraceFormatter defaultStackTraceFormatter = new BasicStackTraceFormatter();
	private SpeedLoggerManagerListener[] managerListeners = new SpeedLoggerManagerListener[0];

	@Override
	public Set<String> getLoggerIds() {
		return this.logger2streams.keySet();
	}

	@Override
	synchronized public void removeStream(String streamId, String sinkId) {
		List<SpeedLoggerStream> streams = this.streams.get(streamId);
		if (CH.isEmpty(streams))
			return;
		List<SpeedLoggerStream> toRemove = new ArrayList<SpeedLoggerStream>();
		for (SpeedLoggerStream i : streams) {
			if (sinkId.equals(i.getSinkId()))
				toRemove.add(i);
		}
		streams.removeAll(toRemove);
		throw new ToDoException();
	}

	@Override
	public void setDefaultStackTraceFormatter(StackTraceFormatter defaultStackTraceFormatter) {
		this.defaultStackTraceFormatter = defaultStackTraceFormatter;
	}

	@Override
	public StackTraceFormatter getDefaultStackTraceFormatter() {
		return defaultStackTraceFormatter;
	}

	@Override
	public long getUid() {
		return uid;
	}

	@Override
	public void addSpeedLoggerManagerListener(SpeedLoggerManagerListener listener) {
		managerListeners = AH.append(managerListeners, listener);
	}

	@Override
	public SpeedLoggerManagerListener[] getManagerListeners() {
		return managerListeners;
	}

	private SpeedLoggerEventListener[] listeners = SpeedLoggerUtils.EMPTY_LISTENER_ARRAY;

	@Override
	public void addSpeedLoggerEventListener(SpeedLoggerEventListener listener) {
		listeners = AH.append(listeners, listener);
	}

	@Override
	public SpeedLoggerEventListener[] getListeners() {
		return listeners;
	}
}
