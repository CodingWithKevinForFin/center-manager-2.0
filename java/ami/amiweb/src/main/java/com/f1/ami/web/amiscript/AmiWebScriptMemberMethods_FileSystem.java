package com.f1.ami.web.amiscript;

import java.io.IOException;
import java.util.Map;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.AmiWebFile;
import com.f1.base.Bytes;
import com.f1.base.Table;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_FileSystem extends AmiWebScriptBaseMemberMethods<AmiWebFileSystem> {

	private AmiWebScriptMemberMethods_FileSystem() {
		super();

		addMethod(WRITE_FILE);
		addMethod(READ_FILE);
		addMethod(READ_BINARY_FILE);
		addMethod(WRITE_BINARY_FILE);

		//		addMethod(this.restrictPath);
		addMethod(GET_AMI_FILE_PATH);

		addMethod(LIST_FILES);
		addMethod(GET_WORKING_DIRECTORY, "workingDirectory");
	}

	private static final AmiAbstractMemberMethod<AmiWebFileSystem> GET_WORKING_DIRECTORY = new AmiAbstractMemberMethod<AmiWebFileSystem>(AmiWebFileSystem.class,
			"getWorkingDirectory", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFileSystem targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getFile(".").getFullPath();
		}

		@Override
		protected String getHelp() {
			return "Returns the path of the working directory (the root file).";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebFileSystem> GET_AMI_FILE_PATH = new AmiAbstractMemberMethod<AmiWebFileSystem>(AmiWebFileSystem.class, "getAmiFilePath",
			String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFileSystem targetObject, Object[] params, DerivedCellCalculator caller) {
			String filePath = (String) params[0];
			if (filePath == null)
				return null;
			// Replace windows slash back slash with linux forward slash
			return SH.replaceAll(filePath, "\\", "/");
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "filePath" };
		}

		@Override
		protected String getHelp() {
			return "Converts and returns a path to AMI default path, which uses forward slash.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebFileSystem> LIST_FILES = new AmiAbstractMemberMethod<AmiWebFileSystem>(AmiWebFileSystem.class, "listFiles", BasicTable.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFileSystem targetObject, Object[] params, DerivedCellCalculator caller) {
			String fileName = (String) params[0];
			AmiWebFile f = targetObject.getFile(fileName);
			Map<Object, Object> details = CH.m("File", f, "Absolute Path", f);
			if (!f.exists()) {
				warning(sf, "Directory not found", details);
			} else if (!f.canRead()) {
				warning(sf, "Directory access denied", details);
			} else if (!f.isDirectory()) {
				warning(sf, "Not a Directory", details);
			} else
				try {
					final AmiWebFile[] files = f.listFiles();
					Table t = new BasicTable(String.class, "name", Long.class, "size", String.class, "type", Boolean.class, "hidden", Boolean.class, "executable", Boolean.class,
							"readable", Boolean.class, "writeable", Long.class, "modifiedOn");
					for (AmiWebFile file : files) {
						String type = file.isDirectory() ? "dir" : file.isFile() ? "file" : null;
						t.getRows().addRow(file.getName(), file.length(), type, file.isHidden(), file.canExecute(), file.canRead(), file.canWrite(), file.lastModified());
					}
					details.put("Files", files.length);
					debug(sf, "Directory Listing", details);
					return t;
				} catch (Exception e) {
					warning(sf, "Directory Listing Error", details, e);
				}
			return null;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "relativePathToDirectory" };
		}

		@Override
		protected String getHelp() {
			return "Returns a Table containing the files and file details of all files in supplied directory.  Details include: name, size, type, hidden, executable, readable, writeable, modifiedOn.";
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "the path to the directory under the AMI root directory. Ex: resources" };
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebFileSystem> WRITE_FILE = new AmiAbstractMemberMethod<AmiWebFileSystem>(AmiWebFileSystem.class, "writeFile", Boolean.class,
			String.class, String.class, Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFileSystem targetObject, Object[] params, DerivedCellCalculator caller) {
			String fileName = (String) params[0];
			if (fileName == null)
				return Boolean.FALSE;
			String text = (String) params[1];
			if (text == null)
				return Boolean.FALSE;
			boolean append = (Boolean) params[2];
			AmiWebFile f = targetObject.getFile(fileName);
			Map<Object, Object> details = CH.m("File", f, "Absolute Path", f.getFullPath(), "File already existed", f.exists());
			try {
				f.getParentFile().mkdirForce();
				//				IOH.ensureDir(f.getParentFile());
			} catch (IOException e1) {
				warning(sf, "File exists, write access denied", details);
				return Boolean.FALSE;
			}
			if (f.exists() && !f.canWrite()) {
				warning(sf, "File exists, write access denied", details);
			}
			try {
				if (append) {
					f.appendText(text);
					//					IOH.appendText(f, text);
					details.put("Chars Appended", text.length());
				} else {
					f.writeText(text);
					details.put("Chars Written", text.length());
				}
				debug(sf, "File Written", details);
				return Boolean.TRUE;
			} catch (IOException e) {
				warning(sf, "File write Error", details, e);
			}
			return Boolean.FALSE;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "file", "data", "shouldAppend" };
		}

		@Override
		protected String getHelp() {
			return "Writes the data to disk. If shouldAppend is true and if file exists then data is appended, otherwise file is overridden. Returns true on success.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebFileSystem> READ_FILE = new AmiAbstractMemberMethod<AmiWebFileSystem>(AmiWebFileSystem.class, "readFile", String.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFileSystem targetObject, Object[] params, DerivedCellCalculator caller) {
			String fileName = (String) params[0];
			AmiWebFile f = targetObject.getFile(fileName);
			Map<Object, Object> details = CH.m("File", f, "Absolute Path", f.getFullPath());
			if (!f.exists()) {
				warning(sf, "File not found", details);
			} else if (!f.canRead()) {
				warning(sf, "File access denied", details);
			} else
				try {
					String r = f.readTextForce();
					if (r != null) {
						details.put("Chars Read", r.length());
						debug(sf, "File Read", details);
						return r;
					}
					warning(sf, "File Read Failed", details);
				} catch (IOException e) {
					warning(sf, "File Read Error", details, e);
				}
			return null;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "fileNamePath" };
		}

		@Override
		protected String getHelp() {
			return "Returns the contents of supplied file or null if file does not exist or is not readable. The filename should be relative to the root folder of AMI.";
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "the path to the file plus the full file name. Ex: resources/myfile.txt" };
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebFileSystem> READ_BINARY_FILE = new AmiAbstractMemberMethod<AmiWebFileSystem>(AmiWebFileSystem.class, "readBinaryFile",
			Bytes.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFileSystem targetObject, Object[] params, DerivedCellCalculator caller) {
			String fileName = (String) params[0];
			if (fileName == null)
				return null;
			AmiWebFile f = targetObject.getFile(fileName);
			Map<Object, Object> details = CH.m("File", f, "Absolute Path", f.getFullPath());
			if (!f.exists()) {
				warning(sf, "File not found", details);
			} else if (!f.canRead()) {
				warning(sf, "File access denied", details);
			} else
				try {
					Bytes r = Bytes.valueOf(f.readBytes());
					if (r != null) {
						details.put("Bytes Read", r.length());
						debug(sf, "File Read", details);
						return r;
					}
					warning(sf, "File Read Failed", details);
				} catch (IOException e) {
					warning(sf, "File Read Error", details, e);
				}
			return null;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "fileName" };
		}

		@Override
		protected String getHelp() {
			return "Returns the binary data of supplied file or null if the file does not exist or is not readable. The filename should be relative to the root folder of AMI.";
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "the path to the file plus the full file name. Ex: resources/myfile.txt" };
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebFileSystem> WRITE_BINARY_FILE = new AmiAbstractMemberMethod<AmiWebFileSystem>(AmiWebFileSystem.class, "writeBinaryFile",
			Boolean.class, String.class, Bytes.class, Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFileSystem targetObject, Object[] params, DerivedCellCalculator caller) {
			String fileName = (String) params[0];
			if (fileName == null)
				return Boolean.FALSE;
			Bytes data = (Bytes) params[1];
			if (data == null)
				return Boolean.FALSE;
			boolean append = (Boolean) params[2];

			AmiWebFile f = targetObject.getFile(fileName);
			Map<Object, Object> details = CH.m("File", f, "Absolute Path", f.getFullPath(), "File already existed", f.exists());
			try {
				AmiWebFile parent = f.getParentFile();
				if (parent == null) {
					warning(sf, "Parent file does not exist. Please double check if the input file name is valid", details);
					return Boolean.FALSE;
				}
				parent.mkdirForce();
			} catch (IOException e1) {
				warning(sf, "File exists, write access denied", details);
				return Boolean.FALSE;
			}
			if (f.exists() && !f.canWrite()) {
				warning(sf, "File exists, write access denied", details);
			}
			try {
				if (append) {
					f.appendBytes(data.getBytes());
					details.put("Chars Appended", data.length());
				} else {
					f.writeBytes(data.getBytes());
					details.put("Chars Written", data.length());
				}
				debug(sf, "File Written", details);
				return Boolean.TRUE;
			} catch (IOException e) {
				warning(sf, "File write Error", details, e);
			}
			return Boolean.FALSE;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "file", "data", "shouldAppend" };
		}

		@Override
		protected String getHelp() {
			return "Writes the data to disk. If shouldAppend is true and if file exists then data is appended, otherwise file is overridden. The filename should be formatted as either an absolute path or a relative path. Returns true on success.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	@Override
	public String getVarTypeName() {
		return "FileSystem";
	}
	@Override
	public String getVarTypeDescription() {
		return "Represents the file system on which the AMI Webserver is running.";
	}
	@Override
	public Class<AmiWebFileSystem> getVarType() {
		return AmiWebFileSystem.class;
	}
	@Override
	public Class<AmiWebFileSystem> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_FileSystem INSTANCE = new AmiWebScriptMemberMethods_FileSystem();
}
