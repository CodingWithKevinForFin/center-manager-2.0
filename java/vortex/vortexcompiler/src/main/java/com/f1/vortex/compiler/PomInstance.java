package com.f1.vortex.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.utils.IOH;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.Tuple3;
import com.f1.utils.xml.XmlElement;

public class PomInstance {

	private String groupId;
	private String version;
	private String name;
	private String artifactId;
	private File pomFile;
	private List<PomInstance> children = new ArrayList<PomInstance>();
	private List<PomDependency> dependenciesNonTest = new ArrayList<PomDependency>();
	private List<PomDependency> allDependencies = new ArrayList<PomDependency>();
	final private String directoryPath;
	private PomInstance parent;
	private XmlElement document;
	private File localJarFile;
	private Map<String, String> properties = new HashMap<String, String>();

	public PomInstance(File pomFile, boolean isLocal) {
		this.pomFile = pomFile;
		this.directoryPath = isLocal ? IOH.getFullPath(pomFile.getParentFile()) : null;
	}

	@Override
	public String toString() {
		return "PomInstance [name=" + name + ", version=" + version + ", groupId=" + groupId + ", artifactId=" + artifactId + ", pomFile=" + pomFile + ", localJarFile="
				+ localJarFile + ", properties=" + properties + "]";
	}

	public boolean isLocal() {
		return directoryPath != null;
	}

	public void addChild(PomInstance child) {
		children.add(child);
		child.setParent(this);
	}

	private void setParent(PomInstance parent) {
		if (parent == this)
			throw new RuntimeException("circular reference: " + this);
		this.parent = parent;
	}

	public PomInstance getParent() {
		return parent;
	}

	public List<PomInstance> getChildren() {
		return children;
	}

	public String getGroupId() {
		if (this.groupId == null && this.parent.getGroupId() != null)
			return this.parent.getGroupId();

		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public void addDependency(PomDependency pomDependency) {
		if (!pomDependency.isTest())
			this.dependenciesNonTest.add(pomDependency);
		this.allDependencies.add(pomDependency);
	}

	public List<PomDependency> getDependencies(boolean includeTests) {
		return includeTests ? this.allDependencies : this.dependenciesNonTest;
	}

	public String getDirectoryPath() {
		return directoryPath;
	}

	public File getPomFile() {
		return pomFile;
	}

	public void getAllDependencies(Map<Tuple3<String, String, String>, PomDependency> sink, boolean includeTest) {
		for (PomDependency d : getDependencies(includeTest)) {
			if (!sink.containsKey(d.getKey())) {
				sink.put(d.getKey(), d);
				if (d.getTarget() != null)
					d.getTarget().getAllDependencies(sink, includeTest);
			}
		}
	}
	public Collection<PomDependency> getAllDependencies(boolean includeTest) {
		Map<Tuple3<String, String, String>, PomDependency> sink = new HashMap<Tuple3<String, String, String>, PomDependency>();
		getAllDependencies(sink, includeTest);
		return sink.values();
	}

	public XmlElement getDocument() {
		return this.document;
	}
	public void setDocument(XmlElement document) {
		this.document = document;
	}
	public XmlElement getVersionFromDepencencyManagement(String groupId, String artifactId) {
		XmlElement dm = this.document.getFirstElement("dependencyManagement");
		if (dm == null)
			return null;
		XmlElement d = dm.getFirstElement("dependencies");
		if (d == null)
			return null;
		for (XmlElement dep : d.getElements("dependency")) {
			if (groupId.equals(dep.getFirstElement("groupId").getInnerAsString()) && artifactId.equals(dep.getFirstElement("artifactId").getInnerAsString()))
				return dep;
		}
		return null;
	}
	public void setJarFile(File localJarFile) {
		this.setLocalJarFile(localJarFile);
	}
	public File getLocalJarFile() {
		return localJarFile;
	}
	public void setLocalJarFile(File localJarFile) {
		this.localJarFile = localJarFile;
	}
	public String getVersion() {
		if (version == null)
			return getParent().getVersion();
		return version;
	}
	public void setVersion(String version) {
		if (version == null)
			throw new RuntimeException("version is null");
		this.version = version;
	}
	public void addProperty(String name, String value) {
		properties.put(name, value.trim());
	}
	public String applyProperties(String text) {
		if (text.startsWith("${") && text.endsWith("}")) {//TODO: handle embedded vars: ...${...}...${...}...
			String name = (text.substring(2, text.length() - 1));
			if ("project.version".equals(name)) {
				return getParent().getVersion();
			}
			if ("project.parent.version".equals(name)) {
				return getParent().getVersion();
			}
			if ("project.groupId".equals(name)) {
				return getParent().getGroupId();
			}
			if ("project.parent.groupId".equals(name)) {
				return getParent().getGroupId();
			}
			String r = getProperty(name);
			if (r == null)
				return text;
			return applyProperties(r);
		} else if (text.contains("${")) {
			StringBuilder sb = new StringBuilder();
			int s = 0;
			for (;;) {
				int e = text.indexOf("${", s);
				if (e == -1) {
					sb.append(text, s, text.length());
					break;
				}
				int e2 = text.indexOf("}", e + 2);
				sb.append(text, s, e);
				sb.append(applyProperties(text.substring(e, e2 + 1)));
				s = e2 + 1;
			}
			return sb.toString();
		}
		return text;
	}

	public String getProperty(String key) {
		final String r = properties.get(key);
		if (r != null)
			return r;
		else if (parent == null)
			return null;
		else
			return parent.getProperty(key);
	}

	public Tuple2<String, String> getGroupAndArtifactId() {
		Tuple2<String, String> groupAndArtifact = new Tuple2<String, String>(getGroupId(), getArtifactId());
		return groupAndArtifact;
	}

}
