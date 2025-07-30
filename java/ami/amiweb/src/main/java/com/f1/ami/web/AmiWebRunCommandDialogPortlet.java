package com.f1.ami.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiCenterPassToRelayRequest;
import com.f1.ami.amicommon.msg.AmiCenterPassToRelayResponse;
import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandResponse;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.base.Action;
import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.container.ResultMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletErrorField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletFieldVisibilityController;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.FormPortletValidator;
import com.f1.suite.web.portal.impl.form.FormPortletValidator.FormPortletValidatorListener;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.StringFormatException;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.string.ExpressionParserException;

public class AmiWebRunCommandDialogPortlet extends GridPortlet implements FormPortletListener, FormPortletValidatorListener, AmiWebLockedPermissiblePortlet {

	private static final String CANCEL = "CANCEL";
	private static final String OK = "OK";
	private static final Logger log = LH.get();
	private static final long DEFAULT_TIMEOUT = 120 * 1000;
	private AmiWebObject[] targets;
	private Row[] targetRows;
	private AmiWebService service;
	private FormPortletNumericRangeField timeoutField;
	private AmiCenterPassToRelayRequest req;
	private AmiWebCommandWrapper command;
	private long timeout;
	private FormPortlet fp;
	private List<FormPortletField<?>> allFields = new ArrayList<FormPortletField<?>>();
	private AmiWebAbstractTablePortlet portlet;
	private FormPortletValidator fpv;
	private FormPortletFieldVisibilityController fvc;
	private boolean shouldAutoSend;
	private List<FormPortletButton> buttons = new ArrayList<FormPortletButton>();
	private boolean shouldHaveCloseButton = true;

	public AmiWebRunCommandDialogPortlet(PortletConfig config, AmiWebObject[] targets, Row[] targetRows, AmiWebAbstractTablePortlet portlet) {
		super(config);
		service = (AmiWebService) getManager().getService(AmiWebService.ID);
		this.portlet = portlet;
		this.targets = targets;
		this.targetRows = targetRows;
		this.fp = new FormPortlet(generateConfig());
		this.fp.setStyle(service.getUserFormStyleManager());
		this.fvc = new FormPortletFieldVisibilityController(fp);
		this.fpv = new FormPortletValidator(fp);
		this.fpv.setListener(this);

		fp.addFormPortletListener(this);
	}

