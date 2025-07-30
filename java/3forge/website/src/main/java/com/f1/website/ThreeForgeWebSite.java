package com.f1.website;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.f1.base.Clock;
import com.f1.base.ObjectGenerator;
import com.f1.base.Password;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.codegen.CodeCompiler;
import com.f1.codegen.impl.BasicCodeCompiler;
import com.f1.email.EmailClient;
import com.f1.email.EmailClientConfig;
import com.f1.http.HttpUtils;
import com.f1.http.handler.FileSystemHttpHandler;
import com.f1.http.handler.JspHttpHandler;
import com.f1.http.handler.RedirectHandler;
import com.f1.http.impl.BasicHttpServer;
import com.f1.http.impl.HttpServerSocket;
import com.f1.utils.BasicLocaleFormatter;
import com.f1.utils.DBH;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.concurrent.FastThreadPool;
import com.f1.utils.db.Database;

public class ThreeForgeWebSite {

	private static final String OPTION_EMAIL_HOST = "email.host";
	private static final String OPTION_EMAIL_PORT = "email.port";
	private static final String OPTION_EMAIL_USER = "email.user";
	private static final String OPTION_EMAIL_PASS = "email.password";
	private static final String OPTION_EMAIL_ADDR = "email.address";
	private static final String OPTION_EMAIL_3FORGE_ADDR = "email.3forge.address";
	private static final String OPTION_TRIAL_PERIOD_MS = "trial.period.ms";

