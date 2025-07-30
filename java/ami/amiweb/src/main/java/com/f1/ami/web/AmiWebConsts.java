package com.f1.ami.web;

public class AmiWebConsts {
	public static final String URL_PARAM_LAYOUT = "LAYOUT";

	public static final String CALLBACK_PREFIX_DELIM = "!";
	public static final String FORMULA_PREFIX_DELIM = "^";

	public static final String LAYOUT_SOURCE_CLOUD = "CLOUD";
	public static final String LAYOUT_SOURCE_LOCAL = "LOCAL";
	public static final String LAYOUT_SOURCE_SHARED = "SHARED";
	public static final String LAYOUT_SOURCE_ABSOLUTE = "ABSOLUTE";
	public static final String LAYOUT_SOURCE_TMP = "TMP";
	public static final String ENCRYPTED_NONE = "NONE";
	public static final String ENCRYPTED_PLAINTEXT = "PLAINTEXT";
	public static final String ENCRYPTED_ENCRYPTED = "ENCRYPTED";

	public static final String USER_SETTING_AMI_LAYOUT_PREFIX = "ami_layout_";
	public static final String USER_SETTING_AMI_PREFS_PREFIX = "ami_prefs_";

	public static final String USER_SETTING_AMI_LAYOUT_CURRENT_SOURCE = "ami_layout_current_source";
	public static final String USER_SETTING_AMI_LAYOUT_CURRENT = "ami_layout_current";
	public static final String USER_SETTING_AMI_LAYOUT_SHARED = "ami_layout_shared";//DEPRECATED, use DEFAULT_LAYOUT=SHARED:<layoutname>
	public static final String USER_SETTING_AMI_EDITOR_KEYBOARD = "developer_ami_editor_keyboard";
	public static final String USER_SETTING_EXPORT = "developer_export_setting";
	public static final String USER_SETTING_LOGOUT = "developer_logout";//Either Debug,Logout,Ignore, null should default to Debug
	public static final String USER_SETTING_DEBUG_INFO = "debug_info_setting_key";
	public static final String USER_SETTING_DEBUG_WARNING = "debug_warning_setting_key";
	public static final String USER_SETTING_DEVELOPER_HEADERS = "developer_headers";
	public static final String USER_SETTING_SHOW_STYLE_EDITOR_TABS = "show_style_editor_tabs";
	//	public static final String USER_SETTING_SHOW_STYLE_EDITOR_DIVIDER = "show_style_editor_divider";
	public static final String USER_SETTING_SHOW_SETTING_DIVIDER = "show_setting_divider";
	public static final String USER_SETTING_SHOW_AUTOSAVE_PROMPT = "show_autosave_prompt";
	public final static String USER_SETTING_LANGUAGE = "language";
	public final static String USER_SETTING_TIME_ZONE = "timeZone";
	public final static String USER_SETTING_DATE_FORMAT = "dateFormat";
	public final static String USER_SETTING_TIME_FORMAT = "timeFormat";
	public final static String USER_SETTING_NUMBER_SEPARATOR = "numberSeparator";
	public final static String USER_SETTING_NUMBER_DECIMAL_PRECISION = "numberDecimalPrecision";
	public final static String USER_SETTING_NUMBER_NEGATIVE_FORMAT = "numberNegativeFormat";
	public final static String USER_SETTING_SPREAD_SHEET_FORMAT_OPTION = "spreadSheetFormatOption";
	public final static String USER_SETTING_SCI_NOT_NUM_DIGITS_LEFT = "sciNotDigitsLeft";
	public final static String USER_SETTING_SCI_NOT_NUM_DIGITS_RIGHT = "sciNotDigitsRight";
	public static final String USER_SETTING_AUTOAPPLY_USERPREFS = "autoApplyUserPrefs";
	public static final String USER_SETTING_DEVELOPER_SCM_TYPE = "scm_plugin";
	public static final String USER_SETTING_DEVELOPER_SCM_URL = "developer_scm_url";
	public static final String USER_SETTING_DEVELOPER_SCM_CLIENT = "developer_scm_client";
	public static final String USER_SETTING_DEVELOPER_SCM_USERNAME = "developer_scm_username";
	public static final String USER_SETTING_DEVELOPER_SCM_PASSWORD = "developer_scm_password";
	public static final String USER_SETTING_DEVELOPER_SCM_SAVE_PASSWORD_MODE = "developer_scm_save_password_mode";
	public static final String USER_SETTING_DEVELOPER_SCM_OPTIONS = "developer_scm_options";
	public static final String USER_SETTING_DEVELOPER_SCM_PATH = "developer_scm_path";
	public static final String USER_SETTING_DEFAULT_FILE_BROWSER_PATH = "defaultFileBrowserPath";
	public static final String USER_SETTING_DEVELOPER_DM_SNAP = "developer_dm_snapsize";
	public static final String USER_SETTING_DEVELOPER_DM_GRID = "developer_dm_gridsize";

