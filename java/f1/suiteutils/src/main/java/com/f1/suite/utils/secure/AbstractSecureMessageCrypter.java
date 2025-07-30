package com.f1.suite.utils.secure;

import com.f1.base.Message;
import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.ContainerServices;
import com.f1.povo.standard.SecureMessage;
import com.f1.utils.IOH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public abstract class AbstractSecureMessageCrypter implements SecureMessageCrypter {
	final private ContainerServices services;
	final private String appName;
	final private long slippageMs;
	final private ObjectGeneratorForClass<SecureMessage> generator;

	public AbstractSecureMessageCrypter(ContainerServices services, String appName, long slippageMs) {
		this.services = services;
		this.appName = appName;
		this.slippageMs = slippageMs;
		this.generator = services.getGenerator(SecureMessage.class);
	}

	@Override
	public <M extends Message> SecureMessage<M> secureMessage(M message) {
		final byte[] rawData = services.getConverter().object2Bytes(message);
		final long now = services.getClock().getNow();
		final byte[] encryptedData;
		try {
			encryptedData = encrypt(rawData, now);
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
		final String signature = sign(rawData, now, appName);
		final SecureMessage<M> r = generator.nw();
		r.setNow(now);
		r.setSender(appName);
		r.setPayload(encryptedData);
		r.setSignature(signature);
		r.setEncodingMode(getEncodingMethod());
		return r;
	}
	@Override
	public <M extends Message> M unsecureMessage(SecureMessage<M> message) {
		if (OH.ne(message.getEncodingMode(), getEncodingMethod()))
			throw new SecurityException("Bad Encoding Mode, not " + getEncodingMethod() + ": " + message.getEncodingMode());
		long now = services.getClock().getNow();
		if (MH.diff(now, message.getNow()) > slippageMs)
			throw new SecurityException("bad timestamp");

		byte[] data;
		try {
			data = decrypt(message.getPayload(), now, message.getSender(), message.getNow());
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
		String signature = sign(data, message.getNow(), message.getSender());
		M r = (M) services.getConverter().bytes2Object(data);
		if (OH.ne(signature, message.getSignature()))
			throw new SecurityException("bad signature");
		return r;
	}

	@Override
	abstract public String getEncodingMethod();
	abstract protected byte[] decrypt(byte[] payload, long now, String senderName, long senderNow) throws Exception;
	abstract protected byte[] encrypt(byte[] payload, long now) throws Exception;

	protected String sign(byte[] payload, long now, String senderName) {
		long code = IOH.checkSumBsdLong(payload);
		code = IOH.checkSumBsdLong(code, senderName.getBytes());
		code = code * 31 + now;
		return SH.toString(code, 62);
	}

}
