
package com.f1.vdb.bdb;

import java.util.ArrayList;
import java.util.List;
import com.sleepycat.je.Transaction;
import com.f1.vdb.VdbEvent;
import com.f1.vdb.VdbTransaction;

public class BdbVdbTransaction implements VdbTransaction
{
  public static final BdbVdbTransaction NONE = new BdbVdbTransaction(null);

  private final Transaction transaction;
  private final List<VdbEvent> events = new ArrayList<VdbEvent>();

  public BdbVdbTransaction(Transaction transaction)
  {
    this.transaction = transaction;
  }

  public Transaction getTransaction()
  {
    return transaction;
  }

  @Override
  public Iterable<VdbEvent> getEvents()
  {
    return events;
  }

  @Override
  public void addEvent(VdbEvent event)
  {
    events.add(event);
  }

}
