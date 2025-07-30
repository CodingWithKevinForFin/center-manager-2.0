package com.f1.ami.relay;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.f1.ami.amicommon.msg.AmiRelayMessage;
import com.f1.ami.amicommon.msg.AmiRelayObjectDeleteMessage;
import com.f1.ami.amicommon.msg.AmiRelayObjectMessage;
import com.f1.ami.relay.fh.AmiRelayBytesToMapConverter;
import com.f1.base.Pointer;
import com.f1.container.OutputPort;
import com.f1.utils.BasicPointer;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.mutable.Mutable;

//This represents all entries of the relay.transforms file
public class AmiRelayTransforms {

	private static final Logger log = LH.get();
	private AmiRelayTransform[] transforms;
	final private Pointer<String> ibuf = new BasicPointer<String>();
	final private Mutable.Long ebuf = new Mutable.Long();
	final private String fileName;

	private boolean debug;
	private long statsInCount;
	private long statsOutCount;
	private long statsDropCount;
	private long statsTotalNanos;
	final private long startTime;
	final private HashMap<String, AmiRelayTransform> transformsByName;
	final private Map<String, AmiRelayDictionary> dictionaries;
	final private boolean canMultiplex;

	public AmiRelayTransforms(String fileName, Map<String, AmiRelayDictionary> dictionaries, AmiRelayTransform[] transforms, boolean debug) {
		this.transforms = transforms;
		this.debug = debug;
		this.startTime = System.currentTimeMillis();
		this.fileName = fileName;
		this.dictionaries = dictionaries;
		this.transformsByName = new HashMap<String, AmiRelayTransform>();
		if (this.transforms != null)
			for (AmiRelayTransform amiRelayTransform : this.transforms)
				this.transformsByName.put(amiRelayTransform.getName(), amiRelayTransform);
		boolean canMultiplex = false;
		if (this.transforms != null)
			for (AmiRelayTransform i : this.transforms)
				if (i.isContinueOnMatch())
					canMultiplex = true;
		this.canMultiplex = canMultiplex;

	}

	public boolean hasTransforms() {
		return transforms != null;
	}

	public void map(AmiRelayMessage action, OutputPort<AmiRelayMessage> out) {
		statsInCount++;
		long n = statsOutCount;
		long start = System.nanoTime();
		if (transforms == null)
			send(action, out);
		else if (action instanceof AmiRelayObjectMessage)
			transform(this.transforms, (AmiRelayObjectMessage) action, out);
		else if (action instanceof AmiRelayObjectDeleteMessage)
			transform(this.transforms, (AmiRelayObjectDeleteMessage) action, out);
		else
			send(action, out);
		long end = System.nanoTime();
		statsTotalNanos += end - start;
		if (n == statsOutCount)
			statsDropCount++;

	}

	private void send(AmiRelayMessage action, OutputPort<AmiRelayMessage> out) {
		out.send(action, null);
		statsOutCount++;
	}

	public static String debug(AmiRelayMessage action) {
		Map<String, Object> values = AmiRelayBytesToMapConverter.read(action.getParams());
		StringBuilder sb = new StringBuilder();
		if (action instanceof AmiRelayObjectMessage) {
			AmiRelayObjectMessage om = (AmiRelayObjectMessage) action;
			String id = om.getId();
			String type = om.getType();
			long expires = om.getExpires();
			if (id != null)
				values.put("I", id);
			if (type != null)
				values.put("T", type);
			if (expires != 0L)
				values.put("E", expires);
		}
		for (Entry<String, Object> e : values.entrySet()) {
			if (sb.length() > 0)
				sb.append('|');
			sb.append(e.getKey()).append('=');
			Object v = e.getValue();
			if (v instanceof CharSequence)
				SH.quote('"', (CharSequence) v, sb);
			else {
				sb.append(v);
				if (v instanceof Number) {
					if (v instanceof Double)
						sb.append('D');
					else if (v instanceof Long)
						sb.append('L');
					else if (v instanceof Float)
						sb.append('F');
				}
			}
		}
		return sb.toString();
	}

