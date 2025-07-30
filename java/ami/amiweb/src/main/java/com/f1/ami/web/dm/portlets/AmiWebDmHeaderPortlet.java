package com.f1.ami.web.dm.portlets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.f1.ami.portlets.AmiWebHeaderPortlet;
import com.f1.ami.portlets.AmiWebHeaderSearchHandler;
import com.f1.ami.web.graph.AmiWebGraphNode;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.visual.GraphMovementListener;
import com.f1.suite.web.portal.impl.visual.GraphPortlet;
import com.f1.suite.web.portal.impl.visual.GraphPortlet.Edge;
import com.f1.suite.web.portal.impl.visual.GraphPortlet.Node;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;

public class AmiWebDmHeaderPortlet implements AmiWebHeaderSearchHandler, GraphMovementListener {

	private static final String ATTACH_DATASOURCE = "Attach Datasource";
	private static final String SHOW_DIVIDERS = "Show Dividers";
	public static final String RT_OBJECTS_HTML = "<span style=\"color:#fbe983\"> <B>Realtime objects are yellow</B></span>";
	public static final String ST_OBJECTS_HTML = "<span style=\"color:#7fc280\"> <B>static items are green</B></span>";
	public static final String LT_OBJECTS_HTML = "<span style=\"color:#8eccf1\"> <B>layout items are blue</B></span>";
	public static final String DATAMODELER_HELP_HTML = "<span style=\"line-height:1.4;font-size:15px;margin-top:-10px;\"> The left tree displays all dashboard objects, available datasources and realtime feeds. Select items in the tree to see a graph displaying the linkage between them. Right click anywhere to view available actions. Use the AmiScript Tab to modify AmiScript associated with objects selected in the tree."
			+ "<br>" + RT_OBJECTS_HTML + "," + ST_OBJECTS_HTML + " and" + LT_OBJECTS_HTML + ".</span>";
	public static final String MINIMAL_HELP_HTML = RT_OBJECTS_HTML + "," + ST_OBJECTS_HTML + " and" + LT_OBJECTS_HTML;
	private final AmiWebHeaderPortlet header;
	private final FormPortletButton addDataSource;
	//	private final FormPortletMultiCheckboxField<String> layouts;
	private String previousSearch = "";
	final private List<Node> selectedCycler = new ArrayList<Node>();
	private Integer selectedCyclerIndex;
	private AmiWebDmTreePortlet owner;
	private FormPortletButton includeDividers;
	private final boolean allowModification;
	private TextMatcher matcher;

