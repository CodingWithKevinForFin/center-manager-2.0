package com.f1.ami.web.charts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.ami.web.AmiWebService;
import com.f1.http.HttpRequestResponse;
import com.f1.utils.ContentType;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class AmiWebImages {

	private static final byte[] MIME_PNG = ContentType.PNG.getMimeTypeAsBytes();
	private static final Logger log = LH.get();
	private static final int ANTI_ALIAS_CUTOFF = 100000;
	private byte[] image;
	private List<HttpRequestResponse> awaitingResponses;
	private boolean done = false;
	private AmiWebChartZoomMetrics zoom;
	private AmiWebChartGraphicsWrapper gw;
	private float compression;
	private Executor executor;
	private List<Runner> runners = new ArrayList<Runner>();
	private final AtomicInteger remaining;
	private boolean isGraphicsAvailable;
	private Map<String, String> fontMappings;

	public AmiWebImages(Executor executor, AmiWebImageGenerator imageGenerators[], AmiWebChartZoomMetrics zoom, int antialiasCutoff, float compression, int suggestedBatchSize,
			int maxBatches, AmiWebService service) {
		this.isGraphicsAvailable = service.getFontsManager().isGraphicsAvailable();
		this.compression = compression;
		this.executor = executor;
		this.zoom = zoom;
		this.fontMappings = service.getFontsManager().getJavaFontMappings();
		int size = 0;
		for (AmiWebImageGenerator i : imageGenerators) {
			if (i != null)
				size += i.getRenderingItems();
		}
		if (!isGraphicsAvailable || (zoom.getWidth() <= 0 || zoom.getHeight() <= 0)) {
			this.remaining = new AtomicInteger(0);
			this.done = true;
			return;
		}
		boolean antialias = size < antialiasCutoff;
		for (AmiWebImageGenerator ig : imageGenerators) {
			if (ig == null)
				continue;
			if (ig.hasGrid())
				this.runners.add(new Runner(this, ig, PHASE_GRID, -1, -1, zoom, antialias));
		}
		for (AmiWebImageGenerator ig : imageGenerators) {
			if (ig == null)
				continue;
			int len = ig.getRenderingItems();
			if (len == 0)
				this.runners.add(new Runner(this, ig, PHASE_DATA, 0, 0, zoom, antialias));
			else {
				int batchSize = getBatchsize(len, suggestedBatchSize, maxBatches);
				for (int start = 0; start < len; start += batchSize) {
					Runner runner = new Runner(this, ig, PHASE_DATA, start, Math.min(start + batchSize, len), zoom, antialias);
					this.runners.add(runner);
				}
			}
			if (ig.hasText())
				this.runners.add(new Runner(this, ig, PHASE_TEXT, -1, -1, zoom, antialias));
		}
		this.remaining = new AtomicInteger(this.runners.size() + 1);
		for (int i = 0; i < this.runners.size(); i++)
			this.executor.execute(this.runners.get(i));
		this.gw = new AmiWebChartGraphicsWrapper();
		this.gw.setAntialias(false);
		this.gw.init(zoom, fontMappings);

		onImageDone();
	}
	public void onImageDone() {
		if (this.remaining.decrementAndGet() == 0)
			combine();

	}

	static public int getBatchsize(int len, int suggestedBatchSize, int maxBatches) {
		if (len == 0)
			return 1;
		int batches = (len + suggestedBatchSize - 1) / suggestedBatchSize;
		if (batches > maxBatches)
			batches = maxBatches;
		int r = (len + batches - 1) / batches;
		return r;
	}

	public void abort() {
		if (!done) {
			setDone();
			this.gw.abort();
			for (int i = 0; i < this.runners.size(); i++)
				this.runners.get(i).gw.abort();
		}
	}

	private void setDone() {
		synchronized (this) {
			this.done = true;
			this.notifyAll();
		}
	}
	public void combine() {
		try {
			for (Runner i : this.runners) {
				if (!this.gw.isAborted())
					i.gw.renderTo(this.gw, i.ig.getOpacity());
			}
			if (!this.gw.isAborted()) {
				long now = System.currentTimeMillis();
				byte[] t = this.gw.renderPNG(compression);
				if (!this.gw.isAborted()) {
					this.image = t;
					int size = gw.getWidth() * gw.getHeight();
					long now2 = System.currentTimeMillis();
					if (log.isLoggable(Level.FINE))
						LH.fine(log, "Compressed at Level ", (int) (compression * 10), ", ", gw.getWidth(), "x", gw.getHeight(), "=>", t.length, " (", (t.length * 25L / size),
								"%) in ", (now2 - now), " millis");
				}
			}
			setDone();
			synchronized (this) {
				if (this.awaitingResponses != null) {
					for (HttpRequestResponse response : this.awaitingResponses) {
						populateResponse(response);
						response.respondNow(false);
					}
					this.awaitingResponses = null;
				}
				this.notifyAll();
			}
		} catch (Exception e) {
			LH.warning(log, "Error producing chart image: ", e);
			setDone();
			notifyAll();
		}
	}
	public void getImage(HttpRequestResponse response) {
		if (!isGraphicsAvailable) {
			response.putResponseHeader("Cache-Control", "no-store");
			response.setContentTypeAsBytes(MIME_PNG);
			try {
				response.getOutputStream().write(IOH.readDataFromResource("amiweb/graphics_error.png"));
			} catch (IOException e) {
				response.getOutputStream().write(0);
			}
			return;
		}
		if (!this.done) {
			synchronized (this) {
				if (!this.done) {
					response.setResponseAsyncMode();
					if (this.awaitingResponses == null)
						this.awaitingResponses = new ArrayList<HttpRequestResponse>(2);
					this.awaitingResponses.add(response);
					return;
				}
			}
		}
		populateResponse(response);
	}

	private void populateResponse(HttpRequestResponse response) {
		try {
			if (image != null) {
				response.setContentTypeAsBytes(MIME_PNG);
				response.getOutputStream().write(this.image);
			} else {
				response.putResponseHeader("Cache-Control", "no-store");
				response.setContentTypeAsBytes(MIME_PNG);
				response.getOutputStream().write(0);
			}
		} catch (IOException e) {
			LH.warning(log, "Could not populate response with image: ", e);
		}
	}

	public byte[] getImage() {
		if (!this.done)
			synchronized (this) {
				while (!this.done) {
					OH.wait(this);

				}
			}
		return this.image;
	}

	final private static byte PHASE_GRID = 1;
	final private static byte PHASE_DATA = 2;
	final private static byte PHASE_TEXT = 3;

	public static class Runner implements Runnable {

		private AmiWebImageGenerator ig;
		private AmiWebChartGraphicsWrapper gw;
		final private int start;
		final private int end;
		final private AmiWebImages owner;
		private AmiWebChartZoomMetrics zoom;
		private boolean antiAlias;
		private byte phase;

		public Runner(AmiWebImages owner, AmiWebImageGenerator ig, byte phase, int start, int end, AmiWebChartZoomMetrics zoom, boolean antiAlias) {
			this.owner = owner;
			this.start = start;
			this.ig = ig;
			this.end = end;
			this.zoom = zoom;
			this.phase = phase;
			this.gw = new AmiWebChartGraphicsWrapper();
			this.antiAlias = antiAlias;
		}

		@Override
		public void run() {
			this.gw.setAntialias(this.antiAlias);
			this.gw.init(zoom, owner.fontMappings);
			switch (phase) {
				case PHASE_GRID:
					this.ig.drawGrid(gw);
					break;
				case PHASE_DATA:
					this.ig.draw(gw, start, end);
					break;
				case PHASE_TEXT:
					this.ig.drawText(gw);
					break;
			}
			this.owner.onImageDone();
		}

	}

}
