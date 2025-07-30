package com.f1.ami.center.triggers;

import com.f1.ami.amicommon.AmiPlugin;
import com.f1.ami.center.AmiCenterProperties;

/**
 * An {@link AmiTrigger} that is declared using properties. See {@link AmiCenterProperties#PROPERTY_AMI_SCHEMA_TRIGGERS} for declaring inside properties file. Note, it's often
 * useful to extend {@link AmiAbstractTriggerPlugin}.
 */

@Deprecated
public interface AmiTriggerPlugin extends AmiTrigger, AmiPlugin {
}
