
package com.f1.vdb.bdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.UniqueConstraintException;
import com.f1.base.BasicTypes;
import com.f1.utils.DetailedException;
import com.f1.base.Valued;
import com.f1.base.ValuedSchema;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.OfflineConverter;
import com.f1.utils.SH;
import com.f1.vdb.VdbEvent;
import com.f1.vdb.VdbException;
import com.f1.vdb.VdbHelper;
import com.f1.vdb.VdbTable;
import com.f1.vdb.impl.BasicVdbEvent;

@SuppressWarnings("unchecked")
public class BdbVdbTable<V extends Valued> implements VdbTable<V, BdbVdbTransaction>
{
  private static final Logger log = Logger.getLogger(BdbVdbTable.class.getName());

  private final ValuedSchema<V> schema;
  private final String[] keyFields;
  private OfflineConverter converter;
  private Database database;
  private final String databaseName;
  private final Map<String, BdbVdbIndex<V>> indexesByName = new HashMap<String, BdbVdbIndex<V>>();

  private boolean isOpen;

  private BdbVdbDatabase bdbVdbDatabase;

  public BdbVdbTable(BdbVdbDatabase environment, ValuedSchema<V> schema, String[] keyFields, OfflineConverter converter)
  {
    this(null, environment, schema, keyFields, converter);
  }

  public BdbVdbTable(String databaseName, BdbVdbDatabase environment, ValuedSchema<V> schema, String[] keyFields,
      OfflineConverter converter)
  {
    this.bdbVdbDatabase = environment;
    this.schema = schema;
    this.keyFields = keyFields;
    this.converter = converter;
    this.databaseName = OH.noNull(databaseName, schema.askOriginalType().getSimpleName());

    for (String k : this.keyFields)
      verifyField(k);
   LH.info(log,"Creating bdb database:" , this.databaseName , " key=" , SH.join(',', this.keyFields));
    open();
  }

  private void verifyField(String keyField)
  {
    if (schema.askBasicType(keyField) == BasicTypes.UNDEFINED) throw new RuntimeException("invalid key for '"
        + databaseName + "':" + keyField);
    if (!Comparable.class.isAssignableFrom(OH.getBoxed(schema.askClass(keyField)))) throw new RuntimeException(
        "type must be Comparable for '" + databaseName + "':" + keyField);
  }

  @Override
  public BdbVdbIndex<V> createSecondaryIndex(String... fieldNames)
  {
    return createSecondaryIndex(false, fieldNames);
  }

  @Override
  public BdbVdbUniqueIndex<V> createSecondaryUniqueIndex(String... fieldNames)
  {
    return (BdbVdbUniqueIndex<V>)createSecondaryIndex(true, fieldNames);
  }

  private BdbVdbIndex<V> createSecondaryIndex(boolean unique, String... fieldNames)
  {
    try
      {
        for (String fieldName : fieldNames)
          verifyField(fieldName);
        String secondaryKey = SH.join(',', fieldNames);
        for (String keyPart : fieldNames)
          {
            if (schema.askBasicType(keyPart) == BasicTypes.UNDEFINED) throw new RuntimeException(
                "invalid secondary key for '" + databaseName + "':" + secondaryKey + " (uknown field:" + keyPart + ")");
          }
        BdbVdbIndex<V> r;
        if (unique) r = new BdbVdbUniqueIndex<V>(this, fieldNames, converter);
        else r = new BdbVdbIndex<V>(false, this, fieldNames, converter);
        CH.putOrThrow(indexesByName, r.getIndexName(), r);
        return r;
      }
    catch (Exception e)
      {
        throw new VdbException("error with creating index " + getTableName() + ":" + SH.join(",", fieldNames)
            + "  unique=" + unique, e);
      }
  }

  @Override
  public ValuedSchema<V> getSchema()
  {
    return schema;
  }

  private static final Pattern PATTERN_INSERT_ERROR = Pattern
      .compile("Could not insert secondary key in (.*)\\.(.*) OperationStatus.KEYEXIST");

  @Override
  public void update(BdbVdbTransaction txn, V message)
  {
    Object[] keys = new Object[keyFields.length];
    for (int i = 0; i < keyFields.length; i++)
      keys[i] = message.ask(keyFields[i]);
    V r = queryPrimary(txn, keys);
    if (r == null) throw new VdbException("key not found for " + databaseName + ":" + SH.join(',', keyFields) + ":["
        + getKey(message) + "]");
    put(txn, message, true);
  }

  @Override
  public void insert(BdbVdbTransaction txn, V message)
  {
    put(txn, message, false);
  }

  @Override
  public void upsert(BdbVdbTransaction txn, V message)
  {
    put(txn, message, true);
  }

