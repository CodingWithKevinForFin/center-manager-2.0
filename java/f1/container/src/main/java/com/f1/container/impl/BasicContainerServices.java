/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.f1.base.Action;
import com.f1.base.Clock;
import com.f1.base.IdeableGenerator;
import com.f1.container.ContainerScope;
import com.f1.container.ContainerServices;
import com.f1.container.State;
import com.f1.container.ThrowableHandler;
import com.f1.msg.MsgManager;
import com.f1.msg.impl.BasicMsgManager;
import com.f1.utils.CH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.LocaleFormatterManager;
import com.f1.utils.OfflineConverter;
import com.f1.utils.PropertyController;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.db.BasicDataSourceManager;
import com.f1.utils.db.Database;
import com.f1.utils.db.DatabaseManager;
import com.f1.utils.ids.BasicIdGenerator;
import com.f1.utils.ids.BasicIdGenerator.Factory;
import com.f1.utils.ids.BasicNamespaceIdGenerator;
import com.f1.utils.ids.BasicStringIdGenerator;
import com.f1.utils.ids.IdGenerator;
import com.f1.utils.ids.NamespaceIdGenerator;
import com.f1.utils.impl.DefaultClock;

public class BasicContainerServices extends AbstractContainerScope implements ContainerServices {
	public static final int TICKET_LENGTH = 10;
	public static final String TICKET_PREFIX = "TK";

	private Map<String, Object> services = new HashMap<String, Object>();
	private IdeableGenerator objectFactory;
	private PropertyController propertyController;
	private Clock clock = new DefaultClock();
	private NamespaceIdGenerator<Long> uidGenerator;
	private NamespaceIdGenerator<String> ticketGenerator;
	private OfflineConverter converter;
	private ThrowableHandler<Action, State> defaultThrowableHandler = new BasicThrowableHandler();
	private LocaleFormatterManager localeFormatterManager;
	private DatabaseManager datasourceManager;
	private MsgManager msgManager;
	private ObjectToJsonConverter jsonConverter = new ObjectToJsonConverter();

	public BasicContainerServices() {
		Factory factory = new BasicIdGenerator.Factory(0);
		this.uidGenerator = new BasicNamespaceIdGenerator<Long>(factory);
		this.ticketGenerator = new BasicNamespaceIdGenerator<String>(new BasicStringIdGenerator.Factory(factory, 10, ""));
		this.localeFormatterManager = new LocaleFormatterManager();
		this.datasourceManager = new BasicDataSourceManager();
		this.msgManager = new BasicMsgManager();
		jsonConverter.setCompactMode(true);
	}

	@Override
	public IdeableGenerator getGenerator() {
		return objectFactory;
	}

	@Override
	public PropertyController getPropertyController() {
		return propertyController;
	}

	@Override
	public Object getService(String serviceName) {
		return CH.getOrThrow(services, serviceName, "custom service not found");
	}
	@Override
	public <C> C getService(String serviceName, Class<C> castTo) {
		return CH.getOrThrow(castTo, services, serviceName, "custom service not found");
	}

	@Override
	public Object getServiceNoThrow(String serviceName) {
		return services.get(serviceName);
	}

	@Override
	public void putService(String name, Object service) {
		assertNotStarted();
		CH.putOrThrow(services, name, service);
		if (service instanceof ContainerScope) {
			ContainerScope cs = (ContainerScope) service;
			if (cs.getParentContainerScope() == null)
				addChildContainerScope(cs);
		}
	}

	@Override
	public Set<String> getServiceNames() {
		return services.keySet();
	}

	@Override
	public void setGenerator(IdeableGenerator generator) {
		assertNotStarted();
		this.objectFactory = generator;
	}

	@Override
	public void setPropertyController(PropertyController pc) {
		assertNotStarted();
		this.propertyController = pc;
	}

	@Override
	public Clock getClock() {
		return clock;
	}

	@Override
	public void setClock(Clock clock) {
		assertNotStarted();
		this.clock = clock;
	}

	@Override
	public IdGenerator<Long> getUidGenerator(String nameSpace) {
		return this.uidGenerator.getIdGenerator(nameSpace);
	}

	@Override
	public void setUidGenerator(NamespaceIdGenerator<Long> uidGenerator) {
		assertNotStarted();
		this.uidGenerator = uidGenerator;
	}

	@Override
	public IdGenerator<String> getTicketGenerator(String nameSpace) {
		return this.ticketGenerator.getIdGenerator(nameSpace);
	}

	@Override
	public void setTicketGenerator(NamespaceIdGenerator<String> ticketGenerator) {
		assertNotStarted();
		this.ticketGenerator = ticketGenerator;
	}

	@Override
	public OfflineConverter getConverter() {
		return converter;
	}

	@Override
	public void setConverter(OfflineConverter converter) {
		this.converter = converter;
	}

	@Override
	public ThrowableHandler<Action, State> getDefaultThrowableHandler() {
		return defaultThrowableHandler;
	}

	@Override
	public void setDefaultThrowableHandler(ThrowableHandler<Action, State> defaultThrowableHandler) {
		assertNotStarted();
		this.defaultThrowableHandler = defaultThrowableHandler;
	}

	@Override
	public void setLocaleFormatterManager(LocaleFormatterManager localeFormatterManager) {
		assertNotStarted();
		this.localeFormatterManager = localeFormatterManager;
	}

	@Override
	public LocaleFormatterManager getLocaleFormatterManager() {
		return localeFormatterManager;
	}

	@Override
	public LocaleFormatter getLocaleFormatter() {
		return getLocaleFormatterManager().getThreadSafeLocaleFormatter(getClock().getLocale(), getClock().getTimeZone());
	}

	public void setDatasourceManager(DatabaseManager datasourceManager) {
		this.datasourceManager = datasourceManager;
	}

	public DatabaseManager getDatasourceManager() {
		return datasourceManager;
	}

	@Override
	public DatabaseManager getDatabaseManager() {
		return datasourceManager;
	}

	@Override
	public void setDatabaseManager(DatabaseManager datasourceManager) {
		assertNotStarted();
		this.datasourceManager = datasourceManager;
	}

	@Override
	public Database getDatabase(String name) {
		return getDatasourceManager().getDatabase(name);
	}

	@Override
	public void addDatabase(String name, Database dataSource) {
		getDatasourceManager().addDataBase(name, dataSource);
	}

	@Override
	public NamespaceIdGenerator<Long> getUidGenerator() {
		return uidGenerator;
	}

	@Override
	public MsgManager getMsgManager() {
		return msgManager;
	}

	@Override
	public void setMsgManager(MsgManager msgManager) {
		assertNotStarted();
		this.msgManager = msgManager;
	}

	@Override
	public ObjectToJsonConverter getJsonConverter() {
		return jsonConverter;
	}

	@Override
	public void setJsonConverter(ObjectToJsonConverter jsonConverter) {
		this.jsonConverter = jsonConverter;
	}

}
