package com.f1.ami.web.style;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.web.AmiWebCss;
import com.f1.ami.web.AmiWebOverrideValue;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.style.impl.AmiWebStyleOption;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_Field;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_Global;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.ToDoException;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.MapInMap;

//a "Style", similar to a class is css 
public class AmiWebStyleImpl implements AmiWebStyle, AmiWebStyleListener, AmiWebStyleManagerListener {

	final private AmiWebOverrideValue<String> parentStyleId = new AmiWebOverrideValue<String>(null);
	private AmiWebStyleImpl parentStyle = null;
	final private AmiWebStyleManager manager;
	final private MapInMap<String, Short, Object> overrides = new MapInMap<String, Short, Object>().setRemoveEmptyPolicy(false);
	final private MapInMap<String, Short, Object> values = new MapInMap<String, Short, Object>().setRemoveEmptyPolicy(false);
	final private BasicMultiMap.List<String, AmiWebStyleOption> varValues = new BasicMultiMap.List<String, AmiWebStyleOption>();
	final private MapInMap<String, Short, Object> resolvedCache = new MapInMap<String, Short, Object>().setRemoveEmptyPolicy(false);
	final private BasicMultiMap.List<String, AmiWebStyleListener> listeners = new BasicMultiMap.List<String, AmiWebStyleListener>();
	final private AmiWebCss css;
	final private AmiWebStyleVars vars;
	private boolean bindDone = false;
	private String label;
	private String id;
	private static final Logger log = LH.get();
	private String url;
	private boolean isReadOnly;

	public AmiWebStyleImpl(AmiWebStyleManager manager, String id, String label) {
		for (String i : manager.getTypeKeys())
			values.put(i, new HashMap<Short, Object>());
		this.manager = manager;
		this.id = id;
		this.label = label;
		this.vars = new AmiWebStyleVars(this);
		css = new AmiWebCss(manager.getService().getCustomCssManager());
		if (manager.getBindDone())
			bind();
		if ((id == null) != (label == null))
			throw new IllegalArgumentException("id and label must both be set or null");
	}

	public AmiWebStyleManager getManager() {
		return this.manager;
	}
	public AmiWebStyleImpl(AmiWebStyleManager manager, Map<String, Object> configuration) {
		this.manager = manager;
		this.id = CH.getOr(Caster_String.INSTANCE, configuration, "id", null);
		this.label = CH.getOrThrow(Caster_String.INSTANCE, configuration, "lb");
		this.vars = new AmiWebStyleVars(this);
		css = new AmiWebCss(manager.getService().getCustomCssManager());
		this.css.setCustomCssForce(AmiWebUtils.getAmiScript(configuration, "css", null));
		this.vars.init(CH.getOr(Map.class, configuration, "vars", Collections.EMPTY_MAP));
		initStyle(configuration);
	}
	public AmiWebStyleImpl(AmiWebStyle existing) {
		this.manager = existing.getStyleManager();
		for (String i : manager.getTypeKeys())
			values.put(i, new HashMap<Short, Object>());
		this.setParentStyle(existing.getParentStyle());
		this.label = existing.getLabel();
		this.id = existing.getId();
		this.vars = new AmiWebStyleVars(this);
		css = new AmiWebCss(manager.getService().getCustomCssManager());
		this.bindDone = false;
		for (String type : existing.getTypes())
			for (short key : existing.getDeclaredKeys(type))
				putValue(type, key, existing.getValue(type, key));
		this.css.setCustomCssForce(existing.getCss().getCustomCss());
	}

