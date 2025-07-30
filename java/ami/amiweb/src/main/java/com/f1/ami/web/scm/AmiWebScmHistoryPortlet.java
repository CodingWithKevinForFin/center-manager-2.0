package com.f1.ami.web.scm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiScmAdapter;
import com.f1.ami.amicommon.AmiScmException;
import com.f1.ami.amicommon.AmiScmRevision;
import com.f1.ami.web.AmiWebFormatterManager;
import com.f1.ami.web.AmiWebObjectDefParser;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.AmiWebViewObjectsPortlet;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmDefParser;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmLinkDefParser;
import com.f1.ami.web.dm.portlets.AmiWebPortletDefParser;
import com.f1.base.Row;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.BasicTable;

public class AmiWebScmHistoryPortlet extends AmiWebScmBasePortlet implements WebContextMenuFactory, WebContextMenuListener {
	protected static final Logger log = LH.get();
	protected FastTablePortlet history;
	protected AmiWebService service;
	private AmiWebScmEditorTabsPortlet editorTabsPortlet;
	private Map<String, Row> changeList2Row;
	private AmiWebObjectDefParser objectDefParser;
	private String configSearchText; // searchText for diff portlet

	public AmiWebScmHistoryPortlet(PortletConfig config) {
		super(config);
		service = AmiWebUtils.getService(getManager());
		BasicTable t = new BasicTable(Long.class, "Date", String.class, "Revision", String.class, "ChangeList", String.class, "FileName", String.class, "User", String.class,
				"Comment", String.class, "LocalName", String.class, "bg", String.class, "fg");
		this.history = new FastTablePortlet(generateConfig(), t, "History");
		AmiWebFormatterManager fm = service.getFormatterManager();
		this.history.getTable().addColumn(true, "Date", "Date", fm.getDateTimeWebCellFormatter());
		this.history.getTable().addColumn(true, "Revision", "Revision", fm.getBasicFormatter());
		this.history.getTable().addColumn(true, "Change List", "ChangeList", fm.getBasicFormatter());
		this.history.getTable().addColumn(true, "File Name", "FileName", fm.getBasicFormatter());
		this.history.getTable().addColumn(true, "User", "User", fm.getBasicFormatter());
		this.history.getTable().addColumn(true, "Comment", "Comment", fm.getBasicFormatter());
		this.history.getTable().setRowBgColorFormula("bg");
		this.history.getTable().setRowTxColorFormula("fg");
		addChild(this.history, 0, 0);

		this.history.getTable().setMenuFactory(this);
		this.history.getTable().addMenuListener(this);
	}

