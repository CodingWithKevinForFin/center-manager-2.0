package com.f1.ami.amicommon;

import java.util.Map;

public interface AmiScmPlugin extends AmiPlugin {
	public static String URL_HELP = "URL";
	public static String CLIENT_HELP = "CLIENT";
	public static String USERNAME_HELP = "USERNAME";
	public static String OPTIONS_HELP = "OPTIONS";
	public static String OPTIONS_DESC = "DESC";
	public static String OPTIONS_ENC = "ENC";
	public static String BASE_PATH_HELP = "BASEPATH";

	public String getScmDescription();
	AmiScmAdapter createScmAdapter();
	public String getSourceControlIcon();
	public Map<String, Object> getScmHelpMap();
	public String getScmHelp(String option);
	public Map<String, Object> getScmOptions();

}
