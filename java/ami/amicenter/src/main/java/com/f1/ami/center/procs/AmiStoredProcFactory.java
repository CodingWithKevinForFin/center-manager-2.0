package com.f1.ami.center.procs;

import com.f1.ami.amicommon.AmiFactoryPlugin;

public interface AmiStoredProcFactory extends AmiFactoryPlugin {

	/**
	 * @return a new, uninitialized AMI stored procedure instance.
	 */
	public AmiStoredProc newStoredProc();
}
