
package com.f1.vdb.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.f1.base.Valued;
import com.f1.utils.AH;
import com.f1.utils.BasicValuedComparator;
import com.f1.utils.OH;
import com.f1.utils.structs.Tuple2;
import com.f1.vdb.VdbEvent;
import com.f1.vdb.VdbHelper;
import com.f1.vdb.VdbIndex;
import com.f1.vdb.VdbTransaction;
import com.f1.vdb.impl.BasicVdbEvent;

public class CacheVdbIndex<V extends Valued, T extends VdbTransaction> implements VdbIndex<V, CacheVdbTransaction<T>>
{

  final private VdbIndex<V, T> inner;
  private CacheVdbTable<V, T> database;

  public CacheVdbIndex(CacheVdbTable<V, T> database, VdbIndex<V, T> inner)
  {
    this.inner = inner;
    this.database = database;
  }

  @Override
  public String getIndexName()
  {
    return inner.getIndexName();
  }

  @Override
  public List<V> query(CacheVdbTransaction<T> txn, Object... keys)
  {
    final List<V> memResults = scanMemCache(getTable(), txn, getKeyFields(), keys);
    final List<V> innerResults = inner.query(txn.getInner(), keys);
    return mergeResults(getTable(), txn, memResults, innerResults);
  }

  @Override
  public List<V> queryBetween(CacheVdbTransaction<T> txn, Comparable<?>[] start, Comparable<?>[] end, int maxCount,
      boolean reverse)
  {
    final List<V> memResults = new ArrayList<V>();
    final CacheVdbTable<V, T> table = getTable();

    // we need to do the scan ourselves. too complicated for the scanMemCache method
    final String[] fields = getKeyFields();
    OUTER: for (VdbEvent e : txn.getEvents())
      {
        if (!table.getTableName().equals(e.getTableName())) continue;
        final V message = (V)e.getValued();
        if ((start != null && VdbHelper.compare(fields, start, message) > 0)
            || (end != null && VdbHelper.compare(fields, end, message) < 0)) continue;
        memResults.add(message);
      }
    Collections.sort(memResults, new BasicValuedComparator(fields, AH.fill(new boolean[fields.length], !reverse)));
    final List<V> innerResults = inner.queryBetween(txn.getInner(), start, end, maxCount, reverse);
    final List<V> r = mergeResults(database, txn, memResults, innerResults);
    if (maxCount < r.size()) return r.subList(0, maxCount);
    return r;
  }

  @Override
  public List<Tuple2<Object, V>> queryKeyValues(CacheVdbTransaction<T> txn)
  {
    final List<V> memResults = new ArrayList<V>();
    final CacheVdbTable<V, T> table = getTable();

    // we need to do the scan ourselves. too complicated for the scanMemCache method
    final String[] fields = table.getKeyFields();
    OUTER: for (VdbEvent e : txn.getEvents())
      {
        if (!table.getTableName().equals(e.getTableName())) continue;
        final V message = (V)e.getValued();
        memResults.add(message);
      }

    // a little messy... extract the values portion from the inner results, this is necessary for merging
    final List<Tuple2<Object, V>> innerResults = inner.queryKeyValues(txn.getInner());
    final ArrayList<V> innerResultValues = new ArrayList<V>(innerResults.size());
    for (Tuple2<Object, V> t : innerResults)
      innerResultValues.add(t.getB());
    final List<V> r = mergeResults(database, txn, memResults, innerResultValues);
    final List<Tuple2<Object, V>> r2 = new ArrayList<Tuple2<Object, V>>(r.size());

    // next rebuild the keys
    for (V t : r)
      r2.add(new Tuple2<Object, V>(VdbHelper.extractKey(getKeyFields(), t), t));
    return r2;
  }

  @Override
  public String[] getKeyFields()
  {
    return inner.getKeyFields();
  }

  @Override
  public CacheVdbTable<V, T> getTable()
  {
    return database;
  }

  @Override
  public long getRowSize()
  {
    return inner.getRowSize();
  }

  @Override
  public boolean isUnique()
  {
    return inner.isUnique();
  }

  public static <V extends Valued> V scanMemCacheUnique(CacheVdbTable<V, ?> db, CacheVdbTransaction<?> txn,
      String[] fields, Object[] keys)
  {
    final String tableName = db.getTableName();
    OUTER: for (VdbEvent e : txn.getEvents())
      {
        if (!tableName.equals(e.getTableName())) continue;
        final V message = (V)e.getValued();
        for (int i = 0; i < fields.length; i++)
          if (OH.ne(keys[i], message.ask(fields[i]))) continue OUTER;
        return message;
      }
    return null;
  }

  public static <V extends Valued> List<V> scanMemCache(CacheVdbTable<V, ?> db, CacheVdbTransaction<?> txn,
      String[] fields, Object[] keys)
  {
    final String tableName = db.getTableName();
    ArrayList<V> r = new ArrayList<V>(1);
    OUTER: for (VdbEvent e : txn.getEvents())
      {
        if (!tableName.equals(e.getTableName())) continue;
        final V message = (V)e.getValued();
        for (int i = 0; i < fields.length; i++)
          if (OH.ne(keys[i], message.ask(fields[i]))) continue OUTER;
        r.add(message);
      }
    return r;
  }

  public static <V extends Valued> List<V> mergeResults(CacheVdbTable<V, ?> db, CacheVdbTransaction<?> txn,
      List<V> cachedResults, List<V> innerResults)
  {
    final String tableName = db.getTableName();

    // no results from inner, just return memcache.
    if (innerResults.isEmpty()) return cachedResults;

    // no results in memory, add inner results to mem cache and return inner results.
    if (cachedResults.isEmpty())
      {
        for (V t : innerResults)
          txn.addEvent(new BasicVdbEvent(VdbEvent.TYPE_RESULT, tableName, VdbHelper.extractKey(db.getKeyFields(), t), t));
        return innerResults;
      }

    // find inner results that _aren't_ in memory (based on the key) and add to memcache, combine both lists & return
    List<V> r = new ArrayList<V>(cachedResults.size() + innerResults.size());
    r.addAll(cachedResults);
    Set<Object> foundInCache = new HashSet<Object>();
    for (V t : cachedResults)
      foundInCache.add(VdbHelper.extractKey(db.getKeyFields(), t));
    for (V t : innerResults)
      if (!foundInCache.contains(VdbHelper.extractKey(db.getKeyFields(), t)))
        {
          r.add(t);
          txn.addEvent(new BasicVdbEvent(VdbEvent.TYPE_RESULT, tableName, VdbHelper.extractKey(db.getKeyFields(), t), t));
        }

    return r;
  }

  public VdbIndex<V, T> getInner()
  {
    return inner;
  }

}
