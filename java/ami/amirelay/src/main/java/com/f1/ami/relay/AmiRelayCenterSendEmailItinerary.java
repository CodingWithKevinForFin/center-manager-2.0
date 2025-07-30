package com.f1.ami.relay;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.f1.ami.amicommon.msg.AmiRelaySendEmailRequest;
import com.f1.ami.amicommon.msg.AmiRelaySendEmailResponse;
import com.f1.base.Message;
import com.f1.container.Partition;
import com.f1.container.ResultMessage;
import com.f1.email.EmailAttachment;
import com.f1.email.EmailClient;
import com.f1.email.EmailClientConfig;
import com.f1.email.MimeTypeManager;
import com.f1.povo.standard.RunnableRequestMessage;
import com.f1.povo.standard.TextMessage;
import com.f1.utils.LH;

public class AmiRelayCenterSendEmailItinerary extends AmiRelayAbstractItinerary<AmiRelaySendEmailRequest> {

	private static final Logger log = LH.get();
	private AmiRelaySendEmailResponse response;
	private Runner cmd;

	@Override
	public byte startJourney(AmiRelayItineraryWorker worker) {
		response = nw(AmiRelaySendEmailResponse.class);
		AmiRelaySendEmailRequest action = getInitialRequest().getAction();

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
			} else if (cmd.getErrorResponse() != null) {
				this.response.setOk(false);
				this.response.setMessage(cmd.getErrorResponse());
			} else {
				AmiRelaySendEmailResponse res = cmd.getResponse();
				if (res == null) {
					response.setOk(false);
					response.setMessage("No response recieved");
				} else if (res.getException() != null) {
						response.setOk(false);
						response.setException(res.getException());
				} else {
					response.setOk(true);
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
		private AmiRelaySendEmailRequest action;
		private String errorResponse;
		private AmiRelaySendEmailResponse response;

		public Runner(AmiRelayServer amiServer, AmiRelaySendEmailRequest action) {
			this.amiServer = amiServer;
			this.action = action;
		}

		public AmiRelaySendEmailResponse getResponse() {
			return response;
		}

		@Override
		public void run() {
			final EmailClient ec;
			boolean needClose = false;
			this.response = amiServer.getTools().nw(AmiRelaySendEmailResponse.class);// amiServer.sendEmail(action, errorSink);
			if (action.getUsername() != null || action.getPassword() != null) {
				EmailClientConfig ecc = (EmailClientConfig) amiServer.getTools().getServices().getServiceNoThrow(AmiRelayMain.SERVICE_EMAILCLIENTCONFIG);
				if (ecc == null) {
					this.errorResponse = "EmailClient Not Configured on Relay";
					return;
				}
				ecc = new EmailClientConfig(ecc);
				if (action.getUsername() != null)
					ecc.setUsername(action.getUsername());
				if (action.getPassword() != null)
					ecc.setPassword(action.getPassword());
				ec = new EmailClient(ecc);
				needClose = true;
			} else
				ec = (EmailClient) amiServer.getTools().getServices().getServiceNoThrow(AmiRelayMain.SERVICE_EMAILCLIENT);
			if (ec == null) {
				this.errorResponse = "EmailClient Not Configured on Relay";
			} else {
				try {
					List<EmailAttachment> attachments;
					if (action.getAttachmentDatas() == null)
						attachments = null;
					else {
						attachments = new ArrayList<EmailAttachment>(action.getAttachmentDatas().size());
						for (int i = 0; i < action.getAttachmentDatas().size(); i++) {
							String name = action.getAttachmentNames().get(i);
							attachments.add(new EmailAttachment(action.getAttachmentDatas().get(i), MimeTypeManager.getInstance().getMimeTypeForFileName(name), name, name));
						}
					}

					ec.sendEmail(action.getBody(), action.getSubject(), action.getToList(), action.getFrom(), action.getIsHtml(), attachments);
				} catch (Exception e) {
					LH.warning(log, e);
					response.setException(e);
					response.setMessage("Email Send Failed: " + e.getMessage());
				}
			}
			if (needClose) {
				ec.close();
			}

		}

		public String getErrorResponse() {
			return errorResponse;
		}

	}

}
