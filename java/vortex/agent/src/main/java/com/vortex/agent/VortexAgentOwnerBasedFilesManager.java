package com.vortex.agent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.concurrent.Executor;

import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.ProcessExecutor;

public class VortexAgentOwnerBasedFilesManager {

	public static final byte COMMAND_EXIT = 4;
	public static final byte COMMAND_CREATE_FILE = 0;
	public static final byte COMMAND_CREATE_DIRECTORY = 1;
	public static final byte COMMAND_RUN_COMMAND = 2;
	public static final byte COMMAND_RENAME_FILE = 3;
	public static final byte COMMAND_REMOVE_FILE = 5;
	public static final byte COMMAND_REMOVE_FILE_NO_THROW = 6;
	private static final int RESPONSE_OKAY = 0;
	private static final int RESPONSE_ERROR = 1;

	public static void main(String a[]) {

		DataInputStream i = new DataInputStream(System.in);
		DataOutputStream o = new DataOutputStream(System.out);
		PrintStream err = System.err;
		try {
			o.writeByte(RESPONSE_OKAY);
			o.flush();
		} catch (Exception e2) {
			e2.printStackTrace(System.err);
			System.exit(1);
		}
		byte[] buf = new byte[10240];
		try {
			boolean done = false;
			while (!done) {
				try {
					int command = i.readByte();
					switch (command) {
						case COMMAND_EXIT: {
							done = true;
							break;
						}
						case COMMAND_CREATE_FILE: {
							File srcFile = new File(i.readUTF());
							boolean isExecutable = i.readBoolean();
							boolean isReadable = i.readBoolean();
							boolean isWriteable = i.readBoolean();
							final int length = i.readInt();
							IOH.ensureDir(srcFile.getParentFile());
							if (srcFile.exists() && srcFile.isFile())
								srcFile.setWritable(true);
							boolean t = makeParentWritable(srcFile);
							FileOutputStream out = new FileOutputStream(srcFile, false);
							try {
								final int tot = IOH.pipe(i, out, buf, length);
								if (tot != length) {
									throw new RuntimeException("bytes read " + tot + " of " + length);
								}
							} finally {
								IOH.close(out);
								resetParentWritable(srcFile, t);
							}
							srcFile.setExecutable(isExecutable);
							srcFile.setReadable(isReadable);
							srcFile.setWritable(isWriteable);
							break;
						}
						case COMMAND_RENAME_FILE: {
							File srcFile = new File(i.readUTF());
							File dstFile = new File(i.readUTF());
							if (!srcFile.isFile())
								throw new RuntimeException("Source file not found for move: " + IOH.getFullPath(srcFile));
							if (dstFile.isFile())
								throw new RuntimeException("Destination file already exists: " + IOH.getFullPath(dstFile));
							srcFile.renameTo(dstFile);
							if (!dstFile.exists())
								throw new RuntimeException("move failed: " + IOH.getFullPath(srcFile) + " --> " + IOH.getFullPath(dstFile));
							break;
						}
						case COMMAND_REMOVE_FILE: {
							File srcFile = new File(i.readUTF());
							if (!srcFile.exists())
								throw new RuntimeException("Source file not found for move: " + IOH.getFullPath(srcFile));
							IOH.deleteForce(srcFile);
							if (srcFile.exists())
								throw new RuntimeException("remove failed, still exists: " + IOH.getFullPath(srcFile));
							break;
						}
						case COMMAND_REMOVE_FILE_NO_THROW: {
							File srcFile = new File(i.readUTF());
							if (srcFile.exists())
								try {
									IOH.deleteForce(srcFile);
								} catch (Exception e) {
								}
							break;
						}
						case COMMAND_CREATE_DIRECTORY: {
							String absFile = i.readUTF();
							boolean isExecutable = i.readBoolean();
							boolean isReadable = i.readBoolean();
							boolean isWriteable = i.readBoolean();
							File file = new File(absFile);
							boolean t = makeParentWritable(file);
							file.setExecutable(isExecutable);
							file.setReadable(isReadable);
							file.setWritable(isWriteable);
							if (file.isFile())
								throw new RuntimeException("Regular file exists: " + IOH.getFullPath(file));
							file.mkdirs();
							resetParentWritable(file, t);
							if (!file.isDirectory())
								throw new RuntimeException("Directory was not created: " + IOH.getFullPath(file));
							break;
						}
					}
					o.writeByte(RESPONSE_OKAY);
					o.flush();
				} catch (EOFException e) {
					System.exit(1);
				} catch (Exception e) {
					e.printStackTrace(System.err);
					try {
						o.writeByte(RESPONSE_ERROR);
						o.writeUTF(e.getMessage());
						o.flush();
					} catch (Exception e2) {
						e2.printStackTrace(System.err);
						System.exit(1);
					}
				}
			}
		} finally {
			System.out.flush();
			System.err.flush();
		}
		System.exit(0);
	}
	private static void resetParentWritable(File file, boolean t) {
		if (t)
			file.getParentFile().setWritable(false);

	}

