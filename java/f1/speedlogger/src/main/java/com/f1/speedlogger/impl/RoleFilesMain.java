package com.f1.speedlogger.impl;

import java.io.IOException;

import com.f1.utils.SH;

public class RoleFilesMain {

	public static void main(String a[]) throws IOException {
		final int maxFilesCount;
		try {
			maxFilesCount = SH.parseInt(a[0]);
		} catch (Exception e) {
			System.err.println("Expecting: java " + RoleFilesMain.class.getCanonicalName() + " maxFilesCount filename1 [filename2 ...]");
			return;
		}
		for (int i = 1; i < a.length; i++) {
			String fileName = a[i];
			SpeedLoggerUtils.roleFile(fileName, maxFilesCount);
		}
	}
}
