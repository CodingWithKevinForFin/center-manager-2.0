package com.f1.utils.encrypt;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.f1.base.Encrypter;
import com.f1.utils.AH;
import com.f1.utils.ByteHelper;
import com.f1.utils.Cksum;
import com.f1.utils.OH;
import com.f1.utils.SH;

/**
 * Uses base64 encoding for converting bytes to/from strings, this includes values and the secret key
 * <P>
 * 
 * The byte array to encrypt is first prepended with an 8-byte cksum (see unix command) which is used to confirm the key has been properly decoded later on. Keep in mind this
 * checksum isn't used for security, but to ensure user isn't accidently encoding/decoding keys with differing secret keys.
 * 
 * @author rcooke
 * 
 */
public class AesEncrypter_WithCksum implements Encrypter {

	public static final char KEY_DELIM = '|';
	public static final int DATA_HEADER = 0x3F03;
	public static final String KEY_HEADER = "3FENCV1";
	final private SecretKey secretKey;
	final private String secretKeyText;
	final private int secretKeyChecksum;
	final private String secretKeyChecksumString;

	/**
	 * create a new secret key
	 * 
	 * @param bitSize
	 */
	public AesEncrypter_WithCksum(int bitSize) {
		this.secretKey = AesEncryptUtils.generateKeyRandom(bitSize);
		this.secretKeyText = AesEncryptUtils.encode64UrlSafe(this.secretKey);
		this.secretKeyChecksum = (int) Cksum.cksum(this.secretKeyText.getBytes());
		this.secretKeyChecksumString = SH.toString(this.secretKeyChecksum, 16);
		try {
			AesEncryptUtils.encryptAes(OH.EMPTY_BYTE_ARRAY, this.secretKey);
		} catch (Exception e) {
			throw new AesEncrypterException("Unexpected error generating secret key", e);
		}
	}

	/**
	 * Use an existing secret key
	 * 
	 * @param bitSize
	 */
	public AesEncrypter_WithCksum(String text) {
		text = text.trim();
		String[] parts = SH.split('|', text);
		if (parts.length < 1 || OH.ne(KEY_HEADER, parts[0]))
			throw new AesEncrypterException("Secret key not generated with this tool (bad header)");
		if (parts.length < 3)
			throw new AesEncrypterException("Secret key not generated with this tool (bad format)");
		int cksum;
		try {
			cksum = SH.parseInt(parts[1], 16);
		} catch (Exception e) {
			throw new AesEncrypterException("Secret key not generated with this tool (bad checksum format)", e);
		}
		String key = parts[2];
		if (((int) Cksum.cksum(key.getBytes())) != cksum)
			throw new AesEncrypterException("Secret key tampered with (checksum mismatch)");
		try {
			this.secretKey = AesEncryptUtils.decode64(key);
			this.secretKeyText = AesEncryptUtils.encode64UrlSafe(this.secretKey);
			this.secretKeyChecksum = (int) Cksum.cksum(this.secretKeyText.getBytes());
			this.secretKeyChecksumString = SH.toString(this.secretKeyChecksum, 16);
		} catch (IllegalArgumentException e) {
			throw new AesEncrypterException("Secret key not generated with this tool (not base64)", e);
		} catch (Exception e) {
			throw new AesEncrypterException("Secret key not generated with this tool (invalid AES secret key format)", e);
		}
		try {
			AesEncryptUtils.encryptAes(OH.EMPTY_BYTE_ARRAY, this.secretKey);
		} catch (Exception e) {
			throw new AesEncrypterException("Secret key not generated with this tool (Cipher test Failed)", e);
		}
	}

	public static boolean isAesSafeEncrypter(String text) {
		text = text.trim();
		return text.startsWith(KEY_HEADER);
	}

