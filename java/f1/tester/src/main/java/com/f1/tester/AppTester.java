package com.f1.tester;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

import com.f1.base.Ackable;
import com.f1.base.Console;
import com.f1.base.Message;
import com.f1.base.Pointer;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.base.ToStringable;
import com.f1.container.Container;
import com.f1.container.ContainerScope;
import com.f1.container.DispatchController;
import com.f1.container.Port;
import com.f1.container.Processor;
import com.f1.container.impl.ContainerHelper;
import com.f1.container.inspect.RecordingDispatchInspector;
import com.f1.container.inspect.RecordingDispatchInspector.RecordedEvent;
import com.f1.container.wrapper.InspectingDispatchController;
import com.f1.tester.diff.DiffResult;
import com.f1.tester.diff.DiffSession;
import com.f1.tester.diff.Differ;
import com.f1.tester.diff.DifferConstants;
import com.f1.tester.diff.IgnoreDiffer;
import com.f1.tester.diff.NumberedPatternDiffer;
import com.f1.tester.diff.RootDiffer;
import com.f1.tester.templates.TemplateRepo;
import com.f1.tester.templates.TemplateSession;
import com.f1.utils.DetailedException;
import com.f1.utils.Duration;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.VolatilePointer;
import com.f1.utils.agg.LongAggregator;
import com.f1.utils.impl.TextMatcherFactory;
import com.f1.utils.structs.table.BasicRow;
import com.f1.utils.structs.table.BasicTable;

@Console(help = "Provides a test harness for loading in templates / variables and sending them as messages into the f1 container")
public class AppTester {

	private TemplateRepo templatesRepo;
	private Container container;
	private TemplateSession session;
	private RecordingDispatchInspector inspector;

	private int ackId = 0;
	private Queue<TesterAcker> acked = new LinkedList<TesterAcker>();

	public AppTester(Container container) {

		this.container = container;
		templatesRepo = new TemplateRepo();
		session = new TemplateSession(templatesRepo);
		templatesRepo.getConverter().setIdeableGenerator(container.getServices().getGenerator());
	}

	public void setSkipTransience(byte skipTransience) {
		templatesRepo.getConverter().setSkipTransience(skipTransience);
	}

	private void initInspector() {
		DispatchController controller = container.getDispatchController();
		if (!(controller instanceof InspectingDispatchController))
			throw new RuntimeException("can not inject inspector... be sure you are using the " + InspectingDispatchController.class.getName()
					+ " dispatcher. ENABLE BY SETTING OPTION: f1.inspecting.mode=true");
		this.inspector = new RecordingDispatchInspector();
		((InspectingDispatchController) controller).addInspector(inspector);
		int types = RecordingDispatchInspector.DISPATCH;
		types |= RecordingDispatchInspector.FORWARD;
		types |= RecordingDispatchInspector.PARTITION_ADD;
		types |= RecordingDispatchInspector.REPLY;
		types |= RecordingDispatchInspector.START;
		types |= RecordingDispatchInspector.STOP;
		this.inspector.ignoreEventsMatching(types, ".*");
	}

	@Console(help = "Display the amount of time for each message to be acked")
	public String getAckStats() {
		LongAggregator la = new LongAggregator();
		la.setRunningAverageMaxSamples(100);
		synchronized (acked) {
			for (TesterAcker ta : acked)
				la.add(ta.getDuration());
		}
		return "ack latency (in milliseconds): " + la.toString();
	}

	@Console(help = "Reset recording of processors (stop all recording)")
	synchronized public void resetRecording() {
		if (this.inspector != null)
			this.inspector.resetRecoding();
	}

	@Console(help = "Start recording processors with names that match the supplied expression", params = { "processorNameexpression" })
	synchronized public void record(String expression) {
		if (this.inspector == null)
			initInspector();
		this.inspector.recordEventsMatching(RecordingDispatchInspector.PROCESS | RecordingDispatchInspector.THROWN, expression);
	}

	@Console(help = "Stop recording processors with names that match the supplied expression", params = { "processorNameexpression" })
	synchronized public void unrecord(String expression) {
		if (this.inspector == null)
			initInspector();
		this.inspector.ignoreEventsMatching(RecordingDispatchInspector.PROCESS | RecordingDispatchInspector.THROWN, expression);
	}

	@Console(help = "List which processors are being recorded")
	public String showRecorded() {
		if (inspector == null)
			return "<not recording>";
		Table<BasicRow> t = new BasicTable(String.class, "Processor", String.class, "action");
		TableList<BasicRow> rows = t.getRows();
		for (ContainerScope cs : ContainerHelper.getAllChildren(container)) {
			if (cs instanceof Processor) {
				for (Integer mask : RecordingDispatchInspector.EVENT_TYPES)
					if (inspector.shouldRecord(mask.intValue(), (Processor) cs))
						rows.addRow(cs.getFullName(), RecordingDispatchInspector.EVENT_TYPES.toDetailedString(mask));
			}
		}
		return t.toString();
	}

