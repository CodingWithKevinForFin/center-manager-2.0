package com.f1.ami.web.style;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.style.impl.AmiWebStyleOption;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.MapInMap;

//Manages all styles, one per user.
public class AmiWebStyleManager {
	public static final String LAYOUT_DEFAULT_ID = "LAYOUT_DEFAULT";
	public static final String LAYOUT_DEFAULT_LABEL = "Layout Default";
	public static final String FACTORY_DEFAULT_ID = "DEFAULT";
	public static final String FACTORY_DEFAULT_LABEL = "Classic";
	private static final Logger log = LH.get();

	private Map<String, AmiWebStyle> stylesById = new HashMap<String, AmiWebStyle>();
	private BasicMultiMap.Set<String, AmiWebStyle> styleIdsToChildStyles = new BasicMultiMap.Set<String, AmiWebStyle>();
	private Map<String, AmiWebStyle> stylesByLabel = new HashMap<String, AmiWebStyle>();
	private LinkedHashSet<AmiWebStyleManagerListener> listeners = new LinkedHashSet<AmiWebStyleManagerListener>();
	private Map<String, AmiWebStyleType> types = new HashMap<String, AmiWebStyleType>();
	private BasicMultiMap.List<String, AmiWebStyleType> typesToChildTypes = new BasicMultiMap.List<String, AmiWebStyleType>();
	private boolean bindDone = true;
	final private AmiWebService service;
	private Map<String, AmiWebStyleOption> optionsByVarname = new HashMap<String, AmiWebStyleOption>();
	private Map<String, AmiWebStyleOption> optionsByDescription = new HashMap<String, AmiWebStyleOption>();
	private MapInMap<String, Short, AmiWebStyleOption> optionsByTypeCode = new MapInMap<String, Short, AmiWebStyleOption>();
	private MapInMap<String, String, AmiWebStyleOption> optionsByTypeName = new MapInMap<String, String, AmiWebStyleOption>();
	private LinkedHashSet<AmiWebStyleType> typesOrderedByHierarchy = null;//null=invalidated

	public AmiWebStyleManager(AmiWebService service) {
		this.service = service;
	}

	public void init(Map<String, Object> configuration) {
		this.bindDone = false;
		List<Map<String, Object>> styles = (List<Map<String, Object>>) configuration.get("styles");
		List<AmiWebStyle> collisions = new ArrayList<AmiWebStyle>();
		for (Map<String, Object> i : styles) {
			final AmiWebStyle style = new AmiWebStyleImpl(this, i);
			if (!addStyle(style))
				collisions.add(style);
		}
		for (AmiWebStyle i : collisions)
			addStyleForce(i);
		this.bindDone = true;
	}
	public Map<String, Object> getConfiguration() {
		List<Map<String, Object>> styles = new ArrayList<Map<String, Object>>(this.stylesById.size());
		for (AmiWebStyle i : this.stylesById.values()) {
			if (!i.getReadOnly()) {
				Map<String, Object> config = i.getStyleConfiguration();
				styles.add(config);
			}
		}
		return CH.m("styles", styles);
	}

	public AmiWebStyle getStyleById(String id) {
		return this.stylesById.get(id);
	}

	public AmiWebStyle getStylesByLabel(String label) {
		return this.stylesByLabel.get(label);
	}

	public AmiWebStyle removeStyleById(String id) {
		AmiWebStyle style = this.stylesById.remove(id);
		this.styleIdsToChildStyles.removeMulti(style.getParentStyle(), style);
		if (style != null) {
			this.stylesByLabel.remove(style.getLabel());
			for (AmiWebStyleManagerListener t : listeners)
				t.onStyleRemoved(style);
			removeListener(style);
		}
		return style;
	}

	public String getNextLabel(String suggested) {
		return SH.getNextId(suggested, this.stylesByLabel.keySet());
	}
	public String getNextId(String suggested) {
		return SH.getNextId(suggested, this.stylesById.keySet());
	}

	public boolean addStyle(AmiWebStyle style) {
		if (style.getId() != null) {
			if (stylesByLabel.containsKey(style.getLabel()))
				return false;
			if (stylesById.containsKey(style.getId()))
				return false;
			// we use below maps when checking cir-ref, don't optimize the call
			this.stylesById.put(style.getId(), style);
			this.stylesByLabel.put(style.getLabel(), style);
			if (hasCircRef(style)) {
				this.stylesById.remove(style.getId());
				this.stylesByLabel.remove(style.getLabel());
				return false;
			}
			this.styleIdsToChildStyles.putMulti(style.getParentStyle(), style);
		}
		for (AmiWebStyleManagerListener t : this.listeners)
			t.onStyleAdded(style);
		addListener(style);
		return true;
	}
	
	public boolean hasCircRef(String newStyle, String original) {
		HashSet<String> sink = new HashSet<String>();
		AmiWebStyle pStyle = this.getStyleById(newStyle);
		getAllParents(newStyle, sink);
		boolean hasCircRef = hasCircRef(pStyle, sink);
		return hasCircRef;
	}
	private void getAllParents (String style, Set<String> sink) {
		AmiWebStyle pStyle = null;
		if (style == null || (pStyle = this.getStyleById(style))==null || pStyle.getParentStyle() == null)
			return;
		String parentStyle = pStyle.getParentStyle();
		if (SH.is(parentStyle) && !sink.add(parentStyle)) {
			throw new RuntimeException("Circular Reference in " + style + "'s line of inheritance");
		}
		getAllParents(parentStyle, sink);
	}
	public boolean hasCircRef(AmiWebStyle style) {
		return style != null && style.getParentStyle() != null && hasCircRef(style, new HashSet<String>());
	}
	private boolean hasCircRef(AmiWebStyle style, Set<String> visited) {
		if (!visited.add(style.getId())) {
			return true; // return so we run the logic in addStyle(...)
//			throw new RuntimeException("Circular reference: " + style.getId());
		}
		String i = style.getParentStyle();
		AmiWebStyle child = getStyleById(i);
		if (child != null)
			if (hasCircRef(child, visited))
				return true;
		return false;
	}

