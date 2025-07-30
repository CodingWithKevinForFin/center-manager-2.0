package com.f1.ami.relay;

import java.util.logging.Logger;

import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandRequest;
import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandResponse;
import com.f1.base.Message;
import com.f1.container.Partition;
import com.f1.container.ResultMessage;
import com.f1.povo.standard.RunnableRequestMessage;
import com.f1.povo.standard.TextMessage;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class AmiRelayCenterRunAmiCommandItinerary extends AmiRelayAbstractItinerary<AmiRelayRunAmiCommandRequest> {

	private AmiRelayRunAmiCommandResponse response;
	private Runner cmd;
	private static final Logger log = LH.get();

	@Override
	public byte startJourney(AmiRelayItineraryWorker worker) {
		response = nw(AmiRelayRunAmiCommandResponse.class);
		AmiRelayRunAmiCommandRequest action = getInitialRequest().getAction();
		response.setCommandUid(getInitialRequest().getAction().getCommandUid());

		this.cmd = new Runner(getState().getAmiServer(), action);
		final RunnableRequestMessage rm = getState().nw(RunnableRequestMessage.class);
		rm.setTimeoutMs(action.getTimeoutMs());
		rm.setPartitionId(Partition.NO_PARTITION);
		rm.setRunnable(cmd);
		worker.sendRunnable(this, rm);
		return STATUS_ACTIVE;
	}

	@Override
	public byte onResponse(ResultMessage<?> result, AmiRelayItineraryWorker worker) {
		if (result.getAction() instanceof TextMessage) {
			TextMessage txt = (TextMessage) result.getAction();
			if (txt.getText() != null) {
				this.response.setOk(false);
				this.response.setMessage(txt.getText());
				this.response.setStatusCode(cmd.errorStatus);
			} else if (cmd.getErrorResponse() != null) {
				this.response.setOk(false);
				this.response.setMessage(cmd.getErrorResponse());
				this.response.setStatusCode(cmd.errorStatus);
			} else {
				AmiRelayRunAmiCommandResponse res = cmd.getResponse();
				if (res == null) {
					response.setOk(false);
					response.setMessage("No response received");
				} else {
					//copy all fields
					try {
						OH.assertEq(response.getCommandUid(), res.getCommandUid());
					} catch (Exception e) {
						LH.warning(log, e);
					}
					response.setAmiMessage(res.getMessage());
					response.setAmiScript(res.getAmiScript());
					response.setConnectionId(res.getConnectionId());
					response.setParams(res.getParams());
					response.setStatusCode(res.getStatusCode());
					response.setMessage(res.getMessage());
					response.setException(res.getException());
					response.setOk(res.getOk());
				}

			}
		}

		return STATUS_COMPLETE;
	}
	@Override
	public Message endJourney(AmiRelayItineraryWorker worker) {
		return response;
	}

	private static class Runner implements Runnable {

		private AmiRelayServer amiServer;
		private AmiRelayRunAmiCommandRequest action;
		private String errorResponse;
		private AmiRelayRunAmiCommandResponse response;
		private int errorStatus;

		public Runner(AmiRelayServer amiServer, AmiRelayRunAmiCommandRequest action) {
			this.amiServer = amiServer;
			this.action = action;
		}

		public AmiRelayRunAmiCommandResponse getResponse() {
			return response;
		}

		@Override
		public void run() {
			StringBuilder errorSink = new StringBuilder();
			this.response = amiServer.callCommand(action, errorSink);
			if (errorSink.length() > 0) {
				this.errorStatus = AmiRelayRunAmiCommandResponse.STATUS_COMMAND_NOT_REGISTERED;
				this.errorResponse = errorSink.toString();
			} else if (this.response == null) {
				this.errorStatus = AmiRelayRunAmiCommandResponse.STATUS_TIMEOUT;
				this.errorResponse = "Time out reached: " + action.getTimeoutMs() + " MS";
			}

		}

		public String getErrorResponse() {
			return errorResponse;
		}

		@Override
		public String toString() {
			return super.toString() + " - " + action.getCommandUid();
		}

	}

}
