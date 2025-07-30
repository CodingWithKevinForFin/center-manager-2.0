package com.f1.suite.utils.secure;

import javax.crypto.SecretKey;

import com.f1.container.ContainerServices;
import com.f1.utils.encrypt.AesEncryptUtils;

public class AesSecureMessageCrypter extends AbstractSecureMessageCrypter {

	final private SecretKey key;
	private String encodingMethod;

	public AesSecureMessageCrypter(ContainerServices services, String appName, long slippageMs, String key, int depth) {
		super(services, appName, slippageMs);
		this.key = AesEncryptUtils.toAesKey(key, depth);
		this.encodingMethod = "AES-" + (depth * 8);
	}

	@Override
	public String getEncodingMethod() {
		return this.encodingMethod;
	}

	@Override
	protected byte[] decrypt(byte[] payload, long now, String senderName, long senderNow) throws Exception {
		return AesEncryptUtils.decryptAes(payload, key);
	}

	@Override
	protected byte[] encrypt(byte[] payload, long now) throws Exception {
		return AesEncryptUtils.encryptAes(payload, key);
	}

}
