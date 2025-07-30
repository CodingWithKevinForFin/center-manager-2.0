package com.f1.ami.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.msg.AmiCenterManageResourcesRequest;
import com.f1.ami.amicommon.msg.AmiCenterResource;
import com.f1.base.Action;
import com.f1.base.IterableAndSize;
import com.f1.container.ResultMessage;
import com.f1.suite.web.portal.BackendResponseListener;
import com.f1.utils.Cksum;
import com.f1.utils.ImageHelper;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.WebImage;
import com.f1.utils.structs.Tuple2;

public class AmiWebResourcesManager implements BackendResponseListener, AmiWebRealtimeObjectListener {
	private final AmiWebFile webResourcesDir;
	private final AmiWebService service;
	private AmiWebGlobalResourceCache globalImageCache;

	public AmiWebResourcesManager(AmiWebService service, AmiWebFile webResourcesDir) {
		this.service = service;
		this.webResourcesDir = webResourcesDir;
		service.getPrimaryWebManager().getAmiObjectsByType(AmiConsts.TYPE_RESOURCE).addAmiListener(this);
		this.globalImageCache = service.getPortletManager().getTools().getServices().getService(AmiWebGlobalResourceCache.SERVICE_ID, AmiWebGlobalResourceCache.class);
	}

	public AmiWebFile getResourcesDirectory() {
		return webResourcesDir;
	}

	public List<AmiWebResource> getWebResources() {
		long now = System.currentTimeMillis();
		List<AmiWebResource> r = new ArrayList<AmiWebResource>();

		List<Tuple2<String, AmiWebFile>> sink = new ArrayList<Tuple2<String, AmiWebFile>>();
		listFilesRecursive(this.webResourcesDir.listFiles(), "", sink);
		for (Tuple2<String, AmiWebFile> nameAndFile : sink) {
			AmiWebFile file = nameAndFile.getB();
			String name = nameAndFile.getA();
			Long cksum = null;
			int w = -1;
			int h = -1;
			byte[] readBytes = null;
			try {
				readBytes = file.readBytes();
				cksum = Cksum.cksum(readBytes);
				WebImage img = ImageHelper.readImage(file.getName(), readBytes);
				if (img != null) {
					w = img.getWidth();
					h = img.getHeight();
				}
			} catch (Exception e) {
			}
			r.add(new AmiWebResource(now, false, name, file.length(), file.lastModified(), cksum, w, h, readBytes));
		}
		IterableAndSize<AmiWebObject> objects = service.getPrimaryWebManager().getAmiObjectsByType(AmiConsts.TYPE_RESOURCE).getAmiObjects();
		for (AmiWebObject obj : objects) {
			String name = obj.getObjectId();
			Long fileSize = (Long) obj.getParam("FileSize");
			Long modifiedOn = (Long) obj.getParam("ModifiedOn");
			Long checksum = (Long) obj.getParam("Checksum");
			Long w = (Long) obj.getParam("ImageWidth");
			Long h = (Long) obj.getParam("ImageHeight");
			r.add(new AmiWebResource(now, true, name, noNull(fileSize), noNull(modifiedOn), noNull(checksum), (int) noNull(w), (int) noNull(h), null));
		}
		return r;
	}

	private long noNull(Long i) {
		return i == null ? -1L : i;
	}

	private void listFilesRecursive(AmiWebFile files[], String path, List<Tuple2<String, AmiWebFile>> sink) {
		for (AmiWebFile file : files) {
			String name = SH.is(path) ? (path + '/' + file.getName()) : file.getName();
			if (file.isDirectory())
				listFilesRecursive(file.listFiles(), name, sink);
			else if (file.canRead())
				sink.add(new Tuple2<String, AmiWebFile>(name, file));
		}
	}

	private List<AmiWebResourcesManagerListener> listeners = new ArrayList<AmiWebResourcesManagerListener>();

