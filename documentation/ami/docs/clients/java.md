# Java

## Setup

### Overview

The AMI Client Listener is used to process messages and commands sent and received by the AMI Client.

The AMI Client connects to the AMI Realtime Backend API. Below is a simple example that sends a message and a command via the AMI Client and processes the command callback.

The AMI Client is **NOT** thread-safe.

### Configuration

The hostname is the host where either AmiCenter or AmiRelay is running.

The port is configured via the property "ami.port" which typically is set to 3289.

### Java interface (see javadoc for details)

```java
com.f1.ami.client.AmiClient
com.f1.ami.client.AmiClientListener
com.f1.ami.client.AmiClientCommandDef
```

### Example - Java Code

``` java
package com.demo.runmaintest;

import java.util.Map;

import com.f1.ami.client.AmiClient;
import com.f1.ami.client.AmiClientCommandDef;
import com.f1.ami.client.AmiClientListener;
import com.f1.utils.OH;
import com.f1.utils.concurrent.HasherMap;

public class SampleClient implements AmiClientListener {
    public static final byte OPTION_AUTO_PROCESS_INCOMING = 2;

    public static void main(String a[]) throws Exception {
        AmiClient client = new AmiClient();
        client.addListener(new SampleClient(client));
        client.start("localhost", 3289, "demo", OPTION_AUTO_PROCESS_INCOMING);
        while (true)
            OH.sleep(1000); // Keep process alive
    }

    private AmiClient amiClient;

    public SampleClient(AmiClient client) {
        this.amiClient = client;
    }
    @Override
    public void onMessageReceived(AmiClient source, long now, int seqnum, int status, CharSequence message) {
        System.out.println("Message received: " + message);
    }
    @Override
    public void onMessageSent(AmiClient source, CharSequence message) {
        System.out.println("Message sent: " + message);
    }
    @Override
    public void onConnect(AmiClient source) {
        System.out.println("Connected");
    }
    @Override
    public void onDisconnect(AmiClient source) {
        System.out.println("Disconnected");
    }
    @Override
    public void onLoggedIn(AmiClient amiClient) {
        // We've successfully connected an logged in, let's register stuff.
        System.out.println("Logged in");
        // Send message
        this.amiClient.startObjectMessage("SampleOrders", "1");
        // Send as String
        // addMessageParamString(String key, String value)
        this.amiClient.addMessageParamString("Order", "Order");
        // Send as int
        // addMessageParamInt(String key, int value)
        this.amiClient.addMessageParamInt("Quantity", 1000);
        // Send as double
        // addMessageParamDouble(String key, double value)
        this.amiClient.addMessageParamDouble("Price", 2703.1995);
        // Send as long
        // addMessageParamLong(String key, long value)
        this.amiClient.addMessageParamLong("Price2", (long) 2703.1995);
        // Send as float
        // addMessageParamFloat(String key, float value)
        this.amiClient.addMessageParamFloat("Volume", (float) 0.45466549498);
        // Send as boolean
        // addMessageParamBoolean(String key, boolean value)
        this.amiClient.addMessageParamBoolean("GTC", false);
        // Send as json object
        // addMessageParamJson(String key, Object value)
        Map map = new HasherMap<String, String>();
        this.amiClient.addMessageParamJson("Table", map);
        // Send as binary
        // addMessageParamBinary(String key, byte[] value)
        byte[] binary = "hello world".getBytes();
        this.amiClient.addMessageParamBinary("Val", binary);
        // Send as num
        // addMessageParamBinary(String key, CharSequence value)
        this.amiClient.addMessageParamEnum("Num", "ENUM");
        // Send as object
        // addMessageParamObject(String key, Object value)
        Object obj = new Object();
        this.amiClient.addMessageParamObject("Data", obj);
        this.amiClient.sendMessageAndFlush();

        // Register a  command
        AmiClientCommandDef def = new AmiClientCommandDef("sample_cmd_def");
        def.setConditions(AmiClientCommandDef.CONDITION_USER_CLICK);
        this.amiClient.sendCommandDefinition(def);
        this.amiClient.flush();
        System.out.println("Sent command");
    }
        @Override
    public void onCommand(AmiClient source, String requestId, String cmd, String userName, String type, String id, Map<String, Object> params) {
        // Do business logic triggered by callback
        System.out.println("On command");
        source.startResponseMessage(requestId, 1, "Okay").addMessageParamLong("sample_user_callback", 45).sendMessageAndFlush();
    }
}
```

## Sending Objects

Once the AmiClient is connected to AMI Realtime Backend API, the client can start sending messages.

