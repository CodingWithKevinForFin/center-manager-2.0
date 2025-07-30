package com.f1.ami.center.sysschema;

import java.util.logging.Logger;

import javax.crypto.SecretKey;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.center.AmiCenterDatasourceThreadSafeLookup;
import com.f1.ami.center.AmiCenterState;
import com.f1.ami.center.AmiCenterUtils;
import com.f1.ami.center.table.AmiColumnWrapper;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiIndex;
import com.f1.ami.center.table.AmiIndexImpl;
import com.f1.ami.center.table.AmiPreparedQuery;
import com.f1.ami.center.table.AmiPreparedQueryCompareClause;
import com.f1.ami.center.table.AmiPreparedRow;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiRowImpl;
import com.f1.ami.center.table.AmiTable;
import com.f1.ami.center.table.AmiTableDef;
import com.f1.ami.center.table.AmiTableImpl;
import com.f1.ami.center.table.AmiTableUtils;
import com.f1.ami.center.table.index.AmiQueryFinder_Comparator;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiSchema_DATASOURCE {

	final public AmiTableImpl table;
	final public AmiColumnWrapper adapter;
	final public AmiColumnWrapper name;
	final public AmiColumnWrapper options;
	final public AmiColumnWrapper pw;
	final public AmiColumnWrapper password;
	final public AmiColumnWrapper url;
	final public AmiColumnWrapper username;
	final public AmiColumnWrapper relayId;
	final public AmiColumnWrapper permittedOverrides;
	final public AmiPreparedRow preparedRow;
	private SecretKey secretKey;
	private AmiCenterState state;
	private AmiPreparedQuery byName;
	private AmiPreparedQueryCompareClause byNameColumn;
	private static final Logger log = LH.get();

	public final AmiCenterDatasourceThreadSafeLookup fastLookup;

	public AmiSchema_DATASOURCE(AmiImdbImpl imdb, CalcFrameStack sf) {
		this.fastLookup = new AmiCenterDatasourceThreadSafeLookup(imdb.getState());

		AmiTableDef def = new AmiTableDef(AmiTableUtils.DEFTYPE_AMI, AmiConsts.TYPE_DATASOURCE);

		this.name = def.addColumn(AmiConsts.PARAM_DATASOURCE_NAME, AmiTable.TYPE_STRING, AmiCenterUtils.NO_NULL);
		this.adapter = def.addColumn(AmiConsts.PARAM_DATASOURCE_ADAPTER, AmiTable.TYPE_STRING, AmiCenterUtils.NO_NULL);
		this.url = def.addColumn(AmiConsts.PARAM_DATASOURCE_URL, AmiTable.TYPE_STRING);
		this.username = def.addColumn(AmiConsts.PARAM_DATASOURCE_USER, AmiTable.TYPE_STRING);
		this.pw = def.addColumn(AmiConsts.PARAM_DATASOURCE_PW, AmiTable.TYPE_STRING);
		this.password = def.addColumn(AmiConsts.PARAM_DATASOURCE_PASSWORD, AmiTable.TYPE_STRING);
		this.options = def.addColumn(AmiConsts.PARAM_DATASOURCE_OPTIONS, AmiTable.TYPE_STRING);
		this.relayId = def.addColumn(AmiConsts.PARAM_DATASOURCE_RELAY_ID, AmiTable.TYPE_STRING);
		this.permittedOverrides = def.addColumn(AmiConsts.PARAM_DATASOURCE_PERMITTED_OVERRIDES, AmiTable.TYPE_STRING);
		this.table = (AmiTableImpl) imdb.createTable(def, sf);
		this.table.addIndex(AmiTableUtils.DEFTYPE_SYSTEM, "pk", CH.l(name.getName()), CH.l(AmiIndexImpl.TYPE_SORT), AmiIndex.CONSTRAINT_TYPE_PRIMARY, null, sf);
		this.table.getTable().addTableListener(this.fastLookup);
		this.byName = this.table.createAmiPreparedQuery();
		this.byNameColumn = this.byName.addCompare(this.name.getInner(), AmiQueryFinder_Comparator.EQ);
		this.preparedRow = this.table.createAmiPreparedRow();
		this.state = ((AmiImdbImpl) imdb).getState();
		AmiTableUtils.setSystemPersister(imdb, table);
	}
	/**
	 * 
	 * Adds a datasource with the provided parameters to the datasource table, provided that a datasource with the provided name does not already exist.
	 * 
	 * @param state
	 * @param amiId
	 * @param adapter
	 * @param name
	 * @param options
	 * @param password
	 * @param url
	 * @param username
	 * @param session
	 * @param sf
	 * @return Returns true if a datasource with the given name does not already exist. Returns false otherwise.
	 */
	public boolean addDatasource(long amiId, String adapter, String name, String options, String encryptedPassword, String url, String username, boolean isEdit,
			String selectedName, String relayId, String permittedOverrides, CalcFrameStack sf) {

		// Check to see if row already exists in table 
		//		AmiRow curRow;
		//		String curName;

		if (!isEdit && OH.ne(selectedName, name) && findByName(name) != null) {
			LH.warning(log, "Failed to add row to datasource table: Datasource name already in use.");
		}
		if (SH.is(permittedOverrides)) {
			for (String s : SH.split(",", permittedOverrides)) {
				if (!AmiConsts.PERMITTED_DS_OVERRIDE_OPTIONS.contains(s.trim()))
					throw new RuntimeException(
							"Invalid option for Permitted Overrides: " + s.trim() + " (valid options are: " + SH.join(',', AmiConsts.PERMITTED_DS_OVERRIDE_OPTIONS) + ")");
			}
			permittedOverrides = SH.replaceAll(permittedOverrides, " ", "");
		} else
			permittedOverrides = null;

		// If amiId == -1, then we are adding a new datasource, 
		// meaning we have to generate a new amiId. 
		// If amiId != -1, then we are in edit mode, 
		// and we don't change amiId. 

		preparedRow.reset();
		preparedRow.setString(this.url, url);
		preparedRow.setString(this.options, options);
		preparedRow.setString(this.username, username);
		preparedRow.setString(this.password, encryptedPassword);
		preparedRow.setString(this.adapter, adapter);
		preparedRow.setString(this.name, name);
		preparedRow.setString(this.relayId, relayId);
		preparedRow.setString(this.permittedOverrides, permittedOverrides);
		if (OH.eq(name, AmiConsts.DATASOURCE_ADAPTER_NAME_AMI)) {
			throw new RuntimeException("Can not use reserved datasource name: " + name);
		}

		if (amiId == -1) {
			AmiRowImpl insertAmiRow = this.table.insertAmiRow(preparedRow, sf);
			if (insertAmiRow == null)
				throw new RuntimeException("Could not add datasource: " + name);
			amiId = insertAmiRow.getAmiId();
		} else {
			AmiRowImpl existing = this.table.getAmiRowByAmiId(amiId);
			if (existing == null)
				throw new RuntimeException("Row not found: " + amiId);
			if (OH.eq(existing.get(AmiConsts.PARAM_DATASOURCE_ADAPTER), AmiConsts.DATASOURCE_ADAPTER_NAME_AMI))
				throw new RuntimeException("Can not edit reserved Datasource: " + selectedName);
			this.table.updateAmiRow(amiId, preparedRow, sf);
		}
		LH.info(log, "Adding row to datasource table - Name: " + preparedRow.getString(this.name) + ", Adapter: " + preparedRow.getString(this.adapter) + ", AmiId: " + amiId);

		return true;

	}
	private AmiRow findByName(String name) {
		this.byNameColumn.setValue(name);
		AmiRow r = this.table.query(this.byName);
		return r;
	}
	public void removeDatasource(long amiId, CalcFrameStack sf) {
		AmiRow removedRow = this.table.getAmiRowByAmiId(amiId);
		String removedRowName = removedRow.getString(this.name);
		String removedRowAdapter = removedRow.getString(this.adapter);
		this.table.removeAmiRow(removedRow, sf);
		LH.info(log, "Deleting row from datasource table - Name: " + removedRowName + ", Adapter: " + removedRowAdapter + ", AmiId: " + amiId);
	}
}