	public boolean initFromCommand(AmiWebCommandWrapper cmd) {
		if (this.command != null)
			throw new IllegalStateException();
		this.command = cmd;
		Integer width = null;
		Integer height = null;
		Integer labelWidth = null;
		int fieldsHeight = 0;
		String args = cmd.getArguments();
		if (SH.is(args)) {
			Map jsonRoot;
			try {
				ObjectToJsonConverter converter = getManager().getJsonConverter();
				Object object;
				try {
					object = converter.stringToObject(args);
				} catch (StringFormatException e) {
					throw new ExpressionParserException(-1, "Command argument (A) param is not valid json", e);
				}
				if (object instanceof Map)
					jsonRoot = (Map) object;
				else
					throw new ExpressionParserException(-1, "top level of json structure must be a map");
				Map jsonForm = (Map) CH.getOr(Caster_Simple.OBJECT, jsonRoot, "form", null);
				Long jsonTimeout = CH.getOr(Caster_Long.INSTANCE, jsonRoot, "timeout", null);
				boolean allHidden = true;
				if (jsonForm != null) {
					List jsonInputs = (List) CH.getOr(Caster_Simple.OBJECT, jsonForm, "inputs", null);
					if (jsonInputs != null) {
						int cnt = 0;
						for (Object o : jsonInputs) {
							cnt++;
							Map<String, Object> jsonInputsEntry = (Map<String, Object>) Caster_Simple.OBJECT.cast(o, true, "inputs entry");
							String jsonLabel = getOrThrow(Caster_String.INSTANCE, jsonInputsEntry, "label", "input #" + cnt);
							String jsonVar = getOrThrow(Caster_String.INSTANCE, jsonInputsEntry, "var", "input #" + cnt);
							boolean jsonRequired = CH.getOr(Caster_Boolean.INSTANCE, jsonInputsEntry, "required", Boolean.FALSE);
							String jsonType = CH.getOr(Caster_String.INSTANCE, jsonInputsEntry, "type", "text");
							String regex = CH.getOr(Caster_String.INSTANCE, jsonInputsEntry, "pattern", null);
							String value = CH.getOr(Caster_String.INSTANCE, jsonInputsEntry, "value", null);
							allHidden = allHidden && "hidden".equals(jsonType);
							int curHeight = addField(jsonVar, regex, value, jsonRequired, jsonLabel, jsonType, jsonInputsEntry).getSuggestedHeight();
							fieldsHeight += curHeight == FormPortletField.SIZE_DEFAULT ? this.service.getUserFormStyleManager().getDefaultFormFieldHeight() : curHeight;
						}
					}
					width = CH.getOr(Caster_Integer.INSTANCE, jsonForm, "width", null);
					height = CH.getOr(Caster_Integer.INSTANCE, jsonForm, "height", null);
					labelWidth = CH.getOr(Caster_Integer.INSTANCE, jsonForm, "labelWidth", null);
					List jsonButtons = (List) CH.getOr(Caster_Simple.OBJECT, jsonForm, "buttons", null);
					if (jsonButtons != null) {
						int cnt = 0;
						for (Object o : jsonButtons) {
							cnt++;
							Map jsonInputsEntry = (Map) Caster_Simple.OBJECT.cast(o, true, "buttons entry");
							String jsonType = getOrThrow(Caster_String.INSTANCE, jsonInputsEntry, "type", "button #" + cnt);
							String jsonLabel = CH.getOr(Caster_String.INSTANCE, jsonInputsEntry, "label", jsonType);
							if ("submit".equals(jsonType)) {
								buttons.add(new FormPortletButton(jsonLabel).setId(OK));
							} else if ("cancel".equals(jsonType)) {
								buttons.add(new FormPortletButton(jsonLabel).setId(CANCEL));
							} else
								throw new ExpressionParserException(-1, "Invalid type for button #" + cnt + ": " + jsonType);
							allHidden = false;
						}
					}

					//validations
					List jsonValidations = (List) CH.getOr(Caster_Simple.OBJECT, jsonForm, "validations", null);
					if (jsonValidations != null)
						for (Object o : jsonValidations) {
							Map jsonValidationEntry = (Map) Caster_Simple.OBJECT.cast(o, true, "validation entry");
							String jsonClause = getOrThrow(Caster_String.INSTANCE, jsonValidationEntry, "clause", "validation");
							String jsonReason = getOrThrow(Caster_String.INSTANCE, jsonValidationEntry, "reason", "validation");

							this.fpv.add(jsonClause, jsonReason);
						}

					this.fpv.compile();
					this.fvc.compile();
				}
				Map dialogForm = (Map) CH.getOr(Caster_Simple.OBJECT, jsonRoot, "dialog", null);
				if (dialogForm != null) {
					String dialogClosable = CH.getOr(Caster_String.INSTANCE, dialogForm, "closable", null);
					if ("false".equals(dialogClosable))
						shouldHaveCloseButton = false;
				}

				if (jsonTimeout != null) {
					this.timeout = jsonTimeout.longValue();
					this.timeoutField = null;
				} else {
					this.timeout = -1L;
					this.timeoutField = fp.addField(new FormPortletNumericRangeField("Timeout(sec)")).setRange(1, 600).setValue(30);
				}

				if (allHidden) {
					if (jsonTimeout == null) {
						this.timeout = DEFAULT_TIMEOUT;
					}
					shouldAutoSend = true;
				}
			} catch (ExpressionParserException e) {
				final String ticket = generateErrorTicket();
				LH.warning(log, ticket, " - Invalid Form definition, for command '", cmd.getId(), "': ", args, e);
				getManager().showAlert(e.getMessage() + "<br>&nbsp;<br>&nbsp;<br>(Reference Ticket " + ticket + ")", e);
				return false;
			} catch (Exception e) {
				final String ticket = generateErrorTicket();
				LH.warning(log, ticket, " - Invalid Form definition, for command '", cmd.getId(), "': ", args, e);
				getManager().showAlert("Bad form definition, reference ticket " + ticket, e);
				return false;
			}
		} else {
			this.timeout = DEFAULT_TIMEOUT;
			this.shouldAutoSend = true;
		}
		if (buttons.isEmpty()) {
			buttons.add(new FormPortletButton("Submit").setId(OK));
			buttons.add(new FormPortletButton("Cancel").setId(CANCEL));
		}
		for (FormPortletButton button : buttons)
			fp.addButton(button);
		if (SH.is(command.getHelp())) {
			HtmlPortlet header = new HtmlPortlet(generateConfig(), "<center>" + AmiUtils.s(SH.noNull(command.getHelp())));
			fieldsHeight += 50;
			addChild(header, 0, 0);
			addChild(fp, 0, 1);
			setRowSize(0, 50);
			shouldAutoSend = false;
		} else {
			HtmlPortlet header = new HtmlPortlet(generateConfig());
			fieldsHeight += 50;
			addChild(header, 0, 0);
			addChild(fp, 0, 1);
			setRowSize(0, 1);
		}
		if (width == null)
			width = 600;
		if (height == null)
			height = 100 + fieldsHeight + (allFields.size() - 1 * 5);
		if (labelWidth == null)
			labelWidth = 100;
		setSuggestedSize(width, height);
		fp.getFormPortletStyle().setLabelsWidth(labelWidth);
		getManager().onPortletAdded(this);

		return true;
	}
	private <T> T getOrThrow(Class<T> class1, Map<String, Object> map, String key, String description) {
		try {
			return CH.getOrThrow(class1, map, key);
		} catch (Exception e) {
			throw new ExpressionParserException(-1, "'" + key + "' missing for " + description, e);
		}
	}
	private <T> T getOrThrow(Caster<T> caster, Map<String, Object> map, String key, String description) {
		try {
			return CH.getOrThrow(caster, map, key);
		} catch (Exception e) {
			throw new ExpressionParserException(-1, "'" + key + "' missing for " + description, e);
		}
	}

