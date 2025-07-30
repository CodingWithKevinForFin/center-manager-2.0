package com.f1.ami.plugins.p4;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.ami.amicommon.AmiScmAdapter;
import com.f1.ami.amicommon.AmiScmException;
import com.f1.ami.amicommon.AmiScmRevision;
import com.f1.base.DateMillis;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.IntArrayList;
import com.f1.utils.SH;
import com.perforce.p4java.client.IClient;
import com.perforce.p4java.core.IChangelist;
import com.perforce.p4java.core.file.FileAction;
import com.perforce.p4java.core.file.FileSpecBuilder;
import com.perforce.p4java.core.file.FileSpecOpStatus;
import com.perforce.p4java.core.file.IFileRevisionData;
import com.perforce.p4java.core.file.IFileSpec;
import com.perforce.p4java.exception.AccessException;
import com.perforce.p4java.exception.ConnectionException;
import com.perforce.p4java.impl.generic.core.Changelist;
import com.perforce.p4java.impl.generic.core.file.FilePath.PathType;
import com.perforce.p4java.option.server.GetDepotFilesOptions;
import com.perforce.p4java.server.IServer;
import com.perforce.p4java.server.ServerFactory;

public class AmiP4ScmAdapter implements AmiScmAdapter {

	private IServer server;
	private IClient client;
	private String directory;

