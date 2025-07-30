package com.f1.vortex.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.utils.Duration;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.SearchPath;
import com.f1.utils.ToDoException;
import com.f1.utils.structs.MapInMap;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.xml.XmlElement;
import com.f1.utils.xml.XmlParser;

public class MavenManager {

	final private XmlParser xmlParser = new XmlParser();
	final private MapInMap<String, String, PomInstance> groupArtifactToPom = new MapInMap<String, String, PomInstance>();
	final private Map<String, PomInstance> dirToPom = new HashMap<String, PomInstance>();
	private File mavenSettingsFile;
	private String mirrorUrl;
	private boolean verbose;

	public MavenManager(boolean verbose) {
		this.verbose = verbose;
	}

	public void resolve() {
		for (PomInstance p : groupArtifactToPom.valuesMulti())
			for (PomDependency dep : p.getDependencies(true)) {
				if (!"1.00-SNAPSHOT".equals(dep.getVersion()))
					continue;
				PomInstance target = groupArtifactToPom.getMulti(dep.getGroupId(), dep.getArtifactId());
				if (target == null) {
					System.err.println("Could not resolve dependency for " + p.getName() + ": " + dep.getGroupId() + "::" + dep.getArtifactId());
				} else {
					dep.setTarget(target);
				}
			}
	}

	public static void main(String a[]) throws IOException {
		MavenManager pm = new MavenManager(true);
		Duration d = new Duration();
		pm.setMavenSettingsFile(new File("/home/rob/.m2/settings.xml"));
		//		PomInstance pom = pm.addLocalPom(new File("/home/rcooke/p4base/dev/java/vortex/eye/pom.xml"));

		//PomInstance pom = pm.addLocalPom(new File("/tmp/java/backend/sfnoncore/pom.xml"));
		try {
			PomInstance pom = pm.addLocalPom(new File("/home/rob/p4sfbase/dev/java/backend/sfnoncore/pom.xml"));
			pm.resolve();
			pm.setLocalRepo(new File("/tmp/repo"));
			Map<Tuple2<String, String>, PomInstance> remoteDependencies = new LinkedHashMap<Tuple2<String, String>, PomInstance>();
			pm.resolveRemoteDependencies(pom, remoteDependencies, true, true);
			Collection<PomDependency> sink = pom.getAllDependencies(true);
			SearchPath sp = new SearchPath();
			for (PomDependency dep : sink) {
				if (dep.getTarget() != null) {
					sp.addDirectory(new File(dep.getTarget().getDirectoryPath() + pm.getSourcePath()));
				}
			}
			List<File> files = sp.search("*.java", SearchPath.OPTION_CONTINUE_AFTER_FIRST_FIND | SearchPath.OPTION_IS_PATTERN | SearchPath.OPTION_RECURSE);

			StringBuilder sb = new StringBuilder();
			for (File f : files)
				sb.append(f).append(SH.NEWLINE);
			IOH.writeText(new File("/tmp/javafiles"), sb.toString());
			d.stampStdout();
		} catch (Exception e) {
			System.err.println(SH.printStackTrace(e));
		}
	}
	public PomInstance addLocalPom(File pomDir) throws IOException {
		return addPom(pomDir, false, true, false);
	}
	public PomInstance addRemotePom(File pomDir) throws IOException {
		return addPom(pomDir, false, false, false);
	}
	public String getSourcePath() {
		return "src/main/java";
	}
	public String getTestPath() {
		return "src/test/java";
	}
	public String getResourcesPath() {
		return "src/main/resources";
	}

