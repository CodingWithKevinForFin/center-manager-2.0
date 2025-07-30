package com.f1.pofo.fix;

import com.f1.base.VID;

/**
 * request to cancel an order
 * 
 */
@VID("F1.FX.CR")
public interface FixCancelRequest extends FixRequest, VersionedMsg {

}
