package com.f1.ami.plugins.p4;

import java.util.Map;

import com.f1.ami.amicommon.AmiScmAdapter;
import com.f1.ami.amicommon.AmiScmPlugin;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;

public class AmiP4ScmPlugin implements AmiScmPlugin {

	private static final String SCM_TYPE = "P4";
	private static final String SCM_DESC = "Perforce";

	private static final Map<String, Object> HELP_MAP = CH.m(//
			AmiScmPlugin.URL_HELP, ""//
			, AmiScmPlugin.CLIENT_HELP, ""//
			, AmiScmPlugin.USERNAME_HELP, ""//
			, AmiScmPlugin.OPTIONS_HELP, ""//
			, AmiScmPlugin.BASE_PATH_HELP, ""//
	);

	@Override
	public void init(ContainerTools tools, PropertyController props) {

	}

	@Override
	public String getPluginId() {
		return SCM_TYPE;
	}

	@Override
	public AmiScmAdapter createScmAdapter() {
		return new AmiP4ScmAdapter();
	}

	@Override
	public String getScmDescription() {
		return SCM_DESC;
	}

	@Override
	public String getSourceControlIcon() {
		return "mysql.png";
	}

	@Override
	public Map<String, Object> getScmHelpMap() {
		return AmiP4ScmPlugin.HELP_MAP;
	}

	@Override
	public String getScmHelp(String option) {
		return SH.s(AmiP4ScmPlugin.HELP_MAP.get(option));
	}

	@Override
	public Map<String, Object> getScmOptions() {
		return CH.m();
	}

}
