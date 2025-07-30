package com.f1.pofo.oms;

import com.f1.base.Message;
import com.f1.base.VID;

/**
 * a message sent to the oms to request a snapshot.
 * 
 */
@VID("F1.OM.SR")
public interface OmsSnapshotRequest extends Message {

}
