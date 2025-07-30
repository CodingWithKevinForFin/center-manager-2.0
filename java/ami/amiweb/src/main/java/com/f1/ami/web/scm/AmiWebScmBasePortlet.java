package com.f1.ami.web.scm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiScmAdapter;
import com.f1.ami.amicommon.AmiScmException;
import com.f1.ami.amicommon.AmiScmRevision;
import com.f1.ami.web.AmiWebConsts;
import com.f1.ami.web.AmiWebDifferPortlet;
import com.f1.ami.web.AmiWebLayoutFile;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;

public class AmiWebScmBasePortlet extends GridPortlet implements ConfirmDialogListener {
	protected static final Logger log = LH.get();
	protected static final String MENU_SCM_REFRESH = "scm_refresh";
	public static final String CONFIRM_DIALOG_SYNC_CHANGELIST = "SYNC_CHANGELIST";
	public static final String CONFIRM_DIALOG_DIFF_CHANGELIST = "DIFF_CHANGELIST";
	protected AmiWebService service;

	public AmiWebScmBasePortlet(PortletConfig config) {
		super(config);
		this.service = AmiWebUtils.getService(this.getManager());
	}
	public boolean refreshScm() {
		return false;
	}
	public AmiWebDifferPortlet getDifferPortlet() {
		return null;
	}
	public AmiWebScmHistoryPortlet getHistoryPortlet() {
		return null;
	}

	public boolean openForEdit(List<String> files) {
		try {
			getAdapter().editFiles(files);
			return true;
		} catch (Exception e) {
			getManager().showAlert(e.getMessage(), e);
			return false;
		}

	}
	public boolean commit(List<String> files, String comment) {
		try {
			getAdapter().commitFiles(files, comment);
			return true;
		} catch (AmiScmException e) {
			getManager().showAlert("Failed to commit files: " + e.getMessage(), e);
			return false;
		}

	}

