package com.f1.base;

//Could'nt put it in com.f1.base since encrypter is in com.f1.utils.
public class PasswordEncrypterManager {
	//	private static final Logger log = LH.get();
	public static Encrypter INSTANCE;

	static public void setEncrypter(Encrypter e) {
		INSTANCE = e;
	}

	static public Encrypter getEncrypter() {
		return INSTANCE;
	}

}