	public static final String ASK = "ask";
	public static final String ALWAYS = "always";
	public static final String NEVER = "never";
	public static final String DEBUG_ONLY = "debug_only";
	public static final String DEVELOPER_HEADERS_EXPAND = "developer_headers_expand";
	public static final String DEVELOPER_HEADERS_COLLAPSE = "developer_headers_collapse";
	public static final String STYLE_EDITOR_SHOW = "show_editor";
	public static final String STYLE_EDITOR_HIDE = "hide_editor";
	public static final String DEVELOPER_AUTOSAVE_SHOW = "developer_autosave_show";
	public static final String DEVELOPER_AUTOSAVE_HIDE = "developer_autosave_hide";

	public static final String TITLE_CSS = "_bg=#CCEECC|_fg=#008800|style.fontSize=14px|_fm=bold|className=ami_edit_menu";
	public static final String TITLE_CSS2 = "_bg=#CCEECC|_fg=#008800|style.fontSize=14px|_fm=bold|className=ami_edit_menu2";

	public static final String PREFIX = "rsc/ami/";
	public static final String ICON_ADD = PREFIX + "add.png";
	public static final String ICON_ADD_ORANGE = PREFIX + "add_orange.png";
	public static final String ICON_ADD_PANEL_BOTTOM = PREFIX + "add_panel_bottom.png";
	public static final String ICON_ADD_PANEL_LEFT = PREFIX + "add_panel_left.png";
	public static final String ICON_ADD_PANEL_RIGHT = PREFIX + "add_panel_right.png";
	public static final String ICON_ADD_PANEL_TOP = PREFIX + "add_panel_top.png";
	public static final String ICON_ALERT = PREFIX + "alert.png";
	public static final String ICON_AMI = PREFIX + "ami.png";
	public static final String ICON_AMI_FILE = PREFIX + "ami_file.png";
	public static final String ICON_CHANGE_2_TABS = PREFIX + "change_2_tabs.png";
	public static final String ICON_CHECK = PREFIX + "check.png";
	public static final String ICON_COPY = PREFIX + "copy.png";
	public static final String ICON_CUT = PREFIX + "cut.png";
	public static final String ICON_DATA_SOURCE = PREFIX + "data_source.png";
	public static final String ICON_DATAMODEL = PREFIX + "dms.svg";
	public static final String ICON_DEBUG = PREFIX + "bug.svg";
	public static final String ICON_DELETE = PREFIX + "delete.png";
	public static final String ICON_DELETE_CLOUD = PREFIX + "delete_cloud.png";
	public static final String ICON_DOT = PREFIX + "dot.png";
	public static final String ICON_DOWN = PREFIX + "down.png";
	public static final String ICON_EXPORT = PREFIX + "export.png";

