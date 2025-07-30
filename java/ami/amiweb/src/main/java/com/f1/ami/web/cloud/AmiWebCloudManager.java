package com.f1.ami.web.cloud;

import com.f1.ami.web.AmiWebFile;
import com.f1.ami.web.AmiWebService;

public interface AmiWebCloudManager {

	public static final char SEPERATOR = '/';

	public AmiWebCloudLayoutTree getCloudLayouts();
	public AmiWebFile getFile(String name);
	public String loadLayout(String name);
	public boolean isLayoutWriteable(String name);
	public void saveLayout(String name, String layout);
	public void removeLayout(String correlationData);
	public void setLayoutWriteable(String name, boolean b);
	public AmiWebFile getLayoutsRootDirectory();
	public void init(AmiWebService amiWebService);

}