  private void put(BdbVdbTransaction txn, V message, boolean allowOverwrite)
  {

    Object key = getKey(message);
    try
      {
        DatabaseEntry valueBytes = toDbEntry(converter, message);
        DatabaseEntry keyBytes = toDbEntry(converter, key);
        OperationStatus result;
        if (allowOverwrite) result = database.put(txn.getTransaction(), keyBytes, valueBytes);
        else result = database.putNoOverwrite(txn.getTransaction(), keyBytes, valueBytes);
        if (result == OperationStatus.KEYEXIST)
          {
            StringBuilder sb = new StringBuilder("duplicate ");
            SH.join(',', keyFields, sb);
            sb.append(":[");
            sb.append(key);
            sb.append(']');
            throw new VdbException("duplicate key for " + databaseName + ":" + sb.toString());
          }
        if (result != OperationStatus.SUCCESS) throw new VdbException("general failure for " + databaseName + ":"
            + result);
      }
    catch (UniqueConstraintException e)
      {
        throw new DetailedException("Duplicate unique key", e).set("key fields", e.getSecondaryDatabaseName()).set(
            "record", message);
      }
    catch (DatabaseException e)
      {
        String m = e.getMessage();
        Matcher matcher = PATTERN_INSERT_ERROR.matcher(m);
        if (matcher.matches())
          {
            String columns = matcher.group(2);
            StringBuilder sb = new StringBuilder("duplicate ").append(columns).append(":[");
            boolean first = true;
            for (String column : SH.split(',', columns))
              {
                if (first) first = false;
                else sb.append(',');
                sb.append(message.ask(column));
              }
            sb.append(']');
            m = sb.toString();
          }

        throw new VdbException(message, m, e);
      }

    // TODO: should only be an update if allowOverwrite & the field already exists
    if (allowOverwrite) txn.addEvent(new BasicVdbEvent(VdbEvent.TYPE_UPDATE, getTableName(), key, message));
    else txn.addEvent(new BasicVdbEvent(VdbEvent.TYPE_INSERT, getTableName(), key, message));
  }

  private void deleteByKey(BdbVdbTransaction txn, Object... key)
  {
    OH.assertEq(key.length, keyFields.length);
    try
      {
        Object keyData = key.length == 1 ? key[0] : CH.l(key);
        DatabaseEntry keyBytes = toDbEntry(converter, keyData);
        database.delete(txn.getTransaction(), keyBytes);
      }
    catch (Exception e_)
      {
        throw new VdbException("error deleting message:" + SH.join(',', key), e_);
      }
  }

  @Override
  public void delete(BdbVdbTransaction txn, V message)
  {
    try
      {
        Object key = getKey(message);
        DatabaseEntry keyBytes = toDbEntry(converter, key);
        database.delete(txn.getTransaction(), keyBytes);
        txn.addEvent(new BasicVdbEvent(VdbEvent.TYPE_DELETE, getTableName(), key, message));
      }
    catch (Exception e)
      {
        throw new VdbException("error deleting message:" + message, e);
      }
  }

  static DatabaseEntry toDbEntry(OfflineConverter converter, Object o)
  {
    try
      {
        return new DatabaseEntry(converter.object2Bytes(o));
      }
    catch (Exception e)
      {
        throw new VdbException(e);
      }
  }

  public static Object fromDbEntry(OfflineConverter converter, DatabaseEntry dbe)
  {
    try
      {
        return converter.bytes2Object(dbe.getData());
      }
    catch (Exception e)
      {
        throw new VdbException(e);
      }
  }

