package com.f1.ami.plugins.ssh;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.AmiServiceLocator;
import com.f1.ami.amicommon.msg.AmiCenterQuery;
import com.f1.ami.amicommon.msg.AmiCenterQueryResult;
import com.f1.ami.amicommon.msg.AmiCenterUpload;
import com.f1.ami.amicommon.msg.AmiCenterUploadTable;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.ami.center.ds.AmiFlatFileDatasourceAdapter2.TableWriter;
import com.f1.base.Bytes;
import com.f1.base.DateMillis;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.FastPrintStream;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.DerivedCellTimeoutController;
import com.f1.utils.structs.table.derived.TimeoutController;

import ch.ethz.ssh2.SFTPv3Client;
import ch.ethz.ssh2.SFTPv3DirectoryEntry;
import ch.ethz.ssh2.SFTPv3FileAttributes;
import ch.ethz.ssh2.SFTPv3FileHandle;

public class AmiSftpDatasourceAdapter implements AmiDatasourceAdapter {
	public static Map<String, String> buildOptions() {
		Map<String, String> r = new HashMap<String, String>();
		r.put(OPTION_PUBLIC_KEY_FILE, "Location Public Key File");
		r.put(OPTION_USE_DUMB_PTY, "true=Use Dumb Pty Terminal (default is false)");
		r.put(OPTION_AUTH_MODE, "keyboardInteractive or password (default is password)");
		return r;
	}

	public static final String OPTION_PUBLIC_KEY_FILE = "publicKeyFile";
	public static final String OPTION_USE_DUMB_PTY = "useDumbPty";
	public static final String OPTION_AUTH_MODE = "authMode";
	private static final Logger log = LH.get();

	private AmiServiceLocator serviceLocator;
	private ContainerTools tools;
	private int port;
	private String hostName;
	private Map<String, String> options;

	@Override
	public void init(ContainerTools tools, AmiServiceLocator serviceLocator) throws AmiDatasourceException {
		this.tools = tools;
		this.serviceLocator = serviceLocator;
		String url = this.serviceLocator.getUrl();
		this.hostName = SH.beforeFirst(url, ':', url);
		try {
			this.port = SH.parseInt(SH.trim(SH.afterFirst(url, ':', "22")));
		} catch (Exception e) {
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Invalid url format, port should be a number", e);
		}
		this.options = SH.splitToMap(',', '=', '\\', serviceLocator.getOptions());
	}

	@Override
	public List<AmiDatasourceTable> getTables(AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		boolean succeeded = false;
		try {
			succeeded = connect(tc);
		} finally {
			IOH.close(this.process);
		}
		if (!succeeded) {
			if (this.process == null)
				return Collections.EMPTY_LIST;//was cancelled
			this.process = null;
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Connection test did not complete");
		}
		this.process = null;
		AmiDatasourceTable sample = tools.nw(AmiDatasourceTable.class);
		sample.setCollectionName(null);
		sample.setName("ls");
		sample.setColumns(Collections.EMPTY_LIST);
		sample.setDatasourceName(this.serviceLocator.getTargetName());
		sample.setCustomQuery("ls .");
		AmiDatasourceTable sample2 = tools.nw(AmiDatasourceTable.class);
		sample2.setCollectionName(null);
		sample2.setName("mkdir");
		sample2.setColumns(Collections.EMPTY_LIST);
		sample2.setDatasourceName(this.serviceLocator.getTargetName());
		sample2.setCustomQuery("mkdir myNewDirectory");
		AmiDatasourceTable sample3 = tools.nw(AmiDatasourceTable.class);
		sample3.setCollectionName(null);
		sample3.setName("mv");
		sample3.setColumns(Collections.EMPTY_LIST);
		sample3.setDatasourceName(this.serviceLocator.getTargetName());
		sample3.setCustomQuery("mv fileName newFileName");
		AmiDatasourceTable sample4 = tools.nw(AmiDatasourceTable.class);
		sample4.setCollectionName(null);
		sample4.setName("get");
		sample4.setColumns(Collections.EMPTY_LIST);
		sample4.setDatasourceName(this.serviceLocator.getTargetName());
		sample4.setCustomQuery("get Data.txt");
		AmiDatasourceTable sample5 = tools.nw(AmiDatasourceTable.class);
		sample5.setCollectionName(null);
		sample5.setName("put");
		sample5.setColumns(Collections.EMPTY_LIST);
		sample5.setDatasourceName(this.serviceLocator.getTargetName());
		sample5.setCustomQuery("insert `Data.txt` from select 'some data' as text");
		return CH.l(sample, sample2, sample3, sample4, sample5);
	}
	@Override
	public List<AmiDatasourceTable> getPreviewData(List<AmiDatasourceTable> tables, int previewCount, AmiDatasourceTracker debugSink, TimeoutController tc)
			throws AmiDatasourceException {
		//		AmiDatasourceTable sample = tools.nw(AmiDatasourceTable.class);
		//		sample.setCollectionName(null);
		//		sample.setName(tables.get(0).getName());
		//		sample.setCreateTableClause("files");
		//		sample.setColumns(Collections.EMPTY_LIST);
		//		sample.setDatasourceName(this.serviceLocator.getTargetName());
		//		sample.setCustomQuery("select * from cmd");
		//		sample.setCustomUse("_cmd=\"ls\"");
		return tables;//CH.l(sample);
	}

