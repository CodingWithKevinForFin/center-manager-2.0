package com.f1.ami.web.style;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.web.AmiWebCss;
import com.f1.ami.web.AmiWebOverrideValue;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.style.impl.AmiWebStyleOption;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.ToDoException;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.table.derived.ToDerivedString;

//A peer to the AmiWebStylePortlet, one-for-one
public class AmiWebStyledPortletPeer implements AmiWebStyleListener, AmiWebStyle, ToDerivedString {

	private static final Logger log = LH.get();
	final private AmiWebStyledPortlet portlet;
	private boolean styleInit = false;
	private Set<String> types;
	final private BasicMultiMap.List<String, AmiWebStyleOption> varValues = new BasicMultiMap.List<String, AmiWebStyleOption>();
	private Map<Short, Object> values = new HashMap<Short, Object>();
	private Map<Short, Object> overrides = new HashMap<Short, Object>();
	private String styleTypeId;
	private AmiWebStyleManager manager;
	final private AmiWebOverrideValue<String> parentStyleId = new AmiWebOverrideValue<String>(null);
	private AmiWebStyleImpl parentStyle;
	private AmiWebStyleType styleType;

	public AmiWebStyledPortletPeer(AmiWebStyledPortlet portlet, AmiWebService service) {
		this.portlet = portlet;
		this.manager = service.getStyleManager();
		this.styleTypeId = this.portlet.getStyleType();
		service.getStyleManager().addListener(this);
		this.setParentStyle(AmiWebStyleManager.LAYOUT_DEFAULT_ID);
		this.types = Collections.singleton(styleTypeId);
		this.styleType = manager.getStyleType(this.styleTypeId);
	}
	public void initStyle() {
		OH.assertFalse(styleInit);
		this.initStyle(null);
	}

	public AmiWebStyledPortlet getPortlet() {
		return portlet;
	}

	@Override
	public void initStyle(Map<String, Object> configuration) {
		readConfig(configuration, true);
	}
	public void importConfig(Map<String, Object> configuration) {
		readConfig(configuration, false);
	}
	public void readConfig(Map<String, Object> configuration, boolean isInit) {
		this.styleInit = true;
		if (configuration != null) {
			if (isInit) {
				this.values.clear();
				this.varValues.clear();
				this.overrides.clear();
			}
			Map<String, Object> m;
			if (configuration.containsKey("vl")) {//BACKWARDS COMPATIBILITY
				Map<String, Map<String, Object>> vl = (Map<String, Map<String, Object>>) CH.getOr(Caster_Simple.OBJECT, configuration, "vl", Collections.emptyMap());
				for (String s : CH.l(vl.keySet()))
					if (!manager.getTypeKeys().contains(s)) {
						LH.warning(log, "Dropping unknown style type: ", s, " ==> ", vl.get(s));
						vl.remove(s);
					}
				m = vl.get(this.styleTypeId);
			} else
				m = configuration;
			if (m != null) {
				for (Entry<String, Object> i : m.entrySet()) {
					if ("pt".equals(i.getKey()))
						continue;
					AmiWebStyleOption option = manager.getOption(this.styleTypeId, i.getKey());
					if (option == null) {
						LH.warning(log, "Unknown option: ", this.styleTypeId + ":" + i.getKey());
					} else {
						Object val = option.toInternalStorageValue(this.manager.getService(), i.getValue());
						if (val == null) {
							if (isInit) {
								LH.warning(log, "Option has invalid value: ", option.getVarname() + " ==> " + i.getValue());
							} else {
								this.values.put(option.getKey(), val);
								if (isVar(val))
									varValues.putMulti((String) val, option);
							}
						} else {
							this.values.put(option.getKey(), val);
							if (isVar(val))
								varValues.putMulti((String) val, option);
						}
					}
				}
			}
			Object o = configuration.get("pt");
			if (o instanceof List) {
				List<String> l = (List<String>) o;
				if (!l.isEmpty())
					o = l.get(0);
			}
			if (o instanceof String) {
				String ps = (String) o;
				if (this.manager.getStyleById(ps) == null && SH.is(ps)) {
					AmiWebStyleImpl style = new AmiWebStyleImpl(this.manager, ps, this.manager.getNextLabel(AmiWebUtils.toPrettyName(ps) + " (missing style placeholder)"));
					style.setParentStyle(AmiWebStyleManager.FACTORY_DEFAULT_ID);
					style.setReadOnly(true);
					this.manager.addStyle(style);
				}
				setParentStyle((String) o);
			}
		}
		String styleType = portlet.getStyleType();
		for (short i : manager.getStyleType(styleType).getKeys())
			setStyleOnPortlet(i, null, this.resolveValue(styleType, i));
	}
	@Override
	public void onStyleAdded(AmiWebStyle style) {
	}
	@Override
	public void onStyleRemoved(AmiWebStyle style) {
	}
	@Override
	public void onStyleLabelChanged(AmiWebStyle style, String old, String label) {
	}
	@Override
	public String getId() {
		return null;
	}
	@Override
	public void setId(String id) {
		if (id != null)
			throw new UnsupportedOperationException();
	}
	@Override
	public String getLabel() {
		return null;
	}
	@Override
	public void setLabel(String label) {
		if (label != null)
			throw new UnsupportedOperationException();

	}
	@Override
	public boolean hasType(String type) {
		return OH.eq(this.styleTypeId, type);
	}
	@Override
	public Set<String> getTypes() {
		return this.types;
	}
	@Override
	public Map<String, Object> getStyleConfiguration() {
		Map<String, Object> vals = new HashMap<String, Object>(values.size());
		if (!values.isEmpty()) {
			for (Entry<Short, Object> i : values.entrySet()) {
				AmiWebStyleOption option = this.manager.getOption(this.styleTypeId, i.getKey());
				if (option == null) {
					LH.warning(log, "Unknown option: ", this.styleTypeId + ":" + i.getKey());
				} else {
					Object val = option.toExportValue(this.manager.getService(), i.getValue());
					if (val == null) {
						LH.warning(log, "Option has invalid value: ", option.getVarname() + " ==> " + i.getValue());
					} else {
						vals.put(option.getSaveKey(), val);
					}
				}
			}
		}

		AmiWebUtils.putSkipEmpty(vals, "pt", this.parentStyleId.getValue());
		return vals;
	}

