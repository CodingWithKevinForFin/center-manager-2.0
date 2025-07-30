
package com.f1.vdb.bdb;

import java.util.ArrayList;
import java.util.List;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.f1.base.Valued;
import com.f1.utils.OfflineConverter;
import com.f1.vdb.VdbException;
import com.f1.vdb.VdbUniqueIndex;

public class BdbVdbUniqueIndex<V extends Valued> extends BdbVdbIndex<V> implements VdbUniqueIndex<V, BdbVdbTransaction>
{

  public BdbVdbUniqueIndex(BdbVdbTable<V> database, String[] keys, OfflineConverter converter)
  {
    super(true, database, keys, converter);
  }

  @Override
  public V queryUnique(BdbVdbTransaction txn, Object... key)
  {
    if (txn == null) txn = BdbVdbTransaction.NONE;
    try
      {
        final Database db = getBdbSecondaryDatabase();
        Cursor cursor = db.openCursor(txn.getTransaction(), null);
        DatabaseEntry foundData = new DatabaseEntry();
        DatabaseEntry foundKey = toDbEntry(key);
        List<V> results = new ArrayList<V>(1);
        OperationStatus result = cursor.getSearchKey(foundKey, foundData, LockMode.DEFAULT);
        if (result != OperationStatus.SUCCESS)
          {
            cursor.close();
            return null;
          }
        V r = (V)BdbVdbTable.fromDbEntry(getConverter(), foundData);
        result = cursor.getNextDup(foundKey, foundData, LockMode.DEFAULT);
        cursor.close();
        if (result == OperationStatus.SUCCESS) throw new VdbException("should not return multiple rows!"
            + getIndexName());
        return r;
      }
    catch (DatabaseException e)
      {
        throw new VdbException("error querying message for field:" + getIndexName(), e);
      }
  }

}
