package com.f1.bootstrap.appmonitor;

import java.util.Collection;
import java.util.Set;

import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.povo.f1app.reqres.F1AppChangeLogLevelRequest;
import com.f1.povo.f1app.reqres.F1AppChangeLogLevelResponse;
import com.f1.povo.f1app.reqres.F1AppResponse;
import com.f1.speedlogger.SpeedLoggerAppender;
import com.f1.speedlogger.SpeedLoggerManager;
import com.f1.speedlogger.SpeedLoggerSink;
import com.f1.speedlogger.SpeedLoggerStream;
import com.f1.speedlogger.impl.BasicSpeedLogger;
import com.f1.speedlogger.impl.BasicSpeedLoggerStream;
import com.f1.utils.CH;
import com.f1.utils.OH;

public class AppMonitorChangeLogLevelProcessor extends BasicRequestProcessor<F1AppChangeLogLevelRequest, AppMonitorState, F1AppResponse> {

	public AppMonitorChangeLogLevelProcessor() {
		super(F1AppChangeLogLevelRequest.class, AppMonitorState.class, F1AppResponse.class);
	}

	@Override
	protected F1AppResponse processRequest(RequestMessage<F1AppChangeLogLevelRequest> action, AppMonitorState state, ThreadScope threadScope) throws Exception {
		F1AppResponse r = nw(F1AppChangeLogLevelResponse.class);
		AppMonitorContainer amc = (AppMonitorContainer) getContainer();

		final int levelInt = action.getAction().getLevel();
		try {
			for (SpeedLoggerManager manager : amc.getManagersListener().getSpeedLoggerManagers()) {
				Set<String> appenderIds = manager.getAppenderIds();
				final SpeedLoggerAppender defaultAppender = manager.getAppender(CH.first(appenderIds));
				for (String sinkId : action.getAction().getSinkIds()) {
					if (!manager.getSinkIds().contains(sinkId))
						continue;
					SpeedLoggerSink sink = manager.getSink(sinkId);
					for (String loggerId : action.getAction().getLoggerIds()) {
						BasicSpeedLogger logger = (BasicSpeedLogger) manager.getLogger(loggerId);
						Collection<SpeedLoggerStream> existingStreams = logger.getStreams();
						SpeedLoggerAppender appender = defaultAppender;
						for (SpeedLoggerStream stream : existingStreams) {
							if (OH.eq(sinkId, stream.getSinkId())) {
								appender = manager.getAppender(stream.getAppenderId());
								break;
							}
						}
						synchronized (logger) {
							SpeedLoggerStream stream = new BasicSpeedLoggerStream(loggerId, manager, appender, sink, levelInt);
							logger.addStreams(CH.l(stream));
						}
					}
				}

			}
			r.setOk(true);
		} catch (Exception e) {
			r.setMessage("General error: " + e);
			r.setOk(false);
		}
		return r;
	}
}
