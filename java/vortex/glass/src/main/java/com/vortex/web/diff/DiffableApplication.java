package com.vortex.web.diff;

import java.util.Set;

import com.f1.povo.f1app.F1AppInstance;
import com.f1.povo.f1app.F1AppProperty;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.Property;
import com.f1.utils.SH;
import com.vortex.client.VortexClientF1AppState;
import com.vortex.client.VortexClientF1AppState.AgentWebProperty;

public class DiffableApplication extends AbstractDiffableNode {

	private VortexClientF1AppState app;
	private final String description;

	public DiffableApplication(VortexClientF1AppState app, Set<String> ignoreList) {
		super(DIFF_TYPE_APP, app.getSnapshot().getAppName());
		StringBuilder description = new StringBuilder();
		F1AppInstance ss = app.getSnapshot();
		this.app = app;
		DiffableProperties locProps = new DiffableProperties("Location");
		DiffableProperties props = new DiffableProperties("Properties");
		DiffableProperties f1Props = new DiffableProperties("F1 Properties");
		DiffableProperties deployProps = new DiffableProperties("Deployment Properties");
		DiffableProperties metadata = new DiffableProperties("MetaData");
		description.append("Process Host: ").append(ss.getHostName()).append(SH.NEWLINE);
		for (AgentWebProperty p : app.getProperties().values()) {
			F1AppProperty prop = p.getObject();
			if (prop.getPosition() != 0)
				continue;
			if (ignoreList.contains(prop.getKey()))
				description.append(prop.getKey()).append(": ").append(prop.getValue()).append(SH.NEWLINE);
			else if (prop.getKey().startsWith("f1.deployment."))
				deployProps.addChild(new DiffableProperty(prop.getKey(), prop.getValue(), describeSource(prop)));
			else if (prop.getKey().startsWith("f1."))
				f1Props.addChild(new DiffableProperty(prop.getKey(), prop.getValue(), describeSource(prop)));
			else
				props.addChild(new DiffableProperty(prop.getKey(), prop.getValue(), describeSource(prop)));
			//deployProps.addChild(new DiffableProperty(prop.getKey(), prop.getValue(), describeSource(prop)));
			//else
			//props.addChild(new DiffableProperty(prop.getKey(), prop.getValue(), describeSource(prop)));
		}
		metadata.addChild(new DiffableProperty("Java External Dirs", SH.join(SH.NEWLINE, CH.sort(ss.getJavaExternalDirs())), ""));
		metadata.addChild(new DiffableProperty("Java Home", ss.getJavaHome(), ""));
		metadata.addChild(new DiffableProperty("Java Vendor", ss.getJavaVendor(), ""));
		metadata.addChild(new DiffableProperty("Java Version", ss.getJavaVersion(), ""));
		locProps.addChild(new DiffableProperty("Process Working Directory", ss.getPwd(), ""));
		locProps.addChild(new DiffableProperty("Process Owner", ss.getUserName(), ""));
		metadata.addChild(new DiffableProperty("JVM classpath", SH.join(SH.NEWLINE, CH.sort(ss.getClasspath())), ""));
		metadata.addChild(new DiffableProperty("JVM Boot classpath", SH.join(SH.NEWLINE, CH.sort(ss.getBootClasspath())), ""));
		metadata.addChild(new DiffableProperty("JVM arguments", SH.join(SH.NEWLINE, CH.sort(ss.getJvmArguments())), ""));
		metadata.addChild(new DiffableProperty("Application arguments", SH.join(SH.NEWLINE, CH.sort(ss.getMainClassArguments())), ""));
		metadata.addChild(new DiffableProperty("Main Class", ss.getMainClassName(), ""));
		addChild(locProps);
		addChild(props);
		addChild(deployProps);
		addChild(f1Props);
		addChild(metadata);
		this.description = SH.join(SH.NEWLINE, (Object[]) AH.sort(SH.splitLines(description.toString())));
	}
	private static final String describeSource(F1AppProperty prop) {
		switch (prop.getSourceType()) {
			case Property.TYPE_CODE:
				return "Declared in Source code: " + prop.getSource() + " (at line " + prop.getSourceLineNumber() + ")";
			case Property.TYPE_FILE:
				return "Declared in: " + prop.getSource() + " (at line " + prop.getSourceLineNumber() + ")";
			case Property.TYPE_PREFERENCE:
				return "Declared in Preference";
			case Property.TYPE_RESOURCE:
				return "Declared in: " + prop.getSource() + " (at line " + prop.getSourceLineNumber() + ")";
			case Property.TYPE_SYSTEM_ENV:
				return "Declared in System Environment";
			case Property.TYPE_SYSTEM_PROPERTY:
				return "Declared in System Property";
		}
		return "";
	}

	@Override
	public boolean isEqualToNode(DiffableNode node) {
		return OH.eq(getDiffName(), node.getDiffName());
	}

	@Override
	public String getContents() {
		return description.toString();
	}

	public VortexClientF1AppState getData() {
		return app;
	}

}
