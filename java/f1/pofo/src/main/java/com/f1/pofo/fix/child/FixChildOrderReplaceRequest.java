package com.f1.pofo.fix.child;

import com.f1.base.VID;
import com.f1.pofo.fix.ChildMessage;
import com.f1.pofo.fix.FixOrderReplaceRequest;

/**
 * A request to replace a child order
 */
@VID("F1.FX.RC")
public interface FixChildOrderReplaceRequest extends FixOrderReplaceRequest, ChildMessage {

}
