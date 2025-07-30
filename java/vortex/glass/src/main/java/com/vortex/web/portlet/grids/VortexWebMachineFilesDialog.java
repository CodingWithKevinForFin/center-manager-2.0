package com.vortex.web.portlet.grids;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.f1.base.Action;
import com.f1.base.TableListenable;
import com.f1.container.ResultMessage;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.BasicPortletDownload;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.text.SimpleFastTextPortlet;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.suite.web.table.impl.MapWebCellFormatter;
import com.f1.suite.web.tree.WebTreeContextMenuFactory;
import com.f1.suite.web.tree.WebTreeContextMenuListener;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.WebTreeNodeListener;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.utils.CH;
import com.f1.utils.Formatter;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.VH;
import com.f1.utils.structs.MapInMap;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.Tuple3;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.vortexcommon.msg.VortexEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentBackupFile;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentEvent;
import com.f1.vortexcommon.msg.agent.VortexAgentFile;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileDeleteRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileSearchRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileSearchResponse;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentResponse;
import com.f1.vortexcommon.msg.eye.VortexEyeBackup;
import com.f1.vortexcommon.msg.eye.VortexVaultEntry;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageBackupRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyePassToAgentRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyePassToAgentResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeQueryDataRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeQueryDataResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeResponse;
import com.vortex.client.VortexClientBackup;
import com.vortex.client.VortexClientBackupFile;
import com.vortex.client.VortexClientEntity;
import com.vortex.client.VortexClientMachine;
import com.vortex.client.VortexClientMachineListener;
import com.vortex.client.VortexClientUtils;
import com.vortex.web.VortexWebEyeService;
import com.vortex.web.portlet.forms.VortexWebBackupFormPortlet;
import com.vortex.web.portlet.forms.VortexWebCommentFormPortlet;
import com.vortex.web.portlet.tables.VortexWebBackupFilesTablePortlet;
import com.vortex.web.portlet.visuals.VortexTerminalPortlet;

