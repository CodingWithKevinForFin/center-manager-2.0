package com.f1.ami.relay;

import com.f1.ami.amicommon.AmiCenterDefinition;

public class AmiRelayCenterDefinition extends AmiCenterDefinition {
	public AmiRelayCenterDefinition(AmiCenterDefinition def) {
		super(def);
	}

	public static AmiRelayCenterDefinition[] wrap(AmiCenterDefinition[] t) {
		final AmiRelayCenterDefinition[] r = new AmiRelayCenterDefinition[t.length];
		for (int i = 0; i < r.length; i++)
			r[i] = new AmiRelayCenterDefinition(t[i]);
		return r;
	}

}
