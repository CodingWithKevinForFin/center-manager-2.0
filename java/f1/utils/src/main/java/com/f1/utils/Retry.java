package com.f1.utils;

/**
 * manages the looping involved with retrying. Typically your could will look
 * like:
 * 
 * <PRE>
 * for(Retry r=new BasicRetry().setMaxAttempts(5);r.shouldTryAgain();){
 *   try{
 *     doSomeConnectLogicThatIfSucceedsDoesntThrow();
 *     r.onSucess();
 *   }catch(SomeException e){
 *     r.onError(e);
 *   }
 * }
 * 
 * -- or -- 
 * 
 * for(Retry r=new BasicRetry().setMaxAttempts(5);r.shouldTryAgain();){
 *   doSomeConnectLogic();
 *   if(checkSomeConditionForSuccess())
 *     r.onSucess();
 *   else
 *     r.onError(new RuntimeException("Some message as to why it failed"));
 * }
 * </PRE>
 */
public interface Retry {
	/** no maximum attempts (keep trying for ever unless a timeout is supplied) */
	int NO_MAX_ATTEMPTS = -1;

	/**
	 * no maximum timeout (keep trying for ever unless a max attempts is
	 * supplied)
	 */
	long NO_MAX_TIME_MS = -1;

	/** no sleep time after failures(immediately try again) */
	long NO_SLEEP_TIME_MS = -1;

	/**
	 * The default number of milliseconds to sleep after a failed attempt before
	 * trying again
	 */
	long DEFAULT_SLEEP_TIME_MS = 10 * 1000;

	/** The Default time to wait before giving up */
	long DEFAULT_MAX_TIME_MS = NO_MAX_TIME_MS;

	/** The default number of attempts before giving up */
	int DEFAULT_MAX_ATTEMPTS = 30;

	/**
	 * Indicate that the attempt failed (and increments an in termal count of
	 * number of failures). Please not this may only becalled after a preceeding
	 * {@link #shouldTryAgain()}, and can not be called once
	 * {@link #onSuccess()} has been called. If the maximum number of attempts
	 * has been exceeded, then the exception passed in will be thrown
	 * 
	 * @param <T>
	 *            type of exception that will be thrown
	 * @param exception
	 *            the exception to throw
	 * @throws T
	 *             the supplied exception, if max attempts has been exceeded
	 */
	<T extends Exception> void onError(T exception) throws T;

	/**
	 * Indicate that the attempt succeeded, meaning next time
	 * {@link #shouldTryAgain()} will return false (allowing the outer loop to
	 * break). Please not this may only becalled after a preceeding
	 * {@link #shouldTryAgain()} and will fail if {@link #onSuccess()} has
	 * already been called.
	 */
	void onSuccess();

	/**
	 * Must be called before any subsequent calls to {@link #onError(Exception)}
	 * or {@link #onSuccess()}. The first time it is called, the timer for
	 * checking the timeout condition will be started
	 * 
	 * @return true unless {@link #onSuccess()} has been called. The first call
	 *         to
	 */
	boolean shouldTryAgain();

/**
   * Set the time to sleep after failed attempts(must be greater than zero or {@link #NO_SLEEP_TIME_MS} to indicate that this should not sleep between calls). Default sleep time is {@link #DEFAULT_SLEEP_TIME_MS}. Will cause {@link #onError(Exception) to sleep. Must be called before any calls to #shouldTryAgain()
   * 
   * @param sleepMs
   *          time to sleep in milliseconds.
   * @return this instance.
   */
	Retry setSleepMs(long sleepMs);

	/**
	 * Set the maximum number of attempts( must be greater than zero or
	 * {@link #NO_MAX_ATTEMPTS} to indicate that it should try forever. Will
	 * default to {@link #DEFAULT_MAX_ATTEMPTS}. Must be called before any calls
	 * to #shouldTryAgain()
	 * 
	 * @param sleepMs
	 *            time to sleep in milliseconds.
	 * @return this instance.
	 */
	Retry setMaxAttempts(int maxAttempts);

/**
   * Set the time to sleep after failed attempts. (must be greater than zero or {@link #NO_MAX_TIME_MS} to indicate that there is no max time). Default is {@link #DEFAULT_MAX_TIME_MS}. Will cause {@link #onError(Exception) to sleep. Must be called before any calls to #shouldTryAgain()
   * 
   * @param sleepMs
   *          time to sleep in milliseconds.
   * @return this instance.
   */
	Retry setMaxTimeMs(long maxTimeMs);
}

