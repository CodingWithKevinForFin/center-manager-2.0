package com.f1.utils.fix;

import com.f1.base.ToStringable;

/**
 * represents all known {@link FixTag}s. Fix tags can be looked up by fix identifier (number).
 */
public interface FixDictionary extends ToStringable {

	/**
	 * returns the {@link FixTag} associated with the supplied id.
	 * 
	 * @param id
	 *            the id of the fix tag.
	 * @return fix tag associated with id, or null if id is not associated with a {@link FixTag}
	 */
	FixTag getFixTag(int id);

	/**
	 * adds a {@link FixTag} to this dictionary.
	 * 
	 * @param tag
	 *            the {@link FixTag} to add.
	 * @throws RuntimeException
	 *             if a fixtag with the same id has already been put into this dictionary.
	 * @see FixTag#getTag()
	 */
	void putFixTag(FixTag tag);

}
