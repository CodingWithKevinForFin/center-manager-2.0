package com.f1.pofo.fix.child;

import com.f1.base.VID;
import com.f1.pofo.fix.ChildMessage;
import com.f1.pofo.fix.FixStatusReport;

/**
 * A status update pertaining to a child order
 */
@VID("F1.FC.SR")
public interface FixChildStatusReport extends FixStatusReport, ChildMessage {

}
