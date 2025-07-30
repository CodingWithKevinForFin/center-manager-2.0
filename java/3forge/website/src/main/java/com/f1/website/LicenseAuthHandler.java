package com.f1.website;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.f1.base.Clock;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpUtils;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.utils.CH;
import com.f1.utils.Cksum;
import com.f1.utils.Formatter;
import com.f1.utils.IOH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.OH;
import com.f1.utils.RsaEncryptHelper;
import com.f1.utils.SH;
import com.f1.utils.encrypt.RsaEncryptUtils;
import com.f1.utils.structs.Tuple2;

public class LicenseAuthHandler extends AbstractHttpHandler {

	final private Clock clock;

	public LicenseAuthHandler(Clock clock) {
		this.clock = clock;
	}

	@Override
	public void handle(HttpRequestResponse request) throws IOException {
		try {
			TfWebsiteManager manager = WebsiteUtils.getConfig(request.getHttpServer());
			TfWebsiteDbService db = manager.getDb();
			Map<String, String> params = request.getParams();
			String username = params.get("username");
			boolean forgot = params.containsKey("forgot");
			if (SH.isnt(username)) {
				if (forgot)
					WebsiteUtils.redirectToForgotPassword(request, OH.noNull(username, ""), "", "Email Required");
				else
					WebsiteUtils.redirectToLogin(request, OH.noNull(username, ""), "", "Email Required");
				return;
			}
			String password = params.get("password");
			WebsiteUser user = WebsiteUtils.getUser(manager, username, this.clock);
			if (user != null && !user.getEnabled()) {
				manager.audit(request, user.getUser(), "LOGIN_FAILED", "Account locked");
				WebsiteUtils.redirectToLogin(request, OH.noNull(username, ""), "", "Account is locked, please contact support@3forge.com");
				return;
			}

			if (user == null || !user.matchesPassword(password)) {
				String msg = SH.isnt(password) ? "Password required" : (user == null ? "username and password do not match" : "username and password do not match");
				if (user != null)
					manager.audit(request, user.getUser(), "LOGIN_FAILED", msg);
				else
					manager.audit(request, username, "LOGIN_FAILED", msg);

				request.putResponseHeader("message", msg);
			} else {
				if (user.getStatus() != 105) {
					request.putResponseHeader("message", "Your account is not permissioned for generating license keys. Please contact support@3forge.com");
					return;
				}
				manager.audit(request, username, "LOGIN_SUCCESS", null);
				int port = request.getHttpServer().getSecurePort();
				String url = HttpUtils.buildUrl(true, request.getHost(), port, HttpUtils.getCanonical(request.getRequestUri(), "dashboard.htm"), "");
				WebsiteUtils.populateLicenses(manager, user);
				// now generate license for the authenticated user.
				// add parameters for the type of licenses the user is registered for.
				generateLicense(request, manager, user);
			}
		} catch (Exception e) {
			WebsiteUtils.handleException(request, e);
		}

	}
	public void generateLicense(HttpRequestResponse request, TfWebsiteManager config, WebsiteUser user) throws Exception {
		WebsiteUtils.populateLicenses(config, user);
		Formatter formatter = user.getFormatter().getDateFormatter(LocaleFormatter.DATE);
		long now = System.currentTimeMillis();
		final String expiresText;
		String nowText = formatter.format(now);
		if (user.getLicenseExpiresDate() > 0) {
			expiresText = SH.toString(user.getLicenseExpiresDate());
		} else if (user.getLicenseDaysLength() > 0) { // hitting here
			long expires = now + (user.getLicenseDaysLength() * TimeUnit.DAYS.toMillis(1));
			expiresText = formatter.format(expires);
		} else
			expiresText = "NOLICENSE";

		File root = new File(config.getLicensesRoot(), user.getHomeDirectory());
		List<String> appNames = user.getLicenseApps();
		String appInstance = "id=" + user.getId();// + ",user=" + user.getUserName();
		String host = request.getParams().get("host");

		String errors = "";
		if (SH.is(host)) {
			if (host.startsWith("~")) {
				if (OH.ne("3Forge", user.getCompany()) || !SH.endsWith(user.getEmail(), "@3forge.com"))
					errors += "Permission denied, host name can not start with tilda (~). ";
				else if (host.contains(","))
					errors += "Invalid host name, can not start with tilda (~) and have a comma (,). ";
			} else {
				for (String s : SH.split(',', host)) {
					if (!IOH.isValidHostName(s)) {
						errors += "Invalid host name. ";
						break;
					}
				}
			}
		} else {
			errors += "Host required. ";
		}

		if (CH.isEmpty(appNames)) {
			errors += "Select at least one application. ";
		}

		for (String appName : appNames) {
			if (user.getLicenseApps().indexOf(appName) == -1)
				throw new RuntimeException("appName not found:" + appName);
		}

		String appName = SH.join(',', appNames);
		Row latest = null;
		Table t = user.getLicenses();
		for (Row r : t.getRows()) {
			if (r.get("appName").equals(appName) && r.get("appInstance").equals(appInstance) && r.get("host").equals(host)) {
				if (latest == null || OH.lt((String) latest.get("expires"), (String) r.get("expires"))) {
					latest = r;
				}
			}
		}
		File f;
		if (latest != null && OH.ge((String) latest.get("expires"), expiresText)) {
			f = new File((String) latest.get("path"));
		} else {

			for (int i = 1;; i++) {
				f = new File(root, "f1key_" + nowText + (i == 1 ? "" : ("_" + i)) + ".txt");
				if (!f.exists())
					break;
			}
			boolean useOldKey = true;
			if (useOldKey) {
				RSAPrivateKey key = generateKey("Some sort of key geNeratIng text that some1 aught 2 b unable 2 guess!#$").getB();
				String prefix = SH.join('|', "3FKEY", appName, appInstance, host, nowText, expiresText);
				String certKey = RsaEncryptUtils.checkSumString(RsaEncryptUtils.encrypt(key, prefix.getBytes(), false));
				IOH.writeText(f, SH.join('|', prefix, certKey) + SH.NEWLINE);
			} else {
				String text = generateKey(expiresText, nowText, appInstance, host, appName);
				IOH.writeText(f, text);
			}
		}

		WebsiteUtils.populateLicenses(config, user);
		config.audit(request, user.getUser(), "LICENSE CREATED FROM AMI", "User " + user.getUserName() + " has created license from AMI.");
		user.setSelectedLicense(IOH.readText(f).trim());
		String key = IOH.readText(f).trim();
		request.putResponseHeader("message", "success");
		request.putResponseHeader("licenseKey", key);
	}

