package com.f1.ami.center;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.f1.ami.amicommon.msg.AmiCenterResource;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.CharSubSequence;
import com.f1.utils.Cksum;
import com.f1.utils.ContentType;
import com.f1.utils.FastByteArrayInputStream;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.impl.CharSequenceHasher;
import com.f1.utils.structs.Tuple2;

public class AmiCenterResourcesManager extends Thread {
	static private Logger log = LH.get();
	public static final String DEFAULT_DIR = "./resources";
	public static final int DEFAULT_PERIOD = 5000;

	HasherMap<CharSequence, AmiCenterResource> resources = new HasherMap<CharSequence, AmiCenterResource>(CharSequenceHasher.INSTANCE);
	final private File root;
	final private StringBuilder tmpBuf = new StringBuilder();
	final private ContainerTools tools;

	private long checkResourcesPeriodMs;
	private int revision = 0;

	public AmiCenterResourcesManager(ContainerTools tools) {
		super("AmiCenterResourcesWatcher");
		setDaemon(true);
		this.root = tools.getOptional(AmiCenterProperties.PROPERTY_AMI_RESOURCES_DIR, new File(DEFAULT_DIR));
		this.checkResourcesPeriodMs = tools.getOptional(AmiCenterProperties.PROPERTY_AMI_RESOURCES_MONITOR_PERIOD_MS, DEFAULT_PERIOD);
		this.tools = tools;
	}

	volatile private boolean needsWakeup;

	public void wakeup() {
		synchronized (this) {
			this.needsWakeup = true;
			this.notify();
		}
	}
	@Override
	public void run() {
		for (;;) {
			synchronized (this) {
				if (this.needsWakeup)
					this.needsWakeup = false;
				else {
					try {
						wait(checkResourcesPeriodMs);
					} catch (InterruptedException e) {
						LH.warning(log, e);
					}
				}
			}
			try {
				tmpBuf.setLength(0);
				int n = getFilesCount(tmpBuf, this.root, true);
				if (n == -1 || n != resources.size()) {
					HasherMap<CharSequence, AmiCenterResource> sink = new HasherMap<CharSequence, AmiCenterResource>(CharSequenceHasher.INSTANCE);
					tmpBuf.setLength(0);
					stream(tmpBuf, root, true, sink);
					for (Entry<CharSequence, Tuple2<AmiCenterResource, AmiCenterResource>> e2 : CH.join(this.resources, sink).entrySet()) {
						Tuple2<AmiCenterResource, AmiCenterResource> v = e2.getValue();
						if (v.getA() == null)
							LH.info(log, "Resource Add: ", v.getB().getPath());
						else if (v.getB() == null)
							LH.info(log, "Resource Rem: ", v.getA().getPath());
						else if (v.getA() != v.getB())
							LH.info(log, "Resource Upd: ", v.getA().getPath());
					}
					this.resources = sink;
					this.revision++;
				}
			} catch (Exception e) {
				LH.warning(log, "Error: ", e);
			}
		}
	}
	public int getFilesCount(StringBuilder path, File file, boolean isRoot) {
		if (!file.canRead())
			return 0;
		int pathLen = path.length();
		try {
			if (!isRoot) {
				if (path.length() > 0)
					path.append('/');
				path.append(file.getName());
			}
			if (file.isDirectory()) {
				int r = 0;
				for (File f : file.listFiles()) {
					int t = getFilesCount(path, f, false);
					if (t == -1)
						return -1;
					r += t;
				}
				return r;
			} else {
				AmiCenterResource t = this.getResource(path);
				if (t == null) {
					return -1;
				} else if (t.getSize() == -1)
					return 1;
				if (t.getSize() != file.length() || t.getModifiedOn() != file.lastModified()) {
					return -1;
				}
				return 1;
			}

		} finally {
			path.setLength(pathLen);
		}
	}

	private FastByteArrayInputStream bufArray = new FastByteArrayInputStream(OH.EMPTY_BYTE_ARRAY);
	private CharSubSequence tmpSubsequence = new CharSubSequence();

	private void stream(StringBuilder path, File file, boolean isRoot, HasherMap<CharSequence, AmiCenterResource> sink) {
		if (!file.canRead())
			return;
		int pathLen = path.length();
		try {
			if (!isRoot) {
				if (path.length() > 0)
					path.append('/');
				path.append(file.getName());
			}
			String name = file.getName();
			long length = file.length();
			long lastModified = file.lastModified();
			if (sink != null && file.isFile()) {
				AmiCenterResource existing = getResource(path);
				if (existing != null && existing.getModifiedOn() == lastModified && existing.getSize() == length) {
					sink.put(existing.getPath(), existing);
				} else {
					final AmiCenterResource t = tools.nw(AmiCenterResource.class);
					byte[] data;
					try {
						data = IOH.readData(file);
						t.setSize(data.length);
						t.setData(data);
						try {
							tmpSubsequence.reset(name, name.lastIndexOf('.') + 1, name.length());
							ContentType type = ContentType.getTypeByFileExtension(tmpSubsequence, null);
							if (type != null && ContentType.TYPE_IMAGE.equals(type.getMimeSuperType())) {
								BufferedImage src = ImageIO.read(bufArray.reset(data));
								if (src != null) {
									t.setImageWidth(src.getWidth());
									t.setImageHeight(src.getHeight());
								} else {
									t.setImageWidth(AmiCenterResource.NO_SIZE);
									t.setImageHeight(AmiCenterResource.NO_SIZE);
								}
							}
						} catch (Throwable th) {
							LH.warning(log, "Error with file ", IOH.getFullPath(file), th);
							t.setImageWidth(AmiCenterResource.NO_SIZE);
							t.setImageHeight(AmiCenterResource.NO_SIZE);
						}
						t.setChecksum(Cksum.cksum(data));
					} catch (IOException e) {
						LH.info(log, "Error reading file: ", IOH.getFullPath(file), e);
						t.setSize(-1);
						t.setData(null);
						t.setChecksum(0);
					}
					t.setModifiedOn(lastModified);
					t.setPath(path.toString());
					t.lock();
					sink.put(t.getPath(), t);
				}
			}
			if (file.isDirectory()) {
				for (File f : file.listFiles())
					stream(path, f, false, sink);
			}
		} finally {
			path.setLength(pathLen);
		}
	}
	public AmiCenterResource getResource(CharSequence name) {
		return this.resources.get(name);
	}

	public int getRevision() {
		return this.revision;
	}
	public HasherMap<CharSequence, AmiCenterResource> getResources() {
		return this.resources;
	}
	public File getRoot() {
		return this.root;
	}

}
