package com.vortex.web.portlet.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.f1.base.Action;
import com.f1.container.ResultMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.CH;
import com.f1.utils.Formatter;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;
import com.f1.utils.VH;
import com.f1.utils.structs.LongKeyMap;
import com.f1.vortexcommon.msg.eye.VortexBuildResult;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunDeploymentResponse;
import com.vortex.client.VortexClientBuildProcedure;
import com.vortex.client.VortexClientBuildResult;
import com.vortex.client.VortexClientDeployment;
import com.vortex.web.VortexWebEyeService;

public class VortexWebRunDeploymentFormPortlet extends VortexWebCommentFormPortlet {
	private VortexWebEyeService service;

	private Map<String, FormPortletTextField> fields = new HashMap<String, FormPortletTextField>();
	private LongKeyMap<FormPortletSelectField<Long>> resultFields = new LongKeyMap<FormPortletSelectField<Long>>();
	private LongKeyMap<FormPortletTextField> searchDeploymentsResultsField = new LongKeyMap<FormPortletTextField>();

	private FormPortletButton submitButton;

	private boolean onlyVerify;
	public VortexWebRunDeploymentFormPortlet(PortletConfig config, boolean onlyVerify) {
		super(config, config.getPortletId(), (List) null, null, onlyVerify ? "verify.jpg" : "deploy.jpg");
		this.onlyVerify = onlyVerify;
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
	}
	@Override
	protected void onUserPressedButton(FormPortletButton button) {
		if (button == submitButton) {
			VortexWebRunDeploymentsDialogPortlet verifyPortlet = null;
			verifyPortlet = new VortexWebRunDeploymentsDialogPortlet(generateConfig(), onlyVerify, getUserComment());
			getManager().showDialog("Verify Deployment Environments", verifyPortlet);
			for (VortexClientDeployment deployment : this.deployments) {
				Map<String, String> vars = new HashMap<String, String>();
				FormPortletSelectField<Long> resultField = resultFields.get(deployment.getId());
				for (String variableName : deployment.getVariables()) {
					FormPortletTextField value = this.fields.get(variableName);
					vars.put(variableName, SH.trim(value.getValue()));
				}
				verifyPortlet.addRow(deployment, service.getAgentManager().getBuildResult(resultField.getValue()), vars);
			}
			verifyPortlet.sendVerify();
			close();
		} else
			super.onUserPressedButton(button);
	}
	@Override
	protected void onUserChangedValue(FormPortletField<?> field, Map<String, String> attributes) {
		if (field instanceof FormPortletTextField) {
			Object correlationData = field.getCorrelationData();
			String title = field.getTitle();
			if (SH.indexOf(title, "Search: ", 0) != -1 && correlationData instanceof VortexClientDeployment) {
				filterDeploymentsResultsField((FormPortletTextField) field);
			} else
				super.onUserChangedValue(field, attributes);
		} else
			super.onUserChangedValue(field, attributes);
	}

	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return 600;
	}

	private List<VortexClientDeployment> deployments;

	public String describe() {
		return (deployments.size() == 1 ? deployments.get(0).getBuildProcedure().getData().getName() : deployments.size() + " deployments");
	}
	public boolean setDeployments(List<VortexClientDeployment> deployments) {

		long now = getManager().getState().getWebState().getPartition().getContainer().getTools().getNow();
		Formatter formatter = getManager().getLocaleFormatter().getDateFormatter(LocaleFormatter.DATETIME);
		this.deployments = deployments;
		Set<String> variableNames = new TreeSet<String>();
		for (VortexClientDeployment deployment : this.deployments) {
			VortexClientBuildProcedure bp = deployment.getBuildProcedure();
			if (bp == null) {
				getManager().showAlert("Deployment DP-" + deployment.getId() + " is invalid because its not associated with an active build procedure");
				return false;
			}

			FormPortletTextField search = searchDeploymentsResultsField.get(deployment.getId());
			if (search == null) {
				FormPortletTextField nwField = new FormPortletTextField("DP-" + deployment.getId() + " Search: ");
				nwField.setCorrelationData(deployment);
				nwField.setWidth(400);
				search = addField(nwField);
				searchDeploymentsResultsField.put(deployment.getId(), nwField);
			}

			FormPortletSelectField<Long> sel = resultFields.get(deployment.getId());
			if (sel == null) {
				sel = addField(new FormPortletSelectField<Long>(Long.class, bp.getData().getName()));

				List<VortexBuildResult> sorted = new ArrayList<VortexBuildResult>();
				for (VortexClientBuildResult br : bp.getBuildResults())
					sorted.add(br.getData());
				VH.sort(sorted, VortexBuildResult.PID_ID);
				Collections.reverse(sorted);
				for (VortexBuildResult i : sorted) {
					VortexClientBuildResult br = service.getAgentManager().getBuildResult(i.getId());
					if (br.getData().getState() == VortexBuildResult.STATE_SUCCCESS)
						sel.addOption(br.getId(), br.getDescription(formatter));
				}
				if (CH.isEmpty(sel.getOptions())) {
					getManager().showAlert(
							"Deployment DP-" + deployment.getId() + " can not be run until a successful build for procedure '" + bp.getData().getName() + "' is available.");
					return false;
				}
				resultFields.put(deployment.getId(), sel);
			}
			VortexClientBuildResult br = deployment.getBuildResult();
			if (br != null)
				sel.setValueNoThrow(br.getId());
			variableNames.addAll(deployment.getVariables());
		}
		for (String v : variableNames) {
			FormPortletTextField field;
			fields.put(v, field = addField(new FormPortletTextField(v)));
			if ("yyyymmdd".equals(v)) {
				field.setValue(getManager().getLocaleFormatter().getDateFormatter(LocaleFormatter.DATE).format(now));
			} else if ("hhmmss".equals(v)) {
				field.setValue(getManager().getLocaleFormatter().getDateFormatter(LocaleFormatter.HHMMSS).format(now));
			}
		}
		addButton(this.submitButton = new FormPortletButton((onlyVerify ? "Verify Deployment - " : "Run Deployment - ") + describe()));
		return true;
	}
	public void filterDeploymentsResultsField(FormPortletTextField searchField) {
		String search = searchField.getValue();
		final TextMatcher searchMatcher = SH.m(SH.is(search) ? search : "*");
		VortexClientDeployment deployment = (VortexClientDeployment) searchField.getCorrelationData();
		VortexClientBuildProcedure bp = deployment.getBuildProcedure();

		Formatter formatter = getManager().getLocaleFormatter().getDateFormatter(LocaleFormatter.DATETIME);
		FormPortletSelectField<Long> sel = resultFields.get(deployment.getId());
		sel.clearOptions();

		List<VortexBuildResult> sorted = new ArrayList<VortexBuildResult>();
		for (VortexClientBuildResult br : bp.getBuildResults())
			sorted.add(br.getData());
		VH.sort(sorted, VortexBuildResult.PID_ID);
		Collections.reverse(sorted);

		for (VortexBuildResult i : sorted) {
			VortexClientBuildResult br = service.getAgentManager().getBuildResult(i.getId());
			if (br.getData().getState() == VortexBuildResult.STATE_SUCCCESS) {
				String description = br.getDescription(formatter);
				if (searchMatcher.matches(description))
					sel.addOption(br.getId(), description);
			}
		}

		VortexClientBuildResult br = deployment.getBuildResult();
		if (br != null) {
			if (CH.isEmpty(sel.getOptions())) {
				sel.addOption(br.getId(), br.getDescription(formatter));
			}
			sel.setValueNoThrow(br.getId());
		}
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		VortexEyeRunDeploymentResponse response = (VortexEyeRunDeploymentResponse) result.getAction();
		if (!response.getOk())
			getManager().showAlert(response.getMessage());
		else
			close();
	}

}
