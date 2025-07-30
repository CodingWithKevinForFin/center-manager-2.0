package com.f1.ami.amicommon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.PropertyController;
import com.f1.utils.concurrent.HasherMap;

public class AmiChainedNamingServiceResolver implements AmiNamingServiceResolver {

	private ContainerTools tools;
	private PropertyController props;
	private List<AmiNamingServiceResolver> resolvers = new ArrayList<AmiNamingServiceResolver>();
	private Map<String, AmiNamingServiceResolver> resolversByName = new HasherMap<String, AmiNamingServiceResolver>();

	@Override
	public void init(ContainerTools tools, PropertyController props) {
		this.tools = tools;
		this.props = props;
	}

	public void addResolver(AmiNamingServiceResolver res) {
		CH.putOrThrow(this.resolversByName, res.getPluginId(), res);
		this.resolvers.add(res);
	}

	@Override
	public AmiServiceLocator resolve(AmiServiceLocator url) {
		for (int i = 0; i < resolvers.size(); i++) {
			try {
				if (resolvers.get(i).canResolve(url)) {
					AmiServiceLocator r = resolvers.get(i).resolve(url);
					if (r != null)
						return r;
				}
			} catch (Exception e) {
				throw new RuntimeException("Name Resolver '" + resolvers.get(i).getPluginId() + "' failed", e);
			}
		}
		return url;
	}
	@Override
	public String getPluginId() {
		return "Chained Resolver";
	}

	@Override
	public boolean canResolve(AmiServiceLocator locator) {
		for (int i = 0; i < resolvers.size(); i++) {
			try {
				if (resolvers.get(i).canResolve(locator))
					return true;
			} catch (Exception e) {
				throw new RuntimeException("Name Resolver '" + resolvers.get(i).getPluginId() + "' failed", e);
			}
		}
		return false;
	}

}