	@Override
	public Map<String, Object> getStyleConfiguration() {
		MapInMap<String, String, Object> vl = new MapInMap<String, String, Object>();
		for (Entry<String, Map<Short, Object>> j : values.entrySet()) {
			Map<Short, Object> inner = j.getValue();
			if (inner.size() > 0) {
				Map<String, Object> r = new HashMap<String, Object>(inner.size());
				for (Entry<Short, Object> i : inner.entrySet()) {
					AmiWebStyleOption option = this.manager.getOption(j.getKey(), i.getKey());
					if (option == null) {
						LH.warning(log, "Unknown option: ", j.getKey() + ":" + i.getKey());
					} else {
						Object val = option.toExportValue(this.manager.getService(), i.getValue());
						if (val == null) {
							LH.warning(log, "Option has invalid value: ", option.getVarname() + " ==> " + i.getValue());
						} else {
							r.put(option.getSaveKey(), val);
						}
					}
				}
				vl.put(j.getKey(), r);
			}
		}
		Map<String, Object> r = CH.mSkipNull("pt", this.parentStyleId.getValue(), "id", this.id, "lb", this.label);
		AmiWebUtils.putSkipEmpty(r, "vars", vars.getConfiguration());
		AmiWebUtils.putAmiScript(r, "css", css.getCustomCss());
		AmiWebUtils.putSkipEmpty(r, "vl", vl);
		return r;
	}

