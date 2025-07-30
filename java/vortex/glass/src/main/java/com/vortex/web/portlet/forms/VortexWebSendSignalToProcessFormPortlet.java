package com.vortex.web.portlet.forms;

import java.util.Collections;
import java.util.List;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.utils.SH;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunSignalProcessRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyePassToAgentRequest;
import com.vortex.client.VortexClientProcess;
import com.vortex.web.VortexWebEyeService;

public class VortexWebSendSignalToProcessFormPortlet extends VortexWebCommentFormPortlet {

	final private FormPortletSelectField<Byte> typeField;
	private List<VortexClientProcess> processes;
	private VortexWebEyeService service;

	public VortexWebSendSignalToProcessFormPortlet(PortletConfig config) {
		super(config, config.getPortletId(), Collections.EMPTY_LIST, "Send Signal", "signal.jpg");
		//addButton(submitButton = new FormPortletButton("submit"));
		typeField = new FormPortletSelectField<Byte>(Byte.class, "Signal Type");
		typeField.addOption((VortexAgentRunSignalProcessRequest.SIG_TERM), "Terminate (posix code 15)");
		typeField.addOption((VortexAgentRunSignalProcessRequest.SIG_KILL), "Kill (posix code 9)");
		typeField.addOption((VortexAgentRunSignalProcessRequest.SIG_QUIT), "Terminal Quit (posix code 3)");
		service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		formPortlet.addField(typeField, 0);
		//commentField = addField(new FormPortletTextAreaField("Optional Comment"));
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == submitButton) {
			if (SH.isnt(super.getCommentField().getValue())) {
				getManager().showAlert("Comment required when sending signal to a process");
				return;
			}
			for (VortexClientProcess proc : processes) {
				VortexAgentRunSignalProcessRequest msg = nw(VortexAgentRunSignalProcessRequest.class);
				msg.setProcessOwner(proc.getData().getUser());
				msg.setProcessPid(proc.getData().getPid());
				msg.setProcessId(proc.getData().getId());
				msg.setProcessStartTime(proc.getData().getStartTime());
				msg.setSignal((typeField.getValue()));
				VortexEyePassToAgentRequest req = nw(VortexEyePassToAgentRequest.class);
				req.setAgentMachineUid(proc.getMachine().getMachineUid());
				req.setAgentProcessUid(proc.getMachine().getProcessUid());
				req.setAgentRequest(msg);
				req.setComment(getUserComment());
				service.sendRequestToBackend(getPortletId(), req);
			}
			close();
			return;
		} else
			super.onButtonPressed(portlet, button);
	}

	public List<VortexClientProcess> getProcesses() {
		return processes;
	}
	public void setProcesses(List<VortexClientProcess> processes) {
		this.processes = processes;
	}

}
