package com.larkinpoint.salestool;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.base.Day;
import com.f1.base.Row;
import com.f1.http.tag.FormatTextTag;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.table.impl.NumberWebCellFormatter;
import com.f1.suite.web.tree.WebTreeContextMenuFactory;
import com.f1.suite.web.tree.WebTreeContextMenuListener;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.utils.CH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.structs.Tuple2;
import com.sso.messages.SsoGroupAttribute;
import com.sso.messages.SsoUser;
import com.sso.messages.UpdateSsoGroupRequest;
import com.vortex.ssoweb.SsoService;
import com.vortex.ssoweb.SsoWebGroup;

public class LarkinParameterTreePortlet extends FastTreePortlet implements WebTreeContextMenuListener, WebTreeContextMenuFactory, Comparator<WebTreeNode> {

	private LarkinParameters params;
	

	public LarkinParameterTreePortlet(PortletConfig portletConfig, LarkinParameters params) {
		super(portletConfig);
		LocaleFormatter localformatter = getManager().getState().getWebState().getFormatter();
		NumberWebCellFormatter datefmt = new NumberWebCellFormatter(localformatter.getDateFormatter(LocaleFormatter.MMDDYYYY));
		NumberWebCellFormatter numfmt = new NumberWebCellFormatter(localformatter.getNumberFormatter(3));
	
		
		getTree().setContextMenuFactory(this);
		getTree().addMenuContextListener(this);
		getTree().getTreeManager().getRoot().setName("Parameters").setCssClass("LarkinParams");
		getTree().getTreeManager().setComparator(this);
		this.params = new LarkinParameters(params);
		String straddleChoice;
		if (params.getStraddleChoice() == 0)
			straddleChoice = new String("ATM Straddle");
		else if (params.getStraddleChoice() == 1)
			straddleChoice = new String("1st Strangle");
		else
			straddleChoice = new String("2nd Strangle");
		WebTreeNode root = getTree().getTreeManager().getRoot();
		WebTreeNode r = getTree().getTreeManager().createNode("Selected Index: " + params.getSymbolName(), root, true).setCssClass("LarkinParams");
		WebTreeNode ra = getTree().getTreeManager().createNode("Near term strategy: " + straddleChoice, r, true).setCssClass("LarkinDetails");
		WebTreeNode rb = getTree().getTreeManager().createNode("Starting TradeDate: " + datefmt.formatCellToText(params.getStartingQuoteDate()), r, true)
				.setCssClass("LarkinDetails");
		WebTreeNode rc = getTree().getTreeManager().createNode("Ending   TradeDate: " + datefmt.formatCellToText(params.getEndingQuoteDate()), r, true)
				.setCssClass("LarkinDetails");

		WebTreeNode r1 = getTree().getTreeManager().createNode("Straddles", root, true).setCssClass("LarkinParams");
		WebTreeNode r1a = getTree().getTreeManager().createNode("CP ratio: " + numfmt.formatCellToText(params.getStraddleRatio()), r1, true).setCssClass("LarkinDetails");
		WebTreeNode r1b = getTree().getTreeManager().createNode("CP ratio UB: " + numfmt.formatCellToText(params.getStraddleRatioUB()), r1, true).setCssClass("LarkinDetails");
		WebTreeNode r1c = getTree().getTreeManager().createNode("CP ratio LB: " + numfmt.formatCellToText(params.getStraddleRatioLB()), r1, true).setCssClass("LarkinDetails");
		WebTreeNode r1d = getTree().getTreeManager().createNode("Days to expiry UB: " + SH.toString(params.getStraddleDaysUB()), r1, true).setCssClass("LarkinDetails");
		WebTreeNode r1e = getTree().getTreeManager().createNode("Days to expiry LB: " + SH.toString(params.getStraddleDaysLB()), r1, true).setCssClass("LarkinDetails");
		WebTreeNode r1f = getTree().getTreeManager().createNode("Quantity: " + numfmt.formatCellToText(params.getStraddleCount()), r1, true).setCssClass("LarkinDetails");

		WebTreeNode r2 = getTree().getTreeManager().createNode("1st Put Bucket", root, true).setCssClass("LarkinParams");
		WebTreeNode r2a = getTree().getTreeManager().createNode("CP ratio: " + numfmt.formatCellToText(params.getPutBucket1cpRatio()), r2, true).setCssClass("LarkinDetails");
		WebTreeNode r2b = getTree().getTreeManager().createNode("CP ratio UB: " + numfmt.formatCellToText(params.getPutBucket1cpRatioUB()), r2, true).setCssClass("LarkinDetails");
		WebTreeNode r2c = getTree().getTreeManager().createNode("CP ratio LB: " + numfmt.formatCellToText(params.getPutBucket1cpRatioLB()), r2, true).setCssClass("LarkinDetails");
		WebTreeNode r2d = getTree().getTreeManager().createNode("Days to expiry UB: " + SH.toString(params.getPutBucket1DaysUB()), r2, true).setCssClass("LarkinDetails");
		WebTreeNode r2e = getTree().getTreeManager().createNode("Days to expiry LB: " + SH.toString(params.getPutBucket1DaysLB()), r2, true).setCssClass("LarkinDetails");
		WebTreeNode r2f = getTree().getTreeManager().createNode("Quantity: " + numfmt.formatCellToText(params.getPutBucket1Count()), r2, true).setCssClass("LarkinDetails");

		WebTreeNode r3 = getTree().getTreeManager().createNode("2nd Put Bucket", root, true).setCssClass("LarkinParams");
		WebTreeNode r3a = getTree().getTreeManager().createNode("CP ratio: " + numfmt.formatCellToText(params.getPutBucket2cpRatio()), r3, true).setCssClass("LarkinDetails");
		WebTreeNode r3b = getTree().getTreeManager().createNode("CP ratio UB: " + numfmt.formatCellToText(params.getPutBucket2cpRatioUB()), r3, true).setCssClass("LarkinDetails");
		WebTreeNode r3c = getTree().getTreeManager().createNode("CP ratio LB: " + numfmt.formatCellToText(params.getPutBucket2cpRatioLB()), r3, true).setCssClass("LarkinDetails");
		WebTreeNode r3d = getTree().getTreeManager().createNode("Days to expiry UB: " + SH.toString(params.getPutBucket2DaysUB()), r3, true).setCssClass("LarkinDetails");
		WebTreeNode r3e = getTree().getTreeManager().createNode("Days to expiry LB: " + SH.toString(params.getPutBucket2DaysLB()), r3, true).setCssClass("LarkinDetails");
		WebTreeNode r3f = getTree().getTreeManager().createNode("Quantity: " + numfmt.formatCellToText(params.getPutBucket2Count()), r3, true).setCssClass("LarkinDetails");

		WebTreeNode r4 = getTree().getTreeManager().createNode("Investment inputs", root, true).setCssClass("green").setCssClass("LarkinParams");
		;
		WebTreeNode r4a = getTree().getTreeManager().createNode("Management Fee: " + numfmt.formatCellToText(params.getManagementFee()), r4, true).setCssClass("LarkinDetails");
		WebTreeNode r4b = getTree().getTreeManager().createNode("Invested Amount: " + numfmt.formatCellToText(params.getInvestedAmount()), r4, true).setCssClass("LarkinDetails");
		WebTreeNode r4c = getTree().getTreeManager().createNode("Investment Percentage: " + numfmt.formatCellToText(params.getInvestmentPercentage()), r4, true)
				.setCssClass("LarkinDetails");
		WebTreeNode r4d = getTree().getTreeManager().createNode("Underlying Value: " + numfmt.formatCellToText(params.getUnderlyingValue()), r4, true).setCssClass("LarkinDetails");
		WebTreeNode r4e = getTree().getTreeManager().createNode("Investment Value: " + numfmt.formatCellToText(params.getInvestmentValue()), r4, true).setCssClass("LarkinDetails");
		WebTreeNode r4f = getTree().getTreeManager().createNode("Quantity: " + numfmt.formatCellToText(params.getPutBucket2Count()), r4, true).setCssClass("LarkinDetails");

		//	this.UnderlyingValue = params.UnderlyingValue;
		//	this.InvestmentValue = params.InvestmentValue;
		//	this.InvestmentPercentage = params.InvestmentPercentage;
		//	this.InvestedAmount = params.InvestedAmount;
		//	this.ManagementFee = params.ManagementFee;
		//.setData(params).setCssClass("clickable");
		//	r.setName("Straddles");

		// TODO Auto-generated constructor stub
	}
	@Override
	public void onContextMenu(FastWebTree tree, String action) {
		// TODO Auto-generated method stub
		if ("save".equals(action)) {
			{
			//	getManager().showDialog("Save Scenario", titlePortlet);
			
			}
		
		} else if ("save".equals(action)) {
	//		loadParams();
		} else if( "run".equals(action)){
			
		}

	}
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		
	

		
	}
	private void loadParams() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onNodeClicked(FastWebTree tree, WebTreeNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public WebMenu createMenu(FastWebTree fastWebTree, List<WebTreeNode> selected) {
		List<WebMenuItem> children = new ArrayList<WebMenuItem>();
		
		
		children.add(new BasicWebMenuLink("Save Parameters " , true, "save"));
		children.add(new BasicWebMenuLink("Load Parameters " , true, "load"));
		children.add(new BasicWebMenuLink("Rerun Scenario " , true, "run"));
		BasicWebMenu r = new BasicWebMenu("", true, children);
		return r;
		
	}

	@Override
	public boolean formatNode(WebTreeNode node, StringBuilder sink) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public int compare(WebTreeNode o1, WebTreeNode o2) {
		// TODO Auto-generated method stub

		return -1;
	}
	
}
