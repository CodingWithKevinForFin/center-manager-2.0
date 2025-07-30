package com.f1.ami.web.dm.portlets;

import java.util.Map;
import java.util.TimeZone;

import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletDivField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTimeZoneField;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.utils.CronTab;
import com.f1.utils.EH;
import com.f1.utils.SH;
import com.f1.utils.Timer;
import com.f1.utils.formatter.BasicDateFormatter;
import com.f1.utils.structs.table.BasicTable;

public class AmiWebCrontabPortlet extends GridPortlet implements FormPortletListener {

	private FormPortlet formPortlet;
	private FastTablePortlet tablePortlet;
	private FormPortlet buttonPortlet;
	private FormPortletButton evalButton;
	private FormPortletButton submitButton;
	private FormPortletButton cancelButton;
	private BasicTable evalTable;
	private FormPortletTimeZoneField timezoneField;
	private FormPortletTextAreaField timersField;
	private DividerPortlet div;
	private AmiWebEditDmPortlet dmEditor;

	public AmiWebCrontabPortlet(PortletConfig config, AmiWebEditDmPortlet dm) {
		super(config);
		this.dmEditor = dm;
		this.formPortlet = new FormPortlet(generateConfig());
		this.timezoneField = this.formPortlet.addField(new FormPortletTimeZoneField());
		this.timersField = this.formPortlet.addField(new FormPortletTextAreaField("Timer(s): "));
		this.timezoneField.setValueNoThrow(dm.getCrontabTimezone());
		if (dm.getCrontab() != null)
			this.timersField.setValueNoThrow(SH.replaceAll(dm.getCrontab(), '&', '\n'));
		this.evalTable = new BasicTable();
		FormPortletDivField help = this.formPortlet.addField(new FormPortletDivField(""));
		this.timezoneField.setLeftTopWidthHeightPx(100, 10, 200, 20);
		this.timersField.setLeftTopRightBottom(100, 35, 30, 125);
		help.setLeftPosPx(100).setBottomPosPx(5).setRightPosPx(10).setHeightPx(120);
		StringBuilder h = new StringBuilder();
		h.append("Valid formats include:<PRE>");
		h.append("   HH:MM:SS             <-- ex: <i>14:30:00</i>\n");
		h.append("   HH:MM:SS WEEKDAY(S)  <-- ex: <i>14:30:00 MON-FRI</i>\n");
		h.append("   HH:MM:SS MONTH DAY   <-- ex: <i>14:30:00 MAY 3</i>\n");
		h.append("   * * * * * *          <-- Crontab syntax (see documentation)\n");
		h.append("</PRE>(Seconds are optional, for example 14:30 is same as 14:30:00)");
		help.setValue(h.toString());
		evalTable.addColumn(Long.class, "Sequence");
		evalTable.addColumn(String.class, "Date");
		evalTable.addColumn(String.class, "Time");
		evalTable.addColumn(String.class, "Weekday");
		evalTable.addColumn(String.class, "MonthAndDay");
		evalTable.addColumn(String.class, "Timezone");
		this.tablePortlet = new FastTablePortlet(generateConfig(), evalTable, "upcoming Runtimes");
		this.div = new DividerPortlet(generateConfig(), false, this.formPortlet, this.tablePortlet);
		AmiWebService service = AmiWebUtils.getService(this.getManager());
		BasicWebCellFormatter f = service.getFormatterManager().getBasicFormatter();
		this.tablePortlet.getTable().addColumn(true, "Unix Epoc(millis)", "Sequence", f).setWidth(120);
		this.tablePortlet.getTable().addColumn(true, "Time", "Time", f);
		this.tablePortlet.getTable().addColumn(true, "Date", "Date", f);
		this.tablePortlet.getTable().addColumn(true, "Weekday", "Weekday", f);
		this.tablePortlet.getTable().addColumn(true, "Month and Day", "MonthAndDay", f);
		this.tablePortlet.getTable().addColumn(true, "Timezone", "Timezone", f).setWidth(200);
		this.buttonPortlet = new FormPortlet(generateConfig());
		this.evalButton = this.buttonPortlet.addButton(new FormPortletButton("Test (alt+enter)"));
		this.submitButton = this.buttonPortlet.addButton(new FormPortletButton("Submit"));
		this.cancelButton = this.buttonPortlet.addButton(new FormPortletButton("Cancel"));
		addChild(this.div, 0, 0);
		addChild(this.buttonPortlet, 0, 1);
		setRowSize(1, 40);
		this.div.setOffset(1);
		this.formPortlet.addFormPortletListener(this);
		this.buttonPortlet.addFormPortletListener(this);
		runEval(false);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.cancelButton) {
			ConfirmDialogPortlet.confirmAndCloseWindow(this, "Discard changes?");
			return;
		}
		if (button == this.evalButton) {
			runEval(true);
		} else if (button == submitButton) {
			try {
				String tz = timezoneField.getValue();
				AmiWebService service = AmiWebUtils.getService(this.getManager());
				if (tz == null)
					tz = service.getVarsManager().getTimeZoneId();
				TimeZone timeZone = EH.getTimeZone(tz);
				String s = timersField.getValue();
				StringBuilder sb = new StringBuilder();
				for (String t : SH.splitLines(s)) {
					if (SH.is(t)) {
						if (sb.length() > 0)
							sb.append('&');
						sb.append(t);
					}
				}
				s = sb.toString();
				if (SH.is(s))
					CronTab.parse(s, timeZone);
				dmEditor.setCrontab(s);
				dmEditor.setCrontabTimezone(timezoneField.getValue());
			} catch (Exception e) {
				getManager().showAlert("Timer syntax incorrect: " + e.getMessage(), e);
				return;
			}
			close();
		}
	}

	private void runEval(boolean showOnError) {
		String tz = timezoneField.getValue();
		final AmiWebService service = AmiWebUtils.getService(this.getManager());
		if (tz == null)
			tz = service.getVarsManager().getTimeZoneId();
		final TimeZone timeZone = EH.getTimeZone(tz);
		final BasicDateFormatter df = new BasicDateFormatter("yyyy-MM-dd", timeZone);
		final BasicDateFormatter tf = new BasicDateFormatter(service.getVarsManager().getTimeWithSecondsFormat(), timeZone);
		final BasicDateFormatter wf = new BasicDateFormatter("EEEE", timeZone);
		final BasicDateFormatter mf = new BasicDateFormatter("MMMM", timeZone);
		this.evalTable.clear();
		String s = timersField.getValue();
		final StringBuilder sb = new StringBuilder();
		for (final String t : SH.splitLines(s)) {
			if (SH.is(t)) {
				if (sb.length() > 0)
					sb.append('&');
				sb.append(t);
			}
		}
		s = sb.toString();
		if (SH.is(s)) {
			Timer crontab;
			try {
				crontab = CronTab.parse(s, timeZone);
			} catch (final Exception e) {
				if (showOnError)
					getManager().showAlert("Timer syntax incorrect: " + e.getMessage(), e);
				return;
			}
			EH.now();
			long n = System.currentTimeMillis();
			for (int i = 0; i < 100; i++) {
				n = crontab.calculateNextOccurance(n + 1);
				this.tablePortlet.addRow(n, df.format(n), tf.format(n), wf.format(n), mf.format(n), tz);
			}
		}
		if (SH.is(s) && this.div.getOffset() == 1)
			this.div.setOffsetFromBottomPx(300);
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		if (keycode == 13 && mask == FormPortlet.KEY_ALT) {
			runEval(true);
		}
	}

}
