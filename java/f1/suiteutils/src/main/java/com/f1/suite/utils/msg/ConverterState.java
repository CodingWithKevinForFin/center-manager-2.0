/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.utils.msg;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.base.IdeableGenerator;
import com.f1.base.Message;
import com.f1.container.RequestMessage;
import com.f1.container.impl.BasicState;
import com.f1.msg.MsgBytesEvent;
import com.f1.msg.MsgOutputTopic;
import com.f1.utils.FastByteArrayDataInputStream;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.OfflineConverter;
import com.f1.utils.converter.bytes.BasicFromByteArrayConverterSession;
import com.f1.utils.converter.bytes.BasicToByteArrayConverterSession;
import com.f1.utils.converter.bytes.FromByteArrayConverterSession;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;

public class ConverterState extends BasicState {
	private static final Logger log = Logger.getLogger(ConverterState.class.getName());

	private static final byte[] EIGHT_BYTES = new byte[8];
	private final FromByteArrayConverterSession fromByteArrayConverterSession;
	ToByteArrayConverterSession toByteArrayConverterSession;
	private final ObjectToByteArrayConverter converter;
	private final FastByteArrayDataInputStream fromStream;
	FastByteArrayDataOutputStream toStream;

	public ConverterState(IdeableGenerator objectFactory, OfflineConverter offlineConverter) {
		converter = (ObjectToByteArrayConverter) ((ObjectToByteArrayConverter) offlineConverter).clone();
		converter.setIdeableGenerator(objectFactory);
		fromStream = new FastByteArrayDataInputStream(OH.EMPTY_BYTE_ARRAY);
		fromByteArrayConverterSession = new BasicFromByteArrayConverterSession(converter, fromStream);
		toStream = new FastByteArrayDataOutputStream();
		toByteArrayConverterSession = new BasicToByteArrayConverterSession(converter, toStream, true);
	}

	public FromByteArrayConverterSession getFromByteArrayConverterSession() {
		return fromByteArrayConverterSession;
	}

	public ToByteArrayConverterSession getToByteArrayConverterSession() {
		return toByteArrayConverterSession;
	}

	public Object read(byte[] buf) throws IOException {
		fromStream.reset(buf);
		boolean supportCircRefs = fromStream.readBoolean();
		fromByteArrayConverterSession.resetCircRefs(supportCircRefs);
		return converter.read(fromByteArrayConverterSession);
	}

	public byte[] write(Object o, boolean supportCircRefs) throws IOException {
		toStream.reset(10000);
		toByteArrayConverterSession.resetCircRefs(supportCircRefs);
		toStream.writeBoolean(supportCircRefs);
		converter.write(o, toByteArrayConverterSession);
		byte[] r = toStream.toByteArray();
		return r;

	}

	private int nextCeilingForWarning = 1000;

	final private Map<Long, MessageWrapper> guidsToWrappedMessages = new HashMap<Long, MessageWrapper>();
	final private Map<Long, RequestMessage> guids2requests = new HashMap<Long, RequestMessage>();
	private long nextId = 0;

	public Long addRequest(RequestMessage o) {
		Long r = nextId++;
		guids2requests.put(r, o);
		if (guids2requests.size() >= nextCeilingForWarning) {
			nextCeilingForWarning *= 2;
			LH.warning(log, "There are ", guids2requests.size(), " outstanding requests w/o responses.  This could result in a memory leak. Will warn again at threshold: ",
					nextCeilingForWarning);
		}
		return r;
	}
	public void monitorRequest(Long correlationId, MsgOutputTopic topic, MsgBytesEvent event, Message outputMessage) {
		guidsToWrappedMessages.put(correlationId, new MessageWrapper(getPartition().getContainer().getTools().getNow(), topic, event, outputMessage));
	}

	public RequestMessage getRequest(Object i, boolean remove) {
		RequestMessage r = remove ? guids2requests.remove(i) : guids2requests.get(i);
		if (r != null && remove && !guidsToWrappedMessages.isEmpty())
			guidsToWrappedMessages.remove(i);
		return r;
	}

	private boolean isAllConnected = true;

	public boolean getIsAllConnected() {
		return isAllConnected;
	}

	public void setIsAllConnected(boolean isAllConnected) {
		this.isAllConnected = isAllConnected;
	}

	public Map<Long, MessageWrapper> getPendingRequests() {
		return this.guidsToWrappedMessages;
	}

	public static class MessageWrapper {
		final MsgOutputTopic channel;
		final MsgBytesEvent e;
		long now;
		final Message message;

		public MessageWrapper(long now, MsgOutputTopic channel, MsgBytesEvent e, Message message) {
			this.now = now;
			this.channel = channel;
			this.e = e;
			this.message = message;
		}
		public long getSendTime() {
			return now;
		}
		public MsgOutputTopic getChannel() {
			return channel;
		}
		public MsgBytesEvent getE() {
			return e;
		}
		public Message getMessage() {
			return message;
		}

	}

}
