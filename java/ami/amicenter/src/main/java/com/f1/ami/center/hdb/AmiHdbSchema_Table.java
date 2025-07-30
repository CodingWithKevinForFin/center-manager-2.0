package com.f1.ami.center.hdb;

public class AmiHdbSchema_Table {

	final private String tableName;
	final private String sqlDef;
	final private byte definedByType;

	final private AmiHdbSchema_Column[] columns;
	private AmiHdbSchema_Index[] indexes;

	public AmiHdbSchema_Table(AmiHdbTable table) {
		this.tableName = table.getName();
		StringBuilder sql = new StringBuilder();
		AmiHdbSchemaUtils.generateHdbSchema(sql, table);
		this.definedByType = table.getDefType();
		this.sqlDef = sql.toString();
		this.columns = new AmiHdbSchema_Column[table.getColumnsCount()];
		for (int i = 0; i < columns.length; i++)
			this.columns[i] = new AmiHdbSchema_Column(table.getColumnAt(i));
		this.indexes = new AmiHdbSchema_Index[table.getIndexes().getSize()];
		for (int i = 0; i < indexes.length; i++)
			this.indexes[i] = new AmiHdbSchema_Index(table.getIndexes().getAt(i));
	}

	public String getSqlDef() {
		return this.sqlDef;
	}

	public String getName() {
		return tableName;
	}

	public byte getDefType() {
		return this.definedByType;
	}

	public AmiHdbSchema_Column[] getColumns() {
		return this.columns;
	}

	public AmiHdbSchema_Index[] getIndexes() {
		return this.indexes;
	}

}
