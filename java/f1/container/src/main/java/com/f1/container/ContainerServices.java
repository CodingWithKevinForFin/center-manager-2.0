/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container;

import java.util.NoSuchElementException;
import java.util.Set;

import com.f1.base.Action;
import com.f1.base.Clock;
import com.f1.base.IdeableGenerator;
import com.f1.msg.MsgManager;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.LocaleFormatterManager;
import com.f1.utils.OfflineConverter;
import com.f1.utils.PropertyController;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.db.Database;
import com.f1.utils.db.DatabaseManager;
import com.f1.utils.ids.IdGenerator;
import com.f1.utils.ids.NamespaceIdGenerator;

/**
 * Facade for accessing a series of 'secondary' services common to typical applications which include factories, id generators, time / local resolvers and custom services. All
 * members of the {@link ContainerServices} are shared throughout the container scope and may be accessed by anything within the container, and as such should be thread safe. Note
 * that registering custom services ({@link #putService(String, Object)}) and other set...() methods can not be invoked when started, as described in {@link StartStoppable}
 * <P>
 * See the {@link ContainerTools} for convenience methods that help encompass common needs.
 */
public interface ContainerServices extends ContainerScope {
	/** default name space */
	String NAMESPACE_DEFAULT = "DEFAULT";

	/**
	 * @return the object factory used to generate objects throughout the container scope. Typically, the {@link ContainerScope#nw(Class)} method will use the object factory
	 *         returned by this method.
	 * @return said object factory must not return null
	 */
	IdeableGenerator getGenerator();

	/**
	 * used to get a long generator for a particular namespace. See {@link NamespaceIdGenerator} and {@link IdGenerator} for details on how namespaces work. The scope of uniqueness
	 * is left up to the user (jvm / global / runtime / etc...)
	 * 
	 * @param nameSpace
	 *            the namespace for a particular generator
	 * @return the generator for supplied namespace
	 */
	IdGenerator<Long> getUidGenerator(String nameSpace);

	NamespaceIdGenerator<Long> getUidGenerator();

	/**
	 * register the {@link NamespaceIdGenerator} which will be used by this container scope for generating uids (unique longs).
	 * 
	 * @param idGenerator
	 *            a unique id.
	 */
	void setUidGenerator(NamespaceIdGenerator<Long> idGenerator);

	/**
	 * used to get a string generator for a particular namespace. See {@link NamespaceIdGenerator} and {@link IdGenerator} for details on how namespaces work. The scope of
	 * uniqueness is left up to the user (jvm / global / runtime / etc...)
	 * 
	 * @param nameSpace
	 *            the namespace for a particular generator
	 * @return the generator for supplied namespace
	 */
	IdGenerator<String> getTicketGenerator(String nameSpace);

	/**
	 * register the {@link NamespaceIdGenerator} which will be used by this container scope for generating uids (unique strings).
	 * 
	 * @param idGenerator
	 *            a unique id.
	 */
	void setTicketGenerator(NamespaceIdGenerator<String> idGenerator);

	/**
	 * see {@link #getGenerator()}
	 * 
	 * @param generator
	 */
	void setGenerator(IdeableGenerator generator);

	/**
	 * Returns the ids of all the custom registered services. see {@link #getService(String)} and {@link #putService(String, Object)}
	 * 
	 * @return unordered set of services. never null, but may be empty
	 */
	Set<String> getServiceNames();

	/**
	 * returns the custom service associated w/ a supplied service name
	 * 
	 * @param serviceName
	 *            the name of the service to return
	 * @return the service. never null
	 * @throw {@link NoSuchElementException} if no service exists for the supplied service name
	 */
	Object getService(String serviceName);
	Object getServiceNoThrow(String serviceName);
	<C> C getService(String serviceName, Class<C> castTo);

	/**
	 * registers a new service by name with this.
	 * 
	 * @param name
	 *            name of the service
	 * @param service
	 *            service to add
	 * @throws RuntimeException
	 *             if the supplied name is already registered w/ a service other than the one supplied
	 * 
	 */
	void putService(String name, Object service);

	/**
	 * @return property controller registed with this container scope via {@link #setPropertyController(PropertyController)}
	 */
	PropertyController getPropertyController();

	/**
	 * @param pc
	 *            the property controller for this container scope.
	 */
	void setPropertyController(PropertyController pc);

	/**
	 * the clock registered with this container scope via {@link #setClock(Clock)}
	 * 
	 * @return
	 */
	Clock getClock();

	/**
	 * 
	 * @param clock
	 *            the clock for this container scope
	 */
	void setClock(Clock clock);

	/**
	 * @return the Converter registered with this container scope
	 */
	OfflineConverter getConverter();

	/**
	 * registers a particular converter w/ this container scope
	 * 
	 * @param converter
	 */
	void setConverter(OfflineConverter converter);

	/**
	 * @return the default object which will handle throwables
	 */
	ThrowableHandler<Action, State> getDefaultThrowableHandler();

	/**
	 * @return assign the default object which will handle throwables
	 */
	void setDefaultThrowableHandler(ThrowableHandler<Action, State> defaultThrowableHandler);
	void setLocaleFormatterManager(LocaleFormatterManager localeFormatterManager);

	LocaleFormatterManager getLocaleFormatterManager();
	LocaleFormatter getLocaleFormatter();

	DatabaseManager getDatabaseManager();
	void setDatabaseManager(DatabaseManager datasourceManager);

	Database getDatabase(String name);
	void addDatabase(String name, Database dataSource);

	MsgManager getMsgManager();
	public void setMsgManager(MsgManager msgManager);

	ObjectToJsonConverter getJsonConverter();
	void setJsonConverter(ObjectToJsonConverter jsonConverter);

}
