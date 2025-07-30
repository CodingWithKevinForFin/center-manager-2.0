package com.f1.ami.web;

import com.f1.suite.web.HttpWebSuite;

public interface AmiWebProperties {
	String PROPERTY_WEB_TITLE = HttpWebSuite.PROPERTY_WEB_TITLE;
	String PROPERTY_WEB_FAVICON = HttpWebSuite.PROPERTY_WEB_FAVICON;
	//REMOVED: String PROPERTY_LOGGED_OUT_URL = HttpWebSuite.PROPERTY_LOGGED_OUT_URL;
	String PROPERTY_F1_LICENSE_WARNING_DAYS = HttpWebSuite.PROPERTY_F1_LICENSE_WARNING_DAYS;

	String PREFIX_AMISCRIPT_VARIABLE = "amiscript.variable.";//TODO: document. These are applied before entitlement settings

	String PROPERTY_AMI_CHART_THREADING_SUGGESTED_POINTS_PER_THREAD = "ami.chart.threading.suggestedPointsPerThread";
	String PROPERTY_AMI_CHART_THREADING_MAX_THREADS_PER_LAYER = "ami.chart.threading.maxThreadsPerLayer";
	String PROPERTY_AMI_CHART_THREADING_THREAD_POOL_SIZE = "ami.chart.threading.threadPoolSize";
	String PROPERTY_AMI_CHART_ANTIALIAS_CUTOFF = "ami.chart.threading.antialiasCutoff";
	String PROPERTY_AMI_CHART_COMPRESSION_LEVEL = "ami.chart.compressionLevel";//betweeen 0..10
	//FILES
	String PROPERTY_AMI_SAMPLEDATA_FILE = "ami.sampledata.file";
	String PROPERTY_AMI_LICENSE_FILE = "ami.license.file";
	//	String PROPERTY_AMI_USERS_FILE = "ami.users.file";
	String PROPERTY_AMI_CLOUD_DIR = "ami.cloud.dir";
	String PROPERTY_USERS_PATH = "users.path";
	String PROPERTY_AMI_ARCHIVE_OLD_SETTINGS = "ami.web.archive.old.settings";
	String PROPERTY_AMI_SHARED_LAYOUTS_DIR = "ami.shared.layouts.dir";
	String PROPERTY_AMI_WEB_AUTH_PLUGIN_CLASS = "ami.web.auth.plugin.class";
	//	String PROPERTY_AMI_AUTH_NAMESPACE = "ami.auth.namespace";
	String PROPERTY_AMI_AUTH_TIMEOUT_MS = "ami.auth.timeout.ms";
	String PROPERTY_AMI_AUTH_CONCURRENT_RETRY_MS = "ami.auth.concurrent.retry.ms"; // the polling time in milliseconds to wait before retrying 
	String PROPERTY_AMI_SHOW_MENU_OPTION_DATA_STATISTICS = "ami.show.menu.option.datastatistics";
	String PROPERTY_AMI_SHOW_MENU_OPTION_FULL_SCREEN = "ami.show.menu.option.fullscreen";
	String PROPERTY_AMI_PERMITTED_CORS_ORIGINS = "ami.web.permitted.cors.origins";

	//HTTP TIMEOUTS AND UPDATES
	String PROPERTY_AMI_REQUEST_TIMEOUT_SECONDS = "ami.request.timeout.seconds";
	String PROPERTY_AMI_FRAMES_PER_SECOND = "ami.frames.per.second";
	String PROPERTY_AMI_SESSION_TIMEOUT_SECONDS = "ami.session.timeout.seconds";
	String PROPERTY_AMI_SESSION_CHECK_PERIOD_SECONDS = "ami.session.check.period.seconds";
	String PROPERTY_AMI_SESSION_COOKIE_NAME = "ami.session.cookiename";
	String PROPERTY_AMI_WEB_RESOURCES_DIR = "ami.web.resources.dir";
	String PROPERTY_AMI_WEB_RESOURCE_CACHE_TTL = "ami.web.resource.cache.ttl";
	String PROPERTY_AMI_RESOURCE_TIMEOUT_SECONDS = "ami.web.resource.timeout.seconds";
	String PROPERTY_AMI_AJAX_COMPRESSION_LEVEL = "ami.web.ajax.compression.level";
	String PROPERTY_AMI_AJAX_COMPRESSION_MIN_SIZE_BYTES = "ami.web.ajax.compression.min.size.bytes";

