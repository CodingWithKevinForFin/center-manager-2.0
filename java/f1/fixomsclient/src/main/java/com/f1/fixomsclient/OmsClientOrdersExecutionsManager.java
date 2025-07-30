package com.f1.fixomsclient;


/**
 * Manages the state of all orders and executions. It is intended to 'connect' remotely to an oms. Typically, {@link #requestSnapshot()} is called after connecting. Until the
 * snapshot is received all 'deltas' such as changes to orders and executions will be queued. This guarantees that the snapshot delta paradigm will be consistent. <BR>
 * Generally, listeners are added to this manager so that they may receive notifications when orders / executions are added / removed / updated.
 * <P>
 * 
 * Care must be taken if accessed from a thread other that the threads calling into the various {@link OmsClientOrdersExecutions}'s notification methods. Use the
 * {@link #borrowOrdersExecutions()} and {@link #freeOrderExecutions(OmsClientOrdersExecutions)} to see the current state of all orders and executions. Note that a failure to call
 * free will result in an eventual timeout (this should be avoided).
 * 
 */
public interface OmsClientOrdersExecutionsManager {

	/**
	 * add a listener which should receive notifications such as updates / adds / deletes for orders and executions.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	void addGuiManagerListener(OmsClientOrdersExecutionsListener listener);

	/**
	 * remove the listener, such that it will no longer receive updates
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	void removeManagerListener(OmsClientOrdersExecutionsListener listener);

	/**
	 * 'borrow' the current snapshot of orders and executions. You <B>must</B> call {@link #freeOrderExecutions(OmsClientOrdersExecutions)} after you are done accessing. During the
	 * period between the borrow and free no notifications will be sent to listeners. <B> This call will block if any listeners are processing notifications.
	 * 
	 * @return current snapshot.
	 */
	public OmsClientOrdersExecutions borrowOrdersExecutions();

	/**
	 * @param orderExecutions
	 *            the borrowed connection.
	 */
	public void freeOrderExecutions(OmsClientOrdersExecutions orderExecutions);

	/**
	 * 
	 * @return a collection of current listeners
	 */
	public Iterable<OmsClientOrdersExecutionsListener> getListeners();

	/**
	 * force the manager to request from the oms a snapshot of all orders and executions. At a minimum, this should be called during the startup process.
	 */
	public void requestSnapshot();

}
