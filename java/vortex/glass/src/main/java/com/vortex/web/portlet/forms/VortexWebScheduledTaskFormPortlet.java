package com.vortex.web.portlet.forms;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import com.f1.base.TableListenable;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletMultiSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletMultiSelectField.Option;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.NumberWebCellFormatter;
import com.f1.utils.CronTab;
import com.f1.utils.IntArrayList;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.formatter.BasicDateFormatter;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexEyeScheduledTask;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageScheduledTaskRequest;
import com.vortex.client.VortexClientBackup;
import com.vortex.client.VortexClientDbServer;
import com.vortex.client.VortexClientDeployment;
import com.vortex.client.VortexClientScheduledTask;
import com.vortex.web.VortexWebEyeService;

public class VortexWebScheduledTaskFormPortlet extends GridPortlet implements FormPortletListener {
	final private VortexWebEyeService service;

	final public FormPortletButton submitButton;
	private Long editId;
	final public FormPortletSelectField<Long> deploymentField;
	final public FormPortletSelectField<Byte> typeField;
	final public FormPortletTextField descriptionField;
	final public FormPortletTextField commandField;
	final private FormPortletSelectField<Long> backupField;
	final private FormPortletSelectField<Long> dbField;
	final private FormPortletSelectField<String> timezoneField;
	final private FormPortletMultiSelectField<Long> weekdaysField;
	final private FormPortletMultiSelectField<Long> hoursField;
	final private FormPortletMultiSelectField<Long> minutesField;
	final private FormPortletMultiSelectField<Long> secondsField;
	final private FormPortletMultiSelectField<Long> monthsField;
	final private FormPortletMultiSelectField<Long> weekInMonthsField;
	final private FormPortletMultiSelectField<Long> weekInYearsField;
	final private FormPortletMultiSelectField<Long> dayInMonthsField;
	final private FormPortletTextAreaField commentsField;
	//final private FormPortletMultiSelectField<Long> dayInYearsField;
	final private FormPortletMultiSelectField<Long> dayOfWeekInMonthsField;
	final private FormPortletSelectField<Byte> stateField;
	final private FormPortletButton showTimetableButton;

	private VortexWebMetadataFormPortlet form;
	private FormPortlet buttonForm;
	private FormPortlet monthForm;
	private FormPortlet weekdayForm;
	private FormPortlet hoursForm;
	private FormPortlet minutesForm;
	private FormPortlet secondsForm;

	private FormPortlet weekInMonthsForm;

	private FormPortlet weekInYearsForm;

	private FormPortlet dayInMonthsForm;

	private FormPortlet dayOfWeekInMonthsForm;

