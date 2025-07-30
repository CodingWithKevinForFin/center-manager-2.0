package com.f1.pofo.fix.child;

import com.f1.base.VID;
import com.f1.pofo.fix.ChildMessage;
import com.f1.pofo.fix.FixExecutionReport;

/**
 * represents an execution report specifically for a child (street side)
 */
@VID("F1.FX.CE")
public interface FixChildExecutionReport extends FixExecutionReport, ChildMessage {

}
