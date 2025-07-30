package com.vortex.web.portlet.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.povo.f1app.reqres.F1AppChangeLogLevelRequest;
import com.f1.speedlogger.SpeedLoggerLevels;
import com.f1.speedlogger.impl.SpeedLoggerUtils;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.utils.CH;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyePassToF1AppRequest;
import com.vortex.client.VortexClientF1AppState;
import com.vortex.client.VortexClientF1AppState.AgentWebLogger;
import com.vortex.client.VortexClientF1AppState.AgentWebLoggerSink;
import com.vortex.client.VortexClientMachine;
import com.vortex.web.VortexWebEyeService;

public class VortexWebChangeLoggerLevelFormPortlet extends FormPortlet {

	final private VortexWebEyeService service;
	final private FormPortletButton submitButton;
	final private FormPortletSelectField<Integer> levelField;
	final private Map<Long, FormPortletSelectField<Integer>> appInstanceIds2sinkFields = new HashMap<Long, FormPortletSelectField<Integer>>();
	final private BasicMultiMap.List<Long, AgentWebLogger> loggersByApp;

	public VortexWebChangeLoggerLevelFormPortlet(PortletConfig config, List<AgentWebLogger> loggers) {
		super(config);
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		addButton(this.submitButton = new FormPortletButton("Adjust Logger Levels"));
		levelField = addField(new FormPortletSelectField<Integer>(Integer.class, "Logger Level"));
		for (int level : SpeedLoggerLevels.LEVELS_2_LABEL_SORTED.keySet())
			levelField.addOption(level, SpeedLoggerUtils.getLevelAsString(level));

		this.loggersByApp = new BasicMultiMap.List<Long, VortexClientF1AppState.AgentWebLogger>();

		int minLevel = SpeedLoggerLevels.SEVERE;
		for (AgentWebLogger logger : loggers) {
			loggersByApp.putMulti(logger.getObject().getF1AppInstanceId(), logger);
			minLevel = Math.min(logger.getObject().getMinLogLevel(), minLevel);
		}
		levelField.setValueNoThrow(minLevel);

		for (Entry<Long, List<AgentWebLogger>> entry : loggersByApp.entrySet()) {
			VortexClientF1AppState aps = entry.getValue().get(0).getAppState();
			VortexClientMachine machine = service.getAgentManager().getAgentMachine(aps.getMachineInstanceId());
			String label = aps.getSnapshot().getAppName() + " on " + machine.getHostName();
			FormPortletSelectField<Integer> sinkField = addField(new FormPortletSelectField<Integer>(Integer.class, label));
			for (AgentWebLoggerSink sink : aps.getLoggerSinksById().values())
				sinkField.addOption(sinkField.getOptionsCount(), sink.getObject().getSinkId());
			appInstanceIds2sinkFields.put(aps.getSnapshot().getId(), sinkField);

		}
	}

	@Override
	public void onUserPressedButton(FormPortletButton formPortletButton) {
		super.onUserPressedButton(formPortletButton);
		for (Entry<Long, FormPortletSelectField<Integer>> e : appInstanceIds2sinkFields.entrySet()) {
			VortexClientF1AppState app = service.getAgentManager().getJavaAppState(e.getKey());

			final F1AppChangeLogLevelRequest req = nw(F1AppChangeLogLevelRequest.class);
			req.setLevel(levelField.getValue());
			final String sinkId = e.getValue().getOption(e.getValue().getValue()).getName();
			req.setSinkIds(CH.l(sinkId));
			List<AgentWebLogger> loggers = loggersByApp.get(app.getSnapshot().getId());
			List<String> loggerIds = new ArrayList<String>(loggers.size());
			for (AgentWebLogger logger : loggers)
				loggerIds.add(logger.getObject().getLoggerId());
			req.setLoggerIds(loggerIds);
			req.setTargetF1AppProcessUid(app.getSnapshot().getProcessUid());
			VortexEyePassToF1AppRequest wrapper = nw(VortexEyePassToF1AppRequest.class);
			wrapper.setF1AppId(app.getSnapshot().getId());
			wrapper.setF1AppRequest(req);
			wrapper.setInvokedBy(getManager().getState().getWebState().getUser().getUserName());
			service.sendRequestToBackend(getPortletId(), wrapper);
		}
		close();
	}
}
