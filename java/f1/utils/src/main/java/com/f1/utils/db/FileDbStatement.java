/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

import com.f1.base.ObjectGenerator;
import com.f1.utils.CachedFile;
import com.f1.utils.IOH;

public class FileDbStatement extends BasicDbStatement {

	private CachedFile cachedFile;
	private CachedFile.Cache cache;
	private final File file;

	public FileDbStatement(File file, ObjectGenerator generator) throws Exception {
		super(null, generator);
		this.file = file;
		parseSql(null);
	}

	public File getFile() {
		return cachedFile.getFile();
	}

	@Override
	protected List<PreparedStatement> prepareStatement(Map<Object, Object> params, Connection connection, DbStatementFactory factory) throws Exception {
		parseSql(null);
		return super.prepareStatement(params, connection, factory);
	}

	@Override
	protected void parseSql(String sqlText) throws Exception {
		if (file == null)
			return;

		this.cachedFile = new CachedFile(file, 1000);

		if (cache == null || cache.isOld())
			cache = cachedFile.getData();
		else
			return;
		if (cache.getText() == null)
			throw new FileNotFoundException(IOH.getFullPath(cachedFile.getFile()));

		super.parseSql(cache.getText());
	}

	@Override
	public String describe() {
		return IOH.getFullPath(file);
	}

}
