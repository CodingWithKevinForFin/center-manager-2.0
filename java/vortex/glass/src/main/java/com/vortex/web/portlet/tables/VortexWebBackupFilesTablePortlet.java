package com.vortex.web.portlet.tables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.f1.base.Action;
import com.f1.base.Row;
import com.f1.container.ResultMessage;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.BasicPortletDownload;
import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletDownload;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.portal.impl.text.SimpleFastTextPortlet;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.MapWebCellFormatter;
import com.f1.suite.web.table.impl.WebTableFilteredSetFilter;
import com.f1.utils.ArchiveFileReader;
import com.f1.utils.CH;
import com.f1.utils.FileMagic;
import com.f1.utils.Formatter;
import com.f1.utils.LH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.LongSet;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.vortexcommon.msg.VortexEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentBackupFile;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexVaultEntry;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeQueryDataRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeQueryDataResponse;
import com.vortex.client.VortexClientBackupFile;
import com.vortex.client.VortexClientEntity;
import com.vortex.client.VortexClientMachine;
import com.vortex.client.VortexClientMachineListener;
import com.vortex.client.VortexClientUtils;
import com.vortex.web.diff.DiffableArchiveNode;
import com.vortex.web.messages.VortexBackupIdInterPortletMessage;
import com.vortex.web.portlet.grids.VortexFileComparePortlet;
import com.vortex.web.portlet.trees.VortexWebDiffTreePortlet;

public class VortexWebBackupFilesTablePortlet extends VortexWebTablePortlet implements VortexClientMachineListener, WebContextMenuFactory, WebContextMenuListener {

	final private LongKeyMap<Row> rows = new LongKeyMap<Row>();
	private BasicPortletSocket backupIdSocket;
	private boolean isHistory;
	private String currentAction;
	private VortexEyeQueryDataRequest currentRequest;
	private Logger log;

	public VortexWebBackupFilesTablePortlet(PortletConfig config, boolean isHistory) {
		super(config, null);
		this.isHistory = isHistory;
		String[] ids = { "bfid", "buid", "cs", "mask", "mtime", "path", "fullpath", "rev", "now", "size", "status", "vvid", "data" };
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("Managed Files");
		SmartTable st = new BasicSmartTable(inner);

		MapWebCellFormatter<Byte> stateFormatter = new MapWebCellFormatter<Byte>(getManager().getTextFormatter());
		stateFormatter.addEntry(VortexAgentBackupFile.STATUS_JUST_UPDATED, "Change Just Captured", "_cna=blue");
		stateFormatter.addEntry(VortexAgentBackupFile.STATUS_NONE, "Monitoring", "_cna=");
		stateFormatter.addEntry(VortexAgentBackupFile.STATUS_UNSTABLE, "Change Detected", "_cna=green");
		stateFormatter.addEntry(VortexAgentBackupFile.STATUS_OFFLINE, "Agent Not Running", "_cna=yellow");
		stateFormatter.addEntry((byte) 10, "Exceeds Max size", "_cna=pink");
		stateFormatter.addEntry((byte) 11, "Deleted", "_cna=red");
		stateFormatter.setDefaultWidth(70).lockFormatter();

		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());
		table.addColumn(false, "Id", "bfid", service.getIdFormatter("BF-"));
		table.addColumn(false, "File Id", "buid", service.getIdFormatter("BU-"));
		table.addColumn(false, "Full Path", "fullpath", service.getBasicFormatter()).setWidth(300);
		table.addColumn(true, "Path", "path", service.getBasicFormatter()).setWidth(200);
		table.addColumn(true, "Revisions", "rev", service.getNumberFormatter()).setWidth(25);
		table.addColumn(true, "Checksum", "cs", service.getChecksumFormatter());
		table.addColumn(true, "Mode", "mask", service.getFileMaskFormatter());
		table.addColumn(true, "Size", "size", service.getMemoryFormatter());
		table.addColumn(true, "Status", "status", stateFormatter);
		table.addColumn(false, "Vault Id", "vvid", service.getIdFormatter("VV-"));
		table.addColumn(true, "Modified Time", "mtime", service.getDateTimeWebCellFormatter());
		table.setMenuFactory(this);
		this.backupIdSocket = addSocket(false, "sendBuid", "Send Backup ID", true, null, CH.s(VortexBackupIdInterPortletMessage.class));
		setTable(table);
		if (!isHistory)
			agentManager.addMachineListener(this);
		else {
			getTable().sortRows("mtime", false, false, false);
			getTable().sortRows("rev", false, false, true);
		}

