package com.f1.msgdirect;

import java.io.File;

import com.f1.base.Factory;
import com.f1.msg.impl.BasicMsgConnectionConfiguration;
import com.f1.utils.IOH;

public class MsgDirectConnectionConfiguration extends BasicMsgConnectionConfiguration {

	private File keystoreFile;
	private byte[] keystoreContents;
	private String keystorePassword;
	private MsgDirectAuthenticator authenticator;
	private boolean forceSsl;

	public MsgDirectConnectionConfiguration(String name) {
		super(name);
	}

	public MsgDirectConnectionConfiguration(String name, Factory<String, String> logNamer) {
		super(name, logNamer);
	}

	public File getKeystoreFile() {
		return keystoreFile;
	}
	public byte[] getKeystoreContents() {
		return keystoreContents;
	}

	public MsgDirectConnectionConfiguration setKeystore(File keystore, String keystorePassword) {
		this.keystoreFile = keystore;
		IOH.assertFileExists(keystore, "ssl keystore");
		this.keystorePassword = keystorePassword;
		return this;
	}
	public MsgDirectConnectionConfiguration setKeystore(byte[] keystoreContents, String keystorePassword) {
		this.keystoreContents = keystoreContents;
		this.keystorePassword = keystorePassword;
		return this;
	}
	public MsgDirectConnectionConfiguration setKeystoreAndForceSsl(File keystore, String keystorePassword) {
		this.keystoreFile = keystore;
		IOH.assertFileExists(keystore, "ssl keystore");
		this.keystorePassword = keystorePassword;
		this.forceSsl = true;
		return this;
	}
	public MsgDirectConnectionConfiguration setKeystoreAndForceSsl(byte[] keystoreContents, String keystorePassword) {
		this.keystoreContents = keystoreContents;
		this.keystorePassword = keystorePassword;
		this.forceSsl = true;
		return this;
	}

	public String getKeystorePassword() {
		return keystorePassword;
	}

	public MsgDirectAuthenticator getAuthenticator() {
		return authenticator;
	}

	public MsgDirectConnectionConfiguration setAuthenticator(MsgDirectAuthenticator authenticator) {
		this.authenticator = authenticator;
		return this;
	}

	public boolean getForceSsl() {
		return forceSsl;
	}

	public MsgDirectConnectionConfiguration setForceSsl(boolean forceSsl) {
		this.forceSsl = forceSsl;
		return this;
	}

}