	public AmiWebDmHeaderPortlet(AmiWebDmTreePortlet owner, AmiWebHeaderPortlet header, boolean allowModification) {
		this.owner = owner;
		this.owner.getGraph().getGraph().addGraphMovementListener(this);
		this.header = header;
		this.allowModification = allowModification;
		StringBuilder legendHtml = new StringBuilder();
		legendHtml.append("<div style=\"height:100%; width:100%; padding:0px 40px; padding-bottom:40px;\">");
		String legendIconPrefix = "<div style=\"display:inline-flex; position:relative; height:100%; width:20%;\"><div style=\"margin:auto; position:relative;\"><div class=\"";
		String legendIconMiddle = "\"></div><div style=\"width:100%; color:white; text-align:center;\">";
		String legendIconSuffix = "</div></div></div>";
		legendHtml.append(legendIconPrefix + "ami_datamodeler_ds" + legendIconMiddle + "Datasource" + legendIconSuffix);
		legendHtml.append(legendIconPrefix + "ami_datamodeler_dm" + legendIconMiddle + "Datamodel" + legendIconSuffix);
		legendHtml.append(legendIconPrefix + "ami_datamodeler_pt" + legendIconMiddle + "Panel" + legendIconSuffix);
		legendHtml.append(legendIconPrefix + "ami_datamodeler_blender" + legendIconMiddle + "Blender" + legendIconSuffix);
		legendHtml.append(legendIconPrefix + "ami_datamodeler_filter" + legendIconMiddle + "Filter" + legendIconSuffix + "</div>");
		RootPortlet root = (RootPortlet) owner.getService().getPortletManager().getRoot();
		int height = root.getHeight();
		this.header.setLegendWidth(300);
		this.header.updateLegendPortletLayout(legendHtml.toString());
		if (height < 888) {
			int scaledHeight = (int) (height * 0.18);
			this.header.setInformationHeaderHeight(scaledHeight);
			this.header.updateBlurbPortletLayout("AMI Data Modeler", MINIMAL_HELP_HTML);
		} else {
			this.header.setInformationHeaderHeight(200);
			this.header.updateBlurbPortletLayout("AMI Data Modeler", DATAMODELER_HELP_HTML);
		}
		this.header.setSearchHandler(this);
		this.header.getBarFormPortlet().addFormPortletListener(this);
		addDataSource = new FormPortletButton(ATTACH_DATASOURCE);
		includeDividers = new FormPortletButton(SHOW_DIVIDERS);
		FormPortlet bar = header.getBarFormPortlet();
		bar.addButton(addDataSource);
		bar.addButton(includeDividers);
		bar.getFormPortletStyle().setButtonPanelStyle("_cna=ami_header_buttons_panel");
		header.updateBarPortletLayout(addDataSource.getHtmlLayoutSignature() + includeDividers.getHtmlLayoutSignature());
		addDataSource.setCssStyle("_cn=ami_datamodeler_adddatasource");
		updateShowDivButton();
	}

	public void updateShowDivButton() {
		includeDividers.setCssStyle(this.owner.getShowDividers() ? "_cn=ami_datamodeler_show_dividers" : "_cn=ami_datamodeler_hide_dividers");
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (this.allowModification && button == this.addDataSource) {
			header.getManager().showDialog(ATTACH_DATASOURCE, new AmiWebDmAddEditDatasourcePortlet(portlet.generateConfig(), null, false, false));
		} else if (button == this.includeDividers) {
			this.owner.setShowDividers(!owner.getShowDividers());
		}

	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		if (formPortlet == this.header.getSearchFormPortlet() && keycode == 13) {
			String text = header.getSearchInputValue();
			this.owner.focusDatamodelTab();
			if (OH.ne(previousSearch, text)) {
				doSearch();
			} else {
				switch (mask) {
					case 0:
						//Enter
						this.doSearchNext();
						break;
					case 1:
						//Ctrl+Enter
						setSelectedNodes(this.selectedCycler, true);
						ensureVisibleNodes(this.selectedCycler);
						this.selectedCyclerIndex = null;
						updateSearchTitle();
						break;
					case 2:
						//Shift+Enter
						this.doSearchPrevious();
						break;
				}
			}
			//		} else if (field == this.layouts) {
			//			LinkedHashSet<String> selected = layouts.getValue();
			//			this.owner.setLayouts(selected);
		}

	}
	private void initSelectedCycler(Collection<Node> nodes) {
		this.selectedCycler.clear();
		this.selectedCycler.addAll(nodes);
		this.selectedCyclerIndex = null;
	}

	@Override
	public void doSearchNext() {
		clearSearchTitle();
		clearSelected();
		if (this.selectedCycler.size() > 0) {
			if (this.selectedCyclerIndex == null) {
				this.selectedCyclerIndex = 0;
			} else {
				this.selectedCyclerIndex++;
				this.selectedCyclerIndex = MH.mod(this.selectedCyclerIndex, this.selectedCycler.size());
			}
			Node n = this.selectedCycler.get(this.selectedCyclerIndex);
			n.setSelected(true);
			this.ensureVisibleNode(n);
		}
		this.updateSearchTitle();
	}
	@Override
	public void doSearchPrevious() {
		clearSearchTitle();
		clearSelected();
		if (this.selectedCycler.size() > 0) {
			if (this.selectedCyclerIndex == null) {
				this.selectedCyclerIndex = this.selectedCycler.size() - 1;
			} else {
				this.selectedCyclerIndex--;
				this.selectedCyclerIndex = MH.mod(this.selectedCyclerIndex, this.selectedCycler.size());
			}
			Node n = this.selectedCycler.get(this.selectedCyclerIndex);
			n.setSelected(true);
			this.ensureVisibleNode(n);
		}
		this.updateSearchTitle();
	}

