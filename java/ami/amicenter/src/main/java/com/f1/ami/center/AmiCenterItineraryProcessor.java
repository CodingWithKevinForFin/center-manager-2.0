package com.f1.ami.center;

import java.util.concurrent.TimeUnit;

import com.f1.ami.amicommon.msg.AmiCenterChangesMessage;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amicommon.msg.AmiRelayRequest;
import com.f1.ami.center.hdb.events.AmiHdbRequest;
import com.f1.base.Action;
import com.f1.base.Message;
import com.f1.container.PartitionResolver;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.povo.msg.MsgMessage;
import com.f1.povo.standard.RunnableRequestMessage;
import com.f1.utils.LH;

public class AmiCenterItineraryProcessor extends AmiCenterBasicProcessor<Message> implements AmiCenterItineraryWorker, PartitionResolver<Message> {

	private AmiCenterState centerState;

	public AmiCenterItineraryProcessor() {
		super(Message.class);
	}

	@Override
	public void init() {
		super.init();
		setPartitionResolver(this);
	}

	@Override
	public void start() {
		super.start();
		this.centerState = getContainer().getPartitionController().getState(AmiCenterSuite.PARTITIONID_AMI_CENTER, AmiCenterState.class);
	}

	@Override
	public void processAction(Message action, AmiCenterState state, ThreadScope threadScope) throws Exception {
		boolean isReadonly = state == null;
		final byte status;
		final AmiCenterItinerary<?> itinerary;
		if (action instanceof AmiCenterStartItineraryMessage) {
			AmiCenterStartItineraryMessage startItinerary = (AmiCenterStartItineraryMessage) action;
			itinerary = startItinerary.getItinerary();
			itinerary.init((RequestMessage) startItinerary.getInitialRequest(), centerState, isReadonly);
		} else {
			ResultMessage<?> result = (ResultMessage<?>) action;
			RequestMessage<?> request = result.getRequestMessage();
			itinerary = (AmiCenterItinerary<?>) request.getCorrelationId();
		}
		if (isReadonly) {
			int timeoutMs = itinerary.getTimeoutMs();
			if (!this.centerState.getPartition().lockForRead(timeoutMs, TimeUnit.MILLISECONDS)) {
				LH.warning(log, "no more pending requests for active itinerary, auto responding: ", itinerary);
				ResultMessage<?> res = nw(ResultMessage.class);
				res.setError(new RuntimeException("Timeout (" + timeoutMs + " millis) expired while waiting for read lock"));
				reply(itinerary.getInitialRequest(), res, threadScope);
				return;
			}
		}
		try {
			if (action instanceof AmiCenterStartItineraryMessage) {
				AmiCenterStartItineraryMessage startItinerary = (AmiCenterStartItineraryMessage) action;
				status = itinerary.startJourney(this);
			} else {
				ResultMessage<?> result = (ResultMessage<?>) action;
				RequestMessage<?> request = result.getRequestMessage();
				itinerary.removePendingRequest(request);
				status = itinerary.onResponse(result, this);
			}
			if (status == AmiCenterItinerary.STATUS_COMPLETE) {
				if (!itinerary.getPendingRequests().isEmpty())
					LH.warning(log, "Still pending requests for completed itinerary '", itinerary, "': ", itinerary.getPendingRequests());
				Message r = itinerary.endJourney(this);
				ResultMessage<Message> resultMessage = nw(ResultMessage.class);
				resultMessage.setAction(r);
				if (itinerary.getInitialRequest() != null) {
					reply(itinerary.getInitialRequest(), resultMessage, threadScope);
				}
			} else {
				if (itinerary.getPendingRequests().isEmpty()) {
					LH.warning(log, "no more pending requests for active itinerary, auto responding: ", itinerary);
					ResultMessage<?> res = nw(ResultMessage.class);
					res.setError(new RuntimeException("itinerary failed to complete"));
					reply(itinerary.getInitialRequest(), res, threadScope);
				}
			}
		} finally {
			if (isReadonly)
				this.centerState.getPartition().unlockForRead();
		}
	}

	@Override
	public void sendToClients(AmiCenterItinerary<?> source, AmiCenterChangesMessage deltas) {
		super.sendToClients(deltas);
	}

	@Override
	public void sendRequestToAgent(AmiCenterItinerary<?> source, AmiRelayRequest req, String processUid) {
		if (processUid == null)
			throw new NullPointerException("processUid");
		MsgMessage msg = super.sendRequestToAgent(req, processUid, getLoopbackPort(), 0);
		RequestMessage<?> request = (RequestMessage<?>) msg.getMessage();
		request.setCorrelationId(source);
		source.addPendingRequest(request);

	}

	@Override
	public <M extends Message> void startItinerary(AmiCenterItinerary<?> source, AmiCenterItinerary<M> itinerary, M action) {
		RequestMessage<M> req = nw(RequestMessage.class);
		req.setResultPort(this.getLoopbackPort());
		req.setAction(action);
		startItinerary(itinerary, req);
		req.setCorrelationId(source);
		source.addPendingRequest(req);
	}

	@Override
	public void sendRunnable(AmiCenterItinerary<?> source, RunnableRequestMessage rm) {
		RequestMessage<RunnableRequestMessage> req = nw(RequestMessage.class);
		req.setResultPort(this.getLoopbackPort());
		req.setAction(rm);
		req.setCorrelationId(source);
		getToRunnablePort().send(req, null);
		source.addPendingRequest(req);
	}

	@Override
	public Object getPartitionId(Message action) {
		if (action instanceof AmiCenterStartItineraryMessage) {
			RequestMessage<?> requestMessage = ((AmiCenterStartItineraryMessage) action).getInitialRequest();
			Action inner = requestMessage.getAction();
			if (inner instanceof AmiCenterQueryDsRequest) {
				AmiCenterQueryDsRequest dsRequest = (AmiCenterQueryDsRequest) inner;
				if (dsRequest.getUseConcurrency() || !"AMI".equals(dsRequest.getDatasourceName()))
					return null;
			}
		} else if (action instanceof ResultMessage) {
			ResultMessage<?> resultMessage = (ResultMessage<?>) action;
			if (resultMessage.getRequestMessage().getCorrelationId() instanceof AmiCenterQueryDsItinerary) {
				AmiCenterQueryDsItinerary itinerary = (AmiCenterQueryDsItinerary) resultMessage.getRequestMessage().getCorrelationId();
				if (itinerary.getInitialRequest().getAction().getUseConcurrency())
					return null;
			}
		}
		return AmiCenterSuite.PARTITIONID_AMI_CENTER;
	}

	@Override
	public void sendHdbRequest(AmiCenterItinerary<?> executeItinerary, AmiHdbRequest rm) {
		final RequestMessage<AmiHdbRequest> request = nw(RequestMessage.class);
		request.setResultPort(this.getLoopbackPort());
		request.setAction(rm);
		this.getToHdbPort().send(request, null);
		request.setCorrelationId(executeItinerary);
		executeItinerary.addPendingRequest(request);
	}
}
