package com.vortex.testtrackagent;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.impl.StringCharReader;
import com.f1.vortexcommon.msg.agent.VortexAgentMachineEventStats;

public class Centos5CommandRunner extends LinuxCommandRunner {

	public Centos5CommandRunner(PropertyController props) {
		super(props);
		// TODO Auto-generated constructor stub
	}
	@Override
	public List<VortexAgentMachineEventStats> runMachineEventStats(CommandRunnerState state, long onwards, byte level) {

		//		long id = getTools().getUidLong("AgentMachineEventStats");
		final List<VortexAgentMachineEventStats> stats = new ArrayList<VortexAgentMachineEventStats>();
		final String toExecute = sudo() + "last -d";
		final String stdout = new String(execute(toExecute).getB());
		final StringBuilder sb = new StringBuilder();
		String[] lines = SH.splitLines(stdout);
		String line;
		for (int i = 0; i < lines.length; i++) {

			line = lines[i];
			if (SH.isnt(line))
				continue;
			try {

				final StringCharReader reader = new StringCharReader(line);
				VortexAgentMachineEventStats stat = nw(VortexAgentMachineEventStats.class);

				reader.skip(' ');
				reader.readUntil(' ', SH.clear(sb));
				String name = sb.toString();
				if ("wtmp".equals(name))
					continue;

				reader.skip(' ');
				reader.readUntil(' ', SH.clear(sb));
				String msg = sb.toString();

				if ("system".equals(msg)) {
					reader.skip(' ');
					reader.readUntil(' ', SH.clear(sb));
					msg += " " + sb.toString();
				}

				reader.skip(' ');
				reader.readUntil(' ', SH.clear(sb));
				String host = sb.toString();
				Date date;
				long gen = 0;
				try {
					reader.skip(' ');
					reader.readChars(24, SH.clear(sb));
					date = state.parseSystemDateBasicVersion(sb.toString());
					gen = date.getTime();
				} catch (ParseException pe) {
					LH.warning(log, "Error parsing date: ", sb.toString());
				}

				reader.skip(' ');
				reader.readUntil(' ', SH.clear(sb));
				String qualifier = sb.toString();
				if ("-".equals(qualifier)) {
					reader.skip(' ');
					reader.mark();
					reader.readChars(24, SH.clear(sb));
					try {
						date = state.parseSystemDate(sb.toString());
						long end = date.getTime();
						stat.setEndTime(end);
					} catch (ParseException pe) {
						reader.returnToMark();
						reader.readUntil('(', SH.clear(sb));
						stat.setNotEnded(sb.toString());
					}
					reader.skip(StringCharReader.WHITE_SPACE);
					reader.expect('(');
					reader.readUntil(')', SH.clear(sb));
					stat.setDuration(sb.toString());
				} else {
					reader.readUntil('\n', sb);
					qualifier = sb.toString();
					stat.setNotEnded(qualifier);
				}

				stat.setLevel((byte) 6);
				stat.setTimeGenerated(gen);
				stat.setHost(host);
				//				stat.setId(id);
				stat.setMessage(msg);
				stat.setName("UNIX");
				stat.setUserName(name);
				stat.setSource("wtmp");

				stats.add(stat);
			} catch (Exception e) {
				LH.warning(log, "Error parsing 'last' cmd: ", stdout, e);
				return stats;
			}
		}

		return stats;
	}

}
