/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.base.IdeableGenerator;
import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.Container;
import com.f1.container.ContainerConstants;
import com.f1.container.ContainerListener;
import com.f1.container.ContainerScope;
import com.f1.container.ContainerServices;
import com.f1.container.ContainerTools;
import com.f1.container.exceptions.ContainerException;
import com.f1.utils.CH;
import com.f1.utils.Iterator2Iterable;
import com.f1.utils.LH;
import com.f1.utils.Labeler;
import com.f1.utils.OH;
import com.f1.utils.ReverseListIterator;

public abstract class AbstractContainerScope extends AbstractStartStoppable implements ContainerScope {
	protected Logger log = Logger.getLogger("CONTAINERSCOPE_PREINIT");
	private Container container;
	private ContainerTools tools;
	private ContainerServices services;
	private IdeableGenerator generator;
	private String name = getClass().getSimpleName();
	private ContainerScope parentContainerScope;
	private IdentityHashMap<ContainerScope, ContainerScope> childContainerScopes = new IdentityHashMap<ContainerScope, ContainerScope>();
	private List<ContainerScope> childContainerScopeList = new ArrayList<ContainerScope>();
	private boolean isDispatching;
	private boolean isInit;
	final private long containerScopeUid = ContainerHelper.nextContainerScopeUid();

	@Override
	public Container getContainer() {
		return container;
	}

	@Override
	public void setContainer(Container container) {
		assertNotStarted();
		if (this.container != null && container != null)
			throw new ContainerException("container already set");
		if (this.container == container)
			return;
		this.container = container;
		if (container == null) {
			//TODO:call onContainerScopedRemoved(...);
		} else {
			for (ContainerListener listener : this.container.getListeners()) {
				listener.onContainerScopeAdded(this);
			}
		}
		initReferences();
		for (ContainerScope c : getChildContainerScopes())
			c.setContainer(container);
	}

	@Override
	public ContainerTools getTools() {
		if (tools == null)
			initReferences();
		return tools;
	}

	@Override
	public ContainerServices getServices() {
		if (services == null)
			initReferences();
		return services;
	}

	@Override
	public <C> C nw(Class<C> clazz) {
		if (generator == null)
			initReferences();
		return generator.nw(clazz);
	}

	public IdeableGenerator getGenerator() {
		if (generator == null)
			initReferences();
		return generator;
	}

	final private void initReferences() {
		if (container == null)
			return;
		tools = container.getTools();
		services = container.getServices();
		if (services != null)
			generator = services.getGenerator();
	}

	@Override
	public void startDispatching() {
		assertNotDispatchingStarted();
		assertInit();
		assertStarted();
		isDispatching = true;
		for (ContainerScope c : getChildContainerScopes())
			try {
				c.startDispatching();
				if (!c.isDispatchingStarted())
					throw new ContainerException("when overriding startDispatching() call super.startDispatching()").setContainerScope(c);
			} catch (ContainerException e) {
				throw new ContainerException("error while starting dispatch", e).setContainerScope(c);
			}
	}

	@Override
	public void assertNotDispatchingStarted() {
		if (isDispatching)
			throw new ContainerException(this, "dispatching already started: startDispatching() has been called.");
	}

	@Override
	public void assertDispatchingStarted() {
		if (!isDispatching)
			throw new ContainerException(this, "dispatching not started: startDispatching() has not been called.");
	}

	@Override
	public void stopDispatching() {
		assertStarted();
		assertDispatchingStarted();
		for (ContainerScope c : getChildContainerScopes())
			try {
				c.stopDispatching();
				if (c.isDispatchingStarted())
					throw new ContainerException("when overriding stopDispatching() call super.stopDispatching()").setContainerScope(this);
			} catch (ContainerException e) {
				throw new ContainerException("error while starting dispatch", e).setContainerScope(this);
			}
		this.isDispatching = false;
	}