	private PomInstance addPom(File pomFile, boolean fromParent, boolean isLocal, boolean cachedAsWell) throws IOException {
		final String fullPomPath = IOH.getFullPath(pomFile);
		if (dirToPom.containsKey(fullPomPath))
			if (cachedAsWell) {
				return dirToPom.get(fullPomPath);
			} else
				return null;
		log("Using ", isLocal ? "local " : "remote", "  pom file: ", fullPomPath, cachedAsWell ? "    <cached>" : "    ");
		final PomInstance r = new PomInstance(pomFile, isLocal);
		if (!pomFile.isFile()) {
			if (isLocal) {
				System.out.println("Skipping missing pom: " + IOH.getFullPath(pomFile));
				return null;
			} else
				throw new FileNotFoundException(IOH.getFullPath(pomFile));
		}
		dirToPom.put(fullPomPath, r);

		final XmlElement doc = xmlParser.parseDocument(pomFile);
		r.setDocument(doc);

		XmlElement parent = doc.getFirstElement("parent");
		if (parent != null && !fromParent) {
			if (isLocal) {
				PomInstance t = addPom(IOH.joinPaths(pomFile.getParentFile(), parent.getFirstElement("relativePath").getInnerAsString(), "pom.xml"), false, true, true);
				t.addChild(r);
			} else {
				File fp = getRemotePom(parent.getFirstElement("groupId").getInnerAsString(), parent.getFirstElement("artifactId").getInnerAsString(),
						parent.getFirstElement("version").getInnerAsString(), ".pom");

				PomInstance t = addPom(fp, false, false, true);
				if (t != null)
					t.addChild(r);
			}
		}

		XmlElement name = doc.getFirstElement("name");
		if (name != null)
			r.setName(name.getInnerAsString());
		r.setArtifactId(doc.getFirstElement("artifactId").getInnerAsString());
		XmlElement groupId = doc.getFirstElement("groupId");
		if (groupId != null)
			r.setGroupId(groupId.getInnerAsString());
		XmlElement version = doc.getFirstElement("version");
		if (version != null)
			r.setVersion(version.getInnerAsString());

		final XmlElement modules = doc.getFirstElement("modules");
		if (modules != null) {
			for (XmlElement module : modules.getElements()) {
				String moduleName = module.getInnerAsString();
				if (isLocal) {
					PomInstance child = addPom(IOH.joinPaths(r.getDirectoryPath(), moduleName, "pom.xml"), true, true, true);
					if (child != null)
						r.addChild(child);
				}
			}
		}
		final XmlElement properties = doc.getFirstElement("properties");
		if (properties != null) {
			for (XmlElement property : properties.getElements()) {
				r.addProperty(property.getName(), property.getInnerAsString());
			}
		}

		final XmlElement dependencies = doc.getFirstElement("dependencies");
		if (dependencies != null) {
			for (XmlElement dependency : dependencies.getElements("dependency")) {
				XmlElement optional = dependency.getFirstElement("optional");
				XmlElement scope = dependency.getFirstElement("scope");
				if (scope != null && OH.in(scope.getInnerAsString(), "provided"))
					continue;
				String scopeText = scope == null ? null : scope.getInnerAsString();
				final String depGroupId = dependency.getFirstElement("groupId").getInnerAsString();
				final String depArtifactId = dependency.getFirstElement("artifactId").getInnerAsString();
				final XmlElement depType = dependency.getFirstElement("type");
				final XmlElement depClassifiers = dependency.getFirstElement("classifier");
				String depVersion = null;
				if (dependency.getFirstElement("version") == null) {
					if (!r.isLocal()) {
						try {
							if (parent != null) {
								final String parGroupId = parent.getFirstElement("groupId").getInnerAsString();
								final String parArtifactId = parent.getFirstElement("artifactId").getInnerAsString();
								final String parVersion = parent.getFirstElement("version").getInnerAsString();
								File pom = getRemotePom(parGroupId, parArtifactId, parVersion, ".pom");
								PomInstance parentPom = addRemotePom(pom);
								if (parentPom == null) {
									parentPom = dirToPom.get(IOH.getFullPath(pom));
								} else
									parentPom.addChild(r);
								while (parentPom != null) {
									XmlElement dep = parentPom.getVersionFromDepencencyManagement(depGroupId, depArtifactId);
									if (dep != null) {
										depVersion = dep.getFirstElement("version").getInnerAsString();
										if (scope == null)
											scope = dep.getFirstElement("scope");
										if (optional == null)
											optional = dep.getFirstElement("optional");
										break;
									}
									parentPom = parentPom.getParent();
								}
								if ("${project.version}".equals(depVersion)) {
									depVersion = parentPom.getDocument().getFirstElement("version").getInnerAsString();
								}
							}
						} catch (Exception e) {
							throw new RuntimeException("Error in " + pomFile + ": " + parent, e);
						}

					} else
						throw new ToDoException("get dependencyManagement for local poms");
					//TODO: get the dependencyManagement from the parent pom
				} else {
					depVersion = dependency.getFirstElement("version").getInnerAsString();
					if ("${project.version}".equals(depVersion)) {
						depVersion = dependency.getFirstElement("version").getInnerAsString();
					}
				}
				boolean isOptional = optional != null && "true".equals(optional.getInnerAsString());
				boolean dependencyCanFail = scope != null && OH.in(scope.getInnerAsString(), "test", "provided");
				if (depVersion != null)
					r.addDependency(new PomDependency(depGroupId, depArtifactId, depVersion, depType == null ? "jar" : depType.getInnerAsString(),
							depClassifiers == null ? null : depClassifiers.getInnerAsString(), dependencyCanFail || isOptional, scopeText));
			}
		}
		groupArtifactToPom.putMulti(r.getGroupId(), r.getArtifactId(), r);
		return r;
	}
	public void setMavenSettingsFile(File file) throws IOException {
		log("Set maven Settings File: ", file);
		this.mavenSettingsFile = file;
		IOH.assertFileExists(this.mavenSettingsFile, "mavent setting file");
		XmlElement doc = new XmlParser().parseDocument(IOH.readText(mavenSettingsFile));
		this.mirrorUrl = doc.getFirstElement("mirrors").getFirstElement("mirror").getFirstElement("url").getInnerAsString();
		log("Mirror url: ", mirrorUrl);
	}

