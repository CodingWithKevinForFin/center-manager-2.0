package com.f1.bootstrap.appmonitor;

import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.povo.f1app.F1AppThreadScope;
import com.f1.povo.f1app.reqres.F1AppInterruptThreadRequest;
import com.f1.povo.f1app.reqres.F1AppResponse;

public class AppMonitorInterruptCommandProcessor extends BasicRequestProcessor<F1AppInterruptThreadRequest, AppMonitorState, F1AppResponse> {

	public AppMonitorInterruptCommandProcessor() {
		super(F1AppInterruptThreadRequest.class, AppMonitorState.class, F1AppResponse.class);
	}

	@Override
	protected F1AppResponse processRequest(RequestMessage<F1AppInterruptThreadRequest> action, AppMonitorState state, ThreadScope threadScope) throws Exception {
		F1AppResponse r = nw(F1AppResponse.class);
		F1AppInterruptThreadRequest req = action.getAction();

		try {
			AppMonitorThreadscopeListener listener = null;
			for (AppMonitorThreadscopeListener tsl : state.getListeners(AppMonitorThreadscopeListener.class)) {
				F1AppThreadScope ao = tsl.getAgentObject();
				if (ao != null && ao.getId() == req.getThreadMonitorId()) {
					listener = tsl;
				}
			}
			if (listener == null) {
				r.setMessage("Thread not available for object:" + req.getThreadMonitorId());
				return r;
			}
			ThreadScope thread = listener.getObject();
			F1AppThreadScope f1Thread = listener.getAgentObject();
			if (f1Thread.getProcessStats() != req.getProcessedEventsCount()) {
				r.setMessage("Thread has already completed processed command #" + req.getProcessedEventsCount() + ", current at: " + f1Thread.getProcessStats());
				return r;
			}
			if (f1Thread.getCurrentProcessorId() != req.getProcessorMonitorId()) {
				return r;
			}
			thread.interrupt();
			r.setMessage("Thread interrupted: " + thread.getName());
			r.setOk(true);
			return r;
		} catch (Exception e) {
			r.setMessage("General error: " + e);
			return r;
		}

	}
}