	@Override
	public void init(com.f1.container.ContainerTools tools, String url, String client, String username, char[] password, String basePath, String options) throws AmiScmException {
		if (SH.isnt(url))
			throw new AmiScmException("URL required");
		if (SH.isnt(client))
			throw new AmiScmException("Client required");
		if (SH.isnt(username))
			throw new AmiScmException("username required");
		try {
			this.server = ServerFactory.getServer("p4java" + url, null);
			server.connect();
		} catch (Exception e) {
			throw new AmiScmException("Could not connect to p4 server: '" + url + "'", e);
		}
		try {
			server.setUserName(username);
			server.login(new String(password));
			this.directory = basePath;
		} catch (Exception e) {
			throw new AmiScmException("Authentication failed: '" + username + "'", e);
		}
		try {
			this.server.setCurrentClient(this.client = this.server.getClient(client));
		} catch (Exception e) {
			throw new AmiScmException("Client not found: '" + client + "'", e);
		}
	};
	public AmiP4ScmAdapter() {
	}
	public static void main(String a[]) throws AmiScmException {

		System.out.println(System.getenv("P4CLIENT"));
		AmiP4ScmAdapter adapter = new AmiP4ScmAdapter();
		String basePath = a[5];//"/home/rcooke/p4base/dev/amilayouts";
		adapter.init(null, a[0], a[1], a[2], a[3].toCharArray(), basePath, null);
		Set<String> files = new LinkedHashSet<String>();
		files.addAll(adapter.getFileNames(basePath));
		for (File f : new File(basePath).listFiles()) {
			files.add(IOH.getFullPath(f));
		}

		Map<String, String> changelists = adapter.getCurrentFileChangelists(CH.l(files));
		for (int i = 0; i < 5; i++) {
			System.out.println("started");
			Map<String, Byte> status = adapter.getFileStatus(CH.l(files));
			for (Entry<String, Byte> e : status.entrySet()) {
				System.out.println(e.getKey() + " ==> " + adapter.getStatusName(e.getValue().byteValue()) + "    " + changelists.get(e.getKey()));
			}
		}
		for (String file : files) {
			System.out.println(file);
			List<AmiScmRevision> history = adapter.getHistory(file);
			for (AmiScmRevision i : history) {
				System.out.println("    " + i.getRevision() + " " + i.getChangelistId() + " " + DateMillis.format(i.getTime()) + " " + i.getUser() + " " + i.getComment());
				System.out.println("    " + adapter.getFile(i.getName(), i.getChangelistId()).length);
			}

		}
	}
	@Override
	public void addFiles(List<String> fileName, byte type) throws AmiScmException {
		try {
			client.addFiles(FileSpecBuilder.makeFileSpecList(fileName), false, -1, type == TYPE_BINARY ? "binary" : "text", false);
		} catch (Exception e) {
			throw new AmiScmException("Error adding file " + fileName + " to perforce", e);
		}
	}
	@Override
	public void editFiles(List<String> fileName) throws AmiScmException {
		try {
			List<IFileSpec> fs = FileSpecBuilder.makeFileSpecList(fileName);
			this.client.editFiles(fs, false, false, -1, null);
		} catch (Exception e) {
			throw new AmiScmException("Error editing files " + fileName + " from perforce", e);
		}
	}
	@Override
	public void deleteFiles(List<String> fileName) throws AmiScmException {
		try {
			List<IFileSpec> fs = FileSpecBuilder.makeFileSpecList(fileName);
			this.client.deleteFiles(fs, -1, false);
		} catch (Exception e) {
			throw new AmiScmException("Error deleting files " + fileName + " from perforce", e);
		}
	}
	@Override
	public void revertFiles(List<String> fileName) throws AmiScmException {
		try {
			List<IFileSpec> fs = FileSpecBuilder.makeFileSpecList(fileName);
			this.client.revertFiles(fs, false, -1, false, false);
		} catch (Exception e) {
			throw new AmiScmException("Error reverting files " + fileName + " from perforce", e);
		}
	}
	@Override
	public void commitFiles(List<String> files, String comment) throws AmiScmException {
		try {
			if (comment == null)
				throw new AmiScmException("Perforce comment required");
			if (files.isEmpty())
				throw new AmiScmException("Error submitting to perforce, no files specified");
			List<IFileSpec> submitFiles = server.getDepotFiles(FileSpecBuilder.makeFileSpecList(files), false);
			Set<String> submitdepotNames = new HashSet<String>(getDepotPaths(submitFiles));

			IChangelist changeList = this.server.getChangelist(IChangelist.DEFAULT);
			List<IFileSpec> changeListFiles = changeList.getFiles(true);
			Set<String> changeListDepotNames = new HashSet<String>(getDepotPaths(changeListFiles));
			Set<String> extra = CH.comm(submitdepotNames, changeListDepotNames, true, false, false);
			if (!extra.isEmpty())
				throw new AmiScmException("Error submitting to perforce, following files are not checked out:  " + extra);
			Set<String> keepCheckedOut = CH.comm(submitdepotNames, changeListDepotNames, false, true, false);
			if (keepCheckedOut.isEmpty()) {
				changeList.setDescription(comment);
				changeList.submit(false);
			} else {
				IChangelist checkInChangeList = client.createChangelist(new Changelist());
				System.out.println("id: " + changeList.getId() + " ==> " + checkInChangeList.getId());
				List<IFileSpec> toCheckIn = new ArrayList<IFileSpec>();
				for (IFileSpec i : changeListFiles) {
					if (!keepCheckedOut.contains(i.getDepotPathString()))
						toCheckIn.add(i);
				}
				client.reopenFiles(toCheckIn, checkInChangeList.getId(), null);
				System.out.println("sz: " + changeList.getFiles(true).size() + " ==> " + checkInChangeList.getFiles(true).size());

				checkInChangeList.refresh();
				checkInChangeList.setDescription(comment);
				checkInChangeList.submit(false);
			}
		} catch (AmiScmException e) {
			throw e;
		} catch (Exception e) {
			throw new AmiScmException("Error submitting file '" + files + "' from perforce", e);
		}
	}
	@Override
	public byte[] getFile(String fileName, String changelist) throws AmiScmException {
		try {
			List<IFileSpec> fs = FileSpecBuilder.makeFileSpecList(fileName);
			int rev = SH.parseInt(changelist);
			fs.get(0).setChangelistId(rev);
			InputStream is = server.getFileContents(fs, false, true);
			try {
				return IOH.readData(is);
			} finally {
				IOH.close(is);
			}
		} catch (Exception e) {
			throw new AmiScmException("Error getting contents of '" + fileName + "', revision " + changelist + " from perforce", e);
		}
	}
	@Override
	public List<AmiScmRevision> getHistory(String fileName) throws AmiScmException {
		try {
			List<IFileSpec> fs = FileSpecBuilder.makeFileSpecList(fileName);

			Map<IFileSpec, List<IFileRevisionData>> t = server.getRevisionHistory(fs, -1, false, false, true, false);
			if (t.isEmpty())
				return Collections.EMPTY_LIST;
			List<IFileRevisionData> val = CH.first(t.values());
			if (CH.isEmpty(val))
				return Collections.EMPTY_LIST;
			List<AmiScmRevision> r = new ArrayList<AmiScmRevision>(val.size());
			for (IFileRevisionData data : val) {
				r.add(new AmiScmRevision(SH.toString(data.getChangelistId()), SH.toString(data.getRevision()), data.getDate().getTime(), data.getDepotFileName(),
						data.getUserName(), data.getDescription()));
			}
			return r;
		} catch (Exception e) {
			throw new AmiScmException("Error getting history at '" + fileName + "'", e);
		}
	}
	@Override
	public Map<String, Byte> getFileStatus(List<String> files) throws AmiScmException {
		final Map<String, Byte> r = new LinkedHashMap<String, Byte>();
		if (files.size() == 0)
			return r;
		final Set<String> remaining = new HashSet<String>(files);
		try {
			final IChangelist cl = server.getChangelist(IChangelist.DEFAULT);
			final List<IFileSpec> clientFiles = cl.getFiles(true);
			final List<IFileSpec> localFiles = toLocalFiles(clientFiles);
			for (int i = 0; i < clientFiles.size(); i++) {
				String path = localFiles.get(i).getLocalPathString();
				if (remaining.remove(path)) {
					FileAction fa = clientFiles.get(i).getAction();
					byte status;
					switch (fa) {
						case ADD:
							status = STATUS_MARKED_FOR_ADD;
							break;
						case EDIT:
							status = STATUS_CHECKED_OUT;
							break;
						case DELETE:
							status = STATUS_MARKED_FOR_DELETE;
							break;
						default:
							status = STATUS_CHECKED_IN;
							break;
					}
					r.put(path, status);
				}
			}
			if (!remaining.isEmpty()) {
				final List<String> notInChangelist = new ArrayList<String>(remaining);
				final GetDepotFilesOptions options = new GetDepotFilesOptions("-e");
				final List<IFileSpec> result = server.getDepotFiles(FileSpecBuilder.makeFileSpecList(notInChangelist), options);
				for (int i = 0; i < notInChangelist.size(); i++)
					r.put(notInChangelist.get(i), result.get(i).getDepotPathString() == null ? STATUS_PRIVATE : STATUS_CHECKED_IN);
			}
		} catch (Exception e) {
			throw new AmiScmException("Error getting perforce  depot files at '" + files + "'", e);
		}
		return r;
	}
	@Override
	public Map<String, String> getCurrentFileChangelists(List<String> files) throws AmiScmException {
		Map<String, String> r = new LinkedHashMap<String, String>();
		if (files.isEmpty())
			return r;
		List<String> filesWithrevision = new ArrayList<String>();
		IntArrayList revisions = new IntArrayList();
		try {
			List<IFileSpec> specs = client.haveList(FileSpecBuilder.makeFileSpecList(files));
			for (int i = 0; i < files.size(); i++) {
				final String path = files.get(i);
				final IFileSpec spec = specs.get(i);
				String apath = spec.getAnnotatedPathString(PathType.LOCAL);
				if (apath != null) {
					String rev = SH.afterLast(apath, "#", null);
					if (rev != null && SH.areBetween(rev, '0', '9')) {
						filesWithrevision.add(path);
						revisions.add(SH.parseInt(rev));
						continue;
					}
				}
				r.put(path, null);
			}

			if (!filesWithrevision.isEmpty()) {
				List<IFileSpec> fs = FileSpecBuilder.makeFileSpecList(filesWithrevision);
				for (int i = 0; i < filesWithrevision.size(); i++) {
					int irev = revisions.get(i);
					fs.get(i).setStartRevision(irev);
					fs.get(i).setEndRevision(irev);
				}
				List<IFileSpec> fs2 = server.getDepotFiles(fs, false);
				for (int i = 0; i < filesWithrevision.size(); i++) {
					int cl = fs2.get(i).getChangelistId();
					String path = filesWithrevision.get(i);
					if (cl == IChangelist.UNKNOWN)
						r.put(path, null);
					else
						r.put(path, SH.toString(cl));
				}
			}
		} catch (Exception e) {
			throw new AmiScmException("Error getting perforce depot files at '" + files + "'", e);
		}
		return r;
	}
	@Override
	public List<String> getFileNames(String directory) throws AmiScmException {
		try {
			List<IFileSpec> fs = FileSpecBuilder.makeFileSpecList(directory + "/...");
			List<IFileSpec> have = client.haveList(fs);
			List<IFileSpec> opened = client.openedFiles(fs, -1, -1);
			opened = toLocalFiles(opened);
			Set<String> r = new LinkedHashSet<String>(opened.size() + have.size());
			for (IFileSpec i : have)
				r.add(i.getLocalPathString());
			for (IFileSpec i : opened)
				r.add(i.getLocalPathString());
			return new ArrayList<String>(r);
		} catch (Exception e) {
			throw new AmiScmException("Error getting perforce depot files at '" + directory + "'", e);
		}
	}
	private void throwErrors(List<IFileSpec> fspecs) throws AmiScmException {
		for (IFileSpec i : fspecs)
			if (i.getOpStatus() == FileSpecOpStatus.CLIENT_ERROR || i.getOpStatus() == FileSpecOpStatus.ERROR)
				throw new AmiScmException(i.getStatusMessage());
	}
	private List<IFileSpec> toLocalFiles(List<IFileSpec> fspecs) throws ConnectionException, AccessException {
		List<String> t = getDepotPaths(fspecs);
		return client.where(FileSpecBuilder.makeFileSpecList(t));
	}
	private List<String> getDepotPaths(List<IFileSpec> fspecs) {
		List<String> t = new ArrayList<String>(fspecs.size());
		for (IFileSpec i : fspecs)
			t.add(i.getDepotPathString());
		return t;
	}
	@Override
	public void syncToChangelists(Map<String, String> files) throws AmiScmException {
		List<String> t = new ArrayList<String>(files.size());
		for (Entry<String, String> s : files.entrySet()) {
			if (SH.is(s.getValue()))
				t.add(s.getKey() + "@" + s.getValue());
			else
				t.add(s.getKey());
		}
		List<IFileSpec> fs = FileSpecBuilder.makeFileSpecList(t);
		try {
			this.client.sync(fs, false, false, false, false);
		} catch (Exception e) {
			throw new AmiScmException("Error syncing files from perforce depot", e);
		}
	}
	@Override
	public void syncDirectories(List<String> directories) throws AmiScmException {
		List<String> t = new ArrayList<String>(directories.size());
		for (String d : directories) {
			t.add(d + "/...");
		}
		List<IFileSpec> fs = FileSpecBuilder.makeFileSpecList(t);
		try {
			this.client.sync(fs, false, false, false, false);
		} catch (Exception e) {
			throw new AmiScmException("Error syncing files from perforce depot", e);
		}

	}
	@Override
	public String getRootDirectory() throws AmiScmException {
		return this.directory;
	}
	@Override
	public String getStatusName(byte status) {
		switch (status) {
			case STATUS_CHECKED_IN:
				return "Checked_In";
			case STATUS_CHECKED_OUT:
				return "Checked_Out";
			case STATUS_MARKED_FOR_ADD:
				return "Mark_For_Add";
			case STATUS_MARKED_FOR_DELETE:
				return "Mark_For_Delete";
			case STATUS_PRIVATE:
				return "Private";
			default:
				return SH.toString(status);
		}
	}
}
