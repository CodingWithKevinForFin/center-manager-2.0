package com.f1.ami.web.scm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.web.AmiWebConsts;
import com.f1.ami.web.AmiWebFile;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.amiscript.AmiWebFileSystem;
import com.f1.base.Row;
import com.f1.http.HttpUtils;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.BasicPortletDownload;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.HtmlPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuFactoryAndListener;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.WebTableListener;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.MapWebCellFormatter;
import com.f1.utils.AH;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;
import com.f1.utils.formatter.BasicTextFormatter;
import com.f1.utils.structs.table.BasicTable;

public class AmiWebFileBrowserPortlet extends GridPortlet
		implements FormPortletListener, WebContextMenuListener, WebTableListener, HtmlPortletListener, ConfirmDialogListener, WebContextMenuFactory {

	private static final char UNIX_SEPERATOR_CHAR = '/';
	private static final char WINDOWS_SEPERATOR_CHAR = '\\';
	private static final String UNIX_SEPERATOR_CHARS = "/";
	private static final String UNIX_ROOT_PATH = "/";
	private static final char THIS_FILE_CHAR = '.';
	private static final String THIS_FILE_CHARS = ".";
	private static final String PARENT_FILE_CHARS = "..";
	private static final String COLUMN_ABSOLUTE_PATH = "AbsolutePath";
	public static final byte TYPE_SELECT_FILE = 1;
	public static final byte TYPE_SELECT_DIR = 2;
	public static final byte TYPE_SELECT_BOTH = 3;
	public static final byte TYPE_CREATE_FILE = 4;
	public static final byte TYPE_SAVE_FILE = 5;
	private static final Logger log = LH.get();
	final private FormPortlet form;
	final private HtmlPortlet url;
	final private BasicTable table;
	final private FastTablePortlet files;
	final private FormPortletButton okayButton;
	final private FormPortletButton cancelButton;
	private String currentPath;
	final private AmiWebService service;
	final private byte dialogType;
	final private FormPortletCheckboxField showHiddenField;
	final private FormPortletCheckboxField showUnpermittedField;
	final private FormPortletTextField filterField;
	final private AmiWebFileBrowserPortletListener listener;
	final private FormPortletTextField nameField;
	private boolean useUrlInput = false;

	private WebContextMenuFactoryAndListener menuFactoryListener;
	final private String basePath;
	private AmiWebFileSystem fs;
	private String sourceType;

	public AmiWebFileBrowserPortlet(PortletConfig config, AmiWebFileBrowserPortletListener listener, String path, byte type, String filter) {
		this(config, listener, path, type, filter, null, null);
	}
	public AmiWebFileBrowserPortlet(PortletConfig config, AmiWebFileBrowserPortletListener listener, String path, byte type, String filter, String basePath, String sourceType) {
		super(config);
		this.sourceType = sourceType;
		this.listener = listener;
		this.service = AmiWebUtils.getService(this.getManager());
		this.fs = this.service.getAmiFileSystem();
		if (SH.isnt(path) && basePath == null)
			path = service.getVarsManager().getSetting(AmiWebConsts.USER_SETTING_DEFAULT_FILE_BROWSER_PATH);
		this.basePath = basePath == null ? null : IOH.toUnixFormatForce(basePath);
		this.dialogType = type;
		this.url = new HtmlPortlet(generateConfig());
		MapWebCellFormatter<String> severityFormatter = new MapWebCellFormatter<String>(new BasicTextFormatter());
		severityFormatter.addEntry("D", "Directory", "_cna=portlet_icon_folder", ""); // Changed first column entry to say "Info" instead of "Debug"
		severityFormatter.addEntry("F", "File", "_cna=portlet_icon_file", "");
		severityFormatter.addEntry("U", "UP", "_cna=portlet_show_up", "");
		this.table = new BasicTable(String.class, "Type", String.class, "File", String.class, "Permissions", Long.class, "Size", Long.class, "ModifiedOn", String.class,
				COLUMN_ABSOLUTE_PATH);
		this.files = new FastTablePortlet(generateConfig(), this.table, "Files");
		this.files.getTable().setMenuFactory(this);
		this.files.getTable().addColumn(true, "", "Type", severityFormatter).setWidth(18);
		this.files.getTable().addColumn(true, "File", "File", this.service.getFormatterManager().getBasicFormatter()).setWidth(312);
		this.files.getTable().addColumn(true, "Mode", "Permissions", this.service.getFormatterManager().getBasicFormatter()).setWidth(35);
		this.files.getTable().addColumn(true, "Modified On", "ModifiedOn", this.service.getFormatterManager().getDateTimeSecsWebCellFormatter()).setWidth(135);
		this.files.getTable().addColumn(false, "Absolute Path", COLUMN_ABSOLUTE_PATH, this.service.getFormatterManager().getBasicFormatter()).setWidth(312);
		this.files.addOption(FastTablePortlet.OPTION_CELL_RIGHT_DIVIDER, 0);
		if (MH.anyBits(type, TYPE_SELECT_FILE))
			this.files.getTable().addColumn(true, "Size", "Size", this.service.getFormatterManager().getMemoryFormatter()).setWidth(83);
		this.files.getTable().sortRows("File", true, true, false);
		this.form = new FormPortlet(generateConfig());
		this.addChild(url, 0, 0);
		this.addChild(files, 0, 1);
		this.addChild(form, 0, 2);
		this.setRowSize(0, 50);
		this.showHiddenField = this.form.addField(new FormPortletCheckboxField("Show Hidden Files"));
		this.showHiddenField.setLeftPosPx(130).setTopPosPx(4);
		this.showUnpermittedField = this.form.addField(new FormPortletCheckboxField("Show Unpermitted Files "));
		this.showUnpermittedField.setLeftPosPx(320).setTopPosPx(4);
		this.filterField = this.form.addField(new FormPortletTextField("Filter:"));
		this.filterField.setLeftPosPx(400).setTopPosPx(4).setHeightPx(20).setRightPosPx(10);
		this.filterField.setValue(filter);

		switch (type) {
			case TYPE_SAVE_FILE:
				this.setRowSize(2, 120);
				this.nameField = this.form.addField(new FormPortletTextField("Save as:"));
				this.nameField.setLeftPosPx(60).setTopPosPx(40).setHeightPx(20).setRightPosPx(10);
				this.okayButton = this.form.addButton(new FormPortletButton("Save"));
				break;
			case TYPE_SELECT_FILE:
			case TYPE_SELECT_BOTH:
				this.setRowSize(2, 120);
				this.nameField = this.form.addField(new FormPortletTextField("Open:"));
				this.nameField.setLeftPosPx(60).setTopPosPx(40).setHeightPx(20).setRightPosPx(10);
				this.okayButton = this.form.addButton(new FormPortletButton("Open"));
				break;
			case TYPE_CREATE_FILE:
				this.setRowSize(2, 120);
				this.nameField = this.form.addField(new FormPortletTextField("Create:"));
				this.nameField.setLeftPosPx(60).setTopPosPx(40).setHeightPx(20).setRightPosPx(10);
				this.okayButton = this.form.addButton(new FormPortletButton("Create"));
				break;
			default:
				this.setRowSize(2, 120);
				this.nameField = this.form.addField(new FormPortletTextField("Open Directory:"));
				this.nameField.setLeftPosPx(110).setTopPosPx(40).setHeightPx(20).setRightPosPx(10);
				this.okayButton = this.form.addButton(new FormPortletButton("Select"));
				break;
		}

		this.cancelButton = this.form.addButton(new FormPortletButton("Cancel"));
		this.form.addFormPortletListener(this);
		this.files.getTable().addMenuListener(this);
		this.files.getTable().addWebTableListener(this);
		//		this.url.setCssStyle("_fs=14px|style.padding=16px 0px 0px 8px|style.overflowY:hidden");
		//		this.url.setCssStyle("_fs=14px|style.display=flex|style.align-items=center|style.padding=8px 8px 8px 8px|style.overflowY:hidden");
		this.url.setCssStyle("_cna=ami_fb_url");
		this.url.addListener(this);
		this.setSuggestedSize(600, 450);
		String t = SH.trim(path);
		AmiWebFile f;
		if (basePath == null) {
			if (SH.isnt(t) || OH.eq(t, THIS_FILE_CHARS)) {
				t = fs.getFile(THIS_FILE_CHARS).getFullPath();
			} else {
				AmiWebFile file = fs.getFile(t);
				if (!file.exists()) {
					AmiWebFile parent = file.getParentFile();
					if (parent != null && parent.isDirectory()) {
						t = getFullPath(parent);
						if (type == TYPE_SAVE_FILE || type == TYPE_CREATE_FILE)
							this.nameField.setValue(file.getName());
					} else
						t = getFullPath(fs.getFile(THIS_FILE_CHARS));
				}
			}
			t = IOH.toUnixFormatForce(t);
			t = t.replaceAll("//+", UNIX_SEPERATOR_CHARS); // remove extra '/'
			t = SH.trim(UNIX_SEPERATOR_CHAR, t); // remove last '/'
			if (!isWindows()) // add '/' before beginning of path for linux
				t = UNIX_ROOT_PATH + t;
			f = fs.getFile(t);
			if (f.isDirectory()) {
				this.updateFilesTable(f.getAbsolutePath(), false);
			} else if (f.isFile()) {
				AmiWebFile parent = f.getParentFile();
				if (parent == null)
					parent = fs.getFile(THIS_FILE_CHARS);
				String fullPath = getFullPath(parent);
				fullPath = IOH.toUnixFormatForce(fullPath);
				updateFilesTable(fullPath, false);
				String name = f.getName();
				if (type == TYPE_SAVE_FILE || type == TYPE_CREATE_FILE)
					this.nameField.setValue(name);
				selectRow("F", name);
			}
		} else {
			if (SH.isnt(t))
				f = fs.getFile(basePath);
			else
				f = fs.getFile(basePath, t);
			if (f.isDirectory()) {
				this.updateFilesTable(f.getAbsolutePath(), false);
			} else if (f.isFile()) {
				String fullPath = getFullPath(f.getParentFile());
				fullPath = IOH.toUnixFormatForce(fullPath);
				updateFilesTable(fullPath, false);
				String name = f.getName();
				if (type == TYPE_SAVE_FILE || type == TYPE_CREATE_FILE)
					this.nameField.setValue(name);
				selectRow("F", name);
			} else {
				updateFilesTable(basePath, false);
				String name = f.getName();
				if (type == TYPE_SAVE_FILE || type == TYPE_CREATE_FILE)
					this.nameField.setValue(name);
			}
		}
		this.files.getTable().sortRows("ModifiedOn", false, true, false);

	}
	private boolean isWindows() {
		return this.service.getAmiFileSystem().isWindows();
	}
	static private String getFullPath(AmiWebFile parent) {
		return parent.getFullPath();
	}
	private void selectRow(String type, String name) {
		for (Row row : this.files.getTable().getRows()) {
			if (OH.eq(type, row.get("Type", String.class)) && OH.eq(name, row.get("File", String.class))) {
				this.files.getTable().setSelectedRows(new int[] { row.getLocation() });
				break;
			}
		}

	}
	private boolean updateFilesTable(String path, boolean showAlertOnError) {
		path = IOH.toUnixFormatForce(path);
		if (basePath != null && !path.startsWith(basePath)) {
			return false;
		}
		if (("".equals(path) && isWindows()) || UNIX_ROOT_PATH.equals(path) && !isWindows()) {
			this.files.getTable().setSearch("");
			this.files.clearRows();
			AmiWebFile[] listFiles = fs.listRoots();
			if (AH.isntEmpty(listFiles))
				for (AmiWebFile s : listFiles)
					this.files.addRow("D", SH.stripSuffix(s.getPath(), "\\", false), toPermissions(s), s.isDirectory() ? null : s.length(), s.lastModified(), s.getAbsolutePath());
			this.currentPath = "";
			this.updateUrlUserInteface();
			return true;
		}
		AmiWebFile t;
		try {
			t = isWindows() ? fs.getFile(path + UNIX_SEPERATOR_CHARS) : fs.getFile(path);
		} catch (Exception e) {
			this.getManager().showAlert("Could not access " + path);
			return false;
		}
		if (t.isDirectory()) {
			if ((!t.canRead() || !t.canExecute()) && showAlertOnError) {
				getManager().showAlert("Permission denied");
				return false;
			}
			this.files.getTable().setSearch("");
			this.files.clearRows();
			if (t.getParent() != null) {
				if (basePath == null || OH.ne(basePath, path)) {
					AmiWebFile parentFile = t.getParentFile();
					String absolutePath = parentFile.getAbsolutePath();
					this.table.getRows().addRow("U", PARENT_FILE_CHARS, null, null, null, absolutePath);
				}
			}
			boolean showHidden = showHiddenField.getBooleanValue();
			boolean showUnpermitted = showUnpermittedField.getBooleanValue();
			AmiWebFile[] listFiles = t.listFiles();
			String matcherText = SH.trim(filterField.getValue());
			TextMatcher matcher = SH.is(matcherText) && OH.ne("*", matcherText) ? SH.mFilePattern(matcherText) : null;

			if (AH.isntEmpty(listFiles))
				for (AmiWebFile s : listFiles) {
					if (s.isHidden() && !showHidden)
						continue;
					if (!showUnpermitted) {
						if (!s.canRead())
							continue;
						if (s.isDirectory() && !s.canExecute())
							continue;
					}
					final String type;
					if (s.isFile()) {
						if (this.dialogType == TYPE_SELECT_DIR)
							continue;
						type = "F";
						if (matcher != null && !matcher.matches(s.getName()))
							continue;
					} else if (s.isDirectory())
						type = "D";
					else
						type = "?";
					this.files.addRow(type, s.getName(), toPermissions(s), s.isDirectory() ? null : s.length(), s.lastModified(), s.getAbsolutePath());
				}
			if (basePath != null)
				this.currentPath = SH.stripPrefix(path, basePath, true);
			else
				this.currentPath = path;
			this.updateUrlUserInteface();
		}
		return true;
	}
	private void updateUrlUserInteface() {
		if (this.useUrlInput) {
			StringBuilder sb = new StringBuilder();
			HtmlPortlet.Callback cb = new HtmlPortlet.Callback("path_input").addAttribute("v", this.getCurrentPath());
			//			String s = "parentNode.callback(event,\"path_input\", \"v\", target.value)";
			sb.append("<input class='ami_fb_path_input' onkeypress='if(event.key==\"Enter\")").append(this.url.generateCallback(cb)).append(";' value='");
			HttpUtils.escapeHtml(this.getCurrentPath(), sb);
			sb.append("'>");
			this.url.setHtml(sb.toString());
		} else {
			StringBuilder sb = new StringBuilder();
			String s = this.url.generateCallback("url_0");
			sb.append("<span class='ami_fb_path_node' onclick='").append(s).append("';'>&nbsp;&nbsp;</span>/");
			if (!UNIX_ROOT_PATH.equals(this.getFullPath())) {
				// \ escape windows path character
				// / unix path character
				String[] parts = SH.split(UNIX_SEPERATOR_CHAR, SH.stripPrefix(this.getCurrentPath(), UNIX_SEPERATOR_CHARS, false));
				int position = 0;
				for (String part : parts) {
					if (position > 0)
						sb.append(UNIX_SEPERATOR_CHAR);
					s = this.url.generateCallback("url_" + (++position));
					//					sb.append("<span class='ami_fb_path_node' onclick='parentNode.callback(event,\"url_").append(++position).append("\");'>");
					sb.append("<span class='ami_fb_path_node' onclick='").append(s).append("');'>");
					HttpUtils.escapeHtml(part, sb);
					sb.append("</span>");
				}
			}
			this.url.setHtml(sb.toString());
		}
	}

	private Object toPermissions(AmiWebFile s) {
		if (s.canRead()) {
			if (s.canWrite()) {
				if (s.canExecute())
					return "rwx";
				else
					return "rw-";
			} else {
				if (s.canExecute())
					return "r-x";
				else
					return "r--";
			}
		} else {
			if (s.canWrite()) {
				if (s.canExecute())
					return "-wx";
				else
					return "-w-";
			} else {
				if (s.canExecute())
					return "--x";
				else
					return "---";
			}
		}
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.cancelButton)
			close();
		else if (button == this.okayButton) {
			List<Row> sr = this.files.getTable().getSelectedRows();
			switch (this.dialogType) {
				case TYPE_CREATE_FILE: {
					String name = SH.trim(this.nameField.getValue());
					if (SH.isnt(name)) {
						getManager().showAlert("Required 'Create' field is blank");
						return;
					} else if (SH.indexOfFirst(name, 0, UNIX_SEPERATOR_CHAR, WINDOWS_SEPERATOR_CHAR) != -1) {
						getManager().showAlert("Invalid file name");
						return;
					}
					String finalName = toAbsoluteFile(name);
					AmiWebFile finalFile = fs.getFile(finalName);
					if (finalFile.exists()) {
						if (finalFile.isDirectory()) {
							getManager().showAlert("Name already exists as a directory");
							return;
						} else {
							getManager().showAlert("File already exists");
							return;
						}
					} else
						onValueSelected(finalName);
					break;
				}
				case TYPE_SAVE_FILE: {
					String name = SH.trim(this.nameField.getValue());
					if (SH.isnt(name)) {
						getManager().showAlert("Required 'Save as' field is blank");
						return;
					} else if (SH.indexOfFirst(name, 0, UNIX_SEPERATOR_CHAR, WINDOWS_SEPERATOR_CHAR) != -1 || !IOH.isValidFilename(name)) {
						getManager().showAlert("File name contains invalid character(s)");
						return;
					}
					String finalName = toAbsoluteFile(name);
					AmiWebFile finalFile = fs.getFile(finalName);
					if (finalFile.exists()) {
						if (finalFile.isDirectory()) {
							getManager().showAlert("Name already exists as a directory");
							return;
						} else if (!finalFile.canWrite()) {
							getManager().showAlert("Permission Denied");
							return;
						} else {
							getManager().showDialog("Overwrite?", new ConfirmDialogPortlet(generateConfig(), "File already exists, Overwrite?", ConfirmDialog.TYPE_OK_CANCEL, this)
									.setCallback("OVERWRITE").setCorrelationData(finalName));
							return;
						}
					} else
						onValueSelected(finalName);
					break;
				}
				case TYPE_SELECT_FILE: {
					if (sr.size() > 1)
						getManager().showAlert("Please select only one file");
					else if (sr.size() == 1 && "D".equals(getType(sr.get(0)))) {
						String name = getName(sr.get(0));
						updateFilesTable(join(this.getFullPath(), name), true);
					} else if (sr.size() == 1 && "U".equals(getType(sr.get(0)))) {
						updateFilesTable(getCurrentPathParent(), true);
					} else {
						String name = SH.trim(this.nameField.getValue());
						if (SH.isnt(name)) {
							getManager().showAlert("Required 'Open' field is blank");
						} else {
							String finalName = toAbsoluteFile(name);
							AmiWebFile finalFile = fs.getFile(finalName);
							if (!finalFile.exists()) {
								getManager().showAlert("Directory not found");
							} else if (!finalFile.canRead()) {
								getManager().showAlert("Permission denied");
							} else if (finalFile.isDirectory()) {
								String fullPath = getFullPath(finalFile);
								fullPath = IOH.toUnixFormatForce(fullPath);
								this.updateFilesTable(fullPath, true);
								this.nameField.setValue("");
							} else
								onValueSelected(finalName);
						}
					}
					break;
				}
				case TYPE_SELECT_DIR: {
					String name = SH.trim(this.nameField.getValue());
					if (SH.isnt(name)) {
						getManager().showAlert("Required 'Open Directory' field is blank");
					} else {
						String finalName = toAbsoluteFile(name);
						AmiWebFile finalFile = fs.getFile(finalName);
						if (!finalFile.exists()) {
							getManager().showAlert("File not found");
						} else if (!finalFile.canRead()) {
							getManager().showAlert("Permission denied");
						} else if (finalFile.isDirectory()) {
							String fullPath = getFullPath(finalFile);
							fullPath = IOH.toUnixFormatForce(fullPath);
							final String path2;
							if (sr.size() == 1 && "D".equals(getType(sr.get(0)))) {
								path2 = join(this.getFullPath(), getName(sr.get(0)));
							} else if (sr.size() == 1 && "U".equals(getType(sr.get(0)))) {
								path2 = this.getCurrentPathParent();
							} else
								path2 = this.getFullPath();
							if (OH.eq(fullPath, path2))
								onValueSelected(fullPath);
							else {
								updateFilesTable(fullPath, true);
								this.nameField.setValue(fullPath);
							}
						} else
							getManager().showAlert("Must select a directory, not a file");
					}
					break;
				}
				case TYPE_SELECT_BOTH: {
					if (sr.size() == 0) {
						onValueSelected(this.getFullPath());
					} else if (sr.size() > 1) {
						getManager().showAlert("Please select only one directory or file");
					} else {
						String name = sr.get(0).get("File", String.class);
						onValueSelected(join(this.getFullPath(), name));
					}
					break;
				}
			}
		}
	}
	private String toAbsoluteFile(String name) {
		if (isWindows() && name.length() > 1 && name.charAt(1) == ':')
			return name;
		else if (name.startsWith(UNIX_ROOT_PATH))
			return name;
		else if (!isWindows() && name.startsWith("~"))
			return EH.getUserHome() + UNIX_SEPERATOR_CHAR + name.substring(1);
		else
			return join(this.getFullPath(), name);
	}
	private void onValueSelected(String file) {
		if (this.listener.onFileSelected(this, file)) {
			saveToDefaultFileBrowserPath(file);
			close();
		}
	}
	public void saveToDefaultFileBrowserPath(String file) {
		if (basePath != null)
			return;
		AmiWebFile t = fs.getFile(file);
		String dir = null;
		if (t.isDirectory())
			dir = t.getAbsolutePath();
		AmiWebFile parentFile = t.getParentFile();
		if (parentFile != null && parentFile.isDirectory())
			dir = parentFile.getAbsolutePath();
		if (dir != null)
			service.getVarsManager().putSetting(AmiWebConsts.USER_SETTING_DEFAULT_FILE_BROWSER_PATH, dir);
	}
	private String join(String left, String right) {
		if (isWindows() && "".equals(left))
			return right;
		//		String sep = isWindows() ? "\\" : "/";
		String sep = UNIX_SEPERATOR_CHARS;
		if (sep.equals(left))
			return left + right;
		if ("".equals(right))
			return left;
		if (left.endsWith(sep) || right.startsWith(sep))
			return left + right;
		return left + sep + right;
	}
	public void setMenuFactoryListener(WebContextMenuFactoryAndListener menuFactoryListener) {
		this.menuFactoryListener = menuFactoryListener;
	}
	@Override
	public void onContextMenu(WebTable table, String action) {
		if ("new_folder".equals(action)) {
			getManager().showDialog("New Folder",
					new ConfirmDialogPortlet(generateConfig(), "", ConfirmDialog.TYPE_OK_CANCEL, this, new FormPortletTextField("New Folder Name: ")).setCallback("NEW_FOLDER"));
		} else if (action.equals("get_path")) {
			List<Row> selected = this.files.getTable().getSelectedRows();
			if (selected.size() != 1)
				return;
			Row row = selected.get(0);
			String name = getName(row);
			AmiWebFile file = fs.getFile(this.getFullPath(), name);
			getManager().showAlert(file.getFullPath());
		} else if (action.equals("get_url")) {
			List<Row> selected = this.files.getTable().getSelectedRows();
			if (selected.size() != 1)
				return;
			Row row = selected.get(0);
			String name = getName(row);
			String fileName = SH.is(currentPath) ? (SH.stripPrefix(currentPath, "/", false) + '/' + name) : name;
			getManager().showAlert(service.getUrl() + '?' + AmiWebConsts.URL_PARAM_LAYOUT + '=' + sourceType + '_' + SH.encodeUrl(fileName));

		} else if ("rename".equals(action)) {
			List<Row> selected = this.files.getTable().getSelectedRows();
			if (selected.size() != 1)
				return;
			Row row = selected.get(0);
			String name = getName(row);
			if (PARENT_FILE_CHARS.equals(name)) {
				getManager().showAlert("Can not rename parent directory");
				return;
			}
			AmiWebFile file = fs.getFile(this.getFullPath(), name);
			if (!file.exists()) {
				getManager().showAlert("Selected file not found");
				return;
			}
			getManager().showDialog("Rename",
					new ConfirmDialogPortlet(generateConfig(), "Rename '" + name + "'", ConfirmDialog.TYPE_OK_CANCEL, this, new FormPortletTextField("New name: ").setValue(name))
							.setCallback("RENAME").setCorrelationData(name));
		} else if ("delete".equals(action)) {
			List<Row> selected = this.files.getTable().getSelectedRows();
			List<String> names = new ArrayList<String>(selected.size());
			for (Row row : selected) {
				String name = getName(row);
				String type = getType(row);
				if ("D".equals(type)) {
					AmiWebFile dir = fs.getFile(this.getFullPath(), name);
					AmiWebFile[] listFiles = dir.listFiles();
					if (AH.isntEmpty(listFiles)) {
						getManager().showAlert("Directory '" + name + "' is not empty");
						return;
					}
				}
				if (PARENT_FILE_CHARS.equals(name)) {
					getManager().showAlert("Can not delete parent directory");
					return;
				}
				names.add(name);
			}
			getManager().showDialog("Confirm Delete",
					new ConfirmDialogPortlet(generateConfig(), "Delete the following " + selected.size() + " selected item(s):<BR><B>" + SH.join("<BR><LEFT>", names),
							ConfirmDialog.TYPE_OK_CANCEL, this).setCallback("DELETE").setCorrelationData(names).updateButton(ConfirmDialog.ID_YES, "Delete"));

		} else if ("download".equals(action)) {
			List<Row> selected = this.files.getTable().getSelectedRows();
			Row row = selected.get(0);
			String name = getName(row);
			char separatorChar = IOH.getSeparatorChar(this.getCurrentPath());
			String filePath = this.getCurrentPath() + separatorChar + name;
			String layout = this.service.getCloudManager().loadLayout(filePath);
			getManager().pushPendingDownload(new BasicPortletDownload(name, layout.getBytes()));
		} else if (this.menuFactoryListener != null) {
			this.menuFactoryListener.onContextMenu(table, action);
		} else if (AmiWebScmBasePortlet.onScmContextMenu(getSelectedAbsolutePaths(table), action, new AmiWebScmBasePortlet(generateConfig()))) {

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
		Row row = fastWebTable.getActiveRow();
		String type = getType(row);
		String name = getName(row);
		switch (this.dialogType) {
			case TYPE_SAVE_FILE:
			case TYPE_CREATE_FILE:
			case TYPE_SELECT_FILE:
				if ("F".equals(type))
					this.nameField.setValue(name);
				break;
			case TYPE_SELECT_DIR:
				if ("D".equals(type))
					this.nameField.setValue(join(getFullPath(), name));
				else if ("U".equals(type))
					this.nameField.setValue(getCurrentPathParent());
				else
					this.nameField.setValue(this.getFullPath());
				break;
			case TYPE_SELECT_BOTH:
				if (row == null)
					this.nameField.setValue(this.getFullPath());
				else if ("F".equals(type) || "D".equals(type))
					this.nameField.setValue(name);
				break;
		}
	}
	private String getType(Row row) {
		return row == null ? null : row.get("Type", String.class);
	}
	private String getName(Row row) {
		return row == null ? null : row.get("File", String.class);
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.showHiddenField || field == this.showUnpermittedField || field == this.filterField)
			updateFilesTable(this.getFullPath(), true);
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		if (field == this.nameField && keycode == 13) {
			if (SH.isnt(this.nameField.getValue()))
				return;
			onButtonPressed(formPortlet, this.okayButton);
		}
	}
	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
		List<Row> selected = this.files.getTable().getSelectedRows();
		if (selected.size() != 1)
			return;
		Row row = selected.get(0);
		String type = row.get("Type", String.class);
		String name = row.get("File", String.class);
		String t;
		String fullPath = this.getFullPath();
		if ("F".equals(type) && MH.anyBits(this.dialogType, TYPE_SELECT_FILE)) {
			onValueSelected(join(fullPath, name));
			return;
		}
		if (PARENT_FILE_CHARS.equals(name)) {
			t = getCurrentPathParent();
		} else {
			t = join(fullPath, name);
		}
		updateFilesTable(t, true);
	}
	private String getCurrentPathParent() {
		String t;
		String fullPath = IOH.toUnixFormatForce(this.getFullPath());

		char separatorChar = UNIX_SEPERATOR_CHAR;
		t = SH.beforeLast(fullPath, separatorChar, this.getFullPath());
		if ("".equals(t))
			t = UNIX_ROOT_PATH;
		return t;
	}

	@Override
	public void onColumnsArranged(WebTable fastWebTable) {
	}

	@Override
	public void onUserClick(HtmlPortlet portlet) {
		this.useUrlInput = !this.useUrlInput;
		this.updateUrlUserInteface();
	}

	public int calculateBasePath() {
		String bp = this.getBasePath();
		return SH.split(UNIX_SEPERATOR_CHAR, SH.stripPrefix(bp, UNIX_SEPERATOR_CHARS, false)).length;
	}

	@Override
	public void onUserCallback(HtmlPortlet htmlPortlet, String id, int mouseX, int mouseY, HtmlPortlet.Callback cb) {
		if (SH.startsWith(id, "url_")) {
			int pos = SH.parseInt(SH.stripPrefix(id, "url_", true));
			int start = this.getBasePath() == null ? 0 : calculateBasePath();
			char separatorChar = UNIX_SEPERATOR_CHAR;
			String[] parts = SH.split(separatorChar, SH.stripPrefix(this.getFullPath(), UNIX_SEPERATOR_CHARS, false));
			String t = SH.joinSub(separatorChar, start, start + pos, parts);
			if (this.basePath != null)
				t = join(basePath, t);
			else if (!isWindows() && THIS_FILE_CHARS.equals(t)) // input string "_/./" and user clicks on the "."
				t = UNIX_ROOT_PATH;
			else if (!isWindows() && !t.startsWith(UNIX_ROOT_PATH))
				t = UNIX_ROOT_PATH + t;
			this.updateFilesTable(t, true);
		} else if (SH.equals(id, "path_input")) {
			String path = (String) cb.getAttribute("v");
			path = SH.replaceAll(path, '\u00A0', ' ');
			this.useUrlInput = false;
			this.updateFilesTable(path, true);

		}

	}

	@Override
	public void onHtmlChanged(String left, String right) {
	}
	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if (ConfirmDialogPortlet.ID_NO.equals(id)) {
			return true;
		}
		if ("OVERWRITE".equals(source.getCallback())) {
			String finalName = (String) source.getCorrelationData();
			onValueSelected(finalName);
		} else if ("NEW_FOLDER".equals(source.getCallback())) {
			String dirname = SH.trim((String) source.getInputField().getValue());
			if (SH.isnt(dirname) || dirname.indexOf(UNIX_SEPERATOR_CHAR) != -1 || dirname.indexOf(WINDOWS_SEPERATOR_CHAR) != -1) {
				getManager().showAlert("Invalid name");
				return false;
			}
			AmiWebFile file = fs.getFile(this.getFullPath(), dirname);
			if (file.exists()) {
				getManager().showAlert("File Already exists");
				return false;
			}
			if (!file.mkdir()) {
				getManager().showAlert("Could not create folder");
				return false;
			}
			LH.info(log, "User ", getManager().describeUser(), " created new folder '", getFullPath(file), "'");
			updateFilesTable(this.getFullPath(), true);
			selectRow("D", dirname);
		} else if ("RENAME".equals(source.getCallback())) {
			String name = SH.trim((String) source.getInputField().getValue());
			if (SH.isnt(name) || name.indexOf(UNIX_SEPERATOR_CHAR) != -1 || name.indexOf(WINDOWS_SEPERATOR_CHAR) != -1) {
				getManager().showAlert("Invalid name");
				return false;
			}
			String origname = (String) source.getCorrelationData();
			if (OH.eq(name, origname)) {
				return true;
			}
			AmiWebFile old = fs.getFile(this.getFullPath(), origname);
			AmiWebFile nuw = fs.getFile(this.getFullPath(), name);
			if (nuw.exists()) {
				getManager().showAlert("File Already exists");
				return false;
			}
			if (!old.move(nuw)) {
				getManager().showAlert("Could not create folder");
				return false;
			}
			LH.info(log, "User ", getManager().describeUser(), " renamed '", getFullPath(old), "' to '", getFullPath(nuw), "'");
			updateFilesTable(this.getFullPath(), true);
			selectRow("D", name);
		} else if ("DELETE".equals(source.getCallback())) {
			List<String> names = (List<String>) source.getCorrelationData();
			for (String name : names) {
				AmiWebFile file = fs.getFile(this.getFullPath(), name);
				if (!file.delete()) {
					getManager().showAlert("Delete failed for: " + name);
				} else
					LH.info(log, "User ", getManager().describeUser(), " deleted '", getFullPath(file), "'");
			}
			updateFilesTable(this.getFullPath(), true);
		}
		return true;
	}

	public byte getType() {
		return this.dialogType;
	}
	@Override
	public WebMenu createMenu(WebTable table) {
		List<Row> selected = table.getSelectedRows();
		BasicWebMenu r = new BasicWebMenu();
		r.add(new BasicWebMenuLink("New Folder", true, "new_folder"));
		if (selected.size() == 1) {
			r.add(new BasicWebMenuLink("Rename", true, "rename"));
			if (this.sourceType != null)
				r.add(new BasicWebMenuLink("Get URL", true, "get_url"));
			String fileType = (String) (selected.get(0).getAt(0));
			// disallow download for directory
			if (!"D".equals(fileType))
				r.add(new BasicWebMenuLink("Download", true, "download"));
		}
		if (selected.size() >= 1) {
			r.add(new BasicWebMenuLink("Delete", true, "delete"));
		}
		if (this.menuFactoryListener != null)
			r.add(this.menuFactoryListener.createMenu(table));
		WebMenu scmMenu = AmiWebScmBasePortlet.createScmMenu(getSelectedAbsolutePaths(table), service);
		if (scmMenu != null)
			r.add(scmMenu);
		return r;
	}
	@Override
	public void onNoSelectedChanged(FastWebTable fastWebTable) {
	}
	private List<String> getSelectedAbsolutePaths(WebTable table) {
		List<String> paths = new ArrayList<String>();
		for (Row row : table.getSelectedRows()) {
			String file = row.get(COLUMN_ABSOLUTE_PATH, String.class);
			paths.add(file);
		}
		return paths;
	}
	public String getFullPath() {
		if (basePath == null)
			return currentPath;
		else
			return join(basePath, currentPath);
	}

	public String getBasePath() {
		return this.basePath;
	}
	public String getCurrentPath() {
		return this.currentPath;
	}
	@Override
	public void onFilterChanging(WebTable fastWebTable) {
	}
	@Override
	public void onColumnsSized(WebTable fastWebTable) {
	}
	@Override
	public void onScroll(int viewTop, int viewPortHeight, long contentWidth, long contentHeight) {
	}
}
