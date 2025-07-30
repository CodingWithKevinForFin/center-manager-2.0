package com.f1.utils.flogger;

/**
 * Creates floggers, typically there will be one instance per JVM. It is
 * associated with a default formatter. It has the various adapters registered
 * with it for logging to underlying loggers. Serves as a factory to generate
 * new {@link Flogger}s
 */
public interface FloggerManager {

	void registerFloggerAdapter(FloggerAdapter adapter);

	void setDefaultFloggerFormatter(FloggerFormatter formatter);

	FloggerFormatter getDefaultFloggerFormatter();

	Flogger createFlogger(String txnId);

	<T> FloggerAdapter<T> getAdapter(T logger);

}
