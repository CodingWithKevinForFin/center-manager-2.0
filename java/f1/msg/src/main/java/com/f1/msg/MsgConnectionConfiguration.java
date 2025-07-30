/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.msg;

import com.f1.base.Factory;

/**
 * A (generally immutable) configuration that drives vendor-specific settings when establishing a connection.
 * <P>
 * Note that late subscription, guaranteed messaging, prioritization and other highly vendor-specific features are not directly featured in this suite of interfaces and should be
 * included as implementation details, ideally as part of classes implementing this interface.
 */

public interface MsgConnectionConfiguration {

	public String getName();

	/**
	 * Provide a lambda for translating logger names.
	 * 
	 * @return
	 */
	public Factory<String, String> getLogNamer();
}
