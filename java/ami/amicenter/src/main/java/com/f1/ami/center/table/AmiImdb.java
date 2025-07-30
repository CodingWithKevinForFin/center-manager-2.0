package com.f1.ami.center.table;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.f1.ami.center.AmiDboFactoryWrapper;
import com.f1.ami.center.dbo.AmiDbo;
import com.f1.ami.center.procs.AmiStoredProc;
import com.f1.ami.center.procs.AmiStoredProcFactory;
import com.f1.ami.center.table.persist.AmiTablePersisterFactory;
import com.f1.ami.center.timers.AmiTimer;
import com.f1.ami.center.timers.AmiTimerFactory;
import com.f1.ami.center.triggers.AmiCommandTriggerPlugin;
import com.f1.ami.center.triggers.AmiServicePlugin;
import com.f1.ami.center.triggers.AmiTimedRunnable;
import com.f1.ami.center.triggers.AmiTrigger;
import com.f1.ami.center.triggers.AmiTriggerFactory;
import com.f1.ami.center.triggers.AmiTriggerPlugin;
import com.f1.container.ContainerTools;
import com.f1.utils.structs.table.derived.MethodFactoryManager;

/**
 * IMDB is Anvil's In Memory Database, Not the Internet Movie database ;) From here you have access to all the core objects that make up said database, including:
 * 
 * <PRE>
 *  tables - {@link AmiTable}, 
 *  triggers - {@link AmiTrigger}, 
 *  stored procedures - {@link AmiStoredProcedure}, 
 *  Indexes - {@link AmiIndex},
 *  Services -{@link AmiServicePlugin}
 *  TimedEvents -{@link AmiTimedRunnable}
 * </PRE>
 * <P>
 * <U>Thread Note:</U> A given IMDB and all of its objects (shown above) are accessed by only a single thread at a time. It's CRITICAL that you do not implement your own threads
 * and rely purely on callbacks (such as triggers and timers) for access to this IMDB and its owned objects, listed above. Rest assured, 3Forge does as much optimization as
 * possible in other threads before supplying/taking data from the IMDB. Note this also includes TimedEvents
 * <P>
 * 
 * <U>Usage:</U> Because of the threading nature mentioned above, on call backs you can enjoy safe access to all of the objects. For example, a call back to a particular trigger
 * can query/update/delete from as many tables as required and only until the call back exists (you call return) will the framework propagate/publish resulting deltas.
 * <P>
 * 
 * <PRE>
 * Startup sequence:
 * 
 * 01) All {@link AmiTable} are created
 * 02) All {@link AmiIndex} are applied
 * 
 * 03) All {@link AmiServicePlugin}s are constructed
 * 04) All {@link AmiTriggerPlugin}s are constructed
 * 05) All {@link AmiStoredProcedure}s are constructed
 * 06) All {@link AmiCommandTriggerPlugin}s are constructed
 * 
 * 07) All {@link AmiServicePlugin#startup(AmiImdb)}s are called
 * 08) All {@link AmiTriggerPlugin#startup(AmiImdb)}s are called
 * 09) All {@link AmiStoredProcedure#startup(AmiImdb)}s are called
 * 10) All {@link AmiCommandTriggerPlugin#startup(AmiImdb)}s are called
 * 11) All {@link AmiServicePlugin#onStartupComplete()}s are called
 * 
 * 12) Process transitions into "Runtime", meaning incoming data, commands, queries and timer events will now start to flow in
 * </PRE>
 * 
 * 
 * 
 */
public interface AmiImdb {

	/**
	 * @return current time in milliseconds
	 */
	long getNow();

	/**
	 * Internal, proprietary
	 */
	int getStringPoolId(String text);
	/**
	 * Internal, proprietary
	 */
	String getStringPoolString(int text);

	//Factories
	Set<String> getTablePersisterTypes();
	AmiTablePersisterFactory getTablePersisterFactory(String type);

	Set<String> getTriggerTypes();
	AmiTriggerFactory getTriggerFactory(String type);

	Set<String> getTimerTypes();
	AmiTimerFactory getTimerFactory(String type);

	Set<String> getStoredProcTypes();
	AmiStoredProcFactory getStoredProcFactory(String type);

	AmiTimer getAmiTimer(String name);
	AmiTrigger getAmiTrigger(String name);
	<T extends AmiTimer> T getAmiTimerOrThrow(String name, Class<T> clazz);
	Set<String> getAmiTimerNamesSorted();
	Collection<? extends AmiTimer> getAmiTimers();

	AmiStoredProc getAmiStoredProc(String name);
	<T extends AmiStoredProc> T getAmiStoredProcOrThrow(String name, Class<T> clazz);
	Set<String> getAmiStoredProcNamesSorted();
	Collection<? extends AmiStoredProc> getAmiStoredProcs();

	AmiTable getAmiTable(String name);
	AmiTable getAmiTableOrThrow(String name);
	Set<String> getAmiTableNamesSorted();
	Collection<? extends AmiTable> getAmiTables();

	//	void executeSql(String amiScript, CalcFrame objects, DerivedCellTimeoutController timeout, int limit, SqlPlanListener planListener, CalcFrameStack csf);
	//	void executeSql(DerivedCellCalculatorExpression amiScript, com.f1.base.Types types, Map<String, Object> objects, AmiImdbSession session,
	//			DerivedCellTimeoutController timeout, int limit, SqlPlanListener planListener);
	//	DerivedCellCalculatorExpression prepareSql(String amiScript, com.f1.base.CalcTypes types, CalcFrameStack cfs);
	boolean registerTimerFromNow(String timerName, long offset, TimeUnit timeunit);
	boolean registerTimerAtTime(String timerName, long timeMillis);
	ContainerTools getTools();

	MethodFactoryManager getAmiScriptMethodFactory();
	AmiImdbSessionManagerService getSessionManager();

	Set<String> getServiceNames();
	AmiServicePlugin getAmiService(String name);
	<T extends AmiServicePlugin> T getAmiServiceOrThrow(String name, Class<T> clazz);

	Set<String> getDboTypes();
	AmiDboFactoryWrapper getDboFactory(String typeName);

	Set<String> getAmiDboNamesSorted();

	AmiDbo getAmiDbo(String name);
}
