package com.f1.ami.web.form.queryfield;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.web.AmiWebDebugManagerImpl;
import com.f1.ami.web.AmiWebOverrideValue;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmError;
import com.f1.ami.web.dm.AmiWebDmListener;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormFieldFactory;
import com.f1.base.Column;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.BasicTable;

public abstract class AbstractDmQueryField<T extends FormPortletField<?>> extends QueryField<T> implements AmiWebDmListener {
	public static final String DISPLAY_VAL_SORT_NONE = "none";
	public static final String DISPLAY_VAL_SORT_ASC = "asc";
	public static final String DISPLAY_VAL_SORT_DESC = "desc";

	private String dmAName;
	private String columnId;
	private String valueId;
	private String dmTableName;
	private boolean dmListening = false;
	private AmiWebOverrideValue<String> displayValSortOption = new AmiWebOverrideValue<String>(AbstractDmQueryField.DISPLAY_VAL_SORT_NONE);

	public AbstractDmQueryField(AmiWebFormFieldFactory<?> factory, AmiWebQueryFormPortlet form, T field) {
		super(factory, form, field);
	}
	@Override
	public Map<String, Object> getJson(Map<String, Object> sink) {
		CH.putExcept(sink, "dsrt", this.getDisplaySortOption(false), AbstractDmQueryField.DISPLAY_VAL_SORT_NONE);
		if (this.getDmName() != null) {
			sink.put("dmid", AmiWebUtils.getRelativeAlias(getForm().getAmiLayoutFullAlias(), getDmName()));
			sink.put("dmtbid", getDmTableName());
			CH.putNoNull(sink, "dscid", getColumnId());
			sink.put("dsvid", getValueId());
		}
		return super.getJson(sink);
	}
	@Override
	public void init(Map<String, Object> initArgs) {
		super.init(initArgs);
		String dsort = CH.getOr(Caster_String.INSTANCE, initArgs, "dsrt", AbstractDmQueryField.DISPLAY_VAL_SORT_NONE);
		// START backwards compatibility
		// For text field
		Boolean ssbv = CH.getOrNoThrow(Caster_Boolean.INSTANCE, initArgs, "ssbv", null);
		if (ssbv != null && ssbv == true)
			dsort = AbstractDmQueryField.DISPLAY_VAL_SORT_ASC;

		// For select field
		Boolean ssbn = CH.getOrNoThrow(Caster_Boolean.INSTANCE, initArgs, "ssbn", null);
		if (ssbn != null && ssbn == true)
			dsort = AbstractDmQueryField.DISPLAY_VAL_SORT_ASC;
		// END backwards compatibility
		this.setDisplaySortOption(dsort, false);
		if (initArgs.containsKey("dmid")) {
			this.setDmName(AmiWebUtils.getFullAlias(this.getForm().getAmiLayoutFullAlias(), CH.getOrThrow(Caster_String.INSTANCE, initArgs, "dmid")));
			this.setDmTableName(CH.getOrThrow(Caster_String.INSTANCE, initArgs, "dmtbid"));
			this.setColumnId(CH.getOr(Caster_String.INSTANCE, initArgs, "dscid", null));
			this.setValueId(CH.getOrThrow(Caster_String.INSTANCE, initArgs, "dsvid"));
			this.bindToDatamodel();
		}
	}
	public String getDisplaySortOption(boolean isOverride) {
		return displayValSortOption.getValue(isOverride);
	}
	public void setDisplaySortOption(String displaySort, boolean isOverride) {
		this.displayValSortOption.setValue(displaySort, isOverride);
	}
	public AmiWebOverrideValue<String> getDisplayValSortOverrideObj() {
		return this.displayValSortOption;
	}
	public void setDmName(String newName) {
		if (OH.eq(dmAName, newName))
			return;
		String oldName = this.dmAName;
		if (dmAName != null && this.dmListening) {
			AmiWebDm dm = getService().getDmManager().getDmByAliasDotName(dmAName);
			if (dm != null) {
				dm.removeDmListener(this);
				this.dmListening = false;
			}
		}

		this.dmAName = newName;
		if (newName != null && oldName != null) {
			getForm().removeDmDependency(this, oldName);
			getForm().addDmDependency(this, newName);
		} else if (newName != null) {
			getForm().addDmDependency(this, newName);
		} else if (oldName != null) {
			getForm().removeDmDependency(this, oldName);
		}
	}
	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}
	public void setValueId(String valueId) {
		this.valueId = valueId;
	}
	public void setDmTableName(String name) {
		this.dmTableName = name;
	}
	public String getColumnId() {
		return columnId;
	}

	public String getDmName() {
		return dmAName;
	}
	public String getDmTableName() {
		return dmTableName;
	}

	public String getValueId() {
		return valueId;
	}

	@Override
	public void onDmDataChanged(AmiWebDm datamodel) {
		if (OH.ne(dmAName, datamodel.getAmiLayoutFullAliasDotId()))
			return;
		Table table = datamodel.getResponseTableset().getTableNoThrow(dmTableName);
		Column idCl = table == null || columnId == null ? null : table.getColumnsMap().get(columnId);
		Column vlCl = table == null || valueId == null ? null : table.getColumnsMap().get(valueId);
		if ((idCl == null && columnId != null) || (vlCl == null && valueId != null)) {
			AmiWebDebugManagerImpl dt = getService().getDebugManager();
			if (dt.shouldDebug(AmiDebugMessage.SEVERITY_WARNING)) {
				String message = "";
				if (table == null)
					message = "Underlying table not found: " + dmTableName;
				else {
					if (idCl == null && columnId != null)
						message += "Missing key column: " + columnId + "\n";
					if (vlCl == null && valueId != null)
						message += "Missing value column: " + valueId + "\n";
					message += "(Available columns in underlying data: " + CH.sort((Set) table.getColumnIds()) + "";
				}
				dt.addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_WARNING, AmiDebugMessage.TYPE_SCHEMA_MISMATCH, this.getAri(), null, "Eror populating select options",
						CH.m("Field Name", this.getName(), "Error Message", message), null));
			}
			return;
		}

		onDataChanged(table.getRows(), idCl, vlCl);

	}
	public abstract void onDataChanged(TableList rows, Column idCl, Column vlCl);

	public void bindToDatamodel() {
		if (this.dmAName != null) {
			AmiWebDm dm = getService().getDmManager().getDmByAliasDotName(dmAName);
			if (dm != null) {
				// If it's not listening to the dm, start listening
				if (!this.dmListening) {
					dm.addDmListener(this);
					this.dmListening = true;
				}
				// Trigger Data Changed
				this.onDmDataChanged(dm);
			}

		}

	}
	@Override
	public boolean hasVisiblePortletForDm(AmiWebDm datamodel) {
		return getForm().getVisible() && OH.eq(dmAName, datamodel.getAmiLayoutFullAliasDotId());
	}
	@Override
	public void onDmRunningQuery(AmiWebDm datamodel, boolean isRequery) {
	}
	@Override
	public void onDmError(AmiWebDm datamodel, AmiWebDmError error) {

	}
	@Override
	public void onDmDataBeforeFilterChanged(AmiWebDm datamodel) {
	}

	// gets called when field switching, rebuilding, hitting cancel on editor
	@Override
	public void onRemoving() {
		super.onRemoving();
		// remove listener, dm link from this dm
		setDmName(null);
	}

	@Override
	public void onDmNameChanged(String oldAliasDotName, AmiWebDm dm) {
		if (OH.eq(this.dmAName, oldAliasDotName)) {
			// TODO might be related to the issue where changing the dm name unlinks the link
			this.dmAName = dm.getAmiLayoutFullAliasDotId();
		}
	}
	public void resetOptions() {
		if (this.getDmName() != null) {
			AmiWebDm dm = getService().getDmManager().getDmByAliasDotName(this.getDmName());
			if (dm != null)
				onDmDataChanged(dm);
		} else {
			onCustomValuesChanged();
		}
	}

	private LinkedHashMap<String, String> customOptions = new LinkedHashMap<String, String>();

	public LinkedHashMap<String, String> getCustomOptions() {
		return this.customOptions;
	}
	public void setCustomOptions(LinkedHashMap<String, String> values) {
		this.customOptions.clear();
		this.customOptions.putAll(values);
		onCustomValuesChanged();
	}
	private void onCustomValuesChanged() {
		BasicTable t = new BasicTable(String.class, "i", String.class, "v");
		TableList rows = t.getRows();
		for (Entry<String, String> i : this.customOptions.entrySet())
			rows.addRow(i.getKey(), i.getValue());
		onDataChanged(rows, t.getColumn("i"), t.getColumn("v"));
	}
	static protected LinkedHashMap<String, String> toValues(Object vals) {
		if (vals instanceof Map)//backwards compatibility
			return new LinkedHashMap<String, String>((Map) vals);
		else if (vals instanceof List) {
			List<Map<String, String>> list = (List<Map<String, String>>) vals;
			LinkedHashMap<String, String> r = new LinkedHashMap<String, String>(list.size());
			for (Map<String, String> i : list) {
				String key = CH.getOrThrow(i, "k");
				String val = CH.getOrThrow(i, "v");
				r.put(key, val);
			}
			return r;
		} else
			return null;

	}
	static protected List<Map<String, String>> fromValues(LinkedHashMap<String, String> customOptions) {
		if (customOptions == null)
			return null;
		if (customOptions.isEmpty())
			return Collections.EMPTY_LIST;
		List<Map<String, String>> r = new ArrayList<Map<String, String>>(customOptions.size());
		for (Entry<String, String> i : customOptions.entrySet())
			r.add((Map) CH.m("k", i.getKey(), "v", i.getValue()));
		return r;
	}
}