	public static void main(String t[]) throws IOException, SQLException {

		ContainerBootstrap bs = new ContainerBootstrap(ThreeForgeWebSite.class, t);
		long start = System.currentTimeMillis();
		bs.setConfigDirProperty("./src/main/config");
		// bs.setLoggingOverrideProperty("info");
		bs.startup();
		PropertyController props = bs.getProperties();
		final FastThreadPool tp = new FastThreadPool(500, "T");
		final BasicHttpServer hs = new BasicHttpServer(tp, props,
				new BasicLocaleFormatter(Locale.getDefault(), TimeZone.getDefault(), true, OH.EMPTY_FILE_ARRAY, Collections.EMPTY_MAP));

		hs.putGlobalResponseHeader("X-Frame-Options", "DENY");
		hs.putGlobalResponseHeader("X-Content-Type-Options", "nosniff");
		hs.putGlobalResponseHeader("X-XSS-Protection", "1; mode=block");
		List<String> scriptSrc = new ArrayList<String>();
		scriptSrc.add("https://www.google-analytics.com");
		scriptSrc.add("https://frontend.id-visitors.com");
		scriptSrc.add("https://*.hotjar.com");
		scriptSrc.add("https://www.googletagmanager.com");
		scriptSrc.add("https://tkwr.maillist-manage.com");
		scriptSrc.add("https://www.gartner.com");
		scriptSrc.add("https://maillist-manage.com");
		scriptSrc.add("https://fast.wistia.com");
		scriptSrc.add("https://fast.wistia.net");
		scriptSrc.add("https://vjs.zencdn.net");
		scriptSrc.add("https://code.jquery.com");
		scriptSrc.add("https://*.twitter.com");
		scriptSrc.add("https://snap.licdn.com");
		scriptSrc.add("https://googleads.g.doubleclick.net");
		scriptSrc.add("https://googleadservices.com");
		scriptSrc.add("https://www.google.com");
		scriptSrc.add("https://www.gstatic.com");
		scriptSrc.add("https://cdn.tailwindcss.com/");
		scriptSrc.add("https://unpkg.com/");
		scriptSrc.add("https://cdn.gtranslate.net");
		scriptSrc.add("https://translate.google.com");
		scriptSrc.add("https://translate.googleapis.com");
		scriptSrc.add("https://translate-pa.googleapis.com");
		scriptSrc.add("https://js.sentry-cdn.com");
		scriptSrc.add("https://*.wistia.com");
		scriptSrc.add("https://*.wistia.net");
		scriptSrc.add("https://*.sentry-cdn.com/");
		scriptSrc.add("https://embedwistia-a.akamaihd.net");
		scriptSrc.add("https://cloud.umami.is");
		scriptSrc.add("https://*.posthog.com");
		scriptSrc.add("https://*.clarity.ms");
		scriptSrc.add("https://*.lftrack.com");
		scriptSrc.add("https://*.leadfeeder.com");
		scriptSrc.add("https://*.lfeeder.com");
		hs.putDefaultResponseHeader(HttpUtils.CONTENT_SECURITY_POLICY,
				"base-uri 'self'; form-action 'self'; frame-ancestors 'self'; media-src 'self' blob: https://fast.wistia.net https://*.wistia.com https://embedwistia-a.akamaihd.net; connect-src 'self' https://*.wistia.com https://distillery.wistia.com https://*.litix.io https://www.google-analytics.com https://*.posthog.com https://*.clarity.ms lftrack.com https://*; img-src 'self' https://*.wistia.com https://*.lftrack.com https://*.leadfeeder.com https://*.lfeeder.com data: https://*; frame-src 'self' https://*.wistia.com https://googletagmanager.com https://*; script-src 'self' 'nonce-static-nonce-value' 'strict-dynamic''unsafe-eval' "
						+ SH.join(' ', scriptSrc) + "; upgrade-insecure-requests; ");

		final int httpPort = props.getOptional("port.http", 80);
		final int httpListenPort = props.getOptional("listen.port.http", 80);
		File faqRoot = props.getRequired("faq.root", File.class);

		final HttpServerSocket socket2 = new HttpServerSocket(httpPort);
		socket2.setListenPort(httpListenPort);

		hs.setDebugging(props.getOptional("debug", false), 10000);
		hs.setErrorHandler(new ErrorHandler());
		hs.setResourceNotFoundHandler(new ErrorHandler());
		final Integer httpsListenPort = props.getOptional("listen.port.https", Caster_Integer.INSTANCE);
		if (props.getOptional("port.https") != null) {
			hs.putGlobalResponseHeader("Strict-Transport-Security", "max-age=63072000; includeSubDomains; preload");
			final int httpsPort = props.getOptional("port.https", 443);
			final File store = props.getRequired("keystore.file", File.class);
			final String pass = props.getRequired("keystore.password");
			final HttpServerSocket socket = new HttpServerSocket(httpsPort, store, pass);
			socket.setListenPort(httpsListenPort);
			hs.addServerSocket(socket);
		}
		File dataDir = props.getOptional("data.dir", new File("./src/main/data"));
		File pressReleaseDir = props.getOptional("press.release.dir", new File("./src/main/data/pressReleaseDocs"));
		//		FaqManager faqManager = new FaqManager(faqRoot);
		//		List<FaqAnswer> answers = new ArrayList<FaqAnswer>();
		//		faqManager.reset(answers);

		hs.addServerSocket(socket2);
		tp.start();
		CodeCompiler compiler = new BasicCodeCompiler(".tmpjsp");
		String dbUrl = props.getRequired("db.url");
		String dbPassword = props.getRequired("db.password");
		Database dbsource = DBH.createPooledDataSource(dbUrl, dbPassword);
		/*		String dbApplicantUrl = props.getRequired("db.applicanturl");
				String dbApplicantPassword = props.getRequired("db.applicantpassword");
				Database dbApplicantSource = DBH.createPooledDataSource(dbApplicantUrl, dbApplicantPassword);*/
		ObjectGenerator generator = bs.getCodeGenerator();
		File sqlpath = props.getOptional("sql.path", new File("./src/main/scripts/sql"));
		Clock clock = bs.createClock();

		File webContentDir = props.getOptional("web.content.dir", new File("WebContent/v3_2020"));
		File documentationDir = new File("WebContent");

		String pagesToRedirectTo404 = props.getOptional("redirect.404.pages");

		System.out.println("params: " + dbsource.toString() + generator.toString() + sqlpath.toString() + clock.toString() + dataDir.toString() + pressReleaseDir.toString());
		TfWebsiteManager wm = initWebManager(props, new TfWebsiteDbService(dbsource, generator, sqlpath), clock, dataDir, pressReleaseDir);
		/*		TfApplicantManager am = initTfApplicantManager(new TfWebsiteDbService(dbApplicantSource, generator, sqlpath));*/

		/*		System.out.println(wm);*/
		hs.setSecurityPolicy(wm);
		hs.putGlobalResponseHeader("Access-Control-Allow-Origin", "*");
		hs.getAttributes().put(TfWebsiteManager.NAME, wm);

		File tutorialsDir = props.getOptional("tutorials.dir", File.class);
		if (tutorialsDir != null) {
			if (!tutorialsDir.isDirectory())
				throw new RuntimeException("tutorials.dir points to non-existent directory: " + IOH.getFullPath(tutorialsDir));
			hs.addHttpHandlerStrict("/tutorials/*", new SecureFileSystemHttpHandler(tutorialsDir, "/tutorials/", 1000, compiler), false);
		}
		final File docsDir = props.getOptional("docs.dir", File.class);
		if (docsDir != null)
			hs.addHttpHandlerStrict("/docs/*", new FileSystemHttpHandler(false, docsDir, "/docs/", 1000, "index.html"), false);

		hs.addHttpHandler("/contactUs$", new ContactUsHttpHandler(), false);
		hs.addHttpHandler("/requestDemo$", new RequestDemoHttpHandler(), false);
		hs.addHttpHandler("/downloadlicense$", new DownloadLicenseHttpHandler(), false);
		//hs.addHttpHandler("/downloadTrial$", new DownloadTrialHttpHandler(), false);
		hs.addHttpHandler("/createLicense$", new CreateLicenseHttpHandler(), false);
		hs.addHttpHandler("/createAccount$", new CreateAccountHttpHandler(), false);
		hs.addHttpHandler("/updateAccount$", new UpdateAccountHttpHandler(), false);
		hs.addHttpHandler("/contactSupport$", new ContactSupportHttpHandler(), false);
		hs.addHttpHandler("/secure_licenses$", new LicensesHttpHandler(), false);
		hs.addHttpHandler("/verify$", new VerifyHttpHandler(), false);
		//hs.addHttpHandler("/reset$", new ResetHttpHandler(), false);
		hs.addHttpHandler("/resend$", new ResendHttpHandler(), false);
		//hs.addHttpHandler("/delete$", new DeleteHttpHandler(), false);
		hs.addHttpHandler("/upload$", new UploadHttpHandler(), false);
		hs.addHttpHandler("/attach$", new UploadAttachmentHttpHandler(), false);
		hs.addHttpHandler("/changePassword$", new ChangePasswordHttpHandler(), false);
		hs.addHttpHandler("/resetPassword$", new ResetPasswordHttpHandler(), false);
		hs.addHttpHandler("/download$", new DownloadHttpHandler(), false);
		hs.addHttpHandler("/sendDownloadLink$", new PressReleaseEmailHttpHandler(), false);
		hs.addHttpHandler("/downloadPressRelease$", new PressReleaseDownloadHttpHandler(), false);
		hs.addHttpHandler("/downloadPressReleaseDirect$", new PressReleaseDirectDownloadHttpHandler(), false);
		hs.addHttpHandler("/logout$", new LogoutHttpHandler(clock), false);
		hs.addHttpHandler("/login_$", new LoginHttpHandler(clock), false);
		hs.addHttpHandler("/loginResetLink$", new LoginResetLinkHttpHandler(clock), false);
		hs.addHttpHandler("/authenticate$", new LicenseAuthHandler(clock), false);
		hs.addHttpHandler("/spoof$", new SpoofHttpHandler(), false);
		//		hs.addHttpHandler("/faq$", new FaqHttpHandler(faqManager), false);
		hs.addHttpHandler("/secure_files$", new FilesHttpHandler(), false);
		hs.addHttpHandler("/getContactFormKey$", new ContactFormKeyHandler(), false);
		hs.addHttpHandler("/onJobRoleClicked$", new JobRoleHttpHandler(), false);
		//		hs.addHttpHandler("/faqanswer/", new FileSystemHttpHandler(false, faqRoot, "/faqanswer/", 1000, "index.html"), true);
		hs.addHttpHandler("/faqanswer/", new FaqAnswerHttpHandler(false, faqRoot, "/faqanswer/", 1000, "index.html"), true);
		hs.addHttpHandler("/resources/", new FileSystemHttpHandler(true, webContentDir, "", 1000, "index.html"), false);
		hs.addHttpHandler("documentation/*", new FileSystemHttpHandler(true, documentationDir, "", 1000, "index.htm"), false);
		//hs.addHttpHandler("/finance/*", new FileSystemHttpHandler(true, new File("WebContent/v3_2020/finance"), "", 1000, "/finance"), true);
		//hs.addHttpHandler("/finance/*", new FileSystemHttpHandler(false, new File("WebContent/v3_2020/finance"), "/finance/", 1000, "index.html"), false);
		//hs.addHttpHandler("/finance/*", new FileSystemHttpHandler(false, new File("WebContent/v3_2020/finance"), "/finance/", 1000, "index.html", false), false);

		hs.addHttpHandler("/submitApplication$", new SubmitApplicationHttpHandler(), false);
		//hs.addHttpHandler("/secure_account$", new UploadManager(), false);

		if (SH.is(pagesToRedirectTo404)) {
			String[] pages = SH.split(',', pagesToRedirectTo404);
			for (String page : pages) {
				String test = page.trim();
				hs.addHttpHandler(test, new RedirectHandler("404.html"), false);
			}
		}
		hs.addHttpHandler("wiki$", new RedirectHandler("https://doc.3forge.com/supported_software/"), false);
		hs.addHttpHandler("products", new RedirectionHttpHandler("https://3forge.com/platform-overview"), false);
		hs.addHttpHandler("finance/operational-oversight", new RedirectionHttpHandler("../operational-oversight"), false);
		hs.addHttpHandler("finance/regulatory-compliance-and-business-reporting", new RedirectionHttpHandler("../regulatory-compliance-and-business-reporting"), false);
		hs.addHttpHandler("finance/risk-and-business-intelligence", new RedirectionHttpHandler("../risk-and-business-intelligence"), false);
		hs.addHttpHandler("stac_report$", new RedirectHandler("stac_report.pdf"), false);
		hs.addHttpHandler("support$", new RedirectHandler("why-3forge"), false);
		hs.addHttpHandler("support.html$", new RedirectHandler("why-3forge"), false);
		hs.addHttpHandler(".htm$", new JspHttpHandler(true, webContentDir, "", 1000, "index.html", compiler), false);
		hs.addHttpHandler(".html$", new JspHttpHandler(true, webContentDir, "", 1000, "index.html", compiler), false);
		hs.addHttpHandler("(!(\\.))&(...$)&(!(\\/$))", new JspHttpHandler(true, webContentDir, "", 1000, "index.html", compiler).setDefaultExtension("html"), false);
		hs.addHttpHandler("*", new FileSystemHttpHandler(true, webContentDir, "", 1000, "index.html"), false);
		hs.start();
		long now = System.currentTimeMillis();
		System.out.println("Server started in " + (now - start) + " milliseconds... Listening for http on " + httpListenPort + ", https on " + httpsListenPort);

	}
	static private TfWebsiteManager initWebManager(PropertyController pc, TfWebsiteDbService db, Clock Clock, File dataDir, File pressReleaseDir) {
		String emailHost = pc.getOptional(OPTION_EMAIL_HOST);
		String emailAddress = pc.getRequired(OPTION_EMAIL_ADDR);
		final EmailClient emailClient;
		if (SH.isnt(emailHost)) {
			System.err.println("No email server specified.  Not sending email notifications. Please specify property: " + OPTION_EMAIL_HOST);
			emailClient = null;
		} else {
			EmailClientConfig config = new EmailClientConfig();
			config.setHost(emailHost);
			config.setPort(pc.getRequired(OPTION_EMAIL_PORT, Integer.class));
			config.setEnableAuthentication(true);
			config.setEnableStartTLS(true);
			config.setEnableSSL(true);
			config.setUsername(pc.getRequired(OPTION_EMAIL_USER));
			System.out.println("option email pass" + Password.valueOf(pc.getRequired(OPTION_EMAIL_PASS)));
			config.setPassword(Password.valueOf(pc.getRequired(OPTION_EMAIL_PASS)));
			System.out.println("option email pass" + Password.valueOf(pc.getRequired(OPTION_EMAIL_PASS)));
			emailClient = new EmailClient(config);
		}
		String resourcesUrl = pc.getRequired("resources.url");
		String emailOverride = pc.getRequired("email.override");
		if ("off".equals(emailOverride))
			emailOverride = null;
		boolean allowWeakPasswords = pc.getOptional("allow.weak.passwords", Boolean.FALSE);
		String threeForgeEmail = pc.getOptional(OPTION_EMAIL_3FORGE_ADDR, emailAddress);
		TfWebsiteManager r = new TfWebsiteManager(db, dataDir, pressReleaseDir, emailClient, Clock, resourcesUrl, emailOverride, threeForgeEmail, allowWeakPasswords);
		r.setHostName(pc.getRequired("http.hostname"));
		r.setUsingAwsLoadbalancer(pc.getOptional("using.aws.loadbalancer", Boolean.FALSE));
		r.setTrialPeriodMs(pc.getOptional(OPTION_TRIAL_PERIOD_MS, 86400000L * 30L));
		r.setEmailAddress(emailAddress);
		String filesRoot = pc.getRequired("filesRoot");
		if (filesRoot == null)
			throw new RuntimeException("usersHomeRoot property missing");
		String userConfigFile = pc.getRequired("userConfigFile");
		if (userConfigFile == null)
			throw new RuntimeException("userConfigFile property missing");
		r.setFilesRoot(filesRoot);
		String licensesRoot = pc.getRequired("licensesRoot");
		if (licensesRoot == null)
			throw new RuntimeException("licensesRoot property missing");
		r.setLicensesRoot(licensesRoot);
		r.setUserConfigFile(userConfigFile);
		return r;
	}
	/*	static private TfApplicantManager initTfApplicantManager(TfWebsiteDbService dbApplicant) {
			return new TfApplicantManager(dbApplicant);
	
		}*/

}
