/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import com.f1.base.Factory;
import com.f1.container.Connectable;
import com.f1.container.Container;
import com.f1.container.ContainerListener;
import com.f1.container.ContainerRuntimeListener;
import com.f1.container.ContainerScope;
import com.f1.container.ContainerServices;
import com.f1.container.ContainerTools;
import com.f1.container.DispatchController;
import com.f1.container.PartitionController;
import com.f1.container.PersistenceController;
import com.f1.container.Port;
import com.f1.container.Processor;
import com.f1.container.ResultActionFutureController;
import com.f1.container.Suite;
import com.f1.container.SuiteController;
import com.f1.container.ThreadPoolController;
import com.f1.container.ThreadScopeController;
import com.f1.container.exceptions.ContainerException;
import com.f1.utils.EH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.impl.PassThroughFactory;

public class BasicContainer extends AbstractContainerScope implements Container {

	private ThreadScopeController threadScopeController;
	private ThreadPoolController threadPoolController;
	private SuiteController suiteController;
	private PartitionController partitionController;
	private DispatchController dispatchController;
	private ContainerTools tools;
	private ContainerServices services;
	private final List<ContainerListener> listeners = new CopyOnWriteArrayList<ContainerListener>();
	private final List<ContainerRuntimeListener> runtimeListeners = new CopyOnWriteArrayList<ContainerRuntimeListener>();
	private PersistenceController persistenceController;
	private Factory<String, String> logFactoryNamer = new PassThroughFactory<String>();
	private ResultActionFutureController resultActionFutureController;

	@Override
	public DispatchController getDispatchController() {
		return dispatchController;
	}

	@Override
	public PartitionController getPartitionController() {
		return partitionController;
	}

	@Override
	public SuiteController getSuiteController() {
		return suiteController;
	}

	@Override
	public ThreadPoolController getThreadPoolController() {
		return threadPoolController;
	}

	@Override
	public ThreadScopeController getThreadScopeController() {
		return threadScopeController;
	}

	@Override
	public ContainerTools getTools() {
		return tools;
	}

	@Override
	public ContainerServices getServices() {
		return services;
	}

	@Override
	public PersistenceController getPersistenceController() {
		return persistenceController;
	}

	@Override
	public ResultActionFutureController getResultActionFutureController() {
		return this.resultActionFutureController;
	}

	public BasicContainer() {
		setServices(new BasicContainerServices());
		setTools(new BasicContainerTools());
		setPartitionController(new BasicPartitionController());
		setDispatchController(new BasicDispatcherController());
		setThreadPoolController(new BasicThreadPoolController());
		setThreadScopeController(new BasicThreadScopeController());
		setSuiteController(new BasicSuiteController());
		setPersistenceController(new BasicPersistenceController());
		setResultActionFutureContoller(new BasicResultActionFutureController());
		setContainer(this);
	}

	public void setServices(ContainerServices containerServices) {
		assertNotStarted();
		replaceChildContainerScope(this.services, containerServices);
		this.services = containerServices;
	}

	public void setTools(ContainerTools containerTools) {

		assertNotStarted();
		removeChildContainerScope(this.tools);
		addChildContainerScope(containerTools);
		this.tools = containerTools;
	}

	public void setPartitionController(PartitionController partitionController) {

		replaceChildContainerScope(this.partitionController, partitionController);
		this.partitionController = partitionController;
	}

	public void setSuiteController(SuiteController suiteController) {
		replaceChildContainerScope(this.suiteController, suiteController);
		this.suiteController = suiteController;

	}

	public void setPersistenceController(PersistenceController persistenceController) {
		replaceChildContainerScope(this.persistenceController, persistenceController);
		this.persistenceController = persistenceController;
	}

	public void setThreadPoolController(ThreadPoolController threadPoolController) {

		replaceChildContainerScope(this.threadPoolController, threadPoolController);
		this.threadPoolController = threadPoolController;
	}

	public void setThreadScopeController(ThreadScopeController threadScopeController) {

		replaceChildContainerScope(this.threadScopeController, threadScopeController);
		this.threadScopeController = threadScopeController;
	}

	public void setDispatchController(DispatchController dispatchController) {
		replaceChildContainerScope(this.dispatchController, dispatchController);
		this.dispatchController = dispatchController;
	}
	public void setResultActionFutureContoller(BasicResultActionFutureController raf) {
		replaceChildContainerScope(this.resultActionFutureController, raf);
		this.resultActionFutureController = raf;
	}

	@Override
	public void startDispatching() {
		info("Container Starting dispatching sequence");
		for (ContainerListener listener : listeners)
			listener.onPreStartDispatching(this);
		super.startDispatching();
		for (ContainerListener listener : listeners)
			listener.onPostStartDispatching(this);
		info("");
		info("");
		info("3FORGE STARTUP SEQUENCE COMPLETE FOR CONTAINER: " + getName());
		info("");
		info("");
		EH.toStdout("3Forge startup sequence complete for container: " + getName(), true);
	}

	@Override
	public void stopDispatching() {
		info("Container stopping dispatching sequence");
		for (ContainerListener listener : listeners)
			listener.onPreStopDispatching(this);
		super.stopDispatching();
		for (ContainerListener listener : listeners)
			listener.onPostStopDispatching(this);
	}