	public void doSearch() {
		String text = header.getSearchInputValue();
		clearSearchTitle();
		clearSelected();
		List<Node> result = new ArrayList<Node>();
		if (SH.is(text)) {
			this.matcher = SH.m(text);
			for (Node n : owner.getGraph().getGraph().getNodes()) {
				if (!n.getVisible())
					continue;
				if (matches(n))
					result.add(n);
			}
		} else
			this.matcher = null;
		this.initSelectedCycler(result);
		this.setSelectedNodes(result, true);
		ensureVisibleNodes(result);
		previousSearch = text;
		updateSearchTitle();
	}
	private boolean matches(Node n) {
		if (matcher == null)
			return false;
		AmiWebGraphNode d = AmiWebDmSmartGraph.getData(n);
		if (d == null)
			return false;
		return (matcher.matches(d.getLabel()) || matcher.matches(d.getDescription()));
	}

	public void updateSearchTitle() {
		if (SH.isnt(previousSearch)) {
			header.updateSearchHelperText(null, null);
		} else if (selectedCycler != null) {
			if (selectedCyclerIndex == null) {
				header.updateSearchHelperText(null, selectedCycler.size());
			} else {
				header.updateSearchHelperText(selectedCyclerIndex + 1, selectedCycler.size());
			}
		} else {
			header.updateSearchHelperText(null, null);
		}
	}

	public void clearSearchTitle() {
		header.updateSearchHelperText(null, null);
	}
	private void ensureVisibleNodes(List<Node> nodes) {
		this.owner.getGraph().getGraph().ensureVisibleNodes(nodes);

	}
	private void clearSelected() {
		this.owner.getGraph().getGraph().clearSelected();
	}
	private void setSelectedNodes(List<Node> nodes, boolean select) {
		for (Node n : nodes) {
			n.setSelected(select);
		}

	}
	private void ensureVisibleNode(Node n) {
		this.owner.getGraph().getGraph().ensureVisibleNode(n);
	}

	@Override
	public void onNodeAdded(GraphPortlet graphPortlet, Node node) {
		if (matches(node) && !this.selectedCycler.contains(node)) {
			this.selectedCycler.add(node);
			updateSearchTitle();
		}
	}

	@Override
	public void onNodeUpdated(GraphPortlet graphPortlet, Node node) {
		if (matches(node))
			onNodeAdded(graphPortlet, node);
		else
			onNodeRemoved(graphPortlet, node);
	}

	@Override
	public void onNodeRemoved(GraphPortlet graphPortlet, Node node) {
		int n = this.selectedCycler.indexOf(node);
		if (n != -1) {
			this.selectedCycler.remove(n);
			if (this.selectedCyclerIndex != null && n < this.selectedCyclerIndex)
				this.selectedCyclerIndex--;
			updateSearchTitle();
		}
	}

	@Override
	public void onNodesCleared() {
		this.selectedCyclerIndex = null;
		this.selectedCycler.clear();
		this.updateSearchTitle();
	}

	@Override
	public void onEdgeAdded(GraphPortlet graphPortlet, Edge node) {
	}

	@Override
	public void onEdgeUpdated(GraphPortlet graphPortlet, Edge node) {
	}

	@Override
	public void onEdgeRemoved(GraphPortlet graphPortlet, Edge node) {
	}

	@Override
	public void onNodeMoved(GraphPortlet graphPortlet, int oldX, int oldY, Node node) {
	}

	@Override
	public void onEdgesCleared() {
	}

}
