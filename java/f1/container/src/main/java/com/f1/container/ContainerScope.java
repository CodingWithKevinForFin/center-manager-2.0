/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container;

import java.util.List;
import java.util.logging.Logger;

import com.f1.base.ObjectGenerator;
import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.exceptions.ContainerException;
import com.f1.container.impl.AbstractContainerScope;
import com.f1.container.impl.BasicContainer;
import com.f1.utils.Labeler;

/**
 * Represents an object which is the member of a container. Please note that the container itself is a container scoped object. All {@link ContainerScope} objects must have a
 * parent, aside from the {@link BasicContainer} which is the root. For {@link Processor}s and {@link Suite}s they can be added to the root suite of the container. Otherwise, you
 * may add as a service using the {@link ContainerServices#putService(String, Object)} <BR>
 * Relationships: The ContainerScopes (CS) within a container form a double linked tree structure, such that each * cs (asside from the container itself) has a parent, and in turn
 * each container scoped will have children, unless its a leaf node. While a paricular CS may be referenced by multiple CS instances, only one will be the true parent. It is the
 * implementers responsibility that life cycle calls are propagated to children, for example when setContainer() is called, be sure to call setContainer on all child container
 * scoped. In addition to expanding on the {@link StartStoppable} life cycle several convenience methods and a naming concept is added. The life cycle of a container is the
 * following:<BR>
 * 
 * <B> constructor(...) -(1)-> setContainer(...) -(2)-> init() -(3)-> start() -(4)-> startDispatching() -(5)-> stop() -(6)-> </B><BR>
 * Stage 1: Only constructor initialization has taken place<BR>
 * Stage 2: container is available<BR>
 * Stage 3: custom initialization is complete<BR>
 * Stage 4: The container has been registered, so convenience methods are accessible <BR>
 * Stage 5: This object should be 'locked' for modification (essentially immutable during this and the next phase).Note, that it is not truly modifiable because stoping the
 * container, unlocks all container scoped items.<BR>
 * Stage 6: After this call, any queued messages will begin flowing through the container. Stage 5: events are'nt flowing anymore. The object is now considered unlocked and can be
 * modified<BR>
 * 
 * <P>
 * See {@link Container} for details. See {@link AbstractContainerScope} for easy implementation<BR>
 * NOTE: All instances and methods extending this interface should be threadsafe during stages 4 & 5.
 */
public interface ContainerScope extends StartStoppable, ContainerUid {

	/**
	 * @return the container that this is a part of (null if not registered with a container yet)
	 */
	Container getContainer();

	/**
	 * register the container.. Should only be called once.
	 * 
	 * @param container
	 *            the container that this is a member of
	 */
	void setContainer(Container container);

	/**
	 * called by the framework once after {@link StartStoppable#start()} to indicate that messages will begin moving throughout the system
	 */
	void startDispatching();

	/**
	 * called by the framework once before {@link StartStoppable#stop()} to indicate that messages will cease moving throughout the system
	 */
	void stopDispatching();

	/**
	 * Names are useful to help identify objects of the same type from one another. By default the name should be the simple class name of the object.
	 * 
	 * @return the name of this object.
	 */
	String getName();

	/**
	 * override the default name. must be done prior to {@link StartStoppable#start()}
	 * 
	 * @param name
	 * @return this instance (for convenience)
	 */
	ContainerScope setName(String name);

	/**
	 * Convenience method for getting to the container's tools.
	 * 
	 * @return same as getContiner().getTools()
	 */
	ContainerTools getTools();

	/**
	 * Convenience method for calling {@link ObjectGenerator#nw(Class...)}. same as getServices().getObjectFactory().nw
	 * 
	 * @param <C>
	 *            type of class
	 * @param clazz
	 *            class to create
	 * @return new instance
	 */
	<C> C nw(Class<C> clazz);

	/**
	 * Convenience method for getting to the container's services.
	 * 
	 * @return same as getContiner().getServices()
	 */
	ContainerServices getServices();

	/**
	 * Convenience method for getting a class generator
	 * 
	 * @param <C>
	 *            type of class
	 * @param clazz
	 *            class that generator should be able to create
	 * @return a generator for creating that class
	 */
	<C> ObjectGeneratorForClass<C> getGenerator(Class<C> clazz);

	/**
	 * immutable list of all ContainerScope objects owned by this container. The list is ordered by the order in which children are added
	 * 
	 * @return list, if no children an empy list (never null)
	 */
	List<ContainerScope> getChildContainerScopes();

	/**
	 * Parent ContainerScope.
	 * 
	 * @return parent. Null if not set yet or if this is the root aka Container itself
	 */
	ContainerScope getParentContainerScope();

	/**
	 * registers the container scope that will be the parent of this container scope. Please note that each container scope must only have at most one parent.
	 * 
	 * @param containerScope
	 *            the parent of this container scope, please note this container should also be added as a child of the parent.
	 * @throws ContainerException
	 *             if a differnt {@link ContainerScope} has already been registered as this container scope's parent
	 */
	void setParentContainerScope(ContainerScope containerScope);

	/**
	 * the full name of this wireable using {@link ContainerConstants#NAME_SEPERATOR} as the seperator.
	 * 
	 * @return parent_name_or_blank + seperator + name;
	 */
	String getFullName();

	/**
	 * returns the child (or grandchild, etc) of this suite based on the name... Performance is questionable. in the case of colission, the first registed will be returned
	 * 
	 * @param name
	 *            of the child (to nest use forward slash example: getChild("mysuite/mysubsuite/myprocessor/inPort");
	 * @return child or null, if none with that name
	 * @throws ContainerException
	 *             if no such child exists
	 */
	ContainerScope getChild(String name);

	/**
	 * the log associated with this object
	 * 
	 * @return must not return null.
	 */
	Logger getLog();

	/**
	 * see {@link StartStoppable#start}. Should call {@link StartStoppable#start()} of all children {@link ContainerScope}
	 */
	@Override
	void start();

	/**
	 * see {@link StartStoppable#start}. Should call {@link StartStoppable#stop()} of all children {@link ContainerScope}
	 */
	@Override
	void stop();

	/**
	 * replaces a child in this scope. if the supplied existing child is null, then the replacement is simply added. if the existing value is not a child, an exception is thrown.
	 * Please note, this is different from removing and adding in that the position of the child in the list of children will remain intact
	 * 
	 * @param <C>
	 * @param existing
	 *            existing child
	 * @param replacement
	 *            replacement child
	 */
	<C extends ContainerScope> void replaceChildContainerScope(C existing, C replacement);

	void assertDispatchingStarted();

	void assertNotDispatchingStarted();

	boolean isDispatchingStarted();

	/**
	 * Called early on in the life cycle (but after the container has been set via {@link #setContainer(Container)}) to provide the implementer a chance to initiate any member
	 * variables that need the container or various related services. IMPORTANT: be sure to call super.init()!
	 */
	void init();

	/**
	 * @return true if {@link #init()} has already been called on this.
	 */
	boolean isInit();

	/**
	 * @throws ContainerException
	 *             if {@link #isInit()} would return false
	 */
	void assertInit() throws ContainerException;

	/**
	 * @throws ContainerException
	 *             if {@link #isInit()} would return true
	 */
	void assertNotInit() throws ContainerException;

	/**
	 * used to populate the labeler w/ useful diagnostic information
	 * 
	 * @param labeler
	 *            sink to populate with diagnostic info, such as interesting configuration details, etc. Should be thread safe
	 */
	void diagnose(Labeler labeler_);

	<C extends ContainerScope> C addChildContainerScope(C child);

}
