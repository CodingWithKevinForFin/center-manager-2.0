package com.f1.utils.encrypt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.f1.base.Encrypter;
import com.f1.utils.IOH;
import com.f1.utils.OH;

public class AesEncrypter_Simple implements Encrypter {

	final private SecretKey secretKey;
	private String key64;

	public SecretKey getSecretKey() {
		return this.secretKey;
	}

	public AesEncrypter_Simple(String key64) {
		this.key64 = key64;
		this.secretKey = AesEncryptUtils.decode64(key64);
	}
	public AesEncrypter_Simple(File file) {
		try {
			this.key64 = IOH.readText(file);
			this.secretKey = AesEncryptUtils.decode64(key64);
		} catch (IOException e) {
			throw new RuntimeException("Failed to read AES key from file '" + IOH.getFullPath(file) + "'", e);
		}
	}
	@Override
	public String encryptString(String unencrypted) {
		if (unencrypted == null)
			return null;
		return encrypt(unencrypted.getBytes());
	}

	@Override
	public String decryptString(String encrypted) {
		if (encrypted == null)
			return null;
		return new String(decrypt(encrypted));
	}

	@Override
	public String encrypt(byte[] bytes) {
		if (bytes == null)
			return null;
		try {
			return EncoderUtils.encode64UrlSafe(AesEncryptUtils.encryptAes(bytes, this.secretKey));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public byte[] decrypt(String encrypted) {
		if (encrypted == null)
			return null;
		try {
			return AesEncryptUtils.decryptAes(EncoderUtils.decode64(encrypted), this.secretKey);
		} catch (BadPaddingException e) {
			throw new AesEncrypterException("Could not decrypt value with this key", e);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getKey64() {
		return key64;
	}

	@Override
	public int getBitDepth() {
		SecretKeySpec key = (SecretKeySpec) secretKey;
		return key.getEncoded().length * 8;
	}

	@Override
	public OutputStream createEncrypter(OutputStream inner) {
		try {
			final Cipher c = Cipher.getInstance("AES");
			c.init(Cipher.ENCRYPT_MODE, this.secretKey);
			return new FastCipherOutputStream(inner, c);
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
	}

	@Override
	public InputStream createDecrypter(InputStream inner) {
		try {
			final Cipher c = Cipher.getInstance("AES");
			c.init(Cipher.DECRYPT_MODE, this.secretKey);
			return new FastCipherInputStream(inner, c);
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
	}

}
