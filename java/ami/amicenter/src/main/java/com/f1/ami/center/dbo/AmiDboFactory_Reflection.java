package com.f1.ami.center.dbo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.ami.amicommon.customobjects.AmiScriptClassPluginWrapper;
import com.f1.base.Lockable;
import com.f1.base.LockedException;
import com.f1.container.ContainerTools;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.structs.table.derived.DerivedCellMemberMethod;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiDboFactory_Reflection implements AmiDboFactory, Lockable {

	private Class clazz;
	private String pluginId;
	private AmiScriptClassPluginWrapper wrapper;
	private ArrayList<ParamsDefinition> methodDefinitions;
	final private List<ParamsDefinition> callbackDefinitions = new ArrayList<ParamsDefinition>();
	final private List<AmiFactoryOption> allowedOptions = new ArrayList<AmiFactoryOption>();
	private boolean isLocked;
	private Constructor constructor;

	public AmiDboFactory_Reflection(Class clazz) {
		if (!AmiDbo.class.isAssignableFrom(clazz))
			throw new RuntimeException("Class must implement AmiDbo interface: " + clazz.getName());
		this.clazz = clazz;
		this.wrapper = new AmiScriptClassPluginWrapper(null, this.clazz);
		for (Constructor i : wrapper.getClazz().getConstructors())
			if (i.getParameterCount() == 0 && (i.getModifiers() & (Modifier.PRIVATE | Modifier.PROTECTED)) == 0)
				this.constructor = i;
		this.pluginId = this.wrapper.getName();
	}

	@Override
	public Collection<AmiFactoryOption> getAllowedOptions() {
		return this.allowedOptions;
	}

	protected void addOption(AmiFactoryOption option) {
		LockedException.assertNotLocked(this);
		this.allowedOptions.add(option);
	}
	protected void addCallback(ParamsDefinition pd) {
		LockedException.assertNotLocked(this);
		this.addOption(new AmiFactoryOption("callback_" + pd.getMethodName(), String.class, false, pd.toString()));
		this.callbackDefinitions.add(pd);
	}

	@Override
	public void init(ContainerTools tools, PropertyController props) {
		LockedException.assertNotLocked(this);
		lock();
	}

	@Override
	public String getPluginId() {
		return this.pluginId;
	}

	@Override
	public AmiDbo newDbo() {
		LockedException.assertLocked(this);
		if (this.constructor == null)
			throw new RuntimeException("Plugin Factory must override newDbo() or plugin must have default constructor : " + this.wrapper.getClazz().getName());
		try {
			return (AmiDbo) this.constructor.newInstance(OH.EMPTY_OBJECT_ARRAY);
		} catch (Exception e) {
			throw new RuntimeException("Plugin constructor throw Exception: " + this.wrapper.getClazz().getName(), e);
		}
	}

	@Override
	public List<ParamsDefinition> getCallbackDefinitions() {
		return this.callbackDefinitions;
	}

	@Override
	public String getDboClassName() {
		return this.wrapper.getName();
	}

	@Override
	public void lock() {
		this.isLocked = true;
	}

	@Override
	public boolean isLocked() {
		return isLocked;
	}

	@Override
	public Class<?> getDboClassType() {
		return this.clazz;
	}

	@Override
	public List<? extends DerivedCellMemberMethod> getMethods() {
		return this.wrapper.getMethods();
	}
	public void addOption(String string) {
		this.addOption(new AmiFactoryOption(string, String.class, false));
	}
	public void addOptionRequired(String string) {
		this.addOption(new AmiFactoryOption(string, String.class, true));
	}
	public void addOption(String string, Class type) {
		this.addOption(new AmiFactoryOption(string, type, false));
	}
	public void addOptionRequired(String string, Class type) {
		this.addOption(new AmiFactoryOption(string, type, true));
	}
	public void addCallback(String name, Class<?> returnType, String[] argNames, Class<?> argTypes[]) {
		addCallback(new ParamsDefinition(name, returnType, argNames, argTypes, false, (byte) 0));
	}

}
