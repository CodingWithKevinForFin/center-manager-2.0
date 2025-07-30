package com.f1.utils.encrypt;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AesEncryptUtils {

	private static final String AES = "AES";

	private static final ThreadLocal<Cipher> CIPHERS = new ThreadLocal<Cipher>() {
		protected Cipher initialValue() {
			try {
				return Cipher.getInstance(AES);
			} catch (Exception e) {
				throw new RuntimeException("Could not create AES cipher", e);
			}
		};
	};

	private static final ThreadLocal<KeyGenerator> KEY_GENERATORS = new ThreadLocal<KeyGenerator>() {
		protected KeyGenerator initialValue() {
			try {
				return KeyGenerator.getInstance(AES);
			} catch (Exception e) {
				throw new RuntimeException("Could not create AES cipher", e);
			}
		};
	};
	private static final ThreadLocal<SecureRandom> SECURERANDOM_GENERATORS = new ThreadLocal<SecureRandom>() {
		protected SecureRandom initialValue() {
			return new SecureRandom();
		};
	};

	public static Cipher getAesCipherThreadLocal() {
		return CIPHERS.get();
	}

	public static KeyGenerator getAesKeyGeneratorThreadLocal() {
		return KEY_GENERATORS.get();
	}
	public static SecureRandom getSecureRandomThreadLocal() {
		return SECURERANDOM_GENERATORS.get();
	}

	static public SecretKey generateKey(int bitSize) throws Exception {
		KeyGenerator keyGen = getAesKeyGeneratorThreadLocal();
		keyGen.init(bitSize);
		return keyGen.generateKey();
	}
	static public SecretKey generateKeyRandom(int bitSize) {
		KeyGenerator keyGen = getAesKeyGeneratorThreadLocal();
		keyGen.init(bitSize, getSecureRandomThreadLocal());
		return keyGen.generateKey();
	}
	static public StringBuilder encode64(SecretKey key, StringBuilder sink) {
		return EncoderUtils.encode64(key.getEncoded(), sink);
	}
	static public String encode64(SecretKey key) {
		return encode64(key, new StringBuilder()).toString();
	}
	static public StringBuilder encode64UrlSafe(SecretKey key, StringBuilder sink) {
		return EncoderUtils.encode64UrlSafe(key.getEncoded(), sink);
	}
	static public String encode64UrlSafe(SecretKey key) {
		return encode64UrlSafe(key, new StringBuilder()).toString();
	}

	static public SecretKey decode64(CharSequence cs) {
		return new SecretKeySpec(EncoderUtils.decode64IgnoreWhitespace(cs), AES);
	}

	static public SecretKey toAesKey(String s, int length) {
		if (length != 16 && length != 24 && length != 32)
			throw new RuntimeException("Key length must be 16, 24, 32. Not: " + length);
		byte[] r = new byte[length];
		TextBasedSecureRandom.fill(s, 0, r);
		return new SecretKeySpec(r, AES);
	}

	public static byte[] encryptAes(byte[] data, SecretKey key) throws Exception {
		Cipher cipher = getAesCipherThreadLocal();
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encryptedTextBytes = cipher.doFinal(data);
		return encryptedTextBytes;
	}

	public static byte[] decryptAes(byte[] encryptedText, SecretKey key) throws Exception {
		Cipher cipher = getAesCipherThreadLocal();
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(encryptedText);
	}

}