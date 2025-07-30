package com.f1.ami.plugins.git;

import java.util.LinkedHashMap;
import java.util.Map;

import com.f1.ami.amicommon.AmiScmAdapter;
import com.f1.ami.amicommon.AmiScmPlugin;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;

public class AmiGitScmPlugin implements AmiScmPlugin {

	private static final String SCM_TYPE = "Git";
	private static final String SCM_DESC = "Git";
	private static final Map<String, Object> HELP_MAP = CH.m(//
			AmiScmPlugin.URL_HELP, "https://domain/project.git"//
			, AmiScmPlugin.CLIENT_HELP, "Client Name"//
			, AmiScmPlugin.USERNAME_HELP, "Account Name/Email"//
			, AmiScmPlugin.OPTIONS_HELP, "Use this to manually input custom options, expecting comma delimited, associator `=`, and escaped with `\\` "//
			, AmiScmPlugin.BASE_PATH_HELP, "Git Repository Directory"//
	);

	protected static final String OPTION_SSH_KEY_PASS = "SSHKeyPass";
	protected static final String OPTION_SSH_KEY = "SSHKey";
	protected static final String OPTION_BRANCH = "Branch";
	protected static final String OPTION_REMOTE = "Remote";

	private static final Map<String, Object> SCM_OPTIONS;
	static {
		SCM_OPTIONS = new LinkedHashMap<String, Object>();
		SCM_OPTIONS.put(OPTION_BRANCH, CH.m(OPTIONS_DESC, "Branch, default is master"));
		SCM_OPTIONS.put(OPTION_REMOTE, CH.m(OPTIONS_DESC, "Remote, default is origin"));
		SCM_OPTIONS.put(OPTION_SSH_KEY, CH.m(OPTIONS_DESC, "Path to private ssh key file"));
		SCM_OPTIONS.put(OPTION_SSH_KEY_PASS, CH.m(OPTIONS_DESC, "Password if needed for ssh key file", OPTIONS_ENC, true));
	}

	@Override
	public void init(ContainerTools tools, PropertyController props) {

	}

	@Override
	public String getPluginId() {
		return SCM_TYPE;
	}

	@Override
	public AmiScmAdapter createScmAdapter() {
		return new AmiGitScmAdapter();
	}

	@Override
	public String getScmDescription() {
		return SCM_DESC;
	}

	@Override
	public String getSourceControlIcon() {
		return "git.png";
	}

	@Override
	public Map<String, Object> getScmHelpMap() {
		return AmiGitScmPlugin.HELP_MAP;
	}

	@Override
	public String getScmHelp(String option) {
		return SH.s(AmiGitScmPlugin.HELP_MAP.get(option));
	}

	@Override
	public Map<String, Object> getScmOptions() {
		return this.SCM_OPTIONS;

	}

}
