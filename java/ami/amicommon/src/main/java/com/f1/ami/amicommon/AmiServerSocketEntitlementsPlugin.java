package com.f1.ami.amicommon;

import com.f1.utils.ServerSocketEntitlements;

public interface AmiServerSocketEntitlementsPlugin extends AmiPlugin {

	public ServerSocketEntitlements createEntitlements();

}
