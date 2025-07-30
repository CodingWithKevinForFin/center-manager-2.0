package com.f1.ami.center.table.persist;

import com.f1.ami.center.table.AmiRowImpl;
import com.f1.ami.center.table.AmiTable;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public interface AmiTablePersister {

	public void init(AmiTable sink);

	void onRemoveRow(AmiRowImpl row);
	void onAddRow(AmiRowImpl r);
	void onRowUpdated(AmiRowImpl sink, long updatedColumns0, long updatedColumns64);

	//return true if it can use compacting (should call saveTable afterwards
	public boolean loadTableFromPersist(CalcFrameStack sf);
	public void saveTableToPersist(CalcFrameStack sf);
	public void clear(CalcFrameStack sf);
	public void flushChanges(CalcFrameStack sf);
	public void drop(CalcFrameStack sf);

	public void onTableRename(String oldName, String name, CalcFrameStack sf);

}
