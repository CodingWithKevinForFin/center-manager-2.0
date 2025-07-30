package com.f1.ami.amicommon;

import java.util.logging.Logger;

import com.f1.container.Container;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;

public class AmiNamingServiceResolverHelper {
	private static final Logger log = LH.get();

	public static AmiNamingServiceResolver getService(Container container) {
		return (AmiNamingServiceResolver) container.getServices().getServiceNoThrow(AmiConsts.SERVICE_SERVICE_RESOLVER);
	}

	public static void setService(Container container, AmiChainedNamingServiceResolver serviceResolver) {
		container.getServices().putService(AmiConsts.SERVICE_SERVICE_RESOLVER, serviceResolver);
	}

	public static void loadServiceResolver(Container container, PropertyController props) {
		String names = props.getOptional(AmiCommonProperties.PROPERTY_AMI_SERVICE_RESOLVERS);

		AmiChainedNamingServiceResolver chainedResolver = new AmiChainedNamingServiceResolver();
		if (SH.is(names)) {
			for (String clazz : SH.split(',', names)) {
				StringBuilder errorSink = new StringBuilder();
				AmiNamingServiceResolver serviceResolver = AmiUtils.loadPlugin(clazz, "Naming Service Resolver", container.getTools(), props.getSubPropertyController(clazz + '.'),
						AmiNamingServiceResolver.class, errorSink);
				if (errorSink.length() > 0)
					throw new RuntimeException(errorSink.toString());
				chainedResolver.addResolver(serviceResolver);
			}
		}

		setService(container, chainedResolver);
	}
}
