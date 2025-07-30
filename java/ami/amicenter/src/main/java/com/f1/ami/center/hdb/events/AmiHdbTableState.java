package com.f1.ami.center.hdb.events;

import java.util.ArrayList;
import java.util.List;

import com.f1.ami.center.hdb.AmiHdbTable;
import com.f1.container.Partition;
import com.f1.container.impl.BasicState;
import com.f1.utils.SH;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiHdbTableState extends BasicState {

	final private StringBuilder tmpBuffer = new StringBuilder();
	final private List listBuffer = new ArrayList();
	private AmiHdbTable table;
	private String tableName;
	private CalcFrameStack stackFrame;

	public AmiHdbTable getTable() {
		return this.table;
	}

	public ColumnarTable getTmpTable() {
		return this.getTable().getTableBuffer();
	}

	public StringBuilder getTmpBuf() {
		return this.tmpBuffer;
	}

	public void setTable(AmiHdbTable table) {
		this.table = table;
		table.setState(this);
	}
	@Override
	public void setPartition(Partition partition) {
		super.setPartition(partition);
		this.tableName = SH.stripPrefix((String) partition.getPartitionId(), AmiHdbTable.F1PARTITION_PREFIX, false);
	}

	public String getTableName() {
		return this.tableName;
	}

	public List getListBuffer() {
		return listBuffer;
	}

	public CalcFrameStack getStackFrame() {
		return this.stackFrame;
	}
}
