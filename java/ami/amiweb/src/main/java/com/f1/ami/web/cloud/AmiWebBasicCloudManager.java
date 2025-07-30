package com.f1.ami.web.cloud;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.f1.ami.web.AmiWebFile;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.amiscript.AmiWebFileSystem;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class AmiWebBasicCloudManager implements AmiWebCloudManager {
	private static final Logger log = LH.get();

	private AmiWebFile cloudLayoutsDirectory;
	private String usersFileCache;

	private String PROPERTY_PREFIX = "user_properties.";

	private AmiWebFileSystem fs;

	private String cloudDirectoryString;

	public AmiWebBasicCloudManager(String cloudDirectory, AmiWebFileSystem fs) throws IOException {
		this.cloudDirectoryString = cloudDirectory;
		this.fs = fs;
	}
	public void init(AmiWebService service) {
		try {
			this.cloudLayoutsDirectory = fs.getFile(cloudDirectoryString);
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
		//		this.checkUsersUptodate();
	}

	@Override
	public String loadLayout(String name) {
		try {
			return getFile(name).readText();
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	@Override
	public AmiWebFile getFile(String name) {
		return fs.getFile(cloudLayoutsDirectory.getAbsolutePath() + '/' + name);
	}

	@Override
	public void saveLayout(String name, String layout) {
		try {
			AmiWebFile out = getFile(name);
			out.getParentFile().mkdirForce();
			out.writeText(layout);
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	@Override
	public AmiWebCloudLayoutTree getCloudLayouts() {
		if (!cloudLayoutsDirectory.isDirectory())
			return null;
		else
			return toCloudLayout("", cloudLayoutsDirectory);
	}

	private AmiWebCloudLayoutTree toCloudLayout(String prefix, AmiWebFile dir) {
		Map<String, String> layouts = new TreeMap<String, String>();
		Map<String, AmiWebCloudLayoutTree> children = new TreeMap<String, AmiWebCloudLayoutTree>();
		for (AmiWebFile file : dir.listFiles()) {
			String name = "".equals(prefix) ? file.getName() : (prefix + SEPERATOR + file.getName());
			if (file.isFile())
				layouts.put(file.getName(), name);
			else if (file.isDirectory())
				children.put(file.getName(), toCloudLayout(name, file));
		}
		String name = "".equals(prefix) ? "" : dir.getName();
		return new AmiWebCloudLayoutTree(prefix, name, layouts, children);
	}

	@Override
	public void removeLayout(String name) {
		if (name.indexOf("..") != -1)
			throw new RuntimeException("Illegal name: " + name);
		try {
			getFile(name).deleteForce();
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	//	private void checkUsersUptodate() {
	//		if (this.usersFileCache == this.usersFile.getText())
	//			return;
	//		this.usersFileCache = this.usersFile.getText();
	//		this.users.clear();
	//		try {
	//			for (String line : SH.splitLines(this.usersFileCache)) {
	//				if (SH.isnt(line))
	//					continue;
	//				if (!line.startsWith(PROPERTY_PREFIX))
	//					continue;
	//				line = SH.stripPrefix(line, PROPERTY_PREFIX, true);
	//				String un = SH.beforeFirst(line, '=', line);
	//				if (SH.isnt(un))
	//					continue;
	//				String propertiesText = SH.afterFirst(line, '=', "");
	//				Map<String, String> properties = new HashMap<String, String>();
	//				SH.splitToMap(properties, '|', '=', '\\', propertiesText);
	//				this.users.put(un, new AmiWebCloudUser((Map) properties));
	//			}
	//		} catch (Exception e) {
	//			LH.warning(log, "error processing: ", this.usersFile.getFile().getFullPath(), e);
	//			return;
	//		}
	//	}

	//	@Override
	//	public Set<String> getCloudUsers() {
	//		checkUsersUptodate();
	//		return users.keySet();
	//	}
	//
	//	@Override
	//	public AmiWebCloudUser removeCloudUser(String user) {
	//		checkUsersUptodate();
	//		AmiWebCloudUser r = users.remove(user);
	//		if (r == null)
	//			return null;
	//		writeUsers();
	//		return r;
	//	}
	//
	//	@Override
	//	public AmiWebCloudUser loadCloudUser(String user) {
	//		checkUsersUptodate();
	//		return users.get(user);
	//	}
	//
	//	@Override
	//	public void saveCloudUser(AmiWebCloudUser user) {
	//		checkUsersUptodate();
	//		AmiWebCloudUser r = users.put(user.getUsername(), user);
	//		writeUsers();
	//	}

	//	private void writeUsers() {
	//		//TODO: use safe file
	//		StringBuilder sb = new StringBuilder();
	//		for (String s : CH.sort(this.users.keySet())) {
	//			sb.append(PROPERTY_PREFIX);
	//			sb.append(s).append('=');
	//			AmiWebCloudUser user = this.users.get(s);
	//			SH.joinMap('|', '=', '\\', user.getProperties(), sb);
	//			sb.append(SH.NEWLINE);
	//		}
	//		try {
	//			this.usersFile.setText(sb.toString());
	//		} catch (IOException e) {
	//			throw new RuntimeException("Error writing to file:" + usersFile.getFile().getFullPath(), e);
	//		}
	//	}

	//	@Override
	//	public boolean hasAdmins() {
	//		checkUsersUptodate();
	//		for (AmiWebCloudUser i : this.users.values())
	//			if (i.isAdmin())
	//				return true;
	//		return false;
	//	}

	@Override
	public boolean isLayoutWriteable(String name) {
		return getFile(name).canWrite();
	}

	@Override
	public void setLayoutWriteable(String name, boolean b) {
		getFile(name).setWritable(b);
	}

	@Override
	public AmiWebFile getLayoutsRootDirectory() {
		return this.cloudLayoutsDirectory;
	}
}
