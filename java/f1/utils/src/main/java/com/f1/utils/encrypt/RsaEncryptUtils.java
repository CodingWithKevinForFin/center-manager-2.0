package com.f1.utils.encrypt;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Arrays;
import java.util.zip.Adler32;

import javax.crypto.Cipher;

import com.f1.utils.Cksum;
import com.f1.utils.IOH;
import com.f1.utils.MH;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;

public class RsaEncryptUtils {

	private static final char DELIM = '|';

	static public byte[] encrypt(Key privateKey, byte[] data, boolean random) {
		if (data == null)
			throw new NullPointerException("data");
		if (privateKey == null)
			throw new NullPointerException("privateKey");
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			if (random)
				cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			else
				cipher.init(Cipher.ENCRYPT_MODE, privateKey, new TextBasedSecureRandom(new String(privateKey.getEncoded())));
			int blocks = (data.length + 52) / 53;
			byte[] r = new byte[blocks * 64];
			int remaining = data.length, position = 0;
			for (int i = 0; i < blocks; i++) {
				position += cipher.doFinal(data, i * 53, Math.min(53, remaining), r, position);
				remaining -= 53;
			}
			return Arrays.copyOf(r, position);
		} catch (Exception e) {
			throw new RuntimeException("Error encrypting data for RSA key", e);
		}
	}

	static public byte[] decrypt(Key publicKey, byte[] data) {
		if (data == null)
			throw new NullPointerException("data");
		if (publicKey == null)
			throw new NullPointerException("publicKey");
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			int blocks = (data.length + 63) / 64;
			byte[] r = new byte[blocks * 53 + 11];
			int remaining = data.length, position = 0;
			for (int i = 0; i < blocks; i++) {
				position += cipher.doFinal(data, i * 64, Math.min(64, remaining), r, position);
				remaining -= 64;
			}
			return Arrays.copyOf(r, position);
		} catch (Exception e) {
			throw new RuntimeException("Error decrypting data for RSA private key: " + keyToString(publicKey), e);
		}
	}

	public static Tuple2<RSAPublicKey, RSAPrivateKey> generateKey(String text) {
		KeyPairGenerator keyGen;
		try {
			keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(new RSAKeyGenParameterSpec(512, BigInteger.valueOf(65537)), new TextBasedSecureRandom(text));
			KeyPair pair = keyGen.generateKeyPair();
			return new Tuple2<RSAPublicKey, RSAPrivateKey>((RSAPublicKey) pair.getPublic(), (RSAPrivateKey) pair.getPrivate());
		} catch (Exception e) {
			throw new RuntimeException("error generating " + 512 + "-bit RSA key for text: " + SH.password(text), e);
		}
	}

	public static String keyToString(Key key) {
		if (key instanceof RSAPrivateKey)
			return privateKeyToString((RSAPrivateKey) key);
		else if (key instanceof RSAPublicKey)
			return publicKeyToString((RSAPublicKey) key);
		StringBuilder sb = new StringBuilder();
		sb.append(key.getAlgorithm());
		sb.append(DELIM);
		sb.append(key.getFormat());
		sb.append(DELIM);
		SH.toHex(key.getEncoded(), sb);
		return sb.toString();
	}
	public static String publicKeyToString(RSAPublicKey key) {
		StringBuilder sb = new StringBuilder();
		sb.append(key.getAlgorithm());
		sb.append(DELIM);
		sb.append(key.getFormat());
		sb.append(DELIM);
		sb.append(key.getModulus().toString());
		sb.append(DELIM);
		sb.append(key.getPublicExponent().toString());
		sb.append(DELIM);
		SH.toHex(key.getEncoded(), sb);
		return sb.toString();
	}
	public static String privateKeyToString(RSAPrivateKey key) {
		StringBuilder sb = new StringBuilder();
		sb.append(key.getAlgorithm());
		sb.append(DELIM);
		sb.append(key.getFormat());
		sb.append(DELIM);
		sb.append(key.getModulus().toString());
		sb.append(DELIM);
		sb.append(key.getPrivateExponent().toString());
		sb.append(DELIM);
		SH.toHex(key.getEncoded(), sb);
		return sb.toString();
	}
	public static RSAPublicKey stringToPublicKey(String text) {
		final String[] parts = SH.split(DELIM, text);
		final String aglorithm = parts[0];
		final String format = parts[1];
		final BigInteger modulus = new BigInteger(parts[2]);
		final BigInteger publicExponent = new BigInteger(parts[3]);
		final byte[] encoded = SH.fromHex(parts[4]);
		return new BasicRSAPublicKey(aglorithm, format, encoded, modulus, publicExponent);
	}
	public static RSAPrivateKey stringToPrivateKey(String text) {
		final String[] parts = SH.split(DELIM, text);
		final String aglorithm = parts[0];
		final String format = parts[1];
		final BigInteger modulus = new BigInteger(parts[2]);
		final BigInteger publicExponent = new BigInteger(parts[3]);
		final byte[] encoded = SH.fromHex(parts[4]);
		return new BasicRSAPrivateKey(aglorithm, format, encoded, modulus, publicExponent);
	}

	private static class AbstractRSAKey implements RSAKey, Key {

		final private String algorithm;
		final private String format;
		final private byte[] encoded;
		final private BigInteger modulus;
		final private BigInteger exponent;

		public AbstractRSAKey(String algorithm, String format, byte[] encoded, BigInteger modulus, BigInteger exponent) {
			this.algorithm = algorithm;
			this.format = format;
			this.encoded = encoded;
			this.modulus = modulus;
			this.exponent = exponent;
		}
		@Override
		public String getAlgorithm() {
			return algorithm;
		}

		@Override
		public String getFormat() {
			return format;
		}

		@Override
		public byte[] getEncoded() {
			return encoded;
		}

		@Override
		public BigInteger getModulus() {
			return modulus;
		}
		public BigInteger getExponent() {
			return exponent;
		}
	}

	public static class BasicRSAPublicKey extends AbstractRSAKey implements RSAPublicKey {

		public BasicRSAPublicKey(String algorithm, String format, byte[] encoded, BigInteger modulus, BigInteger exponent) {
			super(algorithm, format, encoded, modulus, exponent);
		}

		@Override
		public BigInteger getPublicExponent() {
			return super.getExponent();
		}

	}

	public static class BasicRSAPrivateKey extends AbstractRSAKey implements RSAPrivateKey {

		public BasicRSAPrivateKey(String algorithm, String format, byte[] encoded, BigInteger modulus, BigInteger exponent) {
			super(algorithm, format, encoded, modulus, exponent);
		}

		@Override
		public BigInteger getPrivateExponent() {
			return super.getExponent();
		}

	}

	public static long checkSum(byte[] data) {
		final Adler32 adler32 = new Adler32();
		adler32.update(data);
		return MH.abs(adler32.getValue());
	}

	final private static int MAXLENGTH = SH.toString(Long.MAX_VALUE, 62).length();

	@Deprecated
	public static String checkSumString(byte[] data) {
		return SH.rightAlign('0', SH.toString(checkSum(data) | (1l << 62), 62), MAXLENGTH, true);
	}

	public static void main(String a[]) throws IOException {
		Tuple2<RSAPublicKey, RSAPrivateKey> key = generateKey("this is a test");
		byte[] data0 = IOH.readData(new File("c:/tmp/TestFile.xlsx"));
		System.err.println(Cksum.cksum(data0));
		byte[] data1 = encrypt(key.getB(), data0, false);
		System.err.println(Cksum.cksum(data1));
		byte[] data2 = decrypt(key.getA(), data1);
		System.err.println(Cksum.cksum(data2));

	}

}
