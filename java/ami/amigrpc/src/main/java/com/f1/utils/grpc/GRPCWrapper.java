package com.f1.utils.grpc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.f1.utils.SH;
import com.f1.utils.grpc.ProtobufUtils.ProtobufObjectTemplate;
import com.f1.utils.grpc.ProtobufUtils.ProtobufStub;
import com.f1.utils.grpc.ProtobufUtils.StubType;

import io.grpc.ConnectivityState;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;

public class GRPCWrapper {
	private ManagedChannel channel = null;
	private String stubClass;
	private String url;
	private ProtobufStub stub;
	private String classPrefix = "";
	private String command;
	private String methodName;
	private Object[] methodParams;

	private HashMap<String, ProtobufObjectTemplate> templates = new HashMap<String, ProtobufObjectTemplate>();

	public GRPCWrapper() {
	}

	public Map<String, ProtobufObjectTemplate> getTemplates() {
		return this.templates;
	}

	public ProtobufObjectTemplate getTemplate(final Object o) {
		return ProtobufUtils.getOrSetTemplate(o, this.templates);
	}

	public ProtobufStub getStub() {
		return stub;
	}

	public Object invokeAsyncCompiledMethod(final StreamObserver<?> listener) {
		if (this.channel != null && this.channel.isShutdown())
			startConnection();

		//Handle async method calls
		if (this.stub.getStubType().equals(StubType.NORMAL)) {
			final Object[] params = Arrays.copyOf(this.methodParams, this.methodParams.length + 1);
			params[this.methodParams.length] = listener;
			return this.stub.invokeMethod(this.methodName, params);
		} else {
			throw new UnsupportedOperationException("Async calls not supported for stubs of this type");
		}
	}

	public Object invokeCompiledMethod() {
		if (this.stub == null || SH.isnt(this.methodName))
			return null;

		return this.stub.invokeMethod(this.methodName, this.methodParams);
	}

	public void shutdownChannel() {
		if (channel != null && !this.channel.isShutdown())
			channel.shutdown();
	}

	public String getMethodName() {
		return this.methodName;
	}

	public Object[] getMethodParams() {
		return this.methodParams;
	}

	public byte compileCommand(final String command) {
		this.command = command;

		ProtobufParser parser = new ProtobufParser(this.command, this.classPrefix);
		final byte firstInstruction = (byte) parser.pop();

		//Handle describe command
		if (firstInstruction == ProtobufParser.INSTRUCTION_DESCRIBE_OBJECT) {
			this.methodName = "describe";
			this.methodParams = new Object[] { parser.pop() };
			return ProtobufParser.INSTRUCTION_DESCRIBE_OBJECT;
		}

		if (firstInstruction != ProtobufParser.INSTRUCTION_CALL_METHOD)
			throw new RuntimeException("Failed to initialize GRPC FH: Could not create method");
		this.methodName = (String) parser.pop();
		if (SH.isnt(this.methodName))
			throw new RuntimeException("Could not get method name");
		ArrayList<Object> vars = new ArrayList<Object>();
		for (Object o = parser.pop(); o != null; o = parser.pop()) {
			if (o instanceof Byte) {
				byte b = (byte) o;
				if (b == ProtobufParser.INSTRUCTION_CALL_METHOD)
					throw new RuntimeException("Failed to initialize GRPC FH: Parser in bad state");
				else if (b == ProtobufParser.INSTRUCTION_CONSTRUCT_OBJECT)
					vars.add(ProtobufParser.compileObject(parser, templates));
				else if (b == ProtobufParser.INSTRUCTION_CALL_METHOD_END)
					continue;
				else
					throw new UnsupportedOperationException("Unhandled instruction: " + b);
			} else {
				vars.add(o);
			}
		}

		this.methodParams = vars.toArray(new Object[vars.size()]);
		return ProtobufParser.INSTRUCTION_CALL_METHOD;
	}

	private void startConnection() {
		//TODO: Add more credentials support
		this.channel = Grpc.newChannelBuilder(this.url, InsecureChannelCredentials.create()).build();
	}

	public void init(final String url, final String stubClass, final String metadata, final ProtobufUtils.StubType stubType, final String classPrefix) {
		this.url = url;
		if (SH.isnt(this.url))
			throw new RuntimeException("Failed to initialize GRPC FH: A valid GRPC URL is required");
		this.url = SH.replaceAll(this.url, '\\', '/');

		startConnection();
		ConnectivityState state = this.channel.getState(false);
		if (!(state.equals(ConnectivityState.IDLE) || state.equals(ConnectivityState.READY)))
			throw new RuntimeException("Failed to connect to url: " + this.url);

		this.stubClass = stubClass;

		if (SH.is(metadata)) {
			final Map<String, String> m = SH.splitToMap(',', ':', '\\', metadata);
			stub = new ProtobufStub(this.stubClass, this.channel, stubType, m);
			if (stub == null)
				throw new RuntimeException("Failed to initialize GRPC FH: Failed to initialize stub with metadata: " + metadata);
		} else {
			stub = new ProtobufStub(this.stubClass, this.channel, stubType);
		}

		if (stub == null)
			throw new RuntimeException("Failed to initialize GRPC FH: Failed to initialize stub with class: " + this.stubClass);

		this.classPrefix = classPrefix;
	}
}
