package com.f1.utils.fix;

import com.f1.base.ToStringable;

/**
 * identifies a particular fix tag, and identifies it's grouping characteristics
 * (if it is a grouping tag)
 */
public interface FixTag extends ToStringable {

	/**
	 * @return the id of this tag. Ex. 'MsgType' is 35
	 */
	int getTag();

	/**
	 * returns true if this starts a group .For example a tag representing 78
	 * (noAllocs) should return true.
	 * 
	 * @return if this tag idicates that a group is going to follow
	 */
	boolean isGroup();

	/**
	 * the id of the first tag of each group.
	 * 
	 * @return valid tag id
	 */
	int getStartTag();

	/**
	 * the id of the last tag of each group.
	 * 
	 * @return valid tag id
	 */
	boolean getIsInGroup(int fix);

	/**
	 * @return a user legible name. For example 'MsgType'
	 */
	String getName();
}
