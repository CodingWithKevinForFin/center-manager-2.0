package com.f1.ami.center.table.persist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;

public class AmiTablePersisterFactory_Fast implements AmiTablePersisterFactory {

	public static final String OPTION_PERSIST_DIR = "persist_dir";
	public static final String OPTION_PERSIST_COMPACT_PERCENT = "persist_compact_percent";
	public static final String OPTION_PERSIST_ENCRYPTER = "persist_encrypter";
	public static final String NAME = "FAST";
	private ContainerTools tools;

	private List<AmiFactoryOption> options = new ArrayList<AmiFactoryOption>();

	public AmiTablePersisterFactory_Fast() {
		options.add(new AmiFactoryOption(OPTION_PERSIST_DIR, String.class, false, ""));
		options.add(new AmiFactoryOption(OPTION_PERSIST_COMPACT_PERCENT, Double.class, false, ""));
		options.add(new AmiFactoryOption(OPTION_PERSIST_ENCRYPTER, String.class, false, ""));
	}
	@Override
	public void init(ContainerTools tools, PropertyController props) {
		this.tools = tools;
	}

	@Override
	public AmiTablePersister newPersister(Map<String, Object> options) {
		return new AmiTablePersister_Fast(this, options);
	}

	@Override
	public String getPluginId() {
		return NAME;
	}

	@Override
	public Collection<AmiFactoryOption> getAllowedOptions() {
		return options;
	}

}
