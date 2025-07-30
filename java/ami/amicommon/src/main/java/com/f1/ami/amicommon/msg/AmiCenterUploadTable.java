package com.f1.ami.amicommon.msg;

import java.util.List;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.Table;
import com.f1.base.VID;

@VID("F1.VE.ACUTB")
/**
 * an upload to run. Consider the AMI Script Example, this would result in one entry:
 * 
 * <PRE>
 *     use _OPTN="myoption" INSERT INTO mytable(col1,col2,col3) FROM SELECT * FROM SOMETABLE;
 *                                      \_____/ \____________/       \_____________________/
 *                                         |          |                        |
 *                                         |          |                        +-> data - tabular result of running this command
 *                                         |          |
 *                                         |          + -------------------------> targetColums: col1,col2,col3
 *                                         |
 *                                         +------------------------------------->  targetTable:  mytable
 * </PRE>
 */
public interface AmiCenterUploadTable extends Message {

	@PID(1)
	/**
	 * @param data
	 *            data to upload
	 */
	public void setData(Table data);
	public Table getData();

	@PID(2)
	/**
	 * @param tableName
	 *            the name of the table in the datasource to upload data into
	 */
	public void setTargetTable(String tableName);
	public String getTargetTable();

	@PID(3)
	/**
	 * @param columns
	 *            the ordered name of column to upload for. This maybe null, meaning the datasource should use the natural column names based on the target table
	 */
	public void setTargetColumns(List<String> columns);
	public List<String> getTargetColumns();//maybe null, if implicit
}
