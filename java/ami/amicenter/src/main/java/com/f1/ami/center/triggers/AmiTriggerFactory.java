package com.f1.ami.center.triggers;

import com.f1.ami.amicommon.AmiFactoryPlugin;

public interface AmiTriggerFactory extends AmiFactoryPlugin {

	/**
	 * @return a new, uninitialized AMI trigger instance
	 */
	public AmiTrigger newTrigger();

}