	public AmiWebResource getWebResource(String name) {
		long now = System.currentTimeMillis();
		AmiWebResource existing = this.globalImageCache.get(name);
		if (existing != null)
			return existing;
		IterableAndSize<AmiWebObject> objects = service.getPrimaryWebManager().getAmiObjectsByType(AmiConsts.TYPE_RESOURCE).getAmiObjects();
		for (AmiWebObject obj : objects) {
			String fileName = obj.getObjectId();
			if (OH.ne(fileName, name))
				continue;
			Long fileSize = (Long) obj.getParam("FileSize");
			Long modifiedOn = (Long) obj.getParam("ModifiedOn");
			Long checksum = (Long) obj.getParam("Checksum");
			Long w = (Long) obj.getParam("ImageWidth");
			Long h = (Long) obj.getParam("ImageHeight");
			return new AmiWebResource(now, true, name, noNull(fileSize), noNull(modifiedOn), noNull(checksum), (int) noNull(w), (int) noNull(h), null);
		}
		return null;
	}
	public void removeResource(String path, boolean isCenter) {
		if (isCenter) {
			AmiCenterManageResourcesRequest req = service.nw(AmiCenterManageResourcesRequest.class);
			AmiCenterResource resource = service.nw(AmiCenterResource.class);
			resource.setPath(path);
			req.setResource(resource);
			service.sendRequestToBackend(this, req);
		} else {
			AmiWebFile rd = this.service.getResourcesManager().getResourcesDirectory();
			AmiWebFile file = this.service.getAmiFileSystem().getFile(rd, path);
			try {
				file.delete();
				fireOnResourcesChanged();
			} catch (Exception e) {
				service.getPortletManager().showAlert("Unexpected Error: " + e.getMessage(), e);
			}
		}
		globalImageCache.clear();
	}

	public void uploadResource(String path, byte[] data) throws IOException {
		AmiWebFile rd = this.service.getResourcesManager().getResourcesDirectory();
		AmiWebFile file = this.service.getAmiFileSystem().getFile(rd, path);
		file.getParentFile().mkdirForce();
		file.writeBytes(data);
		fireOnResourcesChanged();
		globalImageCache.clear();
	}

	private void fireOnResourcesChanged() {
		for (AmiWebResourcesManagerListener i : this.listeners)
			i.onResourcesChanged();
	}

	public void addListener(AmiWebResourcesManagerListener listener) {
		this.listeners.add(listener);
	}
	public void removeListener(AmiWebResourcesManagerListener listener) {
		this.listeners.remove(listener);
	}

	public void uploadFile(String path, byte[] data, boolean isCenter) {
		// TODO Auto-generated method stub

		if (isCenter) {
			AmiCenterManageResourcesRequest req = service.nw(AmiCenterManageResourcesRequest.class);
			AmiCenterResource resource = service.nw(AmiCenterResource.class);
			resource.setPath(path);
			resource.setData(data);
			req.setResource(resource);
			service.sendRequestToBackend(this, req);
		} else {
			try {
				this.service.getResourcesManager().uploadResource(path, data);
				fireOnResourcesChanged();
			} catch (Exception e) {
				service.getPortletManager().showAlert("Unexpected Error: " + e.getMessage(), e);
			}
		}
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
	}

	@Override
	public void onAmiEntitiesReset(AmiWebRealtimeObjectManager manager) {
		fireOnResourcesChanged();
	}

	@Override
	public void onAmiEntityAdded(AmiWebRealtimeObjectManager manager, AmiWebObject entity) {
	}

	@Override
	public void onAmiEntityUpdated(AmiWebRealtimeObjectManager manager, AmiWebObjectFields fields, AmiWebObject entity) {
	}

	@Override
	public void onAmiEntityRemoved(AmiWebRealtimeObjectManager manager, AmiWebObject entity) {
	}

	@Override
	public void onLowerAriChanged(AmiWebRealtimeObjectManager manager, String oldAri, String newAri) {
	}

	@Override
	public void onSchemaChanged(AmiWebRealtimeObjectManager manager, byte status, Map<String, Tuple2<Class, Class>> columns) {
	}

}