	// top menu icons
	public static final String ICON_ABOUT = PREFIX + "about.svg";
	public static final String ICON_ABS_OPEN = PREFIX + "abs-open.svg";
	public static final String ICON_ABS_SAVEAS = PREFIX + "abs-saveas.svg";
	public static final String ICON_MYLAYOUT_OPEN = PREFIX + "my-open.svg";
	public static final String ICON_MYLAYOUT_SAVEAS = PREFIX + "my-saveas.svg";
	public static final String ICON_EXPORT_ROOT = PREFIX + "exp-root.svg";
	public static final String ICON_EXPORT_ROOT_ALL = PREFIX + "exp-all.svg";
	public static final String ICON_UPLOAD = PREFIX + "upload.svg";
	public static final String ICON_DOWNLOAD = PREFIX + "download.svg";
	public static final String ICON_NEW_WINDOW = PREFIX + "window.svg";
	public static final String ICON_MANAGE_WINDOWS = PREFIX + "manage-windows.svg";
	public static final String ICON_DEV_SETTINGS = PREFIX + "dev-settings.svg";
	public static final String ICON_SC_SETTINGS = PREFIX + "source-control.svg";
	public static final String ICON_USER_SETTINGS = PREFIX + "user-settings.svg";
	public static final String ICON_SHUTDOWN = PREFIX + "shutdown.svg";
	public static final String ICON_DOCUMENTATION = PREFIX + "amiscript-doc.svg";
	public static final String ICON_EDITORS = PREFIX + "editors.svg";
	public static final String ICON_DASH_OBJECTS = PREFIX + "dash-objects.svg";
	public static final String ICON_INCLUDED_LAYOUTS = PREFIX + "included-layouts.svg";
	public static final String ICON_SESSION_VARS = PREFIX + "session-vars.svg";
	public static final String ICON_DASH_STYLES = PREFIX + "dashboard-styles.svg";
	public static final String ICON_DASH_SETTINGS = PREFIX + "dash-settings.svg";
	public static final String ICON_STYLE_MANAGER = PREFIX + "style-manager.svg";
	public static final String ICON_RSC_MANAGER = PREFIX + "rsc-manager.svg";
	public static final String ICON_USER = PREFIX + "user.svg";
	public static final String ICON_RESET = PREFIX + "reset.svg";
	public static final String ICON_LOAD_DATA = PREFIX + "load-data.svg";
	public static final String ICON_ADVANCED = PREFIX + "advanced.svg";
	public static final String ICON_CUSTOM_CALLBACKS = PREFIX + "data-stat2.svg";
	public static final String ICON_LICENSE = PREFIX + "license.svg";
	public static final String ICON_LICENSE_INFO = PREFIX + "license-info.svg";
	public static final String ICON_SET_DEFAULTS = PREFIX + "defaults-manager.svg";
	public static final String ICON_CUSTOM_METHODS = PREFIX + "cust-methods.svg";
	public static final String ICON_CUSTOM_CSS = PREFIX + "cust-css.svg";
	public static final String ICON_STATISTICS = PREFIX + "data-stat.svg";
	public static final String ICON_DIFF = PREFIX + "diff.svg";
	public static final String ICON_SETTINGS = PREFIX + "rebuild.svg";
	public static final String ICON_SIMULATOR = PREFIX + "simulator.svg";

