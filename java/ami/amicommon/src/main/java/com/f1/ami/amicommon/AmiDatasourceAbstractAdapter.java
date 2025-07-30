package com.f1.ami.amicommon;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.impl.CaseInsensitiveHasher;

public abstract class AmiDatasourceAbstractAdapter implements AmiDatasourceAdapter {

	protected ContainerTools tools;
	private AmiServiceLocator serviceName;
	private Map<String, String> options = new HasherMap<String, String>(CaseInsensitiveHasher.INSTANCE);

	@Override
	public void init(ContainerTools tools, AmiServiceLocator locator) throws AmiDatasourceException {
		this.tools = tools;
		this.serviceName = locator;
		if (locator.getOptions() != null)
			this.options = SH.splitToMap(options, ',', '=', '\\', locator.getOptions());

	}
	public static Map<String, String> splitOptionsToMap(String options) {
		Map<String, String> optionsMap = new HashMap<String, String>();
		optionsMap = SH.splitToMap(optionsMap, ',', '=', '\\', options);
		return optionsMap;
	}
	public <T> T getOption(String optionname, T deflt) {
		return CH.getOr((Class<T>) deflt.getClass(), this.options, optionname, deflt);
	}
	public <T> T getOptionNoThrow(String optionname, T deflt) {
		return CH.getOrNoThrow((Class<T>) deflt.getClass(), this.options, optionname, deflt);
	}
	public Set<String> getOptions() {
		return this.options.keySet();
	}

	@Override
	public AmiServiceLocator getServiceLocator() {
		return this.serviceName;
	}

	@Override
	public boolean cancelQuery() {
		return false;
	}

}
