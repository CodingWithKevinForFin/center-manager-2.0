package com.f1.persist;

public interface PersistReader {

	/**
	 * @param sink
	 *            the event to be populated.
	 * @return true if an event could be read
	 * @throws Exception
	 */
	void pumpEvent(PersistEvent sink) throws Exception;
}
