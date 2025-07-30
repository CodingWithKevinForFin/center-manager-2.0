package com.f1.ami.center.sysschema;

import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiDatasourcePlugin;
import com.f1.ami.center.AmiCenterState;
import com.f1.ami.center.table.AmiColumnWrapper;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiPreparedRow;
import com.f1.ami.center.table.AmiRowImpl;
import com.f1.ami.center.table.AmiTable;
import com.f1.ami.center.table.AmiTableDef;
import com.f1.ami.center.table.AmiTableImpl;
import com.f1.ami.center.table.AmiTableUtils;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiSchema_DATASOURCE_TYPE {
	private static final Logger log = LH.get();

	final public AmiTableImpl table;
	final public AmiColumnWrapper descriptions;
	final public AmiColumnWrapper i;
	final public AmiColumnWrapper classType;
	final public AmiColumnWrapper icon;
	final public AmiColumnWrapper properties;
	final public AmiPreparedRow preparedRow;
	private static final ObjectToJsonConverter JSON_CONVERTER = new ObjectToJsonConverter();
	static {
		JSON_CONVERTER.setCompactMode(true);
	}

	public AmiSchema_DATASOURCE_TYPE(AmiImdbImpl imdb, CalcFrameStack sf) {

		AmiTableDef def = new AmiTableDef(AmiTableUtils.DEFTYPE_AMI, AmiConsts.TYPE_DATASOURCE_TYPE);

		this.descriptions = def.addColumn("Description", AmiTable.TYPE_STRING);
		this.i = def.addColumn(AmiConsts.TABLE_PARAM_I, AmiTable.TYPE_STRING);
		this.classType = def.addColumn("ClassType", AmiTable.TYPE_STRING);
		this.icon = def.addColumn("Icon", AmiTable.TYPE_STRING);
		this.properties = def.addColumn("Properties", AmiTable.TYPE_STRING);

		this.table = (AmiTableImpl) imdb.createTable(def, sf);
		this.preparedRow = this.table.createAmiPreparedRow();

	}
	public void addDatasourceType(AmiCenterState state, AmiDatasourcePlugin adp, CalcFrameStack sf) {

		preparedRow.reset();
		try {
			preparedRow.setString(this.descriptions, adp.getDatasourceDescription());
			preparedRow.setString(this.i, adp.getPluginId());
			preparedRow.setString(this.classType, OH.getClassName(adp));
			preparedRow.setString(this.icon, adp.getDatasourceIcon());
			Map<String, Object> propertiesMap = CH.m(AmiDatasourcePlugin.QUOTES, adp.getDatasourceQuoteType(), AmiDatasourcePlugin.OPERATORS, adp.getDatasourceOperators(),
					AmiDatasourcePlugin.WHERE_SYNTAX, adp.getDatasourceWhereClauseSyntax(), AmiDatasourcePlugin.HELP, adp.getDatasourceHelp(), AmiDatasourcePlugin.HELP_OPTIONS,
					adp.getAvailableOptions());
			String propertiesJson = JSON_CONVERTER.objectToString(propertiesMap);
			preparedRow.setString(this.properties, propertiesJson);
		} catch (AbstractMethodError e) {
			LH.warning(log, "AmiDatasourcePlugin of type ", adp.getPluginId(), " couldn't be initialized ", e);
			throw e;
		}

		this.table.insertAmiRow(preparedRow, sf);

	}
	public AmiRowImpl findById(String id) {
		AmiRowImpl r = this.table.getAmiRow(null, id, null);
		return r;
	}
}
