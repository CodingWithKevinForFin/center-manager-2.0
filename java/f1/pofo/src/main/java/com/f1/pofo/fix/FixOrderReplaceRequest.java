package com.f1.pofo.fix;

import com.f1.base.VID;

/**
 * indicates a replace request. see {@link MsgType#REPLACE_REQUEST}
 */
@VID("F1.FX.OR")
public interface FixOrderReplaceRequest extends FixRequest, VersionedMsg {

}
