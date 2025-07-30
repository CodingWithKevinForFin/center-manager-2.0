package com.f1.ami.web.filter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.CertainKeysHasher;
import com.f1.ami.web.dm.AmiWebDmFilter;
import com.f1.ami.web.dm.portlets.AmiWebDmUtils;
import com.f1.ami.web.filter.SourceTargetHelper.SourceTargetTypesMapping;
import com.f1.ami.web.filter.SourceTargetHelper.SourceTargetValuesMap;
import com.f1.base.CalcFrame;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class AmiWebFilterLink implements Cloneable, AmiWebDmFilter {

	private static final String FORMULA_FORMULA = "formula";
	final private int id;
	private String dmAliasDotName;
	private String dmTableName;
	private String formula;
	private AmiWebService service;
	private AmiWebFilterPortlet filter;

	public AmiWebFilterLink(int id, String dmAliasDotName, String dmTableName, String formula, AmiWebFilterPortlet filter) {
		this.id = id;
		this.dmAliasDotName = dmAliasDotName;
		this.dmTableName = dmTableName;
		this.formula = formula;
		this.filter = filter;
		this.service = filter.getService();
	}

	public AmiWebFilterLink(Map<String, Object> m, AmiWebFilterPortlet filter) {
		this.id = CH.getOrThrow(Caster_Integer.PRIMITIVE, m, "id");
		this.dmAliasDotName = CH.getOrThrow(Caster_String.INSTANCE, m, "dmadn");
		this.dmTableName = CH.getOrThrow(Caster_String.INSTANCE, m, "dmtn");
		this.formula = CH.getOrThrow(Caster_String.INSTANCE, m, "f");
		this.filter = filter;
		this.service = filter.getService();
	}

	public int getId() {
		return id;
	}

	public void setDmAliasDotName(String dmAliasDotName) {
		this.dmAliasDotName = dmAliasDotName;
	}
	public String getDmTableName() {
		return dmTableName;
	}
	public void setDmTableName(String dmTableName) {
		this.dmTableName = dmTableName;
	}
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}

	public AmiWebFilterLink clone() {
		try {
			return (AmiWebFilterLink) super.clone();
		} catch (CloneNotSupportedException e) {
			throw OH.toRuntime(e);
		}
	}

	public Map<String, Object> getConfiguration() {
		return CH.m("id", id, "dmadn", dmAliasDotName, "dmtn", dmTableName, "f", formula);
	}

	@Override
	public String getTargetDmAliasDotName() {
		return this.dmAliasDotName;
	}

	@Override
	public String getTargetTableName() {
		return this.dmTableName;
	}

	@Override
	public Table filter(Table tbl) {
		if (!this.filter.hasSelectedRows(null))
			return tbl;
		Table selected = this.filter.getSelectableRows(null, AmiWebPortlet.SELECTED);
		com.f1.base.CalcTypes source = selected.getColumnTypesMapping();
		com.f1.base.CalcTypes target = tbl.getColumnTypesMapping();
		SourceTargetTypesMapping classTypes = new SourceTargetTypesMapping(AmiWebDmUtils.VARPREFIX_SOURCE, source, AmiWebDmUtils.VARPREFIX_TARGET, target);
		StringBuilder errorSink = new StringBuilder();
		DerivedCellCalculator calc = this.filter.getScriptManager().parseAmiScript(this.formula, classTypes, errorSink, this.service.getDebugManager(),
				AmiDebugMessage.TYPE_FORMULA, this.filter, FORMULA_FORMULA, false, null);
		if (errorSink.length() > 0) {

		}
		if (calc == null)
			return tbl;
		Set<Object> dependencies = new HashSet<Object>();
		SourceTargetValuesMap values = new SourceTargetValuesMap(classTypes, this.getSourcePanel().getScriptManager().getLayoutVariableValues());
		for (Object o : DerivedHelper.getDependencyIds(calc)) {
			String s = (String) o;
			if (values.isSource(s))
				dependencies.add(values.getUnderlyingName(s));
		}
		//this will ensure we're only getting rows with a unique set of values, based on the dependencies
		HasherSet<Row> sourceRows = new HasherSet<Row>(new CertainKeysHasher(AH.toArray(dependencies, Object.class)));
		for (Row r : selected.getRows())
			sourceRows.add(r);

		Row[] sourceRowsArray = AH.toArray(sourceRows, Row.class);//make inner loop fast as possible

		List<Row> keep = new ArrayList<Row>();
		TableList targetRows = tbl.getRows();
		ReusableCalcFrameStack sf = filter.getStackFrame();
		sf.reset(values);
		for (Row tr : targetRows) {
			values.resetUnderlyingTargetValues(tr);
			for (Row sr : sourceRowsArray) {
				values.resetUnderlyingSourceValues((CalcFrame) sr);
				if (Boolean.TRUE.equals(calc.get(sf))) {
					keep.add(tr);
					break;
				}
			}
		}
		if (keep.size() < targetRows.size()) {
			targetRows.clear();
			targetRows.addAll(keep);
		}
		return tbl;
	}

	public void getDependencies(Set<Object> r, com.f1.base.CalcTypes sourceTypes) {
	}

	@Override
	public AmiWebFilterPortlet getSourcePanel() {
		return this.filter;
	}

}
