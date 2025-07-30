package com.vortex.eye.cloud;

import java.net.UnknownHostException;
import java.util.logging.Logger;

import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.vortexcommon.msg.eye.VortexEyeCloudInterface;

public class CloudRunnable implements Runnable {
	private static final Logger log = LH.get(CloudRunnable.class);

	public static final byte ACTION_GET_MACHINES_IN_CLOUD = 1;

	private VortexEyeCloudInterface cloudInterface;
	private CloudAdapter cloudAdapter;
	private Object result;

	private byte action;

	private boolean ok;

	private String message;

	public CloudRunnable(CloudAdapter cloudAdapter, VortexEyeCloudInterface cloudInterface, byte action) {
		this.cloudInterface = cloudInterface;
		this.cloudAdapter = cloudAdapter;
		this.action = action;
	}
	@Override
	public void run() {
		try {
			switch (action) {
				case ACTION_GET_MACHINES_IN_CLOUD:
					result = this.cloudAdapter.getMachinesInCloud(this.cloudInterface);
					break;
				default:
					throw new RuntimeException("Unknown action: " + action);
			}
			this.ok = true;
		} catch (UnknownHostException e) {
			LH.warning(log, "Error running action: ", action, " on ", OH.getSimpleClassName(cloudAdapter) + " for interface CI-", cloudInterface.getId());
			this.message = "Unknown host: " + e.getMessage();
		} catch (Exception e) {
			LH.warning(log, "Error running action: ", action, " on ", OH.getSimpleClassName(cloudAdapter) + " for interface CI-", cloudInterface.getId(), e);
			this.message = e.getMessage();
		}
	}

	public boolean getSuccess() {
		return ok;
	}

	public Object getResults() {
		return result;
	}
	public String getMessage() {
		return message;
	}
}
