package com.f1.ami.portlets;

import java.util.Map;

import com.f1.ami.web.AmiWebConsts;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletContainer;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;
import com.f1.suite.web.portal.style.PortletStyleManager_Form;
import com.f1.utils.OH;

public class AmiWebHeaderPortlet extends GridPortlet implements FormPortletListener, FormPortletContextMenuListener {
	private static final int LEGEND_WIDTH = 450;
	private static final int COL2_WIDTH = 148;
	private static final int SEARCH_WIDTH = 302;
	private static final int SHORT_INFORMATION = 0;
	private static final int FULL_INFORMATION = 100;
	private static final int ROW0_HEIGHT = 20;
	private static final int SEARCH_HEIGHT = 44;
	private GridPortlet parent;
	private FormPortlet blurb;
	private FormPortlet legend;
	private FormPortlet hamburger;
	private FormPortlet search;
	private FormPortlet bar;
	private FormPortletToggleButtonsField<Boolean> hamburgerButton;
	private FormPortletTextField searchInputField;
	private FormPortletButtonField searchButton;
	private FormPortletButtonField nextButton;
	private FormPortletButtonField previousButton;
	private FormPortletTitleField searchHelper;
	private Integer informationHeaderHeight = null;

	private AmiWebHeaderSearchHandler listener;

	private String blurbTitle;
	private String blurbBlop;
	private boolean legendVisible;
	private boolean showSearch;
	private boolean showBar;
	private boolean showLegend;
	private int legendWidth;
	private AmiWebService service;

	public AmiWebHeaderPortlet(PortletConfig config) {
		super(config);
		this.service = AmiWebUtils.getService(config.getPortletManager());
		this.legendWidth = -1;
		legendVisible = true;
		showSearch = true;
		showBar = true;
		showLegend = true;
		initPortlets();
		initGrid();
		initListeners();
		updateLayouts();
		loadGlobalDisplayLegend();
	}
	private void loadGlobalDisplayLegend() {
		String expandLegend = service.getVarsManager().getSetting(AmiWebConsts.USER_SETTING_DEVELOPER_HEADERS);
		if (expandLegend == null)
			return;
		if (expandLegend.equals(AmiWebConsts.DEVELOPER_HEADERS_EXPAND))
			this.legendVisible = true;
		else if (expandLegend.equals(AmiWebConsts.DEVELOPER_HEADERS_COLLAPSE))
			this.legendVisible = false;
		else
			return;
		this.displayLegend(this.legendVisible);
		this.hamburgerButton.setValue(this.legendVisible);
	}
	private void updateLayouts() {
		this.updateBlurbPortletLayout("", "");
		this.updateLegendPortletLayout("");
		this.updateHamburgerPortletLayout();
		this.updateSearchPortletLayout();
		this.updateBarPortletLayout(null);
	}
	private void initPortlets() {
		hamburger = new FormPortlet(generateConfig());
		blurb = new FormPortlet(generateConfig());
		legend = new FormPortlet(generateConfig());
		search = new FormPortlet(generateConfig());
		bar = new FormPortlet(generateConfig());

		this.initHamburgerPortlet();
		this.initSearchPortlet();
	}

	private void initGrid() {
		addChild(hamburger, 1, 0, 2, 1);
		addChild(blurb, 0, 0, 1, 2);
		addChild(legend, 1, 1, 2, 1);
		addChild(bar, 0, 2, 2, 1);
		addChild(search, 2, 2, 1, 1);
		this.setColSize(1, COL2_WIDTH);
		this.setColSize(2, SEARCH_WIDTH);
		this.setRowSize(0, AmiWebHeaderPortlet.ROW0_HEIGHT);
		this.setRowSize(1, AmiWebHeaderPortlet.FULL_INFORMATION);
		this.setRowSize(2, AmiWebHeaderPortlet.SEARCH_HEIGHT);

	}
	private void initListeners() {
		this.hamburger.addFormPortletListener(this);
	}
	private void initHamburgerPortlet() {
		this.search.addMenuListener(this);
		hamburgerButton = new FormPortletToggleButtonsField<Boolean>(Boolean.class, "");
		hamburgerButton.addOption(true, "");
		hamburgerButton.addOption(false, "");
		hamburgerButton.setMode('T');
		hamburger.addField(hamburgerButton);
		this.hamburgerButton.setRightPosPx(2).setWidthPx(20).setTopPosPx(1).setHeightPx(17);
	}