	private AmiSshClient process;
	private SFTPv3Client sftpclient;

	@Override
	public void processQuery(AmiCenterQuery query, AmiCenterQueryResult resultSink, AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		String s = query.getQuery();
		String[] parts = SH.splitContinous(' ', s);
		if (parts.length == 0)
			return;
		String cmd = parts[0];
		try {
			int bufSize = 33991;//This seems to be hard coded in the sftp client
			Map<String, Object> directives = query.getDirectives();
			if (directives.containsKey("bufsize"))
				bufSize = CH.getOrThrow(Integer.class, directives, "bufsize");

			connect(tc);
			if ("ls".equals(cmd) || "dir".equals(cmd)) {
				String dir = parts.length == 1 ? "." : SH.trim(parts[1]);
				Table r = new BasicTable(String.class, "Name", Long.class, "Size", DateMillis.class, "ModifiedOn", DateMillis.class, "AccessedOn", Integer.class, "Permissions");
				for (SFTPv3DirectoryEntry result : sftpclient.ls(dir)) {
					SFTPv3FileAttributes attr = result.attributes;
					String filename = result.filename;
					Integer atime = attr.atime;
					Integer mtime = attr.mtime;
					long size = attr.size;
					Integer permissions = attr.permissions;
					r.getRows().addRow(filename, size, atime == null ? null : new DateMillis(atime * 1000), mtime == null ? null : new DateMillis(mtime * 1000), permissions);
				}
				resultSink.setTables((List) CH.l(r));
			} else if ("mkdir".equals(cmd)) {
				String dir = parts[1];
				this.sftpclient.mkdir(dir, 0xffffffff);
			} else if ("mv".equals(cmd)) {
				String src = parts[1];
				String tgt = parts[2];
				this.sftpclient.mv(src, tgt);
			} else if ("get".equals(cmd)) {
				Table r = new BasicTable(String.class, "Name", Bytes.class, "Data");
				byte buf[] = new byte[bufSize];
				for (int i = 1; i < parts.length; i++) {
					String name = parts[i];
					SFTPv3FileHandle fh = this.sftpclient.openFileRO(name);
					ByteArrayOutputStream buf2 = new ByteArrayOutputStream();
					long total = 0;
					for (;;) {
						int read = this.sftpclient.read(fh, total, buf, 0, buf.length);
						if (read == -1)
							break;
						buf2.write(buf, 0, read);
						total += read;
					}
					r.getRows().addRow(name, new Bytes(buf2.toByteArray()));
				}
				resultSink.setTables((List) CH.l(r));
			}
		} catch (Exception e) {
			IOH.close(this.process);
			throw new AmiDatasourceException(AmiDatasourceException.UNKNOWN_ERROR, e);
		}

	}

	private void join(Thread t, long timeout) throws AmiDatasourceException {
		if (t == null || !t.isAlive())
			return;
		if (timeout <= 0)
			throw new AmiDatasourceException(AmiDatasourceException.TIMEOUT_EXCEEDED, "Timeout exceeded durring connection: " + timeout + " ms");
		try {
			t.join(timeout);
		} catch (InterruptedException e) {
		}
		if (t.isAlive())
			throw new AmiDatasourceException(AmiDatasourceException.TIMEOUT_EXCEEDED, "Timeout exceeded durring connection: " + timeout + " ms");
	}