	private File localRepo;
	private File localMetaRepo;

	public void resolveRemoteDependencies(PomInstance pomInstance, Map<Tuple2<String, String>, PomInstance> localJarFilesSink, boolean includeTest, boolean includeOptional)
			throws IOException {
		resolveRemoteDependencies(pomInstance, localJarFilesSink, includeTest, includeOptional, new HashSet<PomInstance>());
	}

	public void resolveRemoteDependencies(PomInstance pomInstance, Map<Tuple2<String, String>, PomInstance> localJarFilesSink, boolean includeTest, boolean includeOptional,
			Set<PomInstance> found) throws IOException {
		if (pomInstance.toString().indexOf("springframework") != -1)
			System.out.println("How: " + pomInstance);
		if (!found.add(pomInstance))
			return;
		try {
			if (mirrorUrl == null)
				throw new IllegalStateException("set maven settings first");
			if (localRepo == null)
				throw new IllegalStateException("set local repo first");

			//resolve the parent first...apparently people put properties there
			if (pomInstance.getParent() != null)
				resolveRemoteDependencies(pomInstance.getParent(), localJarFilesSink, includeTest, includeOptional, found);

			for (PomDependency dep : pomInstance.getDependencies(includeTest)) {
				if (!includeOptional && dep.getIsOptional())
					continue;
				if (dep.getTarget() == null) {
					try {
						final File localPomFile = getRemotePom(pomInstance, dep, ".pom");
						PomInstance pom = addPom(localPomFile, false, false, true);
						if (pom != null) {
							final File localJarFile;
							if ("pom".equals(dep.getType()) || "natives".equals(dep.getClassifier()))
								localJarFile = null;
							else
								localJarFile = getRemotePom(pomInstance, dep, ".jar");
							pom.setJarFile(localJarFile);
							resolveRemoteDependencies(pom, localJarFilesSink, includeTest, includeOptional, found);
							if (pom.getGroupId() == null)
								pom.setGroupId(dep.getGroupId());
							if (pom.getVersion() == null)
								pom.setVersion(dep.getVersion());
							Tuple2<String, String> groupAndArtifact = pom.getGroupAndArtifactId();
							PomInstance existing = localJarFilesSink.get(groupAndArtifact);
							if (existing == null || OH.gt(pom.getVersion(), existing.getVersion())) {
								localJarFilesSink.put(groupAndArtifact, pom);
							}
						}
					} catch (Exception e) {
						if (!dep.getIsOptional())
							throw new RuntimeException("Error for: " + dep, e);
					}
				} else
					resolveRemoteDependencies(dep.getTarget(), localJarFilesSink, includeTest, includeOptional, found);

			}
		} catch (Exception e) {
			throw new RuntimeException("Error for: " + pomInstance, e);
		}

	}
	public File getRemotePom(PomInstance pom, PomDependency i, String string) throws IOException {
		return getRemotePom(pom.applyProperties(i.getGroupId()), pom.applyProperties(i.getArtifactId()), pom.applyProperties(i.getVersion()), string);
	}
	private File getRemotePom(String groupId, String artifactId, String version, String extension) throws IOException {
		try {
			version = parseVersion(version);
			String pomFileName = artifactId + "-" + version + extension;
			File localFile = new File(localRepo, groupId + "-" + pomFileName);
			if (!localFile.isFile()) {
				final URL sourceUrl = new URL(mirrorUrl + "/" + SH.replaceAll(groupId, '.', '/') + "/" + artifactId + "/" + version + "/" + pomFileName);
				try {
					IOH.downloadFile(sourceUrl, localFile);
				} catch (FileNotFoundException e) {
					final URL metadata = new URL(mirrorUrl + "/" + SH.replaceAll(groupId, '.', '/') + "/" + artifactId + "/" + version + "/" + "maven-metadata.xml");
					File localFile2 = new File(localMetaRepo, "maven-metadata.xml_" + pomFileName);
					if (!localFile2.exists())
						try {
							IOH.downloadFile(metadata, localFile2);
						} catch (Exception e2) {
							log("Failed to get both pom: " + sourceUrl + " and metadata: " + metadata);
							throw e;
						}
					return getRemotePomForMetadata(localFile2, ".pom");
				}
				log("get Remote Pom: groupId=", groupId, ", artifactId=", artifactId, ", version=", version, ", extension=", extension, " local=", localFile);
			}
			return localFile;
		} catch (Exception e) {
			throw new RuntimeException("Error for pom: groupId=" + groupId + ", artifactId=" + artifactId + " version=" + version + " extension=" + extension, e);
		}
	}
	private File getRemotePomForMetadata(File metadataFile, String extension) throws IOException {
		try {
			log("Reading metadata from", metadataFile);
			XmlElement metadata = xmlParser.parseDocument(IOH.readText(metadataFile));
			String groupId = metadata.getFirstElement("groupId").getInnerAsString();
			String artifactId = metadata.getFirstElement("artifactId").getInnerAsString();
			String version = metadata.getFirstElement("version").getInnerAsString();
			XmlElement versioning = metadata.getFirstElement("versioning");
			XmlElement snapshot = versioning == null ? null : versioning.getFirstElement("snapshot");
			String timestamp = snapshot == null ? null : snapshot.getFirstElement("timestamp").getInnerAsString();
			String buildNumber = snapshot == null ? null : snapshot.getFirstElement("buildNumber").getInnerAsString();
			version = parseVersion(version);
			String fileVersion = SH.replaceAll(version, "SNAPSHOT", timestamp + "-" + buildNumber);
			String pomFileName = artifactId + "-" + fileVersion + extension;
			final URL sourceUrl = new URL(mirrorUrl + "/" + SH.replaceAll(groupId, '.', '/') + "/" + artifactId + "/" + version + "/" + pomFileName);
			File localFile = new File(localRepo, groupId + "-" + pomFileName);
			if (!localFile.isFile()) {
				IOH.downloadFile(sourceUrl, localFile);
			}
			return localFile;
		} catch (Exception e) {
			throw new RuntimeException("Error for metadata file: " + IOH.getFullPath(metadataFile), e);
		}
	}

	public void log(Object... string) {
		if (verbose)
			System.out.println(SH.join("", string));

	}

	private String parseVersion(String version) {
		if (version == null)
			throw new RuntimeException("version can not be null");
		if (version.startsWith("[") && version.endsWith(",)")) {
			return SH.strip(version, "[", ",)", true);
		}
		return version;
	}
	public String getMirrorUrl() {
		return mirrorUrl;
	}

	public File getLocalRepo() {
		return localRepo;
	}

	public void setLocalRepo(File localRepo) {
		log("Set local Repo: ", localRepo);
		if (!localRepo.isDirectory() || !localRepo.canWrite())
			throw new RuntimeException("directory must exist and be writeable: " + IOH.getFullPath(localRepo));
		this.localRepo = localRepo;
		localMetaRepo = new File(localRepo, "metadata");
		try {
			IOH.ensureDir(localMetaRepo);
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	public PomInstance getRemotePom(File localPomFile) {
		final String fullPomPath = IOH.getFullPath(localPomFile);
		return this.dirToPom.get(fullPomPath);
	}

}
