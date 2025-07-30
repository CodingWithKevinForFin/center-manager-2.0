package com.vortex.eye.itinerary;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.f1.base.Getter;
import com.f1.base.Message;
import com.f1.base.ValuedParam;
import com.f1.container.ResultMessage;
import com.f1.povo.standard.RunnableRequestMessage;
import com.f1.povo.standard.RunnableResponseMessage;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.VH;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.VortexEyeCloudInterface;
import com.f1.vortexcommon.msg.eye.VortextEyeCloudMachineInfo;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeCIMachineOPRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeCIMachineOPResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeInstallAgentRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunShellCommandRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunShellCommandResponse;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.VortexEyeUtils;
import com.vortex.eye.cloud.CloudAdapter;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;

public class VortexEyeCIMachineOPItinerary extends AbstractVortexEyeItinerary<VortexEyeCIMachineOPRequest> {
	private static final Logger log = LH.get(VortexEyeCIMachineOPItinerary.class);

	private static final byte STEP_NONE = 0;
	private static final byte STEP1_PREPARE_BOX = 1;
	private static final byte STEP1p5_PREPARE_BOX = 8;
	private static final byte STEP2_INSTALL_AGENT = 2;
	private static final byte STEP_START = 3;
	private static final byte STEP_STOP = 4;
	private static final byte STEP_DUP = 5;
	private static final byte STEP_TERMINATE = 6;
	private static final byte STEP_START_AGENT = 7;

	private static final String STATUS_RUNNING = "running";
	private static final String STATUS_STOPPED = "stopped";

	protected String DIR_PKG = "/opt/packages";
	protected String DIR_DATA = "/var/data";
	protected String DIR_SSH_KEY = "~/.ssh";
	protected String USER = "ec2-user";
	protected String DOMAIN = "";
	protected static final int SSH_PORT = 22;
	protected static final int SSH_PORT_FROM = 22;

	private static final int TIMEOUT = 60 * 1000;

	private byte step = STEP_NONE;

	private VortexEyeCIMachineOPResponse r;

	private VortextEyeCloudMachineInfo mi;

	@Override
	public byte startJourney(VortexEyeItineraryWorker worker) {
		//init from props
		PropertyController props = getTools().getSubPropertyController("eye.ci.deployment.");
		DIR_PKG = props.getOptional("dir.package", "/opt/packages");
		DIR_DATA = props.getOptional("dir.data", "/var/data");
		DIR_SSH_KEY = props.getOptional("dir.ssh_key", "~/.ssh");
		USER = props.getOptional("user", "ec2-user");
		DOMAIN = props.getOptional("domain", "");
		scriptFileName = props.getOptional("script");

		step = STEP_NONE;
		this.r = getState().nw(VortexEyeCIMachineOPResponse.class);
		final VortexEyeCIMachineOPRequest req = this.getInitialRequest().getAction();
		mi = getState().getCloudMachineInfo(req.getId());

		updateMI(worker, mi, req.getOp(), "Pending");

		if (mi != null) {
			final VortexEyeCloudInterface ci = getState().getCloudInterface(mi.getCIId());
			if (ci == null) {
				r.setMessage("Couldn't find Cloud Interface for this machine");
				return STATUS_COMPLETE;
			}

			//run the command
			LH.info(log, "Running command on machine id ", mi.getId(), " with op ", req.getOp());

			//validate state vs. command
			switch (req.getOp()) {
				case VortexEyeCIMachineOPRequest.OP_DEPLOY:
				case VortexEyeCIMachineOPRequest.OP_STOP:
				case VortexEyeCIMachineOPRequest.OP_START_AGENT:
					if (!SH.equalsIgnoreCase(STATUS_RUNNING, mi.getStatus())) {
						String msg = "Machine is not running";
						r.setOk(true);
						updateMI(worker, mi, req.getOp(), msg);
						return STATUS_COMPLETE;
					}
					break;

				case VortexEyeCIMachineOPRequest.OP_START:
					if (!SH.equalsIgnoreCase(STATUS_STOPPED, mi.getStatus())) {
						String msg = "Machine is not in a startable state";
						r.setOk(true);
						updateMI(worker, mi, req.getOp(), msg);
						return STATUS_COMPLETE;
					}
					break;
			}

			if (VortexEyeCIMachineOPRequest.OP_DEPLOY == req.getOp()) {
				String[] cmds = new String[] { "sudo sed -i 's/^Defaults *requiretty/#Defaults requiretty/g' /etc/sudoers" };

				if (!runShellCommand(worker, req, cmds, null, "Preparing amazon machine for agent install", STEP1p5_PREPARE_BOX, true))
					return STATUS_COMPLETE;
			} else if (VortexEyeCIMachineOPRequest.OP_START_AGENT == req.getOp()) {
				String[] cmds = new String[] { "sudo " + SH.join('/', DIR_PKG, "agent/scripts/restart.sh") };

				if (!runShellCommand(worker, req, cmds, null, "Trying to start agent", STEP_START_AGENT, false))
					return STATUS_COMPLETE;
			} else {

				RunnableRequestMessage m = getState().nw(RunnableRequestMessage.class);
				m.setPartitionId(ci.getDescription());
				m.setRunnable(new Runnable() {

					@Override
					public void run() {
						try {
							CloudAdapter adapter = VortexEyeRunCloudInterfaceActionItinerary.getCloudAdapter(ci.getCloudVendorType());
							if (adapter != null) {
								switch (req.getOp()) {
									case VortexEyeCIMachineOPRequest.OP_START:
										step = STEP_START;
										adapter.startMachine(mi, ci);
										break;
									case VortexEyeCIMachineOPRequest.OP_STOP:
										step = STEP_STOP;
										adapter.stopMachine(mi, ci);
										break;
									case VortexEyeCIMachineOPRequest.OP_DUP:
										step = STEP_DUP;
										adapter.startMoreLikeThis(mi, ci, req.getName(), req.getNumberOfInstances());
										break;
									case VortexEyeCIMachineOPRequest.OP_TERMINATE:
										step = STEP_TERMINATE;
										adapter.terminateMachine(mi, ci);
										break;
								}
							}
						} catch (Exception e) {
							LH.severe(log, "Failed to run a command on machine ", mi.getId(), e);
							throw new IllegalStateException("Failed to run a command on machine " + mi.getId() + " error-" + e.getMessage(), e);
						}
					}
				});
				m.setTimeoutMs(TIMEOUT);
				worker.sendRunnable(this, m);
			}
			return STATUS_ACTIVE;

		} else {
			r.setMessage("Couldn't find the machine for id " + req.getId());
			return STATUS_COMPLETE;
		}
	}