	@Override
	public void initStyle(Map<String, Object> configuration) {
		if (configuration != null) {
			this.values.clear();
			this.varValues.clear();
			for (String i : manager.getTypeKeys())
				values.put(i, new HashMap<Short, Object>());
			Map<String, Map<String, Object>> vl = (Map<String, Map<String, Object>>) CH.getOr(Caster_Simple.OBJECT, configuration, "vl", Collections.emptyMap());
			for (Entry<String, Map<String, Object>> styleTypes : vl.entrySet()) {
				String styleType = styleTypes.getKey(); // children of Styles in style manager
				/*
				 * Styles
				 *   -Chart Axis <-- this is a style type
				 *   -DashBoard
				 *   ...
				 */
				// BACKWARDS COMPATIBILITY
				if (!"alertDialog".equals(styleType) && !getManager().getTypeKeys().contains(styleType)) {
					LH.warning(log, "Dropping unknown style type: ", styleType, " ==> ", styleTypes.getValue());
				} else {
					Map<String, Object> styleCodes = styleTypes.getValue(); // styles belonging to a particular visualization type
					/*
					 *  Styles
					 *  	-Chart Axis
					 *  		-chartAxisAxTitleSz <-- this is a style code
					 *  ...
					 */
					// BACKWARDS COMPATIBILITY
					// dialogFldBgCl got split to two styles in newer version
					if ("alertDialog".equals(styleType) && styleCodes.containsKey("dialogFldBgCl")) {
						styleCodes.put("dialogFormButtonPnlBgCl", styleCodes.get("dialogFldBgCl"));
					}
					for (Entry<String, Object> i : styleCodes.entrySet()) {
						AmiWebStyleOption option = null;
						String styleCode = i.getKey(); // e.g. fontCl
						Object styleVal = i.getValue(); // e.g. #ff0000
						if (styleCode.startsWith("calenday")) {
							// backward fix for typo
							styleCode = styleCode.replace("calenday", "calendar");
						}
						// begin BACKWARDS COMPATIBILITY
						if ("alertDialog".equals(styleType)) {
							// map old version to new version
							if ("dialogBgCl".equals(styleCode)) {
								// map dialogBgCl to globalUsrWinCl
								option = manager.getOption(AmiWebStyleTypeImpl_Global.TYPE_GLOBAL, "usrWinCl");
							} else if ("dialogTitleFontCl".equals(styleCode)) {
								// map dialogTitleFontCl to globalUsrWinTxtCl
								option = manager.getOption(AmiWebStyleTypeImpl_Global.TYPE_GLOBAL, "usrWinTxtCl");
							} else if ("dialogXButtonBgCl".equals(styleCode)) {
								// dialogXButtonBgCl to globalUsrWinBtnCl
								option = manager.getOption(AmiWebStyleTypeImpl_Global.TYPE_GLOBAL, "usrWinBtnCl");
							} else if ("dialogXButtonBorderCl".equals(styleCode)) {
								// dialogXButtonBorderCl to globalUsrWinBtnUpCl
								option = manager.getOption(AmiWebStyleTypeImpl_Global.TYPE_GLOBAL, "usrWinBtnUpCl");
							} else if ("dialogXButtonShadowCl".equals(styleCode)) {
								// dialogXButtonShadowCl to globalUsrWinBtnDownCl
								option = manager.getOption(AmiWebStyleTypeImpl_Global.TYPE_GLOBAL, "usrWinBtnDownCl");
							} else if ("dialogXButtonIconCl".equals(styleCode)) {
								// dialogXButtonIconCl to globalUsrWinBtnIconCl
								option = manager.getOption(AmiWebStyleTypeImpl_Global.TYPE_GLOBAL, "usrWinBtnIconCl");
							} else if ("dialogFontFam".equals(styleCode)) {
								// dialogFontFam to dialogTitleFontFam
								option = manager.getOption(AmiWebStyleTypeImpl_Global.TYPE_GLOBAL, "dialogTitleFontFam");
							} else
								option = manager.getOption(AmiWebStyleTypeImpl_Global.TYPE_GLOBAL, styleCode);
						} else if ("form".equals(styleType)) {
							// had a typo in previous version
							// field slider section removed
							if (styleCode.startsWith("calenda") || styleCode.contains("fldTrack") || styleCode.contains("fldGrip")) {
								continue;
							}
							// below migrates HTML/Canvas' styles to Fields
							if (styleCode.startsWith("fld")) {
								// field related styles moved to Fields; 
								option = manager.getOption(AmiWebStyleTypeImpl_Field.TYPE_FIELD, styleCode);
								//Start of doing backwards compatible on field Css
								if (option == null && AmiWebStyleConsts.DEPRECATED_FIELD_CSS_STYLE.contains(styleCode)) {
									String namespace = null;
									switch (styleCode) {
										case AmiWebStyleConsts.DEPRECATED_PROPERTY_NAME_FLD_CSS_BTN://ok
											namespace = "formButtonField";
											break;
										case AmiWebStyleConsts.DEPRECATED_PROPERTY_NAME_FLD_CSS_TXT://ok
											namespace = "formTextField";
											break;
										case AmiWebStyleConsts.DEPRECATED_PROPERTY_NAME_FLD_CSS_CHECK://ok
											namespace = "formCheckboxField";
											break;
										case AmiWebStyleConsts.DEPRECATED_PROPERTY_NAME_FLD_CSS_SEL://ok
											namespace = "formSelectField";
											break;
										case AmiWebStyleConsts.DEPRECATED_PROPERTY_NAME_FLD_CSS_DATE://ok
											namespace = "formDateField";
											break;
										case AmiWebStyleConsts.DEPRECATED_PROPERTY_NAME_FLD_CSS_DATE_RNG://ok
											namespace = "formDateRangeField";
											break;
										case AmiWebStyleConsts.DEPRECATED_PROPERTY_NAME_FLD_CSS_DATE_TIME://ok
											namespace = "formDateTimeField";
											break;
										case AmiWebStyleConsts.DEPRECATED_PROPERTY_NAME_FLD_CSS_DIV://ok
											namespace = "formDivField";
											break;
										case AmiWebStyleConsts.DEPRECATED_PROPERTY_NAME_FLD_CSS_IMG://ok
											namespace = "formImageField";
											break;
										case AmiWebStyleConsts.DEPRECATED_PROPERTY_NAME_FLD_CSS_MULTI_SEL://ok
											namespace = "formMultiSelectField";
											break;
										case AmiWebStyleConsts.DEPRECATED_PROPERTY_NAME_FLD_CSS_SUB_RNG://ok
											namespace = "formRangeSliderField";
											break;
										case AmiWebStyleConsts.DEPRECATED_PROPERTY_NAME_FLD_CSS_RNG://ok
											namespace = "formSliderField";
											break;
										case AmiWebStyleConsts.DEPRECATED_PROPERTY_NAME_FLD_CSS_TXT_AREA://ok
											namespace = "formTextareaField";
											break;
										case AmiWebStyleConsts.DEPRECATED_PROPERTY_NAME_FLD_CSS_UPLOAD://ok
											namespace = "formUploadField";
											break;
										case AmiWebStyleConsts.DEPRECATED_PROPERTY_NAME_FLD_CSS_TIME://ok
											namespace = "formTimeField";
											break;
										case AmiWebStyleConsts.DEPRECATED_PROPERTY_NAME_FLD_CSS_TIME_RNG://ok
											namespace = "formTimeRangeField";
											break;

									}
									if (namespace != null) {
										styleCode = "fldCss";
										option = manager.getOption(namespace, styleCode);
									}
								} //End of doing backwards compatible on field Css
							} else {
								// additional fields to move
								switch (styleCode) {
									case "fieldBorderWd":
										option = manager.getOption(AmiWebStyleTypeImpl_Field.TYPE_FIELD, AmiWebStyleConsts.CODE_FLD_BDR_WD);
										break;
									case "lblPd":
									case "underline":
									case "bold":
									case "fontSz":
									case "fontFam":
									case "fontCl":
									case "italic":
										option = manager.getOption(AmiWebStyleTypeImpl_Field.TYPE_FIELD, styleCode);
										break;
									case "txtAlign": // this got removed so no op
										continue;
									default:
										option = manager.getOption(styleType, styleCode);
										break;
								}
							}
						} else if ("formCheckboxField".equals(styleType)) {
							// handle removed fields
							switch (styleCode) {
								case "fldFontFam":
								case "fldFontSz":
								case "fontCl": // these 3 styles got removed, no op
									continue;
								default:
									option = manager.getOption(styleType, styleCode);
									break;
							}
						} else
							// end BACKWARDS COMPATIBILITY
							option = manager.getOption(styleType, styleCode);
						if (option == null) {
							LH.warning(log, "Unknown option for style '", getId(), "': ", styleType + ":" + styleCode);
						} else {
							Object val = option.toInternalStorageValue(this.manager.getService(), styleVal);
							if (val == null) {
								LH.warning(log, "Option has invalid value: ", option.getVarname() + " ==> " + styleVal);
							} else {
								if (isVar(val))
									this.varValues.putMulti((String) val, option);
								this.values.putMulti(option.getNamespace(), option.getKey(), val);
							}
						}
					}

				}
			}
			Object o = configuration.get("pt");
			if (o instanceof List) {
				List<String> l = (List<String>) o;
				if (!l.isEmpty())
					o = l.get(0);
				else
					o = AmiWebStyleManager.FACTORY_DEFAULT_ID;
			}
			if (o instanceof String) {
				setParentStyleFromConfig((String) o);
			}
		}
	}

