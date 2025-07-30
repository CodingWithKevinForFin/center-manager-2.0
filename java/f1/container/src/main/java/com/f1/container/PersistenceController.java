package com.f1.container;

import java.util.Collection;

import com.f1.base.Factory;
import com.f1.base.ValuedListener;
import com.f1.container.impl.PersistenceRoot;

public interface PersistenceController extends ContainerScope {

	public void onLocalStateCreated(State state, boolean isRecovering);

	public void commitState(State state);

	public Collection<Factory<Object, ValuedListener>> getValueListenerFactories();

	public void addValueListenerFactory(Factory<Object, ValuedListener> factory);

	public void addForReplication(PersistenceRoot root);

	public boolean getIsAutoCommit();
	public void setIsAutoCommit(boolean isAutoCommit);

}