	//this one includes nulls, and should be used in conjunction with importConfig
	public Map<String, Object> exportConfig() {
		Map<String, Object> vals = new HashMap<String, Object>(values.size());
		for (AmiWebStyleOption option : getStyleManager().getStyleType(this.styleTypeId).getOptions()) {
			Object value = this.values.get(option.getKey());
			Object val = option.toExportValue(this.manager.getService(), value);
			vals.put(option.getSaveKey(), val);
		}

		AmiWebUtils.putSkipEmpty(vals, "pt", this.parentStyleId.getValue());
		return vals;
	}
	public static Map<String, Object> fromCodes(Map<Short, Object> codes) {
		Map<String, Object> r = new HashMap<String, Object>(codes.size());
		for (Entry<Short, Object> i : codes.entrySet()) {
			String key = AmiWebStyleConsts.GET(i.getKey());
			if (key != null)
				r.put(key, i.getValue());
		}
		return r;
	}
	@Override
	public String getParentStyle() {
		return this.parentStyleId.get();
	}
	@Override
	public Object getValue(String styleType, short name) {
		OH.assertEq(styleType, this.styleTypeId);
		return this.values.get(name);
	}
	public Object getValue(short name) {
		return this.values.get(name);
	}
	@Override
	public void putValue(String styleType, short key, Object value) {
		OH.assertEq(styleType, this.styleTypeId);
		putValue(key, value);
	}
	public void putValue(short key, Object value) {
		Object old = resolveValue(styleTypeId, key);
		AmiWebStyleOption option = manager.getOption(styleTypeId, key);
		if (value == null) {
			values.remove(key);
			if (isVar(old))
				varValues.removeMulti((String) old, option);
		} else {
			values.put(key, value);
			if (isVar(value))
				varValues.putMulti((String) value, option);
		}
		Object nuw = resolveValue(styleTypeId, key);
		if (OH.ne(old, nuw) && !this.overrides.containsKey(key))
			setStyleOnPortlet(key, old, nuw);
	}
	public Object resolveValue(short name) {
		return resolveValue(this.styleTypeId, name);
	}
	@Override
	public Object resolveValue(String styleType, short name) {
		/*
		 * 1. use override (set via amiscript)
		 * 2. use styles set in this portlet (via either style or style manager)
		 * 3. use parent's style (pre-built style, e.g. LAYOUT_DEFAULT, Nova Dark, etc...)
		 */
		if (OH.eq(styleType, this.styleTypeId)) {
			if (this.overrides.containsKey(name)) {
				Object r = this.overrides.get(name);
				if (r != null)
					return r;//if we've specifically overridden to null, use the parent
			} else if (this.values.containsKey(name)) {
				return this.values.get(name);
			}
		}
		return parentStyle.resolveValue(styleType, name);
	}
	@Override
	public void addListener(String type, AmiWebStyleListener listener) {
		throw new UnsupportedOperationException();
	}
	@Override
	public void removeListener(String type, AmiWebStyleListener listener) {
		throw new UnsupportedOperationException();
	}
	@Override
	public void close() {
		this.manager.removeListener(this);
		if (this.parentStyle != null)
			this.parentStyle.removeListener(this.styleTypeId, this);
		this.parentStyle = null;
		this.parentStyleId.clear();
		this.values.clear();
		this.varValues.clear();
		this.overrides.clear();
	}
	@Override
	public Set<Short> getDeclaredKeys(String styleType) {
		OH.assertEq(styleType, this.styleTypeId);
		return getDeclaredKeys();
	}

