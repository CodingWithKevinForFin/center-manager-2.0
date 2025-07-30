/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import com.f1.base.Action;
import com.f1.base.Caster;
import com.f1.base.DateNanos;
import com.f1.container.ContainerTools;
import com.f1.container.InputPort;
import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.ResultActionFuture;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.utils.Property;
import com.f1.utils.PropertyController;

public class BasicContainerTools extends AbstractContainerScope implements ContainerTools {

	@Override
	public <A extends Action> void dispatch(InputPort<? extends A> inputPort, A action) {
		getContainer().getDispatchController().dispatch(null, inputPort.getProcessor(), action, null, null);
	}

	@Override
	public <A extends Action> void dispatch(InputPort<? extends A> inputPort, A action, Object partitionId) {
		getContainer().getDispatchController().dispatch(null, inputPort.getProcessor(), action, partitionId, null);
	}

	@Override
	public <A extends Action> ResultActionFuture request(InputPort<? extends A> inputPort, A a) {
		return request(inputPort, a, (Object) null);
	}

	@Override
	public <A extends Action> void request(InputPort<? extends A> inputPort, A a, OutputPort<ResultMessage<?>> resultPort) {
		request(inputPort, a, null, resultPort);
	}

	@Override
	public <A extends Action> ResultActionFuture request(InputPort<? extends A> inputPort, A a, Object partitionId) {
		RequestMessage<A> req = makeRequest(a);
		req.setFuture(getContainer().getResultActionFutureController().createFuture(this));
		dispatch(inputPort, req, partitionId);
		return req.getFuture();

	}

	@Override
	public <A extends Action> void request(InputPort<? extends A> inputPort, A a, Object partitionId, OutputPort<ResultMessage<?>> resultPort) {
		RequestMessage<A> req = makeRequest(a);
		req.setResultPort(resultPort);
		dispatch(inputPort, req, partitionId);

	}

	protected <A extends Action> RequestMessage<A> makeRequest(A action) {
		RequestMessage<A> r = nw(RequestMessage.class);
		r.setAction(action);
		return r;
	}

	@Override
	public long getNow() {
		return getServices().getClock().getNow();
	}

	@Override
	public long getNowNano() {
		return getServices().getClock().getNowNano();
	}

	@Override
	public <C> C getOptional(String name, C nonNullDefault) {
		return getPropertiesController().getOptional(name, nonNullDefault);
	}

	public PropertyController getPropertiesController() {
		return getServices().getPropertyController();
	}

	@Override
	public <C> C getOptional(String name, Class<C> returnType) {
		return getPropertiesController().getOptional(name, returnType);
	}
	@Override
	public <C> C getOptional(String name, Caster<C> caster) {
		return getPropertiesController().getOptional(name, caster);
	}

	@Override
	public <C> C getRequired(String name, Class<C> returnType) {
		return getPropertiesController().getRequired(name, returnType);
	}
	@Override
	public <C> C getRequired(String name, Caster<C> caster) {
		return getPropertiesController().getRequired(name, caster);
	}

	@Override
	public Properties getProperties() {
		return getPropertiesController().getProperties();
	}

	@Override
	public <C> C getOptionalEnum(String name, C... acceptableValues) {
		return getPropertiesController().getOptionalEnum(name, acceptableValues);
	}

	@Override
	public <C> C getRequiredEnum(String name, C... acceptableValues) {
		return getPropertiesController().getRequiredEnum(name, acceptableValues);
	}

	@Override
	public List<Property> getPropertySources(String name) {
		return getPropertiesController().getPropertySources(name);
	}

	@Override
	public String getRequired(String name) {
		return getPropertiesController().getRequired(name);
	}

	@Override
	public String getOptional(String name) {
		return getPropertiesController().getOptional(name);
	}

	@Override
	public Set<String> getKeys() {
		return getPropertiesController().getKeys();
	}

	@Override
	public String formatBundledText(String bundleTextKey, Object[] arguments) {
		return getServices().getLocaleFormatter().getBundledTextFormatter().formatBundledText(bundleTextKey, arguments);
	}

	@Override
	public String formatBundledTextFromMap(String bundleTextKey, Map<String, Object> arguments) {
		return getServices().getLocaleFormatter().getBundledTextFormatter().formatBundledTextFromMap(bundleTextKey, arguments, 0);
	}

	@Override
	public String formatBundledTextFromMap(String bundleTextKey, Map<String, Object> arguments, int options) {
		return getServices().getLocaleFormatter().getBundledTextFormatter().formatBundledTextFromMap(bundleTextKey, arguments, options);
	}

	@Override
	public List<String> getAllSources() {
		return getPropertiesController().getAllSources();
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
	public PropertyController getSubPropertyController(String namespace) {
		return getPropertiesController().getSubPropertyController(namespace);
	}

	@Override
	public String applyProperties(String text) {
		return getPropertiesController().applyProperties(text);
	}

	@Override
	public String getUidString(String namespace) {
		return getServices().getTicketGenerator(namespace).createNextId();
	}

	@Override
	public long getUidLong(String namespace) {
		return getServices().getUidGenerator(namespace).createNextId();
	}

	@Override
	public ThreadScope getThreadScope() {
		return (ThreadScope) Thread.currentThread();
	}

	@Override
	public Property getProperty(String key) {
		return getPropertiesController().getProperty(key);
	}

	@Override
	public String generateErrorTicket() {
		StringBuilder sb = new StringBuilder(9);
		sb.append("FF-");
		Random r = new Random();
		for (int i = 0; i < 3; i++)
			sb.append((char) ('0' + r.nextInt(10)));
		for (int i = 0; i < 3; i++)
			sb.append((char) ('A' + r.nextInt(26)));
		return sb.toString();
	}

	@Override
	public Map<String, Object> getDefaultDeclaredProperties() {
		return getPropertiesController().getDefaultDeclaredProperties();
	}

	@Override
	public String toProperiesManifest() {
		return getPropertiesController().toProperiesManifest();
	}

	@Override
	public <C> C getOptionalEnum(String name, Map<String, C> acceptableValues, C dflt) {
		return getPropertiesController().getOptionalEnum(name, acceptableValues, dflt);
	}

}
