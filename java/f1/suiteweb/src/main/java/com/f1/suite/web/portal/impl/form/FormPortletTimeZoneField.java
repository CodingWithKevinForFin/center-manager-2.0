package com.f1.suite.web.portal.impl.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;

import com.f1.utils.EH;
import com.f1.utils.OH;

public class FormPortletTimeZoneField extends FormPortletSelectField<String> {
	public static final Comparator<TimeZone> ZONE_SORTER = new Comparator<TimeZone>() {
		@Override
		public int compare(TimeZone z1, TimeZone z2) {
			int r = OH.compare(z1.getRawOffset(), z2.getRawOffset());
			if (r != 0)
				return r;
			return OH.compare(z1.getID(), z2.getID());
		};
	};
	public static final String OPTION_LABEL_DEFAULT = "<Default>";

	public FormPortletTimeZoneField() {
		super(String.class, "Time Zone: ");
		this.initTimeZones();
	}
	public FormPortletTimeZoneField(String title) {
		super(String.class, title);
		this.initTimeZones();
	}
	private void initTimeZones() {
		//Get timezones
		Iterable<TimeZone> zones = EH.getTimeZones();
		List<TimeZone> zoneList = new ArrayList<TimeZone>();
		for (TimeZone z : zones) {
			zoneList.add(z);
		}
		Collections.sort(zoneList, ZONE_SORTER);

		//Add options
		String zoneId;
		int rawOffset;
		this.addOption(null, OPTION_LABEL_DEFAULT);
		for (TimeZone z : zoneList) {
			zoneId = z.getID();
			rawOffset = z.getRawOffset();
			this.addOption(zoneId, "(UTC" + (rawOffset >= 0 ? "+" : "") + (rawOffset / 3600000) + ":00) " + zoneId);
		}
	}
}
