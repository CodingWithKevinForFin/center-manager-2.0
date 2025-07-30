package com.f1.tester;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.container.Container;
import com.f1.container.inspect.RecordingDispatchInspector.RecordedEvent;
import com.f1.utils.Formatter;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.OH;
import com.f1.utils.TextMatcher;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.impl.TextMatcherFactory;

public class TesterUtils {

	public static List<RecordedEvent> findRecorded(List<RecordedEvent> allRecorded, String processorRegexOrNull, String partitionIdRegexOrNull) {
		final TextMatcher processorMatcher = TextMatcherFactory.DEFAULT.toMatcher(processorRegexOrNull);
		final TextMatcher partitionIdMatcher = TextMatcherFactory.DEFAULT.toMatcher(partitionIdRegexOrNull);
		final List<RecordedEvent> r = new ArrayList<RecordedEvent>();
		for (final RecordedEvent event : allRecorded)
			if (processorMatcher.matches(event.getProcessor().getFullName()) && partitionIdMatcher.matches(OH.toString(event.getAction())))
				r.add(event);
		return r;
	}

	public static String toJson(Container container, List<RecordedEvent> recordedEvents, ObjectToJsonConverter converter) {
		final Formatter formatter = container.getServices().getLocaleFormatter().getDateFormatter(LocaleFormatter.DATETIME_FULL);
		final List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		for (RecordedEvent event : recordedEvents) {
			final Map<String, Object> m = new HashMap<String, Object>();
			m.put("when", formatter.format(new Date(event.getTimeNs() / 1000000)));
			m.put("action", event.getAction());
			m.put("processor", event.getProcessor().getFullName());
			results.add(m);
		}
		return new String(converter.objectToString(results));
	}

}

