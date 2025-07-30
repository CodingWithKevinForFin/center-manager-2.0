package com.f1.msg.impl;

import java.util.HashSet;
import java.util.List;

import com.f1.msg.MsgConnection;
import com.f1.msg.MsgConnectionConfiguration;
import com.f1.msg.MsgConnectionExternalInterfaces;
import com.f1.msg.MsgEventListener;
import com.f1.msg.MsgExternalConnection;
import com.f1.msg.MsgInputTopic;
import com.f1.msg.MsgOutputTopic;
import com.f1.utils.CH;
import com.f1.utils.SH;

public class MsgConsole {

	private List<MsgConnection> connections;

	public MsgConsole(MsgConnection... connections) {
		this.connections = CH.l(connections);
	}

	public void addConnection(MsgConnection... connection) {
		CH.l(connections, connections);
	}

	public String show() {
		StringBuilder sb = new StringBuilder();
		for (MsgConnection connection : connections) {
			sb.append("## CONNECTION: " + connection.getClass().getName()).append(SH.NEWLINE);
			sb.append("  is connected / running: " + connection.isRunning()).append(SH.NEWLINE);
			MsgConnectionConfiguration config = connection.getConfiguration();
			if (config == null) {
				sb.append("  ").append("<configuration is null>").append(SH.NEWLINE);
			} else {
				sb.append("  name: ").append(config.getName()).append(SH.NEWLINE);
			}
			for (MsgConnectionExternalInterfaces i : connection.getExternalInterfaces())
				sb.append("  @interface: " + i).append(SH.NEWLINE);
			HashSet<String> c = new HashSet<String>(connection.getInputTopicNames());
			c.addAll(connection.getOutputTopicNames());
			if (!c.isEmpty())
				sb.append(SH.NEWLINE);
			for (String cn : c) {
				String topicName = SH.beforeFirst(cn, '$', cn);
				String suffix = SH.afterFirst(cn, '$', null);
				boolean isInput = connection.getInputTopicNames().contains(cn);
				boolean isOutput = connection.getOutputTopicNames().contains(cn);
				sb.append("  @Topic: ").append(cn).append(SH.NEWLINE);
				if (isOutput) {
					MsgOutputTopic output = connection.getOutputTopic(topicName, suffix);
					long messages = output.getSentMessagesCount();
					sb.append("  Sent: ").append(messages).append(" message(s)").append(SH.NEWLINE);
					for (MsgExternalConnection ex : output.getExternalConnections()) {
						sb.append("    ==> External Connection: " + ex).append(SH.NEWLINE);
					}
				}
				if (isInput) {
					MsgInputTopic input = connection.getInputTopic(topicName, suffix);
					long messages = input.getReceivedMessagesCount();
					sb.append("  Received: ").append(messages).append(" message(s)").append(SH.NEWLINE);
					for (MsgExternalConnection ex : input.getExternalConnections()) {
						sb.append("    <== External Connection: " + ex).append(SH.NEWLINE);
					}
					for (MsgEventListener l : input.getListeners()) {
						sb.append("    ==> Internal Listener: ").append(l).append(SH.NEWLINE);
					}
				}
				sb.append(SH.NEWLINE);
			}
			sb.append(SH.NEWLINE);
		}
		return sb.toString();
	}
}
