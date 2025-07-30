package com.f1.ami.amicommon;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.f1.base.Encrypter;
import com.f1.utils.IOH;
import com.f1.utils.encrypt.EncrypterManager;

public class AmiEncrypter {

	final private Encrypter inner;

	public AmiEncrypter(String key64) {
		inner = EncrypterManager.loadEncrypter(key64);
	}

	public AmiEncrypter(File file) {
		try {
			String key64 = IOH.readText(file);
			inner = EncrypterManager.loadEncrypter(key64);
		} catch (IOException e) {
			throw new RuntimeException("Failed to read AES key from file '" + IOH.getFullPath(file) + "'", e);
		}
	}
	public String encrypt(String unencrypted) {
		return inner.encryptString(unencrypted);
	}

	public String decrypt(String encrypted) {
		return inner.decryptString(encrypted);
	}

	public int getBitDetph() {
		return inner.getBitDepth();
	}

	public InputStream decryptStream(InputStream encrypted) {
		return inner.createDecrypter(encrypted);
	}
	public OutputStream encryptStream(OutputStream encrypted) {
		return inner.createEncrypter(encrypted);
	}
}