	public boolean shouldAutoSend() {
		return this.shouldAutoSend;
	}

	public static class FormField {
		final public String var;
		final public Pattern regex;
		final public boolean required;
		final public String value;
		final public String type;

		public FormField(String var, Pattern regex, String value, boolean required, String type) {
			this.var = var;
			this.regex = regex;
			this.required = required;
			this.value = value;
			this.type = type;
		}

		public boolean isHidden() {
			return "hidden".equals(type);
		}

	}

	private FormPortletField<?> addField(String var, String regex, String value, boolean required, String label, String type, Map jsonInputsEntry) {
		FormField formField;
		try {
			formField = new FormField(var, regex == null ? null : Pattern.compile(regex), value, required, type);
		} catch (Exception e) {
			throw new RuntimeException("Could not parse regex for field " + var + ": " + regex, e);
		}

		FormPortletField field;
		Caster<Map> mapCaster = OH.getCaster(Map.class);
		if ("hidden".equals(type))
			field = new FormPortletErrorField<String>(new FormPortletTextField(label)).setCorrelationData(formField);
		else if ("textarea".equals(type))
			field = this.fp.addField(new FormPortletErrorField<String>(new FormPortletTextAreaField(label)).setCorrelationData(formField));
		else if ("text".equals(type)) {
			FormPortletTextField tfield = new FormPortletTextField(label);
			tfield.setCorrelationData(formField);
			Integer width = CH.getOr(Caster_Integer.INSTANCE, jsonInputsEntry, "width", null);
			if (width != null)
				tfield.setWidth(width);
			field = this.fp.addField(new FormPortletErrorField<String>(tfield));
		} else if ("password".equals(type))
			field = this.fp.addField(new FormPortletErrorField<String>(new FormPortletTextField(label).setPassword(true)).setCorrelationData(formField));
		else if ("select".equals(type)) {
			FormPortletSelectField<String> options = new FormPortletSelectField<String>(String.class, label).setCorrelationData(formField);
			List jsonOptions = (List) getOrThrow(Caster_Simple.OBJECT, jsonInputsEntry, "options", "select input");
			if (!required)
				options.addOption(null, "");
			for (Object o : jsonOptions) {
				Map jsonOptionsEntry = mapCaster.cast(o, true, "options entry");
				Object jsonValue = CH.getOrThrow(jsonOptionsEntry, "value");
				String jsonText = CH.getOr(jsonOptionsEntry, "text", SH.toString(jsonValue));
				options.addOption(Caster_String.INSTANCE.cast(jsonValue), jsonText);
			}
			field = this.fp.addField(options);
		} else if ("checkbox".equals(type)) {
			field = fp.addField(new FormPortletErrorField<Boolean>(new FormPortletCheckboxField(label)).setCorrelationData(formField));
		} else if ("title".equals(type)) {
			field = fp.addField(new FormPortletTitleField(label).setCorrelationData(formField));
			return field;
		} else
			throw new ExpressionParserException(-1, "Invalid 'type' for input: <B>" + type + "</B>");

		String help = CH.getOr(Caster_String.INSTANCE, jsonInputsEntry, "help", null);
		field.setHelp(help);

		//visibility
		String visibleClause = CH.getOr(Caster_String.INSTANCE, jsonInputsEntry, "visible", null);
		this.fvc.add(field, visibleClause);

		//get field validations
		List jsonValidations = (List) CH.getOr(Caster_Simple.OBJECT, jsonInputsEntry, "validations", null);
		if (jsonValidations != null)
			for (Object o : jsonValidations) {
				Map jsonValidationEntry = mapCaster.cast(o, true, "validation entry");
				String jsonClause = CH.getOrThrow(Caster_String.INSTANCE, jsonValidationEntry, "clause");
				String jsonReason = CH.getOrThrow(Caster_String.INSTANCE, jsonValidationEntry, "reason");

				this.fpv.add(field, jsonClause, jsonReason);
			}

		//enabled?
		boolean enabled = (boolean) CH.getOr(jsonInputsEntry, "enabled", true);
		field.setDisabled(!enabled);

		setFieldValueNoThrow(field, value);
		field.setName(var);

		this.allFields.add(field);
		return field;
	}
	private <T> void setFieldValueNoThrow(FormPortletField<T> field, T val) {
		if (val == null)
			return;

		T val2 = field.getCaster().cast(val);
		if (field instanceof FormPortletSelectField)
			((FormPortletSelectField<T>) field).setValueNoThrow(val2);
		else if (field instanceof FormPortletErrorField && ((FormPortletErrorField) field).getField() instanceof FormPortletSelectField) {
			((FormPortletSelectField) ((FormPortletErrorField) field).getField()).setValueNoThrow(val2);
		} else
			field.setValue(val2);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (CANCEL.equals(button.getId())) {
			close();
			return;
		}
		if (req != null)
			return;
		if (OK.equals(button.getId()))
			sendRequest();

	}

