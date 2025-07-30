package com.f1.ami.webmanager;

import java.io.File;

import com.f1.utils.IOH;
import com.f1.utils.SH;

public class AmiWebManagerFile {

	private File file;
	private String fullPath;
	private String mountPoint;

	public AmiWebManagerFile(File file, String mountPoint, String fullPath) {
		this.file = file;
		this.mountPoint = mountPoint;
		this.fullPath = fullPath;
	}

	public File getFile() {
		return this.file;
	}

	public AmiWebManagerFile[] listFiles() {
		String[] t = file.list();
		AmiWebManagerFile[] r = new AmiWebManagerFile[t.length];
		for (int i = 0; i < t.length; i++)
			r[i] = new AmiWebManagerFile(new File(file, t[i]), mountPoint, fullPath + '/' + t[i]);
		return r;
	}
	public String getFullPath() {
		return fullPath;
	}

	public String getParentFullPath() {
		if (this.mountPoint == null)
			return null;
		String r = SH.beforeLast(this.fullPath, '/', null);
		return mountPoint.equals("") || AmiWebManagerController.isMount(r, this.mountPoint) ? r : null;
	}

	@Override
	public String toString() {
		final String p1 = getFullPath();
		final String p2 = IOH.getFullPath(getFile());
		//		return OH.eq(p1, p2) ? p1 : (p1 + " -> " + p2);
		return p1 + " -> " + p2;
	}
}
