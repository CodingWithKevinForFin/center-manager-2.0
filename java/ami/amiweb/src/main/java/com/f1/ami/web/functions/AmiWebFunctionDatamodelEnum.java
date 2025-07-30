package com.f1.ami.web.functions;

import java.util.logging.Logger;

import com.f1.ami.amicommon.functions.AmiWebFunctionFactory;
import com.f1.ami.web.AmiWebDesktopPortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmError;
import com.f1.ami.web.dm.AmiWebDmListener;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculatorN;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionDatamodelEnum extends AbstractMethodDerivedCellCalculatorN implements AmiWebDmListener {
	private static final Logger log = LH.get();
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("datamodelEnum", Object.class,
			"Object value,String datamodelId,String dmTableName,String idColumn,String textColumn");
	static {
		VERIFIER.addDesc(
				"THIS METHOD HAS BEEN DEPRECATED. Convernience function for converting enum keys into there display values by doing a lookup in a locally cached datamodel.");
		VERIFIER.addRetDesc("Return type is based on type of textColumn indatamodel");
		VERIFIER.addParamDesc(0, "Value to use for lookup in datamodel's idColumn");
		VERIFIER.addParamDesc(1, "id of datamodel");
		VERIFIER.addParamDesc(2, "Name of table inside datamodel");
		VERIFIER.addParamDesc(3, "Name of column inside table that will correspond to supplied value");
		VERIFIER.addParamDesc(4, "Name of column inside table that will be used for return value");
	}

	private AmiWebDm datamodel;
	private AmiWebDesktopPortlet datamodelContainer;

	private HasherMap<Object, Object> cache = new HasherMap<Object, Object>();

	private String idName;
	private String vlName;
	private final Caster<?> vlCaster;
	private final Caster<?> idCaster;
	private String datamodelTableName;

	public AmiWebFunctionDatamodelEnum(int position, DerivedCellCalculator[] params, AmiWebDesktopPortlet datamodelContainer) {
		super(position, params);
		DerivedCellCalculator datamodelNameParam = params[1];
		DerivedCellCalculator datamodelTableNameParam = params[2];
		DerivedCellCalculator idColumn = params[3];
		DerivedCellCalculator textColumn = params[4];
		if (!datamodelTableNameParam.isConst() || SH.isnt(datamodelTableNameParam.get(null)))
			throw new ExpressionParserException(position, "DatamodelTable must be a constant string value");
		if (!datamodelNameParam.isConst() || SH.isnt(datamodelNameParam.get(null)))
			throw new ExpressionParserException(position, "Datamodel must be a constant string value");
		if (!idColumn.isConst() || SH.isnt(idColumn.get(null)))
			throw new ExpressionParserException(position, "idColumn must be a constant string value");
		if (!textColumn.isConst() || SH.isnt(textColumn.get(null)))
			throw new ExpressionParserException(position, "textColumn must be a constant string value");

		String datamodelName = (String) datamodelNameParam.get(null);
		this.datamodelTableName = (String) datamodelTableNameParam.get(null);
		this.vlName = (String) textColumn.get(null);
		this.idName = (String) idColumn.get(null);
		this.datamodelContainer = datamodelContainer;
		datamodel = datamodelContainer.getService().getDmManager().getDmByAliasDotName(datamodelName);
		AmiWebDmTableSchema table = datamodel.getResponseOutSchema().getTable(datamodelTableName);
		if (table == null)
			throw new ExpressionParserException(position, "Datamodel table not found: " + datamodelTableName);
		Class<?> idType = table.getClassTypes().getType(idName);
		if (idType == null)
			throw new ExpressionParserException(position, "Datamodel table/id-column not found: " + datamodelTableName + "::" + idName);
		this.idCaster = OH.getCaster(idType);
		Class<?> vlType = table.getClassTypes().getType(vlName);
		if (vlType == null)
			throw new ExpressionParserException(position, "Datamodel table/val-column not found: " + datamodelTableName + "::" + vlName);
		this.vlCaster = OH.getCaster(vlType);
		buildCache();
		datamodel.addDmListener(this);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	private void buildCache() {
		try {
			this.cache.clear();
			Table data = datamodel.getResponseTableset().getTable(datamodelTableName);
			if (data != null) {
				Column idsCol = data.getColumnsMap().get(idName);
				Column vlsCol = data.getColumnsMap().get(vlName);
				int idsColPos = idsCol.getLocation();
				int vlsColPos = vlsCol.getLocation();
				if (idsCol != null && vlsCol != null) {
					for (Row row : data.getRows()) {
						cache.put(this.idCaster.cast(row.getAt(idsColPos)), this.vlCaster.cast(row.getAt(vlsColPos)));
					}
				}
			}
		} catch (Exception e) {
			LH.warning(log, "Error on enum for ", this, e);
		}
	}
	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public Class<?> getReturnType() {
		return vlCaster.getCastToClass();
	}

	@Override
	public Object eval(Object values[]) {
		Object k = this.idCaster.cast(values[0]);
		return cache.get(k);
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator[] params2) {
		return new AmiWebFunctionDatamodelEnum(getPosition(), params2, this.datamodelContainer);
	}

	public static class Factory implements AmiWebFunctionFactory {

		private AmiWebService service;

		public Factory(AmiWebService service) {
			this.service = service;
		}
		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionDatamodelEnum(position, calcs, this.service.getDesktop());
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

	@Override
	public void onDmDataChanged(AmiWebDm datamodel) {
		buildCache();
	}
	@Override
	public boolean hasVisiblePortletForDm(AmiWebDm datamodel) {
		return true;
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
	@Override
	public void onDmNameChanged(String oldAliasDotName, AmiWebDm dm) {
	}

}
