package com.f1.utils.fix;

import com.f1.base.ToStringable;

/**
 * represents a repeating fix group, which is essentially an ordered list of
 * nested {@link FixMap}s.
 */
public interface FixGroup extends Iterable<FixMap>, ToStringable {
	/**
	 * returns a fix group at the zero-based offset.
	 * 
	 * @param offset
	 *            zero based offset.
	 * @return fix group at supplied offset. never null
	 * @throws RuntimeException
	 *             if offset is beyond size.
	 */
	FixMap get(int offset);

	/**
	 * @return the number of repeating fix groups.
	 */
	int size();
}