	public void sendRequest() {
		Map<String, Object> arguments = getArguments(false, true);
		if (arguments == null)
			return;

		int to = timeout == -1 ? (timeoutField.getIntValue() * 1000) : (int) timeout;
		service.sendCommandToBackEnd(getPortletId(), this.command, arguments, to, getFieldValues(), targets, targetRows);

		this.fp.clearButtons();
		this.fp.addButton(new FormPortletButton("Waiting for Response...")).setEnabled(false);
	}
	/**
	 * 
	 * @param hiddenOnly
	 * @return ... will return null if validation fails
	 */
	private Map<String, Object> getArguments(boolean hiddenOnly, boolean validate) {
		Map<String, Object> arguments = new HashMap<String, Object>(allFields.size());

		for (FormPortletField<?> i : this.allFields) {
			FormField ff = (FormField) i.getCorrelationData();
			if (hiddenOnly && !ff.isHidden())
				continue; //skip

			Object value = i.getValue();
			if (validate && ff.required && SH.isnt(value)) {
				getManager().showAlert(i.getTitle() + " is required");
				return null;
			} else if (validate && ff.regex != null && SH.is(value) && !ff.regex.matcher(AmiUtils.s(value)).matches()) {
				getManager().showAlert(i.getTitle() + " is invalid");
				return null;
			}
			arguments.put(ff.var, value);
		}

		return arguments;
	}

