package com.f1.base;

/**
 * 
 * An action that contains another action.
 * 
 * @param <A>
 *            the type of action contained
 */
public interface NestedAction<A extends Action> extends Action {

	/**
	 * get the contained action
	 */
	public A getAction();

	/**
	 * set the contained action
	 */
	public void setAction(A action);

}