	protected void setParentStyleFromConfig(String ps) {
		setParentStyle(ps);
	}

	private void bind() {
		this.bindDone = true;
		clearCache();
	}

	@Override
	public String getParentStyle() {
		return parentStyleId.get();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		if (OH.eq(this.id, id))
			return;
		if (this.manager.getStyleById(this.id) == this) {
			if (this.id == null || id == null)
				throw new RuntimeException("may not add/remove style id");
			if (this.manager.getStyleById(id) != null)
				throw new RuntimeException("duplicate style id: " + id);
			this.manager.removeStyleById(this.id);
			this.id = id;
			this.manager.addStyle(this);
		} else
			this.id = id;
	}
	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public void setLabel(String label) {
		if (OH.eq(label, this.label))
			return;
		if (this.manager.getStyleById(this.id) == this)
			if (this.manager.getStylesByLabel(label) != null)
				throw new RuntimeException("duplicate style label: " + id);
		String old = this.label;
		this.label = label;
		if (this.manager.getStyleById(this.id) == this)
			this.manager.onLabelChanged(this, old, label);
	}

	@Override
	public Object getValue(String type, short name) {
		if (!hasType(type))
			return null;
		return this.values.get(type).get(name);
	}
	@Override
	public void putValue(String type, short key, Object value) {
		Map<Short, Object> values = this.values.get(type);
		if (values == null)
			throw new RuntimeException("type not supported for " + this.label + ": " + type + " (supported include: " + SH.join(',', this.getTypes()) + ")");
		Object old = resolveValue(type, key);
		AmiWebStyleOption option = manager.getOption(type, key);
		if (value == null) {
			values.remove(key);
			if (isVar(old))
				varValues.removeMulti((String) old, option);
		} else {
			values.put(key, value);
			if (isVar(value))
				varValues.putMulti((String) value, option);
		}
		if (!this.overrides.containsKey(type, key)) {
			Object nuw = resolveValueNoCache(type, key);
			if (OH.ne(old, nuw))
				fireValueChanged(type, key, old, nuw, true);
		}
	}