	private boolean runShellCommand(VortexEyeItineraryWorker worker, final VortexEyeCIMachineOPRequest req, String[] cmds, List<byte[]> stdins, String actionDescription,
			byte step, boolean useDumbTTY) {
		try {
			//load private key from ~/.ssh/keyname.pem
			VortexEyeRunShellCommandRequest r = getState().nw(VortexEyeRunShellCommandRequest.class);
			r.setPublicKeyData(getPrivateKey());
			r.setHostName(mi.getPublicIP() == null ? mi.getPrivateIP() : mi.getPublicIP());
			r.setInvokedBy(req.getInvokedBy());
			r.setUsername(USER);
			r.setTimeoutMs(1000 * 60); //60 sec
			r.setStdins(stdins);

			updateMI(worker, mi, req.getOp(), actionDescription);

			r.setCommands(CH.l(cmds));
			r.setUseTTY(useDumbTTY);
			r.setPort(SSH_PORT_FROM);

			this.step = step;
			worker.startItinerary(this, new VortexEyeRunShellCommandItinerary(), r);
		} catch (Exception e) {
			LH.info(log, "Failed - ", actionDescription, e);
			r.setMessage("Failed - " + actionDescription + " - " + e.getMessage());
			return false;
		}

		return true;
	}

	private static final Getter<byte[], String> b2strGetter = new Getter<byte[], String>() {
		@Override
		public String get(byte[] key) {
			return new String(key);
		}
	};

	private String scriptFileName;