	synchronized public void setAckId(int ackId) {
		this.ackId = OH.assertGe(ackId, this.ackId);
	}

	@Console(help = "load variables from the list of supplied files", params = { "fileNames" })
	public void loadVariables(String... fileNames) throws IOException {
		for (String fileName : fileNames)
			loadVariables(new File(fileName));
	}

	public void loadVariables(File fileName) throws IOException {
		Properties properties = new Properties();
		properties.load(new StringReader(IOH.readText(fileName, true)));
		for (Map.Entry<Object, Object> e : properties.entrySet()) {
			putVariable(e.getKey().toString(), session.evaluate(e.getValue().toString() + ";"));
		}
	}

	@Console(help = "load templates from the list of supplied files", params = { "fileNames" })
	public void loadTemplates(String... fileNames) {
		for (String fileName : fileNames)
			loadTemplateAtMpath("", fileName);
	}

	public void loadTemplateAtMpath(String mpath, String fileName) {
		loadTemplates(mpath, new File(fileName));
	}

	synchronized private void loadTemplates(String mpath, File file)

	{
		if (file.isDirectory()) {
			for (File child : file.listFiles())
				loadTemplates(mpath, child);
		} else {
			templatesRepo.putFile(mpath, file);
		}
	}

	@Console(help = "list the acks that have been received, waiting for the supplied timeout", params = { "minCount", "maxCount", "timeoutMs" })
	synchronized public List<TesterAcker> popAcks(int minCount, int maxCount, long timeoutMs) {

		ArrayList<TesterAcker> acks = new ArrayList<TesterAcker>();
		long endTime = EH.currentTimeMillis() + timeoutMs;
		synchronized (acked) {
			while (OH.isBetween(acks.size(), minCount, maxCount)) {
				long now = EH.currentTimeMillis();
				TesterAcker ack = acked.poll();
				if (ack != null) {
					acks.add(ack);
				} else if (now < endTime) {
					OH.wait(acks, now - endTime);
				} else
					break;// no items on que and time has expired
			}
		}
		return acks;
	}

	@Console(help = "pop and return the events that have been recorded, waiting for the supplied timeout", params = { "minCount", "maxCount", "timeoutMs" })
	synchronized public List<RecordedEvent> popRecorded(int minCount, int maxCount, long timeoutMs) {
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>(maxCount);
		if (this.inspector != null) {
			this.inspector.waitForEvents(minCount, timeoutMs);
		}
		return this.inspector.flushEvents(maxCount);

	}

	@Console(help = "pop and return the events that have been recorded (as json), waiting for the supplied timeout", params = { "minCount", "maxCount", "timeoutMs" })
	public String popRecordedAsJson(int minCount, int maxCount, long timeoutMs) {
		return TesterUtils.toJson(container, popRecorded(minCount, maxCount, timeoutMs), templatesRepo.getConverter());
	}

	@Console(help = "add the supplied variables, nameValuePairs should be in the sequence: varName1,value1,varName2,value2,...", params = { "nameValuePairs" })
	synchronized public void putVariables(Object... nameValuePairs) {
		if (nameValuePairs.length % 2 != 0)
			throw new RuntimeException("must be even number of parameters (name1,value1,name2,value2,...)");
		for (int i = 0; i < nameValuePairs.length; i += 2)
			putVariable(nameValuePairs[i].toString(), nameValuePairs[i + 1]);
	}

	@Console(help = "add the supplied variable", params = { "name", "value" })
	synchronized public void putVariable(String name, Object value) {
		if (templatesRepo.getVariableInitialValue(name) == null)
			templatesRepo.putVariableInitialValue(name, value);
		session.putVariable(name, value);
	}

	@Console(help = "reset the variables from the variable files")
	synchronized public void resetSession() {
		session = new TemplateSession(templatesRepo);
	}

	@Console(help = "send the action(s) at the supplied path (from the template files) to the supplied name of the processor or port", params = { "pathOfTemplate",
			"processorOrPortPath" })
	synchronized public String sendAction(String mPath, String processorOrPortPath) {
		return sendAction(mPath, processorOrPortPath, 1, -1);
	}