  @Override
  public List<V> queryAll(BdbVdbTransaction txn)
  {
    if (txn == null) txn = BdbVdbTransaction.NONE;
    try
      {
        Cursor cursor = this.database.openCursor(txn.getTransaction(), null);
        DatabaseEntry foundData = new DatabaseEntry();
        DatabaseEntry foundKey = new DatabaseEntry();
        List<V> results = new ArrayList<V>(1);
        while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)
          results.add((V)fromDbEntry(converter, foundData));
        cursor.close();
        return results;
      }
    catch (DatabaseException e)
      {
        throw new VdbException("error querying all data", e);
      }
  }

  @Override
  public V queryPrimary(BdbVdbTransaction txn, Object... key)
  {
    OH.assertEq(key.length, keyFields.length);
    if (txn == null) txn = BdbVdbTransaction.NONE;
    try
      {
        DatabaseEntry foundData = new DatabaseEntry();
        Object keyData = key.length == 1 ? key[0] : CH.l(key);
        OperationStatus result = database.get(txn.getTransaction(), toDbEntry(converter, keyData), foundData,
            LockMode.DEFAULT);
        if (result == OperationStatus.SUCCESS) return (V)fromDbEntry(converter, foundData);
        else return null;
      }
    catch (DatabaseException e)
      {
        throw new VdbException("error querying message for field:" + SH.join(',', keyFields), e);
      }
  }

  @Override
  public List<V> queryPrimaryBetween(BdbVdbTransaction txn, Comparable[] start, Comparable[] end, int maxCount,
      boolean reverse)
  {
    if (txn == null) txn = BdbVdbTransaction.NONE;
    try
      {
        Cursor cursor = this.database.openCursor(txn.getTransaction(), null);
        DatabaseEntry foundData = new DatabaseEntry();
        DatabaseEntry foundKey;

        List<V> results = new ArrayList<V>(1);
        OperationStatus status;
        if (reverse)
          {
            if (end == null) status = cursor.getNext(foundKey = new DatabaseEntry(), foundData, LockMode.DEFAULT);
            else status = cursor.getSearchKey(foundKey = toDbEntry(converter, end), foundData, LockMode.DEFAULT);
          }
        else
          {
            if (start == null) status = cursor.getPrev(foundKey = new DatabaseEntry(), foundData, LockMode.DEFAULT);
            else status = cursor.getSearchKey(foundKey = toDbEntry(converter, start), foundData, LockMode.DEFAULT);
          }
        int count = 0;
        while (status == OperationStatus.SUCCESS)
          {
            V result = (V)fromDbEntry(converter, foundData);
            results.add(result);
            if (reverse)
              {
                if (start != null && VdbHelper.compare(getKeyFields(), start, result) > 0) break;
                status = cursor.getPrev(foundKey, foundData, LockMode.DEFAULT);
              }
            else
              {
                if (end != null && VdbHelper.compare(getKeyFields(), end, result) < 0) break;
                status = cursor.getNext(foundKey, foundData, LockMode.DEFAULT);
              }
            if (++count == maxCount) break;
          }
        cursor.close();
        return results;
      }
    catch (DatabaseException e)
      {
        throw new VdbException("error querying all data", e);
      }
  }

  @Override
  public Object getKey(V message)
  {
    if (keyFields.length == 1) return message.ask(keyFields[0]);
    final List<Object> r = new ArrayList<Object>(keyFields.length);
    for (int i = 0; i < keyFields.length; i++)
      r.add(message.ask(keyFields[i]));
    return r;
  }

  @Override
  public void truncate(BdbVdbTransaction txn)
  {
    if (txn == null) txn = BdbVdbTransaction.NONE;
    close();
    for (String indexName : indexesByName.keySet())
      {
        BdbVdbIndex<V> i = getIndex(indexName);
        i.truncate(txn);
      }
    getDatabase().getEnvironment().truncateDatabase(txn.getTransaction(), databaseName, false);
    open();
  }

  public void close()
  {
    for (String indexName : indexesByName.keySet())
      {
        getIndex(indexName).close();
      }
    database.close();
  }

  public void open()
  {
    final DatabaseConfig dbCfg = new DatabaseConfig();
    dbCfg.setAllowCreate(true);
    dbCfg.setTransactional(true);
    try
      {
        this.database = bdbVdbDatabase.getEnvironment().openDatabase(null, this.databaseName, dbCfg);
      }
    catch (Exception e)
      {
        throw new VdbException("error creating database for :" + this.databaseName, e);
      }
    for (String indexName : indexesByName.keySet())
      {
        getIndex(indexName).open();
      }
  }

  private Database truncateDatabase(BdbVdbTransaction txn, Database database)
  {
    final DatabaseConfig config = database.getConfig();
    final String databaseName = database.getDatabaseName();
    database.close();
    bdbVdbDatabase.getEnvironment().truncateDatabase(txn.getTransaction(), databaseName, false);
    return bdbVdbDatabase.getEnvironment().openDatabase(txn.getTransaction(), databaseName, config);
  }

  private void truncateSecondaryDatabase(BdbVdbTransaction txn, SecondaryDatabase database)
  {
    final String databaseName = database.getDatabaseName();
    database.close();
    bdbVdbDatabase.getEnvironment().truncateDatabase(txn.getTransaction(), databaseName, false);
  }

  public Database getDatabase()
  {
    return database;
  }

  @Override
  public String getTableName()
  {
    return databaseName;
  }

  @Override
  public String[] getKeyFields()
  {
    return keyFields;
  }

  @Override
  public Set<String> getIndexNames()
  {
    return indexesByName.keySet();
  }

  private void assertOpen()
  {
    if (!isOpen) throw new IllegalStateException();
  }

  public Database getBdbDatabase()
  {
    return database;
  }

  @Override
  public BdbVdbIndex<V> getIndex(String name)
  {
    return CH.getOrThrow(indexesByName, name, "Index not found");
  }

  @Override
  public long getRowSize()
  {
    return database.count();
  }

  public OfflineConverter getOfflineConverter()
  {
    return converter;
  }

  public void setOfflineConverter(OfflineConverter converter)
  {
    this.converter = converter;
  }

}
