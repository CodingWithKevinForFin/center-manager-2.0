package com.f1.pofo.fix.child;

import com.f1.base.VID;
import com.f1.pofo.fix.ChildMessage;
import com.f1.pofo.fix.FixOrderRequest;

/**
 * A request pertaining to a child order
 */
@VID("F1.FX.CO")
public interface FixChildOrderRequest extends FixOrderRequest, ChildMessage {

}
