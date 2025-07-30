/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container;

import com.f1.base.Action;
import com.f1.base.Message;
import com.f1.base.NestedAction;
import com.f1.base.Transient;
import com.f1.base.VID;
import com.f1.container.exceptions.ContainerNestedException;
import com.f1.utils.OH;

@VID("F1.BA.RS")
/**
 * represents an action which will have an associated {@link RequestAction}.<BR>
 * if the processing of a request results in an unhandled exception, the f1 framework will create a new ResultAction and bind the exception to it, via {@link #setError(Throwable)}.
 * <BR>
 * Additionally, if the request is associated with a "future" the f1 framework will assign the result to the future for processing by the requester.
 * <P>
 * Note: A result action can be though of as an envelope which contains a payload, see {@link #getAction()}. The payload/inner actin will actually contain the result-specific
 * details
 * <P>
 * Note: Typically, {@link RequestAction}s will be processed by a special type of {@link Processor} called a {@link RequestProcessor} which will fire off a ResultAction to the
 * RequestAction's {@link RequestAction#getResultProcessor()} processor.
 * <P>
 * <B>Non-Final / Final Responses</B> - This paradigm allows for 'streaming' of responses for a particular request. Effectively, many non-final responses may be sent back in
 * succession for a single request, all of those should have {@link #setIsFinalResult(boolean)} to false. The very last result should indicate that it is the final result
 * (indicating that no more responses will follow) by setting {@link #setIsFinalResult(boolean)} to true. For example:
 * 
 * <PRE>
 * VALID SCENARIO: non-final , non-final, non-final, final
 *  -- or --
 * VALID SCENARIO: final
 *  -- or --
 * INVALID SCENARIO: non-final,final,non-final
 *  -- or --
 * INVALID SCENARIO: non-final
 * </PRE>
 * <P>
 * see {@link Port#request(Action, ThreadScope)} and it's various method overrides. Also see {@link RequestAction}
 * 
 * @param <A>
 *            the type of payload.
 */
public abstract class ResultMessage<A extends Action> implements Message, NestedAction<A> {

	/**
	 * set a {@link Throwable} that occurred during the processing of the associated {@link ResultAction}. Note that populating the error and Action should be mutually exclusive
	 * 
	 * @param t
	 *            the {@link Throwable} that occurred.
	 */
	abstract public void setError(Throwable t);

	/**
	 * see {@link #setError(Throwable)}
	 * 
	 * @return may be null
	 */
	abstract public Throwable getError();

	/**
	 * returns the payload of the result.Note that an see {@link #getActionNoThrowable()} for a version of this method that will return null if an error occured
	 * 
	 * @return the payload of the result. should never be null.
	 * @throws ContainerNestedException
	 *             if an error was set on this result.
	 */
	public A getAction() throws ContainerNestedException {
		Throwable error = getError();
		if (error != null)
			throw new ContainerNestedException(error);
		return getActionNoThrowable();
	}

	/**
	 * set the payload for this result. Note that this should be exclusive with setting the error via {@link #setError(Throwable)}
	 * 
	 * @param action
	 */
	public void setAction(A action) {
		setActionNoThrowable(action);
	}

	/**
	 * @return the payload or null if none set (perhaps because of an error)
	 */
	abstract public A getActionNoThrowable();

	/**
	 * see {@link #setAction(Action)}
	 */
	abstract public void setActionNoThrowable(A action);

	/**
	 * @return the request action associated with this response.
	 */
	@Transient
	abstract public RequestMessage<?> getRequestMessage();

	/**
	 * @param action
	 *            the request action associated with this response.
	 */
	abstract public void setRequestMessage(RequestMessage<?> action);

	/**
	 * @return iff this is a final result. see class level comments for details on what a final / non-final response is
	 */
	abstract public boolean getIsIntermediateResult();

	/**
	 * 
	 * @param finalResult
	 *            indicated whether this is a final result or not. See class level comments for details on what a final / non-final response is.
	 */
	abstract public void setIsIntermediateResult(boolean finalResult);

	public ResultMessage clone() {
		try {
			return (ResultMessage) super.clone();
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
	}
}
