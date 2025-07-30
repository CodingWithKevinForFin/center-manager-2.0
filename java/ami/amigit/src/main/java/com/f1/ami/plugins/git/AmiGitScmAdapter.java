package com.f1.ami.plugins.git;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.RmCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.dircache.DirCacheIterator;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.transport.sshd.KeyPasswordProvider;
import org.eclipse.jgit.transport.sshd.SshdSessionFactory;
import org.eclipse.jgit.transport.sshd.SshdSessionFactoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import com.f1.ami.amicommon.AmiScmAdapter;
import com.f1.ami.amicommon.AmiScmException;
import com.f1.ami.amicommon.AmiScmRevision;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;

public class AmiGitScmAdapter implements AmiScmAdapter {
	private static final Logger log = LH.get();
	private static final String DEFAULT_BRANCH = "master";
	private static final String DEFAULT_REMOTE = "origin";
	private static final int MAX_SSH_PASS_ATTEMPTS = 5;
	//Git members:
	private UsernamePasswordCredentialsProvider credentialsProvider;
	private String uri;
	private String directory;
	public Repository fileRepository;
	private LsRemoteCommand lsCommand;
	private Git git;
	private static String GITMETAFILE = ".git";
	private StringBuilder sb = new StringBuilder();
	private String email;
	private String author;
	private Map<String, String> options;
	private String branch;
	private String remote;
	private String sshKey;
	private String sshKeyPass;

	private SshdSessionFactory factory;
	private TransportConfigCallback sshConfigCallback;
	private Function<CredentialsProvider, KeyPasswordProvider> passwordProviderFunction;

	private Boolean use_ssh = false;

	public AmiGitScmAdapter() {
	}

	@Override
	public void init(com.f1.container.ContainerTools tools, String url, String client, String username, char[] password, String basePath, String options) throws AmiScmException {
		if (SH.isnt(url))
			throw new AmiScmException("URL required");
		if (SH.isnt(client))
			throw new AmiScmException("Client required");
		if (SH.is(options)) {
			this.options = SH.splitToMap(',', '=', '\\', options);
		} else {
			this.options = Collections.<String, String> emptyMap();
		}
		initOptions();
		if (SH.isnt(username) && SH.isnt(this.sshKey))
			throw new AmiScmException("username or sshKey required");
		try {
			this.uri = url;
			this.lsCommand = new LsRemoteCommand(null);
			this.email = username;
			this.author = client;

			if (SH.is(this.sshKey)) {
				this.use_ssh = true;
				initSSH();
			} else {
				this.credentialsProvider = new UsernamePasswordCredentialsProvider(username, password);
			}

			if (this.use_ssh)
				this.lsCommand.setTransportConfigCallback(this.sshConfigCallback);
			else
				this.lsCommand.setCredentialsProvider(this.credentialsProvider);

			this.lsCommand.setRemote(this.uri);
			this.lsCommand.call();

			File fdirectory = new File(basePath);
			this.directory = this.getGitPath(IOH.getFullPath(fdirectory) + File.separatorChar);

			// Check if the git dir exists

			File findDirectory = new File(this.directory + GITMETAFILE);
			File gitDirectory = findDirectory(findDirectory);
			if (gitDirectory == null) {
				cloneDirectory(findDirectory);
			} else {
				this.fileRepository = new FileRepository(gitDirectory);
				this.git = new Git(fileRepository);
				checkGit();
			}
		} catch (Exception e) {
			throw new AmiScmException("Could not connect to git server: '" + url + "'" + "or Authentication failed: '" + username + "'", e);
		}
	}
	private void initOptions() {
		if (this.options.containsKey(AmiGitScmPlugin.OPTION_BRANCH))
			this.branch = this.options.get(AmiGitScmPlugin.OPTION_BRANCH);
		else
			this.branch = DEFAULT_BRANCH;
		if (this.options.containsKey(AmiGitScmPlugin.OPTION_REMOTE))
			this.remote = this.options.get(AmiGitScmPlugin.OPTION_REMOTE);
		else
			this.remote = DEFAULT_REMOTE;
		if (this.options.containsKey(AmiGitScmPlugin.OPTION_SSH_KEY))
			this.sshKey = this.options.get(AmiGitScmPlugin.OPTION_SSH_KEY);
		if (this.options.containsKey(AmiGitScmPlugin.OPTION_SSH_KEY_PASS))
			this.sshKeyPass = this.options.get(AmiGitScmPlugin.OPTION_SSH_KEY_PASS);
		//System.out.println(sshKey);
		//System.out.println(sshKeyPass);
	}

