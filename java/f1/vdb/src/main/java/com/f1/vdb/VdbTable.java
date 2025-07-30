
package com.f1.vdb;

import java.util.List;
import java.util.Set;
import com.f1.base.Valued;
import com.f1.base.ValuedSchema;

@SuppressWarnings("unchecked")
public interface VdbTable<V extends Valued, T extends VdbTransaction>
{

  VdbIndex<V, T> createSecondaryIndex(String... fieldNames);

  VdbUniqueIndex<V, T> createSecondaryUniqueIndex(String... fieldNames);

  ValuedSchema<V> getSchema();

  void update(T txn, V valued);

  void insert(T txn, V valued);

  void upsert(T txn, V valued);

  void delete(T txn, V valued);

  List<V> queryAll(T txn);

  V queryPrimary(T txn, Object... key);

  List<V> queryPrimaryBetween(T txn, Comparable[] start, Comparable[] end, int maxCount, boolean reverse);

  Object getKey(V valued);

  void truncate(T txn);

  String getTableName();

  String[] getKeyFields();

  Set<String> getIndexNames();

  public VdbIndex<V, T> getIndex(String name);

  long getRowSize();
}
