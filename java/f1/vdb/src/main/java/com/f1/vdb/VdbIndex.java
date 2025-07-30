
package com.f1.vdb;

import java.util.List;
import com.f1.base.Valued;
import com.f1.utils.structs.Tuple2;

public interface VdbIndex<V extends Valued, T extends VdbTransaction>
{

  String getIndexName();

  List<V> query(T txn, Object... key);

  List<Tuple2<Object, V>> queryKeyValues(T txn_);

  List<V> queryBetween(T txn, Comparable<?>[] start, Comparable<?>[] end, int maxCount, boolean reverse);

  String[] getKeyFields();

  VdbTable<V, T> getTable();

  long getRowSize();

  boolean isUnique();

}