	public Set<Short> getDeclaredKeys() {
		return values.keySet();
	}
	@Override
	public void setParentStyle(String styleId) {
		if (this.parentStyleId.set(styleId, true)) {
			updateParentStyle();
		}
	}
	private void updateParentStyle() {
		AmiWebStyle i = this.parentStyle;
		if (i != null)
			i.removeListener(this.styleTypeId, this);
		String styleId = this.parentStyleId.get();
		this.parentStyle = (AmiWebStyleImpl) this.manager.getStyleById(styleId);
		if (this.parentStyle == null)
			throw new NullPointerException("Style not found: " + styleId);
		parentStyle.addListener(this.styleTypeId, this);
		if (this.styleInit) {
			BasicIndexedList<String, String> oldVarValues = i.getVarValues();
			BasicIndexedList<String, String> nuwVarValues = this.parentStyle.getVarValues();
			Set<String> changed = new HasherSet<String>();
			for (String s : CH.comm(oldVarValues.keySet(), nuwVarValues.keySet(), true, true, true)) {
				if (OH.ne(oldVarValues.getNoThrow(s), nuwVarValues.getNoThrow(s)))
					changed.add(s);
			}
			for (Short styleKey : this.manager.getStyleType(this.styleTypeId).getKeys())
				if (!this.values.containsKey(styleKey) && !this.overrides.containsKey(styleKey)) {
					Object value = parentStyle.resolveValue(styleTypeId, styleKey);
					setStyleOnPortlet(styleKey, null, value);
				}
			for (Entry<String, List<AmiWebStyleOption>> entry : this.varValues.entrySet()) {
				if (changed.contains(entry.getKey())) {
					Object value = parentStyle.getVarValues().get(entry.getKey());
					for (AmiWebStyleOption options : entry.getValue())
						setStyleOnPortlet(options.getKey(), null, value);
				}
			}
			for (String s : changed)
				AmiWebUtils.onVarConstChanged(this.portlet, s);
		}
		this.portlet.onParentStyleChanged(this);
	}
	@Override
	public boolean inheritsFrom(String styleId) {
		return this.parentStyle != null && this.parentStyle.inheritsFrom(styleId);
	}
	@Override
	public AmiWebStyleManager getStyleManager() {
		return this.manager;
	}
	@Override
	public String getUrl() {
		return null;
	}
	@Override
	public boolean getReadOnly() {
		return false;
	}
	@Override
	public AmiWebStyle setReadOnly(boolean isReadonly) {
		if (isReadonly)
			throw new UnsupportedOperationException();
		return this;
	}
	@Override
	public void onStyleValueChanged(AmiWebStyleImpl style, String styleType, short styleKey, Object old, Object nuw) {
		if (OH.eq(styleType, this.styleTypeId)) {
			if (this.overrides.containsKey(styleKey)) {
				if (this.overrides.get(styleKey) == null)
					setStyleOnPortlet(styleKey, old, nuw);
			} else if (!this.values.containsKey(styleKey))
				setStyleOnPortlet(styleKey, old, nuw);
		}
	}
	private void setStyleOnPortlet(short styleKey, Object old, Object nuw) {
		if (nuw == null) {
			//LH.warning(log, "Error applying style " + styleType + "::" + AmiWebStyleConsts.GET(styleKey) + "=null for " + this.portlet.toString());
			return;
		}
		try {
			this.portlet.onStyleValueChanged(styleKey, old, this.parentStyle.getVarColor(nuw));
		} catch (Exception e) {
			LH.warning(log, "Error applying style " + styleTypeId + "::" + AmiWebStyleConsts.GET(styleKey) + "='" + nuw + "' for " + this.portlet.toString(), e);
		}
	}
	@Override
	public void onStyleParentChanged(AmiWebStyleImpl target, String oldParentStyleId, String parentStyleId) {
	}
	@Override
	public Set<String> getDeclaredVarnames() {
		throw new ToDoException();
	}
	@Override
	public void putValueOverride(String styleType, short key, Object value) {
		OH.assertEq(styleType, this.styleTypeId);
		putValueOverride(key, value);
	}
	public void putValueOverride(short key, Object value) {
		Object old = resolveValue(styleTypeId, key);
		overrides.put(key, value);
		Object nuw = resolveValue(styleTypeId, key);
		if (OH.ne(old, nuw))
			setStyleOnPortlet(key, old, nuw);
	}
	@Override
	public Object getValueOverride(String styleType, short key) {
		OH.assertEq(styleType, this.styleTypeId);
		return this.overrides.get(key);
	}
	@Override
	public void resetOverrides() {
		if (!this.overrides.isEmpty()) {
			for (Entry<Short, Object> i : this.overrides.entrySet()) {
				short key = i.getKey();
				Object old = this.overrides.remove(key);
				Object nuw = resolveValue(styleTypeId, key);
				if (OH.ne(old, nuw) && !this.overrides.containsKey(key))
					setStyleOnPortlet(key, old, nuw);
			}
			this.overrides.clear();
		}
	}
	@Override
	public Object removeValueOverride(String styleType, short key) {
		OH.assertEq(styleType, this.styleTypeId);
		return removeValueOverride(key);
	}
	public Object removeValueOverride(short key) {
		if (!this.overrides.containsKey(key))
			return null;
		Object old = this.overrides.remove(key);
		Object nuw = resolveValue(styleTypeId, key);
		if (OH.ne(old, nuw) && !this.overrides.containsKey(key))
			setStyleOnPortlet(key, old, nuw);
		return old;
	}
	@Override
	public boolean isValueOverride(String styleType, short key) {
		OH.assertEq(styleType, this.styleTypeId);
		return this.overrides.containsKey(key);
	}
	@Override
	public void resetParentStyleOverride() {
		if (this.parentStyleId.clearOverride()) {
			updateParentStyle();
		}
	}
	@Override
	public boolean setParentStyleOverride(String id) {
		if (this.parentStyleId.setOverride(id)) {
			updateParentStyle();
			return true;
		}
		return false;
	}
	@Override
	public String toDerivedString() {
		return toDerivedString(new StringBuilder()).toString();
	}
	@Override
	public StringBuilder toDerivedString(StringBuilder sb) {
		sb.append("PANEL_STYLE[");
		this.getPortlet().toDerivedString(sb);
		return sb.append("]");
	}
	@Override
	public AmiWebCss getCss() {
		return null;
	}
	@Override
	public AmiWebStyleVars getVars() {
		return null;
	}
	@Override
	public BasicIndexedList<String, String> getVarValues() {
		return this.parentStyle == null ? new BasicIndexedList<String, String>() : this.parentStyle.getVarValues();
	}
	@Override
	public void onVarColorRemoved(String key) {
		List<AmiWebStyleOption> t = varValues.get(key);
		if (CH.isntEmpty(t)) {
			Object varColor = this.parentStyle.getVarColor(key);
			for (int j = 0; j < t.size(); j++) {
				AmiWebStyleOption option = t.get(j);
				this.portlet.onStyleValueChanged(option.getKey(), null, varColor);
			}
		}
		AmiWebUtils.onVarConstChanged(this.portlet, key);
	}
	@Override
	public void onVarColorAdded(String key, String color) {
		AmiWebUtils.onVarConstChanged(this.portlet, key);
		//		this.portlet.onVarConstChanged(key);

	}
	@Override
	public void onVarColorUpdated(String key, String old, String color) {
		//		this.portlet.onVarConstChanged(key);
		AmiWebUtils.onVarConstChanged(this.portlet, key);
		List<AmiWebStyleOption> t = varValues.get(key);
		if (CH.isntEmpty(t))
			for (int j = 0; j < t.size(); j++) {
				AmiWebStyleOption option = t.get(j);
				this.portlet.onStyleValueChanged(option.getKey(), old, color);
			}
	}
	static private boolean isVar(Object value) {
		return value instanceof String && SH.startsWith((String) value, '$');
	}

	public AmiWebStyleType getStyleType() {
		return this.styleType;
	}
	@Override
	public boolean isParentStyleOverride() {
		return this.parentStyleId.isOverride();
	}
	@Override
	public Set<Short> getOverrides(String styleType) {
		if (OH.ne(this.styleTypeId, styleType))
			return Collections.EMPTY_SET;
		return this.overrides.keySet();
	}
	@Override
	public boolean isVarColorUsed(String color) {
		return CH.isntEmpty(varValues.get(color));
	}
}
