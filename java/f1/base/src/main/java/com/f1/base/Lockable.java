package com.f1.base;

/**
 * 
 * Generally for classes that follow the "builder" pattern. This is especially useful when you want a class to "become" immutable: Ex: One thread creates and mutates an instance,
 * and then it becomes locked meaning that it can be safely passed to other threads without worry of mutation
 * 
 * @see LockedException
 */
public interface Lockable {

	/**
	 * Force the class to be locked. Classes that are already locked should silently return uneffected.
	 */
	public void lock();

	/**
	 * @return true if locked, generally after {@link #lock()} is called. False indicates the instance can be safely mutated.
	 */
	public boolean isLocked();
}
