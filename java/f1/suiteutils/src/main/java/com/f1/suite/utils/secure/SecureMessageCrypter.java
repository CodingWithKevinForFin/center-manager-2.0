package com.f1.suite.utils.secure;

import com.f1.base.Message;
import com.f1.povo.standard.SecureMessage;

public interface SecureMessageCrypter {
	public <M extends Message> SecureMessage<M> secureMessage(M message);
	public <M extends Message> M unsecureMessage(SecureMessage<M> message);
	public String getEncodingMethod();

}
