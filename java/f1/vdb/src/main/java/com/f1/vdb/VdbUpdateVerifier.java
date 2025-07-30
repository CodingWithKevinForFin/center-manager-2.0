
package com.f1.vdb;

import com.f1.base.Valued;

public interface VdbUpdateVerifier<V extends Valued>
{
  // Called before the update is performed to ensure that
  void verifyUpdate(V existing_, V newField_) throws VdbException;
}
