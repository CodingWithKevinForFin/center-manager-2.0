package com.f1.ami.center.table.persist;

import java.util.Map;

import com.f1.ami.amicommon.AmiFactoryPlugin;

public interface AmiTablePersisterFactory extends AmiFactoryPlugin {

	/**
	 * @return a new, uninitialized instanceo of a table persister, based on the supplied options
	 */
	public AmiTablePersister newPersister(Map<String, Object> options);

}
