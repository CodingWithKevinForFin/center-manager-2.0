
package com.f1.vdb;

import java.util.Set;
import com.f1.base.Valued;

public interface VdbDatabase<T extends VdbTransaction>
{

  String getDatabaseName();

  boolean isStillReplica();

  <V extends Valued> VdbTable<V, T> createTable(String tableName, Class<V> clazz, String... keys);

  <V extends Valued> void insert(T transaction, String tableName, V m);

  <V extends Valued> void update(T transaction, String tableName, V m);

  <V extends Valued> void delete(T transaction, String tableName, V m);

  T startTransaction();

  void commitTransaction(T transaction);

  void abortTransaction(T transaction);

  void truncate(T transaction);

  void truncate(T transaction, String tableName);

  Set<String> getTableNames();

  public VdbTable<?, T> getTable(String tableName);

}
