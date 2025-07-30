package com.f1.ami.center.triggers;

import com.f1.ami.amicommon.AmiPlugin;
import com.f1.ami.center.table.AmiImdb;
import com.f1.ami.center.table.AmiImdbSession;

/**
 * A service is registered in the {@link AmiImdb} as a general object where one can store/retrieve common variables, implement common methods etc. This is more architecturally
 * sound than using static variables methods, etc.
 */

public interface AmiServicePlugin extends AmiPlugin {

	byte STATE_STEP1_IMDB_INITIALIZED = 1;
	byte STATE_STEP2_TABLES_INITIALIZED = 2;
	byte STATE_STEP3_TRIGGERS_INITIALIZED = 3;
	byte STATE_STEP4_PROCEDURES_INITIALIZED = 4;
	byte STATE_STEP5_DBOS_INITIALIZED = 5;
	byte STATE_STEP6_TRIGGERS_FIRED = 6;
	byte STATE_STEP7_TIMERS_INITIALIZED = 7;

	/**
	 * Called once at startup, this is a good spot to grab needed tables, columns, indexes, etc that you will use at runtime
	 * 
	 * @param imdb
	 * @param session
	 * @param step
	 */
	public void startup(AmiImdb imdb, AmiImdbSession session, byte step);

	/**
	 * @return the id of this service.
	 */
	public String getPluginId();

}
