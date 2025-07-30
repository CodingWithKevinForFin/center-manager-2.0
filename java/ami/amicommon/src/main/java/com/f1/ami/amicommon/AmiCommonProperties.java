package com.f1.ami.amicommon;

public interface AmiCommonProperties {
	public static final String DEFAULT_AMISCRIPT_DEFAULT_TIMEOUT = "60 seconds";
	public static final int DEFAULT_AMISCRIPT_LIMIT = 10000;
	public static int DEFAULT_STATS_PERIOD_MS = 20000;
	public static int DEFAULT_STATS_RETENTION_MS = 3600 * 1000 * 24;
	public static String DEFAULT_USER_TIMEZONE = "EST5EDT";
	public static double DEFAULT_AMI_WARNING_MEMORY_MULTIPLIER = 1;

	public static String PROPERTY_AMI_PERSIST_AES_KEY_TEXT = "ami.aes.key.text";
	public static String PROPERTY_AMI_PERSIST_AES_KEY_FILE = "ami.aes.key.file";
	public static String PROPERTY_AMI_PERSIST_AES_KEY_STRENGTH = "ami.aes.key.strength";
	public static String PROPERTY_AMI_COMPONENTS = "ami.components";
	public static String PROPERTY_AMI_LOG_QUERY_MAX_CHARS = "ami.log.query.max.chars";
	public static String PROPERTY_AMI_STATS_TABLE_PERIOD_MS = "ami.stats.period.millis";
	public static String PROPERTY_AMI_STATS_TABLE_RETENTION_MS = "ami.stats.retention.millis";
	public static String PROPERTY_AMI_WARNING_MEMORY_MULTIPLIER = "ami.warning.memory.multiplier";
	public static String PROPERTY_AMI_DEFAULT_USER_TIMEZONE = "ami.default.user.timezone";
	public static String PROPERTY_AMI_AMILOG_STATS_PERIOD = "ami.amilog.stats.period";
	public static String PROPERTY_AMI_AMISCRIPT_DEFAULT_TIMEOUT = "ami.amiscript.default.timeout";
	public static String PROPERTY_AMI_AMISCRIPT_DEFAULT_LIMIT = "ami.amiscript.default.limit";

	public static String PROPERTY_AMI_SERVICE_RESOLVERS = "ami.naming.service.resolvers";

	//CONNECTIVITY
	public static String PROPERTY_AMI_CENTER_PORT = "ami.center.port";
	public static String PROPERTY_AMI_CENTER_HOST = "ami.center.host";
	public static String OPTION_AMI_CENTERS = "ami.centers";

	public static final String PROPERTY_AMI_CENTER_PREFIX = "ami.center.";
	public static final String PROPERTY_AMI_CENTER_SUFFIX_SSL_KEY_PASSWORD = ".ssl.keystore.password";
	public static final String PROPERTY_AMI_CENTER_SUFFIX_SSL_KEY_TEXT_BAS64 = ".ssl.keystore.contents.bas64";
	public static final String PROPERTY_AMI_CENTER_SUFFIX_SSL_KEY_FILE = ".ssl.keystore.file";
	public static final String PROPERTY_AMI_CONSOLE_AUTH_PLUGIN_CLASS = "ami.admin.auth.plugin.class";
	public static final String PROPERTY_AMI_AUTH_PLUGIN_CLASS = "ami.auth.plugin.class";
	public static final String PROPERTY_AUTH_SUFFIX_CACHE_DURATION = ".cache.duration";
	public static String PROPERTY_USERS_ACCESS_FILE = "users.access.file";
	public static String PROPERTY_USERS_ACCESS_FILE_FOR_ENTITLEMENTS = "users.access.file.for.entitlements";//off, on, required=user must exist, force=properties in file will override those from entitlements, required_force=both
	public static String PROPERTY_USERS_ACCESS_FILE_ENCRYPT = "users.access.file.encrypt.mode";
	public static String PROPERTY_AMI_PASSWORD_ENCRYPTER_CLASS = "ami.password.encrypter.class";
	public static String PROPERTY_AMI_PASSWORD_ENCRYPTER_CHARSET = "ami.password.encrypter.charset";

	public String PROPERTY_REST_ON_WEB_PORT = "ami.rest.uses.web.port";//If true use same port as web stuff
	public String PROPERTY_REST_HTTP_PORT = "ami.rest.http.port";
	public String PROPERTY_REST_HTTP_PORT_BINDADDR = "ami.rest.http.port.bindaddr";
	public String PROPERTY_REST_HTTP_PORT_WHITELIST = "ami.rest.http.port.whitelist";
	public String PROPERTY_REST_HTTPS_PORT = "ami.rest.https.port";
	public String PROPERTY_REST_HTTPS_PORT_BINDADDR = "ami.rest.https.port.bindaddr";
	public String PROPERTY_REST_HTTPS_PORT_WHITELIST = "ami.rest.https.port.whitelist";
	public String PROPERTY_REST_HTTPS_KEYSTORE_CONTENTS = "ami.rest.https.keystore.contents.base64";
	public String PROPERTY_REST_HTTPS_KEYSTORE_PASSWORD = "ami.rest.https.keystore.password";
	public String PROPERTY_REST_HTTPS_KEYSTORE_FILE = "ami.rest.https.keystore.file";
	public String PROPERTY_REST_PLUGINS = "ami.rest.plugin.classes";
	public String PROPERTY_REST_AUTH_PLUGIN_CLASS = "ami.rest.auth.plugin.class";
	public String PROPERTY_REST_SHOW_ERRORS = "ami.rest.show.errors";
	public String PROPERTY_REST_SHOW_ENDPOINTS = "ami.rest.show.endpoints";
}
