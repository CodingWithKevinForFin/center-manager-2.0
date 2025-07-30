
package com.f1.vdb.bdb;

import static com.f1.vdb.bdb.BdbVdbTable.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.SecondaryConfig;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.SecondaryKeyCreator;
import com.sleepycat.je.VerifyConfig;
import com.f1.base.Valued;
import com.f1.base.ValuedSchema;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.OfflineConverter;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;
import com.f1.vdb.VdbException;
import com.f1.vdb.VdbHelper;
import com.f1.vdb.VdbIndex;

public class BdbVdbIndex<V extends Valued> implements VdbIndex<V, BdbVdbTransaction>
{

  private final String name;
  private SecondaryDatabase secondaryDatabase;
  private final String[] _keys;
  private final OfflineConverter converter;
  private final BdbVdbTable<V> table;
  private boolean isUnique;
  private String databaseName;

  public BdbVdbIndex(boolean isUnique, BdbVdbTable<V> table, String[] keys_, OfflineConverter converter)
  {
    this.isUnique = isUnique;
    this.name = SH.join(',', keys_);
    this._keys = keys_;
    this.converter = converter;
    this.table = table;
    open();
  }

  void open()
  {
    final SecondaryConfig secondaryDbCfg = new SecondaryConfig();
    secondaryDbCfg.setAllowCreate(true);
    secondaryDbCfg.setTransactional(true);
    secondaryDbCfg.setAllowPopulate(true);
    if (!isUnique()) secondaryDbCfg.setSortedDuplicates(true);
    ParamKeyCreator t;
    if (getKeyFields().length > 1) t = new MessageSecondaryCompositeKeyCreator<V>(converter, table.getSchema(), _keys);
    else t = new MessageSecondaryKeyCreator<V>(converter, table.getSchema(), _keys[0]);
    secondaryDbCfg.setKeyCreator(t);
    secondaryDbCfg.setBtreeComparator(t);
    secondaryDbCfg.setOverrideBtreeComparator(true);
    Environment env = table.getDatabase().getEnvironment();
    databaseName = table.getTableName() + "." + getIndexName();
    secondaryDatabase = env.openSecondaryDatabase(null, databaseName, table.getBdbDatabase(), secondaryDbCfg);
    secondaryDatabase.verify(new VerifyConfig());
  }

  @Override
  public String getIndexName()
  {
    return name;
  }

  @Override
  public List<V> query(BdbVdbTransaction txn_, Object... key_)
  {
    if (key_.length == 1 && key_[0] == null) return queryBetween(txn_, null, null, Integer.MAX_VALUE, false);
    if (txn_ == null) txn_ = BdbVdbTransaction.NONE;
    try
      {
        final Database db = secondaryDatabase;
        Cursor cursor = db.openCursor(txn_.getTransaction(), null);
        DatabaseEntry foundData = new DatabaseEntry();
        DatabaseEntry foundKey = toDbEntry(key_);
        List<V> results = new ArrayList<V>(1);
        OperationStatus result = cursor.getSearchKey(foundKey, foundData, LockMode.DEFAULT);
        while (result == OperationStatus.SUCCESS)
          {
            results.add((V)fromDbEntry(converter, foundData));
            result = cursor.getNextDup(foundKey, foundData, LockMode.DEFAULT);
          }
        cursor.close();
        return results;
      }
    catch (DatabaseException e_)
      {
        throw new VdbException("error querying message for field:" + getIndexName(), e_);
      }
  }

  protected DatabaseEntry toDbEntry(Object... key_)
  {
    OH.assertEq(key_.length, _keys.length);
    if (key_.length > 1) return BdbVdbTable.toDbEntry(converter, CH.l(new ArrayList<Object>(), key_));
    else return BdbVdbTable.toDbEntry(converter, key_[0]);

  }

