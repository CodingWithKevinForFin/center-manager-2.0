package com.f1.ami.web.dm;

import java.util.HashMap;
import java.util.Map;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.AmiWebFormatterManager;
import com.f1.ami.web.AmiWebLayoutHelper;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.form.queryfield.DateQueryField;
import com.f1.base.DateMillis;
import com.f1.suite.web.table.WebCellFormatter;
import com.f1.utils.CH;
import com.f1.utils.OneToOne;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_DateMillis;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;

public class AmiWebFormFieldVarLink {
	public static final int INVALID = -1;
	public static final String NONE = "None";
	public static final String TEXT = "Text";
	public static final String DATE = "Date";
	public static final String TIME = "Time";
	public static final String DATETIME = "Date & Time";

	public static final String[] dateQueryFieldFormatters = { NONE, TEXT, DATE, DATETIME, TIME, AmiUtils.DATE01, AmiUtils.DATE02, AmiUtils.DATE03, AmiUtils.DATE04, AmiUtils.DATE05,
			AmiUtils.DATE06, AmiUtils.DATE07, AmiUtils.DATE08, AmiUtils.DATE09, AmiUtils.DATE10, AmiUtils.DATE11, AmiUtils.DATE12 };
	public static final OneToOne<String, Integer> formatterMapNameToId = new OneToOne<String, Integer>();
	static {
		formatterMapNameToId.put(NONE, 0);
		formatterMapNameToId.put(TEXT, 1);
		formatterMapNameToId.put(DATE, 2);
		formatterMapNameToId.put(TIME, 3);
		formatterMapNameToId.put(DATETIME, 4);
		formatterMapNameToId.put(AmiUtils.DATE01, 5);
		formatterMapNameToId.put(AmiUtils.DATE02, 6);
		formatterMapNameToId.put(AmiUtils.DATE03, 7);
		formatterMapNameToId.put(AmiUtils.DATE04, 8);
		formatterMapNameToId.put(AmiUtils.DATE05, 9);
		formatterMapNameToId.put(AmiUtils.DATE06, 10);
		formatterMapNameToId.put(AmiUtils.DATE07, 11);
		formatterMapNameToId.put(AmiUtils.DATE08, 12);
		formatterMapNameToId.put(AmiUtils.DATE09, 13);
		formatterMapNameToId.put(AmiUtils.DATE10, 14);
		formatterMapNameToId.put(AmiUtils.DATE11, 15);
		formatterMapNameToId.put(AmiUtils.DATE12, 16);
	}
	public static final String[] defaultFormatters = { NONE, TEXT, DATE, DATETIME, TIME };

	private String portletId;
	private String formFullAliasDotName = "";
	private String aliasDotId = "";
	private String sourceVarname;
	private String targetVarname;
	private boolean rerunDmOnChange;
	private int formatterId;

	private AmiWebFormFieldVarLink(String sourceVarname, boolean rerunDmOnChange, String targetVarname, int formatterId, String portletId) {
		this.portletId = portletId;
		this.sourceVarname = sourceVarname;
		this.rerunDmOnChange = rerunDmOnChange;
		this.targetVarname = targetVarname;
		this.formatterId = formatterId;
	}
	public AmiWebFormFieldVarLink(String sourceVarname, boolean rerunDmOnChange, String targetVarname, int formatterId, String portletId, String formFullAliasDotName) {
		this.portletId = portletId;
		this.sourceVarname = sourceVarname;
		this.rerunDmOnChange = rerunDmOnChange;
		this.targetVarname = targetVarname;
		this.formatterId = formatterId;
		this.setFormFullAliasDotName(formFullAliasDotName);

	}
	public static Map<String, Object> getConfiguration(String alias, AmiWebFormFieldVarLink link) {
		Map<String, Object> obj = new HashMap<String, Object>();
		obj.put("portletId", link.getPortletId()); // Deprecated
		obj.put("ffadn", AmiWebUtils.getRelativeAlias(alias, link.getFormFullAliasDotName()));
		obj.put("fn", link.getSourceVarname());
		obj.put("rdoc", link.isRerunDmOnChange());
		obj.put("vn", link.getTargetVarname());
		obj.put("fid", link.getFormatterId());
		return obj;
	}
	public static AmiWebFormFieldVarLink init(Map<String, Object> obj, AmiWebService service) {
		String portletId = CH.getOrNoThrow(Caster_String.INSTANCE, obj, "portletId", null);
		String formfieldAliasDotName = CH.getOrNoThrow(Caster_String.INSTANCE, obj, "ffadn", null);
		String fieldName = CH.getOrThrow(Caster_String.INSTANCE, obj, "fn");
		boolean rerunDmOnChange = CH.getOrThrow(Caster_Boolean.PRIMITIVE, obj, "rdoc");
		String varname = CH.getOrThrow(Caster_String.INSTANCE, obj, "vn");
		int formatterId = CH.getOrThrow(Caster_Integer.PRIMITIVE, obj, "fid");

		//Backwards compatibility
		if (formfieldAliasDotName == null) {
			String panelId = CH.getOrNoThrow(Caster_String.INSTANCE, obj, "pnlId", null);
			if (panelId != null)
				formfieldAliasDotName = AmiWebUtils.getFullAlias(AmiWebLayoutHelper.DEFAULT_ROOT_ALIAS, panelId);
		}

		AmiWebFormFieldVarLink link = new AmiWebFormFieldVarLink(fieldName, rerunDmOnChange, varname, formatterId, portletId, formfieldAliasDotName);

		return link;
	}

