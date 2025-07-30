package com.f1.ami.web.auth;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiCommonProperties;
import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiEncrypter;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.base.Password;
import com.f1.container.ContainerTools;
import com.f1.utils.CachedFile;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;

public class AmiAuthenticatorFileBacked implements AmiAuthenticatorPlugin {

	public static final String DATA_ACCESS_TXT = "data/access.txt";
	private static final Logger log = LH.get();
	private CachedFile file;
	private String encryptMode;
	private ContainerTools properties;

	@Override
	public void init(ContainerTools properties, PropertyController props) {
		File path = props.getOptional(AmiCommonProperties.PROPERTY_USERS_ACCESS_FILE, File.class);
		this.encryptMode = props.getOptionalEnum(AmiCommonProperties.PROPERTY_USERS_ACCESS_FILE_ENCRYPT, null, "off", "password");
		if (path == null)
			path = properties.getOptional(AmiCommonProperties.PROPERTY_USERS_ACCESS_FILE, new File(DATA_ACCESS_TXT));
		if (this.encryptMode == null)
			this.encryptMode = properties.getOptionalEnum(AmiCommonProperties.PROPERTY_USERS_ACCESS_FILE_ENCRYPT, "off", "password");
		if (!path.isFile()) {
			String lines[] = new String[] { "#Each line represents a user, Syntax is: USERNAME|PASSWORD|Key1=Value1|Key2=Value2|....",
					"#Optional keys include: ISDEV, ISADMIN, MAXSESSIONS, DEFAULT_LAYOUT, LAYOUTS, amiscript.variable.<varname>, amiscript.db.variable.<varname>  ",
					"demo|demo123|ISADMIN=true|ISDEV=true|", "test|test123|", "readonly|EFafsdf~d8{X#|AMIDB_PERMISSIONS=read", "sample|sample|ami_layout_shared=sample.ami" };
			try {
				LH.info(log, "User access file '", IOH.getFullPath(path), " not found so creating a default one for demo purposes");
				IOH.writeText(path, SH.join(SH.NEWLINE, lines));
			} catch (IOException e) {
				e.printStackTrace(System.err);
				System.err.println("You need to provide a user acces file (because you're running as sso standalone). ");
				System.err.println("1) Create a file at: " + IOH.getFullPath(path) + "   (or change the users.access.file property)");
				System.err.println("2) populate the file with one line per user with the syntax: user|password");
				System.err.println("Exiting...");
				EH.systemExit(1);
			}

		}
		if ("off".equals(this.encryptMode)) {
			AmiUtils.logSecurityWarning("NON-ENCRYPTED PASSWORDS STORED IN " + IOH.getFullPath(path) + "  (See instructions for "
					+ AmiCommonProperties.PROPERTY_USERS_ACCESS_FILE_ENCRYPT + " option to force encryption)");
		}
		this.properties = properties;
		IOH.ensureReadable(path);
		this.file = new CachedFile(path, 1000);

	}

	@Override
	public AmiAuthResponse authenticate(String namespace, String location, String user, String password) {
		try {
			Tuple2<Password, Map<String, Object>> tuple = findUser(this.file, user);
			if (tuple == null)
				return new BasicAmiAuthResponse.BadUsername();
			String pw = tuple.getA().getPasswordString();
			String pw2;
			if ("password".equals(this.encryptMode)) {
				try {
					AmiEncrypter encrypter = properties.getServices().getService(AmiConsts.SERVICE_ENCRYPTER, AmiEncrypter.class);
					pw2 = encrypter.decrypt(pw);
				} catch (Exception e) {
					LH.warning(log, "Error with encryption for user: " + user, e);
					return new BasicAmiAuthResponse.GeneralError("Password Decoding Error");
				}
			} else
				pw2 = pw;
			if (OH.ne(password, pw2))
				return new BasicAmiAuthResponse.BadPassword();
			return new BasicAmiAuthResponse.Okay(user, tuple.getB());
		} catch (Exception e) {
			LH.warning(log, "error authenticating user: ", user, e);
			return new BasicAmiAuthResponse.GeneralError();
		}
	}
	@Override
	public String getPluginId() {
		return "FILE";
	}

	public static Tuple2<Password, Map<String, Object>> findUser(CachedFile file, String user) {
		for (String line : SH.splitLines(file.getData().getText())) {
			if (SH.isnt(line))
				continue;
			if (line.startsWith("#"))
				continue;
			String[] parts = SH.splitWithEscape('|', '\\', line);
			if (parts.length < 2)
				continue;
			final String un = parts[0];
			final String pw = parts[1];
			if (SH.isnt(un))
				continue;
			else if (OH.ne(user, un))
				continue;
			else {
				final Map<String, Object> attributes = new HashMap<String, Object>();
				for (int i = 2; i < parts.length; i++) {
					final String part = parts[i];
					if (SH.isnt(part))
						continue;
					final String key = SH.beforeFirst(part, '=', null);
					final String val = SH.afterFirst(part, '=', null);
					if (SH.is(val) && SH.is(key))
						attributes.put(key, val);
				}
				return new Tuple2<Password, Map<String, Object>>(new Password(pw), attributes);
			}
		}
		return null;
	}
}
