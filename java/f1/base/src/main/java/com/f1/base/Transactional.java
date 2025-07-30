package com.f1.base;

/**
 * A transactional object that has the concept of multiple mutations and a final (typically blocking) commit to replicate changes.
 * 
 */
public interface Transactional {

	/**
	 * When called pending events are committed. It is expected that this call may be a <i>blocking</i> call, hence expensive in terms of time to execute. After the call completes,
	 * you should assume that the underlying transactional queue has been safely committed and cleared.
	 * 
	 * @return false indicates that nothing was committed because there were no events to commit
	 */
	public boolean commitTransaction();

}
