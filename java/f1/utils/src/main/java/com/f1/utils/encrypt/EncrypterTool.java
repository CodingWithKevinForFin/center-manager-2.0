package com.f1.utils.encrypt;

import java.io.File;
import java.io.IOException;

import javax.crypto.SecretKey;

import com.f1.base.Encrypter;
import com.f1.utils.FastByteArrayDataInputStream;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.IOH;
import com.f1.utils.SH;

public class EncrypterTool {

	//java com.f1.utils.encrypt.EncrypterTool --aes_generate /path/to/secret.aes bitsize
	//java com.f1.utils.encrypt.EncrypterTool --aes_encrypt  /path/to/secret.aes text [text2 text3 ...]
	//java com.f1.utils.encrypt.EncrypterTool --aes_decrypt  /path/to/secret.aes text [text2 text3 ...]
	//java com.f1.utils.encrypt.EncrypterTool --aes_encrypt_file  /path/to/secret.aes /path/to/file
	//java com.f1.utils.encrypt.EncrypterTool --aes_decrypt_file  /path/to/secret.aes /path/to/file/to/write/to text
	public static void main(String[] args) {
		handle(args);
	}
	public static boolean handle(String a[]) {
		if (a.length < 1) {
			printUsageAndExit("");
			return true;
		}
		String cmd = a[0];
		if ("--aes_generate_with_cksum".equalsIgnoreCase(cmd)) {
			if (a.length != 3)
				printUsageAndExit("--aes_generate_with_cksum expecting 2 arguments");
			String file = a[1];
			String bits = a[2];
			int bitsInt;
			try {
				bitsInt = SH.parseInt(bits);
			} catch (Exception e) {
				printUsageAndExit("bits must be a number: " + bits);
				return true;
			}
			File f = new File(file);
			if (f.exists()) {
				printErrorAndExit("File already exists: " + IOH.getFullPath(f));
				return true;
			}
			Encrypter encrypter;
			try {
				encrypter = new AesEncrypter_WithCksum(bitsInt);
			} catch (Exception e) {
				e.printStackTrace();
				printErrorAndExit("Could not Create AES key: " + e.getMessage());
				return true;
			}
			try {
				IOH.writeText(f, encrypter.getKey64() + SH.NEWLINE);
			} catch (Exception e) {
				printErrorAndExit("Could not write to '" + IOH.getFullPath(f) + "': " + e.getMessage());
				return true;
			}
			System.out.println("Created " + bitsInt + "-bit AES secret file: " + IOH.getFullPath(f));
		} else if ("--aes_generate".equalsIgnoreCase(cmd)) {
			if (a.length != 3)
				printUsageAndExit("--aes_generate expecting 2 arguments");
			String file = a[1];
			String bits = a[2];
			int bitsInt;
			try {
				bitsInt = SH.parseInt(bits);
			} catch (Exception e) {
				printUsageAndExit("bits must be a number: " + bits);
				return true;
			}
			File f = new File(file);
			if (f.exists()) {
				printErrorAndExit("File already exists: " + IOH.getFullPath(f));
				return true;
			}
			Encrypter encrypter;
			try {
				SecretKey tmpKey = AesEncryptUtils.generateKeyRandom(bitsInt);
				String key64 = AesEncryptUtils.encode64UrlSafe(tmpKey);
				encrypter = new AesEncrypter_Simple(key64);
			} catch (Exception e) {
				e.printStackTrace();
				printErrorAndExit("Could not Create AES key: " + e.getMessage());
				return true;
			}
			try {
				IOH.writeText(f, encrypter.getKey64() + SH.NEWLINE);
			} catch (Exception e) {
				printErrorAndExit("Could not write to '" + IOH.getFullPath(f) + "': " + e.getMessage());
				return true;
			}
			System.out.println("Created " + bitsInt + "-bit AES secret file: " + IOH.getFullPath(f));
		} else if ("--aes_encrypt".equalsIgnoreCase(cmd)) {
			if (a.length < 2)
				printUsageAndExit("--aes_encrypt expecting file name");
			Encrypter ae = loadEncrypter(a[1]);
			for (int i = 2; i < a.length; i++)
				System.out.println(ae.encryptString(a[i]));
		} else if ("--aes_decrypt".equalsIgnoreCase(cmd)) {
			if (a.length < 2)
				printUsageAndExit("--aes_decrypt expecting file name");
			Encrypter ae = loadEncrypter(a[1]);
			for (int i = 2; i < a.length; i++) {
				try {
					System.out.println(ae.decryptString(a[i]));
				} catch (AesEncrypterException e) {
					System.err.println("Token #" + (i - 1) + " is invalid: " + e.getMessage());
				} catch (Exception e) {
					System.err.println("Token #" + (i - 1) + " is invalid: ");
					e.printStackTrace(System.err);
				}
			}
		} else if ("--aes_decrypt_file".equalsIgnoreCase(cmd)) {
			if (a.length != 4)
				printUsageAndExit("--aes_decrypt_file expecting key file, encrypted source file, target file");
			Encrypter ae = loadEncrypter(a[1]);
			byte[] inData;
			try {
				inData = IOH.readData(new File(a[2]));
			} catch (IOException e) {
				System.err.println("Failed to load file: " + e.getMessage());
				System.exit(2);
				return true;
			}
			File out = new File(a[3]);
			if (out.exists()) {
				System.err.println("File already exists: " + IOH.getFullPath(out));
				System.exit(2);
				return true;
			}
			byte[] outData;
			try {
				outData = decryptStream(ae, inData);
			} catch (AesEncrypterException e) {
				System.err.println("Token is invalid: " + e.getMessage());
				System.exit(2);
				return true;
			} catch (Exception e) {
				System.err.println("Token is invalid: ");
				e.printStackTrace(System.err);
				System.exit(2);
				return true;
			}
			try {
				IOH.writeData(out, outData);
				System.out.println("Decryption complete. Wrote " + outData.length + " bytes to " + IOH.getFullPath(out));
			} catch (Exception e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
				System.exit(2);
				return true;
			}
		} else if ("--aes_encrypt_file".equalsIgnoreCase(cmd)) {
			if (a.length != 4)
				printUsageAndExit("--aes_encrypt_file expecting key file,source file, target file");
			Encrypter ae = loadEncrypter(a[1]);
			byte[] inData;
			try {
				inData = IOH.readData(new File(a[2]));
			} catch (IOException e) {
				System.err.println("Failed to load file: " + e.getMessage());
				System.exit(2);
				return true;
			}
			File out = new File(a[3]);
			if (out.exists()) {
				System.err.println("File already exists: " + IOH.getFullPath(out));
				System.exit(2);
				return true;
			}
			byte[] outData;
			try {
				outData = encryptStream(ae, inData);
			} catch (AesEncrypterException e) {
				System.err.println("Token is invalid: " + e.getMessage());
				System.exit(2);
				return true;
			} catch (Exception e) {
				System.err.println("Token is invalid: ");
				e.printStackTrace(System.err);
				System.exit(2);
				return true;
			}
			try {
				IOH.writeData(out, outData);
				System.out.println("Encryption complete. Wrote " + outData.length + " bytes to " + IOH.getFullPath(out));
			} catch (Exception e) {
				System.err.println(e.getMessage());
				System.exit(2);
				return true;
			}
		} 
		return false;
	}
	private static Encrypter loadEncrypter(String file) {
		File f = new File(file);
		if (!f.exists()) {
			printErrorAndExit("Secret Key file not found: " + IOH.getFullPath(f));
			return null;
		}
		String key;
		try {
			key = IOH.readText(f, true);
		} catch (Exception e) {
			printErrorAndExit("Could not read secret key file: " + e.getMessage());
			return null;
		}
		try {
			return EncrypterManager.loadEncrypter(key);
		} catch (AesEncrypterException e) {
			printErrorAndExit("File '" + IOH.getFullPath(f) + "' invalid: " + e.getMessage());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			printErrorAndExit("File '" + IOH.getFullPath(f) + "' does not contain valid AES key: " + e.getMessage());
			return null;
		}
	}
	private static void printErrorAndExit(String string) {
		System.err.println(string);
		System.exit(1);
	}
	private static void printUsageAndExit(String string) {
		if (SH.is(string)) {
			System.err.println(string);
			System.err.println();
		}
		System.err.println("This command is used to manage AES encrypted tokens using a file based encyrption key");
		System.err.println();
		System.err.println("Options: ");
		System.err.println("   --aes_generate path/to/secret.aes bitdepth                                <-- Creates a new random secret key and stores to file (recommended)");
		System.err.println(
				"   --aes_generate_with_cksum path/to/secret.aes bitdepth                     <-- Creates a new random secret key and stores to file (legacy, includes cksum)");
		System.err.println("   --aes_encrypt path/to/secret.aes plaintext [plaintext2 ...]               <-- Encrypts plain-text to cipher-text using secret key from file");
		System.err.println("   --aes_decrypt path/to/secret.aes ciphertext [ciphertext2 ...]             <-- Decrypts cipher-text to plain-text using secret key from file");
		System.err.println("   --aes_encrypt_file path/to/secret.aes /source/file /target/file.encrypted <-- Encrypts contents of a file using secret key as a binary stream");
		System.err.println("   --aes_decrypt_file path/to/secret.aes /source/file.encrypted /target/file <-- Decrypts ciphertext using secret key as a binary stream");
		System.exit(1);
	}
	private static byte[] encryptStream(Encrypter encrypter, byte[] bytes) throws IOException {
		FastByteArrayDataInputStream in = new FastByteArrayDataInputStream(bytes);
		FastByteArrayDataOutputStream out = new FastByteArrayDataOutputStream();
		IOH.pipe(in, encrypter.createEncrypter(out), true);
		return out.toByteArray();
	}
	private static byte[] decryptStream(Encrypter encrypter, byte[] bytes) throws IOException {
		FastByteArrayDataInputStream in = new FastByteArrayDataInputStream(bytes);
		FastByteArrayDataOutputStream out = new FastByteArrayDataOutputStream();
		IOH.pipe(encrypter.createDecrypter(in), out, true);
		return out.toByteArray();
	}
}
