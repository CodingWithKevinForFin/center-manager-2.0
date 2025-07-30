package com.vortex.web.portlet.forms;

import com.f1.base.Action;
import com.f1.container.ResultMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageMachineRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageMachineResponse;
import com.vortex.client.VortexClientMachine;
import com.vortex.web.VortexWebEyeService;

public class VortexWebMachineFormPortlet extends VortexWebMetadataFormPortlet {

	private VortexClientMachine entity;

	public VortexWebMachineFormPortlet(PortletConfig config, VortexClientMachine clientEntity) {
		super(config, "host.jpg");
		setIconToEdit();
		this.entity = clientEntity;
		initMetadataFormFields(VortexAgentEntity.TYPE_MACHINE);
		populateMetadataFormFields(entity.getData());
		addButton(new FormPortletButton("Edit Machine"));
	}
	@Override
	protected void onUserPressedButton(FormPortletButton formPortletButton) {
		super.onUserPressedButton(formPortletButton);
		VortexEyeManageMachineRequest req = nw(VortexEyeManageMachineRequest.class);
		req.setMachine(this.entity.getData().clone());
		if (!populateMetadata(req.getMachine()))
			return;
		VortexWebEyeService service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		req.setComment(getUserComment());
		service.sendRequestToBackend(getPortletId(), req);
	}
	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		VortexEyeManageMachineResponse action = (VortexEyeManageMachineResponse) result.getAction();
		if (action.getOk())
			close();
		super.onBackendResponse(result);
	}

}
