package com.sjls.f1.start.ofr.controlpanel;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.google.protobuf.Message;
import com.sjls.controlpanel.protobuf.io.CodecException;
import com.sjls.controlpanel.protobuf.io.ICPInstance;
import com.sjls.controlpanel.protobuf.io.IControPanelMsgListener;
import com.sjls.controlpanel.protobuf.io.IMsgCodec;
import com.sjls.controlpanel.protobuf.io.MsgChannel;
import com.sjls.controlpanel.protobuf.messages.ControlPanelMsgs.CPAlertLevel;
import com.sjls.controlpanel.protobuf.messages.ControlPanelMsgs.CPAlertMsg;
import com.sjls.controlpanel.protobuf.messages.ControlPanelMsgs.SJLSTime;
import com.sjls.controlpanel.protobuf.utils.ProtobufUtils;
import com.sjls.f1.start.ofr.brokeralloc.BrokerAllocMgr;

public class CPInstance extends Thread implements ICPInstance {
	public final static String CVS_ID = "$Id: CPInstance.java,v 1.4 2014/12/04 16:26:11 olu Exp $";
	private static Logger m_logger = Logger.getLogger(CPInstance.class);

	private final String m_instanceName;
	private final IControPanelMsgListener m_msgListener;
	private final String m_msgPrefix;
	private final MsgChannel m_channel;
	private volatile boolean m_hasIOError = false;
	private final IBlockOrdersSnapshotProvider m_snapShotProvider;
	private final IMsgCodec m_codec;
	private final BrokerAllocMgr m_brokerAllocMgr;

	public CPInstance(final String instanceName, final Socket skt, final IControPanelMsgListener msgListener, final IMsgCodec codec, final IBlockOrdersSnapshotProvider provider,
			final BrokerAllocMgr brokerAllocMgr) throws IOException {
		super(instanceName);
		m_instanceName = instanceName;
		m_codec = codec;
		m_channel = new MsgChannel(skt, codec);
		m_msgListener = msgListener;
		m_msgPrefix = m_instanceName + ": ";
		m_snapShotProvider = provider;
		m_brokerAllocMgr = brokerAllocMgr;
	}

	@Override
	public boolean hasIOError() {
		return m_hasIOError;
	}

	public void run() {
		m_logger.info("ControlPanel at  [" + m_instanceName + "] STARTED!!!");
		try {
			if (isHandleLoginSuccessful()) {
				final List<Message> list = m_snapShotProvider.getSnapShot();
				if (list == null || list.isEmpty()) {
					m_logger.info(String.format("ControlPanel [%s]:  OFR has no data...No SNAPSHOT Available!", m_instanceName));
				} else {
					for (final Message msg : list) {
						write(msg);
					}
				}
				if (m_brokerAllocMgr == null) {
					m_logger.error(String.format("ControlPanel [%s]:  There is NO BROKER ALLOCATION INFORMATION!!", m_instanceName));
				} else {
					write(m_brokerAllocMgr.getBrokerAllocationMsg());
				}
				doRun();
			}
		} catch (SocketException e) {
			m_logger.warn(m_msgPrefix + getMessage(e));
			m_hasIOError = true;
		} catch (SocketTimeoutException e) {
			m_logger.warn(m_msgPrefix + getMessage(e));
			m_hasIOError = true;
		} catch (Exception e) {
			m_logger.error(m_msgPrefix + getMessage(e), e);
			m_hasIOError = true;
		} finally {
			m_logger.info("ControlPanel at  [" + m_instanceName + "] EXITTING!!!");
			m_channel.close();
		}
	}

