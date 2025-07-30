package com.f1.pofo.oms;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

/**
 * 
 * represents a stateful item that can be revised and can be uniquely
 * represented using composite system and id key.
 */
@VID("F1.FX.RV")
public interface Revisioned extends Message {

	/**
	 * @return An Id which uniquely represents this item within a particular
	 *         system.
	 */
	@PID(120)
	public String getId();

	public void setId(String id);

	/**
	 * @return the id of the system which generated this item (each system
	 *         should have a globally unique id)
	 */
	@PID(121)
	public String getSourceSystem();

	public void setSourceSystem(String sourceSystem);

	/**
	 * @return the current revision of this item. Should start with zero, and be
	 *         incremented by one for each interesting revision.
	 */
	@PID(122)
	public int getRevision();

	public void setRevision(int revision);

}
