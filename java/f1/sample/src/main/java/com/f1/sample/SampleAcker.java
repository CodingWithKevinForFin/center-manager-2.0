package com.f1.sample;

import com.f1.base.Ackable;
import com.f1.base.Acker;

public class SampleAcker implements Acker {
	final private Ackable originalMessage;
	final private String text;

	public SampleAcker(Ackable message, String text) {
		this.originalMessage = message;
		this.text = text;
	}

	@Override
	public void ack(Ackable message, Object optional) {
		// debug
		System.out.println(getClass().getName() + ": " + text + " orignal_message=" + originalMessage + " last_message=" + message + " optional=" + optional);
	}
}