	public void transform(AmiRelayTransform[] transforms, AmiRelayObjectMessage action, OutputPort<AmiRelayMessage> out) {
		if (debug)
			LH.info(log, "START_TRANSFORM: ", debug(action));
		String name = action.getType();
		int sent = 0;
		boolean first = true;
		for (int i = 0; i < transforms.length; i++) {
			AmiRelayTransform transform = transforms[i];
			String[] t = transform.getMapTableToTargetTables(name);
			long start = System.nanoTime();
			if (t.length > 0) {
				action.setType(t[0]);
				byte[] paramsIn = action.getParams();
				ebuf.value = action.getExpires();
				ibuf.put(action.getId());
				byte[] paramsOut = transform.convertParams(paramsIn, ibuf, action.getType(), ebuf);
				if (paramsOut != null) {
					for (int j = 0; j < t.length; j++) {
						AmiRelayObjectMessage action2;
						if (canMultiplex || t.length > 1)
							action2 = (AmiRelayObjectMessage) action.clone();
						else
							action2 = action;
						action2.setType(t[j]);
						action2.setParams(paramsOut.clone());
						action2.setId(ibuf.get());
						action2.setExpires(ebuf.value);
						action2.setTransformState(first ? AmiRelayMessage.TRANSFORM_FIRST : AmiRelayMessage.TRANSFORM_DUP);
						if (debug)
							LH.info(log, SH.rightAlign(' ', transform.getName(), 13, false), ": SENDING ", debug(action2));
						send(action2, out);
					}
					sent += t.length;
					transform.incrementStats(System.nanoTime() - start, t.length);
					if (transform.isContinueOnMatch())
						continue;
					else
						break;
				} else {
					transform.incrementStats(System.nanoTime() - start, 0);
					if (debug)
						LH.info(log, SH.rightAlign(' ', transform.getName(), 13, false), ": MISMATCH");
				}
			} else {
				transform.incrementStats(System.nanoTime() - start, 0);
				if (debug)
					LH.info(log, SH.rightAlign(' ', transform.getName(), 13, false), ": MISMATCH");
			}
			if (!transform.isContinueOnNotMatch())
				break;
		}
		if (debug) {
			if (sent == 0)
				LH.info(log, "END_TRANSFORM: MESSAGE_DROPPED");
			else
				LH.info(log, "END_TRANSFORM: SENT " + sent + " MESSAGE(S)");
		}
	}

	public void transform(AmiRelayTransform[] transforms, AmiRelayObjectDeleteMessage action, OutputPort<AmiRelayMessage> out) {
		if (debug)
			LH.info(log, SH.centerAlign('=', " START ", 13, false), " ==> ", debug(action));
		String name = action.getType();
		boolean first = true;
		for (int i = 0; i < transforms.length; i++) {
			AmiRelayTransform transform = transforms[i];
			String[] t = transform.getMapTableToTargetTables(name);
			if (t.length > 0) {
				long start = System.nanoTime();
				action.setType(t[0]);
				byte[] paramsIn = action.getParams();
				ibuf.put(action.getId());
				byte[] paramsOut = transform.convertParams(paramsIn, ibuf, action.getType(), ebuf);
				if (paramsOut != null) {
					for (int j = 0; j < t.length; j++) {
						AmiRelayObjectDeleteMessage action2 = (AmiRelayObjectDeleteMessage) action.clone();
						action2.setType(t[j]);
						action2.setParams(paramsOut.clone());
						action2.setId(ibuf.get());
						action2.setTransformState(first ? AmiRelayMessage.TRANSFORM_FIRST : AmiRelayMessage.TRANSFORM_DUP);
						send(action2, out);
						if (debug)
							LH.info(log, SH.rightAlign(' ', transform.getName(), 13, false), " <== ", debug(action2));
					}
					transform.incrementStats(System.nanoTime() - start, t.length);
					if (transform.isContinueOnMatch())
						continue;
					else
						break;
				} else if (debug)
					LH.info(log, SH.rightAlign(' ', transform.getName(), 13, false), " XXX EXPRESSION_WAS_FALSE");

			}
			if (!transform.isContinueOnNotMatch())
				break;
		}
		if (debug)
			LH.info(log, SH.centerAlign('=', " DONE ", 13, false));
	}

	public AmiRelayTransform[] getTransforms() {
		return this.transforms;
	}

	public long getStatsInCount() {
		return statsInCount;
	}
	public long getStatsOutCount() {
		return statsOutCount;
	}
	public long getStatsDropCount() {
		return statsDropCount;
	}
	public long getStatesTotalNanos() {
		return statsTotalNanos;
	}
	public long getStartTime() {
		return startTime;
	}
	public String getTransformFileName() {
		return fileName;
	}
	public Map<String, AmiRelayDictionary> getDictionaries() {
		return dictionaries;
	}

	public boolean getDebugMode() {
		return this.debug;
	};

	public void setDebugMode(boolean mode) {
		this.debug = mode;
	}

	public AmiRelayTransform getTransform(String name) {
		return this.transformsByName.get(name);
	}

	public AmiRelayDictionary getDictionary(String name) {
		return null;
	}

	public void resetStats() {
		this.statsInCount = 0L;
		this.statsOutCount = 0L;
		this.statsDropCount = 0L;
		this.statsTotalNanos = 0L;
		for (AmiRelayTransform i : this.transforms) {
			i.resetStats();
		}
	}

	public static void logSkippingTransform(AmiRelayMessage m) {
		LH.info(log, "NO TRANSFORMS SO FORWARDING ALL MESSAGES");
	}

}
