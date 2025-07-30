package com.f1.ami.relay.fh;

import com.f1.ami.relay.AmiRelayIn;
import com.f1.ami.relay.AmiRelayOut;
import com.f1.utils.PropertyController;

//Represents a single instance of an AMI Relay Feedhandler
public interface AmiFH extends AmiRelayOut {

	public static int STATUS_STARTED = 1;
	public static int STATUS_STOPPED = 2;
	public static int STATUS_FAILED = 3;
	public static int STATUS_STARTING = 4;
	public static int STATUS_STOPPING = 5;
	public static int STATUS_START_FAILED = 6;
	public static int STATUS_STOP_FAILED = 7;

	public static final String PCE_STATUS_CHANGED = "STATUS_CHANGED";

	//called during AMI startup
	//  id - unique id per relay/runtime
	//  name - name of the relay, ex: my_feedhandler_name
	//  sysProps - all properties inside ami relay
	//  props - properties specified to this feedhandler
	//  endpoint - the endpoint for sending messages into AMI, You should hold on to this and call methods on it as messages stream in, etc.
	public void init(int id, String name, PropertyController sysProps, PropertyController props, AmiRelayIn endpoint);

	//called when this feedhandler is started/stopped.  Typically, start is called immediately after all  successful init has been called
	public void start();
	public void stop();

	//Return the status of this feedhandler (see constants) the string version is for convenience for the users to see why the current status is what it is.
	public int getStatus();
	public String getStatusReason();

	//the AppId (aka loginId) associated with this connection. See Reserved Columns, column P for details)
	public String getAppId();

	//time and place and description, of the connection, not required but convenient for end users diagnosing connections
	public long getConnectionTime();
	public int getRemotePort(); //ex: 1234
	public String getRemoteIp(); //ex:  myhost
	public String getDescription();//ex: someprotocol://myhost:1234

	//when durability is enabled this will be called back when the message has been succesfully persisted (see ami.relay.guaranteed.messaging.enabled)
	public boolean onAck(long seqnum);//return false if the ack failed for some reason

	public void onCenterConnected(String centerId);
}
