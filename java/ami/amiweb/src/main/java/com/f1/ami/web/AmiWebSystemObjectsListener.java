package com.f1.ami.web;

public interface AmiWebSystemObjectsListener {

	public void onDatasourceAdded(AmiWebDatasourceWrapper gui);
	public void onDatasourceUpdated(AmiWebDatasourceWrapper gui);
	public void onDatasourceRemoved(AmiWebDatasourceWrapper gui);
	public void onTableAdded(AmiWebTableSchemaWrapper table);
	public void onTableRemoved(AmiWebTableSchemaWrapper table);
	public void onGuiClearing(AmiWebSystemObjectsManager gui);
}