See the [AMI Realtime Message Object `O`](../reference/ami_realtime_messages.md#object-o) for more details.

### Class AmiClient

`AmiClient startObjectMessage(String type, CharSequence id)`

:	Starts an object (O) message. Param id is optional.

`AmiClient startObjectMessage(String type, CharSequence id, long expiresOn)`

:	Starts an object (O) message. Param id is optional. If the param expiresOn is: set to 0 the object does not expire, a positive value the object expires at an epoch absolute time, a negative value the object expires in an offset time(milliseconds) into the future.

`void addMessageParamObject(String key, Object value)`

`AmiClient addMessageParams(Map<String, Object> params)`

:	See com.f1.ami.client.AmiClient (javadoc for other addMessageParam[types])

`boolean sendMessage`

:	Finalize and send the current message, returns true if successful

`void flush()`

:	Send pending message buffer to AMI, can be called at anytime

`boolean sendMessageAndFlush()`

:	Send pending message to AMI and block until the message is fully read by AMI, returns true if successful

## Commands

### Register Command

Commands can be created and registered to AMI via the AmiClientCommandDef class.

See the [AMI Realtime Message Command Definition `C`](../reference/ami_realtime_messages.md#command-definition-c) for more details.

#### Example - Java Code

``` java
//Creates a new command
AmiClientCommandDef commandDef = new AmiClientCommandDef("COMMAND_ID");
//Sets the name of the command on the frontend
commandDef.setName("Command Name");
//Specifies when to show the command
commandDef.setFilterClause("panel.title==\"PanelName\"");
//Specifies an additional param from the source table to be passed to the onCommand params
commandDef.setFields("id");
//Sends a command (C) declaration via the AMI Client
client.sendCommandDefinition(commandDef); 
```

### Processing Command Callbacks

Command callbacks are processed using the AmiClientListener onCommand() method.

See the [AMI Realtime Message Response to Execute Command `R`](../reference/ami_realtime_messages.md#response-to-execute-command-r) for more details.

#### Example - Java Code

``` java
public class SampleClient implements AmiClientListener {
    //...
    @Override
    public void onCommand(AmiClient source, String requestId, String cmd, String userName, String type, String id, Map<String, Object> params) {
        String origRequestId = requestId;
        int status = 1;
        String message = "Okay";
        //Starts a response (R) message
        source.startResponseMessage(origRequestId, status, message);
        //Get additional params defined by AmiClientCommandDef.setFields
        long id = (long)params.get("id");
        source.addMessageParamLong("id", id);
        source.sendMessageAndFlush();
    }
}
```

## AmiClientAsServer

The steps to set up the interface for the AmiClientAsServer is similar to the AmiClient interface.

### Configuration

To set it up, you will require the following configuration:

```
ami.relay.fh.active=$${ami.relay.fh.active},csocket  
ami.relay.fh.csocket.start=true  
ami.relay.fh.csocket.class=com.f1.ami.relay.fh.AmiClientSocketFH  
ami.relay.fh.csocket.props.amiId=client_socket  
ami.relay.fh.csocket.props.port=1234  
ami.relay.fh.csocket.props.host=localhost
```

### Example - Java Code

``` java
package com.f1.ami.client;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class AmiClientAsServerTest implements AmiClientAsServerFactory, AmiClientListener {
    public static void main(String a[]) throws IOException {
        new AmiClientAsServer(1234, null, null, new AmiClientAsServerTest());
    }

    @Override
    public void onClient(Socket socket, AmiClient client) throws IOException {
        client.addListener(this);
        client.start(socket, "demo", AmiClient.ENABLE_AUTO_PROCESS_INCOMING);
        client.startObjectMessage("ClientAsServer", null);
        client.addMessageParamString("key", "Hello!");
        client.addMessageParamLong("now", System.currentTimeMillis());
        client.addMessageParamDouble("now", System.currentTimeMillis());
        client.sendMessageAndFlush();
    }

    @Override
    public void onMessageReceived(AmiClient rawClient, long now, int seqnum, int status, CharSequence message) {
        System.out.println("On Message Received: " + message);
    }

    @Override
    public void onMessageSent(AmiClient rawClient, CharSequence message) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onConnect(AmiClient rawClient) {
        System.out.println("Connected");
    }

    @Override
    public void onDisconnect(AmiClient rawClient) {
        System.out.println("Disconnected");
    }

    @Override
    public void onCommand(AmiClient rawClient, String requestId, String cmd, String userName, String objectType, String objectId, Map<String, Object> params) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onLoggedIn(AmiClient rawClient) {
        System.out.println("Loggedin");
    }
}
```

## Subscribe to AMI Center from External Applications to Listen for Events

The Center Client is used to create real time subscriptions to one or more tables on a center. Once subscribed, any changes made to the tables will be published to the client.

### Configuration

Both `out.jar` and `autocode.jar` should be included in the project's classpath. These files can be retrieved from the `amione/lib` directory.

Please replace the "localhost" address and 3270 port with your corresponding center's address and the center's port number.

### Example - Java Code

``` java
package com.f1;

import com.f1.ami.amicommon.AmiCenterDefinition;
import com.f1.ami.amicommon.centerclient.AmiCenterClientListener;
import com.f1.ami.amicommon.centerclient.AmiCenterClientObjectMessage;
import com.f1.ami.client.AmiCenterClient;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.utils.CH;

public class CenterClient implements AmiCenterClientListener {

    public CenterClient() {
    }

    public static void main(String[] args) throws Exception {
        new ContainerBootstrap(CenterClient.class, args);
        CenterClient amiClient = new CenterClient();
        AmiCenterClient centerClient = new AmiCenterClient("demo");
        centerClient.connect("subscription1", "localhost", 3270, amiClient);
        //Subscribe to both table A and __TABLE
        centerClient.subscribe("subscription1", CH.s("A", "__TABLE"));
        while (true);
    }

    @Override
    public void onCenterConnect(AmiCenterDefinition center) {
    }

    @Override
    public void onCenterDisconnect(AmiCenterDefinition center) {
    }

    @Override
    public void onCenterMessage(AmiCenterDefinition center, AmiCenterClientObjectMessage m) {
    }

    @Override
    public void onCenterMessageBatchDone(AmiCenterDefinition center) {
    }

}
```