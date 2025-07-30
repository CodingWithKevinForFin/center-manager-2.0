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
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
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

public class CreateLicenseHttpHandler extends AbstractSecureHttpHandler {

	@Override
	protected void service(HttpRequestResponse request, TfWebsiteManager config, WebsiteUser user) throws Exception {
		WebsiteUtils.populateLicenses(config, user);
		Formatter formatter = user.getFormatter().getDateFormatter(LocaleFormatter.DATE);
		HttpSession session = request.getSession(false);
		long now = System.currentTimeMillis();
		final String expiresText;
		String nowText = formatter.format(now);
		if (user.getLicenseExpiresDate() > 0) {
			expiresText = SH.toString(user.getLicenseExpiresDate());
		} else if (user.getLicenseDaysLength() > 0) {
			long expires = now + (user.getLicenseDaysLength() * TimeUnit.DAYS.toMillis(1));
			expiresText = formatter.format(expires);
		} else
			expiresText = "NOLICENSE";

		File root = new File(config.getLicensesRoot(), user.getHomeDirectory());
		List<String> appNames = (List) request.getParamAsList("appName");
		//		String appInstance = request.getParams().get("appInstance");
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
		session.getAttributes().put("license_error", errors);
		session.getAttributes().put("license_host", SH.noNull(host));
		for (String appName : user.getLicenseApps())
			if (appNames != null && appNames.contains(appName))
				session.getAttributes().put("license_app_" + appName, true);
			else
				session.getAttributes().remove("license_app_" + appName);
		if (!errors.isEmpty()) {
			request.sendRedirect("secure_licenses.htm");
			return;
		}

		for (String appName : appNames) {
			if (user.getLicenseApps().indexOf(appName) == -1)
				throw new RuntimeException("appName not found:" + appName);
		}
		//		if (user.getLicenseInstances().indexOf(appInstance) == -1)
		//			throw new RuntimeException("appInstance not found:" + appInstance);

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
		user.setSelectedLicense(IOH.readText(f).trim());
		config.audit(request, user.getUser(), "CREATE_LICENSE", f.getName());
		request.sendRedirect("secure_licenses.htm");
		//		FastPrintStream op = request.getOutputStream();
		//		ContentType mimetype = ContentType.getTypeByFileExtension("txt");

		//		request.setContentTypeAsBytes((mimetype != null) ? mimetype.getMimeTypeAsBytes() : "application/octet-stream".getBytes());
		//		request.putResponseHeader("Content-Disposition", "attachment; filename=\"f1license.txt\"");
		//		op.write(IOH.readData(f));
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

	public static void main(String a[]) throws IOException {
		try {
			System.out.println(generateKey(a[0], a[1], a[2], a[3], a[4]));
		} catch (Exception e) {
			System.err.println("params are: expires now details host appName");
			e.printStackTrace();
		}
		//		if (a.length != 6)
		//			throw new IllegalArgumentException("usage: appName appInstance host now expires file");
		//		RSAPrivateKey key = generateKey("Some sort of key geNeratIng text that some1 aught 2 b unable 2 guess!#$").getB();
		//		for (String appName : SH.split(',', "app1,app2,app3,app4")) {
		//			for (String appInstance : SH.split(',', "tril,prod")) {
		//				for (String host : SH.split(',', "my.host.com,um.host.net")) {
		//					for (String nowText : SH.split(',', "20160505,20160507")) {
		//						for (String expiresText : SH.split(',', "20160505,20160508")) {
		//							for (int i = 0; i < 10; i++) {
		//								String prefix = SH.join('|', "3FKEY", appName, appInstance, host, nowText, expiresText);
		//								byte[] t = RsaEncryptUtils.encrypt(key, prefix.getBytes(), false);
		//								String certKey = checkSumString(t, 24);
		//								String certKeyWs = salt(certKey, 8);
		//								System.out.println(SH.join('|', prefix, certKeyWs) + " " + isSalt(certKey, certKeyWs, 8));
		//							}
		//						}
		//					}
		//				}
		//			}
		//		}
	}
	private static boolean isSalt(String certKey, String certKeyWs, int len) {
		if (certKey.length() + len != certKeyWs.length())
			return false;
		long num = SH.parseLong(certKeyWs.substring(0, 1), 62);
		int chksum2 = SH.parseInt(certKeyWs.substring(1, 2), 62);
		certKeyWs = certKeyWs.substring(2);
		int chksum = Math.abs((int) Cksum.cksum(certKeyWs.getBytes())) % 62;
		if (chksum != chksum2)
			return false;
		certKey = SH.shuffle(new StringBuilder(certKey), new Random(num)).toString();
		for (int i = 0, l = certKey.length(), n = 0; i < l; i++, n++) {
			n = certKeyWs.indexOf(certKey.charAt(i), n);
			if (n == -1)
				return false;
		}
		return true;
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
