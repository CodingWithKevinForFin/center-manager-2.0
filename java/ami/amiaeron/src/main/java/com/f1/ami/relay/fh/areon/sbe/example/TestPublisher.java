package com.f1.ami.relay.fh.areon.sbe.example;

import java.util.concurrent.TimeUnit;

import org.agrona.BufferUtil;
import org.agrona.CloseHelper;
import org.agrona.concurrent.UnsafeBuffer;

import io.aeron.Aeron;
import io.aeron.Publication;
import io.aeron.driver.MediaDriver;
import io.aeron.samples.SampleConfiguration;

public class TestPublisher {
	private static final int STREAM_ID = SampleConfiguration.STREAM_ID;
	private static final String CHANNEL = SampleConfiguration.CHANNEL;
	private static final long NUMBER_OF_MESSAGES = SampleConfiguration.NUMBER_OF_MESSAGES;
	private static final long LINGER_TIMEOUT_MS = SampleConfiguration.LINGER_TIMEOUT_MS;
	private static final boolean EMBEDDED_MEDIA_DRIVER = true; //SampleConfiguration.EMBEDDED_MEDIA_DRIVER;

	/**
	 * Main method for launching the process.
	 *
	 * @param args
	 *            passed to the process.
	 * @throws InterruptedException
	 *             if the thread sleep delay is interrupted.
	 */
	public static void main(final String[] args) throws InterruptedException {
		System.out.println("Publishing to " + CHANNEL + " on stream id " + STREAM_ID);

		////////////////////////////////////////////////////////////////////////////
		// This section below is to use the SBE encoder instead of just plaintext //
		////////////////////////////////////////////////////////////////////////////

		// If configured to do so, create an embedded media driver within this application rather
		// than relying on an external one.
		final MediaDriver driver = EMBEDDED_MEDIA_DRIVER ? MediaDriver.launchEmbedded() : null;
		System.out.println("Publisher Driver: " + driver);

		final Aeron.Context ctx = new Aeron.Context();
		if (EMBEDDED_MEDIA_DRIVER) {
			ctx.aeronDirectoryName(driver.aeronDirectoryName());
		}

		// Connect a new Aeron instance to the media driver and create a publication on
		// the given channel and stream ID.
		// The Aeron and Publication classes implement "AutoCloseable" and will automatically
		// clean up resources when this try block is finished
		try (Aeron aeron = Aeron.connect(ctx); Publication publication = aeron.addPublication(CHANNEL, STREAM_ID)) {
			final UnsafeBuffer buffer = new UnsafeBuffer(BufferUtil.allocateDirectAligned(256, 64));

			for (long i = 0; i < NUMBER_OF_MESSAGES; i++) {
				System.out.print("Offering " + i + "/" + NUMBER_OF_MESSAGES + " - ");
				MarketDataParser encoder = new MarketDataParser();
				encoder.setRandomValues();

				encoder.encodeAndSend(buffer);
				int encodedLength = encoder.getEncodedLength();
				final long result = publication.offer(buffer, 0, encodedLength);

				if (result > 0) {
					System.out.println("yay!");
				} else if (result == Publication.BACK_PRESSURED) {
					System.out.println("Offer failed due to back pressure");
				} else if (result == Publication.NOT_CONNECTED) {
					System.out.println("Offer failed because publisher is not connected to a subscriber");
				} else if (result == Publication.ADMIN_ACTION) {
					System.out.println("Offer failed because of an administration action in the system");
				} else if (result == Publication.CLOSED) {
					System.out.println("Offer failed because publication is closed");
					break;
				} else if (result == Publication.MAX_POSITION_EXCEEDED) {
					System.out.println("Offer failed due to publication reaching its max position");
					break;
				} else {
					System.out.println("Offer failed due to unknown reason: " + result);
				}

				if (!publication.isConnected()) {
					System.out.println("No active subscribers detected");
				}

				Thread.sleep(TimeUnit.SECONDS.toMillis(1));
			}

			System.out.println("Done sending.");

			if (LINGER_TIMEOUT_MS > 0) {
				System.out.println("Lingering for " + LINGER_TIMEOUT_MS + " milliseconds...");
				Thread.sleep(LINGER_TIMEOUT_MS);
			}
		}

		CloseHelper.close(driver);
	}
}