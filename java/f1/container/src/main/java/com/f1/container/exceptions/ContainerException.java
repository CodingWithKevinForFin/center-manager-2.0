/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.exceptions;

import com.f1.base.Action;
import com.f1.container.ContainerScope;
import com.f1.container.Partition;
import com.f1.container.Port;
import com.f1.container.Processor;
import com.f1.container.State;
import com.f1.utils.DetailedException;
import com.f1.utils.Labeler;

public class ContainerException extends DetailedException {
	private Action action;

	private Processor targetProcessor;
	private State targetState;
	private Object targetPartitionId;
	private Class targetStateType;
	private Partition targetPartition;
	private Port sourcePort;

	private Processor sourceProcessor;
	private State sourceState;
	private Object sourcePartitionId;
	private Class sourceStateType;
	private Partition sourcePartition;

	private ContainerScope containerScope;
	private ContainerScope targetContainerScope;
	private ContainerScope sourceContainerScope;

	public ContainerException() {
		super();
	}

	public ContainerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ContainerException(String message) {
		super(message);
	}

	public ContainerException(Throwable cause) {
		super(cause);
	}

	public ContainerException(ContainerScope cs, String message) {
		this(message);
		setContainerScope(cs);
	}

	public ContainerException(ContainerScope cs, String message, Throwable cause) {
		this(message, cause);
		setContainerScope(cs);
	}

	public ContainerException setTargetProcessor(Processor targetProcessor) {
		this.targetProcessor = targetProcessor;
		super.set("targetProcessor", targetProcessor);
		return this;
	}

	public Processor getTargetProcessor() {
		return targetProcessor;
	}

	public ContainerException setAction(Action action) {
		this.action = action;
		super.set("action", action);
		return this;
	}

	public Action getAction() {
		return action;
	}

	public ContainerException setTargetState(State targetState) {
		this.targetState = targetState;
		super.set("targetState", targetState);
		return this;
	}

	public State getTargetState() {
		return targetState;
	}

	public ContainerException setTargetStateType(Class targetStateType) {
		this.targetStateType = targetStateType;
		super.set("targetStateType", targetStateType);
		return this;
	}

	public Class getTargetStateType() {
		return targetStateType;
	}

	public Object getTargetPartitionId() {
		return targetPartitionId;
	}

	public ContainerException setTargetPartitionId(Object targetPartitionId) {
		this.targetPartitionId = targetPartitionId;
		super.set("targetPartitionId", targetPartitionId);
		return this;
	}

	public Partition getTargetPartition() {
		return targetPartition;
	}

	public ContainerException setTargetPartition(Partition targetPartition) {
		this.targetPartition = targetPartition;
		super.set("targetPartition", targetPartition);
		return this;
	}

	public Processor getSourceProcessor() {
		return sourceProcessor;
	}

	public ContainerException setSourceProcessor(Processor sourceProcessor) {
		this.sourceProcessor = sourceProcessor;
		super.set("sourceProcessor", sourceProcessor);
		return this;
	}

	public State getSourceState() {
		return sourceState;
	}

	public ContainerException setSourceState(State sourceState) {
		this.sourceState = sourceState;
		super.set("sourceState", sourceState);
		return this;
	}

	public Object getSourcePartitionId() {
		return sourcePartitionId;
	}

	public ContainerException setSourcePartitionId(Object sourcePartitionId) {
		this.sourcePartitionId = sourcePartitionId;
		super.set("sourcePartitionId", sourcePartitionId);
		return this;
	}

	public Class getSourceStateType() {
		return sourceStateType;
	}

	public ContainerException setSourceStateType(Class sourceStateType) {
		this.sourceStateType = sourceStateType;
		super.set("sourceStateType", sourceStateType);
		return this;
	}

	public Partition getSourcePartition() {
		return sourcePartition;
	}

	public ContainerException setSourcePartition(Partition sourcePartition) {
		this.sourcePartition = sourcePartition;
		super.set("sourcePartition", sourcePartition);
		return this;
	}

	public ContainerException setContainerScope(ContainerScope containerScope) {
		this.containerScope = containerScope;
		super.set("containerScope", containerScope);
		return this;
	}

	public ContainerScope getContainerScope() {
		return containerScope;
	}

	public ContainerException setTargetContainerScope(ContainerScope containerScope) {
		this.targetContainerScope = containerScope;
		super.set("targetContainerScope", targetContainerScope);
		return this;
	}

	public ContainerScope getTargetContainerScope() {
		return targetContainerScope;
	}

	public ContainerException setSourceContainerScope(ContainerScope sourceContainerScope) {
		this.sourceContainerScope = sourceContainerScope;
		super.set("sourceContainerScope", sourceContainerScope);
		return this;
	}

	public ContainerScope getSourceContainerScope() {
		return sourceContainerScope;
	}

	public ContainerException setSourcePort(Port sourcePort) {
		this.sourcePort = sourcePort;
		super.set("sourcePort", sourcePort);
		return this;
	}

	public Port getSourcePort() {
		return sourcePort;
	}

	@Override
	protected void appendItem(String label, Object value, int maxDebugStringLength, int maxloop, Labeler items) {
		if (value instanceof ContainerScope)
			items.addItem(label + " Full Name", ((ContainerScope) value).getFullName());
		super.appendItem(label, value, maxDebugStringLength, maxloop, items);
	}
}