	private void initSearchPortlet() {
		searchInputField = new FormPortletTextField("");
		searchButton = new FormPortletButtonField("").setValue("Search").setTitle("");
		nextButton = new FormPortletButtonField("").setValue(">").setTitle("");
		previousButton = new FormPortletButtonField("").setValue("<").setTitle("");
		searchHelper = new FormPortletTitleField("");
		search.addField(nextButton);
		search.addField(previousButton);
		search.addField(searchHelper);
		search.addField(searchInputField);
		search.addField(searchButton);
	}

	public void updateBlurbPortletLayout(String title, String blop) {
		StringBuilder infoText = new StringBuilder();

		blurbTitle = title != null ? title : "";
		blurbBlop = blop != null ? blop : "";
		infoText.append("<div style=\"height:100%; width:100%; background:#4c4c4c;\">");
		infoText.append("<div style=\"padding:6px 24px; \">");
		infoText.append("<h1 style=\"position:relative; color:white;\">");
		infoText.append(blurbTitle);
		infoText.append("</h1>");
		infoText.append("<div style=\"position:relative; color:white;\">");
		infoText.append(blurbBlop);
		infoText.append("</div>");
		infoText.append("</div>");
		infoText.append("</div>");

		blurb.setHtmlLayout(infoText.toString());
	}
	public void updateLegendPortletLayout(String htmlLayout) {
		StringBuilder legendHtml = new StringBuilder();

		legendHtml.append("<div style= \"height:100%; width:100%; background:#4c4c4c; position:relative;\">");
		if (htmlLayout != null) {
			legendHtml.append(htmlLayout);
		}
		legendHtml.append("</div>");
		legend.setHtmlLayout(legendHtml.toString());
	}

	private void updateHamburgerPortletLayout() {
		String visible = "<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 415 280\" xml:space=\"preserve\"><g id=\"hamburger\"><g><line style=\"fill:none;stroke:%23FFFFFF;stroke-width:43;stroke-miterlimit:10;\" x1=\"26.3\" y1=\"56.7\" x2=\"389.1\" y2=\"56.7\"/><line style=\"fill:none;stroke:%23FFFFFF;stroke-width:43;stroke-miterlimit:10;\" x1=\"28.7\" y1=\"141.7\" x2=\"391.6\" y2=\"141.7\"/><line style=\"fill:none;stroke:%23FFFFFF;stroke-width:43;stroke-miterlimit:10;\" x1=\"28.7\" y1=\"226.7\" x2=\"391.6\" y2=\"226.7\"/></g></g></svg>";
		String hidden = "<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 415 280\" xml:space=\"preserve\"><g id=\"hide\"><g><line style=\"fill:none;stroke:%23FFFFFF;stroke-width:43;stroke-miterlimit:10;\" x1=\"93.4\" y1=\"26.9\" x2=\"321.6\" y2=\"255\"/><line style=\"fill:none;stroke:%23FFFFFF;stroke-width:43;stroke-miterlimit:10;\" x1=\"93.4\" y1=\"255\" x2=\"321.6\" y2=\"26.9\"/></g></g></svg>";

		StringBuilder css0 = new StringBuilder();
		css0.append("style.backgroundImage=url('data:image/svg+xml;utf8,");
		css0.append(hidden);
		css0.append("')|style.backgroundPosition=center|style.backgroundRepeat=no-repeat");

		StringBuilder css1 = new StringBuilder();
		css1.append("style.background=none|style.backgroundImage=url('data:image/svg+xml;utf8,");
		css1.append(visible);
		css1.append("')|style.backgroundPosition=center|style.backgroundRepeat=no-repeat|style.boxShadow=none");

		hamburgerButton.setButtonStyle("_h=20px|style.minWidth=20px|_w=20px|style.outline=none|style.borderRadius=4px");
		hamburgerButton.setButtonStyleAtIndex(css0.toString(), 0);
		hamburgerButton.setButtonStyleAtIndex(css1.toString(), 1);
		hamburgerButton.setCssStyle("style.float=right");

		PortletStyleManager_Form hamburgerFormStyle = new PortletStyleManager_Form();
		hamburgerFormStyle.setDefaultFormButtonPanelStyle("_bg=#4c4c4c");
		hamburgerFormStyle.setFormStyle("_bg=#4c4c4c");
		hamburger.setStyle(hamburgerFormStyle);
	}