	/**
	 * Handle login from the client. Return true if successful and false if client does not have permission
	 * 
	 * @throws IOException
	 * @throws CodecException
	 */
	private boolean isHandleLoginSuccessful() throws CodecException, IOException {
		m_logger.info(m_msgPrefix + "**** Waiting for ControlPanel to log in...");
		boolean loginStatus = false;
		final Message msg = m_channel.getNextMsg();
		if (msg == null) {
			m_logger.info(m_msgPrefix + "Detected EOF from CP");
		}
		//        else if (msg instanceof CPLoginMsg) {
		//            final CPLoginMsg loginMsg = (CPLoginMsg)msg;
		//            final List<String> userList = StartOFRMain.getStartUsers();
		//            if(userList==null) {
		//                m_logger.info(m_msgPrefix + 
		//                        String.format("No START users list...user [%s] from host [%s] enabled to access START", 
		//                                loginMsg.getUserId(), loginMsg.getHostname()));
		//                loginStatus = true;
		//            }
		//            else {
		//                for(String user : userList) {
		//                    if(user.equalsIgnoreCase(loginMsg.getUserId())) {
		//                        loginStatus = true;
		//                        break;
		//                    }
		//                }
		//            }
		//            //
		//            if(loginStatus==false) {
		//                alertCPWithError(String.format("Access DENIED! [%s:%s] You are not permissioned to use START. Please contact your technical support",
		//                        loginMsg.getHostname(), loginMsg.getUserId()));
		//            }
		//            m_logger.info(m_msgPrefix + String.format("User [%s:%s]: LoginStatus=%s", loginMsg.getHostname(), loginMsg.getUserId(), loginStatus));
		//}
		//        else {
		//            m_logger.debug(m_msgPrefix + String.format("Got Msg [%s]==>[%s] from CP", m_codec.getClassNameFor(msg), msg));
		//            alertCPWithError("Expecting Login message. Your version of ControlPanel is not compatible with START version ["+Version.ENGINE_VERSION+"]");
		//        }
		//        //
		return loginStatus;
	}

	private void alertCPWithError(final String errMsg) {
		final CPAlertMsg.Builder bldr = CPAlertMsg.newBuilder();
		bldr.setText(errMsg);
		bldr.setLevel(CPAlertLevel.FATAL);
		final SJLSTime alertTime = ProtobufUtils.toSJLSTime(new DateTime());
		bldr.setTimeStamp(alertTime);
		write(bldr.build());
	}

	private void doRun() throws CodecException, IOException {
		while (!hasIOError()) {
			final Message msg = m_channel.getNextMsg();
			if (msg == null) {
				m_logger.info(m_msgPrefix + "Detected EOF from CP");
				break;
			}
			//
			if (m_logger.isDebugEnabled()) {
				m_logger.debug(m_msgPrefix + String.format("Got Msg [%s]==>[%s] from CP", m_codec.getClassNameFor(msg), msg));
			}
			m_msgListener.onMessage(msg, this);
		}
	}

	/**
	 * write a msg to the CP Note: This is BLOCKING I/O
	 * 
	 * @param msg
	 */
	@Override
	public void write(final Message msg) {
		if (hasIOError())
			return;
		try {
			m_channel.sendMsg(msg);
			if (m_logger.isDebugEnabled()) {
				m_logger.debug(m_msgPrefix + String.format("Just sent msg [%s] to CP. ==>%s<==", m_codec.getClassNameFor(msg), msg));
			}
		} catch (SocketException e) {
			m_logger.warn(m_msgPrefix + getMessage(e));
			m_hasIOError = true;
		} catch (Exception e) {
			m_logger.error(m_msgPrefix + getMessage(e), e);
			m_hasIOError = true;
		}
	}

	/**
	 * write a msg to the CP Note: This is BLOCKING I/O
	 * 
	 * @param msg
	 */
	public void write(final byte[] msg) {
		if (hasIOError())
			return;
		try {
			m_channel.sendMsg(msg);
		} catch (SocketException e) {
			m_logger.warn(m_msgPrefix + getMessage(e));
			m_hasIOError = true;
		} catch (Exception e) {
			m_logger.error(m_msgPrefix + getMessage(e), e);
			m_hasIOError = true;
		}
	}

	private static String getMessage(final Exception e) {
		return e == null ? "NullPointer" : e.getMessage();
	}

}