	private static boolean makeParentWritable(File file) {
		if (file.getParentFile().isDirectory() && !file.getParentFile().canWrite()) {
			file.getParentFile().setWritable(true);
			return true;
		}
		return false;
	}

	final private DataInputStream stdout;
	final private DataOutputStream stdin;
	final private ProcessExecutor processExecutor;

	public VortexAgentOwnerBasedFilesManager(Executor exec, String commandPrefix, OutputStream err) throws IOException {
		String command = commandPrefix + EH.getJavaExecutableCommand(OH.EMPTY_STRING_ARRAY, getClass().getName(), OH.EMPTY_STRING_ARRAY);
		System.out.println("VortexAgentOwnerBasiedFilesCommand: " + command);

		processExecutor = new ProcessExecutor(exec, command);
		processExecutor.pipeStdErrTo(err);
		PipedOutputStream outStream = new PipedOutputStream();
		//processExecutor.pipeStdOutTo(outStream);
		stdout = new DataInputStream(processExecutor.getStdOut());//new PipedInputStream(outStream));
		stdin = new DataOutputStream(processExecutor.getStdIn());
		if (stdout.readByte() != RESPONSE_OKAY)
			throw new RuntimeException("did not startup succesfully");
	}

	public void createDirectory(File path, boolean isExecutable, boolean isReadable, boolean isWriteable) throws IOException {
		stdin.writeByte(COMMAND_CREATE_DIRECTORY);
		stdin.writeUTF(path.getAbsolutePath());
		stdin.writeBoolean(isExecutable);
		stdin.writeBoolean(isReadable);
		stdin.writeBoolean(isWriteable);
		stdin.flush();
		readResult();
	}

	public void createFile(File path, byte[] data, boolean isExecutable, boolean isReadable, boolean isWriteable) throws IOException {
		stdin.writeByte(COMMAND_CREATE_FILE);
		stdin.writeUTF(path.getAbsolutePath());
		stdin.writeBoolean(isExecutable);
		stdin.writeBoolean(isReadable);
		stdin.writeBoolean(isWriteable);
		stdin.writeInt(data.length);
		stdin.write(data);
		stdin.flush();
		readResult();
	}
	public void deleteFile(File path) throws IOException {
		stdin.writeByte(COMMAND_REMOVE_FILE);
		stdin.writeUTF(path.getAbsolutePath());
		stdin.flush();
		readResult();
	}

	private void readResult() throws IOException {
		byte code = stdout.readByte();
		switch (code) {
			case RESPONSE_ERROR:
				throw new RuntimeException("Response: " + stdout.readUTF());
			case RESPONSE_OKAY:
				return;
			default:
				throw new RuntimeException("unknown response: " + code);
		}
	}

	public void done() throws InterruptedException, IOException {
		stdin.writeByte(COMMAND_EXIT);
		stdin.flush();
		readResult();
		processExecutor.waitFor();
	}
	public void deleteFileNoThrow(File path) throws IOException {
		stdin.writeByte(COMMAND_REMOVE_FILE_NO_THROW);
		stdin.writeUTF(path.getAbsolutePath());
		stdin.flush();
		readResult();
	}

}
