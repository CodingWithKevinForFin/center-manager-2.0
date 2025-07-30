package com.f1.utils;

import java.util.Map;

public interface BundledTextFormatter extends Formatter {
	String HELP_SUFFIX = ".HELP";
	int OPTION_ON_KEY_MISSING_RETURN_NULL = 1;
	int OPTION_ON_KEY_MISSING_PRINT_DETAILED = 2;
	int OPTION_ON_KEY_MISSING_DEBUG = 128;
	int OPTION_ON_ARG_MISSING_PRINT_KEY = 4;
	int OPTION_ON_ARG_MISSING_PRINT_NULL = 8;
	int OPTION_ON_ARG_ERROR_EMBED_MESSAGE = 16;
	int OPTION_LOG_EXCEPTIONS = 32;
	int OPTION_LOG_STACKTRACE = 64;
	int OPTION_KEYS_START_WITH_BANGBANG = 128;

	public String formatBundledText(String propertyKey, Object... arguments);

	public String formatBundledTextFromMap(String propertyKey, Map<String, Object> arguments, int options);

	public boolean formatBundledTextFromMap(String propertyKey, Map<String, Object> arguments, int options, StringBuilder sink);

	public void checkForUpdate();
}
