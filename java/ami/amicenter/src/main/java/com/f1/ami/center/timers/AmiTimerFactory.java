package com.f1.ami.center.timers;

import com.f1.ami.amicommon.AmiFactoryPlugin;

public interface AmiTimerFactory extends AmiFactoryPlugin {

	/**
	 * @return a new, uninitialized AMI timer instance
	 */
	public AmiTimer newTimer();
}
