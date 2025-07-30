package com.f1.container.impl;

import com.f1.utils.PropertyController;

public class SimpleContainerTools extends BasicContainerTools {

	final private PropertyController propertiesController;

	public SimpleContainerTools(PropertyController propertiesController) {
		this.propertiesController = propertiesController;
	}

	@Override
	public PropertyController getPropertiesController() {
		return this.propertiesController;
	}

}
