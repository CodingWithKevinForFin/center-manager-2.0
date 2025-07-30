package com.f1.suite.utils.secure;

import java.security.Key;
import java.security.interfaces.RSAPublicKey;

import com.f1.container.ContainerServices;
import com.f1.utils.IOH;
import com.f1.utils.SH;
import com.f1.utils.encrypt.RsaEncryptUtils;

public class RsaSecureMessageCrypter extends AbstractSecureMessageCrypter {
	private static final String ENCODING_METHOD = "RSA";
	final private Key encryptKey;
	final private RSAPublicKey decryptKey;

	public RsaSecureMessageCrypter(ContainerServices services, Key encryptKey, RSAPublicKey decryptKey, String appName, long slippageMs) {
		super(services, appName, slippageMs);
		this.encryptKey = encryptKey;
		this.decryptKey = decryptKey;
	}

	@Override
	public String getEncodingMethod() {
		return ENCODING_METHOD;
	}

	@Override
	protected byte[] decrypt(byte[] payload, long now, String senderName, long senderNow) {
		return RsaEncryptUtils.decrypt(decryptKey, payload);
	}

	@Override
	protected byte[] encrypt(byte[] payload, long now) {
		return RsaEncryptUtils.encrypt(encryptKey, payload, false);
	}

	@Override
	protected String sign(byte[] payload, long now, String senderName) {
		long code = IOH.checkSumBsdLong(payload);
		code = IOH.checkSumBsdLong(code, senderName.getBytes());
		code = code * 31 + now;
		return SH.toString(code, 62);
	}
}
