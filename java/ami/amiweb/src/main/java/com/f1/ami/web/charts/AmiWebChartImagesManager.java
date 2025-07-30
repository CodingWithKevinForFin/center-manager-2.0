package com.f1.ami.web.charts;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import com.f1.ami.web.AmiWebMain;
import com.f1.ami.web.AmiWebProperties;
import com.f1.ami.web.AmiWebService;
import com.f1.container.ContainerTools;
import com.f1.suite.web.HttpRequestAction;
import com.f1.utils.MH;

public class AmiWebChartImagesManager {

	private static final int DEFAULT_COMPRESSION_LEVEL = 2;
	private static final int DEFAULT_ANTIALIAS_CUTOFF = 100000;
	private static final int DEFAULT_POINTS_PER_BATCH = 100000;
	private static final int DEFAULT_THREADS_PER_LAYER = 10;
	private Map<String, AmiWebImages> imagesByPortletId = new HashMap<String, AmiWebImages>();
	private final Executor executor;
	private final AmiWebService service;
	private final int suggestedBatchSize;
	private final int maxBatches;
	private int antialiasCutoff;
	private float compressionLevel;

	public AmiWebChartImagesManager(AmiWebService service, Executor executor) {
		ContainerTools tools = service.getPortletManager().getTools();
		this.executor = tools.getContainer().getThreadPoolController().getThreadPool(AmiWebMain.AMI_IMAGES_THREAD_POOL_NAME);
		this.service = service;
		this.suggestedBatchSize = tools.getOptional(AmiWebProperties.PROPERTY_AMI_CHART_THREADING_SUGGESTED_POINTS_PER_THREAD, DEFAULT_POINTS_PER_BATCH);
		this.maxBatches = tools.getOptional(AmiWebProperties.PROPERTY_AMI_CHART_THREADING_MAX_THREADS_PER_LAYER, DEFAULT_THREADS_PER_LAYER);
		this.antialiasCutoff = this.service.getPortletManager().getTools().getOptional(AmiWebProperties.PROPERTY_AMI_CHART_ANTIALIAS_CUTOFF, DEFAULT_ANTIALIAS_CUTOFF);
		this.compressionLevel = MH.clip(
				this.service.getPortletManager().getTools().getOptional(AmiWebProperties.PROPERTY_AMI_CHART_COMPRESSION_LEVEL, DEFAULT_COMPRESSION_LEVEL) / 10f, 0, 10);

	}
	public String addImageGenerator(String portletId, int layerPos, AmiWebImageGenerator[] imageGenerators, AmiWebChartZoomMetrics zoom) {
		AmiWebImages awi = generateAmiWebImages(imageGenerators, zoom);
		String id = portletId + "-" + layerPos;
		AmiWebImages old = this.imagesByPortletId.put(id, awi);
		if (old != null)
			old.abort();
		return id;
	}
	public AmiWebImages generateAmiWebImages(AmiWebImageGenerator[] imageGenerators, AmiWebChartZoomMetrics zoom) {
		AmiWebImages awi = new AmiWebImages(executor, imageGenerators, zoom, antialiasCutoff, compressionLevel, suggestedBatchSize, maxBatches, this.service);
		return awi;
	}
	public void onRequest(HttpRequestAction request) {
		String portletId = request.getRequest().getParams().get("portletId");
		AmiWebImages images = this.imagesByPortletId.get(portletId);
		images.getImage(request.getRequest());
	}

}
