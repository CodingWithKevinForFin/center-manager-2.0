/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.tester.templates;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.Message;
import com.f1.base.ObjectGenerator;
import com.f1.tester.json.TestingExpression;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.RH;
import com.f1.utils.assist.RootAssister;
import com.f1.utils.string.JavaInvoker;

public class TemplateSession {

	private TemplateRepo repo;
	private Map<String, Object> variables;
	private List<String> imports;
	private ObjectGenerator generator;

	public TemplateSession(TemplateRepo repo) {
		this(repo, repo.getConverter().getIdeableGenerator());
	}

	public TemplateSession(TemplateRepo repo, ObjectGenerator generator) {
		this.repo = repo;
		this.generator = generator;
		this.variables = repo.newMap();
		this.variables.putAll(repo.getVariableInitialValues());
		for (Map.Entry<String, Object> e : this.variables.entrySet())
			e.setValue(RootAssister.INSTANCE.clone(e.getValue()));
		this.imports = new ArrayList<String>(repo.getImports());
	}

	public void putVariable(String name, Object value) {
		variables.put(name, value);
	}

	public Object getVariable(String name) {
		return variables.get(name);
	}

	public void addImport(String importPath) {
		imports.add(importPath);
	}

	public List<Message> getMessages(String mPath) {
		Object o = fillIn(repo.get(mPath), true);
		if (o instanceof Message)
			return CH.l((Message) o);
		return (List<Message>) o;
	}

	public List<Map> getMaps(String mPath) {
		Object o = fillIn(repo.get(mPath), false);
		if (o instanceof Map)
			return CH.l((Map) o);
		return (List<Map>) o;
	}

	public Object get(String mPath) {
		return fillIn(repo.get(mPath), false);
	}

	private Object fillInMap(Map<String, Object> map, boolean convertToMessages) {
		if (convertToMessages && map.containsKey("_")) {
			if (generator == null)
				throw new NullPointerException("generator not set");
			Message r = (Message) generator.nw(RH.getClass((String) map.get("_")));
			for (Map.Entry<String, Object> e : map.entrySet()) {
				if ("_".equals(e.getKey()))
					continue;
				r.put(e.getKey(), OH.cast(fillIn(e.getValue(), convertToMessages), r.askSchema().askClass(e.getKey())));
			}
			return r;
		} else {
			Map<String, Object> r = new LinkedHashMap<String, Object>();
			for (Map.Entry<String, Object> e : map.entrySet())
				r.put(e.getKey(), fillIn(e.getValue(), convertToMessages));
			return r;
		}
	}

	private Object fillInList(List<Object> list, boolean convertToMessages) {
		List<Object> r = new ArrayList<Object>();
		for (Object o : list)
			r.add(fillIn(o, convertToMessages));
		return r;
	}

	private Object fillIn(Object v, boolean convertToMessages) {
		if (v instanceof TestingExpression)
			return fillInExpression((TestingExpression) v, convertToMessages);
		if (v instanceof Map)
			return fillInMap((Map<String, Object>) v, convertToMessages);
		if (v instanceof List)
			return fillInList((List<Object>) v, convertToMessages);
		return v;
	}

	private Object fillInExpression(TestingExpression v, boolean convertToMessages) {

		Object r = new JavaInvoker().evaluate(v.getNode(), new JavaInvoker.MapBackedObjectScope(variables, imports));
		if (r instanceof JavaInvoker.Package)
			throw new RuntimeException("Could not evaluate(likely due to missing variable): " + v.toString());
		return r;
	}

	public void setGenerator(ObjectGenerator generator) {
		this.generator = generator;
	}

	public ObjectGenerator getGenerator() {
		return generator;
	}

	public Object evaluate(String text) {
		return new JavaInvoker().evaluate(new com.f1.utils.string.JavaExpressionParser().parse(text), new JavaInvoker.MapBackedObjectScope(variables, imports));
	}

	public Set<String> getDeclaredVariables() {
		return this.variables.keySet();
	}

	public TemplateRepo getRepo() {
		return this.repo;
	}
}
