
package com.f1.vdb.cache;

import com.f1.base.Valued;
import com.f1.vdb.VdbDatabase;
import com.f1.vdb.VdbEvent;
import com.f1.vdb.VdbTable;
import com.f1.vdb.VdbTransaction;
import com.f1.vdb.impl.AbstractVdbDatabase;

public class CacheVdbDatabase<T extends VdbTransaction> extends AbstractVdbDatabase<CacheVdbTransaction<T>>
{

  private VdbDatabase<T> inner;

  public CacheVdbDatabase(VdbDatabase<T> inner)
  {
    super(inner.getDatabaseName());
    this.inner = inner;
  }

  @Override
  public boolean isStillReplica()
  {
    return inner.isStillReplica();
  }

  @Override
  public <V extends Valued> CacheVdbTable<V, T> createTable(String dbName, Class<V> clazz, String... keys)
  {
    return (CacheVdbTable<V, T>)super.createTable(dbName, clazz, keys);
  }

  @Override
  protected <V extends Valued> VdbTable<V, CacheVdbTransaction<T>> createTableInner(String tableName, Class<V> clazz,
      String... keys)
  {
    return new CacheVdbTable<V, T>(inner.createTable(tableName, clazz, keys));
  }

  @Override
  public CacheVdbTransaction<T> startTransaction()
  {
    return new CacheVdbTransaction<T>(inner.startTransaction());
  }

  @Override
  public void commitTransaction(CacheVdbTransaction<T> transaction)
  {
    for (VdbEvent event : transaction.getEvents())
      {
        switch (event.getType())
          {
          case VdbEvent.TYPE_INSERT:
            inner.insert(transaction.getInner(), event.getTableName(), event.getValued());
            continue;
          case VdbEvent.TYPE_UPDATE:
            inner.update(transaction.getInner(), event.getTableName(), event.getValued());
            continue;
          case VdbEvent.TYPE_DELETE:
            inner.delete(transaction.getInner(), event.getTableName(), event.getValued());
            continue;
          case VdbEvent.TYPE_RESULT:
            continue;
          default:
            throw new RuntimeException("unknown type for event:" + event);
          }
      }
    inner.commitTransaction(transaction.getInner());
  }

  @Override
  public void abortTransaction(CacheVdbTransaction<T> transaction)
  {
    inner.abortTransaction(transaction.getInner());
  }

  public VdbDatabase<T> getInner()
  {
    return inner;
  }

}