	@Override
	public byte onResponse(ResultMessage<?> result, VortexEyeItineraryWorker worker) {
		final VortexEyeCIMachineOPRequest req = this.getInitialRequest().getAction();

		switch (step) {
			case STEP1p5_PREPARE_BOX:
				String[] cmds;
				File scriptFile = new File(scriptFileName);
				ArrayList<byte[]> stdins = null;

				if (SH.isnt(scriptFileName) || !scriptFile.exists()) {
					cmds = new String[] { //blah 
					"sudo mkdir -p " + DIR_DATA, //data dir
							"sudo ln -s " + DIR_DATA + " /", //link
							"sudo mkdir -p " + DIR_PKG, //where all packages go
							"sudo ln -s " + DIR_PKG + " /", //link
							"sudo cp /etc/ssh/sshd_config /etc/ssh/sshd_config_backup_`date +%Y%m%d`", //back up sshd_config
							//							"sudo sed 's/^#*Port.*$/Port " + SSH_PORT + "/g' /etc/ssh/sshd_config_backup_`date +%Y%m%d` | sudo tee /etc/ssh/sshd_config > /dev/null", //new port 33322
							"sudo cp /etc/hosts /etc/hosts_backup_`date +%Y%m%d`", //back up sshd_config
							"sudo sed 's/localhost.localdomain$/" + mi.getName() + "/g' /etc/hosts_backup_`date +%Y%m%d` | sudo tee /etc/hosts > /dev/null", //new name
							"sudo cp /etc/sysconfig/network /etc/sysconfig/network_backup_`date +%Y%m%d`", //back up network
							"sudo sed 's/^HOSTNAME.*localhost.localdomain$/HOSTNAME=" + mi.getName()
									+ "/g' /etc/sysconfig/network_backup_`date +%Y%m%d` | sudo tee /etc/sysconfig/network > /dev/null", //new name
							"sudo hostname " + mi.getName(),
					//							"sudo /etc/init.d/sshd restart" //restart ssh...make sure that this is last since we are changing the port
					};
				} else {
					try {

						cmds = new String[] { //blah 
						"sudo tee /tmp/prepare.sh", //copy script 
								"sudo chmod a+x /tmp/prepare.sh", //chmod
								"sudo /tmp/prepare.sh " + mi.getName() + " " + DOMAIN + " " + DIR_DATA + " " + DIR_PKG + " >> /tmp/prepare.log 2>&1"//run script 
						};

						stdins = new ArrayList<byte[]>();

						//script for the first command
						stdins.add(IOH.readData(scriptFile));

						//add null stdins for the rest of commands
						for (int i = 1; i < cmds.length; i++)
							stdins.add(null);

					} catch (IOException e) {
						updateMI(worker, mi, req.getOp(), "Failed to load cmd_file " + scriptFileName + "; reason - " + e.getMessage());
						return STATUS_COMPLETE;
					}
				}

				if (!runShellCommand(worker, req, cmds, stdins, "Preparing amazon machine for agent install", STEP1_PREPARE_BOX, false))
					return STATUS_COMPLETE;

				return STATUS_ACTIVE;
			case STEP1_PREPARE_BOX:
				VortexEyeRunShellCommandResponse re = (VortexEyeRunShellCommandResponse) result.getActionNoThrowable();
				if (re != null) {
					List<String> stderrs = CH.l(re.getStderrs(), b2strGetter);
					LH.info(log, "stderrs-", stderrs);
					LH.info(log, "stdoutss-", CH.l(re.getStdouts(), b2strGetter));

					boolean allFailed = true;
					for (int ec : re.getExitCodes())
						if (ec == 0) {
							allFailed = false;
							break;
						}

					if (allFailed) {
						String msg = "Failed to prepare server - " + SH.join(';', stderrs);
						updateMI(worker, mi, req.getOp(), msg);
						r.setMessage(msg);
						r.setOk(false);
						return STATUS_COMPLETE;
					}

				}

				if (error(result, "Failed to prepare the machine"))
					return STATUS_COMPLETE;
				else

					//now install agent
					try {
						updateMI(worker, mi, req.getOp(), "Installing Agent");

						VortexEyeInstallAgentRequest instR = getState().nw(VortexEyeInstallAgentRequest.class);
						instR.setTargetPath(DIR_PKG + "/agent");
						instR.setUsername(USER);

						String interfaceName = null;
						for (String iname : getState().getAgentInterfaces().keySet()) {
							interfaceName = iname;
							if (getState().getAgentInterfaces().get(iname).isSecure)
								break;
						}

						instR.setAgentInterface(interfaceName);
						String agentVersion = CH.first(getState().getAgentVersions());
						if (SH.isnt(agentVersion)) {
							r.setMessage("No available agent versions found on the server...please make sure that eye is configured properly");
							r.setOk(false);
							LH.info(log, "No available agent versions found on the server...please make sure that eye is configured properly");
						}
						instR.setAgentVersion(agentVersion);
						instR.setPublicKeyData(getPrivateKey());
						instR.setDeployUid(req.getInvokedBy());
						instR.setHostName(mi.getPublicIP());
						instR.setPort(SSH_PORT);
						instR.setInvokedBy(req.getInvokedBy());

						step = STEP2_INSTALL_AGENT;
						worker.startItinerary(this, new VortexEyeInstallAgentItinerary(), instR);
					} catch (Exception e) {
						r.setMessage("Failed to install agent - " + e.getMessage());
						LH.info(log, "Failed to install agent", e);
						return STATUS_COMPLETE;
					}

				return STATUS_ACTIVE;
			case STEP2_INSTALL_AGENT:
				if (error(result, "Failed to install agent"))
					return STATUS_COMPLETE;

				updateMI(worker, mi, req.getOp(), "Agent is intalled");
				break;

			case STEP_START:
				String txt = SH.join(',', "Failed to send start command on machine ", req.getId());
				if (error(result, txt))
					updateMI(worker, mi, req.getOp(), txt);
				else
					updateMI(worker, mi, req.getOp(), "Start command requested successfully");

				return STATUS_COMPLETE;

			case STEP_STOP:
				txt = SH.join(',', "Failed to send stop command on machine ", req.getId());
				if (error(result, txt))
					updateMI(worker, mi, req.getOp(), txt);
				else
					updateMI(worker, mi, req.getOp(), "Stop command requested successfully");
				return STATUS_COMPLETE;

			case STEP_DUP:
				txt = SH.join(',', "Failed to request more instances for machine ", req.getId());
				if (error(result, txt))
					updateMI(worker, mi, req.getOp(), txt);
				else
					updateMI(worker, mi, req.getOp(), "Requested more instances successfully");
				return STATUS_COMPLETE;

			case STEP_TERMINATE:
				txt = SH.join(',', "Failed to send terminate command for machine ", req.getId());
				if (error(result, txt))
					updateMI(worker, mi, req.getOp(), txt);
				else
					updateMI(worker, mi, req.getOp(), "Terminate command requested successfully");
				return STATUS_COMPLETE;
			case STEP_START_AGENT:
				txt = SH.join(',', "Failed to start the agent", req.getId());
				if (error(result, txt))
					updateMI(worker, mi, req.getOp(), txt);
				else
					updateMI(worker, mi, req.getOp(), "Started agent command requested successfully");
				return STATUS_COMPLETE;
		}

		error(result, SH.join(',', "Failed to run the command on machine ", req.getId()));

		return STATUS_COMPLETE;
	}
	private boolean error(ResultMessage<?> result, String errorMsg) {
		if (result.getActionNoThrowable() instanceof RunnableResponseMessage) {
			RunnableResponseMessage rm = (RunnableResponseMessage) result.getActionNoThrowable();
			if (rm.getResultCode() != RunnableResponseMessage.RESULT_CODE_COMPLETE) {
				LH.info(log, errorMsg, result.getError());
				r.setMessage(SH.join(',', errorMsg, rm.getText()));
				r.setOk(false);
				return true;
			} else {
				r.setOk(true);
				return false;
			}
		} else {
			if (result.getError() != null) {
				r.setOk(false);
				r.setMessage(result.getError().getMessage());
				return true;
			}
			r.setOk(true);
			return false;
		}
	}

