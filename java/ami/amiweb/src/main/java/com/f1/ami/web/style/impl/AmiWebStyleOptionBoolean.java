package com.f1.ami.web.style.impl;

import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.base.Caster;
import com.f1.base.LockedException;
import com.f1.utils.casters.Caster_Boolean;

public class AmiWebStyleOptionBoolean extends AmiWebStyleOption {

	final private String falseLabel;
	final private String trueLabel;
	private boolean showFalseFirst;
	private String falseStyle;
	private String trueStyle;

	public String getFalseLabel() {
		return falseLabel;
	}

	public String getTrueLabel() {
		return trueLabel;
	}

	public AmiWebStyleOptionBoolean(short key, String saveKey, String namespace, String groupLabel, String label, String trueLabel, String falseLabel) {
		super(key, saveKey, namespace, groupLabel, label, AmiWebStyleConsts.TYPE_BOOLEAN);
		this.trueLabel = trueLabel;
		this.falseLabel = falseLabel;
	}

	public AmiWebStyleOptionBoolean setFalseStyle(String string) {
		LockedException.assertNotLocked(this);
		this.falseStyle = string;
		return this;
	}
	public AmiWebStyleOptionBoolean setTrueStyle(String string) {
		LockedException.assertNotLocked(this);
		this.trueStyle = string;
		return this;
	}

	public AmiWebStyleOptionBoolean setShowFalseFirst(boolean b) {
		LockedException.assertNotLocked(this);
		this.showFalseFirst = b;
		return this;
	}

	public boolean getShowFalseFirst() {
		return this.showFalseFirst;
	}

	public String getFalseStyle() {
		return this.falseStyle;
	}
	public String getTrueStyle() {
		return this.trueStyle;
	}

	@Override
	public Object toInternalStorageValue(AmiWebService service, Object value) {
		return Caster_Boolean.INSTANCE.cast(value);
	}

	@Override
	public AmiWebStyleOptionBoolean copy() {
		AmiWebStyleOptionBoolean r = new AmiWebStyleOptionBoolean(getKey(), this.getSaveKey(), this.getNamespace(), getGroupLabel(), getLabel(), this.getTrueLabel(),
				getFalseLabel());
		copyFields(r);
		r.setFalseStyle(this.getFalseStyle());
		r.setTrueStyle(this.getTrueStyle());
		return r;

	}

	@Override
	public Caster<?> getCaster() {
		return Caster_Boolean.INSTANCE;
	}
}
