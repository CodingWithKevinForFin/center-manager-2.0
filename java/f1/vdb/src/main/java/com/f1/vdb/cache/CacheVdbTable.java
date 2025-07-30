
package com.f1.vdb.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.f1.base.Valued;
import com.f1.base.ValuedSchema;
import com.f1.utils.AH;
import com.f1.utils.BasicValuedComparator;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.vdb.VdbEvent;
import com.f1.vdb.VdbHelper;
import com.f1.vdb.VdbIndex;
import com.f1.vdb.VdbTable;
import com.f1.vdb.VdbTransaction;
import com.f1.vdb.impl.BasicVdbEvent;

public class CacheVdbTable<V extends Valued, T extends VdbTransaction> implements
    VdbTable<V, com.f1.vdb.cache.CacheVdbTransaction<T>>
{

  private VdbTable<V, T> inner;
  private Map<String, VdbIndex<V, CacheVdbTransaction<T>>> indexes = new HashMap<String, VdbIndex<V, CacheVdbTransaction<T>>>();

  public CacheVdbTable(VdbTable<V, T> inner)
  {
    this.inner = inner;
  }

  @Override
  public CacheVdbIndex<V, T> createSecondaryIndex(String... fieldNames)
  {
    return new CacheVdbIndex<V, T>(this, inner.createSecondaryIndex(fieldNames));
  }

  @Override
  public CacheVdbUniqueIndex<V, T> createSecondaryUniqueIndex(String... fieldNames)
  {
    return new CacheVdbUniqueIndex<V, T>(this, inner.createSecondaryUniqueIndex(fieldNames));
  }

  @Override
  public ValuedSchema<V> getSchema()
  {
    return inner.getSchema();
  }

  @Override
  public void insert(CacheVdbTransaction<T> transaction, V valued)
  {
    transaction.addEvent(new BasicVdbEvent(VdbEvent.TYPE_INSERT, getTableName(), getKey(valued), valued));
  }

  @Override
  public void update(CacheVdbTransaction<T> transaction, V valued)
  {
    transaction.addEvent(new BasicVdbEvent(VdbEvent.TYPE_UPDATE, getTableName(), getKey(valued), valued));
  }

  @Override
  public void upsert(CacheVdbTransaction<T> transaction, V valued)
  {
    Object key = getKey(valued);
    VdbEvent event = transaction.getEvent(getTableName(), key);
    if (event == null || event.getType() == VdbEvent.TYPE_DELETE)
      {
        transaction.addEvent(new BasicVdbEvent(VdbEvent.TYPE_INSERT, getTableName(), key, valued));
      }
    else
      {
        transaction.addEvent(new BasicVdbEvent(VdbEvent.TYPE_UPDATE, getTableName(), key, valued));
      }
  }

  @Override
  public void delete(CacheVdbTransaction<T> transaction, V valued)
  {
    transaction.addEvent(new BasicVdbEvent(VdbEvent.TYPE_DELETE, getTableName(), getKey(valued), valued));
  }

  @Override
  public List<V> queryAll(CacheVdbTransaction<T> txn)
  {
    final List<V> memResults = CacheVdbIndex.scanMemCache(this, txn, OH.EMPTY_STRING_ARRAY, null);
    final List<V> bdbResults = inner.queryAll(txn.getInner());
    return CacheVdbIndex.mergeResults(this, txn, memResults, bdbResults);
  }

  @Override
  public V queryPrimary(CacheVdbTransaction<T> txn, Object... key)
  {
    V r = CacheVdbIndex.scanMemCacheUnique(this, txn, this.getKeyFields(), key);
    if (r != null) return r;
    r = inner.queryPrimary(txn.getInner(), key);
    if (r != null) txn.addEvent(new BasicVdbEvent(VdbEvent.TYPE_RESULT, this.getTableName(), getKey(r), r));
    return r;
  }

  @Override
  public List<V> queryPrimaryBetween(CacheVdbTransaction<T> txn, Comparable[] start, Comparable[] end, int maxCount,
      boolean reverse)
  {
    final List<V> memResults = new ArrayList<V>();

    // we need to do the scan ourselves. too complicated for the scanMemCache method
    final String[] fields = this.getKeyFields();
    OUTER: for (VdbEvent e : txn.getEvents())
      {
        if (!this.getTableName().equals(e.getTableName())) continue;
        final V message = (V)e.getValued();
        if ((start != null && VdbHelper.compare(getKeyFields(), start, message) > 0)
            && (end != null && VdbHelper.compare(getKeyFields(), end, message) < 0)) continue;
        memResults.add(message);
      }
    Collections.sort(memResults, new BasicValuedComparator(fields, AH.fill(new boolean[fields.length], !reverse)));
    final List<V> innerResults = inner.queryPrimaryBetween(txn.getInner(), start, end, maxCount, reverse);
    final List<V> r = CacheVdbIndex.mergeResults(this, txn, memResults, innerResults);
    if (maxCount < r.size()) return r.subList(0, maxCount);
    return r;
  }

  @Override
  public Object getKey(V message)
  {
    return inner.getKey(message);
  }

  @Override
  public void truncate(CacheVdbTransaction<T> transaction)
  {
    inner.truncate(transaction.getInner());
    for (VdbEvent e : transaction.getEvents())
      if (e.getTableName().equals(getTableName()) && e.getType() != VdbEvent.TYPE_DELETE) transaction
          .addEvent(new BasicVdbEvent(VdbEvent.TYPE_DELETE, e.getTableName(), getKey((V)e.getValued()), e.getValued()));

  }

  @Override
  public String getTableName()
  {
    return inner.getTableName();
  }

  @Override
  public String[] getKeyFields()
  {
    return inner.getKeyFields();
  }

  @Override
  public Set<String> getIndexNames()
  {
    return inner.getIndexNames();
  }

  @Override
  public VdbIndex<V, CacheVdbTransaction<T>> getIndex(String name)
  {
    return CH.getOrThrow(indexes, name);
  }

  @Override
  public long getRowSize()
  {
    return inner.getRowSize();
  }

  public VdbTable<V, T> getInner()
  {
    return inner;

  }

}
