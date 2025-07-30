package com.f1.ami.amicommon.msg;

import java.util.List;

import com.f1.base.Lockable;
import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.Table;
import com.f1.base.VID;

@VID("F1.AMI.DST")
public interface AmiDatasourceTable extends Message, Lockable {

	@PID(1)
	public String getName();
	public void setName(String name);

	@PID(4)
	public String getCustomQuery();
	public void setCustomQuery(String nameIsCustomQuery);

	@PID(8)
	public String getCustomUse();
	public void setCustomUse(String use);

	@PID(2)
	public List<AmiDatasourceColumn> getColumns();
	public void setColumns(List<AmiDatasourceColumn> columns);

	//null if ami
	@PID(3)
	public String getDatasourceName();
	public void setDatasourceName(String name);

	@PID(5)
	public Table getPreviewData();
	public void setPreviewData(Table previewData);

	@PID(7)
	public Long getPreviewTableSize();
	public void setPreviewTableSize(Long previewData);

	//null if no collection concept for datasource
	@PID(6)
	public String getCollectionName();
	public void setCollectionName(String collectionName);

	@PID(9)
	public void setCreateTableClause(String string);
	public String getCreateTableClause();

	@Override
	AmiDatasourceTable clone();

}
