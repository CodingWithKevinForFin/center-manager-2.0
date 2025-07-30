package com.f1.base;

import java.io.InputStream;
import java.io.OutputStream;

public interface Encrypter extends Decrypter {
	String encryptString(String unencrypted);
	String encrypt(byte[] unencrypted);
	String getKey64();
	int getBitDepth();

	//NOTE: the stream encryption/decryption may NOT be compatible with the above convenience methods
	public InputStream createDecrypter(InputStream inner);//
	public OutputStream createEncrypter(OutputStream inner);
}
