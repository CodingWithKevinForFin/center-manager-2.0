
package com.f1.vdb;

import com.f1.base.Valued;

public class VdbException extends RuntimeException
{
  private static final long serialVersionUID = 6743168772672052606L;

  private Valued _data;

  public VdbException()
  {
    super();
  }

  public VdbException(Valued data_, String message_, Throwable cause_)
  {
    super(message_, cause_);
    this._data = data_;
  }

  public VdbException(String message_, Throwable cause_)
  {
    super(message_, cause_);
  }

  public VdbException(String message_)
  {
    super(message_);
  }

  public VdbException(Throwable cause_)
  {
    super(cause_);
  }

  public Valued getData()
  {
    return _data;
  }

  @Override
  public String getMessage()
  {
    if (_data != null)
      {
        return super.getMessage() + " for: " + _data;
      }
    return super.getMessage();
  }

}
