
package com.f1.vdb.cache;

import com.f1.base.Valued;
import com.f1.vdb.VdbEvent;
import com.f1.vdb.VdbHelper;
import com.f1.vdb.VdbTransaction;
import com.f1.vdb.VdbUniqueIndex;
import com.f1.vdb.impl.BasicVdbEvent;

public class CacheVdbUniqueIndex<V extends Valued, T extends VdbTransaction> extends CacheVdbIndex<V, T> implements
    VdbUniqueIndex<V, CacheVdbTransaction<T>>
{

  public CacheVdbUniqueIndex(CacheVdbTable<V, T> database, VdbUniqueIndex<V, T> inner)
  {
    super(database, inner);
  }

  @Override
  public V queryUnique(CacheVdbTransaction<T> txn, Object... key)
  {
    V r = scanMemCacheUnique(getTable(), txn, getTable().getKeyFields(), key);
    if (r != null) return r;
    r = ((VdbUniqueIndex<V, T>)getInner()).queryUnique(txn.getInner(), key);
    if (r != null) txn.addEvent(new BasicVdbEvent(VdbEvent.TYPE_RESULT, getTable().getTableName(), VdbHelper
        .extractKey(getTable().getKeyFields(), r), r));
    return r;
  }

}
