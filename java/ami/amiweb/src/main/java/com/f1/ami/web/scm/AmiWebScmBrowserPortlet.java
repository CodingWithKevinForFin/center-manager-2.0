package com.f1.ami.web.scm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiScmAdapter;
import com.f1.ami.amicommon.AmiScmException;
import com.f1.ami.amicommon.AmiScmRevision;
import com.f1.ami.web.AmiWebDifferPortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebSpecialPortlet;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.tree.WebTreeContextMenuFactory;
import com.f1.suite.web.tree.WebTreeContextMenuListener;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiWebScmBrowserPortlet extends AmiWebScmBasePortlet implements WebTreeContextMenuListener, WebTreeContextMenuFactory, ConfirmDialogListener, AmiWebSpecialPortlet {
	private static final Logger log = LH.get();
	private AmiWebService service;
	private FastTreePortlet tree;
	private AmiWebScmEditorTabsPortlet editorTabsPortlet;
	private AmiWebScmHistoryPortlet historyPortlet;

	private Map<String, WebTreeNode> nodesById = new HashMap<String, WebTreeNode>();

	public AmiWebScmBrowserPortlet(PortletConfig config, AmiWebService service) {
		super(config);
		this.service = service;
		if (getAdapter() == null) {
			new AMiWebScmConnectPortlet(generateConfig(), service);
		}
		this.tree = new FastTreePortlet(generateConfig());
		this.historyPortlet = new AmiWebScmHistoryPortlet(generateConfig());
		this.editorTabsPortlet = new AmiWebScmEditorTabsPortlet(generateConfig(), this);
		this.historyPortlet.setEditorTabsPortlet(this.editorTabsPortlet);

		DividerPortlet div2 = new DividerPortlet(generateConfig(), true, tree, editorTabsPortlet);
		DividerPortlet div = new DividerPortlet(generateConfig(), false, div2, historyPortlet);
		div2.setOffsetFromTopPx(300);
		div.setOffsetFromBottomPx(200);
		addChild(div, 0, 0);
		this.tree.getTree().addMenuContextListener(this);
		this.tree.getTree().setContextMenuFactory(this);

		String rootPath = this.service.getScmBasePath();
		rootPath = IOH.getFullPath(new File(rootPath));
		WebTreeNode root = this.tree.getTreeManager().getRoot();
		root.setName(rootPath);
		root.setData(new Data(rootPath, AmiScmAdapter.STATUS_DIRECTORY, SH.afterLast(rootPath, '/', rootPath), null));
		root.setIcon("portlet_icon_folder");
		nodesById.put(rootPath, root);
		refreshTree(root);
	}
	private void refreshTree(WebTreeNode root) {
		try {
			boolean isExpanded = root.getIsExpanded();
			boolean isSelected = root.getSelected();
			String origId = getData(root).id;
			final Set<String> local = buildLocalFiles(getData(root).id);
			Data data;
			String rootPath;
			List<String> paths;
			if (root == this.tree.getTreeManager().getRoot()) {
				for (WebTreeNode i : CH.l(root.getChildren()))
					removeNode(i);
				data = (Data) root.getData();
				rootPath = data.id;
				paths = getAdapter().getFileNames(rootPath);
			} else {
				String path = getData(root).id;
				WebTreeNode t = root;
				root = root.getParent();
				removeNode(t);
				data = (Data) root.getData();
				rootPath = data.id;
				paths = filterOutPeers(path, getAdapter().getFileNames(rootPath));
			}
			Map<String, Byte> statuses = getAdapter().getFileStatus(paths);
			Map<String, String> changeLists = getAdapter().getCurrentFileChangelists(paths);
			String rootPathCleaned = SH.stripSuffix(rootPath, "/", false);
			StringBuilder currentPath = new StringBuilder(rootPathCleaned);
			for (String origPath : paths) {
				boolean localCopy = local.remove(origPath);//TODO:pay attention to this
				String path = SH.stripPrefix(origPath, rootPath, true);
				path = SH.trim('/', path);
				String[] parts = SH.split('/', path);
				WebTreeNode n = root;
				currentPath.setLength(rootPathCleaned.length());
				for (int i = 0; i < parts.length; i++) {
					String part = parts[i];
					boolean isLast = i == parts.length - 1;
					WebTreeNode n2 = n.getChildByKey(part);
					if (n2 != null) {
						n = n2;
						if (isLast) {
							Byte status = statuses.get(origPath);
							String changeList = CH.getOrThrow(changeLists, origPath);
							applyStatus(n2, status, changeList);
						}
					} else {
						if (isLast) {
							Byte status = statuses.get(origPath);
							if (status == null) {
								LH.warning(log, "Unknown status for: ", origPath);
								status = AmiScmAdapter.STATUS_PRIVATE;
							}
							String changeList = CH.getOrThrow(changeLists, origPath);
							n = createNode(n, origPath, part, status, changeList);
						} else {
							currentPath.append('/').append(part);
							n = createNode(n, currentPath.toString(), part, AmiScmAdapter.STATUS_DIRECTORY, null);
						}
					}
				}
			}
			for (String origPath : local) {
				boolean isDir = origPath.endsWith("/");
				String path = SH.stripPrefix(origPath, rootPath, true);
				path = SH.trim('/', path);
				String[] parts = SH.split('/', path);
				WebTreeNode n = root;
				currentPath.setLength(rootPathCleaned.length());
				for (int i = 0; i < parts.length; i++) {
					String part = parts[i];
					boolean isLast = i == parts.length - 1;
					WebTreeNode n2 = n.getChildByKey(part);
					if (n2 != null) {
						n = n2;
						if (isLast)
							applyStatus(n2, AmiScmAdapter.STATUS_PRIVATE, null);
					} else {
						if (isLast && !isDir) {
							n = createNode(n, origPath, part, AmiScmAdapter.STATUS_PRIVATE, null);
						} else {
							currentPath.append('/').append(part);
							n = createNode(n, currentPath.toString(), part, AmiScmAdapter.STATUS_DIRECTORY, null);
						}
					}
				}
			}

			WebTreeNode node = nodesById.get(origId);
			if (node != null) {
				node.setSelected(isSelected);
				node.setIsExpanded(isExpanded);
			}
		} catch (AmiScmException e) {
			getManager().showAlert("Error refreshing source control: " + e.getMessage(), e);
		}
	}
	private List<String> filterOutPeers(String path, List<String> fileNames) {
		List<String> r = new ArrayList<String>();
		String dir = path + "/";
		for (String s : fileNames)
			if (s.equals(path) || s.startsWith(dir))
				r.add(s);
		return r;
	}
	private static Data getData(WebTreeNode root) {
		return (Data) root.getData();
	}
	private void removeNode(WebTreeNode toRemove) {
		for (WebTreeNode i : CH.l(toRemove.getChildren())) {
			removeNode(i);
		}
		this.nodesById.remove(getData(toRemove).id);
		toRemove.getParent().removeChild(toRemove);
	}
	private void removeAllChildrenNested(WebTreeNode root, Set<WebTreeNode> toRemove) {
	}
	private void getAllChildrenNested(WebTreeNode node, Set<WebTreeNode> sink) {
		sink.add(node);
		for (WebTreeNode child : node.getChildren())
			getAllChildrenNested(child, sink);
	}
	private WebTreeNode createNode(WebTreeNode parent, String id, String name, byte status, String changeList) {
		WebTreeNode n = this.tree.getTreeManager().createNode(name, parent, false).setKey(name);
		n.setData(new Data(id, status, name, changeList));
		this.nodesById.put(id, n);
		applyStatus(n, status, changeList);
		return n;
	}
	private void applyStatus(WebTreeNode n, byte status, String changeList) {
		Data data = (Data) n.getData();
		data.status = status;
		data.changeList = changeList;
		String css;
		String append;
		String icon;
		switch (status) {
			case AmiScmAdapter.STATUS_CHECKED_IN:
				css = "scm_i";
				append = " (Checkedin)";
				icon = "portlet_icon_file";
				break;
			case AmiScmAdapter.STATUS_CHECKED_OUT:
				css = "scm_o";
				append = " (edit)";
				icon = "portlet_icon_file";
				break;
			case AmiScmAdapter.STATUS_MARKED_FOR_ADD:
				css = "scm_a";
				append = " (add)";
				icon = "portlet_icon_file";
				break;
			case AmiScmAdapter.STATUS_MARKED_FOR_DELETE:
				css = "scm_d";
				append = " (delete)";
				icon = "portlet_icon_file";
				break;
			case AmiScmAdapter.STATUS_PRIVATE:
				css = "scm_p";
				append = "";
				icon = "portlet_icon_file";
				break;
			case AmiScmAdapter.STATUS_DIRECTORY:
				css = "scm_f";
				append = "";
				icon = "portlet_icon_folder";
				break;
			case AmiScmAdapter.STATUS_MODIFIED:
				css = "scm_a";
				append = " (modified)";
				icon = "portlet_icon_file";
				break;

			default:
				throw new NoSuchElementException(SH.toString(status));
		}
		n.setCssClass(css);
		n.setIcon(icon);
		if (data.changeList != null)
			n.setName(data.name + " <i>" + data.changeList + "</i> " + append);
		else
			n.setName(data.name + append);
	}
	private Set<String> buildLocalFiles(String rootPath) {
		HashSet<String> r = new HashSet<String>();
		StringBuilder basePath = new StringBuilder(rootPath);
		SH.trimTrailingInPlace(basePath, '/');
		buildLocalFiles(basePath, new File(rootPath), r);
		return r;
	}

	private void buildLocalFiles(StringBuilder fullName, File file, HashSet<String> files) {
		if (file.isFile()) {
			files.add(fullName.toString());
		} else if (file.isDirectory()) {
			fullName.append('/');
			File[] listFiles = file.listFiles();
			if (listFiles.length == 0) {
				files.add(fullName.toString());
			} else {
				int len = fullName.length();
				for (File f : listFiles) {
					fullName.setLength(len);
					fullName.append(f.getName());
					buildLocalFiles(fullName, f, files);
				}
			}
		}
	}
	@Override
	public WebMenu createMenu(FastWebTree fastWebTree, List<WebTreeNode> selected) {
		BasicWebMenu r = new BasicWebMenu();
		Byte sameStatus = null;
		for (WebTreeNode i : selected) {
			Data data = (Data) i.getData();
			Byte status = data.status;
			if (sameStatus == null)
				sameStatus = status;
			else if (sameStatus != status) {
				sameStatus = null;
				break;
			}
		}
		if (sameStatus != null) {
			switch (sameStatus) {
				case AmiScmAdapter.STATUS_CHECKED_IN:
					r.add(new BasicWebMenuLink("Open for Edit", true, "edit"));
					r.add(new BasicWebMenuLink("Markt for Delete", true, "delete"));
					r.add(new BasicWebMenuLink("Show History", true, "history"));
					if (selected.size() == 1)
						r.add(new BasicWebMenuLink("Diff Against Latest Source Control", true, "diff_scm"));
					r.add(new BasicWebMenuLink("Get Latest From Source Control", true, "sync"));//TODO
					break;
				case AmiScmAdapter.STATUS_CHECKED_OUT:
					r.add(new BasicWebMenuLink("Revert", true, "revert"));
					r.add(new BasicWebMenuLink("Commit", true, "commit"));//TODO
					r.add(new BasicWebMenuLink("Show History", true, "history"));
					if (selected.size() == 1)
						r.add(new BasicWebMenuLink("Diff Against Latest Source Control", true, "diff_scm"));
					break;
				case AmiScmAdapter.STATUS_PRIVATE:
					r.add(new BasicWebMenuLink("Mark for Add to Source Control", true, "add"));
					r.add(new BasicWebMenuLink("Delete local file", true, "delete_local"));
					break;
				case AmiScmAdapter.STATUS_MARKED_FOR_ADD:
					r.add(new BasicWebMenuLink("Revert Add", true, "revert"));
					r.add(new BasicWebMenuLink("Commit", true, "commit"));
					break;
				case AmiScmAdapter.STATUS_MARKED_FOR_DELETE:
					r.add(new BasicWebMenuLink("Revert Delete", true, "revert"));
					r.add(new BasicWebMenuLink("Commit", true, "commit"));
					r.add(new BasicWebMenuLink("Show History", true, "history"));
					break;
				case AmiScmAdapter.STATUS_DIRECTORY://directory
					r.add(new BasicWebMenuLink("Create new File", true, "new"));
			}
		}
		if (selected.size() > 0) {
			r.add(new BasicWebMenuLink("Refresh", true, "refresh"));//TODO
		}
		if (selected.size() == 1) {
			Data data = (Data) selected.get(0).getData();
			r.add(new BasicWebMenuLink("Copy Full Path to Clipboard", true, "copy_path_to_cb").setOnClickJavascript(PortletHelper.createJsCopyToClipboard(data.id)));
		}
		return r;
	}
	@Override
	public boolean formatNode(WebTreeNode node, StringBuilder sink) {
		return false;
	}
	@Override
	public void onContextMenu(FastWebTree tree, String action) {
		try {
			List<WebTreeNode> selected = tree.getSelected();
			List<String> files = new ArrayList<String>(selected.size());
			for (WebTreeNode node : tree.getSelected()) {
				Data data = (Data) node.getData();
				if (data != null)
					files.add(data.id);
			}
			if ("history".equals(action)) {
				this.historyPortlet.getHistory(files);
			} else if ("edit".equals(action)) {
				openForEdit(files);
			} else if ("revert".equals(action)) {
				List<String> canRevert = new ArrayList<String>();
				for (String localName : files) {
					boolean isChanged;
					File file = new File(localName);
					AmiWebScmEditorPortlet editor = this.editorTabsPortlet.getEditor(localName);
					if (editor != null && editor.isInEdit())
						isChanged = true;
					else if (!file.exists())
						isChanged = false;
					else {
						AmiScmRevision rev = CH.last(CH.sort(getAdapter().getHistory(localName)));
						isChanged = rev == null ? true : !AH.eq(IOH.readData(file), getAdapter().getFile(localName, rev.getChangelistId()));
					}
					if (isChanged)
						getManager().showDialog("Confirm",
								new ConfirmDialogPortlet(generateConfig(), "Revert changed file?<BR>" + localName, ConfirmDialogPortlet.TYPE_OK_CANCEL, this).setCallback("REVERT")
										.setCorrelationData(CH.l(localName)));
					else
						canRevert.add(localName);
				}
				if (!canRevert.isEmpty())
					revertFiles(canRevert);
			} else if ("delete".equals(action)) {
				getAdapter().deleteFiles(files);
				refreshFiles(files);
			} else if ("add".equals(action)) {
				getAdapter().addFiles(files, AmiScmAdapter.TYPE_TEXT);
				refreshFiles(files);
			} else if ("delete_local".equals(action)) {
				String message = files.size() == 1 ? ("Permanently Delete: " + files.get(0)) : ("Permanently Delete " + files.size() + " files?");
				getManager().showDialog("Confirm",
						new ConfirmDialogPortlet(generateConfig(), message, ConfirmDialogPortlet.TYPE_OK_CANCEL, this).setCallback("DELETE_LOCAL").setCorrelationData(files));
			} else if ("new".equals(action)) {
				getManager().showDialog("New File", new AmiWebScmNewFilePortlet(generateConfig(), files.get(0), service), 800, 300);
			} else if ("diff_scm".equals(action)) {
				if (files.size() == 1) {
					String localName = files.get(0);
					File file = new File(localName);
					if (!file.exists()) {
						getManager().showAlert("File does not exist locally: " + localName);
						return;
					}
					byte[] local = IOH.readData(file);
					AmiScmRevision rev = CH.last(CH.sort(getAdapter().getHistory(localName)));
					if (rev == null) {
						getManager().showAlert("File does not exist in source control: " + localName);
						return;
					}
					byte[] scm = getAdapter().getFile(localName, rev.getChangelistId());
					AmiWebDifferPortlet differ = new AmiWebDifferPortlet(generateConfig());
					differ.setText(new String(local), new String(scm));
					differ.setTitles(localName + " (local)", localName + " (" + rev.getChangelistId() + ")");
					getManager().showDialog("Diff", differ);
				}
			} else if ("commit".equals(action)) {
				for (String id : files) {
					AmiWebScmEditorPortlet editor = this.editorTabsPortlet.getEditor(id);
					if (editor != null && editor.isInEdit()) {
						getManager().showDialog("Confirm", new ConfirmDialogPortlet(generateConfig(), "Save Changes?", ConfirmDialogPortlet.TYPE_YES_NO, this)
								.setCallback("SAVE_AND_COMMIT").addButton("CANCEL", "Cancel").setCorrelationData(files));
						return;
					}
				}
				getManager().showDialog("Commit", new AmiWebScmCommitPortlet(generateConfig(), this, files));
			} else if ("refresh".equals(action)) {
				for (String file : files) {
					WebTreeNode node = this.nodesById.get(file);
					if (node != null)
						this.refreshTree(node);
				}
			} else if ("copy_path_to_cb".equals(action)) {
			}
		} catch (Exception e) {
			getManager().showAlert(e.getMessage(), e);
			return;
		}
	}
	public boolean openForEdit(List<String> files) {
		try {
			getAdapter().editFiles(files);
			refreshFiles(files);
			return true;
		} catch (Exception e) {
			getManager().showAlert(e.getMessage(), e);
			return false;
		}
	}
	private void refreshFiles(List<String> files) {
		try {
			Map<String, Byte> statuses = getAdapter().getFileStatus(files);
			Map<String, String> changeLists = getAdapter().getCurrentFileChangelists(files);
			for (String id : files) {
				String changeList = changeLists.get(id);
				WebTreeNode node = CH.getOrThrow(this.nodesById, id);
				Byte newStatus = statuses.get(id);
				if (newStatus == null) {
					boolean isFile = new File(id).isFile();
					if (!isFile) {
						node.getParent().removeChild(node);
						this.nodesById.remove(id);
						continue;
					} else
						applyStatus(node, AmiScmAdapter.STATUS_PRIVATE, changeList);
				} else
					applyStatus(node, newStatus, changeList);
			}
		} catch (Exception e) {
			getManager().showAlert("Error refreshing treee: " + e.getMessage(), e);
		}
	}
	@Override
	public void onNodeClicked(FastWebTree tree, WebTreeNode node) {
	}
	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node) {
	}
	@Override
	public void onCellMousedown(FastWebTree tree, WebTreeNode start, FastWebTreeColumn col) {
	}

	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
		try {
			if (columns == this.tree.getTree()) {
				List<WebTreeNode> rows = this.tree.getTree().getSelected();
				if (rows.size() == 1) {
					WebTreeNode row0 = CH.first(rows);
					Data data = (Data) row0.getData();
					if (data != null && data.status != AmiScmAdapter.STATUS_DIRECTORY) {
						String name = data.id;
						this.editorTabsPortlet.showTab(name, IOH.readText(new File(name)), data.status);
					}
				}
			}
		} catch (Exception e) {
			getManager().showAlert(e.getMessage(), e);
			return;
		}
	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if ("DELETE_LOCAL".equals(source.getCallback())) {
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				List<String> files = (List<String>) source.getCorrelationData();
				for (String file : files) {
					new File(file).delete();
				}
				refreshFiles(files);
			}
		} else if ("SAVE_AND_COMMIT".equals(source.getCallback())) {
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				List<String> files = (List<String>) source.getCorrelationData();
				this.editorTabsPortlet.saveFiles(files);
				getManager().showDialog("Commit", new AmiWebScmCommitPortlet(generateConfig(), this, files));
			} else if (ConfirmDialogPortlet.ID_NO.equals(id)) {
				List<String> files = (List<String>) source.getCorrelationData();
				getManager().showDialog("Commit", new AmiWebScmCommitPortlet(generateConfig(), this, files));
			}
		} else if ("REVERT".equals(source.getCallback())) {
			List<String> files = (List<String>) source.getCorrelationData();
			if (ConfirmDialogPortlet.ID_YES.equals(id))
				revertFiles(files);
		}
		return true;
	}
	private void revertFiles(List<String> files) {
		try {
			getAdapter().revertFiles(files);
		} catch (Exception e) {
			getManager().showAlert("Could not revert files: " + e.getMessage(), e);
			return;
		}
		this.editorTabsPortlet.reloadFiles(files);
		refreshFiles(files);
	}

	@Override
	public boolean commit(List<String> files, String comment) {
		try {
			getAdapter().commitFiles(files, comment);
			refreshFiles(files);
			return true;
		} catch (AmiScmException e) {
			getManager().showAlert("Failed to commit files: " + e.getMessage(), e);
			return false;
		}

	}

	public AmiScmAdapter getAdapter() {
		return service.getScmAdapter();
	}

	public static class Data {
		public String id;
		public byte status;
		public String name;
		public String changeList;

		public Data(String id, byte status, String name, String changeList) {
			super();
			OH.assertFalse(id.endsWith("/"));
			OH.assertEq(SH.afterLast(id, '/', id), name);
			this.id = id;
			this.status = status;
			this.name = name;
			this.changeList = changeList;
		}

	}

}
