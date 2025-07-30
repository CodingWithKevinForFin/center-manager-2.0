package com.f1.suite.web;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;

import com.f1.base.F1LicenseInfo;
import com.f1.container.Connectable;
import com.f1.container.impl.BasicSuite;
import com.f1.http.HttpHandler;
import com.f1.http.impl.BasicHttpServer;
import com.f1.utils.BasicLocaleFormatter;
import com.f1.utils.EH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.concurrent.FastThreadPool;

public class HttpWebSuite extends BasicSuite {
	public static final String PROPERTY_WEB_TITLE = "web.title";
	public static final String PROPERTY_WEB_FAVICON = "web.favicon";
	public static final String PROPERTY_LOGGED_OUT_URL = "web.logged.out.url";
	public static final String PROPERTY_F1_LICENSE_WARNING_DAYS = "f1.license.warning.days";

	public static final String DEFAULT_TITLE = "3forge Website";
	public static final String DEFAULT_FAVICON = "rsc/favicon.ico";
	public static final String DEFAULT_LOGOUT_URL = "/";
	public static final int DEFAULT_PROPERTY_F1_LICENSE_WARNING_DAYS = 30;

	public static final String ATTRIBUTE_FAV_ICON = "favIcon";
	public static final String ATTRIBUTE_WEB_TITLE = "webTitle";
	public static final String ATTRIBUTE_F1_LICENSE = "f1license_okay";
	public static final String ATTRIBUTE_F1_LICENSE_STATUS = "f1license_status";
	public static final String ATTRIBUTE_F1_LICENSE_TERM_TIME = "f1license_termtime";
	public static final String ATTRIBUTE_F1_LICENSE_WARN_MS = "f1license_warnms";
	public static final String ATTRIBUTE_F1_LICENSE_HOST = "f1license_host";
	public static final String ATTRIBUTE_F1_LICENSE_APP = "f1license_app";
	public static final String ATTRIBUTE_LOGGED_OUT_URL = "loggedOutUrl";

	final private BasicHttpServer httpServer;

	public HttpWebSuite(BasicHttpServer server) {
		this.httpServer = server;
	}

	@Override
	public void init() {
		super.init();
		if (httpServer.getProperties() == null)
			httpServer.setProperties(getServices().getPropertyController());
		if (httpServer.getThreadPool() == null)
			httpServer.setThreadPool(new FastThreadPool(100, "http"));
		httpServer.setDefaultFormatter(getServices().getLocaleFormatter());
		ConcurrentMap<String, Object> attributes = this.httpServer.getAttributes();
		final PropertyController pc = getServices().getPropertyController();
		String loggedOutUrl = SH.trim(pc.getOptional(PROPERTY_LOGGED_OUT_URL, DEFAULT_LOGOUT_URL));
		if (SH.isnt(loggedOutUrl))
			throw new IllegalArgumentException("invalid value for " + PROPERTY_LOGGED_OUT_URL + ": " + loggedOutUrl);
		attributes.putIfAbsent(ATTRIBUTE_LOGGED_OUT_URL, loggedOutUrl);
		attributes.putIfAbsent(ATTRIBUTE_WEB_TITLE, pc.getOptional(PROPERTY_WEB_TITLE, DEFAULT_TITLE));
		attributes.put(ATTRIBUTE_FAV_ICON, pc.getOptional(PROPERTY_WEB_FAVICON, DEFAULT_FAVICON));
		boolean hasLicense = !"DEV".equals(F1LicenseInfo.getLicenseInstance());
		if (!hasLicense) {
			SimpleDateFormat sdf = new SimpleDateFormat(BasicLocaleFormatter.MMDDYYYY_HHMMSS_FORMAT);
			attributes.putIfAbsent(ATTRIBUTE_F1_LICENSE_STATUS, false);
			attributes.putIfAbsent(ATTRIBUTE_F1_LICENSE_TERM_TIME, sdf.format(new java.util.Date((EH.getStartTime() + 7200000))));
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat(BasicLocaleFormatter.MMDDYYYY_FORMAT);
			attributes.putIfAbsent(ATTRIBUTE_F1_LICENSE_STATUS, true);
			int warnOnDays = getTools().getOptional(PROPERTY_F1_LICENSE_WARNING_DAYS, DEFAULT_PROPERTY_F1_LICENSE_WARNING_DAYS);
			try {
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf2 = new SimpleDateFormat(BasicLocaleFormatter.YYYYMMDD_FORMAT);
				cal.setTime(sdf2.parse(F1LicenseInfo.getLicenseEndDate()));
				attributes.putIfAbsent(ATTRIBUTE_F1_LICENSE_TERM_TIME, sdf.format(cal.getTime()));
				cal.add(Calendar.DAY_OF_YEAR, -warnOnDays);
				long warnMs = cal.getTimeInMillis();
				attributes.putIfAbsent(ATTRIBUTE_F1_LICENSE_WARN_MS, warnMs);
			} catch (ParseException e) {
				throw OH.toRuntime(e);
			}
		}
		attributes.putIfAbsent(ATTRIBUTE_F1_LICENSE_HOST, F1LicenseInfo.getLicenseHost());
		attributes.putIfAbsent(ATTRIBUTE_F1_LICENSE_APP, F1LicenseInfo.getLicenseApp());
		this.getContainer().getPartitionController().registerStateGenerator(new DummyWebStateFactory());
	}
	@Override
	public void start() {
		super.start();
		try {
			if (!httpServer.isRunning())
				httpServer.start();
			Executor threadPool = httpServer.getThreadPool();
			if (threadPool instanceof FastThreadPool) {
				if (!((FastThreadPool) threadPool).isRunning())
					((FastThreadPool) threadPool).start();
			}
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	public <T extends HttpHandler> T addHttpHandler(String pattern, T handler) {
		httpServer.addHttpHandler(pattern, handler, false);
		if (handler instanceof Connectable)
			addChild((Connectable) handler);
		return handler;
	}
	public <T extends HttpHandler> T addHttpHandlerStrict(String pattern, T handler) {
		httpServer.addHttpHandlerStrict(pattern, handler, false);
		if (handler instanceof Connectable)
			addChild((Connectable) handler);
		return handler;
	}

}
