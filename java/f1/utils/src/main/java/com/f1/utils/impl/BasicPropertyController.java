/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import com.f1.base.Caster;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.CopyOnWriteHashMap;
import com.f1.utils.DetailedException;
import com.f1.utils.OH;
import com.f1.utils.PropertiesHelper;
import com.f1.utils.Property;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Object;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.MultiMap;

public class BasicPropertyController implements PropertyController {

	final private Properties inner;
	final private MultiMap<String, Property, List<Property>> propertySources;
	final private List<String> sources;
	final private String namespace;
	final private BasicPropertyController parent;
	private final Map<String, Object> optionalProperties;

	public BasicPropertyController(Properties inner) {
		this(inner, "", null);
	}
	public BasicPropertyController(Properties inner, String namespace, BasicPropertyController parent) {
		this.namespace = namespace;
		this.parent = parent;
		if (parent == null)
			optionalProperties = new CopyOnWriteHashMap<String, Object>();
		else
			optionalProperties = null;
		this.inner = inner;
		this.propertySources = new BasicMultiMap.List<String, Property>();
		for (Object key : inner.keySet()) {
			String k = key.toString();
			String value = inner.getProperty(k);
			propertySources.putMulti(k, new Property(k, value, Property.TYPE_COLLECTION));
		}
		HashSet<String> sourcesSet = new HashSet<String>();
		initSources(sourcesSet);
		this.sources = new ArrayList<String>(sourcesSet);
	}

	private void initSources(Set<String> sink) {
		for (Property p : propertySources.valuesMulti()) {
			String t = Property.formatType(p.getSourceType());
			if (SH.is(p.getSource()))
				t += ":" + p.getSource();
			sink.add(t);
		}
	}

	public BasicPropertyController(Properties inner, MultiMap<String, Property, List<Property>> propertySources) {
		this.namespace = "";
		this.parent = null;
		this.optionalProperties = new ConcurrentHashMap<String, Object>();
		this.inner = inner;
		this.propertySources = propertySources;
		HashSet<String> sourcesSet = new HashSet<String>();
		initSources(sourcesSet);
		this.sources = new ArrayList<String>(sourcesSet);
	}

	@Override
	public <C> C getOptional(String name, Class<C> returnType) {
		if (returnType == null)
			throw new NullPointerException("returnType");
		try {
			return OH.cast(inner.getProperty(name), returnType);
		} catch (Exception e) {
			throw initSource(new DetailedException("error getting optional property").set("property name", name).set("cast to type", returnType.getClass()), name);
		}
	}
	@Override
	public <C> C getOptional(String name, Caster<C> caster) {
		if (caster == null)
			throw new NullPointerException("caster");
		try {
			return caster.cast(inner.getProperty(name));
		} catch (Exception e) {
			throw initSource(new DetailedException("error getting optional property").set("property name", name).set("cast to type", caster.getCastToClass()), name);
		}
	}

	@Override
	public <C> C getOptional(String name, C nonNullDefault) {
		if (nonNullDefault == null)
			throw new NullPointerException("nonNullDefault");
		try {
			C r = (C) OH.getCaster(nonNullDefault.getClass()).cast(inner.getProperty(name));
			if (r != null || inner.containsKey(name))
				return r;
			onOptionalPropertyDefined(name, nonNullDefault);
			return nonNullDefault;
		} catch (Exception e) {
			throw initSource(
					new DetailedException("error getting optional property", e).set("property name", name).set("default value", nonNullDefault)
							.set("cast to type", nonNullDefault.getClass()), name);
		}
	}

	@Override
	public Properties getProperties() {
		return inner;
	}

	@Override
	public <C> C getRequired(String name, Class<C> returnType) {
		C r = null;
		try {
			r = OH.cast(inner.getProperty(name), returnType);
		} catch (Exception e) {
			throw initSource(new DetailedException("could not cast required property", e).set("property name", namespace + name).set("cast to", returnType.getName()), name)
					.setIfPresent("property sources", propertySources.get(name));
		}
		if (r == null)
			throw initSource(new DetailedException("required property missing").set("property name", namespace + name).set("cast to", returnType.getName()), name).setIfPresent(
					"sources", sources);
		return r;
	}
	@Override
	public <C> C getRequired(String name, Caster<C> caster) {
		C r = null;
		try {
			r = caster.cast(inner.getProperty(name));
		} catch (Exception e) {
			throw initSource(new DetailedException("could not cast required property", e).set("property name", namespace + name).set("cast to", caster.getCastToClass().getName()),
					name).setIfPresent("property sources", propertySources.get(name));
		}
		if (r == null)
			throw initSource(new DetailedException("required property missing").set("property name", namespace + name).set("cast to", caster.getCastToClass().getName()), name)
					.setIfPresent("sources", sources);
		return r;
	}

	@Override
	public <C> C getOptionalEnum(String name, Map<String, C> acceptableValues, C dflt) {
		if (acceptableValues.size() == 0)
			throw new IllegalArgumentException("must supply at least one acceptable value");
		String r = getOptional(name, Caster_String.INSTANCE);
		if (r == null) {
			onOptionalPropertyDefined(name, dflt);
			return dflt;
		} else if (!acceptableValues.containsKey(r)) {
			DetailedException e = new DetailedException("invalid value for property ").set("acceptable values", SH.join(',', acceptableValues.keySet())).set("supplied value", r)
					.set("property name", name);
			throw initSource(e, name);
		}
		return acceptableValues.get(r);
	}
	@Override
	public <C> C getOptionalEnum(String name, C... acceptableValues) {
		if (acceptableValues.length == 0)
			throw new IllegalArgumentException("must supply at least one acceptable value");
		Object r = getOptional(name, Caster_Object.INSTANCE);
		if (r == null) {
			r = acceptableValues[0];
			onOptionalPropertyDefined(name, r);
		} else if (AH.indexOf(r, acceptableValues) == -1) {
			DetailedException e = new DetailedException("invalid value for property ").set("acceptable values", SH.join(',', acceptableValues)).set("supplied value", r)
					.set("property name", name);
			throw initSource(e, name);
		}
		return (C) r;
	}

