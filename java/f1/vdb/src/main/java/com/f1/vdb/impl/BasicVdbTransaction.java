
package com.f1.vdb.impl;

import java.util.ArrayList;
import java.util.List;
import com.f1.vdb.VdbEvent;
import com.f1.vdb.VdbTransaction;

public class BasicVdbTransaction implements VdbTransaction
{

  private List<VdbEvent> events = new ArrayList<VdbEvent>();

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