	@Console(help = "display the action(s) at the supplied path", params = { "pathOfTemplate" })
	synchronized public String showAction(String mPath) {
		session.setGenerator(container.getServices().getGenerator());
		StringBuilder sb = new StringBuilder();
		List<Message> messageList = session.getMessages(mPath);
		if (messageList == null)
			return "message(s) not found at mPath: " + mPath;
		for (Message m : messageList)
			sb.append(m).append(SH.NEWLINE);
		return sb.toString();
	}

	/**
	 * Sends an action into the container
	 * 
	 * @param mPath
	 *            the mPath of the message within the template repo to be sent
	 * @param processorOrPortPath
	 *            the container path of the processor or port to send the message on
	 * @param count
	 *            the number of messages to send
	 * @param timeoutMs
	 *            the max amount of time to wait for an ack. <BR>
	 *            -1 = do not register acker <BR>
	 *            0 = async(return as soon as messages are sent) <BR>
	 *            >0 = wait at least specified time in milliseconds. if the specified period of time lapses and no acks are received then the method will return.
	 * @return
	 */
	@Console(help = "send the action(s) at the supplied path (from the template files) to the supplied name of the processor or port a specified number of time.  \nThen wait for all messages to be acked and report on performance stats (unless the timeout Period arrives first)", params = {
			"pathOfTemplate", "processorOrPortPath", "repeatCount", "timeoutMs" })
	synchronized public String sendAction(String mPath, String processorOrPortPath, int count, long timeoutMs)

	{
		try {
			if (count == 0)
				return "  ** sent 0 messages";
			boolean registerAcker = timeoutMs >= 0;
			session.setGenerator(container.getServices().getGenerator());
			templatesRepo.getConverter().setIdeableGenerator(OH.assertNotNull(container.getServices().getGenerator()));
			ContainerScope child = container.getSuiteController().getRootSuite().getChild(processorOrPortPath);
			if (child == null)
				return "child not found under root suite: " + processorOrPortPath;
			Processor processor;
			if (child instanceof Port)
				processor = ((Port<?>) child).getProcessor();
			else if (child instanceof Processor)
				processor = (Processor) child;
			else
				return "can't send messages to: " + child;

			Duration d = new Duration();
			int startAckId = ackId;
			Pointer<Long> lastTimePointer = new VolatilePointer<Long>(0L);
			List<Message> messages = new ArrayList<Message>(count);
			List<TesterAcker> ackers = new ArrayList<TesterAcker>(count);
			AtomicInteger remaining = timeoutMs > 0 ? new AtomicInteger(count) : null;
			for (int i = 0; i < count; i++) {
				List<Message> messageList = session.getMessages(mPath);
				if (messageList == null)
					return "message(s) not found at mPath: " + mPath;
				messages.addAll(messageList);
				if (registerAcker) {
					for (Message m : messageList) {
						m.putAckId(ackId++, true);
						TesterAcker acker = new TesterAcker(lastTimePointer, remaining, m);
						ackers.add(acker);
						m.registerAcker(acker);
						if (timeoutMs > 0)
							acker.enableNotify();
					}
				}
			}
			if (remaining != null)
				remaining.set(messages.size());
			long start = EH.currentTimeMillis();
			for (int i = 0; i < messages.size(); i++) {
				if (registerAcker)
					ackers.get(i).setStartTimeMillis(start);
				Message message = messages.get(i);
				container.getDispatchController().dispatch(null, processor, message, null, null);
			}

			String r = "";
			if (count == 1)
				r += "  ** sent 1 message: " + new String(templatesRepo.getConverter().object2Bytes(messages.get(0))) + SH.NEWLINE;
			else
				r += "  ** sent " + count + " messages. " + SH.NEWLINE;
			if (remaining != null) {
				TesterAcker last = ackers.get(ackers.size() - 1);
				lastTimePointer.put(EH.currentTimeMillis());
				synchronized (remaining) {
					while (remaining.get() > 0 && EH.currentTimeMillis() - lastTimePointer.get() < timeoutMs)
						OH.wait(remaining, timeoutMs);
				}
				LongAggregator la = new LongAggregator();
				synchronized (acked) {
					for (TesterAcker ta : ackers)
						if (ta.getEndTimeMillis() != 0)
							la.add(ta.getDuration());
				}
				if (la.getCount() < count)
					r += "  ** note: Timeout occurred.  Processed " + la.getCount() + " messages before timeout exceeded " + SH.NEWLINE;
				r += "  ** ack latency (in milliseconds): " + la.toString() + SH.NEWLINE;
				la.setRunningAverageMaxSamples(100);
				if (la.getCount() > 0)
					r += "  ** ack throughput: messages per second=" + (la.getCount() * 1000d / la.getMax()) + ", milliseconds per message=" + (double) la.getMax() / la.getCount()
							+ SH.NEWLINE;
			}
			if (registerAcker)
				r += "  ** ackids = [" + startAckId + " .. " + (ackId - 1) + "] " + SH.NEWLINE;
			else
				r += "  ** no ackers registered." + SH.NEWLINE;
			return r;
		} catch (Exception e) {
			throw new DetailedException("Error running test", e).set("mPath", mPath).set("processor / port", processorOrPortPath);
		}
	}