	String PROPERTY_AMI_ALLOW_SITE_FRAMED = "ami.allow.site.framed";
	String PROPERTY_AMI_ALLOW_SAME_SITE = "ami.allow.same.site";
	String PROPERTY_AMI_CONTENT_SECURITY_POLICY = "ami.content.security.policy";

	//DEVEPOPEMENT MODE
	String PROPERTY_AMI_MAX_INFO_MESSAGES = "ami.messages.max.info";
	String PROPERTY_AMI_MAX_WARN_MESSAGES = "ami.messages.max.warn";
	String PROPERTY_AMI_AUTOSAVE_FREQUENCY = "ami.autosave.layout.frequency";
	String PROPERTY_AMI_AUTOSAVE_DIR = "ami.autosave.dir";
	String PROPERTY_AMI_AUTOSAVE_COUNT = "ami.autosave.count";

	// LICENSING
	String PROPERTY_AMI_WEB_DISABLE_LICENSE_WIZARD = "ami.web.disable.license.wizard";
	String PROPERTY_AMI_WEB_LICENSE_AUTH_URL = "ami.web.license.auth.url";

	//HTTP connection
	String PROPERTY_HTTP_HOSTNAME = "http.hostname";
	String PROPERTY_HTTP_ALLOW_METHODS = "http.allow.methods";
	String PROPERTY_HTTPS_KEYSTORE_FILE = "https.keystore.file";
	String PROPERTY_HTTPS_KEYSTORE_CONTENTS = "https.keystore.contents.base64";
	String PROPERTY_HTTPS_KEYSTORE_PASSWORD = "https.keystore.password";
	String PROPERTY_HTTP_PORT = "http.port";
	String PROPERTY_HTTP_PORT_BINDADDR = "http.port.bindaddr";
	String PROPERTY_HTTP_PORT_WHITELIST = "http.port.whitelist";
	String PROPERTY_HTTPS_PORT = "https.port";
	String PROPERTY_HTTPS_PORT_BINDADDR = "https.port.bindaddr";
	String PROPERTY_HTTPS_PORT_WHITELIST = "https.port.whitelist";
	//SAML
	String PROPERTY_SAML_IP_URL = "saml.identity.provider.url";
	String PROPERTY_SAML_SP_URL = "saml.service.provider.url";
	String PROPERTY_SAML_ENTITYID = "saml.entityid";
	String PROPERTY_SAML_RELAY_STATE = "saml.relay.state";
	String PROPERTY_SAML_PLUGIN_CLASS = "saml.plugin.class";
	String PROPERTY_SAML_USERNAME_FIELD = "saml.username.field";
	String PROPERTY_SAML_AMI_ISADMIN_FIELD = "saml.ami.isadmin.field";
	String PROPERTY_SAML_AMI_ISADMIN_VALUES = "saml.ami.isadmin.values";
	String PROPERTY_SAML_AMI_ISDEV_FIELD = "saml.ami.isdev.field";
	String PROPERTY_SAML_AMI_ISDEV_VALUES = "saml.ami.isdev.values";
	String PROPERTY_SAML_AMI_GROUP_FIELD = "saml.ami.group.field";
	String PROPERTY_SAML_AMI_GROUPS = "saml.ami.groups";

	String PROPERTY_SAML_IP_CERT_FILE = "saml.identity.provider.cert.file";
	String PROPERTY_SAML_CLOCK_SKEW_MS = "saml.identity.provider.clock.skew.ms";
	String PROPERTY_SAML_NO_CERT_RSA_KEY_STRENGTH = "saml.identity.provider.nocert.rsa.key.strength";
	String PROPERTY_SAML_MESSAGE_LIFETIME_MS = "saml.identity.provider.lifetime.ms";
	String PROPERTY_SAML_DEBUG = "saml.debug";
	String PROPERTY_SAML_NAME_ID_FORMAT = "saml.nameID.format";

	//SSO
	String PROPERTY_SSO_PLUGIN_CLASS = "sso.plugin.class";
	String PROPERTY_ENTITLEMENTS_PLUGIN_CLASS = "entitlements.plugin.class";

	//Entitlements

