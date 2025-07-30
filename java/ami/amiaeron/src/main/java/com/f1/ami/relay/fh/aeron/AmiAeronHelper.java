package com.f1.ami.relay.fh.aeron;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.agrona.concurrent.IdleStrategy;

import com.f1.utils.LH;

import io.aeron.FragmentAssembler;
import io.aeron.Image;
import io.aeron.Subscription;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.samples.SampleConfiguration;

public class AmiAeronHelper {
	private static final Logger log = LH.get();
	
	public static void printAvailableImage(final Image image) {
		final Subscription subscription = image.subscription();
		String result = String.format("Available image on %s streamId=%d sessionId=%d mtu=%d term-length=%d from %s%n", 
				subscription.channel(), subscription.streamId(), image.sessionId(), image.mtuLength(), 
				image.termBufferLength(), image.sourceIdentity());
		LH.info(log, result);
	}
	
	public static void printUnavailableImage(final Image image) {
		final Subscription subscription = image.subscription();
		String result = String.format("Unavailable image on %s streamId=%d sessionId=%d%n", 
				subscription.channel(), subscription.streamId(), image.sessionId());
		LH.info(log, result);
	}
	
	public static Consumer<Subscription> subscriberLoop(final FragmentHandler fragmentHandler, final int limit, final AtomicBoolean running) {
        return subscriberLoop(fragmentHandler, limit, running, SampleConfiguration.newIdleStrategy());
    }
	
	public static Consumer<Subscription> subscriberLoop(final FragmentHandler fragmentHandler, final int limit, final AtomicBoolean running, final IdleStrategy idleStrategy) {
        return (subscription) -> {
            final FragmentAssembler assembler = new FragmentAssembler(fragmentHandler);
            while (running.get()) {
                final int fragmentsRead = subscription.poll(assembler, limit);
                idleStrategy.idle(fragmentsRead);
            }
        };
    }
}
