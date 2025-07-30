package com.f1.ami.center;

import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.customobjects.AmiScriptClassPluginWrapper;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;

public class AmiCenterPluginHelper {
	private static final Logger log = LH.get();

	public static void initAmiScriptPlugins(ContainerTools tools, Map<String, AmiScriptClassPluginWrapper> amiCenterPlugins) {
		String classes = tools.getOptional(AmiCenterProperties.PROPERTY_AMI_CENTER_AMISCRIPT_CUSTOM_CLASSES);
		if (SH.is(classes)) {
			try {
				for (String clazzName : SH.split(',', classes)) {
					clazzName = SH.trim(clazzName);
					Class<?> clazz;
					try {
						clazz = (Class<?>) Class.forName(clazzName);
					} catch (ClassNotFoundException e) {
						throw new RuntimeException("Error Processing property: " + AmiCenterProperties.PROPERTY_AMI_CENTER_AMISCRIPT_CUSTOM_CLASSES + "=" + classes, e);
					}
					final AmiScriptClassPluginWrapper wrapper = new AmiScriptClassPluginWrapper(tools, clazz);
					CH.putOrThrow(amiCenterPlugins, wrapper.getName(), wrapper, "Ami Script Custom Class");
					LH.info(log, "Added Ami Custom class: " + wrapper.getName() + " => " + clazz.getName());
				}
			} catch (Exception e) {
				throw new RuntimeException("Error Processing property: " + AmiCenterProperties.PROPERTY_AMI_CENTER_AMISCRIPT_CUSTOM_CLASSES + "=" + classes, e);
			}
		}
	}
}
