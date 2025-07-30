package com.f1.omsweb;

import java.util.HashMap;
import java.util.Map;

import com.f1.base.Legible;
import com.f1.base.ToStringable;
import com.f1.pofo.fix.FixOrderInfo;
import com.f1.pofo.fix.FixOrderRequest;
import com.f1.pofo.fix.MsgType;
import com.f1.pofo.oms.OrderType;
import com.f1.pofo.oms.Side;
import com.f1.pofo.oms.TimeInForce;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletFileUploadField;
import com.f1.utils.GuidHelper;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class NewBasketFormPortlet extends FormPortlet implements ToStringable, Legible {

	/**
	 * service for submitting new order
	 */
	private OrdersService service;
	private FormPortletButton submitButton;

	private FormPortletFileUploadField fileUpload;

	public NewBasketFormPortlet(PortletConfig config) {
		super(config);
		addField(this.fileUpload = new FormPortletFileUploadField("File"));
		addButton(this.submitButton = new FormPortletButton("Enter Order(s)"));

		service = (OrdersService) getManager().getService(OrdersService.ID);

	}

	public static class Builder extends AbstractPortletBuilder<NewBasketFormPortlet> {

		public static final String ID = "newBasketForm";

		public Builder() {
			super(NewBasketFormPortlet.class);
		}

		@Override
		public NewBasketFormPortlet buildPortlet(PortletConfig portletConfig) {
			return new NewBasketFormPortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Basket File Upload";
		}
		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	protected void onUserPressedButton(FormPortletButton button) {
		if (button == submitButton) {
			String lines[] = SH.splitLines(this.fileUpload.getData());
			for (String line : lines) {
				if (SH.is(line))
					this.service.createOrder(createNewOrderMsg(line));
			}
		} else
			super.onUserPressedButton(button);
	}

	public FixOrderRequest createNewOrderMsg(String line) {
		Map<String, String> parts = SH.splitToMapWithTrim('|', '=', line.trim());
		String data = this.fileUpload.getData();
		Map<Integer, String> passThruTags = new HashMap<Integer, String>();
		String guid = GuidHelper.getGuid();
		FixOrderRequest msg = createFixOrderRequest();
		FixOrderInfo info = msg.getOrderInfo();

		for (Map.Entry<String, String> e : parts.entrySet()) {
			final String key = e.getKey(), value = e.getValue();
			if (OH.eq(key, "account")) {
				info.setAccount(value);
			} else if (OH.eq(key, "symbol")) {
				msg.setSymbol(value);
			} else if (OH.eq(key, "side")) {
				info.setSide(value.toLowerCase().startsWith("b") ? Side.BUY : Side.SELL);
			} else if (OH.eq(key, "quantity")) {
				info.setOrderQty(SH.parseInt(value));
			} else if (OH.isBetween(key.charAt(0), '0', '9')) {
				passThruTags.put(SH.parseInt(key), value);
			}
		}

		msg.setType(MsgType.NEW_ORDER_SINGLE);
		msg.setRequestId(guid);
		msg.setSessionName("CLIENT1");
		info.setOrderType(OrderType.MARKET);
		info.setTimeInForce(TimeInForce.DAY);
		info.setPassThruTags(passThruTags);

		msg.setOrderInfo(info);
		msg.setRootOrderId(guid);
		return msg;
	}
	private FixOrderRequest createFixOrderRequest() {
		FixOrderRequest msg = getManager().getGenerator().nw(FixOrderRequest.class);
		FixOrderInfo info = getManager().getGenerator().nw(FixOrderInfo.class);
		msg.setOrderInfo(info);
		return msg;
	}

	@Override
	public void onFieldChanged(FormPortletField field) {
		super.onFieldChanged(field);
		if (field == this.fileUpload) {
			String data = this.fileUpload.getData();
			this.submitButton.setName("Enter " + SH.splitLines(data).length + " Order(s)");
		}
	}

	@Override
	public String toLegibleString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		// TODO Auto-generated method stub
		return null;
	}
}
