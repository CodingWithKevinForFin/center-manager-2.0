package com.f1.containerpersist;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.f1.base.Factory;
import com.f1.base.IdeableGenerator;
import com.f1.base.ValuedListener;
import com.f1.container.Container;
import com.f1.container.ContainerListener;
import com.f1.container.ContainerScope;
import com.f1.container.PersistenceController;
import com.f1.container.impl.PersistenceRoot;
import com.f1.persist.PersistException;
import com.f1.persist.PersistStoreListener;
import com.f1.persist.reader.TransactionalPersistReader;
import com.f1.persist.sinks.FilePersister;
import com.f1.persist.sinks.PersistClientSocket;
import com.f1.persist.sinks.PersistServerSocket;
import com.f1.persist.writer.TransactionalPersistWriterFactory;
import com.f1.utils.IOH;
import com.f1.utils.OH;

public class ContainerPersister implements Factory<Object, ValuedListener>, ContainerListener, PersistStoreListener {

	private PersistenceController persistenceController;
	private TransactionalPersistWriterFactory factory;
	private FilePersister persister;
	private IdeableGenerator generator;
	private FilePersister filePersist;
	private List<Object> objects = new ArrayList<Object>();

	public ContainerPersister(PersistenceController persistenceController) throws IOException {
		persistenceController.assertNotStarted();
		this.persistenceController = persistenceController;
		this.persistenceController.addValueListenerFactory(this);
		this.persistenceController.getContainer().addListener(this);
		this.generator = persistenceController.getServices().getGenerator();
		if (this.generator == null)
			throw new NullPointerException("generator");
		this.factory = new TransactionalPersistWriterFactory(generator);
	}

	public void addFileReplication(File persistDir, boolean async, long maxDeltaSizeBytes) throws Exception {
		try {
			persistenceController.assertNotStarted();
			if (filePersist != null)
				throw new RuntimeException("already added file persist");
			IOH.ensureDir(persistDir);
			this.filePersist = new FilePersister(persistDir, factory, generator, async, maxDeltaSizeBytes);
		} catch (Exception e) {
			throw new PersistException("Error adding file replication at: " + IOH.getFullPath(persistDir), e);
		}
	}
	public void addServerPort(int port, boolean async) throws IOException {
		new PersistServerSocket(port, async, factory);
	}

	public void addClientPort(String host, int port) throws IOException {
		persistenceController.assertStarted();
		TransactionalPersistReader persist = new TransactionalPersistReader(generator);
		new PersistClientSocket(host, port, persist, true);
		persist.addListener(this);
	}
	@Override
	public ValuedListener get(Object key) {
		return factory.createListener();
	}

	@Override
	public void onPreStart(Container container) {
		if (this.filePersist != null)
			try {
				this.filePersist.startup();
			} catch (Exception e) {
				throw OH.toRuntime(e);
			}
	}

	private void sendCurrentObjectsToPersistenceController() {
		if (this.filePersist != null)
			this.filePersist.popRecoveredObjects(objects);
		for (Object o : objects) {
			if (o instanceof PersistenceRoot)
				this.persistenceController.addForReplication((PersistenceRoot) o);
		}
	}

	@Override
	public void onPostStart(Container container) {
		sendCurrentObjectsToPersistenceController();
	}

	@Override
	public void onPreStop(Container container) {
	}

	@Override
	public void onPostStop(Container container) {
	}

	@Override
	public void onPreStartDispatching(Container container) {
	}

	@Override
	public void onPostStartDispatching(Container container) {
	}

	@Override
	public void onPreStopDispatching(Container container) {
	}

	@Override
	public void onPostStopDispatching(Container container) {
	}

	@Override
	public void onObjectAdded(Long id, Object object) {
		if (object instanceof PersistenceRoot)
			persistenceController.addForReplication((PersistenceRoot) object);
	}

	@Override
	public void onObjectRemoved(Long id, Object object) {
	}

	@Override
	public void onContainerScopeAdded(ContainerScope abstractContainerScope) {
	}

	public TransactionalPersistWriterFactory getFactory() {
		return this.factory;
	}
}
