package com.vortex.web.portlet.forms;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;
import com.f1.vortexcommon.msg.agent.VortexMetadatable;
import com.f1.vortexcommon.msg.eye.VortexEyeMetadataField;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRequest;
import com.vortex.client.VortexClientMetadataField;
import com.vortex.web.VortexWebEyeService;

public class VortexWebMetadataFormPortlet extends VortexWebCommentFormPortlet {

	final private Map<String, Tuple2<FormPortletField<?>, VortexClientMetadataField>> fieldsByKeyCode = new HashMap<String, Tuple2<FormPortletField<?>, VortexClientMetadataField>>();
	public VortexWebMetadataFormPortlet(PortletConfig config, String imgUrl) {
		super(config, config.getPortletId(), (VortexEyeRequest) null, null, imgUrl);
	}
	public void initMetadataFormFields(byte type) {
		VortexWebEyeService service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		boolean first = true;
		for (VortexClientMetadataField field : service.getAgentManager().getMetadataFields()) {
			if (field.appliesTo(type)) {
				if (first) {
					addField(new FormPortletTitleField("Custom Metadata"));
					first = false;
				}
				FormPortletField<?> f = toField(field, null);
				f.setTitle("[ " + f.getTitle() + " ] ");
				//md.get(field.getData().getKeyCode()));
				fieldsByKeyCode.put(field.getData().getKeyCode(), new Tuple2<FormPortletField<?>, VortexClientMetadataField>(f, field));
				addField(f);
			}
		}

	}

	public void populateMetadataFormFields(VortexMetadatable metadata) {
		VortexWebEyeService service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		Map<String, String> md = metadata.getMetadata();
		for (Entry<String, Tuple2<FormPortletField<?>, VortexClientMetadataField>> e : fieldsByKeyCode.entrySet()) {
			VortexClientMetadataField field = e.getValue().getB();
			populateField(e.getValue().getB().getData(), md == null ? null : md.get(e.getKey()), (FormPortletField<Object>) e.getValue().getA());
		}
	}

	public boolean populateMetadata(VortexMetadatable exp) {
		Map<String, String> md = generateMetadata();
		if (md == null)
			return false;
		exp.setMetadata(md);
		return true;
	}
	public Map<String, String> generateMetadata() {
		Map<String, String> metadata = new HashMap<String, String>();
		for (Entry<String, Tuple2<FormPortletField<?>, VortexClientMetadataField>> f : this.fieldsByKeyCode.entrySet()) {
			FormPortletField<?> field = f.getValue().getA();
			VortexClientMetadataField mfield = f.getValue().getB();
			Object value = field.getValue();
			try {
				String svalue = null;
				if (SH.is(value)) {
					switch (mfield.getData().getValueType()) {
						case VortexEyeMetadataField.VALUE_TYPE_BOOLEAN:
							svalue = Boolean.TRUE.equals(value) ? VortexEyeMetadataField.BOOLEAN_TRUE : null;
							break;
						case VortexEyeMetadataField.VALUE_TYPE_INT:
							int i = ((int) Double.parseDouble(value.toString()));
							svalue = Integer.toString(i);
							break;
						case VortexEyeMetadataField.VALUE_TYPE_ENUM:
							if (OH.ne("|", value))
								svalue = OH.toString(value);
							break;
						default:
							svalue = OH.toString(value);
							break;
					}
					if (SH.is(svalue)) {
						metadata.put(f.getKey(), svalue);
					}
				}
			} catch (NumberFormatException e) {
				getManager().showAlert("Invalid number format for " + mfield.getData().getTitle() + ":" + value);
				return null;
			}
		}
		return metadata;
	}

	private void populateField(VortexEyeMetadataField data, String value, FormPortletField<Object> r) {
		try {
			switch (data.getValueType()) {
				case VortexEyeMetadataField.VALUE_TYPE_BOOLEAN: {
					r.setValue(VortexEyeMetadataField.BOOLEAN_TRUE.equals(value));
					break;
				}
				case VortexEyeMetadataField.VALUE_TYPE_DOUBLE: {
					if (data.getMinValue() != null && data.getMaxValue() != null) {
						if (value != null)
							r.setValue(Double.parseDouble(value));
					} else
						r.setValue(value);
					break;
				}
				case VortexEyeMetadataField.VALUE_TYPE_INT: {
					if (data.getMinValue() != null && data.getMaxValue() != null) {
						if (value != null)
							r.setValue((double) Integer.parseInt(value));
					} else {
						r.setValue(value);
					}
					break;
				}
				case VortexEyeMetadataField.VALUE_TYPE_ENUM: {
					r.setValue("|");
					if (value != null) {
						((FormPortletSelectField<Object>) r).setValueNoThrow(value);
					}

					break;
				}
				case VortexEyeMetadataField.VALUE_TYPE_STRING: {
					if (value != null)
						r.setValue(value);
					break;
				}
				default:
					throw new RuntimeException("unknown type: " + data.getValueType());

			}
		} catch (Exception e) {
			throw new RuntimeException("Error for metadata field: " + data, e);
		}
	}
	private static FormPortletField<?> toField(VortexClientMetadataField field, String value) {
		try {
			VortexEyeMetadataField data = field.getData();
			switch (data.getValueType()) {
				case VortexEyeMetadataField.VALUE_TYPE_BOOLEAN: {
					FormPortletField<Boolean> r = new FormPortletCheckboxField(data.getTitle()).setHelp(data.getDescription());
					return r;
				}
				case VortexEyeMetadataField.VALUE_TYPE_DOUBLE: {
					if (data.getMinValue() != null && data.getMaxValue() != null) {
						FormPortletNumericRangeField r = new FormPortletNumericRangeField(data.getTitle()).setRange(data.getMinValue(), data.getMaxValue());
						r.setHelp(data.getDescription());
						r.setDecimals(4);
						return r;
					} else {
						FormPortletTextField r = new FormPortletTextField(data.getTitle()).setHelp(data.getDescription());
						return r;
					}
				}
				case VortexEyeMetadataField.VALUE_TYPE_INT: {
					if (data.getMinValue() != null && data.getMaxValue() != null) {
						FormPortletField<Double> r = new FormPortletNumericRangeField(data.getTitle()).setRange(data.getMinValue(), data.getMaxValue()).setHelp(
								data.getDescription());
						return r;
					} else {
						FormPortletTextField r = new FormPortletTextField(data.getTitle()).setHelp(data.getDescription());
						return r;
					}
				}
				case VortexEyeMetadataField.VALUE_TYPE_ENUM: {
					FormPortletSelectField<String> r = new FormPortletSelectField<String>(String.class, data.getTitle());
					r.setHelp(data.getDescription());
					for (Entry<String, String> e : data.getEnums().entrySet()) {
						if (OH.eq(e.getKey(), e.getValue()))
							r.addOption(e.getKey(), e.getKey());
						else
							r.addOption(e.getKey(), e.getValue() + " (" + e.getKey() + ")");
					}
					r.addOption("|", "<null>");
					r.setValue("|");

					return r;
				}
				case VortexEyeMetadataField.VALUE_TYPE_STRING: {
					FormPortletTextField r = new FormPortletTextField(data.getTitle()).setMaxChars(data.getMaxLength()).setHelp(data.getDescription());
					return r;
				}
				default:
					throw new RuntimeException("unknown type: " + data.getValueType());

			}
		} catch (Exception e) {
			throw new RuntimeException("Error for metadata field: " + field, e);
		}
	}

	public void addFormPortletListener(FormPortletListener listener) {
		this.formPortlet.addFormPortletListener(listener);
	}

}