	@Override
	public Message endJourney(VortexEyeItineraryWorker worker) {
		if (!r.getOk())
			updateMI(worker, mi, mi.getLastOP(), "Failed to prepare/install agent - " + r.getMessage());
		return r;
	}
	@Override
	protected void populateAuditEvent(VortexEyeCIMachineOPRequest action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink) {
		sink.setEventType(VortexEyeClientEvent.TYPE_CI_COMMAND);
		sink.getParams().put("Action",
				action.getOp() == VortexEyeCIMachineOPRequest.OP_START ? "start" : action.getOp() == VortexEyeCIMachineOPRequest.OP_STOP ? "stop" : "Launch New");
		VortextEyeCloudMachineInfo mi = getState().getCloudMachineInfo(action.getId());

		if (mi != null) {
			for (ValuedParam<VortextEyeCloudMachineInfo> p : VH.getValuedParams(mi))
				sink.getParams().put(p.getName(), SH.toString(p.getValue(mi)));
		}

		if (action.getOp() == VortexEyeCIMachineOPRequest.OP_DUP) {
			sink.getParams().put("Name", action.getName());
			sink.getParams().put("Number of instances", SH.toString(action.getNumberOfInstances()));
		}
	}

	protected byte[] getPrivateKey() throws Exception {
		File keyFile = IOH.joinPaths(DIR_SSH_KEY, mi.getKeyName() + ".pem");
		//		keyFile = IOH.joinPaths("c:\\temp\\ssh", mi.getKeyName() + ".pem");
		if (!keyFile.exists())
			throw new Exception("Failed to find the private key @ " + keyFile.getAbsolutePath());

		return VortexEyeUtils.encrypt(IOH.readData(keyFile));
	}

	protected void updateMI(VortexEyeItineraryWorker worker, VortextEyeCloudMachineInfo mi, short op, String status) {
		LH.info(log, "updating machine instance: ", mi.getInstanceId(), " name:", mi.getName(), " op:", op, " status:", status);

		VortexEyeChangesMessageBuilder cmb = getState().getChangesMessageBuilder();

		VortextEyeCloudMachineInfo nmi = (VortextEyeCloudMachineInfo) mi.clone();
		nmi.setLastOP(op);
		nmi.setLastOPStatus(status);

		cmb.writeUpdate(mi, nmi);

		//send updates to the client
		worker.sendToClients(this, cmb.popToChangesMsg(getState().nextSequenceNumber()));
	}
}
