/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container;

import com.f1.base.Action;
import com.f1.base.Message;
import com.f1.base.NestedAction;
import com.f1.base.Transient;
import com.f1.base.VID;

/**
 * represents an action which will have an associated {@link ResultAction}.<BR>
 * requests may be associated with a processor which will be responsible for processing the resulting action. <BR>
 * Additionally, requests can also be associated with a "future" which will be populated with the result at some later time.
 * <P>
 * Note: A request action can be though of as an envelope which contains a payload, see {@link #getAction()}. The payload/inner actin will actually contain the request-specific
 * details
 * <P>
 * Note: Typically, {@link RequestAction}s will be processed by a special type of {@link Processor} called a {@link RequestProcessor}.
 * <P>
 * 
 * see {@link Port#request(Action, ThreadScope)} and it's various method overrides. Also see {@link ResultAction}
 * 
 * @param <A>
 *            the type of payload
 */

@VID("F1.BA.RQ")
public interface RequestMessage<A extends Action> extends Message, NestedAction<A> {

	/**
	 * @return the port that will receive the response
	 */
	@Transient
	public OutputPort<?> getResultPort();
	public void setResultPort(OutputPort<?> port);

	/**
	 * The payload for this request
	 */
	@Override
	public A getAction();
	@Override
	public void setAction(A action);

	/**
	 * @return If set, the future should be populate with the response when it is ready.
	 */
	@Transient
	public ResultActionFuture<?> getFuture();
	public void setFuture(ResultActionFuture<?> resultActionFuture);

	/**
	 * @return an optional object that you can use to correlate a request with tis response. This also useful when (this will not go across the wire)
	 */
	@Transient
	public Object getCorrelationId();
	public void setCorrelationId(Object correlationId);

	/**
	 * @return true if this could be a duplicate request (extra care/checks should be performed by receiver)
	 */
	@Transient
	public boolean getPosDup();
	public void setPosDup(boolean posDup);

}