	/**
	 * gets fields from selected records...one map per record
	 * 
	 * @return
	 */
	private List<Map<String, Object>> getFieldValues() {
		if (AH.isEmpty(targets) || AH.isEmpty(command.getFields()))
			return null;

		ArrayList r = new ArrayList<Map<String, Object>>(targets.length);
		for (int i = 0; i < targets.length; i++) {
			AmiWebObject target = targets[i];
			HashMap<String, Object> m = new HashMap<String, Object>();
			r.add(m);
			if (target != null) {
				for (String f : command.getFields())
					m.put(f, target.get(f));
			} else {
				Row row = this.targetRows[i];
				for (String f : command.getFields())
					if (row.getType(f) != null)
						m.put(f, row.get(f));
			}
		}

		return r;
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		AmiCenterPassToRelayResponse res = (AmiCenterPassToRelayResponse) result.getAction();
		if (!res.getOk()) {
			if (res.getAgentResponse() != null && SH.is(res.getAgentResponse().getMessage())) {
				getManager().showAlert(res.getAgentResponse().getMessage(), res.getException());
			} else if (res.getMessage() != null)
				getManager().showAlert(res.getMessage(), res.getException());
			else
				getManager().showAlert("Unknown error", res.getException());
			close();
			return;
		}
		AmiRelayRunAmiCommandResponse ares = (AmiRelayRunAmiCommandResponse) res.getAgentResponse();
		if (!ares.getOk()) {
			getManager().showAlert(ares.getMessage(), res.getException());
		} else {
			int code = ares.getStatusCode();
			if (code == AmiRelayRunAmiCommandResponse.STATUS_UPDATE_RECORD && ares.getOk() && /*!this.portlet.isRealtime() &&*/this.targetRows != null) {

				Map<String, Object> sink = new HashMap<String, Object>();
				if (ares.getParams() != null)
					sink.putAll(ares.getParams());
				AmiWebAbstractTablePortlet table = (AmiWebAbstractTablePortlet) this.portlet;//TODO: portlet can be null will through NPE
				Integer index = null;
				index = (Integer) sink.get("index");

				if (index == null && targetRows.length == 1) //single record usecase
					index = 0;

				if (index != null && index >= 0 && index < targetRows.length) {
					Row r = targetRows[index];

					for (Column e : table.getVariableColumns()) {
						if (!sink.containsKey(e.getId()))
							continue;
						int location = e.getLocation();
						Object value = sink.get(e.getId());
						try {
							value = table.getTable().getTable().getColumnAt(location).getTypeCaster().cast(value);
						} catch (Exception e2) {
							value = null;
						}
						for (Row row : this.targetRows)
							row.putAt(location, value);
					}

					sink.clear();
				}
			}
			if (SH.is(ares.getAmiScript())) {
				String layoutAlias = this.portlet == null ? "" : this.portlet.getAmiLayoutFullAlias();
				service.getScriptManager(layoutAlias).parseAndExecuteAmiScript(ares.getAmiScript(), null, null, this.service.getDebugManager(), AmiDebugMessage.TYPE_CMD_RESPONSE,
						this.portlet, this.command.getCmdId());
			}
			if (SH.is(ares.getAmiMessage()))
				getManager().showAlert(ares.getAmiMessage(), res.getException());
			if (ares.getStatusCode() == AmiRelayRunAmiCommandResponse.STATUS_DONT_CLOSE_DIALOG) {
				fp.clearButtons();
				for (FormPortletButton button : this.buttons)
					fp.addButton(button);
				this.req = null;
				return;//don't close
			}
		}
		close();
		super.onBackendResponse(result);
	}
	@Override
	public void setValid(FormPortletField<?> field, boolean valid, List<String> reasons) {
		LH.fine(log, "field ", field.getTitle(), " valid:", valid, " reasons:", reasons);
	}
	@Override
	public void setValid(boolean valid, List<String> reasons) {
		LH.fine(log, "form ", " valid:", valid, " reasons:", reasons);
	}

	public boolean shouldHaveCloseButton() {
		return shouldHaveCloseButton;
	}
}