	@Override
	public Suite getRootSuite() {
		return getSuiteController().getRootSuite();
	}

	@Override
	public void start() {
		for (ContainerListener listener : listeners)
			listener.onPreStart(this);
		super.start();
		int count = verify(this);
		info("Verified " + count + " ConstainerScope objects");
		for (ContainerListener listener : listeners)
			listener.onPostStart(this);
		startDispatching();
	}

	private void info(String string) {
		LH.info(log, "@@@@@@ ", string);

	}

	private int verify(ContainerScope containerScope) {
		int r = 1;
		if (containerScope == null)
			throw new NullPointerException();
		final List<ContainerScope> childScopes = containerScope.getChildContainerScopes();
		if (childScopes == null)
			throw new ContainerException(containerScope, "childScopes is null");
		if (containerScope.getName() == null)
			throw new ContainerException(containerScope, "name is null");
		if (containerScope.getContainer() == null)
			throw new ContainerException(containerScope, "container is null");
		if (containerScope.getLog() == null)
			throw new ContainerException(containerScope, "log is null");
		if (containerScope.getServices() == null)
			throw new ContainerException(containerScope, "services is null");
		if (containerScope.getTools() == null)
			throw new ContainerException(containerScope, "tools is null");
		if (containerScope instanceof Connectable) {
			Connectable connectable = (Connectable) containerScope;
			for (Port port : connectable.getInputs()) {
				if (!port.isInput())
					throw new ContainerException(containerScope, "not an input port").setSourcePort(port);
				if (!childScopes.contains(port))
					throw new ContainerException(containerScope, "input port not a child").setSourcePort(port);
			}
			for (Port port : connectable.getOutputs()) {
				if (port.isInput())
					throw new ContainerException(containerScope, "not an output port").setSourcePort(port);
				if (!childScopes.contains(port))
					throw new ContainerException(containerScope, "output port not a child").setSourcePort(port);
			}
		}
		if (containerScope instanceof Processor) {
			Processor processor = (Processor) containerScope;
			if (processor.getPartitionResolver() == null)
				throw new ContainerException(containerScope, "partition resolver is null (if this is the intent, assign a BasicPartitionResolver with partition id of null)");
			Port inputPort = processor.getInputPort();
			if (inputPort == null)
				throw new ContainerException(containerScope, "input port is null");
			if (!processor.getInputs().contains(inputPort))
				throw new ContainerException(containerScope, "default input port not in list of child input ports").setSourcePort(inputPort);
			if (processor.getActionType() == null)
				throw new ContainerException(containerScope, "action type is null");
		}
		if (containerScope instanceof Port) {
			Port port = (Port) containerScope;
			if (port.getActionType() == null)
				throw new ContainerException(containerScope, "state type is null");
			if (!port.isConnected() && !port.isConnectionOptional()) {
				throw new ContainerException("port is not connected to a processor. If this is intentional, call setConnectionOptional(true)").setSourcePort(port);
			}
		}
		if (!containerScope.isStarted())
			throw new ContainerException(containerScope, "not started");
		Set<String> names = new HashSet<String>();
		for (ContainerScope child : childScopes) {
			if (child.getParentContainerScope() != containerScope)
				throw new ContainerException(this, "parent / child relationship inconsistent").setTargetContainerScope(child);
			if (names.add(child.getName()))
				try {
					r += verify(child);
				} catch (Exception e) {
					throw new ContainerException(containerScope, "verfication of child failed.", e).setTargetContainerScope(child);
				}
		}
		return r;
	}

	@Override
	public void stop() {
		info("calling stopDispatching(...)");
		stopDispatching();
		info("called to stopDispatching(...) complete");
		int count = 0;
		for (;;) {
			if (count % 100 == 0)
				info("Waiting for all events to drain from dispatcher...");
			if (getDispatchController().isIdle())
				break;
			OH.sleep(10);
			count++;
		}
		info("All events have drained, calling stop(...)");
		for (ContainerListener listener : listeners)
			listener.onPreStop(this);
		super.stop();
		for (ContainerListener listener : listeners)
			listener.onPostStop(this);
		info("call to stop(...) complete");
	}

	@Override
	public void addListener(ContainerListener containerListener) {
		assertNotStarted();
		listeners.add(containerListener);
	}

	@Override
	public void removeListener(ContainerListener containerListener) {
		assertNotStarted();
		listeners.remove(containerListener);
	}

	@Override
	public Factory<String, String> getLogNamer() {
		return logFactoryNamer;
	}

	@Override
	public void setLogNamer(Factory<String, String> logFactory) {
		assertNotInit();
		this.logFactoryNamer = logFactory;
	}

	@Override
	public void addRuntimeListener(ContainerRuntimeListener containerRuntimeListener) {
		assertNotStarted();
		runtimeListeners.add(containerRuntimeListener);
	}

	@Override
	public void removeRuntimeListener(ContainerRuntimeListener containerRuntimeListener) {
		assertNotStarted();
		runtimeListeners.remove(containerRuntimeListener);
	}

	@Override
	public Iterable<ContainerListener> getListeners() {
		return listeners;
	}

	@Override
	public Iterable<ContainerRuntimeListener> getRuntimeListeners() {
		return runtimeListeners;
	}

}