	private void checkGit() throws AmiScmException {
		Repository currentRepo = this.git.getRepository();
		try {
			//Ensure that local git repository is up to date
			fetchOrigin();

			//Checkout branch if HEAD is pointing at a different branch
			String currentBranch = currentRepo.getBranch();
			if (!SH.equals(currentBranch, this.branch)) {
				checkoutBranch(this.branch);
			}
		} catch (IOException e) {
			throw new AmiScmException("Could not get current repo's branch: " + e);

		}
	}

	private void checkoutBranch(String branchName) throws AmiScmException {
		try {
			//Check if local ref exists, otherwise create a new local branch
			Ref ref = this.git.getRepository().exactRef("/refs/heads/" + branchName);
			CheckoutCommand checkout = git.checkout();
			if (ref != null) {
				checkout.setCreateBranch(false);
			} else {
				checkout.setCreateBranch(true);
				checkout.setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM);
				checkout.setStartPoint(this.remote + "/" + branchName);
			}
			checkout.setName(branchName);
			checkout.call();
		} catch (Exception e) {
			throw new AmiScmException("Could not checkout branch: " + e);
		}
	}

	private void fetchOrigin() throws AmiScmException {
		//Perform a fetch
		try {
			FetchCommand fetch = git.fetch();
			fetch.setRemote(this.remote);
			if (use_ssh)
				fetch.setTransportConfigCallback(this.sshConfigCallback);
			else
				fetch.setCredentialsProvider(this.credentialsProvider);

			fetch.setRemote(this.remote);
			fetch.call();
		} catch (GitAPIException e) {
			throw new AmiScmException("Could not get fetch origin: " + e);
		}
	}

	private void initSSH() {

		SshdSessionFactoryBuilder builder = new SshdSessionFactoryBuilder().setPreferredAuthentications("publickey").setHomeDirectory(new File(""))
				.setSshDirectory(new File(this.sshKey));

		// Create keypass provider if needed
		if (SH.is(this.sshKeyPass)) {
			this.passwordProviderFunction = new Function<CredentialsProvider, KeyPasswordProvider>() {
				@Override
				public KeyPasswordProvider apply(CredentialsProvider t) {
					return new KeyPasswordProvider() {
						@Override
						public char[] getPassphrase(URIish uri, int attempt) throws IOException {
							return sshKeyPass.toCharArray();
						}

						@Override
						public void setAttempts(int maxNumberOfAttempts) {
							maxNumberOfAttempts = MAX_SSH_PASS_ATTEMPTS;
						}

						@Override
						public boolean keyLoaded(URIish uri, int attempt, Exception error) throws IOException, GeneralSecurityException {
							return false;
						}

					};
				}
			};

			builder = builder.setKeyPasswordProvider(this.passwordProviderFunction);
		}

		this.factory = builder.build(null);

		// Initialize callback
		this.sshConfigCallback = new TransportConfigCallback() {
			@Override
			public void configure(Transport transport) {
				SshTransport sshTransport = (SshTransport) transport;
				sshTransport.setSshSessionFactory(factory);
			}

		};
	}

	private File findDirectory(File findDirectory) {
		FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
		repositoryBuilder.addCeilingDirectory(findDirectory.getParentFile());
		repositoryBuilder.findGitDir(findDirectory);
		File gitDirectory = repositoryBuilder.getGitDir();
		return gitDirectory;

	}
	private void cloneDirectory(File findDirectory) throws InvalidRemoteException, TransportException, GitAPIException, IOException {
		CloneCommand clone = Git.cloneRepository();
		clone.setDirectory(findDirectory.getParentFile());
		clone.setBranch(this.branch);
		clone.setRemote(this.remote);
		clone.setURI(this.uri);
		if (this.use_ssh)
			clone.setTransportConfigCallback(this.sshConfigCallback);
		else
			clone.setCredentialsProvider(this.credentialsProvider);
		clone.call();
		this.fileRepository = new FileRepository(findDirectory(findDirectory));
		this.git = new Git(fileRepository);
		StoredConfig gitConfig = this.git.getRepository().getConfig();
		gitConfig.setBoolean("core", null, "filemode", false);
		gitConfig.save();

	}
	public static void main(String a[]) throws Exception {
		AmiGitScmAdapter adapter = new AmiGitScmAdapter();
		adapter.init(null, "https://github.com/3forge-george/sandbox.git", "test", "3forge-george", "saf".toCharArray(), "", null);
		adapter.getFileNames("/home/share/temp/George/git-dir/sandbox/.git");
	}
	private String getFullPath(String relativePath) {
		SH.clear(sb);
		sb.append(directory).append(relativePath);
		return sb.toString();
	}
	//Git requires / to delimit directories
	private String getGitPath(String fullPath) {
		return SH.replaceAll(fullPath, "\\", "/");
	}
	private String getRelativePath(String fullPath) {
		return SH.afterFirst(this.getGitPath(fullPath), this.directory);
	}
	@Override
	public String getStatusName(byte byteValue) {
		switch (byteValue) {
			case STATUS_CHECKED_IN:
				return "Tracked";
			case STATUS_CHECKED_OUT:
				return "Changed";
			case STATUS_MARKED_FOR_ADD:
				return "Added";
			case STATUS_MARKED_FOR_DELETE:
				return "Removed";
			case STATUS_CONFLICTING:
				return "Conflicting";
			case STATUS_MODIFIED:
				return "Modified";
			case STATUS_PRIVATE:
				return "Untracked";
			default:
				return SH.toString(byteValue);
		}
	}
	@Override
	public void addFiles(List<String> fileName, byte type) throws AmiScmException {
		try {
			AddCommand add = git.add();
			boolean hasAdd = false;
			for (int i = 0; i < fileName.size(); i++) {
				add.addFilepattern(getRelativePath(fileName.get(i)));
				hasAdd = true;
			}
			if (hasAdd)
				add.call();
		} catch (Exception e) {
			throw new AmiScmException("Error adding file " + fileName + " to git", e);
		}
	}
	@Override
	public void editFiles(List<String> fileName) throws AmiScmException {
	}
	@Override
	public void deleteFiles(List<String> fileName) throws AmiScmException {
		try {
			RmCommand rm = git.rm();
			boolean hasRm = false;
			for (int i = 0; i < fileName.size(); i++) {
				String fullPath = fileName.get(i);
				String relativePath = getRelativePath(fullPath);
				rm.addFilepattern(relativePath);
				hasRm = true;
			}
			if (hasRm)
				rm.call();

		} catch (Exception e) {
			throw new AmiScmException("Error deleting files " + fileName + " from git", e);
		}
	}
	@Override
	public void revertFiles(List<String> fileName) throws AmiScmException {
		try {
			StatusCommand sc = git.status();
			Status call = null;
			for (int i = 0; i < fileName.size(); i++) {
				sc.addPath(getRelativePath(fileName.get(i)));
			}
			call = sc.call();
			Set<String> added = call.getAdded();
			Set<String> changed = call.getChanged();
			Set<String> modified = call.getModified();
			Set<String> removed = call.getRemoved();

			Set<String> staged = new HashSet<String>();
			staged.addAll(added);
			staged.addAll(changed);
			staged.addAll(removed);

			if (staged.size() > 0) {
				ResetCommand reset = git.reset();
				for (String relativePath : staged) {
					reset.addPath(relativePath);
				}
				reset.call();

			}
			Set<String> unstaged = new HashSet<String>();
			unstaged.addAll(modified);
			unstaged.addAll(removed);

			if (unstaged.size() > 0) {
				CheckoutCommand checkout = git.checkout();
				for (String relativePath : unstaged) {
					checkout.addPath(relativePath);
				}
				checkout.call();
			}
		} catch (Exception e) {
			throw new AmiScmException("Error reverting files " + fileName + " from git", e);
		}
	}
	@Override
	public void commitFiles(List<String> fileName, String comment) throws AmiScmException {
		try {
			StatusCommand sc = git.status();
			Status call = sc.call();

			// Unstage files that aren't committed
			Set<String> commitFiles = new HashSet<String>();
			for (String file : fileName) {
				commitFiles.add(this.getGitPath(file));
			}
			Set<String> changed = call.getChanged();
			Set<String> added = call.getAdded();
			Set<String> removed = call.getRemoved();

			List<String> addedDontCommit = new ArrayList<String>();
			List<String> removedDontCommit = new ArrayList<String>();

			for (String relativePath : changed) {
				if (!commitFiles.contains(getFullPath(relativePath)))
					addedDontCommit.add(relativePath);
			}

			for (String relativePath : added) {
				if (!commitFiles.contains(getFullPath(relativePath)))
					addedDontCommit.add(relativePath);
			}

			for (String relativePath : removed) {
				if (!commitFiles.contains(getFullPath(relativePath)))
					removedDontCommit.add(relativePath);
			}

			List<String> unstageFiles = new ArrayList<String>();
			unstageFiles.addAll(addedDontCommit);
			unstageFiles.addAll(removedDontCommit);
			if (unstageFiles.size() > 0)
				this.revertFiles(unstageFiles);

			// Do Commit
			CommitCommand commit = git.commit();
			commit.setMessage(comment);
			commit.setAuthor(this.author, this.email);
			commit.setCommitter(this.author, this.email);
			commit.call();

			// Push
			PushCommand push = git.push();
			push.setRemote(this.remote);
			if (use_ssh)
				push.setTransportConfigCallback(this.sshConfigCallback);
			else
				push.setCredentialsProvider(this.credentialsProvider);
			push.call();

			// Restage files 
			this.addFiles(addedDontCommit, AmiScmAdapter.TYPE_TEXT);
			this.deleteFiles(removedDontCommit);

		} catch (Exception e) {
			throw new AmiScmException("Error submitting file '" + fileName + "' from git", e);
		}
	}
	@Override
	public byte[] getFile(String fileName, String changelist) throws AmiScmException {
		RevWalk revWalk = null;
		TreeWalk treeWalk = null;
		try {
			revWalk = new RevWalk(this.fileRepository);
			RevCommit revCommit = revWalk.parseCommit(this.fileRepository.resolve(changelist));
			RevTree tree = revCommit.getTree();
			treeWalk = new TreeWalk(this.fileRepository);
			treeWalk.addTree(tree);
			treeWalk.setRecursive(true);
			treeWalk.setFilter(PathFilter.create(getRelativePath(fileName)));
			if (!treeWalk.next()) {
				throw new IllegalStateException("Could not find file");
			}
			ObjectId objectId = treeWalk.getObjectId(0);
			ObjectLoader loader = this.fileRepository.open(objectId);
			return loader.getBytes();
		} catch (Exception e) {
			throw new AmiScmException("Error getting contents of '" + fileName + "', revision " + changelist + " from git", e);
		} finally {
			if (revWalk != null)
				revWalk.close();
			if (treeWalk != null)
				treeWalk.close();
		}
	}
	@Override
	public List<AmiScmRevision> getHistory(String fileName) throws AmiScmException {
		try {
			FetchCommand fetch = git.fetch();
			if (use_ssh)
				fetch.setTransportConfigCallback(this.sshConfigCallback);
			else
				fetch.setCredentialsProvider(this.credentialsProvider);

			fetch.setRemote(this.remote);
			fetch.call();

			List<AmiScmRevision> r = new ArrayList<AmiScmRevision>();
			LogCommand log = git.log();
			log.all();
			String relativePath = getRelativePath(fileName);
			log.addPath(relativePath);
			Iterable<RevCommit> commits = log.call();
			for (RevCommit commit : commits) {
				AmiScmRevision e = new AmiScmRevision(commit.getName(), null, commit.getCommitTime() * 1000L, fileName, commit.getAuthorIdent().getEmailAddress(),
						commit.getFullMessage());
				r.add(e);
			}

			return r;
		} catch (Exception e) {
			throw new AmiScmException("Error getting history at '" + fileName + "'", e);
		}
	}
	@Override
	public Map<String, Byte> getFileStatus(List<String> files) throws AmiScmException {
		final Map<String, Byte> r = new LinkedHashMap<String, Byte>();
		//If files is null get status on repository
		if (files != null && files.size() == 0)
			return r;

		Map<String, String> gitFileToOriginal = new HashMap<String, String>();
		StatusCommand sc = git.status();
		Status call = null;
		if (files != null)
			for (int i = 0; i < files.size(); i++) {
				String relativePath = getRelativePath(files.get(i));
				sc.addPath(relativePath);
				gitFileToOriginal.put(this.getGitPath(files.get(i)), files.get(i));
			}
		try {
			call = sc.call();
		} catch (Exception e) {
			throw new AmiScmException("Error getting file status ", e);
		}
		return convertGitStatusToScm(gitFileToOriginal, call, r);
	}
	private Map<String, Byte> convertGitStatusToScm(Map<String, String> gitFileToOrignal, Status call, Map<String, Byte> r) {
		if (call == null)
			return r;
		for (String s : call.getModified()) {
			if (r.containsKey(getFullPath(s)))
				LH.info(log, s, "Status Already Set To ", r.get(s), "Setting to ", STATUS_MODIFIED);
			r.put(gitFileToOrignal.get(getFullPath(s)), STATUS_MODIFIED);
		}
		for (String s : call.getAdded()) {
			if (r.containsKey(getFullPath(s)))
				LH.info(log, s, "Status Already Set To ", r.get(s), "Setting to ", STATUS_MARKED_FOR_ADD);
			r.put(gitFileToOrignal.get(getFullPath(s)), STATUS_MARKED_FOR_ADD);
		}
		for (String s : call.getRemoved()) {
			if (r.containsKey(getFullPath(s)))
				LH.info(log, s, "Status Already Set To ", r.get(s), "Setting to ", STATUS_MARKED_FOR_DELETE);
			r.put(gitFileToOrignal.get(getFullPath(s)), STATUS_MARKED_FOR_DELETE);
		}
		for (String s : call.getChanged()) {
			if (r.containsKey(getFullPath(s)))
				LH.info(log, s, "Status Already Set To ", r.get(s), "Setting to ", STATUS_CHECKED_OUT);
			r.put(gitFileToOrignal.get(getFullPath(s)), STATUS_CHECKED_OUT);
		}
		for (String s : call.getConflicting()) {
			if (r.containsKey(getFullPath(s)))
				LH.info(log, s, "Status Already Set To ", r.get(s), "Setting to ", STATUS_CONFLICTING);
			r.put(gitFileToOrignal.get(getFullPath(s)), STATUS_CONFLICTING);
		}
		for (String s : call.getUntracked()) {
			if (r.containsKey(getFullPath(s)))
				LH.info(log, s, "Status Already Set To ", r.get(s), "Setting to ", STATUS_PRIVATE);
			r.put(gitFileToOrignal.get(getFullPath(s)), STATUS_PRIVATE);
		}

		for (Map.Entry<String, String> entry : gitFileToOrignal.entrySet()) {
			//			String location = entry.getKey();
			String originalFileName = entry.getValue();
			if (!r.containsKey(originalFileName))
				r.put(originalFileName, STATUS_CHECKED_IN);
		}
		/*
		if (files != null)
			for (int i = 0; i < files.size(); i++) {
				String location = files.get(i);
				if (!r.containsKey(location))
					r.put(location, STATUS_CHECKED_IN);
			}
			*/
		return r;
	}

	@Override
	public Map<String, String> getCurrentFileChangelists(List<String> files) throws AmiScmException {
		Map<String, String> r = new LinkedHashMap<String, String>();
		if (files.isEmpty())
			return r;

		String rev = null;
		RevWalk revWalk = null;
		try {
			StatusCommand sc = git.status();
			Status call = null;
			for (int i = 0; i < files.size(); i++) {
				sc.addPath(getRelativePath(files.get(i)));
			}
			call = sc.call();
			Set<String> untracked = call.getUntracked();
			Set<String> added = call.getAdded();

			Ref head = fileRepository.findRef("HEAD");

			revWalk = new RevWalk(fileRepository);
			RevCommit revCom = revWalk.parseCommit(head.getObjectId());
			rev = revCom.getName();
			for (int i = 0; i < files.size(); i++) {
				final String path = files.get(i);
				if (untracked.contains(getRelativePath(path)))
					r.put(path, null);
				else if (added.contains(getRelativePath(path)))
					r.put(path, null);
				else
					r.put(path, rev);
			}
		} catch (Exception e) {
			throw new AmiScmException("Error getting git depot files at '" + files + "'", e);
		} finally {
			if (revWalk != null) {
				revWalk.close();
			}
		}

		return r;
	}
	@Override
	public List<String> getFileNames(String directory) throws AmiScmException {
		RevWalk revWalk = null;
		TreeWalk treeWalk = null;
		try {
			ArrayList<String> r = new ArrayList<String>();

			Ref head = fileRepository.findRef("HEAD");

			revWalk = new RevWalk(fileRepository);
			ObjectId objectId = head.getObjectId();

			if (objectId == null) {
				throw new AmiScmException("Could not resolve git repository HEAD at '" + head.getTarget() + "'");
			}

			RevCommit revCom = revWalk.parseCommit(objectId);
			treeWalk = new TreeWalk(fileRepository);
			treeWalk.addTree(revCom.getTree());
			treeWalk.addTree(new DirCacheIterator(fileRepository.readDirCache()));
			treeWalk.setRecursive(true);

			//Get tracked files
			while (treeWalk.next()) {
				r.add(getFullPath(treeWalk.getPathString()));
			}

			//Get Untracked files
			Set<String> untracked = this.git.status().call().getUntracked();
			for (String file : untracked) {
				r.add(getFullPath(file));
			}

			return r;
		} catch (Exception e) {
			throw new AmiScmException("Error getting git depot files at '" + directory + "'", e);
		} finally {
			if (revWalk != null)
				revWalk.close();
			if (treeWalk != null)
				treeWalk.close();
		}
	}

	@Override
	public void syncToChangelists(Map<String, String> files) throws AmiScmException {
		try {
			CheckoutCommand checkout = git.checkout();
			for (String fullPath : files.keySet()) {
				String changeList = files.get(fullPath);
				String relativePath = getRelativePath(fullPath);
				checkout.addPath(relativePath);
				checkout.setStartPoint(changeList);
				checkout.call();
			}
		} catch (Exception e) {
			throw new AmiScmException("Error syncing files from git depot", e);
		}
	}
	@Override
	public void syncDirectories(List<String> directories) throws AmiScmException {
		try {
			PullCommand pull = git.pull();
			if (use_ssh)
				pull.setTransportConfigCallback(this.sshConfigCallback);
			else
				pull.setCredentialsProvider(this.credentialsProvider);
			pull.setRemote(this.remote);
			pull.setRemoteBranchName(this.branch);
			pull.call();

		} catch (Exception e) {
			throw new AmiScmException("Error syncing files from git depot", e);
		}

	}
	@Override
	public String getRootDirectory() throws AmiScmException {
		return this.directory;
	}

}
