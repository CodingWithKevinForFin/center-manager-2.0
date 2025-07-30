package com.f1.encoder;

import java.io.File;
import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import com.f1.utils.IOH;
import com.f1.utils.SH;
import com.f1.utils.encrypt.RsaEncryptUtils;
import com.f1.utils.structs.Tuple2;

public class AuthClassEncoder {

	public static void main(String a[]) throws IOException {
		if (a.length < 2)
			System.err.println("Please provide an app name and at least one file in the arguments");
		final String appname = a[0];
		for (int i = 1; i < a.length; i++) {
			String file = a[i];
			File src = new File(file);
			if (!src.isFile()) {
				System.out.println("File not found, skipping: " + src);
				continue;
			}
			File dst = new File(SH.stripSuffix(file, ".class", true) + ".f1class");
			byte[] data = IOH.readData(src);
			data = processEnc(data, appname);
			IOH.writeData(dst, data);
			IOH.delete(src);
			System.out.println("Converted: " + src + "  ==>  " + dst);
		}
	}

	private static byte[] processEnc(byte[] data, String appname) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 20; i++)
			sb.append(i).append(i * 2).append(appname);
		Tuple2<RSAPublicKey, RSAPrivateKey> k = RsaEncryptUtils.generateKey(sb.toString());
		return RsaEncryptUtils.encrypt(k.getB(), data, true);
	}

	public static void main2(String a[]) throws IOException {
		byte[] enc = IOH.readData(new File("/tmp/PropertiesHelper.3fclass"));
		byte[] data2 = AuthLoader.process(enc, "f1webkit");
		System.out.println(new String(data2));

	}
}