	public static Tuple2<RSAPublicKey, RSAPrivateKey> generateKey(String text) {
		KeyPairGenerator keyGen;
		try {
			keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(512, new RsaEncryptHelper.TextBasedSecureRandom(text));
			KeyPair pair = keyGen.generateKeyPair();
			return new Tuple2<RSAPublicKey, RSAPrivateKey>((RSAPublicKey) pair.getPublic(), (RSAPrivateKey) pair.getPrivate());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("error generating " + 512 + "-bit RSA key for text: " + SH.password(text), e);
		}
	}

	static private String generateKey(final String expiresText, String nowText, String appInstance, String host, String appName) {
		RSAPrivateKey key = generateKey("Some sort of key geNeratIng text that some1 aught 2 b unable 2 guess!#$").getB();
		String prefix = SH.join('|', "3FORGE_KEY", appName, appInstance, host, nowText, expiresText);
		byte[] t2 = RsaEncryptUtils.encrypt(key, prefix.getBytes(), false);
		String certKey = checkSumString(t2, 16);
		String certKeyWs = salt(certKey, 8);
		String text = prefix + '|' + certKeyWs + SH.NEWLINE;
		return text;
	}

	static private String salt(String encrypt, int cnt) {
		SecureRandom sr = new SecureRandom();
		long t = sr.nextInt(62);
		StringBuilder sb = new StringBuilder(encrypt);
		SH.shuffle(sb, new Random(t));
		for (int i = 2; i < cnt; i++)
			sb.insert(sr.nextInt(sb.length() + 1), SH.toString(sr.nextInt(62), 62));
		int chksum = Math.abs((int) Cksum.cksum(sb.toString().getBytes())) % 62;
		sb.insert(0, SH.toString(t, 62));
		sb.insert(1, SH.toString(chksum, 62));
		return sb.toString();
	}

	static private String checkSumString(byte[] encrypt, int length) {
		StringBuilder sb = new StringBuilder();
		long cksum = Cksum.cksum(encrypt);
		SH.toString(cksum, 62, sb);
		Random r = new Random(cksum);
		while (sb.length() < length)
			SH.toString((long) r.nextInt(62), 62, sb);
		sb.setLength(length);
		SH.shuffle(sb, r);
		return sb.toString();
	}
}