	private static boolean available(InputStream is) {
		try {
			return is.read() != -1;
		} catch (IOException e) {
			return false;
		}
	}

	private boolean connect(TimeoutController tc) throws AmiDatasourceException {
		LH.info(log, this.getServiceLocator().getTargetName(), ": Running query");
		String publicKeyFile = this.options.get(OPTION_PUBLIC_KEY_FILE);
		final char[] publicKey;
		byte mode;
		if (publicKeyFile != null) {
			File file = new File(publicKeyFile);
			if (!file.isFile())
				throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "publicKeyFile not found: " + IOH.getFullPath(file));
			if (!file.canRead())
				throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "publicKeyFile not readable: " + IOH.getFullPath(file));
			try {
				publicKey = IOH.readText(file).toCharArray();
			} catch (Exception e) {
				throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Error reading publicKeyFile: " + IOH.getFullPath(file), e);
			}
			mode = AmiSshClient.AUTHMODE_PUBLICKEY;
		} else {
			String authMode = this.options.get(OPTION_AUTH_MODE);
			if (authMode == null || authMode.equals("password"))
				mode = AmiSshClient.AUTHMODE_PASSWORD;
			else if (authMode.equals("keyboardInteractive"))
				mode = AmiSshClient.AUTHMODE_KEYBOARD_INTERACTIVE;
			else
				throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "authMode option must be either 'password' or 'keyboardInteractive'");
			publicKey = null;
		}
		boolean useDumbPty = "true".equals(this.options.get(OPTION_USE_DUMB_PTY));
		long timeout = tc.getTimeoutMillisRemaining();
		this.process = new AmiSshClient(hostName, port, this.serviceLocator.getUsername(),
				this.serviceLocator.getPassword() == null ? null : new String(this.serviceLocator.getPassword()), null, null, (int) timeout, mode, publicKey, useDumbPty);
		LH.info(log, this.getServiceLocator().getTargetName(), ": Running Process, timeout=", timeout);
		if (this.process.connect()) {
			try {
				this.sftpclient = new SFTPv3Client(this.process.getConnection());
			} catch (IOException e) {
				IOH.close(this.process);
				throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "General Error", this.process.getException());
			}
			return true;
		} else if (this.process == null) {
			LH.info(log, this.getServiceLocator().getTargetName(), ": Running Process was cancelled");
			return false;
		} else {
			switch (this.process.getExitCode()) {
				case AmiSshClient.TIMEOUT:
					throw new AmiDatasourceException(AmiDatasourceException.TIMEOUT_EXCEEDED, "Timeout exceeded durring connection: " + timeout + " ms");
				case AmiSshClient.AUTH_FAILED:
					throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Authentication failed");
				case AmiSshClient.UNKNOWN_HOST:
					throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Unknown Host");
				case AmiSshClient.GENERAL_ERROR:
					throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "General Error", this.process.getException());
				default:
					return false;
			}
		}
	}
	@Override
	public boolean cancelQuery() {
		LH.info(log, this.getServiceLocator().getTargetName(), ": Query Cancelled");
		AmiSshClient p = process;
		if (p != null) {
			IOH.close(p);
			this.process = null;
			return true;
		}
		return false;
	}

	@Override
	public void processUpload(AmiCenterUpload upload, AmiCenterQueryResult results, AmiDatasourceTracker tracker) throws AmiDatasourceException {
		if (connect(new DerivedCellTimeoutController(10000))) {
			try {
				TableWriter fw = new TableWriter();
				for (AmiCenterUploadTable i : upload.getData()) {
					FastByteArrayDataOutputStream buffer = new FastByteArrayDataOutputStream();
					FastPrintStream out = new FastPrintStream(buffer);
					try {
						fw.write((Table) i.getData(), i.getTargetColumns(), out);
					} catch (Exception e) {
					}
					SFTPv3FileHandle handle = sftpclient.createFile(i.getTargetTable());
					sftpclient.write(handle, 0, buffer.getBuffer(), 0, buffer.getCount());
					sftpclient.closeFile(handle);
				}
			} catch (Exception e) {
				throw new AmiDatasourceException(AmiDatasourceException.UNKNOWN_ERROR, "Could not write data", e);
			} finally {
				IOH.close(this.process);
			}
		}

	}

	@Override
	public AmiServiceLocator getServiceLocator() {
		return this.serviceLocator;
	}

}
