package com.f1.base;

/**
 * 
 * Thrown when a mutation is attempted on a locked {@link Lockable} object. Note, this may also be thrown if an unlocked class should be locked (see {@link #assertLocked(Lockable)}
 * for example)
 * 
 */
public class LockedException extends RuntimeException {

	public LockedException() {
		super();
	}

	public LockedException(String message, Throwable cause) {
		super(message, cause);
	}

	public LockedException(String message) {
		super(message);
	}

	public LockedException(Throwable cause) {
		super(cause);
	}

	/**
	 * Helper function that will throw {@link LockedException} if the supplied object is locked
	 */
	public static void assertNotLocked(Lockable lock) {
		if (lock.isLocked())
			throw new LockedException("locked: " + lock);
	}

	/**
	 * Helper function that will throw {@link LockedException} if the supplied object is not locked
	 */
	public static void assertLocked(Lockable lock) {
		if (!lock.isLocked())
			throw new LockedException("not locked: " + lock);
	}
}
