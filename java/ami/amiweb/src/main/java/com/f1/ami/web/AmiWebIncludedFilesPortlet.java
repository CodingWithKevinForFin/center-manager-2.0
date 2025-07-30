package com.f1.ami.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiScmAdapter;
import com.f1.ami.amicommon.AmiScmException;
import com.f1.ami.amicommon.AmiScmRevision;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.amiscript.AmiWebFileSystem;
import com.f1.ami.web.cloud.AmiWebCloudLayoutTree;
import com.f1.ami.web.scm.AmiWebFileBrowserPortlet;
import com.f1.ami.web.scm.AmiWebFileBrowserPortletListener;
import com.f1.ami.web.scm.AmiWebScmBasePortlet;
import com.f1.ami.web.scm.AmiWebScmHistoryPortlet;
import com.f1.base.Row;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.table.WebContextMenuFactoryAndListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.tree.WebTreeContextMenuFactory;
import com.f1.suite.web.tree.WebTreeContextMenuListener;
import com.f1.suite.web.tree.WebTreeManager;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.WebTreeNodeFormatter;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.IndexedList;
import com.f1.utils.structs.Tuple2;

public class AmiWebIncludedFilesPortlet extends AmiWebScmBasePortlet implements FormPortletListener, AmiWebFileBrowserPortletListener, WebTreeContextMenuFactory,
		WebTreeContextMenuListener, ConfirmDialogListener, WebContextMenuFactoryAndListener, AmiWebSpecialPortlet {

	private static final Logger log = LH.get();
	public static final String INCLUDED_FILES_BUTTON_SUBMIT = "Submit";
	public static final String INCLUDED_FILES_BUTTON_CANCEL = "Cancel";
	private static final String CONFIRM_DIALOG_ALIAS = "ALIAS";
	private static final String CONFIRM_DIALOG_SYNC_CHANGELIST = "SYNC_CHANGELIST";
	private static final String CONFIRM_DIALOG_DIFF_CHANGELIST = "DIFF_CHANGELIST";
	private static final String CONFIRM_DIALOG_WARNING = "CONTINUE_SAVE";
	private final AmiWebService service;
	private final FastTreePortlet files;
	private final AmiWebScmHistoryPortlet history;

	private AmiWebIncludedFiles includedFilesData;
	private AmiWebFileSystem fs;

	public AmiWebIncludedFilesPortlet(PortletConfig config) {
		super(config);
		this.service = AmiWebUtils.getService(this.getManager());
		this.fs = service.getAmiFileSystem();
		this.includedFilesData = new AmiWebIncludedFiles(this.service);
		FormPortlet footer = addChild(new FormPortlet(generateConfig()), 0, 1);
		footer.addButton(new FormPortletButton(INCLUDED_FILES_BUTTON_SUBMIT));
		footer.addButton(new FormPortletButton(INCLUDED_FILES_BUTTON_CANCEL));

		this.files = new FastTreePortlet(generateConfig());
		WebTreeManager treeManager = this.files.getTree().getTreeManager();
		treeManager.setComparator(new ColumnFormatter_Source());
		FastWebTreeColumn column1 = new FastWebTreeColumn(1, new ColumnFormatter_Location(), "File Location", "", false);
		FastWebTreeColumn column2 = new FastWebTreeColumn(2, new ColumnFormatter_Source(), "Source", "", false);
		FastWebTreeColumn column3 = new FastWebTreeColumn(3, new ColumnFormatter_Permissions(), "Permissions", "", false);
		FastWebTreeColumn column4 = new FastWebTreeColumn(4, new ColumnFormatter_ScmChangelist(), "Changelist", "", false);
		FastWebTreeColumn column5 = new FastWebTreeColumn(5, new ColumnFormatter_ScmStatus(), "Status", "", false);

		this.files.getTree().addColumnAt(true, column1, 0);
		this.files.getTree().addColumnAt(true, column2, 1);
		this.files.getTree().addColumnAt(true, column3, 2);
		this.files.getTree().addColumnAt(true, column4, 3);
		this.files.getTree().addColumnAt(true, column5, 4);
		this.files.getTree().getColumn(0).setWidth(200);
		this.files.getTree().getColumn(1).setWidth(300);
		this.files.getTree().getColumn(2).setWidth(75);
		this.files.getTree().getColumn(3).setWidth(80);
		this.files.getTree().getColumn(4).setWidth(240);
		this.files.getTree().getColumn(5).setWidth(75);

		this.history = new AmiWebScmHistoryPortlet(generateConfig());
		DividerPortlet div = new DividerPortlet(generateConfig(), false, files, history);
		div.setOffsetFromBottomPx(200);
		addChild(div, 0, 0);
		setRowSize(1, 40);
		setSuggestedSize(1200, 600);

		footer.addFormPortletListener(this);
		this.files.getTree().setContextMenuFactory(this);
		this.files.getTree().addMenuContextListener(this);
		this.rebuildTree();
	}
	public static AmiWebIncludedFilesPortlet addToDesktop(AmiWebService service) {
		AmiWebDesktopPortlet desktop = service.getDesktop();
		AmiWebIncludedFilesPortlet t = desktop.getSpecialPortlet(AmiWebIncludedFilesPortlet.class);
		if (t == null) {
			t = new AmiWebIncludedFilesPortlet(service.getPortletManager().generateConfig());
			desktop.addSpecialPortlet(t, "IncludedFiles", 1000, 600);
		}
		return t;
	}
	@Override
	public AmiWebScmHistoryPortlet getHistoryPortlet() {
		return history;
	}
	private void rebuildTree() {
		WebTreeManager treeManager = this.files.getTree().getTreeManager();
		includedFilesData.refreshRootScm();
		WebTreeNode root = treeManager.getRoot();
		root.setName("&lt;root&gt;");
		root.setData(this.getIncludedFilesData().getLayout());
		root.setIcon("portlet_icon_file");
		buildTree(this.files.getTreeManager().getRoot());
	}
	public void buildTree(WebTreeNode node) {
		AmiWebLayoutFile fileRow = (AmiWebLayoutFile) node.getData();
		while (node.getChildrenCount() > 0)
			node.removeChild(node.getChildAt(node.getChildrenCount() - 1));
		for (int i = 0; i < fileRow.getChildrenCount(); i++) {
			AmiWebLayoutFile f = fileRow.getChildAt(i);
			String name = f.getAlias();
			WebTreeNode node2 = this.files.getTreeManager().createNode(f.getAlias(), node, true, f).setIcon("portlet_icon_file");
			buildTree(node2);
		}
		AmiWebLayoutFilesManager.handleDiamond(getIncludedFilesData().getLayout());
		this.files.getTree().flagStyleChanged();
	}

	public void reformatSelectedTree() {
		List<WebTreeNode> nodes = includedFilesData.getSelectedNodes();
		for (WebTreeNode node : nodes) {
			this.files.getTree().onStyleChanged(node);
		}
	}

	public class ColumnFormatter_Source implements WebTreeNodeFormatter, Comparator<WebTreeNode> {

		@Override
		public int compare(WebTreeNode o1, WebTreeNode o2) {
			OH.assertEqIdentity(o1.getParent(), o2.getParent());
			AmiWebLayoutFile d1 = (AmiWebLayoutFile) o1.getData();
			AmiWebLayoutFile d2 = (AmiWebLayoutFile) o2.getData();
			AmiWebLayoutFile p = (AmiWebLayoutFile) o1.getParent().getData();
			return OH.compare(p.getChildPosition(d1), p.getChildPosition(d2));
		}

		@Override
		public void formatToText(WebTreeNode node, StringBuilder sink) {
			AmiWebLayoutFile d1 = (AmiWebLayoutFile) node.getData();
			if (d1.getSource() != null)
				sink.append(d1.getSource());
		}

		@Override
		public void formatToHtml(WebTreeNode node, StringBuilder sink, StringBuilder style) {
			formatToText(node, sink);
		}
		@Override
		public Object getValue(WebTreeNode node) {
			AmiWebLayoutFile d1 = (AmiWebLayoutFile) node.getData();
			return d1.getSource();
		}

		@Override
		public Object getValueDisplay(WebTreeNode node) {
			return this.getValue(node);
		}

		@Override
		public String formatToText(Object data) {
			return OH.toString(data);
		}

	}

	public class ColumnFormatter_Location implements WebTreeNodeFormatter {

		@Override
		public int compare(WebTreeNode o1, WebTreeNode o2) {
			OH.assertEqIdentity(o1.getParent(), o2.getParent());
			AmiWebLayoutFile d1 = (AmiWebLayoutFile) o1.getData();
			AmiWebLayoutFile d2 = (AmiWebLayoutFile) o2.getData();
			AmiWebLayoutFile p = (AmiWebLayoutFile) o1.getParent().getData();
			return OH.compare(p.getChildPosition(d1), p.getChildPosition(d2));
		}

		@Override
		public void formatToText(WebTreeNode node, StringBuilder sink) {
			AmiWebLayoutFile d1 = (AmiWebLayoutFile) node.getData();
			sink.append(d1.getLocation());
		}
		@Override
		public void formatToHtml(WebTreeNode node, StringBuilder sink, StringBuilder style) {
			formatToText(node, sink);
		}

		@Override
		public Object getValue(WebTreeNode node) {
			AmiWebLayoutFile d1 = (AmiWebLayoutFile) node.getData();
			AmiWebLayoutFile p = (AmiWebLayoutFile) node.getParent().getData();
			return p.getChildPosition(d1);
		}

		@Override
		public Object getValueDisplay(WebTreeNode node) {
			return this.getValue(node);
		}

		@Override
		public String formatToText(Object data) {
			return OH.toString(data);
		}

	}

	public class ColumnFormatter_Permissions implements WebTreeNodeFormatter {

		@Override
		public int compare(WebTreeNode o1, WebTreeNode o2) {
			OH.assertEqIdentity(o1.getParent(), o2.getParent());

			AmiWebLayoutFile d1 = (AmiWebLayoutFile) o1.getData();
			AmiWebLayoutFile d2 = (AmiWebLayoutFile) o2.getData();
			AmiWebLayoutFile p = (AmiWebLayoutFile) o1.getParent().getData();
			return OH.compare(p.getChildPosition(d1), p.getChildPosition(d2));
		}

		@Override
		public void formatToText(WebTreeNode node, StringBuilder sink) {
			AmiWebLayoutFile d1 = (AmiWebLayoutFile) node.getData();
			if (d1.getUserReadonly())
				sink.append("read");
			else if (d1.getDuplicateStatus() == AmiWebLayoutFile.SECONDARY)
				sink.append("write(locked)");
			else if (d1.getFileReadonly())
				sink.append("write(file locked)");
			else
				sink.append("write");
		}
		@Override
		public void formatToHtml(WebTreeNode node, StringBuilder sink, StringBuilder style) {
			formatToText(node, sink);
		}

		@Override
		public Object getValue(WebTreeNode node) {
			AmiWebLayoutFile d1 = (AmiWebLayoutFile) node.getData();
			if (d1.getUserReadonly())
				return "read";
			else if (d1.getDuplicateStatus() == AmiWebLayoutFile.SECONDARY)
				return "write(locked)";
			else if (d1.getFileReadonly())
				return "write(file locked)";
			else
				return "write";
		}

		@Override
		public Object getValueDisplay(WebTreeNode node) {
			return this.getValue(node);
		}

		@Override
		public String formatToText(Object data) {
			return OH.toString(data);
		}
	}

	public class ColumnFormatter_ScmChangelist implements WebTreeNodeFormatter {

		@Override
		public int compare(WebTreeNode o1, WebTreeNode o2) {
			return OH.compare(getIncludedFilesData().getNodeScmChangelist(o1), getIncludedFilesData().getNodeScmChangelist(o2));
		}

		@Override
		public void formatToText(WebTreeNode node, StringBuilder sink) {
			sink.append(SH.noNull(getIncludedFilesData().getNodeScmChangelist(node)));
		}

		@Override
		public void formatToHtml(WebTreeNode node, StringBuilder sink, StringBuilder style) {
			formatToText(node, sink);
		}

		@Override
		public Object getValue(WebTreeNode node) {
			StringBuilder sink = new StringBuilder();
			formatToText(node, sink);
			return sink.toString();
		}

		@Override
		public Object getValueDisplay(WebTreeNode node) {
			return this.getValue(node);
		}

		@Override
		public String formatToText(Object data) {
			return OH.toString(data);
		}
	}

	public class ColumnFormatter_ScmStatus implements WebTreeNodeFormatter {

		@Override
		public int compare(WebTreeNode o1, WebTreeNode o2) {
			return OH.compare(getIncludedFilesData().getNodeScmStatus(o1), getIncludedFilesData().getNodeScmStatus(o2));
		}

		private String applyStatus(WebTreeNode node) {

			Byte status = getIncludedFilesData().getNodeScmStatus(node);
			String css;
			String icon;
			if (status == null)
				return null;
			switch (status) {
				case AmiScmAdapter.STATUS_CHECKED_IN:
					css = "scm_i";
					icon = "portlet_icon_file";
					break;
				case AmiScmAdapter.STATUS_CHECKED_OUT:
					css = "scm_o";
					icon = "portlet_icon_file";
					break;
				case AmiScmAdapter.STATUS_MARKED_FOR_ADD:
					css = "scm_a";
					icon = "portlet_icon_file";
					break;
				case AmiScmAdapter.STATUS_MARKED_FOR_DELETE:
					css = "scm_d";
					icon = "portlet_icon_file";
					break;
				case AmiScmAdapter.STATUS_PRIVATE:
					css = "scm_p";
					icon = "portlet_icon_file";
					break;
				case AmiScmAdapter.STATUS_DIRECTORY:
					css = "scm_f";
					icon = "portlet_icon_folder";
					break;
				case AmiScmAdapter.STATUS_MODIFIED:
					css = "scm_o";
					icon = "portlet_icon_file";
					break;
				default:
					throw new NoSuchElementException(SH.toString(status));
			}
			node.setCssClass(css);
			node.setIcon(icon);
			return getAdapter().getStatusName(status);
		}
		@Override
		public void formatToHtml(WebTreeNode node, StringBuilder sink, StringBuilder style) {
			sink.append(SH.noNull(applyStatus(node)));
		}
		@Override
		public void formatToText(WebTreeNode node, StringBuilder sink) {
			Byte status = getIncludedFilesData().getNodeScmStatus(node);
			if (status != null)
				sink.append(getAdapter().getStatusName(status));
		}

		@Override
		public Object getValue(WebTreeNode node) {
			Byte status = getIncludedFilesData().getNodeScmStatus(node);
			if (status == null)
				return null;
			return getAdapter().getStatusName(status);
		}

		@Override
		public Object getValueDisplay(WebTreeNode node) {
			return this.getValue(node);
		}

		@Override
		public String formatToText(Object data) {
			return OH.toString(data);
		}

	}

	@Override
	//Green buttons
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (SH.equals(AmiWebIncludedFilesPortlet.INCLUDED_FILES_BUTTON_CANCEL, button.getName())) {
			if (changed(getIncludedFilesData().getLayout(), getIncludedFilesData().getCurrentLayout()))
				ConfirmDialogPortlet.confirmAndCloseWindow(this, "Discard changes?");
			else
				this.close();
		} else if (SH.equals(AmiWebIncludedFilesPortlet.INCLUDED_FILES_BUTTON_SUBMIT, button.getName())) {
			IndexedList<String, AmiWebLayoutFile> old = new BasicIndexedList<String, AmiWebLayoutFile>();
			IndexedList<String, AmiWebLayoutFile> nuw = new BasicIndexedList<String, AmiWebLayoutFile>();
			getIncludedFilesData().getCurrentLayout().getFullAliasMap(old);
			getIncludedFilesData().getLayout().getFullAliasMap(nuw);
			Set<String> removed = CH.comm(old.keySet(), nuw.keySet(), true, false, false);
			//Check if layouts have been removed
			if (!removed.isEmpty()) {
				Set<String> fileNames = new HashSet<String>();
				for (String s : removed) {
					AmiWebLayoutFile t = old.get(s);
					fileNames.add(t.getSource() + ":" + t.getLocation());
				}
				//Show warning
				ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(this.generateConfig(),
						"The following files will be removed and any unsaved work to those files will be lost:<BR>" + SH.join(',', fileNames), ConfirmDialogPortlet.TYPE_OK_CANCEL,
						this).setCallback(CONFIRM_DIALOG_WARNING);
				this.getManager().showDialog("Continue?", cdp);
			} else {
				this.getIncludedFilesData().setCurrentLayout(getIncludedFilesData().getLayout());
				this.close();
			}
		}

	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {

	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}
	@Override
	public boolean commit(List<String> files, String comment) {
		boolean success = super.commit(files, comment);
		this.updateScm();
		return success;
	};
	@Override
	public boolean onFileSelected(AmiWebFileBrowserPortlet target, String file) {
		AmiWebFile f = fs.getFile(file);
		if (target.getType() == AmiWebFileBrowserPortlet.TYPE_CREATE_FILE) {
			if ("Untitled.ami".equalsIgnoreCase(f.getName())) {
				this.getManager().showAlert("Invalid file name");
				return false;
			}
			String name = getFullPath(f);
			try {
				f.writeText("{}");
				LH.info(log, "User ", this.getManager().describeUser(), " created new file '", getFullPath(f), "'");
			} catch (IOException e) {
				this.getManager().showAlert("Unexpected error: " + e.getMessage());
				LH.info(log, "Error creating file: ", getFullPath(f), e);
				return false;
			}
		}
		AmiWebLayoutFile fileRow = (AmiWebLayoutFile) this.getIncludedFilesData().getContextTarget().getData();
		ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(this.generateConfig(), "Enter an alias for: " + f.getName(), ConfirmDialogPortlet.TYPE_OK_CANCEL, this,
				new FormPortletTextField("Alias:")).setCallback(CONFIRM_DIALOG_ALIAS);
		cdp.getInputField().getForm().addField(new FormPortletCheckboxField("Relative Path: ", true).setId("RELATIVE"));
		cdp.getInputField().getForm().addField(new FormPortletCheckboxField("Read Only: ", false).setId("READONLY"));
		Set<String> existing = fileRow.getChildAliases();
		cdp.setInputFieldValue(SH.getNextId(AmiUtils.toValidVarName(SH.beforeLast(f.getName(), ".", f.getName())), existing));

		Map<String, Object> correlationData = new HashMap<String, Object>();
		correlationData.put("fileBrowserPortlet", target);
		correlationData.put("fileName", file);
		correlationData.put("fileSource", AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE);
		cdp.setCorrelationData(correlationData);

		this.getManager().showDialog("Alias", cdp);
		return false;
	}
	private String getFullPath(AmiWebFile f) {
		return f.getFullPath();
	}
	@Override
	//Right Click
	public WebMenu createMenu(FastWebTree fastWebTree, List<WebTreeNode> selected) {
		WebMenu r = new BasicWebMenu();
		getIncludedFilesData().setSelectedNodes(selected);
		if (selected.size() == 1) {
			WebTreeNode node = selected.get(0);
			AmiWebLayoutFile d1 = (AmiWebLayoutFile) node.getData();
			if (node != fastWebTree.getTreeManager().getRoot()) {
				r.add(new BasicWebMenuLink("Unlink", true, "unlink"));
			}
			BasicWebMenu perms = new BasicWebMenu("Permissions", true);
			r.add(perms);
			perms.add(new BasicWebMenuLink("Writeable", true, "setwrite").setCssStyle(!d1.getUserReadonly() ? "className=ami_menu_checked" : ""));
			perms.add(new BasicWebMenuLink("Readonly", true, "setread").setCssStyle(d1.getUserReadonly() ? "className=ami_menu_checked" : ""));
			r.add(new BasicWebMenuLink("Diff against loaded", true, "diff"));
			WebTreeNode parent = node.getParent();
			if (parent != null && parent.getChildrenCount() > 1) {
				if (parent.getChildAt(0) != node)
					r.add(new BasicWebMenuLink("Move Up (higher Priority)", true, "moveup"));
				if (parent.getChildAt(parent.getChildrenCount() - 1) != node)
					r.add(new BasicWebMenuLink("Move Down (lower Priority)", true, "movedn"));
			}
			BasicWebMenu t = new BasicWebMenu("Add Child Link From", true);
			r.add(t);
			t.add(new BasicWebMenuLink("Existing File....", true, "linke"));
			t.add(new BasicWebMenuLink("New File....", true, "linkn"));

			BasicWebMenu cloudLayoutsMenu = new BasicWebMenu("File in cloud", true);

			AmiWebCloudLayoutTree cloudLayouts = this.service.getCloudManager().getCloudLayouts();
			this.service.getDesktop().buildCloudImport(cloudLayoutsMenu, cloudLayouts, "link_cloud_layout_", false, true);
			t.add(cloudLayoutsMenu);

			BasicWebMenu localLayoutsMenu = new BasicWebMenu("File in my layouts", true);
			for (String name : this.service.getLayoutFilesManager().getLocalLayoutNames().keySet())
				localLayoutsMenu.add(new BasicWebMenuLink(name, true, "link_local_layout_" + name).setBackgroundImage(AmiWebConsts.ICON_AMI_FILE));
			t.add(localLayoutsMenu);
		}

		WebMenu menu = AmiWebScmBasePortlet.createScmMenu(this.getIncludedFilesData().getSelectedFileLocations(), service);
		if (menu != null)
			r.add(menu);
		return r;
	}

	@Override
	public boolean formatNode(WebTreeNode node, StringBuilder sink) {
		return false;
	}

	@Override
	public void onContextMenu(FastWebTree tree, String action) {
		AmiWebLayoutFile target = this.getIncludedFilesData().getTargetLayout();
		if ("linke".equals(action)) {
			String dir;
			if (AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE.equals(target.getSource()))
				dir = fs.getFile(target.getLocation()).getParentFile().getAbsolutePath();
			else if (AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE.equals(this.getIncludedFilesData().getLayout().getSource()))
				dir = fs.getFile(this.getIncludedFilesData().getLayout().getLocation()).getParent();
			else
				dir = null;
			AmiWebFileBrowserPortlet fileBrowser = new AmiWebFileBrowserPortlet(this.generateConfig(), this, dir, AmiWebFileBrowserPortlet.TYPE_SELECT_FILE, "*.ami");
			this.getManager().showDialog("Link Absolute File...", fileBrowser);
		} else if ("linkn".equals(action)) {
			String dir;
			if (AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE.equals(target.getSource()))
				dir = fs.getFile(target.getLocation()).getParentFile().getAbsolutePath();
			else if (AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE.equals(this.getIncludedFilesData().getLayout().getSource()))
				dir = fs.getFile(this.getIncludedFilesData().getLayout().getLocation()).getParent();
			else
				dir = null;
			AmiWebFileBrowserPortlet fileBrowser = new AmiWebFileBrowserPortlet(this.generateConfig(), this, dir + "/Untitled.ami", AmiWebFileBrowserPortlet.TYPE_CREATE_FILE,
					"*.ami");
			this.getManager().showDialog("Link to New File...", fileBrowser);
		} else if ("unlink".equals(action)) {
			AmiWebLayoutFile p = this.getIncludedFilesData().getTargetParentLayout();
			p.removeChild(target);
			AmiWebLayoutFilesManager.handleDiamond(getIncludedFilesData().getLayout());
			this.buildTree(this.getIncludedFilesData().getContextTarget().getParent());
		} else if ("moveup".equals(action)) {
			AmiWebLayoutFile p = this.getIncludedFilesData().getTargetParentLayout();
			int position = p.getChildPosition(target);
			p.removeChildAt(position);
			p.addChild(target, position - 1);
			this.buildTree(this.getIncludedFilesData().getContextTarget().getParent());
		} else if ("movedn".equals(action)) {
			AmiWebLayoutFile p = this.getIncludedFilesData().getTargetParentLayout();
			int position = p.getChildPosition(target);
			p.removeChildAt(position);
			p.addChild(target, position + 1);
			this.buildTree(this.getIncludedFilesData().getContextTarget().getParent());
		} else if ("setread".equals(action)) {
			this.service.getLayoutFilesManager().setWriteable(target, false);
			target.setUserReadonly(true);
			this.buildTree(this.getIncludedFilesData().getContextTarget());
		} else if ("setwrite".equals(action)) {
			this.service.getLayoutFilesManager().setWriteable(target, true);
			target.setUserReadonly(false);
			this.buildTree(this.getIncludedFilesData().getContextTarget());
		} else if ("diff".equals(action)) {
			String left = Tuple2.getB(this.service.getLayoutFilesManager().loadLayoutData(target.getLocation(), target.getSource()));
			String right = this.service.getLayoutFilesManager().toJson(target.buildCurrentJson(this.service));

			AmiWebDifferPortlet diff = new AmiWebDifferPortlet(this.generateConfig());
			diff.setTitles("Last Loaded", "Current");
			diff.setText(left, right);
			this.getManager().showDialog("Diff", diff);
		} else if (action.startsWith("link_local_layout_")) {
			String fileName = SH.afterFirst(action, "link_local_layout_");
			linkFile(fileName, AmiWebConsts.LAYOUT_SOURCE_LOCAL);
		} else if (action.startsWith("link_cloud_layout_")) {
			String parentDir = SH.beforeLast(SH.replaceAll(target.getLocation(), '\\', '/'), '/') + '/';
			String fileName = SH.replaceAll(SH.afterFirst(action, "link_cloud_layout_"), '\\', '/');

			//			String name = 
			linkFile(fileName, AmiWebConsts.LAYOUT_SOURCE_CLOUD);
		} else if (AmiWebScmBasePortlet.onScmContextMenu(getIncludedFilesData().getSelectedFileLocations(), action, this)) {

		}
	}

	@Override
	public boolean refreshScm() {
		this.updateScm();
		return super.refreshScm();
	}
	private void updateScm() {
		// Update the scm status of the nodes
		this.getIncludedFilesData().refreshSelectedScm();
		// Reformat the tree
		this.reformatSelectedTree();
	}

	private boolean linkFile(String fileName, String source) {
		AmiWebLayoutFile targetLayoutFile = this.getIncludedFilesData().getTargetLayout();
		ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(this.generateConfig(), "Enter an alias for: " + fileName, ConfirmDialogPortlet.TYPE_OK_CANCEL, this,
				new FormPortletTextField("Alias:")).setCallback(CONFIRM_DIALOG_ALIAS);
		cdp.getInputField().getForm().addField(new FormPortletCheckboxField("Relative Path: ", true).setId("RELATIVE"));
		cdp.getInputField().getForm().addField(new FormPortletCheckboxField("Read Only: ", true).setId("READONLY"));
		Set<String> existing = targetLayoutFile.getChildAliases();
		cdp.setInputFieldValue(SH.getNextId(AmiUtils.toValidVarName(SH.beforeLast(fileName, ".", fileName)), existing));

		Map<String, Object> correlationData = new HashMap<String, Object>();
		if (source.equals(AmiWebConsts.LAYOUT_SOURCE_LOCAL)) {
			correlationData.put("fileBrowserPortlet", null);
			correlationData.put("fileName", fileName);
			correlationData.put("fileSource", AmiWebConsts.LAYOUT_SOURCE_LOCAL);
		} else if (source.equals(AmiWebConsts.LAYOUT_SOURCE_CLOUD)) {
			correlationData.put("fileBrowserPortlet", null);
			correlationData.put("fileName", fileName);
			correlationData.put("fileSource", AmiWebConsts.LAYOUT_SOURCE_CLOUD);
		}
		cdp.setCorrelationData(correlationData);
		this.getManager().showDialog("Alias", cdp);
		return false;
	}
	private boolean showChangelistDialog(List<String> files, String dialogTitle, String callback) throws AmiScmException {
		FormPortletSelectField<String> input = new FormPortletSelectField<String>(String.class, "Changelist:");
		HashSet<String> changes = new HashSet<String>();
		for (String file : files) {
			List<AmiScmRevision> history = this.service.getScmAdapter().getHistory(file);
			for (AmiScmRevision rev : history) {
				String changelistId = rev.getChangelistId();
				if (changes.contains(changelistId)) {

				} else {
					changes.add(changelistId);
					input.addOption(changelistId, changelistId);
				}
			}

		}

		ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(this.generateConfig(), "Select a changelist: ", ConfirmDialogPortlet.TYPE_OK_CANCEL, this, input).setCallback(callback);
		cdp.setCorrelationData(files);
		this.getManager().showDialog(dialogTitle, cdp);
		return false;
	}
	@Override
	public void onNodeClicked(FastWebTree tree, WebTreeNode node) {

	}

	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node) {
		//		this.refreshScm();
		this.getIncludedFilesData().refreshRootScm();
	}

	@Override
	public void onCellMousedown(FastWebTree tree, WebTreeNode start, FastWebTreeColumn col) {

	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		// Linking Alias Confirmation Dialog 
		if (CONFIRM_DIALOG_ALIAS.equals(source.getCallback())) {
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				Map<String, Object> correlationData = (Map<String, Object>) source.getCorrelationData();
				String fileSource = (String) correlationData.get("fileSource");
				String fileName = (String) correlationData.get("fileName");
				AmiWebFileBrowserPortlet fbp = (AmiWebFileBrowserPortlet) correlationData.get("fileBrowserPortlet");

				String alias = SH.trim((String) source.getInputFieldValue());
				FormPortletField<?> relative = source.getInputField().getForm().getField("RELATIVE");
				boolean isRelative = relative != null && Boolean.TRUE.equals(relative.getValue());
				boolean readonly = ((FormPortletCheckboxField) source.getInputField().getForm().getField("READONLY")).getBooleanValue();
				AmiWebLayoutFile targetLayoutFile = this.getIncludedFilesData().getTargetLayout();
				final String parentSource = targetLayoutFile.getSource();
				if (isRelative) {
					if (parentSource == null) {
						this.getManager().showAlert("Please save the parent layout before assigning a relative child");
						return false;
					}
					final boolean isPermissable;
					if (OH.eq(parentSource, fileSource))
						isPermissable = true;
					else if (AmiWebConsts.LAYOUT_SOURCE_SHARED.equals(parentSource) && AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE.equals(fileSource))
						isPermissable = true;
					else
						isPermissable = false;
					if (!isPermissable) {
						this.getManager().showAlert("For relative includes, a parent from " + parentSource + " can not have a child from " + fileSource);
						return false;
					}
					if (OH.eq(parentSource, fileSource) && (fileSource.equals(AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE) || fileSource.contentEquals(AmiWebConsts.LAYOUT_SOURCE_CLOUD))) {
						String absoluteLocation = targetLayoutFile.getFullAbsoluteLocation();
						String childLocation = fileName;
						if (fileSource.equals(AmiWebConsts.LAYOUT_SOURCE_CLOUD)) {
							AmiWebFile f = this.service.getCloudManager().getFile(childLocation);
							if (f != null)
								childLocation = f.getAbsolutePath();
						}

						String rfileName = getRelativePath(absoluteLocation, childLocation);
						if (rfileName == null) {
							this.getManager().showAlert("Could not build relative path from parent to child: " + absoluteLocation + " vs " + fileName);
							return false;
						}
						fileName = rfileName;
					}
				}
				if (isValidAlias(alias, targetLayoutFile)) {
					try {
						LinkedHashSet<String> stack = new LinkedHashSet<String>();
						buildStackFromRoot(this.getIncludedFilesData().getContextTarget(), stack);
						isRelative = (OH.eq(parentSource, fileSource) && (fileSource.equals(AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE))
								|| fileSource.equals(AmiWebConsts.LAYOUT_SOURCE_CLOUD)) ? isRelative : false;
						AmiWebLayoutFile l = service.getLayoutFilesManager().loadLayout(targetLayoutFile, alias, fileName, isRelative, readonly, fileSource, stack, null);
						targetLayoutFile.addChild(l);
						this.buildTree(this.getIncludedFilesData().getContextTarget());
					} catch (Exception e) {
						this.getManager().showAlert("Could not link to " + fileSource + " file: " + e.getMessage(), e);
						LH.info(log, "Error creating" + fileSource + "file: ", fileName, e);
						return false;
					}
					if (fbp != null) {
						String fileName2 = (String) correlationData.get("fileName");
						fbp.saveToDefaultFileBrowserPath(fileName2);
						fbp.close();
					}
					return true;
				}
			}
		} else if (CONFIRM_DIALOG_WARNING.equals(source.getCallback())) {
			// Continue Save Remove Files Dialog
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				AmiWebLayoutFile data = getIncludedFilesData().getLayout();
				getIncludedFilesData().setCurrentLayout(data);
				this.close();
			}
		} else if (CONFIRM_DIALOG_DIFF_CHANGELIST.equals(source.getCallback())) {
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				String changelist = SH.trim((String) source.getInputFieldValue());
				List<String> correlationData = (List<String>) source.getCorrelationData();
				if (correlationData.size() != 1)
					return true;
				String localName = correlationData.get(0);

				byte[] scm = null;
				try {
					scm = service.getScmAdapter().getFile(localName, changelist);
				} catch (AmiScmException e) {
					service.getPortletManager().showAlert("Unexpected error: " + e.getMessage());
				}
				String left = new String(scm);
				String right = this.service.getLayoutFilesManager().toJson(this.getIncludedFilesData().getTargetLayout().buildCurrentJson(this.service));

				AmiWebDifferPortlet diff = new AmiWebDifferPortlet(this.generateConfig());
				diff.setTitles("Latest Scm", "Current");
				diff.setText(left, right);
				this.getManager().showDialog("Diff", diff);

			}
		} else if (CONFIRM_DIALOG_SYNC_CHANGELIST.equals(source.getCallback())) {
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				String changelist = SH.trim((String) source.getInputFieldValue());
				List<String> files = (List<String>) source.getCorrelationData();
				HashMap<String, String> syncChanges = new HashMap<String, String>();
				for (String file : files) {
					syncChanges.put(file, changelist);
				}
				try {
					this.service.getScmAdapter().syncToChangelists(syncChanges);
				} catch (AmiScmException e) {
					service.getPortletManager().showAlert("Unexpected error: " + e.getMessage());
				}
				this.service.getLayoutFilesManager().loadLayoutDialog(this.getIncludedFilesData().getCurrentLayout().getFullAbsoluteLocation(), null,
						this.getIncludedFilesData().getCurrentLayout().getSource());

			}
		}
		return true;
	}
	private AmiWebIncludedFiles getIncludedFilesData() {
		return includedFilesData;
	}
	private void buildStackFromRoot(WebTreeNode node, LinkedHashSet<String> stack) {
		WebTreeNode parent = node.getParent();
		if (parent != null)
			buildStackFromRoot(parent, stack);
		AmiWebLayoutFile f = (AmiWebLayoutFile) node.getData();
		stack.add(f.getSource() + ":" + f.getAbsoluteLocation());
	}
	private boolean isValidAlias(String alias, AmiWebLayoutFile lf) {
		if (SH.isnt(alias)) {
			this.getManager().showAlert("Alias required");
			return false;
		} else if (!AmiUtils.isValidVariableName(alias, false, false)) {
			this.getManager().showAlert("Invalid alias name, must be valid variable syntax");
			return false;
		} else if (lf.getChildByAlias(alias) != null) {
			this.getManager().showAlert("Alias already exists, must be unique");
			return false;
		}
		return true;
	}
	public static String getRelativePath(String from, String to) {
		from = SH.replaceAll(from, '\\', '/');
		to = SH.replaceAll(to, '\\', '/');
		String toName = SH.afterLast(to, '/');
		from = SH.beforeLast(IOH.getCanonical(from), '/', "");
		to = SH.beforeLast(IOH.getCanonical(to), '/', "");
		if (from.isEmpty() && to.isEmpty())
			return toName;
		String frParts[] = SH.split('/', from);
		String toParts[] = SH.split('/', to);
		int sameCnt = 0;
		for (int end = Math.min(frParts.length, toParts.length); sameCnt < end; sameCnt++) {
			if (OH.ne(frParts[sameCnt], toParts[sameCnt]))
				break;
		}
		if (sameCnt == 0)
			return null;
		StringBuilder sb = new StringBuilder();
		SH.repeat("../", frParts.length - sameCnt, sb);
		SH.join('/', AH.subarray(toParts, sameCnt, toParts.length - sameCnt), sb);
		if (sb.length() == 0)
			sb.append("./");
		else if (!SH.endsWith(sb, '/'))
			sb.append('/');
		return sb.append(toName).toString();
	}
	private boolean changed(AmiWebLayoutFile a, AmiWebLayoutFile b) {
		if (OH.ne(a.getAlias(), b.getAlias()))
			return true;
		if (a.getChildrenCount() != b.getChildrenCount())
			return true;
		for (int i = 0; i < a.getChildrenCount(); i++)
			if (changed(a.getChildAt(i), b.getChildAt(i)))
				return true;
		return false;
	}

	private List<String> getFilesInScm(AmiScmAdapter adapter, List<String> locations) throws AmiScmException {
		List<String> filesInScm = null;
		if (locations == null)
			return filesInScm;
		String rootDirectory = adapter.getRootDirectory();
		if (rootDirectory != null) {
			filesInScm = new ArrayList<String>();
			String rootPath = getFullPath(fs.getFile(rootDirectory));
			char separatorChar = IOH.getSeparatorChar(rootPath);
			rootPath += separatorChar;

			for (int i = 0; i < locations.size(); i++) {
				String filePath = getFullPath(fs.getFile(locations.get(i)));
				String fileComp = filePath + separatorChar;
				if (SH.startsWith(fileComp, rootPath)) {
					filesInScm.add(filePath);
				}
			}
		}
		return filesInScm;
	}

	class AmiWebIncludedFiles {

		final private AmiWebService service;
		private AmiWebLayoutFile layout;
		private WebTreeNode contextTarget;
		private List<WebTreeNode> selectedNodes;
		private List<String> scmPaths;
		private Map<String, Byte> scmStatuses;
		private Map<String, String> scmChangelists;

		public AmiWebIncludedFiles(AmiWebService service) {
			this.service = service;
			AmiWebLayoutFile data = new AmiWebLayoutFile(service.getLayoutFilesManager().getLayout());
			data.rebuildJsonFromCurrentLayout(service);
			this.setLayout(data);
			this.scmChangelists = new HashMap<String, String>();
			this.scmStatuses = new HashMap<String, Byte>();

		}

		public AmiWebLayoutFile getLayout() {
			return layout;
		}
		public AmiWebLayoutFile getCurrentLayout() {
			return service.getLayoutFilesManager().getLayout();
		}

		private void setLayout(AmiWebLayoutFile layout) {
			this.layout = layout;
		}

		public void setCurrentLayout(AmiWebLayoutFile layout) {
			this.service.getLayoutFilesManager().setLayout(layout);
		}
		public AmiWebLayoutFile getTargetLayout() {
			return (AmiWebLayoutFile) contextTarget.getData();
		}
		public AmiWebLayoutFile getTargetParentLayout() {
			return (AmiWebLayoutFile) contextTarget.getParent().getData();
		}
		public WebTreeNode getContextTarget() {
			return contextTarget;
		}

		private void setContextTarget(WebTreeNode contextTarget) {
			this.contextTarget = contextTarget;
		}

		public List<WebTreeNode> getSelectedNodes() {
			return selectedNodes;
		}
		public int getSelectedNodesCount() {
			return this.selectedNodes.size();
		}

		public void setSelectedNodes(List<WebTreeNode> selectedNodes) {
			this.selectedNodes = selectedNodes;
			if (selectedNodes.size() > 0)
				this.setContextTarget(selectedNodes.get(0));
			else
				this.setContextTarget(null);
		}

		public boolean refreshRootScm() {
			AmiWebLayoutFile rootFile = this.layout;
			Iterable<AmiWebLayoutFile> layoutsChildren = rootFile.getChildrenRecursive(true);
			return refreshScmLayouts(layoutsChildren);
		}
		public boolean refreshSelectedScm() {
			return refreshScm(this.selectedNodes);
		}
		private boolean refreshScm(Iterable<WebTreeNode> nodes) {
			List<AmiWebLayoutFile> layouts = new ArrayList<AmiWebLayoutFile>();
			for (WebTreeNode node : nodes) {
				AmiWebLayoutFile layout = (AmiWebLayoutFile) node.getData();
				layouts.add(layout);
			}
			return this.refreshScmLayouts(layouts);
		}
		private boolean refreshScmLayouts(Iterable<AmiWebLayoutFile> layoutsChildren) {
			AmiScmAdapter scmAdapter = this.service.getScmAdapter();

			if (scmAdapter == null)
				return false;
			List<String> locations = new ArrayList<String>();

			try {
				for (AmiWebLayoutFile layout : layoutsChildren) {
					//Check if file is managed by scm
					if (AmiWebScmBasePortlet.isScmManagedFile(scmAdapter, layout.getFullAbsoluteLocation())) {
						String absoluteLocation = layout.getFullAbsoluteLocation();
						locations.add(absoluteLocation);
					}
				}
				//get files in source control root directory
				this.scmPaths = getFilesInScm(scmAdapter, locations);
				this.scmStatuses.putAll(scmAdapter.getFileStatus(this.scmPaths));
				this.scmChangelists.putAll(scmAdapter.getCurrentFileChangelists(this.scmPaths));
				return true;
			} catch (AmiScmException e) {
				service.getPortletManager().showAlert("Unexpected error: " + e.getMessage());
				LH.info(log, "Error checking if file is in source control: ", e);
				return false;
			}
		}
		public Set<Byte> getSelectedScmStatuses() {
			HashSet<Byte> statusTypes = new HashSet<Byte>();
			for (WebTreeNode node : getSelectedNodes()) {
				Byte status = getNodeScmStatus(node);
				if (status != null)
					statusTypes.add(status);
			}
			return statusTypes;
		}
		public List<String> getSelectedFileLocations() {
			List<String> files = new ArrayList<String>();
			if (CH.isntEmpty(this.scmPaths)) {
				for (WebTreeNode node : getSelectedNodes()) {
					AmiWebLayoutFile lo = (AmiWebLayoutFile) node.getData();
					String location = getFullPath(fs.getFile(lo.getFullAbsoluteLocation()));
					if (this.scmPaths.contains(location))
						files.add(location);
				}
			}
			return files;
		}

		public Byte getNodeScmStatus(WebTreeNode node) {
			AmiWebLayoutFile layout = (AmiWebLayoutFile) node.getData();
			String location = layout.getFullAbsoluteLocation();
			if (location == null)
				return null;
			AmiWebFile f = fs.getFile(location);
			return this.scmStatuses.get(getFullPath(f));
		}

		public String getNodeScmChangelist(WebTreeNode node) {
			AmiWebLayoutFile layout = (AmiWebLayoutFile) node.getData();
			String location = layout.getFullAbsoluteLocation();
			if (location == null)
				return null;
			AmiWebFile f = fs.getFile(location);
			return this.scmChangelists.get(getFullPath(f));
		}

	}

	public static void main(String a[]) {
		System.out.println(getRelativePath("from.ami", "to.ami"));
		System.out.println(getRelativePath("/test/me/here/from.ami", "/test/me/there/to.ami"));
		System.out.println(getRelativePath("/test/me/here/what/from.ami", "/test/me/there/to.ami"));
		System.out.println(getRelativePath("/test/me/here/from.ami", "/test/me/there/what/to.ami"));
		System.out.println(getRelativePath("/test/me/here/what/from.ami", "/test/me/there/what/to.ami"));
		System.out.println(getRelativePath("/test/me/here/what/from.ami", "/test/me/here/what/to.ami"));
		System.out.println(getRelativePath("/test/me/here/what/when/from.ami", "/test/me/here/what/to.ami"));
		System.out.println(getRelativePath("/test/me/here/what/from.ami", "/test/me/here/what/when/to.ami"));
		System.out.println(getRelativePath("/test2/me/here/what/from.ami", "/test/me/here/what/when/to.ami"));
	}
	@Override
	public WebMenu createMenu(WebTable table) {
		if (table.getMenuFactory() instanceof AmiWebFileBrowserPortlet) {
			try {
				return AmiWebScmBasePortlet.createScmMenu(getFileBrowserPathsFromTable(table), this.service);
			} catch (AmiScmException e) {
				this.getManager().showAlert("Could not open scm menu", e);
			}
		}
		return null;
	}
	@Override
	public void onContextMenu(WebTable table, String action) {
		if (MENU_SCM_REFRESH.equals(action)) {
			this.refreshScm();
		}
		try {
			AmiWebScmBasePortlet.onScmContextMenu(getFileBrowserPathsFromTable(table), action, this);
		} catch (AmiScmException e) {
			this.getManager().showAlert("Could not run menu", e);
		}
	}
	private List<String> getFileBrowserPathsFromTable(WebTable table) throws AmiScmException {
		List<String> paths = new ArrayList<String>();
		for (Row row : table.getSelectedRows()) {
			String file = row.get("AbsolutePath", String.class);
			paths.add(file);
		}
		return paths = getFilesInScm(this.getAdapter(), paths);
	}
	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
	}
}
