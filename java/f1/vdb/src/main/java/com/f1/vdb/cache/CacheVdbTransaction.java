
package com.f1.vdb.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import com.f1.utils.CH;
import com.f1.utils.structs.Tuple2;
import com.f1.vdb.VdbEvent;
import com.f1.vdb.VdbTransaction;

public class CacheVdbTransaction<T extends VdbTransaction> implements VdbTransaction
{

  private final Tuple2<String, Object> _tempTuple = new Tuple2<String, Object>();
  private final List<VdbEvent> _allEvents = new ArrayList<VdbEvent>();
  private final HashMap<Tuple2<String, Object>, VdbEvent> _events = new LinkedHashMap<Tuple2<String, Object>, VdbEvent>();
  private final T inner;

  public CacheVdbTransaction(T inner)
  {
    this.inner = inner;
  }

  public T getInner()
  {
    return inner;
  }

  private static final int SHIFT = 8;

  @Override
  public Iterable<VdbEvent> getEvents()
  {
    return _events.values();
  }

  public VdbEvent getEvent(String tableName, Object key)
  {
    _tempTuple.setAB(tableName, key);
    return _events.get(_tempTuple);
  }

  /**
   * <pre>
   * NEW:       EXISTING: NULL     INSERT  UPDATE  DELETE  RESULT
   *                      ------   ------  ------  ------  ------
   * INSERT               STORE    ERROR   ERROR   ERROR   ERROR
   * UPDATE               STORE    IGNORE  IGNORE  ERROR   REPLACE
   * DELETE               STORE    REMOVE  REPLACE ERROR   REPLACE
   * RESULT               STORE    ERROR   ERROR   ERROR   IGNORE
   * 
   * * ERROR = throw exception
   * * REMOVE = clear map entry
   * * IGNORE = simply return, map not updated
   * * REPLACE = replace map entry
   * * STORE = put map entry
   * 
   * <pre>
   */
  @Override
  public void addEvent(VdbEvent entry_)
  {

    _tempTuple.setAB(entry_.getTableName(), entry_.getKey());
    try
      {
        VdbEvent existing = _events.get(_tempTuple);
        if (existing != null)
          {
            switch (existing.getType() | (entry_.getType() << SHIFT))
              {

              case VdbEvent.TYPE_INSERT | (VdbEvent.TYPE_DELETE << SHIFT):
                CH.removeOrThrow(_events, _tempTuple);
                _allEvents.add(entry_);
                return;

              case VdbEvent.TYPE_INSERT | (VdbEvent.TYPE_UPDATE << SHIFT):
              case VdbEvent.TYPE_UPDATE | (VdbEvent.TYPE_UPDATE << SHIFT):
              case VdbEvent.TYPE_RESULT | (VdbEvent.TYPE_RESULT << SHIFT):
                _allEvents.add(entry_);
                return;

              case VdbEvent.TYPE_RESULT | (VdbEvent.TYPE_UPDATE << SHIFT):
              case VdbEvent.TYPE_RESULT | (VdbEvent.TYPE_DELETE << SHIFT):
              case VdbEvent.TYPE_UPDATE | (VdbEvent.TYPE_DELETE << SHIFT):
                _events.put(_tempTuple.clone(), entry_);
                _allEvents.add(entry_);
                return;

              case VdbEvent.TYPE_INSERT | (VdbEvent.TYPE_INSERT << SHIFT):
              case VdbEvent.TYPE_INSERT | (VdbEvent.TYPE_RESULT << SHIFT):
              case VdbEvent.TYPE_UPDATE | (VdbEvent.TYPE_INSERT << SHIFT):
              case VdbEvent.TYPE_UPDATE | (VdbEvent.TYPE_RESULT << SHIFT):
              case VdbEvent.TYPE_DELETE | (VdbEvent.TYPE_INSERT << SHIFT):
              case VdbEvent.TYPE_DELETE | (VdbEvent.TYPE_UPDATE << SHIFT):
              case VdbEvent.TYPE_DELETE | (VdbEvent.TYPE_DELETE << SHIFT):
              case VdbEvent.TYPE_DELETE | (VdbEvent.TYPE_RESULT << SHIFT):
              case VdbEvent.TYPE_RESULT | (VdbEvent.TYPE_INSERT << SHIFT):
                throw new RuntimeException("invalid states:" + toString(existing.getType()) + ","
                    + toString(entry_.getType()));
              default:
                throw new RuntimeException("unkown state:" + toString(existing.getType()) + ","
                    + toString(entry_.getType()));
              }
          }
        _allEvents.add(entry_);
        CH.putOrThrow(_events, _tempTuple.clone(), entry_);
      }
    finally
      {
        _tempTuple.clear();
      }
  }

  private String toString(int type_)
  {
    switch (type_)
      {
      case VdbEvent.TYPE_INSERT:
        return "INSERT";
      case VdbEvent.TYPE_UPDATE:
        return "UPDATE";
      case VdbEvent.TYPE_DELETE:
        return "DELETE";
      case VdbEvent.TYPE_RESULT:
        return "RESULT";
      default:
        return "type:" + type_;
      }
  }

  public List<VdbEvent> getAllEvents()
  {
    return _allEvents;
  }

}
