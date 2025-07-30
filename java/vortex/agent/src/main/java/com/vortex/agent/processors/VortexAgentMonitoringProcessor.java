package com.vortex.agent.processors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.container.OutputPort;
import com.f1.container.PartitionResolver;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexDeployment;
import com.f1.vortexcommon.msg.eye.VortexUpdateDeploymentStatusesFromAgent;
import com.vortex.agent.messages.VortexAgentDeploymentUpdateMessage;
import com.vortex.agent.state.VortexAgentDeploymentState;
import com.vortex.agent.state.VortexAgentDeploymentWrapper;

public class VortexAgentMonitoringProcessor extends BasicProcessor<VortexAgentDeploymentUpdateMessage, VortexAgentDeploymentState> implements
		PartitionResolver<VortexAgentDeploymentUpdateMessage> {

	final static Logger log = Logger.getLogger(VortexAgentMonitoringProcessor.class.getName());

	final public OutputPort<VortexAgentDeploymentUpdateMessage> loopback = newOutputPort(VortexAgentDeploymentUpdateMessage.class);
	final public OutputPort<VortexUpdateDeploymentStatusesFromAgent> toEye = newOutputPort(VortexUpdateDeploymentStatusesFromAgent.class);
	private long monitorFrequency;

	public VortexAgentMonitoringProcessor() {
		super(VortexAgentDeploymentUpdateMessage.class, VortexAgentDeploymentState.class);
		setPartitionResolver(this);
	}

	@Override
	public void init() {
		this.monitorFrequency = getTools().getOptional("deployment.check.ms", 3000);
		super.init();
	}

	@Override
	public void processAction(VortexAgentDeploymentUpdateMessage action, VortexAgentDeploymentState state, ThreadScope threadScope) throws Exception {
		if (CH.isntEmpty(action.getAddedPuidToDiids()))
			for (Entry<String, String> e : action.getAddedPuidToDiids().entrySet())
				state.putF1AppPuidToDiid(e.getKey(), e.getValue());
		if (CH.isntEmpty(action.getRemovedPuids()))
			for (String puid : action.getRemovedPuids())
				state.removeF1AppPuid(puid);
		if (action.getPartitionId() == null)//this was just an f1 app puid / diid update
			return;

		VortexAgentDeploymentUpdateMessage startPollingAction = null;
		if (action.getDeployment() != null) {//this is an update, delete or new
			if (action.getDeployment().getRevision() == VortexAgentEntity.REVISION_DONE) {//delete
				state.clear();
				return;
			} else {
				state.init(new VortexAgentDeploymentWrapper(action.getDeployment()));
				if (state.getPolling() == null) {
					startPollingAction = nw(VortexAgentDeploymentUpdateMessage.class);
					startPollingAction.setPartitionId(action.getPartitionId());
					state.setPolling(startPollingAction);
				}
			}
		} else if (state.getDeploymentWrapper() == null) {//was cleared out
			state.setPolling(null);
			return;
		}
		if (action.getMessage() != null)
			state.setMessage(action.getMessage());

		try {
			onNewDeployment(state, action.getStatusBitsToClear(), action.getStatusBitsToSet());

		} catch (Exception e) {
			LH.log(log, Level.INFO, "Error Monitoring Deployment: " + (state.getDeployment() == null ? null : state.getDeployment().getId()), e);
		}

		if (startPollingAction != null) {
			state.setPolling(startPollingAction);
			action = startPollingAction;
		}
		if (state.getPolling() == action) {
			loopback.sendDelayed(action, threadScope, monitorFrequency, TimeUnit.MILLISECONDS);
		}
	}
	private void onNewDeployment(VortexAgentDeploymentState state, int statusBitsToClear, int statusBitsToSet) {
		int origStatus = state.getStatus();
		String origMessage = state.getMessage();
		String origRunningProcessUid = state.getRunningPuid();
		Integer origRunningPid = state.getRunningPid();
		String origScriptsFound = state.getScriptsFound();
		//Long origBuildResultId = state.getCurrentBuildResultId();
		//String origBuildInvokedBy = state.getCurrentBuildInvokedBy();
		try {
			if (statusBitsToSet != 0 || statusBitsToClear != 0)
				state.setStatus(statusBitsToSet | MH.clearBits(state.getStatus(), statusBitsToClear));
			state.setStatus(MH.clearBits(state.getStatus(), VortexDeployment.MASK_DEPLOY | VortexDeployment.MASK_PROCESS));
			state.setRunningPid(null);
			state.setRunningProcessUid(null);
			state.setMessage(null);
			final File targetDir = state.getDeploymentWrapper().getTargetDir();
			final File manifestFile = state.getDeploymentWrapper().getManifestFile();
			if (IOH.isFile(targetDir)) {
				addStatus(state, VortexDeployment.STATUS_DEPLOY_BAD_ROOT_PATH_, "root directory is existing file: " + IOH.getFullPath(targetDir));
			} else if (!IOH.isDirectory(targetDir)) {
				addStatus(state, VortexDeployment.STATUS_DEPLOY_NOT_FOUND_____, "root directory not found: " + IOH.getFullPath(targetDir));
			} else if (!targetDir.canRead()) {
				addStatus(state, VortexDeployment.STATUS_DEPLOY_BAD_ROOT_PATH_, "root directory is not readable: " + IOH.getFullPath(targetDir));
			} else if (!IOH.isFile(manifestFile)) {
				addStatus(state, VortexDeployment.STATUS_DEPLOY_NOT_FOUND_____, "no manifest: " + IOH.getFullPath(manifestFile));
			} else {
				validateScripts(targetDir, state, state);
				validateManifest(targetDir, manifestFile, state);

				final File startScript = IOH.joinPaths(targetDir, SH.beforeFirst(state.getDeployment().getStartScriptFile(), ' '));
				final File procInfoSinkFile = IOH.joinPaths(state.getDeploymentWrapper().getTargetDir(), ".f1proc.txt");
				validateProcInfo(procInfoSinkFile, state);
			}
		} catch (Exception e) {
			LH.warning(log, "Error preparing depoyment: ", state.getDeployment(), e);
			addStatus(state, VortexDeployment.STATUS_DEPLOY_GENERAL_ERROR_, "exception occured preparing in agent: " + e);
		}
		if (state.getStatus() != origStatus // 
				|| OH.ne(state.getMessage(), origMessage) //
				|| OH.ne(state.getRunningPid(), origRunningPid) //
				|| OH.ne(state.getScriptsFound(), origScriptsFound) //
				|| OH.ne(state.getRunningPuid(), origRunningProcessUid)//
				|| state.needsFullsend()//
		//|| OH.ne(state.getCurrentBuildInvokedBy(), origBuildInvokedBy)//
		) {
			VortexUpdateDeploymentStatusesFromAgent updmsg = nw(VortexUpdateDeploymentStatusesFromAgent.class);
			VortexDeployment nuw = nw(VortexDeployment.class);
			nuw.setId(state.getDeployment().getId());

			if (state.needsFullsend() || state.getStatus() != origStatus)
				nuw.setStatus(state.getStatus());
			if (state.needsFullsend() || OH.ne(state.getMessage(), origMessage))
				nuw.setMessage(state.getMessage());
			if (state.needsFullsend() || OH.ne(state.getRunningPuid(), origRunningProcessUid))
				nuw.setRunningProcessUid(state.getRunningPuid());
			if (state.needsFullsend() || OH.ne(state.getRunningPid(), origRunningPid))
				nuw.setRunningPid(state.getRunningPid());
			if (state.needsFullsend() || OH.ne(state.getScriptsFound(), origScriptsFound))
				nuw.setScriptsFound(state.getScriptsFound());
			if (state.needsFullsend()) {
				VortexDeployment dep = state.getDeployment();
				nuw.setCurrentBuildInvokedBy(dep.getCurrentBuildInvokedBy());
				nuw.setCurrentBuildResultId(dep.getCurrentBuildResultId());
				nuw.setDeployedInstanceId(dep.getDeployedInstanceId());
				state.resetNeedsFullsend();
			}
			//if (OH.ne(state.getCurrentBuildResultId(), origBuildResultId))
			//nuw.setCurrentBuildResultId(origBuildResultId);
			//if (OH.ne(state.getCurrentBuildInvokedBy(), origBuildInvokedBy))
			//nuw.setCurrentBuildInvokedBy(origBuildInvokedBy);

			updmsg.setUpdated(CH.l(nuw));
			toEye.send(updmsg, null);

		}
	}
	private void validateProcInfo(File procInfoSinkFile, VortexAgentDeploymentState state) throws IOException {
		if (IOH.isFile(procInfoSinkFile)) {

			String[] lines = SH.splitLines(IOH.readText(procInfoSinkFile));
			boolean found = false;
			Set<String> shutdown = new HashSet<String>();
			for (int i = lines.length - 1; i >= 0; i--) {
				String[] parts = SH.split('|', lines[i]);
				if (parts.length < 6)
					continue;
				String processUid = parts[3];
				String pid = parts[2];
				long startTime = Long.parseLong(parts[0]);
				if ("DN".equals(parts[1])) {
					shutdown.add(processUid);
				} else if ("UP".equals(parts[1]) && !shutdown.contains(processUid)) {
					String diid = state.getF1AppDiidByPuid(processUid);
					//VortexAgentF1AppState f1app = state.getF1AppByProcessUidNoThrow(processUid);
					if (diid != null) {
						final int pidInt = SH.parseInt(parts[2]);
						state.setRunningPid(pidInt);
						state.setRunningProcessUid(processUid);
						addStatus(state, VortexDeployment.STATUS_PROCESS_PID_FOUND____ | VortexDeployment.STATUS_PROCESS_PUID_MATCH___, null);
						final String expected = "DI-" + SH.toString(OH.noNull(state.getDeployment().getDeployedInstanceId(), 0L));
						if (OH.ne(diid, expected))
							addStatus(state, VortexDeployment.STATUS_PROCESS_BAD_DIID_____, "expecting: " + expected + ", not: " + diid);
						found = true;
						break;
					} else {
						if (isPidRunning(pid, startTime)) {
							addStatus(state, VortexDeployment.STATUS_PROCESS_PID_FOUND____, null);
							state.setRunningPid(Integer.parseInt(parts[2]));
							state.setRunningProcessUid(processUid);
							found = true;
							break;
						}
					}
				}
			}
		} else {//look for pid file in the same dir
			String dir = procInfoSinkFile.getParent();
			File pf = IOH.joinPaths(dir, ".pid");

			LH.finer(log, "Checking for pid file at ", pf.getAbsolutePath());

			if (IOH.isntFile(pf))
				return;

			String pidString = SH.trim(IOH.readText(pf));
			long startTime = pf.lastModified();

			LH.finer(log, "found pid file with pid [", pidString, "] startTime [", startTime, "]");

			if (isPidRunning(pidString, startTime)) {
				addStatus(state, VortexDeployment.STATUS_PROCESS_PID_FOUND____, null);
				state.setRunningPid(SH.parseInt(pidString));
			}
		}

	}

	private boolean isPidRunning(String pid, long startTime) {
		final File procDir = new File("/proc/" + pid);
		return procDir.isDirectory() && Math.abs(procDir.lastModified() - startTime) < 1000 * 60;
	}

	private void validateScripts(File targetDir, VortexAgentDeploymentState state, VortexAgentDeploymentState dep) throws FileNotFoundException {
		if (SH.is(dep.getDeployment().getScriptsDirectory())) {
			File scriptsDir = state.getDeploymentWrapper().getScriptsDir();
			if (!scriptsDir.isDirectory()) {
				addStatus(dep, VortexDeployment.STATUS_DEPLOY_SCRIPTS_BAD___, "scripts dir not found: " + IOH.getFullPath(scriptsDir));
			} else {
				List<String> scripts = new ArrayList<String>();
				for (File file : IOH.listFiles(scriptsDir)) {
					if (file.isFile() && !state.getDeploymentWrapper().isSpecialFile(file))
						scripts.add(file.getName());
				}
				String scriptsText = SH.join(',', scripts);
				state.setScriptsFound(scriptsText);
			}
		}

		VortexAgentDeploymentWrapper dw = state.getDeploymentWrapper();
		if (!dw.validateFile(dw.getStopScript()))
			addStatus(dep, VortexDeployment.STATUS_DEPLOY_SCRIPTS_BAD___, "stop script not found: " + dw.getStopScript());
		if (!dw.validateFile(dw.getStopScript()))
			addStatus(dep, VortexDeployment.STATUS_DEPLOY_SCRIPTS_BAD___, "start script not found: " + dw.getStartScript());
	}
	private void validateManifest(File targetDir, File manifestFile, VortexAgentDeploymentState deployment) throws IOException {
		String[] lines = SH.splitLines(IOH.readText(manifestFile));
		try {
			long checksum1 = SH.parseLong(SH.stripPrefix(lines[lines.length - 1], "#CHKSUM: ", true), 62);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < lines.length - 1; i++)
				sb.append(lines[i]).append(SH.NEWLINE);
			long checksum2 = Math.abs(IOH.checkSumBsdLong(sb.toString().getBytes()));
			if (checksum1 != checksum2) {
				addStatus(deployment, VortexDeployment.STATUS_DEPLOY_MANIFEST_BAD__, "Manifest has bad checksum: " + IOH.getFullPath(manifestFile));
				return;
			}
			SH.clear(sb);
			for (int i = 0; i < lines.length - 1; i++) {
				final String[] parts = SH.split(' ', lines[i]);
				final String fileName = parts[0];
				final long length = SH.parseLong(parts[1]);
				final long checksum = SH.parseLong(parts[2], 62);
				File file = new File(fileName);
				if (!file.exists()) {
					sb.append("File not found: ").append(fileName).append(". ");
				} else if (file.length() != length) {
					sb.append("File size changed: ").append(fileName).append(". ");
				} else if (checksum != Math.abs(IOH.checkSumBsdLong(IOH.readData(file)))) {
					sb.append("File changed: ").append(fileName).append(". ");
				}
			}
			if (sb.length() > 0)
				addStatus(deployment, VortexDeployment.STATUS_DEPLOY_FILE_MISMATCH_, "File mismatch: " + sb.toString());
		} catch (Exception e) {
			addStatus(deployment, VortexDeployment.STATUS_DEPLOY_MANIFEST_BAD__, "Manifest invalid: " + e.getMessage());
		}
	}
	private void addStatus(VortexAgentDeploymentState deployment, int status, String message) {
		deployment.setStatus(deployment.getStatus() | status);
		if (message != null)
			deployment.setMessage(message);
	}

	@Override
	public Object getPartitionId(VortexAgentDeploymentUpdateMessage action) {
		return action.getPartitionId();
	}
}
