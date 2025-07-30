package com.f1.ami.center.triggers;

/**
 * A command trigger is a special type of trigger that gets called when a subscriber "command" is executed. This is usually an action that should to invoke some "change" to the
 * system, EX: close a ticket, delete a record, etc. (As opposed to an {@link AmiStoredProcedure}, which should not mutate data, but instead run and return query results)
 * <P>
 * AmiCommandTriggers are called on ({@link #onCommand(AmiCommandRequest)}) in the order they are declared. Once a trigger doesn't return null, the chain is truncated and the
 * return value is sent to the user.
 */
public interface AmiCommandTrigger {

	/**
	 * Called once per command invoked by a subscriber
	 * 
	 * @param request
	 *            represents the various attributes of the caller
	 * @return null if this Trigger cannot handle the type of request, or a response if it can. Note, this is usually determined by evaluating the
	 *         {@link AmiCommandRequest#getCommandDefinitionId()}.
	 */
	AmiCommandResponse onCommand(AmiCommandRequest request);
}
