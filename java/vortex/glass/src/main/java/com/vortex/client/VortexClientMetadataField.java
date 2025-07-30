package com.vortex.client;

import java.util.Map;

import com.f1.suite.web.table.WebCellFormatter;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.suite.web.table.impl.BasicWebColumn;
import com.f1.utils.CH;
import com.f1.utils.MH;
import com.f1.utils.SH;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexEyeMetadataField;

public class VortexClientMetadataField extends VortexClientEntity<VortexEyeMetadataField> {

	public static final String COLUMNID_PREFIX = "md_";

	private String validationDescription;
	private WebCellFormatter formatter;
	private String columnIdName;
	public VortexClientMetadataField(VortexEyeMetadataField data) {
		super(VortexAgentEntity.TYPE_METADATA_FIELD, data);
		update(data);
		this.columnIdName = COLUMNID_PREFIX + getData().getKeyCode();
	}

	static public class MetadataFieldFormatter extends BasicWebCellFormatter {

		private VortexClientMetadataField field;
		private Map<String, String> mapping;
		private String nullValue;

		public MetadataFieldFormatter(VortexClientMetadataField field) {
			this.field = field;
			switch (this.field.getData().getValueType()) {
				case VortexEyeMetadataField.VALUE_TYPE_BOOLEAN:
					mapping = CH.m(VortexEyeMetadataField.BOOLEAN_TRUE, "true", VortexEyeMetadataField.BOOLEAN_FALSE, "false");
					nullValue = "false";
					break;
				case VortexEyeMetadataField.VALUE_TYPE_ENUM:
					mapping = field.getData().getEnums();
					break;
			}
		}

		@Override
		public StringBuilder formatCellToText(Object value, StringBuilder sb) {
			Map<String, String> values = (Map<String, String>) value;
			String val = values == null ? null : values.get(field.getData().getKeyCode());
			if (val == null) {
				if (nullValue != null)
					sb.append(nullValue);
				return sb;
			}
			if (mapping != null) {
				String val2 = mapping.get(val);
				sb.append(val2 != null ? val2 : val);
			} else
				sb.append(val);
			return sb;
		}
		@Override
		public void formatCellToHtml(Object value, StringBuilder sb, StringBuilder cellStyle) {
			Map<String, String> values = (Map<String, String>) value;
			String val = values == null ? null : values.get(field.getData().getKeyCode());
			if (val == null) {
				if (nullValue != null)
					sb.append(nullValue);
				return;
			}
			if (mapping != null) {
				String val2 = mapping.get(val);
				sb.append(val2 != null ? val2 : val);
			} else
				sb.append(val);
			return;
		}
		@Override
		public Comparable getOrdinalValue(Object value) {
			Map<String, String> values = (Map<String, String>) value;
			String val = values == null ? null : values.get(field.getData().getKeyCode());
			if (val == null) {
				return nullValue;
			}
			if (mapping != null) {
				String val2 = mapping.get(val);
				return val2;
			} else {
				switch (field.getData().getValueType()) {
					case VortexEyeMetadataField.VALUE_TYPE_DOUBLE:
						return Double.parseDouble(val);
					case VortexEyeMetadataField.VALUE_TYPE_INT:
						return Integer.parseInt(val);
					default:
						return val;
				}
			}
		}

	}

	@Override
	public void update(VortexEyeMetadataField data) {
		super.update(data);
		data = getData();
		validationDescription = "";
		switch (data.getValueType()) {
			case VortexEyeMetadataField.VALUE_TYPE_BOOLEAN:
				break;
			case VortexEyeMetadataField.VALUE_TYPE_ENUM:
				validationDescription = "[" + SH.join(',', data.getEnums().keySet()) + "]";
				break;
			case VortexEyeMetadataField.VALUE_TYPE_DOUBLE:
				if (data.getMinValue() != null && data.getMaxValue() != null)
					validationDescription = data.getMinValue() + " to " + data.getMaxValue();
				else if (data.getMinValue() != null)
					validationDescription = " > " + data.getMinValue();
				else if (data.getMaxValue() != null)
					validationDescription = " < " + data.getMaxValue();
				break;
			case VortexEyeMetadataField.VALUE_TYPE_INT:
				if (data.getMinValue() != null && data.getMaxValue() != null)
					validationDescription = data.getMinValue().intValue() + " to " + data.getMaxValue().intValue();
				else if (data.getMinValue() != null)
					validationDescription = " >= " + data.getMinValue().intValue();
				else if (data.getMaxValue() != null)
					validationDescription = " <= " + data.getMaxValue().intValue();
				break;
			case VortexEyeMetadataField.VALUE_TYPE_STRING:
				validationDescription = data.getMaxLength() + " max chars";
				break;
		}
		this.formatter = new MetadataFieldFormatter(this);
	}
	public String getValidationDescription() {
		return validationDescription;
	}

	public WebCellFormatter getFormatter() {
		return formatter;
	}

	public boolean appliesTo(byte type) {
		return MH.allBits(getData().getTargetTypes(), 1L << type);
	}
	public WebColumn toWebColumn(String columnId) {
		return new BasicWebColumn(getColumnId(), getColumnName(), getFormatter(), new Object[] { columnId }).setCssColumn("metafield");
	}

	public String getColumnId() {
		return columnIdName;
	}
	public String getColumnName() {
		return getData().getTitle();
	}

}
