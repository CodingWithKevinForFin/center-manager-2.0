package com.f1.ami.web.style.impl;

import java.util.LinkedHashMap;

import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.base.Caster;
import com.f1.base.LockedException;
import com.f1.utils.OH;
import com.f1.utils.OneToOne;
import com.f1.utils.casters.Caster_String;

public class AmiWebStyleOptionChoices extends AmiWebStyleOption {

	final private Class<?> valueType;
	final private Caster<?> caster;
	final private OneToOne<Object, String> optionsToDisplay = new OneToOne<Object, String>(new LinkedHashMap(), new LinkedHashMap());
	final private OneToOne<Object, String> optionsToSave = new OneToOne<Object, String>(new LinkedHashMap(), new LinkedHashMap());
	private boolean useSelect;
	private int minButtonWidth = -1;

	public AmiWebStyleOptionChoices(short key, String saveKey, String namespace, String groupLabel, String label, Class<?> classType) {
		super(key, saveKey, namespace, groupLabel, label, AmiWebStyleConsts.TYPE_ENUM);
		this.valueType = classType;
		this.caster = OH.getCaster(this.valueType);
	}

	public OneToOne<Object, String> getOptionsToDisplayValue() {
		return optionsToDisplay;
	}
	public OneToOne<Object, String> getOptionsToSaveValue() {
		return optionsToSave;
	}

	public AmiWebStyleOptionChoices addOption(Object key, String saveValue, String displayValue) {
		LockedException.assertNotLocked(this);

		if (this.optionsToDisplay.containsKey(saveValue))
			throw new RuntimeException("Values across display/save must be unique: " + saveValue);
		if (this.optionsToSave.containsKey(displayValue))
			throw new RuntimeException("Values across display/save must be unique: " + displayValue);

		if (this.optionsToDisplay.containsValue(saveValue))
			throw new RuntimeException("Values across display/save must be unique: " + saveValue);
		if (this.optionsToDisplay.containsValue(key))
			throw new RuntimeException("Values across display/save must be unique: " + displayValue);

		if (this.optionsToSave.containsValue(displayValue))
			throw new RuntimeException("Values across display/save must be unique: " + displayValue);
		if (this.optionsToSave.containsValue(key))
			throw new RuntimeException("Values across display/save must be unique: " + displayValue);

		this.optionsToSave.put(key, saveValue);
		this.optionsToDisplay.put(key, displayValue);
		return this;
	}

	public AmiWebStyleOptionChoices setMinButtonWidth(int i) {
		this.minButtonWidth = i;
		return this;
	}

	public AmiWebStyleOptionChoices setUseSelect(boolean b) {
		LockedException.assertNotLocked(this);
		this.useSelect = b;
		return this;
	}

	public Class<?> getValueType() {
		return this.valueType;
	}

	public boolean isUseSelect() {
		return useSelect;
	}

	public int getMinButtonWidth() {
		return minButtonWidth;
	}

	@Override
	public Object toExportValue(AmiWebService service, Object value) {
		return this.optionsToSave.getValue(value);
	}
	@Override
	public Object toAmiscriptValue(AmiWebService service, Object value) {
		return this.optionsToSave.getValue(value);
	}
	@Override
	public Object toInternalStorageValue(AmiWebService service, Object value) {
		String cast = Caster_String.INSTANCE.cast(value, false, false);
		if (cast == null)
			return null;
		Object r = this.optionsToSave.getKey(cast);
		if (r == null)
			r = this.optionsToDisplay.getKey(cast);
		if (r == null) {
			Object val2 = OH.cast(value, this.valueType, false, false);
			if (this.optionsToSave.containsKey(val2))
				r = val2;
		}
		return r;
	}

	@Override
	public AmiWebStyleOptionChoices copy() {
		AmiWebStyleOptionChoices r = new AmiWebStyleOptionChoices(getKey(), this.getSaveKey(), getNamespace(), getGroupLabel(), getLabel(), this.getValueType());
		copyFields(r);
		r.setMinButtonWidth(this.getMinButtonWidth());
		r.setUseSelect(this.useSelect);
		r.optionsToDisplay.putAll(this.optionsToDisplay.getInnerKeyValueMap());
		r.optionsToSave.putAll(this.optionsToSave.getInnerKeyValueMap());
		return r;
	}

	@Override
	public Caster<?> getCaster() {
		return this.caster;
	}
}