  @Override
  public List<Tuple2<Object, V>> queryKeyValues(BdbVdbTransaction txn_)
  {
    if (txn_ == null) txn_ = BdbVdbTransaction.NONE;
    try
      {
        Cursor cursor = this.secondaryDatabase.openCursor(txn_.getTransaction(), null);
        DatabaseEntry foundData = new DatabaseEntry();
        DatabaseEntry foundKey = new DatabaseEntry();
        List<Tuple2<Object, V>> results = new ArrayList<Tuple2<Object, V>>(1);
        while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)
          {
            Object key = fromDbEntry(converter, foundKey);
            V value = (V)fromDbEntry(converter, foundData);
            results.add(new Tuple2<Object, V>(key, value));
          }
        cursor.close();
        return results;
      }
    catch (DatabaseException e_)
      {
        throw new VdbException("error querying all data", e_);
      }
  }

  @Override
  public List<V> queryBetween(BdbVdbTransaction txn_, Comparable<?>[] start_, Comparable<?>[] end_, int maxCount_,
      boolean reverse_)
  {
    if (txn_ == null) txn_ = BdbVdbTransaction.NONE;
    try
      {
        Cursor cursor = this.secondaryDatabase.openCursor(txn_.getTransaction(), null);
        DatabaseEntry foundData = new DatabaseEntry();
        DatabaseEntry foundKey;

        List<V> results = new ArrayList<V>(1);
        OperationStatus status;
        if (reverse_)
          {
            if (end_ == null) status = cursor.getPrev(foundKey = new DatabaseEntry(), foundData, LockMode.DEFAULT);
            else
              {
                status = cursor.getSearchKeyRange(foundKey = toDbEntry(end_), foundData, LockMode.DEFAULT);
                if (status == OperationStatus.SUCCESS)// because bdb returns the closest match AFTER the key.
                  {
                    V result = (V)fromDbEntry(converter, foundData);
                    if (compare(end_, result) < 0) status = cursor.getPrev(foundKey, foundData, LockMode.DEFAULT);
                  }
              }
          }
        else
          {
            if (start_ == null) status = cursor.getNext(foundKey = new DatabaseEntry(), foundData, LockMode.DEFAULT);
            else status = cursor.getSearchKeyRange(foundKey = toDbEntry(start_), foundData, LockMode.DEFAULT);
          }
        int count = 0;
        while (status == OperationStatus.SUCCESS)
          {
            V result = (V)fromDbEntry(converter, foundData);
            if (reverse_)
              {
                if (start_ != null && compare(start_, result) > 0) break;
                status = cursor.getPrev(foundKey, foundData, LockMode.DEFAULT);
              }
            else
              {
                if (end_ != null && compare(end_, result) < 0) break;
                status = cursor.getNext(foundKey, foundData, LockMode.DEFAULT);
              }
            results.add(result);
            if (++count == maxCount_) break;
          }
        cursor.close();
        return results;
      }
    catch (DatabaseException e_)
      {
        throw new VdbException("error querying all data", e_);
      }
  }

  public int compare(Comparable[] values, V result)
  {
    return VdbHelper.compare(getKeyFields(), values, result);
  }

  public void truncate(BdbVdbTransaction txn)
  {
    if (txn == null) txn = BdbVdbTransaction.NONE;
    getTable().getDatabase().getEnvironment().truncateDatabase(txn.getTransaction(), databaseName, false);
  }

  @Override
  public String[] getKeyFields()
  {
    return _keys;
  }

  public OfflineConverter getConverter()
  {
    return converter;
  }

  @Override
  public BdbVdbTable<V> getTable()
  {
    return table;
  }

  private static class MessageSecondaryKeyCreator<V extends Valued> implements ParamKeyCreator
  {

    private static final long serialVersionUID = 1L;
    private final String param;
    private OfflineConverter converter;
    private final Class<? extends Comparable<?>> paramType;

    public MessageSecondaryKeyCreator(OfflineConverter converter, ValuedSchema<?> schema, String param)
    {
      this.param = param;
      this.converter = converter;
      this.paramType = (Class<? extends Comparable<?>>)schema.askClass(param);
    }

    @Override
    public boolean createSecondaryKey(SecondaryDatabase db, DatabaseEntry key, DatabaseEntry data, DatabaseEntry out)

    {
      V message = (V)fromDbEntry(converter, data);
      out.setData(converter.object2Bytes(message.ask(param)));
      return true;
    }

    @Override
    public String[] getParams()
    {
      return new String[]
        { param };
    }

    @Override
    public int compare(byte[] o1, byte[] o2)
    {
      Comparable<?> l = OH.cast(converter.bytes2Object(o1), paramType);
      Comparable<?> r = OH.cast(converter.bytes2Object(o2), paramType);
      return OH.compare(l, r);
    }
  }

  private static class MessageSecondaryCompositeKeyCreator<V extends Valued> implements ParamKeyCreator

  {
    private static final long serialVersionUID = 1L;
    private final String[] params;
    private final OfflineConverter converter;
    private final Class<? extends Comparable<?>>[] types;

    public MessageSecondaryCompositeKeyCreator(OfflineConverter converter, ValuedSchema<V> schema, String[] params)
    {
      this.converter = converter;
      this.params = params;
      this.types = new Class[params.length];
      for (int i = 0; i < params.length; i++)
        this.types[i] = (Class<? extends Comparable<?>>)schema.askClass(params[i]);
    }

    @Override
    public boolean createSecondaryKey(SecondaryDatabase db, DatabaseEntry key, DatabaseEntry data, DatabaseEntry out)

    {
      V message = (V)fromDbEntry(converter, data);
      List<Object> values = new ArrayList<Object>(params.length);
      for (String param : params)
        values.add(message.ask(param));
      out.setData(converter.object2Bytes(values));
      return true;
    }

    @Override
    public String[] getParams()
    {
      return params;
    }

    @Override
    public int compare(byte[] o1, byte[] o2)
    {
      try
        {
          List<Comparable<?>> l = (List<Comparable<?>>)converter.bytes2Object(o1);
          List<Comparable<?>> r = (List<Comparable<?>>)converter.bytes2Object(o2);
          for (int i = 0; i < l.size(); i++)
            {
              int j = OH.compare(OH.cast(l.get(i), types[i]), OH.cast(r.get(i), types[i]));
              if (j != 0) return j;
            }
          return 0;
        }
      catch (RuntimeException e)
        {
          throw new RuntimeException("Error for index: [" + SH.join(',', params) + "]", e);
        }
    }

  }

  private interface ParamKeyCreator extends SecondaryKeyCreator, Comparator<byte[]>, Serializable
  {
    String[] getParams();
  }

  @Override
  public long getRowSize()
  {
    return secondaryDatabase.count();
  }

  @Override
  public boolean isUnique()
  {
    return isUnique;
  }

  public Database getBdbSecondaryDatabase()
  {
    return secondaryDatabase;
  }

  public void close()
  {
    secondaryDatabase.close();
  }

}