	//OAUTH
	String PROPERTY_OAUTH_SCOPE = "oauth.scope";
	String PROPERTY_OAUTH_CLIENT_ID = "oauth.client.id";
	String PROPERTY_OAUTH_CLIENT_SECRET = "oauth.client.secret";
	String PROPERTY_OAUTH_REDIRECT_URI = "oauth.redirect.uri";
	String PROPERTY_OAUTH_DYNAMIC_REDIRECT = "oauth.dynamic.redirect";
	String PROPERTY_OAUTH_LOGOUT_REDIRECT_URI = "oauth.logout.redirect.uri";
	String PROPERTY_OAUTH_SERVER_DOMAIN = "oauth.server.domain";
	String PROPERTY_OAUTH_AUTHORIZATION_ENDPOINT = "oauth.authorization.endpoint";
	String PROPERTY_OAUTH_LOGOUT_ENDPOINT = "oauth.logout.endpoint";
	String PROPERTY_OAUTH_SINGLE_LOGOUT_ENABLED = "oauth.single.logout.enabled";
	String PROPERTY_OAUTH_TOKEN_ENDPOINT = "oauth.token.endpoint";
	String PROPERTY_OAUTH_REFRESH_TOKEN_ENDPOINT = "oauth.refresh.token.endpoint";
	String PROPERTY_OAUTH_REFRESH_GRANT_TYPE = "oauth.refresh.grant.type";
	String PROPERTY_OAUTH_REFRESH_SCOPE = "oauth.refresh.scope";
	String PROPERTY_OAUTH_REFRESH_REDIRECT_URI = "oauth.refresh.redirect.uri";
	String PROPERTY_OAUTH_REFRESH_CLIENT_ID = "oauth.refresh.client.id";
	String PROPERTY_OAUTH_REFRESH_CLIENT_SECRET = "oauth.refresh.client.secret";
	String PROPERTY_OAUTH_ACCESS_TOKEN_EXPIRES_IN = "oauth.access.token.expires.in";
	String PROPERTY_OAUTH_CODE_CHALLENGE_METHOD = "oauth.code.challenge.method";
	String PROPERTY_OAUTH_DIGEST_ALGO = "oauth.digest.algo";
	String PROPERTY_OAUTH_USERNAME_FIELD = "oauth.username.field";
	String PROPERTY_OAUTH_AMI_ISADMIN_FIELD = "oauth.ami.isadmin.field";
	String PROPERTY_OAUTH_AMI_ISADMIN_VALUES = "oauth.ami.isadmin.values";
	String PROPERTY_OAUTH_AMI_ISDEV_FIELD = "oauth.ami.isdev.field";
	String PROPERTY_OAUTH_AMI_ISDEV_VALUES = "oauth.ami.isdev.values";
	String PROPERTY_OAUTH_AMI_ROLES_FIELD = "oauth.ami.roles.field";
	String PROPERTY_OAUTH_AMI_DEFAULT_ROLE_FIELD = "oauth.ami.default.role.field";
	String PROPERTY_OAUTH_AMI_ROLES_ENABLED = "oauth.ami.roles.enabled";
	String PROPERTY_OAUTH_AMI_ROLE_ = "oauth.ami.role.";
	String PROPERTY_OAUTH_SESSION_CHECK_PERIOD_SECONDS = "oauth.session.check.period.seconds";
	String PROPERTY_OAUTH_DEBUG = "oauth.debug";
	String PROPERTY_OAUTH_VALIDATE_CERTS = "oauth.validate.certs";

	//OKTA OAUTH
	String PROPERTY_OKTA_OAUTH_SCOPE = "oauth.scope";
	String PROPERTY_OKTA_OAUTH_CLIENT_ID = "oauth.client.id";
	String PROPERTY_OKTA_OAUTH_CLIENT_SECRET = "oauth.client.secret";
	String PROPERTY_OKTA_OAUTH_REDIRECT_URI = "oauth.redirect.uri";
	String PROPERTY_OKTA_OAUTH_SERVER_DOMAIN = "oauth.server.domain";
	String PROPERTY_OKTA_OAUTH_AUTHORIZATION_ENDPOINT = "oauth.authorization.endpoint";
	String PROPERTY_OKTA_OAUTH_TOKEN_ENDPOINT = "oauth.token.endpoint";
	String PROPERTY_OKTA_OAUTH_CODE_CHALLENGE_METHOD = "oauth.code.challenge.method";
	String PROPERTY_OKTA_OAUTH_DIGEST_ALGO = "oauth.digest.algo";
	String PROPERTY_OKTA_OAUTH_USERNAME_FIELD = "oauth.username.field";
	String PROPERTY_OKTA_OAUTH_AMI_ISADMIN_FIELD = "oauth.ami.isadmin.field";
	String PROPERTY_OKTA_OAUTH_AMI_ISADMIN_VALUES = "oauth.ami.isadmin.values";
	String PROPERTY_OKTA_OAUTH_AMI_ISDEV_FIELD = "oauth.ami.isdev.field";
	String PROPERTY_OKTA_OAUTH_AMI_ISDEV_VALUES = "oauth.ami.isdev.values";
	String PROPERTY_OKTA_OAUTH_DEBUG = "oauth.debug";

