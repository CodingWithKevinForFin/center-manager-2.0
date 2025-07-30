package com.vortex.web.portlet.forms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.SH;
import com.f1.utils.impl.TextMatcherFactory;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexBuildResult;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunBuildProcedureRequest;
import com.vortex.client.VortexClientBuildProcedure;
import com.vortex.web.VortexWebEyeService;

public class VortexWebRunProcedureFormPortlet extends VortexWebMetadataFormPortlet {
	private VortexWebEyeService service;

	private FormPortletButton submitButton;

	private String parentPortletId;

	public VortexWebRunProcedureFormPortlet(PortletConfig config, String parentPortletId) {
		super(config, "package.jpg");
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		this.parentPortletId = parentPortletId;
		initMetadataFormFields(VortexAgentEntity.TYPE_BUILD_RESULT);

	}
	@Override
	protected void onUserPressedButton(FormPortletButton button) {
		for (VortexClientBuildProcedure buildProcedureToRun : this.buildProceduresToRun) {
			if (button == submitButton) {
				VortexEyeRunBuildProcedureRequest request = nw(VortexEyeRunBuildProcedureRequest.class);
				request.setBuildProcedureId(buildProcedureToRun.getId());
				Map<String, String> vars = new HashMap<String, String>();
				for (String variableName : buildProcedureToRun.getVariables()) {
					FormPortletTextField value = this.fields.get(variableName);
					vars.put(variableName, SH.trim(value.getValue()));
				}
				request.setBuildProcedureVariables(vars);
				request.setInvokedBy(getManager().getState().getWebState().getUser().getUserName());
				request.setMetadata(generateMetadata());
				if (request.getMetadata() == null)
					return;
				request.setComment(getUserComment());
				service.sendRequestToBackend(parentPortletId, request);
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
		return 600;
	}

	private static String wrap(String text) {
		return TextMatcherFactory.DEFAULT.stringToExpression(text);
	}
	public void setBuildProcedure(VortexClientBuildProcedure bp) {
	}

	private Map<String, FormPortletTextField> fields = new HashMap<String, FormPortletTextField>();

	private List<VortexClientBuildProcedure> buildProceduresToRun;

	public String describe() {
		return (buildProceduresToRun.size() == 1 ? buildProceduresToRun.get(0).getData().getName() : buildProceduresToRun.size() + " procedures");
	}
	public void setBuildProcedures(List<VortexClientBuildProcedure> bps) {

		long now = getManager().getState().getWebState().getPartition().getContainer().getTools().getNow();
		this.buildProceduresToRun = bps;
		Set<String> variableNames = new TreeSet<String>();

		for (VortexClientBuildProcedure buildProcedure : this.buildProceduresToRun)
			variableNames.addAll(buildProcedure.getVariables());
		int loc = 0;
		for (String v : variableNames) {
			FormPortletTextField field;
			fields.put(v, field = addField(new FormPortletTextField(v), loc++));
			if ("yyyymmdd".equals(v)) {
				field.setValue(getManager().getLocaleFormatter().getDateFormatter(LocaleFormatter.DATE).format(now));
			} else if ("hhmmss".equals(v)) {
				field.setValue(getManager().getLocaleFormatter().getDateFormatter(LocaleFormatter.HHMMSS).format(now));
			}
		}
		addButton(this.submitButton = new FormPortletButton("Run Build Procedure - " + describe()));
	}
	public void setVariables(Map<String, String> variables) {
		for (Map.Entry<String, String> e : variables.entrySet()) {
			String v = e.getKey();
			if ("yyyymmdd".equals(v))
				continue;
			if ("hhmmss".equals(v))
				continue;
			FormPortletTextField field = fields.get(v);
			if (field != null)
				field.setValue(e.getValue());
		}
	}
	public void setBuildResult(VortexBuildResult br) {
		if (br.getBuildVariables() != null)
			setVariables(br.getBuildVariables());
		populateMetadataFormFields(br);
	}
}