	protected void fireValueChanged(String type, short key, Object old, Object nuw, boolean walkInheritance) {
		//		this.resolvedCache.removeMulti(type, key);
		List<AmiWebStyleListener> listeners = this.listeners.get(type);
		if (listeners != null)
			for (int i = 0; i < listeners.size(); i++)
				listeners.get(i).onStyleValueChanged(this, type, key, old, nuw);
		listeners = this.listeners.get(null);
		if (listeners != null)
			for (int i = 0; i < listeners.size(); i++)
				listeners.get(i).onStyleValueChanged(this, type, key, old, nuw);
		List<AmiWebStyleType> extended = manager.getChildTypes(type);
		if (walkInheritance && !extended.isEmpty()) {
			for (AmiWebStyleType t : extended) {
				if (getValue(t.getName(), key) == null) {
					Object nuw2 = resolveValueNoCache(t.getName(), key);
					this.resolvedCache.putMulti(t.getName(), key, nuw2);
					fireValueChanged(t.getName(), key, old, nuw2, false);
				}
			}
		}
	}
	@Override
	public Object resolveValue(String type, short name) {
		if (!bindDone)
			bind();
		Object r = resolvedCache.getMulti(type, name);
		if (r != null)
			return r;
		return resolveValueNoCache(type, name);
	}
	private Object resolveValueNoCache(String type, short name) {
		if (!bindDone)
			bind();
		Object r = null;
		for (String t = type; r == null && t != null; t = manager.getStyleType(t).getExtendsName()) {
			if (overrides.containsKey(t, name)) {
				r = getValueOverride(t, name);
				break;
			}
			r = getValue(t, name);
		}
		if (r == null && this.parentStyle != null) {
			r = this.parentStyle.resolveValue(type, name);
		}
		if (r == null)
			this.resolvedCache.removeMulti(type, name);
		else
			this.resolvedCache.putMulti(type, name, r);
		return r;
	}

	public String toString() {
		return "AmiWebStyle[" + id + "] " + label;
	}

	@Override
	public void addListener(String type, AmiWebStyleListener listener) {
		List<AmiWebStyleListener> li = this.listeners.getOrCreate(type);
		CH.addIdentityOrThrow(li, listener);
	}
	@Override
	public void removeListener(String type, AmiWebStyleListener listener) {
		List<AmiWebStyleListener> li = this.listeners.get(type);
		CH.removeOrThrow(li, listener);
	}

	@Override
	public void onStyleAdded(AmiWebStyle style) {
		if (OH.ne(this.parentStyleId.get(), style.getId()))
			return;

		AmiWebStyle old = this.parentStyle;
		//		this.parentStyleId = style.getId();
		this.parentStyle = (AmiWebStyleImpl) style;
		if (old != null)
			old.removeListener(null, this);
		style.addListener(null, this);
		if (bindDone)
			clearCache();
	}
	private void clearCache() {
		this.resolvedCache.clear();
	}

	@Override
	public void onStyleRemoved(AmiWebStyle style) {
		if (OH.ne(this.parentStyleId.get(), style.getId()))
			return;
		AmiWebStyle old = this.parentStyle;
		this.parentStyle = null;
		this.parentStyleId.clear();
		if (old != null)
			old.removeListener(null, this);
		clearCache();
	}
	@Override
	public void onStyleValueChanged(AmiWebStyleImpl style, String type, short key, Object old, Object nuw) {
		//		nuw = this.getVarColor(nuw);
		for (String t = type; t != null; t = manager.getStyleType(t).getExtendsName())
			if (this.values.containsKey(t, key) || this.overrides.containsKey(t, key))
				return;
		Object existing = this.resolvedCache.putMulti(type, key, nuw);
		if (OH.ne(existing, nuw))
			fireValueChanged(type, key, existing, nuw, false);
	}

	@Override
	public void close() {
		this.manager.removeListener(this);
		if (this.parentStyle != null)
			this.parentStyle.removeListener(null, this);
		this.listeners.clear();
		this.parentStyle = null;
		this.parentStyleId.clear();
		this.resolvedCache.clear();
		this.values.clear();
		this.overrides.clear();
		this.varValues.clear();
	}

