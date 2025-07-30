package com.f1.vortex.compiler;

import com.f1.utils.OH;
import com.f1.utils.structs.Tuple3;

public class PomDependency {

	final private String groupId;
	final private String artifactId;
	final private String version;
	private PomInstance target;
	final private Tuple3<String, String, String> key;
	final private String type;
	final private String classifier;
	private boolean isOptional;
	private String scope;

	public PomDependency(String depGroupId, String depArtifactId, String depVersion, String type, String classifier, boolean isOptional, String scope) {
		this.groupId = depGroupId;
		this.artifactId = depArtifactId;
		this.version = depVersion;
		this.type = type;
		this.classifier = classifier;
		this.key = new Tuple3<String, String, String>(groupId, artifactId, version);
		this.isOptional = isOptional;
		this.scope = scope;
	}

	public PomInstance getTarget() {
		return target;
	}

	public void setTarget(PomInstance target) {
		if (this.target != null)
			throw new RuntimeException("can not override");
		this.target = target;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getVersion() {
		return version;
	}
	public String getClassifier() {
		return classifier;
	}

	public Tuple3<String, String, String> getKey() {
		return key;
	}

	@Override
	public String toString() {
		return "PomDependency [groupId=" + groupId + ", artifactId=" + artifactId + ", version=" + version + ", type=" + type + ", classifiers=" + classifier + ", optional="
				+ this.isOptional + "]";
	}

	public boolean getIsOptional() {
		return isOptional;
	}
	public String getType() {
		return this.type;
	}

	public boolean isTest() {
		return OH.eq(scope, "test");
	}

}
