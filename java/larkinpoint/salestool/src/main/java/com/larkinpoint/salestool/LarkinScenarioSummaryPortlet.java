package com.larkinpoint.salestool;

import java.util.HashMap;
import java.util.Map;

import com.f1.base.Action;
import com.f1.base.Row;
import com.f1.container.ResultMessage;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.utils.CH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.larkinpoint.messages.GetOptionDataResponse;
import com.sso.messages.SsoGroupAttribute;
import com.sso.messages.SsoUser;
import com.sso.messages.UpdateSsoGroupRequest;
import com.sso.messages.UpdateSsoGroupResponse;
import com.vortex.ssoweb.SsoService;
import com.vortex.ssoweb.SsoWebGroup;

public class LarkinScenarioSummaryPortlet extends GridPortlet implements FormPortletListener, WebContextMenuListener, WebContextMenuFactory {
	private DividerPortlet verticalDiv1;
	private DividerPortlet verticalDiv2;
	private DividerPortlet horizontalDiv1;
	private LarkinParameterTreePortlet parametersPortlet;
	
	private FormPortlet titlePortlet; 
	private FormPortletTextField title;
	private FormPortletButton saveButton;

	
	private SsoWebGroup group;
	private SsoUser user;
	private SsoService ssoservice;
	
	private LarkinScenarioResultsPortlet resultsPortlet;
	private LarkinScenarioContext LSC;
	LarkinOptionReportPortlet parent;
	
	public LarkinScenarioSummaryPortlet(PortletConfig config, LarkinScenarioContext LSC,LarkinOptionReportPortlet parent) {
		super(config);
		this.LSC = LSC;
		this.parent = parent;
		this.ssoservice = (SsoService) getManager().getService(SsoService.ID);
		{
			titlePortlet = new FormPortlet(generateConfig());

			titlePortlet.addField(title = new FormPortletTextField("Scenario Title:").setWidth(200).setHeight(100) );
			titlePortlet.addButton(saveButton = new FormPortletButton("Save:"));
			
		}
		
		{
			verticalDiv1 = new DividerPortlet(generateConfig(), true);
			verticalDiv2 = new DividerPortlet(generateConfig(), true);
			horizontalDiv1 = new DividerPortlet(generateConfig(), false);
			parametersPortlet = new LarkinParameterTreePortlet(generateConfig(), this.LSC.getParams());
			resultsPortlet = new LarkinScenarioResultsPortlet(generateConfig(), LSC);
		}
		titlePortlet.addFormPortletListener(this);
	
		{
			verticalDiv1.setSize(100, 100);
			verticalDiv2.setSize(100, 100);
			horizontalDiv1.setSize(100, 100);
			verticalDiv1.setOffset(.2);
			verticalDiv2.setOffset(.3);
			verticalDiv1.setVertical(true);
			horizontalDiv1.setOffset(.9);
			
		}
		
		horizontalDiv1.addChild(parametersPortlet);
		horizontalDiv1.addChild(titlePortlet);
		//verticalDiv1.addChild(titlePortlet);
		
		verticalDiv2.addChild(resultsPortlet);
		verticalDiv1.addChild(horizontalDiv1);
		verticalDiv1.addChild(verticalDiv2);
		addChild(verticalDiv1);
		
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public WebMenu createMenu(WebTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onVisibleRowsChanged(FastWebTable fastWebTable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		// TODO Auto-generated method stub
		if (button == this.saveButton ) {
			String title = this.title.getValue();
			if( title != null){
				LSC.getParams().setTitle(this.title.getValue());
				parent.setTabTitle(title);
				saveParams();
			}
		}
		
	}
	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		
		UpdateSsoGroupResponse action = (UpdateSsoGroupResponse) result.getAction();
		if (!action.getOk())
			getManager().showAlert(action.getMessage());
		
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		// TODO Auto-generated method stub
		
	}
	public void saveParams()	{
		ObjectToJsonConverter converter = new ObjectToJsonConverter();
	//	SsoUser user = getManager().getState().getWebState().getUser();
		LarkinParameters params = LSC.getParams();
		

		
		UpdateSsoGroupRequest updateRequest = getManager().getGenerator().nw(UpdateSsoGroupRequest.class);
		updateRequest.setGroupId(user.getGroupId());
		SsoGroupAttribute attr = getManager().getGenerator().nw(SsoGroupAttribute.class);
		attr.setGroupId(updateRequest.getGroupId());
		SsoWebGroup group = ssoservice.getSsoTree().getGroup(updateRequest.getGroupId());
		if (group == null) {
			getManager().showAlert("Group not found: " + updateRequest.getGroupId());
			return;
		}
	
		
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("Title", params.getTitle());
		m.put("SymbolName", params.getSymbolName());
		m.put("StartingQuoteDate", params.getStartingQuoteDate().toStringNoTimeZone());
		m.put("EndingQuoteDate", params.getEndingQuoteDate().toStringNoTimeZone());
		m.put("StraddleChoice", params.getStraddleChoice());
		
		m.put("UnderlyingValue", params.getUnderlyingValue());
		m.put("InvestmentValue", params.getInvestmentValue());
		m.put("InvestmentPercentage", params.getInvestmentPercentage());
		m.put("InvestedAmount", params.getInvestedAmount());
		m.put("ManagementFee", params.getManagementFee());
	
		m.put("straddleDaysUB", params.getStraddleDaysUB());
		m.put("straddleDaysLB", params.getStraddleDaysLB());
		m.put("straddleRatioUB", params.getStraddleRatioUB());
		m.put("straddleRatioLB", params.getStraddleRatioLB());
		m.put("straddleRatio", params.getStraddleRatio());
		m.put("straddleCount", params.getStraddleCount());

		m.put("putBucket1DaysUB", params.getPutBucket1DaysUB());
		m.put("putBucket1DaysLB", params.getPutBucket1DaysLB());
		m.put("putBucket1cpRatio", params.getPutBucket1cpRatio());
		m.put("putBucket1cpRatioUB", params.getPutBucket1cpRatioUB());
		m.put("putBucket1cpRatioLB", params.getPutBucket1cpRatioLB());
		m.put("putBucket1Count", params.getPutBucket1Count());
	
		m.put("putBucket2DaysUB", params.getPutBucket2DaysUB());
		m.put("putBucket2DaysLB", params.getPutBucket2DaysLB());
		m.put("putBucket2cpRatio", params.getPutBucket2cpRatio());
		m.put("putBucket2cpRatioUB", params.getPutBucket2cpRatioUB());
		m.put("putBucket2cpRatioLB", params.getPutBucket2cpRatioLB());
		m.put("putBucket2Count", params.getPutBucket2Count());
		
		
		
		attr.setKey("larkin_scenarios");
		attr.setType(SsoGroupAttribute.TYPE_JSON);
		attr.setValue(converter.objectToString(m));
		updateRequest.setGroupAttributes(CH.l(attr));
		ssoservice.sendRequestToBackend(getPortletId(), updateRequest);
		
	
	}

}
