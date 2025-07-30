package com.f1.ami.center.dbo;

import java.util.Collection;
import java.util.List;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.ami.amicommon.AmiFactoryPlugin;
import com.f1.utils.structs.table.derived.DerivedCellMemberMethod;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public interface AmiDboFactory<T extends AmiDbo> extends AmiFactoryPlugin {

	/**
	 * @return should return a new (uninitialized) AmiDbo. This will be invoked by: CREATE DBO ... OFTYPE DboClassName
	 */
	public T newDbo();

	/**
	 * @return Class name of DBOs created by this factory, as returned by calling the member method getClassName() and also seen in SHOW DBOS, SHOW VARS, etc.
	 */
	public String getDboClassName();

	/**
	 * @return the class type of AmiDbos returned by newDbo().
	 */
	public Class<T> getDboClassType();

	/**
	 * @return callbacks to be supported by this DBO
	 */
	public List<ParamsDefinition> getCallbackDefinitions();

	/**
	 * @return Callbacks to be supported by this class
	 */
	public List<? extends DerivedCellMemberMethod> getMethods();

	/**
	 * @return a list of allowed options for this plugin. Those keys that can be supplied in the USE ... clause. Note that all callbacks should be included.
	 */
	public Collection<AmiFactoryOption> getAllowedOptions();
}
