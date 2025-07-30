package com.f1.vortexcommon.msg.eye;

import com.f1.base.Lockable;
import com.f1.base.PartialMessage;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.VortexEntity;

@VID("F1.VO.VEE")
public interface VortexEyeEntity extends VortexEntity, PartialMessage, Lockable {

	public VortexEyeEntity clone();
}
