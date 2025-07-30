
package com.f1.vdb.impl;

import com.f1.base.ToStringable;
import com.f1.base.Valued;
import com.f1.utils.Immutable;
import com.f1.vdb.VdbEvent;

public class BasicVdbEvent implements VdbEvent, ToStringable, Immutable
{
  private final int type;
  private final Valued valued;
  private final String tableName;
  private final Object key;

  @Override
  public int getType()
  {
    return type;
  }

  public boolean isInsert()
  {
    return type == TYPE_INSERT;
  }

  public boolean isUpdate()
  {
    return type == TYPE_UPDATE;
  }

  public boolean isDelete()
  {
    return type == TYPE_DELETE;
  }

  @Override
  public String getTableName()
  {
    return tableName;
  }

  @Override
  public Valued getValued()
  {
    return valued;
  }

  public BasicVdbEvent(int type, String tableName, Object key, Valued message)
  {
    this.type = type;
    this.tableName = tableName;
    this.key = key;
    this.valued = message;
  }

  @Override
  public String toString()
  {
    return toString(new StringBuilder()).toString();
  }

  @Override
  public StringBuilder toString(StringBuilder sb)
  {
    return sb.append(toString(getType())).append(" FOR ").append(getTableName()).append(':').append(getValued());
  }

  public static String toString(int type)
  {
    switch (type)
      {
      case TYPE_DELETE:
        return "DELETE";
      case TYPE_INSERT:
        return "INSERT";
      case TYPE_UPDATE:
        return "UPDATE";
      default:
        return "TYPE:" + type;
      }
  }

  @Override
  public Object getKey()
  {
    return key;
  }
}