	public AmiWebStyle addStyleForce(AmiWebStyle i) {
		if (addStyle(i))
			return i;
		i.setLabel(getNextLabel(i.getLabel()));
		i.setId(getNextId(i.getId()));
		if (!addStyle(i))
			return null;
		return i;
	}
	public void clear() {
		this.stylesById.clear();
		this.styleIdsToChildStyles.clear();
		this.stylesByLabel.clear();
		this.listeners.clear();
	}
	public Iterable<AmiWebStyle> getAllStyles() {
		return this.stylesById.values();
	}

	public void addListener(AmiWebStyleManagerListener listener) {
		this.listeners.add(listener);
	}
	public void removeListener(AmiWebStyleManagerListener listener) {
		this.listeners.remove(listener);
	}

	public void onLabelChanged(AmiWebStyle target, String old, String label) {
		this.stylesByLabel.remove(old);
		this.stylesByLabel.put(label, target);
		for (AmiWebStyleManagerListener t : this.listeners)
			t.onStyleLabelChanged(target, old, label);
	}
	public boolean getBindDone() {
		return bindDone;
	}

	public Set<String> getTypeKeys() {
		return this.types.keySet();
	}
	public AmiWebStyleType getStyleType(String name) {
		return CH.getOrThrow(this.types, name);
	}

	//Order by hierarchy (roots first,chilren last) and sub-sorted by name
	public Iterable<AmiWebStyleType> getTypes() {
		if (this.typesOrderedByHierarchy == null) {
			this.typesOrderedByHierarchy = new LinkedHashSet<AmiWebStyleType>(this.types.size());
			addTypesOrderByHierarchyRecursive(null);
		}
		return this.typesOrderedByHierarchy;
	}

	public void addType(AmiWebStyleType type) {
		this.typesToChildTypes.putMulti(type.getExtendsName(), type);
		this.typesOrderedByHierarchy = null;
		CH.putOrThrow(types, type.getName(), type);
		for (AmiWebStyleOption i : type.getGroupLabels().valuesMulti()) {
			CH.putOrThrow(this.optionsByVarname, i.getVarname(), i);
			CH.putOrThrow(this.optionsByDescription, i.getDescription(), i);
			this.optionsByTypeCode.putMultiOrThrow(i.getNamespace(), i.getKey(), i);
			this.optionsByTypeName.putMultiOrThrow(i.getNamespace(), i.getSaveKey(), i);
		}
	}
	public List<AmiWebStyleType> getChildTypes(String type) {
		List<AmiWebStyleType> r = this.typesToChildTypes.get(type);
		return r == null ? Collections.EMPTY_LIST : r;
	}
	public Set<AmiWebStyle> getChildStyles(String styleId) {
		Set<AmiWebStyle> r = this.styleIdsToChildStyles.get(styleId);
		return r == null ? Collections.EMPTY_SET : r;
	}

	public AmiWebService getService() {
		return service;
	}

	public void onParentStyleChanged(AmiWebStyleImpl target, String oldParentStyleId, String parentStyleId) {
		if (!this.styleIdsToChildStyles.removeMulti(oldParentStyleId, target))
			return;//hasnt been added yet
		this.styleIdsToChildStyles.putMulti(parentStyleId, target);
		for (AmiWebStyleManagerListener i : this.listeners)
			i.onStyleParentChanged(target, oldParentStyleId, parentStyleId);
	}

	public Map<String, AmiWebStyleOption> getOptionsByVarname() {
		return this.optionsByVarname;
	}

	public Map<String, AmiWebStyleOption> getOptionsByLongname() {
		return this.optionsByDescription;
	}

	public AmiWebStyleOption getOption(String t, short k) {
		return this.optionsByTypeCode.getMulti(t, k);
	}
	public AmiWebStyleOption getOption(String t, String k) {
		return this.optionsByTypeName.getMulti(t, k);
	}

	public Set<String> getStyleIds() {
		return this.stylesById.keySet();
	}

	public void fillInMissingParentStyles() {
		for (Entry<String, AmiWebStyle> i : CH.l(this.stylesById.entrySet())) {
			if (OH.eq(FACTORY_DEFAULT_ID, i.getKey()))
				continue;
			String ps = i.getValue().getParentStyle();
			if (!this.stylesById.containsKey(ps)) {
				AmiWebStyleImpl style = new AmiWebStyleImpl(this, ps, this.getNextLabel(AmiWebUtils.toPrettyName(ps) + " (missing style placeholder)"));
				style.setParentStyle(AmiWebStyleManager.FACTORY_DEFAULT_ID);
				style.setReadOnly(true);
				this.addStyle(style);
			}
		}
	}

	private static final Comparator<AmiWebStyleType> NAME_COMPARATOR = new Comparator<AmiWebStyleType>() {
		@Override
		public int compare(AmiWebStyleType o1, AmiWebStyleType o2) {
			return OH.compare(o1.getName(), o2.getName());
		}
	};

	private void addTypesOrderByHierarchyRecursive(String key) {
		if (this.typesToChildTypes.containsKey(key))
			for (AmiWebStyleType i : CH.sort(this.typesToChildTypes.get(key), NAME_COMPARATOR)) {
				this.typesOrderedByHierarchy.add(i);
				addTypesOrderByHierarchyRecursive(i.getName());
			}
	}
}
