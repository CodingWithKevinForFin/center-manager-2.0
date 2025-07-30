package com.vortex.agent.state;

import java.io.File;

import com.f1.container.impl.BasicState;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.vortexcommon.msg.eye.VortexDeployment;

public class VortexAgentDeploymentWrapper extends BasicState {

	final private VortexDeployment deployment;
	final private String startScript;
	final private String stopScript;
	final private String installScript;
	final private String uninstallScript;
	final private File targetDir;
	final private File manifestFile;
	final private File scriptsDir;
	final private String[] envVars;

	public VortexDeployment getDeployment() {
		return deployment;
	}
	public VortexAgentDeploymentWrapper(VortexDeployment deployment) {
		this.deployment = deployment;
		this.deployment.lock();
		String targetDir = deployment.getTargetDirectory();
		this.targetDir = new File(targetDir);
		this.startScript = getPath(this.targetDir, deployment.getStartScriptFile());
		this.envVars = deployment.getEnvVars() == null ? OH.EMPTY_STRING_ARRAY : SH.splitLines(deployment.getEnvVars());
		this.stopScript = getPath(this.targetDir, deployment.getStopScriptFile());
		this.installScript = getPath(this.targetDir, deployment.getInstallScriptFile());
		this.uninstallScript = getPath(this.targetDir, deployment.getUninstallScriptFile());
		this.manifestFile = new File(targetDir, ".f1deploy.txt");
		this.scriptsDir = new File(targetDir, deployment.getScriptsDirectory());
	}

	private String getPath(File targetDir, String command) {
		if (SH.isnt(command))
			return null;

		return SH.join(File.separatorChar, targetDir.toString(), command);
	}

	public String getStartScript() {
		return startScript;
	}
	public String[] getEnvVars() {
		return envVars;
	}
	public String getStopScript() {
		return stopScript;
	}
	public String getInstallScript() {
		return installScript;
	}
	public String getUninstallScript() {
		return uninstallScript;
	}
	public boolean isSpecialFile(File file) {
		return OH.eq(file, startScript) || OH.eq(file, stopScript) || OH.eq(file, installScript) || OH.eq(file, uninstallScript);
	}
	public File getTargetDir() {
		return targetDir;
	}
	public File getManifestFile() {
		return manifestFile;
	}
	public File getScriptsDir() {
		return scriptsDir;
	}
	public long getId() {
		return deployment.getId();
	}
	public boolean validateFile(String command) {
		if (SH.isnt(command))
			return false;

		File f = new File(SH.beforeFirst(command, ' '));
		return IOH.isFile(f);
	}
}
