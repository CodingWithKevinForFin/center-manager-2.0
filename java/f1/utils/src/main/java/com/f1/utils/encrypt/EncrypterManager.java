package com.f1.utils.encrypt;

import com.f1.base.Encrypter;

public class EncrypterManager {

	static public Encrypter loadEncrypter(String text) {
		if (AesEncrypter_WithCksum.isAesSafeEncrypter(text))
			return new AesEncrypter_WithCksum(text);
		else
			return new AesEncrypter_Simple(text);
	}

}