	// field menu icons
	public static final String ICON_FIELD_BUTTON = PREFIX + "field-button.svg";
	public static final String ICON_FIELD_CHECK_BOX = PREFIX + "field-checkbox.svg";
	public static final String ICON_FIELD_RADIO_BUTTON = PREFIX + "field-radio.svg";
	public static final String ICON_FIELD_COLOR_PICKER = PREFIX + "field-colorpicker.svg";
	public static final String ICON_FIELD_DATE = PREFIX + "field-date.svg";
	public static final String ICON_FIELD_TIME = PREFIX + "field-time.svg";
	public static final String ICON_FIELD_DATETIME = PREFIX + "field-datetime.svg";
	public static final String ICON_FIELD_FILE_UPLOAD = PREFIX + "icon-upload.svg";
	public static final String ICON_FIELD_IMAGE = PREFIX + "field-image.svg";
	public static final String ICON_FIELD_NUMERIC_RANGE = PREFIX + "field-rangeslider.svg";
	public static final String ICON_FIELD_NUMERIC_SLIDER = PREFIX + "field-slider.svg";
	public static final String ICON_FIELD_SELECT = PREFIX + "field-select.svg";
	public static final String ICON_FIELD_MULTI_SELECT = PREFIX + "field-multiselect.svg";
	public static final String ICON_FIELD_TEXT_AREA = PREFIX + "field-textarea.svg";
	public static final String ICON_FIELD_TEXT = PREFIX + "field-text.svg";
	public static final String ICON_FIELD_PASSWORD = PREFIX + "field-password.svg";
	public static final String ICON_FIELD_COLOR_PICKER_GRADIENT = PREFIX + "field-gradientpicker.svg";
	public static final String ICON_FIELD_MULTI_CHECKBOX = PREFIX + "field-multicheckbox.svg";

	public static final String ICON_FILES = PREFIX + "files.png";
	public static final String ICON_FILTER_ORANGE = PREFIX + "filter_orange.png";
	public static final String ICON_FILTER = PREFIX + "filter.png";
	public static final String ICON_FLIP = PREFIX + "flip.png";
	public static final String ICON_FOLDER = PREFIX + "folder.png";
	public static final String ICON_FORM = PREFIX + "form.png";
	public static final String ICON_FORM_ORANGE = PREFIX + "form_orange.png";
	public static final String ICON_FULL_SCREEN = PREFIX + "fullscreen.svg";
	public static final String ICON_HTML = PREFIX + "html.png";
	public static final String ICON_IMPORT = PREFIX + "import.svg";
	public static final String ICON_IMPORT_CLOUD = PREFIX + "cloud-open.svg";
	public static final String ICON_INCREASE = PREFIX + "increase.png";
	public static final String ICON_INFO = PREFIX + "info.png";
	public static final String ICON_LEFT = PREFIX + "left.png";
	public static final String ICON_LOCK = PREFIX + "lock.png";
	public static final String ICON_LOGOUT = PREFIX + "logout.svg";
	public static final String ICON_NEW = PREFIX + "new.svg";
	public static final String ICON_NEW_PANEL = PREFIX + "new_panel.png";
	public static final String ICON_NORMAL = PREFIX + "normal.png";
	public static final String ICON_OPEN = PREFIX + "open.png";
	public static final String ICON_PASTE = PREFIX + "paste.png";
	public static final String ICON_PENCIL = PREFIX + "pencil.png";
	public static final String ICON_PLACE_TAB = PREFIX + "place_tab.png";
	public static final String ICON_PLACE_IN_TAB = PREFIX + "place_in_tab.png";
	public static final String ICON_PLACE_WINDOW = PREFIX + "place_window.png";
	public static final String ICON_PUBLISH_CLOUD = PREFIX + "cloud-saveas.svg";
	public static final String ICON_RIGHT = PREFIX + "right.png";
	public static final String ICON_ROTATE_CLOCKWISE = PREFIX + "recovery.svg";
	public static final String ICON_ROTATE_COUNTER = PREFIX + "recovery.svg";
	public static final String ICON_SAVE = PREFIX + "save.svg";
	public static final String ICON_SAVE_AS = PREFIX + "save_as.png";
	public static final String ICON_SPLIT_HORIZ = PREFIX + "split_horiz.png";
	public static final String ICON_SPLIT_VERT = PREFIX + "split_vert.png";
	public static final String ICON_STAR = PREFIX + "star.png";
	public static final String ICON_STYLING = PREFIX + "styling.png";
	public static final String ICON_TEMPLATE = PREFIX + "template.png";
	public static final String ICON_THIN = PREFIX + "thin.png";
	public static final String ICON_UNLOCK = PREFIX + "unlock.png";
	public static final String ICON_UP = PREFIX + "up.png";
	public static final String ICON_USERS = PREFIX + "users.png";
	public static final String ICON_VIZ = PREFIX + "viz.png";
	public static final String ICON_VIZ_ORANGE = PREFIX + "viz_orange.png";
	public static final String ICON_WARNING = PREFIX + "warning.png";
	public static final String ICON_VARIABLE_TABLE = PREFIX + "variable_table.png";
	public static final String ICON_CLEAR_DATA = PREFIX + "clear_data.png";
	public static final String ICON_UNDOCK = PREFIX + "undock.png";
	public static final String ICON_AMI_SYSTEM_OBJECTS = PREFIX + "ami_system_objects.png";
	public static final String ICON_DATA = PREFIX + "data.png";
	public static final String ICON_UPLOAD_DATA = PREFIX + "upload_data.png";
	public static final String ICON_FIELD = PREFIX + "field.png";
	public static final String ICON_BUTTON = PREFIX + "button.png";
	public static final String ICON_VALUE = PREFIX + "value.png";
	public static final String ICON_DATAMODEL2 = PREFIX + "datamodel2.png";
	public static final String ICON_MENUITEM = PREFIX + "menuitem.png";
	public static final String ICON_GROUPING = PREFIX + "grouping.png";
	public static final String ICON_AXIS = PREFIX + "axis.png";
	public static final String ICON_PLOT = PREFIX + "plot.png";
	public static final String ICON_COLUMN = PREFIX + "column.png";
	public static final String ICON_LAYER = PREFIX + "layer.png";
	public static final String ICON_RELATIONSHIP = PREFIX + "relationship.png";
	public static final String ICON_RELATIONSHIP_WHERE = PREFIX + "relationship.png";
	public static final String ICON_PANEL = PREFIX + "panel.png";
	public static final String ICON_LAYOUT2 = PREFIX + "layout2.png";
	public static final String ICON_PROCESSOR = PREFIX + "processor.png";

