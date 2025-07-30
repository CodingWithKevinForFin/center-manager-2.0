package com.f1.container.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.f1.base.Action;
import com.f1.base.Caster;
import com.f1.base.DateNanos;
import com.f1.container.ContainerTools;
import com.f1.container.InputPort;
import com.f1.container.OutputPort;
import com.f1.container.ResultActionFuture;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.utils.EH;
import com.f1.utils.Property;
import com.f1.utils.PropertyController;

public class ContainerToolsWrapper extends AbstractContainerScope implements ContainerTools {

	private final PropertyController props;

	public ContainerToolsWrapper(PropertyController pc) {
		this.props = pc;
	}

	public <C> C getOptional(String name, C nonNullDefault) {
		return props.getOptional(name, nonNullDefault);
	}

	public <C> C getOptional(String name, Class<C> returnType) {
		return props.getOptional(name, returnType);
	}

	public <C> C getOptional(String name, Caster<C> caster) {
		return props.getOptional(name, caster);
	}

	public <C> C getRequired(String name, Class<C> returnType) {
		return props.getRequired(name, returnType);
	}

	public <C> C getRequired(String name, Caster<C> caster) {
		return props.getRequired(name, caster);
	}

	public Properties getProperties() {
		return props.getProperties();
	}

	public <C> C getOptionalEnum(String name, C... acceptableValues) {
		return props.getOptionalEnum(name, acceptableValues);
	}

	public <C> C getOptionalEnum(String name, Map<String, C> acceptableValues, C dflt) {
		return props.getOptionalEnum(name, acceptableValues, dflt);
	}

	public <C> C getRequiredEnum(String name, C... acceptableValues) {
		return props.getRequiredEnum(name, acceptableValues);
	}

	public List<Property> getPropertySources(String name) {
		return props.getPropertySources(name);
	}

	public String getRequired(String string) {
		return props.getRequired(string);
	}

	public String getOptional(String string) {
		return props.getOptional(string);
	}

	public Set<String> getKeys() {
		return props.getKeys();
	}

	public List<String> getAllSources() {
		return props.getAllSources();
	}

	public PropertyController getSubPropertyController(String namespace) {
		return props.getSubPropertyController(namespace);
	}

	public String applyProperties(String text) {
		return props.applyProperties(text);
	}

	public Property getProperty(String key) {
		return props.getProperty(key);
	}

	public Map<String, Object> getDefaultDeclaredProperties() {
		return props.getDefaultDeclaredProperties();
	}

	public String toProperiesManifest() {
		return props.toProperiesManifest();
	}

	@Override
	public <A extends Action> void dispatch(InputPort<? extends A> inputPort, A action) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <A extends Action> void dispatch(InputPort<? extends A> inputPort, A action, Object partitionId) {
		throw new UnsupportedOperationException();

	}

	@Override
	public <A extends Action> ResultActionFuture request(InputPort<? extends A> outputPort, A a) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <A extends Action> void request(InputPort<? extends A> inputPort, A a, OutputPort<ResultMessage<?>> resultPort) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <A extends Action> ResultActionFuture request(InputPort<? extends A> inputPort, A a, Object partitionId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <A extends Action> void request(InputPort<? extends A> inputPort, A a, Object partitionId, OutputPort<ResultMessage<?>> resultPort) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getNow() {
		return EH.currentTimeMillis();
	}

	@Override
	public long getNowNano() {
		return EH.currentTimeNanos();
	}

	@Override
	public String formatBundledText(String bundleTextKey, Object... arguments) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String formatBundledTextFromMap(String bundleTextKey, Map<String, Object> arguments, int options) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String formatBundledTextFromMap(String bundleTextKey, Map<String, Object> arguments) {
		throw new UnsupportedOperationException();
	}

	@Override
	public DateNanos getNowNanoDate() {
		return new DateNanos(getNowNano());
	}

	@Override
	public Date getNowDate() {
		return new Date(getNow());
	}

	@Override
	public String getUidString(String namespace) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getUidLong(String namespace) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ThreadScope getThreadScope() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String generateErrorTicket() {
		throw new UnsupportedOperationException();
	}

}