	private void updateSearchPortletLayout() {
		if (showSearch) {
			int leftOffsetPx = 0; // for testing only... 
			this.searchButton.setLeftPosPx(leftOffsetPx);
			this.searchButton.setTopPosPx(10);
			this.searchButton.setWidthPx(60);
			this.searchButton.setHeightPx(25);

			this.searchInputField.setLeftPosPx(leftOffsetPx + this.searchButton.getLeftPosPx() + this.searchButton.getWidthPx());
			this.searchInputField.setTopPosPx(10);
			this.searchInputField.setWidthPx(160);
			this.searchInputField.setHeightPx(25);

			this.searchHelper.setLeftPosPx(this.searchInputField.getLeftPosPx());
			this.searchHelper.setTopPosPx(10);
			this.searchHelper.setWidthPx(160);
			this.searchHelper.setHeightPx(25);

			this.previousButton.setLeftPosPx(leftOffsetPx + this.searchInputField.getLeftPosPx() + this.searchInputField.getWidthPx());
			this.previousButton.setTopPosPx(10);
			this.previousButton.setWidthPx(28);
			this.previousButton.setHeightPx(25);

			this.nextButton.setLeftPosPx(leftOffsetPx + this.previousButton.getLeftPosPx() + this.previousButton.getWidthPx());
			this.nextButton.setTopPosPx(10);
			this.nextButton.setWidthPx(28);
			this.nextButton.setHeightPx(25);

			searchHelper.setCssStyle("_cn=ami_header_search_helper");
			searchInputField.setCssStyle("_bg=rgba(0,0,0,0)|style.color=black|style.outline=none|style.padding=4px|style.border=none|style.float=right|placeholder=Search");
			searchButton.setCssStyle("_cn=ami_header_search_btn");
			nextButton.setCssStyle("_cn=ami_header_next_btn");
			previousButton.setCssStyle("_cn=ami_header_previous_btn");
			this.search.getFormPortletStyle().setCssStyle("_bg=#cccccc");
			this.legendWidth = legendWidth == -1 ? COL2_WIDTH : legendWidth;
			this.setColSize(1, this.legendWidth);
			this.setColSize(2, SEARCH_WIDTH);
		} else {
			search.setHtmlLayout("");
			search.getFormPortletStyle().setCssStyle("_bg=#cccccc");

			this.legendWidth = legendWidth == -1 ? LEGEND_WIDTH : legendWidth;
			this.setColSize(1, this.legendWidth);
			this.setColSize(2, 0);
		}
		if (this.showLegend == false)
			this.setColSize(1, 40);
	}
	public void updateSearchHelperText(Integer current, Integer total) {
		if (current == null && total == null)
			searchHelper.setValue("");
		else if (current == null)
			searchHelper.setValue(total + " found");
		else
			searchHelper.setValue(current + " of " + total);
	}

	public void updateBarPortletLayout(String inner) {
		if (showBar) {
			StringBuilder layout = new StringBuilder();
			layout.append("<div style=\"width:100%;height:100%;background:#cccccc; padding:8px 12px\">");
			if (inner != null)
				layout.append(inner);
			layout.append("</div>");
			bar.setHtmlLayout(layout.toString());
		} else {
			bar.setHtmlLayout("");
			this.setRowSize(2, 0);
		}
		bar.getFormPortletStyle().setButtonPanelStyle("_cna=ami_header_buttons_panel");
	}

