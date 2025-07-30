/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.f1.speedlogger.SpeedLoggerLevels;
import com.f1.speedlogger.SpeedLoggerStream;

public class BasicSpeedLoggerConfig implements Comparator<SpeedLoggerStream> {

	final public List<SpeedLoggerStream> streams;
	final public int minLevelNeedsTime, minLevelNeedsStackTrace;
	final public int minimumLevel;

	public BasicSpeedLoggerConfig(Collection<SpeedLoggerStream> streams) {
		ArrayList<SpeedLoggerStream> s = new ArrayList<SpeedLoggerStream>(streams);
		int minLevelNeedsStackTrace = SpeedLoggerLevels.OFF, minLevelNeedsTime = SpeedLoggerLevels.OFF;
		for (SpeedLoggerStream stream : s) {
			int level = stream.getMinimumLevel();
			if (stream.getRequiresStackTrace())
				minLevelNeedsStackTrace = Math.min(minLevelNeedsStackTrace, level);
			if (stream.getRequiresTimeMs())
				minLevelNeedsTime = Math.min(minLevelNeedsTime, level);
		}
		this.minLevelNeedsStackTrace = minLevelNeedsStackTrace;
		this.minLevelNeedsTime = minLevelNeedsTime;
		if (streams.size() > 1)
			Collections.sort(s, this);
		this.streams = Collections.unmodifiableList(s);
		minimumLevel = streams.size() == 0 ? SpeedLoggerLevels.OFF : this.streams.get(0).getMinimumLevel();
	}

	@Override
	public int compare(SpeedLoggerStream arg0, SpeedLoggerStream arg1) {
		return arg0.getMinimumLevel() - arg1.getMinimumLevel();
	}
}