public class VortexWebMachineFilesDialog extends GridPortlet implements WebTreeContextMenuListener, WebTreeNodeListener, WebTreeContextMenuFactory, VortexClientMachineListener,
		Comparator<WebTreeNode>, ConfirmDialogListener {

	private static final byte TYPE_NONE = 0;
	private static final byte TYPE_DOWNLOAD = 1;
	private static final byte TYPE_GETFILE = 2;
	private static final byte TYPE_COMPARE = 3;
	private static final byte TYPE_GETDIR = 4;
	private static final byte TYPE_GET_BACKUP_HISTORY = 5;
	private static final byte TYPE_GET_BACKUP_FILE = 6;
	private static final byte TYPE_DOWNLOAD_BACKUP_FILE = 7;
	private static final byte TYPE_GET_DELETED_HISTORY = 8;
	private static final byte TYPE_DELETE_FILE = 9;

	private static final int MAX_DIFF_SIZE = 1024 * 1024 * 2;
	private static final int MAX_HISTORY_SIZE = 1024 * 1024 * 100;
	private static final int MAX_DOWNLOAD_SIZE = 1024 * 1024 * 100;
	private static final byte MSG_NONE = 0;
	private static final byte MSG_WAIT = 1;
	private static final byte MSG_ERROR = 2;
	final private FastTreePortlet tree;
	final private DividerPortlet divider;
	final private VortexFileTextPortlet textContentsPortlet;
	final private FastTablePortlet tableContentsPortlet;
	final private VortexWebEyeService service;
	private VortexEyeRequest lastRequest;
	private WebTreeNode lastNode;
	private byte requestType;
	final private GridPortlet contentsPane;
	final private HtmlPortlet contentsBlankPortlet;
	private Portlet activePortlet;
	private TableListenable filesTable;
	private VortexFileComparePortlet diffContentsPortlet;
	private VortexTerminalPortlet terminalPortlet;
	private VortexEyeRequest diffRequest;
	private byte[] diffResponseFileData;
	private MapInMap<String, String, WebTreeNode> muidToPathToNode = new MapInMap<String, String, WebTreeNode>();
	private SimpleFastTextPortlet simpleTextPortlet;
	private Formatter dateFormatter;
	private BasicWebCellFormatter checkSumFormatter;
	private WebTreeNode diffNode;
	private WebTreeNode deleteTarget;

	public VortexWebMachineFilesDialog(PortletConfig config, List<Tuple3<String, VortexAgentFile, String>> hostAndDir) {
		super(config);

		tree = new FastTreePortlet(generateConfig());
		tree.getTree().setAutoExpandUntilMultipleNodes(true);
		tree.getTree().setRootLevelVisible(false);
		tree.getTree().addMenuContextListener(this);
		tree.getTree().getTreeManager().addListener(this);
		tree.getTree().setContextMenuFactory(this);
		tree.getTree().getTreeManager().setComparator(this);
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		this.service.getAgentManager().addMachineListener(this);
		for (Tuple3<String, VortexAgentFile, String> f : hostAndDir) {
			Map<String, VortexClientBackup> backups = service.getAgentManager().getBackupsByPathForMachine(f.getA());
			FileEntry fe = new FileEntry(f.getA(), f.getB(), f.getC(), false);
			tree.createNode(f.getC(), tree.getTreeManager().getRoot(), false).setIcon("portlet_icon_host").setData(fe).setCssClass("clickable").setIsExpandable(true);

			if (f.getB() != null) {
				VortexClientBackup backup = findBackup(backups, f.getB().getPath());
				if (backup != null) {
					fe.isManaged = true;
					fe.backup = backup;
				}
			}
			f.lock();
		}
		textContentsPortlet = new VortexFileTextPortlet(generateConfig());
		simpleTextPortlet = new SimpleFastTextPortlet(generateConfig());
		this.dateFormatter = getManager().getLocaleFormatter().getDateFormatter(LocaleFormatter.DATETIME);
		this.checkSumFormatter = service.getChecksumFormatter();
		this.filesTable = new BasicTable(new String[] { "name", "path", "size", "modified", "mask", "isdir", "data" });
		BasicSmartTable st = new BasicSmartTable(filesTable);
		st.setTitle("Files");
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());
		MapWebCellFormatter<Boolean> typeFormatter = new MapWebCellFormatter<Boolean>(getManager().getTextFormatter());
		typeFormatter.addEntry(true, "D", "_cna=portlet_icon_folder", "&nbsp;&nbsp;&nbsp;&nbsp;");
		typeFormatter.addEntry(false, "F", "_cna=portlet_icon_file", "&nbsp;&nbsp;&nbsp;&nbsp;");
		typeFormatter.setDefaultWidth(70).lockFormatter();

		table.addColumn(true, "Type", "isdir", typeFormatter).setWidth(23);
		table.addColumn(true, "File", "name", service.getFilenameFormatter()).setWidth(250);
		table.addColumn(true, "Size", "size", service.getMemoryFormatter());
		table.addColumn(true, "Modified", "modified", service.getDateTimeWebCellFormatter());
		table.addColumn(true, "Mask", "mask", service.getFileMaskFormatter());
		table.sortRows("name", true, true, false);
		this.tableContentsPortlet = new FastTablePortlet(generateConfig(), table);
		this.diffContentsPortlet = new VortexFileComparePortlet(generateConfig());
		this.terminalPortlet = new VortexTerminalPortlet(generateConfig(), getManager().getState().getWebState().getUser().getUserName(), null, null);

		divider = new DividerPortlet(generateConfig(), true);
		divider.addChild(tree);
		divider.addChild(contentsPane = new GridPortlet(generateConfig()));
		addChild(divider, 0, 0);
		divider.setOffset(.15);
		contentsBlankPortlet = new HtmlPortlet(generateConfig());
		setActiveContent(contentsBlankPortlet);
		getManager().onPortletAdded(this);
		if (hostAndDir.size() == 1)
			onNodeClicked(tree.getTree(), tree.getTreeManager().getRoot().getChildAt(0));
	}
	static private VortexClientBackup findBackup(Map<String, VortexClientBackup> backups, String path) {
		if (!path.endsWith("/"))
			path += '/';
		//System.out.println("--------------");
		for (;;) {
			//System.out.println("CHECKING: " + path);
			VortexClientBackup r = backups.get(path);
			if (r != null) {
				return r;
			} else if (path.length() < 2) {
				return null;
			} else {
				int i = path.lastIndexOf('/', path.length() - 2);
				if (i == -1)
					return null;
				path = path.substring(0, i + 1);

			}
		}
	}
	@Override
	public void close() {
		this.service.getAgentManager().removeMachineListener(this);
		super.close();
	}

	@Override
	public void onContextMenu(FastWebTree tree, String action) {
		List<WebTreeNode> selected = tree.getSelected();
		if ("compare".equals(action)) {
			if (lastRequest != null) {
				getManager().showAlert("Please try again after pending request");
				return;
			}
			if (selected.size() == 2) {
				final FileEntry tuple1 = getData(selected.get(0));
				final VortexAgentFile file1 = tuple1.getFile();

				final FileEntry tuple2 = getData(selected.get(1));
				final VortexAgentFile file2 = tuple2.getFile();

				if (file1.getSize() > MAX_DIFF_SIZE || file2.getSize() > MAX_DIFF_SIZE) {
					setMessage("Can not diff files that are larger than " + SH.formatMemory(MAX_DIFF_SIZE), MSG_ERROR);
					return;
				}

				VortexEyeRequest req1 = toFileRequest(tuple1, MAX_DIFF_SIZE, null, true);
				this.lastRequest = req1;

				VortexEyeRequest req2 = toFileRequest(tuple2, MAX_DIFF_SIZE, null, true);
				this.diffRequest = req2;
				this.diffNode = selected.get(1);

				this.lastNode = selected.get(0);
				this.requestType = TYPE_COMPARE;
				setMessage("Retrieving files for comparison: " + file1.getPath() + ", " + file2.getPath(), MSG_WAIT);
				service.sendRequestToBackend(getPortletId(), req1);
				this.diffContentsPortlet.setTitles(getFileName(file1), getFileName(file2));
				this.diffContentsPortlet.setText("", "");
			}
		} else if ("unmanage".equals(action)) {
			final FileEntry tuple1 = getData(selected.get(0));
			VortexClientBackup backup = tuple1.getBackup();
			List<VortexEyeRequest> reqs = new ArrayList<VortexEyeRequest>();
			VortexEyeManageBackupRequest request = nw(VortexEyeManageBackupRequest.class);
			VortexEyeBackup exp = nw(VortexEyeBackup.class);
			exp.setId(backup.getId());
			exp.setRevision(VortexAgentEntity.REVISION_DONE);
			request.setBackup(exp);
			reqs.add(request);
			getManager().showDialog("Delete Managed Directory",
					new VortexWebCommentFormPortlet(generateConfig(), getPortletId(), reqs, "Delete Managed Directory", "backupdest.jpg").setIconToDelete());
		} else if ("edit_managed".equals(action)) {
			final FileEntry tuple1 = getData(selected.get(0));
			VortexClientBackup backup = tuple1.getBackup();
			VortexWebBackupFormPortlet form = new VortexWebBackupFormPortlet(generateConfig());
			form.setBackupToEdit(backup);
			getManager().showDialog("Edit Managed Directory", form);
		} else if ("manage".equals(action)) {
			final FileEntry tuple1 = getData(selected.get(0));
			VortexWebBackupFormPortlet bf = new VortexWebBackupFormPortlet(generateConfig());
			bf.setMachineUid(tuple1.getMuid());
			bf.setDirectory(tuple1.getFile().getPath());
			getManager().showDialog("Add Managed Directory", bf);
		} else if ("terminal".equals(action)) {
			final FileEntry tuple1 = getData(selected.get(0));
			VortexAgentFile file = tuple1.getFile();
			if (file == null)
				this.terminalPortlet.setPwd("/");
			else if (MH.allBits(file.getMask(), VortexAgentFile.DIRECTORY))
				this.terminalPortlet.setPwd(tuple1.getFile().getPath());
			else
				this.terminalPortlet.setPwd(SH.beforeLast(tuple1.getFile().getPath(), '/'));
			this.terminalPortlet.setMachineUid(tuple1.getMuid());
			this.terminalPortlet.logLocation("Terminal started at ");
			setActiveContent(this.terminalPortlet);
		} else if ("deleted".equals(action)) {
			WebTreeNode node = selected.get(0);
			FileEntry muidAndFile = getData(node);
			while (muidAndFile.getBackup() == null) {
				node = node.getParent();
				muidAndFile = getData(node);
			}
			VortexEyeQueryDataRequest eyeReq = nw(VortexEyeQueryDataRequest.class);
			eyeReq.setIds(new long[] { muidAndFile.getBackup().getId() });
			eyeReq.setType(VortexAgentEntity.TYPE_BACKUP_FILE);
			eyeReq.setSearchDeleted(true);
			this.requestType = TYPE_GET_DELETED_HISTORY;
			eyeReq.setSearchExpression(getData(selected.get(0)).getFile().getPath() + "%");
			service.sendRequestToBackend(getPortletId(), eyeReq);
			this.lastRequest = eyeReq;
			this.lastNode = null;
		} else if ("download".equals(action)) {
			if (lastRequest != null) {
				getManager().showAlert("Please try again after pending request");
				return;
			}
			if (selected.size() == 1) {
				FileEntry muidAndFile = getData(selected.get(0));
				VortexAgentFile file = muidAndFile.getFile();
				if (file != null && MH.allBits(file.getMask(), VortexAgentFile.FILE)) {
					if (file.getSize() > 1024 * 1024 * 100) {
						getManager().showAlert("File too large for download, exceeds 100MB");
					} else if (file instanceof VortexAgentBackupFile) {
						VortexEyeRequest request = toFileRequest(muidAndFile, MAX_DOWNLOAD_SIZE, null, true);
						service.sendRequestToBackend(getPortletId(), request);
						this.lastRequest = request;
						this.requestType = TYPE_DOWNLOAD_BACKUP_FILE;
						this.lastNode = selected.get(0);
					} else {
						setMessage("Preparing download for " + muidAndFile.getFile().getPath(), MSG_WAIT);
						VortexEyeRequest eyeReq = toFileRequest(muidAndFile, MAX_DOWNLOAD_SIZE, null, true);
						this.lastRequest = eyeReq;
						this.requestType = TYPE_DOWNLOAD;
						this.lastNode = null;
						service.sendRequestToBackend(getPortletId(), eyeReq);
					}
				}
			}
		} else if ("add_file".equals(action)) {
			getManager().showAlert("Feature not available yet");
		} else if ("add_dir".equals(action)) {
			getManager().showAlert("Feature not available yet");
		} else if ("del_file".equals(action)) {
			if (selected.size() == 1) {
				FileEntry muidAndFile = getData(selected.get(0));
				getManager().showDialog(
						"Delete File",
						new ConfirmDialogPortlet(generateConfig(), "Delete File:<BR><B>" + muidAndFile.getFile().getPath() + "</B>", ConfirmDialogPortlet.TYPE_YES_NO, this)
								.setCorrelationData(new Tuple2("DELETE", selected.get(0))));
			}
		} else if ("del_dir".equals(action)) {
			FileEntry muidAndFile = getData(selected.get(0));
			getManager().showDialog(
					"Delete Directory",
					new ConfirmDialogPortlet(generateConfig(), "Delete Directory and child contents:<BR><B>" + muidAndFile.getFile().getPath() + "</B>",
							ConfirmDialogPortlet.TYPE_YES_NO, this).setCorrelationData(new Tuple2("DELETE", selected.get(0))));
		}

	}
	@Override
	public void onNodeClicked(FastWebTree tree, WebTreeNode node) {
		if (lastRequest != null)
			return;
		if (node != null) {
			FileEntry muidAndFile = getData(node);
			VortexAgentFile file = muidAndFile.getFile();
			if (file != null) {
				if (file instanceof VortexAgentBackupFile) {
					VortexEyeRequest eyeReq = toFileRequest(muidAndFile, MAX_HISTORY_SIZE, null, true);
					service.sendRequestToBackend(getPortletId(), eyeReq);
					this.lastRequest = eyeReq;
					this.lastNode = node;
					this.requestType = TYPE_GET_BACKUP_FILE;
					setMessage("Retrieving file contents from vault: " + file.getPath(), MSG_WAIT);
				} else if (MH.allBits(file.getMask(), VortexAgentFile.FILE)) {
					List<String> searches = new ArrayList<String>();
					searches.add("\n");
					searches.addAll(this.textContentsPortlet.getActiveSearches());
					VortexEyeRequest eyeReq = toFileRequest(muidAndFile, 1024 * 1024, searches, this.textContentsPortlet.getActiveSearchesAreCaseSensitive());
					this.lastRequest = eyeReq;
					this.lastNode = null;
					this.requestType = TYPE_GETFILE;
					setMessage("Retrieving file contents: " + file.getPath(), MSG_WAIT);
					service.sendRequestToBackend(getPortletId(), eyeReq);
				} else if (MH.allBits(file.getMask(), VortexAgentFile.DIRECTORY)) {

					this.filesTable.getRows().clear();
					setMessage("Retrieving directory contents for: " + muidAndFile.getFile().getPath(), MSG_WAIT);
					VortexEyeRequest eyeReq = toFileRequest(muidAndFile, 0, null, true);

					service.sendRequestToBackend(getPortletId(), eyeReq);
					this.lastRequest = eyeReq;
					this.lastNode = node;
					this.requestType = TYPE_GETDIR;
					node.setIsExpanded(true);
				}
			} else if (node.getParent() == this.tree.getTreeManager().getRoot()) {//is a host node
				node.setIsExpanded(!node.getIsExpanded());
			}
		}
	}

	private void setMessage(String string, byte type) {
		switch (type) {
			case MSG_WAIT:
				this.contentsBlankPortlet.setCssClass("html_wait");
				break;
			case MSG_NONE:
				this.contentsBlankPortlet.setCssClass("html_portlet");
				break;
			case MSG_ERROR:
				this.contentsBlankPortlet.setCssClass("html_error");
				break;
		}
		this.contentsBlankPortlet.setHtml("<div>" + string + "</div>");
		setActiveContent(this.contentsBlankPortlet);
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		if (lastRequest != result.getRequestMessage().getAction())
			return;
		VortexEyeResponse eyeResponse = (VortexEyeResponse) result.getAction();
		if (!eyeResponse.getOk()) {
			lastRequest = null;
			this.requestType = TYPE_NONE;
			setMessage(eyeResponse.getMessage(), MSG_ERROR);
			return;
		}
		VortexAgentResponse agentResponse;
		if (eyeResponse instanceof VortexEyePassToAgentResponse) {
			agentResponse = ((VortexEyePassToAgentResponse) eyeResponse).getAgentResponse();
			if (!agentResponse.getOk()) {
				lastRequest = null;
				this.requestType = TYPE_NONE;
				if (agentResponse.getMessage() != null)
					setMessage(agentResponse.getMessage(), MSG_ERROR);
				else
					setMessage("Backend error:" + agentResponse.getClass().getName(), MSG_ERROR);
				return;
			}
		} else
			agentResponse = null;
		lastRequest = null;
		byte reqType = this.requestType;
		this.requestType = TYPE_NONE;
		switch (reqType) {
			case TYPE_COMPARE: {
				//List<VortexAgentFile> files = ((VortexAgentFileSearchResponse) agentResponse).getFiles();
				VortexAgentFile file = toFile(eyeResponse);// files.get(0);
				byte[] data = file.getData();
				if (data == null) {
					setMessage("File not available for comparison", MSG_ERROR);
					diffRequest = null;
					diffResponseFileData = null;
				} else {
					if (diffRequest != null) {
						service.sendRequestToBackend(getPortletId(), diffRequest);
						this.requestType = TYPE_COMPARE;
						lastRequest = diffRequest;
						lastNode = diffNode;
						diffNode = null;
						diffRequest = null;
						diffResponseFileData = data;
					} else {
						this.diffContentsPortlet.setText(new String(diffResponseFileData), new String(data));
						setMessage("", MSG_NONE);
						setActiveContent(this.diffContentsPortlet);
					}
				}
				break;
			}
			case TYPE_DOWNLOAD: {
				VortexEyePassToAgentRequest req = (VortexEyePassToAgentRequest) result.getRequestMessage().getAction();
				List<VortexAgentFile> files = ((VortexAgentFileSearchResponse) agentResponse).getFiles();
				VortexAgentFile file = files.get(0);
				VortexClientUtils.decompressFile(file);
				if (file.getData() == null)
					setMessage("File not available for download", MSG_ERROR);
				else {
					getManager().pushPendingDownload(new BasicPortletDownload(getFileName(file.getPath()), file.getData()));
					setMessage("", MSG_NONE);
				}
				break;
			}
			case TYPE_GETFILE: {
				VortexEyePassToAgentRequest req = (VortexEyePassToAgentRequest) result.getRequestMessage().getAction();
				List<VortexAgentFile> files = ((VortexAgentFileSearchResponse) agentResponse).getFiles();
				VortexAgentFile file = files.get(0);
				VortexClientUtils.decompressFile(file);
				textContentsPortlet.initFromFile(file, req.getAgentMachineUid());
				setActiveContent(textContentsPortlet);
				break;
			}
			case TYPE_GETDIR: {
				VortexEyePassToAgentRequest req = (VortexEyePassToAgentRequest) result.getRequestMessage().getAction();
				List<VortexAgentFile> files = ((VortexAgentFileSearchResponse) agentResponse).getFiles();
				String muid = req.getAgentMachineUid();
				while (lastNode.getChildrenCount() > 0)
					lastNode.removeChild(lastNode.getChildAt(0));
				Map<String, VortexClientBackup> backups = service.getAgentManager().getBackupsByPathForMachine(muid);
				FileEntry parent = getData(lastNode);
				this.filesTable.getRows().clear();
				for (VortexAgentFile file : files) {
					boolean isFile = MH.allBits(file.getMask(), VortexAgentFile.FILE);
					String path = file.getPath();
					String description = getFileName(path);
					boolean isManaged = false;
					VortexClientBackupFile backupFile = null;
					VortexClientBackup backup = null;
					if (parent.isManaged) {
						if (isFile) {
							backupFile = service.getAgentManager().getBackupFilesByPathForMachine(muid).get(path);
							if (backupFile != null)
								isManaged = true;
						} else
							isManaged = true;
					} else {
						backup = backups.get(file.getPath() + "/");
						if (backup != null) {
							isManaged = true;
						}
					}

					this.filesTable.getRows().addRow(description, file.getPath(), file.getSize(), file.getModifiedTime(), file.getMask(),
							MH.anyBits(file.getMask(), VortexAgentFile.DIRECTORY), file);

					if (file.getSize() > 0)
						description += " (" + SH.formatMemory(file.getSize()) + ")";
					WebTreeNode r = tree.getTreeManager().createNode(description, lastNode, false, new FileEntry(muid, file, description, isManaged))
							.setIcon(isManaged ? (isFile ? "portlet_icon_file_managed" : "portlet_icon_folder_managed") : (isFile ? "portlet_icon_file" : "portlet_icon_folder"));
					this.muidToPathToNode.putMulti(muid, isFile ? path : path + "/", r);
					r.setCssClass("clickable");
					if (!isFile)
						r.setIsExpandable(true);
					if (backupFile != null) {
						getData(r).backupFile = backupFile;
						if (backupFile.getData().getRevision() > 0) {
							r.setIsExpandable(true);
						}
					} else if (backup != null)
						getData(r).backup = backup;

				}
				setActiveContent(this.tableContentsPortlet);
				break;
			}
			case TYPE_GET_BACKUP_HISTORY: {
				while (lastNode.getChildrenCount() > 0)
					lastNode.removeChild(lastNode.getChildAt(0));
				VortexEyeQueryDataResponse qres = (VortexEyeQueryDataResponse) result.getAction();
				VH.sort(qres.getData(), VortexAgentBackupFile.PID_REVISION);
				FileEntry parent = getData(lastNode);
				VortexAgentBackupFile prior = null;
				VortexAgentBackupFile head = parent.getBackupFile().getData();
				for (VortexEntity row : qres.getData()) {
					VortexAgentBackupFile bf = (VortexAgentBackupFile) row;
					if (bf.getRevision() == head.getRevision() || (bf.getChecksum() == head.getChecksum() && bf.getSize() == head.getSize()))
						continue;
					if (prior != null && prior.getSize() == bf.getSize() && prior.getChecksum() == bf.getChecksum())
						continue;
					prior = bf;
					String description = describeRevision(bf);
					FileEntry fe = new FileEntry(null, bf, description, true);
					fe.setBackupFile(null);
					WebTreeNode r = tree.getTreeManager().createNode(description, lastNode, false, fe).setIcon("portlet_icon_stack");
					r.setCssClass("clickable");
				}
				break;
			}
			case TYPE_GET_BACKUP_FILE: {
				VortexEyeQueryDataResponse qres = (VortexEyeQueryDataResponse) result.getAction();
				VortexVaultEntry vve = (VortexVaultEntry) qres.getData().get(0);
				VortexAgentFile file = ((VortexAgentBackupFile) getData(this.lastNode).getFile()).clone();
				file.setData(vve.getData());
				VortexClientUtils.decompressFile(file);
				simpleTextPortlet.setLines(new String(file.getData()));
				setActiveContent(simpleTextPortlet);
				break;
			}
			case TYPE_DOWNLOAD_BACKUP_FILE: {
				VortexEyeQueryDataResponse qres = (VortexEyeQueryDataResponse) result.getAction();
				VortexVaultEntry vve = (VortexVaultEntry) qres.getData().get(0);
				VortexAgentFile file = ((VortexAgentBackupFile) getData(this.lastNode).getFile()).clone();
				file.setData(vve.getData());
				VortexClientUtils.decompressFile(file);
				if (file.getData() == null)
					setMessage("File not available for download", MSG_ERROR);
				else {
					getManager().pushPendingDownload(new BasicPortletDownload(getFileName(file.getPath()), file.getData()));
					setMessage("", MSG_NONE);
				}
				break;
			}
			case TYPE_GET_DELETED_HISTORY: {
				VortexEyeQueryDataResponse qres = (VortexEyeQueryDataResponse) result.getAction();
				List<VortexEntity> rows = qres.getData();
				VortexWebBackupFilesTablePortlet portlet = new VortexWebBackupFilesTablePortlet(generateConfig(), true);
				for (VortexEntity row : rows) {
					VortexAgentBackupFile file = (VortexAgentBackupFile) row;
					portlet.addBackupRow(new VortexClientBackupFile(file));
				}
				getManager().showDialog("Delete Files", portlet);
				break;
			}
			case TYPE_DELETE_FILE: {
				this.deleteTarget.getParent().removeChild(this.deleteTarget);
				this.deleteTarget = null;
				this.setMessage("Deleted", MSG_NONE);
				break;
			}
			default:
				super.onBackendResponse(result);
		}
	}
	private VortexAgentFile toFile(VortexEyeResponse eyeResponse) {
		VortexAgentResponse agentResponse;
		if (eyeResponse instanceof VortexEyePassToAgentResponse) {
			agentResponse = ((VortexEyePassToAgentResponse) eyeResponse).getAgentResponse();
			List<VortexAgentFile> files = ((VortexAgentFileSearchResponse) agentResponse).getFiles();
			VortexAgentFile file = files.get(0);
			VortexClientUtils.decompressFile(file);
			return file;
		} else if (eyeResponse instanceof VortexEyeQueryDataResponse) {
			VortexEyeQueryDataResponse qres = (VortexEyeQueryDataResponse) eyeResponse;
			VortexVaultEntry vve = (VortexVaultEntry) qres.getData().get(0);
			VortexAgentFile file = ((VortexAgentBackupFile) getData(this.lastNode).getFile()).clone();
			file.setData(vve.getData());
			VortexClientUtils.decompressFile(file);
			return file;
		} else
			return null;
	}
	private String describeRevision(VortexAgentBackupFile bf) {
		return "Rev " + bf.getRevision() + ": " + dateFormatter.format(bf.getNow()) + ", (" + SH.formatMemory(bf.getSize()) + ") "
				+ checkSumFormatter.formatCellToText(bf.getChecksum());
	}
	private void setActiveContent(Portlet portlet) {
		if (this.activePortlet == portlet)
			return;
		else if (this.activePortlet != null)
			contentsPane.removeChild(this.activePortlet.getPortletId());
		this.activePortlet = portlet;
		contentsPane.addChild(portlet, 0, 0);
		if (getVisible()) {
			getManager().onPortletAdded(portlet);
		}
	}
	private String getFileName(String path) {
		if (path != null && path.length() > 1 && path.charAt(1) == ':')
			return SH.afterLast(path, '\\');
		else
			return SH.afterLast(path, '/');
	}
	private String getFileName(VortexAgentFile file) {
		if (file instanceof VortexAgentBackupFile)
			return getFileName(file.getPath()) + " (REV - " + file.getRevision() + ")";
		else
			return getFileName(file.getPath());
	}
	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node) {

	}

	@Override
	public void onNodeAdded(WebTreeNode node) {

	}

	@Override
	public void onNodeRemoved(WebTreeNode node) {

	}

	@Override
	public void onStyleChanged(WebTreeNode node) {

	}

	@Override
	public void onExpanded(WebTreeNode node) {
		if (!node.getIsExpanded())
			return;
		//if (lastRequest != null)
		//return;
		if (node != null) {
			FileEntry muidAndFile = getData(node);
			VortexAgentFile file = muidAndFile.getFile();
			if (muidAndFile.isManaged && muidAndFile.backupFile != null) {
				VortexEyeQueryDataRequest eyeReq = nw(VortexEyeQueryDataRequest.class);
				eyeReq.setIds(new long[] { muidAndFile.getBackupFile().getId() });
				eyeReq.setType(VortexAgentEntity.TYPE_BACKUP_FILE);
				this.requestType = TYPE_GET_BACKUP_HISTORY;
				service.sendRequestToBackend(getPortletId(), eyeReq);
				this.lastRequest = eyeReq;
				this.lastNode = node;
			} else if (file == null || MH.allBits(file.getMask(), VortexAgentFile.DIRECTORY)) {
				VortexEyeRequest eyeReq = toFileRequest(muidAndFile, 0, Collections.EMPTY_LIST, false);
				this.requestType = TYPE_GETDIR;
				service.sendRequestToBackend(getPortletId(), eyeReq);
				this.lastRequest = eyeReq;
				this.lastNode = node;
				setMessage("Retrieving directory contents for: " + (muidAndFile.getFile() == null ? "<root>" : muidAndFile.getFile().getPath()), MSG_WAIT);
			}
		}
	}
	private VortexEyeRequest toFileRequest(FileEntry muidAndFile, int maxDataSize, List<String> searchExpressions, boolean caseSensitive) {
		VortexAgentFile file = muidAndFile.getFile();
		if (file instanceof VortexAgentBackupFile) {
			VortexEyeQueryDataRequest eyeReq = nw(VortexEyeQueryDataRequest.class);
			eyeReq.setIds(new long[] { ((VortexAgentBackupFile) file).getDataVvid() });
			eyeReq.setType(VortexAgentEntity.TYPE_VAULT_ENTRY);
			return eyeReq;
		}
		VortexEyePassToAgentRequest eyeReq = nw(VortexEyePassToAgentRequest.class);
		VortexAgentFileSearchRequest agentRequest = nw(VortexAgentFileSearchRequest.class);
		eyeReq.setAgentMachineUid(muidAndFile.getMuid());
		agentRequest.setRecurse(false);
		agentRequest.setMaxDataSize(maxDataSize);
		agentRequest.setDataOffset(0);
		if (maxDataSize > 0)
			agentRequest.setIncludeDataExpression("*");
		else
			agentRequest.setSearchExpression("*");
		if (CH.isntEmpty(searchExpressions)) {
			agentRequest.setIsSearchCaseSensitive(caseSensitive);
			agentRequest.setIncludeSearchPositionsExpression("*");
			agentRequest.setSearchInFileExpressions(searchExpressions);
		}
		agentRequest.setIncludeChecksumExpression("");
		if (file != null)
			agentRequest.setRootPaths(CH.l(file.getPath()));
		//textContentsPortlet.initFromFile(null, null);
		eyeReq.setAgentRequest(agentRequest);
		return eyeReq;
	}
	@SuppressWarnings("unchecked")
	private FileEntry getData(WebTreeNode node) {
		return (FileEntry) node.getData();
	}

	@Override
	public void onNodesAddedToVisible(List<WebTreeNode> nodes) {
	}

	@Override
	public void onRemovingNodesFromVisible(List<WebTreeNode> nodes) {
	}

	@Override
	public void onSelectionChanged(WebTreeNode node) {
	}

	@Override
	public WebMenu createMenu(FastWebTree fastWebTree, List<WebTreeNode> selected) {
		BasicWebMenu r = new BasicWebMenu();
		if (fastWebTree.getSelected().size() == 1 && isFile(fastWebTree.getSelected().get(0))) {
			r.addChild(new BasicWebMenuLink("Download", true, "download"));
		}
		if (fastWebTree.getSelected().size() == 2 && isFile(fastWebTree.getSelected().get(0)) && isFile(fastWebTree.getSelected().get(1))) {
			r.addChild(new BasicWebMenuLink("Compare", true, "compare"));
		}
		if (fastWebTree.getSelected().size() == 1 && !(getData(fastWebTree.getSelected().get(0)).getFile() instanceof VortexAgentBackupFile)) {
			r.addChild(new BasicWebMenuLink("Open Terminal", true, "terminal"));
		}
		if (fastWebTree.getSelected().size() == 1) {
			FileEntry entry = getData(fastWebTree.getSelected().get(0));
			if (MH.allBits(entry.getFile().getMask(), VortexAgentFile.DIRECTORY)) {
				if (entry.isManaged) {
					if (entry.getBackup() != null) {
						r.addChild(new BasicWebMenuLink("Stop Managing Directory", true, "unmanage"));
						r.addChild(new BasicWebMenuLink("Edit Managing Directory Settings", true, "edit_managed"));
					}
					r.addChild(new BasicWebMenuLink("Show Deleted Files", true, "deleted"));
				} else
					r.addChild(new BasicWebMenuLink("Make Managed Directory", true, "manage"));
				r.addChild(new BasicWebMenuDivider());
				r.addChild(new BasicWebMenuLink("Upload File", true, "add_file"));
				r.addChild(new BasicWebMenuLink("Create Directory", true, "add_dir"));
				r.addChild(new BasicWebMenuLink("Delete Directory", true, "del_dir"));
			} else if (MH.allBits(entry.getFile().getMask(), VortexAgentFile.FILE)) {
				r.addChild(new BasicWebMenuDivider());
				r.addChild(new BasicWebMenuLink("Delete File", true, "del_file"));
			}
		}

		if (r.getChildren().isEmpty())
			return null;
		return r;
	}

	private boolean isFile(WebTreeNode node) {
		FileEntry data = getData(node);
		return data != null && data.getFile() != null && MH.allBits(data.getFile().getMask(), VortexAgentFile.FILE);
	}
	@Override
	public boolean formatNode(WebTreeNode node, StringBuilder sink) {
		return false;
	}
	@Override
	public void onMachineAdded(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onMachineUpdated(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onMachineStale(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onMachineActive(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onMachineRemoved(VortexClientMachine machine) {
	}
	@Override
	public void onMachineEntityAdded(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEvent.TYPE_BACKUP) {
			VortexClientBackup backup = (VortexClientBackup) node;
			WebTreeNode treeNode = muidToPathToNode.getMulti(backup.getSourceMuid(), backup.getFullSourcePath());
			if (treeNode != null) {
				treeNode.setIcon("portlet_icon_folder_managed");
				getData(treeNode).setManaged(true);
				getData(treeNode).setBackup((VortexClientBackup) node);
			}
		} else if (node.getType() == VortexAgentEvent.TYPE_BACKUP_FILE) {
			VortexClientBackupFile backupFile = (VortexClientBackupFile) node;
			VortexClientBackup backup = backupFile.getBackup(service.getAgentManager());
			if (backup != null) {
				WebTreeNode treeNode = muidToPathToNode.getMulti(backup.getSourceMuid(), backupFile.getData().getPath());
				if (treeNode != null) {
					treeNode.setIcon("portlet_icon_file_managed");
					getData(treeNode).setManaged(true);
					getData(treeNode).setBackupFile((VortexClientBackupFile) node);
				}
			}
		}
	}
	@Override
	public void onMachineEntityUpdated(VortexClientEntity<?> node) {
		//TODO: remove old
		if (node.getType() == VortexAgentEvent.TYPE_BACKUP) {
			VortexClientBackup backup = (VortexClientBackup) node;
			WebTreeNode treeNode = muidToPathToNode.getMulti(backup.getSourceMuid(), backup.getFullSourcePath());
			if (treeNode != null) {
				treeNode.setIcon("portlet_icon_folder_managed");
				getData(treeNode).setManaged(true);
			}
		}
	}
	@Override
	public void onMachineEntityRemoved(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEvent.TYPE_BACKUP) {
			VortexClientBackup backup = (VortexClientBackup) node;
			WebTreeNode treeNode = muidToPathToNode.getMulti(backup.getSourceMuid(), backup.getFullSourcePath());
			if (treeNode != null) {
				treeNode.setIcon("portlet_icon_folder");
				getData(treeNode).setManaged(false);
				getData(treeNode).setBackupFile(null);
			}
		} else if (node.getType() == VortexAgentEvent.TYPE_BACKUP_FILE) {
			VortexClientBackupFile backupFile = (VortexClientBackupFile) node;
			VortexClientBackup backup = backupFile.getBackup(service.getAgentManager());
			if (backup != null) {
				WebTreeNode treeNode = muidToPathToNode.getMulti(backup.getSourceMuid(), backupFile.getData().getPath());
				if (treeNode != null) {
					treeNode.setIcon("portlet_icon_file");
					getData(treeNode).setManaged(false);
					getData(treeNode).setBackupFile(null);
				}
			}
		}
	}

	public static class FileEntry {
		String muid;
		VortexAgentFile file;
		String description;
		boolean isManaged;
		private VortexClientBackupFile backupFile;
		private VortexClientBackup backup;
		public FileEntry(String muid, VortexAgentFile file, String description, boolean isManaged) {
			super();
			this.muid = muid;
			this.file = file;
			this.description = description;
			this.isManaged = isManaged;
		}
		public String getMuid() {
			return muid;
		}
		public void setMuid(String muid) {
			this.muid = muid;
		}
		public VortexAgentFile getFile() {
			return file;
		}
		public void setFile(VortexAgentFile file) {
			this.file = file;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public boolean isManaged() {
			return isManaged;
		}
		public void setManaged(boolean isManaged) {
			this.isManaged = isManaged;
		}
		public VortexClientBackupFile getBackupFile() {
			return backupFile;
		}
		public VortexClientBackup getBackup() {
			return backup;
		}
		public void setBackupFile(VortexClientBackupFile backupFile) {
			this.backupFile = backupFile;
		}
		public void setBackup(VortexClientBackup backup) {
			this.backup = backup;
		}

	}

	@Override
	public int compare(WebTreeNode o1, WebTreeNode o2) {

		if (getData(o1).getFile() instanceof VortexAgentBackupFile && getData(o2).getFile() instanceof VortexAgentBackupFile) {
			VortexAgentBackupFile f1 = (VortexAgentBackupFile) getData(o1).getFile();
			VortexAgentBackupFile f2 = (VortexAgentBackupFile) getData(o2).getFile();
			return -OH.compare(f1.getRevision(), f2.getRevision());
		}
		return OH.compare(o1.getName(), o2.getName());
	}
	@Override
	public boolean onButton(ConfirmDialogPortlet source, String id) {
		if (ConfirmDialogPortlet.ID_NO.equals(id))
			return true;
		Tuple2<String, Object> t = (Tuple2<String, Object>) source.getCorrelationData();
		if ("DELETE".equals(t.getA())) {
			FileEntry muidAndFile = getData((WebTreeNode) t.getB());
			VortexEyePassToAgentRequest eyeReq = nw(VortexEyePassToAgentRequest.class);
			VortexAgentFileDeleteRequest agentRequest = nw(VortexAgentFileDeleteRequest.class);
			agentRequest.setFiles(CH.l(muidAndFile.getFile().getPath()));
			eyeReq.setAgentMachineUid(muidAndFile.getMuid());
			eyeReq.setAgentRequest(agentRequest);
			lastRequest = eyeReq;
			setMessage("Deleting " + muidAndFile.getFile().getPath(), MSG_WAIT);
			this.requestType = TYPE_DELETE_FILE;
			this.deleteTarget = (WebTreeNode) t.getB();
			service.sendRequestToBackend(getPortletId(), eyeReq);
			return true;
		}
		return false;
	}
	@Override
	public void onCheckedChanged(WebTreeNode node) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onCellMousedown(FastWebTree tree, WebTreeNode start, FastWebTreeColumn col) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onNodeChanged(WebTreeNode node) {
		// TODO Auto-generated method stub

	}

}
