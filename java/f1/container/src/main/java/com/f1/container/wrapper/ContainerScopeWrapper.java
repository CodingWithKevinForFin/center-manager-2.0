package com.f1.container.wrapper;

import java.util.List;
import java.util.logging.Logger;

import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.Container;
import com.f1.container.ContainerScope;
import com.f1.container.ContainerServices;
import com.f1.container.ContainerTools;
import com.f1.utils.Labeler;

public class ContainerScopeWrapper implements ContainerScope {

	final public ContainerScope inner;

	public ContainerScopeWrapper(ContainerScope inner) {
		this.inner = inner;
	}

	@Override
	public void start() {
		inner.start();
	}

	@Override
	public void stop() {
		inner.stop();
	}

	@Override
	public Container getContainer() {
		return inner.getContainer();
	}

	@Override
	public void setContainer(Container container_) {
		inner.setContainer(container_);
	}

	@Override
	public ContainerTools getTools() {
		return inner.getTools();
	}

	@Override
	public <C> C nw(Class<C> clazz_) {
		return inner.nw(clazz_);
	}

	@Override
	public ContainerServices getServices() {
		return inner.getServices();
	}

	@Override
	public void startDispatching() {
		inner.startDispatching();
	}

	@Override
	public <C> ObjectGeneratorForClass<C> getGenerator(Class<C> clazz_) {
		return inner.getGenerator(clazz_);
	}

	@Override
	public boolean isStarted() {
		return inner.isStarted();
	}

	@Override
	public void assertStarted() {
		inner.assertStarted();
	}

	@Override
	public void assertNotStarted() {
		inner.assertNotStarted();
	}

	@Override
	public String getName() {
		return inner.getName();
	}

	@Override
	public ContainerScope setName(String name) {
		return inner.setName(name);
	}

	@Override
	public List<ContainerScope> getChildContainerScopes() {
		return inner.getChildContainerScopes();
	}

	@Override
	public ContainerScope getParentContainerScope() {
		return inner.getParentContainerScope();
	}

	@Override
	public void setParentContainerScope(ContainerScope containerScope) {
		inner.setParentContainerScope(containerScope);
	}

	@Override
	public String getFullName() {
		return inner.getFullName();
	}

	@Override
	public ContainerScope getChild(String name_) {
		return inner.getChild(name_);
	}

	@Override
	public Logger getLog() {
		return inner.getLog();
	}

	@Override
	public long getStartedMs() {
		return inner.getStartedMs();
	}

	@Override
	public <C extends ContainerScope> void replaceChildContainerScope(C existing, C replacement) {
		inner.replaceChildContainerScope(existing, replacement);
	}

	@Override
	public void stopDispatching() {
		inner.stopDispatching();
	}

	@Override
	public void assertDispatchingStarted() {
		inner.assertDispatchingStarted();
	}

	@Override
	public void assertNotDispatchingStarted() {
		inner.assertNotDispatchingStarted();
	}

	@Override
	public boolean isDispatchingStarted() {
		return inner.isDispatchingStarted();
	}

	@Override
	public void init() {
		inner.init();
	}

	@Override
	public boolean isInit() {
		return inner.isInit();
	}

	@Override
	public void assertInit() {
		inner.assertInit();
	}

	@Override
	public void assertNotInit() {
		inner.assertNotInit();
	}

	@Override
	public void diagnose(Labeler labeler_) {
		inner.diagnose(labeler_);
	}

	@Override
	public <C extends ContainerScope> C addChildContainerScope(C child) {
		return inner.addChildContainerScope(child);
	}

	@Override
	public long getContainerScopeUid() {
		return inner.getContainerScopeUid();
	}

}