	String PROPERTY_STYLE_FILES = "ami.style.files";
	//LOOK & FEEL
	String PROPERTY_AMI_LOGIN_PAGE_ANIMATED = "ami.login.page.animated";
	String PROPERTY_AMI_LOGIN_PAGE_TITLE = "ami.login.page.title";
	String PROPERTY_AMI_LOGIN_PAGE_TERMS_AND_CONDITIONS_FILE = "ami.login.page.terms.and.conditions.file";
	String PROPERTY_AMI_LOGIN_DEFAULT_USER = "ami.login.default.user";
	String PROPERTY_AMI_LOGIN_DEFAULT_PASS = "ami.login.default.pass";
	String PROPERTY_AMI_WEB_LOGIN_PAGE_LOGO = "ami.login.page.logo.file";
	String PROPERTY_AMI_WEB_MESSAGE_LICENSE_EXPIRES = "ami.web.message.license.expires";
	String PROPERTY_AMI_WEB_MESSAGE_MAX_SESSIONS = "ami.web.message.max.sessions";

	//HIDDEN
	String PROPERTY_AMI_DEBUG = "ami.debug";//hidden property
	String PROPERTY_AMI_SLOWDOWN_REALTIME_MILLIS = "ami.slowdown.realtime.millis";//hidden,temporary property
	String PROPERTY_AMI_WEB_INDEX_HTML_FILE = "ami.web.index.html.file";
	//String PROPERTY_AMI_WEB_LOGOUT_PAGE = "ami.web.logout.page";
	String PROPERTY_AMI_SIMULATOR_ENABLED = "ami.simulator.enabled";//hidden property
	String PROPERTY_AMI_SUPPORT_LEGACY_AMISCRIPT_VARNAMES = "ami.web.support.legacy.amiscript.varnames";

	String PROPERTY_AMI_WEB_AMISCRIPT_CUSTOM_CLASSES = "ami.web.amiscript.custom.classes";
	String PROPERTY_AMI_WEB_PANELS = "ami.web.panels";
	String PROPERTY_AMI_SCM_PLUGINS = "ami.scm.plugins";
	String PROPERTY_AMI_GUI_SERVICE_PLUGINS = "ami.guiservice.plugins";
	String PREFIX_AMI_WEB_PANEL = "ami.web.panel.";
	String PROPERTY_AMI_WEB_HTTP_DEBUG = "ami.web.http.debug";//off,on,verbose
	String PROPERTY_AMI_WEB_HTTP_DEBUG_MAX_BYTES = "ami.web.http.debug.max.bytes";
	String PROPERTY_AMI_WEB_HTTP_CONNECTIONS_MAX = "ami.web.http.connections.max";
	String PROPERTY_AMI_WEB_HTTP_CONNECTIONS_TIMEOUT_MS = "ami.web.http.connections.timeout.ms";
	//	String PROPERTY_AMI_DEFAULT_TO_ADMIN = "ami.web.default.to.admin";
	String PROPERTY_AMI_WEB_DEFAULT_LAYOUT_SHARED = "ami.web.default.layout.shared";//DEPRECATED, use ami.web.default.layout=SHARED:<layoutname>
	String PROPERTY_AMI_WEB_DEFAULT_LAYOUT = "ami.web.default.layout";//DEPRECATED
	String PROPERTY_AMI_WEB_PRECACHED_TABLES = "ami.web.precached.tables";
	String PROPERTY_AMI_WEB_DATA_FILTER_PLUGIN_CLASS = "ami.web.data.filter.plugin.class";
	String PROPERTY_AMI_WEB_USER_PREFERENCES_PLUGIN_CLASS = "ami.web.user.preferences.plugin.class";
	String PROPERTY_AMI_WEB_EVENT_REAPER_DEFAULT_TIMEOUT = "ami.web.event.reaper.default.timeout";
	String PROPERTY_AMI_FONT_FILES = "ami.font.files";
	String PROPERTY_AMI_FONTS_IN_BROWSER = "ami.fonts.in.browser";