	private DetailedException initSource(DetailedException e, String name) {
		if (name != null && propertySources != null) {
			List<Property> list = propertySources.get(name);
			e.set("sources", list != null ? list.toArray() : OH.EMPTY_OBJECT_ARRAY);
		}
		return e;
	}

	@Override
	public <C> C getRequiredEnum(String name, C... acceptableValues) {
		if (acceptableValues.length == 0)
			throw new IllegalArgumentException("must supply at least one acceptable value");
		Object r = getOptional(name, Caster_Object.INSTANCE);
		if (AH.indexOf(r, acceptableValues) == -1) {
			DetailedException e = new DetailedException("invalid value for property ").set("acceptable values", SH.join(',', acceptableValues)).set("supplied value", r)
					.set("property name", name);
			if (propertySources != null)
				e.set("sources", CH.getOr((Map) propertySources, name, Collections.EMPTY_LIST));
			throw e;
		}
		return (C) r;
	}

	@Override
	public List<Property> getPropertySources(String name) {
		return CH.getOr(propertySources, name, CH.emptyList(Property.class));
	}

	@Override
	public String getRequired(String name) {
		return getRequired(name, Caster_String.INSTANCE);
	}

	@Override
	public String getOptional(String name) {
		return getOptional(name, Caster_String.INSTANCE);
	}

	@Override
	public Set<String> getKeys() {
		return (Set) inner.keySet();
	}

	@Override
	public List<String> getAllSources() {
		return sources;
	}

	@Override
	public PropertyController getSubPropertyController(String namespace) {
		Properties props = new Properties();
		for (String k : getKeys()) {
			if (k.startsWith(namespace)) {
				String k2 = SH.stripPrefix(k, namespace, true);
				props.put(k2, getRequired(k, Caster_Object.INSTANCE));
			}
		}
		return new BasicPropertyController(props, this.namespace + namespace, this);
	}

	@Override
	public String applyProperties(String text) {
		String r = new PropertiesHelper().resolveProperty(Collections.EMPTY_MAP, getProperties(), null, text, new Stack<String>(), new HashSet<String>());
		return r;
	}

	@Override
	public String toString() {
		return new TreeMap(this.inner).toString();
	}

	@Override
	public Property getProperty(String key) {
		return CH.lastOr(propertySources.get(key), null);
	}

	private void onOptionalPropertyDefined(String key, Object value) {
		try {
			if (this.parent != null) {
				BasicPropertyController p = this.parent;
				while (p.getParent() != null)
					p = p.getParent();
				p.onOptionalPropertyDefined(this.namespace + key, value);
			} else {
				Object existing = optionalProperties.get(key);
				if (OH.eq(value, existing))
					return;
				else if (existing instanceof Set)
					((Set<Object>) existing).add(value);
				else {
					synchronized (optionalProperties) {
						existing = optionalProperties.get(key);
						if (existing == null)
							optionalProperties.put(key, value);
						else if (OH.eq(value, existing))
							return;
						else if (existing instanceof Set) {
							final List<Object> l = ((List<Object>) existing);
							if (!l.contains(value))
								l.add(value);
						} else
							optionalProperties.put(key, new CopyOnWriteArraySet<Object>(CH.l(existing, value)));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
	public BasicPropertyController getParent() {
		return this.parent;
	}

	@Override
	public Map<String, Object> getDefaultDeclaredProperties() {
		if (parent != null)
			return parent.getDefaultDeclaredProperties();
		else
			return this.optionalProperties;
	}
	@Override
	public String toProperiesManifest() {
		StringBuilder sb = new StringBuilder();
		BasicMultiMap.List<String, Property> propsBySource = new BasicMultiMap.List<String, Property>();
		for (Property i : this.propertySources.valuesMulti()) {
			if (i.getSourceType() == Property.TYPE_FILE)
				propsBySource.putMulti(i.getSource(), i);
		}
		for (String source : CH.sort(propsBySource.keySet())) {
			sb.append("##################################").append(SH.NEWLINE);
			sb.append("## From File ").append(source).append(SH.NEWLINE);
			sb.append("##################################").append(SH.NEWLINE);
			if (!source.startsWith("code:")) {
				for (Property props : propsBySource.get(source)) {
					sb.append(props.getKey()).append('=').append(props.getValue()).append(SH.NEWLINE);//TODO: escape
				}
			}
			sb.append(SH.NEWLINE);
			sb.append(SH.NEWLINE);
		}

		sb.append("##################################").append(SH.NEWLINE);
		sb.append("## Default Declared").append(SH.NEWLINE);
		sb.append("##################################").append(SH.NEWLINE);
		for (Entry<String, Object> s : this.getDefaultDeclaredProperties().entrySet()) {
			sb.append(s.getKey()).append('=').append(s.getValue()).append(SH.NEWLINE);//TODO:escape
		}
		return sb.toString();
	}
}