	public FormPortlet getBarFormPortlet() {
		return this.bar;
	}
	public FormPortlet getSearchFormPortlet() {
		return this.search;
	}
	public FormPortletButtonField getSearchButton() {
		return this.searchButton;
	}
	public FormPortletButtonField getNextButton() {
		return this.nextButton;
	}
	public FormPortletButtonField getPreviousButton() {
		return this.previousButton;
	}
	public String getSearchInputValue() {
		return this.searchInputField.getValue();
	}
	private void displayLegend(boolean isFull) {
		int height = isFull ? this.informationHeaderHeight != null ? this.informationHeaderHeight : AmiWebHeaderPortlet.FULL_INFORMATION : AmiWebHeaderPortlet.SHORT_INFORMATION;
		if (isFull) {
			this.setRowSize(1, height);
			this.updateBlurbPortletLayout(blurbTitle, blurbBlop);
			legend.getFormPortletStyle().setCssStyle("style.display=block");
		} else {
			this.setRowSize(1, height);
			blurb.setHtmlLayout("");
			blurb.getFormPortletStyle().setCssStyle("_bg=#4c4c4c");
			legend.getFormPortletStyle().setCssStyle("style.display=none");
		}
		this.updateHeaderHeight();
	}
	public int getHeaderHeight() {
		int height = 0;
		height += AmiWebHeaderPortlet.ROW0_HEIGHT;
		if (this.legendVisible) {
			if (informationHeaderHeight == null)
				height += AmiWebHeaderPortlet.FULL_INFORMATION;
			else
				height += this.informationHeaderHeight;
		} else {
			height += AmiWebHeaderPortlet.SHORT_INFORMATION;
		}
		if (showBar)
			height += AmiWebHeaderPortlet.SEARCH_HEIGHT;
		return height;
	}

	public void setInformationHeaderHeight(int height) {
		this.informationHeaderHeight = height;
		this.displayLegend(this.legendVisible);
	}

	public void updateHeaderHeight() {
		if (parent != null) {
			int row = parent.getChildOffsetY(this.getPortletId());
			parent.setRowSize(row, this.getHeaderHeight());
		}
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {

	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.hamburgerButton) {
			this.legendVisible = hamburgerButton.getValue();
			this.displayLegend(legendVisible);
		}
	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}
	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		if (listener != null) {
			if (node == this.searchButton) {
				listener.doSearch();
			} else if (node == this.nextButton) {
				listener.doSearchNext();
			} else if (node == this.previousButton) {
				listener.doSearchPrevious();
			}
		}
	}
	@Override
	public void setParent(PortletContainer parent) {
		if (parent != null) {
			this.parent = OH.assertInstanceOf(parent, GridPortlet.class);
		}
		super.setParent(parent);
		if (parent != null && this.parent != null) {
			this.updateHeaderHeight();
		}
	}

	public void addListenersToParent(FormPortletListener _parent) {
		this.search.addFormPortletListener(_parent);
		this.bar.addFormPortletListener(_parent);
	}
	public void removeListenersFromParent(FormPortletListener _parent) {
		this.search.removeFormPortletListener(_parent);
		this.bar.removeFormPortletListener(_parent);
	}
	@Override
	public void onClosed() {
		this.hamburger.removeFormPortletListener(this);
		super.onClosed();
	}
	public void setShowSearch(boolean show) {
		if (showSearch == show)
			return;
		this.showSearch = show;
		this.updateSearchPortletLayout();
	}
	public void setShowBar(boolean show) {
		if (showBar == show)
			return;
		this.showBar = show;
		this.updateBarPortletLayout(null);
		this.updateHeaderHeight();
	}
	public void setShowLegend(boolean b) {
		if (showLegend == b)
			return;
		this.showLegend = b;
		this.updateSearchPortletLayout();
	}
	public void setLegendWidth(int width) {
		this.legendWidth = width;
		this.updateSearchPortletLayout();
	}
	public void setSearchHandler(AmiWebHeaderSearchHandler listener) {
		this.listener = listener;
		this.search.addFormPortletListener(listener);
	}
	public void removeSearchHandler() {
		this.search.removeFormPortletListener(listener);
		this.listener = null;
	}
}
