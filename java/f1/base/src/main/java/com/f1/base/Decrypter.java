package com.f1.base;

public interface Decrypter {
	String decryptString(String encrypted);
	byte[] decrypt(String encrypted);
}