	@Override
	public void start() {
		assertInit();
		try {
			super.start();
		} catch (RuntimeException e) {
			throw new ContainerException("failed to start ", e).setContainerScope(this);

		}
		if (container == null)
			throw new ContainerException(this, "container not specified").setContainerScope(this);
		for (ContainerScope c : getChildContainerScopes())
			try {
				c.start();
				if (!c.isStarted())
					throw new RuntimeException("when overriding start() be sure to call super.start()");
			} catch (ContainerException e) {
				if (e.getContainerScope() == null)
					e.setContainerScope(c);
				throw e;
			} catch (Exception e) {
				throw new ContainerException(c, "Error starting up child ContainerScoped", e);
			}
		if (log.isLoggable(Level.FINER))
			LH.finer(log, "Started: ", getFullName());
	}

	@Override
	public void stop() {
		assertStarted();
		assertNotDispatchingStarted();
		if (container == null)
			throw new ContainerException("container not specified").setContainerScope(this);
		for (ContainerScope c : new Iterator2Iterable<ContainerScope>(new ReverseListIterator<ContainerScope>(getChildContainerScopes())))
			c.stop();
		super.stop();
	}

	@Override
	public <C> ObjectGeneratorForClass<C> getGenerator(Class<C> clazz) {
		return getServices().getGenerator().getGeneratorForClass(clazz);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ContainerScope setName(String name) {
		assertNotStarted();
		this.name = name;
		return this;
	}

	@Override
	public ContainerScope getParentContainerScope() {
		return parentContainerScope;
	}

	@Override
	public void setParentContainerScope(ContainerScope parentContainerScope) {
		assertNotStarted();
		if (this.parentContainerScope == parentContainerScope)
			return;
		if (this.parentContainerScope != null && parentContainerScope != null)
			throw new ContainerException("parent already set").setContainerScope(this).setTargetContainerScope(parentContainerScope);
		this.parentContainerScope = parentContainerScope;
	}

	@Override
	public List<ContainerScope> getChildContainerScopes() {
		return childContainerScopeList;
	}

	@Override
	public <C extends ContainerScope> C addChildContainerScope(C child) {
		if (isStarted()) {
			synchronized (this) {
				for (ContainerScope c = this; c != null; c = c.getParentContainerScope())
					if (child == c)
						throw new ContainerException("circular reference adding child").setContainerScope(this).setTargetContainerScope(child);
				if (childContainerScopes.containsKey(child))
					throw new ContainerException("container scope already child of this").setContainerScope(this).setTargetContainerScope(child);
				childContainerScopes.put(child, child);
				childContainerScopes = CH.copyAndPut(this.childContainerScopes, child, child);
				childContainerScopeList = CH.copyAndAdd(this.childContainerScopeList, child);
				child.setParentContainerScope(this);
				if (child.getContainer() == null)
					child.setContainer(getContainer());
				initChild(child);
			}
			return child;
		}
		assertNotStarted();
		for (ContainerScope c = this; c != null; c = c.getParentContainerScope())
			if (child == c)
				throw new ContainerException("circular reference adding child").setContainerScope(this).setTargetContainerScope(child);

		if (childContainerScopes.containsKey(child))
			throw new ContainerException("container scope already child of this").setContainerScope(this).setTargetContainerScope(child);
		childContainerScopes.put(child, child);
		childContainerScopeList.add(child);
		child.setParentContainerScope(this);
		if (getContainer() != null && child.getContainer() == null)
			child.setContainer(getContainer());
		if (isInit())
			initChild(child);
		return child;
	}

	@Override
	public <C extends ContainerScope> void replaceChildContainerScope(C existing, C replacement) {
		assertNotStarted();
		if (existing == null)
			addChildContainerScope(replacement);
		else {
			try {
				CH.removeOrThrow(childContainerScopes, existing);
				CH.putOrThrow(childContainerScopes, replacement, replacement);
				CH.replaceOrThrow(childContainerScopeList, existing, replacement);
			} catch (Throwable t) {
				throw new ContainerException("could not replace source with target").setContainerScope(this).setSourceContainerScope(existing).setTargetContainerScope(replacement);
			}
		}
	}

	protected void removeChildContainerScope(ContainerScope child) {
		assertNotStarted();
		if (child == null)
			return;
		try {
			CH.removeOrThrow(childContainerScopes, child);
			CH.removeOrThrow(childContainerScopeList, child);
		} catch (Throwable t) {
			throw new ContainerException("could not remove target").setContainerScope(this).setTargetContainerScope(child);
		}
		child.setParentContainerScope(null);
		child.setContainer(null);
	}

	@Override
	public String getFullName() {
		ContainerScope p = getParentContainerScope();
		if (p == null)
			return ContainerConstants.NAME_SEPERATOR + getName();
		return p.getFullName() + ContainerConstants.NAME_SEPERATOR + getName();
	}

	@Override
	public ContainerScope getChild(String name) {
		final int i = name.indexOf('/');
		if (i == -1) {
			for (ContainerScope child : getChildContainerScopes())
				if (name.equals(child.getName()))
					return child;
			throw new ContainerException(this, "child not found").set("child name", name).set("available child names:", getChildNames());
		} else {
			final String directChildName = name.substring(0, i);
			for (ContainerScope child : getChildContainerScopes())
				if (directChildName.equals(child.getName()))
					return child.getChild(name.substring(i + 1));
			throw new ContainerException(this, "child not found").set("direct child name", directChildName).set("child name", name).set("available child names:", getChildNames());
		}
	}

	private Set<String> getChildNames() {
		TreeSet<String> r = new TreeSet<String>();
		for (ContainerScope child : getChildContainerScopes())
			r.add(child.getName());
		return r;
	}

	@Override
	public Logger getLog() {
		return log;
	}

	@Override
	public boolean isDispatchingStarted() {
		return isDispatching;
	}

	@Override
	public void init() {
		assertNotInit();
		if (container == null)
			throw new ContainerException(this, "container is null");
		log = Logger.getLogger(container.getLogNamer().get(getClass().getName()));
		this.isInit = true;
		for (ContainerScope c : getChildContainerScopes())
			initChild(c);
		if (log.isLoggable(Level.FINER))
			LH.finer(log, "Initialized: ", getFullName());
	}

	private void initChild(ContainerScope c) {
		try {
			c.init();
			if (!c.isInit())
				throw new ContainerException(c, "when overriding init() call super.init()");
		} catch (ContainerException e) {
			if (e.getContainerScope() == null)
				e.setContainerScope(c);
			throw e;
		} catch (Exception e) {
			throw new ContainerException(c, "Error initializing child ContainerScoped", e);
		}
	}

	@Override
	public boolean isInit() {
		return isInit;
	}

	@Override
	public void assertInit() {
		if (isInit)
			return;
		throw new ContainerException(this, "not initilialized: init() not called.");
	}

	@Override
	public void assertNotInit() {
		if (!isInit)
			return;
		throw new ContainerException(this, "already initilialized: init() already called.");

	}

	@Override
	public void diagnose(Labeler labeler) {
		labeler.addItem("Name", name);
		labeler.addItem("Started", new Date(getStartedMs()));
		labeler.addItem("Is Dispatching", isDispatching);
		labeler.addItem("Is Init", isInit);
		labeler.addItem("Container Instance", container);
		labeler.addItem("Tools Instance", tools);
		labeler.addItem("Services Instance", services);
		labeler.addItem("Full name", getFullName());
		labeler.addItem("Parent", parentContainerScope);
		for (ContainerScope cs : childContainerScopeList)
			labeler.addItem("Children", cs.getName());
	}

	@Override
	public boolean equals(Object o) {
		return o != null && o.getClass() == getClass() && OH.eq(getName(), ((AbstractContainerScope) o).getName());
	}

	@Override
	public int hashCode() {
		return OH.hashCode(getClass(), getName());
	}

	public long getContainerScopeUid() {
		return containerScopeUid;
	}
}