	public VortexWebScheduledTaskFormPortlet(PortletConfig config) {
		super(config);
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		this.form = new VortexWebMetadataFormPortlet(generateConfig(), "schedule.jpg");
		this.form.getCommentField().setHeight(40);
		this.buttonForm = new FormPortlet(generateConfig());
		this.monthForm = new FormPortlet(generateConfig());
		this.weekdayForm = new FormPortlet(generateConfig());
		this.hoursForm = new FormPortlet(generateConfig());
		this.minutesForm = new FormPortlet(generateConfig());
		this.secondsForm = new FormPortlet(generateConfig());
		buttonForm.addButton(this.submitButton = new FormPortletButton("Create Scheduled Task"));
		buttonForm.addButton(this.showTimetableButton = new FormPortletButton("Preview timetable of upcoming 1000 entries"));

		form.addField(this.descriptionField = new FormPortletTextField("Description")).setValue("").setWidth(300);
		form.addField(this.commentsField = new FormPortletTextAreaField("Comments")).setValue("").setHeight(40);
		form.addField(this.timezoneField = new FormPortletSelectField<String>(String.class, "Timezone"));
		form.addField(this.stateField = new FormPortletSelectField<Byte>(Byte.class, "State"));
		this.form.setIconToAdd();
		this.stateField.addOption(VortexEyeScheduledTask.STATE_ACTIVE, "active");
		this.stateField.addOption(VortexEyeScheduledTask.STATE_PAUSED, "paused");
		TimeZone tz = getManager().getState().getWebState().getFormatter().getTimeZone();
		for (String id : TimeZone.getAvailableIDs()) {
			this.timezoneField.addOption(id, id + "-" + TimeZone.getTimeZone(id).getDisplayName());
		}
		if (tz != null)
			this.timezoneField.setValue(tz.getID());

		int height = 10;
		monthForm.addField(this.monthsField = new FormPortletMultiSelectField<Long>(Long.class, ""));
		monthsField.addOption(0L, "January");
		monthsField.addOption(1L, "February");
		monthsField.addOption(2L, "March");
		monthsField.addOption(3L, "April");
		monthsField.addOption(4L, "May");
		monthsField.addOption(5L, "June");
		monthsField.addOption(6L, "July");
		monthsField.addOption(7L, "August");
		monthsField.addOption(8L, "September");
		monthsField.addOption(9L, "October");
		monthsField.addOption(10L, "November");
		monthsField.addOption(11L, "December");
		monthsField.setSize(height);

		weekdayForm.addField(this.weekdaysField = new FormPortletMultiSelectField<Long>(Long.class, ""));
		weekdaysField.addOption(0L, "Sunday");
		weekdaysField.addOption(1L, "Monday");
		weekdaysField.addOption(2L, "Tuesday");
		weekdaysField.addOption(3L, "Wednesday");
		weekdaysField.addOption(4L, "Thursday");
		weekdaysField.addOption(5L, "Friday");
		weekdaysField.addOption(6L, "Saturday");
		weekdaysField.setSize(height);

		hoursForm.addField(this.hoursField = new FormPortletMultiSelectField<Long>(Long.class, ""));
		hoursField.addOption(0L, "12 AM (Midnight)");
		for (int i = 1; i < 12; i++)
			hoursField.addOption(0L + i, i + " AM");
		hoursField.addOption(12L, "12 PM (Noon)");
		for (int i = 1; i < 12; i++)
			hoursField.addOption(12L + i, i + " PM");
		hoursField.setSize(height);

		minutesForm.addField(this.minutesField = new FormPortletMultiSelectField<Long>(Long.class, ""));
		for (int i = 0; i < 10; i++)
			minutesField.addOption(0L + i, ":0" + i);
		for (int i = 10; i < 60; i++)
			minutesField.addOption(0L + i, ":" + i);
		minutesField.setSize(height);

		secondsForm.addField(this.secondsField = new FormPortletMultiSelectField<Long>(Long.class, ""));
		for (int i = 0; i < 10; i++)
			secondsField.addOption(0L + i, ":0" + i);
		for (int i = 10; i < 60; i++)
			secondsField.addOption(0L + i, ":" + i);
		secondsField.setSize(height);

		weekInMonthsForm = new FormPortlet(generateConfig());
		weekInMonthsForm.addField(this.weekInMonthsField = new FormPortletMultiSelectField<Long>(Long.class, ""));
		fillOptions(this.weekInMonthsField, 6, "week of the month");

		weekInYearsForm = new FormPortlet(generateConfig());
		weekInYearsForm.addField(this.weekInYearsField = new FormPortletMultiSelectField<Long>(Long.class, ""));
		fillOptions(this.weekInYearsField, 53, "week of the year");

		dayInMonthsForm = new FormPortlet(generateConfig());
		dayInMonthsForm.addField(this.dayInMonthsField = new FormPortletMultiSelectField<Long>(Long.class, ""));
		fillOptions(this.dayInMonthsField, 31, "day of the month");

		//addField(this.dayInYearsField = new FormPortletMultiSelectField<Long>(Long.class, "Day of the Year(s)"));
		//fillOptions(this.dayInYearsField, 366, "day of the year");

		dayOfWeekInMonthsForm = new FormPortlet(generateConfig());
		dayOfWeekInMonthsForm.addField(this.dayOfWeekInMonthsField = new FormPortletMultiSelectField<Long>(Long.class, ""));
		fillOptions(this.dayOfWeekInMonthsField, 6, "weekday in the month");

		form.addField(this.typeField = new FormPortletSelectField<Byte>(Byte.class, "Type"));
		this.typeField.addOption(VortexEyeScheduledTask.TYPE_START, "Start application");
		this.typeField.addOption(VortexEyeScheduledTask.TYPE_STOP, "Stop application");
		this.typeField.addOption(VortexEyeScheduledTask.TYPE_SCRIPT, "Script");
		this.typeField.addOption(VortexEyeScheduledTask.TYPE_BACKUP, "Backup");
		this.typeField.addOption(VortexEyeScheduledTask.TYPE_DATABASE_INSPECT, "Inspect Database");

		form.addField(this.deploymentField = new FormPortletSelectField<Long>(Long.class, "Associated Deployment"));
		for (VortexClientDeployment i : service.getAgentManager().getDeployments())
			this.deploymentField.addOption((i.getData().getId()), i.getDescription());

		form.addField(this.commandField = new FormPortletTextField("Command")).setValue("").setWidth(FormPortletTextField.WIDTH_STRETCH);

		form.addField(this.backupField = new FormPortletSelectField<Long>(Long.class, "Backup"));
		for (VortexClientBackup i : service.getAgentManager().getBackups())
			this.backupField.addOption((i.getData().getId()), i.getDescription());

		form.addField(this.dbField = new FormPortletSelectField<Long>(Long.class, "Database server"));
		for (VortexClientDbServer i : service.getAgentManager().getDbServers())
			this.dbField.addOption((i.getData().getId()), i.getDescription());
		form.addFormPortletListener(this);
		form.initMetadataFormFields(VortexAgentEntity.TYPE_SCHEDULED_TASK);
		buttonForm.addFormPortletListener(this);
		addChild(form, 0, 0, 9, 1);

		String css = "portal_form";
		addChild(new HtmlPortlet(generateConfig(), "<B>Hours:</B>", css), 0, 1);
		addChild(hoursForm, 0, 2, 1, 1);
		setColSize(0, 120);

		addChild(new HtmlPortlet(generateConfig(), "<B>Minutes:</B>", css), 1, 1);
		addChild(minutesForm, 1, 2, 1, 1);
		setColSize(1, 70);

		addChild(new HtmlPortlet(generateConfig(), "<B>Seconds:</B>", css), 2, 1);
		addChild(secondsForm, 2, 2, 1, 1);
		setColSize(2, 70);

		addChild(new HtmlPortlet(generateConfig(), "Months:", css), 3, 1);
		addChild(monthForm, 3, 2, 1, 1);
		setColSize(3, 100);

		addChild(new HtmlPortlet(generateConfig(), "Weekdays:", css), 4, 1);
		addChild(weekdayForm, 4, 2, 1, 1);
		setColSize(4, 100);

		addChild(new HtmlPortlet(generateConfig(), "Week of Month:", css), 5, 1);
		addChild(weekInMonthsForm, 5, 2, 1, 1);
		setColSize(5, 160);

		addChild(new HtmlPortlet(generateConfig(), "Week of Year:", css), 6, 1);
		addChild(weekInYearsForm, 6, 2, 1, 1);
		setColSize(6, 180);

		addChild(new HtmlPortlet(generateConfig(), "Day of Month:", css), 7, 1);
		addChild(dayInMonthsForm, 7, 2, 1, 1);
		setColSize(7, 180);

		addChild(new HtmlPortlet(generateConfig(), "Weekday In Month:", css), 8, 1);
		addChild(dayOfWeekInMonthsForm, 8, 2, 1, 1);

		addChild(buttonForm, 0, 3, 9, 1);
		setRowSize(0, 480);
		setRowSize(1, 20);
		//setRowSize(2, 650);
		setRowSize(3, 50);
		updateFieldsForSelectedType();
		setSuggestedSize(1200, 750);
	}
	private void fillOptions(FormPortletMultiSelectField<Long> sink, int cnt, String desc) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < cnt; i++) {
			int num = i + 1;
			sb.append(num);
			switch (num % 10) {
				case 1:
					sb.append("st ");
					break;
				case 2:
					sb.append("nd ");
					break;
				case 3:
					sb.append("rd ");
					break;
				default:
					sb.append("th ");
			}
			sink.addOption((long) i, SH.toStringAndClear(sb.append(desc)));
		}
		sink.setSize(Math.min(cnt, 10));
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (hoursField.getValue().isEmpty() || minutesField.getValue().isEmpty() || secondsField.getValue().isEmpty()) {
			getManager().showAlert("You must select at least one hour, minute and second");
			return;
		}
		if (button == submitButton) {
			VortexEyeManageScheduledTaskRequest request = nw(VortexEyeManageScheduledTaskRequest.class);
			request.setScheduledTask(toScheduledTask());
			if (request.getScheduledTask() == null)
				return;
			service.sendRequestToBackend(getPortletId(), request);
			close();
		} else if (button == showTimetableButton) {
			TableListenable table = new BasicTable(new Object[] { "seq", "date" });
			VortexEyeScheduledTask st = toScheduledTask();
			if (st == null)
				return;
			long last = getManager().getState().getWebState().getPartition().getContainer().getTools().getNow();
			for (int i = 0; i < 1000; i++) {
				long next = getNextOccurency(st, last + 1000);
				if (next == -1) {
					getManager().showAlert("Conflicting configuration.  This job may not run");
					return;
				}
				table.getRows().addRow(i + 1, next);
				last = next;
			}
			table.setTitle("Scheduled tasks");
			FastWebTable ftable = new FastWebTable(new BasicSmartTable(table), getManager().getTextFormatter());
			ftable.addColumn(true, "Sequence", "seq", service.getNumberFormatter()).setCssColumn("bold");
			ftable.addColumn(true, "Date", "date", service.getDateTimeWebCellFormatter()).setCssColumn("bold");
			ftable.addColumn(true, "Year", "date", new NumberWebCellFormatter(new BasicDateFormatter("yyyy")));
			ftable.addColumn(true, "Month", "date", new NumberWebCellFormatter(new BasicDateFormatter("MMMMM")));
			ftable.addColumn(true, "Day Of Month", "date", new NumberWebCellFormatter(new BasicDateFormatter("dd"))).setWidth(100);
			ftable.addColumn(true, "Weekday", "date", new NumberWebCellFormatter(new BasicDateFormatter("EEEEE")));
			ftable.addColumn(true, "Hour", "date", new NumberWebCellFormatter(new BasicDateFormatter("hh_aa"))).setWidth(50);
			ftable.addColumn(true, "Min.", "date", new NumberWebCellFormatter(new BasicDateFormatter("mm"))).setWidth(30);
			ftable.addColumn(true, "Sec.", "date", new NumberWebCellFormatter(new BasicDateFormatter("ss"))).setWidth(30);
			ftable.addColumn(true, "Week of Year", "date", new NumberWebCellFormatter(new BasicDateFormatter("w"))).setWidth(100);
			ftable.addColumn(true, "Week of Month", "date", new NumberWebCellFormatter(new BasicDateFormatter("W"))).setWidth(110);
			ftable.addColumn(true, "Weekday of Month", "date", new NumberWebCellFormatter(new BasicDateFormatter("F"))).setWidth(120);
			FastTablePortlet p = new FastTablePortlet(generateConfig(), ftable);
			GridPortlet p2 = new GridPortlet(generateConfig());
			p2.addChild(p, 0, 0);
			p2.setSuggestedSize(p.getSuggestedWidth(getManager().getPortletMetrics()), 600);
			p2.setTitle("Timetable of next " + table.getSize() + " scheduled runs");
			getManager().showDialog(null, p2);
		}

	}
	private VortexEyeScheduledTask toScheduledTask() {
		VortexEyeScheduledTask r = nw(VortexEyeScheduledTask.class);
		if (this.editId != null)
			r.setId(this.editId);
		byte type = this.typeField.getValue();
		r.setType(type);
		switch (type) {
			case VortexEyeScheduledTask.TYPE_BACKUP:
				r.setDeploymentId(VortexEyeScheduledTask.NO_DEPLOYMENT);
				VortexClientBackup backup = service.getAgentManager().getBackup(backupField.getValue());
				VortexClientDeployment dep = backup.getDeployment();
				if (dep != null)
					r.setDeploymentId(dep.getId());
				r.setTargetId(backupField.getValue());
				break;
			case VortexEyeScheduledTask.TYPE_DATABASE_INSPECT:
				r.setDeploymentId(VortexEyeScheduledTask.NO_DEPLOYMENT);
				r.setTargetId(dbField.getValue());
				break;
			case VortexEyeScheduledTask.TYPE_SCRIPT:
				r.setDeploymentId(deploymentField.getValue());
				r.setCommand(commandField.getValue());
				break;
			case VortexEyeScheduledTask.TYPE_START:
				r.setDeploymentId(deploymentField.getValue());
				break;
			case VortexEyeScheduledTask.TYPE_STOP:
				r.setDeploymentId(deploymentField.getValue());
				break;
		}
		r.setTimezone(this.timezoneField.getValue());
		r.setState(this.stateField.getValue());
		r.setDayInMonths((int) toMask(this.dayInMonthsField));
		r.setDayOfWeekInMonths((byte) toMask(this.dayOfWeekInMonthsField));
		//TODO:r.setDayOfYears(dayOfYear);
		r.setHours((int) toMask(this.hoursField));
		r.setMinutes(toMask(this.minutesField));
		r.setMonthInYears((short) toMask(this.monthsField));
		r.setSeconds(toMask(this.secondsField));
		r.setWeekdays((byte) toMask(this.weekdaysField));
		r.setWeekInMonths((byte) toMask(this.weekInMonthsField));
		r.setWeekInYears(toMask(this.weekInYearsField));
		r.setDescription(this.descriptionField.getValue());
		r.setComments(this.commentsField.getValue());
		if (!form.populateMetadata(r))
			return null;
		return r;
	}
	private long toMask(FormPortletMultiSelectField<Long> field) {
		long r = 0;
		for (long value : field.getValue())
			r |= (1L << value);
		return r;
	}
	private void fromMask(FormPortletMultiSelectField<Long> field, long value) {
		Set<Long> values = new HashSet<Long>();
		for (Option<Long> option : field.getOptions()) {
			long bit = option.getKey();
			if (MH.allBits(value, (1L << bit)))
				values.add(bit);
		}
		field.setValue(values);
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.typeField) {
			updateFieldsForSelectedType();
		}
	}

	private void updateFieldsForSelectedType() {
		this.form.removeFieldNoThrow(commandField);
		this.form.removeFieldNoThrow(deploymentField);
		this.form.removeFieldNoThrow(backupField);
		this.form.removeFieldNoThrow(dbField);
		byte value = this.typeField.getValue();
		switch (value) {
			case VortexEyeScheduledTask.TYPE_BACKUP:
				this.form.addField(backupField);
				break;
			case VortexEyeScheduledTask.TYPE_DATABASE_INSPECT:
				this.form.addField(dbField);
				break;
			case VortexEyeScheduledTask.TYPE_SCRIPT:
				this.form.addField(deploymentField);
				this.form.addField(commandField);
				break;
			case VortexEyeScheduledTask.TYPE_START:
				this.form.addField(deploymentField);
				break;
			case VortexEyeScheduledTask.TYPE_STOP:
				this.form.addField(deploymentField);
				break;
		}

	}

	public void setScheduledTaskToCopy(VortexClientScheduledTask cst) {
		VortexEyeScheduledTask st = cst.getData();
		this.form.setIconToCopy();
		this.typeField.setValue(st.getType());
		this.descriptionField.setValue(st.getDescription());
		this.commentsField.setValue(st.getComments());
		switch (st.getType()) {
			case VortexEyeScheduledTask.TYPE_BACKUP:
				backupField.setValueNoThrow(st.getTargetId());
				deploymentField.setValueNoThrow(st.getDeploymentId());
				break;
			case VortexEyeScheduledTask.TYPE_DATABASE_INSPECT:
				dbField.setValueNoThrow(st.getTargetId());
				break;
			case VortexEyeScheduledTask.TYPE_SCRIPT:
				deploymentField.setValueNoThrow(st.getDeploymentId());
				commandField.setValue(st.getCommand());
				break;
			case VortexEyeScheduledTask.TYPE_START:
				deploymentField.setValueNoThrow(st.getDeploymentId());
				break;
			case VortexEyeScheduledTask.TYPE_STOP:
				deploymentField.setValueNoThrow(st.getDeploymentId());
				break;
		}

		commandField.setValue(st.getCommand());
		this.timezoneField.setValueNoThrow(st.getTimezone());
		this.stateField.setValueNoThrow(st.getState());
		fromMask(this.dayInMonthsField, st.getDayInMonths());
		fromMask(this.dayOfWeekInMonthsField, st.getDayOfWeekInMonths());
		fromMask(this.hoursField, st.getHours());
		fromMask(this.minutesField, st.getMinutes());
		fromMask(this.monthsField, st.getMonthInYears());
		fromMask(this.secondsField, st.getSeconds());
		fromMask(this.weekdaysField, st.getWeekdays());
		fromMask(this.weekInMonthsField, st.getWeekInMonths());
		fromMask(this.weekInYearsField, st.getWeekInYears());
		form.populateMetadataFormFields(st);
		updateFieldsForSelectedType();
	}
	public void setScheduledTaskToEdit(VortexClientScheduledTask cst) {
		setScheduledTaskToCopy(cst);
		this.form.setIconToEdit();
		this.editId = cst.getId();
		this.submitButton.setName("Update scheduled task");
	}

	private long getNextOccurency(VortexEyeScheduledTask st, long now) {
		TimeZone tz = TimeZone.getTimeZone(st.getTimezone());
		int[] months = fromMask(st.getMonthInYears());
		int[] days = fromMask(st.getDayOfYears());
		int[] weekdays = fromMask(st.getWeekdays());
		int[] hours = fromMask(st.getHours());
		int[] minutes = fromMask(st.getMinutes());
		int[] seconds = fromMask(st.getSeconds());
		CronTab ct = new CronTab(months, days, weekdays, hours, minutes, seconds, tz);
		long r = ct.calculateNextOccurance(now);

		for (int count = 0;; count++) {
			if (count > 1000)
				return -1;
			boolean skip = false;
			if (st.getWeekInYears() != 0 && !MH.allBits(st.getWeekInYears(), 1 << (ct.getWeekInYear(r) - 1))) {
				skip = true;
			} else if (st.getWeekInMonths() != 0 && !MH.allBits(st.getWeekInMonths(), 1 << (ct.getWeekInMonth(r) - 1))) {
				skip = true;
			} else if (st.getDayInMonths() != 0 && !MH.allBits(st.getDayInMonths(), 1 << (ct.getDayInMonth(r) - 1))) {
				skip = true;
			} else if (st.getDayOfWeekInMonths() != 0 && !MH.allBits(st.getDayOfWeekInMonths(), 1 << (ct.getDayOfWeekInMonth(r) - 1))) {
				skip = true;
			}
			if (skip) {
				ct = new CronTab(months, days, weekdays, hours, minutes, seconds, tz);
				r = ct.calculateNextOccurance(ct.getNextDay(r));
			} else
				break;
		}
		return r;
	}
	private int[] fromMask(long value) {
		if (value == 0)
			return OH.EMPTY_INT_ARRAY;
		IntArrayList r = new IntArrayList(63);
		for (int i = 0; i < 63; i++) {
			if (MH.allBits(value, (1L << i)))
				r.add(i);
		}
		return r.toIntArray();
	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		// TODO Auto-generated method stub

	}
}
