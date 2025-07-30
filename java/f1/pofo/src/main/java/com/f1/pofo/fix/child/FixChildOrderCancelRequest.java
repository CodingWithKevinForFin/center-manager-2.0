package com.f1.pofo.fix.child;

import com.f1.base.VID;
import com.f1.pofo.fix.ChildMessage;
import com.f1.pofo.fix.FixCancelRequest;

/**
 * A request to cancel a child order
 */
@VID("F1.FX.CC")
public interface FixChildOrderCancelRequest extends FixCancelRequest, ChildMessage {

}