	public void setFormFullAliasDotName(String formFullAliasDotName) {
		this.formFullAliasDotName = formFullAliasDotName;
	}
	public String getFormFullAliasDotName() {
		return formFullAliasDotName;
	}
	@Deprecated
	private String getPortletId() {
		return portletId;
	}
	@Deprecated
	private void setPortletId(String portletId) {
		this.portletId = portletId;
	}
	public String getSourceVarname() {
		return sourceVarname;
	}
	public void setSourceVarname(String varname) {
		this.sourceVarname = varname;
	}
	public boolean isRerunDmOnChange() {
		return rerunDmOnChange;
	}
	public void setRerunDmOnChange(boolean rerunDmOnChange) {
		this.rerunDmOnChange = rerunDmOnChange;
	}
	public String getTargetVarname() {
		return targetVarname;
	}
	public void setTargetVarname(String varname) {
		this.targetVarname = varname;
	}
	public int getFormatterId() {
		return formatterId;
	}
	public void setFormatterId(int formatterId) {
		this.formatterId = formatterId;
	}
	public static String[] getFormattersForField(Object object) {
		if (object instanceof DateQueryField)
			return dateQueryFieldFormatters;
		return defaultFormatters;
	}
	public static int getFormatterIdForFormatter(String formatter) {
		if (!formatterMapNameToId.getKeys().contains(formatter))
			return -1;
		return formatterMapNameToId.getValue(formatter);
	}
	public static Object format(int formatId, Object value, Class<?> clazz, AmiWebFormatterManager fm, AmiWebService service) {
		if (value == null)
			return null;
		String tz = service.getVarsManager().getTimeZoneId();
		switch (formatId) {
			case 0: {
				break;
			}
			case 1: {
				WebCellFormatter formatter = fm.getFormatter(clazz);
				value = formatter.formatCellToText(value);
				break;
			}
			case 2: {
				DateMillis date = Caster_DateMillis.INSTANCE.castOr(value, null);
				if (date == null)
					value = null;
				else
					value = fm.getformatDate("yyyy-MM-dd", date.getDate(), tz);
				break;
			}
			case 3: {
				DateMillis date = Caster_DateMillis.INSTANCE.castOr(value, null);
				if (date == null)
					value = null;
				else
					value = fm.getformatDate("HH:mm:ss", date.getDate(), tz);
				break;
			}
			case 4: {
				DateMillis date = Caster_DateMillis.INSTANCE.castOr(value, null);
				if (date == null)
					value = null;
				else
					value = fm.getformatDate("yyyy-MM-dd HH:mm:ss", date.getDate(), tz);
				break;
			}
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 16: {
				String formatName = formatterMapNameToId.getKey(formatId);
				DateMillis date = (DateMillis) value;
				value = fm.getformatDate(AmiUtils.dateOptionToFormatMap.get(formatName), date.getDate(), tz);
				break;
			}
			default: {
				break;
			}
		}
		return value;

	}
}
