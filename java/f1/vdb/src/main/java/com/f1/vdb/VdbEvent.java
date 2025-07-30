
package com.f1.vdb;

import com.f1.base.Valued;

public interface VdbEvent
{
  public static final int TYPE_INSERT = 1;
  public static final int TYPE_UPDATE = 2;
  public static final int TYPE_DELETE = 4;
  public static final int TYPE_RESULT = 8;

  public int getType();

  public String getTableName();

  public Valued getValued();

  public Object getKey();
}
