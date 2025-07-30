package com.f1.ami.web;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiScmPlugin;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.customobjects.AmiScriptClassPluginWrapper;
import com.f1.ami.web.datafilter.AmiWebDataFilterPlugin;
import com.f1.ami.web.userpref.AmiWebUserPreferencesPlugin;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;

public class AmiWebPluginHelper {
	private static final Logger log = LH.get();

	public static void initAmiScriptPlugins(ContainerTools tools, Map<String, AmiScriptClassPluginWrapper> amiTriggerPlugins) {
		String classes = tools.getOptional(AmiWebProperties.PROPERTY_AMI_WEB_AMISCRIPT_CUSTOM_CLASSES);
		if (SH.is(classes)) {
			try {
				for (String clazzName : SH.split(',', classes)) {
					clazzName = SH.trim(clazzName);
					Class<?> clazz;
					try {
						clazz = (Class<?>) Class.forName(clazzName);
					} catch (ClassNotFoundException e) {
						throw new RuntimeException("Error Processing property: " + AmiWebProperties.PROPERTY_AMI_WEB_AMISCRIPT_CUSTOM_CLASSES + "=" + classes, e);
					}
					final AmiScriptClassPluginWrapper wrapper = new AmiScriptClassPluginWrapper(tools, clazz);
					CH.putOrThrow(amiTriggerPlugins, wrapper.getName(), wrapper, "Ami Script Custom Class");
					LH.info(log, "Added Ami Custom class: " + wrapper.getName() + " => " + clazz.getName());
				}
			} catch (Exception e) {
				throw new RuntimeException("Error Processing property: " + AmiWebProperties.PROPERTY_AMI_WEB_AMISCRIPT_CUSTOM_CLASSES + "=" + classes, e);
			}
		}
	}

	public static void initAmiPlugins(ContainerTools tools, Map<String, AmiWebPanelPlugin> panelPluginsById) {
		String classNames = tools.getOptional(AmiWebProperties.PROPERTY_AMI_WEB_PANELS);
		Map<String, AmiWebPanelPlugin> panelPluginsDisplayName = new HashMap<String, AmiWebPanelPlugin>();
		if (SH.is(classNames)) {
			try {
				for (String id : SH.split(',', classNames)) {
					StringBuilder errorSink = new StringBuilder();
					id = SH.trim(id);
					PropertyController prop = tools.getSubPropertyController(AmiWebProperties.PREFIX_AMI_WEB_PANEL + id + '.');
					String clazz = prop.getRequired("class");
					AmiWebPanelPlugin plugin = AmiUtils.loadPlugin(clazz, "Web Panel Plugin", tools, prop, AmiWebPanelPlugin.class, errorSink);
					if (plugin == null)
						throw new RuntimeException("Error loading " + clazz + ": " + errorSink.toString());
					CH.putOrThrow(panelPluginsDisplayName, plugin.getDisplayName(), plugin, "Web Panel Plugin Panel Type Id");
					CH.putOrThrow(panelPluginsById, plugin.getPluginId(), plugin, "Web Panel Plugin Panel Type Id");
				}
			} catch (Exception e) {
				throw new RuntimeException("Error Processing property: " + AmiWebProperties.PROPERTY_AMI_WEB_PANELS + "=" + classNames, e);
			}
		}

	}

