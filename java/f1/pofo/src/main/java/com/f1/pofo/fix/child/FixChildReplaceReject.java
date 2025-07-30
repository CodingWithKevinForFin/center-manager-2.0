package com.f1.pofo.fix.child;

import com.f1.base.VID;
import com.f1.pofo.fix.ChildMessage;
import com.f1.pofo.fix.FixOrderReplaceReject;

/**
 * A reject pertaining to request to replace a child order
 */
@VID("F1.FC.RR")
public interface FixChildReplaceReject extends FixOrderReplaceReject, ChildMessage {

}
