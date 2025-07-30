package com.f1.ami.center.table.persist;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiEncrypter;
import com.f1.ami.center.AmiCenterState;
import com.f1.ami.center.AmiSysCommandsUtils;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiRowImpl;
import com.f1.ami.center.table.AmiTable;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.SafeFile;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiTablePersister_Text implements AmiTablePersister {
	private static final String FILE_EXTENSION = ".dat";
	private static final String FILE_EXTENSION_SECURE = ".sdat";

	final static private Logger log = LH.get();

	private AmiTable table;
	private AmiCenterState state;

	private boolean needsWrite = false;

	private File persistDirectory;

	private Map<String, Object> options;

	private SafeFile safeFile;

	private AmiEncrypter encrypter;

	public AmiTablePersister_Text(AmiTablePersisterFactory_Text factory, Map<String, Object> options) {
		this.options = options;
	}

	@Override
	public void init(AmiTable sink) {
		this.table = sink;
		this.state = ((AmiImdbImpl) this.table.getImdb()).getState();
		String pd = (String) options.get(AmiTablePersisterFactory_Text.OPTION_PERSIST_DIR);
		String encrypter = (String) options.get(AmiTablePersisterFactory_Fast.OPTION_PERSIST_ENCRYPTER);
		if (SH.is(encrypter))
			this.encrypter = this.state.getEncrypter(encrypter);
		else
			this.encrypter = null;
		this.persistDirectory = pd == null ? state.getPersistDirectory() : new File(pd);
		try {
			IOH.ensureDir(this.persistDirectory);
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
		final File file = new File(persistDirectory, table.getName() + getFileExtension());
		LH.info(log, "For table ", table.getName(), ", Using TEXT based persister at " + IOH.getFullPath(file));
		if (encrypter != null) {
			File nonSecureFile = new File(this.persistDirectory, this.table.getName() + FILE_EXTENSION);
			if (nonSecureFile.exists() && !file.exists()) {
				LH.info(log, "For table ", table.getName(), ", detected encryption has been enabled for a previously unencrypted table. Auto-encrypting...");
				AmiTablePersister_Fast.encryptFile(nonSecureFile, file, this.encrypter);
			}
		} else {
			File secureFile = new File(this.persistDirectory, this.table.getName() + FILE_EXTENSION_SECURE);
			if (secureFile.exists())
				throw new RuntimeException("For Table " + this.table.getName() + ", " + AmiTablePersisterFactory_Fast.OPTION_PERSIST_ENCRYPTER
						+ " is not set but encrypted file found. This is likely a security issue but if intentional, manually decrypt using tools.sh on "
						+ IOH.getFullPath(secureFile));
		}
		try {
			this.safeFile = new SafeFile(file, ".swp");
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}
	@Override
	public void onRemoveRow(AmiRowImpl row) {
		needsWrite = true;
	}

	@Override
	public void onAddRow(AmiRowImpl r) {
		needsWrite = true;
	}

	@Override
	public void onRowUpdated(AmiRowImpl sink, long mask0, long mask64) {
		needsWrite = true;
	}

	@Override
	public boolean loadTableFromPersist(CalcFrameStack sf) {
		if (safeFile.exists())
			readTable(sf);
		else
			writeTable(sf);
		return false;
	}

	private void readTable(CalcFrameStack sf) {
		try {
			AmiSysCommandsUtils.readObjectsFromDisk(state, this.table, this.safeFile, sf, this.encrypter);
		} catch (Exception e) {
			if (this.table.getName().startsWith("__"))
				throw new RuntimeException("CRITICAL SYSTEM TABLE ERROR: Could not load persisted data into " + this.table.getName(), e);
			LH.severe(log, "Could not load persisted data into " + this.table.getName(), e);
		}
	}

	@Override
	public void clear(CalcFrameStack sf) {
	}

	@Override
	public void flushChanges(CalcFrameStack sf) {
		if (needsWrite)
			writeTable(sf);
		needsWrite = false;
	}

	private void writeTable(CalcFrameStack sf) {
		try {
			AmiSysCommandsUtils.writeObjectsToDisk(state, this.table, this.safeFile, sf, this.encrypter);
		} catch (Exception e) {
			LH.severe(log, "Could not persist to " + this.table.getName(), e);
		}
	}

	@Override
	public void saveTableToPersist(CalcFrameStack sf) {
		writeTable(sf);
	}

	@Override
	public void drop(CalcFrameStack sf) {
		try {
			AmiSysCommandsUtils.removeFromDisk(state, this.safeFile);
		} catch (IOException e) {
		}
	}

	@Override
	public void onTableRename(String oldName, String name, CalcFrameStack sf) {
		try {
			SafeFile safeFileNew = new SafeFile(new File(persistDirectory, name + getFileExtension()), ".swp");
			AmiSysCommandsUtils.writeObjectsToDisk(state, this.table, safeFileNew, sf, encrypter);
			AmiSysCommandsUtils.removeFromDisk(state, this.safeFile);
			this.safeFile = safeFileNew;
		} catch (Exception e) {
			LH.severe(log, "Could not persist to " + this.table.getName(), e);
		}
	}

	private String getFileExtension() {
		return this.encrypter == null ? FILE_EXTENSION : FILE_EXTENSION_SECURE;
	}

	public File getFile() {
		return this.safeFile.getFile();
	}

}