	public static AmiWebDataFilterPlugin initAmiDataFilterPlugin(ContainerTools tools) {
		String clazz = tools.getOptional(AmiWebProperties.PROPERTY_AMI_WEB_DATA_FILTER_PLUGIN_CLASS);
		if (SH.is(clazz)) {
			StringBuilder errorSink = new StringBuilder();
			try {
				AmiWebDataFilterPlugin plugin = AmiUtils.loadPlugin(clazz, "Data Filter Plugin", tools, tools, AmiWebDataFilterPlugin.class, errorSink);
				if (plugin == null)
					throw new RuntimeException("Error loading " + clazz + ": " + errorSink.toString());
				return plugin;
			} catch (Exception e) {
				throw new RuntimeException("Error Processing property: " + AmiWebProperties.PROPERTY_AMI_WEB_DATA_FILTER_PLUGIN_CLASS + "=" + clazz, e);
			}
		}
		return null;
	}
	public static AmiWebUserPreferencesPlugin initAmiUserPreferencesStoragePlugin(ContainerTools tools) {
		String clazz = tools.getOptional(AmiWebProperties.PROPERTY_AMI_WEB_USER_PREFERENCES_PLUGIN_CLASS);
		if (SH.is(clazz)) {
			StringBuilder errorSink = new StringBuilder();
			try {
				AmiWebUserPreferencesPlugin plugin = AmiUtils.loadPlugin(clazz, "User Preferences Plugin", tools, tools, AmiWebUserPreferencesPlugin.class, errorSink);
				if (plugin == null)
					throw new RuntimeException("Error loading " + clazz + ": " + errorSink.toString());
				return plugin;
			} catch (Exception e) {
				throw new RuntimeException("Error Processing property: " + AmiWebProperties.PROPERTY_AMI_WEB_USER_PREFERENCES_PLUGIN_CLASS + "=" + clazz, e);
			}
		}
		return null;
	}

	public static void initScmPlugins(ContainerTools tools, Map<String, AmiScmPlugin> sink) {
		String classNames = tools.getOptional(AmiWebProperties.PROPERTY_AMI_SCM_PLUGINS);
		if (SH.is(classNames)) {
			try {
				for (String clazz : SH.split(',', classNames)) {
					StringBuilder errorSink = new StringBuilder();
					clazz = SH.trim(clazz);
					AmiScmPlugin plugin = AmiUtils.loadPlugin(clazz, "Scm Plugin", tools, tools, AmiScmPlugin.class, errorSink);
					if (plugin == null)
						throw new RuntimeException("Error loading " + clazz + ": " + errorSink.toString());
					CH.putOrThrow(sink, plugin.getPluginId(), plugin, "Scm Plugin Class Id");
				}
			} catch (Exception e) {
				throw new RuntimeException("Error Processing property: " + AmiWebProperties.PROPERTY_AMI_SCM_PLUGINS + "=" + classNames, e);
			}
		}

	}

	public static void initGuiServicePlugins(ContainerTools tools, Map<String, AmiWebGuiServicePlugin> sink) {
		String classNames = tools.getOptional(AmiWebProperties.PROPERTY_AMI_GUI_SERVICE_PLUGINS);
		if (SH.is(classNames)) {
			try {
				for (String clazz : SH.split(',', classNames)) {
					StringBuilder errorSink = new StringBuilder();
					clazz = SH.trim(clazz);
					AmiWebGuiServicePlugin plugin = AmiUtils.loadPlugin(clazz, "Gui Plugin", tools, tools, AmiWebGuiServicePlugin.class, errorSink);
					if (plugin == null)
						throw new RuntimeException("Error loading " + clazz + ": " + errorSink.toString());
					CH.putOrThrow(sink, plugin.getPluginId(), plugin, "Gui Service Plugin Class Id");
				}
			} catch (Exception e) {
				throw new RuntimeException("Error Processing property: " + AmiWebProperties.PROPERTY_AMI_GUI_SERVICE_PLUGINS + "=" + classNames, e);
			}
		}

	}
	public static void initRealtimeProcessorsPlugin(ContainerTools tools, Map<String, AmiWebRealtimeProcessorPlugin> sink) {
		String classNames = tools.getOptional(AmiWebProperties.PROPERTY_AMI_REALTIME_PROCESSOR_PLUGIN);
		if (SH.is(classNames)) {
			try {
				for (String clazz : SH.split(',', classNames)) {
					StringBuilder errorSink = new StringBuilder();
					clazz = SH.trim(clazz);
					AmiWebRealtimeProcessorPlugin plugin = AmiUtils.loadPlugin(clazz, "Realtime Processor Id", tools, tools, AmiWebRealtimeProcessorPlugin.class, errorSink);
					if (plugin == null)
						throw new RuntimeException("Error loading " + clazz + ": " + errorSink.toString());
					CH.putOrThrow(sink, plugin.getPluginId(), plugin, "Realtime Processor Id");
				}
			} catch (Exception e) {
				throw new RuntimeException("Error Processing property: " + AmiWebProperties.PROPERTY_AMI_REALTIME_PROCESSOR_PLUGIN + "=" + classNames, e);
			}
		}

	}
}
