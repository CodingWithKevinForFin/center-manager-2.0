
package com.f1.vdb.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.f1.base.Valued;
import com.f1.utils.CH;
import com.f1.vdb.VdbDatabase;
import com.f1.vdb.VdbTable;
import com.f1.vdb.VdbTransaction;

public abstract class AbstractVdbDatabase<T extends VdbTransaction> implements VdbDatabase<T>
{
  private final Map<String, VdbTable<?, T>> tables = new HashMap<String, VdbTable<?, T>>();
  private final String databaseName;

  public AbstractVdbDatabase(String databaseName)
  {
    this.databaseName = databaseName;
  }

  @Override
  public boolean isStillReplica()
  {
    return false;
  }

  @Override
  public <V extends Valued> VdbTable<V, T> createTable(String dbName, Class<V> clazz, String... keys)
  {
    if (tables.containsKey(dbName)) throw new RuntimeException("db already exists:" + dbName);
    VdbTable<V, T> r = createTableInner(dbName, clazz, keys);
    CH.putOrThrow(tables, dbName, r);
    return r;
  }

  abstract protected <V extends Valued> VdbTable<V, T> createTableInner(String dbName_, Class<V> clazz_,
      String... keys_);

  @Override
  public <V extends Valued> void insert(T transaction, String databaseName, V m)
  {
    VdbTable<Valued, T> table = (VdbTable<Valued, T>)getTable(databaseName);
    table.insert(transaction, m);
  }

  @Override
  public <V extends Valued> void update(T transaction, String databaseName, V m)
  {
    VdbTable<V, T> table = (VdbTable<V, T>)getTable(databaseName);
    table.update(transaction, m);
  }

  @Override
  public <V extends Valued> void delete(T transaction, String databaseName, V m)
  {
    VdbTable<V, T> table = (VdbTable<V, T>)getTable(databaseName);
    table.delete(transaction, m);
  }

  @Override
  abstract public T startTransaction();

  @Override
  abstract public void commitTransaction(T transaction_);

  @Override
  abstract public void abortTransaction(T txn_);

  @Override
  public void truncate(T txn)
  {
    for (String tableName : getTableNames())
      getTable(tableName).truncate(txn);
  }

  @Override
  public Set<String> getTableNames()
  {
    return tables.keySet();
  }

  @Override
  public VdbTable<?, T> getTable(String tableName)
  {
    return CH.getOrThrow(tables, tableName, "table not found");
  }

  @Override
  public String getDatabaseName()
  {
    return databaseName;
  }

  @Override
  public void truncate(T transaction, String tableName)
  {
    getTable(tableName).truncate(transaction);
  }

}
