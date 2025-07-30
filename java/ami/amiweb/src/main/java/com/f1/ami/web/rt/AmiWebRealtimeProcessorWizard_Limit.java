package com.f1.ami.web.rt;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.AmiWebManagers;
import com.f1.ami.web.AmiWebMenuUtils;
import com.f1.ami.web.AmiWebRealtimeObjectManager;
import com.f1.ami.web.AmiWebRealtimeProcessor;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.CH;
import com.f1.utils.SH;

public class AmiWebRealtimeProcessorWizard_Limit extends GridPortlet implements FormPortletListener, FormPortletContextMenuListener, FormPortletContextMenuFactory {

	FormPortletTextField orderByField;
	FormPortletSelectField<Boolean> ascendingField;
	FormPortletNumericRangeField countField;
	FormPortletNumericRangeField offsetField;
	private AmiWebService service;
	private FormPortlet form;
	private AmiWebRealtimeObjectManager lowerRealtime;
	private com.f1.base.CalcTypes schema;
	private FormPortletButton cancelButton;
	private FormPortletButton submitButton;
	private FormPortletTextField nameField;
	private FormPortletSelectField<String> aliasField;
	private String lowerId;
	private AmiWebRealtimeProcessor_Limit target;

	public AmiWebRealtimeProcessorWizard_Limit(PortletConfig config, AmiWebRealtimeProcessor target) {
		this(config, CH.first(target.getLowerRealtimeIds()));
		this.target = (AmiWebRealtimeProcessor_Limit) target;
		this.orderByField.setValue(this.target.getOrderBy(false));
		this.ascendingField.setValue(this.target.getAscending(false));
		this.offsetField.setValue(this.target.getOffset(false));
		this.countField.setValue(this.target.getCount(false));
		this.nameField.setValue(this.target.getName());
		this.aliasField.setValue(this.target.getAlias());
	}
	protected AmiWebRealtimeProcessorWizard_Limit(PortletConfig config, String lowerId) {
		super(config);
		this.service = AmiWebUtils.getService(this.getManager());
		this.lowerId = lowerId;
		this.lowerRealtime = service.getWebManagers().getAmiObjectsByType(lowerId);
		this.schema = lowerRealtime.getRealtimeObjectsOutputSchema();
		this.form = new FormPortlet(generateConfig());
		this.nameField = new FormPortletTextField("Processor Id");
		Set<String> names = new HashSet<String>();
		for (AmiWebRealtimeProcessor i : service.getWebManagers().getRealtimeProcessors())
			names.add(SH.afterFirst(i.getRealtimeId(), ':'));
		String name = SH.getNextId("Limit", names, 2);
		this.nameField.setValue(name);
		this.aliasField = new FormPortletSelectField<String>(String.class, "Owning Layout");
		Set<String> t = new HashSet<String>();
		for (String s : this.service.getLayoutFilesManager().getAvailableAliasesDown(""))
			this.aliasField.addOption(s, AmiWebUtils.formatLayoutAlias(s));
		this.form.addField(this.nameField);
		this.form.addField(this.aliasField);

		this.orderByField = this.form.addField(new FormPortletTextField("Order By:"));
		this.orderByField.setHasButton(true);
		this.orderByField.setWidth(FormPortletField.WIDTH_STRETCH);
		this.ascendingField = this.form.addField(new FormPortletSelectField<Boolean>(Boolean.class, "Direction:"));
		this.ascendingField.addOption(Boolean.TRUE, "Ascending");
		this.ascendingField.addOption(Boolean.FALSE, "Desending");
		this.offsetField = this.form.addField(new FormPortletNumericRangeField("Offset:").setNullable(false).setValue(0));
		this.countField = this.form.addField(new FormPortletNumericRangeField("Count:").setNullable(false).setValue(100));
		this.offsetField.setSliderHidden(true);
		this.countField.setSliderHidden(true);
		this.offsetField.setDecimals(0);
		this.countField.setDecimals(0);
		this.offsetField.setRange(0, 10000000);
		this.countField.setRange(0, 10000000);
		form.addButton(this.cancelButton = new FormPortletButton("Cancel"));
		form.addButton(this.submitButton = new FormPortletButton("Submit"));
		this.form.addFormPortletListener(this);
		this.form.addMenuListener(this);
		this.form.setMenuFactory(this);
		this.addChild(this.form);
		setSuggestedSize(800, 500);
	}

	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		WebMenu r = new BasicWebMenu();
		if (field == this.orderByField) {
			WebMenu r2 = new BasicWebMenu("variables", true);
			for (String i : this.schema.getVarKeys())
				r2.add(new BasicWebMenuLink(i, true, "var_" + i));
			r.add(r2);
			AmiWebMenuUtils.createOperatorsMenu(r, service, "");
		}
		return r;
	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		AmiWebService service = this.service;
		AmiWebMenuUtils.processContextMenuAction(service, action, node);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.cancelButton)
			this.close();
		else if (button == this.submitButton) {
			String alias = aliasField.getValue();
			if (!SH.startsWith(lowerId, AmiWebManagers.FEED)) {
				String adn = AmiWebUtils.ari2adn(lowerId);
				if (!AmiWebUtils.isParentAliasOrSame(alias, adn)) {
					getManager().showAlert("Processor can not be in " + adn + " layout, because it would not have visibility to: " + lowerId);
					return;
				}
			}

			String name = SH.trim(this.nameField.getValue());
			if (SH.isnt(this.orderByField.getValue())) {
				getManager().showAlert("Field 'Order By' Required");
				return;
			}
			if (!AmiUtils.isValidVariableName(name, false, false, false)) {
				getManager().showAlert("Invalid Processor name");
				return;
			}

			String adn = AmiWebUtils.getFullAlias(alias, name);
			String currentName = null;
			if (target != null)
				currentName = AmiWebManagers.PROCESSOR + target.getName();
			HashSet<String> toCheckNames = new HashSet<>();
			toCheckNames.addAll(this.service.getWebManagers().getAllTableTypes(""));

			if (currentName != null && toCheckNames.contains(currentName))
				toCheckNames.remove(currentName);

			if (toCheckNames.contains(AmiWebManagers.PROCESSOR + adn)) {
				getManager().showAlert("Processor name already exists");
				return;
			}

			if (this.target != null) {
				Exception exception = this.target.testOrderBy(this.orderByField.getValue());
				if (exception != null) {
					getManager().showAlert("Order By Fomula is invalid: " + exception.getMessage(), exception);
					return;
				}
				this.target.setAdn(adn);
				this.target.setOrderBy(this.orderByField.getValue(), false);
				this.target.setCount(this.countField.getIntValue(), false);
				this.target.setOffset(this.offsetField.getIntValue(), false);
				this.target.setAscending(this.ascendingField.getValue(), false);
				this.target.rebuild();
			} else {
				AmiWebRealtimeProcessor_Limit p = new AmiWebRealtimeProcessor_Limit(service, alias);
				p.setLowerId(this.lowerRealtime.getRealtimeId());
				p.setAdn(adn);
				Exception exception = p.testOrderBy(this.orderByField.getValue());
				if (exception != null) {
					getManager().showAlert("Order By Fomula is invalid: " + exception.getMessage(), exception);
					return;
				}
				p.setCount(this.countField.getIntValue(), false);
				p.setOffset(this.offsetField.getIntValue(), false);
				p.setOrderBy(this.orderByField.getValue(), false);
				p.setAscending(this.ascendingField.getValue(), false);
				p.rebuild();
				service.getWebManagers().addProcessor(p);
			}
			close();
		}

	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

}
