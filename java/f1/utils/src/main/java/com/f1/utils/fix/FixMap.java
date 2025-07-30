package com.f1.utils.fix;

import com.f1.base.IntIterator;
import com.f1.base.Legible;
import com.f1.base.ToStringable;
import com.f1.utils.OH;

/**
 * Represents a fix messages, or nested fix message(in the case of groups). <BR>
 * For non-grouping tags, such that {@link FixTag#isGroup()} returns false<BR>
 * &nbsp;1. There is no {@link FixGroup} associated, such that #getGroups(int) will throw an exception<BR>
 * &nbsp;2. use of the various get(...) and getAs(...) tags will retrieve the associated value<BR>
 * For grouping tags, such that {@link FixTag#isGroup()} returns true<BR>
 * &nbsp;1. There is an associated {@link FixGroup}, such that {@link #getGroups(int)} will return the associated group<BR>
 * &nbsp;2. use of the various get(...) and getAs(...) tags will return the number of repeating groups.<BR>
 * 
 * 
 * @see FixGroup
 */
public interface FixMap extends ToStringable, Legible {
	/**
	 * gets the string representation for a value associated with a supplied fix tag
	 * 
	 * @param fixTag
	 *            fix tag to get value for. see {@link FixTag#getTag()}
	 * @return the string representation of the value associated with supplied fix tag). never null
	 * @throws RuntimeException
	 *             if this map does not have an entry for supplied key
	 */
	String get(int fixTag);

	String get(FixTag fixTag);

	/**
	 * gets the string representation for a value associated with a supplied fix tag
	 * 
	 * @param fixTag
	 *            fix tag to get value for. see {@link FixTag#getTag()}
	 * @return the string representation of the value associated with supplied fix tag) or supplied defaultValue if no value is associated
	 */
	String get(int fixTag, String defaultValue);

	String get(FixTag fixTag, String defaultValue);

	/**
	 * gets the value associated with a supplied fix tag, cast appropriately
	 * 
	 * @param fixTag
	 *            fix tag to get value for. see {@link FixTag#getTag()}
	 * @return the value associated with supplied fix tag. never null
	 * @throws RuntimeException
	 *             if this map does not have an entry for supplied key
	 * @see OH#cast(Object, Class) for details on casting
	 * 
	 */
	<T> T getAs(int fixTag, Class<T> type);

	<T> T getAs(FixTag fixTag, Class<T> type);

	/**
	 * gets the value associated with a supplied fix tag (cast appropriately), or returns supplied default value
	 * 
	 * @param fixTag
	 *            fix tag to get value for. see {@link FixTag#getTag()}
	 * @return the value associated with supplied fix tag, or supplied defaultValue.
	 * @see OH#cast(Object, Class) for details on casting
	 * 
	 */
	<T> T getAs(int fixTag, Class<T> type, T defaultValue);

	<T> T getAs(FixTag fixTag, Class<T> type, T defaultValue);

	/**
	 * returns the fix group associated with a tag.
	 * 
	 * @param fixTag
	 *            the fix tag of the group to return.
	 * @return fix group associated with supplied fixTag. (never null)
	 * @throws RuntimeException
	 *             if tag is not associated with a group
	 */
	FixGroup getGroups(int fixTag);

	FixGroup getGroups(FixTag fixTag);

	/**
	 * returns the fix group associated with a tag, or null if not a group.
	 * 
	 * @param fixTag
	 *            the fix tag of the group to return.
	 * @return fix group associated with supplied fixTag. (or null)
	 */
	FixGroup getGroupsNoThrow(int fixTag);

	FixGroup getGroupsNoThrow(FixTag fixTag);

	/**
	 * the particular group entry. Equivilent to getGroups(fixtag).get(offset)
	 * 
	 * @param fixTag
	 *            the fix tag of the group to return.
	 * @param offset
	 *            zero based offset. see {@link FixGroup#get(int)}
	 * 
	 * @return fix map associated with supplied fixtag and offset.
	 * @throws RuntimeException
	 *             if the tag is not associated with a group, of the offset is out of bounds
	 */
	FixMap getGroupAt(int fixTag, int offset);

	FixMap getGroupAt(FixTag fixTag, int offset);

	/**
	 * the number of repeating groups for a particular fix tag.
	 * 
	 * @param fixTag
	 *            the fix tag of the group to return.
	 * @return the number of groups
	 * @throws RuntimeException
	 *             if the tag is not associated with a group
	 */
	int getGroupsCount(int fixTag);

	int getGroupsCount(FixTag fixTag);

	/**
	 * @return the keys declared for this fixmap. (in no order)
	 */
	IntIterator getKeys();

	/**
	 * 
	 * @param fixTag
	 * @return true iff the fixTag corresponds to a group.
	 */
	boolean isGroup(int fixTag);

	boolean isGroup(FixTag fixTag);

	StringBuilder toLegibleString(StringBuilder sb_, String prefix_);

	StringBuilder toLegibleString(StringBuilder sb_);

	@Override
	String toLegibleString();
}