	@Override
	public void onStyleLabelChanged(AmiWebStyle style, String old, String label) {
	}

	@Override
	final public boolean hasType(String type) {
		return this.values.containsKey(type);
	}

	@Override
	public Set<String> getTypes() {
		return this.values.keySet();
	}

	@Override
	public Set<Short> getDeclaredKeys(String styleType) {
		Map<Short, Object> t = this.values.get(styleType);
		return t == null ? null : t.keySet();
	}

	@Override
	public boolean inheritsFrom(String styleId) {
		if (OH.eq(styleId, this.id))
			return true;
		AmiWebStyle style = this.manager.getStyleById(this.parentStyleId.get());
		if (style != null && style.inheritsFrom(styleId))
			return true;
		return false;
	}
	@Override
	public void setParentStyle(String styleId) {
		String oldParentStyleId = this.parentStyleId.get();
		if (this.parentStyleId.set(styleId, true)) {
			updateParentStyle(oldParentStyleId);
		}
	}
	private void updateParentStyle(String oldParentStyleId) {
		AmiWebStyle i = this.parentStyle;
		if (i != null)
			i.removeListener(null, this);
		String styleId = this.parentStyleId.get();
		this.parentStyle = (AmiWebStyleImpl) this.manager.getStyleById(styleId);
		if (this.parentStyle != null)
			parentStyle.addListener(null, this);
		if (this.id != null)
			this.manager.onParentStyleChanged(this, oldParentStyleId, this.parentStyleId.get());
		clearCache();

		// Fire changed
		for (AmiWebStyleType i2 : this.manager.getTypes())
			for (Short key : i2.getKeys()) {
				String type = i2.getName();
				fireValueChanged(type, key, null, resolveValue(type, key), true);
			}
	}

	@Override
	public AmiWebStyleManager getStyleManager() {
		return this.manager;
	}

	@Override
	public String getUrl() {
		return url;
	}

	public AmiWebStyleImpl setUrl(String url) {
		this.url = url;
		return this;
	}

	@Override
	public boolean getReadOnly() {
		return isReadOnly;
	}

