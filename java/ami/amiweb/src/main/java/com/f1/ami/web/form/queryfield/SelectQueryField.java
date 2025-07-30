package com.f1.ami.web.form.queryfield;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.f1.ami.web.dm.AmiWebDmListener;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormSelectFieldFactory;
import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.TableList;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.casters.Caster_String;

public class SelectQueryField extends AbstractDmQueryField<FormPortletSelectField<String>> implements AmiWebDmListener {

	public SelectQueryField(AmiWebFormSelectFieldFactory factory, AmiWebQueryFormPortlet form) {
		super(factory, form, new FormPortletSelectField(String.class, ""));
	}

	@Override
	public void init(Map<String, Object> initArgs) {
		super.init(initArgs);
		if (getDmName() == null) {
			LinkedHashMap<String, String> options = toValues(CH.getOr(Caster_Simple.OBJECT, initArgs, "v", null));
			if (options != null)
				setCustomOptions(options);
		}
	}

	@Override
	public Map<String, Object> getJson(Map<String, Object> sink) {
		if (this.getDmName() == null)
			sink.put("v", fromValues(getCustomOptions()));
		return super.getJson(sink);
	}

	@Override
	public void onDataChanged(TableList rows, Column idCl, Column vlCl) {
		//Grab the current value and clear options 
		FormPortletSelectField<Object> f = (FormPortletSelectField) getField();
		Object currentValue = f.getValue();
		f.clearOptions();

		//Get all options from tablelist
		int idCol = idCl.getLocation();
		int vlCol = vlCl.getLocation();

		Set<Object> added = new HashSet<Object>();
		Caster<Object> c = OH.getCaster(f.getType());
		for (Row row : rows) {
			Object key = row.getAt(idCol, c);

			if (added.add(key))
				f.addOption(key, row.getAt(vlCol, Caster_String.INSTANCE));
		}
		if (AbstractDmQueryField.DISPLAY_VAL_SORT_ASC.equals(getDisplaySortOption(false)))
			f.sortOptionsByName();
		else if (AbstractDmQueryField.DISPLAY_VAL_SORT_DESC.equals(getDisplaySortOption(false)))
			f.sortOptionsByNameDesc();

		AmiWebQueryFormPortlet queryForm = getForm();
		if (!f.setValueNoThrow(currentValue)) {
			if (f.getOptionsCount() > 0) {
				f.setValue(f.getOptions().iterator().next().getKey());
				queryForm.putPortletVar(getName(), getValue(0), getVarTypeAt(0));
			} else {
				queryForm.putPortletVar(getName(), getValue(0), false, getVarTypeAt(0));
			}
		} else {
			queryForm.putPortletVar(getName(), currentValue, getVarTypeAt(0));
		}
	}

	@Override
	public void setDisplaySortOption(String displaySort, boolean isOverride) {
		if (getDisplayValSortOverrideObj().setValue(displaySort, isOverride)) {
			super.setDisplaySortOption(displaySort, isOverride);
			if (AbstractDmQueryField.DISPLAY_VAL_SORT_ASC.equals(displaySort))
				getField().sortOptionsByName();
			else if (AbstractDmQueryField.DISPLAY_VAL_SORT_DESC.equals(displaySort))
				getField().sortOptionsByNameDesc();
			else
				this.resetOptions();
		}
	}
}
