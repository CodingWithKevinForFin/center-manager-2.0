/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

/**
 * A marker interface that indicates the object can be marshalled/demarshalled and is intended for inter-process-communication. For better performance (and more compact message) be
 * sure to decorate message classes with unique {@link VID} and the parameters with {@link PID}. Note that messages support cloning for fast replication.
 * 
 */
@VID("F1.BA.MS")
public interface Message extends Action, Ideable, Cloneable {

	public Message clone();

}
