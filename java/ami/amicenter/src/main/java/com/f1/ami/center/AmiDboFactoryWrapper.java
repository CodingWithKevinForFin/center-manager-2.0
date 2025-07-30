package com.f1.ami.center;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.ami.center.dbo.AmiDbo;
import com.f1.ami.center.dbo.AmiDboFactory;
import com.f1.ami.center.dbo.AmiDboMethodWrapper;
import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;
import com.f1.utils.structs.table.derived.DerivedCellMemberMethod;
import com.f1.utils.structs.table.derived.ParamsDefinition;

final public class AmiDboFactoryWrapper implements AmiDboFactory {

	final private AmiDboFactory inner;
	final private Collection<AmiFactoryOption> allowedOptions;
	final private String pluginId;
	final private Class<?> dboClassType;
	final private String dboClassName;
	final private List<AmiDboMethodWrapper> methods;
	final private List<ParamsDefinition> callbackDefinitions;

	public AmiDboFactoryWrapper(AmiDboFactory inner) {
		this.inner = inner;
		this.pluginId = inner.getPluginId();
		this.allowedOptions = new ArrayList<AmiFactoryOption>(inner.getAllowedOptions());
		this.dboClassType = inner.getDboClassType();
		this.dboClassName = inner.getDboClassName();
		final List<? extends DerivedCellMemberMethod> t = inner.getMethods();
		this.methods = new ArrayList<AmiDboMethodWrapper>(t.size());
		for (int n = 0; n < t.size(); n++)
			this.methods.add(new AmiDboMethodWrapper(t.get(n), n));
		this.callbackDefinitions = new ArrayList<ParamsDefinition>(inner.getCallbackDefinitions());
	}

	@Override
	public Collection<AmiFactoryOption> getAllowedOptions() {
		return allowedOptions;
	}

	@Override
	public void init(ContainerTools tools, PropertyController props) {
		inner.init(tools, props);
	}

	@Override
	public String getPluginId() {
		return this.pluginId;
	}

	@Override
	public AmiDbo newDbo() {
		return inner.newDbo();
	}

	@Override
	public List<ParamsDefinition> getCallbackDefinitions() {
		return this.callbackDefinitions;
	}

	@Override
	public String getDboClassName() {
		return this.dboClassName;
	}

	@Override
	public Class<?> getDboClassType() {
		return this.dboClassType;
	}

	@Override
	public List<AmiDboMethodWrapper> getMethods() {
		return this.methods;
	}

	public AmiDboFactory getInner() {
		return this.inner;
	}

}
