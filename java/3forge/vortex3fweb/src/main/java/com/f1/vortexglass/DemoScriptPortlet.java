package com.f1.vortexglass;

import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.utils.EH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.Tuple3;

public class DemoScriptPortlet extends FormPortlet {
	private static final Logger log = Logger.getLogger(DemoScriptPortlet.class.getName());
	private FormPortletButton runButton;
	private FormPortletSelectField<Integer> selectField;
	private IntKeyMap<String> scripts;

	public DemoScriptPortlet(PortletConfig config, Map<String, String> scripts) {
		super(config);
		this.scripts = new IntKeyMap<String>();
		selectField = addField(new FormPortletSelectField<Integer>(Integer.class, "script"));
		runButton = addButton(new FormPortletButton("run script"));
		int i = 0;
		for (Entry<String, String> e : scripts.entrySet()) {
			i++;
			selectField.addOption(i, e.getKey());
			this.scripts.put(i, e.getValue());
		}
	}

	public static class Builder extends AbstractPortletBuilder<DemoScriptPortlet> {

		public static final String ID = "DemoScriptingPortlet";
		final private Map<String, String> scripts;

		public Builder(Map<String, String> scripts) {
			super(DemoScriptPortlet.class);
			this.scripts = scripts;
		}
		@Override
		public DemoScriptPortlet buildPortlet(PortletConfig portletConfig) {
			return new DemoScriptPortlet(portletConfig, scripts);
		}
		@Override
		public String getPortletBuilderName() {
			return "Demo Scripting Portlet";
		}
		@Override
		public String getPortletBuilderId() {
			return ID;
		}
	}

	@Override
	public void onUserPressedButton(FormPortletButton formPortletButton) {
		if (formPortletButton == runButton) {
			String script = scripts.get(selectField.getValue());
			LH.info(log, "Running script", script);
			Tuple3<Process, byte[], byte[]> result = EH.exec(getManager().getState().getWebState().getPartition().getContainer().getThreadPoolController(), script);
			int exitCode = result.getA().exitValue();
			String stdout = new String(result.getB());
			String stderr = new String(result.getC());
			getManager().showAlert("Exit code=" + result.getA().exitValue() + ", stdout=" + SH.ddd(stdout, 60) + ", stderr=" + SH.ddd(stderr, 60));
			if (exitCode == 0)
				LH.info(log, "Result from running '", script, "': Exit code=", result.getA().exitValue(), ", stdout=", stdout, ", stderr=", stderr);
			else
				LH.warning(log, "Result from running '", script, "': Exit code=", result.getA().exitValue(), ", stdout=", stdout, ", stderr=", stderr);
		}
		super.onUserPressedButton(formPortletButton);
	}
}
