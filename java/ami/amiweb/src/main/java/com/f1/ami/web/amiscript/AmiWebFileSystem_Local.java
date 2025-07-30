package com.f1.ami.web.amiscript;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.f1.ami.amicommon.webfilespecial.AmiSpecialFileProcessor;
import com.f1.ami.web.AmiWebFile;
import com.f1.ami.web.AmiWebFile_Local;
import com.f1.ami.web.AmiWebSafeFile;
import com.f1.ami.web.AmiWebSafeFile_Local;
import com.f1.ami.web.AmiWebService;
import com.f1.base.Table;
import com.f1.utils.EH;

public class AmiWebFileSystem_Local implements AmiWebFileSystem {

	private AmiWebService service;

	public AmiWebFile getFile(String string) {
		return new AmiWebFile_Local(string);
	}
	public AmiWebFile getFile(AmiWebFile parent, String string) {
		return new AmiWebFile_Local(parent, string);
	}
	public AmiWebFile getFile(String parent, String string) {
		return new AmiWebFile_Local(parent, string);
	}

	public AmiWebFile[] listRoots() {
		File[] roots = File.listRoots();
		if (!EH.isWindows() && roots.length == 1 && "/".equals(roots[0].getPath()))
			roots = roots[0].listFiles();
		if (roots == null)
			return null;
		AmiWebFile[] r = new AmiWebFile[roots.length];
		for (int i = 0; i < r.length; i++)
			r[i] = new AmiWebFile_Local(roots[i]);
		return r;
	}

	@Override
	public AmiWebSafeFile getSafeFile(AmiWebFile file) throws IOException {
		return new AmiWebSafeFile_Local((AmiWebFile_Local) file);
	}
	@Override
	public void init(AmiWebService service) {
		this.service = service;
	}
	@Override
	public Table getSpecial(AmiWebFile file, String instruction, Map<String, ?> params) {
		return AmiSpecialFileProcessor.processSpecial(new File(file.getAbsolutePath()), instruction, params);
	}
	@Override
	public boolean isWindows() {
		return EH.isWindows();
	}
	@Override
	public boolean isLocal() {
		return true;
	}
	@Override
	public String getHostName() {
		return EH.getLocalHost();
	}
}
