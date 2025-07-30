package com.f1.ami.center.table;

import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiFlowControlPause;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsResponse;
import com.f1.ami.amicommon.msg.AmiCenterRequest;
import com.f1.ami.center.AbstractAmiCenterItinerary;
import com.f1.ami.center.AmiCenterItinerary;
import com.f1.ami.center.AmiCenterItineraryProcessor;
import com.f1.ami.center.AmiCenterItineraryWorker;
import com.f1.ami.center.AmiCenterQueryDsToItineraryProcessor;
import com.f1.ami.center.AmiCenterState;
import com.f1.ami.center.hdb.AmiHdbSqlFlowControl;
import com.f1.ami.center.hdb.events.AmiHdbRequest;
import com.f1.ami.center.hdb.events.AmiHdbResponse;
import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.ToDoException;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.stack.CalcFrameStack;

// This is used when script should be auto executed, meaning the calling code should not wait for a response
// An example is in a trigger
public class AmiCenterAutoExecuteItinerary extends AbstractAmiCenterItinerary<AmiCenterRequest> implements AmiCenterProcess {

	private static final Logger log = LH.get();

	public AmiCenterAutoExecuteItineraryListener autoExecuteListener;

	private FlowControlPause pause;
	final private AmiImdbSession ts;
	//	final private Map<String, Object> vars;
	//	final private SqlPlanListener listener;
	final private long startTime;
	final private long processId;
	final private long parentProcessId;
	//	final private int limit;
	//	final private TimeoutController timeout;
	final private String script;
	final private AmiCenterState state;
	final private AmiCenterItineraryProcessor itineraryProcessor;

	private CalcFrameStack stackFrame;

	//	public AmiCenterAutoExecuteItinerary(AmiCenterState state, AmiCenterItineraryProcessor itineraryProcessor, AmiCenterProcess parentProcess, String script,
	//			FlowControlPause pause, AmiImdbSession ts, Map<String, Object> vars, SqlPlanListener planListener, TimeoutController timeout, int limit) {
	public AmiCenterAutoExecuteItinerary(AmiCenterState state, AmiCenterItineraryProcessor itineraryProcessor, AmiCenterProcess parentProcess, String script,
			FlowControlPause pause, AmiImdbSession ts, CalcFrameStack sf) {
		this.parentProcessId = parentProcess.getProcessId();
		this.script = script;
		this.state = state;
		this.itineraryProcessor = itineraryProcessor;
		this.ts = ts;
		this.processId = state.createNextProcessId();
		this.startTime = System.currentTimeMillis();
		this.state.addProcess(this);
		this.stackFrame = sf;
		super.init(null, state, false);
		start(pause);
	}

	public void setListener(AmiCenterAutoExecuteItineraryListener l) {
		OH.assertNull(this.autoExecuteListener);
		this.autoExecuteListener = l;
	}

	@Override
	public byte startJourney(AmiCenterItineraryWorker worker) {
		return STATUS_ACTIVE;
	}

	@Override
	public byte onResponse(ResultMessage<?> result, AmiCenterItineraryWorker worker) {
		ts.lock(this, this.stackFrame.getSqlPlanListener());
		try {
			Object t;
			if (pause instanceof AmiFlowControlPause) {
				if (result.getAction() instanceof AmiCenterQueryDsResponse) {
					AmiCenterQueryDsResponse action = (AmiCenterQueryDsResponse) result.getAction();
					if (!action.getOk()) {
						((AmiFlowControlPause) pause).processErrorResponse(action.getException());
						//						if (this.autoExecuteListener == null)
						//							LH.info(log, "Error during auto execute: ", action.getMessage());
						//						else
						//							this.autoExecuteListener.onAutoExecuteError(this, action.getMessage(), action.getException());
						//						return STATUS_COMPLETE;
					} else
						((AmiFlowControlPause) pause).processResponse(action);
					t = pause.resume();
				} else if (result.getAction() instanceof AmiHdbResponse) {
					AmiHdbSqlFlowControl fc = ((AmiHdbSqlFlowControl) pause);
					fc.processResponse(result.getAction());
					t = pause.resume();
				} else
					throw new IllegalStateException();
			} else {
				t = null;
			}
			if (t instanceof FlowControlPause) {
				start((FlowControlPause) t);
				return STATUS_ACTIVE;
			}
			if (this.autoExecuteListener != null)
				this.autoExecuteListener.onAutoExecuteComplete(this);
		} catch (Exception e) {
			if (this.autoExecuteListener == null)
				LH.info(log, "Error during auto execute: ", e);
			else
				this.autoExecuteListener.onAutoExecuteError(this, e.getMessage(), e);
		} finally {
			ts.unlock();
		}
		state.removeProcess(this);
		return STATUS_COMPLETE;
	}

	private void start(FlowControlPause p) {
		this.pause = p;
		if (p instanceof AmiFlowControlPause) {
			AmiFlowControlPause t = (AmiFlowControlPause) p;
			AmiCenterRequest req = (AmiCenterRequest) t.toRequest(getTools());
			if (req instanceof AmiCenterQueryDsRequest) {
				AmiCenterQueryDsRequest req2 = (AmiCenterQueryDsRequest) req;
				if (req2.getDatasourceName() == null && req2.getDatasourceOverrideAdapter() == null)
					throw new ToDoException();
				req2.setInvokedBy("AUTO");//TODO: get the name somehow
				req2.setRequestTime(System.currentTimeMillis());
				req2.setParentProcessId(getProcessId());
				req2.setIsTest(false);
				req2.setQuerySessionId(0);
				req2.setQuerySessionKeepAlive(false);
				AmiCenterItinerary<AmiCenterQueryDsRequest> itinerary = AmiCenterQueryDsToItineraryProcessor.createQueryItinerary(req2);
				itinerary.init(null, this.state, false);
				itineraryProcessor.startItinerary(this, itinerary, req2);
			} else {
				AmiHdbRequest req2 = (AmiHdbRequest) req;
				AmiCenterHdbItinerary itinerary = new AmiCenterHdbItinerary();
				itinerary.init(null, this.state, false);
				itineraryProcessor.startItinerary(this, itinerary, (AmiHdbRequest) req2);
			}
			//		} else if (p instanceof AmiHdbPause) {
			//			AmiHdbRequest req2 = state.getTools().nw(AmiHdbRequest.class);
			//			req2.setSqlFlowControl(((AmiHdbPause) p).getHdbFlowControl());
			//			AmiCenterHdbItinerary itinerary = new AmiCenterHdbItinerary();
			//			itinerary.init(null, this.state, false);
			//			itineraryProcessor.startItinerary(this, itinerary, req2);
		}
	}

	@Override
	public Message endJourney(AmiCenterItineraryWorker worker) {
		return getTools().nw(Message.class);
	}

	@Override
	public String getQuery() {
		return this.script;
	}

	@Override
	public long getParentProcessId() {
		return this.parentProcessId;
	}

	@Override
	public String getDsName() {
		return "AMI";
	}

	@Override
	public String getDsRelayId() {
		return null;
	}

	@Override
	public String getProcessStatus() {
		return PROCESS_WAIT_SUBPROCESS;
	}

	@Override
	public long getSessionId() {
		return ts.getSessionId();
	}

	@Override
	public long getProcessId() {
		return this.processId;
	}

	@Override
	public long getStartTime() {
		return this.startTime;
	}
}