	@Override
	public AmiWebStyleImpl setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
		return this;
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
		Object old = resolveValue(styleType, key);
		this.overrides.putMulti(styleType, key, value);
		Object nuw = resolveValueNoCache(styleType, key);
		if (OH.ne(old, nuw))
			fireValueChanged(styleType, key, old, nuw, true);
	}

	@Override
	public Object getValueOverride(String styleType, short key) {
		return this.overrides.getMulti(styleType, key);
	}

	@Override
	public Object removeValueOverride(String styleType, short key) {
		if (!this.overrides.containsKey(styleType, key))
			return null;
		Object old = resolveValue(styleType, key);
		Object r = this.overrides.removeMulti(styleType, key);
		Object nuw = resolveValueNoCache(styleType, key);
		if (OH.ne(old, nuw))
			fireValueChanged(styleType, key, old, nuw, true);
		return r;
	}

	@Override
	public void resetOverrides() {
		if (!this.overrides.isEmpty()) {
			for (Entry<String, Map<Short, Object>> i : this.overrides.entrySet())
				for (Short k : i.getValue().keySet())
					this.resolvedCache.removeMulti(i.getKey(), k);
			this.overrides.clear();
		}
	}

	@Override
	public boolean isValueOverride(String styleType, short key) {
		return this.overrides.containsKey(styleType, key);
	}

	@Override
	public void resetParentStyleOverride() {
		String oldParentStyleId = this.parentStyleId.get();
		if (this.parentStyleId.clearOverride())
			updateParentStyle(oldParentStyleId);
	}

	@Override
	public boolean setParentStyleOverride(String id) {
		String oldParentStyleId = this.parentStyleId.get();
		if (this.parentStyleId.setOverride(id)) {
			updateParentStyle(oldParentStyleId);
			return true;
		}
		return false;
	}

	@Override
	public AmiWebCss getCss() {
		return css;
	}

	@Override
	public AmiWebStyleVars getVars() {
		return this.vars;
	}

	BasicIndexedList<String, String> varColorsCache = null;

	static private boolean isVar(Object value) {
		return value instanceof String && SH.startsWith((String) value, '$');
	}
	public Object getVarColor(Object s) {
		if (!isVar(s))
			return s;
		return getVarValues().get((String) s);
	}
	@Override
	public BasicIndexedList<String, String> getVarValues() {
		//if the cache is empty, it needs to go to the parent color cache 
		//to grab all the color variables dependency from its parent  
		if (varColorsCache != null && !varColorsCache.entrySet().isEmpty())
			return this.varColorsCache;
		this.varColorsCache = new BasicIndexedList<String, String>();
		Iterator<Entry<String, String>> iter = this.vars.getColorIterator();
		while (iter.hasNext()) {
			Entry<String, String> i = iter.next();
			this.varColorsCache.add(i.getKey(), i.getValue());
		}
		for (AmiWebStyleImpl i = this.parentStyle; i != null; i = i.getParent())
			for (Entry<String, String> entry : i.getVarValues().entrySet())
				if (!this.varColorsCache.containsKey(entry.getKey()))
					this.varColorsCache.add(entry.getKey(), entry.getValue());
		return this.varColorsCache;
	}

	private AmiWebStyleImpl getParent() {
		return this.parentStyle;
	}

	@Override
	public void onVarColorRemoved(String key) {
		if (this.varColorsCache != null) {
			this.varColorsCache = null;
			this.getManager().getService().getScriptManager().onStyleVarsChanged(this.getId());
		}
		this.manager.getService().getCustomCssManager().onVarChanged(this.id, key);
		for (AmiWebStyleListener i : this.listeners.valuesMulti())
			i.onVarColorRemoved(key);
		List<AmiWebStyleOption> t = varValues.get(key);
		if (CH.isntEmpty(t))
			for (int j = 0; j < t.size(); j++) {
				AmiWebStyleOption option = t.get(j);
				this.resolvedCache.removeMulti(option.getNamespace(), option.getKey());
				fireValueChanged(option.getNamespace(), option.getKey(), null, null, true);
			}
	}

	@Override
	public void onVarColorAdded(String key, String color) {
		if (this.varColorsCache != null) {
			this.varColorsCache = null;
			this.getManager().getService().getScriptManager().onStyleVarsChanged(this.getId());
		}
		this.manager.getService().getCustomCssManager().onVarChanged(this.id, key);
		for (AmiWebStyleListener i : this.listeners.valuesMulti())
			i.onVarColorAdded(key, color);
		List<AmiWebStyleOption> t = varValues.get(key);
		if (CH.isntEmpty(t))
			for (int j = 0; j < t.size(); j++) {
				AmiWebStyleOption option = t.get(j);
				this.resolvedCache.removeMulti(option.getNamespace(), option.getKey());
				fireValueChanged(option.getNamespace(), option.getKey(), null, key, true);
			}
	}

	@Override
	public void onVarColorUpdated(String key, String old, String color) {
		if (this.varColorsCache != null) {
			this.varColorsCache = null;
			this.getManager().getService().getScriptManager().onStyleVarsChanged(this.getId());
		}
		this.manager.getService().getCustomCssManager().onVarChanged(this.id, key);
		for (AmiWebStyleListener i : this.listeners.valuesMulti())
			i.onVarColorUpdated(key, old, color);
		List<AmiWebStyleOption> t = varValues.get(key);
		if (CH.isntEmpty(t))
			for (int j = 0; j < t.size(); j++) {
				AmiWebStyleOption option = t.get(j);
				this.resolvedCache.removeMulti(option.getNamespace(), option.getKey());
				fireValueChanged(option.getNamespace(), option.getKey(), old, key, true);
			}
	}

	@Override
	public boolean isParentStyleOverride() {
		return this.parentStyleId.isOverride();
	}
	@Override
	public Set<Short> getOverrides(String styleType) {
		if (!this.overrides.containsKey(styleType))
			return Collections.emptySet();
		return this.overrides.get(styleType).keySet();
	}

	@Override
	public boolean isVarColorUsed(String s) {
		if (CH.isntEmpty(varValues.get(s)))
			return true;
		for (AmiWebStyleListener i : this.listeners.valuesMulti())
			if (i.isVarColorUsed(s))
				return true;
		return false;
	}
}