	private class TesterAcker implements com.f1.base.Acker, ToStringable {

		private final long ackableId;
		private long startTimeMillis;
		private long endTimeMillis;
		private Object result;
		private boolean shouldNotify = false;
		final private AtomicInteger remaining;
		final private Pointer<Long> lastTimePointer;

		public TesterAcker(Pointer<Long> lastTimePointer, AtomicInteger remaining, Ackable ackable) {
			this.lastTimePointer = lastTimePointer;
			this.ackableId = ackable.askAckId();
			this.remaining = remaining;
		}

		public void enableNotify() {
			this.shouldNotify = true;
		}

		public long getEndTimeMillis() {
			return endTimeMillis;
		}

		public void setStartTimeMillis(long currentTimeMillis) {
			this.startTimeMillis = currentTimeMillis;

		}

		public long getDuration() {
			if (endTimeMillis == 0)
				return -1;
			return endTimeMillis - startTimeMillis;
		}

		@Override
		public void ack(Ackable ackable, Object optionalResult) {
			OH.assertEq(this.ackableId, ackable.askAckId());
			if (endTimeMillis != 0)
				throw new RuntimeException("already acked!");
			this.result = optionalResult;
			endTimeMillis = EH.currentTimeMillis();
			lastTimePointer.put(endTimeMillis);
			synchronized (acked) {
				acked.add(this);
				acked.notify();
			}
			if (remaining != null) {
				if (remaining.decrementAndGet() == 0 && shouldNotify) {
					synchronized (remaining) {
						remaining.notify();
					}
				}
			}
		}

		@Override
		public String toString() {
			return toString(new StringBuilder()).toString();
		}

		@Override
		public StringBuilder toString(StringBuilder sb) {
			sb.append(ackableId);
			sb.append(", ");
			sb.append(getDuration()).append(" ms");
			if (result != null)
				sb.append("  result=").append(result);

			return sb;
		}
	}

	@Console(help = "list registered variables")
	public String showVariables() {
		StringBuilder sb = new StringBuilder();
		for (String i : session.getDeclaredVariables()) {
			sb.append(" ** " + i + "=" + session.getVariable(i)).append(SH.NEWLINE);
		}
		return sb.toString();

	}

	private Map<String, Differ> custDiffers = new HashMap<String, Differ>();

	@Console(help = "only match based on pattern at the supplied paths when diffing.  See NumberPatternDiffer for details", params = { "fieldPatterns" })
	synchronized public void addNumberPatternDiffFields(String... patterns) {
		for (String p : patterns)
			this.custDiffers.put(p, new NumberedPatternDiffer());
	}

	@Console(help = "ignore mismatches at the supplied paths when diffing", params = { "fieldPatterns" })
	synchronized public void addIgnoreDiffFields(String... patterns) {
		for (String p : patterns)
			this.custDiffers.put(p, new IgnoreDiffer());
	}

	@Console(help = "clear out the custom differs that match the supplied patterns", params = { "fieldPatterns" })
	synchronized public boolean removeDiffFields(String... patterns) {
		boolean r = false;
		for (String pattern : patterns)
			if (this.custDiffers.remove(pattern) != null)
				r = true;
		return r;
	}

	@Console(help = "clear out all custom field differs. This includes Number-pattern differs and ignore differs")
	synchronized public void resetNumberPatternDiffFields() {
		this.custDiffers.clear();
	}

	@Console(help = "Diff the template located at the supplied path against the supplied object", params = { "pathOfTemplate", "objectToCompare" })
	synchronized public DiffResult diff(String mPath, Object o) {
		List<Map> messageList = session.getMaps(mPath);
		if (messageList == null)
			return new DiffResult(DifferConstants.MISSING, o, DifferConstants.LEFT_IS_NULL).setKey(mPath);
		final RootDiffer differ = new RootDiffer();
		DiffSession ds = new DiffSession(differ);
		for (Map.Entry<String, Differ> e : custDiffers.entrySet())
			ds.addDiffOverride(TextMatcherFactory.DEFAULT.toMatcher(e.getKey()), e.getValue());
		final DiffResult diff = differ.diff(mPath, messageList.get(0), o, ds);
		if (diff != null)
			diff.setKey(mPath);
		return diff;
	}

	public TemplateSession getTemplateSession() {
		return session;
	}

}
