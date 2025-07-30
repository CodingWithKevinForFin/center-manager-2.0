
package com.f1.vdb;


public interface VdbTransaction
{

  Iterable<VdbEvent> getEvents();

  void addEvent(VdbEvent event);

}

