
package com.f1.vdb;

import com.f1.base.Valued;

public interface VdbUniqueIndex<V extends Valued, T extends VdbTransaction> extends VdbIndex<V, T>
{

  V queryUnique(T txn, Object... key);

}