	@Override
	public String encryptString(String unencrypted) {
		if (unencrypted == null)
			return null;
		return encrypt(unencrypted.getBytes());
	}
	@Override
	public String encrypt(byte bytes[]) {
		if (bytes == null)
			return null;
		try {
			final int cksum = (int) Cksum.cksum(bytes);
			bytes = AH.insertBytes(bytes, 0, 4);
			ByteHelper.writeInt((int) cksum, bytes, 0);

			bytes = AesEncryptUtils.encryptAes(bytes, this.secretKey);

			final int cksum2 = (int) Cksum.cksum(bytes);
			bytes = AH.insertBytes(bytes, 0, 10);
			ByteHelper.writeInt(cksum2, bytes, 0);
			ByteHelper.writeShort(DATA_HEADER, bytes, 4);
			ByteHelper.writeInt(this.secretKeyChecksum, bytes, 6);
			return EncoderUtils.encode64UrlSafe(bytes);
		} catch (Exception e) {
			throw new AesEncrypterException("Unexpected error encrypting data", e);
		}
	}

	@Override
	public String decryptString(String encrypted) {
		if (encrypted == null)
			return null;
		return new String(decrypt(encrypted));
	}

	public boolean isEncryptedWithThisKey(String encrypted) {
		if (encrypted == null)
			return false;
		byte[] data;
		try {
			data = EncoderUtils.decode64(encrypted);
		} catch (Exception e) {
			return false;
		}
		if (data.length < 10 || ByteHelper.readShort(data, 4) != DATA_HEADER)
			return false;
		if (ByteHelper.readInt(data, 0) != (int) Cksum.cksum(data, 10, data.length))
			return false;
		if (ByteHelper.readInt(data, 6) != (int) this.secretKeyChecksum)
			return false;
		return true;
	}

	@Override
	public byte[] decrypt(String encrypted) {
		if (encrypted == null)
			return null;
		return decryptBytes(EncoderUtils.decode64(encrypted));
	}
	public byte[] decryptBytes(byte[] data) {
		if (data == null)
			return null;
		if (data.length < 10 || ByteHelper.readShort(data, 4) != DATA_HEADER)
			throw new AesEncrypterException("Data not encrypted with this tool (header mismatch)");
		if (ByteHelper.readInt(data, 0) != (int) Cksum.cksum(data, 10, data.length))
			throw new AesEncrypterException("Data not encrypted with this tool (checksum mismatch)");
		if (ByteHelper.readInt(data, 6) != (int) this.secretKeyChecksum)
			throw new AesEncrypterException("Data encrypted with a different secret key (secret key checksum mismatch)");
		try {
			data = AH.subarray(data, 10, data.length - 10);
			data = AesEncryptUtils.decryptAes(data, this.secretKey);
		} catch (Exception e) {
			throw new AesEncrypterException("Data encrypted with a different secret key (decrypt AES failure)", e);
		}
		if (data.length < 8)
			throw new AesEncrypterException("Data not encrypted with this tool (missing checksum)");
		if (ByteHelper.readInt(data, 0) != (int) Cksum.cksum(data, 4, data.length))
			throw new AesEncrypterException("Data encrypted with a different secret key (checksum mismatch)");
		return Arrays.copyOfRange(data, 4, data.length);
	}

	@Override
	public String getKey64() {
		return KEY_HEADER + '|' + SH.toString(this.secretKeyChecksum, 16) + KEY_DELIM + this.secretKeyText;
	}

	public static String extractSecretKeyChecksumFromCipherText(String ciphertext) {
		byte[] data = EncoderUtils.decode64(ciphertext);
		if (data.length < 10 || ByteHelper.readShort(data, 4) != DATA_HEADER)
			return null;
		if (ByteHelper.readInt(data, 0) != (int) Cksum.cksum(data, 10, data.length))
			return null;
		int checksum = ByteHelper.readInt(data, 6);
		return SH.toString(checksum, 16);
	}

	public int getSecretKeyChecksum() {
		return this.secretKeyChecksum;
	}
	public String getSecretKeyChecksumString() {
		return this.secretKeyChecksumString;
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