	public boolean getHistory(List<String> files) {
		this.history.clearRows();
		this.changeList2Row = new HashMap<String, Row>();
		try {
			Map<String, String> changeLists;
			changeLists = getAdapter().getCurrentFileChangelists(files);
			for (String file : files) {
				String currentCl = changeLists.get(file);
				for (AmiScmRevision i : getAdapter().getHistory(file)) {
					boolean isCurrent = OH.eq(currentCl, i.getChangelistId());
					Row r = this.history.addRow((Object) i.getTime(), i.getRevision(), i.getChangelistId(), i.getName(), i.getUser(), SH.trim(i.getComment()), file,
							isCurrent ? "#0000FF" : "", isCurrent ? "#FFFFFF" : "");
					this.changeList2Row.put(i.getChangelistId(), r);
				}
			}
		} catch (AmiScmException e) {
			getManager().showAlert(e.getMessage(), e);
			return false;
		}
		return true;
	}
	public Row getRowByChangeList(String changeList) {
		return this.changeList2Row.get(changeList);
	}
	public FastTablePortlet getTablePortlet() {
		return this.history;
	}
	public void setEditorTabsPortlet(AmiWebScmEditorTabsPortlet editorTabsPortlet) {
		this.editorTabsPortlet = editorTabsPortlet;
	}
	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
		try {
			openInScmEditor("Open");
		} catch (Exception e) {
			getManager().showAlert(e.getMessage(), e);
			return;
		}

	}

	@Override
	public WebMenu createMenu(WebTable table) {
		int selected = table.getSelectedRows().size();
		BasicWebMenu r = new BasicWebMenu();
		if (selected == 1) {
			r.add(new BasicWebMenuLink("Open", true, "open"));
			r.add(new BasicWebMenuLink("Diff against Local", true, "diff_local"));
		} else if (selected == 2) {
			r.add(new BasicWebMenuLink("Diff Two Revisions", true, "diff"));
		}
		return r;
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		try {
			if ("diff".equals(action)) {
				if (getObjectDefParser() == null) {
					List<Row> rows = table.getSelectedRows();
					OH.assertEq(rows.size(), 2);
					Row row0 = rows.get(0);
					String fileName0 = row0.get("FileName", String.class);
					Long date0 = row0.get("Date", Long.class);
					String changelist0 = row0.get("ChangeList", String.class);
					byte[] file0 = getAdapter().getFile(fileName0, changelist0);

					Row row1 = rows.get(1);
					String fileName1 = row1.get("FileName", String.class);
					Long date1 = row1.get("Date", Long.class);
					String changelist1 = row1.get("ChangeList", String.class);
					byte[] file1 = getAdapter().getFile(fileName1, changelist1);

					String title0 = fileName0 + " (" + changelist0 + ")";
					String title1 = fileName1 + " (" + changelist1 + ")";
					if (date0 > date1)
						AmiWebUtils.diffConfigurations(service, new String(file0), new String(file1), title0, title1, getConfigSearchText());
					else
						AmiWebUtils.diffConfigurations(service, new String(file1), new String(file0), title1, title0, getConfigSearchText());
				} else
					diffRevisionsForObject();
			} else if ("diff_local".equals(action)) {
				if (getObjectDefParser() == null) {
					List<Row> rows = table.getSelectedRows();
					OH.assertEq(rows.size(), 1);
					Row row0 = rows.get(0);
					String fileName0 = row0.get("FileName", String.class);
					String localName0 = row0.get("LocalName", String.class);
					String changelist0 = row0.get("ChangeList", String.class);
					byte[] file0 = getAdapter().getFile(fileName0, changelist0);

					String text;
					try {
						text = IOH.readText(new File(localName0));
					} catch (IOException e) {
						getManager().showAlert(e.getMessage(), e);
						return;
					}
					AmiWebUtils.diffConfigurations(service, text, new String(file0), localName0 + " (local)", fileName0 + " (" + changelist0 + ")", getConfigSearchText());
					setSelectedHistoryRowByChangeList(changelist0);
				} else
					diffObjectAgainstRevision();
			} else if ("open".equals(action)) {
				if (getObjectDefParser() == null)
					openInScmEditor("View File");
				else
					openRevisionForObject();
			}
		} catch (AmiScmException e) {
			getManager().showAlert(e.getMessage(), e);
			return;
		}

	}
	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {

	}

	@Override
	public void onCellMousedown(WebTable table, Row row, WebColumn col) {

	}

	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {

	}

	private void openInScmEditor(String dialogTitle) throws AmiScmException {
		List<Row> rows = this.history.getTable().getSelectedRows();
		if (rows.size() == 1) {
			Row row0 = CH.first(rows);
			String fileName0 = row0.get("FileName", String.class);
			String localName0 = row0.get("LocalName", String.class);
			String changelist0 = row0.get("ChangeList", String.class);
			byte[] file0 = getAdapter().getFile(fileName0, changelist0);
			String name = fileName0 + " (" + changelist0 + ")";

			//Get text to display in editor
			String fileString;
			fileString = new String(file0);

			//If there is a editorTabs portlet show tab
			if (this.editorTabsPortlet != null) {
				this.editorTabsPortlet.showTab(name, fileString, AmiScmAdapter.STATUS_HISTORY);
			} else {
				AmiWebScmEditorPortlet editor = new AmiWebScmEditorPortlet(generateConfig(), this, name, null, fileString, AmiScmAdapter.STATUS_HISTORY);
				getManager().showDialog(dialogTitle, editor);
			}
		}
	}

	@Override
	public void onNoSelectedChanged(FastWebTable fastWebTable) {
	}
	public void setSelectedHistoryRowByChangeList(String changeList) {
		Row r = getRowByChangeList(changeList);
		history.getTable().setSelectedRows(new int[] { r.getLocation() });
		history.getTable().ensureRowVisible(r.getLocation());
	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if (source.getCallback().equals(AmiWebViewObjectsPortlet.ACTION_DIFF_DIFFERENT_CHANGELIST) && id.equals(ConfirmDialog.ID_YES)) {
			List<String> files = (List<String>) source.getCorrelationData();
			AmiWebScmBasePortlet.onScmContextMenu(files, "scm_diff_scm_changelist", this);
		} else if (source.getCallback().equals(AmiWebScmBasePortlet.CONFIRM_DIALOG_DIFF_CHANGELIST) && id.equals(ConfirmDialog.ID_YES)) {
			OH.assertEq(((List<String>) source.getCorrelationData()).size(), 1);
			String changeList = (String) source.getInputFieldValue();
			setSelectedHistoryRowByChangeList(changeList); // important!
			diffObjectAgainstRevision();
		}
		source.closeDialog();
		return false;
	}
	public AmiWebObjectDefParser getObjectDefParser() {
		return objectDefParser;
	}

	public void setObjectDefParser(AmiWebObjectDefParser objectDefParser) {
		this.objectDefParser = objectDefParser;
	}

	public String getConfigSearchText() {
		return configSearchText;
	}

	public void setConfigSearchText(String searchText) {
		this.configSearchText = searchText;
	}

	private void diffRevisionsForObject() {
		try {
			List<Row> rows = history.getTable().getSelectedRows();
			OH.assertEq(rows.size(), 2);
			Row row0 = rows.get(0);
			String fileName0 = row0.get("FileName", String.class);
			Long date0 = row0.get("Date", Long.class);
			String changelist0 = row0.get("ChangeList", String.class);
			byte[] file0 = getAdapter().getFile(fileName0, changelist0);

			Row row1 = rows.get(1);
			String fileName1 = row1.get("FileName", String.class);
			Long date1 = row1.get("Date", Long.class);
			String changelist1 = row1.get("ChangeList", String.class);
			byte[] file1 = getAdapter().getFile(fileName1, changelist1);

			String text0, text1, title0, title1;
			if (getObjectDefParser() instanceof AmiWebDmDefParser) {
				AmiWebDmDefParser parser = (AmiWebDmDefParser) getObjectDefParser();
				title0 = parser.getAdn() + ":" + changelist0;
				title1 = parser.getAdn() + ":" + changelist1;
				text0 = parser.parseConfigFromLayoutConfig(new String(file0));
				text1 = parser.parseConfigFromLayoutConfig(new String(file1));
				if (text0 == null || text1 == null) {
					getManager().showAlert("<b>" + parser.getAdn() + "</b> is missing either on one or both revisions.");
					return;
				} else if (date0 > date1)
					AmiWebUtils.diffConfigurations(service, text0, text1, title0, title1, getConfigSearchText());
				else
					AmiWebUtils.diffConfigurations(service, text1, text0, title1, title0, getConfigSearchText());
			} else if (getObjectDefParser() instanceof AmiWebDmLinkDefParser) {
				AmiWebDmLinkDefParser parser = (AmiWebDmLinkDefParser) getObjectDefParser();
				title0 = parser.getRelId() + ":" + changelist0;
				title1 = parser.getRelId() + ":" + changelist1;
				text0 = parser.parseConfigFromLayoutConfig(new String(file0));
				text1 = parser.parseConfigFromLayoutConfig(new String(file1));
				if (text0 == null || text1 == null) {
					getManager().showAlert("<b>" + parser.getRelId() + "</b> is missing either on one or both revisions");
					return;
				} else if (date0 > date1)
					AmiWebUtils.diffConfigurations(service, text0, text1, title0, title1, getConfigSearchText());
				else
					AmiWebUtils.diffConfigurations(service, text1, text0, title1, title0, getConfigSearchText());
			} else if (getObjectDefParser() instanceof AmiWebPortletDefParser) {
				AmiWebPortletDefParser parser = (AmiWebPortletDefParser) getObjectDefParser();
				title0 = parser.getAdn() + ":" + changelist0;
				title1 = parser.getAdn() + ":" + changelist1;
				text0 = parser.parseConfigFromLayoutConfig(new String(file0));
				text1 = parser.parseConfigFromLayoutConfig(new String(file1));
				if (text0 == null || text1 == null) {
					getManager().showAlert("<b>" + parser.getAdn() + "</b> is missing either on one or both revisions.");
					return;
				} else if (date0 > date1)
					AmiWebUtils.diffConfigurations(service, text0, text1, title0, title1, getConfigSearchText());
				else
					AmiWebUtils.diffConfigurations(service, text1, text0, title1, title0, getConfigSearchText());
			}
		} catch (Exception e) {
			LH.warning(AmiWebScmHistoryPortlet.log, "Exception diffing two revisions: " + e);
		}
	}
	private void openRevisionForObject() {
		try {
			List<Row> rows = history.getTable().getSelectedRows();
			OH.assertEq(rows.size(), 1);
			Row row0 = rows.get(0);
			String fileName0 = row0.get("FileName", String.class);
			String changelist0 = row0.get("ChangeList", String.class);
			byte[] file0 = getAdapter().getFile(fileName0, changelist0);

			String text0, title0;
			if (getObjectDefParser() instanceof AmiWebDmDefParser) {
				AmiWebDmDefParser parser = (AmiWebDmDefParser) getObjectDefParser();
				title0 = parser.getAdn() + ":" + changelist0;
				text0 = parser.parseConfigFromLayoutConfig(new String(file0));
				if (text0 == null) {
					getManager().showAlert("<b>" + parser.getAdn() + "</b> is not present in this revision.");
					return;
				} else
					AmiWebUtils.showConfiguration(service, text0, title0, getConfigSearchText());
			} else if (getObjectDefParser() instanceof AmiWebDmLinkDefParser) {
				AmiWebDmLinkDefParser parser = (AmiWebDmLinkDefParser) getObjectDefParser();
				title0 = parser.getRelId() + ":" + changelist0;
				text0 = parser.parseConfigFromLayoutConfig(new String(file0));
				if (text0 == null) {
					getManager().showAlert("<b>" + parser.getRelId() + "</b> is not present in this revision.");
					return;
				} else
					AmiWebUtils.showConfiguration(service, text0, title0, getConfigSearchText());
			} else if (getObjectDefParser() instanceof AmiWebPortletDefParser) {
				AmiWebPortletDefParser parser = (AmiWebPortletDefParser) getObjectDefParser();
				title0 = parser.getAdn() + ":" + changelist0;
				text0 = parser.parseConfigFromLayoutConfig(new String(file0));
				if (text0 == null) {
					getManager().showAlert("<b>" + parser.getAdn() + "</b> is not present in this revision.");
					return;
				} else
					AmiWebUtils.showConfiguration(service, text0, title0, getConfigSearchText());
			}
		} catch (Exception e) {
			LH.warning(AmiWebScmHistoryPortlet.log, "Exception opening revision: " + e);
		}
	}
	private void diffObjectAgainstRevision() {
		try {
			List<Row> rows = history.getTable().getSelectedRows();
			OH.assertEq(rows.size(), 1);
			Row row0 = rows.get(0);
			String fileName0 = row0.get("FileName", String.class);
			String changelist0 = row0.get("ChangeList", String.class);
			byte[] file0 = getAdapter().getFile(fileName0, changelist0);

			String text0, title0;
			if (getObjectDefParser() instanceof AmiWebDmDefParser) {
				AmiWebDmDefParser parser = (AmiWebDmDefParser) getObjectDefParser();
				AmiWebDm dm = parser.getDatamodel();
				title0 = parser.getAdn() + ":" + changelist0;
				text0 = parser.parseConfigFromLayoutConfig(new String(file0));
				if (text0 == null) {
					ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(this.generateConfig(),
							"Could not locate Datamodel<b>" + dm.getAmiLayoutFullAliasDotId() + "</b>. Diff against a different changelist?", ConfirmDialogPortlet.TYPE_YES_NO);
					cdp.setCallback(AmiWebViewObjectsPortlet.ACTION_DIFF_DIFFERENT_CHANGELIST);
					cdp.setCorrelationData(new ArrayList<String>(Arrays.asList(fileName0)));
					cdp.addDialogListener(this);
					getManager().showDialog("Diff Changelists", cdp);
				} else
					AmiWebUtils.diffConfigurations(service, service.getLayoutFilesManager().toJson(dm.getConfiguration()), text0, dm.getAmiLayoutFullAliasDotId(), title0,
							getConfigSearchText());
			} else if (getObjectDefParser() instanceof AmiWebDmLinkDefParser) {
				AmiWebDmLinkDefParser parser = (AmiWebDmLinkDefParser) getObjectDefParser();
				AmiWebDmLink link = parser.getRelationship();
				title0 = parser.getRelId() + ":" + changelist0;
				text0 = parser.parseConfigFromLayoutConfig(new String(file0));
				if (text0 == null) {
					ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(this.generateConfig(),
							"Could not locate <b>" + parser.getRelId() + "</b>. Diff against a different changelist?", ConfirmDialogPortlet.TYPE_YES_NO);
					cdp.setCallback(AmiWebViewObjectsPortlet.ACTION_DIFF_DIFFERENT_CHANGELIST);
					cdp.setCorrelationData(new ArrayList<String>(Arrays.asList(fileName0)));
					cdp.addDialogListener(this);
					getManager().showDialog("Diff Changelists", cdp);
				} else
					AmiWebUtils.diffConfigurations(service, service.getLayoutFilesManager().toJson(link.getConfiguration()), text0, link.getRelationshipId(), title0,
							getConfigSearchText());
			} else if (getObjectDefParser() instanceof AmiWebPortletDefParser) {
				AmiWebPortletDefParser parser = (AmiWebPortletDefParser) getObjectDefParser();
				title0 = parser.getAdn() + ":" + changelist0;
				text0 = parser.parseConfigFromLayoutConfig(new String(file0));
				if (text0 == null) {
					ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(this.generateConfig(), "Could not locate <b>" + parser.getAdn() + "Diff against a different changelist?",
							ConfirmDialogPortlet.TYPE_YES_NO);
					cdp.setCallback(AmiWebViewObjectsPortlet.ACTION_DIFF_DIFFERENT_CHANGELIST);
					cdp.setCorrelationData(new ArrayList<String>(Arrays.asList(fileName0)));
					cdp.addDialogListener(this);
					getManager().showDialog("Diff Changelists", cdp);
				} else
					AmiWebUtils.diffConfigurations(service, service.getLayoutFilesManager().toJson(parser.getThisConfig()), text0, parser.getAdn(), title0, getConfigSearchText());
			}
		} catch (Exception e) {

		}
	}

	@Override
	public void onScroll(int viewTop, int viewPortHeight, long contentWidth, long contentHeight) {
	}
}
