package com.f1.base;

import java.util.Arrays;

/**
 * This is NOT intended to be a secure means of storing passwords. It's a simple way to replace an, otherwise, string/char[] to avoid accidental logging, and help against
 * mistakingly seeing passwords when honestly debuging/doing an ENV dump. The password is simply stored as a ones compliment
 * 
 * @author robert
 *
 */
public class Password {
	private static final String DEFAULT_MASKED_TEXT = "*******";
	public static final int UNKNOWN_LENGTH = -1;
	private char[] data; //byte

	//If isObfuscated is true then the data is already obfuscated, if false then we need to obfuscate it. 
	public Password(char[] data, boolean isObfuscated) {
		if (data != null) {
			if (!isObfuscated)
				if (PasswordEncrypterManager.INSTANCE != null)
					this.data = PasswordEncrypterManager.INSTANCE.encryptString(new String(data)).toCharArray();
				else {
					this.data = data.clone();
					for (int i = 0; i < data.length; i++)
						this.data[i] ^= 0xffff;
				}
			else
				this.data = data.clone();
		}
	}

	public Password(CharSequence data) {
		this(data.toString().toCharArray(), false);
	}

	/*
	 * Clears out the password, the password object will no longer be usable
	 */

	public String peekAndClear() {
		if (this.data == null)
			return null;
		String password = this.getPasswordString();
		this.clear();
		return password;

	}
	public void clear() {
		if (this.data == null)
			return;
		Arrays.fill(data, (char) 0);
		this.data = null;
	}
	public String toString() {
		return DEFAULT_MASKED_TEXT;
	}

	public String getPasswordString() {
		if (this.data == null)
			return null;
		// All passwords are created as encrypted regardless of isObfuscated boolean
		// Therefore this method should also return decrypted version regardless
		// Previous else statement on True isObfuscated boolean was incorrect 
		// and caused datasource password override bug in Web
		if (PasswordEncrypterManager.INSTANCE != null)
			return PasswordEncrypterManager.INSTANCE.decryptString(new String(this.data));
		else {
			char data[] = this.data.clone();
			for (int i = 0; i < data.length; i++)
				data[i] ^= 0xffff;
			return new String(data);
		}
	}

	public int getLength() {
		return data != null ? data.length : UNKNOWN_LENGTH;
	}

	public char getObfuscatedCharAt(int n) {
		return data[n];
	}

	public static Password valueOf(CharSequence string) {
		return string == null ? null : new Password(string);
	}

	public static String valueFrom(Password password) {
		return password == null ? null : password.getPasswordString();
	}

	public boolean matches(Password pass) {
		return Arrays.equals(data, pass.data);
	}

}
