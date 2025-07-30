package com.f1.suite.utils.secure;

import com.f1.base.Message;
import com.f1.container.InputPort;
import com.f1.container.OutputPort;
import com.f1.container.RequestInputPort;
import com.f1.container.RequestMessage;
import com.f1.container.RequestOutputPort;
import com.f1.container.ResultMessage;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.container.impl.BasicSuite;
import com.f1.povo.standard.SecureMessage;

public class SecureMessageSuite extends BasicSuite {

	final private DecryptRequestMessageProcessor decryptRequestProcessor;
	final private EncryptResultMessageProcessor encryptResultProcessor;
	final private EncryptRequestMessageProcessor encryptRequestProcessor;
	final private DecryptResultMessageProcessor decryptResultProcessor;

	public final InputPort<RequestMessage<SecureMessage<?>>> inboundSecurePort;
	public final RequestInputPort<Message, Message> inboundUnsecurePort;

	public final RequestOutputPort<SecureMessage, ?> outboundSecurePort;
	public final RequestOutputPort<?, ?> outboundUnsecurePort;

	public SecureMessageSuite(Object partition, SecureMessageCrypter crypter) {
		this.decryptRequestProcessor = new DecryptRequestMessageProcessor(crypter);
		this.encryptRequestProcessor = new EncryptRequestMessageProcessor(crypter);
		this.encryptResultProcessor = new EncryptResultMessageProcessor(crypter);
		this.decryptResultProcessor = new DecryptResultMessageProcessor(crypter);
		addChild(decryptRequestProcessor);
		addChild(decryptResultProcessor);
		addChild(encryptRequestProcessor);
		addChild(encryptResultProcessor);
		this.encryptRequestProcessor.output.setConnectionOptional(true);
		this.decryptRequestProcessor.output.setConnectionOptional(true);
		this.inboundSecurePort = exposeInputPort(this.decryptRequestProcessor);
		this.outboundUnsecurePort = exposeOutputPort(this.decryptRequestProcessor.output);
		this.inboundUnsecurePort = exposeInputPort(this.encryptRequestProcessor);
		this.outboundSecurePort = exposeOutputPort(this.encryptRequestProcessor.output);

		this.decryptRequestProcessor.bindToPartition(partition);
		this.encryptResultProcessor.bindToPartition(partition);
		this.encryptRequestProcessor.bindToPartition(partition);
		this.decryptResultProcessor.bindToPartition(partition);

		wire(this.decryptRequestProcessor.responsePort, this.encryptResultProcessor, true);
		wire(this.encryptRequestProcessor.responsePort, this.decryptResultProcessor, true);
	}

	public static class DecryptRequestMessageProcessor extends BasicRequestProcessor<SecureMessage<?>, State, Message> {

		public final RequestOutputPort output = newRequestOutputPort(Message.class, Message.class);
		private OutputPort responsePort = newOutputPort(Message.class);
		private SecureMessageCrypter crypter;

		public DecryptRequestMessageProcessor(SecureMessageCrypter crypter) {
			super((Class) SecureMessage.class, State.class, Message.class);
			this.crypter = crypter;
		}

		@Override
		public void processAction(RequestMessage<SecureMessage<?>> request, State arg1, ThreadScope threadScope) throws Exception {
			Message payload = crypter.unsecureMessage(request.getAction());
			RequestMessage request2 = nw(RequestMessage.class);
			request2.setCorrelationId(request);
			request2.setAction(payload);
			request2.setResultPort(responsePort);
			output.send(request2, threadScope);
		}

		@Override
		protected Message processRequest(RequestMessage<SecureMessage<?>> arg0, State arg1, ThreadScope arg2) throws Exception {
			throw new UnsupportedOperationException();
		}
	}

	public static class EncryptRequestMessageProcessor extends BasicRequestProcessor<Message, State, Message> {

		private OutputPort responsePort = newOutputPort(Message.class);
		public final RequestOutputPort<SecureMessage, ?> output = newRequestOutputPort(SecureMessage.class, Message.class);

		private SecureMessageCrypter crypter;

		public EncryptRequestMessageProcessor(SecureMessageCrypter crypter) {
			super(Message.class, State.class, Message.class);
			this.crypter = crypter;
		}

		@Override
		public void processAction(RequestMessage<Message> request, State arg1, ThreadScope threadScope) throws Exception {
			SecureMessage<Message> secureMessage = crypter.secureMessage(request.getAction());
			RequestMessage request2 = nw(RequestMessage.class);
			request2.setCorrelationId(request);
			request2.setAction(secureMessage);
			request2.setResultPort(responsePort);
			output.send(request2, threadScope);
		}

		@Override
		protected Message processRequest(RequestMessage<Message> arg0, State arg1, ThreadScope arg2) throws Exception {
			throw new UnsupportedOperationException();
		}
	}

	public static class EncryptResultMessageProcessor extends BasicProcessor<ResultMessage<Message>, State> {

		private SecureMessageCrypter crypter;

		public EncryptResultMessageProcessor(SecureMessageCrypter crypter) {
			super((Class) RequestMessage.class, State.class);
			this.crypter = crypter;
		}

		@Override
		public void processAction(ResultMessage<Message> result, State arg1, ThreadScope threadScope) throws Exception {
			RequestMessage<Message> origRequest = (RequestMessage<Message>) result.getRequestMessage().getCorrelationId();
			SecureMessage<Message> secure = crypter.secureMessage(result.getAction());
			ResultMessage secureResult = nw(ResultMessage.class);
			secureResult.setAction(secure);
			reply(origRequest, secureResult, threadScope);
		}
	}

	public static class DecryptResultMessageProcessor extends BasicProcessor<ResultMessage<SecureMessage<Message>>, State> {

		private SecureMessageCrypter crypter;

		public DecryptResultMessageProcessor(SecureMessageCrypter crypter) {
			super((Class) RequestMessage.class, State.class);
			this.crypter = crypter;
		}

		@Override
		public void processAction(ResultMessage<SecureMessage<Message>> result, State arg1, ThreadScope threadScope) throws Exception {
			RequestMessage<Message> origRequest = (RequestMessage<Message>) result.getRequestMessage().getCorrelationId();
			ResultMessage unsecureResult = nw(ResultMessage.class);
			try {
				Message unsecure = crypter.unsecureMessage(result.getAction());
				unsecureResult.setAction(unsecure);
			} catch (Throwable e) {
				unsecureResult.setError(e);
			}
			reply(origRequest, unsecureResult, threadScope);
		}
	}

	public void init() {
		super.init();
		getServices().getGenerator().register(SecureMessage.class);
	}
}
