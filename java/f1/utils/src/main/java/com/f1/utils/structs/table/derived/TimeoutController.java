package com.f1.utils.structs.table.derived;

public interface TimeoutController {

	int throwIfTimedout();//if timeout reached, throws an exception, otherwise returns millis remaining (always > 0)
	int getTimeoutMillisRemainingOrZero();//returns a number between 0 and Integer.MAX. Zero indicates that their is no timeout left... Note, that many timeout implementations treat 0 as no timeout.
	int getTimeoutMillisRemaining();//returns a number between 1 and Integer.MAX some timeouts treat zero as no timeout, so this will return 1 instead of zero
	int throwIfTimedout(DerivedCellCalculator amiExecuteDerivedCellCalculator);
	long getTimeoutMillis();
	void toDerivedThrowIfTimedout(DerivedCellCalculator block);
	long getStartTimeNanos();
	void addTime(long addTime);//Should only be used when in a debugger.

}
