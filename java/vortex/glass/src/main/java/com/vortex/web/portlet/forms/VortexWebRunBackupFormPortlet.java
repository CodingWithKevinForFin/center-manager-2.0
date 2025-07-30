package com.vortex.web.portlet.forms;

import java.util.List;
import java.util.Map;

import com.f1.base.Action;
import com.f1.container.ResultMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.LongKeyMap.Node;
import com.f1.utils.structs.Tuple2;
import com.f1.vortexcommon.msg.eye.VortexEyeBackup;
import com.f1.vortexcommon.msg.eye.VortexEyeBackupDestination;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunBackupRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunDeploymentResponse;
import com.vortex.client.VortexClientBackup;
import com.vortex.client.VortexClientBackupDestination;
import com.vortex.client.VortexClientDeployment;
import com.vortex.web.VortexWebEyeService;

public class VortexWebRunBackupFormPortlet extends VortexWebCommentFormPortlet {
	public static final byte NO = 0;
	public static final byte YES = 1;
	private VortexWebEyeService service;

	final private LongKeyMap<FormPortletSelectField<Byte>> backupIdToFields = new LongKeyMap<FormPortletSelectField<Byte>>();

	final private FormPortletButton submitButton;
	final private FormPortletButton selectAllButton;
	final private FormPortletButton deselectAllButton;
	final private FormPortletButton cancelButton;

	public VortexWebRunBackupFormPortlet(PortletConfig config) {
		super(config, config.getBuilderId(), (List) null, null, "backup.jpg");
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		addButton(this.submitButton = new FormPortletButton("Run Selected Backups"));
		addButton(this.cancelButton = new FormPortletButton("Cancel"));
		addButton(this.selectAllButton = new FormPortletButton("Select all"));
		addButton(this.deselectAllButton = new FormPortletButton("Unselect all"));
		formPortlet.setLabelsWidth(810);
	}
	@Override
	protected void onUserPressedButton(FormPortletButton button) {
		if (button == cancelButton)
			close();
		else if (button == selectAllButton) {
			for (FormPortletSelectField<Byte> e : backupIdToFields.values())
				e.setValue(YES);
		} else if (button == deselectAllButton) {
			for (FormPortletSelectField<Byte> e : backupIdToFields.values())
				e.setValue(NO);
		} else if (button == submitButton) {
			int cnt = 0;
			//break up the jobs by src / target for parallel processing (ideally this should be done in the eye)
			BasicMultiMap.List<Tuple2<String, Long>, Long> srcMuidAndDestIdToBackupId = new BasicMultiMap.List<Tuple2<String, Long>, Long>();
			for (Node<FormPortletSelectField<Byte>> e : backupIdToFields) {
				if (e.getValue().getValue() == YES) {
					VortexClientBackup clientBackup = service.getAgentManager().getBackup(e.getLongKey());
					VortexEyeBackup backup = clientBackup.getData();
					String srcMuid = clientBackup.getSourceMuid();
					srcMuidAndDestIdToBackupId.putMulti(new Tuple2<String, Long>(srcMuid, backup.getBackupDestinationId()), backup.getId());
					cnt++;
				}
			}
			if (cnt == 0)
				getManager().showAlert("Select at least one backup, or choose cancel");
			else {
				for (List<Long> backupIds : srcMuidAndDestIdToBackupId.values()) {
					VortexEyeRunBackupRequest req = nw(VortexEyeRunBackupRequest.class);
					req.setBackups(backupIds);
					service.sendRequestToBackend(getPortletId(), req);
				}
				close();
			}
		}
		super.onUserPressedButton(button);
	}
	@Override
	protected void onUserChangedValue(FormPortletField<?> field, Map<String, String> attributes) {
		super.onUserChangedValue(field, attributes);
	}

	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return 900;
	}

	public boolean setDestinations(List<VortexClientBackupDestination> deployments) {
		for (VortexClientBackupDestination deployment : deployments) {
			for (VortexClientBackup backup : deployment.getBackups().values()) {
				FormPortletSelectField<Byte> field = addField(new FormPortletSelectField<Byte>(Byte.class, describe(backup)));
				field.addOption(YES, "Backup");
				field.addOption(NO, "Skip");
				this.backupIdToFields.put(backup.getId(), field);
			}
		}
		return true;
	}
	public boolean setDeployments(List<VortexClientDeployment> deployments) {

		for (VortexClientDeployment deployment : deployments) {
			for (VortexClientBackup backup : deployment.getBackups().values()) {
				FormPortletSelectField<Byte> field = addField(new FormPortletSelectField<Byte>(Byte.class, describe(backup)));
				field.addOption(YES, "Backup");
				field.addOption(NO, "Skip");
				this.backupIdToFields.put(backup.getId(), field);
			}
		}

		return true;
	}
	private String describe(VortexClientBackup backup) {
		final VortexEyeBackupDestination backupDestination = service.getAgentManager().getBackupDestination(backup.getData().getBackupDestinationId()).getData();
		final String hostName = service.getAgentManager().getAgentMachineByUid(backup.getSourceMuid()).getHostName();
		final String backupHostName = service.getAgentManager().getAgentMachineByUid(backupDestination.getDestinationMachineUid()).getHostName();
		final String name;
		VortexClientDeployment deployment = backup.getDeployment();
		if (deployment != null)
			name = deployment.getBuildProcedure().getData().getName() + ": " + deployment.getData().getTargetDirectory() + "/";
		else
			name = "";
		return "BU-" + backup.getId() + " (" + hostName + "::" + name + backup.getData().getSourcePath() + " --> " + backupDestination.getName() + "@" + backupHostName + " )";
	}
	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		VortexEyeRunDeploymentResponse response = (VortexEyeRunDeploymentResponse) result.getAction();
		if (!response.getOk())
			getManager().showAlert(response.getMessage());
	}

}
