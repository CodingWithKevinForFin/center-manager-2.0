package com.f1.ami.webbalancer;

public interface AmiWebBalancerProperties {
	String PROPERTY_AMI_WEBBALANCER_ROUTES_FILE = "ami.webbalancer.routes.file";
	String PROPERTY_AMI_WEBBALANCER_SESSIONS_FILE = "ami.webbalancer.sessions.file";
	String PROPERTY_AMI_WEBBALANCER_CHECK_SESSIONS_PERIOD = "ami.webbalancer.check.sessions.period";
	String PROPERTY_AMI_WEBBALANCER_SERVER_ALIVE_CHECK_PERIOD = "ami.webbalancer.server.alive.check.period";
	String PROPERTY_AMI_WEBBALANCER_SERVER_TEST_URL = "ami.webbalancer.server.test.url";
	String PROPERTY_AMI_WEBBALANCER_SERVER_TEST_URL_PORT = "ami.webbalancer.server.test.url.port";//If the test url is running on a difffernt port
	String PROPERTY_AMI_WEBBALANCER_SERVER_TEST_URL_SECURE = "ami.webbalancer.server.test.url.secure";//is the connection https (false=http)
	String PROPERTY_AMI_WEBBALANCER_SERVER_TEST_URL_PERIOD = "ami.webbalancer.server.test.url.period";
	String PROPERTY_AMI_WEBBALANCER_SESSION_TIMEOUT_PERIOD = "ami.webbalancer.session.timeout.period";

	String PROPERTY_AMI_WEBBALANCER_HTTP_PORT = "ami.webbalancer.http.port";
	String PROPERTY_AMI_WEBBALANCER_HTTP_PORT_BINDADDR = "ami.webbalancer.http.port.bindaddr";
	String PROPERTY_AMI_WEBBALANCER_HTTP_PORT_WHITELIST = "ami.webbalancer.http.port.whitelist";

	String PROPERTY_AMI_WEBBALANCER_HTTPS_KEYSTORE_FILE = "ami.webbalancer.https.keystore.file";
	String PROPERTY_AMI_WEBBALANCER_HTTPS_KEYSTORE_PASSWORD = "ami.webbalancer.https.keystore.password";
	String PROPERTY_AMI_WEBBALANCER_HTTPS_PORT = "ami.webbalancer.https.port";
	String PROPERTY_AMI_WEBBALANCER_HTTPS_PORT_BINDADDR = "ami.webbalancer.https.port.bindaddr";
	String PROPERTY_AMI_WEBBALANCER_HTTPS_PORT_WHITELIST = "ami.webbalancer.https.port.whitelist";
	String PROPERTY_AMI_WEBBALANCER_SERVER_SELECTOR_PLUGIN_CLASS = "ami.webbalancer.server.selector.plugin.class";
	String PROPERTY_AMI_WEBBALANCER_MAX_LOGINS_PER_SERVER = "ami.webbalancer.server.max.server.logins";
}
