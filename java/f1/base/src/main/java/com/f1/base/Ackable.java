/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

/**
 * works in conjunction with the {@link Acker} interface to provide a generic means for message acknowlegement. Messages that need guaranteed delivery should implement this
 * interface.
 */
public interface Ackable {

	/**
	 * returned by {@link #askAckId()} to indicate that an ack id has not been assigned (by calling {@link #putAckId(int)}
	 */
	int NO_ACK_ID = 0;

	/**
	 * @return true = this may <i>possibly</i> be a duplicate<BR>
	 *         false = this is guaranteed to not be a duplicate
	 * 
	 */
	boolean askAckIsPosDup();

	/**
	 * @return the ackid for this ackable, or {@link #NO_ACK_ID} if {@link #putAckId(int)} was never called. will never be negative.
	 */
	int askAckId();

	/**
	 * assign an ack id to this message and optionally mark this as a duplicate. note, the ackId must be positive
	 */
	void putAckId(int ackId, boolean isDup);

	/**
	 * called by the consumer of message, after this message has fully been fully acknowledged (and the registered {@link Acker} may deem this message Acknowledged)
	 * 
	 * @param optionalResponse
	 *            a response that should be passed to the {@link Acker}
	 */
	void ack(Object optionalResponse);

	/**
	 * register an acker with this, or clear out existing acker (by passing in null)
	 * 
	 * @param acker
	 *            acker to assign, or null to unregister existing macker
	 */
	void registerAcker(Acker acker);

	/**
	 * Used to transfer an acker from this ackable to some other ackable.
	 * <P>
	 * For example, lets say we need the following work flow:<BR>
	 * 1. message1 is passed into a system and has a registered acker. <BR>
	 * 2. Processing message1 results in message2 getting generated.<BR>
	 * 3. Processing message2 results in message3 getting generated.<BR>
	 * 4. After Processing message3, only then can we ack message1. <BR>
	 * <P>
	 * In this case:<BR>
	 * 1. During step 1: message1.registerAcker(...) should be called<BR>
	 * 2. During step 2: message1.transferAckerTo(message2) should be called<BR>
	 * 3. During step 3: message2.transferAckerTo(message3) should be called<BR>
	 * 4. During step 4: message3.ack(...) should be called<BR>
	 * 
	 * @param ackable
	 */
	void transferAckerTo(Ackable ackable);
}
