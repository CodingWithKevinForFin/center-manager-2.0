package com.f1.container.impl;

import com.f1.container.Container;
import com.f1.container.ContainerListener;

public abstract class AbstractContainerListener implements ContainerListener {

	@Override
	public void onPreStart(Container container_) {
	}

	@Override
	public void onPostStart(Container container_) {
	}

	@Override
	public void onPreStop(Container container_) {
	}

	@Override
	public void onPostStop(Container container_) {
	}

	@Override
	public void onPreStartDispatching(Container container_) {
	}

	@Override
	public void onPostStartDispatching(Container container_) {
	}

	@Override
	public void onPreStopDispatching(Container container_) {
	}

	@Override
	public void onPostStopDispatching(Container container_) {
	}

}
