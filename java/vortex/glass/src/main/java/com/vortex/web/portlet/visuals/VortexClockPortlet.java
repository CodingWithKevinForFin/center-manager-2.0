package com.vortex.web.portlet.visuals;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import com.f1.base.Action;
import com.f1.http.HttpRequestResponse;
import com.f1.suite.web.HttpRequestAction;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.PortletManagerListener;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.suite.web.portal.impl.AbstractPortlet;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSchema;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletColorField;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.CronTab;
import com.f1.utils.EH;
import com.f1.utils.IntArrayList;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class VortexClockPortlet extends AbstractPortlet implements FormPortletListener, PortletManagerListener {

	private static final int SECCONDS_PER_DAY = 3600 * 24;
	private TimeZone timezone = TimeZone.getDefault();
	private GregorianCalendar calendar = new GregorianCalendar(timezone);
	final private List<ClockRule> rules = new ArrayList<ClockRule>();
	private ClockRule currentRule, visibleRule;
	private long nextCheckTimeMs;
	private String timezoneDescription = "";
	public VortexClockPortlet(PortletConfig portletConfig) {
		super(portletConfig);
		rules.add(this.currentRule = this.visibleRule = new ClockRule(null, 0, SECCONDS_PER_DAY - 1, "#000000", "#FFFFFF", "z Y-M-D h:m:s"));
		getManager().addPortletManagerListener(this);
	}

	@Override
	public int getSuggestedHeight(PortletMetrics pm) {
		return 40;
		//return super.getSuggestedHeight(pm);
	}

	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return 300;
	}

	@Override
	protected void initJs() {
		flagPendingAjax();
		long now = getManager().getState().getWebState().getPartition().getContainer().getTools().getNow();
		processRules(now);
		super.initJs();
	}

	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		if ("onclick".equals(callback)) {
			ClockConfigPortlet fp = new ClockConfigPortlet(generateConfig(), rules, this.timezone, this.timezoneDescription);
			fp.buttonFormPortlet.addFormPortletListener(this);
			getManager().showDialog("Configure Clock", fp, 700, 400);
		} else
			super.handleCallback(callback, attributes);
	}
	@Override
	public void drainJavascript() {
		super.drainJavascript();
		long now = getManager().getState().getWebState().getPartition().getContainer().getTools().getNow();
		calendar.setTimeInMillis(now);
		//String bg, fg;
		//if (OH.isBetween(calendar.get(Calendar.HOUR_OF_DAY), 7, 17)) {
		//bg = "white";
		//fg = "black";
		//} else {
		//bg = "black";
		//fg = "lime";
		//}

		//TimeZone gtc = TimeZone.getTimeZone("EST5EDT");
		now += timezone.getOffset(now);

		//String format = "'" + timezone.getID() + "'b Y-M-D h:m:s";
		callJsFunction("init").addParamQuoted(timezoneDescription).addParamQuoted(currentRule.getFormat()).addParam(now).addParamQuoted(currentRule.getfColor())
				.addParamQuoted(currentRule.getbColor()).end();
		//flagPendingAjax();//TODO:REMOVE!!!
		this.visibleRule = currentRule;
	}

	public static final PortletSchema<VortexClockPortlet> SCHEMA = new BasicPortletSchema<VortexClockPortlet>("Clock", "ClockPortlet", VortexClockPortlet.class, false, true);

	@Override
	public PortletSchema<?> getPortletSchema() {
		return SCHEMA;
	}

	public TimeZone getTimezone() {
		return timezone;
	}

	public void setTimezone(TimeZone timezone) {

		this.calendar.setTimeZone(timezone);
		if (timezone == this.timezone)
			return;
		flagPendingAjax();
		this.timezone = timezone;
	}

	public static class Builder extends AbstractPortletBuilder<VortexClockPortlet> {
		private static final String ID = "Clock";
		public Builder() {
			super(VortexClockPortlet.class);
			setIcon("portlet_icon_clock");
		}
		@Override
		public VortexClockPortlet buildPortlet(PortletConfig portletConfig) {
			VortexClockPortlet portlet = new VortexClockPortlet(portletConfig);
			return portlet;
		}
		@Override
		public String getPortletBuilderName() {
			return "Clock";
		}
		@Override
		public String getPortletBuilderId() {
			return ID;
		}
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if ("submit".equals(button.getId())) {
			ClockConfigPortlet ccp = (ClockConfigPortlet) portlet.getParent();
			List<ClockRule> rules = ccp.getRules();
			if (rules != null) {
				setTimezone(TimeZone.getTimeZone(ccp.timezoneSelectField.getValue()));
				this.timezoneDescription = ccp.timezoneTextField.getValue();
				ccp.close();
				this.rules.clear();
				this.rules.addAll(rules);
				long now = getManager().getState().getWebState().getPartition().getContainer().getTools().getNow();
				processRules(now);
			}
		}
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {

	}

	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToPortletIdMapping, StringBuilder sb) {
		super.init(configuration, origToPortletIdMapping, sb);
		String tz = CH.getOr(String.class, configuration, "tz", null);
		this.timezoneDescription = CH.getOr(String.class, configuration, "tzd", null);
		if (tz != null)
			setTimezone(TimeZone.getTimeZone(tz));
		List<String> rules = CH.getOr(List.class, configuration, "rules", null);
		if (rules != null) {
			this.rules.clear();
			this.currentRule = null;
			this.visibleRule = null;
			for (String rule : rules)
				this.rules.add(new ClockRule(rule));
			long now = getManager().getState().getWebState().getPartition().getContainer().getTools().getNow();
			processRules(now);
		}
	}

	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		r.put("tz", getTimezone().getID());
		r.put("tzd", timezoneDescription);
		List<String> rules = new ArrayList<String>(this.rules.size());
		StringBuilder sink = new StringBuilder();
		for (ClockRule i : this.rules) {
			i.dump(SH.clear(sink));
			rules.add(sink.toString());
		}
		r.put("rules", rules);
		return r;
	}

	public static class ClockConfigPortlet extends GridPortlet implements FormPortletListener, FormPortletContextMenuListener {

		private FormPortlet configFormPortlet;
		private FormPortlet buttonFormPortlet;
		private FormPortlet daysFormPortlet;
		private FormPortlet startTimePortlet;
		private FormPortlet endTimePortlet;
		private FormPortlet upButtonsPortlet;
		private FormPortlet dnButtonsPortlet;
		private FormPortlet formatPortlet;
		private FormPortlet fgColorPortlet;
		private FormPortlet bgColorPortlet;
		private FormPortletField<String>[] defaultRow;
		private FormPortlet rmButtonsPortlet;
		private FormPortletSelectField<String> regionSelectField;
		private FormPortletSelectField<String> timezoneSelectField;
		private FormPortletTextField timezoneTextField;

		public ClockConfigPortlet(PortletConfig config, List<ClockRule> rules, TimeZone tz, String title) {
			super(config);
			HtmlPortlet headerPortlet = new HtmlPortlet(generateConfig(), "", "comment_header");
			headerPortlet.setHtml("<div style=\"width:100%;height:100%;background-image:url('rsc/headers/" + "clock.jpg"
					+ "');background-repeat:no-repeat;background-position:center;text-align:center;padding:5px 5px\"></div>");

			this.configFormPortlet = new FormPortlet(generateConfig());
			this.buttonFormPortlet = new FormPortlet(generateConfig());
			regionSelectField = configFormPortlet.addField(new FormPortletSelectField<String>(String.class, "Region").setId("rg"));

			for (String id : EH.getTimeZoneRegions())
				regionSelectField.addOption(id, id.length() == 0 ? "-- OTHER --" : id);
			regionSelectField.sortOptionsByName();
			timezoneSelectField = configFormPortlet.addField(new FormPortletSelectField<String>(String.class, "Timezone").setId("tz"));
			regionSelectField.setValue(EH.getTimeZoneRegion(tz));
			timezoneTextField = configFormPortlet.addField(new FormPortletTextField("Timezone Description").setId("tzd"));
			updateTimeZoneField();
			timezoneSelectField.setValue(tz.getID());
			if (SH.isnt(title))
				updateTimeZoneTextField();
			else
				timezoneTextField.setValue(title);

			buttonFormPortlet.addButton(new FormPortletButton("Import / Export").setId("export"));
			buttonFormPortlet.addButton(new FormPortletButton("Update Clock").setId("submit"));
			buttonFormPortlet.addFormPortletListener(this);
			configFormPortlet.addFormPortletListener(this);

			daysFormPortlet = new FormPortlet(generateConfig()).setLabelsWidth(60);
			startTimePortlet = new FormPortlet(generateConfig()).setLabelsWidth(0);
			endTimePortlet = new FormPortlet(generateConfig()).setLabelsWidth(0);
			formatPortlet = new FormPortlet(generateConfig()).setLabelsWidth(0);
			upButtonsPortlet = new FormPortlet(generateConfig()).setLabelsWidth(0);
			dnButtonsPortlet = new FormPortlet(generateConfig()).setLabelsWidth(0);
			rmButtonsPortlet = new FormPortlet(generateConfig()).setLabelsWidth(0);
			fgColorPortlet = new FormPortlet(generateConfig()).setLabelsWidth(0);
			bgColorPortlet = new FormPortlet(generateConfig()).setLabelsWidth(0);

			daysFormPortlet.addField(new FormPortletTitleField("days")).setHeight(18).setHelp("EX: SMTWRFA or M-F");
			startTimePortlet.addField(new FormPortletTitleField("start time")).setHeight(18).setHelp("HH:MM:SS (in military time)");
			endTimePortlet.addField(new FormPortletTitleField("end time")).setHeight(18).setHelp("HH:MM:SS (in military time)");
			formatPortlet.addField(new FormPortletTitleField("Format")).setHeight(18)
					.setHelp("EX: Y-M-D h:m:s 'title' (Y=year, M=month, d=day of month, h=hour, m=minute, s=second, b=line break, wrap text in single quotes)");
			fgColorPortlet.addField(new FormPortletTitleField("text").setHeight(18));
			bgColorPortlet.addField(new FormPortletTitleField("back").setHeight(18));
			upButtonsPortlet.addField(new FormPortletTitleField("&nbsp").setHeight(18));
			dnButtonsPortlet.addField(new FormPortletTitleField("&nbsp").setHeight(18));
			//rmButtonsPortlet.addField(new FormPortletTitleField("&nbsp"));

			upButtonsPortlet.addMenuListener(this);
			dnButtonsPortlet.addMenuListener(this);
			rmButtonsPortlet.addMenuListener(this);

			rmButtonsPortlet.addField(new FormPortletButtonField("").setValue("&nbsp;&nbsp;&nbsp;add&nbsp;&nbsp;&nbsp;&nbsp;").setId("add"));
			for (int i = 0; i < rules.size(); i++) {
				ClockRule rule = rules.get(i);
				addRow(i, "", formatDays(rule.getDays()), formatTime(rule.getStartTime()), formatTime(rule.getEndTime()), rule.getFormat(), rule.getfColor(), rule.getbColor(),
						rule.getDays() == null);
			}
			updateTitles();
			((FormPortletTextField) defaultRow[POS_DAY]).setDisabled(true);
			((FormPortletTextField) defaultRow[POS_STR]).setDisabled(true);
			((FormPortletTextField) defaultRow[POS_END]).setDisabled(true);

			addChild(headerPortlet, 0, 0, 9, 1);
			addChild(configFormPortlet, 0, 1, 9, 1);
			addChild(daysFormPortlet, 0, 2);
			addChild(startTimePortlet, 1, 2);
			addChild(endTimePortlet, 2, 2);
			addChild(formatPortlet, 3, 2);
			addChild(fgColorPortlet, 4, 2);
			addChild(bgColorPortlet, 5, 2);
			addChild(upButtonsPortlet, 6, 2);
			addChild(dnButtonsPortlet, 7, 2);
			addChild(rmButtonsPortlet, 8, 2);
			addChild(buttonFormPortlet, 0, 3, 9, 1);
			setColSize(0, 140);
			setColSize(1, 65);
			setColSize(2, 65);
			setColSize(4, 35);
			setColSize(5, 35);
			setColSize(6, 32);
			setColSize(7, 32);
			setColSize(8, 65);
			setRowSize(0, 120);
			setRowSize(1, 70);
			setRowSize(3, 40);

		}
		private String describe(String id, TimeZone timeZone) {
			StringBuilder sb = new StringBuilder();
			SH.replaceAll(id, '_', ' ', sb);
			sb.append(" (");
			SH.formatDuration(-timeZone.getRawOffset(), sb);
			if (timeZone.getDSTSavings() != 0) {
				sb.append(", ");
				SH.formatDuration(timeZone.getDSTSavings(), sb);
				sb.append(" DST");
			}
			sb.append(")");
			return sb.toString();
		}
		private String formatDays(int[] days) {
			if (days == null)
				return "SMTWRFA";
			StringBuilder sb = new StringBuilder(days.length);
			for (int day : days) {
				sb.append("SMTWRFA".charAt(day));
			}
			return sb.toString();
		}
		private String formatTime(int time) {
			final StringBuilder r = new StringBuilder(8);
			final int h = time / 3600, m = (time % 3600) / 60, s = time % 60;
			(h < 10 ? r.append('0') : r).append(h);
			(m < 10 ? r.append(":0") : r.append(':')).append(m);
			(s < 10 ? r.append(":0") : r.append(':')).append(s);
			return r.toString();
		}
		private final int POS_DAY = 0;
		private final int POS_STR = 1;
		private final int POS_END = 2;
		private final int POS_FMT = 3;
		private final int POS_FGC = 4;
		private final int POS_BGC = 5;
		private final int POS_UPB = 6;
		private final int POS_DNB = 7;
		private final int POS_RMB = 8;
		private List<FormPortletField<String>[]> rows = new ArrayList<FormPortletField<String>[]>();

		private FormPortletField<String>[] addRow(int position, String label, String days, String startTime, String endTime, String format, String fg, String bg, boolean isDefault) {
			position++;
			FormPortletField<String> fields[] = new FormPortletField[9];
			fields[POS_DAY] = daysFormPortlet.addField(new FormPortletTextField(label).setValue(days), position).setWidth(70);
			fields[POS_STR] = startTimePortlet.addField(new FormPortletTextField("").setValue(startTime), position).setWidth(55);
			fields[POS_END] = endTimePortlet.addField(new FormPortletTextField("").setValue(endTime), position).setWidth(55);
			fields[POS_FMT] = formatPortlet.addField(new FormPortletTextField("").setValue(format), position).setWidth(FormPortletTextField.WIDTH_STRETCH);
			fields[POS_FGC] = fgColorPortlet.addField(new FormPortletColorField(""), position).setValue(fg).setWidth(20);
			fields[POS_BGC] = bgColorPortlet.addField(new FormPortletColorField(""), position).setValue(bg).setWidth(20);
			if (isDefault) {
				fields[POS_UPB] = upButtonsPortlet.addField(new FormPortletTitleField(""), position).setHeight(18);
				fields[POS_DNB] = dnButtonsPortlet.addField(new FormPortletTitleField(""), position).setHeight(18);
				fields[POS_RMB] = rmButtonsPortlet.addField(new FormPortletTitleField(""), position).setHeight(18);
			} else {
				fields[POS_UPB] = upButtonsPortlet.addField(new FormPortletButtonField("").setValue("/\\"), position);
				fields[POS_DNB] = dnButtonsPortlet.addField(new FormPortletButtonField("").setValue("\\/"), position);
				fields[POS_RMB] = rmButtonsPortlet.addField(new FormPortletButtonField("").setValue("remove"), position);
			}
			for (FormPortletField<String> field : fields)
				field.setCorrelationData(fields);
			rows.add(position - 1, fields);
			if (isDefault)
				this.defaultRow = fields;
			return fields;
		}
		@Override
		public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
			if ("export".equals(button.getId())) {
				List<ClockRule> rules = getRules();
				if (rules != null) {
					StringBuilder sb = new StringBuilder();
					for (ClockRule rule : rules) {
						rule.dump(sb);
						sb.append('\n');
					}
					FormPortlet fp = new FormPortlet(generateConfig());
					String text = sb.toString();
					fp.setLabelsWidth(0);
					fp.addField(new FormPortletTextAreaField("").setId("data").setValue(text)).setHeight(400).setCorrelationData(text);
					fp.addButton(new FormPortletButton("import").setId("import"));
					fp.addFormPortletListener(this);
					getManager().showDialog("import / export", fp, 500, 500);
				}
			}
			if ("import".equals(button.getId())) {
				FormPortletField<String> field = portlet.getField("data", String.class);
				if (OH.eq(field.getValue(), field.getCorrelationData())) {
					portlet.close();
					return;
				}
				List<ClockRule> rules2 = new ArrayList<ClockRule>();
				try {
					for (String s : SH.splitLines(field.getValue())) {
						if (SH.isnt(s))
							continue;
						rules2.add(new ClockRule(s.trim()));
					}
				} catch (Exception e) {
					getManager().showAlert("Error importing; " + e.getMessage());
					return;
				}

				for (FormPortletField<String>[] row : rows)
					for (FormPortletField<String> f : row)
						f.getForm().removeField(f);
				rows.clear();
				int i = 0;
				for (ClockRule rule : rules2) {
					addRow(i++, "", formatDays(rule.getDays()), formatTime(rule.getStartTime()), formatTime(rule.getEndTime()), rule.getFormat(), rule.getfColor(),
							rule.getbColor(), rule.getDays() == null);
				}
				portlet.close();
			}
		}
		public List<ClockRule> getRules() {
			List<ClockRule> rules = new ArrayList<ClockRule>();
			int ruleId = 0;
			for (FormPortletField<String>[] row : this.rows) {
				ruleId++;
				final int startTime = parseTime(SH.trim(row[POS_STR].getValue()));
				if (startTime == -1) {
					getManager().showAlert("Invalid start time for rule " + ruleId);
					return null;
				}
				final int endTime = parseTime(SH.trim(row[POS_END].getValue()));
				if (endTime == -1) {
					getManager().showAlert("Invalid end time for rule " + ruleId);
					return null;
				}

				String daysText = SH.trim(row[POS_DAY].getValue());
				if (SH.isnt(daysText)) {
					getManager().showAlert("days Requires for rule " + ruleId);
					return null;
				}
				final Set<Character> days;
				if (row != this.defaultRow) {
					daysText = daysText.toUpperCase();
					days = CH.s(SH.toCharacterArray(daysText));
					for (char i : days) {
						if ("SMTWRFA".indexOf(i) == -1) {
							getManager().showAlert("invalid day for rule " + ruleId + ": " + i);
							return null;
						}
					}
					if (days.size() != daysText.length()) {
						getManager().showAlert("duplicate day listed for rule " + ruleId);
						return null;
					}
				} else
					days = null;

				final String formatText = SH.trim(row[POS_FMT].getValue());
				if (SH.isnt(formatText)) {
					getManager().showAlert("Format required for rule " + ruleId);
					return null;
				}
				final String fColor = SH.trim(row[POS_FGC].getValue());
				final String bColor = SH.trim(row[POS_BGC].getValue());

				rules.add(new ClockRule(toDays(days), startTime, endTime, fColor, bColor, formatText));
			}
			return rules;
		}

		private int[] toDays(Set<Character> days) {
			if (days == null)
				return null;
			IntArrayList r = new IntArrayList(7);
			for (int i = 0; i < 7; i++)
				if (days.contains("SMTWRFA".charAt(i)))
					r.add(i);
			return AH.sort(r.toIntArray());
		}
		private int parseTime(String time) {
			if (SH.isnt(time))
				return -1;
			String[] parts = SH.split(':', time);
			if (OH.isntBetween(parts.length, 1, 3))
				return -1;
			int r = 0;
			for (int pos = 0; pos < parts.length; pos++) {
				if (parts[pos].length() != 2)
					return -1;
				try {
					int i = Integer.parseInt(parts[pos]);
					if (OH.isntBetween(i, 0, pos == 0 ? 23 : 59))
						return -1;
					r += pos == 0 ? i * 3600 : pos == 1 ? i * 60 : i;
				} catch (NumberFormatException e) {
					return -1;
				}
			}
			return r;
		}
		@Override
		public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
			if (field == this.regionSelectField) {
				updateTimeZoneField();
			} else if (field == timezoneSelectField) {
				updateTimeZoneTextField();
			}
		}
		private void updateTimeZoneTextField() {
			this.timezoneTextField.setValue(SH.replaceAll(SH.afterLast(timezoneSelectField.getValue(), "/", timezoneSelectField.getValue()), '_', ' '));
		}
		private void updateTimeZoneField() {
			Map<String, TimeZone> timeZones = EH.getTimeZonesByRegion(this.regionSelectField.getValue());
			this.timezoneSelectField.clearOptions();
			for (Entry<String, TimeZone> e : timeZones.entrySet())
				this.timezoneSelectField.addOption(e.getValue().getID(), describe(e.getKey(), e.getValue()));
			this.timezoneSelectField.sortOptionsByName();
			updateTimeZoneTextField();
		}
		@Override
		public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
			if ("button_clicked".equals(action)) {
				if (portlet == this.rmButtonsPortlet) {
					if ("add".equals(node.getId())) {
						addRow(0, "Rule 1", "", "", "", "", "#000000", "#FFFFFF", false);
						updateTitles();
					} else {
						FormPortletField<String>[] row = (FormPortletField<String>[]) node.getCorrelationData();
						rows.remove(row);
						for (FormPortletField<String> field : row) {
							FormPortlet form = field.getForm();
							form.removeField(field);
						}
						updateTitles();
					}
				} else if (portlet == this.upButtonsPortlet) {
					FormPortletField<String>[] row = (FormPortletField<String>[]) node.getCorrelationData();
					int pos = rows.indexOf(row);
					if (pos > 0) {
						rows.remove(pos);
						pos--;
						rows.add(pos, row);
						for (FormPortletField<String> field : row) {
							FormPortlet form = field.getForm();
							form.removeField(field);
							form.addField(field, pos + 1);
						}
					}
					updateTitles();
				} else if (portlet == this.dnButtonsPortlet) {
					FormPortletField<String>[] row = (FormPortletField<String>[]) node.getCorrelationData();
					int pos = rows.indexOf(row);
					if (pos < rows.size() - 2) {
						rows.remove(pos);
						pos++;
						rows.add(pos, row);
						for (FormPortletField<String> field : row) {
							FormPortlet form = field.getForm();
							form.removeField(field);
							form.addField(field, pos + 1);
						}
					}
					updateTitles();
				}
			}
		}
		private void updateTitles() {
			for (int i = 0; i < rows.size(); i++)
				rows.get(i)[POS_DAY].setTitle("Rule " + SH.toString(i + 1));
		}
		@Override
		public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
			// TODO Auto-generated method stub

		}

	}

	public static class ClockRule {
		private final int[] days;//sunday=0,monday=1, .. ,saturday=6
		private final int startTime;
		private final int endTime;
		private final String fColor;
		private final String bColor;
		private final String format;
		private int[] timeRanges;

		public void dump(StringBuilder sink) {
			if (days != null)
				SH.join("", days, sink);
			sink.append(',').append(startTime).append(',').append(endTime).append(',').append(fColor).append(',').append(bColor).append(',').append(format);
		}

		public ClockRule(String text) {
			String[] parts = SH.split(',', text);
			String days = parts[0];
			if (SH.isnt(days)) {
				this.days = null;
			} else {
				this.days = new int[days.length()];
				for (int i = 0; i < this.days.length; i++)
					this.days[i] = days.charAt(i) - '0';
			}
			this.startTime = Integer.parseInt(parts[1]);
			this.endTime = Integer.parseInt(parts[2]);
			this.fColor = parts[3];
			this.bColor = parts[4];
			this.format = SH.join(',', AH.subarray(parts, 5, parts.length - 5));
			calcTimeRanges();
		}

		public ClockRule(int[] days, int startTime, int endTime, String fColor, String bColor, String format) {
			this.days = days;
			this.startTime = startTime;
			this.endTime = endTime;
			this.fColor = fColor;
			this.bColor = bColor;
			this.format = format;
			calcTimeRanges();
		}
		private void calcTimeRanges() {
			if (days == null)
				this.timeRanges = null;
			else {
				this.timeRanges = new int[days.length * 2];
				for (int i = 0; i < this.days.length; i++) {
					int day = this.days[i];
					int s = day * SECCONDS_PER_DAY + startTime;
					int e = day * SECCONDS_PER_DAY + endTime;
					if (endTime <= startTime)
						e += SECCONDS_PER_DAY;
					timeRanges[i * 2] = s;
					timeRanges[i * 2 + 1] = e;
				}
			}

		}

		public int[] getDays() {
			return days;
		}
		public String getFormat() {
			return format;
		}
		public int getStartTime() {
			return startTime;
		}
		public int getEndTime() {
			return endTime;
		}
		public String getfColor() {
			return fColor;
		}
		public String getbColor() {
			return bColor;
		}

		public int isInRule(int secondsInWeek) {
			if (days == null)
				return SECCONDS_PER_DAY * 7 - 3600;
			for (int i = 0; i < this.timeRanges.length; i += 2) {
				if (this.timeRanges[i] > secondsInWeek)
					return -1;
				else if (secondsInWeek <= this.timeRanges[i + 1])
					return this.timeRanges[i + 1];
			}
			return -1;
		}

		//returns number of seconds from Now
		public int getNextTime(int secondsInWeek) {
			if (days == null)
				return SECCONDS_PER_DAY * 7 - 3600;
			for (int i = 0; i < this.timeRanges.length; i += 2)
				if (this.timeRanges[i] > secondsInWeek)
					return this.timeRanges[i];
			return this.timeRanges[0] + SECCONDS_PER_DAY * 7 - 3600;//subtract 3600 in case of Day Light Saving
		}

	}

	@Override
	public void onFrontendCalled(PortletManager manager, Map<String, String> attributes, HttpRequestAction action) {
		long now = getManager().getState().getWebState().getPartition().getContainer().getTools().getNow();
		if (now >= nextCheckTimeMs) {
			processRules(now);
		}
	}

	private void processRules(long now) {
		this.calendar.setTimeInMillis(now);
		CronTab.clearFieldsOnAndAfter(this.calendar, Calendar.DAY_OF_WEEK);
		int secondsThisWeek = (int) ((now - this.calendar.getTimeInMillis()) / 1000);
		int endTime = -1;

		for (ClockRule rule : rules) {
			endTime = rule.isInRule(secondsThisWeek);
			if (endTime != -1) {
				if (rule != this.currentRule) {
					this.currentRule = rule;
					flagPendingAjax();
				}
				break;
			}
		}
		int min = endTime;
		for (ClockRule rule : rules) {
			int nextTime = rule.getNextTime(secondsThisWeek);
			if (nextTime < min)
				min = nextTime;
		}
		this.nextCheckTimeMs = calendar.getTimeInMillis() + min * 1000;
	}
	@Override
	public void onBackendCalled(PortletManager manager, Action action) {

	}

	@Override
	public void onInit(PortletManager manager, Map<String, Object> configuration, String rootId) {

	}

	public static void main(String a[]) {

		for (int i = 0; i < 7; i++) {
			ClockRule cr = new ClockRule(new int[] { i }, 0, SECCONDS_PER_DAY - 1, "", "", "");
			Calendar ins = Calendar.getInstance();
			ins.setTimeInMillis(ins.getTimeInMillis() + 6 * SECCONDS_PER_DAY * 1000L);
			System.out.println(new Date(ins.getTimeInMillis()));

			long now = ins.getTimeInMillis();
			CronTab.clearFieldsOnAndAfter(ins, Calendar.DAY_OF_WEEK);
			int secondsThisWeek = (int) ((now - ins.getTimeInMillis()) / 1000);
			System.out.println(i + ": " + cr.isInRule(secondsThisWeek));
		}

	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPortletManagerClosed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageRefreshed(PortletManager basicPortletManager) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onMetadataChanged(PortletManager basicPortletManager) {
	}

	@Override
	public void onPageLoading(PortletManager basicPortletManager, Map<String, String> attributes, HttpRequestResponse action) {
		// TODO Auto-generated method stub

	}
}
