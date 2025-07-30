package com.f1.ami.amicommon;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.SH;
import com.f1.utils.encrypt.EncrypterTool;

public class AmiTools {
	public static void main(String[] args) {
		if (args.length < 1) {
			printAllUsagesAndExit();
			return;
		}
		// combine EncrypterTool functionality
		if (EncrypterTool.handle(args)) {
			System.exit(0);
			return;
		}
		if (args.length < 2) {
			printErrorAndExit("--migrate option expects file path, current directory is " + EH.getPwd());
		}
		String cmd = args[0];
		if ("--migrate".equalsIgnoreCase(cmd)) {
			for (int i = 1; i < args.length; i++) {
				// fix each file
				String path = args[i];
				if (SH.is(path)) {
					File f;
					if (!IOH.isAbsolutePath(path)) {
						f = IOH.joinPaths(EH.getPwd(), path);
					} else {
						if (!EH.isWindows())
							path = IOH.toLinuxPath(path);
						f = new File(path);
					}
					// check exist and is file
					if (!f.isFile()) {
						System.err.println(f.getAbsolutePath() + " does not exists or is a directory, skipping...");
						continue;
					}
					fixFile(f);
				}
			}
		} else {
			printUsageAndExit();
		}
	}

	public static void printUsageAndExit() {
		System.err.println("This command is used for supporting backwards compatibility with use ds option. E.g. use ds=myDs becomes use ds= \"myDs\"");
		System.err.println();
		System.err.println("Usage: ");
		System.err.println("   --migrate file1 file2...");
		System.exit(1);
	}

	public static void printAllUsagesAndExit() {
		System.err.println("This tool is used for");
		System.err.println("1. supporting backwards compatibility with use ds option. E.g. use ds=myDs becomes use ds= \"myDs\"");
		System.err.println("Usage: ");
		System.err.println("   --migrate file1 file2...");
		System.err.println();
		System.err.println("2. managing AES encrypted tokens using a file based encyrption key");
		System.err.println("Usage: ");
		System.err.println("   --aes_generate path/to/secret.aes bitdepth                                <-- Creates a new random secret key and stores to file (recommended)");
		System.err.println(
				"   --aes_generate_with_cksum path/to/secret.aes bitdepth                     <-- Creates a new random secret key and stores to file (legacy, includes cksum)");
		System.err.println("   --aes_encrypt path/to/secret.aes plaintext [plaintext2 ...]               <-- Encrypts plain-text to cipher-text using secret key from file");
		System.err.println("   --aes_decrypt path/to/secret.aes ciphertext [ciphertext2 ...]             <-- Decrypts cipher-text to plain-text using secret key from file");
		System.err.println("   --aes_encrypt_file path/to/secret.aes /source/file /target/file.encrypted <-- Encrypts contents of a file using secret key as a binary stream");
		System.err.println("   --aes_decrypt_file path/to/secret.aes /source/file.encrypted /target/file <-- Decrypts ciphertext using secret key as a binary stream");
		System.exit(1);
	}

	private static void printErrorAndExit(String string) {
		System.err.println(string);
		System.exit(1);
	}

	private static void fixFile(File f) {
		//		System.out.println(f.getAbsolutePath());
		if (!validateExtension(f.getName())) {
			return;
		}
		try {
			String txt = IOH.readText(f);
			if (!SH.is(txt)) {
				System.out.println("No change needed:  " + f.getName());
				return;
			}
			List<String> sqlTxt = SH.splitToList("\r", txt);
			if (AmiUtils.fixUseDsLines(sqlTxt)) {
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
				String fname = f.getName();
				String formatted = df.format(new Date());
				// rename existing file to file_date, as backup
				// e.g. myFile.sql -> myFile_20240916.sql
				String newName = SH.splice(fname, fname.lastIndexOf('.'), 0, "_" + formatted);
				IOH.renameFile(f, newName);
				//				write to original file
				IOH.writeText(new File(f.getAbsolutePath()), SH.j('\r', sqlTxt));
				System.out.println("fixed " + f.getName());
			} else {
				System.out.println("No change needed:  " + f.getName());
			}
		} catch (IOException e) {
			System.err.println("Error while fixing " + f.getName() + ":" + e.getMessage());
		}
	}

	private static boolean validateExtension(String fileName) {
		String extension = SH.afterLast(fileName, '.');
		if (!"amisql".contentEquals(extension) && !"sql".contentEquals(extension)) {
			// ignore non-sql files
			return false;
		}
		return true;
	}
}