	// documentation icons
	public static final String ICON_CLASS = PREFIX + "icon-class.png";
	public static final String ICON_METHOD = PREFIX + "icon-method.png";
	public static final String ICON_CALLBACK = PREFIX + "icon-callback.png";

	public static final String DSLAYOUT_NAME = "__LAYOUT";
	public static final String DSLAYOUT_ADAPTER = "__LAYOUT";

	//datamodel variable tree icons
	public static final String DM_TREE_ICON_DS = PREFIX + "dm-tree-icon-ds.png";
	public static final String DM_TREE_ICON_DM = PREFIX + "dm-tree-icon-dm.png";
	public static final String DM_TREE_ICON_BLENDER = PREFIX + "dm-tree-icon-blender.png";

	public static final String DM_TREE_ICON_PANEL_ST = PREFIX + "dm-tree-icon-st-panel.png";
	public static final String DM_TREE_ICON_PANEL_RT = PREFIX + "dm-tree-icon-rt-panel.png";

	public static final String DM_TREE_ICON_PROCESSOR = PREFIX + "dm-tree-icon-processor.png";
	public static final String DM_TREE_ICON_FEED = PREFIX + "dm-tree-icon-feed.png";

	public static final String DM_TREE_ICON_WINDOW = PREFIX + "dm-tree-icon-window.png";
	public static final String DM_TREE_ICON_DESKTOP = PREFIX + "dm-tree-icon-desktop.png";
	public static final String DM_TREE_ICON_TABSPANEL = PREFIX + "dm-tree-icon-tabspanel.png";
	public static final String DM_TREE_ICON_TAB = PREFIX + "dm-tree-icon-tab.png";
	public static final String DM_TREE_ICON_DIVIDER_H = PREFIX + "dm-tree-icon-divider-h.png";
	public static final String DM_TREE_ICON_DIVIDER_V = PREFIX + "dm-tree-icon-divider-v.png";
	public static final String DM_TREE_ICON_RELATIONSHIP = PREFIX + "dm-tree-icon-relationship.png";
	public static final String DM_TREE_ICON_ERROR = PREFIX + "dm-tree-icon-error.png";
}
