package com.f1.fix.oms.plugin;

import com.f1.container.Container;
import com.f1.fix.oms.OmsPluginManager;
import com.f1.utils.PropertyController;

public interface OmsPlugin {

	void onStartup(Container container);

	void init(PropertyController allProperties, PropertyController propertiesForThisPlugin, OmsPluginManager omsPluginManager);
}
