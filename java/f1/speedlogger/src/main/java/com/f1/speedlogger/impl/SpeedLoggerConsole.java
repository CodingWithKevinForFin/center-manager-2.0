package com.f1.speedlogger.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Collection;
import java.util.Queue;

import com.f1.base.Console;
import com.f1.speedlogger.SpeedLogger;
import com.f1.speedlogger.SpeedLogger2Streams;
import com.f1.speedlogger.SpeedLoggerAppender;
import com.f1.speedlogger.SpeedLoggerManager;
import com.f1.speedlogger.SpeedLoggerSink;
import com.f1.speedlogger.SpeedLoggerStream;
import com.f1.utils.ArgParser;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;
import com.f1.utils.impl.TextMatcherFactory;

@Console(name = "SpeedLogger", help = "Used to inspect and modify speed logger configuration at run time.")
public class SpeedLoggerConsole {

	private static ArgParser grepOptions = new ArgParser("Grep Options");
	static {
		grepOptions.addSwitchOptional("A", "after-context", "/[0-9]+/", "Print [num] lines trailing after matching lines.");
		grepOptions.addSwitchOptional("B", "before-context", "/[0-9]+/", "Print [num] lines before matching lines.");
		grepOptions.addSwitchOptional("m", "max-count", "/[0-9]+/", "Print at most matching occurences.");
		grepOptions.addSwitchOptional("p", "preserve", "/[0-9]+/", "Preserve embedded \\\\n and \\\\r.");
		grepOptions.addSwitchOptional("n", "line-number", null, "Prefix each line with line number.");
	}

	private SpeedLoggerManager manager;

	public SpeedLoggerConsole(SpeedLoggerManager manager) {
		this.manager = manager;
	}

	@Console(help = "shows all the active sinks and their configurations")
	public String showSinks() {
		StringBuilder sb = new StringBuilder();
		for (String sinkId : CH.sort(manager.getSinkIds())) {
			SpeedLoggerSink sink = manager.getSink(sinkId);
			sb.append(" ** ").append(sinkId).append(SH.NEWLINE);
			for (String key : CH.sort(sink.getConfiguration().keySet()))
				sb.append("    " + key + ": " + sink.getConfiguration().get(key)).append(SH.NEWLINE);
		}
		return sb.toString();
	}

	@Console(help = "grep through a file sink for given text.\nOptions are -A=# linesAfter -B=# linesBefore -m=# max_count -p preserve -n line numbers", params = { "sinkName",
			"searchExpresion", "options" })
	public String grep(String sinkName, String searchExpression, String options) throws IOException {
		ArgParser.Arguments args = grepOptions.parse(options);
		boolean prefixLines = args.hasOption("n");
		boolean preserve = args.hasOption("p");
		int linesBefore = args.getOptional("B", 0);
		int linesAfter = args.getOptional("A", 0);
		int maxCount = args.getOptional("m", -1);
		SpeedLoggerSink sink = manager.getSink(sinkName);
		String fileName = sink.getConfiguration().get(FileSpeedLoggerSinkFactory.OPTION_FILENAME);
		if (fileName == null)
			return "<Must be a File Sink: " + sinkName + ">";
		StringBuilder sb = new StringBuilder();
		final TextMatcher searcher = TextMatcherFactory.DEFAULT.toMatcher(searchExpression);
		Queue<String> queue = new java.util.LinkedList<String>();
		LineNumberReader reader = new LineNumberReader(new FileReader(new File(fileName)));
		String line;
		int follow = 0;
		final boolean mark = linesBefore > 0 || linesAfter > 0;
		final String noMarkStr = mark ? "  " : "";
		final String markStr = mark ? "**" : "";
		while ((line = reader.readLine()) != null) {
			if (searcher.matches(line) && maxCount != 0) {
				if (maxCount > 0)
					maxCount--;
				if (mark && queue.size() > 0)
					sb.append(SH.NEWLINE).append("----------").append(SH.NEWLINE).append(SH.NEWLINE);
				int lineNumber = reader.getLineNumber() - queue.size();
				while (!queue.isEmpty()) {
					appendLine(preserve, prefixLines ? (lineNumber++) : -1, noMarkStr, queue.remove(), sb);
				}
				appendLine(preserve, prefixLines ? lineNumber : -1, markStr, line, sb);
				follow = linesAfter;
			} else if (follow > 0) {
				appendLine(preserve, prefixLines ? reader.getLineNumber() : -1, noMarkStr, line, sb);
				follow--;
			} else if (linesBefore > 0) {
				queue.add(line);
				if (queue.size() > linesBefore)
					queue.remove();
			} else if (maxCount == 0)
				break;
		}
		return sb.toString();
	}

	private static void appendLine(boolean preserve, int lineNumber, String prefix, String line, StringBuilder sink) {
		prefix = lineNumber < 0 ? prefix : SH.rightAlign('0', SH.toString(lineNumber), 8, false, new StringBuilder(prefix)).toString();
		if (!preserve) {
			line = BasicSpeedLoggerAppender.replaceLfcr(line);
			line = SH.prefixLines(line, prefix, false);
		}
		if (lineNumber >= 0)
			sink.append(prefix).append(' ');
		sink.append(line);
		sink.append(SH.NEWLINE);
	}

	public String showLoggers() {
		return showLoggers(null);
	}

	@Console(help = "Show configuration for existing loggers whose ids' match supplied pattern", params = { "idPattern" })
	public String showLoggers(String idPattern) {
		TextMatcher matcher = TextMatcherFactory.DEFAULT.toMatcher(idPattern);
		StringBuilder sb = new StringBuilder();
		for (String loggerId : CH.sort(manager.getLoggerIds())) {
			if (!matcher.matches(loggerId))
				continue;
			SpeedLogger logger = manager.getLogger(loggerId);
			SpeedLogger2Streams l2s = (SpeedLogger2Streams) logger;
			Collection<SpeedLoggerStream> streams = l2s.getStreams();
			if (streams.isEmpty()) {
				sb.append(" ** ").append(loggerId);
				sb.append("  -->  OFF").append(SH.NEWLINE);
			} else {
				for (SpeedLoggerStream stream : l2s.getStreams()) {
					sb.append(" ** ").append(loggerId);
					sb.append(": if( " + SpeedLoggerUtils.getLevelAsString(stream.getMinimumLevel()) + " ) --> " + stream.getSinkId()).append(SH.NEWLINE);
				}
			}
		}
		return sb.toString();

	}

	@Console(help = "set the level for a particular sink on existing loggers whose ids' match supplied pattern.  ", params = { "loggerIdPattern", "level", "sinkId" })
	public String setLevel(String loggerIdPattern, String level, String sinkId) {
		TextMatcher matcher = SH.m(loggerIdPattern);
		final int levelInt = SpeedLoggerUtils.parseLevel(level);
		final SpeedLoggerSink sink = manager.getSink(sinkId);
		final SpeedLoggerAppender appender = manager.getAppender(manager.getAppenderIds().iterator().next());
		for (String loggerId : CH.sort(manager.getLoggerIds())) {
			if (!matcher.matches(loggerId))
				continue;
			final SpeedLogger2Streams logger = (SpeedLogger2Streams) manager.getLogger(loggerId);
			synchronized (logger) {
				//TODO: I THINK THIS IS WRONG, WE NEED TO UPDATE SpeedLoggerManager To
				SpeedLoggerStream stream = new BasicSpeedLoggerStream(loggerId, manager, appender, sink, levelInt);
				logger.addStreams(CH.l(stream));
			}
		}
		return showLoggers(loggerIdPattern);

	}

}