	public static WebMenu createScmMenu(List<String> files, AmiWebService service) {
		AmiScmAdapter adapter = service.getScmAdapter();
		if (adapter == null)
			return null;
		WebMenu menu = null;
		try {
			Map<String, Byte> statuses = adapter.getFileStatus(files);
			menu = AmiWebScmBasePortlet.createScmMenu(statuses, statuses.size() > 0);
		} catch (AmiScmException e) {
			service.getPortletManager().showAlert("Unexpected error: " + e.getMessage());
		}
		return menu;
	};
	public static WebMenu createScmMenu(Map<String, Byte> statuses, boolean hasScmMenu) {
		WebMenu r = new BasicWebMenu("Source Control", hasScmMenu);
		Set<Byte> statusTypes = new HashSet<Byte>(statuses.values());
		if (hasScmMenu && statusTypes.size() > 0) {
			if (CH.s(AmiScmAdapter.STATUS_CHECKED_IN).containsAll(statusTypes)) {
				r.add(new BasicWebMenuLink("Open For Edit", true, "scm_edit"));
				r.add(new BasicWebMenuLink("Mark For Delete", true, "scm_delete"));
				r.add(new BasicWebMenuLink("Show History", true, "scm_history"));
				if (statuses.size() == 1) {
					r.add(new BasicWebMenuLink("Diff Against Latest", true, "scm_diff_scm"));
					r.add(new BasicWebMenuLink("Diff Against Changelist", true, "scm_diff_scm_changelist"));
				}
				r.add(new BasicWebMenuLink("Get Latest", true, "scm_sync"));
				r.add(new BasicWebMenuLink("Sync Changelist", true, "scm_sync_scm_changelist"));
				r.add(new BasicWebMenuLink("Sync Parent Directory", true, "scm_sync_parent"));
			} else if (CH.s(AmiScmAdapter.STATUS_MODIFIED).containsAll(statusTypes)) {
				r.add(new BasicWebMenuLink("Mark For Add", true, "scm_add"));
				r.add(new BasicWebMenuLink("Show History", true, "scm_history"));
				if (statuses.size() == 1) {
					r.add(new BasicWebMenuLink("Diff Against Latest", true, "scm_diff_scm"));
					r.add(new BasicWebMenuLink("Diff Against Changelist", true, "scm_diff_scm_changelist"));
				}
				r.add(new BasicWebMenuLink("Restore", true, "scm_revert"));
			} else if (CH.s(AmiScmAdapter.STATUS_CHECKED_OUT).containsAll(statusTypes)) {
				r.add(new BasicWebMenuLink("Revert", true, "scm_revert"));
				r.add(new BasicWebMenuLink("Commit", true, "scm_commit"));
				r.add(new BasicWebMenuLink("Show History", true, "scm_history"));
				if (statuses.size() == 1) {
					r.add(new BasicWebMenuLink("Diff Against Latest", true, "scm_diff_scm"));
					r.add(new BasicWebMenuLink("Diff Against Changelist", true, "scm_diff_scm_changelist"));
				}
				r.add(new BasicWebMenuLink("Sync Parent Directory", true, "scm_sync_parent"));
			} else if (CH.s(AmiScmAdapter.STATUS_PRIVATE).containsAll(statusTypes)) {
				r.add(new BasicWebMenuLink("Mark For Add", true, "scm_add"));
				r.add(new BasicWebMenuLink("Sync Parent Directory", true, "scm_sync_parent"));
			} else if (CH.s(AmiScmAdapter.STATUS_MARKED_FOR_ADD).containsAll(statusTypes)) {
				r.add(new BasicWebMenuLink("Revert Add", true, "scm_revert"));
				r.add(new BasicWebMenuLink("Commit", true, "scm_commit"));
				r.add(new BasicWebMenuLink("Sync Parent Directory", true, "scm_sync_parent"));
			} else if (CH.s(AmiScmAdapter.STATUS_MARKED_FOR_DELETE).containsAll(statusTypes)) {
				r.add(new BasicWebMenuLink("Revert Delete", true, "scm_revert"));
				r.add(new BasicWebMenuLink("Commit", true, "scm_commit"));
				r.add(new BasicWebMenuLink("Show History", true, "scm_history"));
				r.add(new BasicWebMenuLink("Sync Parent Directory", true, "scm_sync_parent"));
			} else if (CH.s(AmiScmAdapter.STATUS_MARKED_FOR_ADD, AmiScmAdapter.STATUS_MARKED_FOR_DELETE, AmiScmAdapter.STATUS_CHECKED_OUT).containsAll(statusTypes)) {
				r.add(new BasicWebMenuLink("Revert", true, "scm_revert"));
				r.add(new BasicWebMenuLink("Commit", true, "scm_commit"));
				r.add(new BasicWebMenuLink("Sync Parent Directory", true, "scm_sync_parent"));
			}
		}
		r.add(new BasicWebMenuLink("Refresh", true, MENU_SCM_REFRESH));
		return r;
	}
	public static boolean onScmContextMenu(List<String> files, String action, AmiWebScmBasePortlet scmBase) {
		AmiWebService service = AmiWebUtils.getService(scmBase.getManager());
		PortletManager manager = scmBase.getManager();
		boolean handled = false;
		if (SH.startsWith(action, "scm_")) {
			handled = true;
			try {
				boolean needsScmUpdate = false;
				boolean needsReloadLayout = false;

				if ("scm_add".equals(action)) {
					service.getScmAdapter().addFiles(files, AmiScmAdapter.TYPE_TEXT);
					needsScmUpdate = true;
				} else if ("scm_edit".equals(action)) {
					// Save layout so scm can mark for edit
					service.getLayoutFilesManager().saveLayout();
					// Scm mark for edit
					service.getScmAdapter().editFiles(files);
					needsScmUpdate = true;
				} else if ("scm_revert".equals(action)) {
					service.getScmAdapter().revertFiles(files);
					needsReloadLayout = true;
					needsScmUpdate = true;
				} else if ("scm_delete".equals(action)) {
					service.getScmAdapter().deleteFiles(files);
					needsScmUpdate = true;
				} else if ("scm_history".equals(action)) {
					scmBase.showHistory(files);
				} else if ("scm_diff_scm".equals(action)) {
					scmBase.showDiffScmLatest(files);
				} else if ("scm_diff_scm_changelist".equals(action)) {
					if (files.size() == 1)
						scmBase.showChangelistDialog(files, "Diff to changelist", CONFIRM_DIALOG_DIFF_CHANGELIST);
				} else if ("scm_sync_scm_changelist".equals(action)) {
					scmBase.showChangelistDialog(files, "Sync to changelist", CONFIRM_DIALOG_SYNC_CHANGELIST);
				} else if ("scm_sync".equals(action)) {
					service.getScmAdapter().syncDirectories(files);

					needsReloadLayout = true;
					needsScmUpdate = true;
				} else if ("scm_sync_parent".equals(action)) {
					ArrayList<String> directories = new ArrayList<String>();
					for (String fileName : files) {
						String parent = new File(fileName).getParent();
						if (parent != null)
							directories.add(parent);
					}
					service.getScmAdapter().syncDirectories(directories);
					needsReloadLayout = true;
					needsScmUpdate = true;
				} else if ("scm_commit".equals(action)) {
					manager.showDialog("Commit", new AmiWebScmCommitPortlet(scmBase.generateConfig(), scmBase, files));
				} else if (MENU_SCM_REFRESH.equals(action)) {
					needsScmUpdate = true;
				}

				if (needsReloadLayout) {
					scmBase.reloadLayout();
				}
				if (needsScmUpdate) {
					scmBase.refreshScm();
				}
			} catch (AmiScmException e) {
				service.getPortletManager().showAlert("Unexpected error: " + e.getMessage(), e);
				LH.info(log, "Error : ", e);
			}
		}

		return handled;
	}
	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if (CONFIRM_DIALOG_DIFF_CHANGELIST.equals(source.getCallback())) {
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

				AmiWebLayoutFile f = new AmiWebLayoutFile(this.service.getLayoutFilesManager(), localName, AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE);
				String right = this.service.getLayoutFilesManager().toJson(f.getJson(this.service));

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
				reloadLayout();

			}
		}
		return true;
	}
	protected AmiScmAdapter getAdapter() {
		return this.service.getScmAdapter();
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

	private void showHistory(List<String> files) {
		AmiWebScmHistoryPortlet history = this.getHistoryPortlet();
		boolean showDialog = false;
		if (history == null) {
			history = new AmiWebScmHistoryPortlet(generateConfig());
			showDialog = true;
		}
		history.getHistory(files);
		if (showDialog)
			getManager().showDialog("History", history);
	}
	private void showDiffScmLatest(List<String> files) throws AmiScmException {
		if (files.size() == 1) {
			String localName = files.get(0);

			AmiScmRevision rev = CH.last(CH.sort(service.getScmAdapter().getHistory(localName)));
			if (rev == null) {
				this.getManager().showAlert("File does not exist in source control: " + localName);
				return;
			}
			byte[] scm = service.getScmAdapter().getFile(localName, rev.getChangelistId());
			String left = new String(scm);

			AmiWebLayoutFile f = new AmiWebLayoutFile(this.service.getLayoutFilesManager(), localName, AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE);
			String right = this.service.getLayoutFilesManager().toJson(f.getJson(this.service));

			AmiWebDifferPortlet diff = getDifferPortlet();
			boolean showDialog = false;
			if (diff == null) {
				diff = new AmiWebDifferPortlet(generateConfig());
				showDialog = true;
			}
			diff.setTitles("Latest Scm (" + rev.getChangelistId() + ")", "Current (Local)");
			diff.setText(left, right);
			if (showDialog)
				this.getManager().showDialog("Diff", diff);
		}
	}
	private boolean reloadLayout() {
		AmiWebLayoutFile f = new AmiWebLayoutFile(service.getLayoutFilesManager().getLayout());
		this.service.getLayoutFilesManager().loadLayoutDialog(f.getAbsoluteLocation(), null, f.getSource());
		return false;
	}
	public static boolean isScmManagedFile(AmiScmAdapter scmAdapter, String path) throws AmiScmException {
		String scmRoot = IOH.toUnixFormatForce(scmAdapter.getRootDirectory());
		String unixPath = IOH.toUnixFormatForce(path);
		return SH.startsWith(unixPath, scmRoot);
	}
}