		if (!isHistory)
			for (VortexClientBackupFile bp : service.getAgentManager().getBackupFiles())
				onMachineEntityAdded(bp);
	}

	@Override
	public void close() {
		if (!isHistory)
			agentManager.removeMachineListener(this);
		super.close();
	}
	@Override
	public void onMachineAdded(VortexClientMachine machine) {
	}

	@Override
	public void onMachineUpdated(VortexClientMachine machine) {
	}

	@Override
	public void onMachineStale(VortexClientMachine machine) {
	}

	@Override
	public void onMachineEntityAdded(VortexClientEntity<?> node) {
		if (getIsEyeConnected()) {
			if (node.getType() == VortexAgentEntity.TYPE_BACKUP_FILE) {
				VortexClientBackupFile t = (VortexClientBackupFile) node;
				addBackupRow(t);
			}
		}
	}

	public void addBackupRow(VortexClientBackupFile node) {
		VortexAgentBackupFile bd = node.getData();
		long id = node.getId();
		long buid = bd.getBackupId();
		long cs = bd.getChecksum();
		short mask = bd.getMask();
		long mtime = bd.getModifiedTime();
		String path = node.getPathWithoutRoot(agentManager);//TODO: if path changes
		String fullpath = bd.getPath();
		byte status = bd.getStatus();
		int rev = bd.getRevision();
		long now = bd.getNow();
		long size = bd.getSize();
		long vvid = bd.getDataVvid();
		if (rev == VortexAgentEntity.REVISION_DONE)
			status = 11;
		else if (vvid < 1)
			status = 10;
		Row existing = rows.get(id);
		if (existing == null || isHistory) {
			rows.put(node.getId(), addRow(id, buid, cs, mask, mtime, path, fullpath, rev, now, size, status, vvid, node));
		} else {
			existing.put("buid", buid);
			existing.put("cs", cs);
			existing.put("mask", mask);
			existing.put("mtime", mtime);
			existing.put("path", path);
			existing.put("fullpath", fullpath);
			existing.put("rev", rev);
			existing.put("now", now);
			existing.put("size", size);
			existing.put("vvid", vvid);
			existing.put("status", status);
		}
	}
	private void removeBackupFile(VortexClientBackupFile node) {
		Row existing = rows.remove(node.getId());
		if (existing != null)
			removeRow(existing);
	}

	@Override
	public void onMachineEntityUpdated(VortexClientEntity<?> node) {
		if (node == null)
			return;
		if (getIsEyeConnected())
			switch (node.getType()) {
				case VortexAgentEntity.TYPE_BACKUP_FILE: {
					addBackupRow((VortexClientBackupFile) node);
					break;
				}
			}
	}
	@Override
	public void onMachineEntityRemoved(VortexClientEntity<?> node) {
		if (getIsEyeConnected())
			if (node.getType() == VortexAgentEntity.TYPE_BACKUP_FILE) {
				VortexClientBackupFile rule = (VortexClientBackupFile) node;
				removeBackupFile(rule);
			}
	}

	@Override
	public void onEyeDisconnected() {
		clearRows();
		rows.clear();
	}

	@Override
	protected void onEyeSnapshotProcessed() {
		super.onEyeSnapshotProcessed();
		for (VortexClientBackupFile backup : agentManager.getBackupFiles())
			addBackupRow(backup);
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebBackupFilesTablePortlet> {

		public static final String ID = "BackupFileTablePortlet";

		public Builder() {
			super(VortexWebBackupFilesTablePortlet.class);
		}

		@Override
		public VortexWebBackupFilesTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebBackupFilesTablePortlet(portletConfig, false);
		}

		@Override
		public String getPortletBuilderName() {
			return "Backup File Table";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		currentAction = action;
		if ("history".equals(action)) {
			LongSet ids = new LongSet();
			for (Row row : table.getSelectedRows()) {
				ids.add(row.get("bfid", Long.class));
			}
			VortexEyeQueryDataRequest request = nw(VortexEyeQueryDataRequest.class);
			request.setIds(ids.toLongArray());
			request.setType(VortexAgentEntity.TYPE_BACKUP_FILE);
			service.sendRequestToBackend(getPortletId(), request);
			currentRequest = request;
		} else if ("view".equals(action) || "compare".equals(action) || action.startsWith("download")) {
			LongSet ids = new LongSet();
			for (Row row : table.getSelectedRows()) {
				ids.add(row.get("data", VortexClientBackupFile.class).getData().getDataVvid());
			}
			VortexEyeQueryDataRequest request = nw(VortexEyeQueryDataRequest.class);
			request.setIds(ids.toLongArray());
			request.setType(VortexAgentEntity.TYPE_VAULT_ENTRY);
			service.sendRequestToBackend(getPortletId(), request);
			currentRequest = request;
		}
	}
	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {

	}

	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
		super.onSelectedChanged(fastWebTable);
	}

	@Override
	public WebMenu createMenu(WebTable table) {
		List<WebMenuItem> items = new ArrayList<WebMenuItem>();
		int count = table.getSelectedRows().size();
		if (count >= 1)
			items.add(new BasicWebMenuLink("Show History", true, "history"));
		if (count == 2)
			items.add(new BasicWebMenuLink("Compare Files", true, "compare"));
		if (count == 1) {
			VortexClientBackupFile row = table.getSelectedRows().get(0).get("data", VortexClientBackupFile.class);
			if (row.getData().getDataVvid() > 0) {
				items.add(new BasicWebMenuLink("Download", true, "download1"));
				BasicWebMenu dlm = new BasicWebMenu("Download with Special Name", true, new ArrayList<WebMenuItem>());
				dlm.addChild(new BasicWebMenuLink("as: " + format(row.getData(), 2), true, "download2"));
				dlm.addChild(new BasicWebMenuLink("as: " + format(row.getData(), 3), true, "download3"));
				items.add(dlm);
				items.add(new BasicWebMenuLink("View File", true, "view"));
			}
		}

		//int sel = table.getSelectedRows().size();
		//if (sel > 0) {
		//items.add(new BasicWebMenuLink("Run Backup(s)", true, "run"));
		//if (sel == 1) {
		//items.add(new BasicWebMenuLink("Copy Backup", true, "copy"));
		//items.add(new BasicWebMenuLink("Edit Backup", true, "edit"));
		//}
		//items.add(new BasicWebMenuLink("Delete Backup", true, "delete"));
		//items.add(new BasicWebMenuLink("Show Files", true, "showfiles"));
		//}
		//items.add(new BasicWebMenuLink("Create Backup", true, "create"));
		return new BasicWebMenu("", true, items);
	}

	private String format(VortexAgentBackupFile row, int style) {
		String path = SH.afterLast(row.getPath(), '/');
		Formatter yyyymmdd = getManager().getLocaleFormatter().getDateFormatter(LocaleFormatter.DATE);
		Formatter hhmmss = getManager().getLocaleFormatter().getDateFormatter(LocaleFormatter.HHMMSS);
		int i = path.lastIndexOf('.');
		if (i == -1)
			i = path.length();
		switch (style) {
			case 1:
				return path;
			case 2:
				return path.substring(0, i) + ".rev_" + row.getRevision() + path.substring(i);
			case 3:
				return path.substring(0, i) + "." + yyyymmdd.format(row.getModifiedTime()) + "_" + hhmmss.format(row.getModifiedTime()) + path.substring(i);
		}
		return path;
	}
	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		VortexEyeQueryDataRequest request = (VortexEyeQueryDataRequest) result.getRequestMessage().getAction();
		if (request != currentRequest)
			return;
		VortexEyeQueryDataResponse response = (VortexEyeQueryDataResponse) result.getAction();
		if (request.getType() == VortexAgentEntity.TYPE_BACKUP_FILE) {
			VortexWebBackupFilesTablePortlet p = new VortexWebBackupFilesTablePortlet(generateConfig(), true);
			for (VortexEntity row : response.getData()) {
				p.addBackupRow(new VortexClientBackupFile((VortexAgentBackupFile) row));
			}
			getManager().showDialog("File History", p);
		} else if (request.getType() == VortexAgentEntity.TYPE_VAULT_ENTRY) {

			List<VortexAgentBackupFile> files = new ArrayList<VortexAgentBackupFile>();
			for (VortexEntity row : response.getData()) {
				VortexVaultEntry vaultEntry = (VortexVaultEntry) row;
				VortexAgentBackupFile file = getFileForVvid(vaultEntry.getId()).clone();
				file.setData(vaultEntry.getData());
				VortexClientUtils.decompressFile(file);
				files.add(file);
			}
			if ("view".equals(currentAction)) {
				VortexAgentBackupFile file = files.get(0);
				int type = FileMagic.getType(file.getData());
				if (type != FileMagic.FILE_TYPE_UNKNOWN) {
					ArchiveFileReader afr = new ArchiveFileReader();
					try {
						DiffableArchiveNode left = new DiffableArchiveNode(afr.read("", file.getData()));
						VortexWebDiffTreePortlet p = new VortexWebDiffTreePortlet(generateConfig(), file.getPath() + " (rev " + file.getRevision() + ")", left, null, null);
						getManager().showDialog("File: " + file.getPath() + " (rev-" + file.getRevision() + ")", p);
					} catch (IOException e) {
						LH.warning(log, "error processin diff", e);
					}
				} else {
					SimpleFastTextPortlet text = new SimpleFastTextPortlet(generateConfig());
					String[] lines = SH.splitLines(new String(file.getData()));
					int cnt = 0;
					for (String line : lines)
						text.appendLine(SH.toString(++cnt), line);
					getManager().showDialog("File: " + file.getPath() + " (rev-" + file.getRevision() + ")", text);
				}
			} else if ("compare".equals(currentAction)) {
				VortexAgentBackupFile file = files.get(0);
				VortexAgentBackupFile file2 = files.get(1);

				int type = FileMagic.getType(file.getData());
				int type2 = FileMagic.getType(file.getData());
				if (type == type2) {
					ArchiveFileReader afr = new ArchiveFileReader();
					try {
						DiffableArchiveNode left = new DiffableArchiveNode(afr.read("", file.getData()));
						DiffableArchiveNode right = new DiffableArchiveNode(afr.read("", file2.getData()));
						VortexWebDiffTreePortlet p = new VortexWebDiffTreePortlet(generateConfig(), file.getPath() + " (rev " + file.getRevision() + ")", left, file2.getPath()
								+ " (rev " + file2.getRevision() + ")", right);
						getManager().showDialog("File: " + file.getPath() + " (rev-" + file.getRevision() + ") vs " + file2.getPath() + " (rev-" + file2.getRevision() + ")", p);
					} catch (IOException e) {
						LH.warning(log, "error processin diff", e);
					}
				} else {
					VortexFileComparePortlet p = new VortexFileComparePortlet(generateConfig());
					p.setText(new String(file.getData()), new String(file2.getData()));
					if (OH.eq(file.getPath(), file2.getPath()))
						getManager().showDialog("File: " + file.getPath() + " (rev-" + file.getRevision() + " vs rev-" + file2.getRevision() + ")", p);
					else
						getManager().showDialog("File: " + file.getPath() + " (rev-" + file.getRevision() + ") vs " + file2.getPath() + " (rev-" + file2.getRevision() + ")", p);
				}
			} else if (currentAction.startsWith("download")) {
				for (VortexAgentBackupFile file : files) {
					PortletDownload download = new BasicPortletDownload(format(file, Integer.parseInt(SH.stripPrefix(currentAction, "download", true))), file.getData());
					getManager().pushPendingDownload(download);
				}
			}
		} else
			super.onBackendResponse(result);
	}
	private VortexAgentBackupFile getFileForVvid(long id) {
		for (Row row : getTable().getTable().getRows()) {
			VortexAgentBackupFile f = row.get("data", VortexClientBackupFile.class).getData();
			if (f.getDataVvid() == id)
				return f;
		}
		return null;
	}

	@Override
	public void onMachineRemoved(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket remoteSocket, InterPortletMessage message) {
		if (localSocket == backupIdSocket) {
			VortexBackupIdInterPortletMessage msg = (VortexBackupIdInterPortletMessage) message;
			getTable().setExternalFilter(new WebTableFilteredSetFilter(getTable().getColumn("buid"), msg.getBackupIds()));
			onVortexRowsChanged();
		} else {
			super.onMessage(localSocket, remoteSocket, message);
		}
	}

	protected void onVortexRowsChanged() {
	}

}
