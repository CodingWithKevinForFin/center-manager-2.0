package com.f1.ami.web.style.impl;

import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.base.Caster;
import com.f1.utils.MH;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Integer;

public class AmiWebStyleOptionRange extends AmiWebStyleOption {

	final private int min;
	final private int max;

	public AmiWebStyleOptionRange(short key, String saveKey, String namespace, String groupLabel, String label, int min, int max) {
		super(key, saveKey, namespace, groupLabel, label, AmiWebStyleConsts.TYPE_NUMBER);
		this.min = min;
		this.max = max;
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	@Override
	public Object toInternalStorageValue(AmiWebService service, Object value) {
		Double n = Caster_Double.INSTANCE.cast(value, false, false);
		if (n == null)
			return null;
		return MH.clip((double) n, min, max);
	}

	@Override
	public AmiWebStyleOptionRange copy() {
		AmiWebStyleOptionRange r = new AmiWebStyleOptionRange(getKey(), getSaveKey(), getNamespace(), getGroupLabel(), getLabel(), min, max);
		copyFields(r);
		return r;
	}
	@Override
	public Caster<?> getCaster() {
		return Caster_Integer.INSTANCE;
	}

}
