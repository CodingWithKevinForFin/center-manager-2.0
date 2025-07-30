package com.f1.qfix;

import java.util.logging.Level;

import quickfix.RuntimeError;

import com.f1.msg.MsgConnection;
import com.f1.msg.MsgConnectionListener;
import com.f1.msg.MsgExternalConnection;
import com.f1.msg.MsgInputTopic;
import com.f1.msg.MsgOutputTopic;
import com.f1.msg.MsgTopic;
import com.f1.qfix.msg.FixMsgConnection;
import com.f1.qfix.msg.FixMsgConnectionConfiguration;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;

public class QfixConnector extends FixMsgToProcessorAdapter implements MsgConnectionListener {

	private PropertyController props;
	private FixMsgConnection connection;

	public QfixConnector(PropertyController props) {
		this.props = props;

	}
	@Override
	public void init() {
		super.init();
		try {
			connection = new FixMsgConnection(new FixMsgConnectionConfiguration("FIX", props));
			connection.addMsgConnectionListener(this);
			getServices().getMsgManager().addConnection(connection);
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
	}

	@Override
	public void startDispatching() {
		this.connection.init();
		super.startDispatching();
	}

	@Override
	public void stop() {
		try {
			connection.stop();
		} catch (RuntimeError e) {
			log.log(Level.WARNING, "Config Error with quick fix connector shutdown", e);
		}
	}
	@Override
	public void onDisconnect(MsgConnection connection, MsgTopic msgTopic, String topic, String suffix, String remoteHost, boolean isWrite, MsgExternalConnection externalConnection) {
	}
	@Override
	public void onConnect(MsgConnection connection, MsgTopic msgTopic, String topic, String suffix, String remoteHost, boolean isWrite, MsgExternalConnection externalConnection) {
	}
	@Override
	public void onNewInputTopic(MsgConnection connection, MsgInputTopic r) {
		r.subscribe(this);
	}
	@Override
	public void onNewOutputTopic(MsgConnection connection, MsgOutputTopic r) {
	}

}
