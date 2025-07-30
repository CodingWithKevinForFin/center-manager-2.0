
package com.f1.vdb;

import java.util.ArrayList;
import java.util.List;
import com.f1.base.Valued;
import com.f1.utils.OH;

public class VdbHelper
{

  public static <V extends Valued> Object extractKey(String[] keyFields, V valued)
  {
    if (keyFields.length == 1) return valued.ask(keyFields[0]);
    final List<Object> r = new ArrayList<Object>(keyFields.length);
    for (int i = 0; i < keyFields.length; i++)
      r.add(valued.ask(keyFields[i]));
    return r;
  }

  public static int compare(String[] keys, Comparable[] values_, Valued result_)
  {
    for (int i = 0; i < keys.length; i++)
      {
        final int j = OH.compare(values_[i], (Comparable)result_.ask(keys[i]));
        if (j != 0) return j;
      }
    return 0;
  }

}
