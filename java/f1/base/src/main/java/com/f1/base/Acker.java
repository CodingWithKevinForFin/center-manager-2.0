/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

/**
 * works in conjunction with the {@link Ackable} interface to provide a generic means for message acknowlegement. If an acker is registered with an {@link Ackable} instance (see
 * {@link Ackable#registerAcker(Acker)}) then when {@link Ackable#ack(Object)} is called on said ackable, the ackable should intern call {@link #ack(Ackable, Object)} on the
 * acker,passing in itself as the first param, and passing through the optionalResult as the second param.
 * <P>
 * 
 * Typically, when some module is producing messages which need guaranteed delivery the followings steps take place for each message:<BR>
 * 1) A new message which implements the {@link Ackable} interface is created. (Note, {@link Message} already does)<BR>
 * 2) An {@link Acker} is registered with the message (by calling {@link Ackable#registerAcker(Acker)}). Note, that in some cases it may make more sense to have one common
 * {@link Acker} for all messages from a given source, in other cases it may make more sense to create a unique {@link Acker} per message. <BR>
 * 3) The message is dispatched for processing, potentionally on some other thread.
 */
public interface Acker {
	/**
	 * Called by an {@link Ackable} when {@link Ackable#ack(Object)} is called
	 * 
	 * @param ackable
	 *            the ackable with had {@link Ackable#ack(Object)} called on it.
	 * @param optionalResult
	 *            the param passed into {@link Ackable#ack(Object)}
	 */
	public void ack(Ackable ackable, Object optionalResult);
}
