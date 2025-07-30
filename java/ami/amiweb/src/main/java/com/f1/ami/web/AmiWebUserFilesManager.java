package com.f1.ami.web;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.f1.ami.web.amiscript.AmiWebFileSystem;
import com.f1.suite.web.portal.PortletManager;
import com.f1.utils.AH;
import com.f1.utils.OH;

public class AmiWebUserFilesManager {

	final private AmiWebFile basePath;
	private String userName;
	private String userNamePeriod;

	private AmiWebFileSystem fs;

	public AmiWebUserFilesManager(PortletManager pm, AmiWebFileSystem fs, AmiWebFile basePath) {
		this.fs = fs;
		this.userName = pm.getState().getUserName();
		OH.assertNotNull(this.userName, "username");
		this.userNamePeriod = userName + ".";
		this.basePath = basePath;
		try {
			basePath.mkdirForce();
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}
	public void saveFile(String key, String value) {
		AmiWebSafeFile cfile = getSafeFile(key);
		try {
			cfile.setText(value);
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}
	public void removeFile(String key) {
		//		filesCache.remove(key);
		AmiWebFile file = getFile(key);
		if (file.exists()) {
			file.delete();
		}
	}
	public AmiWebSafeFile getSafeFile(String key) {
		//		AmiWebSafeFile cfile = filesCache.get(key);
		//		if (cfile == null) {
		AmiWebFile file = getFile(key);
		try {
			return fs.getSafeFile(file);
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
		//			filesCache.put(key, cfile);
		//		}
		//		return cfile;
	}
	public String loadFile(String key) {
		return getSafeFile(key).getText();
	}

	public AmiWebFile getFile(String key) {
		return this.fs.getFile(basePath, userNamePeriod + key);
	}

	public Set<String> getFiles() {
		final Set<String> r;
		String[] files = basePath.list();
		if (AH.isEmpty(files))
			r = Collections.EMPTY_SET;
		else {
			r = new TreeSet<String>();
			int length = userNamePeriod.length();
			for (String f : files)
				if (f.startsWith(userNamePeriod))
					r.add(f.substring(length));
		}
		return r;
	}
	public Map<String, AmiWebFile> getAmiFiles() {
		final Map<String, AmiWebFile> r;
		AmiWebFile[] files = basePath.listFiles();
		if (AH.isEmpty(files))
			r = Collections.EMPTY_MAP;
		else {
			r = new HashMap<String, AmiWebFile>();
			int length = userNamePeriod.length();
			for (AmiWebFile file : files) {
				String f = file.getName();
				if (f.startsWith(userNamePeriod))
					r.put(f.substring(length), file);
			}
		}
		return r;
	}
	public void setFileWriteable(String key, boolean b) {
		getSafeFile(key).getFile().setWritable(b);
	}
	public void moveFile(String name, String newFile) {
		//		this.filesCache.remove(name);
		AmiWebFile existing = getFile(name);
		AmiWebFile nuw = getFile(newFile);
		try {
			existing.moveForce(nuw);
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
		//		IOH.moveForce(existing, nuw);
	}
}