	String VALUE_ALWAYS = "always";
	String VALUE_NEVER = "never";
	String VALUE_DEV = "dev";
	String VALUE_ADMIN = "admin";
	String PROPERTY_AMI_WEB_FILTER_DIALOG_MAX_OPTIONS = "ami.web.filter.dialog.max.options";
	String PROPERTY_AMI_FONT_JAVA_MAPPINGS = "ami.font.java.mappings";
	String PROPERTY_AMI_WEB_HTTP_SLOW_RESPONSE_WARN_MS = "ami.web.http.slow.response.warn.ms";
	String PROPERTY_AMI_WEB_HTTP_SLOW_RESPONSE_WARN_LOG_REQUEST_SIZE = "ami.web.http.slow.response.warn.log.request.size";
	String PROPERTY_AMI_SLOW_AQMISCRIPT_WARN_MS = "ami.slow.amiscript.warn.ms";
	String PROPERTY_AMI_REALTIME_PROCESSOR_PLUGIN = "ami.realtime.processor.plugins";
	String PROPERTY_AMI_WEBMANAGER_PORT = "ami.webmanager.port";
	String PROPERTY_AMI_WEBMANAGER_SSL_KEYSTORE_FILE = "ami.webmanager.ssl.keystore.file";
	String PROPERTY_AMI_WEBMANAGER_SSL_KEYSTORE_TEXT_BASE64 = "ami.webmanager.ssl.keystore.contents.base64";
	String PROPERTY_AMI_WEBMANAGER_SSL_KEYSTORE_PASSWORD = "ami.webmanager.ssl.keystore.password";
	String PROPERTY_AMI_WEBMANAGER_HOST = "ami.webmanager.host";
	String PROPERTY_AMI_WEBMANAGER_TIMEOUT = "ami.webmanager.timeout";

	String PROPERTY_AMI_WEB_SHOW_WAIT_ICON_AFTER_DURATION = "ami.web.show.wait.icon.after.duration";
	String PROPERTY_AMI_WEB_PORTAL_DIALOG_HEADER_TITLE = "ami.web.portal.dialog.header.title";
	String PROPERTY_AMI_WEB_MAX_ROWS_PER_SNAPSHOT = "ami.web.max.rows.per.snapshot";
	String PROPERTY_AMI_WEB_ALLOW_JAVASCRIPT_EMBEDDED_IN_HTML = "ami.web.allow.javascript.embedded.in.html";

	String PROPERTY_AMI_WEB_HEADLESS_FILE = "ami.web.headless.file";

	String PREFIX_PROPERTY_AMI_DEFAULT_PREFIX = "ami.default.user.";
	String PROPERTY_AMI_DEFAULT_ISDEV = "ami.web.default.ISDEV";
	String PROPERTY_AMI_DEFAULT_ISADMIN = "ami.web.default.ISADMIN";
	String PROPERTY_AMI_DEFAULT_MAXSESSIONS = "ami.web.default.MAXSESSIONS";
	String PROPERTY_AMI_DEFAULT_DEFAULT_LAYOUT = "ami.web.default.DEFAULT_LAYOUT";
	String PROPERTY_AMI_DEFAULT_LAYOUTS = "ami.web.default.LAYOUTS";

	//splash screen
	String PROPERTY_AMI_WEB_SPLASHSCREEN_INFO_HTML = "ami.web.splashscreen.info.html";

	//URL Rewriting
	String PROPERTY_REDIRECT_URL = "ami.web.url.override";
	String PROPERTY_URL_HIDE_PORT = "ami.web.url.hide.port";
	String PROPERTY_AMI_WEB_URL_ALWAYS_INCLUDE_LAYOUT = "ami.web.url.always.include.layout";

	//Logging
	String PROPERTY_ACTIVITY_LOG = "ami.web.activity.logging";
}
