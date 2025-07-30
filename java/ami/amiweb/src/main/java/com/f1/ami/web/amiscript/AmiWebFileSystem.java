package com.f1.ami.web.amiscript;

import java.io.IOException;
import java.util.Map;

import com.f1.ami.web.AmiWebFile;
import com.f1.ami.web.AmiWebSafeFile;
import com.f1.ami.web.AmiWebService;
import com.f1.base.Table;

public interface AmiWebFileSystem {

	public void init(AmiWebService service);
	public AmiWebFile getFile(String string);
	public AmiWebSafeFile getSafeFile(AmiWebFile string) throws IOException;
	public AmiWebFile getFile(AmiWebFile parent, String string);
	public AmiWebFile getFile(String parent, String string);
	public AmiWebFile[] listRoots();
	Table getSpecial(AmiWebFile file, String instruction, Map<String, ?> params);
	public boolean isWindows();
	public boolean isLocal();
	String getHostName();
}
